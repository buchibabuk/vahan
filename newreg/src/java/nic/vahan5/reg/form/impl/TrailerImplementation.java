/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.java.util.CommonUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.Trailer_dobj;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author Kartikey Singh
 */
public class TrailerImplementation {

    private static Logger LOGGER = Logger.getLogger(TrailerImplementation.class);

    public Trailer_dobj set_trailer_dtls_to_dobj(String appl_no, String regn_no, int pur_cd) throws VahanException {

        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        Trailer_dobj trailer_dobj = null;
        String tname, pname, pvalue;

        if (appl_no != null) {
            appl_no = appl_no.toUpperCase().trim();
        }

        if (regn_no != null) {
            regn_no = regn_no.toUpperCase().trim();
        }


        if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
            tname = "va_trailer";
            pname = "appl_no";
            pvalue = appl_no;
        } else {
            tname = "vt_trailer";
            pname = "regn_no";
            pvalue = regn_no;
        }
        String query = "select body_type, chasi_no, ld_wt, unld_wt, f_axle_descp,r_axle_descp,"
                + "o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight,o_axle_weight,"
                + "t_axle_weight from " + tname + " where " + pname + " = ? ";
        try {

            tmgr = new TransactionManagerReadOnly("Trailer_Impl");
            ps = tmgr.prepareStatement(query);
            ps.setString(1, pvalue);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                trailer_dobj = new Trailer_dobj();

                trailer_dobj.setBody_type(rs.getString("body_type"));
                trailer_dobj.setChasi_no(rs.getString("chasi_no"));
                trailer_dobj.setLd_wt(rs.getInt("ld_wt"));
                trailer_dobj.setUnld_wt(rs.getInt("unld_wt"));
                trailer_dobj.setF_axle_descp(rs.getString("f_axle_descp"));
                trailer_dobj.setR_axle_descp(rs.getString("r_axle_descp"));
                trailer_dobj.setO_axle_descp(rs.getString("o_axle_descp"));
                trailer_dobj.setT_axle_descp(rs.getString("t_axle_descp"));
                trailer_dobj.setF_axle_weight(rs.getInt("f_axle_weight"));
                trailer_dobj.setR_axle_weight(rs.getInt("r_axle_weight"));
                trailer_dobj.setO_axle_weight(rs.getInt("o_axle_weight"));
                trailer_dobj.setT_axle_weight(rs.getInt("t_axle_weight"));
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Trailer Information");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return trailer_dobj;
    }//end of set_trailer_dtls_to_dobj

