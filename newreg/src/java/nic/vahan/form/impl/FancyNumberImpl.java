/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.FancyNumberDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author nic5912
 */
public class FancyNumberImpl {

    private static final Logger LOGGER = Logger.getLogger(FancyNumberImpl.class);

    public static List<Map.Entry<String, String>> getReservedNos() {
        Map<String, String> mp = new HashMap();
        TransactionManager tmgr = null;
        try {
            String sql = "Select regn_no,count(*) numbers from va_fancy_register where status is null group by regn_no ";
            tmgr = new TransactionManager("getReservedNos");
            tmgr.prepareStatement(sql);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                mp.put(rs.getString("regn_no"), rs.getString("numbers"));

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

        return new ArrayList<Map.Entry<String, String>>(mp.entrySet());
    }

    public static boolean isNumberBooked(String regnNo) {
        boolean booked = false;
        TransactionManager tmgr = null;
        try {
            String sql = "Select * from  va_fancy_register where regn_no=? and status is null ";
            tmgr = new TransactionManager("isNumberBooked");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                booked = true;

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

        return booked;
    }

    public static Integer getAmount(String regn_no) throws VahanException {
        int amount = 0;
        TransactionManager tmgr = null;
        boolean flg = false;
        try {
            String sql = "Select booking_fee from vm_fancy_mast where fancy_number = ? ";
            tmgr = new TransactionManager("getAmount");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no.substring(6));
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                amount = rs.getInt("booking_fee");
                flg = true;
            }


            if (!flg) {
                sql = "Select booking_fee from vm_fancy_mast where fancy_number= 'NONE'";
                rs = tmgr.fetchDetachedRowSet_No_release();

                if (rs.next()) {
                    amount = rs.getInt("booking_fee");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    throw new VahanException(e.getMessage());
                }
            }
        }

        return amount;

    }

