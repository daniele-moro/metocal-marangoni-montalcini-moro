/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.boundary;

import business.security.entity.Event;
import business.security.entity.Invite;
import business.security.entity.User;
import business.security.object.NameSurnameEmail;
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

    public User findUser(String email) {
        Query qFindUser = em.createQuery("SELECT u FROM USER u WHERE u.email =?1");
        qFindUser.setParameter(1, email);
        List<User> users = (List<User>) qFindUser.getResultList();
        if (users.size() >= 1) {
            return users.get(0);
        } else {
            return null;
        }
    }

    public NameSurnameEmail findNameSurnameEmailFromUser(String email) {
        User u = findUser(email);
        NameSurnameEmail nameSurnameEmail = new NameSurnameEmail();
        nameSurnameEmail.setNameSurnameEmail(u.getName(), u.getSurname(), u.getEmail());
        System.out.println(nameSurnameEmail.getName());
        return nameSurnameEmail;
    }

    public List<User> findUsersFromNameSurname(String name, String surname) {
        Query qFindUser = em.createQuery("SELECT u FROM USER u WHERE u.name =?1 AND u.surname =?2");
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

    public List<Event> findUserEvent(User user) {
        List<Event> userEvents = new ArrayList<>();
        Query findUserAcceptedEvents = em.createQuery("SELECT invite.event FROM INVITE invite WHERE invite.user = ?1 AND invite.status =?2");
        findUserAcceptedEvents.setParameter(1, user);
        findUserAcceptedEvents.setParameter(2, Invite.InviteStatus.accepted);
        for (Event event : (List<Event>) findUserAcceptedEvents.getResultList()) {
            userEvents.add(event);
        }
        Query findUserCreatedEvents = em.createQuery("SELECT event FROM EVENT event WHERE event.organizer =?1");
        findUserCreatedEvents.setParameter(1, user);
        for (Event event : (List<Event>) findUserCreatedEvents.getResultList()) {
            userEvents.add(event);
        }
        return userEvents;
    }

    public List<Event> findAllEvents() {
        Query findAllEvents = em.createQuery("SELECT e FROM EVENT e");
        return findAllEvents.getResultList();
    }

    /**
     * 
     * @return All the events that aren't already occurred, null if there isn't any one
     */
    public List<Event> findAllFutureEvents() {
        Query findFutureEvents = em.createQuery("SELECT e FROM EVENT e WHERE e.timeStart >= ?1");
        findFutureEvents.setParameter(1, new Date());
        return findFutureEvents.getResultList();
    }
    
    /**
     * 
     * @return All the events that are already occurred, null if there isn't any one
     */
    public List<Event> findAllPastEvents() {
        Query findFutureEvents = em.createQuery("SELECT e FROM EVENT e WHERE e.timeStart < ?1");
        findFutureEvents.setParameter(1, new Date());
        return findFutureEvents.getResultList();
    }
}
