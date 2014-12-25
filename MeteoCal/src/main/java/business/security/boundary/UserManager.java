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
    
    private String oldEmail; 

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
        // Bisogna impostare update on cascade se si vuole modificare la password: nel parametro 6, 
        // al posto di u.getEmail(), bisogna mettere oldEmail, che è il valore vecchio dell'email. 
        // Ma se non si imposta update on cascade, non si può fare la modifica dell'email 
        Query q = em.createQuery("UPDATE USER user SET user.name =?1, user.surname = ?2" + 
            //"', u.birthday = " + user.getBirthday() + 
            ", user.phoneNumber =?3, user.residenceTown =?4, user.email =?5 WHERE user.email =?6");
        q.setParameter(1, u.getName()); 
        q.setParameter(2, u.getSurname()); 
        q.setParameter(3, u.getPhoneNumber()); 
        q.setParameter(4, u.getResidenceTown()); 
        q.setParameter(5, u.getEmail()); 
        q.setParameter(6, u.getEmail()); 
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

    /**
     * @return the oldEmail
     */
    public String getOldEmail() {
        return oldEmail;
    }

    /**
     * @param oldEmail the oldEmail to set
     */
    public void setOldEmail(String oldEmail) {
        this.oldEmail = oldEmail;
    }
    
    
}
