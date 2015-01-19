package gui.security.boundary;

import business.security.control.EventManager;
import business.security.control.SearchManager;
import business.security.control.UserInformationLoader;
import business.security.entity.Event;
import business.security.entity.Users;
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
    
    private Users user;
    
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
                        DefaultScheduleEvent event =  new DefaultScheduleEvent(
                                ev.getName(),
                                ev.getTimeStart(),
                                ev.getTimeEnd(),
                                ev);
                        event.setStyleClass(ev.getPredefinedTypology().toString());
                        loggedUserEvents.addEvent(event);
                    }
                    for(Event ev : userInformationLoader.loadCreatedEvents()) {
                        DefaultScheduleEvent event =  new DefaultScheduleEvent(
                                ev.getName(),
                                ev.getTimeStart(),
                                ev.getTimeEnd(),
                                ev);
                        event.setStyleClass(ev.getPredefinedTypology().toString());
                        loggedUserEvents.addEvent(event);
                    }
                    
                }
            };
        } else {
            user= srcManager.findUser(email);
            loggedUserEvents = new LazyScheduleModel(){
                @Override
                public void loadEvents(Date start, Date end) {
                    for(Event ev : eventManager.loadEvents(user)) {
                        DefaultScheduleEvent event;
                        if(ev.isPublicEvent()){
                            System.out.println("PUBLIC");
                            event =  new DefaultScheduleEvent(
                                    ev.getName(),
                                    ev.getTimeStart(),
                                    ev.getTimeEnd(),
                                    ev);
                            event.setStyleClass(ev.getPredefinedTypology().toString());
                        } else {
                            System.out.println("PRIVATE");
                            event = new DefaultScheduleEvent("PRIVATE", ev.getTimeStart(), ev.getTimeEnd());
                            event.setStyleClass("private");
                        }
                        
                        loggedUserEvents.addEvent(event);
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
    public Users getUser() {
        return user;
    }
    
    public String getPredefined(){
        if(event.getData()!=null){
            System.out.println("PREDEFINED TYPO: "+((Event) event.getData()).getPredefinedTypology());
            return ((Event) event.getData()).getPredefinedTypology().toString();
        }
        return "";
    }
    
    public String goToEvent(Event e){
        return "event.xhtml?faces-redirect=true&amp;id="+e.getId();
    }
    
}