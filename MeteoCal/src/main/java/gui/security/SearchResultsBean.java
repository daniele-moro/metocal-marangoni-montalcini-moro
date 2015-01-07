/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.security;

import business.security.boundary.NotificationManager;
import business.security.boundary.SearchManager;
import business.security.entity.User;
import business.security.object.NameSurnameEmail;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Named
@SessionScoped
public class SearchResultsBean implements Serializable {
    
    @EJB 
    private SearchManager searchManager; 
    
    @NotNull(message = "May not be empty")
    private String name; 
    
    @NotNull(message = "May not be empty")
    private String surname; 
    
    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message = "invalid email")
    private String email; 
    
    private User user; 
    
    private List<User> partialResults; 
    
    
    public SearchResultsBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    
    
    public String showSearchResults() {
        partialResults = searchManager.findUsersFromNameSurname(name, surname);
        if(partialResults.size() == 1) {
            user = partialResults.get(0); 
            partialResults.remove(0); 
            return "userProfile?faces-redirect=true";
        } 
        return "searchForUser?faces-redirect=true";
        
    }
    
    public String showUserProfile() {
        System.out.println("dentro al search bean prima della chiamata a search manager" );
        user = searchManager.findUser(this.email);
        System.out.println("dentro al search bean dopo chiamata" );
        return "userProfile?faces-redirect=true";
        
    }
    
    public String showUserProfile(User u) {
        user = u; 
        return "userProfile?faces-redirect=true";
    }
    
    public String navigateTo() {
        partialResults = null; 
        user = null; 
        email = null; 
        name = null; 
        surname = null; 
        return "home?faces-redirect=true";
    }


    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the partialResults
     */
    public List<User> getPartialResults() {
        return partialResults;
    }
   

    
}