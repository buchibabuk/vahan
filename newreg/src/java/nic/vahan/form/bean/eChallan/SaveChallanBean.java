package nic.vahan.form.bean.eChallan;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;
import nic.java.util.CommonUtils;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.eChallan.AccusedDetailsDobj;
import nic.vahan.form.dobj.eChallan.CompoundingFeeDobj;
import nic.vahan.form.dobj.eChallan.DocTableDobj;
import nic.vahan.form.dobj.eChallan.OffencesDobj;
import nic.vahan.form.dobj.eChallan.SaveChallanDobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.eChallan.SaveChallanImpl;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.form.bean.ReceiptMasterBean;
import nic.vahan.form.bean.permit.PermitCheckDetailsBean;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.eChallan.ChallanConfigrationDobj;
import nic.vahan.form.dobj.eChallan.WitnessdetailDobj;
import nic.vahan.form.impl.eChallan.ChallanUtil;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import static nic.vahan.server.ServerUtil.CompareAccusedList;
import static nic.vahan.server.ServerUtil.CompareDocumentImpounded;
import static nic.vahan.server.ServerUtil.CompareOffenceList;
import static nic.vahan.server.ServerUtil.CompareWitnessList;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;

@ManagedBean(name = "save")
@ViewScoped
public class SaveChallanBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(SaveChallanBean.class);
    private String nccrNo = "";
    private String officerNo = "";
    private String vehicleNo = "";
    private String chasiNo = "";
    private String vhClass = "";
    private String vehicleType;
    private String vehno1 = "";
    private String vehno2 = "";
    private String accusCatg;
    private String accuName;
    private String time = "";
    private String challanPlace = "";
    private String courtName = "";
    private String compFee = "0";
    private String adCompFee = "0";
    private String balanceFee;
    private String rcieptNoInCompFee = "";
    private String contactOfficer = "";
    private String ladenWeight = "";
    private String unladenWeight = "";
    private String pmtVal = "";
    private String fitVal = "";
    private String taxVal = "";
    private String insVal = "";
    private String seatCap = "";
    private String add1 = "";
    private String add2 = "";
    private String AccuseAddress;
    private String tfVehno1;
    private String tfVehno2;
    private String standCap = "";
    private String impndPlace = "";
    private String challanTime = "";
    private String statusMsg = "";
    private String registrationNo = "";
    private String regionState = "";
    private String witnessName = "";
    private String witnessAddress = "";
    private String accusedRemarks = "";
    private String impoundRemarks = "";
    private String randomNumber = "";
    private String dlNo;
    private String ownerNAme;
    private String color;
    private String sleeperCap;
    private String fuel;
    private String accCategInDocImp;
    private String docNo;
    private String docName;
    private String offenceCode = null;
    private String accuseInOffDetail;
    private String accuseInCompFeeDetail;
    private String adCfPenalty = "0";
    private String sezNo = "";
    private String actualWeight;
    private String overLoadWeight;
    private String compoundingFee = null;
    private String actualPassangerWeight;
    private String overLoadPassangerWeight;
    private String passangerCompoundingFee;
    private String hiddenaddressField;
    private String accusedDesc;
    private String accusedDescDocImpnd;
    private String documentDesc;
    private String applicationNo = null;
    private String district;
    private String state = "";
    private String showdate;
    private String issueAuth = "";
    private String addDoc = "";
    private String challanNo = "";
    private String isDocumentImpound;
    private String isvehicleImpound;
    private int settledFee = 0;
    private int totalFee;
    private int purCd;
    private long witnessMobileNo;
    private Date vehImpdDate;
    private Date heaaringDt;
    private Date rcieptDateInCompFee;
    private Date impdDt;
    private Date challanDateCal = new Date();
    private Date docValidity;
    private Date maxDate = new Date();
    boolean flag = true;
    boolean showDocumentImpoundPenal;
    boolean showVehicleImpoundPenal;
    boolean showRefToCourtPenal;
    boolean showWitnessDetailsPenal;
    boolean documentImpoundCheckBox;
    boolean addAccusedDetails;
    boolean cbxAddOffenceDetails;
    boolean showAccusedDialog;
    boolean showDocumentTable;
    boolean showaccusedDataTable;
    boolean vehicleImpoundCheckBox;
    boolean referTOCourtCheckBox;
    boolean witnessDetailsCheckBox;
    boolean disableSaveButton;
    private boolean disableResetButton;
    boolean disablePayonRoadButton;
    private boolean tfOfficerNoDisable;
    private boolean challanRefToCourt;
    private boolean vehImpnd;
    private boolean docImpnd;
    private boolean cbDocImpound;
    private boolean cbVehImpounded;
    private boolean cbRefToCort;
    private boolean cbMissingVehicleNo;
    private boolean cbSaveByChassisNo;
    private boolean disableOwnerDetails;
    private boolean tfVehicleNo2Disabled;
    private boolean tfChessisNoDisabled;
    private boolean penalOverload;
    private boolean penalAccessPassanger;
    private boolean advancedCompFeeDisabled;
    private boolean recieptNoDisabled;
    private boolean recieptDateDisabled;
    private boolean cbxTaxPayOnRoadDisabled;
    private boolean showWitnessPanel;
    private boolean showOffenceDetailPanel;
    private boolean nccr_no_exist;
    private List vehclass = new ArrayList();
    private List fuelList = new ArrayList();
    private List officer = new ArrayList();
    private List stateCode = new ArrayList();
    private List rtoCode = new ArrayList();
    private List offenceList = new ArrayList();
    private List<DocTableDobj> docImpounded = new ArrayList<>();
    private List<WitnessdetailDobj> witnessDetailsList = new ArrayList<>();
    private List<WitnessdetailDobj> oldWitnessDetailsList = new ArrayList<>();
    private List<OffencesDobj> offenceDetails = new ArrayList<>();
    private List<AccusedDetailsDobj> accusedDetails = new ArrayList<>();
    private List<CompoundingFeeDobj> ListForCompoundigFee = new ArrayList<>();
    private List courtCode = new ArrayList();
    private List magistrateList = new ArrayList();
    private String magistrate;
    private List<ComparisonBean> prevChangedDataList;
    private Map<String, Object> accused;
    private Map<String, Object> accusedUpdatedList;
    private Map<String, Object> purposeLabelValue;
    private Map<String, Object> docCode;
    SaveChallanImpl challandao = new SaveChallanImpl();
    WitnessdetailDobj witnessDobj = new WitnessdetailDobj();
    SaveChallanDobj dobj = null;
    SaveChallanDobj dobj1 = null;
    private OffencesDobj offenceDobj = new OffencesDobj();
    SaveChallanDobj challanDobjPrv;
    private List<AccusedDetailsDobj> oldaccused = new ArrayList<>();
    private List<OffencesDobj> oldOffenceList = new ArrayList<>();
    private List<DocTableDobj> oldDocImpoundedList = new ArrayList<>();
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<SaveChallanDobj> previousChallanDetailsList = new ArrayList<>();
    @ManagedProperty(value = "#{rcpt_bean}")
    private ReceiptMasterBean rcptBean;
    private String permanentRCPTNO;
    private Boolean vehicleChallanDetailsButton;
    @ManagedProperty(value = "#{pmtDtlsBean}")
    private PermitCheckDetailsBean pmtCheckDtsl;
    private String siezePoliceStationName;
    private String siezeDistrictName;
    private String witnessPoliceStationName;
    private String accuesdPoliceSation;
    private String accuesdCity;
    private int accuesdPincode;
    private String accuesdFlag;
    private boolean disablePrintButton;
    private boolean otherDocumentPanel;
    private boolean addOffenceManuallyPanel;
    private boolean showOffenceManuallyPanel;
    private List<SaveChallanDobj> challanDetailsList = new ArrayList<>();
    private boolean selectedRow;
    private boolean showMinusButton;
    private boolean showPlusButton = true;
    private boolean showVehicleInfoPanel;
    private String carryGoods;
    private List carryGoodsList = new ArrayList();
    private boolean overloadVehicle;
    private boolean overAccessPassanger;
    private String vahanMessages = null;
    private ChallanConfigrationDobj config_dobj;
    private String commingFrom;
    private String goingTo;
    private SessionVariables sessionVariables = null;
    boolean renderAtApproval;
    private boolean SettleAtSpot;
    private boolean renderedSettleAtSpot = true;
    private boolean checkAdvanceCompFee;

    public SaveChallanBean() {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null || nic.vahan.server.CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
            return;
        }
        reset();
        initialize();


    }

    public void initialize() {
        setRenderAtApproval(false);
        setDisablePayonRoadButton(true);
        Exception exception = null;
        config_dobj = null;
        ChallanUtil challanUtil = new ChallanUtil();
        try {
            config_dobj = challanUtil.getChallanConfigration(Util.getUserStateCode());
            if (config_dobj == null) {
                return;
            }
            setDisablePrintButton(false);
            setDisableSaveButton(true);
            setDisableResetButton(true);
            setVehicleChallanDetailsButton(false);
            String actionCode = String.valueOf(appl_details.getCurrent_action_cd());
            officer = challandao.getChallanOfficer();
            String[][] data = MasterTableFiller.masterTables.TM_OFFICE.getData();
            rtoCode.add(new SelectItem("-1", "---Select---"));
            for (int i = 0; i < data.length; i++) {
                if (sessionVariables.getStateCodeSelected().equalsIgnoreCase(data[i][13])) {
                    rtoCode.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
            data = MasterTableFiller.masterTables.TM_STATE.getData();
            stateCode.add(new SelectItem("-1", "---Select---"));
            for (int i = 0; i < data.length; i++) {
                stateCode.add(new SelectItem(data[i][0], data[i][1]));
            }
            courtCode = MasterTableFiller.getCourtList(sessionVariables.getStateCodeSelected());
            if (config_dobj.isIsmagistrate_exist()) {
                magistrateList = MasterTableFiller.getMagistrateList(sessionVariables.getStateCodeSelected());

            }
            data = MasterTableFiller.masterTables.VM_ACCUSED.getData();
            accused = new LinkedHashMap<>();
            accused.put("----Select----", "-1");
            for (String[] data1 : data) {
                accused.put(data1[1], data1[0]);
            }
            data = MasterTableFiller.masterTables.VM_DOCUMENTS.getData();
            docCode = new LinkedHashMap<>();
            docCode.put("----Select----", "-1");
            for (String[] data1 : data) {
                docCode.put(data1[1], data1[0]);
            }
            data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
            vehclass.add(new SelectItem("-1", "---Select---"));
            for (String[] data1 : data) {
                vehclass.add(new SelectItem(data1[0], data1[1]));
            }
            data = MasterTableFiller.masterTables.VM_FUEL.getData();
            fuelList.add(new SelectItem("-1", "---Select---"));
            for (String[] data1 : data) {
                fuelList.add(new SelectItem(data1[0], data1[1]));
            }
            if (Integer.parseInt(actionCode) == TableConstants.VM_ROLE_ENFORCEMENT_APPROVE) {

                getChallanDataForVerification();

            } else if (Integer.parseInt(actionCode) == TableConstants.VM_ROLE_ENFORCEMENT_VERIFY) {
                getChallanDataForVerification();
            } else {
                setRenderAtApproval(true);
                setDisablePayonRoadButton(false);
                isDisablePayonRoadButton();
                String curr_challan_no = challandao.fetchchallanNo();
                setChallanNo(curr_challan_no);
            }
        } catch (VahanException e) {
            vahanMessages = e.getMessage();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            exception = e;
        }
        if (exception != null) {
            LOGGER.error(exception.toString() + " " + exception.getStackTrace()[0]);
            vahanMessages = "Error :Could Not Able To Load The Form Data.";
        }
    }

    public void getdetailsforvehicle() {
        try {
            InsDobj insDobj = null;
            pmtCheckDtsl.getAlldetails(getTfVehno2().toUpperCase().trim(), insDobj, "", 0);
            //RequestContext ca = RequestContext.getCurrentInstance();
            PrimeFaces.current().executeScript("PF('otherDetailsOfVehicle').show()");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void showRtoList() {
        try {
            setRtoCode(challandao.getrtoCode(getState()));
        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void fetchadvcmpfee() {
        SaveChallanImpl saveChallanImpl = new SaveChallanImpl();
        boolean flag;
        try {
            flag = saveChallanImpl.isReceiptNoExist(rcieptNoInCompFee, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
            if (flag) {
                PrimeFaces.current().ajax().update("rcptmsgDialog");
                PrimeFaces.current().executeScript("PF('messageDialog_rcpt').show();");
                setRcieptNoInCompFee(null);
                return;
            }
            if ((this.rcieptNoInCompFee != null)) {
                this.adCompFee = getCompFee();
                disablePayonRoadButton = true;
            } else {
                this.adCompFee = "0";
                disablePayonRoadButton = false;
            }
        } catch (VahanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "There is some problem to fetch data from database. Please contact to system Administration", "There is some problem to fetch data from database. Please contact to system Administration"));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }

    }

    public void getPriviousChallanDetails() {
        try {
            setPreviousChallanDetailsList(challandao.getPreviousChallanDetails(registrationNo));
            //RequestContext ca = RequestContext.getCurrentInstance();
            PrimeFaces.current().executeScript("PF('dlgchallanDetails').show()");
        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void getDataWithRegnNo(String name, String value) {
        try {
            String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
            vehclass.add(new SelectItem("-1", "---Select---"));
            for (String[] data1 : data) {
                vehclass.add(new SelectItem(data1[0], data1[1]));
            }
            if ("regn_no".equals(name) && !value.isEmpty()) {
                registrationNo = getTfVehno2().toUpperCase().trim();
            }
            if ("chessis_no".equals(name) && !value.isEmpty()) {
                registrationNo = chasiNo.toUpperCase().trim();
                setTfVehicleNo2Disabled(true);
            }
            if (!"".equalsIgnoreCase(registrationNo)) {
                setChallanDetailsList(challandao.getVehDetails(registrationNo, name));
                if (challanDetailsList.size() > 1) {
                    //RequestContext ca = RequestContext.getCurrentInstance();
                    PrimeFaces.current().executeScript("PF('DetailsOfVehicle').show()");
                } else {
                    if (!challanDetailsList.isEmpty()) {
                        for (SaveChallanDobj saveDobj : challanDetailsList) {
                            if (saveDobj != null) {
                                rtoCode.clear();
                                data = MasterTableFiller.masterTables.TM_OFFICE.getData();
                                rtoCode.add(new SelectItem("-1", "---Select---"));
                                for (int i = 0; i < data.length; i++) {
                                    if (saveDobj.getState().equalsIgnoreCase(data[i][13])) {
                                        rtoCode.add(new SelectItem(data[i][0], data[i][1]));
                                    }
                                }
                                setDobj(saveDobj);

                            }
                        }
                        setVehicleChallanDetailsButton(true);
                    } else {
                        resetVehicleDetails();
                        JSFUtils.showMessage("Vehicle Details Are Not Found Please fill Details Manualy");
                        return;
                    }

                }
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
    //function to select particular vehicle details row if vehicle  found duplicate records

    public void selectRow(SaveChallanDobj dobj) throws VahanException {
        if (selectedRow) {
            if (dobj != null) {
                setDobj(dobj);
            } else {
                JSFUtils.showMessage("Vehicle Details Are Not Found Please fill Details Manualy");
                setVehicleChallanDetailsButton(true);
                return;
            }
        } else {
            resetVehicleDetails();
        }

    }
//set value in saveChallanDobj

    private void setDobj(SaveChallanDobj saveDobj) throws VahanException {
        setVehicleChallanDetailsButton(true);
        setTfVehno2(saveDobj.getVehno2());
        setChasiNo(saveDobj.getChasiNo());
        setVhClass(saveDobj.getVhClass());
        setVehicleType(saveDobj.getVehicleType());
        setOwnerNAme(saveDobj.getOwnerName());
        setSeatCap(saveDobj.getSeatCap());
        setLadenWeight(saveDobj.getLadenWt());
        setStandCap(saveDobj.getStandCap());
        setColor(saveDobj.getColor());
        setSleeperCap(saveDobj.getSleeperCap());
        setState(saveDobj.getState());
        setDistrict(saveDobj.getDistrict());
        setFuel(saveDobj.getFuel());
        setHiddenaddressField(saveDobj.getHiddenaddressField());
        setTfVehicleNo2Disabled(true);
        setTfChessisNoDisabled(true);
        setDisableOwnerDetails(true);
        setStatusMsg("");

        if (Integer.parseInt(vhClass) >= 70 && Integer.parseInt(vhClass) <= 77) {
            String[][] data = MasterTableFiller.masterTables.VM_ACCUSED.getData();

            for (int i = 0; i < data.length; i++) {
                accused.put(data[i][1], data[i][0]);
            }
        } else {
            setAccused(challandao.getAccused1());
        }
    }

    private void resetVehicleDetails() {
        setVhClass("");
        setVehicleType("");
        setOwnerNAme("");
        setSeatCap("");
        setLadenWeight("");
        setStandCap("");
        setColor("");
        setSleeperCap("");
        setState("");
        setDistrict("");
        setFuel("");
        setTfChessisNoDisabled(false);
        setTfVehicleNo2Disabled(false);
        setDisableOwnerDetails(false);
    }

    public void getOffenceAccordingAccuse() {
        try {
            setOffenceList(challandao.getOffenceAccusedWise(getVhClass(), getAccuseInOffDetail()));
        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public String showaccusedDetails() {
        boolean flag = true;
        if (accusCatg.equals("-1") || accusCatg == null) {
            JSFUtils.setFacesMessage("Please Select The Accused catg", "messages", JSFUtils.FATAL);
            flag = false;
        }
        if ("".equals(accuName) || accuName == null) {
            JSFUtils.setFacesMessage("Please Enter Accused Name", "messages", JSFUtils.FATAL);
            flag = false;
        }
        if ("".equals(AccuseAddress) || AccuseAddress == null) {
            JSFUtils.setFacesMessage("Please Enter Accused Address", "messages", JSFUtils.FATAL);
            flag = false;
        }

        Iterator<AccusedDetailsDobj> itr = accusedDetails.iterator();
        while (itr.hasNext()) {
            AccusedDetailsDobj checkAccusedDobj = itr.next();
            String checkAccc = checkAccusedDobj.getAccCatergory();

            if (checkAccc.equals(accusCatg)) {
                JSFUtils.showMessage("Accused Already Exists");
                flag = false;
            }
        }
        if (flag) {
            for (Object key : accused.keySet()) {
                if (accused.get(key).toString().equals(accusCatg.toString())) {
                    accusedDesc = key.toString();
                    break;
                }
            }
            AccusedDetailsDobj accdtl = new AccusedDetailsDobj(accusCatg, accusedDesc, accuName, AccuseAddress, dlNo, this.accuesdPoliceSation, this.accuesdCity, this.accuesdFlag, this.accuesdPincode);
            accusedDetails.add(accdtl);
            if (accusedDetails.isEmpty() || accusedDetails == null) {
                setShowaccusedDataTable(false);
            } else {
                setShowaccusedDataTable(true);
            }
            setAccuName("");
            setAccuseAddress("");
            setAccusCatg("-1");
            setDlNo("");
            setAccuesdCity("");
            setAccuesdFlag("");
            setAccuesdPincode(0);
            setAccuesdPoliceSation("");
            accusedUpdatedList = getAccusedCategory(accusedDetails);
        }
        return null;
    }

    public Map<String, Object> getAccusedCategory(List<AccusedDetailsDobj> accusedList) {
        Map<String, Object> fileteredList = new LinkedHashMap<String, Object>();
        fileteredList.put("Select Accused", "-1");
        int count = 0;
        String AccusedDesc = "";
        Iterator<AccusedDetailsDobj> itr = accusedList.iterator();
        while (itr.hasNext()) {
            AccusedDetailsDobj dobj = itr.next();
            String acccusedCategory = dobj.getAccCatergory();
            if (acccusedCategory.equals("O")) {
                AccusedDesc = "OWNER";
            }
            if (acccusedCategory.equals("D")) {
                AccusedDesc = "DRIVER";
            }
            if (acccusedCategory.equals("C")) {
                AccusedDesc = "CONDUCTOR";
            }
            if (acccusedCategory.equals("N")) {
                AccusedDesc = "CONSIGNEE";
            }
            fileteredList.put(AccusedDesc, acccusedCategory);
            count++;
        }
        return fileteredList;
    }

    public void onEdit(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Accused Edited", ((AccusedDetailsDobj) event.getObject()).getAccCatergory());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Accused Cancelled");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        accusedDetails.remove((AccusedDetailsDobj) event.getObject());
    }

    public void showDocImpounded() {
        if (addDoc.equals("-1") || accCategInDocImp.equals("-1") || accCategInDocImp == null) {
            JSFUtils.showMessage("Please Select The Document Type And Accuse Type");
            return;
        }
        if (docNo == "" || docNo == null) {
            JSFUtils.showMessage("Please Enter The Document no No");
            return;
        }
        Iterator<DocTableDobj> itr = docImpounded.iterator();
        while (itr.hasNext()) {
            DocTableDobj dobjCheckDocNo = itr.next();
            String docNo1 = dobjCheckDocNo.getDocNo();
            String doc = dobjCheckDocNo.getDocCode();
            if (docNo1.equals(docNo)) {
                JSFUtils.showMessage("Document_no Already Exists");
                return;
            }
            if (doc.equals(addDoc)) {
                if (doc.equals("6")) {
                    //if this is other document then we can add here 
                    //JSFUtils.showMessage("You Can Add Other Documents");
                } else {
                    JSFUtils.showMessage("Document Already Exists");
                    return;
                }
            }
        }
        for (Object key : docCode.keySet()) {
            if (docCode.get(key).toString().equals(addDoc.toString())) {
                documentDesc = key.toString();
                break;
            }
        }
        for (Object key : accusedUpdatedList.keySet()) {
            if (accusedUpdatedList.get(key).toString().equals(accCategInDocImp.toString())) {
                accusedDescDocImpnd = key.toString();
                break;
            }
        }
        String date = (new SimpleDateFormat("dd-MMM-yy").format(this.docValidity));
        this.showdate = date;
        DocTableDobj docImpound = new DocTableDobj(this.addDoc, this.accusedDescDocImpnd, this.documentDesc.toUpperCase(), this.docValidity.toString(), this.issueAuth.toUpperCase(), this.accCategInDocImp.toUpperCase(), this.docNo.toUpperCase(), this.showdate, this.docName);
        docImpounded.add(docImpound);
        if (docImpounded.isEmpty() || docImpounded == null) {
            setShowDocumentTable(false);
        } else {
            setShowDocumentTable(true);
        }
        setDocNo("");
        setIssueAuth("");
        setAccCategInDocImp("-1");
        setDocValidity(new Date());
        setAddDoc("-1");
        setDocName("");
    }

    public void showWitnessDetails() {
        boolean flag = true;
        if (witnessName.equals("") || witnessName == null) {
            JSFUtils.showMessage("Please Enter The Witness Name");
            flag = false;
        }
        if (witnessAddress.equals("") || witnessAddress == null) {
            JSFUtils.showMessage("Please Enter The Witness  Address");
            flag = false;
        }
        if (witnessPoliceStationName.equals("") || witnessPoliceStationName == null) {
            JSFUtils.showMessage("Please Enter The Witness Police Station Name");
            flag = false;
        }
        WitnessdetailDobj dobj = new WitnessdetailDobj(this.witnessName.toUpperCase(), this.witnessMobileNo, this.witnessAddress.toUpperCase(), this.witnessPoliceStationName.toUpperCase());
        witnessDetailsList.add(dobj);
        setWitnessAddress("");
        setWitnessName("");
        setWitnessMobileNo(0l);
        setWitnessPoliceStationName("");
        if (!witnessDetailsList.isEmpty()) {
            setShowWitnessDetailsPenal(true);
        } else {
            setShowWitnessDetailsPenal(false);
        }
    }

    public void onEditDocImpounded(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Item Edited", ((DocTableDobj) event.getObject()).getDocCode());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onCancelDocImpounded(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Item Cancelled");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        docImpounded.remove((DocTableDobj) event.getObject());
    }

    public void onEditWitnessDetails(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Item Edited", ((WitnessdetailDobj) event.getObject()).getWitnessName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onCancelWitnessDetails(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Item Cancelled");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        witnessDetailsList.remove((WitnessdetailDobj) event.getObject());
    }

    public void getPenaltyWithOffenc() {

        boolean flagPenaltyOffence = true;
        if (("-1".equals(offenceCode)) || "-1".equals(accuseInOffDetail)) {
            JSFUtils.showMessage("Please Select The Offence and Accuse Type");
            flagPenaltyOffence = false;
        } else if ((offenceCode.isEmpty()) || CommonUtils.isNullOrBlank(accuseInOffDetail)) {
            JSFUtils.showMessage("Please fill the Vehicle Details And Accused Details ");
            flagPenaltyOffence = false;
        }
        if (penalOverload) {
            if (CommonUtils.isNullOrBlank(actualWeight)) {
                JSFUtils.showMessage("Actual Weight Can Not Be Blank. ");
                flagPenaltyOffence = false;
            }
            if (CommonUtils.isNullOrBlank(overLoadWeight)) {
                JSFUtils.showMessage("OverLoad Weight Can Not Be Blank. ");
                flagPenaltyOffence = false;
            }
            if (CommonUtils.isNullOrBlank(compoundingFee)) {
                JSFUtils.showMessage("Compounding Fee Can Not Be Blank  ");
                flagPenaltyOffence = false;
            } else if (Integer.parseInt(compoundingFee) < 0) {
                JSFUtils.showMessage("Compounding Fee Can Not be Zero / Less Then Zero  ");
                flagPenaltyOffence = false;
            }
            if (carryGoods.equals("-1")) {
                JSFUtils.showMessage("Carrying Goods Can Not Be Blank. ");
                flagPenaltyOffence = false;
            }
            setOverloadVehicle(true);
        } else if (penalAccessPassanger) {
            if (CommonUtils.isNullOrBlank(actualPassangerWeight)) {
                JSFUtils.showMessage("Passenger Weight can not be blank. ");
                flagPenaltyOffence = false;
            }
            if (CommonUtils.isNullOrBlank(overLoadPassangerWeight)) {
                JSFUtils.showMessage("Passenger OverLoad Weight can not be blank. ");
                flagPenaltyOffence = false;
            }
            if (CommonUtils.isNullOrBlank(compoundingFee)) {
                JSFUtils.showMessage("Compounding Fee Can Not Be Blank. ");
                flagPenaltyOffence = false;
            }
            setOverAccessPassanger(true);
        }

        if (!offenceDetails.isEmpty()) {
            Iterator<OffencesDobj> itr_offence = offenceDetails.iterator();
            while (itr_offence.hasNext()) {
                OffencesDobj offence_dobj = itr_offence.next();
                String check_offence = offence_dobj.getOffenceCode();
                if (check_offence.equals(offenceCode)) {
                    JSFUtils.showMessage("Offence Already Exists");
                    flagPenaltyOffence = false;
                }
            }
        }
        try {
            if (flagPenaltyOffence == true) {
                OffencesDobj dobj_offence = challandao.getOffenceDetails(Integer.parseInt(offenceCode), accuseInOffDetail, vhClass, compoundingFee, getTfVehno2().toUpperCase().trim());
                offenceDetails.add(dobj_offence);
                String recDateCompFee = "";
                if (getRcieptDateInCompFee() == null) {
                    recDateCompFee = "";
                } else {
                    recDateCompFee = getRcieptDateInCompFee().toString();
                }
                Iterator<OffencesDobj> itr = offenceDetails.iterator();
                int CompoundingFee = 0;
                String owenerPenalty = null;
                while (itr.hasNext()) {
                    OffencesDobj doOff = itr.next();
                    if (accuseInOffDetail.equals("O")) {
                        owenerPenalty = doOff.getPenalty();
                    }
                    if (accuseInOffDetail.equals("D")) {
                        owenerPenalty = doOff.getPenalty();
                    }
                    if (accuseInOffDetail.equals("C")) {
                        owenerPenalty = doOff.getPenalty();
                    }
                    if (accuseInOffDetail.equals("N")) {
                        owenerPenalty = doOff.getPenalty();
                    }
                    CompoundingFee = CompoundingFee + Integer.parseInt(owenerPenalty);
                    setCompFee("" + CompoundingFee);
                }
                if (!offenceDetails.isEmpty()) {
                    setShowOffenceDetailPanel(true);
                } else {
                    setShowOffenceDetailPanel(false);
                }
                setDisableSaveButton(true);
            }
            setCompoundingFee("");
        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void onEditOffecedetails(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Offence Edited", ((OffencesDobj) event.getObject()).getOffenceCode());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onCancelffencedetails(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Offence Cancelled");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        String abc = ((OffencesDobj) event.getObject()).getPenalty();
        int latestCompfee = Integer.parseInt(compFee) - Integer.parseInt(abc);
        setCompFee("" + latestCompfee);
        offenceDetails.remove((OffencesDobj) event.getObject());
    }

    public void feePayOnRoad() throws Exception {
        if (tfVehno2 != null && !tfVehno2.isEmpty()) {
            if (CommonUtils.isNullOrBlank(permanentRCPTNO)) {
                permanentRCPTNO = challandao.getCurrentRecptNo();
            }
            setRcieptNoInCompFee(permanentRCPTNO);
            setAdCompFee(compFee);
            setRcieptDateInCompFee(new Date());
            renderedSettleAtSpot = false;

        }
    }

    public void showOffences() throws VahanException, Exception {
        setOffenceList(challandao.getOffenceList(vhClass));
        if (Integer.parseInt(vhClass) >= 70 && Integer.parseInt(vhClass) <= 77) {
            String[][] data = MasterTableFiller.masterTables.VM_ACCUSED.getData();
            accused.put("---Select---", "-1");
            for (int i = 0; i < data.length; i++) {
                accused.put(data[i][1], data[i][0]);
            }
        } else {
            setAccused(challandao.getAccused1());
        }
    }

    //end show offences
    public void showVehicleClass() {
        vehclass.clear();
        String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
        vehclass.add(new SelectItem("-1", "---Select---"));
        for (String[] data1 : data) {
            if (getVehicleType().equals(data1[2])) {
                vehclass.add(new SelectItem(data1[0], data1[1]));
            }
        }

    }

    public void hideShowOverPenals() throws VahanException {
        if (offenceCode.equals("24")) {
            setPenalOverload(true);
            setPenalAccessPassanger(false);
            setCarryGoodsList(new SaveChallanImpl().getGoodsList());
        } else if (offenceCode.equals("41")) {
            setPenalAccessPassanger(true);
            setPenalOverload(false);
        } else {
            setPenalOverload(false);
            setPenalAccessPassanger(false);
        }
    }

    public void overLoadVehicle() {
        int overLoadWieght = Integer.parseInt(actualWeight) - Integer.parseInt(ladenWeight);
        setOverLoadWeight(String.valueOf(overLoadWieght));
        int fee = 2000 + overLoadWieght;
        setCompoundingFee(String.valueOf(fee));
    }

    public void overLoadPassanger() {
        int passangerWieght = Integer.parseInt(seatCap) + Integer.parseInt(standCap);
        int overloadWeight = Integer.parseInt(actualPassangerWeight) - passangerWieght;
        int fee = 2000 + overloadWeight;
        setOverLoadPassangerWeight(String.valueOf(overloadWeight));
        setCompoundingFee(String.valueOf(fee));
    }

    public void showDocumentDialog() {
        if (documentImpoundCheckBox == true) {
            setShowDocumentImpoundPenal(true);
            setOtherDocumentPanel(false);
            PrimeFaces.current().executeScript("PF('" + "document_Dialog" + "').show()");
        } else {
            setShowDocumentImpoundPenal(false);
            PrimeFaces.current().executeScript("PF('" + "document_Dialog" + "').hide()");
        }
    }

    public void showAccuseDialog() {
        if (addAccusedDetails == true) {
            setShowAccusedDialog(true);
            PrimeFaces.current().executeScript("PF('" + "dialog_accused" + "').show()");
        } else {
            setShowAccusedDialog(false);
            PrimeFaces.current().executeScript("PF('" + "dialog_accused" + "').hide()");
        }
    }

    public void showOffenceManuallyPanel() {
        if (addOffenceManuallyPanel == true) {
            setShowOffenceManuallyPanel(true);
        } else {
            setShowOffenceManuallyPanel(false);
        }
    }

    public void showOffenceDetailsDialog() {
        if (cbxAddOffenceDetails == true) {
            setShowAccusedDialog(true);
            PrimeFaces.current().executeScript("PF('" + "dialogOffenceDetails" + "').show()");
        } else {
            setShowAccusedDialog(false);
            PrimeFaces.current().executeScript("PF('" + "dialogOffenceDetails" + "').hide()");
        }
    }

    public void setAdvanceFee(String adCompFee) {
        setAdCompFee(adCompFee);
    }

    public void showWitnessDialog() {
        if (witnessDetailsCheckBox == true) {
            setShowWitnessPanel(true);
            PrimeFaces.current().executeScript("PF('" + "witnessDialog" + "').show()");
        } else {
            PrimeFaces.current().executeScript("PF('" + "witnessDialog" + "').hide()");
            setWitnessName("");
            setWitnessMobileNo(0);
            setWitnessAddress("");
            setShowWitnessPanel(false);
        }
    }

    public void showPanels() throws VahanException {
        if (vehicleImpoundCheckBox == true) {
            setShowVehicleImpoundPenal(true);
            int rand = new Random().nextInt(900) + 100;
            sezNo = sessionVariables.getStateCodeSelected() + sessionVariables.getOffCodeSelected() + rand;
        } else {
            setShowVehicleImpoundPenal(false);
            sezNo = "";
        }
        if (referTOCourtCheckBox == true) {
            setShowRefToCourtPenal(true);
            setAdvancedCompFeeDisabled(true);
            setRecieptDateDisabled(true);
            setRecieptNoDisabled(true);
            setDisablePayonRoadButton(true);
            setCbxTaxPayOnRoadDisabled(true);
        } else {
            setShowRefToCourtPenal(false);
            setAdvancedCompFeeDisabled(false);
            setRecieptDateDisabled(false);
            setRecieptNoDisabled(false);
            setDisablePayonRoadButton(false);
            setCbxTaxPayOnRoadDisabled(false);
        }
        if (cbSaveByChassisNo == true) {
            setTfVehicleNo2Disabled(false);
            setTfChessisNoDisabled(false);
            setDisableOwnerDetails(false);
        } else {
            setTfVehicleNo2Disabled(false);
            setDisableOwnerDetails(true);

        }
    }

    public void disableOnRoadDetails() {
        setRenderAtApproval(false);
    }

    public void getSelectedValues(String name, String value) {
        try {
            if ("court".equalsIgnoreCase(name)) {
                setMagistrate(challandao.getSelectedMagistrate("court_cd", value));
            }
            if ("magistrate".equalsIgnoreCase(name)) {
                setCourtName(challandao.getSelectedMagistrate("magistrate_cd", value));
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

    public void saveChallanData() {
        HttpSession ses = Util.getSession();
        dobj1 = new SaveChallanDobj();
        flag = checkChallanData(dobj1);
        if (!flag) {
            return;
        }
        String userid = sessionVariables.getEmpCodeLoggedIn();
        Status_dobj status_dobj = new Status_dobj();
        status_dobj.setRegn_no(registrationNo);
        status_dobj.setPur_cd(TableConstants.VM_MAST_ENFORCEMENT);
        status_dobj.setAction_cd(TableConstants.VM_ROLE_ENFORCEMENT_ENTRY);
        status_dobj.setFlow_slno(1);
        status_dobj.setFile_movement_slno(1);
        status_dobj.setState_cd(sessionVariables.getStateCodeSelected());
        status_dobj.setOff_cd(sessionVariables.getOffCodeSelected());
        status_dobj.setEmp_cd(0);

        if (flag) {
            try {
                String str = challanNo;
                boolean success = challandao.saveChallanData(accusedDetails, docImpounded, offenceDetails, ListForCompoundigFee, dobj1, status_dobj, witnessDetailsList);
                if (success) {
                    applicationNo = dobj1.getApplicationNO();
                    ses.setAttribute("eChallan_appl_no", applicationNo);
                    challandao.updateChallanNo(userid);
                    JSFUtils.setFacesMessage("Your Application No IS-::" + dobj1.getApplicationNO(), "message", JSFUtils.INFO);
                    setDisablePrintButton(true);
                    setDisableSaveButton(false);
                    setDisableResetButton(false);
                    reset();
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

    }

    private boolean checkChallanData(SaveChallanDobj dobj1) {
        boolean flagStatus = true;
        if (cbSaveByChassisNo == true) {
            if (CommonUtils.isNullOrBlank(chasiNo)) {
                JSFUtils.setFacesMessage("Please Enter The chassis No", "message", JSFUtils.WARN);
                flagStatus = false;
            } else {
                dobj1.setChasiNo(getChasiNo().trim().toUpperCase());
                registrationNo = ChallanUtil.getSubstringFromRight(chasiNo.toUpperCase().trim(), 10);
                dobj1.setVehicleNo(registrationNo);
                setTfVehno2(registrationNo);
            }
        } else {
            registrationNo = getTfVehno2().toUpperCase().trim();
            if (registrationNo.equals("") || registrationNo == null || registrationNo.equals("0000")) {
                JSFUtils.showMessage("Please Fill The Registration No");
                flag = false;
            } else {
                dobj1.setVehicleNo(registrationNo);
            }
        }
        if (CommonUtils.isNullOrBlank(challanNo)) {
            JSFUtils.setFacesMessage("Please Enter The Challan No", "message", JSFUtils.WARN);
            flagStatus = false;
        } else {
            dobj1.setChallanNo(getChallanNo().trim());
        }
        if (challanPlace.equals("") || challanPlace == null) {
            JSFUtils.setFacesMessage("Please Enter The Challan Place", "message", JSFUtils.WARN);
            flagStatus = false;
        } else {
            dobj1.setChallanPlace(getChallanPlace().toUpperCase().trim());
        }
        if (officerNo.equals("-1") || officerNo == null) {
            JSFUtils.showMessage("Please Select Officer");
            flag = false;
        } else {
            dobj1.setOfficerNo(getOfficerNo().trim());
        }

        if (offenceDetails.size() <= 0) {
            JSFUtils.showMessage("Please Fill The Offence And Penalty Details");
            flag = false;
        }
        if (ladenWeight.equals("") || ladenWeight == null) {
            dobj1.setLadenWt("0");
        } else {
            dobj1.setLadenWt(getLadenWeight().trim());
        }
        if (seatCap.equals("") || seatCap == null) {
            dobj1.setSeatCap("0");
        } else {
            dobj1.setSeatCap(getSeatCap().trim());
        }
        if (standCap.equals("") || standCap == null) {
            dobj1.setStandCap("0");
        } else {
            dobj1.setStandCap(getStandCap().trim());
        }
        if (sleeperCap.equals("") || sleeperCap == null) {
            dobj1.setSleeperCap("0");
        } else {
            dobj1.setSleeperCap(getSleeperCap().trim());
        }
        if (compFee == null || compFee.equals("") || "0".equals(compFee)) {
            JSFUtils.setFacesMessage("Compounding Fee Never Be Empty Please Add Offences", "message", JSFUtils.FATAL);
            flagStatus = false;
        } else {
            dobj1.setCompFee(getCompFee().trim());
        }
        if (adCompFee == null || adCompFee.equals("")) {
            dobj1.setAdCompFee(0);
        } else {
            dobj1.setAdCompFee(Integer.parseInt(getAdCompFee().trim()));
        }
        if (CommonUtils.isNullOrBlank(contactOfficer)) {
            dobj1.setContactOfficer("");
        } else {
            dobj1.setContactOfficer(getContactOfficer().toUpperCase().trim());
        }
        if (CommonUtils.isNullOrBlank(impoundRemarks)) {
            dobj1.setImpoundRemarks("");
        } else {
            dobj1.setImpoundRemarks(getImpoundRemarks().toUpperCase().trim());
        }
        if (CommonUtils.isNullOrBlank(accusedRemarks)) {
            dobj1.setAccusedRemarks("");
        } else {
            dobj1.setAccusedRemarks(getAccusedRemarks().toUpperCase().trim());
        }
        dobj1.setNCCRNo(getNccrNo());
        if (docImpounded.size() > 0) {
            dobj1.setDocImpnd("Y");
        } else {
            dobj1.setDocImpnd("N");
        }
        if (CommonUtils.isNullOrBlank(impndPlace) || impndPlace.isEmpty()) {
            dobj1.setVehImpnd("N");
        } else {
            dobj1.setVehImpnd("Y");
        }
        if (accusedDetails.isEmpty()) {
            JSFUtils.showMessage("Please Fill The Accuse Details");
            flag = false;
        }
        if (config_dobj.isCommingFrom()) {
            if (CommonUtils.isNullOrBlank(commingFrom)) {
                JSFUtils.showMessage("Coming From can not be blank.");
                flag = false;
            } else {
                dobj1.setCommingFrom(commingFrom);
            }
        }
        if (config_dobj.isGoingTo()) {
            if (CommonUtils.isNullOrBlank(goingTo)) {
                JSFUtils.showMessage("Going To can not be blank.");
                flag = false;
            } else {
                dobj1.setGoingTo(goingTo);
            }
        }

        if (CommonUtils.isNullOrBlank(sezNo)) {
            dobj1.setSezNo("");
        } else {
            dobj1.setSezNo(getSezNo().toUpperCase().trim());
        }
        if (CommonUtils.isNullOrBlank(siezePoliceStationName)) {
            dobj1.setSezPoliceStation("");
        } else {
            dobj1.setSezPoliceStation(getSiezePoliceStationName().toUpperCase().trim());
        }
        if (CommonUtils.isNullOrBlank(siezeDistrictName)) {
            dobj1.setSezDistrict("");
        } else {
            dobj1.setSezDistrict(getSiezeDistrictName().toUpperCase().trim());
        }
        dobj1.setVehImpdDate(getVehImpdDate());

        if (CommonUtils.isNullOrBlank(impndPlace)) {
            dobj1.setVehImpndPlace("");
        } else {
            dobj1.setVehImpndPlace(getImpndPlace().toUpperCase().trim());
        }
        if (CommonUtils.isNullOrBlank(contactOfficer)) {
            dobj1.setContactOfficer("");
        } else {
            dobj1.setContactOfficer(getContactOfficer());
        }
        if (!("-1".equals(getCourtName()))) {
            dobj1.setCourtName(getCourtName());
            dobj1.setHeaaringDt(getHeaaringDt());
        }
        if (overloadVehicle) {
            dobj1.setOverloadVehicle(true);
            dobj1.setOverLoadWeight(Integer.parseInt(overLoadWeight));
            dobj1.setTypesOfGooods(Integer.parseInt(carryGoods));
        }
        if (overAccessPassanger) {
            dobj1.setAccessPassangerWeight(Integer.parseInt(overLoadPassangerWeight));
            dobj1.setOverAccessPassanger(true);
        }
        if (SettleAtSpot == true) {
            dobj1.setCheckBoxsettledAtSpot(SettleAtSpot);
            int special = 0;
            int total = 0;
            int sumAmout = 0;
            if (offenceDetails != null) {
                for (OffencesDobj obj : offenceDetails) {
                    if (obj.getMvcrClause().equals("194")) {
                        special = special + Integer.parseInt(obj.getPenalty());

                    } else if (!obj.getMvcrClause().equals("194")) {
                        total = total + Integer.parseInt(obj.getPenalty());
                    }
                }
                sumAmout = special + total / 2;
                int AdvComFee = Integer.parseInt(adCompFee);
                if (AdvComFee < sumAmout) {
                    checkAdvanceCompFee = true;
                    JSFUtils.setFacesMessage("Advance Comp Fee should be minimum of 50% of Compounding Fee", "messages", JSFUtils.FATAL);
                    flagStatus = false;
                    return flagStatus;
                }
            }
        }
        dobj1.setState(getState().toUpperCase().trim());
        dobj1.setDistrict(getDistrict().toUpperCase().trim());
        dobj1.setChasiNo(getChasiNo().toUpperCase().trim());
        dobj1.setOwnerName(getOwnerNAme().toUpperCase().trim());
        dobj1.setVhClass(getVhClass().toUpperCase().trim());
        dobj1.setFuel(getFuel().toUpperCase().trim());
        dobj1.setColor(getColor().toUpperCase().trim());
        dobj1.setChallanDt(getChallanDateCal());
        dobj1.setChallanTime(getChallanTime());
        dobj1.setHeaaringDt(getHeaaringDt());
        dobj1.setOnRoadPayRcieptDate(rcieptDateInCompFee);
        dobj1.setOnRoadPayRcieptNo(rcieptNoInCompFee);
        if (!CommonUtils.isNullOrBlank(witnessName)) {
            witnessDobj.setWitnessName(witnessName.trim().toUpperCase());
        }
        if (witnessMobileNo != 0) {
            witnessDobj.setWitnessContactNo(witnessMobileNo);
        }
        if (!CommonUtils.isNullOrBlank(witnessName)) {
            witnessDobj.setWitnessAddress(witnessAddress.trim().toUpperCase());
        }
        return flagStatus;
    }

    public void getChallanDataForVerification() {
        try {
            String Appl_no = String.valueOf(appl_details.getAppl_no());
            int CompoundingFee = 0;
            accusedDetails = challandao.getAccusedDetailsForApproveVarification(Appl_no);
            oldaccused = challandao.getAccusedDetailsForApproveVarification(Appl_no);
            if (accusedDetails.size() > 0) {
                showaccusedDataTable = true;
                addAccusedDetails = true;
            } else {
                showaccusedDataTable = false;
                addAccusedDetails = false;
            }
            offenceDetails = challandao.getOffenceDetailsForApproveVarification(Appl_no);
            Iterator<OffencesDobj> itr = offenceDetails.iterator();
            String owenerPenalty = null;
            while (itr.hasNext()) {
                OffencesDobj doOff = itr.next();
                owenerPenalty = doOff.getPenalty();
                CompoundingFee = CompoundingFee + Integer.parseInt(owenerPenalty);
                setCompFee("" + CompoundingFee);
            }
            if (!offenceDetails.isEmpty()) {
                setCbxAddOffenceDetails(true);
                setShowOffenceDetailPanel(true);
            }
            oldOffenceList = challandao.getOffenceDetailsForApproveVarification(Appl_no);

            SaveChallanDobj challan_dobj = new SaveChallanDobj();
            challan_dobj = challandao.getChallanDetailsAndOwnerDetails(Appl_no);
            challanDobjPrv = (SaveChallanDobj) challan_dobj.clone();
            docImpounded = challandao.getdocImpoundedForVarification(Appl_no);
            if (docImpounded.size() > 0) {
                documentImpoundCheckBox = true;
                showDocumentTable = true;
            } else {
                documentImpoundCheckBox = false;
                showDocumentTable = false;
            }
            oldDocImpoundedList = challandao.getdocImpoundedForVarification(Appl_no);
            if (challan_dobj != null) {
                setAdCompFee(Integer.toString(challan_dobj.getAdCompFee()));
                setApplicationNo(challan_dobj.getApplicationNO());
                setRcieptNoInCompFee(challan_dobj.getOnRoadPayRcieptNo());
                setRcieptDateInCompFee(challan_dobj.getOnRoadPayRcieptDate());
                setChallanNo(challan_dobj.getChallanNo());
                setOfficerNo(challan_dobj.getChalOff());
                setNccrNo(challan_dobj.getNCCRNo());
                setChallanPlace(challan_dobj.getChallanPlace());
                setChallanDateCal(challan_dobj.getChallanDt());
                setChallanTime(challan_dobj.getChallanTime());
                setTfVehno1(challan_dobj.getVehno1());
                setTfVehno2(challan_dobj.getVehno2());
                setChasiNo(challan_dobj.getChasiNo());
                setVhClass(challan_dobj.getVhClass());
                setVehicleType(challan_dobj.getVehicleType());
                setState(challan_dobj.getState());
                setDistrict(challan_dobj.getDistrict());
                setFuel(challan_dobj.getFuel());
                setLadenWeight(challan_dobj.getLadenWt());
                setSeatCap(challan_dobj.getSeatCap());
                setSleeperCap(challan_dobj.getSleeperCap());
                setStandCap(challan_dobj.getStandCap());
                setOwnerNAme(challan_dobj.getOwnerName());
                setColor(challan_dobj.getColor());
                setAccusedRemarks(challan_dobj.getAccusedRemarks());
                setImpoundRemarks(challan_dobj.getImpoundRemarks());
                setVehImpdDate(challan_dobj.getVehImpdDate());
                setImpndPlace(challan_dobj.getVehImpndPlace());
                setContactOfficer(challan_dobj.getContactOfficer());
                setSiezePoliceStationName(challan_dobj.getSezPoliceStation());
                setSiezeDistrictName(challan_dobj.getSezDistrict());
                setCommingFrom(challan_dobj.getCommingFrom());
                setGoingTo(challan_dobj.getGoingTo());
                setSezNo(challan_dobj.getSezNo());
                setSettleAtSpot(challan_dobj.isCheckBoxsettledAtSpot());

                if (!("".equals(challan_dobj.getSezNo()) || challan_dobj.getSezNo() == null)) {
                    setShowVehicleImpoundPenal(true);
                    vehicleImpoundCheckBox = true;
                } else {
                    setShowVehicleImpoundPenal(false);
                    vehicleImpoundCheckBox = false;
                }
                if (!("".equals(challan_dobj.getCourtName()) || challan_dobj.getHeaaringDt() == null)) {
                    referTOCourtCheckBox = true;
                    setShowRefToCourtPenal(true);
                    setCourtName(challan_dobj.getCourtName());
                    setHeaaringDt(challan_dobj.getHeaaringDt());
                    setMagistrate(challan_dobj.getMagistrate());
                } else {
                    setShowRefToCourtPenal(false);
                    referTOCourtCheckBox = false;
                    setCourtName("");
                    setHeaaringDt(null);
                }
                setOffenceList(challandao.getOffenceList(challan_dobj.getVhClass()));
                if (Integer.parseInt(challan_dobj.getVhClass()) >= 70 && Integer.parseInt(challan_dobj.getVhClass()) <= 77) {
                    String[][] data = MasterTableFiller.masterTables.VM_ACCUSED.getData();
                    accused.put("---Select---", "-1");
                    for (int i = 0; i < data.length; i++) {
                        accused.put(data[i][1], data[i][0]);
                    }
                } else {
                    setAccused(challandao.getAccused1());
                }
                accusedUpdatedList = getAccusedCategory(accusedDetails);
                witnessDetailsList = challandao.fetchWitnessDetails(Appl_no);
                oldWitnessDetailsList = challandao.fetchWitnessDetails(Appl_no);
                for (WitnessdetailDobj witnessDobj : witnessDetailsList) {
                    if (witnessDobj != null) {
                        setWitnessDetailsCheckBox(true);
                        setShowWitnessDetailsPenal(true);
                    } else {
                        setWitnessDetailsCheckBox(false);
                        setShowWitnessDetailsPenal(false);
                        setWitnessAddress("");
                        setWitnessMobileNo(0);
                        setWitnessName("");
                        setWitnessPoliceStationName("");
                    }
                }


                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(Appl_no, TableConstants.VM_MAST_ENFORCEMENT);
                setTfVehicleNo2Disabled(true);
                setTfChessisNoDisabled(true);
                setDisableOwnerDetails(true);

                if (SettleAtSpot == false) {
                    setRenderedSettleAtSpot(false);
                }

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

    public void reset() {
        try {
            String currChallanNo = challandao.fetchchallanNo();
            setChallanNo(currChallanNo);
            setOfficerNo(sessionVariables.getEmpCodeLoggedIn().toUpperCase());
            setTfVehno1(null);
            setTfVehno2(null);
            setOwnerNAme("");
            setFuel("-1");
            setVhClass("-1");
            setState("-1");
            setDistrict("-1");
            setColor("");
            setLadenWeight("");
            setVehicleType("-1");
            setSleeperCap("");
            setStandCap("");
            setSeatCap("");
            setChallanTime(DateUtils.getTimeInHHMMSS(new Date()));
            setChallanDateCal(new Date());
            setCommingFrom("");
            setGoingTo("");
            setVehImpdDate(new Date());
            setDocValidity(new Date());
            setRcieptDateInCompFee(new Date());
            setAccuName("");
            setAccusCatg("-1");
            setDlNo("");
            setAccuseAddress("");
            setAccuesdFlag("-1");
            setAccuesdCity("");
            setAccuesdPincode(0);
            setAccuesdPoliceSation("");
            setChasiNo("");
            setContactOfficer("");
            setNccrNo("");
            setChallanPlace("");
            setImpndPlace("");
            setImpdDt(new Date());
            setAdCompFee("0");
            accusedDetails.clear();
            docImpounded.clear();
            offenceDetails.clear();
            witnessDetailsList.clear();
            ListForCompoundigFee.clear();
            setCourtName("-1");
            setHeaaringDt(new Date());
            setSezNo("");
            setImpoundRemarks("");
            setAccusedRemarks("");
            setOffenceCode(null);
            setAccuseInOffDetail("-1");
            setAccuseInCompFeeDetail("-1");
            setCompFee("0");
            setBalanceFee("0");
            setRcieptNoInCompFee("");
            setDocNo("");
            setIssueAuth("");
            setRcieptDateInCompFee(null);
            documentImpoundCheckBox = false;
            vehicleImpoundCheckBox = false;
            referTOCourtCheckBox = false;
            setShowDocumentImpoundPenal(false);
            setShowRefToCourtPenal(false);
            setShowVehicleImpoundPenal(false);
            setTfVehicleNo2Disabled(false);
            setTfChessisNoDisabled(true);
            setDisableOwnerDetails(true);
            setOtherDocumentPanel(true);
            SettleAtSpot = false;
            String[][] data = MasterTableFiller.masterTables.VM_ACCUSED.getData();
            accused = new LinkedHashMap<String, Object>();
            accused.put("----Select----", "-1");
            for (int i = 0; i < data.length; i++) {
                accused.put(data[i][1], data[i][0]);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }

    }

    public void confirmprintCertificate() {
        //RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('printParticular').show()");
    }

    public void confirmDocDialog() {
        //RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('document_Dialog').show()");
        setOtherDocumentPanel(true);
    }

    public void changeDoc() {
        if (addDoc.equals("6")) {
            setOtherDocumentPanel(true);
            setDocName("");
        } else {
            setOtherDocumentPanel(false);
            setDocName("");
        }
    }

    public void showPanel() {
        setShowMinusButton(true);
        setShowVehicleInfoPanel(true);
        setShowPlusButton(false);
    }

    public void hidePanel() {
        setShowMinusButton(false);
        setShowVehicleInfoPanel(false);
        setShowPlusButton(true);
    }

    public void filOwnerDetails() {
        if (accusCatg.equalsIgnoreCase("O")) {
            setAccuName(ownerNAme);
            setAccuseAddress(hiddenaddressField);
        } else {
            setAccuName("");
            setAccuseAddress("");
        }
    }

    public void verifyFields() {
        if (SettleAtSpot == true) {
            setAdCompFee(adCompFee);
        } else {
            setAccusedRemarks("");
            setAdCompFee("0");
            setRcieptNoInCompFee("");
            setRcieptDateInCompFee(null);
            checkAdvanceCompFee = false;
        }
    }

    public void receiptDt() {
        setRcieptDateInCompFee(challanDateCal);
    }

    public Date getDocValidity() {
        return docValidity;
    }

    public String getIssueAuth() {
        return issueAuth;
    }

    public void setDocValidity(Date docValidity) {
        this.docValidity = docValidity;
    }

    public void setIssueAuth(String issueAuth) {
        this.issueAuth = issueAuth;
    }

    public String getAddDoc() {
        return addDoc;
    }

    public void setAddDoc(String addDoc) {
        this.addDoc = addDoc;
    }

    public String getChallanNo() {
        return challanNo;
    }

    public void setChallanNo(String challanNo) {
        this.challanNo = challanNo;
    }

    public String getDistrict() {
        return district;
    }

    public String getState() {
        return state;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the veh_impd_date
     */
    public Date getVehImpdDate() {
        return vehImpdDate;
    }

    public void setVehImpdDate(Date vehImpdDate) {
        this.vehImpdDate = vehImpdDate;
    }

    public String getAccuName() {
        return accuName;
    }

    public String getAccusCatg() {
        return accusCatg;
    }

    public String getAdCompFee() {
        return adCompFee;
    }

    public String getChallanPlace() {
        return challanPlace;
    }

    public String getChasiNo() {
        return chasiNo;
    }

    public String getCompFee() {
        return compFee;
    }

    public String getContactOfficer() {
        return contactOfficer;
    }

    public String getCourtName() {
        return courtName;
    }

    public String getDlNo() {
        return dlNo;
    }

    public String getFitVal() {
        return fitVal;
    }

    public Date getHeaaringDt() {
        return heaaringDt;
    }

    public Date getImpdDt() {
        return impdDt;
    }

    public String getImpndPlace() {
        return impndPlace;
    }

    public String getInsVal() {
        return insVal;
    }

    public String getLadenWeight() {
        return ladenWeight;
    }

    public String getOfficerNo() {
        return officerNo;
    }

    public String getPmtVal() {
        return pmtVal;
    }

    public String getRandomNumber() {
        return randomNumber;
    }

    public String getRegionState() {
        return regionState;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public String getSeatCap() {
        return seatCap;
    }

    public int getSettledFee() {
        return settledFee;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public String getTaxVal() {
        return taxVal;
    }

    public String getTfVehno1() {
        return tfVehno1;
    }

    public String getTfVehno2() {
        return tfVehno2;
    }

    public String getTime() {
        return time;
    }

    public int getTotalFee() {
        return totalFee;
    }

    public String getUnladenWeight() {
        return unladenWeight;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public String getVehno1() {
        return vehno1;
    }

    public String getVehno2() {
        return vehno2;
    }

    public String getVhClass() {
        return vhClass;
    }

    public String getWitnessAddress() {
        return witnessAddress;
    }

    public String getWitnessName() {
        return witnessName;
    }

    public boolean isDocImpnd() {
        return docImpnd;
    }

    public boolean isVehImpnd() {
        return vehImpnd;
    }

    public void setAccuName(String accuName) {
        this.accuName = accuName;
    }

    public void setAccusCatg(String accusCatg) {
        this.accusCatg = accusCatg;
    }

    public void setAdCompFee(String adCompFee) {
        this.adCompFee = adCompFee;
    }

    public void setChallanPlace(String challanPlace) {
        this.challanPlace = challanPlace;
    }

    public void setChasiNo(String chasiNo) {
        this.chasiNo = chasiNo;
    }

    public void setCompFee(String compFee) {
        this.compFee = compFee;
    }

    public void setContactOfficer(String contactOfficer) {
        this.contactOfficer = contactOfficer;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public void setDlNo(String dlNo) {
        this.dlNo = dlNo;
    }

    public void setDocImpnd(boolean docImpnd) {
        this.docImpnd = docImpnd;
    }

    public void setFitVal(String fitVal) {
        this.fitVal = fitVal;
    }

    public void setHeaaringDt(Date heaaringDt) {
        this.heaaringDt = heaaringDt;
    }

    public void setImpdDt(Date impdDt) {
        this.impdDt = impdDt;
    }

    public void setImpndPlace(String impndPlace) {
        this.impndPlace = impndPlace;
    }

    public void setInsVal(String insVal) {
        this.insVal = insVal;
    }

    public void setLadenWeight(String ladenWeight) {
        this.ladenWeight = ladenWeight;
    }

    public void setOfficerNo(String officerNo) {
        this.officerNo = officerNo;
    }

    public void setPmtVal(String pmtVal) {
        this.pmtVal = pmtVal;
    }

    public void setRandomNumber(String randomNumber) {
        this.randomNumber = randomNumber;
    }

    public void setRegionState(String regionState) {
        this.regionState = regionState;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public void setSeatCap(String seatCap) {
        this.seatCap = seatCap;
    }

    public void setSettledFee(int settledFee) {
        this.settledFee = settledFee;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public void setTaxVal(String taxVal) {
        this.taxVal = taxVal;
    }

    public void setTfVehno1(String tfVehno1) {
        this.tfVehno1 = tfVehno1;
    }

    public void setTfVehno2(String tfVehno2) {
        this.tfVehno2 = tfVehno2;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTotalFee(int totalFee) {
        this.totalFee = totalFee;
    }

    public void setUnladenWeight(String unladenWeight) {
        this.unladenWeight = unladenWeight;
    }

    public void setVehImpnd(boolean vehImpnd) {
        this.vehImpnd = vehImpnd;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public void setVehno1(String vehno1) {
        this.vehno1 = vehno1;
    }

    public void setVehno2(String vehno2) {
        this.vehno2 = vehno2;
    }

    public void setVhClass(String vhClass) {
        this.vhClass = vhClass;
    }

    public void setWitnessAddress(String witnessAddress) {
        this.witnessAddress = witnessAddress;
    }

    public void setWitnessName(String witnessName) {
        this.witnessName = witnessName;
    }

    public String getStandCap() {
        return standCap;
    }

    public void setStandCap(String standCap) {
        this.standCap = standCap;
    }

    public boolean isChallanRefToCourt() {
        return challanRefToCourt;
    }

    public void setChallanRefToCourt(boolean challanRefToCourt) {
        this.challanRefToCourt = challanRefToCourt;
    }

    public String getChallanTime() {
        return challanTime;
    }

    public void setChallanTime(String challanTime) {
        this.challanTime = challanTime;
    }

    public String getAdd1() {
        return add1;
    }

    public String getAdd2() {
        return add2;
    }

    public void setAdd1(String add1) {
        this.add1 = add1;
    }

    public void setAdd2(String add2) {
        this.add2 = add2;
    }

    public Date getChallanDateCal() {
        return challanDateCal;
    }

    public void setChallanDateCal(Date challanDateCal) {
        this.challanDateCal = challanDateCal;
    }

    public String getAccuseAddress() {
        return AccuseAddress;
    }

    public void setAccuseAddress(String AccuseAddress) {
        this.AccuseAddress = AccuseAddress;
    }

    public List getRtoCode() {
        return rtoCode;
    }

    public void setRtoCode(List rtoCode) {
        this.rtoCode = rtoCode;
    }

    public boolean getCb_doc_impound() {
        return cbDocImpound;
    }

    public boolean getCb_reftoCort() {
        return cbRefToCort;
    }

    public boolean getCb_veh_impounded() {
        return cbVehImpounded;
    }

    public void setCbDocImpound(boolean cbDocImpound) {
        this.cbDocImpound = cbDocImpound;
    }

    public void setCbRefToCort(boolean cbRefToCort) {
        this.cbRefToCort = cbRefToCort;
    }

    public void setCbVehImpounded(boolean cbVehImpounded) {
        this.cbVehImpounded = cbVehImpounded;
    }

    public String getOwnerNAme() {
        return ownerNAme;
    }

    public void setOwnerNAme(String ownerNAme) {
        this.ownerNAme = ownerNAme;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSleeperCap() {
        return sleeperCap;
    }

    public void setSleeperCap(String sleeperCap) {
        this.sleeperCap = sleeperCap;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public List<AccusedDetailsDobj> getAccusedDetails() {
        return accusedDetails;
    }

    public void setAccusedDetails(List<AccusedDetailsDobj> accusedDetails) {
        this.accusedDetails = accusedDetails;
    }

    public String getAccCategInDocImp() {
        return accCategInDocImp;
    }

    public void setAccCategInDocImp(String accCategInDocImp) {
        this.accCategInDocImp = accCategInDocImp;
    }

    public List<DocTableDobj> getDocImpounded() {
        return docImpounded;
    }

    public void setDocImpounded(List<DocTableDobj> docImpounded) {
        this.docImpounded = docImpounded;
    }

    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public List getOffenceList() {
        return offenceList;
    }

    public void setOffenceList(List offenceList) {
        this.offenceList = offenceList;
    }

    public List<OffencesDobj> getOffenceDetails() {
        return offenceDetails;
    }

    public void setOffenceDetails(List<OffencesDobj> offenceDetails) {
        this.offenceDetails = offenceDetails;
    }

    public String getOffenceCode() {
        return offenceCode;
    }

    public void setOffenceCode(String offenceCode) {
        this.offenceCode = offenceCode;
    }

    public String getAccuseInOffDetail() {
        return accuseInOffDetail;
    }

    public void setAccuseInOffDetail(String accuseInOffDetail) {
        this.accuseInOffDetail = accuseInOffDetail;
    }

    public String getAccuseInCompFeeDetail() {
        return accuseInCompFeeDetail;
    }

    public void setAccuseInCompFeeDetail(String accuseInCompFeeDetail) {
        this.accuseInCompFeeDetail = accuseInCompFeeDetail;
    }

    public String getBalanceFee() {
        return balanceFee;
    }

    public void setBalanceFee(String balanceFee) {
        this.balanceFee = balanceFee;
    }

    public List<CompoundingFeeDobj> getListForCompoundigFee() {
        return ListForCompoundigFee;
    }

    public void setListForCompoundigFee(List<CompoundingFeeDobj> ListForCompoundigFee) {
        this.ListForCompoundigFee = ListForCompoundigFee;
    }

    public String getRcieptNoInCompFee() {
        return rcieptNoInCompFee;
    }

    public void setRcieptNoInCompFee(String rcieptNoInCompFee) {
        this.rcieptNoInCompFee = rcieptNoInCompFee;
    }

    public boolean isDisablePayonRoadButton() {
        return disablePayonRoadButton;
    }

    public void setDisablePayonRoadButton(boolean disablePayonRoadButton) {
        this.disablePayonRoadButton = disablePayonRoadButton;
    }

    public Date getRcieptDateInCompFee() {
        return rcieptDateInCompFee;
    }

    public void setRcieptDateInCompFee(Date rcieptDateInCompFee) {
        this.rcieptDateInCompFee = rcieptDateInCompFee;
    }

    public String getAdCfPenalty() {
        return adCfPenalty;
    }

    public void setAdCfPenalty(String adCfPenalty) {
        this.adCfPenalty = adCfPenalty;
    }

    public String getNccrNo() {
        return nccrNo;
    }

    public void setNccrNo(String nccrNo) {
        this.nccrNo = nccrNo;
    }

    public String getAccusedRemarks() {
        return accusedRemarks;
    }

    public void setAccusedRemarks(String accusedRemarks) {
        this.accusedRemarks = accusedRemarks;
    }

    public String getImpoundRemarks() {
        return impoundRemarks;
    }

    public void setImpoundRemarks(String impoundRemarks) {
        this.impoundRemarks = impoundRemarks;
    }

    public String getSezNo() {
        return sezNo;
    }

    public void setSezNo(String sezNo) {
        this.sezNo = sezNo;
    }

    public boolean isTfOfficerNoDisable() {
        return tfOfficerNoDisable;
    }

    public void setTfOfficerNoDisable(boolean tfOfficerNoDisable) {
        this.tfOfficerNoDisable = tfOfficerNoDisable;
    }

    public List getCourtCode() {
        return courtCode;
    }

    public void setCourtCode(List courtCode) {
        this.courtCode = courtCode;
    }

    public boolean isTfChessisNoDisabled() {
        return tfChessisNoDisabled;
    }

    public void setTfChessisNoDisabled(boolean tfChessisNoDisabled) {
        this.tfChessisNoDisabled = tfChessisNoDisabled;
    }

    public boolean isTfVehicleNo2Disabled() {
        return tfVehicleNo2Disabled;
    }

    public void setTfVehicleNo2Disabled(boolean tfVehicleNo2Disabled) {
        this.tfVehicleNo2Disabled = tfVehicleNo2Disabled;
    }

    public boolean isPenalOverload() {
        return penalOverload;
    }

    public void setPenalOverload(boolean penalOverload) {
        this.penalOverload = penalOverload;
    }

    public String getActualWeight() {
        return actualWeight;
    }

    public void setActualWeight(String actualWeight) {
        this.actualWeight = actualWeight;
    }

    public String getOverLoadWeight() {
        return overLoadWeight;
    }

    public void setOverLoadWeight(String overLoadWeight) {
        this.overLoadWeight = overLoadWeight;
    }

    public String getCompoundingFee() {
        return compoundingFee;
    }

    public void setCompoundingFee(String compoundingFee) {
        this.compoundingFee = compoundingFee;
    }

    public Map<String, Object> getAccused() {
        return accused;
    }

    public void setAccused(Map<String, Object> accused) {
        this.accused = accused;
    }

    public String getHiddenaddressField() {
        return hiddenaddressField;
    }

    public void setHiddenaddressField(String hiddenaddressField) {
        this.hiddenaddressField = hiddenaddressField;
    }

    public Map<String, Object> getAccusedUpdatedList() {
        return accusedUpdatedList;
    }

    public void setAccusedUpdatedList(Map<String, Object> accusedUpdatedList) {
        this.accusedUpdatedList = accusedUpdatedList;
    }

    public List getVehclass() {
        return vehclass;
    }

    public void setVehclass(List vehclass) {
        this.vehclass = vehclass;
    }

    public List getFuelList() {
        return fuelList;
    }

    public void setFuelList(List fuelList) {
        this.fuelList = fuelList;
    }

    public List getOfficer() {
        return officer;
    }

    public void setOfficer(List officer) {
        this.officer = officer;
    }

    public List getStateCode() {
        return stateCode;
    }

    public void setStateCode(List stateCode) {
        this.stateCode = stateCode;
    }

    public Map<String, Object> getPurposeLabelValue() {
        return purposeLabelValue;
    }

    public void setPurposeLabelValue(Map<String, Object> purposeLabelValue) {
        this.purposeLabelValue = purposeLabelValue;
    }

    public String getAccusedDesc() {
        return accusedDesc;
    }

    public void setAccusedDesc(String accusedDesc) {
        this.accusedDesc = accusedDesc;
    }

    public Map<String, Object> getDocCode() {
        return docCode;
    }

    public void setDocCode(Map<String, Object> docCode) {
        this.docCode = docCode;
    }

    public String getDocumentDesc() {
        return documentDesc;
    }

    public void setDocumentDesc(String documentDesc) {
        this.documentDesc = documentDesc;
    }

    public String getAccusedDescDocImpnd() {
        return accusedDescDocImpnd;
    }

    public void setAccusedDescDocImpnd(String accusedDescDocImpnd) {
        this.accusedDescDocImpnd = accusedDescDocImpnd;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    public boolean isDisableSaveButton() {
        return disableSaveButton;
    }

    public void setDisableSaveButton(boolean disableSaveButton) {
        this.disableSaveButton = disableSaveButton;
    }

    public int getPurCd() {
        return purCd;
    }

    public void setPurCd(int purCd) {
        this.purCd = purCd;
    }

    @Override
    public String save() {
        String returnLocation = "";
        SaveChallanImpl impl = null;
        String actionCode = String.valueOf(appl_details.getCurrent_action_cd());
        try {
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());
            SaveChallanDobj dobj1 = new SaveChallanDobj();
            flag = checkChallanData(dobj1);
            if (flag) {
                impl = new SaveChallanImpl();
                impl.saveUpdatedDetails(actionCode, status, ComparisonBeanImpl.changedDataContents(compareChanges()), accusedDetails, docImpounded, offenceDetails, ListForCompoundigFee, dobj1, challanDobjPrv, witnessDetailsList);
                returnLocation = "seatwork";
            }
            if (flag == false) {
                setStatusMsg("");
            }
        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        return returnLocation;
    }

    public List<AccusedDetailsDobj> getOldaccused() {
        return oldaccused;
    }

    public void setOldaccused(List<AccusedDetailsDobj> oldaccused) {
        this.oldaccused = oldaccused;
    }

    public List<OffencesDobj> getOldOffenceList() {
        return oldOffenceList;
    }

    public void setOldOffenceList(List<OffencesDobj> oldOffenceList) {
        this.oldOffenceList = oldOffenceList;
    }

    public boolean isShowDocumentImpoundPenal() {
        return showDocumentImpoundPenal;
    }

    public void setShowDocumentImpoundPenal(boolean showDocumentImpoundPenal) {
        this.showDocumentImpoundPenal = showDocumentImpoundPenal;
    }

    ;
    public boolean isShowVehicleImpoundPenal() {
        return showVehicleImpoundPenal;
    }

    public void setShowVehicleImpoundPenal(boolean showVehicleImpoundPenal) {
        this.showVehicleImpoundPenal = showVehicleImpoundPenal;
    }

    public boolean isShowRefToCourtPenal() {
        return showRefToCourtPenal;
    }

    public void setShowRefToCourtPenal(boolean showRefToCourtPenal) {
        this.showRefToCourtPenal = showRefToCourtPenal;
    }

    public List<DocTableDobj> getOldDocImpoundedList() {
        return oldDocImpoundedList;
    }

    public void setOldDocImpoundedList(List<DocTableDobj> oldDocImpoundedList) {
        this.oldDocImpoundedList = oldDocImpoundedList;
    }

    public boolean isDocumentImpoundCheckBox() {
        return documentImpoundCheckBox;
    }

    public void setDocumentImpoundCheckBox(boolean documentImpoundCheckBox) {
        this.documentImpoundCheckBox = documentImpoundCheckBox;
    }

    public boolean isVehicleImpoundCheckBox() {
        return vehicleImpoundCheckBox;
    }

    public void setVehicleImpoundCheckBox(boolean vehicleImpoundCheckBox) {
        this.vehicleImpoundCheckBox = vehicleImpoundCheckBox;
    }

    public boolean isReferTOCourtCheckBox() {
        return referTOCourtCheckBox;
    }

    public void setReferTOCourtCheckBox(boolean referTOCourtCheckBox) {
        this.referTOCourtCheckBox = referTOCourtCheckBox;
    }

    @Override
    public List<ComparisonBean> compareChanges() {

        if (challanDobjPrv == null) {
            return compBeanList;
        }
        compBeanList.clear();

        Compare("Challan Number", challanDobjPrv.getChallanNo(), getChallanNo(), compBeanList);
        Compare("Challan Officer ", ServerUtil.getLableFromSelectedListToShow(getOfficer(), challanDobjPrv.getChalOff()), ServerUtil.getLableFromSelectedListToShow(getOfficer(), getOfficerNo()), compBeanList);
        Compare("NCCR Number", challanDobjPrv.getNCCRNo(), getNccrNo(), compBeanList);
        Compare("Challan Place", challanDobjPrv.getChallanPlace(), getChallanPlace(), compBeanList);
        Compare("Challan Date", DateUtils.parseDate(challanDobjPrv.getChallanDt()), DateUtils.parseDate(getChallanDateCal()), compBeanList);
        Compare("Challan Time", challanDobjPrv.getChallanTime(), getChallanTime(), compBeanList);
        Compare("Vehicle No Series Part", challanDobjPrv.getVehno1(), getTfVehno1(), compBeanList);
        Compare("Vehicle No No Part", challanDobjPrv.getVehno2(), getTfVehno2(), compBeanList);
        Compare("Chassis No", challanDobjPrv.getChasiNo(), getChasiNo(), compBeanList);
        Compare("Vehicle State ", ServerUtil.getLableFromSelectedListToShow(getStateCode(), challanDobjPrv.getState()), ServerUtil.getLableFromSelectedListToShow(getStateCode(), getState()), compBeanList);
        Compare("Vehicle Rto ", ServerUtil.getLableFromSelectedListToShow(getRtoCode(), challanDobjPrv.getDistrict()), ServerUtil.getLableFromSelectedListToShow(getRtoCode(), getDistrict()), compBeanList);
        Compare("Fuel ", ServerUtil.getLableFromSelectedListToShow(getFuelList(), challanDobjPrv.getFuel()), ServerUtil.getLableFromSelectedListToShow(getFuelList(), getFuel()), compBeanList);
        Compare("Owner Name", challanDobjPrv.getOwnerName(), getOwnerNAme(), compBeanList);
        Compare("Vehicle Class ", ServerUtil.getLableFromSelectedListToShow(getVehclass(), challanDobjPrv.getVhClass()), ServerUtil.getLableFromSelectedListToShow(getVehclass(), getVhClass()), compBeanList);
        Compare("Color", challanDobjPrv.getColor(), getColor(), compBeanList);
        Compare("laden Weight", challanDobjPrv.getLadenWt(), getLadenWeight(), compBeanList);
        Compare("Seating Capacity", challanDobjPrv.getSeatCap(), getSeatCap(), compBeanList);
        Compare("Standing Capacity", challanDobjPrv.getStandCap(), getStandCap(), compBeanList);
        Compare("Sleeper Capacity", challanDobjPrv.getSleeperCap(), getSleeperCap(), compBeanList);
        Compare("Vehicle Seize No", challanDobjPrv.getSezNo(), getSezNo(), compBeanList);
        Compare("Vehicle Impound Date", DateUtils.parseDate(challanDobjPrv.getVehImpdDate()), DateUtils.parseDate(getVehImpdDate()), compBeanList);
        Compare("Vehicle Impound Place", challanDobjPrv.getVehImpndPlace(), getImpndPlace(), compBeanList);
        Compare("Contact Officer", challanDobjPrv.getContactOfficer(), getContactOfficer(), compBeanList);
        Compare("Sieze Police Station", challanDobjPrv.getSezDistrict(), getSiezeDistrictName(), compBeanList);
        Compare("Sieze District", challanDobjPrv.getSezPoliceStation(), getSiezePoliceStationName(), compBeanList);
        Compare("Impound Remarks", challanDobjPrv.getImpoundRemarks(), getImpoundRemarks(), compBeanList);
        Compare("Accused Remarks", challanDobjPrv.getAccusedRemarks(), getAccusedRemarks(), compBeanList);
        if (!("-1".equals(getCourtName()))) {
            Compare("Court Name", ServerUtil.getLableFromSelectedListToShow(getCourtCode(), challanDobjPrv.getCourtName()), ServerUtil.getLableFromSelectedListToShow(getCourtCode(), getCourtName()), compBeanList);
            Compare("Hearing Date", DateUtils.parseDate(challanDobjPrv.getHeaaringDt()), DateUtils.parseDate(getHeaaringDt()), compBeanList);
        }
        CompareWitnessList("Witness List", oldWitnessDetailsList, getWitnessDetailsList(), compBeanList);
        CompareAccusedList("Accused List", oldaccused, getAccusedDetails(), compBeanList);
        CompareOffenceList("Offence List", oldOffenceList, getOffenceDetails(), compBeanList);
        CompareDocumentImpounded("Document List", oldDocImpoundedList, getDocImpounded(), compBeanList);
        Compare("Compounding Fee Receipt NO", challanDobjPrv.getRcieptNoInCompFee(), getRcieptNoInCompFee(), compBeanList);
        Compare("Coming From", challanDobjPrv.getCommingFrom(), getCommingFrom(), compBeanList);
        Compare("Going To", challanDobjPrv.getGoingTo(), getGoingTo(), compBeanList);
        return compBeanList;
    }

    @Override
    public String saveAndMoveFile() {
        String actionCode = String.valueOf(appl_details.getCurrent_action_cd());
        String returnLocation = "";
        SaveChallanImpl impl = null;
        try {
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());
            SaveChallanDobj dobj1 = new SaveChallanDobj();
            flag = checkChallanData(dobj1);
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)) {
                if (flag) {
                    if ((actionCode == null ? (String.valueOf(TableConstants.VM_ROLE_ENFORCEMENT_VERIFY)) == null : actionCode.equals(String.valueOf(TableConstants.VM_ROLE_ENFORCEMENT_VERIFY))) || (actionCode == null ? (String.valueOf(TableConstants.VM_ROLE_ENFORCEMENT_APPROVE)) == null : actionCode.equals(String.valueOf(TableConstants.VM_ROLE_ENFORCEMENT_APPROVE)))) {

                        impl = new SaveChallanImpl();
                        impl.movetoapprove(actionCode, status, ComparisonBeanImpl.changedDataContents(compareChanges()), accusedDetails, docImpounded, offenceDetails, ListForCompoundigFee, dobj1, challanDobjPrv, witnessDetailsList, permanentRCPTNO);
                        returnLocation = "seatwork";
                    }
                }
            }
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {
                status.setPrev_action_cd_selected(app_disapp_dobj.getPre_action_code());
                impl = new SaveChallanImpl();
                impl.reback(actionCode, status, ComparisonBeanImpl.changedDataContents(compareChanges()), accusedDetails, docImpounded, offenceDetails, ListForCompoundigFee, dobj1, challanDobjPrv, witnessDetailsList);
                returnLocation = "seatwork";
            }
        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
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

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList;
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return prevChangedDataList;
    }

    public OffencesDobj getOffenceDobj() {
        return offenceDobj;
    }

    public void setOffenceDobj(OffencesDobj offenceDobj) {
        this.offenceDobj = offenceDobj;
    }

    public String getShowdate() {
        return showdate;
    }

    public void setShowdate(String showdate) {
        this.showdate = showdate;
    }

    public boolean isShowWitnessDetailsPenal() {
        return showWitnessDetailsPenal;
    }

    public void setShowWitnessDetailsPenal(boolean showWitnessDetailsPenal) {
        this.showWitnessDetailsPenal = showWitnessDetailsPenal;
    }

    public boolean isWitnessDetailsCheckBox() {
        return witnessDetailsCheckBox;
    }

    public void setWitnessDetailsCheckBox(boolean witnessDetailsCheckBox) {
        this.witnessDetailsCheckBox = witnessDetailsCheckBox;
    }

    public long getWitnessMobileNo() {
        return witnessMobileNo;
    }

    public void setWitnessMobileNo(long witnessMobileNo) {
        this.witnessMobileNo = witnessMobileNo;
    }

    public ReceiptMasterBean getRcptBean() {
        return rcptBean;
    }

    public void setRcptBean(ReceiptMasterBean rcptBean) {
        this.rcptBean = rcptBean;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getIsDocumentImpound() {
        return isDocumentImpound;
    }

    public void setIsDocumentImpound(String isDocumentImpound) {
        this.isDocumentImpound = isDocumentImpound;
    }

    public String getIsvehicleImpound() {
        return isvehicleImpound;
    }

    public void setIsvehicleImpound(String isvehicleImpound) {
        this.isvehicleImpound = isvehicleImpound;
    }

    public boolean isCbMissingVehicleNo() {
        return cbMissingVehicleNo;
    }

    public void setCbMissingVehicleNo(boolean cbMissingVehicleNo) {
        this.cbMissingVehicleNo = cbMissingVehicleNo;
    }

    public boolean isAdvancedCompFeeDisabled() {
        return advancedCompFeeDisabled;
    }

    public void setAdvancedCompFeeDisabled(boolean advancedCompFeeDisabled) {
        this.advancedCompFeeDisabled = advancedCompFeeDisabled;
    }

    public boolean isRecieptNoDisabled() {
        return recieptNoDisabled;
    }

    public void setRecieptNoDisabled(boolean recieptNoDisabled) {
        this.recieptNoDisabled = recieptNoDisabled;
    }

    public boolean isRecieptDateDisabled() {
        return recieptDateDisabled;
    }

    public void setRecieptDateDisabled(boolean recieptDateDisabled) {
        this.recieptDateDisabled = recieptDateDisabled;
    }

    public boolean isCbxTaxPayOnRoadDisabled() {
        return cbxTaxPayOnRoadDisabled;
    }

    public void setCbxTaxPayOnRoadDisabled(boolean cbxTaxPayOnRoadDisabled) {
        this.cbxTaxPayOnRoadDisabled = cbxTaxPayOnRoadDisabled;
    }

    public String getPermanentRCPTNO() {
        return permanentRCPTNO;
    }

    public void setPermanentRCPTNO(String permanentRCPTNO) {
        this.permanentRCPTNO = permanentRCPTNO;
    }

    public List<SaveChallanDobj> getPreviousChallanDetailsList() {
        return previousChallanDetailsList;
    }

    public void setPreviousChallanDetailsList(List<SaveChallanDobj> previousChallanDetailsList) {
        this.previousChallanDetailsList = previousChallanDetailsList;
    }

    /**
     * @return the vehicleChallanDetailsButton
     */
    public Boolean getVehicleChallanDetailsButton() {
        return vehicleChallanDetailsButton;
    }

    /**
     * @param vehicleChallanDetailsButton the vehicleChallanDetailsButton to set
     */
    public void setVehicleChallanDetailsButton(Boolean vehicleChallanDetailsButton) {
        this.vehicleChallanDetailsButton = vehicleChallanDetailsButton;
    }

    public PermitCheckDetailsBean getPmtCheckDtsl() {
        return pmtCheckDtsl;
    }

    public void setPmtCheckDtsl(PermitCheckDetailsBean pmtCheckDtsl) {
        this.pmtCheckDtsl = pmtCheckDtsl;
    }

    /**
     * @return the cbSaveByChassisNo
     */
    public boolean isCbSaveByChassisNo() {
        return cbSaveByChassisNo;
    }

    /**
     * @param cbSaveByChassisNo the cbSaveByChassisNo to set
     */
    public void setCbSaveByChassisNo(boolean cbSaveByChassisNo) {
        this.cbSaveByChassisNo = cbSaveByChassisNo;
    }

    /**
     * @return the witnessDetailsList
     */
    public List<WitnessdetailDobj> getWitnessDetailsList() {
        return witnessDetailsList;
    }

    /**
     * @param witnessDetailsList the witnessDetailsList to set
     */
    public void setWitnessDetailsList(List<WitnessdetailDobj> witnessDetailsList) {
        this.witnessDetailsList = witnessDetailsList;
    }

    /**
     * @return the siezePoliceStationName
     */
    public String getSiezePoliceStationName() {
        return siezePoliceStationName;
    }

    /**
     * @param siezePoliceStationName the siezePoliceStationName to set
     */
    public void setSiezePoliceStationName(String siezePoliceStationName) {
        this.siezePoliceStationName = siezePoliceStationName;
    }

    /**
     * @return the witnessPoliceStationName
     */
    public String getWitnessPoliceStationName() {
        return witnessPoliceStationName;
    }

    /**
     * @param witnessPoliceStationName the witnessPoliceStationName to set
     */
    public void setWitnessPoliceStationName(String witnessPoliceStationName) {
        this.witnessPoliceStationName = witnessPoliceStationName;
    }

    /**
     * @return the siezeDistrictName
     */
    public String getSiezeDistrictName() {
        return siezeDistrictName;
    }

    /**
     * @param siezeDistrictName the siezeDistrictName to set
     */
    public void setSiezeDistrictName(String siezeDistrictName) {
        this.siezeDistrictName = siezeDistrictName;
    }

    /**
     * @return the accuesdPoliceSation
     */
    public String getAccuesdPoliceSation() {
        return accuesdPoliceSation;
    }

    /**
     * @param accuesdPoliceSation the accuesdPoliceSation to set
     */
    public void setAccuesdPoliceSation(String accuesdPoliceSation) {
        this.accuesdPoliceSation = accuesdPoliceSation;
    }

    /**
     * @return the accuesdCity
     */
    public String getAccuesdCity() {
        return accuesdCity;
    }

    /**
     * @param accuesdCity the accuesdCity to set
     */
    public void setAccuesdCity(String accuesdCity) {
        this.accuesdCity = accuesdCity;
    }

    /**
     * @return the accuesdPincode
     */
    public int getAccuesdPincode() {
        return accuesdPincode;
    }

    /**
     * @param accuesdPincode the accuesdPincode to set
     */
    public void setAccuesdPincode(int accuesdPincode) {
        this.accuesdPincode = accuesdPincode;
    }

    /**
     * @return the accuesdFlag
     */
    public String getAccuesdFlag() {
        return accuesdFlag;
    }

    /**
     * @param accuesdFlag the accuesdFlag to set
     */
    public void setAccuesdFlag(String accuesdFlag) {
        this.accuesdFlag = accuesdFlag;
    }

    /**
     * @return the oldWitnessDetailsList
     */
    public List<WitnessdetailDobj> getOldWitnessDetailsList() {
        return oldWitnessDetailsList;
    }

    /**
     * @param oldWitnessDetailsList the oldWitnessDetailsList to set
     */
    public void setOldWitnessDetailsList(List<WitnessdetailDobj> oldWitnessDetailsList) {
        this.oldWitnessDetailsList = oldWitnessDetailsList;
    }

    public boolean isAddAccusedDetails() {
        return addAccusedDetails;
    }

    public void setAddAccusedDetails(boolean addAccusedDetails) {
        this.addAccusedDetails = addAccusedDetails;
    }

    public boolean isShowAccusedDialog() {
        return showAccusedDialog;
    }

    public void setShowAccusedDialog(boolean showAccusedDialog) {
        this.showAccusedDialog = showAccusedDialog;
    }

    public boolean isShowDocumentTable() {
        return showDocumentTable;
    }

    public void setShowDocumentTable(boolean showDocumentTable) {
        this.showDocumentTable = showDocumentTable;
    }

    public boolean isShowaccusedDataTable() {
        return showaccusedDataTable;
    }

    public void setShowaccusedDataTable(boolean showaccusedDataTable) {
        this.showaccusedDataTable = showaccusedDataTable;
    }

    /**
     * @return the penalAccessPassanger
     */
    public boolean isPenalAccessPassanger() {
        return penalAccessPassanger;
    }

    /**
     * @param penalAccessPassanger the penalAccessPassanger to set
     */
    public void setPenalAccessPassanger(boolean penalAccessPassanger) {
        this.penalAccessPassanger = penalAccessPassanger;
    }

    /**
     * @return the actualPassangerWeight
     */
    public String getActualPassangerWeight() {
        return actualPassangerWeight;
    }

    /**
     * @param actualPassangerWeight the actualPassangerWeight to set
     */
    public void setActualPassangerWeight(String actualPassangerWeight) {
        this.actualPassangerWeight = actualPassangerWeight;
    }

    /**
     * @return the overLoadPassangerWeight
     */
    public String getOverLoadPassangerWeight() {
        return overLoadPassangerWeight;
    }

    /**
     * @param overLoadPassangerWeight the overLoadPassangerWeight to set
     */
    public void setOverLoadPassangerWeight(String overLoadPassangerWeight) {
        this.overLoadPassangerWeight = overLoadPassangerWeight;
    }

    /**
     * @return the passangerCompoundingFee
     */
    public String getPassangerCompoundingFee() {
        return passangerCompoundingFee;
    }

    /**
     * @param passangerCompoundingFee the passangerCompoundingFee to set
     */
    public void setPassangerCompoundingFee(String passangerCompoundingFee) {
        this.passangerCompoundingFee = passangerCompoundingFee;
    }

    public boolean isShowWitnessPanel() {
        return showWitnessPanel;
    }

    public void setShowWitnessPanel(boolean showWitnessPanel) {
        this.showWitnessPanel = showWitnessPanel;
    }

    public boolean isCbxAddOffenceDetails() {
        return cbxAddOffenceDetails;
    }

    public void setCbxAddOffenceDetails(boolean cbxAddOffenceDetails) {
        this.cbxAddOffenceDetails = cbxAddOffenceDetails;
    }

    public boolean isShowOffenceDetailPanel() {
        return showOffenceDetailPanel;
    }

    public void setShowOffenceDetailPanel(boolean showOffenceDetailPanel) {
        this.showOffenceDetailPanel = showOffenceDetailPanel;
    }

    /**
     * @return the disablePrintButton
     */
    public boolean isDisablePrintButton() {
        return disablePrintButton;
    }

    /**
     * @param disablePrintButton the disablePrintButton to set
     */
    public void setDisablePrintButton(boolean disablePrintButton) {
        this.disablePrintButton = disablePrintButton;
    }

    /**
     * @return the disableResetButton
     */
    public boolean isDisableResetButton() {
        return disableResetButton;
    }

    /**
     * @param disableResetButton the disableResetButton to set
     */
    public void setDisableResetButton(boolean disableResetButton) {
        this.disableResetButton = disableResetButton;
    }

    /**
     * @return the otherDocumentPanel
     */
    public boolean isOtherDocumentPanel() {
        return otherDocumentPanel;
    }

    /**
     * @param otherDocumentPanel the otherDocumentPanel to set
     */
    public void setOtherDocumentPanel(boolean otherDocumentPanel) {
        this.otherDocumentPanel = otherDocumentPanel;
    }

    /**
     * @return the docName
     */
    public String getDocName() {
        return docName;
    }

    /**
     * @param docName the docName to set
     */
    public void setDocName(String docName) {
        this.docName = docName;
    }

    /**
     * @return the addOffenceManuallyPanel
     */
    public boolean isAddOffenceManuallyPanel() {
        return addOffenceManuallyPanel;
    }

    /**
     * @param addOffenceManuallyPanel the addOffenceManuallyPanel to set
     */
    public void setAddOffenceManuallyPanel(boolean addOffenceManuallyPanel) {
        this.addOffenceManuallyPanel = addOffenceManuallyPanel;
    }

    /**
     * @return the showOffenceManuallyPanel
     */
    public boolean isShowOffenceManuallyPanel() {
        return showOffenceManuallyPanel;
    }

    /**
     * @param showOffenceManuallyPanel the showOffenceManuallyPanel to set
     */
    public void setShowOffenceManuallyPanel(boolean showOffenceManuallyPanel) {
        this.showOffenceManuallyPanel = showOffenceManuallyPanel;
    }

    /**
     * @return the challanDetailsList
     */
    public List<SaveChallanDobj> getChallanDetailsList() {
        return challanDetailsList;
    }

    /**
     * @param challanDetailsList the challanDetailsList to set
     */
    public void setChallanDetailsList(List<SaveChallanDobj> challanDetailsList) {
        this.challanDetailsList = challanDetailsList;
    }

    /**
     * @return the selectedRow
     */
    public boolean isSelectedRow() {
        return selectedRow;
    }

    /**
     * @param selectedRow the selectedRow to set
     */
    public void setSelectedRow(boolean selectedRow) {
        this.selectedRow = selectedRow;
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

    /**
     * @return the showMinusButton
     */
    public boolean isShowMinusButton() {
        return showMinusButton;
    }

    /**
     * @param showMinusButton the showMinusButton to set
     */
    public void setShowMinusButton(boolean showMinusButton) {
        this.showMinusButton = showMinusButton;
    }

    /**
     * @return the showVehicleInfoPanel
     */
    public boolean isShowVehicleInfoPanel() {
        return showVehicleInfoPanel;
    }

    /**
     * @param showVehicleInfoPanel the showVehicleInfoPanel to set
     */
    public void setShowVehicleInfoPanel(boolean showVehicleInfoPanel) {
        this.showVehicleInfoPanel = showVehicleInfoPanel;
    }

    /**
     * @return the showPlusButton
     */
    public boolean isShowPlusButton() {
        return showPlusButton;
    }

    /**
     * @param showPlusButton the showPlusButton to set
     */
    public void setShowPlusButton(boolean showPlusButton) {
        this.showPlusButton = showPlusButton;
    }

    /**
     * @return the carryGoods
     */
    public String getCarryGoods() {
        return carryGoods;
    }

    /**
     * @param carryGoods the carryGoods to set
     */
    public void setCarryGoods(String carryGoods) {
        this.carryGoods = carryGoods;
    }

    /**
     * @return the carryGoodsList
     */
    public List getCarryGoodsList() {
        return carryGoodsList;
    }

    /**
     * @param carryGoodsList the carryGoodsList to set
     */
    public void setCarryGoodsList(List carryGoodsList) {
        this.carryGoodsList = carryGoodsList;
    }

    public boolean isNccr_no_exist() {
        return nccr_no_exist;
    }

    public void setNccr_no_exist(boolean nccr_no_exist) {
        this.nccr_no_exist = nccr_no_exist;
    }

    public String getVahanMessages() {
        return vahanMessages;
    }

    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }

    public ChallanConfigrationDobj getConfig_dobj() {
        return config_dobj;
    }

    public void setConfig_dobj(ChallanConfigrationDobj config_dobj) {
        this.config_dobj = config_dobj;
    }

    public String getCommingFrom() {
        return commingFrom;
    }

    public void setCommingFrom(String commingFrom) {
        this.commingFrom = commingFrom;
    }

    public String getGoingTo() {
        return goingTo;
    }

    public void setGoingTo(String goingTo) {
        this.goingTo = goingTo;
    }

    public boolean isOverloadVehicle() {
        return overloadVehicle;
    }

    public void setOverloadVehicle(boolean overloadVehicle) {
        this.overloadVehicle = overloadVehicle;
    }

    public boolean isOverAccessPassanger() {
        return overAccessPassanger;
    }

    public void setOverAccessPassanger(boolean overAccessPassanger) {
        this.overAccessPassanger = overAccessPassanger;
    }

    public boolean isDisableOwnerDetails() {
        return disableOwnerDetails;
    }

    public void setDisableOwnerDetails(boolean disableOwnerDetails) {
        this.disableOwnerDetails = disableOwnerDetails;
    }

    public boolean isRenderAtApproval() {
        return renderAtApproval;
    }

    public void setRenderAtApproval(boolean renderAtApproval) {
        this.renderAtApproval = renderAtApproval;
    }

    /**
     * @return the SettleAtSpot
     */
    public boolean isSettleAtSpot() {
        return SettleAtSpot;
    }

    /**
     * @param SettleAtSpot the SettleAtSpot to set
     */
    public void setSettleAtSpot(boolean SettleAtSpot) {
        this.SettleAtSpot = SettleAtSpot;
    }

    /**
     * @return the renderedSettleAtSpot
     */
    public boolean isRenderedSettleAtSpot() {
        return renderedSettleAtSpot;
    }

    /**
     * @param renderedSettleAtSpot the renderedSettleAtSpot to set
     */
    public void setRenderedSettleAtSpot(boolean renderedSettleAtSpot) {
        this.renderedSettleAtSpot = renderedSettleAtSpot;
    }

    public boolean isCheckAdvanceCompFee() {
        return checkAdvanceCompFee;
    }

    public void setCheckAdvanceCompFee(boolean checkAdvanceCompFee) {
        this.checkAdvanceCompFee = checkAdvanceCompFee;
    }
}
