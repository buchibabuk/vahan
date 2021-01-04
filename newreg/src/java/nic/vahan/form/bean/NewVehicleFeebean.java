/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import nic.vahan.form.bean.permit.PermitPanelBean;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.TaxUtils;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.DOTaxDetail;
import nic.vahan.form.dobj.EpayDobj;
import nic.vahan.form.dobj.FeeDobj;
import nic.vahan.form.dobj.Fee_Pay_Dobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.PaymentCollectionDobj;
import nic.vahan.form.dobj.Tax_Pay_Dobj;
import nic.vahan.form.impl.EpayImpl;
import nic.vahan.form.impl.FeeDraftimpl;
import nic.vahan.form.impl.FeeImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.permit.PermitImpl;
import nic.vahan.form.impl.TaxServer_Impl;
import nic.vahan.form.impl.Util;
import org.primefaces.PrimeFaces;
//import tax.web.service.VahanTaxParameters;
import nic.vahan.form.dobj.tax.VahanTaxParameters;
import static nic.vahan.CommonUtils.FormulaUtils.fillTaxParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.fillVehicleParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.Dealer;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TempRegDobj;
import nic.vahan.form.impl.OtherStateVehImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import nic.vahan.form.dobj.OtherStateVehDobj;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.eapplication.OnlinePaymentImpl;
import nic.vahan.form.bean.fancy.RetenRegnNo_dobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.ManualReceiptEntryDobj;
import nic.vahan.form.dobj.OnlinePayDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.TaxExemptiondobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.Trailer_dobj;
import nic.vahan.form.dobj.configuration.TmConfigurationReceipts;
import nic.vahan.form.dobj.dealer.PaymentGatewayDobj;
import nic.vahan.form.impl.ExemptionFeeFineImpl;
import nic.vahan.form.impl.NewImpl;
import static nic.vahan.form.impl.TaxServer_Impl.callTaxService;
import static nic.vahan.form.impl.TaxServer_Impl.callKLTaxService;
import nic.vahan.server.CommonUtils;
import nic.vahan.form.dobj.dealer.TmConfigurationDealerDobj;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.ManualReceiptEntryImpl;
import nic.vahan.form.impl.RetentionImpl;
import nic.vahan.form.impl.Trailer_Impl;
import static nic.vahan.server.ServerUtil.Compare;
import nic.vahan5.reg.rest.config.SpringContext;
import nic.vahan5.reg.rest.model.ContextMessageModel;
import nic.vahan5.reg.rest.model.NewVehicleFeeBeanModel;
import nic.vahan5.reg.rest.model.SessionVariablesModel;
import nic.vahan5.reg.rest.model.WrapperModel;
import nic.vahan5.util.VahanUtil;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

/**
 *
 * @author tranC094
 */
@ManagedBean(name = "newVehicleFeeBean")
@ViewScoped
public class NewVehicleFeebean implements Serializable {

    private WebClient.Builder webClientBuilder = SpringContext.getBean(WebClient.Builder.class);
    private static final Logger LOGGER = Logger.getLogger(NewVehicleFeebean.class);
    private String appl_no;
    private Owner_dobj ownerDobj;
    private OwnerDetailsDobj ownerDetailsDobj;
    FeeImpl feeImpl = null;
    OwnerImpl ownerImpl = null;
    private List<TaxFormPanelBean> listTaxForm = new ArrayList();
    private List<TaxFormPanelBean> listTaxFormBackup = new ArrayList();
    private List list_vm_catg = new ArrayList();
    private List list_maker_model = new ArrayList();
    private FormFeePanelBean feePanelBean = null;
    private TaxFormPanelBean taxBean = null;
    private PaymentCollectionBean paymentBean = null;
    private PermitPanelBean permitPanelBean;
    private long totalAmountPayable = 0;
    FeeDraftimpl feeDraftImpl = null;
    PermitImpl permitImpl = null;
    @ManagedProperty(value = "#{rcpt_bean}")
    private ReceiptMasterBean rcptbean;
    private int pur_cd = 0;
    private boolean newRegistration;
    private boolean tempRegistration;
    private String regn_no;
    private String taxMode = null;
    private List list_vh_class = new ArrayList();
    private String pageLoadingFailed;
    private String taxpanel;
    private String printConfDlgTax;
    private boolean renderTaxPanel = false;
    private String btn_print_label;
    private boolean btn_print = false;
    private boolean btn_save = true;
    private String generatedRcpt = null;
    String printRcptNo = null;
    private boolean buttonPanelVisibility = true;
    private String receiptStickyNote = "currentReceiptStatus";
    private List<PaymentGatewayDobj> addToCartStatusCount;
    private ManualReceiptEntryDobj manualRcptDobjTemp;
    private String chasino;
    Map map = null;
    private String applno;
    // Afzal;
    private List<FeeDobj> feeCollectionLists = null;
    private boolean renderPermitPanel = false;
    private Long userChrg;
    private boolean renderUserChargesAmountPanel = false;
    private List<FeeDobj> listTransWise = null;
    private Long totalUserChrg = 0l;
    private boolean taxExemption = false;
    private boolean taxInstallment = false;
    private boolean renderTaxExemption = false;
    private boolean renderSmartCardFeePanel = false;
    private boolean renderTaxInstallment = false;
    private String taxInstallMode = null;
    private Long smartCardFee;
    private Long smartCardChrg;
    private String rcptNoPopup;
    private int actionCode;
    private boolean showInRto = true;
    private Date currentDate = new Date();
    private TempRegDobj tempReg = null;
    private boolean blnDisableRegnTypeTemp = false;
    private boolean renderTempregPanel = false;
    private List list_c_state;
    private List list_office_to;
    private List list_dealer_cd;
    private OtherStateVehDobj otherStateVehDobj;
    private boolean disableOtherState;
    private boolean renderFeePanelLabel = true;
    private boolean marqueManualReceipt = false;
    private boolean renderManualReceiptMessage = false;
    private String FEE_ACTION = "NEW";
    private String permitRefreshDtl = null;
    private String permitTaxDtl = null;
    private String permitFeeDtl = null;
    private boolean renderCdPanel = false;
    private String seriesAvailMess = "";
    private boolean isSeriesAvail = false;
    private String fancyOrRetenRegnMess = null;
    private boolean isFancyOrReten = false;
    private boolean autoRunTaxListener = true;
    private boolean renderModelCost = false;
    private boolean disableSaveButton = false;
    private boolean disableRevertBackButton = false;
    private boolean renderOnlinePayBtn = false;
    private boolean btnAddToCart = true;
    //private boolean btnAddToCartOnlinePay = false;
    private String paymentTypeBtn = "";
    private boolean renderCancelPayment = false;
    private boolean renderUserAndPasswored = false;
    private boolean renderPushBackSeatPanel = false;
    private boolean isManualReceiptRecord = false;
    private String onlineUserCredentialmsg = "";
    private List<DOTaxDetail> taxDescriptionList = new ArrayList<>();
    private List list_Pincode;
    private String pinStatus = "N";
    private boolean confirmDialogRenderButton = true;
    private SessionVariables sessionVariables = null;
    private List<Trailer_dobj> listTrailerDobj = null;
    private boolean rendertempFeeAmount = false;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private String appl_dt;
    //List<EpayDobj> mandatoryFeeListService = null;

    @PostConstruct
    public void postConstruct() {
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null
                    || sessionVariables.getStateCodeSelected() == null
                    || sessionVariables.getOffCodeSelected() == 0
                    || sessionVariables.getEmpCodeLoggedIn() == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Something went wrong, Please try again..."));
                return;
            }
            TmConfigurationDobj tmConf = Util.getTmConfiguration();
            setFeeCollectionLists(new ArrayList<FeeDobj>());
            setListTransWise(new ArrayList<FeeDobj>());
            feeImpl = new FeeImpl();
            ownerImpl = new OwnerImpl();
            setTaxBean(new TaxFormPanelBean());
            feePanelBean = new FormFeePanelBean();
            paymentBean = new PaymentCollectionBean();

            permitPanelBean = new PermitPanelBean();
            permitImpl = new PermitImpl();
            feePanelBean.setNewBean(this);

            //setBtn_print_label("Print-Receipt-" + rcpt_bean.getBook_no() + (rcpt_bean.getCurrent_rcpt_no() - 1));
            setBtn_print(false);
            map = (Map) Util.getSession().getAttribute("seat_map");
            if (map == null) {
                return;
            }

            VehicleParameters vchparameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
            vchparameters.setREGN_NO((String) map.get("regn_no"));
            if (isCondition(FormulaUtils.replaceTagPermitValues(tmConf.getShowPermitMultiRegion(), vchparameters), "PermitPanelBean()")) {
                permitPanelBean.setRenderMultiRegionList(true);
            }

            if (Util.getSelectedSeat().getAction_cd() == TableConstants.TM_ROLE_NEW_REGISTRATION_FEE) {
                FEE_ACTION = "NEW";
            } else if ((Util.getSelectedSeat().getAction_cd() == TableConstants.TM_ROLE_NEW_REGISTRATION_FIT_FEE) || (Util.getSelectedSeat().getAction_cd() == TableConstants.TM_ROLE_DEALER_NEW_REGISTRATION_FIT_FEE)) {
                FEE_ACTION = "NEWFT";
            }

            String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();

            list_vh_class.clear();
            for (int i = 0; i < data.length; i++) {
                list_vh_class.add(new SelectItem(data[i][0], data[i][1]));
            }

            data = MasterTableFiller.masterTables.VM_VCH_CATG.getData();
            for (int i = 0; i < data.length; i++) {
                list_vm_catg.add(new SelectItem(data[i][0], data[i][1]));
            }
            if (map.get("appl_no") != null) {
                this.appl_no = map.get("appl_no").toString();
            } else {
                appl_no = Util.getSelectedSeat().getAppl_no();
            }

            if (map.get("pur_code") != null) {
                setPur_cd(Integer.parseInt((String) map.get("pur_code")));
            } else {
                setPur_cd(Util.getSelectedSeat().getPur_cd());
            }

            if (map.get("regn_no") != null) {
                setRegn_no((String) map.get("regn_no"));
            } else {
                setRegn_no(Util.getSelectedSeat().getRegn_no());
            }

            if (map.get("actionCode") != null) {
                setActionCode(Integer.parseInt((String) map.get("actionCode")));
            } else {
                setActionCode(Util.getSelectedSeat().getAction_cd());
            }

            if (map.get("appl_dt") != null) {
                appl_dt = map.get("appl_dt").toString();
            } else {
                appl_dt = Util.getSelectedSeat().getAppl_dt().toString();
            }

            //feePanelBean.setPurposeCodeList(EpayImpl.getPurposeCodeList(this.getAppl_no().toUpperCase(), " and pur_cd not in (1) "));
            //vivek 11-May-2015
            ownerDobj = ownerImpl.set_Owner_appl_db_to_dobj(null, appl_no.toUpperCase(), "", getPur_cd());
            if (ownerDobj == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Application detail not found in Selected Office"));
                return;
            }

//            if ("KA,TN,MH".contains(sessionVariables.getStateCodeSelected()) && "O,R".contains(ownerDobj.getRegn_type())) {
//                String[][] instrmentType = MasterTableFiller.masterTables.TM_INSTRUMENTS.getData();
//                for (int i = 0; i < instrmentType.length; i++) {
//                    if (instrmentType[i][0].equals("M")) {
//                        paymentBean.getInstrumentList().add(new SelectItem(instrmentType[i][0], instrmentType[i][1]));
//                        break;
//                    }
//                }
//            }
            if ("KA,TN,MH,RJ,GJ".contains(sessionVariables.getStateCodeSelected())) {
                marqueManualReceipt = true;
            }
            List<ManualReceiptEntryDobj> manualRcptList = new ManualReceiptEntryImpl().getManualReceiptEntryDetails(appl_no);
            if (manualRcptList.size() > 0) {
                manualRcptDobjTemp = ManualReceiptEntryImpl.getApprovedManualReceiptDetails(appl_no);
                renderManualReceiptMessage = true;
            }

