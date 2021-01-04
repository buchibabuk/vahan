/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author DELL
 */
public class VehicleTrackingDetailsDobj implements Serializable {

    private String regn_no;
    private String chasi_no;
    private String appl_no;
    private String device_sr_no;
    private String imei_no;
    private String icc_id;
    private String activation_rcpt_no;
    private Date device_activation_date;
    private Date device_activated_upto;
    private String device_activation_status;
    private String vltd_manufacturer;
    private Date fitted_date;
    private String fitment_center;

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getDevice_sr_no() {
        return device_sr_no;
    }

    public void setDevice_sr_no(String device_sr_no) {
        this.device_sr_no = device_sr_no;
    }

    public String getImei_no() {
        return imei_no;
    }

    public void setImei_no(String imei_no) {
        this.imei_no = imei_no;
    }

    public String getIcc_id() {
        return icc_id;
    }

    public void setIcc_id(String icc_id) {
        this.icc_id = icc_id;
    }

    public String getActivation_rcpt_no() {
        return activation_rcpt_no;
    }

    public void setActivation_rcpt_no(String activation_rcpt_no) {
        this.activation_rcpt_no = activation_rcpt_no;
    }

    public Date getDevice_activation_date() {
        return device_activation_date;
    }

    public void setDevice_activation_date(Date device_activation_date) {
        this.device_activation_date = device_activation_date;
    }

    public Date getDevice_activated_upto() {
        return device_activated_upto;
    }

    public void setDevice_activated_upto(Date device_activated_upto) {
        this.device_activated_upto = device_activated_upto;
    }

    public String getDevice_activation_status() {
        return device_activation_status;
    }

    public void setDevice_activation_status(String device_activation_status) {
        this.device_activation_status = device_activation_status;
    }

    public String getVltd_manufacturer() {
        return vltd_manufacturer;
    }

    public void setVltd_manufacturer(String vltd_manufacturer) {
        this.vltd_manufacturer = vltd_manufacturer;
    }

    public Date getFitted_date() {
        return fitted_date;
    }

    public void setFitted_date(Date fitted_date) {
        this.fitted_date = fitted_date;
    }

    public String getFitment_center() {
        return fitment_center;
    }

    public void setFitment_center(String fitment_center) {
        this.fitment_center = fitment_center;
    }

    public String getChasi_no() {
        return chasi_no;
    }

    public void setChasi_no(String chasi_no) {
        this.chasi_no = chasi_no;
    }
}
