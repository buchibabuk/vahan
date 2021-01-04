/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.permit.PermitPaidFeeDtlsDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author R Gautam
 */
public class PermitPaidFeeDtlsImpl {

    private static final Logger LOGGER = Logger.getLogger(PermitPaidFeeDtlsImpl.class);

    public List<PermitPaidFeeDtlsDobj> getListOfPaidFee(String appl_no) throws VahanException {
        List<PermitPaidFeeDtlsDobj> paidFeeList = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("getListOfPaidFee");
            String Query = "SELECT * from get_appl_rcpt_details(?)";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            paidFeeList = new ArrayList<>();
            while (rs.next()) {
                PermitPaidFeeDtlsDobj dobj = new PermitPaidFeeDtlsDobj();
                dobj.setRcpt_no(rs.getString("rcpt_no"));
                dobj.setFees(rs.getInt("fees"));
                dobj.setFine(rs.getInt("fine"));
                dobj.setRcpt_dt(rs.getString("rcpt_dt"));
                dobj.setPurpose(rs.getString("purpose"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setPurCd(rs.getInt("pur_cd"));
                paidFeeList.add(dobj);
            }
            if (paidFeeList.isEmpty()) {
                paidFeeList = null;
            }
        } catch (Exception e) {
            paidFeeList = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Paid Fee Details not Found, Please Contact to the System Administrator");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return paidFeeList;
    }
}
