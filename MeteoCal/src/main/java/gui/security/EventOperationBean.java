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
import javax.faces.application.FacesMessage;
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
        //Control of the input of the location
        String location = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("geocomplete");
        //Control if the location is valid (not null or not empty)
        if(location ==null || location.isEmpty() ){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Location empty","");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return "";
        }
        
        event.setAcceptedWeatherConditions(acceptedWeatherCondition);
        event.setLocation(location);
        
        //Parse of the location in GPS coordinate
        Location loc;
        try{
        loc = p.parsingLatitudeLongitude(event.getLocation());
        }catch(JSONException ex){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Location invalid","");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return "";
        }
        event.setLocation(location);
        event.setLatitude(loc.getLatitude());
        event.setLongitude(loc.getLongitude());
        
        System.out.println("" + event.getLongitude() + "    " + loc.getLatitude());
        
        try {
            eventManager.createEvent(event);
        } catch (Exception ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            return "";
        }
        
        return "addInvitation?faces-redirect=true&amp;id="+event.getId();
        
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
