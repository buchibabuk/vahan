/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.reports.CommonCarrierRCDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author NIC
 */
public class CommonCarrierRCImpl {

    private static final Logger LOGGER = Logger.getLogger(CommonCarrierRCImpl.class);

    public static ArrayList<CommonCarrierRCDobj> getCommonCarrierRegnCertList(SessionVariables sessionVariables) throws VahanException {
        ArrayList<CommonCarrierRCDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        VahanException vahanexecption = null;
        String sql = null;
        CommonCarrierRCDobj dobj = null;
        try {
            tmgr = new TransactionManager("getPendingRCPrintDocsDetails");
            sql = "select distinct b.appl_no,a.rcpt_no, a.cc_regn_no,a.op_dt,a.state_cd,a.off_cd from  " + TableList.VT_COMMON_CARRIERS + " a \n"
                    + " left outer join " + TableList.VP_APPL_RCPT_MAPPING + " b on b.rcpt_no = a.rcpt_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd \n"
                    + "  where a.state_cd = ? and a.off_cd = ? and a.op_dt > current_date - 15 order by a.op_dt DESC \n";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, sessionVariables.getStateCodeSelected());
            ps.setInt(2, sessionVariables.getOffCodeSelected());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                dobj = new CommonCarrierRCDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRcpt_no(rs.getString("rcpt_no"));
                dobj.setRegn_no(rs.getString("cc_regn_no"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                list.add(dobj);

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
        return list;
    }

    public static ArrayList<CommonCarrierRCDobj> getCommonCarrierRegnCertListByRenoNoWise(SessionVariables sessionVariables, String regn_no) throws VahanException {
        ArrayList<CommonCarrierRCDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        CommonCarrierRCDobj dobj = null;
        try {
            tmgr = new TransactionManager("getCommonCarrierRegnCertListByRenoNoWise");
            sql = "select distinct b.appl_no,a.rcpt_no, a.cc_regn_no,a.op_dt,a.state_cd,a.off_cd from  " + TableList.VT_COMMON_CARRIERS + " a \n"
                    + " left outer join " + TableList.VP_APPL_RCPT_MAPPING + " b on b.rcpt_no = a.rcpt_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd \n"
                    + "  where a.cc_regn_no =? and a.state_cd = ? and a.off_cd = ? order by a.op_dt DESC limit 1\n";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, sessionVariables.getStateCodeSelected());
            ps.setInt(3, sessionVariables.getOffCodeSelected());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                dobj = new CommonCarrierRCDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRcpt_no(rs.getString("rcpt_no"));
                dobj.setRegn_no(rs.getString("cc_regn_no"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                list.add(dobj);

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
        return list;
    }

    public static CommonCarrierRCDobj getComCarRCReportDobj(String regn_no, String rcpt_no, String state_cd, int off_cd) throws VahanException {
        TransactionManager tmgr = null;
        CommonCarrierRCDobj dobj = null;
        PreparedStatement ps = null;
        CommonCarrierRCDobj subListDobj = null;
        List<CommonCarrierRCDobj> list = new ArrayList<CommonCarrierRCDobj>();
        try {
            tmgr = new TransactionManager("getComCarRCReportDobj");
            String sql = "select a.cc_regn_no,to_char(a.valid_fr,'dd-MON-yyyy') as valid_fr,to_char(a.valid_to,'dd-MON-yyyy') as valid_to,a.org_name,a.address,a.authorized_person,a.contact_no,b.off_name,c.pur_cd from  " + TableList.VT_COMMON_CARRIERS + " a \n"
                    + " left outer join " + TableList.TM_OFFICE + " b on b.state_cd = a.state_cd and b.off_cd = a.off_cd \n"
                    + " left outer join (select * from vt_fee where regn_no=? and state_cd=? and off_cd=? order by rcpt_dt desc limit 1) c on c.regn_no = a.cc_regn_no"
                    + "  where a.cc_regn_no= ? and a.rcpt_no= ? and a.state_cd = ? and a.off_cd = ? \n";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            ps.setString(4, regn_no);
            ps.setString(5, rcpt_no);
            ps.setString(6, state_cd);
            ps.setInt(7, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new CommonCarrierRCDobj();
                dobj.setRegn_no(rs.getString("cc_regn_no"));
                dobj.setIssue_Date(rs.getString("valid_fr"));
                dobj.setValid_upto(rs.getString("valid_to"));
                dobj.setOrganization_name(rs.getString("org_name"));
                dobj.setOffice_details(rs.getString("address"));
                dobj.setAuth_Person_name(rs.getString("authorized_person"));
                dobj.setAuth_Person_contact_Details(rs.getString("contact_no"));
                dobj.setOff_name(rs.getString("off_name"));
                if (rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_CARRIER_REGN) {
                    dobj.setPur_Desc("");
                }
                if (rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_CARRIER_RENEWAL) {
                    dobj.setPur_Desc("Renewal of Commom Carrier Certificate");
                }
                if (rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_CARRIER_DUPLICATE) {
                    dobj.setPur_Desc("Duplicate of Commom Carrier Certificate");
                }

                String sqlCCRCSubList = "select location,address,hub_centre,to_char(commencement_dt,'dd-MON-yyyy') as commencement_dt from " + TableList.VT_CC_BRANCH_DETAILS + " where cc_regn_no = ? and rcpt_no = ? and state_cd = ? and off_cd = ? ";
                ps = tmgr.prepareStatement(sqlCCRCSubList);
                ps.setString(1, regn_no);
                ps.setString(2, rcpt_no);
                ps.setString(3, state_cd);
                ps.setInt(4, off_cd);
                RowSet rsList = tmgr.fetchDetachedRowSet();
                while (rsList.next()) {
                    subListDobj = new CommonCarrierRCDobj();
                    subListDobj.setLocation(rsList.getString("location"));
                    subListDobj.setFullAddress_withContact(rsList.getString("address"));
                    subListDobj.setNameOf_usageOffice_Godown_Centre(rsList.getString("hub_centre"));
                    subListDobj.setDate_Of_Commencement(rsList.getString("commencement_dt"));
                    list.add(subListDobj);
                }
                dobj.setPrintCCSubList(list);
            }
        } catch (SQLException sqle) {
            throw new VahanException("Error in Getting details for Registration number :" + regn_no + " !!!");
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
            }
        }
        return dobj;

    }
}
