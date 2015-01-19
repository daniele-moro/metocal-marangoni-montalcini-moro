/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package business.security.control;

import business.security.entity.Event;
import business.security.entity.Group;
import business.security.entity.Invite;
import business.security.entity.Notification;
import business.security.entity.NotificationType;
import business.security.entity.PredefinedTypology;
import business.security.entity.Users;
import java.security.Principal;
import java.util.Date;
import javax.persistence.EntityManager;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * This class test is the Unit Test of the class NotificationManager
 * It tests all the method with EntityManager and MailManager Mocked
 * @author Daniele Moro
 */
public class NotificationManagerTest {
    
    private NotificationManager notificationManager;
    
    private Event event;
    private Users user;
    private Invite invite;
    
    @Before
    public void setUp() {
        notificationManager = new NotificationManager();
        notificationManager.em = mock(EntityManager.class);
        notificationManager.principal = mock(Principal.class);
        notificationManager.mailManager = mock(MailManager.class);
        
        //Creation of one Event, one User and one invite to test the class
        user= new Users();
        user.setEmail("user1@mail.it");
        user.setPassword("t");
        user.setBirthday(new Date(92,1,15));
        user.setName("try");
        user.setSurname("try");
        user.setGroupName(Group.USERS);
        
        event= new Event();
        event.setName("Event1");
        event.setTimeStart(new Date(114,1, 17));
        event.setTimeEnd(new Date(114,1,17));
        event.setDescription("Description of event1");
        event.setLocation("Milano");
        event.setPublicEvent(true);
        event.setOrganizer(user);
        event.setPredefinedTypology(PredefinedTypology.dinner);
        event.setOutdoor(false);
        
        invite = new Invite();
        invite.setEvent(event);
        invite.setUser(user);
        invite.setStatus(Invite.InviteStatus.accepted);
    }
    
    /**
     * Test of createInviteNotification method, of class NotificationManager.
     */
    @Test
    public void testCreateInviteNotification(){
        System.out.println("createInviteNotification");
        
        notificationManager.createInviteNotification(event, user);
        
        //Capture the argument passed to the mocked class
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> captorObj = ArgumentCaptor.forClass(Object.class);
        
        verify(notificationManager.mailManager, times(1)).sendMail(captor.capture(), anyString(), anyString());
        verify(notificationManager.em, times(2)).persist(captorObj.capture());
        
        //Verification that the method trys to send the email to the correct address
        assertThat(captor.getAllValues().get(0), is(user.getEmail()));
        
        //Verification that the method calls the persist method and creates the correct invite
        assertThat(captorObj.getAllValues().get(0), instanceOf(Invite.class));
        assertThat(((Invite)captorObj.getAllValues().get(0)).getUser(), is(user));
        assertThat(((Invite)captorObj.getAllValues().get(0)).getEvent(), is(event));
        assertThat(((Invite)captorObj.getAllValues().get(0)).getStatus(), is(Invite.InviteStatus.invited));
        
        //verification that the method calls the persist method and creates the correct notification
        assertThat(captorObj.getAllValues().get(1), instanceOf(Notification.class));
        assertThat(((Notification)captorObj.getAllValues().get(1)).getRelatedEvent(), is(event));
        assertThat(((Notification)captorObj.getAllValues().get(1)).getNotificatedUser(), is(user));
        assertTrue(((Notification)captorObj.getAllValues().get(1)).getGenerationDate().before(new Date())
                || ((Notification)captorObj.getAllValues().get(1)).getGenerationDate().equals(new Date()) );
        assertThat(((Notification)captorObj.getAllValues().get(1)).getType(), is(NotificationType.invite));
        assertThat(((Notification)captorObj.getAllValues().get(1)).isSeen(), is(false));
    }
    
    /**
     * Test of createDeleteNotification method, of class NotificationManager.
     */
    @Test
    public void testCreateDeleteNotification(){
        System.out.println("createDeleteNotification");
        
        notificationManager.createDeleteNotification(invite);
        
        //Capture the argument passed to the mocked class
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Notification> captorNot = ArgumentCaptor.forClass(Notification.class);
        
        verify(notificationManager.mailManager, times(1)).sendMail(captor.capture(), anyString(), anyString());
        verify(notificationManager.em, times(1)).persist(captorNot.capture());
        
        //Verification that the method trys to send the email to the correct address
        assertThat(captor.getAllValues().get(0), is(user.getEmail()));
        
        //Verification that the method calls the persist method and creates the correct notification
        assertThat(((Notification)captorNot.getAllValues().get(0)).getNotificatedUser(), is(invite.getUser()));
        assertThat(((Notification)captorNot.getAllValues().get(0)).getRelatedEvent(), is(invite.getEvent()));
        assertThat(((Notification)captorNot.getAllValues().get(0)).getType(), is(NotificationType.deletedEvent));
    }
    
    /**
     * Test of createDelayNotification method, of class NotificationManager.
     */
    @Test
    public void testCreateDelayNotification(){
        System.out.println("createDelayNotification");
        
        notificationManager.createDelayNotification(invite);
        
        //Capture the argument passed to the mocked class
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Notification> captorNot = ArgumentCaptor.forClass(Notification.class);
        
        verify(notificationManager.mailManager, times(1)).sendMail(captor.capture(), anyString(), anyString());
        verify(notificationManager.em, times(1)).persist(captorNot.capture());
        
        //Verification that the method trys to send the email to the correct address
        assertThat(captor.getAllValues().get(0), is(user.getEmail()));
        
        //Verification that the method calls the persist method and creates the correct notification
        assertThat(((Notification)captorNot.getAllValues().get(0)).getNotificatedUser(), is(invite.getUser()));
        assertThat(((Notification)captorNot.getAllValues().get(0)).getRelatedEvent(), is(invite.getEvent()));
        assertThat(((Notification)captorNot.getAllValues().get(0)).getType(), is(NotificationType.delayedEvent));
    }
    
    /**
     * Test of createWeatherConditionChangedNotification method, of class NotificationManager.
     */
    @Test
    public void testCreateWeatherConditionChangedNotification() throws Exception {
        System.out.println("createWeatherConfitionChangedNotification");
        
        notificationManager.createWeatherConditionChangedNotification(user, event);
        
        //Capture the argument passed to the mocked class
        ArgumentCaptor<Notification> captorNot = ArgumentCaptor.forClass(Notification.class);
        
        verify(notificationManager.em, times(1)).persist(captorNot.capture());

        //Verification that the method calls the persist method and creates the correct notification
        assertThat(((Notification)captorNot.getAllValues().get(0)).getNotificatedUser(), is(user));
        assertThat(((Notification)captorNot.getAllValues().get(0)).getRelatedEvent(), is(event));
        assertThat(((Notification)captorNot.getAllValues().get(0)).getType(), is(NotificationType.weatherConditionChanged));
    }
    
}
