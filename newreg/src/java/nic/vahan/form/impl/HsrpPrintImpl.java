/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.http.HttpSession;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.HsrpPrintDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author nic
 */
public class HsrpPrintImpl {

    private static final Logger LOGGER = Logger.getLogger(HsrpPrintImpl.class);

    public static ArrayList<HsrpPrintDobj> getPurCdPrintDocsDetails() throws VahanException {
        ArrayList<HsrpPrintDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            HttpSession session = Util.getSession();
            if (session == null || session.getAttribute("user_catg") == null || session.getAttribute("emp_cd") == null) {
                throw new VahanException("Error in Getting HSRP details,Please try again ");
            }
            tmgr = new TransactionManagerReadOnly("getPurCdPrintDocsDetails");
            String user_catg = (String) session.getAttribute("user_catg");
            if (user_catg != null && !user_catg.equals(TableConstants.USER_CATG_DEALER)) {
                ps = tmgr.prepareStatement("select distinct a.appl_no, a.regn_no, b.state_cd, b.off_cd,b.appl_dt, false as is_approved "
                        + " from " + TableList.VA_OWNER + " a, " + TableList.VA_DETAILS + " b"
                        + " where a.appl_no=b.appl_no and a.state_cd=b.state_cd and a.off_cd=b.off_cd and b.state_cd = ? and b.off_cd = ?");
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            }
            if (user_catg != null && user_catg.equals(TableConstants.USER_CATG_DEALER)) {
                Long user_cd = Long.parseLong(session.getAttribute("emp_cd").toString());
                Map makerAndDealerDetail = OwnerImpl.getDealerDetail(user_cd);
                ps = tmgr.prepareStatement("select distinct a.appl_no, a.regn_no, b.state_cd, b.off_cd,b.appl_dt, false as is_approved \n"
                        + " from " + TableList.VA_OWNER + "  a, " + TableList.VA_DETAILS + "  b where a.appl_no=b.appl_no and a.state_cd=b.state_cd and a.off_cd=b.off_cd and b.state_cd = ? and b.off_cd = ? and b.pur_cd = " + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE + " and a.dealer_cd = ? \n"
                        + " union all select distinct b.appl_no, a.regn_no, b.state_cd, b.off_cd,b.appl_dt, true as is_approved \n"
                        + " from " + TableList.VT_OWNER + "  a, " + TableList.VA_DETAILS + "  b where a.regn_no=b.regn_no and a.state_cd=b.state_cd and a.off_cd=b.off_cd and b.state_cd = ? and b.off_cd = ? and b.pur_cd = " + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE + " and a.dealer_cd = ? ");
                ps.setString(1, Util.getUserStateCode());
                ps.setLong(2, Util.getSelectedSeat().getOff_cd());
                ps.setString(3, makerAndDealerDetail.get("dealer_cd").toString());
                ps.setString(4, Util.getUserStateCode());
                ps.setLong(5, Util.getSelectedSeat().getOff_cd());
                ps.setString(6, makerAndDealerDetail.get("dealer_cd").toString());
            }

            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                HsrpPrintDobj dobj = new HsrpPrintDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegno(rs.getString("regn_no"));
                dobj.setIsApproved(rs.getBoolean("is_approved"));
                if (rs.getDate("appl_dt") != null && !rs.getDate("appl_dt").equals("")) {;
                    dobj.setAppl_dt(format.format(rs.getDate("appl_dt")));
                }
                list.add(dobj);
            }

        } catch (VahanException ve) {
            throw ve;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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
        return list;
    }

    public String isApplExistForNewRegistration(String applNo) throws VahanException {
        String status = null;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        String sql;
        RowSet rs;
        try {
            tmgr = new TransactionManagerReadOnly("Check regn no");
            sql = "select entry_status from  " + TableList.VA_DETAILS
                    + " where appl_no=? and pur_cd in (1,126,123) and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if (rs.getString("entry_status") != null || !rs.getString("entry_status").isEmpty()) {
                    status = rs.getString("entry_status");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in getting Application Status.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
            return status;
        }
    }
}
