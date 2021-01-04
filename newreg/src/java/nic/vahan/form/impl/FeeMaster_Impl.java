/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.FeeMaster_dobj;
import org.apache.log4j.Logger;

/**
 *
 * @author Ashok Kumar <send2ashok@hotmail.com>
 */
public class FeeMaster_Impl implements Serializable {

    private static Logger LOGGER = Logger.getLogger(FeeMaster_Impl.class);

    public static ArrayList<FeeMaster_dobj> getPurposeMaster(String fee_type) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String sql = null;
        ArrayList<FeeMaster_dobj> master_dobjs_list = new ArrayList<FeeMaster_dobj>();
        try {
            tmgr = new TransactionManager("FeeMaster_Impl");
            sql = "select pur_cd,descr from tm_purpose_mast where fee_type = ? order by pur_cd";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, fee_type);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                FeeMaster_dobj feeMaster_dobj = new FeeMaster_dobj();
                feeMaster_dobj.setPur_cd(rs.getInt("pur_cd"));
                feeMaster_dobj.setDescr(rs.getString("descr"));
                master_dobjs_list.add(feeMaster_dobj);
            }
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
        return master_dobjs_list;
    }//end of getPuposeMaster()

    public static ArrayList<FeeMaster_dobj> getFeeDetails(int pur_cd) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String sql = null;
        ArrayList<FeeMaster_dobj> master_dobjs_list = new ArrayList<FeeMaster_dobj>();
        try {
            tmgr = new TransactionManager("FeeMaster_Impl");
            sql = "select ? as pur_cd,a.catg, a.catg_desc, b.fees,b.imported_fees "
                    + "  from vm_vch_catg a left outer join vm_feemast_catg b "
                    + "    on a.catg = b.vch_catg and b.pur_cd = ? and b.state_cd=? "
                    + " order by catg";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, pur_cd);
            ps.setInt(2, pur_cd);
            ps.setString(3, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                FeeMaster_dobj feeMaster_dobj = new FeeMaster_dobj();
                feeMaster_dobj.setPur_cd(rs.getInt("pur_cd"));
                feeMaster_dobj.setCatg(rs.getString("catg"));
                feeMaster_dobj.setCatg_desc(rs.getString("catg_desc"));
                feeMaster_dobj.setFees(rs.getInt("fees"));
                feeMaster_dobj.setImported_fees(rs.getInt("imported_fees"));
                master_dobjs_list.add(feeMaster_dobj);
            }
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
        return master_dobjs_list;
    }//end of getPuposeMaster()

    public static void updateFeeServiceCharge(FeeMaster_dobj feeMaster_dobj) throws VahanException, Exception {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        try {
            tmgr = new TransactionManager("updateFeeServiceCharge");

            sql = "select  pur_cd, vch_catg, fees, imported_fees from vm_feemast_catg"
                    + " where pur_cd=? and vch_catg=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, feeMaster_dobj.getPur_cd());
            ps.setString(2, feeMaster_dobj.getCatg());
            ps.setString(3, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) { //if any record is exist then update otherwise insert it

                sql = " update vm_feemast_catg "
                        + " SET  fees=?, imported_fees=? "
                        + " where pur_cd=? and vch_catg=? and state_cd=? ";
                ps = tmgr.prepareStatement(sql);

                ps.setInt(1, feeMaster_dobj.getFees());
                ps.setInt(2, feeMaster_dobj.getImported_fees());
                ps.setInt(3, feeMaster_dobj.getPur_cd());
                ps.setString(4, feeMaster_dobj.getCatg());
                ps.setString(5, Util.getUserStateCode());
                ps.executeUpdate();


            } else {
                sql = " INSERT INTO vm_feemast_catg(state_cd, pur_cd, vch_catg, fees, imported_fees, pay_mode)"
                        + "values(?, ?, ?, ?, ?, ?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, feeMaster_dobj.getPur_cd());
                ps.setString(3, feeMaster_dobj.getCatg());
                ps.setInt(4, feeMaster_dobj.getFees());
                ps.setInt(5, feeMaster_dobj.getImported_fees());
                ps.setString(6, "O");  // this is hardcoded will be updated in future
                ps.executeUpdate();
            }

            tmgr.commit();
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
    }//end of updateFeeServiceCharge method
}//end of FeeMaster_Impl Class
