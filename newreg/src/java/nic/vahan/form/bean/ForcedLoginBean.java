/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.vahan.form.impl.Util;

/**
 *
 * @author tranC103
 */
@ManagedBean
@ViewScoped
public class ForcedLoginBean {

    private String ipAddress = "";

    public ForcedLoginBean() {
//        ipAddress = Util.getIpAddress();
    }

    /**
     * @return the ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * @param ipAddress the ipAddress to set
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
