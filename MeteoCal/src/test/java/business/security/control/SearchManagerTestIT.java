/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package business.security.control;

import business.security.control.UserManager;
import business.security.control.SearchManager;
import business.security.control.NotificationManager;
import business.security.control.EventManager;
import business.security.entity.Event;
import business.security.entity.Group;
import business.security.entity.Invite;
import business.security.entity.Notification;
import business.security.entity.NotificationType;
import business.security.entity.PredefinedTypology;
import business.security.entity.Users;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.RollbackException;
import javax.transaction.UserTransaction;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author m-daniele
 */
@RunWith(Arquillian.class)
public class SearchManagerTestIT {
    
    @EJB
            SearchManager searchManager;
    
    @EJB
            EventManager eventManager;
    
    @EJB
            UserManager userManager;
    
    @EJB
            NotificationManager notificationManager;
    
    @PersistenceContext
            EntityManager em;
    
    @Inject
            UserTransaction utx;
    
    Users user1, user2;
    Event event1, event2;
    Invite invite1, invite2;
    Notification notification1, notification2;
    List<Users> users;
    List<Event> events;
    List<Invite> invites;
    List<Notification> notifications;
    
    
    @Deployment
    public static WebArchive createArchiveAndDeploy(){
        return ShrinkWrap.create(WebArchive.class)
                .addClass(SearchManager.class)
                .addClass(EventManager.class)
                .addClass(UserManager.class)
                .addClass(NotificationManager.class)
                .addClass(EntityManager.class)
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
        
        invite1 = new Invite();
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
        em.persist(notification2);
        
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
        em.createQuery("delete from USERS").executeUpdate();
        utx.commit();
    }
    
    
    @Test
    public void testFindUser(){
        
        assertEquals(user1,searchManager.findUser(user1.getEmail()));
        
        assertNull(searchManager.findUser("aaa"));
        
        
        assertNull(searchManager.findUser("aaa"));
        
    }
    
    @Test
    public void testFindUsersFromNameSurname(){
        users = new ArrayList();
        users.add(user1);
        users.add(user2);
        assertEquals(2,searchManager.findUsersFromNameSurname("try", "try").size());
        assertTrue(searchManager.findUsersFromNameSurname("try", "try").containsAll(users));
    }
    
    @Test
    public void testFindAllEvents() {

        events = new ArrayList();
        events.add(event2);
        events.add(event1);
        assertEquals(2, searchManager.findAllEvents().size());
        assertTrue(searchManager.findAllEvents().containsAll(events));
       
    }
    
    @Test
    public void testFindAllFutureEvents() {
        events = new ArrayList();
        events.add(event2);
        
        assertEquals(1, searchManager.findAllFutureEvents().size());
        assertEquals(events, searchManager.findAllFutureEvents());
        
    }
    
    @Test
    public void testFindAllPastEvents() {
        
        events = new ArrayList();
        events.add(event1);
        
        assertEquals(1, searchManager.findAllPastEvents().size());
        assertEquals(events, searchManager.findAllPastEvents());
        
        
    }
    
    @Test
    public void testFindUserEvents() {
        
        
        events = new ArrayList();
        events.add(event1);
        assertEquals(events, searchManager.findUserEvent(user1));
        
        events = new ArrayList();
        events.add(event1);
        events.add(event2);
        assertEquals(events, searchManager.findUserEvent(user2));
    }
    
    @Test
    public void testFindInviteRelatedToAnEvent() {
        invites = new ArrayList();
        invites.add(invite1);
        assertEquals(invites,searchManager.findInviteRelatedToAnEvent(event1));
        
        invites = new ArrayList();
        invites.add(invite2);
        assertEquals(invites,searchManager.findInviteRelatedToAnEvent(event2));
    }
    
    @Test
    public void testFindNotReadNotification() {
        notifications = new ArrayList();
        notifications.add(notification2);
        assertEquals(1, searchManager.findNotReadNotification(user1).size());
        assertEquals(notifications, searchManager.findNotReadNotification(user1));
        notifications = new ArrayList<>();
        assertEquals(0, searchManager.findNotReadNotification(user2).size());
        assertEquals(notifications, searchManager.findNotReadNotification(user2));
    }

}
