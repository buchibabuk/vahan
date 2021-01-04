/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.form.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.fillVehicleParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.AltDobj;
import nic.vahan.form.dobj.AuditRecoveryDobj;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.dobj.ConvDobj;
import nic.vahan.form.dobj.ManualReceiptEntryDobj;
import nic.vahan.form.dobj.NonUseDobj;
import nic.vahan.form.dobj.OnlinePayDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitFeeDobj;
import nic.vahan.form.dobj.permit.PermitHomeAuthDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.impl.AltImpl;
import nic.vahan.form.impl.BlackListedVehicleImpl;
import nic.vahan.form.impl.ConvImpl;
import nic.vahan.form.impl.FeeImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.ManualReceiptEntryImpl;
import nic.vahan.form.impl.NewImpl;
import nic.vahan.form.impl.NonUseImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.TaxClearanceCertificatePrintImpl;
import nic.vahan.form.impl.TaxServer_Impl;
import nic.vahan.form.impl.ToImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.WalletImpl;
import nic.vahan.form.impl.audit.EntryAuditRecoveryImpl;
import nic.vahan.form.impl.dealer.PendencyBankDetailImpl;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.form.impl.permit.PermitFeeImpl;
import nic.vahan.form.impl.permit.PermitHomeAuthImpl;
import nic.vahan.form.impl.permit.PermitLOIImpl;
import nic.vahan.form.impl.permit.PermitOwnerDetailImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.NewVehicleNo;
import nic.vahan.server.ServerUtil;
import nic.vahan5.reg.commonutils.FormulaUtilities;
import nic.vahan5.reg.form.impl.dealer.PendencyBankDetailImplementation;
import nic.vahan5.reg.form.impl.permit.PassengerPermitDetailImplementation;
import nic.vahan5.reg.form.impl.permit.PermitOwnerDetailImplementation;
import nic.vahan5.reg.server.NewVehicleNumber;
import nic.vahan5.reg.server.ServerUtility;
import org.apache.log4j.Logger;

/**
 *
 * @author Kartikey Singh
 */
public class OnlinePayImplementation {

    private static final Logger LOGGER = Logger.getLogger(OnlinePayImplementation.class);

