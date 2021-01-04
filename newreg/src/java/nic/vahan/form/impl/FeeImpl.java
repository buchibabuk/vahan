/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.BatchUpdateException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.fillVehicleParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.DocumentType;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.eapplication.OnlinePaymentImpl;
import nic.vahan.form.bean.RoadTaxCollectionBean;
import nic.vahan.form.bean.TaxFormPanelBean;
import nic.vahan.form.dobj.DOTaxDetail;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.FeeDobj;
import nic.vahan.form.dobj.Fee_Pay_Dobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.PaymentCollectionDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.Tax_Pay_Dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.ToDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.NewVehicleNo;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.sendSMS;
import org.apache.log4j.Logger;
import nic.vahan.form.dobj.AltDobj;
import nic.vahan.form.dobj.ConvDobj;
import nic.vahan.form.dobj.EpayDobj;
import nic.vahan.form.dobj.FeeFineExemptionDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.LifeTimeTaxUpdateDobj;
import nic.vahan.form.dobj.ManualReceiptEntryDobj;
import nic.vahan.form.dobj.NocDobj;
import nic.vahan.form.dobj.OnlinePayDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.TaxInstallCollectionDobj;
import static nic.vahan.form.impl.EpayImpl.getToTaxAmt;
import nic.vahan.form.impl.dealer.PaymentGatewayImpl;
import nic.vahan.form.dobj.common.UserIDAndPasswordForOnlinePaymentDobj;

/**
 * Fee_Impl is the Implementation class for all Fee related tasks. This class
 * handles fee related tasks for both registered vehicle and new un registered
 * vehicles.
 *
 * @author Prashant Kumar Singh
 */
public class FeeImpl {

    private static Logger LOGGER = Logger.getLogger(FeeImpl.class);

    public static class PaymentGenInfo {

        private long cashAmt = 0;
        private long draftAmt = 0;
        private long totalAmt = 0;
        private long excessAmt = 0;

        /**
         * @return the cashAmt
         */
        public long getCashAmt() {
            return cashAmt;
        }

        /**
         * @param cashAmt the cashAmt to set
         */
        public void setCashAmt(long cashAmt) {
            this.cashAmt = cashAmt;
        }

        /**
         * @return the draftAmt
         */
        public long getDraftAmt() {
            return draftAmt;
        }

        /**
         * @param draftAmt the draftAmt to set
         */
        public void setDraftAmt(long draftAmt) {
            this.draftAmt = draftAmt;
        }

        /**
         * @return the totalAmt
         */
        public long getTotalAmt() {
            return totalAmt;
        }

        /**
         * @param totalAmt the totalAmt to set
         */
        public void setTotalAmt(long totalAmt) {
            this.totalAmt = totalAmt;
        }

        /**
         * @return the excessAmt
         */
        public long getExcessAmt() {
            return excessAmt;
        }

        /**
         * @param excessAmt the excessAmt to set
         */
        public void setExcessAmt(long excessAmt) {
            this.excessAmt = excessAmt;
        }
    }

    public boolean verifySmartCard() {
        boolean isSmartCard = false;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("verifySmartCard");
            isSmartCard = ServerUtil.verifyForSmartCard(Util.getUserStateCode(), Util.getUserOffCode(), tmgr);
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

        return isSmartCard;
    }

