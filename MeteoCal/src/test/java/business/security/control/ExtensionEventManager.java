/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package business.security.control;

import business.security.entity.Event;
import business.security.entity.Group;
import business.security.entity.Users;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author DanieleMarangoni
 */

@Stateless
public class ExtensionEventManager extends EventManager {
    @EJB
    private ExtensionUserInfoLoader extensionUserInfoLoader;
    
     static final String loggedUserEmail = "user1@mail.it";
    
    public ExtensionEventManager() {
        
    }
    
    @Override
    public Users getLoggedUser() {
        return super.em.find(Users.class, loggedUserEmail);
    }
    
    @Override
    public boolean checkDateConsistency(Event event) {
        if (event.getTimeStart().after(event.getTimeEnd())) {
            return false;
        }
        for (Event ev : extensionUserInfoLoader.loadCreatedEvents()) {
            if (event.getId() != ev.getId()) {
                if (ev.isOverlapped(event)) {
                    return false;
                }
            }
        }
        for (Event ev : extensionUserInfoLoader.loadAcceptedEvents()) {
            if (event.getId() != ev.getId()) {
                if (ev.isOverlapped(event)) {
                    return false;
                }
            }
        }
        return true;
    }
}
