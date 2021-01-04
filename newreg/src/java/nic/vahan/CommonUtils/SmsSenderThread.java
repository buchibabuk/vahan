/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.CommonUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import nic.vahan.db.TableConstants;
import static nic.vahan.server.CommonUtils.vahanEncryption;
import nic.vahan.server.ServerUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 *
 * @author kaptan singh
 */
public class SmsSenderThread implements Runnable {

    private String mobileNo = null;
    private String smsContent = null;
    private String stateCd = null;
    private String smsURL = null;

    public String getStateCd() {
        return stateCd;
    }

    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    public SmsSenderThread(String mobileNo, String smsContent, String stateCd) {
        this.mobileNo = mobileNo;
        this.smsContent = smsContent;
        this.stateCd = stateCd;

    }

    public synchronized void sendMessage() throws MalformedURLException, IOException {
        try {
            mobileNo = mobileNo.replaceAll("[^0-9]", "");
            if (!mobileNo.equalsIgnoreCase("")) {
                Boolean blnSendSms = Long.parseLong(mobileNo) > 999999999 ? true : false;
                if (blnSendSms) {
                    smsContent = smsContent.replaceAll(" ", "%20");
                    String s1 = "mobileno=" + mobileNo + "|" + "message=" + smsContent + "|" + "prjname=" + "VAHAN4" + "|" + "statecd=" + stateCd;
                    String s2 = vahanEncryption(s1);
                    HttpClient client = new HttpClient();
                    PostMethod method = null;
                    client.getParams().setParameter("http.useragent", "Test Client");
                    smsURL = ServerUtil.getVahanPgiUrl(TableConstants.SMS_URL);
                    method = new PostMethod(smsURL);
                    //URL httpUrl = new URL("http://10.246.40.176:7081/SMS/proactivesmsNew?encdata=" + smsString);
                    method.addParameter("encdata", s2);
                    method.addParameter("return_url", "XXXX");
                    client.executeMethod(method);

                }
            }
        } catch (Exception ex) {
            System.out.println("Send SMS :" + ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void run() {
        try {
            sendMessage();
        } catch (MalformedURLException ex) {
        } catch (IOException ex) {
        }
    }
}
