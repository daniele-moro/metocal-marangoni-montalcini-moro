
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
    
      public void sendNotifications() {
        for(NameSurnameEmail element : invitedPeople) {
            createInviteNotifications(element); 
            em.persist(getInvite());
            em.persist(getNotification());
            mailManager.sendMail(element.getEmail(), "New Invite", "Hi! You have received a new invite");
        }
    }
    
    public void createInviteNotifications(NameSurnameEmail element) {
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

    public MailManager getMailManager() {
        return mailManager;
    }

    public void setMailManager(MailManager mailManager) {
        this.mailManager = mailManager;
    }
}
