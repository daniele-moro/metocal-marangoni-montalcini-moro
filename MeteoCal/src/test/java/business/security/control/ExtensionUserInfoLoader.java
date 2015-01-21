/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package business.security.control;

import business.security.entity.Group;
import business.security.entity.Users;
import java.util.Date;
import javax.ejb.Stateless;

/**
 *
 * @author DanieleMarangoni
 */
@Stateless
public class ExtensionUserInfoLoader extends UserInformationLoader {
    
    static final String loggedUserEmail = "user1@mail.it";

    
    public ExtensionUserInfoLoader() {
    
    }
    
    @Override
    public Users getLoggedUser() {
        return super.em.find(Users.class, loggedUserEmail);
    }
    
}
