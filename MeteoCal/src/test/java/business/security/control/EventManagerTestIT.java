package business.security.control;

/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/

import business.security.control.PasswordEncrypter;
import business.security.control.JsonPars;
import business.security.control.UserManager;
import business.security.control.SearchManager;
import business.security.control.UserInformationLoader;
import business.security.control.MailManager;
import business.security.control.NotificationManager;
import business.security.control.EventManager;
import business.security.entity.Event;
import business.security.entity.Group;
import business.security.entity.Invite;
import business.security.entity.Notification;
import business.security.entity.NotificationType;
import business.security.entity.PredefinedTypology;
import business.security.entity.Users;
import business.security.entity.WeatherCondition;
import exception.DateConsistencyException;
import exception.WeatherException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import javax.validation.ConstraintViolationException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

/**
 *
 * @author m-daniele
 */
@RunWith(Arquillian.class)
public class EventManagerTestIT {
    
    @EJB
            ExtensionEventManager eventManager;
    
    @EJB
            SearchManager searchManager;
    
    @EJB
            UserManager userManager;
    
    @PersistenceContext
            EntityManager em;
    
    @Inject
            UserTransaction utx;
    
    @EJB
    private NotificationManager notificationManager;
    
    @EJB
    private ExtensionUserInfoLoader userInformationLoader;
    
    @EJB
    private MailManager mailManager;
    
    @EJB
    private JsonPars p;
    
    
    Event event1, event2, event3, event4, event5;
    Users user1, user2, user3, user4;
    WeatherCondition acceptedWC, WCfail, WCnotfail;
    Invite invite1, invite2, invite3, invite4;
    
    
    
    @Deployment
    public static WebArchive createArchiveAndDeploy(){
        return ShrinkWrap.create(WebArchive.class)
                .addClass(ExtensionEventManager.class)
                .addClass(ExtensionUserInfoLoader.class)
                .addClass(EventManager.class)
                .addClass(UserInformationLoader.class)
                .addClass(JsonPars.class)
                .addClass(UserManager.class)
                .addClass(SearchManager.class)
                .addClass(NotificationManager.class)
                .addClass(EntityManager.class)
                .addClass(MailManager.class)
                .addPackage(Event.class.getPackage())
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        
    }
    
    @Before
    public void prepareTests() throws Exception {
        clearData();
        insertData();
    }
    
    
    
