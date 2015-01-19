
package business.security.control;

import business.security.entity.Event;
import business.security.entity.Invite;
import business.security.entity.Notification;
import business.security.entity.NotificationType;
import business.security.entity.Users;
import java.security.Principal;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class NotificationManager {
    
    @PersistenceContext
            EntityManager em;
    
    @Inject
            Principal principal;
    
    @EJB
            MailManager mailManager;
    
    /**
     * This method is called when a user is invited to an event: it first creates the invite and then the notification
     * and stores them in the database; moreover, it sends an email to the invited user
     * @param e
     * @param u
     */
    public void createInviteNotification(Event e, Users u) {
        Invite invite= new Invite();
        invite.setUser(u);
        invite.setStatus(Invite.InviteStatus.invited);
        invite.setEvent(e);
        em.persist(invite);
        Notification notification = new Notification();
        notification.setType(NotificationType.invite);
        notification.setNotificatedUser(u);
        notification.setRelatedEvent(e);
        notification.setSeen(false);
        notification.setGenerationDate(new Date());
        em.persist(notification);
        mailManager.sendMail(u.getEmail(), "New Invite", "Hi! You have received a new invite");
    }
    
    /**
     * This method is called when an event is cancelled: it produces a delete notification, stores
     * it in the database and sends an email to the interested user
     * @param inv
     */
    public void createDeleteNotification (Invite inv) {
        Notification notification = new Notification();
        notification.setRelatedEvent(inv.getEvent());
        notification.setNotificatedUser(inv.getUser());
        notification.setSeen(false);
        notification.setType(NotificationType.deletedEvent);
        notification.setGenerationDate(new Date());
        em.persist(notification);
        //I send the notification mail to the user
        mailManager.sendMail(inv.getUser().getEmail(), "Deleted Event", "Hi! An event for which you have received an invite has been cancelled. Join MeteoCal to discover it.");
    }
    
    /**
     * This method is called when the date of an event is changed: it produces a delay notification, stores
     * it in the database and sends an email to the interested user
     * @param inv
     */
    public void createDelayNotification (Invite inv) {
        Notification notification = new Notification();
        notification.setRelatedEvent(inv.getEvent());
        notification.setNotificatedUser(inv.getUser());
        notification.setSeen(false);
        notification.setType(NotificationType.delayedEvent);
        notification.setGenerationDate(new Date());
        em.persist(notification);
        
        //Invio della mail
        mailManager.sendMail(inv.getUser().getEmail(), "Event Date Changed", "Hi! An event for which you have received an invite has been modified: the date has been changed. Join MeteoCal to discover it.");
    }
    
    public void createWeatherConditionChangedNotification (Users user, Event event) {
        Notification notification = new Notification();
        notification.setGenerationDate(new Date());
        notification.setNotificatedUser(user);
        notification.setRelatedEvent(event);
        notification.setSeen(false);
        notification.setType(NotificationType.weatherConditionChanged);
        em.persist(notification);
        
    }
    
}
