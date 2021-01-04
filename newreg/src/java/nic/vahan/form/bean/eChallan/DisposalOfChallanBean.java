package nic.vahan.form.bean.eChallan;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import nic.vahan.form.impl.eChallan.DisposalOfChallanImpl;
import nic.vahan.form.dobj.eChallan.CompoundingFeeDobj;
import nic.vahan.form.dobj.eChallan.DocTableDobj;
import nic.vahan.form.dobj.eChallan.OffencesDobj;
import nic.vahan.form.dobj.eChallan.SaveChallanDobj;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.vahan.server.CommonUtils;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.rto.vahan.common.VahanException;
import nic.java.util.DateUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.eChallan.AccusedDetailsDobj;
import nic.vahan.form.dobj.eChallan.ChallanConfigrationDobj;
import nic.vahan.form.dobj.eChallan.CompoundingInOfficeDobj;
import nic.vahan.form.dobj.eChallan.DisposeChallanDobj;
import nic.vahan.form.dobj.eChallan.VehcleOffenceDobj;
import nic.vahan.form.dobj.eChallan.WitnessdetailDobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.eChallan.ChallanUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "dispChallan")
@ViewScoped
public class DisposalOfChallanBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static Logger LOGGER = Logger.getLogger(DisposalOfChallanBean.class);
    private String applicationNo = null;
    private String bookNo = null;
    private String chalNo = null;
    private String vehNo = null;
    private String ncrNo = null;
    private String vehState = null;
    private String chalDate = null;
    private String vehRto = null;
    private String chalTime = null;
    private String ownName = null;
    private String chalOfficer = null;
    private String chasiNo = null;
    private String chalPlace = null;
    private String vehClass = null;
    private String fuel = null;
    private String stdCap = null;
    private String seatCap = null;
    private String ladn = null;
    private String sleeprCap = null;
    private String color = null;
    private String courtName = null;
    private Date hearingDt = null;
    private String impDt = null;
    private String placeOfImp = null;
    private String sezureNo = null;
    private String officerNo = null;
    private String statusMsg = null;
    private String dateOfDispose = null;
    private String remarksOfDispose = null;
    private List<VehcleOffenceDobj> offenceList = new ArrayList<VehcleOffenceDobj>();
    private List<OffencesDobj> offenceDetails = new ArrayList<OffencesDobj>();
    private List<DocTableDobj> docDetails = new ArrayList<DocTableDobj>();
    private List<CompoundingFeeDobj> listForCompoundigFee = new ArrayList<CompoundingFeeDobj>();
    private SaveChallanDobj dobjOwnerChallan = null;
    private SaveChallanDobj previousDobjOwnerChallan = null;
    private Date maxDate = new Date();
    private Date minDate = new Date();
    boolean disableSaveButton;
    boolean showApproveDisApprovePanelForSave;
    boolean showPanelForSave;
    boolean disableApplicationNoTextBox = true;
    private List<ComparisonBean> compBeanList = new ArrayList<ComparisonBean>();
    private List<ComparisonBean> prevChangedDataList;
    VehcleOffenceDobj vehOffDobjPrevious = null;
    private List<WitnessdetailDobj> witnessDetails = new ArrayList<>();
    private List<AccusedDetailsDobj> accusedDetails = new ArrayList<>();
    private boolean witnessPanel;
    private boolean accusedPanel;
    private boolean documentPanel;
    private String miscFee = null;
    private String miscReason = null;
    private boolean additionFeeDetails;
    boolean showFeePaidAtCourtPanel;
    private String courtRecieptNo;
    private Date courtRecieptDate;
    private Date maxdate = new Date();
    private String courtPaidAmount;
    private String decisionOfCourt;
    private List magistrateList = new ArrayList();
    private String magistrate;
    private List courtCode = new ArrayList();
    private boolean isMagistrateExist;
    private boolean showMagistrateSom;
    private boolean courtDetailsPanel;
    ChallanUtil challanUtil = new ChallanUtil();
    private String coming_from;
    private String going_to;
    private boolean showRemarksPanel;

    public DisposalOfChallanBean() {
        initilize();
    }

    public void initilize() {
        try {
            ChallanConfigrationDobj config_dobj = null;
            config_dobj = challanUtil.getChallanConfigration(Util.getUserStateCode());
            if (config_dobj == null) {
                JSFUtils.setFacesMessage("There is Some Problem . Try Again Later", "message", JSFUtils.ERROR);
                return;
            }
            courtCode = MasterTableFiller.getCourtList(Util.getUserStateCode());
            if (config_dobj.isIsmagistrate_exist()) {
                magistrateList = MasterTableFiller.getMagistrateList(Util.getUserStateCode());
                setShowMagistrateSom(true);
            }
            disableSaveButton = true;
            String actionCode = String.valueOf(appl_details.getCurrent_action_cd());
            if (Integer.parseInt(actionCode) == TableConstants.VM_ROLE_ENFORCEMENT_DISPOSE_ENTRY) {
                setDisableApplicationNoTextBox(true);
                setShowApproveDisApprovePanelForSave(false);
                setShowPanelForSave(true);
            }
            if (Integer.parseInt(actionCode) == TableConstants.VM_ROLE_ENFORCEMENT_DISPOSE_APPROVE) {
                getChallanInfo(actionCode);
                setDisableApplicationNoTextBox(false);
                setShowApproveDisApprovePanelForSave(true);
                setShowPanelForSave(false);
            }
        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void getSelectedValues(String name, String value) {
        if ("court".equalsIgnoreCase(name)) {
            setMagistrate(challanUtil.getSelectedMagistrate("court_cd", value));
        }
        if ("magistrate".equalsIgnoreCase(name)) {
            setCourtName(challanUtil.getSelectedMagistrate("magistrate_cd", value));
        }
    }

    public void getChallanInfo(String action_Code) {
        DisposalOfChallanImpl dsp = new DisposalOfChallanImpl();
        applicationNo = String.valueOf(appl_details.getAppl_no());
        try {

            dobjOwnerChallan = dsp.getVehicleDetails(applicationNo, chalNo, action_Code);
            if (dobjOwnerChallan == null) {
                JSFUtils.showMessage("Record Not Found For This Challan No." + chalNo);
                return;
            } else if (dobjOwnerChallan.getDisposalChallanDate() != null) {
                JSFUtils.showMessage("Challan No." + chalNo + " Already Disposed ");
                return;
            }
            showRemarksPanel = true;
            if (dobjOwnerChallan.getCourtName() != null && dobjOwnerChallan.getMagistrate() != null && dobjOwnerChallan.getHeaaringDt() != null) {
                setCourtDetailsPanel(true);
            } else {
                setCourtDetailsPanel(false);
            }
            if (Integer.parseInt(action_Code) == TableConstants.VM_ROLE_ENFORCEMENT_DISPOSE_ENTRY) {
                applicationNo = dobjOwnerChallan.getApplicationNO();
                boolean status = dsp.isApplicationNoExistInVaStatus(applicationNo);
                if (status) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Can Not dispose this Challan directly as it is currently in workFlow!!!"));
                    JSFUtils.showMessage("Can Not dispose this Challan directly as it is currently in workFlow!!!");
                    return;
                }
            }

            Integer i = 0;
            setOffenceList(dsp.getvehOffence(applicationNo));
            Iterator<VehcleOffenceDobj> itroff = getOffenceList().iterator();
            while (itroff.hasNext()) {
                VehcleOffenceDobj dobj1 = (VehcleOffenceDobj) itroff.next();
                vehOffDobjPrevious = (VehcleOffenceDobj) dobj1.clone();
                if (dobj1.getAccuCatg().equals("O")) {
                    i = i + Integer.parseInt(dobj1.getPenalty());
                }
                if (dobj1.getAccuCatg().equals("D")) {
                    i = i + Integer.parseInt(dobj1.getPenalty());
                }
                if (dobj1.getAccuCatg().equals("C")) {
                    i = i + Integer.parseInt(dobj1.getPenalty());
                }
            }
            //miscelleneous fee details
            CompoundingInOfficeDobj dobj = dsp.getMiscFeeDetails(applicationNo);
            if (dobj != null) {
                if (dobj.getMiscFee() != null) {
                    setMiscFee(dobj.getMiscFee());
                    setMiscReason(dobj.getReason());
                    setAdditionFeeDetails(true);
                } else {
                    setAdditionFeeDetails(false);
                }
            }
            docDetails = dsp.getDocDetails(applicationNo);
            if (docDetails.size() > 0) {
                setDocDetails(docDetails);
                setDocumentPanel(true);
            } else {
                setDocumentPanel(false);
            }
            setWitnessDetails(ChallanUtil.fetchWitnessDetails(applicationNo));
            if (witnessDetails.size() > 0) {
                setWitnessPanel(true);
            } else {
                setWitnessPanel(false);
            }
            setAccusedDetails(ChallanUtil.getAccusedDetails(applicationNo));
            if (accusedDetails.size() > 0) {
                setAccusedPanel(true);
            } else {
                setAccusedPanel(false);
            }
            List<CompoundingFeeDobj> rcptDetails = dsp.getRcptDetails(dobjOwnerChallan.getVehicleNo(), applicationNo);
            if (docDetails != null) {
                setListForCompoundigFee(rcptDetails);
            }
            setFromData(dobjOwnerChallan);
            previousDobjOwnerChallan = (SaveChallanDobj) dobjOwnerChallan.clone();
            setDisableSaveButton(false);

        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void setFromData(SaveChallanDobj dobj) {
        try {
            setApplicationNo(dobj.getApplicationNO());
            setVehNo(dobj.getVehicleNo());
            setNcrNo(dobj.getNCCRNo());
            setVehState(dobj.getState());
            setVehRto(dobj.getRto());
            setChalDate(dobj.getDate());
            setChalTime(dobj.getChallanTime());
            setOwnName(dobj.getOwnerName());
            setChasiNo(dobj.getChasiNo());
            setChalPlace(dobj.getPlaceCh());
            setVehClass(dobj.getVhClass());
            setFuel(dobj.getFuel());
            setStdCap(dobj.getStandCap());
            setSeatCap(dobj.getSeatCap());
            setLadn(dobj.getLadenWt());
            setSleeprCap(dobj.getSleeperCap());
            setColor(dobj.getColor());
            setChalOfficer(dobj.getChalOff().toUpperCase());
            setImpDt((dobj.getImpndDate()));
            setPlaceOfImp(dobj.getImpndPlace());
            setSezureNo(dobj.getSezNo());
            setOfficerNo(dobj.getContactOfficer());
            setCourtName(dobj.getCourtName());
            setMagistrate(dobj.getMagistrate());
            setHearingDt(dobj.getHeaaringDt());
            setComing_from(dobj.getCommingFrom());
            setGoing_to(dobj.getGoingTo());
            setDateOfDispose(dobj.getDisposalChallanDate());
            if ("".equals(dobj.getDisposalChallanDate()) || dobj.getDisposalChallanDate() == null) {
                Date disposedate = (new Date());
                setDateOfDispose(new SimpleDateFormat("dd-MMM-yy").format(disposedate));
            }
            setRemarksOfDispose(dobj.getDisposalChallanRemarks());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public String saveForDispseChallan() {
        String returnLocation = "";
        boolean isDisposed = false;
        String strCourtcase = "Y";
        DisposeChallanDobj disposeDobj = null;
        try {
            String actionCode = String.valueOf(appl_details.getCurrent_action_cd());
            DisposalOfChallanImpl dao = new DisposalOfChallanImpl();
            String date = null;
            date = getDateOfDispose();
            disposeDobj = isvalidation(actionCode);
            if (disposeDobj != null) {
                if (Integer.parseInt(actionCode) == TableConstants.VM_ROLE_ENFORCEMENT_DISPOSE_APPROVE) {
                    if (dao.chkChallanSettlement(applicationNo)) {
                        JSFUtils.showMessage("Disposal of challan is not allowed, settlement is pending");
                        if (dao.chkChallanPayment(applicationNo)) {
                            JSFUtils.showMessage("Disposal of challan is not allowed, Payment of Fee or Tax is Pending");
                            if (dao.chkRefToCourt(applicationNo)) {
                                JSFUtils.showMessage("Challan referred to court and not yet settled at court, or settlement details not entered.");
                            }
                        }
                        isDisposed = true;
                    }
                }
                if (!isDisposed) {
                    Status_dobj statusDobj = null;
                    if (Integer.parseInt(actionCode) == TableConstants.VM_ROLE_ENFORCEMENT_DISPOSE_APPROVE) {
                        statusDobj = new Status_dobj();
                        statusDobj.setRegn_no(dobjOwnerChallan.getVehicleNo());
                        statusDobj.setPur_cd(TableConstants.VM_MAST_ENFORCEMENT);
                        statusDobj.setAction_cd(TableConstants.VM_ROLE_ENFORCEMENT_DISPOSE_APPROVE);
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
                    }
                    boolean isSave = dao.updateDisposeChallan(vehNo.toUpperCase(), applicationNo, strCourtcase, dobjOwnerChallan, statusDobj, actionCode, ComparisonBeanImpl.changedDataContents(compareChanges()), previousDobjOwnerChallan, disposeDobj);
                    if (!isSave) {
                        JSFUtils.showMessage("Unalbe to dispose challan.");
                    } else {
                        reset();
                        JSFUtils.showMessage("Challan successfully disposed.");
                        if (Integer.parseInt(actionCode) == TableConstants.VM_ROLE_ENFORCEMENT_DISPOSE_APPROVE) {
                            returnLocation = "seatwork";
                        } else {
                            returnLocation = "";
                        }
                    }
                }
            }
        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
            return "";
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
            return "";
        }
        return returnLocation;
    }

    public static String convertDateToString(Date date) {
        String rtnString = "";
        SimpleDateFormat sdf1 = new SimpleDateFormat(DateUtils.DATE_FORMAT);
        if (date != null) {
            rtnString = sdf1.format(date);
        }
        return rtnString;
    }

    public static String dateFormat1(String inputDt) throws VahanException {
        String formateDt = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
        try {
            Date dt1 = sdf.parse(inputDt);
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy");
            formateDt = sdf1.format(dt1);
        } catch (ParseException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return formateDt;
    }

    public void reset() {
        setApplicationNo("");
        setBookNo("");
        setChalNo("");
        setVehNo("");
        setNcrNo("");
        setVehState("");
        setVehRto("");
        setChalDate("");
        setChalTime("");
        setOwnName("");
        setChasiNo("");
        setChalPlace("");
        setVehClass("");
        setFuel("");
        setStdCap("");
        setSeatCap("");
        setLadn("");
        setSleeprCap("");
        setColor("");
        setOfficerNo("");
        setDateOfDispose("");
        setRemarksOfDispose("");
        setImpDt("");
        setSezureNo("");
        setPlaceOfImp("");
        setChalOfficer("");
        setDocDetails(null);
        setOffenceDetails(null);
        setListForCompoundigFee(null);
        setCourtName("");
        setMagistrate("");
        setCourtPaidAmount("");
        setCourtRecieptDate(null);
        setCourtRecieptNo("");
        setDecisionOfCourt("");


    }

    @Override
    public String save() {
        String returnLocation = "";
        try {
            returnLocation = saveForDispseChallan();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        return returnLocation;
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        if (previousDobjOwnerChallan == null) {
            return compBeanList;
        }
        compBeanList.clear();
        Compare("Dispose Remarks", previousDobjOwnerChallan.getDisposalChallanRemarks(), getRemarksOfDispose(), (ArrayList) compBeanList);
        Compare("Dispose Date", previousDobjOwnerChallan.getDisposalChallanDate(), getDateOfDispose(), (ArrayList) compBeanList);
        return compBeanList;
    }

    @Override
    public String saveAndMoveFile() {
        String actionCode = String.valueOf(appl_details.getCurrent_action_cd());
        boolean flagCheck = true;
        String returnLocation = "";
        DisposalOfChallanImpl impl = null;
        DisposalOfChallanImpl dao = new DisposalOfChallanImpl();
        if (CommonUtils.isNullOrBlank(getRemarksOfDispose())) {
            JSFUtils.setFacesMessage("Please Enter The Dispose Remarks", "message", JSFUtils.INFO);
            flagCheck = false;
        } else {
            dobjOwnerChallan.setDisposalChallanRemarks(getRemarksOfDispose());
        }
        if ("".equals(getDateOfDispose()) || getDateOfDispose() == null) {
            JSFUtils.setFacesMessage("Please Enter THe Dispose Date", "message", JSFUtils.INFO);
            flagCheck = false;
        } else {
            dobjOwnerChallan.setDisposalChallanDate(getDateOfDispose());
        }
        if (flagCheck) {
            boolean isDisposed = false;
            String strCourtcase = "Y";
            try {
                if (dao.chkChallanSettlement(applicationNo)) {
                    JSFUtils.showMessage("Disposal of challan is not allowed, settlement is pending");
                    if (dao.chkChallanPayment(applicationNo)) {
                        JSFUtils.showMessage("Disposal of challan is not allowed, Payment of Fee or Tax is Pending");
                        if (dao.chkRefToCourt(applicationNo)) {
                            JSFUtils.showMessage("Challan referred to court and not yet settled at court, or settlement details not entered.");
                        }
                    }
                    isDisposed = true;
                }
                Status_dobj status = new Status_dobj();
                status.setAppl_dt(appl_details.getAppl_dt());
                status.setAppl_no(appl_details.getAppl_no());
                status.setPur_cd(appl_details.getPur_cd());
                status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
                status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
                status.setStatus(getApp_disapp_dobj().getNew_status());
                status.setCurrent_role(appl_details.getCurrent_role());
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)) {
                    if (!isDisposed) {
                        if ((actionCode == null ? (String.valueOf(TableConstants.VM_ROLE_ENFORCEMENT_DISPOSE_APPROVE)) == null : actionCode.equals(String.valueOf(TableConstants.VM_ROLE_ENFORCEMENT_DISPOSE_APPROVE)))) {
                            impl = new DisposalOfChallanImpl();
                            impl.movetoapprove(actionCode, status, ComparisonBeanImpl.changedDataContents(compareChanges()), vehNo.toUpperCase(), applicationNo, strCourtcase, ladn, "", getRemarksOfDispose(), getDateOfDispose(), dobjOwnerChallan, true, "", previousDobjOwnerChallan, docDetails);
                            returnLocation = "seatwork";
                        }
                    } else {
                        JSFUtils.setFacesMessage("There Are Some Problem To Dispose  The Challan", "message", JSFUtils.ERROR);
                    }
                }
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {
                    status.setPrev_action_cd_selected(app_disapp_dobj.getPre_action_code());
                    impl = new DisposalOfChallanImpl();
                    impl.reback(actionCode, status, ComparisonBeanImpl.changedDataContents(compareChanges()), vehNo.toUpperCase(), applicationNo, strCourtcase, ladn, "", getRemarksOfDispose(), getDateOfDispose(), dobjOwnerChallan, true, "", previousDobjOwnerChallan);
                    returnLocation = "seatwork";
                }
            } catch (VahanException e) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, message);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        }
        return returnLocation;
    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return prevChangedDataList;
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList;
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

    public String getChalNo() {
        return chalNo;
    }

    public void setChalNo(String chalNo) {
        this.chalNo = chalNo;
    }

    public String getVehNo() {
        return vehNo;
    }

    public void setVehNo(String vehNo) {
        this.vehNo = vehNo;
    }

    public String getNcrNo() {
        return ncrNo;
    }

    public void setNcrNo(String ncrNo) {
        this.ncrNo = ncrNo;
    }

    public String getVehState() {
        return vehState;
    }

    public void setVehState(String vehState) {
        this.vehState = vehState;
    }

    public String getChalDate() {
        return chalDate;
    }

    public void setChalDate(String chalDate) {
        this.chalDate = chalDate;
    }

    public String getVehRto() {
        return vehRto;
    }

    public void setVehRto(String vehRto) {
        this.vehRto = vehRto;
    }

    public String getChalTime() {
        return chalTime;
    }

    public void setChalTime(String chalTime) {
        this.chalTime = chalTime;
    }

    public String getOwnName() {
        return ownName;
    }

    public void setOwnName(String ownName) {
        this.ownName = ownName;
    }

    public String getChalOfficer() {
        return chalOfficer;
    }

    public void setChalOfficer(String chalOfficer) {
        this.chalOfficer = chalOfficer;
    }

    public String getChasiNo() {
        return chasiNo;
    }

    public void setChasiNo(String chasiNo) {
        this.chasiNo = chasiNo;
    }

    public String getChalPlace() {
        return chalPlace;
    }

    public void setChalPlace(String chalPlace) {
        this.chalPlace = chalPlace;
    }

    public String getVehClass() {
        return vehClass;
    }

    public void setVehClass(String vehClass) {
        this.vehClass = vehClass;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public String getStdCap() {
        return stdCap;
    }

    public void setStdCap(String stdCap) {
        this.stdCap = stdCap;
    }

    public String getSeatCap() {
        return seatCap;
    }

    public void setSeatCap(String seatCap) {
        this.seatCap = seatCap;
    }

    public String getLadn() {
        return ladn;
    }

    public void setLadn(String ladn) {
        this.ladn = ladn;
    }

    public String getSleeprCap() {
        return sleeprCap;
    }

    public void setSleeprCap(String sleeprCap) {
        this.sleeprCap = sleeprCap;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public Date getHearingDt() {
        return hearingDt;
    }

    public void setHearingDt(Date hearingDt) {
        this.hearingDt = hearingDt;
    }

    public String getImpDt() {
        return impDt;
    }

    public void setImpDt(String impDt) {
        this.impDt = impDt;
    }

    public String getPlaceOfImp() {
        return placeOfImp;
    }

    public void setPlaceOfImp(String placeOfImp) {
        this.placeOfImp = placeOfImp;
    }

    public String getSezureNo() {
        return sezureNo;
    }

    public void setSezureNo(String sezureNo) {
        this.sezureNo = sezureNo;
    }

    public String getOfficerNo() {
        return officerNo;
    }

    public void setOfficerNo(String officerNo) {
        this.officerNo = officerNo;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public List<OffencesDobj> getOffenceDetails() {
        return offenceDetails;
    }

    public void setOffenceDetails(List<OffencesDobj> offenceDetails) {
        this.offenceDetails = offenceDetails;
    }

    public List<DocTableDobj> getDocDetails() {
        return docDetails;
    }

    public void setDocDetails(List<DocTableDobj> docDetails) {
        this.docDetails = docDetails;
    }

    public List<CompoundingFeeDobj> getListForCompoundigFee() {
        return listForCompoundigFee;
    }

    public void setListForCompoundigFee(List<CompoundingFeeDobj> listForCompoundigFee) {
        this.listForCompoundigFee = listForCompoundigFee;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
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

    public boolean isDisableSaveButton() {
        return disableSaveButton;
    }

    public void setDisableSaveButton(boolean disableSaveButton) {
        this.disableSaveButton = disableSaveButton;
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

    public boolean isDisableApplicationNoTextBox() {
        return disableApplicationNoTextBox;
    }

    public void setDisableApplicationNoTextBox(boolean disableApplicationNoTextBox) {
        this.disableApplicationNoTextBox = disableApplicationNoTextBox;
    }

    public List<VehcleOffenceDobj> getOffenceList() {
        return offenceList;
    }

    public void setOffenceList(List<VehcleOffenceDobj> offenceList) {
        this.offenceList = offenceList;
    }

    /**
     * @return the witnessDetails
     */
    public List<WitnessdetailDobj> getWitnessDetails() {
        return witnessDetails;
    }

    /**
     * @param witnessDetails the witnessDetails to set
     */
    public void setWitnessDetails(List<WitnessdetailDobj> witnessDetails) {
        this.witnessDetails = witnessDetails;
    }

    /**
     * @return the accusedDetails
     */
    public List<AccusedDetailsDobj> getAccusedDetails() {
        return accusedDetails;
    }

    /**
     * @param accusedDetails the accusedDetails to set
     */
    public void setAccusedDetails(List<AccusedDetailsDobj> accusedDetails) {
        this.accusedDetails = accusedDetails;
    }

    /**
     * @return the witnessPanel
     */
    public boolean isWitnessPanel() {
        return witnessPanel;
    }

    /**
     * @param witnessPanel the witnessPanel to set
     */
    public void setWitnessPanel(boolean witnessPanel) {
        this.witnessPanel = witnessPanel;
    }

    /**
     * @return the accusedPanel
     */
    public boolean isAccusedPanel() {
        return accusedPanel;
    }

    /**
     * @param accusedPanel the accusedPanel to set
     */
    public void setAccusedPanel(boolean accusedPanel) {
        this.accusedPanel = accusedPanel;
    }

    /**
     * @return the documentPanel
     */
    public boolean isDocumentPanel() {
        return documentPanel;
    }

    /**
     * @param documentPanel the documentPanel to set
     */
    public void setDocumentPanel(boolean documentPanel) {
        this.documentPanel = documentPanel;
    }

    /**
     * @return the miscFee
     */
    public String getMiscFee() {
        return miscFee;
    }

    /**
     * @param miscFee the miscFee to set
     */
    public void setMiscFee(String miscFee) {
        this.miscFee = miscFee;
    }

    /**
     * @return the miscReason
     */
    public String getMiscReason() {
        return miscReason;
    }

    /**
     * @param miscReason the miscReason to set
     */
    public void setMiscReason(String miscReason) {
        this.miscReason = miscReason;
    }

    /**
     * @return the additionFeeDetails
     */
    public boolean isAdditionFeeDetails() {
        return additionFeeDetails;
    }

    /**
     * @param additionFeeDetails the additionFeeDetails to set
     */
    public void setAdditionFeeDetails(boolean additionFeeDetails) {
        this.additionFeeDetails = additionFeeDetails;
    }

    public boolean isShowFeePaidAtCourtPanel() {
        return showFeePaidAtCourtPanel;
    }

    public void setShowFeePaidAtCourtPanel(boolean showFeePaidAtCourtPanel) {
        this.showFeePaidAtCourtPanel = showFeePaidAtCourtPanel;
    }

    public String getCourtRecieptNo() {
        return courtRecieptNo;
    }

    public void setCourtRecieptNo(String courtRecieptNo) {
        this.courtRecieptNo = courtRecieptNo;
    }

    public Date getCourtRecieptDate() {
        return courtRecieptDate;
    }

    public void setCourtRecieptDate(Date courtRecieptDate) {
        this.courtRecieptDate = courtRecieptDate;
    }

    public Date getMaxdate() {
        return maxdate;
    }

    public void setMaxdate(Date maxdate) {
        this.maxdate = maxdate;
    }

    public String getCourtPaidAmount() {
        return courtPaidAmount;
    }

    public void setCourtPaidAmount(String courtPaidAmount) {
        this.courtPaidAmount = courtPaidAmount;
    }

    public String getDecisionOfCourt() {
        return decisionOfCourt;
    }

    public void setDecisionOfCourt(String decisionOfCourt) {
        this.decisionOfCourt = decisionOfCourt;
    }

    public String getRemarksOfDispose() {
        return remarksOfDispose;
    }

    public void setRemarksOfDispose(String remarksOfDispose) {
        this.remarksOfDispose = remarksOfDispose;
    }

    public String getDateOfDispose() {
        return dateOfDispose;
    }

    public void setDateOfDispose(String dateOfDispose) {
        this.dateOfDispose = dateOfDispose;
    }

    private DisposeChallanDobj isvalidation(String actionCode) {
        DisposeChallanDobj disposeDobj = new DisposeChallanDobj();
        boolean validationFlag = true;
        if (Integer.parseInt(actionCode) == TableConstants.VM_ROLE_ENFORCEMENT_DISPOSE_ENTRY && courtDetailsPanel) {
            if (CommonUtils.isNullOrBlank(getChalNo())) {
                JSFUtils.setFacesMessage("PLease Enter The Challan No", "message", JSFUtils.INFO);
                validationFlag = false;
            }
            if (CommonUtils.isNullOrBlank(getDecisionOfCourt())) {
                JSFUtils.setFacesMessage("Please Enter The Court Decision", "message", JSFUtils.INFO);
                validationFlag = false;
            }
            if (CommonUtils.isNullOrBlank(getCourtRecieptNo())) {
                JSFUtils.setFacesMessage("Please Enter The Court Receipt No ", "message", JSFUtils.INFO);
                validationFlag = false;
            }
            if (getCourtRecieptDate() == null) {
                JSFUtils.setFacesMessage("Please Enter The Court Receipt Date", "message", JSFUtils.INFO);
                validationFlag = false;
            }
            if (CommonUtils.isNullOrBlank(getCourtPaidAmount())) {
                JSFUtils.setFacesMessage("Please Enter The Couet Paid Amount", "message", JSFUtils.INFO);
                validationFlag = false;
            }
            if (getHearingDt() == null) {
                JSFUtils.setFacesMessage("Please Enter The Hearing Date", "message", JSFUtils.WARN);
                validationFlag = false;
            }
            if (CommonUtils.isNullOrBlank(getCourtName())) {
                JSFUtils.setFacesMessage("Please Enter The Court Name", "message", JSFUtils.INFO);
                validationFlag = false;
            }
        }
        if (CommonUtils.isNullOrBlank(getRemarksOfDispose())) {
            JSFUtils.setFacesMessage("Please Enter THe Dispose Remarks", "message", JSFUtils.WARN);
            validationFlag = false;
        }
        if (getDateOfDispose() == null) {
            JSFUtils.setFacesMessage("Please Enter The Dispose Date", "message", JSFUtils.WARN);
            validationFlag = false;
        }

        if (validationFlag) {
            if (Integer.parseInt(actionCode) == TableConstants.VM_ROLE_ENFORCEMENT_DISPOSE_ENTRY) {
                disposeDobj.setCourt_paid_amount(getCourtPaidAmount());
                disposeDobj.setCourt_rcpt_no(getCourtRecieptNo());
                disposeDobj.setCourt_rcpt_date(getCourtRecieptDate());
                disposeDobj.setDecisionOfCourt(getDecisionOfCourt());
                disposeDobj.setHearing_date(getHearingDt());
                disposeDobj.setCourt_name(getCourtName());
                disposeDobj.setMigistrate(getMagistrate());
            }
            disposeDobj.setRemarksOfDispose(getRemarksOfDispose());
            disposeDobj.setDateOfDispose(getDateOfDispose());
        } else {
            disposeDobj = null;
        }

        return disposeDobj;
    }

    public List getMagistrateList() {
        return magistrateList;
    }

    public void setMagistrateList(List magistrateList) {
        this.magistrateList = magistrateList;
    }

    public String getMagistrate() {
        return magistrate;
    }

    public void setMagistrate(String magistrate) {
        this.magistrate = magistrate;
    }

    public List getCourtCode() {
        return courtCode;
    }

    public void setCourtCode(List courtCode) {
        this.courtCode = courtCode;
    }

    public boolean isIsMagistrateExist() {
        return isMagistrateExist;
    }

    public void setIsMagistrateExist(boolean isMagistrateExist) {
        this.isMagistrateExist = isMagistrateExist;
    }

    public boolean isShowMagistrateSom() {
        return showMagistrateSom;
    }

    public void setShowMagistrateSom(boolean showMagistrateSom) {
        this.showMagistrateSom = showMagistrateSom;
    }

    /**
     * @return the courtDetailsPanel
     */
    public boolean isCourtDetailsPanel() {
        return courtDetailsPanel;
    }

    /**
     * @param courtDetailsPanel the courtDetailsPanel to set
     */
    public void setCourtDetailsPanel(boolean courtDetailsPanel) {
        this.courtDetailsPanel = courtDetailsPanel;
    }

    public String getComing_from() {
        return coming_from;
    }

    public void setComing_from(String coming_from) {
        this.coming_from = coming_from;
    }

    public String getGoing_to() {
        return going_to;
    }

    public void setGoing_to(String going_to) {
        this.going_to = going_to;
    }

    public boolean isShowRemarksPanel() {
        return showRemarksPanel;
    }

    public void setShowRemarksPanel(boolean showRemarksPanel) {
        this.showRemarksPanel = showRemarksPanel;
    }
}