    private void insertData() throws Exception {
        utx.begin();
        em.joinTransaction();
        
        //user1 è il logged user
        user1 = new Users();
        user1.setEmail("user1@mail.it");
        user1.setPassword("t");
        user1.setBirthday(new Date(92,1,15));
        user1.setName("try");
        user1.setSurname("try");
        user1.setGroupName(Group.USERS);
        em.persist(user1);
        
        user2 = new Users();
        user2.setEmail("user2@mail.it");
        user2.setPassword("t");
        user2.setBirthday(new Date(92,1,15));
        user2.setName("try");
        user2.setSurname("try");
        user2.setGroupName(Group.USERS);
        em.persist(user2);
        
        user3 = new Users();
        user3.setEmail("user3@mail.it");
        user3.setPassword("t");
        user3.setBirthday(new Date(92,1,15));
        user3.setName("try");
        user3.setSurname("try");
        user3.setGroupName(Group.USERS);
        em.persist(user3);
        
        user4 = new Users();
        user4.setEmail("user4@mail.it");
        user4.setPassword("t");
        user4.setBirthday(new Date(92,1,15));
        user4.setName("try");
        user4.setSurname("try");
        user4.setGroupName(Group.USERS);
        em.persist(user4);
        
        event1= new Event();
        event1.setId((long) 101);
        event1.setName("Event1");
        event1.setTimeStart(new Date(114,1, 17));
        event1.setTimeEnd(new Date(114,1,17));
        event1.setDescription("Description of event1");
        event1.setLocation("Milano");
        event1.setPublicEvent(true);
        event1.setOrganizer(user1);
        event1.setPredefinedTypology(PredefinedTypology.dinner);
        event1.setOutdoor(false);
        em.persist(event1);
        
        event2= new Event();
        event2.setId((long)102);
        event2.setName("Event1");
        event2.setTimeStart(new Date(116,1, 17));
        event2.setTimeEnd(new Date(116,1,17));
        event2.setDescription("Description of event1");
        event2.setLocation("Milano");
        event2.setPublicEvent(true);
        event2.setOrganizer(user1);
        event2.setPredefinedTypology(PredefinedTypology.dinner);
        event2.setOutdoor(false);
        em.persist(event2);
        
        acceptedWC = new WeatherCondition();
        acceptedWC.setIcon("01d");
        acceptedWC.setId((long)201);
        acceptedWC.setPrecipitation(false);
        acceptedWC.setTemperature((float) 1.0);
        acceptedWC.setWind((float) 1.0);
        em.persist(acceptedWC);
        
        event3= new Event();
        event3.setId((long)103);
        event3.setName("Event1");
        event3.setTimeStart(new Date(117,1, 17));
        event3.setTimeEnd(new Date(117,1,19));
        event3.setDescription("Description of event1");
        event3.setLocation("Milano");
        event3.setPublicEvent(true);
        event3.setOrganizer(user1);
        event3.setPredefinedTypology(PredefinedTypology.dinner);
        event3.setOutdoor(true);
        event3.setAcceptedWeatherConditions(acceptedWC);
        em.persist(event3);
        
        event4= new Event(); 
        event4.setId((long)104);
        event4.setName("Event1");
        event4.setTimeStart(new Date(117,1, 18));
        event4.setTimeEnd(new Date(117,1,18));
        event4.setDescription("Description of event1");
        event4.setLocation("Milano");
        event4.setPublicEvent(true);
        event4.setOrganizer(user4);
        event4.setPredefinedTypology(PredefinedTypology.dinner);
        event4.setOutdoor(true);
        event4.setAcceptedWeatherConditions(acceptedWC);
        em.persist(event4);
        
        event5= new Event(); 
        event5.setId((long)105);
        event5.setName("Event1");
        event5.setTimeStart(new Date(120,1, 18));
        event5.setTimeEnd(new Date(120,1,19));
        event5.setDescription("Description of event1");
        event5.setLocation("Milano");
        event5.setPublicEvent(true);
        event5.setOrganizer(user4);
        event5.setPredefinedTypology(PredefinedTypology.dinner);
        event5.setOutdoor(true);
        event5.setAcceptedWeatherConditions(acceptedWC);
        em.persist(event5);
        
        invite1 = new Invite();
        invite1.setEvent(event1);
        invite1.setUser(user2);
        invite1.setStatus(Invite.InviteStatus.accepted);
        em.persist(invite1);
        
        invite2 = new Invite();
        invite2.setEvent(event1);
        invite2.setUser(user3);
        invite2.setStatus(Invite.InviteStatus.invited);
        em.persist(invite2);
        
        invite3 = new Invite();
        invite3.setEvent(event1);
        invite3.setUser(user4);
        invite3.setStatus(Invite.InviteStatus.notAccepted);
        em.persist(invite3);
        
        invite4 = new Invite();
        invite4.setEvent(event3);
        invite4.setUser(user2);
        invite4.setStatus(Invite.InviteStatus.accepted);
        em.persist(invite4);
        
        WCfail = new WeatherCondition();
        WCfail.setIcon("01d");
        WCfail.setPrecipitation(false);
        WCfail.setTemperature(303);
        WCfail.setWind(4);
        em.persist(WCfail);
        
        
        WCnotfail = new WeatherCondition();
        WCnotfail.setIcon("01d");
        WCnotfail.setPrecipitation(false);
        WCnotfail.setTemperature(278);
        WCnotfail.setWind(4);
        em.persist(WCnotfail);
        
        utx.commit();
        // clear the persistence context (first-level cache)
        em.clear();
    }
    
    
    private void clearData() throws Exception {
        utx.begin();
        em.joinTransaction();
        System.out.println("Dumping old records...");
        em.createQuery("delete from NOTIFICATION").executeUpdate();
        em.createQuery("delete from INVITE").executeUpdate();
        em.createQuery("delete from EVENT").executeUpdate();
        em.createQuery("delete from WeatherCondition").executeUpdate();
        em.createQuery("delete from USERS").executeUpdate();
        utx.commit();
    }
    
