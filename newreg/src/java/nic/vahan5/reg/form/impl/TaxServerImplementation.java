/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.form.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.java.util.CommonUtils;
import nic.java.util.DateUtils;
import nic.java.util.DateUtilsException;
import nic.rto.vahan.common.VahanException;
import static nic.vahan.CommonUtils.FormulaUtils.fillVehicleParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.getSqlFormulaValue;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.TaxUtils;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.common.tax.VahanTaxClient;
import nic.vahan.common.tax.VahanTaxRestClient;
import nic.vahan5.common.tax.VahanTaxClientRest;
import nic.vahan.db.DocumentType;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.bean.TaxFormPanelBean;
import nic.vahan.form.dobj.DOTaxDetail;
import nic.vahan.form.dobj.EpayDobj;
import nic.vahan.form.dobj.FeeDobj;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.Fee_Pay_Dobj;
import nic.vahan.form.dobj.NocDobj;
import nic.vahan.form.dobj.NonUseDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.TaxFieldDobj;
import nic.vahan.form.dobj.Tax_Pay_Dobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.tax.TaxDobj;
import nic.vahan.form.impl.FeeDraftimpl;
import nic.vahan.form.impl.FeeImpl;
import nic.vahan.form.impl.NocImpl;
import nic.vahan.form.impl.NonUseImpl;
import nic.vahan.form.impl.Receipt_Master_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.server.ServerUtil;
import nic.vahan5.reg.commonutils.FormulaUtilities;
import nic.vahan5.reg.form.impl.permit.PassengerPermitDetailImplementation;
import nic.vahan5.reg.rest.model.SessionVariablesModel;
import nic.vahan5.reg.server.ServerUtility;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import tax.web.service.VahanTaxParameters;
import nic.vahan.form.dobj.tax.VahanTaxParameters;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Kartikey Singh
 */
public class TaxServerImplementation {

