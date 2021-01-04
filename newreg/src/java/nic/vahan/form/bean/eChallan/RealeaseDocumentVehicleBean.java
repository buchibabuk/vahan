package nic.vahan.form.bean.eChallan;

import java.io.Serializable;
import nic.vahan.form.impl.eChallan.DisposalOfChallanImpl;
import nic.vahan.form.impl.eChallan.RealeaseDocumentVehicleImpl;
import nic.vahan.form.dobj.eChallan.RealesedDocumentVehicleDobj;
import nic.vahan.form.dobj.eChallan.ReleaseDocumentDetailsDobj;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static nic.vahan.server.ServerUtil.Compare;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "realedocvehicle")
@ViewScoped
public class RealeaseDocumentVehicleBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static Logger LOGGER = Logger.getLogger(RealeaseDocumentVehicleBean.class);
    private String applicationNo = "";
    private String bookNo = "";
    private String challanNo = "";
    private String vehicleNo = "";
    private String releaseDate;
    private String releaseOffice;
    private String releaseRemark;
    private String tfAccussed;
    private RealesedDocumentVehicleDobj realesedDocdobj = new RealesedDocumentVehicleDobj();
    private RealesedDocumentVehicleDobj realesedDocdobjPrev = new RealesedDocumentVehicleDobj();
    private List<ReleaseDocumentDetailsDobj> accusedList = new <ReleaseDocumentDetailsDobj>ArrayList();
    private String errMsg = "";
    private String tfImpndPlace;
    private Date tfImpndDate;
    private String tfOfficerToContact;
    private String tfSezNO;
    private String tfAccused;
    private Date tfRealasedate;
    private String tfRealeseoffice;
    private String tfRemarkany;
    private List<ReleaseDocumentDetailsDobj> documentList = new ArrayList<ReleaseDocumentDetailsDobj>();
    private String category = "";
    private String isVehicleImpound = "";
    private String isDocumentImpound = "";
    private Date maxDate = new Date();
    private Date minDate = new Date();
    boolean disableRelaseButton;
    boolean disablePrintButton;
    boolean showApproveDisApprovePanelForSave;
    boolean showPanelForSave;
    private List<ComparisonBean> compBeanList = new ArrayList<ComparisonBean>();

    public RealeaseDocumentVehicleBean() {
        intilize();
    }

    private void intilize() {
        setDisablePrintButton(false);
        setDisableRelaseButton(true);
        try {
            setTfRealasedate(new Date());
            String actionCode = String.valueOf(appl_details.getCurrent_action_cd());
            if (Integer.parseInt(actionCode) == TableConstants.VM_ROLE_ENFORCEMENT_RELEASE_DOC_VEHICLE_APPROVE) {
                checkchallandata(actionCode);
                setShowApproveDisApprovePanelForSave(true);
                setShowPanelForSave(false);
            } else {
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);

        }
    }

    public void checkchallandata(String action_Code) {
        try {
            applicationNo = String.valueOf(appl_details.getAppl_no());
            if ("".equals(applicationNo) || CommonUtils.isNullOrBlank(applicationNo)) {
                JSFUtils.setFacesMessage("Please Enter The Application NO ", "message", JSFUtils.FATAL);
                return;
            }
            RealeaseDocumentVehicleImpl releasedocdao = new RealeaseDocumentVehicleImpl();
            DisposalOfChallanImpl dao = new DisposalOfChallanImpl();
            if (!releasedocdao.challanExistance(applicationNo)) {
                errMsg = " Challan is not available";
                JSFUtils.showMessage(errMsg);

            } else if (!releasedocdao.chkChallanSettlement(applicationNo)) {
                errMsg = "Document  is not allowed to Realese, settlement is pending";
                JSFUtils.showMessage(errMsg);

            } else if (!releasedocdao.chkPaymentReciept(applicationNo)) {
                errMsg = "Document  is not allowed to Realese, Payment of Fee or Tax is Pending";
                JSFUtils.showMessage(errMsg);

            } else if (!releasedocdao.checkChallanDisposal(applicationNo)) {
                errMsg = "Document  is not allowed to Realese,Disposal is pending";
                JSFUtils.showMessage(errMsg);

            } else {
                getDetailsOfVehicleAndDocImpounded(applicationNo, action_Code);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);

        }
    }

    public void getDetailsOfVehicleAndDocImpounded(String applicationNO, String action_Code) {
        try {
            realesedDocdobj = new RealeaseDocumentVehicleImpl().realaseDocument(applicationNO);
            if (realesedDocdobj != null) {
                setReleaseDate(realesedDocdobj.getRealesdocdetails().getRealeasedate());
                setReleaseOffice(realesedDocdobj.getRealesdocdetails().getRealeseofficer());
                setReleaseRemark(realesedDocdobj.getRealesdocdetails().getRemarkany());
            }
            realesedDocdobjPrev = (RealesedDocumentVehicleDobj) realesedDocdobj.clone();
            List<ReleaseDocumentDetailsDobj> dobjlist = (List<ReleaseDocumentDetailsDobj>) realesedDocdobj.getRealesdocdtlist();
            for (ReleaseDocumentDetailsDobj dobj : dobjlist) {
                isDocumentImpound = dobj.getDocumentImpound();
                isVehicleImpound = dobj.getVehicleImpound();


            }
            vehicleNo = realesedDocdobj.getRealesdocdetails().getVehicleNo();
            if (isDocumentImpound.equalsIgnoreCase("Y") || isVehicleImpound.equalsIgnoreCase("Y")) {
                realesedDocdobj = new RealeaseDocumentVehicleImpl().fetchDocumentVehicleImpound(applicationNO, action_Code);
                if (realesedDocdobj.getMessgae().length() > 0) {
                    JSFUtils.showMessage(realesedDocdobj.getMessgae());

                } else {
                    documentList = realesedDocdobj.getRealesdocdtlist();
                    setTfImpndPlace(realesedDocdobj.getRealesdocdetails().getVehicleimpouddobj().getImpndPlace());
                    setTfImpndDate(realesedDocdobj.getRealesdocdetails().getVehicleimpouddobj().getImpndDate());
                    setTfOfficerToContact(realesedDocdobj.getRealesdocdetails().getVehicleimpouddobj().getOfficerToContact());
                    setTfSezNO(realesedDocdobj.getRealesdocdetails().getVehicleimpouddobj().getSezNO());
                    setTfAccused(realesedDocdobj.getRealesdocdetails().getVehicleimpouddobj().getAccused());
                    if (realesedDocdobj.getRealesdocdetails().getRealeasedate() != null) {
                        setTfRealasedate(JSFUtils.getStringToDateyyyyMMdd(realesedDocdobj.getRealesdocdetails().getRealeasedate()));
                        setTfRealeseoffice(realesedDocdobj.getRealesdocdetails().getRealeseofficer());
                        setTfRemarkany(realesedDocdobj.getRealesdocdetails().getRemarkany());
                    }
                }
            } else {
                JSFUtils.setFacesMessage("Document/Vehicle Not Impound For This Application No ", bookNo, JSFUtils.INFO);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void saveRealeseDocumentVechileImpdData() {
        if ("".equals(applicationNo) || CommonUtils.isNullOrBlank(applicationNo)) {
            JSFUtils.setFacesMessage("Please Enter The Application NO ", "message", JSFUtils.FATAL);
            return;
        }
        boolean flag = false;
        String applicationNo = getApplicationNo();
        String vehicleno = getVehicleNo();
        String realesdate = getTfRealasedate().toString();
        String realeseofficer = getTfRealeseoffice().toString().toUpperCase().trim();
        String remark = getTfRemarkany().toString().toUpperCase().trim();
        realesedDocdobj.getRealesdocdetails().setRealeasedate(realesdate);
        realesedDocdobj.getRealesdocdetails().setRealeseofficer(realeseofficer);
        realesedDocdobj.getRealesdocdetails().setRemarkany(remark);
        Status_dobj statusDobj = new Status_dobj();
        statusDobj.setRegn_no(vehicleno);
        statusDobj.setPur_cd(TableConstants.VM_MAST_ENFORCEMENT_RELEASE_DOC_VEHICLE);
        statusDobj.setAction_cd(TableConstants.VM_ROLE_ENFORCEMENT_RELEASE_DOC_VEHICLE_ENTRY);
        statusDobj.setFlow_slno(1);
        statusDobj.setFile_movement_slno(1);
        statusDobj.setState_cd(Util.getUserStateCode());
        statusDobj.setOff_cd(Util.getSelectedSeat().getOff_cd());
        statusDobj.setEmp_cd(0);
        statusDobj.setAppl_dt(appl_details.getAppl_dt());
        statusDobj.setAppl_no(appl_details.getAppl_no());
        statusDobj.setPur_cd(appl_details.getPur_cd());
        statusDobj.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
        statusDobj.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
        statusDobj.setStatus(getApp_disapp_dobj().getNew_status());
        statusDobj.setCurrent_role(appl_details.getCurrent_role());

        if (realesedDocdobj.getRealesdocdtlist().size() > 0) {
            try {
                flag = new RealeaseDocumentVehicleImpl().updateDocumentVehicleImpound(applicationNo, vehicleno, realesedDocdobj, isDocumentImpound, isVehicleImpound, statusDobj);
                if (flag) {
                    reset();
                    JSFUtils.setFacesMessage("document Or Vehicle has been realesed", "message", JSFUtils.INFO);
                }
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

    }

    public void reset() {
        bookNo = "";
        accusedList.clear();
        category = "";
        challanNo = "";
        documentList.clear();
        realesedDocdobj = null;
        errMsg = "";
        vehicleNo = "";
        setTfAccused("");
        setTfImpndDate(null);
        setTfImpndPlace("");
        setTfOfficerToContact("");
        setTfRealasedate(null);
        setTfRealeseoffice("");
        setTfRemarkany("");
        setTfSezNO("");

    }

    @Override
    public String save() {
        String returnLocation = "";
        saveRealeseDocumentVechileImpdData();
        returnLocation = "seatwork";
        return returnLocation;
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        if (realesedDocdobjPrev == null) {
            return compBeanList;
        }
        compBeanList.clear();
        Compare("Release Date", realesedDocdobjPrev.getRealesdocdetails().getRealeasedate(), getReleaseDate(), compBeanList);
        Compare("Release Officer", realesedDocdobjPrev.getRealesdocdetails().getRealeseofficer(), getReleaseOffice(), compBeanList);
        Compare("Release Remark", realesedDocdobjPrev.getRealesdocdetails().getRemarkany(), getReleaseRemark(), compBeanList);
        return compBeanList;

    }

    public void confirmprintCertificate() {
        // RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('printParticularRelease').show()");
    }

    @Override
    public String saveAndMoveFile() {
        boolean flag = true;
        String actionCode = String.valueOf(appl_details.getCurrent_action_cd());

        String returnLocation = "";
        RealeaseDocumentVehicleImpl impl = null;

        if ("".equals(applicationNo) || CommonUtils.isNullOrBlank(applicationNo)) {
            JSFUtils.setFacesMessage("Please Enter The Application NO ", "message", JSFUtils.FATAL);
            flag = false;
        }

        String applicationNo = getApplicationNo();
        String vehicleno = getVehicleNo();
        String realesdate = getTfRealasedate().toString();
        String realeseofficer = getTfRealeseoffice().toString().toUpperCase().trim();
        String remark = getTfRemarkany().toString().toUpperCase().trim();
        realesedDocdobj.getRealesdocdetails().setRealeasedate(realesdate);
        realesedDocdobj.getRealesdocdetails().setRealeseofficer(realeseofficer);
        realesedDocdobj.getRealesdocdetails().setRemarkany(remark);

        try {
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());
            if (flag) {
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)) {
                    if ((actionCode == null ? (String.valueOf(TableConstants.VM_ROLE_ENFORCEMENT_RELEASE_DOC_VEHICLE_APPROVE)) == null : actionCode.equals(String.valueOf(TableConstants.VM_ROLE_ENFORCEMENT_RELEASE_DOC_VEHICLE_APPROVE)))) {
                        impl = new RealeaseDocumentVehicleImpl();
                        impl.movetoapprove(Integer.parseInt(actionCode), status, ComparisonBeanImpl.changedDataContents(compareChanges()), applicationNo, vehicleno, realesedDocdobj, isDocumentImpound, isVehicleImpound);
                        setShowApproveDisApprovePanelForSave(true);
                        setShowPanelForSave(false);
                    }
                }
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {
                    status.setPrev_action_cd_selected(app_disapp_dobj.getPre_action_code());
                    impl = new RealeaseDocumentVehicleImpl();
                    impl.reback(actionCode, status, ComparisonBeanImpl.changedDataContents(compareChanges()), applicationNo, vehicleno, realesedDocdobj, isDocumentImpound, isVehicleImpound);
                }
            }
            setDisablePrintButton(true);
            setDisableRelaseButton(false);
            reset();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return returnLocation;
    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    public String getChallanNo() {
        return challanNo;
    }

    public void setChallanNo(String challanNo) {
        this.challanNo = challanNo;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public RealesedDocumentVehicleDobj getRealesedDocdobj() {
        return realesedDocdobj;
    }

    public void setRealesedDocdobj(RealesedDocumentVehicleDobj realesedDocdobj) {
        this.realesedDocdobj = realesedDocdobj;
    }

    public List<ReleaseDocumentDetailsDobj> getAccusedList() {
        return accusedList;
    }

    public void setAccusedList(List<ReleaseDocumentDetailsDobj> accusedList) {
        this.accusedList = accusedList;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getTfSezNO() {
        return tfSezNO;
    }

    public void setTfSezNO(String tfSezNO) {
        this.tfSezNO = tfSezNO;
    }

    public List<ReleaseDocumentDetailsDobj> getDocumentList() {
        return documentList;
    }

    public void setDocumentList(List<ReleaseDocumentDetailsDobj> documentList) {
        this.documentList = documentList;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    public String getIsVehicleImpound() {
        return isVehicleImpound;
    }

    public void setIsVehicleImpound(String isVehicleImpound) {
        this.isVehicleImpound = isVehicleImpound;
    }

    public String getIsDocumentImpound() {
        return isDocumentImpound;
    }

    public void setIsDocumentImpound(String isDocumentImpound) {
        this.isDocumentImpound = isDocumentImpound;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public boolean isDisableRelaseButton() {
        return disableRelaseButton;
    }

    public void setDisableRelaseButton(boolean disableRelaseButton) {
        this.disableRelaseButton = disableRelaseButton;
    }

    public boolean isShowApproveDisApprovePanelForSave() {
        return showApproveDisApprovePanelForSave;
    }

    public void setShowApproveDisApprovePanelForSave(boolean showApproveDisApprovePanelForSave) {
        this.showApproveDisApprovePanelForSave = showApproveDisApprovePanelForSave;
    }

    public boolean isShowPanelForSave() {
        return showPanelForSave;
    }

    public void setShowPanelForSave(boolean showPanelForSave) {
        this.showPanelForSave = showPanelForSave;
    }

    public boolean isDisablePrintButton() {
        return disablePrintButton;
    }

    public void setDisablePrintButton(boolean disablePrintButton) {
        this.disablePrintButton = disablePrintButton;
    }

    /**
     * @return the releaseDate
     */
    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * @param releaseDate the releaseDate to set
     */
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * @return the releaseOffice
     */
    public String getReleaseOffice() {
        return releaseOffice;
    }

    /**
     * @param releaseOffice the releaseOffice to set
     */
    public void setReleaseOffice(String releaseOffice) {
        this.releaseOffice = releaseOffice;
    }

    /**
     * @return the releaseRemark
     */
    public String getReleaseRemark() {
        return releaseRemark;
    }

    /**
     * @param releaseRemark the releaseRemark to set
     */
    public void setReleaseRemark(String releaseRemark) {
        this.releaseRemark = releaseRemark;
    }

    public String getTfAccussed() {
        return tfAccussed;
    }

    public void setTfAccussed(String tfAccussed) {
        this.tfAccussed = tfAccussed;
    }

    public RealesedDocumentVehicleDobj getRealesedDocdobjPrev() {
        return realesedDocdobjPrev;
    }

    public void setRealesedDocdobjPrev(RealesedDocumentVehicleDobj realesedDocdobjPrev) {
        this.realesedDocdobjPrev = realesedDocdobjPrev;
    }

    public String getTfImpndPlace() {
        return tfImpndPlace;
    }

    public void setTfImpndPlace(String tfImpndPlace) {
        this.tfImpndPlace = tfImpndPlace;
    }

    public Date getTfImpndDate() {
        return tfImpndDate;
    }

    public void setTfImpndDate(Date tfImpndDate) {
        this.tfImpndDate = tfImpndDate;
    }

    public String getTfOfficerToContact() {
        return tfOfficerToContact;
    }

    public void setTfOfficerToContact(String tfOfficerToContact) {
        this.tfOfficerToContact = tfOfficerToContact;
    }

    public String getTfAccused() {
        return tfAccused;
    }

    public void setTfAccused(String tfAccused) {
        this.tfAccused = tfAccused;
    }

    public Date getTfRealasedate() {
        return tfRealasedate;
    }

    public void setTfRealasedate(Date tfRealasedate) {
        this.tfRealasedate = tfRealasedate;
    }

    public String getTfRealeseoffice() {
        return tfRealeseoffice;
    }

    public void setTfRealeseoffice(String tfRealeseoffice) {
        this.tfRealeseoffice = tfRealeseoffice;
    }

    public String getTfRemarkany() {
        return tfRemarkany;
    }

    public void setTfRemarkany(String tfRemarkany) {
        this.tfRemarkany = tfRemarkany;
    }
}
