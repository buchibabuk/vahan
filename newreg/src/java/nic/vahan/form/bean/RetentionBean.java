/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.bean.fancy.RetenRegnNo_dobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.fancy.AdvanceRegnNo_dobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.NewImpl;
import nic.vahan.form.impl.RetentionImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;

@ManagedBean(name = "retentionBean")
@ViewScoped
public class RetentionBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(RetentionBean.class);
    private String vahanMessages = null;
    private RetenRegnNo_dobj retenRegnNoDobjPrev = null;
    private RetenRegnNo_dobj retenRegnNoDobj = new RetenRegnNo_dobj();
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<ComparisonBean> prevChangedDataList;
    private List<OwnerDetailsDobj> listExistingOwnerDetails = new ArrayList<>();
    private boolean advRegnCheck = false;
    private boolean advRegnCheckDialogue = false;
    private AdvanceRegnNo_dobj advRegnNoDobj = new AdvanceRegnNo_dobj();
    private boolean disableAdvRegnCheck = false;
    private List list_adv_district = new ArrayList();
    private List list_c_district;
    private List list_c_state;
    private String regnNumberAllotedMsg = "";
    private List listOwnerCatg = new ArrayList();
    private OwnerDetailsDobj ownerDetail;

    public RetentionBean() {
    }

    @PostConstruct
    public void init() {


        try {

            if (appl_details == null) {
                vahanMessages = TableConstants.SomthingWentWrong;
                return;
            }

            if (appl_details.getOwnerDetailsDobj() == null) {
                vahanMessages = "Owner Details not Found in the Database, Please Contact to the System Administrator";
                return;
            }
            //Master Filler for Owner Category
            String[][] data = MasterTableFiller.masterTables.VM_OWCATG.getData();
            for (int i = 0; i < data.length; i++) {
                listOwnerCatg.add(new SelectItem(data[i][0], data[i][1]));
            }
            ownerDetail = appl_details.getOwnerDetailsDobj();
            list_c_district = new ArrayList();
            list_c_state = new ArrayList();
            RetentionImpl retentionImpl = new RetentionImpl();
            retenRegnNoDobj = retentionImpl.getVaSurrenderRetentionDetails(appl_details.getAppl_no());
            list_c_state = MasterTableFiller.getStateList();
            if (retenRegnNoDobj != null) {
                this.retenRegnNoDobjPrev = (RetenRegnNo_dobj) retenRegnNoDobj.clone();//for holding current dobj for using in the comparison.
            } else {
                retenRegnNoDobj = new RetenRegnNo_dobj();
                retenRegnNoDobj.setState_cd(appl_details.getCurrent_state_cd());
                retenRegnNoDobj.setOff_cd(appl_details.getCurrent_off_cd());
                retenRegnNoDobj.setAppl_no(appl_details.getAppl_no());
                retenRegnNoDobj.setRegn_no(appl_details.getRegn_no());
                SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
                Date applDate = format.parse(appl_details.getAppl_dt());
                retenRegnNoDobj.setAppl_date(applDate);
            }
            if (appl_details.getCurrent_action_cd() == TableConstants.RETENTION_ENTRY) {
                String regnNoAllotted = NewImpl.getAdvanceRegnNo(appl_details.getAppl_no());
                if (CommonUtils.isNullOrBlank(regnNoAllotted)) {
                    setDisableAdvRegnCheck(false);
                    advRegnCheck = false;
                } else {
                    setRegnNumberAllotedMsg("Vehicle Registration No " + regnNoAllotted + " will allot.");
                    setDisableAdvRegnCheck(true);
                    advRegnCheck = true;
                }
            }
            if (appl_details.getCurrent_action_cd() == TableConstants.RETENTION_VERIFICATION
                    || appl_details.getCurrent_action_cd() == TableConstants.RETENTION_APPROVAL) {
                VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(appl_details.getOwnerDobj());
                String seriesAvailMessage = ServerUtil.getAvailablePrefixSeries(vehParameters);
                if (!seriesAvailMessage.equals(TableConstants.SERIES_EXHAUST_MESSAGE) && !seriesAvailMessage.equals("")) {
                    seriesAvailMessage = "Vehicle Registration No will be Generated from the Series " + seriesAvailMessage + ".";
                    setRegnNumberAllotedMsg(seriesAvailMessage);
                }
                String regnNoAllotted = NewImpl.getAdvanceRegnNo(appl_details.getAppl_no());
                if (regnNoAllotted != null && !regnNoAllotted.isEmpty()) {
                    setRegnNumberAllotedMsg("Vehicle Registration No " + regnNoAllotted + " will allot.");
                    retenRegnNoDobj.setAssignFancyRegnNumber(regnNoAllotted);
                    retenRegnNoDobj.setAssignFancyNumber(true);
                    setAdvRegnCheck(true);
                }

                if (appl_details.getCurrent_action_cd() == TableConstants.RETENTION_VERIFICATION && CommonUtils.isNullOrBlank(regnNoAllotted)) {
                    setDisableAdvRegnCheck(false);
                } else {
                    setDisableAdvRegnCheck(true);
                }
                if (appl_details.getCurrent_action_cd() == TableConstants.RETENTION_APPROVAL) {
                    setDisableAdvRegnCheck(true);
                }
            }

        } catch (VahanException vex) {
            vahanMessages = vex.getMessage();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " [ApplNo-" + appl_details.getAppl_no() + ",RegnNo-" + appl_details.getRegn_no() + ",PurCd-" + appl_details.getPur_cd() + "] " + ex.getStackTrace()[0]);
            vahanMessages = "Something Went Wrong, Please Contact to the System Administrator";
        }
    }

    @Override
    public String save() {
        String returnLocation = "";
        try {
            List<ComparisonBean> compareChanges = compareChanges();
            if (!compareChanges.isEmpty() || retenRegnNoDobjPrev == null) { //save only when data is really changed by user
                RetentionImpl impl = new RetentionImpl();
                impl.makeChangesRetention(retenRegnNoDobj, ComparisonBeanImpl.changedDataContents(compareChanges), appl_details.getCurrentEmpCd());
            }
            returnLocation = "seatwork";
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
        return returnLocation;
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        if (this.retenRegnNoDobjPrev == null) {
            return compBeanList;
        }
        compBeanList.clear();// for clearing the list in case of clicking compare changes button again and again.
        Compare("Reason", retenRegnNoDobjPrev.getReason(), retenRegnNoDobj.getReason(), compBeanList);
        return compBeanList;
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        try {

            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());

            if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_COMPLETE)
                    || getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_REVERT)
                    || getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                status.setVehicleParameters(appl_details.getVehicleParameters());
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE) && this.appl_details.isDocVerifyForFacelessAppl()) {
                    String[] notVerifiedDocDetails = ServerUtil.verifyDocUploaded(appl_details.getAppl_no(), getAppl_details().getCurrent_action_cd());
                    if (notVerifiedDocDetails != null) {
                        appl_details.setNotVerifiedDocdetails(notVerifiedDocDetails[1]);
                        throw new VahanException(notVerifiedDocDetails[0]);
                    }
                }
                List<ComparisonBean> compareChanges = compareChanges();
                RetentionImpl retentionImpl = new RetentionImpl();
                retentionImpl.updateRetentionStatus(retenRegnNoDobj, retenRegnNoDobjPrev, status, ComparisonBeanImpl.changedDataContents(compareChanges), appl_details);
                if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    ServerUtil.sendSmsInFacelessService(appl_details.getAppl_no(), Util.getUserStateCode(), Util.getUserSeatOffCode(), appl_details.getRegn_no(), appl_details.getNotVerifiedDocdetails());
                }
                return_location = "seatwork";

            }

        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
        return return_location;
    }

    public void advanceCheckListener() {
        if (advRegnCheck) {
            advRegnCheckDialogue = true;
        } else {
            advRegnCheckDialogue = false;
            setAdvRegnNoDobj(new AdvanceRegnNo_dobj());
        }
    }

    public void advanceSaveListener() {
        if (getAdvRegnNoDobj().getRegn_no() != null) {
            retenRegnNoDobj.setAssignFancyNumber(true);
            retenRegnNoDobj.setAssignFancyRegnNumber(getAdvRegnNoDobj().getRegn_no());
        } else {
            setAdvRegnNoDobj(new AdvanceRegnNo_dobj());
            setAdvRegnCheck(false);
        }
    }

    public void advanceRcptListener() throws Exception {
        try {
            if (advRegnCheck) {

                String rcptno = getAdvRegnNoDobj().getRecp_no();
                Date rcptDate = NewImpl.getFancyNoRcptDate(rcptno);
                if (rcptDate != null) {
                    NewImpl.validationFancyRcptDate(Util.getTmConfiguration(), rcptno, rcptDate);
                    AdvanceRegnNo_dobj dobj = NewImpl.getAdvanceRegNoDetails(rcptno);
                    setAdvRegnNoDobj(dobj);
                    String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
                    getList_adv_district().clear();

                    for (int i = 0; i < data.length; i++) {
                        if (data[i][2].trim().equals(getAdvRegnNoDobj().getState_cd())) {
                            getList_adv_district().add(new SelectItem(data[i][0], data[i][1]));
                        }
                    }
                    if (!appl_details.getOwnerDetailsDobj().getOwner_name().equalsIgnoreCase(dobj.getOwner_name())
                            || !appl_details.getOwnerDetailsDobj().getF_name().equalsIgnoreCase(dobj.getF_name())) {
                        JSFUtils.setFacesMessage("Owner Name and Father Name does not match!", null, JSFUtils.ERROR);
                        AdvanceRegnNo_dobj dobj1 = new AdvanceRegnNo_dobj();
                        setAdvRegnNoDobj(dobj1);
                    }
                } else {
                    throw new VahanException("Receipt Date Not Found");
                }
            }

        } catch (VahanException ex) {
            JSFUtils.setFacesMessage(ex.getMessage() + "Please Try Again!", null, JSFUtils.ERROR);
        }
    }

    public void advanceExitListener() {
        if (advRegnCheck) {
            setAdvRegnNoDobj(new AdvanceRegnNo_dobj());
            setAdvRegnCheck(false);
        }
    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return null;
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return null;
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
     * @return the retenRegnNoDobjPrev
     */
    public RetenRegnNo_dobj getRetenRegnNoDobjPrev() {
        return retenRegnNoDobjPrev;
    }

    /**
     * @param retenRegnNoDobjPrev the retenRegnNoDobjPrev to set
     */
    public void setRetenRegnNoDobjPrev(RetenRegnNo_dobj retenRegnNoDobjPrev) {
        this.retenRegnNoDobjPrev = retenRegnNoDobjPrev;
    }

    /**
     * @return the retenRegnNoDobj
     */
    public RetenRegnNo_dobj getRetenRegnNoDobj() {
        return retenRegnNoDobj;
    }

    /**
     * @param retenRegnNoDobj the retenRegnNoDobj to set
     */
    public void setRetenRegnNoDobj(RetenRegnNo_dobj retenRegnNoDobj) {
        this.retenRegnNoDobj = retenRegnNoDobj;
    }

    /**
     * @param compBeanList the compBeanList to set
     */
    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    /**
     * @return the listExistingOwnerDetails
     */
    public List<OwnerDetailsDobj> getListExistingOwnerDetails() {
        return listExistingOwnerDetails;
    }

    /**
     * @param listExistingOwnerDetails the listExistingOwnerDetails to set
     */
    public void setListExistingOwnerDetails(List<OwnerDetailsDobj> listExistingOwnerDetails) {
        this.listExistingOwnerDetails = listExistingOwnerDetails;
    }

    public boolean isAdvRegnCheck() {
        return advRegnCheck;
    }

    public void setAdvRegnCheck(boolean advRegnCheck) {
        this.advRegnCheck = advRegnCheck;
    }

    public boolean isAdvRegnCheckDialogue() {
        return advRegnCheckDialogue;
    }

    public void setAdvRegnCheckDialogue(boolean advRegnCheckDialogue) {
        this.advRegnCheckDialogue = advRegnCheckDialogue;
    }

    public AdvanceRegnNo_dobj getAdvRegnNoDobj() {
        return advRegnNoDobj;
    }

    public void setAdvRegnNoDobj(AdvanceRegnNo_dobj advRegnNoDobj) {
        this.advRegnNoDobj = advRegnNoDobj;
    }

    public boolean isDisableAdvRegnCheck() {
        return disableAdvRegnCheck;
    }

    public void setDisableAdvRegnCheck(boolean disableAdvRegnCheck) {
        this.disableAdvRegnCheck = disableAdvRegnCheck;
    }

    public List getList_adv_district() {
        return list_adv_district;
    }

    public void setList_adv_district(List list_adv_district) {
        this.list_adv_district = list_adv_district;
    }

    public List getList_c_district() {
        return list_c_district;
    }

    public void setList_c_district(List list_c_district) {
        this.list_c_district = list_c_district;
    }

    public List getList_c_state() {
        return list_c_state;
    }

    public void setList_c_state(List list_c_state) {
        this.list_c_state = list_c_state;
    }

    public String getRegnNumberAllotedMsg() {
        return regnNumberAllotedMsg;
    }

    public void setRegnNumberAllotedMsg(String regnNumberAllotedMsg) {
        this.regnNumberAllotedMsg = regnNumberAllotedMsg;
    }

    public List getListOwnerCatg() {
        return listOwnerCatg;
    }

    public void setListOwnerCatg(List listOwnerCatg) {
        this.listOwnerCatg = listOwnerCatg;
    }

    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }
}