    @Test
    public void EventManagerShouldBeInjected(){
        assertNotNull(eventManager);
    }
    
    @Test
    public void EntityManagerShouldBeInjected(){
        assertNotNull(em);
    }
    
    @Test
    public void ValidUserIsSaved(){
        String clearPassword = "password";
        String email = "email@email.com";
        Users newUser = new Users();
        newUser.setEmail(email);
        newUser.setPassword(clearPassword);
        newUser.setBirthday(new Date(92,1,15));
        newUser.setName("try");
        newUser.setSurname("try");
        newUser.setGroupName(Group.USERS);
        userManager.save(newUser);
        Users foundUser = searchManager.findUser(email);
        
        // Check presence
        assertNotNull(foundUser);
        
        // Check that password is encrypted
        assertNotNull(foundUser.getPassword());
        assertThat(foundUser.getPassword(),is(PasswordEncrypter.encryptPassword(clearPassword)));
    }
    
    
    @Test
    public void newUsersShouldBeValid() {
        Users newUser = new Users();
        newUser.setEmail("invalidemail");
        try {
            userManager.save(newUser);
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof ConstraintViolationException);
        }
        assertNull(em.find(Users.class, "invalidemail"));
    }
    
    @Test
    public void testCreateEvent(){
        
        try {
            Event newEvent= new Event();
            newEvent.setId((long) 1456772);
            newEvent.setName("Event1");
            newEvent.setTimeStart(new Date(116,1, 20));
            newEvent.setTimeEnd(new Date(116,1,21));
            newEvent.setDescription("Description of event1");
            newEvent.setLocation("Milano");
            newEvent.setPublicEvent(true);
            newEvent.setOrganizer(user1);
            newEvent.setPredefinedTypology(PredefinedTypology.dinner);
            newEvent.setOutdoor(false);
            
            eventManager.createEvent(newEvent);
            
            Event eventById = eventManager.getEventById(newEvent.getId());
            assertEquals(newEvent, eventById);
        } catch (DateConsistencyException ex) {
            fail("Error in date");
        }
        
        
    }
    
    
    @Test
    public void testRemoveEvent() throws Exception {
        
        
        
        assertEquals(event3, invite4.getEvent());
        assertEquals(1, searchManager.findInviteRelatedToAnEvent(event3).size());
        assertEquals(invite4,searchManager.findInviteRelatedToAnEvent(event3).get(0));
        
        eventManager.removeEvent(event3);
        //Verify that the event attribute deleted is true
        assertTrue(eventManager.getEventById(event3.getId()).isDeleted());
        //Check if there already any invite
        assertEquals(0,eventManager.getInvitedPeople(event3.getId()).size());
        
        for(Notification n : searchManager.findNotReadNotification(user2) ){
            if(n.getType()== NotificationType.deletedEvent){
                assertEquals("Exact event",event3, n.getRelatedEvent());
                assertEquals("Exact User", user2, n.getNotificatedUser());
            }
            
        }
        //Check if notifications are sent
        // assertEquals(1,searchManager.findNotReadNotification(user2).size());
        
    }
    
    @Test
    public void testGetInvitedPeople(){
     assertEquals(3, eventManager.getInvitedPeople(event1.getId()).size());
     List<Users> invited = new ArrayList();  
     invited.add(user2); 
     invited.add(user3);
     invited.add(user4);
     assertTrue(eventManager.getInvitedPeople(event1.getId()).containsAll(invited));
     
    }
    
    @Test
    public void testGetEventById() {
        assertEquals(event1, eventManager.getEventById(event1.getId()));
        assertEquals(event2, eventManager.getEventById(event2.getId()));
        assertEquals(event3, eventManager.getEventById(event3.getId()));
    }
    
    @Test 
    public void testLoadUserCreatedEvents() {
        assertEquals(3, eventManager.loadUserCreatedEvents(user1).size());
        assertEquals(0, eventManager.loadUserCreatedEvents(user2).size());
        List<Event> events = new ArrayList(); 
        events.add(event1);
        events.add(event2);
        events.add(event3);
        assertTrue(eventManager.loadUserCreatedEvents(user1).containsAll(events));
        
    }
    
    @Test
    public void testLoadUserAcceptedEvents() {
        assertEquals(2, eventManager.loadUserAcceptedEvents(user2).size());
        List<Event> events = new ArrayList(); 
        events.add(event1); 
        events.add(event3);
        assertTrue(eventManager.loadUserAcceptedEvents(user2).containsAll(events));
        assertEquals(0, eventManager.loadUserAcceptedEvents(user3).size());
        assertEquals(0, eventManager.loadUserAcceptedEvents(user1).size());
        assertEquals(0, eventManager.loadUserAcceptedEvents(user4).size());
        
    }
    
    @Test
    public void testGetAcceptedPeople() {
        assertEquals(1, eventManager.getAcceptedPeople(event1).size());
        assertEquals(user2, eventManager.getAcceptedPeople(event1).get(0));
        assertEquals(0, eventManager.getAcceptedPeople(event2).size());
        
    }
    
    @Test
    public void testGetPendentPeople() {
        assertEquals(1, eventManager.getPendentPeople(event1).size());
        assertEquals(user3, eventManager.getPendentPeople(event1).get(0));
        assertEquals(0, eventManager.getPendentPeople(event2).size());
        
    }
    
    @Test
    public void testGetRefusedPeople() {
        assertEquals(1, eventManager.getRefusedPeople(event1).size());
        assertEquals(user4, eventManager.getRefusedPeople(event1).get(0));
        assertEquals(0, eventManager.getRefusedPeople(event2).size());
        
    }
    
    @Test
    public void testCheckWeather() throws WeatherException {
        assertEquals(true, eventManager.checkWeatherForecast(acceptedWC, WCfail));
        assertEquals(false, eventManager.checkWeatherForecast(acceptedWC, WCnotfail));
    }
    
    @Test
    public void testUpdateEventInformation() throws DateConsistencyException, WeatherException {
        event1.setName("newNameEvent1");
        event1.setPredefinedTypology(PredefinedTypology.lunch);
        eventManager.updateEventInformation(event1, null);
        
        assertEquals("newNameEvent1", eventManager.getEventById(event1.getId()).getName());
        assertEquals(PredefinedTypology.lunch, eventManager.getEventById(event1.getId()).getPredefinedTypology());
        
        
        WeatherCondition wcEvent3 = event3.getAcceptedWeatherConditions();
        wcEvent3.setPrecipitation(true);
        wcEvent3.setWind((float)2.0);
        eventManager.updateEventInformation(event3, wcEvent3);
        assertEquals(true, eventManager.checkWeatherForecast(event3.getAcceptedWeatherConditions(), WCfail));
        assertEquals(true, eventManager.checkWeatherForecast(event3.getAcceptedWeatherConditions(), WCnotfail));
        
        
    }
    
    @Test
    public void testCheckDateConsistency() {
        assertTrue(!eventManager.checkDateConsistency(event4));
        assertTrue(eventManager.checkDateConsistency(event5));
        
    }
    
    

}
