/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.model;

import java.util.ArrayList;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.Trailer_bean;
import nic.vahan.form.dobj.Trailer_dobj;

/**
 *
 * @author BITTU
 */
public class TrailerBeanModel {
    private String body_type;
    private String chassis_no_trailer;
    private int ld_wt;
    private int unld_wt;
    private String f_axle_descp;
    private String r_axle_descp;
    private String o_axle_descp;
    private String t_axle_descp;
    private int f_axle_weight;
    private int r_axle_weight;
    private int o_axle_weight;
    private int t_axle_weight;
    private ArrayList list_body_type;
    private ArrayList<ComparisonBean> compBeanList = new ArrayList<>();
    private Trailer_dobj trailer_dobj_prv;
    private boolean disable;

    public TrailerBeanModel() {
    }    
    
    public TrailerBeanModel(Trailer_bean trailer_bean) {
        
        this.body_type = trailer_bean.getBody_type();
        this.chassis_no_trailer = trailer_bean.getChassis_no_trailer();
        this.ld_wt = trailer_bean.getLd_wt();
        this.unld_wt = trailer_bean.getUnld_wt();
        this.f_axle_descp = trailer_bean.getF_axle_descp();
        this.r_axle_descp = trailer_bean.getR_axle_descp();
        this.o_axle_descp = trailer_bean.getO_axle_descp();
        this.t_axle_descp = trailer_bean.getT_axle_descp();
        this.f_axle_weight = trailer_bean.getF_axle_weight();
        this.r_axle_weight = trailer_bean.getR_axle_weight();
        this.o_axle_weight = trailer_bean.getO_axle_weight();
        this.t_axle_weight = trailer_bean.getT_axle_weight();
        this.list_body_type = trailer_bean.getList_body_type();
        this.compBeanList = trailer_bean.getCompBeanList();
        this.trailer_dobj_prv = trailer_bean.getTrailer_dobj_prv();
        this.disable = trailer_bean.isDisable();
              
    }

      public Trailer_dobj setTrailerBeanToDobj() {

        Trailer_dobj dobj = new Trailer_dobj();
        //setting the value from form_commercial_trailer_dtls to dobj        
        if (validateForm()) {

            dobj.setBody_type(this.getBody_type());
            dobj.setChasi_no(this.getChassis_no_trailer());
            dobj.setLd_wt(this.getLd_wt());
            dobj.setUnld_wt(this.getUnld_wt());
            dobj.setF_axle_descp(this.getF_axle_descp());
            dobj.setR_axle_descp(this.getR_axle_descp());
            dobj.setO_axle_descp(this.getO_axle_descp());
            dobj.setT_axle_descp(this.getT_axle_descp());
            dobj.setF_axle_weight(this.getF_axle_weight());
            dobj.setR_axle_weight(this.getR_axle_weight());
            dobj.setO_axle_weight(this.getO_axle_weight());
            dobj.setT_axle_weight(this.getT_axle_weight());

        }

        return dobj;
    }

    private boolean validateForm() {
        // validate is pending here...
        return true;
    }

    public String getBody_type() {
        return body_type;
    }

    public void setBody_type(String body_type) {
        this.body_type = body_type;
    }

    public String getChassis_no_trailer() {
        return chassis_no_trailer;
    }

    public void setChassis_no_trailer(String chassis_no_trailer) {
        this.chassis_no_trailer = chassis_no_trailer;
    }

    public int getLd_wt() {
        return ld_wt;
    }

    public void setLd_wt(int ld_wt) {
        this.ld_wt = ld_wt;
    }

    public int getUnld_wt() {
        return unld_wt;
    }

    public void setUnld_wt(int unld_wt) {
        this.unld_wt = unld_wt;
    }

    public String getF_axle_descp() {
        return f_axle_descp;
    }

    public void setF_axle_descp(String f_axle_descp) {
        this.f_axle_descp = f_axle_descp;
    }

    public String getR_axle_descp() {
        return r_axle_descp;
    }

    public void setR_axle_descp(String r_axle_descp) {
        this.r_axle_descp = r_axle_descp;
    }

    public String getO_axle_descp() {
        return o_axle_descp;
    }

    public void setO_axle_descp(String o_axle_descp) {
        this.o_axle_descp = o_axle_descp;
    }

    public String getT_axle_descp() {
        return t_axle_descp;
    }

    public void setT_axle_descp(String t_axle_descp) {
        this.t_axle_descp = t_axle_descp;
    }

    public int getF_axle_weight() {
        return f_axle_weight;
    }

    public void setF_axle_weight(int f_axle_weight) {
        this.f_axle_weight = f_axle_weight;
    }

    public int getR_axle_weight() {
        return r_axle_weight;
    }

    public void setR_axle_weight(int r_axle_weight) {
        this.r_axle_weight = r_axle_weight;
    }

    public int getO_axle_weight() {
        return o_axle_weight;
    }

    public void setO_axle_weight(int o_axle_weight) {
        this.o_axle_weight = o_axle_weight;
    }

    public int getT_axle_weight() {
        return t_axle_weight;
    }

    public void setT_axle_weight(int t_axle_weight) {
        this.t_axle_weight = t_axle_weight;
    }

    public ArrayList getList_body_type() {
        return list_body_type;
    }

    public void setList_body_type(ArrayList list_body_type) {
        this.list_body_type = list_body_type;
    }

    public ArrayList<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    public void setCompBeanList(ArrayList<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    public Trailer_dobj getTrailer_dobj_prv() {
        return trailer_dobj_prv;
    }

    public void setTrailer_dobj_prv(Trailer_dobj trailer_dobj_prv) {
        this.trailer_dobj_prv = trailer_dobj_prv;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    @Override
    public String toString() {
        return "TrailerBeanModel{" + "body_type=" + body_type + ", chassis_no_trailer=" + chassis_no_trailer + ", ld_wt=" + ld_wt + ", unld_wt=" + unld_wt + ", f_axle_descp=" + f_axle_descp + ", r_axle_descp=" + r_axle_descp + ", o_axle_descp=" + o_axle_descp + ", t_axle_descp=" + t_axle_descp + ", f_axle_weight=" + f_axle_weight + ", r_axle_weight=" + r_axle_weight + ", o_axle_weight=" + o_axle_weight + ", t_axle_weight=" + t_axle_weight + ", list_body_type=" + list_body_type + ", compBeanList=" + compBeanList + ", trailer_dobj_prv=" + trailer_dobj_prv + ", disable=" + disable + '}';
    }
    
    
    
    
}
