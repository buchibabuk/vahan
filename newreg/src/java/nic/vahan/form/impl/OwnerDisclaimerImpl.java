/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.OwnerDisclaimerDobj;
import org.apache.log4j.Logger;
import nic.vahan.db.TableConstants;

/**
 *
 * @author nic
 */
public class OwnerDisclaimerImpl {

    private static final Logger LOGGER = Logger.getLogger(OwnerDisclaimerImpl.class);

    public static ArrayList<OwnerDisclaimerDobj> getPurCdPrintDocsDetails() throws VahanException {
        ArrayList<OwnerDisclaimerDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            tmgr = new TransactionManager("getPurCdPrintDocsDetails");
            ps = tmgr.prepareStatement("select distinct a.appl_no, a.regn_no, b.state_cd, b.off_cd,b.appl_dt,b.pur_cd"
                    + " from " + TableList.VA_OWNER + " a, " + TableList.VA_DETAILS + " b"
                    + " where a.appl_no = b.appl_no and a.state_cd=b.state_cd and a.off_cd=b.off_cd and "
                    + " b.state_cd = ? and b.off_cd = ? and b.pur_cd NOT IN (" + TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE + "," + TableConstants.VM_TRANSACTION_MAST_TEMP_REG + ")");
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) // found
            {
                OwnerDisclaimerDobj dobj = new OwnerDisclaimerDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegno(rs.getString("regn_no"));
                if (rs.getDate("appl_dt") != null && !rs.getDate("appl_dt").equals("")) {;
                    dobj.setAppl_dt(format.format(rs.getDate("appl_dt")));
                }
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setPurCd(rs.getInt("pur_cd"));
                list.add(dobj);
            }


        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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
}
