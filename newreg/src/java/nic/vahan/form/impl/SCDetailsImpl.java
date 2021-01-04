/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.SCDetailsDobj;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC075
 */
public class SCDetailsImpl {

    private static final Logger LOGGER = Logger.getLogger(SCDetailsImpl.class);

    public static SCDetailsDobj getSCDetails(String regNo, boolean smartcardStatus, String stateCd, int offCd) {
        boolean scIssued = false;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        boolean issmartcard = smartcardStatus;
        SCDetailsDobj dobj = null;
        SCDetailsDobj masterDobj = new SCDetailsDobj();
        List<SCDetailsDobj> list = new ArrayList<SCDetailsDobj>();
        List<SCDetailsDobj> smpendiglist = new ArrayList<SCDetailsDobj>();
        List<SCDetailsDobj> vhsmlist = new ArrayList<SCDetailsDobj>();
        List<SCDetailsDobj> vasmlist = new ArrayList<SCDetailsDobj>();
        try {
            tmgr = new TransactionManagerReadOnly("SCDetailsImpl.getSCDetails");
            if (issmartcard) {
                String sqlQueryiatobe = "select a.rcpt_no,a.deal_cd,a.pur_cd as purpose,to_char(a.rcissuedate,'dd-Mon-yyyy') as rcissuedate,a.rccardchipno,a.drto1name,a.drto2name"
                        + " from " + TableList.RC_IA_TO_BE + " a "
                        + " where status='0' and vehregno in (?, rpad(?, 10, ?), ?) order by a.rcissuedate";
                ps = tmgr.prepareStatement(sqlQueryiatobe);
                ps.setString(1, regNo);
                ps.setString(2, regNo);
                ps.setString(3, " ");
                ps.setString(4, ServerUtil.getRegnNoWithSpace(regNo));

                rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    dobj = new SCDetailsDobj();
                    masterDobj.setSmartcarddatatable(true);
                    dobj.setRcptno(rs.getString("rcpt_no"));
                    dobj.setPurcd(rs.getString("purpose"));
                    dobj.setRccardchipno(rs.getString("rccardchipno"));
                    dobj.setRcissuedt(rs.getString("rcissuedate"));
                    dobj.setDrto1name(rs.getString("drto1name"));
                    dobj.setDrto2name(rs.getString("drto2name"));
                    list.add(dobj);
                }
                masterDobj.setLabelValue("smart card printed");
                masterDobj.setPageHeader("Smart Card Details");
                masterDobj.setScList(list);
                masterDobj.setSmartcard(issmartcard);
                String sqlQuerybetobo = "select a.rcpt_no,a.pur_cd as purpose"
                        + " from " + TableList.RC_BE_TO_BO + " a where status='1' and   vehregno in (?, rpad(?, 10, ?), ?)";
                ps = tmgr.prepareStatement(sqlQuerybetobo);
                ps.setString(1, regNo);
                ps.setString(2, regNo);
                ps.setString(3, " ");
                ps.setString(4, ServerUtil.getRegnNoWithSpace(regNo));

                rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    if (dobj == null) {
                        dobj = new SCDetailsDobj();
                    }
                    masterDobj.setSmartcarddatalist(true);
                    dobj.setScpendingdtls("Smart Card Pending for KMS against Application/Receipt No " + rs.getString("rcpt_no") + " (Purpose " + rs.getString("purpose") + ")");
                    smpendiglist.add(dobj);
                }
                masterDobj.setScpendingList(smpendiglist);
                masterDobj.setTabTitle("SmartCard Details");
            } else {

                String sqlQueryvhsm = "select appl_no,regn_no,to_char(printed_on,'dd-Mon-yyyy') as printedon,printed_by  from " + TableList.VH_RC_PRINT + ""
                        + " where regn_no=? order by printed_on";
                ps = tmgr.prepareStatement(sqlQueryvhsm);
                ps.setString(1, regNo);
                rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    if (dobj == null) {
                        dobj = new SCDetailsDobj();
                    }
                    masterDobj.setRcdatatable(true);
                    dobj.setApplno(rs.getString("appl_no"));
                    dobj.setPurcd(ServerUtil.getPurposeShortDescr(rs.getString("appl_no")));
                    dobj.setPrintedon(rs.getString("printedon"));
                    String printedBy = rs.getString("printed_by");
                    if (rs.getString("printed_by").replaceAll("[^0-9]", "0").equalsIgnoreCase(rs.getString("printed_by"))) {
                        printedBy = ServerUtil.getUserName(Long.parseLong(rs.getString("printed_by").trim().toUpperCase().replaceAll("[^0-9]", "0")));
                        if (printedBy == null || printedBy.isEmpty()) {
                            printedBy = rs.getString("printed_by");
                        }
                    }
                    dobj.setPrintedby(printedBy);
                    vhsmlist.add(dobj);
                }
                String sqlQueryvasm = "select appl_no from " + TableList.VA_RC_PRINT + ""
                        + " where regn_no=? and state_cd=? and off_cd=?";
                ps = tmgr.prepareStatement(sqlQueryvasm);
                ps.setString(1, regNo);
                ps.setString(2, stateCd);
                ps.setInt(3, offCd);
                rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    if (dobj == null) {
                        dobj = new SCDetailsDobj();
                    }
                    masterDobj.setRcddatalist(true);
                    dobj.setRcpendingdtls("RC Printing Pending against Application/Receipt No " + rs.getString("appl_no") + " (Purpose " + ServerUtil.getPurposeShortDescr(rs.getString("appl_no")) + ")");
                    vasmlist.add(dobj);
                }
                masterDobj.setVhscList(vhsmlist);
                masterDobj.setVascList(vasmlist);
                masterDobj.setLabelValue("RC printed");
                masterDobj.setPageHeader("RC Print Details");
                masterDobj.setSmartcard(issmartcard);
                masterDobj.setTabTitle("RC Print Details");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return masterDobj;
    }
}
