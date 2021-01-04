/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.reports.DownloadDispatchDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author afzal
 */
public class ReturnRCDispatchByHandImpl {

    private static final Logger LOGGER = Logger.getLogger(ReturnRCDispatchByHandImpl.class);

    public DownloadDispatchDobj getRCDispatchDetails(String stateCode, int offCode, String radioBtnValue, String enterValue) throws VahanException {
        ArrayList<DownloadDispatchDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        RowSet rs = null;
        DownloadDispatchDobj dobj = null;
        String whereClause = null;
        String vha_disp_whereClause = null;
        if (radioBtnValue != null && !"".contains(radioBtnValue)) {
            if ("A".contains(radioBtnValue)) {
                whereClause = " and a.appl_no='" + enterValue + "'";
                vha_disp_whereClause = " and appl_no='" + enterValue + "'";
            } else if ("R".contains(radioBtnValue)) {
                whereClause = " and a.regn_no='" + enterValue + "' order by a.return_on desc limit 1";
                vha_disp_whereClause = " and regn_no='" + enterValue + "'";
            }
        }
        try {
            tmgr = new TransactionManager("getReturnDispatchInfoDetails");
            sql = "SELECT d.owner_name,d.c_address,d.mobile_no,a.dispatch_ref_no,to_char(d.moved_on,'dd-Mon-yyyy HH24:MM:SS')as dispatch_on,b.user_name as dispatch_by,to_char(a.return_on,'DD-MON-yyyy') as return_on,e.user_name as return_by "
                    + " from " + TableList.VH_RCDISPATCH_RETURN + " a\n"
                    + " LEFT OUTER JOIN (select * from " + TableList.VHA_DISPATCH + "  where state_cd=? and off_cd=? " + vha_disp_whereClause + " order by moved_on desc limit 1) d on d.regn_no=a.regn_no \n"
                    + " LEFT OUTER JOIN " + TableList.TM_USER_INFO + " b on b.user_cd= regexp_replace(COALESCE(trim(d.moved_by::text), '0'), '[^0-9]', '0', 'g')::numeric and b.state_cd=a.state_cd and b.off_cd=a.off_cd \n"
                    + " LEFT OUTER JOIN " + TableList.TM_USER_INFO + " e on e.user_cd= regexp_replace(COALESCE(trim(a.return_by::text), '0'), '[^0-9]', '0', 'g')::numeric and e.state_cd=a.state_cd and e.off_cd=a.off_cd \n"
                    + " where a.state_cd = ? and a.off_cd =? " + whereClause;
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            ps.setInt(2, offCode);
            ps.setString(3, stateCode);
            ps.setInt(4, offCode);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new DownloadDispatchDobj();
                dobj.setOwnerName(rs.getString("owner_name"));
                dobj.setCurrentAddress(rs.getString("c_address"));
                dobj.setMobile_no(rs.getString("mobile_no"));
                dobj.setDispatch_ref_no(rs.getString("dispatch_ref_no"));
                dobj.setDispatchdate(rs.getString("dispatch_on"));
                dobj.setDispatch_by(rs.getString("return_by"));
                dobj.setDispatch_rc_return_on(rs.getString("return_on"));
                dobj.setDispatch_rc_return_by(rs.getString("return_by"));
                list.add(dobj);
            } else {
                sql = "SELECT appl_no,remark,to_char(handed_over_on,'dd-Mon-yyyy') as handed_over_on from " + TableList.VH_RETURN_RC_DISPATCH_BY_HAND + " where state_cd = ? and off_cd =? " + vha_disp_whereClause + " order by handed_over_on desc limit 1";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCode);
                ps.setInt(2, offCode);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    if (rs.getString("appl_no") != null && !rs.getString("appl_no").isEmpty()) {
                        if ("A".contains(radioBtnValue)) {
                            throw new VahanException("RC for Application No " + enterValue + " is already By Hand with Remark: " + rs.getString("remark").toUpperCase() + "  as on date " + rs.getString("handed_over_on") + " !!!");
                        } else if ("R".contains(radioBtnValue)) {
                            throw new VahanException("RC for Registration No " + enterValue + " is already By Hand with Remark: " + rs.getString("remark").toUpperCase() + " Against Application No " + rs.getString("appl_no") + " as on date " + rs.getString("handed_over_on") + " !!!");
                        }
                    }
                }
            }
            if (list.size() > 0) {
                dobj.setListFileExport(list);
            }
        } catch (VahanException ve) {
            throw new VahanException(ve.getMessage());
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
        return dobj;
    }

    public boolean saveRCDispatchByHand(String state_cd, int off_cd, String searchByValue, String searchBy, String remark) throws VahanException {
        boolean isSuccess = false;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        Exception e = null;
        String whereClause = null;
        String vha_disp_whereClause = null;
        if (searchBy.contains("R")) {
            whereClause = "and a.regn_no ='" + searchByValue + "'";
            vha_disp_whereClause = "and  regn_no ='" + searchByValue + "'";
        } else if (searchBy.contains("A")) {
            whereClause = "and a.appl_no ='" + searchByValue + "'";
            vha_disp_whereClause = "and appl_no ='" + searchByValue + "'";
        }
        try {
            String sql = "insert into " + TableList.VH_RETURN_RC_DISPATCH_BY_HAND + " SELECT d.*,a.reason,a.return_on,a.return_by,?,current_timestamp,? \n"
                    + " from " + TableList.VH_RCDISPATCH_RETURN + " a\n"
                    + " LEFT OUTER JOIN (select * from " + TableList.VHA_DISPATCH + "  where state_cd=? and off_cd=? " + vha_disp_whereClause + " order by moved_on desc limit 1) d on d.regn_no=a.regn_no \n"
                    + " where a.state_cd = ? and a.off_cd =?  " + whereClause + " order by a.return_on desc limit 1";
            tmgr = new TransactionManager("RCReturnDispatchImpl.save");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, remark);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, state_cd);
            ps.setInt(4, off_cd);
            ps.setString(5, state_cd);
            ps.setInt(6, off_cd);
            int i = ps.executeUpdate();
            if (i > 0) {
                isSuccess = true;
            } else {
                isSuccess = false;
            }
            sql = "delete from " + TableList.VH_RCDISPATCH_RETURN + " \n"
                    + " where state_cd=? and off_cd=? " + vha_disp_whereClause;
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.executeUpdate();
            tmgr.commit();
        } catch (SQLException ee) {
            e = ee;
        } catch (Exception ee) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            e = ee;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        if (e != null) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return isSuccess;
    }
}