    /**
     * This function used to generate Receipt Number for each application and
     * call method used to insert data for (NEW VEHICLES)
     *
     * @param userId
     * @param transactionNo
     * @param actionCode
     * @param stateCd
     * @param offCd
     * @param userCatg
     * @return
     * @throws VahanException
     * @throws Exception
     */
    public List<OnlinePayDobj> getRecptNoRegnNoAndInsertDataForNewVehicles(String userId, String transactionNo, int actionCode, String stateCd, int offCd, String userCatg) throws VahanException {
        TransactionManager tmgr = null;
        List<OnlinePayDobj> payList = null;
        try {
            tmgr = new TransactionManager("getRecptNoRegnNoAndInsertDataForNewVehicle");
            payList = getRecptNoRegnNoAndInsertDataForNewVehicles(tmgr, userId, transactionNo, actionCode, stateCd, offCd, userCatg);
            if (payList != null && !payList.isEmpty()) {
                tmgr.commit();
            } else {
                throw new VahanException("Unable to proccess your request.Please Try Again");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception e) {
            LOGGER.error("TransactionNo " + transactionNo + " " + e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Unable to proccess your request.Please Try Again");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return payList;
    }

    public List<OnlinePayDobj> getRecptNoRegnNoAndInsertDataForNewVehicles(TransactionManager tmgr, String userId, String transactionNo, int actionCode, String stateCd, int offCd, String userCatg) throws VahanException, Exception {
        String regn_no = null;
        OwnerImpl ownerImpl = new OwnerImpl();
        Owner_dobj ownerDobj = new Owner_dobj();
        List<OnlinePayDobj> payList = null;
        List<OnlinePayDobj> onlinePayDbjList = new ArrayList<OnlinePayDobj>();
        java.sql.Timestamp rcptTimeStamp = null;
        try {
            if (CommonUtils.isNullOrBlank(userId)) {
                throw new VahanException("Session Expired. Please try again !!!");
            }

            String selectSql = " select distinct c.appl_no,s.pur_cd,c.pmt_type, c.pmt_catg, c.service_type, c.route_class,c.route_length, c.no_of_trips, c.domain_cd, c.distance_run_in_quarter,c.no_adv_units,COALESCE(pgi.rcpt_dt,CURRENT_DATE) AS rcpt_dt \n"
                    + " from vp_rcpt_cart c inner join va_status s  on c.appl_no = s.appl_no \n"
                    + " left outer join vahanpgi.vp_pgi_details pgi on c.transaction_no = pgi.payment_id \n"
                    + " where c.user_cd = ? and c.transaction_no = ? ";

            PreparedStatement psSelect = tmgr.prepareStatement(selectSql);
            psSelect.setLong(1, Long.parseLong(userId));
            psSelect.setString(2, transactionNo);

            RowSet rst = tmgr.fetchDetachedRowSet_No_release();
            int counter = 0;
            while (rst.next()) {
                boolean isValidForTemp = false;
                TmConfigurationDobj tmConf = Util.getTmConfiguration();
                counter++;
                if (counter == 1) {
                    rcptTimeStamp = new java.sql.Timestamp(rst.getDate("rcpt_dt").getTime());
                }
                if (userCatg != null && userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                    if (tmConf != null && tmConf.isTempFeeInNewRegis()) {
                        String sql = "select count(*) as noOfRec  from vp_rcpt_cart where appl_no = ? and pur_cd in (" + TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE + "," + TableConstants.VM_TRANSACTION_MAST_TEMP_REG + ")";
                        PreparedStatement ps = tmgr.prepareStatement(sql);
                        ps.setString(1, rst.getString("appl_no"));
                        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {
                            if ((rs.getInt("noOfRec") == 2)) {
                                isValidForTemp = true;
                            }
                        }
                    }
                }
                String reciptNo = ServerUtil.getUniqueOfficeRcptNo(tmgr, stateCd, offCd, TableConstants.DEALER_RCPT_FLAG, false);
                if (rst.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || rst.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                    NewVehicleNo newVehicleNo = new NewVehicleNo();
                    if (userCatg != null && userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                        if (tmConf != null && tmConf.isNum_gen_allowed_dealer()) {
                            if (ServerUtil.validateDealerNumGenAuth(tmgr, userId, stateCd, offCd)) {
                                regn_no = newVehicleNo.generateAssignNewRegistrationNo(offCd, actionCode, rst.getString("appl_no"), null, counter, null, null, tmgr);
                            }
                        } else {
                            regn_no = newVehicleNo.generateAssignNewRegistrationNo(offCd, actionCode, rst.getString("appl_no"), null, counter, null, null, tmgr);
                        }
                    } else {
                        regn_no = newVehicleNo.generateAssignNewRegistrationNo(offCd, actionCode, rst.getString("appl_no"), null, counter, null, null, tmgr);
                    }
                }
                if (userCatg != null && userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                    ownerDobj = ownerImpl.set_Owner_appl_db_to_dobj(null, rst.getString("appl_no"), "", TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE);
                } else {
                    ownerDobj = ownerImpl.set_Owner_appl_db_to_dobj(null, rst.getString("appl_no"), "", TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE);
                }
                List<OnlinePayDobj> taxBreakUpDetails = this.getTaxBreakUpDetails(rst.getString("appl_no"), tmgr);
                List<OnlinePayDobj> feeBreakUpDetails = this.getFeeBreakUpDetails(rst.getString("appl_no"), tmgr);
                if (regn_no != null) {
                    ownerDobj.setRegn_no(regn_no);
                    if (!"NEW".equals(regn_no) && rst.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                        ServerUtil.insertDealerHSRPPendencyDetails(tmgr, ownerDobj, regn_no, tmConf);
                    }
                }
                OnlinePayDobj onlinePayDobj = new OnlinePayDobj();
                onlinePayDobj.setReceiptNo(reciptNo);
                onlinePayDobj.setApplNo(rst.getString("appl_no"));
                onlinePayDobj.setOwnerDobj(ownerDobj);
                onlinePayDobj.setTaxBreakUpDetails(taxBreakUpDetails);
                onlinePayDobj.setFeeBreakUpDetails(feeBreakUpDetails);
                onlinePayDobj.setPurCd(rst.getInt("pur_cd"));
                onlinePayDobj.setPmt_type_code(rst.getString("pmt_type"));
                onlinePayDobj.setPmtCatg(rst.getString("pmt_catg"));
                onlinePayDobj.setServices_TYPE(rst.getString("service_type"));
                onlinePayDobj.setRout_code(rst.getString("route_class"));
                onlinePayDobj.setRout_length(rst.getString("route_length"));
                onlinePayDobj.setNumberOfTrips(rst.getString("no_of_trips"));
                onlinePayDobj.setDomain_CODE(rst.getString("domain_cd"));
                onlinePayDobj.setValidForTemp(isValidForTemp);
                onlinePayDobj.setNoOfAdvUnits(rst.getString("no_adv_units"));
                onlinePayDbjList.add(onlinePayDobj);
            }
            for (OnlinePayDobj pyDobj : onlinePayDbjList) {
                String updateSql = "Update  vp_rcpt_cart set rcpt_no = ? ,rcpt_dt = ?  where appl_no = ? and user_cd = ? and transaction_no = ? and transaction_no IS NOT NULL";
                PreparedStatement psUpdate = tmgr.prepareStatement(updateSql);
                psUpdate.setString(1, pyDobj.getReceiptNo());
                psUpdate.setTimestamp(2, rcptTimeStamp);
                psUpdate.setString(3, pyDobj.getApplNo());
                psUpdate.setLong(4, Long.parseLong(userId));
                psUpdate.setString(5, transactionNo);
                ServerUtil.validateQueryResult(tmgr, psUpdate.executeUpdate(), psUpdate);
            }
            payList = this.saveFeeDetailsAfterSuccesfullPayment(tmgr, transactionNo, onlinePayDbjList, actionCode, userId, stateCd, offCd, userCatg, "NewVehicle", rcptTimeStamp, null);
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("TransactionNo " + transactionNo + " " + e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Receipt Generation Failed !!!");
        }
        return payList;
    }

    /*
     * @author Kartikey Singh
     */
    public List<OnlinePayDobj> getRecptNoRegnNoAndInsertDataForNewVehicles(TransactionManager tmgr, String userId, String transactionNo, int actionCode,
            String stateCd, int offCd, String userCatg, TmConfigurationDobj tmConfigDobj, String empCode, int purCode, int userLoginOffCode,
            String selectedRoleCode, String clientIpAddress, String sessionUserId) throws VahanException, Exception {
        String regn_no = null;
        Owner_dobj ownerDobj = new Owner_dobj();
        List<OnlinePayDobj> payList = null;
        List<OnlinePayDobj> onlinePayDbjList = new ArrayList<OnlinePayDobj>();
        java.sql.Timestamp rcptTimeStamp = null;
        try {
            if (CommonUtils.isNullOrBlank(userId)) {
                throw new VahanException("Session Expired. Please try again !!!");
            }

            String selectSql = " select distinct c.appl_no,s.pur_cd,c.pmt_type, c.pmt_catg, c.service_type, c.route_class,c.route_length, c.no_of_trips, c.domain_cd, c.distance_run_in_quarter,c.no_adv_units,COALESCE(pgi.rcpt_dt,CURRENT_DATE) AS rcpt_dt \n"
                    + " from vp_rcpt_cart c inner join va_status s  on c.appl_no = s.appl_no \n"
                    + " left outer join vahanpgi.vp_pgi_details pgi on c.transaction_no = pgi.payment_id \n"
                    + " where c.user_cd = ? and c.transaction_no = ? ";

            PreparedStatement psSelect = tmgr.prepareStatement(selectSql);
            psSelect.setLong(1, Long.parseLong(userId));
            psSelect.setString(2, transactionNo);

            RowSet rst = tmgr.fetchDetachedRowSet_No_release();
            int counter = 0;
            while (rst.next()) {
                boolean isValidForTemp = false;
                TmConfigurationDobj tmConf = tmConfigDobj;
                counter++;
                if (counter == 1) {
                    rcptTimeStamp = new java.sql.Timestamp(rst.getDate("rcpt_dt").getTime());
                }
                if (userCatg != null && userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                    if (tmConf != null && tmConf.isTempFeeInNewRegis()) {
                        String sql = "select count(*) as noOfRec  from vp_rcpt_cart where appl_no = ? and pur_cd in (" + TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE + "," + TableConstants.VM_TRANSACTION_MAST_TEMP_REG + ")";
                        PreparedStatement ps = tmgr.prepareStatement(sql);
                        ps.setString(1, rst.getString("appl_no"));
                        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {
                            if ((rs.getInt("noOfRec") == 2)) {
                                isValidForTemp = true;
                            }
                        }
                    }
                }
                String reciptNo = ServerUtility.getUniqueOfficeRcptNo(tmgr, stateCd, offCd, TableConstants.DEALER_RCPT_FLAG, false, empCode);
                if (rst.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || rst.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                    NewVehicleNumber newVehicleNumber = new NewVehicleNumber();
                    if (userCatg != null && userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                        if (tmConf != null && tmConf.isNum_gen_allowed_dealer()) {
                            if (ServerUtility.validateDealerNumGenAuth(tmgr, userId, stateCd, offCd)) {
                                regn_no = newVehicleNumber.generateAssignNewRegistrationNo(offCd, actionCode, rst.getString("appl_no"), null, counter, null, null, tmgr,
                                        purCode, stateCd, userLoginOffCode, userCatg, empCode);
                            }
                        } else {
                            regn_no = newVehicleNumber.generateAssignNewRegistrationNo(offCd, actionCode, rst.getString("appl_no"), null, counter, null, null, tmgr,
                                    purCode, stateCd, userLoginOffCode, userCatg, empCode);
                        }
                    } else {
                        regn_no = newVehicleNumber.generateAssignNewRegistrationNo(offCd, actionCode, rst.getString("appl_no"), null, counter, null, null, tmgr,
                                purCode, stateCd, userLoginOffCode, userCatg, empCode);
                    }
                }
                if (userCatg != null && userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                    ownerDobj = new OwnerImplementation().set_Owner_appl_db_to_dobj(null, rst.getString("appl_no"), "", TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE,
                            stateCd, offCd);
                } else {
                    ownerDobj = new OwnerImplementation().set_Owner_appl_db_to_dobj(null, rst.getString("appl_no"), "", TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE,
                            stateCd, offCd);
                }
                List<OnlinePayDobj> taxBreakUpDetails = this.getTaxBreakUpDetails(rst.getString("appl_no"), tmgr);
                List<OnlinePayDobj> feeBreakUpDetails = this.getFeeBreakUpDetails(rst.getString("appl_no"), tmgr);
                if (regn_no != null) {
                    ownerDobj.setRegn_no(regn_no);
                    if (!"NEW".equals(regn_no) && rst.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                        ServerUtility.insertDealerHSRPPendencyDetails(tmgr, ownerDobj, regn_no, tmConf);
                    }
                }
                OnlinePayDobj onlinePayDobj = new OnlinePayDobj();
                onlinePayDobj.setReceiptNo(reciptNo);
                onlinePayDobj.setApplNo(rst.getString("appl_no"));
                onlinePayDobj.setOwnerDobj(ownerDobj);
                onlinePayDobj.setTaxBreakUpDetails(taxBreakUpDetails);
                onlinePayDobj.setFeeBreakUpDetails(feeBreakUpDetails);
                onlinePayDobj.setPurCd(rst.getInt("pur_cd"));
                onlinePayDobj.setPmt_type_code(rst.getString("pmt_type"));
                onlinePayDobj.setPmtCatg(rst.getString("pmt_catg"));
                onlinePayDobj.setServices_TYPE(rst.getString("service_type"));
                onlinePayDobj.setRout_code(rst.getString("route_class"));
                onlinePayDobj.setRout_length(rst.getString("route_length"));
                onlinePayDobj.setNumberOfTrips(rst.getString("no_of_trips"));
                onlinePayDobj.setDomain_CODE(rst.getString("domain_cd"));
                onlinePayDobj.setValidForTemp(isValidForTemp);
                onlinePayDobj.setNoOfAdvUnits(rst.getString("no_adv_units"));
                onlinePayDbjList.add(onlinePayDobj);
            }
            for (OnlinePayDobj pyDobj : onlinePayDbjList) {
                String updateSql = "Update  vp_rcpt_cart set rcpt_no = ? ,rcpt_dt = ?  where appl_no = ? and user_cd = ? and transaction_no = ? and transaction_no IS NOT NULL";
                PreparedStatement psUpdate = tmgr.prepareStatement(updateSql);
                psUpdate.setString(1, pyDobj.getReceiptNo());
                psUpdate.setTimestamp(2, rcptTimeStamp);
                psUpdate.setString(3, pyDobj.getApplNo());
                psUpdate.setLong(4, Long.parseLong(userId));
                psUpdate.setString(5, transactionNo);
                ServerUtility.validateQueryResult(tmgr, psUpdate.executeUpdate(), psUpdate);
            }
            payList = this.saveFeeDetailsAfterSuccesfullPayment(tmgr, transactionNo, onlinePayDbjList, actionCode, userId, stateCd, offCd, userCatg, "NewVehicle", rcptTimeStamp, null,
                    empCode, userLoginOffCode, selectedRoleCode, clientIpAddress, tmConfigDobj, sessionUserId);
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("TransactionNo " + transactionNo + " " + e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Receipt Generation Failed !!!");
        }
        return payList;
    }

    /*
     * This function used to get TaxBreakUp details.
     */
    public List<OnlinePayDobj> getTaxBreakUpDetails(String app_no, TransactionManager tmgr) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        List<OnlinePayDobj> dobjList = new ArrayList<OnlinePayDobj>();

        try {
            sql = "select b.state_cd, b.off_cd, b.appl_no, b.sr_no, b.tax_from, b.tax_upto, b.pur_cd, \n"
                    + " b.prv_adjustment, b.tax, b.exempted, b.rebate, b.surcharge, b.penalty, b.interest,b.tax1,b.tax2, c.rcpt_no \n"
                    + " from vp_cart_tax_breakup b left outer join vp_rcpt_cart c \n"
                    + " on c.appl_no = b.appl_no and c.pur_cd = b.pur_cd \n"
                    + " where c.appl_no = ? \n";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, app_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                OnlinePayDobj dobj = new OnlinePayDobj();
                dobj.setStateCd(rs.getString("state_cd"));
                dobj.setOffCode(rs.getInt("off_cd"));
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setSrNo(rs.getInt("sr_no"));
                dobj.setPeriodFrom(rs.getString("tax_from"));
                dobj.setPeriodUpto(rs.getString("tax_upto"));
                dobj.setPurCd(rs.getInt("pur_cd"));
                dobj.setPrevAdjustement(rs.getInt("prv_adjustment"));
                dobj.setTaxBreakUpAmount(rs.getDouble("tax"));
                dobj.setExempted(rs.getLong("exempted"));
                dobj.setTaxBreakUpRebate(rs.getDouble("rebate"));
                dobj.setTaxBreakUpSurcharge(rs.getDouble("surcharge"));
                dobj.setTaxBreakUpPenalty(rs.getDouble("penalty"));
                dobj.setTaxBreakUpInterest(rs.getDouble("interest"));
                dobj.setReceiptNo(rs.getString("rcpt_no"));
                dobj.setTaxBreakUpAmount1(rs.getDouble("tax1"));
                dobj.setTaxBreakUpAmount2(rs.getDouble("tax2"));
                dobjList.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error("for Application No " + app_no + " " + e);
            throw new VahanException("Unable to proccess your request.Please Try Again");
        }
        return dobjList;
    }

    /*
     * This function used to generate Receipt Number for each application and call method used to insert data for (REGISTERED VEHICLES)
     */
    public List<OnlinePayDobj> getRecptNoRegnNoAndinsertDataForRegisteredVehicles(String userId, String transactionNo, int actionCode, String stateCd, int offCd, String userCatg, int pur_cd, String transactionRegnNo) throws VahanException {
        TransactionManager tmgr = null;
        List<OnlinePayDobj> payList = null;
        OwnerImpl ownerImpl = new OwnerImpl();
        PermitFeeImpl feeImpl = new PermitFeeImpl();
        List<OnlinePayDobj> onlinePayDbjList = new ArrayList<>();
        java.sql.Timestamp rcptTimeStamp = null;
        String selectSql;
        String appl_no = "";
        try {
            tmgr = new TransactionManager("getRecptNoRegnNoAndinsertDataForRegisteredVehicles");
            String reciptNo = ServerUtil.getUniqueOfficeRcptNo(tmgr, stateCd, offCd, TableConstants.DEALER_RCPT_FLAG, false);
            if (pur_cd == TableConstants.VM_PMT_FRESH_PUR_CD && transactionRegnNo.equalsIgnoreCase("NEW")) {
                selectSql = "SELECT a.appl_no,b.regn_no,b.pur_cd,c.state_cd,c.off_cd,'' as chasi_no,pgi.rcpt_dt,a.pmt_type, a.pmt_catg, a.service_type, a.route_class,a.route_length, a.no_of_trips, a.domain_cd, a.distance_run_in_quarter,a.no_adv_units \n"
                        + " from va_details b \n"
                        + " inner join (select * from vp_rcpt_cart where user_cd = ? and transaction_no = ? limit 1) a on a.appl_no = b.appl_no \n"
                        + " left join permit.va_permit_owner c on c.appl_no = b.appl_no \n"
                        + " left outer join vahanpgi.vp_pgi_details pgi on a.transaction_no = pgi.payment_id \n"
                        + " where a.user_cd = ? and a.transaction_no = ? ";
            } else if (pur_cd == TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD || pur_cd == TableConstants.VM_PMT_RENEW_COUNTER_SIGNATURE_PUR_CD) {
                selectSql = "SELECT a.appl_no,b.regn_no,b.pur_cd,b.state_cd,b.off_cd,'' as chasi_no,c.owner_name,c.vh_class,pgi.rcpt_dt,a.pmt_type, a.pmt_catg, a.service_type, a.route_class,a.route_length, a.no_of_trips, a.domain_cd, a.distance_run_in_quarter,a.no_adv_units \n"
                        + " from va_details b \n"
                        + " inner join (select * from vp_rcpt_cart where user_cd = ? and transaction_no = ? limit 1) a on a.appl_no = b.appl_no \n"
                        + " inner join permit.va_permit_countersignature c on c.appl_no = b.appl_no and b.state_cd=c.state_cd \n"
                        + " left outer join vahanpgi.vp_pgi_details pgi on a.transaction_no = pgi.payment_id \n"
                        + " where a.user_cd = ? and a.transaction_no = ? ";
            } else {
                selectSql = "SELECT a.appl_no,b.regn_no,b.pur_cd,c.state_cd,c.off_cd,c.chasi_no,pgi.rcpt_dt,a.pmt_type, a.pmt_catg, a.service_type, a.route_class,a.route_length, a.no_of_trips, a.domain_cd, a.distance_run_in_quarter,a.no_adv_units \n"
                        + " from va_details b \n"
                        + " inner join (select * from vp_rcpt_cart where user_cd = ? and transaction_no = ? limit 1) a on a.appl_no = b.appl_no \n"
                        + " left join vt_owner c on c.regn_no = b.regn_no \n"
                        + " left outer join vahanpgi.vp_pgi_details pgi on a.transaction_no = pgi.payment_id \n"
                        + " where a.user_cd = ? and a.transaction_no = ? and b.entry_status <> '" + TableConstants.STATUS_APPROVED + "' ";
            }

            PreparedStatement psSelect = tmgr.prepareStatement(selectSql);
            psSelect.setLong(1, Long.parseLong(userId));
            psSelect.setString(2, transactionNo);
            psSelect.setLong(3, Long.parseLong(userId));
            psSelect.setString(4, transactionNo);

            RowSet rst = tmgr.fetchDetachedRowSet_No_release();
            while (rst.next()) {
                rcptTimeStamp = new java.sql.Timestamp(rst.getDate("rcpt_dt").getTime());
                List<OnlinePayDobj> taxBreakUpDetails = this.getTaxBreakUpDetails(rst.getString("appl_no"), tmgr);
                List<OnlinePayDobj> feeBreakUpDetails = this.getFeeBreakUpDetails(rst.getString("appl_no"), tmgr);
                OnlinePayDobj payDobj = new OnlinePayDobj();
                Owner_dobj ownerDobj = ownerImpl.getOwnerDobj(ownerImpl.getOwnerDetails(rst.getString("regn_no")));
                if (ownerDobj == null && (rst.getInt("pur_cd") == TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD || rst.getInt("pur_cd") == TableConstants.VM_PMT_RENEW_COUNTER_SIGNATURE_PUR_CD)) {
                    ownerDobj = new Owner_dobj();
                    ownerDobj.setOwner_name(rst.getString("owner_name"));
                    ownerDobj.setVh_class(rst.getInt("vh_class"));
                    ownerDobj.setChasi_no(rst.getString("chasi_no"));
                }
                ownerDobj.setAppl_no(rst.getString("appl_no"));
                appl_no = rst.getString("appl_no");
                payDobj.setReceiptNo(reciptNo);
                payDobj.setApplNo(rst.getString("appl_no"));
                payDobj.setOwnerDobj(ownerDobj);
                payDobj.setTaxBreakUpDetails(taxBreakUpDetails);
                payDobj.setPurCd(rst.getInt("pur_cd"));
                payDobj.setRegnNo(rst.getString("regn_no"));
                payDobj.setChassisNo(rst.getString("chasi_no"));
                payDobj.setPmt_type_code(rst.getString("pmt_type"));
                payDobj.setPmtCatg(rst.getString("pmt_catg"));
                payDobj.setServices_TYPE(rst.getString("service_type"));
                payDobj.setRout_code(rst.getString("route_class"));
                payDobj.setRout_length(rst.getString("route_length"));
                payDobj.setNumberOfTrips(rst.getString("no_of_trips"));
                payDobj.setDomain_CODE(rst.getString("domain_cd"));
                payDobj.setNoOfAdvUnits(rst.getString("no_adv_units"));
                payDobj.setFeeBreakUpDetails(feeBreakUpDetails);
                onlinePayDbjList.add(payDobj);
            }
            //update rcpt_no in permit tables
            if (onlinePayDbjList.size() > 0) {
                for (OnlinePayDobj pyDobj : onlinePayDbjList) {
                    PermitFeeDobj dobj = new PermitFeeDobj();
                    dobj.setAppl_no(pyDobj.getApplNo());
                    dobj.setRegn_no(pyDobj.getRegnNo());
                    dobj.setPur_cd(String.valueOf(pyDobj.getPurCd()));
                    if (pyDobj.getPurCd() == TableConstants.VM_PMT_TEMP_PUR_CD
                            || pyDobj.getPurCd() == TableConstants.VM_PMT_SPECIAL_PUR_CD
                            || pyDobj.getPurCd() == TableConstants.VM_PMT_RENEW_TEMP_PUR_CD) {
                        feeImpl.updateSetVA_temp_Permit(tmgr, pyDobj.getReceiptNo(), pyDobj.getApplNo());
                    } else if (pyDobj.getPurCd() == TableConstants.VM_PMT_FRESH_PUR_CD
                            || pyDobj.getPurCd() == TableConstants.VM_PMT_RENEWAL_PUR_CD) {
                        feeImpl.updateSetVaPermit(dobj, tmgr, pyDobj.getReceiptNo());
                        if (("new").equalsIgnoreCase(pyDobj.getRegnNo()) && ("TN,PY,UP").contains(Util.getUserStateCode())) {
                            new PermitLOIImpl().insertVaOfferApproval(tmgr, dobj.getAppl_no());
                        }
                    } else if (pyDobj.getPurCd() == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD || pyDobj.getPurCd() == TableConstants.VM_PMT_SURRENDER_PUR_CD || pyDobj.getPurCd() == TableConstants.VM_PMT_RESTORE_PUR_CD
                            || pyDobj.getPurCd() == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD || pyDobj.getPurCd() == TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD || pyDobj.getPurCd() == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD
                            || pyDobj.getPurCd() == TableConstants.VM_PMT_TRANSFER_PUR_CD || pyDobj.getPurCd() == TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD || pyDobj.getPurCd() == TableConstants.VM_PMT_CANCELATION_PUR_CD) {
                        feeImpl.updateSetVaPermitTransaction(tmgr, pyDobj.getReceiptNo(), pyDobj.getApplNo());
                    } else if (pyDobj.getPurCd() == TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD || pyDobj.getPurCd() == TableConstants.VM_PMT_RENEW_COUNTER_SIGNATURE_PUR_CD) {
                        feeImpl.updateSetVaCounterSignature(tmgr, pyDobj.getReceiptNo(), pyDobj.getApplNo());
                    } else if (pyDobj.getPurCd() == TableConstants.VM_PMT_LEASE_PUR_CD) {
                        feeImpl.updateSetVaLeasePermit(tmgr, pyDobj.getReceiptNo(), pyDobj.getApplNo());
                    }
                }
            }
            if (!onlinePayDbjList.isEmpty() && onlinePayDbjList.size() > 0) {
                String updateSql = "Update  vp_rcpt_cart set rcpt_no = ? ,rcpt_dt = ?  where appl_no = ? and user_cd = ? and transaction_no = ? and transaction_no IS NOT NULL";
                PreparedStatement psUpdate = tmgr.prepareStatement(updateSql);
                for (OnlinePayDobj pyDobj : onlinePayDbjList) {
                    int i = 1;
                    psUpdate.setString(i++, pyDobj.getReceiptNo());
                    psUpdate.setTimestamp(i++, rcptTimeStamp);
                    psUpdate.setString(i++, pyDobj.getApplNo());
                    psUpdate.setLong(i++, Long.parseLong(userId));
                    psUpdate.setString(i++, transactionNo);
                    ServerUtil.validateQueryResult(tmgr, psUpdate.executeUpdate(), psUpdate);
                }
                String updateSqlTemp = "Update " + TableList.VP_RCPT_CART_TEMP + " set rcpt_no = ? ,rcpt_dt = ?  where appl_no = ? and user_cd = ? and transaction_no = ? and transaction_no IS NOT NULL";
                PreparedStatement psUpdateTemp = tmgr.prepareStatement(updateSqlTemp);
                for (OnlinePayDobj pyDobj : onlinePayDbjList) {
                    int i = 1;
                    psUpdateTemp.setString(i++, pyDobj.getReceiptNo());
                    psUpdateTemp.setTimestamp(i++, rcptTimeStamp);
                    psUpdateTemp.setString(i++, pyDobj.getApplNo());
                    psUpdateTemp.setLong(i++, Long.parseLong(userId));
                    psUpdateTemp.setString(i++, transactionNo);
                    psUpdateTemp.executeUpdate();
                }

                payList = this.saveFeeDetailsAfterSuccesfullPayment(tmgr, transactionNo, onlinePayDbjList, actionCode, userId, stateCd, offCd, userCatg, "RegisteredVehicles", rcptTimeStamp, null);
                selectSql = "SELECT appl_no from permit.va_np_detail where appl_no=? and state_cd=?  ";
                psSelect = tmgr.prepareStatement(selectSql);
                psSelect.setString(1, appl_no);
                psSelect.setString(2, stateCd);
                RowSet rstp = tmgr.fetchDetachedRowSet_No_release();
                if (rstp.next()) {
                    if (!CommonUtils.isNullOrBlank(rstp.getString("appl_no"))) {
                        updateSql = "Update  permit.va_np_detail set paymentstatus = 'Y'  where appl_no = ? and state_cd = ?";
                        psUpdate = tmgr.prepareStatement(updateSql);
                        psUpdate.setString(1, appl_no);
                        psUpdate.setString(2, stateCd);
                        psUpdate.executeUpdate();
                    }

                }
                if (payList != null && !payList.isEmpty()) {
                    tmgr.commit();
                }
            } else {
                throw new VahanException("Application details not found for payment process. Please try again.");
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error("for TransactionNo " + transactionNo + " " + e);
            throw new VahanException("Receipt Generation Failed !!!");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return payList;
    }

    public List<OnlinePayDobj> getRecptNoRegnNoAndinsertDataForOnlineFeeTax(String selectedOption, String userId, String transactionNo, int actionCode, String stateCd, int offCd, String userCatg, String form_type) throws VahanException {
        TransactionManager tmgr = null;
        List<OnlinePayDobj> payList = null;
        List<OnlinePayDobj> onlinePayDbjList = new ArrayList<OnlinePayDobj>();
        java.sql.Timestamp rcptTimeStamp = null;
        try {
            tmgr = new TransactionManager("getRecptNoRegnNoAndinsertDataForOnlineFeeTax");
            String reciptNo = ServerUtil.getUniqueOfficeRcptNo(tmgr, stateCd, offCd, TableConstants.DEALER_RCPT_FLAG, false);
            onlinePayDbjList = getDetailList(tmgr, transactionNo, userId, stateCd, offCd, reciptNo);
            rcptTimeStamp = onlinePayDbjList.get(0).getPaymentDate();
            String updateSql = "Update  vp_rcpt_cart set rcpt_no = ? ,rcpt_dt = ?  where appl_no = ? and user_cd = ? and transaction_no = ? and transaction_no IS NOT NULL";
            PreparedStatement psUpdate = tmgr.prepareStatement(updateSql);
            for (OnlinePayDobj pyDobj : onlinePayDbjList) {
                int i = 1;
                psUpdate.setString(i++, pyDobj.getReceiptNo());
                psUpdate.setTimestamp(i++, rcptTimeStamp);
                psUpdate.setString(i++, pyDobj.getApplNo());
                psUpdate.setLong(i++, Long.parseLong(userId));
                psUpdate.setString(i++, transactionNo);
                ServerUtil.validateQueryResult(tmgr, psUpdate.executeUpdate(), psUpdate);
            }
            String vehicleType = "";
            if ((TableConstants.TAX_MODE_BALANCE).equals(form_type)) {
                vehicleType = "BalanceFee";
            } else if (TableConstants.TAX_INSTALLMENT.equals(form_type)) {
                vehicleType = "TAX-INSTALLMENT";
            } else if (TableConstants.ONLINE_FANCY.equals(form_type)) {
                vehicleType = "ONLINE-FANCY";
            } else if (TableConstants.ONLINE_AUDIT.equals(form_type)) {
                vehicleType = "ONLINE-AUDIT";
            } else {
                vehicleType = "ONLINETAX";
            }
            payList = this.saveFeeDetailsAfterSuccesfullPayment(tmgr, transactionNo, onlinePayDbjList, actionCode, userId, stateCd, offCd, userCatg, vehicleType, rcptTimeStamp, selectedOption);
            if (payList != null && !payList.isEmpty()) {
                tmgr.commit();
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error("for TransactionNo " + transactionNo + " " + e);
            throw new VahanException("Receipt Generation Failed !!!");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return payList;
    }

    /*
     * This function used to get DetailsList of New Vehicle from temporary table (vp_rcpt_cart)
     */
    public List<OnlinePayDobj> getDetailListForNewVehicles(TransactionManager tmgr, String transaction_no, String userId) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        List<OnlinePayDobj> dobjList = new ArrayList<OnlinePayDobj>();
        long manualRcptAmount = 0l;
        try {
            sql = "select distinct ct.transaction_no as pay_id,ct.appl_no as appl_no,ct.pur_cd,to_char(ct.period_from, 'yyyy-mm-dd') as period_from, to_char(ct.period_upto, 'yyyy-mm-dd') as period_upto,ct.penalty,sum(ct.amount+ct.surcharge+ct.interest+ct.tax1+ct.tax2+ct.prv_adjustment-ct.exempted-ct.rebate) as amt ,o.state_cd,o.regn_no,o.chasi_no,o.owner_name,o.off_cd,oi.mobile_no,oi.email_id,ct.rcpt_no,ct.period_mode as tax_mode, sum(COALESCE(m.amount,0)) as manualRcptAmt \n"
                    + " from " + TableList.VP_RCPT_CART + " ct \n"
                    + " inner join " + TableList.VA_OWNER + " o on ct.appl_no = o.appl_no and ct.state_cd=o.state_cd\n"
                    + " left outer join " + TableList.VA_OWNER_IDENTIFICATION + " oi on ct.appl_no = oi.appl_no and ct.state_cd=oi.state_cd\n"
                    + " left join " + TableList.VT_MANUAL_RECEIPT + " m on ct.appl_no= m.transaction_appl_no and ct.state_cd=m.state_cd and ct.off_cd=m.off_cd \n"
                    + " where ct.user_cd = ? and ct.transaction_no = ?\n"
                    + " group by ct.transaction_no ,ct.appl_no ,ct.period_from,ct.period_upto,ct.penalty,o.state_cd,o.regn_no,ct.pur_cd,o.chasi_no,"
                    + " o.owner_name,o.off_cd,oi.mobile_no,oi.email_id  order by appl_no,pur_cd";

            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(userId));
            ps.setString(2, transaction_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                OnlinePayDobj dobj = new OnlinePayDobj();
                dobj.setTransactionNo(rs.getString("pay_id"));
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setPurCd(rs.getInt("pur_cd"));
                dobj.setPeriodFrom(rs.getString("period_from"));
                dobj.setPeriodUpto(rs.getString("period_upto"));
                dobj.setPenalty(rs.getLong("penalty"));
                if (dobj.getPurCd() == TableConstants.TM_ROAD_TAX && rs.getLong("manualRcptAmt") > 0l) {
                    manualRcptAmount = rs.getLong("manualRcptAmt");
                    dobj.setAmount(rs.getLong("amt") + rs.getLong("manualRcptAmt"));
                } else {
                    dobj.setAmount(rs.getLong("amt"));
                }
                dobj.setStateCd(rs.getString("state_cd"));
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setChassisNo(rs.getString("chasi_no"));
                dobj.setOwnerName(rs.getString("owner_name"));
                dobj.setOffCode(rs.getInt("off_cd"));
                dobj.setMobileNumber(rs.getString("mobile_no"));
                dobj.setEmailId(rs.getString("email_id"));
                dobj.setReceiptNo(rs.getString("rcpt_no"));
                dobj.setTaxMode(rs.getString("tax_mode"));
                dobjList.add(dobj);
            }
            if (manualRcptAmount > 0l && !dobjList.isEmpty() && dobjList.size() > 0) {
                OnlinePayDobj dobj = new OnlinePayDobj();
                dobj.setTransactionNo(dobjList.get(0).getTransactionNo());
                dobj.setReceiptNo(dobjList.get(0).getReceiptNo());
                dobj.setApplNo(dobjList.get(0).getApplNo());
                dobj.setPurCd(TableConstants.VM_MAST_MANUAL_RECEIPT);
                dobj.setPenalty(0l);
                dobj.setRegnNo(dobjList.get(0).getRegnNo());
                dobj.setPeriodFrom("");
                dobj.setPeriodUpto("");
                dobj.setAmount(-manualRcptAmount);
                dobjList.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error("for TransactionNo " + transaction_no + " " + e);
            throw new VahanException("Unable to get online payment fee/tax details.Please Try Again");
        }
        return dobjList;
    }

    /*
     * This function used to get DetailsList of Registered Vehicle from temporary table (vp_rcpt_cart)
     */
    public List<OnlinePayDobj> getDetailListForRegisteredVehicles(TransactionManager tmgr, String transaction_no, String userId, String applNo, int pur_cd, String transactionRegn) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        List<OnlinePayDobj> dobjList = new ArrayList<OnlinePayDobj>();
        String payTableName = TableList.VP_RCPT_CART;
        try {
            if (pur_cd == TableConstants.VM_PMT_FRESH_PUR_CD && !CommonUtils.isNullOrBlank(transactionRegn) && transactionRegn.equalsIgnoreCase("NEW")) {
                sql = "  select ct.transaction_no as pay_id,ct.appl_no as appl_no,ct.pur_cd,to_char(ct.period_from, 'yyyy-mm-dd') as period_from, to_char(ct.period_upto, 'yyyy-mm-dd') as period_upto,ct.penalty,sum(ct.amount+ct.surcharge+ct.interest+ct.tax1+ct.tax2+ct.prv_adjustment-ct.exempted-ct.rebate) as amt ,o.state_cd,o.regn_no,'' as chasi_no,o.owner_name,o.off_cd,o.mobile_no,o.email_id,ct.rcpt_no,ct.period_mode as tax_mode \n"
                        + " from vp_rcpt_cart ct \n"
                        + " inner join (SELECT * FROM va_details WHERE appl_no = ? LIMIT 1) d on ct.appl_no = d.appl_no  \n"
                        + " inner join permit.va_permit_owner o on d.appl_no = o.appl_no and ct.user_cd = ? and ct.transaction_no = ? \n"
                        + " left outer join va_owner_identification oi on oi.appl_no = d.appl_no \n"
                        + " group by ct.transaction_no ,ct.appl_no ,ct.period_from,ct.period_upto,ct.penalty,o.state_cd,o.regn_no,ct.pur_cd,chasi_no,o.owner_name,o.off_cd,o.mobile_no,o.email_id  order by appl_no,pur_cd";
            } else if (pur_cd == TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD || pur_cd == TableConstants.VM_PMT_RENEW_COUNTER_SIGNATURE_PUR_CD) {
                sql = "select ct.transaction_no as pay_id,ct.appl_no as appl_no,ct.pur_cd,to_char(ct.period_from, 'yyyy-mm-dd') as period_from, to_char(ct.period_upto, 'yyyy-mm-dd') as period_upto,ct.penalty,sum(ct.amount+ct.surcharge+ct.interest+ct.tax1+ct.tax2+ct.prv_adjustment-ct.exempted-ct.rebate) as amt ,o.state_cd,o.regn_no,'' as chasi_no,o.owner_name,o.off_cd,o.mobile_no,o.email_id,ct.rcpt_no,ct.period_mode as tax_mode \n"
                        + " from vp_rcpt_cart ct \n"
                        + " inner join (SELECT * FROM va_details WHERE appl_no = ? LIMIT 1) d on ct.appl_no = d.appl_no  \n"
                        + " inner join permit.va_permit_countersignature o on o.appl_no = d.appl_no and d.regn_no = o.regn_no and ct.user_cd = ? and ct.transaction_no = ?  \n"
                        + " group by ct.transaction_no ,ct.appl_no ,ct.period_from,ct.period_upto,ct.penalty,o.state_cd,o.regn_no,ct.pur_cd,chasi_no,o.owner_name,o.off_cd,o.mobile_no,o.email_id \n"
                        + " order by appl_no,pur_cd";
            } else {
                if (isExistRcptCartTemp(tmgr, applNo)) {
                    payTableName = TableList.VP_RCPT_CART_TEMP;
                }
                sql = "select ct.transaction_no as pay_id,ct.appl_no as appl_no,ct.pur_cd,to_char(ct.period_from, 'yyyy-mm-dd') as period_from, to_char(ct.period_upto, 'yyyy-mm-dd') as period_upto,ct.penalty,sum(ct.amount+ct.surcharge+ct.interest+ct.tax1+ct.tax2+ct.prv_adjustment-ct.exempted-ct.rebate) as amt ,o.state_cd,o.regn_no,o.chasi_no,o.owner_name,o.off_cd,oi.mobile_no,oi.email_id,ct.rcpt_no,ct.period_mode as tax_mode \n"
                        + " from " + payTableName + " ct \n"
                        + " inner join (SELECT * FROM va_details WHERE appl_no = ? LIMIT 1) d on ct.appl_no = d.appl_no  \n"
                        + " inner join vt_owner o on d.regn_no = o.regn_no and ct.user_cd = ? and ct.transaction_no = ? \n"
                        + " left outer join va_owner_identification oi on oi.appl_no = d.appl_no \n"
                        + " group by ct.transaction_no ,ct.appl_no ,ct.period_from,ct.period_upto,ct.penalty,o.state_cd,o.regn_no,ct.pur_cd,o.chasi_no,o.owner_name,o.off_cd,oi.mobile_no,oi.email_id \n"
                        + " order by appl_no,pur_cd";
            }
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setLong(2, Long.parseLong(userId));
            ps.setString(3, transaction_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                OnlinePayDobj dobj = new OnlinePayDobj();
                dobj.setTransactionNo(rs.getString("pay_id"));
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setPurCd(rs.getInt("pur_cd"));
                dobj.setPeriodFrom(rs.getString("period_from"));
                dobj.setPeriodUpto(rs.getString("period_upto"));
                dobj.setPenalty(rs.getLong("penalty"));
                dobj.setAmount(rs.getLong("amt"));
                dobj.setStateCd(rs.getString("state_cd"));
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setChassisNo(rs.getString("chasi_no"));
                dobj.setOwnerName(rs.getString("owner_name"));
                dobj.setOffCode(rs.getInt("off_cd"));
                dobj.setMobileNumber(rs.getString("mobile_no"));
                dobj.setEmailId(rs.getString("email_id"));
                dobj.setReceiptNo(rs.getString("rcpt_no"));
                dobj.setTaxMode(rs.getString("tax_mode"));
                dobjList.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error("for TransactionNo " + transaction_no + " " + e);
            throw new VahanException("Unable to proccess your request.Please Try Again");
        }
        return dobjList;
    }

    public List<OnlinePayDobj> getDetailListForExemptedPMTFee(TransactionManager tmgr, List<OnlinePayDobj> dobjList, String applNo, String transactionNo, String userId) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        HashMap fineMap = new HashMap();
        boolean flag = false;
        try {
            sql = "select distinct ct.transaction_no as pay_id,ct.appl_no as appl_no,e.fine_exempted as penalty,e.pur_cd,e.state_cd,e.off_cd,d.regn_no,ct.rcpt_no \n"
                    + " from vp_rcpt_cart ct \n"
                    + " inner join (SELECT * FROM va_details WHERE appl_no = ? LIMIT 1) d on ct.appl_no = d.appl_no  \n"
                    + " inner join va_fee_exemtion e on ct.appl_no = e.appl_no  \n"
                    + " where ct.user_cd = ? and ct.transaction_no = ? and e.fine_exempted > 0 and e.exemtion_reason=? and d.pur_cd in (?,?)\n";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setLong(2, Long.parseLong(userId));
            ps.setString(3, transactionNo);
            ps.setString(4, "COVID-19");
            ps.setInt(5, TableConstants.VM_PMT_RENEWAL_PUR_CD);
            ps.setInt(6, TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                flag = true;
                if (rs.getInt("pur_cd") == TableConstants.FEE_FINE_EXEMTION) {
                    OnlinePayDobj dobj = new OnlinePayDobj();
                    dobj.setTransactionNo(rs.getString("pay_id"));
                    dobj.setApplNo(rs.getString("appl_no"));
                    dobj.setPurCd(rs.getInt("pur_cd"));
                    dobj.setPenalty(-rs.getLong("penalty"));
                    dobj.setAmount(0l);
                    dobj.setStateCd(rs.getString("state_cd"));
                    dobj.setRegnNo(rs.getString("regn_no"));
                    dobj.setOffCode(rs.getInt("off_cd"));
                    dobj.setReceiptNo(rs.getString("rcpt_no"));
                    dobj.setPeriodFrom("");
                    dobj.setPeriodUpto("");
                    dobjList.add(dobj);
                } else {
                    fineMap.put(rs.getInt("pur_cd"), rs.getLong("penalty"));
                }
            }
            if (flag) {
                for (OnlinePayDobj feeDobj : dobjList) {
                    if (feeDobj.getPurCd() == TableConstants.VM_PMT_RENEWAL_PUR_CD || feeDobj.getPurCd() == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD) {
                        feeDobj.setPenalty((Long) fineMap.get(feeDobj.getPurCd()));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("for TransactionNo " + transactionNo + " " + e);
            throw new VahanException("Unable to proccess your request.Please Try Again");
        }
        return dobjList;
    }

    /*
     * This function used to insert data in all fee related tables.
    Note: Not upadting as of 1-12-2020
     */
    public List<OnlinePayDobj> saveFeeDetailsAfterSuccesfullPayment(TransactionManager tmgr, String transactionNo, List<OnlinePayDobj> onlinePayDobjList, int actionCode, String userId, String stateCd, int offCd, String userCatg, String vehicleType, java.sql.Timestamp rcptTimeStamp, String balanceFeeSelectOption) throws VahanException {
        List<OnlinePayDobj> dtlDobjList = null;
        List<OnlinePayDobj> payDobjList = null;
        List<OnlinePayDobj> feeDbjList = new ArrayList<>();
        FeeImpl feeImpl = new FeeImpl();
        boolean blackListedCompoundingAmt = false;
        try {
            if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("NewVehicle")) {
                dtlDobjList = getDetailListForNewVehicles(tmgr, transactionNo, userId);
            } else if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("RegisteredVehicles")) {
                dtlDobjList = getDetailListForRegisteredVehicles(tmgr, transactionNo, userId, onlinePayDobjList.get(0).getApplNo(), onlinePayDobjList.get(0).getPurCd(), onlinePayDobjList.get(0).getRegnNo());
            } else if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("BalanceFee") || vehicleType.equalsIgnoreCase("ONLINETAX") || vehicleType.equalsIgnoreCase("TAX-INSTALLMENT") || vehicleType.equalsIgnoreCase("ONLINE-FANCY") || vehicleType.equalsIgnoreCase("ONLINE-AUDIT")) {
                dtlDobjList = onlinePayDobjList;
                BlackListedVehicleDobj blackListedDobj = new BlackListedVehicleImpl().getBlacklistedVehicleDetails(dtlDobjList.get(0).getRegnNo(), null);
                if (blackListedDobj != null) {
                    if (blackListedDobj.getComplain_type() == TableConstants.BLCompoundingAmtCode) {
                        blackListedCompoundingAmt = true;
                    }
                }
            } else if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("TradeCertificate")) {
                dtlDobjList = getDetailListForTradeCertificate(tmgr, transactionNo, userId);
            }
// FEE_FINE_EXEMTION
            if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("RegisteredVehicles") && dtlDobjList != null && dtlDobjList.size() > 0) {
                dtlDobjList = getDetailListForExemptedPMTFee(tmgr, dtlDobjList, onlinePayDobjList.get(0).getApplNo(), transactionNo, userId);
            }
            for (OnlinePayDobj feeDobj : dtlDobjList) {
                if (feeDobj.getPeriodFrom().equals("") && feeDobj.getPeriodUpto().equals("")) {

                    String feeSql = "INSERT INTO vt_fee(regn_no, payment_mode, fees, fine, rcpt_no, rcpt_dt, pur_cd, \n"
                            + " flag, collected_by, state_cd, off_cd) \n"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, \n"
                            + " ?, ?, ?, ?) ";

                    PreparedStatement psFee = tmgr.prepareStatement(feeSql);
                    int i = 1;
                    psFee.setString(i++, feeDobj.getRegnNo());
                    psFee.setString(i++, TableConstants.PAYMENT_MODE);
                    psFee.setLong(i++, feeDobj.getAmount());
                    psFee.setLong(i++, feeDobj.getPenalty());
                    psFee.setString(i++, feeDobj.getReceiptNo());
                    psFee.setTimestamp(i++, rcptTimeStamp);
                    psFee.setInt(i++, feeDobj.getPurCd());
                    psFee.setNull(i++, java.sql.Types.VARCHAR);
                    if (userCatg != null && userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                        psFee.setString(i++, userId);
                    } else {
                        psFee.setString(i++, TableConstants.ONLINE_PAYMENT);
                    }
                    psFee.setString(i++, stateCd);
                    psFee.setInt(i++, offCd);
                    // Purcd =72 for Tax clearance certificate 
                    if (feeDobj.getPurCd() == 72) {
                        TaxClearanceCertificatePrintImpl.insertintoVA_TCC_Print(feeDobj.getRegnNo(), feeDobj.getApplNo(), tmgr, stateCd, offCd);
                    }
                    if (feeDobj.getPurCd() == TableConstants.BLCompoundingAmtCode && blackListedCompoundingAmt) {
                        BlackListedVehicleImpl impl = new BlackListedVehicleImpl();
                        impl.insertIntoVhBlacklistFromVtBlacklist(tmgr, feeDobj.getRegnNo(), stateCd, offCd, feeDobj.getReceiptNo());
                    }
                    ServerUtil.validateQueryResult(tmgr, psFee.executeUpdate(), psFee);
                    if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("RegisteredVehicles")) {
                        feeDbjList.add(feeDobj);
                    }
                }
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (OnlinePayDobj taxDobj : dtlDobjList) {
                if (!taxDobj.getPeriodFrom().equals("")) {

                    String taxSql = "INSERT INTO vt_tax(regn_no, tax_mode, payment_mode, tax_amt, tax_fine, rcpt_no, \n"
                            + " rcpt_dt, tax_from, tax_upto, pur_cd, flag, collected_by, state_cd, off_cd) \n"
                            + " VALUES (?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?) ";

                    PreparedStatement psTax = tmgr.prepareStatement(taxSql);
                    int i = 1;
                    psTax.setString(i++, taxDobj.getRegnNo());
                    if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("BalanceFee")) {
                        psTax.setString(i++, TableConstants.TAX_MODE_BALANCE);
                    } else {
                        psTax.setString(i++, taxDobj.getTaxMode());
                    }
                    psTax.setString(i++, TableConstants.PAYMENT_MODE);
                    psTax.setLong(i++, taxDobj.getAmount());
                    psTax.setLong(i++, taxDobj.getPenalty());
                    psTax.setString(i++, taxDobj.getReceiptNo());
                    psTax.setTimestamp(i++, rcptTimeStamp);
                    psTax.setDate(i++, new java.sql.Date(sdf.parse(taxDobj.getPeriodFrom()).getTime()));
                    if (!taxDobj.getPeriodUpto().equals("")) {
                        psTax.setDate(i++, new java.sql.Date(sdf.parse(taxDobj.getPeriodUpto()).getTime()));
                    } else {
                        psTax.setNull(i++, java.sql.Types.DATE);
                    }
                    psTax.setInt(i++, taxDobj.getPurCd());
                    psTax.setNull(i++, java.sql.Types.VARCHAR);
                    if (userCatg != null && userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                        psTax.setString(i++, userId);
                    } else {
                        psTax.setString(i++, TableConstants.ONLINE_PAYMENT);
                    }
                    psTax.setString(i++, stateCd);
                    psTax.setInt(i++, offCd);
                    ServerUtil.validateQueryResult(tmgr, psTax.executeUpdate(), psTax);
                    if (!CommonUtils.isNullOrBlank(vehicleType) && (vehicleType.equalsIgnoreCase("BalanceFee") || vehicleType.equalsIgnoreCase("ONLINETAX"))) {
                        new TaxClearImplementation().updateAppl_noInVt_Refund_Excess(taxDobj.getApplNo(), taxDobj.getPurCd(), taxDobj.getRegnNo(), TableConstants.TM_ROLE_BALANCE_FEE_TAX_COLLECTION, tmgr);
                    }
                }
            }

            // update owner dobj for to alt and conversion
            int counter = 0;
            Owner_dobj owner_dobj = null;
            if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("RegisteredVehicles")) {
                for (OnlinePayDobj payDobj : onlinePayDobjList) {
                    AltDobj altDobj = null;
                    ConvDobj convdobj = null;

                    if (counter == 0) {
                        owner_dobj = payDobj.getOwnerDobj();
                    }

                    if (payDobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_TO) {
                        String toDbjOwnerName = feeImpl.updateOwnerNameForToOrFrc(payDobj.getPurCd(), payDobj.getApplNo(), payDobj.getOwnerDobj());
                        if (toDbjOwnerName != null) {
                            owner_dobj.setOwner_name(toDbjOwnerName);
                        }
                    }

                    if (payDobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_VEH_ALTER) {
                        AltImpl altImpl = new AltImpl();
                        altDobj = altImpl.set_ALT_appl_db_to_dobj(payDobj.getApplNo());
                        if (altDobj != null) {
                            feeImpl.updateOwnerDobjForAltOrConv(altDobj, convdobj, owner_dobj);
                        }
                    }

                    if (payDobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION) {
                        convdobj = ConvImpl.set_Conversion_appl_db_to_dobj(payDobj.getApplNo(), null);
                        if (convdobj != null) {
                            feeImpl.updateOwnerDobjForAltOrConv(altDobj, convdobj, owner_dobj);
                        }
                    }
                    if (payDobj.getPurCd() == TableConstants.VM_PMT_FRESH_PUR_CD && payDobj.getRegnNo().equalsIgnoreCase("NEW")) {
                        PermitOwnerDetailDobj PmtOwnerDobj = new PermitOwnerDetailImpl().set_VA_Owner_permit_to_dobj(payDobj.getApplNo().toUpperCase(), payDobj.getRegnNo());
                        if (PmtOwnerDobj != null) {
                            owner_dobj.setOwner_name(PmtOwnerDobj.getOwner_name());
                            owner_dobj.setVh_class(PmtOwnerDobj.getVh_class());
                            owner_dobj.setChasi_no("");
                        }
                    }
                    counter++;
                }
                onlinePayDobjList.get(0).setOwnerDobj(owner_dobj);
            }
            int registeredVehicle = 0;
            for (OnlinePayDobj payDobj : onlinePayDobjList) {
                if ((!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("RegisteredVehicles")
                        && registeredVehicle == 0) || (!CommonUtils.isNullOrBlank(vehicleType)
                        && vehicleType.equalsIgnoreCase("NewVehicle")) || (!CommonUtils.isNullOrBlank(vehicleType)
                        && (vehicleType.equalsIgnoreCase("BalanceFee") || vehicleType.equalsIgnoreCase("ONLINETAX") || vehicleType.equalsIgnoreCase("TAX-INSTALLMENT") || vehicleType.equalsIgnoreCase("ONLINE-FANCY") || vehicleType.equalsIgnoreCase("ONLINE-AUDIT")) && registeredVehicle == 0)
                        || (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("TradeCertificate")
                        && registeredVehicle == 0)) {
                    String VpApplRcptMappingSql = "INSERT INTO " + TableList.VP_APPL_RCPT_MAPPING
                            + "( state_cd, off_cd, appl_no, rcpt_no, owner_name, chasi_no, vh_class, \n"
                            + " instrument_cd, excess_amt, cash_amt, remarks)"
                            + "    VALUES (?, ?, ?, ?, ?, ?, ?,?, ?, ?,?)";

                    PreparedStatement psVpApplRcptMapping = tmgr.prepareStatement(VpApplRcptMappingSql);
                    int i = 1;
                    psVpApplRcptMapping.setString(i++, stateCd);
                    psVpApplRcptMapping.setInt(i++, offCd);
                    psVpApplRcptMapping.setString(i++, payDobj.getApplNo());
                    psVpApplRcptMapping.setString(i++, payDobj.getReceiptNo());
                    if (!CommonUtils.isNullOrBlank(vehicleType) && !vehicleType.equalsIgnoreCase("TradeCertificate")) {
                        psVpApplRcptMapping.setString(i++, payDobj.getOwnerDobj().getOwner_name());
                        psVpApplRcptMapping.setString(i++, payDobj.getOwnerDobj().getChasi_no());
                        psVpApplRcptMapping.setInt(i++, payDobj.getOwnerDobj().getVh_class());
                    } else {
                        psVpApplRcptMapping.setString(i++, "");//For TC no owner name
                        psVpApplRcptMapping.setString(i++, "");//For TC no chasi No
                        psVpApplRcptMapping.setInt(i++, 0);    //For TC no vehicle class
                    }
                    psVpApplRcptMapping.setNull(i++, java.sql.Types.INTEGER);
                    psVpApplRcptMapping.setLong(i++, 0);
                    psVpApplRcptMapping.setLong(i++, 0);
                    String remarks = "";
                    if (!CommonUtils.isNullOrBlank(vehicleType)) {
                        if (vehicleType.equalsIgnoreCase("BalanceFee")) {
                            remarks = "BALANCE-";
                        } else if (vehicleType.equalsIgnoreCase("RegisteredVehicles") && ServerUtil.getOfficeCodeType(offCd, stateCd) == TableConstants.FITNESS_CENTER_OFF_TYPE_CODE) {
                            remarks = "Applicable Inspection fee and GST has to paid to Fitness Center";
                        } else {
                            remarks = "ONLINE-PAYMENT";
                        }
                    } else {
                        remarks = "ONLINE-PAYMENT";
                    }
                    ManualReceiptEntryDobj manualRcptDobjTemp = ManualReceiptEntryImpl.getApprovedManualReceiptDetails(payDobj.getApplNo());
                    if (manualRcptDobjTemp != null) {
                        remarks = "Rs. " + manualRcptDobjTemp.getAmount() + "/- adjusted against previously paid vide receipt no:" + manualRcptDobjTemp.getRcptNo() + ".";
                    }
                    psVpApplRcptMapping.setString(i++, remarks);
                    ServerUtil.validateQueryResult(tmgr, psVpApplRcptMapping.executeUpdate(), psVpApplRcptMapping);

                    if ("TAX-INSTALLMENT".equalsIgnoreCase(vehicleType)) {
                        String taxInstSQl = "select REGN_NO, SERIAL_NO, state_cd from " + TableList.VA_TAX_INSTALLMENT_BRKUP + " where regn_no = ?";
                        PreparedStatement psTaxInstSQl = tmgr.prepareStatement(taxInstSQl);
                        psTaxInstSQl.setString(1, payDobj.getOwnerDobj().getRegn_no());
                        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                        while (rs.next()) {
                            taxInstSQl = "UPDATE " + TableList.VT_TAX_INSTALLMENT_BRKUP + " SET RCPT_NO = ?"
                                    + " WHERE REGN_NO = ? and SERIAL_NO = ? ";

                            PreparedStatement ps = tmgr.prepareStatement(taxInstSQl);
                            ps.setString(1, payDobj.getReceiptNo());
                            ps.setString(2, payDobj.getOwnerDobj().getRegn_no());
                            ps.setString(3, rs.getString("SERIAL_NO"));
                            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                            ServerUtil.deleteFromTable(tmgr, payDobj.getOwnerDobj().getRegn_no(), null, TableList.VA_TAX_INSTALLMENT_BRKUP);
                        }
                    }

                    if ("ONLINE-FANCY".equalsIgnoreCase(vehicleType)) {
                        String fancySql = "INSERT INTO " + TableList.VT_ADVANCE_REGN_NO + " SELECT state_cd, off_cd, ?, regn_appl_no, regn_no, owner_name,f_name, c_add1, c_add2,c_add3, c_district, c_pincode, c_state,mobile_no,total_amt from " + TableList.VP_ADVANCE_REGN_NO + " where pay_appl_no = ?";

                        PreparedStatement psFancyCart = tmgr.prepareStatement(fancySql);
                        psFancyCart.setString(1, payDobj.getReceiptNo());
                        psFancyCart.setString(2, payDobj.getApplNo());
                        ServerUtil.validateQueryResult(tmgr, psFancyCart.executeUpdate(), psFancyCart);

                        fancySql = "DELETE FROM " + TableList.VM_AVAILABLE_NO_FANCY + " WHERE regn_no=?";
                        PreparedStatement psVmAvailable = tmgr.prepareStatement(fancySql);
                        psVmAvailable.setString(1, payDobj.getOwnerDobj().getRegn_no());
                        psVmAvailable.executeUpdate();

                        fancySql = "delete from  " + TableList.VP_ADVANCE_REGN_NO + " where pay_appl_no = ? ";
                        PreparedStatement psAdvanceDelete = tmgr.prepareStatement(fancySql);
                        psAdvanceDelete.setString(1, payDobj.getApplNo());
                        ServerUtil.validateQueryResult(tmgr, psAdvanceDelete.executeUpdate(), psAdvanceDelete);

                    }

                    if ("ONLINE-AUDIT".equalsIgnoreCase(vehicleType)) {
                        String selectAuditSql = "select * from  " + TableList.VA_AUDIT_RECOVERY + " where appl_no = ?";

                        PreparedStatement psSelectAudit = tmgr.prepareStatement(selectAuditSql);
                        psSelectAudit.setString(1, payDobj.getApplNo());
                        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {

                            AuditRecoveryDobj selectedauditRecovery = new AuditRecoveryDobj();
                            selectedauditRecovery.setRegn_no(rs.getString("regn_no"));
                            selectedauditRecovery.setPara_no(rs.getString("para_no"));
                            selectedauditRecovery.setPara_year(rs.getString("para_year"));
                            selectedauditRecovery.setAudit_ty(rs.getString("audit_ty"));
                            selectedauditRecovery.setPay_dt(new Date());

                            EntryAuditRecoveryImpl.updateAuditRecoveryDtls(tmgr, TableConstants.ONLINE_PAYMENT, selectedauditRecovery, payDobj.getAmount(), payDobj.getReceiptNo());

                            int insertIntoVtAudit = EntryAuditRecoveryImpl.insertFromVA_TOVT_AUDIT_RECOVERY(tmgr, selectedauditRecovery);
                            if (insertIntoVtAudit == 0) {
                                throw new VahanException("Problem in moving data in  Online Payment of Audit.");
                            }
                        }
                    }

                    feeImpl.saveFinePaneltyExemDetails(payDobj.getApplNo(), payDobj.getReceiptNo(), tmgr, userId);

                    if (payDobj.getTaxBreakUpDetails() != null && payDobj.getTaxBreakUpDetails().size() > 0 && !CommonUtils.isNullOrBlank(vehicleType) && !vehicleType.equalsIgnoreCase("BalanceFee")) {
                        TaxServer_Impl taxServerImpl = new TaxServer_Impl();
                        PassengerPermitDetailDobj permitDobj = new PassengerPermitDetailDobj();
                        permitDobj.setPmt_type_code(payDobj.getPmt_type_code());
                        permitDobj.setPmtCatg(payDobj.getPmtCatg());
                        permitDobj.setServices_TYPE(payDobj.getServices_TYPE());
                        permitDobj.setRout_code(payDobj.getRout_code());
                        permitDobj.setRout_length(payDobj.getRout_length());
                        permitDobj.setNumberOfTrips(payDobj.getNumberOfTrips());
                        permitDobj.setDomain_CODE(payDobj.getDomain_CODE());
                        taxServerImpl.saveTaxBasedInformation(permitDobj, payDobj.getOwnerDobj(), payDobj.getReceiptNo(), payDobj.getNoOfAdvUnits() != null ? payDobj.getNoOfAdvUnits().toString() : null, tmgr);
                    }
                    if ("DL".equals(stateCd) && payDobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE && payDobj.getOwnerDobj().getRegn_no() != null
                            && payDobj.getOwnerDobj().getVh_class() == TableConstants.ERICKSHAW_VCH_CLASS && payDobj.getOwnerDobj().getOwner_cd() == TableConstants.OWNER_CODE_INDIVIDUAL && !"NEW".equals(payDobj.getOwnerDobj().getRegn_no())) {
                        PendencyBankDetailImpl.updateRegnNoForBankSubsidy(tmgr, payDobj.getOwnerDobj().getRegn_no(), stateCd, offCd, payDobj.getApplNo());
                    }

                    registeredVehicle++;
                }
                Status_dobj status_dobj = new Status_dobj();
                VehicleParameters parm = FormulaUtils.fillVehicleParametersFromDobj(payDobj.getOwnerDobj());
                parm.setPUR_CD(payDobj.getPurCd());
                if (payDobj.getOwnerDobj() != null && payDobj.getOwnerDobj().getOtherStateVehDobj() != null && parm != null) {
                    parm.setLOGIN_OFF_CD(Util.getSelectedSeat().getOff_cd());
                    if (payDobj.getOwnerDobj() != null && payDobj.getOwnerDobj().getOtherStateVehDobj().getOldOffCD() != null) {
                        parm.setPREV_OFF_CD(payDobj.getOwnerDobj().getOtherStateVehDobj().getOldOffCD());
                    }
                }
                status_dobj.setVehicleParameters(parm);
                status_dobj.setAction_cd(actionCode);
                status_dobj.setAppl_no(payDobj.getApplNo());
                status_dobj.setPur_cd(payDobj.getPurCd());
                status_dobj.setStatus(TableConstants.STATUS_COMPLETE);
                status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
                status_dobj.setOffice_remark("");
                status_dobj.setPublic_remark("");
                status_dobj.setCntr_id("");
                if (userCatg != null && userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                    status_dobj.setEmp_cd(Long.parseLong(userId));
                } else {
                    status_dobj.setEmp_cd(0);
                }
                status_dobj.setOff_cd(offCd);
                if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("RegisteredVehicles")) {
                    if (payDobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_NOC || payDobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_REM_HYPO) {
                        VehicleParameters vehParameters = fillVehicleParametersFromDobj(payDobj.getOwnerDobj());
                        ToImpl toImpl = new ToImpl();
                        if (toImpl.isSurrenderRetention(payDobj.getApplNo())) {
                            vehParameters.setNOC_RETENTION(1);
                        }
                        if (!feeImpl.getTmPurposeActionFlowCondtion(Util.getUserStateCode(), payDobj.getPurCd(), TableConstants.TM_ACTION_REGISTERED_VEH_FEE, vehParameters)) {
                            continue;//for skipping fee of HPT,NOC
                        }
                    }
                }
                if (!CommonUtils.isNullOrBlank(vehicleType) && !(vehicleType.equalsIgnoreCase("BalanceFee") || vehicleType.equalsIgnoreCase("ONLINETAX") || "TAX-INSTALLMENT".equalsIgnoreCase(vehicleType) || vehicleType.equalsIgnoreCase("ONLINE-FANCY") || vehicleType.equalsIgnoreCase("ONLINE-AUDIT"))) {
                    ServerUtil.fileFlow(tmgr, status_dobj);
                }

                if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("ONLINETAX")) {
                    NonUseDobj nonUseDobj = new NonUseImpl().geTotalNonUseAmount(payDobj.getOwnerDobj());
                    if (nonUseDobj != null) {
                        NonUseImpl nonUseImpl = new NonUseImpl();
                        nonUseImpl.insertInVhNonUseAdjustFromVtNonUseAdjust(nonUseDobj, tmgr);
                        nonUseImpl.deleteFromVtNonUseAdjust(nonUseDobj, tmgr);
                    }
                }
            }
            int regVehCount = 0;
            for (OnlinePayDobj taxBrkupDobj : onlinePayDobjList) {
                if ((!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("RegisteredVehicles") && regVehCount == 0)
                        || (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("NewVehicle"))
                        || (!CommonUtils.isNullOrBlank(vehicleType) && (vehicleType.equalsIgnoreCase("BalanceFee") || vehicleType.equalsIgnoreCase("ONLINETAX") || "TAX-INSTALLMENT".equalsIgnoreCase(vehicleType) || vehicleType.equalsIgnoreCase("ONLINE-FANCY") || vehicleType.equalsIgnoreCase("ONLINE-AUDIT")) && regVehCount == 0)) {
                    if (taxBrkupDobj.getTaxBreakUpDetails() != null && taxBrkupDobj.getTaxBreakUpDetails().size() > 0) {
                        for (OnlinePayDobj taxBreakUp : taxBrkupDobj.getTaxBreakUpDetails()) {

                            String taxBreakUpDetailsSql = "INSERT INTO " + TableList.VT_TAX_BREAKUP + "(state_cd, off_cd, rcpt_no, sr_no, tax_from, tax_upto, pur_cd, \n"
                                    + " prv_adjustment, tax, exempted, rebate, surcharge, penalty, interest, tax1, tax2)\n"
                                    + "    VALUES (?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                            PreparedStatement psTaxBreakUpDetails = tmgr.prepareStatement(taxBreakUpDetailsSql);
                            int i = 1;

                            psTaxBreakUpDetails.setString(i++, stateCd);//   state_cd
                            psTaxBreakUpDetails.setInt(i++, offCd);//   off_cd
                            psTaxBreakUpDetails.setString(i++, taxBrkupDobj.getReceiptNo());
                            psTaxBreakUpDetails.setInt(i++, taxBreakUp.getSrNo());
                            psTaxBreakUpDetails.setDate(i++, new java.sql.Date(sdf.parse(taxBreakUp.getPeriodFrom()).getTime()));
                            if (!taxBreakUp.getPeriodUpto().equals("")) {
                                psTaxBreakUpDetails.setDate(i++, new java.sql.Date(sdf.parse(taxBreakUp.getPeriodUpto()).getTime()));
                            } else {
                                psTaxBreakUpDetails.setNull(i++, java.sql.Types.DATE);
                            }
                            psTaxBreakUpDetails.setInt(i++, taxBreakUp.getPurCd());//pur_cd
                            psTaxBreakUpDetails.setLong(i++, (long) taxBreakUp.getPrevAdjustement());//prv_adjustment
                            psTaxBreakUpDetails.setDouble(i++, taxBreakUp.getTaxBreakUpAmount());//tax
                            psTaxBreakUpDetails.setLong(i++, taxBreakUp.getExempted());//exempted
                            psTaxBreakUpDetails.setDouble(i++, taxBreakUp.getTaxBreakUpRebate());//rebate
                            psTaxBreakUpDetails.setDouble(i++, taxBreakUp.getTaxBreakUpSurcharge());//surcharge
                            psTaxBreakUpDetails.setDouble(i++, taxBreakUp.getTaxBreakUpPenalty());//penalty
                            psTaxBreakUpDetails.setDouble(i++, taxBreakUp.getTaxBreakUpInterest());//interest
                            psTaxBreakUpDetails.setDouble(i++, taxBreakUp.getTaxBreakUpAmount1());//tax1
                            psTaxBreakUpDetails.setDouble(i++, taxBreakUp.getTaxBreakUpAmount2());//tax2                 
                            ServerUtil.validateQueryResult(tmgr, psTaxBreakUpDetails.executeUpdate(), psTaxBreakUpDetails);

                            String taxBreakUpDetailsDeleteSql = "DELETE FROM vp_cart_tax_breakup where appl_no = ? and pur_cd = ? and sr_no = ? ";
                            PreparedStatement psTaxBreakUpDetailsDelete = tmgr.prepareStatement(taxBreakUpDetailsDeleteSql);
                            psTaxBreakUpDetailsDelete.setString(1, taxBreakUp.getApplNo());
                            psTaxBreakUpDetailsDelete.setInt(2, taxBreakUp.getPurCd());
                            psTaxBreakUpDetailsDelete.setInt(3, taxBreakUp.getSrNo());
                            ServerUtil.validateQueryResult(tmgr, psTaxBreakUpDetailsDelete.executeUpdate(), psTaxBreakUpDetailsDelete);
                        }
                        regVehCount++;
                    }
                    if (taxBrkupDobj.getFeeBreakUpDetails() != null && taxBrkupDobj.getFeeBreakUpDetails().size() > 0) {
                        for (OnlinePayDobj feeBreakUp : taxBrkupDobj.getFeeBreakUpDetails()) {

                            String feeBreakUpDetailsSql = "insert into " + TableList.VT_FEE_BREAKUP
                                    + "(state_cd, off_cd, rcpt_no, sr_no, fee_from, fee_upto, pur_cd,"
                                    + " fee, fine, exempted, rebate, surcharge, interest, prv_adjustment)"
                                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                            PreparedStatement psFeeBreakUpDetails = tmgr.prepareStatement(feeBreakUpDetailsSql);
                            int m = 1;

                            psFeeBreakUpDetails.setString(m++, stateCd);//   state_cd
                            psFeeBreakUpDetails.setInt(m++, offCd);//   off_cd
                            psFeeBreakUpDetails.setString(m++, taxBrkupDobj.getReceiptNo());
                            psFeeBreakUpDetails.setInt(m++, feeBreakUp.getSrNo());
                            psFeeBreakUpDetails.setDate(m++, new java.sql.Date(sdf.parse(feeBreakUp.getPeriodFrom()).getTime()));
                            if (!feeBreakUp.getPeriodUpto().equals("")) {
                                psFeeBreakUpDetails.setDate(m++, new java.sql.Date(sdf.parse(feeBreakUp.getPeriodUpto()).getTime()));
                            } else {
                                psFeeBreakUpDetails.setNull(m++, java.sql.Types.DATE);
                            }
                            psFeeBreakUpDetails.setInt(m++, feeBreakUp.getPurCd());

                            psFeeBreakUpDetails.setLong(m++, feeBreakUp.getFeeBreakUpAmount());
                            psFeeBreakUpDetails.setLong(m++, feeBreakUp.getFeeBreakUpFine());
                            psFeeBreakUpDetails.setLong(m++, feeBreakUp.getExempted());
                            psFeeBreakUpDetails.setLong(m++, feeBreakUp.getFeeBreakUpRebate());
                            psFeeBreakUpDetails.setLong(m++, feeBreakUp.getFeeBreakUpSurcharge());
                            psFeeBreakUpDetails.setLong(m++, feeBreakUp.getFeeBreakUpInterest());
                            psFeeBreakUpDetails.setLong(m++, feeBreakUp.getPrevAdjustement());
                            ServerUtil.validateQueryResult(tmgr, psFeeBreakUpDetails.executeUpdate(), psFeeBreakUpDetails);

                            String feeBreakUpDetailsDeleteSql = "DELETE FROM " + TableList.VP_CART_FEE_BREAKUP + " where appl_no = ? and pur_cd = ? and sr_no = ? ";
                            PreparedStatement psFeeBreakUpDetailsDelete = tmgr.prepareStatement(feeBreakUpDetailsDeleteSql);
                            psFeeBreakUpDetailsDelete.setString(1, feeBreakUp.getApplNo());
                            psFeeBreakUpDetailsDelete.setInt(2, feeBreakUp.getPurCd());
                            psFeeBreakUpDetailsDelete.setInt(3, feeBreakUp.getSrNo());
                            ServerUtil.validateQueryResult(tmgr, psFeeBreakUpDetailsDelete.executeUpdate(), psFeeBreakUpDetailsDelete);
                        }
                        regVehCount++;
                    }
                }
            }
            if (userCatg != null && userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                TmConfigurationDobj tmConf = Util.getTmConfiguration();
                if (tmConf != null && tmConf.getTmConfigDealerDobj() != null && tmConf.isTempFeeInNewRegis()) {
                    for (OnlinePayDobj tempDobj : onlinePayDobjList) {
                        if (tempDobj.isValidForTemp()) {
                            if (!tmConf.getTmConfigDealerDobj().isTempRegnApprovalBeforeNewRegn()) {
                                String tempSQL = "INSERT into  " + TableList.VT_OWNER_TEMP + " Select state_cd, off_cd, appl_no, ?, ?, ? , purchase_dt, owner_name, f_name, c_add1, c_add2, c_add3, c_district,  c_pincode, c_state, p_add1, "
                                        + " p_add2, p_add3, p_district, p_pincode, p_state, owner_cd, regn_type, vh_class, chasi_no, eng_no, maker,maker_model, body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap,"
                                        + " unld_wt, ld_wt, gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, \n"
                                        + " cubic_cap, floor_area, ac_fitted, audio_fitted, video_fitted, \n"
                                        + " vch_purchase_as, vch_catg, dealer_cd, sale_amt, laser_code, garage_add, \n"
                                        + " length, width, height, regn_upto, fit_upto, annual_income, imported_vch, \n"
                                        + " other_criteria, ?,?, current_timestamp as op_dt,?,?"
                                        + " FROM " + TableList.VA_OWNER + " where appl_no = ? ";

                                PreparedStatement ps = tmgr.prepareStatement("SELECT *  FROM vm_validity_mast where pur_cd=? and state_cd=?");
                                ps.setInt(1, TableConstants.VM_TRANSACTION_MAST_TEMP_REG);
                                ps.setString(2, stateCd);
                                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                                Date dtTempFrom = new Date();
                                Date dtTempUpto = new Date();
                                VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(tempDobj.getOwnerDobj());
                                vehParameters.setTMP_PURPOSE("OR");
                                while (rs.next()) {
                                    if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "dealerPayment")) {
                                        int newUpto = rs.getInt("new_value");
                                        String mod = rs.getString("mod");
                                        if (mod.equals("D")) {
                                            dtTempUpto = DateUtils.addToDate(dtTempFrom, 1, newUpto);
                                        } else if (mod.equals("M")) {
                                            dtTempUpto = DateUtils.addToDate(dtTempFrom, 2, newUpto);
                                            dtTempUpto = DateUtils.addToDate(dtTempUpto, 1, -1);
                                        } else if (mod.equals("Y")) {
                                            dtTempUpto = DateUtils.addToDate(dtTempFrom, 3, newUpto);
                                            dtTempUpto = DateUtils.addToDate(dtTempUpto, 1, -1);
                                        }
                                    }
                                }
                                if (DateUtils.compareDates(dtTempUpto, new Date()) <= 1) {
                                    dtTempUpto = DateUtils.addToDate(dtTempFrom, 2, 1);
                                    dtTempUpto = DateUtils.addToDate(dtTempUpto, 1, -1);
                                }

                                String tempRegNo = ServerUtil.getUniquePermitNo(tmgr, stateCd, offCd, 0, 0, "M");

                                if (tempRegNo == null || tempRegNo.equalsIgnoreCase("") || tempRegNo.equalsIgnoreCase("TEMPREG")) {
                                    throw new VahanException("Temporary Registration No Generation Failed!!!");
                                }
                                PreparedStatement psTempInsert = tmgr.prepareStatement(tempSQL);
                                psTempInsert.setString(1, tempRegNo);
                                psTempInsert.setDate(2, new java.sql.Date(dtTempFrom.getTime()));
                                psTempInsert.setDate(3, new java.sql.Date(dtTempUpto.getTime()));
                                psTempInsert.setString(4, stateCd);
                                psTempInsert.setInt(5, offCd);
                                psTempInsert.setString(6, "OR");
                                psTempInsert.setString(7, null);
                                psTempInsert.setString(8, tempDobj.getApplNo());
                                ServerUtil.validateQueryResult(tmgr, psTempInsert.executeUpdate(), psTempInsert);

                                String vaTempRCPrintSql = "INSERT INTO " + TableList.VA_TEMP_RC_PRINT + "( state_cd, off_cd, appl_no, temp_regn_no, dealer_cd, op_dt ) \n "
                                        + " VALUES (?, ?, ?, ?, ?, current_timestamp)";

                                PreparedStatement psVaTempRCPrint = tmgr.prepareStatement(vaTempRCPrintSql);
                                psVaTempRCPrint.setString(1, stateCd);
                                psVaTempRCPrint.setInt(2, offCd);
                                psVaTempRCPrint.setString(3, tempDobj.getApplNo());
                                psVaTempRCPrint.setString(4, tempRegNo);
                                psVaTempRCPrint.setString(5, tempDobj.getOwnerDobj().getDealer_cd());
                                ServerUtil.validateQueryResult(tmgr, psVaTempRCPrint.executeUpdate(), psVaTempRCPrint);
                            } else if (tmConf.getTmConfigDealerDobj().isTempRegnApprovalBeforeNewRegn()) { //In case of KL,OR
                                if (!Util.getUserLoginOffCode().equals(offCd)) {
                                    if ("KL".equals(stateCd)) {
                                        this.insertTemporaryRegnDetails(offCd, offCd, "OR", tempDobj, userId, stateCd, transactionNo, tmgr, tmConf.getTmConfigDealerDobj().getTempFlowInNewRegnActionCd());
                                    } else {
                                        this.insertTemporaryRegnDetails(offCd, Util.getUserLoginOffCode(), "OR", tempDobj, userId, stateCd, transactionNo, tmgr, tmConf.getTmConfigDealerDobj().getTempFlowInNewRegnActionCd());
                                    }
                                } else if ((Util.getUserLoginOffCode().equals(offCd)) && tmConf.getTmConfigDealerDobj().isTempRegnFeeWithNewForSameOffices()) {
                                    this.insertTemporaryRegnDetails(offCd, offCd, "SR", tempDobj, userId, stateCd, transactionNo, tmgr, tmConf.getTmConfigDealerDobj().getTempFlowInNewRegnActionCd());
                                }
                            }
                        }
                    }
                }
            }
            String cartSql = "INSERT INTO vph_rcpt_cart  SELECT current_timestamp,?,state_cd, off_cd,user_cd, appl_no, pur_cd ,period_mode, period_from, period_upto, amount, exempted, \n"
                    + " rebate, surcharge, penalty, interest, transaction_no, rcpt_no , rcpt_dt, pmt_type , \n"
                    + " pmt_catg , service_type, route_class, route_length , no_of_trips , domain_cd , distance_run_in_quarter,op_dt,no_adv_units,tax1,tax2,prv_adjustment \n"
                    + " FROM vp_rcpt_cart where user_cd = ? and transaction_no = ? and transaction_no IS NOT NULL";

