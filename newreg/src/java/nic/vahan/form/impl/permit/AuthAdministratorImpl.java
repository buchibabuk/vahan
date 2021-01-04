/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitHomeAuthDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author hcl
 */
public class AuthAdministratorImpl {

    private static final Logger LOGGER = Logger.getLogger(AuthAdministratorImpl.class);

    public boolean save(PermitHomeAuthDobj dobj) throws VahanException {
        boolean flag = false;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("AuthAdministratorImpl Save");
            PermitHomeAuthImpl impl = new PermitHomeAuthImpl();
            impl.vt_permit_home_To_VH_permit_home(tmgr, dobj.getRegnNo().toUpperCase());
            impl.deleteIntoHomeAuth(tmgr, null, TableList.VT_PERMIT_HOME_AUTH, dobj.getRegnNo().toUpperCase());
            insertVtFeeExempted(tmgr, dobj);
            insert_vt_permit_home_auth(tmgr, dobj);
            tmgr.commit();
            flag = true;
        } catch (SQLException e) {
            flag = false;
            throw new VahanException(e.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                flag = false;
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public void insert_vt_permit_home_auth(TransactionManager tmgr, PermitHomeAuthDobj dobj) throws SQLException {
        PreparedStatement ps = null;
        int i = 1;
        String Query = "INSERT INTO " + TableList.VT_PERMIT_HOME_AUTH + "(\n"
                + "            regn_no, pmt_no, auth_no, auth_fr, auth_to, op_dt, pur_cd)\n"
                + "    VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";
        ps = tmgr.prepareStatement(Query);
        ps.setString(i++, dobj.getRegnNo().toUpperCase());
        ps.setString(i++, dobj.getPmtNo().toUpperCase());
        ps.setString(i++, dobj.getAuthNo().toUpperCase());
        ps.setDate(i++, new java.sql.Date(dobj.getAuthFrom().getTime()));
        ps.setDate(i++, new java.sql.Date(dobj.getAuthUpto().getTime()));
        ps.setInt(i++, dobj.getPurCd());
        ps.executeUpdate();
    }

    public void insertVtFeeExempted(TransactionManager tmgr, PermitHomeAuthDobj dobj) throws SQLException {
        PreparedStatement ps = null;
        int i = 1;
        String Query = "INSERT INTO " + TableList.VT_FEE_EXEMPTED
                + " (state_cd, off_cd, appl_no, rcpt_no, pur_cd, flow_slno, file_movement_slno, "
                + "  action_cd, status, office_remark, public_remark, file_movement_type, emp_cd, op_dt)"
                + "  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";
        ps = tmgr.prepareStatement(Query);
        ps.setString(i++, Util.getUserStateCode());
        ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
        ps.setString(i++, dobj.getRcptNo().toUpperCase());
        ps.setString(i++, dobj.getRcptNo().toUpperCase());
        ps.setInt(i++, dobj.getPurCd());
        ps.setInt(i++, 0);
        ps.setInt(i++, 0);
        ps.setInt(i++, 0);
        ps.setString(i++, "N");
        ps.setString(i++, "Administrator Entry");
        ps.setString(i++, "Administrator Entry");
        ps.setString(i++, "F");
        ps.setLong(i++, Long.parseLong(Util.getEmpCode()));
        ps.executeUpdate();
    }

    public Date checkRegnNoWithRcptNo(String regnNo, String RcptNo) throws VahanException {
        PreparedStatement ps;
        TransactionManagerReadOnly tmgr = null;
        Date feeRcptDate = null;
        int i = 0;
        RowSet rs;
        try {
            tmgr = new TransactionManagerReadOnly("checkRegnNoWithRcptNo");
            String Query = "SELECT * from vt_fee_exempted where state_cd = ? AND off_cd = ? AND rcpt_no = ? AND pur_cd in (?,?)";
            ps = tmgr.prepareStatement(Query);
            i = 1;
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.setString(i++, RcptNo);
            ps.setInt(i++, TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                throw new VahanException("Receipt No : " + RcptNo + " already used through administrator form");
            } else {
                Query = "SELECT rcpt_dt :: date from vt_fee where state_cd = ? AND off_cd = ? AND regn_no = ? AND rcpt_no = ? AND pur_cd in (?,?)";
                ps = tmgr.prepareStatement(Query);
                i = 1;
                ps.setString(i++, Util.getUserStateCode());
                ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
                ps.setString(i++, regnNo);
                ps.setString(i++, RcptNo);
                ps.setInt(i++, TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD);
                ps.setInt(i++, TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    feeRcptDate = rs.getDate("rcpt_dt"); 
                } else {
                    throw new VahanException("Data not found in Fee table. Please contact the system Administrator");
                }
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                feeRcptDate = null;
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return feeRcptDate;
    }

    public String authValidity(String regn_no) throws VahanException {
        String prvAuthValidity = "", Query = "";
        RowSet rs;
        PreparedStatement ps;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("AuthAdministratorImpl authValidity");
            Query = "SELECT 'Previous Authorization Validity: ' || to_char(auth_fr,'DD-MON-YYYY') || ' to ' || to_char(auth_to,'DD-MON-YYYY') from " + TableList.VT_PERMIT_HOME_AUTH + "  where regn_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                prvAuthValidity = rs.getString(1);
            } else {
                Query = "SELECT 'Previous Authorization Validity: ' || to_char(auth_fr,'DD-MON-YYYY') || ' to ' || to_char(auth_to,'DD-MON-YYYY') from " + TableList.VH_PERMIT_HOME_AUTH + "  where regn_no = ? order by op_dt DESC limit 1";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, regn_no);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    prvAuthValidity = rs.getString(1);
                }
            }
        } catch (SQLException e) {
            prvAuthValidity = "";
            throw new VahanException(e.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                prvAuthValidity = "";
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return prvAuthValidity;
    }

    public boolean deleteHomeAuth(String regn_no) throws VahanException {
        boolean flag = false;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("deleteHomeAuth");
            PermitHomeAuthImpl impl = new PermitHomeAuthImpl();
            impl.vt_permit_home_To_VH_permit_home(tmgr, regn_no);
            flag = true;
            tmgr.commit();
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
        return flag;
    }

    public boolean isRegnNoExistInVhPermitOrVtPermit(String regnNo) throws VahanException {
        boolean found = false;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("isRegnNoExistInVhPermitOrVtPermit");
            String sql = " SELECT * FROM " + TableList.VT_PERMIT + " where state_cd=? and regn_no=? and pmt_type in (" + TableConstants.AITP + "," + TableConstants.NATIONAL_PERMIT + ")";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, regnNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                found = true;
            }
            if (!found) {
                sql = " SELECT * FROM " + TableList.VH_PERMIT + " where state_cd=? and regn_no=? and pmt_status=? order by moved_on DESC limit 1";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setString(2, regnNo);
                ps.setString(3, "SUR");
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    sql = " SELECT * FROM " + TableList.VT_PERMIT_TRANSACTION + " where state_cd=? and regn_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getUserStateCode());
                    ps.setString(2, regnNo);                   
                    RowSet rs1 = tmgr.fetchDetachedRowSet();
                    if (rs1.next()) {
                        found = true;
                    }
                }
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
        return found;
    }
}
