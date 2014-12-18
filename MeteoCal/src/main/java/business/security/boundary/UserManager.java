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
    
    public void updateUser(User user) {
        System.out.println(user.getName());
        Query q = em.createQuery("UPDATE USER u SET u.name = '" + user.getName() + 
                "', u.surname = '" + user.getSurname() + 
                //"', u.birthday = " + user.getBirthday() + 
                "', u.phoneNumber = ' " + user.getPhoneNumber() + 
                "', u.residenceTown = '" + user.getResidenceTown() +
                "', u.email = '" + user.getEmail() + 
                "' WHERE u.email = '" +user.getEmail() + "'");
        q.executeUpdate();
    }
    
}
