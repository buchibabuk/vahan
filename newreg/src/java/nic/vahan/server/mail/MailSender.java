package nic.vahan.server.mail;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//import com.sun.istack.internal.ByteArrayDataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import javax.faces.application.FacesMessage;
import nic.vahan.db.TableConstants;
import nic.vahan.server.ServerUtil;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

/**
 *
 * @author Rahul
 */
public class MailSender extends Thread {

    private static final Logger LOGGER = Logger.getLogger(MailSender.class);
    String TO = "";
    String MESSAGE = "";
    String SUBJECT = "";
    private String emailURL = null;

    public MailSender() {
    }

    public MailSender(String to, String message, String subject) {
        this.TO = to;
        this.MESSAGE = message;
        this.SUBJECT = subject;
    }

    public MailSender(String to, String message, String subject, FacesMessage facesMessage) {
        this.TO = to;
        this.MESSAGE = message;
        this.SUBJECT = subject;
    }

    public void run() {
        try {
            sendmail(TO, MESSAGE, SUBJECT);    ///// Do NOT WANT TO show error message on GUI form during Mail Sending
        } catch (Exception ex) {
        }
    }

    private static String mailEncryption(String strVal) {
        StringBuffer encVal = new StringBuffer();
        char ch[] = strVal.toCharArray();
        for (char c : ch) {
            encVal.append(Integer.toHexString((byte) c));
        }
        return encVal.toString();
    }

    private void sendmail(String mailID, String mailMessage, String subject) {
        BufferedReader br = null;
        try {

            //sms test
            if (mailID != null && !mailID.equalsIgnoreCase("")) {
                // LOGGER.info("sms test(message) start" + smsMessage);
                String s1 = "mail_id=" + mailID.trim() + "|message=" + mailMessage + "|subject=" + subject;
                String s2 = mailEncryption(s1);
                HttpClient client = new HttpClient();
                PostMethod method = null;

                String readLine = "";
                client.getParams().setParameter("http.useragent", "Test Client");

                ////////////////NORMAL Mail URL ////////
                //method = new PostMethod(MsgProperties.getKeyValue("mail.server.url"));
                emailURL = ServerUtil.getVahanPgiUrl(TableConstants.EMAIL_URL);
                method = new PostMethod(emailURL);

                //method = new PostMethod("http://10.248.93.4:7080/OTP/proactivemail");  //from::nic-vahandl@nic.in

                method.addParameter("encdata", s2);    // "CG04CC5788"
                method.addParameter("return_url", "XXXX");
                int returnCode = client.executeMethod(method);
                if (returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
                    method.getResponseBodyAsString();
                } else {
                    br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
                    String temp;
                    while (((temp = br.readLine()) != null)) {
                        readLine = readLine + temp;
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                }
            }
        }

    }

    /**
     * @return the subject
     */
    public static void main(String args[]) throws GeneralSecurityException {
        //      new MailSender().sendmail("rahulbca18@gmail.com", "test", "test message");
    }
}
