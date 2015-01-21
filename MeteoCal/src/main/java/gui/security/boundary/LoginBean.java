/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.security.boundary;

import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Named
@RequestScoped
public class LoginBean {

    private String username;
    private String password;

    /**
     * This method returns the username
     * @return 
     */
    public String getUsername() {
        return this.username;
    }
    
    /**
     * This method sets the username to the value passed as parameter
     * @param username 
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * This method returns the password
     * @return 
     */
    public String getPassword() {
        return this.password;
    }
    
    /**
     * This method sets the password to the value passed as parameter
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * This method performs the login
     * @return 
     */
    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            request.login(this.username, this.password);
        } catch (ServletException e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Login failed.",""));
            return "";
        }
        return "/user/home?faces-redirect=true";
    }
    
    /**
     * This method performs the logout
     * @return 
     */
    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        request.getSession().invalidate();
        return "/index?faces-redirect=true";
    }
}
