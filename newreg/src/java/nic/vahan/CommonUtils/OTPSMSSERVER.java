/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.CommonUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import nic.vahan.db.TableConstants;
import nic.vahan.server.ServerUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpStatus;
//import org.apache.commons.httpclient.methods.PostMethod;

/**
 *
 * @author Administrator
 */
public class OTPSMSSERVER extends Thread {

    private static final Logger LOGGER = Logger.getLogger(OTPSMSSERVER.class);
    String mobile;
    String smsMessage;
    private String stateCd = null;
    private String otpURL = null;

    public String getStateCd() {
        return stateCd;
    }

    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    private OTPSMSSERVER() {
    }

    public OTPSMSSERVER(String mobile, String smsMessage, String stateCd) {
        this.mobile = mobile;
        this.smsMessage = smsMessage;
        this.stateCd = stateCd;


    }

    public void run() {
        sendAppApprovalSMS(mobile, smsMessage);

    }
    BufferedReader br = null;

    private String smsEncryption(String strVal) {
        StringBuffer encVal = new StringBuffer();
        char ch[] = strVal.toCharArray();
        for (char c : ch) {
            encVal.append(Integer.toHexString((byte) c));
        }
        return encVal.toString();
    }

    private void sendAppApprovalSMS(String mobile, String smsMessage) {
        try {

            if (mobile != null && mobile.length() == 10) {
                String s1 = "mobileno=" + mobile.trim() + "|message=" + smsMessage + "|" + "prjname=" + "VAHAN4" + "|" + "statecd=" + stateCd;
                String s2 = smsEncryption(s1);
                HttpClient client = new HttpClient();
                PostMethod method = null;

                String readLine = "";
                client.getParams().setParameter("http.useragent", "Test Client");
                otpURL = ServerUtil.getVahanPgiUrl(TableConstants.OTP_URL);
                method = new PostMethod(otpURL);
                // method = new PostMethod("http://10.246.40.176:7080/OTP/proactivesmsNew");

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
            System.out.println("Send OTP :" + ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                }
            }
        }
        //end
    }
//    public static void main(String args[]) {
//
//        OTPSMSSERVER sm = new OTPSMSSERVER("8527971919", "Test");
//        sm.start();
//        // sendAppApprovalSMS(mobile,smsMessage);
//        //sendAppApprovalSMS("8527803348", "Test");
//    }
}
