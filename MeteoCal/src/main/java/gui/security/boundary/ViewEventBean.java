/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.security.boundary;

import business.security.control.EventManager;
import business.security.control.SearchManager;
import business.security.control.UserInformationLoader;
import business.security.entity.Event;
import business.security.entity.Invite;
import business.security.entity.Users;
import exception.DateConsistencyException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author m-daniele
 */
@Named
@ViewScoped
public class ViewEventBean implements Serializable {

    private Event event;
    private boolean creator;
    
    private Date suggestedDate;

    @EJB
    private EventManager eventManager;
    
    @EJB
    private SearchManager searchManager;

    @EJB
    private UserInformationLoader userInformationLoader;
    
    private boolean badWeatherConditions; 
    private List<Users> accepted;
    private List<Users> refused;
    private List<Users> pendent;

    @PostConstruct
    public void init() {
        temp();
    }

    private void temp() {
        //I fetch the id passed in GET
        long idEvent = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id"));
        System.out.println("ID EVENTO " + idEvent);
        //I update the list of the people who have accepted, refused, not already answered to the invitation
        setEvent(eventManager.getEventById(idEvent));
        /**
         * If the logged user is the event creator, it is necessary to load the list of the users
         * who have accepted the invitation for the event, the list of the users who have not already
         * answered to the invitation for the event, the list of users who have refused the invitation 
         * for the event
         */
        accepted = eventManager.getAcceptedPeople(event);
        if (event.getOrganizer().equals(eventManager.getLoggedUser())) {
            creator = true;
            if(searchManager.existWeatherChangedNotification(event) != null){
                badWeatherConditions=true;
                suggestedDate =eventManager.suggestNewDate(event);
            }
            accepted = eventManager.getAcceptedPeople(event);
            refused = eventManager.getRefusedPeople(event);
            pendent = eventManager.getPendentPeople(event);
        } else {
            badWeatherConditions = false;
            creator = false;
            refused = null;
            pendent = null;
        }
    }
    
    /**
     * It redirects the user to the page in which he can see his profile
     * @param u
     * @return 
     */
    public String viewProfile(Users u) {
        return "userProfile?faces-redirect=true&amp;email=" + u.getEmail();
    }

    /**
     * @return the event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(Event event) {
        this.event = event;
    }
    
    /**
     * It is called when a user clicks on button "Accept Invitation": it calls a method of the eventManager, 
     * which adds the user to the list of the people who have accepted the invitation.
     */
    public void acceptInvitation() {
        try {
            eventManager.addParticipantToEvent(event);
        } catch (DateConsistencyException ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    /**
     * It is called when a user clicks on button "Refuse Invitation": it calls a method of the eventManager, 
     * which removes the user from the list of the people who have accepted the invitation.
     */
    public void refuseInvitation() {
        eventManager.removeParticipantFromEvent(event);
    }

    /**
     * It is called when a user, who has already accepted the invitation for an event, 
     * clicks on button "DeleteParticipation": it calls a method of the eventManager, 
     * which removes the user from the list of the people who have accepted the invitation.
     */
    public void deleteParticipation() {
        eventManager.removeParticipantFromEvent(event);
    }
    
    /**
     * This methods returns true if the logged user has an invitation for the event
     * passed as parameter and the invite status is "invited"
     * @return 
     */
    public boolean getFindInviteStatusInvited() {
        Invite inv = userInformationLoader.findInviteStatus(event);
        if (inv == null) {
            return false;
        }
        return inv.getStatus() == Invite.InviteStatus.invited;
    }

    /**
     * This methods returns true if the logged user has an invitation for the event
     * passed as parameter and the invite status is "accepted"
     * @return 
     */
    public boolean getFindInviteStatusAccepted() {
        Invite inv = userInformationLoader.findInviteStatus(event);
        if (inv == null) {
            return false;
        }
        return inv.getStatus() == Invite.InviteStatus.accepted;
    }

    /**
     * This methods returns true if the logged user has an invitation for the event
     * passed as parameter and the invite status is "notAccepted"
     * @return 
     */
    public boolean getFindInviteStatusNotAccepted() {
        Invite inv = userInformationLoader.findInviteStatus(event);
        if (inv == null) {
            return false;
        }
        return inv.getStatus() == Invite.InviteStatus.notAccepted;
    }
    
    /**
     * This methods returns true if the logged user has an invitation for the event
     * passed as parameter and the invite status is "delayedEvent"
     * @return 
     */
    public boolean getFindInviteStatusDelayed() {
        Invite inv = userInformationLoader.findInviteStatus(event);
        if (inv == null) {
            return false;
        }
        return inv.getStatus() == Invite.InviteStatus.delayedEvent;
    }

    /**
     * @return the accepted
     */
    public List<Users> getAccepted() {
        return accepted;
    }

    /**
     * @return the refused
     */
    public List<Users> getRefused() {
        return refused;
    }

    /**
     * @return the pendent
     */
    public List<Users> getPendent() {
        return pendent;
    }

    /**
     * @return the creator
     */
    public boolean isCreator() {
        return creator;
    }

    /**
     * @return the badWeatherConditions
     */
    public boolean isBadWeatherConditions() {
        return badWeatherConditions;
    }

    /**
     * @return the suggestedDate
     */
    public Date getSuggestedDate() {
        return suggestedDate;
    }

}
