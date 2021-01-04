/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitLeaseDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

/**
 *
 * @author acer
 */
public class PermitLeaseImpl {

    private static final Logger LOGGER = Logger.getLogger(PermitLeaseImpl.class);

    public String createApplication(PermitLeaseDobj countDobj) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String applNo = "";
        try {

            tmgr = new TransactionManager("PermitLeaseImpl-createApplication");
            applNo = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
            String Query = "INSERT INTO permit.va_lease_permit(\n"
                    + "            state_cd, off_cd, appl_no,rcpt_no,period_mode, period,  \n"
                    + "            pur_cd,l_regn_no,l_owner_name,l_f_name,c_add1,c_city,c_pincode,pmt_type, pmt_no,pmt_valid_from,pmt_valid_upto,m_regn_no,op_dt) \n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?,?,?,?,current_timestamp)";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.setString(i++, applNo);
            ps.setString(i++, "");
            ps.setString(i++, countDobj.getPeriod_mode());
            ps.setInt(i++, countDobj.getPeriod());
            ps.setInt(i++, countDobj.getPur_cd());
            ps.setString(i++, countDobj.getLeaseRegnNo().toUpperCase());
            ps.setString(i++, countDobj.getOwner_name().toUpperCase());
            ps.setString(i++, countDobj.getF_name().toUpperCase());
            ps.setString(i++, countDobj.getAddress().toUpperCase());
            ps.setString(i++, countDobj.getCity().toUpperCase());
            ps.setInt(i++, Integer.parseInt(countDobj.getPincode()));
            ps.setInt(i++, countDobj.getPmt_type());
            ps.setString(i++, countDobj.getPmt_no());
            ps.setDate(i++, new java.sql.Date(countDobj.getValid_from().getTime()));
            ps.setDate(i++, new java.sql.Date(countDobj.getValid_upto().getTime()));
            ps.setString(i++, countDobj.getRegn_no().toUpperCase());
            ps.executeUpdate();
            Status_dobj status = new Status_dobj();
            status.setAppl_no(applNo);
            status.setPur_cd(countDobj.getPur_cd());
            status.setFlow_slno(1);
            status.setFile_movement_slno(1);
            status.setAction_cd(TableConstants.TM_ROLE_PMT_LEASE_PERMIT_ENTRY);
            if (Util.getSession().getAttribute("selected_role_cd") == null) {
                status.setEmp_cd(0);
            } else {
                status.setEmp_cd(Long.parseLong(Util.getEmpCode()));
            }
            status.setRegn_no(countDobj.getRegn_no().toUpperCase());
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
            ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, applNo, TableConstants.TM_ROLE_PMT_LEASE_PERMIT_ENTRY, countDobj.getPur_cd(), null, tmgr);
            ServerUtil.fileFlow(tmgr, status);
            tmgr.commit();
        } catch (VahanException e) {
            applNo = "";
            throw e;
        } catch (Exception e) {
            applNo = "";
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                applNo = "";
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return applNo;
    }

    public PermitLeaseDobj getLeaseDeatilForApplNo(String applNo) {
        TransactionManager tmgr = null;
        PermitLeaseDobj couDobj = null;
        try {
            tmgr = new TransactionManager("getLeaseDeatilForApplNo");
            String Query = "SELECT * FROM permit.va_lease_permit where appl_no = ?";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                couDobj = new PermitLeaseDobj();
                couDobj.setState_cd(rs.getString("state_cd"));
                couDobj.setOff_cd(rs.getInt("off_cd"));
                couDobj.setPmt_no(rs.getString("rcpt_no"));
                couDobj.setPur_cd(rs.getInt("pur_cd"));
                couDobj.setPeriod_mode(rs.getString("period_mode"));
                couDobj.setPeriod(rs.getInt("period"));
                couDobj.setLeaseRegnNo(rs.getString("l_regn_no"));
                couDobj.setOwner_name(rs.getString("l_owner_name"));
                couDobj.setF_name(rs.getString("l_f_name"));
                couDobj.setAddress(rs.getString("c_add1"));
                couDobj.setCity(rs.getString("c_city"));
                couDobj.setPincode(rs.getString("c_pincode"));
                couDobj.setPmt_no(rs.getString("pmt_no"));
                couDobj.setRegn_no(rs.getString("m_regn_no"));

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
        return couDobj;
    }

    public void updateVaLeaseDetails(PermitOwnerDetailDobj ownDobj, PermitLeaseDobj countDobj, TransactionManager tmgr, String applNo) throws SQLException {
        String Query = "UPDATE permit.va_lease_permit\n"
                + "   SET  l_owner_name=?, l_f_name=?, c_add1=?, c_city=?, c_pincode=?,period_mode=?,period=? WHERE  appl_no=? ";
        PreparedStatement ps = tmgr.prepareStatement(Query);
        int i = 1;
        ps.setString(i++, countDobj.getOwner_name());
        ps.setString(i++, countDobj.getF_name());
        ps.setString(i++, countDobj.getAddress());
        ps.setString(i++, countDobj.getCity());
        ps.setInt(i++, Integer.parseInt(countDobj.getPincode()));
        ps.setString(i++, countDobj.getPeriod_mode());
        ps.setInt(i++, countDobj.getPeriod());
        ps.setString(i++, applNo);
        ps.executeUpdate();
    }

    public void moveVaToVha(TransactionManager tmgr, String applNo) throws SQLException {
        String Query = "INSERT INTO permit.vha_lease_permit(\n"
                + "            state_cd, off_cd, appl_no, rcpt_no, period_mode, period, \n"
                + "            pur_cd, l_regn_no, l_owner_name, l_f_name, c_add1, c_city, c_pincode, \n"
                + "            pmt_type,pmt_no,pmt_valid_from,pmt_valid_upto, m_regn_no,op_dt, moved_on, moved_by)\n"
                + "    SELECT  state_cd, off_cd, appl_no, rcpt_no, period_mode, period, \n"
                + "            pur_cd, l_regn_no, l_owner_name, l_f_name, c_add1, c_city, c_pincode, \n"
                + "            pmt_type,pmt_no,pmt_valid_from,pmt_valid_upto, m_regn_no,op_dt, Current_timestamp,? \n"
                + "  FROM permit.va_lease_permit where appl_no = ?";
        PreparedStatement ps = tmgr.prepareStatement(Query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, applNo);
        ps.executeUpdate();
    }

    public void moveVtToVh(TransactionManager tmgr, String regnNo) throws SQLException {
        String Query = "INSERT INTO permit.vh_permit_countersignature(\n"
                + "            state_cd, off_cd, state_cd_from, off_cd_from, appl_no, regn_no, \n"
                + "            rcpt_no, pur_cd, count_sign_no, pmt_no, pmt_type, count_valid_from, \n"
                + "            count_valid_upto, pmt_valid_from, pmt_valid_upto, owner_name, \n"
                + "            mobile_no, email_id, vh_class, seat_cap, unld_wt, ld_wt, vch_catg, \n"
                + "            owner_ctg, f_name, c_add1, c_add2, c_add3, c_district, c_pincode, \n"
                + "            c_state, p_add1, p_add2, p_add3, p_district, p_pincode, p_state, \n"
                + "            op_dt, moved_on, moved_by,region)\n"
                + "   SELECT   state_cd, off_cd, state_cd_from, off_cd_from, appl_no, regn_no, \n"
                + "            rcpt_no, pur_cd, count_sign_no, pmt_no, pmt_type, count_valid_from, \n"
                + "            count_valid_upto, pmt_valid_from, pmt_valid_upto, owner_name, \n"
                + "            mobile_no, email_id, vh_class, seat_cap, unld_wt, ld_wt, vch_catg, \n"
                + "            owner_ctg, f_name, c_add1, c_add2, c_add3, c_district, c_pincode, \n"
                + "            c_state, p_add1, p_add2, p_add3, p_district, p_pincode, p_state, \n"
                + "            op_dt,CURRENT_TIMESTAMP,?,region \n"
                + "  FROM permit.vt_permit_countersignature where appl_no = ? ";
        PreparedStatement ps = tmgr.prepareStatement(Query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, regnNo);
        ps.executeUpdate();
    }

    public void insertIntoVt(TransactionManager tmgr, PermitLeaseDobj countDobj, String regnNo, String applNo) throws SQLException {
        PreparedStatement ps = null;
        Date validFrom = null;
        Date validUpto = null;
        if (("Y").equalsIgnoreCase(countDobj.getPeriod_mode())) {
            Calendar cal = Calendar.getInstance();
            validFrom = cal.getTime();
            cal.add(Calendar.YEAR, countDobj.getPeriod());
            cal.add(Calendar.DAY_OF_YEAR, -1);
            validUpto = cal.getTime();
        }
        if (("M").equalsIgnoreCase(countDobj.getPeriod_mode())) {
            Calendar cal = Calendar.getInstance();
            validFrom = cal.getTime();
            cal.add(Calendar.MONTH, countDobj.getPeriod());
            cal.add(Calendar.DAY_OF_MONTH, -1);
            validUpto = cal.getTime();
        }

        String Query = "SELECT * FROM permit.va_lease_permit where m_regn_no = ? AND appl_no = ?";
        ps = tmgr.prepareStatement(Query);
        ps.setString(1, regnNo);
        ps.setString(2, applNo);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            Date pmtValidUpto = rs.getDate("pmt_valid_upto");
            if (pmtValidUpto != null && pmtValidUpto.compareTo(validUpto) <= 0) {
                validUpto = pmtValidUpto;
            }
        }
        Query = "INSERT INTO permit.vt_lease_permit(\n"
                + "            state_cd, off_cd, appl_no, rcpt_no, pur_cd, l_regn_no, \n"
                + "            lease_valid_from, lease_valid_upto, l_owner_name, l_f_name, c_add1, c_city, \n"
                + "            c_pincode, pmt_type, pmt_no, pmt_valid_from, \n"
                + "            pmt_valid_upto, m_regn_no,op_dt)\n"
                + "   SELECT  state_cd, off_cd, appl_no, rcpt_no, pur_cd, l_regn_no, \n"
                + "            ?, ?, l_owner_name, l_f_name, c_add1, c_city, \n"
                + "            c_pincode, pmt_type, pmt_no, pmt_valid_from, \n"
                + "             pmt_valid_upto, m_regn_no,CURRENT_TIMESTAMP \n"
                + "  FROM permit.va_lease_permit where m_regn_no = ? AND appl_no = ?";
        ps = tmgr.prepareStatement(Query);
        ps.setDate(1, new java.sql.Date(validFrom.getTime()));
        ps.setDate(2, new java.sql.Date(validUpto.getTime()));
        ps.setString(3, regnNo);
        ps.setString(4, applNo);
        ps.executeUpdate();
    }

    public void deleteVatables(TransactionManager tmgr, String applNo, String regnNo, String tableName) throws SQLException {
        String Query = "DELETE FROM " + tableName + " where appl_no = ?";
        PreparedStatement ps = tmgr.prepareStatement(Query);
        ps.setString(1, applNo);
        ps.executeUpdate();
    }

    public String verificationStage(String appl_no, Status_dobj status_dobj, PermitOwnerDetailDobj ownDobj, PermitLeaseDobj countDobj, String CompairChange) throws VahanException {
        TransactionManager tmgr = null;
        String return_location = "";
        try {
            tmgr = new TransactionManager("verificationStage");
            if (!CommonUtils.isNullOrBlank(CompairChange)) {
                moveVaToVha(tmgr, appl_no);
                updateVaLeaseDetails(ownDobj, countDobj, tmgr, appl_no);
                insertIntoVhaChangedData(tmgr, appl_no, CompairChange);
            }
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            ServerUtil.fileFlow(tmgr, status_dobj);
            tmgr.commit();
            return_location = "seatwork";
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);;
            throw new VahanException("There is some problem while saving data");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                return_location = "";
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return return_location;
    }

    public String approvalStage(String appl_no, String regn_no, Status_dobj status_dobj, PermitOwnerDetailDobj ownDobj, PermitLeaseDobj countDobj, PassengerPermitDetailDobj pmtDobj) throws VahanException {
        TransactionManager tmgr = null;
        String return_location = "";
        try {
            tmgr = new TransactionManager("approvalStage");
            moveVaToVha(tmgr, appl_no);
            insertIntoVt(tmgr, countDobj, regn_no, appl_no);
            deleteVatables(tmgr, appl_no, null, "permit.va_lease_permit");
            String docId = CommonPermitPrintImpl.getPermitDocIdForQuery(CommonPermitPrintImpl.getPermitDocId(tmgr, Integer.valueOf(pmtDobj.getPmt_type()), TableConstants.VM_PMT_RENEWAL_PUR_CD, Util.getUserStateCode()));
            String[] beanData = {docId, appl_no, regn_no};
            CommonPermitPrintImpl.insertVaPermitPrint(tmgr, beanData);
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            ServerUtil.fileFlow(tmgr, status_dobj);
            tmgr.commit();
            return_location = "seatwork";
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is some problem while saving data");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                return_location = "";
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return return_location;
    }

    public void stayOnTheSameStage(String appl_no, PermitOwnerDetailDobj ownDobj, PermitLeaseDobj countDobj, String CompairChange) throws VahanException {
        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager("stayOnTheSameStage");
            if (!CommonUtils.isNullOrBlank(CompairChange)) {
                moveVaToVha(tmgr, appl_no);
                updateVaLeaseDetails(ownDobj, countDobj, tmgr, appl_no);
                insertIntoVhaChangedData(tmgr, appl_no, CompairChange);
                tmgr.commit();
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("There is some problem while saving data");
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
}
