/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.security;

import business.security.boundary.UserManager;
import business.security.entity.User;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

@Named
@RequestScoped
public class UserBean{

    @EJB
    UserManager um;
    
    private User user; 
    
    public UserBean() {
    }
    
    public String getName() {
        return um.getLoggedUser().getName();
    }
    
    public User getUser() {
        return um.getLoggedUser();
    }
    
    public String modifyProfile () {
        um.setU(um.getLoggedUser());
        um.setOldEmail(um.getLoggedUser().getEmail());
        return "modifyProfile?faces-redirect=true";
    }
    
}
