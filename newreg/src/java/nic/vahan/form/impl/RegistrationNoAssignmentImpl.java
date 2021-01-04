/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.fancy.AdvanceRegnNo_dobj;
import static nic.vahan.form.impl.NewImpl.insertIntoVhaOwner;
import static nic.vahan.form.impl.NewImpl.updateVaOwner;
import static nic.vahan.form.impl.NewImpl.updateVaOwnerId;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.NewVehicleNo;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

/**
 *
 * @author nicsi
 */
public class RegistrationNoAssignmentImpl {

    private static Logger LOGGER = Logger.getLogger(RegistrationNoAssignmentImpl.class);

    public boolean isFancyNoAssigned(String appl_no) throws Exception {
        boolean isFancy = false;
        PreparedStatement pstmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("isFancyNoAssigned");
            String sql = "SELECT * FROM " + TableList.VT_ADVANCE_REGN_NO + " where regn_appl_no=? and state_cd=? and off_cd=?";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, appl_no);
            pstmt.setString(2, Util.getUserStateCode());
            pstmt.setInt(3, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isFancy = true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
        return isFancy;
    }

    public Map<String, Object> getAvailableRegistrationNoList(String appl_no, String regn_no, boolean isFancy) throws Exception {
        PreparedStatement pstmt = null;
        TransactionManager tmgr = null;
        Map<String, Object> regnNoList = new LinkedHashMap<String, Object>();
        String sql = "";
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("getAvailableRegistrationNoList");
            if (isFancy) {
                sql = "SELECT * FROM " + TableList.VT_ADVANCE_REGN_NO + " where regn_appl_no=? and state_cd=? and off_cd=?";
                pstmt = tmgr.prepareStatement(sql);
                pstmt.setString(1, appl_no);
                pstmt.setString(2, Util.getUserStateCode());
                pstmt.setInt(3, Util.getSelectedSeat().getOff_cd());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    regnNoList.put(rs.getString("regn_no"), rs.getString("regn_no"));
                }
            } else {
                Map map = FormulaUtils.regn_no_records(appl_no, regn_no, tmgr);
                int series_id = Integer.parseInt(map.get("series_id").toString());
                NewVehicleNo.fillForModeMixP(tmgr, series_id, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                sql = "select regn_no from " + TableList.VM_REGN_AVAILABLE + " where status=? and state_cd=? and off_cd=? and series_id=? order by  SUBSTR(regn_no, -4) limit 50";
                pstmt = tmgr.prepareStatement(sql);
                pstmt.setString(1, "A");
                pstmt.setString(2, Util.getUserStateCode());
                pstmt.setInt(3, Util.getSelectedSeat().getOff_cd());
                pstmt.setInt(4, series_id);
                rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    regnNoList.put(rs.getString("regn_no"), rs.getString("regn_no"));
                }
                tmgr.commit();
            }
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
        return regnNoList;
    }

    public String saveAndAssignRegn_no(Owner_dobj ownerDobj, String alloted_regn_no, Status_dobj statusDobj, int pur_cd, List<ComparisonBean> compareChagnes, AdvanceRegnNo_dobj advanceRegnNo_dobj) throws VahanException {
        String new_regn_no = "";
        TransactionManager tmgr = null;
        NewVehicleNo newVehicleNo = new NewVehicleNo();
        try {
            tmgr = new TransactionManager("saveAndAssignRegn_no");
            blockRegnNoForAllocation(alloted_regn_no, ownerDobj.getAppl_no(), tmgr);
            if (advanceRegnNo_dobj != null) {
                advanceRegnNo_dobj.setRegn_appl_no(ownerDobj.getAppl_no());
                NewImpl.updateAdvanceRegNoDetails(advanceRegnNo_dobj, ownerDobj, tmgr);
            }
            new_regn_no = newVehicleNo.generateAssignNewRegistrationNo(Util.getSelectedSeat().getOff_cd(), Util.getSelectedSeat().getAction_cd(), ownerDobj.getAppl_no(), alloted_regn_no, 1, ownerDobj,null, tmgr);
            if (pur_cd == TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION
                    || pur_cd == TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO) {
                reverseUpdationForConversion(tmgr, ownerDobj, new_regn_no);
            }
            if (advanceRegnNo_dobj != null) {
                insertIntoVhaChangedData(tmgr, ownerDobj.getAppl_no(), ComparisonBeanImpl.changedDataContents(compareChagnes));
                insertIntoVhaOwner(tmgr, ownerDobj.getAppl_no());
                updateVaOwner(tmgr, ownerDobj);
            }
            if (!"".equalsIgnoreCase(new_regn_no) && !CommonUtils.isNullOrBlank(new_regn_no)) {
                ServerUtil.webServiceForNextStage(statusDobj, tmgr);
                ServerUtil.fileFlow(tmgr, statusDobj);
                tmgr.commit();
            }
        } catch (VahanException e) {
            throw e;
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
        return new_regn_no;
    }

    public void revertForAdvanceRegnNo(Owner_dobj ownerDobj, String alloted_regn_no, Status_dobj statusDobj, int pur_cd) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("revertForAdvanceRegnNo");
            if (statusDobj.getAction_cd() == TableConstants.VM_ROLE_REGISTRATION_NO_ASSIGNMENT_RTO) {
                statusDobj.setAction_cd(TableConstants.TM_NEW_RC_VERIFICATION);
                statusDobj.setFlow_slno(5);
            } else if (statusDobj.getAction_cd() == TableConstants.VM_ROLE_REGISTRATION_NO_ASSIGNMENT_DEALER) {
                statusDobj.setAction_cd(TableConstants.TM_ROLE_DEALER_VERIFICATION);
                statusDobj.setFlow_slno(4);
            }
            ServerUtil.fileFlow(tmgr, statusDobj);
            tmgr.commit();
        } catch (VahanException e) {
            throw e;
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

    }

    private void blockRegnNoForAllocation(String alloted_regn_no, String appl_no, TransactionManager tmgr) throws VahanException {
        PreparedStatement pstmt = null;
        try {

            String sql = "Update " + TableList.VM_REGN_ALLOTED + " SET  state_cd=? , off_cd=?, regn_no=?,appl_no=? where regn_no=? and state_cd=?";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, Util.getUserStateCode());
            pstmt.setInt(2, Util.getSelectedSeat().getOff_cd());
            pstmt.setString(3, alloted_regn_no);
            pstmt.setString(4, appl_no);
            pstmt.setString(5, alloted_regn_no);
            pstmt.setString(6, Util.getUserStateCode());
            pstmt.executeUpdate();
            int count = pstmt.executeUpdate();
            if (count > 0) {
                throw new VahanException("This Registration No Already Assign or Not Available");
            }

            sql = "INSERT INTO " + TableList.VM_REGN_ALLOTED + "(\n"
                    + " state_cd, off_cd, regn_no, appl_no, op_dt)\n"
                    + " VALUES (?, ?, ?, ?, current_timestamp)";
            pstmt = tmgr.prepareStatement(sql);
            pstmt.setString(1, Util.getUserStateCode());
            pstmt.setInt(2, Util.getSelectedSeat().getOff_cd());
            pstmt.setString(3, alloted_regn_no);
            pstmt.setString(4, appl_no);
            pstmt.executeUpdate();
        } catch (VahanException e) {
            throw e;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is some problem to allocation of This Registration No");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is some problem to allocation of This Registration No");
        }
    }

    private void reverseUpdationForConversion(TransactionManager tmgr, Owner_dobj ownerDobj, String new_regn_no) throws SQLException {
        String sql = "Update " + TableList.VA_DETAILS + " Set regn_no = ? Where regn_no = ? and state_cd = ? and off_cd = ?";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, ownerDobj.getRegn_no());
        ps.setString(2, new_regn_no);
        ps.setString(3, ownerDobj.getState_cd());
        ps.setInt(4, ownerDobj.getOff_cd());
        ps.executeUpdate();


        ///  VT_FEE
        sql = "select DISTINCT fee.rcpt_no from vp_appl_rcpt_mapping map,vt_fee fee"
                + " where map.appl_no=? and map.rcpt_no=fee.rcpt_no and map.state_cd=fee.state_cd and map.off_cd=fee.off_cd ";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, ownerDobj.getAppl_no());

        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        while (rs.next()) {
            //logger.info("Update vt_fee in assignNewVehicleNumberRandom()");
            sql = "UPDATE vt_fee set regn_no= ? WHERE rcpt_no=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getRegn_no());
            ps.setString(2, rs.getString("rcpt_no"));
            ps.setString(3, Util.getUserStateCode());
            ps.setInt(4, Util.getSelectedSeat().getOff_cd());
            ps.executeUpdate();
        }

        //VT_TAX          
        sql = "select DISTINCT tax.rcpt_no from vp_appl_rcpt_mapping map,vt_tax tax"
                + " where map.appl_no=? and map.rcpt_no=tax.rcpt_no and map.state_cd=tax.state_cd and map.off_cd=tax.off_cd ";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, ownerDobj.getAppl_no());

        rs = tmgr.fetchDetachedRowSet_No_release();
        while (rs.next()) {
            //logger.info("Update vt_tax in assignNewVehicleNumberRandom()");
            sql = "UPDATE vt_tax set regn_no= ? WHERE rcpt_no=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getRegn_no());
            ps.setString(2, rs.getString("rcpt_no"));
            ps.setString(3, Util.getUserStateCode());
            ps.setInt(4, Util.getSelectedSeat().getOff_cd());
            ps.executeUpdate();
        }
    }
}//end of updateVaOwner

