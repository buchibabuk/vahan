/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.RestrictUserForLoginDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author DELL
 */
public class RestrictUserForLoginImpl {

    private static final Logger LOGGER = Logger.getLogger(RestrictUserForLoginImpl.class);

    public List getRestrictUserCatgDetails(String state_cd) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        RestrictUserForLoginDobj dobj = null;
        List selectedUserCatg = new ArrayList();
        try {
            tmgr = new TransactionManagerReadOnly("getRestrictUserCatgDetails");
            sql = " select restrict_login_user_catg from " + TableList.TM_CONFIGURATION_USERMGMT + " where state_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                String[] blockUserCatg = rs.getString("restrict_login_user_catg").split(",");
                selectedUserCatg.addAll(Arrays.asList(blockUserCatg));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in getting User Details");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return selectedUserCatg;
    }

    public static boolean updateRestrictUsercatg(String state_cd, List userCatgList) throws VahanException {
        boolean flag = false;
        PreparedStatement ps;
        TransactionManager tmgr = null;
        String sql = "";
        String restrictCatg = "";

        try {
            if (!userCatgList.isEmpty()) {
                for (int i = 0; i < userCatgList.size(); i++) {
                    restrictCatg += userCatgList.get(i) + ",";
                }
            }
            tmgr = new TransactionManager("updateRestrictUsercatg");

            sql = " INSERT INTO " + TableList.THM_CONFIGURATION_USERMGMT + "( \n"
                    + " moved_by, moved_on, state_cd, rcpt_cancel_with_otp, appl_dispose_with_otp, \n"
                    + " owner_mobile_verify_with_otp, change_veh_office_with_otp, \n"
                    + " delete_smartcard_flatfile_withotp, restrict_login_user_catg)\n"
                    + " SELECT ? as moved_by,current_timestamp as moved_on,state_cd, rcpt_cancel_with_otp, appl_dispose_with_otp, \n"
                    + " owner_mobile_verify_with_otp, change_veh_office_with_otp, delete_smartcard_flatfile_withotp, \n"
                    + " restrict_login_user_catg \n"
                    + "  FROM " + TableList.TM_CONFIGURATION_USERMGMT + " where state_cd=? ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, state_cd);
            ps.executeUpdate();

            sql = " Update " + TableList.TM_CONFIGURATION_USERMGMT + " SET restrict_login_user_catg = ? where state_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, restrictCatg);
            ps.setString(2, state_cd);
            int count = ps.executeUpdate();
            if (count > 0) {
                flag = true;
                tmgr.commit();
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return flag;
    }
}
