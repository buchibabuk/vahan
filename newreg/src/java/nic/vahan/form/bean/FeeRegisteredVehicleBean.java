/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
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
import nic.vahan.CommonUtils.TaxUtils;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.permit.PermitPanelBean;
import nic.vahan.form.dobj.AltDobj;
import nic.vahan.form.dobj.ConvDobj;
import nic.vahan.form.dobj.DOTaxDetail;
import nic.vahan.form.dobj.EpayDobj;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.FeeDobj;
import nic.vahan.form.dobj.Fee_Pay_Dobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.PaymentCollectionDobj;
import nic.vahan.form.dobj.Tax_Pay_Dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.impl.AltImpl;
import nic.vahan.form.impl.ConvImpl;
import nic.vahan.form.impl.EpayImpl;
import nic.vahan.form.impl.FeeImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.TaxServer_Impl;
import nic.vahan.form.impl.ToImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.RegnSeriesImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
//import tax.web.service.VahanTaxParameters;
import nic.vahan.form.dobj.tax.VahanTaxParameters;
import static nic.vahan.CommonUtils.FormulaUtils.fillTaxParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.fillVehicleParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.eapplication.OnlinePaymentImpl;
import nic.vahan.form.bean.fancy.RetenRegnNo_dobj;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.dobj.NocDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TaxExemptiondobj;
import nic.vahan.form.impl.ApplicationInwardImpl;
import nic.vahan.form.impl.BlackListedVehicleImpl;
import nic.vahan.form.impl.ExemptionFeeFineImpl;
import nic.vahan.form.impl.NocImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.RetentionImpl;
import nic.vahan.form.impl.TaxInstallmentConfigImpl;
import static nic.vahan.form.impl.TaxServer_Impl.callKLTaxService;
import static nic.vahan.form.impl.TaxServer_Impl.callTaxService;
import nic.vahan.form.impl.fancy.AdvanceRegnFeeImpl;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.server.CommonUtils;

/**
 *
 * @author tranC105
 */
