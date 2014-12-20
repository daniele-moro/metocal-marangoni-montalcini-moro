/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.boundary;

import business.security.entity.Event;
import business.security.entity.Invite;
import business.security.entity.User;
import business.security.entity.WeatherCondition;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    
    private NotificationManager notificationManager; 
    
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
    
    
   public List<Event> findCreatedEvents() {
        Query qCreatedEvents = em.createQuery("SELECT e FROM EVENT e WHERE e.organizer.email = '" + getLoggedUser().getEmail() + "'");
        List<Event> createdEvents = (List<Event>) qCreatedEvents.getResultList(); 
        return createdEvents; 
   }
   
   public List<Event> findAcceptedEvents() {
       Query qAcceptedEvents = em.createQuery("SELECT e FROM EVENT e, INVITE i WHERE i.user.email = '" + getLoggedUser().getEmail() + "' AND i.event.id = e.id AND i.status = '" + Invite.InviteStatus.accepted + "'");
       List<Event> acceptedEvents = (List<Event>) qAcceptedEvents.getResultList(); 
       return acceptedEvents; 
   }
   
   public List<Event> findEventsWithNoAnswer() {
       Query qNoAnswerEvents = em.createQuery("SELECT e FROM EVENT e, INVITE i WHERE i.user.email = '" + getLoggedUser().getEmail() + "' AND i.event.id = e.id AND i.status = '" + Invite.InviteStatus.invited + "'");
       List<Event> noAnswerEvents = (List<Event>) qNoAnswerEvents.getResultList(); 
       return noAnswerEvents; 
   }
   
   public List<Event> findUserEvents() {
       List<Event> userEvents = new ArrayList<>();
       for(Event e : findCreatedEvents()) {
           userEvents.add(e); 
       }
        for (Event e : findAcceptedEvents()) {
            userEvents.add(e);
        }
       for(Event e : findEventsWithNoAnswer()) {
           userEvents.add(e); 
       }
      //TODO: va aggiunto un ordinamento qui
       return userEvents;
   }
   
 
  public boolean checkDateConsistency(Date dateStart, Date dateEnd) {
      if (dateStart.after(dateEnd)) {
          return false;
      } else {
          for(Event e : findUserEvents()) {
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

    

    
    
}
