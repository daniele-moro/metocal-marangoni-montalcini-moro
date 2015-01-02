/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.security;

import business.security.boundary.EventManager;
import business.security.boundary.NotificationManager;
import business.security.boundary.UserInformationLoader;
import business.security.entity.Event;
import business.security.entity.Notification;
import business.security.entity.User;
import business.security.entity.WeatherCondition;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

@Named
@RequestScoped
public class NotificationBean {
        
    @EJB
    private UserInformationLoader userInformationLoader; 
    
    @EJB    
    private EventManager eventManager;

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
        eventManager.setEvent(notification.getRelatedEvent());
        eventManager.setDeletedEvent(notification.getRelatedEvent().isDeleted());
        userInformationLoader.findInviteStatus(notification.getRelatedEvent());
        return "event?faces-redirect=true";
    }
    
    public String showNotifications() {
        userInformationLoader.setNotificationsFound(userInformationLoader.loadNotifications().size() > 0);
        return "notifications?faces-redirect=true";
    }
    
    public String deleteNotification(Notification notification) {
        userInformationLoader.removeNotification(notification); 
        return "notifications?faces-redirect=true";
    }
    
    public String navigateTo() {
        return "home?faces-redirect=true";
    }

    
    
    
    
}
