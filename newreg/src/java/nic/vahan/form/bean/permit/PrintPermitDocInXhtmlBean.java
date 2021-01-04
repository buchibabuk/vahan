/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.permit.AITPStateFeeDraftDobj;
import nic.vahan.form.dobj.permit.CounterSignatureDobj;
import nic.vahan.form.dobj.permit.OtherStateVchCounterSignDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitCheckDetailsDobj;
import nic.vahan.form.dobj.permit.PermitFeeDobj;
import nic.vahan.form.dobj.permit.PermitLeaseDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.dobj.permit.PermitPaidFeeDtlsDobj;
import nic.vahan.form.dobj.permit.PermitTimeTableDobj;
import nic.vahan.form.dobj.permit.SpecialRoutePermitDobj;
import nic.vahan.form.dobj.permit.TemporaryPermitDobj;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.CounterSignatureAuthImpl;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.form.impl.permit.PermitEndorsementAppImpl;
import nic.vahan.form.impl.permit.PermitPaidFeeDtlsImpl;
import nic.vahan.form.impl.permit.PrintPermitDocInXhtmlImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "printDocXHTML")
@RequestScoped
public class PrintPermitDocInXhtmlBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PrintPermitDocInXhtmlBean.class);
    private String region_covered = "";
    private boolean renderLengh = false;
    private PassengerPermitDetailDobj passPmtDobj = null;
    private CounterSignatureDobj counterDobj = null;
    private PermitOwnerDetailDobj ownerPmtDobj = null;
    private PermitFeeDobj pmtFeeDobj = null;
    private TemporaryPermitDobj tempPmtDobj = null;
    PrintPermitDocInXhtmlImpl pmtDocImpl = null;
    private List<PassengerPermitDetailDobj> routedata = null;
    private List<OwnerDetailsDobj> trailer_data = null;
    private List<SpecialRoutePermitDobj> splroutedata = null;
    private String specialFeeDetail = null;
    private String purCdDescr = "";
    private String noteAccForState = "";
    private String allIndiaTag = "All Over India";
    private String offDesce = "";
    private String ruleAndSection = "";
    private String formName = "";
    private String main_header = "";
    private String main_footer = "";
    private String footerSignature = "";
    private boolean footerRender = false;
    private String pmtSubHeading = "";
    private String pmtAuthHeading = "";
    private String routeData;
    private boolean previousOwnerDtlsShow = false;
    private String textQRcode = "";
    private boolean rule = false;
    private String fncr_name = "";
    private int pay_load;
    private String reDirect;
    List<PermitPaidFeeDtlsDobj> paidFeeList = new ArrayList<>();
    private List<PermitLeaseDobj> leasePermit = new ArrayList<>();
    private int totalAmount = 0;
    List<HpaDobj> hypth = null;
    private String vahanMessages = null;
    private SessionVariables sessionVariables = null;
    private String dupReason = "";
    private boolean dupReasonRender = false;
    private boolean purposeRender = false;
    private String opDateInString = null;
    private PermitCheckDetailsDobj dtlsDobj = null;
    private String pmtTypeCatgHeader = null;
    private StreamedContent viewSignFileOff1;
    private byte[] signApprovedOff;
    private String financerAddress = null;
    private String leaseAgreementDate = null;
    TmConfigurationDobj configurationDobj = null;
    private String image_background;
    private String image_logo;
    private boolean show_image_background;
    private boolean show_image_logo;
    private int offer_validity_days;
    private OtherStateVchCounterSignDobj vchForCSDobj;
    private boolean renderParking = false;
    private boolean renderRouteFlag;
    private String routeFlagCondition;
    private boolean otherRouteAllow;
    private List<PassengerPermitDetailDobj> prv_route_list = new ArrayList();
    private boolean renderUPPermitDetail = false;
    private List<AITPStateFeeDraftDobj> aitpPaymentdata = null;
    private String overlappingRoute = "";
    private boolean renderNocPrintCondTP = false;
    private boolean renderNocPrintCondRV = false;
    private boolean renderNocPrintCondTR = false;
    private boolean sepratePrintOfNocSurrenderSlip = false;
    private boolean renderCancelPrintCondML = false;
    private boolean renderPmtnoAndRoutedtl = false;
    private String interStateName = null;
    private String conditionPmtnoAndRoute;
    private boolean isSTAOffice = false;
    private boolean renderManipurPrintCond = false;
    private boolean renderPmtType = false;
    private boolean renderPmtCatg = false;
    private boolean renderPmtCatgInterState = false;
    private boolean renderPmtSpecial = false;
    private boolean renderPmtVehicleClass = false;
    private boolean renderThreeWheelerCond = false;
    private boolean renderManipurStaPrintCond = false;
    private String tempFormHeading = null;
    private String splpFormHeading = null;
    private boolean renderManipurCatgPrintCond = false;
    private boolean renderMnStaCGPmtPrintCond = false;
    private boolean renderMnStaSCPmtPrintCond = false;
    private boolean renderMnGoodsPmtPrintCond = false;
    private boolean renderMnCGPmtPrintCond = false;
    private boolean renderWbCGAuthPrintCond = false;
    private boolean renderWbSCPrintCond = false;
    private boolean renderMnSCRegPrintCond = false;
    private boolean renderMnRoutePrintCond = false;
    private boolean renderWBPrint = false;
    private boolean renderASPrint = false;
    private String registered_office = null;
    Map splConfig = null;
    private List<PermitTimeTableDobj> routeTimeTableData;
    private boolean showTimeTableDetails = false;
    private boolean renderPYprintCond = false;
    private boolean renderMotorVehicleClass = false;
    private boolean renderMaxiVehicleClass = false;
    private boolean renderPYRouteCodeFlag = true;
    private boolean renderBRPrint = false;

    public PrintPermitDocInXhtmlBean() {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
            vahanMessages = "Session time Out";
            return;
        }

        HttpSession ses = Util.getSession();
        String permitType = ses.getAttribute("permitPrintDocPermitType").toString();
        String applNo = ses.getAttribute("permitPrintDocApplNo").toString();
        String purCd = ses.getAttribute("permitPrintDocPurCd").toString();
        String regnNo = ses.getAttribute("permitPrintDocRegnNo").toString();
        String documentId = ses.getAttribute("permitPrintDocID").toString();
        String curApplNo = !CommonUtils.isNullOrBlank(String.valueOf(ses.getAttribute("permitCurrentApplNo"))) ? ses.getAttribute("permitCurrentApplNo").toString() : null;
        Map stateConfig = CommonPermitPrintImpl.getVmPermitStateConfiguration(sessionVariables.getStateCodeSelected());
        splConfig = CommonPermitPrintImpl.getTmSpecialPmtStateConfiguration(sessionVariables.getStateCodeSelected());
        otherRouteAllow = Boolean.parseBoolean(stateConfig.get("other_office_route_allow").toString());
        try {
            VehicleParameters vh_parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
            vh_parameters.setPERMIT_TYPE(Integer.parseInt(permitType));
            if (isCondition(FormulaUtils.replaceTagPermitValues(String.valueOf(stateConfig.get("show_time_table")), vh_parameters), "onSelectPermitType")) {
                setShowTimeTableDetails(true);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        int offerValidityDays = Integer.parseInt(stateConfig.get("offer_validity_days").toString());
        String counter_sign_heading = stateConfig.get("cs_formno_heading").toString();
        this.setOffer_validity_days(offerValidityDays);
        setRouteFlagCondition(stateConfig.get("route_flag_condition").toString());
        reDirect = ses.getAttribute("OfferRedirect").toString();
        passPmtDobj = new PassengerPermitDetailDobj();
        counterDobj = new CounterSignatureDobj();
        ownerPmtDobj = new PermitOwnerDetailDobj();
        vchForCSDobj = new OtherStateVchCounterSignDobj();
        pmtFeeDobj = new PermitFeeDobj();
        tempPmtDobj = new TemporaryPermitDobj();
        routedata = new ArrayList<PassengerPermitDetailDobj>();
        trailer_data = new ArrayList<OwnerDetailsDobj>();
        splroutedata = new ArrayList<SpecialRoutePermitDobj>();
        aitpPaymentdata = new ArrayList<AITPStateFeeDraftDobj>();
        conditionPmtnoAndRoute = stateConfig.get("show_other_state_route").toString();
        String[][] data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].equalsIgnoreCase(purCd.trim())) {
                purCdDescr = data[i][1].toUpperCase();
                break;
            }
        }
        if (Integer.valueOf(purCd) == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD && !(permitType.equalsIgnoreCase(TableConstants.NATIONAL_PERMIT) || permitType.equalsIgnoreCase(TableConstants.AITP))) {
            setPurposeRender(false);
        } else {
            setPurposeRender(true);
        }
        if (Integer.valueOf(purCd) == TableConstants.VM_PMT_DUPLICATE_PUR_CD || Integer.valueOf(purCd) == TableConstants.VM_PMT_DUP_COUNTER_SIGNATURE_PUR_CD) {
            dupReasonRender = true;
            dupReason = new PrintPermitDocInXhtmlImpl().getDupReason(regnNo, Integer.valueOf(purCd));
        }

        data = MasterTableFiller.masterTables.TM_OFFICE.getData();
        for (int i = 0; i < data.length; i++) {
            if ((Integer.valueOf(data[i][0]) == sessionVariables.getOffCodeSelected())
                    && (data[i][13].equalsIgnoreCase(sessionVariables.getStateCodeSelected()))) {
                offDesce = data[i][1].toUpperCase();
                break;
            }
        }

        if (Integer.valueOf(purCd) == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD
                || Integer.valueOf(purCd) == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD) {
            previousOwnerDtlsShow = true;
            pmtDocImpl = new PrintPermitDocInXhtmlImpl();
            Map<String, String> getmap = pmtDocImpl.getPreviousOwnerDetails(regnNo, Integer.valueOf(purCd));
            ownerPmtDobj.setPreviousOwnerName(getmap.get("owner_name"));
            ownerPmtDobj.setPreviousRegnNo(getmap.get("regn_no"));
            ownerPmtDobj.setPreviousFatherName(getmap.get("f_name"));
        }
        if (Integer.valueOf(purCd) == TableConstants.VM_PMT_LEASE_PUR_CD) {
            pmtDocImpl = new PrintPermitDocInXhtmlImpl();
            setLeasePermit(pmtDocImpl.getLeasePermitDetails(regnNo, applNo));
        }
        if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("UK")) {
            allIndiaTag += " OR All Uttarakhaand Excluding Hill OR All Uttarakhand Including Hill";
            rule = true;
        }
        if (Integer.valueOf(purCd) == TableConstants.VM_PMT_DUPLICATE_PUR_CD || Integer.valueOf(purCd) == TableConstants.VM_PMT_CA_PUR_CD
                || Integer.valueOf(purCd) == TableConstants.VM_PMT_TRANSFER_PUR_CD || Integer.valueOf(purCd) == TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD
                || Integer.valueOf(purCd) == TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD || Integer.valueOf(purCd) == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD || Integer.valueOf(purCd) == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD) {
            setSignApprovedOff(pmtDocImpl.getSignDetailsForPermit(regnNo, curApplNo, sessionVariables.getStateCodeSelected()));
        } else {
            setSignApprovedOff(pmtDocImpl.getSignDetailsForPermit(regnNo, applNo, sessionVariables.getStateCodeSelected()));
        }
        if (getSignApprovedOff() != null) {
            setViewSignFileOff1(new DefaultStreamedContent(new ByteArrayInputStream(getSignApprovedOff())));
        } else {
            setViewSignFileOff1(null);
        }
        try {
            configurationDobj = Util.getTmConfiguration();
        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", new FacesMessage(FacesMessage.SEVERITY_INFO, "", e.getMessage()));
        }
        if (configurationDobj.getTmPrintConfgDobj() != null) {
            if (configurationDobj.getTmPrintConfgDobj().getImage_background() != null && !configurationDobj.getTmPrintConfgDobj().getImage_background().isEmpty()) {
                setImage_background(configurationDobj.getTmPrintConfgDobj().getImage_background());
                setShow_image_background(true);
            } else {
                setShow_image_background(false);
            }
            if (configurationDobj.getTmPrintConfgDobj().getImage_logo() != null && !configurationDobj.getTmPrintConfgDobj().getImage_logo().isEmpty()) {
                setImage_logo(configurationDobj.getTmPrintConfgDobj().getImage_logo());
                setShow_image_logo(true);
            } else {
                setShow_image_logo(false);
            }
        }
        getHeadingFooterNote(Integer.valueOf(purCd), Integer.valueOf(permitType), documentId, regnNo);
        if (documentId.equalsIgnoreCase("4")) {
            permitRegister(applNo);
        } else if (documentId.equalsIgnoreCase("11")) {
            permitAckDetails(applNo, regnNo, documentId);
        } else if (documentId.equalsIgnoreCase("9")) {
            surrenderSlipDtls(applNo, documentId);
        } else if (documentId.equalsIgnoreCase("8")) {
            permitNocDetails(applNo, documentId);
        } else if (documentId.equalsIgnoreCase("16")) {
            CounterSignatureAuthorizationDetails(applNo);
        } else if (Integer.valueOf(purCd) == TableConstants.VM_PMT_TEMP_PUR_CD || Integer.valueOf(purCd) == TableConstants.VM_PMT_RENEW_TEMP_PUR_CD || Integer.valueOf(purCd) == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
            tempSpecialPermitDetails(applNo);
        } else if (Integer.valueOf(purCd) == TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD
                || Integer.valueOf(purCd) == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD || documentId.equalsIgnoreCase("5") || documentId.equalsIgnoreCase("12")) {
            homeAuthDetails(applNo, regnNo, Integer.valueOf(purCd), documentId);
        } else if (Integer.valueOf(purCd) == TableConstants.VM_PMT_APPLICATION_PUR_CD
                || documentId.equalsIgnoreCase("3")) {
            offerLetterDetails(applNo, regnNo);
        } else if (Integer.valueOf(purCd) == TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD || Integer.valueOf(purCd) == TableConstants.VM_PMT_DUP_COUNTER_SIGNATURE_PUR_CD || Integer.valueOf(purCd) == TableConstants.VM_PMT_RENEW_COUNTER_SIGNATURE_PUR_CD) {
            setFormName(counter_sign_heading);
            counterSignatureDetails(applNo);
        } else if (Integer.valueOf(purCd) == TableConstants.VM_PMT_FRESH_PUR_CD || Integer.valueOf(purCd) == TableConstants.VM_PMT_RENEWAL_PUR_CD
                || Integer.valueOf(purCd) == TableConstants.VM_PMT_DUPLICATE_PUR_CD || Integer.valueOf(purCd) == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD
                || Integer.valueOf(purCd) == TableConstants.VM_PMT_TRANSFER_PUR_CD || Integer.valueOf(purCd) == TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD
                || Integer.valueOf(purCd) == TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD || Integer.valueOf(purCd) == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD
                || Integer.valueOf(purCd) == TableConstants.VM_PMT_CA_PUR_CD || Integer.valueOf(purCd) == TableConstants.VM_PMT_SUSPENSION_PUR_CD
                || Integer.valueOf(purCd) == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD || Integer.valueOf(purCd) == TableConstants.VM_PMT_LEASE_PUR_CD || Integer.valueOf(purCd) == TableConstants.VM_PMT_TEMP_SUR_PUR_CD
                || Integer.valueOf(purCd) == TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD) {
            switch (Integer.valueOf(permitType)) {
                case 101: {
                    stageCarriagePermit(applNo);
                    break;
                }
                case 102: {
                    contractCarriagePermit(applNo);
                    try {
                        PassengerPermitDetailDobj dobj = new PassengerPermitDetailDobj();
                        dobj.setPmt_type_code(passPmtDobj.getPmt_type_code());
                        dobj.setPmtCatg(passPmtDobj.getPmt_catg_code());
                        VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, dobj, 0, 0, 0, 0, 0, 0, 0, 0);
                        if (!CommonUtils.isNullOrBlank(stateConfig.get("pmt_type_header_on_print").toString())) {
                            String pmt_type_header = stateConfig.get("pmt_type_header_on_print").toString();
                            pmtTypeCatgHeader = FormulaUtils.getSqlFormulaValue(FormulaUtils.replaceTagPermitValues(pmt_type_header, parameters), "pmtTypeCatgHeader_PrintPermitDocInXhtmlBean");
                            if (CommonUtils.isNullOrBlank(pmtTypeCatgHeader)) {
                                setPmtTypeCatgHeader(null);
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    }
                    break;
                }
                case 103: {
                    AllIndiaTouristPermit(applNo);
                    break;
                }
                case 104: {
                    privateServiceVehiclePermit(applNo);
                    break;
                }
                case 105: {
                    goodsCarriagePermit(applNo);
                    break;
                }
                case 106: {
                    nationalGoodsPermit(applNo);
                    break;
                }
            }
        }
    }

    public final void permitRegister(String applNo) {
        try {
            pmtDocImpl = new PrintPermitDocInXhtmlImpl();
            Map<String, String> getmap = pmtDocImpl.getGoodsCarriageDetail(applNo);
            setRoutedata(pmtDocImpl.getRouteData(applNo, getmap.get("state_cd"), Integer.valueOf(getmap.get("off_cd")), TableList.vt_permit_route, otherRouteAllow));
            if (getRoutedata() != null) {
                for (PassengerPermitDetailDobj dobj : routedata) {
                    if (Integer.parseInt(dobj.getRout_length()) > 0) {
                        setRenderLengh(true);
                    }
                    if (!CommonUtils.isNullOrBlank(dobj.getNhOverlapping())) {
                        overlappingRoute += dobj.getNhOverlapping() + " within " + dobj.getNhOverlappingLength() + " Km. ";
                    }
                }
                if (getRoutedata().get(0).getNumberOfTrips().equals("0")) {
                    passPmtDobj.setNumberOfTrips(null);
                } else {
                    passPmtDobj.setNumberOfTrips(getRoutedata().get(0).getNumberOfTrips());
                }
            }
            VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
            parameters.setPERMIT_TYPE(Integer.parseInt(getmap.get("pmt_type_code")));
            parameters.setPERMIT_SUB_CATG(Integer.parseInt(getmap.get("pmt_catg_code")));
            if (isCondition(FormulaUtils.replaceTagPermitValues(getRouteFlagCondition(), parameters), "permitRegister")) {
                setRenderRouteFlag(true);
            }
            if (!CommonUtils.isNullOrBlank(conditionPmtnoAndRoute)) {
                if (Boolean.parseBoolean(FormulaUtils.getSqlFormulaValue(FormulaUtils.replaceTagPermitValues(conditionPmtnoAndRoute, parameters), "stageCarriagePermit"))) {
                    renderPmtnoAndRoutedtl = true;
                }
            }
            passPmtDobj.setRegion_covered(pmtDocImpl.getRegionDetail(applNo, getmap.get("state_cd"), Integer.valueOf(getmap.get("off_cd")), TableList.VT_PERMIT));
            passPmtDobj.setOff_cd(getOffDesce());

            if (getmap.get("fncr_name").equals("")) {
                setFncr_name(null);
            } else {
                setFncr_name(getmap.get("fncr_name"));
            }
            if (getmap.get("service_type_descr").equals("") || getmap.get("service_type_descr").equalsIgnoreCase("Select Services Type")) {
                passPmtDobj.setServices_TYPE(null);
            } else {
                passPmtDobj.setServices_TYPE(getmap.get("service_type_descr"));
            }
            passPmtDobj.setState_cd(getmap.get("state_name"));
            passPmtDobj.setPmt_no(getmap.get("pmt_no"));
            passPmtDobj.setGoods_TO_CARRY(getmap.get("goods_to_carry"));
            passPmtDobj.setValidFromInString(getmap.get("valid_from"));
            passPmtDobj.setValidUptoInString(getmap.get("valid_upto"));
            passPmtDobj.setReplaceDateInString(getmap.get("replace_date"));
            passPmtDobj.setPmt_type(getmap.get("pmt_type").toUpperCase());
            if (!CommonUtils.isNullOrBlank(getmap.get("pmt_catg"))) {
                passPmtDobj.setPmtCatg(" (" + getmap.get("pmt_catg").toUpperCase() + ")");
            }
            ownerPmtDobj.setState_cd(getmap.get("state_cd"));
            ownerPmtDobj.setOwner_name(getmap.get("owner_name").toUpperCase());
            ownerPmtDobj.setF_name(getmap.get("f_name").toUpperCase());
            ownerPmtDobj.setC_add1(getmap.get("c_add1").toUpperCase());
            ownerPmtDobj.setC_add2(getmap.get("c_add2").toUpperCase());
            ownerPmtDobj.setC_add3(getmap.get("c_add3").toUpperCase());
            ownerPmtDobj.setC_state(getmap.get("c_state_name"));
            ownerPmtDobj.setC_pincode(Integer.parseInt(getmap.get("c_pincode")));
            ownerPmtDobj.setLd_wt(Integer.parseInt(getmap.get("ld_wt")));
            ownerPmtDobj.setUnld_wt(Integer.parseInt(getmap.get("unld_wt")));
            ownerPmtDobj.setRegn_no(getmap.get("regn_no"));
            ownerPmtDobj.setCurrentDateInString(getmap.get("currentdate"));
            ownerPmtDobj.setEngnNo(getmap.get("eng_no"));
            ownerPmtDobj.setMakerName(getmap.get("maker_name"));
            ownerPmtDobj.setModelName(getmap.get("model_name"));
            ownerPmtDobj.setDistrictInString(getmap.get("c_district_name"));
            ownerPmtDobj.setRegnDateInString(getmap.get("regn_dt"));
            ownerPmtDobj.setNorms(getmap.get("norms_descr"));
            pmtFeeDobj.setChasi_no(getmap.get("chasi_no"));
            pmtFeeDobj.setSeat_cap(Integer.parseInt(getmap.get("seat_cap")));

            if (getmap.get("stand_cap").equals("")) {
                pmtFeeDobj.setStand_cap(0);
            } else {
                pmtFeeDobj.setStand_cap(Integer.parseInt(getmap.get("stand_cap")));
            }
            pmtFeeDobj.setVh_class(getmap.get("vh_class_desc"));
            ownerPmtDobj.setCurrentDateInString(getmap.get("currentdate"));
            setTextQRcode("Application No: " + applNo.toUpperCase() + " \\nValid Upto: " + getmap.get("valid_upto") + " \\nPermit No: " + getmap.get("pmt_no"));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public final void CounterSignatureAuthorizationDetails(String applNo) {
        try {
            CounterSignatureAuthImpl pmtDocImpl = new CounterSignatureAuthImpl();
            Map<String, String> getmap = pmtDocImpl.getCounterSignatureAuthorizationDetails(applNo);
            ownerPmtDobj.setState_cd(getmap.get("state_cd"));
            passPmtDobj.setApplNo(applNo);
            passPmtDobj.setRegnNo(getmap.get("regn_no"));
            passPmtDobj.setState_cd(getmap.get("state_name").toUpperCase());
            passPmtDobj.setOffer_no(getmap.get("count_sign_auth_no").toUpperCase());
            passPmtDobj.setPmt_no(getmap.get("pmt_no").toUpperCase());
            passPmtDobj.setState_cd_to(getmap.get("state_cd_to"));
            counterDobj.setOff_to_name(getmap.get("off_cd_to"));
            passPmtDobj.setValidUptoInString(getmap.get("count_valid_upto"));
            passPmtDobj.setValidFromInString(getmap.get("pmt_valid_from"));
            counterDobj.setState_cd_from(getmap.get("state_name"));
            counterDobj.setState_to(getmap.get("state_name_to"));
            passPmtDobj.setPmt_type(getmap.get("pmt_type"));
            passPmtDobj.setOff_cd(getmap.get("off_name"));
            passPmtDobj.setFloc(getmap.get("floc"));
            passPmtDobj.setVia(getmap.get("via"));
            passPmtDobj.setTloc(getmap.get("tloc"));
            ownerPmtDobj.setCurrentDateInString(getmap.get("currentdate"));
            ownerPmtDobj.setOwner_name(getmap.get("owner_name"));
            ownerPmtDobj.setF_name(getmap.get("f_name"));
            ownerPmtDobj.setC_add1(getmap.get("c_add1"));
            ownerPmtDobj.setC_add2(getmap.get("c_add2"));
            ownerPmtDobj.setC_add3(getmap.get("c_add3"));
            ownerPmtDobj.setDistrictInString(getmap.get("c_district_name"));
            ownerPmtDobj.setC_pincode(Integer.parseInt(getmap.get("c_pincode")));
            if (getmap.get("state_cd").equalsIgnoreCase("WB") && (getmap.get("off_cd").equalsIgnoreCase("999"))) {
                renderWbCGAuthPrintCond = true;
            }
            setTextQRcode("Regn No: " + getmap.get("regn_no").toUpperCase() + " \\nApplication No: " + applNo.toUpperCase() + " \\nValid Upto: " + getmap.get("count_valid_upto") + " \\nCS Auth No: " + getmap.get("count_sign_auth_no"));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public final void nationalGoodsPermit(String applNo) {
        try {
            pmtDocImpl = new PrintPermitDocInXhtmlImpl();
            Map<String, String> getmap = pmtDocImpl.getNationalAndAllIndiaDetail(applNo);
            if (!CommonUtils.isNullOrBlank(getmap.get("region_covered"))) {
                passPmtDobj.setRegion_covered(pmtDocImpl.getRegionDetail(applNo, getmap.get("state_cd"), Integer.valueOf(getmap.get("off_cd")), TableList.VT_PERMIT));
            }
            if (CommonUtils.isNullOrBlank(getmap.get("service_type_descr")) || getmap.get("service_type_descr").equalsIgnoreCase("Select Services Type")) {
                passPmtDobj.setServices_TYPE(null);
            } else {
                passPmtDobj.setServices_TYPE(getmap.get("service_type_descr"));
            }
            passPmtDobj.setOff_cd(getOffDesce());
            passPmtDobj.setState_cd(getmap.get("state_name"));
            passPmtDobj.setPmt_no(getmap.get("pmt_no"));
            passPmtDobj.setValidFromInString(getmap.get("valid_from"));
            passPmtDobj.setValidUptoInString(getmap.get("valid_upto"));
            passPmtDobj.setGoods_TO_CARRY(getmap.get("goods_to_carry"));
            passPmtDobj.setPmt_type(getmap.get("pmt_type").toUpperCase());
            if (!CommonUtils.isNullOrBlank(getmap.get("pmt_catg"))) {
                passPmtDobj.setPmtCatg(" (" + getmap.get("pmt_catg").toUpperCase() + ")");
            }
            passPmtDobj.setPmtCatg(getmap.get("pmt_catg").toUpperCase());
            ownerPmtDobj.setOwner_name(getmap.get("owner_name").toUpperCase());
            ownerPmtDobj.setF_name(getmap.get("f_name").toUpperCase());
            ownerPmtDobj.setC_add1(getmap.get("c_add1").toUpperCase());
            ownerPmtDobj.setC_add2(getmap.get("c_add2").toUpperCase());
            ownerPmtDobj.setC_add3(getmap.get("c_add3").toUpperCase());
            ownerPmtDobj.setC_state(getmap.get("c_state_name").toUpperCase());
            ownerPmtDobj.setC_pincode(Integer.parseInt(getmap.get("c_pincode")));
            ownerPmtDobj.setChasi_no(getmap.get("chasi_no"));
            ownerPmtDobj.setEngnNo(getmap.get("eng_no"));
            ownerPmtDobj.setLd_wt(Integer.parseInt(getmap.get("ld_wt")));
            ownerPmtDobj.setGcw(Integer.parseInt(getmap.get("gcw")));
            ownerPmtDobj.setUnld_wt(Integer.parseInt(getmap.get("unld_wt")));
            ownerPmtDobj.setRegn_no(getmap.get("regn_no"));
            ownerPmtDobj.setRegnDateInString(getmap.get("regn_dt"));
            ownerPmtDobj.setManuYearInString(getmap.get("manu_yr"));
            ownerPmtDobj.setDistrictInString(getmap.get("c_district_name"));
            ownerPmtDobj.setCurrentDateInString(getmap.get("currentdate"));
            ownerPmtDobj.setMakerName(getmap.get("maker_name"));
            ownerPmtDobj.setModelName(getmap.get("model_name"));
            pmtFeeDobj.setVh_class(getmap.get("vh_class_desc"));
            if (CommonUtils.isNullOrBlank(getmap.get("seat_cap"))) {
                pmtFeeDobj.setSeat_cap(0);
            } else {
                pmtFeeDobj.setSeat_cap(Integer.valueOf(getmap.get("seat_cap")));
            }
            tempPmtDobj.setPmt_no(getmap.get("auth_no"));
            tempPmtDobj.setPrv_valid_fr(getmap.get("auth_fr"));
            tempPmtDobj.setPrv_valid_to(getmap.get("auth_to"));
            ownerPmtDobj.setState_cd(sessionVariables.getStateCodeSelected());
            setTextQRcode("Regn No: " + getmap.get("regn_no").toUpperCase() + " \\nApplication No: " + applNo.toUpperCase() + " \\nValid Upto: " + getmap.get("valid_upto") + " \\nPermit No: " + getmap.get("pmt_no"));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public final void AllIndiaTouristPermit(String applNo) {
        try {
            pmtDocImpl = new PrintPermitDocInXhtmlImpl();
            footerRender = true;
            Map<String, String> getmap = pmtDocImpl.getNationalAndAllIndiaDetail(applNo);
            if (!CommonUtils.isNullOrBlank(getmap.get("region_covered"))) {
                passPmtDobj.setRegion_covered(pmtDocImpl.getRegionDetailStateVise(applNo, getmap.get("region_covered")));
            }
            if ((!CommonUtils.isNullOrBlank(getmap.get("region_covered"))) && CommonUtils.isNullOrBlank(passPmtDobj.getRegion_covered())) {
                passPmtDobj.setRegion_covered(pmtDocImpl.getRegionDetail(applNo, getmap.get("state_cd"), Integer.valueOf(getmap.get("off_cd")), TableList.VT_PERMIT));
            }

            if (CommonUtils.isNullOrBlank(getmap.get("fncr_name"))) {
                setFncr_name(null);
            } else {
                setFncr_name(getmap.get("fncr_name"));
            }
            if (CommonUtils.isNullOrBlank(getmap.get("service_type_descr")) || getmap.get("service_type_descr").equalsIgnoreCase("Select Services Type")) {
                passPmtDobj.setServices_TYPE(null);
            } else {
                passPmtDobj.setServices_TYPE(getmap.get("service_type_descr"));
            }
            passPmtDobj.setOff_cd(getOffDesce());
            passPmtDobj.setState_cd(getmap.get("state_name"));
            passPmtDobj.setPmt_no(getmap.get("pmt_no"));
            passPmtDobj.setValidFromInString(getmap.get("valid_from"));
            passPmtDobj.setValidUptoInString(getmap.get("valid_upto"));
            passPmtDobj.setGoods_TO_CARRY(getmap.get("goods_to_carry"));
            if (("PY").contains(sessionVariables.getStateCodeSelected()) && !CommonUtils.isNullOrBlank(getmap.get("pmt_type")) && getmap.get("pmt_type_code").equals(TableConstants.AITP)) {
                passPmtDobj.setPmt_type("particular tourist vehicle");
            } else {
                if (!CommonUtils.isNullOrBlank(getmap.get("pmt_type"))) {
                    passPmtDobj.setPmt_type(getmap.get("pmt_type").toUpperCase());
                }
            }
            if (CommonUtils.isNullOrBlank(getmap.get("replace_date"))) {
                passPmtDobj.setReplaceDateInString(null);
            } else {
                passPmtDobj.setReplaceDateInString(getmap.get("replace_date").toUpperCase());
            }
            if (!CommonUtils.isNullOrBlank(getmap.get("pmt_catg"))) {
                passPmtDobj.setPmtCatg(" (" + getmap.get("pmt_catg").toUpperCase() + ")");
            }
            ownerPmtDobj.setOwner_name(getmap.get("owner_name").toUpperCase());
            ownerPmtDobj.setF_name(getmap.get("f_name").toUpperCase());
            ownerPmtDobj.setC_add1(getmap.get("c_add1").toUpperCase());
            ownerPmtDobj.setC_add2(getmap.get("c_add2").toUpperCase());
            ownerPmtDobj.setC_add3(getmap.get("c_add3").toUpperCase());
            ownerPmtDobj.setC_state(getmap.get("c_state_name").toUpperCase());
            if (CommonUtils.isNullOrBlank(getmap.get("c_pincode"))) {
                ownerPmtDobj.setC_pincode(0);
            } else {
                ownerPmtDobj.setC_pincode(Integer.parseInt(getmap.get("c_pincode")));
            }

            if (CommonUtils.isNullOrBlank(getmap.get("ld_wt"))) {
                ownerPmtDobj.setLd_wt(0);
            } else {
                ownerPmtDobj.setLd_wt(Integer.parseInt(getmap.get("c_pincode")));
            }

            if (CommonUtils.isNullOrBlank(getmap.get("unld_wt"))) {
                ownerPmtDobj.setUnld_wt(0);
            } else {
                ownerPmtDobj.setUnld_wt(Integer.parseInt(getmap.get("unld_wt")));
            }
            ownerPmtDobj.setRegn_no(getmap.get("regn_no"));
            ownerPmtDobj.setRegnDateInString(getmap.get("regn_dt"));
            ownerPmtDobj.setManuYearInString(getmap.get("manu_yr"));
            ownerPmtDobj.setDistrictInString(getmap.get("c_district_name"));
            ownerPmtDobj.setCurrentDateInString(getmap.get("currentdate"));
            ownerPmtDobj.setModelName(getmap.get("model_name"));
            ownerPmtDobj.setBody_type(getmap.get("body_type"));
            ownerPmtDobj.setState_cd(sessionVariables.getStateCodeSelected());
            if (CommonUtils.isNullOrBlank(getmap.get("stand_cap"))) {
                ownerPmtDobj.setStand_cap(0);
            } else {
                ownerPmtDobj.setStand_cap(Integer.valueOf(getmap.get("stand_cap")));
            }
            if (CommonUtils.isNullOrBlank(getmap.get("sleeper_cap"))) {
                ownerPmtDobj.setSleeper_cap(0);
            } else {
                ownerPmtDobj.setSleeper_cap(Integer.valueOf(getmap.get("sleeper_cap")));
            }
            pmtFeeDobj.setChasi_no(getmap.get("chasi_no"));
            pmtFeeDobj.setVh_class(getmap.get("vh_class_desc"));
            if (CommonUtils.isNullOrBlank(getmap.get("seat_cap"))) {
                pmtFeeDobj.setSeat_cap(0);
            } else {
                pmtFeeDobj.setSeat_cap(Integer.valueOf(getmap.get("seat_cap")));
            }
            tempPmtDobj.setPmt_no(getmap.get("auth_no"));
            tempPmtDobj.setPrv_valid_fr(getmap.get("auth_fr"));
            tempPmtDobj.setPrv_valid_to(getmap.get("auth_to"));
            if (getmap.get("state_cd").equalsIgnoreCase("PY")) {
                renderPYprintCond = true;
            }
            setTextQRcode("Application No: " + applNo.toUpperCase() + " \\nValid Upto: " + getmap.get("valid_upto") + " \\nPermit No: " + getmap.get("pmt_no"));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public final void goodsCarriagePermit(String applNo) {
        try {
            pmtDocImpl = new PrintPermitDocInXhtmlImpl();
            Map<String, String> getmap = pmtDocImpl.getGoodsCarriageDetail(applNo);
            setRoutedata(pmtDocImpl.getRouteData(applNo, getmap.get("state_cd"), Integer.valueOf(getmap.get("off_cd")), TableList.vt_permit_route, false));
            if (getRoutedata() != null) {
                for (PassengerPermitDetailDobj dobj : routedata) {
                    if (!CommonUtils.isNullOrBlank(dobj.getRout_length()) && Integer.parseInt(dobj.getRout_length()) > 0) {
                        setRenderLengh(true);
                    }
                }
            }
//          Madhurendra for Mn on 6-1-19
            if (getmap.get("state_cd").equalsIgnoreCase("MN")) {
                renderManipurPrintCond = true;
                if (getmap.get("pmt_catg").equalsIgnoreCase("Labour Permit")) {
                    renderPmtCatg = true;
                }
                if ((getmap.get("pmt_off_cd").equals("99")) && (getmap.get("pmt_catg").equalsIgnoreCase("Oil Tanker"))) {
                    isSTAOffice = true;
                }
                if (getmap.get("pmt_type_code").equals("105")) {
                    renderMnGoodsPmtPrintCond = true;
                }
                PermitCheckDetailsBean bean = new PermitCheckDetailsBean();
                bean.getAlldetails(getmap.get("regn_no"), null, getmap.get("state_cd"), Integer.parseInt(getmap.get("off_cd")));
                dtlsDobj = bean.dtlsDobj;
            }
            passPmtDobj.setRegion_covered(pmtDocImpl.getRegionDetail(applNo, getmap.get("state_cd"), Integer.valueOf(getmap.get("off_cd")), TableList.VT_PERMIT));
            if (getmap.get("fncr_name").equals("")) {
                setFncr_name(null);
            } else {
                setFncr_name(getmap.get("fncr_name"));
            }
            if (CommonUtils.isNullOrBlank(getmap.get("service_type_descr")) || getmap.get("service_type_descr").equalsIgnoreCase("Select Services Type")) {
                passPmtDobj.setServices_TYPE(null);
            } else {
                passPmtDobj.setServices_TYPE(getmap.get("service_type_descr"));
            }
            passPmtDobj.setOff_cd(getOffDesce());
            passPmtDobj.setState_cd(getmap.get("state_name"));
            passPmtDobj.setPmt_no(getmap.get("pmt_no"));
            passPmtDobj.setGoods_TO_CARRY(getmap.get("goods_to_carry"));
            passPmtDobj.setValidFromInString(getmap.get("valid_from"));
            passPmtDobj.setValidUptoInString(getmap.get("valid_upto"));
            passPmtDobj.setReplaceDateInString(getmap.get("replace_date"));
            passPmtDobj.setOtherCriteria(getmap.get("other_criteria"));
            passPmtDobj.setPmt_type(getmap.get("pmt_type").toUpperCase());
            if (!CommonUtils.isNullOrBlank(getmap.get("pmt_catg"))) {
                passPmtDobj.setPmtCatg(" (" + getmap.get("pmt_catg").toUpperCase() + ")");
            }
            ownerPmtDobj.setOwner_name(getmap.get("owner_name").toUpperCase());
            ownerPmtDobj.setF_name(getmap.get("f_name").toUpperCase());
            ownerPmtDobj.setC_add1(getmap.get("c_add1").toUpperCase());
            ownerPmtDobj.setC_add2(getmap.get("c_add2").toUpperCase());
            ownerPmtDobj.setC_add3(getmap.get("c_add3").toUpperCase());
            ownerPmtDobj.setC_state(getmap.get("c_state_name"));
            ownerPmtDobj.setC_pincode(Integer.parseInt(getmap.get("c_pincode")));
            ownerPmtDobj.setLd_wt(Integer.parseInt(getmap.get("ld_wt")));
            ownerPmtDobj.setUnld_wt(Integer.parseInt(getmap.get("unld_wt")));
            setPay_load(ownerPmtDobj.getLd_wt() - ownerPmtDobj.getUnld_wt());
            ownerPmtDobj.setRegn_no(getmap.get("regn_no"));
            ownerPmtDobj.setCurrentDateInString(getmap.get("currentdate"));
            ownerPmtDobj.setEngnNo(getmap.get("eng_no"));
            ownerPmtDobj.setMakerName(getmap.get("maker_name"));
            ownerPmtDobj.setModelName(getmap.get("model_name"));
            ownerPmtDobj.setDistrictInString(getmap.get("c_district_name"));
            ownerPmtDobj.setRegnDateInString(getmap.get("regn_dt"));
            ownerPmtDobj.setManuYearInString(getmap.get("manu_yr"));
            pmtFeeDobj.setChasi_no(getmap.get("chasi_no"));
            pmtFeeDobj.setSeat_cap(Integer.parseInt(getmap.get("seat_cap")));
            pmtFeeDobj.setVh_class(getmap.get("vh_class_desc"));
            ownerPmtDobj.setCurrentDateInString(getmap.get("currentdate"));
            ownerPmtDobj.setState_cd(sessionVariables.getStateCodeSelected());
            setTrailer_data(pmtDocImpl.getTrailerDetails(ownerPmtDobj.getRegn_no(), ownerPmtDobj.getState_cd(), Integer.valueOf(getmap.get("off_cd"))));
            setTextQRcode("Application No: " + applNo.toUpperCase() + " \\nValid Upto: " + getmap.get("valid_upto") + " \\nPermit No: " + getmap.get("pmt_no"));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public final void stageCarriagePermit(String applNo) {
        try {
            pmtDocImpl = new PrintPermitDocInXhtmlImpl();
            Map<String, String> getmap = pmtDocImpl.getGoodsCarriageDetail(applNo);
            setRouteTimeTableData(pmtDocImpl.getPmtTimeTableData(getmap.get("regn_no"), applNo, getmap.get("state_cd"), Integer.valueOf(getmap.get("pmt_off_cd"))));
            setRoutedata(pmtDocImpl.getRouteData(applNo, getmap.get("state_cd"), Integer.valueOf(getmap.get("off_cd")), TableList.vt_permit_route, otherRouteAllow));
            if (getRoutedata() != null) {
                for (PassengerPermitDetailDobj dobj : routedata) {
                    if (Integer.parseInt(dobj.getRout_length()) > 0) {
                        setRenderLengh(true);
                    }
                    if (!CommonUtils.isNullOrBlank(dobj.getNhOverlapping())) {
                        overlappingRoute += dobj.getNhOverlapping() + " within " + dobj.getNhOverlappingLength() + " Km. ";
                    }
                }
                if (getRoutedata().get(0).getNumberOfTrips().equals("0")) {
                    passPmtDobj.setNumberOfTrips(null);
                } else {
                    passPmtDobj.setNumberOfTrips(getRoutedata().get(0).getNumberOfTrips());
                }
            }
            VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
            parameters.setPERMIT_TYPE(Integer.parseInt(getmap.get("pmt_type_code")));
            parameters.setPERMIT_SUB_CATG(Integer.parseInt(getmap.get("pmt_catg_code")));
            if (isCondition(FormulaUtils.replaceTagPermitValues(getRouteFlagCondition(), parameters), "stageCarriagePermit")) {
                setRenderRouteFlag(true);
            }
            if (!CommonUtils.isNullOrBlank(conditionPmtnoAndRoute)) {
                if (Boolean.parseBoolean(FormulaUtils.getSqlFormulaValue(FormulaUtils.replaceTagPermitValues(conditionPmtnoAndRoute, parameters), "stageCarriagePermit"))) {
                    renderPmtnoAndRoutedtl = true;
                }
            }
            if (getmap.get("state_cd").equalsIgnoreCase("PY")) {
                renderPYRouteCodeFlag = false;
                renderLengh = false;
                renderPYprintCond = true;
            }

            // Madhurendra for Mn on 6-1-19
            if (getmap.get("state_cd").equalsIgnoreCase("MN")) {
                renderManipurPrintCond = true;
                PermitCheckDetailsBean bean = new PermitCheckDetailsBean();
                bean.getAlldetails(getmap.get("regn_no"), null, getmap.get("state_cd"), Integer.parseInt(getmap.get("off_cd")));
                dtlsDobj = bean.dtlsDobj;
                if (getmap.get("pmt_catg").equalsIgnoreCase("Labour Permit")) {
                    renderPmtCatg = true;
                }
                if (getOffDesce().equalsIgnoreCase("STATE TRANSPORT AUTHORITY")) {
                    renderManipurStaPrintCond = true;
                    renderMnStaSCPmtPrintCond = true;
                }
            }

            passPmtDobj.setRegion_covered(pmtDocImpl.getRegionDetail(applNo, getmap.get("state_cd"), Integer.valueOf(getmap.get("off_cd")), TableList.VT_PERMIT));
            passPmtDobj.setOff_cd(getOffDesce());

            if (getmap.get("fncr_name").equals("")) {
                setFncr_name(null);
            } else {
                setFncr_name(getmap.get("fncr_name"));
            }
            if (getmap.get("service_type_descr").equals("") || getmap.get("service_type_descr").equalsIgnoreCase("Select Services Type")) {
                passPmtDobj.setServices_TYPE(null);
            } else {
                passPmtDobj.setServices_TYPE(getmap.get("service_type_descr"));
            }

            passPmtDobj.setState_cd(getmap.get("state_name"));
            passPmtDobj.setPmt_no(getmap.get("pmt_no"));
            passPmtDobj.setGoods_TO_CARRY(getmap.get("goods_to_carry"));
            passPmtDobj.setValidFromInString(getmap.get("valid_from"));
            passPmtDobj.setValidUptoInString(getmap.get("valid_upto"));
            passPmtDobj.setReplaceDateInString(getmap.get("replace_date"));
            passPmtDobj.setOtherCriteria(getmap.get("other_criteria"));
            passPmtDobj.setPmt_type(getmap.get("pmt_type").toUpperCase());
            if (!CommonUtils.isNullOrBlank(getmap.get("pmt_catg"))) {
                passPmtDobj.setPmtCatg(" (" + getmap.get("pmt_catg").toUpperCase() + ")");
            }
            ownerPmtDobj.setState_cd(getmap.get("state_cd"));
            ownerPmtDobj.setOwner_name(getmap.get("owner_name").toUpperCase());
            ownerPmtDobj.setF_name(getmap.get("f_name").toUpperCase());
            ownerPmtDobj.setC_add1(getmap.get("c_add1").toUpperCase());
            ownerPmtDobj.setC_add2(getmap.get("c_add2").toUpperCase());
            ownerPmtDobj.setC_add3(getmap.get("c_add3").toUpperCase());
            ownerPmtDobj.setC_state(getmap.get("c_state_name"));
            ownerPmtDobj.setC_pincode(Integer.parseInt(getmap.get("c_pincode")));
            ownerPmtDobj.setLd_wt(Integer.parseInt(getmap.get("ld_wt")));
            ownerPmtDobj.setUnld_wt(Integer.parseInt(getmap.get("unld_wt")));
            setPay_load(ownerPmtDobj.getLd_wt() - ownerPmtDobj.getUnld_wt());
            ownerPmtDobj.setRegn_no(getmap.get("regn_no"));
            ownerPmtDobj.setCurrentDateInString(getmap.get("currentdate"));
            ownerPmtDobj.setEngnNo(getmap.get("eng_no"));
            ownerPmtDobj.setMakerName(getmap.get("maker_name"));
            ownerPmtDobj.setModelName(getmap.get("model_name"));
            ownerPmtDobj.setDistrictInString(getmap.get("c_district_name"));
            ownerPmtDobj.setRegnDateInString(getmap.get("regn_dt"));
            ownerPmtDobj.setNorms(getmap.get("norms_descr"));
            ownerPmtDobj.setManuYearInString(getmap.get("manu_yr"));
            pmtFeeDobj.setChasi_no(getmap.get("chasi_no"));
            pmtFeeDobj.setSeat_cap(Integer.parseInt(getmap.get("seat_cap")));
            pmtFeeDobj.setSleeper_cap(Integer.parseInt(getmap.get("sleeper_cap")));
            if (getmap.get("stand_cap").equals("")) {
                pmtFeeDobj.setStand_cap(0);
            } else {
                pmtFeeDobj.setStand_cap(Integer.parseInt(getmap.get("stand_cap")));
            }
            pmtFeeDobj.setVh_class(getmap.get("vh_class_desc"));
            passPmtDobj.setParking(getmap.get("parking"));
            passPmtDobj.setJoreny_PURPOSE(getmap.get("jorney_purpose"));
            ownerPmtDobj.setCurrentDateInString(getmap.get("currentdate"));
            if (("AS").contains(sessionVariables.getStateCodeSelected()) && (!getmap.get("off_cd").equalsIgnoreCase("999"))) {
                setRenderASPrint(true);
            }
            if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("WB") && Integer.valueOf(getmap.get("pmt_off_cd")) == 999) {
                renderWBPrint = true;
                renderPmtnoAndRoutedtl = true;
            }
            setTextQRcode("Application No: " + applNo.toUpperCase() + " \\nValid Upto: " + getmap.get("valid_upto") + " \\nPermit No: " + getmap.get("pmt_no"));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public final void contractCarriagePermit(String applNo) {
        try {
            pmtDocImpl = new PrintPermitDocInXhtmlImpl();
            Map<String, String> getmap = pmtDocImpl.getGoodsCarriageDetail(applNo);
            String[] heading = new String[2];
            heading = pmtDocImpl.getHeadingNote(getmap.get("pmt_type_code"), getmap.get("pmt_catg_code"));
            if (!CommonUtils.isNullOrBlank(heading[0])) {
                setFormName(heading[0]);
            }
            if (!CommonUtils.isNullOrBlank(heading[1])) {
                setRuleAndSection(heading[1]);
            }
            if (getmap.get("state_cd").equalsIgnoreCase("BR")) {
                setRenderParking(true);
            }
            if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("UP")) {
                setRenderUPPermitDetail(true);
            }
            setRoutedata(pmtDocImpl.getRouteData(applNo, getmap.get("state_cd"), Integer.valueOf(getmap.get("off_cd")), TableList.vt_permit_route, false));
            if (getRoutedata() != null) {
                for (PassengerPermitDetailDobj dobj : routedata) {
                    if (Integer.parseInt(dobj.getRout_length()) > 0) {
                        setRenderLengh(true);
                    }
                }
            }
            passPmtDobj.setRegion_covered(pmtDocImpl.getRegionDetail(applNo, getmap.get("state_cd"), Integer.valueOf(getmap.get("off_cd")), TableList.VT_PERMIT));
            if (getmap.get("fncr_name").equals("")) {
                setFncr_name(null);
            } else {
                setFncr_name(getmap.get("fncr_name"));
            }
            if (CommonUtils.isNullOrBlank(getmap.get("service_type_descr")) || (getmap.get("service_type_descr").equalsIgnoreCase("Select Service Type"))) {
                passPmtDobj.setServices_TYPE(null);
            } else {
                passPmtDobj.setServices_TYPE(getmap.get("service_type_descr"));
            }
            passPmtDobj.setOff_cd(getOffDesce());
            passPmtDobj.setState_cd(getmap.get("state_name"));
            passPmtDobj.setPmt_no(getmap.get("pmt_no"));
            passPmtDobj.setGoods_TO_CARRY(getmap.get("goods_to_carry"));
            passPmtDobj.setValidFromInString(getmap.get("valid_from"));
            passPmtDobj.setValidUptoInString(getmap.get("valid_upto"));
            passPmtDobj.setReplaceDateInString(getmap.get("replace_date"));
            passPmtDobj.setPmt_type(getmap.get("pmt_type").toUpperCase());
            passPmtDobj.setPmt_type_code(getmap.get("pmt_type_code"));
            passPmtDobj.setPmt_catg_code(getmap.get("pmt_catg_code"));
            if (!CommonUtils.isNullOrBlank(getmap.get("pmt_catg"))) {
                passPmtDobj.setPmtCatg(" (" + getmap.get("pmt_catg").toUpperCase() + ")");
            }
            passPmtDobj.setParking(getmap.get("parking").toUpperCase());
            passPmtDobj.setJoreny_PURPOSE(getmap.get("jorney_purpose").toUpperCase());
            ownerPmtDobj.setOwner_name(getmap.get("owner_name").toUpperCase());
            ownerPmtDobj.setF_name(getmap.get("f_name").toUpperCase());
            ownerPmtDobj.setC_add1(getmap.get("c_add1").toUpperCase());
            ownerPmtDobj.setC_add2(getmap.get("c_add2").toUpperCase());
            ownerPmtDobj.setC_add3(getmap.get("c_add3").toUpperCase());
            ownerPmtDobj.setC_state(getmap.get("c_state_name"));
            ownerPmtDobj.setC_pincode(Integer.parseInt(getmap.get("c_pincode")));
            ownerPmtDobj.setLd_wt(Integer.parseInt(getmap.get("ld_wt")));
            ownerPmtDobj.setUnld_wt(Integer.parseInt(getmap.get("unld_wt")));
            ownerPmtDobj.setRegn_no(getmap.get("regn_no"));
            ownerPmtDobj.setCurrentDateInString(getmap.get("currentdate"));
            ownerPmtDobj.setEngnNo(getmap.get("eng_no"));
            ownerPmtDobj.setMakerName(getmap.get("maker_name"));
            ownerPmtDobj.setModelName(getmap.get("model_name"));
            ownerPmtDobj.setManuYearInString(getmap.get("manu_yr"));
            ownerPmtDobj.setDistrictInString(getmap.get("c_district_name"));
            ownerPmtDobj.setRegnDateInString(getmap.get("regn_dt"));
            pmtFeeDobj.setChasi_no(getmap.get("chasi_no"));
            pmtFeeDobj.setSeat_cap(Integer.parseInt(getmap.get("seat_cap")));
            pmtFeeDobj.setStand_cap(Integer.parseInt(getmap.get("stand_cap")));
            pmtFeeDobj.setSleeper_cap(Integer.parseInt(getmap.get("sleeper_cap")));
            pmtFeeDobj.setVh_class(getmap.get("vh_class_desc"));
            ownerPmtDobj.setCurrentDateInString(getmap.get("currentdate"));
            ownerPmtDobj.setState_cd(sessionVariables.getStateCodeSelected());
            ownerPmtDobj.setFuel_desc(getmap.get("fuel_descr"));
            HpaImpl hpa_Impl = new HpaImpl();
            hypth = hpa_Impl.getHypoDetails(null, TableConstants.VM_TRANSACTION_MAST_REM_HYPO, getmap.get("regn_no").toUpperCase(), true, sessionVariables.getStateCodeSelected());
            if (hypth.size() > 0 && sessionVariables.getStateCodeSelected().equalsIgnoreCase("KA") && "51,53".contains(getmap.get("vh_class"))) {
                for (HpaDobj dobj : hypth) {
                    if (dobj.getHp_type().equalsIgnoreCase("LA") && !CommonUtils.isNullOrBlank(dobj.getFncr_add1())) {
                        setFinancerAddress(dobj.getFncr_add1() + ", " + dobj.getFncr_add2() + ", " + dobj.getFncr_district_descr() + ", " + dobj.getFncr_state_name() + ", " + dobj.getFncr_pincode());
                        setLeaseAgreementDate(dobj.getFrom_dt_descr());
                        ownerPmtDobj.setOwner_name(dobj.getFncr_name());
                        PermitPaidFeeDtlsImpl impl = new PermitPaidFeeDtlsImpl();
                        setPaidFeeList(impl.getListOfPaidFee(applNo));
                        if (paidFeeList != null && getPaidFeeList().size() > 0) {
                            totalAmount = 0;
                            for (PermitPaidFeeDtlsDobj obj : getPaidFeeList()) {
                                totalAmount += obj.getFees() + obj.getFine();
                            }
                            setSpecialFeeDetail("This Vehicle submit permit fee - Rs " + totalAmount + " with Receipt No. " + paidFeeList.get(0).getRcpt_no() + " On " + paidFeeList.get(0).getRcpt_dt() + ".");
                        }
                    }
                }
            }
            if (getmap.get("state_cd").equalsIgnoreCase("MN")) {
                renderManipurPrintCond = true;
            }
            if (getmap.get("state_cd").equalsIgnoreCase("WB")) {
                renderWBPrint = true;
            }
            if (getmap.get("state_cd").equalsIgnoreCase("MN") && Integer.parseInt(getmap.get("pmt_off_cd")) == 99 && getmap.get("pmt_catg").equalsIgnoreCase("PARA TRANSIT")) {
                renderPmtCatg = true;
            }
            setTextQRcode("Application No: " + applNo.toUpperCase() + " \\nValid Upto: " + getmap.get("valid_upto") + " \\nPermit No: " + getmap.get("pmt_no"));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public final void privateServiceVehiclePermit(String app_no) {
        try {
            pmtDocImpl = new PrintPermitDocInXhtmlImpl();
            Map<String, String> getmap = pmtDocImpl.getPrivateServicePermit(app_no);
            String[] heading = new String[2];
            heading = pmtDocImpl.getHeadingNote(getmap.get("pmt_type_code"), getmap.get("pmt_catg_code"));
            if (!CommonUtils.isNullOrBlank(heading[0])) {
                setFormName(heading[0]);
            }
            if (!CommonUtils.isNullOrBlank(heading[1])) {
                setRuleAndSection(heading[1]);
            }
            passPmtDobj.setRegion_covered(pmtDocImpl.getRegionDetail(app_no, getmap.get("state_cd"), Integer.valueOf(getmap.get("off_cd")), TableList.VT_PERMIT));
            HpaImpl hpa_Impl = new HpaImpl();
            hypth = hpa_Impl.getHypoDetails(null, TableConstants.VM_TRANSACTION_MAST_REM_HYPO, getmap.get("regn_no").toUpperCase(), true, sessionVariables.getStateCodeSelected());
            if (CommonUtils.isNullOrBlank(getmap.get("service_type_descr")) || getmap.get("service_type_descr").equalsIgnoreCase("Select Services Type")) {
                passPmtDobj.setServices_TYPE(null);
            } else {
                passPmtDobj.setServices_TYPE(getmap.get("service_type_descr"));
            }
            if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("UP")) {
                setRenderUPPermitDetail(true);
            }
            passPmtDobj.setOff_cd(getOffDesce());
            passPmtDobj.setState_cd(getmap.get("state_name"));
            passPmtDobj.setPmt_no(getmap.get("pmt_no"));
            passPmtDobj.setValidFromInString(getmap.get("valid_from"));
            passPmtDobj.setValidUptoInString(getmap.get("valid_upto"));
            passPmtDobj.setPmt_catg_code(getmap.get("pmt_catg_code"));
            if (("PY").contains(sessionVariables.getStateCodeSelected()) && !CommonUtils.isNullOrBlank(getmap.get("pmt_catg"))
                    && getmap.get("pmt_catg_code").equalsIgnoreCase("31")) {
                passPmtDobj.setPmt_type("");
            } else {
                passPmtDobj.setPmt_type(getmap.get("pmt_type").toUpperCase());
            }
            if (!CommonUtils.isNullOrBlank(getmap.get("pmt_catg")) && CommonUtils.isNullOrBlank(passPmtDobj.getPmt_type())) {
                passPmtDobj.setPmtCatg(getmap.get("pmt_catg").toUpperCase());
            } else if (!CommonUtils.isNullOrBlank(getmap.get("pmt_catg"))) {
                passPmtDobj.setPmtCatg(" (" + getmap.get("pmt_catg").toUpperCase() + ")");
            }
            ownerPmtDobj.setOwner_name(getmap.get("owner_name"));
            passPmtDobj.setPmtOwnerName(getmap.get("owner_name"));
            if (hypth.size() > 0 && sessionVariables.getStateCodeSelected().equalsIgnoreCase("KA")) {
                for (HpaDobj dobj : hypth) {
                    if (dobj.getHp_type().equalsIgnoreCase("LA") && !CommonUtils.isNullOrBlank(dobj.getFncr_add1())) {
                        setFinancerAddress(dobj.getFncr_add1() + ", " + dobj.getFncr_add2() + ", " + dobj.getFncr_district_descr() + ", " + dobj.getFncr_state_name() + ", " + dobj.getFncr_pincode());
                        setLeaseAgreementDate(dobj.getFrom_dt_descr());
                        ownerPmtDobj.setOwner_name(dobj.getFncr_name());
                        PermitPaidFeeDtlsImpl impl = new PermitPaidFeeDtlsImpl();
                        setPaidFeeList(impl.getListOfPaidFee(app_no));
                        if (paidFeeList != null && getPaidFeeList().size() > 0) {
                            totalAmount = 0;
                            for (PermitPaidFeeDtlsDobj obj : getPaidFeeList()) {
                                totalAmount += obj.getFees() + obj.getFine();
                            }
                            setSpecialFeeDetail("This Vehicle submit permit fee - Rs " + totalAmount + " with Receipt No. " + paidFeeList.get(0).getRcpt_no() + " On " + paidFeeList.get(0).getRcpt_dt() + ".");
                        }
                    }
                }
            }

            ownerPmtDobj.setF_name(getmap.get("f_name"));
            ownerPmtDobj.setC_add1(getmap.get("c_add1"));
            ownerPmtDobj.setC_add2(getmap.get("c_add2"));
            ownerPmtDobj.setC_add3(getmap.get("c_add3"));
            ownerPmtDobj.setDistrictInString(getmap.get("c_district_name"));
            ownerPmtDobj.setC_state(getmap.get("c_state_name"));
            pmtFeeDobj.setChasi_no(getmap.get("chasi_no"));
            passPmtDobj.setGoods_TO_CARRY(getmap.get("goods_to_carry"));
            ownerPmtDobj.setRegn_no(getmap.get("regn_no"));
            ownerPmtDobj.setLd_wt(Integer.parseInt(getmap.get("ld_wt")));
            pmtFeeDobj.setSeat_cap(Integer.parseInt(getmap.get("seat_cap")));
            pmtFeeDobj.setStand_cap(Integer.parseInt(getmap.get("stand_cap")));
            pmtFeeDobj.setSleeper_cap(Integer.parseInt(getmap.get("sleeper_cap")));
            pmtFeeDobj.setVh_class(getmap.get("vh_class_desc"));
            ownerPmtDobj.setMakerName(getmap.get("maker_name"));
            ownerPmtDobj.setModelName(getmap.get("model_name"));
            ownerPmtDobj.setC_pincode(Integer.parseInt(getmap.get("c_pincode")));
            ownerPmtDobj.setCurrentDateInString(getmap.get("current_date"));
            ownerPmtDobj.setState_cd(sessionVariables.getStateCodeSelected());
            setRoutedata(pmtDocImpl.getRouteData(app_no, getmap.get("state_cd"), Integer.valueOf(getmap.get("off_cd")), TableList.vt_permit_route, false));
            if (getRoutedata() != null) {
                for (PassengerPermitDetailDobj dobj : routedata) {
                    if (Integer.parseInt(dobj.getRout_length()) > 0) {
                        setRenderLengh(true);
                    }
                }
            }
            setTextQRcode("Application No: " + app_no.toUpperCase() + " \\nValid Upto: " + getmap.get("valid_upto") + " \\nPermit No: " + getmap.get("pmt_no"));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public final void tempSpecialPermitDetails(String app_no) {
        try {
            pmtDocImpl = new PrintPermitDocInXhtmlImpl();
            Map<String, String> getmap = pmtDocImpl.getTempAndSpecialPermit(app_no);
            Map<String, String> route_status = CommonPermitPrintImpl.getVmPermitStateConfiguration(sessionVariables.getStateCodeSelected());
            String[] heading = new String[2];
            heading = pmtDocImpl.getHeadingNote(getmap.get("temp_pmt_type"), "0");
            if (!CommonUtils.isNullOrBlank(heading[0])) {
                setFormName(heading[0]);
            }
            if (!CommonUtils.isNullOrBlank(heading[1])) {
                setRuleAndSection(heading[1]);
            }
            if (Integer.parseInt(getmap.get("pur_cd")) == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                main_footer = new PrintPermitDocInXhtmlImpl().getOffCdFormStateConfig(FormulaUtils.replaceTagPermitValues(splConfig.get("spl_permit_footer").toString(), parameters));
                String[] signature = main_footer.split(":");
                footerSignature = signature[0];
                if (signature.length > 1) {
                    main_footer = signature[1];
                }
            }

            setRoutedata(pmtDocImpl.getRouteDataForTemp(app_no, getmap.get("state_cd"), Integer.valueOf(getmap.get("off_cd"))));
            String area_detail = pmtDocImpl.getRegionVtTemp(app_no);
            if (!area_detail.equalsIgnoreCase("")) {
                passPmtDobj.setRegion_covered(area_detail);
            } else {
                passPmtDobj.setRegion_covered(null);
            }

            if (getmap.get("state_cd").equalsIgnoreCase("WB") && getmap.get("pmt_off_cd").equalsIgnoreCase("999") && purCdDescr.equalsIgnoreCase("Special Permit")) {
                renderWBPrint = true;
                setRegistered_office(getmap.get("off_name"));
            }

            if ("OR,TN".contains(getmap.get("state_cd").toString()) && purCdDescr.equalsIgnoreCase("Special Permit")) {
                PermitPaidFeeDtlsImpl impl = new PermitPaidFeeDtlsImpl();
                boolean payFeeTax = false;
                setPaidFeeList(impl.getListOfPaidFee(app_no));
                if (paidFeeList != null && getPaidFeeList().size() > 0) {
                    totalAmount = 0;
                    for (PermitPaidFeeDtlsDobj obj : getPaidFeeList()) {
                        if ((obj.getPurCd() == TableConstants.VM_PMT_DIFFERENTIAL_SPECIAL_TAX || obj.getPurCd() == TableConstants.SPECIAL_TAX) && obj.getFees() > 0) {
                            payFeeTax = true;
                        }
                        totalAmount += obj.getFees() + obj.getFine();
                    }
                }
                setSplroutedata(pmtDocImpl.getRouteDataForSpecial(app_no));
                String from_tax = JSFUtils.getDateInDD_MMM_YYYY(DateUtils.getStartOfMonthDate(getmap.get("valid_from")));
                String upto_tax = JSFUtils.getDateInDD_MMM_YYYY(DateUtils.getLastOfMonthDate(getmap.get("valid_upto")));
                if (payFeeTax) {
                    setSpecialFeeDetail("This Vehicle submit permit fee + Diff. Special Tax Rs " + totalAmount + " with Receipt No. " + paidFeeList.get(0).getRcpt_no() + " On " + paidFeeList.get(0).getRcpt_dt() + " From " + from_tax + " To " + upto_tax + ".");
                } else {
                    setSpecialFeeDetail("This Vehicle submit permit fee - Rs " + totalAmount + " with Receipt No. " + paidFeeList.get(0).getRcpt_no() + " On " + paidFeeList.get(0).getRcpt_dt() + ".");
                }
            } else {
                setSplroutedata(null);
                setSpecialFeeDetail(null);
            }
            passPmtDobj.setOff_cd(getOffDesce());
            passPmtDobj.setState_cd(getmap.get("state_name"));
            passPmtDobj.setValidFromInString(getmap.get("vtpmtvalidfrom"));
            passPmtDobj.setValidUptoInString(getmap.get("vtpmtvalidupto"));
            passPmtDobj.setPmt_no(getmap.get("vtpmtno"));
            tempPmtDobj.setPmt_no(getmap.get("pmt_no"));
            tempPmtDobj.setPmt_type(getmap.get("temp_pmt_type"));
            tempPmtDobj.setPrv_valid_fr(getmap.get("valid_from"));
            tempPmtDobj.setPrv_valid_to(getmap.get("valid_upto"));
            tempPmtDobj.setPmt_catg(getmap.get("temp_pmt_catg_desc"));
            ownerPmtDobj.setOwner_name(getmap.get("owner_name"));
            ownerPmtDobj.setF_name(getmap.get("f_name"));
            ownerPmtDobj.setC_add1(getmap.get("c_add1"));
            ownerPmtDobj.setC_add2(getmap.get("c_add2"));
            ownerPmtDobj.setC_add3(getmap.get("c_add3"));
            ownerPmtDobj.setDistrictInString(getmap.get("c_district_name"));
            ownerPmtDobj.setC_state(getmap.get("c_state_name"));
            if (CommonUtils.isNullOrBlank(getmap.get("c_pincode"))) {
                ownerPmtDobj.setC_pincode(0);
            } else {
                ownerPmtDobj.setC_pincode(Integer.parseInt(getmap.get("c_pincode")));
            }
            ownerPmtDobj.setRegn_no(getmap.get("regn_no"));
            if (CommonUtils.isNullOrBlank(getmap.get("ld_wt"))) {
                ownerPmtDobj.setLd_wt(0);
            } else {
                ownerPmtDobj.setLd_wt(Integer.parseInt(getmap.get("ld_wt")));
            }
            if (CommonUtils.isNullOrBlank(getmap.get("unld_wt"))) {
                ownerPmtDobj.setUnld_wt(0);
            } else {
                ownerPmtDobj.setUnld_wt(Integer.parseInt(getmap.get("unld_wt")));
            }
            ownerPmtDobj.setState_cd(getmap.get("state_cd"));
            ownerPmtDobj.setEngnNo(getmap.get("eng_no"));
            ownerPmtDobj.setChasi_no(getmap.get("chasi_no"));
            ownerPmtDobj.setMakerName(getmap.get("maker_name"));
            ownerPmtDobj.setModelName(getmap.get("model_name"));
            ownerPmtDobj.setManuYearInString(getmap.get("manu_yr"));
            ownerPmtDobj.setRegnDateInString(getmap.get("regn_dt"));
            ownerPmtDobj.setVh_class(Integer.parseInt(getmap.get("vh_class")));
            if (CommonUtils.isNullOrBlank(getmap.get("sleeper_cap"))) {
                ownerPmtDobj.setSleeper_cap(0);
            } else {
                ownerPmtDobj.setSleeper_cap(Integer.parseInt(getmap.get("sleeper_cap")));
            }
            ownerPmtDobj.setCurrentDateInString(getmap.get("currentdate"));
            if (CommonUtils.isNullOrBlank(getmap.get("seat_cap"))) {
                pmtFeeDobj.setSeat_cap(0);
            } else {
                pmtFeeDobj.setSeat_cap(Integer.parseInt(getmap.get("seat_cap")));
            }
            pmtFeeDobj.setVh_class(getmap.get("vh_class_desc"));
            pmtFeeDobj.setChasi_no(getmap.get("chasi_no"));
            if (!CommonUtils.isNullOrBlank(getmap.get("pmt_type"))) {
                passPmtDobj.setPmt_type(getmap.get("pmt_type").toUpperCase());
            } else {
                passPmtDobj.setPmt_type(getmap.get("temp_pmt_type_desc").toUpperCase());
            }
            if (!CommonUtils.isNullOrBlank(getmap.get("pmt_catg"))) {
                passPmtDobj.setPmtCatg(" (" + getmap.get("pmt_catg").toUpperCase() + ")");
            }
            tempPmtDobj.setRoute_fr(getmap.get("route_from"));
            tempPmtDobj.setRoute_to(getmap.get("route_to"));
            tempPmtDobj.setPurpose(getmap.get("purpose"));
            if (!getmap.get("via").isEmpty()) {
                tempPmtDobj.setVia(getmap.get("via").toUpperCase());
            } else {
                tempPmtDobj.setVia(null);
            }
            tempPmtDobj.setGoods_to_carry(getmap.get("goods_to_carry").toUpperCase());
            if (Integer.parseInt(getmap.get("pur_cd")) == TableConstants.VM_PMT_SPECIAL_PUR_CD || Integer.parseInt(getmap.get("pur_cd")) == TableConstants.VM_PMT_TEMP_PUR_CD) {
                PermitCheckDetailsBean bean = new PermitCheckDetailsBean();
                bean.getAlldetails(getmap.get("regn_no"), null, getmap.get("state_cd"), Integer.parseInt(getmap.get("off_cd")));
                dtlsDobj = bean.dtlsDobj;
                // Madhurendra for Mn on 6-1-19                
                if (getmap.get("state_cd").equalsIgnoreCase("MN")) {
                    renderManipurPrintCond = true;
                    if (getmap.get("temp_pmt_type").equals("102")) {
                        renderMnStaCGPmtPrintCond = true;
                        if (getmap.get("vh_class").equalsIgnoreCase("71")) {
                            renderMotorVehicleClass = true;
                        }

                        if (getmap.get("temp_pmt_catg_desc").equals("PARA TRANSIT")) {
                            renderPmtCatg = true;
                        }
                        if (getmap.get("vh_class").equalsIgnoreCase("71") || getmap.get("vh_class").equalsIgnoreCase("70") || getmap.get("vh_class").equalsIgnoreCase("57") || getmap.get("vh_class").equalsIgnoreCase("73")) {
                            renderPmtVehicleClass = true;
                        }
//                        if (getmap.get("vh_class").equalsIgnoreCase("57")) {
//                            renderThreeWheelerCond = true;
//                        }

                    }
                    if (getOffDesce().equalsIgnoreCase("STATE TRANSPORT AUTHORITY")) {
                        renderManipurStaPrintCond = true;
                        if (getmap.get("temp_pmt_type").equals("101")) {
                            renderMnStaSCPmtPrintCond = true;
                            if (getmap.get("temp_pmt_catg_desc").equals("INTERSTATE")) {
                                renderPmtCatgInterState = true;
                            }
                        }
                        if (getmap.get("pur_cd").equals("36")) {
                            renderPmtSpecial = true;
                        }
                    }

                    if (getmap.get("temp_pmt_type").equals("105")) {
                        renderMnGoodsPmtPrintCond = true;
                        if (pmtDocImpl.getRegionVtTemp(app_no).equalsIgnoreCase("ASSAM")) {
                            renderMnRoutePrintCond = true;
                        }
                        if (getmap.get("temp_pmt_catg_desc").equals("INTERSTATE")) {
                            renderPmtCatgInterState = true;
                        }
                        if (getmap.get("temp_pmt_catg_desc").equalsIgnoreCase("Oil Tanker") || getmap.get("temp_pmt_catg_desc").equals("Labour Permit")) {
                            renderPmtCatg = true;
                        }

                    }
                }
            }
            setTextQRcode("Application No: " + app_no.toUpperCase() + " \\nValid Upto: " + getmap.get("valid_upto") + " \\nPermit No: " + getmap.get("pmt_no"));
            PrimeFaces.current().ajax().update("vtpmtdtls");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public final void homeAuthDetails(String applNo, String regnNo, int pur_cd, String doc_id) {
        try {
            pmtDocImpl = new PrintPermitDocInXhtmlImpl();
            Map<String, String> getmap = null;
            getmap = pmtDocImpl.getHomeAuthCer(applNo);
            if (getmap.isEmpty()) {
                getmap = pmtDocImpl.getHomeAuthCerThroughRegnNo(regnNo);
            }
            if (!CommonUtils.isNullOrBlank(getmap.get("pmt_type")) && getmap.get("pmt_type").equals(TableConstants.AITP) && getmap.get("state_cd").equalsIgnoreCase("PY")) {
                renderPYprintCond = true;
            } else {
                renderPYprintCond = false;
            }
            if (!CommonUtils.isNullOrBlank(getmap.get("pmt_type")) && getmap.get("pmt_type").equals(TableConstants.AITP)) {
                setAitpPaymentdata(pmtDocImpl.getAitpPaymentData(regnNo));
            } else {
                setAitpPaymentdata(null);
            }

            if (doc_id.equalsIgnoreCase("12")) {
                // regnNo = ServerUtil.getRegnNoWithSpace(regnNo);
                Map<String, String> getNpMap = pmtDocImpl.getNPHomeAuthFromNpPortal(regnNo, null);
                if (getNpMap != null) {
                    if (!CommonUtils.isNullOrBlank(getNpMap.get("amount"))) {
                        pmtFeeDobj.setAmount1(Integer.parseInt(getNpMap.get("amount")));
                    }
                    pmtFeeDobj.setNpIssueDate(getNpMap.get("issue_dt"));
                    pmtFeeDobj.setTransactionNo(getNpMap.get("transaction_no"));
                    pmtFeeDobj.setNpTransactionDate(getNpMap.get("txn_dt"));
                    pmtFeeDobj.setBankRefNo(getNpMap.get("bank_ref_no"));
                    passPmtDobj.setNpAuthNo(getNpMap.get("pmt_no").toUpperCase());
                }
            }
            if (!CommonUtils.isNullOrBlank(getmap.get("region_covered"))) {
                passPmtDobj.setRegion_covered(pmtDocImpl.getRegionDetailStateVise(applNo, getmap.get("region_covered")));
                if (CommonUtils.isNullOrBlank(passPmtDobj.getRegion_covered())) {
                    if (pur_cd == TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD || pur_cd == TableConstants.VM_PMT_DUPLICATE_PUR_CD) {
                        passPmtDobj.setRegion_covered(pmtDocImpl.getRegionDetail(applNo, getmap.get("state_cd"), Integer.valueOf(getmap.get("off_cd")), TableList.VT_PERMIT));
                    } else {
                        passPmtDobj.setRegion_covered(pmtDocImpl.getRegionDetailOnAuth(regnNo, getmap.get("state_cd"), Integer.valueOf(getmap.get("off_cd")), TableList.VT_PERMIT));

                    }
                }
            }
            if (getmap.get("pmt_type").equalsIgnoreCase(TableConstants.AITP)) {
                footerRender = true;
                pmtSubHeading = "Authorization for Tourist Vehicle Permit";
            } else if (getmap.get("pmt_type").equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)) {
                footerRender = false;
                pmtSubHeading = "Home State Authorization for Goods Vehicle Permit";
            }
            if (!getmap.get("pmt_type").equalsIgnoreCase(TableConstants.AITP) && !getmap.get("pmt_type").equalsIgnoreCase(TableConstants.NATIONAL_PERMIT) && sessionVariables.getStateCodeSelected().equalsIgnoreCase("MZ")) {
                pmtAuthHeading = "Authorisation For ALL MIZORAM";
            } else if (getmap.get("pmt_type").equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)) {
                pmtAuthHeading = "Renewal Of Home State Authorisation certificate";
            } else {
                pmtAuthHeading = "Renewal Of Authorisation certificate";
            }
            passPmtDobj.setOpDateInString(getmap.get("op_date"));
            passPmtDobj.setOff_cd(getOffDesce());
            passPmtDobj.setState_cd(getmap.get("state_name"));
            passPmtDobj.setPmt_no(getmap.get("auth_no").toUpperCase());
            passPmtDobj.setGoods_TO_CARRY(getmap.get("goods_to_carry"));
            passPmtDobj.setValidFromInString(getmap.get("valid_from"));
            passPmtDobj.setValidUptoInString(getmap.get("valid_upto"));
            passPmtDobj.setReplaceDateInString(getmap.get("replace_date"));
            ownerPmtDobj.setOwner_name(getmap.get("owner_name").toUpperCase());
            ownerPmtDobj.setF_name(getmap.get("f_name").toUpperCase());
            ownerPmtDobj.setC_add1(getmap.get("c_add1").toUpperCase());
            ownerPmtDobj.setC_add2(getmap.get("c_add2").toUpperCase());
            ownerPmtDobj.setC_add3(getmap.get("c_add3").toUpperCase());
            ownerPmtDobj.setC_state(getmap.get("c_state_name"));
            ownerPmtDobj.setC_pincode(Integer.parseInt(getmap.get("c_pincode")));
            ownerPmtDobj.setLd_wt(Integer.parseInt(getmap.get("ld_wt")));
            ownerPmtDobj.setUnld_wt(Integer.parseInt(getmap.get("unld_wt")));
            ownerPmtDobj.setRegn_no(getmap.get("regn_no"));
            ownerPmtDobj.setCurrentDateInString(getmap.get("currentdate"));
            ownerPmtDobj.setEngnNo(getmap.get("eng_no"));
            ownerPmtDobj.setMakerName(getmap.get("maker_name"));
            ownerPmtDobj.setModelName(getmap.get("model_name"));
            ownerPmtDobj.setDistrictInString(getmap.get("c_district_name"));
            ownerPmtDobj.setRegnDateInString(getmap.get("regn_dt"));
            ownerPmtDobj.setManuYearInString(getmap.get("manu_yr"));
            ownerPmtDobj.setState_cd(sessionVariables.getStateCodeSelected());
            pmtFeeDobj.setChasi_no(getmap.get("chasi_no"));
            pmtFeeDobj.setSeat_cap(Integer.parseInt(getmap.get("seat_cap")));
            if (getmap.get("pmt_type").equalsIgnoreCase(TableConstants.AITP)) {
                pmtFeeDobj.setSleeper_cap(Integer.parseInt(getmap.get("sleeper_cap")));
            } else {
                pmtFeeDobj.setSleeper_cap(0);
            }
            pmtFeeDobj.setVh_class(getmap.get("vh_class_desc"));
            pmtFeeDobj.setPeriod(getmap.get("pmt_valid_upto"));
            tempPmtDobj.setPmt_no(getmap.get("pmt_no"));
            tempPmtDobj.setPrv_valid_fr(getmap.get("pmt_valid_from"));
            tempPmtDobj.setPrv_valid_to(getmap.get("pmt_valid_upto"));
            ownerPmtDobj.setCurrentDateInString(getmap.get("currentdate"));
            setTextQRcode("Application No: " + applNo.toUpperCase() + " \\nValid Upto: " + getmap.get("valid_upto") + " \\nAuthorization No: " + getmap.get("auth_no"));
        } catch (Exception e) {
            LOGGER.error(e.toString() + "ApplNo= " + applNo.toUpperCase() + "Regn_no =" + regnNo + e.getStackTrace()[0]);
        }
    }

    public final void permitNocDetails(String applNo, String docId) {
        try {
            String purpose = "";
            pmtDocImpl = new PrintPermitDocInXhtmlImpl();
            Map<String, String> getmap = pmtDocImpl.getNocDetails(applNo, docId);
            passPmtDobj.setOff_cd(getOffDesce().toUpperCase());
            passPmtDobj.setValidUptoInString(getmap.get("after2_month_dt"));
            passPmtDobj.setRegistered_off_name(getmap.get("off_name"));
            passPmtDobj.setState_cd(getmap.get("state_name").toUpperCase());
            passPmtDobj.setPmt_no(getmap.get("pmt_no").toUpperCase());
            passPmtDobj.setRegnNo(getmap.get("regn_no").toUpperCase());
            if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("ML")) {
                setSepratePrintOfNocSurrenderSlip(true);
            }
            if (!CommonUtils.isNullOrBlank(getmap.get("trans_pur_cd"))) {
                purpose = ServerUtil.getTaxHead(Integer.valueOf(getmap.get("trans_pur_cd")));
//              madhurendra on 26-8-19
                if (TableConstants.VM_PMT_TRANSFER_PUR_CD == Integer.parseInt(getmap.get("trans_pur_cd")) && "ML".equalsIgnoreCase(sessionVariables.getStateCodeSelected())) {
                    setRenderNocPrintCondTP(true);
                } else if (TableConstants.VM_PMT_REPLACE_VEH_PUR_CD == Integer.parseInt(getmap.get("trans_pur_cd")) && "ML".equalsIgnoreCase(sessionVariables.getStateCodeSelected())) {
                    setRenderNocPrintCondRV(true);
                } else if (TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD == Integer.parseInt(getmap.get("trans_pur_cd")) && "ML".equalsIgnoreCase(sessionVariables.getStateCodeSelected())) {
                    setRenderNocPrintCondTR(true);
                }
            }
            passPmtDobj.setPaction(purpose);
            passPmtDobj.setValidFromInString(getmap.get("op_dt"));
            ownerPmtDobj.setC_add1(getmap.get("c_add1").toUpperCase());
            ownerPmtDobj.setC_add2(getmap.get("c_add2").toUpperCase());
            ownerPmtDobj.setC_add3(getmap.get("c_add3").toUpperCase());
            ownerPmtDobj.setC_state(getmap.get("c_state_name").toUpperCase());
            ownerPmtDobj.setC_pincode(Integer.valueOf(getmap.get("c_pincode")));
            ownerPmtDobj.setCurrentDateInString(getmap.get("currentdate").toUpperCase());
            ownerPmtDobj.setOwner_name(getmap.get("owner_name").toUpperCase());
            ownerPmtDobj.setManuYearInString(getmap.get("year"));
            ownerPmtDobj.setState_cd(sessionVariables.getStateCodeSelected());
            setTextQRcode("Application No: " + applNo.toUpperCase() + " \\nPermit No: " + getmap.get("pmt_no"));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public final void permitAckDetails(String applNo, String regnNo, String docId) {
        try {
            String route = "";
            pmtDocImpl = new PrintPermitDocInXhtmlImpl();
            Map<String, String> getmap = pmtDocImpl.getAckDetails(applNo, regnNo, docId);
            passPmtDobj.setRegion_covered(pmtDocImpl.getRegionDetail(applNo, getmap.get("state_cd"), Integer.valueOf(getmap.get("off_cd")), TableList.VA_PERMIT));
            setRoutedata(pmtDocImpl.getRouteData(applNo, getmap.get("state_cd"), Integer.valueOf(getmap.get("off_cd")), TableList.va_permit_route, otherRouteAllow));
            if (routedata != null && routedata.size() > 0) {
                for (int i = 0; i < getRoutedata().size(); i++) {
                    String route_detail = getRoutedata().get(i).getRout_code() + " - " + getRoutedata().get(i).getFloc() + " TO " + getRoutedata().get(i).getTloc() + " VIA " + getRoutedata().get(i).getStart_POINT() + " ROUTE_LENGTH " + getRoutedata().get(i).getRout_length() + " TRIP " + getRoutedata().get(i).getNumberOfTrips() + ",";
                    route = route + route_detail;
                }
                setRouteData(route);
            }
            passPmtDobj.setOff_cd(getOffDesce().toUpperCase());
            passPmtDobj.setState_cd(getmap.get("state_name").toUpperCase());
            passPmtDobj.setApplNo(getmap.get("appl_no").toUpperCase());
            passPmtDobj.setRegnNo(getmap.get("regn_no").toUpperCase());
            passPmtDobj.setPaction(getmap.get("pur_cd").toUpperCase());
            passPmtDobj.setPmt_type(getmap.get("pmt_type").toUpperCase());
            passPmtDobj.setPmtCatg(getmap.get("pmt_catg").toUpperCase());
            ownerPmtDobj.setCurrentDateInString(getmap.get("op_dt"));
            ownerPmtDobj.setOwner_name(getmap.get("owner_name").toUpperCase());
            ownerPmtDobj.setF_name(getmap.get("f_name").toUpperCase());
            ownerPmtDobj.setC_add1(getmap.get("c_add1").toUpperCase());
            ownerPmtDobj.setC_add2(getmap.get("c_add2").toUpperCase());
            ownerPmtDobj.setC_add3(getmap.get("c_add3").toUpperCase());
            ownerPmtDobj.setC_state(getmap.get("c_state_name").toUpperCase());
            ownerPmtDobj.setC_pincode(Integer.valueOf(getmap.get("c_pincode")));
            ownerPmtDobj.setChasi_no(getmap.get("chasi_no").toUpperCase());
            ownerPmtDobj.setEngnNo(getmap.get("eng_no").toUpperCase());
            ownerPmtDobj.setVh_class_desc(getmap.get("vh_class_desc").toUpperCase());
            ownerPmtDobj.setVch_catg(getmap.get("catg_desc").toUpperCase());
            ownerPmtDobj.setFuel_desc(getmap.get("fuel_descr").toUpperCase());
            ownerPmtDobj.setMobile_no(Long.valueOf(getmap.get("mobile_no")));
            ownerPmtDobj.setEmail_id(getmap.get("email_id").toUpperCase());
            ownerPmtDobj.setSeat_cap(Integer.parseInt(getmap.get("seat_cap")));
            if (CommonUtils.isNullOrBlank(getmap.get("regn_dt"))) {
                ownerPmtDobj.setRegnDateInString(null);
            } else {
                ownerPmtDobj.setRegnDateInString(getmap.get("regn_dt"));
            }
            if (CommonUtils.isNullOrBlank(getmap.get("color"))) {
                pmtFeeDobj.setColor(null);
            } else {
                pmtFeeDobj.setColor(getmap.get("color"));
            }
            setRouteTimeTableData(pmtDocImpl.getPmtTimeTableDataVa(getmap.get("regn_no"), applNo, getmap.get("state_cd"), Integer.valueOf(getmap.get("off_cd"))));
            setTextQRcode("Application No: " + getmap.get("appl_no").toUpperCase() + " \\nOwner Name: " + getmap.get("owner_name").toUpperCase() + " \\n Chasi No: " + getmap.get("chasi_no").toUpperCase());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public final void getHeadingFooterNote(int purCd, int permitType, String doc_id, String regn_no) {
        try {
            pmtDocImpl = new PrintPermitDocInXhtmlImpl();
            if (!CommonUtils.isNullOrBlank(regn_no) && !CommonUtils.isNullOrBlank(doc_id)) {
                setOpDateInString(pmtDocImpl.getPrintOpDate(regn_no, doc_id));
            }
            Map<String, String> getmap = pmtDocImpl.getHeadingFooterNote(sessionVariables.getStateCodeSelected());
            main_header = getmap.get("permit_main_header");
            VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
            main_footer = new PrintPermitDocInXhtmlImpl().getOffCdFormStateConfig(FormulaUtils.replaceTagPermitValues(getmap.get("permit_main_footer"), parameters));
            String[] signature = main_footer.split(":");
            footerSignature = signature[0];
            if (signature.length > 1) {
                main_footer = signature[1];
            }
            if (purCd == TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD
                    || purCd == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD || doc_id.equals("5")) {
                switch (permitType) {
                    case 103: {
                        formName = getmap.get("auth_aitp_rule_heading");
                        ruleAndSection = getmap.get("auth_aitp_formno_heading");
                        break;
                    }
                    case 106: {
                        formName = getmap.get("auth_np_rule_heading");
                        ruleAndSection = getmap.get("auth_np_formno_heading");
                        break;
                    }
                    case 102: {
                        if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("MZ")) {
                            formName = getmap.get("auth_aitp_rule_heading");
                            ruleAndSection = getmap.get("auth_aitp_formno_heading");
                        }
                        break;
                    }
                }

            } else if (purCd == TableConstants.VM_PMT_FRESH_PUR_CD || purCd == TableConstants.VM_PMT_RENEWAL_PUR_CD
                    || purCd == TableConstants.VM_PMT_DUPLICATE_PUR_CD || purCd == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD
                    || purCd == TableConstants.VM_PMT_TRANSFER_PUR_CD || purCd == TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD) {
                switch (permitType) {
                    case 101: {
                        formName = getmap.get("sc_rule_heading");
                        ruleAndSection = getmap.get("sc_formno_heading");
                        break;
                    }
                    case 102: {
                        formName = getmap.get("cc_rule_heading");
                        ruleAndSection = getmap.get("cc_formno_heading");
                        break;
                    }
                    case 103: {
                        formName = getmap.get("aitp_rule_heading");
                        ruleAndSection = getmap.get("aitp_formno_heading");
                        break;
                    }
                    case 104: {
                        formName = getmap.get("psvp_rule_heading");
                        ruleAndSection = getmap.get("psvp_formno_heading");
                        break;
                    }
                    case 105: {
                        formName = getmap.get("gp_rule_heading");
                        ruleAndSection = getmap.get("gp_formno_heading");
                        break;
                    }
                    case 106: {
                        formName = getmap.get("np_rule_heading");
                        ruleAndSection = getmap.get("np_formno_heading");
                        break;
                    }
                }
            } else if (purCd == TableConstants.VM_PMT_TEMP_PUR_CD
                    || purCd == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                switch (purCd) {
                    case 35: {
                        formName = getmap.get("temp_rule_heading");
                        ruleAndSection = getmap.get("temp_formno_heading");
                        break;
                    }
                    case 36: {
                        formName = getmap.get("spec_rule_heading");
                        ruleAndSection = getmap.get("spec_formno_heading");
                        break;
                    }
                }
            }
            if (Integer.valueOf(purCd) == TableConstants.VM_PMT_FRESH_PUR_CD) {
                noteAccForState = getmap.get("note_in_footer");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public final void offerLetterDetails(String applNo, String regn_no) {
        try {
            String route = "";
            pmtDocImpl = new PrintPermitDocInXhtmlImpl();
            Map<String, String> getmap = pmtDocImpl.getOfferLetterDetails(applNo, regn_no);
            passPmtDobj.setRegion_covered(pmtDocImpl.getRegionDetail(applNo, getmap.get("state_cd"), Integer.valueOf(getmap.get("off_cd")), TableList.VA_PERMIT));
            passPmtDobj.setOff_cd(getOffDesce());
            passPmtDobj.setApplNo(applNo);
            passPmtDobj.setApplDateInString(getmap.get("appl_dt"));
            passPmtDobj.setOffer_no(getmap.get("offer_no").toUpperCase());
            passPmtDobj.setOrder_no(getmap.get("order_no").toUpperCase());
            passPmtDobj.setValidUptoInString(getmap.get("order_dt"));
            passPmtDobj.setPmt_type(getmap.get("pmt_type").toUpperCase());
            if (!CommonUtils.isNullOrBlank(getmap.get("pmt_catg"))) {
                passPmtDobj.setPmtCatg(" (" + getmap.get("pmt_catg").toUpperCase() + ")");
            }
            passPmtDobj.setPeriod(getmap.get("period").toUpperCase());
            passPmtDobj.setPeriod_mode(getmap.get("period_mode").toUpperCase());
            passPmtDobj.setState_cd(getmap.get("state_name").toUpperCase());
            ownerPmtDobj.setOwner_name(getmap.get("owner_name").toUpperCase());
            ownerPmtDobj.setF_name(getmap.get("f_name").toUpperCase());
            ownerPmtDobj.setCurrentDateInString(getmap.get("currentdate"));
            ownerPmtDobj.setRegn_no(getmap.get("regn_no"));
            ownerPmtDobj.setC_add1(getmap.get("address"));
            if (!CommonUtils.isNullOrBlank(getmap.get("seat_cap"))) {
                ownerPmtDobj.setSeat_cap(Integer.parseInt(getmap.get("seat_cap")));
            }
            pmtFeeDobj.setVh_class(getmap.get("vh_class"));
            pmtFeeDobj.setFuel(getmap.get("fuel_type"));
            pmtFeeDobj.setOld_regn_no(getmap.get("old_regn_no"));
            ownerPmtDobj.setVch_catg(getmap.get("norms_descr").toUpperCase());
            pmtFeeDobj.setRecpt_no(getmap.get("rcpt_no").toUpperCase());
            ownerPmtDobj.setState_cd(sessionVariables.getStateCodeSelected());
            setRoutedata(pmtDocImpl.getRouteData(applNo, getmap.get("state_cd"), Integer.valueOf(getmap.get("off_cd")), TableList.va_permit_route, otherRouteAllow));
            if (routedata != null && routedata.size() > 0) {
                for (int i = 0; i < getRoutedata().size(); i++) {
                    String route_detail = getRoutedata().get(i).getRout_code() + " - " + getRoutedata().get(i).getFloc() + " TO " + getRoutedata().get(i).getTloc() + " VIA " + getRoutedata().get(i).getStart_POINT() + ",";
                    route = route + route_detail;
                    interStateName = getRoutedata().get(i).getState_cd_to();
                }
                setRouteData(route);
            }
//          madhurendra on 4-3-2020
            if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("WB") && Integer.valueOf(getmap.get("off_cd")) == 999 && getmap.get("pmt_type").equalsIgnoreCase("Stage Carriage Permit")) {
                renderWBPrint = true;
            }
            if (Integer.parseInt(getmap.get("pmt_type_code")) == TableConstants.STAGE_CARRIAGE_PERMIT && "2,3".contains(getmap.get("pmt_catg_code")) && renderWBPrint) {
                renderPmtnoAndRoutedtl = true;
            }
            setIsSTAOffice(CommonPermitPrintImpl.isSTAOffice(Integer.valueOf(getmap.get("off_cd")), getmap.get("state_cd")));

            setTextQRcode("Regn No: " + getmap.get("regn_no").toUpperCase() + " \\nApplication No: " + applNo.toUpperCase() + " \\nOffer No: " + getmap.get("offer_no"));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + applNo.toUpperCase() + e.getStackTrace()[0]);
        }
    }

    public final void counterSignatureDetails(String applNo) {
        try {
            pmtDocImpl = new PrintPermitDocInXhtmlImpl();
            Map<String, String> getmap = pmtDocImpl.getCounterSignatureDetails(applNo);
            PassengerPermitDetailImpl passImp = new PassengerPermitDetailImpl();
            PermitEndorsementAppImpl pmtEndoImpl = new PermitEndorsementAppImpl();
            PassengerPermitDetailDobj permit_dobj = passImp.set_vt_permit_regnNo_to_dobj(getmap.get("regn_no").toUpperCase(), getmap.get("pmt_no").toUpperCase());
            if (permit_dobj != null) {
                setPrv_route_list(pmtEndoImpl.getRouteAndOtherData(permit_dobj.getState_cd(), Integer.parseInt(permit_dobj.getOff_cd()), permit_dobj, TableConstants.TM_ROLE_PMT_COUNTER_SIGNATURE_ENTRY));
            }
            setRoutedata(pmtDocImpl.getRouteData(applNo, getmap.get("state_code"), Integer.valueOf(getmap.get("off_cd")), TableList.vt_permit_route, false));
            passPmtDobj.setApplNo(applNo);
            passPmtDobj.setOff_cd(getOffDesce());
            passPmtDobj.setOffer_no(getmap.get("count_sign_no").toUpperCase());
            passPmtDobj.setPmt_no(getmap.get("pmt_no").toUpperCase());
            passPmtDobj.setState_cd(getmap.get("state_name").toUpperCase());
            passPmtDobj.setValidFromInString(getmap.get("count_valid_from"));
            passPmtDobj.setValidUptoInString(getmap.get("count_valid_upto"));
            passPmtDobj.setRcpt_no(getmap.get("receipt_number").toUpperCase());
            ownerPmtDobj.setOwner_name(getmap.get("owner_name").toUpperCase());
            ownerPmtDobj.setF_name(getmap.get("f_name").toUpperCase());
            ownerPmtDobj.setCurrentDateInString(getmap.get("currentdate"));
            ownerPmtDobj.setRegn_no(getmap.get("regn_no"));
            ownerPmtDobj.setSeat_cap(Integer.parseInt(getmap.get("seat_cap")));
            ownerPmtDobj.setSleeper_cap(Integer.parseInt(getmap.get("sleeper_cap")));
            ownerPmtDobj.setStand_cap(Integer.parseInt(getmap.get("stand_cap")));
            ownerPmtDobj.setVch_catg(getmap.get("catg_desc"));
            ownerPmtDobj.setVh_class_desc(getmap.get("vh_class"));
            ownerPmtDobj.setC_add1(getmap.get("c_add1").toUpperCase());
            ownerPmtDobj.setC_add2(getmap.get("c_add2").toUpperCase());
            ownerPmtDobj.setC_add3(getmap.get("c_add3").toUpperCase());
            ownerPmtDobj.setC_state(getmap.get("c_state_name"));
            ownerPmtDobj.setC_pincode(Integer.parseInt(getmap.get("c_pincode")));
            ownerPmtDobj.setState_cd(getmap.get("state_code"));
            ownerPmtDobj.setBody_type(getmap.get("body_type"));
            ownerPmtDobj.setUnld_wt(Integer.valueOf(getmap.get("unld_wt")));
            ownerPmtDobj.setLd_wt(Integer.valueOf(getmap.get("ld_wt")));
            if (!CommonUtils.isNullOrBlank(getmap.get("maker_name"))) {
                ownerPmtDobj.setMakerName(getmap.get("maker_name").toUpperCase());
            } else {
                ownerPmtDobj.setMakerName(null);
            }
            if (!CommonUtils.isNullOrBlank(getmap.get("model_name"))) {
                ownerPmtDobj.setModelName(getmap.get("model_name").toUpperCase());
            } else {
                ownerPmtDobj.setModelName(null);
            }
            if (CommonUtils.isNullOrBlank(getmap.get("area"))) {
                passPmtDobj.setRegion_covered(null);
            } else {
                passPmtDobj.setRegion_covered(getmap.get("area"));
            }
            if ("TN".contains(Util.getUserStateCode())) {
                vchForCSDobj.setVacancy_no(pmtDocImpl.getVacancyNoForCounterSign(getmap.get("state_cd_from"), getmap.get("regn_no")));
            }
            if ("BR".contains(Util.getUserStateCode())) {
                renderBRPrint = true;
            }
            PermitCheckDetailsBean bean = new PermitCheckDetailsBean();
            bean.getAlldetails(getmap.get("regn_no"), null, getmap.get("state_cd_from"), Integer.parseInt(getmap.get("off_cd_from")));
            dtlsDobj = bean.dtlsDobj;
            setTextQRcode("Regn No: " + getmap.get("regn_no").toUpperCase() + " \\nApplication No: " + applNo.toUpperCase() + " \\nCounter Signature No: " + getmap.get("count_sign_no"));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public final void surrenderSlipDtls(String applNo, String docId) {
        try {
            pmtDocImpl = new PrintPermitDocInXhtmlImpl();
            PermitPaidFeeDtlsImpl impl = new PermitPaidFeeDtlsImpl();
            setPaidFeeList(impl.getListOfPaidFee(applNo));
            if (paidFeeList != null && getPaidFeeList().size() > 0) {
                totalAmount = 0;
                for (PermitPaidFeeDtlsDobj obj : getPaidFeeList()) {
                    totalAmount += obj.getFees() + obj.getFine();
                }
            }
            Map<String, String> getmap = pmtDocImpl.getSurrenderSlipDetails(applNo, docId);
            if (getmap.size() > 0) {
                passPmtDobj.setState_cd(getmap.get("state_name"));
                ownerPmtDobj.setState_cd(getmap.get("state_cd"));
                passPmtDobj.setRegistered_off_name(getmap.get("off_name"));
                passPmtDobj.setPmt_no(getmap.get("pmt_no"));
                passPmtDobj.setValidUptoInString(getmap.get("valid_upto"));
                if (!CommonUtils.isNullOrBlank(getmap.get("owner_name"))) {
                    ownerPmtDobj.setOwner_name(getmap.get("owner_name").toUpperCase());
                }
                if (!CommonUtils.isNullOrBlank(getmap.get("f_name"))) {
                    ownerPmtDobj.setF_name(getmap.get("f_name").toUpperCase());
                }
                ownerPmtDobj.setC_state(getmap.get("state_name"));
                ownerPmtDobj.setRegn_no(getmap.get("regn_no"));
                ownerPmtDobj.setCurrentDateInString(getmap.get("op_dt"));
                if (!CommonUtils.isNullOrBlank(getmap.get("c_add1"))) {
                    ownerPmtDobj.setC_add1(getmap.get("c_add1").toUpperCase() + ",");
                }
                if (!CommonUtils.isNullOrBlank(getmap.get("c_add2"))) {
                    ownerPmtDobj.setC_add2(getmap.get("c_add2").toUpperCase() + ",");
                }
                if (!CommonUtils.isNullOrBlank(getmap.get("c_add3"))) {
                    ownerPmtDobj.setC_add3(getmap.get("c_add3").toUpperCase() + ",");
                }
                if (!CommonUtils.isNullOrBlank(getmap.get("c_pincode"))) {
                    ownerPmtDobj.setC_pincode(Integer.parseInt(getmap.get("c_pincode")));
                }
                if (!CommonUtils.isNullOrBlank(getmap.get("trans_pur_cd"))) {
                    setPurCdDescr(ServerUtil.getTaxHead(Integer.valueOf(getmap.get("trans_pur_cd").trim())));
                }
                if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("ML")) {
                    setSepratePrintOfNocSurrenderSlip(true);
                }
                if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("ML") && sessionVariables.getOffCodeSelected() != TableConstants.STA) {
                    setRenderCancelPrintCondML(true);
                }
                setTextQRcode("Regn No: " + getmap.get("regn_no").toUpperCase() + " \\nApplication No: " + applNo.toUpperCase());
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public String redirect() {
        String backButton = "";
        if (reDirect.equalsIgnoreCase("PrintForm")) {
            backButton = "/ui/permit/form_newPermit_Print.xhtml?faces-redirect=true";
        } else if (reDirect.equalsIgnoreCase("PermitPassForm")
                || CommonUtils.isNullOrBlank(reDirect)) {
            backButton = "seatwork";
        }
        return backButton;
    }

    public PassengerPermitDetailDobj getPassPmtDobj() {
        return passPmtDobj;
    }

    public void setPassPmtDobj(PassengerPermitDetailDobj passPmtDobj) {
        this.passPmtDobj = passPmtDobj;
    }

    public PermitOwnerDetailDobj getOwnerPmtDobj() {
        return ownerPmtDobj;
    }

    public void setOwnerPmtDobj(PermitOwnerDetailDobj ownerPmtDobj) {
        this.ownerPmtDobj = ownerPmtDobj;
    }

    public PermitFeeDobj getPmtFeeDobj() {
        return pmtFeeDobj;
    }

    public void setPmtFeeDobj(PermitFeeDobj pmtFeeDobj) {
        this.pmtFeeDobj = pmtFeeDobj;
    }

    public String getRegion_covered() {
        return region_covered;
    }

    public void setRegion_covered(String region_covered) {
        this.region_covered = region_covered;
    }

    public TemporaryPermitDobj getTempPmtDobj() {
        return tempPmtDobj;
    }

    public void setTempPmtDobj(TemporaryPermitDobj tempPmtDobj) {
        this.tempPmtDobj = tempPmtDobj;
    }

    public PrintPermitDocInXhtmlImpl getPmtDocImpl() {
        return pmtDocImpl;
    }

    public void setPmtDocImpl(PrintPermitDocInXhtmlImpl pmtDocImpl) {
        this.pmtDocImpl = pmtDocImpl;
    }

    public List<PassengerPermitDetailDobj> getRoutedata() {
        return routedata;
    }

    public void setRoutedata(List<PassengerPermitDetailDobj> routedata) {
        this.routedata = routedata;
    }

    public String getPurCdDescr() {
        return purCdDescr;
    }

    public void setPurCdDescr(String purCdDescr) {
        this.purCdDescr = purCdDescr;
    }

    public String getNoteAccForState() {
        return noteAccForState;
    }

    public void setNoteAccForState(String noteAccForState) {
        this.noteAccForState = noteAccForState;
    }

    public String getAllIndiaTag() {
        return allIndiaTag;
    }

    public void setAllIndiaTag(String allIndiaTag) {
        this.allIndiaTag = allIndiaTag;
    }

    public String getOffDesce() {
        return offDesce;
    }

    public void setOffDesce(String offDesce) {
        this.offDesce = offDesce;
    }

    public String getRuleAndSection() {
        return ruleAndSection;
    }

    public void setRuleAndSection(String ruleAndSection) {
        this.ruleAndSection = ruleAndSection;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public boolean isFooterRender() {
        return footerRender;
    }

    public void setFooterRender(boolean footerRender) {
        this.footerRender = footerRender;
    }

    public boolean isPreviousOwnerDtlsShow() {
        return previousOwnerDtlsShow;
    }

    public void setPreviousOwnerDtlsShow(boolean previousOwnerDtlsShow) {
        this.previousOwnerDtlsShow = previousOwnerDtlsShow;
    }

    public String getTextQRcode() {
        return textQRcode;
    }

    public void setTextQRcode(String textQRcode) {
        this.textQRcode = textQRcode;
    }

    public boolean isRule() {
        return rule;
    }

    public void setRule(boolean rule) {
        this.rule = rule;
    }

    public String getFncr_name() {
        return fncr_name;
    }

    public int getPay_load() {
        return pay_load;
    }

    public void setPay_load(int pay_load) {
        this.pay_load = pay_load;
    }

    public void setFncr_name(String fncr_name) {
        this.fncr_name = fncr_name;
    }

    public boolean isRenderLengh() {
        return renderLengh;
    }

    public void setRenderLengh(boolean renderLengh) {
        this.renderLengh = renderLengh;
    }

    public String getReDirect() {
        return reDirect;
    }

    public void setReDirect(String reDirect) {
        this.reDirect = reDirect;
    }

    public List<PermitPaidFeeDtlsDobj> getPaidFeeList() {
        return paidFeeList;
    }

    public void setPaidFeeList(List<PermitPaidFeeDtlsDobj> paidFeeList) {
        this.paidFeeList = paidFeeList;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getRouteData() {
        return routeData;
    }

    public void setRouteData(String routeData) {
        this.routeData = routeData;
    }

    public List<HpaDobj> getHypth() {
        return hypth;
    }

    public void setHypth(List<HpaDobj> hypth) {
        this.hypth = hypth;
    }

    public List<SpecialRoutePermitDobj> getSplroutedata() {
        return splroutedata;
    }

    public void setSplroutedata(List<SpecialRoutePermitDobj> splroutedata) {
        this.splroutedata = splroutedata;
    }

    public String getSpecialFeeDetail() {
        return specialFeeDetail;
    }

    public void setSpecialFeeDetail(String specialFeeDetail) {
        this.specialFeeDetail = specialFeeDetail;
    }

    public String getMain_header() {
        return main_header;
    }

    public void setMain_header(String main_header) {
        this.main_header = main_header;
    }

    public String getMain_footer() {
        return main_footer;
    }

    public void setMain_footer(String main_footer) {
        this.main_footer = main_footer;
    }

    public String getFooterSignature() {
        return footerSignature;
    }

    public void setFooterSignature(String footerSignature) {
        this.footerSignature = footerSignature;
    }

    public String getVahanMessages() {
        return vahanMessages;
    }

    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }

    public SessionVariables getSessionVariables() {
        return sessionVariables;
    }

    public void setSessionVariables(SessionVariables sessionVariables) {
        this.sessionVariables = sessionVariables;
    }

    public String getPmtSubHeading() {
        return pmtSubHeading;
    }

    public void setPmtSubHeading(String pmtSubHeading) {
        this.pmtSubHeading = pmtSubHeading;
    }

    public List<OwnerDetailsDobj> getTrailer_data() {
        return trailer_data;
    }

    public void setTrailer_data(List<OwnerDetailsDobj> trailer_data) {
        this.trailer_data = trailer_data;
    }

    public String getDupReason() {
        return dupReason;
    }

    public void setDupReason(String dupReason) {
        this.dupReason = dupReason;
    }

    public boolean isDupReasonRender() {
        return dupReasonRender;
    }

    public void setDupReasonRender(boolean dupReasonRender) {
        this.dupReasonRender = dupReasonRender;
    }

    public String getPmtAuthHeading() {
        return pmtAuthHeading;
    }

    public void setPmtAuthHeading(String pmtAuthHeading) {
        this.pmtAuthHeading = pmtAuthHeading;
    }

    public boolean isPurposeRender() {
        return purposeRender;
    }

    public void setPurposeRender(boolean purposeRender) {
        this.purposeRender = purposeRender;
    }

    public String getOpDateInString() {
        return opDateInString;
    }

    public void setOpDateInString(String opDateInString) {
        this.opDateInString = opDateInString;
    }

    public PermitCheckDetailsDobj getDtlsDobj() {
        return dtlsDobj;
    }

    public void setDtlsDobj(PermitCheckDetailsDobj dtlsDobj) {
        this.dtlsDobj = dtlsDobj;
    }

    public String getPmtTypeCatgHeader() {
        return pmtTypeCatgHeader;
    }

    public void setPmtTypeCatgHeader(String pmtTypeCatgHeader) {
        this.pmtTypeCatgHeader = pmtTypeCatgHeader;
    }

    public StreamedContent getViewSignFileOff1() {
        return viewSignFileOff1;
    }

    public void setViewSignFileOff1(StreamedContent viewSignFileOff1) {
        this.viewSignFileOff1 = viewSignFileOff1;
    }

    public byte[] getSignApprovedOff() {
        return signApprovedOff;
    }

    public void setSignApprovedOff(byte[] signApprovedOff) {
        this.signApprovedOff = signApprovedOff;
    }

    public List<PermitLeaseDobj> getLeasePermit() {
        return leasePermit;
    }

    public void setLeasePermit(List<PermitLeaseDobj> leasePermit) {
        this.leasePermit = leasePermit;
    }

    public String getFinancerAddress() {
        return financerAddress;
    }

    public void setFinancerAddress(String financerAddress) {
        this.financerAddress = financerAddress;
    }

    public String getLeaseAgreementDate() {
        return leaseAgreementDate;
    }

    public void setLeaseAgreementDate(String leaseAgreementDate) {
        this.leaseAgreementDate = leaseAgreementDate;
    }

    public String getImage_background() {
        return image_background;
    }

    public void setImage_background(String image_background) {
        this.image_background = image_background;
    }

    public String getImage_logo() {
        return image_logo;
    }

    public void setImage_logo(String image_logo) {
        this.image_logo = image_logo;
    }

    public boolean isShow_image_background() {
        return show_image_background;
    }

    public void setShow_image_background(boolean show_image_background) {
        this.show_image_background = show_image_background;
    }

    public boolean isShow_image_logo() {
        return show_image_logo;
    }

    public void setShow_image_logo(boolean show_image_logo) {
        this.show_image_logo = show_image_logo;
    }

    public int getOffer_validity_days() {
        return offer_validity_days;
    }

    public void setOffer_validity_days(int offer_validity_days) {
        this.offer_validity_days = offer_validity_days;
    }

    public OtherStateVchCounterSignDobj getVchForCSDobj() {
        return vchForCSDobj;
    }

    public void setVchForCSDobj(OtherStateVchCounterSignDobj vchForCSDobj) {
        this.vchForCSDobj = vchForCSDobj;
    }

    public boolean isRenderParking() {
        return renderParking;
    }

    public void setRenderParking(boolean renderParking) {
        this.renderParking = renderParking;
    }

    public boolean isShowTimeTableDetails() {
        return showTimeTableDetails;
    }

    public void setShowTimeTableDetails(boolean showTimeTableDetails) {
        this.showTimeTableDetails = showTimeTableDetails;
    }

    public boolean isRenderRouteFlag() {
        return renderRouteFlag;
    }

    public void setRenderRouteFlag(boolean renderRouteFlag) {
        this.renderRouteFlag = renderRouteFlag;
    }

    public List<PassengerPermitDetailDobj> getPrv_route_list() {
        return prv_route_list;
    }

    public void setPrv_route_list(List<PassengerPermitDetailDobj> prv_route_list) {
        this.prv_route_list = prv_route_list;
    }

    public String getRouteFlagCondition() {
        return routeFlagCondition;
    }

    public void setRouteFlagCondition(String routeFlagCondition) {
        this.routeFlagCondition = routeFlagCondition;
    }

    public boolean isRenderUPPermitDetail() {
        return renderUPPermitDetail;
    }

    public void setRenderUPPermitDetail(boolean renderUPPermitDetail) {
        this.renderUPPermitDetail = renderUPPermitDetail;
    }

    public List<AITPStateFeeDraftDobj> getAitpPaymentdata() {
        return aitpPaymentdata;
    }

    public void setAitpPaymentdata(List<AITPStateFeeDraftDobj> aitpPaymentdata) {
        this.aitpPaymentdata = aitpPaymentdata;
    }

    public String getOverlappingRoute() {
        return overlappingRoute;
    }

    public void setOverlappingRoute(String overlappingRoute) {
        this.overlappingRoute = overlappingRoute;
    }

    /**
     * @return the renderNocPrintCondTP
     */
    public boolean isRenderNocPrintCondTP() {
        return renderNocPrintCondTP;
    }

    /**
     * @param renderNocPrintCondTP the renderNocPrintCondTP to set
     */
    public void setRenderNocPrintCondTP(boolean renderNocPrintCondTP) {
        this.renderNocPrintCondTP = renderNocPrintCondTP;
    }

    /**
     * @return the renderNocPrintCondRV
     */
    public boolean isRenderNocPrintCondRV() {
        return renderNocPrintCondRV;
    }

    /**
     * @param renderNocPrintCondRV the renderNocPrintCondRV to set
     */
    public void setRenderNocPrintCondRV(boolean renderNocPrintCondRV) {
        this.renderNocPrintCondRV = renderNocPrintCondRV;
    }

    /**
     * @return the renderNocPrintCondTR
     */
    public boolean isRenderNocPrintCondTR() {
        return renderNocPrintCondTR;
    }

    /**
     * @param renderNocPrintCondTR the renderNocPrintCondTR to set
     */
    public void setRenderNocPrintCondTR(boolean renderNocPrintCondTR) {
        this.renderNocPrintCondTR = renderNocPrintCondTR;
    }

    public boolean isSepratePrintOfNocSurrenderSlip() {
        return sepratePrintOfNocSurrenderSlip;
    }

    public void setSepratePrintOfNocSurrenderSlip(boolean sepratePrintOfNocSurrenderSlip) {
        this.sepratePrintOfNocSurrenderSlip = sepratePrintOfNocSurrenderSlip;
    }

    public boolean isRenderCancelPrintCondML() {
        return renderCancelPrintCondML;
    }

    public void setRenderCancelPrintCondML(boolean renderCancelPrintCondML) {
        this.renderCancelPrintCondML = renderCancelPrintCondML;
    }

    /**
     * @return the interStateName
     */
    public String getInterStateName() {
        return interStateName;
    }

    /**
     * @param interStateName the interStateName to set
     */
    public void setInterStateName(String interStateName) {
        this.interStateName = interStateName;
    }

    /**
     * @return the renderPmtnoAndRoutedtl
     */
    public boolean isRenderPmtnoAndRoutedtl() {
        return renderPmtnoAndRoutedtl;
    }

    /**
     * @param renderPmtnoAndRoutedtl the renderPmtnoAndRoutedtl to set
     */
    public void setRenderPmtnoAndRoutedtl(boolean renderPmtnoAndRoutedtl) {
        this.renderPmtnoAndRoutedtl = renderPmtnoAndRoutedtl;
    }

    public boolean isIsSTAOffice() {
        return isSTAOffice;
    }

    public void setIsSTAOffice(boolean isSTAOffice) {
        this.isSTAOffice = isSTAOffice;
    }

    /**
     * @return the renderASPrint
     */
    public boolean isRenderASPrint() {
        return renderASPrint;
    }

    /**
     * @param renderASPrint the renderASPrint to set
     */
    public void setRenderASPrint(boolean renderASPrint) {
        this.renderASPrint = renderASPrint;
    }

    /**
     * @return the renderWBPrint
     */
    public boolean isRenderWBPrint() {
        return renderWBPrint;
    }

    /**
     * @param renderWBPrint the renderWBPrint to set
     */
    public void setRenderWBPrint(boolean renderWBPrint) {
        this.renderWBPrint = renderWBPrint;
    }

    public String getRegistered_office() {
        return registered_office;
    }

    public void setRegistered_office(String registered_office) {
        this.registered_office = registered_office;
    }

    public CounterSignatureDobj getCounterDobj() {
        return counterDobj;
    }

    public void setCounterDobj(CounterSignatureDobj counterDobj) {
        this.counterDobj = counterDobj;
    }

    public boolean isRenderWbCGAuthPrintCond() {
        return renderWbCGAuthPrintCond;
    }

    public void setRenderWbCGAuthPrintCond(boolean renderWbCGAuthPrintCond) {
        this.renderWbCGAuthPrintCond = renderWbCGAuthPrintCond;
    }

    public List<PermitTimeTableDobj> getRouteTimeTableData() {
        return routeTimeTableData;
    }

    public void setRouteTimeTableData(List<PermitTimeTableDobj> routeTimeTableData) {
        this.routeTimeTableData = routeTimeTableData;
    }

    public boolean isRenderPYprintCond() {
        return renderPYprintCond;
    }

    public void setRenderPYprintCond(boolean renderPYprintCond) {
        this.renderPYprintCond = renderPYprintCond;
    }

    public boolean isRenderPYRouteCodeFlag() {
        return renderPYRouteCodeFlag;
    }

    public void setRenderPYRouteCodeFlag(boolean renderPYRouteCodeFlag) {
        this.renderPYRouteCodeFlag = renderPYRouteCodeFlag;
    }

    /**
     * @return the renderManipurPrintCond
     */
    public boolean isRenderManipurPrintCond() {
        return renderManipurPrintCond;
    }

    /**
     * @param renderManipurPrintCond the renderManipurPrintCond to set
     */
    public void setRenderManipurPrintCond(boolean renderManipurPrintCond) {
        this.renderManipurPrintCond = renderManipurPrintCond;
    }

    /**
     * @return the renderPmtType
     */
    public boolean isRenderPmtType() {
        return renderPmtType;
    }

    /**
     * @param renderPmtType the renderPmtType to set
     */
    public void setRenderPmtType(boolean renderPmtType) {
        this.renderPmtType = renderPmtType;
    }

    /**
     * @return the renderPmtCatg
     */
    public boolean isRenderPmtCatg() {
        return renderPmtCatg;
    }

    /**
     * @param renderPmtCatg the renderPmtCatg to set
     */
    public void setRenderPmtCatg(boolean renderPmtCatg) {
        this.renderPmtCatg = renderPmtCatg;
    }

    /**
     * @return the renderPmtVehicleClass
     */
    public boolean isRenderPmtVehicleClass() {
        return renderPmtVehicleClass;
    }

    /**
     * @param renderPmtVehicleClass the renderPmtVehicleClass to set
     */
    public void setRenderPmtVehicleClass(boolean renderPmtVehicleClass) {
        this.renderPmtVehicleClass = renderPmtVehicleClass;
    }

    /**
     * @return the renderManipurStaPrintCond
     */
    public boolean isRenderManipurStaPrintCond() {
        return renderManipurStaPrintCond;
    }

    /**
     * @param renderManipurStaPrintCond the renderManipurStaPrintCond to set
     */
    public void setRenderManipurStaPrintCond(boolean renderManipurStaPrintCond) {
        this.renderManipurStaPrintCond = renderManipurStaPrintCond;
    }

    /**
     * @return the tempFormHeading
     */
    public String getTempFormHeading() {
        return tempFormHeading;
    }

    /**
     * @param tempFormHeading the tempFormHeading to set
     */
    public void setTempFormHeading(String tempFormHeading) {
        this.tempFormHeading = tempFormHeading;
    }

    /**
     * @return the splpFormHeading
     */
    public String getSplpFormHeading() {
        return splpFormHeading;
    }

    /**
     * @param splpFormHeading the splpFormHeading to set
     */
    public void setSplpFormHeading(String splpFormHeading) {
        this.splpFormHeading = splpFormHeading;
    }

    /**
     * @return the renderManipurCatgPrintCond
     */
    public boolean isRenderManipurCatgPrintCond() {
        return renderManipurCatgPrintCond;
    }

    /**
     * @param renderManipurCatgPrintCond the renderManipurCatgPrintCond to set
     */
    public void setRenderManipurCatgPrintCond(boolean renderManipurCatgPrintCond) {
        this.renderManipurCatgPrintCond = renderManipurCatgPrintCond;
    }

    /**
     * @return the renderMnStaCGPmtPrintCond
     */
    public boolean isRenderMnStaCGPmtPrintCond() {
        return renderMnStaCGPmtPrintCond;
    }

    /**
     * @param renderMnStaCGPmtPrintCond the renderMnStaCGPmtPrintCond to set
     */
    public void setRenderMnStaCGPmtPrintCond(boolean renderMnStaCGPmtPrintCond) {
        this.renderMnStaCGPmtPrintCond = renderMnStaCGPmtPrintCond;
    }

    /**
     * @return the renderMnStaSCPmtPrintCond
     */
    public boolean isRenderMnStaSCPmtPrintCond() {
        return renderMnStaSCPmtPrintCond;
    }

    /**
     * @param renderMnStaSCPmtPrintCond the renderMnStaSCPmtPrintCond to set
     */
    public void setRenderMnStaSCPmtPrintCond(boolean renderMnStaSCPmtPrintCond) {
        this.renderMnStaSCPmtPrintCond = renderMnStaSCPmtPrintCond;
    }

    /**
     * @return the renderMnGoodsPmtPrintCond
     */
    public boolean isRenderMnGoodsPmtPrintCond() {
        return renderMnGoodsPmtPrintCond;
    }

    /**
     * @param renderMnGoodsPmtPrintCond the renderMnGoodsPmtPrintCond to set
     */
    public void setRenderMnGoodsPmtPrintCond(boolean renderMnGoodsPmtPrintCond) {
        this.renderMnGoodsPmtPrintCond = renderMnGoodsPmtPrintCond;
    }

    /**
     * @return the renderMnCGPmtPrintCond
     */
    public boolean isRenderMnCGPmtPrintCond() {
        return renderMnCGPmtPrintCond;
    }

    /**
     * @param renderMnCGPmtPrintCond the renderMnCGPmtPrintCond to set
     */
    public void setRenderMnCGPmtPrintCond(boolean renderMnCGPmtPrintCond) {
        this.renderMnCGPmtPrintCond = renderMnCGPmtPrintCond;
    }

    /**
     * @return the renderWbSCPrintCond
     */
    public boolean isRenderWbSCPrintCond() {
        return renderWbSCPrintCond;
    }

    /**
     * @param renderWbSCPrintCond the renderWbSCPrintCond to set
     */
    public void setRenderWbSCPrintCond(boolean renderWbSCPrintCond) {
        this.renderWbSCPrintCond = renderWbSCPrintCond;
    }

    /**
     * @return the renderMnSCRegPrintCond
     */
    public boolean isRenderMnSCRegPrintCond() {
        return renderMnSCRegPrintCond;
    }

    /**
     * @param renderMnSCRegPrintCond the renderMnSCRegPrintCond to set
     */
    public void setRenderMnSCRegPrintCond(boolean renderMnSCRegPrintCond) {
        this.renderMnSCRegPrintCond = renderMnSCRegPrintCond;
    }

    /**
     * @return the renderMnRoutePrintCond
     */
    public boolean isRenderMnRoutePrintCond() {
        return renderMnRoutePrintCond;
    }

    /**
     * @param renderMnRoutePrintCond the renderMnRoutePrintCond to set
     */
    public void setRenderMnRoutePrintCond(boolean renderMnRoutePrintCond) {
        this.renderMnRoutePrintCond = renderMnRoutePrintCond;
    }

    /**
     * @return the renderMotorVehicleClass
     */
    public boolean isRenderMotorVehicleClass() {
        return renderMotorVehicleClass;
    }

    /**
     * @param renderMotorVehicleClass the renderMotorVehicleClass to set
     */
    public void setRenderMotorVehicleClass(boolean renderMotorVehicleClass) {
        this.renderMotorVehicleClass = renderMotorVehicleClass;
    }

    /**
     * @return the renderMaxiVehicleClass
     */
    public boolean isRenderMaxiVehicleClass() {
        return renderMaxiVehicleClass;
    }

    /**
     * @param renderMaxiVehicleClass the renderMaxiVehicleClass to set
     */
    public void setRenderMaxiVehicleClass(boolean renderMaxiVehicleClass) {
        this.renderMaxiVehicleClass = renderMaxiVehicleClass;
    }

    /**
     * @return the renderPmtCatgInterState
     */
    public boolean isRenderPmtCatgInterState() {
        return renderPmtCatgInterState;
    }

    /**
     * @param renderPmtCatgInterState the renderPmtCatgInterState to set
     */
    public void setRenderPmtCatgInterState(boolean renderPmtCatgInterState) {
        this.renderPmtCatgInterState = renderPmtCatgInterState;
    }

    /**
     * @return the renderPmtSpecial
     */
    public boolean isRenderPmtSpecial() {
        return renderPmtSpecial;
    }

    /**
     * @param renderPmtSpecial the renderPmtSpecial to set
     */
    public void setRenderPmtSpecial(boolean renderPmtSpecial) {
        this.renderPmtSpecial = renderPmtSpecial;
    }

    /**
     * @return the renderThreeWheelerCond
     */
    public boolean isRenderThreeWheelerCond() {
        return renderThreeWheelerCond;
    }

    /**
     * @param renderThreeWheelerCond the renderThreeWheelerCond to set
     */
    public void setRenderThreeWheelerCond(boolean renderThreeWheelerCond) {
        this.renderThreeWheelerCond = renderThreeWheelerCond;
    }

    public boolean isRenderBRPrint() {
        return renderBRPrint;
    }

    public void setRenderBRPrint(boolean renderBRPrint) {
        this.renderBRPrint = renderBRPrint;
    }
}
