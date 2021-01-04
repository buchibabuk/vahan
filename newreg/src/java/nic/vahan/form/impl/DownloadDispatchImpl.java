/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.TmConfigurationDispatchDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.reports.DownloadDispatchDobj;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.sendSMS;
import org.apache.log4j.Logger;

/**
 *
 * @author afzal
 */
public class DownloadDispatchImpl {

    private static final Logger LOGGER = Logger.getLogger(DownloadDispatchImpl.class);

    public static DownloadDispatchDobj getDispatchRCDetails(SessionVariables sessionVariables, String regn_no, boolean isShowingAllPenList, TmConfigurationDispatchDobj tmConfDispatchDobj, DownloadDispatchDobj vmSPNSeriesDobj, Date oldDate, boolean rc_by_hand) throws VahanException {
        ArrayList listdownload = new ArrayList();
        DownloadDispatchDobj ret_dobj = new DownloadDispatchDobj();
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String sqlQuery = "";
        String prefix = "";
        int current_start_no = 0;
        String search_byDate = "";
        int end_no = 0;
        int count_srno = 0;
        boolean isSmartCard = false;
        DecimalFormat df = new DecimalFormat("00000000");
        if (tmConfDispatchDobj.isIs_speed_post_series() && !isShowingAllPenList && vmSPNSeriesDobj != null) {
            prefix = vmSPNSeriesDobj.getPrefix();
            if (vmSPNSeriesDobj.getCurrentStartNo() != null && !vmSPNSeriesDobj.getCurrentStartNo().isEmpty()) {
                current_start_no = Integer.parseInt(vmSPNSeriesDobj.getCurrentStartNo());
            }
            if (vmSPNSeriesDobj.getEndNo() != null && !vmSPNSeriesDobj.getEndNo().isEmpty()) {
                end_no = Integer.parseInt(vmSPNSeriesDobj.getEndNo());
            }

        }
        if (oldDate != null) {
            search_byDate = new java.sql.Date(oldDate.getTime()).toString();
        }
        try {
            tmgr = new TransactionManager("getDispatchRCDetails-1");
            isSmartCard = ServerUtil.verifyForSmartCard(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), tmgr);
            DownloadDispatchDobj dobj = null;
            sqlQuery = "select to_char(current_timestamp,'dd_Mon_yyyy_hh24_mi_ss') as cdate,to_char(current_timestamp,'dd_Mon_yyyy') as dispatchdate,to_char(a.entered_on,'dd-Mon-yyyy') rcIssuedate,to_char(v.regn_dt,'dd-Mon-yyyy') regndate,a.appl_no,a.mobile_no, a.regn_no, a.owner_name,a.c_address,a.dispatch_ref_no,"
                    + "v.c_add1 , v.c_add2 , v.c_add3 ,b.descr as c_district_name ,c.descr as c_state_name , v.c_pincode,v.f_name,d.off_name,e.email_id,vvc.transport_catg,v.ld_wt {RC_IA_TO_BE} {HSRP}"
                    + " from " + TableList.VA_DISPATCH + " a ";
            if (isSmartCard) {
                sqlQuery = sqlQuery.replace("{RC_IA_TO_BE}", ", ia.pur_cd")
                        + " inner join " + TableList.RC_IA_TO_BE + " ia ON ia.rcpt_no = a.appl_no and ia.status = '0'";
            } else {
                sqlQuery = sqlQuery.replace("{RC_IA_TO_BE}", "");
            }
            if (tmConfDispatchDobj.isIs_verify_for_hsrp() && ServerUtil.verifyForHsrp(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), tmgr)) {
                sqlQuery = sqlQuery.replace("{HSRP}", ", hsrp.regn_no as hsrpRegnNo")
                        + " left outer join " + TableList.VT_HSRP + " hsrp ON hsrp.regn_no = a.regn_no";
            } else {
                sqlQuery = sqlQuery.replace("{HSRP}", "");
            }
            sqlQuery = sqlQuery
                    + " left outer join " + TableList.VT_OWNER + " v on v.regn_no = a.regn_no and v.state_cd = a.state_cd and v.off_cd = a.off_cd "
                    + " left outer join " + TableList.TM_DISTRICT + " b on b.dist_cd = v.c_district "
                    + " left outer join " + TableList.TM_STATE + " c on c.state_code = v.c_state "
                    + " left outer join " + TableList.TM_OFFICE + " d on d.state_cd = v.c_state and d.off_cd=v.off_cd"
                    + " left outer join " + TableList.VT_OWNER_IDENTIFICATION + "  e on e.regn_no = a.regn_no and e.state_cd = a.state_cd and e.off_cd=a.off_cd "
                    + " left outer join " + TableList.VM_VH_CLASS + " vvc ON vvc.vh_class = v.vh_class"
                    + " where a.state_cd = ? and a.off_cd  = ? ";

