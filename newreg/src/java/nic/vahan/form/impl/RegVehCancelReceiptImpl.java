/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.server.ServerUtil;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.db.DocumentType;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.TaxBasedOnDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.fancy.AdvanceRegnNo_dobj;
import nic.vahan.form.dobj.tax.TaxDobj;
import nic.vahan.form.impl.hsrp.HsrpEntryImpl;
import nic.vahan.form.impl.tax.TaxImpl;
import org.apache.log4j.Logger;

public class RegVehCancelReceiptImpl {

    private static final Logger LOGGER = Logger.getLogger(RegVehCancelReceiptImpl.class);

    public List<Map<String, Integer>> transactionListOfCancelReciept(String rcpt_no, String state_cd, int off_cd, String empCode, int actionCode) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        int totalFee = 0;
        boolean isSameDay = true;
        String regnNo = null;
        String purCdList = null;
        Date rcptDate = null;

        if (rcpt_no != null) {
            rcpt_no = rcpt_no.toUpperCase().trim();
        }

        List<Map<String, Integer>> purMap = null;

        try {
            tmgr = new TransactionManager("transactionListOfCancelReciept");

            sql = "SELECT * FROM get_rcpt_details(?, ?, ?)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, rcpt_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            Map<String, Integer> receiptDetails = new LinkedHashMap<>();
            while (rs.next())//found
            {
                if (rs.getString("status").trim().equalsIgnoreCase(TableConstants.STATUS_APPROVED)) {
                    receiptDetails.put("A", 0);//label,value -----check for one of application is approved from one of purpose 
                    purMap = new ArrayList();
                    purMap.add(receiptDetails);
                    return purMap;
                }
                if (!rs.getString("user_cd").trim().equalsIgnoreCase(empCode.trim()) && actionCode != TableConstants.CANCEL_CASH_RCPT_ADMIN) {
                    receiptDetails.put("U", 0);//label,value ------check for when user is not same who is try to cancel it
                    purMap = new ArrayList();
                    purMap.add(receiptDetails);
                    return purMap;
                }

                SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
                rcptDate = DateUtils.parseDate(rs.getString("rcpt_dt"));
                if (!rs.getString("rcpt_dt").trim().substring(0, 11).equalsIgnoreCase(format.format(new Date()).trim())) {
                    isSameDay = false;
                }

                if (rs.getString("payment_mode").trim().equalsIgnoreCase("Online")) {
                    receiptDetails.put("I", 0);//label,value ------check for when Payment Mode is Online
                    purMap = new ArrayList();
                    purMap.add(receiptDetails);
                    return purMap;
                }

                totalFee = totalFee + rs.getInt("fees");

                if (regnNo == null) {
                    regnNo = rs.getString("regn_no");
                }
                purCdList = rs.getInt("pur_cd") + ",";
            }

            //for checking that if the total fee is zero and role is admin for cancellation
            if (actionCode == TableConstants.CANCEL_CASH_RCPT_ADMIN
                    && (totalFee == 0
                    || (rcptDate != null && !isSameDay && rcptDate.compareTo(ServerUtil.dateRange(new Date(), 0, 0, -3)) >= 0))) {
                //do nothing
                //office admin can cancel the receipt upto 3 days of the receipt date
            } else if (!isSameDay) {
                receiptDetails.put("D", 0);//label,value ------check for when receipt coming for cancel on same day
                purMap = new ArrayList();
                purMap.add(receiptDetails);
                return purMap;
            }


            if (regnNo != null && !"NEW,TEMPREG".contains(regnNo) && purCdList != null) {
                TaxImpl taxImpl = new TaxImpl();
                purCdList = purCdList.replaceAll(",$", "");//for replacing last ,
                TaxDobj taxDobj = taxImpl.getTaxDetails(regnNo, purCdList, state_cd);
                if (taxDobj != null) {
                    if (!taxDobj.getRcpt_no().equalsIgnoreCase(rcpt_no)) {
                        throw new VahanException("Receipt No [" + taxDobj.getRcpt_no() + "] is Latest Receipt than [" + rcpt_no + "]. Please Cancel the Latest Receipt No [" + taxDobj.getRcpt_no() + "] before Receipt No [" + rcpt_no + "]");
                    }
                }
            }

            rs.beforeFirst();
            int paymentMode = 0;
            Map<String, Integer> paymentModeDtls = null;
            purMap = new ArrayList();
            totalFee = 0;
            int totalFine = 0;
            while (rs.next()) {
                receiptDetails.put(rs.getString("purpose") + " (" + rs.getInt("fees") + ")", rs.getInt("pur_cd")); //label,value
                totalFee = totalFee + rs.getInt("fees");
                totalFine = totalFine + rs.getInt("fine");
                if (rs.getString("payment_mode").equalsIgnoreCase(TableConstants.PaymentMode_MIX) && paymentMode == 0) {
                    paymentModeDtls = new LinkedHashMap<>();
                    paymentModeDtls.put("Mixed", 0);//payment mode
                    purMap.add(paymentModeDtls);
                    paymentMode = 1;//for loop breaking
                }
            }
            if (!receiptDetails.isEmpty()) {
                receiptDetails.put("Total Fine (" + totalFine + ")", -1); //label,value for showing Total Fine
                int totalAmount = totalFee + totalFine;
                receiptDetails.put("Total Amount (" + totalAmount + ")", -2); //label,value for showing Total Amount
            }
            purMap.add(0, receiptDetails);
            if (paymentModeDtls != null) {
                purMap.add(1, paymentModeDtls);
            }


        } catch (VahanException vex) {
            throw vex;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Getting Details of Receipt Details, Please Contact to the System Administrator.");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during Getting Details of Receipt Details, Please Contact to the System Administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return purMap;
    }

