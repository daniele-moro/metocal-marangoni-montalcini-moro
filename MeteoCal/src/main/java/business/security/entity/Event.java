/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.entity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "event")
@Entity(name = "EVENT")
public class Event implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotNull(message = "Name May not be empty")
    private String name;
    
    @NotNull(message = "Time Start May not be empty")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date timeStart;

    @NotNull(message = "Time End May not be empty")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date timeEnd;
     
    private String description;
    
    @NotNull(message = "Location May not be empty")
    private String location;
    
    private String latitude;
    
    private String longitude;
    
    @NotNull(message = "May not be empty")
    private boolean publicEvent;
    
    private String notPredefinedTypology;
    
    private boolean delayedEvent;
    
    private boolean deleted;
    
    @NotNull(message = "May not be empty")
    private Users organizer;
    
    @NotNull(message = "Typology May not be empty")
    private PredefinedTypology predefinedTypology;
    
    private boolean outdoor;
    
    
    private WeatherCondition acceptedWeatherConditions;
    
    private WeatherCondition weatherForecast;
    
    @XmlTransient
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public boolean isPublicEvent() {
        return publicEvent;
    }

    public void setPublicEvent(boolean publicEvent) {
        this.publicEvent = publicEvent;
    }

    public String getNotPredefinedTypology() {
        return notPredefinedTypology;
    }

    public void setNotPredefinedTypology(String notPredefinedTypology) {
        this.notPredefinedTypology = notPredefinedTypology;
    }

    public boolean isDelayedEvent() {
        return delayedEvent;
    }

    public void setDelayedEvent(boolean delayedEvent) {
        this.delayedEvent = delayedEvent;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @XmlTransient
    public Users getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Users organizer) {
        this.organizer = organizer;
    }
    
    public PredefinedTypology getPredefinedTypology() {
        return predefinedTypology;
    }

    public void setPredefinedTypology(PredefinedTypology predefinedTypology) {
        this.predefinedTypology = predefinedTypology;
    }

    public WeatherCondition getAcceptedWeatherConditions() {
        return acceptedWeatherConditions;
    }

    public void setAcceptedWeatherConditions(WeatherCondition acceptedWeatherConditions) {
        this.acceptedWeatherConditions = acceptedWeatherConditions;
    }

    @XmlTransient
    public WeatherCondition getWeatherForecast() {
        return weatherForecast;
    }

    public void setWeatherForecast(WeatherCondition weatherForecast) {
        this.weatherForecast = weatherForecast;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Event other = (Event) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
    public boolean isOrganizer(Users user){
        return this.organizer.equals(user);
    }
    
    /**
     * This method control if there is any overlap of period between the current object (this) and the parameter
     * @param ev
     * @return False if there isn't any overlap, True if there's an overlap
     */
    public boolean isOverlapped(Event ev){
        if(this.timeEnd==null || this.timeStart==null || ev.timeEnd==null || ev.timeStart==null)
            throw new IllegalArgumentException("Illegal Data");//Or return true

        if((this.getTimeStart().after(ev.getTimeStart()) && this.getTimeStart().before(ev.getTimeEnd()))
                || (this.getTimeEnd().after(ev.getTimeStart()) && this.getTimeEnd().before(ev.getTimeEnd()))
                || (this.getTimeStart().equals(ev.getTimeStart()) && this.getTimeEnd().equals(ev.getTimeEnd()))){
            return true;
        }

        if((ev.getTimeStart().after(this.getTimeStart()) && ev.getTimeStart().before(this.getTimeEnd()))
                || (ev.getTimeEnd().after(this.getTimeStart()) && ev.getTimeEnd().before(this.getTimeEnd()))
                || (ev.getTimeStart().equals(this.getTimeStart()) && ev.getTimeEnd().equals(this.getTimeEnd()))){
            return true;
        }    
        return false;
    }

    public boolean isOutdoor() {
        return outdoor;
    }

    public void setOutdoor(boolean outdoor) {
        this.outdoor = outdoor;
    }

    
    
}
