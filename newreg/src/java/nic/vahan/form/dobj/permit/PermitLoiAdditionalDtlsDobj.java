/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;

/**
 *
 * @author R Gautam
 */
public class PermitLoiAdditionalDtlsDobj implements Serializable {

    private String batchNo;
    private String adharCardNo;
    private String dlNo;
    private byte[] image;

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getAdharCardNo() {
        return adharCardNo;
    }

    public void setAdharCardNo(String adharCardNo) {
        this.adharCardNo = adharCardNo;
    }

    public String getDlNo() {
        return dlNo;
    }

    public void setDlNo(String dlNo) {
        this.dlNo = dlNo;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
