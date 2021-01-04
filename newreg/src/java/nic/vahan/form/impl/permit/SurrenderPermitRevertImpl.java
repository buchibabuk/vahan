/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.dobj.permit.SurrenderPermitRevertDobj;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author ankur
 */
public class SurrenderPermitRevertImpl {

    private static final Logger LOGGER = Logger.getLogger(SurrenderPermitRevertImpl.class);
    static RowSet rs = null;

    // revert 
    public static boolean saveSurenderPermitRevertDetails(String regn_no, SurrenderPermitRevertDobj surrdobj, PermitDetailDobj pmt_dobj) {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String pmt_no = "";
        String sql = null;
        boolean flag = false;
        try {
            tmgr = new TransactionManager("saveSurenderPermitRevertDetails");
            sql = "select pmt_type,pmt_no from " + TableList.VH_PERMIT + " where  state_cd = ? AND off_cd = ? AND pmt_status=? AND regn_no = ?  order by moved_on desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            if (pmt_dobj != null && pmt_dobj.getOff_cd() != 0) {
                ps.setInt(2, pmt_dobj.getOff_cd());
            } else {
                ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            }
            ps.setString(3, "SUR");
            ps.setString(4, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                pmt_no = rs.getString("pmt_no");
                sql = " INSERT INTO " + TableList.VT_PERMIT
                        + "   SELECT state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, "
                        + "   valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, "
                        + "   service_type, goods_to_carry, jorney_purpose, parking, replace_date, "
                        + "   remarks, op_dt"
                        + "  FROM " + TableList.VH_PERMIT
                        + " where regn_no = ? AND state_cd = ? AND off_cd = ? order by moved_on desc limit 1";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regn_no);
                ps.setString(2, Util.getUserStateCode());
                if (pmt_dobj != null && pmt_dobj.getOff_cd() != 0) {
                    ps.setInt(3, pmt_dobj.getOff_cd());
                } else {
                    ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                }
                ps.executeUpdate();
                if (((Integer.valueOf(surrdobj.getPaction_code()) == TableConstants.VM_PMT_CANCELATION_PUR_CD)
                        || (Integer.valueOf(surrdobj.getPaction_code()) == TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD)
                        || (Integer.valueOf(surrdobj.getPaction_code()) == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD)
                        || (Integer.valueOf(surrdobj.getPaction_code()) == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD)
                        || (Integer.valueOf(surrdobj.getPaction_code()) == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD))
                        && (rs.getInt("pmt_type") == Integer.valueOf(TableConstants.AITP)
                        || rs.getInt("pmt_type") == Integer.valueOf(TableConstants.NATIONAL_PERMIT))) {
                    sql = " INSERT INTO " + TableList.VT_PERMIT_HOME_AUTH
                            + "  SELECT regn_no,pmt_no,auth_no,auth_fr,auth_to,op_dt,pur_cd "
                            + "  FROM " + TableList.VH_PERMIT_HOME_AUTH
                            + " where regn_no = ? AND pmt_no = ? order by moved_on desc limit 1";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, regn_no);
                    ps.setString(2, pmt_no);
                    ps.executeUpdate();
                }
            }
            if (!((Integer.parseInt(surrdobj.getPaction_code()) == TableConstants.VM_PMT_CANCELATION_PUR_CD) || (Integer.parseInt(surrdobj.getPaction_code()) == TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD))) {

                sql = "INSERT INTO " + TableList.VH_PERMIT_TRANSACTION + "(\n"
                        + "            state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, rcpt_no, \n"
                        + "            trans_pur_cd, remarks, order_no, order_dt, order_by, new_regn_no, \n"
                        + "            user_cd, op_dt, moved_on, moved_by)\n"
                        + " SELECT     state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, rcpt_no, \n"
                        + "            trans_pur_cd, remarks, order_no, order_dt, order_by, new_regn_no, \n"
                        + "            user_cd, op_dt,CURRENT_TIMESTAMP,?\n"
                        + "  FROM " + TableList.VT_PERMIT_TRANSACTION + " WHERE regn_no = ? AND state_cd = ? AND off_cd = ? order by op_dt limit 1;";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, regn_no);
                ps.setString(3, Util.getUserStateCode());
                ps.setInt(4, Util.getSelectedSeat().getOff_cd());
                ps.executeUpdate();

                sql = "DELETE FROM " + TableList.VT_PERMIT_TRANSACTION + " WHERE regn_no = ? AND state_cd = ? AND off_cd = ?;";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regn_no);
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                ps.executeUpdate();
            }
            moveVhInstrumentAitpToVtInstrumentAitp(regn_no, tmgr);
            insertSurrenderRevertUserInfo(surrdobj.getAppl_no(), tmgr);
            flag = true;
            tmgr.commit();

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
        return flag;
    }

    public static void moveVhInstrumentAitpToVtInstrumentAitp(String regnNo, TransactionManager tmgr) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;
        String appl_no = null;
        sql = " Select appl_no FROM " + TableList.VH_INSTRUMENT_AITP
                + " WHERE regn_no = ? and state_cd=? and status=? order by moved_on desc limit 1";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, regnNo);
        ps.setString(2, Util.getUserStateCode());
        ps.setString(3, "SUR");
        rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            appl_no = rs.getString("appl_no");
            sql = " INSERT INTO " + TableList.VT_INSTRUMENT_AITP + " select state_cd,off_cd,appl_no, sr_no, regn_no, pay_state_cd,instrument_type,"
                    + " instrument_no, instrument_dt, instrument_amt,bank_code,branch_name,payable_to,recieved_dt,op_dt,pur_cd FROM " + TableList.VH_INSTRUMENT_AITP
                    + " WHERE regn_no = ? and state_cd=? and appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setString(3, appl_no);
            ps.executeUpdate();
        }
    }

    public static void insertSurrenderRevertUserInfo(String appl_no, TransactionManager tmgr) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        sql = "INSERT INTO permit.va_surrender_revert_user_info(\n"
                + "            state_cd, off_cd, appl_no, user_cd, op_dt)\n"
                + "    VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getUserStateCode());
        ps.setInt(2, Util.getSelectedSeat().getOff_cd());
        ps.setString(3, appl_no);
        ps.setString(4, Util.getEmpCode());
        ps.executeUpdate();
    }

    //rev
    public static SurrenderPermitRevertDobj getToatalNoOfPermitList(String regn_no) {
        String Query;
        PreparedStatement ps;
        SurrenderPermitRevertDobj dobj = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getToatalNoOfPermitList");
            Query = "(SELECT a.PMT_NO, a.regn_no,a.trans_pur_cd, a.op_dt as move_date,b.descr,a.appl_no from " + TableList.VT_PERMIT_TRANSACTION + " a"
                    + " left join " + TableList.TM_PURPOSE_MAST + " b on a.trans_pur_cd = b.pur_cd"
                    + " where regn_no = ? AND a.state_cd = ? AND a.off_cd = ? AND a.trans_pur_cd IN (?,?,?,?,?,?,?,?,?)"
                    + " AND regn_no NOT IN (SELECT regn_no from PERMIT.VT_PERMIT P where p.state_cd = a.state_cd AND p.off_cd = a.off_cd and p.regn_no = ?)"
                    + " UNION "
                    + " SELECT a.PMT_NO, a.regn_no,a.trans_pur_cd, a.moved_on as move_date,b.descr,a.appl_no from " + TableList.VHA_PERMIT_TRANSACTION + " a"
                    + " left join " + TableList.TM_PURPOSE_MAST + " b on a.trans_pur_cd = b.pur_cd"
                    + " where regn_no = ? AND a.state_cd = ? AND a.off_cd = ? AND a.trans_pur_cd IN (?,?)"
                    + " AND regn_no NOT IN (SELECT regn_no from PERMIT.VT_PERMIT P where p.state_cd = a.state_cd AND p.off_cd = a.off_cd and p.regn_no = ?))"
                    + " order by move_date DESC limit 1";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, regn_no);
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.setInt(i++, TableConstants.VM_PMT_CA_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_REPLACE_VEH_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_TRANSFER_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_TEMP_SUR_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_SUSPENSION_PUR_CD);
            ps.setString(i++, regn_no);
            ps.setString(i++, regn_no);
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.setInt(i++, TableConstants.VM_PMT_CANCELATION_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD);
            ps.setString(i++, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new SurrenderPermitRevertDobj();
                dobj.setPmt_no(rs.getString("PMT_NO"));
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setPaction_code(rs.getString("TRANS_PUR_CD"));
                dobj.setPaction(rs.getString("descr"));
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setOp_dt(rs.getDate("move_date"));
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
        return dobj;
    }

    //Get Permit deatils For Surender Permit Revert
    public static PermitDetailDobj getPermitdetailsForCancellation(String regn_no) {
        PermitDetailDobj dobj = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("Permit Details Implimentation");

            String sql = " ((SELECT a.state_cd, a.off_cd, a.appl_no, a.pmt_no, a.regn_no, a.issue_dt, a.valid_from,"
                    + " a.valid_upto, a.rcpt_no, a.pur_cd, b.trans_pur_cd, a.pmt_type, a.pmt_catg, a.domain_cd, a.region_covered, "
                    + " a.service_type, a.goods_to_carry, a.jorney_purpose, a.parking, a.replace_date, "
                    + " a.remarks, a.op_dt, b.op_dt as move_date, pmtct.descr"
                    + " FROM " + TableList.VH_PERMIT + " a "
                    + " left join " + TableList.VT_PERMIT_TRANSACTION + " b on b.regn_no = a.regn_no AND b.pmt_no=a.pmt_no"
                    + " left join " + TableList.VM_PERMIT_CATG + " pmtct on pmtct.code = a.pmt_catg AND"
                    + " pmtct.permit_type = a.pmt_type AND pmtct.state_cd = a.state_cd"
                    + " where a.regn_no=? AND a.pmt_status = ? AND a.state_cd = ? AND b.off_cd = ? AND b.trans_pur_cd IN (?,?,?,?,?,?,?,?,?))"
                    + " UNION "
                    + " (SELECT a.state_cd, a.off_cd, a.appl_no, a.pmt_no, a.regn_no, a.issue_dt, a.valid_from,"
                    + " a.valid_upto, a.rcpt_no, a.pur_cd, b.trans_pur_cd, a.pmt_type, a.pmt_catg, a.domain_cd, a.region_covered, "
                    + " a.service_type, a.goods_to_carry, a.jorney_purpose, a.parking, a.replace_date, "
                    + " a.remarks, a.op_dt, b.moved_on as move_date, pmtct.descr"
                    + " FROM " + TableList.VH_PERMIT + " a "
                    + " left join " + TableList.VHA_PERMIT_TRANSACTION + " b on b.regn_no = a.regn_no AND b.pmt_no=a.pmt_no"
                    + " left join " + TableList.VM_PERMIT_CATG + " pmtct on pmtct.code = a.pmt_catg AND"
                    + " pmtct.permit_type = a.pmt_type AND pmtct.state_cd = a.state_cd"
                    + " where a.regn_no=? AND a.pmt_status = ? AND a.state_cd = ? AND b.off_cd = ? AND b.trans_pur_cd IN (?,?)))"
                    + "order by move_date DESC LIMIT 1";

            PreparedStatement ps = tmgr.prepareStatement(sql);
            int j = 1;
            ps.setString(j++, regn_no);
            ps.setString(j++, "SUR");
            ps.setString(j++, Util.getUserStateCode());
            ps.setInt(j++, Util.getSelectedSeat().getOff_cd());
            ps.setInt(j++, TableConstants.VM_PMT_CA_PUR_CD);
            ps.setInt(j++, TableConstants.VM_PMT_REPLACE_VEH_PUR_CD);
            ps.setInt(j++, TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD);
            ps.setInt(j++, TableConstants.VM_PMT_TRANSFER_PUR_CD);
            ps.setInt(j++, TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD);
            ps.setInt(j++, TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD);
            ps.setInt(j++, TableConstants.VM_PMT_SUSPENSION_PUR_CD);
            ps.setInt(j++, TableConstants.VM_PMT_TEMP_SUR_PUR_CD);
            ps.setInt(j++, TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD);
            ps.setString(j++, regn_no);
            ps.setString(j++, "SUR");
            ps.setString(j++, Util.getUserStateCode());
            ps.setInt(j++, Util.getSelectedSeat().getOff_cd());
            ps.setInt(j++, TableConstants.VM_PMT_CANCELATION_PUR_CD);
            ps.setInt(j++, TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD);

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new PermitDetailDobj();
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setIssue_dt(rs.getDate("issue_dt"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setPmt_no(rs.getString("pmt_no"));
                dobj.setValid_from(rs.getDate("valid_from"));
                dobj.setValid_upto(rs.getDate("valid_upto"));
                dobj.setRcpt_no(rs.getString("rcpt_no"));
                dobj.setPur_cd(rs.getInt("pur_cd"));
                dobj.setPmt_type(rs.getInt("pmt_type"));
                dobj.setPmt_catg(rs.getInt("pmt_catg"));
                dobj.setPmt_catg_desc(rs.getString("descr"));
                dobj.setService_type(rs.getInt("service_type"));
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
        return dobj;
    }
}
