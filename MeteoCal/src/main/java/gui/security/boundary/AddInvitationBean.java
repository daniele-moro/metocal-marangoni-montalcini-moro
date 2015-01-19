/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package gui.security.boundary;

import business.security.control.EventManager;
import business.security.control.NotificationManager;
import business.security.control.SearchManager;
import business.security.entity.Event;
import business.security.entity.Users;
import exception.InviteException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private List<Users> invitedPeople;
    
    //lista dei risultati parziali
    private List<Users> partialResult;
    
    private boolean construct;
    
    private Event event;
    
    
    /**
     * Metodo che precarica gli utenti invitati all'evento
     */
    @PostConstruct
    public void init(){
        temp();
    }
    
    private void temp(){
        construct=true;
        FacesMessage errMessage;
        //Prelevo l'id passato in GET
        String param=FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        
        //Controllo se viene passato l'id dell'evento alla pagina
        if(param==null){
            errMessage=new FacesMessage(FacesMessage.SEVERITY_ERROR, "No event selected", "");
            FacesContext.getCurrentInstance().addMessage(null, errMessage);
            construct=false;
            return;
        }
        idEvent = Long.parseLong(param);
        System.out.println("ID EVENTO "+this.idEvent);
        //Carico l'evento solo per verificare che l'evento esista
        event = eventManager.getEventById(idEvent);
        
        //controllo se l'evento esiste
        if(event==null ){
            errMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Event Error", "");
            construct=false;
            FacesContext.getCurrentInstance().addMessage(null, errMessage);
            return;
        }
        
        //Controllo se l'organizzatore è l'utente loggato
        if(!event.isOrganizer(eventManager.getLoggedUser())){
            errMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "You aren't the organizer of this event", "");
            construct=false;
            FacesContext.getCurrentInstance().addMessage(null, errMessage);
            return;
        }
        //Aggiorno la lista degli eventi
        invitedPeople=eventManager.getInvitedPeople(idEvent);
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
    
    public List<Users> getInvitedPeople() {
        return this.invitedPeople;
    }
    
    public List<Users> getPartialResults() {
        return this.partialResult;
    }
    
    
    
    public void addUserThroughEmail() {
        Users u = searchManager.findUser(email);
        addUser(u);
    }
    
    public String addSelectedUser(Users u){
        addUser(u);
        partialResult= null;
        return "";
    }
    
    private void addUser(Users user){
        try {
            eventManager.addInvitation(user, event);
            invitedPeople = eventManager.getInvitedPeople(idEvent);
        } catch (InviteException ex) {
            FacesMessage message;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR,"ERROR",ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
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
    
    /**
     * @return the construct
     */
    public boolean isConstruct() {
        return construct;
    }
}
