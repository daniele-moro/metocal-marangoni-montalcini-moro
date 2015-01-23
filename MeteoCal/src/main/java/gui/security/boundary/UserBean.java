/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.security.boundary;

import business.security.control.UserManager;
import business.security.entity.Users;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

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
        
        //Control if the user is too young
        GregorianCalendar currDate = new GregorianCalendar();
        currDate.roll(Calendar.YEAR, -14);
        if(user.getBirthday().after(currDate.getTime())){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Invalid inserted date, you must have an age greater than 14 years");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return "";
        }
        
        //Control if the user has inserted a location
        String residenceTown = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("geocomplete");
        if(residenceTown ==null || residenceTown.isEmpty() ){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Location empty","");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return "";
        }
        user.setResidenceTown(residenceTown);
        um.updateUser(user);
        user=null;
        return "profile?faces-redirect=true";
    }
    
}