            PreparedStatement psCart = tmgr.prepareStatement(cartSql);
            if (userCatg != null && userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                psCart.setString(1, userId);
            } else {
                psCart.setString(1, TableConstants.ONLINE_PAYMENT);
            }
            psCart.setLong(2, Long.parseLong(userId));
            psCart.setString(3, transactionNo);
            ServerUtil.validateQueryResult(tmgr, psCart.executeUpdate(), psCart);

            String cartDeleteSql = "delete from vp_rcpt_cart where user_cd = ? and transaction_no = ? and transaction_no IS NOT NULL";
            PreparedStatement psCartDelete = tmgr.prepareStatement(cartDeleteSql);
            psCartDelete.setLong(1, Long.parseLong(userId));
            psCartDelete.setString(2, transactionNo);
            ServerUtil.validateQueryResult(tmgr, psCartDelete.executeUpdate(), psCartDelete);

            if (userCatg != null && !userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                String userIdSql = "INSERT INTO vph_online_pay_user_info SELECT current_timestamp,?,state_cd, off_cd, user_cd, user_pwd, mobile_no, created_by, created_dt,appl_no\n"
                        + "  FROM vp_online_pay_user_info where user_cd = ? ";

                PreparedStatement psUserId = tmgr.prepareStatement(userIdSql);
                psUserId.setString(1, TableConstants.ONLINE_PAYMENT);
                psUserId.setLong(2, Long.parseLong(userId));
                ServerUtil.validateQueryResult(tmgr, psUserId.executeUpdate(), psUserId);

                String userIdDeleteSql = "delete from vp_online_pay_user_info where user_cd = ? ";
                PreparedStatement psUserIdDelete = tmgr.prepareStatement(userIdDeleteSql);
                psUserIdDelete.setLong(1, Long.parseLong(userId));
                ServerUtil.validateQueryResult(tmgr, psUserIdDelete.executeUpdate(), psUserIdDelete);
                // In case of Balance fee
                if (!CommonUtils.isNullOrBlank(vehicleType) && (vehicleType.equalsIgnoreCase("BalanceFee") || vehicleType.equalsIgnoreCase("ONLINETAX") || "TAX-INSTALLMENT".equalsIgnoreCase(vehicleType) || vehicleType.equals("ONLINE-FANCY") || vehicleType.equals("ONLINE-AUDIT"))) {
                    String BalanceInsertSql = "INSERT INTO " + TableList.VPH_ONLINE_PAY_BALANCE_FEE_DETAILS + " SELECT current_timestamp, ?, state_cd, off_cd, user_cd, appl_no, "
                            + "regn_no, owner_name, selected_option, chasi_no, op_dt,form_type,vh_class FROM " + TableList.VP_ONLINE_PAY_BALANCE_FEE_DETAILS + "  where user_cd = ?  ";

                    PreparedStatement psBalance = tmgr.prepareStatement(BalanceInsertSql);
                    psBalance.setString(1, TableConstants.ONLINE_PAYMENT);
                    psBalance.setLong(2, Long.parseLong(userId));
                    ServerUtil.validateQueryResult(tmgr, psBalance.executeUpdate(), psBalance);

                    String BalanceDeleteSql = "delete from " + TableList.VP_ONLINE_PAY_BALANCE_FEE_DETAILS + " where user_cd = ? ";
                    PreparedStatement psBalanceDelete = tmgr.prepareStatement(BalanceDeleteSql);
                    psBalanceDelete.setLong(1, Long.parseLong(userId));
                    ServerUtil.validateQueryResult(tmgr, psBalanceDelete.executeUpdate(), psBalanceDelete);
                }
            }
            if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("NewVehicle")) {
                payDobjList = this.getEReceiptDetailForNewVehicles(transactionNo, tmgr, stateCd, offCd);
            } else if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("RegisteredVehicles")) {
                payDobjList = this.getEReceiptDetailForRegisteredVehicles(transactionNo, onlinePayDobjList.get(0).getRegnNo(), tmgr, onlinePayDobjList.get(0).getPurCd());
            } else if (!CommonUtils.isNullOrBlank(vehicleType) && (vehicleType.equalsIgnoreCase("BalanceFee") || vehicleType.equalsIgnoreCase("ONLINETAX") || "TAX-INSTALLMENT".equalsIgnoreCase(vehicleType) || vehicleType.equals("ONLINE-FANCY") || vehicleType.equalsIgnoreCase("ONLINE-AUDIT"))) {
                payDobjList = this.getEReceiptDetailForBalanceFee(stateCd, offCd, transactionNo, userId, tmgr);
            } else if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("TradeCertificate")) {
                payDobjList = this.getEReceiptDetailForTradeCertificate(transactionNo, tmgr, stateCd, offCd);
            }

            for (OnlinePayDobj payDobj : dtlDobjList) {
                if (("DL,UP").contains(stateCd) && dtlDobjList != null && (payDobj.getPurCd() == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD || payDobj.getPurCd() == TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD || payDobj.getPurCd() == TableConstants.VM_PMT_FRESH_PUR_CD)) {
                    PassengerPermitDetailDobj passDobjVa = new PassengerPermitDetailImpl().set_permit_appl_db_to_dobj(payDobj.getApplNo());
                    if (passDobjVa != null && passDobjVa.getPmt_type().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT) && passDobjVa.getPaction().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_FRESH_PUR_CD))) {
                        PermitFeeDobj dobj = new PermitFeeDobj();
                        dobj.setPaymentStaus("Y");
                        dobj.setPur_cd(passDobjVa.getPaction());
                        dobj.setAppl_no(payDobj.getApplNo());
                        dobj.setRegn_no(passDobjVa.getRegnNo());
                        dobj.setPermit_type(passDobjVa.getPmt_type());
                        dobj.setPermit_catg(CommonUtils.isNullOrBlank(passDobjVa.getPmtCatg()) ? -1 : Integer.parseInt(passDobjVa.getPmtCatg()));
                        insertNpPermitDetails(dobj, null, tmgr);
                        break;
                    } else if (passDobjVa != null && passDobjVa.getPmt_type().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT) && passDobjVa.getPaction().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEWAL_PUR_CD))) {
                        PermitFeeDobj dobj = new PermitFeeDobj();
                        dobj.setPaymentStaus("Y");
                        dobj.setPur_cd(passDobjVa.getPaction());
                        dobj.setAppl_no(payDobj.getApplNo());
                        dobj.setRegn_no(passDobjVa.getRegnNo());
                        dobj.setPermit_type(passDobjVa.getPmt_type());
                        dobj.setPermit_catg(CommonUtils.isNullOrBlank(passDobjVa.getPmtCatg()) ? -1 : Integer.parseInt(passDobjVa.getPmtCatg()));
                        insertNpPermitDetails(dobj, null, tmgr);
                        break;
                    } else {
                        PermitHomeAuthDobj authDobj = new PermitHomeAuthImpl().getVaHomeAuthDetails(payDobj.getApplNo());
                        if (authDobj != null) {
                            PassengerPermitDetailDobj passDobjVt = new PassengerPermitDetailImpl().set_vt_permit_regnNo_to_dobj(authDobj.getRegnNo(), null);
                            if (passDobjVt != null && passDobjVt.getPmt_type().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)) {
                                PermitFeeDobj dobj = new PermitFeeDobj();
                                dobj.setPur_cd(String.valueOf(payDobj.getPurCd()));
                                dobj.setPaymentStaus("Y");
                                dobj.setAppl_no(payDobj.getApplNo());
                                dobj.setRegn_no(authDobj.getRegnNo());
                                dobj.setPermit_type(passDobjVt.getPmt_type());
                                dobj.setPermit_catg(CommonUtils.isNullOrBlank(passDobjVt.getPmtCatg()) ? -1 : Integer.parseInt(passDobjVt.getPmtCatg()));
                                insertNpPermitDetails(dobj, null, tmgr);
                                break;
                            }
                        }
                    }
                }
            }
            this.sendSms(transactionNo, tmgr);
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + transactionNo + " " + e.getStackTrace()[0]);
            throw new VahanException("Unable to proccess your request.Please Try Again");
        }
        return payDobjList;
    }

    /*
     * @author Kartikey Singh
     * This function used to insert data in all fee related tables.
     */
    public List<OnlinePayDobj> saveFeeDetailsAfterSuccesfullPayment(TransactionManager tmgr, String transactionNo, List<OnlinePayDobj> onlinePayDobjList,
            int actionCode, String userId, String stateCd, int offCd, String userCatg, String vehicleType, java.sql.Timestamp rcptTimeStamp,
            String balanceFeeSelectOption, String empCode, int userLoginOffCode, String selectedRoleCode, String clientIpAddress, TmConfigurationDobj tmConfigDobj, String sessionUserId) throws VahanException {
        List<OnlinePayDobj> dtlDobjList = null;
        List<OnlinePayDobj> payDobjList = null;
        List<OnlinePayDobj> feeDbjList = new ArrayList<>();
        FeeImplementation feeImplementation = new FeeImplementation();
        boolean blackListedCompoundingAmt = false;
        try {
            if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("NewVehicle")) {
                dtlDobjList = getDetailListForNewVehicles(tmgr, transactionNo, userId);
            } else if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("RegisteredVehicles")) {
                dtlDobjList = getDetailListForRegisteredVehicles(tmgr, transactionNo, userId, onlinePayDobjList.get(0).getApplNo(), onlinePayDobjList.get(0).getPurCd(), onlinePayDobjList.get(0).getRegnNo());
            } else if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("BalanceFee") || vehicleType.equalsIgnoreCase("ONLINETAX") || vehicleType.equalsIgnoreCase("TAX-INSTALLMENT") || vehicleType.equalsIgnoreCase("ONLINE-FANCY") || vehicleType.equalsIgnoreCase("ONLINE-AUDIT")) {
                dtlDobjList = onlinePayDobjList;
                BlackListedVehicleDobj blackListedDobj = new BlackListedVehicleImpl().getBlacklistedVehicleDetails(dtlDobjList.get(0).getRegnNo(), null);
                if (blackListedDobj != null) {
                    if (blackListedDobj.getComplain_type() == TableConstants.BLCompoundingAmtCode) {
                        blackListedCompoundingAmt = true;
                    }
                }
            } else if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("TradeCertificate")) {
                dtlDobjList = getDetailListForTradeCertificate(tmgr, transactionNo, userId);
            }
