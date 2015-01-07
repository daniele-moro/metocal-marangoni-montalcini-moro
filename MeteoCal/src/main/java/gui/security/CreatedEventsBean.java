/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package gui.security;

import business.security.boundary.EventManager;
import business.security.boundary.UserInformationLoader;
import business.security.entity.Event;
import business.security.entity.WeatherCondition;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

@Named
@SessionScoped
public class CreatedEventsBean implements Serializable {
    
    @EJB
    private UserInformationLoader userInformationLoader;
    
    @EJB
    private EventManager eventManager;
    
    private Event event;
    
    private WeatherCondition acceptedWeatherCondition;
    
    
    public CreatedEventsBean() {
    }
    
    public UserInformationLoader getUserInformationLoader() {
        return userInformationLoader;
    }
    
    public void setUserInformationLoader(UserInformationLoader userInformationLoader) {
        this.userInformationLoader = userInformationLoader;
    }
    
    public EventManager getEventManager() {
        return eventManager;
    }
    
    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }
    
    public List<Event> getCreatedEvents() {
        return userInformationLoader.loadCreatedEvents();
    }
    
    /*  public String showModifyEvent(Event event, WeatherCondition acceptedWeatherConditions) {
    //eventManager.setEvent(event);
    //eventManager.setAcceptedWeatherConditions(acceptedWeatherConditions);
    return "modifyEvent?faces-redirect=true&amp;id"+event.getId();
    }*/
    
    public String modifyEventInformation() {
        eventManager.updateEventInformation(event, acceptedWeatherCondition);
        return "createdEvent?faces-redirect=true";
    }
    
    public String deleteEvent() {
        eventManager.removeEvent(event);
        return "createdEvent?faces-redirect=true";
    }
    
    public String addInvitations() {
        return "addInvitation?faces-redirect=true&amp;id="+event.getId();
    }
    
    public String navigateTo() {
        return "home?faces-redirect=true";
    }
    
    public String showModifyEvent(Event event, WeatherCondition acceptedWeatherConditions) {
        this.event=event;
        this.acceptedWeatherCondition=acceptedWeatherConditions;
        return "modifyEvent?faces-redirect=true&amp;id"+event.getId();
    }
    
    /**
     * @return the event
     */
    public Event getEventToModify() {
        return event;
    }
    
    /**
     * @param event the event to set
     */
    public void setEvent(Event event) {
        this.event = event;
    }
    
    /**
     * @return the acceptedWeatherCondition
     */
    public WeatherCondition getAcceptedWeatherConditionToModify() {
        return acceptedWeatherCondition;
    }
    
    /**
     * @param acceptedWeatherCondition the acceptedWeatherCondition to set
     */
    public void setAcceptedWeatherCondition(WeatherCondition acceptedWeatherCondition) {
        this.acceptedWeatherCondition = acceptedWeatherCondition;
    }
    
    
    
    
    
    
    
    
}
