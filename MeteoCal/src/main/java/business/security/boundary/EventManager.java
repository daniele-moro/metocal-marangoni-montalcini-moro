package business.security.boundary;

import business.security.control.MailManager;
import business.security.entity.Event;
import business.security.entity.Invite;
import business.security.entity.User;
import business.security.entity.WeatherCondition;
import business.security.object.NameSurnameEmail;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class EventManager {
    
    @PersistenceContext
            EntityManager em;
    
    @Inject
            Principal principal;
    
    @EJB
    private NotificationManager notificationManager;
    
    @EJB
    private UserInformationLoader userInformationLoader;
    
    @EJB
    private SearchManager searchManager;
    
    @EJB
    private MailManager mailManager; 
    
    private boolean deletedEvent = false;
    
    private List<NameSurnameEmail> partialResults;
    
    private WeatherCondition acceptedWeatherConditions;
    
    public EventManager() {
        acceptedWeatherConditions = new WeatherCondition();
        partialResults = new ArrayList<>();
        notificationManager = new NotificationManager();
    }
    
    /**
     * Creation of a new Event
     * @param event Event to add
     * @param awc Accepted weather conditions for the event
     * @param wf Weather forecasts for the event
     */
    public void createEvent(Event event, WeatherCondition awc) {
        event.setOrganizer(getLoggedUser());
        System.out.println("" + awc.getPrecipitation() + " " + awc.getTemperature() + " " + awc.getWind() );
        save(awc);
        save(event.getWeatherForecast());
        event.setAcceptedWeatherConditions(awc);
        em.persist(event);
        // notificationManager.setEvent(event);
    }
    
    /**
     * Persist of the weather condition
     * @param weatherCondition 
     */
    public void save(WeatherCondition weatherCondition) {
        em.persist(weatherCondition);
    }
    /**
     * It finds the logged user
     * @return the logged user
     */
    public User getLoggedUser() {
        return em.find(User.class, principal.getName());
    }
    
    /**
     * This method checks the consistency of the interval of the start and end dates, e.g., 
     * if the event with this type of time interval can be created without causing any overlaps with other events
     * @param start Start of the event
     * @param end End of the event
     * @return true: no overlaps would be created; false: overlaps would be created
     */
    public boolean checkDateConsistency(Date start, Date end) {
        if (start.after((end))) {
            return false;
        } else {
            for(Event ev : userInformationLoader.loadCreatedEvents()) {
                if(start.after(ev.getTimeStart()) && start.before(ev.getTimeEnd())
                        || end.after(ev.getTimeStart()) && end.before(ev.getTimeEnd())
                        || start.equals(ev.getTimeStart()) && end.equals(ev.getTimeEnd())) {
                    return false;
                }
            }
            for(Event ev : userInformationLoader.loadAcceptedEvents()) {
                if(start.after(ev.getTimeStart()) && start.before(ev.getTimeEnd())
                        || end.after(ev.getTimeStart()) && end.before(ev.getTimeEnd())
                        || start.equals(ev.getTimeStart()) && end.equals(ev.getTimeEnd())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Overload of the previous method, passing an event instead of date start and date end
     * @param event Event to control
     * @return
     */
    public boolean checkDateConsistency(Event event){
        return checkDateConsistency(event.getTimeStart(), event.getTimeEnd());
    }

    
    public List<NameSurnameEmail> getPartialResults() {
        if (partialResults == null) {
            partialResults = new ArrayList<>();
        }
        return partialResults;
    }
    
    public void setPartialResults(List<NameSurnameEmail> partialResults) {
        this.partialResults = partialResults;
    }
    
    public List<User> getInvitedPeople(long idEvent) {
        Query findInvitedPeopleThroughIDev = em.createQuery("SELECT u FROM INVITE i, USER u WHERE i.event.id = ?1 AND i.user.email = u.email");
        findInvitedPeopleThroughIDev.setParameter(1, idEvent);
        return ((List<User>) findInvitedPeopleThroughIDev.getResultList());
    }
    
    
    public WeatherCondition getAcceptedWeatherConditions() {
        return acceptedWeatherConditions;
    }
    
    public void setAcceptedWeatherConditions(WeatherCondition acceptedWeatherConditions) {
        this.acceptedWeatherConditions = acceptedWeatherConditions;
    }
    
    public boolean isDeletedEvent() {
        return deletedEvent;
    }
    
    public void setDeletedEvent(boolean deletedEvent) {
        this.deletedEvent = deletedEvent;
    }
    
    public void addInvitation(String email, long idEvent) {
        User u = searchManager.findUser(email);
        Event event = getEventById(idEvent);
        notificationManager.createInviteNotification(event, u);
    }
    
    public void addInvitation(User u, long idEvent) {
        Event event = getEventById(idEvent);
        notificationManager.createInviteNotification(event, u);
    }
    
    /**
     * This method executes the remove of an event: it is set as "deleted" and all invites related to this 
     * event are deleted; each interested person receives a delete notification. 
     * @param event : the event which has been cancelled. 
     */
    public void removeEvent(Event event) {
        event.setDeleted(true);
        em.merge(event);
        //Invio delle notifiche di cancellazione dell'evento
        for(Invite inv : searchManager.findInviteRelatedToAnEvent(event)) {
            if(inv.getStatus() == Invite.InviteStatus.accepted || inv.getStatus() == Invite.InviteStatus.invited || inv.getStatus() == Invite.InviteStatus.delayedEvent) {
                notificationManager.createDeleteNotification(inv);
                //Rimozione dell'invito
            }
            em.remove(inv);
        }
    }
    
    /**
     * This method searches in the database for an event which has the specified id. 
     * @param idEvent: the id of the event which the method is trying to find in the db
     * @return the searched event
     */
    public Event getEventById(long idEvent){
        Query findEventThroughId = em.createQuery("SELECT event from EVENT event WHERE event.id =?1 ");
        findEventThroughId.setParameter(1, idEvent);
        return ((List<Event>) findEventThroughId.getResultList()).get(0);
    }
    
    /**
     * This method executes the update of an event: if there's a change in the dates, every interested user is
     * notified and receives an email in which it is explained the result of this change.
     * @param event: the event which has been modified
     * @param awc the new accepted weather condition for the event
     */
    public void updateEventInformation(Event event, WeatherCondition awc) {
        Query updateWeatherCondition = em.createQuery("UPDATE WeatherCondition w SET w.precipitation =?1, w.wind =?2, w.temperature =?3 WHERE w.id =?4");
        updateWeatherCondition.setParameter(1, awc.getPrecipitation());
        updateWeatherCondition.setParameter(2, awc.getWind());
        updateWeatherCondition.setParameter(3, awc.getTemperature());
        updateWeatherCondition.setParameter(4, awc.getId());
        updateWeatherCondition.executeUpdate();
        
        Query updateEventInformation = em.createQuery ("UPDATE EVENT event SET event.name =?1, event.location =?2, event.description =?3, event.predefinedTypology = ?4 WHERE event.id =?5");
        updateEventInformation.setParameter(1, event.getName());
        updateEventInformation.setParameter(2, event.getLocation());
        updateEventInformation.setParameter(3, event.getDescription());
        updateEventInformation.setParameter(4, event.getPredefinedTypology());
        updateEventInformation.setParameter(5, event.getId());
        updateEventInformation.executeUpdate();
        
        
        //if the date is changed
        Event ev = getEventById(event.getId());
        if (!ev.getTimeStart().equals(event.getTimeStart()) || !ev.getTimeEnd().equals(event.getTimeEnd())) {
            boolean mailSent = false; 
            for(Invite inv : searchManager.findInviteRelatedToAnEvent(event)) {
                if(inv.getStatus().equals(Invite.InviteStatus.accepted) || inv.getStatus().equals(Invite.InviteStatus.invited) || inv.getStatus().equals(Invite.InviteStatus.delayedEvent)) {
                    mailSent = false; 
                    //Sending delay notification 
                    notificationManager.createDelayNotification(inv);
                    //Overlaps checking
                    for(Event evv : searchManager.findUserEvent(inv.getUser())) {
                        if((event.getTimeStart().after(evv.getTimeStart()) && event.getTimeStart().before(evv.getTimeEnd()))
                                || (event.getTimeEnd().after(evv.getTimeStart()) && event.getTimeEnd().before(evv.getTimeEnd()))
                                || (event.getTimeEnd().equals(evv.getTimeStart()) && event.getTimeEnd().equals(evv.getTimeEnd()))) {
                            Query updateInvitationStatus = em.createQuery("UPDATE INVITE invite SET invite.status= ?1 WHERE invite.event = ?2 AND invite.user = ?3");
                            updateInvitationStatus.setParameter(1, Invite.InviteStatus.delayedEvent);
                            updateInvitationStatus.setParameter(2, event);
                            updateInvitationStatus.setParameter(3, inv.getUser());
                            updateInvitationStatus.executeUpdate();
                            if (!mailSent) {
                                mailManager.sendMail(inv.getUser().getEmail(), "Overlapping Events", "Hi! An event for which you have received an invite has been modified: the date has been changed. According to the new date, "
                                + "the event is overlapping respect to an event to which you are going to participate. So, now you are not considered among the participants of the event of which the date has been modified. "
                                + "If you want to participate to this event, you have to delete your participation to the other event and accept another time the invitation to this one.");
                                mailSent = true; 
                            }
                        }
                    }
                    if(!mailSent) {
                        mailManager.sendMail(inv.getUser().getEmail(), "Overlapping Events", "Hi! An event for which you have received an invite has been modified: the date has been changed. You have no overlaps"
                                + "with the other events you are going to participate to so you are between the participants, but we suggest you to check your notifications and discover the new date. If you "
                                + "can't participate because of the new date, you can remove your participation to the event");
                    }
                }
            }
        }
        
       
        Query updateDateOfEvent = em.createQuery("UPDATE EVENT event SET event.timeStart =?1, event.timeEnd =?2 WHERE event.id =?3");
        updateDateOfEvent.setParameter(1, event.getTimeStart());
        updateDateOfEvent.setParameter(2, event.getTimeEnd());
        updateDateOfEvent.setParameter(3, event.getId());
        updateDateOfEvent.executeUpdate();
        /*notificationManager.setEvent(e);
        notificationManager.sendNotifications(NotificationType.delayedEvent);*/
    }
    
    /**
     * This method is called when the logged user decides to participate to an event: it updates the invite 
     * status in the database, setting it as "accepted"
     * @param ev : the event to which the user has decided to participate. 
     */
    public void addParticipantToEvent(Event ev) {
        Query updateInviteStatus = em.createQuery ("UPDATE INVITE i SET i.status =?1 WHERE i.event =?2 AND i.user = ?3");
        updateInviteStatus.setParameter(1, Invite.InviteStatus.accepted);
        updateInviteStatus.setParameter(2, ev);
        updateInviteStatus.setParameter(3, getLoggedUser());
        updateInviteStatus.executeUpdate();
    }
    
    /**
     * This method is called when the logged user decides to remove his participation to an event: 
     * it updates the invite status in the database, setting it as "notAccepted"
     * @param ev : the event to which the user has decided to remove his participation. 
     */
    public void removeParticipantFromEvent(Event ev) {
        Query updateInviteStatus = em.createQuery ("UPDATE INVITE i SET i.status =?1 WHERE i.event =?2 AND i.user = ?3");
        updateInviteStatus.setParameter(1, Invite.InviteStatus.notAccepted);
        updateInviteStatus.setParameter(2, ev);
        updateInviteStatus.setParameter(3, getLoggedUser());
        updateInviteStatus.executeUpdate();
    }
    
    /**
     * This methods search in the database all the events which are created by the specified user
     * @param user: the user on which the search is based
     * @return All the events created by the specified user
     */
    public List<Event> loadUserCreatedEvents(User user) {
        Query qCreatedEvents = em.createQuery("SELECT e FROM EVENT e WHERE e.organizer.email =?1");
        qCreatedEvents.setParameter(1, user.getEmail());
        List<Event> createdEvents = (List<Event>) qCreatedEvents.getResultList();
        return createdEvents;
    }
    
    /**
     * This methods search in the database all the events which are accepted by the specified user
     * @param user: the user on which the search is based
     * @return All the events accepted by the specified user
     */
    public List<Event> loadUserAcceptedEvents(User user) {
        Query qAcceptedEvents = em.createQuery("SELECT e FROM EVENT e, INVITE i WHERE i.user.email =?1 AND i.event.id = e.id AND i.status =?2");
        qAcceptedEvents.setParameter(1, user.getEmail());
        qAcceptedEvents.setParameter(2, Invite.InviteStatus.accepted);
        
        List<Event> acceptedEvents = (List<Event>) qAcceptedEvents.getResultList();
        return acceptedEvents;
    }
    
    /**
     * This methods calls two other methods which find his accepted and created events: if his calendar is 
     * public, they are added to the result list
     * @param u: the user on which the search is based
     * @return All the events which are displayed in the calendar of the searched user
     */
    public List<Event> loadEvent(User u) {
        List<Event> userEvents = new ArrayList<>();
        if (u.isCalendarPublic()) {
            for(Event e : loadUserCreatedEvents(u)) {
                if(e.isPublicEvent()) {
                    userEvents.add(e);
                }
            }
            for (Event e : loadUserAcceptedEvents(u)) {
                if(e.isPublicEvent()) {
                    userEvents.add(e);
                }
            }
        }
        return userEvents;
    }
    
    /**
     * It calls a method of the searchManager in order to find all the events stored in the database
     * @return all the events stored in the database
     */
    public List<Event> getAllEvents() {
        return searchManager.findAllEvents();
    }
    
    
    /**
     * This method returns the list of the users that have accepted the invitation for the specified event
     * @param e: the event on which the search is based
     * @return list of the users that have accepted the invitation for the specified event
     */
    public List<User> getAcceptedPeople(Event e){
        Query findAcceptedPeople = em.createQuery("SELECT u FROM INVITE i, USER u WHERE i.event = ?1 AND i.user.email = u.email AND i.status = ?2");
        findAcceptedPeople.setParameter(1, e);
        findAcceptedPeople.setParameter(2, Invite.InviteStatus.accepted);
        return ((List<User>) findAcceptedPeople.getResultList());
    }
    
     /**
     * This method returns the list of the users that have refused the invitation for the specified event
     * @param e: the event on which the search is based
     * @return list of the users that have refused the invitation for the specified event
     */
    public List<User> getRefusedPeople(Event e){
        Query findAcceptedPeople = em.createQuery("SELECT u FROM INVITE i, USER u WHERE i.event = ?1 AND i.user.email = u.email AND i.status = ?2");
        findAcceptedPeople.setParameter(1, e);
        findAcceptedPeople.setParameter(2, Invite.InviteStatus.notAccepted);
        return ((List<User>) findAcceptedPeople.getResultList());
    }
    
     /**
     * This method returns the list of the users that have not already accepted the invitation for the specified event
     * @param e: the event on which the search is based
     * @return list of the users that have not already accepted the invitation for the specified event
     */
    public List<User>getPendentPeople(Event e){
        Query findAcceptedPeople = em.createQuery("SELECT u FROM INVITE i, USER u WHERE i.event = ?1 AND i.user.email = u.email AND i.status = ?2");
        findAcceptedPeople.setParameter(1, e);
        findAcceptedPeople.setParameter(2, Invite.InviteStatus.invited);
        return ((List<User>) findAcceptedPeople.getResultList());
    }
    
    /**
     * This method compares the weather forecast with the accepted weather condition of the event: 
     * if there is a discrepancy, a mail is sent to the organizer
     * @param event 
     */
    public void checkWeatherForecast(Event event) {
        boolean notDesiredTemperature = false; 
        boolean notDesiredPrecipitation = false; 
        boolean notDesiredWind = false; 
        switch ((int)event.getAcceptedWeatherConditions().getTemperature()) {
            case (0): 
                if(event.getWeatherForecast().getTemperature() > 0) {
                    notDesiredTemperature = true; 
                }
                break; 
            case (1): 
                if(event.getWeatherForecast().getTemperature() < 0 || event.getWeatherForecast().getTemperature() > 10) {
                    notDesiredTemperature = true; 
                }
                break; 
            case (2): 
                if(event.getWeatherForecast().getTemperature() < 10 || event.getWeatherForecast().getTemperature() > 20) {
                    notDesiredTemperature = true; 
                }
               break; 
            case (3):
                if(event.getWeatherForecast().getTemperature() < 20) {
                    notDesiredTemperature = true; 
                }
                break; 
            default: 
                break; 
        }
        switch ((int)event.getAcceptedWeatherConditions().getWind()) {
            case (0): 
                if(event.getWeatherForecast().getWind() > 0) {
                    notDesiredWind = true; 
                }
                break; 
            case (1): 
                if(event.getWeatherForecast().getWind() > 8) {
                    notDesiredWind = true; 
                }
                break; 
            default: 
                break; 
        }
        if(event.getAcceptedWeatherConditions().getPrecipitation() == false && event.getWeatherForecast().getPrecipitation() == true) {
            notDesiredPrecipitation = true; 
        }
        
        if(notDesiredTemperature || notDesiredPrecipitation || notDesiredWind) {
            String temperature = ""; 
            String wind = ""; 
            String precipitation = ""; 
            if(notDesiredTemperature) {
                temperature = "The temperature for your event is different from which you have indicated as desired"; 
            }
            if(notDesiredPrecipitation) {
                precipitation = "The precipitation for your event is different from which you have indicated as desired";
            }
            if(notDesiredWind) {
                wind = "The wind for your event is different from which you have indicated as desired"; 
            }
            mailManager.sendMail(event.getOrganizer().getEmail(), "Not optimal weather forecasts", "Hi! We suggest you to check the weather forecast for your event, since we have discover some problems: " + precipitation + " " + wind + " " + temperature);
            }
        }
    
    /**
     * This method checks if an invitation has already been sent to the user with the inserted email; 
     * if an invitation already exists, it will return a false value, indicating that it is not possible
     * to send another invitation to that user. 
     * @param email: email of the user that the organizer wants to invite to his event
     * @param event: the event for which the organizer wants to send an invitation
     * @return : the boolean value, which highlights if it is possible to send an invitation to the specified user
     */
        public boolean checkEmailForInvitation(String email, Event event) {
        for(Invite invite: searchManager.findInviteRelatedToAnEvent(event)) {
            if(invite.getUser().getEmail().equals(email)) {
                return false; 
            }
        }
        return true;  
    }
        
    
        
    
}
