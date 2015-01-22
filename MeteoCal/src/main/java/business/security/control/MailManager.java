package business.security.control;


import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Message.RecipientType;

@Stateless
public class MailManager {
    
   @Resource (name = "mail/sendMail")
   private Session mailSession;
   
   /**
    * It execute the email sends
    * @param destination
    * @param subject
    * @param text 
    */
   @Asynchronous
    public void sendMail (String destination, String subject, String text){
        System.out.println("--------------------------------SPEDIZIONE MAIL!!!!!!-------------------------------------");
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
