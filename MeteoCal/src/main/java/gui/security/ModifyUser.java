/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.security;

import business.security.boundary.UserManager;
import business.security.entity.User;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class ModifyUser {
    
    @EJB
    private UserManager um;
    
    private User user; 
    
    public User getUser() {
        if (user == null) {
            user = new User();
        }
        return user; 
    }
    
    public void setUser(User u) {
        user = u; 
    }
    
    public String updateProfile () {
        um.updateUser(user);
        return "home";
    }
}
