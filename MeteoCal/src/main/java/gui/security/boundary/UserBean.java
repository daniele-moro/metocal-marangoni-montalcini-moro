/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.security.boundary;

import business.security.control.UserManager;
import business.security.entity.Users;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

@Named
@SessionScoped
public class UserBean implements Serializable{

    @EJB
    UserManager um;
    
    private Users user; 
    
    /**
     * This method returns the name of the logged user
     * @return 
     */
    public String getName() {
        return um.getLoggedUser().getName();
    }
    
    /**
     * This method return the user; if it is null, it creates a new user and set all its fields
     * to the values of the logged user
     * @return 
     */
    public Users getUser() {
        if(user==null){
            user=new Users();
            Users u= um.getLoggedUser();
            user.setBirthday(u.getBirthday());
            user.setCalendarPublic(u.isCalendarPublic());
            user.setEmail(u.getEmail());
            user.setGroupName(u.getGroupName());
            user.setName(u.getName());
            user.setPassword(u.getPassword());
            user.setPhoneNumber(u.getPhoneNumber());
            user.setResidenceTown(u.getResidenceTown());
            user.setSurname(u.getSurname());
        }
        return user;
    }
    
    /**
     * This method redirects to the Modify Profile page.
     * @return 
     */
    public String modifyProfile () {
        return "modifyProfile?faces-redirect=true";
    }
    
    /**
     * This method is called when a user has inserted the new data and have clicked the button 
     * "ModifyProfile": it calls a method of the userManager, which executes the update.
     * @return 
     */
    public String updateProfile(){
        um.updateUser(user);
        user=null;
        return "profile?faces-redirect=true";
    }
    
}
