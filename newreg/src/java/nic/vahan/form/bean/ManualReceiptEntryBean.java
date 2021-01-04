/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.ManualReceiptEntryDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.ApplicationDisposeImpl;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.ManualReceiptEntryImpl;
import nic.vahan.form.impl.RegVehCancelReceiptImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.primefaces.PrimeFaces;

/**
 *
 * @author DELL
 */
@ViewScoped
@ManagedBean(name = "manualRcptEntryBean")
public class ManualReceiptEntryBean extends AbstractApplBean implements ApproveDisapproveInterface, Serializable {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ManualReceiptEntryBean.class);
    private Map<String, Integer> purCodeList = null;
    private List<ComparisonBean> prevChangedDataList;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<Status_dobj> applStatus = new ArrayList<>();
    private boolean isdisabledTransactionNo = false;
    private boolean isdisabledAmount = false;
    private ManualReceiptEntryDobj manualrcptdobj = new ManualReceiptEntryDobj();
    private ManualReceiptEntryDobj manualrcptDobjPrev;
    String msg = "No Message";
    private boolean renderExemptionOD = false;
    private String appl_no = "";
    private boolean saveButton = false;
    private boolean applApproved = false;
    private String sucessMessage;
    private List<ManualReceiptEntryDobj> manualRcptRecordList;
    private long grandTotal = 0l;

    @PostConstruct
    public void init() {
        ManualReceiptEntryImpl manualRcptImpl = new ManualReceiptEntryImpl();
        try {
            saveButton = true;
            if (getAppl_details() != null && getAppl_details().getAppl_no() != null) {
                manualrcptdobj = manualRcptImpl.getApplicationDetails(getAppl_details().getAppl_no());
                if (manualrcptdobj != null) {
                    manualrcptDobjPrev = (ManualReceiptEntryDobj) manualrcptdobj.clone();
                    isdisabledTransactionNo = true;
                    saveButton = false;
                } else {
                    throw new VahanException("Error in getting Manual receipt Application Details");
                }
            }
            prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());

        } catch (VahanException e) {
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage(TableConstants.SomthingWentWrong);

        }
    }

    public void checkvalidApplNo() {
        if (!CommonUtils.isNullOrBlank(manualrcptdobj.getTrans_appl_no())) {
            purCodeList = ApplicationDisposeImpl.listApplicationDispose(getManualrcptdobj().getTrans_appl_no(), Util.getUserStateCode(), Util.getUserOffCode(), Long.parseLong(Util.getEmpCode()), Util.getUserCategory());
            if (purCodeList == null || purCodeList.isEmpty()) {
                reset();
                msg = "There is no Valid Transaction Against this Application No";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
            }
        } else {
            msg = "Please Enter Application Number";
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
        }
        if (!purCodeList.isEmpty()) {
            fillManualRcptRecordDataTable();
        }
    }

    public void checkApplicationStatus() {
        try {
            if (!CommonUtils.isNullOrBlank(manualrcptdobj.getTrans_appl_no())) {
                applStatus = ServerUtil.applicationStatusByApplNo(manualrcptdobj.getTrans_appl_no(), Util.getUserStateCode());
                if (applStatus == null || applStatus.isEmpty() || applStatus.size() < 1) {
                    msg = "Either Application No is Invalid or Application No is Approved.Please try with different Application No";
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                    manualrcptdobj.setTrans_appl_no("");
                    return;
                } else {
                    applApproved = ManualReceiptEntryImpl.isFeePaidForVehicle(manualrcptdobj.getTrans_appl_no());
                    if (applApproved) {
                        msg = "Fee is already paid For this Application No.Please try with different Application No";
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                        manualrcptdobj.setTrans_appl_no("");
                    }
                }
            } else {
                msg = "Please Enter Valid Transaction Application Number";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
            }
        } catch (VahanException e) {
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage(TableConstants.SomthingWentWrong);

        }
    }

    public void checkValidRcptNo() throws VahanException {
        try {
            if (!CommonUtils.isNullOrBlank(manualrcptdobj.getRcptNo())) {
                boolean isBalanceFeeTaxReceipt = new RegVehCancelReceiptImpl().isBalanceFeeTaxCollectionReceipt(null, manualrcptdobj.getRcptNo(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                if (isBalanceFeeTaxReceipt) {
                    String[] arr = ManualReceiptEntryImpl.getBalanceFeeTaxAmount(manualrcptdobj.getRcptNo()).split("`");
                    if (arr != null && arr.length == 2) {
                        manualrcptdobj.setAmount(Long.valueOf(arr[0]));
                        manualrcptdobj.setReceipt_dt(DateUtil.parseDateFromYYYYMMDD(arr[1]));
                    }
                    isdisabledAmount = true;
                } else {
                    String rcptNumber = ManualReceiptEntryImpl.getRcptNumberDetails(manualrcptdobj.getRcptNo());
                    if (rcptNumber != null) {
                        reset();
                        msg = "This Receipt Number Is Already In Use,Please Use Different Receipt No.";
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                        return;
                    }
                }
            }
        } catch (VahanException e) {
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage(TableConstants.SomthingWentWrong);
        }
    }

    public void getManualRcptAmount() throws VahanException {
        try {
            if (!CommonUtils.isNullOrBlank(manualrcptdobj.getRcptNo())) {
                manualrcptdobj = ManualReceiptEntryImpl.getManualFeeAmount(manualrcptdobj.getRcptNo());
                if (manualrcptdobj != null) {
                    manualrcptdobj.getAmount();
                    manualrcptdobj.getReceipt_dt();
                } else {
                    msg = "Please Use Valid Receipt Number.";
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                    manualrcptdobj.setRcptNo("");
                }
            }
        } catch (VahanException e) {
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage(TableConstants.SomthingWentWrong);
        }

    }

    public void reset() {
        manualrcptdobj.setTrans_appl_no("");
        manualrcptdobj.setRcptNo("");
        manualrcptdobj.getAmount();
    }

    @Override
    public String save() {
        String returnLocation = "";
        String msg = null;
        try {
            Status_dobj statusDobj = new Status_dobj();
            statusDobj.setAction_cd(appl_details.getCurrent_action_cd());
            statusDobj.setAppl_dt(appl_details.getAppl_dt());
            statusDobj.setAppl_no(appl_details.getAppl_no());
            statusDobj.setRegn_no(appl_details.getRegn_no());
            statusDobj.setPur_cd(appl_details.getPur_cd());
            statusDobj.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            statusDobj.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            statusDobj.setStatus(getApp_disapp_dobj().getNew_status());
            statusDobj.setCurrent_role(appl_details.getCurrent_role());
            if (!CommonUtils.isNullOrBlank(manualrcptdobj.getRcptNo())) {
                ManualReceiptEntryImpl manualReceiptEntryImpl = new ManualReceiptEntryImpl();
                if (appl_details.getAppl_no() == null) {
                    appl_no = manualReceiptEntryImpl.insertDataintoVa(getManualrcptdobj(), ComparisonBeanImpl.changedDataContents(compareChanges()), statusDobj);
                    PrimeFaces.current().ajax().update("saveDialog");
                    PrimeFaces.current().executeScript("PF('saveMnual').show()");
                    return null;
                } else if (statusDobj.getAppl_no() != null) {
                    appl_no = manualReceiptEntryImpl.insertDataintoVa(getManualrcptdobj(), ComparisonBeanImpl.changedDataContents(compareChanges()), statusDobj);
                    returnLocation = "seatwork";
                } else {
                    JSFUtils.showMessage("Please Enter Application Number");
                }
            } else {
                JSFUtils.showMessage("Please Enter Receipt Number");
            }
        } catch (VahanException e) {
            JSFUtils.showMessage(e.getMessage());
            returnLocation = "";
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage("Error in Data Saving.");
            returnLocation = "";
        }
        return returnLocation;

    }

    @Override
    public String saveAndMoveFile() {
        String returnLocation = "";
        try {
            Status_dobj statusDobj = new Status_dobj();
            statusDobj.setAction_cd(appl_details.getCurrent_action_cd());
            statusDobj.setAppl_dt(appl_details.getAppl_dt());
            statusDobj.setAppl_no(appl_details.getAppl_no());
            statusDobj.setRegn_no(appl_details.getRegn_no());
            statusDobj.setPur_cd(appl_details.getPur_cd());
            statusDobj.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            statusDobj.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            statusDobj.setStatus(getApp_disapp_dobj().getNew_status());
            statusDobj.setCurrent_role(appl_details.getCurrent_role());
            if (!CommonUtils.isNullOrBlank(manualrcptdobj.getRcptNo())) {
                ManualReceiptEntryImpl manualReceiptEntryImpl = new ManualReceiptEntryImpl();
                manualReceiptEntryImpl.updateInsertManualRecieptEntry(getManualrcptdobj(), ComparisonBeanImpl.changedDataContents(compareChanges()), statusDobj);
                returnLocation = "seatwork";
            }
        } catch (VahanException e) {
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage("Error in Data Saving.");
        }
        return returnLocation;

    }

    @Override
    public List<ComparisonBean> compareChanges() {

        if (!compBeanList.isEmpty()) {
            compBeanList.clear();
        }
        if (manualrcptDobjPrev == null) {
            return compBeanList;
        }
        Compare("State Code", manualrcptDobjPrev.getState_cd(), manualrcptdobj.getState_cd(), (ArrayList) compBeanList);
        Compare("Office Code", manualrcptDobjPrev.getOff_cd(), manualrcptdobj.getOff_cd(), (ArrayList) compBeanList);
        Compare("Application No", manualrcptDobjPrev.getApplno(), manualrcptdobj.getApplno(), (ArrayList) compBeanList);
        Compare("Transaction Appl No", manualrcptDobjPrev.getTrans_appl_no(), manualrcptdobj.getTrans_appl_no(), (ArrayList) compBeanList);
        Compare("Manual ReceiptNO", manualrcptDobjPrev.getRcptNo(), manualrcptdobj.getRcptNo(), (ArrayList) compBeanList);
        Compare("Receipt Date", manualrcptDobjPrev.getReceipt_dt(), manualrcptdobj.getReceipt_dt(), compBeanList);
        Compare("Amount", manualrcptDobjPrev.getAmount(), manualrcptdobj.getAmount(), (ArrayList) compBeanList);

        return compBeanList;
    }

    public List<ComparisonBean> addToComapreChangesList(List<ComparisonBean> compBeanListPrev) throws VahanException {
        List<ComparisonBean> list = compareChanges();
        if (compBeanListPrev == null) {
            compBeanListPrev = new ArrayList<>();
        }
        if (!list.isEmpty()) {
            compBeanListPrev.addAll(list);
        }
        return compBeanListPrev;
    }

    public String updateManualRcptAmount() {
        String path = "";
        boolean flag = false;
        try {
            ManualReceiptEntryImpl manualRcptDtls = new ManualReceiptEntryImpl();
            flag = manualRcptDtls.updateManulaRcptAmount(manualrcptdobj);
            if (flag) {
                sucessMessage = "Manual Receipt Amount Successfully Updated For Transaction Application No :" + manualrcptdobj.getTrans_appl_no() + " ";
                PrimeFaces.current().ajax().update("op_show_panel_success");
                PrimeFaces.current().executeScript("PF('successDlgVar').show();");

            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Technical Error"));
            }
        } catch (VahanException e) {
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage("Error in Data Saving.");
        }
        return path;
    }

    private void fillManualRcptRecordDataTable() {
        try {
            manualRcptRecordList = ManualReceiptEntryImpl.fillMaualRecordDataTable(manualrcptdobj);
            for (ManualReceiptEntryDobj dobj : manualRcptRecordList) {
                grandTotal = grandTotal + dobj.getAmount();
            }
        } catch (VahanException e) {
            LOGGER.error(e.getMessage());
            JSFUtils.setFacesMessage("Technical Error.", null, JSFUtils.ERROR);
        }
    }

    /**
     * @return the renderExemptionOD
     */
    public boolean isRenderExemptionOD() {
        return renderExemptionOD;
    }

    /**
     * @param renderExemptionOD the renderExemptionOD to set
     */
    public void setRenderExemptionOD(boolean renderExemptionOD) {
        this.renderExemptionOD = renderExemptionOD;
    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return prevChangedDataList; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the manualrcptdobj
     */
    public ManualReceiptEntryDobj getManualrcptdobj() {
        return manualrcptdobj;
    }

    /**
     * @param manualrcptdobj the manualrcptdobj to set
     */
    public void setManualrcptdobj(ManualReceiptEntryDobj manualrcptdobj) {
        this.manualrcptdobj = manualrcptdobj;
    }

    /**
     * @return the isdisabledTransactionNo
     */
    public boolean isIsdisabledTransactionNo() {
        return isdisabledTransactionNo;
    }

    /**
     * @param isdisabledTransactionNo the isdisabledTransactionNo to set
     */
    public void setIsdisabledTransactionNo(boolean isdisabledTransactionNo) {
        this.isdisabledTransactionNo = isdisabledTransactionNo;
    }

    /**
     * @return the isdisabledAmount
     */
    public boolean isIsdisabledAmount() {
        return isdisabledAmount;
    }

    /**
     * @param isdisabledAmount the isdisabledAmount to set
     */
    public void setIsdisabledAmount(boolean isdisabledAmount) {
        this.isdisabledAmount = isdisabledAmount;
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
     * @return the saveButton
     */
    public boolean isSaveButton() {
        return saveButton;
    }

    /**
     * @param saveButton the saveButton to set
     */
    public void setSaveButton(boolean saveButton) {
        this.saveButton = saveButton;
    }

    /**
     * @return the applStatus
     */
    public List<Status_dobj> getApplStatus() {
        return applStatus;
    }

    /**
     * @param applStatus the applStatus to set
     */
    public void setApplStatus(List<Status_dobj> applStatus) {
        this.applStatus = applStatus;
    }

    /**
     * @return the applApproved
     */
    public boolean isApplApproved() {
        return applApproved;
    }

    /**
     * @param applApproved the applApproved to set
     */
    public void setApplApproved(boolean applApproved) {
        this.applApproved = applApproved;
    }

    /**
     * @return the sucessMessage
     */
    public String getSucessMessage() {
        return sucessMessage;
    }

    /**
     * @param sucessMessage the sucessMessage to set
     */
    public void setSucessMessage(String sucessMessage) {
        this.sucessMessage = sucessMessage;
    }

    /**
     * @return the manualRcptRecordList
     */
    public List<ManualReceiptEntryDobj> getManualRcptRecordList() {
        return manualRcptRecordList;
    }

    /**
     * @param manualRcptRecordList the manualRcptRecordList to set
     */
    public void setManualRcptRecordList(List<ManualReceiptEntryDobj> manualRcptRecordList) {
        this.manualRcptRecordList = manualRcptRecordList;
    }

    public long getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(long grandTotal) {
        this.grandTotal = grandTotal;
    }
}