            if ((tmConfDispatchDobj.isIs_search_by_regn_no() || tmConfDispatchDobj.isIs_rcdispatch_byhand()) && regn_no != null && !regn_no.equals("")) {
                sqlQuery = sqlQuery + " and a.regn_no='" + regn_no + "'";
            } else if (tmConfDispatchDobj.isIs_search_by_regn_no() && (regn_no == null || regn_no.equals("")) && !isShowingAllPenList) {
                if (tmConfDispatchDobj.isIs_rcdispatch_userwise() && sessionVariables.getEmpCodeLoggedIn() != null && !sessionVariables.getEmpCodeLoggedIn().isEmpty()) {
                    sqlQuery = sqlQuery + " and a.dispatch_ref_no is not null and a.dispatchrefno_entered_by='" + sessionVariables.getEmpCodeLoggedIn() + "'";
                } else {
                    sqlQuery = sqlQuery + " and a.dispatch_ref_no is not null ";
                }
            } else if (isShowingAllPenList && isSmartCard) {
                sqlQuery = sqlQuery + " and ia.rcissuedate BETWEEN '" + search_byDate + "'::date AND ('" + search_byDate + "'::date + '1 day'::interval -  '1 second'::interval)";
            } else if (isShowingAllPenList && !isSmartCard) {
                sqlQuery = sqlQuery + " and a.entered_on BETWEEN '" + search_byDate + "'::date AND ('" + search_byDate + "'::date + '1 day'::interval -  '1 second'::interval)";
            }
            if (isSmartCard) {
                sqlQuery = sqlQuery + " order by ia.rcissuedate, ia.vehregno limit 2000";
            } else {
                sqlQuery = sqlQuery + " order by a.entered_on , a.regn_no limit 2000";
            }
            ps = tmgr.prepareStatement(sqlQuery);
            ps.setString(1, sessionVariables.getStateCodeSelected());
            ps.setInt(2, sessionVariables.getOffCodeSelected());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (tmConfDispatchDobj.isIs_verify_for_hsrp()) {
                    if ("NEW".contains(rs.getString("pur_cd")) && (rs.getString("hsrpRegnNo") == null || rs.getString("hsrpRegnNo").equals(""))) {
                        continue;
                    }
                }
                if ("RJ".contains(sessionVariables.getStateCodeSelected())) {
                    if (rc_by_hand && rs.getString("transport_catg") != null && !rs.getString("transport_catg").equals("")
                            && rs.getString("transport_catg").equals("G") && rs.getInt("ld_wt") <= 3000) {
                        throw new VahanException("RC of Goods Vehilce with laden weight equal or less then 3000 KG. will not be dispatch by hand!!");
                    }
                }
                dobj = new DownloadDispatchDobj();
                dobj.setSrno(++count_srno);
                dobj.setAppl_no(rs.getString("appl_no"));
                if (rs.getString("regn_no") == null) {
                    dobj.setRegnNo(" ");
                } else {
                    dobj.setRegnNo(rs.getString("regn_no"));
                }
                if (rs.getString("owner_name") == null) {
                    dobj.setOwnerName(" ");
                } else {
                    dobj.setOwnerName(rs.getString("owner_name"));
                }
                if (rs.getString("c_address") == null) {
                    dobj.setCurrentAddress(" ");
                } else {
                    dobj.setCurrentAddress(rs.getString("c_address"));
                }
                if (rs.getString("regndate") == null) {
                    dobj.setRegnDt(" ");
                } else {
                    dobj.setRegnDt(rs.getString("regndate"));
                }
                if (rs.getString("cdate") == null) {
                    dobj.setReportGenDate(" ");
                } else {
                    dobj.setReportGenDate(rs.getString("cdate"));
                }
                if (rs.getString("rcIssuedate") == null) {
                    dobj.setRcIssueDt(" ");
                } else {
                    dobj.setRcIssueDt(String.valueOf(rs.getString("rcIssuedate")));
                }
                if (rs.getString("c_add1") == null) {
                    dobj.setAdd1(" ");
                } else {
                    dobj.setAdd1(rs.getString("c_add1"));
                }
                if (rs.getString("c_add2") == null) {
                    dobj.setAdd2(" ");
                } else {
                    dobj.setAdd2(rs.getString("c_add2"));
                }
                if (rs.getString("c_add3") == null) {
                    dobj.setAdd3(" ");
                } else {
                    dobj.setAdd3(rs.getString("c_add3"));
                }
                if (rs.getString("c_district_name") == null) {
                    dobj.setCity(" ");
                } else {
                    dobj.setCity(rs.getString("c_district_name"));
                }
                if (rs.getInt("c_pincode") == 0) {
                    dobj.setPincode(" ");
                } else {
                    dobj.setPincode(String.valueOf(rs.getInt("c_pincode")));
                }
                if (rs.getString("f_name") == null) {
                    dobj.setFname(" ");
                } else {
                    dobj.setFname(rs.getString("f_name"));
                }
                if (rs.getString("mobile_no") == null) {
                    dobj.setMobile_no(" ");
                } else {
                    dobj.setMobile_no(rs.getString("mobile_no"));
                }
                dobj.setDispatchdate(rs.getString("dispatchdate"));
                dobj.setOffName(rs.getString("off_name"));
                ret_dobj.setCur_date(rs.getString("cdate"));
                ret_dobj.setDownloadFileName(sessionVariables.getStateCodeSelected() + sessionVariables.getOffCodeSelected() + "_DISPATCH_" + rs.getString("cdate"));
                dobj.setEmail_id(rs.getString("email_id"));
                if (tmConfDispatchDobj.isIs_speed_post_series() && current_start_no > 0 && end_no > 0 && current_start_no <= end_no && !isShowingAllPenList) {

                    int chkDigit = getBarCodeCheckDigitNo(df.format(current_start_no));
                    String speedPostNumber = prefix + df.format(current_start_no) + String.valueOf(chkDigit) + "IN";
                    dobj.setDispatch_ref_no(speedPostNumber);
                    dobj.setDispatch_ref_no_for_display(speedPostNumber);
                } else {
                    dobj.setDispatch_ref_no(rs.getString("dispatch_ref_no"));
                    dobj.setDispatch_ref_no_for_display(rs.getString("dispatch_ref_no"));
                }
                dobj.setCurrentStartNo(df.format(current_start_no));
                listdownload.add(dobj);
                if (tmConfDispatchDobj.isIs_speed_post_series() && current_start_no == end_no && !isShowingAllPenList) {
                    break;
                }
                current_start_no++;
            }
            ret_dobj.setListFileExport(listdownload);
            if (regn_no != null && !regn_no.equals("")) {
                if (listdownload.isEmpty()) {
                    tmgr = new TransactionManager("getDispatchRCDetails-2");
                    sqlQuery = "select appl_no"
                            + " from " + TableList.VA_DISPATCH + " where regn_no= '" + regn_no + "' and state_cd = '" + sessionVariables.getStateCodeSelected() + "' and off_cd =  " + sessionVariables.getOffCodeSelected() + "  "
                            + " order by entered_on DESC limit 1";
                    ps = tmgr.prepareStatement(sqlQuery);
                    rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        if (rc_by_hand && tmConfDispatchDobj.getBy_hand_class_type() != 3) {
                            ret_dobj.setPending_dispatch_rc_details("RC is Pending with Appl no " + rs.getString("appl_no") + " Either (KMS / HSRP) is pending or RC will not be dispatch by hand for this Registration no!!");
                        } else if (tmConfDispatchDobj.isIs_search_by_regn_no() && tmConfDispatchDobj.getBy_hand_class_type() != 3) {
                            ret_dobj.setPending_dispatch_rc_details("RC is Pending with Appl no " + rs.getString("appl_no") + " Either (KMS / HSRP) is pending or RC will be dispatch by hand for this Registration no!!");
                        } else {
                            ret_dobj.setPending_dispatch_rc_details("RC is Pending with Appl no " + rs.getString("appl_no") + " Either KMS or HSRP is pending for this Registration no");
                        }
                    }
                    tmgr = new TransactionManager("getDispatchRCDetails-3");
                    sqlQuery = "select a.appl_no, COALESCE(a.dispatch_ref_no, 'NA') as dispatch_ref_no,to_char(a.moved_on,'dd-MON-yyyy') as dispatch_on"
                            + " from " + TableList.VHA_DISPATCH + " a"
                            + " where a.regn_no= '" + regn_no + "' and a.state_cd = '" + sessionVariables.getStateCodeSelected() + "' and a.off_cd =  " + sessionVariables.getOffCodeSelected() + "  "
                            + " order by a.moved_on DESC limit 1";
                    ps = tmgr.prepareStatement(sqlQuery);
                    rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        if (rs.getString("dispatch_ref_no") != null && !"".contains(rs.getString("dispatch_ref_no"))) {
                            ret_dobj.setDispatch_rc_details("RC Dispatched on " + rs.getString("dispatch_on") + " with Appl no " + rs.getString("appl_no") + " and barcode " + rs.getString("dispatch_ref_no"));
                        }
                    }
                }
            }
        } catch (VahanException vex) {
            throw new VahanException(vex.getMessage());
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return ret_dobj;
    }

    public static int getBarCodeCheckDigitNo(String num) {
        int d1 = Integer.valueOf(num.substring(0, 1)) * 8;
        int d2 = Integer.valueOf(num.substring(1, 2)) * 6;
        int d3 = Integer.valueOf(num.substring(2, 3)) * 4;
        int d4 = Integer.valueOf(num.substring(3, 4)) * 2;
        int d5 = Integer.valueOf(num.substring(4, 5)) * 3;
        int d6 = Integer.valueOf(num.substring(5, 6)) * 5;
        int d7 = Integer.valueOf(num.substring(6, 7)) * 9;
        int d8 = Integer.valueOf(num.substring(7, 8)) * 7;
        int sum = d1 + d2 + d3 + d4 + d5 + d6 + d7 + d8;
        int rem = sum % 11;
        if (rem == 0) {
            return 5;
        } else if (rem == 1) {
            return 0;
        } else {
            return (11 - rem);
        }
    }

    public static boolean updateVhRcPrintAfterIsDownloadedAction(SessionVariables sessionVariables, DownloadDispatchDobj dwnDobj, TmConfigurationDispatchDobj tmConfDispatchDobj) throws VahanException {
        boolean issuccess = false;
        List<DownloadDispatchDobj> dwnDobjWithBarcode = new ArrayList<DownloadDispatchDobj>();
        List<DownloadDispatchDobj> dwnDobjWithoutBarcode = new ArrayList<DownloadDispatchDobj>();
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("updateVhRcPrintAfterIsDownloadedAction");
            if (!tmConfDispatchDobj.isIs_barcode_mandatory()) {
                for (DownloadDispatchDobj dobjDtls : dwnDobj.getListFileExport()) {
                    if (dobjDtls.getDispatch_ref_no() != null && !dobjDtls.getDispatch_ref_no().equals("")) {
                        dwnDobjWithBarcode.add(dobjDtls);
                    } else {
                        dwnDobjWithoutBarcode.add(dobjDtls);
                    }
                }
                if (!dwnDobjWithBarcode.isEmpty()) {
                    dwnDobj.setListFileExport(dwnDobjWithBarcode);
                    issuccess = saveAndMoveInHistoryWithBarcode(sessionVariables, dwnDobj, tmConfDispatchDobj, tmgr);

                }
                if (!dwnDobjWithoutBarcode.isEmpty()) {
                    dwnDobj.setListFileExport(dwnDobjWithoutBarcode);
                    issuccess = saveAndMoveInHistoryWithoutBarcode(sessionVariables, dwnDobj, issuccess, tmgr);
                }
            } else {
                issuccess = saveAndMoveInHistoryWithoutBarcode(sessionVariables, dwnDobj, issuccess, tmgr);
            }
            tmgr.commit();
        } catch (VahanException ve) {
            throw new VahanException(ve.getMessage());
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return issuccess;
    }

    public static boolean saveAndMoveInHistory(SessionVariables sessionVariables, DownloadDispatchDobj dwnDobj, TmConfigurationDispatchDobj tmConfDispatchDobj) throws VahanException {
        Exception e = null;
        boolean issuccess = false;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("saveAndMoveInHistory");
            issuccess = saveAndMoveInHistoryWithBarcode(sessionVariables, dwnDobj, tmConfDispatchDobj, tmgr);
            tmgr.commit();
        } catch (VahanException ve) {
            throw ve;

        } catch (Exception ex) {
            e = ex;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        if (e != null) {
            throw new VahanException("Error in Getting details for Generation the dispatch file");
        }
        return issuccess;
    }

    public static boolean saveAndMoveInHistoryDispatchRCByHand(SessionVariables sessionVariables, DownloadDispatchDobj dwnDobj, TmConfigurationDispatchDobj tmConfDispatchDobj) throws VahanException {
        Exception e = null;
        boolean issuccess = false;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("saveAndMoveInHistory");
            issuccess = dispatchRCByHand(sessionVariables, dwnDobj, tmConfDispatchDobj, tmgr);
            tmgr.commit();
        } catch (Exception ee) {
            e = ee;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        if (e != null) {
            throw new VahanException("Error in Getting details for Generation the dispatch file");
        }
        return issuccess;
    }

    public static boolean saveAndMoveInHistoryWithoutBarcode(SessionVariables sessionVariables, DownloadDispatchDobj dwnDobj, boolean issuccess, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        Exception e = null;
        try {
            String sqlQuery = "";
            String appl_no = "";
            for (int i = 0; i < dwnDobj.getListFileExport().size(); i++) {
                if (i == 0 && i < dwnDobj.getListFileExport().size() - 1) {
                    appl_no += "'" + dwnDobj.getListFileExport().get(i).getAppl_no() + "',";
                } else if (i > 0 && i < dwnDobj.getListFileExport().size() - 1) {
                    appl_no += "'" + dwnDobj.getListFileExport().get(i).getAppl_no() + "',";
                } else {
                    appl_no += "'" + dwnDobj.getListFileExport().get(i).getAppl_no() + "'";
                }
            }
            if (!issuccess) {
                sqlQuery = "insert into " + TableList.VT_DISPATCH_FLATFILE + " (state_cd,off_cd,flat_file,op_dt,entered_by) values (?,?,?,current_timestamp,?)";
                ps = tmgr.prepareStatement(sqlQuery);
                ps.setString(1, sessionVariables.getStateCodeSelected());
                ps.setInt(2, sessionVariables.getOffCodeSelected());
                ps.setString(3, dwnDobj.getDownloadFileName());
                ps.setString(4, sessionVariables.getEmpCodeLoggedIn());
                ps.executeUpdate();
            }
            sqlQuery = " insert into " + TableList.VHA_DISPATCH
                    + " select state_cd,off_cd,appl_no,regn_no,doc_type,owner_name,c_address,mobile_no,entered_on,entered_by,dispatch_ref_no,current_date,?,current_timestamp,? "
                    + " from " + TableList.VA_DISPATCH
                    + " where state_cd=? and off_cd=? and appl_no in (" + appl_no + ")";
            ps = tmgr.prepareStatement(sqlQuery);
            ps.setString(1, dwnDobj.getDownloadFileName());
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, sessionVariables.getStateCodeSelected());
            ps.setInt(4, sessionVariables.getOffCodeSelected());
            ps.executeUpdate();
            sqlQuery = "delete from " + TableList.VA_DISPATCH + " where appl_no in (" + appl_no + ") and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sqlQuery);
            ps.setString(1, sessionVariables.getStateCodeSelected());
            ps.setInt(2, sessionVariables.getOffCodeSelected());
            ps.executeUpdate();
            issuccess = true;
        } catch (SQLException ee) {
            e = ee;
        } catch (Exception ee) {
            e = ee;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        if (e != null) {
            throw new VahanException("Error in Generation the dispatch file");
        }
        return issuccess;
    }

    public static boolean saveAndMoveInHistoryWithBarcode(SessionVariables sessionVariables, DownloadDispatchDobj dwnDobj, TmConfigurationDispatchDobj tmConfDispatchDobj, TransactionManager tmgr) throws VahanException {
        boolean issuccess = false;
        PreparedStatement ps = null;
        String msgMobileCollection = "";
        DownloadDispatchDobj dobj = null;
        int current_start_no = 0;
        String sqlQuery = "";
        try {

            sqlQuery = "insert into " + TableList.VT_DISPATCH_FLATFILE + " (state_cd,off_cd,flat_file,op_dt,entered_by) values (?,?,?,current_timestamp,?)";
            ps = tmgr.prepareStatement(sqlQuery);
            ps.setString(1, sessionVariables.getStateCodeSelected());
            ps.setInt(2, sessionVariables.getOffCodeSelected());
            ps.setString(3, dwnDobj.getDownloadFileName());
            ps.setString(4, sessionVariables.getEmpCodeLoggedIn());
            ps.executeUpdate();

            if ((tmConfDispatchDobj.isIs_speed_post_series() && tmConfDispatchDobj.isIs_sendSMS_owner())
                    || (tmConfDispatchDobj.isIs_speed_post_series() && !tmConfDispatchDobj.isIs_sendSMS_owner())) { //DL OR PB
                for (DownloadDispatchDobj dobjDtls : dwnDobj.getListFileExport()) {
                    sqlQuery = " insert into " + TableList.VHA_DISPATCH
                            + " select state_cd,off_cd,appl_no,regn_no,doc_type,owner_name,c_address,mobile_no,entered_on,entered_by,?,current_date,?,current_timestamp,? "
                            + " from " + TableList.VA_DISPATCH + " where appl_no=? AND state_cd=? AND off_cd=?";
                    ps = tmgr.prepareStatement(sqlQuery);
                    ps.setString(1, dobjDtls.getDispatch_ref_no().toUpperCase());
                    ps.setString(2, dwnDobj.getDownloadFileName());
                    ps.setString(3, Util.getEmpCode());
                    ps.setString(4, dobjDtls.getAppl_no());
                    ps.setString(5, sessionVariables.getStateCodeSelected());
                    ps.setInt(6, sessionVariables.getOffCodeSelected());
                    ps.executeUpdate();
                    if (tmConfDispatchDobj.isIs_sendSMS_owner()) {
                        msgMobileCollection = "[RC No. " + dobjDtls.getRegnNo() + "]%0D%0A"
                                + "Dispatched on ." + dobjDtls.getDispatchdate() + " and can be tracked using Speed Post Tracking no. " + dobjDtls.getDispatch_ref_no().toUpperCase() + ".%0D%0A"
                                + "Thanks " + dobjDtls.getOffName();
                        sendSMS(dobjDtls.getMobile_no(), msgMobileCollection);
                    }
                    sqlQuery = "delete from " + TableList.VA_DISPATCH + " where appl_no=? AND state_cd=? AND off_cd=?";
                    ps = tmgr.prepareStatement(sqlQuery);
                    ps.setString(1, dobjDtls.getAppl_no());
                    ps.setString(2, sessionVariables.getStateCodeSelected());
                    ps.setInt(3, sessionVariables.getOffCodeSelected());
                    ps.executeUpdate();
                }
            } else if (!tmConfDispatchDobj.isIs_speed_post_series() && tmConfDispatchDobj.isIs_sendSMS_owner()) { //GJ
                for (DownloadDispatchDobj dobjDtls : dwnDobj.getListFileExport()) {
                    sqlQuery = " insert into " + TableList.VHA_DISPATCH
                            + " select state_cd,off_cd,appl_no,regn_no,doc_type,owner_name,c_address,mobile_no,entered_on,entered_by,dispatch_ref_no,current_date,?,current_timestamp,? "
                            + " from " + TableList.VA_DISPATCH + " where appl_no=? AND state_cd=? AND off_cd=?";
                    ps = tmgr.prepareStatement(sqlQuery);
                    ps.setString(1, dwnDobj.getDownloadFileName());
                    ps.setString(2, Util.getEmpCode());
                    ps.setString(3, dobjDtls.getAppl_no());
                    ps.setString(4, sessionVariables.getStateCodeSelected());
                    ps.setInt(5, sessionVariables.getOffCodeSelected());
                    ps.executeUpdate();

                    msgMobileCollection = "[RC No. " + dobjDtls.getRegnNo() + "]%0D%0A"
                            + "Dispatched on ." + dobjDtls.getDispatchdate() + " and can be tracked using Speed Post Tracking no. " + dobjDtls.getDispatch_ref_no().toUpperCase() + ".%0D%0A"
                            + "Thanks " + dobjDtls.getOffName();
                    sendSMS(dobjDtls.getMobile_no(), msgMobileCollection);

                    sqlQuery = "delete from " + TableList.VA_DISPATCH + " where appl_no=? AND state_cd=? AND off_cd=?";
                    ps = tmgr.prepareStatement(sqlQuery);
                    ps.setString(1, dobjDtls.getAppl_no());
                    ps.setString(2, sessionVariables.getStateCodeSelected());
                    ps.setInt(3, sessionVariables.getOffCodeSelected());
                    ps.executeUpdate();

                }
            } else if (!tmConfDispatchDobj.isIs_speed_post_series() && !tmConfDispatchDobj.isIs_sendSMS_owner()) { //MH
                sqlQuery = " insert into " + TableList.VHA_DISPATCH
                        + " select state_cd,off_cd,appl_no,regn_no,doc_type,owner_name,c_address,mobile_no,entered_on,entered_by,dispatch_ref_no,current_date,?,current_timestamp,? "
                        + " from " + TableList.VA_DISPATCH + " where entered_on< to_timestamp( ?,'dd_Mon_yyyy_hh24_mi_ss') and dispatch_ref_no is NOT null AND state_cd=? AND off_cd=?";
                ps = tmgr.prepareStatement(sqlQuery);
                ps.setString(1, dwnDobj.getDownloadFileName());
                ps.setString(2, Util.getEmpCode());
                ps.setString(3, dwnDobj.getCur_date());
                ps.setString(4, sessionVariables.getStateCodeSelected());
                ps.setInt(5, sessionVariables.getOffCodeSelected());
                ps.executeUpdate();

                sqlQuery = "delete from " + TableList.VA_DISPATCH + " where entered_on< to_timestamp( ?,'dd_Mon_yyyy_hh24_mi_ss') and dispatch_ref_no is NOT null and state_cd=? and off_cd=?";
                ps = tmgr.prepareStatement(sqlQuery);
                ps.setString(1, dwnDobj.getCur_date());
                ps.setString(2, sessionVariables.getStateCodeSelected());
                ps.setInt(3, sessionVariables.getOffCodeSelected());
                ps.executeUpdate();
            }
            issuccess = true;
            if (issuccess && tmConfDispatchDobj.isIs_speed_post_series()) {
                dobj = dwnDobj.getListFileExport().get(dwnDobj.getListFileExport().size() - 1);
                current_start_no = Integer.valueOf(dobj.getCurrentStartNo());
                sqlQuery = "update " + TableList.VM_SPEED_POST_SERIES + " set current_start_no = ? where state_cd=? and off_cd=?";
                ps = tmgr.prepareStatement(sqlQuery);
                ps.setInt(1, current_start_no + 1);
                ps.setString(2, sessionVariables.getStateCodeSelected());
                ps.setInt(3, sessionVariables.getOffCodeSelected());
                ps.executeUpdate();
            }
        } catch (SQLException sqlex) {
            throw new VahanException("Error in Generation the dispatch file");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return issuccess;
    }

    public static DownloadDispatchDobj getAllOldDispatchRCList(SessionVariables sessionVariables, Date oldDate, TmConfigurationDispatchDobj tmConfDispatchDobj) throws VahanException {
        ArrayList listdownload = new ArrayList();
        DateFormat writeFormat = new SimpleDateFormat("dd-MMM-yyyy");
        TransactionManager tmgr = null;
        DownloadDispatchDobj ret_dobj = null;
        Exception e = null;
        boolean isSmartCard = false;
        int count_srno = 0;
        TmConfigurationDobj configurationDobj = Util.getTmConfiguration();
        try {
            tmgr = new TransactionManager("getAllOldDispatchRCList");
            isSmartCard = ServerUtil.verifyForSmartCard(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), tmgr);
            String sql = "select a.regn_no,to_char(current_timestamp,'dd_Mon_yyyy_hh24_mi_ss') cdate,to_char(a.entered_on,'dd-Mon-yyyy') rcIssuedate,to_char(v.regn_dt,'dd-Mon-yyyy') regndate,a.mobile_no,a.owner_name,a.c_address, \n"
                    + "v.c_add1 , v.c_add2 , v.c_add3 , b.descr as c_district_name ,c.descr as c_state_name , v.c_pincode,v.f_name,a.dispatch_ref_no,f.off_name,f.pin_cd,e.email_id,d.rcpt_heading,d.rcpt_subheading,to_char(a.moved_on,'dd-Mon-yyyy') Dispdate,to_char(current_timestamp,'dd-Mon-yyyy hh24:mi:ss') as printed_on,a.state_cd ,(COALESCE(f.off_add1, '')|| ','||COALESCE(f.off_add2, '')|| ','||COALESCE(f.pin_cd::varchar, '')) as off_address,f.mobile_no as off_mobNo,f.landline,g.email_id as email_id_offAdmin {RC_IA_TO_BE}\n"
                    + " from " + TableList.VHA_DISPATCH + " a \n";
            if (isSmartCard) {
                sql = sql.replace("{RC_IA_TO_BE}", ", ia.pur_cd")
                        + " inner join " + TableList.RC_IA_TO_BE + " ia ON ia.rcpt_no = a.appl_no and ia.status = '0' \n";
            } else {
                sql = sql.replace("{RC_IA_TO_BE}", "");
            }
            sql = sql
                    + " left outer join " + TableList.VT_OWNER + " v on v.regn_no = a.regn_no and a.state_cd = v.state_cd and a.off_cd = v.off_cd \n"
                    + " left outer join " + TableList.TM_DISTRICT + " b on b.dist_cd = v.c_district \n"
                    + " left outer join " + TableList.TM_STATE + " c on c.state_code = v.c_state \n"
                    + " left outer join " + TableList.VT_OWNER_IDENTIFICATION + "  e on e.regn_no = a.regn_no and e.state_cd = a.state_cd and e.off_cd=a.off_cd \n"
                    + " left outer join " + TableList.TM_CONFIGURATION + " d on d.state_cd = a.state_cd  \n"
                    + " left outer join " + TableList.TM_OFFICE + " f on f.state_cd = a.state_cd and f.off_cd=a.off_cd \n"
                    + " left outer join " + TableList.TM_USER_INFO + " g ON g.user_catg='A' and g.state_cd = a.state_cd and g.off_cd=a.off_cd \n"
                    + " where a.state_cd=? and a.off_cd=? and a.moved_on BETWEEN ?::date AND (?::date + '1 day'::interval -  '1 second'::interval) {EMP_CODE}";

            if (sessionVariables != null && sessionVariables.getActionCodeSelected() == TableConstants.VM_ROLE_REVERT_GENERATED_RC_TO_OFF_STAFF) {
                if (tmConfDispatchDobj != null && tmConfDispatchDobj.isIs_rcdispatch_userwise() && sessionVariables.getEmpCodeLoggedIn() != null && !sessionVariables.getEmpCodeLoggedIn().isEmpty()) {
                    sql = sql.replace("{EMP_CODE}", " and a.moved_by='" + sessionVariables.getEmpCodeLoggedIn() + "'");
                } else {
                    sql = sql.replace("{EMP_CODE}", "");
                }
            } else if (sessionVariables.getActionCodeSelected() == TableConstants.VM_ROLE_REVERT_GENERATED_RC_TO_OFF_ADMIN) {

                sql = sql.replace("{EMP_CODE}", "");
            }

            if (isSmartCard) {
                sql = sql + " order by ia.rcissuedate, ia.vehregno ";
            } else {
                sql = sql + " order by a.entered_on, a.regn_no ";
            }
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, sessionVariables.getStateCodeSelected());
            ps.setInt(2, sessionVariables.getOffCodeSelected());
            ps.setDate(3, new java.sql.Date(oldDate.getTime()));
            ps.setDate(4, new java.sql.Date(oldDate.getTime()));
            RowSet rs = tmgr.fetchDetachedRowSet();
            ret_dobj = new DownloadDispatchDobj();
            while (rs.next()) {
                DownloadDispatchDobj dobj = null;
                dobj = new DownloadDispatchDobj();
                dobj.setSrno(++count_srno);
                if (rs.getString("regn_no") == null || rs.getString("regn_no").equals("")) {
                    dobj.setRegnNo(" ");
                } else {
                    dobj.setRegnNo(rs.getString("regn_no"));
                }
                if (rs.getString("regndate") == null || rs.getString("regndate").equals("")) {
                    dobj.setRegnDt(" ");
                } else {
                    dobj.setRegnDt(rs.getString("regndate"));
                }
                if (rs.getString("owner_name") == null || rs.getString("owner_name").equals("")) {
                    dobj.setOwnerName(" ");
                } else {
                    dobj.setOwnerName(rs.getString("owner_name"));
                }
                if (rs.getString("c_address") == null || rs.getString("c_address").equals("")) {
                    dobj.setCurrentAddress(" ");
                } else {
                    dobj.setCurrentAddress(rs.getString("c_address"));
                }
                if (rs.getString("regndate") == null || rs.getString("regndate").equals("")) {
                    dobj.setRegnDt(" ");
                } else {
                    dobj.setRegnDt(rs.getString("regndate"));
                }
                if (rs.getString("rcIssuedate") == null || rs.getString("rcIssuedate").equals("")) {
                    dobj.setRcIssueDt(" ");
                } else {
                    dobj.setRcIssueDt(String.valueOf(rs.getString("rcIssuedate")));
                }
                if (rs.getString("c_add1") == null || rs.getString("c_add1").equals("")) {
                    dobj.setAdd1(" ");
                } else {
                    dobj.setAdd1(rs.getString("c_add1"));
                }
                if (rs.getString("c_add2") == null || rs.getString("c_add2").equals("")) {
                    dobj.setAdd2(" ");
                } else {
                    dobj.setAdd2(rs.getString("c_add2"));
                }
                if (rs.getString("c_add3") == null || rs.getString("c_add3").equals("")) {
                    dobj.setAdd3(" ");
                } else {
                    dobj.setAdd3(rs.getString("c_add3"));
                }
                if (rs.getString("c_district_name") == null || rs.getString("c_district_name").equals("")) {
                    dobj.setCity(" ");
                } else {
                    dobj.setCity(rs.getString("c_district_name"));
                }
                if (rs.getInt("c_pincode") == 0) {
                    dobj.setPincode(" ");
                } else {
                    dobj.setPincode(String.valueOf(rs.getInt("c_pincode")));
                }
                if (rs.getString("cdate") == null || rs.getString("cdate").equals("")) {
                    dobj.setReportGenDate(" ");
                } else {
                    dobj.setReportGenDate(String.valueOf(rs.getString("cdate")));
                }
                if (rs.getString("f_name") == null || rs.getString("f_name").equals("")) {
                    dobj.setFname(" ");
                } else {
                    dobj.setFname(rs.getString("f_name"));
                }
                if (rs.getString("mobile_no") == null || rs.getString("mobile_no").equals("")) {
                    dobj.setMobile_no(" ");
                } else {
                    dobj.setMobile_no(rs.getString("mobile_no"));
                }

                if (rs.getString("dispatch_ref_no") == null || rs.getString("dispatch_ref_no").equals("")) {
                    dobj.setDispatch_ref_no(" ");
                    dobj.setIsShowBarcode(false);
                } else {
                    dobj.setDispatch_ref_no(rs.getString("dispatch_ref_no"));
                    dobj.setDispatch_ref_no_for_display(rs.getString("dispatch_ref_no"));
                    dobj.setIsShowBarcode(true);
                }
                if (rs.getString("Dispdate") == null || rs.getString("Dispdate").equals("")) {
                    dobj.setDispatch_date(" ");
                } else {
                    dobj.setDispatch_date(rs.getString("Dispdate"));
                }
                if (rs.getString("c_state_name") == null || rs.getString("c_state_name").equals("")) {
                    dobj.setStateName(" ");
                } else {
                    dobj.setStateName(rs.getString("c_state_name"));
                }
                dobj.setEmail_id(rs.getString("email_id"));
                dobj.setRcptHeading(rs.getString("rcpt_heading"));
                dobj.setRcptSubHeading(rs.getString("rcpt_subheading"));
                dobj.setOffName(rs.getString("off_name"));
                dobj.setOffPinCode(rs.getString("pin_cd"));
                dobj.setPrinted_on(rs.getString("printed_on"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOffAddress(rs.getString("off_address"));
                dobj.setOff_mobileno(rs.getString("off_mobNo"));
                dobj.setOff_landline(rs.getString("landline"));
                dobj.setEmail_id_offAdmin(rs.getString("email_id_offAdmin"));
                if (configurationDobj.getTmPrintConfgDobj() != null) {
                    if (configurationDobj.getTmPrintConfgDobj().getImage_background() != null && !configurationDobj.getTmPrintConfgDobj().getImage_background().isEmpty()) {
                        dobj.setImage_background(configurationDobj.getTmPrintConfgDobj().getImage_background());
                        dobj.setShow_image_background(true);
                    } else {
                        dobj.setShow_image_background(false);
                    }
                    if (configurationDobj.getTmPrintConfgDobj().getImage_logo() != null && !configurationDobj.getTmPrintConfgDobj().getImage_logo().isEmpty()) {
                        dobj.setImage_logo(configurationDobj.getTmPrintConfgDobj().getImage_logo());
                        dobj.setShow_image_logo(true);
                    } else {
                        dobj.setShow_image_logo(false);
                    }
                }
                listdownload.add(dobj);
            }
            ret_dobj.setListFileExport(listdownload);
            if (ret_dobj != null) {
                ret_dobj.setDownloadFileName(sessionVariables.getStateCodeSelected() + sessionVariables.getOffCodeSelected() + "_DISPATCHLIST_AS_ON_" + writeFormat.format(oldDate).toUpperCase());

            }
        } catch (SQLException ee) {
            e = ee;
        } catch (Exception ee) {
            e = ee;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        if (e != null) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return ret_dobj;
    }

    public static List<DownloadDispatchDobj> getOldDispatchRCList(SessionVariables sessionVariables, Date oldDate, TmConfigurationDispatchDobj tmConfDispatchDobj) throws VahanException {
        List<DownloadDispatchDobj> retList = new ArrayList<>();
        String sql = "Select *  from " + TableList.VT_DISPATCH_FLATFILE
                + "   where state_cd = ? and off_cd = ? and date(op_dt)=date(?) {EMP_CODE} order by op_dt  DESC";

        if (sessionVariables.getActionCodeSelected() == TableConstants.VM_ROLE_REVERT_GENERATED_RC_TO_OFF_STAFF) {
            if (tmConfDispatchDobj != null && tmConfDispatchDobj.isIs_rcdispatch_userwise() && sessionVariables.getEmpCodeLoggedIn() != null && !sessionVariables.getEmpCodeLoggedIn().isEmpty()) {
                sql = sql.replace("{EMP_CODE}", " and entered_by ='" + sessionVariables.getEmpCodeLoggedIn() + "'");
            } else {
                sql = sql.replace("{EMP_CODE}", "");
            }
        } else if (sessionVariables.getActionCodeSelected() == TableConstants.VM_ROLE_REVERT_GENERATED_RC_TO_OFF_ADMIN) {

            sql = sql.replace("{EMP_CODE}", "");
        }
        TransactionManagerReadOnly tmgr = null;
        DownloadDispatchDobj ret_dobj = null;
        Exception e = null;
        try {
            tmgr = new TransactionManagerReadOnly("getOldDispatchRCList");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, sessionVariables.getStateCodeSelected());
            ps.setInt(2, sessionVariables.getOffCodeSelected());
            ps.setDate(3, new java.sql.Date(oldDate.getTime()));
            RowSet rsSm = tmgr.fetchDetachedRowSet();
            while (rsSm.next()) {
                ret_dobj = new DownloadDispatchDobj();
                ret_dobj.setDownloadFileName(rsSm.getString("flat_file"));
                retList.add(ret_dobj);

            }
        } catch (SQLException ee) {
            e = ee;
        } catch (Exception ee) {
            e = ee;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        if (e != null) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return retList;
    }

    public static String updateVaDispatchRCDetail(SessionVariables sessionVariables, String appl_no, String barcode) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        int i = 0;
        String returnResponse = "false";
        try {
            tmgr = new TransactionManager("updateVaDispatchRCDetail");
            String sqlQuery = " update " + TableList.VA_DISPATCH + " set dispatch_ref_no =?,dispatchrefno_entered_by=? where state_cd=? and off_cd=? and appl_no=?";
            ps = tmgr.prepareStatement(sqlQuery);
            ps.setString(1, barcode.trim().toUpperCase());
            ps.setString(2, sessionVariables.getEmpCodeLoggedIn());
            ps.setString(3, sessionVariables.getStateCodeSelected());
            ps.setInt(4, sessionVariables.getOffCodeSelected());
            ps.setString(5, appl_no);
            i = ps.executeUpdate();
            if (1 > 0) {
                returnResponse = "true";
            } else {
                returnResponse = "false";
            }
            tmgr.commit();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return returnResponse;
    }

    public static List<DownloadDispatchDobj> getOldDispatchRCListFilewise(SessionVariables sessionVariables, String fileNmae) throws VahanException {
        List<DownloadDispatchDobj> retList = new ArrayList<>();
        ArrayList listSmartCard = null;
        TransactionManager tmgr = null;
        Exception e = null;
        PreparedStatement ps = null;
        DownloadDispatchDobj ret_dobj = null;
        boolean isSmartCard = false;
        int count_srno = 0;
        TmConfigurationDobj configurationDobj = Util.getTmConfiguration();
        try {
            tmgr = new TransactionManager("getOldDispatchRCList");
            isSmartCard = ServerUtil.verifyForSmartCard(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), tmgr);
            ret_dobj = new DownloadDispatchDobj();
            DownloadDispatchDobj dobj = null;
            listSmartCard = new ArrayList();
            String sql = "select a.regn_no,to_char(current_timestamp,'dd_Mon_yyyy_hh24_mi_ss') cdate,to_char(a.entered_on,'dd-Mon-yyyy') rcIssuedate,to_char(v.regn_dt,'dd-Mon-yyyy') regndate,a.mobile_no,a.owner_name,a.c_address, \n"
                    + "v.c_add1 , v.c_add2 , v.c_add3 , b.descr as c_district_name ,c.descr as c_state_name , v.c_pincode,v.f_name,a.dispatch_ref_no,f.off_name,f.pin_cd,e.email_id,d.rcpt_heading,d.rcpt_subheading,to_char(a.moved_on,'dd-Mon-yyyy') Dispdate,to_char(current_timestamp,'dd-Mon-yyyy hh24:mi:ss') as printed_on,a.state_cd ,(COALESCE(f.off_add1, '')|| ','||COALESCE(f.off_add2, '')|| ','||COALESCE(f.pin_cd::varchar, '')) as off_address,f.mobile_no as off_mobNo,f.landline,g.email_id as email_id_offAdmin {RC_IA_TO_BE} \n"
                    + " from " + TableList.VHA_DISPATCH + " a \n";
            if (isSmartCard) {
                sql = sql.replace("{RC_IA_TO_BE}", ", ia.pur_cd")
                        + " inner join " + TableList.RC_IA_TO_BE + " ia ON ia.rcpt_no = a.appl_no and ia.status = '0' \n";
            } else {
                sql = sql.replace("{RC_IA_TO_BE}", "");
            }
            sql = sql
                    + " left outer join " + TableList.VT_OWNER + " v on v.regn_no = a.regn_no and a.state_cd = v.state_cd and a.off_cd = v.off_cd \n"
                    + " left outer join " + TableList.TM_DISTRICT + " b on b.dist_cd = v.c_district \n"
                    + " left outer join " + TableList.TM_STATE + " c on c.state_code = v.c_state \n"
                    + " left outer join " + TableList.VT_OWNER_IDENTIFICATION + "  e on e.regn_no = a.regn_no and e.state_cd = a.state_cd and e.off_cd=a.off_cd \n"
                    + " left outer join " + TableList.TM_CONFIGURATION + " d on d.state_cd = a.state_cd \n"
                    + " left outer join " + TableList.TM_OFFICE + " f on f.state_cd = a.state_cd and f.off_cd=a.off_cd \n"
                    + " left outer join " + TableList.TM_USER_INFO + " g ON g.user_catg='A' and g.state_cd = a.state_cd and g.off_cd=a.off_cd \n"
                    + " where a.state_cd=? and a.off_cd=? and a.flat_file=? ";
            if (isSmartCard) {
                sql = sql + " order by ia.rcissuedate, ia.vehregno ";
            } else {
                sql = sql + " order by a.entered_on, a.regn_no ";
            }
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, sessionVariables.getStateCodeSelected());
            ps.setInt(2, sessionVariables.getOffCodeSelected());
            ps.setString(3, fileNmae);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new DownloadDispatchDobj();
                dobj.setSrno(++count_srno);
                if (rs.getString("regn_no") == null || rs.getString("regn_no").equals("")) {
                    dobj.setRegnNo(" ");
                } else {
                    dobj.setRegnNo(rs.getString("regn_no"));
                }
                if (rs.getString("regndate") == null || rs.getString("regndate").equals("")) {
                    dobj.setRegnDt(" ");
                } else {
                    dobj.setRegnDt(rs.getString("regndate"));
                }
                if (rs.getString("owner_name") == null || rs.getString("owner_name").equals("")) {
                    dobj.setOwnerName(" ");
                } else {
                    dobj.setOwnerName(rs.getString("owner_name"));
                }
                if (rs.getString("c_address") == null || rs.getString("c_address").equals("")) {
                    dobj.setCurrentAddress(" ");
                } else {
                    dobj.setCurrentAddress(rs.getString("c_address"));
                }
                if (rs.getString("regndate") == null || rs.getString("regndate").equals("")) {
                    dobj.setRegnDt(" ");
                } else {
                    dobj.setRegnDt(rs.getString("regndate"));
                }
                if (rs.getString("rcIssuedate") == null || rs.getString("rcIssuedate").equals("")) {
                    dobj.setRcIssueDt(" ");
                } else {
                    dobj.setRcIssueDt(String.valueOf(rs.getString("rcIssuedate")));
                }
                if (rs.getString("c_add1") == null || rs.getString("c_add1").equals("")) {
                    dobj.setAdd1(" ");
                } else {
                    dobj.setAdd1(rs.getString("c_add1"));
                }
                if (rs.getString("c_add2") == null || rs.getString("c_add2").equals("")) {
                    dobj.setAdd2(" ");
                } else {
                    dobj.setAdd2(rs.getString("c_add2"));
                }
                if (rs.getString("c_add3") == null || rs.getString("c_add3").equals("")) {
                    dobj.setAdd3(" ");
                } else {
                    dobj.setAdd3(rs.getString("c_add3"));
                }
                if (rs.getString("c_district_name") == null || rs.getString("c_district_name").equals("")) {
                    dobj.setCity(" ");
                } else {
                    dobj.setCity(rs.getString("c_district_name"));
                }
                if (rs.getInt("c_pincode") == 0) {
                    dobj.setPincode(" ");
                } else {
                    dobj.setPincode(String.valueOf(rs.getInt("c_pincode")));
                }
                if (rs.getString("cdate") == null || rs.getString("cdate").equals("")) {
                    dobj.setReportGenDate(" ");
                } else {
                    dobj.setReportGenDate(String.valueOf(rs.getString("cdate")));
                }
                if (rs.getString("f_name") == null || rs.getString("f_name").equals("")) {
                    dobj.setFname(" ");
                } else {
                    dobj.setFname(rs.getString("f_name"));
                }
                if (rs.getString("mobile_no") == null || rs.getString("mobile_no").equals("")) {
                    dobj.setMobile_no(" ");
                } else {
                    dobj.setMobile_no(rs.getString("mobile_no"));
                }
                if (rs.getString("dispatch_ref_no") == null || rs.getString("dispatch_ref_no").equals("")) {
                    dobj.setDispatch_ref_no(" ");
                    dobj.setIsShowBarcode(false);
                } else {
                    dobj.setDispatch_ref_no(rs.getString("dispatch_ref_no"));
                    dobj.setDispatch_ref_no_for_display(rs.getString("dispatch_ref_no"));
                    dobj.setIsShowBarcode(true);
                }
                if (rs.getString("Dispdate") == null || rs.getString("Dispdate").equals("")) {
                    dobj.setDispatch_date(" ");
                } else {
                    dobj.setDispatch_date(rs.getString("Dispdate"));
                }
                if (rs.getString("c_state_name") == null || rs.getString("c_state_name").equals("")) {
                    dobj.setStateName(" ");
                } else {
                    dobj.setStateName(rs.getString("c_state_name"));
                }
                dobj.setEmail_id(rs.getString("email_id"));
                dobj.setRcptHeading(rs.getString("rcpt_heading"));
                dobj.setRcptSubHeading(rs.getString("rcpt_subheading"));
                dobj.setOffName(rs.getString("off_name"));
                dobj.setOffPinCode(rs.getString("pin_cd"));
                dobj.setPrinted_on(rs.getString("printed_on"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOffAddress(rs.getString("off_address"));
                dobj.setOff_mobileno(rs.getString("off_mobNo"));
                dobj.setOff_landline(rs.getString("landline"));
                dobj.setEmail_id_offAdmin(rs.getString("email_id_offAdmin"));
                if (configurationDobj.getTmPrintConfgDobj() != null) {
                    if (configurationDobj.getTmPrintConfgDobj().getImage_background() != null && !configurationDobj.getTmPrintConfgDobj().getImage_background().isEmpty()) {
                        dobj.setImage_background(configurationDobj.getTmPrintConfgDobj().getImage_background());
                        dobj.setShow_image_background(true);
                    } else {
                        dobj.setShow_image_background(false);
                    }
                    if (configurationDobj.getTmPrintConfgDobj().getImage_logo() != null && !configurationDobj.getTmPrintConfgDobj().getImage_logo().isEmpty()) {
                        dobj.setImage_logo(configurationDobj.getTmPrintConfgDobj().getImage_logo());
                        dobj.setShow_image_logo(true);
                    } else {
                        dobj.setShow_image_logo(false);
                    }
                }
                listSmartCard.add(dobj);
            }
            ret_dobj.setListFileExport(listSmartCard);
            ret_dobj.setDownloadFileName(fileNmae);
            retList.add(ret_dobj);
        } catch (SQLException ee) {
            e = ee;
        } catch (Exception ee) {
            e = ee;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        if (e != null) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return retList;
    }

    static public boolean chkDuplicateBarcodeNo(String barcode_no) throws VahanException {
        boolean dupBarcode = false;
        String sqlDup = "Select * from " + TableList.VA_DISPATCH
                + " where dispatch_ref_no=? limit 1";

        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        try {
            tmgr = new TransactionManagerReadOnly("chkDuplicatedupBarcodeNo");
            ps = tmgr.prepareStatement(sqlDup);
            ps.setString(1, barcode_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dupBarcode = true;
            } else {
                String sql = "Select * from " + TableList.VHA_DISPATCH
                        + " where dispatch_ref_no = ? limit 1";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, barcode_no);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    dupBarcode = true;
                }

            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return dupBarcode;
    }

    public static DownloadDispatchDobj getSpeedPostSeriesDetails(SessionVariables sessionVariables) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        DownloadDispatchDobj dobj = null;
        Exception e = null;
        try {
            tmgr = new TransactionManagerReadOnly("getSpeedPostSeriesDetails");
            String sql = "Select *  from " + TableList.VM_SPEED_POST_SERIES
                    + "   where state_cd = ? and off_cd = ?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, sessionVariables.getStateCodeSelected());
            ps.setInt(2, sessionVariables.getOffCodeSelected());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new DownloadDispatchDobj();
                dobj.setUpdateSeries(true);
                dobj.setCurrentStartNo(rs.getString("current_start_no"));
                dobj.setEndNo(rs.getString("end_no"));
                dobj.setPrefix(rs.getString("pre_fix"));
            }
        } catch (SQLException ee) {
            e = ee;
        } catch (Exception ee) {
            e = ee;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        if (e != null) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return dobj;
    }

    public static boolean saveSpeedPostSeries(SessionVariables sessionVariables, DownloadDispatchDobj dwnDobj) throws VahanException {
        PreparedStatement ps = null;
        Exception e = null;
        TransactionManager tmgr = null;
        String sqlQuery = null;
        boolean status = false;
        try {
            tmgr = new TransactionManager("saveSpeedPostSeries");
            if (!dwnDobj.isUpdateSeries()) {
                sqlQuery = "insert into " + TableList.VM_SPEED_POST_SERIES + " (state_cd,off_cd,pre_fix,current_start_no,end_no,entered_on,entered_by) values (?,?,?,?,?,current_timestamp,?)";
                ps = tmgr.prepareStatement(sqlQuery);
                ps.setString(1, sessionVariables.getStateCodeSelected());
                ps.setInt(2, sessionVariables.getOffCodeSelected());
                ps.setString(3, dwnDobj.getPrefix());
                ps.setInt(4, Integer.parseInt(dwnDobj.getCurrentStartNo()));
                ps.setInt(5, Integer.parseInt(dwnDobj.getEndNo()));
                ps.setString(6, sessionVariables.getEmpCodeLoggedIn());
                ps.executeUpdate();
                tmgr.commit();
                status = true;
            } else {
                sqlQuery = "update " + TableList.VM_SPEED_POST_SERIES + " set current_start_no = ?,end_no = ?,pre_fix = ? where state_cd=? and off_cd=?";
                ps = tmgr.prepareStatement(sqlQuery);
                ps.setInt(1, Integer.parseInt(dwnDobj.getCurrentStartNo()));
                ps.setInt(2, Integer.parseInt(dwnDobj.getEndNo()));
                ps.setString(3, dwnDobj.getPrefix());
                ps.setString(4, sessionVariables.getStateCodeSelected());
                ps.setInt(5, sessionVariables.getOffCodeSelected());
                ps.executeUpdate();
                status = true;
                tmgr.commit();
            }

        } catch (SQLException ee) {
            e = ee;
        } catch (Exception ee) {
            e = ee;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        if (e != null) {
            throw new VahanException("Error in Insert / Updating the Speed Post Series");
        }
        return status;
    }

    public static boolean revertDetails(SessionVariables sessionVariables, String flatFile, TmConfigurationDispatchDobj tmConfDispatchDobj) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sqlQuery = null;
        boolean status = false;
        try {
            tmgr = new TransactionManager("revertDetails");
            sqlQuery = "SELECT * FROM " + TableList.VT_DISPATCH_FLATFILE + " where state_cd=? and off_cd=? and upper(flat_file) =? {RC_OP_DT}{EMP_CODE}";
            if (sessionVariables.getActionCodeSelected() == TableConstants.VM_ROLE_REVERT_GENERATED_RC_TO_OFF_STAFF) {
                sqlQuery = sqlQuery.replace("{RC_OP_DT}", " and date(op_dt) = current_date");
                if (tmConfDispatchDobj != null && tmConfDispatchDobj.isIs_rcdispatch_userwise() && sessionVariables.getEmpCodeLoggedIn() != null && !sessionVariables.getEmpCodeLoggedIn().isEmpty()) {
                    sqlQuery = sqlQuery.replace("{EMP_CODE}", " and entered_by ='" + sessionVariables.getEmpCodeLoggedIn() + "'");
                } else {
                    sqlQuery = sqlQuery.replace("{EMP_CODE}", "");
                }

            } else if (sessionVariables.getActionCodeSelected() == TableConstants.VM_ROLE_REVERT_GENERATED_RC_TO_OFF_ADMIN) {
                sqlQuery = sqlQuery.replace("{RC_OP_DT}", "");
                sqlQuery = sqlQuery.replace("{EMP_CODE}", "");
            }
            ps = tmgr.prepareStatement(sqlQuery);
            ps.setString(1, sessionVariables.getStateCodeSelected());
            ps.setInt(2, sessionVariables.getOffCodeSelected());
            ps.setString(3, flatFile);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (!rs.next()) {
                throw new VahanException("Flat File ' " + flatFile.toUpperCase() + " ' does not exist or you are not authorized to revert this Flat File !!!");
            } else {
                rs = null;
                if (tmConfDispatchDobj.isIs_rcdispatch_byhand() && tmConfDispatchDobj.isIs_barcode_mandatory()) {
                    sqlQuery = "SELECT appl_no from " + TableList.VHA_DISPATCH + " "
                            + " where state_cd=? and off_cd=? and upper(flat_file) =? and dispatch_ref_no is null limit 1";
                    ps = tmgr.prepareStatement(sqlQuery);
                    ps.setString(1, sessionVariables.getStateCodeSelected());
                    ps.setInt(2, sessionVariables.getOffCodeSelected());
                    ps.setString(3, flatFile);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        throw new VahanException("Flat File ' " + flatFile.toUpperCase() + " ' does not revert Because the RC dispatched by Hand !!!");
                    } else {
                        sqlQuery = "INSERT INTO " + TableList.VA_DISPATCH + "(SELECT state_cd,off_cd,appl_no,regn_no,doc_type,owner_name,c_address,mobile_no,entered_on,entered_by from " + TableList.VHA_DISPATCH + " "
                                + " where state_cd=? and off_cd=? and upper(flat_file) =? )";
                        ps = tmgr.prepareStatement(sqlQuery);
                        ps.setString(1, sessionVariables.getStateCodeSelected());
                        ps.setInt(2, sessionVariables.getOffCodeSelected());
                        ps.setString(3, flatFile);
                        ps.executeUpdate();

                        sqlQuery = "DELETE FROM " + TableList.VHA_DISPATCH + " where state_cd=? and off_cd=? and upper(flat_file) =?";
                        ps = tmgr.prepareStatement(sqlQuery);
                        ps.setString(1, sessionVariables.getStateCodeSelected());
                        ps.setInt(2, sessionVariables.getOffCodeSelected());
                        ps.setString(3, flatFile);
                        ps.executeUpdate();

                        sqlQuery = "DELETE FROM " + TableList.VT_DISPATCH_FLATFILE + " where state_cd=? and off_cd=? and upper(flat_file) =?";
                        ps = tmgr.prepareStatement(sqlQuery);
                        ps.setString(1, sessionVariables.getStateCodeSelected());
                        ps.setInt(2, sessionVariables.getOffCodeSelected());
                        ps.setString(3, flatFile);
                        ps.executeUpdate();
                        tmgr.commit();
                    }
                } else {
                    sqlQuery = "INSERT INTO " + TableList.VA_DISPATCH + "(SELECT state_cd,off_cd,appl_no,regn_no,doc_type,owner_name,c_address,mobile_no,entered_on,entered_by from " + TableList.VHA_DISPATCH + " "
                            + " where state_cd=? and off_cd=? and upper(flat_file) =? )";
                    ps = tmgr.prepareStatement(sqlQuery);
                    ps.setString(1, sessionVariables.getStateCodeSelected());
                    ps.setInt(2, sessionVariables.getOffCodeSelected());
                    ps.setString(3, flatFile);
                    ps.executeUpdate();

                    sqlQuery = "DELETE FROM " + TableList.VHA_DISPATCH + " where state_cd=? and off_cd=? and upper(flat_file) =?";
                    ps = tmgr.prepareStatement(sqlQuery);
                    ps.setString(1, sessionVariables.getStateCodeSelected());
                    ps.setInt(2, sessionVariables.getOffCodeSelected());
                    ps.setString(3, flatFile);
                    ps.executeUpdate();

                    sqlQuery = "DELETE FROM " + TableList.VT_DISPATCH_FLATFILE + " where state_cd=? and off_cd=? and upper(flat_file) =?";
                    ps = tmgr.prepareStatement(sqlQuery);
                    ps.setString(1, sessionVariables.getStateCodeSelected());
                    ps.setInt(2, sessionVariables.getOffCodeSelected());
                    ps.setString(3, flatFile);
                    ps.executeUpdate();
                    tmgr.commit();
                }
            }

            status = true;
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return status;
    }

    public static boolean dispatchRCByHand(SessionVariables sessionVariables, DownloadDispatchDobj dwnDobj, TmConfigurationDispatchDobj tmConfDispatchDobj, TransactionManager tmgr) throws VahanException {
        boolean issuccess = false;
        PreparedStatement ps = null;
        Exception e = null;
        String msgMobileCollection = "";
        DownloadDispatchDobj dobj = null;
        int current_start_no = 0;
        String sqlQuery = "";
        try {

            sqlQuery = "insert into " + TableList.VT_DISPATCH_FLATFILE + " (state_cd,off_cd,flat_file,op_dt,entered_by) values (?,?,?,current_timestamp,?)";
            ps = tmgr.prepareStatement(sqlQuery);
            ps.setString(1, sessionVariables.getStateCodeSelected());
            ps.setInt(2, sessionVariables.getOffCodeSelected());
            ps.setString(3, dwnDobj.getDownloadFileName());
            ps.setString(4, sessionVariables.getEmpCodeLoggedIn());
            ps.executeUpdate();
            DownloadDispatchDobj dobjDtls = dwnDobj.getListFileExport().get(0);
            sqlQuery = " insert into " + TableList.VHA_DISPATCH
                    + " select state_cd,off_cd,appl_no,regn_no,?,owner_name,c_address,mobile_no,entered_on,entered_by,null,?,?,?,? "
                    + " from " + TableList.VA_DISPATCH + " where appl_no=? AND state_cd=? AND off_cd=?";
            ps = tmgr.prepareStatement(sqlQuery);
            ps.setString(1, dobjDtls.getRemark());
            ps.setDate(2, new java.sql.Date(dobjDtls.getDt_hand_over().getTime()));
            ps.setString(3, dwnDobj.getDownloadFileName());
            ps.setTimestamp(4, new java.sql.Timestamp(dobjDtls.getDt_hand_over().getTime()));
            ps.setString(5, Util.getEmpCode());
            ps.setString(6, dobjDtls.getAppl_no());
            ps.setString(7, sessionVariables.getStateCodeSelected());
            ps.setInt(8, sessionVariables.getOffCodeSelected());
            ps.executeUpdate();
            if (tmConfDispatchDobj.isIs_sendSMS_owner()) {
                msgMobileCollection = "[RC No. " + dobjDtls.getRegnNo() + "]%0D%0A"
                        + "Dispatched on ." + dobjDtls.getDispatchdate() + " and can be tracked using Speed Post Tracking no. " + dobjDtls.getDispatch_ref_no().toUpperCase() + ".%0D%0A"
                        + "Thanks " + dobjDtls.getOffName();
                sendSMS(dobjDtls.getMobile_no(), msgMobileCollection);
            }
            sqlQuery = "delete from " + TableList.VA_DISPATCH + " where appl_no=? AND state_cd=? AND off_cd=?";
            ps = tmgr.prepareStatement(sqlQuery);
            ps.setString(1, dobjDtls.getAppl_no());
            ps.setString(2, sessionVariables.getStateCodeSelected());
            ps.setInt(3, sessionVariables.getOffCodeSelected());
            ps.executeUpdate();
            issuccess = true;
        } catch (SQLException ee) {
            e = ee;
        } catch (Exception ee) {
            e = ee;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        if (e != null) {
            throw new VahanException("Error in Generation the dispatch file");
        }
        return issuccess;
    }
}
