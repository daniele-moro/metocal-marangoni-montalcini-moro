/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 *
 * @author DanieleMarangoni
 */
@Entity(name = "INVITE")
public class Invite implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    private Event event;

    
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public static enum InviteStatus {
        accepted, 
        notAccepted, 
        delayedEvent, 
        invited;
    }
    
    @Id
    private Users user;
    
    @NotNull(message = "May not be empty")
    private InviteStatus status; 
    

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public InviteStatus getStatus() {
        return status;
    }

    public void setStatus(InviteStatus status) {
        this.status = status;
    }
    
}
