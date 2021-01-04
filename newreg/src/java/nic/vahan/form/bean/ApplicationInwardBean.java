/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.MessagesDobj;
import nic.rto.vahan.common.VahanException;
import static nic.vahan.CommonUtils.FormulaUtils.fillVehicleParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.getSqlFormulaValue;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.ApplicationInwardImpl;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.user_mgmt.dobj.TmConfigurationOtpDobj;
import nic.vahan.form.dobj.AuditRecoveryDobj;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.NonUseDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.PuccDobj;
import nic.vahan.form.dobj.TaxClearDobj;
import nic.vahan.form.dobj.TaxInstallCollectionDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.TmConfigurationOwnerIdentificationDobj;
import nic.vahan.form.dobj.VmSmartCardHsrpDobj;
import nic.vahan.form.dobj.common.RegistrationStatusParametersDobj;
import nic.vahan.form.dobj.configuration.TmConfigurationApplInwardDobj;
import nic.vahan.form.dobj.eChallan.ChallanReportDobj;
import nic.vahan.form.dobj.fitness.TmConfigurationFitnessDobj;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.dobj.pucc.TmConfigurationPUCCdobj;
import nic.vahan.form.dobj.smartcard.SmartCardDobj;
import nic.vahan.form.impl.BlackListedVehicleImpl;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.Home_Impl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.InsPuccUpdateImpl;
import nic.vahan.form.impl.NewImpl;
import nic.vahan.form.impl.NonUseImpl;
import nic.vahan.form.dobj.TmConfigurationNonUseDobj;
import nic.vahan.form.dobj.configuration.OnlineConfigurationDobj;
import nic.vahan.form.impl.DocumentUploadImpl;
import nic.vahan.form.impl.OwnerIdentificationImpl;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.ScrappedVehicleImpl;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan.form.impl.TaxClearImpl;
import nic.vahan.form.impl.TaxServer_Impl;
import nic.vahan.form.impl.admin.DelDupRegnNoImpl;
import nic.vahan.form.impl.eChallan.SaveChallanImpl;
import nic.vahan.form.impl.hsrp.HSRPRequestImpl;
import nic.vahan.form.impl.permit.PermitDetailImpl;
import nic.vahan.form.impl.permit.SurrenderPermitImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import nic.vahan.services.clients.NcrbWsClient;
import nic.vahan.services.insurance.InsuranceDetailService;
import nic.vahan.services.insurance.dobj.InsuranceInfoDobj;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.primefaces.PrimeFaces;
import nic.vahan.form.dobj.AuctionDobj;
import nic.vahan.form.dobj.ConvDobj;
import nic.vahan.form.dobj.TmConfigurationServiceExemption;
import nic.vahan.form.dobj.configuration.TmConfigApplAppointmentInward;
import nic.vahan.form.impl.AuctionImpl;
import nic.vahan.form.impl.ConvImpl;
import nic.vahan.services.DmsDocCheckUtils;
import nic.vahan.services.VTDocumentModel;

@ViewScoped
@ManagedBean(name = "applicationInward")
public class ApplicationInwardBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ApplicationInwardBean.class);
    private String regn_no;
    private int pur_cd;
    private List<String> selectedPurposeCode;
    private String message = null;
    private boolean render = false;
    private Map<Object, Object> purCodeList = null;
    private OwnerDetailsDobj ownerDetail;
    private Owner_dobj ownerDobj = null;
    @ManagedProperty(value = "#{ins_bean}")
    private InsBean ins_bean;
    @ManagedProperty(value = "#{hypothecationDetails_bean}")
    private HypothecationDetailsBean hypothecationDetails_bean;
    private FitnessDobj fitnessDobj = null;
    private List<SeatAllotedDetails> seatWork = null;
    private SeatAllotedDetails selectedSeat = new SeatAllotedDetails();
    private OwnerIdentificationDobj ownerIdentificationPrev = null;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private RegistrationStatusParametersDobj regStatus = new RegistrationStatusParametersDobj();
    private List<Status_dobj> onlineApplDataList = null;
    private List<OwnerDetailsDobj> dupRegnList = new ArrayList<>();
    private List<TaxClearDobj> taxDetaillist = null;
    private List listOwnerCatg = new ArrayList();
    private List<Status_dobj> statusList = new ArrayList<>();
    private PermitDetailDobj permitDetailDobj = null;
    private PermitDetailDobj permitSurrenderDetailDobj = null;
    private boolean isTSR = false;
    private boolean renderHpaVarification;
    private boolean validateHpaVarification;
    private String insuranceComparison;
    private String officeRemarks;
    private boolean vldtVehAgeExpireVarfcn;
    private boolean renderVehAgeExpireVarfcn;
    private List<ChallanReportDobj> eChallanInfo;
    private String echallanInfoURL;
    private String currentStateCd = null;
    private String userCatg = null;
    private int currentOffCd = 0;
    NonUseDobj nonUseDobj = null;
    TmConfigurationApplInwardDobj tmConfigurationApplInwardDobj = null;
    private Date appointmentDt;
    private int appointmentTakenOffice;
    String permitStatus = "";
    @ManagedProperty(value = "#{documentUploadBean}")
    private DocumentUploadBean documentUpload_bean;
    private boolean documentUploadShow = false;
    private String ownerMobileVerifyOtp = null;
    private String enteredOwnerMobVerifyOtp = null;
    String mobileNoCountMessage = null;
    private OwnerDetailsDobj moveToHistoryOwnerDtls = null;
    private String currentOfficeName;
    private FitnessDobj tempFitnessDetails = null;
    private boolean renderMoveToHistoryButton = false;

    private AuctionDobj auctionDobj = null;
    TmConfigApplAppointmentInward tmConfigApplAppointmentInwardDobj = null;
    private boolean renConversionTab = false;
    private boolean disableVehType = true;
    private List vh_class = new ArrayList();
    private ConvDobj conv_dobj = new ConvDobj();
    private int vehType;
    private List vh_type = new ArrayList();
    private List vh_category = new ArrayList();
    TmConfigurationServiceExemption tmConfigurationServiceExemption = null;
    private boolean restrictPurposeforEchallan = false;

    public ApplicationInwardBean() {
        if (Util.getSelectedSeat() != null) {
            currentOffCd = Util.getSelectedSeat().getOff_cd();
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("redirectTo", "documentsStatus");
        currentStateCd = Util.getUserStateCode();
        userCatg = Util.getUserCategory();
    }

    public void showDetails() {
        statusList = null;
        ownerDobj = null;
        moveToHistoryOwnerDtls = null;
        boolean docUploadAtOnlineService = false;
        StringBuilder applNos = new StringBuilder();
        if (this.regn_no.trim() == null || this.regn_no.trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Please Enter Valid Registration Number"));
            return;
        }

        try {

            if (currentStateCd == null || currentOffCd == 0 || userCatg == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Something Went Wrong. Please try again after some time..."));
                return;
            }

            //Master Filler for Owner Category
            String[][] data = MasterTableFiller.masterTables.VM_OWCATG.getData();
            for (int i = 0; i < data.length; i++) {
                listOwnerCatg.add(new SelectItem(data[i][0], data[i][1]));
            }
            OwnerImpl owner_Impl = new OwnerImpl();
            List<OwnerDetailsDobj> ownerDetailsDobjList = owner_Impl.getOwnerDetailsList(this.regn_no.trim(), null);
            if (ownerDetailsDobjList == null || ownerDetailsDobjList.isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Invalid Registration Number or Registration No not found in the Record"));
                return;
            }
            if (ownerDetailsDobjList.size() == 1) {
                ownerDetail = ownerDetailsDobjList.get(0);
            } else if (ownerDetailsDobjList.size() >= 2) {
                ownerDetailsDobjList = owner_Impl.getStateWiseOwnerList(ownerDetailsDobjList, currentStateCd);
            }

            if (ownerDetailsDobjList.size() >= 2) {
                int ownerDetailsCounts = 0;
                int ownerDetailsCountsNoc = 0;
                boolean isSameOffice = false;
                for (int i = 0; i < ownerDetailsDobjList.size(); i++) {

                    if (ownerDetailsDobjList.get(i).getOff_cd() == currentOffCd) {
                        isSameOffice = true;
                    }
                    if ("N".equalsIgnoreCase(ownerDetailsDobjList.get(i).getStatus())) {
                        ownerDetailsCountsNoc++;
                    }
                    if (!"N".equalsIgnoreCase(ownerDetailsDobjList.get(i).getStatus())) {
                        ownerDetailsCounts++;
                        ownerDetail = ownerDetailsDobjList.get(i);
                    }
                }
                if (!isSameOffice) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration Number not found in Selected Office"));
                    return;
                }
                dupRegnList = ownerDetailsDobjList;
                if ("WB".contains(currentStateCd)) {
                    renderMoveToHistoryButton = true;
                }
                PrimeFaces.current().executeScript("PF('varDupRegNo').show()");
                return;

            } else if (ownerDetailsDobjList.size() == 1) {
                ownerDetail = ownerDetailsDobjList.get(0);
            }

            //**Start--for setting checking if there is any temporary fitness exist or not
            FitnessImpl fitnessImpl = new FitnessImpl();
            tempFitnessDetails = fitnessImpl.getVtFitnessTempDetails(regn_no);
            //**End

            BlackListedVehicleImpl obj = new BlackListedVehicleImpl();
            BlackListedVehicleDobj blackListedDobj = obj.getBlacklistedVehicleDetails(regn_no, ownerDetail.getChasi_no());
            if (blackListedDobj != null) {
                if (blackListedDobj.getComplain_type() == TableConstants.BLCompoundingAmtCode) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Vehicle is blacklisted for compounding amount please first pay amount through Balance Fee option!!!"));
                    return;
                }
            }

            purCodeList = ServerUtil.getCodeDescr(TableList.TM_PURPOSE_MAST, "pur_cd", "descr", "inward_appl", "Y");
            ApplicationInwardImpl inwardImpl = new ApplicationInwardImpl();
            OnlineConfigurationDobj onlineConfigDobj = inwardImpl.getOnlineConfigurationDobj(currentStateCd);
            onlineApplDataList = inwardImpl.getOnlineApplicationDetails(regn_no);
            ApplicationInwardImpl impl = new ApplicationInwardImpl();
            tmConfigurationApplInwardDobj = impl.getApplInwardAnywhereInStateConfig(currentStateCd);
            tmConfigApplAppointmentInwardDobj = impl.getApplInwardAppointmentInStateConfig(currentStateCd);
            if (tmConfigurationApplInwardDobj != null && Util.getSession() != null && Util.getSession().getAttribute("msgDobj") != null) {
                MessagesDobj messagesDobj = (MessagesDobj) Util.getSession().getAttribute("msgDobj");
                if (messagesDobj != null) {
                    String hsrp = messagesDobj.getHsrpStatus();
                    ownerDobj = new OwnerImpl().getOwnerDobj(ownerDetail);
                    VehicleParameters vehParameters = fillVehicleParametersFromDobj(ownerDobj);
                    if ("YES".equalsIgnoreCase(hsrp) && isCondition(replaceTagValues(tmConfigurationApplInwardDobj.getTransactionNotAllowWithoutHSRP(), vehParameters), "ApplicationInward")) {
                        String[] vehArgs = {ownerDetail.getRegn_no(), ownerDetail.getState_cd(), "" + ownerDetail.getOff_cd() + ""};
                        String[] hsrpDetails = new HSRPRequestImpl().checkVehicleRegistration(vehArgs);
                        if (hsrpDetails != null && CommonUtils.isNullOrBlank(hsrpDetails[1])) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Vehicle HSRP not fitted. Please fit HSRP first."));
                            return;
                        }
                    }
                }
            }
            if (onlineApplDataList != null && !onlineApplDataList.isEmpty()) {

                //check of same office and state for which application is inwarded online
                if (ownerDetail != null && onlineApplDataList.get(0) != null
                        && onlineApplDataList.get(0).getOff_cd() != currentOffCd
                        && !onlineApplDataList.get(0).getState_cd().equalsIgnoreCase(currentStateCd)) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Online Application No-[" + onlineApplDataList.get(0).getAppl_no() + "] is not Applicable for current Office and current state, Please Check Office Name and State Name for this Application No."));
                    return;
                } else if (ownerDetail != null && onlineApplDataList.get(0) != null
                        && onlineApplDataList.get(0).getOff_cd() != currentOffCd
                        && onlineApplDataList.get(0).getState_cd().equalsIgnoreCase(currentStateCd) && "HR".equalsIgnoreCase(Util.getUserStateCode())) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Online Application No-[" + onlineApplDataList.get(0).getAppl_no() + "] is not Applicable for current Office, Please Check Office Name for this Application No."));
                    return;
                }

                if (onlineConfigDobj != null && onlineConfigDobj.isDocUpload()) {
                    inwardImpl.getOnlineApplicationDocUploadPendingDetails(regn_no, currentStateCd);
                }

                if (tmConfigApplAppointmentInwardDobj != null && tmConfigApplAppointmentInwardDobj.isAppointmentOnDate()) {
                    appointmentDt = ApplicationInwardImpl.getAppointmentDate(onlineApplDataList.get(0).getAppl_no(), ownerDobj);
                    if (appointmentDt != null) {
                        if ("GJ".contains(currentStateCd)) {
                            appointmentTakenOffice = ApplicationInwardImpl.getAppointmentTakenOffice(onlineApplDataList.get(0).getAppl_no(), ownerDobj);
                            if (appointmentTakenOffice > 0 && onlineApplDataList.get(0).getOff_cd() != appointmentTakenOffice) {
                                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Appointment for Online Application No-[" + onlineApplDataList.get(0).getAppl_no() + "] has not been taken for this office."));
                                return;
                            }
                        }
                        if (DateUtils.compareDates(appointmentDt, new Date()) == 1) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Appointment for Online Application No-[" + onlineApplDataList.get(0).getAppl_no() + "] has been expired on " + DateUtils.getDateInDDMMYYYY(appointmentDt) + ". Please Re-Schedule your appointment."));
                            return;
                        } else if (DateUtils.compareDates(appointmentDt, new Date()) == 2) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Appointment for Online Application No-[" + onlineApplDataList.get(0).getAppl_no() + "] is scheduled on dated " + DateUtils.getDateInDDMMYYYY(appointmentDt) + ". "));
                            return;
                        }
                    } else if ("GJ".contains(currentStateCd)) {
                        for (int i = 0; i < onlineApplDataList.size(); i++) {
                            if (tmConfigApplAppointmentInwardDobj.getRestrictedPurCdInwardOnline().contains("," + onlineApplDataList.get(i).getPur_cd() + ",")) {
                                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Appointment for Online Application No-[" + onlineApplDataList.get(i).getAppl_no() + "] has not been taken. Please take the Appointment first."));
                                return;
                            }
                        }
                    }
                }

                selectedPurposeCode = new ArrayList<>();
                Map<Object, Object> keyValueList = new LinkedHashMap<>();
                for (int i = 0; i < onlineApplDataList.size(); i++) {
                    applNos.append("'" + onlineApplDataList.get(i).getAppl_no() + "'");
                    if (i < onlineApplDataList.size() - 1) {
                        applNos.append(",");
                    }
                    Map<Object, Object> map = purCodeList;
                    for (Map.Entry<Object, Object> entry : map.entrySet()) {
                        if (onlineApplDataList.get(i).getPur_cd() == Integer.parseInt(entry.getValue().toString())) {
                            selectedPurposeCode.add(entry.getValue().toString());//for showing checkbox selected
                            keyValueList.put(entry.getKey().toString(), entry.getValue());
                            break;
                        }
                    }

                    if (onlineConfigDobj != null && onlineConfigDobj.isDocUpload() && !CommonUtils.isNullOrBlank(onlineConfigDobj.getPurCd()) && !docUploadAtOnlineService) {
                        if (onlineConfigDobj.getPurCd().contains(onlineApplDataList.get(i).getPur_cd() + "")) {
                            docUploadAtOnlineService = true;
                        }
                    }
                }
                purCodeList.clear();
                purCodeList = keyValueList;
            } else if (onlineApplDataList == null || onlineApplDataList.isEmpty()) {
                if (ownerDetail != null) {

                    int off_under_cd = impl.getoffUnderCd(currentOffCd, currentStateCd);
                    String anyWhereInwardPurCd = null;
                    if (tmConfigurationApplInwardDobj != null) {
                        anyWhereInwardPurCd = tmConfigurationApplInwardDobj.getPur_code();
                    }

                    auctionDobj = new AuctionImpl().getDetailsFromVtAuction(null, regn_no);
                    if (auctionDobj != null) {
                        if (auctionDobj.getStateCd().equals(currentStateCd)) {
                            if (ownerDetail != null && ownerDetail.getState_cd() != null && !ownerDetail.getState_cd().equals(currentStateCd)) {
                                throw new VahanException("Vehicle of Registration Number " + regn_no + " has been registered in State " + ServerUtil.getStateNameByStateCode(ownerDetail.getState_cd()) + " and " + ServerUtil.getOfficeName(ownerDetail.getOff_cd(), ownerDetail.getState_cd()) + ".Please do New Registration with registration type NEW REGN AGAINST AUCTION/CONFISCATED VEH.");
                            }
                            purCodeList.clear();
                            purCodeList.put(TableConstants.TRANSFER_OF_OWNERSHIP, TableConstants.VM_TRANSACTION_MAST_TO);
                            if (ownerDetail.getOwner_cd() == 5 || ownerDetail.getOwner_cd() == 15) {
                                purCodeList.put(TableConstants.REASSIGNMENT_OF_REGISTRATION_PURPOSE_DESCR, TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO);
                            }
                        } else {
                            throw new VahanException("Auction of Registration Number " + regn_no + " has been done in State " + ServerUtil.getStateNameByStateCode(auctionDobj.getStateCd()) + " and " + ServerUtil.getOfficeName(auctionDobj.getOffCd(), auctionDobj.getStateCd()) + "");
                        }
                    } else {
                        if (ownerDetail.getState_cd().equalsIgnoreCase(currentStateCd)) {
                            Util.getUserAuthority().getAssignedOffice();

                            if (anyWhereInwardPurCd != null && !anyWhereInwardPurCd.trim().isEmpty() && ownerDetail.getOff_cd() != currentOffCd) {
                                purCodeList.clear();
                                purCodeList = ServerUtil.getPurCodesDescrMap(anyWhereInwardPurCd);//It will return list of allowed pur_cd and description
                            } else if ((ownerDetail.getOff_cd() != currentOffCd && (!userCatg.equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_USER) && "AS".contains(currentStateCd))) || ((off_under_cd != ownerDetail.getOff_cd() && userCatg.equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_USER) && "JH,AS".contains(currentStateCd)))) {
                                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration Number not Found in Selected Office"));
                                return;
                            }
                        } else {
                            //for allowing fitness inspection in other state
                            purCodeList.clear();
                            purCodeList.put(TableConstants.FITNESS_INSPECTION_PURPOSE_DESCR, TableConstants.VM_TRANSACTION_MAST_FIT_CERT);
                        }
                    }
                } else {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration Number not Found in Selected Office"));
                    return;
                }
            }
            if (userCatg.equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_USER) && onlineApplDataList != null && !onlineApplDataList.isEmpty()) {

//                if (onlineApplDataList == null || onlineApplDataList.isEmpty()) {
//                    throw new VahanException("No Fitness Insepection + Cetificate Application found against Registration No " + regn_no);
//                }
//                if (onlineApplDataList != null && !onlineApplDataList.isEmpty()) {
                if (onlineApplDataList.size() > 1) {
                    int count = 0;
                    purCodeList.clear();
                    for (Status_dobj status_dobj : onlineApplDataList) {
                        if (status_dobj.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_FIT_CERT && "DL".equalsIgnoreCase(currentStateCd)) {
                            purCodeList.put(TableConstants.FITNESS_PURPOSE_DESCR, TableConstants.VM_TRANSACTION_MAST_FIT_CERT);
                            count++;
                        }
                    }
                    if (count == 0) {
                        throw new VahanException("You are Authorized to Inward Only Fitness Insepection + Cetificate, Multiple Application found on this Registration No" + regn_no);
                    }
                }
                if (onlineApplDataList.size() == 1 && onlineApplDataList.get(0).getPur_cd() != TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {
                    throw new VahanException("You are Authorized to Inward Only Fitness Insepection + Cetificate, Diffrent Application found on this Registration No" + regn_no);
                }
//                }
            } else if (userCatg.equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_USER) && (onlineApplDataList == null || onlineApplDataList.isEmpty())) {
                purCodeList.clear();
                if (ownerDetail.getState_cd().equalsIgnoreCase(currentStateCd)) {
                    purCodeList.put(TableConstants.FITNESS_PURPOSE_DESCR, TableConstants.VM_TRANSACTION_MAST_FIT_CERT);
                } else {
                    purCodeList.put(TableConstants.FITNESS_INSPECTION_PURPOSE_DESCR, TableConstants.VM_TRANSACTION_MAST_FIT_CERT);
                }

                // purCodeList.put(TableConstants.FITNESS_PURPOSE_DESCR, TableConstants.VM_TRANSACTION_MAST_FIT_CERT);
                if (!"RJ,JH".contains(currentStateCd)) {
                    purCodeList.put(TableConstants.DUPLICATE_FC_DESCR, TableConstants.VM_TRANSACTION_MAST_DUP_FC);
                    purCodeList.put(TableConstants.FITNESS_CANCELLATION, TableConstants.TM_PURPOSE_FITNESS_CANCELLATION);
                }

            }
            //for Owner Identification Fields disallow typing
            if (ownerDetail.getOwnerIdentity() != null) {
                //ownerDetail.getOwnerIdentity().setFlag(true);
                //ownerDetail.getOwnerIdentity().setMobileNoEditable(true);
                if (ownerDetail.getOwnerIdentity().getMobile_no() == null) {
                    ownerDetail.getOwnerIdentity().setMobile_no(0l);
                }
                if (ownerDetail.getOwnerIdentity().getOwnerCatg() == TableConstants.OWNER_CATG_OTHERS) {
                    ownerDetail.getOwnerIdentity().setOwnerCatgEditable(false);
                } else {
                    ownerDetail.getOwnerIdentity().setOwnerCatgEditable(true);
                }
                ownerIdentificationPrev = (OwnerIdentificationDobj) ownerDetail.getOwnerIdentity().clone();
            }

            OwnerImpl ownerImpl = new OwnerImpl();
            ownerDobj = ownerImpl.getOwnerDobj(ownerDetail);
            VehicleParameters vehParameters = fillVehicleParametersFromDobj(ownerDobj);
            // check for DL validation
            OwnerIdentificationImpl identificationImpl = new OwnerIdentificationImpl();
            TmConfigurationOwnerIdentificationDobj tmConfigOwnerId = identificationImpl.getTmConfigurationOwnerIdentification(currentStateCd);
            if (tmConfigOwnerId != null) {
                if (ownerDetail.getOwnerIdentity() == null) {
                    OwnerIdentificationDobj identificationDobj = new OwnerIdentificationDobj();
                    ownerDetail.setOwnerIdentity(identificationDobj);
                }

                if (tmConfigOwnerId.isDl_required() != null && isCondition(replaceTagValues(tmConfigOwnerId.isDl_required(), vehParameters), "isDl_required()")) {
                    ownerDetail.getOwnerIdentity().setDlRequired(true);
                }
//                if (tmConfigOwnerId.getPan_card_mandatory() != null && isCondition(replaceTagValues(tmConfigOwnerId.getPan_card_mandatory(), vehParameters), "getPan_card_mandatory1")) {
//                    ownerDetail.getOwnerIdentity().setPan_card_mandatory(true);
//                }
            }

            if (ownerDetail.getVehType() == TableConstants.VM_VEHTYPE_TRANSPORT) {
                permitDetailDobj = PermitDetailImpl.getPermitdetailsFromRegnNo(regn_no);
            }

            boolean isFitnessRequired = inwardImpl.getFitnessRequiredFor(ownerDobj, Util.getTmConfiguration());
            if (isFitnessRequired) {
                FitnessImpl fitImpl = new FitnessImpl();
                fitnessDobj = fitImpl.set_Fitness_appl_db_to_dobj(regn_no, null);
                ownerDetail.setFitnessDobj(fitnessDobj);
            }

            //#################### Insurance Details Filler Start ##############
            InsDobj ins_dobj = InsImpl.set_ins_dtls_db_to_dobjVA(this.regn_no.trim());
            if (ins_dobj != null) {
                ins_bean.set_Ins_dobj_to_bean(ins_dobj);
                ownerDetail.setInsDobj(ins_dobj);
                insuranceComparison = null;
            } else {
                ins_dobj = InsImpl.set_ins_dtls_db_to_dobj(this.regn_no.trim(), null, null, 0);
                if (ins_dobj != null) {
                    ins_bean.set_Ins_dobj_to_bean(ins_dobj);
                    ownerDetail.setInsDobj(ins_dobj);
                    insuranceComparison = ins_dobj.toString();
                }
            }
            //#################### Insurance Details Filler End ################
            boolean isblacklistedvehicle;
            TmConfigurationDobj tmConfig = Util.getTmConfiguration();
            isblacklistedvehicle = ServerUtil.isPurposeCodeOnBlacklistedVehicle(regn_no, Util.getSelectedSeat().getAction_cd(), currentStateCd, tmConfig);
            if (isblacklistedvehicle) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Vehicle No. has been Blacklisted."));
                return;
            }
            //Check for blacklisted
            if (blackListedDobj != null) {
                if ((blackListedDobj.getComplain_type() == TableConstants.BLTheftCode
                        || blackListedDobj.getComplain_type() == TableConstants.BLDestroyedAccidentCode)) {
                    ins_bean.setMin_dt(ownerDetail.getPurchase_date());
                }
                ownerDetail.setBlackListedVehicleDobj(blackListedDobj);
            }

            //for knowing that if vehicle registration is expired or not
            ApplicationInwardImpl applicationInwardImpl = new ApplicationInwardImpl();
            ownerDetail.setVehAgeExpire(applicationInwardImpl.isVehAgeExpired(ownerDobj, permitDetailDobj));

            //################### Hypothecation Details Filler Start ###########
            HpaImpl hpa_Impl = new HpaImpl();
            List<HpaDobj> hypth = hpa_Impl.getHypoDetails(null, TableConstants.VM_TRANSACTION_MAST_REM_HYPO, this.regn_no.trim(), true, currentStateCd);
            hypothecationDetails_bean.setListHypthDetails(hypth);
            //################### Hypothecation Details Filler End #############
            if (applicationInwardImpl.checkPermit(ownerDobj.getVh_class())) {
                String regnno = CommonUtils.formRegnNo(this.regn_no.trim());
                permitStatus = applicationInwardImpl.checkNationalPermitValidity(regnno);
            }

            regStatus = ServerUtil.fillRegStatusParameters(ownerDetail);

            if (permitDetailDobj == null) {//for permit surrender status
                permitSurrenderDetailDobj = PermitDetailImpl.getPermitSurrenderDetails(regn_no, ownerDetail.getState_cd());
                if (permitSurrenderDetailDobj != null) {

                    regStatus.setPermitSurrenderStatus(permitSurrenderDetailDobj.getTransactionPurCdDescr());//permit surrender reason

                    if ("DL".equalsIgnoreCase(currentStateCd)
                            && permitSurrenderDetailDobj.getTrans_pur_cd() == TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD
                            && permitSurrenderDetailDobj.getPur_cd() == TableConstants.VM_PMT_SURRENDER_PUR_CD) {
                        //for Delhi State. Case is for TSR
                        isTSR = PermitDetailImpl.isTSRallowed(regn_no, ownerDetail.getState_cd());
                    }
                }
            }
            if (!(ownerDobj.getOwner_cd() == TableConstants.VEH_TYPE_STATE_GOVT
                    || ownerDobj.getOwner_cd() == TableConstants.VEH_TYPE_GOVT_UNDERTAKING
                    || ownerDobj.getOwner_cd() == TableConstants.VEH_TYPE_STATE_TRANS_DEPT)) {
                //start of getting insurance details from service
                InsuranceDetailService detailService = new InsuranceDetailService();
                InsDobj insDobj = detailService.getInsuranceDetailsByService(regn_no, ownerDetail.getState_cd(), ownerDetail.getOff_cd());
                if (insDobj != null) {
                    if (insDobj.isIibData()) {
                        ins_bean.componentReadOnly(false);
                        ins_bean.setGovtVehFlag(true);
                    } else {
                        ins_bean.componentReadOnly(true);
                        ins_bean.setGovtVehFlag(false);
                    }
                    ins_bean.set_Ins_dobj_to_bean(insDobj);
                    ownerDetail.setInsDobj(insDobj);
                    insuranceComparison = null;
                    regStatus.setInsuranceStatus(false);
                }

            }

            renderHpaVarification = false;
            validateHpaVarification = false;
            renderVehAgeExpireVarfcn = false;
            vldtVehAgeExpireVarfcn = false;
            if (ownerDetail.isVehAgeExpire() && tmConfigurationApplInwardDobj != null
                    && tmConfigurationApplInwardDobj.getNcr_office() != null
                    && isCondition(replaceTagValues(tmConfigurationApplInwardDobj.getNcr_office(), vehParameters), "getNcr_office()")) {
                if (tmConfigurationApplInwardDobj.isVeh_age_verification_required()) {
                    renderVehAgeExpireVarfcn = true;//for displaying verification Message if vehicale age expire

                    String allowedPurCdWhenVehicleExpired = null;//for displaying transaction which is allowed only when vehicle is expired
                    if (tmConfigurationApplInwardDobj != null
                            && tmConfigurationApplInwardDobj.getTransactions_allowed_for_expired_vehicle() != null
                            && !tmConfigurationApplInwardDobj.getTransactions_allowed_for_expired_vehicle().trim().isEmpty()) {
                        allowedPurCdWhenVehicleExpired = getSqlFormulaValue(replaceTagValues(tmConfigurationApplInwardDobj.getTransactions_allowed_for_expired_vehicle(), vehParameters), "showDetails:allowedPurCdWhenVehicleExpired");
                        if (ownerDetail.getState_cd().equalsIgnoreCase(currentStateCd)
                                && allowedPurCdWhenVehicleExpired != null
                                && !allowedPurCdWhenVehicleExpired.trim().isEmpty()) {
                            if (!ownerDetail.getState_cd().equalsIgnoreCase(currentStateCd)) {
                                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "vehicle age as per state policy has been expired, Some transactions are allowed only where the vehicle is registered."));
                                return;
                            }
                            purCodeList.clear();
                            purCodeList = ServerUtil.getPurCodesDescrMap(allowedPurCdWhenVehicleExpired);//It will return list of allowed pur_cd and description
                        }
                    }

                } else {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "vehicle age as per state policy has been expired, No transaction is possible on this Vehicle No-" + regn_no.toUpperCase()));
                    return;
                }
            }

            render = true;

            if (ownerDobj != null) {
                taxDetaillist = TaxClearImpl.getTaxDetaillist(regn_no, ownerDetail.getState_cd());
                if (taxDetaillist == null || taxDetaillist.isEmpty()) {
                    taxDetaillist = null;
                }
            }
            if (currentStateCd.equals("KL")) {
                if (ownerDetail.getPush_bk_seat() != 0 || ownerDetail.getOrdinary_seat() != 0) {
                    ownerDetail.setPushBkSeatRender(true);
                } else {
                    ownerDetail.setPushBkSeatRender(false);
                }
            }

            //getting eChallan Details
            eChallanInfo = applicationInwardImpl.getEChallanInfo(regn_no);
            if (eChallanInfo == null || eChallanInfo.isEmpty()) {
                eChallanInfo = SaveChallanImpl.getVahaneChallanInfo(regn_no);
            }
