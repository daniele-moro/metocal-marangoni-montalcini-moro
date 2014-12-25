/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.boundary;

import business.security.entity.Event;
import business.security.entity.Invite;
import business.security.entity.User;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class UserInformationLoader {

    @PersistenceContext
    EntityManager em;
    
    @Inject
    Principal principal;
    
  public UserInformationLoader() {
      
  }
    

    public User getLoggedUser() {
        System.out.println("ciao " + em);
        return em.find(User.class, principal.getName());
    }
    
    
   public List<Event> loadCreatedEvents() {
        Query qCreatedEvents = em.createQuery("SELECT e FROM EVENT e WHERE e.organizer.email =?1");
        qCreatedEvents.setParameter(1, getLoggedUser().getEmail());
        List<Event> createdEvents = (List<Event>) qCreatedEvents.getResultList(); 
        return createdEvents; 
   }
   
   public List<Event> loadAcceptedEvents() {
       Query qAcceptedEvents = em.createQuery("SELECT e FROM EVENT e, INVITE i WHERE i.user.email =?1 AND i.event.id = e.id AND i.status =?2");
       qAcceptedEvents.setParameter(1, getLoggedUser().getEmail());
       qAcceptedEvents.setParameter(2, Invite.InviteStatus.accepted);
       
       List<Event> acceptedEvents = (List<Event>) qAcceptedEvents.getResultList(); 
       return acceptedEvents; 
   }
   
   public List<Event> loadEventsWithNoAnswer() {
       Query qNoAnswerEvents = em.createQuery("SELECT e FROM EVENT e, INVITE i WHERE i.user.email =?1 AND i.event.id = e.id AND i.status =?2");
       qNoAnswerEvents.setParameter(1, getLoggedUser().getEmail());
       qNoAnswerEvents.setParameter(2, Invite.InviteStatus.invited);
       List<Event> noAnswerEvents = (List<Event>) qNoAnswerEvents.getResultList(); 
       return noAnswerEvents; 
   }
   
   public List<Event> loadEvent() {
       List<Event> userEvents = new ArrayList<>();
       for(Event e : loadCreatedEvents()) {
           userEvents.add(e); 
       }
        for (Event e : loadAcceptedEvents()) {
            userEvents.add(e);
        }
       for(Event e : loadEventsWithNoAnswer()) {
           userEvents.add(e); 
       }
      //TODO: va aggiunto un ordinamento qui
       return userEvents;
   }
   
 
 
}
