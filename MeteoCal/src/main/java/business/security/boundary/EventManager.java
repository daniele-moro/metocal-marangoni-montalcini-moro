/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.boundary;

import business.security.entity.Event;
import business.security.entity.Group;
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
public class EventManager {

    @PersistenceContext
    EntityManager em;
    
    @Inject
    Principal principal;

    public void save(Event event) {
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
    
   public void findCreatedEvent() {
        Query qCreatedEvent = em.createQuery("SELECT e.name FROM EVENT e WHERE e.organizer.email = '" + getLoggedUser().getEmail() + "'");
        List<String> lista = new ArrayList<String>(); 
        lista = qCreatedEvent.getResultList();
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
}
