/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.admin;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.AxleBean;
import nic.vahan.form.bean.ExArmyBean;
import nic.vahan.form.bean.HypothecationDetailsBean;
import nic.vahan.form.bean.InsBean;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.ExArmyDobj;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.ApplicationDisposeImpl;
import nic.vahan.form.impl.AxleImpl;
import nic.vahan.form.impl.ExArmyImpl;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Administrator
 */
@ViewScoped
@ManagedBean(name = "skipFeeBean")
public class SkipFeeBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(SkipFeeBean.class);
    private String regn_no = "";
    private String appl_no;
    private String receiptNo;
    private int pur_cd;
    private String reason;
    //private List<String> selectedPurposeCode;
    private String message = null;
    private boolean render = false;
    private Map<String, Integer> mapPurCdDescr = null;
    private OwnerDetailsDobj ownerDetail;
    @ManagedProperty(value = "#{ins_bean}")
    private InsBean ins_bean;
    @ManagedProperty(value = "#{hypothecationDetails_bean}")
    private HypothecationDetailsBean hypothecationDetails_bean;
    @ManagedProperty(value = "#{exArmyBean}")
    private ExArmyBean exArmyBean;
    @ManagedProperty(value = "#{axleBean}")
    private AxleBean axleBean;
    private int action_cd;

    public void showDetails() {
        int pur_cd = 0;
        String msg = "No Message";
        ApplicationDisposeImpl impl = null;

        if (this.appl_no == null || this.appl_no.trim().equalsIgnoreCase("")) {
            msg = "Please Enter Valid Application Number";
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
            return;
        }

        if (receiptNo == null || receiptNo.trim().equalsIgnoreCase("")) {
            msg = "Please Enter Valid Receipt Number";
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
            return;
        }

        try {

            mapPurCdDescr = ApplicationDisposeImpl.mapPurCdDescr(appl_no, receiptNo, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());

            OwnerImpl owner_Impl = new OwnerImpl();
            //Map<String, Integer> map = purCodeList;
            regn_no = ApplicationDisposeImpl.getRegNo(appl_no);
            for (Map.Entry<String, Integer> entry : mapPurCdDescr.entrySet()) {

                if (entry.getValue() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                        || entry.getValue() == TableConstants.VM_TRANSACTION_MAST_TEMP_REG
                        || entry.getValue() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                        || entry.getValue() == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {

                    ownerDetail = owner_Impl.getVaOwnerDetails(this.appl_no.trim(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                    pur_cd = entry.getValue();
                }


            }

            if (pur_cd != TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                    && pur_cd != TableConstants.VM_TRANSACTION_MAST_TEMP_REG
                    && pur_cd != TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                    && pur_cd != TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE
                    && !regn_no.equalsIgnoreCase("NEW")
                    && !regn_no.equalsIgnoreCase("TEMPREG")) {

                impl = new ApplicationDisposeImpl();
                //Get alloted off_cd for permit --NAMAN JAIN
                String offCd = impl.getlistOfOffCdForPermit(appl_no, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                if (CommonUtils.isNullOrBlank(offCd)) {
                    offCd = String.valueOf(Util.getSelectedSeat().getOff_cd());
                }
                ownerDetail = owner_Impl.getOwnerDetails(this.regn_no.trim(), Util.getUserStateCode(), offCd);
                if (ownerDetail == null) {
                    ownerDetail = owner_Impl.getOwnerDetails(this.regn_no.trim(), Util.getUserStateCode(), offCd);
                }
            }



            if (ownerDetail != null) {


                //for Owner Identification Fields disallow typing
                if (ownerDetail.getOwnerIdentity() != null) {
                    ownerDetail.getOwnerIdentity().setFlag(true);
                    ownerDetail.getOwnerIdentity().setMobileNoEditable(true);
                }


                if (ownerDetail.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_EXARMY)) {
                    ExArmyDobj dobj = null;
                    if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                        dobj = ExArmyImpl.setExArmyDetails_db_to_dobj(null, appl_no, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                    } else {
                        dobj = ExArmyImpl.setExArmyDetails_db_to_dobj(null, regn_no, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                    }
                    if (dobj != null) {
                        exArmyBean.setDobj_To_Bean(dobj);
                    }
                }

                if (ownerDetail.getVehTypeDescr().equalsIgnoreCase(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR)) {
                    AxleDetailsDobj dobj = null;
                    if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                        dobj = AxleImpl.setAxleVehDetails_db_to_dobj(null, appl_no, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                    } else {
                        dobj = AxleImpl.setAxleVehDetails_db_to_dobj(null, regn_no, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                    }
                    if (dobj != null) {
                        axleBean.setDobj_To_Bean(dobj);
                    }
                }


                //#################### Insurance Details Filler Start ##############
                InsDobj ins_dobj = null;
                if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                    ins_dobj = InsImpl.set_ins_dtls_db_to_dobj(null, appl_no, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                } else {
                    ins_dobj = InsImpl.set_ins_dtls_db_to_dobj(this.regn_no.trim(), null, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                }
                if (ins_dobj != null) {
                    ins_bean.set_Ins_dobj_to_bean(ins_dobj);
                }
                //#################### Insurance Details Filler End ################



                //################### Hypothecation Details Filler Start ###########
                HpaImpl hpa_Impl = new HpaImpl();
                List<HpaDobj> hypth = null;
                if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                    hypth = hpa_Impl.getHypoDetails(this.appl_no.trim(), TableConstants.VM_TRANSACTION_MAST_REM_HYPO, null, true, Util.getUserStateCode());
                } else {
                    hypth = hpa_Impl.getHypoDetails(null, TableConstants.VM_TRANSACTION_MAST_REM_HYPO, this.regn_no.trim(), true, Util.getUserStateCode());
                }
                hypothecationDetails_bean.setListHypthDetails(hypth);
                //################### Hypothecation Details Filler End #############                
            }
            ins_bean.componentReadOnly(false);
            render = true;

        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error!", "Registration Number Not Found"));
        }

    }

    public void skipFee() {

        Exception e = null;
        Status_dobj status = null;
        ApplicationDisposeImpl impl = null;
        String msg = null;

//        if (selectedPurposeCode.isEmpty()) {
//            JSFUtils.showMessagesInDialog("Alert!", "You have Not Selected any Option", FacesMessage.SEVERITY_WARN);
//            return;
//        }

        if (mapPurCdDescr == null || mapPurCdDescr.size() < 1) {
            JSFUtils.showMessagesInDialog("Alert!", "No Proper Purpose found", FacesMessage.SEVERITY_WARN);
            return;
        }

        try {
            impl = new ApplicationDisposeImpl();
            OwnerImpl owImpl = new OwnerImpl();
            if (CommonPermitPrintImpl.checkPermitPrintPending(getAppl_no(), "3")) {
                JSFUtils.showMessagesInDialog("Alert!", "First Print Offer Letter", FacesMessage.SEVERITY_WARN);
                return;
            }
            for (Map.Entry<String, Integer> entry : mapPurCdDescr.entrySet()) {

                status = new Status_dobj();
                status.setAppl_no(appl_no);
//                status.setEmp_cd(Long.parseLong(Util.getEmpCode()));
                status.setPur_cd(entry.getValue());
                status.setOffice_remark(reason);
                status.setState_cd(Util.getUserStateCode());
                status.setOff_cd(Util.getSelectedSeat().getOff_cd());
                status.setVehicleParameters(FormulaUtils.fillVehicleParametersFromDobj(owImpl.getOwnerDobj(ownerDetail)));
                status.setStatus("C");
                impl.saveSkipFee(status, receiptNo);

            }

        } catch (VahanException vex) {
            e = vex;
        } catch (Exception ex) {
            e = ex;
        }


        if (e != null && e instanceof VahanException) {
            msg = "Application Fee can not be Exempted due to " + e.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
            render = false;
            appl_no = "";
            reason = "";
            receiptNo = "";
            PrimeFaces.current().ajax().update("panelOwnerInfo");
            return;
        }


        if (e != null) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exception Occured - Application Fee Could not Exempted", "Exception Occured - Application Could not Exempted"));
            return;
        }

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Fee Exempted Successfully for Application Number " + appl_no, "Fee Exempted Successfully for Application Number " + appl_no));
        render = false;
        appl_no = "";
        reason = "";
        receiptNo = "";
        PrimeFaces.current().ajax().update("panelOwnerInfo");
    }

    /**
     * @return the regn_no
     */
    public String getRegn_no() {
        return regn_no;
    }

    /**
     * @param regn_no the regn_no to set
     */
    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
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
     * @return the render
     */
    public boolean isRender() {
        return render;
    }

    /**
     * @param render the render to set
     */
    public void setRender(boolean render) {
        this.render = render;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
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
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }

    /**
     * @param appl_no the appl_no to set
     */
    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @param exArmyBean the exArmyBean to set
     */
    public void setExArmyBean(ExArmyBean exArmyBean) {
        this.exArmyBean = exArmyBean;
    }

    /**
     * @param axleBean the axleBean to set
     */
    public void setAxleBean(AxleBean axleBean) {
        this.axleBean = axleBean;
    }

    /**
     * @return the action_cd
     */
    public int getAction_cd() {
        return action_cd;
    }

    /**
     * @param action_cd the action_cd to set
     */
    public void setAction_cd(int action_cd) {
        this.action_cd = action_cd;
    }

    /**
     * @return the receiptNo
     */
    public String getReceiptNo() {
        return receiptNo;
    }

    /**
     * @param receiptNo the receiptNo to set
     */
    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    /**
     * @return the mapPurCdDescr
     */
    public Map<String, Integer> getMapPurCdDescr() {
        return mapPurCdDescr;
    }

    /**
     * @param mapPurCdDescr the mapPurCdDescr to set
     */
    public void setMapPurCdDescr(Map<String, Integer> mapPurCdDescr) {
        this.mapPurCdDescr = mapPurCdDescr;
    }
}
