/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.smartcard;

import java.io.Serializable;

/**
 *
 * @author tranC075
 */
public class SmartCardIpDobj implements Serializable {

    private String empCd = "";
    private String ip = "";

    /**
     * @return the empCd
     */
    public String getEmpCd() {
        return empCd;
    }

    /**
     * @param empCd the empCd to set
     */
    public void setEmpCd(String empCd) {
        this.empCd = empCd;
    }

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }
}
