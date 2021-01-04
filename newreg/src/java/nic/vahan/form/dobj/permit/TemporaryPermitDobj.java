/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Administrator
 */
public class TemporaryPermitDobj implements Cloneable, Serializable {

    private String regn_no;
    private Date valid_from;
    private Date valid_upto;
    private String route_fr;
    private String route_to;
    private String prv_valid_fr;
    private String prv_valid_to;
    private String purpose;
    private int off_cd;
    private int pur_cd;
    private String state_cd;
    private String period_mode;
    private String period_in_no;
    private String region_covered;
    private String pmt_no;
    private String via;
    private String goods_to_carry;
    private String pmt_type;
    private String pmt_catg;
    private int route_length;
    private int service_type;

    public Date getValid_from() {
        return valid_from;
    }

    public void setValid_from(Date valid_from) {
        this.valid_from = valid_from;
    }

    public Date getValid_upto() {
        return valid_upto;
    }

    public void setValid_upto(Date valid_upto) {
        this.valid_upto = valid_upto;
    }

    public String getRoute_fr() {
        return route_fr;
    }

    public void setRoute_fr(String route_fr) {
        this.route_fr = route_fr;
    }

    public String getRoute_to() {
        return route_to;
    }

    public void setRoute_to(String route_to) {
        this.route_to = route_to;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getPrv_valid_fr() {
        return prv_valid_fr;
    }

    public void setPrv_valid_fr(String prv_valid_fr) {
        this.prv_valid_fr = prv_valid_fr;
    }

    public String getPrv_valid_to() {
        return prv_valid_to;
    }

    public void setPrv_valid_to(String prv_valid_to) {
        this.prv_valid_to = prv_valid_to;
    }

    public String getPeriod_in_no() {
        return period_in_no;
    }

    public void setPeriod_in_no(String period_in_no) {
        this.period_in_no = period_in_no;
    }

    public String getPeriod_mode() {
        return period_mode;
    }

    public void setPeriod_mode(String period_mode) {
        this.period_mode = period_mode;
    }

    public String getRegion_covered() {
        return region_covered;
    }

    public void setRegion_covered(String region_covered) {
        this.region_covered = region_covered;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the off_cd
     */
    public int getOff_cd() {
        return off_cd;
    }

    /**
     * @param off_cd the off_cd to set
     */
    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    /**
     * @return the pur_cd
     */
    public int getPur_cd() {
        return pur_cd;
    }

    /**
     * @param pur_cd the pur_cd to set
     */
    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    /**
     * @return the state_cd
     */
    public String getState_cd() {
        return state_cd;
    }

    /**
     * @param state_cd the state_cd to set
     */
    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public String getPmt_no() {
        return pmt_no;
    }

    public void setPmt_no(String pmt_no) {
        this.pmt_no = pmt_no;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String getGoods_to_carry() {
        return goods_to_carry;
    }

    public void setGoods_to_carry(String goods_to_carry) {
        this.goods_to_carry = goods_to_carry;
    }

    public String getPmt_type() {
        return pmt_type;
    }

    public void setPmt_type(String pmt_type) {
        this.pmt_type = pmt_type;
    }

    public String getPmt_catg() {
        return pmt_catg;
    }

    public void setPmt_catg(String pmt_catg) {
        this.pmt_catg = pmt_catg;
    }

    /**
     * @return the route_length
     */
    public int getRoute_length() {
        return route_length;
    }

    /**
     * @param route_length the route_length to set
     */
    public void setRoute_length(int route_length) {
        this.route_length = route_length;
    }

    /**
     * @return the service_type
     */
    public int getService_type() {
        return service_type;
    }

    /**
     * @param service_type the service_type to set
     */
    public void setService_type(int service_type) {
        this.service_type = service_type;
    }
}