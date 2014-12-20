/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.boundary;

import business.security.entity.User;
import business.security.object.NameSurnameEmail;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class SearchManager {

    @PersistenceContext
    EntityManager em;
    
    @Inject
    Principal principal;
    
    public SearchManager() {
    }
   
   
   public User findUser(String email) {
       System.out.println(email + "dentro find User");
       System.out.println("" + em);
       Query qFindUser = em.createQuery("SELECT u FROM USER u WHERE u.email = '" + email + "'"); 
       System.out.println("ciao");
       //Query qFindUserThroughEmail = em.createQuery("SELECT u FROM USER u WHERE u.email = '" + email + "'");
       List <User> user = (List<User>) qFindUser.getResultList();
       return user.get(0);
   }
   
   public User findUser(String name, String surname) {
       System.out.println("" + em);
       Query qFindUser = em.createQuery("SELECT u FROM USER u WHERE u.name = '" + name + "' AND u.surname '" + surname + "'"); 
       System.out.println("ciao");
       List <User> users = (List<User>) qFindUser.getResultList();
       return users.get(0);
   }
   
   public NameSurnameEmail findNameSurnameEmailFromUser(String email) {
       System.out.println(email + "searchManager dentro find name surame email from user");
       User u = findUser(email);
       NameSurnameEmail nameSurnameEmail = new NameSurnameEmail();
       nameSurnameEmail.setNameSurnameEmail(u.getName(), u.getSurname(), u.getEmail());
       System.out.println(nameSurnameEmail.getName());
       return nameSurnameEmail;
   }
  
    public List<NameSurnameEmail> findNameEmailSurnameFromNameSurname(String name, String surname) {
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