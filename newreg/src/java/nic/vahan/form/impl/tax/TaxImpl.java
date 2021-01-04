/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.tax;

import java.sql.PreparedStatement;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.tax.TaxDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author ASHOK
 */
public class TaxImpl {

    private static final Logger LOGGER = Logger.getLogger(TaxImpl.class);

    public TaxDobj getTaxDetails(String regnNo, String purCd, String stateCd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String sql = null;
        TaxDobj taxDobj = null;

        try {
            tmgr = new TransactionManager("getTaxDetails");

            sql = "SELECT * FROM " + TableList.VT_TAX
                    + " WHERE regn_no=? and pur_cd in (" + purCd + ") and state_cd=? "
                    + " ORDER BY rcpt_dt desc limit 1";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {

                taxDobj = new TaxDobj();
                taxDobj.setRegn_no(rs.getString("regn_no"));
                taxDobj.setTax_mode(rs.getString("tax_mode"));
                taxDobj.setPayment_mode(rs.getString("payment_mode"));
                taxDobj.setTax_amt(rs.getInt("tax_amt"));
                taxDobj.setTax_fine(rs.getInt("tax_fine"));
                taxDobj.setRcpt_no(rs.getString("rcpt_no"));
                taxDobj.setRcpt_dt(rs.getTimestamp("rcpt_dt"));
                taxDobj.setTax_from(rs.getDate("tax_from"));
                taxDobj.setTax_upto(rs.getDate("tax_upto"));
                taxDobj.setPur_cd(rs.getInt("pur_cd"));
                taxDobj.setFlag(rs.getString("flag"));
                taxDobj.setCollected_by(rs.getString("collected_by"));
                taxDobj.setState_cd(rs.getString("state_cd"));
                taxDobj.setOff_cd(rs.getInt("off_cd"));
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Getting Tax Details for the Registration No " + regnNo + ", Please Contact to the System Administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return taxDobj;
    }

    public String getPrevious_TaxMode(String regnNo, String purCd, String stateCd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String sql = null;
        String tax_mode = null;
        try {
            tmgr = new TransactionManager("getPrevious_TaxMode");
            sql = "select tax_mode from " + TableList.VT_TAX + " where  pur_cd in (" + purCd + ") and rcpt_dt::date >='2012-09-03' and tax_upto >='2012-09-03' and tax_mode not in ('M','Q','Y','B') and regn_no=? and state_cd=? order by rcpt_dt desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                tax_mode = rs.getString("tax_mode");
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Getting Tax Details for the Registration No " + regnNo + ", Please Contact to the System Administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return tax_mode;
    }
}
