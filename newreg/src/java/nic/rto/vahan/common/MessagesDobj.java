/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.rto.vahan.common;

/**
 *
 * @author ASHOK
 */
public class MessagesDobj {

    private String smartCardStatus = "";
    private String hsrpStatus = "";

    /**
     * @return the smartCardStatus
     */
    public String getSmartCardStatus() {
        return smartCardStatus;
    }

    /**
     * @param smartCardStatus the smartCardStatus to set
     */
    public void setSmartCardStatus(String smartCardStatus) {
        this.smartCardStatus = smartCardStatus;
    }

    /**
     * @return the hsrpStatus
     */
    public String getHsrpStatus() {
        return hsrpStatus;
    }

    /**
     * @param hsrpStatus the hsrpStatus to set
     */
    public void setHsrpStatus(String hsrpStatus) {
        this.hsrpStatus = hsrpStatus;
    }
}
