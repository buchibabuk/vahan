/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import nic.vahan.form.dobj.FeeMaster_dobj;
import nic.vahan.form.impl.FeeMaster_Impl;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author Ashok Kumar <send2ashok@hotmail.com>
 */
@ManagedBean
@ViewScoped
public class FeeMaster_bean implements Serializable {

    private List<FeeMaster_dobj> purpose_mast_list = null;
    private List<FeeMaster_dobj> fee_list = null;
    private FeeMaster_dobj purpose_master;
    private String fee_type;
    private String fee_header;

    @PostConstruct
    public void init() {
        fee_header = "Empty Fee Details";
    }

    public void onRowSelect(ActionEvent event) {

        int pur_cd = (int) event.getComponent().getAttributes().get("pur_cd");
        fee_list = FeeMaster_Impl.getFeeDetails(pur_cd);
        fee_header = event.getComponent().getAttributes().get("descr").toString().toUpperCase();
    }

    public void feeTypeCodeChanged(AjaxBehaviorEvent e) {
        purpose_mast_list = FeeMaster_Impl.getPurposeMaster(fee_type);
        fee_list = null; // for empty the fee datail table 
        fee_header = "Empty Fee Details";
    }

    public void onRowEdit(RowEditEvent event) throws Exception { //it is called twice need to update in form and logic

        FeeMaster_dobj feeMaster_dobj = ((FeeMaster_dobj) event.getObject());

        if (feeMaster_dobj != null) {
            FeeMaster_Impl.updateFeeServiceCharge(feeMaster_dobj); // need to check whether it is updating all the changes........
        }

    }

    public void onRowCancel(RowEditEvent event) {
        String descr = ((FeeMaster_dobj) event.getObject()).getCatg_desc();
    }

    /**
     * @return the purpose_mast_list
     */
    public List<FeeMaster_dobj> getPurpose_mast_list() {
        return purpose_mast_list;
    }

    /**
     * @param purpose_mast_list the purpose_mast_list to set
     */
    public void setPurpose_mast_list(List<FeeMaster_dobj> purpose_mast_list) {
        this.purpose_mast_list = purpose_mast_list;
    }

    /**
     * @return the purpose_master
     */
    public FeeMaster_dobj getPurpose_master() {
        return purpose_master;
    }

    /**
     * @param purpose_master the purpose_master to set
     */
    public void setPurpose_master(FeeMaster_dobj purpose_master) {
        this.purpose_master = purpose_master;
    }

    /**
     * @return the fee_list
     */
    public List<FeeMaster_dobj> getFee_list() {
        return fee_list;
    }

    /**
     * @param fee_list the fee_list to set
     */
    public void setFee_list(List<FeeMaster_dobj> fee_list) {
        this.fee_list = fee_list;
    }

    /**
     * @return the fee_type
     */
    public String getFee_type() {
        return fee_type;
    }

    /**
     * @param fee_type the fee_type to set
     */
    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }

    /**
     * @return the fee_header
     */
    public String getFee_header() {
        return fee_header;
    }

    /**
     * @param fee_header the fee_header to set
     */
    public void setFee_header(String fee_header) {
        this.fee_header = fee_header;
    }
}
