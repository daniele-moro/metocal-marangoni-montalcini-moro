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
import exception.DateConsistencyException;
import java.security.Principal;
import java.util.Date;
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

    
    Event event1, event2;
    Users user1;
    Users user2;
    Invite invite1;
    
    
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
        
        event1= new Event();
        event1.setId((long) 345667);
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
        event2.setId((long)3);
        event2.setName("Event1");
        event2.setTimeStart(new Date(116,1, 17));
        event2.setTimeEnd(new Date(116,1,17));
        event2.setDescription("Description of event1");
        event2.setLocation("Milano");
        event2.setPublicEvent(true);
        event2.setOrganizer(user2);
        event2.setPredefinedTypology(PredefinedTypology.dinner);
        event2.setOutdoor(false);
        em.persist(event2);
        
        /*invite1 = new Invite();
        invite1.setEvent(event1);
        invite1.setUser(user2);
        invite1.setStatus(Invite.InviteStatus.accepted);
        em.persist(invite1);
        
        notification1= new Notification();
        notification1.setGenerationDate(new Date());
        notification1.setNotificatedUser(user2);
        notification1.setRelatedEvent(event1);
        notification1.setSeen(true);
        notification1.setType(NotificationType.invite);
        em.persist(notification1);
        
        
        invite2 = new Invite();
        invite2.setEvent(event2);
        invite2.setUser(user1);
        invite2.setStatus(Invite.InviteStatus.invited);
        em.persist(invite2);

        notification2 = new Notification();
        notification2.setGenerationDate(new Date());
        notification2.setNotificatedUser(user1);
        notification2.setRelatedEvent(event2);
        notification2.setSeen(false);
        notification2.setType(NotificationType.invite);
        em.persist(notification2);*/
        
        utx.commit();
        // clear the persistence context (first-level cache)
        em.clear();
    }
    
    
    private void clearData() throws Exception {
        utx.begin();
        em.joinTransaction();
        System.out.println("Dumping old records...");
        //em.createQuery("delete from NOTIFICATION").executeUpdate();
        //em.createQuery("delete from INVITE").executeUpdate();
        em.createQuery("delete from EVENT").executeUpdate();
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
            //Create Users
            Users user1=new Users();
            user1.setEmail("z@z.it");
            user1.setPassword("myPass");
            user1.setBirthday(new Date(92,1,15));
            user1.setName("try");
            user1.setSurname("try");
            user1.setGroupName(Group.USERS);
                userManager.save(user1);
            
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
    public void testRemoveEvent(){
        user1 = new Users();
        user1.setEmail("t@t.it");
        user1.setPassword("t");
        user1.setBirthday(new Date(92,1,15));
        user1.setName("try");
        user1.setSurname("try");
        user1.setGroupName(Group.USERS);
        userManager.save(user1);
        
        user2 = new Users();
        user2.setEmail("t2@t2.it");
        user2.setPassword("t2");
        user2.setBirthday(new Date(92,1,15));
        user2.setName("try");
        user2.setSurname("try");
        user2.setGroupName(Group.USERS);
        userManager.save(user2);
        
        event1= new Event();
        event1.setId((long)2500);
        event1.setName("Event1");
        event1.setTimeStart(new Date(116,1, 20));
        event1.setTimeEnd(new Date(116,1,21));
        event1.setDescription("Description of event1");
        event1.setLocation("Milano");
        event1.setPublicEvent(true);
        event1.setOrganizer(user1);
        event1.setPredefinedTypology(PredefinedTypology.dinner);
        event1.setOutdoor(false);
        //eventManager.save(event1);
        
        invite1 = new Invite();
        invite1.setEvent(event1);
        invite1.setUser(user2);
        invite1.setStatus(Invite.InviteStatus.accepted);
        //eventManager.save(invite1);
        
        eventManager.removeEvent(event1);
        //Verify that the event attribute deleted is true
        assertTrue(eventManager.getEventById(event1.getId()).isDeleted());
        //Check if there already any invite
        assertEquals(0,eventManager.getInvitedPeople(event1.getId()).size());
        
        for(Notification n : searchManager.findNotReadNotification(user2) ){
            if(n.getType()== NotificationType.deletedEvent){
                assertEquals("Exact event",event1, n.getRelatedEvent());
                assertEquals("Exact User", user2, n.getNotificatedUser());
            }
            
        }
        //Check if notifications are sent
       // assertEquals(1,searchManager.findNotReadNotification(user2).size());
        
    }
    
    @Test
    public void testGetInvitedPeople(){
        user1 = new Users();
        user1.setEmail("t1@t1.it");
        user1.setPassword("t");
        user1.setBirthday(new Date(92,1,15));
        user1.setName("try");
        user1.setSurname("try");
        user1.setGroupName(Group.USERS);
        userManager.save(user1);
        
        user2 = new Users();
        user2.setEmail("t3@t3.it");
        user2.setPassword("t2");
        user2.setBirthday(new Date(92,1,15));
        user2.setName("try");
        user2.setSurname("try");
        user2.setGroupName(Group.USERS);
        userManager.save(user2);
        
        event1= new Event();
        event1.setName("Event1");
        event1.setTimeStart(new Date(116,1, 20));
        event1.setTimeEnd(new Date(116,1,21));
        event1.setDescription("Description of event1");
        event1.setLocation("Milano");
        event1.setPublicEvent(true);
        event1.setOrganizer(user1);
        event1.setPredefinedTypology(PredefinedTypology.dinner);
        event1.setOutdoor(false);
        //eventManager.save(event1);
        
        invite1 = new Invite();
        invite1.setEvent(event1);
        invite1.setUser(user2);
        invite1.setStatus(Invite.InviteStatus.accepted);
        //eventManager.save(invite1);
    }
    
    
    
    
}


class prova implements Principal{
    
    private String n;
    
    public void setLoggedUser(String name){
        this.n=name;
    }
    

    @Override
    public String getName() {
        return "z@z.it";
    }
    
}
