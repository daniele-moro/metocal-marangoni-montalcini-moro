/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.security;

import business.security.boundary.EventManager;
import business.security.boundary.SearchManager;
import business.security.boundary.UserInformationLoader;
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
        //Prelevo l'id passato in GET
        long idEvent = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id"));
        System.out.println("ID EVENTO " + idEvent);
        //Aggiorno la lista degli eventi
        setEvent(eventManager.getEventById(idEvent));
        /**
         * Se l'utente loggato è il creatore devo caricare la lista degli: -
         * utenti che hanno accettato la partecipazione - utenti che hanno
         * rifiutato la partecipazione - utenti che devono ancora rispondere
         */
        accepted = eventManager.getAcceptedPeople(event);
        if (event.getOrganizer().equals(eventManager.getLoggedUser())) {
            creator = true;
            if(searchManager.existWeatherChangedNotification(event)){
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

    public void acceptInvitation() {
        try {
            eventManager.addParticipantToEvent(event);
        } catch (DateConsistencyException ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void refuseInvitation() {
        eventManager.removeParticipantFromEvent(event);
        //return "event?faces-redirect=true";
    }

    public void deleteParticipation() {
        eventManager.removeParticipantFromEvent(event);
        //return "event?faces-redirect=true";
    }

    public boolean getFindInviteStatusInvited() {
        Invite inv = userInformationLoader.findInviteStatus(event);
        if (inv == null) {
            return false;
        }
        return inv.getStatus() == Invite.InviteStatus.invited;
    }

    public boolean getFindInviteStatusAccepted() {
        Invite inv = userInformationLoader.findInviteStatus(event);
        if (inv == null) {
            return false;
        }
        return inv.getStatus() == Invite.InviteStatus.accepted;
    }

    public boolean getFindInviteStatusNotAccepted() {
        Invite inv = userInformationLoader.findInviteStatus(event);
        if (inv == null) {
            return false;
        }
        return inv.getStatus() == Invite.InviteStatus.notAccepted;
    }

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
