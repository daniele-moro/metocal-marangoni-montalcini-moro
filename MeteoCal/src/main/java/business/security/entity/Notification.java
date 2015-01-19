/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;

@Entity(name = "NOTIFICATION")
public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
   
    private NotificationType type;
    
    private Users notificatedUser;
    
    private Event relatedevent;
    
    private boolean seen;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date generationDate; 

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public NotificationType getType() {
        return type;
    }

  
    public void setType(NotificationType type) {
        this.type = type;
    }

   
    public Users getNotificatedUser() {
        return notificatedUser;
    }

    public void setNotificatedUser(Users notificatedUser) {
        this.notificatedUser = notificatedUser;
    }
    
    public Event getRelatedEvent() {
        return relatedevent;
    }
    
    public void setRelatedEvent(Event relatedevent) {
        this.relatedevent = relatedevent;
    }
       
    public Date getGenerationDate() {
        return generationDate;
    }
    
    public void setGenerationDate(Date generationDate) {
        this.generationDate = generationDate;
    }

    /**
     * @return the seen
     */
    public boolean isSeen() {
        return seen;
    }

    /**
     * @param seen the seen to set
     */
    public void setSeen(boolean seen) {
        this.seen = seen;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Notification other = (Notification) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
}