//            if (eChallanInfo != null && !eChallanInfo.isEmpty()) {
//                echallanInfoURL = "/ui/eChallan/eChallanInfo.xhtml";
//                // PrimeFaces.current().executeScript("PF('varEchallan').show();");
//            }

            // Uploaded Document View
            if (onlineApplDataList != null && !onlineApplDataList.isEmpty()) {
                String appl_no = DocumentUploadImpl.checkVahanEServiceUploadedDocument(regn_no, currentStateCd, applNos.toString());
                if (!CommonUtils.isNullOrBlank(appl_no) || docUploadAtOnlineService) {
                    documentUpload_bean.setApplNo(!CommonUtils.isNullOrBlank(appl_no) ? appl_no : onlineApplDataList.get(0).getAppl_no());
                    List<VTDocumentModel> uploadDocList = DmsDocCheckUtils.getUploadedDocumentList(documentUpload_bean.getApplNo());
                    if (!uploadDocList.isEmpty() && uploadDocList.size() > 0) {
                        documentUploadShow = true;
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "You can view uploaded documents in 'Document Details' tab !!!"));
                    }
                }
            }
            if (tmConfig != null) {
                TmConfigurationOtpDobj tmConfigurationOtpDobj = tmConfig.getTmConfigOtpDobj();
                if (tmConfigurationOtpDobj != null && tmConfigurationOtpDobj.isMobile_no_count_with_otp()) {
                    long mobileNo = ownerDetail.getOwnerIdentity().getMobile_no();
                    mobileNoCountMessage = ServerUtil.getMobileNoCountMessage(mobileNo);
                }
            }

        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error!", "Registration Number Not Found"));
        }
    }

    public void delDuplicateVehDetails(OwnerDetailsDobj ownerDtls) {
        try {
            //For restricting the user to generate application no again and again.start           
            if ((statusList == null || statusList.isEmpty())) {
                statusList = ServerUtil.applicationStatus(this.regn_no.trim());
            }
            if (!statusList.isEmpty()) {
                PrimeFaces.current().executeScript("PF('varInwardedApplNo').show()");
                return;
            }
            if ("TN".equals(currentStateCd) && moveToHistoryOwnerDtls == null && ownerDtls != null && ownerDtls.getHpaDobj() != null) {
                List<HpaDobj> hypth = new HpaImpl().getHypothecationList(ownerDetail.getRegn_no(), ownerDetail.getState_cd(), currentOffCd);
                if (hypth == null || hypth.isEmpty()) {
                    moveToHistoryOwnerDtls = ownerDtls;
                    currentOfficeName = ServerUtil.getOfficeName(currentOffCd, currentStateCd);
                    PrimeFaces.current().ajax().update("confirmDialog");
                    PrimeFaces.current().executeScript("PF('confirmDialogVar').show()");
                    return;
                }
            }
            boolean isDelete = new DelDupRegnNoImpl().deleteDuplicateVehicle(ownerDtls);
            if (isDelete) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Registration Number Deleted Successfully"));
                PrimeFaces.current().executeScript("PF('varDupRegNo').hide()");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void moveHistoryDuplicateVehDetails(String moveHPAType) {
        try {
            if (moveToHistoryOwnerDtls != null) {
                moveToHistoryOwnerDtls.setMoveHPADetails(moveHPAType);
                boolean isDelete = new DelDupRegnNoImpl().deleteDuplicateVehicle(moveToHistoryOwnerDtls);
                if (isDelete) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Registration Number Deleted Successfully"));
                    PrimeFaces.current().executeScript("PF('varDupRegNo').hide()");
                    PrimeFaces.current().executeScript("PF('confirmDialogVar').hide()");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public String saveApplicationNo() {

        String appl_no = null;
        List<Status_dobj> listStatus_dobj = null;
        boolean skipPendingApplicationCheck = false;
        boolean insuranceSkip = false;
        boolean puccCheck = false;
        PuccDobj puccDobj = null;

        if (selectedPurposeCode == null || selectedPurposeCode.isEmpty()) {
            JSFUtils.showMessagesInDialog("Alert!", "You have Not Selected any Option", FacesMessage.SEVERITY_WARN);
            return "";
        }
        try {

            if (validateApplInward()) {
                return "";
            }

            listStatus_dobj = new ArrayList<>();

            //In Maharashtra checked for invalid permit category during Fitness Renewal
            if ("MH".equalsIgnoreCase(currentStateCd)) {
                if (selectedPurposeCode.contains(String.valueOf(TableConstants.VM_TRANSACTION_MAST_FIT_CERT))
                        && (permitDetailDobj != null)) {
                    FitnessImpl fitImpl = new FitnessImpl();
                    ownerDobj.setPmt_type(permitDetailDobj.getPmt_type());
                    ownerDobj.setPmt_catg(permitDetailDobj.getPmt_catg());
                    fitImpl.getVehAgeValidity(ownerDobj);

                }
            }

            for (int i = 0; i < selectedPurposeCode.size(); i++) {
                if (tmConfigApplAppointmentInwardDobj != null && tmConfigApplAppointmentInwardDobj.isAppointmentOnDate()
                        && !(CommonUtils.isNullOrBlank(tmConfigApplAppointmentInwardDobj.getRestrictedPurCdInwardOnline()))
                        && tmConfigApplAppointmentInwardDobj.getRestrictedPurCdInwardOnline().contains("," + selectedPurposeCode.get(i) + ",")
                        && (onlineApplDataList == null || onlineApplDataList.isEmpty())) {
                    PrimeFaces.current().dialog().showMessageDynamic(
                            new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!",
                                    "Application Can't Inward for " + ServerUtil.getTaxHead(Integer.parseInt(selectedPurposeCode.get(i))) + ", Please Apply Online for Appointment"));
                    return null;
                }
                if (Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_OWNER
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_COMPANY
                        || (Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_MAST_NON_USE && selectedPurposeCode.size() == 1)
                        || (Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE && selectedPurposeCode.size() == 1)
                        /*fitness appl allowed if any appl pending for approval in Delhi*/
                        || (Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_FIT_CERT && selectedPurposeCode.size() == 1)) {
                    skipPendingApplicationCheck = true;
                }

                if (Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_MAST_RC_CANCELLATION
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_FRESH_RC
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_OWNER
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_COMPANY
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_MAST_NON_USE
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_MAST_VEHICLE_SCRAPE
                        || auctionDobj != null) {
                    insuranceSkip = true;
                }

                if (Integer.parseInt(selectedPurposeCode.get(i)) != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS
                        && Integer.parseInt(selectedPurposeCode.get(i)) != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_OWNER
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_COMPANY
                        && Integer.parseInt(selectedPurposeCode.get(i)) != TableConstants.VM_MAST_RC_SURRENDER
                        && Integer.parseInt(selectedPurposeCode.get(i)) != TableConstants.VM_MAST_RC_CANCELLATION
                        && Integer.parseInt(selectedPurposeCode.get(i)) != TableConstants.VM_MAST_RC_RELEASE
                        && Integer.parseInt(selectedPurposeCode.get(i)) != TableConstants.VM_MAST_NON_USE
                        && Integer.parseInt(selectedPurposeCode.get(i)) != TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE
                        && Integer.parseInt(selectedPurposeCode.get(i)) != TableConstants.VM_MAST_VEHICLE_SCRAPE
                        && Integer.parseInt(selectedPurposeCode.get(i)) != TableConstants.VM_DUPLICATE_TO_TAX_CARD) {
                    puccCheck = true;
                }
                if ("GA".contains(currentStateCd)
                        && ownerDobj.getOther_criteria() == TableConstants.OTHER_CRITERIA_MINE_VEH
                        && selectedPurposeCode.size() == 1
                        && (Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_REM_HYPO
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_HPC)) {
                    puccCheck = false;
                    insuranceSkip = true;
                }
                Status_dobj status_dobj = new Status_dobj();
                status_dobj.setRegn_no(this.regn_no.toUpperCase().trim());
                status_dobj.setPur_cd(Integer.parseInt(selectedPurposeCode.get(i)));
                //status_dobj.setFlow_slno(1);//initial flow serial no.
                //status_dobj.setFile_movement_slno(1);//initial file movement serial no.
                status_dobj.setState_cd(currentStateCd);
                status_dobj.setOff_cd(currentOffCd);
                status_dobj.setEmp_cd(0);
                status_dobj.setSeat_cd("N");
                status_dobj.setCntr_id("");
                status_dobj.setStatus("N");
                status_dobj.setOffice_remark("");
                if (renderVehAgeExpireVarfcn && vldtVehAgeExpireVarfcn) {
                    if (officeRemarks == null || officeRemarks.isEmpty() || officeRemarks.trim().length() <= 5) {
                        PrimeFaces.current().dialog().showMessageDynamic(
                                new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!",
                                        "Expired vehicle verification Remarks can not be empty and must be more than 5 character."));
                        return null;
                    }
                    status_dobj.setOffice_remark("VEHICLE AGE EXPIRE: " + officeRemarks);
                }
                status_dobj.setPublic_remark("");
                status_dobj.setFile_movement_type("F");
                status_dobj.setUser_id(Util.getUserId());
                status_dobj.setAppl_date(ServerUtil.getSystemDateInPostgres());
                status_dobj.setUser_type("");
                status_dobj.setEntry_ip(Util.getClientIpAdress());
                status_dobj.setEntry_status("");
                if (Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_PMT_PERMIT_PARTICULAR_PUR_CD_WITHOUT_FEE) {
                    status_dobj.setEntry_status("Y");
                } else {
                    status_dobj.setEntry_status("");
                }
                status_dobj.setConfirm_ip("");
                status_dobj.setConfirm_status("");
                status_dobj.setConfirm_date(new java.util.Date());

                listStatus_dobj.add(status_dobj);
            }

            //skip pucc check
            if (ownerDetail != null && ownerDetail.getBlackListedVehicleDobj() != null
                    && (ownerDetail.getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLTheftCode
                    || ownerDetail.getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLDestroyedAccidentCode
                    || ownerDetail.getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLLoanDefaulterCode)) {
                puccCheck = false;
            }

            //pucc check
            if (puccCheck) {
                InsPuccUpdateImpl puccUpdateImpl = new InsPuccUpdateImpl();
                VehicleParameters vehParameters = null;
                if (ownerDobj != null) {
                    vehParameters = fillVehicleParametersFromDobj(ownerDobj);
                }

                TmConfigurationPUCCdobj configurationPUCCdobj = puccUpdateImpl.getTmConfigurationPUCCdobj(currentStateCd);
                if (configurationPUCCdobj != null
                        && configurationPUCCdobj.getExpired_pucc_check() != null
                        && vehParameters != null && configurationPUCCdobj.getExpired_pucc_check() != null
                        && isCondition(replaceTagValues(configurationPUCCdobj.getExpired_pucc_check(), vehParameters), "saveApplicationNo")) {
                    if (puccDobj == null) {
                        puccDobj = puccUpdateImpl.getPuccDetails(regn_no);
                    }
                    if (!userCatg.equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_USER)) {
                        if (puccDobj == null || (puccDobj.getPuccUpto() != null && DateUtils.compareDates(new Date(), puccDobj.getPuccUpto()) == 2)) {
                            getSelectedSeat().setPuccCheck(true);
                            getSelectedSeat().setRegn_no(regn_no);
                            getSelectedSeat().setOff_cd(currentOffCd);
                            Util.getSession().setAttribute("SelectedSeat", selectedSeat);
                            return "/ui/registration/formUpdateInsurancePollutionDetails.xhtml?faces-redirect=true";
                        }
                    } else if (userCatg.equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_USER)
                            && (puccDobj == null || puccDobj.getPuccUpto() == null
                            || (puccDobj.getPuccUpto() != null && DateUtils.compareDates(new Date(), puccDobj.getPuccUpto()) == 2))) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Valid PUCC details not found, please visit Registring Authority to Update latest PUCC details."));
                        return "";
                    }
                }
            }

            if (!regStatus.isInsuranceStatus() && !insuranceSkip) {
                if (!InsImpl.validateOwnerCodeWithInsType(ownerDetail.getOwner_cd(), ins_bean.getInsType())) {
                    PrimeFaces.current().dialog().showMessageDynamic(
                            new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!",
                                    "Invalid Combination of Ownership Type & Insurance Type"));
                    return "";
                }
                InsDobj insDobj = ins_bean.set_InsBean_to_dobj();
                insDobj.setRegn_no(regn_no);
                ownerDetail.setInsDobj(insDobj);
                if ("RJ".equalsIgnoreCase(currentStateCd) && userCatg.equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_USER)
                        && (ownerDetail.getInsDobj() == null || ownerDetail.getInsDobj().getIns_upto() == null
                        || (ownerDetail.getInsDobj().getIns_upto() != null && DateUtils.compareDates(new Date(), ownerDetail.getInsDobj().getIns_upto()) == 2))) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Valid Insurance details not found, please visit Registring Authority to Update latest Insurance details."));
                    return "";
                }
                if (insuranceSkip) {
                    ownerDetail.setInsDobj(null);
                }
                /*InsImpl insImpl = new InsImpl();
                 if (!insImpl.validateInsurance(insDobj)) {
                 PrimeFaces.current().dialog().showMessageDynamic(
                 new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!",
                 "Insurance is Expired. Please Update the Insurance Details"));
                 return "";
                 }*/
            }

            //For restricting the user to generate application no again and again.start           
            //  if (statusList == null || statusList.isEmpty()) {
            if ((statusList == null || statusList.isEmpty())) {
                if (!((selectedPurposeCode.contains(String.valueOf(TableConstants.VM_MAST_NON_USE))
                        || selectedPurposeCode.contains(String.valueOf(TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE)))
                        && "OR".equals(currentStateCd))) {

                    statusList = ServerUtil.applicationStatus(this.regn_no.trim());

                    //**************************************************************
                    for (Status_dobj status_dobj : statusList) {
                        if (status_dobj.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_FIT_CERT
                                && fitnessDobj != null) {
                            //for allowing other transaction for inward if fitness renewal is not expired and pending for approval
                            if (fitnessDobj.getFit_valid_to() != null
                                    && (DateUtils.compareDates(fitnessDobj.getFit_valid_to(), new Date()) == 0
                                    || DateUtils.compareDates(fitnessDobj.getFit_valid_to(), new Date()) == 2)) {
                                skipPendingApplicationCheck = true;
                            }
                            break;
                        }
                        //If NonUse Application already pending to approval then Restrict to Inward the VM_MAST_NON_USE_RESTORE_REMOVE
                        if (status_dobj.getPur_cd() == TableConstants.VM_MAST_NON_USE && selectedPurposeCode.contains(String.valueOf(TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE))
                                || status_dobj.getPur_cd() == TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE && selectedPurposeCode.contains(String.valueOf(TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE))) {
                            skipPendingApplicationCheck = false;
                            break;
                        }

                        if (((status_dobj.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD
                                || (status_dobj.getPur_cd() == TableConstants.VM_PMT_RENEWAL_PUR_CD))
                                || (status_dobj.getPur_cd() == TableConstants.VM_PMT_TRANSFER_PUR_CD)
                                || (status_dobj.getPur_cd() == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD)
                                || (status_dobj.getPur_cd() == TableConstants.VM_PMT_SURRENDER_PUR_CD)
                                || (status_dobj.getPur_cd() == TableConstants.VM_PMT_TEMP_SUR_PUR_CD)
                                || (status_dobj.getPur_cd() == TableConstants.VM_PMT_CANCELATION_PUR_CD)
                                || (status_dobj.getPur_cd() == TableConstants.VM_PMT_DUPLICATE_PUR_CD) && "CG".equalsIgnoreCase(currentStateCd))) {

                            for (int i = 0; i < selectedPurposeCode.size(); i++) {
                                if (((selectedPurposeCode.get(i).equalsIgnoreCase(String.valueOf(TableConstants.VM_TRANSACTION_MAST_FIT_CERT)))
                                        || (selectedPurposeCode.get(i).equalsIgnoreCase(String.valueOf(TableConstants.VM_TRANSACTION_MAST_DUP_FC)))
                                        || (selectedPurposeCode.get(i).equalsIgnoreCase(String.valueOf(TableConstants.TM_PURPOSE_FITNESS_CANCELLATION)))
                                        || (selectedPurposeCode.get(i).equalsIgnoreCase(String.valueOf(TableConstants.VM_TRANSACTION_MAST_RC_TO_SMART_CARD_CONVERSION)))
                                        || (selectedPurposeCode.get(i).equalsIgnoreCase(String.valueOf(TableConstants.VM_TRANSACTION_MAST_FIT_REVOKE)))
                                        || (selectedPurposeCode.get(i).equalsIgnoreCase(String.valueOf(TableConstants.VM_TRANSACTION_MAST_ADD_HYPO)))
                                        || (selectedPurposeCode.get(i).equalsIgnoreCase(String.valueOf(TableConstants.VM_TRANSACTION_MAST_REM_HYPO)))
                                        || (selectedPurposeCode.get(i).equalsIgnoreCase(String.valueOf(TableConstants.VM_TRANSACTION_MAST_HPC)))
                                        || (selectedPurposeCode.get(i).equalsIgnoreCase(String.valueOf(TableConstants.VM_TRANSACTION_MAST_DUP_RC)))
                                        || (selectedPurposeCode.get(i).equalsIgnoreCase(String.valueOf(TableConstants.VM_MAST_NON_USE)))
                                        || (selectedPurposeCode.get(i).equalsIgnoreCase(String.valueOf(TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE)))
                                        || (selectedPurposeCode.get(i).equalsIgnoreCase(String.valueOf(TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE)))
                                        || (selectedPurposeCode.get(i).equalsIgnoreCase(String.valueOf(TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_OWNER)))
                                        || (selectedPurposeCode.get(i).equalsIgnoreCase(String.valueOf(TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_COMPANY)))
                                        || (selectedPurposeCode.get(i).equalsIgnoreCase(String.valueOf(TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS)))) && (selectedPurposeCode.size() == 1)) {
                                    skipPendingApplicationCheck = true;
                                }
                            }
                        }
                    }

                    //**************************************************************  
                }

            }
            if (statusList != null && !statusList.isEmpty() && !skipPendingApplicationCheck) {
                PrimeFaces.current().executeScript("PF('varInwardedApplNo').show()");
                return null;
            }

            //For restricting the user to generate application no again and again.end
            List<ComparisonBean> compareChanges = compareChanges(ownerDetail);
            ApplicationInwardImpl impl = new ApplicationInwardImpl();
            appl_no = impl.saveAndGenerateApplication(listStatus_dobj, onlineApplDataList, ownerDetail, ComparisonBeanImpl.changedDataContents(compareChanges), regStatus, insuranceComparison, conv_dobj);

            seatWork = Home_Impl.seatWorkList(String.valueOf(currentOffCd), appl_no, "", "applNo", null, null);

            if (seatWork != null && !seatWork.isEmpty() && seatWork.size() == 1) { //redirect when only one application is generated for particular registration

                selectedSeat.setAppl_no(seatWork.get(0).getAppl_no());
                selectedSeat.setPur_cd(seatWork.get(0).getPur_cd());
                selectedSeat.setAction_cd(seatWork.get(0).getAction_cd());
                selectedSeat.setPurpose_descr(seatWork.get(0).getPurpose_descr());
                selectedSeat.setOffice_remark(seatWork.get(0).getOffice_remark());
                selectedSeat.setRemark_for_public(seatWork.get(0).getRemark_for_public());
                selectedSeat.setRegn_no(seatWork.get(0).getRegn_no());
                selectedSeat.setAppl_dt(seatWork.get(0).getAppl_dt());
                selectedSeat.setStatus(seatWork.get(0).getStatus());
                selectedSeat.setRedirect_url(seatWork.get(0).getRedirect_url());

                String uri = selectedSeatWork();

                if (uri != null) {
                    return uri;
                }
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Could not Generate Application No Due to " + ve.getMessage(), "Could not Generate Application No Due to " + ve.getMessage()));
            return null;
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            System.out.print("ApplicationInwardBean StackTrace: ");
            e.printStackTrace();
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0] + " ---> " + e.getStackTrace()[2]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return null;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return null;
        }

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Application Number is Generted Successfully.Application Number is : " + appl_no, ""));
        render = false;
        regn_no = "";
        selectedPurposeCode.clear();
        renderHpaVarification = false;
        validateHpaVarification = false;
        PrimeFaces.current().ajax().update("panelOwnerInfo");
        return "";
    }

    public boolean validateApplInward() throws Exception {
        boolean flag = false;

        if (!selectedPurposeCode.isEmpty()) {

            List<Integer> purCd = new ArrayList<>();
            for (int i = 0; i < selectedPurposeCode.size(); i++) {
                purCd.add(Integer.parseInt(selectedPurposeCode.get(i)));
            }

            OwnerImpl ownerImpl = new OwnerImpl();
            Owner_dobj ownerDobj = ownerImpl.getOwnerDobj(ownerDetail);
            flag = performAllCheck(regn_no, purCd, ownerDobj, false);
        }

        return flag;
    }//end of validateApplInward

    public boolean performAllCheck(String regn_no, List<Integer> purCd, Owner_dobj ownerDobj, boolean regnNoIsNewforSwapping) throws Exception {
        boolean result = false;
        boolean isHypth = false;
        String vtOwnerStatus = "";
        String appl_no = null;
        Date fit_upto = null;
        Date currentDate = new Date();
        int vhType = 0;
        int vchAge = 0;
        boolean rcParticularCheck = false;
        boolean rcCheck = false;
        String facesMessages = "No Messages";
        boolean fitnessCheck = false;
        boolean toWithTheft = false;
        boolean fitnessReqFor = false;
        boolean renewRegReqFor = false;
        boolean isHptAppl = false;
        boolean isVehicleScrapAppl = false;
        boolean isToAppl = false;
        boolean isVehValidityExpired = false;
        boolean isRenewalRegAppl = false;
        VehicleParameters vehParameters = null;
        boolean isReassignmentAllowed = false;
        boolean isRevocationAllowed = false;
        TmConfigurationFitnessDobj tmConfigurationFitnessDobj = null;
        TmConfigurationNonUseDobj tmConfigurationNonuseDobj = Util.getTmConfigurationNonUse();
        Date taxDueFrom = TaxServer_Impl.getTaxDueFromDate(ownerDobj, TableConstants.TM_ROAD_TAX);
        if (taxDueFrom != null) {
            tmConfigurationServiceExemption = ServerUtil.getTmConfigurationServiceExemption(currentStateCd, taxDueFrom);
        }

        if (purCd == null || purCd.isEmpty()) {
            return result;
        }
        if (regn_no != null) {
            regn_no = regn_no.toUpperCase();
        }

        if (ownerDobj == null) {
            throw new VahanException("Owner Details not Found, Please Contact to the System Administrator.");
        }
        vehParameters = fillVehicleParametersFromDobj(ownerDobj);
        vtOwnerStatus = ownerDobj.getStatus();
        vhType = ownerDobj.getVehType();
        fit_upto = ownerDobj.getFit_upto();
        isVehValidityExpired = ownerDobj.isVehAgeExpire();
        if (vtOwnerStatus.equalsIgnoreCase(TableConstants.VT_DE_REGISTRATION)) {
            for (int i = 0; i < purCd.size(); i++) {
                if ((purCd.get(i) != TableConstants.VM_MAST_VEHICLE_SCRAPE) && (purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_REM_HYPO)) {
                    facesMessages = "Cant't Inward Application because Vehicle is DE-REGISTERED. Only Scrapping and HPT Allowed";
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
                    PrimeFaces.current().ajax().update("msgDialog");
                    PrimeFaces.current().executeScript("PF('messageDialog').show();");
                    result = true;
                    return result;
                }
            }
        }

        try {
            NcrbWsClient ncrbWS = new NcrbWsClient();
            String NcrbMessage = ncrbWS.callNcrbService(regn_no);
            ArrayList<Integer> apList = new ArrayList<Integer>(); //Allowed purpose list
            apList.add(TableConstants.VM_TRANSACTION_MAST_TO);
            apList.add(TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS);
            apList.add(TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE);
            apList.add(TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_OWNER);
            apList.add(TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_COMPANY);
            apList.add(TableConstants.VM_TRANSACTION_MAST_REM_HYPO);
            apList.add(TableConstants.VM_MAST_RC_SURRENDER);
            apList.add(TableConstants.VM_MAST_RC_CANCELLATION);
            apList.add(TableConstants.VM_TRANSACTION_MAST_FRESH_RC);
            apList.add(TableConstants.VM_TRANSACTION_MAST_DUP_RC);
            apList.add(TableConstants.VM_MAST_VEHICLE_SCRAPE);
            apList.add(TableConstants.SWAPPING_REGN_PUR_CD);
            if (!NcrbMessage.isEmpty()) {
                if (!apList.containsAll(purCd)) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert", NcrbMessage));
                    result = true;
                    return result;
                } else {
                    if (purCd.contains(TableConstants.VM_MAST_VEHICLE_SCRAPE) || purCd.contains(TableConstants.SWAPPING_REGN_PUR_CD)) {
                        BlackListedVehicleImpl obj = new BlackListedVehicleImpl();
                        List<BlackListedVehicleDobj> untracedblackListedDobj = obj.getUntracedRCDetail(regn_no, null);
                        if (untracedblackListedDobj.size() < 1) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert", NcrbMessage));
                            result = true;
                            return result;
                        }
                    }
                    if (purCd.contains(TableConstants.VM_TRANSACTION_MAST_TO)) {
                        if (ownerDobj.getBlackListedVehicleDobj() != null && ownerDobj.getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLTheftCode) {
                            toWithTheft = true;
                        } else {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert", "As per NCRB the vehicle is showing stolen, please use the option <b>'Entry-Blackisted Vehicle'</b> in VAHAN to blacklist first.</br>After that select the option <b>'Transfer of Ownership'</b> in inward application. The vehicle will be transfered in the name of Insurance Company Only.</br></br>" + NcrbMessage));
                            result = true;
                            return result;
                        }
                    }
                }

            } else if (!permitStatus.isEmpty()) {
                apList.remove(TableConstants.VM_TRANSACTION_MAST_TO); // Transfer of ownership allowed
                apList.remove(TableConstants.VM_TRANSACTION_MAST_FRESH_RC);
                apList.add(TableConstants.VM_TRANSACTION_MAST_NOC);
                apList.remove(TableConstants.VM_TRANSACTION_MAST_DUP_RC);
                apList.remove(TableConstants.VM_MAST_VEHICLE_SCRAPE);
                if (!apList.containsAll(purCd)) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert", "Application can not be inward" + permitStatus));
                    result = true;
                    return result;
                }

            }
        } catch (Exception ex) {
            //LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }

        if (fitnessDobj != null && fitnessDobj.getFit_valid_to() != null
                && (fitnessDobj.getFit_result().equalsIgnoreCase(TableConstants.FitnessResultPass) || fitnessDobj.getFit_result().equalsIgnoreCase("P")/*Fitness Result if Pass value will be P for non vahan4 data*/)
                && DateUtils.compareDates(fit_upto, fitnessDobj.getFit_valid_to()) == 1) {
            fit_upto = fitnessDobj.getFit_valid_to();
        }
        currentDate = DateUtils.parseDate(DateUtils.getCurrentDate());

        ApplicationInwardImpl applicationInwardImpl = new ApplicationInwardImpl();
        FitnessImpl fitnessImpl = new FitnessImpl();
        TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
        tmConfigurationFitnessDobj = fitnessImpl.getFitnessConfiguration(currentStateCd);
        if (tmConfigurationDobj == null) {
            tmConfigurationDobj = ServerUtil.getTmConfigurationParameters(currentStateCd);
        }

        boolean taxPaidOrClear = applicationInwardImpl.taxPaidOrClearedStatus(regn_no, tmConfigurationDobj, ownerDobj, TableConstants.TM_ROAD_TAX);
        vchAge = DateUtils.getDate1MinusDate2_Months(ownerDobj.getRegn_dt(), currentDate);
        if (ownerDobj.getState_cd().equalsIgnoreCase("MH") && !taxPaidOrClear && vchAge >= 180) {
            taxPaidOrClear = applicationInwardImpl.taxPaidOrClearedStatus(regn_no, tmConfigurationDobj, ownerDobj, TableConstants.TM_ENVIRONMENT_TAX);
        }

        if (ownerDobj.getState_cd().equalsIgnoreCase("MZ") && ownerDobj.getVehType() == TableConstants.VM_VEHTYPE_TRANSPORT) {
            taxPaidOrClear = applicationInwardImpl.taxPaidOrClearedStatus(regn_no, tmConfigurationDobj, ownerDobj, TableConstants.TM_ADDN_ROAD_TAX);
        }

        if ("TN".contains(ownerDobj.getState_cd())) {
            for (int i = 0; i < purCd.size(); i++) {
                if (purCd.get(i) != TableConstants.VM_MAST_RC_RELEASE
                        && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS
                        && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE
                        && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_COMPANY
                        && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_OWNER
                        && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_NOC_CANCEL
                        && purCd.get(i) != TableConstants.VM_MAST_VEHICLE_SCRAPE
                        && purCd.get(i) != TableConstants.VM_DUPLICATE_TO_TAX_CARD) {
                    String[] taxModes = TaxServer_Impl.getAvailalableTaxModes(ownerDobj, TableConstants.VM_TRANSACTION_MAST_GREEN_TAX, null);
                    if (taxModes != null && taxModes.length > 0) {
                        taxPaidOrClear = applicationInwardImpl.taxPaidOrClearedStatus(regn_no, tmConfigurationDobj, ownerDobj, TableConstants.VM_TRANSACTION_MAST_GREEN_TAX);
                        if (!taxPaidOrClear) {
                            facesMessages = "Green Tax is not Paid by this Registration No. " + regn_no.trim() + " Please Clear Green Tax First or Fill the Tax Exemption Details if Applicable.";
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
                            PrimeFaces.current().ajax().update("msgDialog");
                            PrimeFaces.current().executeScript("PF('messageDialog').show();");
                            result = true;
                            return result;
                        }
                    }
                }
            }
        }

        if (auctionDobj != null) {
            taxPaidOrClear = true;
        }

        if (eChallanInfo != null && !eChallanInfo.isEmpty()) {
            for (ChallanReportDobj dobj : eChallanInfo) {
                if (dobj.getChal_date() != null) {
                    long countEchallanDays = DateUtils.getDate1MinusDate2_Days(dobj.getChal_date(), new Date());
                    if (countEchallanDays >= 90) {
                        restrictPurposeforEchallan = true;
                        break;
                    }
                }
            }
            for (int i = 0; i < purCd.size(); i++) {
                if ((tmConfigurationDobj.getEchallan_pur_cd_restict().contains(purCd.get(i).toString())) || (restrictPurposeforEchallan && (purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS
                        && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_FIT_CERT
                        && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE
                        && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_COMPANY
                        && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_OWNER
                        && purCd.get(i) != TableConstants.VM_MAST_NON_USE
                        && purCd.get(i) != TableConstants.VM_MAST_RC_CANCELLATION
                        && purCd.get(i) != TableConstants.VM_MAST_RC_RELEASE
                        && purCd.get(i) != TableConstants.VM_MAST_RC_SURRENDER
                        && purCd.get(i) != TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE))) {
                    echallanInfoURL = "/ui/eChallan/eChallanInfo.xhtml";
                    PrimeFaces.current().executeScript("PF('varEchallan').show()");
                    PrimeFaces.current().ajax().update("eChallanInfoPanel");
                    result = true;
                    return result;
                }
            }

        }

        boolean isNonUse = NonUseImpl.nonUseDetailsExist(ownerDobj.getRegn_no(), ownerDobj.getState_cd());
        if (!taxPaidOrClear && "DL,UP".contains(currentStateCd) && vhType == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
            taxPaidOrClear = true;
        }
        if (ownerDobj != null && ownerDobj.getOwner_sr() == 0) {
            for (int i = 0; i < purCd.size(); i++) {
                if (purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_TO) {
                    facesMessages = "Can't inward Application, Owner Serial No found ZERO for selected vehicle, please contact State Administrator.";
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
                    PrimeFaces.current().ajax().update("msgDialog");
                    PrimeFaces.current().executeScript("PF('messageDialog').show();");
                    result = true;
                    return result;
                }
            }
        }
        //for handling case when vehicle comes for Vehicle Conversion within same state to different RTO
        if (!taxPaidOrClear && ownerDobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)
                && currentStateCd.equalsIgnoreCase("DL")) {
            for (int i = 0; i < purCd.size(); i++) {
                if (purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION) {
                    taxPaidOrClear = applicationInwardImpl.taxPaidOrClearedStatusOnVehType(regn_no);
                    break;
                }
            }
        }
        //for Skipping Tax Constraints in Particular Cases Start
        if (!taxPaidOrClear) {
            for (int i = 0; i < purCd.size(); i++) {
                if (purCd.get(i) == TableConstants.VM_MAST_RC_RELEASE
                        || ((tmConfigurationServiceExemption != null && tmConfigurationServiceExemption.getPur_cd().contains("," + purCd.get(i) + ",") && purCd.size() == 1 && tmConfigurationServiceExemption.isTax_exemption()))
                        || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS
                        || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE
                        || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_COMPANY
                        || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_OWNER
                        || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_NOC_CANCEL
                        || purCd.get(i) == TableConstants.VM_MAST_VEHICLE_SCRAPE
                        || purCd.get(i) == TableConstants.VM_DUPLICATE_TO_TAX_CARD
                        || purCd.get(i) == TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE
                        || purCd.get(i) == TableConstants.VM_MAST_NON_USE && ("TN".contains(currentStateCd))
                        || (isNonUse && purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_REM_HYPO)
                        || (isNonUse && purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO)
                        || ("KA".contains(currentStateCd) && vtOwnerStatus.equalsIgnoreCase(TableConstants.VT_RC_SURRENDER_STATUS)
                        && (purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_NOC || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_REM_HYPO || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO))
                        || (purCd.get(i) == TableConstants.VM_MAST_RC_CANCELLATION)
                        || ("GA".contains(currentStateCd) && ownerDobj.getOther_criteria() == TableConstants.OTHER_CRITERIA_MINE_VEH && purCd.size() == 1 && (purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_REM_HYPO || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_HPC))) {
                    taxPaidOrClear = true;
                    break;
                }
                if ("JH".contains(currentStateCd) && purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_REN_REG) {
                    taxPaidOrClear = true;
                    break;
                } else if ((purCd.get(i) == TableConstants.VM_MAST_RC_CANCELLATION || purCd.get(i) == TableConstants.VM_MAST_NON_USE) && isNonUse) {
                    taxPaidOrClear = true;
                    break;
                }
            }
        }//for Skipping Tax Constraints in Particular Cases End

        if (!taxPaidOrClear) {
            facesMessages = "Tax is not Paid by this Registration No. " + regn_no.trim() + " Please Clear the Tax First or Fill the Tax Exemption Details if Applicable.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
            PrimeFaces.current().ajax().update("msgDialog");
            PrimeFaces.current().executeScript("PF('messageDialog').show();");
            result = true;
            return result;
        }

        if (applicationInwardImpl.getFitnessRequiredFor(ownerDobj, tmConfigurationDobj) && auctionDobj == null) {
            fitnessReqFor = true;
        }
        if (applicationInwardImpl.getRenewRegRequiredFor(ownerDobj, tmConfigurationDobj) && auctionDobj == null) {
            renewRegReqFor = true;
        }

        for (int i = 0; i < purCd.size(); i++) {//Check for RC Particulars

            if (purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS
                    || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE
                    || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_COMPANY
                    || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_OWNER
                    || purCd.get(i) == TableConstants.VM_MAST_VEHICLE_SCRAPE) {
                rcParticularCheck = true;
            }

            if (purCd.get(i) == TableConstants.VM_MAST_VEHICLE_SCRAPE) {
                isVehicleScrapAppl = true;
            }
            if (purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_REM_HYPO) {
                isHptAppl = true;
            }

            if (purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_REN_REG) {
                isRenewalRegAppl = true;
            }

            if (purCd.get(i) == TableConstants.VM_MAST_RC_SURRENDER
                    || purCd.get(i) == TableConstants.VM_MAST_RC_RELEASE
                    || purCd.get(i) == TableConstants.VM_MAST_RC_CANCELLATION
                    || purCd.get(i) == TableConstants.VM_MAST_NON_USE
                    || purCd.get(i) == TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE) { //rcCheck used for skipping fitness validity at the time of rc surrnder/release
                rcCheck = true;
            }

            if ((purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_TO
                    || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_FRESH_RC
                    || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_DUP_RC
                    || purCd.get(i) == TableConstants.VM_DUPLICATE_TO_TAX_CARD
                    || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_REM_HYPO
                    || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_HPC)
                    && ownerDobj.getBlackListedVehicleDobj() != null) {
                if (ownerDobj.getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLTheftCode
                        || ownerDobj.getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLDestroyedAccidentCode
                        || ownerDobj.getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLLoanDefaulterCode
                        || ownerDobj.getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLConfiscationCode) {
                    result = false;
                    toWithTheft = true;
                } else {
                    facesMessages = "Can't Inward Application because of Vehicle is Blacklisted due to Reason [" + ownerDobj.getBlackListedVehicleDobj().getComplainDesc().toUpperCase() + "] Dated [ " + ownerDobj.getBlackListedVehicleDobj().getComplaindt() + " ] in State [ " + ownerDobj.getBlackListedVehicleDobj().getStateName() + " ] at Office [ " + ownerDobj.getBlackListedVehicleDobj().getOfficeName() + " ]";
                    result = true;
                }
            } else if ("KA".equalsIgnoreCase(currentStateCd) && (purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) && purCd.size() == 1 && ownerDobj.getBlackListedVehicleDobj() != null) {
                if (ownerDobj.getBlackListedVehicleDobj().getComplain_type() != TableConstants.BLDestroyedAccidentCode
                        && ownerDobj.getBlackListedVehicleDobj().getComplain_type() != TableConstants.BLTheftCode
                        && ownerDobj.getBlackListedVehicleDobj().getComplain_type() != TableConstants.BLScrappedCode) {
                    result = false;
                    return result;
                } else if (ownerDobj.getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLDestroyedAccidentCode
                        || ownerDobj.getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLTheftCode
                        || ownerDobj.getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLScrappedCode) {
                    facesMessages = "Can't Inward Application because of Vehicle is Blacklisted due to Reason [" + ownerDobj.getBlackListedVehicleDobj().getComplainDesc().toUpperCase() + "] Dated [ " + ownerDobj.getBlackListedVehicleDobj().getComplaindt() + " ] in State [ " + ownerDobj.getBlackListedVehicleDobj().getStateName() + " ] at Office [ " + ownerDobj.getBlackListedVehicleDobj().getOfficeName() + " ]";
                    result = true;
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
                    PrimeFaces.current().ajax().update("msgDialog");
                    PrimeFaces.current().executeScript("PF('messageDialog').show();");
                    return result;
                }

            } else if ((purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_VEH_ALTER) && ownerDobj.getBlackListedVehicleDobj() != null) {
                if (ownerDobj.getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLDestroyedAccidentCode) {
                    result = false;
                    toWithTheft = true;
                }
            } else if (ownerDobj.getBlackListedVehicleDobj() != null && !toWithTheft && ownerDobj.getTmConfSwappingDobj() != null && !ownerDobj.getTmConfSwappingDobj().isSwapping_allowed_theft_untraced_case()) {
                facesMessages = "Can't Inward Application because of Vehicle is Blacklisted due to Reason [" + ownerDobj.getBlackListedVehicleDobj().getComplainDesc().toUpperCase() + "] Dated [ " + ownerDobj.getBlackListedVehicleDobj().getComplaindt() + " ] in State [ " + ownerDobj.getBlackListedVehicleDobj().getStateName() + " ] at Office [ " + ownerDobj.getBlackListedVehicleDobj().getOfficeName() + " ]";
                result = true;
                if (result && !rcParticularCheck && !rcCheck) {//will be updated for better solution
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
                    PrimeFaces.current().ajax().update("msgDialog");
                    PrimeFaces.current().executeScript("PF('messageDialog').show();");
                    return result;
                }
            } else if (ownerDobj.getBlackListedVehicleDobj() != null && !toWithTheft && ownerDobj.getTmConfSwappingDobj() != null && ownerDobj.getTmConfSwappingDobj().isSwapping_allowed_theft_untraced_case()) {
                if (ownerDobj.getBlackListedVehicleDobj().getComplain_type() != TableConstants.BLTheftCode) {
                    facesMessages = "Can't Inward Application because of Vehicle is Blacklisted due to Reason [" + ownerDobj.getBlackListedVehicleDobj().getComplainDesc().toUpperCase() + "] Dated [ " + ownerDobj.getBlackListedVehicleDobj().getComplaindt() + " ] in State [ " + ownerDobj.getBlackListedVehicleDobj().getStateName() + " ] at Office [ " + ownerDobj.getBlackListedVehicleDobj().getOfficeName() + " ]";
                    result = true;
                    if (result && !rcParticularCheck && !rcCheck) {//will be updated for better solution
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        return result;
                    }
                } else if (!ownerDobj.getBlackListedVehicleDobj().isUntracedReportOfBlacklistedForSwappingUse()) {
                    facesMessages = "Can't Inward Application because of Vehicle is Blacklisted without Untraced report due to Reason [" + ownerDobj.getBlackListedVehicleDobj().getComplainDesc().toUpperCase() + "] Dated [ " + ownerDobj.getBlackListedVehicleDobj().getComplaindt() + " ] in State [ " + ownerDobj.getBlackListedVehicleDobj().getStateName() + " ] at Office [ " + ownerDobj.getBlackListedVehicleDobj().getOfficeName() + " ]";
                    result = true;
                    if (result && !rcParticularCheck && !rcCheck) {//will be updated for better solution
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        return result;
                    }
                }
            } else if ("KA".equalsIgnoreCase(currentStateCd) && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_FIT_CERT && ownerDobj.getBlackListedVehicleDobj() != null && !toWithTheft) {
                facesMessages = "Can't Inward Application because of Vehicle is Blacklisted due to Reason [" + ownerDobj.getBlackListedVehicleDobj().getComplainDesc().toUpperCase() + "] Dated [ " + ownerDobj.getBlackListedVehicleDobj().getComplaindt() + " ] in State [ " + ownerDobj.getBlackListedVehicleDobj().getStateName() + " ] at Office [ " + ownerDobj.getBlackListedVehicleDobj().getOfficeName() + " ]";
                result = true;
                if (result && !rcParticularCheck && !rcCheck) {//will be updated for better solution
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
                    PrimeFaces.current().ajax().update("msgDialog");
                    PrimeFaces.current().executeScript("PF('messageDialog').show();");
                    return result;
                }
            } else if (!"KA".equalsIgnoreCase(currentStateCd) && ownerDobj.getBlackListedVehicleDobj() != null && !toWithTheft) {
                facesMessages = "Can't Inward Application because of Vehicle is Blacklisted due to Reason [" + ownerDobj.getBlackListedVehicleDobj().getComplainDesc().toUpperCase() + "] Dated [ " + ownerDobj.getBlackListedVehicleDobj().getComplaindt() + " ] in State [ " + ownerDobj.getBlackListedVehicleDobj().getStateName() + " ] at Office [ " + ownerDobj.getBlackListedVehicleDobj().getOfficeName() + " ]";
                result = true;
                if (result && !rcParticularCheck && !rcCheck) {//will be updated for better solution
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
                    PrimeFaces.current().ajax().update("msgDialog");
                    PrimeFaces.current().executeScript("PF('messageDialog').show();");
                    return result;
                }
            }

            if (vehParameters != null) {
                vehParameters.setPUR_CD(purCd.get(i));
            }

            if (fit_upto != null && fit_upto.compareTo(currentDate) < 0
                    && !isVehValidityExpired && fitnessReqFor
                    && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_FIT_CERT
                    && !fitnessCheck && !toWithTheft
                    && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_NOC_CANCEL
                    && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_DUP_RC
                    && purCd.get(i) != TableConstants.VM_DUPLICATE_TO_TAX_CARD
                    && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_FIT_REVOKE
                    && purCd.get(i) != TableConstants.VM_MAST_RC_CANCELLATION
                    && !(isNonUse && purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_REM_HYPO)
                    && !(isNonUse && purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO)
                    && !("UK".contains(currentStateCd) && purCd.get(i) == TableConstants.VM_MAST_RC_SURRENDER)) {
                if (tmConfigurationFitnessDobj != null && tmConfigurationFitnessDobj.getSkip_fitness_check_if_veh_age_expire() != null && vehParameters != null && isCondition(replaceTagValues(tmConfigurationFitnessDobj.getSkip_fitness_check_if_veh_age_expire(), vehParameters), "getVehAgeValidity")) {
                    //skip fitness validation
                } else {
                    facesMessages = "Fitness of Vehicle is Expired, Please Select Fitness Renewal!!!";
                    result = true;

                    // for allowing fitness if there is online application in case of fitness expire
                    // when online application coming to RTO for application inward
                    if (onlineApplDataList != null && onlineApplDataList.size() > 0) {
                        facesMessages = "Fitness of Vehicle is Expired, Please Select Fitness Renewal before Processing of Online Application";
                        result = true;
                        if (purCodeList != null) {
                            purCodeList.clear();
                        }
                        if (selectedPurposeCode != null) {
                            selectedPurposeCode.clear();
                        }
                        onlineApplDataList.clear();
                        onlineApplDataList = null;
                        purCodeList.put(TableConstants.FITNESS_PURPOSE_DESCR, TableConstants.VM_TRANSACTION_MAST_FIT_CERT);
                        PrimeFaces.current().ajax().update("panelOwnerInfo");
                    }
                }

            } else if (fit_upto != null && fit_upto.compareTo(currentDate) < 0
                    && purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_FIT_CERT
                    && vhType == TableConstants.VM_VEHTYPE_TRANSPORT) {

                result = false;
                fitnessCheck = true;
            }

            if (purCd.contains(TableConstants.VM_TRANSACTION_MAST_FIT_CERT)
                    && purCd.contains(TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO)
                    && "RJ".equalsIgnoreCase(currentStateCd)) {
                isReassignmentAllowed = true;
            }
            if (purCd.get(i) == TableConstants.VM_MAST_RC_CANCELLATION) {
                toWithTheft = true;
            }

            if (isVehValidityExpired
                    && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_FIT_CERT
                    && fitnessReqFor && !toWithTheft
                    && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_NOC_CANCEL
                    && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_REN_REG
                    && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_DUP_RC
                    && purCd.get(i) != TableConstants.VM_DUPLICATE_TO_TAX_CARD
                    && purCd.get(i) != TableConstants.VM_MAST_VEHICLE_SCRAPE
                    && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_DUP_RC
                    && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS
                    && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE
                    && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_COMPANY
                    && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_OWNER
                    && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_FIT_REVOKE
                    && !isReassignmentAllowed
                    && !("GA".contains(currentStateCd) && ownerDobj.getOther_criteria() == TableConstants.OTHER_CRITERIA_MINE_VEH && purCd.size() == 1 && (purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_REM_HYPO || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_HPC))) {

                if (fit_upto != null && fit_upto.compareTo(currentDate) < 0) {
                    boolean skipFitnessCheckIfVehAgeExpire = false;
                    if (tmConfigurationFitnessDobj != null && tmConfigurationFitnessDobj.getSkip_fitness_check_if_veh_age_expire() != null && vehParameters != null) {
                        vehParameters.setVEH_AGE_EXPIRED("true");
                        if (isCondition(replaceTagValues(tmConfigurationFitnessDobj.getSkip_fitness_check_if_veh_age_expire(), vehParameters), "getVehAgeValidity")) {
                            skipFitnessCheckIfVehAgeExpire = true;
                        }
                    }

                    if ("KA".contains(currentStateCd) && vtOwnerStatus.equalsIgnoreCase(TableConstants.VT_RC_SURRENDER_STATUS)
                            && (purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_NOC || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_REM_HYPO || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO || purCd.get(i) == TableConstants.VM_MAST_RC_RELEASE)) {
                        skipFitnessCheckIfVehAgeExpire = true;
                    }

                    if ("KA,UK".contains(currentStateCd) && purCd.get(i) == TableConstants.VM_MAST_RC_SURRENDER) {
                        skipFitnessCheckIfVehAgeExpire = true;
                    }

                    if (!"DL".equalsIgnoreCase(currentStateCd)) {
                        if ((isNonUse && purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_REM_HYPO)
                                || (isNonUse && purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO) || skipFitnessCheckIfVehAgeExpire || ("OR".equalsIgnoreCase(currentStateCd) && (purCd.get(i) != TableConstants.VM_MAST_NON_USE || purCd.get(i) != TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE))) {
                            //skip validation
                        } else {
                            facesMessages = "Registration Validity / Fitness Validity of the Vehicle is Expired, Please Select Renewal of Registration or Renewal of Fitness!!!";
                            result = true;

                            // for allowing fitness/Renewal of Registration if there is online application in case of fitness/Registration expire
                            // when online application coming to RTO for application inward
                            if (onlineApplDataList != null && onlineApplDataList.size() > 0) {
                                facesMessages = "Registration or Fitness of Vehicle is Expired, Please Select Renewal of Registration or Fitness Renewal before Processing of Online Application";
                                result = true;
                                if (purCodeList != null) {
                                    purCodeList.clear();
                                }
                                if (selectedPurposeCode != null) {
                                    selectedPurposeCode.clear();
                                }
                                onlineApplDataList.clear();
                                onlineApplDataList = null;
                                purCodeList.put(TableConstants.FITNESS_PURPOSE_DESCR, TableConstants.VM_TRANSACTION_MAST_FIT_CERT);
                                purCodeList.put(TableConstants.RENEWAL_OF_REGISTRATION_PURPOSE_DESCR, TableConstants.VM_TRANSACTION_MAST_REN_REG);
                                PrimeFaces.current().ajax().update("panelOwnerInfo");
                            }
                        }
                    }
                }
            }

            if (isVehValidityExpired
                    && renewRegReqFor && !toWithTheft
                    && !isRenewalRegAppl
                    && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_NOC_CANCEL
                    && purCd.get(i) != TableConstants.VM_DUPLICATE_TO_TAX_CARD
                    && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_DUP_RC
                    && !(isNonUse && purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_REM_HYPO)
                    && !(isNonUse && purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO)) {
                if (!"DL,WB".contains(currentStateCd)) {
                    facesMessages = "Registration of Vehicle is Expired, Please Select Renewal of Registration!!!";
                    result = true;
                }
            }

            if (isRenewalRegAppl && result) {
                result = false;
            }

            if (isNonUse && isVehValidityExpired && isCondition(replaceTagValues(tmConfigurationDobj.getApplInwardExempForVehAgeExpire(), vehParameters), "getApplInwardExempForVehAgeExpire")
                    && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_DUP_RC
                    && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_NOC_CANCEL
                    && purCd.get(i) != TableConstants.VM_DUPLICATE_TO_TAX_CARD) {
                facesMessages = "Transaction is not allowed due to Registration of this Vehicle is Expired.";
                result = true;
                rcCheck = false;
            }

            if (fitnessReqFor && renewRegReqFor) {//if both fitness and registration expired.
                int count = 0;
                for (int j = 0; j < purCd.size(); j++) {
                    if (purCd.get(j) == TableConstants.VM_TRANSACTION_MAST_FIT_CERT
                            || purCd.get(j) == TableConstants.VM_TRANSACTION_MAST_REN_REG) {
                        count++;
                    }
                }
                if (count == 2) {
                    result = false;
                }
            }

            //checking renewal_regn_rqrd_for column in tm_configuration
            if (purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_REN_REG && !renewRegReqFor) {
                facesMessages = "Renewal of Registration application is not allowed for this vehicle.";
                result = true;
            }
        }
        for (int i = 0; i < purCd.size(); i++) {
            if ((ins_bean.isIibData() && ins_bean.getIns_upto() != null && ins_bean.getIns_upto().compareTo(currentDate) < 0)
                    && !(Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS
                    || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE
                    || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_COMPANY
                    || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_OWNER
                    || purCd.get(i) == TableConstants.VM_MAST_RC_CANCELLATION
                    || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_FRESH_RC
                    || purCd.get(i) == TableConstants.VM_MAST_NON_USE
                    || purCd.get(i) == TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE
                    || purCd.get(i) == TableConstants.VM_MAST_VEHICLE_SCRAPE
                    || purCd.get(i) == TableConstants.VM_MAST_RC_SURRENDER
                    || purCd.get(i) == TableConstants.VM_MAST_RC_RELEASE
                    || purCd.get(i) == TableConstants.VM_MAST_RC_CANCELLATION
                    || purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_REM_HYPO
                    || (isVehicleScrapAppl && isHptAppl && purCd.size() == 2)
                    || toWithTheft)) {
                facesMessages = "Can't Inward Application Because Insurance Validity Expired . The shown insurance details have been uploaded by Insurance Company to Vahan, if any discrepencies found, the Vehicle Owner have to contact respective Insurance Compnay for uploading the Latest Correct Insurance Details on Vahan.";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
                PrimeFaces.current().ajax().update("msgDialog");
                PrimeFaces.current().executeScript("PF('messageDialog').show();");
                result = true;
                return result;
            }
        }

        //for Delhi Case Only Start              
        if (((renewRegReqFor && isVehValidityExpired) || (fitnessReqFor && isVehValidityExpired))
                && isTSR && ownerDobj.getBlackListedVehicleDobj() == null
                && ((isToAppl && purCd.size() == 1) || (isToAppl && isHptAppl && purCd.size() == 2))) {
            result = false;//for skipping constraints for expiring of veh age in delhi
        }
        //for Delhi Case Only End

        if (result && !rcParticularCheck && !rcCheck) {//will be updated for better solution
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
            PrimeFaces.current().ajax().update("msgDialog");
            PrimeFaces.current().executeScript("PF('messageDialog').show();");
            result = true;
            return result;
        }

        if (result) {//reset result
            result = false;
        }

        //added By Nitin 21-05-2015-Check for RC
        boolean onlyDuplicateRCSelected = false;
        for (int i = 0; i < purCd.size(); i++) {
            if (purCd.get(i) != TableConstants.VM_MAST_RC_RELEASE) {//Check for RC
                if (vtOwnerStatus.equalsIgnoreCase(TableConstants.VT_RC_SURRENDER_STATUS) || vtOwnerStatus.equalsIgnoreCase(TableConstants.VT_RC_CANCEL_STATUS) || vtOwnerStatus.equalsIgnoreCase(TableConstants.VT_RC_SUSPEND_STATUS)) {
                    if (!"KA".contains(currentStateCd)) {
                        facesMessages = "Cant't Inward Application because RC is Surrendered/Cancel.";
                        result = true;
                    } else if ("KA".contains(currentStateCd) && (vtOwnerStatus.equalsIgnoreCase(TableConstants.VT_RC_SURRENDER_STATUS) && (purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_NOC && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_REM_HYPO && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_ADD_HYPO)
                            || (vtOwnerStatus.equalsIgnoreCase(TableConstants.VT_RC_CANCEL_STATUS) || vtOwnerStatus.equalsIgnoreCase(TableConstants.VT_RC_SUSPEND_STATUS)))) {
                        facesMessages = "Cant't Inward Application because RC is Surrendered/Cancel.";
                        result = true;
                    }
                }
            }

            if ("HP".contains(currentStateCd)) {
                if (purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION) {
                    if (permitDetailDobj == null && permitSurrenderDetailDobj == null
                            && tmConfigurationDobj != null && isCondition(replaceTagValues(tmConfigurationDobj.getPermit_exemption(), vehParameters), "getPermitExemption")) {
                        facesMessages = "Can't Inward Conversion Application as Permit details are not found!!!";
                        result = true;
                    }

                    if (ownerDobj.getFuel() != TableConstants.VM_FUEL_TYPE_ELECTRIC && ownerDobj.getCubic_cap() <= 0) {
                        facesMessages = "Can't Inward Conversion Application as Cubic Capacity detail is missing!!!";
                        result = true;
                    }
                }
            }
            if (purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_DUP_RC) {
                onlyDuplicateRCSelected = true;
            }

            if ((vtOwnerStatus.equalsIgnoreCase(TableConstants.VT_SCRAP_VEHICLE_STATUS)) && (purCd.get(i) != TableConstants.RETENTION_OF_REGISTRATION_NO_PUR_CD)) {
                facesMessages = "Can't Inward Application because Vehicle is Scrapped!!!";
                result = true;
            }

        }

        //Check for smart Card
        SmartCardDobj smartCardDobj = ServerUtil.getSmartCardDetailsFromRcBeToBo(regn_no);
        if (smartCardDobj != null && !onlyDuplicateRCSelected) {
            facesMessages = "Can't Inward Application due to Smart Card is Pending for Activation for Registration No " + regn_no + " against Application No " + smartCardDobj.getAppl_no();
            result = true;
        }

        //Only For Orissa---Check  for Audit---------
        if (!CommonUtils.isNullOrBlank(currentStateCd) && "OR".equals(currentStateCd)) {
            AuditRecoveryDobj auditRecoveryDobj = ServerUtil.getAuditRecordFromVA_AUDIT(regn_no, currentStateCd, currentOffCd);
            if (auditRecoveryDobj != null) {
                facesMessages = "Can't Inward Application due to Audit is Pending for  Registration No " + regn_no + "";
                result = true;
            }
        }
        //Only for orissa----Check for Tax installment
        if (!CommonUtils.isNullOrBlank(currentStateCd) && "OR".equals(currentStateCd)) {
            TaxInstallCollectionDobj TaxinstallDobj = ServerUtil.getTaxInstallCheck(regn_no, currentStateCd, currentOffCd);
            if (TaxinstallDobj != null) {
                facesMessages = "Can't Inward Application due to TaxInstallment is Pending for  Registration No " + regn_no + "";
                result = true;
            }
        }

        //Check for RC Print
        PrintDocImpl docImpl = new PrintDocImpl();
        if (!regnNoIsNewforSwapping && "UP".equalsIgnoreCase(currentStateCd)) {
            appl_no = docImpl.getApplNoFromVaRcPrint(regn_no);

        }
        if (appl_no != null) {
            facesMessages = "Can't Inward Application due to RC is Pending for Print for Registration No " + regn_no + " against Application No " + appl_no;
            result = true;
        }

        //Dispatch Check for GJ
        if ("GJ".equalsIgnoreCase(currentStateCd)) {
            Boolean status = ServerUtil.isDispatchDetailsExist(currentStateCd, Util.getUserSeatOffCode(), regn_no);
            if (status) {
                facesMessages = "Can't Inward Application due to RC Dispatch is Pending for Registration No " + regn_no;
                result = true;
            }
        }

        if (result && !rcParticularCheck) {//repeat code 2 will be updated for better solution
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
            PrimeFaces.current().ajax().update("msgDialog");
            PrimeFaces.current().executeScript("PF('messageDialog').show();");
            result = true;
            return result;
        }

        if (result) {//reset result
            result = false;
        }

        for (int i = 0; i < purCd.size(); i++) {
            if (purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_NOC_CANCEL) {//Check for NOC
                if (vtOwnerStatus.equalsIgnoreCase(TableConstants.VT_NOC_ISSUE_STATUS)) {
                    facesMessages = "Can't Inward Application due to NOC Issued for this Vehicle.NOC Cancel is Allowed Only!!!";
                    result = true;
                }
            } else {
                if (!vtOwnerStatus.equalsIgnoreCase(TableConstants.VT_NOC_ISSUE_STATUS)) {
                    facesMessages = "Can't Inward Application of NOC Cancel because NOC is not issued!!!";
                    result = true;
                }
            }

            if (isNonUse && (purCd.get(i) != TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE
                    && purCd.get(i) != TableConstants.VM_MAST_RC_CANCELLATION
                    && purCd.get(i) != TableConstants.VM_MAST_NON_USE
                    && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_ADD_HYPO
                    && purCd.get(i) != TableConstants.VM_TRANSACTION_MAST_REM_HYPO)) {
                facesMessages = "Can't Inward Application because Vehicle is in NonUse!!!";
                result = true;
            }

            if (purCd.get(i) == TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE && !isNonUse) {
                facesMessages = "Can't Inward Application of NonUse Restore/Removal because Vehicle is Not in NonUse!!!";
                result = true;
            }

            if (purCd.get(i) == TableConstants.VM_MAST_NON_USE && !isNonUse) { //previous Non use tax adjustment
                if (tmConfigurationNonuseDobj != null && tmConfigurationNonuseDobj.isNonuse_adjust_in_tax_amt()) {
                    NonUseImpl nonUseImpl = new NonUseImpl();
                    boolean isPmtSur = false;
                    nonUseDobj = nonUseImpl.geTotalNonUseAmount(ownerDobj);
                    if (nonUseDobj != null) {
                        if (currentStateCd.equalsIgnoreCase("CG")) {
                            isPmtSur = nonUseImpl.isNonUseAdjustMform(regn_no);
                        }

                        if (!isPmtSur) {
                            double totalPaybalRebate = 0.0;
                            double totalPaybalPenalty = 0.0;
                            totalPaybalRebate = nonUseDobj.getAdjustmentAmount();
                            totalPaybalPenalty = nonUseDobj.getNonUsePenalty();

                            if (totalPaybalRebate > 0) {
                                facesMessages = "Can't Inward Application because previous NonUse Tax Adjustment Amount has not been utilized. Please pay the Tax to utilize the NonUse adjusted amount.";
                                result = true;
                            }
                            if (totalPaybalPenalty > 0) {
                                facesMessages = "Can't Inward Application because previous NonUse Penalty not paid. Please pay the Tax to clear the NonUse penalty.";
                                result = true;
                            }
                        }
                    }
                }
            }

        }

        if (result && !rcParticularCheck) {//repeat code 3 will be updated for better solution
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
            PrimeFaces.current().ajax().update("msgDialog");
            PrimeFaces.current().executeScript("PF('messageDialog').show();");
            result = true;
            return result;
        }

        if (result) {//reset result
            result = false;
        }
        //added By Nitin 21-05-2015
        for (int i = 0; i < purCd.size(); i++) {
            int flag = 0;
            if ((onlineApplDataList == null || onlineApplDataList.isEmpty()) && tmConfigurationApplInwardDobj != null
                    && !CommonUtils.isNullOrBlank(tmConfigurationApplInwardDobj.getPurCdInwardNotAllowInOffice()) && tmConfigurationApplInwardDobj.getPurCdInwardNotAllowInOffice().contains("," + purCd.get(i) + ",")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "You can't Inward " + ServerUtil.getTaxHead(purCd.get(i)) + " application as per the state request, kindly apply it through Vahan Citizen Portal.", "You can't Inward " + ServerUtil.getTaxHead(purCd.get(i)) + " application as per the state request, kindly apply it through Vahan Citizen Portal."));
                PrimeFaces.current().ajax().update("msgDialog");
                PrimeFaces.current().executeScript("PF('messageDialog').show();");
                return true;
            }
            switch (purCd.get(i)) {

                case TableConstants.VM_TRANSACTION_MAST_REN_REG:
                    Date today = new Date();
                    Date priorMonth = new Date();
                    if (currentStateCd.equalsIgnoreCase("OR")) {
                        priorMonth = ServerUtil.dateRange(fit_upto, 0, -6, 0);//Appl inward allowed within 6 month from fitness validity.                                          
                        if (fit_upto.compareTo(today) > 0 && vhType == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                            if (today.compareTo(priorMonth) <= 0) {
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Can't Inward Application due to valid fitness Date or Tranport Vehicle !!!", "Can't Inward Application due to valid fitness Date or Transport Vehicle !!!"));
                                PrimeFaces.current().ajax().update("msgDialog");
                                PrimeFaces.current().executeScript("PF('messageDialog').show();");
                                result = true;
                                return result;
                            }
                        }
                    } else {
                        if (!validateFitnessApp(fit_upto) && vhType == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Can't Inward Application due to valid fitness Date or Tranport Vehicle !!!", "Can't Inward Application due to valid fitness Date or Transport Vehicle !!!"));
                            PrimeFaces.current().ajax().update("msgDialog");
                            PrimeFaces.current().executeScript("PF('messageDialog').show();");
                            result = true;
                            return result;
                        }
                    }
                    NewImpl.getReNewRegUpto(ownerDobj);

                    //above block can be configure by configuration table
                    int checkSelectionVarRenReg = 0;
                    if (ownerDetail.getState_cd().equalsIgnoreCase(currentStateCd)
                            && ownerDetail.getOff_cd() != currentOffCd) {
                        for (int j = 0; j < purCd.size(); j++) {
                            if (purCd.get(j) == TableConstants.VM_TRANSACTION_MAST_TO
                                    || purCd.get(j) == TableConstants.VM_TRANSACTION_MAST_CHG_ADD) {
                                checkSelectionVarRenReg++;
                                break;
                            }
                        }

                        if (checkSelectionVarRenReg == 0) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Renewal Of Registration  is Allowed with TO/CA Only."));
                            result = true;
                            return result;
                        }
                    }
                    if (currentStateCd.equalsIgnoreCase("RJ") && vhType == TableConstants.VM_VEHTYPE_TRANSPORT) {
                        int VehAge = (int) Math.ceil(DateUtils.getDate1MinusDate2_Months(ownerDobj.getPurchase_dt(), new Date()) / 12.0);
                        if (VehAge >= 14 && VehAge <= 30) {
                            int surrenderPurCode = new SurrenderPermitImpl().getTransPurCdFromVtPmtTranaction(regn_no);
                            if (permitDetailDobj != null) {
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Can't Inward Application due to Permit is not Surrender !!!", "Can't Inward Application due to Permit is not Surrender !!!"));
                                PrimeFaces.current().ajax().update("msgDialog");
                                PrimeFaces.current().executeScript("PF('messageDialog').show();");
                                return true;
                            } else if (surrenderPurCode != TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD && surrenderPurCode != TableConstants.EMPTY_INT) {
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Can't Inward Application due to Permit is not Surrender for Renewal of Vehicle !!!", "Can't Inward Application due to Permit is not Surrender for Renewal of Vehicle !!!"));
                                PrimeFaces.current().ajax().update("msgDialog");
                                PrimeFaces.current().executeScript("PF('messageDialog').show();");
                                return true;
                            }
                        }
                    }
                    //above block can be canfigure by configuration table

                    break;

                case TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION:
                    boolean fitnessFlag = false;
                    boolean fitnessRequ = false;
                    if (vhType == TableConstants.VM_VEHTYPE_TRANSPORT && permitDetailDobj != null) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Can't Inward Application due to Permit is not Surrender/Cancelled !!!", "Can't Inward Application due to Permit is not Surrender/Cancelled !!!"));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        return true;
                    }
                    for (int j = 0; j < purCd.size(); j++) {
                        if (purCd.get(j) == TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO && applicationInwardImpl.verifyNumberGenInConversion()) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Can't Inward Application Conversion With RMA because both will Generate New Registration Number !!!", "Can't Inward Application Conversion With RMA because both will Generate New Registration Number !!!"));
                            PrimeFaces.current().ajax().update("msgDialog");
                            PrimeFaces.current().executeScript("PF('messageDialog').show();");
                            return true;
                        }
                        if (vhType == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                            VehicleParameters vehParametersConv = vehParameters;
                            vehParametersConv.setVH_CLASS(conv_dobj.getNew_vch_class());
                            vehParametersConv.setVCH_TYPE(vehType);
                            if (tmConfigurationDobj != null && isCondition(replaceTagValues(tmConfigurationDobj.getFitness_rqrd_for(), vehParametersConv), "getFitness_rqrd_for")) {
                                fitnessRequ = true;
                            }
                            if (purCd.get(j) == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {
                                fitnessFlag = true;
                            }
                        }
                    }
                    if (!fitnessFlag && fitnessRequ) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Conversion to Transport Vehicle, Please Select Fitness Inspection with Conversion of Vehicle", "Conversion to Transport Vehicle, Please Select Fitness Inspection with Conversion of Vehicle"));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        return true;
                    }
                    break;

                case TableConstants.VM_TRANSACTION_MAST_TO:
                case TableConstants.VM_TRANSACTION_MAST_NOC:
                    boolean hypoChk = true;
                    if (vhType == TableConstants.VM_VEHTYPE_TRANSPORT && permitDetailDobj != null) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Can't Inward Application due to Permit is not Surrender !!!", "Can't Inward Application due to Permit is not Surrender !!!"));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        return true;
                    }

                    if (purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_NOC && "OR,KA,TN".contains(currentStateCd)) {
                        hypoChk = false;
                    } else if (purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_TO && auctionDobj != null) {
                        hypoChk = false;
                    }

                    if (purCd.get(i) == TableConstants.VM_TRANSACTION_MAST_TO
                            && ownerDetail.getState_cd().equalsIgnoreCase(currentStateCd)
                            && ownerDetail.getOff_cd() != currentOffCd
                            && ownerDetail.getVch_catg() != null && auctionDobj == null) {
                        if (!isCondition(replaceTagValues(tmConfigurationApplInwardDobj.getCheck_for_TO_without_NOC(), vehParameters), "getCheck_for_TO_without_NOC")) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Transfer of Ownership not allowed for this Vehilce without NOC in Different Offices.", "Transfer of Ownership not allowed for this Vehilce without NOC in Different Offices."));
                            PrimeFaces.current().ajax().update("msgDialog");
                            PrimeFaces.current().executeScript("PF('messageDialog').show();");
                            return true;
                        }
                    }

                    if (!isHypth && hypoChk) {
                        isHypth = ServerUtil.checkHypthOrNot(regn_no, ownerDobj.getState_cd());
                    }

                    if (isHypth) {
                        int checkSelection = 0;
                        for (int j = 0; j < purCd.size(); j++) {
                            if (purCd.get(j) == TableConstants.VM_TRANSACTION_MAST_REM_HYPO
                                    || purCd.get(j) == TableConstants.VM_TRANSACTION_MAST_HPC) {
                                flag = 1;
                                checkSelection++;
                            }
                        }

                        if (flag != 1 || checkSelection != 1) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Vehicle is HYPOTHECATED, You Have to Choose Only HPT or HP Continution!!!"));
                            result = true;
                            return result;
                        }

                    }
                    break;

                case TableConstants.VM_TRANSACTION_MAST_FRESH_RC:

                    if (!isHypth) {
                        isHypth = ServerUtil.checkHypthOrNot(regn_no, ownerDobj.getState_cd());
                    }

                    if (isHypth) {
                        for (int j = 0; j < purCd.size(); j++) {
                            if (purCd.get(j) == TableConstants.VM_TRANSACTION_MAST_REM_HYPO) {
                                flag = 1;
                                break;
                            }
                        }

                        if (flag != 1) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Vehicle is HYPOTHECATED, You Have to Choose HPT"));
                            result = true;
                            return result;
                        }
                    } else {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Vehicle is NOT HYPOTHECATED, You Can not Inward Application for Fresh RC"));
                        result = true;
                        return result;
                    }

                case TableConstants.VM_TRANSACTION_MAST_REM_HYPO:

                    if (!isHypth) {
                        isHypth = ServerUtil.checkHypthOrNot(regn_no, ownerDobj.getState_cd());
                    }
                    if (!isHypth) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Vehicle is not HYPOTHECATED, you can't Request for HPT", "Vehicle is not HYPOTHECATED, you can't Request for HPT"));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        result = true;
                        return result;
                    }

                    //above block can be configure by configuration table
                    int checkSelectionVar = 0;
                    if (isHypth && ownerDetail.getState_cd().equalsIgnoreCase(currentStateCd)
                            && ownerDetail.getOff_cd() != currentOffCd) {
                        for (int j = 0; j < purCd.size(); j++) {
                            if (purCd.get(j) == TableConstants.VM_TRANSACTION_MAST_TO
                                    || purCd.get(j) == TableConstants.VM_TRANSACTION_MAST_NOC || purCd.get(j) == TableConstants.VM_TRANSACTION_MAST_CHG_ADD) {
                                checkSelectionVar++;
                                break;
                            }
                        }

                        if (checkSelectionVar == 0) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Hypothecation Termination is Allowed with CA/TO/NOC Only."));
                            result = true;
                            return result;
                        }
                    }
                    //above block can be canfigure by configuration table
                    break;

                case TableConstants.VM_TRANSACTION_MAST_ADD_HYPO:

                    if (!isHypth) {
                        isHypth = ServerUtil.checkHypthOrNot(regn_no, ownerDobj.getState_cd());
                    }

                    int checkSingleHPA = 0;
                    if (ownerDetail.getState_cd().equalsIgnoreCase(currentStateCd)
                            && ownerDetail.getOff_cd() != currentOffCd) {
                        for (int j = 0; j < purCd.size(); j++) {
                            if ((purCd.get(j) != TableConstants.VM_TRANSACTION_MAST_REM_HYPO)
                                    && (purCd.get(j) != TableConstants.VM_TRANSACTION_MAST_HPC)
                                    && (purCd.get(j) != TableConstants.VM_TRANSACTION_MAST_ADD_HYPO)) {
                                checkSingleHPA++;
                                break;
                            }
                        }

                        if (checkSingleHPA == 0) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Hypothecation Addition is not  Allowed, Please Select Another Transaction with  Hypothecation Addition ."));
                            result = true;
                            return result;
                        }
                    }
                    if (isHypth && !renderHpaVarification) {
                        renderHpaVarification = true;
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Vehicle is already HYPOTHECATED, Please Verify Hypothecation Addition Declaration to Proceed Further", "Vehicle is already HYPOTHECATED, Please Verify Hypothecation Addition Declaration to Proceed Further"));
                        PrimeFaces.current().ajax().update("applGenMsg");
                        PrimeFaces.current().ajax().update("panelOwnerInfo");
                        result = true;
                        return result;
                    }
                    break;

                case TableConstants.VM_TRANSACTION_MAST_FIT_CERT:
                    if (ownerDetail.getState_cd().equalsIgnoreCase(currentStateCd)
                            && ownerDetail.getOff_cd() != currentOffCd && tmConfigurationApplInwardDobj != null) {
                        if (!isCondition(replaceTagValues(tmConfigurationApplInwardDobj.getCheck_for_anywhere_fitness(), vehParameters), "getCheck_for_fitness") && auctionDobj == null) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Fitness is not allowed for this Vehilce in Other Office.", "Fitness is not allowed for this Vehilce in Other Office."));
                            PrimeFaces.current().ajax().update("msgDialog");
                            PrimeFaces.current().executeScript("PF('messageDialog').show();");
                            return true;
                        }
                    }
                    Map<String, Integer> fitOfficerList = ServerUtil.getFitOfficerList(currentStateCd, currentOffCd);
                    if (fitOfficerList == null || fitOfficerList.isEmpty()) {
                        message = "Fitness Renewal Application Can not be Proceed due to there is no Fitness Officer in your Office";
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, message, message));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        result = true;
                        return result;
                    }

                    boolean convWithFitness = false;

                    if (fitnessDobj != null) {
                        if (!CommonUtils.isNullOrBlank(fitnessDobj.getRemark()) && fitnessDobj.getRemark().startsWith("FC:")) {

                            if (tmConfigurationFitnessDobj == null) {
                                isRevocationAllowed = false;
                            } else {
                                Date date = ServerUtil.dateRange(fitnessDobj.getFit_valid_to(), 0, 0, tmConfigurationFitnessDobj.getFitness_revoke_allowed_days());
                                if (tmConfigurationFitnessDobj.isFitness_revoke_allowed() && date.compareTo(currentDate) >= 0) {
                                    isRevocationAllowed = true;
                                } else {
                                    isRevocationAllowed = false;
                                }
                            }
                        }

                        if (isRevocationAllowed) {
                            facesMessages = "Renewal of Fitness is not Allowed for this Vehicle, Please go for Fitness Revocation.";
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
                            PrimeFaces.current().ajax().update("msgDialog");
                            PrimeFaces.current().executeScript("PF('messageDialog').show();");
                            result = true;
                            return result;

                        }
                    }
                    if (vhType == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                        FitnessImpl fitImpl = new FitnessImpl();
                        FitnessDobj fitDobj = fitImpl.set_Fitness_appl_db_to_dobj(this.regn_no.trim(), null);
                        if (fitDobj != null && fitDobj.getFit_result() != null && fitDobj.getFit_result().equalsIgnoreCase(TableConstants.FitnessResultFail)
                                && (statusList == null || statusList.isEmpty())) {
                            statusList = ServerUtil.applicationStatus(this.regn_no.trim());
                            if (statusList != null && !statusList.isEmpty()) {
                                for (int i1 = 0; i1 < statusList.size(); i++) {
                                    if (statusList.get(i1).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION) {
                                        convWithFitness = true;
                                        break;
                                    }
                                }
                            }
                        }
                        for (int j = 0; j < purCd.size(); j++) {
                            if (purCd.get(j) == TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION) {
                                convWithFitness = true;
                                break;
                            }
                        }
                    }

                    if (!convWithFitness && !fitnessReqFor) {
                        facesMessages = "Renewal of Fitness is not Allowed for this Vehicle, Please Contact to the System Administrator.";
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        result = true;
                        return result;
                    }

                    String isFitnessAppApproved = applicationInwardImpl.isApplApproved(regn_no, purCd.get(i), currentStateCd);
                    if (isFitnessAppApproved != null) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, isFitnessAppApproved, isFitnessAppApproved));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        result = true;
                        return result;
                    }

                    Date fitNid = null;
                    Date fitValidTo = null;
                    if (fitnessDobj != null && fitnessDobj.getFit_valid_to() != null) {
                        fitValidTo = fitnessDobj.getFit_valid_to();
                    } else if (fit_upto != null) {
                        fitValidTo = fit_upto;
                    }
                    if (fitValidTo != null) {
                        fitNid = ServerUtil.dateRange(fitValidTo, 0, 0, tmConfigurationDobj.getNid_days());
                        if (fitnessDobj != null && fitnessDobj.getFit_nid() != null) {
                            if (DateUtils.compareDates(fitnessDobj.getFit_nid(), fitNid) == 2) {
                                //if (date1 > date2)==2
                                fitNid = fitnessDobj.getFit_nid();
                            }
                        }
                        if ("UP".equals(currentStateCd) && onlineApplDataList != null && !onlineApplDataList.isEmpty() && appointmentDt != null) {
                            fitNid = appointmentDt;
                        }
                        if (!CommonUtils.isNullOrBlank(currentStateCd) && !"OR".equals(currentStateCd)) {
                            if (!convWithFitness && fitNid.compareTo(currentDate) > 0) {
                                message = "Can not Request for Fitness Certificate before NID (Next Inspection Date) is " + ServerUtil.parseDateToString(fitNid);
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, message, message));
                                PrimeFaces.current().ajax().update("msgDialog");
                                PrimeFaces.current().executeScript("PF('messageDialog').show();");
                                result = true;
                                return result;
                            }
                        }
