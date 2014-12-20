/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.security;

import business.security.boundary.EventManager;
import business.security.boundary.NotificationManager;
import business.security.entity.Invite;
import business.security.entity.Notification;
import business.security.entity.NotificationType;
import business.security.object.NameSurnameEmail;
import java.util.ArrayList;
import java.util.Date;
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
    private EventManager eventManager;
    
    @EJB
    private NotificationManager notificationManager; 

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
        invitedPeople = new ArrayList<>();
    }

    public List<NameSurnameEmail> getInvitedPeople() {
        if (invitedPeople == null) {
            invitedPeople = new ArrayList<>();
        }
        return invitedPeople;
    }

    public void setInvitedPeople(List<NameSurnameEmail> invitedPeople) {
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
        invitedPeople.add(eventManager.findNameSurnameEmailFromUser(email)); 
        System.out.println(invitedPeople.get(0) + "fewafw");
        System.out.println(eventManager.findNameSurnameEmailFromUser(email));
        email = ""; 
        System.out.println(email + "ee");
        
    }
    
    
    
    public String addUserThroughNameSurname() {
        System.out.println("appena dentro add User");
        partialResults = eventManager.findUser(name, surname);
        if(partialResults.size() == 1) {
            invitedPeople.add(partialResults.get(0)); 
        } else {
            return "searchResults"; 
        } 
        //TODO: azzerare le stringhe name e surname
        return "addInvitation"; 
        
    }

    public List<NameSurnameEmail> getPartialResults() {
        return partialResults;
    }

    public void setPartialResults(List<NameSurnameEmail> partialResults) {
        this.partialResults = partialResults;
    }
    
    public String sendInvitations() {
        System.out.println(invitedPeople.get(0) + "dentro a send invitation");
        for (NameSurnameEmail element : invitedPeople) {
            
            Invite invite = new Invite(); 
            invite.setUser(eventManager.findUser(element.getEmail()));
            invite.setStatus(Invite.InviteStatus.invited);
            Notification notification = new Notification(); 
            notification.setType(NotificationType.invite);
            notification.setNotificatedUser(eventManager.findUser(element.getEmail()));
            notification.setSeen(false);
            notification.setGenerationDate(new Date());
            notificationManager.sendNotification(invite, notification); 
        }
        return "createdEvent";
    }
    
    
}
