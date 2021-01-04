/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.configuration;

import java.io.Serializable;

/**
 *
 * @author AMI
 */
public class TmConfigurationAdvanceRegnDobj implements Serializable {

    private boolean oldNumberReplace;
    private int sizeOldNumber;

    /**
     * @return the oldNumberReplace
     */
    public boolean isOldNumberReplace() {
        return oldNumberReplace;
    }

    /**
     * @param oldNumberReplace the oldNumberReplace to set
     */
    public void setOldNumberReplace(boolean oldNumberReplace) {
        this.oldNumberReplace = oldNumberReplace;
    }

    /**
     * @return the sizeOldNumber
     */
    public int getSizeOldNumber() {
        return sizeOldNumber;
    }

    /**
     * @param sizeOldNumber the sizeOldNumber to set
     */
    public void setSizeOldNumber(int sizeOldNumber) {
        this.sizeOldNumber = sizeOldNumber;
    }
}
