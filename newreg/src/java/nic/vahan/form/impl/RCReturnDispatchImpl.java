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
import nic.vahan.form.dobj.TmConfigurationDispatchDobj;
import nic.vahan.form.dobj.reports.DownloadDispatchDobj;
import static nic.vahan.server.ServerUtil.sendSMS;
import org.apache.log4j.Logger;

/**
 *
 * @author nicsi
 */
public class RCReturnDispatchImpl {

    private static final Logger LOGGER = Logger.getLogger(RCReturnDispatchImpl.class);

    public boolean save(DownloadDispatchDobj dobj, int reason, TmConfigurationDispatchDobj tmConfDispatchDobj) throws VahanException {
        boolean isSuccess = false;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        Exception e = null;
        String msgMobileCollection = "";
        try {
            String sql = "insert into " + TableList.VH_RCDISPATCH_RETURN + " (select state_cd,off_cd,appl_no,regn_no," + reason + ",dispatch_ref_no,moved_on,current_timestamp,? from " + TableList.VHA_DISPATCH + " \n"
                    + " where state_cd=? and off_cd=? and appl_no=? order by moved_on desc limit 1)";
            tmgr = new TransactionManager("getAllOldDispatchRCList");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, dobj.getState_cd());
            ps.setInt(3, dobj.getOff_cd());
            ps.setString(4, dobj.getAppl_no().trim());
            int i = ps.executeUpdate();
            if (i > 0) {
                isSuccess = true;
            } else {
                isSuccess = false;
            }
            tmgr.commit();
            if (isSuccess && tmConfDispatchDobj.isIs_sendSMS_owner()) {
                if (tmConfDispatchDobj.getSms_dispatch_return() != null && !tmConfDispatchDobj.getSms_dispatch_return().isEmpty()) {
                    msgMobileCollection = tmConfDispatchDobj.getSms_dispatch_return()
                            .replace("{REGN_NO}", dobj.getRegnNo())
                            .replace("{DISPATCH_DATE}", dobj.getDispatchdate())
                            .replace("{DISPATCH_NO}", dobj.getDispatch_ref_no().toUpperCase())
                            .replace("{REASON}", getReasonDesc(reason, tmgr))
                            .replace("{OFFICE_NAME}", dobj.getOffName());
                    sendSMS(dobj.getMobile_no(), msgMobileCollection);
                }
            }
        } catch (SQLException ee) {
            e = ee;
        } catch (Exception ee) {
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

    public DownloadDispatchDobj getRCDispatchDetails(String stateCode, int offCode, String radioBtnValue, String enterValue) throws VahanException {
        ArrayList<DownloadDispatchDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        RowSet rs = null;
        DownloadDispatchDobj dobj = null;
        String whereClause = null;
        String whereClauseByHand = null;
        if (radioBtnValue != null && !"".contains(radioBtnValue)) {
            if ("A".contains(radioBtnValue)) {
                whereClause = " and a.appl_no='" + enterValue + "'";
                whereClauseByHand = " and a.appl_no='" + enterValue + "'";
            } else if ("R".contains(radioBtnValue)) {
                whereClause = " and a.regn_no='" + enterValue + "' order by a.moved_on desc limit 1";
                whereClauseByHand = " and a.regn_no='" + enterValue + "' order by a.handed_over_on desc limit 1";
            }
        }
        try {
            tmgr = new TransactionManager("getReturnDispatchInfoDetails");
            sql = "SELECT c.appl_no,to_char(c.return_on,'dd-Mon-yyyy HH24:MM:SS')as return_on,a.appl_no as dispatchApplNo,a.regn_no,d.off_name,a.owner_name,a.c_address,a.mobile_no,a.dispatch_ref_no,to_char(a.moved_on,'DD-MON-yyyy') as dispatch_on,b.user_name as dispatch_by  "
                    + " from " + TableList.VHA_DISPATCH + " a\n"
                    + " LEFT OUTER JOIN " + TableList.TM_USER_INFO + " b on b.user_cd= regexp_replace(COALESCE(trim(a.moved_by::text), '0'), '[^0-9]', '0', 'g')::numeric and b.state_cd=a.state_cd and b.off_cd=a.off_cd \n"
                    + " LEFT OUTER JOIN " + TableList.VH_RCDISPATCH_RETURN + " c on c.state_cd=a.state_cd and c.off_cd=a.off_cd and c.appl_no=a.appl_no and c.dispatch_on=a.moved_on \n"
                    + " left outer join " + TableList.TM_OFFICE + " d on d.state_cd = a.state_cd and d.off_cd=a.off_cd"
                    + " where a.state_cd = ? and a.off_cd =? " + whereClause;
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            ps.setInt(2, offCode);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new DownloadDispatchDobj();
                if (rs.getString("appl_no") != null && !rs.getString("appl_no").isEmpty()) {
                    if ("A".contains(radioBtnValue)) {
                        throw new VahanException("The RC Dispatch for Application No " + enterValue + " is already returned as on date " + rs.getString("return_on") + " !!!");
                    } else if ("R".contains(radioBtnValue)) {
                        throw new VahanException("The RC Dispatch for Registration No " + enterValue + " is already returned with Application No" + rs.getString("appl_no") + " as on date " + rs.getString("return_on") + " !!!");
                    }
                }
                dobj.setOwnerName(rs.getString("owner_name"));
                dobj.setCurrentAddress(rs.getString("c_address"));
                dobj.setMobile_no(rs.getString("mobile_no"));
                dobj.setDispatch_ref_no(rs.getString("dispatch_ref_no"));
                dobj.setDispatchdate(rs.getString("dispatch_on"));
                dobj.setDispatch_by(rs.getString("dispatch_by"));
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setAppl_no(rs.getString("dispatchApplNo"));
                dobj.setState_cd(stateCode);
                dobj.setOff_cd(offCode);
                dobj.setOffName(rs.getString("off_name"));
                list.add(dobj);
                sql = "SELECT a.appl_no,a.remark,to_char(a.handed_over_on,'dd-Mon-yyyy') as handed_over_on from " + TableList.VH_RETURN_RC_DISPATCH_BY_HAND + " a\n"
                        + " inner join " + TableList.VHA_DISPATCH + " b on b.appl_no=a.appl_no and b.state_cd=a.state_cd and b.off_cd=a.off_cd \n"
                        + " where a.state_cd = ? and a.off_cd =? " + whereClauseByHand;
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, stateCode);
                ps.setInt(2, offCode);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    if (rs.getString("appl_no") != null && !rs.getString("appl_no").isEmpty()) {
                        if ("A".contains(radioBtnValue)) {
                            throw new VahanException("RC for Application No " + enterValue + " is already Delivered By Hand with Remark: " + rs.getString("remark").toUpperCase() + "  as on date " + rs.getString("handed_over_on") + " !!!");
                        } else if ("R".contains(radioBtnValue)) {
                            throw new VahanException("RC for Registration No " + enterValue + " is already Delivered By Hand with Remark: " + rs.getString("remark").toUpperCase() + " Against Application No " + rs.getString("appl_no") + " as on date " + rs.getString("handed_over_on") + " !!!");
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

    public String getReasonDesc(int reason, TransactionManager tmgr) {
        String reasonDesc = "Not Available";
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "Select descr from " + TableList.VM_RCD_RETURN_REASON + " where code=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, reason);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                reasonDesc = rs.getString("descr");
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        return reasonDesc;
    }
}
