/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package gui.security;

import business.security.boundary.EventManager;
import business.security.boundary.NotificationManager;
import business.security.boundary.SearchManager;
import business.security.entity.User;
import business.security.object.NameSurnameEmail;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedProperty;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Named
@SessionScoped
public class AddInvitationBean implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @ManagedProperty(value = "#{param.eventID}")
    private String eventID;
    
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
    
    List<User> invitedPeople;
    List<User> partialResult;
    
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
    
    public List<User> getInvitedPeople() {
        //return eventManager.getInvitedPeople();
        return this.invitedPeople;
        
    }
    
    public List<User> getPartialResults() {
        //return eventManager.getPartialResults();
        return this.partialResult;
    }
    
    
    
    public String addUserThroughEmail() {
        String idEvent = FacesContext.getCurrentInstance().
                getExternalContext().getRequestParameterMap().get("idEvent");
        System.out.println(idEvent);
        System.out.println(email + " dentro addInvitationBeanaddUserThroughemail");
        eventManager.addInvitation(email, Integer.parseInt(idEvent));
        
        //Prelevo dal db la lista degli invitati
        invitedPeople= eventManager.getInvitedPeople(Integer.parseInt(idEvent));
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
            
            //Prelevo dal db la lista degli invitati
            invitedPeople= eventManager.getInvitedPeople(Integer.parseInt(idEvent));
        }
        return "addInvitation?faces-redirect=true&amp;id="+idEvent;
        
        //TODO: azzerare le stringhe name e surname
        
    }
    
    public String navigateTo() {
        invitedPeople=null;
        name=null;
        surname=null;
        email=null;
        return "home?faces-redirect=true";
    }
    
    
    
}
