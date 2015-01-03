/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.entity;

import business.security.control.PasswordEncrypter;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity(name = "USER")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message = "invalid email")
    @NotNull(message = "May not be empty")
    private String email;
   
    @NotNull(message = "May not be empty")
    private String name;
    
    @NotNull(message = "May not be empty")
    private String surname;
    
    @NotNull(message = "May not be empty")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date birthday; 
    
    private String phoneNumber;
    
    private String residenceTown;
    
    private boolean calendarPublic; 
    
    @NotNull(message = "May not be empty")
    private String password;
    
    @NotNull(message = "May not be empty")
    private String groupName;
   
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = PasswordEncrypter.encryptPassword(password);
    }

    public String getSurname() {
        return surname;
    }

    
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getResidenceTown() {
        return residenceTown;
    }

    public void setResidenceTown(String residenceTown) {
        this.residenceTown = residenceTown;
    }

    public Date getBirthday() {
        return birthday;
    }

    
    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    
    public boolean isCalendarPublic() {
        return calendarPublic;
    }

    public void setCalendarPublic(boolean calendarPublic) {
        this.calendarPublic = calendarPublic;
    }

}
