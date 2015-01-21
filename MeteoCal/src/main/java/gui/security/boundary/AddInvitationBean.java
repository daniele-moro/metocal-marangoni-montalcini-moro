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
     * This method loads the users already invited to the event
     */
    @PostConstruct
    public void init(){
        temp();
    }
    
    /**
     * This method inserts in the list invitedPeople all the users already invited to the event
     */
    private void temp(){
        construct=true;
        FacesMessage errMessage;
        //I fetch the id passed in GET
        String param=FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        
        //I check if the id is passed to the page
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
        
        //I check if the event exists
        if(event==null ){
            errMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Event Error", "");
            construct=false;
            FacesContext.getCurrentInstance().addMessage(null, errMessage);
            return;
        }
        
        //I check if the logged user is the organizer
        if(!event.isOrganizer(eventManager.getLoggedUser())){
            errMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "You aren't the organizer of this event", "");
            construct=false;
            FacesContext.getCurrentInstance().addMessage(null, errMessage);
            return;
        }
        //I update the list of the invited people
        invitedPeople=eventManager.getInvitedPeople(idEvent);
    }
    
    /**
     * This method returns the name
     * @return 
     */
    public String getName() {
        return name;
    }
    
    /**
     * This method is used to assign to name the value of the parameter
     * @param name 
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * This method returns the surname
     * @return 
     */
    public String getSurname() {
        return surname;
    }
    
    /**
     * This method is used to assign to surname the value of the parameter
     * @param surname
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }
    
     /**
     * This method returns the email
     * @return 
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * This method is used to assign to email the value of the parameter
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * This method returns the list of the invited people
     * @return 
     */
    public List<Users> getInvitedPeople() {
        return this.invitedPeople;
    }
    
    /**
     * This method returns the list partialResult
     * @return 
     */
    public List<Users> getPartialResults() {
        return this.partialResult;
    }
    
    
    /**
     * This method calls a method of the searchManager in order to find the user whose email corresponds
     * to the attribute "email"; then calls another method in order to add the found user to the list 
     * of invited people. 
     */
    public void addUserThroughEmail() {
        Users u = searchManager.findUser(email);
        addUser(u);
    }
    
    /**
     * This method calls another method in order to add the user passed as parameter to the list 
     * of invited people.
     */
    public String addSelectedUser(Users u){
        addUser(u);
        partialResult= null;
        return "";
    }
    
    /**
     * This method calls an eventManager method in order to add the user passed as parameter to the list 
     * of invited people, then update the list of the invited people.
     */
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
    
    /**
     * This method updates the list partialResult by calling a method of the searchManager, which 
     * finds all the users whose name and surname correspond to the inserted ones; if no matches are found, it 
     * displays a message. 
     */
    public void addUserThroughNameSurname() {
        //I search the users throgh the inserted name and surname. 
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
