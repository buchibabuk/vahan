/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.reports;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.reports.TaxExemCertDobj;
import nic.vahan.form.impl.CommonCarrierRCImpl;
import org.apache.log4j.Logger;

/**
 *
 * @author Mohd Afzal
 */
public class TaxExemCertImpl {

    private static final Logger LOGGER = Logger.getLogger(TaxExemCertImpl.class);

    public static TaxExemCertDobj getTaxExemCertDetails(SessionVariables sessionVariables, String regn_no) throws VahanException {
        TaxExemCertDobj dobj = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;

        try {
            tmgr = new TransactionManager("getCommonCarrierRegnCertListByRenoNoWise");
            sql = "select regn_no from  " + TableList.VT_TAX_EXEM + " \n"
                    + "  where regn_no =? and state_cd = ? and off_cd = ? order by op_dt DESC limit 1\n";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, sessionVariables.getStateCodeSelected());
            ps.setInt(3, sessionVariables.getOffCodeSelected());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                dobj = new TaxExemCertDobj();
                dobj.setRegn_no(rs.getString("regn_no"));

            }
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
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
        }
        return dobj;
    }
}
