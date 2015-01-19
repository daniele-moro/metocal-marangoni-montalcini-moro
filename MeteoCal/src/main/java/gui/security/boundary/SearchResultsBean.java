/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.security.boundary;

import business.security.control.SearchManager;
import business.security.entity.Users;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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
    
   // private Users user; 
    
    private List<Users> partialResults; 

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
        //Se la lista del risultato è vuota, visualizzo un messaggio di informatione
        if(partialResults==null || partialResults.isEmpty()){
            FacesMessage message;
            message = new FacesMessage("No results","");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
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
        Users u= searchManager.findUser(this.email);
        if(u!=null){
            System.out.println("dentro al search bean dopo chiamata" );
            return "userProfile?faces-redirect=true&amp;email="+u.getEmail();
        } else {
            FacesMessage message;
            message = new FacesMessage("Error","Mail inexistent");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return "";
        } 
    }
    
    public String showUserProfile(Users u) {
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
    public List<Users> getPartialResults() {
        return partialResults;
    }
   

    
}