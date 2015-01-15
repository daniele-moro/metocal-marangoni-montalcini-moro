package business.security.entity;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@RequestScoped
@FacesValidator("uniqueEmailValidator")
public class UniqueEmailValidator implements Validator, Serializable {

    @PersistenceContext
    protected EntityManager em;
    
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        
        boolean isValid = false;
        
        try {
            Query qUser = em.createQuery("SELECT u FROM USER u WHERE u.email =?1");
            qUser.setParameter(1, (String) value);
            qUser.getSingleResult();
        } catch (NoResultException ex) {
            isValid = true; // No result means unique validation was OK!
        }
        if (!isValid) {
            throw new ValidatorException(new FacesMessage(
                FacesMessage.SEVERITY_ERROR, "Email is already in use.", null));
        }
        }
}
