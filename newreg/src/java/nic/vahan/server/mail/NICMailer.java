/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.server.mail;

/**
 *
 * @author tranC084
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author tranC074
 */
import com.sun.mail.util.MailSSLSocketFactory;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

public class NICMailer {

    public static final String MAIL_SERVER = "mail.nic.in";
    //public static final String USERNAME = "username@nic.in";
    public static final String USERNAME = "nic-odi";
    //public static final String PASSWORD = "password";
    public static final String PASSWORD = "*Odi13idO";
    public static final String USERNAME_CTAX = "username@nic.in";
    public static final String PASSWORD_CTAX = "password";

    public NICMailer() {
    }

    public static String doSendMail(String mmsg, String toAddress, String subject) throws Exception {
        String status = "N";
        try {
            String fromAddress = "nic-odi";
            String message = "" + mmsg;
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", MAIL_SERVER);
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.socketFactory", sf);
            //   properties.put("mail.smtp.ssl.socketFactory", "javax.net.ssl.SSLSocketFactory");
            Session session = Session.getInstance(properties);
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromAddress));
            msg.addRecipients(Message.RecipientType.TO, toAddress);
            msg.setSubject(subject);
            msg.setText(message);
            Transport tr = session.getTransport("smtp");
            tr.connect(MAIL_SERVER, USERNAME, PASSWORD);
            tr.sendMessage(msg, msg.getAllRecipients());
            tr.close();
            status = "Y";
        } catch (AddressException ex) {
            System.out.println(ex.getMessage());
            throw new Exception("Address not Valid");
        } catch (MessagingException ex) {
            System.out.println(ex.getMessage());
            throw new Exception("Mail sending fail " + ex.getMessage());
        } catch (Exception e) {
            throw new Exception("Mail sending fail " + e.getMessage());
        }
        return status;
    }

    public String sendEmailHtml(String toAddress, String msgs, String email_sub) throws Exception {
        String status = "N";
        try {
            String fromAddress = "username";
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", MAIL_SERVER);
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.socketFactory", sf);
            Session session = Session.getDefaultInstance(properties);// Get the default Session object.
            MimeMessage message = new MimeMessage(session);// Create a default MimeMessage object.
            message.setFrom(new InternetAddress(fromAddress));// Set From: header field of the header.
            message.addRecipients(Message.RecipientType.TO, toAddress);
            message.setSubject(email_sub);// Set Subject: header field
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(msgs, "text/html");// Send the actual HTML message, as big as you like
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);
            Transport tr = session.getTransport("smtp");
            tr.connect(MAIL_SERVER, USERNAME_CTAX, PASSWORD_CTAX);
            tr.sendMessage(message, message.getAllRecipients());// Send message
            tr.close();
            status = "Y";
        } catch (AddressException ex) {
            throw ex;
        } catch (MessagingException ex) {
            throw ex;
        } catch (Exception e) {
            throw e;
        }
        return status;
    }

    public String sendEmailAttch(String toAddress, String msgs, String email_sub, String path, String filename) throws Exception {
        String status = "";
        String from = "username@nic.in";
        try {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            // Get system properties
            Properties properties = System.getProperties();
            // Setup mail server
            properties.put("mail.smtp.host", "mail.nic.in");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.socketFactory", sf);
            // Get the default Session object.
            Session session = Session.getInstance(properties);
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);
            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));
            // Set To: header field of the header.
            //message.addRecipient(Message.RecipientType.TO,new InternetAddress(toAddress));
            message.addRecipients(Message.RecipientType.TO, toAddress);
            // Set Subject: header field
            message.setSubject(email_sub);
            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();
            // Fill the message
            messageBodyPart.setText(msgs);
            // Create a multipar message
            Multipart multipart = new MimeMultipart();
            // Set text message part
            multipart.addBodyPart(messageBodyPart);
            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            // String filename = "file.txt";
            DataSource source = new FileDataSource(path + "/" + filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);
            // Send the complete message parts
            message.setContent(multipart);
            Transport tr = session.getTransport("smtp");
            tr.connect(MAIL_SERVER, USERNAME_CTAX, PASSWORD_CTAX);
            // Send message
            tr.sendMessage(message, message.getAllRecipients());
            tr.close();
            System.out.println("Sent message successfully....");
            status = "sucess";
        } catch (Exception e) {
            throw e;
        } finally {
        }
        return status;
    }
}
