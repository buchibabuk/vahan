/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import nic.java.util.DateUtils;
import nic.java.util.DateUtilsException;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.bean.fancy.RetenRegnNo_dobj;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.ConvDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.fancy.AdvanceRegnNo_dobj;
import nic.vahan.form.impl.AxleImpl;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.ConvImpl;
import nic.vahan.form.impl.NewImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.RegVehCancelReceiptImpl;
import nic.vahan.form.impl.TaxServer_Impl;
import nic.vahan.form.impl.ToImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
//import org.primefaces.context.RequestContext;
import org.primefaces.PrimeFaces;

/**
 *
 * @author tranC103
 */
@ManagedBean
@ViewScoped
public class ConvBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(ConvBean.class);
    private String masterLayout = "/masterLayoutPage_new.xhtml";
    private List vh_type = new ArrayList();
    private List vh_class = new ArrayList();
    private List vh_category = new ArrayList();
    private List listReason = new ArrayList();
    private List taxDueFromList = new ArrayList();
    private ConvDobj conv_dobj = null;
    private ConvDobj conv_dobj_prev;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<ComparisonBean> prevChangedDataList;
    private String reasonSelected;
    private boolean reasonDisable = true;
    private int vehType;
    private String vh_catg;
    private int vh_cls;
    private Date fit_upto;
    private String reasonPlaceholder = "";
    private boolean disable;
    private boolean disableExcessAmt = false;
    private Date currentDate = DateUtil.parseDate(DateUtil.getCurrentDate());
    private Date maxDate = new Date();
    private Date taxPaidUpto;
    private Date taxPaidFrom;
    private long taxAmtPaid;
    private String taxMode;
    private long excessAmt = 0;
    private boolean taxPaidOrClearUsingNocDate;
    private Map listTaxModes = new LinkedHashMap();
    private String selectedNewTaxDueFrom;
    private List listOtherCriteria = new ArrayList();
    private String vahanMessages = null;
    private boolean axleDetail_Visibility_tab = false;
    AxleDetailsDobj axleDobj = null;
    @ManagedProperty(value = "#{axleBean}")
    private AxleBean axleBean;
    private boolean disableVehType = true;
    private boolean retAllowed = false;
    private boolean retCheck = false;
    private boolean disableRetCheck = false;
    private boolean advRetCheckDialogue = false;
    private RetenRegnNo_dobj retenRegNoDobj = new RetenRegnNo_dobj();
    private List list_adv_district = new ArrayList();
    private String regnNumberAllotedMsg = "";
    private boolean advRegnCheck = false;
    private boolean advRegnCheckDialogue = false;
    private AdvanceRegnNo_dobj advRegnNoDobj = new AdvanceRegnNo_dobj();
    private boolean disableAdvRegnCheck = false;
    private List list_c_state = new ArrayList();
    private boolean enableTaxClearUpto = true;
    private String fitnessValidLabel;
    private boolean fancyRetention;
    private String selectedFancyRetnetion;
    private List templistTaxDueFrom;
    private boolean taxDueFrom = false;
    private boolean nexSeriesAvail;
    private Owner_dobj ownerDobj = null;
    private boolean permitPanel = false;
    private boolean isPmtTypeRqrd;
    private boolean isPmtCatgRqrd;
    int oldSeriesId = 0;
    private OwnerDetailsDobj OwnerDetailsDobj = null;
    private boolean pushBkSeatRender = false;
    private boolean renderMultiRegionList = false;
    private String retRcptMsg;
    private ConvDobj conv_dobj_inward = null;
    private boolean inwardDetailsDisable = true;

    public ConvBean() {
        vh_type.add(new SelectItem("1", "Transport"));
        vh_type.add(new SelectItem("2", "Non-Transport"));
    }

    @PostConstruct
    public void init() {
        listReason.add(new SelectItem("-1", "-SELECT-"));
        listReason.add(new SelectItem("Govt Order/Notification", "Govt Order/Notification"));
        listReason.add(new SelectItem("Other State Vehicle", "Other State Vehicle"));
        listReason.add(new SelectItem("DTO ORDER", "DTO ORDER"));
        listReason.add(new SelectItem("Other Case", "Other Case"));

        ConvImpl convImpl = new ConvImpl();
        OwnerImpl impl = new OwnerImpl();
        String[][] data;
        try {
            if (getAppl_details() != null) {
                if (appl_details.getCurrent_state_cd() == null || appl_details.getCurrent_off_cd() == 0) {
                    vahanMessages = "Something went wrong, Please try after sometime...";
                    return;
                }
                conv_dobj = ConvImpl.set_Conversion_appl_db_to_dobj(getAppl_details().getAppl_no(), null);
                if (appl_details.getOwnerDetailsDobj() == null) {
                    OwnerDetailsDobj = impl.getOwnerDetails(conv_dobj.getOld_regn_no(), appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd());
                    ownerDobj = impl.getOwnerDobj(OwnerDetailsDobj);
                    if (appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_CONV_APPROVAL) {
                        appl_details.setChasi_no(ownerDobj.getChasi_no());
                        appl_details.setOwn_name(ownerDobj.getOwner_name());
                    }
                    if (OwnerDetailsDobj == null) {
                        vahanMessages = "Owner Details not Found in the Database, Please Contact to the System Administrator";
                        return;
                    }
                } else {
                    OwnerDetailsDobj = appl_details.getOwnerDetailsDobj();
                    ownerDobj = appl_details.getOwnerDobj();
                }
                templistTaxDueFrom = new ArrayList();
//                ownerDobj = appl_details.getOwnerDobj();
                data = MasterTableFiller.masterTables.VM_TAXDUE_FROM.getData();
                for (int i = 0; i < data.length; i++) {
                    SelectItem si = new SelectItem(data[i][0], data[i][1]);
                    taxDueFromList.add(si);
                    if (!taxDueFrom && data[i][2] != null && data[i][2].contains("," + appl_details.getCurrent_state_cd() + ",")) {
                        taxDueFrom = true;
                    }
                    if (data[i][2] == null || !data[i][2].contains("," + appl_details.getCurrent_state_cd() + ",")) {
                        templistTaxDueFrom.add(si);
                    }
                }
                if (taxDueFrom) {
                    taxDueFromList.removeAll(templistTaxDueFrom);
                }
                list_c_state = MasterTableFiller.getStateList();
                setListTaxModes(ServerUtil.getTaxModeList());
                listOtherCriteria = MasterTableFiller.getOtherCriteriaList(appl_details.getCurrent_state_cd());
                Object[] currentData = ConvImpl.present_technicalDetail(getAppl_details().getRegn_no());
                fit_upto = JSFUtils.getStringToDateyyyyMMdd(OwnerDetailsDobj.getFit_upto());
                vh_cls = OwnerDetailsDobj.getVh_class();
                vh_catg = OwnerDetailsDobj.getVch_catg();
                if (currentData != null && !currentData[0].toString().isEmpty()) {
                    currentdata.put("Vehicle Current Technical Detail", (String) currentData[0]);
                } else {
                    String prevDetails = "Vehicle Class [" + OwnerDetailsDobj.getVh_class_desc() + "], Vehicle Category [" + vh_catg + "], Fitment Date Upto [" + OwnerDetailsDobj.getFit_upto() + "]";
                    prevDetails = prevDetails.replace(",", "&nbsp; <font color=\"red\">|</font> &nbsp;");
                    currentdata.put("Vehicle Current Technical Detail", prevDetails);
                }
                procesConversionDetails(vh_cls, vh_catg);
                if (vehType == TableConstants.VM_VEHTYPE_TRANSPORT) {
                    maxDate = ServerUtil.dateRange(currentDate, 0, 1, 0);
                    fitnessValidLabel = "New Fitness Validity From:";
                } else {
                    maxDate = null;
                    fitnessValidLabel = "New Fitness Validity Upto:";
                }
//                conv_dobj = ConvImpl.set_Conversion_appl_db_to_dobj(getAppl_details().getAppl_no(), null);
//                enableTaxClearUpto = convImpl.isTaxCollectedWithConv(Util.getUserStateCode(), TableConstants.CONV_ACTION, TableConstants.TM_ROAD_TAX);
                if (conv_dobj != null) {
                    conv_dobj.setPurchaseDate(JSFUtils.getStringToDateyyyyMMdd(OwnerDetailsDobj.getPurchase_dt()));
                    reasonSelected = conv_dobj.getPerm_by();
                    if (!CommonUtils.isNullOrBlank(reasonSelected)) {
                        if (!reasonSelected.equalsIgnoreCase("Govt Order/Notification")
                                && !reasonSelected.equalsIgnoreCase("Other State Vehicle")
                                && !reasonSelected.equalsIgnoreCase("DTO ORDER")) {
                            reasonSelected = "Other Case";
                        }
                    }
                    if (!conv_dobj.getNew_vch_catg().isEmpty()) {
                        vehClassListener();
                    }
                    selectedNewTaxDueFrom = DateUtils.getDateInDDMMYYYY(conv_dobj.getNewTaxDueFrom());
                    conv_dobj_prev = (ConvDobj) conv_dobj.clone();
                } else {
                    conv_dobj = new ConvDobj();
                    conv_dobj.setAppl_no(getAppl_details().getAppl_no());
                    conv_dobj.setOld_regn_no(getAppl_details().getRegn_no());
                    conv_dobj.setOld_vch_class(vh_cls);
                    conv_dobj.setOld_vch_catg(vh_catg);
                    conv_dobj.setOld_fit_dt(fit_upto);
                    conv_dobj.setPurchaseDate(JSFUtils.getStringToDateyyyyMMdd(OwnerDetailsDobj.getPurchase_dt()));
                    conv_dobj_inward = ConvImpl.set_ConversionInward_appl_db_to_dobj(getAppl_details().getAppl_no(), null);
                    if (conv_dobj_inward != null) {
                        conv_dobj.setNew_vch_class(conv_dobj_inward.getNew_vch_class());
                        vehClassListener();
                        conv_dobj.setNew_vch_catg(conv_dobj_inward.getNew_vch_catg());

                    } else {
                        if (appl_details.getCurrent_action_cd() == TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION_ENTRY) {
                            inwardDetailsDisable = false;
                        }
                    }
                    if (conv_dobj_inward != null && !ServerUtil.isTransport(conv_dobj_inward.getNew_vch_class(), null)) {
                        conv_dobj.setNew_fit_dt(fit_upto);
                    }
                    if ("PB".contains(getAppl_details().getCurrent_state_cd()) && ServerUtil.isTransport(vh_cls, null)) {
                        Date fitUpto = convImpl.prevConvDtlsNtToTr(getAppl_details().getRegn_no(), getAppl_details().getCurrent_off_cd(), getAppl_details().getCurrent_state_cd());
                        if (fitUpto != null) {
                            conv_dobj.setNew_fit_dt(fitUpto);
                            setMaxDate(fitUpto);
                        }
                    }
                    conv_dobj_prev = (ConvDobj) conv_dobj.clone();
                }
                if (maxDate != null && conv_dobj.getNew_fit_dt() != null && maxDate.before(conv_dobj.getNew_fit_dt())
                        && !ServerUtil.isTransport(conv_dobj.getOld_vch_class(), null)
                        && !ServerUtil.isTransport(conv_dobj.getNew_vch_class(), null)) {
                    maxDate = ServerUtil.dateRange(conv_dobj.getNew_fit_dt(), 0, 0, 0);
                }
                ToImpl to_Impl = new ToImpl();
                TmConfigurationDobj tmConfig = Util.getTmConfiguration();
                if (appl_details.getCurrent_action_cd() == TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION_ENTRY) {
                    if (tmConfig != null && tmConfig.isTax_adjustment()) {
                        excessAmt = processTaxDetails(currentData, getSurchargeAmount(currentData, tmConfig.getTax_adjustment_with_surcharge()));
                        conv_dobj.setExcessAmt(excessAmt);
                        setDisableExcessAmt(true);
                    } else {
                        setDisableExcessAmt(false);
                        conv_dobj.setExcessAmt(0);
                    }
                }
                if (appl_details.getCurrent_state_cd().equalsIgnoreCase("JH")) {
                    if (vehType == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                        Date newFitupto = DateUtils.addToDate(getAppl_details().getOwnerDobj().getRegn_dt(), 3, 15);
                        if (DateUtils.isAfter(DateUtils.parseDate(newFitupto), DateUtils.getCurrentDate())) {
                            conv_dobj.setNew_fit_dt(ServerUtil.dateRange(newFitupto, 0, 0, -1));
                        }
                    }

                }
                if (tmConfig == null) {
                    tmConfig = ServerUtil.getTmConfigurationParameters(appl_details.getCurrent_state_cd());
                }
                if (tmConfig != null && tmConfig.isReassign_retained_no_with_conv()) {
                    setRetAllowed(true);
                }
                if (tmConfig != null) {
                    if (tmConfig.isHold_regnNo_with_conversion() && (to_Impl.isFancyNo(appl_details.getRegn_no()) || tmConfig.isTo_retention_for_all_regn())) {
                        fancyRetention = true;
                    }
                    if (fancyRetention && to_Impl.isSurrenderRetention(appl_details.getAppl_no())) {
                        selectedFancyRetnetion = "YES";
                    } else if (fancyRetention && appl_details.getCurrent_role() != TableConstants.TM_ROLE_ENTRY) {
                        selectedFancyRetnetion = "NO";
                    }
                    boolean NumGenrateWithTOReRegCon = to_Impl.isNumGenrateWithTOReRegCon(conv_dobj.getAppl_no(), TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION);
                    if ((tmConfig.isHold_regnNo_with_conversion() && (to_Impl.isFancyNo(appl_details.getRegn_no()) || tmConfig.isTo_retention_for_all_regn()))) {
                        if (NumGenrateWithTOReRegCon) {
                            if (convImpl.isNumRetentionWithTOConv(appl_details.getAppl_no(), appl_details.getCurrent_off_cd(), appl_details.getCurrent_state_cd())) {
                                selectedFancyRetnetion = "YES";
                            }
                        }
                    }
                }
                if ("HP,CH,RJ".contains(appl_details.getCurrent_state_cd())) {
                    RegVehCancelReceiptImpl regVehRcptImpl = new RegVehCancelReceiptImpl();
                    VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
                    oldSeriesId = regVehRcptImpl.getSeriesIdFromVmSeries(vehParameters);//appl_details.getOwnerDobj()
                    ownerDobj.setPmt_catg(conv_dobj.getPmt_catg());
                    ownerDobj.setPmt_type(conv_dobj.getPmt_type());
                    ownerDobj.setServicesType(String.valueOf(conv_dobj.getServiceType()));
                    permitTypeChangeListener();
                    if (appl_details.getCurrent_action_cd() != TableConstants.CONVERSION_OF_VEHICLE_ENTRY) {
                        permitPanel = false;
                    }
                }
                int action_cd = appl_details.getCurrent_action_cd();
                if (action_cd == TableConstants.TM_ROLE_CONV_VERIFICATION
                        || action_cd == TableConstants.TM_ROLE_CONV_APPROVAL) {
                    if (convImpl.checkReAssignRegnFee(conv_dobj)) {
                        VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
                        vehParameters.setVCH_TYPE(vehType);
                        vehParameters.setVH_CLASS(conv_dobj.getNew_vch_class());
                        vehParameters.setVCH_CATG(conv_dobj.getNew_vch_catg());
                        String seriesAvailMessage = ServerUtil.getAvailablePrefixSeries(vehParameters);
                        if (!seriesAvailMessage.equals(TableConstants.SERIES_EXHAUST_MESSAGE) && !seriesAvailMessage.equals("")) {
                            seriesAvailMessage = "Vehicle Registration No will be Generated from the Series " + seriesAvailMessage + ".";
                            setRegnNumberAllotedMsg(seriesAvailMessage);
                        }
                    }
                    if ("HP,CH,RJ".contains(appl_details.getCurrent_state_cd()) && !conv_dobj.isNextSeriesGen()) {
                        String seriesAvailMessage = "Vehicle Registration No will not be Changed.";
                        setRegnNumberAllotedMsg(seriesAvailMessage);
                    }

                    setDisable(true);
                    String regnNoAllotted = NewImpl.getAdvanceRetenNo(appl_details.getAppl_no());
                    if (regnNoAllotted != null && !regnNoAllotted.isEmpty()) {
                        setRegnNumberAllotedMsg("Vehicle Registration No " + regnNoAllotted + " will allot.");
                        setRetCheck(true);
                        conv_dobj.setAssignRetainNo(true);
                        conv_dobj.setAssignRetainRegnNo(regnNoAllotted);
                    } else {
                        regnNoAllotted = NewImpl.getAdvanceRegnNo(appl_details.getAppl_no());
                        if (regnNoAllotted != null && !regnNoAllotted.isEmpty()) {
                            setRegnNumberAllotedMsg("Vehicle Registration No " + regnNoAllotted + " will allot.");
                            conv_dobj.setAssignFancyRegnNumber(regnNoAllotted);
                            conv_dobj.setAssignFancyNumber(true);
                            setAdvRegnCheck(true);
                        }
                    }
                    if (appl_details.getCurrent_role() == TableConstants.TM_ROLE_VERIFICATION && CommonUtils.isNullOrBlank(regnNoAllotted)) {
                        setDisableAdvRegnCheck(false);
                        setDisableRetCheck(false);
                    }
                    if (appl_details.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL) {
                        setDisableRetCheck(true);
                        setDisableAdvRegnCheck(true);
                    }
                }
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
//                taxPaidOrClearUsingNocDate = convImpl.taxPaidOrClearedStatusUsingNOC(getAppl_details().getRegn_no(), getAppl_details().getOwnerDobj().getOwner_cd());
//                if (!taxPaidOrClearUsingNocDate) {
//                    String facesMessages = "Tax is not Paid by this Registration No. Please Clear the tax first.";
//                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, facesMessages, facesMessages));
//                }

                processAxleDetails();
            }
        } catch (VahanException vex) {
            vahanMessages = vex.getMessage();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            vahanMessages = "Something Went Wrong, Please Contact to the System Administrator";
        }
    }

    private void processAxleDetails() {
        if (vehType == TableConstants.VM_VEHTYPE_TRANSPORT) {
            axleDetail_Visibility_tab = true;
            axleDobj = AxleImpl.setAxleVehDetails_db_to_dobj(getAppl_details().getAppl_no(), null, appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd());
            if (axleDobj != null) {
                axleBean.setDobj_To_Bean(axleDobj);
                axleBean.setAxle_dobj_prv(axleDobj);
            } else {
                axleDobj = AxleImpl.setAxleVehDetails_db_to_dobj(null, getAppl_details().getRegn_no(), appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd());
                if (axleDobj != null) {
                    axleBean.setDobj_To_Bean(axleDobj);
                    axleBean.setAxle_dobj_prv(axleDobj);
                }
            }
        } else {
            axleDetail_Visibility_tab = false;
        }
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
        try {
            String twoWheelerVhClass = ",1,2,3,4,51,52,53,";
            List vh_category_temp = new ArrayList();
            int vehClass = conv_dobj.getNew_vch_class();
            String oldVhClass = "," + String.valueOf(ownerDobj.getVh_class()) + ",";
            if (ServerUtil.isTransport(vehClass, null)) {
                setVehType(TableConstants.VM_VEHTYPE_TRANSPORT);
                setAxleDetail_Visibility_tab(true);
                if ("HP,CH,RJ".contains(appl_details.getCurrent_state_cd())) {
                    permitPanel = true;
                }
            } else {
                setVehType(TableConstants.VM_VEHTYPE_NON_TRANSPORT);
                setAxleDetail_Visibility_tab(false);
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
            if (permitPanel && "HP,CH,RJ".contains(appl_details.getCurrent_state_cd())) {
                //for checking mandatory fields for permit type and permit category
                String taxRqrdField[] = ServerUtil.getFieldsReqForTax(appl_details.getCurrent_state_cd(), vehClass);
                if (taxRqrdField != null && taxRqrdField.length > 0) {
                    isPmtTypeRqrd = false;
                    setIsPmtCatgRqrd(false);
                    for (int i = 0; i < taxRqrdField.length; i++) {
                        if (taxRqrdField[i].equalsIgnoreCase(TableConstants.VM_PMT_TYPE_IN_TAX_CODE)) {
                            isPmtTypeRqrd = true;
                        }
                        if (taxRqrdField[i].equalsIgnoreCase(TableConstants.VM_PMT_CATG_IN_TAX_CODE)) {
                            isPmtTypeRqrd = true;
                            setIsPmtCatgRqrd(true);
                            break;
                        }
                    }
                }

                //############################Filteration of Permit Type Start###############
                String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
                String transportCatg = null;
                for (int i = 0; i < data.length; i++) {
                    if (data[i][0].equalsIgnoreCase(String.valueOf(vehClass))) {
                        transportCatg = data[i][3];
                        break;
                    }
                }

                if (transportCatg != null) {
                    data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
                    ownerDobj.getPermitTypeList().clear();
                    ownerDobj.getPermitCategoryList().clear();
                    for (int i = 0; i < data.length; i++) {
                        if (data[i][2].equalsIgnoreCase(transportCatg)) {
                            ownerDobj.getPermitTypeList().add(new SelectItem(data[i][0], data[i][1]));
                        }
                    }
                }
                //permitTypeChangeListener();
                //Add for permit service
                // pmt_service_type_list = new ArrayList();
                ownerDobj.getPmtServiceTypeList().clear();
                String[][] data1 = MasterTableFiller.masterTables.vm_service_type.getData();
                ownerDobj.getPmtServiceTypeList().add(new SelectItem("", ""));
                for (int i = 0; i < data1.length; i++) {
                    ownerDobj.getPmtServiceTypeList().add(new SelectItem(data1[i][0], data1[i][1]));
                }
            }
            if (Util.getUserStateCode().equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(conv_dobj.getNew_vch_class()) + ",")) {
                setPushBkSeatRender(true);
            } else {
                setPushBkSeatRender(false);
                ownerDobj.setPush_bk_seat(0);
                ownerDobj.setOrdinary_seat(0);

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
        } catch (VahanException ex) {
            //LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void permitTypeChangeListener() {
        try {
//        if (ownerDobj.getPmt_type() > 0) {
            ownerDobj.getPermitCategoryList().clear();
            String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
            for (int j = 0; j < data.length; j++) {
                if (data[j][0].equalsIgnoreCase(appl_details.getCurrent_state_cd())
                        && Integer.parseInt(data[j][3]) == ownerDobj.getPmt_type()) {
                    ownerDobj.getPermitCategoryList().add(new SelectItem(data[j][1], data[j][2]));
                }
            }
            if ("HP,CH,RJ".contains(appl_details.getCurrent_state_cd())) {
                conv_dobj.setPmt_catg(ownerDobj.getPmt_catg());
                conv_dobj.setPmt_type(ownerDobj.getPmt_type());
                conv_dobj.setServiceType(Integer.parseInt(ownerDobj.getServicesType()));
            }
            nexSeriesAvail = numberGenration(oldSeriesId);
            conv_dobj.setNextSeriesGen(nexSeriesAvail);
            //  }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void validatePermitType(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (permitPanel && value != null) {
            FacesMessage msg = null;
            if (isPmtTypeRqrd && Integer.parseInt(value.toString()) <= 0) {
                msg = new FacesMessage("Invalid Permit Type, Please Select Valid Permit Type.");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
    }

    public void validatePermitCatg(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (permitPanel && value != null) {
            FacesMessage msg = null;
            if (isPmtTypeRqrd && Integer.parseInt(value.toString()) < 0) {
                msg = new FacesMessage("Invalid Permit Category, Please Select Valid Permit Category.");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
    }

    public void reasonChangeListener() {
        if (!reasonSelected.equals("Other Case") && !reasonSelected.equals("-1")) {
            conv_dobj.setPerm_by(reasonSelected);
            reasonDisable = true;
        } else {
            reasonDisable = false;
            reasonPlaceholder = "Enter Reason Here.";
            conv_dobj.setPerm_by(null);
        }
    }

    public void newTaxDueFromListener() {
        if (!conv_dobj.getNewTaxDueFromFLag().isEmpty()) {
            if (conv_dobj.getNewTaxDueFromFLag().equalsIgnoreCase("R")) {
                conv_dobj.setNewTaxDueFrom(getAppl_details().getOwnerDobj().getRegn_dt());
            } else if (conv_dobj.getNewTaxDueFromFLag().equalsIgnoreCase("C")) {
                conv_dobj.setNewTaxDueFrom(JSFUtils.getStringToDateddMMMyyyy(getAppl_details().getAppl_dt()));
            } else if (conv_dobj.getNewTaxDueFromFLag().equalsIgnoreCase("P")) {
                conv_dobj.setNewTaxDueFrom(getAppl_details().getOwnerDobj().getPurchase_dt());
            } else if (conv_dobj.getNewTaxDueFromFLag().equalsIgnoreCase("T")) {
                conv_dobj.setNewTaxDueFrom(TaxServer_Impl.getTaxDueFromDate(getAppl_details().getOwnerDobj(), TableConstants.TM_ROAD_TAX));
            }
            selectedNewTaxDueFrom = DateUtils.getDateInDDMMYYYY(conv_dobj.getNewTaxDueFrom());
            if (conv_dobj.getNewTaxDueFromFLag().equalsIgnoreCase("E")) {
                conv_dobj.setNewTaxDueFrom(JSFUtils.getStringToDateddMMMyyyy(getAppl_details().getAppl_dt()));
                selectedNewTaxDueFrom = "Tax Exemted or OTT/LTT already paid.";
            }
            if (conv_dobj.getNewTaxDueFromFLag().equalsIgnoreCase("U")) {
                enableTaxClearUpto = false;
                conv_dobj.setNewTaxDueFrom(conv_dobj.getNewTaxPaidUpto());
            } else {
                enableTaxClearUpto = true;
                conv_dobj.setNewTaxPaidUpto(null);
            }
        }
    }

    public void retCheckListener() {
        if (retCheck) {
            setAdvRetCheckDialogue(true);
            setAdvRegnCheckDialogue(false);
        }
    }

    public void advanceSaveListener() {
        if (getRetenRegNoDobj().getRegn_no() != null) {
            conv_dobj.setAssignRetainNo(true);
            conv_dobj.setAssignRetainRegnNo(getRetenRegNoDobj().getRegn_no());
        } else if (getAdvRegnNoDobj().getRegn_no() != null) {
            conv_dobj.setNew_regn_no(getAdvRegnNoDobj().getRegn_no());
            setDisableRetCheck(true);
            conv_dobj.setAssignFancyNumber(true);
            conv_dobj.setAssignFancyRegnNumber(getAdvRegnNoDobj().getRegn_no());
        } else {
            setAdvRegnNoDobj(null);
            setRetenRegNoDobj(null);
            setAdvRegnCheck(false);
            setRetCheck(false);
        }
    }

    public void advanceExitListener() {
        if (advRegnCheck) {
            setAdvRegnNoDobj(null);
            setAdvRegnCheck(false);
        }
        if (retCheck) {
            setRetenRegNoDobj(null);
            setRetCheck(false);
        }

    }

    public void advanceRcptListener() {
        try {
            if (retCheck) {
                String rcptno = getRetenRegNoDobj().getRecp_no();
                if (rcptno == null || rcptno.isEmpty()) {
                    return;
                }
                Date rcptDate = NewImpl.getRetainNoRcptDate(rcptno, appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd());
                if (rcptDate != null) {
                    if ("SK".contains(appl_details.getCurrent_state_cd())) {
                        if (ownerDobj.getOwner_cd() == TableConstants.VEH_TYPE_GOVT || ownerDobj.getOwner_cd() == TableConstants.VEH_TYPE_STATE_GOVT || ownerDobj.getOwner_cd() == TableConstants.VEH_TYPE_GOVT_UNDERTAKING) {
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
                    if (!OwnerDetailsDobj.getOwner_name().equalsIgnoreCase(dobj.getOwner_name())
                            || !OwnerDetailsDobj.getF_name().equalsIgnoreCase(dobj.getF_name())) {
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
                    if (!OwnerDetailsDobj.getOwner_name().equalsIgnoreCase(dobj.getOwner_name())
                            || !OwnerDetailsDobj.getF_name().equalsIgnoreCase(dobj.getF_name())) {
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

    @Override
    public List<ComparisonBean> compareChanges() {
        if (conv_dobj_prev == null) {
            return getCompBeanList();
        }
        getCompBeanList().clear();
        Compare("vh_class", conv_dobj_prev.getOld_vch_class(), conv_dobj.getNew_vch_class(), (ArrayList) getCompBeanList());
        Compare("vh_category", conv_dobj_prev.getNew_vch_catg(), conv_dobj.getNew_vch_catg(), (ArrayList) getCompBeanList());
        if (conv_dobj_prev.getNew_fit_dt() != null) {
            Compare("fit_date", conv_dobj_prev.getNew_fit_dt(), conv_dobj.getNew_fit_dt(), (ArrayList) getCompBeanList());
        }
        Compare("perm_ref_no", conv_dobj_prev.getPerm_ref_no(), conv_dobj.getPerm_ref_no(), (ArrayList) getCompBeanList());
        if (conv_dobj_prev.getPerm_dt() != null) {
            Compare("perm_dt", conv_dobj_prev.getPerm_dt(), conv_dobj.getPerm_dt(), (ArrayList) getCompBeanList());
        }
        Compare("perm_by", conv_dobj_prev.getPerm_by(), conv_dobj.getPerm_by(), (ArrayList) getCompBeanList());
        if (conv_dobj_prev.getNewTaxPaidUpto() != null) {
            Compare("New_Tax_Paid_Upto", conv_dobj_prev.getNewTaxPaidUpto(), conv_dobj.getNewTaxPaidUpto(), (ArrayList) getCompBeanList());
        }
        Compare("Excess Amount", conv_dobj_prev.getExcessAmt(), conv_dobj.getExcessAmt(), (ArrayList) getCompBeanList());
//        if (conv_dobj_prev.getNewTaxPaidUpto() != null) {
//            Compare("New_Tax_Due_From", conv_dobj_prev.getNewTaxDueFrom(), conv_dobj.getNewTaxDueFrom(), (ArrayList) getCompBeanList());
//        }
        Compare("New Tax Due From Flag", conv_dobj_prev.getNewTaxDueFromFLag(), conv_dobj.getNewTaxDueFromFLag(), (ArrayList) getCompBeanList());
        Compare("Tax Mode", conv_dobj_prev.getNewTaxMode(), conv_dobj.getNewTaxMode(), (ArrayList) getCompBeanList());
        return getCompBeanList();
    }

    @Override
    public String save() {
        String return_location = "";
        String axelChangedData = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            Date applDate = format.parse(appl_details.getAppl_dt());
            if (appl_details.getCurrent_role() == TableConstants.TM_ROLE_ENTRY) {
//                if (!taxPaidOrClearUsingNocDate) {
//                    String facesMessages = "Tax is not Paid by this Registration No. Please Clear the tax first.";
//                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, facesMessages, facesMessages));
//                    return return_location;
//                }
            }
            if (isAxleDetail_Visibility_tab()) {
                axleDobj = axleBean.setBean_To_Dobj();
                axelChangedData = ComparisonBeanImpl.changedDataContents(axleBean.compareChagnes());
            }
            List<ComparisonBean> compareChanges = compareChanges();
            if (!compareChanges.isEmpty() || conv_dobj_prev == null || !axelChangedData.isEmpty()) { //save only when data is really changed by user
                ConvImpl.makeChangeConv(conv_dobj, axleDobj, ComparisonBeanImpl.changedDataContents(compareChanges), axelChangedData, selectedFancyRetnetion, applDate);
            }
            return_location = "seatwork";

        } catch (VahanException vme) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), vme.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
        }
        return return_location;
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        try {
//            if (appl_details.getCurrent_role() == TableConstants.TM_ROLE_ENTRY) {
//                if (!taxPaidOrClearUsingNocDate) {
//                    String facesMessages = "Tax is not Paid by this Registration No. Please Clear the tax first.";
//                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, facesMessages, facesMessages));
//                    return return_location;
//                }
//            }

            if (isAxleDetail_Visibility_tab()) {
                axleDobj = axleBean.setBean_To_Dobj();
            }
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());

            status.setCurrent_role(appl_details.getCurrent_role());
            if ("HP,CH,RJ".contains(appl_details.getCurrent_state_cd())) {
                conv_dobj.setPmt_catg(ownerDobj.getPmt_catg());
                conv_dobj.setServiceType(Integer.parseInt(ownerDobj.getServicesType()));
            }

            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                    || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)
                    || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                ConvImpl impl = new ConvImpl();
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE) && this.appl_details.isDocVerifyForFacelessAppl()) {
                    String[] notVerifiedDocDetails = ServerUtil.verifyDocUploaded(appl_details.getAppl_no(), getAppl_details().getCurrent_action_cd());
                    if (notVerifiedDocDetails != null) {
                        appl_details.setNotVerifiedDocdetails(notVerifiedDocDetails[1]);
                        throw new VahanException(notVerifiedDocDetails[0]);
                    }
                }
                impl.update_Conv_Status(conv_dobj, axleDobj, status, ComparisonBeanImpl.changedDataContents(compareChanges()), ComparisonBeanImpl.changedDataContents(axleBean.compareChagnes()), selectedFancyRetnetion, ownerDobj, appl_details);
                if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    ServerUtil.sendSmsInFacelessService(appl_details.getAppl_no(), Util.getUserStateCode(), Util.getUserSeatOffCode(), appl_details.getRegn_no(), appl_details.getNotVerifiedDocdetails());
                    return_location = disapprovalPrint();
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
        } catch (VahanException vme) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), vme.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
        }
        return return_location;
    }

    public boolean numberGenration(int oldSeriesId) throws VahanException {
        boolean isnumgenration = true;
        try {
            VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
            RegVehCancelReceiptImpl regVehRcptImpl = new RegVehCancelReceiptImpl();
            vehParameters.setVCH_TYPE(vehType);
            vehParameters.setVH_CLASS(conv_dobj.getNew_vch_class());
            vehParameters.setVCH_CATG(conv_dobj.getNew_vch_catg());
            vehParameters.setPERMIT_TYPE(conv_dobj.getPmt_type());
            vehParameters.setPERMIT_SUB_CATG(conv_dobj.getPmt_catg());
            int NewSeriesId = 0;
            if ("HP,CH".contains(appl_details.getCurrent_state_cd())) {
                NewSeriesId = regVehRcptImpl.getSeriesId(vehParameters);
            }
            if ("HP,CH".contains(appl_details.getCurrent_state_cd()) && oldSeriesId == NewSeriesId) {
                String seriesAvailMessage = "Vehicle Registration No will not be Changed.";
                setRegnNumberAllotedMsg(seriesAvailMessage);
                isnumgenration = false;
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return isnumgenration;
    }

    public String printDisclaimer() {
        return PrintDocImpl.printOwnerDiscReport("registeredVehicles", "reportFormat");
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return prevChangedDataList;
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList;
    }

    /**
     * @return the masterLayout
     */
    public String getMasterLayout() {
        return masterLayout;
    }

    /**
     * @param masterLayout the masterLayout to set
     */
    public void setMasterLayout(String masterLayout) {
        this.masterLayout = masterLayout;
    }

    /**
     * @return the vh_type
     */
    public List getVh_type() {
        return vh_type;
    }

    /**
     * @param vh_type the vh_type to set
     */
    public void setVh_type(List vh_type) {
        this.vh_type = vh_type;
    }

    /**
     * @return the vh_class
     */
    public List getVh_class() {
        return vh_class;
    }

    /**
     * @param vh_class the vh_class to set
     */
    public void setVh_class(List vh_class) {
        this.vh_class = vh_class;
    }

    /**
     * @return the vh_category
     */
    public List getVh_category() {
        return vh_category;
    }

    /**
     * @param vh_category the vh_category to set
     */
    public void setVh_category(List vh_category) {
        this.vh_category = vh_category;
    }

    /**
     * @return the conv_dobj
     */
    public ConvDobj getConv_dobj() {
        return conv_dobj;
    }

    /**
     * @param conv_dobj the conv_dobj to set
     */
    public void setConv_dobj(ConvDobj conv_dobj) {
        this.conv_dobj = conv_dobj;
    }

    /**
     * @param compBeanList the compBeanList to set
     */
    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    public ConvDobj getConv_dobj_prev() {
        return conv_dobj_prev;
    }

    public void setConv_dobj_prev(ConvDobj conv_dobj_prev) {
        this.conv_dobj_prev = conv_dobj_prev;
    }

    public List getListReason() {
        return listReason;
    }

    public void setListReason(List listReason) {
        this.listReason = listReason;
    }

    public String getReasonSelected() {
        return reasonSelected;
    }

    public void setReasonSelected(String reasonSelected) {
        this.reasonSelected = reasonSelected;
    }

    public boolean isReasonDisable() {
        return reasonDisable;
    }

    public void setReasonDisable(boolean reasonDisable) {
        this.reasonDisable = reasonDisable;
    }

    public String getVh_catg() {
        return vh_catg;
    }

    public void setVh_catg(String vh_catg) {
        this.vh_catg = vh_catg;
    }

    public int getVh_cls() {
        return vh_cls;
    }

    public void setVh_cls(int vh_cls) {
        this.vh_cls = vh_cls;
    }

    public String getReasonPlaceholder() {
        return reasonPlaceholder;
    }

    public void setReasonPlaceholder(String reasonPlaceholder) {
        this.reasonPlaceholder = reasonPlaceholder;
    }

    /**
     * @return the vehType
     */
    public int getVehType() {
        return vehType;
    }

    /**
     * @param vehType the vehType to set
     */
    public void setVehType(int vehType) {
        this.vehType = vehType;
    }

    /**
     * @return the fit_upto
     */
    public Date getFit_upto() {
        return fit_upto;
    }

    /**
     * @param fit_upto the fit_upto to set
     */
    public void setFit_upto(Date fit_upto) {
        this.fit_upto = fit_upto;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
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

    private double getSurchargeAmount(Object[] currentData, double surcharge_percentage) {
        double surChargeAmt = 0;
        long taxAmt = 0;
        if (surcharge_percentage != 0) {
            if (currentData != null && currentData.length != 0) {
                taxAmt = Long.parseLong(currentData[2].toString());
            }
            surChargeAmt = taxAmt * (surcharge_percentage / 100);
        }
        return surChargeAmt;
    }

    private long processTaxDetails(Object[] taxDetails, double surchargeAmt) {
        if (taxDetails != null && taxDetails.length != 0) {
            taxMode = taxDetails[1].toString();
            taxAmtPaid = Long.parseLong(taxDetails[2].toString());
            taxAmtPaid = (long) (taxAmtPaid - surchargeAmt);
            taxPaidUpto = (Date) taxDetails[3];
            taxPaidFrom = (Date) taxDetails[4];
            double dayWiseAmt = 0;
            long remainingDays = 0;
            long durationOfTaxPaid = 0;
            try {
                currentDate = DateUtils.parseDate(DateUtils.getStartOfMonthDate(DateUtils.parseDate(currentDate)));
                if (taxPaidUpto.after(currentDate)) {
                    if (taxMode.equals("E")) {
                        return excessAmt = 0;
                    } else {
                        durationOfTaxPaid = DateUtils.getDate1MinusDate2_Days(taxPaidFrom, taxPaidUpto);
                        remainingDays = DateUtils.getDate1MinusDate2_Days(currentDate, taxPaidUpto);
                        dayWiseAmt = (double) taxAmtPaid / durationOfTaxPaid;
                        excessAmt = (long) (remainingDays * dayWiseAmt);
                    }
                }
            } catch (DateUtilsException ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return excessAmt;
    }

    /**
     * @return the disableExcessAmt
     */
    public boolean isDisableExcessAmt() {
        return disableExcessAmt;
    }

    /**
     * @param disableExcessAmt the disableExcessAmt to set
     */
    public void setDisableExcessAmt(boolean disableExcessAmt) {
        this.disableExcessAmt = disableExcessAmt;
    }

    /**
     * @return the taxDueFromList
     */
    public List getTaxDueFromList() {
        return taxDueFromList;
    }

    /**
     * @param taxDueFromList the taxDueFromList to set
     */
    public void setTaxDueFromList(List taxDueFromList) {
        this.taxDueFromList = taxDueFromList;
    }

    /**
     * @return the listTaxModes
     */
    public Map getListTaxModes() {
        return listTaxModes;
    }

    /**
     * @param listTaxModes the listTaxModes to set
     */
    public void setListTaxModes(Map listTaxModes) {
        this.listTaxModes = listTaxModes;
    }

    /**
     * @return the selectedNewTaxDueFrom
     */
    public String getSelectedNewTaxDueFrom() {
        return selectedNewTaxDueFrom;
    }

    /**
     * @param selectedNewTaxDueFrom the selectedNewTaxDueFrom to set
     */
    public void setSelectedNewTaxDueFrom(String selectedNewTaxDueFrom) {
        this.selectedNewTaxDueFrom = selectedNewTaxDueFrom;
    }

    /**
     * @return the listOtherCriteria
     */
    public List getListOtherCriteria() {
        return listOtherCriteria;
    }

    /**
     * @param listOtherCriteria the listOtherCriteria to set
     */
    public void setListOtherCriteria(List listOtherCriteria) {
        this.listOtherCriteria = listOtherCriteria;
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

    /**
     * @return the axleDetail_Visibility_tab
     */
    public boolean isAxleDetail_Visibility_tab() {
        return axleDetail_Visibility_tab;
    }

    /**
     * @param axleDetail_Visibility_tab the axleDetail_Visibility_tab to set
     */
    public void setAxleDetail_Visibility_tab(boolean axleDetail_Visibility_tab) {
        this.axleDetail_Visibility_tab = axleDetail_Visibility_tab;
    }

    /**
     * @return the axleBean
     */
    public AxleBean getAxleBean() {
        return axleBean;
    }

    /**
     * @param axleBean the axleBean to set
     */
    public void setAxleBean(AxleBean axleBean) {
        this.axleBean = axleBean;
    }

    /**
     * @return the disableVehType
     */
    public boolean isDisableVehType() {
        return disableVehType;
    }

    /**
     * @param disableVehType the disableVehType to set
     */
    public void setDisableVehType(boolean disableVehType) {
        this.disableVehType = disableVehType;
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
     * @return the regnNumberAllotedMsg
     */
    public String getRegnNumberAllotedMsg() {
        return regnNumberAllotedMsg;
    }

    /**
     * @param regnNumberAllotedMsg the regnNumberAllotedMsg to set
     */
    public void setRegnNumberAllotedMsg(String regnNumberAllotedMsg) {
        this.regnNumberAllotedMsg = regnNumberAllotedMsg;
    }

    public boolean isAdvRegnCheck() {
        return advRegnCheck;
    }

    public void setAdvRegnCheck(boolean advRegnCheck) {
        this.advRegnCheck = advRegnCheck;
    }

    public AdvanceRegnNo_dobj getAdvRegnNoDobj() {
        return advRegnNoDobj;
    }

    public void setAdvRegnNoDobj(AdvanceRegnNo_dobj advRegnNoDobj) {
        this.advRegnNoDobj = advRegnNoDobj;
    }

    public void advanceCheckListener() {
        if (advRegnCheck) {
            advRegnCheckDialogue = true;
            retCheck = false;

        }
    }

    public boolean isAdvRegnCheckDialogue() {
        return advRegnCheckDialogue;
    }

    public void setAdvRegnCheckDialogue(boolean advRegnCheckDialogue) {
        this.advRegnCheckDialogue = advRegnCheckDialogue;
    }

    public boolean isDisableAdvRegnCheck() {
        return disableAdvRegnCheck;
    }

    public void setDisableAdvRegnCheck(boolean disableAdvRegnCheck) {
        this.disableAdvRegnCheck = disableAdvRegnCheck;
    }

    public List getList_c_state() {
        return list_c_state;
    }

    public void setList_c_state(List list_c_state) {
        this.list_c_state = list_c_state;
    }

    /**
     * @return the maxDate
     */
    public Date getMaxDate() {
        return maxDate;
    }

    /**
     * @param maxDate the maxDate to set
     */
    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    /**
     * @return the enableTaxClearUpto
     */
    public boolean isEnableTaxClearUpto() {
        return enableTaxClearUpto;
    }

    /**
     * @param enableTaxClearUpto the enableTaxClearUpto to set
     */
    public void setEnableTaxClearUpto(boolean enableTaxClearUpto) {
        this.enableTaxClearUpto = enableTaxClearUpto;
    }

    /**
     * @return the fitnessValidLabel
     */
    public String getFitnessValidLabel() {
        return fitnessValidLabel;
    }

    /**
     * @param fitnessValidLabel the fitnessValidLabel to set
     */
    public void setFitnessValidLabel(String fitnessValidLabel) {
        this.fitnessValidLabel = fitnessValidLabel;
    }

    public boolean isFancyRetention() {
        return fancyRetention;
    }

    public void setFancyRetention(boolean fancyRetention) {
        this.fancyRetention = fancyRetention;
    }

    public String getSelectedFancyRetnetion() {
        return selectedFancyRetnetion;
    }

    public void setSelectedFancyRetnetion(String selectedFancyRetnetion) {
        this.selectedFancyRetnetion = selectedFancyRetnetion;
    }

    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    public boolean isPermitPanel() {
        return permitPanel;
    }

    public void setPermitPanel(boolean permitPanel) {
        this.permitPanel = permitPanel;
    }

    /**
     * @return the isPmtCatgRqrd
     */
    public boolean isIsPmtCatgRqrd() {
        return isPmtCatgRqrd;
    }

    /**
     * @param isPmtCatgRqrd the isPmtCatgRqrd to set
     */
    public void setIsPmtCatgRqrd(boolean isPmtCatgRqrd) {
        this.isPmtCatgRqrd = isPmtCatgRqrd;
    }

    public boolean isPushBkSeatRender() {
        return pushBkSeatRender;
    }

    public void setPushBkSeatRender(boolean pushBkSeatRender) {
        this.pushBkSeatRender = pushBkSeatRender;
    }

    public boolean isRenderMultiRegionList() {
        return renderMultiRegionList;
    }

    public void setRenderMultiRegionList(boolean renderMultiRegionList) {
        this.renderMultiRegionList = renderMultiRegionList;
    }

    public String getRetRcptMsg() {
        return retRcptMsg;
    }

    public void setRetRcptMsg(String retRcptMsg) {
        this.retRcptMsg = retRcptMsg;
    }

    public boolean isInwardDetailsDisable() {
        return inwardDetailsDisable;
    }

    public void setInwardDetailsDisable(boolean inwardDetailsDisable) {
        this.inwardDetailsDisable = inwardDetailsDisable;
    }
}