    public static void verifyFancy_RegnNo(String regn_no, int offCd) throws VahanException, Exception {

        int num_part;
        String series_part;
        TransactionManager tmg = null;
        try {
            series_part = regn_no.substring(4, 6);
            num_part = Integer.parseInt(regn_no.substring(6));
            String sql = "select * from vt_fancy_range where "
                    + " current_date >= date_from and current_date <=date_upto and  "
                    + num_part + " >= regn_no_from and " + num_part + " <= regn_no_upto "
                    + " and reg_series=? and off_cd=?";
            tmg = new TransactionManager("verifyFancy_RegnNo");
            PreparedStatement ps = tmg.prepareStatement(sql);
            ps.setString(1, series_part);
            ps.setInt(2, offCd);

            RowSet rs = tmg.fetchDetachedRowSet_No_release();
            if (!rs.next()) {
                throw new VahanException("Registration Number " + regn_no + " is not available ");
            }

            sql = "select * from  " + TableList.VT_OWNER + " where  regn_no=? ";

            ps = tmg.prepareStatement(sql);
            ps.setString(1, regn_no);

            rs = tmg.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                throw new VahanException("Registration Number " + regn_no + " is already registered ");
            }

            //if number is already allocated
            sql = "Select * from  va_fancy_register where regn_no=? and status in ("
                    + TableConstants.FANCY_ACCEPTED + ","
                    + TableConstants.FANCY_PAID + ")";

            ps = tmg.prepareStatement(sql);
            ps.setString(1, regn_no);

            rs = tmg.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                throw new VahanException("Registration Number " + regn_no + " is already allocated ");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } catch (NumberFormatException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Numeric part of Registration is incorrect");
        } finally {
            if (tmg != null) {
                tmg.release();
            }

        }
    }

    public static FancyNumberDobj get_fancy_appl_no(String appl_no) throws VahanException {

        FancyNumberDobj dobj = null;
        TransactionManager tmg = null;
        try {
            String sql = "Select * from  va_owner_temp where appl_no=? ";
            tmg = new TransactionManager("get_fancy_appl_no");
            PreparedStatement ps = tmg.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rsTemp = tmg.fetchDetachedRowSet_No_release();

            sql = "Select * from   " + TableList.VA_OWNER + " where appl_no=? ";
            ps = tmg.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rsNew = tmg.fetchDetachedRowSet_No_release();

            if (rsTemp.next()) {
                dobj = new FancyNumberDobj();
                dobj.setChasi_no(rsTemp.getString("chasi_no"));
                dobj.setOwner_name(rsTemp.getString("owner_name"));
                dobj.setC_add1(rsTemp.getString("c_add1"));
                dobj.setC_add2(rsTemp.getString("c_add2"));
                dobj.setC_district(rsTemp.getInt("c_district"));
                dobj.setC_taluk(rsTemp.getInt("c_taluk"));
                dobj.setC_village(rsTemp.getInt("c_village"));
                dobj.setC_pincode(rsTemp.getString("c_pincode"));

            } else if (rsNew.next()) {

                dobj = new FancyNumberDobj();
                dobj.setChasi_no(rsNew.getString("chasi_no"));
                dobj.setOwner_name(rsNew.getString("owner_name"));
                dobj.setC_add1(rsNew.getString("c_add1"));
                dobj.setC_add2(rsNew.getString("c_add2"));
                dobj.setC_district(rsNew.getInt("c_district"));
                dobj.setC_taluk(rsNew.getInt("c_taluk"));
                dobj.setC_village(rsNew.getInt("c_village"));
                dobj.setC_pincode(rsNew.getString("c_pincode"));

            } else {
                throw new VahanException("Invalid Application No");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } finally {
            if (tmg != null) {
                try {
                    tmg.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    throw new VahanException(e.getMessage());
                }
            }
        }

        return dobj;
    }

    public static FancyNumberDobj get_fancy_bal(String rcpt) throws VahanException {

        FancyNumberDobj dobj = null;
        TransactionManager tmgr = null;
        try {
            String sql = "Select * from  va_fancy_register where recp_no=? and status=? ";
            tmgr = new TransactionManager("get_fancy_rcpt_no");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, rcpt);
            ps.setString(2, TableConstants.FANCY_ACCEPTED);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new FancyNumberDobj();
                dobj.setRegn_appl_no(rs.getString("regn_appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setC_add1(rs.getString("c_add1"));
                dobj.setC_add2(rs.getString("c_add2"));
                dobj.setC_district(rs.getInt("c_district"));
                dobj.setC_taluk(rs.getInt("c_taluk"));
                dobj.setC_village(rs.getInt("c_village"));
                dobj.setC_pincode(rs.getString("c_pincode"));
                dobj.setBal_amt(rs.getInt("bal_amount"));

            } else {
                throw new VahanException("Invalid Receipt No");
            }
        } catch (SQLException e) {
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

        return dobj;
    }

    public static synchronized String saveFancyDetail(FancyNumberDobj dobj, String state_cd, int off_cd) throws VahanException, Exception {
        String rcpt = null;
        TransactionManager tmg = null;
        PreparedStatement ps;
        String sql;
        try {

            tmg = new TransactionManager("saveFancyDetail");
            //Generate a receipt No
            rcpt = Receipt_Master_Impl.generateNewRcptNo(off_cd, tmg);

            sql = "Insert into vt_fee (regn_no,payment_mode,fees,fine,rcpt_no,rcpt_dt,pur_cd,collected_by,state_cd,off_cd) "
                    + " values(?,?,?,?,?,current_timestamp,?,?,?,?)";
            ps = tmg.prepareStatement(sql);
            ps.setString(1, dobj.getRegn_no());
            ps.setString(2, TableConstants.TM_PAYMENT_MODE_CASH);
            ps.setInt(3, dobj.getReserve_amt());
            ps.setInt(4, 0);
            ps.setString(5, rcpt);
            ps.setInt(6, TableConstants.VM_TRANSACTION_MAST_CHOICE_NO);
            ps.setString(7, Util.getEmpCode());
            ps.setString(8, state_cd);
            ps.setInt(9, off_cd);
            ps.executeUpdate();


            //insert it into va_fancy_register
            sql = "INSERT INTO va_fancy_register (recp_no ,  recp_dt, regn_appl_no ,  regn_no ,chasi_no,"
                    + "  owner_name , c_add1 ,  c_add2 ,  c_village, c_taluk ,  c_district ,"
                    + "  c_pincode ,  reserve_amt ,   state_cd ,  rto_cd ) "
                    + " values(?,current_timestamp,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            ps = tmg.prepareStatement(sql);
            ps.setString(1, rcpt);
            ps.setString(2, dobj.getRegn_appl_no());
            ps.setString(3, dobj.getRegn_no());
            ps.setString(4, dobj.getChasi_no());
            ps.setString(5, dobj.getOwner_name());
            ps.setString(6, dobj.getC_add1());
            ps.setString(7, dobj.getC_add2());
            ps.setInt(8, dobj.getC_village());
            ps.setInt(9, dobj.getC_taluk());
            ps.setInt(10, dobj.getC_district());
            ps.setString(11, dobj.getC_pincode());
            ps.setInt(12, dobj.getReserve_amt());
            ps.setString(13, state_cd);
            ps.setInt(14, off_cd);

            ps.executeUpdate();

            //vt_appl_rcpt_mapping

            sql = "insert into vt_appl_rcpt_mapping values(?,?,?,?)";
            ps = tmg.prepareStatement(sql);
            ps.setString(1, dobj.getRegn_appl_no());
            ps.setString(2, rcpt);
            ps.setString(3, state_cd);
            ps.setInt(4, off_cd);

            ps.executeUpdate();
            tmg.commit();


        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } finally {
            if (tmg != null) {
                try {
                    tmg.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    throw new VahanException(e.getMessage());
                }
            }
        }

        return rcpt;
    }

    public static synchronized String saveBalanceFancyAmount(FancyNumberDobj dobj, String state_cd, int off_cd) throws VahanException, Exception {
        String rcpt = null;
        TransactionManager tmg = null;
        PreparedStatement ps;
        String sql;
        try {

            tmg = new TransactionManager("saveBalanceFancyAmount");
            //Generate a receipt No
            rcpt = Receipt_Master_Impl.generateNewRcptNo(off_cd, tmg);

            //vt_fee
            sql = "Insert into vt_fee (regn_no,payment_mode,fees,fine,rcpt_no,rcpt_dt,pur_cd,collected_by,state_cd,off_cd) "
                    + " values(?,?,?,?,?,current_timestamp,?,?,?,?)";
            ps = tmg.prepareStatement(sql);
            ps.setString(1, dobj.getRegn_no());
            ps.setString(2, TableConstants.TM_PAYMENT_MODE_CASH);
            ps.setInt(3, dobj.getReserve_amt());
            ps.setInt(4, 0);
            ps.setString(5, rcpt);
            ps.setInt(6, TableConstants.VM_TRANSACTION_MAST_CHOICE_NO);
            ps.setString(7, Util.getEmpCode());
            ps.setString(8, state_cd);
            ps.setInt(9, off_cd);
            ps.executeUpdate();


            //insert into vt_fee_balance

            sql = "Insert into vt_fee_balance (appl_no,regn_no,pur_cd,bal_fees,bal_fine,"
                    + " entered_by,entered_on,rcpt_no,state_cd,off_cd) "
                    + " values(?,?,?,?,?,?,current_timestamp,?,?,?)";
            ps = tmg.prepareStatement(sql);
            ps.setString(1, dobj.getRegn_appl_no());
            ps.setString(2, dobj.getRegn_no());
            ps.setInt(3, TableConstants.VM_TRANSACTION_MAST_CHOICE_NO);
            ps.setInt(4, dobj.getReserve_amt());
            ps.setInt(5, 0);
            ps.setString(6, Util.getEmpCode());
            ps.setString(7, rcpt);
            ps.setString(8, state_cd);
            ps.setInt(9, off_cd);

            ps.executeUpdate();

            //insert it in va_fancy_register 
            sql = "Update va_fancy_register set status=? where recp_no=? ";
            ps = tmg.prepareStatement(sql);
            ps.setString(1, TableConstants.FANCY_PAID);
            ps.setString(2, dobj.getBal_rcptno());

            ps.executeUpdate();


            //vt_appl_rcpt_mapping

            sql = "insert into vt_appl_rcpt_mapping values(?,?,?,?)";
            ps = tmg.prepareStatement(sql);
            ps.setString(1, dobj.getRegn_appl_no());
            ps.setString(2, rcpt);
            ps.setString(3, state_cd);
            ps.setInt(4, off_cd);

            ps.executeUpdate();

            tmg.commit();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } finally {
            if (tmg != null) {
                try {
                    tmg.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    throw new VahanException(e.getMessage());
                }
            }
        }
        return rcpt;
    }
}
