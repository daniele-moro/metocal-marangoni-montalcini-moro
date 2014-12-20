/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.boundary;

import business.security.entity.Event;
import business.security.entity.Group;
import business.security.entity.Invite;
import business.security.entity.User;
import business.security.entity.WeatherCondition;
import business.security.object.NameSurnameEmail;
import java.beans.Statement;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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

    public void createEvent(Event event) {
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
    
    public void updateUser(User user) {
        System.out.println(user.getName());
        Query qUpdateUser = em.createQuery("UPDATE USER u SET u.name = '" + user.getName() + 
                "', u.surname = '" + user.getSurname() + 
                //"', u.birthday = " + user.getBirthday() + 
                "', u.phoneNumber = ' " + user.getPhoneNumber() + 
                "', u.residenceTown = '" + user.getResidenceTown() +
                "', u.email = '" + user.getEmail() + 
                "' WHERE u.email = '" +user.getEmail() + "'");
        qUpdateUser.executeUpdate();
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
   
   public User findUser(String email) {
       NameSurnameEmail nameSurnameEmail = new NameSurnameEmail();
       Query qFindUserThroughEmail = em.createQuery("SELECT u FROM USER u WHERE u.email = '" + email +"'");
       List <User> user = (List<User>) qFindUserThroughEmail.getResultList();
       return user.get(0);
   }
   
   public NameSurnameEmail findNameSurnameEmailFromUser(String email) {
       User u = findUser(email);
       NameSurnameEmail nameSurnameEmail = new NameSurnameEmail();
       nameSurnameEmail.setNameSurnameEmail(u.getName(), u.getSurname(), u.getEmail());
       return nameSurnameEmail;
   }
  
    public List<NameSurnameEmail> findUser(String name, String surname) {
       Query qFindUserEmail = em.createQuery("SELECT u.email FROM USER u WHERE u.name = '" + name + "' AND u.surname = '" + surname + "'"); 
       List<String> emails;
       List<NameSurnameEmail> list = new ArrayList<>(); 
       emails = (List<String>) qFindUserEmail.getResultList(); 
       for (String email : emails) {
           NameSurnameEmail element = new NameSurnameEmail(); 
           element.setEmail(email);
           element.setName(name);
           element.setSurname(surname);
           list.add(element); 
       }
       return list; 
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
}