// FEE_FINE_EXEMTION
            if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("RegisteredVehicles") && dtlDobjList != null && dtlDobjList.size() > 0) {
                dtlDobjList = getDetailListForExemptedPMTFee(tmgr, dtlDobjList, onlinePayDobjList.get(0).getApplNo(), transactionNo, userId);
            }
            for (OnlinePayDobj feeDobj : dtlDobjList) {
                if (feeDobj.getPeriodFrom().equals("") && feeDobj.getPeriodUpto().equals("")) {

                    String feeSql = "INSERT INTO vt_fee(regn_no, payment_mode, fees, fine, rcpt_no, rcpt_dt, pur_cd, \n"
                            + " flag, collected_by, state_cd, off_cd) \n"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, \n"
                            + " ?, ?, ?, ?) ";

                    PreparedStatement psFee = tmgr.prepareStatement(feeSql);
                    int i = 1;
                    psFee.setString(i++, feeDobj.getRegnNo());
                    psFee.setString(i++, TableConstants.PAYMENT_MODE);
                    psFee.setLong(i++, feeDobj.getAmount());
                    psFee.setLong(i++, feeDobj.getPenalty());
                    psFee.setString(i++, feeDobj.getReceiptNo());
                    psFee.setTimestamp(i++, rcptTimeStamp);
                    psFee.setInt(i++, feeDobj.getPurCd());
                    psFee.setNull(i++, java.sql.Types.VARCHAR);
                    if (userCatg != null && userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                        psFee.setString(i++, userId);
                    } else {
                        psFee.setString(i++, TableConstants.ONLINE_PAYMENT);
                    }
                    psFee.setString(i++, stateCd);
                    psFee.setInt(i++, offCd);
                    // Purcd =72 for Tax clearance certificate 
                    if (feeDobj.getPurCd() == 72) {
                        TaxClearanceCertificatePrintImplementation.insertintoVA_TCC_Print(feeDobj.getRegnNo(), feeDobj.getApplNo(), tmgr, stateCd, offCd);
                    }
                    if (feeDobj.getPurCd() == TableConstants.BLCompoundingAmtCode && blackListedCompoundingAmt) {
                        BlackListedVehicleImplementation impl = new BlackListedVehicleImplementation();
                        impl.insertIntoVhBlacklistFromVtBlacklist(tmgr, feeDobj.getRegnNo(), stateCd, offCd, feeDobj.getReceiptNo(), empCode);
                    }
                    ServerUtility.validateQueryResult(tmgr, psFee.executeUpdate(), psFee);
                    if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("RegisteredVehicles")) {
                        feeDbjList.add(feeDobj);
                    }
                }
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (OnlinePayDobj taxDobj : dtlDobjList) {
                if (!taxDobj.getPeriodFrom().equals("")) {

                    String taxSql = "INSERT INTO vt_tax(regn_no, tax_mode, payment_mode, tax_amt, tax_fine, rcpt_no, \n"
                            + " rcpt_dt, tax_from, tax_upto, pur_cd, flag, collected_by, state_cd, off_cd) \n"
                            + " VALUES (?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?) ";

                    PreparedStatement psTax = tmgr.prepareStatement(taxSql);
                    int i = 1;
                    psTax.setString(i++, taxDobj.getRegnNo());
                    if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("BalanceFee")) {
                        psTax.setString(i++, TableConstants.TAX_MODE_BALANCE);
                    } else {
                        psTax.setString(i++, taxDobj.getTaxMode());
                    }
                    psTax.setString(i++, TableConstants.PAYMENT_MODE);
                    psTax.setLong(i++, taxDobj.getAmount());
                    psTax.setLong(i++, taxDobj.getPenalty());
                    psTax.setString(i++, taxDobj.getReceiptNo());
                    psTax.setTimestamp(i++, rcptTimeStamp);
                    psTax.setDate(i++, new java.sql.Date(sdf.parse(taxDobj.getPeriodFrom()).getTime()));
                    if (!taxDobj.getPeriodUpto().equals("")) {
                        psTax.setDate(i++, new java.sql.Date(sdf.parse(taxDobj.getPeriodUpto()).getTime()));
                    } else {
                        psTax.setNull(i++, java.sql.Types.DATE);
                    }
                    psTax.setInt(i++, taxDobj.getPurCd());
                    psTax.setNull(i++, java.sql.Types.VARCHAR);
                    if (userCatg != null && userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                        psTax.setString(i++, userId);
                    } else {
                        psTax.setString(i++, TableConstants.ONLINE_PAYMENT);
                    }
                    psTax.setString(i++, stateCd);
                    psTax.setInt(i++, offCd);
                    ServerUtility.validateQueryResult(tmgr, psTax.executeUpdate(), psTax);
                    if (!CommonUtils.isNullOrBlank(vehicleType) && (vehicleType.equalsIgnoreCase("BalanceFee") || vehicleType.equalsIgnoreCase("ONLINETAX"))) {
                        new TaxClearImplementation().updateAppl_noInVt_Refund_Excess(taxDobj.getApplNo(), taxDobj.getPurCd(), taxDobj.getRegnNo(), TableConstants.TM_ROLE_BALANCE_FEE_TAX_COLLECTION, tmgr, stateCd);
                    }
                }
            }

            // update owner dobj for to alt and conversion
            int counter = 0;
            Owner_dobj owner_dobj = null;
            if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("RegisteredVehicles")) {
                for (OnlinePayDobj payDobj : onlinePayDobjList) {
                    AltDobj altDobj = null;
                    ConvDobj convdobj = null;

                    if (counter == 0) {
                        owner_dobj = payDobj.getOwnerDobj();
                    }

                    if (payDobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_TO) {
                        String toDbjOwnerName = feeImplementation.updateOwnerNameForToOrFrc(payDobj.getPurCd(), payDobj.getApplNo(), payDobj.getOwnerDobj());
                        if (toDbjOwnerName != null) {
                            owner_dobj.setOwner_name(toDbjOwnerName);
                        }
                    }

                    if (payDobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_VEH_ALTER) {
                        AltImpl altImpl = new AltImpl();
                        altDobj = altImpl.set_ALT_appl_db_to_dobj(payDobj.getApplNo());
                        if (altDobj != null) {
                            feeImplementation.updateOwnerDobjForAltOrConv(altDobj, convdobj, owner_dobj, stateCd);
                        }
                    }

                    if (payDobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION) {
                        convdobj = ConvImpl.set_Conversion_appl_db_to_dobj(payDobj.getApplNo(), null);
                        if (convdobj != null) {
                            feeImplementation.updateOwnerDobjForAltOrConv(altDobj, convdobj, owner_dobj, stateCd);
                        }
                    }
                    if (payDobj.getPurCd() == TableConstants.VM_PMT_FRESH_PUR_CD && payDobj.getRegnNo().equalsIgnoreCase("NEW")) {
                        PermitOwnerDetailDobj PmtOwnerDobj = new PermitOwnerDetailImplementation().set_VA_Owner_permit_to_dobj(payDobj.getApplNo().toUpperCase(), payDobj.getRegnNo(), stateCd);
                        if (PmtOwnerDobj != null) {
                            owner_dobj.setOwner_name(PmtOwnerDobj.getOwner_name());
                            owner_dobj.setVh_class(PmtOwnerDobj.getVh_class());
                            owner_dobj.setChasi_no("");
                        }
                    }
                    counter++;
                }
                onlinePayDobjList.get(0).setOwnerDobj(owner_dobj);
            }
            int registeredVehicle = 0;
            for (OnlinePayDobj payDobj : onlinePayDobjList) {
                if ((!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("RegisteredVehicles")
                        && registeredVehicle == 0) || (!CommonUtils.isNullOrBlank(vehicleType)
                        && vehicleType.equalsIgnoreCase("NewVehicle")) || (!CommonUtils.isNullOrBlank(vehicleType)
                        && (vehicleType.equalsIgnoreCase("BalanceFee") || vehicleType.equalsIgnoreCase("ONLINETAX") || vehicleType.equalsIgnoreCase("TAX-INSTALLMENT") || vehicleType.equalsIgnoreCase("ONLINE-FANCY") || vehicleType.equalsIgnoreCase("ONLINE-AUDIT")) && registeredVehicle == 0)
                        || (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("TradeCertificate")
                        && registeredVehicle == 0)) {
                    String VpApplRcptMappingSql = "INSERT INTO " + TableList.VP_APPL_RCPT_MAPPING
                            + "( state_cd, off_cd, appl_no, rcpt_no, owner_name, chasi_no, vh_class, \n"
                            + " instrument_cd, excess_amt, cash_amt, remarks)"
                            + "    VALUES (?, ?, ?, ?, ?, ?, ?,?, ?, ?,?)";

                    PreparedStatement psVpApplRcptMapping = tmgr.prepareStatement(VpApplRcptMappingSql);
                    int i = 1;
                    psVpApplRcptMapping.setString(i++, stateCd);
                    psVpApplRcptMapping.setInt(i++, offCd);
                    psVpApplRcptMapping.setString(i++, payDobj.getApplNo());
                    psVpApplRcptMapping.setString(i++, payDobj.getReceiptNo());
                    if (!CommonUtils.isNullOrBlank(vehicleType) && !vehicleType.equalsIgnoreCase("TradeCertificate")) {
                        psVpApplRcptMapping.setString(i++, payDobj.getOwnerDobj().getOwner_name());
                        psVpApplRcptMapping.setString(i++, payDobj.getOwnerDobj().getChasi_no());
                        psVpApplRcptMapping.setInt(i++, payDobj.getOwnerDobj().getVh_class());
                    } else {
                        psVpApplRcptMapping.setString(i++, "");//For TC no owner name
                        psVpApplRcptMapping.setString(i++, "");//For TC no chasi No
                        psVpApplRcptMapping.setInt(i++, 0);    //For TC no vehicle class
                    }
                    psVpApplRcptMapping.setNull(i++, java.sql.Types.INTEGER);
                    psVpApplRcptMapping.setLong(i++, 0);
                    psVpApplRcptMapping.setLong(i++, 0);
                    String remarks = "";
                    if (!CommonUtils.isNullOrBlank(vehicleType)) {
                        if (vehicleType.equalsIgnoreCase("BalanceFee")) {
                            remarks = "BALANCE-";
                        } else if (vehicleType.equalsIgnoreCase("RegisteredVehicles") && ServerUtil.getOfficeCodeType(offCd, stateCd) == TableConstants.FITNESS_CENTER_OFF_TYPE_CODE) {
                            remarks = "Applicable Inspection fee and GST has to paid to Fitness Center";
                        } else {
                            remarks = "ONLINE-PAYMENT";
                        }
                    } else {
                        remarks = "ONLINE-PAYMENT";
                    }
                    ManualReceiptEntryDobj manualRcptDobjTemp = ManualReceiptEntryImpl.getApprovedManualReceiptDetails(payDobj.getApplNo());
                    if (manualRcptDobjTemp != null) {
                        remarks = "Rs. " + manualRcptDobjTemp.getAmount() + "/- adjusted against previously paid vide receipt no:" + manualRcptDobjTemp.getRcptNo() + ".";
                    }
                    psVpApplRcptMapping.setString(i++, remarks);
                    ServerUtility.validateQueryResult(tmgr, psVpApplRcptMapping.executeUpdate(), psVpApplRcptMapping);

                    if ("TAX-INSTALLMENT".equalsIgnoreCase(vehicleType)) {
                        String taxInstSQl = "select REGN_NO, SERIAL_NO, state_cd from " + TableList.VA_TAX_INSTALLMENT_BRKUP + " where regn_no = ?";
                        PreparedStatement psTaxInstSQl = tmgr.prepareStatement(taxInstSQl);
                        psTaxInstSQl.setString(1, payDobj.getOwnerDobj().getRegn_no());
                        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                        while (rs.next()) {
                            taxInstSQl = "UPDATE " + TableList.VT_TAX_INSTALLMENT_BRKUP + " SET RCPT_NO = ?"
                                    + " WHERE REGN_NO = ? and SERIAL_NO = ? ";

                            PreparedStatement ps = tmgr.prepareStatement(taxInstSQl);
                            ps.setString(1, payDobj.getReceiptNo());
                            ps.setString(2, payDobj.getOwnerDobj().getRegn_no());
                            ps.setString(3, rs.getString("SERIAL_NO"));
                            ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                            ServerUtility.deleteFromTable(tmgr, payDobj.getOwnerDobj().getRegn_no(), null, TableList.VA_TAX_INSTALLMENT_BRKUP);
                        }
                    }

                    if ("ONLINE-FANCY".equalsIgnoreCase(vehicleType)) {
                        String fancySql = "INSERT INTO " + TableList.VT_ADVANCE_REGN_NO + " SELECT state_cd, off_cd, ?, regn_appl_no, regn_no, owner_name,f_name, c_add1, c_add2,c_add3, c_district, c_pincode, c_state,mobile_no,total_amt from " + TableList.VP_ADVANCE_REGN_NO + " where pay_appl_no = ?";

                        PreparedStatement psFancyCart = tmgr.prepareStatement(fancySql);
                        psFancyCart.setString(1, payDobj.getReceiptNo());
                        psFancyCart.setString(2, payDobj.getApplNo());
                        ServerUtil.validateQueryResult(tmgr, psFancyCart.executeUpdate(), psFancyCart);

                        fancySql = "DELETE FROM " + TableList.VM_AVAILABLE_NO_FANCY + " WHERE regn_no=?";
                        PreparedStatement psVmAvailable = tmgr.prepareStatement(fancySql);
                        psVmAvailable.setString(1, payDobj.getOwnerDobj().getRegn_no());
                        psVmAvailable.executeUpdate();

                        fancySql = "delete from  " + TableList.VP_ADVANCE_REGN_NO + " where pay_appl_no = ? ";
                        PreparedStatement psAdvanceDelete = tmgr.prepareStatement(fancySql);
                        psAdvanceDelete.setString(1, payDobj.getApplNo());
                        ServerUtil.validateQueryResult(tmgr, psAdvanceDelete.executeUpdate(), psAdvanceDelete);

                    }

                    if ("ONLINE-AUDIT".equalsIgnoreCase(vehicleType)) {
                        String selectAuditSql = "select * from  " + TableList.VA_AUDIT_RECOVERY + " where appl_no = ?";

                        PreparedStatement psSelectAudit = tmgr.prepareStatement(selectAuditSql);
                        psSelectAudit.setString(1, payDobj.getApplNo());
                        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {

                            AuditRecoveryDobj selectedauditRecovery = new AuditRecoveryDobj();
                            selectedauditRecovery.setRegn_no(rs.getString("regn_no"));
                            selectedauditRecovery.setPara_no(rs.getString("para_no"));
                            selectedauditRecovery.setPara_year(rs.getString("para_year"));
                            selectedauditRecovery.setAudit_ty(rs.getString("audit_ty"));
                            selectedauditRecovery.setPay_dt(new Date());

                            EntryAuditRecoveryImpl.updateAuditRecoveryDtls(tmgr, TableConstants.ONLINE_PAYMENT, selectedauditRecovery, payDobj.getAmount(), payDobj.getReceiptNo());

                            int insertIntoVtAudit = EntryAuditRecoveryImpl.insertFromVA_TOVT_AUDIT_RECOVERY(tmgr, selectedauditRecovery);
                            if (insertIntoVtAudit == 0) {
                                throw new VahanException("Problem in moving data in  Online Payment of Audit.");
                            }
                        }
                    }

                    feeImplementation.saveFinePaneltyExemDetails(payDobj.getApplNo(), payDobj.getReceiptNo(), tmgr, userId);

                    if (payDobj.getTaxBreakUpDetails() != null && payDobj.getTaxBreakUpDetails().size() > 0 && !CommonUtils.isNullOrBlank(vehicleType) && !vehicleType.equalsIgnoreCase("BalanceFee")) {

                        PassengerPermitDetailDobj permitDobj = new PassengerPermitDetailDobj();
                        permitDobj.setPmt_type_code(payDobj.getPmt_type_code());
                        permitDobj.setPmtCatg(payDobj.getPmtCatg());
                        permitDobj.setServices_TYPE(payDobj.getServices_TYPE());
                        permitDobj.setRout_code(payDobj.getRout_code());
                        permitDobj.setRout_length(payDobj.getRout_length());
                        permitDobj.setNumberOfTrips(payDobj.getNumberOfTrips());
                        permitDobj.setDomain_CODE(payDobj.getDomain_CODE());
                        new TaxServerImplementation().saveTaxBasedInformation(permitDobj, payDobj.getOwnerDobj(), payDobj.getReceiptNo(), payDobj.getNoOfAdvUnits() != null ? payDobj.getNoOfAdvUnits().toString() : null,
                                tmgr, stateCd, offCd);
                    }
                    if ("DL".equals(stateCd) && payDobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE && payDobj.getOwnerDobj().getRegn_no() != null
                            && payDobj.getOwnerDobj().getVh_class() == TableConstants.ERICKSHAW_VCH_CLASS && payDobj.getOwnerDobj().getOwner_cd() == TableConstants.OWNER_CODE_INDIVIDUAL && !"NEW".equals(payDobj.getOwnerDobj().getRegn_no())) {
                        PendencyBankDetailImplementation.updateRegnNoForBankSubsidy(tmgr, payDobj.getOwnerDobj().getRegn_no(), stateCd, offCd, payDobj.getApplNo());
                    }

                    registeredVehicle++;
                }
                Status_dobj status_dobj = new Status_dobj();
                VehicleParameters parm = FormulaUtilities.fillVehicleParametersFromDobj(payDobj.getOwnerDobj(), offCd, userCatg, actionCode, userLoginOffCode, stateCd);
                parm.setPUR_CD(payDobj.getPurCd());
                if (payDobj.getOwnerDobj() != null && payDobj.getOwnerDobj().getOtherStateVehDobj() != null && parm != null) {
                    parm.setLOGIN_OFF_CD(userLoginOffCode);
                    if (payDobj.getOwnerDobj() != null && payDobj.getOwnerDobj().getOtherStateVehDobj().getOldOffCD() != null) {
                        parm.setPREV_OFF_CD(payDobj.getOwnerDobj().getOtherStateVehDobj().getOldOffCD());
                    }
                }
                status_dobj.setVehicleParameters(parm);
                status_dobj.setAction_cd(actionCode);
                status_dobj.setAppl_no(payDobj.getApplNo());
                status_dobj.setPur_cd(payDobj.getPurCd());
                status_dobj.setStatus(TableConstants.STATUS_COMPLETE);
                status_dobj = ServerUtility.webServiceForNextStage(status_dobj, tmgr, empCode, stateCd, offCd);
                status_dobj.setOffice_remark("");
                status_dobj.setPublic_remark("");
                status_dobj.setCntr_id("");
                if (userCatg != null && userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                    status_dobj.setEmp_cd(Long.parseLong(userId));
                } else {
                    status_dobj.setEmp_cd(0);
                }
                status_dobj.setOff_cd(offCd);
                if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("RegisteredVehicles")) {
                    if (payDobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_NOC || payDobj.getPurCd() == TableConstants.VM_TRANSACTION_MAST_REM_HYPO) {
                        VehicleParameters vehParameters = FormulaUtilities.fillVehicleParametersFromDobj(payDobj.getOwnerDobj(), offCd, userCatg, actionCode, userLoginOffCode, stateCd);
                        ToImpl toImpl = new ToImpl();
                        if (toImpl.isSurrenderRetention(payDobj.getApplNo())) {
                            vehParameters.setNOC_RETENTION(1);
                        }
                        if (!feeImplementation.getTmPurposeActionFlowCondtion(stateCd, payDobj.getPurCd(), TableConstants.TM_ACTION_REGISTERED_VEH_FEE, vehParameters)) {
                            continue;//for skipping fee of HPT,NOC
                        }
                    }
                }
                if (!CommonUtils.isNullOrBlank(vehicleType) && !(vehicleType.equalsIgnoreCase("BalanceFee") || vehicleType.equalsIgnoreCase("ONLINETAX") || "TAX-INSTALLMENT".equalsIgnoreCase(vehicleType) || vehicleType.equalsIgnoreCase("ONLINE-FANCY") || vehicleType.equalsIgnoreCase("ONLINE-AUDIT"))) {
                    ServerUtility.fileFlow(tmgr, status_dobj, actionCode, selectedRoleCode, stateCd, offCd, empCode, userCatg, clientIpAddress,
                            tmConfigDobj.isAllowFacelessService(), tmConfigDobj.isDefacement());
                }

                if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("ONLINETAX")) {
                    NonUseDobj nonUseDobj = new NonUseImplementation().geTotalNonUseAmount(payDobj.getOwnerDobj());
                    if (nonUseDobj != null) {
                        NonUseImplementation nonUseImpl = new NonUseImplementation();
                        nonUseImpl.insertInVhNonUseAdjustFromVtNonUseAdjust(nonUseDobj, tmgr, empCode, stateCd);
                        nonUseImpl.deleteFromVtNonUseAdjust(nonUseDobj, tmgr, stateCd);
                    }
                }
            }
            int regVehCount = 0;
            for (OnlinePayDobj taxBrkupDobj : onlinePayDobjList) {
                if ((!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("RegisteredVehicles") && regVehCount == 0)
                        || (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("NewVehicle"))
                        || (!CommonUtils.isNullOrBlank(vehicleType) && (vehicleType.equalsIgnoreCase("BalanceFee") || vehicleType.equalsIgnoreCase("ONLINETAX") || "TAX-INSTALLMENT".equalsIgnoreCase(vehicleType) || vehicleType.equalsIgnoreCase("ONLINE-FANCY") || vehicleType.equalsIgnoreCase("ONLINE-AUDIT")) && regVehCount == 0)) {
                    if (taxBrkupDobj.getTaxBreakUpDetails() != null && taxBrkupDobj.getTaxBreakUpDetails().size() > 0) {
                        for (OnlinePayDobj taxBreakUp : taxBrkupDobj.getTaxBreakUpDetails()) {

                            String taxBreakUpDetailsSql = "INSERT INTO " + TableList.VT_TAX_BREAKUP + "(state_cd, off_cd, rcpt_no, sr_no, tax_from, tax_upto, pur_cd, \n"
                                    + " prv_adjustment, tax, exempted, rebate, surcharge, penalty, interest, tax1, tax2)\n"
                                    + "    VALUES (?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                            PreparedStatement psTaxBreakUpDetails = tmgr.prepareStatement(taxBreakUpDetailsSql);
                            int i = 1;

                            psTaxBreakUpDetails.setString(i++, stateCd);//   state_cd
                            psTaxBreakUpDetails.setInt(i++, offCd);//   off_cd
                            psTaxBreakUpDetails.setString(i++, taxBrkupDobj.getReceiptNo());
                            psTaxBreakUpDetails.setInt(i++, taxBreakUp.getSrNo());
                            psTaxBreakUpDetails.setDate(i++, new java.sql.Date(sdf.parse(taxBreakUp.getPeriodFrom()).getTime()));
                            if (!taxBreakUp.getPeriodUpto().equals("")) {
                                psTaxBreakUpDetails.setDate(i++, new java.sql.Date(sdf.parse(taxBreakUp.getPeriodUpto()).getTime()));
                            } else {
                                psTaxBreakUpDetails.setNull(i++, java.sql.Types.DATE);
                            }
                            psTaxBreakUpDetails.setInt(i++, taxBreakUp.getPurCd());//pur_cd
                            psTaxBreakUpDetails.setLong(i++, (long) taxBreakUp.getPrevAdjustement());//prv_adjustment
                            psTaxBreakUpDetails.setDouble(i++, taxBreakUp.getTaxBreakUpAmount());//tax
                            psTaxBreakUpDetails.setLong(i++, taxBreakUp.getExempted());//exempted
                            psTaxBreakUpDetails.setDouble(i++, taxBreakUp.getTaxBreakUpRebate());//rebate
                            psTaxBreakUpDetails.setDouble(i++, taxBreakUp.getTaxBreakUpSurcharge());//surcharge
                            psTaxBreakUpDetails.setDouble(i++, taxBreakUp.getTaxBreakUpPenalty());//penalty
                            psTaxBreakUpDetails.setDouble(i++, taxBreakUp.getTaxBreakUpInterest());//interest
                            psTaxBreakUpDetails.setDouble(i++, taxBreakUp.getTaxBreakUpAmount1());//tax1
                            psTaxBreakUpDetails.setDouble(i++, taxBreakUp.getTaxBreakUpAmount2());//tax2                 
                            ServerUtility.validateQueryResult(tmgr, psTaxBreakUpDetails.executeUpdate(), psTaxBreakUpDetails);

                            String taxBreakUpDetailsDeleteSql = "DELETE FROM vp_cart_tax_breakup where appl_no = ? and pur_cd = ? and sr_no = ? ";
                            PreparedStatement psTaxBreakUpDetailsDelete = tmgr.prepareStatement(taxBreakUpDetailsDeleteSql);
                            psTaxBreakUpDetailsDelete.setString(1, taxBreakUp.getApplNo());
                            psTaxBreakUpDetailsDelete.setInt(2, taxBreakUp.getPurCd());
                            psTaxBreakUpDetailsDelete.setInt(3, taxBreakUp.getSrNo());
                            ServerUtil.validateQueryResult(tmgr, psTaxBreakUpDetailsDelete.executeUpdate(), psTaxBreakUpDetailsDelete);
                        }
                        regVehCount++;
                    }
                    if (taxBrkupDobj.getFeeBreakUpDetails() != null && taxBrkupDobj.getFeeBreakUpDetails().size() > 0) {
                        for (OnlinePayDobj feeBreakUp : taxBrkupDobj.getFeeBreakUpDetails()) {

                            String feeBreakUpDetailsSql = "insert into " + TableList.VT_FEE_BREAKUP
                                    + "(state_cd, off_cd, rcpt_no, sr_no, fee_from, fee_upto, pur_cd,"
                                    + " fee, fine, exempted, rebate, surcharge, interest, prv_adjustment)"
                                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                            PreparedStatement psFeeBreakUpDetails = tmgr.prepareStatement(feeBreakUpDetailsSql);
                            int m = 1;

                            psFeeBreakUpDetails.setString(m++, stateCd);//   state_cd
                            psFeeBreakUpDetails.setInt(m++, offCd);//   off_cd
                            psFeeBreakUpDetails.setString(m++, taxBrkupDobj.getReceiptNo());
                            psFeeBreakUpDetails.setInt(m++, feeBreakUp.getSrNo());
                            psFeeBreakUpDetails.setDate(m++, new java.sql.Date(sdf.parse(feeBreakUp.getPeriodFrom()).getTime()));
                            if (!feeBreakUp.getPeriodUpto().equals("")) {
                                psFeeBreakUpDetails.setDate(m++, new java.sql.Date(sdf.parse(feeBreakUp.getPeriodUpto()).getTime()));
                            } else {
                                psFeeBreakUpDetails.setNull(m++, java.sql.Types.DATE);
                            }
                            psFeeBreakUpDetails.setInt(m++, feeBreakUp.getPurCd());

                            psFeeBreakUpDetails.setLong(m++, feeBreakUp.getFeeBreakUpAmount());
                            psFeeBreakUpDetails.setLong(m++, feeBreakUp.getFeeBreakUpFine());
                            psFeeBreakUpDetails.setLong(m++, feeBreakUp.getExempted());
                            psFeeBreakUpDetails.setLong(m++, feeBreakUp.getFeeBreakUpRebate());
                            psFeeBreakUpDetails.setLong(m++, feeBreakUp.getFeeBreakUpSurcharge());
                            psFeeBreakUpDetails.setLong(m++, feeBreakUp.getFeeBreakUpInterest());
                            psFeeBreakUpDetails.setLong(m++, feeBreakUp.getPrevAdjustement());
                            ServerUtil.validateQueryResult(tmgr, psFeeBreakUpDetails.executeUpdate(), psFeeBreakUpDetails);

                            String feeBreakUpDetailsDeleteSql = "DELETE FROM " + TableList.VP_CART_FEE_BREAKUP + " where appl_no = ? and pur_cd = ? and sr_no = ? ";
                            PreparedStatement psFeeBreakUpDetailsDelete = tmgr.prepareStatement(feeBreakUpDetailsDeleteSql);
                            psFeeBreakUpDetailsDelete.setString(1, feeBreakUp.getApplNo());
                            psFeeBreakUpDetailsDelete.setInt(2, feeBreakUp.getPurCd());
                            psFeeBreakUpDetailsDelete.setInt(3, feeBreakUp.getSrNo());
                            ServerUtil.validateQueryResult(tmgr, psFeeBreakUpDetailsDelete.executeUpdate(), psFeeBreakUpDetailsDelete);
                        }
                        regVehCount++;
                    }
                }
            }
            if (userCatg != null && userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                TmConfigurationDobj tmConf = tmConfigDobj;
                if (tmConf != null && tmConf.getTmConfigDealerDobj() != null && tmConf.isTempFeeInNewRegis()) {
                    for (OnlinePayDobj tempDobj : onlinePayDobjList) {
                        if (tempDobj.isValidForTemp()) {
                            if (!tmConf.getTmConfigDealerDobj().isTempRegnApprovalBeforeNewRegn()) {
                                String tempSQL = "INSERT into  " + TableList.VT_OWNER_TEMP + " Select state_cd, off_cd, appl_no, ?, ?, ? , purchase_dt, owner_name, f_name, c_add1, c_add2, c_add3, c_district,  c_pincode, c_state, p_add1, "
                                        + " p_add2, p_add3, p_district, p_pincode, p_state, owner_cd, regn_type, vh_class, chasi_no, eng_no, maker,maker_model, body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap,"
                                        + " unld_wt, ld_wt, gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, \n"
                                        + " cubic_cap, floor_area, ac_fitted, audio_fitted, video_fitted, \n"
                                        + " vch_purchase_as, vch_catg, dealer_cd, sale_amt, laser_code, garage_add, \n"
                                        + " length, width, height, regn_upto, fit_upto, annual_income, imported_vch, \n"
                                        + " other_criteria, ?,?, current_timestamp as op_dt,?,?"
                                        + " FROM " + TableList.VA_OWNER + " where appl_no = ? ";

                                PreparedStatement ps = tmgr.prepareStatement("SELECT *  FROM vm_validity_mast where pur_cd=? and state_cd=?");
                                ps.setInt(1, TableConstants.VM_TRANSACTION_MAST_TEMP_REG);
                                ps.setString(2, stateCd);
                                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                                Date dtTempFrom = new Date();
                                Date dtTempUpto = new Date();
                                VehicleParameters vehParameters = FormulaUtilities.fillVehicleParametersFromDobj(tempDobj.getOwnerDobj(), offCd, userCatg, actionCode, userLoginOffCode, stateCd);
                                vehParameters.setTMP_PURPOSE("OR");
                                while (rs.next()) {
                                    if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "dealerPayment")) {
                                        int newUpto = rs.getInt("new_value");
                                        String mod = rs.getString("mod");
                                        if (mod.equals("D")) {
                                            dtTempUpto = DateUtils.addToDate(dtTempFrom, 1, newUpto);
                                        } else if (mod.equals("M")) {
                                            dtTempUpto = DateUtils.addToDate(dtTempFrom, 2, newUpto);
                                            dtTempUpto = DateUtils.addToDate(dtTempUpto, 1, -1);
                                        } else if (mod.equals("Y")) {
                                            dtTempUpto = DateUtils.addToDate(dtTempFrom, 3, newUpto);
                                            dtTempUpto = DateUtils.addToDate(dtTempUpto, 1, -1);
                                        }
                                    }
                                }
                                if (DateUtils.compareDates(dtTempUpto, new Date()) <= 1) {
                                    dtTempUpto = DateUtils.addToDate(dtTempFrom, 2, 1);
                                    dtTempUpto = DateUtils.addToDate(dtTempUpto, 1, -1);
                                }

                                String tempRegNo = ServerUtility.getUniquePermitNo(tmgr, stateCd, offCd, 0, 0, "M", empCode);

                                if (tempRegNo == null || tempRegNo.equalsIgnoreCase("") || tempRegNo.equalsIgnoreCase("TEMPREG")) {
                                    throw new VahanException("Temporary Registration No Generation Failed!!!");
                                }
                                PreparedStatement psTempInsert = tmgr.prepareStatement(tempSQL);
                                psTempInsert.setString(1, tempRegNo);
                                psTempInsert.setDate(2, new java.sql.Date(dtTempFrom.getTime()));
                                psTempInsert.setDate(3, new java.sql.Date(dtTempUpto.getTime()));
                                psTempInsert.setString(4, stateCd);
                                psTempInsert.setInt(5, offCd);
                                psTempInsert.setString(6, "OR");
                                psTempInsert.setString(7, null);
                                psTempInsert.setString(8, tempDobj.getApplNo());
                                ServerUtility.validateQueryResult(tmgr, psTempInsert.executeUpdate(), psTempInsert);

                                String vaTempRCPrintSql = "INSERT INTO " + TableList.VA_TEMP_RC_PRINT + "( state_cd, off_cd, appl_no, temp_regn_no, dealer_cd, op_dt ) \n "
                                        + " VALUES (?, ?, ?, ?, ?, current_timestamp)";

                                PreparedStatement psVaTempRCPrint = tmgr.prepareStatement(vaTempRCPrintSql);
                                psVaTempRCPrint.setString(1, stateCd);
                                psVaTempRCPrint.setInt(2, offCd);
                                psVaTempRCPrint.setString(3, tempDobj.getApplNo());
                                psVaTempRCPrint.setString(4, tempRegNo);
                                psVaTempRCPrint.setString(5, tempDobj.getOwnerDobj().getDealer_cd());
                                ServerUtility.validateQueryResult(tmgr, psVaTempRCPrint.executeUpdate(), psVaTempRCPrint);
                            } else if (tmConf.getTmConfigDealerDobj().isTempRegnApprovalBeforeNewRegn()) { //In case of KL,OR
                                if (userLoginOffCode != offCd) {
                                    if ("KL".equals(stateCd)) {
                                        this.insertTemporaryRegnDetails(offCd, offCd, "OR", tempDobj, userId, stateCd, transactionNo, tmgr, tmConf.getTmConfigDealerDobj().getTempFlowInNewRegnActionCd(), userCatg, actionCode,
                                                sessionUserId, clientIpAddress, empCode);
                                    } else {
                                        this.insertTemporaryRegnDetails(offCd, userLoginOffCode, "OR", tempDobj, userId, stateCd, transactionNo, tmgr, tmConf.getTmConfigDealerDobj().getTempFlowInNewRegnActionCd(), userCatg, actionCode,
                                                sessionUserId, clientIpAddress, empCode);
                                    }
                                } else if ((userLoginOffCode != offCd) && tmConf.getTmConfigDealerDobj().isTempRegnFeeWithNewForSameOffices()) {
                                    this.insertTemporaryRegnDetails(offCd, offCd, "SR", tempDobj, userId, stateCd, transactionNo, tmgr, tmConf.getTmConfigDealerDobj().getTempFlowInNewRegnActionCd(), userCatg, actionCode,
                                            sessionUserId, clientIpAddress, empCode);
                                }
                            }
                        }
                    }
                }
            }
            String payTableName = TableList.VP_RCPT_CART;
            if (vehicleType.equalsIgnoreCase("RegisteredVehicles") && isExistRcptCartTemp(tmgr, onlinePayDobjList.get(0).getApplNo())) {
                payTableName = TableList.VP_RCPT_CART_TEMP;
            }
            String cartSql = "INSERT INTO vph_rcpt_cart  SELECT current_timestamp,?,state_cd, off_cd,user_cd, appl_no, pur_cd ,period_mode, period_from, period_upto, amount, exempted, \n"
                    + " rebate, surcharge, penalty, interest, transaction_no, rcpt_no , rcpt_dt, pmt_type , \n"
                    + " pmt_catg , service_type, route_class, route_length , no_of_trips , domain_cd , distance_run_in_quarter,op_dt,no_adv_units,tax1,tax2,prv_adjustment \n"
                    + " FROM " + payTableName + " where user_cd = ? and transaction_no = ? and transaction_no IS NOT NULL";

            PreparedStatement psCart = tmgr.prepareStatement(cartSql);
            if (userCatg != null && userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                psCart.setString(1, userId);
            } else {
                psCart.setString(1, TableConstants.ONLINE_PAYMENT);
            }
            psCart.setLong(2, Long.parseLong(userId));
            psCart.setString(3, transactionNo);
            ServerUtility.validateQueryResult(tmgr, psCart.executeUpdate(), psCart);

            String cartDeleteSql = "delete from vp_rcpt_cart where user_cd = ? and transaction_no = ? and transaction_no IS NOT NULL";
            PreparedStatement psCartDelete = tmgr.prepareStatement(cartDeleteSql);
            psCartDelete.setLong(1, Long.parseLong(userId));
            psCartDelete.setString(2, transactionNo);
            ServerUtility.validateQueryResult(tmgr, psCartDelete.executeUpdate(), psCartDelete);
            
            String cartDeleteSqlTemp = "delete from " + TableList.VP_RCPT_CART_TEMP + " where user_cd = ? and transaction_no = ? and transaction_no IS NOT NULL";
            PreparedStatement psCartDeleteTemp = tmgr.prepareStatement(cartDeleteSqlTemp);
            psCartDeleteTemp.setLong(1, Long.parseLong(userId));
            psCartDeleteTemp.setString(2, transactionNo);
            psCartDeleteTemp.executeUpdate();

            if (userCatg != null && !userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                String userIdSql = "INSERT INTO vph_online_pay_user_info SELECT current_timestamp,?,state_cd, off_cd, user_cd, user_pwd, mobile_no, created_by, created_dt,appl_no\n"
                        + "  FROM vp_online_pay_user_info where user_cd = ? ";

                PreparedStatement psUserId = tmgr.prepareStatement(userIdSql);
                psUserId.setString(1, TableConstants.ONLINE_PAYMENT);
                psUserId.setLong(2, Long.parseLong(userId));
                ServerUtility.validateQueryResult(tmgr, psUserId.executeUpdate(), psUserId);

                String userIdDeleteSql = "delete from vp_online_pay_user_info where user_cd = ? ";
                PreparedStatement psUserIdDelete = tmgr.prepareStatement(userIdDeleteSql);
                psUserIdDelete.setLong(1, Long.parseLong(userId));
                ServerUtility.validateQueryResult(tmgr, psUserIdDelete.executeUpdate(), psUserIdDelete);
                // In case of Balance fee
                if (!CommonUtils.isNullOrBlank(vehicleType) && (vehicleType.equalsIgnoreCase("BalanceFee") || vehicleType.equalsIgnoreCase("ONLINETAX") || "TAX-INSTALLMENT".equalsIgnoreCase(vehicleType) || vehicleType.equals("ONLINE-FANCY") || vehicleType.equals("ONLINE-AUDIT"))) {
                    String BalanceInsertSql = "INSERT INTO " + TableList.VPH_ONLINE_PAY_BALANCE_FEE_DETAILS + " SELECT current_timestamp, ?, state_cd, off_cd, user_cd, appl_no, "
                            + "regn_no, owner_name, selected_option, chasi_no, op_dt,form_type,vh_class FROM " + TableList.VP_ONLINE_PAY_BALANCE_FEE_DETAILS + "  where user_cd = ?  ";

                    PreparedStatement psBalance = tmgr.prepareStatement(BalanceInsertSql);
                    psBalance.setString(1, TableConstants.ONLINE_PAYMENT);
                    psBalance.setLong(2, Long.parseLong(userId));
                    ServerUtility.validateQueryResult(tmgr, psBalance.executeUpdate(), psBalance);

                    String BalanceDeleteSql = "delete from " + TableList.VP_ONLINE_PAY_BALANCE_FEE_DETAILS + " where user_cd = ? ";
                    PreparedStatement psBalanceDelete = tmgr.prepareStatement(BalanceDeleteSql);
                    psBalanceDelete.setLong(1, Long.parseLong(userId));
                    ServerUtility.validateQueryResult(tmgr, psBalanceDelete.executeUpdate(), psBalanceDelete);
                }
            }
            if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("NewVehicle")) {
                payDobjList = this.getEReceiptDetailForNewVehicles(transactionNo, tmgr, stateCd, offCd);
            } else if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("RegisteredVehicles")) {
                payDobjList = this.getEReceiptDetailForRegisteredVehicles(transactionNo, onlinePayDobjList.get(0).getRegnNo(), tmgr, onlinePayDobjList.get(0).getPurCd());
            } else if (!CommonUtils.isNullOrBlank(vehicleType) && (vehicleType.equalsIgnoreCase("BalanceFee") || vehicleType.equalsIgnoreCase("ONLINETAX") || "TAX-INSTALLMENT".equalsIgnoreCase(vehicleType) || vehicleType.equals("ONLINE-FANCY") || vehicleType.equalsIgnoreCase("ONLINE-AUDIT"))) {
                payDobjList = this.getEReceiptDetailForBalanceFee(stateCd, offCd, transactionNo, userId, tmgr);
            } else if (!CommonUtils.isNullOrBlank(vehicleType) && vehicleType.equalsIgnoreCase("TradeCertificate")) {
                payDobjList = this.getEReceiptDetailForTradeCertificate(transactionNo, tmgr, stateCd, offCd);
            }
            for (OnlinePayDobj payDobj : dtlDobjList) {
                if (("DL,UP,GJ").contains(stateCd) && dtlDobjList != null && (payDobj.getPurCd() == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD || payDobj.getPurCd() == TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD || payDobj.getPurCd() == TableConstants.VM_PMT_FRESH_PUR_CD)) {
                    PassengerPermitDetailDobj passDobjVa = new PassengerPermitDetailImplementation(stateCd).set_permit_appl_db_to_dobj(payDobj.getApplNo());
                    if (passDobjVa != null && passDobjVa.getPmt_type().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT) && passDobjVa.getPaction().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_FRESH_PUR_CD))) {
                        PermitFeeDobj dobj = new PermitFeeDobj();
                        dobj.setPaymentStaus("Y");
                        dobj.setPur_cd(passDobjVa.getPaction());
                        dobj.setAppl_no(payDobj.getApplNo());
                        dobj.setRegn_no(passDobjVa.getRegnNo());
                        dobj.setPermit_type(passDobjVa.getPmt_type());
                        dobj.setPermit_catg(CommonUtils.isNullOrBlank(passDobjVa.getPmtCatg()) ? -1 : Integer.parseInt(passDobjVa.getPmtCatg()));
                        insertNpPermitDetails(dobj, null, tmgr, stateCd, userLoginOffCode, offCd);
                        break;
                    } else if (passDobjVa != null && passDobjVa.getPmt_type().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT) && passDobjVa.getPaction().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEWAL_PUR_CD))) {
                        PermitFeeDobj dobj = new PermitFeeDobj();
                        dobj.setPaymentStaus("Y");
                        dobj.setPur_cd(passDobjVa.getPaction());
                        dobj.setAppl_no(payDobj.getApplNo());
                        dobj.setRegn_no(passDobjVa.getRegnNo());
                        dobj.setPermit_type(passDobjVa.getPmt_type());
                        dobj.setPermit_catg(CommonUtils.isNullOrBlank(passDobjVa.getPmtCatg()) ? -1 : Integer.parseInt(passDobjVa.getPmtCatg()));
                        insertNpPermitDetails(dobj, null, tmgr, stateCd, userLoginOffCode, offCd);
                        break;
                    } else {
                        PermitHomeAuthDobj authDobj = new PermitHomeAuthImpl().getVaHomeAuthDetails(payDobj.getApplNo());
                        if (authDobj != null) {
                            PassengerPermitDetailDobj passDobjVt = new PassengerPermitDetailImplementation(stateCd).set_vt_permit_regnNo_to_dobj(authDobj.getRegnNo(), null);
                            if (passDobjVt != null && passDobjVt.getPmt_type().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)) {
                                PermitFeeDobj dobj = new PermitFeeDobj();
                                dobj.setPur_cd(String.valueOf(payDobj.getPurCd()));
                                dobj.setPaymentStaus("Y");
                                dobj.setAppl_no(payDobj.getApplNo());
                                dobj.setRegn_no(authDobj.getRegnNo());
                                dobj.setPermit_type(passDobjVt.getPmt_type());
                                dobj.setPermit_catg(CommonUtils.isNullOrBlank(passDobjVt.getPmtCatg()) ? -1 : Integer.parseInt(passDobjVt.getPmtCatg()));
                                insertNpPermitDetails(dobj, null, tmgr, stateCd, userLoginOffCode, offCd);
                                break;
                            }
                        }
                    }
                }
            }
            this.sendSms(transactionNo, tmgr, stateCd);
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + transactionNo + " " + e.getStackTrace()[0]);
            throw new VahanException("Unable to proccess your request.Please Try Again");
        }
        return payDobjList;
    }

    public void insertTemporaryRegnDetails(int selectedOffCd, int loginOffCd, String tempReason, OnlinePayDobj tempDobj, String userId, String stateCd, String transactionNo, TransactionManager tmgr, int tempFlowInNewActionCd) throws VahanException, SQLException, Exception {
        SimpleDateFormat sd = new SimpleDateFormat("dd-MMM-yyyy");
        String dt = sd.format(new Date());
        VehicleParameters parameters = FormulaUtils.fillVehicleParametersFromDobj(tempDobj.getOwnerDobj());
        Status_dobj status = new Status_dobj();
        status.setAppl_dt(dt);
        status.setAppl_no(tempDobj.getApplNo());
        status.setRegn_no("TEMPREG");
        status.setPur_cd(TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE);
        status.setEmp_cd(Long.parseLong(userId));
        status.setState_cd(stateCd);
        status.setOff_cd(loginOffCd);
        status.setOffice_remark("");
        status.setPublic_remark("");
        status.setStatus("C");
        String selectSql = "select * from tm_purpose_action_flow where state_cd = ? and pur_cd = ? and action_cd = ? ";
        PreparedStatement psSelect = tmgr.prepareStatement(selectSql);
        psSelect.setString(1, stateCd);
        psSelect.setInt(2, TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE);
        psSelect.setInt(3, tempFlowInNewActionCd);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            status.setFlow_slno(rs.getInt("flow_srno"));
            status.setFile_movement_slno(1);
            status.setAction_cd(rs.getInt("action_cd"));
        }
        status.setVehicleParameters(parameters);
        ServerUtil.fileFlowForNewApplication(tmgr, status);
        // here off_cd is the login off and off_to is selected off
        String insertVaOwnerTempSql = "INSERT INTO " + TableList.VA_OWNER_TEMP + "(state_cd,off_cd,appl_no, temp_regn_no, state_cd_to, off_cd_to,purpose,body_building,op_dt,temp_regn_type)"
                + "    VALUES (?,?,?, ?, ?, ?,?,?,current_timestamp,?)";

        PreparedStatement ps = tmgr.prepareStatement(insertVaOwnerTempSql);
        int i = 1;
        ps.setString(i++, stateCd);
        ps.setInt(i++, loginOffCd);
        ps.setString(i++, tempDobj.getApplNo());
        ps.setString(i++, "TEMPREG");
        ps.setString(i++, stateCd);
        ps.setInt(i++, selectedOffCd);
        ps.setString(i++, tempReason);
        ps.setString(i++, "");
        ps.setString(i++, "N");//for new temp regn
        ps.executeUpdate();

        // Remove duplicate records
        NewImpl.insertIntoTempRegnDetails(tmgr, tempDobj.getApplNo());
        ServerUtil.deleteFromTable(tmgr, null, tempDobj.getApplNo(), TableList.VA_TMP_REGN_DTL);
        // offcd is selected off and off to is login off
        String insertIntoVaTempRegnSql = "INSERT INTO " + TableList.VA_TMP_REGN_DTL
                + "(state_cd,off_cd,appl_no, regn_no, tmp_off_cd, tmp_state_cd, tmp_regn_no,op_dt)"
                + "  VALUES (?,?,?, ?, ?, ?, ?,current_timestamp)";

        ps = tmgr.prepareStatement(insertIntoVaTempRegnSql);
        int k = 1;
        ps.setString(k++, stateCd);
        ps.setInt(k++, selectedOffCd);
        ps.setString(k++, tempDobj.getApplNo());
        ps.setString(k++, tempDobj.getOwnerDobj().getRegn_no());
        ps.setInt(k++, loginOffCd);
        ps.setString(k++, stateCd);
        ps.setString(k++, "TEMPREG");
        ps.executeUpdate();

        // here off_cd is the login off and off_to is selected off
        String insertIntoOwnerRegntemp = "INSERT INTO " + TableList.VP_OWNER_REG_TEMP + "(state_cd, off_cd, state_to, off_to, appl_no, \n"
                + "chasi_no, transaction_no, rcpt_no, op_dt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, current_timestamp) ";

        ps = tmgr.prepareStatement(insertIntoOwnerRegntemp);
        int j = 1;
        ps.setString(j++, stateCd);
        ps.setInt(j++, loginOffCd);
        ps.setString(j++, stateCd);
        ps.setInt(j++, selectedOffCd);
        ps.setString(j++, tempDobj.getApplNo());
        ps.setString(j++, tempDobj.getOwnerDobj().getChasi_no());
        ps.setString(j++, transactionNo);
        ps.setString(j++, tempDobj.getReceiptNo());
        ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
    }

    /**
     * @author Kartikey Singh
     */
    public void insertTemporaryRegnDetails(int selectedOffCd, int loginOffCd, String tempReason, OnlinePayDobj tempDobj, String userId,
            String stateCd, String transactionNo, TransactionManager tmgr, int tempFlowInNewActionCd, String userCategory, int actionCode,
            String sessionUserId, String clientIpAddress, String empCode) throws VahanException, SQLException, Exception {
        SimpleDateFormat sd = new SimpleDateFormat("dd-MMM-yyyy");
        String dt = sd.format(new Date());
        VehicleParameters parameters = FormulaUtilities.fillVehicleParametersFromDobj(tempDobj.getOwnerDobj(), selectedOffCd, userCategory,
                actionCode, loginOffCd, stateCd);
        Status_dobj status = new Status_dobj();
        status.setAppl_dt(dt);
        status.setAppl_no(tempDobj.getApplNo());
        status.setRegn_no("TEMPREG");
        status.setPur_cd(TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE);
        status.setEmp_cd(Long.parseLong(userId));
        status.setState_cd(stateCd);
        status.setOff_cd(loginOffCd);
        status.setOffice_remark("");
        status.setPublic_remark("");
        status.setStatus("C");
        String selectSql = "select * from tm_purpose_action_flow where state_cd = ? and pur_cd = ? and action_cd = ? ";
        PreparedStatement psSelect = tmgr.prepareStatement(selectSql);
        psSelect.setString(1, stateCd);
        psSelect.setInt(2, TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE);
        psSelect.setInt(3, tempFlowInNewActionCd);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            status.setFlow_slno(rs.getInt("flow_srno"));
            status.setFile_movement_slno(1);
            status.setAction_cd(rs.getInt("action_cd"));
        }
        status.setVehicleParameters(parameters);
        ServerUtility.fileFlowForNewApplication(tmgr, status, sessionUserId, clientIpAddress);
        // here off_cd is the login off and off_to is selected off
        String insertVaOwnerTempSql = "INSERT INTO " + TableList.VA_OWNER_TEMP + "(state_cd,off_cd,appl_no, temp_regn_no, state_cd_to, off_cd_to,purpose,body_building,op_dt,temp_regn_type)"
                + "    VALUES (?,?,?, ?, ?, ?,?,?,current_timestamp,?)";

        PreparedStatement ps = tmgr.prepareStatement(insertVaOwnerTempSql);
        int i = 1;
        ps.setString(i++, stateCd);
        ps.setInt(i++, loginOffCd);
        ps.setString(i++, tempDobj.getApplNo());
        ps.setString(i++, "TEMPREG");
        ps.setString(i++, stateCd);
        ps.setInt(i++, selectedOffCd);
        ps.setString(i++, tempReason);
        ps.setString(i++, "");
        ps.setString(i++, "N");//for new temp regn
        ps.executeUpdate();

        // Remove duplicate records
        NewImplementation.insertIntoTempRegnDetails(tmgr, tempDobj.getApplNo(), empCode);
        ServerUtility.deleteFromTable(tmgr, null, tempDobj.getApplNo(), TableList.VA_TMP_REGN_DTL);
        // offcd is selected off and off to is login off
        String insertIntoVaTempRegnSql = "INSERT INTO " + TableList.VA_TMP_REGN_DTL
                + "(state_cd,off_cd,appl_no, regn_no, tmp_off_cd, tmp_state_cd, tmp_regn_no,op_dt)"
                + "  VALUES (?,?,?, ?, ?, ?, ?,current_timestamp)";

        ps = tmgr.prepareStatement(insertIntoVaTempRegnSql);
        int k = 1;
        ps.setString(k++, stateCd);
        ps.setInt(k++, selectedOffCd);
        ps.setString(k++, tempDobj.getApplNo());
        ps.setString(k++, tempDobj.getOwnerDobj().getRegn_no());
        ps.setInt(k++, loginOffCd);
        ps.setString(k++, stateCd);
        ps.setString(k++, "TEMPREG");
        ps.executeUpdate();

        // here off_cd is the login off and off_to is selected off
        String insertIntoOwnerRegntemp = "INSERT INTO " + TableList.VP_OWNER_REG_TEMP + "(state_cd, off_cd, state_to, off_to, appl_no, \n"
                + "chasi_no, transaction_no, rcpt_no, op_dt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, current_timestamp) ";

        ps = tmgr.prepareStatement(insertIntoOwnerRegntemp);
        int j = 1;
        ps.setString(j++, stateCd);
        ps.setInt(j++, loginOffCd);
        ps.setString(j++, stateCd);
        ps.setInt(j++, selectedOffCd);
        ps.setString(j++, tempDobj.getApplNo());
        ps.setString(j++, tempDobj.getOwnerDobj().getChasi_no());
        ps.setString(j++, transactionNo);
        ps.setString(j++, tempDobj.getReceiptNo());
        ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);
    }

    public List<OnlinePayDobj> getEReceiptDetailForBalanceFee(String stateCd, int offCd, String transaction_no, String userId, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        List<OnlinePayDobj> dobjReceiptList = new ArrayList<>();
        try {
            String sql = " select ct.appl_no ,vpo.regn_no as regn_no,vpo.chasi_no as chasi_no,s.descr as statename,to_char(vpd.rcpt_dt,'dd-MON-yyyy') as rcpt_dt,vpd.transaction_id as transaction_no,vpd.return_rcpt_no as bank_ref_no,"
                    + " sum(ct.amount + ct.surcharge + ct.interest + ct.prv_adjustment - ct.exempted - ct.rebate + ct.penalty) as trans_amt, ct.rcpt_no from vph_rcpt_cart as ct "
                    + " inner join " + TableList.VPH_ONLINE_PAY_BALANCE_FEE_DETAILS + " vpo on vpo.state_cd= ct.state_cd and vpo.off_cd= ct.off_cd and vpo.appl_no = ct.appl_no "
                    + " left outer join vahanpgi.vp_pgi_details vpd on ct.transaction_no = vpd.payment_id left outer join tm_state s on ct.state_cd = s.state_code "
                    + " where ct.state_cd = ? and ct.off_cd = ? and ct.transaction_no = ? and vpo.user_cd= ? group by ct.appl_no ,vpd.rcpt_dt,vpd.transaction_id,vpd.return_rcpt_no,regn_no,chasi_no ,statename, rcpt_no";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, transaction_no);
            ps.setLong(4, Long.parseLong(userId));
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                OnlinePayDobj dobj = new OnlinePayDobj();
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setChassisNo(rs.getString("chasi_no"));
                dobj.setTransactionDate(rs.getString("rcpt_dt"));
                dobj.setTransactionNo(rs.getString("transaction_no"));
                dobj.setTransactionAmount(rs.getInt("trans_amt"));
                dobj.setStateName(rs.getString("statename"));
                dobj.setBankReferenceNo(rs.getString("bank_ref_no"));
                dobj.setReceiptNo(rs.getString("rcpt_no"));
                dobjReceiptList.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error("for TransactionNo " + transaction_no + " " + e);
            throw new VahanException("Unable to proccess your request.Please Try Again");
        }
        return dobjReceiptList;
    }

    public List<OnlinePayDobj> getDetailListBalanceFee(TransactionManager tmgr, String transaction_no, String userId, String applNo) throws VahanException {
        try {
            List<OnlinePayDobj> dobjList = this.getDetailListForNewVehicles(tmgr, transaction_no, userId);
            if (dobjList != null && !dobjList.isEmpty()) {
                return dobjList;
            } else {
                dobjList = this.getDetailListForRegisteredVehicles(tmgr, transaction_no, userId, applNo, -1, null);
                return dobjList;
            }
        } catch (Exception e) {
            throw new VahanException("Unable to proccess your request.Please Try Again");
        }
    }

    public List<OnlinePayDobj> getDetailList(TransactionManager tmgr, String transaction_no, String userId, String stateCd, int offCd, String reciptNo) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        List<OnlinePayDobj> dobjList = new ArrayList<>();
        try {
            sql = " select vp.transaction_no as pay_id,vp.appl_no as appl_no,vp.pur_cd,to_char(vp.period_from, 'yyyy-mm-dd') as period_from, to_char(vp.period_upto, 'yyyy-mm-dd') as period_upto,vp.penalty,sum(vp.amount+vp.surcharge+vp.interest+vp.tax1+vp.tax2+vp.prv_adjustment-vp.exempted-vp.rebate) as amount"
                    + " ,vp.state_cd,o.regn_no,o.owner_name,vp.off_cd,vp.rcpt_no,vp.period_mode as tax_mode,pgi.rcpt_dt,o.chasi_no,o.vh_class from " + TableList.VP_RCPT_CART + " vp inner join "
                    + " " + TableList.VP_ONLINE_PAY_BALANCE_FEE_DETAILS + " o on vp.state_cd = o.state_cd and vp.off_cd = o.off_cd and vp.appl_no = o.appl_no and vp.user_cd = o.user_cd "
                    + " left outer join vahanpgi.vp_pgi_details pgi on vp.transaction_no = pgi.payment_id where vp.state_cd = ? and vp.off_cd = ? "
                    + " and vp.user_cd = ? and vp.transaction_no = ? group by vp.transaction_no ,vp.appl_no ,vp.period_from,vp.period_upto,vp.penalty,o.state_cd,o.regn_no,vp.pur_cd,o.owner_name,o.off_cd,vp.user_cd,pgi.rcpt_dt,o.chasi_no,o.vh_class";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setLong(3, Long.parseLong(userId));
            ps.setString(4, transaction_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                OnlinePayDobj dobj = new OnlinePayDobj();
                dobj.setTransactionNo(rs.getString("pay_id"));
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setPurCd(rs.getInt("pur_cd"));
                dobj.setPeriodFrom(rs.getString("period_from"));
                dobj.setPeriodUpto(rs.getString("period_upto"));
                dobj.setPenalty(rs.getLong("penalty"));
                dobj.setAmount(rs.getLong("amount"));
                dobj.setStateCd(rs.getString("state_cd"));
                dobj.setRegnNo(rs.getString("regn_no"));
                Owner_dobj ownerDobj = new OwnerImpl().getVtOwnerDetailsForRegnNoStateCdOffCd(rs.getString("regn_no"), rs.getString("state_cd"), rs.getInt("off_cd"));
                if (ownerDobj == null) {
                    ownerDobj = new Owner_dobj();
                    ownerDobj.setOwner_name(rs.getString("owner_name"));
                    ownerDobj.setChasi_no(rs.getString("chasi_no"));
                    ownerDobj.setVh_class(rs.getInt("vh_class"));
                }
                dobj.setOwnerDobj(ownerDobj);
                reciptNo = reciptNo == null ? rs.getString("rcpt_no") : reciptNo;
                dobj.setOffCode(rs.getInt("off_cd"));
                dobj.setReceiptNo(reciptNo);
                dobj.setPaymentDate(new java.sql.Timestamp(rs.getDate("rcpt_dt").getTime()));
                dobj.setTaxMode(rs.getString("tax_mode"));
                List<OnlinePayDobj> taxBreakUpDetails = this.getTaxBreakUpDetails(rs.getString("appl_no"), tmgr);
                dobj.setTaxBreakUpDetails(taxBreakUpDetails);
                dobjList.add(dobj);
                List<OnlinePayDobj> feeBreakUpDetails = this.getFeeBreakUpDetails(rs.getString("appl_no"), tmgr);
                dobj.setFeeBreakUpDetails(feeBreakUpDetails);
            }
        } catch (Exception e) {
            LOGGER.error("for TransactionNo " + transaction_no + " " + e);
            throw new VahanException("Unable to proccess your request.Please Try Again");
        }
        return dobjList;
    }

    /*
     * This function used to get DetailsList of New Vehicle for showing on Receipt
     */
    public List<OnlinePayDobj> getEReceiptDetailForNewVehicles(String transaction_no, TransactionManager tmgr, String stateCd, int offCd) throws VahanException {

        String sql = null;
        PreparedStatement ps = null;
        List appl_no_list = new ArrayList();
        try {
            sql = " select o.appl_no ,o.regn_no as regn_no,o.chasi_no as chasi_no,o.state_name as statename,to_char(vpd.rcpt_dt,'dd-MON-yyyy') as rcpt_dt,vpd.transaction_id as transaction_no,"
                    + " vpd.return_rcpt_no as bank_ref_no,sum(ct.amount + ct.surcharge + ct.interest + ct.tax1 + ct.tax2 + ct.penalty + ct.prv_adjustment - ct.exempted - ct.rebate) as trans_amt,"
                    + " vpd.treasury_ref_no as treasury_no,ct.rcpt_no, off_name,vh_class_desc,dlr_name,image_logo,image_background  \n"
                    + " from vph_rcpt_cart as ct left outer join vva_owner as o on ct.appl_no = o.appl_no \n"
                    + " left outer join vahanpgi.vp_pgi_details vpd on ct.transaction_no = vpd.payment_id \n"
                    + " left outer join tm_configuration_print p on ct.state_cd = p.state_cd "
                    + " where ct.transaction_no = ? and ct.state_cd = ? and ct.off_cd = ? \n"
                    + " group by o.appl_no ,vpd.rcpt_dt,vpd.transaction_id,vpd.return_rcpt_no,regn_no,chasi_no ,statename,treasury_ref_no,rcpt_no, off_name,vh_class_desc,dlr_name,image_logo,image_background  ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, transaction_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                OnlinePayDobj dobj = new OnlinePayDobj();
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setChassisNo(rs.getString("chasi_no"));
                dobj.setTransactionDate(rs.getString("rcpt_dt"));
                dobj.setTransactionNo(rs.getString("transaction_no"));
                dobj.setTransactionAmount(rs.getInt("trans_amt"));
                dobj.setStateName(rs.getString("statename"));
                dobj.setBankReferenceNo(rs.getString("bank_ref_no"));
                dobj.setTreasuryRefNo(rs.getString("treasury_no"));
                dobj.setReceiptNo(rs.getString("rcpt_no"));
                dobj.setOfficeName(rs.getString("off_name"));
                dobj.setVhClassDesc(rs.getString("vh_class_desc"));
                dobj.setDealerName(rs.getString("dlr_name"));
                dobj.setStateLogo(rs.getString("image_logo"));
                dobj.setImageBackground(rs.getString("image_background"));
                appl_no_list.add(dobj);
            }

        } catch (Exception e) {
            LOGGER.error("for TransactionNo " + transaction_no + " " + e);
            throw new VahanException("Unable to proccess your request.Please Try Again");
        }
        return appl_no_list;
    }

    /*
     * This function used to get DetailsList of Registered Vehicle for showing on Receipt
     */
    public List<OnlinePayDobj> getEReceiptDetailForRegisteredVehicles(String transaction_no, String regnNo, TransactionManager tmgr, int pur_cd) throws VahanException {

        String sql = null;
        PreparedStatement ps = null;
        List appl_no_list = new ArrayList();
        try {
            if (pur_cd == TableConstants.VM_PMT_FRESH_PUR_CD && regnNo.equalsIgnoreCase("NEW")) {
                sql = " select ct.appl_no ,o.regn_no as regn_no,'' as chasi_no,s.descr as statename,to_char(vpd.rcpt_dt,'dd-MON-yyyy') as rcpt_dt,vpd.transaction_id as transaction_no,vpd.return_rcpt_no as   bank_ref_no,sum(ct.amount + ct.surcharge + ct.interest + ct.tax1 + ct.tax2 + ct.penalty + ct.prv_adjustment - ct.exempted - ct.rebate) as trans_amt, ct.rcpt_no \n"
                        + " from vph_rcpt_cart as ct \n"
                        + " left outer join permit.va_permit_owner as o on ct.appl_no = o.appl_no  and  o.regn_no = ? \n"
                        + " left outer join vahanpgi.vp_pgi_details vpd on ct.transaction_no = vpd.payment_id \n"
                        + " left outer join tm_state s on s.state_code=o.state_cd\n"
                        + " where ct.transaction_no = ? \n"
                        + " group by ct.appl_no ,vpd.rcpt_dt,vpd.transaction_id,vpd.return_rcpt_no,regn_no,chasi_no ,statename, rcpt_no";
            } else {
                sql = " select ct.appl_no ,o.regn_no as regn_no,o.chasi_no as chasi_no,o.state_name as statename,to_char(vpd.rcpt_dt,'dd-MON-yyyy') as rcpt_dt,vpd.transaction_id as transaction_no,vpd.return_rcpt_no as bank_ref_no,sum(ct.amount + ct.surcharge + ct.interest + ct.tax1 + ct.tax2 + ct.penalty + ct.prv_adjustment - ct.exempted - ct.rebate) as trans_amt, ct.rcpt_no\n"
                        + " from vph_rcpt_cart as ct left outer join vv_owner as o on o.regn_no = ? \n"
                        + " left outer join vahanpgi.vp_pgi_details vpd on ct.transaction_no = vpd.payment_id \n"
                        + " where ct.transaction_no = ? \n"
                        + " group by ct.appl_no ,vpd.rcpt_dt,vpd.transaction_id,vpd.return_rcpt_no,regn_no,chasi_no ,statename, rcpt_no ";
            }
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, transaction_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                OnlinePayDobj dobj = new OnlinePayDobj();
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setChassisNo(rs.getString("chasi_no"));
                dobj.setTransactionDate(rs.getString("rcpt_dt"));
                dobj.setTransactionNo(rs.getString("transaction_no"));
                dobj.setTransactionAmount(rs.getInt("trans_amt"));
                dobj.setStateName(rs.getString("statename"));
                dobj.setBankReferenceNo(rs.getString("bank_ref_no"));
                dobj.setReceiptNo(rs.getString("rcpt_no"));
                appl_no_list.add(dobj);
            }

        } catch (Exception e) {
            LOGGER.error("for TransactionNo " + transaction_no + " " + e);
            throw new VahanException("Unable to proccess your request.Please Try Again");
        }
        return appl_no_list;
    }

    /*
     * This function used to send SMS.
     */
    public void sendSms(String transactionNo, TransactionManager tmgr) throws VahanException {
        String smsSql = null;
        PreparedStatement psSms = null;
        String mobileSms = null;

        try {
            smsSql = "select distinct v.appl_no ,v.rcpt_no, o.regn_no, o.dlr_name,a.off_name, sum(v.amount+v.surcharge+v.penalty+v.interest+v.tax1+v.tax2 + v.prv_adjustment-v.exempted-v.rebate) as amount \n"
                    + "from vph_rcpt_cart v left outer join vva_owner o \n"
                    + "on v.appl_no = o.appl_no \n"
                    + " left outer join tm_office a on v.state_cd=a.state_cd and v.off_cd = a.off_cd "
                    + "where v.transaction_no = ? \n"
                    + "group by 1,2,3,4,5 order by 1 ";

            psSms = tmgr.prepareStatement(smsSql);
            psSms.setString(1, transactionNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                if ("OR".equals(Util.getUserStateCode())) {
                    mobileSms = "Received Rs." + rs.getInt("amount") + "/- against new registration vide receipt no " + rs.getString("rcpt_no") + " dated " + JSFUtils.convertToStandardDateFormat(new java.util.Date()) + " %0D%0A \n"
                            + " from Dealer " + rs.getString("dlr_name") + ". Provisional registration number [" + rs.getString("regn_no") + "]  is assigned. %0D%0A"
                            + " Please wait till you get next SMS after approval by RTO " + rs.getString("off_name") + " to take delivery of vehicle from dealer.";
                } else if (rs.getString("regn_no") != null && !rs.getString("regn_no").equals("NEW")) {
                    mobileSms = "[" + rs.getString("appl_no") + "/" + rs.getString("regn_no") + "]%0D%0A"
                            + "Received Rs." + rs.getInt("amount") + "/- against new registration  vide receipt no " + rs.getString("rcpt_no") + " dated " + JSFUtils.convertToStandardDateFormat(new java.util.Date()) + ".%0D%0A"
                            + "From " + rs.getString("dlr_name") + " Thanks " + rs.getString("off_name");
                } else {
                    mobileSms = "[" + rs.getString("appl_no") + "]%0D%0A"
                            + "Received Rs." + rs.getInt("amount") + "/- against new registration  vide receipt no " + rs.getString("rcpt_no") + " dated " + JSFUtils.convertToStandardDateFormat(new java.util.Date()) + ".%0D%0A"
                            + "From " + rs.getString("dlr_name") + " Thanks " + rs.getString("off_name");
                }
                String mobileNo = new FeeImpl().getMobileNo(rs.getString("appl_no"));
                ServerUtil.sendSMS(mobileNo, mobileSms);
            }
        } catch (Exception e) {
            LOGGER.error("for TransactionNo " + transactionNo + " " + e);
            throw new VahanException("Unable to proccess your request.Please Try Again");
        }
    }

    /**
     * #author Kartikey SIngh This function used to send SMS.
     */
    public void sendSms(String transactionNo, TransactionManager tmgr, String stateCode) throws VahanException {
        String smsSql = null;
        PreparedStatement psSms = null;
        String mobileSms = null;

        try {
            smsSql = "select distinct v.appl_no ,v.rcpt_no, o.regn_no, o.dlr_name,a.off_name, sum(v.amount+v.surcharge+v.penalty+v.interest+v.tax1+v.tax2 + v.prv_adjustment-v.exempted-v.rebate) as amount \n"
                    + "from vph_rcpt_cart v left outer join vva_owner o \n"
                    + "on v.appl_no = o.appl_no \n"
                    + " left outer join tm_office a on v.state_cd=a.state_cd and v.off_cd = a.off_cd "
                    + "where v.transaction_no = ? \n"
                    + "group by 1,2,3,4,5 order by 1 ";

            psSms = tmgr.prepareStatement(smsSql);
            psSms.setString(1, transactionNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                if ("OR".equals(stateCode)) {
                    mobileSms = "Received Rs." + rs.getInt("amount") + "/- against new registration vide receipt no " + rs.getString("rcpt_no") + " dated " + JSFUtils.convertToStandardDateFormat(new java.util.Date()) + " %0D%0A \n"
                            + " from Dealer " + rs.getString("dlr_name") + ". Provisional registration number [" + rs.getString("regn_no") + "]  is assigned. %0D%0A"
                            + " Please wait till you get next SMS after approval by RTO " + rs.getString("off_name") + " to take delivery of vehicle from dealer.";
                } else if (rs.getString("regn_no") != null && !rs.getString("regn_no").equals("NEW")) {
                    mobileSms = "[" + rs.getString("appl_no") + "/" + rs.getString("regn_no") + "]%0D%0A"
                            + "Received Rs." + rs.getInt("amount") + "/- against new registration  vide receipt no " + rs.getString("rcpt_no") + " dated " + JSFUtils.convertToStandardDateFormat(new java.util.Date()) + ".%0D%0A"
                            + "From " + rs.getString("dlr_name") + " Thanks " + rs.getString("off_name");
                } else {
                    mobileSms = "[" + rs.getString("appl_no") + "]%0D%0A"
                            + "Received Rs." + rs.getInt("amount") + "/- against new registration  vide receipt no " + rs.getString("rcpt_no") + " dated " + JSFUtils.convertToStandardDateFormat(new java.util.Date()) + ".%0D%0A"
                            + "From " + rs.getString("dlr_name") + " Thanks " + rs.getString("off_name");
                }
                String mobileNo = new FeeImplementation().getMobileNo(rs.getString("appl_no"));
                ServerUtility.sendSMS(mobileNo, mobileSms, stateCode);
            }
        } catch (Exception e) {
            LOGGER.error("for TransactionNo " + transactionNo + " " + e);
            throw new VahanException("Unable to proccess your request.Please Try Again");
        }
    }

    /*
     * This function used to get DetailsList For Pending Cases/Failure Cases.
     */
    public List<OnlinePayDobj> getEReceiptDetailForUnSuccessfulPayments(TransactionManager tmgr, String transaction_no) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        List appl_no_list = new ArrayList();
        RowSet rs;
        try {
            sql = " select o.appl_no ,coalesce(o.regn_no,vb.regn_no) as regn_no,coalesce(o.chasi_no,vb.chasi_no) as chasi_no,o.state_name as statename,to_char(COALESCE(vpd.rcpt_dt,ct.op_dt),'dd-MON-yyyy') as rcpt_dt,vpd.transaction_id as transaction_no,vpd.return_rcpt_no as bank_ref_no,"
                    + " sum(ct.amount + ct.surcharge + ct.interest + ct.tax1 + ct.tax2 + ct.penalty + ct.prv_adjustment - ct.exempted - ct.rebate) as trans_amt,off_name,vh_class_desc,dlr_name,image_logo,image_background \n"
                    + " from vp_rcpt_cart as ct left outer join vva_owner as o on ct.appl_no = o.appl_no \n"
                    + " left outer join vahanpgi.vp_pgi_details vpd on ct.transaction_no = vpd.payment_id \n"
                    + " left outer join tm_configuration_print p on ct.state_cd = p.state_cd"
                    + " left outer join " + TableList.VP_ONLINE_PAY_BALANCE_FEE_DETAILS + " vb on ct.state_cd = vb.state_cd and ct.off_cd = vb.off_cd and ct.appl_no = vb.appl_no and ct.user_cd = vb.user_cd "
                    + " where ct.transaction_no = ? \n"
                    + " group by o.appl_no ,vpd.rcpt_dt,ct.op_dt,vpd.transaction_id,vpd.return_rcpt_no,o.regn_no,o.chasi_no ,statename, vb.regn_no ,vb.chasi_no,off_name,vh_class_desc,dlr_name,image_logo,image_background ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, transaction_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            appl_no_list.clear();
            while (rs.next()) {
                OnlinePayDobj dobj = new OnlinePayDobj();
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setChassisNo(rs.getString("chasi_no"));
                dobj.setTransactionDate(rs.getString("rcpt_dt"));
                dobj.setTransactionNo(rs.getString("transaction_no"));
                dobj.setTransactionAmount(rs.getInt("trans_amt"));
                dobj.setStateName(rs.getString("statename"));
                dobj.setBankReferenceNo(rs.getString("bank_ref_no"));
                dobj.setOfficeName(rs.getString("off_name"));
                dobj.setVhClassDesc(rs.getString("vh_class_desc"));
                dobj.setDealerName(rs.getString("dlr_name"));
                dobj.setStateLogo(rs.getString("image_logo"));
                dobj.setImageBackground(rs.getString("image_background"));
                appl_no_list.add(dobj);
            }
            if (appl_no_list.size() <= 0) {
                sql = "select a.appl_no ,o.regn_no,o.chasi_no as chasi_no,o.state_name as statename,to_char(COALESCE(vpd.rcpt_dt,ct.op_dt),'dd-MON-yyyy') as rcpt_dt,vpd.transaction_id as transaction_no,vpd.return_rcpt_no as bank_ref_no,sum(ct.amount + ct.surcharge + ct.interest + ct.tax1 + ct.tax2 + ct.penalty + ct.prv_adjustment - ct.exempted - ct.rebate) as trans_amt\n"
                        + "from vp_rcpt_cart as ct \n"
                        + "left outer join va_details a on a.appl_no = ct.appl_no\n"
                        + "left outer join vahanpgi.vp_pgi_details vpd on ct.transaction_no = vpd.payment_id \n"
                        + "left outer join vv_owner as o on a.regn_no = o.regn_no\n"
                        + "where ct.transaction_no = ? \n"
                        + "group by a.appl_no ,vpd.rcpt_dt,ct.op_dt,vpd.transaction_id,vpd.return_rcpt_no,o.regn_no,chasi_no ,statename ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, transaction_no);
                rs = tmgr.fetchDetachedRowSet_No_release();
                appl_no_list.clear();
                while (rs.next()) {
                    OnlinePayDobj dobj = new OnlinePayDobj();
                    dobj.setApplNo(rs.getString("appl_no"));
                    dobj.setRegnNo(rs.getString("regn_no"));
                    dobj.setChassisNo(rs.getString("chasi_no"));
                    dobj.setTransactionDate(rs.getString("rcpt_dt"));
                    dobj.setTransactionNo(rs.getString("transaction_no"));
                    dobj.setTransactionAmount(rs.getInt("trans_amt"));
                    dobj.setStateName(rs.getString("statename"));
                    dobj.setBankReferenceNo(rs.getString("bank_ref_no"));
                    appl_no_list.add(dobj);
                }
            }
        } catch (Exception e) {
            LOGGER.error("for TransactionNo " + transaction_no + " " + e);
            throw new VahanException("Unable to proccess your request.Please Try Again");
        }
        return appl_no_list;
    }

    /*
     * This function used for Pending Case.
     */
    public List<OnlinePayDobj> getPendingCaseDetails(String tranNo) throws VahanException {
        TransactionManager tmgr = null;
        List<OnlinePayDobj> payDobjList = null;
        try {
            tmgr = new TransactionManager("getPendingCaseDetails");
            payDobjList = this.getEReceiptDetailForUnSuccessfulPayments(tmgr, tranNo);
        } catch (Exception e) {
            LOGGER.error("for TransactionNo " + tranNo + " " + e);
            throw new VahanException("Unable to proccess your request.Please Try Again");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return payDobjList;
    }

    /*
     * This function used in case of failure of Payment.
     */
    public List<OnlinePayDobj> saveDataAfterFailurePayment(String userId, String transactionNumber, String reason, String userCatg) throws VahanException {
        TransactionManager tmgr = null;
        List<OnlinePayDobj> payDobjList = null;
        try {
            tmgr = new TransactionManager("deletePaymentId");
            payDobjList = this.getEReceiptDetailForUnSuccessfulPayments(tmgr, transactionNumber);
            if (userCatg != null && !userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                String userIdSql = "INSERT INTO vph_online_pay_user_info_fail SELECT current_timestamp,? , state_cd, off_cd, user_cd, user_pwd, mobile_no,?,created_by,created_dt,appl_no \n"
                        + "  FROM vp_online_pay_user_info  where user_cd = ? ";

                PreparedStatement psUserId = tmgr.prepareStatement(userIdSql);
                psUserId.setString(1, TableConstants.ONLINE_PAYMENT);
                psUserId.setString(2, reason);
                psUserId.setLong(3, Long.parseLong(userId));
                ServerUtil.validateQueryResult(tmgr, psUserId.executeUpdate(), psUserId);

                String BalanceInsertSql = "INSERT INTO " + TableList.VPH_ONLINE_PAY_BALANCE_FEE_FAIL + " SELECT current_timestamp, ?, state_cd, off_cd, user_cd, appl_no, "
                        + "regn_no, owner_name, selected_option, chasi_no, ?, op_dt,form_type,vh_class FROM " + TableList.VP_ONLINE_PAY_BALANCE_FEE_DETAILS + "  where user_cd = ?  ";

                PreparedStatement psBalance = tmgr.prepareStatement(BalanceInsertSql);
                psBalance.setString(1, TableConstants.ONLINE_PAYMENT);
                psBalance.setString(2, reason);
                psBalance.setLong(3, Long.parseLong(userId));
                psBalance.executeUpdate();
            }

            String insertSql = "INSERT INTO vph_rcpt_cart_fail  SELECT distinct current_timestamp,? as moved_by ,state_cd,off_cd,appl_no, transaction_no,?,op_dt FROM vp_rcpt_cart where user_cd = ? and transaction_no = ? and transaction_no IS NOT NULL";

            PreparedStatement psInsert = tmgr.prepareStatement(insertSql);
            if (userCatg != null && userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                psInsert.setLong(1, Long.parseLong(userId));
            } else {
                psInsert.setLong(1, Long.parseLong(TableConstants.ONLINE_PAYMENT));
            }
            psInsert.setString(2, reason);
            psInsert.setLong(3, Long.parseLong(userId));
            psInsert.setString(4, transactionNumber);
            ServerUtil.validateQueryResult(tmgr, psInsert.executeUpdate(), psInsert);

            String cartSql = "INSERT INTO vph_rcpt_cart_fail_detail  SELECT current_timestamp,?,state_cd, off_cd,user_cd, appl_no, pur_cd ,period_mode, period_from, period_upto, amount, exempted, \n"
                    + " rebate, surcharge, penalty, interest, transaction_no,pmt_type , \n"
                    + " pmt_catg , service_type, route_class, route_length , no_of_trips , domain_cd , distance_run_in_quarter,op_dt,no_adv_units,tax1,tax2,prv_adjustment\n"
                    + " FROM vp_rcpt_cart where user_cd = ? and transaction_no = ? and transaction_no IS NOT NULL";

            PreparedStatement psCart = tmgr.prepareStatement(cartSql);
            if (userCatg != null && userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                psCart.setString(1, userId);
            } else {
                psCart.setString(1, TableConstants.ONLINE_PAYMENT);
            }
            psCart.setLong(2, Long.parseLong(userId));
            psCart.setString(3, transactionNumber);
            ServerUtil.validateQueryResult(tmgr, psCart.executeUpdate(), psCart);

            String insertBreakUpSql = " INSERT INTO vph_rcpt_cart_fail_breakup  SELECT current_timestamp as moved_on,? as moved_by,state_cd,off_cd,transaction_no,? as dealer_cd,pur_cd,sum(amount) as ttlAmt, sum(exempted) as ttlExempted, sum(rebate) as ttlRebate, sum(surcharge) as ttlSurcharge, sum(penalty) as ttlPenalty, sum(interest) as ttlInterest,sum(tax1) as ttlTax1, sum(tax2) as ttlTax2,sum(prv_adjustment) as ttlPrvAdjustment \n"
                    + " from vp_rcpt_cart where user_cd = ? and transaction_no = ? \n"
                    + " group by moved_on,user_cd,state_cd,off_cd,transaction_no,pur_cd \n";

            PreparedStatement psInsertBreakUp = tmgr.prepareStatement(insertBreakUpSql);
            if (userCatg != null && userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                Map makerAndDealerDetail = OwnerImpl.getDealerDetail(Long.parseLong(userId));
                String dealerCd = makerAndDealerDetail.get("dealer_cd").toString();
                psInsertBreakUp.setLong(1, Long.parseLong(userId));
                if (dealerCd != null) {
                    psInsertBreakUp.setString(2, dealerCd);
                }
            } else {
                psInsertBreakUp.setLong(1, Long.parseLong(TableConstants.ONLINE_PAYMENT));
                psInsertBreakUp.setString(2, "ONLINE_RTO");
            }
            psInsertBreakUp.setLong(3, Long.parseLong(userId));
            psInsertBreakUp.setString(4, transactionNumber);
            ServerUtil.validateQueryResult(tmgr, psInsertBreakUp.executeUpdate(), psInsertBreakUp);

            String updateSql = "update vp_rcpt_cart set transaction_no = null  where transaction_no = ? and transaction_no IS NOT NULL and user_cd = ? ";
            PreparedStatement ps = tmgr.prepareStatement(updateSql);
            ps.setString(1, transactionNumber);
            ps.setLong(2, Long.parseLong(userId));
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
            
            String updateSqlTemp = "update " + TableList.VP_RCPT_CART_TEMP + " set transaction_no = null  where transaction_no = ? and transaction_no IS NOT NULL and user_cd = ? ";
            PreparedStatement psTemp = tmgr.prepareStatement(updateSqlTemp);
            psTemp.setString(1, transactionNumber);
            psTemp.setLong(2, Long.parseLong(userId));
            psTemp.executeUpdate();

            tmgr.commit();
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error("for TransactionNo " + transactionNumber + " " + e);
            throw new VahanException("Unable to proccess your request.Please Try Again");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return payDobjList;
    }

    public List<OnlinePayDobj> updateFailTransAppls(String transactionNo, String userId, String stateCd, int offCd, String reason) throws VahanException {
        TransactionManager tmgr = null;
        boolean status = false;
        String failedApplList = "";
        List<OnlinePayDobj> dobjList = null;
        int vpRcptApplCount = 0;
        int failedApplAmount = 0;
        try {
            tmgr = new TransactionManager("updateFailTransactionStatus");
            String sql = "select case when (a.vpRcptApplCount = a.failApplCount) then true else false end as status,a.failApplList,a.vpRcptApplCount,a.failApplCount,a.failAmount \n"
                    + " from ( \n"
                    + " select count(distinct vp.appl_no) as vpRcptApplCount ,(select count(vph.appl_no) from vph_rcpt_cart_fail vph where vph.transaction_no = ? and vph.state_cd = ? and vph.off_cd = ? and vph.user_cd = ? ) AS failApplCount, \n"
                    + " (SELECT array_to_string(array(select '''' || vph.appl_no || '''' from vph_rcpt_cart_fail vph \n"
                    + " where vph.user_cd = ? and vph.state_cd = ? and vph.off_cd = ? AND vph.transaction_no = ? group by vph.appl_no),', ') as failApplList) \n"
                    + ",(SELECT sum(vph_brkup.ttlamount+vph_brkup.ttlsurcharge+vph_brkup.ttlpenalty+vph_brkup.ttlinterest+vph_brkup.ttltax1+vph_brkup.ttltax2-vph_brkup.ttlexempted-vph_brkup.ttlrebate) from vph_rcpt_cart_fail_breakup  vph_brkup \n"
                    + " where vph_brkup.user_cd = ? and vph_brkup.state_cd = ? and vph_brkup.off_cd = ? AND vph_brkup.transaction_no = ? group by vph_brkup.transaction_no) as failAmount"
                    + " from vp_rcpt_cart vp \n"
                    + " where vp.transaction_no is null and vp.state_cd = ? and vp.off_cd = ? and vp.user_cd =  ? and vp.appl_no in \n"
                    + " (select f.appl_no from vph_rcpt_cart_fail f where f.transaction_no = ? and f.state_cd = ?  and f.off_cd = ? and f.user_cd = ?)) as a ";

            PreparedStatement ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, transactionNo);
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ps.setLong(i++, Long.parseLong(userId));
            ps.setLong(i++, Long.parseLong(userId));
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ps.setString(i++, transactionNo);
            ps.setLong(i++, Long.parseLong(userId));
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ps.setString(i++, transactionNo);
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ps.setLong(i++, Long.parseLong(userId));
            ps.setString(i++, transactionNo);
            ps.setString(i++, stateCd);
            ps.setInt(i++, offCd);
            ps.setLong(i++, Long.parseLong(userId));
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                status = rs.getBoolean("status");
                failedApplList = rs.getString("failApplList");
                vpRcptApplCount = rs.getInt("vpRcptApplCount");
                failedApplAmount = rs.getInt("failAmount");
            } else {
                throw new VahanException("Problem in getting status of fail transaction");
            }

            if (status) {
                dobjList = this.revertDataAndMoveFromVphFailToVhFail(tmgr, userId, transactionNo, reason, stateCd, offCd, failedApplList, vpRcptApplCount, failedApplAmount);
                if (dobjList != null && !dobjList.isEmpty()) {
                    tmgr.commit();
                }
            } else {
                dobjList = new ArrayList();
                OnlinePayDobj dobj = new OnlinePayDobj();
                dobj.setTransactionAmount(failedApplAmount);
                dobj.setApplNoList(failedApplList);
                dobj.setTransactionNo(transactionNo);
                dobj.setReverifyActionType(TableConstants.REVERIFY_WALLET);
                dobjList.add(dobj);
            }
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception e) {
            LOGGER.error("for TransactionNo " + transactionNo + " " + e);
            throw new VahanException("Problem in Reverifcation");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dobjList;
    }

    public List<OnlinePayDobj> revertDataAndMoveFromVphFailToVhFail(TransactionManager tmgr, String userId, String transactionNo, String reason, String stateCd, int offCd, String failedApplList, int vpRcptApplCount, int failedApplAmount) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        int countOfUpdatedApplNo = 0;
        int amountOfUpdatedApplNo = 0;
        List<OnlinePayDobj> dobjList = null;
        WalletImpl walletImpl = new WalletImpl();
        try {
            // used  to lock the record against the application no
            sql = "update vp_rcpt_cart set appl_no = appl_no where user_cd = ? and  state_cd = ? and off_cd = ? and transaction_no IS NULL and appl_no IN (" + failedApplList + ")";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(userId));
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

            sql = "update vp_rcpt_cart set transaction_no = ? where user_cd = ? and  state_cd = ? and off_cd = ? and transaction_no IS NULL and appl_no IN (" + failedApplList + ")";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, transactionNo);
            ps.setLong(2, Long.parseLong(userId));
            ps.setString(3, stateCd);
            ps.setInt(4, offCd);
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

            walletImpl.moveDataFromVphFailToVHFail(tmgr, TableConstants.REVERIFY_REVERT, transactionNo, userId, stateCd, offCd);

            dobjList = this.getDetailsOfApplNumber(tmgr, transactionNo, userId, stateCd, offCd, failedApplList);
            if (dobjList != null && !dobjList.isEmpty()) {
                countOfUpdatedApplNo = dobjList.get(0).getApplNoCount();
                amountOfUpdatedApplNo = dobjList.get(0).getTransactionAmount();
            }

            if (countOfUpdatedApplNo != vpRcptApplCount) {
                throw new VahanException("No of application mismatch!!!");
            }

            if (amountOfUpdatedApplNo != failedApplAmount) {
                throw new VahanException("Amount mismatch!!!");
            }

        } catch (VahanException ex) {
            throw ex;
        } catch (Exception e) {
            LOGGER.error("for TransactionNo " + transactionNo + " " + e);
            throw new VahanException("Problem in Reverifcation.");
        }
        return dobjList;
    }

    public List<OnlinePayDobj> getDetailsOfApplNumber(TransactionManager tmgr, String transactionNo, String userId, String stateCd, int offCd, String failedApplList) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        List<OnlinePayDobj> dobjList = new ArrayList();
        try {
            sql = "select count(distinct vp.appl_no) as count,sum(vp.amount+vp.surcharge+vp.penalty+vp.interest+vp.tax1+vp.tax2-vp.exempted-vp.rebate) as amount from vp_rcpt_cart vp where vp.transaction_no IS NOT NULL and vp.transaction_no = ? and vp.user_cd = ? and vp.state_cd = ? and vp.off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, transactionNo);
            ps.setLong(2, Long.parseLong(userId));
            ps.setString(3, stateCd);
            ps.setInt(4, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                OnlinePayDobj dobj = new OnlinePayDobj();
                dobj.setApplNoCount(rs.getInt("count"));
                dobj.setTransactionAmount(rs.getInt("amount"));
                dobj.setApplNoList(failedApplList);
                dobj.setTransactionNo(transactionNo);
                dobj.setReverifyActionType(TableConstants.REVERIFY_REVERT);
                dobjList.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error("for TransactionNo " + transactionNo + " " + e);
            throw new VahanException("Problem in getting the count of updated application number in reverification");
        }
        return dobjList;
    }

    public List<OnlinePayDobj> getRecptNoRegnNoAndInsertDataForTradeCertificate(String userId, String transactionNo, int actionCode, String stateCd, int offCd, String userCatg) throws VahanException {
        TransactionManager tmgr = null;
        List<OnlinePayDobj> payList = null;
        List<OnlinePayDobj> onlinePayDbjList = new ArrayList<OnlinePayDobj>();
        java.sql.Timestamp rcptTimeStamp = null;
        try {
            tmgr = new TransactionManager("getRecptNoRegnNoAndInsertDataForTradeCertificate");
            String reciptNo = ServerUtil.getUniqueOfficeRcptNo(tmgr, stateCd, offCd, TableConstants.DEALER_RCPT_FLAG, false);
            String selectSql = "SELECT a.appl_no,b.regn_no,b.pur_cd,b.state_cd,"
                    + " b.off_cd \n"
                    + " from vp_rcpt_cart a\n"
                    + " inner join va_details b on a.appl_no = b.appl_no and a.pur_cd = b.pur_cd \n"
                    + " where a.user_cd = ? and a.transaction_no = ? ";
            PreparedStatement psSelect = tmgr.prepareStatement(selectSql);
            psSelect.setLong(1, Long.parseLong(userId));
            psSelect.setString(2, transactionNo);

            RowSet rst = tmgr.fetchDetachedRowSet_No_release();
            while (rst.next()) {
                rcptTimeStamp = new java.sql.Timestamp(new Date().getTime());
                List<OnlinePayDobj> taxBreakUpDetails = this.getTaxBreakUpDetails(rst.getString("appl_no"), tmgr);
                List<OnlinePayDobj> feeBreakUpDetails = this.getFeeBreakUpDetails(rst.getString("appl_no"), tmgr);
                OnlinePayDobj payDobj = new OnlinePayDobj();
                payDobj.setReceiptNo(reciptNo);
                payDobj.setApplNo(rst.getString("appl_no"));
                payDobj.setTaxBreakUpDetails(taxBreakUpDetails);
                payDobj.setFeeBreakUpDetails(feeBreakUpDetails);
                payDobj.setPurCd(rst.getInt("pur_cd"));
                payDobj.setRegnNo(rst.getString("regn_no"));
                onlinePayDbjList.add(payDobj);
            }
            String updateSql = "Update  vp_rcpt_cart set rcpt_no = ? ,rcpt_dt = ?  where appl_no = ? and user_cd = ? and transaction_no = ? and transaction_no IS NOT NULL";
            PreparedStatement psUpdate = tmgr.prepareStatement(updateSql);
            for (OnlinePayDobj pyDobj : onlinePayDbjList) {
                int i = 1;
                psUpdate.setString(i++, pyDobj.getReceiptNo());
                psUpdate.setTimestamp(i++, rcptTimeStamp);
                psUpdate.setString(i++, pyDobj.getApplNo());
                psUpdate.setLong(i++, Long.parseLong(userId));
                psUpdate.setString(i++, transactionNo);
                ServerUtil.validateQueryResult(tmgr, psUpdate.executeUpdate(), psUpdate);
            }
            payList = this.saveFeeDetailsAfterSuccesfullPayment(tmgr, transactionNo, onlinePayDbjList, actionCode, userId, stateCd, offCd, userCatg, "TradeCertificate", rcptTimeStamp, null);
            if (payList != null && !payList.isEmpty()) {
                tmgr.commit();
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error("for TransactionNo " + transactionNo + " " + e);
            throw new VahanException("Receipt Generation Failed !!!");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return payList;
    }

    /*
     * This function used to get DetailsList of Trade Certificate from temporary table (vp_rcpt_cart)
     */
    public List<OnlinePayDobj> getDetailListForTradeCertificate(TransactionManager tmgr, String transaction_no, String userId) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        List<OnlinePayDobj> dobjList = new ArrayList<>();
        try {
            sql = "select ct.transaction_no as pay_id,ct.appl_no as appl_no,ct.pur_cd,to_char(ct.period_from, 'yyyy-mm-dd') as period_from, "
                    + " to_char(ct.period_upto, 'yyyy-mm-dd') as period_upto,ct.penalty,sum(ct.amount+ct.surcharge+ct.interest+ct.tax1+ct.tax2-ct.exempted-ct.rebate) as amt ,"
                    + " vaTC.state_cd,vaTC.dealer_cd,vmDealer.dealer_name,vaTC.off_cd,vmDealer.contact_no,vmDealer.email_id,ct.rcpt_no,ct.period_mode as tax_mode \n"
                    + " from vp_rcpt_cart ct inner join va_trade_certificate vaTC \n"
                    + " on ct.appl_no = vaTC.appl_no and ct.user_cd = ? and ct.transaction_no = ? \n"
                    + " left outer join vm_dealer_mast vmDealer on vmDealer.dealer_cd = vaTC.dealer_cd \n"
                    + " group by ct.transaction_no ,ct.appl_no ,ct.period_from,ct.period_upto,ct.penalty,vaTC.state_cd,vaTC.dealer_cd,ct.pur_cd,vmDealer.dealer_name,vaTC.off_cd,vmDealer.contact_no,vmDealer.email_id \n"
                    + " order by appl_no,pur_cd ";

            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(userId));
            ps.setString(2, transaction_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                OnlinePayDobj dobj = new OnlinePayDobj();
                dobj.setTransactionNo(rs.getString("pay_id"));
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setPurCd(rs.getInt("pur_cd"));
                dobj.setPeriodFrom(rs.getString("period_from"));
                dobj.setPeriodUpto(rs.getString("period_upto"));
                dobj.setPenalty(rs.getLong("penalty"));
                dobj.setAmount(rs.getLong("amt"));
                dobj.setStateCd(rs.getString("state_cd"));
                dobj.setRegnNo(rs.getString("dealer_cd"));
                dobj.setOwnerName(rs.getString("dealer_name"));
                dobj.setOffCode(rs.getInt("off_cd"));
                dobj.setMobileNumber(rs.getString("contact_no"));
                dobj.setEmailId(rs.getString("email_id"));
                dobj.setReceiptNo(rs.getString("rcpt_no"));
                dobj.setTaxMode(rs.getString("tax_mode"));
                dobjList.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error("for TransactionNo " + transaction_no + " " + e);
            throw new VahanException("Unable to proccess your request.Please Try Again");
        }
        return dobjList;
    }

    public List<OnlinePayDobj> getEReceiptDetailForTradeCertificate(String transaction_no, TransactionManager tmgr, String stateCd, int offCd) throws VahanException {

        String sql = null;
        PreparedStatement ps = null;
        List appl_no_list = new ArrayList();
        try {
            sql = " select vaTC.appl_no ,vaTC.dealer_cd as regn_no,vaTC.state_cd as statename,to_char(vpd.rcpt_dt,'dd-MON-yyyy') as rcpt_dt,vpd.transaction_id as transaction_no,vpd.return_rcpt_no as bank_ref_no,sum(ct.amount + ct.surcharge + ct.interest + ct.tax1 + ct.tax2 + ct.penalty - ct.exempted - ct.rebate) as trans_amt, vpd.treasury_ref_no as treasury_no ,vmDealer.dealer_name, ct.rcpt_no \n"
                    + " from vph_rcpt_cart as ct left outer join va_trade_certificate as vaTC on ct.appl_no = vaTC.appl_no \n"
                    + " left outer join vahanpgi.vp_pgi_details vpd on ct.transaction_no = vpd.payment_id \n"
                    + " left outer join vm_dealer_mast vmDealer on vmDealer.dealer_cd = vaTC.dealer_cd \n"
                    + " where ct.transaction_no = ? and ct.state_cd = ? and ct.off_cd = ? \n"
                    + " group by vaTC.appl_no ,vpd.rcpt_dt,vpd.transaction_id,vpd.return_rcpt_no,vmDealer.dealer_name,regn_no ,statename,treasury_ref_no, rcpt_no ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, transaction_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                OnlinePayDobj dobj = new OnlinePayDobj();
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setTransactionDate(rs.getString("rcpt_dt"));
                dobj.setTransactionNo(rs.getString("transaction_no"));
                dobj.setTransactionAmount(rs.getInt("trans_amt"));
                dobj.setStateName(rs.getString("statename"));
                dobj.setBankReferenceNo(rs.getString("bank_ref_no"));
                dobj.setTreasuryRefNo(rs.getString("treasury_no"));
                dobj.setOwnerName(rs.getString("dealer_name"));
                dobj.setReceiptNo(rs.getString("rcpt_no"));
                appl_no_list.add(dobj);
            }

        } catch (Exception e) {
            LOGGER.error("for TransactionNo " + transaction_no + " " + e);
            throw new VahanException("Unable to proccess your request.Please Try Again");
        }
        return appl_no_list;
    }

    public static void updateReVerifiedTransactionNo(String transactionNo, String stateCd, String userCd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("updateTransactionNo");
            sql = "select appl_no from vph_rcpt_cart_fail where transaction_no = ? and state_cd= ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, transactionNo);
            ps.setString(2, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                sql = "update vp_rcpt_cart set transaction_no = ? where appl_no = ? and user_cd = ? and transaction_no IS NULL ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, transactionNo);
                ps.setString(2, rs.getString("appl_no"));
                ps.setLong(3, Long.parseLong(userCd));
                if (ps.executeUpdate() > 0) {
                    tmgr.commit();
                } else {
                    throw new VahanException("Re-verify Tansaction no. updation failed !!!");
                }
            }

        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Unable to update the re-vrify transaction no.Please Try Again");
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

    public void insertNpPermitDetails(PermitFeeDobj dobj, String trans_status, TransactionManager tmgr) throws VahanException {
        String pmt_from = null;
        String pmt_upto = null;
        String auth_fr = null;
        String auth_to = null;
        String auth_no = "";
        String pmt_no = "";
        ResultSet rs = null;
        PreparedStatement psUserPass = null;
        int purcd = Integer.parseInt(dobj.getPur_cd());
        try {
            if (purcd == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD || (purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD && dobj != null && dobj.getAppl_no() != null)) {
                PassengerPermitDetailDobj passDobj = new PassengerPermitDetailImpl().set_permit_appl_db_to_dobj(dobj.getAppl_no());
                if (passDobj != null && passDobj.getPaction().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEWAL_PUR_CD))) {
                    OwnerImpl ownImpl = new OwnerImpl();
                    Owner_dobj ownDobj = ownImpl.set_Owner_appl_db_to_dobj(dobj.getRegn_no(), null, null, 0);
                    ownDobj.setPmt_type(Integer.parseInt(passDobj.getPmt_type()));
                    ownDobj.setPmt_catg(Integer.parseInt(passDobj.getPmtCatg()));
                    int vehAge = new FitnessImpl().getVehAgeValidity(ownDobj);
                    Date maxValidUpto = null;
                    if (vehAge != 99) {
                        maxValidUpto = ServerUtil.dateRange(ownDobj.getPurchase_dt(), vehAge, 0, -1);

                    }
                    String query = " Select c.period_mode,c.period,a.valid_from ,a.valid_upto,a.pmt_no ,b.auth_no,\n"
                            + "   b.auth_fr , b.auth_to from  permit.vt_permit a \n"
                            + "   inner join permit.va_permit c on c.state_cd = a.state_cd and c.regn_no = a.regn_no \n"
                            + "   inner join permit.vt_permit_home_auth b on b.pmt_no = a.pmt_no and b.regn_no = a.regn_no \n"
                            + "   where c.appl_no=? and  a.state_cd=? and a.regn_no=? ";
                    psUserPass = tmgr.prepareStatement(query);
                    psUserPass.setString(1, dobj.getAppl_no());
                    psUserPass.setString(2, Util.getUserStateCode());
                    psUserPass.setString(3, dobj.getRegn_no());
                    rs = psUserPass.executeQuery();
                    if (rs.next()) {
                        if (rs.getString("period_mode").equalsIgnoreCase("Y")) {
                            pmt_from = DateUtil.parseDateYYYYMMDDToString(ServerUtil.dateRange(rs.getDate("valid_upto"), 0, 0, 1));
                            pmt_upto = DateUtil.parseDateYYYYMMDDToString(ServerUtil.dateRange((ServerUtil.dateRange(rs.getDate("valid_upto"), 0, 0, 1)), rs.getInt("period"), 0, -1));
                        } else if (rs.getString("period_mode").equalsIgnoreCase("M")) {
                            pmt_from = DateUtil.parseDateYYYYMMDDToString(ServerUtil.dateRange(rs.getDate("valid_upto"), 0, 0, 1));
                            pmt_upto = DateUtil.parseDateYYYYMMDDToString(ServerUtil.dateRange((ServerUtil.dateRange(rs.getDate("valid_upto"), 0, 0, 1)), 0, rs.getInt("period"), -1));
                        }

                        pmt_no = rs.getString("pmt_no");
                        auth_no = "";
                        auth_fr = DateUtil.parseDateYYYYMMDDToString(ServerUtil.dateRange(rs.getDate("auth_to"), 0, 0, 1));;
                        auth_to = DateUtil.parseDateYYYYMMDDToString(ServerUtil.dateRange(rs.getDate("auth_to"), 1, 0, 0));
                        if (maxValidUpto != null && maxValidUpto.compareTo(ServerUtil.dateRange(rs.getDate("auth_to"), 1, 0, 0)) <= 0) {
                            auth_to = DateUtil.parseDateYYYYMMDDToString(maxValidUpto);
                        }
                        if (maxValidUpto != null && maxValidUpto.compareTo(JSFUtils.getStringToDateyyyyMMdd(pmt_upto)) <= 0) {
                            pmt_upto = DateUtil.parseDateYYYYMMDDToString(maxValidUpto);
                        }
                    }
                } else {
                    String query = " Select to_char(a.valid_from,'yyyy-MM-dd') as valid_from ,to_char(a.valid_upto,'yyyy-MM-dd') as valid_upto,a.pmt_no ,b.auth_no,"
                            + " to_char(b.auth_fr,'yyyy-MM-dd') as auth_fr , to_char(b.auth_to,'yyyy-MM-dd') as auth_to from  permit.vt_permit a "
                            + " left outer join permit.va_permit_home_auth b on b.pmt_no = a.pmt_no and b.regn_no = a.regn_no"
                            + " where b.appl_no=? and  a.state_cd=? and a.regn_no=? ";
                    psUserPass = tmgr.prepareStatement(query);
                    psUserPass.setString(1, dobj.getAppl_no());
                    psUserPass.setString(2, Util.getUserStateCode());
                    psUserPass.setString(3, dobj.getRegn_no());
                    rs = psUserPass.executeQuery();
                    if (rs.next()) {
                        pmt_from = rs.getString("valid_from");
                        pmt_upto = rs.getString("valid_upto");
                        pmt_no = rs.getString("pmt_no");
                        auth_no = rs.getString("auth_no");
                        auth_fr = rs.getString("auth_fr");
                        auth_to = rs.getString("auth_to");
                    }
                }
            }

            if (purcd == TableConstants.VM_PMT_CANCELATION_PUR_CD || purcd == TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD) {
                String query = " Select to_char(a.valid_from,'yyyy-MM-dd') as valid_from ,to_char(a.valid_upto,'yyyy-MM-dd') as valid_upto,a.pmt_no ,b.auth_no,"
                        + " to_char(b.auth_fr,'yyyy-MM-dd') as auth_fr , to_char(b.auth_to,'yyyy-MM-dd') as auth_to from  permit.vt_permit a "
                        + " left outer join permit.vt_permit_home_auth b on b.pmt_no = a.pmt_no and b.regn_no = a.regn_no "
                        + " where  a.state_cd=? and a.regn_no=? ";
                psUserPass = tmgr.prepareStatement(query);
                psUserPass.setString(1, Util.getUserStateCode());
                psUserPass.setString(2, dobj.getRegn_no());
                rs = psUserPass.executeQuery();
                if (rs.next()) {
                    pmt_from = rs.getString("valid_from");
                    pmt_upto = rs.getString("valid_upto");
                    pmt_no = rs.getString("pmt_no");
                    auth_no = rs.getString("auth_no");
                    auth_fr = rs.getString("auth_fr");
                    auth_to = rs.getString("auth_to");
                }
            }
            if (purcd == TableConstants.VM_PMT_FRESH_PUR_CD) {
                psUserPass = null;
                String query = " INSERT INTO permit.va_np_detail(state_cd, off_cd, appl_no, pmt_no, regn_no, valid_from,valid_upto,auth_no,auth_from,auth_upto,pur_cd,permit_type,permit_catg,paymentstatus,trans_status) \n"
                        + " VALUES (?, ?, ?," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "''" : pmt_no) + " , ?," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date" : pmt_from) + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date + interval  '5 year'" : pmt_upto)
                        + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "''" : auth_no) + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date" : auth_fr) + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date -1  + interval  '1 year'" : auth_to) + ",?,?,?,?,?) ";
                psUserPass = tmgr.prepareStatement(query);

                int m = 1;
                psUserPass.setString(m++, Util.getUserStateCode());
                psUserPass.setInt(m++, Util.getUserLoginOffCode());
                psUserPass.setString(m++, dobj.getAppl_no());
                psUserPass.setString(m++, dobj.getRegn_no());
                psUserPass.setInt(m++, Integer.parseInt(dobj.getPur_cd()));
                psUserPass.setInt(m++, Integer.parseInt(dobj.getPermit_type()));
                psUserPass.setInt(m++, dobj.getPermit_catg());
                psUserPass.setString(m++, dobj.getPaymentStaus());
                psUserPass.setString(m++, trans_status);
                psUserPass.executeUpdate();
            }
            if (purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD && pmt_from != null && pmt_upto != null && auth_fr != null && auth_to != null) {
                psUserPass = null;
                String sql = "Select * from permit.va_np_detail where regn_no = ? and appl_no=?";
                psUserPass = tmgr.prepareStatement(sql);
                psUserPass.setString(1, dobj.getRegn_no());
                psUserPass.setString(2, dobj.getAppl_no());
                RowSet rowSet = tmgr.fetchDetachedRowSet_No_release();
                if (rowSet.next()) {
                    sql = "update permit.va_np_detail set pmt_no=?,valid_from=?,valid_upto=?,auth_no=?,auth_from=?,auth_upto=? WHERE REGN_NO =? and appl_no=?";
                    psUserPass = tmgr.prepareStatement(sql);
                    psUserPass.setString(1, pmt_no);
                    psUserPass.setDate(2, new java.sql.Date(JSFUtils.getStringToDateyyyyMMdd(pmt_from).getTime()));
                    psUserPass.setDate(3, new java.sql.Date(JSFUtils.getStringToDateyyyyMMdd(pmt_upto).getTime()));
                    psUserPass.setString(4, auth_no);
                    psUserPass.setDate(5, new java.sql.Date(JSFUtils.getStringToDateyyyyMMdd(auth_fr).getTime()));
                    psUserPass.setDate(6, new java.sql.Date(JSFUtils.getStringToDateyyyyMMdd(auth_to).getTime()));
                    psUserPass.setString(7, dobj.getRegn_no());
                    psUserPass.setString(8, dobj.getAppl_no());
                    psUserPass.executeUpdate();
                } else {
                    pmt_from = "'" + pmt_from + "'";
                    pmt_upto = "'" + pmt_upto + "'";
                    auth_fr = "'" + auth_fr + "'";
                    auth_to = "'" + auth_to + "'";
                    auth_no = "'" + auth_no + "'";
                    pmt_no = "'" + pmt_no + "'";
                    psUserPass = null;
                    String query = " INSERT INTO permit.va_np_detail(state_cd, off_cd, appl_no, pmt_no, regn_no, valid_from,valid_upto,auth_no,auth_from,auth_upto,pur_cd,permit_type,permit_catg,paymentstatus,trans_status) \n"
                            + " VALUES (?, ?, ?," + pmt_no + " , ?," + pmt_from + "," + pmt_upto
                            + "," + auth_no + "," + auth_fr + "," + auth_to + ",?,?,?,?,?) ";
                    psUserPass = tmgr.prepareStatement(query);
                    int m = 1;
                    psUserPass.setString(m++, Util.getUserStateCode());
                    psUserPass.setInt(m++, Util.getUserLoginOffCode());
                    psUserPass.setString(m++, dobj.getAppl_no());
                    psUserPass.setString(m++, dobj.getRegn_no());
                    psUserPass.setInt(m++, Integer.parseInt(dobj.getPur_cd()));
                    psUserPass.setInt(m++, Integer.parseInt(dobj.getPermit_type()));
                    psUserPass.setInt(m++, dobj.getPermit_catg());
                    psUserPass.setString(m++, dobj.getPaymentStaus());
                    psUserPass.setString(m++, trans_status);
                    psUserPass.executeUpdate();
                }
            }
            if (purcd != TableConstants.VM_PMT_FRESH_PUR_CD && purcd != TableConstants.VM_PMT_RENEWAL_PUR_CD && pmt_from != null && pmt_upto != null && auth_fr != null && auth_to != null) {
                psUserPass = null;
                pmt_from = "'" + pmt_from + "'";
                pmt_upto = "'" + pmt_upto + "'";
                auth_fr = "'" + auth_fr + "'";
                auth_to = "'" + auth_to + "'";
                auth_no = "'" + auth_no + "'";
                pmt_no = "'" + pmt_no + "'";
                psUserPass = null;
                String query = " INSERT INTO permit.va_np_detail(state_cd, off_cd, appl_no, pmt_no, regn_no, valid_from,valid_upto,auth_no,auth_from,auth_upto,pur_cd,permit_type,permit_catg,paymentstatus,trans_status) \n"
                        + " VALUES (?, ?, ?," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "''" : pmt_no) + " , ?," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date" : pmt_from) + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date + interval  '5 year'" : pmt_upto)
                        + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "''" : auth_no) + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date" : auth_fr) + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date -1  + interval  '1 year'" : auth_to) + ",?,?,?,?,?) ";
                psUserPass = tmgr.prepareStatement(query);

                int m = 1;
                psUserPass.setString(m++, Util.getUserStateCode());
                psUserPass.setInt(m++, Util.getUserLoginOffCode());
                psUserPass.setString(m++, dobj.getAppl_no());
                psUserPass.setString(m++, dobj.getRegn_no());
                psUserPass.setInt(m++, Integer.parseInt(dobj.getPur_cd()));
                psUserPass.setInt(m++, Integer.parseInt(dobj.getPermit_type()));
                psUserPass.setInt(m++, dobj.getPermit_catg());
                psUserPass.setString(m++, dobj.getPaymentStaus());
                psUserPass.setString(m++, trans_status);
                psUserPass.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.toString());
        }
    }

    /**
     * @author Kartikey Singh
     */
    public void insertNpPermitDetails(PermitFeeDobj dobj, String trans_status, TransactionManager tmgr, String stateCode, int userLoginOffCode, int selectedOffCode) throws VahanException {
        String pmt_from = null;
        String pmt_upto = null;
        String auth_fr = null;
        String auth_to = null;
        String auth_no = "";
        String pmt_no = "";
        ResultSet rs = null;
        PreparedStatement psUserPass = null;
        int purcd = Integer.parseInt(dobj.getPur_cd());
        try {
            if (purcd == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD || (purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD && dobj != null && dobj.getAppl_no() != null)) {
                PassengerPermitDetailDobj passDobj = new PassengerPermitDetailImplementation(stateCode).set_permit_appl_db_to_dobj(dobj.getAppl_no());
                if (passDobj != null && passDobj.getPaction().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEWAL_PUR_CD))) {
                    Owner_dobj ownDobj = new OwnerImplementation().set_Owner_appl_db_to_dobj(dobj.getRegn_no(), null, null, 0, stateCode, selectedOffCode);
                    ownDobj.setPmt_type(Integer.parseInt(passDobj.getPmt_type()));
                    ownDobj.setPmt_catg(Integer.parseInt(passDobj.getPmtCatg()));
                    int vehAge = new FitnessImpl().getVehAgeValidity(ownDobj);
                    Date maxValidUpto = null;
                    if (vehAge != 99) {
                        maxValidUpto = ServerUtility.dateRange(ownDobj.getPurchase_dt(), vehAge, 0, -1);

                    }
                    String query = " Select c.period_mode,c.period,a.valid_from ,a.valid_upto,a.pmt_no ,b.auth_no,\n"
                            + "   b.auth_fr , b.auth_to from  permit.vt_permit a \n"
                            + "   inner join permit.va_permit c on c.state_cd = a.state_cd and c.regn_no = a.regn_no \n"
                            + "   inner join permit.vt_permit_home_auth b on b.pmt_no = a.pmt_no and b.regn_no = a.regn_no \n"
                            + "   where c.appl_no=? and  a.state_cd=? and a.regn_no=? ";
                    psUserPass = tmgr.prepareStatement(query);
                    psUserPass.setString(1, dobj.getAppl_no());
                    psUserPass.setString(2, stateCode);
                    psUserPass.setString(3, dobj.getRegn_no());
                    rs = psUserPass.executeQuery();
                    if (rs.next()) {
                        if (rs.getString("period_mode").equalsIgnoreCase("Y")) {
                            pmt_from = DateUtil.parseDateYYYYMMDDToString(ServerUtil.dateRange(rs.getDate("valid_upto"), 0, 0, 1));
                            pmt_upto = DateUtil.parseDateYYYYMMDDToString(ServerUtil.dateRange((ServerUtil.dateRange(rs.getDate("valid_upto"), 0, 0, 1)), rs.getInt("period"), 0, -1));
                        } else if (rs.getString("period_mode").equalsIgnoreCase("M")) {
                            pmt_from = DateUtil.parseDateYYYYMMDDToString(ServerUtil.dateRange(rs.getDate("valid_upto"), 0, 0, 1));
                            pmt_upto = DateUtil.parseDateYYYYMMDDToString(ServerUtil.dateRange((ServerUtil.dateRange(rs.getDate("valid_upto"), 0, 0, 1)), 0, rs.getInt("period"), -1));
                        }

                        pmt_no = rs.getString("pmt_no");
                        auth_no = "";
                        auth_fr = DateUtil.parseDateYYYYMMDDToString(ServerUtil.dateRange(rs.getDate("auth_to"), 0, 0, 1));;
                        auth_to = DateUtil.parseDateYYYYMMDDToString(ServerUtil.dateRange(rs.getDate("auth_to"), 1, 0, 0));
                        if (maxValidUpto != null && maxValidUpto.compareTo(ServerUtil.dateRange(rs.getDate("auth_to"), 1, 0, 0)) <= 0) {
                            auth_to = DateUtil.parseDateYYYYMMDDToString(maxValidUpto);
                        }
                        if (maxValidUpto != null && maxValidUpto.compareTo(JSFUtils.getStringToDateyyyyMMdd(pmt_upto)) <= 0) {
                            pmt_upto = DateUtil.parseDateYYYYMMDDToString(maxValidUpto);
                        }
                    }
                } else {
                    String query = " Select to_char(a.valid_from,'yyyy-MM-dd') as valid_from ,to_char(a.valid_upto,'yyyy-MM-dd') as valid_upto,a.pmt_no ,b.auth_no,"
                            + " to_char(b.auth_fr,'yyyy-MM-dd') as auth_fr , to_char(b.auth_to,'yyyy-MM-dd') as auth_to from  permit.vt_permit a "
                            + " left outer join permit.va_permit_home_auth b on b.pmt_no = a.pmt_no and b.regn_no = a.regn_no"
                            + " where b.appl_no=? and  a.state_cd=? and a.regn_no=? ";
                    psUserPass = tmgr.prepareStatement(query);
                    psUserPass.setString(1, dobj.getAppl_no());
                    psUserPass.setString(2, stateCode);
                    psUserPass.setString(3, dobj.getRegn_no());
                    rs = psUserPass.executeQuery();
                    if (rs.next()) {
                        pmt_from = rs.getString("valid_from");
                        pmt_upto = rs.getString("valid_upto");
                        pmt_no = rs.getString("pmt_no");
                        auth_no = rs.getString("auth_no");
                        auth_fr = rs.getString("auth_fr");
                        auth_to = rs.getString("auth_to");
                    }
                }
            }

            if (purcd == TableConstants.VM_PMT_CANCELATION_PUR_CD || purcd == TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD) {
                String query = " Select to_char(a.valid_from,'yyyy-MM-dd') as valid_from ,to_char(a.valid_upto,'yyyy-MM-dd') as valid_upto,a.pmt_no ,b.auth_no,"
                        + " to_char(b.auth_fr,'yyyy-MM-dd') as auth_fr , to_char(b.auth_to,'yyyy-MM-dd') as auth_to from  permit.vt_permit a "
                        + " left outer join permit.vt_permit_home_auth b on b.pmt_no = a.pmt_no and b.regn_no = a.regn_no "
                        + " where  a.state_cd=? and a.regn_no=? ";
                psUserPass = tmgr.prepareStatement(query);
                psUserPass.setString(1, stateCode);
                psUserPass.setString(2, dobj.getRegn_no());
                rs = psUserPass.executeQuery();
                if (rs.next()) {
                    pmt_from = rs.getString("valid_from");
                    pmt_upto = rs.getString("valid_upto");
                    pmt_no = rs.getString("pmt_no");
                    auth_no = rs.getString("auth_no");
                    auth_fr = rs.getString("auth_fr");
                    auth_to = rs.getString("auth_to");
                }
            }
            if (purcd == TableConstants.VM_PMT_FRESH_PUR_CD) {
                psUserPass = null;
                String query = " INSERT INTO permit.va_np_detail(state_cd, off_cd, appl_no, pmt_no, regn_no, valid_from,valid_upto,auth_no,auth_from,auth_upto,pur_cd,permit_type,permit_catg,paymentstatus,trans_status) \n"
                        + " VALUES (?, ?, ?," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "''" : pmt_no) + " , ?," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date" : pmt_from) + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date + interval  '5 year'" : pmt_upto)
                        + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "''" : auth_no) + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date" : auth_fr) + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date -1  + interval  '1 year'" : auth_to) + ",?,?,?,?,?) ";
                psUserPass = tmgr.prepareStatement(query);

                int m = 1;
                psUserPass.setString(m++, stateCode);
                psUserPass.setInt(m++, userLoginOffCode);
                psUserPass.setString(m++, dobj.getAppl_no());
                psUserPass.setString(m++, dobj.getRegn_no());
                psUserPass.setInt(m++, Integer.parseInt(dobj.getPur_cd()));
                psUserPass.setInt(m++, Integer.parseInt(dobj.getPermit_type()));
                psUserPass.setInt(m++, dobj.getPermit_catg());
                psUserPass.setString(m++, dobj.getPaymentStaus());
                psUserPass.setString(m++, trans_status);
                psUserPass.executeUpdate();
            }

            if (purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD && pmt_from != null && pmt_upto != null && auth_fr != null && auth_to != null) {
                psUserPass = null;
                String sql = "Select * from permit.va_np_detail where regn_no = ? and appl_no=?";
                psUserPass = tmgr.prepareStatement(sql);
                psUserPass.setString(1, dobj.getRegn_no());
                psUserPass.setString(2, dobj.getAppl_no());
                RowSet rowSet = tmgr.fetchDetachedRowSet_No_release();
                if (rowSet.next()) {
                    sql = "update permit.va_np_detail set pmt_no=?,valid_from=?,valid_upto=?,auth_no=?,auth_from=?,auth_upto=? WHERE REGN_NO =? and appl_no=?";
                    psUserPass = tmgr.prepareStatement(sql);
                    psUserPass.setString(1, pmt_no);
                    psUserPass.setDate(2, new java.sql.Date(JSFUtils.getStringToDateyyyyMMdd(pmt_from).getTime()));
                    psUserPass.setDate(3, new java.sql.Date(JSFUtils.getStringToDateyyyyMMdd(pmt_upto).getTime()));
                    psUserPass.setString(4, auth_no);
                    psUserPass.setDate(5, new java.sql.Date(JSFUtils.getStringToDateyyyyMMdd(auth_fr).getTime()));
                    psUserPass.setDate(6, new java.sql.Date(JSFUtils.getStringToDateyyyyMMdd(auth_to).getTime()));
                    psUserPass.setString(7, dobj.getRegn_no());
                    psUserPass.setString(8, dobj.getAppl_no());
                    psUserPass.executeUpdate();
                } else {
                    pmt_from = "'" + pmt_from + "'";
                    pmt_upto = "'" + pmt_upto + "'";
                    auth_fr = "'" + auth_fr + "'";
                    auth_to = "'" + auth_to + "'";
                    auth_no = "'" + auth_no + "'";
                    pmt_no = "'" + pmt_no + "'";
                    psUserPass = null;
                    String query = " INSERT INTO permit.va_np_detail(state_cd, off_cd, appl_no, pmt_no, regn_no, valid_from,valid_upto,auth_no,auth_from,auth_upto,pur_cd,permit_type,permit_catg,paymentstatus,trans_status) \n"
                            + " VALUES (?, ?, ?," + pmt_no + " , ?," + pmt_from + "," + pmt_upto
                            + "," + auth_no + "," + auth_fr + "," + auth_to + ",?,?,?,?,?) ";
                    psUserPass = tmgr.prepareStatement(query);
                    int m = 1;
                    psUserPass.setString(m++, stateCode);
                    psUserPass.setInt(m++, userLoginOffCode);
                    psUserPass.setString(m++, dobj.getAppl_no());
                    psUserPass.setString(m++, dobj.getRegn_no());
                    psUserPass.setInt(m++, Integer.parseInt(dobj.getPur_cd()));
                    psUserPass.setInt(m++, Integer.parseInt(dobj.getPermit_type()));
                    psUserPass.setInt(m++, dobj.getPermit_catg());
                    psUserPass.setString(m++, dobj.getPaymentStaus());
                    psUserPass.setString(m++, trans_status);
                    psUserPass.executeUpdate();
                }
            }

            if (purcd != TableConstants.VM_PMT_FRESH_PUR_CD && purcd != TableConstants.VM_PMT_RENEWAL_PUR_CD && pmt_from != null && pmt_upto != null && auth_fr != null && auth_to != null) {
                psUserPass = null;
                pmt_from = "'" + pmt_from + "'";
                pmt_upto = "'" + pmt_upto + "'";
                auth_fr = "'" + auth_fr + "'";
                auth_to = "'" + auth_to + "'";
                auth_no = "'" + auth_no + "'";
                pmt_no = "'" + pmt_no + "'";
                psUserPass = null;
                String query = " INSERT INTO permit.va_np_detail(state_cd, off_cd, appl_no, pmt_no, regn_no, valid_from,valid_upto,auth_no,auth_from,auth_upto,pur_cd,permit_type,permit_catg,paymentstatus,trans_status) \n"
                        + " VALUES (?, ?, ?," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "''" : pmt_no) + " , ?," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date" : pmt_from) + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date + interval  '5 year'" : pmt_upto)
                        + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "''" : auth_no) + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date" : auth_fr) + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date -1  + interval  '1 year'" : auth_to) + ",?,?,?,?,?) ";
                psUserPass = tmgr.prepareStatement(query);

                int m = 1;
                psUserPass.setString(m++, stateCode);
                psUserPass.setInt(m++, userLoginOffCode);
                psUserPass.setString(m++, dobj.getAppl_no());
                psUserPass.setString(m++, dobj.getRegn_no());
                psUserPass.setInt(m++, Integer.parseInt(dobj.getPur_cd()));
                psUserPass.setInt(m++, Integer.parseInt(dobj.getPermit_type()));
                psUserPass.setInt(m++, dobj.getPermit_catg());
                psUserPass.setString(m++, dobj.getPaymentStaus());
                psUserPass.setString(m++, trans_status);
                psUserPass.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.toString());
        }
    }

    public List<OnlinePayDobj> getFeeBreakUpDetails(String app_no, TransactionManager tmgr) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        List<OnlinePayDobj> dobjList = new ArrayList<OnlinePayDobj>();

        try {
            sql = "select b.state_cd, b.off_cd, b.appl_no, b.sr_no, b.fee_from, b.fee_upto, b.pur_cd, \n"
                    + " b.fee, b.fine, b.exempted, b.rebate, b.surcharge,b.interest,b.prv_adjustment, c.rcpt_no \n"
                    + " from " + TableList.VP_CART_FEE_BREAKUP + " b left outer join " + TableList.VP_RCPT_CART + " c \n"
                    + " on c.appl_no = b.appl_no and c.pur_cd = b.pur_cd \n"
                    + " where c.appl_no = ? \n";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, app_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                OnlinePayDobj dobj = new OnlinePayDobj();
                dobj.setStateCd(rs.getString("state_cd"));
                dobj.setOffCode(rs.getInt("off_cd"));
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setSrNo(rs.getInt("sr_no"));
                dobj.setPeriodFrom(rs.getString("fee_from"));
                dobj.setPeriodUpto(rs.getString("fee_upto"));
                dobj.setPurCd(rs.getInt("pur_cd"));
                dobj.setFeeBreakUpAmount(rs.getLong("fee"));
                dobj.setFeeBreakUpFine(rs.getLong("fine"));
                dobj.setExempted(rs.getLong("exempted"));
                dobj.setFeeBreakUpRebate(rs.getLong("rebate"));
                dobj.setFeeBreakUpSurcharge(rs.getLong("surcharge"));
                dobj.setFeeBreakUpInterest(rs.getLong("interest"));
                dobj.setPrevAdjustement(rs.getInt("prv_adjustment"));
                dobj.setReceiptNo(rs.getString("rcpt_no"));
                dobjList.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error("for Application No " + app_no + " " + e);
            throw new VahanException("Unable to proccess your request.Please Try Again");
        }
        return dobjList;
    }

    public boolean isExistRcptCartTemp(TransactionManager tmgr, String applNo) throws SQLException {
        String sql = "select appl_no from " + TableList.VP_RCPT_CART_TEMP + " where appl_no = ? ";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, applNo);
        RowSet rsTemp = tmgr.fetchDetachedRowSet_No_release();
        return rsTemp.next();
    }
}