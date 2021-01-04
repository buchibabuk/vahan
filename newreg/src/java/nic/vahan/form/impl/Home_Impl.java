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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.ApplicationStatusDobj;
import nic.vahan.form.dobj.tradecert.TradeCertDetailsDobj;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;

/**
 *
 * @author nic5912
 */
public class Home_Impl {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Home_Impl.class);

    public static Map<String, Object> getAllotedOfficeCodeDescr(boolean dealerCheck) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        Map<String, Object> allotedOfficeLabelValue = new LinkedHashMap<String, Object>();

        try {
            tmgr = new TransactionManager("getAllotedOfficeCodeDescr()");
            if (dealerCheck) {
                sql = "SELECT distinct b.off_cd,b.off_name FROM " + TableList.VIEW_OFF_USER_SEAT_ACTION + " a "
                        + " right join " + TableList.TM_OFFICE + " b on a.state_cd = b.state_cd WHERE a.user_cd=? and b.off_type_cd NOT IN(5,9) order by 2";
            } else {
                sql = " SELECT distinct off_cd,off_name "
                        + " FROM " + TableList.VIEW_OFF_USER_SEAT_ACTION
                        + "  WHERE user_cd=? order by 2";
            }
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(Util.getEmpCode()));

            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next())//found
            {
                allotedOfficeLabelValue.put(rs.getString("off_name"), rs.getInt("off_cd")); //label,value
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return allotedOfficeLabelValue;
    }

    public boolean getDealerAuthority(Long empCode) {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        boolean dealerAuth = false;
        try {
            tmgr = new TransactionManagerReadOnly("getDealerAuthority()");
            sql = "Select all_office_auth from " + TableList.TM_USER_PERMISSIONS + " Where user_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, empCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            {
                if (rs.next()) {
                    dealerAuth = rs.getBoolean("all_office_auth");
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dealerAuth;
    }

    public static int getRoleCodeOfSeatWork(int action_cd) {
        int retrnRole = 0;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        try {
            tmgr = new TransactionManager("getAllotedActionCodeDescr()");
            sql = "SELECT role_cd FROM " + TableList.TM_ACTION + " where action_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, action_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next())//found
            {
                retrnRole = rs.getInt("role_cd");
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }

        return retrnRole;
    }

    public static List getCashCounterDetails() throws VahanException {
        List CashCounterDetails = new ArrayList();
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            tmgr = new TransactionManagerReadOnly("getAllotedActionCodeDescr()");
            sql = "SELECT day_begin FROM " + TableList.VM_SMART_CARD_HSRP + " where state_cd = ? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserSeatOffCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next())//found
            {
                if (rs.getDate("day_begin") != null && !rs.getDate("day_begin").equals("")) {
                    CashCounterDetails.add(format.format(rs.getDate("day_begin")));
                } else {
                    CashCounterDetails.add("null");
                }
                sql = "select close_cash,open_cash_dt from " + TableList.TM_USER_OPEN_CASH + " where state_cd =? and off_cd=? and user_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getUserSeatOffCode());
                ps.setLong(3, Long.parseLong(Util.getEmpCode()));
                rs = tmgr.fetchDetachedRowSet();
                CashCounterDetails.add(true);
                if (rs.next()) {
                    Date CurrDate = new Date();
                    String currentDt = format.format(CurrDate);
                    if (format.format(rs.getDate("open_cash_dt")).equals(currentDt)) {
                        CashCounterDetails.add(1, rs.getBoolean("close_cash"));
                    } else {
                        CashCounterDetails.add(1, true);
                    }

                }
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }

        return CashCounterDetails;
    }

    public static String getDBCurrentDate() {
        String Current_timestamp = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            tmgr = new TransactionManager("getAllotedActionCodeDescr()");
            sql = "SELECT current_date as currDate";
            ps = tmgr.prepareStatement(sql);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next())//found
            {
                Current_timestamp = format.format(rs.getDate("currDate"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return Current_timestamp;
    }

    public static Map<String, Object> getAllotedActionCodeDescr(int selectedOffCd, String stateCd, String empCd) {

        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        Map<String, Object> allotedActionLabelValue = new LinkedHashMap<String, Object>();
        try {
            tmgr = new TransactionManagerReadOnly("getAllotedActionCodeDescr()");
            sql = "SELECT action_cd, action_descr,action_abbrv, role_cd, redirect_menu, redirect_url \n"
                    + " FROM vvv_off_user_seat_action \n"
                    + " where user_cd = ? and state_cd = ? and off_cd = ? and action_under_project = ? and length(redirect_menu) > 0 \n"
                    + " order by 3 ";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(empCd));
            ps.setString(2, stateCd);
            ps.setInt(3, selectedOffCd);
            ps.setString(4, TableConstants.PROJECT_NAME);

            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next())//found
            {
                allotedActionLabelValue.put(rs.getString("action_abbrv"), rs.getInt("action_cd")); //label,value
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }

        return allotedActionLabelValue;
    }

    public static ArrayList<SeatAllotedDetails> seatWorkList(String getSelected_off_cd, String appl_no, String regn_no, String category, Date fromDate, Date toDate) {

        ArrayList<SeatAllotedDetails> list = new ArrayList<>();
        String sql = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmg = null;
        try {
            tmg = new TransactionManagerReadOnly("seatWorkListForHomeBean");
            if (category.equalsIgnoreCase("applNo") || category.equalsIgnoreCase("oldApplNo")) {
                sql = " select appl_no, appl_dt,regn_no,pur_cd,purpose,action_cd,action_descr,office_remark,public_remark,file_movement_slno,status,redirect_url,seat_cd\n"
                        + " from get_user_pending_work_appl_no(?,?,?)";
                ps = tmg.prepareStatement(sql);
                ps.setString(1, appl_no);
                ps.setLong(2, Long.parseLong(Util.getEmpCode()));
                ps.setInt(3, Integer.parseInt(getSelected_off_cd));
            } else if (category.equalsIgnoreCase("regnNo")) {
                sql = " select appl_no, appl_dt,regn_no,pur_cd,purpose,action_cd,action_descr,office_remark,public_remark,file_movement_slno,status,redirect_url,seat_cd\n"
                        + " from get_user_pending_work_regn_no(?,?,?)";
                ps = tmg.prepareStatement(sql);
                ps.setString(1, regn_no);
                ps.setLong(2, Long.parseLong(Util.getEmpCode()));
                ps.setInt(3, Integer.parseInt(getSelected_off_cd));
            } else if (category.equalsIgnoreCase("faceApplNo")) {
                sql = " select appl_no, appl_dt,regn_no,pur_cd,purpose,action_cd,action_descr,office_remark,public_remark,file_movement_slno,status,redirect_url,seat_cd\n"
                        + " from get_user_pending_work_faceless(?,?,?)";
                ps = tmg.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Integer.parseInt(getSelected_off_cd));
                ps.setLong(3, Long.parseLong(Util.getEmpCode()));
            } else if (category.equalsIgnoreCase("all")) {
                if (fromDate != null && toDate != null) {
                    sql = " select appl_no, appl_dt,regn_no,pur_cd,purpose,action_cd,action_descr,office_remark,public_remark,file_movement_slno,status,redirect_url,seat_cd\n"
                            + " from get_user_pending_work_bydates(?,?,?,?,?)";
                    ps = tmg.prepareStatement(sql);
                    ps.setString(1, Util.getUserStateCode());
                    ps.setInt(2, Integer.parseInt(getSelected_off_cd));
                    ps.setLong(3, Long.parseLong(Util.getEmpCode()));
                    ps.setDate(4, new java.sql.Date(fromDate.getTime()));
                    ps.setDate(5, new java.sql.Date(toDate.getTime()));
                } else {
                    sql = " select appl_no, appl_dt,regn_no,pur_cd,purpose,action_cd,action_descr,office_remark,public_remark,file_movement_slno,status,redirect_url,seat_cd\n"
                            + " from get_user_pending_work(?,?,?)";
                    ps = tmg.prepareStatement(sql);
                    ps.setString(1, Util.getUserStateCode());
                    ps.setInt(2, Integer.parseInt(getSelected_off_cd));
                    ps.setLong(3, Long.parseLong(Util.getEmpCode()));
                }
            }

            RowSet rs = tmg.fetchDetachedRowSet();
            while (rs.next()) {
                SeatAllotedDetails wd = new SeatAllotedDetails();

                wd.setAppl_no(rs.getString("appl_no"));
                wd.setAppl_dt(rs.getString("appl_dt"));
                wd.setRegn_no(rs.getString("regn_no"));
                wd.setPur_cd(rs.getInt("pur_cd"));
                wd.setPurpose_descr(rs.getString("purpose"));
                wd.setAction_cd(rs.getInt("action_cd"));
                wd.setAction_descr(rs.getString("action_descr"));
                wd.setOffice_remark(rs.getString("office_remark"));
                wd.setRemark_for_public(rs.getString("public_remark"));
                wd.setFile_movement_slno(rs.getInt("file_movement_slno"));
                wd.setStatus(rs.getString("status"));
                wd.setRedirect_url(rs.getString("redirect_url"));
                Map<String, ApplicationStatusDobj> applicationStatus = MasterTableFiller.applStatus;
                if (!CommonUtils.isNullOrBlank(rs.getString("seat_cd"))) {
                    if (applicationStatus != null && !applicationStatus.isEmpty()) {
                        String[] colourAndApplStatus = getColourAndAppStatus(rs.getString("seat_cd"), applicationStatus);
                        wd.setColour(colourAndApplStatus[0]);
                        wd.setApplStatusDescr(colourAndApplStatus[1]);
                    }
                } else {
                    if (applicationStatus != null && !applicationStatus.isEmpty()) {
                        ApplicationStatusDobj clrDbj = applicationStatus.get(TableConstants.STATUS_COMPLETE);
                        if (clrDbj != null) {
                            wd.setColour(clrDbj.getColourCode());
                            wd.setApplStatusDescr(clrDbj.getDescr());
                        }
                    }
                }
                list.add(wd);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmg != null) {
                    tmg.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return list;
    }

    public static String[] getColourAndAppStatus(String seatCd, Map<String, ApplicationStatusDobj> applicationStatusColur) throws Exception {
        String[] colourAndApplStatus = new String[2];
        if (!CommonUtils.isNullOrBlank(seatCd)) {
            if (seatCd.length() > 1) {
                String[] seatCdArr = seatCd.split(",");
                if (seatCdArr.length == 3) {
                    seatCd = seatCd.split(",")[2];
                }
            }

            ApplicationStatusDobj clrDbj = applicationStatusColur.get(seatCd);
            if (clrDbj != null) {
                colourAndApplStatus[0] = clrDbj.getColourCode();
                colourAndApplStatus[1] = clrDbj.getDescr();
            }
        }
        return colourAndApplStatus;
    }

    public static String getAvailableRegnNoList(int allotedOffCode) {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        String available_regn_no_List = "";
        try {
            tmgr = new TransactionManager("getAvailableRegnNoList");
            sql = "select (prefix_series || LPAD(running_no::text, 4, '0')) as available_regn_no,upper_range_no,running_no,prefix_series,no_gen_type, \n"
                    + "       (upper_range_no - running_no) as diff, COALESCE(next_prefix_series, 'Not Available') as next_series ,series_id "
                    + " from vm_regn_series\n"
                    + " where state_cd = ? and off_cd = ? \n"
                    + "order by 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, allotedOffCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (rs.getString("no_gen_type").equals(TableConstants.RANDOM_NUMBER_STATUS_M)) {
                    sql = "select count(1) as total from vm_regn_available where status = ? and state_cd = ? and off_cd = ? and series_id = ? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, TableConstants.RANDOM_NUMBER_STATUS_A);
                    ps.setString(2, Util.getUserStateCode());
                    ps.setInt(3, allotedOffCode);
                    ps.setInt(4, rs.getInt("series_id"));
                    RowSet rs1 = tmgr.fetchDetachedRowSet();
                    if (rs1.next()) {
                        int total = rs.getInt("diff") + rs1.getInt("total");
                        available_regn_no_List += rs.getString("prefix_series");
                        if (total <= 500) {
                            available_regn_no_List += " (<span style='color:red;'>Series is going to be Exhausted</span>)";
                        } else {
                            available_regn_no_List += "";
                        }
                        available_regn_no_List += " | ";
                    }
                } else {
                    if (rs.getInt("upper_range_no") == rs.getInt("running_no")) {
                        available_regn_no_List += rs.getString("prefix_series");
                        available_regn_no_List += " (<span style='color:red;'>Series has been Exhausted</span>)";
                        available_regn_no_List += "<span style='color:blue; font-size:18px;'> | </span>";
                    } else {
                        available_regn_no_List += rs.getString("available_regn_no");
                        if (rs.getInt("diff") <= 500) {
                            available_regn_no_List += " (<span style='color:red;'>Registration No limit has been reached</span>)";
                        } else {
                            available_regn_no_List += "";
                        }
                        available_regn_no_List += "<span style='color:blue; font-size:18px;'> | </span>";
                    }
                }
            }
            if (available_regn_no_List.endsWith("<span style='color:blue; font-size:18px;'> | </span>")) {
                available_regn_no_List = available_regn_no_List.substring(0, available_regn_no_List.length() - 52);
            }
        } catch (SQLException ex) {
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
        return available_regn_no_List;
    }

    public static SeatAllotedDetails getSmartCardAndHsrpStatus(int allotedOffCode) {
        SeatAllotedDetails status = new SeatAllotedDetails();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getSmartCardAndHsrpStatus");
            boolean smartcardStatus = ServerUtil.verifyForSmartCard(Util.getUserStateCode(), allotedOffCode, tmgr);
            boolean hsrpStatus = ServerUtil.verifyForHsrp(Util.getUserStateCode(), allotedOffCode, tmgr);

            if (smartcardStatus) {
                status.setSmartCardStatus("YES");
            } else {
                status.setSmartCardStatus("NO");
            }

            if (hsrpStatus) {
                status.setHsrpStatus("YES");
            } else {
                status.setHsrpStatus("NO");
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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
        return status;
    }

    public static boolean isFeePaid(String appl_no) {
        boolean feePaid = false;
        String link_appl_no = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("inside getLinkApplNo");
            link_appl_no = ServerUtil.getLinkApplNo(appl_no);
            if (link_appl_no != null) {
                String sql = "select rcpt_no from " + TableList.VP_APPL_RCPT_MAPPING + " where appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, link_appl_no);
                RowSet rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    feePaid = true;
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        }
        return feePaid;
    }

    public static List<TradeCertDetailsDobj> getTradeCertificateDetails(String dealerCode) {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        TradeCertDetailsDobj certDobj;
        List<TradeCertDetailsDobj> tradeCertDetailsList = new ArrayList<TradeCertDetailsDobj>();
        Date currentDate = new Date();
        try {
            tmgr = new TransactionManager("getTradeCertificateDetails");
            sql = "select * from " + TableList.VT_TRADE_CERTIFICATE + " where state_cd = ?  and dealer_cd = ? order by vch_catg ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, dealerCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                certDobj = new TradeCertDetailsDobj();
                Date validUpto = new Date(rs.getDate("valid_upto").getTime());
                if (currentDate.after(validUpto)) {
                    certDobj.setMessage("Trade Certificate has been expired ");
                    certDobj.setVehCatg(rs.getString("vch_catg"));
                } else {
                    certDobj.setDealerCd(dealerCode);
                    certDobj.setNoOfVeh(rs.getInt("no_of_vch"));
                    certDobj.setNoOfVehUsed(rs.getInt("no_of_vch_used"));
                    certDobj.setOffCd(rs.getInt("off_cd"));
                    certDobj.setStateCd(rs.getString("state_cd"));
                    certDobj.setTradeCertNo(rs.getString("cert_no"));
                    certDobj.setValidUpto(rs.getDate("valid_upto"));
                    certDobj.setVehCatg(rs.getString("vch_catg"));

                }
                tradeCertDetailsList.add(certDobj);
            }
        } catch (SQLException ex) {
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
        return tradeCertDetailsList;
    }

    public static String getDealerBlockUnBlockStatus(String dealer_cd) {
        String dealerStatusMsg = "";
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        try {
            tmgr = new TransactionManagerReadOnly("getDealerBlockUnBlockStatus");
            sql = "select reason from vp_dealer_status where dealer_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dealer_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dealerStatusMsg = rs.getString("reason");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return dealerStatusMsg;
    }

    public boolean validatePullBackApplication(String applNumberForPendingWrk, String empCode, Map<String, Integer> purCdForPullBack) throws VahanException {
        boolean allGood = false;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        List<String> invalidAppPurCode = new ArrayList();
        try {
            tmgr = new TransactionManager("validatePullBackApplication");
            for (Map.Entry<String, Integer> entry : purCdForPullBack.entrySet()) {
                int purCode = entry.getValue();
                String query = "SELECT * from " + TableList.VHA_STATUS + " where appl_no = ? and pur_cd = ? order by moved_on  desc limit 1";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, applNumberForPendingWrk);
                ps.setInt(2, purCode);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    if (!empCode.equalsIgnoreCase(rs.getString("emp_cd"))) {
                        throw new VahanException("You are not authorised to pull back this application.");
                    } else if (rs.getInt("action_cd") % 1000 != TableConstants.TM_ROLE_ENTRY
                            && rs.getInt("action_cd") % 1000 != TableConstants.TM_ROLE_VERIFICATION
                            && rs.getInt("action_cd") != TableConstants.TM_ROLE_NEW_APPL
                            && rs.getInt("action_cd") != TableConstants.TM_NEW_RC_FITNESS_INSPECTION_APPROVAL
                            && rs.getInt("action_cd") != TableConstants.TM_ROLE_DEALER_NEW_APPL
                            && rs.getInt("action_cd") != TableConstants.TM_ROLE_DEALER_VERIFICATION
                            && rs.getInt("action_cd") != TableConstants.TM_ROLE_DEALER_TEMP_APPL
                            && rs.getInt("action_cd") != TableConstants.TM_ROLE_DEALER_TEMP_VERIFICATION) {
                        invalidAppPurCode.add(entry.getKey());
                    } else if (rs.getInt("action_cd") == TableConstants.VM_ROLE_REGISTRATION_NO_ASSIGNMENT_RTO
                            || rs.getInt("action_cd") == TableConstants.VM_ROLE_REGISTRATION_NO_ASSIGNMENT_DEALER) {
                        invalidAppPurCode.add(entry.getKey());
                    } else {
                        allGood = true;
                    }
                } else {
                    throw new VahanException("Pull back is not allowed at this stage of application.");
                }
            }
            if (!invalidAppPurCode.isEmpty()) {
                for (String key : invalidAppPurCode) {
                    purCdForPullBack.remove(key);
                }
            }
            if (purCdForPullBack.isEmpty()) {
                throw new VahanException("Pull back is not allowed at this stage of application.");
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
        }
        return allGood;
    }

    public boolean pullBackApplication(String applNumberForPendingWrk, String pullBackReason, List<String> selectedPurCdForPullBack) throws VahanException {
        boolean status = false;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        int prevActionCode = 0;
        int verifyActionCode = 0;
        try {
            tmgr = new TransactionManager("pullBackApplication");
            for (int i = 0; i < selectedPurCdForPullBack.size(); i++) {

                int purCode = Integer.parseInt(selectedPurCdForPullBack.get(i));
                String query = "Select a.action_cd as prevactioncode,b.action_cd as verifyactioncode from"
                        + " " + TableList.VHA_STATUS + " a inner join " + TableList.VA_STATUS + " b on a.appl_no = b.appl_no and a.pur_cd = b.pur_cd "
                        + " where a.appl_no = ? and a.pur_cd = ? order by a.moved_on desc limit 1";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, applNumberForPendingWrk);
                ps.setInt(2, purCode);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    prevActionCode = rs.getInt("prevactioncode");
                    verifyActionCode = rs.getInt("verifyactioncode");
                }
                if (verifyActionCode % 1000 == TableConstants.TM_ROLE_APPROVAL
                        || verifyActionCode == TableConstants.TM_ACTION_REGISTERED_VEH_FEE
                        || verifyActionCode == TableConstants.TM_ROLE_NEW_REGISTRATION_FEE
                        || verifyActionCode == TableConstants.TM_ROLE_NEW_REGISTRATION_FIT_FEE
                        || verifyActionCode == TableConstants.TM_ROLE_DEALER_VERIFICATION
                        || verifyActionCode == TableConstants.TM_ROLE_DEALER_NEW_REGN_FEE
                        || verifyActionCode == TableConstants.TM_ROLE_DEALER_TEMP_VERIFICATION
                        || verifyActionCode == TableConstants.TM_ROLE_DEALER_TEMP_REGN_FEE) {
                    query = "INSERT INTO " + TableList.VHA_STATUS + " "
                            + " SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno, \n"
                            + "  action_cd, seat_cd, cntr_id, ?, office_remark, ?, \n"
                            + "  file_movement_type, ? , op_dt, current_timestamp,?"
                            + "  from  " + TableList.VA_STATUS + " where appl_no=? and pur_cd = ? and action_cd = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, TableConstants.STATUS_PULLBACK);
                    ps.setString(2, pullBackReason);
                    ps.setLong(3, Long.parseLong(Util.getEmpCode()));
                    ps.setString(4, Util.getClientIpAdress());
                    ps.setString(5, applNumberForPendingWrk);
                    ps.setInt(6, purCode);
                    ps.setInt(7, verifyActionCode);

                    ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                    query = "update " + TableList.VA_STATUS + " set "
                            + "file_movement_slno=file_movement_slno+1,"
                            + "flow_slno = flow_slno-1,"
                            + "action_cd=?,"
                            + "seat_cd = '" + TableConstants.STATUS_PULLBACK + "',"
                            + "op_dt=current_timestamp"
                            + " where appl_no=? and pur_cd = ? and action_cd = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setInt(1, prevActionCode);
                    ps.setString(2, applNumberForPendingWrk);
                    ps.setInt(3, purCode);
                    ps.setInt(4, verifyActionCode);
                    ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                    status = true;
                } else {
                    String descr = ServerUtil.getPurposeShortDescr(applNumberForPendingWrk);
                    throw new VahanException(descr + " Application already moved.Please Refresh the Browser.");
                }
            }
            if (status) {
                tmgr.commit();
            }
        } catch (SQLException e) {
            throw new VahanException(e.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    throw new VahanException(e.getMessage());
                }
            }
        }
        return status;
    }
}