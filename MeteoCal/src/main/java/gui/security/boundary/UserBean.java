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

    public String getName() {
        return um.getLoggedUser().getName();
    }
    
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
    
    public String modifyProfile () {
        return "modifyProfile?faces-redirect=true";
    }
    
    public String updateProfile(){
        um.updateUser(user);
        user=null;
        return "profile?faces-redirect=true";
    }
    
}
