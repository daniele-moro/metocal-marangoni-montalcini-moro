/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.security;

import business.security.boundary.NotificationManager;
import business.security.boundary.SearchManager;
import business.security.control.MailManager;
import business.security.object.NameSurnameEmail;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Named
@RequestScoped
public class SuggestRegistrationBean {
    
    @EJB
    private MailManager mailManager; 
    
    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message = "invalid email")
    private String email; 

    public SuggestRegistrationBean() {
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public MailManager getMailManager() {
        return mailManager;
    }

    public void setMailManager(MailManager mailManager) {
        this.mailManager = mailManager;
    }
    
    public String sendSuggestion() {
        mailManager.sendMail(email, "Register yourself in MeteoCal!", "da definire, bisogna mettere il link della registration");
        email = "";
        return "suggestRegistration?faces-redirect=true"; 
    }
    
   
    
}
