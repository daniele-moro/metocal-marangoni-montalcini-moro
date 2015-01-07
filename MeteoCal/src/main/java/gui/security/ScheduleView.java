package gui.security;
 
import business.security.boundary.EventManager;
import business.security.boundary.SearchManager;
import business.security.boundary.UserInformationLoader;
import business.security.entity.Event;
import business.security.entity.User;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
 
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
 
@ManagedBean
@ViewScoped
public class ScheduleView implements Serializable {
 
     
    private ScheduleModel loggedUserEvents;
    
    private ScheduleModel searchedUserEvents; 
 
    private ScheduleEvent event = new DefaultScheduleEvent();
    
    @EJB
    private UserInformationLoader userInformationLoader; 
    
    @EJB
    private EventManager eventManager; 
 
    @PostConstruct
    public void init() {
        loggedUserEvents = new LazyScheduleModel() {
             
            @Override
            public void loadEvents(Date start, Date end) {
                for(Event event : userInformationLoader.loadAcceptedEvents()) {
                    loggedUserEvents.addEvent(new DefaultScheduleEvent(event.getName(), event.getTimeStart(), event.getTimeEnd())); 
                }
                for(Event event : userInformationLoader.loadCreatedEvents()) {
                    loggedUserEvents.addEvent(new DefaultScheduleEvent(event.getName(), event.getTimeStart(), event.getTimeEnd())); 
                }
                
            }   
        };
    }
     
    

     
    public ScheduleEvent getEvent() {
        return event;
    }
 
    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }
   
     
    public void onEventSelect(SelectEvent selectEvent) {
        event = (ScheduleEvent) selectEvent.getObject();
    }
     
    
    public ScheduleModel getLoggedUserEvents() {
        return loggedUserEvents;
    }
    
    public ScheduleModel getSearchedUserEvents(User user) {
            searchedUserEvents = new LazyScheduleModel(){
                @Override
                public void loadEvents(Date start, Date end) {
                    for(Event event : eventManager.loadEvent(user)) {
                        searchedUserEvents.addEvent(new DefaultScheduleEvent(event.getName(), event.getTimeStart(), event.getTimeEnd())); 
                    }
                }
            };
        return searchedUserEvents; 
    }
   
}