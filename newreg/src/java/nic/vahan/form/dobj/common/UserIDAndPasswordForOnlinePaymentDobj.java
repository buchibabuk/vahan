/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.common;

/**
 *
 * @author Divya Kamboj
 */
public class UserIDAndPasswordForOnlinePaymentDobj {

    private String user_pwd;
    private long user_cd;
    private String appl_no;
    private String transactionNo;

    public String getUser_pwd() {
        return user_pwd;
    }

    public void setUser_pwd(String user_pwd) {
        this.user_pwd = user_pwd;
    }

    public long getUser_cd() {
        return user_cd;
    }

    public void setUser_cd(long user_cd) {
        this.user_cd = user_cd;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }
}
