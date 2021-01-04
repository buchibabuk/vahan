/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.EpayDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.server.taxcal.DOTaxDetails;
import nic.vahan.server.taxcal.TaxCalVB;
import org.apache.log4j.Logger;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.fillTaxParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.fillVehicleParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.VehicleParameters;
//import tax.web.service.VahanTaxParameters;
import nic.vahan.form.dobj.tax.VahanTaxParameters;
import nic.vahan.CommonUtils.TaxUtils;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.form.dobj.ConvDobj;
import nic.vahan.form.dobj.DOTaxDetail;
import nic.vahan.form.dobj.FeeDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.permit.ExemptionDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.server.ServerUtil;
import static nic.vahan.form.impl.TaxServer_Impl.callTaxService;
import nic.vahan.form.impl.permit.ExemptionImpl;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.FeeFineExemptionDobj;
import nic.vahan.form.dobj.Fee_Pay_Dobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.LifeTimeTaxUpdateDobj;
import nic.vahan.form.dobj.ManualReceiptEntryDobj;
import nic.vahan.form.dobj.ToDobj;
import nic.vahan.form.dobj.VmSmartCardHsrpDobj;
import nic.vahan.form.dobj.configuration.TmConfigurationReceipts;
import nic.vahan.form.dobj.fitness.TmConfigurationFitnessDobj;
import static nic.vahan.form.impl.EpayImpl.getDueDateForNewRegistration;
import static nic.vahan.form.impl.EpayImpl.getFeeUpto;
import static nic.vahan.form.impl.EpayImpl.getPmtPurposeCodeFee;
import static nic.vahan.form.impl.EpayImpl.getPurposeCodeFee;
import static nic.vahan.form.impl.EpayImpl.getServiceChargeType;
import static nic.vahan.form.impl.EpayImpl.getTaxDetails;
import static nic.vahan.form.impl.EpayImpl.getTempFeeOfficeAllotment;
import static nic.vahan.form.impl.EpayImpl.getToTaxAmt;
import static nic.vahan.form.impl.EpayImpl.getValidForTempFeeDetails;
import static nic.vahan.form.impl.EpayImpl.insertFeeBalanceTables;
import static nic.vahan.form.impl.EpayImpl.insertTaxBalanceTables;
import static nic.vahan.form.impl.EpayImpl.isSwpFeePaidForFirstVehicle;
import static nic.vahan.form.impl.TaxServer_Impl.callTaxService;
import static nic.vahan.form.impl.TaxServer_Impl.callKLTaxService;
import nic.vahan.form.impl.fancy.AdvanceRegnFeeImpl;
import nic.vahan.form.impl.permit.PermitShowFeeDetailImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.services.clients.NewFeesAndTaxClient;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author tranC095
 */
public class EpayImpl {

    private static Logger LOGGER = Logger.getLogger(EpayImpl.class);

