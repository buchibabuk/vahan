/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.CommonUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.Security;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import org.apache.log4j.Logger;

/**
 *
 * @author kaptan singh
 */
public class MailSenderThread implements Runnable {

    private static Logger LOGGER = Logger.getLogger(MailSenderThread.class);
    static ResourceBundle rb = ResourceBundle.getBundle("myapp", Locale.getDefault());

    public String getKeyValue(String key) {
        String tempKey = rb.getString(key);
        return ((tempKey == null) ? "" : tempKey);
    }
    private String recipients[];
    private String subject;
    private String message;

    public MailSenderThread(String recipients[], String subject, String message) {
        this.recipients = recipients;
        this.subject = subject;
        this.message = message;

    }

    public synchronized void sendMail() throws MalformedURLException, IOException {
        try {
            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            Properties props = new Properties();

            props.put("mail.smtp.host", getKeyValue("smtp.host"));
            props.put("mail.smtp.auth", getKeyValue("smtp.auth"));
            props.put("mail.debug", Boolean.parseBoolean(getKeyValue("mail.debug")));
            props.put("mail.smtp.port", getKeyValue("smtp.port"));
            props.put("mail.smtp.socketFactory.port", getKeyValue("smtp.port"));
//        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");

            Session session = Session.getDefaultInstance(props);

            session.setDebug(Boolean.parseBoolean(getKeyValue("mail.debug")));

            //construct the mime message
            MimeMessage mimeMessage = new MimeMessage(session);

            //from
            InternetAddress iaSender = new InternetAddress(getKeyValue("fromMailId"), getKeyValue("name"));
            mimeMessage.setFrom(iaSender);

            // To
            InternetAddress[] iToAddrs = InternetAddress.parse(recipients[0]);
            mimeMessage.setRecipients(Message.RecipientType.TO, iToAddrs);

            //subject
            mimeMessage.setSubject(subject);

            // Text
            StringBuffer sb = new StringBuffer();
            sb.append("<HTML>\n");
            sb.append("<HEAD>\n");
            sb.append("<TITLE>" + subject + "</TITLE>\n");
            sb.append("</HEAD>\n");
            sb.append("<BODY style=\"color:#1F497D; font-size:11.0pt;\">" + message + "</BODY>\n");
            sb.append("</HTML>\n");

            message = sb.toString();

            // Body
            String mailer = "sendhtml";
            mimeMessage.setHeader("X-Mailer", mailer);
            mimeMessage.setDataHandler(new DataHandler(new ByteArrayDataSource(message, "text/html")));

            // Set the sent date
            mimeMessage.setSentDate(new Date());

            //commit changes
            mimeMessage.saveChanges();

            // send mail via SMTP transport
            Transport.send(mimeMessage);

        } catch (AddressException ex) {
            LOGGER.error("Invalid Address:" + ex.toString() + " " + ex.getStackTrace()[0]);
        } catch (MessagingException ex) {
            LOGGER.error("Mail sending fails:" + ex.toString() + " " + ex.getStackTrace()[0]);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }

    }

    public void run() {
        try {
            sendMail();
        } catch (MalformedURLException ex) {
            LOGGER.error("Invalid Address:" + ex);
        } catch (IOException ex) {
            LOGGER.error("Mail sending fails:" + ex);
        }
    }
}
