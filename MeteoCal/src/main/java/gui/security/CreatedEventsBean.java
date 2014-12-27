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
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

@Named
@RequestScoped
public class CreatedEventsBean {
    
    @EJB      
    private UserInformationLoader userInformationLoader;
    
    @EJB
    private EventManager eventManager; 
   
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
    
    public String showModifyEvent(Event event, WeatherCondition acceptedWeatherConditions) {
        eventManager.setEvent(event);
        eventManager.setAcceptedWeatherConditions(acceptedWeatherConditions);
        return "modifyEvent?faces-redirect=true";
    }
    
    public String modifyEventInformation() {
        eventManager.updateEventInformation();
        return "createdEvent?faces-redirect=true";
    }
    
    public String deleteEvent() {
        eventManager.removeEvent(); 
        return "createdEvent?faces-redirect=true";
    }
    
    public String addInvitations() {
        //eventManager.addInvitation(); 
        return "addInvitation?faces-redirect=true";
    }
    
    public String navigateTo() {
        return "home?faces-redirect=true"; 
    }

    
    
    
    


    
    
}
