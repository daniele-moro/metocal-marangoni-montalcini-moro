package business.security.boundary;

import business.security.entity.Event;
import business.security.entity.Invite;
import business.security.entity.Notification;
import business.security.entity.User;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class UserInformationLoader {
    
    @PersistenceContext
            EntityManager em;
    
    @Inject
            Principal principal;
    
    public User getLoggedUser() {
        System.out.println("ciao " + em);
        return em.find(User.class, principal.getName());
    }
    
    /**
     * This method searches in the database for the events created by the logged user (NOT deleted)
     * @return list of the events created by the logged user
     */
    public List<Event> loadCreatedEvents() {
        Query qCreatedEvents = em.createQuery("SELECT e FROM EVENT e WHERE e.organizer.email =?1 AND e.deleted=FALSE");
        qCreatedEvents.setParameter(1, getLoggedUser().getEmail());
        List<Event> createdEvents = (List<Event>) qCreatedEvents.getResultList();
        return createdEvents;
    }
    
    /**
     * This method searches in the database for the events accepted by the logged user (NOT deleted)
     * @return list of the events accepted by the logged user
     */
    public List<Event> loadAcceptedEvents() {
        Query qAcceptedEvents = em.createQuery("SELECT e FROM EVENT e, INVITE i WHERE i.user.email =?1 AND i.event.id = e.id AND i.status =?2 AND e.deleted=FALSE");
        qAcceptedEvents.setParameter(1, getLoggedUser().getEmail());
        qAcceptedEvents.setParameter(2, Invite.InviteStatus.accepted);
        
        List<Event> acceptedEvents = (List<Event>) qAcceptedEvents.getResultList();
        return acceptedEvents;
    }
    
    /**
     * This method searches in the database for the events to which the logged user has not already 
     * confirmed his participation 
     * @return list of the events to which the logged user has not already confirmed his participation 
     */
    public List<Event> loadEventsWithNoAnswer() {
        Query qNoAnswerEvents = em.createQuery("SELECT e FROM EVENT e, INVITE i WHERE i.user.email =?1 AND i.event.id = e.id AND i.status =?2");
        qNoAnswerEvents.setParameter(1, getLoggedUser().getEmail());
        qNoAnswerEvents.setParameter(2, Invite.InviteStatus.invited);
        List<Event> noAnswerEvents = (List<Event>) qNoAnswerEvents.getResultList();
        return noAnswerEvents;
    }
    
    /**
     * This method calls three other methods which find the events of the user (not deleted)
     * @return The events of the users
     */
    public List<Event> loadEvent() {
        List<Event> userEvents = new ArrayList<>();
        for(Event e : loadCreatedEvents()) {
            userEvents.add(e);
        }
        for (Event e : loadAcceptedEvents()) {
            userEvents.add(e);
        }
        for(Event e : loadEventsWithNoAnswer()) {
            userEvents.add(e);
        }
        //TODO: va aggiunto un ordinamento qui
        return userEvents;
    }
    
    /**
     * This method search in the database for the notifications sent to the logged user
     * @return list of the notifications sent to the logged user
     */
    public List<Notification> loadNotifications() {
        Query findUserNotifications = em.createQuery("SELECT n FROM NOTIFICATION n WHERE n.notificatedUser = ?1 ORDER BY n.generationDate DESC");
        findUserNotifications.setParameter(1, getLoggedUser());
        List<Notification> notifications = findUserNotifications.getResultList();
        return notifications;
    }
    
    /**
     * This method searches in the database for the invite sent to the logged user related to an event 
     * and return it
     * @param event
     * @return 
     */
    public Invite findInviteStatus(Event event) {
        Query findInvite = em.createQuery("SELECT i FROM INVITE i WHERE i.event =?1 AND i.user =?2");
        findInvite.setParameter(1, event);
        findInvite.setParameter(2, getLoggedUser());
        if(!findInvite.getResultList().isEmpty())
            return (Invite)findInvite.getResultList().get(0);
        else
            return null;
    }
    
    /**
     * This method is called when a notification is seen: it set the field "seen" as true
     * @param notification 
     */
    public void setNotificationSeen(Notification notification) {
        notification.setSeen(true);
        em.merge(notification);
    }
    
    /**
     * This method is called when a user decides to delete a notification: the notification
     * is deleted from the database. 
     * @param notification 
     */
    public void removeNotification(Notification notification) {
        Query findNotification = em.createQuery("SELECT n FROM NOTIFICATION n WHERE n.id =?1");
        findNotification.setParameter(1, notification.getId());
        Notification not = (Notification) findNotification.getResultList().get(0);
        em.remove(not);
    }
    
}
