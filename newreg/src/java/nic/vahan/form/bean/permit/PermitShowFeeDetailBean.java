/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.FeeDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.TaxExemptiondobj;
import nic.vahan.form.dobj.permit.PermitFeeRequiredDataDobj;
import nic.vahan.form.dobj.permit.PermitShowFeeDetailDobj;
import nic.vahan.form.impl.AxleImpl;
import nic.vahan.form.impl.EpayImpl;
import nic.vahan.form.impl.ExemptionFeeFineImpl;
import nic.vahan.form.impl.FeeImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.PermitShowFeeDetailImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import static nic.vahan.form.impl.fancy.AdvanceRegnFeeImpl.replaceTagValues;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.form.impl.tradecert.ApplicationFeeTradeCertImpl;
import nic.vahan.form.dobj.FeeFineExemptionDobj;
import nic.vahan.form.dobj.configuration.TmConfigurationReceipts;

/**
 *
 * @author Naman Jain
 */
@ManagedBean(name = "showFee")
@ViewScoped
public class PermitShowFeeDetailBean extends AbstractApplBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PermitShowFeeDetailBean.class);
    private List<PermitShowFeeDetailDobj> feeShowPanal = null;
    private List<PermitShowFeeDetailDobj> feeShowPanalList = null;
    private PermitShowFeeDetailDobj feeDtlsDobj = null;
    private List purList = new ArrayList();
    private PermitShowFeeDetailImpl feeImpl = null;
    private long permitFee = 0;
    private long permitFine = 0;
    public long totalAmt = 0;
    private long userChrg = 0;
    long extraChrg = 0;
    private boolean renderUserChargesAmountPanel = false;
    private List<FeeDobj> listTransWise = null;
    private long totalUserChrg = 0l;
    private FeeImpl newRegistrationfeeImpl = null;
    PermitShowFeeDetailImpl showFeeImpl = null;
    private int vh_class = 0;
    private int pmtTypeMain = 0;
    public Date pmtValidFrom = null;
    public Date pmtValidUpto = null;
    int feeCollectionSize = 0;
    //public String[] requiredData = new String[15];
    private PermitFeeRequiredDataDobj requiredDataDobj = new PermitFeeRequiredDataDobj();
    private boolean addFeeDisable = false;
    private Map<String, String> stateConfigMap = null;
    private boolean maualFee = false;
    private boolean exemFine = false;
    private String action = null;

    public PermitShowFeeDetailBean() {
        try {
            String[][] data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
            purList.add(new SelectItem("-1", "--- SELECT FEE ---"));
            for (int i = 0; i < data.length; i++) {
                if (data[i][3].equalsIgnoreCase("PF") || Integer.parseInt(data[i][0]) == TableConstants.VM_TRANSACTION_MAST_ALL
                        || Integer.parseInt(data[i][0]) == TableConstants.SCAN_FEE_PUR || Integer.parseInt(data[i][0]) == TableConstants.SOCIETY_FEE_PUR || Integer.parseInt(data[i][0]) == TableConstants.SPECIAL_TAX
                        || Integer.parseInt(data[i][0]) == TableConstants.WELFARE_TAX || Integer.parseInt(data[i][0]) == TableConstants.VM_TRANSACTION_MAST_USER_CHARGES || Integer.parseInt(data[i][0]) == TableConstants.VM_MAST_MANUAL_RECEIPT || Integer.parseInt(data[i][0]) == TableConstants.PERMIT_PAPER_DOCUMENT_CHARGE || Integer.parseInt(data[i][0]) == TableConstants.PAPER_DOCUMENT_CHARGE || Integer.parseInt(data[i][0]) == TableConstants.PASSENGER_TAX || Integer.parseInt(data[i][0]) == TableConstants.STAMP_FEE || Integer.parseInt(data[i][0]) == TableConstants.PRINT_FEE || Integer.parseInt(data[i][0]) == TableConstants.FEE_FINE_EXEMTION) {

                    purList.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
            stateConfigMap = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
            newRegistrationfeeImpl = new FeeImpl();
            showFeeImpl = new PermitShowFeeDetailImpl();
            if (appl_details != null && appl_details.getCurrent_action_cd() == TableConstants.TM_ROLE_PMT_APPL_FEE) {
                appl_details.setPur_cd(TableConstants.VM_PMT_APPLICATION_PUR_CD);
            }
            if (appl_details != null && appl_details.getPur_cd() == TableConstants.VM_PMT_APPLICATION_PUR_CD || appl_details.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD
                    || appl_details.getPur_cd() == TableConstants.VM_PMT_RENEWAL_PUR_CD) {
                requiredDataDobj = showFeeImpl.getVaPermitDetailsWithVtOwnerDetail(appl_details.getAppl_no(), appl_details.getPur_cd(), appl_details.getRegn_no());
            } else if (appl_details.getPur_cd() == TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD) {
                requiredDataDobj = showFeeImpl.getRequiredDtlsFromEnd_Vari(appl_details.getAppl_no(), appl_details.getPur_cd());
            } else if (appl_details.getPur_cd() == TableConstants.VM_PMT_TEMP_PUR_CD || appl_details.getPur_cd() == TableConstants.VM_PMT_SPECIAL_PUR_CD || appl_details.getPur_cd() == TableConstants.VM_PMT_RENEW_TEMP_PUR_CD) {
                requiredDataDobj = showFeeImpl.getVaTempPermitDetailsWithVtOwnerDetail(appl_details.getAppl_no(), appl_details.getPur_cd());
            } else if (appl_details.getPur_cd() == TableConstants.VM_PMT_DUPLICATE_PUR_CD) {
                requiredDataDobj = showFeeImpl.getVaDupPermitDetailsWithVtOwnerDetail(appl_details.getRegn_no(), appl_details.getPur_cd());
            } else if (appl_details.getPur_cd() == TableConstants.VM_PMT_SURRENDER_PUR_CD
                    || appl_details.getPur_cd() == TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD
                    || appl_details.getPur_cd() == TableConstants.VM_PMT_CANCELATION_PUR_CD) {
                requiredDataDobj = showFeeImpl.getVaPermitTransactionDetailsWithVtOwnerDetail(appl_details.getAppl_no(), appl_details.getPur_cd());
            } else if (appl_details.getPur_cd() == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD) {
                requiredDataDobj = showFeeImpl.getVaPermitHomeAuthDetailsWithVtOwnerDetail(appl_details.getAppl_no(), appl_details.getPur_cd());
                if (requiredDataDobj.getPmt_type() > 0) {
                    VehicleParameters vehParameters = new VehicleParameters();
                    vehParameters.setPERMIT_TYPE(requiredDataDobj.getPmt_type());
                    if (!CommonUtils.isNullOrBlank(requiredDataDobj.getValidFromPrvDate()) && isCondition(replaceTagValues(stateConfigMap.get("auth_recursive_fee"), vehParameters), "PermitShowFeeDetailBean()")) {
                        requiredDataDobj.setValidFromTemp(requiredDataDobj.getValidFromPrvDate());
                        requiredDataDobj.setRenderRecursiveAuthfee(true);
                    }
                }
            } else if (appl_details.getPur_cd() == TableConstants.VM_PMT_TRANSFER_PUR_CD
                    || appl_details.getPur_cd() == TableConstants.VM_PMT_CA_PUR_CD
                    || appl_details.getPur_cd() == TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD) {
                requiredDataDobj = showFeeImpl.getVaPermitTransDtlsWithTransferPermit(appl_details.getAppl_no(), appl_details.getPur_cd());
            } else if (appl_details.getPur_cd() == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD
                    || appl_details.getPur_cd() == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD
                    || appl_details.getPur_cd() == TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD
                    || appl_details.getPur_cd() == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD
                    || (appl_details.getPur_cd() == TableConstants.VM_PMT_RESTORE_PUR_CD
                    && appl_details.getCurrent_state_cd().equalsIgnoreCase("KA"))) {
                requiredDataDobj = showFeeImpl.getTransferReplacementCase(appl_details.getAppl_no(), appl_details.getPur_cd());
            } else if (appl_details.getPur_cd() == TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD
                    || appl_details.getPur_cd() == TableConstants.VM_PMT_RENEW_COUNTER_SIGNATURE_PUR_CD) {
                requiredDataDobj = showFeeImpl.getCounterSignatureCase(appl_details.getAppl_no(), appl_details.getPur_cd());
            } else if (appl_details.getPur_cd() == TableConstants.VM_PMT_DUP_COUNTER_SIGNATURE_PUR_CD) {
                requiredDataDobj = showFeeImpl.getDupCounterSignature(appl_details.getRegn_no(), appl_details.getPur_cd());
            } else if (appl_details.getPur_cd() == TableConstants.VM_PMT_LEASE_PUR_CD) {
                requiredDataDobj = showFeeImpl.getLeasePermitCase(appl_details.getAppl_no(), appl_details.getPur_cd());
            } else if (appl_details.getPur_cd() == TableConstants.VM_PMT_COUNTER_SIGNATURE_AUTH_PUR_CD) {
                requiredDataDobj = showFeeImpl.getCounterSignatureAuth(appl_details.getAppl_no(), appl_details.getPur_cd());
            }
            if (requiredDataDobj == null) {
                throw new VahanException("Details not found please check again.");
            }
            requiredDataDobj.setRegn_no(appl_details.getRegn_no());
            requiredDataDobj.setAppl_no(appl_details.getAppl_no());
            feeShowPanal = new ArrayList();
            feeShowPanal = showFeeDetails(requiredDataDobj, feeShowPanal);
            if (feeShowPanal != null && feeShowPanal.size() > 0) {
                for (PermitShowFeeDetailDobj dobj : feeShowPanal) {
                    if (Integer.parseInt(dobj.getPermitAmt()) < 0) {
                        throw new VahanException("Permit Fee can not be paid due to negative Amount");
                    }
                }
            }
            if ((appl_details.getPur_cd() == TableConstants.VM_PMT_RENEWAL_PUR_CD || appl_details.getPur_cd() == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD) && feeShowPanal != null && feeShowPanal.size() > 0) {
                feeShowPanalList = new ArrayList<>();
                int fineAmount = 0;
                int feeAmount = 0;
                String fine_formula = "";
                feeShowPanalList.addAll(feeShowPanal);
                for (PermitShowFeeDetailDobj dobj : feeShowPanalList) {
                    VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(null, null, 0, 0, 0, 0, 0, 0, 0, 0);
                    parameters.setPUR_CD(Integer.parseInt(dobj.getPurCd()));
                    parameters.setPERMIT_TYPE(requiredDataDobj.getPmt_type());
                    parameters.setSERVICE_TYPE(requiredDataDobj.getService_type());
                    if (Integer.parseInt(dobj.getPurCd()) == TableConstants.VM_PMT_RENEWAL_PUR_CD) {
                        parameters.setTAX_DUE_FROM_DATE(requiredDataDobj.getValidUpToPrvDate());
                    }
                    if (Integer.parseInt(dobj.getPenalty()) > 0) {
                        TmConfigurationReceipts configFeeFineZero = ServerUtil.getTmConfigurationReceipts(Util.getUserStateCode());
                        if (configFeeFineZero != null) {
                            if (Integer.parseInt(dobj.getPurCd()) == TableConstants.VM_PMT_RENEWAL_PUR_CD || Integer.parseInt(dobj.getPurCd()) == TableConstants.VM_PMT_APPLICATION_PUR_CD) {
                                parameters.setTAX_DUE_FROM_DATE(requiredDataDobj.getValidUpToPrvDate());
                            } else {
                                parameters.setTAX_DUE_FROM_DATE(requiredDataDobj.getValidAuthUpToDate());
                            }
                            fine_formula = FormulaUtils.getSqlFormulaValue(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("fine_exempted_condition"), parameters), "PermitShowFeeDetailBean()");
                            if (isCondition(FormulaUtils.replaceTagPermitValues(configFeeFineZero.getFineAmtZero(), parameters), "PermitShowFeeDetailBean()") && !Util.getUserStateCode().equalsIgnoreCase("WB")) {
                                fineAmount = fineAmount + Integer.parseInt(dobj.getPenalty());
                            } else if (!CommonUtils.isNullOrBlank(fine_formula) && isCondition(fine_formula, "PermitShowFeeDetailBean()")) {
                                fineAmount = fineAmount + Integer.parseInt(dobj.getPenalty());
                            } else if (!JSFUtils.isNumeric(FormulaUtils.getSqlFormulaValue(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("fine_exempted_condition"), parameters), "PermitShowFeeDetailBean"))
                                    && Boolean.valueOf(FormulaUtils.getSqlFormulaValue(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("fine_exempted_condition"), parameters), "PermitShowFeeDetailBean"))) {
                                fineAmount = fineAmount + Integer.parseInt(dobj.getPenalty());
                            }
                            if (Integer.parseInt(dobj.getPurCd()) == TableConstants.VM_PMT_RENEWAL_PUR_CD && isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("fee_exempted_condition"), parameters), "PermitShowFeeDetailBean()")) {
                                feeAmount = feeAmount + Integer.parseInt(dobj.getPermitAmt());
                            }
                        }
                    } else if (Integer.parseInt(dobj.getPurCd()) == TableConstants.VM_PMT_RENEWAL_PUR_CD && Integer.parseInt(dobj.getPermitAmt()) > 0 && isCondition(FormulaUtils.replaceTagPermitValues(stateConfigMap.get("fee_exempted_condition"), parameters), "PermitShowFeeDetailBean()")) {
                        feeAmount = feeAmount + Integer.parseInt(dobj.getPermitAmt());
                    }
                }
                if (fineAmount > 0) {
                    PermitShowFeeDetailDobj dobj1 = new PermitShowFeeDetailDobj();
                    if (feeAmount > 0) {
                        dobj1.setPermitAmt("-".concat(String.valueOf(feeAmount)));
                    } else {
                        dobj1.setPermitAmt(String.valueOf("0"));
                    }
                    dobj1.setPenalty("-".concat(String.valueOf(fineAmount)));
                    dobj1.setPermitHead(ServerUtil.getTaxHead(TableConstants.FEE_FINE_EXEMTION));
                    dobj1.setPurCd(String.valueOf(TableConstants.FEE_FINE_EXEMTION));
                    dobj1.setPermitHeadDisable(true);
                    dobj1.setDisableMinusBt(true);
                    dobj1.setExem_fee_fine(true);
                    feeShowPanal.add(new PermitShowFeeDetailDobj(dobj1));
                    exemFine = true;
                } else if (feeAmount > 0) {
                    PermitShowFeeDetailDobj dobj1 = new PermitShowFeeDetailDobj();
                    dobj1.setPermitAmt("-".concat(String.valueOf(feeAmount)));
                    dobj1.setPenalty(String.valueOf("0"));
                    dobj1.setPermitHead(ServerUtil.getTaxHead(TableConstants.FEE_FINE_EXEMTION));
                    dobj1.setPurCd(String.valueOf(TableConstants.FEE_FINE_EXEMTION));
                    dobj1.setPermitHeadDisable(true);
                    dobj1.setDisableMinusBt(true);
                    dobj1.setExem_fee_fine(true);
                    feeShowPanal.add(new PermitShowFeeDetailDobj(dobj1));
                    exemFine = true;
                }

            }

            List<TaxExemptiondobj> taxExemList = new ArrayList<>();
            ExemptionFeeFineImpl exemImpl = new ExemptionFeeFineImpl();
            taxExemList = exemImpl.getExemptionDetails(appl_details.getAppl_no());
            if (taxExemList.size() > 0 && !exemFine) {
                PermitShowFeeDetailDobj dobj = new PermitShowFeeDetailDobj();
                dobj.setPermitAmt(String.valueOf("0"));
                dobj.setPenalty(String.valueOf(-taxExemList.get(0).getExemAmount()));
                dobj.setPermitHead(ServerUtil.getTaxHead(TableConstants.FEE_FINE_EXEMTION));
                dobj.setPurCd(String.valueOf(TableConstants.FEE_FINE_EXEMTION));
                dobj.setPermitHeadDisable(true);
                dobj.setDisableMinusBt(true);
                feeShowPanal.add(new PermitShowFeeDetailDobj(dobj));
            }
            Object[] manualFeeReceiptDtlsArr = ApplicationFeeTradeCertImpl.getManualFeeReceiptDtls(appl_details.getAppl_no());
            if (manualFeeReceiptDtlsArr != null) {
                PermitShowFeeDetailDobj dobj = new PermitShowFeeDetailDobj();
                dobj.setPermitAmt("-".concat(String.valueOf(manualFeeReceiptDtlsArr[0])));
                dobj.setPenalty(String.valueOf("0"));
                dobj.setPermitHead(ServerUtil.getTaxHead(TableConstants.VM_MAST_MANUAL_RECEIPT));
                dobj.setPurCd(String.valueOf(TableConstants.VM_MAST_MANUAL_RECEIPT));
                dobj.setPermitHeadDisable(true);
                dobj.setDisableMinusBt(true);
                maualFee = true;
                feeShowPanal.add(new PermitShowFeeDetailDobj(dobj));
            }
            if (appl_details.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD && (requiredDataDobj.getPmt_type() != 0)) {
                if (requiredDataDobj.getPmt_type() == Integer.valueOf(TableConstants.AITP) || requiredDataDobj.getPmt_type() == Integer.valueOf(TableConstants.NATIONAL_PERMIT)) {
                    feeShowPanal = showFeeDetails(showFeeImpl.getHomeAuthDetails(appl_details.getAppl_no(), appl_details.getRegn_no(), requiredDataDobj.getPmt_type()), feeShowPanal);
                }
            }
            Map<String, String> onlinePermitFee = showFeeImpl.getOnlinePermitFeeDtls(requiredDataDobj.getRegn_no(),
                    requiredDataDobj.getAppl_no());
            for (int i = 0; i < feeShowPanal.size(); i++) {
                String pur_cd = feeShowPanal.get(i).getPurCd();
                if (!onlinePermitFee.isEmpty() && ("," + onlinePermitFee.get("trans_pur_cd") + ",").contains("," + pur_cd + ",") && Boolean.valueOf(onlinePermitFee.get(pur_cd))) {
                    feeShowPanal.remove(i);
                    --i;
                } else {
                    permitFee = permitFee + Integer.valueOf(feeShowPanal.get(i).getPermitAmt());
                    permitFine = permitFine + Integer.valueOf(feeShowPanal.get(i).getPenalty());
                    feeCollectionSize++;
                }
            }

            if (requiredDataDobj.getVh_class() == 0) {
                throw new VahanException("Details not found please check");
            }
            vh_class = requiredDataDobj.getVh_class();
            pmtTypeMain = requiredDataDobj.getPmt_type();
            updateTotalPayableAmount(requiredDataDobj.getVh_class(), feeCollectionSize, appl_details.getRegn_no(), appl_details.getPur_cd(), requiredDataDobj.getPmt_type(), maualFee);
        } catch (VahanException e) {
            JSFUtils.showMessagesInDialog("Error", e.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Error", TableConstants.SomthingWentWrong, FacesMessage.SEVERITY_ERROR);
        }
    }

    public void addManualPenalty() throws VahanException {
        PrimeFaces.current().executeScript("PF('bui').show();");
        int feeCollectionSize = 0;
        permitFee = 0;
        permitFine = 0;
        for (PermitShowFeeDetailDobj amt : feeShowPanal) {
            permitFee = permitFee + Integer.valueOf(amt.getPermitAmt());
            if (!CommonUtils.isNullOrBlank(amt.getPenalty())) {
                permitFine = permitFine + Integer.valueOf(amt.getPenalty());
            }
            feeCollectionSize++;
        }
        updateTotalPayableAmount(vh_class, feeCollectionSize, appl_details.getRegn_no(), appl_details.getPur_cd(), pmtTypeMain, false);
        if (JSFUtils.findComponentById("Fee_detail:workbench_tabview", "permitFee")) {
            PrimeFaces.current().ajax().update("Fee_detail:workbench_tabview:permitFee");
        }
        PrimeFaces.current().executeScript("PF('bui').hide();");
    }

    /**
     * @param State_Code = ParaMeter 0
     * @param pur_cd = ParaMeter 1
     * @param pmt_catg = ParaMeter 2
     * @param pmt_type = ParaMeter 3
     * @param period_mode = ParaMeter 4
     * @param period = ParaMeter 5
     * @param region_covered = ParaMeter 6
     * @param vh_class = ParaMeter 7
     * @param seat_cap = ParaMeter 8
     * @param unld_wt = ParaMeter 9
     * @param ld_wt = ParaMeter 10
     * @param regn_no = ParaMeter 11
     */
    public List<PermitShowFeeDetailDobj> showFeeDetails(PermitFeeRequiredDataDobj requiredDataDobj, List<PermitShowFeeDetailDobj> feeShowPanal) throws VahanException {
        try {
            if (requiredDataDobj.getPur_cd() == 0) {
                throw new VahanException("Details not found please check");
            }
            Date validFrom, validUpto = null;
            feeImpl = new PermitShowFeeDetailImpl();
            PermitShowFeeDetailDobj dobj = new PermitShowFeeDetailDobj();
            String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
            for (int i = 0; i < data.length; i++) {
                if ((String.valueOf(requiredDataDobj.getVh_class())).equalsIgnoreCase(data[i][0])) {
                    dobj.setVhClass(data[i][1]);
                    break;
                }
            }
            dobj.setPurCd(String.valueOf(requiredDataDobj.getPur_cd()));
            data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
            for (int i = 0; i < data.length; i++) {
                if (String.valueOf(requiredDataDobj.getPur_cd()).equalsIgnoreCase(data[i][0])) {
                    dobj.setPermitHead(data[i][1]);
                    break;
                }
            }
            Calendar cal = Calendar.getInstance();
            validFrom = cal.getTime();
            if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RENEWAL_PUR_CD && "WB".equalsIgnoreCase(Util.getUserStateCode())) {
                validFrom = showFeeImpl.validFromDateForRenPermit(appl_details.getRegn_no());
            }
            if (validFrom != null && ("Y").equalsIgnoreCase(requiredDataDobj.getPeriod_mode())) {
                validUpto = ServerUtil.dateRange(validFrom, requiredDataDobj.getPeriod(), 0, -1);
            } else if (validFrom != null && ("M").equalsIgnoreCase(requiredDataDobj.getPeriod_mode())) {
                validUpto = ServerUtil.dateRange(validFrom, 0, requiredDataDobj.getPeriod(), -1);
            } else if (validFrom != null && ("D").equalsIgnoreCase(requiredDataDobj.getPeriod_mode())) {
                validUpto = ServerUtil.dateRange(validFrom, 0, 0, requiredDataDobj.getPeriod());
            } else if (validFrom != null && ("W").equalsIgnoreCase(requiredDataDobj.getPeriod_mode())) {
                int Days = (requiredDataDobj.getPeriod() * 7) + 1;
                validUpto = ServerUtil.dateRange(validFrom, 0, 0, Days);
            } else if (validFrom != null && ("L").equalsIgnoreCase(requiredDataDobj.getPeriod_mode())) {
                validUpto = ServerUtil.dateRange(validFrom, requiredDataDobj.getPeriod(), 0, 0);
            }

            if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RENEWAL_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_APPLICATION_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD) {
                int pmtType = 0, pmtCatg = 0;
                OwnerImpl ownImpl = new OwnerImpl();
                Owner_dobj ownDobj = ownImpl.set_Owner_appl_db_to_dobj(appl_details.getRegn_no(), null, null, 0);

                if (requiredDataDobj.getPmt_catg() != 0) {
                    pmtCatg = requiredDataDobj.getPmt_catg();
                }
                if (requiredDataDobj.getPmt_type() != 0) {
                    pmtType = requiredDataDobj.getPmt_type();
                }
                FitnessImpl fitImpl = new FitnessImpl();
                if (pmtType == 106 && "WB".equalsIgnoreCase(Util.getUserStateCode())) {
                    Date axelValidUpto = null;
                    ownDobj.setAxleDobj(AxleImpl.setAxleVehDetails_db_to_dobj(null, appl_details.getRegn_no(), Util.getUserStateCode(), ownDobj.getOff_cd()));
                    if (ownDobj.getAxleDobj() == null) {
                        throw new VahanException("Owner Axle details not found. Please contact to system administrator.");
                    }
                    if (ownDobj.getAxleDobj().getNoOfAxle() <= 2) {
                        axelValidUpto = ServerUtil.dateRange(ownDobj.getRegn_dt(), 12, 0, -1);
                    } else {
                        axelValidUpto = ServerUtil.dateRange(ownDobj.getRegn_dt(), 15, 0, -1);
                    }
                    if (validUpto != null && (validUpto).compareTo(axelValidUpto) > 0) {
                        validUpto = axelValidUpto;
                    }
                } else if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RENEWAL_PUR_CD) {
                    ownDobj.setPmt_type(pmtType);
                    ownDobj.setPmt_catg(pmtCatg);
                    int vehAge = fitImpl.getVehAgeValidity(ownDobj);
                    Date maxValidUpto = null;
                    if (vehAge != 99) {
                        maxValidUpto = ServerUtil.dateRange(ownDobj.getPurchase_dt(), vehAge, 0, -1);
                        if (maxValidUpto != null && maxValidUpto.compareTo(validUpto) <= 0) {
                            validUpto = maxValidUpto;
                        }
                    }
                }
            }

            if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_DUPLICATE_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_SURRENDER_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_CANCELATION_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_TRANSFER_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_CA_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_TEMP_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RENEW_TEMP_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_SPECIAL_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_DUP_COUNTER_SIGNATURE_PUR_CD) {
                validFrom = CommonPermitPrintImpl.getDateDD_MMM_YYYY(requiredDataDobj.getValidFromTemp());
                validUpto = CommonPermitPrintImpl.getDateDD_MMM_YYYY(requiredDataDobj.getValidUptoTemp());
            }
            if (validFrom != null && validUpto != null) {
                dobj.setPermitFrom(CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(validFrom));
                dobj.setPermitUpto(CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(validUpto));
                setPmtValidFrom(validFrom);
                setPmtValidUpto(validUpto);
            }
            if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RENEWAL_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_TRANSFER_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_CA_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_APPLICATION_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RENEW_COUNTER_SIGNATURE_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_DUP_COUNTER_SIGNATURE_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_COUNTER_SIGNATURE_AUTH_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_LEASE_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_SMART_CARD_FEE) {
                feeShowPanal = feeImpl.getPermitFeeDetails(requiredDataDobj, dobj, feeShowPanal, appl_details.getAppl_no());
            } else if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_TEMP_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_SPECIAL_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RENEW_TEMP_PUR_CD) {
                feeShowPanal = feeImpl.getFeeforTempSpecialPermit(requiredDataDobj, dobj, validFrom, validUpto, feeShowPanal);
            } else if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_DUPLICATE_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_SURRENDER_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_CANCELATION_PUR_CD) {
                feeShowPanal = feeImpl.getPermitFeeDetailsOnlyPurCdANDStateCd(requiredDataDobj, dobj, feeShowPanal);
            } else if (requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD
                    || requiredDataDobj.getPur_cd() == TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD) {
                if (requiredDataDobj.getPur_cd() != TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD) {
                    requiredDataDobj.setPur_cd(appl_details.getPur_cd());
                }
                feeShowPanal = feeImpl.getPermitFeeDetailsOnlyPurCdANDStateCd(requiredDataDobj, dobj, feeShowPanal);
            }

        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return feeShowPanal;
    }

    public void updateTotalPayableAmount(int vhClass, int feeCollectionSize, String regn_no, int purcd, int pmtType, boolean manualFeeDetails) throws VahanException {
        boolean serviceChargePerTrans = false;
        setUserChrg(EpayImpl.getPurposeCodeFee(null, vhClass, 99, showFeeImpl.getVhClassCatgWithRegnNo(regn_no, purcd)));
        if (userChrg <= 0) {
            PermitShowFeeDetailImpl pmtShowImpl = new PermitShowFeeDetailImpl();
            setUserChrg(pmtShowImpl.getServicesChargesForPermit(Util.getUserStateCode(), purcd, vhClass, pmtType, showFeeImpl.getVhClassCatgWithRegnNo(regn_no, purcd)));
        }
        setListTransWise(newRegistrationfeeImpl.getIsTransWise(Util.getUserStateCode()));
        if (getListTransWise().get(0).getPerTrans() == true) {
            serviceChargePerTrans = perServiceChargeTransaction(purcd, vhClass, pmtType, regn_no);
        }
        if (getListTransWise().get(0).getPerRcpt() == false && getListTransWise().get(0).getPerTrans() == false) {
            setRenderUserChargesAmountPanel(false);
        } else if ((!manualFeeDetails) && getListTransWise().get(0).getPerTrans() == true && !serviceChargePerTrans && !(Util.getUserStateCode().equalsIgnoreCase("PY") && purcd == TableConstants.VM_PMT_SPECIAL_PUR_CD)) {
            if (getUserChrg() > 0l) {
                extraChrg = (getUserChrg() * feeCollectionSize);
                setRenderUserChargesAmountPanel(true);
            } else {
                setRenderUserChargesAmountPanel(false);
            }
        } else {
            if (getUserChrg() > 0l) {
                extraChrg = getUserChrg();
                setRenderUserChargesAmountPanel(true);
            } else {
                setRenderUserChargesAmountPanel(false);
            }
        }
        totalAmt = permitFee + permitFine + extraChrg;
    }

    public boolean perServiceChargeTransaction(int pur_cd, int vh_class, int pmt_type, String regn_no) {
        boolean service_charge = false;
        String vh_catg = showFeeImpl.getVhClassCatgWithRegnNo(regn_no, pur_cd);
        VehicleParameters vehParameters = new VehicleParameters();
        vehParameters.setPUR_CD(pur_cd);
        vehParameters.setVH_CLASS(vh_class);
        vehParameters.setPERMIT_TYPE(pmt_type);
        vehParameters.setVCH_CATG(vh_catg);
        if (isCondition(replaceTagValues(stateConfigMap.get("service_charge_per_transaction"), vehParameters), "vm_feemast_service(State:" + Util.getUserStateCode() + ",PurCd:" + pur_cd + ")")) {
            service_charge = true;
        }
        return service_charge;
    }

    public void addFee() {
        try {
            PermitShowFeeDetailImpl pmtShowImpl = new PermitShowFeeDetailImpl();
            String[] addPur_cd = pmtShowImpl.getAdditionalFeePurCD(Util.getUserStateCode(), appl_details.getPur_cd());
            if (addPur_cd != null) {
                for (int i = 0; i < addPur_cd.length; i++) {
                    requiredDataDobj.setPur_cd(Integer.valueOf(addPur_cd[i].trim()));
                    for (PermitShowFeeDetailDobj dataValue : feeShowPanal) {
                        requiredDataDobj.setValidFromTemp(dataValue.getPermitFrom());
                        requiredDataDobj.setValidUptoTemp(dataValue.getPermitUpto());
                        break;
                    }
                    feeShowPanal = showFeeDetails(requiredDataDobj, feeShowPanal);
                    permitFee = 0;
                    permitFine = 0;
                    for (PermitShowFeeDetailDobj amt : feeShowPanal) {
                        permitFee = permitFee + Integer.valueOf(amt.getPermitAmt());
                        permitFine = permitFine + Integer.valueOf(amt.getPenalty());
                        feeCollectionSize++;
                    }
                    addFeeDisable = true;
                    updateTotalPayableAmount(vh_class, feeCollectionSize, appl_details.getRegn_no(), appl_details.getPur_cd(), pmtTypeMain, false);
                    if (JSFUtils.findComponentById("Fee_detail:workbench_tabview", "permitFee")) {
                        PrimeFaces.current().ajax().update("Fee_detail:workbench_tabview:permitFee");
                    }
                    PrimeFaces.current().executeScript("PF('bui').hide();");
                }
            } else {
                JSFUtils.showMessagesInDialog("Information", "NOT Allowed to add fee", FacesMessage.SEVERITY_INFO);
                addFeeDisable = true;
            }
        } catch (Exception e) {
        }
    }

    public void calculateFee(String purCd) {
        try {
            requiredDataDobj.setPur_cd(Integer.valueOf(purCd.trim()));
            for (PermitShowFeeDetailDobj dataValue : feeShowPanal) {
                requiredDataDobj.setValidFromTemp(dataValue.getPermitFrom());
                requiredDataDobj.setValidUptoTemp(dataValue.getPermitUpto());
                break;
            }
            PermitShowFeeDetailDobj selectedFeeObject = new PermitShowFeeDetailDobj(purCd);
            int lastIndex = getFeeShowPanal().lastIndexOf(selectedFeeObject);
            getFeeShowPanal().remove(lastIndex);
            if (!getFeeShowPanal().contains(selectedFeeObject)) {
                getFeeShowPanal().remove(selectedFeeObject);
                feeShowPanal = showFeeDetails(requiredDataDobj, feeShowPanal);
                permitFee = 0;
                permitFine = 0;
                for (PermitShowFeeDetailDobj amt : feeShowPanal) {
                    permitFee = permitFee + Integer.valueOf(amt.getPermitAmt());
                    permitFine = permitFine + Integer.valueOf(amt.getPenalty());
                    feeCollectionSize++;
                }
                addFeeDisable = true;
                updateTotalPayableAmount(vh_class, feeCollectionSize, appl_details.getRegn_no(), appl_details.getPur_cd(), pmtTypeMain, false);
                if (JSFUtils.findComponentById("Fee_detail:workbench_tabview", "permitFee")) {
                    PrimeFaces.current().ajax().update("Fee_detail:workbench_tabview:permitFee");
                }
                PrimeFaces.current().executeScript("PF('bui').hide();");
            } else {
                permitFee = 0;
                permitFine = 0;
                for (PermitShowFeeDetailDobj amt : feeShowPanal) {
                    permitFee = permitFee + Integer.valueOf(amt.getPermitAmt());
                    permitFine = permitFine + Integer.valueOf(amt.getPenalty());
                    feeCollectionSize++;
                }
                addFeeDisable = true;
                updateTotalPayableAmount(vh_class, feeCollectionSize, appl_details.getRegn_no(), appl_details.getPur_cd(), pmtTypeMain, false);
                if (JSFUtils.findComponentById("Fee_detail:workbench_tabview", "permitFee")) {
                    PrimeFaces.current().ajax().update("Fee_detail:workbench_tabview:permitFee");
                }
                PrimeFaces.current().executeScript("PF('bui').hide();");
            }
        } catch (Exception e) {
        }
    }

    public void addNewRow(String purCd) {
        String mode = getAction();
        try {
            if ("add".equalsIgnoreCase(mode)) {
                if (CommonUtils.isNullOrBlank(purCd) || Integer.valueOf(purCd) == -1) {
                    JSFUtils.showMessagesInDialog("Information", "Please Select Fee Head!", FacesMessage.SEVERITY_WARN);
                } else {
                    if (getFeeShowPanal().size() == 7) {
                        JSFUtils.showMessagesInDialog("Information", "Maximum number of Fees heads collection reached!", FacesMessage.SEVERITY_WARN);
                    } else {
                        feeShowPanal.add(new PermitShowFeeDetailDobj());
//                        if ("UP".contains(Util.getUserStateCode()) && purList.size() > 0 && appl_details.getPur_cd() == TableConstants.VM_PMT_RENEWAL_PUR_CD && pmtTypeMain == Integer.parseInt(TableConstants.NATIONAL_PERMIT)) {
//                            for (int i = 0; i < purList.size(); i++) {
//                                SelectItem sl = (SelectItem) purList.get(i);
//                                if (((String) sl.getValue()).equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD))) {
//                                    purList.remove(i);
//                                    break;
//                                }
//                            }
//                        }
                        updateTotalPayableAmount(vh_class, feeCollectionSize, appl_details.getRegn_no(), appl_details.getPur_cd(), pmtTypeMain, false);
                        if (JSFUtils.findComponentById("Fee_detail:workbench_tabview", "permitFee")) {
                            PrimeFaces.current().ajax().update("Fee_detail:workbench_tabview:permitFee");
                        }
                        PrimeFaces.current().executeScript("PF('bui').hide();");
                    }
                }
            } else if ("minus".equalsIgnoreCase(mode)) {
                boolean isExist = feeImpl.checkPurCDExiatance(appl_details.getAppl_no(), Integer.valueOf(purCd));
                if (isExist) {
                    JSFUtils.showMessagesInDialog("Information", "Fee Details can't be remove!", FacesMessage.SEVERITY_WARN);
                } else {
                    PermitShowFeeDetailDobj selectedFeeObject = new PermitShowFeeDetailDobj(purCd);
                    int lastIndex = getFeeShowPanal().lastIndexOf(selectedFeeObject);
                    if (lastIndex == 0 && getFeeShowPanal().size() == 1) {
                        feeShowPanal.clear();
                        feeShowPanal.add(new PermitShowFeeDetailDobj());
                        feeShowPanal = showFeeDetails(requiredDataDobj, feeShowPanal);
                        permitFee = 0;
                        permitFine = 0;
                        for (PermitShowFeeDetailDobj amt : feeShowPanal) {
                            permitFee = permitFee + Integer.valueOf(amt.getPermitAmt());
                            permitFine = permitFine + Integer.valueOf(amt.getPenalty());
                            feeCollectionSize++;
                        }
                        addFeeDisable = true;
                        updateTotalPayableAmount(vh_class, feeCollectionSize, appl_details.getRegn_no(), appl_details.getPur_cd(), pmtTypeMain, false);
                        if (JSFUtils.findComponentById("Fee_detail:workbench_tabview", "permitFee")) {
                            PrimeFaces.current().ajax().update("Fee_detail:workbench_tabview:permitFee");
                        }
                        PrimeFaces.current().executeScript("PF('bui').hide();");
                    } else {
                        getFeeShowPanal().remove(lastIndex);
                        permitFee = 0;
                        permitFine = 0;
                        for (PermitShowFeeDetailDobj amt : feeShowPanal) {
                            permitFee = permitFee + Integer.valueOf(amt.getPermitAmt());
                            permitFine = permitFine + Integer.valueOf(amt.getPenalty());
                            feeCollectionSize++;
                        }
                        addFeeDisable = true;
                        updateTotalPayableAmount(vh_class, feeCollectionSize, appl_details.getRegn_no(), appl_details.getPur_cd(), pmtTypeMain, false);
                        if (JSFUtils.findComponentById("Fee_detail:workbench_tabview", "permitFee")) {
                            PrimeFaces.current().ajax().update("Fee_detail:workbench_tabview:permitFee");
                        }
                        PrimeFaces.current().executeScript("PF('bui').hide();");
                    }
                }
            }
        } catch (Exception e) {
        }

    }

    public List<PermitShowFeeDetailDobj> getFeeShowPanal() {
        return feeShowPanal;
    }

    public void setFeeShowPanal(List<PermitShowFeeDetailDobj> feeShowPanal) {
        this.feeShowPanal = feeShowPanal;
    }

    public long getPermitFee() {
        return permitFee;
    }

    public void setPermitFee(long permitFee) {
        this.permitFee = permitFee;
    }

    public long getPermitFine() {
        return permitFine;
    }

    public void setPermitFine(long permitFine) {
        this.permitFine = permitFine;
    }

    public long getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(long totalAmt) {
        this.totalAmt = totalAmt;
    }

    public PermitShowFeeDetailImpl getFeeImpl() {
        return feeImpl;
    }

    public void setFeeImpl(PermitShowFeeDetailImpl feeImpl) {
        this.feeImpl = feeImpl;
    }

    public long getUserChrg() {
        return userChrg;
    }

    public void setUserChrg(long userChrg) {
        this.userChrg = userChrg;
    }

    public boolean isRenderUserChargesAmountPanel() {
        return renderUserChargesAmountPanel;
    }

    public void setRenderUserChargesAmountPanel(boolean renderUserChargesAmountPanel) {
        this.renderUserChargesAmountPanel = renderUserChargesAmountPanel;
    }

    public List<FeeDobj> getListTransWise() {
        return listTransWise;
    }

    public void setListTransWise(List<FeeDobj> listTransWise) {
        this.listTransWise = listTransWise;
    }

    public long getTotalUserChrg() {
        return totalUserChrg;
    }

    public void setTotalUserChrg(long totalUserChrg) {
        this.totalUserChrg = totalUserChrg;
    }

    public long getExtraChrg() {
        return extraChrg;
    }

    public void setExtraChrg(long extraChrg) {
        this.extraChrg = extraChrg;
    }

    public Date getPmtValidFrom() {
        return pmtValidFrom;
    }

    public void setPmtValidFrom(Date pmtValidFrom) {
        this.pmtValidFrom = pmtValidFrom;
    }

    public Date getPmtValidUpto() {
        return pmtValidUpto;
    }

    public void setPmtValidUpto(Date pmtValidUpto) {
        this.pmtValidUpto = pmtValidUpto;
    }

    public PermitShowFeeDetailDobj getFeeDtlsDobj() {
        return feeDtlsDobj;
    }

    public void setFeeDtlsDobj(PermitShowFeeDetailDobj feeDtlsDobj) {
        this.feeDtlsDobj = feeDtlsDobj;
    }

    public boolean isAddFeeDisable() {
        return addFeeDisable;
    }

    public void setAddFeeDisable(boolean addFeeDisable) {
        this.addFeeDisable = addFeeDisable;
    }

    public List getPurList() {
        return purList;
    }

    public void setPurList(List purList) {
        this.purList = purList;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
