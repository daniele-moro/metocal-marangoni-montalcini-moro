/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.boundary;

import business.security.entity.Event;
import business.security.entity.User;
import business.security.entity.WeatherCondition;
import java.security.Principal;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    
    private Event event; 
    
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
          for(Event e : userInformationLoader.loadCreatedEvents()) {
              if(dateStart.after(e.getTimeStart()) && dateStart.before(e.getTimeEnd()) || dateEnd.after(e.getTimeStart()) && dateEnd.before(e.getTimeEnd())) {
                  return false; 
              }
          }
          for(Event e : userInformationLoader.loadAcceptedEvents()) {
              if(dateStart.after(e.getTimeStart()) && dateStart.before(e.getTimeEnd()) || dateEnd.after(e.getTimeStart()) && dateEnd.before(e.getTimeEnd())) {
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
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    

    
    
}