    public static EpayDobj getPurCDPaymentThruWebService(Owner_dobj ownerDobj, String appl_no, String regn_no, int purCD, boolean hypo) {
        EpayDobj retDobj = new EpayDobj();
        //ConcurrentHashMap mp = new ConcurrentHashMap();
        //FessForApproval cl = null;
        NewFeesAndTaxClient cl = null;
        try {
            // mp.putIfAbsent(cl, cl);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//            Owner_dobj dobj = OwnerImpl.getNewVehicleApplNo("RJ17121200082318");
            ownerDobj.setAppl_no(appl_no);
            ownerDobj.setRegn_no(regn_no);
            ownerDobj.setPurCD(purCD);
            ownerDobj.setHypo(hypo);
            ownerDobj.setActionCd(Util.getSelectedSeat().getAction_cd());
            ownerDobj.setUserCatg(Util.getUserCategory());
            ownerDobj.setEmpCode(Util.getEmpCode());
            // ownerDobj.setState_cd("RJ");
            cl = new NewFeesAndTaxClient("At Approval");
            String param = mapper.writeValueAsString(ownerDobj);
            //commented
            //String rt = cl.NewVehicleDobj_JSON_STRING(String.class, param);
            //added---------------
            String rt = cl.newFeesRecalculation_JSON_STRING(String.class, param);
            //----------------
            retDobj = mapper.readValue(rt, EpayDobj.class);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        return retDobj;
    }

    public static EpayDobj getPurCDPaymentsNewRegistration(Owner_dobj ownerDobj, String appl_no, String regn_no, int purCD, boolean hypo) throws VahanException {
        EpayDobj retDobj = new EpayDobj();
        ArrayList<EpayDobj> list = new ArrayList();
        List<EpayDobj> listFeeTaxPaid = null;
        TransactionManager tmgr = null;
        try {
            if (!(ownerDobj.getRegn_type().equals("O") || ownerDobj.getRegn_type().equals("R"))) {
                ownerDobj.setRegn_dt(ownerDobj.getPurchase_dt());
            }

            tmgr = new TransactionManager("getPurCDPaymentsNewRegistration");
            listFeeTaxPaid = EpayImpl.getFeePaidDetails(appl_no).getList();

            if (listFeeTaxPaid == null || listFeeTaxPaid.isEmpty()) {
                /**
                 * for handling old case where fee is not available and for fee
                 * tax exempted case where re-calculation of fee is not
                 * available
                 */
                retDobj = null;
                return retDobj;
            }

            Map<Integer, Integer> mpNoOfUnits = null;
            PassengerPermitDetailDobj permitDobj = TaxServer_Impl.getPermitInfoForSavedTax(Util.getUserStateCode(),
                    Util.getSelectedSeat().getOff_cd(), appl_no, tmgr);
            if (permitDobj != null) {
                if ("KL".equals(Util.getUserStateCode()) && !CommonUtils.isNullOrBlank(permitDobj.getPmt_type_code())) { // It will remove after one month.
                    if (ownerDobj.getPmt_type() <= 0) {
                        ownerDobj.setPmt_type(Integer.parseInt(permitDobj.getPmt_type_code()));
                    }
                    if (ownerDobj.getPmt_catg() <= 0 && !CommonUtils.isNullOrBlank(permitDobj.getPmtCatg())) {
                        ownerDobj.setPmt_catg(Integer.parseInt(permitDobj.getPmtCatg()));
                    }
                }
                permitDobj.setPmt_type(String.valueOf(ownerDobj.getPmt_type()));
                permitDobj.setPmtCatg(String.valueOf(ownerDobj.getPmt_catg()));
                if (!CommonUtils.isNullOrBlank(ownerDobj.getServicesType())) {
                    permitDobj.setServices_TYPE(String.valueOf(ownerDobj.getServicesType()));
                }
                if (ownerDobj.getRegion_covered() != null && ownerDobj.getRegion_covered().length > 0) {
                    permitDobj.setRegion_covered(String.valueOf(ownerDobj.getRegion_covered().length));
                }
                if (permitDobj.getNoOfAdvUnits() != null) {
                    mpNoOfUnits = NewImpl.getRqrdTaxNoOfAdvUnits(permitDobj.getNoOfAdvUnits());
                }
            }

            java.util.Date payDate = new java.util.Date();
            if (listFeeTaxPaid != null) {
                for (EpayDobj ep : listFeeTaxPaid) {
                    if (ep.getPurCd() == 1) {
                        payDate = ep.getRcptDt();
                        break;
                    }
                }

            }

            List<EpayDobj> mandatoryFeeList = EpayImpl.getFeeDetailsByAction(ownerDobj, "NEWFT",
                    ownerDobj.getVh_class(), ownerDobj.getVch_catg(), permitDobj, payDate);

            mandatoryFeeList.addAll(EpayImpl.getFeeDetailsByAction(ownerDobj, "NEW",
                    ownerDobj.getVh_class(), ownerDobj.getVch_catg(), permitDobj, payDate));

            VahanTaxParameters taxParameters = fillTaxParametersFromDobj(ownerDobj, permitDobj);
            VehicleParameters vehParam = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj, permitDobj);
            vehParam.setNEW_VCH("Y");
            Map<Integer, String> rqrdTaxModes = null;
            if (ownerDobj.getRqrd_tax_modes() != null && !ownerDobj.getRqrd_tax_modes().isEmpty()) {
                rqrdTaxModes = NewImpl.getRqrdTaxModes(ownerDobj.getRqrd_tax_modes());
            }

            long netTaxAmount = 0;
            String sql = "select rcpt_dt,period_from,a.pur_cd,tax_mode,purpose  "
                    + " from get_appl_rcpt_details(?) a,tm_purpose_mast b "
                    + " where a.pur_cd=b.pur_cd and a.tax_mode <> 'B' and fee_type='TX' and period_from is not null order by pur_cd";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(DateUtils.parseDate(rs.getString("period_from"))));
                if ("UP".contains(ownerDobj.getState_cd()) && ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_TEMPORARY)) {
                    if (purCD == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || purCD == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                        long dtDiff = 0L;
                        if (null != ownerDobj.getTempReg() && null != ownerDobj.getTempReg().getTmp_regn_dt()) {
                            dtDiff = DateUtils.getDate1MinusDate2_Days(ownerDobj.getPurchase_dt(), ownerDobj.getTempReg().getTmp_regn_dt());
                        }
                        if (!ServerUtil.isTransport(ownerDobj.getVh_class(), ownerDobj)
                                && (dtDiff > 6)) {
                            taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
                        } else if (DateUtils.compareDates(ownerDobj.getTempReg().getTmp_valid_upto(), new java.util.Date()) == 2) {
                            taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(new java.util.Date()));
                        } else {
                            if (null != ownerDobj.getTempReg().getTmp_valid_upto()) {
                                taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(DateUtils.addToDate(ownerDobj.getTempReg().getTmp_valid_upto(), 1, 1)));
                            }
                        }
                    } else if (ServerUtil.getOfficeCdForDealerTempAppl(appl_no, ownerDobj.getState_cd(), "offCorrection") != 0 && purCD == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                        taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(new java.util.Date()));
                    }
                }
                if ("JH".contains(ownerDobj.getState_cd()) && (TableConstants.VM_REGN_TYPE_TEMPORARY.equals(ownerDobj.getRegn_type()) || TableConstants.VM_REGN_TYPE_NEW.equals(ownerDobj.getRegn_type()))
                        && ownerDobj.getOwner_cd() == 1 && ownerDobj.getVh_class() == 7 && ownerDobj.getOwner_identity() != null) {
                    int panCount = OwnerImpl.checkPanNoCount(ownerDobj.getOwner_identity().getPan_no(), ownerDobj.getState_cd());
                    if (panCount > 0) {
                        taxParameters.setOTHER_CRITERIA(99);
                    }
                }
                taxParameters.setPAYMENT_DATE(DateUtils.parseDate(DateUtils.parseDate(rs.getString("rcpt_dt"))));
                taxParameters.setPUR_CD(rs.getInt("pur_cd"));
                taxParameters.setTAX_MODE(rs.getString("tax_mode"));
                taxParameters.setNEW_VCH("Y");
                taxParameters.setTAX_MODE_NO_ADV(1);
                if (rqrdTaxModes != null && !rqrdTaxModes.isEmpty()) {
                    for (Map.Entry<Integer, String> entry : rqrdTaxModes.entrySet()) {
                        if (entry.getKey() == rs.getInt("pur_cd") && entry.getValue() != null
                                && !entry.getValue().isEmpty() && !entry.getValue().equals(rs.getString("tax_mode"))) {
                            taxParameters.setTAX_MODE(entry.getValue());
                        }
                    }
                }

                if (mpNoOfUnits != null && !mpNoOfUnits.isEmpty()) {
                    Integer noUnit = mpNoOfUnits.get(rs.getInt("pur_cd"));
                    if (noUnit != null && noUnit != 0) {
                        taxParameters.setTAX_MODE_NO_ADV(noUnit);
                    }
                }

                if (rs.getInt("pur_cd") == 70 && "MH".contains(Util.getUserStateCode())) {
                    taxParameters.setNET_TAX_AMT((double) netTaxAmount);
                }

                List<DOTaxDetail> listTaxBreakUp = null;
                try {
                    if (taxParameters.getSTATE_CD().equals("KL")) {
                        int pushbackseat = 0;
                        int ordinaryseat = 0;
                        if (TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(ownerDobj.getVh_class() + ","))) {
                            pushbackseat = ownerDobj.getPush_bk_seat();
                            ordinaryseat = ownerDobj.getOrdinary_seat();
                        }
                        listTaxBreakUp = callKLTaxService(taxParameters, pushbackseat, ordinaryseat, ownerDobj.getRegn_no(), ownerDobj.getChasi_no());
                    } else {
                        listTaxBreakUp = callTaxService(taxParameters);
                    }
                } catch (VahanException vme) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), vme.getMessage()));
                }
                if (listTaxBreakUp == null) {
                    continue;
                }
                //listTaxBreakUp = TaxUtils.sortTaxDetails(listTaxBreakUp);
                int i = 0;
                long finalTaxAmount = 0;
                long finalPenalty = 0;
                EpayDobj epPayDobj = null;
                String taxFrom = null;
                String taxUpto = null;
                for (DOTaxDetail dobj : listTaxBreakUp) {
                    if (i == 0) {
                        taxFrom = dobj.getTAX_FROM();
                    }

                    if (i == listTaxBreakUp.size() - 1) {
                        taxUpto = dobj.getTAX_UPTO() == null ? "One Time" : dobj.getTAX_UPTO();
                    }
                    i++;

                    finalTaxAmount = (long) (finalTaxAmount + dobj.getAMOUNT() + dobj.getSURCHARGE() + dobj.getINTEREST() + dobj.getAMOUNT1() + dobj.getAMOUNT2()
                            - dobj.getREBATE() + dobj.getPRV_ADJ());
                    finalPenalty = (long) (finalPenalty + dobj.getPENALTY());

                    if (rs.getInt("pur_cd") == 58 && "MH".contains(Util.getUserStateCode())) {
                        netTaxAmount = (long) (netTaxAmount + dobj.getAMOUNT());
                    }
                }
                if (i > 0) {
                    epPayDobj = new EpayDobj();
                    epPayDobj.setPurCd(rs.getInt("pur_cd"));
                    epPayDobj.setPurCdDescr(rs.getString("purpose") + "( " + taxFrom + " to " + taxUpto + " )");
                    epPayDobj.setE_TaxFee(finalTaxAmount);
                    epPayDobj.setE_total(finalTaxAmount + finalPenalty);
                    epPayDobj.setE_FinePenalty(finalPenalty);
                    mandatoryFeeList.add(epPayDobj);
                }

            }

            boolean serviceCh = false;

            Map<String, List<Integer>> mapString = new HashMap<>();
            for (EpayDobj ep : listFeeTaxPaid) {
                if (mandatoryFeeList.contains(ep)) {
                    EpayDobj epMan = mandatoryFeeList.get(mandatoryFeeList.indexOf(ep));
                    ep.setDueDate(epMan.getDueDate());
                    ep.setDueDateString(epMan.getDueDateString());
                    if (ep.getTaxMode() == null || !ep.getTaxMode().equals("B")) {
                        ep.setAct_TaxFee(epMan.getE_TaxFee());
                        ep.setAct_FinePenalty(epMan.getE_FinePenalty());
                        ep.setAct_total(epMan.getE_total());
                    }

                    if (mapString.containsKey(ep.getRcpt_no())) {
                        List<Integer> listRcptPurcd = mapString.get(ep.getRcpt_no());
                        listRcptPurcd.add(ep.getPurCd());
                    } else {
                        List<Integer> listRcptPurcd = new ArrayList<>();
                        listRcptPurcd.add(ep.getPurCd());
                        mapString.put(ep.getRcpt_no(), listRcptPurcd);
                    }
                } else if (ep.getPurCd() == 99) {
                    serviceCh = true;

                } else {//for checking whether fee/tax is taken from balance fee module
                    long feeValue = getPurposeCodeFee(ownerDobj, ownerDobj.getVh_class(), ep.getPurCd(), ownerDobj.getVch_catg());
                    java.util.Date dueDate = getDueDateForNewRegistration(ep.getPurCd(), ownerDobj);
                    long fineValue = ServerUtil.getPurposeFineAmount(ownerDobj.getState_cd(), ep.getPurCd(), feeValue, dueDate, ep.getRcptDt(),
                            vehParam, true);
                    ep.setAct_TaxFee(feeValue);
                    ep.setAct_FinePenalty(fineValue);
                    ep.setAct_total(feeValue + fineValue);

                    if (mapString.containsKey(ep.getRcpt_no())) {
                        List<Integer> listRcptPurcd = mapString.get(ep.getRcpt_no());
                        listRcptPurcd.add(ep.getPurCd());
                    } else {
                        List<Integer> listRcptPurcd = new ArrayList<>();
                        listRcptPurcd.add(ep.getPurCd());
                        mapString.put(ep.getRcpt_no(), listRcptPurcd);
                    }

                }
                list.add(ep);

            }

            if (hypo) {
                EpayDobj epHyp = new EpayDobj();
                epHyp.setPurCd(TableConstants.VM_TRANSACTION_MAST_ADD_HYPO);
                if (!list.contains(epHyp) && mandatoryFeeList.contains(epHyp)) {
                    epHyp = mandatoryFeeList.get(mandatoryFeeList.indexOf(epHyp));
                    epHyp.setAct_TaxFee(epHyp.getE_TaxFee());
                    epHyp.setAct_FinePenalty(epHyp.getE_FinePenalty());
                    epHyp.setAct_total(epHyp.getE_total());

                    epHyp.setE_TaxFee(0);
                    epHyp.setE_FinePenalty(0);
                    epHyp.setE_total(0);
                    list.add(epHyp);
                }
            }

            if (serviceCh) {
                long userCh = 0;
                for (Map.Entry<String, List<Integer>> entry : mapString.entrySet()) {
                    for (EpayDobj epD : list) {
                        if (epD.getPurCd() == 99 && epD.getRcpt_no() != null && epD.getRcpt_no().equals(entry.getKey())) {
                            userCh = EpayImpl.getUserChargesFee(ownerDobj, entry.getValue(), vehParam);
                            epD.setAct_TaxFee(userCh);
                            epD.setAct_FinePenalty(0);
                            epD.setAct_total(userCh);
                            break;
                        }
                    }
                }

            }

            retDobj.setList(list);
            long amt = 0;
            long amtAct = 0;
            long amtActFine = 0;
            long amtEpFine = 0;
            long amtEpFee = 0;
            long amtActFee = 0;
            for (EpayDobj ep : list) {
                TmConfigurationReceipts configFeeFineZero = ServerUtil.getTmConfigurationReceipts(Util.getUserStateCode());
                if (configFeeFineZero != null) {
                    vehParam.setTRANSACTION_PUR_CD(ep.getPurCd());
                    if (isCondition(replaceTagValues(configFeeFineZero.getFeeAmtZero(), vehParam), "getPurCDPaymentsNewRegistration- (State: " + Util.getUserStateCode() + ")")) {
                        ep.setAct_TaxFee(0L);
                        ep.setAct_total(ep.getAct_TaxFee() + ep.getAct_FinePenalty());
                    }
                    if (isCondition(replaceTagValues(configFeeFineZero.getFineAmtZero(), vehParam), "getPurCDPaymentsNewRegistration-(State: " + Util.getUserStateCode() + ")")) {
                        ep.setAct_FinePenalty(0L);
                        ep.setAct_total(ep.getAct_TaxFee() + ep.getAct_FinePenalty());
                    }
                }

                amt = amt + ep.getE_total();
                amtAct = amtAct + ep.getAct_total();
                amtEpFee = amtEpFee + ep.getE_TaxFee();
                amtActFee = amtActFee + ep.getAct_TaxFee();
                amtActFine = amtActFine + ep.getAct_FinePenalty();
                amtEpFine = amtEpFine + ep.getE_FinePenalty();
            }
            retDobj.setE_total(amt);
            retDobj.setAct_total(amtAct);
            retDobj.setE_TaxFee(amtEpFee);
            retDobj.setAct_TaxFee(amtActFee);
            retDobj.setAct_FinePenalty(amtActFine);
            retDobj.setE_FinePenalty(amtEpFine);

        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return retDobj;
    }

    public static EpayDobj getPurCdPaymentsRegisteredVehicle(String selectedStateCd, int selectedOffCd, Owner_dobj ownerDobj, String appl_no, String regn_no, int purCD, String reasonForFitness) throws VahanException {
        EpayDobj retDobj = new EpayDobj();
        ArrayList<EpayDobj> list = new ArrayList();
        List<EpayDobj> listFeeTaxPaid = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getPurCdPaymentsRegisteredVehicle");

            listFeeTaxPaid = EpayImpl.getFeePaidDetails(appl_no).getList();
            if (listFeeTaxPaid == null || listFeeTaxPaid.isEmpty()) {
                /**
                 * for handling old case where fee is not available and for fee
                 * tax exempted case where re-calculation of fee is not
                 * available
                 */
                retDobj = null;
                return retDobj;
            }
            List<Integer> listRcptPurcd = new ArrayList<>();
            List<EpayDobj> feeListNeedToPaid = EpayImpl.getFeeDetailsRegisteredVehicle(selectedStateCd, selectedOffCd, ownerDobj, appl_no, ownerDobj.getVh_class(), ownerDobj.getVch_catg(), null, true, listFeeTaxPaid, reasonForFitness);
            boolean serviceCh = false;
            Set<EpayDobj> setPurCdAlreadyExist = new HashSet<>();
            for (EpayDobj ep : listFeeTaxPaid) {
                if (ep.getPurCd() == 99) {
                    serviceCh = true;
                }

                if (feeListNeedToPaid.contains(ep) && !setPurCdAlreadyExist.contains(ep)) {
                    setPurCdAlreadyExist.add(ep);
                    listRcptPurcd.add(ep.getPurCd());
                    EpayDobj epMan = feeListNeedToPaid.get(feeListNeedToPaid.indexOf(ep));
                    ep.setAct_TaxFee(epMan.getE_TaxFee());
                    ep.setAct_FinePenalty(epMan.getE_FinePenalty());
                    ep.setAct_total(epMan.getE_total());
                    ep.setDueDate(epMan.getDueDate());
                    if (epMan.getDueDate() != null) {
                        ep.setDueDateString(epMan.getDueDateString());
                    }
                }
                if (ep.getPurCd() == TableConstants.FEE_FINE_EXEMTION && "DL".equalsIgnoreCase(selectedStateCd)) {
                    ep.setAct_FinePenalty(ep.getE_FinePenalty());
                    ep.setAct_total(ep.getE_total());
                    ep.setAct_TaxFee(ep.getE_TaxFee());

                }
                list.add(ep);
            }

            for (EpayDobj ep : feeListNeedToPaid) {//for additional list if any
                if (!listFeeTaxPaid.contains(ep)) {
                    //EpayDobj epMan = feeListNeedToPaid.get(feeListNeedToPaid.indexOf(ep));
                    listRcptPurcd.add(ep.getPurCd());
                    ep.setAct_TaxFee(ep.getE_TaxFee());
                    ep.setAct_FinePenalty(ep.getE_FinePenalty());
                    ep.setAct_total(ep.getE_total());
                    ep.setE_TaxFee(0);
                    ep.setE_total(0);
                    ep.setE_FinePenalty(0);
                    ep.setPurCdDescr(ServerUtil.getTaxHead(ep.getPurCd()));
                    list.add(ep);
                }
            }///end of additional list if any

            if (serviceCh) {
                long userCh = 0;
                userCh = EpayImpl.getUserChargesFee(ownerDobj, listRcptPurcd, null);
                EpayDobj epSch = new EpayDobj();
                epSch.setPurCd(99);
                int indx = list.indexOf(epSch);
                if (indx >= 0) {
                    epSch = list.get(indx);
                    epSch.setAct_TaxFee(userCh);
                    epSch.setAct_FinePenalty(0);
                    epSch.setAct_total(userCh);
                }
            }
            retDobj.setList(list);

            long amt = 0;
            long amtAct = 0;
            long amtActFine = 0;
            long amtEpFine = 0;
            long amtEpFee = 0;
            long amtActFee = 0;
            for (EpayDobj ep : list) {
                amt = amt + ep.getE_total();
                amtAct = amtAct + ep.getAct_total();
                amtEpFee = amtEpFee + ep.getE_TaxFee();
                amtActFee = amtActFee + ep.getAct_TaxFee();
                amtActFine = amtActFine + ep.getAct_FinePenalty();
                amtEpFine = amtEpFine + ep.getE_FinePenalty();
            }
            retDobj.setE_total(amt);
            retDobj.setAct_total(amtAct);
            retDobj.setE_TaxFee(amtEpFee);
            retDobj.setAct_TaxFee(amtActFee);
            retDobj.setAct_FinePenalty(amtActFine);
            retDobj.setE_FinePenalty(amtEpFine);

        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return retDobj;
    }

    public static EpayDobj getFeePaidDetails(String appl_no) {
        EpayDobj epDobj = new EpayDobj();
        TransactionManager tmgr = null;
        try {
            List<EpayDobj> list = new ArrayList<>();
            tmgr = new TransactionManager("getFeePaidDetails");
            String sql = "select *, purpose || case when period_from is not null then '(' ||  period_from ||"
                    + " ' to ' || COALESCE(period_upto, 'One Time') || ')' else '' end as purpose_label "
                    + "   from get_appl_rcpt_details(?) "
                    + "   order by rcpt_dt, rcpt_no,purpose";

            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                EpayDobj ep = new EpayDobj();
                ep.setPurCd(rs.getInt("pur_cd"));
                ep.setPurCdDescr(rs.getString("purpose_label"));
                ep.setE_TaxFee(rs.getLong("fees"));
                ep.setE_FinePenalty(rs.getInt("fine"));
                ep.setE_total(rs.getLong("fees") + rs.getInt("fine"));
                ep.setRcptDt(DateUtils.parseDate(rs.getString("rcpt_dt")));
                ep.setRcpt_no(rs.getString("rcpt_no"));
                ep.setTaxMode(rs.getString("tax_mode"));
                list.add(ep);
            }

            sql = "select sum(fees)+sum(fine) as sum  from get_appl_rcpt_details(?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                epDobj.setE_total(rs.getLong("sum"));
            }
            epDobj.setList(list);

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return epDobj;

    }

    public static ArrayList<EpayDobj> getPurCD_E_payments(String appl_no, String regn_no, int purCD) throws VahanException {
        ArrayList<EpayDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        VahanException vahanexecption = null;
        int vhClass = 0;
        String vchcatg = null;
        RowSet rs = null;
        RowSet rsEpay = null;
        String purCdDesc = null;

        try {

            //Get Owner Detail
            tmgr = new TransactionManager("getOwnerDetais");
            String sql = "select descr from tm_purpose_mast where pur_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, purCD);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                purCdDesc = rs.getString("descr");
            }

            if (TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE == purCD) {
                sql = "SELECT * from  " + TableList.VA_OWNER
                        + " where appl_no=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_no);
                rs = tmgr.fetchDetachedRowSet_No_release();

                if (rs.next()) {
                    vhClass = rs.getInt("vh_class");
                    vchcatg = rs.getString("vch_catg");
                }

            } else if (TableConstants.VM_TRANSACTION_MAST_TEMP_REG == purCD) {
                sql = "SELECT * from va_owner_temp where appl_no=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_no);
                rs = tmgr.fetchDetachedRowSet_No_release();

                if (rs.next()) {
                    vhClass = rs.getInt("vh_class");
                    vchcatg = rs.getString("vch_catg");
                }

            } else {
                sql = "SELECT * from  " + TableList.VT_OWNER
                        + " where regn_no=? and state_cd = ? and off_cd = ?";
                //paramValue = regn_no;
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regn_no);
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                rs = tmgr.fetchDetachedRowSet_No_release();

                if (rs.next()) {
                    vhClass = rs.getInt("vh_class");
                    vchcatg = rs.getString("vch_catg");
                }

            }

            String sqlfee = "SELECT sum(fees) fees,sum(fine) fine from vt_fee,vp_appl_rcpt_mapping "
                    + " where vp_appl_rcpt_mapping.rcpt_no=vt_fee.rcpt_no and vp_appl_rcpt_mapping.appl_no=?"
                    + "and  vt_fee.pur_cd=? group by (vp_appl_rcpt_mapping.appl_no)";

            ps = tmgr.prepareStatement(sqlfee);
            ps.setString(1, appl_no);
            ps.setInt(2, purCD);

            rs = tmgr.fetchDetachedRowSet_No_release();

            while (rs.next()) // found
            {
                EpayDobj dobj = new EpayDobj();
                dobj.setPurCd(purCD);
                dobj.setPurCdDescr(purCdDesc);
                dobj.setE_TaxFee(rs.getInt("fees"));
                dobj.setE_FinePenalty(rs.getInt("fine"));
                dobj.setE_total(dobj.getE_TaxFee()
                        + dobj.getE_FinePenalty()); //+ dobj.getE_cess() + dobj.getE_service_charge());
                dobj.setAppl_no(appl_no);
                ps = tmgr.prepareStatement("SELECT * from get_purpose_fee(?,?,?) ");
                ps.setInt(1, purCD);
                ps.setInt(2, vhClass);
                ps.setString(3, vchcatg);
                RowSet rsAmt = tmgr.fetchDetachedRowSet_No_release();
                if (rsAmt.next()) {
                    dobj.setAct_TaxFee(rsAmt.getInt("fee_amt"));
                    dobj.setAct_FinePenalty(0);
                    dobj.setAct_total(dobj.getAct_TaxFee() + dobj.getAct_FinePenalty()); //+ dobj.getAct_cess() + dobj.getAct_service_charge());
                }
                list.add(dobj);
            }
            ArrayList<DOTaxDetails> listtax = getTaxDetails(appl_no, purCD, regn_no);
            if (listtax != null) {
                ps = tmgr.prepareStatement("SELECT sum(tax_amt) tax_amt,sum(tax_fine) tax_fine,max(vt_tax.pur_cd)  pur_cd"
                        + " FROM vt_tax,vp_appl_rcpt_mapping "
                        + " where vt_tax.rcpt_no=vp_appl_rcpt_mapping.rcpt_no and vp_appl_rcpt_mapping.appl_no=?"
                        + " group by (vp_appl_rcpt_mapping.appl_no,vt_tax.pur_cd) ");
                ps.setString(1, appl_no);
                rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    EpayDobj dobj = new EpayDobj();
                    if (rs.getInt("pur_cd") == TableConstants.TM_ROAD_TAX) {
                        dobj.setPurCdDescr(TableConstants.TM_ROAD_TAX_DESC);
                    } else if (rs.getInt("pur_cd") == TableConstants.TM_ADDN_ROAD_TAX) {
                        dobj.setPurCdDescr(TableConstants.TM_ADDN_ROAD_TAX_DESC);
                    }
                    dobj.setPurCd(rs.getInt("pur_cd"));
                    dobj.setE_TaxFee(rs.getInt("tax_amt"));
                    dobj.setE_FinePenalty(rs.getInt("tax_fine"));
                    dobj.setE_total(dobj.getE_TaxFee() + dobj.getE_FinePenalty()); //+ dobj.getE_cess()+ dobj.getE_service_charge());
                    dobj.setAppl_no(appl_no);
                    for (int i = 0; i < listtax.size(); i++) {
                        DOTaxDetails doTax = listtax.get(i);
                        if (doTax.getPUR_CD() == dobj.getPurCd()) {
                            dobj.setAct_TaxFee(doTax.getAMOUNT());
                            dobj.setAct_FinePenalty(doTax.getFINE());
                            dobj.setAct_total(dobj.getAct_TaxFee() + dobj.getAct_FinePenalty()); //+ dobj.getAct_cess() + dobj.getAct_service_charge());
                        }
                    }
                    list.add(dobj);
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanexecption = new VahanException("Error in fetching details for [ " + appl_no + "]");
            throw vahanexecption;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return list;
    }

    public static ArrayList<DOTaxDetails> getTaxDetails(String appl_no, int purCD, String regn_no) throws VahanException {
        ArrayList listTax = null;

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        VahanException vahanexecption = null;
        String[] params = new String[16];
        RowSet rs = null;
        String taxmode = null;
        try {

            //Get Owner Detail
            tmgr = new TransactionManager("getOwnerDetais");
            if (TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE == purCD) {

                ps = tmgr.prepareStatement("SELECT tax_mode from vt_tax,vp_appl_rcpt_mapping map "
                        + " where map.rcpt_no=vt_tax.rcpt_no and  map.appl_no=? ");
                ps.setString(1, appl_no);
                rs = tmgr.fetchDetachedRowSet_No_release();

                if (rs.next()) {
                    taxmode = rs.getString("tax_mode");
                }

                ps = tmgr.prepareStatement("SELECT * from va_owner where appl_no=? ");
                ps.setString(1, appl_no);
                rs = tmgr.fetchDetachedRowSet_No_release();

                if (rs.next()) {
                    params[0] = regn_no;
                    params[1] = taxmode;//"L";  // HardCoded taxmode= 
                    ps = tmgr.prepareStatement("SELECT class_type from vm_vh_class where vh_class=?");
                    ps.setInt(1, rs.getInt("vh_class"));
                    RowSet rspvtcom = tmgr.fetchDetachedRowSet_No_release();
                    if (rspvtcom.next()) {
                        params[2] = rspvtcom.getString("class_type");  //|pvtcomm=" + 
                    }

                    params[3] = rs.getString("regn_type");  //registrationtype
                    params[4] = rs.getString("owner_cd");   //ownership
                    params[5] = DateUtils.getDateInDDMMYYYY(rs.getDate("purchase_dt"));  //purchasedate
                    params[6] = rs.getString("vh_class");  //vehicleclass
                    params[7] = rs.getString("seat_cap");  //seatcap
                    params[8] = rs.getString("unld_wt");   //unladenwt
                    params[9] = rs.getString("ld_wt");  //ladenwt

                    params[10] = rs.getString("hp"); // hp
                    params[11] = rs.getString("cubic_cap"); //cc
                    params[12] = rs.getString("sale_amt"); //saleamount
                    params[13] = rs.getString("fuel"); //fueltype
                    //  params[14]= ""; //flag
                    params[14] = rs.getString("vch_catg"); //vchcatg
                    params[15] = rs.getString("ac_fitted").equals("T") ? "TRUE" : "FALSE"; //acfitted

                }

            } else if (TableConstants.VM_TRANSACTION_MAST_TEMP_REG == purCD) {
            } else {

                ps = tmgr.prepareStatement("SELECT * from " + TableList.VT_OWNER
                        + "  where regn_no=? and state_cd = ? and off_cd = ? ");
                ps.setString(1, regn_no);
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                rs = tmgr.fetchDetachedRowSet_No_release();

                if (rs.next()) {
                    params[0] = regn_no;
                    params[1] = "L";  // HardCoded taxmode= 
                    ps = tmgr.prepareStatement("SELECT class_type from vm_vh_class where vh_class=?");
                    ps.setInt(1, rs.getInt("vh_class"));
                    RowSet rspvtcom = tmgr.fetchDetachedRowSet_No_release();
                    if (rspvtcom.next()) {
                        params[2] = rspvtcom.getString("class_type");  //|pvtcomm=" + 
                    }

                    params[3] = rs.getString("regn_type");  //registrationtype
                    params[4] = rs.getString("owner_cd");   //ownership
                    params[5] = DateUtils.getDateInDDMMYYYY(rs.getDate("purchase_dt"));  //purchasedate
                    params[6] = rs.getString("vh_class");  //vehicleclass
                    params[7] = rs.getString("seat_cap");  //seatcap
                    params[8] = rs.getString("unld_wt");   //unladenwt
                    params[9] = rs.getString("ld_wt");  //ladenwt

                    params[10] = rs.getString("hp"); // hp
                    params[11] = rs.getString("cubic_cap"); //cc
                    params[12] = rs.getString("sale_amt"); //saleamount
                    params[13] = rs.getString("fuel"); //fueltype
                    //  params[14]= ""; //flag
                    params[14] = rs.getString("vch_catg"); //vchcatg
                    params[15] = rs.getString("ac_fitted").equals("T") ? "Y" : "N"; //acfitted // Y/N changed By ambrish.

                }

            }

            DOTaxDetails[] dobj = TaxCalVB.calculateTax(params);
            if (dobj != null) {
                listTax = new ArrayList();
                for (int i = 0; i < dobj.length; i++) {
                    listTax.add(dobj[i]);
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanexecption = new VahanException("Error in fetching details for [ " + appl_no + "]");
            throw vahanexecption;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return listTax;

    }

    public static String intimateUser(List<EpayDobj> listDobj) throws Exception {

        String message = null;
        //for (int i = 0; i < listDobj.size(); i++) {
        String regn_no = null;
        boolean flg = false;
        TransactionManager tmg = null;
        try {
            tmg = new TransactionManager("intimateUser");

            for (int i = 0; i < listDobj.size(); i++) {

                EpayDobj dobj = listDobj.get(i);
                String applno = dobj.getAppl_no();
                //dobj.getAct_FinePenalty();
                //dobj.getAct_TaxFee();
                int pur_cd = dobj.getPurCd();

                String sql = "Select vt_fee.* from vt_fee,vp_appl_rcpt_mapping where appl_no=? "
                        + " and pur_cd=?  and vt_fee.rcpt_no=vp_appl_rcpt_mapping.rcpt_no ";

                PreparedStatement ps = tmg.prepareStatement(sql);
                ps.setString(1, applno);
                ps.setInt(2, pur_cd);
                RowSet rs = tmg.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    regn_no = rs.getString("regn_no");
                    sql = "Select * from vt_fee_balance where  appl_no=? "
                            + " and pur_cd=?  ";
                    // tmg = new TransactionManager("intimateUser");
                    ps = tmg.prepareStatement(sql);
                    ps.setString(1, applno);
                    ps.setInt(2, pur_cd);
                    rs = tmg.fetchDetachedRowSet_No_release();
                    if (!rs.next()) {
                        if (((dobj.getAct_TaxFee() - dobj.getE_TaxFee()) != 0) || ((dobj.getAct_FinePenalty() - dobj.getE_FinePenalty()) != 0)) {
                            insertFeeBalanceTables(dobj, regn_no, tmg);
                            flg = true;
                        }

                    }

                }

                sql = "Select vt_tax.* from vt_tax,vp_appl_rcpt_mapping where "
                        + " vt_tax.rcpt_no=vp_appl_rcpt_mapping.rcpt_no and vp_appl_rcpt_mapping.appl_no=? "
                        + " and vt_tax.pur_cd=?  ";
                //tmg = new TransactionManager("intimateUser");
                ps = tmg.prepareStatement(sql);
                ps.setString(1, applno);
                ps.setInt(2, pur_cd);
                rs = tmg.fetchDetachedRowSet_No_release();
                Date taxFrdate = null;
                Date taxToDate = null;
                if (rs.next()) {
                    regn_no = rs.getString("regn_no");
                    taxFrdate = rs.getDate("tax_from");
                    taxToDate = rs.getDate("tax_upto");

                    sql = "Select * from vt_tax_balance where appl_no=? "
                            + " and pur_cd=?  ";
                    // tmg = new TransactionManager("intimateUser");
                    ps = tmg.prepareStatement(sql);
                    ps.setString(1, applno);
                    ps.setInt(2, pur_cd);
                    rs = tmg.fetchDetachedRowSet_No_release();
                    if (!rs.next()) {
                        if (((dobj.getAct_TaxFee() - dobj.getE_TaxFee()) != 0) || ((dobj.getAct_FinePenalty() - dobj.getE_FinePenalty()) != 0)) {
                            insertTaxBalanceTables(dobj, regn_no, taxFrdate, taxToDate, tmg);
                            flg = true;
                        }
                    }

                }

                if (flg) {
                    message = "Message Is Send to User";
                } else {
                    message = "User is already Informed";
                }
            }

            tmg.commit();

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmg != null) {
                tmg.release();
            }
        }
        return message;
    }

    public static void insertFeeBalanceTables(EpayDobj dobj, String regn_no, TransactionManager tmg) throws VahanException, SQLException {
        String sqlInsert = "insert into vt_fee_balance "
                + "(appl_no,regn_no,pur_cd,bal_fees,bal_fine,entered_by,entered_on,state_cd,off_cd)"
                + " values(?,?,?,?,?,?,current_timestamp,?,?)";
        PreparedStatement ps = tmg.prepareStatement(sqlInsert);
        ps.setString(1, dobj.getAppl_no());
        ps.setString(2, regn_no);
        ps.setInt(3, dobj.getPurCd());
        ps.setLong(4, dobj.getAct_TaxFee() - dobj.getE_TaxFee());
        ps.setLong(5, dobj.getAct_FinePenalty() - dobj.getE_FinePenalty());
        ps.setString(6, Util.getEmpCode());
        ps.setString(7, Util.getUserStateCode());
        ps.setInt(8, Util.getUserSeatOffCode());
        ps.executeUpdate();

    }

    public static void insertTaxBalanceTables(EpayDobj dobj, String regn_no, Date taxFrdate, Date taxToDate, TransactionManager tmg) throws VahanException, SQLException {

        String sqlInsert = "insert into vt_tax_balance "
                + "(appl_no,regn_no,pur_cd,tax_from,tax_upto,bal_tax_amt,bal_tax_fine,entered_by,entered_on,state_cd,off_cd)"
                + " values(?,?,?,?,?,?,?,?,current_timestamp,?,?)";
        PreparedStatement ps = tmg.prepareStatement(sqlInsert);
        ps.setString(1, dobj.getAppl_no());
        ps.setString(2, regn_no);
        ps.setInt(3, dobj.getPurCd());
        ps.setDate(4, taxFrdate);
        ps.setDate(5, taxToDate);
        ps.setLong(6, dobj.getAct_TaxFee() - dobj.getE_TaxFee());
        ps.setLong(7, dobj.getAct_FinePenalty() - dobj.getE_FinePenalty());
        ps.setString(8, Util.getEmpCode());
        ps.setString(9, Util.getUserStateCode());
        ps.setInt(10, Util.getUserSeatOffCode());
        ps.executeUpdate();
    }

    // Added by Prashant 30-10-2014
    /**
     *
     * @param stateCd State Code
     * @param feeAction Fee Action : example NEW
     * @param vehClass
     * @param purCd
     * @param vehCatg
     * @return List<E_pay_dobj>
     */
    public static List<EpayDobj> getFeeDetailsByAction(Owner_dobj ownerdobj, String feeAction,
            int vehClass, String vehCatg, PassengerPermitDetailDobj permitDobj, java.util.Date paymentDate) throws VahanException {
        List<EpayDobj> list = new ArrayList<EpayDobj>();
        TransactionManager tmgr = null;
        int selectSeatOffCd = 0;
        try {
            selectSeatOffCd = Util.getUserSeatOffCode();
            if (selectSeatOffCd == 0) {
                throw new VahanException("Something went wrong please go to Home page and try again.");
            }
            VehicleParameters parameters = FormulaUtils.fillVehicleParametersFromDobj(ownerdobj, permitDobj);
            parameters.setNEW_VCH("Y");
            parameters.setPUR_CD(Util.getSelectedSeat().getPur_cd());
            VahanTaxParameters taxParameters = null;
            TmConfigurationDobj conf = Util.getTmConfiguration();

            String whereIam = "E_pay_Impl.getFeeDetailsByAction";
            String feeSQL = "select * from dealer.vc_action_purpose_map dl,tm_purpose_mast pm where dl.pur_cd=pm.pur_cd  and state_cd= ? "
                    + " and action in (?, 'ALL') and fee_type != '" + TableConstants.TM_FEE_TAX_TYPE + "'";
            tmgr = new TransactionManager(whereIam);
            PreparedStatement ps = tmgr.prepareStatement(feeSQL);
            ps.setString(1, ownerdobj.getState_cd());
            ps.setString(2, feeAction);

            RowSet rsFeeApplicable = tmgr.fetchDetachedRowSet_No_release();

            while (rsFeeApplicable.next()) {
                java.util.Date fromDt = null;
                java.util.Date uptoDt = null;
                boolean blnShowTax = false;
                if (isCondition(replaceTagValues(rsFeeApplicable.getString("condition_formula"), parameters), "getFeeDetailsByAction-1")) {
                    int pur_cd = rsFeeApplicable.getInt("pur_cd");
                    String pur_cdDescr = rsFeeApplicable.getString("descr");
                    if (pur_cd == 1 && Util.getUserStateCode() != null && Util.getUserStateCode().equals("KA") && ownerdobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_CONFISCATED_AUCTION) && Util.getTmConfiguration().isRegnNoNotAssignOthState() && ownerdobj.getAuctionDobj() != null && ownerdobj.getAuctionDobj().getRegnNo() != null && !ownerdobj.getAuctionDobj().getRegnNo().equals("NEW")) {
                        pur_cd = 5;
                        pur_cdDescr = "Transfer of Ownership";
                    }
                    long feeValue = 0;
                    long fineValue = 0;
                    String taxMods = "L";
                    if (rsFeeApplicable.getBoolean("amt_from_wsdl")) {

                        ps = tmgr.prepareStatement("SELECT *  FROM vm_allowed_mods where state_cd=? and  pur_cd=? ");
                        ps.setString(1, Util.getUserStateCode());
                        ps.setInt(2, pur_cd);
                        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                        while (rs.next()) {
                            if (isCondition(replaceTagValues(rs.getString("condition_formula"), parameters), "getFeeDetailsByAction-2")) {
                                String[] taxModsArr = rs.getString("mods").split(",");
                                taxMods = taxModsArr[0];
                                blnShowTax = true;
                            }
                        }

                        taxParameters = fillTaxParametersFromDobj(ownerdobj, permitDobj);
                        taxParameters.setREGN_TYPE("N");
                        taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(new java.util.Date()));
                        taxParameters.setTAX_MODE(taxMods);
                        taxParameters.setPUR_CD(pur_cd);
                        taxParameters.setNEW_VCH("Y");

                        List<DOTaxDetail> listTaxBreakUp = callTaxService(taxParameters);
                        if (listTaxBreakUp != null) {
                            listTaxBreakUp = TaxUtils.sortTaxDetails(listTaxBreakUp);
                            for (DOTaxDetail doTax : listTaxBreakUp) {
                                feeValue = (long) (feeValue + doTax.getAMOUNT());
                            }
                            if (blnShowTax) {
                                fromDt = DateUtils.parseDate(listTaxBreakUp.get(0).getTAX_FROM());
                                uptoDt = DateUtils.parseDate(listTaxBreakUp.get(listTaxBreakUp.size() - 1).getTAX_UPTO());
                            }
                        }

                    } else {

                        if (pur_cd == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {
                            NewVehicleFitnessImpl newVehicleFitnessImpl = new NewVehicleFitnessImpl();
                            boolean isFailed = newVehicleFitnessImpl.isFailedFitness(ownerdobj.getAppl_no(), pur_cd, Util.getSelectedSeat().getOff_cd(), Util.getUserStateCode()); // fetching only this field for now
                            int temp_pur_cd = 0;
                            if (isFailed && "DL".equalsIgnoreCase(Util.getUserStateCode())) {
                                pur_cd = TableConstants.VM_VEHICLE_INSPECTION_FEE;
                            }
                        }
                        feeValue = getPurposeCodeFee(ownerdobj, vehClass, pur_cd, vehCatg);
                        if (feeValue == 0 && pur_cd == TableConstants.VM_TRANSACTION_MAST_TEMP_TAX && conf.isTemp_tax_as_mvtax()) {
                            taxParameters = fillTaxParametersFromDobj(ownerdobj, permitDobj);
                            taxParameters.setREGN_TYPE("N");
                            taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(new java.util.Date()));
                            taxParameters.setTAX_MODE("M");
                            taxParameters.setPUR_CD(58);
                            taxParameters.setNEW_VCH("Y");
                            List<DOTaxDetail> listTaxBreakUp = callTaxService(taxParameters);
                            for (DOTaxDetail doTax : listTaxBreakUp) {
                                feeValue = (long) (feeValue + doTax.getAMOUNT());
                            }

                        } else if (pur_cd == TableConstants.VM_PMT_FRESH_PUR_CD || pur_cd == TableConstants.VM_PMT_APPLICATION_PUR_CD
                                || pur_cd == TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD || pur_cd == TableConstants.VM_PMT_SURCHARG_FEE) {
                            feeValue = getPmtPurposeCodeFee(ownerdobj, pur_cd, permitDobj, "Fee");
                        } else if (pur_cd == TableConstants.VM_TRANSACTION_MAST_CHOICE_NO) {
                            Map<String, String> choiceDetails = OwnerChoiceNoImpl.getOwnerChoiceRegnNoDetails(ownerdobj.getAppl_no());
                            if (choiceDetails != null && !choiceDetails.isEmpty()) {
                                feeValue = Long.parseLong(choiceDetails.get("choice_fees"));
                            } else {
                                continue;
                            }
                        }
                    }
                    if (paymentDate == null) {
                        paymentDate = new java.util.Date();
                    }

                    // for Orissa if rto is different than assigned office
                    if (conf != null && conf.getTmConfigDealerDobj() != null && !conf.getTmConfigDealerDobj().isTempRegnFeeWithNewForSameOffices() && Util.getUserCategory().equals(TableConstants.USER_CATG_DEALER) && conf.isTempFeeInNewRegis() && pur_cd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG && rsFeeApplicable.getString("action").equals("NEW")) {
                        Integer offCd = null;
                        List offCdList = ServerUtil.getOffCode(Util.getEmpCode());
                        if (!offCdList.isEmpty()) {
                            offCd = Integer.valueOf(offCdList.get(0).toString());
                        }
                        if (selectSeatOffCd == offCd || getTempFeeOfficeAllotment(offCd, selectSeatOffCd, Util.getUserStateCode())) {
                            continue;
                        }
                    }

                    if ("UP".contains(Util.getUserStateCode()) && isCondition(replaceTagValues("<33> in (13)", parameters), "updateForNewRegAppl") && Util.getTmConfiguration() != null && Util.getTmConfiguration().getTmConfigDealerDobj() != null && !Util.getTmConfiguration().getTmConfigDealerDobj().isTempRegnFeeWithNewForSameOffices() && Util.getUserCategory().equals(TableConstants.USER_CATG_OFF_STAFF) && Util.getTmConfiguration().isTempFeeInNewRegis() && pur_cd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG && rsFeeApplicable.getString("action").equals("NEW")) {
                        String isValidForTempFeeAtRTO = getValidForTempFeeDetails(Util.getUserStateCode(), Util.getUserSeatOffCode(), ownerdobj.getAppl_no());
                        if (isValidForTempFeeAtRTO != null && "TRUE".equalsIgnoreCase(isValidForTempFeeAtRTO)) {
                            continue;
                        }
                    }

                    java.util.Date dueDate = getDueDateForNewRegistration(pur_cd, ownerdobj);
                    if ("KA,OR,CG,GA,JH,BR".contains(ownerdobj.getState_cd()) && ownerdobj.getOtherStateVehDobj() != null && ownerdobj.getOtherStateVehDobj().getNocDate() != null) {
                        //dueDate = ownerdobj.getOtherStateVehDobj().getNocDate();
                        if ("OR".contains(ownerdobj.getState_cd()) && pur_cd == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {
                            dueDate = ownerdobj.getFit_upto();
                        } else {
                            dueDate = ownerdobj.getOtherStateVehDobj().getNocDate();
                        }
                    }

                    if (ownerdobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_CONFISCATED_AUCTION) && ownerdobj.getAuctionDobj() != null) {
                        dueDate = ownerdobj.getAuctionDobj().getAuctionDate();
                    }
                    if (pur_cd == TableConstants.VM_PMT_FRESH_PUR_CD || pur_cd == TableConstants.VM_PMT_APPLICATION_PUR_CD
                            || pur_cd == TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD) {
                        fineValue = getPmtPurposeCodeFee(ownerdobj, pur_cd, permitDobj, "Fine");
                    } else {

                        fineValue = ServerUtil.getPurposeFineAmount(ownerdobj.getState_cd(), pur_cd, feeValue,
                                dueDate, paymentDate, parameters, true);
                    }
                    EpayDobj ePayDobj = new EpayDobj();
                    ePayDobj.setPurCd(pur_cd);
                    ePayDobj.setPurCdDescr(pur_cdDescr);
                    ePayDobj.setE_FinePenalty((int) fineValue);
                    ePayDobj.setE_TaxFee((int) feeValue);
                    ePayDobj.setE_total((int) (feeValue + ePayDobj.getE_FinePenalty()));
                    ePayDobj.setFromDate(fromDt);
                    ePayDobj.setUptoDate(uptoDt);
                    ePayDobj.setDueDate(dueDate);
                    if (ePayDobj.getDueDate() != null) {
                        ePayDobj.setDueDateString(ServerUtil.parseDateToString(ePayDobj.getDueDate()));
                    }
                    list.add(ePayDobj);
                }
            }
            List<ManualReceiptEntryDobj> manualRcptList = ManualReceiptEntryImpl.getManualReceiptEntryDetails(ownerdobj.getAppl_no());
            for (ManualReceiptEntryDobj manualrcptdobj : manualRcptList) {
                EpayDobj ePayDobj = new EpayDobj();
                ePayDobj.setPurCd(manualrcptdobj.getPur_cd());
                ePayDobj.setE_TaxFee(-manualrcptdobj.getAmount());
                ePayDobj.setE_FinePenalty(0l);
                list.add(ePayDobj);
            }

        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return list;
    }

    public static java.util.Date getDueDateForNewRegistration(int pur_cd, Owner_dobj ownerdobj) throws VahanException {
        java.util.Date dueDate = new java.util.Date();
        try {
            if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                    || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                //New Registration
                dueDate = ownerdobj.getPurchase_dt();
                if (ownerdobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_TEMPORARY)
                        && ownerdobj.getTempReg() != null && ownerdobj.getTempReg().getTmp_valid_upto() != null) {
                    if ("RJ".equals(Util.getUserStateCode())) {
                        dueDate = ownerdobj.getPurchase_dt();
                    } else if ("HP".equals(Util.getUserStateCode())) {
                        dueDate = ownerdobj.getTempReg().getTmp_valid_upto();
                    } else {
                        dueDate = DateUtils.addToDate(ownerdobj.getTempReg().getTmp_valid_upto(), 1, 1);
                    }
                } else if (ownerdobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)) {
                    dueDate = new java.util.Date();
                } else if (ownerdobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_CD)) {
                    dueDate = ownerdobj.getCdDobj().getSaleDate();
                }
            } else if (pur_cd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG
                    || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                dueDate = ownerdobj.getPurchase_dt();
            } else if (pur_cd == TableConstants.VM_TRANSACTION_MAST_FIT_CERT
                    && (ownerdobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                    || ownerdobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))
                    && ownerdobj.getFit_upto() != null) {
                dueDate = DateUtils.addToDate(ownerdobj.getFit_upto(), 1, 1);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem in getting Due Date, please try after sometime or contact Administrator.");
        }
        return dueDate;
    }

    public static List<EpayDobj> getFeeDetailsByActionTax(Owner_dobj ownerdobj, String feeAction) {
        List<EpayDobj> list = new ArrayList<EpayDobj>();
        TransactionManager tmgr = null;
        VehicleParameters parameters = FormulaUtils.fillVehicleParametersFromDobj(ownerdobj);
        try {

            String whereIam = "getFeeDetailsByActionTax";
            String feeSQL = "select case when pm.pur_cd=58 then 1 else 2 end as orderby, dl.*, pm.descr "
                    + " from dealer.vc_action_purpose_map dl, tm_purpose_mast pm "
                    + " where dl.pur_cd=pm.pur_cd  and state_cd= ? "
                    + " and action=? and fee_type in ('" + TableConstants.TM_FEE_TAX_TYPE + "') "
                    + " ORDER BY 1";
            tmgr = new TransactionManager(whereIam);
            PreparedStatement ps = tmgr.prepareStatement(feeSQL);
            ps.setString(1, ownerdobj.getState_cd());
            ps.setString(2, feeAction);

            RowSet rsFeeApplicable = tmgr.fetchDetachedRowSet();
            boolean taxExempted = false;
            while (rsFeeApplicable.next()) {
                int pur_cd = rsFeeApplicable.getInt("pur_cd");
                whereIam = "getFeeDetailsByActionTax{" + ownerdobj.getState_cd() + ",Action:" + feeAction + ",Pur:" + pur_cd + "}";
                if (isCondition(replaceTagValues(rsFeeApplicable.getString("condition_formula"), parameters), whereIam)) {
                    EpayDobj ePayDobj = new EpayDobj();
                    ePayDobj.setPurCd(pur_cd);
                    ePayDobj.setPurCdDescr(rsFeeApplicable.getString("descr"));
                    if (pur_cd == TableConstants.TM_ROAD_TAX) {
                        tmgr = new TransactionManager("getFeeDetailsByActionTax-TaxExem");
                        String sqlTaxExemp = "Select tax_exemption from " + TableList.TM_CONFIGURATION + " where state_cd=? ";
                        ps = tmgr.prepareStatement(sqlTaxExemp);
                        ps.setString(1, ownerdobj.getState_cd());
                        RowSet rs = tmgr.fetchDetachedRowSet();
                        if (rs.next()) {
                            whereIam = "getFeeDetailsByActionTax{tm_cong-tax_exemption-" + ownerdobj.getState_cd() + "}";
                            if (isCondition(replaceTagValues(rs.getString("tax_exemption"), parameters), "getFeeDetailsByActionTax")) {
                                taxExempted = true;
                            }
                        }
                    }
                    ePayDobj.setTaxExempted(taxExempted);
                    list.add(ePayDobj);
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return list;
    }

    public static List<EpayDobj> getFeeDetailsRegisteredVehicle(String selectedStateCd, int selectedOffCd, Owner_dobj ownerdobj, String appl_no, int vehClass, String vehCatg, ConvDobj convdobj, boolean isRecheck, List<EpayDobj> listFeeTaxPaid, String reasonForFitness) throws VahanException {

        Set<EpayDobj> st = new LinkedHashSet<>();
        TransactionManager tmgr = null;
        java.util.Date dueDate;
        java.util.Date paymentDate = new java.util.Date();
        int allowedActionCd = TableConstants.TM_ACTION_REGISTERED_VEH_FEE;
        VahanTaxParameters vehicleTaxParameters = null;
        VehicleParameters vehicleParameters = null;
        PassengerPermitDetailDobj permitDobj = null;
        FitnessDobj fitnessDobjTemp = null;
        String sql = null;
        ToDobj toDobj = null;

        try {
            sql = "select pur_cd,action_cd from va_status where appl_no=?";
            tmgr = new TransactionManager("EpayImpl.getFeeDetailsRegisteredVehicle()");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rsFeeApplicable = tmgr.fetchDetachedRowSet_No_release();
            boolean allNotSameActionCode = false;
            boolean isNoc = false;
            boolean isFeeSkip = false;
            int pur_cd = 0;
            permitDobj = TaxServer_Impl.getPermitInfoForSavedTax(ownerdobj);
            TaxInstallmentConfigImpl instaximpl = new TaxInstallmentConfigImpl();
            PassengerPermitDetailDobj permitDob_info = instaximpl.getPermitBaseOnTaxPermitInfo(ownerdobj.getState_cd(), ownerdobj.getOff_cd(), ownerdobj.getRegn_no());
            if (permitDobj != null && permitDob_info != null && permitDobj.getOp_dt() != null && permitDob_info.getOp_dt() != null) {
                if (DateUtils.compareDates(permitDobj.getOp_dt(), permitDob_info.getOp_dt()) == 1) {
                    permitDobj = permitDob_info;
                }
            } else if (permitDobj == null && permitDob_info != null) {
                permitDobj = permitDob_info;
            }
            vehicleTaxParameters = fillTaxParametersFromDobj(ownerdobj, permitDobj);//calculation of vehicle Tax parameter

            //calculation of vehicle parameter
            vehicleParameters = FormulaUtils.fillVehicleParametersFromDobj(ownerdobj, permitDobj);
            if (ownerdobj.getRegn_dt() != null) {
                vehicleParameters.setVCH_AGE((int) Math.floor(DateUtils.getDate1MinusDate2_Months(ownerdobj.getRegn_dt(), new java.util.Date()) / 12.0));
            }
            while (rsFeeApplicable.next()) {
                if (rsFeeApplicable.getInt("action_cd") != allowedActionCd) {
                    allNotSameActionCode = true;
                }

                pur_cd = rsFeeApplicable.getInt("pur_cd");
                if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NOC) {
                    isNoc = true;
                }

                if (pur_cd == TableConstants.VM_TRANSACTION_MAST_TO) {
                    ToImpl toImpl = new ToImpl();
                    toDobj = toImpl.set_TO_appl_db_to_dobj(appl_no);
                    if (toDobj != null && toDobj.getReason() != null && toDobj.getReason().trim().length() > 0) {
                        vehicleParameters.setTO_REASON(toDobj.getReason().toUpperCase());
                    }
                }

                if (pur_cd == TableConstants.SWAPPING_REGN_PUR_CD && "RJ,UP".contains(selectedStateCd)) {
                    if (isSwpFeePaidForFirstVehicle(appl_no)) {
                        pur_cd = 17;
                    }
                }

                if (pur_cd == TableConstants.VM_TRANSACTION_MAST_REM_HYPO) {
                    FeeImpl feeImpl = new FeeImpl();
                    VehicleParameters vehParameters = fillVehicleParametersFromDobj(ownerdobj);
                    if (!feeImpl.getTmPurposeActionFlowCondtion(Util.getUserStateCode(), pur_cd, TableConstants.TM_ACTION_REGISTERED_VEH_FEE, vehParameters)) {
                        isFeeSkip = true;//for skip fee
                        continue;
                    }
                    if ("KA".contains(selectedStateCd)
                            && feeImpl.skipPurCodeFee(selectedStateCd, selectedOffCd, appl_no, pur_cd, "7,12")) {
                        continue;
                    }
                }

                if (pur_cd == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {//checking if fitness is failed

                    FitnessImpl fitImpl = new FitnessImpl();
                    boolean isFailed = fitImpl.isFailedFitness(ownerdobj.getRegn_no(), pur_cd, selectedOffCd, selectedStateCd); // fetching only this field for now
                    if (isFailed && !"JH".equals(selectedStateCd)) {
                        pur_cd = TableConstants.VM_VEHICLE_INSPECTION_FEE;
                    }

                    //inspection fee for other state vehicle
                    if (!ownerdobj.getState_cd().equalsIgnoreCase(selectedStateCd)) {
                        pur_cd = TableConstants.VM_VEHICLE_INSPECTION_FEE;
                    }

                    //for calculating panalty based on fitness inspection done in other state
                    fitnessDobjTemp = fitImpl.getVtFitnessTempDetails(ownerdobj.getRegn_no());
                    if (fitnessDobjTemp != null && fitnessDobjTemp.getFit_chk_dt() != null) {
                        paymentDate = fitnessDobjTemp.getFit_chk_dt();
                    }

                    int off_type_cd = ServerUtil.getOfficeCodeType(selectedOffCd, selectedStateCd);
                    vehicleParameters.setOFF_TYPE_CD(off_type_cd);

                    if (ownerdobj.getState_cd().equalsIgnoreCase("DL")) {

                        //start of...skipping parking fees when user already paid advanced fees where advance days for skipping parking fees is determind by particular state.
                        java.util.Date parkingDueDate = getFeeUpto(ownerdobj.getRegn_no(), ownerdobj.getState_cd(), ownerdobj.getOff_cd(), TableConstants.VM_PARKING_FEE_PUR_CD);
                        if (parkingDueDate != null && DateUtils.compareDates(parkingDueDate, new java.util.Date()) == 2) {
                            parkingDueDate = ServerUtil.dateRange(parkingDueDate, 0, 0, 1);
                            long advanceDays = DateUtils.getDate1MinusDate2_Days(DateUtils.parseDate(DateUtils.getDateInDDMMYYYY(new java.util.Date())), DateUtils.parseDate(DateUtils.getDateInDDMMYYYY(parkingDueDate)));
                            vehicleParameters.setPARKING_FEE_ADVANCE_DAYS((int) advanceDays);

                        }
                        if (isRecheck) {
                            if (listFeeTaxPaid != null && !listFeeTaxPaid.isEmpty()) {
                                for (int i = 0; i < listFeeTaxPaid.size(); i++) {
                                    if (listFeeTaxPaid.get(i).getPurCd() == TableConstants.VM_PARKING_FEE_PUR_CD) {
                                        vehicleParameters.setPARKING_FEE_ADVANCE_DAYS(0);
                                        break;
                                    }
                                }
                            }
                        }
                        //end of...skipping parking fees when user already paid advanced fees where advance days for skipping parking fees is determind by particular state.

                        //start of...calculation of expiry of vehicle age
                        ApplicationInwardImpl applicationInwardImpl = new ApplicationInwardImpl();
                        PermitDetailDobj permitDetailDobj = new PermitDetailDobj();
                        if (permitDobj != null) {
                            if (permitDobj.getPmt_type() != null && permitDobj.getPmt_type().trim().length() > 0) {
                                permitDetailDobj.setPmt_type(Integer.parseInt(permitDobj.getPmt_type()));
                            }
                            if (permitDobj.getPmtCatg() != null && permitDobj.getPmtCatg().trim().length() > 0) {
                                permitDetailDobj.setPmt_catg(Integer.parseInt(permitDobj.getPmtCatg()));
                            }
                            if (permitDobj.getValid_upto() != null) {
                                permitDetailDobj.setValid_upto(permitDobj.getValid_upto());
                            }
                            if ((permitDobj.getPmt_type() == null || (permitDobj.getPmt_type() != null && permitDobj.getPmt_type().trim().length() <= 0))
                                    && (permitDobj.getPmtCatg() == null || (permitDobj.getPmtCatg() != null && permitDobj.getPmtCatg().trim().length() <= 0))) {
                                permitDetailDobj = null;
                            }
                        } else {
                            permitDetailDobj = null;
                        }
                        boolean isVehAgeExpired = applicationInwardImpl.isVehAgeExpired(ownerdobj, permitDetailDobj);
                        if (isVehAgeExpired) {
                            vehicleParameters.setVEH_AGE_EXPIRED("true");
                        }
                        //end of...calculation of expiry of vehicle age
                    }
                }

                if (pur_cd == TableConstants.RETENTION_OF_REGISTRATION_NO_PUR_CD) {
                    RetentionImpl retentionImpl = new RetentionImpl();
                    if (retentionImpl.getVaSurrenderRetentionDetails(appl_no) != null) {
                        pur_cd = TableConstants.SWAPPING_REGN_PUR_CD;
                    }
                }

                long fineValue = 0;
                long feeValue = 0;

//                sql = "SELECT state_cd, action, map.pur_cd, condition_formula,map.action "
//                        + "  FROM dealer.vc_action_purpose_map  map,tm_purpose_mast mast where  state_cd=? "
//                        + "  and map.action in (mast.short_descr,'ALL') and mast.pur_cd=? ";
                sql = "SELECT distinct state_cd, action, map.pur_cd, condition_formula,map.action \n"
                        + "FROM dealer.vc_action_purpose_map  map,tm_purpose_mast mast where  state_cd=?\n"
                        + "and mast.pur_cd = map.pur_cd and (action = 'ALL' OR action in (Select short_descr from tm_purpose_mast where pur_cd = ?)) and fee_type not in ('TX')";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, selectedStateCd);
                ps.setInt(2, pur_cd);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                boolean fndPurCd = false;

                ownerdobj.setAppl_no(appl_no);

                while (rs.next()) {
                    if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehicleParameters), "getFeeDetailsRegisteredVehicle")) {

                        int purCdN = rs.getInt("pur_cd");

                        //for delhi case when fitness is expired then renewal of registration fee should come
                        //this will be handled by condtiono formula in future
                        if (ownerdobj.getState_cd().equalsIgnoreCase("DL")
                                && (pur_cd == TableConstants.VM_TRANSACTION_MAST_FIT_CERT)
                                && (purCdN == TableConstants.VM_TRANSACTION_MAST_REN_REG)) {
                            if (isRecheck) {
                                java.util.Date reCheckFeeDate = ServerUtil.getRegVehPaymentDateForRecheckFee(pur_cd, appl_no, ownerdobj);
                                if (reCheckFeeDate != null
                                        && DateUtils.compareDates(DateUtils.getDateInDDMMYYYY(reCheckFeeDate), DateUtils.getDateInDDMMYYYY(ownerdobj.getFit_upto())) <= 1) {
                                    continue;
                                }
                            } else {
                                java.util.Date fitnessExpireComparisonDate = new java.util.Date();
                                if (fitnessDobjTemp != null && fitnessDobjTemp.getFit_chk_dt() != null) {
                                    fitnessExpireComparisonDate = fitnessDobjTemp.getFit_chk_dt();
                                }
                                if (DateUtils.parseDate(DateUtils.parseDate(fitnessExpireComparisonDate)).compareTo(ownerdobj.getFit_upto()) < 1) {
                                    continue;
                                }
                            }
                        }
                        TmConfigurationDobj tmConfDobj = Util.getTmConfiguration();
                        if (purCdN == TableConstants.SWAPPING_REGN_PUR_CD && tmConfDobj != null && tmConfDobj.isTo_retention()) {
                            ToImpl toImpl = new ToImpl();
                            if (!toImpl.isSurrenderRetention(appl_no)) {
                                continue;
                            }
                        }
                        dueDate = ServerUtil.getRegVehDueDate(purCdN, appl_no, ownerdobj);
                        if (dueDate != null) {
                            vehicleTaxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(dueDate));
                        }
                        EpayDobj ePayDobj = new EpayDobj();
                        if (purCdN == TableConstants.SWAPPING_REGN_PUR_CD && "DL".equalsIgnoreCase(selectedStateCd)) {
                            feeValue = AdvanceRegnFeeImpl.getRetainAmount(ownerdobj.getRegn_no(), selectedStateCd, selectedOffCd, ownerdobj);
                        } else {
                            if ("DL".equalsIgnoreCase(selectedStateCd) && (purCdN == TableConstants.VM_TRANSACTION_MAST_FIT_CERT || purCdN == TableConstants.VM_VEHICLE_INSPECTION_FEE) && ownerdobj.getVh_class() == 57) {
                                feeValue = 0;
                            } else {
                                feeValue = getPurposeCodeFee(selectedStateCd, selectedOffCd, ownerdobj, vehClass, purCdN, vehCatg, ePayDobj, convdobj, vehicleTaxParameters, vehicleParameters, permitDobj, isRecheck, reasonForFitness);
                            }
                        }
                        // feeValue = getPurposeCodeFee(selectedStateCd, selectedOffCd, ownerdobj, vehClass, purCdN, vehCatg, ePayDobj, convdobj, vehicleTaxParameters, vehicleParameters, permitDobj, isRecheck, reasonForFitness);

                        if (isRecheck) {
                            paymentDate = ServerUtil.getRegVehPaymentDateForRecheckFee(purCdN, appl_no, ownerdobj);
                            //temporary solution for state JH
                            if ("JH".equalsIgnoreCase(selectedStateCd) && purCdN == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {
                                if (listFeeTaxPaid != null && !listFeeTaxPaid.isEmpty()) {
                                    for (int i = 0; i < listFeeTaxPaid.size(); i++) {
                                        if (listFeeTaxPaid.get(i).getPurCd() == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {
                                            paymentDate = listFeeTaxPaid.get(i).getRcptDt();
                                            break;
                                        }
                                    }
                                }
                            }//end of temporary solution for JH
                        }
                        if (selectedStateCd.equalsIgnoreCase("KA") && pur_cd == TableConstants.VM_TRANSACTION_MAST_TO && toDobj.getReason().equalsIgnoreCase("SUCCESSION")) {
                            feeValue = feeValue / 2;
                        }

                        fineValue = ServerUtil.getPurposeFineAmount(ownerdobj.getState_cd(), purCdN, feeValue, dueDate, paymentDate, vehicleParameters, false);
                        if (fineValue > 0) {
                            TmConfigurationReceipts configFeeFineZero = ServerUtil.getTmConfigurationReceipts(selectedStateCd);
                            if (configFeeFineZero != null) {
                                FeeFineExemptionDobj feeFineDobj = null;
                                vehicleParameters.setTRANSACTION_PUR_CD(purCdN);
                                if (isCondition(replaceTagValues(configFeeFineZero.getFeeAmtZero(), vehicleParameters), "getFeeDetailsRegisteredVehicle-1")) {
                                    feeFineDobj = new FeeFineExemptionDobj();
                                    feeFineDobj.setExemFeeAmount(feeValue);
                                    feeValue = 0;
                                }
                                if (isCondition(replaceTagValues(configFeeFineZero.getFineAmtZero(), vehicleParameters), "getFeeDetailsRegisteredVehicle-2")) {
                                    if (configFeeFineZero.getCovid19_from() != null && configFeeFineZero.getCovid19_upto() != null && dueDate != null && dueDate.after(configFeeFineZero.getCovid19_from()) && dueDate.before(configFeeFineZero.getCovid19_upto())) {
                                        dueDate = configFeeFineZero.getCovid19_upto();
                                        fineValue = ServerUtil.getPurposeFineAmount(ownerdobj.getState_cd(), purCdN, feeValue, dueDate, paymentDate, vehicleParameters, false);
                                    } else {
                                        if (feeFineDobj != null) {
                                            feeFineDobj.setExemFineAmount(fineValue);
                                        } else {
                                            feeFineDobj = new FeeFineExemptionDobj();
                                            feeFineDobj.setExemFineAmount(fineValue);
                                        }
                                        fineValue = 0;
                                    }
                                }
                                if (feeFineDobj != null) {
                                    ePayDobj.setFeeFineExemptionDobj(feeFineDobj);
                                }
                            }
                        }

                        ePayDobj.setPurCd(purCdN);
                        ePayDobj.setE_FinePenalty((int) fineValue);
                        ePayDobj.setE_TaxFee((int) feeValue);
                        ePayDobj.setE_total((int) (feeValue + ePayDobj.getE_FinePenalty()));
                        ePayDobj.setDueDate(dueDate);
                        if (ePayDobj.getDueDate() != null) {
                            ePayDobj.setDueDateString(ServerUtil.parseDateToString(ePayDobj.getDueDate()));
                        }
                        /*
                         * For UK 
                         */
                        if (purCdN == TableConstants.VM_TRANSACTION_MAST_ADDL_TO_VEHICLE && !"WB".equals(selectedStateCd)) {

                            sql = "Select reason from " + TableList.VA_TO + " where appl_no=? ";
                            if ("PB".equals(selectedStateCd)) {
                                sql = sql + " and (reason=? or reason=?)";
                            } else {
                                sql = sql + " and reason=? ";
                            }
                            ps = tmgr.prepareStatement(sql);
                            ps.setString(1, ownerdobj.getAppl_no());
                            ps.setString(2, TableConstants.TO_SUCCESSION);
                            if ("PB".equals(selectedStateCd)) {
                                ps.setString(3, TableConstants.TO_THEFT);
                            }
                            RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                            if (!rs1.next()) {
                                st.add(ePayDobj);
                            }
                        } else {
                            st.add(ePayDobj);
                        }
                        if (purCdN != TableConstants.VM_TRANSACTION_MAST_ALL) {
                            fndPurCd = true;
                        }
                        if (Util.getUserStateCode().equals("UK") && purCdN == TableConstants.VM_TRANSACTION_MAST_GREEN_TAX) {
                            java.util.Date feeUpToDate = getFeeUpto(ownerdobj.getRegn_no(), ownerdobj.getState_cd(), ownerdobj.getOff_cd(), purCdN);
                            boolean flag = true;
                            if (feeUpToDate != null) {
                                java.util.Date nidDate = ServerUtil.dateRange(feeUpToDate, 0, 0, -30);
                                java.util.Date currDate = new java.util.Date();
                                if (nidDate.after(currDate)) {
                                    st.remove(ePayDobj);
                                    flag = false;
                                }
                            }
                            if (flag) {
                                if (!ownerdobj.getRegn_no().equalsIgnoreCase("NEW")) {
                                    if (feeUpToDate != null) {
                                        ePayDobj.setFromDate(ServerUtil.dateRange(feeUpToDate, 0, 0, 1));
                                    } else {
                                        ePayDobj.setFromDate(new java.util.Date());
                                    }
                                }
                                int greenTaxValidyUpTo = FitnessImpl.getReNewFitnessUpto(ownerdobj, purCdN);
                                ePayDobj.setUptoDate(ServerUtil.dateRange(ePayDobj.getFromDate(), greenTaxValidyUpTo, 0, -1));
                            }
                        }

                    }
                }

                if (!fndPurCd) {
                    dueDate = ServerUtil.getRegVehDueDate(pur_cd, appl_no, ownerdobj);
                    if (dueDate != null) {
                        vehicleTaxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(dueDate));
                    }
                    EpayDobj ePayDobj = new EpayDobj();
                    feeValue = getPurposeCodeFee(selectedStateCd, selectedOffCd, ownerdobj, vehClass, pur_cd, vehCatg, ePayDobj, convdobj, vehicleTaxParameters, vehicleParameters, permitDobj, isRecheck, reasonForFitness);
                    //Addition of 2000 for odd even random number for HR                    
                    if (pur_cd == TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO && selectedStateCd.equalsIgnoreCase("HR")) {
                        if (!CommonUtils.isNullOrBlank(new Rereg_Impl().oddEvenOpted(ownerdobj.getAppl_no(), tmgr))) {
                            feeValue = getPurposeCodeFee(selectedStateCd, selectedOffCd, ownerdobj, vehClass, TableConstants.RANDOM_ODD_EVEN_FEE, vehCatg, ePayDobj, convdobj, vehicleTaxParameters, vehicleParameters, permitDobj, isRecheck, reasonForFitness);
                        } else if (!CommonUtils.isNullOrBlank(NewImpl.getAdvanceRegnNo(ownerdobj.getAppl_no()))) {
                            feeValue = 0l;
                        }
                    }
                    if (isRecheck) {
                        paymentDate = ServerUtil.getRegVehPaymentDateForRecheckFee(pur_cd, appl_no, ownerdobj);
                        //temporary solution for state JH
                        if ("JH".equalsIgnoreCase(selectedStateCd) && pur_cd == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {
                            if (listFeeTaxPaid != null && !listFeeTaxPaid.isEmpty()) {
                                for (int i = 0; i < listFeeTaxPaid.size(); i++) {
                                    if (listFeeTaxPaid.get(i).getPurCd() == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {
                                        paymentDate = listFeeTaxPaid.get(i).getRcptDt();
                                        break;
                                    }
                                }
                            }
                        }//end of temporary solution for JH
                    }
                    fineValue = ServerUtil.getPurposeFineAmount(ownerdobj.getState_cd(), pur_cd, feeValue, dueDate, paymentDate, vehicleParameters, false);

                    if (fineValue > 0) {
                        TmConfigurationReceipts configFeeFineZero = ServerUtil.getTmConfigurationReceipts(selectedStateCd);
                        if (configFeeFineZero != null) {
                            FeeFineExemptionDobj feeFineDobj = null;
                            vehicleParameters.setTRANSACTION_PUR_CD(pur_cd);
                            if (isCondition(replaceTagValues(configFeeFineZero.getFeeAmtZero(), vehicleParameters), "getFeeDetailsRegisteredVehicle-1")) {
                                feeFineDobj = new FeeFineExemptionDobj();
                                feeFineDobj.setExemFeeAmount(feeValue);
                                feeValue = 0;
                            }
                            if (isCondition(replaceTagValues(configFeeFineZero.getFineAmtZero(), vehicleParameters), "getFeeDetailsRegisteredVehicle-2")) {
                                if (configFeeFineZero.getCovid19_from() != null && configFeeFineZero.getCovid19_upto() != null && dueDate != null && dueDate.after(configFeeFineZero.getCovid19_from()) && dueDate.before(configFeeFineZero.getCovid19_upto())) {
                                    dueDate = configFeeFineZero.getCovid19_upto();
                                    fineValue = ServerUtil.getPurposeFineAmount(ownerdobj.getState_cd(), pur_cd, feeValue, dueDate, paymentDate, vehicleParameters, false);
                                } else {
                                    if (feeFineDobj != null) {
                                        feeFineDobj.setExemFineAmount(fineValue);
                                    } else {
                                        feeFineDobj = new FeeFineExemptionDobj();
                                        feeFineDobj.setExemFineAmount(fineValue);
                                    }
                                    fineValue = 0;
                                }
                            }
                            if (feeFineDobj != null) {
                                ePayDobj.setFeeFineExemptionDobj(feeFineDobj);
                            }
                        }
                    }

                    ePayDobj.setPurCd(pur_cd);
                    ePayDobj.setE_FinePenalty((int) fineValue);
                    ePayDobj.setE_TaxFee((int) feeValue);
                    ePayDobj.setE_total((int) (feeValue + ePayDobj.getE_FinePenalty()));
                    ePayDobj.setDueDate(dueDate);
                    if (ePayDobj.getDueDate() != null) {
                        ePayDobj.setDueDateString(ServerUtil.parseDateToString(ePayDobj.getDueDate()));
                    }
                    st.add(ePayDobj);
                }
            }
            List<ManualReceiptEntryDobj> manualRcptList = ManualReceiptEntryImpl.getManualReceiptEntryDetails(appl_no);
            for (ManualReceiptEntryDobj manualrcptdobj : manualRcptList) {
                EpayDobj ePayDobj = new EpayDobj();
                ePayDobj.setPurCd(manualrcptdobj.getPur_cd());
                ePayDobj.setE_TaxFee(-manualrcptdobj.getAmount());
                ePayDobj.setE_FinePenalty(0l);
                st.add(ePayDobj);
            }

            if (!isNoc && !isFeeSkip && allNotSameActionCode && !isRecheck) {
                throw new VahanException("Fee Can't be Submitted because one of the Transaction is Pending on Data Entry Level!!!");
            }

        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching Of Fee Details");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        List<EpayDobj> list = new ArrayList<>(st);
        return list;
    }

    /**
     *
     * @param vehClass Vehicle Class of the Vehicle
     * @param purCd Purpose Code
     * @param vehCatg Vehicle Category
     * @return Fee Value
     */
    public static Long getPurposeCodeFee(Owner_dobj ownerdobj, int vehClass, int purCd, String vehCatg) throws VahanException {
        Long fee = 0l;
        TransactionManager tmgr = null;
        String whereIam = "E_pay_Impl.getPurposeFee";
        PreparedStatement ps = null;
        RowSet rsFee = null;
        boolean fromWsdl = false;
        try {
            tmgr = new TransactionManager(whereIam);

            PassengerPermitDetailDobj permitDob = TaxServer_Impl.getPermitInfoForSavedTax(ownerdobj);
            VahanTaxParameters taxParameters = fillTaxParametersFromDobj(ownerdobj, permitDob);

            String sql = "Select * from " + TableList.TM_PURPOSE_MAST + " where pur_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, purCd);
            rsFee = tmgr.fetchDetachedRowSet_No_release();
            if (rsFee.next()) {
                fromWsdl = rsFee.getBoolean("amt_from_wsdl");
            }

            if (!fromWsdl) {

//                if ("GJ".contains(ownerdobj.getState_cd()) && "4,5".contains(String.valueOf(ownerdobj.getOwner_cd()))) {
//                    return 0L;
//                }
                String feeSQL = "select * from vm_feemast where state_cd = ? and pur_cd = ? and vh_class in (?, 0) order by vh_class desc";
                ps = tmgr.prepareStatement(feeSQL);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, purCd);
                ps.setInt(3, vehClass);
                rsFee = tmgr.fetchDetachedRowSet_No_release();
                if (rsFee.next()) {
                    if (rsFee.getLong("fees") != 0) {
                        fee = rsFee.getLong("fees");

                        if (ownerdobj != null && purCd == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO
                                || purCd == TableConstants.VM_TRANSACTION_MAST_REM_HYPO
                                || purCd == TableConstants.VM_TRANSACTION_MAST_HPC) {
                            HpaImpl impl = new HpaImpl();
                            int total = impl.maxHypothecationNo(ownerdobj.getAppl_no(), ownerdobj.getRegn_no(), purCd);
                            if (total >= 1) {
                                fee = fee * total;
                            }
                        }
                        return fee;
                    }
                }

                boolean isAutomaticFitness = false;
                if (purCd == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {
                    VmSmartCardHsrpDobj smartCardHsrpDobj = ServerUtil.getVmSmartCardHsrpParameters(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                    if (smartCardHsrpDobj != null) {
                        if (isCondition(replaceTagValues(smartCardHsrpDobj.getAutomaticFitness(), taxParameters), "getPurposeCodeFee")) {
                            isAutomaticFitness = true;
                        }

                    }
                }

                VehicleParameters vehParams = fillVehicleParametersFromDobj(ownerdobj);
                int off_type_cd = ServerUtil.getOfficeCodeType(Util.getSelectedSeat().getOff_cd(), Util.getUserStateCode());
                vehParams.setOFF_TYPE_CD(off_type_cd);
                feeSQL = "select * from vm_feemast_catg where state_cd=? and pur_cd=? and vch_catg=?";

                ps = tmgr.prepareStatement(feeSQL);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, purCd);
                ps.setString(3, vehCatg);
                rsFee = tmgr.fetchDetachedRowSet_No_release();
                while (rsFee.next()) {
                    if (isCondition(replaceTagValues(rsFee.getString("condition_formula"), vehParams), "getFeeDetailsNewRegisteredVehicleForDL")) {
                        if (ownerdobj != null && ownerdobj.getImported_vch().equals("Y")) {
                            fee = rsFee.getLong("imported_fees"); // fetching only this field for now
                            break;
                        } else if (isAutomaticFitness) {
                            fee = rsFee.getLong("automatic_fitness_fee"); // fetching only this field for now
                            break;
                        } else {
                            fee = rsFee.getLong("fees"); // fetching only this field for now
                            break;
                        }
                    }
                }
//                if (rsFee.next()) {
//                    if (ownerdobj != null && ownerdobj.getImported_vch().equals("Y")) {
//                        fee = rsFee.getLong("imported_fees"); // fetching only this field for now
//                    } else if (isAutomaticFitness) {
//                        fee = rsFee.getLong("automatic_fitness_fee"); // fetching only this field for now
//                    } else {
//                        fee = rsFee.getLong("fees"); // fetching only this field for now
//                    }
//                }
            } else {

                taxParameters.setTAX_MODE("L");
                taxParameters.setPUR_CD(purCd);
//                Green Tax Calculation With Renewal of Registration  (FOR State UP)
                if (purCd == TableConstants.VM_TRANSACTION_MAST_ADDL_TO_VEHICLE
                        || purCd == TableConstants.VM_TRANSACTION_MAST_GREEN_TAX) {
                    LifeTimeTaxUpdateDobj lifeTimeTaxUpdate = new LifeTimeTaxUpdateDobj();
                    lifeTimeTaxUpdate.setApplNo(ownerdobj.getAppl_no());
                    lifeTimeTaxUpdate.setRegnNo(ownerdobj.getRegn_no());
                    lifeTimeTaxUpdate.setOffcd(ownerdobj.getOff_cd());
                    lifeTimeTaxUpdate.setStateCd(ownerdobj.getState_cd());
                    LifeTimeTaxUpdateImpl lifeTimeTaxUpdateImpl = new LifeTimeTaxUpdateImpl();
                    long taxAmt = lifeTimeTaxUpdateImpl.getLifeTaxUpdateAmt(lifeTimeTaxUpdate);
                    if (taxAmt <= 0) {
                        taxAmt = getToTaxAmt(ownerdobj.getRegn_no());
                    }
                    taxParameters.setNET_TAX_AMT((double) taxAmt);
                }
                try {
                    List<DOTaxDetail> listTaxBreakUp = callTaxService(taxParameters);
                    for (DOTaxDetail doTax : listTaxBreakUp) {
                        fee = (long) (fee + doTax.getAMOUNT());
                    }
                } catch (Exception e) {
                    //To avoid throwing of Exception for cases with unavailable Tax Amount or Appropriate Tax Modes
                }

            }

            if (ownerdobj != null && purCd == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO
                    || purCd == TableConstants.VM_TRANSACTION_MAST_REM_HYPO
                    || purCd == TableConstants.VM_TRANSACTION_MAST_HPC) {
                HpaImpl impl = new HpaImpl();
                int total = impl.maxHypothecationNo(ownerdobj.getAppl_no(), ownerdobj.getRegn_no(), purCd);
                if (total >= 1) {
                    fee = fee * total;
                }
            }

        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in calculating the Fee/Tax, please try again or contact Administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return fee;

    }

    public static Long getPurposeCodeFee(String selectedStateCd, int selectedOffCd, Owner_dobj ownerdobj, int vehClass, int purCd, String vehCatg, EpayDobj ePayDobj, ConvDobj convDobj, VahanTaxParameters vehicleTaxParameters, VehicleParameters vehicleParameters, PassengerPermitDetailDobj permitDob, boolean isRecheck, String reasonForFitness) throws VahanException {
        Long fee = 0l;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rsFee = null;
        boolean fromWsdl = false;
        String sql = null;
        try {
            tmgr = new TransactionManager("EpayImpl.getPurposeCodeFee()");
            sql = "Select * from " + TableList.TM_PURPOSE_MAST + " where pur_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, purCd);
            rsFee = tmgr.fetchDetachedRowSet_No_release();
            if (rsFee.next()) {
                fromWsdl = rsFee.getBoolean("amt_from_wsdl");
            }

            if (!fromWsdl) {

                sql = "select * from vm_feemast where state_cd = ? and pur_cd = ? and vh_class in (?, 0) order by vh_class desc";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, selectedStateCd);
                ps.setInt(2, purCd);
                ps.setInt(3, vehClass);
                rsFee = tmgr.fetchDetachedRowSet_No_release();
                if (rsFee.next()) {
                    if (rsFee.getLong("fees") != 0) {
                        fee = rsFee.getLong("fees");

                        if (ownerdobj != null && purCd == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO
                                || purCd == TableConstants.VM_TRANSACTION_MAST_REM_HYPO
                                || purCd == TableConstants.VM_TRANSACTION_MAST_HPC) {
                            HpaImpl impl = new HpaImpl();
                            int total = impl.maxHypothecationNo(ownerdobj.getAppl_no(), ownerdobj.getRegn_no(), purCd);
                            if (total >= 1) {
                                fee = fee * total;
                            }
                        }
                        return fee;
                    }
                }

                boolean isAutomaticFitness = false;
                if (purCd == TableConstants.VM_TRANSACTION_MAST_FIT_CERT
                        || purCd == TableConstants.VM_VEHICLE_INSPECTION_FEE) {
                    VmSmartCardHsrpDobj smartCardHsrpDobj = ServerUtil.getVmSmartCardHsrpParameters(selectedStateCd, selectedOffCd);
                    if (smartCardHsrpDobj != null) {
                        if (isCondition(replaceTagValues(smartCardHsrpDobj.getAutomaticFitness(), vehicleParameters), "getPurposeCodeFee")) {
                            isAutomaticFitness = true;
                        }
                    }
                }

                sql = "select * from vm_feemast_catg where state_cd=? and pur_cd=? and vch_catg=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, selectedStateCd);
                ps.setInt(2, purCd);
                ps.setString(3, vehCatg);
                rsFee = tmgr.fetchDetachedRowSet_No_release();
                while (rsFee.next()) {
                    if (isCondition(replaceTagValues(rsFee.getString("condition_formula"), vehicleParameters), "getFeeDetailsRegisteredVehicleForDL")) {
                        if (ownerdobj != null && ownerdobj.getImported_vch().equals("Y")) {
                            fee = rsFee.getLong("imported_fees"); // fetching only this field for now
                            break;
                        } else if (isAutomaticFitness) {
                            fee = rsFee.getLong("automatic_fitness_fee"); // fetching only this field for now
                            break;
                        } else {
                            fee = rsFee.getLong("fees"); // fetching only this field for now
                            break;
                        }
                    }
                }
            } else {
                FitnessImpl fitnessImpl = new FitnessImpl();
                java.util.Date maxRegnUptoDate = fitnessImpl.getMaxRegnUptoDate(ownerdobj);
                String taxMods = "L";
                boolean blnShowTax = false;
                java.util.Date fromDt = null;
                java.util.Date uptoDt = null;
                vehicleTaxParameters.setPUR_CD(purCd);

                if (purCd == TableConstants.VM_PARKING_FEE_PUR_CD) {
                    TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
                    if (permitDob == null
                            && tmConfigurationDobj != null
                            && isCondition(replaceTagValues(tmConfigurationDobj.getPermit_exemption(), vehicleParameters), "getPurposeCodeFee_1")) {
                        throw new VahanException("Permit Details not found, Please Contact to System Administrator");
                    } else if (!ServerUtil.isTransport(ownerdobj.getVh_class(), ownerdobj) && convDobj != null) {
                        //due date is current date when whehcle is non-tranport and application is for conversion
                        //here tax mode is life time
                        vehicleTaxParameters.setTAX_DUE_FROM_DATE(ServerUtil.parseDateToStringDDMMYYYY(new java.util.Date()));
                    } else {
                        sql = "SELECT *  FROM vm_allowed_mods where state_cd=? and  pur_cd=? ";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, selectedStateCd);
                        ps.setInt(2, purCd);
                        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                        vehicleParameters.setNEW_VCH("N");
                        while (rs.next()) {
                            if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehicleParameters), "getPurposeCodeFee")) {
                                String[] taxModsArr = rs.getString("mods").split(",");
                                taxMods = taxModsArr[0];
                                blnShowTax = true;
                            }
                        }

                        ExemptionImpl exemptionImpl = new ExemptionImpl();//for parking fee exemption
                        ExemptionDobj exemptionDobj = exemptionImpl.getExemptionDetailsBasedOnPurpose(ownerdobj.getRegn_no(), TableConstants.VM_PARK_EXEMPTION_PUR_CD);
                        boolean isRecordExist = false;

                        sql = " SELECT a.state_cd, a.off_cd, a.regn_no,a.rcpt_no,b.fee_from,b.fee_upto FROM " + TableList.VT_FEE
                                + " a left join " + TableList.VT_FEE_BREAKUP
                                + " b on a.rcpt_no=b.rcpt_no and a.state_cd=b.state_cd and a.off_cd=b.off_cd and a.pur_cd=b.pur_cd "
                                + " WHERE a.regn_no=? and a.pur_cd=? and a.state_cd=? "
                                + " ORDER BY a.rcpt_dt desc limit 1";

                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, ownerdobj.getRegn_no());
                        ps.setInt(2, purCd);
                        ps.setString(3, selectedStateCd);
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        isRecordExist = rs.next();

                        if (isRecordExist || exemptionDobj != null) {
                            java.util.Date taxDueDate = null;
                            java.util.Date date = null;
                            java.util.Date parkingExemptionDate = null;

                            if (isRecordExist) {
                                date = rs.getDate("fee_upto");
                            }

                            if (exemptionDobj != null) {
                                parkingExemptionDate = exemptionDobj.getExem_to_date();
                            }

                            if (isRecheck && !(DateUtils.compareDates(maxRegnUptoDate, date) == 1)) {
                                date = rs.getDate("fee_from");
                            }

                            if (date != null && parkingExemptionDate != null && DateUtils.compareDates(date, parkingExemptionDate) <= 1) {
                                date = parkingExemptionDate;//max date if found in exemption table for parking
                            }

                            if (date == null) {
                                date = parkingExemptionDate;
                            }

                            if (date == null && convDobj != null) {
                                taxDueDate = new java.util.Date();
                            }

                            if (date == null) {
                                throw new VahanException("Previous Parking Fee Upto Date is not found, Please Contact to System Administrator");
                            }

                            if (taxDueDate == null) {
                                taxDueDate = ServerUtil.dateRange(date, 0, 0, 1);
                            }

                            if ("DL".equalsIgnoreCase(Util.getUserStateCode()) && isRecheck) {
                                taxDueDate = date;
                            }
                            vehicleTaxParameters.setTAX_DUE_FROM_DATE(ServerUtil.parseDateToStringDDMMYYYY(taxDueDate));
                            vehicleTaxParameters.setNEW_VCH("N");
                        } else {
                            throw new VahanException("Previous Parking Fee Details not found, Please Contact to System Administrator");
                        }
                    }
                }

                //Green Tax Calculation With Renewal of Registration  (FOR State UP)
                if (purCd == TableConstants.VM_TRANSACTION_MAST_ADDL_TO_VEHICLE
                        || purCd == TableConstants.VM_TRANSACTION_MAST_GREEN_TAX) {
                    LifeTimeTaxUpdateDobj lifeTimeTaxUpdate = new LifeTimeTaxUpdateDobj();
                    lifeTimeTaxUpdate.setApplNo(ownerdobj.getAppl_no());
                    lifeTimeTaxUpdate.setRegnNo(ownerdobj.getRegn_no());
                    lifeTimeTaxUpdate.setOffcd(ownerdobj.getOff_cd());
                    lifeTimeTaxUpdate.setStateCd(ownerdobj.getState_cd());
                    LifeTimeTaxUpdateImpl lifeTimeTaxUpdateImpl = new LifeTimeTaxUpdateImpl();
                    long taxAmt = lifeTimeTaxUpdateImpl.getLifeTaxUpdateAmt(lifeTimeTaxUpdate);
                    if (taxAmt <= 0) {
                        taxAmt = getToTaxAmt(ownerdobj.getRegn_no());
                        if (taxAmt <= 0) {
                            taxAmt = 0;
                        }
                    }
                    vehicleTaxParameters.setNET_TAX_AMT((double) taxAmt);

                    //For Gujrat vehicle is transfered as individual from firm (or was purchased as Firm)
                    if ("GJ".contains(ownerdobj.getState_cd())) {
                        ToImpl toImpl = new ToImpl();
                        ToDobj toDobj = toImpl.set_TO_appl_db_to_dobj(ownerdobj.getAppl_no());
                        int vhto_owner_cd = new ToImpl().getOwner_Cd(ownerdobj.getRegn_no(), ownerdobj.getState_cd());
                        if (toDobj != null) {
                            if (ownerdobj.getOwner_cd() == TableConstants.OWNER_CODE_FIRM
                                    || vhto_owner_cd == TableConstants.OWNER_CODE_FIRM) {
                                if (vhto_owner_cd > 0) {
                                    vehicleTaxParameters.setOWNER_CD(vhto_owner_cd);
                                } else {
                                    vehicleTaxParameters.setOWNER_CD(ownerdobj.getOwner_cd());
                                }
                            }
                        }
                    }
                }
                vehicleTaxParameters.setTAX_MODE(taxMods);

                if (purCd == TableConstants.VM_PARKING_FEE_PUR_CD
                        && "DL".equalsIgnoreCase(Util.getUserStateCode())
                        && (CommonUtils.isNullOrBlank(reasonForFitness) || !TableConstants.FITNESS_RENEWAL_FOR_NOC_OR_CONVERSION.contains(reasonForFitness))
                        && Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {
//                    int months_from_due_date = 0;
                    int months_from_current_date = 0;
                    // FitnessImpl fitnessImpl = new FitnessImpl();
                    java.util.Date maxUptoDate = fitnessImpl.getMaxRegnUptoDate(ownerdobj);
                    if (maxUptoDate == null) {
                        int regValidityYear = ApplicationInwardImpl.getRegValidityYear(ownerdobj);
                        java.util.Date regnDate = ownerdobj.getRegn_dt();
                        if (regValidityYear == 0) {
                            regnDate = ownerdobj.getRegn_upto();
                        }
                        maxUptoDate = ServerUtil.dateRange(regnDate, regValidityYear, 0, (regValidityYear > 0 ? -1 : 0));

                    }
                    months_from_current_date = DateUtils.getDate1MinusDate2_Months(new java.util.Date(), maxUptoDate);
                    if (months_from_current_date <= 12 && months_from_current_date > 0) {
                        vehicleTaxParameters.setTAX_CEASE_DATE(DateUtils.parseDate(maxUptoDate));
                    } else if (months_from_current_date < 24 && months_from_current_date > 12 && taxMods.equalsIgnoreCase("Y2")) {
                        vehicleTaxParameters.setTAX_CEASE_DATE(DateUtils.parseDate(maxUptoDate));
                    } else if (months_from_current_date < 0) {
                        vehicleTaxParameters.setTAX_CEASE_DATE(DateUtils.parseDate(ServerUtil.dateRange(new java.util.Date(), 0, 1, -1)));
                    }

//                    months_from_current_date = DateUtils.getDate1MinusDate2_Months(new java.util.Date(), maxUptoDate);
//                    if (months_from_current_date < 12 && months_from_current_date > 0) {
//                        months_from_due_date = DateUtils.getDate1MinusDate2_Months((JSFUtils.getStringToDate(vehicleTaxParameters.getTAXDUEFROMDATE())), maxUptoDate);
//                        if (months_from_due_date > 0) {
//                            vehicleTaxParameters.setTAXMODE("M");
//                            vehicleTaxParameters.setTAXMODENOADV(months_from_due_date+1);
//                        }
//
//                    }
                } else if (TableConstants.FITNESS_RENEWAL_FOR_NOC_OR_CONVERSION.contains(reasonForFitness)) {
//                    vehicleTaxParameters.setTAXMODE("M");
//                    vehicleTaxParameters.setTAXMODENOADV(2);
                    vehicleTaxParameters.setTAX_CEASE_DATE(DateUtils.parseDate(ServerUtil.dateRange(new java.util.Date(), 0, 1, -1)));
                }
                List<DOTaxDetail> listTaxBreakUp = null;
                try {
                    listTaxBreakUp = callTaxService(vehicleTaxParameters);
                } catch (VahanException e) {
                    //To avoid throwing of Exception for cases with unavailable Tax Amount or Appropriate Tax Modes
                }

                if (listTaxBreakUp != null) {
                    listTaxBreakUp = TaxUtils.sortTaxDetails(listTaxBreakUp);
                    for (DOTaxDetail doTax : listTaxBreakUp) {
                        fee = (long) (fee + doTax.getAMOUNT());
                    }
                    if (blnShowTax) {
                        fromDt = DateUtils.parseDate(listTaxBreakUp.get(0).getTAX_FROM());
                        uptoDt = DateUtils.parseDate(listTaxBreakUp.get(listTaxBreakUp.size() - 1).getTAX_UPTO());
                    }
                }
                ePayDobj.setPurCd(purCd);
                ePayDobj.setFromDate(fromDt);
                ePayDobj.setUptoDate(uptoDt);
            }

            if (ownerdobj != null && purCd == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO
                    || purCd == TableConstants.VM_TRANSACTION_MAST_REM_HYPO
                    || purCd == TableConstants.VM_TRANSACTION_MAST_HPC) {
                HpaImpl impl = new HpaImpl();
                int total = impl.maxHypothecationNo(ownerdobj.getAppl_no(), ownerdobj.getRegn_no(), purCd);
                if (total >= 1) {
                    fee = fee * total;
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return fee;
    }

    public static Long getPmtPurposeCodeFee(Owner_dobj ownerdobj, int transPurCd, PassengerPermitDetailDobj permitDob, String feeHead) throws VahanException {
        Long fee = 0l;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rsFee = null;
        String sql = null;
        PermitShowFeeDetailImpl pmtShowimpl = new PermitShowFeeDetailImpl();
        int pDay = 0, pMonth = 0, pCMonth = 0, pYear = 0, perRegion = 0, perRoute = 0, exem_amount = 0, fine_to_be_taken = 0;
        try {
            tmgr = new TransactionManager("getPmtPurposeCodeFee");
            sql = "SELECT * FROM " + TableList.VM_PERMIT_FEE_SLAB + " where state_cd = ? and pur_cd = ? and trans_pur_cd = ?";
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, TableConstants.VM_PMT_FRESH_PUR_CD);
            ps.setInt(i++, transPurCd);
            rsFee = tmgr.fetchDetachedRowSet_No_release();
            while (rsFee.next()) {
                pYear = 5;
                VehicleParameters parameters = FormulaUtils.fillPermitParametersFromDobj(ownerdobj, permitDob, pDay, pMonth, pCMonth, pYear, exem_amount, fine_to_be_taken, 0, 0);
                if (isCondition(FormulaUtils.replaceTagPermitValues(rsFee.getString("condition_formula"), parameters), "getPmtFeeDetailsFromSlab")) {
                    if (feeHead.equalsIgnoreCase("Fee")) {
                        fee = Long.parseLong(pmtShowimpl.getPmtFeeValueFormSlab(FormulaUtils.replaceTagPermitValues(rsFee.getString("fee_rate_formula"), parameters), tmgr).trim());
                        break;
                    } else if (feeHead.equalsIgnoreCase("Fine")) {
                        fee = Long.parseLong(pmtShowimpl.getPmtFeeValueFormSlab(FormulaUtils.replaceTagPermitValues(rsFee.getString("fine_rate_formula"), parameters), tmgr).trim());
                        break;
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return fee;
    }

    /*
     * Get User Charges
     * 
     */
    public static Long getUserChargesFee(Owner_dobj ownerdobj, List<Integer> purCdList, VehicleParameters param) {
        Long fee = 0l;
        TransactionManager tmgr = null;
        String whereIam = "E_pay_Impl.getUserChargesFee";
        PreparedStatement ps = null;
        RowSet rs = null;
        String sql = null;
        VehicleParameters parameters = null;
        if (param == null) {
            parameters = FormulaUtils.fillVehicleParametersFromDobj(ownerdobj);
        } else {
            parameters = param;
        }

        try {
            tmgr = new TransactionManager(whereIam);
            String serviceType = getServiceChargeType();
            if (serviceType == null) {
                return 0L;
            }

            if ("TN".contains(Util.getUserStateCode())) {
                serviceType = "TN";
            }

            if ("HP".contains(Util.getUserStateCode()) && Util.getSelectedSeat() != null) {
                parameters.setTRANSACTION_PUR_CD(Util.getSelectedSeat().getPur_cd());
            }

            // For Service Charge Per receipt calculate Amount for pur_cd 0
            if (serviceType.equalsIgnoreCase(TableConstants.SERVICE_CHARGE_PER_RCPT)) {
                if (purCdList != null && !purCdList.isEmpty()) {
                    parameters.setPUR_CD(purCdList.get(0));
                }
                sql = "Select amount,condition_formula from " + TableList.VM_FEEMAST_SERVICE
                        + " where state_cd=? and pur_cd=0";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    if (isCondition(replaceTagValues(rs.getString("condition_formula"), parameters), "getUserChargesFee-1")) {
                        fee = rs.getLong("amount");
                        if (Util.getUserStateCode().equalsIgnoreCase("HP") && (purCdList != null) && (!purCdList.isEmpty())
                                && (purCdList.contains(TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) || purCdList.contains(TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE))
                                && purCdList.contains(TableConstants.VM_PMT_FRESH_PUR_CD)) {
                            fee *= 2;
                        }
                        return fee;
                    }
                }
            }

//             Service charge per transactions
//              For Service Charge Per Transaction calculate Amount for pur_cd if
//              pur_cd does not have entry in table calculate fee for pur_cd 0
            if (serviceType.equalsIgnoreCase(TableConstants.SERVICE_CHARGE_PER_TRANS)) {
                if (purCdList != null) {
                    for (int purCd : purCdList) {
                        sql = "Select amount,condition_formula from " + TableList.VM_FEEMAST_SERVICE
                                + " where state_cd=? and pur_cd=?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, Util.getUserStateCode());
                        ps.setInt(2, purCd);
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        boolean isPurCdFound = false;
                        while (rs.next()) {
                            if (isCondition(replaceTagValues(rs.getString("condition_formula"), parameters), "getUserChargesFee-2")) {
                                fee = fee + rs.getLong("amount");
                                isPurCdFound = true;
                            }
                        }

                        if (!isPurCdFound) {
                            sql = "Select amount,condition_formula from " + TableList.VM_FEEMAST_SERVICE
                                    + " where state_cd=? and pur_cd=0";
                            ps = tmgr.prepareStatement(sql);
                            ps.setString(1, Util.getUserStateCode());
                            rs = tmgr.fetchDetachedRowSet_No_release();
                            if (rs.next()) {
                                if (isCondition(replaceTagValues(rs.getString("condition_formula"), parameters), "getUserChargesFee-2")) {
                                    fee = fee + rs.getLong("amount");
                                }
                            }
                        }
                    }
                }
            }

            //--TN
            if (serviceType.contains("TN")) {
                fee = 0l;
                if (purCdList != null) {
                    for (int purCd : purCdList) {
                        sql = "Select amount,condition_formula from " + TableList.VM_FEEMAST_SERVICE
                                + " where state_cd=? and pur_cd=?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, Util.getUserStateCode());
                        ps.setInt(2, purCd);
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        boolean isPurCdFound = false;
                        while (rs.next()) {
                            if (isCondition(replaceTagValues(rs.getString("condition_formula"), parameters), "getUserChargesFee-2")) {
                                if (rs.getLong("amount") > fee) {
                                    fee = rs.getLong("amount");
                                }

                                isPurCdFound = true;
                            }
                        }

                        if (!isPurCdFound) {
                            sql = "Select amount,condition_formula from " + TableList.VM_FEEMAST_SERVICE
                                    + " where state_cd=? and pur_cd=0";
                            ps = tmgr.prepareStatement(sql);
                            ps.setString(1, Util.getUserStateCode());
                            rs = tmgr.fetchDetachedRowSet_No_release();
                            if (rs.next()) {
                                if (isCondition(replaceTagValues(rs.getString("condition_formula"), parameters), "getUserChargesFee-2")) {
                                    if (rs.getLong("amount") > fee) {
                                        fee = rs.getLong("amount");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return fee;
    }

    /**
     * null for No service charge is available SERVICE_CHARGE_PER_RCPT
     * SERVICE_CHARGE_PER_TRANS
     *
     * @return Service
     */
    public static String getServiceChargeType() {
        String serviceChargeType = null;
        try {
            TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
            if (tmConfigurationDobj != null) {
                TmConfigurationFitnessDobj fitnessConfigDobj = new FitnessImpl().getFitnessConfiguration(Util.getUserStateCode());
                if (fitnessConfigDobj != null && fitnessConfigDobj.isSkip_user_chg_fitness_centre() && (TableConstants.USER_CATG_FITNESS_CENTER_ADMIN.equals(Util.getUserCategory()) || TableConstants.USER_CATG_FITNESS_CENTER_USER.equals(Util.getUserCategory())) || (Util.getSelectedSeat() != null && Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE && tmConfigurationDobj.getTmConfigDealerDobj() != null && !tmConfigurationDobj.getTmConfigDealerDobj().isExmptServiceChargeInTRC())) {
                    return null;
                }

                if (tmConfigurationDobj.isService_charges_per_rcpt()) {
                    serviceChargeType = TableConstants.SERVICE_CHARGE_PER_RCPT;
                } else if (tmConfigurationDobj.isService_charges_per_trans()) {
                    serviceChargeType = TableConstants.SERVICE_CHARGE_PER_TRANS;
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return serviceChargeType;
    }

    public static int getToTaxAmt(String regn_no) {
        int taxAmt = 0;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getToTaxAmt");
            String sql = "select tax_amt from " + TableList.VT_TAX + " where regn_no=? and "
                    + " state_cd=? and pur_cd=? and tax_mode in ('L', 'O') order by rcpt_dt desc ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, TableConstants.TM_ROAD_TAX);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                taxAmt = rs.getInt("tax_amt");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        return taxAmt;

    }

//    public static List<DOTaxDetail> callTaxService(VahanTaxParameters taxParameters) throws VahanException {
//        List<DOTaxDetail> tempTaxList = null;
//
//        VahanTaxClient taxClient = null;
//        try {
//            taxClient = new VahanTaxClient();
//            String taxServiceResponse = taxClient.getTaxDetails(taxParameters);
//            tempTaxList = taxClient.parseTaxResponse(taxServiceResponse, taxParameters.getPURCD(), taxParameters.getTAXMODE());
//            if (tempTaxList != null) {
//                tempTaxList = TaxUtils.sortTaxDetails(tempTaxList);
//                DOTaxDetail doTax = tempTaxList.get(0);
//                if (doTax.getAMOUNT() == null || doTax.getAMOUNT().equals(0)) {
//                    throw new VahanMessageException("No Valid Tax Slabs Found for ");
//                }
//            }
//
//
//        } catch (javax.xml.ws.WebServiceException e) {
//            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//        }
//
//
//        return tempTaxList;
//    }
    public static ArrayList getPurposeCodeList(String stateCd, String appl_no, String whereClause, boolean convFlag) {
        ArrayList purposeCodeList = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String Query = null;
        try {
            purposeCodeList = new ArrayList();
            purposeCodeList.clear();
            tmgr = new TransactionManager("get Purpose code list as per application");
            if (whereClause != null) {
                if (convFlag) {
                    Query = "select distinct pur_cd, descr from (select pur_cd, descr from TM_PURPOSE_MAST "
                            + "  where inward_appl = 'N' and fee_type IN ('RF','FE') " + whereClause
                            + "union all\n"
                            + "select a.pur_cd, a.descr from " + TableList.TM_PURPOSE_MAST + " a, " + TableList.VA_DETAILS + " b where a.pur_cd IN (SELECT map.pur_cd\n"
                            + "FROM dealer.vc_action_purpose_map  map," + TableList.TM_PURPOSE_MAST + " mast where mast.short_descr=map.action and mast.pur_cd=b.pur_cd) and b.appl_no=? and a.fee_type IN ('RF','FE')) a order by 2";
                    ps = tmgr.prepareStatement(Query);
                    ps.setString(1, appl_no);
                } else {

                    Query = "  select distinct pur_cd, descr FROM " + TableList.TM_PURPOSE_MAST
                            + "      WHERE  fee_type IN ('RF','FE') " + whereClause
                            + "       and (inward_appl = 'N' or "
                            + "       pur_cd in (select pur_cd from " + TableList.VA_DETAILS + " where appl_no=?) or "
                            + "       pur_cd in (select pur_cd FROM " + TableList.VC_ACTION_PURPOSE_MAP
                            + "       WHERE state_cd=? and action in ( SELECT distinct b.short_descr FROM " + TableList.VC_ACTION_PURPOSE_MAP
                            + "       a," + TableList.TM_PURPOSE_MAST + " b," + TableList.VA_DETAILS
                            + "       c WHERE a.pur_cd = b.pur_cd and a.pur_cd = c.pur_cd and"
                            + "       b.pur_cd = c.pur_cd and a.state_cd=? and c.appl_no=?)))"
                            + "       order by 2";
                    ps = tmgr.prepareStatement(Query);
                    ps.setString(1, appl_no);
                    ps.setString(2, stateCd);
                    ps.setString(3, stateCd);
                    ps.setString(4, appl_no);

                }
            } else {
                Query = "select pur_cd, descr from " + TableList.TM_PURPOSE_MAST + " where inward_appl = 'N' and fee_type IN ('RF','FE') "
                        + " union all"
                        + " select b.pur_cd, a.descr from " + TableList.TM_PURPOSE_MAST + " a, " + TableList.VA_DETAILS + " b where a.pur_cd = b.pur_cd and b.appl_no=?"
                        + " order by 2, 1";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, appl_no);
            }
            rs = tmgr.fetchDetachedRowSet();
            if (rs != null) {
                while (rs.next()) {
                    purposeCodeList.add(new SelectItem(rs.getString("pur_cd"), rs.getString("descr")));
                }
            } else {
                return purposeCodeList;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return purposeCodeList;
    }

    public static List<FeeDobj> getAllForStateFeeDetails(String stateCd) {
        List<FeeDobj> listFeeDobj = new ArrayList<>();
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getAllFeeDetails");
            String sql = "Select distinct mp.*,fee.fees,mast.descr from dealer.vc_action_purpose_map mp,vm_feemast fee,tm_purpose_mast mast "
                    + " where action='ALL' and mast.pur_cd=mp.pur_cd and  mp.state_cd=fee.state_cd and mp.pur_cd=fee.pur_cd and  mp.state_cd=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                FeeDobj feeDobj = new FeeDobj();
                feeDobj.setPurCd(rs.getInt("pur_cd"));
                feeDobj.setFeeAmount(rs.getLong("fees"));
                feeDobj.setFeeHeadDescr(rs.getString("descr"));
                feeDobj.setDisableDropDown(true);
                listFeeDobj.add(feeDobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return listFeeDobj;
    }

    public static java.util.Date getFeeUpto(String regnNo, String stateCd, int offCd, int purCd) throws VahanException {
        java.util.Date feeUpto = null;
        TransactionManager tmgr = null;
        String sql = null;
        boolean isRecordExist = false;
        try {
            ExemptionImpl exemptionImpl = new ExemptionImpl();//for parking fee exemption
            ExemptionDobj exemptionDobj = exemptionImpl.getExemptionDetailsBasedOnPurpose(regnNo, TableConstants.VM_PARK_EXEMPTION_PUR_CD);

            tmgr = new TransactionManager("getFeeUpto");
            sql = " SELECT a.state_cd, a.off_cd, a.regn_no,a.rcpt_no,b.fee_from,b.fee_upto FROM " + TableList.VT_FEE
                    + " a left join " + TableList.VT_FEE_BREAKUP
                    + " b on a.rcpt_no=b.rcpt_no and a.state_cd=b.state_cd and a.off_cd=b.off_cd and a.pur_cd=b.pur_cd "
                    + " WHERE a.regn_no=? and a.pur_cd=? and a.state_cd=?"
                    + " ORDER BY a.rcpt_dt desc limit 1";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setInt(2, purCd);
            ps.setString(3, stateCd);

            RowSet rs = tmgr.fetchDetachedRowSet();
            isRecordExist = rs.next();

            if (isRecordExist || exemptionDobj != null) {
                java.util.Date parkingExemptionDate = null;
                if (isRecordExist) {
                    feeUpto = rs.getDate("fee_upto");
                }

                if (exemptionDobj != null) {
                    parkingExemptionDate = exemptionDobj.getExem_to_date();
                }

                if (feeUpto != null && parkingExemptionDate != null && DateUtils.compareDates(feeUpto, parkingExemptionDate) <= 1) {
                    feeUpto = parkingExemptionDate;//max date if found in exemption table for parking
                }

                if (feeUpto == null) {
                    feeUpto = parkingExemptionDate;
                }
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            feeUpto = null;
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem in Getting Fee Upto Date, Please Contact to System Administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return feeUpto;
    }

    public static boolean isSwpFeePaidForFirstVehicle(String appl_no) {
        boolean feePaid = false;
        String link_appl_no = null;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManagerReadOnly("inside isSwpFeePaidForFirstVehicle");
            link_appl_no = ServerUtil.getLinkApplNo(appl_no);
            if (link_appl_no != null) {
                String sql = "select rcpt_no from " + TableList.VP_APPL_RCPT_MAPPING + " where appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, link_appl_no);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    feePaid = true;
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        }
        return feePaid;
    }

    public static List getAllTaxListForTaxDiff(Owner_dobj owner_dobj, VehicleParameters parameters) {
        List diff_pur_cd_list = new ArrayList<>();
        SelectItem item = new SelectItem("-1", "Select Tax Type");
        diff_pur_cd_list.add(item);
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = "";
        try {
            if (parameters != null && parameters.getREGN_DATE() != null) {
                parameters.setREGN_DATE(JSFUtils.getDateInDD_MMM_YYYY(parameters.getREGN_DATE()));
            }

            tmgr = new TransactionManager("getAllowedPurCodeDescr");
            sql = " SELECT distinct a.pur_cd,a.descr FROM " + TableList.TM_PURPOSE_MAST + " a," + TableList.VM_ALLOWED_MODS + " b "
                    + " WHERE  a.pur_cd = b.pur_cd and b.state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, owner_dobj.getState_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next())//found
            {
                item = new SelectItem(rs.getString("pur_cd"), rs.getString("descr"));
                diff_pur_cd_list.add(item);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        }
        return diff_pur_cd_list;
    }

    public static boolean getTempFeeOfficeAllotment(int assignedOffCd, int selectedOffcd, String stateCd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = "";
        boolean isValid = false;
        try {
            tmgr = new TransactionManagerReadOnly("getTempFeeOfficeAllotment");
            sql = "select * from " + TableList.VM_TEMP_FEE_OFFICE_ALLOTMENT + " where assigned_off_cd = ? and selected_off_cd = ? and state_cd= ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, assignedOffCd);
            ps.setInt(2, selectedOffcd);
            ps.setString(3, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next())//found
            {
                isValid = true;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting data of temp fee allotment.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        }
        return isValid;
    }

    public static String getValidForTempFeeDetails(String stateCd, int loginOffCd, String applNo) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = "";
        String isValidTempFeeAtRTO = null;
        RowSet rs = null;
        try {
            tmgr = new TransactionManagerReadOnly("getValidForTempFeeForDealerApplAtRTO");
            int i = 1;
            sql = "select case when ( ? = p.assigned_office) then 'TRUE' else p.assigned_office end as isvalidfortempfee from va_status s "
                    + " inner join tm_user_permissions p on s.state_cd = p.state_cd and s.emp_cd = p.user_cd and all_office_auth= true "
                    + " where pur_cd = " + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE + " and s.state_cd = ? and appl_no = ? and action_cd = " + TableConstants.TM_ROLE_NEW_REGISTRATION_FEE + " ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(i++, String.valueOf(loginOffCd));
            ps.setString(i++, stateCd);
            ps.setString(i++, applNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next())//found
            {
                isValidTempFeeAtRTO = rs.getString("isvalidfortempfee");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting data of temp fee in new registration application details.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        }
        return isValidTempFeeAtRTO;
    }

    public static void checkTempFeeInNewRegn(TmConfigurationDobj config, int purCd, int selectedSeatOffCd, String stateCd, Fee_Pay_Dobj feePayDobj, String empCode, VehicleParameters vehParameters) throws VahanException {
        int purCode = 0;
        if (config != null && config.getTmConfigDealerDobj() != null && config.isTempFeeInNewRegis() && purCd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
            Integer assignedOffCd = null;
            List offCdList = ServerUtil.getOffCode(empCode);
            if (!offCdList.isEmpty()) {
                assignedOffCd = Integer.valueOf(offCdList.get(0).toString());
            } else {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
            for (FeeDobj feePurDobj : feePayDobj.getCollectedFeeList()) {
                if (feePurDobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
                    purCode = feePurDobj.getPurCd();
                    break;
                }
            }
            String conditionFormula = ServerUtil.getConditionFormulaByAction(stateCd, "NEW", TableConstants.VM_TRANSACTION_MAST_TEMP_REG);
            if (purCode == 0 && !getTempFeeOfficeAllotment(assignedOffCd, selectedSeatOffCd, stateCd) && isCondition(replaceTagValues(conditionFormula, vehParameters), "addToCart-2") && ((config.getTmConfigDealerDobj().isTempRegnFeeWithNewForSameOffices()) || (!config.getTmConfigDealerDobj().isTempRegnFeeWithNewForSameOffices() && selectedSeatOffCd != assignedOffCd))) {
                throw new VahanException("Temporary fee is missing.Selected Office '" + ServerUtil.getOfficeName(selectedSeatOffCd, stateCd) + "' and Logged in Office '" + ServerUtil.getOfficeName(assignedOffCd, stateCd) + "'. Please go to HOME page and try again.");
            }
        }
    }
}
