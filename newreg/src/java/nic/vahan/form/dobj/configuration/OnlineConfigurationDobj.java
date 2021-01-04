/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.configuration;

import java.io.Serializable;

/**
 *
 * @author DELL
 */
public class OnlineConfigurationDobj implements Serializable {
    
    private String stateCd;
    private boolean docUpload;
    private String purCd;

    public String getStateCd() {
        return stateCd;
    }

    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    public boolean isDocUpload() {
        return docUpload;
    }

    public void setDocUpload(boolean docUpload) {
        this.docUpload = docUpload;
    }

    public String getPurCd() {
        return purCd;
    }

    public void setPurCd(String purCd) {
        this.purCd = purCd;
    }
    
}
