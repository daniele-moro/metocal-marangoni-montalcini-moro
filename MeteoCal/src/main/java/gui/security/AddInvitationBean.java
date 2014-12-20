/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.security;

import business.security.boundary.NotificationManager;
import business.security.boundary.SearchManager;
import business.security.object.NameSurnameEmail;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Named
@RequestScoped
public class AddInvitationBean {
    
    @EJB
    private NotificationManager notificationManager; 
    
    @EJB
    private SearchManager searchManager; 
    
    private List<NameSurnameEmail> invitedPeople;
    
    private List<NameSurnameEmail> partialResults;
    
    @NotNull(message = "May not be empty")
    private String name; 
    
    @NotNull(message = "May not be empty")
    private String surname; 
    
    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message = "invalid email")
    private String email; 

    public AddInvitationBean() {
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
    
    
    public List<NameSurnameEmail> getInvitedPeople() {
        return notificationManager.getInvitedPeople();
        
    }

    public void setInvitedPeople(List<NameSurnameEmail> invitedPeople) {
        this.invitedPeople = invitedPeople;
    }

    public List<NameSurnameEmail> getPartialResults() {
        return notificationManager.getPartialResults();
    }
    
    public void setPartialResults(List<NameSurnameEmail> partialResults) {
        this.partialResults = partialResults;
    }
    
    public String addUserThroughEmail() {
        System.out.println(email + "dentro addInvitationBeanaddUserThroughemail");
        notificationManager.addInvitation(searchManager.findNameSurnameEmailFromUser(email), searchManager.findUser(email));
        return "addInvitation"; 
    }
    
    
    
    public String addUserThroughNameSurname() {
        System.out.println("appena dentro add User");
        notificationManager.setPartialResults(searchManager.findNameEmailSurnameFromNameSurname(name, surname));
        if(notificationManager.getPartialResults().size() == 1) {
            notificationManager.addInvitation(searchManager.findNameEmailSurnameFromNameSurname(name, surname).get(0), searchManager.findUser(name, surname));
        } else {
            return "searchResults"; 
        }
        
        return "addInvitation";
        
        //TODO: azzerare le stringhe name e surname
        
    }

    
    public String sendInvitations() {
        notificationManager.sendNotifications();
        return "createdEvent";
    }

    
    
}
