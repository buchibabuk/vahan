/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.reports;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.reports.AdvertisementOnVehicleDobj;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class AdvertisementOnVehicleImpl {

    private static final Logger LOGGER = Logger.getLogger(AdvertisementOnVehicleImpl.class);

    public AdvertisementOnVehicleDobj isRegnExist(String regnNo) throws VahanException {
        AdvertisementOnVehicleDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String sql;
        RowSet rs;
        try {
            tmgr = new TransactionManager("NocPrintImpl.isApplExistForNOC method");

            sql = "select a.regn_no,a.appl_no,to_char(a.from_dt,'dd-Mon-yyyy') as from_dt,to_char(a.upto_dt,'dd-Mon-yyyy') as upto_dt"
                    + ",to_char(current_timestamp,'dd-Mon-yyyy hh24:mi:ss') as printedON,c.descr as stateName"
                    + ",d.off_name,b.rcpt_heading,b.rcpt_subheading from  " + TableList.VT_ADVERTISEMENT + " a "
                    + " LEFT OUTER JOIN tm_configuration b ON b.state_cd = a.state_cd"
                    + " LEFT JOIN tm_state c ON c.state_code = a.state_cd"
                    + " LEFT JOIN tm_office d ON d.off_cd = a.off_cd AND d.state_cd = a.state_cd"
                    + " where a.state_cd=? and a.off_cd=? and a.regn_no=? order by a.op_dt desc limit 1";;
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setString(3, regnNo);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new AdvertisementOnVehicleDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setFron_date(rs.getString("from_dt"));
                dobj.setUpto_date(rs.getString("upto_dt"));
                dobj.setPrintedOn(rs.getString("printedON"));
                dobj.setStateName(rs.getString("stateName"));
                dobj.setOffName(rs.getString("off_name"));
                dobj.setHeader(rs.getString("rcpt_heading"));
                dobj.setSubHeader(rs.getString("rcpt_subheading"));


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
        return dobj;
    }
}
