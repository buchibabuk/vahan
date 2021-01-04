/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.dobj.DupDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.DupImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import nic.vahan.services.insurance.InsuranceDetailService;
import nic.vahan.services.insurance.dobj.InsuranceInfoDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC095
 */
@ManagedBean(name = "dupCert")
@ViewScoped
public class DupBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(DupBean.class);
    private List reasonList;
    private Date currDate;
    private DupDobj prevDupDobj;
    private DupDobj dup_dobj = new DupDobj();
    private List<ComparisonBean> prevChangedDataList;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private String reasonSelect;
    private boolean boolDupPanel;
    private boolean boolReason;
    private boolean renderSaveButton;
    private boolean renderPrintButton;
    private Date regnDate;
    @ManagedProperty(value = "#{ins_bean}")
    private InsBean ins_bean;
    private String vahanMessages = null;

    public DupBean() {
        renderPrintButton = false;
        renderSaveButton = true;
        reasonList = new ArrayList();
        reasonList.add(new SelectItem("-1", "-SELECT-"));
        reasonList.add(new SelectItem("LOST", "LOST"));
        reasonList.add(new SelectItem("THEFT", "THEFT"));
        reasonList.add(new SelectItem("TORN", "TORN/DEFACED/DAMAGED"));
        reasonList.add(new SelectItem("OTHER", "OTHER"));
        boolDupPanel = false;
        boolReason = true;
    }

    @PostConstruct
    public void init() {
        InsDobj ins_dobj_ret = null;
        InsDobj ins_dobj_retVA = null;
        try {
            if (getAppl_details() != null) {
                DupImpl dup_Impl = new DupImpl();
                dup_dobj = dup_Impl.set_dobj_from_db(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                if (dup_dobj != null) {
                    prevDupDobj = (DupDobj) dup_dobj.clone();
                } else {
                    dup_dobj = new DupDobj();
                    dup_dobj.setAppl_no(appl_details.getAppl_no());
                    dup_dobj.setRegn_no(appl_details.getRegn_no());
                    dup_dobj.setPur_cd(appl_details.getPur_cd());
                    dup_dobj.setState_cd(appl_details.getCurrent_state_cd());
                    dup_dobj.setOff_cd(appl_details.getCurrent_off_cd());
                }

                if (appl_details.getOwnerDetailsDobj() == null) {
                    vahanMessages = "Owner Details not Found in the Database, Please Contact to the System Administrator";
                    return;
                }

                setBeanFromDobj();//this function is modified when component binding was removed.
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                ins_bean.setAppl_no(appl_details.getAppl_no());
                ins_bean.setRegn_no(appl_details.getRegn_no());
                ins_bean.setPur_cd(appl_details.getPur_cd());
                ins_dobj_ret = InsImpl.set_ins_dtls_db_to_dobj(appl_details.getRegn_no(), null, appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd());
                ins_dobj_retVA = InsImpl.set_ins_dtls_db_to_dobjVA(appl_details.getRegn_no());
                //start of getting insurance details from service
                InsuranceDetailService detailService = new InsuranceDetailService();
                InsDobj insDobj = detailService.getInsuranceDetailsByService(getAppl_details().getRegn_no(), getAppl_details().getCurrent_state_cd(), getAppl_details().getCurrent_off_cd());
                if (insDobj != null) {
                    if ((DateUtils.compareDates(DateUtils.addToDate(insDobj.getIns_from(), DateUtils.MONTH, -1), appl_details.getOwnerDetailsDobj().getPurchase_date()) != 1)) {
                        if (ins_dobj_ret != null) {
                            insDobj.setIdv(ins_dobj_ret.getIdv());
                        } else if (ins_dobj_retVA != null) {
                            insDobj.setIdv(ins_dobj_retVA.getIdv());
                        }
                        ins_bean.set_Ins_dobj_to_bean(insDobj);
                        ins_bean.setDisable(true);
                        //end of getting insurance details from service 
                    }
                    //for checking insurance availablity and expiration start
                    if (!insDobj.isIibData()) {
                        ins_bean.componentReadOnly(true);
                        ins_bean.setGovtVehFlag(false);
                        ins_bean.validateInsurance(ins_dobj_ret, ins_dobj_retVA, false);
                    } //for checking insurance availablity and expiration end 
                }
                setRegnDate(getAppl_details().getOwnerDobj().getRegn_dt());

                //Check for blacklisted                
                BlackListedVehicleDobj blacklistedStatus = appl_details.getOwnerDetailsDobj().getBlackListedVehicleDobj();
                if (blacklistedStatus != null && (blacklistedStatus.getComplain_type() == TableConstants.BLTheftCode || blacklistedStatus.getComplain_type() == TableConstants.BLDestroyedAccidentCode)) {
                    ins_bean.setMin_dt(appl_details.getOwnerDobj().getPurchase_dt());
                }
            }
        } catch (VahanException vex) {
            vahanMessages = vex.getMessage();
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " [ApplNo-" + appl_details.getAppl_no() + ",RegnNo-" + appl_details.getRegn_no() + ",PurCd-" + appl_details.getPur_cd() + "] " + sqle.getStackTrace()[0]);
            vahanMessages = "Something Went Wrong, Please Contact to the System Administrator";
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " [ApplNo-" + appl_details.getAppl_no() + ",RegnNo-" + appl_details.getRegn_no() + ",PurCd-" + appl_details.getPur_cd() + "] " + ex.getStackTrace()[0]);
            vahanMessages = "Something Went Wrong, Please Contact to the System Administrator";
        }
    }

    public void makeDobjFromBean() {
        //this function is customized when comp9onent binding is removed

        dup_dobj.setAppl_no(getAppl_details().getAppl_no());
        dup_dobj.setPur_cd(getAppl_details().getPur_cd());
        dup_dobj.setRegn_no(getAppl_details().getRegn_no());
        dup_dobj.setState_cd(Util.getUserStateCode());
        dup_dobj.setOff_cd(Util.getSelectedSeat().getOff_cd());

        if (dup_dobj.getFir_dt() != null && (this.dup_dobj.getReason().equalsIgnoreCase("THEFT") || this.dup_dobj.getReason().equalsIgnoreCase("LOST"))) {
        } else {
            dup_dobj.setFir_dt(null);
        }
        if (dup_dobj.getFir_no() != null && (this.dup_dobj.getReason().equalsIgnoreCase("THEFT") || this.dup_dobj.getReason().equalsIgnoreCase("LOST"))) {
        } else {
            dup_dobj.setFir_no("");
        }
        if (dup_dobj.getPolice_station() != null && (this.dup_dobj.getReason().equalsIgnoreCase("THEFT") || this.dup_dobj.getReason().equalsIgnoreCase("LOST"))) {
        } else {
            dup_dobj.setPolice_station("");
        }
    }

    public void setBeanFromDobj() {//this function is modified when component binding was removed.

        if (dup_dobj.getReason() == null) {
            return;
        }
        reasonSelect = dup_dobj.getReason();

        if (!dup_dobj.getReason().equalsIgnoreCase("LOST") && !dup_dobj.getReason().equalsIgnoreCase("THEFT")
                && !dup_dobj.getReason().equalsIgnoreCase("TORN") && !dup_dobj.getReason().equalsIgnoreCase("OTHER")) {
            boolReason = false;
            reasonSelect = "OTHER";
        } else if (dup_dobj.getReason().equalsIgnoreCase("THEFT") || dup_dobj.getReason().equalsIgnoreCase("LOST")) {
            boolReason = true;
            boolDupPanel = true;
        } else {
            this.dup_dobj.setFir_no("");
            this.dup_dobj.setPolice_station("");
        }

    }

    public List<ComparisonBean> addToComapreChangesList(List<ComparisonBean> compBeanListPrev) throws VahanException {

        List<ComparisonBean> list = compareChanges();

        if (compBeanListPrev == null) {
            compBeanListPrev = new ArrayList<>();
        }
        if (list.size() > 0) {
            compBeanListPrev.addAll(list);
        }

        return compBeanListPrev;
    }

    @Override
    public List<ComparisonBean> compareChanges() {

        DupDobj dobj = getPrevDupDobj();  //getting the dobj from workbench

        if (dobj == null) {
            return compBeanList;
        }
        compBeanList.clear();

        if (dobj.getReason().trim().equalsIgnoreCase("THEFT") || dobj.getReason().trim().equalsIgnoreCase("LOST")) {
            Compare("Fir Date", dobj.getFir_dt(), this.dup_dobj.getFir_dt(), compBeanList);
            Compare("Fir No", dobj.getFir_no(), this.dup_dobj.getFir_no(), compBeanList);
            Compare("Police Station", dobj.getPolice_station(), this.dup_dobj.getPolice_station(), compBeanList);
        }
        Compare("Reason", dobj.getReason(), this.dup_dobj.getReason(), compBeanList);

        return getCompBeanList();

    }

    public List getReasonList() {
        return reasonList;
    }

    public void setReasonList(List reasonList) {
        this.reasonList = reasonList;
    }

    public void vehReasonChangeListener(AjaxBehaviorEvent event) {

        if (reasonSelect.equalsIgnoreCase("THEFT") || reasonSelect.equalsIgnoreCase("LOST")) {
            dup_dobj.setReason(reasonSelect);
            boolReason = true;
            boolDupPanel = true;
            dup_dobj.setFir_no(null);
            dup_dobj.setFir_dt(null);
            dup_dobj.setPolice_station(null);
        } else {
            dup_dobj.setReason(reasonSelect);
            boolReason = true;
            boolDupPanel = false;
            if (reasonSelect.equalsIgnoreCase("OTHER")) {
                dup_dobj.setReason("");
                boolReason = false;
            }
        }
    }

    public Date getCurrDate() {
        currDate = new Date();
        return currDate;
    }

    public void setCurrDate(Date currDate) {
        this.currDate = currDate;
    }

    /**
     * @return the PrevDupDobj
     */
    public DupDobj getPrevDupDobj() {
        return prevDupDobj;
    }

    /**
     * @param PrevDupDobj the PrevDupDobj to set
     */
    public void setPrevDupDobj(DupDobj prevDupDobj) {
        this.prevDupDobj = prevDupDobj;
    }

    /**
     * @return the compBeanList
     */
    @Override
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    /**
     * @param compBeanList the compBeanList to set
     */
    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    /**
     * @return the reasonSelect
     */
    public String getReasonSelect() {
        return reasonSelect;
    }

    /**
     * @param reasonSelect the reasonSelect to set
     */
    public void setReasonSelect(String reasonSelect) {
        this.reasonSelect = reasonSelect;
    }

    @Override
    public String save() {
        String return_location = "";
        try {
            List<ComparisonBean> compareChanges = compareChanges();
            //for updating or inserting insurance details with Duplicate Certificate details
            InsDobj ins_dobj_new = ins_bean.set_InsBean_to_dobj();
            if (ins_dobj_new != null) {
                if (appl_details.getOwnerDetailsDobj().getBlackListedVehicleDobj() != null
                        && (appl_details.getOwnerDetailsDobj().getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLTheftCode
                        || appl_details.getOwnerDetailsDobj().getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLDestroyedAccidentCode)) {
                    //skip insurance validation check in case of theft and destroyed vehicle.
                    InsImpl insImpl = new InsImpl();
                    appl_details.getOwnerDetailsDobj().setInsDobj(ins_dobj_new);
                    insImpl.validateInsuranceForBlackListedVehicle(appl_details.getOwnerDetailsDobj());
                } else {
                    ins_bean.validateInsurance(ins_dobj_new);
                }
                ins_bean.ins_update(appl_details.getOwnerDetailsDobj());
                if (!compareChanges.isEmpty() || getPrevDupDobj() == null) { //save only when data is really changed by user}
                    makeDobjFromBean();//It is customized when removed component binding
                    DupImpl.saveChangeDup(dup_dobj, ComparisonBeanImpl.changedDataContents(compareChanges));
                }
                return_location = "seatwork";
            } else {
                if (ins_dobj_new == null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Insurance Details not available !!!"));
                }
            }
        } catch (VahanException vme) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), vme.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
        }
        return return_location;
    }

    @Override
    public String saveAndMoveFile() {

        String return_location = "";
        InsDobj ins_dobj_new = ins_bean.set_InsBean_to_dobj();
        if (ins_dobj_new != null) {
            try {
                if (!getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                    if (appl_details.getOwnerDetailsDobj().getBlackListedVehicleDobj() != null
                            && (appl_details.getOwnerDetailsDobj().getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLTheftCode
                            || appl_details.getOwnerDetailsDobj().getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLDestroyedAccidentCode)) {
                        //skip insurance validation check in case of theft and destroyed vehicle.
                        InsImpl insImpl = new InsImpl();
                        appl_details.getOwnerDetailsDobj().setInsDobj(ins_dobj_new);
                        insImpl.validateInsuranceForBlackListedVehicle(appl_details.getOwnerDetailsDobj());
                        ins_bean.ins_update(appl_details.getOwnerDetailsDobj());
                    } else if (ins_bean.validateInsurance(ins_dobj_new)) {
                        ins_bean.ins_update(appl_details.getOwnerDetailsDobj());
                    } else {
                        return return_location;
                    }
                }

                Status_dobj status = new Status_dobj();
                status.setAppl_dt(appl_details.getAppl_dt());
                status.setAppl_no(appl_details.getAppl_no());
                status.setPur_cd(appl_details.getPur_cd());
                status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
                status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
                status.setStatus(getApp_disapp_dobj().getNew_status());

                status.setCurrent_role(appl_details.getCurrent_role());

                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                        || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)
                        || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                    status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                    status.setVehicleParameters(appl_details.getVehicleParameters());
                    if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE) && this.appl_details.isDocVerifyForFacelessAppl()) {
                        String[] notVerifiedDocDetails = ServerUtil.verifyDocUploaded(appl_details.getAppl_no(), getAppl_details().getCurrent_action_cd());
                        if (notVerifiedDocDetails != null) {
                            appl_details.setNotVerifiedDocdetails(notVerifiedDocDetails[1]);
                            throw new VahanException(notVerifiedDocDetails[0]);
                        }
                    }
                    DupImpl frc_impl = new DupImpl();
                    makeDobjFromBean();//It is customized when removed component binding     
                    if (dup_dobj.getReason() == null) {
                        throw new VahanException("Reason for taking duplicate certificate cannot be null.");
                    }
                    frc_impl.update_DupCert_Status(status.getCurrent_role(), dup_dobj, prevDupDobj, status, ComparisonBeanImpl.changedDataContents(compareChanges()));
                    if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                        ServerUtil.sendSmsInFacelessService(appl_details.getAppl_no(), Util.getUserStateCode(), Util.getUserSeatOffCode(), appl_details.getRegn_no(), appl_details.getNotVerifiedDocdetails());
                        return_location = disapprovalPrint();
                    } else {
                        return_location = "seatwork";
                    }
                }
            } catch (VahanException ve) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
            }
        } else {
            if (ins_dobj_new == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Insurance Details not available !!!"));
            }
        }
        return return_location;
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return prevChangedDataList;
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList;
    }

    /**
     * @return the dup_dobj
     */
    public DupDobj getDup_dobj() {
        return dup_dobj;
    }

    /**
     * @param dup_dobj the dup_dobj to set
     */
    public void setDup_dobj(DupDobj dup_dobj) {
        this.dup_dobj = dup_dobj;
    }

    /**
     * @param ins_bean the ins_bean to set
     */
    public void setIns_bean(InsBean ins_bean) {
        this.ins_bean = ins_bean;
    }

    /**
     * @return the renderSaveButton
     */
    public boolean isRenderSaveButton() {
        return renderSaveButton;
    }

    /**
     * @param renderSaveButton the renderSaveButton to set
     */
    public void setRenderSaveButton(boolean renderSaveButton) {
        this.renderSaveButton = renderSaveButton;
    }

    /**
     * @return the renderPrintButton
     */
    public boolean isRenderPrintButton() {
        return renderPrintButton;
    }

    /**
     * @param renderPrintButton the renderPrintButton to set
     */
    public void setRenderPrintButton(boolean renderPrintButton) {
        this.renderPrintButton = renderPrintButton;
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the boolDupPanel
     */
    public boolean isBoolDupPanel() {
        return boolDupPanel;
    }

    /**
     * @param boolDupPanel the boolDupPanel to set
     */
    public void setBoolDupPanel(boolean boolDupPanel) {
        this.boolDupPanel = boolDupPanel;
    }

    /**
     * @return the boolReason
     */
    public boolean isBoolReason() {
        return boolReason;
    }

    /**
     * @param boolReason the boolReason to set
     */
    public void setBoolReason(boolean boolReason) {
        this.boolReason = boolReason;
    }

    /**
     * @return the regnDate
     */
    public Date getRegnDate() {
        return regnDate;
    }

    /**
     * @param regnDate the regnDate to set
     */
    public void setRegnDate(Date regnDate) {
        this.regnDate = regnDate;
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
}
