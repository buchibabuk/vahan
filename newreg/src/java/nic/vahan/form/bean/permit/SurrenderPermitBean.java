/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.HypothecationDetailsBean;
import nic.vahan.form.bean.OwnerBean;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitCheckDetailsDobj;
import nic.vahan.form.dobj.permit.PermitHomeAuthDobj;
import nic.vahan.form.dobj.permit.SurrenderPermitDobj;
import nic.vahan.form.impl.ApproveImpl;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.PermitDetailImpl;
import nic.vahan.form.impl.permit.PermitOwnerDetailImpl;
import nic.vahan.form.impl.permit.SurrenderPermitImpl;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.PermitFeeImpl;
import nic.vahan.form.impl.permit.PermitHomeAuthImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
//import org.primefaces.component.commandbutton.CommandButton;
//import org.primefaces.component.inputtext.InputText;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "surren_goods_permit")
@ViewScoped
public class SurrenderPermitBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(SurrenderPermitBean.class);
    @ManagedProperty(value = "#{approveImpl}")
    private ApproveImpl approveImpl;
    @ManagedProperty(value = "#{pmt_owner_dtls}")
    private PermitOwnerDetailBean ownerBean;
    @ManagedProperty(value = "#{Permit_Dtls_bean}")
    private PermitDetailBean permit_Dtls_bean;
    @ManagedProperty(value = "#{owner_bean}")
    private OwnerBean owner_bean;
    private Owner_dobj ownerDobj;
    PermitOwnerDetailDobj owner_dobj = null;
    PermitOwnerDetailImpl ownerImpl = null;
    SurrenderPermitDobj prvdobj = new SurrenderPermitDobj();
    PermitDetailImpl pmt_impl = null;
    PermitDetailDobj pmt_dobj = null;
    private String new_regn_no = "";
    private boolean showVerifyPanels = false;
    private boolean renderNewVeh = false;
    private boolean showApprovePanels = false;
    private boolean showEntryPanels = false;
    private boolean showPrintPanels = false;
    private boolean showgoods = false;
    private boolean showPassenger = false;
    private boolean disableBut = true;
    private String show_pannel = null;
    private String header_msg = null;
    private String receipt_no = null;
    private String regn_number = null;
    private String regn_no;
    private String pmt_type_desc;
    private String route_from;
    private String route_to;
    private String goods_to_caryy;
    private String region_desc;
    private String purpose;
    private String trans_purpose = "O";
    private String issue_auth;
    private String remarks;
    private String refference_no;
    private String permit_surender;
    private String verify_app_no;
    private String approve_app_no;
    private String app_no_msg = "";
    private String pur_cd = "";
    private int pmt_type;
    private int region_cd;
    private Date surrender_dt = new Date();
    private Date effect_from = new Date();
    private List verify_app_no_list = new ArrayList();
    private List purpose_list = new ArrayList();
    private List transferPurposeList = new ArrayList();
    private List list_vh_class = new ArrayList();
    private List list_vm_catg = new ArrayList();
    private List list_maker_model = new ArrayList();
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<ComparisonBean> prevChangedDataList;
    FacesMessage message = null;
    private List<PassengerPermitDetailDobj> multiPermitDetails = new ArrayList<>();
    private PassengerPermitDetailDobj selectedValue = null;
    private boolean newRegnNoDisable = false;
    private boolean nocIssue = false;
    private boolean renderNocIssue = false;
    private List<OwnerDetailsDobj> listExistingOwnerDetails = new ArrayList<>();
    private boolean existingOwnerDisable = false;
    private boolean renderReAssigment = false;
    private String ReAssigmentRegnNo = "";
    // Nitin KUmar 21-01-2016 Begin 
    private OwnerDetailsDobj ownerDetail;
    // Nitin KUmar 21-01-2016 End
    private PermitHomeAuthDobj authDobj = null;
    @ManagedProperty(value = "#{hypothecationDetails_bean}")
    private HypothecationDetailsBean hypothecationDetails_bean;
    private Date currentDate = new Date();
    public String[] allowedSurrPurCdArray = null;
    private int transPurCd = 0;
    private boolean surrDtDisable = false;
    private String vahanMessages = null;
    private SessionVariables sessionVariables = null;
    private boolean renderPushBackSeatPanel = false;
    private boolean renderTransferCase = false;
    private int tempspl_pur_cd = 0;
    @ManagedProperty(value = "#{pmtDtlsBean}")
    private PermitCheckDetailsBean pmtCheckDtsl;
    private boolean renderOtherVehicleInfo = false;
    private Map<String, String> stateConfig = null;
    private boolean multiPmtAllow = false;
    private boolean surrenderPmtOffice = false;
    private boolean cancelPmtByIssueOffice = false;
    private boolean showNpDetails = false;
    private boolean renderDocUploadTab = false;
    private String dmsUrl = "";

    public SurrenderPermitBean() {
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                    || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
                vahanMessages = "Session time Out";
                return;
            }
            stateConfig = CommonPermitPrintImpl.getVmPermitStateConfiguration(sessionVariables.getStateCodeSelected());
            multiPmtAllow = Boolean.parseBoolean(stateConfig.get("multi_pmt_allow_on_veh"));
            allowedSurrPurCdArray = String.valueOf(stateConfig.get("allowed_surr_pur_cd")).split(",");

            String surrenderPmtOffice_str = "";
            String cancelPmtByIssueOffice_str = "";
            VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
            surrenderPmtOffice_str = FormulaUtils.getSqlFormulaValue(FormulaUtils.replaceTagPermitValues(stateConfig.get("surrender_pmt_office"), parameters), "SurrenderPermitBean()");
            cancelPmtByIssueOffice_str = FormulaUtils.getSqlFormulaValue(FormulaUtils.replaceTagPermitValues(stateConfig.get("cancel_permit_by_authority"), parameters), "SurrenderPermitBean()");
            surrenderPmtOffice = Boolean.parseBoolean(surrenderPmtOffice_str);
            cancelPmtByIssueOffice = Boolean.parseBoolean(cancelPmtByIssueOffice_str);
            parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
            parameters.setPUR_CD(appl_details.getPur_cd());
            if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfig.get("show_uploaded_document"), parameters), "init")) {
                renderDocUploadTab = true;
            } else {
                renderDocUploadTab = false;
            }
            String[][] data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
            purpose_list.add(new SelectItem("-1", "SELECT"));
            for (int i = 0; i < data.length; i++) {
                if (allowedSurrPurCdArray.length == 1) {
                    if (allowedSurrPurCdArray[0].equalsIgnoreCase("ANY")) {
                        if (String.valueOf(TableConstants.VM_PMT_TRANSFER_PUR_CD).equals(data[i][0]) || data[i][0].equals(String.valueOf(TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD))
                                || data[i][0].equals(String.valueOf(TableConstants.VM_PMT_CA_PUR_CD)) || data[i][0].equals(String.valueOf(TableConstants.VM_PMT_CANCELATION_PUR_CD))
                                || data[i][0].equals(String.valueOf(TableConstants.VM_PMT_REPLACE_VEH_PUR_CD)) || data[i][0].equals(String.valueOf(TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD))
                                || data[i][0].equals(String.valueOf(TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD)) || data[i][0].equals(String.valueOf(TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD))
                                || data[i][0].equals(String.valueOf(TableConstants.VM_PMT_SUSPENSION_PUR_CD)) || data[i][0].equals(String.valueOf(TableConstants.VM_PMT_TEMP_SUR_PUR_CD))
                                || data[i][0].equals(String.valueOf(TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD))) {
                            purpose_list.add(new SelectItem(data[i][0], data[i][1]));
                        }
                    } else {
                        if (allowedSurrPurCdArray[0].equals(data[i][0])) {
                            purpose_list.add(new SelectItem(data[i][0], data[i][1]));
                            break;
                        }
                    }
                } else if (allowedSurrPurCdArray.length > 1) {
                    for (int j = 0; j < allowedSurrPurCdArray.length; j++) {
                        if (allowedSurrPurCdArray[j].equals(data[i][0])) {
                            purpose_list.add(new SelectItem(data[i][0], data[i][1]));
                        }
                    }
                }
            }
            transferPurposeList = new SurrenderPermitImpl().getTransferCaseForSurrender();
            data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
            list_vh_class.clear();
            for (int i = 0; i < data.length; i++) {
                list_vh_class.add(new SelectItem(data[i][0], data[i][1]));
            }

            data = MasterTableFiller.masterTables.VM_VCH_CATG.getData();
            for (int i = 0; i < data.length; i++) {
                list_vm_catg.add(new SelectItem(data[i][0], data[i][1]));
            }

            data = MasterTableFiller.masterTables.VM_MODELS.getData();
            for (int i = 0; i < data.length; i++) {
                list_maker_model.add(new SelectItem(data[i][1], data[i][2]));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    @PostConstruct
    public void init() {
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                    || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
                vahanMessages = "Session time Out";
                return;
            }
            PermitFeeImpl feeImpl = new PermitFeeImpl();
            if (TableConstants.TM_ROLE_PMT_SURRENDER_ENTRY == sessionVariables.getActionCodeSelected()) {
                header_msg = "Surrender Goods/Passenger Permit";
                showEntryPanels = true;
                showVerifyPanels = false;
                showApprovePanels = false;
                renderOtherVehicleInfo = true;
            } else if (TableConstants.TM_ROLE_PMT_SURRENDER_VERIFICATION == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_SURRENDER_TRANSFER_VERIFICATION == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_SURRENDER_TRANSFER_IN_DEATH_VERIFICATION == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_CA_VERIFICATION == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_SUSPENSION_VERIFICATION == sessionVariables.getActionCodeSelected()) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                header_msg = "Verification of Surrender Goods/Passenger Permit";
                regn_number = appl_details.getRegn_no().toUpperCase();
                verify_app_no = appl_details.getAppl_no().toUpperCase();
                showEntryPanels = false;
                showVerifyPanels = true;
                showApprovePanels = false;
                renderOtherVehicleInfo = true;
                showPermitVerifyDetails();
            } else if (TableConstants.TM_ROLE_PMT_SURRENDER_APPROVAL == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_SURRENDER_TRANSFER_APPROVE == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_SURRENDER_TRANSFER_IN_DEATH_APPROVE == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_CA_APPROVAL == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_SUSPENSION_APPROVAL == sessionVariables.getActionCodeSelected()) {
                header_msg = "Approval of Surrender Goods/Passenger Permit";
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                regn_number = appl_details.getRegn_no().toUpperCase();
                verify_app_no = appl_details.getAppl_no().toUpperCase();
                showEntryPanels = false;
                showVerifyPanels = true;
                showApprovePanels = false;
                renderNocIssue = true;
                renderOtherVehicleInfo = true;
                showPermitVerifyDetails();
            } else if (TableConstants.TM_ROLE_PMT_RESTORE_ENTRY == sessionVariables.getActionCodeSelected()) {
                header_msg = "Restore  Goods/Passenger Permit";
                showEntryPanels = true;
                disableBut = true;
                showVerifyPanels = false;
                renderOtherVehicleInfo = false;
                showApprovePanels = false;
            } else if (TableConstants.TM_ROLE_PMT_RESTORE_VERIFICATION == sessionVariables.getActionCodeSelected()) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                header_msg = "Verification of Restore Goods/Passenger Permit";
                regn_number = appl_details.getRegn_no().toUpperCase();
                verify_app_no = appl_details.getAppl_no().toUpperCase();
                showEntryPanels = false;
                showVerifyPanels = true;
                renderOtherVehicleInfo = false;
                showApprovePanels = false;
                showRestorePermitVerifyDetails();
            } else if (TableConstants.TM_ROLE_PMT_RESTORE_APPROVAL == sessionVariables.getActionCodeSelected()) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                header_msg = "Approval of Restore Goods/Passenger Permit";
                regn_number = appl_details.getRegn_no().toUpperCase();
                verify_app_no = appl_details.getAppl_no().toUpperCase();
                showEntryPanels = false;
                showVerifyPanels = true;
                renderOtherVehicleInfo = false;
                showApprovePanels = false;
                showRestorePermitVerifyDetails();
            } else if (TableConstants.TM_ROLE_PMT_REPLACE_VEH_VERIFICATION == sessionVariables.getActionCodeSelected()) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                header_msg = "Verification of Replace Vehicle Goods/Passenger Permit";
                if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("UK")) {
                    regn_number = feeImpl.getRegnNoForReplacement(appl_details.getRegn_no().toUpperCase());
                } else {
                    regn_number = appl_details.getRegn_no().toUpperCase();
                }
                verify_app_no = appl_details.getAppl_no().toUpperCase();
                showEntryPanels = false;
                showVerifyPanels = true;
                showApprovePanels = false;
                renderOtherVehicleInfo = false;
                showRestorePermitVerifyDetails();
            } else if (TableConstants.TM_ROLE_PMT_REPLACE_VEH_APPROVAL == sessionVariables.getActionCodeSelected()) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                header_msg = "Approval of Replace Vehicle Goods/Passenger Permit";
                if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("UK")) {
                    regn_number = feeImpl.getRegnNoForReplacement(appl_details.getRegn_no().toUpperCase());
                } else {
                    regn_number = appl_details.getRegn_no().toUpperCase();
                }
                verify_app_no = appl_details.getAppl_no().toUpperCase();
                showEntryPanels = false;
                showVerifyPanels = true;
                showApprovePanels = false;
                renderOtherVehicleInfo = false;
                showRestorePermitVerifyDetails();
            } else if (TableConstants.TM_ROLE_PMT_RE_ASSIGMENT_VERIFICATION == sessionVariables.getActionCodeSelected()) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                header_msg = "Verification of RE-ASSIGMENT Vehicle Goods/Passenger Permit";
                regn_number = appl_details.getRegn_no().toUpperCase();
                verify_app_no = appl_details.getAppl_no().toUpperCase();
                showEntryPanels = false;
                showVerifyPanels = true;
                showApprovePanels = false;
                renderOtherVehicleInfo = false;
                showRestorePermitVerifyDetails();
            } else if (TableConstants.TM_ROLE_PMT_RE_ASSIGMENT_APPROVAL == sessionVariables.getActionCodeSelected()) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                header_msg = "Approval of RE-ASSIGMENT Vehicle Goods/Passenger Permit";
                regn_number = appl_details.getRegn_no().toUpperCase();
                verify_app_no = appl_details.getAppl_no().toUpperCase();
                showEntryPanels = false;
                showVerifyPanels = true;
                renderOtherVehicleInfo = false;
                showApprovePanels = false;
                showRestorePermitVerifyDetails();
            } else if (TableConstants.TM_ROLE_PMT_TO_REPLACE_VERIFICATION == sessionVariables.getActionCodeSelected()) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                header_msg = "Verification of Transfer & Replace Vehicle Goods/Passenger Permit";
                regn_number = appl_details.getRegn_no().toUpperCase();
                verify_app_no = appl_details.getAppl_no().toUpperCase();
                showEntryPanels = false;
                showVerifyPanels = true;
                showApprovePanels = false;
                renderOtherVehicleInfo = false;
                showRestorePermitVerifyDetails();
            } else if (TableConstants.TM_ROLE_PMT_TO_REPLACE_APPROVAL == sessionVariables.getActionCodeSelected()) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                header_msg = "Approval of Transfer & Replace Vehicle Goods/Passenger Permit";
                regn_number = appl_details.getRegn_no().toUpperCase();
                verify_app_no = appl_details.getAppl_no().toUpperCase();
                showEntryPanels = false;
                showVerifyPanels = true;
                showApprovePanels = false;
                renderOtherVehicleInfo = false;
                showRestorePermitVerifyDetails();
            } else if (TableConstants.TM_ROLE_PMT_PERMANENT_SURRENDER_VERIFICATION == sessionVariables.getActionCodeSelected()) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                header_msg = "Verification of Permanent Suurender Of Permit";
                regn_number = appl_details.getRegn_no().toUpperCase();
                verify_app_no = appl_details.getAppl_no().toUpperCase();
                showEntryPanels = false;
                showVerifyPanels = true;
                renderOtherVehicleInfo = true;
                showApprovePanels = false;
                showPermitVerifyDetails();
            } else if (TableConstants.TM_ROLE_PMT_PERMANENT_SURRENDER_APPROVAL == sessionVariables.getActionCodeSelected()) {
                header_msg = "Approval of Permanent Suurender Of Permit";
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                regn_number = appl_details.getRegn_no().toUpperCase();
                verify_app_no = appl_details.getAppl_no().toUpperCase();
                showEntryPanels = false;
                showVerifyPanels = true;
                showApprovePanels = false;
                renderOtherVehicleInfo = true;
                renderNocIssue = true;
                showPermitVerifyDetails();
            } else if (TableConstants.TM_ROLE_PMT_CANCEL_VERIFICATION == sessionVariables.getActionCodeSelected()) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                header_msg = "Verification of Cancellation Of Permit";
                regn_number = appl_details.getRegn_no().toUpperCase();
                verify_app_no = appl_details.getAppl_no().toUpperCase();
                showEntryPanels = false;
                showVerifyPanels = true;
                renderOtherVehicleInfo = true;
                showApprovePanels = false;
                showPermitVerifyDetails();
            } else if (TableConstants.TM_ROLE_PMT_CANCEL_APPROVAL == sessionVariables.getActionCodeSelected()) {
                header_msg = "Approval of Cancellation Of Permit";
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                regn_number = appl_details.getRegn_no().toUpperCase();
                verify_app_no = appl_details.getAppl_no().toUpperCase();
                showEntryPanels = false;
                showVerifyPanels = true;
                showApprovePanels = false;
                renderOtherVehicleInfo = true;
                renderNocIssue = true;
                showPermitVerifyDetails();
            } else if (TableConstants.TM_ROLE_PMT_TO_REPLACE_OTHER_OFFICE_VERIFICATION == sessionVariables.getActionCodeSelected()) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                header_msg = "Verification of Transfer & Replace To Other Office Goods/Passenger Permit";
                regn_number = appl_details.getRegn_no().toUpperCase();
                verify_app_no = appl_details.getAppl_no().toUpperCase();
                showEntryPanels = false;
                showVerifyPanels = true;
                renderOtherVehicleInfo = false;
                showApprovePanels = false;
                showRestorePermitVerifyDetails();
            } else if (TableConstants.TM_ROLE_PMT_TO_REPLACE_OTHER_OFFICE_APPROVAL == sessionVariables.getActionCodeSelected()) {
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                header_msg = "Approval of Transfer & Replace To Other Office Goods/Passenger Permit";
                regn_number = appl_details.getRegn_no().toUpperCase();
                verify_app_no = appl_details.getAppl_no().toUpperCase();
                showEntryPanels = false;
                showVerifyPanels = true;
                renderOtherVehicleInfo = false;
                showApprovePanels = false;
                showRestorePermitVerifyDetails();
            }
        } catch (Exception e) {
        }
    }

    public void onlyVehReplaceSelect(String trans_pur_cd) {
        purpose_list.clear();
        String[][] data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].equalsIgnoreCase(trans_pur_cd)) {
                purpose_list.add(new SelectItem(data[i][0], data[i][1]));
                break;
            }
        }
        if (trans_pur_cd.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_REPLACE_VEH_PUR_CD))
                || trans_pur_cd.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD))) {
            renderNewVeh = true;
            disableBut = true;
        } else {
            renderNewVeh = false;
            disableBut = false;
        }
    }

    public void onSelectTranferPurpose() {
        int pur_cd = Integer.parseInt(getPurpose());
        if (pur_cd == TableConstants.VM_PMT_TRANSFER_PUR_CD) {
            setRenderTransferCase(true);
        } else {
            setRenderTransferCase(false);
        }
    }

    public void showRequiredPannelValueChangeListener(ValueChangeEvent evt) {
        String radioVal = evt.getNewValue().toString();
        if (("entry").equalsIgnoreCase(radioVal)) {
            setShowPrintPanels(false);
            header_msg = "Surrender Goods Permit";
        } else if (("slip").equalsIgnoreCase(radioVal)) {
            setShowPrintPanels(true);
            header_msg = "Print Surrent Slip";
        }
    }

    public void reset() {
        regn_no = "";
        pmt_type_desc = "";
        goods_to_caryy = "";
        region_desc = "";
        receipt_no = "";
        purpose = "-1";
        surrender_dt = null;
        issue_auth = "";
        refference_no = "";
        effect_from = null;
        remarks = "";
        showgoods = true;
        showPassenger = false;
        permit_surender = "-1";
        route_from = "";
        route_to = "";
    }

    private String validateForm(PermitCheckDetailsDobj validDobj) {
        String result = "";
        if (validDobj != null && validDobj.getTaxUpto().equalsIgnoreCase("LIFE TIME") && !CommonUtils.isNullOrBlank(validDobj.getTaxMode()) && ("OLS".contains(validDobj.getTaxMode()))) {
            validDobj.setTaxUpto("31-DEC-2999");
        } else if (validDobj != null && validDobj.getTaxUpto().equalsIgnoreCase("LIFE TIME") && !CommonUtils.isNullOrBlank(validDobj.getTaxMode()) && (!"OLS".contains(validDobj.getTaxMode()))) {
            validDobj.setTaxUpto("");
        }
        if (sessionVariables == null) {
            result = "Session time Out";
            return null;
        }
        if (CommonUtils.isNullOrBlank(regn_no)) {
            result = "Vehicle Details not found";
            return null;
        }
        SurrenderPermitDobj dobj = SurrenderPermitImpl.getRebackPermitDetails(regn_no.toUpperCase(), null, sessionVariables.getActionCodeSelected());
        if (StringUtils.isBlank(purpose) || ("-1").equalsIgnoreCase(purpose)) {
            result = "Select the Purpose!";
        } else if (StringUtils.isBlank(issue_auth)) {
            result = "Enter issuing Authority!";
        } else if (StringUtils.isBlank(refference_no)) {
            result = "Enter Reference Number!";
        } else if (effect_from == null) {
            result = "Enter Effective from Date!";
        } else if (StringUtils.isBlank(remarks)) {
            result = "Enter Remarks!";
        } else if (dobj != null && TableConstants.TM_ROLE_PMT_SURRENDER_ENTRY == sessionVariables.getActionCodeSelected()) {
            if (multiPmtAllow) {
                result = "";
            } else {
                result = "Registration No. " + dobj.getRegn_no() + " already surrender with the purpose of " + ServerUtil.getTaxHead(Integer.valueOf(dobj.getPurpose().trim()));
            }
        } else if (validDobj != null && TableConstants.TM_ROLE_PMT_SURRENDER_ENTRY == sessionVariables.getActionCodeSelected() && !validDobj.isExemptedFitness() && (CommonUtils.isNullOrBlank(validDobj.getFitValidTo()) || !(DateUtils.compareDates(DateUtils.getCurrentLocalDate(), CommonPermitPrintImpl.getDateDD_MMM_YYYY(validDobj.getFitValidTo())) <= 1))) {
            result = "Vehicle Fitness has been expired.";
        } else if (Integer.parseInt(purpose) == TableConstants.VM_PMT_CANCELATION_PUR_CD && validDobj != null && TableConstants.TM_ROLE_PMT_SURRENDER_ENTRY == sessionVariables.getActionCodeSelected() && (CommonUtils.isNullOrBlank(validDobj.getTaxUpto()) || !(DateUtils.compareDates(DateUtils.getCurrentLocalDate(), CommonPermitPrintImpl.getDateDD_MMM_YYYY(validDobj.getTaxUpto())) <= 1)) && Util.getUserStateCode().equalsIgnoreCase("PY")) {
            result = "Vehicle Tax has been expired.";
        }
        if (validDobj != null && multiPermitDetails.size() > 0 && TableConstants.TM_ROLE_PMT_SURRENDER_ENTRY == sessionVariables.getActionCodeSelected() && !CommonUtils.isNullOrBlank(result) && result.equalsIgnoreCase("Vehicle Tax has been expired.")) {
            for (int i = 0; i <= multiPermitDetails.size(); i++) {
                if (Integer.parseInt(multiPermitDetails.get(i).getPaction_code()) == TableConstants.VM_PMT_SPECIAL_PUR_CD && permit_Dtls_bean.getPmt_no().toUpperCase().equalsIgnoreCase(multiPermitDetails.get(i).getPmt_no())) {
                    result = CommonPermitPrintImpl.checkPmtGracePeriod(pmtCheckDtsl.getDtlsDobj(), (pmt_dobj != null && pmt_dobj.getPmt_type() != 0) ? pmt_dobj.getPmt_type() : 0);
                    break;
                }
            }

        }
        return result;
    }

    public void getPermitDetail() {
        try {
            String temp_regn_no = getRegn_number().toUpperCase();
            SurrenderPermitImpl surImpl = new SurrenderPermitImpl();
            if (TableConstants.TM_ROLE_PMT_SURRENDER_ENTRY == sessionVariables.getActionCodeSelected()) {
                regn_number = CommonPermitPrintImpl.getRegnNoForSurrenderInVtPermit(getRegn_number().toUpperCase(), sessionVariables.getStateCodeSelected());
                if (CommonUtils.isNullOrBlank(regn_number)) {
                    regn_number = CommonPermitPrintImpl.getRegnNoinVtTempPermit(temp_regn_no, sessionVariables.getStateCodeSelected());
                }
                if (CommonUtils.isNullOrBlank(regn_number)) {
                    throw new VahanException("Please Enter Valid Vehicle Number!");
                }
                boolean flag = surImpl.checkApplPendingStatusInOnlineApp(regn_number.toUpperCase());
                if (flag) {
                    showApplPendingDialogBox("Application is pending at Online Permit for this vehicle number, so permit cannot be surrendered");
                    return;
                }
                String surrMsg = surImpl.permitSurrMsg(regn_number.toUpperCase(), multiPmtAllow);
                if (!CommonUtils.isNullOrBlank(surrMsg)) {
                    showApplPendingDialogBox(surrMsg);
                    return;
                }
                pmtCheckDtsl.getAlldetails(regn_number.toUpperCase(), null, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
            } else if (TableConstants.TM_ROLE_PMT_RESTORE_ENTRY == sessionVariables.getActionCodeSelected()) {
                regn_number = CommonPermitPrintImpl.getRegnNoinVtPermitTranaction(getRegn_number().toUpperCase());
                if (CommonUtils.isNullOrBlank(regn_number)) {
                    throw new VahanException("Please Enter Valid Vehicle Number!");
                }
                transPurCd = surImpl.getTransPurCdFromVtPmtTranaction(getRegn_number().toUpperCase());
                if (transPurCd == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD) {
                    setRegn_number((surImpl.getReAssigmentOldToNew(getRegn_number().toUpperCase())).toUpperCase());
                }
                if (CommonUtils.isNullOrBlank(regn_number)) {
                    throw new VahanException("Please Enter Valid Vehicle Number!");
                }

            }
            //nitin kumar - 21-01-2015 BEGIN 
            OwnerImpl owner_Impl = new OwnerImpl();
            List<OwnerDetailsDobj> ownerDetailsDobjList = owner_Impl.getOwnerDetailsList(regn_number.toUpperCase().trim(), null);
            ownerDetailsDobjList = owner_Impl.getStateWiseOwnerList(ownerDetailsDobjList, sessionVariables.getStateCodeSelected());
            if (TableConstants.TM_ROLE_PMT_RESTORE_ENTRY == sessionVariables.getActionCodeSelected() && ownerDetailsDobjList != null
                    && ownerDetailsDobjList.size() == 0 && (transPurCd == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD || transPurCd == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD)) {
                ownerImpl = new PermitOwnerDetailImpl();
                owner_dobj = ownerImpl.setVhOwnerDtlsOnlyDisplay(regn_number.toUpperCase().trim(), sessionVariables.getStateCodeSelected());
                if (owner_dobj != null) {
                    ownerDetail = new OwnerDetailsDobj();
                    ownerDetail.setState_cd(owner_dobj.getState_cd());
                    ownerDetail.setOff_cd(owner_dobj.getOff_cd());
                    ownerDetail.setRegn_no(owner_dobj.getRegn_no());
                    ownerDetail.setVh_class(owner_dobj.getVh_class());
                    ownerDetail.setRegn_dt(owner_dobj.getRegnDt().toString());
                }
            } else if (ownerDetailsDobjList != null && ownerDetailsDobjList.size() == 1) {
                ownerDetail = ownerDetailsDobjList.get(0);
            } else if (ownerDetailsDobjList != null && ownerDetailsDobjList.size() >= 2) {
                ownerBean.setDupRegnList(ownerDetailsDobjList);
                //PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Can't Inward Application due to Duplicate Registration No Found for Current State"));
                PrimeFaces.current().executeScript("PF('varDupRegNo').show()");
                return;
            }

            if (ownerDetail == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration Number Not Found in Currnet Office"));
                return;
            }

            //nitin kumar  - 21-01-2015 - END
            if (TableConstants.TM_ROLE_PMT_SURRENDER_ENTRY == sessionVariables.getActionCodeSelected()) {
                multiPermitDetails = SurrenderPermitImpl.getToatalNoOfPermitList(sessionVariables.getStateCodeSelected(), regn_number.toUpperCase());
                if (multiPermitDetails.size() > 1) {
                    PrimeFaces.current().ajax().update("no_Of_pmt");
                    PrimeFaces.current().executeScript("PF('noOfPmt').show();");
                } else if (multiPermitDetails.size() == 1) {
                    showPermitDetails(multiPermitDetails.get(0).getPmt_no().toUpperCase(), Integer.valueOf(multiPermitDetails.get(0).getPaction_code()), ownerDetail);
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please Enter Valid Vehicle Number!", "Please Enter Valid Vehicle Number!");
                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                    return;
                }
            } else if (TableConstants.TM_ROLE_PMT_RESTORE_ENTRY == sessionVariables.getActionCodeSelected()) {
                if (transPurCd == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD) {
                    multiPermitDetails = SurrenderPermitImpl.getToatalNoOfPermitSurrInSameRegn((surImpl.getReAssigmentNewToOld(getRegn_number().toUpperCase())).toUpperCase());
                } else {
                    multiPermitDetails = SurrenderPermitImpl.getToatalNoOfPermitSurrInSameRegn(regn_number.toUpperCase());
                }
                if (multiPermitDetails.size() > 1) {
                    PrimeFaces.current().ajax().update("no_Of_pmt");
                    PrimeFaces.current().executeScript("PF('noOfPmt').show();");
                } else if (multiPermitDetails.size() == 1) {
                    showPermitDetails(multiPermitDetails.get(0).getPmt_no().toUpperCase(), Integer.valueOf(multiPermitDetails.get(0).getPaction_code()), ownerDetail);
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please Enter Valid Vehicle Number!", "Please Enter Valid Vehicle Number!");
                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                    return;
                }
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please Enter Valid Vehicle Number!", "Please Enter Valid Vehicle Number!");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return;
            }
        } catch (VahanException e) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", TableConstants.SomthingWentWrong));
        }
    }

    public void showPermitDetails(String pmt_no, int pur_cd, OwnerDetailsDobj ownerDetail) {
        try {
            if (TableConstants.TM_ROLE_PMT_RESTORE_ENTRY == sessionVariables.getActionCodeSelected()) {
                SurrenderPermitImpl surImpl = new SurrenderPermitImpl();
                if (transPurCd == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD) {
                    setRegn_number((surImpl.getReAssigmentNewToOld(ownerDetail.getRegn_no().toUpperCase())).toUpperCase());
                }
                if (transPurCd == TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD) {
                    Map<String, String> OffAllotResult = PermitDetailImpl.getOffAllotmentResult(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), ownerDetail.getOff_cd());
                    if (("false").equalsIgnoreCase(OffAllotResult.get("offAllowed"))) {
                        JSFUtils.showMessagesInDialog("Information", "This Office not Allowed to Restore Permit", FacesMessage.SEVERITY_ERROR);
                        return;
                    }
                }
                boolean flage = SurrenderPermitImpl.getvhPermit_statueRegnno(regn_number.toUpperCase(), pmt_no);
                if (flage) {
                    disableBut = false;
                    showRebackDetails(pmt_no);
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Enter Valid Vehicle Number!", "Please Enter Valid Vehicle Number!");
                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                    return;
                }
            }
            if (TableConstants.TM_ROLE_PMT_SURRENDER_ENTRY == sessionVariables.getActionCodeSelected() || TableConstants.TM_ROLE_PMT_REPLACE_VEH_ENTRY == sessionVariables.getActionCodeSelected()) {
                if (regn_number == null || ("").equalsIgnoreCase(regn_number)) {
                    message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Please Enter Registration Number", "Enter Recipt Number!");
                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                    return;
                }
                ownerImpl = new PermitOwnerDetailImpl();
                permit_Dtls_bean.permitComponentReadOnly(true);
                pmt_dobj = PermitDetailImpl.getPermitdetailsThroughPmtNo(pmt_no, pur_cd);
                if (pmt_dobj == null) {
                    throw new VahanException("Permit Details not found in corresponding tables. Please contact to System administrator.");
                } else if ((pur_cd == TableConstants.VM_PMT_FRESH_PUR_CD
                        || pur_cd == TableConstants.VM_PMT_RENEWAL_PUR_CD
                        || pur_cd == TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD) && Util.getSelectedSeat() != null && pmt_dobj.getOff_cd() != Util.getSelectedSeat().getOff_cd() && surrenderPmtOffice) {
                    throw new VahanException("Permit should be surrendered by the issuing authority of permit.");
                }
                if (pmt_dobj.getPmt_type() == Integer.valueOf(TableConstants.AITP)
                        || pmt_dobj.getPmt_type() == Integer.valueOf(TableConstants.NATIONAL_PERMIT)) {
                    PermitHomeAuthImpl homeImpl = new PermitHomeAuthImpl();
                    setAuthDobj(homeImpl.getSelectInVtPermit(pmt_dobj.getRegn_no().toUpperCase()));
                    if (pmt_dobj.getPmt_type() == Integer.parseInt(TableConstants.NATIONAL_PERMIT)) {
                        permit_Dtls_bean.setNationalPermitAuthDetails(pmt_dobj.getRegn_no().toUpperCase());
                    }
                } else {
                    setAuthDobj(null);
                }
                if (TableConstants.TM_ROLE_PMT_SURRENDER_ENTRY == sessionVariables.getActionCodeSelected()) {
                    String[][] data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
                    if (pur_cd == TableConstants.VM_PMT_FRESH_PUR_CD
                            || pur_cd == TableConstants.VM_PMT_RENEWAL_PUR_CD
                            || pur_cd == TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD) {
                        purpose_list.clear();
                        purpose_list.add(new SelectItem("-1", "SELECT"));
                        for (int i = 0; i < data.length; i++) {
                            if (allowedSurrPurCdArray.length == 1) {
                                if (allowedSurrPurCdArray[0].equalsIgnoreCase("ANY")) {
                                    if (String.valueOf(TableConstants.VM_PMT_TRANSFER_PUR_CD).equals(data[i][0]) || data[i][0].equals(String.valueOf(TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD))
                                            || data[i][0].equals(String.valueOf(TableConstants.VM_PMT_CA_PUR_CD)) || data[i][0].equals(String.valueOf(TableConstants.VM_PMT_CANCELATION_PUR_CD))
                                            || data[i][0].equals(String.valueOf(TableConstants.VM_PMT_REPLACE_VEH_PUR_CD)) || data[i][0].equals(String.valueOf(TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD))
                                            || data[i][0].equals(String.valueOf(TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD)) || data[i][0].equals(String.valueOf(TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD))
                                            || data[i][0].equals(String.valueOf(TableConstants.VM_PMT_SUSPENSION_PUR_CD)) || data[i][0].equals(String.valueOf(TableConstants.VM_PMT_TEMP_SUR_PUR_CD))
                                            || data[i][0].equals(String.valueOf(TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD))) {
                                        purpose_list.add(new SelectItem(data[i][0], data[i][1]));
                                    }
                                } else {
                                    if (allowedSurrPurCdArray[0].equals(data[i][0])) {
                                        purpose_list.add(new SelectItem(data[i][0], data[i][1]));
                                        break;
                                    }
                                }
                            } else if (allowedSurrPurCdArray.length > 1) {
                                for (int j = 0; j < allowedSurrPurCdArray.length; j++) {
                                    if (allowedSurrPurCdArray[j].equals(data[i][0])) {
                                        purpose_list.add(new SelectItem(data[i][0], data[i][1]));
                                    }
                                }
                            }
                        }
                    } else if (pur_cd == TableConstants.VM_PMT_TEMP_PUR_CD
                            || pur_cd == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                        purpose_list.clear();
                        setTempspl_pur_cd(pur_cd);
                        purpose_list.add(new SelectItem("-1", "SELECT"));
                        for (int i = 0; i < data.length; i++) {
                            if (data[i][0].equals(String.valueOf(TableConstants.VM_PMT_CANCELATION_PUR_CD))) {
                                purpose_list.add(new SelectItem(data[i][0], data[i][1]));
                            }
                        }
                    }
                }
                // nitin kumar
//                PermitOwnerDetailDobj dobj = ownerImpl.set_Owner_appl_db_to_dobj(regn_number.toUpperCase());
                PermitOwnerDetailDobj dobj = ownerBean.getOwnerDetailsDobj(ownerDetail);
                // nitin kumar
                if (pmt_dobj != null && dobj.getState_cd().equalsIgnoreCase(sessionVariables.getStateCodeSelected())) {
                    ownerBean.setValueinDOBJ(dobj);
                    permit_Dtls_bean.set_Permit_Dtls_dobj_to_bean(pmt_dobj);
                    showgoods = true;
                    showPassenger = true;
                    regn_no = regn_number.toUpperCase();
                    disableBut = false;
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Please Enter Recipt Number", "Please Enter Valid Registration  Number!");
                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                    return;
                }
            }
            if (!CommonUtils.isNullOrBlank(permit_Dtls_bean.getNp_auth_no()) && pmt_dobj.getPmt_type() == Integer.parseInt(TableConstants.NATIONAL_PERMIT)) {
                setShowNpDetails(true);
                PrimeFaces.current().ajax().update("All_detail:npAuth");
            } else {
                setShowNpDetails(false);
                PrimeFaces.current().ajax().update("All_detail:npAuth");
            }

            PrimeFaces.current().ajax().update("All_detail");
            PrimeFaces.current().ajax().update("panalSave");
        } catch (VahanException em) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, em.getMessage(), em.getMessage());
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
        } catch (Exception e) {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Enter Valid Registration  Number!", "Please Enter Valid Registration  Number!");
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void getPermitDetailThroughDataTable() {
        showPermitDetails(selectedValue.getPmt_no(), Integer.valueOf(selectedValue.getPaction_code()), ownerDetail);
    }

    public void saveSurrenderDetails() {
        try {

            if (TableConstants.TM_ROLE_PMT_SURRENDER_ENTRY == sessionVariables.getActionCodeSelected()) {
                VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                parameters.setPUR_CD(Integer.parseInt(purpose));
                if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfig.get("fitness_exempted"), parameters), "saveSurrenderDetails")) {
                    pmtCheckDtsl.getDtlsDobj().setExemptedFitness(true);
                }
            }
            String result = validateForm(pmtCheckDtsl.getDtlsDobj());
            if (!(result == null || ("").equalsIgnoreCase(result))) {
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "", result);
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return;
            }
            if (TableConstants.TM_ROLE_PMT_SURRENDER_ENTRY == sessionVariables.getActionCodeSelected() && TableConstants.VM_PMT_TEMP_SUR_PUR_CD == Integer.parseInt(purpose) && !DateUtils.parseDate(pmtCheckDtsl.dtlsDobj.getTaxUpto()).after(new Date())) {
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "", "Please Pay Your MV Tax");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return;
            }
            if (TableConstants.TM_ROLE_PMT_RESTORE_ENTRY == sessionVariables.getActionCodeSelected()) {
                if (purpose.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_PUR_CD))
                        || purpose.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD))
                        || purpose.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD))) {
                    if (listExistingOwnerDetails.size() == 0) {
                        SurrenderPermitDobj dobj = SurrenderPermitImpl.getRebackPermitDetails(regn_number.toUpperCase(), null, TableConstants.TM_ROLE_PMT_RESTORE_ENTRY);
                        String[] feeDetails = new SurrenderPermitImpl().getPmtSurFeeDetails(regn_no.toUpperCase(), TableConstants.VM_TRANSFER_OWNER_PUR_CD, purpose);
                        if (!(feeDetails != null && !(CommonUtils.isNullOrBlank(feeDetails[0])) && (dobj.getSurrenderDate().compareTo(JSFUtils.getStringToDateyyyyMMdd(feeDetails[1])) <= 0))) {
                            result = "Please transfer the owner (TO Case) first.";
                            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Information", result);
                            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                            return;
                        }
                    }
                }

                if (purpose.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_CA_PUR_CD))) {
                    SurrenderPermitDobj dobj = SurrenderPermitImpl.getRebackPermitDetails(regn_number.toUpperCase(), null, TableConstants.TM_ROLE_PMT_RESTORE_ENTRY);
                    String[] feeDetails = new SurrenderPermitImpl().getPmtSurFeeDetails(regn_no.toUpperCase(), TableConstants.VM_CHANGE_OF_ADDRESS_PUR_CD, purpose);
                    if (listExistingOwnerDetails.size() == 0) {
                        if (!(feeDetails != null && !(CommonUtils.isNullOrBlank(feeDetails[0])) && (dobj.getSurrenderDate().compareTo(JSFUtils.getStringToDateyyyyMMdd(feeDetails[1])) <= 0))) {
                            result = "Please change your address first.";
                            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Information", result);
                            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                            return;
                        }
                    }
                }

                if (purpose.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD))) {
                    if (!renderReAssigment) {
                        result = "Please Re-Assign Vehicle registration.";
                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Information", result);
                        FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                        return;
                    }
                }

                if (purpose.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_REPLACE_VEH_PUR_CD))
                        || purpose.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD))) {
                    if (CommonUtils.isNullOrBlank(getNew_regn_no())) {
                        result = "Please Provide a New Registration Number!";
                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Fill the Vehicle No", result);
                        FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                        return;
                    }
                }
                if (purpose.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD))) {
                    if (!new SurrenderPermitImpl().checkVehicleTransferDetails(regn_no, ownerDetail.getState_cd(), ownerDetail.getOff_cd())) {
                        result = "This is not NOC issued to office";
                        message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Information", result);
                        FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                        return;
                    }
                }
            }

