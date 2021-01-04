package nic.vahan.form.bean.eChallan;

import java.io.Serializable;
import java.text.ParseException;
import nic.vahan.form.impl.eChallan.CompoundingOfficeImpl;
import nic.vahan.form.dobj.eChallan.DocImpoundDobj;
import nic.vahan.form.dobj.eChallan.SaveChallanDobj;
import nic.vahan.form.dobj.eChallan.CompoundingInOfficeDobj;
import nic.vahan.form.dobj.eChallan.TaxDetailsDobj;
import nic.vahan.form.dobj.eChallan.TaxSettlementDobj;
import nic.vahan.form.dobj.eChallan.VehcleOffenceDobj;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.eChallan.AccusedDetailsDobj;
import nic.vahan.form.dobj.eChallan.ChallanConfigrationDobj;
import nic.vahan.form.dobj.eChallan.ChallanHoldDobj;
import nic.vahan.form.dobj.eChallan.CompoundingFeeDobj;
import nic.vahan.form.dobj.eChallan.OffenceReferDetailsDobj;
import nic.vahan.form.dobj.eChallan.OffencesDobj;
import nic.vahan.form.dobj.eChallan.WitnessdetailDobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.eChallan.ChallanUtil;
import nic.vahan.server.CommonUtils;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "settlement")
@ViewScoped
public class CompoundingInOfficeBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static Logger LOGGER = Logger.getLogger(CompoundingInOfficeBean.class);
    private String bookNo;
    private String challanNo;
    private String applicationNo;
    CompoundingInOfficeDobj dobjSettlement = null;
    CompoundingInOfficeDobj dobjSettlementPrevious = null;
    SaveChallanDobj savedobj = null;
    CompoundingOfficeImpl settleDao = new CompoundingOfficeImpl();
    private List accusedList = new ArrayList();
    private String accusedCategory;
    private String regn_no;
    private String ownerName;
    private String chasiNo;
    private String vhClass;
    private String accusedName;
    private String standCap;
    private String seatCap;
    private String sleepCap;
    private String fuel;
    private String challanPlace;
    private String challanOfficer;
    private String color;
    private String ladenWt;
    private String stateCd;
    private String rtoCd;
    private String ncCrNo;
    private String challanDt;
    private String challanTime;
    private List<VehcleOffenceDobj> vehOffenceList = new ArrayList<>();
    private List<VehcleOffenceDobj> vehPrevOffenceList = new ArrayList<>();
    private String bookingFee;
    private String settleFee;
    private String miscFee = "0";
    private List<TaxDetailsDobj> taxDetailList = new ArrayList<>();
    private String taxFrom;
    private String taxUpto;
    private String settleCompTax = "0";
    private String settleCompPenalty = "0";
    private String settleCompInterest = "0";
    private List<TaxSettlementDobj> settleTaxDetail = new ArrayList<>();
    private int count = 0;
    private List<DocImpoundDobj> docImndList = new ArrayList<DocImpoundDobj>();
    private String courtName;
    private String hearDate;
    private String compoundFee = "0";
    private String totalFee = "0";
    private String impoundDate;
    private String impoundPlace;
    private String seizureNo;
    private String contactOfficer;
    private List taxTypeList = new ArrayList();
    private String taxType;
    private String sattelmentAmount = "0";
    boolean disableSaveButton;
    boolean taxDetailPenalVisiblity;
    boolean sattelmentTaxDetailPenalVisiblity;
    boolean showApproveDisApprovePanelForSave;
    boolean showPanelForSave;
    boolean cbxFeePaidAtCOurt;
    private boolean cbxAuthorties;
    boolean showFeePaidAtCourtPanel;
    private boolean showFeePaidAtAuthorty;
    private String courtRecieptNo;
    private Date courtRecieptDate;
    private String courtPaidAmount;
    private String CourtDecision;
    private Date maxdate = new Date();
    private String minimumOffencePenalty = "100";
    VehcleOffenceDobj vehOffDobjPrevious = null;
    private int balanceFee = 0;
    private boolean hidefeepanel;
    private boolean hideMissfeepanel;
    private boolean hidetaxsettlementpanel;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private String reasonOfMiscellaneousFees;
    boolean hidetaxListPenal;
    private Boolean showOffenceAddPanel;
    private Map<String, Object> accusedType;
    private Map<String, Object> Offence;
    private String offenceCode;
    private List<OffencesDobj> offenceDetails = new ArrayList<>();
    private String compFee;
    private String advcompFee;
    private boolean disableChangeButton = true;
    private boolean disablePayAtCourt = false;
    private boolean disablePayAtAuthority = false;
    private List<WitnessdetailDobj> witnessDetails = new ArrayList<>();
    private List<AccusedDetailsDobj> accusedDetails = new ArrayList<>();
    private boolean disableOwnerDetails;
    private List stateCode = new ArrayList();
    private List rtoCode = new ArrayList();
    private List fuelList = new ArrayList();
    private List vehclass = new ArrayList();
    private boolean referToCourte;
    private String authortyOffence;
    private String authorties;
    private String authortyDecision;
    private String authpenalty;
    private String authortySection;
    private Date authortyReferDate;
    private Date authDecisionDate;
    private Map<String, Object> authOffenceList = new LinkedHashMap<String, Object>();
    private Map<String, Object> authortyList = new LinkedHashMap<String, Object>();
    private List<OffenceReferDetailsDobj> addAuthDetails = new ArrayList();
    private List<OffenceReferDetailsDobj> referAuthorties = new ArrayList();
    private List accusedWiseOffenceList = new ArrayList();
    private boolean witnessPanel;
    private boolean accusedPanel;
    private boolean documentPanel;
    private ChallanHoldDobj dobj = null;
    private ArrayList statusList = new ArrayList();
    private boolean challanHoldEnable = false;
    private String coming_from;
    private String going_to;
    private boolean disable_coming_from;
    private boolean disable_going_to;
    private SessionVariables sessionVariables = null;
    List<String> checkTaxType = new ArrayList<>();

    public CompoundingInOfficeBean() throws Exception {
        sessionVariables = new SessionVariables();

        if (sessionVariables == null || nic.vahan.server.CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
            return;
        }
        reset();
        intilize();
        dobj = new ChallanHoldDobj();
        statusList.add("ACTIVE");
        statusList.add("HOLD");

    }

    public void intilize() {
        try {

            boolean isHold = settleDao.isChallanHold(appl_details.getAppl_no());
            if (isHold) {
                setChallanHoldEnable(true);
            } else {
                setChallanHoldEnable(false);
            }
            String[][] data = MasterTableFiller.masterTables.TM_OFFICE.getData();
            rtoCode.add(new SelectItem("-1", "---Select---"));
            for (int i = 0; i < data.length; i++) {
                rtoCode.add(new SelectItem(data[i][0], data[i][1]));
            }
            data = MasterTableFiller.masterTables.TM_STATE.getData();
            stateCode.add(new SelectItem("-1", "---Select---"));
            for (int i = 0; i < data.length; i++) {
                stateCode.add(new SelectItem(data[i][0], data[i][1]));
            }
            data = MasterTableFiller.masterTables.VM_FUEL.getData();
            fuelList.add(new SelectItem("-1", "---Select---"));
            for (String[] data1 : data) {
                fuelList.add(new SelectItem(data1[0], data1[1]));
            }
            data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
            vehclass.add(new SelectItem("-1", "---Select---"));
            for (String[] data1 : data) {
                vehclass.add(new SelectItem(data1[0], data1[1]));
            }
            String ApplNo = String.valueOf(appl_details.getAppl_no());

            setHidefeepanel(true);
            taxTypeList.clear();
            taxTypeList.add(new SelectItem("-1", "Select Tax Type"));
            taxTypeList.add(new SelectItem("58", "58:Road Tax"));
            taxTypeList.add(new SelectItem("59", "59:Special Tax"));
            setTaxTypeList(taxTypeList);
            disableSaveButton = true;
            String actionCode = String.valueOf(appl_details.getCurrent_action_cd());
            if (Integer.parseInt(actionCode) == TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_APPROVE) {
                getSettlementTabData(TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_APPROVE);
                setShowApproveDisApprovePanelForSave(true);
                setShowPanelForSave(false);
            } else if (Integer.parseInt(actionCode) == TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_VERIFY) {

                getSettlementTabData(TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_VERIFY);
                setShowApproveDisApprovePanelForSave(true);
                setShowPanelForSave(false);
            } else if (Integer.parseInt(actionCode) == TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_ENTRY) {

                getSettlementTabData(TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_ENTRY);
                setShowApproveDisApprovePanelForSave(false);
                setShowFeePaidAtCourtPanel(false);
                setShowPanelForSave(true);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }

    }

    public void getSettlementTabData(int actionCode) throws VahanException {
        ChallanUtil challanUtil = new ChallanUtil();
        String ApplNo = "";
        try {
            ApplNo = String.valueOf(appl_details.getAppl_no());
            if (CommonUtils.isNullOrBlank(ApplNo)) {
                JSFUtils.setFacesMessage("Please Enter The Application No", "message", JSFUtils.FATAL);
                return;
            }
            dobjSettlement = settleDao.getSettleMentData(ApplNo, actionCode);
            if (dobjSettlement != null) {
                dobjSettlementPrevious = (CompoundingInOfficeDobj) dobjSettlement.clone();
                String madeChasiNo = ChallanUtil.getSubstringFromRight(chasiNo, 10);
                //check if vehicle is challaned via chassis number( vehicle is not Registered)
                if (dobjSettlement.getOwnerName() == null || dobjSettlement.getOwnerName().equals("")) {
                    // if (regn_no.equals(madeChasiNo)) {
                    setApplicationNo(ApplNo);
                    setRegn_no(dobjSettlement.getRegnNo());
                    setChallanPlace(dobjSettlement.getChallanPlace());
                    setChasiNo(dobjSettlement.getChasiNo());
                    setChallanDt(dobjSettlement.getChallanDt());
                    setChallanTime(dobjSettlement.getChallanTime());
                    setVhClass(dobjSettlement.getVhClass());
                    setNcCrNo(dobjSettlement.getNcCrNo());
                    setAccusedName(dobjSettlement.getAccusedName());
                    setChallanOfficer(dobjSettlement.getChallanOfficer().toUpperCase());
                    setComing_from(dobjSettlement.getComing_from());
                    setGoing_to(dobjSettlement.getGoing_to());
                    setDisableOwnerDetails(true);
                    // }
                } //check if vehicle is challaned via Vehicle number(Registered vehicle)
                else if (dobjSettlement.getOwnerName() != null || !dobjSettlement.getOwnerName().equals("")) {
                    setApplicationNo(ApplNo);
                    setRegn_no(dobjSettlement.getRegnNo());
                    setOwnerName(dobjSettlement.getOwnerName());
                    setChasiNo(dobjSettlement.getChasiNo());
                    setChallanPlace(dobjSettlement.getChallanPlace());
                    setChallanDt(dobjSettlement.getChallanDt());
                    setChallanTime(dobjSettlement.getChallanTime());
                    setVhClass(dobjSettlement.getVhClass());
                    setStandCap(dobjSettlement.getStandCap());
                    setSeatCap(dobjSettlement.getSeatCap());
                    setSleepCap(dobjSettlement.getSleepCap());
                    setFuel(dobjSettlement.getFuel());
                    setColor(dobjSettlement.getColor());
                    setLadenWt(dobjSettlement.getLadenWt());
                    setStateCd(dobjSettlement.getStateCd());
                    setComing_from(dobjSettlement.getComing_from());
                    setGoing_to(dobjSettlement.getGoing_to());
                    rtoCode.clear();
                    String[][] data = MasterTableFiller.masterTables.TM_OFFICE.getData();
                    rtoCode.add(new SelectItem("-1", "---Select---"));
                    for (int i = 0; i < data.length; i++) {
                        if (getStateCd().equalsIgnoreCase(data[i][13])) {
                            rtoCode.add(new SelectItem(data[i][0], data[i][1]));
                        }
                    }
                    setRtoCd(dobjSettlement.getRtoCd());
                    setAccusedName(dobjSettlement.getAccusedName());
                    setNcCrNo(dobjSettlement.getNcCrNo());
                    setChallanOfficer(dobjSettlement.getChallanOfficer().toUpperCase());
                    setDisableOwnerDetails(true);
                }
                setReferAuthorties(settleDao.getReferAuthorty(ApplNo));
                if (referAuthorties.size() > 0) {
                    setDisablePayAtAuthority(true);
                    setDisableChangeButton(false);
                }
                if (dobjSettlement.getCourtName() != null && !"".equalsIgnoreCase(dobjSettlement.getCourtName())) {
                    setCourtName(dobjSettlement.getCourtName());
                    setDisableChangeButton(false);
                    setDisablePayAtCourt(true);
                    setHearDate(dobjSettlement.getHearDate());
                    hideMissfeepanel = false;

                } else {
                    hideMissfeepanel = true;

                }

                setCompoundFee(dobjSettlement.getCompoundFee());
                setAdvcompFee(dobjSettlement.getAdvcompfee());
                if (!CommonUtils.isNullOrBlank(dobjSettlement.getAdvcompfee()) && !"0".equalsIgnoreCase(dobjSettlement.getAdvcompfee())) {
                    setTotalFee(String.valueOf(Integer.parseInt(dobjSettlement.getCompoundFee()) - Integer.parseInt(dobjSettlement.getAdvcompfee())));
                } else if ("0".equalsIgnoreCase(dobjSettlement.getTotalFee())) {
                    setTotalFee(dobjSettlement.getCompoundFee());

                } else {
                    setTotalFee(dobjSettlement.getTotalFee());
                }
                setImpoundDate(dobjSettlement.getImpndDate());
                setImpoundPlace(dobjSettlement.getImpndPlace());
                setContactOfficer(dobjSettlement.getContactOfficer());
                setMiscFee(dobjSettlement.getMiscFee());
                setReasonOfMiscellaneousFees(dobjSettlement.getReason());
                setSeizureNo(dobjSettlement.getSeizureNo());
                dobjSettlement.setBookNo(bookNo);
                dobjSettlement.setChallanNo(challanNo);
                dobjSettlement.setVhClass(dobjSettlement.getVhClass());
                dobjSettlement.setAccuCateg(accusedCategory);
                dobjSettlement.setApplicationNo(ApplNo);
                setWitnessDetails(ChallanUtil.fetchWitnessDetails(ApplNo));
                if (witnessDetails.size() > 0) {
                    setWitnessPanel(true);
                } else {
                    setWitnessPanel(false);
                }
                setAccusedDetails(ChallanUtil.getAccusedDetails(ApplNo));
                if (accusedDetails.size() > 0) {
                    setAccusedPanel(true);
                } else {
                    setAccusedPanel(false);
                }
                Integer i = 0;
                setDocImndList(settleDao.addDocImpoundData(dobjSettlement));
                if (docImndList.size() > 0) {
                    setDocumentPanel(true);
                } else {
                    setDocumentPanel(false);
                }
                setVehOffenceList(settleDao.getvehOffence(dobjSettlement));
                setVehPrevOffenceList(settleDao.getPrviousvehOffence(dobjSettlement));
                Iterator<VehcleOffenceDobj> itroff = getVehOffenceList().iterator();
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
                    setAccusedCategory(dobj1.getAccuCatg());
                }
                setCompoundFee("" + i);

                if (actionCode == TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_ENTRY) {

                    ChallanConfigrationDobj dobj = challanUtil.getChallanConfigration(sessionVariables.getStateCodeSelected());
                    if (dobj == null) {
                        JSFUtils.setFacesMessage("There is Some Problem . Try Again Later", "message", JSFUtils.ERROR);
                        return;
                    }
                    if (dobj.isIs_tax_enable()) {
                        setHidetaxsettlementpanel(true);
                    } else {
                        setHidetaxsettlementpanel(false);
                    }
                } else {
                    setTaxDetailList(settleDao.getTaxDetails(dobjSettlement));
                    for (TaxDetailsDobj dobj : taxDetailList) {
                        if (!CommonUtils.isNullOrBlank(dobj.getCfTax())) {
                            setSettleCompTax(dobj.getCfTax());
                        }
                        if (!CommonUtils.isNullOrBlank(dobj.getCfPeanlty())) {
                            setSettleCompPenalty(dobj.getCfPeanlty());
                        }
                        if (!CommonUtils.isNullOrBlank(dobj.getCfInterest())) {
                            setSettleCompInterest(dobj.getCfInterest());
                        }
                        if (actionCode == TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_APPROVE) {
                            TaxSettlementDobj setteldobj = new TaxSettlementDobj();
                            setteldobj.setSlNo(dobj.getSlNo());
                            setteldobj.setTaxType(dobj.getTaxType());
                            setteldobj.setTaxUpto(dobj.getTaxUpto());
                            setteldobj.setTaxFrom(dobj.getTaxFrom());
                            setteldobj.setSetComTax(dobj.getCfTax());
                            setteldobj.setSetComPenalty(dobj.getCfPeanlty());
                            setteldobj.setSetComInterst(dobj.getCfInterest());
                            settleTaxDetail.add(setteldobj);
                        }
                    }
                    if (taxDetailList.size() > 0) {
                        setTaxDetailPenalVisiblity(true);
                        setSattelmentTaxDetailPenalVisiblity(true);
                        setHidetaxsettlementpanel(true);
                    } else {
                        setTaxDetailPenalVisiblity(false);
                        setSattelmentTaxDetailPenalVisiblity(false);
                        setHidetaxsettlementpanel(false);
                    }
                    for (TaxDetailsDobj dobj : taxDetailList) {
                        setTaxFrom(dobj.getTaxFrom());
                        setTaxUpto(dobj.getTaxUpto());
                        if (!("0".equals(dobjSettlement.getAdvcompfee())) && (!("".equals(dobjSettlement.getOnroadrecptno()) || dobjSettlement.getOnroadrecptno() == null))) {
                            setHidefeepanel(false);
                        } else {
                            setHidefeepanel(true);
                        }
                        if (!("".equals(dobj.getTaxRcptNo()) || dobj.getTaxRcptNo() == null) && (!("0".equals(dobj.getCfTax())))) {
                            setHidetaxsettlementpanel(false);
                        } else {
                            setHidetaxsettlementpanel(true);
                        }
                    }
                }
                if (!(CommonUtils.isNullOrBlank(dobjSettlement.getCourtRecieptNo()))) {
                    setShowFeePaidAtCourtPanel(true);
                    cbxFeePaidAtCOurt = true;
                    setCourtDecision(dobjSettlement.getCourtDecision());
                    setCourtRecieptNo(dobjSettlement.getCourtRecieptNo());
                    setCourtPaidAmount(dobjSettlement.getCourtPaidAmount());
                    setCourtRecieptDate(dobjSettlement.getCourtRecieptDate());
                } else {
                    setShowFeePaidAtCourtPanel(false);
                    setCourtDecision("");
                    setCourtRecieptNo("");
                    setCourtPaidAmount("");
                    setCourtRecieptDate(null);
                }
                setAddAuthDetails(settleDao.getAuthortyDetailsList(dobjSettlement.getApplicationNo()));
                if (addAuthDetails.size() > 0) {
                    setShowFeePaidAtAuthorty(true);
                    setDisablePayAtAuthority(true);
                    cbxAuthorties = true;
                } else {
                    setShowFeePaidAtAuthorty(false);
                    cbxAuthorties = false;
                }
                setDisableSaveButton(false);
            } else {
                JSFUtils.showMessage("Challan Number Already Settled Or Does Not Exists");
                reset();
                setDisableSaveButton(true);
                setTaxDetailPenalVisiblity(false);
                setSattelmentTaxDetailPenalVisiblity(false);
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void showRtoList() {
        String[][] data = MasterTableFiller.masterTables.TM_OFFICE.getData();
        rtoCode.add(new SelectItem("-1", "---Select---"));
        for (int i = 0; i < data.length; i++) {
            if (getStateCd().equalsIgnoreCase(data[i][13])) {
                rtoCode.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
    }

    public void changeOffenceDialog() {
        //RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('OffenceAddPanel').show()");
        try {
            setAccusedType(settleDao.getAccusedList(applicationNo));
        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void getOffenceAccordingAccuse() {
        try {
            setAccusedWiseOffenceList(settleDao.getOffenceAccusedWise(vhClass, accusedName));
        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void getPenaltyWithOffenc() {
        offenceDetails.clear();
        boolean flagPenaltyOffence = true;
        CompoundingFeeDobj compoundingFeeDobj = new CompoundingFeeDobj();
        if (("-1".equals(offenceCode)) || "-1".equals(accusedName)) {
            JSFUtils.showMessage("Plesae Select The Offence and Accuse Type");
            flagPenaltyOffence = false;
        } else if (nic.java.util.CommonUtils.isNullOrBlank(offenceCode) || nic.java.util.CommonUtils.isNullOrBlank(accusedName)) {
            JSFUtils.showMessage("Plesae fill the Vehicle Details And Accused Details ");
            flagPenaltyOffence = false;
        }
        if (!vehOffenceList.isEmpty()) {
            Iterator<VehcleOffenceDobj> itr_offence = vehOffenceList.iterator();
            while (itr_offence.hasNext()) {
                VehcleOffenceDobj offence_dobj = itr_offence.next();
                String check_offence = offence_dobj.getOffenceCd();

                if (check_offence.equals(offenceCode)) {
                    JSFUtils.showMessage("Offence Already Exists");
                    flagPenaltyOffence = false;
                }
            }
        }
        try {
            if (flagPenaltyOffence == true) {
                OffencesDobj dobj = ChallanUtil.getOffenceDetails(offenceCode, accusedName, dobjSettlement);
                offenceDetails.add(dobj);
                boolean save = settleDao.saveOffenceDetails(applicationNo, offenceDetails);
                if (save) {
                    setVehOffenceList(settleDao.getvehOffence(dobjSettlement));
                    JSFUtils.setFacesMessage("Offence Saved Successfully", "messege", JSFUtils.ERROR);
                }
                Iterator<OffencesDobj> itr = getOffenceDetails().iterator();
                int CompoundingFee = 0;
                String owenerPenalty = null;
                while (itr.hasNext()) {
                    OffencesDobj doOff = itr.next();

                    if (accusedName.equals("O")) {
                        owenerPenalty = doOff.getPenalty();
                    }
                    if (accusedName.equals("D")) {
                        owenerPenalty = doOff.getPenalty();
                    }
                    if (accusedName.equals("C")) {
                        owenerPenalty = doOff.getPenalty();
                    }
                    if (accusedName.equals("N")) {

                        owenerPenalty = doOff.getPenalty();
                    }
                    CompoundingFee = Integer.parseInt(compoundFee) + Integer.parseInt(owenerPenalty);
                    setCompoundFee("" + CompoundingFee);
                    setTotalFee(compoundFee);
                    dobjSettlement.setTotalFee(String.valueOf(Integer.parseInt(compoundFee) - Integer.parseInt(dobjSettlement.getAdvcompfee())));
                    setTotalFee(dobjSettlement.getTotalFee());
                }
                setOffenceCode("");
                setAccusedName("");

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

    public void addMiscFee() {

        if ("0".equals(miscFee) && "0".equals(compoundFee)) {
            JSFUtils.setFacesMessage("Please Enter The Miscellaneous Fee/Settlement Fee", "message", JSFUtils.FATAL);
            return;
        }
        dobjSettlement.setMiscFee(miscFee);
        int totalFee = 0;
        try {
            settleDao.addMiscellaneousFees(applicationNo, miscFee, reasonOfMiscellaneousFees);
            setVehOffenceList(settleDao.getvehOffence(dobjSettlement));
            if (!miscFee.trim().isEmpty() || !miscFee.equals("")) {
                totalFee = Integer.parseInt(miscFee) + Integer.parseInt(compoundFee);
            }
            if (!(sattelmentAmount.equals("0"))) {
                totalFee = Integer.parseInt(miscFee) + Integer.parseInt(sattelmentAmount);
            }
            setTotalFee(String.valueOf(totalFee));
        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
    // add parking fee

    public void addParkingFee() {

        if ("0".equals(miscFee) && "0".equals(compoundFee)) {
            JSFUtils.setFacesMessage("Please Enter The Miscellaneous Fee/Settlement Fee", "message", JSFUtils.FATAL);
            return;
        }
        if (reasonOfMiscellaneousFees.equals("") || reasonOfMiscellaneousFees == null) {
            JSFUtils.setFacesMessage("Please Enter The Reason", "message", JSFUtils.FATAL);
            return;
        }
        dobjSettlement.setMiscFee(miscFee);
        int totalFee = 0;
        try {
            settleDao.addMissFees(applicationNo, miscFee, reasonOfMiscellaneousFees);
            setVehOffenceList(settleDao.getvehOffence(dobjSettlement));
            if (!miscFee.trim().isEmpty() || !miscFee.equals("")) {
                totalFee = Integer.parseInt(miscFee) + Integer.parseInt(compoundFee);
            }
            if (!(sattelmentAmount.equals("0"))) {
                totalFee = Integer.parseInt(miscFee) + Integer.parseInt(sattelmentAmount);
            }
            setTotalFee(String.valueOf(totalFee));
        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void showPaidAtCourt() {
        if (cbxFeePaidAtCOurt == true) {
            setShowFeePaidAtCourtPanel(true);
        } else {
            setShowFeePaidAtCourtPanel(false);
            setCourtDecision("");
            setCourtPaidAmount("");
            setCourtRecieptDate(null);
            setCourtRecieptNo("");
        }
    }
    //method to add authority details

    public void addAuthDetail() {
        OffenceReferDetailsDobj dobj = new OffenceReferDetailsDobj();

        if (authortyOffence.equals("-1")) {
            JSFUtils.setFacesMessage("Please Select Offence ", "message", JSFUtils.FATAL);
            return;
        } else {
            dobj.setOffenceCode(authortyOffence);
            String auth_off = "";
            for (Object key : authOffenceList.keySet()) {
                if (authOffenceList.get(key).toString().equals(authortyOffence.toString())) {
                    auth_off = key.toString();
                    break;
                }
            }
            dobj.setOffenceDescr(auth_off);
        }
        if (authorties.equals("-1")) {
            JSFUtils.setFacesMessage("Please Select Authority ", "message", JSFUtils.FATAL);
            return;
        } else {

            String auth = "";
            for (Object key : authortyList.keySet()) {
                if (authortyList.get(key).toString().equals(authorties.toString())) {
                    auth = key.toString();
                    break;
                }
            }
            dobj.setAuthoritydescr(auth);
        }
        if (authortyReferDate.equals("") || authortyReferDate == null) {
            JSFUtils.setFacesMessage("Please Enter Date  ", "message", JSFUtils.FATAL);
            return;
        } else {
            dobj.setReferDate(authortyReferDate);
        }
        if (authDecisionDate.equals("") || authDecisionDate == null) {
            JSFUtils.setFacesMessage("Please Enter  Decision Date  ", "message", JSFUtils.FATAL);
            return;
        } else {
            dobj.setDecision_date(authDecisionDate);
        }
        if (authortyDecision.equals("") || authortyDecision == null) {
            JSFUtils.setFacesMessage("Please Enter  Decision Date  ", "message", JSFUtils.FATAL);
            return;
        } else {
            dobj.setAuthority_decision(authortyDecision);
        }
        for (OffenceReferDetailsDobj dobj_ref : addAuthDetails) {
            if (dobj_ref.getOffenceCode().equalsIgnoreCase(getAuthortyOffence())) {
                JSFUtils.setFacesMessage("Duplicate Record ", "message", JSFUtils.WARN);
                return;
            }
        }

        addAuthDetails.add(dobj);
        setAuthDecisionDate(null);
        setAuthorties("-1");
        setAuthortyDecision("");
        setAuthortyOffence("-1");
        setAuthortyReferDate(null);

    }
//method authority Details

    public void getAuthDetails() {
        OffenceReferDetailsDobj dobj;
        try {
            dobj = settleDao.getAuthortyDetails(applicationNo, getAuthortyOffence());
            setAuthortyReferDate(dobj.getReferDate());
            setAuthorties(dobj.getAuthorityCode());
            setAuthpenalty(dobj.getPenalty());
            setAuthortySection(dobj.getSectionDescr());
        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }

    }

    public void showAuthortyPanel() throws Exception {
        if (cbxAuthorties == true) {
            setShowFeePaidAtAuthorty(true);
            setAuthOffenceList(settleDao.getAuthortyOffence(applicationNo));
            setAuthortyList(settleDao.getAuthorty());

        } else {
            setShowFeePaidAtAuthorty(false);
        }
    }

    public void upadetTotalFeeOnTheBaisisOfCourt() {
        String totaFee = getTotalFee();
        String courtAmount = getCourtPaidAmount();
        if (CommonUtils.isNullOrBlank(courtAmount)) {
            setTotalFee(dobjSettlement.getCompoundFee());
        }
        if ("0".equals(courtAmount)) {
            setTotalFee("0");
        }
        if (!CommonUtils.isNullOrBlank(courtAmount)) {
            setTotalFee("0");
        }
    }

    public void getRowOfOffence() {
        int UpdatedtotalFee = 0;
        for (VehcleOffenceDobj dobj : getVehOffenceList()) {
            if (dobj.isSelect()) {
                dobj.setTest(true);
                balanceFee = Integer.parseInt(dobj.getPenalty()) - Integer.parseInt(minimumOffencePenalty);
                UpdatedtotalFee = Integer.parseInt(getTotalFee()) - balanceFee;
                totalFee = UpdatedtotalFee + "";
                dobj.setPenalty(minimumOffencePenalty);
                dobjSettlement.setOffencePenalty(Integer.parseInt(minimumOffencePenalty));
                setTotalFee(totalFee);
                break;
            } else {
                if (dobj.isTest()) {
                    int penaltySetter = Integer.parseInt(minimumOffencePenalty) + balanceFee;
                    dobj.setPenalty(penaltySetter + "");
                    UpdatedtotalFee = Integer.parseInt(getTotalFee()) + balanceFee;
                    totalFee = UpdatedtotalFee + "";
                    setTotalFee(totalFee);
                    dobj.setTest(false);
                }
            }
        }
    }

    public String saveSettleData() {
        boolean flag = checkValidattionForsave();
        String status;
        String refered = "N";
        boolean checkAuthStatus = false;
        String returnLocation = "";
        try {
            status = settleDao.getHoldChallanStatus(appl_details.getAppl_no());
            if (status.equals("H")) {
                JSFUtils.showMessage("This Challan is on Hold,Please Unhold the Challan.");
                flag = false;
                checkAuthStatus = settleDao.checkAuthortyCase(appl_details.getAppl_no());
            }
            if (checkAuthStatus && cbxAuthorties) {
                refered = "Y";
            }
            if (checkAuthStatus && !cbxAuthorties) {
                refered = "N";
                JSFUtils.setFacesMessage("This Case Is Refered To Authorities . PLease Fill The Authorities Details", "Message", JSFUtils.WARN);
                flag = false;
            }
            if (!checkAuthStatus && cbxAuthorties) {
                refered = "N";
                JSFUtils.setFacesMessage("This Case Is Not Refered To Authorities .", "Message", JSFUtils.WARN);
                flag = false;
            }
            String actionCode = String.valueOf(appl_details.getCurrent_action_cd());
            Status_dobj statusDobj = new Status_dobj();
            statusDobj.setRegn_no(dobjSettlement.getRegnNo());
            statusDobj.setPur_cd(TableConstants.VM_MAST_ENFORCEMENT);
            statusDobj.setAction_cd(TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_ENTRY);
            statusDobj.setFlow_slno(1);
            statusDobj.setFile_movement_slno(1);
            statusDobj.setState_cd(sessionVariables.getStateCodeSelected());
            statusDobj.setOff_cd(sessionVariables.getOffCodeSelected());
            statusDobj.setEmp_cd(0);
            statusDobj.setAppl_dt(appl_details.getAppl_dt());
            statusDobj.setAppl_no(appl_details.getAppl_no());
            statusDobj.setPur_cd(appl_details.getPur_cd());
            statusDobj.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            statusDobj.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            statusDobj.setStatus(getApp_disapp_dobj().getNew_status());
            statusDobj.setCurrent_role(appl_details.getCurrent_role());
            boolean success = false;
            if (flag) {
                success = settleDao.saveSettleData(dobjSettlement, taxDetailList, statusDobj, isCbxFeePaidAtCOurt(), actionCode, getVehOffenceList(), refered, addAuthDetails, settleTaxDetail);
                if (success) {
                    JSFUtils.showMessage("Data Saved Successfully");
                    reset();
                    returnLocation = "seatwork";
                }
            } else {
                JSFUtils.showMessage("Please Provide Proper Details .");
                returnLocation = "";
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

    private boolean checkValidattionForsave() {
        boolean flag = true;


        dobjSettlement.setStateOfVeh(stateCd);
        dobjSettlement.setRtoOfVeh(rtoCd);
        dobjSettlement.setFuel(fuel);
        dobjSettlement.setOwnerName(ownerName);
        dobjSettlement.setStandCap(standCap);
        dobjSettlement.setSeatCap(seatCap);
        dobjSettlement.setLadenWt(ladenWt);
        dobjSettlement.setSleepCap(sleepCap);
        dobjSettlement.setColor(color);
        if (applicationNo.trim().isEmpty() || applicationNo.equals("")) {
            JSFUtils.setFacesMessage("ApplicationNo Nerver Empty ", "message", JSFUtils.FATAL);
            flag = false;
        }
        if (totalFee.trim().isEmpty() || totalFee.equals("")) {
            flag = false;
        } else {
            dobjSettlement.setSettleFee(totalFee);
            dobjSettlement.setTotalFee(totalFee);
            dobjSettlement.setCompoundFee(compoundFee);
            dobjSettlement.setMiscFee(miscFee);
        }
        dobjSettlement.setCbxFeePaidAtCOurt(isCbxFeePaidAtCOurt());
        if (isCbxFeePaidAtCOurt()) {
            if (CommonUtils.isNullOrBlank(getCourtDecision())) {
                JSFUtils.setFacesMessage("Please Enter The Court Decision", "message", JSFUtils.WARN);
                flag = false;
            } else {
                dobjSettlement.setCourtDecision(getCourtDecision().toUpperCase().trim());
            }
            if (CommonUtils.isNullOrBlank(getCourtPaidAmount()) || "0".equalsIgnoreCase(getCourtPaidAmount())) {
                JSFUtils.setFacesMessage("Please Enter Amount Paid In Court", "message", JSFUtils.WARN);
                flag = false;
            } else {
                dobjSettlement.setCourtPaidAmount(getCourtPaidAmount());
            }
            if (CommonUtils.isNullOrBlank(getCourtRecieptNo())) {
                JSFUtils.setFacesMessage("Please Enter The Court Receipt No :", "message", JSFUtils.WARN);
                flag = false;
            } else {
                dobjSettlement.setCourtRecieptNo(getCourtRecieptNo().toUpperCase().trim());
            }
            if ("".equals(getCourtRecieptDate()) || getCourtRecieptDate() == null) {
                JSFUtils.setFacesMessage("Please Enter The Court Receipt Date :", "message", JSFUtils.WARN);
                flag = false;
            } else {
                dobjSettlement.setCourtRecieptDate(getCourtRecieptDate());
            }
        } else {
            // RequestContext ca = RequestContext.getCurrentInstance();
            PrimeFaces.current().executeScript("PF('cortDetailsDialog').show()");

        }
        dobjSettlement.setCbxFeePaidAtAuthority(isCbxAuthorties());
        dobjSettlement.setSettleChalTax(settleCompTax);
        dobjSettlement.setSettleChalPenalty(settleCompPenalty);
        dobjSettlement.setSettleChalInterest(settleCompInterest);
        dobjSettlement.setTaxFrom(taxFrom);
        dobjSettlement.setTaxUpto(taxUpto);
        dobjSettlement.setAccuCateg(accusedCategory);
        return flag;
    }

    public void addTaxSettlementDataTable() {
        try {

            if (getTaxType().equals("-1")) {
                JSFUtils.setFacesMessage("Please select Tax type", "messages", JSFUtils.FATAL);
                resetChallanTaxSettlement();
                return;
            }
            if (checkTaxType.contains(getTaxType())) {
                JSFUtils.setFacesMessage("Tax type is already saved", "messages", JSFUtils.FATAL);
                resetChallanTaxSettlement();
                return;
            }
            ++count;
            int slNo = count;
            TaxSettlementDobj dobjTaxSettle = new TaxSettlementDobj(String.valueOf(slNo), getTaxType(), DateUtil.convertDatePattern(this.taxFrom), DateUtil.convertDatePattern(this.taxUpto), this.settleCompPenalty,
                    this.settleCompTax, this.settleCompInterest);
            settleTaxDetail.add(dobjTaxSettle);
            checkTaxType.add(getTaxType());
            resetChallanTaxSettlement();

        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " : " + ex.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " : " + ex.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void resetChallanTaxSettlement() {
        setTaxType(null);
        setTaxFrom(null);
        setTaxUpto(null);
        setSettleCompTax("0");
        setSettleCompPenalty("0");
        setSettleCompInterest("0");
    }

    public void getDocImpndData() {
        try {
            setDocImndList(settleDao.addDocImpoundData(dobjSettlement));
        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void deleteOffence(VehcleOffenceDobj dobj) {
        try {
            settleDao.deleteAndUpdate(applicationNo, dobj);
            setVehPrevOffenceList(settleDao.getPrviousvehOffence(dobjSettlement));
            setVehOffenceList(settleDao.getvehOffence(dobjSettlement));
            String penalty = dobj.getPenalty();
            int comFee = Integer.parseInt(compoundFee) - Integer.parseInt(penalty);
            setCompoundFee("" + comFee);
            setTotalFee(compoundFee);
            vehOffenceList.remove((VehcleOffenceDobj) dobj);
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
        setBookNo("");
        setChallanNo("");
        setChallanDt("");
        setChallanOfficer("");
        setChallanOfficer("");
        setChallanPlace("");
        setChallanTime("");
        setVhClass("");
        setLadenWt("");
        setFuel("");
        setSeatCap("");
        setSettleFee("");
        setTotalFee("");
        setAccusedName("");
        setAccusedCategory("");
        setCourtName("");
        setCompoundFee("0");
        docImndList.clear();
        settleTaxDetail.clear();
        vehOffenceList.clear();
        taxDetailList.clear();
        addAuthDetails.clear();
        setMiscFee("0");
        setRtoCd("");
        setStateCd("");
        setHearDate("");
        setOwnerName("");
        setSattelmentAmount("0");
        setSettleCompInterest("0");
        setSettleCompPenalty("0");
        setSettleCompTax("0");
        setChasiNo("");
        setStandCap("");
        setNcCrNo("");
        setImpoundDate("");
        setImpoundPlace("");
        setContactOfficer("");
        setSleepCap("");
        setColor("");
        setTaxFrom("");
        setTaxUpto("");
        setTaxType("-1");
        setCourtDecision("");
        setCourtPaidAmount("");
        setCourtRecieptDate(null);
        setCourtRecieptNo("");
        setOffenceCode("-1");
        setAccusedName("-1");
        setAuthDecisionDate(null);
        setAuthorties("-1");
        setAuthortyDecision("");
        setAuthortyOffence("-1");
        setAuthortyReferDate(null);
        checkTaxType.clear();

    }

    @Override
    public String save() {
        String return_location = "";
        return_location = saveSettleData();
        return return_location;
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        if (dobjSettlementPrevious == null) {
            return compBeanList;
        }
        compBeanList.clear();
        Compare("settelment Amount", dobjSettlementPrevious.getSettleFee(), getSattelmentAmount(), (ArrayList) compBeanList);
        Compare("Miscellaneous Amount", dobjSettlementPrevious.getMiscFee(), getMiscFee(), (ArrayList) compBeanList);
        Compare("Total Amount", dobjSettlementPrevious.getCompoundFee(), getTotalFee(), (ArrayList) compBeanList);
        Compare("Sttelment Tax Amount", dobjSettlementPrevious.getSettleChalTax(), getSettleCompTax(), (ArrayList) compBeanList);
        Compare("Sttelment Penalty Amount", dobjSettlementPrevious.getSettleChalPenalty(), getSettleCompPenalty(), (ArrayList) compBeanList);
        Compare("Sttelment Intrest Amount", dobjSettlementPrevious.getSettleChalInterest(), getSettleCompInterest(), (ArrayList) compBeanList);
        Compare("Court Receipt No", dobjSettlementPrevious.getCourtRecieptNo(), getCourtRecieptNo(), (ArrayList) compBeanList);
        Compare("Court Decision", dobjSettlementPrevious.getCourtDecision(), getCourtDecision(), (ArrayList) compBeanList);
        Compare("Court Paid Amount", dobjSettlementPrevious.getCourtPaidAmount(), getCourtPaidAmount(), (ArrayList) compBeanList);

        Compare("Authority Offence", dobjSettlementPrevious.getAuthortyOffence(), getAuthortyOffence(), (ArrayList) compBeanList);
        Compare("Authority ", dobjSettlementPrevious.getAuthorties(), getAuthorties(), (ArrayList) compBeanList);
        Compare("Refer Date", DateUtils.parseDate(dobjSettlementPrevious.getAuthortyReferDate()), DateUtils.parseDate(getAuthortyReferDate()), (ArrayList) compBeanList);
        Compare("Authority Decision ", dobjSettlementPrevious.getAuthortyDecision(), getAuthortyDecision(), (ArrayList) compBeanList);
        Compare("Authority Decision Date", DateUtils.parseDate(dobjSettlementPrevious.getAuthDecisionDate()), DateUtils.parseDate(getAuthDecisionDate()), (ArrayList) compBeanList);
        if (getCourtRecieptDate() != null) {
            Compare("Court Receipt Date", dobjSettlementPrevious.getCourtRecieptDate(), getCourtRecieptDate(), (ArrayList) compBeanList);
        }

        return compBeanList;
    }

    @Override
    public String saveAndMoveFile() {
        String actionCode = String.valueOf(appl_details.getCurrent_action_cd());

        String returnLocation = "";
        CompoundingOfficeImpl impl = null;

        try {
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());
            boolean flag = checkValidattionForsave();
            String refered = "N";
            boolean checkAuthStatus = settleDao.checkAuthortyCase(appl_details.getAppl_no());
            if (checkAuthStatus && cbxAuthorties) {
                refered = "Y";
            }
            if (checkAuthStatus && !cbxAuthorties) {
                refered = "N";
                JSFUtils.setFacesMessage("This Case Is Refered To Authorities . PLease Fill The Authorities Details", "Message", JSFUtils.WARN);
                flag = false;
            }
            if (!checkAuthStatus && cbxAuthorties) {
                refered = "N";
                JSFUtils.setFacesMessage("This Case Is Not Refered To Authorities .", "Message", JSFUtils.WARN);
                flag = false;
            }

            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)) {
                if (flag) {

                    if ((actionCode == null ? (String.valueOf(TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_VERIFY)) == null : actionCode.equals(String.valueOf(TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_VERIFY)))) {

                        impl = new CompoundingOfficeImpl();
                        impl.movetoapprove(actionCode, status, ComparisonBeanImpl.changedDataContents(compareChanges()), dobjSettlement, taxDetailList, isCbxFeePaidAtCOurt(), vehOffenceList, refered, addAuthDetails);
                        returnLocation = "seatwork";
                    }

                    if ((actionCode == null ? (String.valueOf(TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_APPROVE)) == null : actionCode.equals(String.valueOf(TableConstants.VM_ROLE_ENFORCEMENT_SETTLMENT_APPROVE)))) {

                        impl = new CompoundingOfficeImpl();
                        impl.movetoapprove(actionCode, status, ComparisonBeanImpl.changedDataContents(compareChanges()), dobjSettlement, taxDetailList, isCbxFeePaidAtCOurt(), vehOffenceList, refered, addAuthDetails);
                        returnLocation = "seatwork";
                    }

                } else {
                }
            }
            if (flag == false) {
                JSFUtils.setFacesMessage("Some problem to move the file", "message", JSFUtils.FATAL);
            }
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {
                status.setPrev_action_cd_selected(app_disapp_dobj.getPre_action_code());
                impl = new CompoundingOfficeImpl();
                impl.reback(actionCode, status, ComparisonBeanImpl.changedDataContents(compareChanges()), dobjSettlement, taxDetailList, isCbxFeePaidAtCOurt());
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
        return returnLocation;
    }

    public void holdChallanDialog() {
        //RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('holdChallanDailogs').show()");

    }
    //function to hold the challan

    public void holdAndActiveChallan() {
        try {
            CompoundingOfficeImpl impl = new CompoundingOfficeImpl();
            if (dobj.getChallanHoldStatus().equals("-1") && dobj.getChallanHoldStatus() == null) {
                JSFUtils.showMessage("Please Select Hold Status");
                return;
            }

            if (dobj.getHoldReason().equals("") && dobj.getHoldReason() == null) {
                JSFUtils.showMessage("Please Enter Hold Reason");
                return;
            }

            boolean flag = impl.holdAndActiveChallan(dobj, appl_details.getAppl_no());
            if (flag) {
                JSFUtils.showMessagesInDialog("Alert", " Challan " + dobj.getChallanHoldStatus() + " Successfully!!!", FacesMessage.SEVERITY_INFO);
                setChallanHoldEnable(false);
                dobj.setHoldReason("");
                dobj.setChallanHoldStatus("");
            } else {
                JSFUtils.showMessagesInDialog("Alert", "There Is Some Problem,Please Try Again !!!", FacesMessage.SEVERITY_ERROR);
                return;
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

    public void setFromDt() {
        try {
            setTaxFrom(DateUtil.convertDatePattern(taxFrom));
        } catch (VahanException ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
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

    public String getAccusedCategory() {
        return accusedCategory;
    }

    public void setAccusedCategory(String accusedCategory) {
        this.accusedCategory = accusedCategory;
    }

    public List getAccusedList() {
        return accusedList;
    }

    public void setAccusedList(List accusedList) {
        this.accusedList = accusedList;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getVhClass() {
        return vhClass;
    }

    public void setVhClass(String vhClass) {
        this.vhClass = vhClass;
    }

    public String getAccusedName() {
        return accusedName;
    }

    public void setAccusedName(String accusedName) {
        this.accusedName = accusedName;
    }

    public String getStandCap() {
        return standCap;
    }

    public void setStandCap(String standCap) {
        this.standCap = standCap;
    }

    public String getSeatCap() {
        return seatCap;
    }

    public void setSeatCap(String seatCap) {
        this.seatCap = seatCap;
    }

    public String getSleepCap() {
        return sleepCap;
    }

    public void setSleepCap(String sleepCap) {
        this.sleepCap = sleepCap;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public String getChallanPlace() {
        return challanPlace;
    }

    public void setChallanPlace(String challanPlace) {
        this.challanPlace = challanPlace;
    }

    public String getChallanOfficer() {
        return challanOfficer;
    }

    public void setChallanOfficer(String challanOfficer) {
        this.challanOfficer = challanOfficer;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLadenWt() {
        return ladenWt;
    }

    public void setLadenWt(String ladenWt) {
        this.ladenWt = ladenWt;
    }

    public String getStateCd() {
        return stateCd;
    }

    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    public String getRtoCd() {
        return rtoCd;
    }

    public void setRtoCd(String rtoCd) {
        this.rtoCd = rtoCd;
    }

    public String getNcCrNo() {
        return ncCrNo;
    }

    public void setNcCrNo(String ncCrNo) {
        this.ncCrNo = ncCrNo;
    }

    public String getChallanDt() {
        return challanDt;
    }

    public void setChallanDt(String challanDt) {
        this.challanDt = challanDt;
    }

    public String getChallanTime() {
        return challanTime;
    }

    public void setChallanTime(String challanTime) {
        this.challanTime = challanTime;
    }

    public String getChasiNo() {
        return chasiNo;
    }

    public void setChasiNo(String chasiNo) {
        this.chasiNo = chasiNo;
    }

    public List<VehcleOffenceDobj> getVehOffenceList() {
        return vehOffenceList;
    }

    public void setVehOffenceList(List<VehcleOffenceDobj> vehOffenceList) {
        this.vehOffenceList = vehOffenceList;
    }

    public String getBookingFee() {
        return bookingFee;
    }

    public void setBookingFee(String bookingFee) {
        this.bookingFee = bookingFee;
    }

    public String getSettleFee() {
        return settleFee;
    }

    public void setSettleFee(String settleFee) {
        this.settleFee = settleFee;
    }

    public String getMiscFee() {
        return miscFee;
    }

    public void setMiscFee(String miscFee) {
        this.miscFee = miscFee;
    }

    public List<TaxDetailsDobj> getTaxDetailList() {
        return taxDetailList;
    }

    public void setTaxDetailList(List<TaxDetailsDobj> taxDetailList) {
        this.taxDetailList = taxDetailList;
    }

    public String getTaxFrom() {
        return taxFrom;
    }

    public void setTaxFrom(String taxFrom) {
        this.taxFrom = taxFrom;
    }

    public String getTaxUpto() {
        return taxUpto;
    }

    public void setTaxUpto(String taxUpto) {
        this.taxUpto = taxUpto;
    }

    public String getSettleCompTax() {
        return settleCompTax;
    }

    public void setSettleCompTax(String settleCompTax) {
        this.settleCompTax = settleCompTax;
    }

    public String getSettleCompPenalty() {
        return settleCompPenalty;
    }

    public void setSettleCompPenalty(String settleCompPenalty) {
        this.settleCompPenalty = settleCompPenalty;
    }

    public String getSettleCompInterest() {
        return settleCompInterest;
    }

    public void setSettleCompInterest(String settleCompInterest) {
        this.settleCompInterest = settleCompInterest;
    }

    public List<TaxSettlementDobj> getSettleTaxDetail() {
        return settleTaxDetail;
    }

    public void setSettleTaxDetail(List<TaxSettlementDobj> settleTaxDetail) {
        this.settleTaxDetail = settleTaxDetail;
    }

    public List<DocImpoundDobj> getDocImndList() {
        return docImndList;
    }

    public void setDocImndList(List<DocImpoundDobj> docImndList) {
        this.docImndList = docImndList;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public String getHearDate() {
        return hearDate;
    }

    public void setHearDate(String hearDate) {
        this.hearDate = hearDate;
    }

    public String getCompoundFee() {
        return compoundFee;
    }

    public void setCompoundFee(String compoundFee) {
        this.compoundFee = compoundFee;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getImpoundDate() {
        return impoundDate;
    }

    public void setImpoundDate(String impoundDate) {
        this.impoundDate = impoundDate;
    }

    public String getImpoundPlace() {
        return impoundPlace;
    }

    public void setImpoundPlace(String impoundPlace) {
        this.impoundPlace = impoundPlace;
    }

    public String getSeizureNo() {
        return seizureNo;
    }

    public void setSeizureNo(String seizureNo) {
        this.seizureNo = seizureNo;
    }

    public String getContactOfficer() {
        return contactOfficer;
    }

    public void setContactOfficer(String contactOfficer) {
        this.contactOfficer = contactOfficer;
    }

    public List getTaxTypeList() {
        return taxTypeList;
    }

    public void setTaxTypeList(List taxTypeList) {
        this.taxTypeList = taxTypeList;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    public String getSattelmentAmount() {
        return sattelmentAmount;
    }

    public void setSattelmentAmount(String sattelmentAmount) {
        this.sattelmentAmount = sattelmentAmount;
    }

    public boolean isDisableSaveButton() {
        return disableSaveButton;
    }

    public void setDisableSaveButton(boolean disableSaveButton) {
        this.disableSaveButton = disableSaveButton;
    }

    public boolean isTaxDetailPenalVisiblity() {
        return taxDetailPenalVisiblity;
    }

    public void setTaxDetailPenalVisiblity(boolean taxDetailPenalVisiblity) {
        this.taxDetailPenalVisiblity = taxDetailPenalVisiblity;
    }

    public boolean isSattelmentTaxDetailPenalVisiblity() {
        return sattelmentTaxDetailPenalVisiblity;
    }

    public void setSattelmentTaxDetailPenalVisiblity(boolean sattelmentTaxDetailPenalVisiblity) {
        this.sattelmentTaxDetailPenalVisiblity = sattelmentTaxDetailPenalVisiblity;
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

    public boolean isCbxFeePaidAtCOurt() {
        return cbxFeePaidAtCOurt;
    }

    public void setCbxFeePaidAtCOurt(boolean cbxFeePaidAtCOurt) {
        this.cbxFeePaidAtCOurt = cbxFeePaidAtCOurt;
    }

    public boolean isShowFeePaidAtCourtPanel() {
        return showFeePaidAtCourtPanel;
    }

    public void setShowFeePaidAtCourtPanel(boolean showFeePaidAtCourtPanel) {
        this.showFeePaidAtCourtPanel = showFeePaidAtCourtPanel;
    }

    public String getCourtPaidAmount() {
        return courtPaidAmount;
    }

    public void setCourtPaidAmount(String courtPaidAmount) {
        this.courtPaidAmount = courtPaidAmount;
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

    public String getCourtDecision() {
        return CourtDecision;
    }

    public void setCourtDecision(String CourtDecision) {
        this.CourtDecision = CourtDecision;
    }

    public Date getMaxdate() {
        return maxdate;
    }

    public void setMaxdate(Date maxdate) {
        this.maxdate = maxdate;
    }

    public String getMinimumOffencePenalty() {
        return minimumOffencePenalty;
    }

    public void setMinimumOffencePenalty(String minimumOffencePenalty) {
        this.minimumOffencePenalty = minimumOffencePenalty;
    }

    public int getBalanceFee() {
        return balanceFee;
    }

    public void setBalanceFee(int balanceFee) {
        this.balanceFee = balanceFee;
    }

    public boolean isHidefeepanel() {
        return hidefeepanel;
    }

    public void setHidefeepanel(boolean hidefeepanel) {
        this.hidefeepanel = hidefeepanel;
    }

    public boolean isHidetaxsettlementpanel() {
        return hidetaxsettlementpanel;
    }

    public void setHidetaxsettlementpanel(boolean hidetaxsettlementpanel) {
        this.hidetaxsettlementpanel = hidetaxsettlementpanel;
    }

    public String getReasonOfMiscellaneousFees() {
        return reasonOfMiscellaneousFees;
    }

    public void setReasonOfMiscellaneousFees(String reasonOfMiscellaneousFees) {
        this.reasonOfMiscellaneousFees = reasonOfMiscellaneousFees;
    }

    public boolean isHidetaxListPenal() {
        return hidetaxListPenal;
    }

    public void setHidetaxListPenal(boolean hidetaxListPenal) {
        this.hidetaxListPenal = hidetaxListPenal;
    }

    public boolean isHideMissfeepanel() {
        return hideMissfeepanel;
    }

    public void setHideMissfeepanel(boolean hideMissfeepanel) {
        this.hideMissfeepanel = hideMissfeepanel;
    }

    /**
     * @return the showOffenceAddPanel
     */
    public Boolean getShowOffenceAddPanel() {
        return showOffenceAddPanel;
    }

    /**
     * @param showOffenceAddPanel the showOffenceAddPanel to set
     */
    public void setShowOffenceAddPanel(Boolean showOffenceAddPanel) {
        this.showOffenceAddPanel = showOffenceAddPanel;
    }

    /**
     * @return the vehPrevOffenceList
     */
    public List<VehcleOffenceDobj> getVehPrevOffenceList() {
        return vehPrevOffenceList;
    }

    /**
     * @param vehPrevOffenceList the vehPrevOffenceList to set
     */
    public void setVehPrevOffenceList(List<VehcleOffenceDobj> vehPrevOffenceList) {
        this.vehPrevOffenceList = vehPrevOffenceList;
    }

    /**
     * @return the accusedType
     */
    public Map<String, Object> getAccusedType() {
        return accusedType;
    }

    /**
     * @param accusedType the accusedType to set
     */
    public void setAccusedType(Map<String, Object> accusedType) {
        this.accusedType = accusedType;
    }

    /**
     * @return the Offence
     */
    public Map<String, Object> getOffence() {
        return Offence;
    }

    /**
     * @param Offence the Offence to set
     */
    public void setOffence(Map<String, Object> Offence) {
        this.Offence = Offence;
    }

    /**
     * @return the offenceCode
     */
    public String getOffenceCode() {
        return offenceCode;
    }

    /**
     * @param offenceCode the offenceCode to set
     */
    public void setOffenceCode(String offenceCode) {
        this.offenceCode = offenceCode;
    }

    /**
     * @return the compFee
     */
    public String getCompFee() {
        return compFee;
    }

    /**
     * @param compFee the compFee to set
     */
    public void setCompFee(String compFee) {
        this.compFee = compFee;
    }

    /**
     * @return the offenceDetails
     */
    public List<OffencesDobj> getOffenceDetails() {
        return offenceDetails;
    }

    /**
     * @param offenceDetails the offenceDetails to set
     */
    public void setOffenceDetails(List<OffencesDobj> offenceDetails) {
        this.offenceDetails = offenceDetails;
    }

    /**
     * @return the disableChangeButton
     */
    public boolean isDisableChangeButton() {
        return disableChangeButton;
    }

    /**
     * @param disableChangeButton the disableChangeButton to set
     */
    public void setDisableChangeButton(boolean disableChangeButton) {
        this.disableChangeButton = disableChangeButton;
    }

    /**
     * @return the disablePayAtCourt
     */
    public boolean isDisablePayAtCourt() {
        return disablePayAtCourt;
    }

    /**
     * @param disablePayAtCourt the disablePayAtCourt to set
     */
    public void setDisablePayAtCourt(boolean disablePayAtCourt) {
        this.disablePayAtCourt = disablePayAtCourt;
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
     * @return the stateCode
     */
    public List getStateCode() {
        return stateCode;
    }

    /**
     * @param stateCode the stateCode to set
     */
    public void setStateCode(List stateCode) {
        this.stateCode = stateCode;
    }

    /**
     * @return the rtoCode
     */
    public List getRtoCode() {
        return rtoCode;
    }

    /**
     * @param rtoCode the rtoCode to set
     */
    public void setRtoCode(List rtoCode) {
        this.rtoCode = rtoCode;
    }

    /**
     * @return the fuelList
     */
    public List getFuelList() {
        return fuelList;
    }

    /**
     * @param fuelList the fuelList to set
     */
    public void setFuelList(List fuelList) {
        this.fuelList = fuelList;
    }

    /**
     * @return the vehclass
     */
    public List getVehclass() {
        return vehclass;
    }

    /**
     * @param vehclass the vehclass to set
     */
    public void setVehclass(List vehclass) {
        this.vehclass = vehclass;
    }

    /**
     * @return the referToCourte
     */
    public boolean isReferToCourte() {
        return referToCourte;
    }

    /**
     * @param referToCourte the referToCourte to set
     */
    public void setReferToCourte(boolean referToCourte) {
        this.referToCourte = referToCourte;
    }

    /**
     * @return the cbxAuthorties
     */
    public boolean isCbxAuthorties() {
        return cbxAuthorties;
    }

    /**
     * @param cbxAuthorties the cbxAuthorties to set
     */
    public void setCbxAuthorties(boolean cbxAuthorties) {
        this.cbxAuthorties = cbxAuthorties;
    }

    /**
     * @return the showFeePaidAtAuthorty
     */
    public boolean isShowFeePaidAtAuthorty() {
        return showFeePaidAtAuthorty;
    }

    /**
     * @param showFeePaidAtAuthorty the showFeePaidAtAuthorty to set
     */
    public void setShowFeePaidAtAuthorty(boolean showFeePaidAtAuthorty) {
        this.showFeePaidAtAuthorty = showFeePaidAtAuthorty;
    }

    /**
     * @return the authortyOffence
     */
    public String getAuthortyOffence() {
        return authortyOffence;
    }

    /**
     * @param authortyOffence the authortyOffence to set
     */
    public void setAuthortyOffence(String authortyOffence) {
        this.authortyOffence = authortyOffence;
    }

    /**
     * @return the authorties
     */
    public String getAuthorties() {
        return authorties;
    }

    /**
     * @param authorties the authorties to set
     */
    public void setAuthorties(String authorties) {
        this.authorties = authorties;
    }

    /**
     * @return the authortyDecision
     */
    public String getAuthortyDecision() {
        return authortyDecision;
    }

    /**
     * @param authortyDecision the authortyDecision to set
     */
    public void setAuthortyDecision(String authortyDecision) {
        this.authortyDecision = authortyDecision;
    }

    /**
     * @return the authortyReferDate
     */
    public Date getAuthortyReferDate() {
        return authortyReferDate;
    }

    /**
     * @param authortyReferDate the authortyReferDate to set
     */
    public void setAuthortyReferDate(Date authortyReferDate) {
        this.authortyReferDate = authortyReferDate;
    }

    /**
     * @return the authDecisionDate
     */
    public Date getAuthDecisionDate() {
        return authDecisionDate;
    }

    /**
     * @param authDecisionDate the authDecisionDate to set
     */
    public void setAuthDecisionDate(Date authDecisionDate) {
        this.authDecisionDate = authDecisionDate;
    }

    /**
     * @return the authpenalty
     */
    public String getAuthpenalty() {
        return authpenalty;
    }

    /**
     * @param authpenalty the authpenalty to set
     */
    public void setAuthpenalty(String authpenalty) {
        this.authpenalty = authpenalty;
    }

    /**
     * @return the authortySection
     */
    public String getAuthortySection() {
        return authortySection;
    }

    /**
     * @param authortySection the authortySection to set
     */
    public void setAuthortySection(String authortySection) {
        this.authortySection = authortySection;
    }

    public List<OffenceReferDetailsDobj> getAddAuthDetails() {
        return addAuthDetails;
    }

    public void setAddAuthDetails(List<OffenceReferDetailsDobj> addAuthDetails) {
        this.addAuthDetails = addAuthDetails;
    }

    /**
     * @return the authOffenceList
     */
    public Map<String, Object> getAuthOffenceList() {
        return authOffenceList;
    }

    /**
     * @param authOffenceList the authOffenceList to set
     */
    public void setAuthOffenceList(Map<String, Object> authOffenceList) {
        this.authOffenceList = authOffenceList;
    }

    /**
     * @return the authortyList
     */
    public Map<String, Object> getAuthortyList() {
        return authortyList;
    }

    /**
     * @param authortyList the authortyList to set
     */
    public void setAuthortyList(Map<String, Object> authortyList) {
        this.authortyList = authortyList;
    }

    /**
     * @return the disablePayAtAuthority
     */
    public boolean isDisablePayAtAuthority() {
        return disablePayAtAuthority;
    }

    /**
     * @param disablePayAtAuthority the disablePayAtAuthority to set
     */
    public void setDisablePayAtAuthority(boolean disablePayAtAuthority) {
        this.disablePayAtAuthority = disablePayAtAuthority;
    }

    /**
     * @return the referAuthorties
     */
    public List<OffenceReferDetailsDobj> getReferAuthorties() {
        return referAuthorties;
    }

    /**
     * @param referAuthorties the referAuthorties to set
     */
    public void setReferAuthorties(List<OffenceReferDetailsDobj> referAuthorties) {
        this.referAuthorties = referAuthorties;
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
     * @return the accusedWiseOffenceList
     */
    public List getAccusedWiseOffenceList() {
        return accusedWiseOffenceList;
    }

    /**
     * @param accusedWiseOffenceList the accusedWiseOffenceList to set
     */
    public void setAccusedWiseOffenceList(List accusedWiseOffenceList) {
        this.accusedWiseOffenceList = accusedWiseOffenceList;
    }

    /**
     * @return the dobj
     */
    public ChallanHoldDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(ChallanHoldDobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the statusList
     */
    public ArrayList getStatusList() {
        return statusList;
    }

    /**
     * @param statusList the statusList to set
     */
    public void setStatusList(ArrayList statusList) {
        this.statusList = statusList;
    }

    /**
     * @return the challanHoldEnable
     */
    public boolean isChallanHoldEnable() {
        return challanHoldEnable;
    }

    /**
     * @param challanHoldEnable the challanHoldEnable to set
     */
    public void setChallanHoldEnable(boolean challanHoldEnable) {
        this.challanHoldEnable = challanHoldEnable;
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

    public boolean isDisable_coming_from() {
        return disable_coming_from;
    }

    public void setDisable_coming_from(boolean disable_coming_from) {
        this.disable_coming_from = disable_coming_from;
    }

    public boolean isDisable_going_to() {
        return disable_going_to;
    }

    public void setDisable_going_to(boolean disable_going_to) {
        this.disable_going_to = disable_going_to;
    }

    public String getAdvcompFee() {
        return advcompFee;
    }

    public void setAdvcompFee(String advcompFee) {
        this.advcompFee = advcompFee;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public boolean isDisableOwnerDetails() {
        return disableOwnerDetails;
    }

    public void setDisableOwnerDetails(boolean disableOwnerDetails) {
        this.disableOwnerDetails = disableOwnerDetails;
    }
}
