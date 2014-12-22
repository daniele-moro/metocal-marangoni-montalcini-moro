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
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Named
@RequestScoped
public class SearchResultsBean {
    
    @EJB 
    private SearchManager searchManager; 
    
    @NotNull(message = "May not be empty")
    private String name; 
    
    @NotNull(message = "May not be empty")
    private String surname; 
    
    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message = "invalid email")
    private String email; 
    
    
    public List<NameSurnameEmail> getSearchedUsers() {
        return searchManager.getSearchedUsers();
    }

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
    
     public User getSearchedUser() {
        return searchManager.getSearchedUser();
    }

    
    
    public String showSearchResults() {
        searchManager.loadSearchedUser(name, surname);
        if(searchManager.getSearchedUsers().size() == 1) {
            searchManager.searchedUserProfile(searchManager.getSearchedUsers().get(0).getEmail());
            return "userProfile?faces-redirect=true";
        } 
        return "searchForUserResult?faces-redirect=true";
        
    }
    
    public String showUserProfile() {
        System.out.println("dentro al search bean prima della chiamata a search manager" );
        searchManager.searchedUserProfile(this.email);
        System.out.println("dentro al search bean dopo chiamata" );
        return "userProfile?faces-redirect=true";
        
    }
    
    public String showUserProfile(String email) {
        searchManager.searchedUserProfile(email);
        return "userProfile?faces-redirect=true";
    }
    
    public String navigateTo() {
        searchManager.setSearchedUsers(new ArrayList<>()); 
        searchManager.setSearchedUser(new User());
        return "home?faces-redirect=true";
    }

    
   

    
}