    public boolean isHypothecated(String parameter, int flag) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        VahanException vahanexecption = null;
        String whereIam = "Fee_Impl.isHypothecated";
        String vaHpaSQL = null;
        try {
            tmgr = new TransactionManager(whereIam);

            if (flag == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || flag == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || flag == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE_FITNESS) {
                vaHpaSQL = "SELECT appl_no, regn_no, sr_no, hp_type, fncr_name, fncr_add1, fncr_add2, "
                        + "       fncr_add3, fncr_district, fncr_pincode, fncr_state, from_dt, "
                        + "       op_dt FROM "
                        + TableList.VA_HPA
                        + " where appl_no =?";
                ps = tmgr.prepareStatement(vaHpaSQL);
                ps.setString(1, parameter);
            } else if (flag == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || flag == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                vaHpaSQL = "SELECT  * FROM  "
                        + TableList.VA_HPA
                        + " where appl_no =?";
                ps = tmgr.prepareStatement(vaHpaSQL);
                ps.setString(1, parameter);
//            } else {
//                vaHpaSQL = "SELECT * FROM " + TableList.VT_HYPTH
//                        + " where regn_no =? and state_cd = ? and off_cd = ?";
//                ps = tmgr.prepareStatement(vaHpaSQL);
//                ps.setString(1, parameter);
//                ps.setString(2, Util.getUserStateCode());
//                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            }

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanexecption = new VahanException("Error in fetching details for [ " + parameter + "]");
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

    }

    public Object[] getUserIDAndPassword(String appl_no) throws VahanException, Exception {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        Object[] obj = null;
        try {
            tmgr = new TransactionManager("inside getTotalMsgCount method in FeeImpl");
            String sql = "select distinct vpin.user_cd,vpin.user_pwd from " + TableList.VP_RCPT_CART + " as vpr, " + TableList.VP_ONLINE_PAY_USER_INFO + " as vpin where vpr.user_cd=vpin.user_cd and vpr.state_cd=vpin.state_cd and vpr.appl_no =? and vpr.state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                obj = new Object[2];
                obj[0] = rs.getString("user_pwd");
                obj[1] = rs.getLong("user_cd");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting Details. Please contact Administrator!!!");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return obj;
    }

    public boolean getOnlinePaymentCancel(String appl_no, long user_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        boolean flag = false;
        String transactionNo = null;
        try {
            if (!CommonUtils.isNullOrBlank(appl_no)) {
                tmgr = new TransactionManager("inside getOnlinePaymentCancel method in FeeImpl");
                String sql = "select distinct vpr.transaction_no from " + TableList.VP_RCPT_CART + " as vpr, " + TableList.VP_ONLINE_PAY_USER_INFO + " as vpin where vpr.user_cd=vpin.user_cd and vpr.state_cd=vpin.state_cd and vpr.appl_no =?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_no);
                RowSet rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    transactionNo = rs.getString("transaction_no");
                }
                if (CommonUtils.isNullOrBlank(transactionNo)) {
                    OnlinePaymentImpl onl = new OnlinePaymentImpl();
                    flag = onl.getPaymentRevertBack(user_cd + "", appl_no, null);
                }
            } else {
                throw new VahanException("Invalid Application Number.");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong during fetching details of Online Payment Cancel");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong during fetching details of Online Payment Cancel");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public String saveOnlinePaymentData(Fee_Pay_Dobj feeDobj, long checkTotalAmount, String applNo, Long mobileNo, String stateCd, int offCd, String empCode, String payType, String selectedOption, OwnerDetailsDobj ownerDobj, boolean isManualRcptAttach, boolean isFeeFineExemptionAllow) throws VahanException {
        TransactionManager tmgr = null;
        boolean success = false;
        String userInfo = null;
        try {
            tmgr = new TransactionManager("saveOnlineCashDetails");
            long userCd = ServerUtil.getUniqueUserCd(tmgr, stateCd);
            if (userCd != 0) {
                String strSQL = "SELECT USER_CD FROM " + TableList.VP_RCPT_CART + " WHERE user_cd= ? ";
                PreparedStatement psmt = tmgr.prepareStatement(strSQL);
                psmt.setLong(1, userCd);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    throw new VahanException("Problem occured during generating online info.");
                }
            }
            StringBuffer userName = new StringBuffer(String.valueOf(userCd).substring(0, 4).toLowerCase());
            StringBuffer userPwd = userName.append(UUID.randomUUID().toString().substring(0, 5));

            if (selectedOption != null && (selectedOption.equalsIgnoreCase("R") || selectedOption.equalsIgnoreCase("O") || selectedOption.equalsIgnoreCase("C") || selectedOption.equalsIgnoreCase("I") || selectedOption.equalsIgnoreCase(TableConstants.ONLINE_FANCY))) {
                applNo = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
            }

            String userPassSql = " INSERT INTO " + TableList.VP_ONLINE_PAY_USER_INFO + "(state_cd, off_cd, user_cd, user_pwd, mobile_no, created_by,created_dt,appl_no) \n"
                    + " VALUES (?, ?, ?, ?, ?, ?, current_timestamp,?) ";

            PreparedStatement psUserPass = tmgr.prepareStatement(userPassSql);
            int m = 1;
            psUserPass.setString(m++, stateCd);
            psUserPass.setInt(m++, offCd);
            psUserPass.setLong(m++, userCd);
            psUserPass.setString(m++, userPwd.toString());
            psUserPass.setLong(m++, mobileNo);
            psUserPass.setLong(m++, Long.parseLong(empCode));
            psUserPass.setString(m++, applNo);
            psUserPass.executeUpdate();
            if (!CommonUtils.isNullOrBlank(payType) && (TableConstants.TAX_MODE_BALANCE.equals(payType) || TableConstants.ONLINE_TAX.equals(payType) || TableConstants.TAX_INSTALLMENT.equals(payType) || TableConstants.ONLINE_FANCY.equals(payType) || TableConstants.ONLINE_AUDIT.equals(payType))) {
                String userBalanceSql = " INSERT INTO " + TableList.VP_ONLINE_PAY_BALANCE_FEE_DETAILS + "(state_cd, off_cd, user_cd, appl_no, regn_no, owner_name, selected_option,chasi_no, \n"
                        + " op_dt,form_type,vh_class) VALUES (?, ?, ?, ?, ?, ?, ?, ?, current_timestamp,?,?);";
                PreparedStatement psUserBalance = tmgr.prepareStatement(userBalanceSql);
                m = 1;
                psUserBalance.setString(m++, stateCd);
                psUserBalance.setInt(m++, offCd);
                psUserBalance.setLong(m++, userCd);
                psUserBalance.setString(m++, applNo);
                psUserBalance.setString(m++, ownerDobj.getRegn_no() == null ? "" : ownerDobj.getRegn_no());
                psUserBalance.setString(m++, ownerDobj.getOwner_name());
                psUserBalance.setString(m++, selectedOption);
                psUserBalance.setString(m++, ownerDobj.getChasi_no() == null ? "" : ownerDobj.getChasi_no());
                if (TableConstants.ONLINE_TAX.equals(payType)) {
                    psUserBalance.setString(m++, TableConstants.ONLINE_TAX);
                } else if (TableConstants.TAX_INSTALLMENT.equals(payType)) {
                    psUserBalance.setString(m++, TableConstants.TAX_INSTALLMENT);
                } else if (TableConstants.ONLINE_FANCY.equals(payType)) {
                    psUserBalance.setString(m++, TableConstants.ONLINE_FANCY);
                } else if (TableConstants.ONLINE_AUDIT.equals(payType)) {
                    psUserBalance.setString(m++, TableConstants.ONLINE_AUDIT);
                } else {
                    psUserBalance.setString(m++, TableConstants.TAX_MODE_BALANCE);
                }
                psUserBalance.setInt(m++, ownerDobj.getVh_class());
                ServerUtil.validateQueryResult(tmgr, psUserBalance.executeUpdate(), psUserBalance);

                for (TaxInstallCollectionDobj object : feeDobj.getTaxInstallDobjList()) {
                    String installSQL = "INSERT INTO " + TableList.VA_TAX_INSTALLMENT_BRKUP + "(\n"
                            + "	state_cd, off_cd, appl_no, regn_no, serial_no, tax_amt_instl, pay_due_date, user_cd, op_dt)\n"
                            + "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP);";
                    PreparedStatement psInstall = tmgr.prepareStatement(installSQL);
                    m = 1;
                    psInstall.setString(m++, stateCd);
                    psInstall.setInt(m++, offCd);
                    psInstall.setString(m++, applNo);
                    psInstall.setString(m++, ownerDobj.getRegn_no());
                    psInstall.setString(m++, object.getSerialno());
                    psInstall.setLong(m++, object.getTaxAmountInst() + object.getPenalty());
                    psInstall.setTimestamp(m++, new java.sql.Timestamp(DateUtils.parseDate(object.getPaydueDate()).getTime()));
                    psInstall.setLong(m++, userCd);
                    ServerUtil.validateQueryResult(tmgr, psInstall.executeUpdate(), psInstall);
                }
            }
            if (isManualRcptAttach) {
                ManualReceiptEntryImpl.updateVTManualReceiptEntryStatus(tmgr, applNo, true);
            }
            this.insertIntoVpRcptCart(tmgr, feeDobj, applNo, userCd, stateCd, offCd, payType, isManualRcptAttach, isFeeFineExemptionAllow);
            if (isFeeFineExemptionAllow) {
                this.insertIntoVpRcptCartTemp(tmgr, feeDobj, applNo, stateCd, offCd, userCd);
            }

            long amount = this.getTotalAmtForOnlinePayment(tmgr, applNo, userCd, stateCd, offCd);
            if (selectedOption != null && selectedOption.equalsIgnoreCase(TableConstants.ONLINE_FANCY)) {
                for (FeeDobj feePurDobj : feeDobj.getCollectedFeeList()) {
                    if (feePurDobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_CHOICE_NO) {

                        String fancySql = "INSERT INTO " + TableList.VP_ADVANCE_REGN_NO + "(state_cd, off_cd,user_cd, pay_appl_no, regn_appl_no, regn_no, owner_name,f_name, c_add1, c_add2, c_district, c_pincode, c_state,mobile_no,total_amt)VALUES (?, ?,?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?,?, ?)";

                        PreparedStatement psFancy = tmgr.prepareStatement(fancySql);
                        psFancy.setString(1, stateCd);
                        psFancy.setInt(2, offCd);
                        psFancy.setLong(3, userCd);
                        psFancy.setString(4, applNo);
                        if (ownerDobj.getApplNo() != null && !ownerDobj.getApplNo().equalsIgnoreCase("")) {
                            psFancy.setString(5, ownerDobj.getApplNo().toUpperCase());
                        } else {
                            psFancy.setNull(5, java.sql.Types.VARCHAR);
                        }
                        psFancy.setString(6, ownerDobj.getRegn_no().toUpperCase());
                        psFancy.setString(7, ownerDobj.getOwner_name().toUpperCase());
                        psFancy.setString(8, ownerDobj.getF_name().toUpperCase());
                        psFancy.setString(9, ownerDobj.getC_add1().toUpperCase());
                        psFancy.setString(10, ownerDobj.getC_add2().toUpperCase());
                        psFancy.setInt(11, ownerDobj.getC_district());
                        psFancy.setInt(12, ownerDobj.getC_pincode());
                        psFancy.setString(13, stateCd);
                        psFancy.setLong(14, ownerDobj.getOwnerIdentity().getMobile_no());
                        psFancy.setLong(15, feePurDobj.getFeeAmount());
                        psFancy.executeUpdate();
                    }
                }
            }
            if (amount == checkTotalAmount) {
                String mobileSms = "Please visit https://vahan.parivahan.gov.in/vahan/vahan/ui/eapplication/form_payment.xhtml . User ID :" + applNo + " and Password: " + userPwd + " is valid for one day only.";
                ServerUtil.sendSMS(String.valueOf(mobileNo), mobileSms);
                tmgr.commit();
                success = true;
                userInfo = userPwd.toString();
            } else {
                throw new VahanException("Problem in Saving Details!");
            }

            if (selectedOption != null && (selectedOption.equalsIgnoreCase("R") || selectedOption.equalsIgnoreCase("O") || selectedOption.equalsIgnoreCase("C") || selectedOption.equalsIgnoreCase("I") || selectedOption.equalsIgnoreCase(TableConstants.ONLINE_FANCY))) {
                userInfo = applNo + "," + userInfo;
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in saving FeeDetails !!!");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return userInfo;
    }

    public void insertIntoVpRcptCartTemp(TransactionManager tmgr, Fee_Pay_Dobj feeDobj, String applNo, String stateCd, int offCd, long userCd) throws VahanException {
        PassengerPermitDetailDobj permitDobj = null;
        StringBuilder taxNoAdvUnits = null;
        PreparedStatement ps = null;
        try {
            permitDobj = feeDobj.getPermitDobj();
            for (FeeDobj feePurDobj : feeDobj.getCollectedFeeList()) {
                String sql = "SELECT * FROM " + TableList.VP_RCPT_CART_TEMP + " WHERE appl_no=? and pur_cd=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, applNo);
                ps.setInt(2, feePurDobj.getPurCd());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    throw new VahanException("Duplicate records found !!!");
                }
                if (feePurDobj.getPurCd() != -1) {
                    String addTocartSql = "INSERT INTO " + TableList.VP_RCPT_CART_TEMP + "(state_cd,off_cd,user_cd, appl_no, pur_cd, period_mode, period_from, period_upto,"
                            + " amount, exempted, rebate, surcharge, penalty, interest,pmt_type, pmt_catg, service_type, route_class,"
                            + " route_length, no_of_trips, domain_cd, distance_run_in_quarter,op_dt,no_adv_units,tax1,tax2,prv_adjustment) "
                            + " VALUES (?,?,?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,current_timestamp,?,?,?,?)";

                    PreparedStatement psAddtoCart = tmgr.prepareStatement(addTocartSql);
                    int i = 1;
                    psAddtoCart.setString(i++, stateCd);
                    if (offCd > 0) {
                        psAddtoCart.setInt(i++, offCd);
                    } else {
                        throw new VahanException("Problem occurred during adding into cart, Please go to home page and try again.");
                    }
                    psAddtoCart.setLong(i++, userCd);
                    psAddtoCart.setString(i++, applNo);
                    psAddtoCart.setInt(i++, feePurDobj.getPurCd());
                    psAddtoCart.setString(i++, null);
                    psAddtoCart.setDate(i++, null);
                    psAddtoCart.setDate(i++, null);
                    psAddtoCart.setLong(i++, feePurDobj.getFeeAmount());
                    psAddtoCart.setLong(i++, 0);
                    psAddtoCart.setLong(i++, 0);
                    psAddtoCart.setLong(i++, 0);
                    psAddtoCart.setLong(i++, feePurDobj.getFineAmount());//fine
                    psAddtoCart.setLong(i++, 0);//fine

                    if (permitDobj != null && permitDobj.getPmt_type_code() != null && !permitDobj.getPmt_type_code().equals("")) {
                        psAddtoCart.setInt(i++, Integer.parseInt(permitDobj.getPmt_type_code()));
                    } else {
                        psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                    }

                    if (permitDobj != null && permitDobj.getPmtCatg() != null && !permitDobj.getPmtCatg().equals("")) {
                        psAddtoCart.setInt(i++, Integer.parseInt(permitDobj.getPmtCatg()));
                    } else {
                        psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                    }

                    if (permitDobj != null && permitDobj.getServices_TYPE() != null && !permitDobj.getServices_TYPE().equals("")) {
                        psAddtoCart.setInt(i++, Integer.parseInt(permitDobj.getServices_TYPE()));
                    } else {
                        psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                    }

                    if (permitDobj != null && permitDobj.getRout_code() != null && !permitDobj.getRout_code().equals("")) {
                        psAddtoCart.setInt(i++, Integer.parseInt(permitDobj.getRout_code()));
                    } else {
                        psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                    }

                    if (permitDobj != null && permitDobj.getRout_length() != null && !permitDobj.getRout_length().equals("")) {
                        psAddtoCart.setInt(i++, Integer.parseInt(permitDobj.getRout_length()));
                    } else {
                        psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                    }

                    if (permitDobj != null && permitDobj.getNumberOfTrips() != null && !permitDobj.getNumberOfTrips().equals("")) {
                        psAddtoCart.setInt(i++, Integer.parseInt(permitDobj.getNumberOfTrips()));
                    } else {
                        psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                    }

                    if (permitDobj != null && permitDobj.getDomain_CODE() != null && !permitDobj.getDomain_CODE().equals("")) {
                        psAddtoCart.setInt(i++, Integer.parseInt(permitDobj.getDomain_CODE()));
                    } else {
                        psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                    }

                    psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                    psAddtoCart.setString(i++, null);
                    psAddtoCart.setLong(i++, 0);
                    psAddtoCart.setLong(i++, 0);
                    psAddtoCart.setLong(i++, 0);
                    ServerUtil.validateQueryResult(tmgr, psAddtoCart.executeUpdate(), psAddtoCart);
                }
            }

            if (feeDobj.getListTaxDobj() != null) {
                for (Tax_Pay_Dobj taxDobj : feeDobj.getListTaxDobj()) {
                    if (taxDobj.getPur_cd() != -1) {

                        String addTocartSql = "INSERT INTO " + TableList.VP_RCPT_CART_TEMP + "(state_cd,off_cd,user_cd, appl_no, pur_cd, period_mode, period_from, period_upto,"
                                + " amount, exempted, rebate, surcharge, penalty, interest,pmt_type, pmt_catg, service_type, route_class,"
                                + " route_length, no_of_trips, domain_cd, distance_run_in_quarter,op_dt,no_adv_units,tax1,tax2,prv_adjustment) "
                                + " VALUES (?,?,?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,current_timestamp,?,?,?,?)";

                        PreparedStatement pstmtVtTax = tmgr.prepareStatement(addTocartSql);
                        int i = 1;
                        pstmtVtTax.setString(i++, stateCd);
                        pstmtVtTax.setInt(i++, offCd);
                        pstmtVtTax.setLong(i++, userCd);
                        pstmtVtTax.setString(i++, applNo);
                        pstmtVtTax.setInt(i++, taxDobj.getPur_cd());
                        pstmtVtTax.setString(i++, taxDobj.getTaxMode());
                        if (taxDobj.getFinalTaxFrom() != null) {
                            pstmtVtTax.setDate(i++, new java.sql.Date(DateUtils.parseDate(taxDobj.getFinalTaxFrom()).getTime()));
                        } else {
                            pstmtVtTax.setNull(i++, java.sql.Types.DATE);//tax_from
                        }
                        if (taxDobj.getFinalTaxUpto() != null) {
                            pstmtVtTax.setDate(i++, new java.sql.Date(DateUtils.parseDate(taxDobj.getFinalTaxUpto()).getTime()));
                        } else {
                            pstmtVtTax.setNull(i++, java.sql.Types.DATE);//tax_upto
                        }
                        pstmtVtTax.setLong(i++, taxDobj.getTotalPaybaleTax());
                        pstmtVtTax.setLong(i++, 0);
                        pstmtVtTax.setLong(i++, taxDobj.getTotalPaybaleRebate());
                        pstmtVtTax.setLong(i++, taxDobj.getTotalPaybaleSurcharge());

                        pstmtVtTax.setLong(i++, taxDobj.getTotalPaybalePenalty());
                        pstmtVtTax.setLong(i++, taxDobj.getTotalPaybaleInterest());

                        if (permitDobj != null && permitDobj.getPmt_type_code() != null && !permitDobj.getPmt_type_code().equals("")) {
                            pstmtVtTax.setInt(i++, Integer.parseInt(permitDobj.getPmt_type_code()));
                        } else {
                            pstmtVtTax.setNull(i++, java.sql.Types.INTEGER);
                        }

                        if (permitDobj != null && permitDobj.getPmtCatg() != null && !permitDobj.getPmtCatg().equals("")) {
                            pstmtVtTax.setInt(i++, Integer.parseInt(permitDobj.getPmtCatg()));
                        } else {
                            pstmtVtTax.setNull(i++, java.sql.Types.INTEGER);
                        }

                        if (permitDobj != null && permitDobj.getServices_TYPE() != null && !permitDobj.getServices_TYPE().equals("")) {
                            pstmtVtTax.setInt(i++, Integer.parseInt(permitDobj.getServices_TYPE()));
                        } else {
                            pstmtVtTax.setNull(i++, java.sql.Types.INTEGER);
                        }

                        if (permitDobj != null && permitDobj.getRout_code() != null && !permitDobj.getRout_code().equals("")) {
                            pstmtVtTax.setInt(i++, Integer.parseInt(permitDobj.getRout_code()));
                        } else {
                            pstmtVtTax.setNull(i++, java.sql.Types.INTEGER);
                        }

                        if (permitDobj != null && permitDobj.getRout_length() != null && !permitDobj.getRout_length().equals("")) {
                            pstmtVtTax.setInt(i++, Integer.parseInt(permitDobj.getRout_length()));
                        } else {
                            pstmtVtTax.setNull(i++, java.sql.Types.INTEGER);
                        }

                        if (permitDobj != null && permitDobj.getNumberOfTrips() != null && !permitDobj.getNumberOfTrips().equals("")) {
                            pstmtVtTax.setInt(i++, Integer.parseInt(permitDobj.getNumberOfTrips()));
                        } else {
                            pstmtVtTax.setNull(i++, java.sql.Types.INTEGER);
                        }

                        if (permitDobj != null && permitDobj.getDomain_CODE() != null && !permitDobj.getDomain_CODE().equals("")) {
                            pstmtVtTax.setInt(i++, Integer.parseInt(permitDobj.getDomain_CODE()));
                        } else {
                            pstmtVtTax.setNull(i++, java.sql.Types.INTEGER);
                        }
                        pstmtVtTax.setNull(i++, java.sql.Types.INTEGER);//ps.setInt(i++, permitDobj != null && permitDobj.getDistance_run_in_quarter());
                        pstmtVtTax.setString(i++, null);
                        pstmtVtTax.setLong(i++, (long) taxDobj.getTotalPaybaleTax1());
                        pstmtVtTax.setLong(i++, (long) taxDobj.getTotalPaybaleTax2());
                        pstmtVtTax.setLong(i++, (long) taxDobj.getPreviousAdj());
                        ServerUtil.validateQueryResult(tmgr, pstmtVtTax.executeUpdate(), pstmtVtTax);

                        if (taxNoAdvUnits == null) {
                            taxNoAdvUnits = new StringBuilder();
                            taxNoAdvUnits.append(taxDobj.getPur_cd()).append("=").append(taxDobj.getNoOfAdvUnits());
                        } else {
                            taxNoAdvUnits.append(",").append(taxDobj.getPur_cd()).append("=").append(taxDobj.getNoOfAdvUnits());
                        }
                    }
                }
            }
            if (feeDobj.getListTaxDobj() != null && taxNoAdvUnits != null && !CommonUtils.isNullOrBlank(taxNoAdvUnits.toString())) {
                String updateSql = "Update  " + TableList.VP_RCPT_CART_TEMP + " set no_adv_units = ?  where appl_no = ? and user_cd = ? and state_cd = ? and off_cd = ? ";
                PreparedStatement psUpdate = tmgr.prepareStatement(updateSql);
                psUpdate.setString(1, taxNoAdvUnits.toString());
                psUpdate.setString(2, applNo);
                psUpdate.setLong(3, userCd);
                psUpdate.setString(4, stateCd);
                psUpdate.setInt(5, offCd);
                ServerUtil.validateQueryResult(tmgr, psUpdate.executeUpdate(), psUpdate);
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem occured during saving temporary payment data !!!");
        }
    }

    /**
     * Method to get the Owner Information Saved Against application number
     *
     * @param applicationNo Application Number
     * @return Owner Information against provided application number
     * @throws nic.rto.vahan.common.VahanException in case of any exception
     */
    public Owner_dobj getOwnerInfoByApplicationNo(String applicationNo) throws VahanException {
        Owner_dobj dobj = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        VahanException vahanexecption = null;
        String whereIam = "Fee_Impl.getOwnerInfoByApplicationNo";
        String vaOwnerSQL = "SELECT *  FROM " + TableList.VA_OWNER
                + " where appl_no=?";
        String vaHpaSQL = "SELECT fncr_name,fncr_add1,fncr_add2,fncr_village,fncr_taluk,"
                + " fncr_district,fncr_pincode,from_dt,state_cd, off_cd FROM va_hpa where appl_no =? and regn_no =?";
        try {
            tmgr = new TransactionManager(whereIam);
            ps = tmgr.prepareStatement(vaOwnerSQL);
            ps.setString(1, applicationNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                // data found
                dobj = new Owner_dobj();
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setOwner_name(rs.getString("owner_name"));
                //  dobj.setEng_no(rs.getString(""));
                dobj.setChasi_no(rs.getString("chasi_no"));
                // dobj.getOwner_identity().setMobile_no(rs.getInt("mob_no"));
                dobj.setF_name(rs.getString("f_name"));

                // check for hypth application
                ps = tmgr.prepareStatement(vaHpaSQL);
                ps.setString(1, applicationNo);
                ps.setString(2, dobj.getRegn_no());
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    dobj.setHypothecatedFlag(true);
                }

            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanexecption = new VahanException("Error in fetching details for [ " + applicationNo + "]");
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
        return dobj;

    }

    public String getMobileNo(String appNo) {
        String mobileNo = "";
        TransactionManager tmgr = null;
        String ChasiSQL = "select mobile_no from va_owner_identification where appl_no=?";
        try {
            tmgr = new TransactionManager("getMobileNo");
            PreparedStatement ps = tmgr.prepareStatement(ChasiSQL);
            ps.setString(1, appNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                mobileNo = rs.getString("mobile_no");
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
        return mobileNo;
    }

    public String getOfficeName(int offCode, String stateCode) {
        String officeName = "";
        TransactionManager tmgr = null;
        String ChasiSQL = "select off_name from tm_office where off_cd=? and state_cd = ?";
        try {
            tmgr = new TransactionManager("getOfficeName");
            PreparedStatement ps = tmgr.prepareStatement(ChasiSQL);
            ps.setInt(1, offCode);
            ps.setString(2, stateCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                officeName = rs.getString("off_name");
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
        return officeName;
    }

    public String getRcptNo(int offCode, String stateCode, String applNo) {
        String rcptNo = "";
        TransactionManager tmgr = null;
        String ChasiSQL = "select rcpt_no from vp_appl_rcpt_mapping where off_cd=? and state_cd = ? and appl_no = ?";
        try {
            tmgr = new TransactionManager("getOfficeName");
            PreparedStatement ps = tmgr.prepareStatement(ChasiSQL);
            ps.setInt(1, offCode);
            ps.setString(2, stateCode);
            ps.setString(3, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                rcptNo = rs.getString("rcpt_no");
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
        return rcptNo;
    }

    public String getPurCodeShortForm(String purCode) {
        String shortForm = "";
        TransactionManager tmgr = null;
        String sql = "select pur_cd,descr,short_descr from tm_purpose_mast where pur_cd in (" + purCode + ")";

        try {
            tmgr = new TransactionManager("getPurCodeShortForm");
            PreparedStatement ps = tmgr.prepareStatement(sql);

            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (rs.getString("short_descr") != null) {
                    if (!shortForm.equals("")) {
                        shortForm = shortForm + "/";
                    }
                    shortForm = shortForm + rs.getString("short_descr");
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
        return shortForm;
    }

    public String saveBalanceFeeDetailsInstrument(Fee_Pay_Dobj feeDobj, FeeDraftDobj feeDraftDobj, String remarks, String sOption, boolean blackListedCompoundingAmt) throws VahanException {
        String rcpt = null;
        TransactionManager tmgr = null;
        long lngTotalAmt = 0;
        boolean newAppl = false;
        try {
            tmgr = new TransactionManager("saveBalanceFeeDetailsInstrument");
            PreparedStatement pstmtVtFee = null;
            rcpt = Receipt_Master_Impl.generateNewRcptNo(Util.getSelectedSeat().getOff_cd(), tmgr);
            java.sql.Timestamp rcptTimeStamp = new java.sql.Timestamp(feeDobj.getRcptDt().getTime());
            String applNo = null;
            if (feeDobj.getApplNo() == null) {
                applNo = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
                feeDobj.setApplNo(applNo);
            }
            for (FeeDobj feePurDobj : feeDobj.getCollectedFeeList()) {
                if (feePurDobj.getPurCd() != -1) {
                    String vtFeeSQL = "INSERT INTO vt_fee("
                            + " regn_no, payment_mode, fees, fine, rcpt_no, rcpt_dt, pur_cd, "
                            + " flag, collected_by, state_cd, off_cd)"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, "
                            + " ?, ?, ?, ?)";
                    pstmtVtFee = tmgr.prepareStatement(vtFeeSQL);
                    pstmtVtFee.setString(1, feeDobj.getRegnNo());//regn_no
                    pstmtVtFee.setString(2, feeDobj.getPaymentMode());//payment_mode
                    pstmtVtFee.setLong(3, feePurDobj.getFeeAmount());//fees
                    pstmtVtFee.setLong(4, feePurDobj.getFineAmount());//fine
                    lngTotalAmt = lngTotalAmt + (feePurDobj.getFeeAmount() + feePurDobj.getFineAmount());
                    pstmtVtFee.setString(5, rcpt);//rcpt_no
                    pstmtVtFee.setTimestamp(6, rcptTimeStamp);//rcpt_dt
                    pstmtVtFee.setInt(7, feePurDobj.getPurCd());//   pur_cd
                    pstmtVtFee.setNull(8, java.sql.Types.VARCHAR);//   flag
                    pstmtVtFee.setString(9, Util.getEmpCode());//   collected_by
                    pstmtVtFee.setString(10, Util.getUserStateCode());//   state_cd
                    pstmtVtFee.setInt(11, Util.getUserOffCode());//   off_cd
                    pstmtVtFee.executeUpdate();

                    if (feePurDobj.getFromDate() != null || feePurDobj.getUptoDate() != null) {
                        String sql = "insert into " + TableList.VT_FEE_BREAKUP
                                + "(state_cd, off_cd, rcpt_no, sr_no, fee_from, fee_upto, pur_cd,"
                                + " fee, fine, exempted, rebate, surcharge, interest, prv_adjustment)"
                                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement ps = tmgr.prepareStatement(sql);
                        int i = 1;
                        ps.setString(i++, Util.getUserStateCode());
                        ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
                        ps.setString(i++, rcpt);
                        ps.setInt(i++, 1);
                        // if(ps.setNull(7, java.sql.Types.VARCHAR);)
                        ps.setDate(i++, new java.sql.Date(feePurDobj.getFromDate().getTime()));
                        if (feePurDobj.getUptoDate() == null) {
                            ps.setNull(i++, java.sql.Types.DATE);
                        } else {
                            ps.setDate(i++, new java.sql.Date(feePurDobj.getUptoDate().getTime()));
                        }

                        ps.setInt(i++, feePurDobj.getPurCd());
                        ps.setLong(i++, feePurDobj.getFeeAmount());
                        ps.setLong(i++, feePurDobj.getFineAmount());
                        ps.setInt(i++, 0);
                        ps.setInt(i++, 0);
                        ps.setInt(i++, 0);
                        ps.setInt(i++, 0);
                        ps.setInt(i++, 0);
                        ps.executeUpdate();

                    }

                }
                if (feePurDobj.getPurCd() == TableConstants.BLCompoundingAmtCode && blackListedCompoundingAmt) {
                    BlackListedVehicleImpl impl = new BlackListedVehicleImpl();
                    impl.insertIntoVhBlacklistFromVtBlacklist(tmgr, feeDobj.getRegnNo(), Util.getUserStateCode(), Util.getUserOffCode(), rcpt);
                }
            }
            for (Tax_Pay_Dobj dobj : feeDobj.getListTaxDobj()) {
                if (dobj.getPur_cd() != -1) {
                    String vtTaxSQL = "INSERT INTO vt_tax("
                            + " regn_no, tax_mode, payment_mode, tax_amt, tax_fine, rcpt_no, rcpt_dt, tax_from, tax_upto, pur_cd, flag, collected_by, state_cd,off_cd)"
                            + " VALUES (?, ?, ?, ?, ?, ?, current_timestamp, ?, ?, ?, ?, ?, ?, ?)";

                    pstmtVtFee = tmgr.prepareStatement(vtTaxSQL);
                    int i = 1;
                    pstmtVtFee.setString(i++, feeDobj.getRegnNo());//regn_no
                    pstmtVtFee.setString(i++, TableConstants.TAX_MODE_BALANCE);//tax_mode(B) For Balance Tax 
                    pstmtVtFee.setString(i++, dobj.getPaymentMode());//payment_mode
                    pstmtVtFee.setLong(i++, dobj.getFinalTaxAmount() - dobj.getTotalPaybalePenalty());//tax_amt
                    pstmtVtFee.setLong(i++, dobj.getTotalPaybalePenalty());//tax_fine
                    pstmtVtFee.setString(i++, rcpt);//rcpt_no
                    pstmtVtFee.setDate(i++, new java.sql.Date(DateUtils.parseDate(dobj.getFinalTaxFrom()).getTime()));//tax_from
                    if (dobj.getFinalTaxUpto() != null) {
                        pstmtVtFee.setDate(i++, new java.sql.Date(DateUtils.parseDate(dobj.getFinalTaxUpto()).getTime()));//tax_upto.setDate(i++, new java.sql.Date(DateUtils.parseDate(taxDobj.getFinalTaxUpto()).getTime()));//tax_upto
                    } else {
                        pstmtVtFee.setNull(i++, java.sql.Types.DATE);//tax_upto
                    }
                    pstmtVtFee.setInt(i++, dobj.getPur_cd());//   pur_cd
                    pstmtVtFee.setNull(i++, java.sql.Types.VARCHAR);//   flag
                    pstmtVtFee.setString(i++, Util.getEmpCode());//   collected_by
                    pstmtVtFee.setString(i++, Util.getUserStateCode());//   state_cd
                    pstmtVtFee.setInt(i++, Util.getSelectedSeat().getOff_cd());//   off_cd
                    pstmtVtFee.executeUpdate();
                    int srNo = 0;
                    for (DOTaxDetail taxBrkUpDobj : dobj.getTaxBreakDetails()) {
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
                        pstmtVtTaxBrkUp.setString(1, rcpt);//rcpt_no
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
                        pstmtVtTaxBrkUp.setInt(16, Util.getSelectedSeat().getOff_cd());//   off_cd
                        pstmtVtTaxBrkUp.executeUpdate();
                    }
                    TaxClearImpl taxClearImpl = new TaxClearImpl();
                    taxClearImpl.updateAppl_noInVt_Refund_Excess(applNo, dobj.getPur_cd(), dobj.getRegnNo(), TableConstants.TM_ROLE_BALANCE_FEE_TAX_COLLECTION, tmgr);
                }
            }
            if (rcpt == null) {
                return rcpt;
            }
            Long inscd = null;
            if (feeDraftDobj != null) {
                FeeDraftimpl feeDraft_impl = new FeeDraftimpl();
                feeDraftDobj.setAppl_no(feeDobj.getApplNo());
                feeDraftDobj.setRcpt_no(rcpt);
                inscd = feeDraft_impl.saveDraftDetails(feeDraftDobj, tmgr);
            }
            String regnNo = feeDobj.getRegnNo();
            String sql = "select va_status.pur_cd,regn_no from va_status,va_details"
                    + " where va_status.appl_no=va_details.appl_no "
                    + " and va_status.appl_no=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, feeDobj.getApplNo());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                if (rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                        || rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_TEMP_REG
                        || rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                        || rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE
                        || rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_CARRIER_RENEWAL
                        || rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_CARRIER_DUPLICATE
                        || rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE_FITNESS) {
                    newAppl = true;
                    break;
                } else {
                    regnNo = rs.getString("regn_no");
                }
            }
            OwnerImpl owImpl = new OwnerImpl();
            Owner_dobj owndobj = null;
            TmConfigurationDobj configurationDobj = Util.getTmConfiguration();
            if (newAppl) {
                owndobj = owImpl.set_Owner_appl_db_to_dobj(null, feeDobj.getApplNo(),
                        null, TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE);
            } else if (Util.getUserCategory() != null && configurationDobj != null
                    && (Util.getUserCategory().equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_USER) || configurationDobj.isAllow_fitness_all_RTO())) {
                //for allowing fitness test centre/All RTO for Fitness for taking balance fee
                owndobj = owImpl.set_Owner_appl_db_to_dobj(regnNo, null, null, TableConstants.VM_TRANSACTION_MAST_FIT_CERT);
            } else {
                owndobj = owImpl.set_Owner_appl_db_to_dobj_with_state_off_cd(regnNo, null, null, 1);
            }
            if (feeDobj.getFeeTypeCategory().matches("[C|O|F]+")) {
                owndobj = new Owner_dobj();
                owndobj.setOwner_name(feeDobj.getOwnerDobj().getOwner_name());
            }
            if (feeDobj.getFeeTypeCategory().matches("T")) {
                owndobj = owImpl.set_Owner_appl_db_to_dobj_with_state_off_cd(regnNo, null, null, 18);
            }
            if (owndobj == null) {
                throw new VahanException("Owner Details not found. Please try again.");
            }
            PaymentGenInfo payInfo = getPaymentInfo(feeDobj, feeDraftDobj);
            saveRecptInstMap(inscd, feeDobj.getApplNo(), rcpt, payInfo, owndobj, tmgr, remarks);
            ServerUtil.insertForQRDetails(feeDobj.getApplNo(), null, null, rcpt, false, DocumentType.RECEIPT_QR, Util.getUserStateCode(), Util.getUserOffCode(), tmgr);
            /////////////// CODE INSERTED ONLY FOR CARRIER REGISTRATION
            boolean blnCarrierFeepaid = false;
            int purCd = 0;
            for (FeeDobj feePurDobj : feeDobj.getCollectedFeeList()) {
                if (feePurDobj.getPurCd() != -1
                        && (feePurDobj.getPurCd() == TableConstants.VM_TRANSACTION_CARRIER_REGN
                        || feePurDobj.getPurCd() == TableConstants.VM_TRANSACTION_CARRIER_DUPLICATE
                        || feePurDobj.getPurCd() == TableConstants.VM_TRANSACTION_CARRIER_RENEWAL) && sOption.equals("F")) {
                    blnCarrierFeepaid = true;
                    purCd = feePurDobj.getPurCd();
                }
            }
            if (sOption.equals("F")) {
                if (!blnCarrierFeepaid) {
                    throw new VahanException("Carrier Registration Fee Not Paid");
                }

                Status_dobj status = new Status_dobj();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                String dt = sdf.format(new Date());
                status.setAppl_dt(dt);
                status.setPur_cd(purCd);
                status.setStatus(TableConstants.STATUS_COMPLETE);
                status.setAppl_no(feeDobj.getApplNo());
                status.setOffice_remark("Fee Submitted");
                status.setPublic_remark("");
                status = ServerUtil.webServiceForNextStage(status, tmgr);
                ServerUtil.fileFlow(tmgr, status);
            }
            if (sOption.equals("D")) {
                ServerUtil.reInsertForDispatch(feeDobj.getRegnNo(), feeDobj.getState_cd(), Util.getSelectedSeat().getOff_cd(), tmgr);
            }
            ///////////////////////////////////////////////
            tmgr.commit();
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return rcpt;

    }

    public String[] saveFeeDetailsInstrument(Fee_Pay_Dobj feeDobj, FeeDraftDobj feeDraftDobj) throws VahanException {

        String[] rcpt = null;
        TransactionManager tmgr = null;
        String remarks = "";
        String remarksLTT = "";
        String remarksFitness = "";
        long taxAmt = 0;
        try {

            tmgr = new TransactionManager("saveFeeDetailsInstrument");
            rcpt = saveFeeDetails(feeDobj, feeDraftDobj, tmgr);
            if (rcpt == null) {
                return rcpt;
            }
            String mobileNo = getMobileNo(feeDobj.getApplNo());
            Long inscd = null;
            if (feeDraftDobj != null) {
                FeeDraftimpl feeDraft_impl = new FeeDraftimpl();
                feeDraftDobj.setAppl_no(rcpt[0]);
                feeDraftDobj.setRcpt_no(rcpt[1]);
                inscd = feeDraft_impl.saveDraftDetails(feeDraftDobj, tmgr);

            }

            for (int i = 0; i < feeDobj.getCollectedFeeList().size(); i++) {
                // Purcd =72 for Tax clearance certificate 
                if (feeDobj.getCollectedFeeList().get(i).getPurCd() == 72) {
                    TaxClearanceCertificatePrintImpl.insertintoVA_TCC_Print(feeDobj.getOwnerDobj().getRegn_no(), feeDobj.getApplNo(), tmgr, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                }
                //for updating new owner name in VP_APPL_RCPT_MAPPING table in case of TO/FRC
                String toDbjOwnerName = this.updateOwnerNameForToOrFrc(feeDobj.getCollectedFeeList().get(i).getPurCd(), Util.getSelectedSeat().getAppl_no(), feeDobj.getOwnerDobj());
                if (toDbjOwnerName != null) {
                    feeDobj.getOwnerDobj().setOwner_name(toDbjOwnerName);
                }

                if (feeDobj.getCollectedFeeList().get(i).getPurCd() == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {
                    FitnessImpl fitnessImpl = new FitnessImpl();
                    FitnessDobj fitnessDobjTemp = fitnessImpl.getVtFitnessTempDetails(feeDobj.getOwnerDobj().getRegn_no());
                    if (fitnessDobjTemp != null) {
                        String offName = fitnessDobjTemp.getOff_name().length() > 20 ? (fitnessDobjTemp.getOff_name().substring(0, 20)) : fitnessDobjTemp.getOff_name();
                        remarks = "Temp. Fitness has been taken from-" + fitnessDobjTemp.getState_cd() + "," + offName + " Appl.No-[" + fitnessDobjTemp.getAppl_no() + "] dt:" + fitnessDobjTemp.getOp_dt_descr();
                    }
                }

                if (feeDobj.getCollectedFeeList().get(i).getPurCd() == TableConstants.VM_TRANSACTION_MAST_ADDL_TO_VEHICLE
                        || feeDobj.getCollectedFeeList().get(i).getPurCd() == TableConstants.VM_TRANSACTION_MAST_GREEN_TAX) {
                    LifeTimeTaxUpdateDobj lifeTimeTaxUpdate = new LifeTimeTaxUpdateDobj();
                    lifeTimeTaxUpdate.setRegnNo(feeDobj.getOwnerDobj().getRegn_no());
                    lifeTimeTaxUpdate.setOffcd(feeDobj.getOwnerDobj().getOff_cd());
                    lifeTimeTaxUpdate.setStateCd(feeDobj.getOwnerDobj().getState_cd());
                    LifeTimeTaxUpdateImpl lifeTimeTaxUpdateImpl = new LifeTimeTaxUpdateImpl();
                    taxAmt = lifeTimeTaxUpdateImpl.getLifeTaxUpdateAmt(lifeTimeTaxUpdate);
                    if (taxAmt > 0) {
                        remarksLTT = remarksLTT + ServerUtil.getTaxHead(feeDobj.getCollectedFeeList().get(i).getPurCd()) + "/";
                    }
                    if (taxAmt <= 0) {
                        taxAmt = getToTaxAmt(feeDobj.getOwnerDobj().getRegn_no());
                    }

                }
                if (feeDobj.getCollectedFeeList().get(i).getPurCd() == TableConstants.VM_MAST_MANUAL_RECEIPT) {
                    ManualReceiptEntryImpl.updateVTManualReceiptEntryStatus(tmgr, feeDobj.getApplNo(), true);
                    ManualReceiptEntryDobj manualRcptDobjTemp = ManualReceiptEntryImpl.getApprovedManualReceiptDetails(feeDobj.getApplNo());
                    if (manualRcptDobjTemp != null) {
                        remarks = remarks + "Rs. " + manualRcptDobjTemp.getAmount() + "/- adjusted against previously paid vide receipt no:" + manualRcptDobjTemp.getRcptNo() + ".";
                    }

                }

                if (feeDobj.getCollectedFeeList().get(i).getFeeFineExemDobj() != null) {
                    feeDobj.getCollectedFeeList().get(i).getFeeFineExemDobj().setRcptNo(rcpt[1]);
                    saveFeeFineExemptionDetails(feeDobj.getCollectedFeeList().get(i).getFeeFineExemDobj(), tmgr);
                }
            }
            if (!CommonUtils.isNullOrBlank(remarksLTT)) {
                remarksLTT = remarksLTT.substring(0, remarksLTT.lastIndexOf("/"));
                remarksLTT = remarksLTT + " calculated on the basis of Last Paid LTT/OTT Rs." + taxAmt;
            }
            if (!CommonUtils.isNullOrBlank(feeDobj.getRemarks()) && Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {
                remarksFitness = feeDobj.getRemarks();
            }

            remarks = remarks + remarksLTT + remarksFitness;

            Owner_dobj owndobj = feeDobj.getOwnerDobj();
            PaymentGenInfo payInfo = getPaymentInfo(feeDobj, feeDraftDobj);
            saveRecptInstMap(inscd, feeDobj.getApplNo(), rcpt[1], payInfo, owndobj, tmgr, remarks);
            NewVehicleNo newVehicleNo = new NewVehicleNo();
            String regn_no = newVehicleNo.generateAssignNewRegistrationNo(Util.getSelectedSeat().getOff_cd(), Util.getSelectedSeat().getAction_cd(), feeDobj.getApplNo(), null, 1, null, null, tmgr);
            rcpt[2] = regn_no;
            if (feeDobj.isTaxInstallment()) {
                String sql = "Insert into vt_tax_installment(state_cd,off_cd,appl_regn_no,tax_mode,user_cd,op_dt) "
                        + " values(?,?,?,?,?,current_timestamp)";
                PreparedStatement ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getSelectedSeat().getOff_cd());
                ps.setString(3, Util.getSelectedSeat().getAppl_no());
                ps.setString(4, feeDobj.getTaxInstallMode());
                ps.setString(5, Util.getEmpCode());
                ps.executeUpdate();
            }
            if ("UP".contains(Util.getUserStateCode()) && null != owndobj && owndobj.getVh_class() == 13 && Util.getTmConfiguration() != null && Util.getTmConfiguration().getTmConfigDealerDobj() != null && !Util.getTmConfiguration().getTmConfigDealerDobj().isTempRegnFeeWithNewForSameOffices() && Util.getUserCategory().equals(TableConstants.USER_CATG_OFF_STAFF) && Util.getTmConfiguration().isTempFeeInNewRegis()) {
                String isValidForTempFeeAtRTO = EpayImpl.getValidForTempFeeDetails(Util.getUserStateCode(), Util.getUserSeatOffCode(), Util.getSelectedSeat().getAppl_no());
                if (isValidForTempFeeAtRTO != null && !"TRUE".equalsIgnoreCase(isValidForTempFeeAtRTO)) {
                    OnlinePayDobj payDobj = new OnlinePayDobj();
                    payDobj.setOwnerDobj(owndobj);
                    payDobj.setApplNo(Util.getSelectedSeat().getAppl_no());
                    payDobj.setReceiptNo(rcpt[1]);
                    new OnlinePayImpl().insertTemporaryRegnDetails(Util.getSelectedSeat().getOff_cd(), Integer.parseInt(isValidForTempFeeAtRTO), "OR", payDobj, "0", Util.getUserStateCode(), "", tmgr, Util.getTmConfiguration().getTmConfigDealerDobj().getTempFlowInNewRegnActionCd());
                }
            }

            if (owndobj != null && feeDobj.getPermitDobj() != null && !CommonUtils.isNullOrBlank(feeDobj.getPermitDobj().getChangeData())) {
                owndobj.setAppl_no(feeDobj.getApplNo());
                NewImpl.UpdatePmtDtlsIntoVaOwner(tmgr, owndobj);
                NewImpl.insertOrUpdateVaPermitNewRegn(tmgr, owndobj);
                ComparisonBeanImpl.updateChangedData(owndobj.getAppl_no(), feeDobj.getPermitDobj().getChangeData(), tmgr);
            }
            ServerUtil.insertForQRDetails(feeDobj.getApplNo(), null, null, rcpt[1], false, DocumentType.RECEIPT_QR, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), tmgr);
            tmgr.commit();
            long amtCollectionFeeTotal = 0;
            long amtTaxTotal = 0;
            long totalAmount = 0;
            String purCd = "";
            String msgMobileCollection = "";
            String msgMobileTax = "";
            String officeName = getOfficeName(Util.getSelectedSeat().getOff_cd(), Util.getUserStateCode());
            String purShortForm = "";
            if (feeDobj != null) {
                if (feeDobj.getCollectedFeeList().size() > 0) {
                    for (FeeDobj freedobj : feeDobj.getCollectedFeeList()) {
                        if (purCd.equals("")) {
                            purCd = String.valueOf(freedobj.getPurCd());
                        } else {
                            purCd = purCd + ", " + String.valueOf(freedobj.getPurCd());
                        }

                        amtCollectionFeeTotal = amtCollectionFeeTotal + freedobj.getTotalAmount();
                    }
                    purShortForm = getPurCodeShortForm(purCd);
                    purCd = "";
                }
                if (feeDobj.getListTaxDobj() != null) {
                    if (feeDobj.getListTaxDobj().size() > 0) {
                        for (Tax_Pay_Dobj taxpaydobj : feeDobj.getListTaxDobj()) {
                            if (purCd.equals("")) {
                                purCd = String.valueOf(taxpaydobj.getPur_cd());
                            } else {
                                purCd = purCd + "/ " + String.valueOf(taxpaydobj.getPur_cd());
                            }
                            amtTaxTotal = amtTaxTotal + taxpaydobj.getFinalTaxAmount();
                        }
                    }
                }
                totalAmount = amtCollectionFeeTotal + amtTaxTotal;
                if (feeDobj.getIsNewCollectionForm()) {
                    if (rcpt[2] == null || rcpt[2].equals("NEW") || rcpt[2].equals("")) {
                        msgMobileCollection = "[" + feeDobj.getApplNo().toUpperCase() + "]%0D%0A"
                                + "Received Rs." + totalAmount + "/- against new registration fee vide receipt no " + rcpt[1] + " dated " + JSFUtils.convertToStandardDateFormat(new java.util.Date()) + ".%0D%0A"
                                + "Thanks " + officeName;
                    } else {
                        msgMobileCollection = "[" + regn_no + "]%0D%0A"
                                + "Received Rs." + totalAmount + "/- against new registration fee vide receipt no " + rcpt[1] + " dated " + JSFUtils.convertToStandardDateFormat(new java.util.Date()) + ".%0D%0A"
                                + "Thanks " + officeName;
                    }
                } else {
                    msgMobileCollection
                            = "[" + feeDobj.getApplNo().toUpperCase() + "]%0D%0A"
                            + "Received Rs." + totalAmount + "/- against " + purShortForm + " fee vide receipt no " + rcpt[1] + " dated " + JSFUtils.convertToStandardDateFormat(new java.util.Date()) + ".%0D%0A"
                            + "Thanks " + officeName;
                }

                sendSMS(mobileNo, msgMobileCollection);
            }
        } catch (VahanException ve) {
            //LOGGER.error(ve);
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Receipt No Genereation Failed...!!!");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return rcpt;
    }

    public String updateOwnerNameForToOrFrc(int purCd, String applNo, Owner_dobj ownerDbj) {
        String toDbjOwnerName = null;
        if (purCd == TableConstants.VM_TRANSACTION_MAST_TO
                || purCd == TableConstants.VM_TRANSACTION_MAST_FRESH_RC) {
            ToImpl impl = new ToImpl();
            ToDobj toDobj = impl.set_TO_appl_db_to_dobj(Util.getSelectedSeat().getAppl_no());
            if (toDobj != null && ownerDbj != null) {
                toDbjOwnerName = toDobj.getOwner_name();
            }
        }
        return toDbjOwnerName;
    }

    public void updateOwnerDobjForAltOrConv(AltDobj altDobj, ConvDobj convdobj, Owner_dobj ownerDobj) throws VahanException, Exception {
        if (ownerDobj != null) {
            if (altDobj != null) {
                ownerDobj.setFit_upto(altDobj.getFit_date_upto());
                ownerDobj.setVh_class(altDobj.getVh_class());
                ownerDobj.setVch_catg(altDobj.getVch_catg());
            }
            if (convdobj != null) {
                ownerDobj.setFit_upto(convdobj.getNew_fit_dt());
                ownerDobj.setVh_class(convdobj.getNew_vch_class());
                if (!Util.getUserStateCode().equalsIgnoreCase("OR")) {
                    ownerDobj.setVch_catg(convdobj.getNew_vch_catg());
                }
                if (!Util.getUserStateCode().equalsIgnoreCase("NL")) {
                    ownerDobj.setRegn_type(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE);
                }
                ownerDobj.setTax_mode(convdobj.getNewTaxMode());
                ownerDobj.setVehType(ServerUtil.VehicleClassType(convdobj.getNew_vch_class()));
            }
        } else {
            throw new VahanException("Problem in Updating Details");
        }
    }

    public void saveRecptInstMap(Long instNo, String applno, String rcptno, PaymentGenInfo payInfo, Owner_dobj owndobj, TransactionManager tmgr, String remarks) throws SQLException {
        String sql = "INSERT INTO " + TableList.VP_APPL_RCPT_MAPPING
                + "( state_cd, off_cd, appl_no, rcpt_no, owner_name, chasi_no, vh_class, \n"
                + " instrument_cd, excess_amt, cash_amt,remarks)"
                + "    VALUES (?, ?, ?, ?, ?, ?, ?,?, ?, ?,?)";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        int i = 1;
        ps.setString(i++, Util.getUserStateCode());
        ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
        ps.setString(i++, applno);
        ps.setString(i++, rcptno);
        ps.setString(i++, owndobj.getOwner_name());
        ps.setString(i++, owndobj.getChasi_no() == null ? "" : owndobj.getChasi_no());
        ps.setInt(i++, owndobj.getVh_class());
        if (instNo == null) {
            ps.setNull(i++, java.sql.Types.INTEGER);
        } else {
            ps.setLong(i++, instNo);
        }

        ps.setLong(i++, payInfo.getExcessAmt());
        ps.setLong(i++, payInfo.getCashAmt());
        if (remarks != null && !remarks.equals("")) {
            ps.setString(i++, remarks.toUpperCase());
        } else {
            ps.setString(i++, null);
        }
        ps.executeUpdate();

    }

    public String getChassisNo(String Statecd, int offcd, String rcptNo) {
        String chasino = null;
        TransactionManager tmgr = null;
        String ChasiSQL = "select chasi_no from vt_owner where regn_no=(select distinct(REGN_NO) from get_rcpt_details(?,?,?)) and state_cd  = ? and off_cd  =? ";
        try {
            tmgr = new TransactionManager("Returning chassis no");
            PreparedStatement ps = tmgr.prepareStatement(ChasiSQL);
            ps.setString(1, Statecd);
            ps.setInt(2, offcd);
            ps.setString(3, rcptNo);
            ps.setString(4, Util.getUserStateCode());
            ps.setInt(5, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                chasino = rs.getString("chasi_no");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return chasino;
    }

    public PaymentGenInfo getPaymentInfo(List<Tax_Pay_Dobj> listTaxDobj, FeeDraftDobj feeDraftDobj, Long userChg) {
        PaymentGenInfo payInfo = new PaymentGenInfo();
        long amtTotal = 0;
        long amtDraft = 0;
        long amtCash = 0;
        long amtExcess = 0;

        if (feeDraftDobj != null) {
            for (PaymentCollectionDobj draftPayment : feeDraftDobj.getDraftPaymentList()) {
                amtDraft = amtDraft + Long.parseLong(draftPayment.getAmount());
            }
        }

        for (Tax_Pay_Dobj taxDobj : listTaxDobj) {
            if (taxDobj != null && !taxDobj.getTaxMode().equals("0")) {
                amtTotal = amtTotal + taxDobj.getFinalTaxAmount();
            }
        }
        amtTotal = amtTotal + userChg;
        if (amtDraft > amtTotal) {
            amtExcess = amtDraft - amtTotal;
            amtCash = 0;
        } else {
            amtExcess = 0;
            amtCash = amtTotal - amtDraft;
        }

        payInfo.setCashAmt(amtCash);
        payInfo.setDraftAmt(amtDraft);
        payInfo.setExcessAmt(amtExcess);
        payInfo.setTotalAmt(amtTotal);

        return payInfo;
    }

    public PaymentGenInfo getPaymentInfo(Fee_Pay_Dobj feeDobj, FeeDraftDobj feeDraftDobj) {
        PaymentGenInfo payInfo = new PaymentGenInfo();
        long amtTotal = 0;
        long amtDraft = 0;
        long amtCash = 0;
        long amtExcess = 0;
        for (FeeDobj feePurDobj : feeDobj.getCollectedFeeList()) {
            amtTotal = amtTotal + feePurDobj.getFeeAmount() + feePurDobj.getFineAmount();
        }
        if (feeDraftDobj != null) {
            for (PaymentCollectionDobj draftPayment : feeDraftDobj.getDraftPaymentList()) {
                amtDraft = amtDraft + Long.parseLong(draftPayment.getAmount());
            }
        }

        if (feeDobj.getListTaxDobj() != null) {

            for (int i = 0; i < feeDobj.getListTaxDobj().size(); i++) {
                Tax_Pay_Dobj taxDobj = null;
                taxDobj = feeDobj.getListTaxDobj().get(i);
                if (taxDobj.getFinalTaxAmount() != 0l) {
                    amtTotal = amtTotal + taxDobj.getFinalTaxAmount();
                }
            }
        }

        if (amtDraft > amtTotal) {
            amtExcess = amtDraft - amtTotal;
            amtCash = 0;
        } else {
            amtExcess = 0;
            amtCash = amtTotal - amtDraft;
        }

        payInfo.setCashAmt(amtCash);
        payInfo.setDraftAmt(amtDraft);
        payInfo.setExcessAmt(amtExcess);
        payInfo.setTotalAmt(amtTotal);

        return payInfo;
    }

    /**
     * Documented By: NITIN MISHRA ** the bond**
     *
     *
     * This function serves the purpose of saving fee data for new register
     * vehicle and registered vehicle In case of new registration we fetch the
     * data with given appl_no that and then move to the next flow and in case
     * of registered vehcile we generate an application no and saving the data
     * in va_status with different purpose code and each will me moved to the
     * next flow and goes in the history table vha_status and updated in
     * va_status with flow_no and action code
     *
     * @param feeDobj
     * @return receipt number
     * @throws VahanException *
     *
     */
    public String[] saveFeeDetails(Fee_Pay_Dobj feeDobj, FeeDraftDobj feeDraftDobj, TransactionManager tmgr) throws VahanException {
        long lngTotalAmt = 0;

        Status_dobj status = new Status_dobj();
        String vtFeeSQL = "INSERT INTO vt_fee("
                + " regn_no, payment_mode, fees, fine, rcpt_no, rcpt_dt, pur_cd, "
                + " flag, collected_by, state_cd, off_cd)"
                + " VALUES (?, ?, ?, ?, ?, current_timestamp, ?, "
                + " ?, ?, ?, ?)";

        //for selcting from va_status for a given appl_no
        String vaStatusSelectSql = " select * from va_status where appl_no=?";

        String whereIam = "Fee_Impl.saveFeeDetails";
        PreparedStatement pstmtVtFee = null;
        PreparedStatement psSelectVaStaus = null;
        String rcptNo = null;
        String applNo = null;
        String applno[] = new String[3];
        boolean isTaxSaved = false;
        try {

            rcptNo = Receipt_Master_Impl.generateNewRcptNo(Util.getUserOffCode(), tmgr);

            pstmtVtFee = tmgr.prepareStatement(vtFeeSQL);
            // Multiple Entry against purpose codes
            for (FeeDobj feePurDobj : feeDobj.getCollectedFeeList()) {
                pstmtVtFee.setString(1, feeDobj.getRegnNo());//regn_no
                pstmtVtFee.setString(2, feeDobj.getPaymentMode());//payment_mode
                pstmtVtFee.setLong(3, feePurDobj.getFeeAmount());//fees
                pstmtVtFee.setLong(4, feePurDobj.getFineAmount());//fine
                lngTotalAmt = lngTotalAmt + (feePurDobj.getFeeAmount() + feePurDobj.getFineAmount());
                pstmtVtFee.setString(5, rcptNo);//rcpt_no
                pstmtVtFee.setInt(6, feePurDobj.getPurCd());//   pur_cd
                pstmtVtFee.setNull(7, java.sql.Types.VARCHAR);//   flag
                pstmtVtFee.setString(8, Util.getEmpCode());//   collected_by
                pstmtVtFee.setString(9, Util.getUserStateCode());//   state_cd
                pstmtVtFee.setInt(10, Util.getUserOffCode());//   off_cd

                if (feePurDobj.getFromDate() != null || feePurDobj.getUptoDate() != null) {
                    String sql = "insert into " + TableList.VT_FEE_BREAKUP
                            + "(state_cd, off_cd, rcpt_no, sr_no, fee_from, fee_upto, pur_cd,"
                            + " fee, fine, exempted, rebate, surcharge, interest, prv_adjustment)"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement ps = tmgr.prepareStatement(sql);
                    int i = 1;
                    ps.setString(i++, Util.getUserStateCode());
                    ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
                    ps.setString(i++, rcptNo);
                    ps.setInt(i++, 1);
                    // if(ps.setNull(7, java.sql.Types.VARCHAR);)
                    ps.setDate(i++, new java.sql.Date(feePurDobj.getFromDate().getTime()));
                    if (feePurDobj.getUptoDate() == null) {
                        ps.setNull(i++, java.sql.Types.DATE);
                    } else {
                        ps.setDate(i++, new java.sql.Date(feePurDobj.getUptoDate().getTime()));
                    }

                    ps.setInt(i++, feePurDobj.getPurCd());
                    ps.setLong(i++, feePurDobj.getFeeAmount());
                    ps.setLong(i++, feePurDobj.getFineAmount());
                    ps.setInt(i++, 0);
                    ps.setInt(i++, 0);
                    ps.setInt(i++, 0);
                    ps.setInt(i++, 0);
                    ps.setInt(i++, 0);
                    ps.executeUpdate();

                }

                pstmtVtFee.addBatch();
            }

            if (pstmtVtFee != null) {
                pstmtVtFee.executeBatch();
            }

            boolean newRegistrationFormFlow = feeDobj.getIsNewCollectionForm();

            if (newRegistrationFormFlow) {
                applno[0] = applNo;
                applno[1] = rcptNo;
                applNo = feeDobj.getApplNo();
                if (feeDobj.getListTaxDobj() != null) {
                    TaxServer_Impl taxServerImpl = new TaxServer_Impl();
                    isTaxSaved = taxServerImpl.savetaxDetails(feeDobj.getListTaxDobj(),
                            rcptNo, feeDobj.getOwnerDobj(), feeDobj.getPermitDobj(), tmgr);
                }
                String newStatus = null;
                String counterId = null;
                int purCd = 0;
                int actionCd = 0;
                int checkActionCd = 0;
                vaStatusSelectSql = "select * from va_status where appl_no=?";
                psSelectVaStaus = tmgr.prepareStatement(vaStatusSelectSql);
                psSelectVaStaus.setString(1, feeDobj.getApplNo());
                status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
                status.setAppl_no(feeDobj.getApplNo());
                status.setStatus(TableConstants.STATUS_COMPLETE);
                status.setOffice_remark("");
                status.setPublic_remark("");
                RowSet rsSelectVaStatus = tmgr.fetchDetachedRowSet_No_release();
                rsSelectVaStatus.last();
                int numRows = rsSelectVaStatus.getRow();
                int rowsCount = 0;
                rsSelectVaStatus.beforeFirst();

                while (rsSelectVaStatus.next()) {

                    newStatus = rsSelectVaStatus.getString("status");
                    counterId = rsSelectVaStatus.getString("cntr_id");
                    purCd = rsSelectVaStatus.getInt("pur_cd");
                    actionCd = rsSelectVaStatus.getInt("action_cd");
                    if (rowsCount == 0) {
                        checkActionCd = actionCd;
                    }
                    if (checkActionCd != actionCd) {
                        return applno = null;
                    }
                    status.setPur_cd(purCd);
                    VehicleParameters parm = FormulaUtils.fillVehicleParametersFromDobj(feeDobj.getOwnerDobj());
                    parm.setPUR_CD(purCd);
                    if (feeDobj.getOwnerDobj().getOtherStateVehDobj() != null && parm != null) {
                        parm.setLOGIN_OFF_CD(Util.getSelectedSeat().getOff_cd());
                        if (feeDobj.getOwnerDobj().getOtherStateVehDobj().getOldOffCD() != null) {
                            parm.setPREV_OFF_CD(feeDobj.getOwnerDobj().getOtherStateVehDobj().getOldOffCD());
                        }
                    }
                    status.setVehicleParameters(parm);
                    ServerUtil.webServiceForNextStage(status, TableConstants.STATUS_COMPLETE, null, feeDobj.getApplNo(), actionCd, purCd, null);
                    ServerUtil.fileFlow(tmgr, status);
                    rowsCount++;
                }
            } else if (feeDobj.isBlnTempRegn()) {
                applno[0] = applNo;
                applno[1] = rcptNo;
                applNo = feeDobj.getApplNo();

                if (feeDobj.getListTaxDobj() != null) {
                    TaxServer_Impl taxServerImpl = new TaxServer_Impl();
                    isTaxSaved = taxServerImpl.savetaxDetails(feeDobj.getListTaxDobj(),
                            rcptNo, feeDobj.getOwnerDobj(), feeDobj.getPermitDobj(), tmgr);

                }

                String newStatus = null;
                String counterId = null;
                int purCd = 0;
                int actionCd = 0;
                int checkActionCd = 0;
                vaStatusSelectSql = "select * from va_status where appl_no=?";
                psSelectVaStaus = tmgr.prepareStatement(vaStatusSelectSql);
                psSelectVaStaus.setString(1, feeDobj.getApplNo());
                RowSet rsSelectVaStatus = tmgr.fetchDetachedRowSet_No_release();
                rsSelectVaStatus.last();
                int numRows = rsSelectVaStatus.getRow();
                rsSelectVaStatus.beforeFirst();
                int rowsCount = 0;

                while (rsSelectVaStatus.next()) {

                    newStatus = rsSelectVaStatus.getString("status");
                    counterId = rsSelectVaStatus.getString("cntr_id");
                    purCd = rsSelectVaStatus.getInt("pur_cd");
                    actionCd = rsSelectVaStatus.getInt("action_cd");

                    if (rowsCount == 0) {
                        checkActionCd = actionCd;
                    }
                    if (checkActionCd != actionCd) {
                        return applno = null;
                    }
                    // newstatus="F" -- forward
                    status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
                    status.setAppl_no(feeDobj.getApplNo());
                    status.setStatus(TableConstants.STATUS_COMPLETE);
                    status.setOffice_remark("");
                    status.setPublic_remark("");
                    //status.setPur_cd(TableConstants.VM_TRANSACTION_MAST_TEMP_REG);
                    status.setPur_cd(purCd);
                    // ServerUtil.webServiceForNextStage(status, TableConstants.STATUS_COMPLETE, null, feeDobj.getApplNo(), TableConstants.TM_ROLE_TEMP_REGISTRATION_FEE, TableConstants.VM_TRANSACTION_MAST_TEMP_REG, null);
                    ServerUtil.webServiceForNextStage(status, TableConstants.STATUS_COMPLETE, null, feeDobj.getApplNo(), actionCd, purCd, null);
                    ServerUtil.fileFlow(tmgr, status);
                    rowsCount++;
                }
            } else {//For Registered Vehicle Transactions
                //for selcting from va_status for a given appl_no
                vaStatusSelectSql = " select * from va_status where appl_no=?";

                if (feeDobj.getListTaxDobj() != null) {
                    TaxServer_Impl taxServerImpl = new TaxServer_Impl();
                    isTaxSaved = taxServerImpl.savetaxDetails(feeDobj.getListTaxDobj(),
                            rcptNo, feeDobj.getOwnerDobj(), feeDobj.getPermitDobj(), tmgr);
                }

                applNo = feeDobj.getApplNo();
                applno[0] = applNo;
                applno[1] = rcptNo;
                String newStatus = null;
                String counterId = null;
                int purCd = 0;
                int actionCd = 0;
                int checkActionCd = 0;

                psSelectVaStaus = tmgr.prepareStatement(vaStatusSelectSql);
                psSelectVaStaus.setString(1, feeDobj.getApplNo());
                // newstatus="F" -- forward
                status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
                status.setAppl_no(feeDobj.getApplNo());
                status.setStatus(TableConstants.STATUS_COMPLETE);
                status.setOffice_remark("");
                status.setPublic_remark("");
                RowSet rsSelectVaStatus = tmgr.fetchDetachedRowSet_No_release();
                rsSelectVaStatus.last();
                int numRows = rsSelectVaStatus.getRow();
                int rowsCount = 0;
                rsSelectVaStatus.beforeFirst();

                while (rsSelectVaStatus.next()) {
                    newStatus = rsSelectVaStatus.getString("status");
                    counterId = rsSelectVaStatus.getString("cntr_id");
                    purCd = rsSelectVaStatus.getInt("pur_cd");
                    actionCd = rsSelectVaStatus.getInt("action_cd");

                    if (purCd == TableConstants.VM_TRANSACTION_MAST_NOC || purCd == TableConstants.VM_TRANSACTION_MAST_REM_HYPO) {
                        VehicleParameters vehParameters = fillVehicleParametersFromDobj(feeDobj.getOwnerDobj());
                        ToImpl toImpl = new ToImpl();
                        if (toImpl.isSurrenderRetention(feeDobj.getApplNo())) {
                            vehParameters.setNOC_RETENTION(1);
                        }
                        if (!getTmPurposeActionFlowCondtion(Util.getUserStateCode(), purCd, TableConstants.TM_ACTION_REGISTERED_VEH_FEE, vehParameters)) {
                            continue;//for skipping fee of HPT,NOC
                        }

                    }
                    if ("GJ".equalsIgnoreCase(Util.getUserStateCode()) && purCd == TableConstants.VM_TRANSACTION_MAST_NOC) {
                        NocImpl nocimpl = new NocImpl();
                        NocDobj noc = nocimpl.set_NOC_appl_db_to_dobj(feeDobj.getApplNo(), feeDobj.getRegnNo());
                        if (noc != null && !CommonUtils.isNullOrBlank(noc.getState_to()) && !noc.getState_to().equalsIgnoreCase("GJ") && nocimpl.isNOCInwardwithHPC(feeDobj.getApplNo(), feeDobj.getRegnNo(), noc, Util.getUserStateCode(), Util.getUserSeatOffCode())) {
                            continue;
                        }
                    }

                    if (rowsCount == 0) {
                        checkActionCd = actionCd;
                    }
                    if (checkActionCd != actionCd) {
                        return applno = null;
                    }

                    status.setPur_cd(purCd);
                    VehicleParameters parm = FormulaUtils.fillVehicleParametersFromDobj(feeDobj.getOwnerDobj());
                    status.setVehicleParameters(parm);
                    ServerUtil.webServiceForNextStage(status, TableConstants.STATUS_COMPLETE, null, feeDobj.getApplNo(), actionCd, purCd, null);
                    ServerUtil.fileFlow(tmgr, status);
                    rowsCount++;
                }
            }
            //Saving Fine/Penalty Exemtion Order Details.
            saveFinePaneltyExemDetails(feeDobj.getApplNo(), rcptNo, tmgr, Util.getEmpCode());

        } catch (VahanException ve) {
            rcptNo = null;
            throw ve;
        } catch (BatchUpdateException ex) {
            rcptNo = null;
            LOGGER.error(ex.getNextException() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error In Saving Fee Details");
        } catch (Exception e) {
            rcptNo = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error In Saving Fee Details");
        }
        return applno;
    }

    public boolean checkPurCDExist(String applno, int purCD) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        boolean isExist = false;
        String vaStatusSelectSql = " select * from va_status where appl_no=? and pur_cd=?";
        try {
            tmgr = new TransactionManager("checkPurCDExist");
            ps = tmgr.prepareStatement(vaStatusSelectSql);
            ps.setString(1, applno);
            ps.setInt(2, purCD);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isExist = true;
            }
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
        return isExist;

    }
    //by komal

    public void saveAddToCartDetails(Fee_Pay_Dobj feeDobj, String applNo, long checkTotalAmount, String stateCd, int offCd, String empCode) throws VahanException {
        TransactionManager tmgr = null;
        Status_dobj status = new Status_dobj();
        String vaStatusSelectSql = "select * from va_status where appl_no=?";
        int noOfApplsForDealerPayment = 0;
        try {
            int cartCount = ServerUtil.getAddToCartStatusCount();
            TmConfigurationDobj tmConfDobj = Util.getTmConfiguration();
            if (tmConfDobj != null) {
                noOfApplsForDealerPayment = tmConfDobj.getNoOfApplsForDealerPayment();
            } else {
                throw new VahanException("Something went wrong!!!");
            }

            if (cartCount >= noOfApplsForDealerPayment) {
                throw new VahanException("Cart should contains only " + noOfApplsForDealerPayment + " Application, please Rollback excess added applications to initiate Payment...");
            }

            tmgr = new TransactionManager("saveAddToCartDetails");

            this.insertIntoVpRcptCart(tmgr, feeDobj, applNo, Long.parseLong(empCode), stateCd, offCd, null, false, false);

            int purCd = 0;
            int actionCd = 0;
            PreparedStatement psSelectVaStaus = tmgr.prepareStatement(vaStatusSelectSql);
            psSelectVaStaus.setString(1, applNo);
            RowSet rsSelectVaStatus = tmgr.fetchDetachedRowSet_No_release();
            if (rsSelectVaStatus.next()) {
                purCd = rsSelectVaStatus.getInt("pur_cd");
                actionCd = rsSelectVaStatus.getInt("action_cd");
            }

            status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
            status.setAppl_no(applNo);
            status.setStatus(TableConstants.STATUS_COMPLETE);
            status.setOffice_remark("");
            status.setPublic_remark("");
            status.setPur_cd(purCd);
            status.setEmp_cd(Long.parseLong(Util.getEmpCode()));

            long ttAmount = this.getTotalAmtForOnlinePayment(tmgr, applNo, Long.parseLong(empCode), stateCd, offCd);

            if (ttAmount <= 0l || checkTotalAmount <= 0l) {
                throw new VahanException("Total amount should not be zero.");
            }
            if (ttAmount == checkTotalAmount) {
                ServerUtil.webServiceForNextStage(status, TableConstants.STATUS_COMPLETE, null, feeDobj.getApplNo(), actionCd, purCd, null);
                ServerUtil.fileFlow(tmgr, status);
                tmgr.commit();
            } else {
                throw new VahanException("Problem in Add to Cart!");
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in saving FeeDetails !!!");
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

    public List<FeeDobj> getIsTransWise(String StateCd) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        List<FeeDobj> listTransWise = new ArrayList<FeeDobj>();
        String vaStatusSelectSql = " select service_charges_per_rcpt,service_charges_per_trans from " + TableList.TM_CONFIGURATION + " where state_cd=?";
        try {
            tmgr = new TransactionManager("getIsTransWise");
            ps = tmgr.prepareStatement(vaStatusSelectSql);
            ps.setString(1, StateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                FeeDobj fee = new FeeDobj();
                fee.setPerRcpt(rs.getBoolean("service_charges_per_rcpt"));
                fee.setPerTrans(rs.getBoolean("service_charges_per_trans"));
                listTransWise.add(fee);
            }
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
        return listTransWise;
    }

//    public static Map<String, Object> getPreviousActionCode(int current_action_cd, int pur_cd) throws VahanException {
//        PreparedStatement ps = null;
//        TransactionManager tmgr = null;
//        String sql = null;
//        Map<String, Object> prevActionLabelValue = new HashMap<String, Object>();
//
//        try {
//            tmgr = new TransactionManager("getPreviousActionCode");
//
//            sql = "select  action_cd,action_abbrv from vvv_purpose_action_flow \n"
//                    + " where flow_srno = (select a.flow_srno \n"
//                    + " from tm_purpose_action_flow a \n"
//                    + " where a.state_cd = ? and a.action_cd = ? ) - 1 and  pur_cd = ? and state_cd = ? ";
//
//            ps = tmgr.prepareStatement(sql);
//            ps.setString(1, Util.getUserStateCode());
//            ps.setInt(2, current_action_cd);
//            ps.setInt(3, pur_cd);
//            ps.setString(4, Util.getUserStateCode());
//
//            RowSet rs = tmgr.fetchDetachedRowSet();
//
//            while (rs.next()) {
//                prevActionLabelValue.put(rs.getString("action_abbrv"), rs.getString("action_cd"));
//            }
//
//        } catch (SQLException sqle) {
//            LOGGER.error(sqle);
//            throw new VahanException(sqle.getMessage());
//        } finally {
//            try {
//                if (tmgr != null) {
//                    tmgr.release();
//                }
//            } catch (Exception e) {
//                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
//                throw new VahanException(e.getMessage());
//            }
//        }
//
//        return prevActionLabelValue;
//    }
    public static void revertBackForRectification(Status_dobj status_dobj) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("reverBackForRectification");

            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);

            status_dobj.setOffice_remark("Revert Back For Rectification");
            status_dobj.setPublic_remark("");
            status_dobj.setCntr_id("");
            status_dobj.setEmp_cd(Long.parseLong(Util.getEmpCode()));
            status_dobj.setOff_cd(Util.getUserOffCode());

            ServerUtil.fileFlow(tmgr, status_dobj);
            tmgr.commit();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
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

    public boolean skipPurCodeFee(String stateCd, int offCd, String applNo, int skipParticularPurCd, String skipFromListOfPurCd) throws VahanException {
        boolean isTrue = false;
        TransactionManager tmgr = null;
        int count = 0;
        String sql = "SELECT pur_cd FROM " + TableList.VA_STATUS + " WHERE ? in (" + skipFromListOfPurCd + ") and pur_cd in (" + skipFromListOfPurCd + ") and appl_no=? and state_cd=? and off_cd=?";
        try {
            tmgr = new TransactionManager("skipPurCodeFee");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setInt(1, skipParticularPurCd);
            ps.setString(2, applNo);
            ps.setString(3, stateCd);
            ps.setInt(4, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                count++;
            }
            if (count > 1) {//for handling only one application with one pur_cd
                isTrue = true;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong During Fee Skip Process.Please Contact to the System Administrator.");
        }
        return isTrue;
    }

    public boolean getOnlinePayData(String applNo) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        boolean isData = false;
        String selectSql = " select * from " + TableList.VP_RCPT_CART + " where appl_no  = ? ";
        try {
            tmgr = new TransactionManager("getOnlinePayData");
            ps = tmgr.prepareStatement(selectSql);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isData = true;
            }
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
        return isData;
    }

    public void saveFinePaneltyExemDetails(String applNo, String rcptNo, TransactionManager tmgr, String userCode) throws SQLException {
        PreparedStatement ps;
        String query = "INSERT INTO " + TableList.VT_FEE_EXEMTION + "("
                + "            state_cd, off_cd, rcpt_no, pur_cd, fee_exempted, fine_exempted, "
                + "            order_no, order_dt, exemtion_reason, rcpt_dt)"
                + "    SELECT state_cd, off_cd, ? as rcpt_no, pur_cd, fee_exempted, fine_exempted, "
                + "       order_no, order_dt, exemtion_reason,current_timestamp"
                + "  FROM " + TableList.VA_FEE_EXEMTION + " Where appl_no = ? and pur_cd in (251,252,253,27,39)";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, rcptNo);
        ps.setString(2, applNo);
        ps.executeUpdate();

        ExemptionFeeFineImpl implObj = new ExemptionFeeFineImpl();
        implObj.moveDataIntoHistory(tmgr, applNo, userCode);

        query = "Delete from " + TableList.VA_FEE_EXEMTION + " Where appl_no =? and pur_cd in (251,252,253,27,39)";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, applNo);
        ps.executeUpdate();

    }

    public boolean getTmPurposeActionFlowCondtion(String stateCd, int purCd, int actionCd, VehicleParameters vehParameters) throws VahanException {
        boolean condition = false;
        TransactionManager tmgr = null;
        try {
            if (vehParameters != null) {
                tmgr = new TransactionManager("getTmPurposeActionFlowCondtion");
                String sql = "SELECT condition_formula FROM " + TableList.TM_PURPOSE_ACTION_FLOW
                        + " WHERE state_cd=? and pur_cd=? and action_cd=? limit 1";
                PreparedStatement ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCd);
                ps.setInt(2, purCd);
                ps.setInt(3, actionCd);
                RowSet rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getTmPurposeActionFlowCondtion")) {
                        condition = true;
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong when getting Condtion of File Flow, Please Contact to System Administrator");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return condition;
    }

    public List getExemptedPurposeAmount(long exemptedPurAmount, long purposeAmount) {
        List amount = new ArrayList();
        long remainPurposeAmount = 0l;
        long remainExemptedPurAmount = 0l;
        if (purposeAmount > exemptedPurAmount) {
            remainPurposeAmount = purposeAmount - exemptedPurAmount;
        } else {
            remainExemptedPurAmount = exemptedPurAmount - purposeAmount;
        }
        amount.add(remainPurposeAmount);
        amount.add(remainExemptedPurAmount);
        return amount;
    }

    public void insertIntoVpRcptCart(TransactionManager tmgr, Fee_Pay_Dobj feeDobj, String applNo, long userCd, String stateCd, int offCd, String payType, boolean isManualRcptAttach, boolean isFeeFineExemptionAllow) throws VahanException, SQLException, Exception {
        StringBuilder taxNoAdvUnits = null;
        PassengerPermitDetailDobj permitDobj = feeDobj.getPermitDobj();
        long feeFineAmount = 0l;
        long taxPenaltyAmount = 0l;
        long taxInterestAmount = 0l;
        if (isFeeFineExemptionAllow) {
            for (FeeDobj feePurDobj : feeDobj.getCollectedFeeList()) {
                if (TableConstants.FEE_FINE_EXEMTION == feePurDobj.getPurCd()) {
                    feeFineAmount = -feePurDobj.getFineAmount();
                } else if (TableConstants.TAX_PENALTY_EXEMTION == feePurDobj.getPurCd()) {
                    taxPenaltyAmount = -feePurDobj.getFineAmount();
                } else if (TableConstants.TAX_INTEREST_EXEMTION == feePurDobj.getPurCd()) {
                    taxInterestAmount = -feePurDobj.getFineAmount();
                }
            }
        }
        for (FeeDobj feePurDobj : feeDobj.getCollectedFeeList()) {
            String sql = "SELECT * FROM " + TableList.VP_RCPT_CART + " WHERE appl_no=? and pur_cd=? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setInt(2, feePurDobj.getPurCd());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                throw new VahanException("Duplicate records found !!!");
            }

            if (feePurDobj.getPurCd() != -1) {
                if ((isManualRcptAttach && feePurDobj.getPurCd() == TableConstants.VM_MAST_MANUAL_RECEIPT) || TableConstants.FEE_FINE_EXEMTION == feePurDobj.getPurCd()) {
                    continue;
                }

                String addTocartSql = "INSERT INTO vp_rcpt_cart(state_cd,off_cd,user_cd, appl_no, pur_cd, period_mode, period_from, period_upto,"
                        + " amount, exempted, rebate, surcharge, penalty, interest,pmt_type, pmt_catg, service_type, route_class,"
                        + " route_length, no_of_trips, domain_cd, distance_run_in_quarter,op_dt,no_adv_units,tax1,tax2,prv_adjustment) "
                        + " VALUES (?,?,?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,current_timestamp,?,?,?,?)";

                PreparedStatement psAddtoCart = tmgr.prepareStatement(addTocartSql);
                int i = 1;
                psAddtoCart.setString(i++, stateCd);
                if (offCd > 0) {
                    psAddtoCart.setInt(i++, offCd);
                } else {
                    throw new VahanException("Problem occurred during adding into cart, Please go to home page and try again.");
                }
                psAddtoCart.setLong(i++, userCd);
                psAddtoCart.setString(i++, applNo);
                psAddtoCart.setInt(i++, feePurDobj.getPurCd());
                psAddtoCart.setString(i++, null);
                psAddtoCart.setDate(i++, null);
                psAddtoCart.setDate(i++, null);
                psAddtoCart.setLong(i++, feePurDobj.getFeeAmount());
                psAddtoCart.setLong(i++, 0);
                psAddtoCart.setLong(i++, 0);
                psAddtoCart.setLong(i++, 0);
                if (feeFineAmount > 0L) {
                    List amount = getExemptedPurposeAmount(feeFineAmount, feePurDobj.getFineAmount());
                    long insertAmount = (long) amount.get(0);
                    feeFineAmount = (long) amount.get(1);
                    psAddtoCart.setLong(i++, insertAmount);//fine
                } else {
                    psAddtoCart.setLong(i++, feePurDobj.getFineAmount());//fine
                }
                psAddtoCart.setLong(i++, 0);//fine
                if (permitDobj != null && permitDobj.getPmt_type_code() != null && !permitDobj.getPmt_type_code().equals("")) {
                    psAddtoCart.setInt(i++, Integer.parseInt(permitDobj.getPmt_type_code()));
                } else {
                    psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                }

                if (permitDobj != null && permitDobj.getPmtCatg() != null && !permitDobj.getPmtCatg().equals("")) {
                    psAddtoCart.setInt(i++, Integer.parseInt(permitDobj.getPmtCatg()));
                } else {
                    psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                }

                if (permitDobj != null && permitDobj.getServices_TYPE() != null && !permitDobj.getServices_TYPE().equals("")) {
                    psAddtoCart.setInt(i++, Integer.parseInt(permitDobj.getServices_TYPE()));
                } else {
                    psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                }

                if (permitDobj != null && permitDobj.getRout_code() != null && !permitDobj.getRout_code().equals("")) {
                    psAddtoCart.setInt(i++, Integer.parseInt(permitDobj.getRout_code()));
                } else {
                    psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                }

                if (permitDobj != null && permitDobj.getRout_length() != null && !permitDobj.getRout_length().equals("")) {
                    psAddtoCart.setInt(i++, Integer.parseInt(permitDobj.getRout_length()));
                } else {
                    psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                }

                if (permitDobj != null && permitDobj.getNumberOfTrips() != null && !permitDobj.getNumberOfTrips().equals("")) {
                    psAddtoCart.setInt(i++, Integer.parseInt(permitDobj.getNumberOfTrips()));
                } else {
                    psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                }

                if (permitDobj != null && permitDobj.getDomain_CODE() != null && !permitDobj.getDomain_CODE().equals("")) {
                    psAddtoCart.setInt(i++, Integer.parseInt(permitDobj.getDomain_CODE()));
                } else {
                    psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                }

                psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                psAddtoCart.setString(i++, null);
                psAddtoCart.setLong(i++, 0);
                psAddtoCart.setLong(i++, 0);
                psAddtoCart.setLong(i++, 0);
                ServerUtil.validateQueryResult(tmgr, psAddtoCart.executeUpdate(), psAddtoCart);

                if (feePurDobj.getFromDate() != null || feePurDobj.getUptoDate() != null) {
                    String vpCartFeeBreakUpSql = "INSERT INTO " + TableList.VP_CART_FEE_BREAKUP + "( "
                            + " state_cd, off_cd, appl_no, sr_no, fee_from, fee_upto, pur_cd, "
                            + " fee, fine, exempted, rebate, surcharge, interest, prv_adjustment,"
                            + "  op_dt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,current_timestamp)";

                    PreparedStatement psVpCartFeeBreakUpSql = tmgr.prepareStatement(vpCartFeeBreakUpSql);
                    int k = 1;
                    psVpCartFeeBreakUpSql.setString(k++, stateCd);
                    psVpCartFeeBreakUpSql.setInt(k++, offCd);
                    psVpCartFeeBreakUpSql.setString(k++, applNo);
                    psVpCartFeeBreakUpSql.setInt(k++, 1);
                    psVpCartFeeBreakUpSql.setDate(k++, new java.sql.Date(feePurDobj.getFromDate().getTime()));
                    if (feePurDobj.getUptoDate() == null) {
                        psVpCartFeeBreakUpSql.setNull(k++, java.sql.Types.DATE);
                    } else {
                        psVpCartFeeBreakUpSql.setDate(k++, new java.sql.Date(feePurDobj.getUptoDate().getTime()));
                    }

                    psVpCartFeeBreakUpSql.setInt(k++, feePurDobj.getPurCd());
                    psVpCartFeeBreakUpSql.setLong(k++, feePurDobj.getFeeAmount());
                    psVpCartFeeBreakUpSql.setLong(k++, feePurDobj.getFineAmount());
                    psVpCartFeeBreakUpSql.setInt(k++, 0);
                    psVpCartFeeBreakUpSql.setInt(k++, 0);
                    psVpCartFeeBreakUpSql.setInt(k++, 0);
                    psVpCartFeeBreakUpSql.setInt(k++, 0);
                    psVpCartFeeBreakUpSql.setInt(k++, 0);
                    ServerUtil.validateQueryResult(tmgr, psVpCartFeeBreakUpSql.executeUpdate(), psVpCartFeeBreakUpSql);
                }
            }
        }
        if (feeDobj.getListTaxDobj() != null) {
            for (Tax_Pay_Dobj taxDobj : feeDobj.getListTaxDobj()) {
                if (taxDobj.getPur_cd() != -1) {
                    if (TableConstants.TAX_PENALTY_EXEMTION == taxDobj.getPur_cd() || TableConstants.TAX_INTEREST_EXEMTION == taxDobj.getPur_cd()) {
                        continue;
                    }
                    String addTocartSql = "INSERT INTO " + TableList.VP_RCPT_CART + "(state_cd,off_cd,user_cd, appl_no, pur_cd, period_mode, period_from, period_upto,"
                            + " amount, exempted, rebate, surcharge, penalty, interest,pmt_type, pmt_catg, service_type, route_class,"
                            + " route_length, no_of_trips, domain_cd, distance_run_in_quarter,op_dt,no_adv_units,tax1,tax2,prv_adjustment) "
                            + " VALUES (?,?,?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,current_timestamp,?,?,?,?)";

                    PreparedStatement pstmtVtTax = tmgr.prepareStatement(addTocartSql);
                    int i = 1;
                    pstmtVtTax.setString(i++, stateCd);
                    pstmtVtTax.setInt(i++, offCd);
                    pstmtVtTax.setLong(i++, userCd);
                    pstmtVtTax.setString(i++, applNo);
                    pstmtVtTax.setInt(i++, taxDobj.getPur_cd());
                    if (!CommonUtils.isNullOrBlank(payType) && TableConstants.TAX_MODE_BALANCE.equalsIgnoreCase(payType)) {
                        pstmtVtTax.setString(i++, TableConstants.TAX_MODE_BALANCE);
                    } else {
                        pstmtVtTax.setString(i++, taxDobj.getTaxMode());
                    }
                    if (taxDobj.getFinalTaxFrom() != null) {
                        pstmtVtTax.setDate(i++, new java.sql.Date(DateUtils.parseDate(taxDobj.getFinalTaxFrom()).getTime()));
                    } else {
                        pstmtVtTax.setNull(i++, java.sql.Types.DATE);//tax_from
                    }
                    if (taxDobj.getFinalTaxUpto() != null) {
                        pstmtVtTax.setDate(i++, new java.sql.Date(DateUtils.parseDate(taxDobj.getFinalTaxUpto()).getTime()));
                    } else {
                        pstmtVtTax.setNull(i++, java.sql.Types.DATE);//tax_upto
                    }
                    pstmtVtTax.setLong(i++, taxDobj.getTotalPaybaleTax());
                    pstmtVtTax.setLong(i++, 0);
                    pstmtVtTax.setLong(i++, taxDobj.getTotalPaybaleRebate());
                    pstmtVtTax.setLong(i++, taxDobj.getTotalPaybaleSurcharge());
                    if (taxDobj.getPur_cd() == TableConstants.TM_ROAD_TAX && (taxInterestAmount > 0L || taxPenaltyAmount > 0L)) {
                        if (taxPenaltyAmount > 0L) {
                            pstmtVtTax.setLong(i++, taxDobj.getTotalPaybalePenalty() - taxPenaltyAmount);
                            taxPenaltyAmount = 0L;
                        } else {
                            pstmtVtTax.setLong(i++, taxDobj.getTotalPaybalePenalty());
                        }
                        if (taxInterestAmount > 0L) {
                            pstmtVtTax.setLong(i++, taxDobj.getTotalPaybaleInterest() - taxInterestAmount);
                            taxInterestAmount = 0L;
                        } else {
                            pstmtVtTax.setLong(i++, taxDobj.getTotalPaybaleInterest());
                        }
                    } else {
                        pstmtVtTax.setLong(i++, taxDobj.getTotalPaybalePenalty());
                        pstmtVtTax.setLong(i++, taxDobj.getTotalPaybaleInterest());
                    }

                    if (permitDobj != null && permitDobj.getPmt_type_code() != null && !permitDobj.getPmt_type_code().equals("")) {
                        pstmtVtTax.setInt(i++, Integer.parseInt(permitDobj.getPmt_type_code()));
                    } else {
                        pstmtVtTax.setNull(i++, java.sql.Types.INTEGER);
                    }

                    if (permitDobj != null && permitDobj.getPmtCatg() != null && !permitDobj.getPmtCatg().equals("")) {
                        pstmtVtTax.setInt(i++, Integer.parseInt(permitDobj.getPmtCatg()));
                    } else {
                        pstmtVtTax.setNull(i++, java.sql.Types.INTEGER);
                    }

                    if (permitDobj != null && permitDobj.getServices_TYPE() != null && !permitDobj.getServices_TYPE().equals("")) {
                        pstmtVtTax.setInt(i++, Integer.parseInt(permitDobj.getServices_TYPE()));
                    } else {
                        pstmtVtTax.setNull(i++, java.sql.Types.INTEGER);
                    }

                    if (permitDobj != null && permitDobj.getRout_code() != null && !permitDobj.getRout_code().equals("")) {
                        pstmtVtTax.setInt(i++, Integer.parseInt(permitDobj.getRout_code()));
                    } else {
                        pstmtVtTax.setNull(i++, java.sql.Types.INTEGER);
                    }

                    if (permitDobj != null && permitDobj.getRout_length() != null && !permitDobj.getRout_length().equals("")) {
                        pstmtVtTax.setInt(i++, Integer.parseInt(permitDobj.getRout_length()));
                    } else {
                        pstmtVtTax.setNull(i++, java.sql.Types.INTEGER);
                    }

                    if (permitDobj != null && permitDobj.getNumberOfTrips() != null && !permitDobj.getNumberOfTrips().equals("")) {
                        pstmtVtTax.setInt(i++, Integer.parseInt(permitDobj.getNumberOfTrips()));
                    } else {
                        pstmtVtTax.setNull(i++, java.sql.Types.INTEGER);
                    }

                    if (permitDobj != null && permitDobj.getDomain_CODE() != null && !permitDobj.getDomain_CODE().equals("")) {
                        pstmtVtTax.setInt(i++, Integer.parseInt(permitDobj.getDomain_CODE()));
                    } else {
                        pstmtVtTax.setNull(i++, java.sql.Types.INTEGER);
                    }

                    pstmtVtTax.setNull(i++, java.sql.Types.INTEGER);//ps.setInt(i++, permitDobj != null && permitDobj.getDistance_run_in_quarter());
                    pstmtVtTax.setString(i++, null);
                    pstmtVtTax.setLong(i++, (long) taxDobj.getTotalPaybaleTax1());
                    pstmtVtTax.setLong(i++, (long) taxDobj.getTotalPaybaleTax2());
                    pstmtVtTax.setLong(i++, (long) taxDobj.getPreviousAdj());
                    ServerUtil.validateQueryResult(tmgr, pstmtVtTax.executeUpdate(), pstmtVtTax);

                    int srNo = 0;
                    for (DOTaxDetail taxBrkUpDobj : taxDobj.getTaxBreakDetails()) {

                        int j = 1;
                        String addTocartBreakUpSql = "INSERT INTO " + TableList.VP_CART_TAX_BREAKUP + "("
                                + "            state_cd, off_cd, appl_no, sr_no, tax_from, tax_upto, pur_cd, \n"
                                + "            prv_adjustment, tax, exempted, rebate, surcharge, penalty, interest,tax1,tax2,op_dt)\n"
                                + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,current_timestamp)";

                        PreparedStatement pstmtVtTaxBrkUp = tmgr.prepareStatement(addTocartBreakUpSql);
                        java.sql.Timestamp taxFrom = null;
                        java.sql.Timestamp taxUpto = null;
                        taxFrom = new java.sql.Timestamp(DateUtils.parseDate(taxBrkUpDobj.getTAX_FROM()).getTime());

                        srNo++;
                        pstmtVtTaxBrkUp.setString(j++, stateCd);
                        pstmtVtTaxBrkUp.setInt(j++, offCd);//   off_cd
                        pstmtVtTaxBrkUp.setString(j++, applNo);//rcpt_no
                        pstmtVtTaxBrkUp.setInt(j++, srNo);//sr_no
                        pstmtVtTaxBrkUp.setTimestamp(j++, taxFrom);//tax_from
                        if (taxBrkUpDobj.getTAX_UPTO() != null) {
                            taxUpto = new java.sql.Timestamp(DateUtils.parseDate(taxBrkUpDobj.getTAX_UPTO()).getTime());
                            pstmtVtTaxBrkUp.setTimestamp(j++, taxUpto);//tax_Upto
                        } else {
                            pstmtVtTaxBrkUp.setNull(j++, java.sql.Types.DATE);//tax_upto
                        }
                        pstmtVtTaxBrkUp.setInt(j++, taxBrkUpDobj.getPUR_CD());//pur_cd
                        pstmtVtTaxBrkUp.setLong(j++, taxBrkUpDobj.getPRV_ADJ());//prv_adjustment
                        pstmtVtTaxBrkUp.setDouble(j++, taxBrkUpDobj.getAMOUNT());//tax
                        pstmtVtTaxBrkUp.setLong(j++, 0);//exempted
                        pstmtVtTaxBrkUp.setDouble(j++, taxBrkUpDobj.getREBATE());//rebate
                        pstmtVtTaxBrkUp.setDouble(j++, taxBrkUpDobj.getSURCHARGE());//surcharge
                        pstmtVtTaxBrkUp.setDouble(j++, taxBrkUpDobj.getPENALTY());//penalty
                        pstmtVtTaxBrkUp.setDouble(j++, taxBrkUpDobj.getINTEREST());//interest
                        pstmtVtTaxBrkUp.setDouble(j++, taxBrkUpDobj.getAMOUNT1());//tax1
                        pstmtVtTaxBrkUp.setDouble(j++, taxBrkUpDobj.getAMOUNT2());//tax2
                        ServerUtil.validateQueryResult(tmgr, pstmtVtTaxBrkUp.executeUpdate(), pstmtVtTaxBrkUp);
                    }
                    if (taxNoAdvUnits == null) {
                        taxNoAdvUnits = new StringBuilder();
                        taxNoAdvUnits.append(taxDobj.getPur_cd()).append("=").append(taxDobj.getNoOfAdvUnits());
                    } else {
                        taxNoAdvUnits.append(",").append(taxDobj.getPur_cd()).append("=").append(taxDobj.getNoOfAdvUnits());
                    }
                }
            }
        }
        if (feeDobj.getListTaxDobj() != null && taxNoAdvUnits != null && !CommonUtils.isNullOrBlank(taxNoAdvUnits.toString())) {
            String updateSql = "Update  vp_rcpt_cart set no_adv_units = ?  where appl_no = ? and user_cd = ? and state_cd = ? and off_cd = ? ";
            PreparedStatement psUpdate = tmgr.prepareStatement(updateSql);
            psUpdate.setString(1, taxNoAdvUnits.toString());
            psUpdate.setString(2, applNo);
            psUpdate.setLong(3, userCd);
            psUpdate.setString(4, stateCd);
            psUpdate.setInt(5, offCd);
            ServerUtil.validateQueryResult(tmgr, psUpdate.executeUpdate(), psUpdate);
        }
    }

    private long getTotalAmtForOnlinePayment(TransactionManager tmgr, String applNo, long userCd, String stateCd, int offCd) throws SQLException, Exception {
        long amount = 0l;
        String selectSql = "select sum(vp.amount+vp.surcharge+vp.penalty+vp.interest+vp.tax1+vp.tax2+vp.prv_adjustment-vp.exempted-vp.rebate) as amt \n"
                + " from vp_rcpt_cart vp \n"
                + " where vp.appl_no = ? and vp.state_cd = ? and vp.off_cd = ? and vp.user_cd= ? ";
        PreparedStatement psSelectVpRcptCart = tmgr.prepareStatement(selectSql);
        int i = 1;
        psSelectVpRcptCart.setString(i++, applNo);
        psSelectVpRcptCart.setString(i++, stateCd);
        psSelectVpRcptCart.setInt(i++, offCd);
        psSelectVpRcptCart.setLong(i++, userCd);
        RowSet rsSelectVpRcptcart = tmgr.fetchDetachedRowSet_No_release();
        if (rsSelectVpRcptcart.next()) {
            amount = rsSelectVpRcptcart.getLong("amt");
        }
        return amount;
    }

    public UserIDAndPasswordForOnlinePaymentDobj getUserIDAndPasswordForOnlinePayment(String stateCd, int offCd, String appl_no, String ownerName, Long mobileNo, String regnNo, String selectPayType) throws VahanException, Exception {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        UserIDAndPasswordForOnlinePaymentDobj obj = null;
        String whereCon = "and vpb.appl_no = ? ";
        try {
            tmgr = new TransactionManager("getUserIDAndPasswordForOnlinePayment");
            if ("R".equals(selectPayType) || "O".equals(selectPayType)) {
                whereCon = " and regn_no = ? ";
            } else if ("C".equals(selectPayType)) {
                whereCon = " and vpo.mobile_no = ? ";
            }
            String sql = "select distinct vpo.user_cd,vpo.user_pwd,vpo.appl_no, vp.transaction_no from " + TableList.VP_ONLINE_PAY_BALANCE_FEE_DETAILS + " vpb inner join " + TableList.VP_RCPT_CART + " vp "
                    + " on vpb.state_cd = vp.state_cd and vpb.off_cd= vp.off_cd and vpb.user_cd = vp.user_cd inner join " + TableList.VP_ONLINE_PAY_USER_INFO + " vpo "
                    + " on vpb.state_cd = vpo.state_cd and vpb.off_cd= vpo.off_cd and vpb.user_cd = vpo.user_cd and vpb.appl_no = vpo.appl_no "
                    + " where vpb.state_cd= ? and vpb.off_cd = ? " + whereCon + " ";
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            if ("R".equals(selectPayType) || "O".equals(selectPayType) || "D".equals(selectPayType)) {
                ps.setString(i++, regnNo);
            } else if ("C".equals(selectPayType)) {
                ps.setLong(i++, mobileNo);
            } else if ("A".equals(selectPayType)) {
                ps.setString(i++, appl_no);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                obj = new UserIDAndPasswordForOnlinePaymentDobj();
                obj.setUser_pwd(rs.getString("user_pwd"));
                obj.setUser_cd(rs.getLong("user_cd"));
                obj.setAppl_no(rs.getString("appl_no"));
                obj.setTransactionNo(rs.getString("transaction_no"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting Details. Please contact Administrator!!!");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return obj;
    }

    public List<TaxFormPanelBean> getOnlineFeeTaxDetails(String stateCd, int offCd, String applNo) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        List<TaxFormPanelBean> dobjList = new ArrayList<>();
        try {
            tmgr = new TransactionManagerReadOnly("getOnlineFeeTaxDetails");
            String sql = "select b.descr as purpose, to_char(a.period_from, 'dd-Mon-yyyy') as period_from, to_char(a.period_upto, 'dd-Mon-yyyy') "
                    + " as period_upto, (amount + surcharge + interest + tax1 + tax2 + penalty + prv_adjustment - exempted - rebate) as totalamount,amount,surcharge ,interest,"
                    + " tax1,tax2, penalty as fine, a.pur_cd,rebate,exempted,prv_adjustment from vp_rcpt_cart a left outer join tm_purpose_mast b on b.pur_cd = a.pur_cd "
                    + "where a.state_cd= ? and a.off_cd = ? and  a.appl_no = ? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                TaxFormPanelBean dobj = new TaxFormPanelBean();
                dobj.setFinalTaxAmount(rs.getLong("totalamount"));
                dobj.setTotalPayableAmount(rs.getLong("amount"));
                dobj.setTotalPaybaleSurcharge(rs.getLong("surcharge"));
                dobj.setTotalPaybaleInterest(rs.getLong("interest"));
                dobj.setTotalTax1(rs.getLong("tax1"));
                dobj.setTotalTax2(rs.getLong("tax2"));
                dobj.setTotalPaybalePenalty(rs.getLong("fine"));
                dobj.setPur_cd(rs.getInt("pur_cd"));
                dobj.setTotalPaybaleRebate(rs.getLong("rebate"));
                dobj.setFinalTaxFrom(rs.getString("period_from"));
                dobj.setFinalTaxUpto(rs.getString("period_upto"));
                dobj.setTotalPayablePrvAdj(rs.getLong("prv_adjustment"));
                dobjList.add(dobj);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem in getting Online Fee Tax details.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return dobjList;
    }

    public List<OnlinePayDobj> moveFileWithZeroAmtAtDealerPoint(Fee_Pay_Dobj feeDobj, String applNo, String empCode, String stateCd, int offCd, long checkTotalAmount) throws VahanException {
        Status_dobj status = new Status_dobj();
        TransactionManager tmgr = null;
        List<OnlinePayDobj> onlinePayDbjList = null;
        try {
            tmgr = new TransactionManager("moveFileWithZeroAmtAtDealerPoint");
            this.insertIntoVpRcptCart(tmgr, feeDobj, applNo, Long.parseLong(empCode), stateCd, offCd, null, false, false);
            Object[] validateTransNo = new PaymentGatewayImpl().updateTransactionNumber(tmgr, stateCd, "'" + applNo + "'");
            Integer updatedCount = (Integer) validateTransNo[0];
            String transNo = (String) validateTransNo[1];
            if (updatedCount > 0 && !transNo.equals("")) {
                int purCd = 0;
                int actionCd = 0;
                String vaStatusSelectSql = "select * from va_status where appl_no=?";
                PreparedStatement psSelectVaStaus = tmgr.prepareStatement(vaStatusSelectSql);
                psSelectVaStaus.setString(1, applNo);
                RowSet rsSelectVaStatus = tmgr.fetchDetachedRowSet_No_release();
                if (rsSelectVaStatus.next()) {
                    purCd = rsSelectVaStatus.getInt("pur_cd");
                    actionCd = rsSelectVaStatus.getInt("action_cd");
                }

                status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
                status.setAppl_no(applNo);
                status.setStatus(TableConstants.STATUS_COMPLETE);
                status.setOffice_remark("");
                status.setPublic_remark("");
                status.setPur_cd(purCd);
                status.setEmp_cd(Long.parseLong(Util.getEmpCode()));
                ServerUtil.webServiceForNextStage(status, TableConstants.STATUS_COMPLETE, null, feeDobj.getApplNo(), actionCd, purCd, null);
                ServerUtil.fileFlow(tmgr, status);
                Util.getSelectedSeat().setAction_cd(TableConstants.TM_ROLE_DEALER_CART_PAYMENT);
                onlinePayDbjList = new OnlinePayImpl().getRecptNoRegnNoAndInsertDataForNewVehicles(tmgr, empCode, transNo, TableConstants.TM_ROLE_DEALER_CART_PAYMENT, stateCd, offCd, TableConstants.USER_CATG_DEALER);
                if (onlinePayDbjList != null && !onlinePayDbjList.isEmpty()) {
                    tmgr.commit();
                } else {
                    throw new VahanException("Problem in Generating Receipt!");
                }
            } else {
                throw new VahanException("Transaction No updation/generation failed!!!");
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Unable to save data with zero amount.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return onlinePayDbjList;
    }

    public OnlinePayDobj getOnlineFeeTaxDetail(String stateCd, int offCd, String applNo, Owner_dobj ownerDobj) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        OnlinePayDobj onlinePaydobj = new OnlinePayDobj();
        List<SelectItem> taxModeList = null;
        try {
            tmgr = new TransactionManagerReadOnly("getOnlineFeeTaxDetails");
            String sql = "select b.descr as purpose,a.period_mode , to_char(a.period_from, 'dd-Mon-yyyy') as period_from, to_char(a.period_upto, 'dd-Mon-yyyy') "
                    + " as period_upto, (amount + surcharge + interest + tax1 + tax2 + penalty - exempted - rebate) as totalamount,amount,surcharge ,interest,"
                    + " tax1,tax2, penalty as fine, a.pur_cd,rebate,exempted from vp_rcpt_cart a left outer join tm_purpose_mast b on b.pur_cd = a.pur_cd "
                    + " where a.state_cd= ? and a.off_cd = ? and  a.appl_no = ? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (!(rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_USER_CHARGES || rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_ALL)) {
                    TaxFormPanelBean dobj = new TaxFormPanelBean();
                    dobj.setTotalPaybaleTax(rs.getLong("amount"));
                    dobj.setTotalPayableAmount(rs.getLong("totalamount"));
                    dobj.setFinalTaxAmount(rs.getLong("totalamount"));
                    dobj.setTotalPaybaleSurcharge(rs.getLong("surcharge"));
                    dobj.setTotalPaybaleInterest(rs.getLong("interest"));
                    dobj.setTotalTax1(rs.getLong("tax1"));
                    dobj.setTotalTax2(rs.getLong("tax2"));
                    dobj.setTotalPaybalePenalty(rs.getLong("fine"));
                    dobj.setPur_cd(rs.getInt("pur_cd"));
                    dobj.setTotalPaybaleRebate(rs.getLong("rebate"));
                    dobj.setFinalTaxFrom(rs.getString("period_from"));
                    dobj.setFinalTaxUpto(rs.getString("period_upto"));
                    dobj.setTaxPurcdDesc(rs.getString("purpose"));
                    dobj.setTaxMode(rs.getString("period_mode"));
                    dobj.setDisableNoOfUnits(true);
                    taxModeList = new RoadTaxCollectionBean().getListTaxModes(ownerDobj, dobj.getPur_cd());
                    dobj.setListTaxModes(taxModeList);
                    onlinePaydobj.getTaxFormPanelBeanList().add(dobj);
                    // dobjList.add(dobj);
                } else {
                    EpayDobj dobj = new EpayDobj();
                    dobj.setPurCd(rs.getInt("pur_cd"));
                    dobj.setE_total(rs.getLong("totalamount"));
                    // dobj.setFineAmount(rs.getLong("totalamount"));
                    onlinePaydobj.getCommon_fee_chargeList().add(dobj);

                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem in getting Online Fee Tax details.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return onlinePaydobj;
    }

    public static void saveFeeFineExemptionDetails(FeeFineExemptionDobj dobj, TransactionManager tmgr) throws SQLException {
        PreparedStatement ps;
        String query = "INSERT INTO " + TableList.VT_FEE_FINE_EXEMPTION + "("
                + " state_cd, off_cd, regn_no, rcpt_no, rcpt_dt, pur_cd, fee_exempted,"
                + " fine_exempted) VALUES (?,?,?,?,current_timestamp,?,?,?)";
        ps = tmgr.prepareStatement(query);
        int i = 1;
        ps.setString(i++, dobj.getStateCd());
        ps.setInt(i++, dobj.getOffCd());
        ps.setString(i++, dobj.getRegnNo());
        ps.setString(i++, dobj.getRcptNo());
        ps.setInt(i++, dobj.getPurCd());
        ps.setLong(i++, dobj.getExemFeeAmount());
        ps.setLong(i++, dobj.getExemFineAmount());
        ps.executeUpdate();
    }
}
