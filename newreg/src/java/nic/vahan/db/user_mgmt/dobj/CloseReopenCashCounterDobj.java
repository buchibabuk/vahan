/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.dobj;

import java.io.Serializable;

/**
 *
 * @author NIC
 */
public class CloseReopenCashCounterDobj implements Serializable {

    private String user_id;
    private String user_name;
    private boolean close_cash;
    private String user_cd;
    

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public boolean isClose_cash() {
        return close_cash;
    }

    public void setClose_cash(boolean close_cash) {
        this.close_cash = close_cash;
    }

    public String getUser_cd() {
        return user_cd;
    }

    public void setUser_cd(String user_cd) {
        this.user_cd = user_cd;
    }

//    public boolean isOpen_all_counter() {
//        return open_all_counter;
//    }
//
//    public void setOpen_all_counter(boolean open_all_counter) {
//        this.open_all_counter = open_all_counter;
//    }
}
