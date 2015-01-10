/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.security;

import business.security.boundary.EventManager;
import business.security.object.Location;
import business.security.boundary.JsonPars;
import business.security.boundary.UserInformationLoader;
import business.security.entity.Event;
import business.security.entity.WeatherCondition;
import java.util.Date;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import org.codehaus.jettison.json.JSONException;

@Named
@RequestScoped
public class EventOperationBean {

    @EJB
    private EventManager eventManager;
    
    @EJB
    private UserInformationLoader userInformationLoader; 
    
    @EJB
    private JsonPars p;
    
    private Event event; 
    
    private WeatherCondition acceptedWeatherCondition;
    
    private WeatherCondition weatherForecast; 
    
    private Date currentDate = new Date();


    public EventOperationBean() {
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
        Location loc = p.parsingLatitudeLongitude(event.getLocation());
        event.setLatitude(loc.getLatitude());
        event.setLongitude(loc.getLongitude());
        System.out.println("" + event.getLongitude() + "    " + loc.getLongitude());
        weatherForecast = p.parsingWeather(event.getLatitude(), event.getLongitude(), event.getTimeStart());
        event.setWeatherForecast(weatherForecast);
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

    /**
     * @return the weatherForecast
     */
    public WeatherCondition getWeatherForecast() {
        return weatherForecast;
    }

    /**
     * @param weatherForecast the weatherForecast to set
     */
    public void setWeatherForecast(WeatherCondition weatherForecast) {
        this.weatherForecast = weatherForecast;
    }
    
    
    public Date getCurrentDate() {
        return currentDate;
    }

    

    
}
