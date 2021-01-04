package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.NdcDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

public class NdcImpl {

    private static final Logger LOGGER = Logger.getLogger(NdcImpl.class);

    public static void insertUpdateNDC(TransactionManager tmgr, NdcDobj ndc_dobj, String userCd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "SELECT * FROM " + TableList.VA_NDC + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, ndc_dobj.getAppl_no());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) { //if any record exists then update otherwise insert it
                insertIntoNDCHistory(tmgr, ndc_dobj.getAppl_no(), userCd);
                updateNDC(tmgr, ndc_dobj);
            } else {
                insertIntoNDC(tmgr, ndc_dobj);
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " insertUpdateNDC " + e.getStackTrace()[0]);
            throw new VahanException(" Error occured while upating Details");
        }
    }

    public static void updateNDC(TransactionManager tmgr, NdcDobj ndc_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = " update " + TableList.VA_NDC + " set  "
                    + " pmt_no=?, state_to=?, off_to=?,ndc_no=?, ncrb_no=?,"
                    + " dl_no=?, badge_no=?, reason_cd=?, remark=?, status=?, op_dt=current_timestamp where appl_no=?";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, ndc_dobj.getPmt_no());
            ps.setString(2, ndc_dobj.getState_to());
            ps.setInt(3, ndc_dobj.getOff_to());
            ps.setString(4, ndc_dobj.getNdc_no());
            ps.setString(5, ndc_dobj.getNcrb_no());
            ps.setString(6, ndc_dobj.getDl_no());
            ps.setString(7, ndc_dobj.getBadge_no());
            ps.setInt(8, 0);
            ps.setString(9, ndc_dobj.getRemark());
            ps.setString(10, "");
            ps.setString(11, ndc_dobj.getAppl_no());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in updating NDC details.");
        }
    }

    public static void insertIntoNDCHistory(TransactionManager tmgr, String appl_no, String userCd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "INSERT INTO " + TableList.VHA_NDC + "(moved_on, moved_by, state_cd, off_cd, appl_no, regn_no, pmt_no, \n"
                    + " state_to, off_to, ndc_no, ndc_dt, ncrb_no, dl_no, badge_no, reason_cd, \n"
                    + " remark, status, op_dt) \n"
                    + " SELECT  current_timestamp as moved_on,? as moved_by,state_cd, off_cd, appl_no, regn_no, pmt_no, state_to, off_to, ndc_no, ndc_dt, ncrb_no, dl_no, badge_no, reason_cd, remark, status, op_dt FROM " + TableList.VA_NDC + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, userCd);
            ps.setString(2, appl_no);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        }
    }

    public static void insertIntoNDC(TransactionManager tmgr, NdcDobj ndc_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "INSERT INTO " + TableList.VA_NDC + "(state_cd, off_cd, appl_no, regn_no, pmt_no, state_to, off_to, \n"
                    + "ndc_no, ndc_dt, ncrb_no, dl_no, badge_no, reason_cd, remark, \n"
                    + "status, op_dt) "
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, ndc_dobj.getState_cd());
            ps.setInt(2, ndc_dobj.getOff_cd());
            ps.setString(3, ndc_dobj.getAppl_no());
            ps.setString(4, ndc_dobj.getRegn_no());
            ps.setString(5, ndc_dobj.getPmt_no());
            ps.setString(6, ndc_dobj.getState_to());
            ps.setInt(7, ndc_dobj.getOff_to());
            ps.setString(8, ndc_dobj.getNdc_no());
            ps.setNull(9, java.sql.Types.NULL);
            ps.setString(10, ndc_dobj.getNcrb_no());
            ps.setString(11, ndc_dobj.getDl_no());
            ps.setString(12, ndc_dobj.getBadge_no());
            ps.setInt(13, 0);
            ps.setString(14, ndc_dobj.getRemark());
            ps.setString(15, "");
            ps.executeUpdate();
        } catch (SQLException se) {
            LOGGER.error(se.toString() + " " + se.getStackTrace()[0]);
            throw new VahanException("Error occured while saving data.");
        }
    }

    public static void makeChangeNDC(NdcDobj ndc_dobj, String changedata, String userCd) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("makeChangeNDC");
            insertUpdateNDC(tmgr, ndc_dobj, userCd);
            if (!CommonUtils.isNullOrBlank(changedata)) {
                ComparisonBeanImpl.updateChangedData(ndc_dobj.getAppl_no(), changedata, tmgr);
            }
            tmgr.commit();
        } catch (VahanException ve) {
            throw ve;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
    }

    public static void saveChangesAndFileMove(NdcDobj ndc_dobj, String changedata, String userCd, Status_dobj status_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("saveChangesAndFileMove");
            if (!CommonUtils.isNullOrBlank(changedata)) {
                ComparisonBeanImpl.updateChangedData(ndc_dobj.getAppl_no(), changedata, tmgr);
            }
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_ENTRY
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_VERIFICATION
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL) {

                insertUpdateNDC(tmgr, ndc_dobj, userCd);
            }
            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL && status_dobj.getStatus().equals(TableConstants.STATUS_COMPLETE)) {

                sql = "INSERT INTO " + TableList.VH_NDC + " (moved_on, moved_by, state_cd, off_cd, appl_no, regn_no, pmt_no, state_to, off_to, ndc_no, ndc_dt, ncrb_no, dl_no, badge_no, reason_cd, remark, status, op_dt) \n"
                        + "   SELECT current_timestamp,?, state_cd, off_cd, appl_no, regn_no, pmt_no, state_to, off_to, ndc_no, ndc_dt, ncrb_no, dl_no, badge_no, reason_cd, remark, status, op_dt \n"
                        + "   FROM " + TableList.VT_NDC + " where regn_no=? and state_cd = ? and off_cd = ? and ndc_dt = (Select ndc_dt from " + TableList.VT_NDC + " where regn_no=? and state_cd = ? and off_cd = ? order by ndc_dt desc limit 1)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, userCd);
                ps.setString(2, ndc_dobj.getRegn_no());
                ps.setString(3, ndc_dobj.getState_cd());
                ps.setInt(4, ndc_dobj.getOff_cd());
                ps.setString(5, ndc_dobj.getRegn_no());
                ps.setString(6, ndc_dobj.getState_cd());
                ps.setInt(7, ndc_dobj.getOff_cd());
                ps.executeUpdate();

                sql = "Delete from " + TableList.VT_NDC + " where regn_no=? and state_cd = ? and off_cd = ? and ndc_dt = (Select ndc_dt from " + TableList.VT_NDC + " where regn_no=? and state_cd = ? and off_cd = ? order by ndc_dt desc limit 1)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, ndc_dobj.getRegn_no());
                ps.setString(2, ndc_dobj.getState_cd());
                ps.setInt(3, ndc_dobj.getOff_cd());
                ps.setString(4, ndc_dobj.getRegn_no());
                ps.setString(5, ndc_dobj.getState_cd());
                ps.setInt(6, ndc_dobj.getOff_cd());
                ps.executeUpdate();

                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);

                String ndc_no;
                ndc_no = ServerUtil.getUniquePermitNo(tmgr, ndc_dobj.getState_cd(), ndc_dobj.getOff_cd(), 0, 0, "D");

                if (!CommonUtils.isNullOrBlank(ndc_no)) {

                    sql = "insert into " + TableList.VT_NDC
                            + " SELECT  state_cd, off_cd, appl_no, regn_no, pmt_no, state_to, off_to, \n"
                            + " ?, current_timestamp, ncrb_no, dl_no, badge_no, reason_cd, remark, \n"
                            + " ?, op_dt FROM " + TableList.VA_NDC + ""
                            + " where appl_no=?";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, ndc_no);
                    ps.setString(2, "0");
                    ps.setString(3, ndc_dobj.getAppl_no());
                    ps.executeUpdate();

                    sql = "INSERT INTO " + TableList.VHA_NDC + "( moved_on, moved_by, state_cd, off_cd, appl_no, regn_no, pmt_no, \n"
                            + " state_to, off_to, ndc_no, ndc_dt, ncrb_no, dl_no, badge_no, reason_cd, \n"
                            + " remark, status, op_dt) \n "
                            + " SELECT current_timestamp + interval '1 second' as moved_on,? as moved_by,state_cd, off_cd, appl_no, regn_no, pmt_no, state_to, off_to, ndc_no, ndc_dt, ncrb_no, dl_no, badge_no, reason_cd, remark, status , op_dt FROM " + TableList.VA_NDC + " WHERE appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, userCd);
                    ps.setString(2, ndc_dobj.getAppl_no());
                    ps.executeUpdate();

                    ServerUtil.deleteFromTable(tmgr, null, ndc_dobj.getAppl_no(), TableList.VA_NDC);
                }
            }
            status_dobj.setOffice_remark(status_dobj.getOffice_remark() == null ? "" : status_dobj.getOffice_remark());
            status_dobj.setPublic_remark("");
            status_dobj.setCntr_id("");
            ServerUtil.fileFlow(tmgr, status_dobj);
            tmgr.commit();
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem occured during saving NDC details.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
    }

    public NdcDobj getNDCDetails(String applNo) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        NdcDobj ndcDobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("getNDCDetails");
            sql = "select * from va_ndc where appl_no= ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                ndcDobj = new NdcDobj();
                ndcDobj.setState_cd(rs.getString("state_cd"));
                ndcDobj.setOff_cd(rs.getInt("off_cd"));
                ndcDobj.setAppl_no(rs.getString("appl_no"));
                ndcDobj.setRegn_no(rs.getString("regn_no"));
                ndcDobj.setPmt_no(rs.getString("pmt_no"));
                ndcDobj.setState_to(rs.getString("state_to"));
                ndcDobj.setOff_to(rs.getInt("off_to"));
                ndcDobj.setNdc_no(rs.getString("ndc_no"));
                ndcDobj.setNdc_dt(rs.getDate("ndc_dt"));
                ndcDobj.setNcrb_no(rs.getString("ncrb_no"));
                ndcDobj.setBadge_no(rs.getString("badge_no"));
                ndcDobj.setRemark(rs.getString("remark"));
                ndcDobj.setDl_no(rs.getString("dl_no"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem occured during fetching NDC details.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return ndcDobj;
    }
}