//            if ((TableConstants.TM_ROLE_PMT_SURRENDER_ENTRY == sessionVariables.getActionCodeSelected())
//                    && (Integer.valueOf(purpose) == TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD
//                    || Integer.valueOf(purpose) == TableConstants.VM_PMT_CANCELATION_PUR_CD)) {
//                permit_Dtls_bean.setNationalPermitAuthDetails(regn_no.toUpperCase().trim());
//                if (pmt_dobj.getPmt_type() == Integer.valueOf(TableConstants.NATIONAL_PERMIT) && !CommonUtils.isNullOrBlank(permit_Dtls_bean.getNp_auth_upto()) && !CommonUtils.isNullOrBlank(permit_Dtls_bean.getNp_auth_from())) {
//                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "First Cancel Morth Autharization ");
//                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
//                    return;
//                }
//            }
            if ((TableConstants.TM_ROLE_PMT_SURRENDER_ENTRY == sessionVariables.getActionCodeSelected())
                    && (Integer.valueOf(purpose) == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD
                    || Integer.valueOf(purpose) == TableConstants.VM_PMT_TRANSFER_PUR_CD
                    || Integer.valueOf(purpose) == TableConstants.VM_PMT_CA_PUR_CD
                    || Integer.valueOf(purpose) == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD
                    || Integer.valueOf(purpose) == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD
                    || Integer.valueOf(purpose) == TableConstants.VM_PMT_SUSPENSION_PUR_CD)
                    || Integer.valueOf(purpose) == TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD) {
                if ((pmt_dobj.getPmt_type() == Integer.valueOf(TableConstants.AITP)
                        || pmt_dobj.getPmt_type() == Integer.valueOf(TableConstants.NATIONAL_PERMIT))
                        && (authDobj == null)) {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Authorization details not fetch.Please Fill the deatils throw Administrator Form");
                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                    return;
                } else if ((pmt_dobj.getPmt_type() == Integer.valueOf(TableConstants.AITP)
                        || pmt_dobj.getPmt_type() == Integer.valueOf(TableConstants.NATIONAL_PERMIT))
                        && (authDobj != null)) {
                    if (!CommonUtils.isNullOrBlank(authDobj.getPmtNo()) && !CommonUtils.isNullOrBlank(pmt_dobj.getPmt_no()) && !(authDobj.getPmtNo().trim().equalsIgnoreCase(pmt_dobj.getPmt_no().trim()))) {
                        message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Authorization details not fetch.Please Fill the deatils throw Administrator Form");
                        FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                        return;
                    }
                }
            }
            SurrenderPermitDobj dobj = setDobj();
            if (TableConstants.TM_ROLE_PMT_SURRENDER_ENTRY == sessionVariables.getActionCodeSelected()) {
                OwnerImpl impl = new OwnerImpl();
                Owner_dobj ownDobj = impl.getOwnerDobj(ownerDetail);
                dobj.setTransfer_purpose(getTrans_purpose());
                dobj.setTempspl_purpose(tempspl_pur_cd);
                String[] values = CommonPermitPrintImpl.getPmtValidateMsg(sessionVariables.getStateCodeSelected(), Integer.valueOf(dobj.getPurpose()), ownDobj, permit_Dtls_bean.setPmtDobj(pmt_dobj));
                if (values != null) {
                    JSFUtils.showMessagesInDialog("Error", values[1], FacesMessage.SEVERITY_ERROR);
                    return;
                }
                if (pmt_dobj != null && dobj.getOff_cd() != pmt_dobj.getOff_cd() && Integer.parseInt(purpose) == TableConstants.VM_PMT_CANCELATION_PUR_CD && cancelPmtByIssueOffice) {
                    JSFUtils.showMessagesInDialog("Information", "Cancel permit from issuing authority only.", FacesMessage.SEVERITY_ERROR);
                    return;
                }
            }
            String str = ServerUtil.applicationStatusForPermit(dobj.getRegn_no(), dobj.getState_cd());
            if (CommonUtils.isNullOrBlank(str)) {
                String app_no = SurrenderPermitImpl.saveSurenderPermitDetails(dobj, showNpDetails);
                printDialogBox(app_no);
            } else {
                showApplPendingDialogBox(str);
            }
        } catch (VahanException ex) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Some problem to save data", "Some problem to save data");
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void showPermitVerifyDetails() {
        try {
            SurrenderPermitDobj dobj = SurrenderPermitImpl.getVaPermitTransactionDetails(verify_app_no);
            permit_Dtls_bean.permitComponentReadOnly(true);
            if (dobj != null) {
                pmt_dobj = PermitDetailImpl.getPermitdetails(dobj.getPmt_no());
                owner_dobj = new PermitOwnerDetailDobj();
                ownerImpl = new PermitOwnerDetailImpl();

                PermitOwnerDetailDobj ownerdobj = ownerImpl.setVtOwnerDtlsOnlyDisplay(regn_number.toUpperCase(), sessionVariables.getStateCodeSelected());
                if (ownerdobj != null) {
                    ownerBean.setValueinDOBJ(ownerdobj);
                    pmtCheckDtsl.getAlldetails(regn_number.toUpperCase(), null, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                } else {
                    JSFUtils.setFacesMessage("Owner Details Not Found", "Message", JSFUtils.WARN);
                    return;
                }
                if (Integer.parseInt(dobj.getPurpose()) == TableConstants.VM_PMT_CANCELATION_PUR_CD || Integer.parseInt(dobj.getPurpose()) == TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD) {
                    if (pmt_dobj.getPmt_type() == Integer.parseInt(TableConstants.NATIONAL_PERMIT)) {
                        permit_Dtls_bean.setNationalPermitAuthDetails(pmt_dobj.getRegn_no().toUpperCase());
                        // PermitHomeAuthDobj npDobj = ServerUtil.getNPAuthDetailsAtPrint(regn_number.toUpperCase(), verify_app_no, sessionVariables.getStateCodeSelected());
                        if (!CommonUtils.isNullOrBlank(permit_Dtls_bean.getNp_auth_no())) {
                            setShowNpDetails(true);
                        }
                    }
                }
                regn_no = regn_number;
                permit_Dtls_bean.set_Permit_Dtls_dobj_to_bean(pmt_dobj);
                if (TableConstants.TM_ROLE_PMT_SURRENDER_TRANSFER_VERIFICATION == sessionVariables.getActionCodeSelected()
                        || TableConstants.TM_ROLE_PMT_SURRENDER_TRANSFER_APPROVE == sessionVariables.getActionCodeSelected()
                        || TableConstants.TM_ROLE_PMT_SURRENDER_TRANSFER_IN_DEATH_APPROVE == sessionVariables.getActionCodeSelected()
                        || TableConstants.TM_ROLE_PMT_SURRENDER_TRANSFER_IN_DEATH_VERIFICATION == sessionVariables.getActionCodeSelected()
                        || TableConstants.TM_ROLE_PMT_SUSPENSION_VERIFICATION == sessionVariables.getActionCodeSelected()
                        || TableConstants.TM_ROLE_PMT_SUSPENSION_APPROVAL == sessionVariables.getActionCodeSelected()) {
                    if (!CommonUtils.isNullOrBlank(dobj.getPurpose()) && dobj.getPurpose().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_PUR_CD))
                            || dobj.getPurpose().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD))
                            || dobj.getPurpose().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_SUSPENSION_PUR_CD))) {
                        trans_purpose = dobj.getTransfer_purpose();
                        setRenderTransferCase(true);
                        SelectItem selected_flag = null;
                        for (Object object : transferPurposeList) {
                            selected_flag = ((SelectItem) object).getValue().toString().equalsIgnoreCase(trans_purpose) ? (SelectItem) object : null;
                        }
                        transferPurposeList.clear();
                        transferPurposeList.add(selected_flag);
                        onlyVehReplaceSelect(dobj.getPurpose());
                    }
                } else {
                    purpose = dobj.getPurpose();
                }
                refference_no = dobj.getOrder_no();
                issue_auth = dobj.getOrder_by();
                surrender_dt = dobj.getOrder_dt();
                effect_from = dobj.getOrder_dt();
                remarks = dobj.getRemarks();
                prvdobj = (SurrenderPermitDobj) dobj.clone();
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Enter Valid Vehicle Number!", "Please Enter Valid Vehicle Number!");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return;
            }
        } catch (VahanException | CloneNotSupportedException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void showRestorePermitVerifyDetails() {
        try {
            SurrenderPermitDobj dobj = SurrenderPermitImpl.getVaPermitTransactionDetails(verify_app_no);
            permit_Dtls_bean.permitComponentReadOnly(true);
            if (dobj != null) {
                pmt_dobj = PermitDetailImpl.getVH_Permitdetails(dobj.getRegn_no().toUpperCase(), dobj.getPmt_no().toUpperCase());
                if (pmt_dobj == null) {
                    throw new VahanException("Permit Details not found in corresponding tables. Please contact to System administrator.");
                }
                owner_dobj = new PermitOwnerDetailDobj();
                ownerImpl = new PermitOwnerDetailImpl();
                PermitOwnerDetailDobj ownerdobj = null;
                if (Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD) {
                    ownerdobj = ownerImpl.setVtOwnerDtlsOnlyDisplay(dobj.getNew_regn_no().toUpperCase(), sessionVariables.getStateCodeSelected());
                } else {
                    ownerdobj = ownerImpl.setVtOwnerDtlsOnlyDisplay(regn_number.toUpperCase(), sessionVariables.getStateCodeSelected());
                }

                if (ownerdobj == null && (Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD)) {
                    ownerdobj = ownerImpl.setVhOwnerDtlsOnlyDisplay(dobj.getRegn_no().toUpperCase(), sessionVariables.getStateCodeSelected());
                }
                ownerBean.setValueinDOBJ(ownerdobj);
                regn_no = regn_number;
                permit_Dtls_bean.set_Permit_Dtls_dobj_to_bean(pmt_dobj);
                if (dobj.getPurpose().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_REPLACE_VEH_PUR_CD))
                        || dobj.getPurpose().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD))
                        || dobj.getPurpose().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD))) {
                    if (pmt_dobj.getPmt_type() == Integer.valueOf(TableConstants.AITP)
                            || pmt_dobj.getPmt_type() == Integer.valueOf(TableConstants.NATIONAL_PERMIT)) {
                        PermitHomeAuthImpl homeImpl = new PermitHomeAuthImpl();
                        setAuthDobj(homeImpl.getSelectInVhPermit(dobj.getRegn_no().toUpperCase(), dobj.getPmt_no().toUpperCase()));
                    } else {
                        setAuthDobj(null);
                    }
                    onlyVehReplaceSelect(dobj.getPurpose());
                    newRegnNoDisable = true;
                    HpaImpl hpa_Impl = new HpaImpl();
                    List<HpaDobj> hypth = hpa_Impl.getHypoDetails(null, TableConstants.VM_TRANSACTION_MAST_REM_HYPO, dobj.getNew_regn_no().toUpperCase(), true, sessionVariables.getStateCodeSelected());
                    if (hypth.size() > 0) {
                        renderNewVeh = true;
                    }
                    hypothecationDetails_bean.setListHypthDetails(hypth);
                }
                purpose = dobj.getPurpose();
                if (Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_TRANSFER_PUR_CD || Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD) {
                    trans_purpose = dobj.getTransfer_purpose();
                    setRenderTransferCase(true);
                    SelectItem selected_flag = null;
                    for (Object object : transferPurposeList) {
                        selected_flag = ((SelectItem) object).getValue().toString().equalsIgnoreCase(trans_purpose) ? (SelectItem) object : null;
                    }
                    transferPurposeList.clear();
                    transferPurposeList.add(selected_flag);
                    SurrenderPermitImpl impl = new SurrenderPermitImpl();
                    listExistingOwnerDetails = impl.toOwnerDetails(regn_number.toUpperCase());
                    if (listExistingOwnerDetails.size() > 0) {
                        existingOwnerDisable = true;
                    }
                }
                if (Integer.valueOf(dobj.getPurpose()) == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD) {
                    renderReAssigment = true;
                    ReAssigmentRegnNo = dobj.getNew_regn_no().toUpperCase();
                }
                refference_no = dobj.getOrder_no();
                issue_auth = dobj.getOrder_by();
                surrender_dt = dobj.getOrder_dt();
                effect_from = dobj.getOrder_dt();
                remarks = dobj.getRemarks();
                setNew_regn_no(dobj.getNew_regn_no());
                prvdobj = (SurrenderPermitDobj) dobj.clone();
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Enter Valid Vehicle Number!", "Please Enter Valid Vehicle Number!");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return;
            }
        } catch (VahanException ex) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage());
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void openModifyUploadedDocumentService() {

        try {
            String appl_no = appl_details.getAppl_no();
            dmsUrl = ServerUtil.getVahanPgiUrl(TableConstants.DMS_CON_VER);
            dmsUrl = dmsUrl.replace("ApplNo", appl_no);
            dmsUrl = dmsUrl.replace("ApplStatus", TableConstants.DOCUMENT_MODIFY_STATUS);
            dmsUrl = dmsUrl + TableConstants.SECURITY_KEY;
            PrimeFaces.current().ajax().update("test_opnFrame");
            PrimeFaces.current().executeScript("PF('ifrmDlg').show()");
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

    public void showRebackDetails(String pmtNo) {
        try {
            SurrenderPermitDobj dobj = SurrenderPermitImpl.getRebackPermitDetails(regn_number.toUpperCase(), pmtNo.toUpperCase(), sessionVariables.getActionCodeSelected());
            permit_Dtls_bean.permitComponentReadOnly(true);
            pmt_dobj = PermitDetailImpl.getVH_Permitdetails(dobj.getRegn_no().toUpperCase(), dobj.getPmt_no().toUpperCase());
            if (pmt_dobj.getPmt_type() == Integer.valueOf(TableConstants.AITP)
                    || pmt_dobj.getPmt_type() == Integer.valueOf(TableConstants.NATIONAL_PERMIT)) {
                PermitHomeAuthImpl homeImpl = new PermitHomeAuthImpl();
                setAuthDobj(homeImpl.getSelectInVhPermit(dobj.getRegn_no().toUpperCase(), dobj.getPmt_no().toUpperCase()));
            } else {
                setAuthDobj(null);
            }

            ownerImpl = new PermitOwnerDetailImpl();
            SurrenderPermitImpl surImpl = new SurrenderPermitImpl();
            if (transPurCd == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD) {
                regn_no = regn_number;
                setRegn_number((surImpl.getReAssigmentOldToNew(regn_number.toUpperCase())).toUpperCase());
            }
            PermitOwnerDetailDobj ownerdobj = ownerImpl.setVtOwnerDtlsOnlyDisplay(regn_number.toUpperCase(), sessionVariables.getStateCodeSelected());
            if ((pmt_dobj.getPmt_type() == Integer.valueOf(TableConstants.AITP) || pmt_dobj.getPmt_type() == Integer.valueOf(TableConstants.NATIONAL_PERMIT)) && transPurCd == TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD) {
                PermitHomeAuthImpl homeImpl = new PermitHomeAuthImpl();
                setAuthDobj(homeImpl.getSelectInVtPermit(dobj.getRegn_no().toUpperCase()));
            }
            if ((pmt_dobj.getPmt_type() == Integer.valueOf(TableConstants.AITP) || pmt_dobj.getPmt_type() == Integer.valueOf(TableConstants.NATIONAL_PERMIT))
                    && (transPurCd == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD || transPurCd == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD || transPurCd == TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD)
                    && (authDobj == null)) {
                throw new VahanException("Authorization details not found. Please contact to system administrator");
            }
            if (TableConstants.TM_ROLE_PMT_RESTORE_ENTRY == sessionVariables.getActionCodeSelected() && ownerdobj == null && (transPurCd == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD || transPurCd == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD)) {
                ownerdobj = getOwner_dobj();
            }
            if (pmt_dobj != null && ownerdobj.getState_cd().equalsIgnoreCase(sessionVariables.getStateCodeSelected())) {
                ownerBean.setValueinDOBJ(ownerdobj);
                if (transPurCd != TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD) {
                    regn_no = regn_number;
                }
                permit_Dtls_bean.set_Permit_Dtls_dobj_to_bean(pmt_dobj);
                if (Integer.parseInt(dobj.getPurpose()) == TableConstants.VM_PMT_TRANSFER_PUR_CD
                        || Integer.parseInt(dobj.getPurpose()) == TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD
                        || Integer.parseInt(dobj.getPurpose()) == TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD) {
                    if (Integer.parseInt(dobj.getPurpose()) == TableConstants.VM_PMT_TRANSFER_PUR_CD) {
                        trans_purpose = dobj.getTransfer_purpose();
                        setRenderTransferCase(true);
                        SelectItem selected_flag = null;
                        for (Object object : transferPurposeList) {
                            selected_flag = ((SelectItem) object).getValue().toString().equalsIgnoreCase(trans_purpose) ? (SelectItem) object : null;
                        }
                        transferPurposeList.clear();
                        transferPurposeList.add(selected_flag);
                    }
                    existingOwnerDisable = true;
                    SurrenderPermitImpl impl = new SurrenderPermitImpl();
                    listExistingOwnerDetails = impl.toOwnerDetails(regn_number.toUpperCase());
                } else if (Integer.parseInt(dobj.getPurpose()) == TableConstants.VM_PMT_CA_PUR_CD) {
                    existingOwnerDisable = true;
                    SurrenderPermitImpl impl = new SurrenderPermitImpl();
                    listExistingOwnerDetails = impl.caOwnerDetails(regn_number.toUpperCase());
                } else if (Integer.parseInt(dobj.getPurpose()) == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD) {
                    renderReAssigment = true;
                    ReAssigmentRegnNo = regn_number.toUpperCase();
                } else {
                    renderReAssigment = false;
                    existingOwnerDisable = false;
                    listExistingOwnerDetails = null;
                }
                onlyVehReplaceSelect(dobj.getPurpose());
                if (dobj.getSurrenderDate() != null) {
                    surrender_dt = dobj.getSurrenderDate();
                    surrDtDisable = true;
                }
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Enter Valid Vehicle Number!", "Please Enter Valid Vehicle Number!");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return;
            }
        } catch (VahanException e) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
        } catch (Exception e) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "There are Some Problem to Show details!", "There are Some Problem to Show details!");
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void saveSurrenderVerifyDetails(Status_dobj status_dobj, String compairChange) throws VahanException {
        String result = validateForm(null);
        if (!(result == null || ("").equalsIgnoreCase(result))) {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Please Enter Recipt Number", result);
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            return;
        }
        if (TableConstants.TM_ROLE_PMT_RESTORE_VERIFICATION == sessionVariables.getActionCodeSelected()) {
            if (purpose.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_REPLACE_VEH_PUR_CD))
                    || purpose.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD))) {
                if (CommonUtils.isNullOrBlank(getNew_regn_no())) {
                    result = "please Provide a New Registration Number!";
                    message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Please Enter Recipt Number", result);
                    FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                    return;
                }
            }
        }
        SurrenderPermitDobj dobj = setDobj();
        String flage = SurrenderPermitImpl.saveSurenderPermitVerifyDetails(dobj, appl_details.getAppl_no(), status_dobj, compairChange);
        if (flage == null || ("").equalsIgnoreCase("")) {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Problem in saving data", "Problem in saving data");
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            return;
        }
    }

    public SurrenderPermitDobj setDobj() {
        SurrenderPermitDobj dobj = null;
        try {
            dobj = new SurrenderPermitDobj();
            dobj.setOff_cd(sessionVariables.getOffCodeSelected());
            dobj.setState_cd(sessionVariables.getStateCodeSelected());
            dobj.setRegn_no(regn_no.toUpperCase());
            if (permit_Dtls_bean == null || CommonUtils.isNullOrBlank(permit_Dtls_bean.getPmt_no())) {
                throw new VahanException("Permit details Not found. Please contact to system administrator");
            }
            dobj.setPmt_no(permit_Dtls_bean.getPmt_no().toUpperCase());
            dobj.setPmtType(pmt_dobj.getPmt_type());
            dobj.setPurpose(purpose);
            if (TableConstants.TM_ROLE_PMT_SURRENDER_VERIFICATION == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_SURRENDER_APPROVAL == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_SURRENDER_ENTRY == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_SURRENDER_TRANSFER_APPROVE == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_SURRENDER_TRANSFER_VERIFICATION == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_SURRENDER_TRANSFER_IN_DEATH_APPROVE == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_SURRENDER_TRANSFER_IN_DEATH_VERIFICATION == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_PERMANENT_SURRENDER_VERIFICATION == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_PERMANENT_SURRENDER_APPROVAL == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_CANCEL_VERIFICATION == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_CANCEL_APPROVAL == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_CA_VERIFICATION == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_CA_APPROVAL == sessionVariables.getActionCodeSelected()) {
                dobj.setPur_cd(TableConstants.VM_PMT_SURRENDER_PUR_CD);
            } else if (TableConstants.TM_ROLE_PMT_RESTORE_VERIFICATION == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_RESTORE_APPROVAL == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_RESTORE_ENTRY == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_REPLACE_VEH_VERIFICATION == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_REPLACE_VEH_APPROVAL == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_REPLACE_VEH_ENTRY == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_RE_ASSIGMENT_VERIFICATION == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_RE_ASSIGMENT_APPROVAL == sessionVariables.getActionCodeSelected()) {
                dobj.setPur_cd(TableConstants.VM_PMT_RESTORE_PUR_CD);
            }

            if ((TableConstants.TM_ROLE_PMT_REPLACE_VEH_VERIFICATION == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_REPLACE_VEH_APPROVAL == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_RESTORE_ENTRY == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_TO_REPLACE_VERIFICATION == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_TO_REPLACE_APPROVAL == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_RE_ASSIGMENT_VERIFICATION == sessionVariables.getActionCodeSelected()
                    || TableConstants.TM_ROLE_PMT_RE_ASSIGMENT_APPROVAL == sessionVariables.getActionCodeSelected())
                    && ((purpose.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_REPLACE_VEH_PUR_CD)))
                    || (purpose.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD)))
                    || (purpose.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD))))) {
                if ((purpose.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD)))) {
                    dobj.setNew_regn_no(getReAssigmentRegnNo().toUpperCase());
                } else {
                    dobj.setNew_regn_no(getNew_regn_no().toUpperCase());
                }
            } else {
                dobj.setNew_regn_no(null);
            }
            dobj.setRemarks(remarks.toUpperCase());
            dobj.setOrder_no(refference_no);
            dobj.setOrder_dt(surrender_dt);
            dobj.setOrder_by(issue_auth.toUpperCase());
            dobj.setTransfer_purpose(trans_purpose);
        } catch (VahanException e) {
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage(TableConstants.SomthingWentWrong);
        }
        return dobj;
    }

    public void showPermitApproveDetails() {
        try {
            if (approve_app_no == null || ("-1").equalsIgnoreCase(approve_app_no)) {
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Please Enter Registration Number", "Select Registration Number!");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return;
            }
            regn_number = approve_app_no;
            SurrenderPermitDobj dobj = SurrenderPermitImpl.getApprovePermitdetails(approve_app_no);
            permit_Dtls_bean.permitComponentReadOnly(true);
            pmt_dobj = PermitDetailImpl.getPermitdetails(CommonPermitPrintImpl.getPmtNoThroughVtPermit(regn_number.toUpperCase()));
            if (dobj != null) {
                regn_no = regn_number;
                permit_Dtls_bean.set_Permit_Dtls_dobj_to_bean(pmt_dobj);
                purpose = dobj.getPurpose();
                refference_no = dobj.getOrder_no();
                issue_auth = dobj.getOrder_by();
                surrender_dt = dobj.getOrder_dt();
                effect_from = dobj.getOrder_dt();
                remarks = dobj.getRemarks();
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Please Enter Recipt Number", "Please Enter Valid Registration  Number!");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return;
            }
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public String saveSurrenderApproveDetails(Status_dobj status_dobj, String compairChange) {
        String return_location = "";
        try {
            String result = validateForm(null);
            if (!(result == null || ("").equalsIgnoreCase(result))) {
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Please Enter Recipt Number", result);
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return_location = "";
                return null;
            }
            if (TableConstants.TM_ROLE_PMT_REPLACE_VEH_APPROVAL == sessionVariables.getActionCodeSelected()) {
                if (purpose.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_REPLACE_VEH_PUR_CD))
                        || purpose.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD))) {
                    if (CommonUtils.isNullOrBlank(getNew_regn_no())) {
                        result = "please Provide a New Registration Number!";
                        message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Please Enter Recipt Number", result);
                        FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                        return_location = "";
                        return null;
                    }
                }
            }
            SurrenderPermitDobj dobj = setDobj();
            String appl_no = appl_details.getAppl_no();
            String flag = "";
            if (TableConstants.TM_ROLE_PMT_SURRENDER_APPROVAL == appl_details.getCurrent_action_cd()
                    || TableConstants.TM_ROLE_PMT_SURRENDER_TRANSFER_APPROVE == appl_details.getCurrent_action_cd()
                    || TableConstants.TM_ROLE_PMT_CA_APPROVAL == appl_details.getCurrent_action_cd()
                    || TableConstants.TM_ROLE_PMT_SUSPENSION_APPROVAL == appl_details.getCurrent_action_cd()
                    || TableConstants.TM_ROLE_PMT_SURRENDER_TRANSFER_IN_DEATH_APPROVE == appl_details.getCurrent_action_cd()
                    || TableConstants.TM_ROLE_PMT_PERMANENT_SURRENDER_APPROVAL == appl_details.getCurrent_action_cd()
                    || TableConstants.TM_ROLE_PMT_CANCEL_APPROVAL == appl_details.getCurrent_action_cd()) {
                flag = SurrenderPermitImpl.saveApproveSurrenderPermitDetails(dobj, appl_no, status_dobj, compairChange, nocIssue, showNpDetails);
            } else if (TableConstants.TM_ROLE_PMT_RESTORE_APPROVAL == appl_details.getCurrent_action_cd() || TableConstants.TM_ROLE_PMT_TO_REPLACE_OTHER_OFFICE_APPROVAL == appl_details.getCurrent_action_cd()) {
                flag = SurrenderPermitImpl.saveApproveRestroPermitDetails(dobj, appl_no, status_dobj, compairChange);
            } else if (TableConstants.TM_ROLE_PMT_REPLACE_VEH_APPROVAL == appl_details.getCurrent_action_cd()
                    || TableConstants.TM_ROLE_PMT_TO_REPLACE_APPROVAL == appl_details.getCurrent_action_cd()) {

                flag = SurrenderPermitImpl.saveApprovePurposeOfVehChange(dobj, appl_no, status_dobj, compairChange);
            } else if (TableConstants.TM_ROLE_PMT_RE_ASSIGMENT_APPROVAL == appl_details.getCurrent_action_cd()) {
                dobj.setNew_regn_no(getReAssigmentRegnNo().toUpperCase());
                flag = SurrenderPermitImpl.saveApprovePurposeOfVehChange(dobj, appl_no, status_dobj, compairChange);
            }
            if (flag == null || ("").equalsIgnoreCase(flag)) {
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Problem in saving data", "Problem in saving data");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return_location = "";
            } else if (flag.equalsIgnoreCase("Success")) {
                return_location = "seatwork";
            }
        } catch (VahanException e) {
            return_location = "";
            JSFUtils.showMessage(e.getMessage());
            JSFUtils.showMessagesInDialog("Information", e.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (Exception e) {
            return_location = "";
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            JSFUtils.showMessage(TableConstants.SomthingWentWrong);
        }
        return return_location;
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
    public String save() {
        try {
            String appl_no = getAppl_details().getAppl_no();
            SurrenderPermitImpl impl = new SurrenderPermitImpl();
            SurrenderPermitDobj dobj = setDobj();
            impl.stayOnTheSameStage(ComparisonBeanImpl.changedDataContents(compareChanges()), dobj, appl_no);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return "seatwork";
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        if (prvdobj == null) {
            return compBeanList;
        }
        compBeanList.clear();
        Compare("Purpose", CommonPermitPrintImpl.getLableForSelectedList((ArrayList) purpose_list, prvdobj.getPurpose()), CommonPermitPrintImpl.getLableForSelectedList((ArrayList) purpose_list, getPurpose()), (ArrayList) compBeanList);
        Compare("Issuing By", prvdobj.getOrder_by(), getIssue_auth(), (ArrayList) compBeanList);
        Compare("Order Number", prvdobj.getOrder_no(), getRefference_no(), (ArrayList) compBeanList);
        Compare("Remarks", prvdobj.getRemarks(), getRemarks(), (ArrayList) compBeanList);
        if ((TableConstants.TM_ROLE_PMT_RESTORE_VERIFICATION == sessionVariables.getActionCodeSelected()) || (TableConstants.TM_ROLE_PMT_RESTORE_APPROVAL == sessionVariables.getActionCodeSelected())) {
            if (purpose.equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_REPLACE_VEH_PUR_CD))) {
                Compare("New Registration Number", prvdobj.getNew_regn_no(), getNew_regn_no(), (ArrayList) compBeanList);
            }
        }
        return compBeanList;
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        try {
            if (StringUtils.isBlank(issue_auth)) {
                throw new VahanException("Enter issuing Authority!");
            } else if (StringUtils.isBlank(refference_no)) {
                throw new VahanException("Enter Reference Number!");
            } else if (effect_from == null) {
                throw new VahanException("Enter Effective from Date!");
            } else if (StringUtils.isBlank(remarks)) {
                throw new VahanException("Please Enter Remarks!");
            }
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE) || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                if ((sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_SURRENDER_VERIFICATION)
                        || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_SURRENDER_TRANSFER_VERIFICATION)
                        || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_SURRENDER_TRANSFER_IN_DEATH_VERIFICATION)
                        || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_CA_VERIFICATION)
                        || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_SUSPENSION_VERIFICATION)
                        || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_RESTORE_VERIFICATION)
                        || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_REPLACE_VEH_VERIFICATION)
                        || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_TO_REPLACE_VERIFICATION)
                        || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_PERMANENT_SURRENDER_VERIFICATION)
                        || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_CANCEL_VERIFICATION)
                        || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_RE_ASSIGMENT_VERIFICATION)
                        || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_TO_REPLACE_OTHER_OFFICE_VERIFICATION)) {
                    saveSurrenderVerifyDetails(status, ComparisonBeanImpl.changedDataContents(compareChanges()));
                    if (status.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                        return_location = disapprovalPrint();
                    } else {
                        return_location = "seatwork";
                    }
                }
                if ((sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_SURRENDER_APPROVAL)
                        || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_SURRENDER_TRANSFER_APPROVE)
                        || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_SURRENDER_TRANSFER_IN_DEATH_APPROVE)
                        || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_CA_APPROVAL)
                        || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_SUSPENSION_APPROVAL)
                        || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_RESTORE_APPROVAL)
                        || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_REPLACE_VEH_APPROVAL)
                        || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_TO_REPLACE_APPROVAL)
                        || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_PERMANENT_SURRENDER_APPROVAL)
                        || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_CANCEL_APPROVAL)
                        || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_RE_ASSIGMENT_APPROVAL)
                        || (sessionVariables.getActionCodeSelected() == TableConstants.TM_ROLE_PMT_TO_REPLACE_OTHER_OFFICE_APPROVAL)) {
                    return_location = saveSurrenderApproveDetails(status, ComparisonBeanImpl.changedDataContents(compareChanges()));
                }
            }
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {
                String appl_no = appl_details.getAppl_no();
                SurrenderPermitDobj dobj = setDobj();
                SurrenderPermitImpl impl = new SurrenderPermitImpl();
                status.setPrev_action_cd_selected(app_disapp_dobj.getPre_action_code());
                impl.rebackStatus(status, ComparisonBeanImpl.changedDataContents(compareChanges()), dobj, appl_no);
                return_location = "seatwork";
            }
        } catch (VahanException ve) {
            JSFUtils.showMessage(ve.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage(TableConstants.SomthingWentWrong);
        }
        return return_location;
    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public PermitDetailImpl getPmt_impl() {
        return pmt_impl;
    }

    public void setPmt_impl(PermitDetailImpl pmt_impl) {
        this.pmt_impl = pmt_impl;
    }

    public PermitDetailDobj getPmt_dobj() {
        return pmt_dobj;
    }

    public void setPmt_dobj(PermitDetailDobj pmt_dobj) {
        this.pmt_dobj = pmt_dobj;
    }

    public String getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(String pur_cd) {
        this.pur_cd = pur_cd;
    }

    public boolean isDisableBut() {
        return disableBut;
    }

    public void setDisableBut(boolean disableBut) {
        this.disableBut = disableBut;
    }

    public String getApp_no_msg() {
        return app_no_msg;
    }

    public void setApp_no_msg(String app_no_msg) {
        this.app_no_msg = app_no_msg;
    }

    public void printDialogBox(String app_no) {
        if (("").equalsIgnoreCase(app_no)) {
            setApp_no_msg("Application Number is not genrated");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        } else {
            setApp_no_msg("Application Number generated :" + app_no + ". Please note down the Application No for future reference.");
            PrimeFaces.current().executeScript("PF('appNum').show();");
        }
    }

    public void showApplPendingDialogBox(String str) {
        setApp_no_msg(str);
        PrimeFaces.current().ajax().update("app_num_id");
        PrimeFaces.current().executeScript("PF('appNum').show();");
    }

    public String getNew_regn_no() {
        return new_regn_no;
    }

    public void setNew_regn_no(String new_regn_no) {
        this.new_regn_no = new_regn_no;
    }

    public void onBlurEnent() {
        disableBut = true;
        OwnerImpl owner_Impl = new OwnerImpl();
        try {
            if (TableConstants.TM_ROLE_PMT_RESTORE_ENTRY == sessionVariables.getActionCodeSelected()) {
                regn_number = CommonPermitPrintImpl.getRegnNoinVtPermitTranaction(getRegn_number().toUpperCase());
                if (CommonUtils.isNullOrBlank(regn_number)) {
                    throw new VahanException("Please Enter Valid Vehicle Number!");
                }
            }
            if (!CommonUtils.isNullOrBlank(getNew_regn_no())) {
                try {
//                    if (getPurpose().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD)) && impl.checkTOCasePerform(regn_no.toUpperCase(), getNew_regn_no().toUpperCase())) {
//                        setNew_regn_no("");
//                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Please Transfer the vehicle first."));
//                        return;
//                    }
                    String pmt_details = SurrenderPermitImpl.checkPermitIn_VT_Permit(getNew_regn_no().toUpperCase());
                    String status = SurrenderPermitImpl.checkSameVehClass(sessionVariables.getStateCodeSelected(), regn_number.toUpperCase(), getNew_regn_no().toUpperCase(), transPurCd);

                    if (("no").equalsIgnoreCase(status) && transPurCd == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD) {
                        VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                        parameters.setPERMIT_TYPE(pmt_dobj.getPmt_type());
                        parameters.setVH_CLASS(ownerDetail.getVh_class());
                        if (isCondition(FormulaUtils.replaceTagPermitValues(stateConfig.get("veh_replace_condition"), parameters), "onBlurEnent")) {
                            status = SurrenderPermitImpl.checkVhClassOfReplacedVehicle(sessionVariables.getStateCodeSelected(), regn_number.toUpperCase(), getNew_regn_no().toUpperCase());
                        }
                    }

                    if (("yes").equalsIgnoreCase(status)) {
                        if (("").equals(pmt_details)) {
                            if (getNew_regn_no().toUpperCase().equalsIgnoreCase(regn_number) && !sessionVariables.getStateCodeSelected().equalsIgnoreCase("UK")) {
                                setNew_regn_no("");
                                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Not provide Same Registration Number !!!!"));
                            } else {
                                ownerDobj = owner_Impl.set_Owner_appl_db_to_dobj(getNew_regn_no().toUpperCase(), "", "", 0);
                                if ("RJ,WB".contains(ownerDobj.getState_cd()) && !(ownerDetail.getOwner_name().trim().equalsIgnoreCase(ownerDobj.getOwner_name().trim()))) {
                                    setNew_regn_no("");
                                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "", "New Owner Name Not Match !!!"));
                                } else if (ownerDobj.getState_cd().equalsIgnoreCase("RJ") && JSFUtils.getStringToDateyyyyMMdd(ownerDetail.getRegn_dt()).after(ownerDobj.getRegn_dt())) {
                                    setNew_regn_no("");
                                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Old Vehicle Registered after New Vehicle !!!"));
                                } else {
                                    owner_bean.set_Owner_appl_dobj_to_bean(ownerDobj);
                                    PrimeFaces.current().executeScript("PF('newinfo1').show();");
                                    HpaImpl hpa_Impl = new HpaImpl();
                                    List<HpaDobj> hypth = hpa_Impl.getHypoDetails(null, TableConstants.VM_TRANSACTION_MAST_REM_HYPO, getNew_regn_no().toUpperCase(), true, sessionVariables.getStateCodeSelected());
                                    hypothecationDetails_bean.setListHypthDetails(hypth);
                                    disableBut = false;
                                }
                            }
                        } else {
                            setNew_regn_no("");
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Permit Is already Down : " + pmt_details));
                        }
                    } else {
                        setNew_regn_no("");
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Vehicle Class Not Match !!!"));
                    }
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        } catch (VahanException e) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", e.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", "Some problem to get Old Vehile No. Please contact to System Administrator!"));
        }
    }

    public void newRegistrationProcess() {
        PrimeFaces.current().executeScript("PF('newinfo1').hide()");
        newRegnNoDisable = true;
        disableBut = false;
    }

    public void editVehicleLink() {
        newRegnNoDisable = false;
        disableBut = true;
    }

    public boolean isRenderNewVeh() {
        return renderNewVeh;
    }

    public void setRenderNewVeh(boolean renderNewVeh) {
        this.renderNewVeh = renderNewVeh;
    }

    public PermitOwnerDetailDobj getOwner_dobj() {
        return owner_dobj;
    }

    public void setOwner_dobj(PermitOwnerDetailDobj owner_dobj) {
        this.owner_dobj = owner_dobj;
    }

    public PermitOwnerDetailImpl getOwnerImpl() {
        return ownerImpl;
    }

    public void setOwnerImpl(PermitOwnerDetailImpl ownerImpl) {
        this.ownerImpl = ownerImpl;
    }

    public PermitOwnerDetailBean getOwnerBean() {
        return ownerBean;
    }

    public void setOwnerBean(PermitOwnerDetailBean ownerBean) {
        this.ownerBean = ownerBean;
    }

    /**
     * @return the showPrintPanels
     */
    public boolean isShowPrintPanels() {
        return showPrintPanels;
    }

    /**
     * @param showPrintPanels the showPrintPanels to set
     */
    public void setShowPrintPanels(boolean showPrintPanels) {
        this.showPrintPanels = showPrintPanels;
    }

    /**
     * @return the show_pannel
     */
    public String getShow_pannel() {
        return show_pannel;
    }

    /**
     * @param show_pannel the show_pannel to set
     */
    public void setShow_pannel(String show_pannel) {
        this.show_pannel = show_pannel;
    }

    /**
     * @return the header_msg
     */
    public String getHeader_msg() {
        return header_msg;
    }

    /**
     * @param header_msg the header_msg to set
     */
    public void setHeader_msg(String header_msg) {
        this.header_msg = header_msg;
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
     * @return the pmt_type
     */
    public int getPmt_type() {
        return pmt_type;
    }

    /**
     * @param pmt_type the pmt_type to set
     */
    public void setPmt_type(int pmt_type) {
        this.pmt_type = pmt_type;
    }

    /**
     * @return the pmt_type_desc
     */
    public String getPmt_type_desc() {
        return pmt_type_desc;
    }

    /**
     * @param pmt_type_desc the pmt_type_desc to set
     */
    public void setPmt_type_desc(String pmt_type_desc) {
        this.pmt_type_desc = pmt_type_desc;
    }

    /**
     * @return the goods_to_caryy
     */
    public String getGoods_to_caryy() {
        return goods_to_caryy;
    }

    /**
     * @param goods_to_caryy the goods_to_caryy to set
     */
    public void setGoods_to_caryy(String goods_to_caryy) {
        this.goods_to_caryy = goods_to_caryy;
    }

    /**
     * @return the region_cd
     */
    public int getRegion_cd() {
        return region_cd;
    }

    /**
     * @param region_cd the region_cd to set
     */
    public void setRegion_cd(int region_cd) {
        this.region_cd = region_cd;
    }

    /**
     * @return the region_desc
     */
    public String getRegion_desc() {
        return region_desc;
    }

    /**
     * @param region_desc the region_desc to set
     */
    public void setRegion_desc(String region_desc) {
        this.region_desc = region_desc;
    }

    /**
     * @return the surrender_dt
     */
    public Date getSurrender_dt() {
        if (TableConstants.TM_ROLE_PMT_RESTORE_ENTRY != sessionVariables.getActionCodeSelected()) {
            setSurrender_dt(new Date());
        }
        return surrender_dt;
    }

    /**
     * @param surrender_dt the surrender_dt to set
     */
    public void setSurrender_dt(Date surrender_dt) {
        this.surrender_dt = surrender_dt;
    }

    /**
     * @return the purpose
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * @param purpose the purpose to set
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * @return the issue_auth
     */
    public String getIssue_auth() {
        return issue_auth;
    }

    /**
     * @param issue_auth the issue_auth to set
     */
    public void setIssue_auth(String issue_auth) {
        this.issue_auth = issue_auth;
    }

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * @return the effect_from
     */
    public Date getEffect_from() {
        setEffect_from(new Date());
        return effect_from;
    }

    /**
     * @param effect_from the effect_from to set
     */
    public void setEffect_from(Date effect_from) {
        this.effect_from = effect_from;
    }

    /**
     * @return the refference_no
     */
    public String getRefference_no() {
        return refference_no;
    }

    /**
     * @param refference_no the refference_no to set
     */
    public void setRefference_no(String refference_no) {
        this.refference_no = refference_no;
    }

    /**
     * @return the receipt_no
     */
    public String getReceipt_no() {
        return receipt_no;
    }

    /**
     * @param receipt_no the receipt_no to set
     */
    public void setReceipt_no(String receipt_no) {
        this.receipt_no = receipt_no;
    }

    /**
     * @return the route_from
     */
    public String getRoute_from() {
        return route_from;
    }

    /**
     * @param route_from the route_from to set
     */
    public void setRoute_from(String route_from) {
        this.route_from = route_from;
    }

    /**
     * @return the route_to
     */
    public String getRoute_to() {
        return route_to;
    }

    /**
     * @param route_to the route_to to set
     */
    public void setRoute_to(String route_to) {
        this.route_to = route_to;
    }

    /**
     * @return the showgoods
     */
    public boolean isShowgoods() {
        return showgoods;
    }

    /**
     * @param showgoods the showgoods to set
     */
    public void setShowgoods(boolean showgoods) {
        this.showgoods = showgoods;
    }

    /**
     * @return the showPassenger
     */
    public boolean isShowPassenger() {
        return showPassenger;
    }

    /**
     * @param showPassenger the showPassenger to set
     */
    public void setShowPassenger(boolean showPassenger) {
        this.showPassenger = showPassenger;
    }

    /**
     * @return the permit_surender
     */
    public String getPermit_surender() {
        return permit_surender;
    }

    /**
     * @param permit_surender the permit_surender to set
     */
    public void setPermit_surender(String permit_surender) {
        this.permit_surender = permit_surender;
    }

    /**
     * @return the regn_number
     */
    public String getRegn_number() {
        return regn_number;
    }

    /**
     * @param regn_number the regn_number to set
     */
    public void setRegn_number(String regn_number) {
        this.regn_number = regn_number;
    }

    /**
     * @return the verify_app_no
     */
    public String getVerify_app_no() {
        return verify_app_no;
    }

    /**
     * @param verify_app_no the verify_app_no to set
     */
    public void setVerify_app_no(String verify_app_no) {
        this.verify_app_no = verify_app_no;
    }

    /**
     * @return the showVerifyPanels
     */
    public boolean isShowVerifyPanels() {
        return showVerifyPanels;
    }

    /**
     * @param showVerifyPanels the showVerifyPanels to set
     */
    public void setShowVerifyPanels(boolean showVerifyPanels) {
        this.showVerifyPanels = showVerifyPanels;
    }

    /**
     * @return the showApprovePanels
     */
    public boolean isShowApprovePanels() {
        return showApprovePanels;
    }

    /**
     * @param showApprovePanels the showApprovePanels to set
     */
    public void setShowApprovePanels(boolean showApprovePanels) {
        this.showApprovePanels = showApprovePanels;
    }

    /**
     * @return the showEntryPanels
     */
    public boolean isShowEntryPanels() {
        return showEntryPanels;
    }

    /**
     * @param showEntryPanels the showEntryPanels to set
     */
    public void setShowEntryPanels(boolean showEntryPanels) {
        this.showEntryPanels = showEntryPanels;
    }

    /**
     * @return the approve_app_no
     */
    public String getApprove_app_no() {
        return approve_app_no;
    }

    /**
     * @param approve_app_no the approve_app_no to set
     */
    public void setApprove_app_no(String approve_app_no) {
        this.approve_app_no = approve_app_no;
    }

    public PermitDetailBean getPermit_Dtls_bean() {
        return permit_Dtls_bean;
    }

    public void setPermit_Dtls_bean(PermitDetailBean permit_Dtls_bean) {
        this.permit_Dtls_bean = permit_Dtls_bean;
    }

    public ApproveImpl getApproveImpl() {
        return approveImpl;
    }

    public void setApproveImpl(ApproveImpl approveImpl) {
        this.approveImpl = approveImpl;
    }

    public OwnerBean getOwner_bean() {
        return owner_bean;
    }

    public void setOwner_bean(OwnerBean owner_bean) {
        this.owner_bean = owner_bean;
    }

    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    public List getVerify_app_no_list() {
        return verify_app_no_list;
    }

    public void setVerify_app_no_list(List verify_app_no_list) {
        this.verify_app_no_list = verify_app_no_list;
    }

    public List getPurpose_list() {
        return purpose_list;
    }

    public void setPurpose_list(List purpose_list) {
        this.purpose_list = purpose_list;
    }

    public List getList_vh_class() {
        return list_vh_class;
    }

    public void setList_vh_class(List list_vh_class) {
        this.list_vh_class = list_vh_class;
    }

    public List getList_vm_catg() {
        return list_vm_catg;
    }

    public void setList_vm_catg(List list_vm_catg) {
        this.list_vm_catg = list_vm_catg;
    }

    public List getList_maker_model() {
        return list_maker_model;
    }

    public void setList_maker_model(List list_maker_model) {
        this.list_maker_model = list_maker_model;
    }

    public List<PassengerPermitDetailDobj> getMultiPermitDetails() {
        return multiPermitDetails;
    }

    public void setMultiPermitDetails(List<PassengerPermitDetailDobj> multiPermitDetails) {
        this.multiPermitDetails = multiPermitDetails;
    }

    public PassengerPermitDetailDobj getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(PassengerPermitDetailDobj selectedValue) {
        this.selectedValue = selectedValue;
    }

    public boolean isNewRegnNoDisable() {
        return newRegnNoDisable;
    }

    public void setNewRegnNoDisable(boolean newRegnNoDisable) {
        this.newRegnNoDisable = newRegnNoDisable;
    }

    public boolean isNocIssue() {
        return nocIssue;
    }

    public void setNocIssue(boolean nocIssue) {
        this.nocIssue = nocIssue;
    }

    public boolean isRenderNocIssue() {
        return renderNocIssue;
    }

    public void setRenderNocIssue(boolean renderNocIssue) {
        this.renderNocIssue = renderNocIssue;
    }

    public List<OwnerDetailsDobj> getListExistingOwnerDetails() {
        return listExistingOwnerDetails;
    }

    public void setListExistingOwnerDetails(List<OwnerDetailsDobj> listExistingOwnerDetails) {
        this.listExistingOwnerDetails = listExistingOwnerDetails;
    }

    public boolean isExistingOwnerDisable() {
        return existingOwnerDisable;
    }

    public void setExistingOwnerDisable(boolean existingOwnerDisable) {
        this.existingOwnerDisable = existingOwnerDisable;
    }

    public PermitHomeAuthDobj getAuthDobj() {
        return authDobj;
    }

    public void setAuthDobj(PermitHomeAuthDobj authDobj) {
        this.authDobj = authDobj;
    }

    public HypothecationDetailsBean getHypothecationDetails_bean() {
        return hypothecationDetails_bean;
    }

    public void setHypothecationDetails_bean(HypothecationDetailsBean hypothecationDetails_bean) {
        this.hypothecationDetails_bean = hypothecationDetails_bean;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public int getTransPurCd() {
        return transPurCd;
    }

    public void setTransPurCd(int transPurCd) {
        this.transPurCd = transPurCd;
    }

    public boolean isRenderReAssigment() {
        return renderReAssigment;
    }

    public void setRenderReAssigment(boolean renderReAssigment) {
        this.renderReAssigment = renderReAssigment;
    }

    public String getReAssigmentRegnNo() {
        return ReAssigmentRegnNo;
    }

    public void setReAssigmentRegnNo(String ReAssigmentRegnNo) {
        this.ReAssigmentRegnNo = ReAssigmentRegnNo;
    }

    public boolean isSurrDtDisable() {
        return surrDtDisable;
    }

    public void setSurrDtDisable(boolean surrDtDisable) {
        this.surrDtDisable = surrDtDisable;
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

    public boolean isRenderPushBackSeatPanel() {
        return renderPushBackSeatPanel;
    }

    public void setRenderPushBackSeatPanel(boolean renderPushBackSeatPanel) {
        this.renderPushBackSeatPanel = renderPushBackSeatPanel;
    }

    public String getTrans_purpose() {
        return trans_purpose;
    }

    public void setTrans_purpose(String trans_purpose) {
        this.trans_purpose = trans_purpose;
    }

    public List getTransferPurposeList() {
        return transferPurposeList;
    }

    public void setTransferPurposeList(List transferPurposeList) {
        this.transferPurposeList = transferPurposeList;
    }

    public boolean isRenderTransferCase() {
        return renderTransferCase;
    }

    public void setRenderTransferCase(boolean renderTransferCase) {
        this.renderTransferCase = renderTransferCase;
    }

    public int getTempspl_pur_cd() {
        return tempspl_pur_cd;
    }

    public void setTempspl_pur_cd(int tempspl_pur_cd) {
        this.tempspl_pur_cd = tempspl_pur_cd;
    }

    public PermitCheckDetailsBean getPmtCheckDtsl() {
        return pmtCheckDtsl;
    }

    public void setPmtCheckDtsl(PermitCheckDetailsBean pmtCheckDtsl) {
        this.pmtCheckDtsl = pmtCheckDtsl;
    }

    public boolean isRenderOtherVehicleInfo() {
        return renderOtherVehicleInfo;
    }

    public void setRenderOtherVehicleInfo(boolean renderOtherVehicleInfo) {
        this.renderOtherVehicleInfo = renderOtherVehicleInfo;
    }

    public boolean isMultiPmtAllow() {
        return multiPmtAllow;
    }

    public void setMultiPmtAllow(boolean multiPmtAllow) {
        this.multiPmtAllow = multiPmtAllow;
    }

    public boolean isCancelPmtByIssueOffice() {
        return cancelPmtByIssueOffice;
    }

    public void setCancelPmtByIssueOffice(boolean cancelPmtByIssueOffice) {
        this.cancelPmtByIssueOffice = cancelPmtByIssueOffice;
    }

    public boolean isShowNpDetails() {
        return showNpDetails;
    }

    public void setShowNpDetails(boolean showNpDetails) {
        this.showNpDetails = showNpDetails;
    }

    public boolean isRenderDocUploadTab() {
        return renderDocUploadTab;
    }

    public void setRenderDocUploadTab(boolean renderDocUploadTab) {
        this.renderDocUploadTab = renderDocUploadTab;
    }

    public String getDmsUrl() {
        return dmsUrl;
    }

    public void setDmsUrl(String dmsUrl) {
        this.dmsUrl = dmsUrl;
    }
}
