/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.login.model;

/**
 *
 * @author Kartikey Singh
 */
public class CustomLoginBeanModel {

    private String userId;
    private String applNo;
    private int randNo;
    private String casTicket;

    public CustomLoginBeanModel() {
        this.randNo = (int) (Math.random() * 1000 * 1000 * 1000);
    }

    public CustomLoginBeanModel(String userId, String applNo) {
        this.userId = userId;
        this.applNo = applNo;
        this.randNo = (int) (Math.random() * 1000 * 1000 * 1000);
    }

    public CustomLoginBeanModel(String userId, String applNo, String casTicket) {
        this.userId = userId;
        this.applNo = applNo;
        this.casTicket = casTicket;
        this.randNo = (int) (Math.random() * 1000 * 1000 * 1000);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    public int getRandNo() {
        return randNo;
    }

    public void setRandNo(int randNo) {
        this.randNo = randNo;
    }

    public String getCasTicket() {
        return casTicket;
    }

    public void setCasTicket(String casTicket) {
        this.casTicket = casTicket;
    }

    @Override
    public String toString() {
        return "CustomLoginBeanModel{" + "userId=" + userId + ", applNo=" + applNo + ", randNo=" + randNo + ", casTicket=" + casTicket + '}';
    }
}
