/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.boundary;

import business.security.entity.Event;
import business.security.entity.Group;
import business.security.entity.Invite;
import business.security.entity.Notification;
import business.security.entity.User;
import business.security.entity.WeatherCondition;
import business.security.object.NameSurnameEmail;
import java.beans.Statement;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class NotificationManager {

    @PersistenceContext
    EntityManager em;
    
    @Inject
    Principal principal;
    
    private Event event; 

    public void save(Notification notification) {
        notification.setRelatedEvent(findEvent(getEvent().getId()));
        em.persist(notification);
    }
    
   public void save(Invite invite) {
      invite.setEvent(findEvent(getEvent().getId()));
       em.persist(invite);
   }
   
   
   public Event findEvent(Long eventId) {
       Query qFindEventThrougId = em.createQuery("SELECT e FROM EVENT e WHERE e.id = " + eventId);
       List<Event> event = (List<Event>) qFindEventThrougId.getResultList(); 
       return event.get(0);
       
   }

    
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
    
}
