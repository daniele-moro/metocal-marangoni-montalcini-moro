/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.boundary;

import business.security.entity.Group;
import business.security.entity.User;
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
    
    private User u; 

    public void save(User user) {
        user.setGroupName(Group.USER);
        em.persist(user);
    }

    public void unregister() {
        em.remove(getLoggedUser());
    }

    public User getLoggedUser() {
        return em.find(User.class, principal.getName());
    }
    
    public void updateUser() {
        System.out.println(u.getName());
        Query q = em.createQuery("UPDATE USER u SET u.name = '" + u.getName() + 
                "', u.surname = '" + u.getSurname() + 
                //"', u.birthday = " + user.getBirthday() + 
                "', u.phoneNumber = ' " + u.getPhoneNumber() + 
                "', u.residenceTown = '" + u.getResidenceTown() +
                "', u.email = '" + u.getEmail() + 
                "' WHERE u.email = '" +u.getEmail() + "'");
        q.executeUpdate();
    }

    /**
     * @return the u
     */
    public User getU() {
        return u;
    }

    /**
     * @param u the u to set
     */
    public void setU(User u) {
        this.u = u;
    }
    
    
}
