/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package gui.security.boundary;

import business.security.control.UserManager;
import business.security.control.MailManager;
import business.security.entity.Users;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

@Named
@RequestScoped
public class RegistrationBean {
    
    @EJB
    private UserManager um;
    
    @EJB
    private MailManager mailManager;
    
    private Users user;
    
    public RegistrationBean() {
    }
    
    public Users getUser() {
        if (user == null) {
            user = new Users();
        }
        return user;
    }
    
    public void setUser(Users user) {
        this.user = user;
    }
    
    public String register() {
        GregorianCalendar currDate = new GregorianCalendar();
        currDate.roll(Calendar.YEAR, -16);
        if(user.getBirthday().after(currDate.getTime())){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Invalid inserted date, you must have an age greater than 14 years");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return "";
        }
        
        String residenceTown = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("geocomplete");
        if(residenceTown ==null || residenceTown.isEmpty() ){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Location empty","");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return "";
        }
        user.setResidenceTown(residenceTown);
        um.save(user);
        mailManager.sendMail(user.getEmail(), "Registration to MeteoCal", "Welcome in Meteocal! Your registration has been succesfully completed.");
        return "index?faces-redirect=true";
    }
    
}
