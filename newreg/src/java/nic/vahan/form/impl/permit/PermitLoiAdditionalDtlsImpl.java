/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.permit.PermitLoiAdditionalDtlsDobj;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author R Gautam
 */
public class PermitLoiAdditionalDtlsImpl {

    private static final Logger LOGGER = Logger.getLogger(PermitLoiAdditionalDtlsImpl.class);

    public PermitLoiAdditionalDtlsDobj getVpLoiDtls(String appl_no) {
        PermitLoiAdditionalDtlsDobj dobj = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getVpLoiDtls");
            String Query = "SELECT appl_no,dl_no,image_data,psv_no,aadhar_no from onlineschema.vp_loi_dtls where state_cd = ? and  appl_no = ?";
            PreparedStatement ps = tmgr.prepareStatement(Query);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new PermitLoiAdditionalDtlsDobj();
                dobj.setAdharCardNo(rs.getString("aadhar_no"));
                dobj.setBatchNo(rs.getString("psv_no"));
                dobj.setDlNo(rs.getString("dl_no"));
                dobj.setImage(rs.getBytes("image_data"));
            }
        } catch (SQLException e) {
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
        return dobj;
    }
}
