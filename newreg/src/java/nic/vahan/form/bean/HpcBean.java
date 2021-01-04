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
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author Sumit Gupta
 */
@ManagedBean(name = "hpc_bean")
@ViewScoped
public class HpcBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(HpcBean.class);
    private String masterLayout = "/masterLayoutPage_new.xhtml";
    private SessionVariables sessionVariables = null;
    private String appl_no;
    private String regn_no;
    private int pur_cd;
    private String vahanMessages = null;
    private Date maxDate = new Date();
    @ManagedProperty(value = "#{hypothecationDetails_bean}")
    private HypothecationDetailsBean hypothecationDetails_bean;
    private HpaDobj hpaDobj = new HpaDobj();

    @PostConstruct
    public void init() {
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null
                    || sessionVariables.getStateCodeSelected() == null
                    || sessionVariables.getOffCodeSelected() == 0
                    || sessionVariables.getEmpCodeLoggedIn() == null) {
                vahanMessages = "Something went wrong, Please try again...";
                return;
            }
            //################### Hypothecation Details Filler Start ###########
            HpaImpl hpa_Impl = new HpaImpl();
            List<HpaDobj> hypth = hpa_Impl.getHypoDetails(null, TableConstants.VM_TRANSACTION_MAST_REM_HYPO, sessionVariables.getSelectedWork().getRegn_no(), true, sessionVariables.getStateCodeSelected());


            if (hypth == null || hypth.isEmpty()) {
                vahanMessages = "Hypothecation Details is not found";
                return;
            } else {
                hpaDobj.setRegn_no(hypth.get(0).getRegn_no());
            }
            hypothecationDetails_bean.setListHypthDetails(hypth);
            //################### Hypothecation Details Filler End #############



        } catch (VahanException vme) {
            vahanMessages = vme.getMessage();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            vahanMessages = TableConstants.SomthingWentWrong;
        }

    }

    public String approveHPC() {
        String ret = "";
        try {
            Status_dobj status = new Status_dobj();
            status.setAppl_no(sessionVariables.getSelectedWork().getAppl_no());
            status.setPur_cd(TableConstants.VM_TRANSACTION_MAST_HPC);
            status.setStatus(TableConstants.STATUS_COMPLETE);
            status.setState_cd(sessionVariables.getStateCodeSelected());
            status.setOff_cd(sessionVariables.getOffCodeSelected());
            status.setUser_id(sessionVariables.getEmpCodeLoggedIn());
            status.setConfirm_ip(Util.getClientIpAdress());
            status.setEmp_cd(Long.valueOf(sessionVariables.getEmpCodeLoggedIn()));
            HpaImpl hpa_impl = new HpaImpl();
            hpa_impl.update_HPC_Status(hpaDobj, status);
            ret = "home";
        } catch (VahanException vme) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), vme.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
        return ret;
    }

    /**
     * @return the masterLayout
     */
    public String getMasterLayout() {
        return masterLayout;
    }

    /**
     * @param masterLayout the masterLayout to set
     */
    public void setMasterLayout(String masterLayout) {
        this.masterLayout = masterLayout;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public int getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    public String getVahanMessages() {
        return vahanMessages;
    }

    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    /**
     * @return the hpaDobj
     */
    public HpaDobj getHpaDobj() {
        return hpaDobj;
    }

    /**
     * @param hpaDobj the hpaDobj to set
     */
    public void setHpaDobj(HpaDobj hpaDobj) {
        this.hpaDobj = hpaDobj;
    }

    /**
     * @param hypothecationDetails_bean the hypothecationDetails_bean to set
     */
    public void setHypothecationDetails_bean(HypothecationDetailsBean hypothecationDetails_bean) {
        this.hypothecationDetails_bean = hypothecationDetails_bean;
    }
}
