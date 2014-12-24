/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.security;

import business.security.boundary.UserManager;
import business.security.control.MailManager;
import business.security.entity.User;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

@Named
@RequestScoped
public class RegistrationBean {

    @EJB
    private UserManager um;
    
    @EJB
    private MailManager mailManager; 

    private User user;

    public RegistrationBean() {
    }

    public User getUser() {
        if (user == null) {
            user = new User();
        }
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }

    public String register() {
        um.save(user);
        mailManager.sendMail(user.getEmail(), "Registration to MeteoCal", "Welcome in Meteocal! Your registration has been succesfully completed.");
        return "login?faces-redirect=true";
    }

}