            if ("RJ".equals(ownerDobj.getState_cd()) && ownerDobj.getVehType() == TableConstants.VM_VEHTYPE_TRANSPORT && ownerDobj.getPurchase_dt() != null && DateUtils.compareDates(DateUtil.parseDate(ownerDobj.getPurchase_dt()), "10-07-2019") == 1) {
                renderModelCost = true;
            }
            if (ownerDobj.getState_cd().equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(ownerDobj.getVh_class() + ",")) && (ownerDobj.getPush_bk_seat() != 0 || ownerDobj.getOrdinary_seat() != 0)) {
                setRenderPushBackSeatPanel(true);
            } else {
                setRenderPushBackSeatPanel(false);
            }

            /*Repeat Code for getting details from va_owner*/
            ownerDetailsDobj = ownerImpl.getVaOwnerDetails(appl_no.toUpperCase(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
            //ownerDobj = ownerImpl.getOwnerDobj(ownerDetailsDobj);
            listTrailerDobj = Trailer_Impl.set_trailer_dtls_to_dobjList(appl_no, null, pur_cd);
            ownerDobj.setListTrailerDobj(listTrailerDobj);
            int vehClass = ownerDobj.getVh_class();
            data = MasterTableFiller.masterTables.VM_MODELS.getData();
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equals(String.valueOf(ownerDobj.getMaker()))) {
                    list_maker_model.add(new SelectItem(data[i][1], data[i][2]));
                }
            }
            if (list_maker_model.isEmpty()) {
                list_maker_model.add(ownerDobj.getMaker_model());
            }
            if (ServerUtil.isTaxOnPermit(vehClass, sessionVariables.getStateCodeSelected())) {

                setRenderPermitPanel(true);
                permitPanelBean.getPermitDobj().setPmt_type_code(String.valueOf(ownerDobj.getPmt_type()));
                permitPanelBean.getPermitDobj().setPmtCatg(String.valueOf(ownerDobj.getPmt_catg()));
                //Add for permit service type
                NewImpl new_Impl = new NewImpl();
                String servictype = new_Impl.getServiceTypeFromVaPermitNew(ownerDobj);
                ownerDobj.setServicesType(servictype);
                String[] region_covered = new_Impl.getRegionCoveredFromVaPermitNew(ownerDobj);
                ownerDobj.setRegion_covered(region_covered);
                permitPanelBean.getPermitDobj().setServices_TYPE(String.valueOf(ownerDobj.getServicesType()));
                permitPanelBean.getPermitDobj().setRegionCoveredArr(ownerDobj.getRegion_covered());
                permitPanelBean.getPermitDobjPrev().setPmt_type(ownerDobj.getPmt_type() + "");
                permitPanelBean.getPermitDobjPrev().setPmtCatg(ownerDobj.getPmt_catg() + "");
                permitPanelBean.getPermitDobjPrev().setServices_TYPE(ownerDobj.getServicesType() + "");
                permitPanelBean.getPermitDobjPrev().setRegionCoveredArr(ownerDobj.getRegion_covered());

                //end
                permitPanelBean.onSelectPermitType(null);
                permitPanelBean.setIsDisable(true);
                if (ownerDobj != null && ((ownerDobj.getPmt_type() <= 0 && ownerDobj.getPmt_catg() <= 0))) {
                    permitPanelBean.setIsDisable(false);
                }
                /*
                 * Permit Details will be not be shown for Fitness Fee
                 */
                if (Util.getSelectedSeat().getAction_cd() == TableConstants.TM_ROLE_NEW_REGISTRATION_FIT_FEE
                        || Util.getSelectedSeat().getAction_cd() == TableConstants.TM_ROLE_DEALER_NEW_REGISTRATION_FIT_FEE
                        || Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_MAST_TEMP_REG
                        || Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                    setRenderPermitPanel(false);
                }
            } else {
                getPermitPanelBean().setPermitDobj(null);
                setRenderPermitPanel(false);
            }
            VehicleParameters vehParameters = fillVehicleParametersFromDobj(ownerDobj);
            if (((getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE_FITNESS) || (getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE && tmConf != null && tmConf.getTmConfigDealerDobj() != null && tmConf.getTmConfigDealerDobj().isPaymentAtOffice())) {
                setNewRegistration(true);
                setTempRegistration(false);
            }
            //by komal
            setShowInRto(true);
            if ((getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) && !(tmConf != null && tmConf.getTmConfigDealerDobj() != null && tmConf.getTmConfigDealerDobj().isPaymentAtOffice())) {
                setNewRegistration(true);
                setTempRegistration(false);
                setButtonPanelVisibility(false);
                setShowInRto(false);
                setReceiptStickyNote("addToCartStatus");
                setAddToCartStatusCount(ServerUtil.getCartList(Long.parseLong(sessionVariables.getEmpCodeLoggedIn()), sessionVariables.getOffCodeSelected(), sessionVariables.getStateCodeSelected()));
                paymentBean.setPaymentCollectionPanelVisibility(false);
            }

            if (getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE && !(tmConf != null && tmConf.getTmConfigDealerDobj() != null && tmConf.getTmConfigDealerDobj().isPaymentAtOffice())) {
                setNewRegistration(false);
                setTempRegistration(true);
                setButtonPanelVisibility(false);
                setShowInRto(false);
                setReceiptStickyNote("addToCartStatus");
                setAddToCartStatusCount(ServerUtil.getCartList(Long.parseLong(sessionVariables.getEmpCodeLoggedIn()), sessionVariables.getOffCodeSelected(), sessionVariables.getStateCodeSelected()));
                paymentBean.setPaymentCollectionPanelVisibility(false);
            }

            if (getPur_cd() == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || (getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE && tmConf != null && tmConf.getTmConfigDealerDobj() != null && tmConf.getTmConfigDealerDobj().isPaymentAtOffice())) {
                setTempRegistration(true);
                setNewRegistration(false);
            }
// For Online Pay User

            if (tmConf != null) {
                if (tmConf.isOnlinePayment()) {
                    setRenderOnlinePayBtn(true);
                    boolean onlineData = new FeeImpl().getOnlinePayData(appl_no);
                    if (onlineData) {
                        setBtn_save(false);
                        setBtn_print(true);
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

            if (getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE_FITNESS) {
                setBtn_print(true);//For Disable Revert Back In New Vehicle Fitness 
            }

            if ((getPur_cd() == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) && Util.getUserStateCode().equals("KA")) {
                rendertempFeeAmount = true;
            }

        } catch (VahanException ve) {
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        //end vivek

        fillRcptTaxPermitPanel(appl_no);

    }

    public void fillRcptTaxPermitPanel(String applNo) {
        if (rcptbean.getBook_rcpt_no() == null || rcptbean.getBook_rcpt_no().equals("")) {
            rcptNoPopup = "There is no Current Receipt No Assigned.";
            // RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().executeScript("PF('dlgRcptNoPopup').show()");
            return;
        }
        getFeePanelBean().reset();
        Exception exc = null;
        try {
            if (ownerImpl != null && isNewRegistration()) {
                if (isRenderPermitPanel()) {
                    setRenderFeePanelLabel(false);
                } else {
                    updateForNewRegAppl(applNo);
                    setFancyOrRetenRegnMess(returnFancyOrRetenDtld(applNo));
                    if (getFancyOrRetenRegnMess() != null) {
                        setIsSeriesAvail(false);
                        setIsFancyOrReten(true);
                    } else {
                        if (ownerDobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE) && Util.getTmConfiguration().isRegnNoNotAssignOthState()) {
                            setSeriesAvailMess("Vehicle Registration No " + ownerDobj.getRegn_no() + " will be  allotted");
                            setIsSeriesAvail(true);
                            setIsFancyOrReten(false);
                        } else if (ownerDobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_CONFISCATED_AUCTION) && Util.getTmConfiguration().isRegnNoNotAssignOthState() && ownerDobj.getAuctionDobj() != null && ownerDobj.getAuctionDobj().getRegnNo() != null && !ownerDobj.getAuctionDobj().getRegnNo().equals("NEW")) {
                            setSeriesAvailMess("Vehicle Registration No " + ownerDobj.getRegn_no() + " will be  allotted");
                            setIsSeriesAvail(true);
                            setIsFancyOrReten(false);
                        } else {
                            VehicleParameters parameters = fillVehicleParametersFromDobj(ownerDobj, permitPanelBean.getPermitDobj());
                            setSeriesAvailMess(ServerUtil.getAvailablePrefixSeries(parameters));
                            if (!getSeriesAvailMess().equals(TableConstants.SERIES_EXHAUST_MESSAGE) && !getSeriesAvailMess().equals("")) {
                                setSeriesAvailMess("Vehicle Registration No will be Generated from the Series " + getSeriesAvailMess() + ".");
                                setIsSeriesAvail(true);
                                setIsFancyOrReten(false);
                            } else {
                                setSeriesAvailMess("Vehicle Registration No Series not Available.");
                                setIsSeriesAvail(true);
                                setIsFancyOrReten(false);
                            }
                        }
                    }
                }
            } else if (ownerImpl != null && isTempRegistration()) {
                if (ownerDobj == null) {
                    ownerDobj = ownerImpl.set_Owner_appl_db_to_dobj(null, appl_no.toUpperCase(), "", getPur_cd());
                }
                if (ownerDobj == null) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Application No Details Not Found!", "Please Try Again!");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return;
                }

                //by komal
                boolean isVehicleHypothecated = feeImpl.isHypothecated(ownerDobj.getAppl_no().toUpperCase(), getPur_cd());
                ownerDobj.setHypothecatedFlag(isVehicleHypothecated);

                // Automatic Fee Panel Updation
                // Setting mandatory Fee For this Form
                List<EpayDobj> mandatoryFeeList = EpayImpl.getFeeDetailsByAction(ownerDobj, "TMP",
                        ownerDobj.getVh_class(), ownerDobj.getVch_catg(), permitPanelBean.getPermitDobj(), new java.util.Date());
                if (mandatoryFeeList != null && !mandatoryFeeList.isEmpty()) {
                    getFeePanelBean().strictResetPaymentList();
                }

                for (EpayDobj feeDobj : mandatoryFeeList) {

                    FeeDobj collectedFeeDobj = new FeeDobj();
                    collectedFeeDobj.setFeeAmount((long) feeDobj.getE_TaxFee());
                    collectedFeeDobj.setFineAmount((long) feeDobj.getE_FinePenalty());
                    collectedFeeDobj.setPurCd(feeDobj.getPurCd());
                    collectedFeeDobj.setDisableDropDown(true);
                    collectedFeeDobj.setDueDate(feeDobj.getDueDate());
                    collectedFeeDobj.setDueDateString(feeDobj.getDueDateString());

                    // Clear previoulsy Set lists
                    if (TableConstants.VM_TRANSACTION_MAST_ADD_HYPO == feeDobj.getPurCd()) {
                        if (ownerDobj.isHypothecatedFlag()) {
                            if (ownerDobj.getTempReg() == null) {
                                getFeePanelBean().getPayableFeeCollectionList().add(collectedFeeDobj);
                                getFeePanelBean().getFeeCollectionList().add(collectedFeeDobj);
                                getFeeCollectionLists().add(collectedFeeDobj);
                            }
                        }
                    } else if (TableConstants.VM_TRANSACTION_MAST_SHOWROOM_INSPECTION_FEE == feeDobj.getPurCd()) {
                        if (ownerDobj.getTempReg() == null) {
                            getFeePanelBean().getPayableFeeCollectionList().add(collectedFeeDobj);
                            getFeePanelBean().getFeeCollectionList().add(collectedFeeDobj);
                            getFeeCollectionLists().add(collectedFeeDobj);
                        }
                    } else {
                        VehicleParameters parameters = fillVehicleParametersFromDobj(ownerDobj);
                        TmConfigurationReceipts configFeeFineZero = ServerUtil.getTmConfigurationReceipts(sessionVariables.getStateCodeSelected());
                        if (configFeeFineZero != null) {
                            parameters.setTRANSACTION_PUR_CD(feeDobj.getPurCd());
                            if (isCondition(replaceTagValues(configFeeFineZero.getFeeAmtZero(), parameters), "fillRcptTaxPermitPanel- (State: " + sessionVariables.getStateCodeSelected() + ")")) {
                                collectedFeeDobj.setFeeAmount(0L);
                            }
                            if (isCondition(replaceTagValues(configFeeFineZero.getFineAmtZero(), parameters), "fillRcptTaxPermitPanel-(State: " + sessionVariables.getStateCodeSelected() + ")")) {
                                collectedFeeDobj.setFineAmount(0L);
                            }
                        }
                        getFeePanelBean().getPayableFeeCollectionList().add(collectedFeeDobj);
                        getFeePanelBean().getFeeCollectionList().add(collectedFeeDobj);
                        getFeeCollectionLists().add(collectedFeeDobj);
                    }
                }

                getFeePanelBean().calculateTotal();
                VahanTaxParameters taxParameters = fillTaxParametersFromDobj(ownerDobj, permitPanelBean.getPermitDobj());
                fillTaxBeanList(taxParameters, "TMP");
                updateTotalPayableAmount();
            }
//Added by Afzal for Smart Card Fee

            if (getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DUP_RC
                    || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_CHG_ADD || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_TO
                    || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_REM_HYPO
                    || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_FRESH_RC
                    || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_REN_REG || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_VEH_ALTER
                    || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {

                if ((Util.getSelectedSeat().getAction_cd() != TableConstants.TM_ROLE_NEW_REGISTRATION_FIT_FEE
                        && Util.getSelectedSeat().getAction_cd() != TableConstants.TM_ROLE_DEALER_NEW_REGISTRATION_FIT_FEE)
                        && !renderPermitPanel && feeImpl.verifySmartCard()) {
                    setSmartCardChrg(EpayImpl.getPurposeCodeFee(ownerDobj, ownerDobj.getVh_class(), 80, ownerDobj.getVch_catg()));
                    if (getSmartCardChrg() > 0l) {
                        setRenderSmartCardFeePanel(true);
                        setSmartCardFee(getSmartCardChrg());
                        updateTotalPayableAmount();
                    }
                }

            } else {
                setRenderSmartCardFeePanel(false);
            }
        } catch (VahanException e) {
            exc = e;
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            exc = e;
        } catch (javax.xml.ws.WebServiceException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            exc = e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            exc = e;
        }

        if (exc != null) {
            pageLoadingFailed = exc.getMessage();
            // RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("new_veh_fee_subview:pageLoadingFailed");
            PrimeFaces.current().executeScript("PF('dlgPageLoadingFailed').show()");

        }

    }

    public void addNewRow(Integer purCd) {
        String mode = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("actionmode");
        if ("add".equalsIgnoreCase(mode)) {
            if (purCd == null || purCd == -1) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Select Fee Head!", "Select Fee Head!");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return;
            }
            // add row in the fee panel
            if (getFeeCollectionLists().size() == 10) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Maximum number of Fees heads collection reached", "Maximum number of Fees heads collection reached");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            } else {
                getFeeCollectionLists().add(new FeeDobj());
                getFeePanelBean().setEnable(false);
            }

        } else if ("minus".equalsIgnoreCase(mode)) {
            // remove current row from table.
            boolean isExist = feeImpl.checkPurCDExist(appl_no, purCd);
            if (isExist) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Fee Details can't be remove", "Fee Details can't be remove");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return;
            }
            FeeDobj selectedFeeObject = new FeeDobj(purCd);
            int lastIndex = getFeeCollectionLists().lastIndexOf(selectedFeeObject);
            if (lastIndex == 0 && getFeeCollectionLists().size() == 1) {
                getFeeCollectionLists().clear();
                getFeeCollectionLists().add(new FeeDobj());
                getFeePanelBean().setTotalFeeAmount(0);
                getFeePanelBean().setTotalFineAmount(0);
                getFeePanelBean().setTotalAmount(0);
            } else {
                getFeeCollectionLists().remove(lastIndex);
                calculateTotal();
            }
        }
    }

    public void calculateFee(Integer selectedFeeCode, int vehClass, String vehCatg) throws VahanException {

        if (Utility.isNullOrBlank(vehCatg)) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Unknown Vehicle Category.!", "Unknown Vehicle Category!");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }

        if (selectedFeeCode == -1 || selectedFeeCode == 0) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Unknown purpose code for fee collection.", "Unable to calculate fee!");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        if (vehClass == -1 || vehClass == 0) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Unknown vehicle class for fee colletion.", "Unable to calculate fee!");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        if (selectedFeeCode == TableConstants.TAX_PENALTY_EXEMTION || selectedFeeCode == TableConstants.FEE_FINE_EXEMTION || selectedFeeCode == TableConstants.TAX_INTEREST_EXEMTION) {
            FeeDobj selectedFeeObject = new FeeDobj(selectedFeeCode);
            int lastIndex = getFeeCollectionLists().lastIndexOf(selectedFeeObject);
            getFeeCollectionLists().remove(lastIndex);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Not authorize to add these fee head.", "Unable to calculate fee!");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }

        FeeDobj selectedFeeObject = new FeeDobj(selectedFeeCode);
        int lastIndex = getFeeCollectionLists().lastIndexOf(selectedFeeObject);
        getFeeCollectionLists().remove(lastIndex);
        if (!getFeeCollectionLists().contains(selectedFeeObject)) {
            Long feeValue = 0L;
            if (!(selectedFeeCode == TableConstants.VM_PMT_APPLICATION_PUR_CD || selectedFeeCode == TableConstants.VM_PMT_FRESH_PUR_CD
                    || selectedFeeCode == TableConstants.VM_PMT_SURCHARG_FEE
                    || selectedFeeCode == TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD)) {
                feeValue = EpayImpl.getPurposeCodeFee(ownerDobj, vehClass, selectedFeeCode, vehCatg);
            } else if (permitPanelBean.getPermitDobj() != null) {
                if (!CommonUtils.isNullOrBlank(permitPanelBean.getPermitDobj().getDomain_CODE())) {
                    permitPanelBean.getPermitDobj().setRegion_covered(permitPanelBean.getPermitDobj().getDomain_CODE());
                } else {
                    String[] regionCoveredArr = permitPanelBean.getPermitDobj().getRegionCoveredArr();
                    if (regionCoveredArr != null && regionCoveredArr.length > 0) {
                        permitPanelBean.getPermitDobj().setRegion_covered(String.valueOf(regionCoveredArr.length));
                    }
                }
                feeValue = EpayImpl.getPmtPurposeCodeFee(ownerDobj, selectedFeeCode, permitPanelBean.getPermitDobj(), "Fee");
            }
            Long fineValue = 0l;
            java.util.Date dueDate = null;
            if ("KA,OR,CG,GA,JH,BR".contains(ownerDobj.getState_cd()) && ownerDobj.getOtherStateVehDobj() != null && ownerDobj.getOtherStateVehDobj().getNocDate() != null) {
                //dueDate = ownerDobj.getOtherStateVehDobj().getNocDate();
                if ("OR".contains(ownerDobj.getState_cd()) && pur_cd == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {
                    dueDate = ownerDobj.getFit_upto();
                } else {
                    dueDate = ownerDobj.getOtherStateVehDobj().getNocDate();
                }
                VehicleParameters parameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj, permitPanelBean.getPermitDobj());
                parameters.setNEW_VCH("Y");
                fineValue = ServerUtil.getPurposeFineAmount(ownerDobj.getState_cd(), selectedFeeCode, feeValue,
                        dueDate, new java.util.Date(), parameters, true);
            }

            getFeeCollectionLists().remove(selectedFeeObject);
            selectedFeeObject.setFeeAmount(feeValue);
            selectedFeeObject.setFineAmount(fineValue);
            selectedFeeObject.setTotalAmount(feeValue + selectedFeeObject.getFineAmount());
            getFeeCollectionLists().add(selectedFeeObject);
            if ("UP".contains(ownerDobj.getState_cd()) && feeValue == 0
                    && selectedFeeCode == TableConstants.VM_TRANSACTION_MAST_TEMP_TAX && isTempRegistration()) {
                selectedFeeObject.setReadOnlyFee(false);
            }
            calculateTotal();
            updateTotalPayableAmount();
            getFeePanelBean().setEnable(true);

        } else {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Fee Head Already Selected!", "Fee Head Already Selected!");
            FacesContext.getCurrentInstance().addMessage(null, message);
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

    public void updateForNewRegAppl(String applNo) throws VahanException, Exception {

        if (ownerDobj == null) {
            ownerDobj = ownerImpl.set_Owner_appl_db_to_dobj(null, appl_no.toUpperCase(), "", getPur_cd());
        }
        if (ownerDetailsDobj == null) {//need to remove, It is Unnecessary to use.
            ownerDetailsDobj = ownerImpl.getVaOwnerDetails(appl_no.toUpperCase(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
        }

        if (ownerDobj == null) {
            throw new VahanException("No Application Details Found :" + applNo);
        }

        if (renderPermitPanel && permitPanelBean.getPermitDobj() != null) {
            permitFeeDtl = permitPanelBean.getPermitDobj().toString();
        }

        ownerDobj.setAppl_no(appl_no.toUpperCase());
        chasino = ownerDobj.getChasi_no();
        //by komal
        boolean isVehicleHypothecated = feeImpl.isHypothecated(ownerDobj.getAppl_no().toUpperCase(), getPur_cd());
        ownerDobj.setHypothecatedFlag(isVehicleHypothecated);

        if (ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_TEMPORARY)) {
            renderTempregPanel = true;

            tempReg = ownerDobj.getTempReg();
            blnDisableRegnTypeTemp = true;
            renderTempregPanel = true;
            list_c_state = new ArrayList();

            list_c_state = MasterTableFiller.getStateList();

            if (tempReg != null && tempReg.getTmp_state_cd() != null) {
                list_office_to = new ArrayList();
                list_office_to = MasterTableFiller.getOfficeList(tempReg.getTmp_state_cd());
            }

            list_dealer_cd = new ArrayList();
            List<Dealer> listDealer = ServerUtil.getDealerList(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());

            for (Dealer dl : listDealer) {
                list_dealer_cd.add(new SelectItem(dl.getDealer_cd(), dl.getDealer_name()));
            }

        }

//        if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("OR")) {
//            setList_Pincode(new ArrayList());
//            setList_Pincode(ServerUtil.getPincodeList(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected()));
//            if (getList_Pincode().size() > 0) {
//                for (int i = 0; i < getList_Pincode().size(); i++) {
//                    if (ownerDobj.getC_pincode() == (int) getList_Pincode().get(i)) {
//                        setPinStatus("Y");
//                        break;
//                    } else {
//                        setPinStatus("N");
//                    }
//                }
//            }
//        }
        if (ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)) {
            OtherStateVehImpl other = new OtherStateVehImpl();
            ownerDobj.setOtherStateVehDobj(other.setOtherVehicleDetailsToDobj(appl_no));
        }

        if (ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_CD)) {
            renderCdPanel = true;
        }

        ownerDobj.setAppl_no(appl_no);
        if (permitPanelBean != null && permitPanelBean.getPermitDobj() != null) {
            String[] regionCoveredArr = permitPanelBean.getPermitDobj().getRegionCoveredArr();
            if (regionCoveredArr != null && regionCoveredArr.length > 0) {
                permitPanelBean.getPermitDobj().setRegion_covered(String.valueOf(regionCoveredArr.length));
                permitPanelBean.getPermitDobj().setMultiRegion(new NewImpl().checkMultiRegion(appl_no));
            }
        }
        // Automatic Fee Panel Updation
        // Setting mandatory Fee For this Form
        List<EpayDobj> mandatoryFeeList = EpayImpl.getFeeDetailsByAction(ownerDobj, FEE_ACTION,
                ownerDobj.getVh_class(), ownerDobj.getVch_catg(), permitPanelBean.getPermitDobj(), new java.util.Date());
        if (mandatoryFeeList != null && !mandatoryFeeList.isEmpty()) {
            getFeePanelBean().strictResetPaymentList();
        }
        VehicleParameters vehParams = fillVehicleParametersFromDobj(ownerDobj);
        TmConfigurationReceipts configFeeFineZero = ServerUtil.getTmConfigurationReceipts(sessionVariables.getStateCodeSelected());
        for (EpayDobj feeDobj : mandatoryFeeList) {

            FeeDobj collectedFeeDobj = new FeeDobj();
            collectedFeeDobj.setFeeAmount((long) feeDobj.getE_TaxFee());
            collectedFeeDobj.setFineAmount((long) feeDobj.getE_FinePenalty());
            collectedFeeDobj.setPurCd(feeDobj.getPurCd());
            collectedFeeDobj.setDisableDropDown(true);
            collectedFeeDobj.setFromDate(feeDobj.getFromDate());
            collectedFeeDobj.setUptoDate(feeDobj.getUptoDate());
            collectedFeeDobj.setDueDate(feeDobj.getDueDate());
            collectedFeeDobj.setDueDateString(feeDobj.getDueDateString());
            collectedFeeDobj.setReadOnlyFine(true);
            if (collectedFeeDobj.getFromDate() != null && collectedFeeDobj.getUptoDate() != null) {
                collectedFeeDobj.setRenderFromDate(true);
                collectedFeeDobj.setRenderUptoDate(true);
            }
            // Clear previoulsy Set lists
            if (TableConstants.VM_TRANSACTION_MAST_ADD_HYPO == feeDobj.getPurCd()) {
                if (ownerDobj.isHypothecatedFlag()) {
                    vehParams.setTRANSACTION_PUR_CD(feeDobj.getPurCd());
                    if (configFeeFineZero != null) {
                        if (isCondition(replaceTagValues(configFeeFineZero.getFeeAmtZero(), vehParams), "updateForNewRegAppl")) {
                            collectedFeeDobj.setFeeAmount(0L);
                        }
                        if (isCondition(replaceTagValues(configFeeFineZero.getFineAmtZero(), vehParams), "updateForNewRegAppl")) {
                            collectedFeeDobj.setFineAmount(0L);
                        }
                    }
                    getFeePanelBean().getPayableFeeCollectionList().add(collectedFeeDobj);
                    getFeePanelBean().getFeeCollectionList().add(collectedFeeDobj);
                    getFeeCollectionLists().add(collectedFeeDobj);
                }
//                

            } else {
                if (configFeeFineZero != null) {
                    vehParams.setTRANSACTION_PUR_CD(feeDobj.getPurCd());
                    if (isCondition(replaceTagValues(configFeeFineZero.getFeeAmtZero(), vehParams), "updateForNewRegAppl")) {
                        collectedFeeDobj.setFeeAmount(0L);
                    }
                    if (isCondition(replaceTagValues(configFeeFineZero.getFineAmtZero(), vehParams), "updateForNewRegAppl")) {
                        collectedFeeDobj.setFineAmount(0L);
                    }
                }

                getFeePanelBean().getPayableFeeCollectionList().add(collectedFeeDobj);
                getFeePanelBean().getFeeCollectionList().add(collectedFeeDobj);
                getFeeCollectionLists().add(collectedFeeDobj);
            }

            if ((ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                    || ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) && TableConstants.VM_TRANSACTION_MAST_CHG_ADD == feeDobj.getPurCd()) {
                collectedFeeDobj.setDisableDropDown(false);
            }

            if ("OR".equalsIgnoreCase(sessionVariables.getStateCodeSelected()) && TableConstants.POSTAL_PUR_CD == feeDobj.getPurCd()) {
                int fee = ServerUtil.getPincodeFee(sessionVariables.getStateCodeSelected(), ownerDobj.getC_pincode());
                collectedFeeDobj.setFeeAmount((long) fee);
            }
        }
        if (Util.getUserStateCode().equalsIgnoreCase("SK")) {
            RetentionImpl retImpl = new RetentionImpl();
            int rcptDur = 0;
            RetenRegnNo_dobj dobj = retImpl.getVtSurrenderRetentionDetails(appl_no);
            if (dobj != null) {
                rcptDur = DateUtil.getDate1MinusDate2_Months(dobj.getAppl_date(), DateUtils.getCurrentLocalDate()) / 12 - 1;
                if (rcptDur > 0) {
                    FeeDobj collectedFeeDobj = new FeeDobj();
                    collectedFeeDobj.setFeeAmount((long) rcptDur * 5000);
                    collectedFeeDobj.setFineAmount((long) 0);
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
        if ((mandatoryFeeList == null || mandatoryFeeList.isEmpty()) && "O,R".contains(ownerDobj.getRegn_type())) {
            FeeDobj collectedFeeDobj = new FeeDobj();
            getFeePanelBean().getPayableFeeCollectionList().add(collectedFeeDobj);
            getFeePanelBean().getFeeCollectionList().add(collectedFeeDobj);
            getFeeCollectionLists().add(collectedFeeDobj);
        }

        setFinePenaltyDetails(appl_no);

        getFeePanelBean().calculateTotal();

        VahanTaxParameters taxParameters = fillTaxParametersFromDobj(ownerDobj, permitPanelBean.getPermitDobj());
        taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
        if (ownerDobj.getRegn_dt() == null) {
            taxParameters.setREGN_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
        }

        taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
        if (null != permitImpl) {

            // by komal
            PassengerPermitDetailDobj permitDobj = permitImpl.getPermitDetails(getAppl_no().toUpperCase(), null, getPur_cd());
            if (null != permitDobj) {
                String permitType = permitDobj.getPmt_type_code();
                String domainCode = permitDobj.getDomain_CODE();
                String serviceType = permitDobj.getServices_TYPE();
                String permitCatg = permitDobj.getPmtCatg();
                String period = permitDobj.getPeriod();

                permitPanelBean.getPermitDobj().setPmt_type_code(permitType);
                permitPanelBean.getPermitDobj().setDomain_CODE(domainCode);
                permitPanelBean.getPermitDobj().setServices_TYPE(serviceType);
                permitPanelBean.getPermitDobj().setPmtCatg(permitCatg);
                permitPanelBean.getPermitDobj().setPeriod(period);

                //setting permit detials in VahanTaxParameters to calculate tax
                if (!Utility.isNullOrBlank(domainCode) && !"-1".equalsIgnoreCase(domainCode)) {
                    taxParameters.setDOMAIN_CD(Integer.parseInt(domainCode));
                }
                if (!Utility.isNullOrBlank(permitType) && !"-1".equalsIgnoreCase(permitType)) {
                    taxParameters.setPERMIT_TYPE(Integer.parseInt(permitType));
                }
                if (!Utility.isNullOrBlank(serviceType) && !"-1".equalsIgnoreCase(serviceType)) {
                    taxParameters.setSERVICE_TYPE(Integer.parseInt(serviceType));
                }
                if (!Utility.isNullOrBlank(permitCatg) && !"-1".equalsIgnoreCase(permitCatg)) {
                    taxParameters.setPERMIT_SUB_CATG(Integer.parseInt(permitCatg));
                }
            }
        }

        fillTaxBeanList(taxParameters, FEE_ACTION);
        updateTotalPayableAmount();

        if (ownerDobj.getRqrd_tax_modes() != null && !ownerDobj.getRqrd_tax_modes().isEmpty() && isRenderTaxPanel()) {
            Map<Integer, String> rqrdTaxModes = NewImpl.getRqrdTaxModes(ownerDobj.getRqrd_tax_modes());
            if (rqrdTaxModes != null && !rqrdTaxModes.isEmpty()) {
                for (Map.Entry<Integer, String> entry : rqrdTaxModes.entrySet()) {
                    if (entry.getKey() == 58) {
                        taxInstallMode = entry.getValue();
                        break;
                    }
                }

            }

            VehicleParameters vehParameters = fillVehicleParametersFromDobj(ownerDobj);
            vehParameters.setTAX_MODE(taxInstallMode);
            if (isCondition(replaceTagValues(Util.getTmConfiguration().getTax_installment(), vehParameters), "updateForNewRegAppl")) {
                renderTaxInstallment = true;
            }

            if (sessionVariables.getStateCodeSelected().equals("OR") && ownerDobj.getOwner_identity() != null) {
                if (ownerDobj.getOwner_identity().getOwnerCatg() == TableConstants.OWNER_CATG_PHYSICALLY_HANDICAPPED
                        && ownerDobj.getVh_class() == Integer.parseInt(TableConstants.INVALID_CARRIAGE)) {
                    renderTaxExemption = true;
                } else {
                    renderTaxExemption = false;
                }
            }

        }

    }

    public void updateForNewRegApplRest(String applNo) throws VahanException, Exception {
        String baseUrl = VahanUtil.getBaseUrl();
        WrapperModel wrapperModel = new WrapperModel();
        wrapperModel.setSessionVariables(new SessionVariablesModel(this.getSessionVariables()));
        wrapperModel.setNewVehicleFeeBeanModel(new NewVehicleFeeBeanModel(this));
        wrapperModel.setTmConfigDobj(Util.getTmConfiguration());

        String restUrl = baseUrl + "/fee/updateForNewRegAppl";
        wrapperModel = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build()
                .post()
                .uri(restUrl)
                .body(Mono.just(wrapperModel), WrapperModel.class)
                .retrieve()
                .bodyToMono(WrapperModel.class)
                .block();

        if (wrapperModel == null) {
            throw new VahanException("Something went wrong when trying to fetch data from REST API");
        }
        wrapperModel.getNewVehicleFeeBeanModel().setNewVehicleFeeBeanFromModel(this);
        VahanTaxParameters taxParameters = wrapperModel.getVahanTaxParameters();
        fillTaxBeanList(taxParameters, FEE_ACTION);
        updateTotalPayableAmount();

        /**
         * @CommentBy Kartikey Singh Specific scenario for Physically
         * Handicapped Owners registering in Orissa. Moved from REST to this
         * method to prevent assigning renderTaxExemption a wrong value as per
         * the old code
         */
        if (ownerDobj.getRqrd_tax_modes() != null && !ownerDobj.getRqrd_tax_modes().isEmpty() && isRenderTaxPanel()) {
            if (sessionVariables.getStateCodeSelected().equals("OR") && ownerDobj.getOwner_identity() != null) {
                if (ownerDobj.getOwner_identity().getOwnerCatg() == TableConstants.OWNER_CATG_PHYSICALLY_HANDICAPPED
                        && ownerDobj.getVh_class() == Integer.parseInt(TableConstants.INVALID_CARRIAGE)) {
                    setRenderTaxExemption(true);
                } else {
                    setRenderTaxExemption(false);
                }
            }
        }
    }

    public void fillTaxBeanList(VahanTaxParameters taxParameters, String feeAction) throws VahanException {
        //For Fitness Fee No Tax Fee is collected
        if ((Util.getSelectedSeat().getAction_cd() == TableConstants.TM_ROLE_NEW_REGISTRATION_FIT_FEE) || (Util.getSelectedSeat().getAction_cd() == TableConstants.TM_ROLE_DEALER_NEW_REGISTRATION_FIT_FEE)) {
            return;
        }
        String[][] dataTaxModes = MasterTableFiller.masterTables.VM_TAX_MODE.getData();
        TaxFormPanelBean bean = null;
        Map<Integer, String> rqrdTaxModes = null;
        VehicleParameters parameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
        parameters.setLOGIN_OFF_CD(Util.getSelectedSeat().getOff_cd());
        if (ownerDobj.getOtherStateVehDobj() != null && ownerDobj.getOtherStateVehDobj().getOldOffCD() != null) {
            parameters.setPREV_OFF_CD(ownerDobj.getOtherStateVehDobj().getOldOffCD());
        }
        if (isCondition(replaceTagValues(Util.getTmConfiguration().getTax_exemption(), parameters), "fillTaxBeanList")) {
            setRenderTaxExemption(true);
        }
        if (isRenderTaxExemption() && Util.getUserStateCode().equals("DL")) {
            this.taxExemption = true;
            renderTaxPanel = false;
            ownerDobj.getTaxModList().clear();
            ownerDobj.setTaxModList(null);
            ownerDobj.setRqrd_tax_modes(null);
        } else {
            this.taxExemption = false;
            renderTaxPanel = true;
        }
        if (ownerDobj.getRqrd_tax_modes() != null && !ownerDobj.getRqrd_tax_modes().isEmpty()) {
            setRenderTaxPanel(true);
            if (!autoRunTaxListener) {
                isViewIsRendered();
                return;
            }
        } else {

            List<EpayDobj> listTaxTypes = EpayImpl.getFeeDetailsByActionTax(ownerDobj, feeAction);
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

            for (EpayDobj dobj : listTaxTypes) {
                taxParameters.setPUR_CD(dobj.getPurCd());
                taxParameters.setNEW_VCH("Y");
                VehicleParameters vehParameters = fillVehicleParametersFromDobj(ownerDobj);
                vehParameters.setNEW_VCH("Y");
                //AvailableTax modes:
                List listTaxModes = new ArrayList();
                listTaxModes.add(new SelectItem("0", "--Select--"));
                String[] taxModes = TaxServer_Impl.getAvailalableTaxModes(ownerDobj, dobj.getPurCd(), vehParameters);

                if (taxModes != null) {
                    for (int i = 0; i < taxModes.length; i++) {
                        for (int ii = 0; ii < dataTaxModes.length; ii++) {
                            if (dataTaxModes[ii][0].trim().equals(taxModes[i].trim())) {
                                listTaxModes.add(new SelectItem(dataTaxModes[ii][0], dataTaxModes[ii][1]));
                                break;
                            }
                        }
                    }

                    if (tempRegistration) {
                        //If Tempor
                        listTaxModes.add(new SelectItem("M", "Monthly"));
                        bean = new TaxFormPanelBean();
                        bean.setPur_cd(dobj.getPurCd());
                        bean.setTaxPurcdDesc(dobj.getPurCdDescr());
                        bean.updateTaxBean();
                        bean.setListTaxModes(listTaxModes);
                        getListTaxForm().add(bean);
                        //continue;
                    } else {
                        bean = new TaxFormPanelBean();
                        bean.setPur_cd(dobj.getPurCd());
                        bean.setTaxPurcdDesc(dobj.getPurCdDescr());
                        bean.updateTaxBean();
                        bean.setListTaxModes(listTaxModes);
                        getListTaxForm().add(bean);
                    }
                }
            }
        }
        listTaxFormBackup = new ArrayList<>(getListTaxForm());
        //tax exemption for other District/RTO within same state
        if ((taxParameters != null
                && taxParameters.getREGN_TYPE().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))
                && !isCondition(replaceTagValues(Util.getTmConfiguration().getOther_rto_number_change(), parameters), "fillTaxBeanList")) {
            renderTaxPanel = false;
            renderTaxExemption = false;
        }
    }

    public void updateTotalPayableAmount() {
        long finalTaxAmount = 0;
        setTotalAmountPayable(0);
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
            VehicleParameters param = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
            param.setNEW_VCH("Y");
            Long userCharges = EpayImpl.getUserChargesFee(ownerDobj, listPurCd, param);
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
//        }
//
//
//        return tempTaxList;
//    }
    public void reset() {
        setAppl_no("");
        ownerDobj = null;
        getFeePanelBean().reset();
        getPaymentBean().reset();
        getTaxBean().resetAll();
        setBtn_save(false);
    }

    public void validateForm(String payType) throws VahanException {
        this.paymentTypeBtn = payType;
        boolean allGood = false;
        List<FeeDobj> feeDobjList = null;
        TmConfigurationReceipts configFeeFineZero = null;

        if (getTotalAmountPayable() == 0l) {
            VehicleParameters vehParameters = fillVehicleParametersFromDobj(ownerDobj);
            vehParameters.setTRANSACTION_PUR_CD(pur_cd);
            configFeeFineZero = ServerUtil.getTmConfigurationReceipts(sessionVariables.getStateCodeSelected());
            if (configFeeFineZero != null) {
                if (!isCondition(replaceTagValues(configFeeFineZero.getFeeAmtZero(), vehParameters), "validateForm")) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Paybale Amount Can not be zero", "Please check payable amount");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return;
                }
            } else {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Paybale Amount Can not be zero", "Please check payable amount");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
        }
        // Checking for Payment Mode
        if (Utility.isNullOrBlank(getPaymentBean().getPayment_mode())
                || "-1".equalsIgnoreCase(getPaymentBean().getPayment_mode())) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please select payment mode", "Please select payment mode");
            FacesContext.getCurrentInstance().addMessage(null, message);
            //allGood = true;
            return;
        }

        try {
            ServerUtil.validateNoCashPayment(paymentTypeBtn, getPaymentBean().getPayment_mode());
        } catch (VahanException ve) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, ve.getMessage(), ve.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }

        if (payType.equalsIgnoreCase("OnlinePayment") && !getPaymentBean().getPayment_mode().equals("C")) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please select cash payment mode for online payment.", "Please select cash payment mode for online payment.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        // Cash or some thing else
        // hardcode as cash = 4
        if (!getPaymentBean().getPayment_mode().equals("C")) {
            long totalBal = 0;
            for (nic.vahan.form.dobj.PaymentCollectionDobj payDobj : getPaymentBean().getPaymentlist()) {
                if (!(payDobj.validateDobj())) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, payDobj.getValidationMessage(), payDobj.getValidationMessage());
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return;
                }
                totalBal = totalBal + Long.parseLong(payDobj.getAmount());
            }
            if (totalBal > getTotalAmountPayable()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Excess Total Instrument Amount (Rs." + (totalBal - getTotalAmountPayable()) + "/- must be adjusted in any transaction head amount."));
                return;
            }
            if ("DL,SK".contains(sessionVariables.getStateCodeSelected())) {
                if (totalBal != getTotalAmountPayable()) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Paying Instrument Amount Must be equal to Total Payable Amount"));
                    return;
                }
            }
        } else {
            if ("SK".contains(sessionVariables.getStateCodeSelected())) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please select Mixed Mode for payment. Because Cash payment mode in not allowed in Sikkim"));
                return;
            }
        }
        //If other state/district vehicle than CA or TO is mandatory

        if (ownerDobj != null && "NEW".equalsIgnoreCase(FEE_ACTION)
                && ((ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE) && !"HR".contains(ownerDobj.getState_cd()))
                || ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))) {

            feeDobjList = getFeeCollectionLists();
            if (feeDobjList != null) {
                boolean flg = false;
                for (FeeDobj feedobj : feeDobjList) {
                    if (feedobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_CHG_ADD
                            || feedobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_TO
                            || feedobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION) {
                        flg = true;
                        break;
                    }
                }
                if (ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE) && "KA".contains(ownerDobj.getState_cd()) && (ownerDobj.getVh_class() == 13 || ownerDobj.getVh_class() == 28)) {
                    flg = true;
                }
                if (!flg) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Vehicle belongs to Other State/District, Please Select any one 'Change of Address Fee' or 'Transfer Of Ownership Fee' or 'Conversion of Vehicle Fee' also...",
                            "Vehicle belongs to Other State/District, Please Select any one 'Change of Address Fee' or 'Transfer Of Ownership Fee' or 'Conversion of Vehicle Fee' also...");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return;
                }
            }

        }
        if (isManualReceiptRecord = ManualReceiptEntryImpl.isManualReceiptRecordInVa(appl_no)) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please Approve Manual Receipt Entry against Transaction Application No:" + appl_no + " then Proceed."));
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

            String taxCalc = permitPanelBean.getPermitDobj() != null ? permitPanelBean.getPermitDobj().toString() : "";
            taxCalc = taxCalc + " : " + ownerDobj.getSale_amt() + " : " + ownerDobj.getOther_criteria();
            if (bean.getTaxCalcBasedOnParms() != null && !bean.getTaxCalcBasedOnParms().equals(taxCalc)) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Tax is Calculated for Different  Details,Please Press Get Fee-Tax Details button",
                        "Tax is Calculated for Different Details,Please Press Get Fee-Tax Details button");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }

            //Validate Tax Penalty Exemption Amount            
            feeDobjList = getFeeCollectionLists();
            if (feeDobjList != null && !feeDobjList.isEmpty()) {
                long totalPenaltyAmnt = 0l;
                for (TaxFormPanelBean bean_amt : listTaxForm) {
                    totalPenaltyAmnt = totalPenaltyAmnt + bean_amt.getTotalPaybalePenalty();
                }
                for (FeeDobj feedobj : feeDobjList) {
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

        if (renderTaxPanel && listTaxForm.size() > 0 && !flg) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please select Tax Mode", "Please select Tax Mode");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
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
            // RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("new_veh_fee_subview:confirmationPopup");
            PrimeFaces.current().executeScript("PF('confDlgTax').show()");
            PrimeFaces.current().executeScript("PF('buiVarNoImage').show()");
        }
    }

    public void blockMasterLayout(String renderType) {
        if (renderType.equals("REVERT")) {
            confirmDialogRenderButton = false;
        } else {
            confirmDialogRenderButton = true;
        }
        // RequestContext rc = RequestContext.getCurrentInstance();
        PrimeFaces.current().ajax().update("new_veh_fee_subview:confirmDialogAddToCart");
        PrimeFaces.current().executeScript("PF('buiVarNoImage').show()");
        PrimeFaces.current().executeScript("PF('confirmDialogAddToCartVar').show()");
    }

    public void calculateBalanceAmount(long totalAmount) {
        long totalBal = 0;

        for (PaymentCollectionDobj dobj : getPaymentBean().getPaymentlist()) {
            if (dobj.getAmount() != null) {
                totalBal = totalBal + Long.parseLong(dobj.getAmount());
            }
        }

        if (getPaymentBean().isIsCashSelected()) {
            getPaymentBean().setCash(totalBal);
            getPaymentBean().setExcessAmount(0);
        }

        long excess = 0;
        if ((totalAmount - totalBal) < 0) {
            excess = totalAmount - totalBal;
            getPaymentBean().setBalanceAmount(0);
            getPaymentBean().setExcessAmount(-excess);
            getPaymentBean().setCash(0);
        } else {
            getPaymentBean().setBalanceAmount((totalAmount) - totalBal);
            getPaymentBean().setCash((totalAmount) - totalBal);
        }

        getPaymentBean().setAmountInCash(totalBal);
    }

    public String saveNewRegistrationFee() throws VahanException {
        String baseUrl = VahanUtil.getBaseUrl();
        String rcptNo[] = null;
        try {
            if (getAppl_no() == null || getAppl_no().equals("")) {
                return "";
            }

            WrapperModel wrapperModel = new WrapperModel();
            wrapperModel.setSessionVariables(new SessionVariablesModel(this.getSessionVariables()));
            wrapperModel.setNewVehicleFeeBeanModel(new NewVehicleFeeBeanModel(this));
            wrapperModel.setTmConfigDobj(Util.getTmConfiguration());

            String restUrl = baseUrl + "/fee/save"
                    + "?clientIpAddress=" + Util.getClientIpAdress()
                    + "&selectedRoleCode=" + (String) Util.getSession().getAttribute("selected_role_cd");
            wrapperModel = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build()
                    .post()
                    .uri(restUrl)
                    .body(Mono.just(wrapperModel), WrapperModel.class)
                    .retrieve()
                    .bodyToMono(WrapperModel.class)
                    .block();

            if (wrapperModel == null) {
                LOGGER.error("Something went wrong when trying to fetch data from REST API");
                throw new VahanException("Something went wrong when trying to fetch data from REST API");
            }

            List<FeeDobj> feeCollectionCloneLists = new ArrayList<>(wrapperModel.getNewVehicleFeeBeanModel().getFeeCollectionLists());
            // Goes through
            if (this.paymentTypeBtn.equals("RtoPayment")) {
                rcptNo = wrapperModel.getNewVehicleFeeBeanModel().getRcptNo();
                generatedRcpt = rcptNo[1];
                if (!Utility.isNullOrBlank(generatedRcpt)) {
                    Thread feesTaxTread = null;
                    String BASE_URI = "http://127.0.0.1:{port}/vahantaxws/webresources/";
                    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                    int serverPort = request.getLocalPort();
                    String baseURL = BASE_URI.replace("{port}", serverPort + "");
                    String feeAct = FEE_ACTION;
                    if (isTempRegistration()) {
                        feeAct = "TMP";
                    }
                    List<TaxFormPanelBean> listTaxFormService = new ArrayList<>();
                    if (renderTaxPanel) {
                        listTaxFormService = listTaxForm;
                    }
//                    feesTaxTread = new Thread(new FeesTaxThread(ownerDobj, permitPanelBean, feeCollectionCloneLists, listTaxFormService,
//                            Util.getUserLoginOffCode(), Util.getSelectedSeat().getPur_cd(), feeAct,
//                            generatedRcpt, Util.getEmpCode(), Util.getUserStateCode(), baseURL, Util.getSelectedSeat().getAction_cd()), "fessTaxThread");
//                    feesTaxTread.start();
                    String succesfulMes = " Receipt Number Generated : " + generatedRcpt + " For Application No. :" + getAppl_no();
                    if (rcptNo[2] != null) {
                        succesfulMes = succesfulMes + " Regn No Generated: " + rcptNo[2];
                    }
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful Transaction", succesfulMes);
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                    setBtn_print_label("Print-Receipt-" + generatedRcpt);
                    setBtn_print(true);
                    getFeePanelBean().setReadOnlyFineAmount(true);
                    setApplno(getAppl_no());
                    reset();
                    setTotalUserChrg(0l);
                    setSmartCardFee(0l);
                    setTotalAmountPayable(0);
                    Util.getSession().removeAttribute("seat_map");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    //PrimeFaces.current().ajax().update("new_veh_fee_subview:taxpanel");
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rcptno", generatedRcpt);
                    return "PrintCashReceiptReport";
                }
                rcptbean.reset();
            } else if (this.paymentTypeBtn.equals("OnlinePayment")) {
                String userPwd = wrapperModel.getNewVehicleFeeBeanModel().getUserPwd();
                if (userPwd != null) {
                    String userInfo = "Online Payment Credentials User ID : " + appl_no + " & Password :  " + userPwd;
                    setOnlineUserCredentialmsg(userInfo);
                    PrimeFaces.current().ajax().update("new_veh_fee_subview:onlinePaymentdialog");
                    PrimeFaces.current().executeScript("PF('buiVar').show();");
                    PrimeFaces.current().executeScript("PF('onlinePaymentvar').show();");
                }
            }

        } catch (WebClientResponseException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getResponseBodyAsString(), e.getResponseBodyAsString()));
        } catch (VahanException vmex) {
            LOGGER.error(vmex.toString() + " " + vmex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vmex.getMessage(), vmex.getMessage()));
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
        return "";
    }
    // by komal

    public String addToCartOriginal() {
        Exception ex = null;
        String return_path = "";
        int roadTaxCount = 0;
        int newRegnFeeAndTempFee = 0;
        long checkTotalAmount = 0L;
        VehicleParameters vehParameters = null;
        String stateCd = null;
        int offCd = 0;
        String empCode = null;
        TmConfigurationDealerDobj tmDealerConfigDobj = null;
        TmConfigurationReceipts configFeeFineZero = null;
        List<OnlinePayDobj> payDobj = null;

        if (getAppl_no() == null || getAppl_no().equals("")) {
            return return_path;
        }

        try {
            TmConfigurationDobj tmConfig = Util.getTmConfiguration();
            if (sessionVariables.getStateCodeSelected() != null && sessionVariables.getOffCodeSelected() != 0 && sessionVariables.getEmpCodeLoggedIn() != null && tmConfig != null && tmConfig.getTmConfigDealerDobj() != null) {
                stateCd = sessionVariables.getStateCodeSelected();
                offCd = sessionVariables.getOffCodeSelected();
                empCode = sessionVariables.getEmpCodeLoggedIn();
                tmDealerConfigDobj = tmConfig.getTmConfigDealerDobj();
            } else {
                return "";
            }
            configFeeFineZero = ServerUtil.getTmConfigurationReceipts(sessionVariables.getStateCodeSelected());
            vehParameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);

            if (renderPermitPanel && permitPanelBean.getPermitDobj() != null) {
                permitRefreshDtl = permitPanelBean.getPermitDobj().toString();
                if (permitTaxDtl != null && !permitTaxDtl.equals(permitRefreshDtl)) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Tax is Calculated for Different Permit Details,Please Press Get Fee-Tax Details button",
                            "Tax is Calculated for Different Permit Details,Please Press Get Fee-Tax Details button");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return return_path;

                }

                if (permitFeeDtl != null && !permitFeeDtl.equals(permitRefreshDtl)) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Fee is Calculated for Different Permit Details,Please Press Get Fee-Tax Details button",
                            "Fee is Calculated for Different Permit Details,Please Press Get Fee-Tax Details button");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return return_path;

                }
            }

            if (ownerDobj != null) {
                if (isNewRegistration()) {
                    vehParameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
                    String seriesAvailableStatus = ServerUtil.getAvailablePrefixSeries(vehParameters);
                    if (seriesAvailableStatus.equalsIgnoreCase(TableConstants.SERIES_EXHAUST_MESSAGE)) {
                        throw new VahanException(TableConstants.SERIES_EXHAUST_MESSAGE);
                    }

                    String isCriteriaMatchMsg = ServerUtil.isNewRegnNotAllowed(vehParameters);
                    if (isCriteriaMatchMsg != null && !isCriteriaMatchMsg.isEmpty()) {
                        throw new VahanException(isCriteriaMatchMsg);
                    }
                }

                vehParameters.setPUR_CD(pur_cd);
                vehParameters.setACTION_CD(actionCode);
                vehParameters.setAPPL_DATE(DateUtil.convertStringDDMMMYYYYToYYYYMMDD(appl_dt));
                boolean isValidForRegn = ServerUtil.validateVehicleNorms(ownerDobj, pur_cd, vehParameters, tmDealerConfigDobj);
                if (!isValidForRegn) {
                    throw new VahanException("State Transport Department has not authorized you to take Online Payment for '" + MasterTableFiller.masterTables.VM_VH_CLASS.getDesc(ownerDobj.getVh_class() + "") + "' with emission norms " + MasterTableFiller.masterTables.VM_NORMS.getDesc(ownerDobj.getNorms() + "") + ", please contact respective Registering Authority regarding this.");
                }
            }

            Fee_Pay_Dobj feePayDobj = new Fee_Pay_Dobj();

            List<FeeDobj> feeDobjList = getFeeCollectionLists();
            if (feeDobjList != null) {
                for (FeeDobj feedobj : feeDobjList) {
                    if (feedobj.getPurCd() == 99 || feedobj.getPurCd() == 80) {
                        continue;
                    }
                }
            }

            if (isNewRegistration() || tempRegistration) {
                if (renderTaxPanel) {
                    List<DOTaxDetail> taxBreakUpList = null;
                    if (listTaxForm.size() > 0) {
                        List<Tax_Pay_Dobj> listTaxDobj = new ArrayList<Tax_Pay_Dobj>();

                        taxBreakUpList = new ArrayList();
                        for (TaxFormPanelBean bean : listTaxForm) {
                            if (!bean.getTaxMode().equals("0")) {
                                taxBreakUpList.addAll(bean.getTaxDescriptionList());
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
                                taxDobj.setRegnNo(regn_no);
                                taxDobj.setPaymentMode(getPaymentBean().getPayment_mode());
                                taxDobj.setTaxBreakDetails(bean.getTaxDescriptionList());
                                if (ownerDobj != null) {
                                    taxDobj.setRegnNo(getOwnerDobj().getRegn_no());
                                    taxDobj.setApplNo(getOwnerDobj().getAppl_no().toUpperCase());

                                }

                                taxDobj.setDeal_cd(empCode);
                                taxDobj.setOff_cd(offCd);

                                taxDobj.setOp_dt(new java.util.Date());
                                taxDobj.setRcptDate(new java.util.Date());
                                taxDobj.setApplNo(appl_no);
                                taxDobj.setNoOfAdvUnits(bean.getNoOfUnits());
                                taxDobj.setTotalPaybaleTax1(bean.getTotalTax1());
                                taxDobj.setTotalPaybaleTax2(bean.getTotalTax2());
                                listTaxDobj.add(taxDobj);
                            }
                        }

                        feePayDobj.setListTaxDobj(listTaxDobj);
                        feePayDobj.setPermitDobj(getPermitPanelBean().getPermitDobj());

                    }
                }

            }
            feePayDobj.setOp_dt(new java.util.Date());
            feePayDobj.setRegnNo(regn_no);
            feePayDobj.setApplNo(getAppl_no().toUpperCase());
            feePayDobj.setRcptDt(new java.util.Date());
            feePayDobj.setPaymentMode(getPaymentBean().getPayment_mode());

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

            feePayDobj.setCollectedFeeList(feeCollectionCloneLists);

            boolean flg = false;
            if (listTaxForm != null) {
                for (TaxFormPanelBean bean : listTaxForm) {
                    if (!bean.getTaxMode().equals("0")) {
                        flg = true;
                    }
                }

                if (renderTaxPanel && listTaxForm.size() > 0 && !flg) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please select Tax Mode", "Please select Tax Mode");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return return_path;
                }
            }

            if (feePayDobj.getCollectedFeeList() != null && !feePayDobj.getCollectedFeeList().isEmpty()) {
                for (FeeDobj dobj : feePayDobj.getCollectedFeeList()) {
                    int pur_cd = dobj.getPurCd();
                    if (getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                        if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {
                            newRegnFeeAndTempFee++;
                        }
                    } else if (getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                        if (pur_cd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
                            newRegnFeeAndTempFee++;
                        }
                    }
                    Long totalAmount = dobj.getTotalAmount();
                    checkTotalAmount = checkTotalAmount + totalAmount;
                    vehParameters.setTRANSACTION_PUR_CD(pur_cd);
                    if ((totalAmount == null || totalAmount == 0L)) {
                        if (configFeeFineZero != null) {
                            if (!isCondition(replaceTagValues(configFeeFineZero.getFeeAmtZero(), vehParameters), "addToCart-tm_configuration-Fee_amt_zero(State:" + stateCd + ")")) {
                                throw new VahanException("Fee Head / Fee Amount cannot be zero value for " + ServerUtil.getTaxHead(pur_cd));
                            }
                        } else {
                            throw new VahanException("Fee Head / Fee Amount cannot be zero value for " + ServerUtil.getTaxHead(pur_cd));
                        }
                    }
                }

                if (newRegnFeeAndTempFee <= 0) {
                    throw new VahanException("Not Valid For add To Cart");
                }
            }
            List<Tax_Pay_Dobj> taxDobjList = feePayDobj.getListTaxDobj();
            if (taxDobjList != null && !taxDobjList.isEmpty()) {
                for (Tax_Pay_Dobj taxdobj : taxDobjList) {
                    int pur_cd = taxdobj.getPur_cd();
                    Long totalAmount = taxdobj.getFinalTaxAmount();
                    checkTotalAmount = checkTotalAmount + totalAmount;
                    if (totalAmount == null || totalAmount == 0L || pur_cd <= 0) {
                        if (taxdobj.getTotalPaybaleTax() <= 0 || taxdobj.getFinalTaxAmount() < 0) {
                            throw new VahanException("Record cannot be saved with zero value for " + ServerUtil.getTaxHead(taxdobj.getPur_cd()));
                        }
                    }
                }
            }

            if (getTotalAmountPayable() == 0l) {
                payDobj = new FeeImpl().moveFileWithZeroAmtAtDealerPoint(feePayDobj, getAppl_no(), empCode, stateCd, offCd, checkTotalAmount);
                if (payDobj != null) {
                    if (!CommonUtils.isNullOrBlank(payDobj.get(0).getReceiptNo())) {
                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rcptno", payDobj.get(0).getReceiptNo());
                        return "PrintCashReceiptReport";
                    }
                }
            }

            if (feePayDobj.getCollectedFeeList() != null && !feePayDobj.getCollectedFeeList().isEmpty()) {
                if (getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                    vehParameters.setPUR_CD(getPur_cd());
                    EpayImpl.checkTempFeeInNewRegn(tmConfig, pur_cd, sessionVariables.getOffCodeSelected(), stateCd, feePayDobj, sessionVariables.getEmpCodeLoggedIn(), vehParameters);
                    if (taxDobjList == null) {
                        if ((tmDealerConfigDobj.isTaxExemptionAllowed() && (isCondition(replaceTagValues(tmConfig.getTax_exemption(), vehParameters), "addToCart-2"))) || "NEWFT".equalsIgnoreCase(FEE_ACTION)) {
                            feeImpl.saveAddToCartDetails(feePayDobj, getAppl_no(), checkTotalAmount, stateCd, offCd, empCode);
                        } else {
                            throw new VahanException("Problem in tax Details!!!.");
                        }
                    } else {
                        if (!taxDobjList.isEmpty()) {
                            for (Tax_Pay_Dobj taxdobj : taxDobjList) {
                                int pur_cd = taxdobj.getPur_cd();
                                if (pur_cd == TableConstants.TM_ROAD_TAX || ("KL".equals(stateCd) && pur_cd == TableConstants.TM_CESS_TAX)) {
                                    roadTaxCount++;
                                    break;
                                }
                            }
                            if (roadTaxCount > 0) {
                                feeImpl.saveAddToCartDetails(feePayDobj, getAppl_no(), checkTotalAmount, stateCd, offCd, empCode);
                            } else {
                                throw new VahanException("Problem in tax Details!!!.");
                            }
                        }
                    }
                } else {
                    feeImpl.saveAddToCartDetails(feePayDobj, getAppl_no(), checkTotalAmount, stateCd, offCd, empCode);
                }
            } else {
                throw new VahanException("Payable Amount cannot be zero.Please check Fee and Tax Details!!!.");
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return return_path;
        } catch (Exception e) {
            ex = e;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", "There is some Problem while adding into the cart, please try after sometime or contact Administrator!");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return return_path;
        }

        if (ex == null) {
            return_path = "home";
        }

        return return_path;
    }

    public String addToCart() {
        String baseUrl = VahanUtil.getBaseUrl();
        Exception ex = null;
        String return_path = "";

        if (getAppl_no() == null || getAppl_no().equals("")) {
            return return_path;
        }

        try {
            WrapperModel wrapperModel = new WrapperModel();
            wrapperModel.setTmConfigDobj(Util.getTmConfiguration());
            wrapperModel.setSessionVariables(new SessionVariablesModel(sessionVariables));
            wrapperModel.setOwnerDobj(ownerDobj);
            wrapperModel.setNewVehicleFeeBeanModel(new NewVehicleFeeBeanModel(this));

            String restUrl = baseUrl + "/fee/addToCart"
                    + "?clientIpAddress=" + Util.getClientIpAdress()
                    + "&selectedRoleCode=" + (String) Util.getSession().getAttribute("selected_role_cd");
            wrapperModel = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build()
                    .post()
                    .uri(restUrl)
                    .body(Mono.just(wrapperModel), WrapperModel.class)
                    .retrieve()
                    .bodyToMono(WrapperModel.class)
                    .block();

            if (wrapperModel == null) {
                return return_path;
            }

            // Check for messages in FacesContext or RequestContext.                // Retirn if required.
            ContextMessageModel context = wrapperModel.getContextMessageModel();
            if (context != null) {
                Severity severity = context.getMessageSeverity().equals(ContextMessageModel.MessageSeverity.WARN) ? FacesMessage.SEVERITY_WARN : FacesMessage.SEVERITY_ERROR;
                FacesMessage facesMessage = new FacesMessage(severity, context.getMessage1(), context.getMessage2());
                if (context.getMessageContext().equals(ContextMessageModel.MessageContext.FACES)) {
                    FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                } else {
                    PrimeFaces.current().dialog().showMessageDynamic(facesMessage);
                }

                if (context.isIsReturn()) {
                    return return_path;
                }
            }

            if (wrapperModel.getNewVehicleFeeBeanModel().getTotalAmountPayable() == 0l) {
                List<OnlinePayDobj> payDobj = wrapperModel.getNewVehicleFeeBeanModel().getPayDobj();
                // Setting action code as done in feeimplementation.moveFileWithZeroAmtAtDealerPoint()
                Util.getSelectedSeat().setAction_cd(TableConstants.TM_ROLE_DEALER_CART_PAYMENT);
                if (payDobj != null) {
                    if (!CommonUtils.isNullOrBlank(payDobj.get(0).getReceiptNo())) {
                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rcptno", payDobj.get(0).getReceiptNo());
                        return "PrintCashReceiptReport";
                    }
                }
            }
        } catch (WebClientResponseException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return return_path;
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return return_path;
        } catch (Exception e) {
            ex = e;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", "There is some Problem while adding into the cart, please try after sometime or contact Administrator!");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return return_path;
        }

        if (ex == null) {
            return_path = "home";
        }

        return return_path;
    }

    public String reverBackForRectificationOriginal() throws VahanException {
        Status_dobj status_dobj = new Status_dobj();
        int prevAcCode = 0;
        Exception ex = null;
        String return_path = "";
        try {
            prevAcCode = ServerUtil.getPreviousActionCode(getActionCode(), getPur_cd(), appl_no, fillVehicleParametersFromDobj(ownerDobj));

            status_dobj.setPrev_action_cd_selected(prevAcCode);
            status_dobj.setAppl_no(appl_no);
            status_dobj.setPur_cd(getPur_cd());
            status_dobj.setStatus(TableConstants.STATUS_REVERT);
            status_dobj.setSeat_cd(TableConstants.STATUS_REVERT);

            FeeImpl.revertBackForRectification(status_dobj);
        } catch (VahanException e) {
            ex = e;
        } catch (Exception e) {
            ex = e;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

        if (ex != null) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", "Can't RollBack For " + appl_no + ",Please contact Administrator!");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        if (ex == null) {
            return_path = "home";
        }

        return return_path;
    }

    public String reverBackForRectification() throws VahanException {
        Exception ex = null;
        String return_path = "";
        try {
            String baseUrl = VahanUtil.getBaseUrl();

            WrapperModel wrapperModel = new WrapperModel();
            wrapperModel.setTmConfigDobj(Util.getTmConfiguration());
            wrapperModel.setSessionVariables(new SessionVariablesModel(sessionVariables));
            wrapperModel.setOwnerDobj(ownerDobj);
            wrapperModel.setNewVehicleFeeBeanModel(new NewVehicleFeeBeanModel(this));

            String restUrl = baseUrl + "/fee/reverBack"
                    + "?clientIpAddress=" + Util.getClientIpAdress()
                    + "&selectedRoleCode=" + (String) Util.getSession().getAttribute("selected_role_cd");
            wrapperModel = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build()
                    .post()
                    .uri(restUrl)
                    .body(Mono.just(wrapperModel), WrapperModel.class)
                    .retrieve()
                    .bodyToMono(WrapperModel.class)
                    .block();

            if (wrapperModel == null) {
                throw new VahanException("Unable to revert application for rectification.");
            }
        } catch (VahanException e) {
            ex = e;
        } catch (Exception e) {
            ex = e;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

        if (ex != null) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", "Can't RollBack For " + appl_no + ",Please contact Administrator!");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        if (ex == null) {
            return_path = "home";
        }

        return return_path;
    }

    public String returnFancyOrRetenDtld(String applNo) {
        return ownerImpl.checkForFancyOrRetenRegn(appl_no.toUpperCase());
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
     * @return the ownerDobj
     */
    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    /**
     * @param ownerDobj the ownerDobj to set
     */
    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    public FormFeePanelBean getFeePanelBean() {
        return feePanelBean;
    }

    public void setFeePanelBean(FormFeePanelBean feePanelBean) {
        this.feePanelBean = feePanelBean;
    }

    public PaymentCollectionBean getPaymentBean() {
        return paymentBean;
    }

    public void setPaymentBean(PaymentCollectionBean paymentBean) {
        this.paymentBean = paymentBean;
    }

    public Long getTotalAmountPayable() {
        return totalAmountPayable;
    }

    public void setTotalAmountPayable(long totalAmountPayable) {
        this.totalAmountPayable = totalAmountPayable;
    }

    public PermitPanelBean getPermitPanelBean() {
        return permitPanelBean;
    }

    public void setPermitPanelBean(PermitPanelBean permitPanelBean) {
        this.permitPanelBean = permitPanelBean;
    }

    public ReceiptMasterBean getRcptbean() {
        return rcptbean;
    }

    public void setRcptbean(ReceiptMasterBean rcptbean) {
        this.rcptbean = rcptbean;
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
     * @return the newRegistration
     */
    public boolean isNewRegistration() {
        return newRegistration;
    }

    /**
     * @param newRegistration the newRegistration to set
     */
    public void setNewRegistration(boolean newRegistration) {
        this.newRegistration = newRegistration;
    }

    /**
     * @return the tempRegistration
     */
    public boolean isTempRegistration() {
        return tempRegistration;
    }

    /**
     * @param tempRegistration the tempRegistration to set
     */
    public void setTempRegistration(boolean tempRegistration) {
        this.tempRegistration = tempRegistration;
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
     * @return the taxMode
     */
    public String getTaxMode() {
        return taxMode;
    }

    /**
     * @param taxMode the taxMode to set
     */
    public void setTaxMode(String taxMode) {
        this.taxMode = taxMode;
    }

//
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
     * @return the list_vh_class
     */
    public List getList_vh_class() {
        return list_vh_class;
    }

    /**
     * @param list_vh_class the list_vh_class to set
     */
    public void setList_vh_class(List list_vh_class) {
        this.list_vh_class = list_vh_class;
    }

    public void taxModeListener(AjaxBehaviorEvent event) {

        //############################--IMPORTANT--####################################
        //if any changes in this method then same changes also must be in beforeTaxModeListener()
        //############################--IMPORTANT--####################################
        try {
            TaxFormPanelBean taxSelectedFormBean = (TaxFormPanelBean) event.getComponent().getAttributes().get("taxBeanAttr");
            if (taxSelectedFormBean.getTaxMode().equals("0")) {
                taxSelectedFormBean.resetAll();
                updateTotalPayableAmount();
                return;
            }

            if (taxSelectedFormBean.getTaxMode().equalsIgnoreCase("O") || taxSelectedFormBean.getTaxMode().equalsIgnoreCase("L")) {
                taxSelectedFormBean.setNoOfUnits(1);
            }

            String taxCalc = permitPanelBean.getPermitDobj() != null ? permitPanelBean.getPermitDobj().toString() : "";
            taxCalc = taxCalc + " : " + ownerDobj.getSale_amt() + " : " + ownerDobj.getOther_criteria();
            taxSelectedFormBean.setTaxCalcBasedOnParms(taxCalc);

            VahanTaxParameters taxParameters = null;
            taxSelectedFormBean.resetAll();
            taxParameters = fillTaxParametersFromDobj(ownerDobj, permitPanelBean.getPermitDobj());
            if (Util.getTmConfiguration().isAuto_tax_no_units()) {
                for (TaxFormPanelBean taxForm : listTaxForm) {
                    if (taxForm.getTaxMode().equals(taxSelectedFormBean.getTaxMode())) {
                        taxForm.setNoOfUnits(taxSelectedFormBean.getNoOfUnits());
                        updateSelectedTaxFormBean(taxForm, taxParameters);
                    }
                }
            } else {
                updateSelectedTaxFormBean(taxSelectedFormBean, taxParameters);
            }

        } catch (VahanException ve) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

    }

    public void updateSelectedTaxFormBean(TaxFormPanelBean taxSelectedFormBean, VahanTaxParameters taxParameters) throws VahanException {
        try {
            if (taxSelectedFormBean.getTaxMode().equals("0")) {
                taxSelectedFormBean.resetAll();
                updateTotalPayableAmount();
                return;
            }

            if (taxSelectedFormBean.getTaxMode().equalsIgnoreCase("O") || taxSelectedFormBean.getTaxMode().equalsIgnoreCase("L")) {
                taxSelectedFormBean.setNoOfUnits(1);
            }

            taxParameters.setTAX_MODE(taxSelectedFormBean.getTaxMode());
            taxParameters.setPUR_CD(taxSelectedFormBean.getPur_cd());
            taxParameters.setNEW_VCH("Y");
            taxParameters.setTAX_MODE_NO_ADV(taxSelectedFormBean.getNoOfUnits());
            taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));

            if (taxParameters.getTAX_MODE().equals("E")) {
                return;
            }

            if (newRegistration && ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_TEMPORARY)
                    && ownerDobj.getTempReg() != null && ownerDobj.getTempReg().getTmp_regn_dt() != null) {
                long dtDiff = DateUtils.getDate1MinusDate2_Days(ownerDobj.getPurchase_dt(), ownerDobj.getTempReg().getTmp_regn_dt());

                if ("UP".contains(sessionVariables.getStateCodeSelected()) && !ServerUtil.isTransport(ownerDobj.getVh_class(), ownerDobj)
                        && (dtDiff > 6)) {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));

                } else if ("HP,HR,DD,DN,NL,CH".contains(sessionVariables.getStateCodeSelected())) {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
                } else if ("RJ".equals(sessionVariables.getStateCodeSelected()) || "GJ".equals(sessionVariables.getStateCodeSelected())) {
                    if (ownerDobj.getVch_purchase_as().equalsIgnoreCase(TableConstants.PURCHASE_AS_CHASIS)) {
                        if (DateUtils.compareDates(ownerDobj.getTempReg().getTmp_valid_upto(), new Date()) == 2) {
                            taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(new Date()));
                        } else {
                            if ("RJ".equals(sessionVariables.getStateCodeSelected())) {
                                taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(DateUtils.addToDate(ownerDobj.getTempReg().getTmp_valid_upto(), 1, 1)));
                            } else if ("GJ".equals(sessionVariables.getStateCodeSelected())) {
                                if (DateUtils.isBefore(DateUtils.parseDate(ownerDobj.getInsDobj().getIns_from()), DateUtils.parseDate(ownerDobj.getPurchase_dt()))) {
                                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getInsDobj().getIns_from()));
                                } else {
                                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
                                }
                            }
                        }
                    } else {
                        if (sessionVariables.getStateCodeSelected().equals("GJ")) {
                            if (DateUtils.isBefore(DateUtils.parseDate(ownerDobj.getInsDobj().getIns_from()), DateUtils.parseDate(ownerDobj.getPurchase_dt()))) {
                                taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getInsDobj().getIns_from()));
                            }
                        } else {
                            taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
                        }
                    }
                } else if ("OR".equals(sessionVariables.getStateCodeSelected()) && ownerDobj.getVch_purchase_as().equalsIgnoreCase(TableConstants.PURCHASE_AS_FULLY_BODYBUILT) && ownerDobj.getOther_criteria() == 1) {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));

                } else if ("MH".equals(sessionVariables.getStateCodeSelected()) && ownerDobj.getVch_purchase_as().equalsIgnoreCase(TableConstants.PURCHASE_AS_CHASIS)) {
                    /**
                     * For MH,vehicle purchased as Chasi Vehicle tax is
                     * calculated from payment date
                     */
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(new Date()));
                } else if ("AS".equals(sessionVariables.getStateCodeSelected())) {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
                } else if ("BR".equals(sessionVariables.getStateCodeSelected())) {
                    if (DateUtils.compareDates(ownerDobj.getTempReg().getTmp_valid_upto(), new Date()) == 2 || DateUtils.compareDates(ownerDobj.getTempReg().getTmp_valid_upto(), new Date()) == 0) {
                        taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(new Date()));
                    } else {
                        taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
                    }
                } else {
                    if (DateUtils.compareDates(ownerDobj.getTempReg().getTmp_valid_upto(), new Date()) == 2) {
                        taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(new Date()));
                    } else {
                        taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(DateUtils.addToDate(ownerDobj.getTempReg().getTmp_valid_upto(), 1, 1)));
                    }
                }
                if ("ML".contains(sessionVariables.getStateCodeSelected()) && ServerUtil.isTransport(ownerDobj.getVh_class(), ownerDobj)
                        && (taxSelectedFormBean.getPur_cd() == TableConstants.PASSENGER_TAX || taxSelectedFormBean.getPur_cd() == TableConstants.GOODS_TAX)) {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(new Date()));
                }

            } else if (newRegistration && ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)) {
                if ("UP,RJ,MH,JH,PY,AS,KA,KL".contains(sessionVariables.getStateCodeSelected())) {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getOtherStateVehDobj().getNocDate()));
                } else if ("MN".contains(sessionVariables.getStateCodeSelected())) {
                    int vehicleAge = DateUtils.getDate1MinusDate2_Months(ownerDobj.getPurchase_dt(), new Date());
                    if (vehicleAge <= 24) {
                        taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getOtherStateVehDobj().getStateEntryDate()));
                    } else {
                        taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getRegn_upto()));
                    }
                } else {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getOtherStateVehDobj().getStateEntryDate()));
                }

                if (ownerDobj.getRegn_dt() == null) {
                    taxParameters.setREGN_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
                }
                if ("ML".contains(sessionVariables.getStateCodeSelected()) && ServerUtil.isTransport(ownerDobj.getVh_class(), ownerDobj)
                        && (taxSelectedFormBean.getPur_cd() == TableConstants.PASSENGER_TAX || taxSelectedFormBean.getPur_cd() == TableConstants.GOODS_TAX)) {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(new Date()));
                }
            } else if (newRegistration && ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_CD)) {
                if (ownerDobj.getCdDobj() != null) {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getCdDobj().getSaleDate()));
                } else {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(new Date()));
                }
                taxParameters.setREGN_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
            } else if (newRegistration && ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_EXARMY)) {
                if ("RJ".contains(sessionVariables.getStateCodeSelected()) && ownerDobj.getExArmy_dobj() != null) {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getExArmy_dobj().getTf_VoucherDate()));
                } else if ("MH".contains(sessionVariables.getStateCodeSelected()) && ownerDobj.getExArmy_dobj() != null) {
                    taxParameters.setREGN_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
                } else if ("MN".contains(sessionVariables.getStateCodeSelected()) && ownerDobj.getExArmy_dobj() != null) {
                    int vehicleAge = DateUtils.getDate1MinusDate2_Months(ownerDobj.getPurchase_dt(), new Date());
                    if (vehicleAge <= 24) {
                        taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
                    } else {
                        taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(new Date()));
                    }
                }
                if ("ML".contains(sessionVariables.getStateCodeSelected()) && ServerUtil.isTransport(ownerDobj.getVh_class(), ownerDobj)
                        && (taxSelectedFormBean.getPur_cd() == TableConstants.PASSENGER_TAX || taxSelectedFormBean.getPur_cd() == TableConstants.GOODS_TAX)) {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(new Date()));
                }
            } else if (newRegistration && ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_NEW)) {
                if ("GJ".contains(sessionVariables.getStateCodeSelected()) && ownerDobj.getInsDobj() != null) {
                    if (DateUtils.isBefore(DateUtils.parseDate(ownerDobj.getInsDobj().getIns_from()), DateUtils.parseDate(ownerDobj.getPurchase_dt()))) {
                        taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getInsDobj().getIns_from()));
                    }
                }

                if ("PB".contains(sessionVariables.getStateCodeSelected()) && ServerUtil.isTransport(ownerDobj.getVh_class(), ownerDobj) && ownerDobj.getVch_purchase_as().equals("C")) {
                    FitnessDobj fitDobj = new FitnessImpl().set_Fitness_appl_db_to_dobj(null, ownerDobj.getAppl_no());
                    if (fitDobj != null) {
                        taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(fitDobj.getFit_chk_dt()));
                    }
                }

                if ("ML".contains(sessionVariables.getStateCodeSelected()) && ServerUtil.isTransport(ownerDobj.getVh_class(), ownerDobj)
                        && (taxSelectedFormBean.getPur_cd() == TableConstants.PASSENGER_TAX || taxSelectedFormBean.getPur_cd() == TableConstants.GOODS_TAX)) {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getRegn_dt()));
                }
            } else if (newRegistration && ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                if ("PB".contains(sessionVariables.getStateCodeSelected())) {
                    String prevRegno = ownerDobj.getRegn_no();
                    ownerDobj.setRegn_no(ownerDobj.getOtherStateVehDobj().getOldRegnNo());
                    Date taxDueFrom = TaxServer_Impl.getTaxDueFromDate(ownerDobj, taxSelectedFormBean.getPur_cd());
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(taxDueFrom));
                    ownerDobj.setRegn_no(prevRegno);
                }
                if ("ML".contains(sessionVariables.getStateCodeSelected()) && ServerUtil.isTransport(ownerDobj.getVh_class(), ownerDobj)
                        && (taxSelectedFormBean.getPur_cd() == TableConstants.PASSENGER_TAX || taxSelectedFormBean.getPur_cd() == TableConstants.GOODS_TAX)) {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(new Date()));
                }
            } else if (newRegistration && ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_CONFISCATED_AUCTION)) {
                if (ownerDobj.getAuctionDobj() != null) {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getAuctionDobj().getAuctionDate()));
                }
            }

            if ("JH".contains(sessionVariables.getStateCodeSelected()) && newRegistration
                    && (ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_TEMPORARY)
                    || ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_NEW))
                    && ownerDobj.getOwner_cd() == 1 && ownerDobj.getVh_class() == 7) {
                int panCount = OwnerImpl.checkPanNoCount(ownerDobj.getOwner_identity().getPan_no(), ownerDobj.getOwner_identity().getState_cd());
                if (panCount > 0) {
                    taxParameters.setOTHER_CRITERIA(99);
                }
            }

            if (renderPermitPanel && permitPanelBean.getPermitDobj() != null) {
                permitTaxDtl = permitPanelBean.getPermitDobj().toString();
            }
            if (tempRegistration && "OR".contains(sessionVariables.getStateCodeSelected())) {
                taxParameters.setOTHER_CRITERIA(99);
                if (ownerDobj.getTempReg() == null) {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
                } else if (ownerDobj.getTempReg() != null && DateUtils.compareDates(ownerDobj.getTempReg().getTmp_valid_upto(), new Date()) == 2) {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getTempReg().getTmp_valid_upto()));
                } else {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(DateUtils.addToDate(ownerDobj.getTempReg().getTmp_valid_upto(), 1, 1)));
                }
            } else if (tempRegistration && "WB,CG".contains(sessionVariables.getStateCodeSelected())) {
                if (ownerDobj.getTempReg() == null) {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
                } else if (ownerDobj.getTempReg() != null) {
                    taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(DateUtils.addToDate(ownerDobj.getTempReg().getTmp_valid_upto(), 1, 1)));
                }
            }
            taxParameters.setTRANSACTION_PUR_CD(getPur_cd());
