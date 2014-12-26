
package business.security.boundary;

import business.security.control.MailManager;
import business.security.entity.Event;
import business.security.entity.Invite;
import business.security.entity.Notification;
import business.security.entity.NotificationType;
import business.security.entity.User;
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
    private SearchManager searchManager; 
    
    @EJB
    private MailManager mailManager; 
    
    private Event event; 
    
    private Invite invite; 
    
    private Notification notification; 
    
    public NotificationManager() {
        searchManager = new SearchManager(); 
    }
  
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
    
    
    public SearchManager getSearchManager() {
        return searchManager;
    }

    public void setSearchManager(SearchManager searchManager) {
        this.searchManager = searchManager;
    }

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
    
    
    
      public void sendNotifications(NotificationType notificationType) {
        switch (notificationType) { 
            case invite:
                break;
            case delayedEvent:
                //MANCA DA CONSIDERARE LA PARTE: SE L'UTENTE HA SOVRAPPOSIZIONI CON ALTRI EVENTI
                for(Invite inv : searchManager.findInviteRelatedToAnEvent(event)) {
                    if(inv.getStatus() == Invite.InviteStatus.accepted || inv.getStatus() == Invite.InviteStatus.invited) {
                        createDelayNotification(inv);
                        em.persist(getNotification());
                        mailManager.sendMail(inv.getUser().getEmail(), "Event Date Changed", "Hi! An event for which you have received an invite has been modified: the date has been changed. Join MeteoCal to discover it.");
                        for(Event e : searchManager.findUserEvent(inv.getUser())) {
                            if(event.getTimeStart().after(e.getTimeStart()) && event.getTimeStart().before(e.getTimeEnd()) || event.getTimeEnd().after(e.getTimeStart()) && event.getTimeEnd().before(e.getTimeEnd())) {
                                Query updateInvitationStatus = em.createQuery("UPDATE INVITE invite SET invite.status= ?1 WHERE invite.event = ?2 AND invite.user = ?3"); 
                                updateInvitationStatus.setParameter(1, Invite.InviteStatus.delayedEvent);
                                updateInvitationStatus.setParameter(2, event);
                                updateInvitationStatus.setParameter(3, inv.getUser());
                                updateInvitationStatus.executeUpdate();
                                mailManager.sendMail(inv.getUser().getEmail(), "Overlapping Events", "Hi! An event for which you have received an invite has been modified: the date has been changed. According to the new date, "
                                        + "the event is overlapping respect to an event to which you are going to participate. So, now you are not considered among the participants of the event of which the date has been modified. "
                                        + "If you want to participate to this event, you have to delete your participation to the other event and accept another time the invitation to this one.");
                                break; 
                            }
                        }
                    }
                }
                break;
            case deletedEvent:
                for(Invite inv : searchManager.findInviteRelatedToAnEvent(event)) {
                    if(inv.getStatus() == Invite.InviteStatus.accepted || inv.getStatus() == Invite.InviteStatus.invited) {
                        createDeleteNotification(inv);
                        em.persist(getNotification());
                        mailManager.sendMail(inv.getUser().getEmail(), "Deleted Event", "Hi! An event for which you have received an invite has been cancelled. Join MeteoCal to discover it.");
                        em.remove(inv);
                    }
                }
                break;
            case weatherConditionChanged:
                break;
            default:
                throw new AssertionError(notificationType.name());
            
        }
    }

    
    public void createInviteNotification(NameSurnameEmail element) {
            setInvite(new Invite()); 
            getInvite().setUser(searchManager.findUser(element.getEmail()));
            getInvite().setStatus(Invite.InviteStatus.invited);
            getInvite().setEvent(event);
            em.persist(getInvite());
            setNotification(new Notification()); 
            getNotification().setType(NotificationType.invite);
            getNotification().setNotificatedUser(searchManager.findUser(element.getEmail()));
            getNotification().setRelatedEvent(event);
            getNotification().setSeen(false);
            getNotification().setGenerationDate(new Date());
            em.persist(getNotification());
            mailManager.sendMail(element.getEmail(), "New Invite", "Hi! You have received a new invite");
    }
    
    public void createDeleteNotification (Invite inv) {
        setNotification(new Notification()); 
        getNotification().setRelatedEvent(inv.getEvent());
        getNotification().setNotificatedUser(inv.getUser());
        getNotification().setSeen(false);
        getNotification().setType(NotificationType.deletedEvent);
        getNotification().setGenerationDate(new Date());
    }
    
    public void createDelayNotification (Invite inv) {
        setNotification(new Notification()); 
        getNotification().setRelatedEvent(inv.getEvent());
        getNotification().setNotificatedUser(inv.getUser());
        getNotification().setSeen(false);
        getNotification().setType(NotificationType.delayedEvent);
        getNotification().setGenerationDate(new Date());
        
    }

    public MailManager getMailManager() {
        return mailManager;
    }

    public void setMailManager(MailManager mailManager) {
        this.mailManager = mailManager;
    }
    
}
