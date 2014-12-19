/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.security;

import business.security.boundary.EventManager;
import business.security.entity.WeatherCondition;
import business.security.object.NameSurnameEmail;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Named
@RequestScoped
public class AddInvitationBean {
    
    @EJB
    private EventManager eventManager;

    private List<NameSurnameEmail> invitedPeople;
    
    @NotNull(message = "May not be empty")
    private String name; 
    
    @NotNull(message = "May not be empty")
    private String surname; 
    
    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message = "invalid email")
    private String email; 

    public AddInvitationBean() {
        invitedPeople = new ArrayList<>();
    }

    public List<NameSurnameEmail> getInvitedPeopleEmails() {
        if (invitedPeople == null) {
            invitedPeople = new ArrayList<>();
        }
        return invitedPeople;
    }

    public void setInvitedPeopleEmails(List<NameSurnameEmail> invitedPeople) {
        this.invitedPeople = invitedPeople;
    }
    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

   
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void addUserThroughEmail() {
        System.out.println("Dentro add user through email");
        if(email!=null) {
            invitedPeople.add(eventManager.findUser(email));  
        }
        System.out.println(invitedPeople.toString());
        
    }
    
    
    
    public void addUserThroughNameSurname() {
       
    }
    
}
