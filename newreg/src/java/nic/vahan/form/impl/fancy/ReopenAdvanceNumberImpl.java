/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.fancy;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.fancy.ReopenAdvanceNumberDobj;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author SUMIT GUPTA
 */
public class ReopenAdvanceNumberImpl {

    private static final Logger LOGGER = Logger.getLogger(ReopenAdvanceNumberImpl.class);

    public List<ReopenAdvanceNumberDobj> getAdvanceNumberDetails(String state_cd, int off_cd) throws VahanException {
        List<ReopenAdvanceNumberDobj> reopenlist = new ArrayList<>();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        // reopenlist = null;
        try {
            tmgr = new TransactionManagerReadOnly("ReopenAdvanceImpl");
            ps = tmgr.prepareStatement("SELECT a.regn_no,a.owner_name,to_char(b.rcpt_dt,'dd-MM-yyyy') rcpt_dt,c.fancy_fee_valid_mod,c.fancy_fee_valid_period FROM "
                    + TableList.VT_ADVANCE_REGN_NO + " a "
                    + " inner join " + TableList.VT_FEE + " b on b.regn_no = a.regn_no and b.state_cd = a. state_cd and b.off_cd = a.off_cd and pur_cd = 95 "
                    + " inner join " + TableList.TM_CONFIGURATION + " c on c.state_cd = a.state_cd "
                    + " left join " + TableList.VM_REGN_SERIES + " d on (d.prefix_series = substring(a.regn_no,0,length(a.regn_no)-3) OR d.next_prefix_series = substring(a.regn_no,0,length(a.regn_no)-3) ) and d.state_cd = a.state_cd and d.off_cd = a.off_cd "
                    + " left join " + TableList.VM_ADVANCE_REGN_SERIES + " e on e.prefix_series = substring(a.regn_no,0,length(a.regn_no)-3) and e.state_cd = a.state_cd and e.off_cd = a.off_cd "
                    + " WHERE a.state_cd=? and a.off_cd=? and a.regn_no not in (select regn_no from vt_owner where state_cd = ? and off_cd = ?) and case when c.fancy_fee_valid_mod='D' then b.rcpt_dt+interval '1 days'*c.fancy_fee_valid_period <current_date when c.fancy_fee_valid_mod='M' then "
                    + " b.rcpt_dt+interval '1 months'*c.fancy_fee_valid_period <current_date when c.fancy_fee_valid_mod='Y' then "
                    + " b.rcpt_dt+interval '1 year'*c.fancy_fee_valid_period <current_date end ");

            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, state_cd);
            ps.setInt(4, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                ReopenAdvanceNumberDobj dobj = new ReopenAdvanceNumberDobj();
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setBook_dt(rs.getString("rcpt_dt"));
                dobj.setChecked(Boolean.FALSE);
                reopenlist.add(dobj);
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Getting Expired Advance Registration Number List");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return reopenlist;
    }

    public static int inserIntoHistory(String regn_no, String state_cd, int off_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        int flag = 0;
        try {
            tmgr = new TransactionManager("insertIntoHistory");
            String sql = "insert into " + TableList.VH_ADVANCE_REGN_NO
                    + " select *,current_timestamp as reopen_on, ? as reopen_by"
                    + " from " + TableList.VT_ADVANCE_REGN_NO
                    + " where regn_no = ? and state_cd = ? and off_cd = ?";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, regn_no);
            ps.setString(3, state_cd);
            ps.setInt(4, off_cd);
            flag = ps.executeUpdate();

            sql = "delete from " + TableList.VT_ADVANCE_REGN_NO + " where regn_no = ? and state_cd = ? and off_cd = ?";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            flag = ps.executeUpdate();
            tmgr.commit();


        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            flag = 0;
            throw new VahanException("Error in Reopening Advance Number");
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
