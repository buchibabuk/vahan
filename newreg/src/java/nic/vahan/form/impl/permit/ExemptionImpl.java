/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.ExemptionDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

/**
 *
 * @author hcl
 */
public class ExemptionImpl {

    private static final Logger LOGGER = Logger.getLogger(ExemptionImpl.class);

    public String saveDetailsInVa(ExemptionDobj exemDobj) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String Query;
        String applNo = null;
        try {
            tmgr = new TransactionManager("saveDetailsInVa");
            applNo = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
            Query = "INSERT INTO " + TableList.VA_EXEMPTION + "(\n"
                    + "            state_cd, off_cd, appl_no, regn_no, pmt_no,pur_cd, exem_from_date, exem_to_date, \n"
                    + "            exem_amount, fine_to_be_taken, order_no, order_by, order_dt, \n"
                    + "            exem_reason, op_dt)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, ?, ?, ?, \n"
                    + "            ?, current_timestamp)";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.setString(i++, applNo);
            ps.setString(i++, exemDobj.getRegn_no().toUpperCase());
            ps.setString(i++, exemDobj.getPmt_no().toUpperCase());
            ps.setInt(i++, exemDobj.getPur_cd());
            ps.setDate(i++, new java.sql.Date(exemDobj.getExem_from_date().getTime()));
            ps.setDate(i++, new java.sql.Date(exemDobj.getExem_to_date().getTime()));
            ps.setInt(i++, Integer.valueOf(exemDobj.getExem_amount()));
            ps.setInt(i++, Integer.valueOf(exemDobj.getFine_to_be_taken()));
            ps.setString(i++, exemDobj.getOrder_no().toUpperCase());
            ps.setString(i++, exemDobj.getOrder_by().toUpperCase());
            if (exemDobj.getOrder_dt() != null) {
                ps.setDate(i++, new java.sql.Date(exemDobj.getOrder_dt().getTime()));
            } else {
                ps.setDate(i++, null);
            }
            ps.setString(i++, exemDobj.getExem_reason().toUpperCase());
            ps.executeUpdate();

            Status_dobj status = new Status_dobj();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            String dt = sdf.format(new Date());
            status.setAppl_dt(dt);
            status.setAppl_no(applNo.toUpperCase());
            status.setPur_cd(exemDobj.getPur_cd());
            status.setFlow_slno(1);
            status.setFile_movement_slno(1);
            status.setAction_cd(CommonPermitPrintImpl.getActionCd(Util.getUserStateCode(), exemDobj.getPur_cd(), 0, 1, tmgr, 0));
            status.setEmp_cd(Long.parseLong(Util.getEmpCode()));
            status.setRegn_no(exemDobj.getRegn_no().toUpperCase());
            status.setOffice_remark("");
            status.setPublic_remark("");
            status.setStatus("N");
            status.setState_cd(Util.getUserStateCode());
            status.setOff_cd(Util.getSelectedSeat().getOff_cd());

            ServerUtil.fileFlowForNewApplication(tmgr, status);
            status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
            status.setStatus(TableConstants.STATUS_COMPLETE);
            status.setAppl_no(applNo);
            status.setOffice_remark("");
            status.setPublic_remark("");

            ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, applNo,
                    CommonPermitPrintImpl.getActionCd(Util.getUserStateCode(), exemDobj.getPur_cd(), 0, 2, tmgr, 0), exemDobj.getPur_cd(), null, tmgr);
            ServerUtil.fileFlow(tmgr, status);
            tmgr.commit();
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            applNo = null;
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
        return applNo;
    }

    public ExemptionDobj showSaveDetails(String applNo) {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String Query;
        ExemptionDobj exemDobj = null;
        try {
            tmgr = new TransactionManager("saveDetailsInVa");
            Query = "SELECT * FROM " + TableList.VA_EXEMPTION + " where appl_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                exemDobj = new ExemptionDobj();
                exemDobj.setState_cd(rs.getString("state_cd"));
                exemDobj.setOff_cd(rs.getInt("off_cd"));
                exemDobj.setAppl_no(rs.getString("appl_no"));
                exemDobj.setRegn_no(rs.getString("regn_no"));
                exemDobj.setPmt_no(rs.getString("pmt_no"));
                exemDobj.setPur_cd(rs.getInt("pur_cd"));
                exemDobj.setExem_from_date(rs.getDate("exem_from_date"));
                exemDobj.setExem_to_date(rs.getDate("exem_to_date"));
                exemDobj.setExem_amount(rs.getInt("exem_amount"));
                exemDobj.setFine_to_be_taken(rs.getInt("fine_to_be_taken"));
                exemDobj.setOrder_no(rs.getString("order_no"));
                exemDobj.setOrder_by(rs.getString("order_by"));
                exemDobj.setOrder_dt(rs.getDate("order_dt"));
                exemDobj.setExem_reason(rs.getString("exem_reason"));
            }
        } catch (Exception e) {
            exemDobj = null;
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
        return exemDobj;
    }

    public void onlySaveDetails(String applNo, ExemptionDobj exemDobj, String CompairChange) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("onlySaveDetails");
            if (!CommonUtils.isNullOrBlank(CompairChange)) {
                moveVaToVhaPmtExem(tmgr, applNo);
                updateVaPmtExem(tmgr, exemDobj, applNo);
                insertIntoVhaChangedData(tmgr, applNo, CompairChange);
                tmgr.commit();
            }
        } catch (SQLException e) {
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

    public void saveAndMoveforVarification(String applNo, ExemptionDobj exemDobj, String CompairChange, Status_dobj statusDobj) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("saveAndMoveforVarification");
            if (!CommonUtils.isNullOrBlank(CompairChange)) {
                moveVaToVhaPmtExem(tmgr, applNo);
                updateVaPmtExem(tmgr, exemDobj, applNo);
                insertIntoVhaChangedData(tmgr, applNo, CompairChange);
            }
            statusDobj = ServerUtil.webServiceForNextStage(statusDobj, tmgr);
            ServerUtil.fileFlow(tmgr, statusDobj);
            tmgr.commit();
        } catch (SQLException e) {
            throw new VahanException(e.getMessage());
        } catch (Exception e) {
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

    public void saveAndMoveforApproval(String applNo, String regnNo, ExemptionDobj exemDobj, String CompairChange, Status_dobj statusDobj) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("saveAndMoveforApproval");
            if (!CommonUtils.isNullOrBlank(CompairChange)) {
                updateVaPmtExem(tmgr, exemDobj, applNo);
                insertIntoVhaChangedData(tmgr, applNo, CompairChange);
            }
            moveVaToVhaPmtExem(tmgr, applNo);
            String Query = "Select * from " + TableList.VT_EXEMPTION + " where regn_no = ? ";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            ps.setString(1, regnNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                moveVTToVhPmtExem(tmgr, regnNo, exemDobj.getPur_cd(), false);
                deleteInVtPmtExem(tmgr, regnNo, exemDobj.getPur_cd());
            }
            moveVaToVtPmtExem(tmgr, applNo);
            deleteInVaPmtExem(tmgr, applNo);
            statusDobj.setEntry_status(TableConstants.STATUS_APPROVED);
            ServerUtil.updateApprovedStatus(tmgr, statusDobj);
            statusDobj.setStatus(TableConstants.STATUS_COMPLETE);
            ServerUtil.fileFlow(tmgr, statusDobj);
            tmgr.commit();
        } catch (SQLException e) {
            throw new VahanException(e.getMessage());
        } catch (Exception e) {
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

    public void moveVaToVhaPmtExem(TransactionManager tmgr, String applNo) throws SQLException {
        String Query = "INSERT INTO " + TableList.VHA_EXEMPTION + "(\n"
                + "            state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, exem_from_date, \n"
                + "            exem_to_date, exem_amount, fine_to_be_taken, order_no, order_by, \n"
                + "            order_dt, exem_reason, op_dt, moved_on, moved_by)\n"
                + "    SELECT  state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, exem_from_date, \n"
                + "            exem_to_date, exem_amount, fine_to_be_taken, order_no, order_by, \n"
                + "            order_dt, exem_reason, op_dt,current_timestamp,?\n"
                + "       FROM " + TableList.VA_EXEMPTION + " where appl_no = ? ";
        PreparedStatement ps = tmgr.prepareStatement(Query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, applNo);
        ps.executeUpdate();
    }

    public void deleteVaPmtExem(TransactionManager tmgr, String applNo) throws SQLException {
        String Query = "DELETE FROM " + TableList.VA_EXEMPTION + " WHERE APPL_NO = ?";
        PreparedStatement ps = tmgr.prepareStatement(Query);
        ps.setString(1, applNo);
        ps.executeUpdate();
    }

    public void updateVaPmtExem(TransactionManager tmgr, ExemptionDobj exemDobj, String applNo) throws SQLException {
        String Query = "UPDATE " + TableList.VA_EXEMPTION + "\n"
                + "   SET exem_from_date=?, exem_to_date=?, exem_amount=?, fine_to_be_taken=?, \n"
                + "       order_no=?, order_by=?, order_dt=?, exem_reason=?\n"
                + " WHERE appl_no=?";
        PreparedStatement ps = tmgr.prepareStatement(Query);
        int i = 1;
        ps.setDate(i++, new java.sql.Date(exemDobj.getExem_from_date().getTime()));
        ps.setDate(i++, new java.sql.Date(exemDobj.getExem_to_date().getTime()));
        ps.setInt(i++, Integer.valueOf(exemDobj.getExem_amount()));
        ps.setInt(i++, Integer.valueOf(exemDobj.getFine_to_be_taken()));
        ps.setString(i++, exemDobj.getOrder_no().toUpperCase());
        ps.setString(i++, exemDobj.getOrder_by().toUpperCase());
        if (exemDobj.getOrder_dt() != null) {
            ps.setDate(i++, new java.sql.Date(exemDobj.getOrder_dt().getTime()));
        } else {
            ps.setDate(i++, null);
        }
        ps.setString(i++, exemDobj.getExem_reason().toUpperCase());
        ps.setString(i++, applNo);
        ps.executeUpdate();
    }

    public void moveVaToVtPmtExem(TransactionManager tmgr, String applNo) throws SQLException {
        String Query = "INSERT INTO " + TableList.VT_EXEMPTION + "(\n"
                + "            state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, exem_from_date, \n"
                + "            exem_to_date, exem_amount, fine_to_be_taken, order_no, order_by, \n"
                + "            order_dt, exem_reason, op_dt)\n"
                + "     SELECT state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, exem_from_date, \n"
                + "            exem_to_date, exem_amount, fine_to_be_taken, order_no, order_by, \n"
                + "            order_dt, exem_reason, current_timestamp\n"
                + "       FROM " + TableList.VA_EXEMPTION + " where appl_no = ?";
        PreparedStatement ps = tmgr.prepareStatement(Query);
        ps.setString(1, applNo);
        ps.executeUpdate();
    }

    public void moveVTToVhPmtExem(TransactionManager tmgr, String regnNo, int purCd, boolean used) throws SQLException {
        String Query = "INSERT INTO " + TableList.VH_EXEMPTION + "(\n"
                + "            state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, exem_from_date, \n"
                + "            exem_to_date, exem_amount, fine_to_be_taken, order_no, order_by, \n"
                + "            order_dt, exem_reason, op_dt, exem_used, moved_on, moved_by)\n"
                + "     SELECT state_cd, off_cd, appl_no, regn_no, pmt_no, pur_cd, exem_from_date, \n"
                + "            exem_to_date, exem_amount, fine_to_be_taken, order_no, order_by, \n"
                + "            order_dt, exem_reason, op_dt,?,current_timestamp,?\n"
                + "       FROM " + TableList.VT_EXEMPTION + " where regn_no = ? AND pur_cd = ? order by op_dt DESC";
        PreparedStatement ps = tmgr.prepareStatement(Query);
        ps.setBoolean(1, used);
        ps.setString(2, Util.getEmpCode());
        ps.setString(3, regnNo);
        ps.setInt(4, purCd);
        ps.executeUpdate();
    }

    public void deleteInVaPmtExem(TransactionManager tmgr, String applNo) throws SQLException {
        String Query = "DELETE FROM " + TableList.VA_EXEMPTION + " WHERE appl_no = ? ";
        PreparedStatement ps = tmgr.prepareStatement(Query);
        ps.setString(1, applNo);
        ps.executeUpdate();
    }

    public void deleteInVtPmtExem(TransactionManager tmgr, String regnNo, int purCd) throws SQLException {
        String Query = "DELETE FROM " + TableList.VT_EXEMPTION + " WHERE regn_no = ? AND pur_cd = ?";
        PreparedStatement ps = tmgr.prepareStatement(Query);
        ps.setString(1, regnNo);
        ps.setInt(2, purCd);
        ps.executeUpdate();
    }

    public String getFitnessDetails(String regnNo) {
        TransactionManager tmgr = null;
        String fitValidUpto = null;
        try {
            tmgr = new TransactionManager("saveAndMoveforApproval");
            String Query = "Select to_char(fit_valid_to,'DD-MON-YYYY') as fitValidUpto from " + TableList.VT_FITNESS + " where regn_no = ? order by regn_no DESC limit 1";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            ps.setString(1, regnNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                fitValidUpto = rs.getString(1);
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
        return fitValidUpto;
    }

    public String getParkingDetails(String regnNo, String state_cd) {
        TransactionManager tmgr = null;
        String Query = "", parkValidUpto = "";
        PreparedStatement ps;
        RowSet rs;
        int i = 0;
        try {
            tmgr = new TransactionManager("saveAndMoveforApproval");
            Query = "SELECT to_char(exem_to_date,'DD-MON-YYYY') as parkValidUpto from vt_exemption \n"
                    + "where state_cd = ? and regn_no = ? and pur_cd = ? order by op_dt DESC limit 1";
            ps = tmgr.prepareStatement(Query);
            i = 1;
            ps.setString(i++, state_cd);
            ps.setString(i++, regnNo);
            ps.setInt(i++, TableConstants.VM_PARK_EXEMPTION_PUR_CD);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                parkValidUpto = rs.getString(1);
            }
            if (CommonUtils.isNullOrBlank(parkValidUpto)) {
                Query = "SELECT to_char(a.fee_upto,'DD-MON-YYYY') as parkValidUpto from " + TableList.VT_FEE_BREAKUP + " a  \n"
                        + "inner join " + TableList.VT_FEE + " b on b.regn_no = ? AND a.state_cd = b.state_cd AND a.pur_cd = b.pur_cd \n"
                        + "where a.rcpt_no = b.rcpt_no and a.pur_cd = ? and a.state_cd = ? order by b.rcpt_dt DESC limit 1";
                ps = tmgr.prepareStatement(Query);
                i = 1;
                ps.setString(i++, regnNo);
                ps.setInt(i++, TableConstants.VM_PARKING_FEE_PUR_CD);
                ps.setString(i++, state_cd);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    parkValidUpto = rs.getString(1);
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
        return parkValidUpto;
    }

    public ExemptionDobj getExemptionDetailsBasedOnPurpose(String regnNo, int purCd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String Query;
        ExemptionDobj exemDobj = null;
        try {
            tmgr = new TransactionManager("getExemptionDetailsBasedOnPurpose");
            Query = "SELECT * FROM " + TableList.VT_EXEMPTION + " where regn_no = ? and pur_cd=? order by op_dt desc limit 1";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regnNo);
            ps.setInt(2, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                exemDobj = new ExemptionDobj();
                exemDobj.setState_cd(rs.getString("state_cd"));
                exemDobj.setOff_cd(rs.getInt("off_cd"));
                exemDobj.setAppl_no(rs.getString("appl_no"));
                exemDobj.setRegn_no(rs.getString("regn_no"));
                exemDobj.setPmt_no(rs.getString("pmt_no"));
                exemDobj.setPur_cd(rs.getInt("pur_cd"));
                exemDobj.setExem_from_date(rs.getDate("exem_from_date"));
                exemDobj.setExem_to_date(rs.getDate("exem_to_date"));
                exemDobj.setExem_amount(rs.getInt("exem_amount"));
                exemDobj.setFine_to_be_taken(rs.getInt("fine_to_be_taken"));
                exemDobj.setOrder_no(rs.getString("order_no"));
                exemDobj.setOrder_by(rs.getString("order_by"));
                exemDobj.setOrder_dt(rs.getDate("order_dt"));
                exemDobj.setExem_reason(rs.getString("exem_reason"));
            }
        } catch (Exception e) {
            exemDobj = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in Getting Exemption Details, Please Contact to System Administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return exemDobj;
    }//end of getExemptionDetailsBasedOnPurpose
}
