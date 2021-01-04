/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;

/**
 *
 * @author acer
 */
public class RcToFinancerPrintImpl {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(RcToFinancerPrintImpl.class);

    public void printHistory(OwnerDetailsDobj dobj, String appl_no, String emp_cd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = "INSERT INTO " + TableList.VHA_FRC_PRINT + "("
                + " appl_no, regn_no, op_dt, state_cd, off_cd, moved_by, moved_on)"
                + " SELECT appl_no, regn_no, op_dt, state_cd, off_cd, ?, CURRENT_TIMESTAMP"
                + " FROM " + TableList.VA_FRC_PRINT + " where regn_no = ? and appl_no = ?"
                + " and state_cd = ? and off_cd = ? and CURRENT_DATE = op_dt::date";
        try {
            tmgr = new TransactionManager("printHistory()");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, emp_cd);
            ps.setString(2, dobj.getRegn_no());
            ps.setString(3, appl_no);
            ps.setString(4, dobj.getState_cd());
            ps.setInt(5, dobj.getOff_cd());
            int i = ps.executeUpdate();
            if (i > 0) {
                tmgr.commit();
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem occured while print");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
    }

    public OwnerDetailsDobj initializePrintDetail(String appl_no, String regn_no,
            String state_cd, int off_cd, String empCd) throws VahanException {
        TransactionManager tmgr = null;
        OwnerDetailsDobj dobj = null;
        try {
            tmgr = new TransactionManager("RcToFinancerPrintImpl.initializePrintDetail()");
            int i = initializeFRC(tmgr, appl_no, regn_no, state_cd, off_cd);
            if (i > 0) {
                dobj = gettingPrintDTLS(tmgr, appl_no, regn_no, state_cd, off_cd);
            }
            tmgr.commit();
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Record May Not Be Initialize to Print, Please revert back this application for print");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return dobj;
    }

    public OwnerDetailsDobj gettingRequiredPrintDtls(String appl_no, String regn_no,
            String state_cd, int off_cd) throws VahanException {
        TransactionManager tmgr = null;
        OwnerDetailsDobj dobj = null;
        try {
            tmgr = new TransactionManager("RcToFinancerPrintImpl.gettingRequiredPrint()");
//            boolean approved = gettingApprovalDtls(tmgr, appl_no, regn_no);
//            if (approved) {
//                throw new VahanException("The Application is already approved, Therefore print is not available");
//            } else {
            if (isCurrentDatePrint(tmgr, appl_no, regn_no)) {
                dobj = gettingPrintDTLS(tmgr, appl_no, regn_no, state_cd, off_cd);
            } else {
                throw new VahanException("Combination of Application No and Registration No. may not correct or Date of printing has passed");
            }
//            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Record May Not Be Initialize to Print");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return dobj;
    }

    private int initializeFRC(TransactionManager tmgr, String appl_no, String regn_no,
            String state_cd, int off_cd) throws SQLException {
        PreparedStatement ps = null;
        Timestamp op_dt = new Timestamp(new Date().getTime());
        String sql = "SELECT appl_no, regn_no, op_dt, state_cd, off_cd"
                + " FROM " + TableList.VA_FRC_PRINT + " where regn_no = ? and appl_no = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, regn_no);
        ps.setString(2, appl_no);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            op_dt = rs.getTimestamp("op_dt");
            sql = "UPDATE " + TableList.VA_FRC_PRINT
                    + " SET op_dt=CURRENT_TIMESTAMP"
                    + " WHERE  appl_no = ? and regn_no= ?";
            ps = tmgr.prepareStatement(sql);
        } else {
            sql = "INSERT INTO " + TableList.VA_FRC_PRINT + "("
                    + " appl_no, regn_no, op_dt, state_cd, off_cd)"
                    + " VALUES (?, ?, ?, ?, ?);";
            ps = tmgr.prepareStatement(sql);
            ps.setTimestamp(3, op_dt);
            ps.setString(4, state_cd);
            ps.setInt(5, off_cd);
        }
        ps.setString(1, appl_no);
        ps.setString(2, regn_no);
        return ps.executeUpdate();
    }

    private OwnerDetailsDobj gettingPrintDTLS(TransactionManager tmgr, String appl_no, String regn_no,
            String state_cd, int off_cd) throws SQLException {
        String sql = "select owner_name, toff.off_name as owner_off, toffFin.off_name as fin_off, tso.descr as owner_state, COALESCE(v_hyp.fncr_name,v_hpt.fncr_name) as fncr_name, "
                + " regexp_replace(COALESCE(v_hyp.fncr_add1,v_hpt.fncr_add1)|| ', ' || COALESCE(v_hyp.fncr_add2,v_hpt.fncr_add2)|| ', ' "
                + " || COALESCE(v_hyp.fncr_add3,v_hpt.fncr_add3)|| ', ' ||  COALESCE(tdh.descr,'') || ', ' || tsh.descr || ', ' "
                + " || COALESCE(COALESCE(v_hyp.fncr_pincode::text,v_hpt.fncr_pincode::text),''), '(, ){2,}', ', ')  as address  "
                + " from " + TableList.VT_OWNER + " vo "
                + " left outer join " + TableList.VT_HYPTH + " v_hyp on vo.regn_no = v_hyp.regn_no and vo.state_cd=v_hyp.state_cd and vo.off_cd = v_hyp.off_cd"
                + " left outer join " + TableList.VH_HPT + " v_hpt on vo.regn_no = v_hpt.regn_no  and vo.state_cd=v_hpt.state_cd and vo.off_cd = v_hpt.off_cd and v_hpt.appl_no = ?"
                + " left outer join " + TableList.TM_DISTRICT + " tdh on tdh.state_cd = COALESCE(v_hyp.fncr_state,v_hpt.fncr_state) and tdh.dist_cd = COALESCE(v_hyp.fncr_district,v_hpt.fncr_district) "
                + " inner join " + TableList.TM_OFFICE + " toff on toff.state_cd = vo.state_cd and toff.off_cd = vo.off_cd  "
                + " inner join " + TableList.TM_OFFICE + " toffFin on toffFin.state_cd = COALESCE(v_hyp.state_cd,v_hpt.state_cd) and toffFin.off_cd = COALESCE(v_hyp.off_cd,v_hpt.off_cd) "
                + " inner join " + TableList.TM_STATE + " tsh on tsh.state_code =  COALESCE(v_hyp.fncr_state,v_hpt.fncr_state)  "
                + " inner join " + TableList.TM_STATE + " tso on tso.state_code =  vo.state_cd  "
                + " where vo.regn_no=? and vo.state_cd=? and vo.off_cd=? order by COALESCE(v_hyp.sr_no::text,v_hpt.sr_no::text)::numeric desc LIMIT 1";
        PreparedStatement ps = null;
        OwnerDetailsDobj dobj = null;
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, appl_no);
        ps.setString(2, regn_no);
        ps.setString(3, state_cd);
        ps.setInt(4, off_cd);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            dobj = new OwnerDetailsDobj();
            dobj.setRegn_no(regn_no);
            dobj.setOwner_name(rs.getString("owner_name"));
            dobj.setOff_name(rs.getString("owner_off"));
            dobj.setState_name(rs.getString("owner_state"));
            dobj.setP_add2(rs.getString("fin_off"));
            dobj.setF_name(rs.getString("fncr_name"));
            dobj.setP_add1(rs.getString("address"));
            //dobj.setOff_name(ServerUtil.getOfficeName(off_cd, state_cd));
        }
        return dobj;

    }

    private boolean gettingApprovalDtls(TransactionManager tmgr, String appl_no, String regn_no) throws SQLException {
        PreparedStatement ps = null;
        boolean found = false;
        String sql = "select appl_no from " + TableList.VA_DETAILS
                + " where pur_cd=? and regn_no = ? and appl_no = ? and regexp_replace(entry_status, '\\s+$', '') = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setInt(1, TableConstants.VM_TRANSACTION_MAST_FRESH_RC);
        ps.setString(2, regn_no);
        ps.setString(3, appl_no);
        ps.setString(4, TableConstants.STATUS_APPROVED);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        found = rs.next();
        return found;
    }

    private boolean isCurrentDatePrint(TransactionManager tmgr, String appl_no, String regn_no) throws SQLException {
        PreparedStatement ps = null;
        boolean found = false;
        String sql = "SELECT op_dt FROM " + TableList.VA_FRC_PRINT
                + " where regn_no = ? and appl_no = ? and CURRENT_DATE = op_dt::date";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, regn_no);
        ps.setString(2, appl_no);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        found = rs.next();
        return found;
    }
}
