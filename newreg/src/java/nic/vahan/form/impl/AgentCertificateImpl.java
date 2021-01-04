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
import nic.vahan.form.dobj.reports.AgentCertificateDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author NIC
 */
public class AgentCertificateImpl {

    private static final Logger LOGGER = Logger.getLogger(AgentCertificateImpl.class);

    public static ArrayList<AgentCertificateDobj> getAgentCertPendingList(SessionVariables sessionVariables) throws VahanException {
        ArrayList<AgentCertificateDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        VahanException vahanexecption = null;
        String sql = null;
        AgentCertificateDobj dobj = null;
        try {
            tmgr = new TransactionManager("getPendingRCPrintDocsDetails");
            sql = "select distinct b.appl_no,a.agent_name,a.f_name,a.contact_no,to_char(a.valid_fr,'dd-Mon-yyyy') as valid_from,to_char(a.valid_to,'dd-Mon-yyyy') as valid_to,a.name_of_counter,a.deal_cd,upper ( COALESCE(a.padd1,'') || ', ' || COALESCE(a.padd2,'') || ', ' || COALESCE(a.pcity,'') || ',' || chr(10) || COALESCE(c.descr , '') || '-' || COALESCE(d.descr,'') || '-' || COALESCE(a.ppincode,'')) as p_full_add,\n"
                    + " a.rcpt_no, a.agent_licence_no,a.op_dt,a.state_cd,a.off_cd,a.place_of_business from  " + TableList.VT_AGENT_DETAILS + " a \n"
                    + " left outer join " + TableList.VP_APPL_RCPT_MAPPING + " b on b.rcpt_no = a.rcpt_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd \n"
                    + "  LEFT JOIN " + TableList.TM_DISTRICT + " c ON c.state_cd = a.state_cd AND c.dist_cd::text = a.pdistrict \n"
                    + "  LEFT JOIN " + TableList.TM_STATE + " d ON d.state_code = a.state_cd \n"
                    + "  where a.state_cd = ? and a.off_cd = ? and a.op_dt > current_date - 15 order by a.op_dt DESC \n";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, sessionVariables.getStateCodeSelected());
            ps.setInt(2, sessionVariables.getOffCodeSelected());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                dobj = new AgentCertificateDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRcpt_no(rs.getString("rcpt_no"));
                dobj.setAgent_licence_no(rs.getString("agent_licence_no"));
                dobj.setAgent_name(rs.getString("agent_name"));

                dobj.setF_name(rs.getString("f_name"));
                dobj.setContact_no(rs.getString("contact_no"));
                dobj.setValid_from(rs.getString("valid_from"));
                dobj.setValid_upto(rs.getString("valid_to"));
                dobj.setName_of_counter(rs.getString("name_of_counter"));
                dobj.setDeal_cd(rs.getString("deal_cd"));
                dobj.setP_full_address(rs.getString("p_full_add"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setPlace_of_business(rs.getString("place_of_business"));
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

    public static ArrayList<AgentCertificateDobj> getAgentCertListByAgentLincNoWise(SessionVariables sessionVariables, String agent_licence_no) throws VahanException {
        ArrayList<AgentCertificateDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        AgentCertificateDobj dobj = null;
        try {
            tmgr = new TransactionManager("getCommonCarrierRegnCertListByRenoNoWise");
            sql = "select distinct a.agent_name,a.f_name,a.contact_no,to_char(a.valid_fr,'dd-Mon-yyyy') as valid_from,to_char(a.valid_to,'dd-Mon-yyyy') as valid_to,a.name_of_counter,a.deal_cd,upper ( COALESCE(a.padd1,'') || ', ' || COALESCE(a.padd2,'') || ', ' || COALESCE(a.pcity,'') || ',' || chr(10) || COALESCE(c.descr , '') || '-' || COALESCE(d.descr,'') || '-' || COALESCE(a.ppincode,'')) as p_full_add,\n"
                    + " b.appl_no,a.rcpt_no, a.agent_licence_no,a.op_dt,a.state_cd,a.off_cd,a.place_of_business from  " + TableList.VT_AGENT_DETAILS + " a \n"
                    + " left outer join " + TableList.VP_APPL_RCPT_MAPPING + " b on b.rcpt_no = a.rcpt_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd \n"
                    + "  LEFT JOIN " + TableList.TM_DISTRICT + " c ON c.state_cd = a.state_cd AND c.dist_cd::text = a.pdistrict\n"
                    + "  LEFT JOIN " + TableList.TM_STATE + " d ON d.state_code = a.state_cd\n"
                    + "  where a.agent_licence_no =? and a.state_cd = ? and a.off_cd = ? order by a.op_dt DESC limit 1\n";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, agent_licence_no);
            ps.setString(2, sessionVariables.getStateCodeSelected());
            ps.setInt(3, sessionVariables.getOffCodeSelected());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                dobj = new AgentCertificateDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRcpt_no(rs.getString("rcpt_no"));
                dobj.setAgent_licence_no(rs.getString("agent_licence_no"));
                dobj.setAgent_name(rs.getString("agent_name"));
                dobj.setF_name(rs.getString("f_name"));
                dobj.setContact_no(rs.getString("contact_no"));
                dobj.setValid_from(rs.getString("valid_from"));
                dobj.setValid_upto(rs.getString("valid_to"));
                dobj.setName_of_counter(rs.getString("name_of_counter"));
                dobj.setDeal_cd(rs.getString("deal_cd"));
                dobj.setP_full_address(rs.getString("p_full_add"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setPlace_of_business(rs.getString("place_of_business"));
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

    public static AgentCertificateDobj getAgentReportDobj(String agent_licence_no, String state_cd, int off_cd) throws VahanException {
        TransactionManager tmgr = null;
        AgentCertificateDobj dobj = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        PreparedStatement ps = null;
        List<AgentCertificateDobj> list = new ArrayList<AgentCertificateDobj>();
        try {
            tmgr = new TransactionManager("getComCarRCReportDobj");
            String sql = "select distinct a.agent_name,a.f_name,a.contact_no,to_char(a.valid_fr,'dd-Mon-yyyy') as valid_from,to_char(a.valid_to,'dd-Mon-yyyy') as valid_to,a.name_of_counter,a.deal_cd,upper ( COALESCE(a.padd1,'') || ', ' || COALESCE(a.padd2,'') || ', ' || COALESCE(a.pcity,'') || ',' || chr(10) || COALESCE(c.descr , '') || '-' || COALESCE(d.descr,'') || '-' || COALESCE(a.ppincode,'')) as p_full_add,\n"
                    + " b.appl_no,a.rcpt_no, a.agent_licence_no,a.op_dt,a.state_cd,a.off_cd,e.off_name,f.pur_cd,a.place_of_business from  " + TableList.VT_AGENT_DETAILS + " a \n"
                    + " left outer join " + TableList.VP_APPL_RCPT_MAPPING + " b on b.rcpt_no = a.rcpt_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd \n"
                    + "  LEFT JOIN " + TableList.TM_DISTRICT + " c ON c.state_cd = a.state_cd AND c.dist_cd::text = a.district\n"
                    + "  LEFT JOIN " + TableList.TM_STATE + " d ON d.state_code = a.state_cd\n"
                    + " left outer join " + TableList.TM_OFFICE + " e on e.state_cd = a.state_cd and e.off_cd = a.off_cd \n"
                    + " left outer join (select * from vt_fee where regn_no=? and state_cd=? and off_cd=? order by rcpt_dt desc limit 1) f on f.regn_no = a.agent_licence_no \n"
                    + "  where a.agent_licence_no= ? \n";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, agent_licence_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            ps.setString(4, agent_licence_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new AgentCertificateDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRcpt_no(rs.getString("rcpt_no"));
                dobj.setAgent_licence_no(rs.getString("agent_licence_no"));
                dobj.setAgent_name(rs.getString("agent_name"));

                dobj.setF_name(rs.getString("f_name"));
                dobj.setContact_no(rs.getString("contact_no"));
                dobj.setValid_from(rs.getString("valid_from"));
                dobj.setValid_upto(rs.getString("valid_to"));
                dobj.setName_of_counter(rs.getString("name_of_counter"));
                dobj.setDeal_cd(rs.getString("deal_cd"));
                dobj.setP_full_address(rs.getString("p_full_add"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setPlace_of_business(rs.getString("place_of_business"));

                if (rs.getInt("pur_cd") == TableConstants.AGENT_DETAIL_REN_PUR_CD) {
                    dobj.setPur_desc("Renewal of Agent Licence Certificate");
                }
                if (rs.getInt("pur_cd") == TableConstants.AGENT_DETAIL_DUP_PUR_CD) {
                    dobj.setPur_desc("Duplicate of Agent Licence Certificate");
                }
                if (rs.getInt("pur_cd") == TableConstants.AGENT_DETAIL_PUR_CD) {
                    dobj.setPur_desc("");
                }
            }
        } catch (SQLException sqle) {
            throw new VahanException("Error in Getting details for Registration number :" + agent_licence_no + " !!!");
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
