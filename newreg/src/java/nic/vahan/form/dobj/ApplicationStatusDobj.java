/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

public class ApplicationStatusDobj implements Serializable{
    private String colourCode;
    private String seatCd;
    private String descr;
    
    /**
     * @return the colourCode
     */
    public String getColourCode() {
        return colourCode;
    }

    /**
     * @param colourCode the colourCode to set
     */
    public void setColourCode(String colourCode) {
        this.colourCode = colourCode;
    }

    /**
     * @return the seatCd
     */
    public String getSeatCd() {
        return seatCd;
    }

    /**
     * @param seatCd the seatCd to set
     */
    public void setSeatCd(String seatCd) {
        this.seatCd = seatCd;
    }

    /**
     * @return the descr
     */
    public String getDescr() {
        return descr;
    }

    /**
     * @param descr the descr to set
     */
    public void setDescr(String descr) {
        this.descr = descr;
    }
}
