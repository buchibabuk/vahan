/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db;

/**
 *
 * @author acer
 */
public interface DocumentType {

    int RC_QR = 10;
    int RECEIPT_QR = 11;
    int FC_QR_38 = 12;
    int FC_QR_38A = 17;
    int NOC_QR = 13;
    int RCPT_CANCEL_QR = 14;
    int NOC_CANCEL_QR = 15;
    int RC_CANCEL_QR = 16;
    String FORM_38A_QR = "38A";
    String FORM_38_QR = "38";
}
