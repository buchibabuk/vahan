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
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.bean.fancy.RetenRegnNo_dobj;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.TmConfigurationOwnerIdentificationDobj;
import nic.vahan.form.dobj.ToDobj;
import nic.vahan.form.dobj.fancy.AdvanceRegnNo_dobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.NewImpl;
import nic.vahan.form.impl.OwnerIdentificationImpl;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.ToImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.form.dobj.AuctionDobj;
import nic.vahan.form.impl.ApplicationInwardImpl;
import nic.vahan.form.impl.AuctionImpl;
import nic.vahan.form.impl.dealer.OfficeCorrectionImpl;
import nic.vahan.server.CommonUtils;

@ManagedBean(name = "to_bean")
@ViewScoped
public class ToBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(ToBean.class);
    private List list_c_district;
    private List list_p_district;
    private List list_c_state;
    private List list_p_state;
    private List list_owner_code;
    private List list_owner_catg;
    private ToDobj to_dobj_prv = null;
    private ToDobj to_dobj = new ToDobj();
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private Date today = new Date();
    private Date minDate = new Date();
    private String purposeType;
    private String purposeTypeLabel;
    private List<ComparisonBean> prevChangedDataList;
    private List<OwnerDetailsDobj> listExistingOwnerDetails = new ArrayList<>();
    private int option;
    private boolean sameAsCurrAddress;
    private OwnerIdentificationDobj owner_identification = new OwnerIdentificationDobj();
    private OwnerIdentificationDobj ownerIdentificationPrev = null;
    private boolean rendersavebutton = false;
    private boolean renderApprovePrint = false;
    private boolean renderApprovePrintSuccession = false;
    private boolean boolFName;
    private boolean boolOwnrCode;
    private boolean boolOwnrCatg;
    private boolean boolOwnerName;
    @ManagedProperty(value = "#{hypothecationDetails_bean}")
    private HypothecationDetailsBean hypothecationDetails_bean;
    private boolean fancyRetention;
    private String selectedFancyRetnetion;
    private String vahanMessages = null;
    private boolean retAllowed = false;
    private boolean retCheck = false;
    private boolean disableRetCheck = false;
    private boolean advRetCheckDialogue = false;
    private RetenRegnNo_dobj retenRegNoDobj = new RetenRegnNo_dobj();
    private List list_adv_district = new ArrayList();
    private boolean advRegnCheck = false;
    private boolean advRegnCheckDialogue = false;
    private AdvanceRegnNo_dobj advRegnNoDobj = new AdvanceRegnNo_dobj();
    private boolean disableAdvRegnCheck = false;
    private AuctionDobj auctionDobj = null;
    private boolean auctionVisibilityTab = false;
    private boolean disableRetSelectOneMenu = false;
    private String retRcptMsg;

    public ToBean() {

        if (appl_details.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_TO) {
            purposeType = "Sale";
        } else {
            purposeType = "RC-Surrendered";
        }

        purposeTypeLabel = purposeType;
        list_c_district = new ArrayList();
        list_c_state = new ArrayList();
        list_p_state = new ArrayList();
        list_p_district = new ArrayList();
        list_owner_code = new ArrayList();
        list_owner_catg = new ArrayList();
        setRendersavebutton(true);

        String[][] data = MasterTableFiller.masterTables.VM_OWCODE.getData();
        for (int i = 0; i < data.length; i++) {
            list_owner_code.add(new SelectItem(data[i][0], data[i][1]));
        }

        data = MasterTableFiller.masterTables.VM_OWCATG.getData();
        for (int i = 0; i < data.length; i++) {
            list_owner_catg.add(new SelectItem(data[i][0], data[i][1]));
        }

    }

    @PostConstruct
    public void init() {

        try {
            ToImpl to_Impl = new ToImpl();
            OwnerIdentificationImpl ownerIdentificationImpl = new OwnerIdentificationImpl();
            InsDobj ins_dobj_ret = null;
            list_c_state = MasterTableFiller.getStateList();
            list_p_state = MasterTableFiller.getStateList();

            if (getAppl_details() != null) {

                TmConfigurationDobj tmConfig = Util.getTmConfiguration();
                if (tmConfig == null) {
                    tmConfig = ServerUtil.getTmConfigurationParameters(Util.getUserStateCode());
                }

                if (tmConfig != null) {
                    if (tmConfig.isTo_retention() && (to_Impl.isFancyNo(appl_details.getRegn_no()) || tmConfig.isTo_retention_for_all_regn())) {
                        fancyRetention = true;
                    }
                    if (fancyRetention && to_Impl.isSurrenderRetention(appl_details.getAppl_no())) {
                        selectedFancyRetnetion = "YES";
                    } else if (fancyRetention && appl_details.getCurrent_role() != TableConstants.TM_ROLE_ENTRY) {
                        selectedFancyRetnetion = "NO";
                    }
                }

                if (!CommonUtils.isNullOrBlank(selectedFancyRetnetion) && fancyRetention) {
                    if (selectedFancyRetnetion.equalsIgnoreCase("YES")) {
                        String rcptNo = OfficeCorrectionImpl.getRcptNoForFeePaidForPurpose(appl_details.getAppl_no(), TableConstants.SWAPPING_REGN_PUR_CD);
                        if (!CommonUtils.isNullOrBlank(rcptNo)) {
                            disableRetSelectOneMenu = true;
                        }
                    }
                }

                listExistingOwnerDetails.add(appl_details.getOwnerDetailsDobj());//for filling existing owner details
                list_c_district = MasterTableFiller.getDistrictList(Util.getUserStateCode());
                list_p_district = MasterTableFiller.getDistrictList(Util.getUserStateCode());

                if (appl_details.getOwnerDetailsDobj() == null) {
                    vahanMessages = "Owner Details not Found in the Database, Please Contact to the System Administrator";
                    return;
                }

                to_dobj = to_Impl.set_TO_appl_db_to_dobj(getAppl_details().getAppl_no());
                if (to_dobj != null) {
                    if (to_dobj.getReason().equalsIgnoreCase("Sale") || to_dobj.getReason().equalsIgnoreCase("Auction")) {
                        setRenderApprovePrint(true);
                    } else if (to_dobj.getReason().equalsIgnoreCase("Succession")) {
                        setRenderApprovePrintSuccession(true);
                    }
                    if (!CommonUtils.isNullOrBlank(selectedFancyRetnetion)) {
                        to_dobj.setNumberRetention(selectedFancyRetnetion);
                    } else {
                        to_dobj.setNumberRetention("NO");
                    }
                }
                OwnerIdentificationImpl identificationImpl = new OwnerIdentificationImpl();
                TmConfigurationOwnerIdentificationDobj tmConfigOwnerId = identificationImpl.getTmConfigurationOwnerIdentification(Util.getUserStateCode());
                owner_identification = ownerIdentificationImpl.set_Owner_identification_appl_db_dobj(getAppl_details().getAppl_no());
                VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(appl_details.getOwnerDobj());
                if (owner_identification == null) {
                    owner_identification = new OwnerIdentificationDobj();

                    if (tmConfigOwnerId != null && isCondition(replaceTagValues(tmConfigOwnerId.getPan_card_mandatory(), vehParameters), "getPan_card_mandatory1")) {
                        owner_identification.setPan_card_mandatory(true);
                    }
                } else {
                    ownerIdentificationPrev = (OwnerIdentificationDobj) owner_identification.clone();
                    if (tmConfigOwnerId != null && isCondition(replaceTagValues(tmConfigOwnerId.getPan_card_mandatory(), vehParameters), "getPan_card_mandatory1")) {
                        owner_identification.setPan_card_mandatory(true);
                    }
                }
                if (to_dobj != null) {
                    this.to_dobj_prv = (ToDobj) to_dobj.clone();//for holding current dobj for using in the comparison.
                    this.purposeType = to_dobj.getReason();
                    this.purposeTypeLabel = to_dobj.getReason();
                    list_p_district.clear();//need to fix this for setting only list according to state..pending..
                    list_p_district = MasterTableFiller.getDistrictList(to_dobj.getP_state());

                    //for disable the owner's f_name in case of other-than-individual
                    if (to_dobj.getOwner_cd() > 0 && to_dobj.getOwner_cd() != TableConstants.OWNER_CODE_INDIVIDUAL) {
                        boolFName = true;
                    }

                } else {
                    to_dobj = new ToDobj();
                    to_dobj.setStateCode(appl_details.getCurrent_state_cd());
                    to_dobj.setOffCode(appl_details.getCurrent_off_cd());
                    if (listExistingOwnerDetails != null || !listExistingOwnerDetails.isEmpty()) {
                        to_dobj.setC_state(listExistingOwnerDetails.get(0).getState_cd());//if form is empty at entry time
                        to_dobj.setP_state(listExistingOwnerDetails.get(0).getState_cd());//if form is empty at entry time
                    } else if (listExistingOwnerDetails == null || listExistingOwnerDetails.isEmpty()) {
                        to_dobj.setC_state(appl_details.getCurrent_state_cd());//if form is empty at entry time
                        to_dobj.setP_state(appl_details.getCurrent_state_cd());//if form is empty at entry time
                    }

                    //filling details of Hypothecation in the case of FRC first time
                    if (appl_details.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_FRESH_RC) {
                        HpaImpl hpa_Impl = new HpaImpl();
                        List<HpaDobj> hypth = hpa_Impl.getHypoDetails(null, TableConstants.VM_TRANSACTION_MAST_REM_HYPO, appl_details.getRegn_no(), true, Util.getUserStateCode());
                        hypothecationDetails_bean.setListHypthDetails(hypth);

                        if (hypth.size() > 0) {
                            this.to_dobj.setOwner_name(hypth.get(0).getFncr_name());
                            this.to_dobj.setC_add1(hypth.get(0).getFncr_add1());
                            this.to_dobj.setC_add2(hypth.get(0).getFncr_add2());
                            this.to_dobj.setC_add3(hypth.get(0).getFncr_add3());
                            this.to_dobj.setC_pincode(hypth.get(0).getFncr_pincode());
                            this.to_dobj.setC_state(hypth.get(0).getFncr_state());
                            this.to_dobj.setC_district(hypth.get(0).getFncr_district());
                            list_c_district.clear();//need to fix this for setting only list according to state..pending..
                            list_c_district = MasterTableFiller.getDistrictList(hypth.get(0).getFncr_state());
                        } else {
                            list_c_district.clear();//need to fix this for setting only list according to state..pending..
                            list_c_district = MasterTableFiller.getDistrictList(to_dobj.getC_state());
                        }
                        this.to_dobj.setOwner_cd(TableConstants.OWNER_CODE_FIRM);
                        this.to_dobj.setF_name("NA");
                        this.to_dobj.setOwner_ctg(TableConstants.OWNER_CATG_OTHERS);
                    }

                }

                to_dobj.setAppl_no(appl_details.getAppl_no());
                to_dobj.setRegn_no(appl_details.getRegn_no());
                to_dobj.setPur_cd(appl_details.getPur_cd());
                to_dobj.setStateCode(appl_details.getCurrent_state_cd());
                to_dobj.setOffCode(appl_details.getCurrent_off_cd());
                minDate = ServerUtil.dateRange(JSFUtils.getStringToDateyyyyMMdd(appl_details.getOwnerDetailsDobj().getRegn_dt()), 0, 0, 0);

                setAuctionDobj(new AuctionImpl().getDetailsFromVtAuction(null, appl_details.getRegn_no()));
                if (getAuctionDobj() != null) {
                    setAuctionVisibilityTab(true);
                    auctionDobj.setDisableAuctionPanel(true);
                    minDate = getAuctionDobj().getAuctionDate();
                }

                //filling details of Hypothecation in the case of FRC
                if (appl_details.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_FRESH_RC) {
                    boolOwnerName = true;
                    boolOwnrCode = true;
                    boolFName = true;
                    boolOwnrCatg = true;
                    if (to_dobj != null && (to_dobj.getOwner_name() == null || to_dobj.getOwner_name().length() <= 0)) {
                        HpaImpl hpaImpl = new HpaImpl();
                        HpaDobj hpaDobj = hpaImpl.getVhHptDetails(to_dobj.getAppl_no(), to_dobj.getRegn_no(), to_dobj.getOffCode());
                        if (hpaDobj != null && hpaDobj.getFncr_name() != null) {
                            this.to_dobj.setOwner_name(hpaDobj.getFncr_name());
                        }
                    }
                }

                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());

                ins_dobj_ret = InsImpl.set_ins_dtls_db_to_dobj(appl_details.getRegn_no(), null, null, 0);

                if (tmConfig != null && tmConfig.isReassign_retained_no_with_to()) {
                    setRetAllowed(true);
                }
                if (appl_details.getCurrent_role() == TableConstants.TM_ROLE_VERIFICATION
                        || appl_details.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL) {
                    setDisableRetCheck(true);
                    setDisableAdvRegnCheck(true);
                    String regnNoAllotted = NewImpl.getAdvanceRetenNo(appl_details.getAppl_no());
                    if (regnNoAllotted != null && !regnNoAllotted.isEmpty()) {
                        setRetCheck(true);
                        to_dobj.setAssignRetainNo(true);
                        to_dobj.setAssignRetainRegnNo(regnNoAllotted);
                    } else {
                        regnNoAllotted = NewImpl.getAdvanceRegnNo(appl_details.getAppl_no());
                        if (regnNoAllotted != null && !regnNoAllotted.isEmpty()) {
                            setAdvRegnCheck(true);
                            to_dobj.setAssignFancyNumber(true);
                            to_dobj.setAssignFancyRegnNumber(regnNoAllotted);
                        }
                    }
                }

                //Check for blacklisted
                BlackListedVehicleDobj blacklistedStatus = appl_details.getOwnerDetailsDobj().getBlackListedVehicleDobj();

                if (blacklistedStatus != null
                        && (blacklistedStatus.getComplain_type() == TableConstants.BLTheftCode
                        || blacklistedStatus.getComplain_type() == TableConstants.BLDestroyedAccidentCode
                        || blacklistedStatus.getComplain_type() == TableConstants.BLConfiscationCode)) {

                    /*if (!purposeType.equalsIgnoreCase(TableConstants.THEFT)) {
                     this.to_dobj.setSale_dt(null);
                     this.to_dobj.setTransfer_dt(null);
                     }*/
                    if (blacklistedStatus.getComplain_type() == TableConstants.BLTheftCode) {
                        this.purposeTypeLabel = "Theft";
                        this.purposeType = "Theft";
                    }

                    if (blacklistedStatus.getComplain_type() == TableConstants.BLDestroyedAccidentCode) {
                        this.purposeTypeLabel = "Destroyed";
                        this.purposeType = "Destroyed";
                    }

                    if (blacklistedStatus.getComplain_type() == TableConstants.BLConfiscationCode) {
                        this.purposeTypeLabel = "Confiscation";
                        this.purposeType = "Confiscation";
                    }

                    if (blacklistedStatus.getComplain_type() != TableConstants.BLConfiscationCode) {
                        this.to_dobj.setOwner_cd(TableConstants.OWNER_CODE_FIRM);
                        boolOwnrCode = true;
                        this.to_dobj.setF_name("NA");
                        boolFName = true;
                        this.to_dobj.setOwner_ctg(TableConstants.OWNER_CATG_OTHERS);
                        boolOwnrCatg = true;
                        if (ins_dobj_ret != null && ins_dobj_ret.getInsCompName() != null && appl_details.getPur_cd() != TableConstants.VM_TRANSACTION_MAST_FRESH_RC) {
                            if (ins_dobj_ret.getInsCompName().length() <= 35) {
                                this.to_dobj.setOwner_name(ins_dobj_ret.getInsCompName());
                            } else {
                                this.to_dobj.setOwner_name(ins_dobj_ret.getInsCompName().substring(0, 35));
                            }
                            boolOwnerName = true;
                        }
                    }
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
    public List<ComparisonBean> compareChanges() {

        if (this.to_dobj_prv == null) {
            return compBeanList;
        }
        compBeanList.clear(); // for clearing the list in case of clicking compare changes button again and again.

        Compare("O_Name", to_dobj_prv.getOwner_name(), to_dobj.getOwner_name(), compBeanList);
        Compare("F_Name", to_dobj_prv.getF_name(), to_dobj.getF_name(), compBeanList);
        Compare("O_Type", to_dobj_prv.getOwner_cd(), to_dobj.getOwner_cd(), compBeanList);
        Compare("O_Catg", to_dobj_prv.getOwner_ctg(), to_dobj.getOwner_ctg(), compBeanList);
        Compare("Sale_Amt", to_dobj_prv.getSale_amt(), to_dobj.getSale_amt(), compBeanList);
        Compare("Sale_dt", to_dobj_prv.getSale_dt(), to_dobj.getSale_dt(), compBeanList);
        Compare("Transfer_dt", to_dobj_prv.getTransfer_dt(), to_dobj.getTransfer_dt(), compBeanList);
        Compare("Reason", to_dobj_prv.getReason(), this.purposeType, compBeanList);
        Compare("C_Add1", to_dobj_prv.getC_add1(), to_dobj.getC_add1(), compBeanList);
        Compare("C_Add2", to_dobj_prv.getC_add2(), to_dobj.getC_add2(), compBeanList);
        Compare("C_Add3", to_dobj_prv.getC_add3(), to_dobj.getC_add3(), compBeanList);
        Compare("C_Dist", to_dobj_prv.getC_district(), to_dobj.getC_district(), compBeanList);
        Compare("C_State", to_dobj_prv.getC_state(), to_dobj.getC_state(), compBeanList);
        Compare("C_Pin", to_dobj_prv.getC_pincode(), to_dobj.getC_pincode(), compBeanList);
        Compare("P_Add1", to_dobj_prv.getP_add1(), to_dobj.getP_add1(), compBeanList);
        Compare("P_Add2", to_dobj_prv.getP_add2(), to_dobj.getP_add2(), compBeanList);
        Compare("P_Add3", to_dobj_prv.getP_add3(), to_dobj.getP_add3(), compBeanList);
        Compare("P_Dist", to_dobj_prv.getP_district(), to_dobj.getP_district(), compBeanList);
        Compare("P_State", to_dobj_prv.getP_state(), to_dobj.getP_state(), compBeanList);
        Compare("P_Pin", to_dobj_prv.getP_pincode(), to_dobj.getP_pincode(), compBeanList);
        Compare("G_Address", to_dobj_prv.getGarage_add(), to_dobj.getGarage_add(), compBeanList);
        if (fancyRetention) {
            Compare("Fancy_Ret", to_dobj_prv.getNumberRetention(), this.selectedFancyRetnetion, compBeanList);
        }
        if (ownerIdentificationPrev != null) {
            Compare("PAN_No", ownerIdentificationPrev.getPan_no(), this.owner_identification.getPan_no(), compBeanList);
            Compare("Mobile", ownerIdentificationPrev.getMobile_no(), this.owner_identification.getMobile_no(), compBeanList);
            Compare("Email", ownerIdentificationPrev.getEmail_id(), this.owner_identification.getEmail_id(), compBeanList);
            Compare("Aadhar", ownerIdentificationPrev.getAadhar_no(), this.owner_identification.getAadhar_no(), compBeanList);
            Compare("Passport", ownerIdentificationPrev.getPassport_no(), this.owner_identification.getPassport_no(), compBeanList);
            Compare("Ration", ownerIdentificationPrev.getRation_card_no(), this.owner_identification.getRation_card_no(), compBeanList);
            Compare("Voter_Id", ownerIdentificationPrev.getVoter_id(), this.owner_identification.getVoter_id(), compBeanList);
            Compare("DL", ownerIdentificationPrev.getDl_no(), this.owner_identification.getDl_no(), compBeanList);
        }

        return compBeanList;

    }

    @Override
    public String save() {
        String return_location = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            Date applDate = format.parse(appl_details.getAppl_dt());

            List<ComparisonBean> compareChanges = compareChanges();
            owner_identification.setAppl_no(appl_details.getAppl_no());
            owner_identification.setRegn_no(appl_details.getRegn_no());

            to_dobj.setReason(purposeType);
            owner_identification.setAppl_no(appl_details.getAppl_no());
            owner_identification.setRegn_no(appl_details.getRegn_no());

            if (!compareChanges.isEmpty() || to_dobj_prv == null) { //save only when data is really changed by user
                ToImpl impl = new ToImpl();
                impl.makeChangeTO(to_dobj, owner_identification, ComparisonBeanImpl.changedDataContents(compareChanges), selectedFancyRetnetion, applDate);
            }
            return_location = "seatwork";
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
        }
        return return_location;
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        ToImpl to_Impl = null;
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

                List<ComparisonBean> compareChanges = compareChanges();

                to_dobj.setReason(purposeType);
                owner_identification.setAppl_no(appl_details.getAppl_no());
                owner_identification.setRegn_no(appl_details.getRegn_no());

                to_Impl = new ToImpl();
                status.setVehicleParameters(appl_details.getVehicleParameters());
                status.getVehicleParameters().setOWNER_CD(to_dobj.getOwner_cd());
                status.getVehicleParameters().setTO_REASON(to_dobj.getReason());
                if (TableConstants.STATUS_COMPLETE.equals(getApp_disapp_dobj().getNew_status())) {
                    if ("HP".contains(Util.getUserStateCode())) {
                        String flag = new ApplicationInwardImpl().getFancyNumberModule(appl_details.getRegn_no());
                        if (!CommonUtils.isNullOrBlank(flag) && !CommonUtils.isNullOrBlank(selectedFancyRetnetion) && fancyRetention) {
                            if (flag.equalsIgnoreCase(TableConstants.AUCTION_MODULE) && !selectedFancyRetnetion.equalsIgnoreCase("YES")) {
                                throw new VahanException(TableConstants.HP_eAuction_TO_Stop_Msg);
                            }
                        }
                    }
                }
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE) && this.appl_details.isDocVerifyForFacelessAppl()) {
                    String[] notVerifiedDocDetails = ServerUtil.verifyDocUploaded(appl_details.getAppl_no(), getAppl_details().getCurrent_action_cd());
                    if (notVerifiedDocDetails != null) {
                        appl_details.setNotVerifiedDocdetails(notVerifiedDocDetails[1]);
                        throw new VahanException(notVerifiedDocDetails[0]);
                    }
                }
                to_Impl.update_TO_Status(to_dobj, to_dobj_prv, owner_identification, status, ComparisonBeanImpl.changedDataContents(compareChanges), selectedFancyRetnetion, appl_details, getAuctionDobj());
                if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    ServerUtil.sendSmsInFacelessService(appl_details.getAppl_no(), Util.getUserStateCode(), Util.getUserSeatOffCode(), appl_details.getRegn_no(), appl_details.getNotVerifiedDocdetails());
                    return_location = disapprovalPrint();
                }
                if (status.getCurrent_role() == TableConstants.TM_ROLE_VERIFICATION
                        && status.getAction_cd() == TableConstants.FRESH_RC_TO_FINANCIER_APPROVAL
                        && "AR,MH,ML,MN,MZ,NL,OR,TR".contains(Util.getUserStateCode())) {
                    return_location = frcPrint();
                } else {
                    return_location = "seatwork";
                }

            }
            if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_COMPLETE) && appl_details.getCurrent_role() == 4) {
                if (!ServerUtil.getDataEntryIncomplete(appl_details.getAppl_no())) {
                    PrimeFaces.current().ajax().update("app_disapp_new_form:showOwnerDiscPopup");
                    PrimeFaces.current().executeScript("PF('successDialog').show()");
                    return_location = "";
                }
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
        }
        return return_location;
    }

    public void updatePurposeType() {
        if (purposeType.equalsIgnoreCase("Auction")) {
            purposeTypeLabel = "Auction";
        } else if (purposeType.equalsIgnoreCase("Sale")) {
            purposeTypeLabel = "Sale";
        } else if (purposeType.equalsIgnoreCase("Succession")) {
            purposeTypeLabel = "Succession";
        } else if (purposeType.equalsIgnoreCase("Destroyed")) {
            purposeTypeLabel = "Destroyed";
        } else if (purposeType.equalsIgnoreCase("Theft")) {
            purposeTypeLabel = "Theft";
        } else if (purposeType.equalsIgnoreCase("Confiscation")) {
            purposeTypeLabel = "Confiscation";
        }
    }

    public String frcPrint() {
        if (appl_details != null) {
            HttpSession ses = Util.getSession();
            ses.setAttribute("applNofrc", appl_details.getAppl_no());
            ses.setAttribute("regnNofrc", appl_details.getRegn_no());
            ses.setAttribute("purCodefrc", appl_details.getPur_cd());
            return "/ui/registration/formPrintRcToFinancer.xhtml?faces-redirect=true";
        }
        return "";
    }

    public String printDisclaimer() {
        return PrintDocImpl.printOwnerDiscReport("registeredVehicles", "reportFormat");
    }

    public void sameAsCurrentAddressListener(AjaxBehaviorEvent event) {

        if (this.sameAsCurrAddress) {

            to_dobj.setP_add1(to_dobj.getC_add1());
            to_dobj.setP_add2(to_dobj.getC_add2());
            to_dobj.setP_add3(to_dobj.getC_add3());
            to_dobj.setP_pincode(to_dobj.getC_pincode());
            to_dobj.setP_state(to_dobj.getC_state());

            if (this.to_dobj.getC_district() != -1) {
                String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
                list_p_district.clear();
                for (int i = 0; i < data.length; i++) {
                    if (this.to_dobj.getC_state().equalsIgnoreCase(data[i][2])) {
                        list_p_district.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
                this.to_dobj.setP_district(this.to_dobj.getC_district());
            }

        } else {
            this.to_dobj.setP_add1("");
            this.to_dobj.setP_add2("");
            this.to_dobj.setP_add3("");
            this.to_dobj.setP_pincode(0);
            this.to_dobj.setP_district(-1);
        }
    }

    public void vehOwner_cdListener(AjaxBehaviorEvent event) {

        //OWNER_CODE_INDIVIDUAL
        if (to_dobj.getOwner_cd() != TableConstants.OWNER_CODE_INDIVIDUAL) {
            to_dobj.setF_name("NA");
            boolFName = true;
        } else {
            to_dobj.setF_name("");
            boolFName = false;
        }
    }

    public void cStateListener(AjaxBehaviorEvent event) {

        String cStateCd = this.to_dobj.getC_state();
        String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
        list_c_district.clear();
        for (int i = 0; i < data.length; i++) {
            if (data[i][2].equals(cStateCd)) {
                list_c_district.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
    }//end of cStateListener

    public void pStateListener(AjaxBehaviorEvent event) {

        String pStateCd = this.to_dobj.getP_state();
        String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
        list_p_district.clear();
        for (int i = 0; i < data.length; i++) {
            if (data[i][2].equals(pStateCd)) {
                list_p_district.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
    }//end of pStateListener

    public void retCheckListener() {
        if (retCheck) {
            setAdvRetCheckDialogue(true);
            //setAdvRetCheckDialogue(true);
            setAdvRegnCheckDialogue(false);

        }

    }

    public void advanceRcptListener() throws Exception {
        try {
            List<Status_dobj> statusList = ServerUtil.applicationStatus(to_dobj.getRegn_no(), to_dobj.getAppl_no(), Util.getUserStateCode());
            int valid_value = 0;
            String pur_cd_d = "";

            if (!statusList.isEmpty() && statusList.size() > 1) {
                for (int i = 0; i < statusList.size(); i++) {
                    pur_cd_d = pur_cd_d + "," + statusList.get(i).getPur_cd();
                }
                pur_cd_d = pur_cd_d + ",";
                if (pur_cd_d.contains("," + TableConstants.VM_TRANSACTION_MAST_TO + ",")) {
                    if (pur_cd_d.contains("," + TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO + ",")) {
                        for (int i = 0; i < statusList.size(); i++) {
                            if (statusList.get(i).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO && (statusList.get(i).getAction_cd() == TableConstants.RE_ASSIGNMENT_OF_REGISTRATION_NO_ENTRY || statusList.get(i).getAction_cd() == TableConstants.TM_ACTION_REGISTERED_VEH_FEE)) {
                                throw new VahanException("Please select the  check box for Advance No OR Retention No at time of Reassign ");
                            }
                        }
                    } else if (pur_cd_d.contains("," + TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION + ",")) {
                        for (int i = 0; i < statusList.size(); i++) {
                            if (statusList.get(i).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION && (statusList.get(i).getAction_cd() == TableConstants.TM_ROLE_CONV_ENTRY || statusList.get(i).getAction_cd() == TableConstants.TM_ACTION_REGISTERED_VEH_FEE)) {
                                throw new VahanException("Please select the  check box for Advance No OR Retention No at time of Conversion ");
                            }
                        }
                    }
                }
            }

            if (retCheck) {
                String rcptno = getRetenRegNoDobj().getRecp_no();
                if (rcptno == null || rcptno.isEmpty()) {
                    return;
                }
                Date rcptDate = NewImpl.getRetainNoRcptDate(rcptno, appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd());
                if (rcptDate != null) {
                    if ("SK".contains(appl_details.getCurrent_state_cd())) {
                        if (appl_details.getOwnerDobj().getOwner_cd() == TableConstants.VEH_TYPE_GOVT
                                || appl_details.getOwnerDobj().getOwner_cd() == TableConstants.VEH_TYPE_STATE_GOVT
                                || appl_details.getOwnerDobj().getOwner_cd() == TableConstants.VEH_TYPE_GOVT_UNDERTAKING) {
                            // Do Nothing
                        } else {
                            try {
                                NewImpl.validationRetainNoRcptDate(Util.getTmConfiguration(), rcptno, rcptDate);
                            } catch (VahanException ex) {
                                setRetRcptMsg(rcptno + " has been expire on " + DateUtil.parseDate(rcptDate) + ". If you want to continue Please pay 5000/year amount at the time of fee, otherwise cancel and proceed !!!");
                                PrimeFaces.current().executeScript("PF('retExpire').show();");
                            }
                        }
                    } else {
                        NewImpl.validationRetainNoRcptDate(Util.getTmConfiguration(), rcptno, rcptDate);
                    }
                    RetenRegnNo_dobj dobj = NewImpl.getRetenRegNoDetails(rcptno);
                    setRetenRegNoDobj(dobj);
                    String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
                    getList_adv_district().clear();

                    for (int i = 0; i < data.length; i++) {
                        if (data[i][2].trim().equals(getRetenRegNoDobj().getState_cd())) {
                            getList_adv_district().add(new SelectItem(data[i][0], data[i][1]));
                        }
                    }
                    if (!to_dobj.getOwner_name().equalsIgnoreCase(dobj.getOwner_name())
                            || !to_dobj.getF_name().equalsIgnoreCase(dobj.getF_name())) {
                        JSFUtils.setFacesMessage("Owner Name and Father Name does not match!", null, JSFUtils.ERROR);
                        RetenRegnNo_dobj dobj1 = new RetenRegnNo_dobj();
                        setRetenRegNoDobj(dobj1);
                    }
                } else {
                    throw new VahanException("Receipt Date Not Found");
                }
            } else if (advRegnCheck) {

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
                    if (!to_dobj.getOwner_name().equalsIgnoreCase(dobj.getOwner_name())
                            || !to_dobj.getF_name().equalsIgnoreCase(dobj.getF_name())) {
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
            RetenRegnNo_dobj dobj1 = new RetenRegnNo_dobj();
            setRetenRegNoDobj(dobj1);
        }
    }

    public void advanceExitListener() {
        if (retCheck) {
            setRetenRegNoDobj(null);
            setRetCheck(false);
        }
        if (advRegnCheck) {
            setAdvRegnNoDobj(null);
            setAdvRegnCheck(false);
        }
    }

    public void advanceSaveListener() {
        //fill details from AdvanceRegnNo_dobj
//        if (getAdvRegnNoDobj().getRegn_no() != null) {
//            setNew_regn_no(getAdvRegnNoDobj().getRegn_no());
//            setDisableRetCheck(true);
//        } else if (getRetenRegNoDobj().getRegn_no() != null) {
//            setNew_regn_no(getRetenRegNoDobj().getRegn_no());
//            setDisableAdvRegnCheck(true);
//        } else {
//            setAdvRegnNoDobj(null);
//            setRetenRegNoDobj(null);
//            setAdvRegnCheck(false);
//            setRetCheck(false);
//        }

        if (getRetenRegNoDobj().getRegn_no() != null) {
            to_dobj.setAssignRetainNo(true);
            to_dobj.setAssignRetainRegnNo(getRetenRegNoDobj().getRegn_no());
        } else if (getAdvRegnNoDobj().getRegn_no() != null) {
            //  to_dobj.setNew_regn_no(getAdvRegnNoDobj().getRegn_no());
            setDisableRetCheck(true);
            to_dobj.setAssignFancyNumber(true);
            to_dobj.setAssignFancyRegnNumber(getAdvRegnNoDobj().getRegn_no());
        } else {
            setAdvRegnNoDobj(null);
            setRetenRegNoDobj(null);
            setAdvRegnCheck(false);
            setRetCheck(false);
        }
    }

    /**
     * @return the list_c_district
     */
    public List getList_c_district() {
        return list_c_district;
    }

    /**
     * @param list_c_district the list_c_district to set
     */
    public void setList_c_district(List list_c_district) {
        this.list_c_district = list_c_district;
    }

    /**
     * @return the list_p_district
     */
    public List getList_p_district() {
        return list_p_district;
    }

    /**
     * @param list_p_district the list_p_district to set
     */
    public void setList_p_district(List list_p_district) {
        this.list_p_district = list_p_district;
    }

    /**
     * @return the list_owner_code
     */
    public List getList_owner_code() {
        return list_owner_code;
    }

    /**
     * @param list_owner_code the list_owner_code to set
     */
    public void setList_owner_code(List list_owner_code) {
        this.list_owner_code = list_owner_code;
    }

    /**
     * @return the to_dobj_prv
     */
    public ToDobj getTo_dobj_prv() {
        return to_dobj_prv;
    }

    /**
     * @param to_dobj_prv the to_dobj_prv to set
     */
    public void setTo_dobj_prv(ToDobj to_dobj_prv) {
        this.to_dobj_prv = to_dobj_prv;
    }

    /**
     * @return the compBeanList
     */
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
     * @return the list_owner_catg
     */
    public List getList_owner_catg() {
        return list_owner_catg;
    }

    /**
     * @param list_owner_catg the list_owner_catg to set
     */
    public void setList_owner_catg(List list_owner_catg) {
        this.list_owner_catg = list_owner_catg;
    }

    /**
     * @return the today
     */
    public Date getToday() {
        return today;
    }

    /**
     * @param today the today to set
     */
    public void setToday(Date today) {
        this.today = today;
    }

    /**
     * @return the purposeType
     */
    public String getPurposeType() {
        return purposeType;
    }

    /**
     * @param purposeType the purposeType to set
     */
    public void setPurposeType(String purposeType) {
        this.purposeType = purposeType;
    }

    /**
     * @return the purposeTypeLabel
     */
    public String getPurposeTypeLabel() {
        return purposeTypeLabel;
    }

    /**
     * @param purposeTypeLabel the purposeTypeLabel to set
     */
    public void setPurposeTypeLabel(String purposeTypeLabel) {
        this.purposeTypeLabel = purposeTypeLabel;
    }

    /**
     * @return the prevChangedDataList
     */
    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return prevChangedDataList;
    }

    /**
     * @param prevChangedDataList the prevChangedDataList to set
     */
    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList;
    }

    /**
     * @return the to_dobj
     */
    public ToDobj getTo_dobj() {
        return to_dobj;
    }

    /**
     * @param to_dobj the to_dobj to set
     */
    public void setTo_dobj(ToDobj to_dobj) {
        this.to_dobj = to_dobj;
    }

    /**
     * @return the option
     */
    public int getOption() {
        return option;
    }

    /**
     * @param option the option to set
     */
    public void setOption(int option) {
        this.option = option;
    }

    /**
     * @return the sameAsCurrAddress
     */
    public boolean isSameAsCurrAddress() {
        return sameAsCurrAddress;
    }

    /**
     * @param sameAsCurrAddress the sameAsCurrAddress to set
     */
    public void setSameAsCurrAddress(boolean sameAsCurrAddress) {
        this.sameAsCurrAddress = sameAsCurrAddress;
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

    /**
     * @return the list_c_state
     */
    public List getList_c_state() {
        return list_c_state;
    }

    /**
     * @param list_c_state the list_c_state to set
     */
    public void setList_c_state(List list_c_state) {
        this.list_c_state = list_c_state;
    }

    /**
     * @return the list_p_state
     */
    public List getList_p_state() {
        return list_p_state;
    }

    /**
     * @param list_p_state the list_p_state to set
     */
    public void setList_p_state(List list_p_state) {
        this.list_p_state = list_p_state;
    }

    /**
     * @return the owner_identification
     */
    public OwnerIdentificationDobj getOwner_identification() {
        return owner_identification;
    }

    /**
     * @param owner_identification the owner_identification to set
     */
    public void setOwner_identification(OwnerIdentificationDobj owner_identification) {
        this.owner_identification = owner_identification;
    }

    /**
     * @return the rendersavebutton
     */
    public boolean isRendersavebutton() {
        return rendersavebutton;
    }

    /**
     * @param rendersavebutton the rendersavebutton to set
     */
    public void setRendersavebutton(boolean rendersavebutton) {
        this.rendersavebutton = rendersavebutton;
    }

    /**
     * @param hypothecationDetails_bean the hypothecationDetails_bean to set
     */
    public void setHypothecationDetails_bean(HypothecationDetailsBean hypothecationDetails_bean) {
        this.hypothecationDetails_bean = hypothecationDetails_bean;
    }

    /**
     * @return the ownerIdentificationPrev
     */
    public OwnerIdentificationDobj getOwnerIdentificationPrev() {
        return ownerIdentificationPrev;
    }

    /**
     * @param ownerIdentificationPrev the ownerIdentificationPrev to set
     */
    public void setOwnerIdentificationPrev(OwnerIdentificationDobj ownerIdentificationPrev) {
        this.ownerIdentificationPrev = ownerIdentificationPrev;
    }

    /**
     * @return the renderApprovePrint
     */
    public boolean isRenderApprovePrint() {
        return renderApprovePrint;
    }

    /**
     * @param renderApprovePrint the renderApprovePrint to set
     */
    public void setRenderApprovePrint(boolean renderApprovePrint) {
        this.renderApprovePrint = renderApprovePrint;
    }

    /**
     * @return the renderApprovePrintSuccession
     */
    public boolean isRenderApprovePrintSuccession() {
        return renderApprovePrintSuccession;
    }

    /**
     * @param renderApprovePrintSuccession the renderApprovePrintSuccession to
     * set
     */
    public void setRenderApprovePrintSuccession(boolean renderApprovePrintSuccession) {
        this.renderApprovePrintSuccession = renderApprovePrintSuccession;
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the boolFName
     */
    public boolean isBoolFName() {
        return boolFName;
    }

    /**
     * @param boolFName the boolFName to set
     */
    public void setBoolFName(boolean boolFName) {
        this.boolFName = boolFName;
    }

    /**
     * @return the boolOwnrCode
     */
    public boolean isBoolOwnrCode() {
        return boolOwnrCode;
    }

    /**
     * @param boolOwnrCode the boolOwnrCode to set
     */
    public void setBoolOwnrCode(boolean boolOwnrCode) {
        this.boolOwnrCode = boolOwnrCode;
    }

    /**
     * @return the boolOwnrCatg
     */
    public boolean isBoolOwnrCatg() {
        return boolOwnrCatg;
    }

    /**
     * @param boolOwnrCatg the boolOwnrCatg to set
     */
    public void setBoolOwnrCatg(boolean boolOwnrCatg) {
        this.boolOwnrCatg = boolOwnrCatg;
    }

    /**
     * @return the boolOwnerName
     */
    public boolean isBoolOwnerName() {
        return boolOwnerName;
    }

    /**
     * @param boolOwnerName the boolOwnerName to set
     */
    public void setBoolOwnerName(boolean boolOwnerName) {
        this.boolOwnerName = boolOwnerName;
    }

    /**
     * @return the minDate
     */
    public Date getMinDate() {
        return minDate;
    }

    /**
     * @param minDate the minDate to set
     */
    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    /**
     * @return the fancyRetention
     */
    public boolean isFancyRetention() {
        return fancyRetention;
    }

    /**
     * @param fancyRetention the fancyRetention to set
     */
    public void setFancyRetention(boolean fancyRetention) {
        this.fancyRetention = fancyRetention;
    }

    /**
     * @return the selectedFancyRetnetion
     */
    public String getSelectedFancyRetnetion() {
        return selectedFancyRetnetion;
    }

    /**
     * @param selectedFancyRetnetion the selectedFancyRetnetion to set
     */
    public void setSelectedFancyRetnetion(String selectedFancyRetnetion) {
        this.selectedFancyRetnetion = selectedFancyRetnetion;
    }

    /**
     * @return the retAllowed
     */
    public boolean isRetAllowed() {
        return retAllowed;
    }

    /**
     * @param retAllowed the retAllowed to set
     */
    public void setRetAllowed(boolean retAllowed) {
        this.retAllowed = retAllowed;
    }

    /**
     * @return the retCheck
     */
    public boolean isRetCheck() {
        return retCheck;
    }

    /**
     * @param retCheck the retCheck to set
     */
    public void setRetCheck(boolean retCheck) {
        this.retCheck = retCheck;
    }

    /**
     * @return the disableRetCheck
     */
    public boolean isDisableRetCheck() {
        return disableRetCheck;
    }

    /**
     * @param disableRetCheck the disableRetCheck to set
     */
    public void setDisableRetCheck(boolean disableRetCheck) {
        this.disableRetCheck = disableRetCheck;
    }

    /**
     * @return the advRetCheckDialogue
     */
    public boolean isAdvRetCheckDialogue() {
        return advRetCheckDialogue;
    }

    /**
     * @param advRetCheckDialogue the advRetCheckDialogue to set
     */
    public void setAdvRetCheckDialogue(boolean advRetCheckDialogue) {
        this.advRetCheckDialogue = advRetCheckDialogue;
    }

    /**
     * @return the retenRegNoDobj
     */
    public RetenRegnNo_dobj getRetenRegNoDobj() {
        return retenRegNoDobj;
    }

    /**
     * @param retenRegNoDobj the retenRegNoDobj to set
     */
    public void setRetenRegNoDobj(RetenRegnNo_dobj retenRegNoDobj) {
        this.retenRegNoDobj = retenRegNoDobj;
    }

    /**
     * @return the list_adv_district
     */
    public List getList_adv_district() {
        return list_adv_district;
    }

    /**
     * @param list_adv_district the list_adv_district to set
     */
    public void setList_adv_district(List list_adv_district) {
        this.list_adv_district = list_adv_district;
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

    public void advanceCheckListener() {
        if (advRegnCheck) {
            advRegnCheckDialogue = true;
            retCheck = false;

        }
    }

    /**
     * @return the auctionVisibilityTab
     */
    public boolean isAuctionVisibilityTab() {
        return auctionVisibilityTab;
    }

    /**
     * @param auctionVisibilityTab the auctionVisibilityTab to set
     */
    public void setAuctionVisibilityTab(boolean auctionVisibilityTab) {
        this.auctionVisibilityTab = auctionVisibilityTab;
    }

    /**
     * @return the auctionDobj
     */
    public AuctionDobj getAuctionDobj() {
        return auctionDobj;
    }

    /**
     * @param auctionDobj the auctionDobj to set
     */
    public void setAuctionDobj(AuctionDobj auctionDobj) {
        this.auctionDobj = auctionDobj;
    }

    /**
     * @return the disableRetSelectOneMenu
     */
    public boolean isDisableRetSelectOneMenu() {
        return disableRetSelectOneMenu;
    }

    /**
     * @param disableRetSelectOneMenu the disableRetSelectOneMenu to set
     */
    public void setDisableRetSelectOneMenu(boolean disableRetSelectOneMenu) {
        this.disableRetSelectOneMenu = disableRetSelectOneMenu;
    }

    public String getRetRcptMsg() {
        return retRcptMsg;
    }

    public void setRetRcptMsg(String retRcptMsg) {
        this.retRcptMsg = retRcptMsg;
    }
}
