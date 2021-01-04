/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.hsrp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.hsrp.HSRP_dobj;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

public class HsrpEntryImpl {

    private static final Logger LOGGER = Logger.getLogger(HsrpEntryImpl.class);

    public HSRP_dobj validateRegnForHSRP(String regn_no, String stateCd, int offCd, boolean isBeforeApproval) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement psmt = null;
        RowSet rs = null;
        HSRP_dobj hsrpdobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("checkHsrpFrontNoinVtHsrp");
            String sql = "select hsrp_no_front,hsrp_no_back from  " + TableList.VT_HSRP + "  where regn_no=? ";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, regn_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                String frontNo = rs.getString("hsrp_no_front");
                String backNo = rs.getString("hsrp_no_back");
                sql = "select state_cd, off_cd, appl_no, regn_no, user_cd ,hsrp_flag, user_cd, op_dt, hsrp_no_front, hsrp_no_back, hsrp_fix_dt, hsrp_fix_amt, hsrp_amt_taken_on, \n"
                        + "       hsrp_op_dt from  " + TableList.VT_HSRP + "  where regn_no = ? and state_cd = ? and off_cd = ? ";
                psmt = tmgr.prepareStatement(sql);
                psmt.setString(1, regn_no);
                psmt.setString(2, stateCd);
                psmt.setInt(3, offCd);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    hsrpdobj = new HSRP_dobj();
                    hsrpdobj.setState_cd(stateCd);
                    hsrpdobj.setRegn_no(regn_no);
                    hsrpdobj.setOff_cd(offCd);
                    hsrpdobj.setAppl_no(rs.getString("appl_no"));
                    hsrpdobj.setHsrp_no_front(rs.getString("hsrp_no_front"));
                    hsrpdobj.setHsrp_no_back(rs.getString("hsrp_no_back"));
                    hsrpdobj.setHsrp_fix_amt(rs.getFloat("hsrp_fix_amt"));
                    hsrpdobj.setHsrp_fixed_dt(DateUtil.parseDate1(rs.getString("hsrp_fix_dt")));
                    hsrpdobj.setHsrp_amt_taken_on_dt(DateUtil.parseDate1(rs.getString("hsrp_amt_taken_on")));
                    hsrpdobj.setEmp_cd(rs.getString("user_cd"));
//                    if (!isBeforeApproval) {
//                        if (!CommonUtils.isNullOrBlank(rs.getString("appl_no"))) {
//                            this.checkRcPrintStatus(rs.getString("appl_no"), stateCd, offCd, tmgr);
//                        }
//                    }
                } else {
                    throw new VahanException("Vehicle is already HSRP Fitted. Front HSRP Number is " + frontNo + " and Back HSRP Number is :" + backNo);
                }
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (SQLException sqlex) {
            LOGGER.error(sqlex.toString() + " " + sqlex.getStackTrace()[0]);
            throw new VahanException("Problem in getting HSRP vehicle details.");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting HSRP vehicle details.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return hsrpdobj;
    }

    public boolean saveHSRPDetails(String regn_no, HSRP_dobj hsrp_dobj, int purCd, String hsrpFlag) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        boolean valid = false;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("saveHSRPDetails");
            this.checkDuplicateHSRPLaser(hsrp_dobj, tmgr, hsrpFlag);

            sql = "INSERT INTO " + TableList.VT_HSRP + "(state_cd, off_cd, appl_no, regn_no, hsrp_flag, user_cd, op_dt, \n"
                    + " hsrp_no_front, hsrp_no_back, hsrp_fix_dt, hsrp_fix_amt, hsrp_amt_taken_on, hsrp_op_dt)\n"
                    + " VALUES (?, ?, ?, ?, ?, ?, current_timestamp, ?, ?, ?, ?, ?, current_timestamp)";
            int i = 1;
            ps = tmgr.prepareStatement(sql);
            ps.setString(i++, hsrp_dobj.getState_cd());//state_cd
            ps.setInt(i++, hsrp_dobj.getOff_cd());//off_cd
            ps.setString(i++, hsrp_dobj.getAppl_no());//appl_no
            ps.setString(i++, regn_no);//regn_no
            ps.setString(i++, hsrpFlag);//hsrp_flag
            ps.setString(i++, hsrp_dobj.getEmp_cd());//user_cd
            ps.setString(i++, hsrp_dobj.getHsrp_no_front());//hsrp_no_front
            ps.setString(i++, hsrp_dobj.getHsrp_no_back());//hsrp_no_back
            ps.setDate(i++, new java.sql.Date(hsrp_dobj.getHsrp_fixed_dt().getTime()));
            ps.setFloat(i++, hsrp_dobj.getHsrp_fix_amt());//hsrp_fix_amt
            ps.setDate(i++, new java.sql.Date(hsrp_dobj.getHsrp_fixed_dt().getTime()));
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
            ServerUtil.updateHSRPStatus(tmgr, hsrp_dobj.getAppl_no(), hsrp_dobj.getState_cd(), regn_no, hsrp_dobj.getEmp_cd());
            tmgr.commit();
            valid = true;
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Saving HSRP details.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return valid;
    }

    public boolean insertIntoVH_HSRP(String regn_no, String stateCd, int offCd, TransactionManager tmgr, String empCode, String reason) throws VahanException {
        PreparedStatement psmt = null;
        boolean status = false;
        try {
            String CheckSeries = "INSERT INTO hsrp.vh_hsrp(state_cd, off_cd, appl_no, regn_no, hsrp_flag, user_cd, op_dt, \n"
                    + " hsrp_no_front, hsrp_no_back, hsrp_fix_dt, hsrp_fix_amt, hsrp_amt_taken_on, \n"
                    + " hsrp_op_dt, moved_reason, moved_on, moved_by) \n"
                    + " SELECT state_cd, off_cd, appl_no, regn_no, hsrp_flag, user_cd, op_dt, hsrp_no_front, \n"
                    + " hsrp_no_back, hsrp_fix_dt, hsrp_fix_amt, hsrp_amt_taken_on, hsrp_op_dt, ?, current_timestamp, ?  \n"
                    + " FROM hsrp.vt_hsrp where state_cd =? and off_cd= ? and regn_no= ? ";
            psmt = tmgr.prepareStatement(CheckSeries);
            if (reason.equals("UB")) {
                psmt.setString(1, "Update HSRP Details");
            } else {
                psmt.setString(1, reason);
            }
            psmt.setString(2, empCode);
            psmt.setString(3, stateCd);
            psmt.setInt(4, offCd);
            psmt.setString(5, regn_no);
            psmt.executeUpdate();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error: Getting HSRP details.");
        }
        return status;
    }

    public boolean updateHSRPVehicleDetails(HSRP_dobj hsrp_dobj, String empCode, boolean isBeforeApproval, String hsrpFlag) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement psmt = null;
        String whereiam = "updateHSRPVehicleDetails";
        boolean flag = false;
        try {
            tmgr = new TransactionManager(whereiam);
            this.checkDuplicateHSRPLaser(hsrp_dobj, tmgr, hsrpFlag);

            this.insertIntoVH_HSRP(hsrp_dobj.getRegn_no(), hsrp_dobj.getState_cd(), hsrp_dobj.getOff_cd(), tmgr, empCode, hsrpFlag);
            String checkSeries = "UPDATE " + TableList.VT_HSRP + " SET hsrp_no_front=?, hsrp_no_back=?, hsrp_fix_dt=?, hsrp_fix_amt=?, hsrp_amt_taken_on=?,"
                    + " hsrp_op_dt=current_timestamp ";
            if (!hsrpFlag.equals("UB")) {
                checkSeries += ", hsrp_flag=?";
            }
            checkSeries += " WHERE state_cd =? and off_cd= ? and regn_no= ? ";
            psmt = tmgr.prepareStatement(checkSeries);
            int i = 1;
            psmt.setString(i++, hsrp_dobj.getHsrp_no_front());//hsrp_no_front
            psmt.setString(i++, hsrp_dobj.getHsrp_no_back());//hsrp_no_back
            psmt.setDate(i++, new java.sql.Date(hsrp_dobj.getHsrp_fixed_dt().getTime()));
            psmt.setFloat(i++, hsrp_dobj.getHsrp_fix_amt());//hsrp_fix_amt
            psmt.setDate(i++, new java.sql.Date(hsrp_dobj.getHsrp_fixed_dt().getTime()));
            if (!hsrpFlag.equals("UB")) {
                psmt.setString(i++, hsrpFlag);//hsrp_flag
            }
            psmt.setString(i++, hsrp_dobj.getState_cd());
            psmt.setInt(i++, hsrp_dobj.getOff_cd());
            psmt.setString(i++, hsrp_dobj.getRegn_no());
            psmt.executeUpdate();
            tmgr.commit();
            flag = true;
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error: Updating HSRP flat file details.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public void checkDuplicateHSRPLaser(HSRP_dobj hsrp_dobj, TransactionManager tmgr, String hsrpFlag) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        ResultSet rs = null;
        try {
            String error = "";
            if (hsrpFlag != null) {
                if (hsrpFlag.equals("NB") || hsrpFlag.equals("OB") || hsrpFlag.equals("UB") || hsrpFlag.equals("DB")) {
                    sql = "select regn_no, hsrp_no_front, hsrp_no_back , "
                            + "'main' as table from hsrp.vt_hsrp "
                            + "where (hsrp_no_front IN (?,?) or hsrp_no_back IN (?,?)) and regn_no != ?"
                            + " union "
                            + " select regn_no, hsrp_no_front, hsrp_no_back , "
                            + "'history' as table from hsrp.vh_hsrp where "
                            + "(hsrp_no_front IN (?,?) or hsrp_no_back IN (?,?)) and regn_no != ?"
                            + "and moved_reason != 'Update HSRP Details'";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, hsrp_dobj.getHsrp_no_front());
                    ps.setString(2, hsrp_dobj.getHsrp_no_back());
                    ps.setString(3, hsrp_dobj.getHsrp_no_front());
                    ps.setString(4, hsrp_dobj.getHsrp_no_back());
                    ps.setString(5, hsrp_dobj.getRegn_no());
                    ps.setString(6, hsrp_dobj.getHsrp_no_front());
                    ps.setString(7, hsrp_dobj.getHsrp_no_back());
                    ps.setString(8, hsrp_dobj.getHsrp_no_front());
                    ps.setString(9, hsrp_dobj.getHsrp_no_back());
                    ps.setString(10, hsrp_dobj.getRegn_no());
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        if (rs.getString("table").equals("main")) {
                            error += ("HSRP front no." + rs.getString("hsrp_no_front") + " HSRP Back no." + rs.getString("hsrp_no_back") + " found against vehicle no. " + rs.getString("regn_no"));
                        } else {
                            error += ("HSRP front no." + rs.getString("hsrp_no_front") + " HSRP Back no." + rs.getString("hsrp_no_back") + " has been used in past against vehicle no. " + rs.getString("regn_no"));
                        }
                    }
                } //G
                else if (hsrpFlag.equals("DF")) {
                    boolean frontError = false;
                    boolean rearError = false;
                    sql = "select regn_no, hsrp_no_front, hsrp_no_back , "
                            + "'main' as table from hsrp.vt_hsrp "
                            + "where (hsrp_no_front IN (?) or hsrp_no_back IN (?)) and regn_no != ?"
                            + " union "
                            + " select regn_no, hsrp_no_front, hsrp_no_back , "
                            + "'history' as table from hsrp.vh_hsrp  "
                            + "where (hsrp_no_front IN (?,?) or hsrp_no_back IN (?,?)) and regn_no != ?"
                            + "and moved_reason != 'Update HSRP Details'";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, hsrp_dobj.getHsrp_no_front());
                    ps.setString(2, hsrp_dobj.getHsrp_no_front());
                    ps.setString(3, hsrp_dobj.getRegn_no());
                    ps.setString(4, hsrp_dobj.getHsrp_no_front());
                    ps.setString(5, hsrp_dobj.getHsrp_no_front());
                    ps.setString(6, hsrp_dobj.getRegn_no());
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        if (rs.getString("table").equals("main")) {
                            error += ("HSRP front no." + rs.getString("hsrp_no_front") + " found against vehicle no. " + rs.getString("regn_no"));
                        } else {
                            error += ("HSRP front no." + rs.getString("hsrp_no_front") + " has been used in past against vehicle no. " + rs.getString("regn_no"));
                        }
                    }
                } //H
                else if (hsrpFlag.equals("DR")) {
                    boolean frontError = false;
                    boolean rearError = false;
                    sql = "select regn_no, hsrp_no_front, hsrp_no_back , "
                            + "'main' as table from hsrp.vt_hsrp "
                            + "where (hsrp_no_front IN (?) or hsrp_no_back IN (?)) and regn_no != ?"
                            + " union "
                            + " select regn_no, hsrp_no_front, hsrp_no_back , "
                            + "'history' as table from hsrp.vh_hsrp  "
                            + "where (hsrp_no_front IN (?) or hsrp_no_back IN (?)) and regn_no != ?"
                            + "and moved_reason != 'Update HSRP Details'";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, hsrp_dobj.getHsrp_no_back());
                    ps.setString(2, hsrp_dobj.getHsrp_no_back());
                    ps.setString(3, hsrp_dobj.getRegn_no());
                    ps.setString(4, hsrp_dobj.getHsrp_no_back());
                    ps.setString(5, hsrp_dobj.getHsrp_no_back());
                    ps.setString(6, hsrp_dobj.getRegn_no());
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        if (rs.getString("table").equals("main")) {
                            error += (" HSRP Back no." + rs.getString("hsrp_no_back") + " found against vehicle no. " + rs.getString("regn_no"));
                        } else {
                            error += (" HSRP Back no." + rs.getString("hsrp_no_back") + " has been used in past against vehicle no. " + rs.getString("regn_no"));
                        }
                    }
                }
            }
            if (error.length() > 2) {
                throw new VahanException(error);
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Getting duplicate HSRP laser.");
        }
    }

    public void checkRcPrintStatus(String applNo, String state_cd, int off_cd, TransactionManagerReadOnly tmgr) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            boolean isSmartCard = ServerUtil.isSmartCard(state_cd, off_cd);
            if (isSmartCard) {
                sql = "select vehregno from " + TableList.RC_IA_TO_BE + " where rcpt_no = ?";
            } else {
                sql = "select regn_no from " + TableList.VH_RC_PRINT + " where appl_no = ?";
            }
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                throw new VahanException("Registration Certificate already printed. You cannot update HSRP Details.");
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Getting RC details.");
        }
    }

    public String getValidVender(String stateCd, int officeCd, int maker) throws VahanException {
        String message = "";
        TransactionManager tmgr = null;
        PreparedStatement psmt = null;
        String whereiam = "getValidVender";
        boolean flag = false;
        try {
            tmgr = new TransactionManager(whereiam);
            String sqlQuery = "select ven_descr from vv_hsrp_vender_config where state_cd = ? and office_List like ? and maker_code =  ?";
            psmt = tmgr.prepareStatement(sqlQuery);
            psmt.setString(1, stateCd);
            psmt.setString(2, "%," + officeCd + ",%");
            psmt.setString(3, String.valueOf(maker));

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                message = "Pending at vender :- " + rs.getString("ven_descr");
            }
            flag = true;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error: Getting vender Details.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return message;

    }

    public boolean makerPresentInHomologation(int maker) throws VahanException {
        boolean isPresent = false;
        PreparedStatement ps = null;
        String sql = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("HsrpEntryImpl.makerPresentInHomologation");
            sql = "select * from homologation.vm_maker where code = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, maker);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isPresent = true;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return isPresent;
    }

    public boolean checkTempDiffStateVehicle(String regn_no, String stateCd) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement psmt = null;
        RowSet rs = null;
        Boolean diffState = false;
        try {
            tmgr = new TransactionManagerReadOnly("checkHsrpFrontNoinVtHsrp");
            String sql = "select state_cd ,tmp_state_cd from vt_tmp_regn_dtl where regn_no = ?  and state_cd =?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, regn_no);
            psmt.setString(2, stateCd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (!rs.getString("state_cd").equalsIgnoreCase(rs.getString("tmp_state_cd"))) {
                    diffState = true;
                }
            }
        } catch (SQLException sqlex) {
            LOGGER.error(sqlex.toString() + " " + sqlex.getStackTrace()[0]);
            throw new VahanException("Problem in getting HSRP vehicle details.");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting HSRP vehicle details.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return diffState;
    }

    public boolean getHSRPAfterFinalApprovalStatus(String stateCd) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement psmt = null;
        RowSet rs = null;
        Boolean status = false;
        try {
            tmgr = new TransactionManagerReadOnly("checkHsrpFrontNoinVtHsrp");
            String sql = "select hsrp_after_final_approval from tm_configuration_hsrp where state_cd = ?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, stateCd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                status = rs.getBoolean("hsrp_after_final_approval");
            }
        } catch (SQLException sqlex) {
            LOGGER.error(sqlex.toString() + " " + sqlex.getStackTrace()[0]);
            throw new VahanException("Problem in getting HSRP vehicle details.");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting HSRP vehicle details.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return status;
    }
}
