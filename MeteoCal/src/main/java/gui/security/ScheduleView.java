package gui.security;
 
import business.security.boundary.EventManager;
import business.security.boundary.SearchManager;
import business.security.boundary.UserInformationLoader;
import business.security.entity.Event;
import business.security.entity.User;
import java.io.Serializable;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
 
@ManagedBean
@ViewScoped
public class ScheduleView implements Serializable {
 
     
    private ScheduleModel loggedUserEvents;
    
    private User user;
 
    private ScheduleEvent event = new DefaultScheduleEvent();
    
    @EJB
    private UserInformationLoader userInformationLoader; 
    
    @EJB
    private EventManager eventManager;
    
    @EJB
    private SearchManager srcManager;
    
    @PostConstruct
    public void init() {
        
        temp();
    }
    
    private void temp(){
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();
        String email=req.getParameter("email");
        if(email==null){
            loggedUserEvents = new LazyScheduleModel() {
                
                @Override
                public void loadEvents(Date start, Date end) {
                    for(Event ev : userInformationLoader.loadAcceptedEvents()) {
                        loggedUserEvents.addEvent(new DefaultScheduleEvent(ev.getName(), ev.getTimeStart(), ev.getTimeEnd()));
                    }
                    for(Event ev : userInformationLoader.loadCreatedEvents()) {
                        loggedUserEvents.addEvent(new DefaultScheduleEvent(ev.getName(), ev.getTimeStart(), ev.getTimeEnd()));
                    }
                    
                }
            };
        } else {
           user= srcManager.findUser(email);
           loggedUserEvents = new LazyScheduleModel(){
                @Override
                public void loadEvents(Date start, Date end) {
                    for(Event ev : eventManager.loadEvent(user)) {
                        loggedUserEvents.addEvent(new DefaultScheduleEvent(ev.getName(), ev.getTimeStart(), ev.getTimeEnd())); 
                    }
                }
            };
            
        }
        
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

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }
   
}