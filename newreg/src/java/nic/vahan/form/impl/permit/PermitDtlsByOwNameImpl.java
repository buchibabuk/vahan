/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.permit.PermitDtlsByOwNameDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author hcl
 */
public class PermitDtlsByOwNameImpl {

    private static final Logger LOGGER = Logger.getLogger(PermitDtlsByOwNameImpl.class);

    public List<PermitDtlsByOwNameDobj> getPmtDtlsByOwnName(String ownName, String fName, String state_cd) {
        TransactionManagerReadOnly tmgr = null;
        List<PermitDtlsByOwNameDobj> pmtDtlsList = new ArrayList<>();
        String Query = "SELECT main.* from\n"
                + "(SELECT a.regn_no, a.pmt_no, COALESCE(owner_name, '') as owner_name, \n"
                + "       COALESCE(f_name, '') as f_name, COALESCE(c_add1, '') || ',' || COALESCE(c_add2, '') || ',' || COALESCE(c_add3, '') || ' PIN-' || COALESCE(c_pincode::varchar, '') as address, \n"
                + "       to_char(regn_dt, 'DD-MON-YYYY') as regn_dt, c.descr as status\n"
                + "from " + TableList.VT_PERMIT + " a\n"
                + "left outer join " + TableList.VT_OWNER + " b on a.regn_no = b.regn_no AND a.state_cd = b.state_cd\n"
                + "left outer join " + TableList.VM_OWNER_STATUS + " c on c.status = b.status \n"
                + "where a.state_cd = ? and a.off_Cd = ?\n"
                + "UNION\n"
                + "SELECT a.regn_no, a.pmt_no, COALESCE(owner_name, '') as owner_name, \n"
                + "       COALESCE(f_name, '') as f_name, COALESCE(c_add1, '') || ',' || COALESCE(c_add2, '') || ',' || COALESCE(c_add3, '') || ' PIN-' || COALESCE(c_pincode::varchar, '') as address, \n"
                + "       to_char(regn_dt, 'DD-MON-YYYY') as regn_dt, c.descr as status from " + TableList.VH_PERMIT + " a \n"
                + "left outer join " + TableList.VT_OWNER + " b on a.regn_no = b.regn_no AND a.state_cd = b.state_cd\n"
                + "left outer join " + TableList.VM_OWNER_STATUS + " c on c.status = b.status\n"
                + "where a.state_cd=? and a.off_Cd = ? and a.regn_no not in (SELECT regn_no from " + TableList.VT_PERMIT + " where state_cd = ? and off_cd=?) \n"
                + "and (a.regn_no,a.moved_on) in (SELECT DISTINCT(regn_no),max(moved_on) from " + TableList.VH_PERMIT + " where state_cd = ? and off_cd = ?  group by regn_no)) as main\n"
                + "where main.owner_name like ? ";
        try {
            tmgr = new TransactionManagerReadOnly("getPmtDtlsByOwnName");
            if (!CommonUtils.isNullOrBlank(fName)) {
                Query += " AND main.f_name like ? order by main.regn_no ";
            } else {
                Query += " order by main.regn_no ";
            }
            PreparedStatement ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, state_cd);
            ps.setInt(i++, Util.getUserSeatOffCode());
            ps.setString(i++, state_cd);
            ps.setInt(i++, Util.getUserSeatOffCode());
            ps.setString(i++, state_cd);
            ps.setInt(i++, Util.getUserSeatOffCode());
            ps.setString(i++, state_cd);
            ps.setInt(i++, Util.getUserSeatOffCode());
            ps.setString(i++, "%" + ownName + "%");
            if (!CommonUtils.isNullOrBlank(fName)) {
                ps.setString(i++, "%" + fName + "%");
            }
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                pmtDtlsList.add(new PermitDtlsByOwNameDobj(rs.getString("regn_no"), rs.getString("pmt_no"), rs.getString("owner_name"), rs.getString("f_name"), rs.getString("address"), rs.getString("regn_dt"), rs.getString("status")));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return pmtDtlsList;
    }

    public List<PermitDtlsByOwNameDobj> getPmtDtlsByPmtNo(String pmtNo, String state_cd) {
        TransactionManagerReadOnly tmgr = null;
        List<PermitDtlsByOwNameDobj> pmtDtlsList = new ArrayList<>();
        String Query = "SELECT a.regn_no, a.pmt_no, COALESCE(owner_name, '') as owner_name, \n"
                + "       COALESCE(f_name, '') as f_name, COALESCE(c_add1, '') || ',' || COALESCE(c_add2, '') || ',' || COALESCE(c_add3, '') || ' PIN-' || COALESCE(c_pincode::varchar, '') as address, \n"
                + "       to_char(regn_dt, 'DD-MON-YYYY') as regn_dt, c.descr as status\n"
                + "from " + TableList.VT_PERMIT + " a\n"
                + "left outer join " + TableList.VT_OWNER + " b on a.regn_no = b.regn_no AND a.state_cd = b.state_cd\n"
                + "left outer join " + TableList.VM_OWNER_STATUS + " c on c.status = b.status \n"
                + "where a.state_cd = ? and a.off_Cd = ? and a.pmt_no like ? order by regn_no";
        try {
            tmgr = new TransactionManagerReadOnly("getPmtDtlsByOwnName");
            PreparedStatement ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, state_cd);
            ps.setInt(i++, Util.getUserSeatOffCode());
            ps.setString(i++, "%" + pmtNo + "%");
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                pmtDtlsList.add(new PermitDtlsByOwNameDobj(rs.getString("regn_no"), rs.getString("pmt_no"), rs.getString("owner_name"), rs.getString("f_name"), rs.getString("address"), rs.getString("regn_dt"), rs.getString("status")));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return pmtDtlsList;
    }
}
