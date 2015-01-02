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
import business.security.entity.Invite;
import business.security.entity.User;
import business.security.entity.WeatherCondition;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

@Named
@RequestScoped
public class EventBean {

    @EJB
    private EventManager eventManager;
    
    @EJB
    private NotificationManager notificationManager; 
    
    @EJB
    private UserInformationLoader userInformationLoader; 
    
    private Event event; 
    
    private WeatherCondition acceptedWeatherCondition;
    

    public EventBean() {
    }

    public Event getEvent() {
        if (event == null) {
            event = new Event();
        }
        return event;
    }
    
    public Event getEventToModify() {
        return eventManager.getEvent();
    }
    
    public WeatherCondition getAcceptedWeatherConditionsToModify() {
        return eventManager.getAcceptedWeatherConditions();
    }
    
    public WeatherCondition getAcceptedWeatherCondition() {
        if (acceptedWeatherCondition == null) {
            acceptedWeatherCondition = new WeatherCondition();
        }
        return acceptedWeatherCondition;
    }
    
    
    
    public String createEvent() {
        eventManager.createEvent(event, acceptedWeatherCondition);
        return "addInvitation?faces-redirect=true";
    }
    
    public void createdEvent() {
        userInformationLoader.loadCreatedEvents();
    }
    
    
    public boolean checkDateConsistency() {
        return eventManager.checkDateConsistency(); 
    }

    /**
     * @param event the event to set
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * @param acceptedWeatherCondition the acceptedWeatherCondition to set
     */
    public void setAcceptedWeatherCondition(WeatherCondition acceptedWeatherCondition) {
        this.acceptedWeatherCondition = acceptedWeatherCondition;
    }
    
    public Event loadEventInformation() {
        return eventManager.getEvent();
    }
    
    public String acceptInvitation() {
        eventManager.addParticipantToEvent();
        return "event?faces-redirect=true";
    }
    
    public String refuseInvitation() {
        eventManager.removeParticipantFromEvent();
        return "event?faces-redirect=true";
    }
    
    public String reAcceptInvitation() {
        eventManager.addParticipantToEvent();
        return "event?faces-redirect=true";
    }
    
    public String deleteParticipation() {
        eventManager.removeParticipantFromEvent(); 
        return "event?faces-redirect=true";
    }
    
    public boolean getInvitationStatusInvited() {
        return userInformationLoader.isInviteStatusInvited();
    }
    
    public boolean getInvitationStatusRefused() {
        return userInformationLoader.isInviteStatusNotAccepted();
    }
    
    public boolean getInvitationStatusDelayedEvent() {
        return userInformationLoader.isInviteStatusDelayedEvent();
    }
    
    public boolean getInvitationStatusAccepted() {
        return userInformationLoader.isInviteStatusAccepted();
    }
    
    public boolean getEventDeleted() {
        return eventManager.isDeletedEvent();
    }
    

    

}
