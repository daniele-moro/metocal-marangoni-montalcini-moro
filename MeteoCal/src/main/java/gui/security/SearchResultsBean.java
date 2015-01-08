/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.security;

import business.security.boundary.SearchManager;
import business.security.entity.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Named
@ViewScoped
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
    
   // private User user; 
    
    private List<User> partialResults; 

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

    /**
     * Metodo per caricare gli utenti che corrispondono ai campi di ricerca (nome e cognome)
     * @param actionEvent 
     */
    public void showPartialResults(ActionEvent actionEvent) {
        partialResults = searchManager.findUsersFromNameSurname(name, surname); 
    }
    
    /**
     * Metodo per verificare che se c'è un solo risultato nella ricerca per nome e cognome,
     * si passa subito alla pagina successiva
     * @return next page ""=this page
     */
    public String showUserProfileResult(){
        if(partialResults.size() == 1) {
            return "userProfile?faces-redirect=true&amp;email="+partialResults.get(0).getEmail();
        }
        return "";
    }

    /**
     * Metodo usato per caricare il profilo dell'utente
     * @return 
     */
    public String showUserProfile() {
        System.out.println("dentro al search bean prima della chiamata a search manager" );
        User u= searchManager.findUser(this.email);
        if(u!=null){
            System.out.println("dentro al search bean dopo chiamata" );
            return "userProfile?faces-redirect=true&amp;email="+u.getEmail();
        }
        return "";
        
        
    }
    
    public String showUserProfile(User u) {
        String mail=u.getEmail();
        return "userProfile?faces-redirect=true&amp;email="+mail;
    }
    
    public String navigateTo() {
        return "home?faces-redirect=true";
    }
    
    private void cleanFields(){
        partialResults = null; 
        email = null; 
        name = null; 
        surname = null;
    }
    
    /**
     * @return the partialResults
     */
    public List<User> getPartialResults() {
        return partialResults;
    }
   

    
}