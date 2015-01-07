/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.security;

import business.security.boundary.EventManager;
import business.security.boundary.UserInformationLoader;
import business.security.entity.Event;
import business.security.entity.Invite;
import business.security.entity.Notification;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

@Named
@SessionScoped
public class NotificationBean implements Serializable{
        
    @EJB
    private UserInformationLoader userInformationLoader; 
    
    @EJB    
    private EventManager eventManager;
    
    private Event event;

    public NotificationBean() {
    }

    public UserInformationLoader getUserInformationLoader() {
        return userInformationLoader;
    }
    
    public void setUserInformationLoader(UserInformationLoader userInformationLoader) {
        this.userInformationLoader = userInformationLoader;
    }
    
    public List<Notification> getNotification() {
        return userInformationLoader.loadNotifications(); 
    }
    
    public EventManager getEventManager() {
        return eventManager;
    }
    
    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }
    
    public boolean getIsNotificationsFound() {
        return userInformationLoader.isNotificationsFound();
    }
    
    public String showEventRelatedToNotification(Notification notification) {
        userInformationLoader.setNotificationSeen(notification); 
        //eventManager.setEvent(notification.getRelatedEvent());
        //eventManager.setDeletedEvent(notification.getRelatedEvent().isDeleted());
        this.setEvent(notification.getRelatedEvent());
        return "event?faces-redirect=true";
    }
    
    public boolean getFindInviteStatusInvited() {
        return userInformationLoader.findInviteStatus(event).getStatus()== Invite.InviteStatus.invited;
    }
    
     public boolean getFindInviteStatusAccepted() {
        return userInformationLoader.findInviteStatus(event).getStatus() == Invite.InviteStatus.accepted;
    }
      public boolean getFindInviteStatusNotAccepted() {
        return userInformationLoader.findInviteStatus(event).getStatus() == Invite.InviteStatus.notAccepted;
    }
       public boolean getFindInviteStatusDelayed() {
        return userInformationLoader.findInviteStatus(event).getStatus() == Invite.InviteStatus.delayedEvent;
    }
    public String showNotifications() {
        return "notifications?faces-redirect=true";
    }
    
    public String deleteNotification(Notification notification) {
        userInformationLoader.removeNotification(notification); 
        return "notifications?faces-redirect=true";
    }
    
    public String navigateTo() {
        return "home?faces-redirect=true";
    }

    /**
     * @return the event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    
    
    
    
}
