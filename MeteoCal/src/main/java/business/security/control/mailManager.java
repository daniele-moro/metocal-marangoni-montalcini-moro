/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.security.control;


import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Message.RecipientType;

/**
 *
 * @author Daniele Moro
 */
@Stateless
public class mailManager {
    
   @Resource (name = "mail/SendMail")
   private Session mailSession;
   
    public void sendMail (String destination, String subject, String text){
        Message msg = new MimeMessage(mailSession);
        try {
            msg.setSubject(subject);
            msg.setRecipient(RecipientType.TO, new InternetAddress(destination));
            msg.setText(text);
            Transport.send(msg);
            System.out.println("MAIL INVIATA!");
        }
        catch(MessagingException me) {
            System.out.println("EXCEPTION: "+me.getMessage());
        }
    }
 }
