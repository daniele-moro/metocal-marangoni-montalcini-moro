/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.control;

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

    /**
     * This method stores the user passed as parameter in the database
     * @param user 
     */
    public void save(Users user) {
        user.setGroupName(Group.USERS);
        em.persist(user);
    }

    /**
     * This method return the logged user
     * @return 
     */
    public Users getLoggedUser() {
        return em.find(Users.class, principal.getName());
    }
    
    /**
     * This method executes the update of the user profile
     * @param usr 
     */
    public void updateUser(Users usr) {
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

    
}
