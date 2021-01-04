/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.TccDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author acer
 */
public class TccReportImpl {

    private static final Logger LOGGER = Logger.getLogger(TccReportImpl.class);

    public static List<TccDobj> getTccData(String regnNo, String state_cd, int off_cd) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        RowSet rs;
        List<TccDobj> tccDetailslist = null;
        TccDobj dobj = null;
        if (Util.getSelectedSeat() != null) {
            off_cd = Util.getSelectedSeat().getOff_cd();
        }

        try {
            tmgr = new TransactionManager("TccReportImpl.getTccData");
            String sql = " select vhtc.appl_no,vhtc.regn_no,vhtc.tcc_no,to_char(vhtc.op_dt, 'dd-Mon-yyyy') op_dt,ftc.fees,ftc.pur_cd\n"
                    + "        from vha_tcc_print vhtc"
                    + "        inner join vt_fee ftc ON vhtc.regn_no = ftc.regn_no where vhtc.regn_no= ?  and pur_cd=72 "
                    + " and vhtc.tcc_no is not null and vhtc.state_cd = ? and vhtc.off_cd = ?  order by op_dt";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);

            rs = tmgr.fetchDetachedRowSet();
            tccDetailslist = new ArrayList<TccDobj>();
            while (rs.next()) {
                dobj = new TccDobj();
                dobj.setAmount(rs.getString("fees"));
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setPrintDt(rs.getString("op_dt"));
                dobj.setTcrNo(rs.getString("tcc_no"));

                tccDetailslist.add(dobj);
            }
            if (dobj == null) {
                tccDetailslist = null;
            }
        } catch (Exception e) {
            tccDetailslist = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                tccDetailslist = null;
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return tccDetailslist;
    }
}
