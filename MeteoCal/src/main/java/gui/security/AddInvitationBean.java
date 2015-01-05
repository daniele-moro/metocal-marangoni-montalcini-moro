/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.security;

import business.security.boundary.EventManager;
import business.security.boundary.NotificationManager;
import business.security.boundary.SearchManager;
import business.security.object.NameSurnameEmail;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Named
@RequestScoped
public class AddInvitationBean {
    
    @EJB
    private NotificationManager notificationManager; 
    
    @EJB
    private SearchManager searchManager; 
    
    @EJB
    private EventManager eventManager;
    
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
        return eventManager.getInvitedPeople();
        
    }

    public List<NameSurnameEmail> getPartialResults() {
        return eventManager.getPartialResults();
    }
    
    public String addUserThroughEmail() {
        String idEvent = FacesContext.getCurrentInstance().
		getExternalContext().getRequestParameterMap().get("idEvent");
        System.out.println(idEvent);
        System.out.println(email + " dentro addInvitationBeanaddUserThroughemail");
        eventManager.addInvitation(email, Integer.parseInt(idEvent));
        return "addInvitation?faces-redirect=true&amp;id="+idEvent; 
    }
    
    public String addUser(NameSurnameEmail element) {
        eventManager.addInvitation(element);
        return "addInvitation?faces-redirect=true";
    }
    
    public String addUserThroughNameSurname() {
        
        String idEvent = FacesContext.getCurrentInstance().
		getExternalContext().getRequestParameterMap().get("idEvent");
        
        System.out.println("appena dentro add User");
        eventManager.setPartialResults(searchManager.findNameEmailSurnameFromNameSurname(name, surname));
        if(eventManager.getPartialResults().size() == 1) {
            eventManager.getPartialResults().remove(0);
            eventManager.addInvitation(name, surname, Integer.parseInt(idEvent));
        }         
        return "addInvitation?faces-redirect=true&amp;id="+idEvent;
        
        //TODO: azzerare le stringhe name e surname
        
    }
    
    public String navigateTo() {
        return "home?faces-redirect=true";
    }

    
    
}
