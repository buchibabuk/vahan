/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.admin;

/**
 *
 * @author Administrator
 */
public class ChangesByUser {

    private String userName;
    private String changed_data;
    private String op_dt;
    private int user;

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the changed_data
     */
    public String getChanged_data() {
        return changed_data;
    }

    /**
     * @param changed_data the changed_data to set
     */
    public void setChanged_data(String changed_data) {
        this.changed_data = changed_data;
    }

    /**
     * @return the op_dt
     */
    public String getOp_dt() {
        return op_dt;
    }

    /**
     * @param op_dt the op_dt to set
     */
    public void setOp_dt(String op_dt) {
        this.op_dt = op_dt;
    }

    /**
     * @return the user
     */
    public int getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(int user) {
        this.user = user;
    }
}
