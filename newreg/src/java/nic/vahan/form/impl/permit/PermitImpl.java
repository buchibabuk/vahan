/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC094
 */
public class PermitImpl {

    private static final Logger LOGGER = Logger.getLogger(PermitImpl.class);

    /**
     *
     * @param applNo Application Number
     * @param regnNo Registration Number
     * @param tableConstant Table Constant Used as flag
     * @param vhClass Vehicle Class
     * @return
     */
    public PassengerPermitDetailDobj getPermitDetails(String applNo, String regnNo, int tableConstant) {
        PassengerPermitDetailDobj permitDobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("Owner_Impl");
            String query;
            String parameterValue;
            if (TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE == tableConstant
                    || TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE == tableConstant
                    || TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE_FITNESS == tableConstant) {

                if (applNo != null || !applNo.isEmpty()) {
                    query = "select * from " + TableList.VA_PERMIT + "  where appl_no=?";
                    parameterValue = applNo;
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    RowSet rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        permitDobj = new PassengerPermitDetailDobj();
                        permitDobj.setDomain_CODE(rs.getString("domain_cd"));
                        permitDobj.setPmt_type_code(rs.getString("pmt_type"));
                        permitDobj.setPmtCatg(rs.getString("pmt_catg"));
                        permitDobj.setServices_TYPE(rs.getString("service_type"));
                        permitDobj.setPeriod(rs.getString("period"));
                    }
                }
            } else {
                if (regnNo != null || !regnNo.isEmpty()) {
                    query = "select * from " + TableList.VT_PERMIT + "  where regn_no=?";
                    parameterValue = regnNo;
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, parameterValue);
                    RowSet rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        permitDobj = new PassengerPermitDetailDobj();
                        permitDobj.setDomain_CODE(rs.getString("domain_code"));
                        permitDobj.setPmt_type_code(rs.getString("type"));
                        permitDobj.setPmtCatg(rs.getString("pmt_catg"));
                        permitDobj.setServices_TYPE(rs.getString("service_type"));
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.equals(e);
            }
        }
        return permitDobj;
    }

    public static void getVaPermitOwnerDetails(String loiNo, String stateCd, int offCd, Owner_dobj dobj) throws VahanException {

        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("getVaPermitOwnerDetails");
            boolean isExist = entryAlreadyDoneForLoi(loiNo, stateCd);

            if (!isExist) {
                String sql = "select b.*,a.offer_no from permit.va_permit a,permit.va_permit_owner b where a.appl_no=b.appl_no and a.offer_no=? and a.state_cd=? and a.off_cd=? and b.vh_class=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, loiNo);
                ps.setString(2, stateCd);
                ps.setInt(3, offCd);
                ps.setInt(4, dobj.getVh_class());
                RowSet rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    dobj.setState_cd(rs.getString("state_cd"));
                    dobj.setOff_cd(rs.getInt("off_cd"));
                    dobj.setOwner_name(rs.getString("owner_name"));
                    dobj.setF_name(rs.getString("f_name"));
                    dobj.setVh_class(rs.getInt("vh_class"));
                    dobj.setOwner_cd(1);
                    if (dobj.getOwner_identity() == null) {
                        OwnerIdentificationDobj ownerIdnt = new OwnerIdentificationDobj();
                        ownerIdnt.setMobile_no(rs.getLong("mobile_no"));
                        ownerIdnt.setEmail_id(rs.getString("email_id"));
                        ownerIdnt.setOwnerCatg(rs.getInt("owner_ctg"));
                        dobj.setOwner_identity(ownerIdnt);
                    } else {
                        dobj.getOwner_identity().setMobile_no(rs.getLong("mobile_no"));
                        dobj.getOwner_identity().setEmail_id(rs.getString("email_id"));
                        dobj.getOwner_identity().setOwnerCatg(rs.getInt("owner_ctg"));
                    }
                    dobj.setC_add1(rs.getString("c_add1"));
                    dobj.setC_add2(rs.getString("c_add2"));
                    dobj.setC_add3(rs.getString("c_add3"));
                    dobj.setC_district(rs.getInt("c_district"));
                    dobj.setC_pincode(rs.getInt("c_pincode"));
                    dobj.setC_state(rs.getString("c_state"));
                    dobj.setP_add1(rs.getString("p_add1"));
                    dobj.setP_add2(rs.getString("p_add2"));
                    dobj.setP_add3(rs.getString("p_add3"));
                    dobj.setP_district(rs.getInt("p_district"));
                    dobj.setP_pincode(rs.getInt("p_pincode"));
                    dobj.setP_state(rs.getString("p_state"));
                    dobj.setVch_catg(rs.getString("vch_catg"));
                    dobj.setNewLoiNo(rs.getString("offer_no"));
                } else {
                    throw new VahanException("Please Enter Valid Loi No");
                }
            } else {
                throw new VahanException("Loi No is Already used :");
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Getting LOI details");
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

    public void insertNewLoiDetails(String appl_number, String newLoiNo, String regn_no, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        try {
            String sql = "SELECT * FROM  permit.va_new_reg_loi WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_number);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (!rs.next()) {
                sql = "INSERT INTO permit.va_new_reg_loi(\n"
                        + "            appl_no, offer_no ,regn_no)\n"
                        + "    VALUES (?, ?, ?);";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_number);
                ps.setString(2, newLoiNo);
                ps.setString(3, regn_no);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Saving New Permit LOI details");
        }

    }

    public String getNewLoiDetails(String appl_number) throws VahanException {
        String newLoiNo = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("getNewLoiDetails");
            String sql = "SELECT appl_no, offer_no FROM  permit.va_new_reg_loi WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_number);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                newLoiNo = rs.getString("offer_no");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fatching New LOI details");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return newLoiNo;
    }

    public void insertVhaNewLoiDetails(String appl_number, String newLoiNo, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        try {

            String sql = "INSERT INTO permit.vha_new_reg_loi(\n"
                    + "            moved_on, moved_by, appl_no, offer_no,regn_no)\n"
                    + "SELECT current_timestamp as moved_on, ? as moved_by, appl_no, offer_no,regn_no\n"
                    + "  FROM permit.va_new_reg_loi WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_number);
            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Saving New LOI details");
        }

    }

    public void updateNewLoiDetails(String appl_number, String newLoiNo, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        try {
            String sql = "UPDATE permit.va_new_reg_loi SET appl_no=?, offer_no=?  WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_number);
            ps.setString(2, newLoiNo);
            ps.setString(3, appl_number);
            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Updating New LOI details");
        }

    }

    public void insertVtNewLoiDetails(String regn_no, String newLoiNo, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        try {

            String sql = "INSERT INTO permit.vt_new_reg_loi(\n"
                    + "           state_cd,off_cd, regn_no, offer_no)\n"
                    + "SELECT ?,?, regn_no, offer_no\n"
                    + "  FROM permit.va_new_reg_loi WHERE offer_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setString(3, newLoiNo);
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Approval New LOI details");
        }

    }

    public void saveNewLoiDetails(TransactionManager tmgr, String APPL_NO, String newLoiNo, String regn_no) throws VahanException {

        PreparedStatement ps = null;
        try {
            String sql = "SELECT appl_no, offer_no FROM  permit.va_new_reg_loi WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, APPL_NO);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                insertVhaNewLoiDetails(APPL_NO, newLoiNo, tmgr);
                updateNewLoiDetails(APPL_NO, newLoiNo, tmgr);
            } else {
                insertNewLoiDetails(APPL_NO, newLoiNo, regn_no, tmgr);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is some peroblem in saving new loi details !");
        }

    }

    public void verifyLoiDetails(String appl_number, String newLoiNo, String state_cd, int off_cd, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("getVaPermitOwnerDetails");
            String sql = "select b.*,a.offer_no from permit.va_permit a,permit.va_permit_owner b where a.appl_no=b.appl_no and a.offer_no=? and a.state_cd=? and a.off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, newLoiNo);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (!rs.next()) {
                throw new VahanException("Please Enter Valid Loi No");
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Getting LOI details");
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

    public static boolean entryAlreadyDoneForLoi(String offer_no, String stateCd) throws VahanException {
        boolean isDetailsExist = false;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            String sql = "select * from " + TableList.VA_NEW_REGN_LOI + " where offer_no=?";
            tmgr = new TransactionManager("entryAlreadyDoneForLoi");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, offer_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                isDetailsExist = true;
            }

            sql = "select * from " + TableList.VT_NEW_REGN_LOI + " where offer_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, offer_no);
            ps.setString(2, stateCd);

            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isDetailsExist = true;
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("ERROR:");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return isDetailsExist;
    }
}
