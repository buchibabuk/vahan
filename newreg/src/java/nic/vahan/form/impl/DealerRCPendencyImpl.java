/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.ApplicationStatusDobj;
import nic.vahan.form.dobj.reports.DealerRCPendencyDobj;
import static nic.vahan.form.impl.Home_Impl.getColourAndAppStatus;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Afzal
 */
public class DealerRCPendencyImpl {

    private static final Logger LOGGER = Logger.getLogger(DealerRCPendencyImpl.class);

    public static ArrayList<DealerRCPendencyDobj> getDealerRCPenDetails(int office_cd, String state_cd, String DealerCode) throws VahanException {
        ArrayList<DealerRCPendencyDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        VahanException vahanexecption = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        try {
            tmgr = new TransactionManagerReadOnly("getPurCdPrintDocsDetails");
            ps = tmgr.prepareStatement("SELECT * from vahan4.get_dealer_registration_pendency(?,?,?)");
            ps.setString(1, state_cd);
            ps.setLong(2, office_cd);
            ps.setString(3, DealerCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                DealerRCPendencyDobj dobj = new DealerRCPendencyDobj();
                dobj.setOffName(rs.getString("off_name"));
                dobj.setDealerName(rs.getString("dealer_name"));
                dobj.setApplNO(rs.getString("appl_no"));
                dobj.setRegnNO(rs.getString("regn_no"));
                dobj.setOwnerName(rs.getString("owner_name"));
                dobj.setPurpose(rs.getString("purpose"));
                dobj.setStatus(rs.getString("action_abbrv"));
                dobj.setPendingFrom(format.format(rs.getTimestamp("pending_since")));
                dobj.setChasiNO(rs.getString("chasi_no"));
                dobj.setModelName(rs.getString("model_name"));
                dobj.setHypth(rs.getString("hypth"));
                Map<String, ApplicationStatusDobj> applicationStatus = MasterTableFiller.applStatus;
                if (!CommonUtils.isNullOrBlank(rs.getString("seat_cd"))) {
                    if (applicationStatus != null && !applicationStatus.isEmpty()) {
                        String[] colourAndApplStatus = getColourAndAppStatus(rs.getString("seat_cd"), applicationStatus);
                        dobj.setApplStatus(colourAndApplStatus[1]);
                    }
                } else {
                    if (applicationStatus != null && !applicationStatus.isEmpty()) {
                        ApplicationStatusDobj clrDbj = applicationStatus.get(TableConstants.STATUS_COMPLETE);
                        if (clrDbj != null) {
                            dobj.setApplStatus(clrDbj.getDescr());
                        }
                    }
                }
                list.add(dobj);
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw vahanexecption;
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
