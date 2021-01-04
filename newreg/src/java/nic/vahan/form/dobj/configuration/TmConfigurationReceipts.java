/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.configuration;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author DELL
 */
public class TmConfigurationReceipts implements Serializable {

    private String stateCd;
    private String feeAmtZero;
    private String fineAmtZero;
    private Date covid19_from;
    private Date covid19_upto;

    /**
     * @return the stateCd
     */
    public String getStateCd() {
        return stateCd;
    }

    /**
     * @param stateCd the stateCd to set
     */
    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    /**
     * @return the feeAmtZero
     */
    public String getFeeAmtZero() {
        return feeAmtZero;
    }

    /**
     * @param feeAmtZero the feeAmtZero to set
     */
    public void setFeeAmtZero(String feeAmtZero) {
        this.feeAmtZero = feeAmtZero;
    }

    /**
     * @return the fineAmtZero
     */
    public String getFineAmtZero() {
        return fineAmtZero;
    }

    /**
     * @param fineAmtZero the fineAmtZero to set
     */
    public void setFineAmtZero(String fineAmtZero) {
        this.fineAmtZero = fineAmtZero;
    }

    /**
     * @return the covid19_from
     */
    public Date getCovid19_from() {
        return covid19_from;
    }

    /**
     * @param covid19_from the covid19_from to set
     */
    public void setCovid19_from(Date covid19_from) {
        this.covid19_from = covid19_from;
    }

    /**
     * @return the covid19_upto
     */
    public Date getCovid19_upto() {
        return covid19_upto;
    }

    /**
     * @param covid19_upto the covid19_upto to set
     */
    public void setCovid19_upto(Date covid19_upto) {
        this.covid19_upto = covid19_upto;
    }

}
