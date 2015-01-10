/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package gui.security;

import business.security.boundary.EventManager;
import business.security.boundary.JsonPars;
import business.security.object.Location;
import business.security.boundary.UserInformationLoader;
import business.security.entity.Event;
import business.security.entity.WeatherCondition;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
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
    
    public String modifyEventInformation() throws JSONException {
        String location = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("geocomplete");
        event.setLocation(location);
        //change backspace with %20
        String replace = event.getLocation().replace(" ", "%20");
        Location loc = p.parsingLatitudeLongitude(replace);
        event.setLatitude(loc.getLatitude());
        event.setLongitude(loc.getLongitude());
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
    
    public Date getCurrentDate() {
        return currentDate;
    }
    
    
    
    
    

    public String showEvent(Event e){
        return "event.xhtml?faces-redirect=true&amp;id="+e.getId();
    }

}
