/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package gui.security.boundary;

import business.security.control.EventManager;
import business.security.control.JsonPars;
import business.security.object.Location;
import business.security.control.UserInformationLoader;
import business.security.entity.Event;
import business.security.entity.WeatherCondition;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.codehaus.jettison.json.JSONException;

@Named
@SessionScoped
public class CreatedEventsBean implements Serializable {
    
    @EJB
    private UserInformationLoader userInformationLoader;
    
    @EJB
    private EventManager eventManager;
    
    @EJB
    private JsonPars p;
    
    private Event event;
    
    private WeatherCondition acceptedWeatherCondition;
    
    private Date currentDate = new Date();

    /**
     * This method calls a method of the userInformationLoader, which finds all events created by 
     * the logged user.
     * @return 
     */
    public List<Event> getCreatedEvents() {
        return userInformationLoader.loadCreatedEvents();
    }
  
    /**
     * This method, starting from the inserted data, sets event location, latitude and longitude and 
     * calls the eventManager method "updateEventInformation". 
     * @return 
     */
    public String modifyEventInformation(){
        String location = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("geocomplete");
        //Control if the location is valid (not null or not empty)
        if(location ==null || location.isEmpty() ){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Location empty","");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return "";
        }
        
        //Parse of the location in GPS coordinate
        Location loc;
        try {
            loc = p.parsingLatitudeLongitude(location);
        } catch (JSONException ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Location invalid","");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return "";
        }
        event.setLocation(location);
        event.setLatitude(loc.getLatitude());
        event.setLongitude(loc.getLongitude());
        
        try {
            eventManager.updateEventInformation(event, acceptedWeatherCondition);
        } catch (Exception ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            return "";
        }
        return "createdEvent?faces-redirect=true";
    }
    
    /**
     * This method calls a method of the event manager which removes the event (the event is not cancelled
     * by the database, but all the invites related to it are removed and a deleteEvent notification is sent
     * to the interested users).
     * @return 
     */
    public String deleteEvent() {
        eventManager.removeEvent(event);
        return "createdEvent?faces-redirect=true";
    }
    
    /**
     * This method is called when the user clicks on button "Add Invitation": through this method, the user
     * is redirected to the Add Invitation page
     * @return 
     */
    public String addInvitations() {
        return "addInvitation?faces-redirect=true&amp;id="+event.getId();
    }
    

    /**
     * This method redirects the user to the page which allows him to update information
     * of the event he wants to modify
     * @param event
     * @param acceptedWeatherConditions
     * @return 
     */
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
    
    /**
     * This method returns the current date.
     * @return 
     */
    public Date getCurrentDate() {
        return currentDate;
    }
    /**
     * This methods redirects to the event page
     * @param e
     * @return 
     */
    public String showEvent(Event e){
        return "event.xhtml?faces-redirect=true&amp;id="+e.getId();
    }

}
