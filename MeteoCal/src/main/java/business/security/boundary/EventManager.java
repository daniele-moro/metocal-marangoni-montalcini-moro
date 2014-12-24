/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.boundary;

import business.security.entity.Event;
import business.security.entity.NotificationType;
import business.security.entity.User;
import business.security.entity.WeatherCondition;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class EventManager {

    @PersistenceContext
    EntityManager em;
    
    @Inject
    Principal principal;
    
    @EJB
    private NotificationManager notificationManager;
    
    @EJB
    private UserInformationLoader userInformationLoader; 
    
    private Event e; 
    
    private WeatherCondition acceptedWeatherConditions; 
    
    public EventManager() {
        notificationManager = new NotificationManager();
    }
    

    public void createEvent(Event event) {
        getNotificationManager().setEvent(event);
        em.persist(event);
    }
    
    public void save(WeatherCondition weatherCondition) {
        em.persist(weatherCondition);
    }

    public void deleteEvent() {
        em.remove(getLoggedUser());
    }

    public User getLoggedUser() {
        return em.find(User.class, principal.getName());
    }

   
 
 public boolean checkDateConsistency(Date dateStart, Date dateEnd) {
      if (dateStart.after(dateEnd)) {
          return false;
      } else {
          for(Event ev : userInformationLoader.loadCreatedEvents()) {
              if(dateStart.after(ev.getTimeStart()) && dateStart.before(ev.getTimeEnd()) || dateEnd.after(ev.getTimeStart()) && dateEnd.before(ev.getTimeEnd())) {
                  return false; 
              }
          }
          for(Event ev : userInformationLoader.loadAcceptedEvents()) {
              if(dateStart.after(ev.getTimeStart()) && dateStart.before(ev.getTimeEnd()) || dateEnd.after(ev.getTimeStart()) && dateEnd.before(ev.getTimeEnd())) {
                  return false; 
              }
          }
      }
      return true; 
  }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public Event getEvent() {
        return e;
    }

    public void setEvent(Event event) {
        this.e = event;
    }

    public WeatherCondition getAcceptedWeatherConditions() {
        return acceptedWeatherConditions;
    }

    public void setAcceptedWeatherConditions(WeatherCondition acceptedWeatherConditions) {
        this.acceptedWeatherConditions = acceptedWeatherConditions;
    }
    
    public void removeEvent() {
        Query setEventDeleted = em.createQuery("UPDATE EVENT event SET event.deleted = true WHERE event.id = " + e.getId());
        setEventDeleted.executeUpdate();
        notificationManager.setEvent(e);
        notificationManager.sendNotifications(NotificationType.deletedEvent);
    }
    
    public void updateEventInformation() {
        /*Query findWeatherCondition = em.createQuery("SELECT w from WeatherCondition w WHERE w.precipitation = " + acceptedWeatherConditions.getPrecipitation() + ", w.wind = " + acceptedWeatherConditions.getWind() + ", w.temperature = " + acceptedWeatherConditions.getTemperature());   
        if(((List<WeatherCondition>) findWeatherCondition.getResultList()).isEmpty()) {
            em.persist(acceptedWeatherConditions);
            e.setAcceptedWeatherConditions(acceptedWeatherConditions);
        }*/
         
        System.out.println("dentro updateEventInformation" + e.getName());
        Query updateEventInformation = em.createQuery ("UPDATE EVENT e SET e.name = '" + e.getName() + "', e.town = '" + e.getTown() + "', e.address = '" + e.getAddress() + "', "
                + "e.description = '" + e.getDescription() + "' WHERE e.id = " + e.getId()); 
        updateEventInformation.executeUpdate();
        
        //if the date is changed
        
        /*notificationManager.setEvent(e);
        notificationManager.sendNotifications(NotificationType.delayedEvent);*/
    }

    

    
    
}