//                        } else {
//                            Date inspactionDate = null;
//                            inspactionDate = DateUtils.addToDate(fitNid, DateUtils.MONTH, -6);
//                            if (!convWithFitness && inspactionDate.compareTo(currentDate) >= 0) {
//                                message = "Can not Request for Fitness Certificate 6 month before NID (Next Inspection Date) is " + ServerUtil.parseDateToString(fitNid);
//                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, message, message));
//                                PrimeFaces.current().ajax().update("msgDialog");
//                                PrimeFaces.current().executeScript("PF('messageDialog').show();");
//                                result = true;
//                                return result;
//                            }
//                        }
                    }
                    FitnessImpl.getReNewFitnessUpto(ownerDobj, TableConstants.VM_TRANSACTION_MAST_FIT_CERT);
                    break;

                case TableConstants.VM_TRANSACTION_MAST_HPC:
                    int checkSelection = 0;
                    if (!isHypth) {
                        isHypth = ServerUtil.checkHypthOrNot(regn_no, ownerDobj.getState_cd());
                    }

                    if (isHypth) {
                        for (int j = 0; j < purCd.size(); j++) {
                            if (purCd.get(j) == TableConstants.VM_TRANSACTION_MAST_TO
                                    || purCd.get(j) == TableConstants.VM_TRANSACTION_MAST_NOC) {
                                checkSelection++;
                                break;
                            }
                        }

                        if (checkSelection == 0) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "HPC is Allowed with TO/NOC Only."));
                            result = true;
                            return result;
                        }

                    } else {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Vehicle is NOT HYPOTHECATED, HPC is not Allowed."));
                        result = true;
                        return result;
                    }
                    break;

                case TableConstants.VM_TRANSACTION_MAST_DUP_FC:
                    if (fitnessDobj == null) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Duplicate FC for this Registration is not Allowed either due to Record is not found or Fintess is not Done."));
                        result = true;
                        return result;
                    }
                    //for handling case when vehicle comes for Fitness  within same state to different RTO
                    if (onlineApplDataList != null && onlineApplDataList.isEmpty() && tmConfigurationDobj != null) {
                        boolean allow_fitness_all_rto = tmConfigurationDobj.isAllow_fitness_all_RTO();
                        if (allow_fitness_all_rto && ownerDobj.getOff_cd() != currentOffCd && fitnessDobj.getOff_cd() != currentOffCd) {
                            facesMessages = "Can't Inward Application because of Vehicle Duplicate FC for this Registration is not Allowed either due to Record is not found or Fintess is not Done.";
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
                            PrimeFaces.current().ajax().update("msgDialog");
                            PrimeFaces.current().executeScript("PF('messageDialog').show();");
                            return true;
                        }
                        if (allow_fitness_all_rto && ownerDobj.getOff_cd() == currentOffCd && fitnessDobj.getOff_cd() != ownerDobj.getOff_cd()) {
                            facesMessages = "Can't Inward Application because of Vehicle Duplicate FC is not in Selected office. Please Contact to State [ " + ServerUtil.getStateNameByStateCode(currentStateCd) + " ] at Office [ " + ServerUtil.getOfficeName(fitnessDobj.getOff_cd(), currentStateCd) + " ]";
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
                            PrimeFaces.current().ajax().update("msgDialog");
                            PrimeFaces.current().executeScript("PF('messageDialog').show();");
                            return true;
                        }
                    }
                    //End 

                    break;
                case TableConstants.VM_MAST_RC_CANCELLATION:
                    if (purCd.size() > 1) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Only Rc Cancel is Allowed !!!", "Only Rc Cancel is Allowed !!!"));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        result = true;
                        return result;
                    }

                    if (vhType == TableConstants.VM_VEHTYPE_TRANSPORT && permitDetailDobj != null) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Can't Inward Application due to Permit is not Surrender !!!", "Can't Inward Application due to Permit is not Surrender !!!"));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        result = true;
                        return result;
                    }
                    break;
                case TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS:
                case TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE:
                    String isAppApproved = applicationInwardImpl.isApplApproved(regn_no, purCd.get(i), currentStateCd);
                    if (isAppApproved != null) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, isAppApproved, isAppApproved));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        result = true;
                        return result;
                    }
                    break;
                case TableConstants.VM_MAST_VEHICLE_SCRAPE:
                    boolean isExist = ScrappedVehicleImpl.scrapDetailsExist(ownerDobj.getRegn_no(), ownerDobj.getState_cd(), ownerDobj.getOff_cd());
                    if (isExist) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "This Vehicle is Already Scrapped.", "This Vehicle is Already Scrapped."));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        return true;
                    }
                    if (tmConfigurationDobj != null && !isCondition(replaceTagValues(tmConfigurationDobj.getScrap_veh_type(), vehParameters), "getScrapVehType")) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "This Vehicle Type Can't be Scrapped for Current State.", "This Vehicle Type Can't be Scrapped for Current State"));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        return true;
                    }
                    if (vhType == TableConstants.VM_VEHTYPE_TRANSPORT && permitDetailDobj != null) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Can't Inward Application due to Permit is not Surrender.", "Can't Inward Application due to Permit is not Surrender."));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        return true;
                    }
                    if (!isHypth) {
                        isHypth = ServerUtil.checkHypthOrNot(regn_no, ownerDobj.getState_cd());
                    }
                    if (isHypth && !(isVehicleScrapAppl && isHptAppl && purCd.size() == 2)) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Vehicle is HYPOTHECATED, You have to first Inward for Hypothecation Termination before Scrapping of Vehicle", "Vehicle is HYPOTHECATED, You have to first Inward for Hypothecation Termination before Scrapping of Vehicle"));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        result = true;
                        return result;
                    }
                    break;

                case TableConstants.VM_MAST_NON_USE:
                    NonUseImpl nonUseImpl = new NonUseImpl();
                    if ("CG".equals(currentStateCd) && permitDetailDobj != null && !nonUseImpl.checkMFormStatus(regn_no) && !"HGV,MGV,LGV".contains(ownerDobj.getVch_catg())) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Submit M form  Before K Form ", "Please Submit M form  Before K Form"));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        result = true;
                        return result;
                    }

                    if (isNonUse && !(tmConfigurationNonuseDobj != null && tmConfigurationNonuseDobj.isNonuse_continue_without_restore())) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "This Vehicle is Already In NonUse", "This Vehicle is Already In NonUse"));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        return true;
                    }

                    if (fit_upto != null && fit_upto.compareTo(currentDate) < 0 && tmConfigurationNonuseDobj != null && !tmConfigurationNonuseDobj.isSkip_fitness_validation_in_nonuse()) {
                        //skip fitness validation
                        facesMessages = "Application can't be inward due to Fitness validity Expired.";
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        return true;
                    }

                    if ("AS".equals(currentStateCd) && ins_bean.getIns_upto() != null && ins_bean.getIns_upto().compareTo(currentDate) < 0) {
                        facesMessages = "Application can't be inward due to Insurance validity Expired.";
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        return true;
                    }

                    if ("AS".equals(currentStateCd) && permitDetailDobj != null && permitDetailDobj.getValid_upto().before(new Date())) {
                        facesMessages = "Application can't be inward due to Permit validity expired.";
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        result = true;
                        return result;
                    }

                    break;
                case TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO:
                    if (vhType == TableConstants.VM_VEHTYPE_TRANSPORT) {
                        int surrenderPurCode = new SurrenderPermitImpl().getTransPurCdFromVtPmtTranaction(regn_no);
                        if (permitDetailDobj != null) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Can't Inward Application due to Permit is not Surrender !!!", "Can't Inward Application due to Permit is not Surrender !!!"));
                            PrimeFaces.current().ajax().update("msgDialog");
                            PrimeFaces.current().executeScript("PF('messageDialog').show();");
                            return true;
                        } else if (surrenderPurCode != TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD && surrenderPurCode != TableConstants.EMPTY_INT) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Can't Inward Application due to Permit is not Surrender for Reassigment of Vehicle !!!", "Can't Inward Application due to Permit is not Surrender for Reassigment of Vehicle !!!"));
                            PrimeFaces.current().ajax().update("msgDialog");
                            PrimeFaces.current().executeScript("PF('messageDialog').show();");
                            return true;
                        }
                    }
                    break;
                case TableConstants.VM_TRANSACTION_MAST_FIT_REVOKE:
                    if (fitnessDobj != null) {
                        if (!CommonUtils.isNullOrBlank(fitnessDobj.getRemark()) && fitnessDobj.getRemark().startsWith("FC:")) {
                            if (tmConfigurationFitnessDobj == null) {
                                isRevocationAllowed = false;
                            } else {
                                Date date = ServerUtil.dateRange(fitnessDobj.getFit_valid_to(), 0, 0, tmConfigurationFitnessDobj.getFitness_revoke_allowed_days());
                                if (tmConfigurationFitnessDobj.isFitness_revoke_allowed() && date.compareTo(currentDate) >= 0) {
                                    isRevocationAllowed = true;
                                } else {
                                    isRevocationAllowed = false;
                                }
                            }
                        }
                        if (!isRevocationAllowed) {
                            facesMessages = "Revocation of Fitness is not Allowed for this Vehicle, Please go for Renewal of Fitness.";
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
                            PrimeFaces.current().ajax().update("msgDialog");
                            PrimeFaces.current().executeScript("PF('messageDialog').show();");
                            result = true;
                            return result;
                        }
                    }
                    break;
                case TableConstants.VM_TRANSACTION_MAST_RC_TO_SMART_CARD_CONVERSION:
                    //boolean isSmartCard = ServerUtil.verifyForSmartCard(appl_no, pur_cd, null)
                    VmSmartCardHsrpDobj dobj = new VmSmartCardHsrpDobj();
                    dobj = ServerUtil.getVmSmartCardHsrpParameters(currentStateCd, currentOffCd);
                    if (dobj != null) {
                        if (!dobj.isSmart_card()) {
                            facesMessages = "Smart Card is not allowed in this RTO, Please Contact to the System Administrator.";
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
                            PrimeFaces.current().ajax().update("msgDialog");
                            PrimeFaces.current().executeScript("PF('messageDialog').show();");
                            result = true;
                            return result;
                        }
                    }
                    boolean isSmartCardFeePaid = applicationInwardImpl.getSmartCardFeeDetails(regn_no);
                    if (isSmartCardFeePaid) {
                        facesMessages = "Smart Card is Already available for this vehicle, Please Contact to the System Administrator.";
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        result = true;
                        return result;
                    }
                    break;
                case TableConstants.VM_TRANSACTION_MAST_CHG_ADD:
                    if (ownerDetail.getState_cd().equalsIgnoreCase(currentStateCd)
                            && ownerDetail.getOff_cd() != currentOffCd
                            && ownerDetail.getVch_catg() != null) {
                        if (!isCondition(replaceTagValues(tmConfigurationApplInwardDobj.getCheck_for_TO_without_NOC(), vehParameters), "getCheck_for_TO_without_NOC")) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Change Of Address not allowed for this Vehilce without NOC in Different Offices.", "Change Of Address not allowed for this Vehilce without NOC in Different Offices."));
                            PrimeFaces.current().ajax().update("msgDialog");
                            PrimeFaces.current().executeScript("PF('messageDialog').show();");
                            return true;
                        }
                    }
                    break;
                case TableConstants.RETENTION_OF_REGISTRATION_NO_PUR_CD:
                    if (purCd.size() > 1) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Retention of Registration No can't allowed with other transaction. Please select Retention of Registration No only.", "Retention of Registration No can't allowed with other transaction. Please select Retention of Registration No only."));
                        PrimeFaces.current().ajax().update("msgDialog");
                        PrimeFaces.current().executeScript("PF('messageDialog').show();");
                        return true;
                    }
                    break;
            }
        }

        if (rcParticularCheck && purCd.size() > 1) {
//
            if (purCd.size() == 2 && isVehicleScrapAppl && isHptAppl && isHypth) {
//                //do nothing
            } else {
                facesMessages = "Only Scrapped Vehicle or RC Particulars or RC Particulars for Office Purpose is Allowed to Inward at a Time!!!";
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
                PrimeFaces.current().ajax().update("msgDialog");
                PrimeFaces.current().executeScript("PF('messageDialog').show();");
                result = true;
                return result;
            }
        } else if (rcParticularCheck && purCd.size() == 1) {
            result = false;
        }
        if ("RJ".equalsIgnoreCase(currentStateCd) && userCatg.equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_USER)
                && vhType == TableConstants.VM_VEHTYPE_TRANSPORT && DateUtils.isBefore(DateUtils.parseDate(ownerDobj.getRegn_upto()), DateUtils.getCurrentDate())) {
            facesMessages = "Cant't Inward Application as Vehicle Registration Validity has been expired, please contact respective Registering Authority for Renewal of Registration.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, facesMessages, facesMessages));
            PrimeFaces.current().ajax().update("msgDialog");
            PrimeFaces.current().executeScript("PF('messageDialog').show();");
            result = true;
            return result;
        }

        return result;
    }

    private boolean validateFitnessApp(Date fitUpto) {
        boolean flag = false;
        Date today = new Date();
        Date priorMonth = new Date();
        priorMonth = ServerUtil.dateRange(today, 0, -1, 0);//Appl inward allowed within 1 month from fitness validity.
        if (fitUpto.compareTo(today) < 0) {//Expired from current date
            flag = true;
        } else if ((fitUpto.compareTo(today) > 0) && (today.compareTo(priorMonth) >= 0)) {//Valid from current date but under 1 month of validity
            flag = true;
        }
        return flag;
    }

    public String selectedSeatWork() {
        String requestedUrl = null;
        try {
            int action_cd = 0;
            int action_role = 0;
            int purCode = 0;
            String cur_status = null;
            Map map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            if (map != null && seatWork != null && !seatWork.isEmpty() && seatWork.size() > 1) {
                action_cd = Integer.parseInt(map.get("actionCode").toString());
                action_role = Integer.parseInt(map.get("actionCode").toString().substring(3));// for extracting role_code from action_code
                purCode = Integer.parseInt(map.get("pur_code").toString());
                cur_status = (String) map.get("cur_status");
            } else if (seatWork != null && !seatWork.isEmpty() && seatWork.size() == 1) {
                action_cd = seatWork.get(0).getAction_cd();
                action_role = Integer.parseInt(String.valueOf(seatWork.get(0).getAction_cd()).substring(3));// for extracting role_code from action_code
                purCode = seatWork.get(0).getPur_cd();
                cur_status = seatWork.get(0).getStatus();
                map = new HashMap();
                map.put("appl_no", seatWork.get(0).getAppl_no());
                map.put("pur_code", seatWork.get(0).getPur_cd());
                map.put("actionCode", seatWork.get(0).getAction_cd());
                map.put("Purpose", seatWork.get(0).getPurpose_descr());
                map.put("office_remark", seatWork.get(0).getOffice_remark());
                map.put("public_remark", seatWork.get(0).getRemark_for_public());
                map.put("regn_no", seatWork.get(0).getRegn_no());
                map.put("appl_dt", seatWork.get(0).getAppl_dt());
                map.put("cur_status", seatWork.get(0).getStatus());
                map.put("redirect_url", seatWork.get(0).getRedirect_url());
            }

            if (Home_Impl.getRoleCodeOfSeatWork(action_cd) == 9) {
                List list = Home_Impl.getCashCounterDetails();
                FacesMessage msg;
                String constMsg = "";
                if (list.size() > 0) {

                    if (list.get(0) != null && !list.get(0).equals(Home_Impl.getDBCurrentDate())) {
                        constMsg = "Day Begin/Cash Counter Open Process not Started.";
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, constMsg, constMsg);
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        return null;
                    }
                    if (((boolean) list.get(1)) == true) {
                        constMsg = "Cash Counter is not Opened.";
                        msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, constMsg, constMsg);
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                        return null;
                    }
                } else {
                    constMsg = "Day Begin/Cash Counter Open Process not Started.";
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, constMsg, constMsg);
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    return null;
                }
            }
            //copy map
            Map map2 = new HashMap();
            map2.putAll(map);

            map2.put("request_source", "seat");
            Util.getSession().setAttribute("seat_map", map2);
            selectedSeat.setOff_cd(currentOffCd);
            Util.getSession().setAttribute("SelectedSeat", selectedSeat);
            //added By Nitin Kumar
            if (purCode != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS) {
                if (purCode != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE
                        && purCode != TableConstants.VM_PMT_PERMIT_PARTICULAR_PUR_CD_WITHOUT_FEE) {
                    if (action_role == TableConstants.TM_ROLE_PRINT && cur_status.equalsIgnoreCase(TableConstants.STATUS_NOT_STARTED)) {
                        return "print_normal";
                    }
                }
            }
            //added By Nitin Kumar

            requestedUrl = getSelectedSeat().getRedirect_url();
            seatWork.clear();
        } catch (Exception e) {
        }

        return requestedUrl;
    }

    public List<ComparisonBean> compareChanges(OwnerDetailsDobj dobj) {

        if (this.ownerIdentificationPrev == null) {
            return compBeanList;
        }
        compBeanList.clear();

        if (ownerIdentificationPrev != null) {
            Compare("PAN_No", ownerIdentificationPrev.getPan_no(), dobj.getOwnerIdentity().getPan_no(), compBeanList);
            Compare("Mobile", ownerIdentificationPrev.getMobile_no(), dobj.getOwnerIdentity().getMobile_no(), compBeanList);
            Compare("Email", ownerIdentificationPrev.getEmail_id(), dobj.getOwnerIdentity().getEmail_id(), compBeanList);
            Compare("Aadhar", ownerIdentificationPrev.getAadhar_no(), dobj.getOwnerIdentity().getAadhar_no(), compBeanList);
            Compare("Passport", ownerIdentificationPrev.getPassport_no(), dobj.getOwnerIdentity().getPassport_no(), compBeanList);
            Compare("Ration", ownerIdentificationPrev.getRation_card_no(), dobj.getOwnerIdentity().getRation_card_no(), compBeanList);
            Compare("Voter_Id", ownerIdentificationPrev.getVoter_id(), dobj.getOwnerIdentity().getVoter_id(), compBeanList);
            Compare("DL", ownerIdentificationPrev.getDl_no(), dobj.getOwnerIdentity().getDl_no(), compBeanList);
            Compare("Owner Category", ownerIdentificationPrev.getOwnerCatg(), dobj.getOwnerIdentity().getOwnerCatg(), compBeanList);
        }

        return compBeanList;

    }

    public void skipMobileEntryListener() {
        boolean rcParticularCheck = false;
        boolean insuranceSkipCheck = false;
        renConversionTab = false;

        if (selectedPurposeCode != null && !selectedPurposeCode.isEmpty()) {
            for (int i = 0; i < selectedPurposeCode.size(); i++) {
                if (Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_COMPANY
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_OWNER
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_MAST_NON_USE) {
                    rcParticularCheck = true;
                }
                if (Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_COMPANY
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_OWNER
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_MAST_RC_CANCELLATION
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_FRESH_RC
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_MAST_NON_USE
                        || Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_MAST_VEHICLE_SCRAPE) {
                    insuranceSkipCheck = true;
                }

                if (rcParticularCheck || insuranceSkipCheck) {
                    break;
                }
            }

            for (int i = 0; i < selectedPurposeCode.size(); i++) {
                if (Integer.parseInt(selectedPurposeCode.get(i)) == TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION) {
                    renConversionTab = true;
                    vh_type.add(new SelectItem("1", "Transport"));
                    vh_type.add(new SelectItem("2", "Non-Transport"));
                    procesConversionDetails(ownerDetail.getVh_class(), ownerDetail.getVch_catg());
                    break;
                }
            }
        } else {
            renConversionTab = false;
        }
        if (rcParticularCheck) {
            ownerDetail.getOwnerIdentity().setMobileNoEditable(true);
        } else {
            ownerDetail.getOwnerIdentity().setMobileNoEditable(false);
        }

        if (insuranceSkipCheck) {//for insurance details check
            ins_bean.setDisable(true);
            ins_bean.setGovtVehFlag(true);
        } else {
            if (regStatus.isInsuranceStatus()) {
                ins_bean.setDisable(true);
                ins_bean.setGovtVehFlag(true);
            } else if (!ins_bean.isIibData()) {
                ins_bean.setDisable(false);
                ins_bean.setGovtVehFlag(false);
            }
        }
    }

    public String sendOtpAndVerifyOwnerMobileNo(String otpType) throws VahanException {
        String otp_msg = "";
        long mobileNo = ownerDetail.getOwnerIdentity().getMobile_no();
        String mobileno = String.valueOf(mobileNo);
        TmConfigurationDobj tmConfDobj = Util.getTmConfiguration();
        String uri = null;
        try {
            if (tmConfDobj != null && tmConfDobj.getTmConfigOtpDobj() != null && tmConfDobj.getTmConfigOtpDobj().isMobile_no_count_with_otp()
                    && !CommonUtils.isNullOrBlank(mobileNoCountMessage)
                    && !CommonUtils.isNullOrBlank(otpType)
                    && !CommonUtils.isNullOrBlank(mobileno)
                    && !(selectedPurposeCode.contains(String.valueOf(TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE)) && (selectedPurposeCode.size() == 1))) {
                switch (otpType) {
                    case "sendOtp":
                        setOwnerMobileVerifyOtp(new ServerUtil().genOTP(mobileno));
                        otp_msg = getOwnerMobileVerifyOtp() + ": OTP for Owner Mobile-No Verification. Please share with concerned RTO staff only.";
                        ServerUtil.sendOTP(mobileno, otp_msg, Util.getUserStateCode());
                        PrimeFaces.current().ajax().update("otp_confirmation");
                        PrimeFaces.current().ajax().update("otp_dialog");
                        PrimeFaces.current().executeScript("PF('otp_confrm').show()");
                        break;
                    case "resendOtp":
                        if (!CommonUtils.isNullOrBlank(ownerMobileVerifyOtp)) {
                            otp_msg = getOwnerMobileVerifyOtp() + ": OTP for Owner Mobile-No Verification. Please share with concerned RTO staff only.";
                            ServerUtil.sendOTP(mobileno, otp_msg, Util.getUserStateCode());
                        } else {
                            setOwnerMobileVerifyOtp(new ServerUtil().genOTP(mobileno));
                            otp_msg = getOwnerMobileVerifyOtp() + ": OTP for Owner Mobile-No Verification. Please share with concerned RTO staff only.";
                            ServerUtil.sendOTP(mobileno, otp_msg, Util.getUserStateCode());
                        }
                        break;
                    case "confirmOtp":
                        if (getOwnerMobileVerifyOtp() != null && getEnteredOwnerMobVerifyOtp() != null && (getOwnerMobileVerifyOtp().equals(getEnteredOwnerMobVerifyOtp()))) {
                            PrimeFaces.current().ajax().update(" otp_text otp_confirmation");
                            PrimeFaces.current().executeScript("PF('otp_confrm').hide()");
                            uri = saveApplicationNo();
                        } else {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Invalid OTP, Please enter correct OTP", "Invalid OTP, Please enter correct OTP"));

                        }
                        break;
                    default:
                        break;
                }
            } else {
                uri = saveApplicationNo();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));

        }
        return uri;
    }

    private void procesConversionDetails(int vh_cls, String vh_catg) {
        if (vh_cls != 0 && !vh_catg.isEmpty()) {
            //Detection of Vehicle Type
            int vh_type_tmp = 0;
            String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equals(vh_cls + "")) {
                    vh_type_tmp = Integer.parseInt(data[i][2]);
                }
            }
            if (vh_type_tmp == TableConstants.VM_VEHTYPE_TRANSPORT) {
                setVehType(TableConstants.VM_VEHTYPE_NON_TRANSPORT);
            } else if (vh_type_tmp == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                setVehType(TableConstants.VM_VEHTYPE_TRANSPORT);
            }
            ConvImpl implObj = new ConvImpl();
            vh_class = implObj.getConvertibleClassesList(vh_cls);
            if (vh_class.isEmpty()) {
                vh_class = MasterTableFiller.getVehicleClassTypeWise(getVehType());
            }

        }
    }

    public void vehClassListener() {
        String twoWheelerVhClass = ",1,2,3,4,51,52,53,";
        List vh_category_temp = new ArrayList();
        try {
            int vehClass = conv_dobj.getNew_vch_class();
            String oldVhClass = "," + String.valueOf(ownerDobj.getVh_class()) + ",";
            if (ServerUtil.isTransport(vehClass, null)) {
                setVehType(TableConstants.VM_VEHTYPE_TRANSPORT);
            } else {
                setVehType(TableConstants.VM_VEHTYPE_NON_TRANSPORT);

            }
            String[][] dataMap = MasterTableFiller.masterTables.VM_VHCLASS_CATG_MAP.getData();
            String[][] dataCatg = MasterTableFiller.masterTables.VM_VCH_CATG.getData();
            vh_category.clear();
            for (int i = 0; i < dataMap.length; i++) {
                if (dataMap[i][0].equals(vehClass + "")) {
                    for (int j = 0; j < dataCatg.length; j++) {
                        if (dataCatg[j][0].equals(dataMap[i][1])) {
                            vh_category.add(new SelectItem(dataCatg[j][0], dataCatg[j][1]));
                        }
                    }
                }
            }
            for (int i = 0; i < vh_category.size(); i++) {
                javax.faces.model.SelectItem obj = (javax.faces.model.SelectItem) vh_category.get(i);
                if (twoWheelerVhClass.contains(oldVhClass) && vehClass == 5 && obj.getValue().toString().equalsIgnoreCase("4WIC")) {
                    vh_category_temp.add(obj);
                } else if (!twoWheelerVhClass.contains(oldVhClass) && vehClass == 5 && obj.getValue().toString().equalsIgnoreCase("2WIC")) {
                    vh_category_temp.add(obj);
                }
            }
            vh_category.removeAll(vh_category_temp);
        } catch (Exception ex) {
            new VahanException(TableConstants.SomthingWentWrong);
        }
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
     * @return the selectedPurposeCode
     */
    public List<String> getSelectedPurposeCode() {
        return selectedPurposeCode;
    }

    /**
     * @param selectedPurposeCode the selectedPurposeCode to set
     */
    public void setSelectedPurposeCode(List<String> selectedPurposeCode) {
        this.selectedPurposeCode = selectedPurposeCode;
    }

    /**
     * @return the purCodeList
     */
    public Map<Object, Object> getPurCodeList() {
        return purCodeList;
    }

    /**
     * @param purCodeList the purCodeList to set
     */
    public void setPurCodeList(Map<Object, Object> purCodeList) {
        this.purCodeList = purCodeList;
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
     * @return the fitnessDobj
     */
    public FitnessDobj getFitnessDobj() {
        return fitnessDobj;
    }

    /**
     * @param fitnessDobj the fitnessDobj to set
     */
    public void setFitnessDobj(FitnessDobj fitnessDobj) {
        this.fitnessDobj = fitnessDobj;
    }

    /**
     * @return the seatWork
     */
    public List<SeatAllotedDetails> getSeatWork() {
        return seatWork;
    }

    /**
     * @param seatWork the seatWork to set
     */
    public void setSeatWork(List<SeatAllotedDetails> seatWork) {
        this.seatWork = seatWork;
    }

    /**
     * @return the selectedSeat
     */
    public SeatAllotedDetails getSelectedSeat() {
        return selectedSeat;
    }

    /**
     * @param selectedSeat the selectedSeat to set
     */
    public void setSelectedSeat(SeatAllotedDetails selectedSeat) {
        this.selectedSeat = selectedSeat;
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
     * @return the regStatus
     */
    public RegistrationStatusParametersDobj getRegStatus() {
        return regStatus;
    }

    /**
     * @param regStatus the regStatus to set
     */
    public void setRegStatus(RegistrationStatusParametersDobj regStatus) {
        this.regStatus = regStatus;
    }

    /**
     * @return the onlineApplDataList
     */
    public List<Status_dobj> getOnlineApplDataList() {
        return onlineApplDataList;
    }

    /**
     * @param onlineApplDataList the onlineApplDataList to set
     */
    public void setOnlineApplDataList(List<Status_dobj> onlineApplDataList) {
        this.onlineApplDataList = onlineApplDataList;
    }

    /**
     * @return the dupRegnList
     */
    public List<OwnerDetailsDobj> getDupRegnList() {
        return dupRegnList;
    }

    /**
     * @param dupRegnList the dupRegnList to set
     */
    public void setDupRegnList(List<OwnerDetailsDobj> dupRegnList) {
        this.dupRegnList = dupRegnList;
    }

    /**
     * @return the listOwnerCatg
     */
    public List getListOwnerCatg() {
        return listOwnerCatg;
    }

    /**
     * @param listOwnerCatg the listOwnerCatg to set
     */
    public void setListOwnerCatg(List listOwnerCatg) {
        this.listOwnerCatg = listOwnerCatg;
    }

    /**
     * @return the statusList
     */
    public List<Status_dobj> getStatusList() {
        return statusList;
    }

    /**
     * @param statusList the statusList to set
     */
    public void setStatusList(List<Status_dobj> statusList) {
        this.statusList = statusList;
    }

    /**
     * @return the permitDetailDobj
     */
    public PermitDetailDobj getPermitDetailDobj() {
        return permitDetailDobj;
    }

    /**
     * @param permitDetailDobj the permitDetailDobj to set
     */
    public void setPermitDetailDobj(PermitDetailDobj permitDetailDobj) {
        this.permitDetailDobj = permitDetailDobj;
    }

    /**
     * @return the permitSurrenderDetailDobj
     */
    public PermitDetailDobj getPermitSurrenderDetailDobj() {
        return permitSurrenderDetailDobj;
    }

    /**
     * @param permitSurrenderDetailDobj the permitSurrenderDetailDobj to set
     */
    public void setPermitSurrenderDetailDobj(PermitDetailDobj permitSurrenderDetailDobj) {
        this.permitSurrenderDetailDobj = permitSurrenderDetailDobj;
    }

    /**
     * @return the renderHpaVarification
     */
    public boolean isRenderHpaVarification() {
        return renderHpaVarification;
    }

    /**
     * @param renderHpaVarification the renderHpaVarification to set
     */
    public void setRenderHpaVarification(boolean renderHpaVarification) {
        this.renderHpaVarification = renderHpaVarification;
    }

    /**
     * @return the validateHpaVarification
     */
    public boolean isValidateHpaVarification() {
        return validateHpaVarification;
    }

    /**
     * @param validateHpaVarification the validateHpaVarification to set
     */
    public void setValidateHpaVarification(boolean validateHpaVarification) {
        this.validateHpaVarification = validateHpaVarification;
    }

    /**
     * @return the insuranceComparison
     */
    public String getInsuranceComparison() {
        return insuranceComparison;
    }

    /**
     * @param insuranceComparison the insuranceComparison to set
     */
    public void setInsuranceComparison(String insuranceComparison) {
        this.insuranceComparison = insuranceComparison;
    }

    /**
     * @return the officeRemarks
     */
    public String getOfficeRemarks() {
        return officeRemarks;
    }

    /**
     * @param officeRemarks the officeRemarks to set
     */
    public void setOfficeRemarks(String officeRemarks) {
        this.officeRemarks = officeRemarks;
    }

    /**
     * @return the vldtVehAgeExpireVarfcn
     */
    public boolean isVldtVehAgeExpireVarfcn() {
        return vldtVehAgeExpireVarfcn;
    }

    /**
     * @param vldtVehAgeExpireVarfcn the vldtVehAgeExpireVarfcn to set
     */
    public void setVldtVehAgeExpireVarfcn(boolean vldtVehAgeExpireVarfcn) {
        this.vldtVehAgeExpireVarfcn = vldtVehAgeExpireVarfcn;
    }

    /**
     * @return the renderVehAgeExpireVarfcn
     */
    public boolean isRenderVehAgeExpireVarfcn() {
        return renderVehAgeExpireVarfcn;
    }

    /**
     * @param renderVehAgeExpireVarfcn the renderVehAgeExpireVarfcn to set
     */
    public void setRenderVehAgeExpireVarfcn(boolean renderVehAgeExpireVarfcn) {
        this.renderVehAgeExpireVarfcn = renderVehAgeExpireVarfcn;
    }

    public List<TaxClearDobj> getTaxDetaillist() {
        return taxDetaillist;
    }

    public void setTaxDetaillist(List<TaxClearDobj> taxDetaillist) {
        this.taxDetaillist = taxDetaillist;
    }

    /**
     * @return the eChallanInfo
     */
    public List<ChallanReportDobj> geteChallanInfo() {
        return eChallanInfo;
    }

    /**
     * @param eChallanInfo the eChallanInfo to set
     */
    public void seteChallanInfo(List<ChallanReportDobj> eChallanInfo) {
        this.eChallanInfo = eChallanInfo;
    }

    /**
     * @return the echallanInfoURL
     */
    public String getEchallanInfoURL() {
        return echallanInfoURL;
    }

    /**
     * @param echallanInfoURL the echallanInfoURL to set
     */
    public void setEchallanInfoURL(String echallanInfoURL) {
        this.echallanInfoURL = echallanInfoURL;
    }

    /**
     * @return the currentOffCd1
     */
    public int getCurrentOffCd() {
        return currentOffCd;
    }

    /**
     * @param currentOffCd1 the currentOffCd1 to set
     */
    public void setCurrentOffCd(int currentOffCd) {
        this.currentOffCd = currentOffCd;
    }

    /**
     * @return the documentUpload_bean
     */
    public DocumentUploadBean getDocumentUpload_bean() {
        return documentUpload_bean;
    }

    /**
     * @param documentUpload_bean the documentUpload_bean to set
     */
    public void setDocumentUpload_bean(DocumentUploadBean documentUpload_bean) {
        this.documentUpload_bean = documentUpload_bean;
    }

    /**
     * @return the documentUploadShow
     */
    public boolean isDocumentUploadShow() {
        return documentUploadShow;
    }

    /**
     * @param documentUploadShow the documentUploadShow to set
     */
    public void setDocumentUploadShow(boolean documentUploadShow) {
        this.documentUploadShow = documentUploadShow;
    }

    public String getOwnerMobileVerifyOtp() {
        return ownerMobileVerifyOtp;
    }

    public void setOwnerMobileVerifyOtp(String ownerMobileVerifyOtp) {
        this.ownerMobileVerifyOtp = ownerMobileVerifyOtp;
    }

    public String getEnteredOwnerMobVerifyOtp() {
        return enteredOwnerMobVerifyOtp;
    }

    public void setEnteredOwnerMobVerifyOtp(String enteredOwnerMobVerifyOtp) {
        this.enteredOwnerMobVerifyOtp = enteredOwnerMobVerifyOtp;
    }

    public String getMobileNoCountMessage() {
        return mobileNoCountMessage;
    }

    public void setMobileNoCountMessage(String mobileNoCountMessage) {
        this.mobileNoCountMessage = mobileNoCountMessage;
    }

    /**
     * @return the moveToHistoryOwnerDtls
     */
    public OwnerDetailsDobj getMoveToHistoryOwnerDtls() {
        return moveToHistoryOwnerDtls;
    }

    /**
     * @param moveToHistoryOwnerDtls the moveToHistoryOwnerDtls to set
     */
    public void setMoveToHistoryOwnerDtls(OwnerDetailsDobj moveToHistoryOwnerDtls) {
        this.moveToHistoryOwnerDtls = moveToHistoryOwnerDtls;
    }

    /**
     * @return the currentOfficeName
     */
    public String getCurrentOfficeName() {
        return currentOfficeName;
    }

    /**
     * @param currentOfficeName the currentOfficeName to set
     */
    public void setCurrentOfficeName(String currentOfficeName) {
        this.currentOfficeName = currentOfficeName;
    }

    public FitnessDobj getTempFitnessDetails() {
        return tempFitnessDetails;
    }

    public void setTempFitnessDetails(FitnessDobj tempFitnessDetails) {
        this.tempFitnessDetails = tempFitnessDetails;
    }

    /**
     * @return the renderMoveToHistoryButton
     */
    public boolean isRenderMoveToHistoryButton() {
        return renderMoveToHistoryButton;
    }

    /**
     * @param renderMoveToHistoryButton the renderMoveToHistoryButton to set
     */
    public void setRenderMoveToHistoryButton(boolean renderMoveToHistoryButton) {
        this.renderMoveToHistoryButton = renderMoveToHistoryButton;
    }

    public AuctionDobj getAuctionDobj() {
        return auctionDobj;
    }

    public void setAuctionDobj(AuctionDobj auctionDobj) {
        this.auctionDobj = auctionDobj;
    }

    public boolean isRenConversionTab() {
        return renConversionTab;
    }

    public void setRenConversionTab(boolean renConversionTab) {
        this.renConversionTab = renConversionTab;
    }

    public List getVh_class() {
        return vh_class;
    }

    public void setVh_class(List vh_class) {
        this.vh_class = vh_class;
    }

    public ConvDobj getConv_dobj() {
        return conv_dobj;
    }

    public void setConv_dobj(ConvDobj conv_dobj) {
        this.conv_dobj = conv_dobj;
    }

    public int getVehType() {
        return vehType;
    }

    public void setVehType(int vehType) {
        this.vehType = vehType;
    }

    public List getVh_type() {
        return vh_type;
    }

    public void setVh_type(List vh_type) {
        this.vh_type = vh_type;
    }

    public List getVh_category() {
        return vh_category;
    }

    public void setVh_category(List vh_category) {
        this.vh_category = vh_category;
    }

    public boolean isDisableVehType() {
        return disableVehType;
    }

    public void setDisableVehType(boolean disableVehType) {
        this.disableVehType = disableVehType;
    }

    public boolean isRestrictPurposeforEchallan() {
        return restrictPurposeforEchallan;
    }

    public void setRestrictPurposeforEchallan(boolean restrictPurposeforEchallan) {
        this.restrictPurposeforEchallan = restrictPurposeforEchallan;
    }

}