    public void cancelReceipt(String rcpt_no, String reason, String appl_no, String empCode, String stateCode, int offCode, OwnerDetailsDobj ownerDetailsDobj, String paymentMode, int totalFee, int actionCode, boolean isAdvanceRegnFee) throws SQLException, Exception {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        RowSet rs = null;
        String sql = null;
        String state_cd = stateCode;
        int off_cd = offCode;
        int action_cd = 0;
        List<Status_dobj> status = new ArrayList<>();
        boolean fitnessApproved = false;
        TaxBasedOnDobj taxBasedOnDobj = null;


        try {
            tmgr = new TransactionManager("cancelReceipt");

            sql = "select rcpt_no from vph_instrument_cart  "
                    + "where state_cd = ?  and off_cd = ? and rcpt_no = ?";
            ps = tmgr.prepareStatement(sql);

            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, rcpt_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                throw new VahanException("Receipt can not be Cancelled In Case Of Single Draft Against Multiple Registration No");
            }

            boolean isNewRegistration = false;
            sql = "select * from " + TableList.VA_DETAILS
                    + " where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            rs = tmgr.fetchDetachedRowSet_No_release();

            while (rs.next()) {

                if (rs.getString("entry_status") != null && rs.getString("entry_status").equalsIgnoreCase(TableConstants.STATUS_APPROVED)) {
                    if ((rs.getInt("pur_cd") != TableConstants.VM_PMT_APPLICATION_PUR_CD) && (rs.getInt("pur_cd") != TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE)) {
                        throw new VahanException("Application No [" + appl_no + "] is Approved against Receipt No " + rcpt_no);
                    }
                }

                Status_dobj statusDobj = new Status_dobj();
                statusDobj.setRegn_no(rs.getString("regn_no"));
                statusDobj.setAppl_no(rs.getString("appl_no"));
                statusDobj.setPur_cd(rs.getInt("pur_cd"));
                if (statusDobj.getPur_cd() == 1) {
                    isNewRegistration = true;
                }
                statusDobj.setFlow_slno(1);//initial flow serial no.
                statusDobj.setFile_movement_slno(1);
                statusDobj.setState_cd(rs.getString("state_cd"));
                statusDobj.setOff_cd(rs.getInt("off_cd"));
                statusDobj.setEmp_cd(0);
                statusDobj.setSeat_cd(TableConstants.FEECANCELSTATUS);
                statusDobj.setCntr_id("");
                statusDobj.setStatus("N");
                statusDobj.setOffice_remark("");
                statusDobj.setPublic_remark("");
                statusDobj.setFile_movement_type("F");
                statusDobj.setUser_id(rs.getString("user_id"));
                statusDobj.setAppl_date(ServerUtil.getSystemDateInPostgres());
                statusDobj.setUser_type("");
                statusDobj.setEntry_ip("");
                statusDobj.setEntry_status("");
                statusDobj.setConfirm_ip("");
                statusDobj.setConfirm_status("");
                statusDobj.setConfirm_date(new java.util.Date());

                status.add(statusDobj);
            }

            if (!status.isEmpty() && off_cd == 0 && state_cd == null) {
                state_cd = status.get(0).getState_cd();
                off_cd = status.get(0).getOff_cd();
            }

            boolean isBalanceFeeTaxReceipt = isBalanceFeeTaxCollectionReceipt(appl_no, rcpt_no, state_cd, off_cd);

            if (isBalanceFeeTaxReceipt) {
                sql = "select * from " + TableList.VH_BLACKLIST
                        + " where state_cd = ? and off_cd = ? and right(action_taken,16) = ?";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, state_cd);
                ps.setInt(2, off_cd);
                ps.setString(3, rcpt_no);
                rs = tmgr.fetchDetachedRowSet_No_release();

                if (rs.next()) {
                    sql = "INSERT INTO " + TableList.VT_BLACKLIST
                            + " SELECT state_cd, off_cd, regn_no, complain_type, fir_no, fir_dt, complain,"
                            + " complain_dt, entered_by, compounding_amt"
                            + " FROM " + TableList.VH_BLACKLIST
                            + " where state_cd = ? and off_cd = ? and right(action_taken,16) = ?";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, state_cd);
                    ps.setInt(2, off_cd);
                    ps.setString(3, rcpt_no);
                    ps.executeUpdate();

                    sql = "UPDATE " + TableList.VH_BLACKLIST
                            + " SET action_taken = 'Amount Paid Against Receipt Number " + rcpt_no + " was canceled' "
                            + " where state_cd = ? and off_cd = ? and right(action_taken,16) = ?";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, state_cd);
                    ps.setInt(2, off_cd);
                    ps.setString(3, rcpt_no);
                    ps.executeUpdate();
                }
            }


            sql = "INSERT INTO " + TableList.VT_FEE_CANCEL
                    + " SELECT regn_no, payment_mode, fees, fine, rcpt_no, rcpt_dt, pur_cd,"
                    + "        flag, collected_by, state_cd, off_cd, ? as reason, current_timestamp, collected_by "
                    + " FROM " + TableList.VT_FEE
                    + " where rcpt_no = ? and state_cd = ? and off_cd = ?";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, reason);
            ps.setString(2, rcpt_no);
            ps.setString(3, state_cd);
            ps.setInt(4, off_cd);
            ps.executeUpdate();

            sql = "DELETE FROM " + TableList.VT_FEE
                    + " where rcpt_no = ? and state_cd = ? and off_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, rcpt_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            ps.executeUpdate();


            sql = " INSERT INTO " + TableList.VT_TAX_CANCEL
                    + " SELECT regn_no, tax_mode, payment_mode, tax_amt, tax_fine, rcpt_no,"
                    + " rcpt_dt, tax_from, tax_upto, pur_cd, flag, collected_by, state_cd,"
                    + " off_cd, ? as reason, current_timestamp, collected_by "
                    + " FROM " + TableList.VT_TAX
                    + " where rcpt_no = ? and state_cd = ? and off_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, reason);
            ps.setString(2, rcpt_no);
            ps.setString(3, state_cd);
            ps.setInt(4, off_cd);
            ps.executeUpdate();
            if (!isNewRegistration) {
                sql = "select a.* from vt_tax a, vp_appl_rcpt_mapping  b where  a.rcpt_no=b.rcpt_no and a.state_cd=b.state_cd and "
                        + "a.off_cd=b.off_cd and b.appl_no=? and  a.tax_mode <> 'B'";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_no);
                rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    String regno = rs.getString("regn_no");
                    Date taxUpto = rs.getDate("tax_upto");
                    int purCd = rs.getInt("pur_cd");
                    TaxServer_Impl.insertUpdateTaxDefaulter(regno, taxUpto, purCd, tmgr);
                }
            }

            sql = "DELETE FROM " + TableList.VT_TAX
                    + " where rcpt_no = ? and state_cd = ? and off_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, rcpt_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            ps.executeUpdate();


            sql = "INSERT INTO " + TableList.VH_TAX_CLEAR + "(\n"
                    + "            state_cd, off_cd, appl_no, regn_no, pur_cd, clear_fr, clear_to, \n"
                    + "            tcr_no, iss_auth, iss_dt, remark, user_cd, op_dt, rcpt_no, rcpt_dt, moved_on, moved_by)\n"
                    + "      SELECT state_cd, off_cd, appl_no, regn_no, pur_cd, clear_fr, clear_to, \n"
                    + "       tcr_no, iss_auth, iss_dt, remark, user_cd, op_dt, rcpt_no, rcpt_dt, CURRENT_TIMESTAMP, ?\n"
                    + "  FROM " + TableList.VT_TAX_CLEAR
                    + "     WHERE rcpt_no=? and state_cd = ? and off_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, rcpt_no);
            ps.setString(3, state_cd);
            ps.setInt(4, off_cd);
            int execution = ps.executeUpdate();

            if (execution > 1) {

                if (!isNewRegistration) {
                    sql = "SELECT state_cd, off_cd, appl_no, regn_no, pur_cd, clear_fr, clear_to, "
                            + "       tcr_no, iss_auth, iss_dt, remark,  current_timestamp, rcpt_no, rcpt_dt "
                            + "  FROM " + TableList.VT_TAX_CLEAR
                            + " WHERE appl_no=? and state_cd=? and off_cd=?";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, appl_no);
                    ps.setString(2, state_cd);
                    ps.setInt(3, off_cd);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    while (rs.next()) {
                        String regno = rs.getString("regn_no");
                        Date taxUpto = rs.getDate("clear_to");
                        int purCd = rs.getInt("pur_cd");
                        TaxServer_Impl.insertUpdateTaxDefaulter(regno, taxUpto, purCd, tmgr);

                    }
                }
                sql = "DELETE FROM " + TableList.VT_TAX_CLEAR
                        + " where rcpt_no = ? and state_cd = ? and off_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, rcpt_no);
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
                ps.executeUpdate();
            }


            //Cancelling Fee/Fine Exemtion Order Details
            sql = "INSERT INTO " + TableList.VT_FEE_EXEMTION_CANCEL + "("
                    + " cancel_dt, cancel_by, state_cd, off_cd, rcpt_no, pur_cd, fee_exempted, "
                    + " fine_exempted, order_no, order_dt, exemtion_reason, rcpt_dt, "
                    + " cancel_reason)"
                    + " SELECT current_timestamp,?,state_cd, off_cd, rcpt_no, pur_cd, fee_exempted, fine_exempted, "
                    + " order_no, order_dt, exemtion_reason, rcpt_dt,?"
                    + " FROM " + TableList.VT_FEE_EXEMTION + " Where rcpt_no = ? and state_cd = ? and off_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, reason);
            ps.setString(3, rcpt_no);
            ps.setString(4, state_cd);
            ps.setInt(5, off_cd);
            ps.executeUpdate();

            sql = "DELETE FROM " + TableList.VT_FEE_EXEMTION + " Where rcpt_no = ? and state_cd = ? and off_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, rcpt_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            ps.executeUpdate();


            sql = "  INSERT INTO " + TableList.VT_TAX_BASED_ON_CANCEL
                    + " SELECT current_timestamp as can_dt, ? as can_by,? as reason,a.* FROM " + TableList.VT_TAX_BASED_ON
                    + " a WHERE a.state_cd=? and a.off_cd=? and a.rcpt_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, reason);
            ps.setString(3, state_cd);
            ps.setInt(4, off_cd);
            ps.setString(5, rcpt_no);
            ps.executeUpdate();

            /*
             *Cancelling the previous adjustment tax receipt.
             */
            String registrationNo = getRegNo(appl_no, rcpt_no, state_cd, off_cd);

            sql = "UPDATE " + TableList.VT_REFUND_EXCESS + " SET"
                    + " bal_tax_appl_no = null, road_tax_appl_no = null "
                    + " where regn_no = ? and (road_tax_appl_no = ? or bal_tax_appl_no = ?) and state_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, registrationNo);
            ps.setString(2, appl_no);
            ps.setString(3, appl_no);
            ps.setString(4, state_cd);
            ps.executeUpdate();

            //Cancelling Tax installment fees
            sql = "UPDATE " + TableList.VT_TAX_INSTALLMENT_BRKUP + " SET"
                    + " rcpt_no = null "
                    + " where rcpt_no = ? and state_cd = ? and off_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, rcpt_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            ps.executeUpdate();

            //Cancelling Tax installment check while doing new registration
            sql = "DELETE FROM " + TableList.VT_TAX_INSTALLMENT + ""
                    + " where appl_regn_no = ? and state_cd = ? and off_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            ps.executeUpdate();

            taxBasedOnDobj = ServerUtil.getTaxBasedOnDetails(state_cd, off_cd, rcpt_no);


            sql = "DELETE FROM " + TableList.VT_TAX_BASED_ON
                    + " where rcpt_no = ? and state_cd = ? and off_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, rcpt_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            ps.executeUpdate();

            //Fee & Fine exemption based on condition formula (without GUI)
            sql = "INSERT INTO " + TableList.VT_FEE_FINE_EXEMPTION_CANCEL + "("
                    + " state_cd, off_cd, regn_no, rcpt_no, rcpt_dt, pur_cd, fee_exempted, fine_exempted, "
                    + " cancel_dt, cancel_by, cancel_reason) "
                    + " SELECT state_cd, off_cd, regn_no, rcpt_no, rcpt_dt, pur_cd, fee_exempted, fine_exempted, "
                    + " current_timestamp, ?, ? FROM " + TableList.VT_FEE_FINE_EXEMPTION + " WHERE state_cd=? and off_cd=? and rcpt_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, reason);
            ps.setString(3, state_cd);
            ps.setInt(4, off_cd);
            ps.setString(5, rcpt_no);
            ps.executeUpdate();

            sql = "DELETE FROM " + TableList.VT_FEE_FINE_EXEMPTION + " WHERE rcpt_no = ? and state_cd = ? and off_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, rcpt_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            ps.executeUpdate();

            if (paymentMode != null && paymentMode.equalsIgnoreCase(TableConstants.PaymentMode_MIX)) { //if mixed mode 
                sql = "   INSERT INTO " + TableList.VT_INSTRUMENTS_CANCEL
                        + "  SELECT a.*, ? as reason, current_timestamp as can_dt, ? as can_by "
                        + "    FROM " + TableList.VT_INSTRUMENTS
                        + "    a left join " + TableList.VP_APPL_RCPT_MAPPING
                        + "    b on a.instrument_cd=b.instrument_cd and b.instrument_cd is not null "
                        + "    WHERE a.state_cd=? and a.off_cd=? and b.rcpt_no=? "
                        + "    and a.instrument_cd=(SELECT instrument_cd FROM " + TableList.VP_APPL_RCPT_MAPPING
                        + "    WHERE rcpt_no=? and state_cd=? and off_cd=?) ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, reason);
                ps.setString(2, empCode);
                ps.setString(3, state_cd);
                ps.setInt(4, off_cd);
                ps.setString(5, rcpt_no);
                ps.setString(6, rcpt_no);
                ps.setString(7, state_cd);
                ps.setInt(8, off_cd);
                ps.executeUpdate();

                sql = "DELETE FROM " + TableList.VT_INSTRUMENTS
                        + " WHERE instrument_cd=(SELECT instrument_cd FROM " + TableList.VP_APPL_RCPT_MAPPING
                        + " WHERE rcpt_no=? and state_cd=? and off_cd=?) "
                        + " and state_cd=? and off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, rcpt_no);
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
                ps.setString(4, state_cd);
                ps.setInt(5, off_cd);
                ps.executeUpdate();
            }

            sql = "INSERT INTO " + TableList.VP_APPL_RCPT_MAPPING_CANCEL
                    + " SELECT current_timestamp as moved_on, ? as moved_by,state_cd, off_cd, appl_no, rcpt_no, owner_name, chasi_no, vh_class,"
                    + "       instrument_cd, excess_amt, cash_amt,remarks  "
                    + "  FROM " + TableList.VP_APPL_RCPT_MAPPING
                    + "  WHERE rcpt_no=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, rcpt_no);
            ps.setString(3, state_cd);
            ps.setInt(4, off_cd);
            ps.executeUpdate();

            sql = "DELETE FROM " + TableList.VP_APPL_RCPT_MAPPING
                    + " WHERE rcpt_no=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, rcpt_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            ps.executeUpdate();

            sql = "DELETE FROM " + TableList.VA_TCC_PRINT + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.executeUpdate();


            if (actionCode == TableConstants.CANCEL_CASH_RCPT_ADMIN && totalFee == 0) {

                sql = "INSERT INTO " + TableList.VT_ADVANCE_REGN_NO_CANCEL
                        + " SELECT *,? as reason,current_timestamp as can_dt,? as can_by FROM " + TableList.VT_ADVANCE_REGN_NO
                        + " WHERE recp_no=? and state_cd=? and off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, reason);
                ps.setString(2, empCode);
                ps.setString(3, rcpt_no);
                ps.setString(4, state_cd);
                ps.setInt(5, off_cd);
                ps.executeUpdate();

                sql = "DELETE FROM " + TableList.VT_ADVANCE_REGN_NO
                        + " WHERE recp_no=? and state_cd=? and off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, rcpt_no);
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
                ps.executeUpdate();

            } else {

                if (!status.isEmpty() && status.get(0).getRegn_no().equalsIgnoreCase("NEW")) {
                    sql = "INSERT INTO " + TableList.VT_ADVANCE_REGN_NO_CANCEL
                            + " SELECT *,? as reason,current_timestamp as can_dt,? as can_by FROM " + TableList.VT_ADVANCE_REGN_NO
                            + " WHERE recp_no=? and state_cd=? and off_cd=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, reason);
                    ps.setString(2, empCode);
                    ps.setString(3, rcpt_no);
                    ps.setString(4, state_cd);
                    ps.setInt(5, off_cd);
                    ps.executeUpdate();

                    sql = "DELETE FROM " + TableList.VT_ADVANCE_REGN_NO
                            + " WHERE recp_no=? and state_cd=? and off_cd=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, rcpt_no);
                    ps.setString(2, state_cd);
                    ps.setInt(3, off_cd);
                    ps.executeUpdate();
                } else {
                    sql = "INSERT INTO " + TableList.VT_ADVANCE_REGN_NO_CANCEL
                            + " SELECT *,? as reason,current_timestamp as can_dt,? as can_by FROM " + TableList.VT_ADVANCE_REGN_NO
                            + " WHERE recp_no=? and state_cd=? and off_cd=? and regn_appl_no is null";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, reason);
                    ps.setString(2, empCode);
                    ps.setString(3, rcpt_no);
                    ps.setString(4, state_cd);
                    ps.setInt(5, off_cd);
                    ps.executeUpdate();

                    sql = "DELETE FROM " + TableList.VT_ADVANCE_REGN_NO
                            + " WHERE recp_no=? and state_cd=? and off_cd=? and regn_appl_no is null";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, rcpt_no);
                    ps.setString(2, state_cd);
                    ps.setInt(3, off_cd);
                    ps.executeUpdate();

                }
            }


            sql = "select appl_no from " + TableList.VA_FC_PRINT + " where appl_no=? and state_cd=? and off_cd=?"
                    + " UNION "
                    + " select appl_no from " + TableList.VH_FC_PRINT + " where appl_no=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            ps.setString(4, appl_no);
            ps.setString(5, state_cd);
            ps.setInt(6, off_cd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                fitnessApproved = true;
            }

            if (!fitnessApproved) {//for checking if fitness fee is paid or not                
                sql = "select action_cd from " + TableList.VHA_STATUS
                        + " where appl_no=? and action_cd=? and status=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_no);
                ps.setInt(2, TableConstants.TM_ROLE_NEW_REGISTRATION_FIT_FEE);
                ps.setString(3, "C");//complete
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    fitnessApproved = true;
                }
            }
            updateReceiptStatus(tmgr, appl_no);
            for (int i = 0; i < status.size(); i++) {

                if (!isBalanceFeeTaxReceipt && !isAdvanceRegnFee) {

                    if ((status.get(i).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && !status.get(i).getRegn_no().equalsIgnoreCase("NEW"))
                            || (status.get(i).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_TEMP_REG && !status.get(i).getRegn_no().equalsIgnoreCase("TEMPREG"))) {
                        cancelCashRcptForNewTempReg(tmgr, status.get(i), empCode, ownerDetailsDobj, taxBasedOnDobj);
                    }

                    int file_movement_slno = 1;
                    sql = "select file_movement_slno from " + TableList.VA_STATUS
                            + " where appl_no=? and pur_cd=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status.get(i).getAppl_no());
                    ps.setInt(2, status.get(i).getPur_cd());
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        file_movement_slno = rs.getInt("file_movement_slno");

                        //for handling HPC/Duplicate TO TAX fee cancellation because there is no next flow for HPC/Duplicate TO TAX
                        if (status.get(i).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_HPC
                                || status.get(i).getPur_cd() == TableConstants.VM_DUPLICATE_TO_TAX_CARD) {
                            file_movement_slno++;
                            sql = " INSERT INTO " + TableList.VHA_STATUS
                                    + "       SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, ? as file_movement_slno,"
                                    + "       action_cd, seat_cd, cntr_id, ? as status, office_remark, public_remark,"
                                    + "       file_movement_type, emp_cd, op_dt, current_timestamp as moved_on, ? "
                                    + "       FROM " + TableList.VA_STATUS
                                    + "  where appl_no=? and pur_cd=?";

                            ps = tmgr.prepareStatement(sql);
                            ps.setInt(1, file_movement_slno);
                            ps.setString(2, TableConstants.FEECANCELSTATUS);//changed status from D to Z
                            ps.setString(3, Util.getClientIpAdress());
                            ps.setString(4, status.get(i).getAppl_no());
                            ps.setInt(5, status.get(i).getPur_cd());
                            ps.executeUpdate();
                        } else {
                            sql = " INSERT INTO " + TableList.VHA_STATUS
                                    + "       SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno,"
                                    + "       action_cd, seat_cd, cntr_id, ? as status, office_remark, public_remark,"
                                    + "       file_movement_type, emp_cd, op_dt, current_timestamp as moved_on, ? "
                                    + "       FROM " + TableList.VA_STATUS
                                    + "  where appl_no=? and pur_cd=?";

                            ps = tmgr.prepareStatement(sql);
                            ps.setString(1, TableConstants.FEECANCELSTATUS);//changed status from D to Z
                            ps.setString(2, Util.getClientIpAdress());
                            ps.setString(3, status.get(i).getAppl_no());
                            ps.setInt(4, status.get(i).getPur_cd());
                            ps.executeUpdate();
                        }

                        sql = "DELETE FROM " + TableList.VA_STATUS + " WHERE appl_no=? and pur_cd=?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, status.get(i).getAppl_no());
                        ps.setInt(2, status.get(i).getPur_cd());
                        ps.executeUpdate();

                        if (fitnessApproved && status.get(i).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                            action_cd = TableConstants.TM_ROLE_NEW_REGISTRATION_FEE;
                            int flowSlNo = 0;
                            sql = "SELECT flow_slno FROM " + TableList.VHA_STATUS + " WHERE appl_no=? and action_cd = ? and pur_cd=? and state_cd=? and off_cd=? order by moved_on desc limit 1";
                            ps = tmgr.prepareStatement(sql);
                            ps.setString(1, status.get(i).getAppl_no());
                            ps.setInt(2, action_cd);
                            ps.setInt(3, status.get(i).getPur_cd());
                            ps.setString(4, state_cd);
                            ps.setInt(5, off_cd);
                            RowSet rsNew = tmgr.fetchDetachedRowSet_No_release();
                            if (rsNew.next()) {
                                flowSlNo = rsNew.getInt("flow_slno");
                            }
                            if (flowSlNo == 0) {//if new registration fee is not paid
                                action_cd = ServerUtil.getInitialAction(tmgr, status.get(i).getState_cd(), status.get(i).getPur_cd(), null)[0];
                                flowSlNo = 1;
                            }
                            status.get(i).setFlow_slno(flowSlNo);
                        } else {
                            action_cd = ServerUtil.getInitialAction(tmgr, status.get(i).getState_cd(), status.get(i).getPur_cd(), null)[0];
                        }

                        status.get(i).setAction_cd(action_cd);//Initial Action_cd
                        status.get(i).setFile_movement_slno(file_movement_slno + 1);//old file movement
                        ServerUtil.insertIntoVaStatus(tmgr, status.get(i));
                    }
                }
            }
            ServerUtil.insertForQRDetails(appl_no, null, null, rcpt_no, true, DocumentType.RCPT_CANCEL_QR, state_cd, off_cd, tmgr);
            tmgr.commit();//Commiting data here..

        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

    }

    public String getApplNo(String rcpt_no, String state_cd, int off_cd) throws SQLException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        String applNo = null;

        if (rcpt_no != null) {
            rcpt_no = rcpt_no.toUpperCase().trim();
        }

        try {
            tmgr = new TransactionManager("getApplNo");

            sql = "SELECT distinct appl_no from " + TableList.VP_APPL_RCPT_MAPPING
                    + " WHERE rcpt_no=? and state_cd=? and off_cd =?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, rcpt_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next())//found
            {
                applNo = rs.getString("appl_no");
            }

        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return applNo;
    }

    public String getApprovedStatus(String rcpt_no, String state_cd, int off_cd) throws SQLException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        String approvedStatus = null;

        if (rcpt_no != null) {
            rcpt_no = rcpt_no.toUpperCase().trim();
        }

        try {
            tmgr = new TransactionManager("getApprovedStatus");

            sql = "SELECT entry_status from " + TableList.VA_DETAILS
                    + " WHERE regn_no = (SELECT regn_no FROM " + TableList.VT_ADVANCE_REGN_NO
                    + " WHERE state_cd=? and off_cd=? and recp_no=?) and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, rcpt_no);
            ps.setString(4, state_cd);
            ps.setInt(5, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next())//found
            {
                approvedStatus = rs.getString("entry_status");
            }

        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return approvedStatus;
    }

    public String getAdvanceRegnStatus(String rcpt_no, String state_cd, int off_cd) throws SQLException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        String advanceRegnStatus = null;

        if (rcpt_no != null) {
            rcpt_no = rcpt_no.toUpperCase().trim();
        }

        try {
            tmgr = new TransactionManager("getAdvanceRegnStatus");

            sql = "SELECT regn_appl_no FROM " + TableList.VT_ADVANCE_REGN_NO
                    + " WHERE state_cd=? and off_cd=? and recp_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, rcpt_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next())//found
            {
                advanceRegnStatus = rs.getString("regn_appl_no");
            }

        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return advanceRegnStatus;
    }

    public String getRegNo(String appl_no, String rcptNo, String stateCd, int offCd) {

        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        String regnNo = "";
        try {

            tmgr = new TransactionManager("getRegNo");
            sql = "SELECT distinct regn_no, pur_cd from " + TableList.VA_DETAILS
                    + " WHERE appl_no=? and state_cd=? and off_cd=? order by pur_cd ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) // found
            {
                regnNo = rs.getString("regn_no");
            }


            if ((regnNo == null || regnNo.length() <= 0) && rcptNo != null) {
                sql = "SELECT a.regn_no FROM " + TableList.VT_FEE
                        + " a INNER JOIN " + TableList.VT_OWNER
                        + " b on a.regn_no=b.regn_no  and a.state_cd = b.state_cd and a.off_cd = b.off_cd "
                        + " WHERE a.rcpt_no=? and a.state_cd=? and a.off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, rcptNo);
                ps.setString(2, stateCd);
                ps.setInt(3, offCd);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) // found
                {
                    regnNo = rs.getString("regn_no");
                }
            }


            if ((regnNo == null || regnNo.length() <= 0) && rcptNo != null) {
                sql = "SELECT a.regn_no FROM " + TableList.VT_TAX
                        + " a INNER JOIN " + TableList.VT_OWNER
                        + " b on a.regn_no=b.regn_no and a.state_cd = b.state_cd "
                        + " WHERE a.rcpt_no=? and a.state_cd=? and a.off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, rcptNo);
                ps.setString(2, stateCd);
                ps.setInt(3, offCd);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) // found
                {
                    regnNo = rs.getString("regn_no");
                }
            }
            if ((regnNo == null || regnNo.length() <= 0) && rcptNo != null) {
                sql = "SELECT a.regn_no FROM " + TableList.VT_TAX
                        + " a INNER JOIN " + TableList.VT_OWNER
                        + " b on a.regn_no=b.regn_no "
                        + " WHERE a.rcpt_no=? and a.state_cd=? and a.off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, rcptNo);
                ps.setString(2, stateCd);
                ps.setInt(3, offCd);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) // found
                {
                    regnNo = rs.getString("regn_no");
                }
            }

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
        return regnNo;
    }

    public void cancelCashRcptForNewTempReg(TransactionManager tmgr, Status_dobj dobj, String empCode, OwnerDetailsDobj ownerDetailsDobj, TaxBasedOnDobj taxBasedOnDobj) throws Exception {
        PreparedStatement ps = null;
        String sql = null;
        String regnNoDescr = null;

        if (dobj == null) {
            throw new VahanException("Record Not Found in VA_DETAILS!!!");
        }

        if (dobj.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
            regnNoDescr = "NEW";

            TmConfigurationDobj configurationDobj = Util.getTmConfiguration();
            if (configurationDobj == null) {
                throw new VahanException("State Configuration is not Found in the Database , Please Contact to System Administrator");
            }

            if (configurationDobj.getRegn_gen_type().equalsIgnoreCase(TableConstants.NO_GEN_TYPE_RAND)) {
                throw new VahanException("Ragistration No is Generated Randomly");
            }
        }

        if (dobj.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
            regnNoDescr = "TEMPREG";
        }

        /*sql = "UPDATE " + TableList.VA_RANDOM_REGN_NO
         + " SET regn_no_alloted=?"
         + " WHERE appl_no=? and  regn_no_alloted=?";
         ps = tmgr.prepareStatement(sql);
         ps.setString(1, regnNoDescr);
         ps.setString(2, dobj.getAppl_no());
         ps.setString(3, dobj.getRegn_no());
         ps.executeUpdate();*/


        if ((ownerDetailsDobj != null
                && !ownerDetailsDobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))
                && isRegnNoForOtherStateVehicles(tmgr, dobj.getAppl_no(), ownerDetailsDobj.getRegn_type())) {

            sql = "UPDATE " + TableList.VA_DETAILS
                    + " SET regn_no=?"
                    + " WHERE appl_no=? and  regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNoDescr);
            ps.setString(2, dobj.getAppl_no());
            ps.setString(3, dobj.getRegn_no());
            ps.executeUpdate();

            sql = "UPDATE " + TableList.VA_OWNER
                    + " SET regn_no=?,op_dt = current_timestamp "
                    + " WHERE appl_no=? and  regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNoDescr);
            ps.setString(2, dobj.getAppl_no());
            ps.setString(3, dobj.getRegn_no());
            ps.executeUpdate();

            sql = "UPDATE " + TableList.VA_OWNER_IDENTIFICATION
                    + " SET regn_no=?,op_dt = current_timestamp "
                    + " WHERE appl_no=? and  regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNoDescr);
            ps.setString(2, dobj.getAppl_no());
            ps.setString(3, dobj.getRegn_no());
            ps.executeUpdate();

            sql = "UPDATE " + TableList.VA_HPA
                    + " SET regn_no=?,op_dt = current_timestamp "
                    + " WHERE appl_no=? and  regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNoDescr);
            ps.setString(2, dobj.getAppl_no());
            ps.setString(3, dobj.getRegn_no());
            ps.executeUpdate();

            sql = "UPDATE " + TableList.VA_INSURANCE
                    + " SET regn_no=?,op_dt = current_timestamp"
                    + " WHERE appl_no=? and  regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNoDescr);
            ps.setString(2, dobj.getAppl_no());
            ps.setString(3, dobj.getRegn_no());
            ps.executeUpdate();

            sql = "UPDATE " + TableList.VA_TRAILER
                    + " SET regn_no=?,op_dt = current_timestamp "
                    + " WHERE appl_no=? and  regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNoDescr);
            ps.setString(2, dobj.getAppl_no());
            ps.setString(3, dobj.getRegn_no());
            ps.executeUpdate();


            //for handling cancellation of scrapped Vehicle against new registration --Start--
            sql = "UPDATE " + TableList.VT_SCRAP_VEHICLE
                    + " SET new_regn_no=?,new_chasi_no=?"
                    + " WHERE regn_appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setNull(1, java.sql.Types.VARCHAR);
            ps.setNull(2, java.sql.Types.VARCHAR);
            ps.setString(3, dobj.getAppl_no());
            ps.executeUpdate();
            //for handling cancellation of scrapped Vehicle against new registration --End----



            if (dobj.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && !ownerDetailsDobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                sql = "UPDATE " + TableList.VA_FITNESS
                        + " SET regn_no=?,op_dt = current_timestamp "
                        + " WHERE appl_no=? and  regn_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regnNoDescr);
                ps.setString(2, dobj.getAppl_no());
                ps.setString(3, dobj.getRegn_no());
                ps.executeUpdate();


                OwnerImpl ownerImpl = new OwnerImpl();
                Owner_dobj owner_dobj = ownerImpl.getOwnerDobj(ownerDetailsDobj);
                VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(owner_dobj);
                int seriesId = getSeriesId(vehParameters);
                String allotedNo = ServerUtil.getRegnNoAllotedDetail(dobj.getAppl_no(), owner_dobj.getState_cd(), owner_dobj.getOff_cd());
                if (allotedNo != null && allotedNo.trim().length() > 0) {
                    sql = "INSERT INTO " + TableList.VM_REGN_AVAILABLE
                            + " (state_cd, off_cd, regn_no, status, amount, entered_by, entered_on, prefix_series, series_id)"
                            + " VALUES (?, ?, ?, ?, ?, ?, current_timestamp, ?, ?)";
                    int suffixSize = 4;
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, dobj.getState_cd());
                    ps.setInt(2, dobj.getOff_cd());
                    ps.setString(3, dobj.getRegn_no());
                    ps.setString(4, "A");
                    ps.setInt(5, 0);
                    ps.setString(6, empCode);
                    ps.setString(7, dobj.getRegn_no().substring(0, (dobj.getRegn_no().length() - suffixSize)));
                    ps.setInt(8, seriesId);
                    ps.executeUpdate();

                    sql = "DELETE FROM " + TableList.VM_REGN_ALLOTED
                            + " WHERE REGN_NO=? AND APPL_NO=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, dobj.getRegn_no());
                    ps.setString(2, dobj.getAppl_no());
                    ps.executeUpdate();
                }
                new HsrpEntryImpl().insertIntoVH_HSRP(dobj.getRegn_no(), dobj.getState_cd(), dobj.getOff_cd(), tmgr, empCode, "Cancel Receipt");
                ServerUtil.deleteFromTable(tmgr, dobj.getRegn_no(), null, TableList.VT_HSRP);
            }

        }
    }

    public int getSeriesId(VehicleParameters vehParameters) throws VahanException {
        int seriesId = 0;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        Exception e = null;
        String sql = null;
        try {
            tmgr = new TransactionManager("getSeriesId");
            sql = "SELECT * FROM " + TableList.VM_REGN_SERIES
                    + " WHERE state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, vehParameters.getSTATE_CD());
            ps.setInt(2, vehParameters.getOFF_CD());
            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("criteria_formula"), vehParameters), "getSeriesId")) {
                    seriesId = rs.getInt("series_id");
                    break;
                }
            }

        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            e = sqle;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            e = ex;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

        if (e != null) {
            throw new VahanException("Error in Getting Series ID for Prefix of the Registration No");
        }

        if (seriesId == 0) {
            throw new VahanException("Series ID Can not be Zero");
        }

        return seriesId;
    }

    public int getSeriesIdFromVmSeries(VehicleParameters vehParameters) throws VahanException {
        int seriesId = 0;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        Exception e = null;
        String sql = null;
        try {
            tmgr = new TransactionManager("getSeriesId");
            sql = "SELECT * FROM " + TableList.VM_SERIES
                    + " WHERE state_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, vehParameters.getSTATE_CD());
            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("criteria_formula"), vehParameters), "getSeriesId")) {
                    seriesId = rs.getInt("series_id");
                    break;
                }
            }

        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            e = sqle;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            e = ex;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

        if (e != null) {
            throw new VahanException("Error in Getting Series ID for Prefix of the Registration No");
        }

        if (seriesId == 0) {
            throw new VahanException("Series ID Can not be Zero");
        }

        return seriesId;
    }

    public int getPurCd(String applNo, String state_cd, int off_cd) throws SQLException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        int purCd = 0;

        if (applNo != null) {
            applNo = applNo.toUpperCase().trim();
        }

        try {
            tmgr = new TransactionManager("getPurCd");

            sql = "SELECT distinct pur_cd from " + TableList.VA_DETAILS
                    + " WHERE appl_no=? and state_cd=? and off_cd =? and pur_cd in (1,18,122,123,124) order by pur_cd limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next())//found
            {
                purCd = rs.getInt("pur_cd");
            }

        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return purCd;
    }

    public int getPurCdFromVaDetails(String applNo, String state_cd, int off_cd) throws SQLException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        int purCd = 0;
        if (applNo != null) {
            applNo = applNo.toUpperCase().trim();
        }
        try {
            tmgr = new TransactionManager("getPurCdFromVaDetails");
            sql = "SELECT  pur_cd from " + TableList.VA_DETAILS
                    + " WHERE appl_no=? and state_cd=? and off_cd =? order by appl_dt desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next())//found
            {
                purCd = rs.getInt("pur_cd");
            }
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return purCd;
    }

    public AdvanceRegnNo_dobj getAdvanceRegnNoDobj(String rcpt_no, String state_cd, int off_cd) throws Exception {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        AdvanceRegnNo_dobj advanceRegnNoDobj = null;
        int counter = 0;

        if (rcpt_no != null) {
            rcpt_no = rcpt_no.toUpperCase().trim();
        }

        try {
            tmgr = new TransactionManager("getAdvanceRegnNoDobj");

            sql = "SELECT state_cd, off_cd, recp_no, regn_appl_no, regn_no, owner_name,"
                    + " f_name, c_add1, c_add2, c_add3, c_district, c_pincode, c_state,"
                    + " mobile_no, total_amt FROM " + TableList.VT_ADVANCE_REGN_NO
                    + " WHERE state_cd=? and off_cd=? and recp_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, rcpt_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next())//found
            {
                advanceRegnNoDobj = new AdvanceRegnNo_dobj();
                advanceRegnNoDobj.setState_cd(rs.getString("state_cd"));
                advanceRegnNoDobj.setOff_cd(rs.getInt("off_cd"));
                advanceRegnNoDobj.setRecp_no(rs.getString("recp_no"));
                advanceRegnNoDobj.setRegn_appl_no(rs.getString("regn_appl_no"));
                advanceRegnNoDobj.setRegn_no(rs.getString("regn_no"));
                advanceRegnNoDobj.setOwner_name(rs.getString("owner_name"));
                advanceRegnNoDobj.setF_name(rs.getString("f_name"));
                advanceRegnNoDobj.setC_add1(rs.getString("c_add1"));
                advanceRegnNoDobj.setC_add2(rs.getString("c_add2"));
                advanceRegnNoDobj.setC_add3(rs.getString("c_add3"));
                advanceRegnNoDobj.setC_district(rs.getInt("c_district"));
                advanceRegnNoDobj.setC_pincode(rs.getInt("c_pincode"));
                advanceRegnNoDobj.setMobile_no(rs.getLong("mobile_no"));
                advanceRegnNoDobj.setTotal_amt(rs.getLong("total_amt"));

                counter++;
                if (counter > 1) {
                    throw new VahanException("Multiple Record Found for Receipt No " + advanceRegnNoDobj.getRecp_no());
                }
            }

        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return advanceRegnNoDobj;
    }

    public boolean getFitnessApprovalStatus(String applNo, int actionCd, String state_cd, int off_cd) throws Exception {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        boolean fitnessApprovalStatus = false;

        try {
            tmgr = new TransactionManager("getFitnessApprovalStatus");

            sql = "SELECT * FROM " + TableList.VHA_STATUS
                    + " WHERE appl_no=? and action_cd=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setInt(2, actionCd);
            ps.setString(3, state_cd);
            ps.setInt(4, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next())//found
            {
                fitnessApprovalStatus = true;
            }

        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return fitnessApprovalStatus;
    }

    public boolean isBalanceFeeTaxCollectionReceipt(String applNo, String rcptNo, String stateCd, int offCd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        RowSet rs = null;
        boolean isBlanceFeeTaxRcpt = false;
        try {
            tmgr = new TransactionManager("isBalanceFeeTaxCollectionReceipt");
            sql = "select appl_no,remarks from " + TableList.VP_APPL_RCPT_MAPPING
                    + " WHERE rcpt_no=? and state_cd=? and off_cd=? and upper(substring(remarks,1,7))='BALANCE'";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, rcptNo);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isBlanceFeeTaxRcpt = true;
            }

            /* sql = "SELECT appl_no FROM " + TableList.VA_DETAILS
             + " WHERE appl_no=? and (case when pur_cd = 123 then 1 when pur_cd=124 then 18 else pur_cd end) in (SELECT case when pur_cd=202 then 2 else pur_cd end FROM " + TableList.VT_FEE
             + " WHERE rcpt_no=? and state_cd=? and off_cd=?)";
             ps = tmgr.prepareStatement(sql);
             ps.setString(1, applNo);
             ps.setString(2, rcptNo);
             ps.setString(3, stateCd);
             ps.setInt(4, offCd);
             rs = tmgr.fetchDetachedRowSet_No_release();
             if (!rs.next()) {
             isBlanceFeeTaxRcpt = true;
             //for handling fitness fee cancel which is with new registration start
             sql = " SELECT a.appl_no from " + TableList.VHA_STATUS + " a left join " + TableList.VT_FEE + " b "
             + " on a.state_cd=b.state_cd and a.off_cd=b.off_cd and b.rcpt_no=? and b.pur_cd=2 and rcpt_dt=moved_on "
             + " where a.appl_no = ? and a.action_cd in (" + TableConstants.TM_ROLE_NEW_REGISTRATION_FIT_FEE + "," + TableConstants.TM_ROLE_DEALER_NEW_REGISTRATION_FIT_FEE + ")"
             + " and a.pur_cd in (" + TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE + "," + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE + ") "
             + " and a.status='C' and a.state_cd=? and a.off_cd=? order by a.moved_on desc limit 1";
             ps = tmgr.prepareStatement(sql);
             ps.setString(1, rcptNo);
             ps.setString(2, applNo);
             ps.setString(3, stateCd);
             ps.setInt(4, offCd);
             rs = tmgr.fetchDetachedRowSet();
             if (rs.next()) {
             isBlanceFeeTaxRcpt = false;
             }//for handling fitness fee cancel which is with new registration end
             }*/
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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
        return isBlanceFeeTaxRcpt;
    }
    //  Get user mobile_no and Email_id for OTP

    public String getUserDetails(String userCd) throws Exception {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        RowSet rs = null;
        String userData = "";
        try {
            tmgr = new TransactionManagerReadOnly("getUserMObileNo");
            sql = "SELECT mobile_no,email_id from " + TableList.TM_USER_INFO + " where user_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(userCd));
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                userData = rs.getString("mobile_no") + "," + rs.getString("email_id");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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
        return userData;
    }

    public boolean isRegnNoForOtherStateVehicles(TransactionManager tmgr, String applNo, String regnType) throws VahanException {
        boolean isGenerate = true;
        if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)) {
            isGenerate = false;
            RowSet rs = null;
            String sql = "select * from " + TableList.VM_REGN_ALLOTED + " where appl_no =? ";
            try {
                PreparedStatement ps = tmgr.prepareStatement(sql);
                ps.setString(1, applNo);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    isGenerate = true;
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                throw new VahanException("Other State Vehicle Registration No Generation Problem.");
            }
        }
        return isGenerate;

    }

    public static void updateReceiptStatus(TransactionManager tmgr, String appl_no) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "UPDATE  " + TableList.VT_MANUAL_RECEIPT + " SET rcpt_used=false WHERE state_cd= ? and off_cd= ? and transaction_appl_no=?";
            int l = 1;
            ps = tmgr.prepareStatement(sql);
            ps.setString(l++, Util.getUserStateCode());
            ps.setInt(l++, Util.getUserOffCode());
            ps.setString(l++, appl_no);
            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some Error occurred while fetching the Record");
        }
    }
}