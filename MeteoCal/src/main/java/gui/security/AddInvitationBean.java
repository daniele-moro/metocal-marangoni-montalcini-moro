/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package gui.security;

import business.security.boundary.EventManager;
import business.security.boundary.NotificationManager;
import business.security.boundary.SearchManager;
import business.security.entity.Event;
import business.security.entity.User;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Named
@ViewScoped
public class AddInvitationBean implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private long idEvent;
    
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
    
    //Lista degli invitati
    List<User> invitedPeople;
    
    //lista dei risultati parziali
    List<User> partialResult;
    
    private Event event;
    
    /**
     * Metodo che precarica gli utenti invitati all'evento
     */
    @PostConstruct
    public void init(){
        temp();
    }
    
    private void temp(){
        //Prelevo l'id passato in GET
        idEvent = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id"));
        System.out.println("ID EVENTO "+this.idEvent);
        //Aggiorno la lista degli eventi
        invitedPeople=eventManager.getInvitedPeople(idEvent);
        event=eventManager.getEventById(idEvent);
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
        return this.invitedPeople;
    }
    
    public List<User> getPartialResults() {
        return this.partialResult;
    }
    
    
    
    public void addUserThroughEmail() {
        eventManager.addInvitation(email,idEvent);
        /*
        manca da controllare se l'invito è mandabile, e in caso di problemi visualizzare un messaggio
        FacesMessage message;
        message = new FacesMessage("No results","");
        FacesContext.getCurrentInstance().addMessage(null, message);*/
        //Prelevo dal db la lista degli invitati
        invitedPeople = eventManager.getInvitedPeople(idEvent);
    }
    
    public String addUser(User u){
        eventManager.addInvitation(u, idEvent);
        
        //Prelevo dal db la lista degli invitati
        invitedPeople = eventManager.getInvitedPeople(idEvent);
        partialResult= null;
        return"";
    }
    
    public void addUserThroughNameSurname() {
        //Cerco gli utenti per nome e cognome
        System.out.println("appena dentro add User");
        partialResult = searchManager.findUsersFromNameSurname(name, surname);
        if(partialResult == null || partialResult.isEmpty()){
            FacesMessage message;
            message = new FacesMessage("No results","");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
}