//   CALL KERALA TAX SERVICE WITH SOME MORE PARAMETER                  
            List<DOTaxDetail> listTaxBreakUp;
            if (taxParameters.getSTATE_CD().equals("KL")) {
                int pushbackseat = 0;
                int ordinaryseat = 0;
                if (TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(ownerDobj.getVh_class() + ","))) {
                    pushbackseat = ownerDobj.getPush_bk_seat();
                    ordinaryseat = ownerDobj.getOrdinary_seat();
                }
//                taxParameters.setNEWVCH("Y");
//                taxParameters.setREGNTYPE("N");
                listTaxBreakUp = callKLTaxService(taxParameters, pushbackseat, ordinaryseat, ownerDobj.getRegn_no(), ownerDobj.getChasi_no());
            } else {
                listTaxBreakUp = callTaxService(taxParameters);
            }
            listTaxBreakUp = TaxUtils.sortTaxDetails(listTaxBreakUp);
            if (taxSelectedFormBean.getTaxMode().equals("O") || taxSelectedFormBean.getTaxMode().equals("L")
                    || taxSelectedFormBean.getTaxMode().equals("S")) {
                listTaxBreakUp.get(listTaxBreakUp.size() - 1).setTAX_UPTO(null);
            }

            if (ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                    || ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_CD)) {

                if (taxSelectedFormBean.getTaxMode().equals("Y10") || (taxSelectedFormBean.getTaxMode().equals("Y12") && taxParameters.getSTATE_CD().equals("JH"))
                        || taxSelectedFormBean.getTaxMode().equals("Y15") || taxSelectedFormBean.getTaxMode().equals("L")
                        || taxSelectedFormBean.getTaxMode().equals("O")
                        || taxSelectedFormBean.getTaxMode().equals("S")) {
                    if (taxSelectedFormBean.getTaxMode().equals("O") || taxSelectedFormBean.getTaxMode().equals("L")
                            || taxSelectedFormBean.getTaxMode().equals("S")) {
                        listTaxBreakUp.get(listTaxBreakUp.size() - 1).setTAX_UPTO(null);
                    } else {
                        Date dt = otherStateTaxUpto(ownerDobj.getPurchase_dt(), taxSelectedFormBean.getTaxMode(), sessionVariables.getStateCodeSelected());
                        listTaxBreakUp.get(listTaxBreakUp.size() - 1).setTAX_UPTO(DateUtils.parseDate(dt));
                    }
                }
            }

            if (ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_EXARMY)
                    && taxParameters.getSTATE_CD().equals("MN")) {
                int vehicleAge = DateUtils.getDate1MinusDate2_Months(ownerDobj.getPurchase_dt(), new Date());
                if (taxSelectedFormBean.getPur_cd() != TableConstants.TM_ADDN_ROAD_TAX && vehicleAge >= 15) {
                    Date dt = exArmyTaxUpto(ownerDobj.getPurchase_dt(), taxSelectedFormBean.getTaxMode(), sessionVariables.getStateCodeSelected());
                    if (dt != null) {
                        listTaxBreakUp.get(listTaxBreakUp.size() - 1).setTAX_UPTO(ServerUtil.parseDateToString(dt));
                    }
                }
            }

            taxSelectedFormBean.setTaxDescriptionList(listTaxBreakUp);
            taxSelectedFormBean.updateTaxBean();
            if ("KA".contains(Util.getUserStateCode())) {
                for (FeeDobj fee : feeCollectionLists) {
                    if (fee.getPurCd().equals(TableConstants.VM_TRANSACTION_MAST_TEMP_REG) || fee.getPurCd().equals(TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE)) {
                        if (fee.getFeeAmount() == 50) {
                            if (listTaxBreakUp != null && !listTaxBreakUp.isEmpty()) {
                                taxSelectedFormBean.setFinalTaxUpto(ServerUtil.parseDateToString(DateUtils.addToDate(taxSelectedFormBean.getFinalTaxFrom(), 2, 1)));
                                taxSelectedFormBean.setFinalTaxUpto(ServerUtil.parseDateToString(DateUtils.addToDate(taxSelectedFormBean.getFinalTaxUpto(), 1, -1)));
                                listTaxBreakUp.get(listTaxBreakUp.size() - 1).setTAX_UPTO(taxSelectedFormBean.getFinalTaxUpto());
                            }
                        }
                    }
                }
            }
            updateTotalPayableAmount();
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void isViewIsRendered() {
        String errorMsg = "";
        try {
            if (ownerDobj.getRqrd_tax_modes() != null && !ownerDobj.getRqrd_tax_modes().isEmpty()) {
                VahanTaxParameters taxParameters = fillTaxParametersFromDobj(ownerDobj, permitPanelBean.getPermitDobj());
                Map<Integer, String> rqrdTaxModes = NewImpl.getRqrdTaxModes(ownerDobj.getRqrd_tax_modes());
                long netTaxAmount = 0;
                String roadTaxMod = null;
                if (rqrdTaxModes != null && !rqrdTaxModes.isEmpty()) {

                    for (Map.Entry<Integer, String> entry : rqrdTaxModes.entrySet()) {

                        TaxFormPanelBean taxSelectedFormBean = new TaxFormPanelBean();
                        taxSelectedFormBean.setPur_cd(entry.getKey());
                        List listTaxModes = new ArrayList();
                        listTaxModes.add(new SelectItem(entry.getValue(), ServerUtil.getTaxModeDescr(entry.getValue())));
                        taxSelectedFormBean.setTaxPurcdDesc(ServerUtil.getTaxHead(entry.getKey()));
                        taxSelectedFormBean.setListTaxModes(listTaxModes);
                        getListTaxForm().add(taxSelectedFormBean);
                        String taxCalc = permitPanelBean.getPermitDobj() != null ? permitPanelBean.getPermitDobj().toString() : "";
                        taxCalc = taxCalc + " : " + ownerDobj.getSale_amt() + " : " + ownerDobj.getOther_criteria();
                        taxSelectedFormBean.setTaxCalcBasedOnParms(taxCalc);
                        taxSelectedFormBean.setTaxMode(entry.getValue());
                        if (entry.getKey() == 70 && "MH".contains(sessionVariables.getStateCodeSelected())) {
                            taxParameters.setNET_TAX_AMT((double) netTaxAmount);
                            taxParameters.setTAX_MODE(roadTaxMod);
                            taxSelectedFormBean.setTaxMode(roadTaxMod);

                            if (!"OLS".contains(roadTaxMod)) {
                                taxSelectedFormBean.setTaxMode("Y");
                                taxSelectedFormBean.setPur_cd(58);
                                updateSelectedTaxFormBean(taxSelectedFormBean, taxParameters);
                                netTaxAmount = taxSelectedFormBean.getTotalPaybaleTax();
                                taxParameters.setNET_TAX_AMT((double) netTaxAmount);
                                taxSelectedFormBean.setPur_cd(70);
                            }

                            updateSelectedTaxFormBean(taxSelectedFormBean, taxParameters);

                            if (!"OLS".contains(roadTaxMod)) {
                                DOTaxDetail dOTaxDetail = new DOTaxDetail();
                                dOTaxDetail.setAMOUNT((double) taxSelectedFormBean.getTotalPaybaleTax());
                                dOTaxDetail.setAMOUNT1((double) taxSelectedFormBean.getTotalTax1());
                                dOTaxDetail.setAMOUNT2((double) taxSelectedFormBean.getTotalTax2());
                                dOTaxDetail.setSURCHARGE((double) taxSelectedFormBean.getTotalPaybaleSurcharge());
                                dOTaxDetail.setINTEREST((double) taxSelectedFormBean.getTotalPaybaleInterest());
                                dOTaxDetail.setREBATE((double) taxSelectedFormBean.getTotalPaybaleRebate());
                                dOTaxDetail.setPENALTY((double) taxSelectedFormBean.getTotalPaybalePenalty());
                                dOTaxDetail.setPRV_ADJ(taxSelectedFormBean.getTotalPayablePrvAdj());
                                dOTaxDetail.setTAX_FROM(taxSelectedFormBean.getFinalTaxFrom());
                                dOTaxDetail.setPUR_CD(entry.getKey());
                                taxSelectedFormBean.getTaxDescriptionList().clear();
                                taxSelectedFormBean.getTaxDescriptionList().add(dOTaxDetail);
                                taxSelectedFormBean.setTaxMode(entry.getValue());
                                taxSelectedFormBean.updateTaxBean();
                                updateTotalPayableAmount();
                            }
                        } else {
                            updateSelectedTaxFormBean(taxSelectedFormBean, taxParameters);
                        }
                        if (entry.getKey() == 58 && "MH".contains(sessionVariables.getStateCodeSelected())) {
                            netTaxAmount = taxSelectedFormBean.getTotalPaybaleTax();
                            roadTaxMod = taxSelectedFormBean.getTaxMode();
                        }
                    }
                }

                listTaxFormBackup = new ArrayList<>(getListTaxForm());
            }
        } catch (VahanException ve) {
            errorMsg = ve.getMessage();
        } catch (Exception e) {
            errorMsg = "Error in Calulating the MV Tax Amount.";
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        if (errorMsg != null && !errorMsg.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMsg, errorMsg));
        }
        autoRunTaxListener = false;
    }

    public void taxExemptionListener(AjaxBehaviorEvent event) {
        if ("KL".equals(Util.getUserStateCode())) {
            if (taxExemption) {
                TaxFormPanelBean txF = new TaxFormPanelBean(58);
                listTaxForm.remove(txF);
            } else {
                listTaxForm = listTaxFormBackup;
            }
            if (listTaxForm == null || listTaxForm.isEmpty()) {
                setRenderTaxPanel(false);
            } else {
                setRenderTaxPanel(true);
            }

        } else {

            if (taxExemption) {
                setRenderTaxPanel(false);
            } else {
                setRenderTaxPanel(true);
            }
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

    public Date exArmyTaxUpto(Date uptoDate, String taxMode, String stateCd) throws Exception {
        int intMonth = 0;
        int intDays = 0;
        int minModePeriod = 1;
        try {
            minModePeriod = Integer.parseInt(taxMode.substring(1));
        } catch (Exception e) {
            minModePeriod = 1;
        }
        Date retDate = uptoDate;
        Date retDate1 = uptoDate;
        taxMode = taxMode.substring(0, 1);

        if (taxMode.equals("Y")) {
            intMonth = 12 * minModePeriod;
        } else if (taxMode.equals("Q")) {
            intMonth = 3 * minModePeriod;
        } else if (taxMode.equals("M")) {
            intMonth = 1 * minModePeriod;
        } else if (taxMode.equals("W")) {
            intDays = 7 * minModePeriod;
        } else if (taxMode.equals("L") || taxMode.equals("O")) {
            intMonth = 15 * 12;
        }

        if (intMonth > 0) {
            retDate1 = DateUtils.addToDate(retDate1, 2, intMonth);
            retDate1 = DateUtils.addToDate(retDate1, 1, -1);
        } else if (intDays > 0) {
            retDate1 = DateUtils.addToDate(retDate1, 1, intDays);
            retDate1 = DateUtils.addToDate(retDate1, 1, -1);
        }
        // After completed 15yrs in MN 5yr Added in UPTO date //
        if (",MN,".contains("," + stateCd + ",") && taxMode.equals("Y") && minModePeriod == 15
                || taxMode.equalsIgnoreCase("O")
                || taxMode.equalsIgnoreCase("L")) {
            for (int i = 1; i > 0; i++) {
                int compareDate = DateUtils.compareDates(retDate1, new Date());
                if (compareDate == 0 || compareDate == 1) {
                    intMonth = 5 * 12;
                    retDate1 = DateUtils.addToDate(retDate1, 2, intMonth);
                    retDate1 = DateUtils.addToDate(retDate1, 1, -1);
                } else {
                    break;
                }
            }
            retDate = retDate1;
        }
        return retDate;
    }

    public Date otherStateTaxUpto(Date uptoDate, String taxMode, String stateCd) throws Exception {
        int intMonth = 0;
        int intDays = 0;
        int minModePeriod = 1;
        try {
            minModePeriod = Integer.parseInt(taxMode.substring(1));
        } catch (Exception e) {
            minModePeriod = 1;
        }
        Date retDate = uptoDate;
        taxMode = taxMode.substring(0, 1);

        if (taxMode.equals("Y")) {
            intMonth = 12 * minModePeriod;
        } else if (taxMode.equals("Q")) {
            intMonth = 3 * minModePeriod;
        } else if (taxMode.equals("M")) {
            intMonth = 1 * minModePeriod;
        } else if (taxMode.equals("W")) {
            intDays = 7 * minModePeriod;
        } else if (taxMode.equals("L") || taxMode.equals("O")) {
            intMonth = 15 * 12;
        }

        if (intMonth > 0) {
            retDate = DateUtils.addToDate(retDate, 2, intMonth);
            retDate = DateUtils.addToDate(retDate, 1, -1);
        } else if (intDays > 0) {
            retDate = DateUtils.addToDate(retDate, 1, intDays);
            retDate = DateUtils.addToDate(retDate, 1, -1);
        }
        // After completed 15yrs in AS 5yr Added in UPTO date //
        if (",BR,JH,AS,TR,CH,NL,".contains("," + stateCd + ",") && taxMode.equals("Y") && minModePeriod == 15 || taxMode.equalsIgnoreCase("O") || taxMode.equalsIgnoreCase("L")) {
            for (int i = 1; i > 0; i++) {
                int compareDate = DateUtils.compareDates(retDate, new Date());
                if (compareDate == 0 || compareDate == 1) {
                    intMonth = 5 * 12;
                    retDate = DateUtils.addToDate(retDate, 2, intMonth);
                    retDate = DateUtils.addToDate(retDate, 1, -1);
                } else {
                    break;
                }
            }
        }

        return retDate;
    }

    public void refreshPermit() {
        Exception e = null;
        try {
            if (renderPermitPanel && permitPanelBean.getPermitDobj() != null) {
                permitRefreshDtl = permitPanelBean.getPermitDobj().toString();
                setRenderFeePanelLabel(true);
                getFeeCollectionLists().clear();
                getListTaxForm().clear();
                updateForNewRegAppl(applno);
                // by Afzal
                setFancyOrRetenRegnMess(returnFancyOrRetenDtld(applno));
                if (getFancyOrRetenRegnMess() != null) {
                    setIsSeriesAvail(false);
                    setIsFancyOrReten(true);
                } else {
                    if (permitPanelBean.getPermitDobj().getPmt_type_code() != null
                            && !permitPanelBean.getPermitDobj().getPmt_type_code().equals("")) {

                        ownerDobj.setPmt_type(Integer.parseInt(permitPanelBean.getPermitDobj().getPmt_type_code()));

                        if (permitPanelBean.getPermitDobj().getPmtCatg() != null && !permitPanelBean.getPermitDobj().getPmtCatg().equals("")) {
                            ownerDobj.setPmt_catg(Integer.parseInt(permitPanelBean.getPermitDobj().getPmtCatg()));
                        }
                        if (permitPanelBean.getPermitDobj().getServices_TYPE() != null && !permitPanelBean.getPermitDobj().getServices_TYPE().equals("")) {
                            ownerDobj.setServicesType(permitPanelBean.getPermitDobj().getServices_TYPE().trim());
                        }
                        VehicleParameters parameters = fillVehicleParametersFromDobj(ownerDobj, permitPanelBean.getPermitDobj());
                        setSeriesAvailMess(ServerUtil.getAvailablePrefixSeries(parameters));
                        if (!getSeriesAvailMess().equals(TableConstants.SERIES_EXHAUST_MESSAGE) && !getSeriesAvailMess().equals("")) {
                            setSeriesAvailMess("Vehicle Registration No will be Generated from the Series " + getSeriesAvailMess() + ".");
                            setIsSeriesAvail(true);
                            setIsFancyOrReten(false);
                        } else {
                            setSeriesAvailMess("Vehicle Registration No Series not Available.");
                            setIsSeriesAvail(true);
                            setIsFancyOrReten(false);
                        }
                    } else {
                        //setSeriesAvailMess("Vehicle Registration No will be Generated from the Series.");
                        setIsSeriesAvail(false);
                        setIsFancyOrReten(false);
                    }
                }

                if (getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DUP_RC
                        || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_CHG_ADD || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_TO
                        || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_REM_HYPO
                        || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_FRESH_RC
                        || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_REN_REG || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_VEH_ALTER
                        || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {

                    if ((Util.getSelectedSeat().getAction_cd() != TableConstants.TM_ROLE_NEW_REGISTRATION_FIT_FEE || Util.getSelectedSeat().getAction_cd() != TableConstants.TM_ROLE_DEALER_NEW_REGISTRATION_FIT_FEE)
                            && feeImpl.verifySmartCard()) {
                        setSmartCardChrg(EpayImpl.getPurposeCodeFee(ownerDobj, ownerDobj.getVh_class(), 80, ownerDobj.getVch_catg()));
                        if (getSmartCardChrg() > 0l) {
                            setRenderSmartCardFeePanel(true);
                            setSmartCardFee(getSmartCardChrg());
                            updateTotalPayableAmount();
                        }
                    }

                } else {
                    setRenderSmartCardFeePanel(false);
                }

            }
        } catch (VahanException ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, ex.getMessage(), ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            e = ex;
        } catch (Exception ex) {
            e = ex;
        }

        if (e != null) {
            pageLoadingFailed = e.getMessage();
            // RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("new_veh_fee_subview:pageLoadingFailed");
            PrimeFaces.current().executeScript("PF('dlgPageLoadingFailed').show()");
        }
    }

    public void refreshPermitRest() {
        String baseUrl = VahanUtil.getBaseUrl();
        Exception e = null;
        try {
            if (renderPermitPanel && permitPanelBean.getPermitDobj() != null) {
                permitRefreshDtl = permitPanelBean.getPermitDobj().toString();
                setRenderFeePanelLabel(true);
                getFeeCollectionLists().clear();
                getListTaxForm().clear();
                updateForNewRegAppl(applno);
                // by Afzal
                setFancyOrRetenRegnMess(returnFancyOrRetenDtld(applno));
                if (getFancyOrRetenRegnMess() != null) {
                    setIsSeriesAvail(false);
                    setIsFancyOrReten(true);
                } else {
                    if (permitPanelBean.getPermitDobj().getPmt_type_code() != null
                            && !permitPanelBean.getPermitDobj().getPmt_type_code().equals("")) {

                        ownerDobj.setPmt_type(Integer.parseInt(permitPanelBean.getPermitDobj().getPmt_type_code()));

                        if (permitPanelBean.getPermitDobj().getPmtCatg() != null && !permitPanelBean.getPermitDobj().getPmtCatg().equals("")) {
                            ownerDobj.setPmt_catg(Integer.parseInt(permitPanelBean.getPermitDobj().getPmtCatg()));
                        }
                        if (permitPanelBean.getPermitDobj().getServices_TYPE() != null && !permitPanelBean.getPermitDobj().getServices_TYPE().equals("")) {
                            ownerDobj.setServicesType(permitPanelBean.getPermitDobj().getServices_TYPE().trim());
                        }
//                        VehicleParameters parameters = fillVehicleParametersFromDobj(ownerDobj, permitPanelBean.getPermitDobj());
//                        setSeriesAvailMess(ServerUtil.getAvailablePrefixSeries(parameters));

                        WrapperModel wrapperModel = new WrapperModel();
                        wrapperModel.setSessionVariables(new SessionVariablesModel(this.getSessionVariables()));
                        wrapperModel.setNewVehicleFeeBeanModel(new NewVehicleFeeBeanModel(this));

                        String restUrl = baseUrl + "/fee/refreshPermit1";
                        String seriesAvailMessage = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .build()
                                .post()
                                .uri(restUrl)
                                .body(Mono.just(wrapperModel), WrapperModel.class)
                                .retrieve()
                                .bodyToMono(String.class)
                                .block();

                        if (seriesAvailMessage == null) {
                            throw new VahanException("Something went wrong when trying to fetch data from REST API");
                        }
                        setSeriesAvailMess(seriesAvailMessage);

                        if (!getSeriesAvailMess().equals(TableConstants.SERIES_EXHAUST_MESSAGE) && !getSeriesAvailMess().equals("")) {
                            setSeriesAvailMess("Vehicle Registration No will be Generated from the Series " + getSeriesAvailMess() + ".");
                            setIsSeriesAvail(true);
                            setIsFancyOrReten(false);
                        } else {
                            setSeriesAvailMess("Vehicle Registration No Series not Available.");
                            setIsSeriesAvail(true);
                            setIsFancyOrReten(false);
                        }
                    } else {
                        //setSeriesAvailMess("Vehicle Registration No will be Generated from the Series.");
                        setIsSeriesAvail(false);
                        setIsFancyOrReten(false);
                    }
                }

                if (getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DUP_RC
                        || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_CHG_ADD || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_TO
                        || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_REM_HYPO
                        || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_FRESH_RC
                        || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_REN_REG || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_VEH_ALTER
                        || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO || getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {

//                    if ((Util.getSelectedSeat().getAction_cd() != TableConstants.TM_ROLE_NEW_REGISTRATION_FIT_FEE || Util.getSelectedSeat().getAction_cd() != TableConstants.TM_ROLE_DEALER_NEW_REGISTRATION_FIT_FEE)
//                            && feeImpl.verifySmartCard()) {
//                        setSmartCardChrg(EpayImpl.getPurposeCodeFee(ownerDobj, ownerDobj.getVh_class(), 80, ownerDobj.getVch_catg()));
//                        if (getSmartCardChrg() > 0l) {
//                            setRenderSmartCardFeePanel(true);
//                            setSmartCardFee(getSmartCardChrg());
//                            updateTotalPayableAmount();
//                        }
//                    }
                    WrapperModel wrapperModel = new WrapperModel();
                    wrapperModel.setSessionVariables(new SessionVariablesModel(this.getSessionVariables()));
                    wrapperModel.setNewVehicleFeeBeanModel(new NewVehicleFeeBeanModel(this));

                    String restUrl = baseUrl + "/fee/refreshPermit2";
                    Long smarCardChrg = 0L;
                    try {
                        smarCardChrg = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .build()
                                .post()
                                .uri(restUrl)
                                .body(Mono.just(wrapperModel), WrapperModel.class)
                                .retrieve()
                                .bodyToMono(Long.class)
                                .block();
                        setSmartCardChrg(smarCardChrg);
                        if (smarCardChrg >= 01) {
                            setRenderSmartCardFeePanel(true);
                            setSmartCardFee(getSmartCardChrg());
                            updateTotalPayableAmount();
                        }
                    } catch (WebClientResponseException ex) {
                        LOGGER.error(e.getMessage());
                    } catch (Exception ex) {
                        LOGGER.error(e.getMessage());
                    }
                } else {
                    setRenderSmartCardFeePanel(false);
                }

            }
        } catch (VahanException ex) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, ex.getMessage(), ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            e = ex;
        } catch (Exception ex) {
            e = ex;
        }

        if (e != null) {
            pageLoadingFailed = e.getMessage();
            // RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("new_veh_fee_subview:pageLoadingFailed");
            PrimeFaces.current().executeScript("PF('dlgPageLoadingFailed').show()");
        }
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
                        setBtn_print(false);
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

    public void getBreakUpDetailsByPurpose(TaxFormPanelBean bean) {
        if (taxDescriptionList != null) {
            taxDescriptionList.clear();
        }
        taxDescriptionList = bean.getTaxDescriptionList();
        // RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().ajax().update("new_veh_fee_subview:tabViewId:sub_view_tax_dtls:opBreakupDetails");
        PrimeFaces.current().executeScript("PF('dia_tax_breakup').show()");
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

    public void calculateTotalTemp(AjaxBehaviorEvent event) {
        listTaxForm.clear();
        isViewIsRendered();
        calculateTotal();
        //RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().ajax().update("new_veh_fee_subview:ownerDetailsPanel");

    }

    /**
     * @return the pageLoadingFailed
     */
    public String getPageLoadingFailed() {
        return pageLoadingFailed;
    }

    /**
     * @param pageLoadingFailed the pageLoadingFailed to set
     */
    public void setPageLoadingFailed(String pageLoadingFailed) {
        this.pageLoadingFailed = pageLoadingFailed;
    }

    /**
     * @return the renderTaxPanel
     */
    public boolean isRenderTaxPanel() {
        return renderTaxPanel;
    }

    /**
     * @param renderTaxPanel the renderTaxPanel to set
     */
    public void setRenderTaxPanel(boolean renderTaxPanel) {
        this.renderTaxPanel = renderTaxPanel;
    }

    /**
     * @return the list_vm_catg
     */
    public List getList_vm_catg() {
        return list_vm_catg;
    }

    /**
     * @param list_vm_catg the list_vm_catg to set
     */
    public void setList_vm_catg(List list_vm_catg) {
        this.list_vm_catg = list_vm_catg;
    }

    /**
     * @return the list_maker_model
     */
    public List getList_maker_model() {
        return list_maker_model;
    }

    /**
     * @param list_maker_model the list_maker_model to set
     */
    public void setList_maker_model(List list_maker_model) {
        this.list_maker_model = list_maker_model;
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
     * @return the taxBean
     */
    public TaxFormPanelBean getTaxBean() {
        return taxBean;
    }

    /**
     * @param taxBean the taxBean to set
     */
    public void setTaxBean(TaxFormPanelBean taxBean) {
        this.taxBean = taxBean;
    }

    /**
     * @return the taxpanel
     */
    public String getTaxpanel() {
        return taxpanel;
    }

    /**
     * @param taxpanel the taxpanel to set
     */
    public void setTaxpanel(String taxpanel) {
        this.taxpanel = taxpanel;
    }

    /**
     * @return the printConfDlgTax
     */
    public String getPrintConfDlgTax() {
        return printConfDlgTax;
    }

    /**
     * @param printConfDlgTax the printConfDlgTax to set
     */
    public void setPrintConfDlgTax(String printConfDlgTax) {
        this.printConfDlgTax = printConfDlgTax;
    }

    /**
     * @return the receiptStickyNote
     */
    public String getReceiptStickyNote() {
        return receiptStickyNote;
    }

    /**
     * @param receiptStickyNote the receiptStickyNote to set
     */
    public void setReceiptStickyNote(String receiptStickyNote) {
        this.receiptStickyNote = receiptStickyNote;
    }

    /**
     * @return the buttonPanelVisibility
     */
    public boolean isButtonPanelVisibility() {
        return buttonPanelVisibility;
    }

    /**
     * @param buttonPanelVisibility the buttonPanelVisibility to set
     */
    public void setButtonPanelVisibility(boolean buttonPanelVisibility) {
        this.buttonPanelVisibility = buttonPanelVisibility;
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
     * @return the feeCollectionLists
     */
    public List<FeeDobj> getFeeCollectionLists() {
        return feeCollectionLists;
    }

    /**
     * @param feeCollectionLists the feeCollectionLists to set
     */
    public void setFeeCollectionLists(List<FeeDobj> feeCollectionLists) {
        this.feeCollectionLists = feeCollectionLists;
    }

    /**
     * @return the btn_print
     */
    public boolean isBtn_print() {
        return btn_print;
    }

    /**
     * @param btn_print the btn_print to set
     */
    public void setBtn_print(boolean btn_print) {
        this.btn_print = btn_print;
    }

    /**
     * @return the addToCartStatusCount
     */
    public List<PaymentGatewayDobj> getAddToCartStatusCount() {
        return addToCartStatusCount;
    }

    /**
     * @param addToCartStatusCount the addToCartStatusCount to set
     */
    public void setAddToCartStatusCount(List<PaymentGatewayDobj> addToCartStatusCount) {
        this.addToCartStatusCount = addToCartStatusCount;
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
     * @return the applno
     */
    public String getApplno() {
        return applno;
    }

    /**
     * @param applno the applno to set
     */
    public void setApplno(String applno) {
        this.applno = applno;
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
     * @return the rendersmartCardFeePanel
     */
    public boolean isRenderSmartCardFeePanel() {
        return renderSmartCardFeePanel;
    }

    /**
     * @param rendersmartCardFeePanel the rendersmartCardFeePanel to set
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
     * @return the actionCode
     */
    public int getActionCode() {
        return actionCode;
    }

    /**
     * @param actionCode the actionCode to set
     */
    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
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
     * @return the blnDisableRegnTypeTemp
     */
    public boolean isBlnDisableRegnTypeTemp() {
        return blnDisableRegnTypeTemp;
    }

    /**
     * @param blnDisableRegnTypeTemp the blnDisableRegnTypeTemp to set
     */
    public void setBlnDisableRegnTypeTemp(boolean blnDisableRegnTypeTemp) {
        this.blnDisableRegnTypeTemp = blnDisableRegnTypeTemp;
    }

    /**
     * @return the renderTempregPanel
     */
    public boolean isRenderTempregPanel() {
        return renderTempregPanel;
    }

    /**
     * @param renderTempregPanel the renderTempregPanel to set
     */
    public void setRenderTempregPanel(boolean renderTempregPanel) {
        this.renderTempregPanel = renderTempregPanel;
    }

    /**
     * @return the tempReg
     */
    public TempRegDobj getTempReg() {
        return tempReg;
    }

    /**
     * @param tempReg the tempReg to set
     */
    public void setTempReg(TempRegDobj tempReg) {
        this.tempReg = tempReg;
    }

    /**
     * @return the currentDate
     */
    public Date getCurrentDate() {
        return currentDate;
    }

    /**
     * @param currentDate the currentDate to set
     */
    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
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
     * @return the list_office_to
     */
    public List getList_office_to() {
        return list_office_to;
    }

    /**
     * @param list_office_to the list_office_to to set
     */
    public void setList_office_to(List list_office_to) {
        this.list_office_to = list_office_to;
    }

    /**
     * @return the list_dealer_cd
     */
    public List getList_dealer_cd() {
        return list_dealer_cd;
    }

    /**
     * @param list_dealer_cd the list_dealer_cd to set
     */
    public void setList_dealer_cd(List list_dealer_cd) {
        this.list_dealer_cd = list_dealer_cd;
    }

    /**
     * @return the otherStateVehDobj
     */
    public OtherStateVehDobj getOtherStateVehDobj() {
        return otherStateVehDobj;
    }

    /**
     * @param otherStateVehDobj the otherStateVehDobj to set
     */
    public void setOtherStateVehDobj(OtherStateVehDobj otherStateVehDobj) {
        this.otherStateVehDobj = otherStateVehDobj;
    }

    /**
     * @return the disableOtherState
     */
    public boolean isDisableOtherState() {
        return disableOtherState;
    }

    /**
     * @param disableOtherState the disableOtherState to set
     */
    public void setDisableOtherState(boolean disableOtherState) {
        this.disableOtherState = disableOtherState;
    }

    /**
     * @return the ownerDetailsDobj
     */
    public OwnerDetailsDobj getOwnerDetailsDobj() {
        return ownerDetailsDobj;
    }

    /**
     * @param ownerDetailsDobj the ownerDetailsDobj to set
     */
    public void setOwnerDetailsDobj(OwnerDetailsDobj ownerDetailsDobj) {
        this.ownerDetailsDobj = ownerDetailsDobj;
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
     * @return the renderCdPanel
     */
    public boolean isRenderCdPanel() {
        return renderCdPanel;
    }

    /**
     * @param renderCdPanel the renderCdPanel to set
     */
    public void setRenderCdPanel(boolean renderCdPanel) {
        this.renderCdPanel = renderCdPanel;
    }

    /**
     * @return the btn_print_label
     */
    public String getBtn_print_label() {
        return btn_print_label;
    }

    /**
     * @param btn_print_label the btn_print_label to set
     */
    public void setBtn_print_label(String btn_print_label) {
        this.btn_print_label = btn_print_label;
    }

    /**
     * @return the seriesAvailMess
     */
    public String getSeriesAvailMess() {
        return seriesAvailMess;
    }

    /**
     * @param seriesAvailMess the seriesAvailMess to set
     */
    public void setSeriesAvailMess(String seriesAvailMess) {
        this.seriesAvailMess = seriesAvailMess;
    }

    /**
     * @return the isSeriesAvail
     */
    public boolean isIsSeriesAvail() {
        return isSeriesAvail;
    }

    /**
     * @param isSeriesAvail the isSeriesAvail to set
     */
    public void setIsSeriesAvail(boolean isSeriesAvail) {
        this.isSeriesAvail = isSeriesAvail;
    }

    /**
     * @return the fancyOrRetenRegnMess
     */
    public String getFancyOrRetenRegnMess() {
        return fancyOrRetenRegnMess;
    }

    /**
     * @param fancyOrRetenRegnMess the fancyOrRetenRegnMess to set
     */
    public void setFancyOrRetenRegnMess(String fancyOrRetenRegnMess) {
        this.fancyOrRetenRegnMess = fancyOrRetenRegnMess;
    }

    /**
     * @return the isFancyOrReten
     */
    public boolean isIsFancyOrReten() {
        return isFancyOrReten;
    }

    /**
     * @param isFancyOrReten the isFancyOrReten to set
     */
    public void setIsFancyOrReten(boolean isFancyOrReten) {
        this.isFancyOrReten = isFancyOrReten;
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

    /**
     * @return the renderTaxInstallment
     */
    public boolean isRenderTaxInstallment() {
        return renderTaxInstallment;
    }

    /**
     * @param renderTaxInstallment the renderTaxInstallment to set
     */
    public void setRenderTaxInstallment(boolean renderTaxInstallment) {
        this.renderTaxInstallment = renderTaxInstallment;
    }

    /**
     * @return the taxInstallment
     */
    public boolean isTaxInstallment() {
        return taxInstallment;
    }

    /**
     * @param taxInstallment the taxInstallment to set
     */
    public void setTaxInstallment(boolean taxInstallment) {
        this.taxInstallment = taxInstallment;
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
     * @return the disableSaveButton
     */
    public boolean isDisableSaveButton() {
        return disableSaveButton;
    }

    /**
     * @param disableSaveButton the disableSaveButton to set
     */
    public void setDisableSaveButton(boolean disableSaveButton) {
        this.disableSaveButton = disableSaveButton;
    }

    /**
     * @return the disableRevertBackButton
     */
    public boolean isDisableRevertBackButton() {
        return disableRevertBackButton;
    }

    /**
     * @param disableRevertBackButton the disableRevertBackButton to set
     */
    public void setDisableRevertBackButton(boolean disableRevertBackButton) {
        this.disableRevertBackButton = disableRevertBackButton;
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
     * @return the btnAddToCart
     */
    public boolean isBtnAddToCart() {
        return btnAddToCart;
    }

    /**
     * @param btnAddToCart the btnAddToCart to set
     */
    public void setBtnAddToCart(boolean btnAddToCart) {
        this.btnAddToCart = btnAddToCart;
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
     * @return the renderModelCost
     */
    public boolean isRenderModelCost() {
        return renderModelCost;
    }

    /**
     * @param renderModelCost the renderModelCost to set
     */
    public void setRenderModelCost(boolean renderModelCost) {
        this.renderModelCost = renderModelCost;
    }

    public boolean isRenderCancelPayment() {
        return renderCancelPayment;
    }

    public void setRenderCancelPayment(boolean renderCancelPayment) {
        this.renderCancelPayment = renderCancelPayment;
    }

    public boolean isRenderUserAndPasswored() {
        return renderUserAndPasswored;
    }

    public void setRenderUserAndPasswored(boolean renderUserAndPasswored) {
        this.renderUserAndPasswored = renderUserAndPasswored;
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

    public List<DOTaxDetail> getTaxDescriptionList() {
        return taxDescriptionList;
    }

    public void setTaxDescriptionList(List<DOTaxDetail> taxDescriptionList) {
        this.taxDescriptionList = taxDescriptionList;
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
     * @return the confirmDialogRenderButton
     */
    public boolean isConfirmDialogRenderButton() {
        return confirmDialogRenderButton;
    }

    /**
     * @param confirmDialogRenderButton the confirmDialogRenderButton to set
     */
    public void setConfirmDialogRenderButton(boolean confirmDialogRenderButton) {
        this.confirmDialogRenderButton = confirmDialogRenderButton;
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

    public boolean isRenderPushBackSeatPanel() {
        return renderPushBackSeatPanel;
    }

    public void setRenderPushBackSeatPanel(boolean renderPushBackSeatPanel) {
        this.renderPushBackSeatPanel = renderPushBackSeatPanel;
    }

    /**
     * @return the listTrailerDobj
     */
    public List<Trailer_dobj> getListTrailerDobj() {
        return listTrailerDobj;
    }

    /**
     * @param listTrailerDobj the listTrailerDobj to set
     */
    public void setListTrailerDobj(List<Trailer_dobj> listTrailerDobj) {
        this.listTrailerDobj = listTrailerDobj;
    }

    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    public List<ComparisonBean> compareChagnes() throws VahanException {
        PassengerPermitDetailDobj permitDobjCom = permitPanelBean.getPermitDobjPrev();
        if (permitDobjCom == null) {
            return compBeanList;
        }
        compBeanList.clear();
        Compare("Permit Type", permitDobjCom.getPmt_type(), ownerDobj.getPmt_type() + "", compBeanList);
        Compare("Permit Catg", permitDobjCom.getPmtCatg(), ownerDobj.getPmt_catg() + "", compBeanList);
        Compare("Permit ServiceType", permitDobjCom.getServices_TYPE(), ownerDobj.getServicesType(), compBeanList);
        Compare("Permit RegionCovered", permitDobjCom.getRegionCoveredArr(), ownerDobj.getRegion_covered(), compBeanList);
        return compBeanList;
    }

    /**
     * @return the marqueManualReceipt
     */
    public boolean isMarqueManualReceipt() {
        return marqueManualReceipt;
    }

    /**
     * @param marqueManualReceipt the marqueManualReceipt to set
     */
    public void setMarqueManualReceipt(boolean marqueManualReceipt) {
        this.marqueManualReceipt = marqueManualReceipt;
    }

    /**
     * @return the renderManualReceiptMessage
     */
    public boolean isRenderManualReceiptMessage() {
        return renderManualReceiptMessage;
    }

    /**
     * @param renderManualReceiptMessage the renderManualReceiptMessage to set
     */
    public void setRenderManualReceiptMessage(boolean renderManualReceiptMessage) {
        this.renderManualReceiptMessage = renderManualReceiptMessage;
    }

    /**
     * @return the manualRcptDobjTemp
     */
    public ManualReceiptEntryDobj getManualRcptDobjTemp() {
        return manualRcptDobjTemp;
    }

    /**
     * @param manualRcptDobjTemp the manualRcptDobjTemp to set
     */
    public void setManualRcptDobjTemp(ManualReceiptEntryDobj manualRcptDobjTemp) {
        this.manualRcptDobjTemp = manualRcptDobjTemp;
    }

    public FeeImpl getFeeImpl() {
        return feeImpl;
    }

    public void setFeeImpl(FeeImpl feeImpl) {
        this.feeImpl = feeImpl;
    }

    public OwnerImpl getOwnerImpl() {
        return ownerImpl;
    }

    public void setOwnerImpl(OwnerImpl ownerImpl) {
        this.ownerImpl = ownerImpl;
    }

    public List<TaxFormPanelBean> getListTaxFormBackup() {
        return listTaxFormBackup;
    }

    public void setListTaxFormBackup(List<TaxFormPanelBean> listTaxFormBackup) {
        this.listTaxFormBackup = listTaxFormBackup;
    }

    public FeeDraftimpl getFeeDraftImpl() {
        return feeDraftImpl;
    }

    public void setFeeDraftImpl(FeeDraftimpl feeDraftImpl) {
        this.feeDraftImpl = feeDraftImpl;
    }

    public PermitImpl getPermitImpl() {
        return permitImpl;
    }

    public void setPermitImpl(PermitImpl permitImpl) {
        this.permitImpl = permitImpl;
    }

    public String getGeneratedRcpt() {
        return generatedRcpt;
    }

    public void setGeneratedRcpt(String generatedRcpt) {
        this.generatedRcpt = generatedRcpt;
    }

    public String getPrintRcptNo() {
        return printRcptNo;
    }

    public void setPrintRcptNo(String printRcptNo) {
        this.printRcptNo = printRcptNo;
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public String getFEE_ACTION() {
        return FEE_ACTION;
    }

    public void setFEE_ACTION(String FEE_ACTION) {
        this.FEE_ACTION = FEE_ACTION;
    }

    public String getPermitRefreshDtl() {
        return permitRefreshDtl;
    }

    public void setPermitRefreshDtl(String permitRefreshDtl) {
        this.permitRefreshDtl = permitRefreshDtl;
    }

    public String getPermitTaxDtl() {
        return permitTaxDtl;
    }

    public void setPermitTaxDtl(String permitTaxDtl) {
        this.permitTaxDtl = permitTaxDtl;
    }

    public String getPermitFeeDtl() {
        return permitFeeDtl;
    }

    public void setPermitFeeDtl(String permitFeeDtl) {
        this.permitFeeDtl = permitFeeDtl;
    }

    public boolean isIsManualReceiptRecord() {
        return isManualReceiptRecord;
    }

    public void setIsManualReceiptRecord(boolean isManualReceiptRecord) {
        this.isManualReceiptRecord = isManualReceiptRecord;
    }

    public SessionVariables getSessionVariables() {
        return sessionVariables;
    }

    public void setSessionVariables(SessionVariables sessionVariables) {
        this.sessionVariables = sessionVariables;
    }

    public String getAppl_dt() {
        return appl_dt;
    }

    public void setAppl_dt(String appl_dt) {
        this.appl_dt = appl_dt;
    }
}
