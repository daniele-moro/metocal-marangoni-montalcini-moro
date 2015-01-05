package business.security.boundary;

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
    
    private boolean deletedEvent = false;
   
    private List<NameSurnameEmail> invitedPeople;
    
    private List<NameSurnameEmail> partialResults;
    
    private Event e; 
    
    private WeatherCondition acceptedWeatherConditions; 
    
    public EventManager() {
        e = new Event(); 
        acceptedWeatherConditions = new WeatherCondition();
        invitedPeople = new ArrayList<>(); 
        partialResults = new ArrayList<>(); 
        notificationManager = new NotificationManager();
    }
    
    /**
     * Creation of a new Event
     * @param event Event to add
     * @param awc 
     */
    public void createEvent(Event event, WeatherCondition awc) {
        event.setOrganizer(getLoggedUser());
        save(awc);
        event.setAcceptedWeatherConditions(awc);
        em.persist(event);
        setEvent(event);
       // notificationManager.setEvent(event);
    }
    
    public void save(WeatherCondition weatherCondition) {
        em.persist(weatherCondition);
    }

    public void deleteEvent() {
        em.remove(getLoggedUser());
    }

    public User getLoggedUser() {
        return em.find(User.class, principal.getName());
    }

    public boolean checkDateConsistency() {
        if (e.getTimeStart().after(e.getTimeEnd())) {
            return false;
        } else {
            for(Event ev : userInformationLoader.loadCreatedEvents()) {
                if(e.getTimeStart().after(ev.getTimeStart()) && e.getTimeStart().before(ev.getTimeEnd())
                        || e.getTimeEnd().after(ev.getTimeStart()) && e.getTimeEnd().before(ev.getTimeEnd())
                        || e.getTimeStart().equals(ev.getTimeStart()) && e.getTimeEnd().equals(ev.getTimeEnd())) {
                    return false; 
                }
            }
            for(Event ev : userInformationLoader.loadAcceptedEvents()) {
                if(e.getTimeStart().after(ev.getTimeStart()) && e.getTimeStart().before(ev.getTimeEnd()) 
                        || e.getTimeEnd().after(ev.getTimeStart()) && e.getTimeEnd().before(ev.getTimeEnd())
                        || e.getTimeStart().equals(ev.getTimeStart()) && e.getTimeEnd().equals(ev.getTimeEnd())) {
                    return false; 
                }
            }
        }
        return true; 
    }
    /**
     * Metodo per controllare la consistenza di un'intervallo, 
     * cioè se l'evento con questo intervallo di tempi può essere creato senza creare problemi
     * @param start Inzio dell'evento
     * @param end Fine dell'evento
     * @return 
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

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public Event getEvent() {
        return e;
    }

    public void setEvent(Event event) {
        this.e = event;
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
  
    public List<NameSurnameEmail> getInvitedPeople() {
        if (invitedPeople == null) {
            invitedPeople = new ArrayList<>();
        }
        return invitedPeople;
    }

    public void setInvitedPeople(List<NameSurnameEmail> invitedPeople) {
        this.invitedPeople = invitedPeople;
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
    
    public void addInvitation(String email, int idEvent) {
        NameSurnameEmail element = searchManager.findNameSurnameEmailFromUser(email);
        Event event = getEventById(idEvent);
        notificationManager.createInviteNotification(event, element);
        invitedPeople.add(element);
    }
    
    public void addInvitation(String name, String surname, int idEvent) {
        Event event = getEventById(idEvent);
        NameSurnameEmail element = searchManager.findNameEmailSurnameFromNameSurname(name, surname).get(0);
        notificationManager.createInviteNotification(event, element);
        invitedPeople.add(element);
    }
    
    public void addInvitation(NameSurnameEmail element) {
        notificationManager.createInviteNotification(e, element);
        invitedPeople.add(element);
        partialResults = new ArrayList<>(); 
    }
    
    public void removeEvent() {
        Query setEventDeleted = em.createQuery("UPDATE EVENT event SET event.deleted =?1 WHERE event.id = ?2");
        setEventDeleted.setParameter(1, true); 
        setEventDeleted.setParameter(2, e.getId());
        setEventDeleted.executeUpdate();
        //Invio delle notifiche a tutti gli utenti che erano stati invitati
        for(Invite inv : searchManager.findInviteRelatedToAnEvent(e)) {
            if(inv.getStatus() == Invite.InviteStatus.accepted || inv.getStatus() == Invite.InviteStatus.invited || inv.getStatus() == Invite.InviteStatus.delayedEvent) {
                notificationManager.createDeleteNotification(inv);
                //Rimozione dell'invito
            }
            em.remove(inv);
        }
    }
    
    public Event getEventById(int idEvent){
        Query findEventThroughId = em.createQuery("SELECT event from EVENT event WHERE event.id =?1 ");
        findEventThroughId.setParameter(1, idEvent);
        return ((List<Event>) findEventThroughId.getResultList()).get(0);
    }
    
    public void updateEventInformation() {
        Query updateWeatherCondition = em.createQuery("UPDATE WeatherCondition w SET w.precipitation =?1, w.wind =?2, w.temperature =?3 WHERE w.id =?4");
        updateWeatherCondition.setParameter(1, acceptedWeatherConditions.getPrecipitation());
        updateWeatherCondition.setParameter(2, acceptedWeatherConditions.getWind());
        updateWeatherCondition.setParameter(3, acceptedWeatherConditions.getTemperature());
        updateWeatherCondition.setParameter(4, acceptedWeatherConditions.getId());
        updateWeatherCondition.executeUpdate();
        
        Query updateEventInformation = em.createQuery ("UPDATE EVENT event SET event.name =?1, event.town =?2, event.address =?3, event.description =?4, event.predefinedTypology = ?5 WHERE event.id =?6");
        updateEventInformation.setParameter(1, e.getName());
        updateEventInformation.setParameter(2, e.getTown());
        updateEventInformation.setParameter(3, e.getAddress());
        updateEventInformation.setParameter(4, e.getDescription());
        updateEventInformation.setParameter(5, e.getPredefinedTypology());
        updateEventInformation.setParameter(6, e.getId());
        updateEventInformation.executeUpdate();
        
        
        //if the date is changed
        //si puo usare il metodo getEvetnById
        Query findEventThroughId = em.createQuery("SELECT event from EVENT event WHERE event.id =?1 ");
        findEventThroughId.setParameter(1, e.getId());
        Event ev = ((List<Event>) findEventThroughId.getResultList()).get(0);
        if (!ev.getTimeStart().equals(e.getTimeStart()) || !ev.getTimeEnd().equals(e.getTimeEnd())) {
            //notificationManager.setEvent(e);
            //notificationManager.sendNotifications(NotificationType.delayedEvent);
            
            
            for(Invite inv : searchManager.findInviteRelatedToAnEvent(e)) {
                if(inv.getStatus() == Invite.InviteStatus.accepted || inv.getStatus() == Invite.InviteStatus.invited) {
                    //Invio della notifica di DELAY
                    notificationManager.createDelayNotification(inv);
                    
                    //Controllo se ci sono sovrapposizioni
                    for(Event evv : searchManager.findUserEvent(inv.getUser())) {
                        if((e.getTimeStart().after(evv.getTimeStart()) && e.getTimeStart().before(evv.getTimeEnd()))
                                || (e.getTimeEnd().after(evv.getTimeStart()) && e.getTimeEnd().before(evv.getTimeEnd()))
                                || (e.getTimeEnd().equals(evv.getTimeStart()) && e.getTimeEnd().equals(evv.getTimeEnd()))) {
                            Query updateInvitationStatus = em.createQuery("UPDATE INVITE invite SET invite.status= ?1 WHERE invite.event = ?2 AND invite.user = ?3");
                            updateInvitationStatus.setParameter(1, Invite.InviteStatus.delayedEvent);
                            updateInvitationStatus.setParameter(2, e);
                            updateInvitationStatus.setParameter(3, inv.getUser());
                            updateInvitationStatus.executeUpdate();
                            /** @TODO: Manca da mandare la mail di notifica**/
                            
                            /*MailManager mailManager.sendMail(inv.getUser().getEmail(), "Overlapping Events", "Hi! An event for which you have received an invite has been modified: the date has been changed. According to the new date, "
                            + "the event is overlapping respect to an event to which you are going to participate. So, now you are not considered among the participants of the event of which the date has been modified. "
                            + "If you want to participate to this event, you have to delete your participation to the other event and accept another time the invitation to this one.");
                            */
                            break;
                        }
                    }
                }
            }
        }
        
        //@TODO: Manca da mandare le notifiche per il delayed event
        Query updateDateOfEvent = em.createQuery("UPDATE EVENT event SET event.timeStart =?1, event.timeEnd =?2 WHERE event.id =?3");
        updateDateOfEvent.setParameter(1, e.getTimeStart());
        updateDateOfEvent.setParameter(2, e.getTimeEnd());
        updateDateOfEvent.setParameter(3, e.getId());
        updateDateOfEvent.executeUpdate();
        /*notificationManager.setEvent(e);
        notificationManager.sendNotifications(NotificationType.delayedEvent);*/
    }
    
    /*
    NON DOVREBBE SERVIRE
    public void addInvitation() {
    //notificationManager.setEvent(e);
    }*/
    
    public void addParticipantToEvent() {
        Query updateInviteStatus = em.createQuery ("UPDATE INVITE i SET i.status =?1 WHERE i.event =?2 AND i.user = ?3");
        updateInviteStatus.setParameter(1, Invite.InviteStatus.accepted); 
        updateInviteStatus.setParameter(2, e);
        updateInviteStatus.setParameter(3, getLoggedUser()); 
        updateInviteStatus.executeUpdate();
        userInformationLoader.setInviteStatusAccepted(true);
        userInformationLoader.setInviteStatusDelayedEvent(false);
        userInformationLoader.setInviteStatusInvited(false);
        userInformationLoader.setInviteStatusNotAccepted(false);
    }
    
    public void removeParticipantFromEvent() {
        Query updateInviteStatus = em.createQuery ("UPDATE INVITE i SET i.status =?1 WHERE i.event =?2 AND i.user = ?3");
        updateInviteStatus.setParameter(1, Invite.InviteStatus.notAccepted); 
        updateInviteStatus.setParameter(2, e);
        updateInviteStatus.setParameter(3, getLoggedUser()); 
        updateInviteStatus.executeUpdate();
        userInformationLoader.setInviteStatusNotAccepted(true);
        userInformationLoader.setInviteStatusAccepted(false);
        userInformationLoader.setInviteStatusDelayedEvent(false);
        userInformationLoader.setInviteStatusInvited(false);
    }
    
    public List<Event> loadUserCreatedEvents(User user) {
        Query qCreatedEvents = em.createQuery("SELECT e FROM EVENT e WHERE e.organizer.email =?1");
        qCreatedEvents.setParameter(1, user.getEmail());
        List<Event> createdEvents = (List<Event>) qCreatedEvents.getResultList(); 
        return createdEvents; 
   }
   
   public List<Event> loadUserAcceptedEvents(User user) {
       Query qAcceptedEvents = em.createQuery("SELECT e FROM EVENT e, INVITE i WHERE i.user.email =?1 AND i.event.id = e.id AND i.status =?2");
       qAcceptedEvents.setParameter(1, user.getEmail());
       qAcceptedEvents.setParameter(2, Invite.InviteStatus.accepted);
       
       List<Event> acceptedEvents = (List<Event>) qAcceptedEvents.getResultList(); 
       return acceptedEvents; 
   }
   
   public List<Event> loadEvent() {
       User u = searchManager.getSearchedUser(); 
       List<Event> userEvents = new ArrayList<>();
       if (u.isCalendarPublic()) {
            for(Event e : loadUserCreatedEvents(u)) {
                if(e.isPublicEvent()) {
                    userEvents.add(e); 
                }
            }          
            for (Event e : loadUserAcceptedEvents(searchManager.getSearchedUser())) {
                 if(e.isPublicEvent()) {
                    userEvents.add(e); 
                }
             }
       }
      //TODO: va aggiunto un ordinamento qui
       return userEvents;
   }
   

    
    
}