    public static void updateTrailer(TransactionManager tmgr, Trailer_dobj dobj, String appl_no) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "UPDATE va_trailer"
                    + "   SET body_type=?, chasi_no=?, ld_wt=?, unld_wt=?,"
                    + "       f_axle_descp=?, r_axle_descp=?, o_axle_descp=?, t_axle_descp=?, "
                    + "       f_axle_weight=?, r_axle_weight=?, o_axle_weight=?, t_axle_weight=? "
                    + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getBody_type());
            ps.setString(2, dobj.getChasi_no());
            ps.setInt(3, dobj.getLd_wt());
            ps.setInt(4, dobj.getUnld_wt());
            ps.setString(5, dobj.getF_axle_descp());
            ps.setString(6, dobj.getR_axle_descp());
            ps.setString(7, dobj.getO_axle_descp());
            ps.setString(8, dobj.getT_axle_descp());
            ps.setInt(9, dobj.getF_axle_weight());
            ps.setInt(10, dobj.getR_axle_weight());
            ps.setInt(11, dobj.getO_axle_weight());
            ps.setInt(12, dobj.getT_axle_weight());
            ps.setString(13, appl_no);
            ps.executeUpdate();
        } catch (SQLException sqle) {
            throw new VahanException(sqle.getMessage());
        }
    } // end of updateHPA

    public static void insertIntoTrailerHistory(TransactionManager tmgr, String appl_no) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;

        try {
            sql = "INSERT INTO vha_trailer "
                    + " SELECT current_timestamp as moved_on, ? as moved_by, state_cd, off_cd, appl_no, regn_no, sr_no, chasi_no, body_type, ld_wt, unld_wt, f_axle_descp, "
                    + "       r_axle_descp, o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight, "
                    + "       o_axle_weight, t_axle_weight, op_dt"
                    + "  FROM va_trailer where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            ps.executeUpdate();
        } catch (SQLException sqle) {
            throw new VahanException(sqle.getMessage());
        }
    } // end of insertIntoHPAHistory
    
    /**
     * @author Kartikey Singh
     */
     public static void insertIntoTrailerHistory(TransactionManager tmgr, String appl_no, String empCode) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;

        try {
            sql = "INSERT INTO vha_trailer "
                    + " SELECT current_timestamp as moved_on, ? as moved_by, state_cd, off_cd, appl_no, regn_no, sr_no, chasi_no, body_type, ld_wt, unld_wt, f_axle_descp, "
                    + "       r_axle_descp, o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight, "
                    + "       o_axle_weight, t_axle_weight, op_dt"
                    + "  FROM va_trailer where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, appl_no);
            ps.executeUpdate();
        } catch (SQLException sqle) {
            throw new VahanException(sqle.getMessage());
        }
    } // end of insertIntoHPAHistory

    public static void insertIntoTrailer(TransactionManager tmgr, Trailer_dobj dobj, String appl_no, String regn_no, String chasi_no) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;

        try {


            sql = "INSERT INTO va_trailer(state_cd , off_cd, appl_no, regn_no, sr_no, chasi_no, body_type, ld_wt, unld_wt, f_axle_descp,"
                    + " r_axle_descp, o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight,"
                    + " o_axle_weight, t_axle_weight, op_dt) "
                    + " VALUES (?, ?, ?, ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setString(3, appl_no);
            ps.setString(4, regn_no);
            ps.setString(5, dobj.getChasi_no());
            ps.setString(6, dobj.getBody_type());
            ps.setInt(7, dobj.getLd_wt());
            ps.setInt(8, dobj.getUnld_wt());
            ps.setString(9, dobj.getF_axle_descp());
            ps.setString(10, dobj.getR_axle_descp());
            ps.setString(11, dobj.getO_axle_descp());
            ps.setString(12, dobj.getT_axle_descp());
            ps.setInt(13, dobj.getF_axle_weight());
            ps.setInt(14, dobj.getR_axle_weight());
            ps.setInt(15, dobj.getO_axle_weight());
            ps.setInt(16, dobj.getT_axle_weight());
            ps.executeUpdate();
        } catch (SQLException sqle) {
            throw new VahanException(sqle.getMessage());
        }
    } // end of insertIntoHPA
    
    /*
     * @author Kartikey Singh
     */
    public static void insertIntoTrailer(TransactionManager tmgr, Trailer_dobj dobj, String appl_no, String regn_no, 
            String chasi_no, String userStateCode, int offCode) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;

        try {


            sql = "INSERT INTO va_trailer(state_cd , off_cd, appl_no, regn_no, sr_no, chasi_no, body_type, ld_wt, unld_wt, f_axle_descp,"
                    + " r_axle_descp, o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight,"
                    + " o_axle_weight, t_axle_weight, op_dt) "
                    + " VALUES (?, ?, ?, ?, 1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, userStateCode);
            ps.setInt(2, offCode);
            ps.setString(3, appl_no);
            ps.setString(4, regn_no);
            ps.setString(5, dobj.getChasi_no());
            ps.setString(6, dobj.getBody_type());
            ps.setInt(7, dobj.getLd_wt());
            ps.setInt(8, dobj.getUnld_wt());
            ps.setString(9, dobj.getF_axle_descp());
            ps.setString(10, dobj.getR_axle_descp());
            ps.setString(11, dobj.getO_axle_descp());
            ps.setString(12, dobj.getT_axle_descp());
            ps.setInt(13, dobj.getF_axle_weight());
            ps.setInt(14, dobj.getR_axle_weight());
            ps.setInt(15, dobj.getO_axle_weight());
            ps.setInt(16, dobj.getT_axle_weight());
            ps.executeUpdate();
        } catch (SQLException sqle) {
            throw new VahanException(sqle.getMessage());
        }
    } // end of insertIntoHPA

    public static void insertUpdateTrailer(TransactionManager tmgr, String appl_no, String regn_no, String chasi_no, Trailer_dobj trailer_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {

            sql = "SELECT * FROM va_trailer where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) { //if any record is exist then update otherwise insert it
                if (trailer_dobj != null) {
                    insertIntoTrailerHistory(tmgr, appl_no);
                    updateTrailer(tmgr, trailer_dobj, appl_no);
                }

            } else {
                if (trailer_dobj != null) {
                    insertIntoTrailer(tmgr, trailer_dobj, appl_no, regn_no, chasi_no);
                }
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    } // end of insertUpdateTrailer
    
    /**
     * @author Kartikey Singh
     */
    public static void insertUpdateTrailer(TransactionManager tmgr, String appl_no, String regn_no, String chasi_no, 
            Trailer_dobj trailer_dobj, String empCode, String userStateCode, int offCode) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {

            sql = "SELECT * FROM va_trailer where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) { //if any record is exist then update otherwise insert it
                if (trailer_dobj != null) {
                    insertIntoTrailerHistory(tmgr, appl_no, empCode);
                    updateTrailer(tmgr, trailer_dobj, appl_no);
                }

            } else {
                if (trailer_dobj != null) {
                    insertIntoTrailer(tmgr, trailer_dobj, appl_no, regn_no, chasi_no, userStateCode, offCode);
                }
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    } // end of insertUpdateTrailer

    //Delete from va_trailer using regn_no
    public static void deleteFromVaTrailer(TransactionManager tmgr, String regn_no, String appl_no) throws SQLException, Exception {

        PreparedStatement ps = null;
        String sql = null;
        sql = "Delete from " + TableList.VA_TRAILER + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, appl_no);
        ps.executeUpdate();

    }

    public static Trailer_dobj checkTrailerChassis_owner(String chassis_no) {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        Trailer_dobj dobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("Trailer_Impl");
            ps = tmgr.prepareStatement(" select a.chasi_no,a.regn_no,a.state_cd,a.off_cd from"
                    + "((select chasi_no,regn_no,state_cd,off_cd from " + TableList.VT_OWNER
                    + " where chasi_no=? )"
                    + " UNION ALL "
                    + " (select chasi_no,regn_no,state_cd,off_cd from " + TableList.VA_OWNER
                    + " where chasi_no=? )"
                    + " ) a ");
            ps.setString(1, chassis_no);
            ps.setString(2, chassis_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                dobj = new Trailer_dobj();
                dobj.setDup_chassis(rs.getString("chasi_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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

    public static Trailer_dobj checkTrailerChassis_trailer(String chassis_no) {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        Trailer_dobj dobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("Trailer_Impl");
            ps = tmgr.prepareStatement(" select a.chasi_no,a.regn_no,a.state_cd,a.off_cd from"
                    + "((select chasi_no,regn_no,state_cd,off_cd from " + TableList.VT_TRAILER
                    + " where chasi_no=? )"
                    + " UNION ALL "
                    + " (select chasi_no,regn_no,state_cd,off_cd from " + TableList.VA_TRAILER
                    + " where chasi_no=? )"
                    + " ) a ");
            ps.setString(1, chassis_no);
            ps.setString(2, chassis_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                dobj = new Trailer_dobj();
                dobj.setDup_chassis(rs.getString("chasi_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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

    public static Trailer_dobj checkTrailerChassis_VTOwnerTrailer(String chassis_no) {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        Trailer_dobj dobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("Trailer_Impl");
            ps = tmgr.prepareStatement(" select a.chasi_no,a.regn_no,a.state_cd,a.off_cd from"
                    + "((select chasi_no,regn_no,state_cd,off_cd from " + TableList.VT_TRAILER
                    + " where chasi_no=? )"
                    + " UNION ALL "
                    + " (select chasi_no,regn_no,state_cd,off_cd from " + TableList.VT_OWNER
                    + " where chasi_no=? )"
                    + " ) a ");
            ps.setString(1, chassis_no);
            ps.setString(2, chassis_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                dobj = new Trailer_dobj();
                dobj.setDup_chassis(rs.getString("chasi_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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

    public static void movedataapprovalTrailer(TransactionManager tmgr, String regn_no, String appl_no, int offCd) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;
        sql = "Insert into " + TableList.VH_TRAILER + " Select state_cd, off_cd, regn_no, chasi_no, body_type, ld_wt,"
                + " unld_wt, f_axle_descp,"
                + " r_axle_descp, o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight,"
                + " o_axle_weight, t_axle_weight, NULL, current_timestamp, ? from " + TableList.VT_TRAILER
                + " where regn_no=? and state_cd= ? and off_cd= ? ";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, regn_no);
        ps.setString(3, Util.getUserStateCode());
        ps.setInt(4, offCd);
        ps.executeUpdate();

        sql = "Delete FROM  " + TableList.VT_TRAILER
                + " where regn_no=? and state_cd = ? and off_cd= ?";

        ps = tmgr.prepareStatement(sql);

        ps.setString(1, regn_no);
        ps.setString(2, Util.getUserStateCode());
        ps.setInt(3, offCd);
        ps.executeUpdate();

        //Inserting into vt_trailer from va_trailer
        sql = "INSERT into " + TableList.VT_TRAILER + " "
                + "Select regn_no, chasi_no, body_type, ld_wt,"
                + " unld_wt, f_axle_descp,"
                + " r_axle_descp, o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight,"
                + " o_axle_weight, t_axle_weight, ?, ?, current_timestamp as op_dt "
                + " FROM " + TableList.VA_TRAILER + " where appl_no=?";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getUserStateCode());
        ps.setInt(2, offCd);
        ps.setString(3, appl_no);
        ps.executeUpdate();
    }
    
    /**
     * @author Kartikey Singh
     */
    public static void movedataapprovalTrailer(TransactionManager tmgr, String regn_no, String appl_no, String empCode,
            String userStateCode, int selectedOffCode) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;
        sql = "Insert into " + TableList.VH_TRAILER + " Select state_cd, off_cd, regn_no, chasi_no, body_type, ld_wt,"
                + " unld_wt, f_axle_descp,"
                + " r_axle_descp, o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight,"
                + " o_axle_weight, t_axle_weight, NULL, current_timestamp, ? from " + TableList.VT_TRAILER
                + " where regn_no=? and state_cd= ? and off_cd= ? ";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, empCode);
        ps.setString(2, regn_no);
        ps.setString(3, userStateCode);
        ps.setInt(4, selectedOffCode);
        ps.executeUpdate();

        sql = "Delete FROM  " + TableList.VT_TRAILER
                + " where regn_no=? and state_cd = ? and off_cd= ?";

        ps = tmgr.prepareStatement(sql);

        ps.setString(1, regn_no);
        ps.setString(2, userStateCode);
        ps.setInt(3, selectedOffCode);
        ps.executeUpdate();

        //Inserting into vt_trailer from va_trailer
        sql = "INSERT into " + TableList.VT_TRAILER + " "
                + "Select regn_no, chasi_no, body_type, ld_wt,"
                + " unld_wt, f_axle_descp,"
                + " r_axle_descp, o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight,"
                + " o_axle_weight, t_axle_weight, ?, ?, current_timestamp as op_dt "
                + " FROM " + TableList.VA_TRAILER + " where appl_no=?";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, userStateCode);
        ps.setInt(2, selectedOffCode);
        ps.setString(3, appl_no);
        ps.executeUpdate();
    }

    public static void validationTrailer(Trailer_dobj dobj) throws VahanException {

        try {
            if (CommonUtils.isNullOrBlank(dobj.getBody_type())) {
                throw new VahanException("Enter Trailer Body Type");
            }

            if (dobj.getLd_wt() <= 0 || dobj.getUnld_wt() <= 0) {
                throw new VahanException("Enter Trailer Laden/UnLaden Weight");
            }

            if (CommonUtils.isNullOrBlank(dobj.getF_axle_descp())) {
                throw new VahanException("Enter Trailer Front Axle Desc");
            }

            if (CommonUtils.isNullOrBlank(dobj.getR_axle_descp())) {
                throw new VahanException("Enter Trailer Rear Axle Desc");
            }

            if (dobj.getR_axle_weight() <= 0 || dobj.getF_axle_weight() <= 0) {
                throw new VahanException("Enter Trailer Front/Rear Weight");
            }
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public static List<Trailer_dobj> set_trailer_dtls_to_dobjList(String appl_no, String regn_no, int pur_cd) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        List<Trailer_dobj> trailer_dobjList = null;
        try {
            tmgr = new TransactionManagerReadOnly("set_trailer_dtls_to_dobjList");
            trailer_dobjList = set_trailer_dobjList(tmgr, appl_no, regn_no, pur_cd);
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Trailer Information");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return trailer_dobjList;
    }

    public static List<Trailer_dobj> set_trailer_dobjList(TransactionManagerReadOnly tmgr, String appl_no, String regn_no, int pur_cd) throws VahanException {

        PreparedStatement ps = null;
        Trailer_dobj trailer_dobj = null;
        String tname, pname, pvalue;
        List<Trailer_dobj> trailer_dobjList = new ArrayList<>();

        if (appl_no != null) {
            appl_no = appl_no.toUpperCase().trim();
        }

        if (regn_no != null) {
            regn_no = regn_no.toUpperCase().trim();
        }

        if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
            tname = "va_trailer";
            pname = "appl_no";
            pvalue = appl_no;
        } else {
            tname = "vt_trailer";
            pname = "regn_no";
            pvalue = regn_no;
        }
        String query = "select body_type, chasi_no, ld_wt, unld_wt, f_axle_descp,r_axle_descp,"
                + "o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight,o_axle_weight,"
                + "t_axle_weight from " + tname + " where " + pname + " = ? ";
        try {
            ps = tmgr.prepareStatement(query);
            ps.setString(1, pvalue);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                trailer_dobj = new Trailer_dobj();
                trailer_dobj.setBody_type(rs.getString("body_type"));
                trailer_dobj.setChasi_no(rs.getString("chasi_no"));
                trailer_dobj.setLd_wt(rs.getInt("ld_wt"));
                trailer_dobj.setUnld_wt(rs.getInt("unld_wt"));
                trailer_dobj.setF_axle_descp(rs.getString("f_axle_descp"));
                trailer_dobj.setR_axle_descp(rs.getString("r_axle_descp"));
                trailer_dobj.setO_axle_descp(rs.getString("o_axle_descp"));
                trailer_dobj.setT_axle_descp(rs.getString("t_axle_descp"));
                trailer_dobj.setF_axle_weight(rs.getInt("f_axle_weight"));
                trailer_dobj.setR_axle_weight(rs.getInt("r_axle_weight"));
                trailer_dobj.setO_axle_weight(rs.getInt("o_axle_weight"));
                trailer_dobj.setT_axle_weight(rs.getInt("t_axle_weight"));
                trailer_dobjList.add(trailer_dobj);
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return trailer_dobjList;
    }//end of set_trailer_dtls_to_dobj
}