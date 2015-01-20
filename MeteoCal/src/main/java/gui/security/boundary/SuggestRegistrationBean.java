/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.security.boundary;

import business.security.control.MailManager;
import business.security.control.SearchManager;
import business.security.control.UserManager;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.validation.constraints.Pattern;

@Named
@RequestScoped
public class SuggestRegistrationBean {

    @EJB
    private MailManager mailManager;

    @EJB
    private SearchManager searchManager;

    @EJB
    private UserManager userManager;

    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message = "invalid email")
    private String email;

    public SuggestRegistrationBean() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MailManager getMailManager() {
        return mailManager;
    }

    public void setMailManager(MailManager mailManager) {
        this.mailManager = mailManager;
    }

    public String sendSuggestion() {
        if (searchManager.findUser(email) == null) {
            mailManager.sendMail(email, "Register yourself in MeteoCal!", "Hi, " + userManager.getLoggedUser().getName() + " " + userManager.getLoggedUser().getSurname() + " suggests you to join on MeteoCal");
            email = "";
            return "suggestRegistration?faces-redirect=true";
        } else {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "The inserted email is already registered");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return "";
        }
    }

}
