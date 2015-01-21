package business.security.control;

import business.security.entity.Event;
import business.security.entity.Invite;
import business.security.entity.PredefinedTypology;
import business.security.entity.Users;
import business.security.entity.WeatherCondition;
import exception.DateConsistencyException;
import exception.InviteException;
import exception.WeatherException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.codehaus.jettison.json.JSONException;

@Stateless
public class EventManager {
    
    @PersistenceContext
            EntityManager em;
    
    @Inject
            Principal principal;
    
    @EJB
            NotificationManager notificationManager;
    
    @EJB
            UserInformationLoader userInformationLoader;
    
    @EJB
            SearchManager searchManager;
    
    @EJB
            MailManager mailManager;
    
    @EJB
            JsonPars p;
    
    /**
     * Creation of a new Event, it also takes the weather forecast
     *
     * @param event Event to add
     * @throws DateConsistencyException Exception in case of overlapping or
     * inconsitent date
     * @throws JSONException
     */
    public void createEvent(Event event) throws DateConsistencyException {
        if (checkDateConsistency(event)) {
            System.out.println("NOT PREDEFINED TYPO:" + event.getNotPredefinedTypology());
            
            if (event.getOrganizer() == null) {
                event.setOrganizer(getLoggedUser());
            }
            if (event.isOutdoor()) {
                save(event.getAcceptedWeatherConditions());
                em.persist(event);
                
            } else {
                event.setAcceptedWeatherConditions(null);
                em.persist(event);
            }
            try {
                //prelevo le previsioni del tempo
                WeatherCondition weatherForecast = p.parsingWeather(event.getLatitude(), event.getLongitude(), event.getTimeStart());
                //Le aggiungo all'evento se e solo se non mi torna una exception
                event.setWeatherForecast(weatherForecast);
                save(event.getWeatherForecast());
                em.merge(event);
                
            } catch (WeatherException ex) {
                System.out.println("" + ex.getMessage());
            }
        } else {
            throw new DateConsistencyException("You may have some overlapping event, or the date of start is after the end");
        }
    }
    
    /**
     * Persist of the weather condition
     *
     * @param weatherCondition
     */
    private void save(WeatherCondition weatherCondition) {
        em.persist(weatherCondition);
    }
    
    /**
     * It finds the logged user
     *
     * @return the logged user
     */
    public Users getLoggedUser() {
        return em.find(Users.class, principal.getName());
    }
    