@ManagedBean(name = "feeRegisteredvehicle")
@ViewScoped
public class FeeRegisteredVehicleBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(FeeRegisteredVehicleBean.class);
    private String veh_no;
    private String chasi_no;
    private Date regn_date;
    private Date fitness_val;
    private String owner_name;
    private String veh_class;
    private boolean isHypothecated;
    private String total_payable_Amount;
    private List vhClass_list = null;
    OwnerImpl ownerImpl = null;
    private Owner_dobj ownerDobj;
    private List<PaymentCollectionDobj> payment_list = new ArrayList<>();
    private PaymentCollectionBean paymentBean = new PaymentCollectionBean();
    FeeImpl feeImpl = new FeeImpl();
    @ManagedProperty(value = "#{rcpt_bean}")
    private ReceiptMasterBean rcpt_bean;
    private boolean isdisable = false;
    private PaymentCollectionDobj paymentDobj;
    private ConvDobj convdobj;
    //-----------------
    private String appl_no;
    private int pur_cd;
    private boolean renderTaxPanel = false;
    private List<TaxFormPanelBean> listTaxForm = new ArrayList();
    private TaxFormPanelBean taxFormBean = null;
    private long totalAmountPayable = 0;
    private boolean btn_save = true;
    private String generatedRcpt = null;
    private String chasino;
    private String regnno;
    Map map = null;
    // Afzal to get fee list details
    private List<FeeDobj> feeCollectionLists = null;
    private FormFeePanelBean feePanelBean = new FormFeePanelBean();
    @ManagedProperty(value = "#{permitPanelBean}")
    private PermitPanelBean permitPanelBean;
    private boolean renderPermitPanel = false;
    private Long userChrg;
    private boolean renderUserChargesAmountPanel = false;
    private List<FeeDobj> listTransWise = null;
    private Long totalUserChrg = 0l;
    private boolean renderSmartCardFeePanel = false;
    private Long smartCardFee;
    private Long smartCardChrg;
    private String rcptNoPopup;
    private boolean showInRto = true;
    private AltDobj altDobj = null;
    private boolean renderFeePanelLabel = false;
    private boolean renderTaxExemption = false;
    private boolean taxExemption = false;
    private TmConfigurationDobj tmConfDobj = null;
    private String regnNonodataPopup;
    private Map<String, Integer> purCodeList = null;
    private List<String> selectedPurCdForRevertBack;
    private boolean autoRunTaxListener = true;
    private String FEE_ACTION = "";
    private List<DOTaxDetail> taxDescriptionList = new ArrayList<>();
    private boolean taxInstallment = false;
    private boolean renderTaxInstallment = false;
    private String taxInstallMode = null;
    private String pinStatus = "N";
    private List list_Pincode;
    private SessionVariables sessionVariables = null;
    private boolean rendertempFeeAmount = false;
    private boolean renderOnlinePayBtn = false;
    private String paymentTypeBtn = "";
    private String onlineUserCredentialmsg = "";
    private boolean renderUserAndPasswored = false;
    private boolean renderCancelPayment = false;
    private FitnessDobj tempFitnessDetails = null;
    private String reasonForFitness = "";
    private boolean renderPanelForm = false;
    private boolean renderPanelReasonForFitness = false;
    private String fitnessInspectionFeeMessage = "";

    public FeeRegisteredVehicleBean() {
        sessionVariables = new SessionVariables();
    }

    public TaxFormPanelBean getTaxFormBean() {
        if (taxFormBean == null) {
            taxFormBean = new TaxFormPanelBean();
        }
        return taxFormBean;
    }

    public void setTaxFormBean(TaxFormPanelBean taxFormBean) {
        this.taxFormBean = taxFormBean;
    }

    @PostConstruct
    public void PostConstruct() {
        Exception e = null;
        try {
            if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                    || sessionVariables.getOffCodeSelected() == 0) {
                return;
            }
            setFeeCollectionLists(new ArrayList<FeeDobj>());
            setListTransWise(new ArrayList<FeeDobj>());
            setRenderTaxPanel(false);
            ownerImpl = new OwnerImpl();
            payment_list.add(new PaymentCollectionDobj());
            setVhClass_list(new ArrayList());
            setVhClass_list(MasterTableFiller.getVehicleClassList());
            ///-----------------
            map = (Map) Util.getSession().getAttribute("seat_map");
            if (map == null) {
                return;
            }

            if (map.get("appl_no") != null) {
                appl_no = map.get("appl_no").toString();
            } else {
                appl_no = Util.getSelectedSeat().getAppl_no();
            }

            if (map.get("pur_code") != null) {
                pur_cd = Integer.parseInt(map.get("pur_code").toString());
            } else {
                pur_cd = Util.getSelectedSeat().getPur_cd();
            }

            if (map.get("regn_no") != null) {
                veh_no = (String) map.get("regn_no");
            } else {
                veh_no = Util.getSelectedSeat().getRegn_no();
            }

            if (veh_no != null && pur_cd == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {
                FitnessImpl fitnessImpl = new FitnessImpl();
                //for setting checking if there is any temporary fitness exist or not
                tempFitnessDetails = fitnessImpl.getVtFitnessTempDetails(veh_no);
            }

            convdobj = ConvImpl.set_Conversion_appl_db_to_dobj(getAppl_no(), null);
            AltImpl altImpl = new AltImpl();
            altDobj = altImpl.set_ALT_appl_db_to_dobj(getAppl_no());
            if (convdobj != null || pur_cd == TableConstants.SWAPPING_REGN_PUR_CD) {
                feePanelBean.setPurposeCodeList(EpayImpl.getPurposeCodeList(sessionVariables.getStateCodeSelected(), this.getAppl_no().toUpperCase(), " and pur_cd not in (1,18) ", true));
            } else {
                feePanelBean.setPurposeCodeList(EpayImpl.getPurposeCodeList(sessionVariables.getStateCodeSelected(), this.getAppl_no().toUpperCase(), " and pur_cd not in (1,18) ", false));
            }
            if ("DL".equalsIgnoreCase(Util.getUserStateCode()) && pur_cd == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {
                renderPanelReasonForFitness = true;
                ApplicationInwardImpl inwardImpl = new ApplicationInwardImpl();
                if (ownerImpl != null) {
                    // ownerDobj = ownerImpl.set_Owner_appl_db_to_dobj(getVeh_no().toUpperCase(), null, null, 2);
                    Status_dobj staDobj = inwardImpl.getApplInwardOthOffDobj(appl_no);
                    if (staDobj != null) {
                        ownerDobj = ownerImpl.getVtOwnerDetailsForRegnNoStateCdOffCd(veh_no, staDobj.getState_cd(), staDobj.getOff_cd());
                    } else {
                        ownerDobj = ownerImpl.getVtOwnerDetailsForRegnNoStateCdOffCd(veh_no, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                    }
                }

                if (ownerDobj != null) {
                    tmConfDobj = Util.getTmConfiguration();//getting from session when user logged in.
                    if (tmConfDobj == null) {
                        tmConfDobj = ServerUtil.getTmConfigurationParameters(Util.getUserStateCode());
                    }
                    chasino = ownerDobj.getChasi_no();
                    ownerDobj.setAppl_no(appl_no);

                    if (altDobj != null) {
                        feeImpl.updateOwnerDobjForAltOrConv(altDobj, convdobj, ownerDobj);
                        setFitness_val(altDobj.getFit_date_upto());
                        setVeh_class(altDobj.getVh_class() + "");
                    }
                    if (convdobj != null) {
                        feeImpl.updateOwnerDobjForAltOrConv(altDobj, convdobj, ownerDobj);
                        setFitness_val(convdobj.getNew_fit_dt());
                        setVeh_class(convdobj.getNew_vch_class() + "");
                        FEE_ACTION = "CON";
                        if ("PB".contains(Util.getUserStateCode()) && ServerUtil.isTransport(convdobj.getOld_vch_class(), null)) {
                            ConvImpl convImpl = new ConvImpl();
                            Date fitUpto = convImpl.prevConvDtlsNtToTr(ownerDobj.getRegn_no(), Util.getUserSeatOffCode(), Util.getUserStateCode());
                            if (fitUpto != null) {
                                setRenderTaxExemption(true);
                            }
                        }

                    }
                    setChasi_no(ownerDobj.getChasi_no());
                    setRegn_date(ownerDobj.getRegn_dt());
                    setFitness_val(ownerDobj.getFit_upto());
                    setOwner_name(ownerDobj.getOwner_name());
                    setVeh_class(String.valueOf(ownerDobj.getVh_class()));
                    setIsdisable(true);
                }
            } else {
                renderPanelForm = true;
                getVehicleDetails();
            }
            //  getVehicleDetails();
            if (sessionVariables.getUserCatgForLoggedInUser().equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_USER) && sessionVariables.getStateCodeSelected().equalsIgnoreCase("RJ")) {
                renderPermitPanel = false;
                fitnessInspectionFeeMessage = "Applicable Inspection fee and GST has to paid to Fitness Center.";
            }
            ApplicationInwardImpl inwardImpl = new ApplicationInwardImpl();
            purCodeList = inwardImpl.getPurCodeDescr(appl_no);
            TmConfigurationDobj tmConf = Util.getTmConfiguration();
            if (tmConf != null) {
                if (tmConf.isOnlinePayment()) {
                    setRenderOnlinePayBtn(true);
                    boolean onlineData = new FeeImpl().getOnlinePayData(appl_no);
                    if (onlineData) {
                        setBtn_save(false);
                        setRenderOnlinePayBtn(false);
                        Object[] obj = new FeeImpl().getUserIDAndPassword(appl_no);
                        if (obj != null && obj.length > 0) {
                            String userInfo = "Online Payment Credentials User ID is : " + appl_no + " & Password : " + obj[0];
                            setRenderCancelPayment(true);
                            setOnlineUserCredentialmsg(userInfo);
                            setRenderUserAndPasswored(true);
                        } else {
                            setRenderCancelPayment(true);
                        }
                    }
                }
            }
        } catch (VahanException ve) {
            e = ve;
        } catch (Exception ee) {
            e = ee;
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
        }

        if (e != null && e instanceof VahanException) {
            setRcptNoPopup(e.getMessage());
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().executeScript("PF('dlgRcptNoPopup').show()");
        }
    }

    public void getVehicleDetails() throws Exception {
        getFeeCollectionLists().clear();
        getFeePanelBean().getFeeCollectionList().clear();
        if (rcpt_bean.getBook_rcpt_no() == null || rcpt_bean.getBook_rcpt_no().equals("")) {
            setRcptNoPopup("There is no Current Receipt No Assigned.");
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().executeScript("PF('dlgRcptNoPopup').show()");
            return;
        }
        if (("DL".equalsIgnoreCase(Util.getUserStateCode()) && pur_cd != TableConstants.VM_TRANSACTION_MAST_FIT_CERT) || (!"DL".equalsIgnoreCase(Util.getUserStateCode()))) {
            ApplicationInwardImpl inwardImpl = new ApplicationInwardImpl();
            if (ownerImpl != null) {
                // ownerDobj = ownerImpl.set_Owner_appl_db_to_dobj(getVeh_no().toUpperCase(), null, null, 2);
                Status_dobj staDobj = inwardImpl.getApplInwardOthOffDobj(appl_no);
                if (staDobj != null) {
                    ownerDobj = ownerImpl.getVtOwnerDetailsForRegnNoStateCdOffCd(veh_no, staDobj.getState_cd(), staDobj.getOff_cd());
                } else {
                    ownerDobj = ownerImpl.getVtOwnerDetailsForRegnNoStateCdOffCd(veh_no, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                }
            }
        }
        if (ownerDobj != null) {

            tmConfDobj = Util.getTmConfiguration();//getting from session when user logged in.
            if (tmConfDobj == null) {
                tmConfDobj = ServerUtil.getTmConfigurationParameters(Util.getUserStateCode());
            }
            chasino = ownerDobj.getChasi_no();
            ownerDobj.setAppl_no(appl_no);

            if (altDobj != null) {
                feeImpl.updateOwnerDobjForAltOrConv(altDobj, convdobj, ownerDobj);
                setFitness_val(altDobj.getFit_date_upto());
                setVeh_class(altDobj.getVh_class() + "");
            }
            if (convdobj != null) {
                feeImpl.updateOwnerDobjForAltOrConv(altDobj, convdobj, ownerDobj);
                setFitness_val(convdobj.getNew_fit_dt());
                setVeh_class(convdobj.getNew_vch_class() + "");
                FEE_ACTION = "CON";
                if ("PB".contains(Util.getUserStateCode()) && ServerUtil.isTransport(convdobj.getOld_vch_class(), null)) {
                    ConvImpl convImpl = new ConvImpl();
                    Date fitUpto = convImpl.prevConvDtlsNtToTr(ownerDobj.getRegn_no(), Util.getUserSeatOffCode(), Util.getUserStateCode());
                    if (fitUpto != null) {
                        setRenderTaxExemption(true);
                    }
                }

            }

            if (pur_cd == TableConstants.VM_TRANSACTION_MAST_REN_REG) {
                FEE_ACTION = "REN";
            }

            if (pur_cd == TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO) {
                FEE_ACTION = "RRN";
            }

//            if (Util.getUserStateCode().equalsIgnoreCase("OR")) {
//                setList_Pincode(new ArrayList());
//                setList_Pincode(ServerUtil.getPincodeList(Util.getUserStateCode(), Util.getUserSeatOffCode()));
//                if (getList_Pincode().size() > 0) {
//                    for (int i = 0; i < getList_Pincode().size(); i++) {
//                        if (ownerDobj.getC_pincode() == (int) getList_Pincode().get(i)) {
//                            setPinStatus("Y");
//                            break;
//                        } else {
//                            setPinStatus("N");
//                        }
//                    }
//                }
//            }
            if (pur_cd == TableConstants.VM_TRANSACTION_MAST_TO) {
                ownerDobj.setTax_mode(ConvImpl.getTaxModeInfo(getVeh_no().toUpperCase(), TableConstants.TM_ROAD_TAX));
            }

            VehicleParameters vehParameters = fillVehicleParametersFromDobj(ownerDobj);
            setChasi_no(ownerDobj.getChasi_no());
            setRegn_date(ownerDobj.getRegn_dt());
            setFitness_val(ownerDobj.getFit_upto());
            setOwner_name(ownerDobj.getOwner_name());
            setVeh_class(String.valueOf(ownerDobj.getVh_class()));
            setIsdisable(true);

            //Check for blacklisted
            BlackListedVehicleImpl obj = new BlackListedVehicleImpl();
            BlackListedVehicleDobj blackListedDobj = obj.getBlacklistedVehicleDetails(ownerDobj.getRegn_no(), ownerDobj.getChasi_no());
            if (blackListedDobj != null) {
                ownerDobj.setBlackListedVehicleDobj(blackListedDobj);
            }

            List<EpayDobj> feeList = EpayImpl.getFeeDetailsRegisteredVehicle(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), ownerDobj, appl_no, ownerDobj.getVh_class(), ownerDobj.getVch_catg(), convdobj, false, null, reasonForFitness);
            getFeePanelBean().strictResetPaymentList();
            for (EpayDobj feeDobj : feeList) {
                if (feeDobj.getPurCd() == TableConstants.SWAPPING_REGN_PUR_CD && tmConfDobj != null && "HR".contains(tmConfDobj.getState_cd()) && tmConfDobj.isTo_retention()) {
                    ToImpl toImpl = new ToImpl();
                    if (!toImpl.isHRSurrenderRetention(appl_no)) {
                        continue;
                    }
                } else if (feeDobj.getPurCd() == TableConstants.SWAPPING_REGN_PUR_CD && tmConfDobj != null && tmConfDobj.isTo_retention()) {
                    ToImpl toImpl = new ToImpl();
                    if (!toImpl.isSurrenderRetention(appl_no)) {
                        continue;
                    }
                }

                FeeDobj collectedFeeDobj = new FeeDobj();
                if ((feeDobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_GREEN_TAX) && (feeDobj.getE_TaxFee() == 0)
                        || (feeDobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_ADDL_TO_VEHICLE
                        && getPur_cd() == TableConstants.VM_TRANSACTION_MAST_TO && feeDobj.getE_TaxFee() == 0)) {
                    collectedFeeDobj.setReadOnlyFee(false);
                }
                vehParameters.setPUR_CD(feeDobj.getPurCd());
                if (isCondition(replaceTagValues(tmConfDobj.getFee_amt_zero(), vehParameters), "getVehicleDetails-1")) {
                    if (pur_cd != TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO && !Util.getUserStateCode().equalsIgnoreCase("HR")) {
                        collectedFeeDobj.setFeeAmount(0L);
                    } else {
                        collectedFeeDobj.setFeeAmount((long) feeDobj.getE_TaxFee());
                    }
                } else {
                    collectedFeeDobj.setFeeAmount((long) feeDobj.getE_TaxFee());
                }

                if ("OR".equalsIgnoreCase(Util.getUserStateCode()) && (TableConstants.POSTAL_PUR_CD == feeDobj.getPurCd())) {
                    int fee = ServerUtil.getPincodeFee(sessionVariables.getStateCodeSelected(), ownerDobj.getC_pincode());
                    collectedFeeDobj.setFeeAmount((long) fee);
                }

                //Retention of Registration No. fee 1/2 or 1/4 of fancy number fee
                if ("CH,WB".contains(Util.getUserStateCode()) && feeDobj.getPurCd() == TableConstants.SWAPPING_REGN_PUR_CD
                        && tmConfDobj != null && tmConfDobj.isTo_retention()) {
                    long reserveAmount = AdvanceRegnFeeImpl.getOutOfTurnFee(veh_no.substring(veh_no.length() - 4, veh_no.length()), Util.getUserStateCode(), String.valueOf(ownerDobj.getVehType()), String.valueOf(ownerDobj.getVh_class()));
                    if (reserveAmount > 0) {
                        if ("WB".contains(Util.getUserStateCode())) {
                            collectedFeeDobj.setFeeAmount((long) reserveAmount / 4);
                        } else if (ownerDobj.getOwner_cd() != 4 && ownerDobj.getOwner_cd() != 5) {
                            collectedFeeDobj.setFeeAmount((long) reserveAmount / 2);
                        }
                    }
                }
                if ("UP".contains(Util.getUserStateCode()) && pur_cd == TableConstants.SWAPPING_REGN_PUR_CD) {
                    long reserveAmount = AdvanceRegnFeeImpl.getOutOfTurnFee(veh_no.substring(veh_no.length() - 4, veh_no.length()), Util.getUserStateCode(), String.valueOf(ownerDobj.getVehType()), String.valueOf(ownerDobj.getVh_class()));
                    if (reserveAmount > 0) {
                        if (collectedFeeDobj.getFeeAmount() < (reserveAmount / 5)) {
                            collectedFeeDobj.setFeeAmount((long) reserveAmount / 5);
                        }
                    }
                    if (ownerDobj.getOwner_cd() == 4 || ownerDobj.getOwner_cd() == 5) {
                        collectedFeeDobj.setFeeAmount(0l);
                    }
                }
                collectedFeeDobj.setFromDate(feeDobj.getFromDate());
                collectedFeeDobj.setUptoDate(feeDobj.getUptoDate());
                if (collectedFeeDobj.getFromDate() != null && collectedFeeDobj.getUptoDate() != null) {
                    collectedFeeDobj.setRenderFromDate(true);
                    collectedFeeDobj.setRenderUptoDate(true);
                }
                collectedFeeDobj.setFineAmount((long) feeDobj.getE_FinePenalty());

                if (feeDobj.getFeeFineExemptionDobj() != null && (feeDobj.getFeeFineExemptionDobj().getExemFeeAmount() > 0l
                        || feeDobj.getFeeFineExemptionDobj().getExemFineAmount() > 0l)) {
                    feeDobj.getFeeFineExemptionDobj().setRegnNo(ownerDobj.getRegn_no());
                    feeDobj.getFeeFineExemptionDobj().setPurCd(feeDobj.getPurCd());
                    feeDobj.getFeeFineExemptionDobj().setOffCd(sessionVariables.getOffCodeSelected());
                    feeDobj.getFeeFineExemptionDobj().setStateCd(sessionVariables.getStateCodeSelected());
                    collectedFeeDobj.setFeeFineExemDobj(feeDobj.getFeeFineExemptionDobj());
                }

                if (collectedFeeDobj.getFineAmount() > 0
                        && feeDobj.getDueDate() != null) {
//                        && DateUtils.compareDates("28-DEC-2016", JSFUtils.convertToStandardDateFormat(feeDobj.getDueDate())) == 1) {
                    collectedFeeDobj.setReadOnlyFine(true);//for making fine value readonly
                }
                collectedFeeDobj.setPurCd(feeDobj.getPurCd());
                collectedFeeDobj.setDisableDropDown(true);
                collectedFeeDobj.setDueDate(feeDobj.getDueDate());
                collectedFeeDobj.setDueDateString(feeDobj.getDueDateString());
                getFeePanelBean().getPayableFeeCollectionList().add(collectedFeeDobj);
                getFeePanelBean().getFeeCollectionList().add(collectedFeeDobj);
                getFeeCollectionLists().add(collectedFeeDobj);
                if (tmConfDobj.isIsFancyFeeZero() && feeDobj.getPurCd() == TableConstants.SWAPPING_REGN_PUR_CD) {
                    boolean isfancy = new RegnSeriesImpl().isRunningNoIsFancyNo(sessionVariables.getSelectedWork().getRegn_no().substring(sessionVariables.getSelectedWork().getRegn_no().length() - 4), sessionVariables.getStateCodeSelected());
                    if (isfancy) {
                        collectedFeeDobj.setFeeAmount(0l);
                    }
                }
                if ("HP,CH".contains(Util.getUserStateCode()) && feeDobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO && convdobj != null && !ServerUtil.checkNextSeriesGen(appl_no, TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION)) {
                    for (FeeDobj dobj : feeCollectionLists) {
                        if (dobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO) {
                            feeCollectionLists.remove(dobj);
                            break;
                        }
                    }
                    for (FeeDobj dobj : getFeePanelBean().getFeeCollectionList()) {
                        if (dobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO) {
                            getFeePanelBean().getFeeCollectionList().remove(dobj);
                            break;
                        }
                    }
                }
                if ("GJ".equalsIgnoreCase(Util.getUserStateCode()) && feeDobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_NOC) {
                    NocImpl nocimpl = new NocImpl();
                    NocDobj noc = nocimpl.set_NOC_appl_db_to_dobj(appl_no, regnno);
                    if (noc != null && !CommonUtils.isNullOrBlank(noc.getState_to())) {
                        for (FeeDobj dobj : feeCollectionLists) {
                            if (dobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_NOC) {
                                feeCollectionLists.remove(dobj);
                                break;
                            }
                        }
                        for (FeeDobj dobj : getFeePanelBean().getFeeCollectionList()) {
                            if (dobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_NOC) {
                                getFeePanelBean().getFeeCollectionList().remove(dobj);
                                break;
                            }
                        }
                    }
                }
            }
            if (Util.getUserStateCode().equalsIgnoreCase("SK")) {
                RetentionImpl retImpl = new RetentionImpl();
                int rcptDur = 0;
                RetenRegnNo_dobj dobj = retImpl.getVtSurrenderRetentionDetails(appl_no);
                if (dobj != null) {
                    rcptDur = DateUtil.getDate1MinusDate2_Months(dobj.getAppl_date(), DateUtils.getCurrentLocalDate()) / 12 - 1;
                    if (rcptDur > 0) {
                        boolean removeSwappRegnFee = false;
                        FeeDobj removeSwappRegnFeeDobj = null;
                        for (FeeDobj fee : feeCollectionLists) {
                            if (fee.getPurCd() == TableConstants.SWAPPING_REGN_PUR_CD) {
                                removeSwappRegnFee = true;
                                removeSwappRegnFeeDobj = fee;
                            }
                        }
                        if (removeSwappRegnFee) {
                            feeCollectionLists.remove(removeSwappRegnFeeDobj);
                        }
                        FeeDobj collectedFeeDobj = new FeeDobj();
                        collectedFeeDobj.setFeeAmount((long) rcptDur * 5000 + removeSwappRegnFeeDobj.getFeeAmount());
                        collectedFeeDobj.setFineAmount((long) 0 + removeSwappRegnFeeDobj.getFineAmount());
                        collectedFeeDobj.setPurCd(TableConstants.SWAPPING_REGN_PUR_CD);
                        collectedFeeDobj.setDisableDropDown(true);
                        collectedFeeDobj.setDueDate(new Date());
                        collectedFeeDobj.setDueDateString(DateUtil.parseDateToString(new Date()));
                        getFeePanelBean().getPayableFeeCollectionList().add(collectedFeeDobj);
                        getFeePanelBean().getFeeCollectionList().add(collectedFeeDobj);
                        getFeeCollectionLists().add(collectedFeeDobj);
                    }
                }
            }
            setFinePenaltyDetails(appl_no);
            if (ServerUtil.isTaxOnPermit(ownerDobj.getVh_class(), Util.getUserStateCode())) {
                PassengerPermitDetailDobj permitDob = TaxServer_Impl.getPermitInfoForSavedTax(ownerDobj);
                TaxInstallmentConfigImpl instaximpl = new TaxInstallmentConfigImpl();
                PassengerPermitDetailDobj permitDob_info = instaximpl.getPermitBaseOnTaxPermitInfo(ownerDobj.getState_cd(), ownerDobj.getOff_cd(), ownerDobj.getRegn_no());
                if (permitDob != null && permitDob_info != null && permitDob.getOp_dt() != null && permitDob_info.getOp_dt() != null) {
                    if (DateUtils.compareDates(permitDob.getOp_dt(), permitDob_info.getOp_dt()) == 1) {
                        permitDob = permitDob_info;
                    }
                } else if (permitDob == null && permitDob_info != null) {
                    permitDob = permitDob_info;
                }
                if (permitDob != null) {
                    getPermitPanelBean().setPermitDobj(permitDob);
                    getPermitPanelBean().onSelectPermitType(null);
                }
                //In any case set pmt_type and pmt_catg in case of HP state
                if ("HP,CH,RJ".contains(Util.getUserStateCode()) && convdobj != null && convdobj.getPmt_type() > 0) {
                    if (permitDob == null) {
                        permitDob = new PassengerPermitDetailDobj();
                    }
                    permitDob.setPmt_type_code(Integer.toString(convdobj.getPmt_type()));
                    permitDob.setPmtCatg(Integer.toString(convdobj.getPmt_catg()));
                    permitDob.setServices_TYPE(Integer.toString(convdobj.getServiceType()));
                    getPermitPanelBean().setIsDisable(true);

                    getPermitPanelBean().setPermitDobj(permitDob);
                    getPermitPanelBean().onSelectPermitType(null);
                }
                setRenderPermitPanel(true);
            } else {
                getPermitPanelBean().setPermitDobj(null);
                setRenderPermitPanel(false);
            }

            VahanTaxParameters taxParameters = fillTaxParametersFromDobj(ownerDobj, getPermitPanelBean().getPermitDobj());
            List<EpayDobj> listTaxTypes = EpayImpl.getFeeDetailsByActionTax(ownerDobj, FEE_ACTION);
            if (listTaxTypes.size() > 0) {
                for (EpayDobj ePay : listTaxTypes) {
                    if (ePay.isTaxExempted()) {
                        setRenderTaxExemption(true);
                    }
                }
                setRenderTaxPanel(true);
            } else {
                setRenderTaxPanel(false);
            }
            //For Mulitple times conversion case
            if (FEE_ACTION.equalsIgnoreCase("CON")) {
                if (convdobj != null) {
                    if (convdobj.getNewTaxDueFromFLag().equalsIgnoreCase("E")) {
                        setRenderTaxExemption(true);
                    }
                    if (isCondition(replaceTagValues(Util.getTmConfiguration().getTax_installment(), vehParameters), "getVehicleDetails")) {
                        renderTaxInstallment = true;
                    }
                }
            }

            fillTaxBeanList(taxParameters, listTaxTypes);

            getFeePanelBean().calculateTotal();
            updateTotalPayableAmount();

            if (ServerUtil.isSmartCardFeeForRegisteredVehicle(ownerDobj.getState_cd(), sessionVariables.getOffCodeSelected(), ownerDobj.getRegn_no(), getAppl_no())) {
                setSmartCardChrg(EpayImpl.getPurposeCodeFee(ownerDobj, ownerDobj.getVh_class(), 80, ownerDobj.getVch_catg()));
                if (getSmartCardChrg() > 0l) {
                    setRenderSmartCardFeePanel(true);
                    setSmartCardFee(getSmartCardChrg());
                    updateTotalPayableAmount();
                }
            } else {
                setRenderSmartCardFeePanel(false);
            }

        } else {
            setRegnNonodataPopup("No Data Found For Given Registration Number");
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().executeScript("PF('dlgNodataPopupforRegnNo').show()");
            reset();
        }
    }

    public void resonForFitnessListner() {
        renderPanelForm = false;
    }

    public void getFeeDetailsWithParkingValidity() {
        Exception e = null;
        try {
            renderPanelForm = true;
            getVehicleDetails();
        } catch (Exception ee) {
            e = ee;
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
        }

        if (e != null && e instanceof VahanException) {
            setRcptNoPopup(e.getMessage());
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().executeScript("PF('dlgRcptNoPopup').show()");
        }
    }

    public void setFinePenaltyDetails(String applNo) throws VahanException {
        FeeDobj dobj = null;
        ExemptionFeeFineImpl exemImpl = new ExemptionFeeFineImpl();
        List<TaxExemptiondobj> taxExemList = exemImpl.getExemptionDetails(applNo);
        for (TaxExemptiondobj exemdobj : taxExemList) {
            dobj = new FeeDobj();
            dobj.setPurCd(exemdobj.getPur_cd());
            dobj.setFineAmount(-exemdobj.getExemAmount());
            dobj.setFeeAmount(0l);
            dobj.setDisableDropDown(true);
            dobj.setReadOnlyFee(true);
            dobj.setReadOnlyFine(true);
            feeCollectionLists.add(dobj);
            getFeePanelBean().getFeeCollectionList().add(dobj);

        }
    }

    public void taxExemptionListener(AjaxBehaviorEvent event) {
        if (taxExemption) {
            setRenderTaxPanel(false);
        } else {
            setRenderTaxPanel(true);
        }
        updateTotalPayableAmount();

    }

    public void taxInstallListener(AjaxBehaviorEvent event) {
        if (taxInstallment) {
            setRenderTaxPanel(false);
        } else {
            setRenderTaxPanel(true);
        }
        updateTotalPayableAmount();
    }

    public void addNewRow(Integer purCd) {
        String mode = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("actionmode");
        if ("add".equalsIgnoreCase(mode)) {
            if (purCd == null || purCd == -1) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please Select Fee Head!"));
                return;
            }
            // add row in the fee panel
            if (getFeeCollectionLists().size() == 7) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Maximum number of Fees heads collection reached!"));
            } else {
                getFeeCollectionLists().add(new FeeDobj());
                getFeePanelBean().setEnable(false);
            }

        } else if ("minus".equalsIgnoreCase(mode)) {
            // remove current row from table.
            boolean isExist = feeImpl.checkPurCDExist(appl_no, purCd);
            if (isExist) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Fee Details can't be remove!"));
                return;
            }
            FeeDobj selectedFeeObject = new FeeDobj(purCd);
            int lastIndex = getFeeCollectionLists().lastIndexOf(selectedFeeObject);
            if (lastIndex == 0 && getFeeCollectionLists().size() == 1) {
                getFeeCollectionLists().clear();
                //getPayableFeeCollectionList().clear();
                getFeeCollectionLists().add(new FeeDobj());
                getFeePanelBean().setTotalFeeAmount(0);
                getFeePanelBean().setTotalFineAmount(0);
                getFeePanelBean().setTotalAmount(0);
            } else {
                getFeeCollectionLists().remove(lastIndex);
                //getPayableFeeCollectionList().remove(lastIndex);
                calculateTotal();
            }
        }
    }

    public void calculateFee(Integer selectedFeeCode, int vehClass, String vehCatg) throws VahanException {
        if (Utility.isNullOrBlank(vehCatg)) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Unknown Vehicle Category!"));
            return;
        }

        if (selectedFeeCode == -1 || selectedFeeCode == 0) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Unknown purpose code for fee collection!"));
            return;
        }
        if (vehClass == -1 || vehClass == 0) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Unknown vehicle class for fee colletion!"));
            return;
        }

        FeeDobj selectedFeeObject = new FeeDobj(selectedFeeCode);
        int lastIndex = getFeeCollectionLists().lastIndexOf(selectedFeeObject);
        getFeeCollectionLists().remove(lastIndex);
        //if (!getPayableFeeCollectionList().contains(selectedFeeObject)) {
        if (!getFeeCollectionLists().contains(selectedFeeObject)) {
            // List<E_pay_dobj> feeDobj = E_pay_Impl.getFeeDetailsByAction("KL", "NEW", 15, "LMV");
            Long feeValue = EpayImpl.getPurposeCodeFee(ownerDobj, vehClass, selectedFeeCode, vehCatg);
            getFeeCollectionLists().remove(selectedFeeObject);
            selectedFeeObject.setFeeAmount(feeValue);
            selectedFeeObject.setFineAmount(0l);
            selectedFeeObject.setTotalAmount(feeValue + selectedFeeObject.getFineAmount());
            if (selectedFeeCode == TableConstants.VM_TRANSACTION_MAST_MISC
                    || (selectedFeeCode == TableConstants.VM_TRANSACTION_MAST_ADDL_TO_VEHICLE
                    && getPur_cd() == TableConstants.VM_TRANSACTION_MAST_TO
                    && feeValue == 0)) {
                selectedFeeObject.setReadOnlyFee(false);
            }
            getFeeCollectionLists().add(selectedFeeObject);
            calculateTotal();
            updateTotalPayableAmount();
            getFeePanelBean().setEnable(true);

        } else {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Fee Head Already Selected!"));
            calculateTotal();
            return;
        }

    }

    public void calculateTotal() {
        long sumOfFee = 0;
        long sumOfFine = 0;
        long sumOfTotal = 0;

        for (FeeDobj sumDobj : getFeeCollectionLists()) {
            sumOfFee = sumOfFee + sumDobj.getFeeAmount();
            sumOfFine = sumOfFine + sumDobj.getFineAmount();
            sumOfTotal = sumOfTotal + sumDobj.getTotalAmount();
            getFeePanelBean().setTotalFeeAmount(sumOfFee);
            getFeePanelBean().setTotalFineAmount(sumOfFine);
            getFeePanelBean().setTotalAmount(sumOfTotal);
            updateTotalPayableAmount();
        }
    }

    public void taxModeListener(AjaxBehaviorEvent event) {
        TaxFormPanelBean taxSelectedFormBean = null;
        try {
            taxSelectedFormBean = (TaxFormPanelBean) event.getComponent().getAttributes().get("taxBeanAttr");
            if (taxSelectedFormBean.getTaxMode().equals("0")) {
                taxSelectedFormBean.resetAll();
                updateTotalPayableAmount();
                return;
            }
            if (taxSelectedFormBean.getTaxMode().equalsIgnoreCase("O") || taxSelectedFormBean.getTaxMode().equalsIgnoreCase("L")) {
                taxSelectedFormBean.setNoOfUnits(1);
            }
            VahanTaxParameters taxParameters = fillTaxParametersFromDobj(ownerDobj, getPermitPanelBean().getPermitDobj());
            taxParameters.setTAX_MODE(taxSelectedFormBean.getTaxMode());
            taxParameters.setPUR_CD(taxSelectedFormBean.getPur_cd());
            if (convdobj != null) {
                taxParameters.setOTHER_CRITERIA(convdobj.getOtherCriteria());
                if (convdobj.getNewTaxDueFromFLag().equalsIgnoreCase("T")) {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(TaxServer_Impl.getTaxDueFromDate(ownerDobj, taxSelectedFormBean.getPur_cd())));//private to commercial
                } else {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(convdobj.getNewTaxDueFrom()));
                }
                if (Util.getUserStateCode().equals("HR")) {
                    taxParameters.setREGN_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
                }
            }
            if (FEE_ACTION.equalsIgnoreCase("REN")) {
                if (Util.getUserStateCode().equalsIgnoreCase("JK")) {
                    String dt = null;
                    if (DateUtils.isAfter(DateUtils.parseDate(TaxServer_Impl.getTaxDueFromDate(ownerDobj, TableConstants.TM_ROAD_TAX)), DateUtils.parseDate(ownerDobj.getRegn_upto()))) {
                        dt = DateUtils.parseDate(TaxServer_Impl.getTaxDueFromDate(ownerDobj, TableConstants.TM_ROAD_TAX));
                    } else {
                        dt = DateUtils.parseDate(ownerDobj.getRegn_upto());
                    }
                    taxParameters.setTAX_DUE_FROM_DATE(dt);
                } else {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ServerUtil.dateRange(ownerDobj.getRegn_upto(), 0, 0, 1)));
                }
            }
            taxParameters.setTAX_MODE_NO_ADV(taxSelectedFormBean.getNoOfUnits());
            taxParameters.setTRANSACTION_PUR_CD(pur_cd);
            List<DOTaxDetail> listTaxBreakUp = null;
            if (taxParameters.getSTATE_CD().equals("KL")) {
                int pushbackseat = 0;
                int ordinaryseat = 0;
                if (TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(ownerDobj.getVh_class() + ",")) && (convdobj.getPush_bk_seat() != 0 || convdobj.getOrdinary_seat() != 0)) {
                    pushbackseat = convdobj.getPush_bk_seat();
                    ordinaryseat = convdobj.getOrdinary_seat();
                }
                listTaxBreakUp = callKLTaxService(taxParameters, pushbackseat, ordinaryseat, ownerDobj.getRegn_no(), ownerDobj.getChasi_no());
            } else {
                listTaxBreakUp = callTaxService(taxParameters);
            }
            listTaxBreakUp = TaxUtils.sortTaxDetails(listTaxBreakUp);

            if (listTaxBreakUp != null && (taxSelectedFormBean.getTaxMode().equals("O") || taxSelectedFormBean.getTaxMode().equals("L")
                    || taxSelectedFormBean.getTaxMode().equals("S"))) {
                listTaxBreakUp.get(listTaxBreakUp.size() - 1).setTAX_UPTO(null);
            }

            if (convdobj != null) {
                long sumOfPrvAmount = convdobj.getExcessAmt();
                for (int i = 0; i < listTaxBreakUp.size(); i++) {
                    if (listTaxBreakUp.get(i).getPUR_CD() == TableConstants.TM_ROAD_TAX) {
                        if ((listTaxBreakUp.get(i).getAMOUNT() + listTaxBreakUp.get(i).getSURCHARGE()) >= sumOfPrvAmount) {
                            listTaxBreakUp.get(i).setPRV_ADJ(-1 * sumOfPrvAmount);
                            sumOfPrvAmount = sumOfPrvAmount - sumOfPrvAmount;
                        } else if ((listTaxBreakUp.get(i).getAMOUNT() + listTaxBreakUp.get(i).getSURCHARGE()) < sumOfPrvAmount) {
                            listTaxBreakUp.get(i).setPRV_ADJ(-1 * Math.round((listTaxBreakUp.get(i).getAMOUNT() + listTaxBreakUp.get(i).getSURCHARGE())));
                            sumOfPrvAmount = sumOfPrvAmount - Math.round((listTaxBreakUp.get(i).getAMOUNT() + listTaxBreakUp.get(i).getSURCHARGE()));
                        }
                    }

                }
                if (Util.getUserStateCode().equalsIgnoreCase("AS")) {
                    if (ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                            || ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_CD)) {
                        if (taxSelectedFormBean.getTaxMode().equals("Y10") || taxSelectedFormBean.getTaxMode().equals("Y15") || taxSelectedFormBean.getTaxMode().equals("L")
                                || taxSelectedFormBean.getTaxMode().equals("O")
                                || taxSelectedFormBean.getTaxMode().equals("S")) {
                            if (taxSelectedFormBean.getTaxMode().equals("O") || taxSelectedFormBean.getTaxMode().equals("L")
                                    || taxSelectedFormBean.getTaxMode().equals("S")) {
                                listTaxBreakUp.get(listTaxBreakUp.size() - 1).setTAX_UPTO(null);
                            } else {
                                Date dt = new NewVehicleFeebean().otherStateTaxUpto(ownerDobj.getPurchase_dt(), taxSelectedFormBean.getTaxMode(), sessionVariables.getStateCodeSelected());
                                listTaxBreakUp.get(listTaxBreakUp.size() - 1).setTAX_UPTO(DateUtils.parseDate(dt));
                            }

                        }
                    }
                }

                if (Util.getUserStateCode().equalsIgnoreCase("NL")) {
                    if (taxSelectedFormBean.getTaxMode().equals("Y15") || taxSelectedFormBean.getTaxMode().equals("Y5")) {
                        Date dt = new NewVehicleFeebean().otherStateTaxUpto(ownerDobj.getPurchase_dt(), taxSelectedFormBean.getTaxMode(), sessionVariables.getStateCodeSelected());
                        listTaxBreakUp.get(listTaxBreakUp.size() - 1).setTAX_UPTO(DateUtils.parseDate(dt));
                    }
                }

                if (Util.getUserStateCode().equalsIgnoreCase("HP")) {
                    if (taxSelectedFormBean.getTaxMode().equals("L")) {
                        listTaxBreakUp.get(listTaxBreakUp.size() - 1).setTAX_UPTO(DateUtils.parseDate(fitness_val));
                    }
                }

            }

            if (FEE_ACTION.equalsIgnoreCase("REN")) {
                if ("JH".contains(Util.getUserStateCode())) {
                    Date dt = new NewVehicleFeebean().otherStateTaxUpto(ownerDobj.getPurchase_dt(), taxSelectedFormBean.getTaxMode(), sessionVariables.getStateCodeSelected());
                    listTaxBreakUp.get(listTaxBreakUp.size() - 1).setTAX_UPTO(DateUtils.parseDate(dt));
                }
            }
            taxSelectedFormBean.setTaxDescriptionList(TaxUtils.sortTaxDetails(listTaxBreakUp));
            taxSelectedFormBean.updateTaxBean();
            updateTotalPayableAmount();

        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            if (taxSelectedFormBean != null) {
                taxSelectedFormBean.reset();
                taxSelectedFormBean.resetAll();
                updateTotalPayableAmount();
            }
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong,
                    TableConstants.SomthingWentWrong));
            if (taxSelectedFormBean != null) {
                taxSelectedFormBean.reset();
                taxSelectedFormBean.resetAll();
                updateTotalPayableAmount();
            }
            return;
        }
    }

    //By Pawan
    //Tax Calculation
    public void fillTaxBeanList(VahanTaxParameters taxParameters, List<EpayDobj> listTaxTypes) throws VahanException {

        String[][] dataTaxModes = MasterTableFiller.masterTables.VM_TAX_MODE.getData();
        TaxFormPanelBean bean = null;
        VehicleParameters vehParameters = null;
        try {
            for (EpayDobj dobj : listTaxTypes) {
                vehParameters = fillVehicleParametersFromDobj(ownerDobj, getPermitPanelBean().getPermitDobj());
                if (vehParameters.getREGN_DATE() != null) {
                    vehParameters.setREGN_DATE(JSFUtils.getDateInDD_MMM_YYYY(vehParameters.getREGN_DATE()));
                }
                taxParameters.setPUR_CD(dobj.getPurCd());
                //AvailableTax modes:
                String[] taxModes = TaxServer_Impl.getAvailalableTaxModes(ownerDobj, dobj.getPurCd(), vehParameters);
                ArrayList listTaxModes = new ArrayList();
                listTaxModes.add(new SelectItem("0", "--Select--"));
                if (taxModes != null) {

                    for (int i = 0; i < taxModes.length; i++) {
                        for (int ii = 0; ii < dataTaxModes.length; ii++) {
                            if (dataTaxModes[ii][0].trim().equals(taxModes[i].trim())) {
                                listTaxModes.add(new SelectItem(dataTaxModes[ii][0], dataTaxModes[ii][1]));
                                break;
                            }
                        }
                    }

                    bean = new TaxFormPanelBean();

                    bean.setPur_cd(dobj.getPurCd());
                    bean.setTaxPurcdDesc(dobj.getPurCdDescr());
                    bean.updateTaxBean();
                    bean.setListTaxModes(listTaxModes);
                    bean.setRenderPrvAdj(true);
                    getListTaxForm().add(bean);

                }

            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

//    public List<DOTaxDetail> callTaxService(VahanTaxParameters taxParameters) throws VahanException {
//        List<DOTaxDetail> tempTaxList = null;
//
//        VahanTaxClient taxClient = null;
//        try {
//            taxClient = new VahanTaxClient();
//            String taxServiceResponse = taxClient.getTaxDetails(taxParameters);
//            tempTaxList = taxClient.parseTaxResponse(taxServiceResponse, taxParameters.getPURCD(), taxParameters.getTAXMODE());
//
//        } catch (javax.xml.ws.WebServiceException e) {
//            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//            throw new VahanException(e.getMessage());
//        }
//
//        return tempTaxList;
//    }
    //Tax Calculation
    public void updateTotalPayableAmount() {

        setTotalAmountPayable(0);
        long finalTaxAmount = 0;
        List<Integer> listPurCd = new ArrayList<>();

        boolean feeFineExem = false;
        boolean taxPenaltyExem = false;
        boolean taxInterestExem = false;
        boolean manualFeeReceipt = false;
        for (FeeDobj dobj : feeCollectionLists) {
            if (dobj.getPurCd() == TableConstants.FEE_FINE_EXEMTION) {
                feeFineExem = true;
            }
            if (dobj.getPurCd() == TableConstants.TAX_PENALTY_EXEMTION) {
                taxPenaltyExem = true;
            }
            if (dobj.getPurCd() == TableConstants.TAX_INTEREST_EXEMTION) {
                taxInterestExem = true;
            }
            if (dobj.getPurCd() == TableConstants.VM_MAST_MANUAL_RECEIPT) {
                manualFeeReceipt = true;
            }
        }
        if (feeFineExem || taxPenaltyExem || taxInterestExem || manualFeeReceipt) {
            setTotalAmountPayable(getTotalAmountPayable() + getFeePanelBean().getTotalAmount());
        } else {
            if (getFeePanelBean().getTotalAmount() > 0) {
                setTotalAmountPayable(getTotalAmountPayable() + getFeePanelBean().getTotalAmount());
            }
        }

        if (renderTaxPanel) {
            for (TaxFormPanelBean bean : listTaxForm) {
                finalTaxAmount = finalTaxAmount + bean.getFinalTaxAmount();
                if (bean.getFinalTaxAmount() > 0) {
                    listPurCd.add(bean.getPur_cd());
                }
            }
        }

        if (EpayImpl.getServiceChargeType() != null) {
            for (FeeDobj fee : getFeeCollectionLists()) {
                listPurCd.add(fee.getPurCd());
            }

            if (getSmartCardFee() != null) {
                listPurCd.add(TableConstants.VM_TRANSACTION_MAST_SMART_CARD_FEE);
            }

            Long userCharges = EpayImpl.getUserChargesFee(ownerDobj, listPurCd, null);
            setTotalUserChrg(userCharges);
            setRenderUserChargesAmountPanel(true);

        } else {
            setRenderUserChargesAmountPanel(false);
        }

        if (getSmartCardFee() != null) {
            setTotalAmountPayable(getTotalAmountPayable() + finalTaxAmount + getTotalUserChrg() + getSmartCardFee());
        } else {
            setTotalAmountPayable(getTotalAmountPayable() + finalTaxAmount + getTotalUserChrg());
        }
    }

    public void updateTotalAmtOnRemoveFee() {
        setTotal_payable_Amount("");
        //setTotal_payable_Amount(feePanelBean.getTotalAmount() + "");
        setTotal_payable_Amount(getTotalAmountPayable() + "");
    }

    public String saveFeeDetails() throws VahanException {
        Exception e = null;
        Fee_Pay_Dobj feePayDobj = new Fee_Pay_Dobj();
        feePayDobj.setCollectedFeeList(getFeeCollectionLists());
        long checkTotalAmount = 0L;
        boolean isFeeFineExemptionAllow = false;

        // For Tax Collection Information
        if (renderTaxPanel) {
            if (listTaxForm.size() > 0) {
                List<Tax_Pay_Dobj> listTaxDobj = new ArrayList<>();

                for (TaxFormPanelBean bean : listTaxForm) {
                    if (!bean.getTaxMode().equals("0")) {
                        Tax_Pay_Dobj taxDobj = new Tax_Pay_Dobj();

                        taxDobj.setFinalTaxAmount(bean.getFinalTaxAmount());
                        taxDobj.setTotalPaybaleTax(bean.getTotalPaybaleTax());
                        taxDobj.setTotalPaybalePenalty(bean.getTotalPaybalePenalty());
                        taxDobj.setTotalPaybaleSurcharge(bean.getTotalPaybaleSurcharge());
                        taxDobj.setTotalPaybaleRebate(bean.getTotalPaybaleRebate());
                        taxDobj.setTotalPaybaleInterest(bean.getTotalPaybaleInterest());
                        taxDobj.setFinalTaxFrom(bean.getFinalTaxFrom());
                        taxDobj.setFinalTaxUpto(bean.getFinalTaxUpto());
                        taxDobj.setTaxMode(bean.getTaxMode());
                        taxDobj.setPur_cd(bean.getPur_cd());
                        taxDobj.setRegnNo(ownerDobj.getRegn_no());
                        taxDobj.setPaymentMode(getPaymentBean().getPayment_mode());
                        taxDobj.setTaxBreakDetails(bean.getTaxDescriptionList());
                        if (ownerDobj != null) {
                            taxDobj.setRegnNo(getOwnerDobj().getRegn_no());
                            taxDobj.setApplNo(getAppl_no());
                        }

                        taxDobj.setDeal_cd(Util.getEmpCode());
                        taxDobj.setOff_cd(Util.getSelectedSeat().getOff_cd());

                        taxDobj.setOp_dt(new java.util.Date());
                        taxDobj.setRcptDate(new java.util.Date());
                        taxDobj.setApplNo(appl_no);
                        taxDobj.setNoOfAdvUnits(bean.getNoOfUnits());
                        taxDobj.setTotalPaybaleTax1(bean.getTotalTax1());
                        taxDobj.setTotalPaybaleTax2(bean.getTotalTax2());
                        taxDobj.setPreviousAdj(bean.getTotalPayablePrvAdj());
                        listTaxDobj.add(taxDobj);

                    }

                }
                feePayDobj.setListTaxDobj(listTaxDobj);
                feePayDobj.setPermitDobj(getPermitPanelBean().getPermitDobj());
            }

        }

        feePayDobj.setOp_dt(new java.util.Date());
        if (ownerDobj == null) {
            return "";
        }
        feePayDobj.setRegnNo(ownerDobj.getRegn_no());
        feePayDobj.setApplNo(getAppl_no());
        feePayDobj.setRcptDt(new java.util.Date());
        feePayDobj.setPaymentMode(getPaymentBean().getPayment_mode());
        feePayDobj.setState_cd(ownerDobj.getState_cd());
        try {

            FeeDraftDobj feeDraftDobj = null;

            if (!Utility.isNullOrBlank(getPaymentBean().getPayment_mode()) && (!getPaymentBean().getPayment_mode().equals("-1"))
                    && (!getPaymentBean().getPayment_mode().equals("C"))) {
                feeDraftDobj = new FeeDraftDobj();
                String pay_mode = getPaymentBean().getPayment_mode();
                feeDraftDobj.setAppl_no(getAppl_no());
                feeDraftDobj.setFlag("A");
                feeDraftDobj.setCollected_by(Util.getEmpCode());
                feeDraftDobj.setState_cd(getOwnerDobj().getState_cd());
                feeDraftDobj.setOff_cd(String.valueOf(Util.getUserOffCode()));
                feeDraftDobj.setDraftPaymentList(getPaymentBean().getPaymentlist());

            }
            feePayDobj.setOwnerDobj(ownerDobj);
            PassengerPermitDetailImpl passengerPermitDetailImpl = new PassengerPermitDetailImpl();
            PassengerPermitDetailDobj permitDobj = passengerPermitDetailImpl.set_vt_permit_regnNo_to_dobj(ownerDobj.getRegn_no(), "");
            VehicleParameters vehParameters = fillVehicleParametersFromDobj(ownerDobj, permitDobj);
            List<FeeDobj> feeDobjList = getFeeCollectionLists();
            if (feeDobjList != null) {
                for (FeeDobj feedobj : feeDobjList) {
                    if (feedobj.getPurCd() == 99 || feedobj.getPurCd() == 80) {
                        continue;
                    }
                    Long totalAmount = feedobj.getTotalAmount();
                    int pur_cd = feedobj.getPurCd();
                    vehParameters.setPUR_CD(pur_cd);
                    if (!tmConfDobj.isIsFancyFeeZero() && pur_cd != TableConstants.SWAPPING_REGN_PUR_CD) {
                        if ((pur_cd != TableConstants.VM_TRANSACTION_MAST_NOC && !isCondition(replaceTagValues(tmConfDobj.getFee_amt_zero(), vehParameters), "saveFeeDetails-1")
                                && (totalAmount == null || totalAmount == 0L)) || pur_cd <= 0) {
                            throw new VahanException("Record cannot be saved with zero value for " + ServerUtil.getTaxHead(feedobj.getPurCd()));
                        }
                    }
                }
            }

            List<Tax_Pay_Dobj> taxDobjList = feePayDobj.getListTaxDobj();
            if (taxDobjList != null) {
                for (Tax_Pay_Dobj taxdobj : taxDobjList) {
                    int pur_cd = taxdobj.getPur_cd();
                    vehParameters.setPUR_CD(pur_cd);
                    Long totalAmount = taxdobj.getFinalTaxAmount();
                    if (totalAmount <= 0) {
                        if (!isCondition(replaceTagValues(tmConfDobj.getFee_amt_zero(), vehParameters), "saveFeeDetails-2")
                                && taxdobj.getTotalPaybaleTax() <= 0 || taxdobj.getFinalTaxAmount() < 0) {
                            throw new VahanException("Record cannot be saved with zero value for " + ServerUtil.getTaxHead(taxdobj.getPur_cd()));
                        }
                    }

                }
            }

            List<FeeDobj> feeCollectionCloneLists = new ArrayList<>(feeCollectionLists);
            if (isRenderUserChargesAmountPanel()) {
                FeeDobj userCharge = new FeeDobj();
                userCharge.setFeeAmount((long) getTotalUserChrg());
                userCharge.setFineAmount((long) 0l);
                userCharge.setPurCd(99);
                getFeePanelBean().getFeeCollectionList().add(userCharge);
                feeCollectionCloneLists.add(userCharge);
            }
            if (isRenderSmartCardFeePanel()) {
                FeeDobj smtCrdCharge = new FeeDobj();
                smtCrdCharge.setFeeAmount((long) getSmartCardFee());
                smtCrdCharge.setFineAmount((long) 0l);
                smtCrdCharge.setPurCd(80);
                getFeePanelBean().getFeeCollectionList().add(smtCrdCharge);
                feeCollectionCloneLists.add(smtCrdCharge);
            }
            if (isTaxInstallment()) {
                feePayDobj.setTaxInstallment(taxInstallment);
                feePayDobj.setTaxInstallMode(convdobj.getNewTaxMode());
            }
            feePayDobj.setCollectedFeeList(feeCollectionCloneLists);
            feePayDobj.setRemarks(reasonForFitness);

            if (!"".equals(fitnessInspectionFeeMessage)) {
                feePayDobj.setRemarks(feePayDobj.getRemarks() + ":" + fitnessInspectionFeeMessage);
            }
            if (this.paymentTypeBtn.equals("RtoPayment")) {
                String[] applNo = feeImpl.saveFeeDetailsInstrument(feePayDobj, feeDraftDobj);//Details(feePayDobj);

                if (applNo == null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Fee Can't be Submitted because One of the Transaction is Pending on Data Entry Level.", "Fee Can't be Submitted because One of the Transaction is Pending on Data Entry Level."));
                    return null;
                }

                generatedRcpt = applNo[1];

                if (!Utility.isNullOrBlank(generatedRcpt)) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful Transaction!", "Receipt Number: " + applNo[1]));
                    getFeePanelBean().setReadOnlyFineAmount(true);
                    setRegnno(getOwnerDobj().getRegn_no());
                    reset();
                    setTotalUserChrg(0l);
                    setSmartCardFee(0l);
                    setTotalAmountPayable(0);
                    Util.getSession().removeAttribute("seat_map");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().ajax().update("form_registered_vehicle_fee:pg_button");
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rcptno", generatedRcpt);
                    return "PrintCashReceiptReport";
                }
                rcpt_bean.reset();
            } else if (this.paymentTypeBtn.equals("OnlinePayment")) {
                List<OwnerDetailsDobj> ownerlist = new OwnerImpl().getOwnerDetailsList(ownerDobj.getRegn_no(), null);
                if (ownerlist != null && !ownerlist.isEmpty() && ownerlist.size() > 1) {
                    throw new VahanException("Duplicate vehicle details are available against the Registration no " + ownerlist.get(0).getRegn_no() + ". Please do de-duplicate first then initiate for online payment.");
                }
                if (feePayDobj.getCollectedFeeList() != null && !feePayDobj.getCollectedFeeList().isEmpty()) {
                    for (FeeDobj dobj : feePayDobj.getCollectedFeeList()) {
                        Long totalAmount = dobj.getTotalAmount();
                        checkTotalAmount = checkTotalAmount + totalAmount;
                        if (dobj.getPurCd() == TableConstants.VM_MAST_MANUAL_RECEIPT) {
                            throw new VahanException("In this Application, Manual receipt attach, you can not make Online Payment.");
                        }
                        if (dobj.getPurCd() == TableConstants.FEE_FINE_EXEMTION || dobj.getPurCd() == TableConstants.TAX_PENALTY_EXEMTION
                                || dobj.getPurCd() == TableConstants.TAX_INTEREST_EXEMTION) {
                            isFeeFineExemptionAllow = true;
                        }
                    }
                }

                List<Tax_Pay_Dobj> taxList = feePayDobj.getListTaxDobj();
                if (taxList != null && !taxList.isEmpty()) {
                    for (Tax_Pay_Dobj taxdobj : taxList) {
                        Long totalAmount = taxdobj.getFinalTaxAmount();
                        checkTotalAmount = checkTotalAmount + totalAmount;
                    }
                }
                String userPwd = feeImpl.saveOnlinePaymentData(feePayDobj, checkTotalAmount, appl_no, ownerDobj.getOwner_identity().getMobile_no(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), sessionVariables.getEmpCodeLoggedIn(), null, null, null, false, isFeeFineExemptionAllow);
                if (userPwd != null) {
                    String userInfo = "Online Payment Credentials User ID : " + appl_no + " & Password :  " + userPwd;
                    setOnlineUserCredentialmsg(userInfo);
                    PrimeFaces.current().ajax().update("form_registered_vehicle_fee:onlinePaymentdialog");
                    PrimeFaces.current().executeScript("PF('blockVar').show();");
                    PrimeFaces.current().executeScript("PF('onlinePaymentvar').show();");
                } else {
                    throw new VahanException("Error in Saving Details !!");
                }
            }
        } catch (VahanException ve) {
            e = ve;
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
        }
        if (e != null) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }
        return "";
    }

    public String getChassisNo(String Statecd, int offcd, String rcptNo) {
        return feeImpl.getChassisNo(Statecd, offcd, rcptNo);
    }

    public void validateForm(String payType) {
        this.setPaymentTypeBtn(payType);
        List<FeeDobj> feeDobjList = null;
        boolean allGood = false;
        boolean attachManualRcpt = false;
        try {
            if (tmConfDobj == null) {
                tmConfDobj = Util.getTmConfiguration();
            }
            if (getTotalAmountPayable() == 0) {
                feeDobjList = getFeeCollectionLists();
                PassengerPermitDetailImpl passengerPermitDetailImpl = new PassengerPermitDetailImpl();
                PassengerPermitDetailDobj permitDobj = passengerPermitDetailImpl.set_vt_permit_regnNo_to_dobj(ownerDobj.getRegn_no(), "");
                VehicleParameters vehParameters = fillVehicleParametersFromDobj(ownerDobj, permitDobj);
                if (feeDobjList != null && !feeDobjList.isEmpty() && feeDobjList.get(0).getPurCd() != null) {
                    vehParameters.setPUR_CD(feeDobjList.get(0).getPurCd());
                }
                for (FeeDobj feeDobj : feeDobjList) {
                    if (feeDobj.getPurCd() == TableConstants.VM_MAST_MANUAL_RECEIPT) {
                        attachManualRcpt = true;
                        break;
                    }
                }
                if (!isCondition(replaceTagValues(tmConfDobj.getFee_amt_zero(), vehParameters), "validateForm") && !attachManualRcpt) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Fee can't be submitted with zero payable amount"));
                    return;
                }
            }
            if ((getFeePanelBean().getPayableFeeCollectionList().isEmpty())) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please select Fee"));
                allGood = true;
                return;
            }
            // Checking for Payment Mode
            if (Utility.isNullOrBlank(getPaymentBean().getPayment_mode())
                    || "-1".equalsIgnoreCase(getPaymentBean().getPayment_mode())) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please select payment mode"));
                allGood = true;
                return;
            }
            try {
                ServerUtil.validateNoCashPayment(paymentTypeBtn, getPaymentBean().getPayment_mode());
            } catch (VahanException ve) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, ve.getMessage(), ve.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }

            if (payType.equals("OnlinePayment") && !getPaymentBean().getPayment_mode().equals("C")) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please select cash payment mode for online payment.", "Please select cash payment mode for online payment.");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }

            //Validate Fee Fine Exemption Amount
            feeDobjList = getFeeCollectionLists();
            if (feeDobjList != null && !feeDobjList.isEmpty()) {
                long totalFine = 0l;
                for (FeeDobj feedobj : feeDobjList) {//add
                    if (feedobj.getPurCd() != TableConstants.FEE_FINE_EXEMTION && feedobj.getPurCd() != TableConstants.TAX_PENALTY_EXEMTION && feedobj.getPurCd() != TableConstants.TAX_INTEREST_EXEMTION) {
                        totalFine += feedobj.getFineAmount();
                    }
                }
                for (FeeDobj feedobj : feeDobjList) {
                    if (feedobj.getPurCd() == TableConstants.FEE_FINE_EXEMTION && totalFine < -feedobj.getFineAmount()) {
                        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid Exemption Order Details.", "Invalid Exemption Order Details. Please make sure amount is valid.");
                        FacesContext.getCurrentInstance().addMessage(null, message);
                        return;
                    }
                }
            }

            boolean flg = false;
            for (TaxFormPanelBean bean : listTaxForm) {
                if (!bean.getTaxMode().equals("0")) {
                    flg = true;
                }

                //Validate Tax Penalty Exemption Amount            
                feeDobjList = getFeeCollectionLists();
                if (feeDobjList != null && !feeDobjList.isEmpty()) {
                    long totalPenaltyAmnt = 0l;
                    for (TaxFormPanelBean bean_amt : listTaxForm) {
                        totalPenaltyAmnt = totalPenaltyAmnt + bean_amt.getTotalPaybalePenalty();
                    }
                    for (FeeDobj feedobj : feeDobjList) {
//                    if (feedobj.getPurCd() == TableConstants.TAX_PENALTY_EXEMTION && bean.getTotalPaybalePenalty() < -feedobj.getFineAmount()) {
                        if (feedobj.getPurCd() == TableConstants.TAX_PENALTY_EXEMTION && totalPenaltyAmnt < -feedobj.getFineAmount()) {
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid Exemption Order Details.", "Invalid Exemption Order Details. Please make sure amount is valid.");
                            FacesContext.getCurrentInstance().addMessage(null, message);
                            return;
                        }
                    }
                }

                //Validate Tax Interest Exemption Amount            
                feeDobjList = getFeeCollectionLists();
                if (feeDobjList != null && !feeDobjList.isEmpty()) {
                    long totalInterestAmnt = 0l;

                    for (TaxFormPanelBean bean_amt : listTaxForm) {
                        totalInterestAmnt = totalInterestAmnt + bean_amt.getTotalPaybaleInterest();
                    }
                    for (FeeDobj feedobj : feeDobjList) {

                        if (feedobj.getPurCd() == TableConstants.TAX_INTEREST_EXEMTION && totalInterestAmnt < -feedobj.getFineAmount()) {
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid Exemption Order Details.", "Invalid Exemption Order Details. Please make sure amount is valid.");
                            FacesContext.getCurrentInstance().addMessage(null, message);
                            return;
                        }
                    }
                }
            }

            if (listTaxForm.size() > 0 && !flg && !taxExemption && !taxInstallment) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please select Tax Mode"));
                return;
            }
            // Cash or some thing else
            // hardcode as cash = 4
            if (!getPaymentBean().getPayment_mode().equals("C")) {
                long totalBal = 0;
                for (PaymentCollectionDobj payDobj : getPaymentBean().getPaymentlist()) {
                    if (!(allGood = payDobj.validateDobj())) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", payDobj.getValidationMessage()));
                        allGood = true;
//                    PrimeFaces.current().ajax().update("form_registered_vehicle_fee:PaymentPanel:payment_panel");
                        break;
                    } else {
                        allGood = false;
                    }
                    totalBal = totalBal + Long.parseLong(payDobj.getAmount());
                }
                if (!allGood) {
                    if (totalBal > getTotalAmountPayable()) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Excess Total Instrument Amount (Rs." + (totalBal - getTotalAmountPayable()) + "/- must be adjusted in any transaction head amount."));
                        return;
                    } else if ("DL,SK".contains(Util.getUserStateCode())) {
                        if (totalBal != getTotalAmountPayable()) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Paying Total Instrument Amount Must be equal to Total Payable Amount"));
                            return;
                        }
                    }
                }
            } else {
                if ("SK".contains(Util.getUserStateCode())) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please select Mixed Mode for payment. Because Cash payment mode in not allowed in Sikkim"));
                    allGood = true;
                    return;
                }
            }

            for (PaymentCollectionDobj dobj : getPaymentBean().getPaymentlist()) {
                if (dobj.getInstrument() != null && !dobj.getInstrument().isEmpty()) {
                    if ((Util.getTmConfiguration() != null && Util.getTmConfiguration().getTmConfigPayVerifyDobj() != null) && ((dobj.getInstrument().equals(TableConstants.EGRASS_INSTRUMENT_CODE) && Util.getTmConfiguration().getTmConfigPayVerifyDobj().iseGrassVerify()) || (dobj.getInstrument().equals(TableConstants.INSTRUMENT_CODE_BANK_RECEIPT) && Util.getTmConfiguration().getTmConfigPayVerifyDobj().isBankReceiptVerify()))) {
                        if (!dobj.iseGrassVerified()) {
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Either you have not verify the EGRASS payment or verification status is unsuccessful",
                                    "Either you have not verify the payment or verification status is unsuccessful");
                            FacesContext.getCurrentInstance().addMessage(null, message);
                            return;
                        }
                    }
                }
            }

            calculateBalanceAmount(getTotalAmountPayable());
            if (!allGood) {
                //RequestContext rc = RequestContext.getCurrentInstance();
                PrimeFaces.current().ajax().update("form_registered_vehicle_fee:popup");
                PrimeFaces.current().executeScript("PF('confDlgFee').show()");
            }
        } catch (VahanException vex) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", vex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", TableConstants.SomthingWentWrong));
        }
    }

    public void calculateBalanceAmount(long totalAmount) {
        long totalBal = 0;

        for (PaymentCollectionDobj dobj : getPaymentBean().getPaymentlist()) {
            if (dobj.getAmount() != null) {
                totalBal = totalBal + Long.parseLong(dobj.getAmount());
            }
        }
        if ((totalAmount - totalBal) < 0) {
            getPaymentBean().setBalanceAmount(0);
        } else {
            getPaymentBean().setBalanceAmount((totalAmount) - totalBal);
        }
        getPaymentBean().setAmountInCash(totalBal);
    }

    public void reset() {
        setVeh_no("");
        ownerDobj = null;
        getFeePanelBean().reset();
        getPaymentBean().reset();
        getTaxFormBean().resetAll();
        setChasi_no("");
        setFitness_val(null);
        setRegn_date(null);
        setOwner_name("");
        setVeh_class("-1");
        setIsdisable(false);
        setBtn_save(false);
        getFeePanelBean().getFeeCollectionList().clear();
    }

    public String revertBackForRectificationRegVeh() {
        String retString = null;
        String errorMessage = null;
        Exception e = null;

        if (selectedPurCdForRevertBack == null || selectedPurCdForRevertBack.isEmpty()) {
            JSFUtils.showMessagesInDialog("Alert!", "You have not Selected any Option for Revert Back for Rectification", FacesMessage.SEVERITY_WARN);
            return retString;
        }

        try {
            ApplicationInwardImpl impl = new ApplicationInwardImpl();
            impl.regVehRevertBackfileFlow(appl_no, sessionVariables.getStateCodeSelected(), selectedPurCdForRevertBack, ownerDobj);
            retString = "home";
        } catch (VahanException vex) {
            e = vex;
        } catch (Exception ex) {
            e = ex;
        }

        if (e != null && e instanceof VahanException) {
            errorMessage = "Can not Revert Back for Rectification due to " + e.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, errorMessage));
            return retString;
        }

        if (e != null) {
            errorMessage = "Exception Occured - Error in Processing of Revert Back for Rectification, Please Contact to System Administrator";
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, errorMessage));
            return retString;
        }

        return retString;
    }

    public void getBreakUpDetailsByPurpose(TaxFormPanelBean bean) {

        if (taxDescriptionList != null) {
            taxDescriptionList.clear();
        }
        taxDescriptionList = bean.getTaxDescriptionList();
        // RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().ajax().update("form_registered_vehicle_fee:sub_view_tax_dtls:opBreakupDetails");
        PrimeFaces.current().executeScript("PF('dia_tax_breakup').show()");
    }

    public String getCancelPayment() {
        OnlinePaymentImpl onImpl = new OnlinePaymentImpl();
        boolean flag = false;
        String password = "";
        long user_cd = 0;
        try {
            Object[] obj = feeImpl.getUserIDAndPassword(appl_no);
            if (obj != null && obj.length > 0) {
                password = (String) obj[0];
                user_cd = (long) obj[1];
                if (CommonUtils.isNullOrBlank(onImpl.getTransactionNumber(appl_no))) {
                    flag = onImpl.getPaymentRevertBack(user_cd + "", appl_no, null);
                    if (flag) {
                        setBtn_save(true);
                        setRenderOnlinePayBtn(true);
                        setRenderCancelPayment(false);
                        setRenderUserAndPasswored(false);
                    }
                } else {
                    throw new VahanException("Payment has been initiated, you can not Cancel Online Payment");
                }
            }
        } catch (VahanException e) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", e.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return "";
    }

    public void isViewIsRendered() {
        //used for calculating tax automatically based on modes
        autoRunTaxListener = false;
    }

    public String getVeh_no() {
        return veh_no;
    }

    public void setVeh_no(String veh_no) {
        this.veh_no = veh_no;
    }

    public String getChasi_no() {
        return chasi_no;
    }

    public void setChasi_no(String chasi_no) {
        this.chasi_no = chasi_no;
    }

    public Date getRegn_date() {
        return regn_date;
    }

    public void setRegn_date(Date regn_date) {
        this.regn_date = regn_date;
    }

    public Date getFitness_val() {
        return fitness_val;
    }

    public void setFitness_val(Date fitness_val) {
        this.fitness_val = fitness_val;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getVeh_class() {
        return veh_class;
    }

    public void setVeh_class(String veh_class) {
        this.veh_class = veh_class;
    }

    public boolean isIsHypothecated() {
        return isHypothecated;
    }

    public void setIsHypothecated(boolean isHypothecated) {
        this.isHypothecated = isHypothecated;
    }

    /**
     * @return the vhClass_list
     */
    public List getVhClass_list() {
        return vhClass_list;
    }

    /**
     * @param vhClass_list the vhClass_list to set
     */
    public void setVhClass_list(List vhClass_list) {
        this.vhClass_list = vhClass_list;
    }

    /**
     * @return the payment_list
     */
    public List<PaymentCollectionDobj> getPayment_list() {
        return payment_list;
    }

    /**
     * @param payment_list the payment_list to set
     */
    public void setPayment_list(List<PaymentCollectionDobj> payment_list) {
        this.payment_list = payment_list;
    }

    public OwnerImpl getOwnerImpl() {
        return ownerImpl;
    }

    public void setOwnerImpl(OwnerImpl ownerImpl) {
        this.ownerImpl = ownerImpl;
    }

    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    public PaymentCollectionBean getPaymentBean() {
        return paymentBean;
    }

    public void setPaymentBean(PaymentCollectionBean paymentBean) {
        this.paymentBean = paymentBean;
    }

    public FormFeePanelBean getFeePanelBean() {
        return feePanelBean;
    }

    public void setFeePanelBean(FormFeePanelBean feePanelBean) {
        this.feePanelBean = feePanelBean;
    }

    /**
     * @return the total_payable_Amount
     */
    public String getTotal_payable_Amount() {
        return total_payable_Amount;
    }

    /**
     * @param total_payable_Amount the total_payable_Amount to set
     */
    public void setTotal_payable_Amount(String total_payable_Amount) {
        this.total_payable_Amount = total_payable_Amount;
    }

    /**
     * @return the rcpt_bean
     */
    public ReceiptMasterBean getRcpt_bean() {
        return rcpt_bean;
    }

    /**
     * @param rcpt_bean the rcpt_bean to set
     */
    public void setRcpt_bean(ReceiptMasterBean rcpt_bean) {
        this.rcpt_bean = rcpt_bean;
    }

    public boolean isIsdisable() {
        return isdisable;
    }

    public void setIsdisable(boolean isdisable) {
        this.isdisable = isdisable;
    }

    public PaymentCollectionDobj getPaymentDobj() {
        return paymentDobj;
    }

    public void setPaymentDobj(PaymentCollectionDobj paymentDobj) {
        this.paymentDobj = paymentDobj;
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

    public boolean isRenderTaxPanel() {
        return renderTaxPanel;
    }

    public void setRenderTaxPanel(boolean renderTaxPanel) {
        this.renderTaxPanel = renderTaxPanel;
    }

    /**
     * @return the listTaxForm
     */
    public List<TaxFormPanelBean> getListTaxForm() {
        return listTaxForm;
    }

    /**
     * @param listTaxForm the listTaxForm to set
     */
    public void setListTaxForm(List<TaxFormPanelBean> listTaxForm) {
        this.listTaxForm = listTaxForm;
    }

    /**
     * @return the totalAmountPayable
     */
    public long getTotalAmountPayable() {
        return totalAmountPayable;
    }

    /**
     * @param totalAmountPayable the totalAmountPayable to set
     */
    public void setTotalAmountPayable(long totalAmountPayable) {
        this.totalAmountPayable = totalAmountPayable;
    }

    public ConvDobj getConvdobj() {
        return convdobj;
    }

    public void setConvdobj(ConvDobj convdobj) {
        this.convdobj = convdobj;
    }

    /**
     * @return the chasino
     */
    public String getChasino() {
        return chasino;
    }

    /**
     * @param chasino the chasino to set
     */
    public void setChasino(String chasino) {
        this.chasino = chasino;
    }

    /**
     * @return the btn_save
     */
    public boolean isBtn_save() {
        return btn_save;
    }

    /**
     * @param btn_save the btn_save to set
     */
    public void setBtn_save(boolean btn_save) {
        this.btn_save = btn_save;
    }

    /**
     * @return the feeCollectionList
     */
    public List<FeeDobj> getFeeCollectionLists() {
        return feeCollectionLists;
    }

    /**
     * @param feeCollectionList the feeCollectionList to set
     */
    public void setFeeCollectionLists(ArrayList<FeeDobj> feeCollectionLists) {
        this.feeCollectionLists = feeCollectionLists;
    }

    /**
     * @return the permitPanelBean
     */
    public PermitPanelBean getPermitPanelBean() {
        return permitPanelBean;
    }

    /**
     * @param permitPanelBean the permitPanelBean to set
     */
    public void setPermitPanelBean(PermitPanelBean permitPanelBean) {
        this.permitPanelBean = permitPanelBean;
    }

    /**
     * @return the renderPermitPanel
     */
    public boolean isRenderPermitPanel() {
        return renderPermitPanel;
    }

    /**
     * @param renderPermitPanel the renderPermitPanel to set
     */
    public void setRenderPermitPanel(boolean renderPermitPanel) {
        this.renderPermitPanel = renderPermitPanel;
    }

    /**
     * @return the regnno
     */
    public String getRegnno() {
        return regnno;
    }

    /**
     * @param regnno the regnno to set
     */
    public void setRegnno(String regnno) {
        this.regnno = regnno;
    }

    /**
     * @return the userChrg
     */
    public Long getUserChrg() {
        return userChrg;
    }

    /**
     * @param userChrg the userChrg to set
     */
    public void setUserChrg(Long userChrg) {
        this.userChrg = userChrg;
    }

    /**
     * @return the renderUserChargesAmountPanel
     */
    public boolean isRenderUserChargesAmountPanel() {
        return renderUserChargesAmountPanel;
    }

    /**
     * @param renderUserChargesAmountPanel the renderUserChargesAmountPanel to
     * set
     */
    public void setRenderUserChargesAmountPanel(boolean renderUserChargesAmountPanel) {
        this.renderUserChargesAmountPanel = renderUserChargesAmountPanel;
    }

    /**
     * @return the totalUserChrg
     */
    public Long getTotalUserChrg() {
        return totalUserChrg;
    }

    /**
     * @param totalUserChrg the totalUserChrg to set
     */
    public void setTotalUserChrg(Long totalUserChrg) {
        this.totalUserChrg = totalUserChrg;
    }

    /**
     * @return the listTransWise
     */
    public List<FeeDobj> getListTransWise() {
        return listTransWise;
    }

    /**
     * @param listTransWise the listTransWise to set
     */
    public void setListTransWise(List<FeeDobj> listTransWise) {
        this.listTransWise = listTransWise;
    }

    /**
     * @return the renderSmartCardFeePanel
     */
    public boolean isRenderSmartCardFeePanel() {
        return renderSmartCardFeePanel;
    }

    /**
     * @param renderSmartCardFeePanel the renderSmartCardFeePanel to set
     */
    public void setRenderSmartCardFeePanel(boolean renderSmartCardFeePanel) {
        this.renderSmartCardFeePanel = renderSmartCardFeePanel;
    }

    /**
     * @return the smartCardFee
     */
    public Long getSmartCardFee() {
        return smartCardFee;
    }

    /**
     * @param smartCardFee the smartCardFee to set
     */
    public void setSmartCardFee(Long smartCardFee) {
        this.smartCardFee = smartCardFee;
    }

    /**
     * @return the smartCardChrg
     */
    public Long getSmartCardChrg() {
        return smartCardChrg;
    }

    /**
     * @param smartCardChrg the smartCardChrg to set
     */
    public void setSmartCardChrg(Long smartCardChrg) {
        this.smartCardChrg = smartCardChrg;
    }

    /**
     * @return the rcptNoPopup
     */
    public String getRcptNoPopup() {
        return rcptNoPopup;
    }

    /**
     * @param rcptNoPopup the rcptNoPopup to set
     */
    public void setRcptNoPopup(String rcptNoPopup) {
        this.rcptNoPopup = rcptNoPopup;
    }

    /**
     * @return the showInRto
     */
    public boolean isShowInRto() {
        return showInRto;
    }

    /**
     * @param showInRto the showInRto to set
     */
    public void setShowInRto(boolean showInRto) {
        this.showInRto = showInRto;
    }

    /**
     * @return the altDobj
     */
    public AltDobj getAltDobj() {
        return altDobj;
    }

    /**
     * @param altDobj the altDobj to set
     */
    public void setAltDobj(AltDobj altDobj) {
        this.altDobj = altDobj;
    }

    /**
     * @return the renderFeePanelLabel
     */
    public boolean isRenderFeePanelLabel() {
        return renderFeePanelLabel;
    }

    /**
     * @param renderFeePanelLabel the renderFeePanelLabel to set
     */
    public void setRenderFeePanelLabel(boolean renderFeePanelLabel) {
        this.renderFeePanelLabel = renderFeePanelLabel;
    }

    /**
     * @return the renderTaxExemption
     */
    public boolean isRenderTaxExemption() {
        return renderTaxExemption;
    }

    /**
     * @param renderTaxExemption the renderTaxExemption to set
     */
    public void setRenderTaxExemption(boolean renderTaxExemption) {
        this.renderTaxExemption = renderTaxExemption;
    }

    /**
     * @return the taxExemption
     */
    public boolean isTaxExemption() {
        return taxExemption;
    }

    /**
     * @param taxExemption the taxExemption to set
     */
    public void setTaxExemption(boolean taxExemption) {
        this.taxExemption = taxExemption;
    }

    /**
     * @return the tmConfDobj
     */
    public TmConfigurationDobj getTmConfDobj() {
        return tmConfDobj;
    }

    /**
     * @param tmConfDobj the tmConfDobj to set
     */
    public void setTmConfDobj(TmConfigurationDobj tmConfDobj) {
        this.tmConfDobj = tmConfDobj;
    }

    /**
     * @return the regnNonodataPopup
     */
    public String getRegnNonodataPopup() {
        return regnNonodataPopup;
    }

    /**
     * @param regnNonodataPopup the regnNonodataPopup to set
     */
    public void setRegnNonodataPopup(String regnNonodataPopup) {
        this.regnNonodataPopup = regnNonodataPopup;
    }

    /**
     * @return the purCodeList
     */
    public Map<String, Integer> getPurCodeList() {
        return purCodeList;
    }

    /**
     * @param purCodeList the purCodeList to set
     */
    public void setPurCodeList(Map<String, Integer> purCodeList) {
        this.purCodeList = purCodeList;
    }

    /**
     * @return the selectedPurCdForRevertBack
     */
    public List<String> getSelectedPurCdForRevertBack() {
        return selectedPurCdForRevertBack;
    }

    /**
     * @param selectedPurCdForRevertBack the selectedPurCdForRevertBack to set
     */
    public void setSelectedPurCdForRevertBack(List<String> selectedPurCdForRevertBack) {
        this.selectedPurCdForRevertBack = selectedPurCdForRevertBack;
    }

    /**
     * @return the autoRunTaxListener
     */
    public boolean isAutoRunTaxListener() {
        return autoRunTaxListener;
    }

    /**
     * @param autoRunTaxListener the autoRunTaxListener to set
     */
    public void setAutoRunTaxListener(boolean autoRunTaxListener) {
        this.autoRunTaxListener = autoRunTaxListener;
    }

    public List<DOTaxDetail> getTaxDescriptionList() {
        return taxDescriptionList;
    }

    public void setTaxDescriptionList(List<DOTaxDetail> taxDescriptionList) {
        this.taxDescriptionList = taxDescriptionList;
    }

    public boolean isTaxInstallment() {
        return taxInstallment;
    }

    public void setTaxInstallment(boolean taxInstallment) {
        this.taxInstallment = taxInstallment;
    }

    public boolean isRenderTaxInstallment() {
        return renderTaxInstallment;
    }

    public void setRenderTaxInstallment(boolean renderTaxInstallment) {
        this.renderTaxInstallment = renderTaxInstallment;
    }

    /**
     * @return the taxInstallMode
     */
    public String getTaxInstallMode() {
        return taxInstallMode;
    }

    /**
     * @param taxInstallMode the taxInstallMode to set
     */
    public void setTaxInstallMode(String taxInstallMode) {
        this.taxInstallMode = taxInstallMode;
    }

    /**
     * @return the pinStatus
     */
    public String getPinStatus() {
        return pinStatus;
    }

    /**
     * @param pinStatus the pinStatus to set
     */
    public void setPinStatus(String pinStatus) {
        this.pinStatus = pinStatus;
    }

    /**
     * @return the list_Pincode
     */
    public List getList_Pincode() {
        return list_Pincode;
    }

    /**
     * @param list_Pincode the list_Pincode to set
     */
    public void setList_Pincode(List list_Pincode) {
        this.list_Pincode = list_Pincode;
    }

    /**
     * @return the rendertempFeeAmount
     */
    public boolean isRendertempFeeAmount() {
        return rendertempFeeAmount;
    }

    /**
     * @param rendertempFeeAmount the rendertempFeeAmount to set
     */
    public void setRendertempFeeAmount(boolean rendertempFeeAmount) {
        this.rendertempFeeAmount = rendertempFeeAmount;
    }

    /**
     * @return the renderOnlinePayBtn
     */
    public boolean isRenderOnlinePayBtn() {
        return renderOnlinePayBtn;
    }

    /**
     * @param renderOnlinePayBtn the renderOnlinePayBtn to set
     */
    public void setRenderOnlinePayBtn(boolean renderOnlinePayBtn) {
        this.renderOnlinePayBtn = renderOnlinePayBtn;
    }

    /**
     * @return the paymentTypeBtn
     */
    public String getPaymentTypeBtn() {
        return paymentTypeBtn;
    }

    /**
     * @param paymentTypeBtn the paymentTypeBtn to set
     */
    public void setPaymentTypeBtn(String paymentTypeBtn) {
        this.paymentTypeBtn = paymentTypeBtn;
    }

    /**
     * @return the onlineUserCredentialmsg
     */
    public String getOnlineUserCredentialmsg() {
        return onlineUserCredentialmsg;
    }

    /**
     * @param onlineUserCredentialmsg the onlineUserCredentialmsg to set
     */
    public void setOnlineUserCredentialmsg(String onlineUserCredentialmsg) {
        this.onlineUserCredentialmsg = onlineUserCredentialmsg;
    }

    /**
     * @return the renderUserAndPasswored
     */
    public boolean isRenderUserAndPasswored() {
        return renderUserAndPasswored;
    }

    /**
     * @param renderUserAndPasswored the renderUserAndPasswored to set
     */
    public void setRenderUserAndPasswored(boolean renderUserAndPasswored) {
        this.renderUserAndPasswored = renderUserAndPasswored;
    }

    /**
     * @return the renderCancelPayment
     */
    public boolean isRenderCancelPayment() {
        return renderCancelPayment;
    }

    /**
     * @param renderCancelPayment the renderCancelPayment to set
     */
    public void setRenderCancelPayment(boolean renderCancelPayment) {
        this.renderCancelPayment = renderCancelPayment;
    }

    /**
     * @return the tempFitnessDetails
     */
    public FitnessDobj getTempFitnessDetails() {
        return tempFitnessDetails;
    }

    /**
     * @param tempFitnessDetails the tempFitnessDetails to set
     */
    public void setTempFitnessDetails(FitnessDobj tempFitnessDetails) {
        this.tempFitnessDetails = tempFitnessDetails;
    }

    public String getReasonForFitness() {
        return reasonForFitness;
    }

    public void setReasonForFitness(String reasonForFitness) {
        this.reasonForFitness = reasonForFitness;
    }

    public boolean isRenderPanelForm() {
        return renderPanelForm;
    }

    public void setRenderPanelForm(boolean renderPanelForm) {
        this.renderPanelForm = renderPanelForm;
    }

    public boolean isRenderPanelReasonForFitness() {
        return renderPanelReasonForFitness;
    }

    public void setRenderPanelReasonForFitness(boolean renderPanelReasonForFitness) {
        this.renderPanelReasonForFitness = renderPanelReasonForFitness;
    }

    public String getFitnessInspectionFeeMessage() {
        return fitnessInspectionFeeMessage;
    }

    public void setFitnessInspectionFeeMessage(String fitnessInspectionFeeMessage) {
        this.fitnessInspectionFeeMessage = fitnessInspectionFeeMessage;
    }
}
