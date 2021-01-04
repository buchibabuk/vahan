package nic.vahan.form.impl.eChallan;

import java.io.Serializable;
import nic.vahan.form.dobj.eChallan.ChallanFeeDobj;
import nic.vahan.form.dobj.eChallan.ChallanOwnerDobj;
import nic.vahan.form.dobj.eChallan.ChallanOwnerTaxDobj;
import nic.vahan.form.dobj.eChallan.DrivingLicenseDobj;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.vahan.server.CommonUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.eChallan.CompoundingInOfficeDobj;
import nic.vahan.form.impl.Receipt_Master_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

public class ChallanFeeImpl implements Serializable {

    private static Logger LOGGER = Logger.getLogger(ChallanFeeImpl.class);

    public boolean isValidChallanNoforFeeCollection(String applicationNo) throws VahanException {
        boolean isValid = false;
        String whereIam = "ChallanDAO.isValidChallanNoforFeeCollection";
        TransactionManager tmgr = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String isValidChallanNoforFeeCollectionSQL = "SELECT * FROM  " + TableList.VT_CHALLAN_SETTLEMANT + "   WHERE appl_no=?";

        String isCourtRefCaseSQL = "SELECT appl_no, court_cd, hearing_date, court_paid_amount, court_rcpt_no, \n"
                + "  remarks, court_status, op_dt, state_cd, off_cd, court_rcpt_date\n"
                + "  FROM  " + TableList.VT_CHALLAN_REFER_TO_COURT + "   \n"
                + "  where appl_no=? and (court_rcpt_no is null or remarks is null or hearing_date is null)";

        try {
            tmgr = new TransactionManager(whereIam);
            pstmt = tmgr.prepareStatement(isValidChallanNoforFeeCollectionSQL);
            pstmt.setString(1, applicationNo);
            rs = tmgr.fetchDetachedRowSet();
            if (!rs.next()) {
                isValid = false;
                throw new VahanException("Challan not settled yet.");
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

        return isValid;
    }

    public CompoundingInOfficeDobj fatchDetailsOfCourt(String applicationNo) throws VahanException {
        boolean isDataExist = false;
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        RowSet rowSet = null;
        CompoundingInOfficeDobj dobj = null;
        try {
            tmgr = new TransactionManager("fatching COURT DECISION");
            String sqlChallanRefToCourt = "SELECT appl_no, court_cd, hearing_date, court_paid_amount, court_rcpt_no, \n"
                    + "  remarks, court_status, op_dt, state_cd, off_cd, court_rcpt_date\n"
                    + "  FROM " + TableList.VT_CHALLAN_REFER_TO_COURT
                    + "  where appl_no=? and court_paid_amount!=0 and court_paid_amount is not null ";
            pstmt = tmgr.prepareStatement(sqlChallanRefToCourt);
            pstmt.setString(1, applicationNo);
            rowSet = tmgr.fetchDetachedRowSet();
            if (rowSet.next()) {
                dobj = new CompoundingInOfficeDobj();
                dobj.setCourtDecision(rowSet.getString("remarks"));
                dobj.setCourtPaidAmount(rowSet.getString("court_paid_amount"));
                dobj.setCourtRecieptNo(rowSet.getString("court_rcpt_no"));
                dobj.setCourtRecieptDate(rowSet.getDate("court_rcpt_date"));
                isDataExist = true;
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        }
        return dobj;
    }

    public ChallanOwnerDobj getChallanOwnerDetails(String applicationNo) throws VahanException {
        String whereIam = "ChallanDAO.getChallanOwnerDetails";
        TransactionManager tmgr = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        ChallanOwnerDobj dobj = null;
        String getChallanOwnerDetailsSQL = "SELECT regn_no, vh_class,vch_off_cd,vch_state_cd,"
                + "owner_name,chasi_no FROM  " + TableList.VT_CHALLAN_OWNER + "   WHERE appl_no=?";
        try {
            tmgr = new TransactionManager("Challan Owner Details");
            pstmt = tmgr.prepareStatement(getChallanOwnerDetailsSQL);

            pstmt.setString(1, applicationNo);

            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new ChallanOwnerDobj();
                dobj.setChasiNo(rs.getString("chasi_no"));
                dobj.setOwnerName(rs.getString("owner_name"));
                dobj.setRtoFrom(rs.getString("vch_off_cd"));
                dobj.setStateFrom(rs.getString("vch_state_cd"));
                dobj.setVehicleNo(rs.getString("regn_no"));
                dobj.setVhClassCd(rs.getString("vh_class"));
            }
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
        return dobj;

    }

    public List<ChallanFeeDobj> getChallanFeeDetails(String applicationNo, String accusedCategory) throws VahanException {

        TransactionManager tmgr = null;
        RowSet rs = null;
        boolean toUseAccusedCatg = true;
        String totalFee = "";
        List<ChallanFeeDobj> challanFeeList = null;

        if (!"O".equalsIgnoreCase(accusedCategory) && !("C").equalsIgnoreCase(accusedCategory) && !("D").equalsIgnoreCase(accusedCategory)) {
            toUseAccusedCatg = false;
        } else {
            toUseAccusedCatg = true;
        }

        String getChallanOwnerTaxDetailsSQL = "  SELECT fees.pur_cd,fees.adv_amt,fees.adv_rcpt_no,fees.cmpd_amt,fees.settlement_amt"
                + " ,ownerr.regn_no FROM  " + TableList.VT_CHALLAN_AMT + " fees"
                + " INNER JOIN  " + TableList.VT_CHALLAN_OWNER + "  ownerr ON fees.appl_no= ownerr.appl_no"
                + " WHERE ownerr.appl_no=?";

        if (toUseAccusedCatg) {

            getChallanOwnerTaxDetailsSQL = getChallanOwnerTaxDetailsSQL + "  AND accused_catg =?";
        }
        String whereIam = "ChallanDAO.getChallanFeeDetails";
        PreparedStatement pstmt = null;
        try {
            tmgr = new TransactionManager(whereIam);
            pstmt = tmgr.prepareStatement(getChallanOwnerTaxDetailsSQL);

            pstmt.setString(1, applicationNo);
            if (toUseAccusedCatg) {
                pstmt.setString(2, accusedCategory);
            }

            rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                if (challanFeeList == null) {
                    challanFeeList = new ArrayList<ChallanFeeDobj>();
                }

                ChallanFeeDobj dobj = new ChallanFeeDobj();

                dobj.setVehicleNo(rs.getString("regn_no"));
                dobj.setPurposeCd(rs.getString("pur_cd"));
                dobj.setAcFee(rs.getString("adv_amt"));
                dobj.setcFee(rs.getString("cmpd_amt"));
                dobj.setsFee(rs.getString("settlement_amt"));
                dobj.setRecpNo(rs.getString("adv_rcpt_no"));

                challanFeeList.add(dobj);
            }
            rs.beforeFirst();

            if (rs.next()) {
                totalFee = rs.getString("cmpd_amt");
            }
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
        return challanFeeList;
    }

    public DrivingLicenseDobj getDrivingLicenseDetails(String applicationNo) throws VahanException {
        DrivingLicenseDobj dobj = null;
        String whereIam = "ChallanDAO.getChallanOwnerTaxDetails";
        TransactionManager tmgr = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String getDrivingLicenseDetailsSQL = "SELECT * FROM  " + TableList.VT_CHALLAN_ACCUSED + "   WHERE appl_no=? ";
        try {
            tmgr = new TransactionManager(whereIam);
            pstmt = tmgr.prepareStatement(getDrivingLicenseDetailsSQL);

            pstmt.setString(1, applicationNo);

            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new DrivingLicenseDobj();

                dobj.setAcctCatg(rs.getString("accused_catg"));
                dobj.setAccussedName(rs.getString("accused_name"));
                dobj.setDlNo(rs.getString("dl_no"));
                dobj.setStateCd(rs.getString("state_cd"));
                dobj.setRtoCd(rs.getString("off_cd"));
            }
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
        return dobj;
    }

    public List<ChallanOwnerTaxDobj> getChallanOwnerTaxDetails(String applicationNo) throws VahanException {

        String getChallanOwnerTaxDetailsSQL = "SELECT * , (TAX + PENALTY + INTEREST) TOTAL_AMOUNT  from   (SELECT "
                + " PUR_CD, TAX_FROM , TAX_UPTO,"
                + "  COALESCE(chal_tax,0) TAX,"
                + "  COALESCE(chal_penalty,0)  PENALTY,"
                + "  COALESCE(chal_interest,0)  INTEREST,"
                + " STATE_CD,off_cd,cmpd_rcpt_dt,cmpd_rcpt_no "
                + " FROM  " + TableList.VT_CHALLAN_TAX + "   WHERE appl_no=?) DATA ";
        String whereIam = "ChallanDAO.getChallanOwnerTaxDetails";
        TransactionManager tmgr = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        List<ChallanOwnerTaxDobj> dobjList = new ArrayList<ChallanOwnerTaxDobj>();
        try {
            tmgr = new TransactionManager(whereIam);
            pstmt = tmgr.prepareStatement(getChallanOwnerTaxDetailsSQL);
            pstmt.setString(1, applicationNo);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                ChallanOwnerTaxDobj dobj = new ChallanOwnerTaxDobj();
                dobj.setTotalAmount(rs.getString("TOTAL_AMOUNT"));
                dobj.setTotalTax(rs.getString("TAX"));
                dobj.setPurCd(rs.getString("PUR_CD"));
                dobj.setTotalPenalty(rs.getString("PENALTY"));
                dobj.setTotalInterest(rs.getString("INTEREST"));
                dobj.setTaxFrom(getDateFormatted(rs.getTimestamp("TAX_FROM")));
                dobj.setTaxUpto(getDateFormatted(rs.getTimestamp("TAX_UPTO")));
                dobj.setStateCd(rs.getString("STATE_CD"));
                dobj.setRtoCd(rs.getString("off_cd"));
                dobj.setRecpNo(rs.getString("cmpd_rcpt_no"));
                dobj.setcRecpDt(getDateFormatted(rs.getTimestamp("cmpd_rcpt_dt")));

                dobjList.add(dobj);
            }
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
        return dobjList;
    }

    public boolean isFeeCollectionAllowedAgainstAccusedCategory(String applicationNo, String accusedAcatg) throws VahanException {
        boolean isFeeAllowed = true;
        String isFeeAllowedSQL = "SELECT * FROM  " + TableList.VT_CHALLAN_AMT + "   WHERE cmpd_rcpt_dt IS NOT NULL "
                + "AND cmpd_rcpt_no IS NOT NULL AND appl_no=? ";
        String whereIam = "ChallanDAO.isFeeCollectionAllowedAgainstAccusedCategory`";

        TransactionManager tmgr = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            tmgr = new TransactionManager(whereIam);
            pstmt = tmgr.prepareStatement(isFeeAllowedSQL);

            pstmt.setString(1, applicationNo);

            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isFeeAllowed = false;
            }
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
        return isFeeAllowed;
    }

    public boolean saveChallanFee(ChallanFeeDobj dobjFee, List<ChallanOwnerTaxDobj> taxDobjList, Status_dobj statusDobj, ChallanOwnerDobj challanOwnerDobj) throws VahanException {

        boolean isSaved = false;
        String whereIam = "ChallanDAO.saveChallanFee";
        TransactionManager tmgr = null;
        ResultSet rs = null;
        String officerId = dobjFee.getDealCd();
        PreparedStatement ps = null;
        Timestamp currentTimeStamp = null;
        List<ChallanOwnerTaxDobj> taxObjList = dobjFee.getDobjTax();

        String sql = null;

        try {
            String permanentRCPTNO = "";
            tmgr = new TransactionManager(whereIam);
            if (dobjFee.getVehicleNo() != null && (!"0".equalsIgnoreCase(dobjFee.getsFee()) || !(dobjFee.getDobjTax().isEmpty()))) {
                permanentRCPTNO = Receipt_Master_Impl.generateNewRcptNo(Util.getSelectedSeat().getOff_cd(), tmgr);
                if (CommonUtils.isNullOrBlank(permanentRCPTNO)) {
                    throw new VahanException("Unable to generate Receipt Number for " + officerId + ". Please contact Admninistrator");
                }
                String getCurrentTimeSQL = "SELECT CURRENT_TIMESTAMP";
                ps = tmgr.prepareStatement(getCurrentTimeSQL);

                rs = ps.executeQuery();
                if (rs.next()) {
                    currentTimeStamp = rs.getTimestamp(1);
                }
                ps = null;
                rs = null;
                String sqlChallanReferTOCourt = "select * from  " + TableList.VT_CHALLAN_REFER_TO_COURT + " where appl_no=? and court_paid_amount>0 and (court_rcpt_no is not null or court_rcpt_no!='') ";
                ps = tmgr.prepareStatement(sqlChallanReferTOCourt);
                ps.setString(1, dobjFee.getApplicationNo());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (!rs.next()) {


                    String challanFeeSQL = "UPDATE  " + TableList.VT_CHALLAN_AMT + "   SET  cmpd_rcpt_no=? ,cmpd_rcpt_dt=? "
                            + " WHERE appl_no=?  ";
                    ps = tmgr.prepareStatement(challanFeeSQL);
                    ps.setString(1, permanentRCPTNO);
                    ps.setTimestamp(2, currentTimeStamp);
                    ps.setString(3, dobjFee.getApplicationNo());
                    ps.executeUpdate();

                    String vtFeeSQl = "INSERT INTO " + TableList.VT_FEE + " (regn_no, payment_mode, fees, fine,  rcpt_no, rcpt_dt, pur_cd,  collected_by, state_cd, off_cd)"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

                    ps = tmgr.prepareStatement(vtFeeSQl);
                    ps.setString(1, dobjFee.getVehicleNo());
                    ps.setString(2, dobjFee.getCmPaymentMode());
                    ps.setInt(3, Integer.parseInt(dobjFee.getsFee()));
                    ps.setInt(4, 0);
                    ps.setString(5, permanentRCPTNO);
                    ps.setTimestamp(6, currentTimeStamp);
                    ps.setInt(7, TableConstants.VM_MAST_ENFORCEMENT_FEE_TAX);
                    ps.setString(8, Util.getEmpCode());
                    ps.setString(9, Util.getUserStateCode());
                    ps.setInt(10, Util.getSelectedSeat().getOff_cd());
                    ps.execute();

                    // }
                }

                String sql1tax = "Select * from " + TableList.VT_CHALLAN_TAX + " where appl_no=? and state_cd=? ";
                ps = tmgr.prepareStatement(sql1tax);
                ps.setString(1, dobjFee.getApplicationNo());
                ps.setString(2, Util.getUserStateCode());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {

                    String challanTaxSQLCase2 = "UPDATE  " + TableList.VT_CHALLAN_TAX + "    SET cmpd_rcpt_no=?,cmpd_rcpt_dt=? where appl_no=? ";

                    ps = tmgr.prepareStatement(challanTaxSQLCase2);
                    ps.setString(1, permanentRCPTNO);
                    ps.setTimestamp(2, currentTimeStamp);
                    ps.setString(3, dobjFee.getApplicationNo());
                    ps.execute();

//                    String vtTaxBreakUpSQL = "INSERT INTO " + TableList.VT_TAX_BREAKUP + " (rcpt_no, sr_no, tax_from, tax_upto, pur_cd,"
//                            + " prv_adjustment, tax,exempted, rebate, surcharge, penalty, interest, tax1, tax2, state_cd, off_cd)"
//                            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//                    ps = tmgr.prepareStatement(vtTaxBreakUpSQL);
//                    for (ChallanOwnerTaxDobj dobj_tax : taxObjList) {
//                        ps.setString(1, permanentRCPTNO);
//                        ps.setInt(2, 0);
//                        ps.setTimestamp(3, getDateToTimesTampDDMMMYYYY(dobj_tax.getTaxFrom()));
//                        ps.setTimestamp(4, getDateToTimesTampDDMMMYYYY(dobj_tax.getTaxUpto()));
//                        ps.setInt(5, Integer.parseInt(dobj_tax.getPurCd()));
//                        ps.setInt(6, 0);
//                        ps.setDouble(7, Integer.parseInt(dobj_tax.getTotalTax()));
//                        ps.setInt(8, 0);
//                        ps.setDouble(9, 0);
//                        ps.setDouble(10, 0);
//                        ps.setDouble(11, Integer.parseInt(dobj_tax.getTotalPenalty()));
//                        ps.setDouble(12, Integer.parseInt(dobj_tax.getTotalInterest()));
//                        ps.setDouble(13, 0);
//                        ps.setDouble(14, 0);
//                        ps.setString(15, Util.getUserStateCode());
//                        ps.setInt(16, Util.getSelectedSeat().getOff_cd());
//                        ps.execute();
//
//                    }
//                    String sqlVtTax = "INSERT INTO " + TableList.VT_TAX + "(\n"
//                            + "            regn_no, tax_mode, payment_mode, tax_amt, tax_fine, rcpt_no, \n"
//                            + "            rcpt_dt, tax_from, tax_upto, pur_cd, collected_by, state_cd, \n"
//                            + "            off_cd)\n"
//                            + "    VALUES (?, ?, ?, ?, ?, ?, \n"
//                            + "            ?, ?, ?, ?, ?, ?, \n"
//                            + "            ?)";
//                    ps = tmgr.prepareStatement(sqlVtTax);
//                    for (ChallanOwnerTaxDobj dobj_tax : taxObjList) {
//                        ps.setString(1, dobjFee.getVehicleNo());
//                        ps.setString(2, "Y");
//                        ps.setString(3, "C");
//                        ps.setInt(4, Integer.parseInt(dobj_tax.getTotalTax()));
//                        ps.setInt(5, Integer.parseInt(dobj_tax.getTotalPenalty()));
//                        ps.setString(6, permanentRCPTNO);
//                        ps.setTimestamp(7, currentTimeStamp);
//                        ps.setTimestamp(8, getDateToTimesTampDDMMMYYYY(dobj_tax.getTaxFrom()));
//                        ps.setTimestamp(9, getDateToTimesTampDDMMMYYYY(dobj_tax.getTaxUpto()));
//                        ps.setInt(10, Integer.parseInt(dobj_tax.getPurCd()));
//                        ps.setString(11, Util.getEmpCode());
//                        ps.setString(12, Util.getUserStateCode());
//                        ps.setInt(13, Util.getSelectedSeat().getOff_cd());
//                        ps.execute();
//                    }
                }
                sql = "INSERT INTO " + TableList.VP_APPL_RCPT_MAPPING + " "
                        + "(state_cd, off_cd, appl_no, rcpt_no, owner_name, chasi_no, vh_class,excess_amt, cash_amt, remarks)"
                        + "  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getSelectedSeat().getOff_cd());
                ps.setString(3, dobjFee.getApplicationNo());
                ps.setString(4, permanentRCPTNO);
                ps.setString(5, challanOwnerDobj.getOwnerName());
                ps.setString(6, challanOwnerDobj.getChasiNo());
                ps.setInt(7, Integer.parseInt(challanOwnerDobj.getVhClassCd()));
                ps.setInt(8, 0);
                ps.setInt(9, Integer.parseInt(dobjFee.getsFee()));
                ps.setString(10, null);
                ps.execute();
            }

            String applicationNo = dobjFee.getApplicationNo();
            if (applicationNo != null && !"".equals(applicationNo)) {
                ServerUtil.webServiceForNextStage(statusDobj, TableConstants.FORWARD, null, applicationNo,
                        TableConstants.VM_ROLE_ENFORCEMENT_FEE_TAX, statusDobj.getPur_cd(), null, tmgr);
                ServerUtil.fileFlow(tmgr, statusDobj);
            }

            isSaved = true;
            dobjFee.setRecpNo(permanentRCPTNO);

            tmgr.commit();
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
        return isSaved;
    }

    public RowSet getRowSetForChallanFees(String Book_no, String accused, int challanNO) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("Fee Receipt Printing");
            String sql_book_details = "SELECT chal_fee.veh_no,chal_fee.chal_no, chal_fee.acctg, chal_fee.cfee, chal_fee.c_recp_no,"
                    + "chal_fee.c_recp_dt, chal_fee.court_recp_no, chal_fee.recp_no, chal_fee.recp_dt , chal_fee.op_dt "
                    + "FROM  " + TableList.VT_CHALLAN_AMT + "  chal_fee where chal_fee.book_no =? AND chal_fee.chal_no=? AND chal_fee.acctg=?";
            pstmt = tmgr.prepareStatement(sql_book_details);
            pstmt.setString(1, Book_no);
            pstmt.setInt(2, challanNO);
            pstmt.setString(3, accused);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                rs.beforeFirst();
                return rs;
            } else {
                rs = null;
            }
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
        return rs;
    }

    public List getRtoByState(String stateCd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        ArrayList rtoList = new ArrayList();
        SelectItem item = new SelectItem("-1", "Select RTO");
        rtoList.add(item);
        if (CommonUtils.isNullOrBlank(stateCd)) {
            return rtoList;
        }

        try {
            tmgr = new TransactionManager("getRtoByState");
            String sql_accused = "select off_cd,off_name FROM " + TableList.TM_OFFICE + "   WHERE state_cd =? order by off_cd";
            ps = tmgr.prepareStatement(sql_accused);
            ps.setString(1, stateCd);
            RowSet rowSet = tmgr.fetchDetachedRowSet();
            while (rowSet.next()) {
                item = new SelectItem(rowSet.getString("off_cd"), rowSet.getString("off_name"));
                rtoList.add(item);
            }
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
        return rtoList;
    }

    public static String getDateFormatted(java.sql.Timestamp data) {
        if (data == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        java.util.Date date = new java.util.Date(data.getTime());
        String finalString = "";
        finalString = sdf.format(date);
        return finalString;
    }

    public static long getFormattedLongValue(String value) {
        long parsedValue = 0l;
        if (CommonUtils.isNullOrBlank(value)) {
            return parsedValue;
        }
        try {
            parsedValue = Long.parseLong(value);
        } catch (NumberFormatException ne) {
        }
        return parsedValue;
    }

    public static Timestamp getDateToTimesTampDDMMMYYYY(String strDt) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MMM-yyyy");
        Timestamp timeStampDate;
        Date date = null;
        try {
            date = sdf1.parse(strDt);
        } catch (ParseException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        timeStampDate = new Timestamp(date.getTime());
        return timeStampDate;
    }
}