    /**
     * This method checks the consistency of the interval of the start and end
     * dates, e.g., if the event with this type of time interval can be created
     * without causing any overlaps with other events
     *
     * @param event Event to control
     * @return
     */
    public boolean checkDateConsistency(Event event) {
        if (event.getTimeStart().after(event.getTimeEnd())) {
            return false;
        }
        for (Event ev : userInformationLoader.loadCreatedEvents()) {
            if (!event.equals( ev)) {
                if (ev.isOverlapped(event)) {
                    return false;
                }
            }
        }
        for (Event ev : userInformationLoader.loadAcceptedEvents()) {
            if (!event.equals(ev)) {
                if (ev.isOverlapped(event)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public List<Users> getInvitedPeople(long idEvent) {
        Query findInvitedPeopleThroughIDev = em.createQuery("SELECT u FROM INVITE i, USERS u WHERE i.event.id = ?1 AND i.user.email = u.email");
        System.out.println("" + idEvent);
        findInvitedPeopleThroughIDev.setParameter(1, idEvent);
        return ((List<Users>) findInvitedPeopleThroughIDev.getResultList());
    }
    
    /**
     * This method add an invitation related to the event passed to the user
     * passed
     *
     * @param user Users to add the invitation
     * @param event Event related to the invitation
     * @throws exception.InviteException
     */
    public void addInvitation(Users user, Event event) throws InviteException {
        //Control if parameter are null
        if (user == null) {
            throw new InviteException("User inexistent");
        }
        if (event == null) {
            throw new InviteException("Event inexistent");
        }
        //Control if the organizer is the invited
        if (event.isOrganizer(user)) {
            throw new InviteException("The organizer can't be invited");
        }
        
        //Control if the user has already been invited
        if (!this.checkUserForInvitation(user, event)) {
            throw new InviteException("The user has already been invited");
        }
        notificationManager.createInviteNotification(event, user);
    }
    
    /**
     * This method executes the remove of an event: it is set as "deleted" and
     * all invites related to this event are deleted; each interested person
     * receives a delete notification.
     *
     * @param event : the event which has been cancelled.
     */
    public void removeEvent(Event event) {
        event.setDeleted(true);
        em.merge(event);
        //Invio delle notifiche di cancellazione dell'evento
        for (Invite inv : searchManager.findInviteRelatedToAnEvent(event)) {
            if (inv.getStatus() == Invite.InviteStatus.accepted || inv.getStatus() == Invite.InviteStatus.invited || inv.getStatus() == Invite.InviteStatus.delayedEvent) {
                notificationManager.createDeleteNotification(inv);
            }
            //Rimozione dell'invito
            em.remove(inv);
        }
    }
    
    /**
     * This method searches in the database for an event which has the specified
     * id.
     *
     * @param idEvent: the id of the event which the method is trying to find in
     * the db
     * @return the searched event or null if the id is incorrect
     */
    public Event getEventById(long idEvent) {
        Query findEventThroughId = em.createQuery("SELECT event from EVENT event WHERE event.id =?1 ");
        findEventThroughId.setParameter(1, idEvent);
        if (!findEventThroughId.getResultList().isEmpty()) {
            return (Event) findEventThroughId.getResultList().get(0);
        } else {
            return null;
        }
    }
    
    /**
     * This method executes the update of an event: if there's a change in the
     * dates, every interested user is notified and receives an email in which
     * it is explained the result of this change.
     *
     * @param event: the event which has been modified
     * @param awc the new accepted weather condition for the event
     * @throws exception.DateConsistencyException
     */
    public void updateEventInformation(Event event, WeatherCondition awc) throws DateConsistencyException {
        //Check the consistency of the modified event
        if (checkDateConsistency(event)) {
            
            //if the acceptedeWeather conditions are modified, we modify them in the database
            if (awc != null) {
                Query updateWeatherCondition = em.createQuery("UPDATE WeatherCondition w SET w.precipitation =?1, w.wind =?2, w.temperature =?3 WHERE w.id =?4");
                updateWeatherCondition.setParameter(1, awc.getPrecipitation());
                updateWeatherCondition.setParameter(2, awc.getWind());
                updateWeatherCondition.setParameter(3, awc.getTemperature());
                updateWeatherCondition.setParameter(4, awc.getId());
                updateWeatherCondition.executeUpdate();
            }
            
            //Update of the information of the event
            Query updateEventInformation = em.createQuery("UPDATE EVENT event SET event.name =?1, event.location =?2, event.description =?3, event.predefinedTypology = ?4 WHERE event.id =?5");
            updateEventInformation.setParameter(1, event.getName());
            updateEventInformation.setParameter(2, event.getLocation());
            updateEventInformation.setParameter(3, event.getDescription());
            updateEventInformation.setParameter(4, event.getPredefinedTypology());
            updateEventInformation.setParameter(5, event.getId());
            updateEventInformation.executeUpdate();
            
            //Control if the notpredefinedtypology is modified
            if (event.getPredefinedTypology().equals(PredefinedTypology.other)) {
                Query updateNotPredefinedTypology = em.createQuery("UPDATE EVENT event SET event.notPredefinedTypology =?1 WHERE event.id =?2");
                updateNotPredefinedTypology.setParameter(1, event.getNotPredefinedTypology());
                updateNotPredefinedTypology.setParameter(2, event.getId());
                updateNotPredefinedTypology.executeUpdate();
            } else {
                Query updateNotPredefinedTypology = em.createQuery("UPDATE EVENT event SET event.notPredefinedTypology =?1 WHERE event.id =?2");
                updateNotPredefinedTypology.setParameter(1, null);
                updateNotPredefinedTypology.setParameter(2, event.getId());
                updateNotPredefinedTypology.executeUpdate();
            }
            
            //if the date is changed
            Event ev = getEventById(event.getId());
            if (!ev.getTimeStart().equals(event.getTimeStart()) || !ev.getTimeEnd().equals(event.getTimeEnd())) {
                boolean mailSent = false;
                
                for (Invite inv : searchManager.findInviteRelatedToAnEvent(event)) {
                    if (inv.getStatus().equals(Invite.InviteStatus.accepted)
                            || inv.getStatus().equals(Invite.InviteStatus.invited)
                            || inv.getStatus().equals(Invite.InviteStatus.delayedEvent)) {
                        mailSent = false;
                        //Sending delay notification
                        notificationManager.createDelayNotification(inv);
                        //Overlaps checking
                        for (Event evv : searchManager.findUserEvent(inv.getUser())) {
                            //Controllo che l'evento non sia il corrente e che sia sovrapposto
                            if (!evv.equals(ev) && evv.isOverlapped(event) && !mailSent){
                                /*(event.getTimeStart().after(evv.getTimeStart()) && event.getTimeStart().before(evv.getTimeEnd()))
                                || (event.getTimeEnd().after(evv.getTimeStart()) && event.getTimeEnd().before(evv.getTimeEnd()))
                                || (event.getTimeEnd().equals(evv.getTimeStart()) && event.getTimeEnd().equals(evv.getTimeEnd()))) {*/
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
                        if (!mailSent) {
                            mailManager.sendMail(inv.getUser().getEmail(), "Modified date of event", "Hi! An event for which you have received an invite has been modified: the date has been changed. You have no overlaps "
                                    + "with the other events you are going to participate to so you are between the participants, but we suggest you to check your notifications and discover the new date. If you "
                                    + "can't participate because of the new date, you can remove your participation to the event");
                        }
                    }
                }
                
                //If the date is changed, the update is performed
                Query updateDateOfEvent = em.createQuery("UPDATE EVENT event SET event.timeStart =?1, event.timeEnd =?2 WHERE event.id =?3");
                updateDateOfEvent.setParameter(1, event.getTimeStart());
                updateDateOfEvent.setParameter(2, event.getTimeEnd());
                updateDateOfEvent.setParameter(3, event.getId());
                updateDateOfEvent.executeUpdate();
                
            }
            try {
                //prelevo le previsioni del tempo
                WeatherCondition weatherForecast = p.parsingWeather(event.getLatitude(), event.getLongitude(), event.getTimeStart());
                //Le aggiungo all'evento se e solo se non mi torna una exception
                WeatherCondition oldWC = event.getWeatherForecast();
                Query updateWC = em.createQuery("UPDATE WeatherCondition wc SET wc.icon =?1, wc.precipitation =?2, wc.temperature =?3, wc.wind=?4 WHERE wc.id=?5");
                updateWC.setParameter(1,weatherForecast.getIcon());
                updateWC.setParameter(2,weatherForecast.getPrecipitation());
                updateWC.setParameter(3,weatherForecast.getTemperature());
                updateWC.setParameter(4,weatherForecast.getWind());
                updateWC.setParameter(5,oldWC.getId());
                updateWC.executeUpdate();
                
            } catch (WeatherException ex) {
                System.out.println("" + ex.getMessage());
            }
        } else {
            throw new DateConsistencyException("You may have some overlapping event, or the date of start is after the end");
        }
    }
    
    /**
     * This method is called when the logged user decides to participate to an
     * event: it updates the invite status in the database, setting it as
     * "accepted"
     *
     * @param ev Event the loggedUser wants to participate
     * @throws DateConsistencyException Exception generated in case the logged
     * user has an overlapping event
     */
    public void addParticipantToEvent(Event ev) throws DateConsistencyException {
        //Control if there is an overlapping event
        if (this.checkDateConsistency(ev)) {
            Query updateInviteStatus = em.createQuery("UPDATE INVITE i SET i.status =?1 WHERE i.event =?2 AND i.user = ?3");
            updateInviteStatus.setParameter(1, Invite.InviteStatus.accepted);
            updateInviteStatus.setParameter(2, ev);
            updateInviteStatus.setParameter(3, getLoggedUser());
            updateInviteStatus.executeUpdate();
        } else {
            //If there's an overlapping event, generate an exception
            throw new DateConsistencyException("You may have an ovelapping event, you have to delete your participation to that event before accepting this");
        }
    }
    
    /**
     * This method is called when the logged user decides to remove his
     * participation to an event: it updates the invite status in the database,
     * setting it as "notAccepted"
     *
     * @param ev : the event to which the user has decided to remove his
     * participation.
     */
    public void removeParticipantFromEvent(Event ev) {
        Query updateInviteStatus = em.createQuery("UPDATE INVITE i SET i.status =?1 WHERE i.event =?2 AND i.user = ?3");
        updateInviteStatus.setParameter(1, Invite.InviteStatus.notAccepted);
        updateInviteStatus.setParameter(2, ev);
        updateInviteStatus.setParameter(3, getLoggedUser());
        updateInviteStatus.executeUpdate();
    }
    
    /**
     * This methods search in the database all the events which are created by
     * the specified user (NOT deleted)
     *
     * @param user: the user on which the search is based
     * @return All the events created by the specified user
     */
    public List<Event> loadUserCreatedEvents(Users user) {
        Query qCreatedEvents = em.createQuery("SELECT e FROM EVENT e WHERE e.organizer.email =?1 AND e.deleted=FALSE");
        qCreatedEvents.setParameter(1, user.getEmail());
        List<Event> createdEvents = (List<Event>) qCreatedEvents.getResultList();
        return createdEvents;
    }
    
    /**
     * This methods search in the database all the events which are accepted by
     * the specified user (NOT deleted)
     *
     * @param user: the user on which the search is based
     * @return All the events accepted by the specified user
     */
    public List<Event> loadUserAcceptedEvents(Users user) {
        Query qAcceptedEvents = em.createQuery("SELECT e FROM EVENT e, INVITE i WHERE i.user.email =?1 AND i.event.id = e.id AND i.status =?2 AND e.deleted=FALSE");
        qAcceptedEvents.setParameter(1, user.getEmail());
        qAcceptedEvents.setParameter(2, Invite.InviteStatus.accepted);
        
        List<Event> acceptedEvents = (List<Event>) qAcceptedEvents.getResultList();
        return acceptedEvents;
    }
    
    /**
     * This methods calls
     * {@link loadUserCreatedEvents(Users user) loadUserCreatedEvents(Users)} and
     * {@link loadUserAcceptedEvents(Users user) loadUserAcceptedEvents(Users)}
     * which find his accepted and created events (NOT delete), events which are
     * public and only if user has public calendar
     *
     * @param u: the user on which the search is based
     * @return All the events which are displayed in the calendar of the
     * searched user
     */
    public List<Event> loadEvents(Users u) {
        List<Event> userEvents = new ArrayList<>();
        if (u.isCalendarPublic()) {
            for (Event e : loadUserCreatedEvents(u)) {
                // if (e.isPublicEvent()) {
                userEvents.add(e);
                //  }
            }
            for (Event e : loadUserAcceptedEvents(u)) {
                // if (e.isPublicEvent()) {
                userEvents.add(e);
                // }
            }
        }
        return userEvents;
    }
    
    /**
     * This method returns the list of the users that have accepted the
     * invitation for the specified event
     *
     * @param e: the event on which the search is based
     * @return list of the users that have accepted the invitation for the
     * specified event
     */
    public List<Users> getAcceptedPeople(Event e) {
        Query findAcceptedPeople = em.createQuery("SELECT u FROM INVITE i, USERS u WHERE i.event = ?1 AND i.user.email = u.email AND i.status = ?2");
        findAcceptedPeople.setParameter(1, e);
        findAcceptedPeople.setParameter(2, Invite.InviteStatus.accepted);
        return ((List<Users>) findAcceptedPeople.getResultList());
    }
    
    /**
     * This method returns the list of the users that have refused the
     * invitation for the specified event
     *
     * @param e: the event on which the search is based
     * @return list of the users that have refused the invitation for the
     * specified event
     */
    public List<Users> getRefusedPeople(Event e) {
        Query findAcceptedPeople = em.createQuery("SELECT u FROM INVITE i, USERS u WHERE i.event = ?1 AND i.user.email = u.email AND i.status = ?2");
        findAcceptedPeople.setParameter(1, e);
        findAcceptedPeople.setParameter(2, Invite.InviteStatus.notAccepted);
        return ((List<Users>) findAcceptedPeople.getResultList());
    }
    
    /**
     * Metodo che torna la lista degli utenti che non hanno ancora accettato o
     * rifiutato l'evento o chi ha l'evento come spostato (causa
     * sovrapposizioni)
     *
     * @param e
     * @return
     */
    public List<Users> getPendentPeople(Event e) {
        Query findAcceptedPeople = em.createQuery("SELECT u FROM INVITE i, USERS u WHERE i.event = ?1 AND i.user.email = u.email AND (i.status = ?2 OR i.status = ?3)");
        findAcceptedPeople.setParameter(1, e);
        findAcceptedPeople.setParameter(2, Invite.InviteStatus.invited);
        findAcceptedPeople.setParameter(3, Invite.InviteStatus.delayedEvent);
        return ((List<Users>) findAcceptedPeople.getResultList());
    }
    
    public Date suggestNewDate(Event event) {
        List<WeatherCondition> listWeatherForecast;
        try {
            listWeatherForecast = p.weatherForecastNextDays(event.getLatitude(), event.getLongitude(), event.getTimeStart());
            for (int i = 1; i < listWeatherForecast.size(); i++) {
                if (checkWeatherForecast(event.getAcceptedWeatherConditions(), listWeatherForecast.get(i))) {
                    Calendar date = new GregorianCalendar();
                    date.setTime(event.getTimeStart());
                    date.add(Calendar.DAY_OF_MONTH, i);
                    
                    System.out.println("pioggia: " + listWeatherForecast.get(i));
                    System.out.println("vento: " + listWeatherForecast.get(i));
                    System.out.println("temperatura" + listWeatherForecast.get(i));
                    System.out.println("timeStart: " + event.getTimeStart());
                    System.out.println("date : " + date.getTime());
                    return date.getTime();
                }
            }
        } catch (WeatherException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * This method compares the weather forecast with the accepted weather
     * condition of the event: if there is a discrepancy, a mail is sent to the
     * organizer
     *
     * @param event
     */
    public boolean checkWeatherForecast(WeatherCondition acceptedWeatherCondition, WeatherCondition weatherForecast) throws WeatherException {
        if (weatherForecast == null || acceptedWeatherCondition == null) {
            throw new WeatherException("No weather forecast or accepted weather condition");
        }
        return (checkTemperature(acceptedWeatherCondition, weatherForecast) || checkWind(acceptedWeatherCondition, weatherForecast) || checkPrecipitation(acceptedWeatherCondition, weatherForecast));
    }
    
    public boolean checkWeatherForecast(Event event) {
        boolean notDesiredTemperature = checkTemperature(event.getAcceptedWeatherConditions(), event.getWeatherForecast());
        boolean notDesiredPrecipitation = checkPrecipitation(event.getAcceptedWeatherConditions(), event.getWeatherForecast());
        boolean notDesiredWind = checkWind(event.getAcceptedWeatherConditions(), event.getWeatherForecast());
        String message = "";
        if (notDesiredTemperature) {
            message += " \nthe temperature for your event is different from which you have indicated as desired ";
        }
        if (notDesiredPrecipitation) {
            message += "\nthe precipitation for your event is different from which you have indicated as desired ";
        }
        if (notDesiredWind) {
            message += "\nthe wind for your event is different from which you have indicated as desired ";
        }
        if (notDesiredPrecipitation || notDesiredTemperature || notDesiredWind) {
            notificationManager.createWeatherConditionChangedNotification(event.getOrganizer(), event);
            mailManager.sendMail(event.getOrganizer().getEmail(), "Not optimal weather forecasts", "Hi! We suggest you to check the weather forecast for your event, since we have discovered some problems: " + message);
            return true;
        }
        return false;
    }
    
    public boolean checkWeatherOneDayBefore(Event event) {
        if (checkTemperature(event.getAcceptedWeatherConditions(), event.getWeatherForecast())
                || checkPrecipitation(event.getAcceptedWeatherConditions(), event.getWeatherForecast())
                || checkWind(event.getAcceptedWeatherConditions(), event.getWeatherForecast())) {
            for (Invite invite : searchManager.findInviteRelatedToAnEvent(event)) {
                if (invite.getStatus().equals(Invite.InviteStatus.accepted)) {
                    notificationManager.createWeatherConditionChangedNotification(invite.getUser(), event);
                }
            }
            return true;
        }
        return false;
    }
    
    private boolean checkTemperature(WeatherCondition acceptedWeatherCondition, WeatherCondition weatherForecast) {
        switch ((int) acceptedWeatherCondition.getTemperature()) {
            case (0):
                if (weatherForecast.getTemperature() >= (0 + 273)) {
                    return true;
                }
                break;
            case (1):
                if (weatherForecast.getTemperature() < (0 + 273) || weatherForecast.getTemperature() > (10 + 273)) {
                    return true;
                }
                break;
            case (2):
                if (weatherForecast.getTemperature() < (10 + 273) || weatherForecast.getTemperature() > (20 + 273)) {
                    return true;
                }
                break;
            case (3):
                if (weatherForecast.getTemperature() < (20 + 273)) {
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }
    
    private boolean checkWind(WeatherCondition acceptedWeatherCondition, WeatherCondition weatherForecast) {
        switch ((int) acceptedWeatherCondition.getWind()) {
            case (0):
                if (weatherForecast.getWind() > 0) {
                    return true;
                }
                break;
            case (1):
                if (weatherForecast.getWind() > 8) {
                    return true;
                }
                break;
            case (2):
                if (weatherForecast.getWind() < 8) {
                    return true;
                }
            default:
                break;
        }
        return false;
    }
    
    private boolean checkPrecipitation(WeatherCondition acceptedWeatherCondition, WeatherCondition weatherForecast) {
        if (acceptedWeatherCondition.getPrecipitation() == false && weatherForecast.getPrecipitation() == true) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * This method checks if an invitation has already been sent to the user
     * with the inserted email; if an invitation already exists, it will return
     * a false value, indicating that it is not possible to send another
     * invitation to that user.
     *
     * @param user: user that the organizer wants to invite to his event
     * @param event: the event for which the organizer wants to send an
     * invitation
     * @return : the boolean value, which highlights if it is possible to send
     * an invitation to the specified user
     */
    public boolean checkUserForInvitation(Users user, Event event) {
        for (Invite invite : searchManager.findInviteRelatedToAnEvent(event)) {
            if (invite.getUser().equals(user)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * This method updates the weather forecast and in case of outdoor events
     * sends the email if the weather forecast are changed
     *
     * @param e Event
     * @param newWeat New weather forecast
     */
    public void weatherUpdater(Event e, WeatherCondition newWeat) {
        if (e.getWeatherForecast() != null) {
            WeatherCondition w;
            Query findCondition = em.createQuery("SELECT w FROM WeatherCondition w WHERE w.id = ?1");
            findCondition.setParameter(1, e.getWeatherForecast().getId());
            w = (WeatherCondition) findCondition.getResultList().get(0);
            
            //Mandiamo le email in caso di cambiamento di condizioni climatiche
            if (!w.getIcon().equals(newWeat.getIcon()) && e.isOutdoor()) {
                mailManager.sendMail(e.getOrganizer().getEmail(), "Weather forecast changed for the event " + '"' + e.getName() + '"', "The weather forecast for the event are changed.");
                for (Users u : this.getAcceptedPeople(e)) {
                    mailManager.sendMail(u.getEmail(), "Weather forecast changed for the event " + '"' + e.getName() + '"', "The weather forecast for the event are changed.");
                }
            }
            w.setPrecipitation(newWeat.getPrecipitation());
            w.setTemperature(newWeat.getTemperature());
            w.setIcon(newWeat.getIcon());
            w.setWind(newWeat.getWind());
            em.merge(w);
        } else {
            em.persist(newWeat);
            e.setWeatherForecast(newWeat);
            em.merge(e);
        }
        
    }
}
