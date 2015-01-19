/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.control;

import business.security.entity.Event;
import business.security.entity.Group;
import business.security.entity.Invite;
import business.security.entity.PredefinedTypology;
import business.security.entity.Users;
import business.security.entity.WeatherCondition;
import exception.DateConsistencyException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author Daniele
 */
public class EventManagerTest {

    EventManager eventManager;

    List<Event> events;
    List<Users> users;
    List<Invite> invites;

    @Before
    public void setUp() {
        eventManager = new EventManager();
        eventManager.em = mock(EntityManager.class);
        eventManager.principal = mock(Principal.class);
        eventManager.mailManager = mock(MailManager.class);
        eventManager.p = mock(JsonPars.class);
        eventManager.userInformationLoader = mock(UserInformationLoader.class);

        events = new ArrayList();
        users = new ArrayList();
        invites = new ArrayList();

        //Creation of the users used in the tests
        for (int i = 0; i < 3; i++) {
            Users newUser = new Users();
            newUser.setEmail("user" + i + "@mail.it");
            newUser.setPassword("t");
            newUser.setBirthday(new Date(92, 1, 15 + i));
            newUser.setName("try" + i);
            newUser.setSurname("try" + i);
            newUser.setGroupName(Group.USERS);
            users.add(newUser);
        }

        //Creation of the events used in the tests
        for (int i = 0; i < 2; i++) {
            Event newEvent = new Event();
            newEvent.setName("Event " + i);
            newEvent.setId(Long.MIN_VALUE+i);
            newEvent.setTimeStart(new Date(116, 2, 17 + i));
            newEvent.setTimeEnd(new Date(116, 2, 18 + i));
            newEvent.setDescription("Description of event" + i);
            newEvent.setLocation("Milano");
            newEvent.setPublicEvent(true);
            newEvent.setOrganizer(users.get(i));
            newEvent.setPredefinedTypology(PredefinedTypology.dinner);
            newEvent.setOutdoor(false);
            events.add(newEvent);
        }

        //Setting of the invites
        Invite invite1 = new Invite();
        invite1.setEvent(events.get(0));
        invite1.setUser(users.get(2));
        invite1.setStatus(Invite.InviteStatus.invited);
        invites.add(invite1);

        Invite invite2 = new Invite();
        invite2.setEvent(events.get(1));
        invite2.setUser(users.get(0));
        invite2.setStatus(Invite.InviteStatus.invited);
        invites.add(invite2);

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of createEvent method, of class EventManager.
     */
    @Test
    public void testCreateEventSimple() throws Exception {
        System.out.println("createEvent");
        when(eventManager.getLoggedUser()).thenReturn(users.get(0));

        //SIMPLE TEST, CREATION OF AN EVENT
        //Setting the new event
        Event newEvent = new Event();
        newEvent.setName("Last event");
        newEvent.setTimeStart(new Date(116, 3, 17));
        newEvent.setTimeEnd(new Date(116, 3, 18));
        newEvent.setDescription("Description of event");
        newEvent.setLocation("Milano");
        newEvent.setPublicEvent(true);
        newEvent.setPredefinedTypology(PredefinedTypology.dinner);
        newEvent.setOutdoor(false);

        eventManager.createEvent(newEvent);
        
        //Capture argument passed to mocked class
        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        
        verify(eventManager.em, times(1)).persist(captor.capture());
        //Verify that the argument is correct
        assertThat(captor.getValue(), is(newEvent));
    }
    
    @Test(expected=DateConsistencyException.class)
    public void testCreateEventExceptionDateStartAfterEnd() throws Exception{
        //START AFTER END
        when(eventManager.getLoggedUser()).thenReturn(users.get(0));
        when(eventManager.userInformationLoader.loadCreatedEvents()).thenReturn(events.subList(0, 1));
        when(eventManager.userInformationLoader.loadAcceptedEvents()).thenReturn(events.subList(1, 2));
        Event newEvent = new Event();
        newEvent.setName("event");
        newEvent.setTimeStart(new Date(116, 3, 19));
        newEvent.setTimeEnd(new Date(116, 3, 18));
        newEvent.setDescription("Description of event");
        newEvent.setLocation("Milano");
        newEvent.setPublicEvent(true);
        newEvent.setPredefinedTypology(PredefinedTypology.dinner);
        newEvent.setOutdoor(false);
        eventManager.createEvent(newEvent);
    }
    
    @Test(expected=DateConsistencyException.class)
    public void testCreateEventExceptionOverlap() throws Exception{
        //OVERLAP WITH AN EVENT
        when(eventManager.getLoggedUser()).thenReturn(users.get(0));
        when(eventManager.userInformationLoader.loadCreatedEvents()).thenReturn(events.subList(0, 1));
        when(eventManager.userInformationLoader.loadAcceptedEvents()).thenReturn(events.subList(1, 2));
        Event newEvent = new Event();
        newEvent.setId((long)100);
        newEvent.setName("Overlapped event");
        newEvent.setTimeStart(new Date(116, 2, 17));
        newEvent.setTimeEnd(new Date(116, 2, 18));
        newEvent.setDescription("Description of event");
        newEvent.setLocation("Milano");
        newEvent.setPublicEvent(true);
        newEvent.setPredefinedTypology(PredefinedTypology.dinner);
        newEvent.setOutdoor(false);
        eventManager.createEvent(newEvent);
    }
    
    @Test
    public void testCreateEventOutdoorEvent() throws Exception{
        //OUTDOOR EVENT
        WeatherCondition forecasted = new WeatherCondition();
        forecasted.setIcon("BB");
        forecasted.setPrecipitation(true);
        forecasted.setTemperature(1);
        forecasted.setWind(0);
        
        when(eventManager.getLoggedUser()).thenReturn(users.get(0));
        when(eventManager.userInformationLoader.loadCreatedEvents()).thenReturn(events.subList(0, 1));
        when(eventManager.userInformationLoader.loadAcceptedEvents()).thenReturn(events.subList(1, 2));
        when(eventManager.p.parsingWeather(anyString(), anyString(), anyObject())).thenReturn(forecasted);
        
        Event newEvent = new Event();
        newEvent.setId((long)100);
        newEvent.setName("Outdoor event");
        newEvent.setTimeStart(new Date(116, 3, 17));
        newEvent.setTimeEnd(new Date(116, 3, 18));
        newEvent.setDescription("Description of event");
        newEvent.setLocation("Milano");
        newEvent.setPublicEvent(true);
        newEvent.setPredefinedTypology(PredefinedTypology.dinner);
        newEvent.setOutdoor(true);
        
        WeatherCondition awc = new WeatherCondition();
        awc.setIcon("AA");
        awc.setPrecipitation(true);
        awc.setTemperature(0);
        awc.setWind(1);
        newEvent.setAcceptedWeatherConditions(awc);
        
        eventManager.createEvent(newEvent);
        
         //Capture argument passed to mocked class
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(eventManager.em, times(3)).persist(captor.capture());
        
        //Verify that the argument passed are correct
        assertThat(captor.getAllValues().get(0), instanceOf(WeatherCondition.class));
        assertThat(captor.getAllValues().get(1), instanceOf(Event.class));
        assertThat(captor.getAllValues().get(2), instanceOf(WeatherCondition.class));
        
        assertThat((WeatherCondition)captor.getAllValues().get(0), is(awc));
        assertThat((Event)captor.getAllValues().get(1), is(newEvent));
        assertThat((WeatherCondition)captor.getAllValues().get(2), is(forecasted));

    }
    
//
//    /**
//     * Test of checkDateConsistency method, of class EventManager.
//     */
//    @Test
//    public void testCheckDateConsistency() throws Exception {
//        System.out.println("checkDateConsistency");
//        Event event = null;
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        EventManager instance = (EventManager)container.getContext().lookup("java:global/classes/EventManager");
//        boolean expResult = false;
//        boolean result = instance.checkDateConsistency(event);
//        assertEquals(expResult, result);
//        container.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getInvitedPeople method, of class EventManager.
//     */
//    @Test
//    public void testGetInvitedPeople() throws Exception {
//        System.out.println("getInvitedPeople");
//        long idEvent = 0L;
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        EventManager instance = (EventManager)container.getContext().lookup("java:global/classes/EventManager");
//        List<Users> expResult = null;
//        List<Users> result = instance.getInvitedPeople(idEvent);
//        assertEquals(expResult, result);
//        container.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addInvitation method, of class EventManager.
//     */
//    @Test
//    public void testAddInvitation() throws Exception {
//        System.out.println("addInvitation");
//        Users user = null;
//        Event event = null;
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        EventManager instance = (EventManager)container.getContext().lookup("java:global/classes/EventManager");
//        instance.addInvitation(user, event);
//        container.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of removeEvent method, of class EventManager.
//     */
//    @Test
//    public void testRemoveEvent() throws Exception {
//        System.out.println("removeEvent");
//        Event event = null;
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        EventManager instance = (EventManager)container.getContext().lookup("java:global/classes/EventManager");
//        instance.removeEvent(event);
//        container.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getEventById method, of class EventManager.
//     */
//    @Test
//    public void testGetEventById() throws Exception {
//        System.out.println("getEventById");
//        long idEvent = 0L;
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        EventManager instance = (EventManager)container.getContext().lookup("java:global/classes/EventManager");
//        Event expResult = null;
//        Event result = instance.getEventById(idEvent);
//        assertEquals(expResult, result);
//        container.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of updateEventInformation method, of class EventManager.
//     */
//    @Test
//    public void testUpdateEventInformation() throws Exception {
//        System.out.println("updateEventInformation");
//        Event event = null;
//        WeatherCondition awc = null;
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        EventManager instance = (EventManager)container.getContext().lookup("java:global/classes/EventManager");
//        instance.updateEventInformation(event, awc);
//        container.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of addParticipantToEvent method, of class EventManager.
//     */
//    @Test
//    public void testAddParticipantToEvent() throws Exception {
//        System.out.println("addParticipantToEvent");
//        Event ev = null;
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        EventManager instance = (EventManager)container.getContext().lookup("java:global/classes/EventManager");
//        instance.addParticipantToEvent(ev);
//        container.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of removeParticipantFromEvent method, of class EventManager.
//     */
//    @Test
//    public void testRemoveParticipantFromEvent() throws Exception {
//        System.out.println("removeParticipantFromEvent");
//        Event ev = null;
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        EventManager instance = (EventManager)container.getContext().lookup("java:global/classes/EventManager");
//        instance.removeParticipantFromEvent(ev);
//        container.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of loadUserCreatedEvents method, of class EventManager.
//     */
//    @Test
//    public void testLoadUserCreatedEvents() throws Exception {
//        System.out.println("loadUserCreatedEvents");
//        Users user = null;
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        EventManager instance = (EventManager)container.getContext().lookup("java:global/classes/EventManager");
//        List<Event> expResult = null;
//        List<Event> result = instance.loadUserCreatedEvents(user);
//        assertEquals(expResult, result);
//        container.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of loadUserAcceptedEvents method, of class EventManager.
//     */
//    @Test
//    public void testLoadUserAcceptedEvents() throws Exception {
//        System.out.println("loadUserAcceptedEvents");
//        Users user = null;
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        EventManager instance = (EventManager)container.getContext().lookup("java:global/classes/EventManager");
//        List<Event> expResult = null;
//        List<Event> result = instance.loadUserAcceptedEvents(user);
//        assertEquals(expResult, result);
//        container.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of loadEvents method, of class EventManager.
//     */
//    @Test
//    public void testLoadEvents() throws Exception {
//        System.out.println("loadEvents");
//        Users u = null;
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        EventManager instance = (EventManager)container.getContext().lookup("java:global/classes/EventManager");
//        List<Event> expResult = null;
//        List<Event> result = instance.loadEvents(u);
//        assertEquals(expResult, result);
//        container.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getAcceptedPeople method, of class EventManager.
//     */
//    @Test
//    public void testGetAcceptedPeople() throws Exception {
//        System.out.println("getAcceptedPeople");
//        Event e = null;
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        EventManager instance = (EventManager)container.getContext().lookup("java:global/classes/EventManager");
//        List<Users> expResult = null;
//        List<Users> result = instance.getAcceptedPeople(e);
//        assertEquals(expResult, result);
//        container.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRefusedPeople method, of class EventManager.
//     */
//    @Test
//    public void testGetRefusedPeople() throws Exception {
//        System.out.println("getRefusedPeople");
//        Event e = null;
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        EventManager instance = (EventManager)container.getContext().lookup("java:global/classes/EventManager");
//        List<Users> expResult = null;
//        List<Users> result = instance.getRefusedPeople(e);
//        assertEquals(expResult, result);
//        container.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getPendentPeople method, of class EventManager.
//     */
//    @Test
//    public void testGetPendentPeople() throws Exception {
//        System.out.println("getPendentPeople");
//        Event e = null;
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        EventManager instance = (EventManager)container.getContext().lookup("java:global/classes/EventManager");
//        List<Users> expResult = null;
//        List<Users> result = instance.getPendentPeople(e);
//        assertEquals(expResult, result);
//        container.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of suggestNewDate method, of class EventManager.
//     */
//    @Test
//    public void testSuggestNewDate() throws Exception {
//        System.out.println("suggestNewDate");
//        Event event = null;
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        EventManager instance = (EventManager)container.getContext().lookup("java:global/classes/EventManager");
//        Date expResult = null;
//        Date result = instance.suggestNewDate(event);
//        assertEquals(expResult, result);
//        container.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of checkWeatherForecast method, of class EventManager.
//     */
//    @Test
//    public void testCheckWeatherForecast_WeatherCondition_WeatherCondition() throws Exception {
//        System.out.println("checkWeatherForecast");
//        WeatherCondition acceptedWeatherCondition = null;
//        WeatherCondition weatherForecast = null;
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        EventManager instance = (EventManager)container.getContext().lookup("java:global/classes/EventManager");
//        boolean expResult = false;
//        boolean result = instance.checkWeatherForecast(acceptedWeatherCondition, weatherForecast);
//        assertEquals(expResult, result);
//        container.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of checkWeatherForecast method, of class EventManager.
//     */
//    @Test
//    public void testCheckWeatherForecast_Event() throws Exception {
//        System.out.println("checkWeatherForecast");
//        Event event = null;
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        EventManager instance = (EventManager)container.getContext().lookup("java:global/classes/EventManager");
//        boolean expResult = false;
//        boolean result = instance.checkWeatherForecast(event);
//        assertEquals(expResult, result);
//        container.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of checkWeatherOneDayBefore method, of class EventManager.
//     */
//    @Test
//    public void testCheckWeatherOneDayBefore() throws Exception {
//        System.out.println("checkWeatherOneDayBefore");
//        Event event = null;
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        EventManager instance = (EventManager)container.getContext().lookup("java:global/classes/EventManager");
//        boolean expResult = false;
//        boolean result = instance.checkWeatherOneDayBefore(event);
//        assertEquals(expResult, result);
//        container.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of checkUserForInvitation method, of class EventManager.
//     */
//    @Test
//    public void testCheckUserForInvitation() throws Exception {
//        System.out.println("checkUserForInvitation");
//        Users user = null;
//        Event event = null;
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        EventManager instance = (EventManager)container.getContext().lookup("java:global/classes/EventManager");
//        boolean expResult = false;
//        boolean result = instance.checkUserForInvitation(user, event);
//        assertEquals(expResult, result);
//        container.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of weatherUpdater method, of class EventManager.
//     */
//    @Test
//    public void testWeatherUpdater() throws Exception {
//        System.out.println("weatherUpdater");
//        Event e = null;
//        WeatherCondition newWeat = null;
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        EventManager instance = (EventManager)container.getContext().lookup("java:global/classes/EventManager");
//        instance.weatherUpdater(e, newWeat);
//        container.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of save method, of class EventManager.
//     */
//    @Test
//    public void testSave_Event() throws Exception {
//        System.out.println("save");
//        Event event = null;
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        EventManager instance = (EventManager)container.getContext().lookup("java:global/classes/EventManager");
//        instance.save(event);
//        container.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of save method, of class EventManager.
//     */
//    @Test
//    public void testSave_Invite() throws Exception {
//        System.out.println("save");
//        Invite invite = null;
//        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
//        EventManager instance = (EventManager)container.getContext().lookup("java:global/classes/EventManager");
//        instance.save(invite);
//        container.close();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
