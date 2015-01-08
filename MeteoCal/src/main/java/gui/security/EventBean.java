/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.security;

import business.security.boundary.EventManager;
import business.security.boundary.Location;
import business.security.boundary.JsonPars;
import business.security.boundary.UserInformationLoader;
import business.security.entity.Event;
import business.security.entity.WeatherCondition;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import org.codehaus.jettison.json.JSONException;

@Named
@RequestScoped
public class EventBean {

    @EJB
    private EventManager eventManager;
    
    @EJB
    private UserInformationLoader userInformationLoader; 
    
    @EJB
    private JsonPars p;
    
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
    
    public WeatherCondition getAcceptedWeatherConditionsToModify() {
        return eventManager.getAcceptedWeatherConditions();
    }
    
    public WeatherCondition getAcceptedWeatherCondition() {
        if (acceptedWeatherCondition == null) {
            acceptedWeatherCondition = new WeatherCondition();
        }
        return acceptedWeatherCondition;
    }
    
    public String createEvent() throws JSONException {
        String location = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("geocomplete");
        event.setLocation(location);
        //change backspace with %20 for the request
        String replace = event.getLocation().replace(" ", "%20");
        Location loc = p.parsingLatitudeLongitude(replace);
        event.setLatitude(loc.getLatitude());
        event.setLongitude(loc.getLongitude());
        eventManager.createEvent(event, acceptedWeatherCondition);
        return "addInvitation?faces-redirect=true&amp;includeViewParams=true&amp;id="+event.getId();
    }
    
    public void createdEvent() {
        userInformationLoader.loadCreatedEvents();
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
    
    public boolean getEventDeleted() {
        return eventManager.isDeletedEvent();
    }
    
    

    
}
