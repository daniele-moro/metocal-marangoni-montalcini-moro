/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package business.security.control;

import business.security.entity.Event;
import business.security.entity.Invite;
import business.security.entity.Notification;
import business.security.entity.NotificationType;
import business.security.entity.Users;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class SearchManager {
    
    @PersistenceContext
            EntityManager em;
    
    @Inject
            Principal principal;
    
    public Users findUser(String email) {
        Query qFindUser = em.createQuery("SELECT u FROM USERS u WHERE u.email =?1");
        qFindUser.setParameter(1, email);
        List<Users> users = (List<Users>) qFindUser.getResultList();
        if (users.size() >= 1) {
            return users.get(0);
        } else {
            return null;
        }
    }
    
    public List<Users> findUsersFromNameSurname(String name, String surname) {
        Query qFindUser = em.createQuery("SELECT u FROM USERS u WHERE u.name =?1 AND u.surname =?2");
        qFindUser.setParameter(1, name);
        qFindUser.setParameter(2, surname);
        return qFindUser.getResultList();
    }
    
    public List<Invite> findInviteRelatedToAnEvent(Event event) {
        Query findInvites = em.createQuery("SELECT i from INVITE i WHERE i.event.id =?1");
        findInvites.setParameter(1, event.getId());
        List<Invite> invites = (List<Invite>) findInvites.getResultList();
        return invites;
    }
    
    public List<Event> findUserEvent(Users user) {
        //Prelevo gli eventi a cui partecipa l'utente
        List<Event> userEvents = new ArrayList<>();
        Query findUserAcceptedEvents = em.createQuery("SELECT invite.event FROM INVITE invite WHERE invite.user = ?1 AND invite.status =?2");
        findUserAcceptedEvents.setParameter(1, user);
        findUserAcceptedEvents.setParameter(2, Invite.InviteStatus.accepted);
        for (Event event : (List<Event>) findUserAcceptedEvents.getResultList()) {
            userEvents.add(event);
        }
        
        //Prelevo gli eventi di cui è organizzatore l'utente
        Query findUserCreatedEvents = em.createQuery("SELECT event FROM EVENT event WHERE event.organizer =?1 AND event.deleted = ?2");
        findUserCreatedEvents.setParameter(1, user);
        findUserCreatedEvents.setParameter(2, false);
        for (Event event : (List<Event>) findUserCreatedEvents.getResultList()) {
            userEvents.add(event);
        }
        
        return userEvents;
    }
    
    //TODO: REMOVE!!!!!!
    public List<Event> findAllEvents() {
        Query findAllEvents = em.createQuery("SELECT e FROM EVENT e");
        return findAllEvents.getResultList();
    }
    
    /**
     *
     * @return All the events that aren't already occurred and not deleted, null if there isn't any one
     */
    public List<Event> findAllFutureEvents() {
        Query findFutureEvents = em.createQuery("SELECT e FROM EVENT e WHERE e.timeStart >= ?1 AND e.deleted=FALSE");
        findFutureEvents.setParameter(1, new Date());
        return findFutureEvents.getResultList();
    }
    
    /**
     *
     * @return All the events that are already occurred and not deleted, null if there isn't any one
     */
    public List<Event> findAllPastEvents() {
        Query findFutureEvents = em.createQuery("SELECT e FROM EVENT e WHERE e.timeStart < ?1 AND e.deleted=FALSE");
        findFutureEvents.setParameter(1, new Date());
        return findFutureEvents.getResultList();
    }
    
    /**
     * This method verifies if for the organizer of an event there's a weather changed notification
     * @param event
     * @return 
     */
    public Notification existWeatherChangedNotification(Event event) {
        Query findWeatherChangedNotification = em.createQuery("SELECT n FROM NOTIFICATION n WHERE n.relatedevent =?1 AND n.type = ?2 AND n.notificatedUser = ?3");
        findWeatherChangedNotification.setParameter(1,event);
        findWeatherChangedNotification.setParameter(2,NotificationType.weatherConditionChanged);
        findWeatherChangedNotification.setParameter(3,event.getOrganizer());
        if(findWeatherChangedNotification.getResultList() != null && !findWeatherChangedNotification.getResultList().isEmpty()) {
            return (Notification) findWeatherChangedNotification.getResultList().get(0);
        }
        return null;
        
    }
    
    public List<Notification> findNotReadNotification(Users user) {
        Query findNotReadNotification = em.createQuery("SELECT n FROM NOTIFICATION n WHERE n.notificatedUser = ?1 AND n.seen = ?2");
        findNotReadNotification.setParameter(1,user);
        findNotReadNotification.setParameter(2,false);
        return findNotReadNotification.getResultList();
    }
}

