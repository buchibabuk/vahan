/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.printRCCancelDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author acer
 */
public class PrintRCCancelImpl {

    private static final Logger LOGGER = Logger.getLogger(PrintRCCancelImpl.class);

    public printRCCancelDobj getRCCancelDetails(String regn_no) throws VahanException {

        printRCCancelDobj rcCancelDobj = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;


        String sql = " select a.appl_no,a.regn_no,b.chasi_no,b.eng_no,b.manu_mon,b.manu_yr,b.vh_class,b.vch_catg,b.body_type,"
                + " to_char(b.regn_dt,'dd-MON-yyyy') regn_dt,to_char(c.tax_upto,'dd-MON-yyyy') tax_upto,to_char(a.cancel_dt,'dd-MON-yyyy') fit_chk_dt,g.descr as vh_class_desc,b.body_type,max(c.rcpt_dt),e.rcpt_heading,e.rcpt_subheading,f.off_name"
                + " from " + TableList.VT_RC_CANCEL + " a"
                + " inner join " + TableList.VT_OWNER + " b on b.regn_no=a.regn_no"
                + " left join " + TableList.VT_TAX + " c on c.regn_no=b.regn_no and c.state_cd=b.state_cd and c.off_cd = b.off_cd and c.rcpt_dt=(select rcpt_dt from vt_tax t1  where b.state_cd=t1.state_cd and b.off_cd=t1.off_cd and b.regn_no=t1.regn_no and t1.pur_cd = 58 order by t1.rcpt_dt desc limit 1)"
                + " left join " + TableList.TM_CONFIGURATION + " e on e.state_cd = b.state_cd "
                + " left join " + TableList.TM_OFFICE + " f on f.state_cd = b.state_cd and f.off_cd = b.off_cd "
                + " left join " + TableList.VM_VH_CLASS + " g on g.vh_class = b.vh_class"
                + " where a.regn_no = ? and b.state_cd = ? "
                + " group by a.regn_no,b.chasi_no,b.eng_no,b.manu_mon,"
                + " b.manu_yr,b.vh_class,b.vch_catg,b.body_type,b.regn_dt,c.tax_upto,fit_chk_dt,g.descr,e.rcpt_heading,e.rcpt_subheading,f.off_name";
        try {
            tmgr = new TransactionManager("getRCCancelDetails");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no.toUpperCase().trim());
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                rcCancelDobj = new printRCCancelDobj();
                rcCancelDobj.setAppl_no(rs.getString("appl_no"));
                rcCancelDobj.setRegn_no(rs.getString("regn_no"));
                rcCancelDobj.setChasi_no(rs.getString("chasi_no"));
                rcCancelDobj.setEng_no(rs.getString("eng_no"));
                rcCancelDobj.setManu_mon(rs.getInt("manu_mon"));
                rcCancelDobj.setManu_yr(rs.getInt("manu_yr"));
                rcCancelDobj.setVh_class(rs.getInt("vh_class"));
                rcCancelDobj.setVch_catg(rs.getString("vch_catg"));
                rcCancelDobj.setBody_type(rs.getString("body_type"));
                rcCancelDobj.setRegn_dt(rs.getString("regn_dt"));
                rcCancelDobj.setTax_upto(rs.getString("tax_upto"));
                rcCancelDobj.setFit_chk_dt(rs.getString("fit_chk_dt"));
                rcCancelDobj.setVh_class_desc(rs.getString("vh_class_desc"));
                rcCancelDobj.setReportHeading(rs.getString("rcpt_heading"));
                rcCancelDobj.setReportSubHeading(rs.getString("rcpt_subheading"));
                rcCancelDobj.setOff_name(rs.getString("off_name"));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Unable to get RC cancel details.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return rcCancelDobj;
    }

    public boolean checkRCCancel(String regn_no) throws VahanException {
        boolean isRCCancel = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("checkRCCancel");
            ps = tmgr.prepareStatement("select * from " + TableList.VT_RC_CANCEL + " where regn_no = ?");
            ps.setString(1, regn_no.toUpperCase().trim());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isRCCancel = true;
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in checking RC Cancel Details");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
            return isRCCancel;
        }
    }
}
