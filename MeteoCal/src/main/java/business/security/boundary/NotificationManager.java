
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
    
    private List<NameSurnameEmail> invitedPeople;
    
    private List<NameSurnameEmail> partialResults;
    
    public NotificationManager() {
        searchManager = new SearchManager(); 
        invitedPeople = new ArrayList<>(); 
        partialResults = new ArrayList<>(); 
    }
  
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
    
    public List<NameSurnameEmail> getPartialResults() {
        if (partialResults == null) {
            partialResults = new ArrayList<>();
        }
        return partialResults;
    }

    public void setPartialResults(List<NameSurnameEmail> partialResults) {
        this.partialResults = partialResults;
    }
  
    public List<NameSurnameEmail> getInvitedPeople() {
        if (invitedPeople == null) {
            invitedPeople = new ArrayList<>();
        }
        return invitedPeople;
    }

    public void setInvitedPeople(List<NameSurnameEmail> invitedPeople) {
        this.invitedPeople = invitedPeople;
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
    
    public void addInvitation(String email) {
        invitedPeople.add(searchManager.findNameSurnameEmailFromUser(email)); 
    }
    
    public void addInvitation(String name, String surname) {
        invitedPeople.add(searchManager.findNameEmailSurnameFromNameSurname(name, surname).get(0));
    }
    
    public void addInvitation(NameSurnameEmail element) {
        invitedPeople.add(element);
        partialResults = new ArrayList<>(); 
    }
    
      public void sendNotifications(NotificationType notificationType) {
        switch (notificationType) { 
            case invite:
                for(NameSurnameEmail element : invitedPeople) {
                    createInviteNotification(element); 
                    em.persist(getInvite());
                    em.persist(getNotification());
                    mailManager.sendMail(element.getEmail(), "New Invite", "Hi! You have received a new invite");
                }
                break;
            case delayedEvent:
                //MANCA DA CONSIDERARE LA PARTE: SE L'UTENTE HA SOVRAPPOSIZIONI CON ALTRI EVENTI
                for(Invite inv : searchManager.findInviteRelatedToAnEvent(event)) {
                    if(inv.getStatus() == Invite.InviteStatus.accepted || inv.getStatus() == Invite.InviteStatus.invited) {
                        createDelayNotification(inv);
                        em.persist(getNotification());
                        mailManager.sendMail(inv.getUser().getEmail(), "Event Date Changed", "Hi! An event for which you have received an invite has been modified: the date has been changed. Join MeteoCal to discover it.");
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
            setNotification(new Notification()); 
            getNotification().setType(NotificationType.invite);
            getNotification().setNotificatedUser(searchManager.findUser(element.getEmail()));
            getNotification().setRelatedEvent(event);
            getNotification().setSeen(false);
            getNotification().setGenerationDate(new Date());
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
    
    public void removeUser(NameSurnameEmail object) {
        invitedPeople.remove(object);
    }
}
