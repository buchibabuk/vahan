/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import nic.java.util.UtilsException;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.server.ServerUtil;
import nic.vahan.server.mail.MailSender;

/**
 *
 * @author acer
 */
public class SmsMailOTPImpl {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(SmsMailOTPImpl.class);

    //For sending OTP and Mail while Cancel Receipt and Dispose Application
    public static String sendOTPorMail(String userCd, String subject, String requestType, String prevOtp, String cancelType) throws VahanException {
        String emailId = "";
        String otp_msg = "";
        String otp = null;
        String mobileNo = "";

        try {
            if (requestType.equalsIgnoreCase("sendOtp")) {
                String[] userData = new RegVehCancelReceiptImpl().getUserDetails(userCd).split(",");
                if (userData != null && !userData.toString().equals("") && userData.length == 2) {
                    mobileNo = userData[0];
                    emailId = userData[1];
                    if (mobileNo != null && !mobileNo.equals("")) {
                        otp = new ServerUtil().genOTP(mobileNo);
                        otp_msg = otp + ": " + cancelType + " Valid for one time use. Do not share it with anyone.";
                        ServerUtil.sendOTP(mobileNo, otp_msg, Util.getUserStateCode());
                    }
                    if (emailId != null && !emailId.equals("")) {
                        otp_msg = otp + ": " + cancelType + " Valid for one time use. Do not share it with anyone. This is system generated email please don't reply.";
                        MailSender sendMail = new MailSender(emailId, otp_msg, subject);
                        sendMail.start();
                    }
                }
            } else if (requestType.equals("resendOtp")) {
                otp = prevOtp;
                otp_msg = otp + ": " + cancelType + " Valid for one time use. Do not share it with anyone.";
                ServerUtil.sendOTP(mobileNo, otp_msg, Util.getUserStateCode());
                otp_msg = otp + ": " + cancelType + " Valid for one time use. Do not share it with anyone. This is system generated email please don't reply.";
                MailSender sendMail = new MailSender(emailId, otp_msg, subject);
                sendMail.start();
            }
        } catch (VahanException ve) {
            otp = null;
            throw ve;
        } catch (Exception e) {
            otp = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return otp;
    }
}
