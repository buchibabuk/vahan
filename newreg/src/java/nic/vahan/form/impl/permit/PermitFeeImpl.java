/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.DocumentType;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.ManualReceiptEntryDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.permit.PermitFeeDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TaxExemptiondobj;
import nic.vahan.form.dobj.permit.CommonPermitFeeDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitShowFeeDetailDobj;
import nic.vahan.form.impl.ExemptionFeeFineImpl;
import nic.vahan.form.impl.FeeDraftimpl;
import nic.vahan.form.impl.FeeImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.ManualReceiptEntryImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

public class PermitFeeImpl {

    private static final Logger LOGGER = Logger.getLogger(PermitFeeImpl.class);

    public PermitFeeImpl() {
    }

    public PermitFeeDobj getRegnNO(String appNo) {
        PermitFeeDobj pmtfee_dobj = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("Permit_Fee_Impl.getRegnNO");
            ps = tmgr.prepareStatement("select * from " + TableList.VA_PERMIT + " where appl_no =?");
            ps.setString(1, appNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                String rcptNo = rs.getString("rcpt_no");
                pmtfee_dobj = new PermitFeeDobj();
                pmtfee_dobj.setRegn_no(rs.getString("regn_no"));
                pmtfee_dobj.setPermit_domain(rs.getInt("domain_cd"));
                pmtfee_dobj.setPermit_catg(rs.getInt("pmt_catg"));
                pmtfee_dobj.setPermit_type(rs.getString("pmt_type"));
                pmtfee_dobj.setPur_cd("" + rs.getInt("pur_cd"));
                pmtfee_dobj.setService_type(rs.getInt("service_type"));
                pmtfee_dobj.setRegn_no(rs.getString("regn_no"));
                pmtfee_dobj.setPeriod(rs.getString("period"));
                pmtfee_dobj.setPeriod_mode(rs.getString("period_mode"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return pmtfee_dobj;
    }

    public PermitFeeDobj getDetailsCounterSignature(String appNo) {
        PermitFeeDobj pmtfee_dobj = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getDetailsCounterSignature");
            ps = tmgr.prepareStatement("select * from permit.va_permit_countersignature where appl_no =?");
            ps.setString(1, appNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                pmtfee_dobj = new PermitFeeDobj();
                pmtfee_dobj.setRegn_no(rs.getString("regn_no"));
                pmtfee_dobj.setPermit_type(rs.getString("pmt_type"));
                pmtfee_dobj.setPur_cd("" + rs.getInt("pur_cd"));
                pmtfee_dobj.setPeriod(rs.getString("period"));
                pmtfee_dobj.setPeriod_mode(rs.getString("period_mode"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return pmtfee_dobj;
    }

    public PermitFeeDobj getDetailsCounterSignatureFromVt(String regn_no) {
        PermitFeeDobj pmtfee_dobj = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getDetailsCounterSignature");
            ps = tmgr.prepareStatement("select * from permit.vt_permit_countersignature where regn_no =?");
            ps.setString(1, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                pmtfee_dobj = new PermitFeeDobj();
                pmtfee_dobj.setRegn_no(rs.getString("regn_no"));
                pmtfee_dobj.setPermit_type(rs.getString("pmt_type"));
                pmtfee_dobj.setPur_cd("" + rs.getInt("pur_cd"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return pmtfee_dobj;
    }

    public PermitFeeDobj getDetailsLeasePermit(String appNo) {
        PermitFeeDobj pmtfee_dobj = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getDetailsLeasePermit");
            ps = tmgr.prepareStatement("select a.m_regn_no as regn_no,b.pmt_type,a.pur_cd,a.period,a.period_mode from permit.va_lease_permit a inner join permit.vt_permit b on a.m_regn_no=b.regn_no and a.state_cd=b.state_cd where a.appl_no =?");
            ps.setString(1, appNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                pmtfee_dobj = new PermitFeeDobj();
                pmtfee_dobj.setRegn_no(rs.getString("regn_no"));
                pmtfee_dobj.setPermit_type(rs.getString("pmt_type"));
                pmtfee_dobj.setPur_cd("" + rs.getInt("pur_cd"));
                pmtfee_dobj.setPeriod(rs.getString("period"));
                pmtfee_dobj.setPeriod_mode(rs.getString("period_mode"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return pmtfee_dobj;
    }

    public PermitFeeDobj getDetaliINVtPermit(String regn_no, String appl_no, String state_cd) {
        PermitFeeDobj pmtfee_dobj = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("Permit_Fee_Impl.getDetaliINVtPermit");
            ps = tmgr.prepareStatement("select * from " + TableList.VT_PERMIT + " where regn_no =? order by pmt_type desc");
            ps.setString(1, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                pmtfee_dobj = new PermitFeeDobj();
                pmtfee_dobj.setPermit_domain(rs.getInt("domain_cd"));
                pmtfee_dobj.setPermit_catg(rs.getInt("pmt_catg"));
                pmtfee_dobj.setPermit_type(rs.getString("pmt_type"));
                pmtfee_dobj.setService_type(rs.getInt("service_type"));
                pmtfee_dobj.setRegn_no(rs.getString("regn_no"));
                pmtfee_dobj.setPermit_no(rs.getString("pmt_no"));
                pmtfee_dobj.setPermit_valid_upto(rs.getDate("valid_upto"));
            } else {
                ps = null;
                ps = tmgr.prepareStatement("select * from " + TableList.VA_PERMIT_TRANSACTION + " a inner join " + TableList.VT_TEMP_PERMIT + " b on a.state_cd=b.state_cd and a.regn_no=b.regn_no and a.pmt_no=b.pmt_no  where a.state_cd=? and a.appl_no =? and b.regn_no=?");
                ps.setString(1, state_cd);
                ps.setString(2, appl_no);
                ps.setString(3, regn_no);
                RowSet rs1 = tmgr.fetchDetachedRowSet();
                if (rs1.next()) {
                    pmtfee_dobj = new PermitFeeDobj();
                    pmtfee_dobj.setPermit_domain(0);
                    pmtfee_dobj.setPermit_catg(rs1.getInt("pmt_catg"));
                    pmtfee_dobj.setPermit_type(rs1.getString("pmt_type"));
                    pmtfee_dobj.setService_type(-1);
                    pmtfee_dobj.setRegn_no(rs1.getString("regn_no"));
                    pmtfee_dobj.setPermit_no(rs1.getString("pmt_no"));
                    pmtfee_dobj.setPermit_valid_upto(rs1.getDate("valid_upto"));
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return pmtfee_dobj;
    }

    public PermitFeeDobj getDetaliINVhPermit(String regn_no) {
        PermitFeeDobj pmtfee_dobj = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("Permit_Fee_Impl.getRegnNO");
            ps = tmgr.prepareStatement("select * from " + TableList.VH_PERMIT + " where regn_no =?");
            ps.setString(1, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                pmtfee_dobj = new PermitFeeDobj();
                pmtfee_dobj.setPermit_domain(rs.getInt("domain_cd"));
                pmtfee_dobj.setPermit_catg(rs.getInt("pmt_catg"));
                pmtfee_dobj.setPermit_type(rs.getString("pmt_type"));
                pmtfee_dobj.setService_type(rs.getInt("service_type"));
                pmtfee_dobj.setRegn_no(rs.getString("regn_no"));
                pmtfee_dobj.setPermit_no(rs.getString("pmt_no"));
                pmtfee_dobj.setPermit_valid_upto(rs.getDate("valid_upto"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return pmtfee_dobj;
    }

    public PermitFeeDobj getDetailsOfTempPermit(String appl_no) {
        PermitFeeDobj pmtfee_dobj = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getDetailsOfTempPermit");
            ps = tmgr.prepareStatement("select regn_no,pmt_type,pmt_catg,period_mode,period from " + TableList.VA_TEMP_PERMIT + " where appl_no =?");
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                pmtfee_dobj = new PermitFeeDobj();
                pmtfee_dobj.setRegn_no(rs.getString("regn_no"));
                pmtfee_dobj.setPermit_catg(rs.getInt("pmt_catg"));
                pmtfee_dobj.setPermit_type(rs.getString("pmt_type"));
                pmtfee_dobj.setRegn_no(rs.getString("regn_no"));
                pmtfee_dobj.setPeriod(rs.getString("period"));
                pmtfee_dobj.setPeriod_mode(rs.getString("period_mode"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return pmtfee_dobj;
    }

    public PermitFeeDobj get_regnNo_details(String regn_no, int action_cd) {
        PermitFeeDobj pmt_fee_dobj = null;
        PreparedStatement ps;
        TransactionManager tmgr = null;
        String Query;
        try {
            tmgr = new TransactionManager("get_regnNo_details");
            Query = "Select owner_name, f_name, vh_class, b.pmt_no,to_char(regn_dt,'DD-MON-YYYY') as regn_dt \n"
                    + "from " + TableList.VT_OWNER
                    + " a "
                    + "left outer join " + TableList.VT_PERMIT + " b on b.regn_no = a.regn_no\n"
                    + "where a.regn_no = ? AND a.STATE_CD = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                pmt_fee_dobj = new PermitFeeDobj();
                pmt_fee_dobj.setNew_f_name(rs.getString("f_name"));
                pmt_fee_dobj.setNew_o_name(rs.getString("owner_name"));
                pmt_fee_dobj.setNew_vh_class(rs.getString("vh_class"));
                pmt_fee_dobj.setRegn_dt(rs.getString("regn_dt"));
                pmt_fee_dobj.setPermit_no(rs.getString("pmt_no"));
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
        return pmt_fee_dobj;
    }

    public void updateSetVaPermit(PermitFeeDobj dobj, TransactionManager tmgr, String rcptNo) throws VahanException {
        PreparedStatement ps = null;
        PassengerPermitDetailImpl passImpl = null;
        try {
            String Query = "SELECT * FROM " + TableList.VA_PERMIT + " where appl_no=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, dobj.getAppl_no());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (!CommonUtils.isNullOrBlank(rs.getString("rcpt_no"))) {
                    passImpl = new PassengerPermitDetailImpl();
                    passImpl.insertVHA_PermitandUpdateVa_Permit(tmgr, null, dobj.getAppl_no(), null);
                }
            }
            String updateVAPermit = "update " + TableList.VA_PERMIT + " set rcpt_no=?, regn_no=? where appl_no=?";
            ps = tmgr.prepareStatement(updateVAPermit);
            ps.setString(1, rcptNo);
            if (("new").equalsIgnoreCase(dobj.getRegn_no())) {
                ps.setString(2, CommonUtils.isNullOrBlank(dobj.getNew_regn_no()) ? "NEW" : dobj.getNew_regn_no());
            } else {
                ps.setString(2, dobj.getRegn_no());
            }
            ps.setString(3, dobj.getAppl_no());
            ps.executeUpdate();

            if (dobj.getPur_cd().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEWAL_PUR_CD))) {
                Query = "select * from " + TableList.VT_EXEMPTION + " where regn_no = ? and pur_cd = ? ";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, dobj.getRegn_no().toUpperCase());
                ps.setInt(2, TableConstants.VM_PMT_EXEMPTION_PUR_CD);
                RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                if (rs1.next()) {
                    ExemptionImpl impl = new ExemptionImpl();
                    impl.moveVTToVhPmtExem(tmgr, dobj.getRegn_no().toUpperCase(), TableConstants.VM_PMT_EXEMPTION_PUR_CD, true);
                    impl.deleteInVtPmtExem(tmgr, dobj.getRegn_no().toUpperCase(), TableConstants.VM_PMT_EXEMPTION_PUR_CD);
                }
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void updateSetVA_temp_Permit(TransactionManager tmgr, String rcptNo, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String updateVAPermit = "update " + TableList.VA_TEMP_PERMIT + " set rcpt_no=? where appl_no=?";
        ps = tmgr.prepareStatement(updateVAPermit);
        ps.setString(1, rcptNo);
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public void updateSetVaPermitTransaction(TransactionManager tmgr, String rcptNo, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String updateVAPermit = "update " + TableList.VA_PERMIT_TRANSACTION + " set rcpt_no=? where appl_no=?";
        ps = tmgr.prepareStatement(updateVAPermit);
        ps.setString(1, rcptNo);
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public void updateSetVaCounterSignature(TransactionManager tmgr, String rcptNo, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String updateVAPermit = "update permit.va_permit_countersignature set rcpt_no=? where appl_no=?";
        ps = tmgr.prepareStatement(updateVAPermit);
        ps.setString(1, rcptNo);
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public void updateSetVaCounterSignatureAuth(TransactionManager tmgr, String rcptNo, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String updateVAPermit = "update permit.va_permit_countersignature_authorization set rcpt_no=? where appl_no=?";
        ps = tmgr.prepareStatement(updateVAPermit);
        ps.setString(1, rcptNo);
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public void updateSetVaLeasePermit(TransactionManager tmgr, String rcptNo, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String updateVAPermit = "update permit.va_lease_permit set rcpt_no=? where appl_no=?";
        ps = tmgr.prepareStatement(updateVAPermit);
        ps.setString(1, rcptNo);
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public void updateVA_Permit_Owner(TransactionManager tmgr, String newRegn_no, String appl_no) throws SQLException {
        if (!CommonUtils.isNullOrBlank(newRegn_no)) {
            String Query;
            PreparedStatement ps = null;
            Query = "INSERT INTO " + TableList.VHA_PERMIT_OWNER + "(\n"
                    + "            state_cd, off_cd, appl_no, regn_no, owner_name, f_name, c_add1, \n"
                    + "            c_add2, c_add3, c_district, c_pincode, c_state, p_add1, p_add2, \n"
                    + "            p_add3, p_district, p_pincode, p_state, mobile_no, email_id, \n"
                    + "            vh_class, seat_cap, unld_wt, ld_wt, vch_catg, owner_ctg, moved_on, moved_by,fuel)\n"
                    + "    SELECT state_cd, off_cd, appl_no, regn_no, owner_name, f_name, c_add1, \n"
                    + "           c_add2, c_add3, c_district, c_pincode, c_state, p_add1, p_add2, \n"
                    + "           p_add3, p_district, p_pincode, p_state, mobile_no, email_id, \n"
                    + "           vh_class, seat_cap, unld_wt, ld_wt, vch_catg, owner_ctg,current_timestamp,?,fuel\n"
                    + "FROM " + TableList.VA_PERMIT_OWNER + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            ps.executeUpdate();


            Query = "UPDATE " + TableList.VA_PERMIT_OWNER + " SET  regn_no=? WHERE appl_no=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, newRegn_no);
            ps.setString(2, appl_no);
            ps.executeUpdate();

            Query = "INSERT INTO " + TableList.VHA_PERMIT_OWNER + "(\n"
                    + "            state_cd, off_cd, appl_no, regn_no, owner_name, f_name, c_add1, \n"
                    + "            c_add2, c_add3, c_district, c_pincode, c_state, p_add1, p_add2, \n"
                    + "            p_add3, p_district, p_pincode, p_state, mobile_no, email_id, \n"
                    + "            vh_class, seat_cap, unld_wt, ld_wt, vch_catg,owner_ctg, moved_on, moved_by,fuel)\n"
                    + "    SELECT state_cd, off_cd, appl_no, regn_no, owner_name, f_name, c_add1, \n"
                    + "           c_add2, c_add3, c_district, c_pincode, c_state, p_add1, p_add2, \n"
                    + "           p_add3, p_district, p_pincode, p_state, mobile_no, email_id, \n"
                    + "           vh_class, seat_cap, unld_wt, ld_wt, vch_catg,owner_ctg,current_timestamp + interval '1 second' as moved_on,?,fuel\n"
                    + "FROM " + TableList.VA_PERMIT_OWNER + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            ps.executeUpdate();

            Query = "DELETE FROM " + TableList.VA_PERMIT_OWNER + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            ps.executeUpdate();

            Query = "UPDATE " + TableList.VA_DETAILS + " SET regn_no=? WHERE appl_no=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, newRegn_no);
            ps.setString(2, appl_no);
            ps.executeUpdate();
        }
    }

    public void updateOfferVtPermit(TransactionManager tmgr, String offerNo, String appl_no) throws SQLException {
        String Query;
        PreparedStatement ps;
        Query = "INSERT INTO " + TableList.VHA_PERMIT + " (\n"
                + "            state_cd, off_cd, appl_no, regn_no, rcpt_no, period_mode, period, \n"
                + "            pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, service_type, \n"
                + "            goods_to_carry, jorney_purpose, parking, replace_date, alloted_flag, \n"
                + "            offer_no, order_no, order_by, order_dt, remarks, op_dt, moved_on, \n"
                + "            moved_by)\n"
                + "     SELECT state_cd, off_cd, appl_no, regn_no, rcpt_no, period_mode, period, \n"
                + "            pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, service_type, \n"
                + "            goods_to_carry, jorney_purpose, parking, replace_date, alloted_flag, \n"
                + "            offer_no, order_no, order_by, order_dt, remarks, op_dt,current_timestamp,? \n"
                + "  FROM  " + TableList.VA_PERMIT + " where appl_no = ?";
        ps = tmgr.prepareStatement(Query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();

        Query = "UPDATE " + TableList.VA_PERMIT + " SET  offer_no=?,alloted_flag = 'A' WHERE appl_no=?";
        ps = tmgr.prepareStatement(Query);
        ps.setString(1, offerNo);
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

public String saveFeeDetails(PermitFeeDobj dobj, CommonPermitFeeDobj pmt_fee_dobj, FeeDraftDobj feeDraftDobj, boolean printOfferLetter) throws VahanException {
        CommonPermitFeeImpl pmt_fee_impl = null;
        FeeDraftimpl feeDraft_impl = null;
        List<TaxExemptiondobj> fineExemList = new ArrayList<>();
        String rcpt_no = "";
        long inscd = 0;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;

        try {
            pmt_fee_impl = new CommonPermitFeeImpl();
            feeDraft_impl = new FeeDraftimpl();
            tmgr = new TransactionManager("Save Fee Details");
            rcpt_no = pmt_fee_impl.createRecpt_no(tmgr);
            pmt_fee_dobj.setRcpt_no(rcpt_no);
            Status_dobj status = new Status_dobj();
            if (feeDraftDobj != null) {
                feeDraftDobj.setAppl_no(dobj.getAppl_no());
                feeDraftDobj.setRcpt_no(rcpt_no);
                inscd = feeDraft_impl.saveDraftDetails(feeDraftDobj, tmgr);
            }
            if (("new").equalsIgnoreCase(dobj.getRegn_no()) && ("TN,UP").contains(Util.getUserStateCode())) {
                printOfferLetter = true;
            }
            if (printOfferLetter) {
                String offerLetterNo = ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getUserOffCode(), Integer.parseInt(dobj.getPermit_type()), 0, "O");
                if (CommonUtils.isNullOrBlank(offerLetterNo) || ("").equalsIgnoreCase(offerLetterNo)) {
                    throw new VahanException("Offer Letter not genrated");
                } else if (("new").equalsIgnoreCase(dobj.getRegn_no()) && ("TN,PY,UP").contains(Util.getUserStateCode())) {
                    new PermitLOIImpl().insertVaOfferApproval(tmgr, dobj.getAppl_no());
                } else {
                    dobj.setOffer_no(offerLetterNo);
                    String docId = CommonPermitPrintImpl.getPermitDocIdForQuery(CommonPermitPrintImpl.getPermitDocId(tmgr, 0, TableConstants.VM_PMT_APPLICATION_PUR_CD, Util.getUserStateCode()));
                    String[] beanData = {docId, dobj.getAppl_no(), dobj.getRegn_no()};
                    CommonPermitPrintImpl.insertVaPermitPrint(tmgr, beanData);
                    String message = "Permit Order No " + offerLetterNo + " generated against Application No " + dobj.getAppl_no();
                    ServerUtil.insertUsersTransactionMessage(message, 1, tmgr);
                }
            }
            if (("NEW").equalsIgnoreCase(dobj.getRegn_no()) && !printOfferLetter && !CommonUtils.isNullOrBlank(dobj.getNew_regn_no())) {
                updateVA_Permit_Owner(tmgr, dobj.getNew_regn_no(), dobj.getAppl_no());
            } else if (("NEW").equalsIgnoreCase(dobj.getRegn_no()) && printOfferLetter && !(("TN,PY").contains(Util.getUserStateCode()))) {
                updateOfferVtPermit(tmgr, dobj.getOffer_no(), dobj.getAppl_no());
            }

            CommonPermitFeeImpl.PaymentGenInfo payInfo = pmt_fee_impl.getPaymentInfo(pmt_fee_dobj, feeDraftDobj);
            pmt_fee_impl.saveRecptInstMap(inscd, pmt_fee_dobj.getAppl_no(), rcpt_no, payInfo, pmt_fee_dobj, tmgr);
            pmt_fee_impl.insert_into_vt_fee(pmt_fee_dobj, tmgr);
            new FeeImpl().saveFinePaneltyExemDetails(pmt_fee_dobj.getAppl_no(), rcpt_no, tmgr, pmt_fee_dobj.getCollected_by());
            for (PermitShowFeeDetailDobj feeShow : pmt_fee_dobj.getListPmtFeeDetails()) {
                if (feeShow.getPurCd().equalsIgnoreCase(String.valueOf(TableConstants.VM_MAST_MANUAL_RECEIPT))) {
                    List<ManualReceiptEntryDobj> maualFee = ManualReceiptEntryImpl.getManualReceiptEntryDetails(pmt_fee_dobj.getAppl_no());
                    if (maualFee.size() > 0) {
                        for (ManualReceiptEntryDobj manualfeeShow : maualFee) {
                            if (manualfeeShow.getAmount() == Integer.parseInt(feeShow.getPermitAmt().replace("-", ""))) {
                                ManualReceiptEntryImpl.updateVTManualReceiptEntryStatus(tmgr, pmt_fee_dobj.getAppl_no(), true);
                            }
                        }
                    }
                }
            }
          
            for (PermitShowFeeDetailDobj feeShow : pmt_fee_dobj.getListPmtFeeDetails()) {
                if (feeShow.isExem_fee_fine() && Integer.parseInt(feeShow.getPurCd()) == TableConstants.FEE_FINE_EXEMTION) {
                    TaxExemptiondobj exem_dobj = new TaxExemptiondobj();
                    exem_dobj.setPur_cd(TableConstants.FEE_FINE_EXEMTION);
                    if (feeShow.getPermitAmt().contains("-")) {
                        exem_dobj.setExemFeeAmount(Integer.parseInt(feeShow.getPermitAmt().replace("-", "")));
                    }
                    if (feeShow.getPenalty().contains("-")) {
                        exem_dobj.setExemAmount(Integer.parseInt(feeShow.getPenalty().replace("-", "")));
                    }
                    exem_dobj.setPermissionNo(pmt_fee_dobj.getRegn_no());
                    exem_dobj.setPermissionDt(new Date());
                    exem_dobj.setPerpose("COVID-19");
                    fineExemList.add(exem_dobj);
                }
            }
            if (fineExemList.size() > 0) {
                new ExemptionFeeFineImpl().saveExemptionFeeFineDetails(tmgr, pmt_fee_dobj.getAppl_no(), fineExemList, pmt_fee_dobj.getState_cd(), Integer.parseInt(pmt_fee_dobj.getOff_cd()));
                new FeeImpl().saveFinePaneltyExemDetails(pmt_fee_dobj.getAppl_no(), rcpt_no, tmgr, Util.getEmpCode());
            }
            if (dobj.getPur_cd().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_TEMP_PUR_CD))
                    || dobj.getPur_cd().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_SPECIAL_PUR_CD))
                    || dobj.getPur_cd().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEW_TEMP_PUR_CD))) {
                updateSetVA_temp_Permit(tmgr, rcpt_no, dobj.getAppl_no());
            } else if (dobj.getPur_cd().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_FRESH_PUR_CD))
                    || dobj.getPur_cd().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEWAL_PUR_CD))) {
                updateSetVaPermit(dobj, tmgr, rcpt_no);
            } else if (dobj.getPur_cd().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_DUPLICATE_PUR_CD))) {
            } else if (dobj.getPur_cd().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_REPLACE_VEH_PUR_CD))) {
                updateSetVaPermitTransaction(tmgr, rcpt_no, dobj.getAppl_no());
            } else if (dobj.getPur_cd().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD)) || dobj.getPur_cd().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEW_COUNTER_SIGNATURE_PUR_CD))) {
                updateSetVaCounterSignature(tmgr, rcpt_no, dobj.getAppl_no());
            } else if (dobj.getPur_cd().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_LEASE_PUR_CD))) {
                updateSetVaLeasePermit(tmgr, rcpt_no, dobj.getAppl_no());
            } else if (dobj.getPur_cd().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_COUNTER_SIGNATURE_AUTH_PUR_CD))) {
                updateSetVaCounterSignatureAuth(tmgr, rcpt_no, dobj.getAppl_no());
            }

            status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
            status.setStatus(TableConstants.STATUS_COMPLETE);
            status.setAppl_no(dobj.getAppl_no());
            status.setOffice_remark("Fee Submitted");
            status.setPublic_remark("");
            status.setState_cd(Util.getUserStateCode());
            status.setPur_cd(Integer.parseInt(dobj.getPur_cd()));
            if (dobj.getPur_cd().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_FRESH_PUR_CD))) {
                VehicleParameters vehicleParametersForNewApp = new VehicleParameters();
                vehicleParametersForNewApp.setREGN_NO(dobj.getRegn_no());
                status.setVehicleParameters(vehicleParametersForNewApp);
            }
            ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, dobj.getAppl_no(),
                    TableConstants.TM_ROLE_PMT_FEE, Integer.parseInt(dobj.getPur_cd()), null, tmgr);
            ServerUtil.fileFlow(tmgr, status);
            if (("DL,UP,GJ").contains(Util.getUserStateCode()) && dobj != null && !CommonUtils.isNullOrBlank(dobj.getPermit_type()) && dobj.getPermit_type().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT)) {
                for (PermitShowFeeDetailDobj feeDobj : pmt_fee_dobj.getListPmtFeeDetails()) {
                    if (feeDobj.getPurCd().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD)) || feeDobj.getPurCd().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD)) || feeDobj.getPurCd().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_FRESH_PUR_CD)) || feeDobj.getPurCd().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD)) || feeDobj.getPurCd().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_CANCELATION_PUR_CD))) {
                        dobj.setPaymentStaus("Y");
                        insertNpPermitDetails(dobj, null, tmgr);
                        break;
                    }
                }
            }
            ServerUtil.insertForQRDetails(dobj.getAppl_no(), null, null, rcpt_no, false, DocumentType.RECEIPT_QR, Util.getUserStateCode(), Util.getUserOffCode(), tmgr);
            tmgr.commit();
        } catch (VahanException ex) {
            throw new VahanException(ex.getMessage());
        } catch (NumberFormatException ex) {
            throw new VahanException(ex.getMessage());
        } catch (Exception e) {
            rcpt_no = "";
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
        return rcpt_no;
    }

    public String onlinePayment(PermitFeeDobj dobj, CommonPermitFeeDobj pmt_fee_dobj, FeeDraftDobj feeDraftDobj, boolean printOfferLetter, String mobileNo) throws VahanException {
        String onlinePaymentMsg = "";
        TransactionManager tmgr = null;
        int checkTotalAmount = 0;
        List<TaxExemptiondobj> fineExemList = new ArrayList<>();
        try {
            tmgr = new TransactionManager("saveOnlineCashDetails");
            long user_cd = ServerUtil.getUniqueUserCd(tmgr, Util.getUserStateCode());

            //System.out.println("user Password : " + userPwd);
            if (user_cd != 0) {
                String strSQL = "SELECT USER_CD FROM " + TableList.VP_RCPT_CART + " WHERE user_cd= ? ";
                PreparedStatement psmt = tmgr.prepareStatement(strSQL);
                psmt.setLong(1, user_cd);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    throw new VahanException("Problem occured during generating online info.");
                }
            }
            StringBuffer userName = new StringBuffer(String.valueOf(user_cd).substring(0, 4).toLowerCase());
            StringBuffer userPwd = userName.append(UUID.randomUUID().toString().substring(0, 5));
            String userPassSql = " INSERT INTO vp_online_pay_user_info(state_cd, off_cd, user_cd, user_pwd, mobile_no, created_by,created_dt,appl_no) \n"
                    + " VALUES (?, ?, ?, ?, ?, ?, current_timestamp,?) ";

            PreparedStatement psUserPass = tmgr.prepareStatement(userPassSql);
            int m = 1;
            psUserPass.setString(m++, Util.getUserStateCode());
            psUserPass.setInt(m++, Util.getUserSeatOffCode());
            psUserPass.setLong(m++, user_cd);
            psUserPass.setString(m++, userPwd.toString());
            psUserPass.setLong(m++, Long.parseLong(mobileNo));
            psUserPass.setLong(m++, Long.parseLong(Util.getEmpCode()));
            psUserPass.setString(m++, pmt_fee_dobj.getAppl_no());
            psUserPass.executeUpdate();
            if (printOfferLetter) {
                String offerLetterNo = ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getUserOffCode(), Integer.parseInt(dobj.getPermit_type()), 0, "O");
                if (CommonUtils.isNullOrBlank(offerLetterNo) || ("").equalsIgnoreCase(offerLetterNo)) {
                    throw new VahanException("Offer Letter not genrated");
                } else {
                    dobj.setOffer_no(offerLetterNo);
                    String docId = CommonPermitPrintImpl.getPermitDocIdForQuery(CommonPermitPrintImpl.getPermitDocId(tmgr, 0, TableConstants.VM_PMT_APPLICATION_PUR_CD, Util.getUserStateCode()));
                    String[] beanData = {docId, dobj.getAppl_no(), dobj.getRegn_no()};
                    CommonPermitPrintImpl.insertVaPermitPrint(tmgr, beanData);
                    String message = "Permit Order No " + offerLetterNo + " generated against Application No " + dobj.getAppl_no();
                    ServerUtil.insertUsersTransactionMessage(message, 1, tmgr);
                }
            }

            if (("NEW").equalsIgnoreCase(dobj.getRegn_no()) && !printOfferLetter) {
                updateVA_Permit_Owner(tmgr, dobj.getNew_regn_no(), dobj.getAppl_no());
            } else if (("NEW").equalsIgnoreCase(dobj.getRegn_no()) && printOfferLetter) {
                updateOfferVtPermit(tmgr, dobj.getOffer_no(), dobj.getAppl_no());
            }

            PassengerPermitDetailDobj permitDobj = new PassengerPermitDetailDobj();
            HashMap fineMap=new HashMap();
            boolean isExemFeeFine = false;
            for (PermitShowFeeDetailDobj feeShowPanal : pmt_fee_dobj.getListPmtFeeDetails()) {
                if (feeShowPanal.isExem_fee_fine()) {
                    isExemFeeFine = true;
                    break;
                }
            }
            for (PermitShowFeeDetailDobj feeShowPanal : pmt_fee_dobj.getListPmtFeeDetails()) {
                String addTocartSql = "INSERT INTO vp_rcpt_cart(state_cd,off_cd,user_cd, appl_no, pur_cd, period_mode, period_from, period_upto,"
                        + " amount, exempted, rebate, surcharge, penalty, interest,pmt_type, pmt_catg, service_type, route_class,"
                        + " route_length, no_of_trips, domain_cd, distance_run_in_quarter,op_dt) "
                        + " VALUES (?,?,?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,current_timestamp)";

                if (!feeShowPanal.isExem_fee_fine() && Integer.parseInt(feeShowPanal.getPurCd()) != TableConstants.FEE_FINE_EXEMTION) {
                    if (isExemFeeFine && (Integer.parseInt(feeShowPanal.getPurCd()) == TableConstants.VM_PMT_RENEWAL_PUR_CD || Integer.parseInt(feeShowPanal.getPurCd()) == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD)) {
                        fineMap.put(feeShowPanal.getPurCd(), new String(feeShowPanal.getPenalty()));
                        feeShowPanal.setPenalty("0");
                    }
                    PreparedStatement psAddtoCart = tmgr.prepareStatement(addTocartSql);
                    int i = 1;
                    psAddtoCart.setString(i++, Util.getUserStateCode());
                    psAddtoCart.setInt(i++, Util.getSelectedSeat().getOff_cd());
                    psAddtoCart.setLong(i++, user_cd);
                    psAddtoCart.setString(i++, pmt_fee_dobj.getAppl_no());
                    psAddtoCart.setInt(i++, Integer.parseInt(feeShowPanal.getPurCd()));
                    if (Util.getUserStateCode().equalsIgnoreCase("CG") && Integer.parseInt(feeShowPanal.getPurCd()) == TableConstants.VM_PMT_DIFFERENTIAL_SPECIAL_TAX) {
                        psAddtoCart.setString(i++, "M");
                    } else {
                        psAddtoCart.setString(i++, null);
                    }
                    psAddtoCart.setDate(i++, null);
                    psAddtoCart.setDate(i++, null);
                    psAddtoCart.setLong(i++, Integer.parseInt(feeShowPanal.getPermitAmt()));
                    checkTotalAmount += (Integer.parseInt(feeShowPanal.getPermitAmt()) + Integer.parseInt(feeShowPanal.getPenalty()));
                    psAddtoCart.setLong(i++, 0);
                    psAddtoCart.setLong(i++, 0);
                    psAddtoCart.setLong(i++, 0);
                    psAddtoCart.setLong(i++, Integer.parseInt(feeShowPanal.getPenalty()));//fine
                    psAddtoCart.setLong(i++, 0);//fine
                    if (permitDobj != null && permitDobj.getPmt_type_code() != null && !permitDobj.getPmt_type_code().equals("")) {
                        psAddtoCart.setInt(i++, Integer.parseInt(permitDobj.getPmt_type_code()));
                    } else {
                        psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                    }

                    if (permitDobj != null && permitDobj.getPmtCatg() != null && !permitDobj.getPmtCatg().equals("")) {
                        psAddtoCart.setInt(i++, Integer.parseInt(permitDobj.getPmtCatg()));
                    } else {
                        psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                    }

                    if (permitDobj != null && permitDobj.getServices_TYPE() != null && !permitDobj.getServices_TYPE().equals("")) {
                        psAddtoCart.setInt(i++, Integer.parseInt(permitDobj.getServices_TYPE()));
                    } else {
                        psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                    }

                    if (permitDobj != null && permitDobj.getRout_code() != null && !permitDobj.getRout_code().equals("")) {
                        psAddtoCart.setInt(i++, Integer.parseInt(permitDobj.getRout_code()));
                    } else {
                        psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                    }

                    if (permitDobj != null && permitDobj.getRout_length() != null && !permitDobj.getRout_length().equals("")) {
                        psAddtoCart.setInt(i++, Integer.parseInt(permitDobj.getRout_length()));
                    } else {
                        psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                    }

                    if (permitDobj != null && permitDobj.getNumberOfTrips() != null && !permitDobj.getNumberOfTrips().equals("")) {
                        psAddtoCart.setInt(i++, Integer.parseInt(permitDobj.getNumberOfTrips()));
                    } else {
                        psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                    }

                    if (permitDobj != null && permitDobj.getDomain_CODE() != null && !permitDobj.getDomain_CODE().equals("")) {
                        psAddtoCart.setInt(i++, Integer.parseInt(permitDobj.getDomain_CODE()));
                    } else {
                        psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                    }

                    psAddtoCart.setNull(i++, java.sql.Types.INTEGER);
                    ServerUtil.validateQueryResult(tmgr, psAddtoCart.executeUpdate(), psAddtoCart);

                }
            }
            for (PermitShowFeeDetailDobj feeShow : pmt_fee_dobj.getListPmtFeeDetails()) {
                if (feeShow.isExem_fee_fine() && Integer.parseInt(feeShow.getPurCd()) == TableConstants.FEE_FINE_EXEMTION) {
                    TaxExemptiondobj exem_dobj = new TaxExemptiondobj();
                    exem_dobj.setPur_cd(TableConstants.FEE_FINE_EXEMTION);
                    if (feeShow.getPermitAmt().contains("-")) {
                        exem_dobj.setExemFeeAmount(Integer.parseInt(feeShow.getPermitAmt().replace("-", "")));
                    }
                    if (feeShow.getPenalty().contains("-")) {
                        exem_dobj.setExemAmount(Integer.parseInt(feeShow.getPenalty().replace("-", "")));
                    }
                    exem_dobj.setPermissionNo(pmt_fee_dobj.getRegn_no());
                    exem_dobj.setPermissionDt(new Date());
                    exem_dobj.setPerpose("COVID-19");
                    fineExemList.add(exem_dobj);
                }
                String penalty=(String)fineMap.get(feeShow.getPurCd());
                if (isExemFeeFine && Integer.parseInt(feeShow.getPurCd()) == TableConstants.VM_PMT_RENEWAL_PUR_CD && Integer.parseInt(penalty) > 0) {
                    TaxExemptiondobj exem_dobj = new TaxExemptiondobj();
                    exem_dobj.setPur_cd(TableConstants.VM_PMT_RENEWAL_PUR_CD);
                    if (feeShow.getPermitAmt().contains("-")) {
                        exem_dobj.setExemFeeAmount(Integer.parseInt(feeShow.getPermitAmt()));
                    }
                    exem_dobj.setExemAmount(Integer.parseInt((String)fineMap.get(feeShow.getPurCd())));
                    exem_dobj.setPermissionNo(pmt_fee_dobj.getRegn_no());
                    exem_dobj.setPermissionDt(new Date());
                    exem_dobj.setPerpose("COVID-19");
                    fineExemList.add(exem_dobj);
                }
                if (isExemFeeFine && Integer.parseInt(feeShow.getPurCd()) == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD && Integer.parseInt(penalty) > 0) {
                    TaxExemptiondobj exem_dobj = new TaxExemptiondobj();
                    exem_dobj.setPur_cd(TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD);
//                    if (feeShow.getPermitAmt().contains("-")) {
//                        exem_dobj.setExemFeeAmount(Integer.parseInt(feeShow.getPermitAmt()));
//                    }
                    exem_dobj.setExemAmount(Integer.parseInt((String)fineMap.get(feeShow.getPurCd())));
                    exem_dobj.setPermissionNo(pmt_fee_dobj.getRegn_no());
                    exem_dobj.setPermissionDt(new Date());
                    exem_dobj.setPerpose("COVID-19");
                    fineExemList.add(exem_dobj);
                }
            }
            if (fineExemList.size() > 0) {

                new ExemptionFeeFineImpl().saveExemptionFeeFineDetails(tmgr, pmt_fee_dobj.getAppl_no(), fineExemList, pmt_fee_dobj.getState_cd(), Integer.parseInt(pmt_fee_dobj.getOff_cd()));
            }
            String selectSql = "select sum(vp.amount+vp.surcharge+vp.penalty+vp.interest-vp.exempted-vp.rebate) as amount from vp_rcpt_cart vp where appl_no = ? and user_cd = ? ";
            PreparedStatement psSelectVpRcptCart = tmgr.prepareStatement(selectSql);
            int i = 1;
            psSelectVpRcptCart.setString(i++, pmt_fee_dobj.getAppl_no());
            psSelectVpRcptCart.setLong(i++, user_cd);
            RowSet rsSelectVpRcptcart = tmgr.fetchDetachedRowSet_No_release();
            if (rsSelectVpRcptcart.next()) {
                long amount = rsSelectVpRcptcart.getLong("amount");
                if (amount == checkTotalAmount) {
                    String mobileSms = "Please visit https://vahan.parivahan.gov.in/vahan and select Online Payment option. User ID : " + pmt_fee_dobj.getAppl_no() + " and Password: " + userPwd + " .Valid for one day only.";
                    ServerUtil.sendSMS(mobileNo, mobileSms);
                    tmgr.commit();
                    onlinePaymentMsg = userPwd.toString();
                } else {
                    throw new VahanException("Problem in Saving Details!");
                }
            }
        } catch (VahanException ve) {
            throw ve;
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
        return onlinePaymentMsg;
    }

    public String getOffereLetterNo(String applNo) {
        String offerLetterNo = "", Query = "";
        RowSet rs;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getOffereLetterNo");
            Query = "SELECT  offer_no FROM " + TableList.VA_PERMIT + " where appl_no = ? and alloted_flag = 'A'";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                offerLetterNo = rs.getString("offer_no");
            }
        } catch (SQLException e) {
            offerLetterNo = "";
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
        return offerLetterNo;
    }

    public String[] getNewOwnerDetails(String regnNo, String state_cd) {
        String[] ownerNewDetails = new String[3];
        String Query;
        RowSet rs;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getOffereLetterNo");
            Query = "SELECT OWNER_NAME,CHASI_NO,VH_CLASS FROM VT_OWNER WHERE REGN_NO = ? and state_cd = ? and status in ('A','Y') ";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regnNo);
            ps.setString(2, state_cd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                ownerNewDetails[0] = rs.getString("OWNER_NAME");
                ownerNewDetails[1] = rs.getString("CHASI_NO");
                ownerNewDetails[2] = rs.getString("VH_CLASS");
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
        return ownerNewDetails;
    }

    public String getRegnNoForReplacement(String regnNo) {
        String registrationNo = "";
        String Query;
        RowSet rs;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("get RegnNo For Replacement");
            Query = "SELECT regn_no FROM " + TableList.VA_PERMIT_TRANSACTION + " where new_regn_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regnNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                registrationNo = rs.getString("regn_no");
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
        return registrationNo;
    }

    public void feeSkip(PermitFeeDobj dobj, long totalAmount) throws VahanException {
        TransactionManager tmgr = null;
        RowSet rs;
        PreparedStatement ps;
        int count = 0;
        try {
            tmgr = new TransactionManager("feeSkip");
            if (dobj == null) {
                throw new VahanException("Permit information not found. Please Contact to system administrator.");
            }
            Status_dobj status = new Status_dobj();
            String Query = " SELECT  string_agg(rcpt_no,'') as rcpt_no,sum (fee) as fee,sum(fine)as fine  from  \n"
                    + " (SELECT rcpt_no , sum(fees) as fee ,sum(fine) as fine from " + TableList.VT_FEE + " where \n"
                    + " (state_cd,off_cd,rcpt_no) in (SELECT state_cd,off_cd,rcpt_no from " + TableList.VT_FEE + "  where (state_cd,off_cd,rcpt_no) in (SELECT state_cd,off_cd,rcpt_no from " + TableList.VP_APPL_RCPT_MAPPING + "  where \n"
                    + " appl_no in (SELECT appl_no from " + TableList.VA_DETAILS + "  where regn_no=? and pur_cd in (?,?) and state_cd = ?)) and pur_cd IN (?,?)) and pur_cd in (?,?,?,?,?,?,?) group by rcpt_no \n"
                    + " union \n"
                    + " SELECT ''rcpt_no , sum(fees) as fee ,sum(fine) as fine from " + TableList.VT_FEE + " where \n"
                    + " (state_cd,off_cd,rcpt_no) in (SELECT state_cd,off_cd,rcpt_no from " + TableList.VT_FEE + "  where (state_cd,off_cd,rcpt_no) in (SELECT state_cd,off_cd,rcpt_no from " + TableList.VP_APPL_RCPT_MAPPING + "  where \n"
                    + "  appl_no =? ) and pur_cd IN (?,?,?,?)) and pur_cd in (?,?,?,?) group by rcpt_no \n"
                    + "  ) a ";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, dobj.getRegn_no().toUpperCase());
            ps.setInt(i++, TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE);
            ps.setInt(i++, TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE);
            ps.setString(i++, dobj.getState_cd());
            ps.setInt(i++, TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE);
            ps.setInt(i++, TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE);
            ps.setInt(i++, TableConstants.VM_PMT_FRESH_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_APPLICATION_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_SURCHARG_FEE);
            ps.setInt(i++, TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD);
            ps.setInt(i++, TableConstants.VM_TRANSACTION_MAST_USER_CHARGES);
            ps.setInt(i++, TableConstants.PERMIT_PAPER_DOCUMENT_CHARGE);
            ps.setInt(i++, TableConstants.VM_TRANSACTION_MAST_ALL);
            ps.setString(i++, dobj.getAppl_no().toUpperCase());
            ps.setInt(i++, TableConstants.VM_PMT_APPLICATION_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_SURCHARG_FEE);
            ps.setInt(i++, TableConstants.PERMIT_PAPER_DOCUMENT_CHARGE);
            ps.setInt(i++, TableConstants.VM_PMT_APPLICATION_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD);
            ps.setInt(i++, TableConstants.VM_PMT_SURCHARG_FEE);
            ps.setInt(i++, TableConstants.PERMIT_PAPER_DOCUMENT_CHARGE);
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                if ((rs.getLong("fee") + rs.getLong("fine")) < totalAmount) {
                    throw new VahanException("Fee Not Match.");
                }
                updateSetVaPermit(dobj, tmgr, rs.getString("rcpt_no"));
                status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
                status.setStatus(TableConstants.STATUS_COMPLETE);
                status.setAppl_no(dobj.getAppl_no().toUpperCase());
                status.setOffice_remark("Fee Submitted");
                status.setPublic_remark("");
                status.setState_cd(Util.getUserStateCode());
                status.setPur_cd(Integer.valueOf(dobj.getPur_cd()));
                ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, dobj.getAppl_no(),
                        TableConstants.TM_ROLE_PMT_FEE, Integer.valueOf(dobj.getPur_cd()), null, tmgr);
                ServerUtil.fileFlow(tmgr, status);
                count++;
            }
            if (count > 1) {
                throw new VahanException("Mutiple records found in data base. Please contact to system administrator.");
            } else if (count == 1) {
                tmgr.commit();
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
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

    public boolean checkPmtissuedAfterFeePaidAtNewRegn(String regn_no) throws VahanException {
        TransactionManager tmgr = null;
        RowSet rs;
        PreparedStatement ps;
        RowSet rs1;
        Date regn_dt = null;
        Date fresh_pmt_dt = null;
        boolean flag = false;

        try {
            tmgr = new TransactionManager("checkPmtissuedAfterFeePaidAtNewRegn");
            String Query = "SELECT appl_no,pur_cd,confirm_date from " + TableList.VA_DETAILS + " where state_cd = ? and regn_no=? and pur_cd = ? and entry_status='A' order by confirm_date desc limit 1";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, Util.getUserStateCode());
            ps.setString(i++, regn_no.toUpperCase());
            ps.setInt(i++, TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                regn_dt = rs.getDate("confirm_date");
                Query = "SELECT appl_no,pur_cd,confirm_date from " + TableList.VA_DETAILS + " where state_cd = ? and regn_no=? and pur_cd = ? and entry_status='A' order by appl_dt limit 1";
                ps = tmgr.prepareStatement(Query);
                i = 1;
                ps.setString(i++, Util.getUserStateCode());
                ps.setString(i++, regn_no.toUpperCase());
                ps.setInt(i++, TableConstants.VM_PMT_FRESH_PUR_CD);
                rs1 = tmgr.fetchDetachedRowSet_No_release();
                if (rs1.next()) {
                    fresh_pmt_dt = rs1.getDate("confirm_date");
                }
            }

            if (regn_dt != null && fresh_pmt_dt != null && fresh_pmt_dt.after(regn_dt)) {
                flag = true;
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
        return flag;
    }

    public void insertNpPermitDetails(PermitFeeDobj dobj, String trans_status, TransactionManager tmgr) throws VahanException {
        String pmt_from = null;
        String pmt_upto = null;
        String auth_fr = null;
        String auth_to = null;
        String auth_no = "";
        String pmt_no = "";
        ResultSet rs = null;
        PreparedStatement psUserPass = null;
        int purcd = Integer.parseInt(dobj.getPur_cd());
        try {
            if (purcd == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD || (purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD && dobj != null && dobj.getAppl_no() != null)) {
                PassengerPermitDetailDobj passDobj = new PassengerPermitDetailImpl().set_permit_appl_db_to_dobj(dobj.getAppl_no());
                if (passDobj != null && passDobj.getPaction().equalsIgnoreCase(String.valueOf(TableConstants.VM_PMT_RENEWAL_PUR_CD))) {
                    OwnerImpl ownImpl = new OwnerImpl();
                    Owner_dobj ownDobj = ownImpl.set_Owner_appl_db_to_dobj(dobj.getRegn_no(), null, null, 0);
                    ownDobj.setPmt_type(Integer.parseInt(passDobj.getPmt_type()));
                    ownDobj.setPmt_catg(Integer.parseInt(passDobj.getPmtCatg()));
                    int vehAge = new FitnessImpl().getVehAgeValidity(ownDobj);
                    Date maxValidUpto = null;
                    if (vehAge != 99) {
                        maxValidUpto = ServerUtil.dateRange(ownDobj.getPurchase_dt(), vehAge, 0, -1);

                    }
                    String query = " Select c.period_mode,c.period,a.valid_from ,a.valid_upto,a.pmt_no ,b.auth_no,\n"
                            + "   b.auth_fr , b.auth_to from  permit.vt_permit a \n"
                            + "   inner join permit.va_permit c on c.state_cd = a.state_cd and c.regn_no = a.regn_no \n"
                            + "   inner join permit.vt_permit_home_auth b on b.pmt_no = a.pmt_no and b.regn_no = a.regn_no \n"
                            + "   where c.appl_no=? and  a.state_cd=? and a.regn_no=? ";
                    psUserPass = tmgr.prepareStatement(query);
                    psUserPass.setString(1, dobj.getAppl_no());
                    psUserPass.setString(2, Util.getUserStateCode());
                    psUserPass.setString(3, dobj.getRegn_no());
                    rs = psUserPass.executeQuery();
                    if (rs.next()) {
                        if (rs.getString("period_mode").equalsIgnoreCase("Y")) {
                            pmt_from = DateUtil.parseDateYYYYMMDDToString(ServerUtil.dateRange(rs.getDate("valid_upto"), 0, 0, 1));
                            pmt_upto = DateUtil.parseDateYYYYMMDDToString(ServerUtil.dateRange((ServerUtil.dateRange(rs.getDate("valid_upto"), 0, 0, 1)), rs.getInt("period"), 0, -1));
                        } else if (rs.getString("period_mode").equalsIgnoreCase("M")) {
                            pmt_from = DateUtil.parseDateYYYYMMDDToString(ServerUtil.dateRange(rs.getDate("valid_upto"), 0, 0, 1));
                            pmt_upto = DateUtil.parseDateYYYYMMDDToString(ServerUtil.dateRange((ServerUtil.dateRange(rs.getDate("valid_upto"), 0, 0, 1)), 0, rs.getInt("period"), -1));
                        }

                        pmt_no = rs.getString("pmt_no");
                        auth_no = "";
                        auth_fr = DateUtil.parseDateYYYYMMDDToString(ServerUtil.dateRange(rs.getDate("auth_to"), 0, 0, 1));;
                        auth_to = DateUtil.parseDateYYYYMMDDToString(ServerUtil.dateRange(rs.getDate("auth_to"), 1, 0, 0));
                        if (maxValidUpto != null && maxValidUpto.compareTo(ServerUtil.dateRange(rs.getDate("auth_to"), 1, 0, 0)) <= 0) {
                            auth_to = DateUtil.parseDateYYYYMMDDToString(maxValidUpto);
                        }
                        if (maxValidUpto != null && maxValidUpto.compareTo(JSFUtils.getStringToDateyyyyMMdd(pmt_upto)) <= 0) {
                            pmt_upto = DateUtil.parseDateYYYYMMDDToString(maxValidUpto);
                        }
                    }

                } else {
                    String query = " Select to_char(a.valid_from,'yyyy-MM-dd') as valid_from ,to_char(a.valid_upto,'yyyy-MM-dd') as valid_upto,a.pmt_no ,b.auth_no,"
                            + " to_char(b.auth_fr,'yyyy-MM-dd') as auth_fr , to_char(b.auth_to,'yyyy-MM-dd') as auth_to from  permit.vt_permit a "
                            + " left outer join permit.va_permit_home_auth b on b.pmt_no = a.pmt_no and b.regn_no = a.regn_no"
                            + " where b.appl_no=? and  a.state_cd=? and a.regn_no=? ";
                    psUserPass = tmgr.prepareStatement(query);
                    psUserPass.setString(1, dobj.getAppl_no());
                    psUserPass.setString(2, Util.getUserStateCode());
                    psUserPass.setString(3, dobj.getRegn_no());
                    rs = psUserPass.executeQuery();
                    if (rs.next()) {
                        pmt_from = rs.getString("valid_from");
                        pmt_upto = rs.getString("valid_upto");
                        pmt_no = rs.getString("pmt_no");
                        auth_no = rs.getString("auth_no");
                        auth_fr = rs.getString("auth_fr");
                        auth_to = rs.getString("auth_to");
                    }
                }
            }
            if (purcd == TableConstants.VM_PMT_CANCELATION_PUR_CD || purcd == TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD) {
                String query = " Select to_char(a.valid_from,'yyyy-MM-dd') as valid_from ,to_char(a.valid_upto,'yyyy-MM-dd') as valid_upto,a.pmt_no ,b.auth_no,"
                        + " to_char(b.auth_fr,'yyyy-MM-dd') as auth_fr , to_char(b.auth_to,'yyyy-MM-dd') as auth_to from  permit.vt_permit a "
                        + " left outer join permit.vt_permit_home_auth b on b.pmt_no = a.pmt_no and b.regn_no = a.regn_no "
                        + " where  a.state_cd=? and a.regn_no=? ";
                psUserPass = tmgr.prepareStatement(query);
                psUserPass.setString(1, Util.getUserStateCode());
                psUserPass.setString(2, dobj.getRegn_no());
                rs = psUserPass.executeQuery();
                if (rs.next()) {
                    pmt_from = rs.getString("valid_from");
                    pmt_upto = rs.getString("valid_upto");
                    pmt_no = rs.getString("pmt_no");
                    auth_no = rs.getString("auth_no");
                    auth_fr = rs.getString("auth_fr");
                    auth_to = rs.getString("auth_to");
                }
            }
            if (purcd == TableConstants.VM_PMT_FRESH_PUR_CD) {
                psUserPass = null;
                String query = " INSERT INTO permit.va_np_detail(state_cd, off_cd, appl_no, pmt_no, regn_no, valid_from,valid_upto,auth_no,auth_from,auth_upto,pur_cd,permit_type,permit_catg,paymentstatus,trans_status) \n"
                        + " VALUES (?, ?, ?," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "''" : pmt_no) + " , ?," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date" : pmt_from) + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date + interval  '5 year'" : pmt_upto)
                        + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "''" : auth_no) + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date" : auth_fr) + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date -1  + interval  '1 year'" : auth_to) + ",?,?,?,?,?) ";
                psUserPass = tmgr.prepareStatement(query);

                int m = 1;
                psUserPass.setString(m++, Util.getUserStateCode());
                psUserPass.setInt(m++, Util.getUserSeatOffCode());
                psUserPass.setString(m++, dobj.getAppl_no());
                psUserPass.setString(m++, dobj.getRegn_no());
                psUserPass.setInt(m++, Integer.parseInt(dobj.getPur_cd()));
                psUserPass.setInt(m++, Integer.parseInt(dobj.getPermit_type()));
                psUserPass.setInt(m++, dobj.getPermit_catg());
                psUserPass.setString(m++, dobj.getPaymentStaus());
                psUserPass.setString(m++, trans_status);
                psUserPass.executeUpdate();
            }
            if (purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD && pmt_from != null && pmt_upto != null && auth_fr != null && auth_to != null) {
                psUserPass = null;
                String sql = "Select * from permit.va_np_detail where regn_no = ? and appl_no=?";
                psUserPass = tmgr.prepareStatement(sql);
                psUserPass.setString(1, dobj.getRegn_no());
                psUserPass.setString(2, dobj.getAppl_no());
                RowSet rowSet = tmgr.fetchDetachedRowSet_No_release();
                if (rowSet.next()) {
                    sql = "update permit.va_np_detail set pmt_no=?,valid_from=?,valid_upto=?,auth_no=?,auth_from=?,auth_upto=? WHERE REGN_NO =? and appl_no=?";
                    psUserPass = tmgr.prepareStatement(sql);
                    psUserPass.setString(1, pmt_no);
                    psUserPass.setDate(2, new java.sql.Date(JSFUtils.getStringToDateyyyyMMdd(pmt_from).getTime()));
                    psUserPass.setDate(3, new java.sql.Date(JSFUtils.getStringToDateyyyyMMdd(pmt_upto).getTime()));
                    psUserPass.setString(4, auth_no);
                    psUserPass.setDate(5, new java.sql.Date(JSFUtils.getStringToDateyyyyMMdd(auth_fr).getTime()));
                    psUserPass.setDate(6, new java.sql.Date(JSFUtils.getStringToDateyyyyMMdd(auth_to).getTime()));
                    psUserPass.setString(7, dobj.getRegn_no());
                    psUserPass.setString(8, dobj.getAppl_no());
                    psUserPass.executeUpdate();
                } else {
                    pmt_from = "'" + pmt_from + "'";
                    pmt_upto = "'" + pmt_upto + "'";
                    auth_fr = "'" + auth_fr + "'";
                    auth_to = "'" + auth_to + "'";
                    auth_no = "'" + auth_no + "'";
                    pmt_no = "'" + pmt_no + "'";
                    psUserPass = null;
                    String query = " INSERT INTO permit.va_np_detail(state_cd, off_cd, appl_no, pmt_no, regn_no, valid_from,valid_upto,auth_no,auth_from,auth_upto,pur_cd,permit_type,permit_catg,paymentstatus,trans_status) \n"
                            + " VALUES (?, ?, ?," + pmt_no + " , ?," + pmt_from + "," + pmt_upto
                            + "," + auth_no + "," + auth_fr + "," + auth_to + ",?,?,?,?,?) ";
                    psUserPass = tmgr.prepareStatement(query);
                    int m = 1;
                    psUserPass.setString(m++, Util.getUserStateCode());
                    psUserPass.setInt(m++, Util.getUserSeatOffCode());
                    psUserPass.setString(m++, dobj.getAppl_no());
                    psUserPass.setString(m++, dobj.getRegn_no());
                    psUserPass.setInt(m++, Integer.parseInt(dobj.getPur_cd()));
                    psUserPass.setInt(m++, Integer.parseInt(dobj.getPermit_type()));
                    psUserPass.setInt(m++, dobj.getPermit_catg());
                    psUserPass.setString(m++, dobj.getPaymentStaus());
                    psUserPass.setString(m++, trans_status);
                    psUserPass.executeUpdate();
                }
            }
            if (purcd != TableConstants.VM_PMT_FRESH_PUR_CD && purcd != TableConstants.VM_PMT_RENEWAL_PUR_CD && pmt_from != null && pmt_upto != null && auth_fr != null && auth_to != null) {
                psUserPass = null;
                pmt_from = "'" + pmt_from + "'";
                pmt_upto = "'" + pmt_upto + "'";
                auth_fr = "'" + auth_fr + "'";
                auth_to = "'" + auth_to + "'";
                auth_no = "'" + auth_no + "'";
                pmt_no = "'" + pmt_no + "'";
                psUserPass = null;
                String query = " INSERT INTO permit.va_np_detail(state_cd, off_cd, appl_no, pmt_no, regn_no, valid_from,valid_upto,auth_no,auth_from,auth_upto,pur_cd,permit_type,permit_catg,paymentstatus,trans_status) \n"
                        + " VALUES (?, ?, ?," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "''" : pmt_no) + " , ?," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date" : pmt_from) + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date + interval  '5 year'" : pmt_upto)
                        + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "''" : auth_no) + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date" : auth_fr) + "," + (purcd == TableConstants.VM_PMT_FRESH_PUR_CD || purcd == TableConstants.VM_PMT_RENEWAL_PUR_CD ? "current_date -1  + interval  '1 year'" : auth_to) + ",?,?,?,?,?) ";
                psUserPass = tmgr.prepareStatement(query);

                int m = 1;
                psUserPass.setString(m++, Util.getUserStateCode());
                psUserPass.setInt(m++, Util.getUserSeatOffCode());
                psUserPass.setString(m++, dobj.getAppl_no());
                psUserPass.setString(m++, dobj.getRegn_no());
                psUserPass.setInt(m++, Integer.parseInt(dobj.getPur_cd()));
                psUserPass.setInt(m++, Integer.parseInt(dobj.getPermit_type()));
                psUserPass.setInt(m++, dobj.getPermit_catg());
                psUserPass.setString(m++, dobj.getPaymentStaus());
                psUserPass.setString(m++, trans_status);
                psUserPass.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.toString());
        }
    }

    public PermitFeeDobj getDetailsCounterSignatureAuth(String appNo) throws VahanException {
        PermitFeeDobj pmtfee_dobj = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getDetailsCounterSignature");
            ps = tmgr.prepareStatement("select * from " + TableList.VA_PERMIT_COUNTERSIGNATURE_AUTHORIZATION + " where appl_no =?");
            ps.setString(1, appNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                pmtfee_dobj = new PermitFeeDobj();
                pmtfee_dobj.setRegn_no(rs.getString("regn_no"));
                pmtfee_dobj.setPermit_type(rs.getString("pmt_type"));
                pmtfee_dobj.setPur_cd("" + rs.getInt("pur_cd"));
                pmtfee_dobj.setPeriod(rs.getString("period"));
                pmtfee_dobj.setPeriod_mode(rs.getString("period_mode"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    throw new VahanException(e.toString());
                }
            }
        }
        return pmtfee_dobj;
    }

    public boolean cancelFineExemPayment(String user_cd, String appl_no) throws VahanException {
        boolean flag = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("cancelFineExemPayment");
            if (new ExemptionFeeFineImpl().isFineExemptionDetailsExist(appl_no)) {
                new ExemptionFeeFineImpl().moveDataIntoHistory(tmgr, appl_no, user_cd);
                String query = "Delete from " + TableList.VA_FEE_EXEMTION + " Where appl_no =?";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, appl_no);
                ps.executeUpdate();
                tmgr.commit();
                flag = true;
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

        return flag;

    }
}
