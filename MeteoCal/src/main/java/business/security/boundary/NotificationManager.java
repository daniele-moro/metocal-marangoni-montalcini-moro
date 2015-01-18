
package business.security.boundary;

import business.security.control.MailManager;
import business.security.entity.Event;
import business.security.entity.Invite;
import business.security.entity.Notification;
import business.security.entity.NotificationType;
import business.security.entity.Users;
import business.security.object.NameSurnameEmail;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class NotificationManager {

    @PersistenceContext
    EntityManager em;
    
    @Inject
    Principal principal;
    
    @EJB
    private MailManager mailManager; 
    
    private Invite invite; 
    
    private Notification notification; 

    public Invite getInvite() {
        return invite;
    }

    public void setInvite(Invite invite) {
        this.invite = invite;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
    
    /**
     * This method is called when a user is invited to an event: it create the invite and the notification 
     * and stores them in the database; moreover, it sends an email to the invited user
     * @param e
     * @param u 
     */
    public void createInviteNotification(Event e, Users u) {
            setInvite(new Invite()); 
            getInvite().setUser(u);
            getInvite().setStatus(Invite.InviteStatus.invited);
            getInvite().setEvent(e);
            em.persist(getInvite());
            setNotification(new Notification()); 
            getNotification().setType(NotificationType.invite);
            getNotification().setNotificatedUser(u);
            getNotification().setRelatedEvent(e);
            getNotification().setSeen(false);
            getNotification().setGenerationDate(new Date());
            em.persist(getNotification());
            mailManager.sendMail(u.getEmail(), "New Invite", "Hi! You have received a new invite");
    }
    
    /**
     * This method is called when an event is cancelled: it produces a delete notification, stores
     * it in the database and sends an email to the interested user
     * @param inv 
     */
    public void createDeleteNotification (Invite inv) {
        setNotification(new Notification()); 
        getNotification().setRelatedEvent(inv.getEvent());
        getNotification().setNotificatedUser(inv.getUser());
        getNotification().setSeen(false);
        getNotification().setType(NotificationType.deletedEvent);
        getNotification().setGenerationDate(new Date());
        em.persist(getNotification());
        //I send the notification mail to the user
        mailManager.sendMail(inv.getUser().getEmail(), "Deleted Event", "Hi! An event for which you have received an invite has been cancelled. Join MeteoCal to discover it.");
    }

    /**
     * This method is called when the date of an event is changed: it produces a delay notification, stores
     * it in the database and sends an email to the interested user
     * @param inv 
     */
    public void createDelayNotification (Invite inv) {
        setNotification(new Notification()); 
        getNotification().setRelatedEvent(inv.getEvent());
        getNotification().setNotificatedUser(inv.getUser());
        getNotification().setSeen(false);
        getNotification().setType(NotificationType.delayedEvent);
        getNotification().setGenerationDate(new Date());
        em.persist(getNotification());
        
        //Invio della mail
        mailManager.sendMail(inv.getUser().getEmail(), "Event Date Changed", "Hi! An event for which you have received an invite has been modified: the date has been changed. Join MeteoCal to discover it.");
    }
    
    public void createWeatherConditionChangedNotification (Users user, Event event) {
        setNotification(new Notification()); 
        getNotification().setGenerationDate(new Date());
        getNotification().setNotificatedUser(user);
        getNotification().setRelatedEvent(event);
        getNotification().setSeen(false);
        getNotification().setType(NotificationType.weatherConditionChanged);
        em.persist(getNotification()); 
        
    }

}
