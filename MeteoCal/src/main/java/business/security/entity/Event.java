/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.entity;

import java.io.Serializable;
import java.util.Date;
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
    
    @NotNull(message = "May not be empty")
    private String name;
    
    @NotNull(message = "May not be empty")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date timeStart;

    @NotNull(message = "May not be empty")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date timeEnd;
     
    private String description;
    
    @NotNull(message = "May not be empty")
    private String location;
    
    private String latitude;
    
    private String longitude;
    
    @NotNull(message = "May not be empty")
    private boolean publicEvent;
    
    private String notPredefinedTypology;
    
    private boolean delayedEvent;
    
    private boolean deleted;
    
    @NotNull(message = "May not be empty")
    private User organizer;
    
    @NotNull(message = "May not be empty")
    private PredefinedTypology predefinedTypology;
    
    //@NotNull(message = "May not be empty")
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
    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
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

    
}
