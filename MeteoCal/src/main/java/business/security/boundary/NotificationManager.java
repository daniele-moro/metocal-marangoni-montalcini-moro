/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.boundary;

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
    
    private SearchManager searchManager; 
    
    private Event event; 
    
    private Invite invite; 
    
    private Notification notification; 
    
    private List<User> users; 
    
    private List<NameSurnameEmail> invitedPeople;
    
    private List<NameSurnameEmail> partialResults;
    
    public NotificationManager() {
        users = new ArrayList<>();
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
    
     
    public void addInvitation(NameSurnameEmail element, User u) {
        System.out.println("nel notificaiton manager" + em);
        invitedPeople.add(element);
        users.add(u); 
        
    }
    
      public void sendNotifications() {
        createInviteNotifications(); 
        em.persist(getInvite());
        em.persist(getNotification());
    }
    
    public void createInviteNotifications() {
        for(int i = 0; i < invitedPeople.size(); i++) {
            setInvite(new Invite()); 
            getInvite().setUser(users.get(i));
            getInvite().setStatus(Invite.InviteStatus.invited);
            getInvite().setEvent(event);
            setNotification(new Notification()); 
            getNotification().setType(NotificationType.invite);
            getNotification().setNotificatedUser(users.get(i));
            getNotification().setRelatedEvent(event);
            getNotification().setSeen(false);
            getNotification().setGenerationDate(new Date());
        }   
    }
}
