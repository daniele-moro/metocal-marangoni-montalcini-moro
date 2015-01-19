/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.boundary;

import business.security.entity.Group;
import business.security.entity.Users;
import java.security.Principal;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class UserManager {

    @PersistenceContext
    EntityManager em;
    
    @Inject
    Principal principal;

    public void save(Users user) {
        user.setGroupName(Group.USERS);
        em.persist(user);
    }

    public void unregister() {
        em.remove(getLoggedUser());
    }

    public Users getLoggedUser() {
        return em.find(Users.class, principal.getName());
    }
    
    public void updateUser(Users usr) {
        // Bisogna impostare update on cascade se si vuole modificare la password: nel parametro 6, 
        // al posto di u.getEmail(), bisogna mettere oldEmail, che è il valore vecchio dell'email. 
        // Ma se non si imposta update on cascade, non si può fare la modifica dell'email 
        Query q = em.createQuery("UPDATE USERS user SET user.name =?1, user.surname = ?2, user.birthday =?3, user.phoneNumber =?4, user.residenceTown =?5, user.calendarPublic =?6 WHERE user.email =?7");
        q.setParameter(1, usr.getName()); 
        q.setParameter(2, usr.getSurname()); 
        q.setParameter(3, usr.getBirthday()); 
        q.setParameter(4, usr.getPhoneNumber()); 
        q.setParameter(5, usr.getResidenceTown()); 
        q.setParameter(6, usr.isCalendarPublic()); 
        q.setParameter(7, usr.getEmail()); 
        q.executeUpdate();
    }
    
    public void checkFields() {
        
    }
    
}
