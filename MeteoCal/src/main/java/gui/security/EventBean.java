/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.security;

import business.security.boundary.EventManager;
import business.security.boundary.NotificationManager;
import business.security.boundary.UserManager;
import business.security.entity.Event;
import business.security.entity.PredefinedTypology;
import business.security.entity.User;
import business.security.entity.WeatherCondition;
import java.util.List;
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
    
    public void setEvent(Event event) {
        this.event = event;
    }
    
    public WeatherCondition getAcceptedWeatherCondition() {
        if (acceptedWeatherCondition == null) {
            acceptedWeatherCondition = new WeatherCondition();
        }
        return acceptedWeatherCondition;
    }
    
    public void setAcceptedWeatherCondition(WeatherCondition acceptedWeatherCondition) {
        this.acceptedWeatherCondition = acceptedWeatherCondition;
    }
    
    
    public String createEvent() {
        event.setOrganizer(eventManager.getLoggedUser());
        eventManager.save(acceptedWeatherCondition);
        event.setAcceptedWeatherConditions(acceptedWeatherCondition);
        eventManager.save(event);
        notificationManager.setEvent(event); 
        return "addInvitation";
    }
    
    public void createdEvent() {
        eventManager.findCreatedEvent();
    }

    

}
