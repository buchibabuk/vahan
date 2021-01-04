/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.ApplRegStatus_dobj;
import org.apache.log4j.Logger;

/**
 *
 * @author nic5912
 */
public class ApplRegStatus_Impl {

    private static Logger LOGGER = Logger.getLogger(ApplRegStatus_Impl.class);
    private static String vahanExceptionMessage = "Problem In fetching Details";

    public static String getApplNo(String chasiNo, String state_cd, int off_cd) throws VahanException {
        String applNo = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            String sql = "Select appl_no from " + TableList.VA_OWNER + " where chasi_no = ? and state_cd =? and off_cd =?";
            tmgr = new TransactionManagerReadOnly("getApplicationNo");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, chasiNo);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                applNo = rs.getString("appl_no");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(vahanExceptionMessage);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(vahanExceptionMessage);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return applNo;
    }

    ////////// Appl Status for state admin 
    public static int getShowRtoDetails(String val, String state_cd, String option) throws VahanException {
        int off_cd = 0;
        TransactionManagerReadOnly tmgr = null;
        try {
            String table = "";
            String column = "";

            if ("applno".equalsIgnoreCase(option)) {
                column = "appl_no";
                table = TableList.VA_DETAILS;
            } else if ("regno".equalsIgnoreCase(option)) {
                column = "regn_no";
                table = TableList.VA_DETAILS;
            } else if ("chasino".equalsIgnoreCase(option)) {
                column = "chasi_no";
                table = TableList.VA_OWNER;
            }
            String sql = "Select off_cd from " + table + " where " + column + " = ? and state_cd =?";
            tmgr = new TransactionManagerReadOnly("getApplicationNo");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, val);
            ps.setString(2, state_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                off_cd = rs.getInt("off_cd");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(vahanExceptionMessage);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(vahanExceptionMessage);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return off_cd;
    }

    public static ArrayList<ApplRegStatus_dobj> getCurrentStatus(String applNo, String regnNo, String state_cd, int off_cd) throws VahanException {
        ArrayList<ApplRegStatus_dobj> list = new ArrayList<ApplRegStatus_dobj>();
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        String whereConditionVar = null;
        String vaStatusVar = null;
        String vhaStatusVar = null;
        String dealerCdVar = null;
        String applNoList = "";
        String regn_no = "";
        try {

            tmgr = new TransactionManagerReadOnly("getStatusByApplicationNo");

            if (applNo != null && !applNo.equals("") && regnNo != null && !regnNo.equals("")) {
                whereConditionVar = " WHERE a.appl_no = ? and (vad.regn_no = ? or vhad.regn_no = ?) ";
                vaStatusVar = " FROM va_status va WHERE  state_cd = ? and off_cd = ? and appl_no = ? and file_movement_slno = (select max(file_movement_slno )  FROM va_status va1 WHERE  va1.state_cd = va.state_cd and va1.off_cd = va.off_cd and va1.appl_no = va.appl_no and va1.pur_cd=va.pur_cd )";
                vhaStatusVar = " FROM vha_status vha WHERE  state_cd = ? and off_cd = ?  and appl_no = ? and file_movement_slno = (select max(file_movement_slno )  FROM vha_status vha1 WHERE  vha1.state_cd = vha.state_cd and vha1.off_cd = vha.off_cd and vha1.appl_no = vha.appl_no  and vha1.pur_cd=vha.pur_cd ) and pur_cd not in (select pur_cd FROM va_status va WHERE  va.state_cd = vha.state_cd and va.off_cd = vha.off_cd and va.appl_no = vha.appl_no) ) a";
                dealerCdVar = " select dealer_cd from va_owner o  where state_cd = ? and off_cd = ? and appl_no = ? \n"
                        + " UNION ALL \n"
                        + " select dealer_cd from vt_owner o  where state_cd = ? and off_cd = ? and regn_no = ? ";
            } else if (applNo != null && !applNo.equals("")) {
                String selectSql = "select regn_no from va_details va where appl_no = ? "
                        + " UNION ALL "
                        + " select regn_no from vha_details where appl_no = ? ";
                PreparedStatement ps = tmgr.prepareStatement(selectSql);
                ps.setString(1, applNo);
                ps.setString(2, applNo);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    regn_no = rs.getString("regn_no");
                }
                whereConditionVar = " WHERE a.appl_no = ? ";
                vaStatusVar = " FROM va_status va WHERE  state_cd = ? and off_cd = ? and appl_no = ? and file_movement_slno = (select max(file_movement_slno )  FROM va_status va1 WHERE  va1.state_cd = va.state_cd and va1.off_cd = va.off_cd and va1.appl_no = va.appl_no  and va1.pur_cd=va.pur_cd )";
                vhaStatusVar = " FROM vha_status vha WHERE  state_cd = ? and off_cd = ?  and appl_no = ? and file_movement_slno = (select max(file_movement_slno )  FROM vha_status vha1 WHERE  vha1.state_cd = vha.state_cd and vha1.off_cd = vha.off_cd and vha1.appl_no = vha.appl_no  and vha1.pur_cd=vha.pur_cd ) and pur_cd not in (select pur_cd FROM va_status va WHERE  va.state_cd = vha.state_cd and va.off_cd = vha.off_cd and va.appl_no = vha.appl_no) ) a";
                dealerCdVar = " select distinct dealer_cd from va_owner o  where state_cd = ? and off_cd = ? and appl_no = ? \n"
                        + " UNION ALL \n"
                        + " select distinct dealer_cd from vt_owner o  where state_cd = ? and off_cd = ? and regn_no = '" + regn_no + "' limit 1 ";
            } else if (regnNo != null && !regnNo.equals("")) {
                String selectSql = "select appl_no from va_details va where regn_no = ? "
                        + " UNION ALL "
                        + " select appl_no from vha_details where regn_no = ? ";
                PreparedStatement ps = tmgr.prepareStatement(selectSql);
                ps.setString(1, regnNo);
                ps.setString(2, regnNo);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    applNoList += "'" + rs.getString("appl_no") + "',";
                }
                if (!applNoList.isEmpty()) {
                    applNoList = applNoList.substring(0, applNoList.length() - 1);
                } else {
                    throw new VahanException("Invalid Registration No");
                }
                whereConditionVar = " WHERE (vad.regn_no = ? or vhad.regn_no = ?) ";
                vaStatusVar = " FROM va_status va WHERE  state_cd = ? and off_cd = ? and appl_no IN (" + applNoList + ") and file_movement_slno = (select max(file_movement_slno ) FROM va_status va1 WHERE  va1.state_cd = va.state_cd and va1.off_cd = va.off_cd and va1.appl_no = va.appl_no and va1.pur_cd=va.pur_cd  )";
                vhaStatusVar = " FROM vha_status vha WHERE  state_cd = ? and off_cd = ?  and appl_no IN (" + applNoList + ") and file_movement_slno = (select max(file_movement_slno )  FROM vha_status vha1 WHERE  vha1.state_cd = vha.state_cd and vha1.off_cd = vha.off_cd and vha1.appl_no = vha.appl_no and vha1.pur_cd=vha.pur_cd ) and pur_cd not in (select pur_cd FROM va_status va WHERE  va.state_cd = vha.state_cd and va.off_cd = vha.off_cd and va.appl_no = vha.appl_no) ) a";
                dealerCdVar = " select distinct dealer_cd from va_owner o  where state_cd = ? and off_cd = ? and appl_no IN (" + applNoList + ") \n"
                        + " UNION ALL \n"
                        + " select distinct dealer_cd from vt_owner o  where state_cd = ? and off_cd = ? and regn_no = ? limit 1 ";
            }
            sql = "select distinct a.appl_no,COALESCE(vad.appl_dt,vhad.appl_dt) as appl_dt, COALESCE(vad.regn_no,vhad.regn_no) as regn_no,a.pur_cd,c.descr, \n"
                    + " case when vad.entry_status='A' then 'A' when COALESCE(vhad.entry_status,'') <> '' and a.status = 'D' then 'D' when a.status='D' and COALESCE(vhad.entry_status,'') = '' then 'FC' else a.status end status ,\n"
                    + " a.action_cd,b.action_abbrv,a.emp_cd,COALESCE(ui.user_name,a.emp_cd::text) AS approved_by,a.moved_on,(" + dealerCdVar + ") as dealer_cd, off.off_name as off_name from \n"
                    + " (SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno, \n"
                    + " action_cd, seat_cd, cntr_id, status, office_remark, public_remark, emp_cd,  op_dt moved_on " + vaStatusVar + " \n"
                    + " UNION ALL \n"
                    + " SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno, \n"
                    + " action_cd, seat_cd, cntr_id, status, office_remark, public_remark, emp_cd, moved_on " + vhaStatusVar + " \n "
                    + " LEFT OUTER JOIN va_details vad on vad.appl_no=a.appl_no and vad.pur_cd = a.pur_cd \n"
                    + " LEFT OUTER JOIN vha_details vhad on vhad.appl_no=a.appl_no and vhad.pur_cd = a.pur_cd \n"
                    + " LEFT OUTER JOIN tm_action b on b.action_cd = a.action_cd \n"
                    + " LEFT OUTER JOIN " + TableList.TM_USER_INFO + " ui on ui.user_cd = a.emp_cd\n"
                    + " LEFT OUTER JOIN tm_office off on off.off_cd = a.off_cd and off.state_cd=a.state_cd\n"
                    + " LEFT OUTER JOIN tm_purpose_mast c ON c.pur_cd = a.pur_cd " + whereConditionVar + " order by a.appl_no desc ,c.descr asc";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, state_cd);
            ps.setInt(i++, off_cd);
            if ((applNo != null && !applNo.equals("") && regnNo != null && !regnNo.equals(""))
                    || (applNo != null && !applNo.equals(""))) {
                if (applNo != null && !applNo.equals("") && regnNo != null && !regnNo.equals("")) {
                    ps.setString(i++, applNo);
                    ps.setString(i++, state_cd);
                    ps.setInt(i++, off_cd);
                    ps.setString(i++, regnNo);
                } else if (applNo != null && !applNo.equals("")) {
                    ps.setString(i++, applNo);
                    ps.setString(i++, state_cd);
                    ps.setInt(i++, off_cd);
                }
            } else if (regnNo != null && !regnNo.equals("")) {
                ps.setString(i++, state_cd);
                ps.setInt(i++, off_cd);
                ps.setString(i++, regnNo);
            }

            ps.setString(i++, state_cd);
            ps.setInt(i++, off_cd);
            if ((applNo != null && !applNo.equals("") && regnNo != null && !regnNo.equals(""))
                    || (applNo != null && !applNo.equals(""))) {
                ps.setString(i++, applNo);
                ps.setString(i++, state_cd);
                ps.setInt(i++, off_cd);
                ps.setString(i++, applNo);
                if (applNo != null && !applNo.equals("") && regnNo != null && !regnNo.equals("")) {
                    ps.setString(i++, applNo);
                    ps.setString(i++, regnNo);
                    ps.setString(i++, regnNo);
                } else if (applNo != null && !applNo.equals("")) {
                    ps.setString(i++, applNo);
                }
            } else if (regnNo != null && !regnNo.equals("")) {
                ps.setString(i++, state_cd);
                ps.setInt(i++, off_cd);
                ps.setString(i++, regnNo);
                ps.setString(i++, regnNo);
            }
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            ApplRegStatus_dobj dobj = null;
            while (rs.next()) {
                dobj = new ApplRegStatus_dobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setAppl_dt(Utility.convertdateFormatString(rs.getString("appl_dt")));
                dobj.setRegno(rs.getString("regn_no"));
                dobj.setPurCdDescr(rs.getString("descr"));
                dobj.setPurCd(rs.getInt("pur_cd"));
                dobj.setStatus(rs.getString("status"));
                dobj.setDealerCd(rs.getString("dealer_cd"));
                dobj.setOffName(rs.getString("off_name"));
                boolean chkHsrp = false;
                boolean chkSmartCard = false;
                boolean chkRCPrint = false;
                boolean chkFCPrint = false;
                boolean chkDispatchRC = false;
                int actioncd = rs.getInt("action_cd");
                if (dobj.getStatus().equalsIgnoreCase("A")) {
                    dobj.setApprovalStatus("A");
                    dobj.setStatusDesc("COMPLETED / APPROVED ON " + Utility.convertdateFormatString(rs.getString("moved_on")) + " by user " + rs.getString("approved_by"));
                    //tmgr = new TransactionManager("getStatusByApplicationNo3");
                    sql = " SELECT a.stage, a.purpose,a.appl_no,a.op_dt,COALESCE(ui.user_name,a.printed_by) AS printed_by FROM \n"
                            + " ( select '2' stage,'RC' purpose,appl_no,printed_on op_dt,printed_by from vh_rc_print a where appl_no=? and printed_on=(select max(printed_on) from vh_rc_print b where b.appl_no=a.appl_no)\n"
                            + " union all"
                            + " select '1' stage,'RC' purpose,appl_no,op_dt,'PRINTING OPERATOR' printed_by from va_rc_print a where appl_no=? "
                            + " union all"
                            + " select '3' stage,'HSRP' purpose,appl_no,hsrp_op_dt op_dt,user_cd printed_by from hsrp.vt_hsrp where appl_no=?"
                            + " union all"
                            + " select '2' stage,'HSRP' purpose,appl_no,moved_on op_dt,moved_by printed_by from hsrp.vha_hsrp where appl_no=?"
                            + " union all"
                            + " select '1' stage,'HSRP' purpose,appl_no,op_dt,'HSRP VENDOR' printed_by from hsrp.va_hsrp where appl_no=?"
                            + " union all"
                            + " select '2' stage,'SMART CARD' purpose,rcpt_no appl_no,rcissuedate op_dt,deal_cd printed_by from smartcard.rc_ia_to_be where rcpt_no=? AND status='0' "
                            + " union all "
                            + " select '1' stage,'SMART CARD' purpose,rcpt_no appl_no,op_dt,'SMARTCARD VENDOR' printed_by from smartcard.rc_be_to_bo  where rcpt_no=? AND status='1'"
                            + " union all"
                            + " select '1' stage,'SMART CARD' purpose, appl_no,op_dt,'SMARTCARD VENDOR' printed_by from smartcard.va_smart_card where appl_no=?"
                            + " union all select '1' stage,'DISPATCH RC' purpose,appl_no,entered_on,'DISPATCH OPERATOR' printed_by from dispatch.va_dispatch  where appl_no=? "
                            + " union all select '2' stage,'DISPATCH RC' purpose,appl_no || '/' || COALESCE(dispatch_ref_no, 'NA'),moved_on,moved_by printed_by from dispatch.vha_dispatch where appl_no=? "
                            + " union all select '2' stage,'FC' purpose,appl_no,printed_on op_dt,printed_by from vh_fc_print a where appl_no=? and printed_on=(select max(printed_on) from vh_fc_print b where b.appl_no=a.appl_no and b.state_cd=a.state_cd and b.off_cd=a.off_cd)\n"
                            + " union all select '1' stage,'FC' purpose,appl_no,op_dt,'PRINTING OPERATOR' printed_by from va_fc_print a where appl_no=? "
                            + " )a \n"
                            + " left outer join " + TableList.TM_USER_INFO + " ui on ui.user_cd = regexp_replace(COALESCE(trim(a.printed_by), '0'), '[^0-9]', '0', 'g')::numeric ";
                    PreparedStatement ps3 = tmgr.prepareStatement(sql);
                    ps3.setString(1, dobj.getAppl_no());
                    ps3.setString(2, dobj.getAppl_no());
                    ps3.setString(3, dobj.getAppl_no());
                    ps3.setString(4, dobj.getAppl_no());
                    ps3.setString(5, dobj.getAppl_no());
                    ps3.setString(6, dobj.getAppl_no());
                    ps3.setString(7, dobj.getAppl_no());
                    ps3.setString(8, dobj.getAppl_no());
                    ps3.setString(9, dobj.getAppl_no());
                    ps3.setString(10, dobj.getAppl_no());
                    ps3.setString(11, dobj.getAppl_no());
                    ps3.setString(12, dobj.getAppl_no());
                    RowSet rs3 = tmgr.fetchDetachedRowSet_No_release();
                    while (rs3.next()) {
                        switch (rs3.getString("purpose").toUpperCase()) {
                            case "RC":
                                if (chkRCPrint == false) {
                                    if (rs3.getString("stage").equalsIgnoreCase("2")) {
                                        dobj.setRcPrinted("RC Printed on " + Utility.convertdateFormatString(rs3.getString("op_dt")) + " by user " + rs3.getString("printed_by"));
                                        chkRCPrint = true;
                                        break;
                                    } else {
                                        dobj.setRcPrinted("RC Print Pending on " + Utility.convertdateFormatString(rs3.getString("op_dt")) + " by user " + rs3.getString("printed_by"));
                                        chkRCPrint = true;
                                        break;
                                    }
                                }
                                break;
                            case "HSRP":
                                if (chkHsrp == false) {
                                    if (rs3.getString("stage").equalsIgnoreCase("3")) {
                                        dobj.setHsrpDone("Completed on " + Utility.convertdateFormatString(rs3.getString("op_dt")) + " by user " + rs3.getString("printed_by"));
                                        chkHsrp = true;
                                        break;
                                    } else if (rs3.getString("stage").equalsIgnoreCase("2")) {
                                        dobj.setHsrpDone("Pending at Vendor Side on " + Utility.convertdateFormatString(rs3.getString("op_dt")) + " by user " + rs3.getString("printed_by"));
                                        chkHsrp = true;
                                        break;
                                    } else {
                                        dobj.setHsrpDone("Pending for file generation on " + Utility.convertdateFormatString(rs3.getString("op_dt")) + " by user " + rs3.getString("printed_by"));
                                        chkHsrp = true;
                                        break;
                                    }
                                }
                                break;
                            case "SMART CARD":
                                if (chkSmartCard == false) {
                                    if (rs3.getString("stage").equalsIgnoreCase("2")) {
                                        dobj.setSmartCardDone("SMART CARD Generated on " + Utility.convertdateFormatString(rs3.getString("op_dt")) + " by user " + rs3.getString("printed_by"));
                                        chkSmartCard = true;
                                        break;
                                    } else {
                                        dobj.setSmartCardDone("SMART CARD Pending on " + Utility.convertdateFormatString(rs3.getString("op_dt")) + " by user " + rs3.getString("printed_by"));
                                        chkSmartCard = true;
                                        break;
                                    }
                                }
                                break;
                            case "DISPATCH RC":
                                if (chkDispatchRC == false) {
                                    if (rs3.getString("stage").equalsIgnoreCase("1")) {
                                        dobj.setDispatchRCDone("RC DISPATCH Pending against Appl. no " + rs3.getString("appl_no"));
                                        chkDispatchRC = true;
                                        break;
                                    } else {
                                        dobj.setDispatchRCDone("RC DISPATCH Generated [ApplNo/Barcode: " + rs3.getString("appl_no") + "] dated " + Utility.convertdateFormatString(rs3.getString("op_dt")) + " by user " + rs3.getString("printed_by"));
                                        chkDispatchRC = true;
                                        break;
                                    }
                                }
                                break;
                            case "FC":
                                if (chkFCPrint == false) {
                                    if (rs3.getString("stage").equalsIgnoreCase("2")) {
                                        dobj.setFcPrinted("FC Printed on " + Utility.convertdateFormatString(rs3.getString("op_dt")) + " by user " + rs3.getString("printed_by"));
                                        chkFCPrint = true;
                                        break;
                                    } else {
                                        dobj.setFcPrinted("FC Print Pending on " + Utility.convertdateFormatString(rs3.getString("op_dt")) + " by user " + rs3.getString("printed_by"));
                                        chkFCPrint = true;
                                        break;
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                } else if (rs.getString("status").equalsIgnoreCase("D")) {
                    dobj.setApprovalStatus("D");
                    dobj.setStatusDesc("DISPOSED ON " + Utility.convertdateFormatString(rs.getString("moved_on")));
                } else if (rs.getString("status").equalsIgnoreCase("FC")) {
                    dobj.setApprovalStatus("FC");
                    dobj.setStatusDesc("FEE CANCELLED ON " + Utility.convertdateFormatString(rs.getString("moved_on")));
                } else {
                    if (rs.getString("status").equalsIgnoreCase("C")) {
                        sql = "SELECT action_abbrv from tm_action where action_cd = ? ";
                        PreparedStatement ps2 = tmgr.prepareStatement(sql);
                        ps2.setInt(1, actioncd + 1);
                        RowSet rs2 = tmgr.fetchDetachedRowSet_No_release();
                        while (rs2.next()) {
                            dobj.setApprovalStatus("P");
                            dobj.setStatusDesc("PENDING AT " + rs2.getString("action_abbrv"));
                        }
                    } else {
                        dobj.setApprovalStatus("P");
                        dobj.setStatusDesc("PENDING AT " + rs.getString("action_abbrv"));
                    }
                }
                list.add(dobj);
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(vahanExceptionMessage);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(vahanExceptionMessage);
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

    public static ArrayList<ApplRegStatus_dobj> getOldStatus(String applNo, String regnNo, String purCd) throws VahanException {
        ArrayList<ApplRegStatus_dobj> list = new ArrayList<>();
        TransactionManagerReadOnly tmgr = null;
        try {
            String sql = "select office,purpose_descr,action_descr,status,office_remark,Public_Remark,entered_by,entered_on,file_movement_slno,entry_ip from get_appl_status('"
                    + applNo
                    + "') where pur_cd = " + purCd
                    + " ORDER BY purpose_descr asc,file_movement_slno desc";
            tmgr = new TransactionManagerReadOnly("getStatusByApplicationNo");
            PreparedStatement ps = tmgr.prepareStatement(sql);
//            ps.setString(1, applNo);
//            ps.setString(2, regnNo);
//            ps.setString(3, applNo);
//            ps.setString(4, regnNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            ApplRegStatus_dobj dobj = null;
            while (rs.next()) {
                dobj = new ApplRegStatus_dobj();
                dobj.setOffice(rs.getString("office"));
                dobj.setPurCdDescr(rs.getString("purpose_descr"));
                dobj.setAction_descr(rs.getString("action_descr"));
                dobj.setStatus(rs.getString("status"));
                dobj.setOffice_remark(rs.getString("office_remark"));
                dobj.setPublicRemark(rs.getString("Public_Remark"));
                dobj.setEntered_by(rs.getString("entered_by"));
                dobj.setEntered_on(rs.getString("entered_on"));
                dobj.setEntry_ip(rs.getString("entry_ip"));
                String statusDesc = "";
                switch (rs.getString("status")) {
                    case "R":
                        statusDesc = "CANCELLED";
                        break;
                    case "C":
                        statusDesc = "COMPLETED";
                        break;
                    case "N":
                        statusDesc = "NOT STARTED";
                        dobj.setEntered_by("");
                        dobj.setEntered_on("");
                        dobj.setEntry_ip("");
                        break;
                    case "M":
                        statusDesc = "REVERTED";
                        break;
                    case "I":
                        statusDesc = "APPLICATION HOLD";
                        break;
                    case "K":
                        statusDesc = "DISPATCH PENDING";
                        break;
                    case "A":
                        statusDesc = "APPROVED";
                        break;
                    case "B":
                        statusDesc = "BACKWARD";
                        break;
                    case "F":
                        statusDesc = "FORWARDED";
                        break;
                    case "D":
                        statusDesc = "DISPOSED";
                        break;
                    case "Z":
                        statusDesc = "FEE CANCELLED";
                        break;
                }
                dobj.setStatusDesc(statusDesc);

                list.add(dobj);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(vahanExceptionMessage);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(vahanExceptionMessage);
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

    public static String getRegnNo(String chasiNo, String state_cd, int off_cd) throws VahanException {
        String regnNo = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            String sql = "Select regn_no from " + TableList.VT_OWNER + " where chasi_no = ? and state_cd =? and off_cd =?";
            tmgr = new TransactionManagerReadOnly("getRegnNo");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, chasiNo);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                regnNo = rs.getString("regn_no");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(vahanExceptionMessage);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(vahanExceptionMessage);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return regnNo;
    }

    public static String getApplNoByRegnNo(String regnNo, String state_cd, int off_cd, String userCatg) throws VahanException {
        String applNo = null;
        TransactionManagerReadOnly tmgr = null;
        String offCodeVar = "";
        try {
            if (!TableConstants.USER_CATG_STATE_ADMIN.equals(userCatg)) {
                offCodeVar = "and off_cd =?";
            }
            String sql = "Select appl_no from " + TableList.VA_DETAILS + " where state_cd =? and regn_no = ?  " + offCodeVar;
            tmgr = new TransactionManagerReadOnly("getApplNoByRegnNo");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setString(2, regnNo);
            if (!TableConstants.USER_CATG_STATE_ADMIN.equals(userCatg)) {
                ps.setInt(3, off_cd);
            }

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                applNo = rs.getString("appl_no");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(vahanExceptionMessage);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(vahanExceptionMessage);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return applNo;
    }

    public static boolean checkVahanBacklogRegnNo(String regnNo, String state_cd, int off_cd, String userCatg) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        String offCodeVar = "";
        try {
            if (!TableConstants.USER_CATG_STATE_ADMIN.equals(userCatg)) {
                offCodeVar = "and off_cd =?";
            }
            String sql = "select regn_no from " + TableList.VB_BACKLOG_STATUS + " where regn_no=? and state_cd=? and reason ilike '%SelfBacklog%'" + offCodeVar;
            tmgr = new TransactionManagerReadOnly("checkVahanBacklogRegnNo");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, state_cd);
            if (!TableConstants.USER_CATG_STATE_ADMIN.equals(userCatg)) {
                ps.setInt(3, off_cd);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return false;
    }
}