    private static final Logger LOGGER = Logger.getLogger(TaxServerImplementation.class);

//   public Long getBalanceTaxAmountAgainstRegnNo(String regnNo) {
//        String taxBalanceSQL = "Select pur_cd, tax_from,tax_upto, coalesce(bal_tax_amt,0)  + coalesce(bal_tax_fine,0) :: numeric as balanceamount,entered_on"
//                + "from vt_tax_balance where regn_no =? and state_cd=? and off_cd=? and rcpt_no is null  ";
//        String whereIam = "TaxServer_Impl.getBalanceTaxAmountAgainstRegnNo";
//        Long balanceAmount = 0l;
//        TransactionManager tmgr = null;
//        try {
//            tmgr = new TransactionManager(whereIam);
//            PreparedStatement ps = tmgr.prepareStatement(taxBalanceSQL);
//            ps.setString(1, regnNo);
//            ps.setString(2, Util.getUserStateCode());
//            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
//
//            RowSet rs = tmgr.fetchDetachedRowSet();
//            if (rs.next()) {
//                balanceAmount = rs.getLong("balanceamount");
//            }
//        } catch (SQLException e) {
//            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//        } finally {
//            try {
//                if (tmgr != null) {
//                    tmgr.release();
//                }
//            } catch (Exception e) {
//                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//            }
//        }
//
//        return balanceAmount;
//    }
    /**
     *
     * @param regnNo Registration Number of the Vehicle
     * @param taxPurCd Tax option : 58 for Road Tax , 59 for Additional Raod Tax
     * @return Tax Upto Date
     */
    public Date getTaxFromUptoDate(String regnNo, int taxPurCd) {
        Date taxFromDate = null;
        String taxClearDateSQL = null;
        taxClearDateSQL = "SELECT * FROM (SELECT MAX(TAX_FROM) TAX_FROM , MAX(TAX_UPTO) TAX_UPTO FROM vt_tax where regn_no = ?"
                + "and pur_Cd =? ) tax "
                + " WHERE TAX_FROM IS NOT NULL AND TAX_UPTO IS NOT NULL";
        String whereIam = "TaxServerImplementation.getTaxFromUptoDate";
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly(whereIam);
            PreparedStatement ps = tmgr.prepareStatement(taxClearDateSQL);
            ps.setString(1, regnNo);
            ps.setInt(2, taxPurCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                taxFromDate = rs.getDate("TAX_UPTO");
                if (taxFromDate != null) {
                    // Adding  one day to the tax from date
                    try {
                        taxFromDate = DateUtils.addToDate(taxFromDate, DateUtils.DAY, 1);
                    } catch (DateUtilsException e) {
                        LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    }
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
        return taxFromDate;
    }

    public String saveTaxTransactionDetails(Long userChg, String payMode, List<Tax_Pay_Dobj> listTaxDobj,
            FeeDraftDobj feeDraftDobj, Owner_dobj dobj, PassengerPermitDetailDobj permitDobj, List<EpayDobj> listTransactionCharge,
            String stateCd, int offCd, NonUseDobj nonUseDobj) throws VahanException {

        String rcptNo = null;

        TransactionManager tmgr = null;
        long inscd = 0;
        Exception e = null;
        try {
            if (dobj == null) {
                throw new VahanException("Owner Details not found, please try again.");
            } else if (dobj.getOwner_name() == null) {
                throw new VahanException("Owner Name not found, please correct and try again.");
            }
            tmgr = new TransactionManager("saveTaxTransactionDetails");
            rcptNo = Receipt_Master_Impl.generateNewRcptNo(offCd, tmgr);

            String sql = "Select rcpt_no from " + TableList.VT_FEE + " where rcpt_no=? and state_cd=? and off_cd=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, rcptNo);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                throw new VahanException("Receipt No " + rcptNo + " is already assigned to Fee");
            }
            List<FeeDobj> list = new ArrayList<>();
            Fee_Pay_Dobj fee_Pay_Dobj = new Fee_Pay_Dobj();
            FeeDobj feeDobj = null;
            if (userChg != null && userChg != 0) {
                sql = "INSERT INTO vt_fee("
                        + " regn_no, payment_mode, fees, fine, rcpt_no, rcpt_dt, pur_cd, "
                        + " flag, collected_by, state_cd, off_cd)"
                        + " VALUES (?, ?, ?, ?, ?, current_timestamp, ?, "
                        + " ?, ?, ?, ?)";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getRegn_no());//regn_no
                ps.setString(2, payMode);//payment_mode
                ps.setLong(3, userChg);//fees
                ps.setLong(4, 0);//fine
                ps.setString(5, rcptNo);//rcpt_no
                ps.setInt(6, TableConstants.VM_TRANSACTION_MAST_USER_CHARGES);//   pur_cd
                ps.setNull(7, java.sql.Types.VARCHAR);//   flag
                ps.setString(8, Util.getEmpCode());//   collected_by
                ps.setString(9, stateCd);//   state_cd
                ps.setInt(10, offCd);//   off_cd
                ps.executeUpdate();
                feeDobj = new FeeDobj();
                feeDobj.setFeeAmount(userChg);
                feeDobj.setFineAmount(0L);
                feeDobj.setPurCd(TableConstants.VM_TRANSACTION_MAST_USER_CHARGES);
                list.add(feeDobj);
            }
            if (listTransactionCharge != null && !listTransactionCharge.isEmpty()) {
                for (EpayDobj ePayDobj : listTransactionCharge) {
                    ServerUtil.saveCommonChargesForAll(ePayDobj, dobj.getRegn_no(), tmgr, rcptNo, payMode);
                    feeDobj = new FeeDobj();
                    feeDobj.setPurCd(ePayDobj.getPurCd());
                    feeDobj.setFeeAmount(ePayDobj.getE_TaxFee());
                    feeDobj.setFineAmount(0L);
                    list.add(feeDobj);
                }
            }
            fee_Pay_Dobj.setCollectedFeeList(list);
            fee_Pay_Dobj.setListTaxDobj(listTaxDobj);
            String applNo = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
            dobj.setAppl_no(applNo);
            savetaxDetails(listTaxDobj, rcptNo, dobj, permitDobj, tmgr);
            if (feeDraftDobj != null) {
                FeeDraftimpl feeDraft_impl = new FeeDraftimpl();
                feeDraftDobj.setAppl_no(applNo);
                feeDraftDobj.setRcpt_no(rcptNo);
                inscd = feeDraft_impl.saveDraftDetails(feeDraftDobj, tmgr);
            }

            if (nonUseDobj != null) {
                NonUseImpl nonUseImpl = new NonUseImpl();
                nonUseImpl.insertInVhNonUseAdjustFromVtNonUseAdjust(nonUseDobj, tmgr);
                nonUseImpl.deleteFromVtNonUseAdjust(nonUseDobj, tmgr);
            }
            FeeImpl feeImpl = new FeeImpl();
//            FeeImpl.PaymentGenInfo paymentInfo = feeImpl.getPaymentInfo(listTaxDobj, feeDraftDobj, userChg);
            FeeImpl.PaymentGenInfo paymentInfo = feeImpl.getPaymentInfo(fee_Pay_Dobj, feeDraftDobj);

            //Save Exemption Order Details
            if (fee_Pay_Dobj.getApplNo() == null) {
                fee_Pay_Dobj.setApplNo(dobj.getRegn_no());
            }
            feeImpl.saveFinePaneltyExemDetails(fee_Pay_Dobj.getApplNo(), rcptNo, tmgr, Util.getEmpCode());

            feeImpl.saveRecptInstMap(inscd, applNo, rcptNo, paymentInfo, dobj, tmgr, null);
            ServerUtil.insertForQRDetails(applNo, null, null, rcptNo, false, DocumentType.RECEIPT_QR, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), tmgr);
            tmgr.commit();

        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ee) {
            if (dobj != null) {
                LOGGER.error("RegnNo:" + dobj.getRegn_no() + "," + ee.toString() + " " + ee.getStackTrace()[0]);
            } else {
                LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            }
            e = ee;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ee) {
                LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            }
        }

        if (e != null) {
            throw new VahanException("Receipt Number Generation Failed");
        }
        return rcptNo;

    }

    /**
     * Save Tax Transaction Data
     *
     *
     */
    public boolean savetaxDetails(List<Tax_Pay_Dobj> listTaxDobj, String receiptNo, Owner_dobj ownerDobj, PassengerPermitDetailDobj permitDobj, TransactionManager tmgr) throws VahanException {
        boolean isSavedFlag = false;
        StringBuilder taxNoAdvUnits = null;
        try {
            //Generate a receipt No
            // off_cd = rto_cd      
            String applno = ownerDobj.getAppl_no();
            for (Tax_Pay_Dobj taxDobj : listTaxDobj) {
                if (taxDobj.getTaxMode().equals("0")) {
                    continue;
                }

                if (taxDobj.getTaxMode().equals("E")) {
                    continue;
                }

                if (taxDobj.getFinalTaxAmount() <= 0) {
                    /*
                     * Tax Amount can't be less than equal to Zero and Final Tax Amount can't be less than 0 
                     */
                    if (taxDobj.getTotalPaybaleTax() <= 0 || taxDobj.getFinalTaxAmount() < 0) {
                        throw new VahanException("Tax Amount Less than 0 can't be saved");
                    }
                }
                if (!taxDobj.getTaxMode().equals("O") && !taxDobj.getTaxMode().equals("L")
                        && !taxDobj.getTaxMode().equals("S")) {

                    if (taxDobj.getFinalTaxUpto() == null) {
                        throw new VahanException("Tax Paid Upto Date can't be NULL for Selected Tax Mode");
                    }
                    if (!"OR,KL".contains(Util.getUserStateCode()) && !taxDobj.getTaxMode().equals("I")
                            && ("PY".contains(Util.getUserStateCode()) && taxDobj.getTaxCeaseDate() == null)) {
                        if (DateUtils.isAfter(DateUtils.getCurrentDate(), taxDobj.getFinalTaxUpto())) {
                            throw new VahanException("Tax Paid Upto Date can't be less than current date");
                        }
                    }
                }

                String vtTaxSQL = "INSERT INTO vt_tax("
                        + " regn_no, tax_mode, payment_mode, tax_amt, tax_fine, rcpt_no, rcpt_dt, tax_from, tax_upto, pur_cd, flag, collected_by, state_cd,off_cd)"
                        + " VALUES (?, ?, ?, ?, ?, ?, current_timestamp, ?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement pstmtVtTax = tmgr.prepareStatement(vtTaxSQL);
                int i = 1;
                pstmtVtTax.setString(i++, taxDobj.getRegnNo());//regn_no
                pstmtVtTax.setString(i++, taxDobj.getTaxMode());//tax_mode
                pstmtVtTax.setString(i++, taxDobj.getPaymentMode());//payment_mode
                pstmtVtTax.setLong(i++, taxDobj.getFinalTaxAmount() - taxDobj.getTotalPaybalePenalty());//tax_amt
                pstmtVtTax.setLong(i++, taxDobj.getTotalPaybalePenalty());//tax_fine
                pstmtVtTax.setString(i++, receiptNo);//rcpt_no
                pstmtVtTax.setDate(i++, new java.sql.Date(DateUtils.parseDate(taxDobj.getFinalTaxFrom()).getTime()));//tax_from
                if (taxDobj.getFinalTaxUpto() != null) {
                    pstmtVtTax.setDate(i++, new java.sql.Date(DateUtils.parseDate(taxDobj.getFinalTaxUpto()).getTime()));//tax_upto
                } else {
                    pstmtVtTax.setNull(i++, java.sql.Types.DATE);//tax_upto
                }

                pstmtVtTax.setInt(i++, taxDobj.getPur_cd());//   pur_cd
                pstmtVtTax.setNull(i++, java.sql.Types.VARCHAR);//   flag
                pstmtVtTax.setString(i++, Util.getEmpCode());//   collected_by
                pstmtVtTax.setString(i++, Util.getUserStateCode());//   state_cd
                pstmtVtTax.setInt(i++, Util.getSelectedSeat().getOff_cd());//   off_cd
                pstmtVtTax.executeUpdate();
                if (Util.getUserStateCode().equalsIgnoreCase("ML")) {
                    TaxClearImplementation taxClearImpl = new TaxClearImplementation();
                    taxClearImpl.updateAppl_noInVt_Refund_Excess(applno, taxDobj.getPur_cd(), taxDobj.getRegnNo(), TableConstants.TM_ROLE_TAX_COLLECTION, tmgr);
                }
                if (!ownerDobj.getRegn_no().equalsIgnoreCase("NEW") && "OR,KA".contains(Util.getUserStateCode()) && TableConstants.TRACTOR_VEH_CLASS.contains("," + ownerDobj.getVh_class() + ",")) {
                    String sqlOwnerDetailTrailor = "select distinct regn_no from " + TableList.VT_OWNER
                            + " where (regn_no,state_cd,off_cd) in (select regn_no,state_cd,off_cd from " + TableList.VT_SIDE_TRAILER + " where link_regn_no =? and state_cd=? and off_cd=?)";
                    PreparedStatement ps = tmgr.prepareStatement(sqlOwnerDetailTrailor);
                    ps.setString(1, taxDobj.getRegnNo());
                    ps.setString(2, Util.getUserStateCode());
                    ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                    RowSet rst = tmgr.fetchDetachedRowSet_No_release();
                    while (rst.next()) {
                        String trailor_regn_no = rst.getString("regn_no");
                        String vtTaxClear = "INSERT INTO " + TableList.VT_TAX_CLEAR + "(\n"
                                + "            state_cd, off_cd, appl_no, regn_no, pur_cd, clear_fr, clear_to, \n"
                                + "            tcr_no,  iss_dt, remark, user_cd, op_dt, rcpt_no, rcpt_dt)\n"
                                + "    VALUES (?, ?, ?, ?, ?, ?, ?, \n"
                                + "            ?, CURRENT_TIMESTAMP,?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP);";

                        PreparedStatement pstmtVtTaxClear = tmgr.prepareStatement(vtTaxClear);
                        int k = 1;
                        pstmtVtTaxClear.setString(k++, Util.getUserStateCode());
                        pstmtVtTaxClear.setInt(k++, Util.getSelectedSeat().getOff_cd());
                        pstmtVtTaxClear.setString(k++, ownerDobj.getAppl_no());
                        pstmtVtTaxClear.setString(k++, trailor_regn_no);
                        pstmtVtTaxClear.setInt(k++, taxDobj.getPur_cd());//   pur_cd
                        pstmtVtTaxClear.setDate(k++, new java.sql.Date(DateUtils.parseDate(taxDobj.getFinalTaxFrom()).getTime()));//tax_from
                        if (taxDobj.getFinalTaxUpto() != null) {
                            pstmtVtTaxClear.setDate(k++, new java.sql.Date(DateUtils.parseDate(taxDobj.getFinalTaxUpto()).getTime()));//tax_upto
                        } else {
                            pstmtVtTaxClear.setNull(k++, java.sql.Types.DATE);//tax_upto
                        }
                        pstmtVtTaxClear.setString(k++, taxDobj.getRegnNo());//tcrNo
                        pstmtVtTaxClear.setString(k++, "COMBINED TRAILOR ENTRY");//remark
                        pstmtVtTaxClear.setString(k++, Util.getEmpCode());
                        pstmtVtTaxClear.setString(k++, receiptNo);
                        pstmtVtTaxClear.executeUpdate();
                    }
                }
                ownerDobj.setAppl_no(null);
                int srNo = 0;
                for (DOTaxDetail taxBrkUpDobj : taxDobj.getTaxBreakDetails()) {
                    String vtTaxBrkupSQL = "INSERT INTO vt_tax_breakup("
                            + " rcpt_no, sr_no, tax_from, tax_upto, pur_cd, prv_adjustment, tax, "
                            + " exempted, rebate, surcharge, penalty, interest, tax1, tax2, "
                            + " state_cd, off_cd)"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, "
                            + " ?, ?, ?, ?, ?, ?, ?, "
                            + " ?, ?)";

                    PreparedStatement pstmtVtTaxBrkUp = tmgr.prepareStatement(vtTaxBrkupSQL);

                    // Mulitple Entry for Tax Breakup
                    java.sql.Timestamp taxFrom = null;
                    java.sql.Timestamp taxUpto = null;

                    taxFrom = new java.sql.Timestamp(DateUtils.parseDate(taxBrkUpDobj.getTAX_FROM()).getTime());
                    srNo++;
                    pstmtVtTaxBrkUp.setString(1, receiptNo);//rcpt_no
                    pstmtVtTaxBrkUp.setInt(2, srNo);//sr_no
                    pstmtVtTaxBrkUp.setTimestamp(3, taxFrom);//tax_from
                    if (taxBrkUpDobj.getTAX_UPTO() != null) {
                        taxUpto = new java.sql.Timestamp(DateUtils.parseDate(taxBrkUpDobj.getTAX_UPTO()).getTime());
                        pstmtVtTaxBrkUp.setTimestamp(4, taxUpto);//tax_upto
                    } else {
                        pstmtVtTaxBrkUp.setNull(4, java.sql.Types.DATE);//tax_upto
                    }

                    pstmtVtTaxBrkUp.setInt(5, taxBrkUpDobj.getPUR_CD());//pur_cd
                    pstmtVtTaxBrkUp.setLong(6, taxBrkUpDobj.getPRV_ADJ());//prv_adjustment
                    pstmtVtTaxBrkUp.setDouble(7, taxBrkUpDobj.getAMOUNT());//tax
                    pstmtVtTaxBrkUp.setLong(8, 0);//exempted
                    pstmtVtTaxBrkUp.setDouble(9, taxBrkUpDobj.getREBATE());//rebate
                    pstmtVtTaxBrkUp.setDouble(10, taxBrkUpDobj.getSURCHARGE());//surcharge
                    pstmtVtTaxBrkUp.setDouble(11, taxBrkUpDobj.getPENALTY());//penalty
                    pstmtVtTaxBrkUp.setDouble(12, taxBrkUpDobj.getINTEREST());//interest
                    pstmtVtTaxBrkUp.setDouble(13, taxBrkUpDobj.getAMOUNT1());//excess_amt
                    pstmtVtTaxBrkUp.setDouble(14, taxBrkUpDobj.getAMOUNT2());//cash_amt
                    pstmtVtTaxBrkUp.setString(15, Util.getUserStateCode());//   state_cd
                    pstmtVtTaxBrkUp.setInt(16, Util.getUserOffCode());//   off_cd
                    pstmtVtTaxBrkUp.executeUpdate();
                    isSavedFlag = true;

                }
                if (taxNoAdvUnits == null) {
                    taxNoAdvUnits = new StringBuilder();
                    taxNoAdvUnits.append(taxDobj.getPur_cd()).append("=").append(taxDobj.getNoOfAdvUnits());
                } else {
                    taxNoAdvUnits.append(",").append(taxDobj.getPur_cd()).append("=").append(taxDobj.getNoOfAdvUnits());
                }
                if (Util.getSelectedSeat() != null && Util.getSelectedSeat().getAction_cd() == 99994) {
                    Date taxUpto = taxDobj.getFinalTaxUpto() != null ? DateUtils.parseDate(taxDobj.getFinalTaxUpto()) : new Date();
                    insertUpdateTaxDefaulter(taxDobj.getRegnNo(), taxUpto, taxDobj.getPur_cd(), tmgr);
                }
            }
            if (isSavedFlag) {
                saveTaxBasedInformation(permitDobj, ownerDobj, receiptNo, taxNoAdvUnits != null ? taxNoAdvUnits.toString() : null, tmgr);
            }
        } catch (VahanException ve) {
            isSavedFlag = false;
            throw ve;
        } catch (Exception e) {
            isSavedFlag = false;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Saving Tax Information Failed");
        }

        return isSavedFlag;

    }

    /**
     * @author Kartikey Singh
     * @OriginalComment Save Tax Transaction Data
     */
    public boolean savetaxDetails(List<Tax_Pay_Dobj> listTaxDobj, String receiptNo, Owner_dobj ownerDobj, PassengerPermitDetailDobj permitDobj,
            TransactionManager tmgr, String empCode, String stateCode, int selectedOffCode, int actionCode) throws VahanException {
        boolean isSavedFlag = false;
        StringBuilder taxNoAdvUnits = null;
        try {
            //Generate a receipt No
            // off_cd = rto_cd      
            String applno = ownerDobj.getAppl_no();
            for (Tax_Pay_Dobj taxDobj : listTaxDobj) {
                if (taxDobj.getTaxMode().equals("0")) {
                    continue;
                }

                if (taxDobj.getTaxMode().equals("E")) {
                    continue;
                }

                if (taxDobj.getFinalTaxAmount() <= 0) {
                    /*
                     * Tax Amount can't be less than equal to Zero and Final Tax Amount can't be less than 0 
                     */
                    if (taxDobj.getTotalPaybaleTax() <= 0 || taxDobj.getFinalTaxAmount() < 0) {
                        throw new VahanException("Tax Amount Less than 0 can't be saved");
                    }
                }
                if (!taxDobj.getTaxMode().equals("O") && !taxDobj.getTaxMode().equals("L")
                        && !taxDobj.getTaxMode().equals("S")) {

                    if (taxDobj.getFinalTaxUpto() == null) {
                        throw new VahanException("Tax Paid Upto Date can't be NULL for Selected Tax Mode");
                    }
                    if (!"OR,KL".contains(stateCode) && !taxDobj.getTaxMode().equals("I")
                            && ("PY".contains(stateCode) && taxDobj.getTaxCeaseDate() == null)) {
                        if (DateUtils.isAfter(DateUtils.getCurrentDate(), taxDobj.getFinalTaxUpto())) {
                            throw new VahanException("Tax Paid Upto Date can't be less than current date");
                        }
                    }
                }

                String vtTaxSQL = "INSERT INTO vt_tax("
                        + " regn_no, tax_mode, payment_mode, tax_amt, tax_fine, rcpt_no, rcpt_dt, tax_from, tax_upto, pur_cd, flag, collected_by, state_cd,off_cd)"
                        + " VALUES (?, ?, ?, ?, ?, ?, current_timestamp, ?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement pstmtVtTax = tmgr.prepareStatement(vtTaxSQL);
                int i = 1;
                pstmtVtTax.setString(i++, taxDobj.getRegnNo());//regn_no
                pstmtVtTax.setString(i++, taxDobj.getTaxMode());//tax_mode
                pstmtVtTax.setString(i++, taxDobj.getPaymentMode());//payment_mode
                pstmtVtTax.setLong(i++, taxDobj.getFinalTaxAmount() - taxDobj.getTotalPaybalePenalty());//tax_amt
                pstmtVtTax.setLong(i++, taxDobj.getTotalPaybalePenalty());//tax_fine
                pstmtVtTax.setString(i++, receiptNo);//rcpt_no
                pstmtVtTax.setDate(i++, new java.sql.Date(DateUtils.parseDate(taxDobj.getFinalTaxFrom()).getTime()));//tax_from
                if (taxDobj.getFinalTaxUpto() != null) {
                    pstmtVtTax.setDate(i++, new java.sql.Date(DateUtils.parseDate(taxDobj.getFinalTaxUpto()).getTime()));//tax_upto
                } else {
                    pstmtVtTax.setNull(i++, java.sql.Types.DATE);//tax_upto
                }

                pstmtVtTax.setInt(i++, taxDobj.getPur_cd());//   pur_cd
                pstmtVtTax.setNull(i++, java.sql.Types.VARCHAR);//   flag
                pstmtVtTax.setString(i++, empCode);//   collected_by
                pstmtVtTax.setString(i++, stateCode);//   state_cd
                pstmtVtTax.setInt(i++, selectedOffCode);//   off_cd
                pstmtVtTax.executeUpdate();
                if (stateCode.equalsIgnoreCase("ML")) {
                    new TaxClearImplementation().updateAppl_noInVt_Refund_Excess(applno, taxDobj.getPur_cd(), taxDobj.getRegnNo(), TableConstants.TM_ROLE_TAX_COLLECTION, tmgr, stateCode);
                }
                if (!ownerDobj.getRegn_no().equalsIgnoreCase("NEW") && "OR,KA".contains(stateCode) && TableConstants.TRACTOR_VEH_CLASS.contains("," + ownerDobj.getVh_class() + ",")) {
                    String sqlOwnerDetailTrailor = "select distinct regn_no from " + TableList.VT_OWNER
                            + " where (regn_no,state_cd,off_cd) in (select regn_no,state_cd,off_cd from " + TableList.VT_SIDE_TRAILER + " where link_regn_no =? and state_cd=? and off_cd=?)";
                    PreparedStatement ps = tmgr.prepareStatement(sqlOwnerDetailTrailor);
                    ps.setString(1, taxDobj.getRegnNo());
                    ps.setString(2, stateCode);
                    ps.setInt(3, selectedOffCode);
                    RowSet rst = tmgr.fetchDetachedRowSet_No_release();
                    while (rst.next()) {
                        String trailor_regn_no = rst.getString("regn_no");
                        String vtTaxClear = "INSERT INTO " + TableList.VT_TAX_CLEAR + "(\n"
                                + "            state_cd, off_cd, appl_no, regn_no, pur_cd, clear_fr, clear_to, \n"
                                + "            tcr_no,  iss_dt, remark, user_cd, op_dt, rcpt_no, rcpt_dt)\n"
                                + "    VALUES (?, ?, ?, ?, ?, ?, ?, \n"
                                + "            ?, CURRENT_TIMESTAMP,?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP);";

                        PreparedStatement pstmtVtTaxClear = tmgr.prepareStatement(vtTaxClear);
                        int k = 1;
                        pstmtVtTaxClear.setString(k++, stateCode);
                        pstmtVtTaxClear.setInt(k++, selectedOffCode);
                        pstmtVtTaxClear.setString(k++, ownerDobj.getAppl_no());
                        pstmtVtTaxClear.setString(k++, trailor_regn_no);
                        pstmtVtTaxClear.setInt(k++, taxDobj.getPur_cd());//   pur_cd
                        pstmtVtTaxClear.setDate(k++, new java.sql.Date(DateUtils.parseDate(taxDobj.getFinalTaxFrom()).getTime()));//tax_from
                        if (taxDobj.getFinalTaxUpto() != null) {
                            pstmtVtTaxClear.setDate(k++, new java.sql.Date(DateUtils.parseDate(taxDobj.getFinalTaxUpto()).getTime()));//tax_upto
                        } else {
                            pstmtVtTaxClear.setNull(k++, java.sql.Types.DATE);//tax_upto
                        }
                        pstmtVtTaxClear.setString(k++, taxDobj.getRegnNo());//tcrNo
                        pstmtVtTaxClear.setString(k++, "COMBINED TRAILOR ENTRY");//remark
                        pstmtVtTaxClear.setString(k++, empCode);
                        pstmtVtTaxClear.setString(k++, receiptNo);
                        pstmtVtTaxClear.executeUpdate();
                    }
                }
                ownerDobj.setAppl_no(null);
                int srNo = 0;
                for (DOTaxDetail taxBrkUpDobj : taxDobj.getTaxBreakDetails()) {
                    String vtTaxBrkupSQL = "INSERT INTO vt_tax_breakup("
                            + " rcpt_no, sr_no, tax_from, tax_upto, pur_cd, prv_adjustment, tax, "
                            + " exempted, rebate, surcharge, penalty, interest, tax1, tax2, "
                            + " state_cd, off_cd)"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, "
                            + " ?, ?, ?, ?, ?, ?, ?, "
                            + " ?, ?)";

                    PreparedStatement pstmtVtTaxBrkUp = tmgr.prepareStatement(vtTaxBrkupSQL);

                    // Mulitple Entry for Tax Breakup
                    java.sql.Timestamp taxFrom = null;
                    java.sql.Timestamp taxUpto = null;

                    taxFrom = new java.sql.Timestamp(DateUtils.parseDate(taxBrkUpDobj.getTAX_FROM()).getTime());
                    srNo++;
                    pstmtVtTaxBrkUp.setString(1, receiptNo);//rcpt_no
                    pstmtVtTaxBrkUp.setInt(2, srNo);//sr_no
                    pstmtVtTaxBrkUp.setTimestamp(3, taxFrom);//tax_from
                    if (taxBrkUpDobj.getTAX_UPTO() != null) {
                        taxUpto = new java.sql.Timestamp(DateUtils.parseDate(taxBrkUpDobj.getTAX_UPTO()).getTime());
                        pstmtVtTaxBrkUp.setTimestamp(4, taxUpto);//tax_upto
                    } else {
                        pstmtVtTaxBrkUp.setNull(4, java.sql.Types.DATE);//tax_upto
                    }

                    pstmtVtTaxBrkUp.setInt(5, taxBrkUpDobj.getPUR_CD());//pur_cd
                    pstmtVtTaxBrkUp.setLong(6, taxBrkUpDobj.getPRV_ADJ());//prv_adjustment
                    pstmtVtTaxBrkUp.setDouble(7, taxBrkUpDobj.getAMOUNT());//tax
                    pstmtVtTaxBrkUp.setLong(8, 0);//exempted
                    pstmtVtTaxBrkUp.setDouble(9, taxBrkUpDobj.getREBATE());//rebate
                    pstmtVtTaxBrkUp.setDouble(10, taxBrkUpDobj.getSURCHARGE());//surcharge
                    pstmtVtTaxBrkUp.setDouble(11, taxBrkUpDobj.getPENALTY());//penalty
                    pstmtVtTaxBrkUp.setDouble(12, taxBrkUpDobj.getINTEREST());//interest
                    pstmtVtTaxBrkUp.setDouble(13, taxBrkUpDobj.getAMOUNT1());//excess_amt
                    pstmtVtTaxBrkUp.setDouble(14, taxBrkUpDobj.getAMOUNT2());//cash_amt
                    pstmtVtTaxBrkUp.setString(15, stateCode);//   state_cd
                    pstmtVtTaxBrkUp.setInt(16, selectedOffCode);//   off_cd
                    pstmtVtTaxBrkUp.executeUpdate();
                    isSavedFlag = true;

                }
                if (taxNoAdvUnits == null) {
                    taxNoAdvUnits = new StringBuilder();
                    taxNoAdvUnits.append(taxDobj.getPur_cd()).append("=").append(taxDobj.getNoOfAdvUnits());
                } else {
                    taxNoAdvUnits.append(",").append(taxDobj.getPur_cd()).append("=").append(taxDobj.getNoOfAdvUnits());
                }
                if (actionCode == 99994) {
                    Date taxUpto = taxDobj.getFinalTaxUpto() != null ? DateUtils.parseDate(taxDobj.getFinalTaxUpto()) : new Date();
                    insertUpdateTaxDefaulter(taxDobj.getRegnNo(), taxUpto, taxDobj.getPur_cd(), tmgr, stateCode);
                }
            }
            if (isSavedFlag) {
                saveTaxBasedInformation(permitDobj, ownerDobj, receiptNo, taxNoAdvUnits != null ? taxNoAdvUnits.toString() : null, tmgr,
                        stateCode, selectedOffCode);
            }
        } catch (VahanException ve) {
            isSavedFlag = false;
            throw ve;
        } catch (Exception e) {
            isSavedFlag = false;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Saving Tax Information Failed");
        }

        return isSavedFlag;

    }

    public void saveTaxBasedInformation(PassengerPermitDetailDobj permitDobj, Owner_dobj dobj, String rcptNo, String noAdvUnits, TransactionManager tmgr) throws VahanException {
        String sqlTaxBasedUpon = "Insert into " + TableList.VT_TAX_BASED_ON + "( state_cd, off_cd, rcpt_no, regn_no, purchase_dt, regn_type, vh_class, \n"
                + "  no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt, "
                + "  gcw, fuel, wheelbase, cubic_cap, floor_area, ac_fitted, audio_fitted, "
                + "  video_fitted, vch_purchase_as, vch_catg, sale_amt, length, width, "
                + "  height, imported_vch, other_criteria, fin_yr_sale_amt, pmt_type, "
                + "  pmt_catg, service_type, route_class, route_length, no_of_trips, "
                + "  domain_cd, distance_run_in_quarter,no_adv_units, op_dt) values(?, ?, ?, ?, ?, ?, ?, "
                + "  ?, ?, ?, ?, ?, ?, ?, "
                + "  ?, ?, ?, ?, ?, ?, ?, "
                + "  ?, ?, ?, ?, ?, ?, "
                + "  ?, ?, ?, ?, ?, "
                + "  ?, ?, ?, ?, ?, "
                + "  ?, ?,?, current_timestamp)";

        PreparedStatement ps = null;
        try {
            ps = tmgr.prepareStatement(sqlTaxBasedUpon);
            int i = 1;
            if (Util.getUserStateCode() != null) {
                ps.setString(i++, Util.getUserStateCode());
            } else {
                ps.setString(i++, dobj.getState_cd());
            }

            if (Util.getSelectedSeat() != null && Util.getSelectedSeat().getOff_cd() != 0) {
                ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            } else {
                ps.setInt(i++, dobj.getOff_cd());
            }

            ps.setString(i++, rcptNo);
            ps.setString(i++, dobj.getRegn_no());
            ps.setDate(i++, new java.sql.Date(dobj.getPurchase_dt().getTime()));
            ps.setString(i++, dobj.getRegn_type());
            ps.setInt(i++, dobj.getVh_class());
            ps.setInt(i++, dobj.getNo_cyl());
            ps.setFloat(i++, dobj.getHp());
            ps.setInt(i++, dobj.getSeat_cap());
            ps.setInt(i++, dobj.getStand_cap());
            ps.setInt(i++, dobj.getSleeper_cap());
            ps.setInt(i++, dobj.getUnld_wt());
            ps.setInt(i++, dobj.getLd_wt());
            ps.setInt(i++, dobj.getGcw());
            ps.setInt(i++, dobj.getFuel());
            ps.setInt(i++, dobj.getWheelbase());
            ps.setFloat(i++, dobj.getCubic_cap());
            ps.setFloat(i++, dobj.getFloor_area());
            ps.setString(i++, dobj.getAc_fitted());
            ps.setString(i++, dobj.getAudio_fitted());
            ps.setString(i++, dobj.getVideo_fitted());
            ps.setString(i++, dobj.getVch_purchase_as());
            ps.setString(i++, dobj.getVch_catg());
            ps.setInt(i++, dobj.getSale_amt());
            ps.setInt(i++, dobj.getLength());
            ps.setInt(i++, dobj.getWidth());
            ps.setInt(i++, dobj.getHeight());
            ps.setString(i++, dobj.getImported_vch());
            ps.setInt(i++, dobj.getOther_criteria());
            ps.setInt(i++, dobj.getSale_amt());

            if (permitDobj != null && permitDobj.getPmt_type_code() != null && !permitDobj.getPmt_type_code().equals("")) {
                ps.setInt(i++, Integer.parseInt(permitDobj.getPmt_type_code()));
            } else {
                ps.setNull(i++, java.sql.Types.INTEGER);
            }

            if (permitDobj != null && permitDobj.getPmtCatg() != null && !permitDobj.getPmtCatg().equals("")) {
                ps.setInt(i++, Integer.parseInt(permitDobj.getPmtCatg()));
            } else {
                ps.setNull(i++, java.sql.Types.INTEGER);
            }

            if (permitDobj != null && permitDobj.getServices_TYPE() != null && !permitDobj.getServices_TYPE().equals("")) {
                ps.setInt(i++, Integer.parseInt(permitDobj.getServices_TYPE()));
            } else {
                ps.setNull(i++, java.sql.Types.INTEGER);
            }


            if (permitDobj != null && permitDobj.getRout_code() != null && !permitDobj.getRout_code().equals("")) {
                ps.setInt(i++, Integer.parseInt(permitDobj.getRout_code()));
            } else {
                ps.setNull(i++, java.sql.Types.INTEGER);
            }

            if (permitDobj != null && permitDobj.getRout_length() != null && !permitDobj.getRout_length().equals("")) {
                ps.setInt(i++, Integer.parseInt(permitDobj.getRout_length()));
            } else {
                ps.setNull(i++, java.sql.Types.INTEGER);
            }

            if (permitDobj != null && permitDobj.getNumberOfTrips() != null && !permitDobj.getNumberOfTrips().equals("")) {
                ps.setInt(i++, Integer.parseInt(permitDobj.getNumberOfTrips()));
            } else {
                ps.setNull(i++, java.sql.Types.INTEGER);
            }

            if (permitDobj != null && permitDobj.getDomain_CODE() != null && !permitDobj.getDomain_CODE().equals("")) {
                ps.setInt(i++, Integer.parseInt(permitDobj.getDomain_CODE()));
            } else {
                ps.setNull(i++, java.sql.Types.INTEGER);
            }

            ps.setNull(i++, java.sql.Types.INTEGER);//ps.setInt(i++, permitDobj != null && permitDobj.getDistance_run_in_quarter());
            ps.setString(i++, noAdvUnits);
            ps.executeUpdate();


        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Saving of Tax Permit Detail failed");
        }
    }

    /**
     * @author Kartikey Singh
     */
    public void saveTaxBasedInformation(PassengerPermitDetailDobj permitDobj, Owner_dobj dobj, String rcptNo, String noAdvUnits,
            TransactionManager tmgr, String stateCode, int selectedOffCode) throws VahanException {
        String sqlTaxBasedUpon = "Insert into " + TableList.VT_TAX_BASED_ON + "( state_cd, off_cd, rcpt_no, regn_no, purchase_dt, regn_type, vh_class, \n"
                + "  no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt, "
                + "  gcw, fuel, wheelbase, cubic_cap, floor_area, ac_fitted, audio_fitted, "
                + "  video_fitted, vch_purchase_as, vch_catg, sale_amt, length, width, "
                + "  height, imported_vch, other_criteria, fin_yr_sale_amt, pmt_type, "
                + "  pmt_catg, service_type, route_class, route_length, no_of_trips, "
                + "  domain_cd, distance_run_in_quarter,no_adv_units, op_dt) values(?, ?, ?, ?, ?, ?, ?, "
                + "  ?, ?, ?, ?, ?, ?, ?, "
                + "  ?, ?, ?, ?, ?, ?, ?, "
                + "  ?, ?, ?, ?, ?, ?, "
                + "  ?, ?, ?, ?, ?, "
                + "  ?, ?, ?, ?, ?, "
                + "  ?, ?,?, current_timestamp)";

        PreparedStatement ps = null;
        try {
            ps = tmgr.prepareStatement(sqlTaxBasedUpon);
            int i = 1;
            if (stateCode != null) {
                ps.setString(i++, stateCode);
            } else {
                ps.setString(i++, dobj.getState_cd());
            }

            if (selectedOffCode != 0) {
                ps.setInt(i++, selectedOffCode);
            } else {
                ps.setInt(i++, dobj.getOff_cd());
            }

            ps.setString(i++, rcptNo);
            ps.setString(i++, dobj.getRegn_no());
            ps.setDate(i++, new java.sql.Date(dobj.getPurchase_dt().getTime()));
            ps.setString(i++, dobj.getRegn_type());
            ps.setInt(i++, dobj.getVh_class());
            ps.setInt(i++, dobj.getNo_cyl());
            ps.setFloat(i++, dobj.getHp());
            ps.setInt(i++, dobj.getSeat_cap());
            ps.setInt(i++, dobj.getStand_cap());
            ps.setInt(i++, dobj.getSleeper_cap());
            ps.setInt(i++, dobj.getUnld_wt());
            ps.setInt(i++, dobj.getLd_wt());
            ps.setInt(i++, dobj.getGcw());
            ps.setInt(i++, dobj.getFuel());
            ps.setInt(i++, dobj.getWheelbase());
            ps.setFloat(i++, dobj.getCubic_cap());
            ps.setFloat(i++, dobj.getFloor_area());
            ps.setString(i++, dobj.getAc_fitted());
            ps.setString(i++, dobj.getAudio_fitted());
            ps.setString(i++, dobj.getVideo_fitted());
            ps.setString(i++, dobj.getVch_purchase_as());
            ps.setString(i++, dobj.getVch_catg());
            ps.setInt(i++, dobj.getSale_amt());
            ps.setInt(i++, dobj.getLength());
            ps.setInt(i++, dobj.getWidth());
            ps.setInt(i++, dobj.getHeight());
            ps.setString(i++, dobj.getImported_vch());
            ps.setInt(i++, dobj.getOther_criteria());
            ps.setInt(i++, dobj.getSale_amt());

            if (permitDobj != null && permitDobj.getPmt_type_code() != null && !permitDobj.getPmt_type_code().equals("")) {
                ps.setInt(i++, Integer.parseInt(permitDobj.getPmt_type_code()));
            } else {
                ps.setNull(i++, java.sql.Types.INTEGER);
            }

            if (permitDobj != null && permitDobj.getPmtCatg() != null && !permitDobj.getPmtCatg().equals("")) {
                ps.setInt(i++, Integer.parseInt(permitDobj.getPmtCatg()));
            } else {
                ps.setNull(i++, java.sql.Types.INTEGER);
            }

            if (permitDobj != null && permitDobj.getServices_TYPE() != null && !permitDobj.getServices_TYPE().equals("")) {
                ps.setInt(i++, Integer.parseInt(permitDobj.getServices_TYPE()));
            } else {
                ps.setNull(i++, java.sql.Types.INTEGER);
            }


            if (permitDobj != null && permitDobj.getRout_code() != null && !permitDobj.getRout_code().equals("")) {
                ps.setInt(i++, Integer.parseInt(permitDobj.getRout_code()));
            } else {
                ps.setNull(i++, java.sql.Types.INTEGER);
            }

            if (permitDobj != null && permitDobj.getRout_length() != null && !permitDobj.getRout_length().equals("")) {
                ps.setInt(i++, Integer.parseInt(permitDobj.getRout_length()));
            } else {
                ps.setNull(i++, java.sql.Types.INTEGER);
            }

            if (permitDobj != null && permitDobj.getNumberOfTrips() != null && !permitDobj.getNumberOfTrips().equals("")) {
                ps.setInt(i++, Integer.parseInt(permitDobj.getNumberOfTrips()));
            } else {
                ps.setNull(i++, java.sql.Types.INTEGER);
            }

            if (permitDobj != null && permitDobj.getDomain_CODE() != null && !permitDobj.getDomain_CODE().equals("")) {
                ps.setInt(i++, Integer.parseInt(permitDobj.getDomain_CODE()));
            } else {
                ps.setNull(i++, java.sql.Types.INTEGER);
            }

            ps.setNull(i++, java.sql.Types.INTEGER);//ps.setInt(i++, permitDobj != null && permitDobj.getDistance_run_in_quarter());
            ps.setString(i++, noAdvUnits);
            ps.executeUpdate();


        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Saving of Tax Permit Detail failed");
        }
    }

    public static PassengerPermitDetailDobj getPermitInfoForSavedTax(Owner_dobj ownerDobj) throws VahanException {
        PassengerPermitDetailDobj permitDobj = null;
        PassengerPermitDetailDobj permitDobjCompare = null;
        VahanException vahanexecption = null;
        boolean isTransport = false;
        String sql = "Select pmt_type ,pmt_catg ,service_type ,route_class ,route_length, "
                + "no_of_trips ,domain_cd ,distance_run_in_quarter,other_criteria,op_dt from " + TableList.VT_TAX_BASED_ON
                + " where regn_no=? and state_cd=?  order by op_dt desc limit 1";

        TransactionManagerReadOnly tmgr = null;
        PassengerPermitDetailImpl pmImpl = new PassengerPermitDetailImpl();
        try {
            if (ownerDobj == null || ownerDobj.getRegn_no() == null || ownerDobj.getRegn_no().isEmpty()
                    || ownerDobj.getRegn_no().equalsIgnoreCase("NEW") || ownerDobj.getRegn_no().equalsIgnoreCase("TEMPREG")) {
                return null;
            }
            tmgr = new TransactionManagerReadOnly("getPermitInfoForSavedTax");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getRegn_no());
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                permitDobj = new PassengerPermitDetailDobj();
                permitDobj.setPmt_type_code(rs.getString("pmt_type"));
                permitDobj.setPmtCatg(rs.getString("pmt_catg"));
                permitDobj.setServices_TYPE(rs.getString("service_type"));
                permitDobj.setRout_code(rs.getString("route_class"));
                permitDobj.setRout_length(rs.getString("route_length"));
                permitDobj.setNumberOfTrips(rs.getString("no_of_trips"));
                permitDobj.setDomain_CODE(rs.getString("domain_cd"));
                permitDobj.setOtherCriteria(rs.getString("other_criteria"));
                permitDobj.setOp_dt(rs.getDate("op_dt"));
            }

            isTransport = ServerUtil.isTransport(ownerDobj.getVh_class(), ownerDobj);

            if (isTransport) {//for getting details from vt_permit
                permitDobjCompare = pmImpl.set_vt_permit_regnNo_to_dobj(ownerDobj.getRegn_no(), "");
            }

            if (permitDobjCompare == null && isTransport) {//for getting from vh_permit
                PassengerPermitDetailImpl permitDetailImpl = new PassengerPermitDetailImpl();
                permitDobjCompare = permitDetailImpl.getPermitHistory(ownerDobj.getRegn_no(), ownerDobj.getState_cd());
            }

            if (permitDobjCompare != null) {
                if (permitDobj != null) {
                    permitDobjCompare.setOtherCriteria(permitDobj.getOtherCriteria());
                    if (permitDobjCompare.getServices_TYPE() != null || permitDobjCompare.getServices_TYPE().isEmpty()) {
                        permitDobjCompare.setServices_TYPE(permitDobj.getServices_TYPE());
                    }
                }
            } else {
                permitDobjCompare = permitDobj;
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanexecption = new VahanException(TableConstants.SomthingWentWrong);
            throw vahanexecption;
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        return permitDobjCompare;
    }

    /**
     * @author Kartikey Singh
     */
    public static PassengerPermitDetailDobj getPermitInfoForSavedTax(Owner_dobj ownerDobj, String stateCode) throws VahanException {
        PassengerPermitDetailDobj permitDobj = null;
        PassengerPermitDetailDobj permitDobjCompare = null;
        VahanException vahanexecption = null;
        boolean isTransport = false;
        String sql = "Select pmt_type ,pmt_catg ,service_type ,route_class ,route_length, "
                + "no_of_trips ,domain_cd ,distance_run_in_quarter,other_criteria,op_dt from " + TableList.VT_TAX_BASED_ON
                + " where regn_no=? and state_cd=?  order by op_dt desc limit 1";

        TransactionManagerReadOnly tmgr = null;
        PassengerPermitDetailImplementation pmImpl = new PassengerPermitDetailImplementation(stateCode);
        try {
            if (ownerDobj == null || ownerDobj.getRegn_no() == null || ownerDobj.getRegn_no().isEmpty()
                    || ownerDobj.getRegn_no().equalsIgnoreCase("NEW") || ownerDobj.getRegn_no().equalsIgnoreCase("TEMPREG")) {
                return null;
            }
            tmgr = new TransactionManagerReadOnly("getPermitInfoForSavedTax");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getRegn_no());
            ps.setString(2, stateCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                permitDobj = new PassengerPermitDetailDobj();
                permitDobj.setPmt_type_code(rs.getString("pmt_type"));
                permitDobj.setPmtCatg(rs.getString("pmt_catg"));
                permitDobj.setServices_TYPE(rs.getString("service_type"));
                permitDobj.setRout_code(rs.getString("route_class"));
                permitDobj.setRout_length(rs.getString("route_length"));
                permitDobj.setNumberOfTrips(rs.getString("no_of_trips"));
                permitDobj.setDomain_CODE(rs.getString("domain_cd"));
                permitDobj.setOtherCriteria(rs.getString("other_criteria"));
                permitDobj.setOp_dt(rs.getDate("op_dt"));
            }

            isTransport = ServerUtility.isTransport(ownerDobj.getVh_class(), ownerDobj);

            if (isTransport) {//for getting details from vt_permit
                permitDobjCompare = pmImpl.set_vt_permit_regnNo_to_dobj(ownerDobj.getRegn_no(), "");
            }

            if (permitDobjCompare == null && isTransport) {//for getting from vh_permit                
                permitDobjCompare = pmImpl.getPermitHistory(ownerDobj.getRegn_no(), ownerDobj.getState_cd());
            }

            if (permitDobjCompare != null) {
                if (permitDobj != null) {
                    permitDobjCompare.setOtherCriteria(permitDobj.getOtherCriteria());
                    if (permitDobjCompare.getServices_TYPE() != null || permitDobjCompare.getServices_TYPE().isEmpty()) {
                        permitDobjCompare.setServices_TYPE(permitDobj.getServices_TYPE());
                    }
                }
            } else {
                permitDobjCompare = permitDobj;
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanexecption = new VahanException(TableConstants.SomthingWentWrong);
            throw vahanexecption;
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return permitDobjCompare;
    }

    public static PassengerPermitDetailDobj getPermitInfoForVpInstrumentCart(String regnNo) {
        PassengerPermitDetailDobj permitDobj = null;
        String sql = "Select pmt_type ,pmt_catg ,service_type ,route_class ,route_length, "
                + "no_of_trips ,domain_cd ,distance_run_in_quarter from vp_instrument_cart "
                + " where regn_no=? and user_cd = ? limit 1";

        TransactionManagerReadOnly tmgr = null;
        try {

            tmgr = new TransactionManagerReadOnly("getPermitInfoForVpInstrumentCart");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setInt(2, Integer.parseInt(Util.getEmpCode()));
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                permitDobj = new PassengerPermitDetailDobj();
                permitDobj.setPmt_type_code(rs.getString("pmt_type"));
                permitDobj.setPmtCatg(rs.getString("pmt_catg"));
                permitDobj.setServices_TYPE(rs.getString("service_type"));
                permitDobj.setRout_code(rs.getString("route_class"));
                permitDobj.setRout_length(rs.getString("route_length"));
                permitDobj.setNumberOfTrips(rs.getString("no_of_trips"));
                permitDobj.setDomain_CODE(rs.getString("domain_cd"));

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

        return permitDobj;
    }

    public static PassengerPermitDetailDobj getPermitInfoForSavedTax(String stateCd, int offCd, String appl_no, TransactionManager tmgr) throws VahanException {
        PassengerPermitDetailDobj permitDobj = null;

        String sql = "select b.* from vp_appl_rcpt_mapping a , VT_TAX_BASED_ON b "
                + " where a.rcpt_no=b.rcpt_no and a.state_cd=b.state_cd and a.off_cd=b.off_cd and"
                + " appl_no =? order by op_dt limit 1";

        try {

            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                permitDobj = new PassengerPermitDetailDobj();
                permitDobj.setPmt_type_code(rs.getString("pmt_type"));
                permitDobj.setPmtCatg(rs.getString("pmt_catg"));
                permitDobj.setServices_TYPE(rs.getString("service_type"));
                permitDobj.setRout_code(rs.getString("route_class"));
                permitDobj.setRout_length(rs.getString("route_length"));
                permitDobj.setNumberOfTrips(rs.getString("no_of_trips"));
                permitDobj.setDomain_CODE(rs.getString("domain_cd"));
                permitDobj.setOtherCriteria(rs.getString("other_criteria"));
                permitDobj.setNoOfAdvUnits(rs.getString("no_adv_units"));

            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching of Tax Based on Information");
        }

        return permitDobj;
    }

    public static List<TaxFormPanelBean> getListTaxFormForNewVehicle(Owner_dobj own_dobj, VehicleParameters vehParam, String action, int purCd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        List<TaxFormPanelBean> listTaxForm = new ArrayList<>();
        VehicleParameters vehParameters = null;
        try {

            if (vehParam != null) {
                vehParameters = vehParam;
            } else {
                vehParameters = fillVehicleParametersFromDobj(own_dobj);
            }

            if (vehParameters.getREGN_DATE() != null) {
                vehParameters.setREGN_DATE(JSFUtils.getDateInDD_MMM_YYYY(vehParameters.getREGN_DATE()));
            }
            vehParameters.setPUR_CD(purCd);
            tmgr = new TransactionManagerReadOnly("getAvailalableTaxModes");
            ps = tmgr.prepareStatement("select mods.*,pm.descr from vm_allowed_mods mods,tm_purpose_mast pm  where state_cd=?  and pm.pur_cd=mods.pur_cd  and mods.pur_cd in\n"
                    + " (select dl.pur_cd from dealer.vc_action_purpose_map dl where  mods.state_cd= dl.state_cd  and action=? "
                    + " and fee_type in('" + TableConstants.TM_FEE_TAX_TYPE + "'))");
            if (Util.getUserStateCode() == null) {
                ps.setString(1, own_dobj.getState_cd());
            } else {
                ps.setString(1, Util.getUserStateCode());
            }
            ps.setString(2, action);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                String dataFromTable = "vm_allowed_mods(State:" + rs.getString("state_cd") + ",Sr:" + rs.getInt("sr_no") + ")";
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), dataFromTable)) {
                    TaxFormPanelBean bean = new TaxFormPanelBean();
                    bean.setPur_cd(rs.getInt("pur_cd"));
                    bean.setTaxPurcdDesc(rs.getString("descr"));
                    String taxMods = rs.getString("mods");
                    if (purCd == 18 || purCd == 124) {
                        if (!taxMods.contains("M")) {
                            taxMods = taxMods + ",M";
                        }
                    }
                    List<SelectItem> listTaxModes = fillListTaxModes(taxMods.split(","));
                    bean.setListTaxModes(listTaxModes);
                    if (rs.getString("initial_due_date") != null && !rs.getString("initial_due_date").isEmpty()) {
                        Date initiaDueDate = DateUtils.parseDate(getSqlFormulaValue(replaceTagValues(rs.getString("initial_due_date"), vehParameters), "initial_due_date_getListTaxFormForNewVehicle"));
                        Date effectiveDueDate = rs.getDate("effective_date");
                        bean.setInitialDueDate(initiaDueDate);
                        bean.setInitialEffectiveDate(effectiveDueDate);
                    }
                    listTaxForm.add(bean);
                }
            }

        } catch (Exception e) {
            LOGGER.error("[ Regn No " + own_dobj != null && own_dobj.getRegn_no() != null ? own_dobj.getRegn_no() : "" + "]" + e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Getting Tax Modes");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return listTaxForm;
    }

    public static List<TaxFormPanelBean> getListTaxForm(Owner_dobj own_dobj, VehicleParameters vehParam) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        List<TaxFormPanelBean> listTaxForm = new ArrayList<>();
        VehicleParameters vehParameters = null;
        try {

            if (vehParam != null) {
                vehParameters = vehParam;
            } else {
                vehParameters = fillVehicleParametersFromDobj(own_dobj);
            }

            if (vehParameters.getREGN_DATE() != null) {
                vehParameters.setREGN_DATE(JSFUtils.getDateInDD_MMM_YYYY(vehParameters.getREGN_DATE()));
            }
            vehParameters.setPAYMENT_DATE(DateUtils.getCurrentDate_YYYY_MM_DD());
            tmgr = new TransactionManagerReadOnly("getAvailalableTaxModes");
            ps = tmgr.prepareStatement("SELECT vm_allowed_mods.*,descr  FROM vm_allowed_mods,tm_purpose_mast where state_cd=? and "
                    + " vm_allowed_mods.pur_cd=tm_purpose_mast.pur_cd order by tm_purpose_mast.pur_cd ");
            if (Util.getUserStateCode() == null) {
                ps.setString(1, own_dobj.getState_cd());
            } else {
                ps.setString(1, Util.getUserStateCode());
            }
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                Date dateDueFrom = getTaxDueFromDate(own_dobj, rs.getInt("pur_cd"));
                if (dateDueFrom != null) {
                    vehParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(dateDueFrom));
                }
                if ("KL".contains(Util.getUserStateCode() == null ? own_dobj.getState_cd() : Util.getUserStateCode())) {
                    if (rs.getInt("pur_cd") == 60) {
                        boolean isTaxPaid = isTaxPaidValid(own_dobj.getRegn_no(), rs.getInt("pur_cd"));
                        if (isTaxPaid) {
                            continue;
                        }
                    }
                }

                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getListTaxForm")) {
                    TaxFormPanelBean bean = new TaxFormPanelBean();
                    bean.setPur_cd(rs.getInt("pur_cd"));
                    bean.setTaxPurcdDesc(rs.getString("descr"));
                    String taxMods = rs.getString("mods");
                    if (Util.getSelectedSeat() != null && (Util.getSelectedSeat().getPur_cd() == 18 || Util.getSelectedSeat().getPur_cd() == 124)) {
                        if (!taxMods.contains("M")) {
                            taxMods = taxMods + ",M";
                        }
                    }
                    if (rs.getString("tax_cease_date") != null && !rs.getString("tax_cease_date").isEmpty()) {
                        bean.setTaxCeaseDate(DateUtils.parseDate(rs.getString("tax_cease_date")));
                    }
                    List<SelectItem> listTaxModes = fillListTaxModes(taxMods.split(","));
                    bean.setListTaxModes(listTaxModes);
                    if (rs.getString("initial_due_date") != null && !rs.getString("initial_due_date").isEmpty()) {
                        Date initiaDueDate = DateUtils.parseDate(getSqlFormulaValue(replaceTagValues(rs.getString("initial_due_date"), vehParameters), "initial_due_date_getListTaxForm"));
                        Date effectiveDueDate = rs.getDate("effective_date");
                        bean.setInitialDueDate(initiaDueDate);
                        bean.setInitialEffectiveDate(effectiveDueDate);
                    }
                    listTaxForm.add(bean);
                }
            }

        } catch (Exception e) {
            LOGGER.error("[ Regn No " + own_dobj != null && own_dobj.getRegn_no() != null ? own_dobj.getRegn_no() : "" + "]" + e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Getting Tax Modes");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return listTaxForm;
    }

    private static List<SelectItem> getListTaxModes(Owner_dobj ownerDobj, int pur_cd, VehicleParameters vehicleParameters) throws VahanException {
        String[] taxModes = TaxServerImplementation.getAvailalableTaxModes(ownerDobj, pur_cd, vehicleParameters);
        List<SelectItem> listTaxModes = new ArrayList();
        String[][] dataTaxModes = MasterTableFiller.masterTables.VM_TAX_MODE.getData();
        if (taxModes != null) {
            listTaxModes.add(new SelectItem("0", "--Select--"));
            for (int i = 0; i < taxModes.length; i++) {
                for (int ii = 0; ii < dataTaxModes.length; ii++) {
                    if (dataTaxModes[ii][0].trim().equals(taxModes[i].trim())) {
                        listTaxModes.add(new SelectItem(dataTaxModes[ii][0], dataTaxModes[ii][1]));
                        break;
                    }
                }
            }
        }

        return listTaxModes;
    }

    public static List<SelectItem> fillListTaxModes(String[] taxModes) throws VahanException {
        List<SelectItem> listTaxModes = new ArrayList();
        String[][] dataTaxModes = MasterTableFiller.masterTables.VM_TAX_MODE.getData();
        if (taxModes != null) {
            listTaxModes.add(new SelectItem("0", "--Select--"));
            for (int i = 0; i < taxModes.length; i++) {
                for (int ii = 0; ii < dataTaxModes.length; ii++) {
                    if (dataTaxModes[ii][0].trim().equals(taxModes[i].trim())) {
                        listTaxModes.add(new SelectItem(dataTaxModes[ii][0], dataTaxModes[ii][1]));
                        break;
                    }
                }
            }
        }

        return listTaxModes;
    }

    public static Map<Integer, String> getTaxPurCodeDescr(Owner_dobj own_dobj) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        VehicleParameters vehParameters = null;
        Map<Integer, String> mp = new LinkedHashMap<>();

        try {
            vehParameters = fillVehicleParametersFromDobj(own_dobj);
            if (vehParameters.getREGN_DATE() != null) {
                vehParameters.setREGN_DATE(JSFUtils.getDateInDD_MMM_YYYY(vehParameters.getREGN_DATE()));
            }
            tmgr = new TransactionManagerReadOnly("getAvailalableTaxModes");
            ps = tmgr.prepareStatement("SELECT vm_allowed_mods.*,descr  FROM vm_allowed_mods,tm_purpose_mast where state_cd=? and "
                    + " vm_allowed_mods.pur_cd=tm_purpose_mast.pur_cd order by tm_purpose_mast.pur_cd ");
            ps.setString(1, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "vm_allowed_mods(State:" + Util.getUserStateCode() + ",SrNo:" + rs.getInt("sr_no"))) {
                    mp.put(rs.getInt("pur_cd"), rs.getString("descr"));
                }
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (ParseException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return mp;
    }

    public static String[] getAvailalableTaxModes(Owner_dobj own_dobj, int pur_cd, VehicleParameters vehParam) throws VahanException {

        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String[] listTaxModes = null;
        VehicleParameters vehParameters = null;
        if (vehParam != null) {
            vehParameters = vehParam;
        } else {
            vehParameters = fillVehicleParametersFromDobj(own_dobj);
        }

        try {
            if (vehParameters.getREGN_DATE() != null) {
                vehParameters.setREGN_DATE(JSFUtils.getDateInDD_MMM_YYYY(vehParameters.getREGN_DATE()));
            }
            tmgr = new TransactionManagerReadOnly("getAvailalableTaxModes");
            ps = tmgr.prepareStatement("SELECT *  FROM vm_allowed_mods where pur_cd=? and state_cd=?");
            ps.setInt(1, pur_cd);
            if (Util.getUserStateCode() == null || Util.getUserStateCode().equals("")) {
                ps.setString(2, own_dobj.getState_cd());
            } else {
                ps.setString(2, Util.getUserStateCode());
            }

            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getAvailalableTaxModes")) {
                    String availableTaxModes = rs.getString("mods");
                    listTaxModes = availableTaxModes.split(",");
                }
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return listTaxModes;
    }

    /**
     * @author Kartikey Singh
     */
    public static String[] getAvailalableTaxModes(Owner_dobj own_dobj, int pur_cd, VehicleParameters vehParam, SessionVariablesModel sessionVariablesModel) throws VahanException {

        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String[] listTaxModes = null;
        VehicleParameters vehParameters = null;
        if (vehParam != null) {
            vehParameters = vehParam;
        } else {
            vehParameters = FormulaUtilities.fillVehicleParametersFromDobj(own_dobj, sessionVariablesModel);
        }

        try {
            if (vehParameters.getREGN_DATE() != null) {
                vehParameters.setREGN_DATE(JSFUtils.getDateInDD_MMM_YYYY(vehParameters.getREGN_DATE()));
            }
            tmgr = new TransactionManagerReadOnly("getAvailalableTaxModes");
            ps = tmgr.prepareStatement("SELECT *  FROM vm_allowed_mods where pur_cd=? and state_cd=?");
            ps.setInt(1, pur_cd);
            if (sessionVariablesModel.getStateCodeSelected() == null || sessionVariablesModel.getStateCodeSelected().equals("")) {
                ps.setString(2, own_dobj.getState_cd());
            } else {
                ps.setString(2, sessionVariablesModel.getStateCodeSelected());
            }

            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getAvailalableTaxModes")) {
                    String availableTaxModes = rs.getString("mods");
                    listTaxModes = availableTaxModes.split(",");
                }
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return listTaxModes;
    }

    /**
     * returns max date +1 day upto which tax is paid if else purchase date is
     * returned
     *
     * @param dobj
     * @param pur_cd
     * @return
     */
    public static Date getTaxDueFromDate(Owner_dobj dobj, int pur_cd) {
        Date dateDueFrom = null;
        Date dateClearUpto = null;
        Date dateTaxUpto = null;
        Date dateExempUpto = null;
        Timestamp taxRcptDate = null;
        Timestamp clearRcptDate = null;
        Timestamp exempRcptDate = null;
        Timestamp rcpDuetDate = null;
        Date dateFeeUpto = null;
        Timestamp feeRcptDate = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("getTaxDueFromDate");
            String sql = "select max(tax_upto) tax_upto,rcpt_dt from  " + TableList.VT_TAX
                    + "       where regn_no=? and pur_cd=? and state_cd=?"
                    + "       and left(tax_mode, 1) in ('Y', 'H', 'Q', 'M','D') "
                    + "       group by rcpt_dt "
                    + "       order by rcpt_dt desc limit 1";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getRegn_no());
            ps.setInt(2, pur_cd);
            ps.setString(3, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dateTaxUpto = rs.getDate("tax_upto");
                if (dateTaxUpto != null) {
                    dateTaxUpto = DateUtils.addToDate(dateTaxUpto, 1, 1);
                    taxRcptDate = rs.getTimestamp("rcpt_dt");
                }

            }

            sql = "select max(clear_to) clear_to,op_dt from  " + TableList.VT_TAX_CLEAR
                    + " where regn_no=? and pur_cd=? and state_cd=? group by op_dt"
                    + "  order by op_dt desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getRegn_no());
            ps.setInt(2, pur_cd);
            ps.setString(3, Util.getUserStateCode());

            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dateClearUpto = rs.getDate("clear_to");
                if (dateClearUpto != null) {
                    dateClearUpto = DateUtils.addToDate(dateClearUpto, 1, 1);
                    clearRcptDate = rs.getTimestamp("op_dt");
                }
            }

            if (dateTaxUpto == null) {
                dateDueFrom = dateClearUpto;
                rcpDuetDate = clearRcptDate;
            } else if (dateClearUpto == null) {
                dateDueFrom = dateTaxUpto;
                rcpDuetDate = taxRcptDate;
            } else {
                if (taxRcptDate != null && clearRcptDate != null) {
                    if (taxRcptDate.after(clearRcptDate)) {
                        dateDueFrom = dateTaxUpto;
                        rcpDuetDate = taxRcptDate;
                    } else {
                        dateDueFrom = dateClearUpto;
                        rcpDuetDate = clearRcptDate;
                    }
                }
            }

            if (pur_cd == TableConstants.TM_ROAD_TAX || pur_cd == TableConstants.TM_ADDN_ROAD_TAX) {
                sql = "select exem_to,op_dt from " + TableList.VT_TAX_EXEM
                        + " where regn_no=?  and state_cd=?  order by op_dt desc limit 1";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getRegn_no());
                ps.setString(2, Util.getUserStateCode());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    dateExempUpto = rs.getDate("exem_to");
                    if (dateExempUpto != null) {
                        dateExempUpto = DateUtils.addToDate(dateExempUpto, 1, 1);
                        exempRcptDate = rs.getTimestamp("op_dt");
                    }
                }
            }
            if (dateExempUpto != null) {
                if (dateDueFrom == null) {
                    dateDueFrom = dateExempUpto;
                } else {
                    if (rcpDuetDate != null && exempRcptDate != null) {
                        if (exempRcptDate.after(rcpDuetDate)) {
                            dateDueFrom = dateExempUpto;
                        }
                    }
                }
            }
            if (pur_cd == TableConstants.TM_AUDIO_FEE || pur_cd == TableConstants.TM_VIDEO_FEE) {
                sql = "select max(fee_upto) fee_upto,rcpt_dt from " + TableList.VT_FEE
                        + " f inner join " + TableList.VT_FEE_BREAKUP + " b on b.rcpt_no=f.rcpt_no and b.state_cd=f.state_cd and b.off_cd=f.off_cd and b.pur_cd=f.pur_cd "
                        + "where regn_no=? and f.pur_cd=? and f.state_cd=? group by f.rcpt_dt order by f.rcpt_dt desc limit 1";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getRegn_no());
                ps.setInt(2, pur_cd);
                ps.setString(3, Util.getUserStateCode());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    dateFeeUpto = rs.getDate("fee_upto");
                    if (dateFeeUpto != null) {
                        dateFeeUpto = DateUtils.addToDate(dateFeeUpto, 1, 1);
                        feeRcptDate = rs.getTimestamp("rcpt_dt");
                    }
                }
            }
            if (dateFeeUpto != null) {
                if (dateDueFrom == null) {
                    dateDueFrom = dateFeeUpto;
                    rcpDuetDate = feeRcptDate;
                } else {
                    if (feeRcptDate != null && feeRcptDate.after(rcpDuetDate)) {
                        dateDueFrom = dateFeeUpto;
                    }
                }
            }
            if (dateDueFrom == null) {
                dateDueFrom = dobj.getPurchase_dt();
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
        return dateDueFrom;
    }
    
    /**
     * returns max date +1 day upto which tax is paid if else purchase date is
     * returned
     * @author Kartikey Singh
     *
     * @param dobj
     * @param pur_cd
     * @param stateCode
     * @return
     */
    public static Date getTaxDueFromDate(Owner_dobj dobj, int pur_cd, String stateCode) {
        Date dateDueFrom = null;
        Date dateClearUpto = null;
        Date dateTaxUpto = null;
        Date dateExempUpto = null;
        Timestamp taxRcptDate = null;
        Timestamp clearRcptDate = null;
        Timestamp exempRcptDate = null;
        Timestamp rcpDuetDate = null;
        Date dateFeeUpto = null;
        Timestamp feeRcptDate = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("getTaxDueFromDate");
            String sql = "select max(tax_upto) tax_upto,rcpt_dt from  " + TableList.VT_TAX
                    + "       where regn_no=? and pur_cd=? and state_cd=?"
                    + "       and left(tax_mode, 1) in ('Y', 'H', 'Q', 'M','D') "
                    + "       group by rcpt_dt "
                    + "       order by rcpt_dt desc limit 1";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getRegn_no());
            ps.setInt(2, pur_cd);
            ps.setString(3, stateCode);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dateTaxUpto = rs.getDate("tax_upto");
                if (dateTaxUpto != null) {
                    dateTaxUpto = DateUtils.addToDate(dateTaxUpto, 1, 1);
                    taxRcptDate = rs.getTimestamp("rcpt_dt");
                }

            }

            sql = "select max(clear_to) clear_to,op_dt from  " + TableList.VT_TAX_CLEAR
                    + " where regn_no=? and pur_cd=? and state_cd=? group by op_dt"
                    + "  order by op_dt desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getRegn_no());
            ps.setInt(2, pur_cd);
            ps.setString(3, stateCode);

            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dateClearUpto = rs.getDate("clear_to");
                if (dateClearUpto != null) {
                    dateClearUpto = DateUtils.addToDate(dateClearUpto, 1, 1);
                    clearRcptDate = rs.getTimestamp("op_dt");
                }
            }

            if (dateTaxUpto == null) {
                dateDueFrom = dateClearUpto;
                rcpDuetDate = clearRcptDate;
            } else if (dateClearUpto == null) {
                dateDueFrom = dateTaxUpto;
                rcpDuetDate = taxRcptDate;
            } else {
                if (taxRcptDate != null && clearRcptDate != null) {
                    if (taxRcptDate.after(clearRcptDate)) {
                        dateDueFrom = dateTaxUpto;
                        rcpDuetDate = taxRcptDate;
                    } else {
                        dateDueFrom = dateClearUpto;
                        rcpDuetDate = clearRcptDate;
                    }
                }
            }

            if (pur_cd == TableConstants.TM_ROAD_TAX || pur_cd == TableConstants.TM_ADDN_ROAD_TAX) {
                sql = "select exem_to,op_dt from " + TableList.VT_TAX_EXEM
                        + " where regn_no=?  and state_cd=?  order by op_dt desc limit 1";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getRegn_no());
                ps.setString(2, stateCode);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    dateExempUpto = rs.getDate("exem_to");
                    if (dateExempUpto != null) {
                        dateExempUpto = DateUtils.addToDate(dateExempUpto, 1, 1);
                        exempRcptDate = rs.getTimestamp("op_dt");
                    }
                }
            }
            if (dateExempUpto != null) {
                if (dateDueFrom == null) {
                    dateDueFrom = dateExempUpto;
                } else {
                    if (rcpDuetDate != null && exempRcptDate != null) {
                        if (exempRcptDate.after(rcpDuetDate)) {
                            dateDueFrom = dateExempUpto;
                        }
                    }
                }
            }
            if (pur_cd == TableConstants.TM_AUDIO_FEE || pur_cd == TableConstants.TM_VIDEO_FEE) {
                sql = "select max(fee_upto) fee_upto,rcpt_dt from " + TableList.VT_FEE
                        + " f inner join " + TableList.VT_FEE_BREAKUP + " b on b.rcpt_no=f.rcpt_no and b.state_cd=f.state_cd and b.off_cd=f.off_cd and b.pur_cd=f.pur_cd "
                        + "where regn_no=? and f.pur_cd=? and f.state_cd=? group by f.rcpt_dt order by f.rcpt_dt desc limit 1";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getRegn_no());
                ps.setInt(2, pur_cd);
                ps.setString(3, stateCode);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    dateFeeUpto = rs.getDate("fee_upto");
                    if (dateFeeUpto != null) {
                        dateFeeUpto = DateUtils.addToDate(dateFeeUpto, 1, 1);
                        feeRcptDate = rs.getTimestamp("rcpt_dt");
                    }
                }
            }
            if (dateFeeUpto != null) {
                if (dateDueFrom == null) {
                    dateDueFrom = dateFeeUpto;
                    rcpDuetDate = feeRcptDate;
                } else {
                    if (feeRcptDate != null && feeRcptDate.after(rcpDuetDate)) {
                        dateDueFrom = dateFeeUpto;
                    }
                }
            }
            if (dateDueFrom == null) {
                dateDueFrom = dobj.getPurchase_dt();
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
        return dateDueFrom;
    }

    public static TaxDobj getAdvancedTaxPaidInfo(Owner_dobj dobj, int pur_cd) {
        TaxDobj taxDobj = null;

        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("getAdvancedTaxPaidInfo");
            String sql = "select b.rcpt_no,a.rcpt_dt::date rcpt_dt,min(a.tax_from) taxfrom,max(a.tax_upto) taxupto,"
                    + " max(a.tax_mode) taxmode,sum(b.tax)  -sum(b.rebate) +sum(b.surcharge) "
                    + "+sum(b.penalty) +sum(b.interest) +sum(b.tax1) +sum(b.tax2)  advTax"
                    + " from vt_tax a "
                    + " inner join vt_tax_breakup b  on b.state_cd=a.state_cd and a.off_cd=b.off_cd and a.rcpt_no=b.rcpt_no"
                    + " and a.rcpt_dt <= b.tax_upto "
                    + " where a.state_cd=? and a.regn_no=? and a.pur_cd=? and left(a.tax_mode, 1) in ('Y', 'H', 'Q', 'M') "
                    + " group by   b.rcpt_no,a.rcpt_dt "
                    + " order by a.rcpt_dt desc "
                    + " limit 1";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, dobj.getRegn_no());
            ps.setInt(3, pur_cd);

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                taxDobj = new TaxDobj();
                //rcptDt = rs.getTimestamp("rcpt_dt");
                taxDobj.setRcpt_dt(rs.getTimestamp("rcpt_dt"));
                //advTaxFrom = rs.getDate("taxfrom");
                taxDobj.setTax_from(rs.getDate("taxfrom"));
                //advTaxUpto= rs.getDate("taxupto");
                taxDobj.setTax_upto(rs.getDate("taxupto"));
                //advTaxPaid =rs.getInt("advTax");
                taxDobj.setTax_amt(rs.getInt("advTax"));
                taxDobj.setTax_mode(rs.getString("taxmode"));

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
        return taxDobj;

    }

    public static String taxPaidInfo(String regn_no) throws VahanException {
        //boolean taxPaidForDay = false;
        TransactionManager tmgr = null;
        Exception e = null;
        Date rcptDate = null;
        String message = null;
        try {
            tmgr = new TransactionManager("validateTaxPaidForDay");
            String sql = "select a.* ,b.descr tax_mode_descr from vt_tax a left join vm_tax_mode b  on a.tax_mode=b.tax_mode"
                    + " where a.regn_no=? and a.pur_cd=58 and a.state_cd=? and left(COALESCE(a.tax_mode, ' '), 1) IN ('Y', 'Q', 'H', 'M', 'L', 'S', 'O') order by rcpt_dt desc limit 1";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                rcptDate = rs.getDate("rcpt_dt");
                if ("L,O,S".contains(rs.getString("tax_mode"))) {
                    message = "Error: " + rs.getString("tax_mode_descr")
                            + " can't be paid again for Vehicle No " + rs.getString("regn_no")
                            + ", already paid vide receipt no " + rs.getString("rcpt_no") + " dated " + DateUtils.getDateInDDMMYYYY(rcptDate);

                } else if (rs.getString("tax_mode_descr") != null && !rs.getString("tax_mode_descr").isEmpty()) {
                    if (DateUtils.compareDates(rcptDate, DateUtils.getCurrentLocalDate()) == 0) {
                        message = "TODAY already Motor Vehicle Tax paid for Vehicle No " + rs.getString("regn_no")
                                + " from " + DateUtils.getDateInDDMMYYYY(rs.getDate("tax_from"))
                                + " to " + DateUtils.getDateInDDMMYYYY(rs.getDate("tax_upto"))
                                + "{" + rs.getString("tax_mode_descr") + "}"
                                + " vide receipt no " + rs.getString("rcpt_no")
                                + "  dated   " + DateUtils.getDateInDDMMYYYY(rcptDate);
                    }
                }
            }
        } catch (Exception ee) {
            e = ee;
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    e = ex;
                }
            }
        }
        if (e != null) {
            throw new VahanException("Error for " + regn_no);
        }
        return message;

    }

    public static boolean validateTaxPaidForDay(String regn_no) throws VahanException {
        boolean taxPaidForDay = false;
        TransactionManager tmgr = null;
        Exception e = null;
        try {
            tmgr = new TransactionManager("validateTaxPaidForDay");
            String sql = "select regn_no from " + TableList.VT_TAX
                    + " where regn_no=? and rcpt_dt::date =current_timestamp::date and state_cd=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                taxPaidForDay = true;
            }
        } catch (Exception ee) {
            e = ee;
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    e = ex;
                }
            }
        }
        if (e != null) {
            throw new VahanException("Error for " + regn_no);
        }
        return taxPaidForDay;

    }

    public static Map<String, String> getTaxPaidAndClearDetail(String regn_no, int pur_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        Map<String, String> mp = new LinkedHashMap<>();

        try {
            tmgr = new TransactionManager("getTaxPaidAndClearDetail");
            String sql = "select a.tax_amt ,to_char(a.tax_from,'dd-Mon-yyyy') as tax_from,to_char(a.tax_upto,'dd-Mon-yyyy') as tax_upto,a.tax_mode,mode.descr,rcpt_no,regn_no from " + TableList.VT_TAX + " a"
                    + " left join vm_tax_mode mode on mode.tax_mode=a.tax_mode "
                    + " where a.regn_no=? and state_cd=?  and pur_cd=? order by a.rcpt_dt DESC limit 1 ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, pur_cd);

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (pur_cd == TableConstants.TM_ROAD_TAX) {
                    mp.put("taxpaid", "Last MV Tax Amt Paid: " + "Rs. " + rs.getString("tax_amt") + "/- " + "(" + rs.getString("descr") + ")" + " vide receipt no " + rs.getString("rcpt_no") + " period from " + rs.getString("tax_from") + " to " + rs.getString("tax_upto"));
                }
                if (pur_cd == TableConstants.TM_ADDN_ROAD_TAX) {
                    mp.put("addtaxpaid", "Last MV Additional Tax Amt Paid: " + "Rs. " + rs.getString("tax_amt") + "/- " + "(" + rs.getString("descr") + ")" + " vide receipt no " + rs.getString("rcpt_no") + " period from " + rs.getString("tax_from") + " to " + rs.getString("tax_upto"));
                }
            }

            sql = "select to_char(a.clear_fr,'dd-Mon-yyyy') as clear_fr,to_char(a.clear_to,'dd-Mon-yyyy') as clear_to,a.tcr_no,a.regn_no from " + TableList.VT_TAX_CLEAR + " a"
                    + " where a.regn_no=? and state_cd=?  and pur_cd=? order by op_dt desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, pur_cd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (pur_cd == TableConstants.TM_ROAD_TAX) {
                    mp.put("taxclear", "Last MV Tax Clearance given from " + rs.getString("clear_fr") + " to " + rs.getString("clear_to"));
                }
                if (pur_cd == TableConstants.TM_ADDN_ROAD_TAX) {
                    mp.put("addtaxclear", "Last MV Additional Tax given Clearance from " + rs.getString("clear_fr") + " to " + rs.getString("clear_to"));
                }
            }

            sql = "select to_char(a.exem_fr,'dd-Mon-yyyy') as exem_fr,to_char(a.exem_to,'dd-Mon-yyyy') as exem_to,a.regn_no from " + TableList.VT_TAX_EXEM + " a"
                    + " where a.regn_no=? and state_cd=?   order by op_dt desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                mp.put("taxExemp", "Last MV Tax Exemption was given from " + rs.getString("exem_fr") + " to " + rs.getString("exem_to"));
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
        return mp;
    }

    public static TaxFieldDobj getTaxField() {
        TaxFieldDobj taxField = new TaxFieldDobj();
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("getTaxField");
            String sql = "Select * from vm_tax_field_label where state_cd=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                taxField.setTax1Label(rs.getString("tax1"));
                taxField.setTax2Label(rs.getString("tax2"));
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

        return taxField;
    }

    public static TaxFieldDobj getTaxFields(String stateCd) {

        TransactionManagerReadOnly tmgr = null;
        TaxFieldDobj taxField = new TaxFieldDobj();
        try {
            tmgr = new TransactionManagerReadOnly("getTaxField");
            String sql = "Select * from vm_tax_field_label where state_cd=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                taxField.setTax1Label(rs.getString("tax1"));
                taxField.setTax2Label(rs.getString("tax2"));
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
        return taxField;
    }

    public static List<DOTaxDetail> callTaxService(VahanTaxParameters taxParameters) throws VahanException {
        List<DOTaxDetail> tempTaxList = null;
        // VahanTaxClient taxClient = null;
        VahanTaxRestClient taxClient = null;
        String purCdDesc = null;
        try {
            purCdDesc = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getDesc(taxParameters.getPUR_CD().toString());
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            taxClient = new VahanTaxRestClient();
            String param = mapper.writeValueAsString(taxParameters);
            String taxServiceResponse = taxClient.mvTax_JSON_STRING(param);
            tempTaxList = taxClient.parseTaxResponse(taxServiceResponse, taxParameters.getPUR_CD(), taxParameters.getTAX_MODE());
            if (tempTaxList != null) {
                tempTaxList = TaxUtils.sortTaxDetails(tempTaxList);
                String errorMessage = "";
                for (DOTaxDetail doTax : tempTaxList) {
                    if (doTax.getAMOUNT() == null || (doTax.getAMOUNT() + (doTax.getAMOUNT1() == null ? 0 : doTax.getAMOUNT1()) + (doTax.getAMOUNT2() == null ? 0 : doTax.getAMOUNT2())) == 0.0) {
                        errorMessage = errorMessage + "No Valid Slab Found For " + purCdDesc
                                + " for duration from " + doTax.getTAX_FROM() + " to " + doTax.getTAX_UPTO() + " \n ";
                    }
                }

                if (!errorMessage.isEmpty()) {
                    throw new VahanException(errorMessage);
                }
            }
        } catch (javax.xml.ws.WebServiceException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Tax Amount Calculation for " + purCdDesc);
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Tax Amount Calculation for " + purCdDesc);
        } finally {
        }


        return tempTaxList;
    }

    /**
     * @author Kartikey Singh Changed the reference of VahanTaxClient class to a
     * new one VahanTaxClientRest Only changed references to the session object,
     * rest kept everything else same as the original
     */
    public static List<DOTaxDetail> callTaxService(VahanTaxParameters taxParameters, String stateCode) throws VahanException {
        List<DOTaxDetail> tempTaxList = null;
        VahanTaxClientRest taxClient = null;
        String purCdDesc = null;
        try {            
            purCdDesc = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getDesc(taxParameters.getPUR_CD().toString());
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            taxClient = new VahanTaxClientRest();
            String param = mapper.writeValueAsString(taxParameters);
            String taxServiceResponse = taxClient.mvTax_JSON_STRING(param);
            tempTaxList = taxClient.parseTaxResponse(taxServiceResponse, taxParameters.getPUR_CD(), taxParameters.getTAX_MODE());            
            if (tempTaxList != null) {
                tempTaxList = TaxUtils.sortTaxDetails(tempTaxList);
                String errorMessage = "";
                for (DOTaxDetail doTax : tempTaxList) {
                    if (doTax.getAMOUNT() == null || (doTax.getAMOUNT() + (doTax.getAMOUNT1() == null ? 0 : doTax.getAMOUNT1()) + (doTax.getAMOUNT2() == null ? 0 : doTax.getAMOUNT2())) == 0.0) {
                        errorMessage = errorMessage + "No Valid Slab Found For " + purCdDesc
                                + " for duration from " + doTax.getTAX_FROM() + " to " + doTax.getTAX_UPTO() + " \n ";
                    }
                }

                if (!errorMessage.isEmpty()) {
                    throw new VahanException(errorMessage);
                }
            }
        } catch (javax.xml.ws.WebServiceException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Tax Amount Calculation for " + purCdDesc);
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Tax Amount Calculation for " + purCdDesc);
        } finally {
        }


        return tempTaxList;
    }

    public static void insertUpdateTaxDefaulter(String regn_no, Date taxUpto, int purCd, TransactionManager tmgr) throws VahanException {
        try {
            if ("NEW,TEMPREG".contains(regn_no) || regn_no.matches(".*/+.*") || taxUpto == null || purCd == TableConstants.TRADE_CERTIFICATE_TAX) {
                return;
            }

            String sql = "select * from vt_owner where regn_no=? and state_cd=? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.last()) {
                if (rs.getRow() > 1) {
                    throw new VahanException(rs.getRow() + "  Records Against Registration Number " + regn_no);
                }
            }

            rs.beforeFirst();

            if (rs.next()) {

                String status = rs.getString("status");
                String stateCd = rs.getString("state_cd");
                if (!CommonUtils.isNullOrBlank(status) && status.equals("N")) {
                    NocImpl nocImpl = new NocImpl();
                    NocDobj nocDobj = nocImpl.set_NOC_appl_db_to_dobj(null, regn_no);
                    if (!Util.getUserStateCode().equals(nocDobj.getState_to()) || !Util.getUserStateCode().equals(stateCd)) {
                        throw new VahanException("Either Invalid vehicle Registration No or You are not authorized to collect the MV Tax for this vehicle no.");
                    }
                }

                int offCd = rs.getInt("off_cd");
                int vhClass = rs.getInt("vh_class");
                String vchCatg = rs.getString("vch_catg");
                //Not a Tax Defaulter
                if (DateUtils.isAfter(DateUtils.parseDate(taxUpto), DateUtils.getCurrentDate())) {
                    sql = "select * from dashboard.vt_tax_not_defaulter_n where regn_no=? and pur_cd=? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, regn_no);
                    ps.setInt(2, purCd);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        updateForTaxNotDefaulter(regn_no, stateCd, offCd, taxUpto, purCd, tmgr);
                    } else {
                        insertForTaxNotDefaulter(regn_no, stateCd, offCd, vhClass, vchCatg, taxUpto, purCd, tmgr);
                    }

                    sql = "delete from dashboard.vt_tax_defaulter_n where regn_no=? and pur_cd=? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, regn_no);
                    ps.setInt(2, purCd);
                    ps.executeUpdate();

                } else {

                    sql = "select * from dashboard.vt_tax_defaulter_n where regn_no=? and pur_cd=? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, regn_no);
                    ps.setInt(2, purCd);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        updateForTaxDefaulter(regn_no, stateCd, offCd, taxUpto, purCd, tmgr);
                    } else {
                        insertForTaxDefaulter(regn_no, stateCd, offCd, vhClass, vchCatg, taxUpto, purCd, tmgr);
                    }
                    sql = "delete from dashboard.vt_tax_not_defaulter_n where regn_no=? and pur_cd=? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, regn_no);
                    ps.setInt(2, purCd);
                    ps.executeUpdate();
                }

            } else {
                throw new VahanException("No Records Found Against Registration Number: " + regn_no);
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error In Tax Defaulter Generation Information");
        }
    }

    /**
     * @author Kartikey Singh
     */
    public static void insertUpdateTaxDefaulter(String regn_no, Date taxUpto, int purCd, TransactionManager tmgr,
            String stateCode) throws VahanException {
        try {
            if ("NEW,TEMPREG".contains(regn_no) || regn_no.matches(".*/+.*") || taxUpto == null || purCd == TableConstants.TRADE_CERTIFICATE_TAX) {
                return;
            }

            String sql = "select * from vt_owner where regn_no=? and state_cd=? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCode);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.last()) {
                if (rs.getRow() > 1) {
                    throw new VahanException(rs.getRow() + "  Records Against Registration Number " + regn_no);
                }
            }

            rs.beforeFirst();

            if (rs.next()) {
                String status = rs.getString("status");
                String stateCd = rs.getString("state_cd");
                if (!CommonUtils.isNullOrBlank(status) && status.equals("N")) {
                    NocImpl nocImpl = new NocImpl();
                    NocDobj nocDobj = nocImpl.set_NOC_appl_db_to_dobj(null, regn_no);
                    if (!stateCode.equals(nocDobj.getState_to()) || !stateCode.equals(stateCd)) {
                        throw new VahanException("Either Invalid vehicle Registration No or You are not authorized to collect the MV Tax for this vehicle no.");
                    }
                }

                int offCd = rs.getInt("off_cd");
                int vhClass = rs.getInt("vh_class");
                String vchCatg = rs.getString("vch_catg");
                //Not a Tax Defaulter
                if (DateUtils.isAfter(DateUtils.parseDate(taxUpto), DateUtils.getCurrentDate())) {
                    sql = "select * from dashboard.vt_tax_not_defaulter_n where regn_no=? and pur_cd=? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, regn_no);
                    ps.setInt(2, purCd);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        updateForTaxNotDefaulter(regn_no, stateCd, offCd, taxUpto, purCd, tmgr);
                    } else {
                        insertForTaxNotDefaulter(regn_no, stateCd, offCd, vhClass, vchCatg, taxUpto, purCd, tmgr);
                    }

                    sql = "delete from dashboard.vt_tax_defaulter_n where regn_no=? and pur_cd=? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, regn_no);
                    ps.setInt(2, purCd);
                    ps.executeUpdate();

                } else {
                    sql = "select * from dashboard.vt_tax_defaulter_n where regn_no=? and pur_cd=? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, regn_no);
                    ps.setInt(2, purCd);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        updateForTaxDefaulter(regn_no, stateCd, offCd, taxUpto, purCd, tmgr);
                    } else {
                        insertForTaxDefaulter(regn_no, stateCd, offCd, vhClass, vchCatg, taxUpto, purCd, tmgr);
                    }
                    sql = "delete from dashboard.vt_tax_not_defaulter_n where regn_no=? and pur_cd=? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, regn_no);
                    ps.setInt(2, purCd);
                    ps.executeUpdate();
                }

            } else {
                throw new VahanException("No Records Found Against Registration Number: " + regn_no);
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error In Tax Defaulter Generation Information");
        }
    }

    public static void insertForTaxDefaulter(String regn_no, String stateCd, int offCd, int vhClass, String vchCatg, Date taxUpto, int purCd, TransactionManager tmgr) throws VahanException {

        try {
            String sql = "INSERT INTO dashboard.vt_tax_defaulter_n(state_cd, off_cd, regn_no, vh_class, vch_catg, pur_cd, tax_cleared_upto )"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?) ";
            PreparedStatement ps = tmgr.prepareStatement(sql);

            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, regn_no);
            ps.setInt(4, vhClass);
            ps.setString(5, vchCatg);
            ps.setInt(6, purCd);
            ps.setDate(7, new java.sql.Date(taxUpto.getTime()));
            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in inserting for Tax Defaulter Details");
        }

    }

    public static void updateForTaxDefaulter(String regn_no, String stateCd, int offCd, Date taxUpto, int purCd, TransactionManager tmgr) throws VahanException {

        try {
            String sql = "UPDATE dashboard.vt_tax_defaulter_n  SET op_dt=null  WHERE state_cd=?  and regn_no=? and pur_cd=? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setString(2, regn_no);
            ps.setInt(3, purCd);
            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Updation for Tax Defaulter Details");
        }

    }

    public static void insertForTaxNotDefaulter(String regn_no, String stateCd, int offCd, int vhClass, String vchCatg, Date taxUpto, int purCd, TransactionManager tmgr) throws VahanException {

        try {
            String sql = "INSERT INTO dashboard.vt_tax_not_defaulter_n(state_cd, off_cd, regn_no, vh_class, vch_catg, pur_cd, tax_cleared_upto )"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?) ";
            PreparedStatement ps = tmgr.prepareStatement(sql);

            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, regn_no);
            ps.setInt(4, vhClass);
            ps.setString(5, vchCatg);
            ps.setInt(6, purCd);
            ps.setDate(7, new java.sql.Date(taxUpto.getTime()));
            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in inserting for Tax Defaulter Details");
        }

    }

    public static void updateForTaxNotDefaulter(String regn_no, String stateCd, int offCd, Date taxUpto, int purCd, TransactionManager tmgr) throws VahanException {

        try {
            String sql = "UPDATE dashboard.vt_tax_not_defaulter_n  SET tax_cleared_upto=?  WHERE state_cd=?  and regn_no=? and pur_cd=? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(taxUpto.getTime()));
            ps.setString(2, stateCd);
            ps.setString(3, regn_no);
            ps.setInt(4, purCd);
            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Updation for Tax Defaulter Details");
        }

    }

    public static List<Integer> getPendingOnlinePurCodesForRechecks(String regno, String stateCd) throws VahanException {
        List<Integer> listPurcd = new ArrayList();
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getPendingOnlinePurCodesForRechecks");
            String sql = "select pur_cd from onlineschema.vt_temp_appl_transaction where regn_no=? and state_cd=? and application_status='R'";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regno);
            ps.setString(2, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                listPurcd.add(rs.getInt("pur_cd"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Getting Pending Online Fees For Rechecks");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        return listPurcd;
    }

    public static String callKlTaxService(VahanTaxParameters taxparameter, int pushbackseat, int ordinaryseat, String regn_no, String conn_Type, String chassiNo) {
        String statusResponse = null;
        String kl_tax_url;
        try {
            JSONArray jsonArray = new JSONArray();
            JSONObject taxParamObj = new JSONObject();
            JSONObject jsonObj = new JSONObject();
            taxParamObj.put("STATE", taxparameter.getSTATE_CD());
            taxParamObj.put("VCH_CATG", taxparameter.getVCH_CATG());
            taxParamObj.put("VH_CLASS", taxparameter.getVH_CLASS());
            taxParamObj.put("NEW_VCH", taxparameter.getNEW_VCH());
            taxParamObj.put("REGN_TYPE", taxparameter.getREGN_TYPE());
            taxParamObj.put("TAX_DUE_FROM_DATE", taxparameter.getTAX_DUE_FROM_DATE());
            jsonObj.put("SEAT_CAP", taxparameter.getSEAT_CAP());
            jsonObj.put("UNLD_WT", taxparameter.getUNLD_WT());
            jsonObj.put("FLOOR_AREA", taxparameter.getFLOOR_AREA());
            jsonObj.put("LD_WT", taxparameter.getLD_WT());
            jsonObj.put("FUEL", taxparameter.getFUEL());
            jsonObj.put("SERVICE_TYPE", taxparameter.getSERVICE_TYPE());
            jsonObj.put("PERMIT_TYPE", taxparameter.getPERMIT_TYPE());
            jsonObj.put("PERMIT_SUB_CATG", taxparameter.getPERMIT_SUB_CATG());
            jsonObj.put("SLEEPAR_CAP", taxparameter.getSLEEPAR_CAP());
            jsonObj.put("CC", taxparameter.getCC());
            jsonObj.put("SALE_AMT", taxparameter.getSALE_AMT());
            jsonObj.put("REGN_DATE", taxparameter.getREGN_DATE());
            jsonObj.put("STAND_CAP", taxparameter.getSTAND_CAP());
            jsonObj.put("PUSHBACK_CAP", pushbackseat);
            jsonObj.put("ORDINARY_CAP", ordinaryseat);
            jsonObj.put("TAX_MODE", taxparameter.getTAX_MODE());
            jsonObj.put("PUR_CD", taxparameter.getPUR_CD());
            if (taxparameter.getPUR_CD() == 60) {
                if (taxparameter.getNEW_VCH().equals("Y")) {
                    jsonObj.put("CESS_LAST_PAID_DATE", "");
                } else {
                    if (isCessAlreadyPaid(regn_no)) {
                        jsonObj.put("CESS_LAST_PAID_DATE", taxparameter.getTAX_DUE_FROM_DATE());
                    } else {
                        jsonObj.put("CESS_LAST_PAID_DATE", "");
                    }
                }

            } else if (taxparameter.getPUR_CD() == 69) {
                jsonObj.put("GREENTAX_PAID_UPTO_DATE", taxparameter.getTAX_DUE_FROM_DATE());
            }
            jsonObj.put("TAX_MODE", taxparameter.getTAX_MODE());
            jsonObj.put("NO_SEATS", taxparameter.getSEAT_CAP());
            if (taxparameter.getPAYMENT_DATE() != null) {
                jsonObj.put("PAYMENT_DATE", taxparameter.getPAYMENT_DATE());
            } else {
                jsonObj.put("PAYMENT_DATE", DateUtils.parseDate(new Date()));
            }

            jsonObj.put("REGN_NO", regn_no);
            jsonObj.put("CHASSIS_NO", chassiNo);

            if (conn_Type != null && TableConstants.KL_TAXRATE_URL.equals(conn_Type)) {
                // JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);
                taxParamObj.put("data", jsonArray);
                String jsonSendToTaxServices = taxParamObj.toString();
                String requestJson = "fields=" + jsonSendToTaxServices;
                kl_tax_url = ServerUtil.getVahanPgiUrl(TableConstants.KL_TAXRATE_URL);
                return statusResponse = sendPost(kl_tax_url, requestJson);
            }

            JSONArray jsTaxEnd = getKlTaxEndordetails(regn_no, taxparameter, pushbackseat, ordinaryseat, chassiNo);
            if (jsTaxEnd != null) {
                taxParamObj = new JSONObject();
                taxParamObj.put("STATE", taxparameter.getSTATE_CD());
                taxParamObj.put("VCH_CATG", taxparameter.getVCH_CATG());
                taxParamObj.put("VH_CLASS", taxparameter.getVH_CLASS());
                taxParamObj.put("PERMIT_SUB_CATG", taxparameter.getPERMIT_SUB_CATG());
                taxParamObj.put("data", jsTaxEnd);
            } else {
                // JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);
                taxParamObj.put("data", jsonArray);
            }
            String jsonSendToTaxServices = taxParamObj.toString();
            String requestJson = "fields=" + jsonSendToTaxServices;
            if (jsTaxEnd != null) {
                kl_tax_url = ServerUtil.getVahanPgiUrl(TableConstants.KL_ENDORTAX_URL);
            } else {
                kl_tax_url = ServerUtil.getVahanPgiUrl(TableConstants.KL_TAX_URL);
            }

            //kl_tax_url = ServerUtil.getVahanPgiUrl(TableConstants.KL_TAX_URL);
            statusResponse = sendPost(kl_tax_url, requestJson);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        return statusResponse;
    }

    public static JSONArray getKlTaxEndordetails(String regno, VahanTaxParameters taxparameter, int pushbackseat, int ordinaryseat, String chassiNo) throws VahanException {
        JSONArray jsonArray = new JSONArray();
        boolean isEndorsed = false;
        TransactionManager tmgr = null;
        boolean flag = false;
        try {
            tmgr = new TransactionManager("getKlTaxEndordetails");
            String sql = "Select * from vt_endorsement_tax where regn_no=? and tax_mode='C'";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regno);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                flag = true;
            }
            if (flag) {
//                sql = "select *,to_char(with_effect_tax_dt,'dd-MM-yyyy') as taxDueFromDt,"
//                        + " to_char(endorsmnttax_from_dt,'dd-MM-yyyy') as ENDORSEMENT_DATE ,to_char(endorsmnttax_upto_dt,'dd-MM-yyyy') as ENDORSEMENT_UPTO_DATE"
//                        + " from vt_endorsement_tax where endorsmnttax_upto_dt >= current_date and regn_no=? ";

                sql = "select *,to_char(with_effect_tax_dt,'dd-MM-yyyy') as taxDueFromDt,"
                        + " to_char(endorsmnttax_from_dt,'dd-MM-yyyy') as ENDORSEMENT_DATE ,to_char(endorsmnttax_upto_dt,'dd-MM-yyyy') as ENDORSEMENT_UPTO_DATE"
                        + " from vt_endorsement_tax where regn_no=? ";

            } else {
                sql = "select a.*,to_char(with_effect_tax_dt,'dd-MM-yyyy') as taxDueFromDt,"
                        + " to_char(endorsmnttax_from_dt,'dd-MM-yyyy') as ENDORSEMENT_DATE ,"
                        + " to_char(endorsmnttax_upto_dt,'dd-MM-yyyy') as ENDORSEMENT_UPTO_DATE "
                        + " from vt_endorsement_tax a "
                        + " left outer join "
                        + " (select * from vt_tax b where b.regn_no=? and b.pur_cd=58 and left(b.tax_mode, 1) "
                        + "  in ('Y', 'H', 'Q', 'M')  order by rcpt_dt desc limit 1 ) c "
                        + "   on a.regn_no=c.regn_no and a.state_cd=c.state_cd  "
                        + "   where a.regn_no=? and (c.regn_no is null or with_effect_tax_dt > c.tax_upto)";
            }
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regno);
            if (!flag) {
                ps.setString(2, regno);
            }
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                JSONObject jsonObj = new JSONObject();
                //jsonObj.put("TAX_MODE", taxparameter.getTAXMODE());
                if (rs.getString("tax_mode").equals("C")) {//Court Order
                    jsonObj.put("ENDORSEMENT_MODE", rs.getString("tax_mode"));
                    jsonObj.put("NO_OF_QUARTERS", rs.getInt("no_of_quarter"));
                    jsonObj.put("TAX_RATE", rs.getInt("tax_rate"));
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    Date date1 = sdf.parse(rs.getString("taxDueFromDt"));
                    Date date2 = new Date();
                    if (taxparameter.getTAX_DUE_FROM_DATE() != null) {
                        date2 = sdf.parse(taxparameter.getTAX_DUE_FROM_DATE());
                    }
                    if (date1.compareTo(date2) > 0 || date1.compareTo(date2) == 0) {//taxDueFromDt > taxFromDate or equal
                        jsonObj.put("TAX_DUE_FROM_DATE", rs.getString("taxDueFromDt"));
                        jsonObj.put("ENDORSEMENT_DATE", rs.getString("ENDORSEMENT_DATE"));
                        jsonObj.put("ENDORSEMENT_UPTO_DATE", rs.getString("ENDORSEMENT_UPTO_DATE"));

                    } else if (date1.compareTo(date2) < 0) {//taxDueFromDt < taxFrom Date
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date2);
                        calendar.add(Calendar.MONTH, 2);
                        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                        Date nextMonthLastDay = calendar.getTime();
                        jsonObj.put("TAX_DUE_FROM_DATE", taxparameter.getTAX_DUE_FROM_DATE());
                        jsonObj.put("ENDORSEMENT_DATE", rs.getString("ENDORSEMENT_DATE"));
                        jsonObj.put("ENDORSEMENT_UPTO_DATE", sdf.format(nextMonthLastDay));
                    }
                    jsonObj.put("REGN_NO", regno);
                    jsonObj.put("CHASSIS_NO", chassiNo);
                } else {
                    jsonObj.put("ENDORSEMENT_MODE", rs.getString("tax_mode"));
                    jsonObj.put("NO_OF_QUARTERS", rs.getInt("no_of_quarter"));
                    jsonObj.put("TAX_RATE", rs.getInt("tax_rate"));
                    jsonObj.put("TAX_DUE_FROM_DATE", rs.getString("taxDueFromDt"));
                    jsonObj.put("ENDORSEMENT_DATE", rs.getString("ENDORSEMENT_DATE"));
                    jsonObj.put("ENDORSEMENT_UPTO_DATE", rs.getString("ENDORSEMENT_UPTO_DATE"));
                    jsonObj.put("REGN_NO", regno);
                    jsonObj.put("CHASSIS_NO", chassiNo);
                }

                if (rs.getString("tax_mode").equals("A")) {
                    jsonObj.put("SEAT_CAP", taxparameter.getSEAT_CAP());
                    jsonObj.put("FUEL", taxparameter.getSEAT_CAP());
                    jsonObj.put("PUSHBACK_CAP", pushbackseat);//---
                    jsonObj.put("TAX_MODE", taxparameter.getTAX_MODE());
                    jsonObj.put("REGN_DATE", taxparameter.getREGN_DATE());
                    jsonObj.put("STAND_CAP", taxparameter.getSTAND_CAP());
                    jsonObj.put("ORDINARY_CAP", ordinaryseat);//---
                    jsonObj.put("PERMIT_TYPE", taxparameter.getPERMIT_TYPE());
                    jsonObj.put("SERVICE_TYPE", taxparameter.getSERVICE_TYPE());
                    jsonObj.put("NO_SEATS", taxparameter.getSEAT_CAP());
                    jsonObj.put("CC", taxparameter.getCC());
                    jsonObj.put("SLEEPAR_CAP", taxparameter.getSLEEPAR_CAP());
                    jsonObj.put("UNLD_WT", taxparameter.getUNLD_WT());
                    jsonObj.put("PUR_CD", taxparameter.getPUR_CD());
                    jsonObj.put("LD_WT", taxparameter.getLD_WT());
                    jsonObj.put("SALE_AMT", taxparameter.getSALE_AMT());
                    jsonObj.put("FLOOR_AREA", taxparameter.getFLOOR_AREA());
                    jsonObj.put("TAX_DUE_FROM_DATE", rs.getString("taxDueFromDt"));
                }

                jsonArray.put(jsonObj);
                isEndorsed = true;
            }
            System.out.println(": " + jsonArray.toString());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Getting Pending Online Fees For Rechecks");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        if (isEndorsed) {
            return jsonArray;
        } else {
            return null;
        }
    }

    public static String sendPost(String url, String urlParameters) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    public static List<DOTaxDetail> callKLTaxService(VahanTaxParameters taxParameters, int pushbackseat, int ordinaryseat, String regn_no, String chassiNo) throws VahanException {
        List<DOTaxDetail> tempTaxList = null;
        TransactionManager tmgr = null;
        double TAX_AMOUNT = 0;
        String PERIOD_UPTO = "";
        String PERIOD_FROM = "";
        double Interest = 0;
        double AdditionalTax = 0;

        try {
            if (taxParameters.getSTATE_CD().equals("KL")) {
                String statusResponse = callKlTaxService(taxParameters, pushbackseat, ordinaryseat, regn_no, null, chassiNo);
                if (statusResponse != null) {

                    JSONObject jsonResp = new JSONObject(statusResponse);

                    String message = (String) jsonResp.get("message");
                    if (message.equalsIgnoreCase("SUCCESS")) {
                        JSONArray data = (JSONArray) jsonResp.get("data");
                        if (!data.isNull(0)) {
                            DateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject jsonRespData = (JSONObject) data.get(i);
                                PERIOD_FROM = (String) jsonRespData.get("PERIOD_FROM");
                                PERIOD_UPTO = (String) jsonRespData.get("PERIOD_UPTO");
                                if (jsonRespData.get("TAX_AMOUNT") instanceof Integer || jsonRespData.get("TAX_AMOUNT") instanceof Double || jsonRespData.get("TAX_AMOUNT") instanceof String) {
                                    if (jsonRespData.get("TAX_AMOUNT") != null) {
                                        if (jsonRespData.get("TAX_AMOUNT") instanceof String) {
                                            try {
                                                TAX_AMOUNT = Double.parseDouble(jsonRespData.get("TAX_AMOUNT").toString());
                                            } catch (Exception ex) {
                                                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                                            }
                                        } else {
                                            TAX_AMOUNT = (jsonRespData.get("TAX_AMOUNT") instanceof Integer) ? ((Integer) jsonRespData.get("TAX_AMOUNT")).doubleValue() : Double.valueOf(jsonRespData.get("TAX_AMOUNT").toString());
                                        }

                                    }
                                }

                                if (jsonRespData.get("INTEREST_AMOUNT") instanceof Integer || jsonRespData.get("INTEREST_AMOUNT") instanceof Double || jsonRespData.get("INTEREST_AMOUNT") instanceof String) {
                                    if (jsonRespData.get("INTEREST_AMOUNT") != null) {
                                        if (jsonRespData.get("INTEREST_AMOUNT") instanceof String) {
                                            try {
                                                Interest = Double.parseDouble(jsonRespData.get("INTEREST_AMOUNT").toString());
                                            } catch (Exception ex) {
                                                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                                            }

                                        } else {
                                            Interest = (jsonRespData.get("INTEREST_AMOUNT") instanceof Integer) ? ((Integer) jsonRespData.get("INTEREST_AMOUNT")).doubleValue() : Double.valueOf(jsonRespData.get("INTEREST_AMOUNT").toString());
                                        }

                                    }
                                }

                                if (jsonRespData.get("ADDLTAX_AMOUNT") instanceof Integer || jsonRespData.get("ADDLTAX_AMOUNT") instanceof Double || jsonRespData.get("ADDLTAX_AMOUNT") instanceof String) {
                                    if (jsonRespData.get("ADDLTAX_AMOUNT") != null) {
                                        if (jsonRespData.get("ADDLTAX_AMOUNT") instanceof String) {
                                            try {
                                                AdditionalTax = Double.parseDouble(jsonRespData.get("ADDLTAX_AMOUNT").toString());
                                            } catch (Exception ex) {
                                                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                                            }

                                        } else {
                                            AdditionalTax = (jsonRespData.get("ADDLTAX_AMOUNT") instanceof Integer) ? ((Integer) jsonRespData.get("ADDLTAX_AMOUNT")).doubleValue() : Double.valueOf(jsonRespData.get("ADDLTAX_AMOUNT").toString());
                                        }

                                    }
                                }
                            }
                            tempTaxList = new ArrayList<>();
                            String taxHead = ServerUtil.getTaxHead(taxParameters.getPUR_CD());
                            DOTaxDetail doTax = new DOTaxDetail();
//                            if (taxParameters.getPURCD() == 60) {
//                                doTax.setAMOUNT(100.00);
//                            } else {
                            doTax.setAMOUNT(TAX_AMOUNT);
//                            }

                            doTax.setTAX_HEAD(taxHead);
                            doTax.setPUR_CD(taxParameters.getPUR_CD());
                            doTax.setTAX_MODE(taxParameters.getTAX_MODE());
                            doTax.setTAX_FROM(PERIOD_FROM);
                            doTax.setTAX_UPTO(PERIOD_UPTO);
                            doTax.setINTEREST(Interest);
                            doTax.setPENALTY(0.0);
                            doTax.setSURCHARGE(0.0);
                            doTax.setREBATE(0.0);
                            doTax.setAMOUNT1(AdditionalTax);
                            doTax.setAMOUNT2(0.0);
                            tempTaxList.add(doTax);
                            tempTaxList = TaxUtils.sortTaxDetails(tempTaxList);

                        }
                    } else {
                        throw new VahanException(message);
                    }
                } else {
                    throw new VahanException("Tax Service is not responding ");
                }
            }
        } catch (JSONException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (VahanException ve) {
            throw ve;
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return tempTaxList;
    }

    public static boolean isTaxPaidValid(String regn_no, int pur_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager("getTaxPaidAndClearDetail");
            String sql = "select regn_no,tax_upto from " + TableList.VT_TAX
                    + " where regn_no=? and state_cd=?  and pur_cd=? order by rcpt_dt DESC limit 1 ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, pur_cd);

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (rs.getDate("tax_upto") == null || rs.getDate("tax_upto").after(DateUtils.addToDate(new Date(), 1, -1))) {
                    return true;
                }
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
        return false;

    }

    public static boolean isCessAlreadyPaid(String regn_no) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstm = null;
        RowSet rs = null;
        String sql = "";
        boolean isExist = false;
        try {
            tmgr = new TransactionManager("getPreviousDetails");

            sql = "select * from " + TableList.VT_TAX + " where regn_no=? and state_cd =? and pur_cd = ? ";
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(1, regn_no);
            pstm.setString(2, Util.getUserStateCode());
            pstm.setInt(3, 60);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isExist = true;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Unable to get the CESS Details");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return isExist;
    }

    // TaxRate Service
    public static int callKLTaxRateService(VahanTaxParameters taxParameters, int pushbackseat, int ordinaryseat, String regn_no, String chassiNo) throws VahanException {

        int taxRate = 0;

        try {
            if (taxParameters.getSTATE_CD().equals("KL")) {
                String response = callKlTaxService(taxParameters, pushbackseat, ordinaryseat, regn_no, TableConstants.KL_TAXRATE_URL, chassiNo).replace("\"", "");
                try {
                    taxRate = Integer.parseInt(response);
                } catch (NumberFormatException nfe) {
                    throw new VahanException(response);
                }
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in geting tax rate.");
        }
        return taxRate;
    }

    /*
     *This method fetch the tax clear upto date.
     */
    public static Date getPGTaxDetails(String regn_no, int pur_cd) throws VahanException {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        Date taxClearUpto = null;
        String sql;
        try {
            tmgr = new TransactionManager("getPGTaxDetails");
            sql = "select a.* from " + TableList.VT_TAX_CLEAR + " a"
                    + " where a.regn_no=? and state_cd=?  and pur_cd=? order by op_dt desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, pur_cd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                taxClearUpto = rs.getDate("clear_to");
            }
            sql = "select a.* from " + TableList.VT_TAX + " a"
                    + " where a.regn_no=? and state_cd=?  and pur_cd=? order by rcpt_dt desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, pur_cd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                taxClearUpto = rs.getDate("tax_upto");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in getting pg tax clearance details.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return taxClearUpto;
    }
}
