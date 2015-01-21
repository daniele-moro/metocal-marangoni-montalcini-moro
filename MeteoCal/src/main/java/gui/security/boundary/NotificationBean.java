/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package gui.security.boundary;

import business.security.control.EventManager;
import business.security.control.SearchManager;
import business.security.control.UserInformationLoader;
import business.security.entity.Event;
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
    
    @EJB
    private SearchManager searchManager;
    
    private Event event;
    
    /**
     * This method calls a method of the userInformationLoader which returns the list of the 
     * logged user's notifications.
     * @return 
     */
    public List<Notification> getNotification() {
        return userInformationLoader.loadNotifications();
    }
    
    /**
     * This method sets the event to the event the notification is related to, then redirects
     * the user to the page in which he can see all the event information
     * @param notification
     * @return 
     */
    public String showEventRelatedToNotification(Notification notification) {
        userInformationLoader.setNotificationSeen(notification);
        this.setEvent(notification.getRelatedEvent());
        return "event?faces-redirect=true&amp;id="+event.getId();
    }
    
    /**
     * This method calls the userInformationLoader method which performs the delete of the selected
     * notification
     * @param notification
     * @return 
     */
    public String deleteNotification(Notification notification) {
        userInformationLoader.removeNotification(notification);
        return "notifications?faces-redirect=true";
    }
    
    /**
     * It returns the event
     * @return 
     */
    public Event getEvent() {
        return event;
    }
    
    /**
     * It sets the event to the event passed as parameter
     * @param event 
     */
    public void setEvent(Event event) {
        this.event = event;
    }
    
    /**
     * It returns true if there is at least one logged users's not read notification 
     * @return 
     */
    public boolean notReadNotification () {
        return !searchManager.findNotReadNotification(userInformationLoader.getLoggedUser()).isEmpty();
        
    }
}
