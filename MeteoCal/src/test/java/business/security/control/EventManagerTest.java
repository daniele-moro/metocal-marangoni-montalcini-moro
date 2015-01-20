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
import exception.InviteException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Matchers.anyInt;
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
        eventManager.searchManager = mock(SearchManager.class);
        eventManager.notificationManager = mock(NotificationManager.class);

        events = new ArrayList();
        users = new ArrayList();
        invites = new ArrayList();

        //Creation of the users used in the tests
        for (int i = 0; i < 4; i++) {
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
            newEvent.setId(Long.MIN_VALUE + i);
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
        //User 2 invited to event 0
        Invite invite1 = new Invite();
        invite1.setEvent(events.get(0));
        invite1.setUser(users.get(2));
        invite1.setStatus(Invite.InviteStatus.invited);
        invites.add(invite1);

        //User 3 invited to event 0
        Invite invite2 = new Invite();
        invite2.setEvent(events.get(0));
        invite2.setUser(users.get(3));
        invite2.setStatus(Invite.InviteStatus.accepted);
        invites.add(invite2);

        //User 0 invited to event 1
        Invite invite3 = new Invite();
        invite3.setEvent(events.get(1));
        invite3.setUser(users.get(0));
        invite3.setStatus(Invite.InviteStatus.accepted);
        invites.add(invite3);
        
        
        //Setting of a forecast
        WeatherCondition forecast = new WeatherCondition();
        forecast.setIcon("BB");
        forecast.setId((long)2);
        events.get(0).setWeatherForecast(forecast);

        //When the system query the db to get the weathear forecast of the event 0
        TypedQuery<WeatherCondition> query = mock(TypedQuery.class);
        when(eventManager.em.createQuery("SELECT w FROM WeatherCondition w WHERE w.id = ?1")).thenReturn(query);
        when(query.setParameter(1, events.get(0).getWeatherForecast().getId())).thenReturn(query);
        List<WeatherCondition> listForecast = new ArrayList<>();
        listForecast.add(forecast);
        when(query.getResultList()).thenReturn(listForecast);
        
        
        //The logged user is the user 0
        when(eventManager.getLoggedUser()).thenReturn(users.get(0));
        //Invites relative of the event 0
        when(eventManager.searchManager.findInviteRelatedToAnEvent(events.get(0))).thenReturn(invites.subList(0, 2));
        //List of createdEvents and acceptedEvents of user 0
        when(eventManager.userInformationLoader.loadCreatedEvents()).thenReturn(events.subList(0, 1));
        when(eventManager.userInformationLoader.loadAcceptedEvents()).thenReturn(events.subList(1, 2));
        
        //List of accepted people of the event 0
        TypedQuery<Users> query2 = mock(TypedQuery.class);
        when(eventManager.em.createQuery("SELECT u FROM INVITE i, USERS u WHERE i.event = ?1 AND i.user.email = u.email AND i.status = ?2")).thenReturn(query2);
        when(query2.setParameter(1, events.get(0))).thenReturn(query2);
        when(query2.setParameter(2, Invite.InviteStatus.accepted)).thenReturn(query2);
        List<Users> listUs = new ArrayList<>();
        listUs.add(users.get(3));
        when(query2.getResultList()).thenReturn(listUs);
        
        //Return events of the user 2
        List<Event> tempList = new ArrayList<>();
        tempList.add(events.get(0));
        when(eventManager.searchManager.findUserEvent(users.get(2))).thenReturn(tempList);
        tempList= new ArrayList<>();
        tempList.add(events.get(0));
        //Return events of the user 3
        when(eventManager.searchManager.findUserEvent(users.get(3))).thenReturn(tempList);
    }

    /**
     * Test of createEvent method, of class EventManager. correct functionality
     */
    @Test
    public void testCreateEventSimple() throws Exception {
        System.out.println("createEvent");

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

    /**
     * Test of createEvent method, of class EventManager. Exception in case of
     * Date of start after the end
     */
    @Test(expected = DateConsistencyException.class)
    public void testCreateEventExceptionDateStartAfterEnd() throws Exception {
        //START AFTER END

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

    /**
     * Test of createEvent method, of class EventManager. Exception in case of
     * overlapping
     */
    @Test(expected = DateConsistencyException.class)
    public void testCreateEventExceptionOverlap() throws Exception {
        //OVERLAP WITH AN EVENT
        Event newEvent = new Event();
        newEvent.setId((long) 100);
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

    /**
     * Test of createEvent method, of class EventManager. correct functionality
     * with an outdoor event
     */
    @Test
    public void testCreateEventOutdoorEvent() throws Exception {
        //OUTDOOR EVENT
        WeatherCondition forecasted = new WeatherCondition();
        forecasted.setIcon("BB");
        forecasted.setPrecipitation(true);
        forecasted.setTemperature(1);
        forecasted.setWind(0);

        when(eventManager.p.parsingWeather(anyString(), anyString(), anyObject())).thenReturn(forecasted);

        Event newEvent = new Event();
        newEvent.setId((long) 100);
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

        assertThat((WeatherCondition) captor.getAllValues().get(0), is(awc));
        assertThat((Event) captor.getAllValues().get(1), is(newEvent));
        assertThat((WeatherCondition) captor.getAllValues().get(2), is(forecasted));
        

    }

    /**
     * Test of checkDateConsistency method, of class EventManager.
     */
    @Test
    public void testCheckDateConsistency() throws Exception {
        System.out.println("checkDateConsistency Overlap");

        Event newEvent = new Event();
        newEvent.setId((long) 100);
        newEvent.setName("OverlappedEvent");
        newEvent.setTimeStart(new Date(116, 2, 18));
        newEvent.setTimeEnd(new Date(116, 2, 19));
        newEvent.setDescription("Description of event");
        newEvent.setLocation("Milano");
        newEvent.setPublicEvent(true);
        newEvent.setPredefinedTypology(PredefinedTypology.dinner);
        newEvent.setOutdoor(false);

        assertThat(eventManager.checkDateConsistency(newEvent), is(false));

        System.out.println("checkDateConsistency Begin after end");
        newEvent = new Event();
        newEvent.setId((long) 100);
        newEvent.setName("OverlappedEvent");
        newEvent.setTimeStart(new Date(115, 2, 18));
        newEvent.setTimeEnd(new Date(116, 2, 19));
        newEvent.setDescription("Description of event");
        newEvent.setLocation("Milano");
        newEvent.setPublicEvent(true);
        newEvent.setPredefinedTypology(PredefinedTypology.dinner);
        newEvent.setOutdoor(false);

        assertThat(eventManager.checkDateConsistency(newEvent), is(false));

        System.out.println("checkDateConsistency NO Overlap");
        newEvent = new Event();
        newEvent.setId((long) 100);
        newEvent.setName("NotOverlappedEvent");
        newEvent.setTimeStart(new Date(116, 4, 18));
        newEvent.setTimeEnd(new Date(116, 4, 19));
        newEvent.setDescription("Description of event");
        newEvent.setLocation("Milano");
        newEvent.setPublicEvent(true);
        newEvent.setPredefinedTypology(PredefinedTypology.dinner);
        newEvent.setOutdoor(false);

        assertThat(eventManager.checkDateConsistency(newEvent), is(true));
    }

    /**
     * Test of addInvitation method, of class EventManager. correct
     * functionality
     */
    @Test
    public void testAddInvitation() throws Exception {

        //Add invitation of user 1 to event 0
        eventManager.addInvitation(users.get(1), events.get(0));

        //Capture argument passed to method of mocked class
        ArgumentCaptor<Event> captor1 = ArgumentCaptor.forClass(Event.class);
        ArgumentCaptor<Users> captor2 = ArgumentCaptor.forClass(Users.class);

        verify(eventManager.notificationManager, times(1)).createInviteNotification(captor1.capture(), captor2.capture());
        //Verify that the argument is correct
        assertThat(captor1.getValue(), is(events.get(0)));
        assertThat(captor2.getValue(), is(users.get(1)));
    }

    /**
     * Test of addInvitation method, of class EventManager. exception in case of
     * null parameter
     */
    @Test(expected = InviteException.class)
    public void testAddInvitationPassingNullParameters() throws Exception {

        eventManager.addInvitation(null, events.get(0));
        eventManager.addInvitation(users.get(1), null);
        eventManager.addInvitation(null, null);
    }

    /**
     * Test of addInvitation method, of class EventManager. exception in case of
     * passing the organizer
     */
    @Test(expected = InviteException.class)
    public void testAddInvitationPassingOrganizer() throws Exception {

        //Add invitation of user 0 to event 0
        eventManager.addInvitation(users.get(0), events.get(0));
    }

    /**
     * Test of addInvitation method, of class EventManager. exception in case of
     * passing user already invited
     */
    @Test(expected = InviteException.class)
    public void testAddInvitationPassingInvitedUser() throws Exception {

        //Add invitation of user 2 to event 0
        eventManager.addInvitation(users.get(2), events.get(0));
    }

    /**
     * Test of removeEvent method, of class EventManager. correct functionality
     */
    @Test
    public void testRemoveEvent() throws Exception {
        System.out.println("removeEvent");

        eventManager.removeEvent(events.get(0));

        //Check if the deleted field is setted
        assertThat(events.get(0).isDeleted(), is(true));

        //Capture argument passed to method of mocked class
        ArgumentCaptor<Invite> captorNotif = ArgumentCaptor.forClass(Invite.class);
        ArgumentCaptor<Invite> captorRemove = ArgumentCaptor.forClass(Invite.class);

        verify(eventManager.notificationManager, times(2)).createDeleteNotification(captorNotif.capture());
        verify(eventManager.em, times(2)).remove(captorRemove.capture());

        //Verify that the argument is correct
        assertThat(captorNotif.getAllValues(), is(captorRemove.getAllValues()));

        assertTrue(captorNotif.getAllValues().contains(invites.get(0)));
        assertTrue(captorNotif.getAllValues().contains(invites.get(1)));

        assertTrue(captorRemove.getAllValues().contains(invites.get(0)));
        assertTrue(captorRemove.getAllValues().contains(invites.get(1)));
    }

    /**
     * Test of updateEventInformation method, of class EventManager.
     */
    @Test
    public void testUpdateEventInformation() throws Exception {
//        WeatherCondition newWeather = new WeatherCondition();
//        System.out.println("EEEE");
//        Query query = mock(TypedQuery.class);
//        when(eventManager.em.createQuery(anyString())).thenReturn(query);
//        when(query.setParameter(anyInt(), anyObject())).thenReturn(query);
//        when(query.executeUpdate()).thenReturn(anyInt());
//        
//        
//        eventManager.updateEventInformation(events.get(0), newWeather);
//
//        
//        //Capture argument passed to method of mocked class
//        ArgumentCaptor<String> captorMail = ArgumentCaptor.forClass(String.class);
//
//        verify(eventManager.mailManager).sendMail(captorMail.capture(), anyString(), anyString());
//
//        //Verify that the argument is correct
//        assertThat(captorMail.getValue(), is(users.get(3).getEmail()));
        
    }

    /**
     * Test of addParticipantToEvent method, of class EventManager. Exception
     * when the user has an overlapping event
     */
    @Test(expected = DateConsistencyException.class)
    public void testAddParticipantToEventException() throws Exception {
        //Event overlapped with event of user0 generates exception
        Event newEvent = new Event();
        newEvent.setName("Event");
        newEvent.setId((long) 100);
        newEvent.setTimeStart(new Date(116, 2, 17));
        newEvent.setTimeEnd(new Date(116, 2, 18));
        newEvent.setDescription("Description of event");
        newEvent.setLocation("Milano");
        newEvent.setPublicEvent(true);
        newEvent.setPredefinedTypology(PredefinedTypology.dinner);
        newEvent.setOutdoor(false);

        eventManager.addParticipantToEvent(newEvent);
    }

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
    /**
     * Test of checkUserForInvitation method, of class EventManager.
     */
    @Test
    public void testCheckUserForInvitation() throws Exception {
        assertThat(eventManager.checkUserForInvitation(users.get(2), events.get(0)), is(false));
        assertThat(eventManager.checkUserForInvitation(users.get(1), events.get(0)), is(true));

    }

    /**
     * Test of weatherUpdater method, of class EventManager.
     * Outdoor event with weather change
     */
    @Test
    public void testWeatherUpdater() throws Exception {
        WeatherCondition newForecast = new WeatherCondition();
        newForecast.setId((long)1);
        newForecast.setIcon("AA");
        events.get(0).setOutdoor(true);
        
        eventManager.weatherUpdater(events.get(0), newForecast);
        
        //Capture argument passed to method of mocked class
        ArgumentCaptor<String> captorMail = ArgumentCaptor.forClass(String.class);

        verify(eventManager.mailManager, times(2)).sendMail(captorMail.capture(), anyString(), anyString());

        //Verify that the argument is correct
        assertTrue(captorMail.getAllValues().contains(users.get(0).getEmail()));
        assertTrue(captorMail.getAllValues().contains(users.get(3).getEmail()));
        
    }

}
