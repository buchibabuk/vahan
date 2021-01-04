/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.SmartCardImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author Sumit Gupta
 */
@ManagedBean(name = "rctosmartcardconversion_bean")
@ViewScoped
public class RCtoSmartCardConversionBean extends AbstractApplBean implements Serializable {
    
    private static final Logger LOGGER = Logger.getLogger(RCtoSmartCardConversionBean.class);
    private String vahanMessages = null;
    private OwnerDetailsDobj ownerDetail;
    @ManagedProperty(value = "#{ins_bean}")
    private InsBean ins_bean;
    @ManagedProperty(value = "#{hypothecationDetails_bean}")
    private HypothecationDetailsBean hypothecationDetails_bean;
    
    @PostConstruct
    public void init() {
        try {
            
            if (getAppl_details() == null
                    || getAppl_details().getCurrent_state_cd() == null
                    || getAppl_details().getCurrent_off_cd() == 0
                    || getAppl_details().getCurrentEmpCd() == null) {
                vahanMessages = "Something went wrong, Please try again...";
                return;
            }
            
            if (getAppl_details().getOwnerDetailsDobj() == null) {
                vahanMessages = "Owner details not found";
                return;
            }
            
            ownerDetail = getAppl_details().getOwnerDetailsDobj();

            //#################### Insurance Details Filler Start ##############
            InsDobj ins_dobj = null;
            ins_dobj = InsImpl.set_ins_dtls_db_to_dobj(appl_details.getRegn_no(), null, appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd());
            if (ins_dobj != null) {
                ins_bean.set_Ins_dobj_to_bean(ins_dobj);
            }
            //#################### Insurance Details Filler End ################
            //################### Hypothecation Details Filler Start ###########
            HpaImpl hpa_Impl = new HpaImpl();
            List<HpaDobj> hypth = hpa_Impl.getHypoDetails(null, TableConstants.VM_TRANSACTION_MAST_REM_HYPO, appl_details.getRegn_no(), true, appl_details.getCurrent_state_cd());
            hypothecationDetails_bean.setListHypthDetails(hypth);
            //################### Hypothecation Details Filler End #############
            ins_bean.componentReadOnly(false);
        } catch (VahanException vme) {
            vahanMessages = vme.getMessage();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            vahanMessages = TableConstants.SomthingWentWrong;
        }
        
    }
    
    public String approveRCtoSmartcardConversion() {
        String ret = "";
        try {
            Status_dobj status = new Status_dobj();
            status.setAppl_no(appl_details.getAppl_no());
            status.setRegn_no(appl_details.getRegn_no());
            status.setPur_cd(TableConstants.VM_TRANSACTION_MAST_RC_TO_SMART_CARD_CONVERSION);
            status.setStatus(TableConstants.STATUS_COMPLETE);
            status.setState_cd(appl_details.getCurrent_state_cd());
            status.setOff_cd(appl_details.getCurrent_off_cd());
            status.setConfirm_ip(Util.getClientIpAdress());
            status.setEmp_cd(Long.valueOf(appl_details.getCurrentEmpCd()));
            TmConfigurationDobj tmConfig = Util.getTmConfiguration();
            SmartCardImpl impl = new SmartCardImpl();
            impl.update_RCtoSmartCardConversion_Status(status,tmConfig);
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
     * @return the vahanMessages
     */
    public String getVahanMessages() {
        return vahanMessages;
    }

    /**
     * @param vahanMessages the vahanMessages to set
     */
    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }

    /**
     * @return the ownerDetail
     */
    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    /**
     * @param ownerDetail the ownerDetail to set
     */
    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }

    /**
     * @param ins_bean the ins_bean to set
     */
    public void setIns_bean(InsBean ins_bean) {
        this.ins_bean = ins_bean;
    }

    /**
     * @param hypothecationDetails_bean the hypothecationDetails_bean to set
     */
    public void setHypothecationDetails_bean(HypothecationDetailsBean hypothecationDetails_bean) {
        this.hypothecationDetails_bean = hypothecationDetails_bean;
    }
}
