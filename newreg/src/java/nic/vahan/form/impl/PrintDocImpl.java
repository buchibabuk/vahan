/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.context.FacesContext;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.bean.permit.PermitRouteList;
import nic.vahan.form.dobj.eChallan.CashReportDobj;
import nic.vahan.form.dobj.DisAppNoticeDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.NoOfDuesCerificateDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.eChallan.DocumentReleaseDobj;
import nic.vahan.form.dobj.tradecert.TCPrintDobj;
import nic.vahan.form.dobj.PrintCertificatesDobj;
import nic.vahan.form.dobj.reports.OwnerDisclaimerReportDobj;
import nic.vahan.form.dobj.reports.VehicleParticularDobj;
import nic.vahan.server.ServerUtil;
import nic.vahan.form.dobj.RCCancelCertificateDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.VehicleBlackListReportDobj;
import nic.vahan.form.dobj.eChallan.ChallanReportDobj;
import nic.vahan.form.dobj.eChallan.VehicleReleaseDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitCheckDetailsDobj;
import nic.vahan.form.dobj.permit.PermitHomeAuthDobj;
import nic.vahan.form.dobj.reports.CashReceiptReportDobj;
import nic.vahan.form.dobj.reports.CashRecieptSubListDobj;
import nic.vahan.form.dobj.reports.CollectionSummaryDobj;
import nic.vahan.form.dobj.reports.DownloadDispatchDobj;
import nic.vahan.form.dobj.reports.FCPrintReportDobj;
import nic.vahan.form.dobj.reports.HSRPAuthorizationReportDobj;
import nic.vahan.form.dobj.reports.NOCReportDobj;
import nic.vahan.form.dobj.reports.NewRCReportDobj;
import nic.vahan.form.dobj.reports.PendingReportDobj;
import nic.vahan.form.dobj.reports.ScrappedVehicleReportDobj;
import nic.vahan.form.dobj.reports.TaxDefaulterListDobj;
import nic.vahan.form.dobj.reports.TempRCReportDobj;
import nic.vahan.form.dobj.reports.VmRoadSafetySloganPrintDobj;
import nic.vahan.form.dobj.tradecert.ApplicationFeeTradeCertDobj;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.form.impl.tradecert.ApplicationFeeTradeCertImpl;
import nic.vahan.form.impl.permit.PermitCheckDetailsImpl;
import nic.vahan.form.impl.tradecert.ApplicationTradeCertImpl;
import nic.vahan.server.CommonUtils;
import static nic.vahan.server.ServerUtil.verifyForHsrp;
import org.apache.log4j.Logger;

public class PrintDocImpl {

    private static final Logger LOGGER = Logger.getLogger(PrintDocImpl.class);
    static SessionVariables sessionVariables = new SessionVariables();

    public static ArrayList<PrintCertificatesDobj> getPurCdPrintDocsDetails(String appl_no, int purCD) throws VahanException {
        ArrayList<PrintCertificatesDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        VahanException vahanexecption = null;
        try {
            tmgr = new TransactionManagerReadOnly("getPurCdPrintDocsDetails");
            ps = tmgr.prepareStatement("SELECT va.appl_no,va_details.regn_no,va.pur_cd ,pmast.descr"
                    + " from " + TableList.VA_STATUS + " va,tm_purpose_mast pmast,va_details "
                    + " where va.appl_no=? and va.pur_cd=? "
                    + " and va.pur_cd=pmast.pur_cd and va_details.pur_cd=va.pur_cd and va_details.appl_no = va.appl_no");
            ps.setString(1, appl_no);
            ps.setInt(2, purCD);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                PrintCertificatesDobj dobj = new PrintCertificatesDobj();
                dobj.setPurCd(rs.getInt("pur_cd"));
                dobj.setPurCdDescr(rs.getString("descr"));
                dobj.setAppl_no(appl_no);
                dobj.setRegno(rs.getString("regn_no"));
                list.add(dobj);
            }
        } catch (Exception ex) {
            vahanexecption = new VahanException("Error in fetching details for [ " + appl_no + "]");
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

    public static void deleteAndSaveHistory(String appl_no, String reg_no, boolean isRCDispatch, boolean isRCDispatchWithoutPostalFee, int pur_cd, String state_cd, int off_cd) {
        TransactionManager tmgr = null;
        PreparedStatement psvarcprint = null;
        PreparedStatement psvhrcprint = null;
        try {
            tmgr = new TransactionManager("deleteAndSaveHistory");
            String vhrcprintSql = " insert into " + TableList.VH_RC_PRINT
                    + " select appl_no,regn_no,op_dt,current_timestamp,?,?,? from " + TableList.VA_RC_PRINT + " where appl_no=? AND state_cd=? AND off_cd=?";
            psvhrcprint = tmgr.prepareStatement(vhrcprintSql);
            psvhrcprint.setString(1, Util.getEmpCode());
            psvhrcprint.setString(2, state_cd);
            psvhrcprint.setInt(3, off_cd);
            psvhrcprint.setString(4, appl_no);
            psvhrcprint.setString(5, state_cd);
            psvhrcprint.setInt(6, off_cd);
            psvhrcprint.executeUpdate();
            if ((isRCDispatch && pur_cd == 200) || (isRCDispatchWithoutPostalFee)) {
                vhrcprintSql = " insert into " + TableList.VA_DISPATCH
                        + " select a.state_cd, a.off_cd,?, a.regn_no,'RC',a.owner_name,upper(a.c_add1 || ', ' || a.c_add2 || ', ' || a.c_add3 || ',' || a.c_district_name || '-' || a.c_state_name || '-' || a.c_pincode) as c_full_add,b.mobile_no,current_timestamp,? "
                        + " from " + TableList.VIEW_VV_OWNER + " a "
                        + " left outer join " + TableList.VT_OWNER_IDENTIFICATION + " b on b.regn_no = a.regn_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd "
                        + " where a.regn_no = ? and a.state_cd=? and a.off_cd=? "
                        + " and ? not in (select appl_no from " + TableList.VA_DISPATCH + " where appl_no=?) ";
                psvhrcprint = tmgr.prepareStatement(vhrcprintSql);
                psvhrcprint.setString(1, appl_no);
                psvhrcprint.setString(2, Util.getEmpCode());
                psvhrcprint.setString(3, reg_no);
                psvhrcprint.setString(4, state_cd);
                psvhrcprint.setInt(5, off_cd);
                psvhrcprint.setString(6, appl_no);
                psvhrcprint.setString(7, appl_no);
                psvhrcprint.executeUpdate();
            }
            String varcprintSql = " delete from " + TableList.VA_RC_PRINT + " where appl_no=? AND state_cd=? AND off_cd=?";
            psvarcprint = tmgr.prepareStatement(varcprintSql);
            psvarcprint.setString(1, appl_no);
            psvarcprint.setString(2, state_cd);
            psvarcprint.setInt(3, off_cd);
            psvarcprint.executeUpdate();

            vhrcprintSql = "INSERT INTO " + TableList.VHA_STATUS + " "
                    + " SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno, "
                    + "  action_cd, seat_cd, cntr_id, status, office_remark, public_remark, "
                    + "  file_movement_type, ? , op_dt, current_timestamp "
                    + "  from  " + TableList.VA_STATUS + " where appl_no=? and pur_cd=? ";
            psvhrcprint = tmgr.prepareStatement(vhrcprintSql);
            psvhrcprint.setLong(1, Long.parseLong(Util.getEmpCode()));
            psvhrcprint.setString(2, appl_no);
            psvhrcprint.setInt(3, TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE);
            psvhrcprint.executeUpdate();
            tmgr.commit();
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
    }

    public static void SaveVHRCPrintHistory(String applno, String state_cd, int off_cd) {
        PreparedStatement psvhrcprint = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("deleteAndSaveHistory");
            String vhrcprintSql = " insert into " + TableList.VH_RC_PRINT
                    + " select appl_no,regn_no,op_dt,current_timestamp,?,state_cd,off_cd from " + TableList.VH_RC_PRINT + " where appl_no=? AND state_cd=? AND off_cd=? order by op_dt desc limit 1";
            psvhrcprint = tmgr.prepareStatement(vhrcprintSql);
            psvhrcprint.setString(1, Util.getEmpCode());
            psvhrcprint.setString(2, applno);
            psvhrcprint.setString(3, state_cd);
            psvhrcprint.setInt(4, off_cd);
            psvhrcprint.executeUpdate();
            tmgr.commit();
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
    }

    public static ArrayList<PrintCertificatesDobj> getPendingRCPrintDocsDetails(String state_cd, int off_cd, int actionCd, String dealerCd, boolean newRCPrintAtDealer) throws VahanException {
        ArrayList<PrintCertificatesDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        String dealerCdSQL = "";
        RowSet rs;
        try {
            if (newRCPrintAtDealer) {
                if (actionCd == TableConstants.TM_ROLE_DEALER_PRINT_RC) {
                    dealerCdSQL = " and c.dealer_cd = ? and c.vh_class <= 50 and b.pur_cd = " + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE + " ";
                } else {
                    dealerCdSQL = " and b.pur_cd <> " + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE + " ";
                }
            }

            tmgr = new TransactionManagerReadOnly("getPendingRCPrintDocsDetails");
            sql = "select distinct a.appl_no, a.regn_no, a.state_cd, a.off_cd,a.op_dt,c.regn_type,c.regn_dt,b.pur_cd,COALESCE(c.manu_yr,0) as manu_yr , COALESCE(c.manu_mon,0) as manu_mon from  " + TableList.VA_RC_PRINT + " a "
                    + " inner join " + TableList.VA_DETAILS + " b on b.appl_no = a.appl_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd "
                    + " left outer join " + TableList.VT_OWNER + " c on c.regn_no = a.regn_no and c.state_cd = a.state_cd and c.off_cd = a.off_cd "
                    + "  where a.state_cd = ? and a.off_cd = ? and a.op_dt > current_date - 15 " + dealerCdSQL + " order by a.op_dt DESC";
            int i = 1;
            ps = tmgr.prepareStatement(sql);
            ps.setString(i++, state_cd);
            ps.setInt(i++, off_cd);
            if (actionCd == TableConstants.TM_ROLE_DEALER_PRINT_RC) {
                ps.setString(i++, dealerCd);
            }
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                PrintCertificatesDobj dobj = new PrintCertificatesDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegno(rs.getString("regn_no"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setRegn_type(rs.getString("regn_type"));
                dobj.setRegn_date(rs.getDate("regn_dt"));
                dobj.setPurCd(rs.getInt("pur_cd"));
                if (rs.getInt("manu_mon") > 0 && rs.getInt("manu_yr") > 0 && rs.getInt("manu_mon") < 10) {
                    dobj.setMfgYearMonthYYYYMM(Integer.parseInt(rs.getString("manu_yr") + "0" + rs.getString("manu_mon")));
                } else if (rs.getInt("manu_mon") > 0 && rs.getInt("manu_yr") > 0) {
                    dobj.setMfgYearMonthYYYYMM(Integer.parseInt(rs.getString("manu_yr") + rs.getString("manu_mon")));
                }
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
            }
        }
        return list;
    }

    public static ArrayList<PrintCertificatesDobj> isRegnExistForRC(String regn_no, String state_cd, int off_cd, int actionCd, String dealerCd, boolean newRCPrintAtDealer) {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        PrintCertificatesDobj dobj = null;
        String sql;
        RowSet rs;
        String dealerCdSQL = "";
        ArrayList<PrintCertificatesDobj> list = new ArrayList();
        try {
            if (newRCPrintAtDealer) {
                if (actionCd == TableConstants.TM_ROLE_DEALER_PRINT_RC) {
                    dealerCdSQL = " and c.dealer_cd = ? and c.vh_class <= 50 and b.pur_cd = " + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE + " ";
                } else {
                    dealerCdSQL = " and b.pur_cd <> " + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE + " ";
                }
            }
            tmgr = new TransactionManagerReadOnly("PrintDocImpl.isRegnExistForRC");
            sql = "select distinct a.appl_no,a.regn_no,c.regn_type,c.regn_dt,b.pur_cd,COALESCE(c.manu_yr,0) as manu_yr, COALESCE(c.manu_mon,0) as manu_mon  from  " + TableList.VA_RC_PRINT + "  a "
                    + " inner join " + TableList.VA_DETAILS + "  b on b.appl_no = a.appl_no "
                    + " left outer join " + TableList.VT_OWNER + "  c on c.regn_no = a.regn_no and c.state_cd = a.state_cd and c.off_cd = a.off_cd "
                    + " where a.regn_no = ? and a.state_cd = ? and a.off_cd = ? " + dealerCdSQL + " ";
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, regn_no);
            ps.setString(i++, state_cd);
            ps.setInt(i++, off_cd);
            if (actionCd == TableConstants.TM_ROLE_DEALER_PRINT_RC) {
                ps.setString(i++, dealerCd);
            }
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new PrintCertificatesDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegno(rs.getString("regn_no"));
                dobj.setRegn_type(rs.getString("regn_type"));
                dobj.setRegn_date(rs.getDate("regn_dt"));
                dobj.setPurCd(rs.getInt("pur_cd"));
                if (rs.getInt("manu_mon") > 0 && rs.getInt("manu_mon") < 10) {
                    dobj.setMfgYearMonthYYYYMM(Integer.parseInt(rs.getString("manu_yr") + "0" + rs.getString("manu_mon")));
                } else {
                    dobj.setMfgYearMonthYYYYMM(Integer.parseInt(rs.getString("manu_yr") + rs.getString("manu_mon")));
                }

                list.add(dobj);
            }
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
            return list;
        }
    }

    public static ArrayList<PrintCertificatesDobj> isRegnExistForPrintedRC(String regn_no, String state_cd, int off_cd) {
        ArrayList<PrintCertificatesDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        try {
            tmgr = new TransactionManagerReadOnly("isRegnExistForPrintedRC");

            sql = "select distinct a.appl_no, a.regn_no, a.state_cd,a.printed_on,a.off_cd,to_char(a.printed_on,'dd-Mon-yyyy HH24:mi:ss') as printedondt,b.user_name as printed_by from  " + TableList.VH_RC_PRINT + " a "
                    + " LEFT OUTER JOIN " + TableList.TM_USER_INFO + " b on b.user_cd= regexp_replace(COALESCE(trim(a.printed_by::text), '0'), '[^0-9]', '0', 'g')::numeric and b.state_cd=a.state_cd and b.off_cd=a.off_cd "
                    + " where a.state_cd = ? and a.off_cd =? and a.regn_no=? order by printed_on DESC limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, regn_no);

            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                PrintCertificatesDobj dobj = new PrintCertificatesDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegno(rs.getString("regn_no"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setPrinted_on(rs.getString("printedondt"));
                dobj.setPrinted_by(rs.getString("printed_by"));
                list.add(dobj);
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
        return list;
    }

    public static ArrayList<PrintCertificatesDobj> isRegnExistForFC(String regn_no, String state_cd, int off_cd) {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        PrintCertificatesDobj dobj = null;
        String sql;
        RowSet rs;
        ArrayList<PrintCertificatesDobj> list = new ArrayList();
        try {
            tmgr = new TransactionManagerReadOnly("PrintDocImpl.isRegnExistForFC");
            sql = "select appl_no,regn_no from  " + TableList.VA_FC_PRINT
                    + " where regn_no=? and state_cd=? and off_cd=? order by op_dt DESC";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new PrintCertificatesDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegno(rs.getString("regn_no"));
                list.add(dobj);
            }
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
            return list;
        }
    }

    public static ArrayList<PrintCertificatesDobj> getTempPrintDocsDetails(int pur_cd) throws VahanException {
        ArrayList<PrintCertificatesDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        VahanException vahanexecption = null;
        try {
            tmgr = new TransactionManagerReadOnly("getTempPrintDocsDetails");
            ps = tmgr.prepareStatement("select distinct a.appl_no, a.temp_regn_no as regn_no, b.state_cd, b.off_cd"
                    + " from " + TableList.VA_TEMP_RC_PRINT + " a, " + TableList.VA_DETAILS + " b"
                    + " where a.appl_no = b.appl_no and b.state_cd = ? and b.off_cd = ? and b.pur_cd = ? ");
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setInt(3, pur_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                PrintCertificatesDobj dobj = new PrintCertificatesDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegno(rs.getString("regn_no"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setState_cd(rs.getString("state_cd"));
                list.add(dobj);
            }

        } catch (Exception ex) {
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

    public static TempRCReportDobj getTempRcReportDobj(String regn_no, String appl_no, String state_cd, int off_cd, String rcRadiobtnValue) throws VahanException {

        TransactionManagerReadOnly tmgr = null;
        TempRCReportDobj dobj = null;
        float tax = 0f;
        float rebate = 0f;
        float surcharge = 0f;
        float penalty = 0f;
        float interest = 0f;
        float tax1 = 0f;
        float tax2 = 0f;
        Map<String, String> taxPaid = new HashMap<>();
        Date taxPaidUpto = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        TmConfigurationDobj configurationDobj = Util.getTmConfiguration();

        try {
            String sql = "SELECT a.state_cd,a.off_cd, upper(a.state_name)as state_name , a.off_name, a.temp_regn_no, to_char(a.valid_from,'dd-Mon-yyyy')as valid_from,to_char(a.valid_upto,'dd-Mon-yyyy')as valid_upto, a.purchase_dt,case when tax_mode='E' then 'EXEMPTED' else 'NOT EXEMPTED' end as isexem,\n"
                    + " a.owner_name, a.f_name, upper(a.c_add1 || ', ' || a.c_add2 || ', ' || a.c_add3 || ',' || chr(10) || a.c_district_name || '-' || a.c_state_name || '-' || a.c_pincode) as c_full_add,"
                    + " upper(a.p_add1 || ', ' || a.p_add2 || ', ' || a.p_add3 || ',' || chr(10) ||  a.p_district_name || ', ' || a.p_state_name || '-' || a.p_pincode) as p_full_add,"
                    + " a.owner_cd,g.descr as ow_descr,case when owner_cd in (4,5) then 'GOVT' else 'PRIVATE' end as ISGOV ,  a.regn_type, upper(a.vh_class_desc)  as vh_class_desc, a.chasi_no, a.eng_no, a.maker, a.maker_name, a.model_name, a.body_type, a.no_cyl, a.hp,"
                    + " a.seat_cap, a.stand_cap, a.sleeper_cap, a.unld_wt, a.ld_wt, a.gcw, a.fuel, a.fuel_descr, a.color, a.manu_mon, a.manu_yr, a.norms_descr, upper(a.norms_descr) as norms_descr, a.wheelbase,"
                    + " a.cubic_cap, a.floor_area, a.ac_fitted, a.audio_fitted, a.video_fitted, a.vch_purchase_as, a.vch_catg, a.dealer_cd, upper(a.dlr_name || ', ' || a.dlr_add1 || ', ' || a.dlr_add2 || ', ' ||"
                    + " a.dlr_add3 || ', ' || a.dlr_city || ',' || chr(10) || a.dlr_district || '-' || a.dlr_pincode) as dlr_full_add , a.sale_amt, upper(a.garage_add) as garage_add, a.length, a.width, a.height, a.regn_upto, a.fit_upto, a.annual_income,"
                    + " a.imported_vch, a.state_cd_to, k.descr as statedescrto, a.off_cd_to, i.off_name as hoffnameto, a.purpose, a.body_building, b.f_axle_descp, b.r_axle_descp, b.o_axle_descp, b.t_axle_descp, b.f_axle_weight, b.r_axle_weight, b.o_axle_weight, b.t_axle_weight,"
                    + " c.chasi_no as tr_chasi_no, c.body_type as tr_body_type, c.ld_wt as tr_ld_wt, c.unld_wt as tr_unld_wt, c.f_axle_descp as tr_f_axle_descp, c.r_axle_descp as tr_r_axle_descp, c.o_axle_descp as tr_o_axle_descp, c.t_axle_descp as tr_t_axle_descp,  c.f_axle_weight as tr_f_axle_weight, c.r_axle_weight as tr_r_axle_weight, c.o_axle_weight as tr_o_axle_weight, c.t_axle_weight as tr_t_axle_weight,"
                    + " date(f.from_dt) as hypth_from_dt, (select distinct max(sr_no) from vv_hypth where regn_no=f.regn_no and f.state_cd =  state_cd and f.off_cd =  off_cd  ) as sr_no,(f.fncr_name || ', ' || f.fncr_add1 || ', ' || f.fncr_add2 || ', ' || f.fncr_add3 || ', ' || chr(10) ||  f.fncr_district || ', ' || f.fncr_district_name || ', ' || f.fncr_state || ', ' || f.fncr_state_name || ', ' || f.fncr_pincode) as fncr_full_add,"
                    + " to_char(h.tax_from,'dd-Mon-yyyy')as tax_from, h.tax_amt , h.rcpt_no , to_char(h.tax_upto,'dd-Mon-yyyy')as tax_upto,to_char(a.op_dt,'dd-Mon-yyyy')as op_dt,j.rcpt_heading,j.rcpt_subheading, to_char(current_timestamp,'dd-Mon-yyyy hh24:mi:ss') as printed_on,vtf.fees,vtf.rcpt_no as feercpt_no,to_char(vtf.rcpt_dt,'dd-Mon-yyyy')as rcpt_dt,vtf.fine,j.temp_rc_heading,usrsign.doc_sign as userSign "
                    + " FROM vv_owner_temp a \n"
                    + " LEFT OUTER JOIN vt_axle b ON b.regn_no = a.temp_regn_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd \n"
                    + " LEFT OUTER JOIN vt_trailer c ON c.regn_no = a.temp_regn_no \n"
                    + " LEFT OUTER JOIN vt_retrofitting_dtls d ON d.regn_no = a.temp_regn_no and d.state_cd = a.state_cd and d.off_cd = a.off_cd \n"
                    + " LEFT OUTER JOIN vv_insurance e ON e.regn_no = a.temp_regn_no and e.state_cd = a.state_cd and e.off_cd = a.off_cd \n"
                    + " LEFT OUTER JOIN vv_hypth f ON f.regn_no = a.temp_regn_no and f.state_cd = a.state_cd and f.off_cd = a.off_cd   and sr_no=(select distinct max(sr_no) from vv_hypth where regn_no=f.regn_no and f.state_cd = state_cd and f.off_cd = off_cd ) \n"
                    + " LEFT OUTER JOIN vm_owcode g ON g.ow_code=a.owner_cd \n"
                    + " LEFT OUTER JOIN vt_tax h ON h.regn_no=a.temp_regn_no and tax_amt>0 and a.state_cd = h.state_cd and a.off_cd = h.off_cd \n"
                    + " LEFT OUTER JOIN (Select * from vt_fee where regn_no=? and fees>0 and state_cd =? and off_cd=? and pur_cd=18 ORDER BY RCPT_DT DESC limit 1) vtf ON vtf.regn_no=a.temp_regn_no"
                    + " LEFT OUTER JOIN tm_configuration j ON j.state_cd = a.state_cd \n"
                    + " LEFT JOIN tm_office i ON i.off_cd = a.off_cd_to AND i.state_cd = a.state_cd_to::bpchar \n"
                    + " LEFT JOIN tm_state k ON k.state_code = a.state_cd_to::bpchar \n"
                    + " LEFT OUTER JOIN (select * from vha_status  where appl_no = ? order by moved_on desc limit 1) vhastatus on  vhastatus.appl_no = a.appl_no and vhastatus.pur_cd in(18,124) \n"
                    + " LEFT OUTER JOIN " + TableList.TM_USER_SIGN + " usrsign ON usrsign.user_cd = vhastatus.emp_cd \n"
                    + " WHERE a.temp_regn_no=? and a.state_cd=? and a.off_cd=? ";
            tmgr = new TransactionManagerReadOnly("getTempRcReportDobj-1");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            ps.setString(4, appl_no);
            ps.setString(5, regn_no);
            ps.setString(6, state_cd);
            ps.setInt(7, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new TempRCReportDobj();
                if (!state_cd.contains("KA")) {
                    dobj.setNotKAState(true);
                }
                dobj.setTempRCHeading(rs.getString("temp_rc_heading"));
                dobj.setReportHeading(rs.getString("rcpt_heading"));
                dobj.setReportSubHeading(rs.getString("rcpt_subheading"));
                dobj.setAppl_no(appl_no);

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
                    if (configurationDobj.getTmPrintConfgDobj().getTemporary_rc_remarks() != null && !configurationDobj.getTmPrintConfgDobj().getTemporary_rc_remarks().isEmpty()) {
                        dobj.setRemarks(configurationDobj.getTmPrintConfgDobj().getTemporary_rc_remarks());
                    }
                    if (configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan()) {
                        VmRoadSafetySloganPrintDobj vmrssdobj = getStateRoadSafetySlogan();
                        if (vmrssdobj != null) {
                            dobj.setShowRoadSafetySlogan(configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan());
                            dobj.setRoadSafetySloganDobj(vmrssdobj);
                        }
                    }
                }
                dobj.setBodyType(rs.getString("body_type"));
                dobj.setChasiNo(rs.getString("chasi_no"));
                dobj.setColor(rs.getString("color"));
                dobj.setEngineNo(rs.getString("eng_no"));
                if (rs.getInt("fees") > 0) {
                    dobj.setFeeAmt(" Fees Rs " + rs.getString("fees") + " /-");
                }
                if (rs.getInt("fine") > 0) {
                    dobj.setFineAmt(" Fine Rs" + rs.getString("fine") + " /-");
                }
                if (rs.getString("off_cd_to") != null && !rs.getString("off_cd_to").equals("")) {
                    dobj.setPaddress(rs.getString("hoffnameto") + " " + rs.getString("statedescrto") + " ( office Code - " + rs.getString("state_cd_to") + rs.getString("off_cd_to") + " )");
                } else {
                    dobj.setPaddress(rs.getString("hoffnameto") + " " + rs.getString("statedescrto"));
                }

                if (rs.getString("purpose") != null && !rs.getString("purpose").equals("") && rs.getString("purpose").equalsIgnoreCase("Body Building")) {
                    dobj.setBodybildaddress(rs.getString("body_building"));
                }

                if (rs.getString("feercpt_no") != null && rs.getString("feercpt_no").length() > 0) {
                    dobj.setFeeRectNo(" VIDE CH No " + rs.getString("feercpt_no"));
                }
                if (rs.getString("rcpt_dt") != null && rs.getString("rcpt_dt").length() > 0) {
                    dobj.setFeeRcptDate(" Dated " + rs.getString("rcpt_dt"));
                }
                if (rs.getString("fncr_full_add") != null && !rs.getString("fncr_full_add").isEmpty()) {
                    dobj.setFincierDtls(rs.getString("fncr_full_add"));
                }
                dobj.setMakerName(rs.getString("maker_name"));
                dobj.setOffName(rs.getString("off_name"));
                dobj.setStateName(rs.getString("state_name"));
                dobj.setOwnerAddress(rs.getString("c_full_add"));
                dobj.setOwnerName(rs.getString("owner_name"));
                dobj.setFatherName(rs.getString("f_name"));
                dobj.setPrintedOn(rs.getString("printed_on"));
                dobj.setSeatingCap(rs.getString("seat_cap"));
                if (rs.getString("rcpt_no") != null && rs.getString("rcpt_no").length() > 0) {
                    dobj.setTaxRcptNo("VIDE CH No " + rs.getString("rcpt_no"));
                }

                taxPaid = ServerUtil.taxPaidInfo(false, regn_no, 58);

                if (!taxPaid.isEmpty()) {
                    for (Map.Entry<String, String> entry : taxPaid.entrySet()) {
                        if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("tax_mode") && entry.getValue() != null) {
                            if (!entry.getValue().isEmpty() && "L,O".contains(entry.getValue())) {
                                dobj.setTaxUptoDate("(One Time)");
                                break;
                            }
                        } else if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("tax_upto") && entry.getValue() != null) {
                            taxPaidUpto = DateUtils.parseDate(entry.getValue());
                            if (rs.getString("tax_from") != null && rs.getString("tax_from").length() > 0) {
                                dobj.setTaxFromdate(" Dated From " + rs.getString("tax_from"));
                            }
                            dobj.setTaxUptoDate(" to " + format.format(taxPaidUpto));
                            break;
                        }
                    }
                }
                dobj.setTempRegnFrom(rs.getString("valid_from"));
                dobj.setTempRegnUpto(rs.getString("valid_upto"));
                dobj.setVchDesc(rs.getString("vh_class_desc"));
                dobj.setTempRegnNo(rs.getString("temp_regn_no"));
                dobj.setIssueDate(rs.getString("op_dt"));
                dobj.setMakerModelName(rs.getString("model_name"));
                if (rs.getBytes("userSign") != null && !rs.getBytes("userSign").equals("")) {
                    dobj.setUserSign(rs.getBytes("userSign"));
                    dobj.setIsUserSignExist(true);
                } else {
                    dobj.setIsUserSignExist(false);
                }
                tmgr = new TransactionManagerReadOnly("getTempRcReportDobj-2");
                sql = "SELECT * from vt_tax_breakup    where rcpt_no in (select rcpt_no from vp_appl_rcpt_mapping where appl_no = ? limit 1 ) and  state_cd=? and off_cd=? and pur_cd in(58,59)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_no);
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
                rs = tmgr.fetchDetachedRowSet();
                while (rs.next()) {
                    tax += rs.getFloat("tax");
                    rebate += rs.getFloat("rebate");
                    surcharge += rs.getFloat("surcharge");
                    penalty += rs.getFloat("Penalty");
                    interest += rs.getFloat("Interest");
                    tax1 += rs.getFloat("tax1");
                    tax2 += rs.getFloat("tax2");
                }

                if (tax > 0) {
                    dobj.setTaxAmt("Tax Rs " + String.valueOf(tax).replace(".00", "") + " /-");
                }
                if (rebate > 0) {
                    dobj.setRebateAmt("Rebate Rs " + String.valueOf(rebate).replace(".00", "") + " /-");
                }
                if (surcharge > 0) {
                    dobj.setSurchargeAmt("Surcharge Rs " + String.valueOf(surcharge).replace(".00", "") + " /-");
                }
                if (penalty > 0) {
                    dobj.setPenaltyAmt("Penalty Rs " + String.valueOf(penalty).replace(".00", "") + " /-");
                }
                if (interest > 0) {
                    dobj.setInterestAmt("Interest Rs " + String.valueOf(interest).replace(".00", "") + " /-");
                }
                if (tax1 > 0) {
                    dobj.setTax1Amt("Rs " + String.valueOf(tax1).replace(".00", "") + " /-");
                }
                if (tax2 > 0) {
                    dobj.setTax2Amt("Rs " + String.valueOf(tax2).replace(".00", "") + " /-");
                }

                TmConfigurationDobj tmDobj = Util.getTmConfiguration();
                if (tmDobj != null && tmDobj.isTempFeeInNewRegis() && (dobj.getFeeAmt() == null || dobj.getFeeAmt().equals("")) && (dobj.getFeeRectNo() == null || dobj.getFeeRectNo().equals(""))) {
                    tmgr = new TransactionManagerReadOnly("getTempRcReportDobj-3");
                    sql = " select f.fees ,m.rcpt_no ,to_char(f.rcpt_dt,'dd-Mon-yyyy')as rcpt_dt,f.pur_cd from vt_owner_temp a \n"
                            + " left join vp_appl_rcpt_mapping m on  m.appl_no=a.appl_no and a.state_cd=m.state_cd and a.off_cd=m.off_cd \n"
                            + " left join vt_fee f on m.rcpt_no=f.rcpt_no and f.pur_cd in (" + TableConstants.VM_TRANSACTION_MAST_TEMP_REG + "," + TableConstants.VM_TRANSACTION_MAST_TEMP_TAX + ") and a.state_cd=f.state_cd and a.off_cd=f.off_cd \n"
                            + " where a.temp_regn_no = ? and a.state_cd = ? and a.off_cd = ? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, regn_no);
                    ps.setString(2, state_cd);
                    ps.setInt(3, off_cd);
                    rs = tmgr.fetchDetachedRowSet();
                    while (rs.next()) {
                        if (rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
                            if (rs.getString("fees") != null && !rs.getString("fees").equals("")) {
                                dobj.setFeeAmt("Rs " + rs.getString("fees") + " /-");
                            }
                            if (rs.getString("rcpt_no") != null && !rs.getString("rcpt_no").equals("")) {
                                dobj.setFeeRectNo(" VIDE CH No " + rs.getString("rcpt_no"));
                            }
                            if (rs.getString("rcpt_dt") != null && !rs.getString("rcpt_dt").equals("")) {
                                dobj.setFeeRcptDate(" Dated " + rs.getString("rcpt_dt"));
                            }
                        } else if (rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_TEMP_TAX) {
                            if (rs.getString("fees") != null && !rs.getString("fees").equals("")) {
                                dobj.setTaxAmt("Rs " + rs.getString("fees") + " /-");
                            }
                            if (rs.getString("rcpt_no") != null && !rs.getString("rcpt_no").equals("")) {
                                dobj.setTaxRcptNo(" VIDE CH No " + rs.getString("rcpt_no"));
                            }
                        }
                    }
                }
                if (rcRadiobtnValue.equalsIgnoreCase("PENDINGTEMPRC") || rcRadiobtnValue.equalsIgnoreCase("TEMPRC")) {
                    deleteAndSaveHistoryForTempRegnNo(appl_no, regn_no, state_cd, off_cd);
                } else if (rcRadiobtnValue.equalsIgnoreCase("PRINTEDTEMPRC") || rcRadiobtnValue.equalsIgnoreCase("REPRINTTEMPRC")) {
                    SaveTempRCPrintHistory(appl_no, state_cd, off_cd);
                }
            }
        } catch (VahanException e) {
            throw new VahanException("Something Went Wrong, Please try again.");
        } catch (Exception e) {
            LOGGER.error(e);
            throw new VahanException("Something Went wrong.Please try again");
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

    public static VehicleParticularDobj getVehicleParticularDobj(SessionVariables sessionVariables, String regn_no, String appl_no) throws VahanException, ParseException {

        TransactionManager tmgr = null;
        VehicleParticularDobj dobj = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        Date dateRegn_upto = null;
        Date dateFit_upto = null;
        VehicleParticularDobj hptSubListDobj = null;
        Map<String, String> taxPaid = new HashMap<>();
        Date taxPaidUpto = null;
        TmConfigurationDobj configurationDobj = Util.getTmConfiguration();
        int i = 1;
        try {
            String sql = " SELECT to_char(g.transfer_dt ,'dd-Mon-yyyy') as transfer_on,g.owner_name as previous_owner_name,"
                    + " to_char(k.entry_dt,'dd-Mon-yyyy') as entry_date, k.old_regn_no as othreStateOldRegnNo,o.descr as old_state,to_char(n.moved_on,'dd-Mon-yyyy') as conversion_dt,n.old_regn_no as ConvOldRegnNo,to_char((select max(from_dt) from vh_ca where regn_no=det.regn_no),'dd-Mon-yyyy') as last_ca_on, "
                    + " to_char((select max(op_dt) from vh_alt where regn_no=det.regn_no),'dd-Mon-yyyy') as last_alt_on,to_char(a.fit_upto,'dd-Mon-yyyy') as fit_upto, "
                    + " a.regn_no,a.regn_dt,a.owner_name,a.owner_sr,a.f_name,a.vh_class_desc,a.vh_class,a.maker_name,a.model_name,a.body_type,a.manu_mon,a.manu_yr,a.no_cyl,a.chasi_no,a.eng_no, "
                    + " a.hp, a.seat_cap,a.stand_cap,a.sleeper_cap,a.unld_wt, a.ld_wt,to_char(a.regn_upto,'dd-Mon-yyyy') as regn_upto,a.cubic_cap,a.color,fuel_descr,"
                    + " case when h.tax_mode IN ('L', 'O') then 'One Time' else to_char(tax_upto,'dd-Mon-yyyy') end as tax_upto,h.tax_amt, "
                    + " ins.policy_no, ins.ins_from, ins.ins_upto, ins.ins_company_name,ins.ins_type_descr,m.mobile_no,m.email_id, "
                    + " (a.c_add1 || ',' ||  a.c_add2 || ',' || a.c_add3 || ',' || a.c_district_name || ',' || a.c_state_name || '-'|| COALESCE(a.c_pincode::varchar, ''))  AS owner_dtls, "
                    + " det.regn_no,case when d.pur_cd=13 then d.fees else '0'::numeric end as fee,d.rcpt_dt,d.rcpt_no,UPPER(a.off_name) AS off_name, UPPER(a.state_name) as state_name,d.pur_cd, "
                    + " noc.noc_no,noc.noc_dt,e.descr AS statetoname,f.off_name as offtoname,"
                    + " vmblk.descr as comp_type, vtblk.fir_no,to_char(vtblk.fir_dt,'dd-Mon-yyyy')as fir_dt,vtblk.complain,to_char(vtblk.complain_dt,'dd-Mon-yyyy')as comp_dt, to_char(current_timestamp,'dd-Mon-yyyy hh24:mi:ss') as printed_on,p.pmt_no, to_char(p.valid_from,'dd-Mon-yyyy') as valid_from,to_char(p.valid_upto,'dd-Mon-yyyy') as valid_upto,sertp.descr as serviceType,pmttp.descr as permitType, pmtct.descr as permitCatg,paut.auth_no,to_char(paut.auth_fr,'dd-Mon-yyyy') as auth_fr,to_char(paut.auth_to,'dd-Mon-yyyy') as auth_to,p.state_cd,p.off_cd,p.appl_no,q.descr as vehNorms, a.status ,r.rcpt_heading,r.rcpt_subheading ,a.floor_area,a.wheelbase, \n"
                    + " (s.fncr_name || ',' || s.fncr_add1 || ',' || s.fncr_add2 || ',' || COALESCE(s.fncr_add3::varchar,'') || ',' || COALESCE(w.descr::varchar,'') || ',' || COALESCE(s.fncr_pincode::varchar,'') || ',' || 'Terminate on ' || COALESCE(to_char(s.term_dt,'dd-Mon-yyyy')::varchar)) AS hpt_dtls,"
                    + " ('Surrender on ' || COALESCE(to_char(t.surr_dt,'dd-Mon-yyyy')::varchar, '') || ' Reason ' || t.reason) as rc_surr_dtls ,"
                    + " ('Cancel on ' || COALESCE(to_char(u.cancel_dt,'dd-Mon-yyyy')::varchar, '') || ' Reason ' || u.reason) as rc_cancel_dtld,"
                    + " v.kit_srno,v.kit_type,v.kit_manuf,v.kit_pucc_norms,v.workshop,v.workshop_lic_no,to_char(v.fitment_dt,'dd-Mon-yyyy') as kitFitmenton,to_char(v.hydro_test_dt,'dd-Mon-yyyy') as kitHydroTeston,v.cyl_srno,v.approval_no,to_char(v.approval_dt,'dd-Mon-yyyy') as kitApproveon,x.f_axle_descp, x.r_axle_descp, x.o_axle_descp, x.t_axle_descp, x.f_axle_weight, x.r_axle_weight, x.o_axle_weight, x.t_axle_weight"
                    + " ,y.sg_no,to_char(y.sg_fitted_on,'dd-MON-yyyy')as sg_fitted_on,y.sg_fitted_at,vhachndata.changed_data as adminRemark,sdt.link_regn_no,sdt1.regn_no as linkRegnNo,p.region_covered,purmast.descr as permit_pur_cd_desc,p.rcpt_no as permit_rcpt_no,tmoff.off_name as permitOfficeName,vttaxbasedon.regn_no as taxbasedRegnNo,"
                    + " to_char(puc.pucc_from,'dd-Mon-yyyy') as pucc_from, to_char(puc.pucc_upto,'dd-Mon-yyyy') as pucc_upto"
                    + " from vv_owner a \n"
                    + " LEFT  JOIN vp_appl_rcpt_mapping map on map.appl_no= ? "
                    + " LEFT  JOIN va_details det ON det.appl_no = map.appl_no "
                    + " LEFT  JOIN vt_fee d on d.rcpt_no=map.rcpt_no  and d.pur_cd in (13,73) and d.state_cd= map.state_cd and d.off_cd = map.off_cd "
                    + " LEFT  JOIN vv_insurance ins on ins.regn_no = a.regn_no and ins.state_cd = a.state_cd and ins.off_cd = a.off_cd  "
                    + " LEFT  JOIN vt_noc noc ON noc.regn_no = a.regn_no  and noc.state_cd=a.state_cd and noc.off_cd=a.off_cd "
                    + " LEFT JOIN tm_state e ON e.state_code = noc.state_to "
                    + " LEFT  JOIN vt_blacklist vtblk on vtblk.regn_no = a.regn_no "
                    + " LEFT  JOIN vm_blacklist vmblk on vmblk.code = vtblk.complain_type "
                    + " LEFT JOIN tm_office f ON f.off_cd = noc.off_to AND f.state_cd = noc.state_to \n"
                    + " LEFT  JOIN vt_owner_identification m ON m.regn_no = a.regn_no  and m.state_cd = a.state_cd and m.off_cd = a.off_cd \n"
                    + " LEFT JOIN (SELECT * FROM vt_tax WHERE regn_no = ? and pur_cd = 58 and left(tax_mode, 1) IN ('H','L', 'O', 'S', 'Y', 'Q', 'M') ORDER BY rcpt_dt DESC LIMIT 1) h ON h.regn_no = a.regn_no \n"
                    + " LEFT  JOIN PERMIT.VT_PERMIT p ON p.regn_no =a.regn_no and p.state_cd=a.state_cd \n"
                    + " left join PERMIT.VM_SERVICE_TYPE sertp on sertp.code=p.service_type \n"
                    + " left join PERMIT.VM_PERMIT_TYPE  pmttp on pmttp.code = p.pmt_type \n"
                    + " left join PERMIT.VM_PERMIT_CATG  pmtct on pmtct.code = p.pmt_catg and pmtct.state_cd = p.state_cd \n"
                    + " left join PERMIT.VT_PERMIT_HOME_AUTH  paut on paut.regn_no = p.regn_no and paut.pmt_no=p.pmt_no \n"
                    + " LEFT JOIN tm_office tmoff ON tmoff.off_cd = p.off_cd AND tmoff.state_cd = p.state_cd \n"
                    + "	LEFT JOIN (select * from vh_to where regn_no = ? and state_cd = ? and off_cd = ? order by moved_on desc limit 1) g ON g.regn_no = a.regn_no and g.state_cd = a.state_cd and g.off_cd = a.off_cd \n"
                    + " LEFT OUTER JOIN vt_other_stat_veh k ON k.new_regn_no = a.regn_no and  k.state_cd = a.state_cd and k.off_cd = a.off_cd \n"
                    + " LEFT OUTER JOIN tm_state o ON o.state_code=k.old_state_cd \n"
                    + " LEFT OUTER JOIN (select * from vh_conversion where new_regn_no = ? and state_cd = ? and off_cd = ? order by moved_on desc limit 1) n ON n.new_regn_no = a.regn_no and n.state_cd = a.state_cd and n.off_cd = a.off_cd \n"
                    + " LEFT OUTER JOIN vm_norms q ON q.code=a.norms \n"
                    + " LEFT JOIN tm_configuration r ON r.state_cd = a.state_cd \n"
                    + " LEFT OUTER JOIN (select * from vh_hpt where regn_no = ? and state_cd = ? and off_cd = ? order by moved_on desc limit 1) s ON s.regn_no = a.regn_no and s.state_cd = a.state_cd and s.off_cd = a.off_cd \n"
                    + " LEFT JOIN vt_rc_surrender t ON t.regn_no = a.regn_no and t.state_cd = a.state_cd and t.off_cd = a.off_cd \n"
                    + " LEFT JOIN vt_rc_cancel u ON u.regn_no = a.regn_no \n"
                    + " LEFT OUTER JOIN vt_retrofitting_dtls v ON v.regn_no = a.regn_no and v.state_cd = a.state_cd and v.off_cd = a.off_cd "
                    + " LEFT OUTER JOIN tm_district w ON w.dist_cd=s.fncr_district \n"
                    + " LEFT OUTER JOIN vt_axle x ON x.regn_no = a.regn_no and x.state_cd = a.state_cd and x.off_cd = a.off_cd "
                    + " LEFT OUTER JOIN vt_speed_governor y ON y.regn_no = a.regn_no and y.state_cd = a.state_cd and y.off_cd = a.off_cd "
                    + " LEFT OUTER JOIN (select * from vha_changed_data where appl_no = ? and state_cd = ? and off_cd = ? order by op_dt desc limit 1) vhachndata ON vhachndata.state_cd = a.state_cd and vhachndata.off_cd = a.off_cd"
                    + " LEFT OUTER JOIN vt_side_trailer sdt ON sdt.regn_no = a.regn_no and sdt.state_cd = a.state_cd and sdt.off_cd = a.off_cd"
                    + " LEFT OUTER JOIN vt_side_trailer sdt1 ON sdt1.link_regn_no = a.regn_no and sdt1.state_cd = a.state_cd and sdt1.off_cd = a.off_cd"
                    + " LEFT OUTER JOIN tm_purpose_mast purmast ON purmast.pur_cd = p.pur_cd\n"
                    + " LEFT OUTER JOIN (SELECT * FROM vt_tax_based_on WHERE regn_no =? and state_cd =? and pmt_type in(101,102,103,104)ORDER BY op_dt DESC LIMIT 1) vttaxbasedon ON vttaxbasedon.state_cd=h.state_cd and vttaxbasedon.regn_no=h.regn_no \n"
                    + " LEFT OUTER JOIN vt_pucc puc on a.regn_no = puc.regn_no and a.state_cd = puc.state_cd and a.off_cd = puc.off_cd"
                    + " where a.regn_no= ? and a.state_cd = ? and a.off_cd = ?";
            tmgr = new TransactionManager("getVehicleParticularDobj-1");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(i++, appl_no);
            ps.setString(i++, regn_no);
            ps.setString(i++, regn_no);
            ps.setString(i++, sessionVariables.getStateCodeSelected());
            ps.setInt(i++, sessionVariables.getOffCodeSelected());
            ps.setString(i++, regn_no);
            ps.setString(i++, sessionVariables.getStateCodeSelected());
            ps.setInt(i++, sessionVariables.getOffCodeSelected());
            ps.setString(i++, regn_no);
            ps.setString(i++, sessionVariables.getStateCodeSelected());
            ps.setInt(i++, sessionVariables.getOffCodeSelected());
            ps.setString(i++, regn_no);
            ps.setString(i++, sessionVariables.getStateCodeSelected());
            ps.setInt(i++, sessionVariables.getOffCodeSelected());
            ps.setString(i++, regn_no);
            ps.setString(i++, sessionVariables.getStateCodeSelected());
            ps.setString(i++, regn_no);
            ps.setString(i++, sessionVariables.getStateCodeSelected());
            ps.setInt(i++, sessionVariables.getOffCodeSelected());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new VehicleParticularDobj();
                dobj.setTransfer_on(rs.getString("transfer_on"));
                dobj.setPreOwner(rs.getString("previous_owner_name"));
                dobj.setEntryDate(rs.getString("entry_date"));
                if (rs.getString("othreStateOldRegnNo") != null && !"".contains(rs.getString("othreStateOldRegnNo"))) {
                    dobj.setPreRegno(rs.getString("othreStateOldRegnNo"));
                } else if (rs.getString("ConvOldRegnNo") != null && !"".contains(rs.getString("ConvOldRegnNo"))) {
                    dobj.setPreRegno(rs.getString("ConvOldRegnNo"));
                }
                dobj.setOldState(rs.getString("old_state"));
                dobj.setConversionDate(rs.getString("conversion_dt"));
                dobj.setLast_ca_on(rs.getString("last_ca_on"));
                dobj.setLast_alt_on(rs.getString("last_alt_on"));
                dobj.setRegn_no(rs.getString("regn_no"));
                if (rs.getDate("regn_dt") != null && !rs.getDate("regn_dt").equals("")) {;
                    dobj.setRegn_dt(format.format(rs.getDate("regn_dt")));
                }
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setFather_name(rs.getString("f_name"));
                dobj.setOwner_serial_no(rs.getString("owner_sr"));
                dobj.setVhclass_descr(rs.getString("vh_class_desc"));
                dobj.setOwner_dtls(rs.getString("owner_dtls"));
                dobj.setVehicle_maker(rs.getString("maker_name"));
                dobj.setVehicle_model(rs.getString("model_name"));
                dobj.setBody(rs.getString("body_type"));
                dobj.setManu_year(rs.getString("manu_mon") + "/" + rs.getString("manu_yr"));
                dobj.setNo_of_cylinder(rs.getString("no_cyl"));
                dobj.setChassis_no(rs.getString("chasi_no"));
                dobj.setH_power(rs.getString("hp"));
                dobj.setEngine_no(rs.getString("eng_no"));
                dobj.setSeat(rs.getString("seat_cap"));
                dobj.setStand_cap(rs.getString("stand_cap"));
                dobj.setSleep_cap(rs.getString("sleeper_cap"));
                dobj.setUnladen(rs.getString("unld_wt"));
                dobj.setLaden_wt(rs.getString("ld_wt"));
                if (rs.getString("fit_upto") != null && !rs.getString("fit_upto").equals("")) {
                    dobj.setFit_upto(rs.getString("fit_upto"));
                    dateFit_upto = format.parse(rs.getString("fit_upto"));
                }
                if (rs.getString("regn_upto") != null && !rs.getString("regn_upto").equals("")) {
                    dobj.setVal_upto(rs.getString("regn_upto"));
                    dateRegn_upto = format.parse(rs.getString("regn_upto"));
                }
                if (dateFit_upto != null && dateRegn_upto != null) {
                    if (dateFit_upto.after(dateRegn_upto)) {
                        dobj.setVal_upto(rs.getString("fit_upto"));
                    }
                }
                dobj.setCubic_capacity(rs.getString("cubic_cap"));
                taxPaid = ServerUtil.taxPaidInfo(false, regn_no, 58);
                if (!taxPaid.isEmpty()) {
                    for (Map.Entry<String, String> entry : taxPaid.entrySet()) {
                        if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("tax_mode") && entry.getValue() != null) {
                            if (!entry.getValue().isEmpty() && "L,O".contains(entry.getValue())) {
                                dobj.setTax_upto("One Time");
                                break;
                            }
                        } else if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("tax_upto") && entry.getValue() != null) {
                            taxPaidUpto = DateUtils.parseDate(entry.getValue());
                            dobj.setTax_upto(format.format(taxPaidUpto));
                            break;
                        }
                    }
                }
                if (rs.getInt("tax_amt") > 0) {
                    dobj.setTax_amt(rs.getInt("tax_amt"));
                }
                dobj.setColor(rs.getString("color"));
                dobj.setFuel(rs.getString("fuel_descr"));
                dobj.setPolicy_no(rs.getString("policy_no"));
                if (rs.getDate("ins_from") != null && !rs.getDate("ins_from").equals("")) {
                    dobj.setIns_from(format.format(rs.getDate("ins_from")));
                }
                if (rs.getDate("ins_upto") != null && !rs.getDate("ins_upto").equals("")) {
                    dobj.setIns_upto(format.format(rs.getDate("ins_upto")));
                }
                dobj.setIns_company(rs.getString("ins_company_name"));
                dobj.setIns_type_desc(rs.getString("ins_type_descr"));
                dobj.setMobile(rs.getString("mobile_no"));
                dobj.setEmail(rs.getString("email_id"));
                sql = "select (hp_type_descr || '-' || FNCR_NAME || ', ' ||  fncr_add1 || fncr_add2 || fncr_add3 || ',' || fncr_district_name || '-' || COALESCE(fncr_pincode::varchar, ''))  AS fncr_dtls \n"
                        + " from " + TableList.VV_HYPTH + " where regn_no = ? and state_cd = ? and off_cd = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regn_no);
                ps.setString(2, sessionVariables.getStateCodeSelected());
                ps.setInt(3, sessionVariables.getOffCodeSelected());
                RowSet rsList = tmgr.fetchDetachedRowSet();
                List<VehicleParticularDobj> list = new ArrayList<VehicleParticularDobj>();
                int count = 0;
                while (rsList.next()) {
                    hptSubListDobj = new VehicleParticularDobj();
                    if (rsList.getString("fncr_dtls") != null && !"".contains(rsList.getString("fncr_dtls"))) {
                        count++;
                        dobj.setIsHPDtls(true);
                        if (count > 1) {
                            hptSubListDobj.setFncr_dtls(count + ". " + rsList.getString("fncr_dtls"));
                        } else {
                            hptSubListDobj.setFncr_dtls(count + ". " + rsList.getString("fncr_dtls"));
                        }
                        list.add(hptSubListDobj);
                    }
                }
                dobj.setHptSubList(list);
                if (Integer.parseInt(rs.getString("fee")) != 0 && !rs.getString("fee").equals("")) {
                    dobj.setFee(rs.getString("fee"));
                } else {
                    dobj.setPurpose("(for internal use)");
                }
                if (rs.getDate("rcpt_dt") != null && !rs.getDate("rcpt_dt").equals("")) {
                    dobj.setRcpt_dt(format.format(rs.getDate("rcpt_dt")));
                }
                if (rs.getString("rcpt_no") != null && !rs.getString("rcpt_no").equals("")) {
                    dobj.setRcpt_no(rs.getString("rcpt_no"));
                }

                dobj.setOff_name(rs.getString("off_name"));
                dobj.setState_name(rs.getString("state_name"));
                dobj.setHeader(rs.getString("rcpt_heading"));
                dobj.setSubHeader(rs.getString("rcpt_subheading"));
                if (rs.getString("noc_no") != null && !rs.getString("noc_no").equals("")) {
                    dobj.setNocNo("No: " + rs.getString("noc_no"));
                    dobj.setStatetoname(" for: " + rs.getString("statetoname"));
                    dobj.setOfftoname("," + rs.getString("offtoname"));
                    dobj.setIsNOCDtls(true);
                } else {
                    dobj.setIsNOCDtls(false);
                }
                if (rs.getDate("noc_dt") != null && !rs.getDate("noc_dt").equals("")) {
                    dobj.setNocDt("issued on: " + format.format(rs.getDate("noc_dt")));
                }
                if (rs.getString("sg_no") != null && !rs.getString("sg_no").equals("")) {
                    dobj.setIsspeed_governor(true);
                    dobj.setSpeed_governor_no(rs.getString("sg_no"));
                } else {
                    dobj.setIsspeed_governor(false);
                }
                if (rs.getString("sg_fitted_on") != null && !rs.getString("sg_fitted_on").equals("")) {
                    dobj.setSg_fittedDate(rs.getString("sg_fitted_on"));
                }
                if (rs.getString("sg_fitted_at") != null && !rs.getString("sg_fitted_at").equals("")) {
                    dobj.setSg_manuName(rs.getString("sg_fitted_at"));
                }
                if (rs.getString("comp_type") != null && !rs.getString("comp_type").equals("")) {
                    dobj.setComp_type("Complain Type: " + rs.getString("comp_type"));
                    dobj.setComplain(", Complain: " + rs.getString("complain"));
                }
                if (rs.getString("fir_no") != null && !rs.getString("fir_no").equals("")) {
                    dobj.setFir_no(", FIR No: " + rs.getString("fir_no"));
                }

                if (rs.getString("fir_dt") != null && !rs.getString("fir_dt").equals("")) {
                    dobj.setFir_dt(", FIR Date: " + rs.getString("fir_dt"));
                }
                if (rs.getString("comp_dt") != null && !rs.getString("comp_dt").equals("")) {
                    dobj.setComp_dt(", Complain Date: " + rs.getString("comp_dt"));
                    dobj.setIsBlkDtls(true);
                } else {
                    dobj.setIsBlkDtls(false);
                }

                dobj.setPrinted_on(rs.getString("printed_on"));
                dobj.setAppl_no(appl_no);
                if (rs.getString("pmt_no") != null && !rs.getString("pmt_no").equals("")) {
                    dobj.setPassPmtDobj(new PassengerPermitDetailDobj());
                    dobj.getPassPmtDobj().setPmt_no(rs.getString("pmt_no"));
                    dobj.getPassPmtDobj().setApplNo(rs.getString("appl_no"));
                    dobj.getPassPmtDobj().setValidFromInString(rs.getString("valid_from"));
                    dobj.getPassPmtDobj().setValidUptoInString(rs.getString("valid_upto"));
                    dobj.getPassPmtDobj().setServices_TYPE(rs.getString("serviceType"));
                    dobj.getPassPmtDobj().setPmtCatg(rs.getString("permitCatg"));
                    dobj.getPassPmtDobj().setPmt_type(rs.getString("permitType"));
                    dobj.getPassPmtDobj().setState_cd(rs.getString("state_cd"));
                    dobj.getPassPmtDobj().setOff_cd(rs.getString("off_cd"));
                    dobj.getPassPmtDobj().setPermitOfficeName(rs.getString("permitOfficeName"));
                    dobj.setVehicleType(true);
                    dobj.setPermitFeeDtls(rs.getString("permit_pur_cd_desc") + " Fee Rs. ");
                    if (rs.getString("region_covered") != null && !rs.getString("region_covered").isEmpty()) {
                        getPermitAreaDetails(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), rs.getString("region_covered"), dobj);
                    }
                    getPermitFeeDetails(sessionVariables.getStateCodeSelected(), rs.getString("permit_rcpt_no"), dobj);
                } else {
                    if (rs.getString("taxbasedRegnNo") != null && !rs.getString("taxbasedRegnNo").isEmpty()) {
                        dobj.setVehicleType(true);
                    } else {
                        dobj.setVehicleType(false);
                    }
                    dobj.setAreaActionSource(null);
                }
                if (rs.getString("auth_no") != null && !rs.getString("auth_no").equals("")) {
                    dobj.setAuthdobj(new PermitHomeAuthDobj());
                    dobj.getAuthdobj().setAuthNo(rs.getString("auth_no"));
                    dobj.getAuthdobj().setAuthFromInString(rs.getString("auth_fr"));
                    dobj.getAuthdobj().setAuthUptoInString(rs.getString("auth_to"));
                }
                dobj.setVchNorms(rs.getString("vehNorms"));
                dobj.setVchStatus(rs.getString("status"));
                dobj.setFloorArea(rs.getString("floor_area"));
                dobj.setWheelBase(rs.getString("wheelbase"));

                dobj.setRc_cancel_dtls(rs.getString("rc_cancel_dtld"));
                dobj.setRc_surr_dtls(rs.getString("rc_surr_dtls"));
                dobj.setHpt_dtls(rs.getString("hpt_dtls"));
                dobj.setKitManufactureDt(rs.getString("kitFitmenton"));
                dobj.setKitManufactureName(rs.getString("kit_manuf"));
                dobj.setKitHydroDt(rs.getString("kitHydroTeston"));
                dobj.setKitDesc(rs.getString("kit_type"));
                dobj.setKitWorkName(rs.getString("workshop"));
                dobj.setTclDt(rs.getString("kitApproveon"));
                dobj.setTclNo(rs.getString("approval_no"));
                dobj.setKitNo(rs.getString("kit_srno"));
                dobj.setCylNo(rs.getString("cyl_srno"));
                dobj.setFdesc(rs.getString("f_axle_descp"));
                dobj.setRdesc(rs.getString("r_axle_descp"));
                dobj.setOdesc(rs.getString("o_axle_descp"));
                dobj.setTdesc(rs.getString("t_axle_descp"));
                dobj.setFweight(rs.getString("f_axle_weight"));
                dobj.setRweight(rs.getString("r_axle_weight"));
                dobj.setOweight(rs.getString("o_axle_weight"));
                dobj.setTweight(rs.getString("t_axle_weight"));
                if (ServerUtil.isTransport(rs.getInt("vh_class"), null)) {
                    dobj.setIsTransport(true);
                } else {
                    dobj.setIsTransport(false);
                }
                if (rs.getString("adminRemark") != null && !"".contains(rs.getString("adminRemark"))) {
                    String adminRemark = null;
                    String wordToFind = "AdminRemarks-";
                    Pattern word = Pattern.compile(wordToFind);
                    Matcher match = word.matcher(rs.getString("adminRemark"));
                    int startIndex = 0;
                    while (match.find()) {
                        startIndex = (match.end());
                    }
                    adminRemark = rs.getString("adminRemark").substring(startIndex, rs.getString("adminRemark").lastIndexOf("]"));
                    if (adminRemark != null) {
                        dobj.setVhaChangedDataAdminRemark(adminRemark);
                        dobj.setIsAdminRemark(true);
                    } else {
                        dobj.setIsAdminRemark(false);
                    }
                } else {
                    dobj.setIsAdminRemark(false);
                }
                if (rs.getString("kit_srno") != null && !"".contains(rs.getString("kit_srno"))) {
                    dobj.setIsLPGDtls(true);
                } else {
                    dobj.setIsLPGDtls(false);
                }

                if (rs.getString("policy_no") != null && !"".contains(rs.getString("policy_no"))) {
                    dobj.setIsInsDtls(true);
                } else {
                    dobj.setIsInsDtls(false);
                }
                if (rs.getString("hpt_dtls") != null && !"".contains(rs.getString("hpt_dtls"))) {
                    dobj.setIsHPTDtls(true);
                } else {
                    dobj.setIsHPTDtls(false);
                }
                if (rs.getString("rc_surr_dtls") != null && !"".contains(rs.getString("rc_surr_dtls"))) {
                    dobj.setIsRCSurrDtls(true);
                } else {
                    dobj.setIsRCSurrDtls(false);
                }
                if (rs.getString("rc_cancel_dtld") != null && !"".contains(rs.getString("rc_cancel_dtld"))) {
                    dobj.setIsRCCancelDtls(true);
                } else {
                    dobj.setIsRCCancelDtls(false);
                }
                if (rs.getString("link_regn_no") != null && !"".contains(rs.getString("link_regn_no"))) {
                    dobj.setLinkVehNumber(rs.getString("link_regn_no"));
                }
                if (rs.getString("linkRegnNo") != null && !"".contains(rs.getString("linkRegnNo"))) {
                    dobj.setLinkVehNumber(rs.getString("linkRegnNo"));
                }
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
                    if (configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan()) {
                        VmRoadSafetySloganPrintDobj vmrssdobj = getStateRoadSafetySlogan();
                        if (vmrssdobj != null) {
                            dobj.setShowRoadSafetySlogan(configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan());
                            dobj.setRoadSafetySloganDobj(vmrssdobj);
                        }
                    }
                }
                if (rs.getString("pucc_from") != null && !rs.getString("pucc_from").equals("")) {
                    dobj.setPucc_from(rs.getString("pucc_from"));
                }
                if (rs.getString("pucc_upto") != null && !rs.getString("pucc_upto").equals("")) {
                    dobj.setPucc_upto(rs.getString("pucc_upto"));
                }
            }
            if (dobj != null) {
                tmgr = new TransactionManager("getVehicleParticularDobj-2");
                if ((sessionVariables.getSelectedWork().getPur_cd() == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS) || (sessionVariables.getSelectedWork().getPur_cd() == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE) || (sessionVariables.getSelectedWork().getPur_cd() == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_COMPANY) || (sessionVariables.getSelectedWork().getPur_cd() == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_OWNER)) {
                    printSucessAndApproveStatus(tmgr, sessionVariables);
                    printSucessAndMoveInHistory(sessionVariables, appl_no, dobj, tmgr);
                } else {
                    printSucessAndMoveTodayPrintedInHistory(sessionVariables, appl_no, dobj, tmgr);
                }
                tmgr.commit();
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting Vehicle Particulars, please try after sometime or contact Administrator.");
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

    public static void getPermitAreaDetails(String state_cd, int off_cd, String region_covered, VehicleParticularDobj dobj) {
        String region_cd_withoutcommas = null;
        dobj.getAreaActionSource().clear();
        PassengerPermitDetailImpl passImp = new PassengerPermitDetailImpl();
        region_cd_withoutcommas = ',' == region_covered.charAt(region_covered.length() - 1) ? region_covered.substring(0, region_covered.lastIndexOf(',')) : region_covered;
        Map<String, String> mapRouteList = passImp.getTargetAreaMapWithStrBuil(region_cd_withoutcommas, state_cd, off_cd);
        for (Map.Entry<String, String> entry : mapRouteList.entrySet()) {
            dobj.getAreaActionSource().add(new PermitRouteList(entry.getKey(), entry.getValue()));
        }
    }

    public static void getPermitFeeDetails(String state_cd, String rcpt_no, VehicleParticularDobj dobj) {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("detailRegn_no");
            String query;
            query = "select sum(fees+fine) as amount,to_char(rcpt_dt,'dd-Mon-yyyy') as rcpt_dt from " + TableList.VT_FEE + " where state_cd= ? and regn_no=? and rcpt_no=? group by 2";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, state_cd);
            ps.setString(2, dobj.getRegn_no().toUpperCase());
            ps.setString(3, rcpt_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                dobj.setPermitFeeDtls(dobj.getPermitFeeDtls() + rs.getString("amount") + "/- paid vide receipt no " + rcpt_no + " dated " + rs.getString("rcpt_dt"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

    }

    public static OwnerDisclaimerReportDobj getOwnerDisclaimerDobj(String regn_no, String appl_no) throws VahanException, ParseException {
        TransactionManager tmgr = null;
        OwnerDisclaimerReportDobj dobj = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        TmConfigurationDobj configurationDobj = Util.getTmConfiguration();
        try {
            String sql = " SELECT a.state_cd,a.appl_no, upper(a.state_name)as state_name , a.off_name, case when upper(a.regn_no) = 'NEW' then 'REGN NO NOT ASSIGN' else a.regn_no end as regn_no ,case when upper(k.regn_no) IS NULL then 'FANCY NO NOT ASSIGN' else k.regn_no end as advance_regn_no, case when upper(l.old_regn_no) IS NULL then 'RETEN NO NOT ASSIGN' else l.old_regn_no end as reten_regn_no , a.regn_dt, a.purchase_dt,case when tax_mode='E' then 'EXEMPTED' else 'NOT EXEMPTED' end as isexem, "
                    + " a.owner_sr, a.owner_name, a.f_name, upper(a.c_add1 || ', ' || a.c_add2 || ', ' || a.c_add3 || ',' || chr(10) || a.c_district_name || '-' || a.c_state_name || '-' || a.c_pincode) as c_full_add,"
                    + " upper(a.p_add1 || ', ' || a.p_add2 || ', ' || a.p_add3 || ',' || chr(10) ||  a.p_district_name || ', ' || a.p_state_name || '-' || a.p_pincode) as p_full_add,"
                    + " a.owner_cd,g.descr as ow_descr,case when owner_cd in (4,5) then 'GOVT' else 'PRIVATE' end as ISGOV , j.descr as regn_type,a.vh_class, upper(a.vh_class_desc)  as vh_class_desc, a.chasi_no, a.eng_no, a.maker, a.maker_name as maker_name, a.model_name as model_name, a.body_type, a.no_cyl, a.hp,"
                    + " a.seat_cap, a.stand_cap, a.sleeper_cap, a.unld_wt, a.ld_wt, a.gcw, a.fuel, a.fuel_descr, a.color, a.manu_mon, a.manu_yr, a.norms_descr, upper(a.norms_descr) as norms_descr, a.wheelbase,"
                    + " a.cubic_cap, a.floor_area, a.ac_fitted, a.audio_fitted, a.video_fitted, a.vch_purchase_as, a.vch_catg, a.dealer_cd, case when a.dlr_name <> '' then upper(a.dlr_name || ', ' || a.dlr_add1 || ', ' || a.dlr_add2 || ', ' ||"
                    + " a.dlr_add3 || ', ' || a.dlr_city || ',' || chr(10) || a.dlr_district || ' ' || a.dlr_pincode) else '' end as dlr_full_add , a.sale_amt, upper(a.garage_add) as garage_add, a.length, a.width, a.height, a.regn_upto, a.fit_upto, a.annual_income,"
                    + " a.imported_vch, b.f_axle_descp, b.r_axle_descp, b.o_axle_descp, b.t_axle_descp, b.f_axle_weight, b.r_axle_weight, b.o_axle_weight, b.t_axle_weight,"
                    + " c.chasi_no as tr_chasi_no, c.body_type as tr_body_type, c.ld_wt as tr_ld_wt, c.unld_wt as tr_unld_wt, c.f_axle_descp as tr_f_axle_descp, c.r_axle_descp as tr_r_axle_descp, c.o_axle_descp as tr_o_axle_descp, c.t_axle_descp as tr_t_axle_descp,  c.f_axle_weight as tr_f_axle_weight, c.r_axle_weight as tr_r_axle_weight, c.o_axle_weight as tr_o_axle_weight, c.t_axle_weight as tr_t_axle_weight,"
                    + " date(f.from_dt) as hypth_from_dt, (select distinct max(sr_no) from vv_hypth where regn_no=f.regn_no) as sr_no,(f.fncr_name || ', ' || f.fncr_add1 || ', ' || f.fncr_add2 || ', ' || f.fncr_add3 || ', ' || chr(10) ||  f.fncr_district_name || ', ' || f.fncr_state_name || ', ' || f.fncr_pincode) as fncr_full_add, to_char(h.tax_from,'dd-Mon-yyyy') as tax_from , h.tax_amt , h.rcpt_no , "
                    + " case when h.tax_mode IN ('L', 'O') then 'One Time' else to_char(tax_upto,'dd-Mon-yyyy') end as tax_upto,"
                    + " to_char(a.op_dt,'dd-Mon-yyyy')as op_dt, e.ins_type_descr as ins_type, e.ins_company_name as company_name, e.policy_no as policy_no,e.ins_from as ins_from, e.ins_upto as ins_upto,d.kit_srno,d.kit_type,d.kit_manuf,d.workshop,d.fitment_dt,d.hydro_test_dt,d.cyl_srno,d.approval_no,d.approval_dt,a.pmt_type,a.pmt_catg,pt.descr as pmt_type_desc,pc.descr as pmt_catg_desc , o.pan_no ,o.aadhar_no ,o.voter_id ,o.passport_no,p.rcpt_heading,p.rcpt_subheading, bs.reason "
                    + " FROM " + TableList.VIEW_VVA_OWNER + " a"
                    + " LEFT OUTER JOIN va_axle b ON b.appl_no = a.appl_no"
                    + " LEFT OUTER JOIN va_trailer c ON c.appl_no = a.appl_no"
                    + " LEFT OUTER JOIN va_retrofitting_dtls d ON d.appl_no = a.appl_no"
                    + " LEFT OUTER JOIN vva_insurance e ON e.appl_no = a.appl_no"
                    + " LEFT OUTER JOIN vva_hpa f ON f.appl_no = a.appl_no and f.sr_no=1"
                    + " LEFT OUTER JOIN vm_owcode g ON g.ow_code=a.owner_cd"
                    + " LEFT OUTER JOIN vm_regn_type j ON j.regn_typecode = a.regn_type"
                    + " LEFT OUTER JOIN (SELECT * FROM vt_tax WHERE regn_no = ? and vt_tax.state_cd = ? and vt_tax.off_cd=? and pur_cd = 58 and left(tax_mode, 1) IN ('H','L', 'O', 'S', 'Y', 'Q', 'M') ORDER BY rcpt_dt DESC LIMIT 1) h ON h.regn_no = a.regn_no "
                    + " LEFT OUTER JOIN vt_advance_regn_no k ON a.appl_no = k.regn_appl_no "
                    + " LEFT OUTER JOIN vt_surrender_retention l ON l.regn_appl_no=a.appl_no "
                    + " LEFT OUTER JOIN permit.vm_permit_type pt ON pt.code=a.pmt_type "
                    + " LEFT OUTER JOIN permit.vm_permit_catg pc ON pc.state_cd=a.state_cd and pc.code=a.pmt_catg "
                    + " LEFT OUTER JOIN va_owner_identification o ON o.appl_no = a.appl_no "
                    + " LEFT OUTER JOIN tm_configuration p ON p.state_cd = a.state_cd "
                    + " LEFT OUTER JOIN " + TableList.VT_BSIV_CHASSIS_ALLOWED_BY_SC_ORDER + " bs ON bs.chasi_no = a.chasi_no "
                    + " WHERE a.appl_no=?";

            tmgr = new TransactionManager("getOwnerDisclaimerDobj");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            ps.setString(4, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new OwnerDisclaimerReportDobj();
                dobj.setHeader(rs.getString("rcpt_heading"));
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
                    if (configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan()) {
                        VmRoadSafetySloganPrintDobj vmrssdobj = getStateRoadSafetySlogan();
                        if (vmrssdobj != null) {
                            dobj.setShowRoadSafetySlogan(configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan());
                            dobj.setRoadSafetySloganDobj(vmrssdobj);
                        }
                    }
                }
                dobj.setOffName(rs.getString("off_name"));
                dobj.setRegnNO(rs.getString("regn_no"));
                if (rs.getDate("regn_dt") != null && !rs.getDate("regn_dt").equals("")) {;
                    dobj.setRegnDT(format.format(rs.getDate("regn_dt")));
                }
                dobj.setApplNO(rs.getString("appl_no"));
                dobj.setStateCD(rs.getString("state_cd"));
                if (rs.getDate("purchase_dt") != null && !rs.getDate("purchase_dt").equals("")) {;
                    dobj.setPuechaseDT(format.format(rs.getDate("purchase_dt")));
                }
                dobj.setStateName(rs.getString("state_name"));
                dobj.setIsExem(rs.getString("isexem"));
                dobj.setOwnerSrNO(rs.getString("owner_sr"));
                dobj.setOwnerName(rs.getString("owner_name"));
                dobj.setFname(rs.getString("f_name"));
                dobj.setCurrAddress(rs.getString("c_full_add"));
                dobj.setPerAddress(rs.getString("p_full_add"));
                dobj.setOwnerCD(rs.getString("owner_cd"));
                dobj.setOwnerDesc(rs.getString("ow_descr"));
                dobj.setIsGOV(rs.getString("isgov"));
                dobj.setRegnType(rs.getString("regn_type"));
                dobj.setVchDescr(rs.getString("vh_class_desc"));
                dobj.setChassiNO(rs.getString("chasi_no"));
                dobj.setEngNO(rs.getString("eng_no"));
                dobj.setMaker(rs.getString("maker"));
                dobj.setMakerName(rs.getString("maker_name"));
                dobj.setModelName(rs.getString("model_name"));
                dobj.setBodyType(rs.getString("body_type"));
                dobj.setHorsepwr(rs.getString("hp"));
                dobj.setNoOfCyl(rs.getString("no_cyl"));
                dobj.setSeatCap(rs.getString("seat_cap"));
                dobj.setStandCap(rs.getString("stand_cap"));
                dobj.setSleepCap(rs.getString("sleeper_cap"));
                dobj.setUnLdWt(rs.getString("unld_wt"));
                dobj.setLdWt(rs.getString("ld_wt"));
                dobj.setGcw(rs.getString("gcw"));
                dobj.setFuelDesc(rs.getString("fuel_descr"));
                dobj.setFuel(rs.getString("fuel"));
                dobj.setColor(rs.getString("color"));
                dobj.setManuMnt(rs.getString("manu_mon"));
                dobj.setManuYrs(rs.getString("manu_yr"));
                dobj.setNormsDesc(rs.getString("norms_descr"));
                dobj.setWheelBase(rs.getString("wheelbase"));
                dobj.setCubCap(rs.getString("cubic_cap"));
                dobj.setFloorArea(rs.getString("floor_area"));
                dobj.setAcFitted(rs.getString("ac_fitted"));
                dobj.setAudioFitted(rs.getString("audio_fitted"));
                dobj.setVideoFitted(rs.getString("video_fitted"));
                dobj.setVhcPurchaseAs(rs.getString("vch_purchase_as"));
                dobj.setVchCatg(rs.getString("vch_catg"));
                dobj.setDealerAddress(rs.getString("dlr_full_add"));
                dobj.setSaleAmt(rs.getString("sale_amt"));
                dobj.setGarageAddress(rs.getString("garage_add"));
                dobj.setLength(rs.getString("length"));
                dobj.setWidth(rs.getString("width"));
                dobj.setHeight(rs.getString("height"));
                dobj.setKitManuf(rs.getString("kit_manuf"));
                dobj.setKitSrNo(rs.getString("kit_srno"));
                dobj.setKitType(rs.getString("kit_type"));
                dobj.setFitmentDate(rs.getString("fitment_dt"));
                dobj.setHydroTestDate(rs.getString("hydro_test_dt"));
                dobj.setApprovalNo(rs.getString("approval_no"));
                dobj.setApprovalDate(rs.getString("approval_dt"));
                dobj.setWorkShop(rs.getString("workshop"));
                dobj.setCylSrNo(rs.getString("cyl_srno"));
                if (rs.getDate("regn_upto") != null && !rs.getDate("regn_upto").equals("")) {
                    dobj.setRegnUpto(rs.getString("regn_upto"));
                }
                if (rs.getDate("fit_upto") != null && !rs.getDate("fit_upto").equals("")) {
                    dobj.setFitUpto(rs.getString("fit_upto"));
                }
                dobj.setAnnulIncome(rs.getString("annual_income"));
                dobj.setImpVch(rs.getString("imported_vch"));
                dobj.setFaxlDesc(rs.getString("f_axle_descp"));
                dobj.setRaxlDesc(rs.getString("r_axle_descp"));
                dobj.setOaxlDesc(rs.getString("o_axle_descp"));
                dobj.setTaxlDesc(rs.getString("t_axle_descp"));
                dobj.setFaxlWeight(rs.getString("f_axle_weight"));
                dobj.setRaxlWeight(rs.getString("r_axle_weight"));
                dobj.setOaxlWeight(rs.getString("o_axle_weight"));
                dobj.setTaxlWeight(rs.getString("t_axle_weight"));
                dobj.setTrChasiNO(rs.getString("tr_chasi_no"));
                if (!CommonUtils.isNullOrBlank(dobj.getTrChasiNO())) {
                    dobj.setRenderAttachedTrailerDetails(true);
                }
                dobj.setTrBodyType(rs.getString("tr_body_type"));
                dobj.setTrLdWt(rs.getString("tr_ld_wt"));
                dobj.setTrUnldWt(rs.getString("tr_unld_wt"));
                dobj.setTrFAxlDesc(rs.getString("tr_f_axle_descp"));
                dobj.setTrRAxlDesc(rs.getString("tr_r_axle_descp"));
                dobj.setTrOAxlDesc(rs.getString("tr_o_axle_descp"));
                dobj.setTrTAxlDesc(rs.getString("tr_t_axle_descp"));
                dobj.setTrFAxlWeight(rs.getString("tr_f_axle_weight"));
                dobj.setTrRAxlWeight(rs.getString("tr_r_axle_weight"));
                dobj.setTrOAxlWeight(rs.getString("tr_o_axle_weight"));
                dobj.setTrTAxlWeight(rs.getString("tr_t_axle_weight"));
                if (rs.getDate("hypth_from_dt") != null && !rs.getDate("hypth_from_dt").equals("")) {
                    dobj.setHypthFromDT(format.format(rs.getDate("hypth_from_dt")));
                }
                dobj.setSrNO(rs.getString("sr_no"));
                dobj.setFncrAddress(rs.getString("fncr_full_add"));
                if (rs.getString("tax_from") != null && !rs.getString("tax_from").equals("")) {
                    dobj.setTaxFromDT(rs.getString("tax_from"));
                }
                if (rs.getString("tax_upto") != null && !rs.getString("tax_upto").equals("")) {
                    dobj.setTaxUpto(rs.getString("tax_upto"));
                }
                dobj.setTaxAmt(rs.getString("tax_amt"));
                dobj.setRcptNO(rs.getString("rcpt_no"));
                if (rs.getString("op_dt") != null && !rs.getString("op_dt").equals("")) {
                    dobj.setOpDT(rs.getString("op_dt"));
                }
                dobj.setInsType(rs.getString("ins_type"));
                dobj.setInsCompany(rs.getString("company_name"));
                dobj.setPolicyNO(rs.getString("policy_no"));
                if (rs.getDate("ins_from") != null && !rs.getDate("ins_from").equals("")) {
                    dobj.setInsFrom(format.format(rs.getDate("ins_from")));
                }
                if (rs.getDate("ins_upto") != null && !rs.getDate("ins_upto").equals("")) {
                    dobj.setInsUpto(format.format(rs.getDate("ins_upto")));
                }
                if (ServerUtil.isTransport(rs.getInt("vh_class"), null)) {
                    dobj.setVehType("Transport");
                } else {
                    dobj.setVehType("Non-Transport");
                }
                if (rs.getDate("regn_dt") != null && !rs.getDate("regn_dt").equals("")) {;
                    dobj.setRegnDate(String.valueOf(rs.getDate("regn_dt")));
                }
                if (rs.getString("reten_regn_no") != null && !rs.getString("reten_regn_no").equals("")) {
                    dobj.setRetenRegnNo(rs.getString("reten_regn_no"));

                }
                if (rs.getString("advance_regn_no") != null && !rs.getString("advance_regn_no").equals("")) {
                    if (rs.getString("advance_regn_no").equals("FANCY NO NOT ASSIGN") && rs.getString("reten_regn_no").equals("RETEN NO NOT ASSIGN")) {
                        dobj.setIsAdvanceRegnNoAssign(true);
                    }
                    dobj.setAdvanceRegnNo(rs.getString("advance_regn_no"));
                }
                if (rs.getInt("pmt_type") != 0 && rs.getInt("pmt_type") > 0) {
                    dobj.setIspmt_type(true);
                }
                if (rs.getInt("pmt_catg") != 0 && rs.getInt("pmt_catg") > 0) {
                    dobj.setIspmt_catg(true);
                }
                if (rs.getString("pmt_type_desc") != null && !rs.getString("pmt_type_desc").equals("")) {
                    dobj.setPmt_type(rs.getString("pmt_type_desc"));
                }
                if (rs.getString("pmt_catg_desc") != null && !rs.getString("pmt_catg_desc").equals("")) {
                    dobj.setPmt_catg(rs.getString("pmt_catg_desc"));
                }
                if (rs.getString("pan_no") != null && !rs.getString("pan_no").equals("")) {
                    dobj.setPanNo(rs.getString("pan_no"));
                }
                if (rs.getString("aadhar_no") != null && !rs.getString("aadhar_no").equals("")) {
                    dobj.setAadharNo(rs.getString("aadhar_no"));
                }
                if (rs.getString("voter_id") != null && !rs.getString("voter_id").equals("")) {
                    dobj.setVoterId(rs.getString("voter_id"));
                }
                if (rs.getString("passport_no") != null && !rs.getString("passport_no").equals("")) {
                    dobj.setPassportNo(rs.getString("passport_no"));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("reason"))) {
                    dobj.setBsIVRegnReason(rs.getString("reason"));
                }
            }
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
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dobj;
    }

    public static NewRCReportDobj getNewRCReportDobj(String regn_no, String applno, boolean isRCDispatch, boolean isRCDispatchWithoutPostalFee, String rcRadiobtnValue, String paperrc, String state_cd, int off_cd) throws VahanException, ParseException {
        TransactionManagerReadOnly tmgr = null;
        NewRCReportDobj dobj = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        Date dateRegn_upto = null;
        Date dateFit_upto = null;
        Map<String, String> taxPaid = new HashMap<>();
        Date taxPaidUpto = null;
        boolean isRCPrint = true;
        PermitCheckDetailsDobj permitDobj = null;
        PermitCheckDetailsImpl permitImpl = null;
        TmConfigurationDobj configurationDobj = Util.getTmConfiguration();
        try {
            String sql = "SELECT a.state_cd, upper(a.state_name)as state_name , a.off_name, a.regn_no, to_char(a.regn_dt,'dd-Mon-yyyy') as regn_dt, to_char(a.purchase_dt,'dd-Mon-yyyy') as purchase_dt,"
                    + " a.owner_sr, a.owner_name, a.f_name, upper(a.c_add1 || ', ' || a.c_add2 || ', ' || a.c_add3 || ',' || chr(10) || a.c_district_name || '-' || a.c_state_name || '-' || a.c_pincode) as c_full_add,"
                    + " upper(a.p_add1 || ', ' || a.p_add2 || ', ' || a.p_add3 || ',' || chr(10) ||  a.p_district_name || ', ' || a.p_state_name || '-' || a.p_pincode) as p_full_add,"
                    + " a.owner_cd,g.descr as ow_descr,case when a.owner_cd in (4,5,15) then 'GOVT' else 'PRIVATE' end as ISGOV ,  a.regn_type,a.vh_class, upper(a.vh_class_desc)  as vh_class_desc, a.chasi_no, a.eng_no, a.maker, a.maker_name, a.model_name, a.body_type, a.no_cyl, a.hp,"
                    + " a.seat_cap, a.stand_cap, a.sleeper_cap, a.unld_wt, a.ld_wt, a.gcw, a.fuel, a.fuel_descr, a.color, a.manu_mon, a.manu_yr, a.norms_descr, upper(a.norms_descr) as norms_descr, a.wheelbase,"
                    + " a.cubic_cap, a.floor_area, case when a.ac_fitted = 'Y' then 'YES' else 'NO' end as ac_fitted, a.audio_fitted, a.video_fitted, a.vch_purchase_as, a.vch_catg, a.dealer_cd, upper(a.dlr_name || ', ' || a.dlr_add1 || ', ' || a.dlr_add2 || ', ' ||"
                    + " a.dlr_add3 || ', ' || a.dlr_city || ',' || chr(10) || a.dlr_district || '-' || a.dlr_pincode) as dlr_full_add , a.sale_amt, upper(a.garage_add) as garage_add, a.length, a.width, a.height, to_char(a.regn_upto,'dd-Mon-yyyy') as regn_upto, to_char(a.fit_upto,'dd-Mon-yyyy') as fit_upto, a.annual_income,"
                    + " a.imported_vch, b.f_axle_descp, b.r_axle_descp, b.o_axle_descp, b.t_axle_descp, b.f_axle_weight, b.r_axle_weight, b.o_axle_weight, b.t_axle_weight,"
                    + " c.chasi_no as tr_chasi_no, c.body_type as tr_body_type, c.ld_wt as tr_ld_wt, c.unld_wt as tr_unld_wt, c.f_axle_descp as tr_f_axle_descp, c.r_axle_descp as tr_r_axle_descp, c.o_axle_descp as tr_o_axle_descp, c.t_axle_descp as tr_t_axle_descp,  c.f_axle_weight as tr_f_axle_weight, c.r_axle_weight as tr_r_axle_weight, c.o_axle_weight as tr_o_axle_weight, c.t_axle_weight as tr_t_axle_weight,"
                    + " to_char(date(f.from_dt),'dd-Mon-yyyy') as hypth_from_dt, (select distinct max(sr_no) from vv_hypth  where regn_no=f.regn_no and state_cd=f.state_cd and off_cd=f.off_cd ) as sr_no,(f.fncr_name || ', ' || COALESCE(f.fncr_add1,'') || ', ' || COALESCE(f.fncr_add2,'') || ', ' || COALESCE(f.fncr_add3,'') || ', ' || chr(10) ||  COALESCE(f.fncr_district_name,'') || ', ' || COALESCE(f.fncr_state_name,'') || '-' || COALESCE(f.fncr_pincode,0)) as fncr_full_add,f.fncr_name,"
                    + " to_char(h.tax_from,'dd-Mon-yyyy') as tax_from , h.tax_amt , h.rcpt_no ,case when h.tax_mode IN ('L', 'O') then 'One Time' else to_char(tax_upto,'dd-Mon-yyyy') end as tax_upto,to_char(a.op_dt,'dd-Mon-yyyy')as op_dt,i.hsrp_no_front,i.hsrp_no_back,j.rcpt_heading,j.rcpt_subheading,to_char(current_timestamp,'dd-Mon-yyyy hh24:mi:ss') as printed_on,current_timestamp as printedon_sign,"
                    + " to_char(k.entry_dt,'dd-Mon-yyyy') as entry_date, k.old_regn_no,o.descr as old_state,"
                    + " to_char(m.transfer_dt ,'dd-Mon-yyyy') as transfer_dt,m.owner_name as previous_owner_name,"
                    + " to_char(n.moved_on,'dd-Mon-yyyy') as conversion_dt,oc.criteria_desc,q.descr as serviceType,d.cyl_srno,r.pur_cd,"
                    + " sdt.link_regn_no,j.tax_exemption,vaDtls.pur_cd AS vaDtls_purCD,pmtcatg.descr as pmtCatg,to_char(vaDtls.confirm_date,'dd-Mon-yyyy') AS transactionDate,usrsign.doc_sign as userSign \n"
                    + " FROM vv_owner a \n"
                    + " LEFT OUTER JOIN vt_axle b ON b.regn_no = a.regn_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd \n"
                    + " LEFT OUTER JOIN vt_trailer c ON c.regn_no = a.regn_no and c.state_cd = a.state_cd and c.off_cd = a.off_cd \n"
                    + " LEFT OUTER JOIN vt_retrofitting_dtls d ON d.regn_no = a.regn_no and d.state_cd = a.state_cd and d.off_cd = a.off_cd \n"
                    + " LEFT OUTER JOIN vv_insurance e ON e.regn_no = a.regn_no and e.state_cd = a.state_cd and e.off_cd = a.off_cd  \n"
                    + " LEFT OUTER JOIN tm_configuration j ON j.state_cd = a.state_cd \n"
                    + " LEFT OUTER JOIN (select * from vv_hypth where regn_no =? and vv_hypth.state_cd=? and vv_hypth.off_cd=? order by sr_no desc LIMIT 1 ) f ON f.regn_no = a.regn_no \n"
                    + " LEFT OUTER JOIN vm_owcode g ON g.ow_code=a.owner_cd \n"
                    + " LEFT OUTER JOIN (SELECT * FROM vt_tax WHERE regn_no =? and vt_tax.state_cd =? and pur_cd = 58 and left(tax_mode, 1) IN ('H','L', 'O', 'S', 'Y', 'Q', 'M') ORDER BY rcpt_dt DESC LIMIT 1) h ON h.regn_no=a.regn_no and tax_amt>0 \n"
                    + " LEFT OUTER JOIN hsrp.vt_hsrp i ON i.regn_no = a.regn_no and i.state_cd = a.state_cd and i.off_cd = a.off_cd  \n"
                    + " LEFT OUTER JOIN vt_other_stat_veh k ON k.new_regn_no = a.regn_no and  k.state_cd = a.state_cd and k.off_cd = a.off_cd \n"
                    + "	LEFT OUTER JOIN (select * from vh_to where regn_no = ? and state_cd = ? and off_cd = ? order by moved_on desc limit 1) m ON m.regn_no = a.regn_no and m.state_cd = a.state_cd and m.off_cd = a.off_cd \n"
                    + " LEFT OUTER JOIN (select * from vh_conversion where new_regn_no = ? and state_cd = ? and off_cd = ? order by moved_on desc limit 1) n ON n.new_regn_no = a.regn_no and n.state_cd = a.state_cd and n.off_cd = a.off_cd \n"
                    + " LEFT OUTER JOIN tm_state o ON o.state_code=k.old_state_cd \n"
                    + " LEFT OUTER JOIN vm_other_criteria oc ON oc.criteria_cd = a.other_criteria and oc.state_cd=a.state_cd \n"
                    + " LEFT OUTER JOIN vt_tax_based_on p ON p.state_cd=h.state_cd and p.off_cd=h.off_cd and p.rcpt_no=h.rcpt_no \n"
                    + " LEFT OUTER JOIN permit.vm_service_type q ON q.code=p.service_type \n"
                    + " LEFT OUTER JOIN (SELECT a.* FROM vt_fee a, vp_appl_rcpt_mapping b WHERE b.appl_no=? and b.state_cd=a.state_cd and b.off_cd=a.off_cd and b.rcpt_no=a.rcpt_no and a.pur_cd = 200 LIMIT 1) r ON r.regn_no=a.regn_no \n"
                    + " LEFT OUTER JOIN vt_side_trailer sdt ON sdt.regn_no = a.regn_no and sdt.state_cd = a.state_cd and sdt.off_cd = a.off_cd \n"
                    + " LEFT OUTER JOIN  (select * from va_details where appl_no = ?  order by confirm_date desc limit 1) vaDtls on vaDtls.regn_no=a.regn_no \n"
                    + " LEFT OUTER JOIN permit.vm_permit_catg pmtcatg ON pmtcatg.code=p.pmt_catg and pmtcatg.permit_type=p.pmt_type and pmtcatg.state_cd=p.state_cd \n"
                    + " LEFT OUTER JOIN (select * from vha_status  where appl_no = ? order by moved_on desc limit 1) vhastatus on  vhastatus.appl_no = vaDtls.appl_no and vhastatus.pur_cd = vaDtls.pur_cd \n"
                    + " LEFT OUTER JOIN " + TableList.TM_USER_SIGN + " usrsign ON usrsign.user_cd = vhastatus.emp_cd \n"
                    + " WHERE"
                    + " a.regn_no=? and a.state_cd=? and a.off_cd=? ";

            tmgr = new TransactionManagerReadOnly("getNewRCReportDobj");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            ps.setString(4, regn_no);
            ps.setString(5, state_cd);
            ps.setString(6, regn_no);
            ps.setString(7, state_cd);
            ps.setInt(8, off_cd);
            ps.setString(9, regn_no);
            ps.setString(10, state_cd);
            ps.setInt(11, off_cd);
            ps.setString(12, applno);
            ps.setString(13, applno);
            ps.setString(14, applno);
            ps.setString(15, regn_no);
            ps.setString(16, state_cd);
            ps.setInt(17, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                permitDobj = new PermitCheckDetailsDobj();
                permitImpl = new PermitCheckDetailsImpl();
                permitDobj = permitImpl.getLatestTaxDtls(permitDobj, regn_no);
                dobj = new NewRCReportDobj();
//                if (state_cd.equals("UK")) {
//                    dobj.setWatermark(true);
//                } else {
//                    dobj.setWatermark(false);
//                }
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
                    if (configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan()) {
                        VmRoadSafetySloganPrintDobj vmrssdobj = getStateRoadSafetySlogan();
                        if (vmrssdobj != null) {
                            dobj.setShowRoadSafetySlogan(configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan());
                            dobj.setRoadSafetySloganDobj(vmrssdobj);
                        }
                    }
                }
                dobj.setPrintedon(rs.getString("printed_on"));
                dobj.setPrintedSign(format.format(rs.getDate("printedon_sign")));
                dobj.setHeader(ServerUtil.getRcptHeading());
                dobj.setSubHeader(ServerUtil.getRcptSubHeading());
                dobj.setStateName(rs.getString("state_name"));
                dobj.setOffName(rs.getString("off_name"));
                dobj.setRegnNo(rs.getString("regn_no"));
                if (rs.getString("regn_dt") != null && !rs.getString("regn_dt").equals("")) {
                    dobj.setRegnDt(rs.getString("regn_dt"));
                }
                if (rs.getString("purchase_dt") != null && !rs.getString("purchase_dt").equals("")) {
                    dobj.setRegnDt(rs.getString("regn_dt"));
                }
                if (rs.getString("purchase_dt") != null && !rs.getString("purchase_dt").equals("")) {
                    dobj.setPurchaseDt(rs.getString("purchase_dt"));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("tax_exemption"))) {
                    OwnerImpl ownerImpl = new OwnerImpl();
                    Owner_dobj ownerDobj = ownerImpl.set_Owner_appl_db_to_dobj(regn_no.toUpperCase(), "", "", rs.getInt("vaDtls_purCD"));
                    VehicleParameters parameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
                    if (isCondition(replaceTagValues(rs.getString("tax_exemption"), parameters), "getNewRCReportDobj") && permitDobj != null && (CommonUtils.isNullOrBlank(permitDobj.getTaxUpto()) || permitDobj.getTaxPurCd().equalsIgnoreCase("TAX Exemp"))) {
                        dobj.setIsexempted("EXEMPTED");
                    } else {
                        dobj.setIsexempted("NOT EXEMPTED");
                    }
                } else {
                    dobj.setIsexempted("NOT EXEMPTED");
                }
                dobj.setOwnerName(rs.getString("owner_name"));
                dobj.setFatherName(rs.getString("f_name"));
                dobj.setCurrFullAddr(rs.getString("c_full_add"));
                dobj.setPerFullAddr(rs.getString("p_full_add"));
                dobj.setOwnership(rs.getString("ow_descr"));
                dobj.setGovVeh(rs.getString("isgov"));
                dobj.setRegnType(rs.getString("regn_type"));
                dobj.setVchDesc(rs.getString("vh_class_desc"));
                dobj.setChasiNo(rs.getString("chasi_no"));
                dobj.setEngNo(rs.getString("eng_no"));
                dobj.setMakerName(rs.getString("maker_name"));
                dobj.setModelName(rs.getString("model_name"));
                dobj.setBodyType(rs.getString("body_type"));
                dobj.setHpower(rs.getString("hp"));
                dobj.setNoofCyl(rs.getString("no_cyl"));
                dobj.setSeatCap(rs.getString("seat_cap"));
                dobj.setStandCap(rs.getString("stand_cap"));
                dobj.setSleepCap(rs.getString("sleeper_cap"));
                dobj.setUnldwt(rs.getString("unld_wt"));
                dobj.setLdwt(rs.getString("ld_wt"));
                dobj.setGcw(rs.getString("gcw"));
                if (rs.getInt("fuel") == 6) {
                    dobj.setIscng(true);
                    dobj.setKitCylno(rs.getString("cyl_srno"));
                }
                dobj.setFuelDesc(rs.getString("fuel_descr"));
                dobj.setColor(rs.getString("color"));
                if (Integer.parseInt(rs.getString("manu_mon")) != 10 && Integer.parseInt(rs.getString("manu_mon")) != 11 && Integer.parseInt(rs.getString("manu_mon")) != 12) {
                    dobj.setManuMnt("0" + rs.getString("manu_mon"));
                } else {
                    dobj.setManuMnt(rs.getString("manu_mon"));
                }
                dobj.setManuYr(rs.getString("manu_yr"));
                dobj.setNormDesc(rs.getString("norms_descr"));
                dobj.setWheelBase(rs.getString("wheelbase"));
                dobj.setCubicCap(rs.getString("cubic_cap"));
                dobj.setFloorArea(rs.getString("floor_area"));
                dobj.setAcfitted(rs.getString("ac_fitted"));
                dobj.setAudioFitted(rs.getString("audio_fitted"));
                dobj.setVideoFitted(rs.getString("video_fitted"));
                if (rs.getString("vch_purchase_as") != null && !rs.getString("vch_purchase_as").equals("")) {
                    if (rs.getString("vch_purchase_as").equals("B")) {
                        dobj.setVehPurchAS("Fully Built");
                    } else if (rs.getString("vch_purchase_as").equals("C")) {
                        dobj.setVehPurchAS("Drive Away Chassis");
                    }
                    dobj.setIsvch_purchase_as(true);
                }
                dobj.setDealerAddress(rs.getString("dlr_full_add"));
                dobj.setSaleAmt(rs.getString("sale_amt"));
                if (rs.getString("regn_upto") != null && !rs.getString("regn_upto").equals("")) {
                    dobj.setRegnUpto(rs.getString("regn_upto"));
                    dateRegn_upto = format.parse(rs.getString("regn_upto"));
                }
                if (rs.getString("fit_upto") != null && !rs.getString("fit_upto").equals("")) {
                    dobj.setFitUpto(rs.getString("fit_upto"));
                    dateFit_upto = format.parse(rs.getString("fit_upto"));
                }
                if (dateFit_upto.after(dateRegn_upto)) {
                    dobj.setRegnUpto(rs.getString("fit_upto"));
                }
                dobj.setFdesc(rs.getString("f_axle_descp"));
                dobj.setRdesc(rs.getString("r_axle_descp"));
                dobj.setOdesc(rs.getString("o_axle_descp"));
                dobj.setTdesc(rs.getString("t_axle_descp"));
                dobj.setFweight(rs.getString("f_axle_weight"));
                dobj.setRweight(rs.getString("r_axle_weight"));
                dobj.setOweight(rs.getString("o_axle_weight"));
                dobj.setTweight(rs.getString("t_axle_weight"));
                if (!Utility.isNullOrBlank(rs.getString("tr_chasi_no"))) {
                    dobj.setIstransportWithSemTrailler(true);
                }
                dobj.setTrChasino(rs.getString("tr_chasi_no"));
                dobj.setTrBodyType(rs.getString("tr_body_type"));
                dobj.setTrldwt(rs.getString("tr_ld_wt"));
                dobj.setTrunldwt(rs.getString("tr_unld_wt"));
                dobj.setTrfdesc(rs.getString("tr_f_axle_descp"));
                dobj.setTrrdesc(rs.getString("tr_r_axle_descp"));
                dobj.setTrodesc(rs.getString("tr_o_axle_descp"));
                dobj.setTrtdesc(rs.getString("tr_t_axle_descp"));
                dobj.setTrfweight(rs.getString("tr_f_axle_weight"));
                dobj.setTrrweight(rs.getString("tr_r_axle_weight"));
                dobj.setTroweight(rs.getString("tr_o_axle_weight"));
                dobj.setTrtweight(rs.getString("tr_t_axle_weight"));
                if (rs.getString("hypth_from_dt") != null && !rs.getString("hypth_from_dt").equals("")) {
                    dobj.setHyptFromdt(rs.getString("hypth_from_dt"));
                }
                dobj.setFncrFullAddr(rs.getString("fncr_full_add"));
                if (rs.getString("tax_from") != null && !rs.getString("tax_from").equals("")) {
                    dobj.setTaxFrom(rs.getString("tax_from"));
                }

                taxPaid = ServerUtil.taxPaidInfo(isRCPrint, rs.getString("regn_no"), 58);

                if (!taxPaid.isEmpty()) {
                    for (Map.Entry<String, String> entry : taxPaid.entrySet()) {
                        if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("tax_mode") && entry.getValue() != null) {
                            if (!entry.getValue().isEmpty() && "L,O".contains(entry.getValue())) {
                                dobj.setTaxUpto("One Time");
                                break;
                            }
                        } else if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("tax_upto") && entry.getValue() != null) {
                            taxPaidUpto = DateUtils.parseDate(entry.getValue());
                            dobj.setTaxUpto(format.format(taxPaidUpto));
                            break;
                        }
                    }
                }
                dobj.setTaxAmt(rs.getString("tax_amt"));
                dobj.setRcptno(rs.getString("rcpt_no"));
                if (rs.getString("op_dt") != null && !rs.getString("op_dt").equals("")) {
                    dobj.setOpdate(rs.getString("op_dt"));
                }
                dobj.setHsrpfront(rs.getString("hsrp_no_front"));
                dobj.setHsrpback(rs.getString("hsrp_no_back"));
                dobj.setOwnerSrno(rs.getInt("owner_sr"));
                dobj.setSrno(rs.getInt("sr_no"));
                dobj.setPurposeDesc(ServerUtil.getPurposeShortDescr(applno.toUpperCase()));
                if (!Utility.isNullOrBlank(rs.getString("dealer_cd"))) {
                    dobj.setIsfncr(true);
                }

                /*
                 * Other state,transfer and conversion details.
                 * Added by KML on 06-10-2015
                 */
                dobj.setPreOwner(rs.getString("previous_owner_name"));
                dobj.setPreRegno(rs.getString("old_regn_no"));
                dobj.setOldState(rs.getString("old_state"));
                dobj.setConversionDate(rs.getString("conversion_dt"));
                dobj.setTransferDate(rs.getString("transfer_dt"));
                dobj.setEntryDate(rs.getString("entry_date"));
                dobj.setOtherCriteria(rs.getString("criteria_desc"));
                if (ServerUtil.isTransport(rs.getInt("vh_class"), null)) {
                    dobj.setIstransport(true);
                    if (paperrc.contains(TableConstants.VM_PLASTIC_CARD_RC_CD) || paperrc.contains(TableConstants.VM_PLASTIC_CARD_RC_PVC_CD)) {
                        dobj.setRegnType("T");
                    }
                } else {
                    dobj.setIstransport(false);
                    if (paperrc.contains(TableConstants.VM_PLASTIC_CARD_RC_CD) || paperrc.contains(TableConstants.VM_PLASTIC_CARD_RC_PVC_CD)) {
                        dobj.setNontransport(true);
                        dobj.setRegnType("NT");
                    }
                }
                dobj.setStateCD(rs.getString("state_cd"));
                if (rs.getString("serviceType") != null && !rs.getString("serviceType").equals("")) {
                    dobj.setServiceType(rs.getString("serviceType"));
                    dobj.setIsServiceType(true);
                }
                if (paperrc.contains(TableConstants.VM_PLASTIC_CARD_RC_PVC_CD)) {
                    dobj.setPageBreakforHR(true);
                    dobj.setPageBreakExceptHR(false);
                    dobj.setSmartcardFrontPanel("smartcard-front-panel");
                    dobj.setSmartcardBackPanel("smartcard-back-panel");
                    dobj.setTopMarginOnPlasticRC("fncr-content-pvc-position");
                    dobj.setShowhideborder("border-bottom: 0px dashed #004c99");

                } else {
                    dobj.setPageBreakExceptHR(true);
                    dobj.setPageBreakforHR(false);
                    dobj.setSmartcardBackPanel("smartcard-back-panel-side");
                    dobj.setSmartcardFrontPanel("smartcard-front-panel-side");
                    dobj.setTopMarginOnPlasticRC("fncr-content-rc-position");
                    dobj.setShowhideborder("border-bottom: 2px dashed #004c99");
                }
                if (rs.getString("fncr_name") != null && !rs.getString("fncr_name").equals("")) {
                    dobj.setIshypothecated(true);
                    dobj.setFncrname(rs.getString("fncr_name"));
                }
                dobj.setLinkVehicleno(rs.getString("link_regn_no"));

                if ("KL".contains(state_cd) && dobj.isIstransport() && rs.getString("pmtCatg") != null && !rs.getString("pmtCatg").isEmpty()) {
                    dobj.setPermitCatg("(" + rs.getString("pmtCatg") + ")");
                }
                if ("KL".contains(state_cd) && rs.getString("transactionDate") != null && !rs.getString("transactionDate").isEmpty()) {
                    dobj.setTransactionDate(rs.getString("transactionDate"));
                    dobj.setShowTransactionDate(true);
                } else {
                    dobj.setShowTransactionDate(false);
                }
                if (rs.getBytes("userSign") != null && !rs.getBytes("userSign").equals("")) {
                    dobj.setUserSign(rs.getBytes("userSign"));
                    dobj.setIsUserSignExist(true);
                } else {
                    dobj.setIsUserSignExist(false);
                }
                if (rcRadiobtnValue.equalsIgnoreCase("PENRC") || rcRadiobtnValue.equalsIgnoreCase("REGNNORC")) {
                    deleteAndSaveHistory(applno, regn_no, isRCDispatch, isRCDispatchWithoutPostalFee, rs.getInt("pur_cd"), state_cd, off_cd);
                } else if (rcRadiobtnValue.equalsIgnoreCase("PRTRC") || rcRadiobtnValue.equalsIgnoreCase("REPRINTRC")) {
                    SaveVHRCPrintHistory(applno, state_cd, off_cd);
                }
            }
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
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dobj;
    }

    public static HSRPAuthorizationReportDobj getHsrpAuthorizationReportDobj(String regn_appl_no, boolean isApproved, String dealerCode) throws VahanException, ParseException {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        HSRPAuthorizationReportDobj dobj = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        java.util.Date today = Calendar.getInstance().getTime();
        String sql = null;
        TmConfigurationDobj configurationDobj = Util.getTmConfiguration();
        String dealerVar = "";
        try {
            tmgr = new TransactionManagerReadOnly("getHsrpAuthorizationReportDobj");
            if (Util.getUserCategory() != null && Util.getUserCategory().equals(TableConstants.USER_CATG_DEALER)) {
                if (dealerCode != null) {
                    dealerVar = " and a.dealer_cd = ?";
                } else {
                    throw new VahanException(TableConstants.SomthingWentWrong);
                }
            }
            if (isApproved) {
                sql = " SELECT a.state_cd, upper(a.state_name)as state_name , a.off_name, a.regn_no, a.regn_dt,a.owner_name, a.f_name, upper(a.c_add1 || ', ' || a.c_add2 || ', ' || a.c_add3 || ',' || chr(10) || a.c_district_name || '-' || a.c_state_name || '-' || a.c_pincode) as c_full_add, upper(a.vh_class_desc)  as vh_class_desc, a.chasi_no, a.eng_no, a.maker, a.maker_name, "
                        + " a.model_name, a.fuel, a.fuel_descr, a.color, a.manu_mon, a.manu_yr,b.mobile_no,c.appl_no,b.email_id,to_char(a.op_dt,'dd-Mon-yyyy') as op_dt,d.rcpt_heading,d.rcpt_subheading"
                        + " FROM " + TableList.VA_DETAILS + " c "
                        + " LEFT JOIN " + TableList.VT_OWNER_IDENTIFICATION + " b on b.regn_no=c.regn_no and b.state_cd = c.state_cd and b.off_cd = c.off_cd  "
                        + " LEFT JOIN " + TableList.VIEW_VV_OWNER + " a on a.regn_no=c.regn_no and a.state_cd =  c.state_cd and a.off_cd = c.off_cd " + dealerVar + " "
                        + " LEFT JOIN " + TableList.TM_CONFIGURATION + " d on d.state_cd=c.state_cd "
                        + " WHERE c.appl_no=? and c.pur_cd in (1,123,126) and c.state_cd = ? and c.off_cd=? ";
                ps = tmgr.prepareStatement(sql);
                int i = 1;
                if (Util.getUserCategory() != null && Util.getUserCategory().equals(TableConstants.USER_CATG_DEALER)) {
                    ps.setString(i++, dealerCode);
                }
                ps.setString(i++, regn_appl_no.toUpperCase());
                ps.setString(i++, Util.getUserStateCode());
                ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            } else {
                sql = " SELECT a.state_cd, upper(a.state_name)as state_name , a.off_name, a.regn_no, a.regn_dt,a.owner_name, a.f_name, upper(a.c_add1 || ', ' || a.c_add2 || ', ' || a.c_add3 || ',' || chr(10) || a.c_district_name || '-' || a.c_state_name || '-' || a.c_pincode) as c_full_add, upper(a.vh_class_desc)  as vh_class_desc, a.chasi_no, a.eng_no, a.maker, a.maker_name, "
                        + " a.model_name, a.fuel, a.fuel_descr, a.color, a.manu_mon, a.manu_yr,b.mobile_no,a.appl_no,b.email_id,to_char(a.op_dt,'dd-Mon-yyyy') as op_dt,d.rcpt_heading,d.rcpt_subheading"
                        + " FROM " + TableList.VIEW_VVA_OWNER + " a "
                        + " LEFT JOIN " + TableList.VT_OWNER_IDENTIFICATION + " b on b.regn_no=a.regn_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd "
                        + " LEFT JOIN " + TableList.TM_CONFIGURATION + " d on d.state_cd=a.state_cd "
                        + "WHERE a.appl_no=? and a.state_cd = ? and a.off_cd = ? " + dealerVar + " ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regn_appl_no.toUpperCase());
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                if (Util.getUserCategory() != null && Util.getUserCategory().equals(TableConstants.USER_CATG_DEALER)) {
                    ps.setString(4, dealerCode);
                }
            }
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new HSRPAuthorizationReportDobj();
                dobj.setHeader(rs.getString("rcpt_heading"));
                dobj.setSubHeader(rs.getString("rcpt_subheading"));

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
                    if (configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan()) {
                        VmRoadSafetySloganPrintDobj vmrssdobj = getStateRoadSafetySlogan();
                        if (vmrssdobj != null) {
                            dobj.setShowRoadSafetySlogan(configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan());
                            dobj.setRoadSafetySloganDobj(vmrssdobj);
                        }
                    }
                }
                dobj.setOffName(rs.getString("off_name"));
                dobj.setRegnNo(rs.getString("regn_no"));
                if (rs.getDate("regn_dt") != null && !rs.getDate("regn_dt").equals("")) {;
                    dobj.setRegnDt(format.format(rs.getDate("regn_dt")));
                }
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setStateCD(rs.getString("state_cd"));
                dobj.setStateName(rs.getString("state_name"));
                dobj.setOwnerName(rs.getString("owner_name"));
                dobj.setFatherName(rs.getString("f_name"));
                dobj.setCurrAddr(rs.getString("c_full_add"));
                dobj.setVchDesc(rs.getString("vh_class_desc"));
                dobj.setChasiNo(rs.getString("chasi_no"));
                dobj.setEngNo(rs.getString("eng_no"));
                dobj.setMakerName(rs.getString("maker_name"));
                dobj.setModelName(rs.getString("model_name"));
                dobj.setFuelDesc(rs.getString("fuel_descr"));
                dobj.setColor(rs.getString("color"));
                dobj.setManuMnt(rs.getString("manu_mon"));
                dobj.setManuYr(rs.getString("manu_yr"));
                dobj.setMobile(rs.getString("mobile_no"));
                dobj.setEmailId(rs.getString("email_id"));
                dobj.setOpdt(rs.getString("op_dt"));
                dobj.setReportGeneratedon(format.format(today));
            }
        } catch (SQLException ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            throw new VahanException("Unable to get HSRP Authorization details.");
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

    public static CashReceiptReportDobj getCashReceiptReportDobj(String rcptno) throws VahanException, ParseException {

        TransactionManager tmgr = null;
        CashReceiptReportDobj masterDobj = null;
        CashReceiptReportDobj instMasterDobj = null;
        CashRecieptSubListDobj subListDOBJ = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        Boolean issurcharge = false;
        Boolean isexcessamt = false;
        Boolean iscashamt = false;
        Boolean isrebate = false;
        Boolean isprv_adjs = false;
        Boolean isinterest = false;
        Boolean isexempted = false;
        Boolean isfine = false;
        Boolean isForTC = false;
        int totalInstAmt = 0;
        String regn_type = null;
        String old_regn_no = null;
        TmConfigurationDobj configurationDobj = Util.getTmConfiguration();
        try {
            String sqlOwner = " select a.* , b.descr as vh_class_descr ,vph.transaction_no,vp.transaction_id, c.sale_amt, h.fncr_name, "
                    + " vp.return_rcpt_no as bank_ref_no, a.cash_amt, a.excess_amt,a.remarks,ad.regn_no as adv_regn_no,d.regn_type,d.eng_no,to_char(current_timestamp,'dd-Mon-yyyy hh24:mi:ss') as printedDate \n"
                    + " ,e.descr as serviceType ,f.descr as pmtType,j.rcpt_heading,j.rcpt_subheading ,upper(COALESCE(d.c_add1,'') || ', ' || COALESCE(d.c_add2,'') || ', ' || COALESCE(d.c_add3,'')  || ',' || COALESCE(k.descr,'') || '-' || COALESCE(l.descr,'')  || '-' || COALESCE(d.c_pincode,0)) as c_full_add, "
                    + " m.old_regn_no, n.fine_exempted \n"
                    + " from " + TableList.VP_APPL_RCPT_MAPPING + " a \n"
                    + " left outer join " + TableList.VM_VH_CLASS + " b on b.vh_class = a.vh_class \n"
                    + " left outer join " + TableList.VPH_RCPT_CART + " vph on vph.appl_no = a.appl_no \n"
                    + " left outer join " + TableList.VT_TAX_BASED_ON + " c on c.state_cd = a.state_cd and c.off_cd = a.off_cd and c.rcpt_no = a.rcpt_no \n"
                    + " left outer join " + TableList.VVA_HPA + " h on h.appl_no = vph.appl_no and h.sr_no = 1 \n"
                    + " left outer join " + TableList.VP_PGI_DETAILS + " vp on vp.payment_id = vph.transaction_no \n"
                    + " left outer join " + TableList.VT_ADVANCE_REGN_NO + " ad on ad.state_cd = a.state_cd and ad.off_cd = a.off_cd and ad.recp_no = a.rcpt_no \n"
                    + " left outer join " + TableList.VA_OWNER + " d on d.appl_no = a.appl_no \n"
                    + " LEFT OUTER JOIN " + TableList.VM_SERVICES_TYPE + " e ON e.code=c.service_type \n"
                    + " LEFT OUTER JOIN " + TableList.VM_PERMIT_TYPE + " f ON f.code=c.pmt_type \n"
                    + " LEFT OUTER JOIN " + TableList.TM_CONFIGURATION + " j ON j.state_cd = a.state_cd \n"
                    + " LEFT OUTER JOIN " + TableList.TM_DISTRICT + " k on d.c_district=k.dist_cd  and d.state_cd=k.state_cd\n"
                    + " LEFT OUTER JOIN " + TableList.TM_STATE + " l on  d.c_state=l.state_code "
                    + " LEFT OUTER JOIN " + TableList.VA_OTHER_STATE_VEH + " m on  m.appl_no=a.appl_no\n"
                    + " LEFT OUTER JOIN " + TableList.VT_FEE_FINE_EXEMPTION + " n on n.state_cd = a.state_cd and n.off_cd = a.off_cd and n.rcpt_no = a.rcpt_no \n"
                    + " where a.state_cd = ? and a.off_cd = ? and a.rcpt_no = ?";

            tmgr = new TransactionManager("getCashReceiptReportDobj-1");
            PreparedStatement ps = tmgr.prepareStatement(sqlOwner);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setString(3, rcptno);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                masterDobj = new CashReceiptReportDobj();
                masterDobj.setC_full_add(rs.getString("c_full_add"));
                if (configurationDobj.getTmPrintConfgDobj() != null) {
                    if (configurationDobj.getTmPrintConfgDobj().getImage_background() != null && !configurationDobj.getTmPrintConfgDobj().getImage_background().isEmpty()) {
                        masterDobj.setImage_background(configurationDobj.getTmPrintConfgDobj().getImage_background());
                        masterDobj.setShow_image_background(true);
                    } else {
                        masterDobj.setShow_image_background(false);
                    }
                    if (configurationDobj.getTmPrintConfgDobj().getImage_logo() != null && !configurationDobj.getTmPrintConfgDobj().getImage_logo().isEmpty()) {
                        masterDobj.setImage_logo(configurationDobj.getTmPrintConfgDobj().getImage_logo());
                        masterDobj.setShow_image_logo(true);
                    } else {
                        masterDobj.setShow_image_logo(false);
                    }
                    if (configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan()) {
                        VmRoadSafetySloganPrintDobj vmrssdobj = getStateRoadSafetySlogan();
                        if (vmrssdobj != null) {
                            masterDobj.setShowRoadSafetySlogan(configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan());
                            masterDobj.setRoadSafetySloganDobj(vmrssdobj);
                        }
                    }
                    if (configurationDobj.getTmPrintConfgDobj().getCash_rcpt_note() != null && !configurationDobj.getTmPrintConfgDobj().getCash_rcpt_note().isEmpty()) {
                        masterDobj.setShowCashRcptNote(true);
                        masterDobj.setCashRcptNote(configurationDobj.getTmPrintConfgDobj().getCash_rcpt_note());
                    } else {
                        masterDobj.setShowCashRcptNote(false);
                        masterDobj.setCashRcptNote(null);
                    }
                    masterDobj.setAdvance_receipt_validity_days(configurationDobj.getTmPrintConfgDobj().getAdvance_receipt_validity_days());
                }
                masterDobj.setHeader(rs.getString("rcpt_heading"));
                masterDobj.setSubHeader(rs.getString("rcpt_subheading"));
                masterDobj.setReceiptNo(rs.getString("rcpt_no"));
                masterDobj.setOwnerName(rs.getString("owner_name"));
                if (rs.getString("chasi_no") != null && !rs.getString("chasi_no").isEmpty()) {
                    if (rs.getString("chasi_no").equalsIgnoreCase("CHASI_NO_NOT_APPLICABLE_FOR_TC")) {   ///// Setting VEHICLE NO NA for trade certificate
//                        masterDobj.setChasino("CHASI_NO_NOT_APPLICABLE");
                        masterDobj.setIsChasi_no(false);
                        isForTC = true;
                    } else {
                        masterDobj.setChasino(rs.getString("chasi_no"));
                        masterDobj.setIsChasi_no(true);
                    }
                } else {
                    masterDobj.setIsChasi_no(false);
                }
                if (rs.getInt("vh_class") > 0 || isForTC) {
                    if (isForTC) {
//                        masterDobj.setVhClass("VEHICLE_CLASS_NOT_APPLICABLE");
                        masterDobj.setIsVhClass(false);
                    } else {
                        masterDobj.setVhClass(rs.getString("vh_class_descr"));
                        masterDobj.setIsVhClass(true);
                    }
                } else {
                    masterDobj.setIsVhClass(false);
                }
                masterDobj.setApplNo(rs.getString("appl_no"));
                masterDobj.setTransactionId(rs.getString("transaction_id"));
                masterDobj.setSaleAmt(rs.getLong("sale_amt"));
                masterDobj.setFinancerName(rs.getString("fncr_name"));
                masterDobj.setBankRefNo(rs.getString("bank_ref_no"));
                masterDobj.setExcessAmt(rs.getString("excess_amt"));
                masterDobj.setCashAmt(rs.getString("cash_amt"));
                masterDobj.setInstrumentcd(rs.getLong("instrument_cd"));
                masterDobj.setPrintedDate(rs.getString("printedDate"));
                if (rs.getString("eng_no") != null && !rs.getString("eng_no").equals("")) {
                    masterDobj.setEngno(rs.getString("eng_no"));
                }
                regn_type = rs.getString("regn_type");
                old_regn_no = rs.getString("old_regn_no");
                if (rs.getString("remarks") != null && !rs.getString("remarks").equals("")) {
                    masterDobj.setIsbalFeeRemarks(true);
                }
                if (rs.getString("adv_regn_no") != null && !rs.getString("adv_regn_no").equals("")) {
                    masterDobj.setIsfancyno(true);
                }

                masterDobj.setBalFeeRemarks(rs.getString("remarks"));
                if (rs.getString("serviceType") != null && !rs.getString("serviceType").equals("")) {
                    masterDobj.setIsserviceType(true);
                    masterDobj.setServiceType(rs.getString("serviceType"));
                } else {
                    masterDobj.setIsserviceType(false);

                }
                if (rs.getString("pmtType") != null && !rs.getString("pmtType").equals("")) {
                    masterDobj.setIspmtType(true);
                    masterDobj.setPmtType(rs.getString("pmtType"));
                } else {
                    masterDobj.setIspmtType(false);
                }
                String sqlsubList = "select a.*, COALESCE(to_char(b.regn_dt, 'dd-Mon-yyyy'), '') as regn_dt,b.eng_no,upper(COALESCE(b.c_add1,'') || ', ' || COALESCE(b.c_add2,'') || ', ' || COALESCE(b.c_add3,'')  || ',' || COALESCE(c.descr,'') || '-' || COALESCE(d.descr,'')  || '-' || COALESCE(b.c_pincode,0)) as c_full_add from "
                        + " get_rcpt_details(?,?,?) a"
                        + " left outer join " + TableList.VT_OWNER + " b on b.regn_no = a.regn_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd "
                        + " left join " + TableList.TM_DISTRICT + " c on b.c_district=c.dist_cd  and b.state_cd=c.state_cd\n"
                        + " left join " + TableList.TM_STATE + " d on  b.c_state=d.state_code\n";
                PreparedStatement psSubList = tmgr.prepareStatement(sqlsubList);
                psSubList.setString(1, Util.getUserStateCode());
                psSubList.setInt(2, Util.getSelectedSeat().getOff_cd());
                psSubList.setString(3, rcptno);
                RowSet rsList = tmgr.fetchDetachedRowSet();
                int grandTotal = 0;
                Utility utility = new Utility();
                List<CashRecieptSubListDobj> list = new ArrayList<CashRecieptSubListDobj>();
                while (rsList.next()) {
                    if (rsList.getString("eng_no") != null && !rsList.getString("eng_no").equals("")) {
                        masterDobj.setEngno(rsList.getString("eng_no"));
                    }
                    if (rsList.getString("c_full_add") != null && !rsList.getString("c_full_add").equals("")) {
                        masterDobj.setC_full_add(rsList.getString("c_full_add"));
                    }
                    masterDobj.setOffname(rsList.getString("office"));
                    if (rsList.getString("rcpt_dt") != null && !rsList.getString("rcpt_dt").equals("")) {
                        masterDobj.setReceiptDate(rsList.getString("rcpt_dt"));
                    }
                    if (rsList.getString("regn_no") != null && !rsList.getString("regn_no").isEmpty()) {
                        if (isForTC) {
//                            masterDobj.setRegnNo("VEHICLE_NO_NOT_APPLICABLE");
                            masterDobj.setIsRegn_no(false);
                        } else if (rsList.getString("regn_no").equalsIgnoreCase("NEW") && regn_type != null && (regn_type.equalsIgnoreCase("O") || regn_type.equalsIgnoreCase("R"))) {
                            if (rsList.getString("state_cd").contains("KA")) {
                                masterDobj.setRegnNo(old_regn_no);
                            } else {
                                masterDobj.setRegnNo("Other RTO/State Vch");
                            }
                            masterDobj.setIsRegn_no(true);

                        } else {
                            masterDobj.setRegnNo(rsList.getString("regn_no"));
                            masterDobj.setIsRegn_no(true);
                        }
                    } else {
                        masterDobj.setIsRegn_no(false);
                    }
                    if (rsList.getString("user_cd").equalsIgnoreCase(TableConstants.ONLINE_PAYMENT)) {
                        masterDobj.setEmpName("ONLINE PAYMENT");
                    } else {
                        masterDobj.setEmpName(rsList.getString("emp_name"));
                    }
                    masterDobj.setRegnDate(rsList.getString("regn_dt"));
                    if (masterDobj.getRegnDate() != null && !masterDobj.getRegnDate().equals("")) {
                        masterDobj.setIsregnDate(true);
                    }
                    CashRecieptSubListDobj dobjTaxdetailall = getTaxDetailAll(rcptno, rsList.getInt("pur_cd"));
                    subListDOBJ = new CashRecieptSubListDobj();
                    subListDOBJ.setBlnisfine(isfine);
                    if (dobjTaxdetailall != null) {
                        if (configurationDobj.getTmPrintConfgDobj() != null && configurationDobj.getTmPrintConfgDobj().isPrint_tax_token()) {
                            masterDobj.setPrint_tax_token(true);
                        } else {
                            masterDobj.setPrint_tax_token(false);
                        }
                        if (Double.parseDouble(dobjTaxdetailall.getSurcharge()) > 0) {
                            issurcharge = true;
                        } else {
                            issurcharge = false;
                        }
                        subListDOBJ.setBlnissurcharge(issurcharge);
                        if (Double.parseDouble(dobjTaxdetailall.getExcessAmt()) > 0) {
                            isexcessamt = true;
                        } else {
                            isexcessamt = false;
                        }
                        subListDOBJ.setBlnisexcessAmt(isexcessamt);
                        if (Double.parseDouble(dobjTaxdetailall.getCashAmt()) > 0) {
                            iscashamt = true;
                        } else {
                            iscashamt = false;
                        }
                        subListDOBJ.setBlniscashAmt(iscashamt);
                        if (Double.parseDouble(dobjTaxdetailall.getRebate()) > 0) {
                            isrebate = true;
                        } else {
                            isrebate = false;
                        }
                        subListDOBJ.setBlnisrebate(isrebate);
                        if (Double.parseDouble(dobjTaxdetailall.getPrv_adjustment()) > 0) {
                            isprv_adjs = true;
                        } else {
                            isprv_adjs = false;
                        }
                        subListDOBJ.setBlnisprv_adjustment(isprv_adjs);
                        if (Double.parseDouble(dobjTaxdetailall.getInterest()) > 0) {
                            isinterest = true;
                        } else {
                            isinterest = false;
                        }
                        subListDOBJ.setBlnisinterest(isinterest);
                        if (Double.parseDouble(dobjTaxdetailall.getExempted()) > 0) {
                            isexempted = true;
                        } else {
                            isexempted = false;
                        }
                        subListDOBJ.setBlnisexempted(isexempted);
                    }
                    if (rsList.getString("period_from").equals("")) {
                        subListDOBJ.setDateFrom(rsList.getString("period_from"));
                    } else {
                        subListDOBJ.setDateFrom("(" + rsList.getString("period_from"));
                    }
                    if (rsList.getString("period_upto") != null && !rsList.getString("period_upto").equals("")) {
                        subListDOBJ.setDateUpto(" to " + rsList.getString("period_upto") + ")");
                    } else {
                        subListDOBJ.setDateUpto(rsList.getString("period_upto"));
                    }
                    subListDOBJ.setPurpose(rsList.getString("purpose"));
                    subListDOBJ.setPurCd(rsList.getInt("pur_cd"));

                    if (dobjTaxdetailall == null) {
                        subListDOBJ.setAmount(rsList.getInt("fees") + "");
                        subListDOBJ.setPenalty(rsList.getInt("fine") + "");
                        subListDOBJ.setTotal(rsList.getInt("fees") + rsList.getInt("fine"));

                    } else {
                        // Checking the purpose codes are same or not
                        if (rsList.getInt("pur_cd") == dobjTaxdetailall.getPurCd()) {
                            subListDOBJ.setAmount(dobjTaxdetailall.getAmount());
                            subListDOBJ.setSurcharge(dobjTaxdetailall.getSurcharge());
                            subListDOBJ.setExcessAmt(dobjTaxdetailall.getExcessAmt());
                            subListDOBJ.setCashAmt(dobjTaxdetailall.getCashAmt());
                            subListDOBJ.setRebate(dobjTaxdetailall.getRebate());
                            subListDOBJ.setPrv_adjustment(dobjTaxdetailall.getPrv_adjustment());
                            subListDOBJ.setInterest(dobjTaxdetailall.getInterest());
                            subListDOBJ.setExempted(dobjTaxdetailall.getExempted());
                            subListDOBJ.setPenalty(rsList.getInt("fine") + "");
                            subListDOBJ.setTotal(rsList.getInt("fees") + rsList.getInt("fine"));
                        }
                    }
                    grandTotal += subListDOBJ.getTotal();
                    list.add(subListDOBJ);
                }
                if (String.valueOf(masterDobj.getInstrumentcd()) != null && !String.valueOf(masterDobj.getInstrumentcd()).equals("") && masterDobj.getInstrumentcd() > 0) {
                    tmgr = new TransactionManager("getCashReceiptReportDobj-2");
                    String instrumentSQL = "select a.sr_no::varchar || '. ' || c.descr || ' No ' || a.instrument_no || ' dtd ' || to_char(a.instrument_dt,'dd-Mon-yyyy') || ' Amt ' || a.instrument_amt::varchar || '/- of ' || b.descr as instrument,a.instrument_amt"
                            + "  from VT_INSTRUMENTS a"
                            + "  LEFT OUTER JOIN TM_BANK b ON b.bank_code = a.bank_code"
                            + "  left outer join tm_instruments c on c.code = a.instrument_type"
                            + " where a.instrument_cd =?"
                            + " order by a.sr_no";
                    PreparedStatement psinst = tmgr.prepareStatement(instrumentSQL);
                    psinst.setLong(1, masterDobj.getInstrumentcd());
                    RowSet rsinst = tmgr.fetchDetachedRowSet();
                    List<CashReceiptReportDobj> instList = new ArrayList<CashReceiptReportDobj>();
                    while (rsinst.next()) {
                        instMasterDobj = new CashReceiptReportDobj();
                        if (!Utility.isNullOrBlank(rsinst.getString("instrument_amt"))) {
                            totalInstAmt += Integer.parseInt(rsinst.getString("instrument_amt"));
                        }

                        instMasterDobj.setInstrumentDtls(rsinst.getString("instrument"));
                        instList.add(instMasterDobj);
                    }
                    if (totalInstAmt > 0) {
                        masterDobj.setInstrumentAmt(String.valueOf(totalInstAmt));
                        masterDobj.setIsinstrumentAmt(true);
                    }
                    if (masterDobj.getExcessAmt() != null && !masterDobj.getExcessAmt().equals("") && Integer.parseInt(masterDobj.getExcessAmt()) > 0) {
                        masterDobj.setIsecsessAmt(true);
                    }
                    if (masterDobj.getCashAmt() != null && !masterDobj.getCashAmt().equals("") && Integer.parseInt(masterDobj.getCashAmt()) > 0) {
                        masterDobj.setIscashAmt(true);
                    }
                    masterDobj.setPrntInstList(instList);
                }
                if (rs.getString("fine_exempted") != null && !rs.getString("fine_exempted").equals("")) {
                    masterDobj.setExemAmount(rs.getString("fine_exempted"));
                }

                masterDobj.setGrandTotal(grandTotal);
                masterDobj.setGrandTotalInWords(utility.ConvertNumberToWords(grandTotal));
                masterDobj.setPrntRecieptSubList(list);
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Exception for Receipt no :" + rcptno + " :" + e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Printing Cash Receipt");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error("Exception for Receipt no :" + rcptno + " :" + e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return masterDobj;
    }

    public RCCancelCertificateDobj getVehicleDetails(String vehicleNO) throws VahanException {
        RCCancelCertificateDobj dobj = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = "select ownerr.regn_no,ownerr.chasi_no,ownerr.eng_no,ownerr.owner_name,"
                + " ownerr.c_add1,ownerr.c_add2,ownerr.c_add3,ownerr.c_district,\n"
                + " ownerr.c_pincode,ownerr.c_state ,office.off_name,state.descr,rccancel.file_ref_no\n"
                + " from " + TableList.VIEW_VV_OWNER + "  ownerr \n"
                + " Left Outer Join " + TableList.TM_OFFICE + " office on  office.off_cd=ownerr.off_cd  and office.state_cd=ownerr.state_cd \n"
                + " Left Outer Join " + TableList.TM_STATE + " state on  state.state_code=ownerr.state_cd \n"
                + " Left Outer Join " + TableList.VT_RC_CANCEL + " rccancel on  ownerr.regn_no= rccancel.regn_no \n"
                + " where ownerr.regn_no=? and ownerr.state_cd=? and ownerr.off_cd = ?  ";
        try {
            tmgr = new TransactionManagerReadOnly("getVehicleDetails");
            ps = tmgr.prepareStatement(sql);

            ps.setString(1, vehicleNO);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());

            RowSet rowSet = tmgr.fetchDetachedRowSet();
            if (rowSet.next()) {
                dobj = new RCCancelCertificateDobj();
                dobj.setChassisNo(rowSet.getString("chasi_no"));
                dobj.setEngineNO(rowSet.getString("eng_no"));
                dobj.setOwnerAddress(rowSet.getString("c_add1") + "," + rowSet.getString("c_add2")
                        + "," + rowSet.getString("c_add3") + "," + rowSet.getString("c_district")
                        + "," + rowSet.getString("c_pincode")
                        + "," + rowSet.getString("c_state"));
                dobj.setOwnerName(rowSet.getString("owner_name"));
                dobj.setState(rowSet.getString("descr"));
                dobj.setOffice(rowSet.getString("off_name"));
                dobj.setFileRefrenceNo(rowSet.getString("file_ref_no"));
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
        return dobj;
    }

    public VehicleReleaseDobj getVehicleReleaseDetails(String appl_no) throws VahanException {
        VehicleReleaseDobj dobj = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = " select 0+row_number() OVER() as sl_no,vehicle.regn_no as vehicle_no,\n"
                + "               vehicle.release_by,vehicle.release_dt,vehicle.police_station_name,vehicle.police_station_district_name,COALESCE(amt.cmpd_amt,amt.adv_amt) as cfee,amt.settlement_amt as sfee,\n"
                + "               COALESCE(amt.cmpd_rcpt_no,amt.adv_rcpt_no) as cmd_rcpt_no,COALESCE(amt.cmpd_rcpt_dt,amt.adv_rcpt_dt) as cmd_rcpt_dt,rtoCode.off_name as rto "
                + "               from " + TableList.VT_VEHICLE_IMPOUND + " vehicle\n"
                + "               left  join " + TableList.VT_CHALLAN_AMT + " amt on vehicle.appl_no=amt.appl_no and vehicle.state_cd=amt.state_cd\n"
                + "               left outer join " + TableList.TM_OFFICE + " rtoCode on vehicle.state_cd::TEXT=rtoCode.state_cd and vehicle.off_cd=rtoCode.off_cd \n"
                + "               where vehicle.appl_no=? and vehicle.state_cd=?";
        try {
            tmgr = new TransactionManagerReadOnly("getVehicleReleaseDetails");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, Util.getUserStateCode());
            RowSet rowSet = tmgr.fetchDetachedRowSet();
            if (rowSet.next()) {
                dobj = new VehicleReleaseDobj();
                dobj.setSrNo(rowSet.getInt("sl_no"));
                dobj.setVehicle_no(rowSet.getString("vehicle_no"));
                dobj.setCmd_rcpt_no(rowSet.getString("cmd_rcpt_no"));
                dobj.setCmd_rcpt_dt(rowSet.getDate("cmd_rcpt_dt"));
                dobj.setRelease_Date(rowSet.getDate("release_dt"));
                dobj.setRelease_by(rowSet.getString("release_by"));
                dobj.setCmd_fee(rowSet.getInt("cfee"));
                dobj.setDistrict(rowSet.getString("police_station_district_name"));
                dobj.setPoliceStation(rowSet.getString("police_station_name"));
                dobj.setRto_name(rowSet.getString("rto"));
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
        return dobj;
    }

    public List<DocumentReleaseDobj> getDocumentReleaseDetails(String appl_no) throws VahanException {
        DocumentReleaseDobj dobj = null;
        PreparedStatement ps = null;
        List<DocumentReleaseDobj> listDobj = new ArrayList<DocumentReleaseDobj>();
        TransactionManagerReadOnly tmgr = null;
        String sql = "select 0+row_number() OVER() as sl_no,challan.chal_no,challan.regn_no as vehicle_no, \n"
                + " to_char(docs.validity,'dd-Mon-yyyy') as validity,docs.doc_no,docs.other_doc_name, \n"
                + " docs.iss_auth,docs.accused_catg,docs.release_by,docs.release_dt,document.descr, \n"
                + " rtoCode.off_name as rto \n"
                + " from " + TableList.VH_CHALLAN + " challan \n"
                + " left outer join  " + TableList.VT_DOCS_IMPOUND + " docs on challan.appl_no=docs.appl_no and challan.state_cd=docs.state_cd \n"
                + " left outer join " + TableList.VM_DOCUMENTS + " document on docs.doc_cd=document.code \n"
                + " left outer join " + TableList.TM_OFFICE + " rtoCode on challan.state_cd::TEXT=rtoCode.state_cd and challan.off_cd=rtoCode.off_cd \n"
                + " where challan.appl_no=? and challan.state_cd=?";
        try {
            tmgr = new TransactionManagerReadOnly("getDocumentReleaseDetails");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, Util.getUserStateCode());
            RowSet rowSet = tmgr.fetchDetachedRowSet();
            while (rowSet.next()) {
                dobj = new DocumentReleaseDobj();
                dobj.setSrNo(rowSet.getInt("sl_no"));
                dobj.setChal_no(rowSet.getString("chal_no"));
                dobj.setVehicle_no(rowSet.getString("vehicle_no"));
                dobj.setRelease_Date(rowSet.getString("release_dt"));
                dobj.setRelease_by(rowSet.getString("release_by"));
                dobj.setAccused_catg(rowSet.getString("accused_catg"));
                dobj.setDoc_desc(rowSet.getString("descr"));
                dobj.setDoc_no(rowSet.getString("doc_no"));
                dobj.setValidity(rowSet.getString("validity"));
                dobj.setIss_auth(rowSet.getString("iss_auth"));
                dobj.setRto_name(rowSet.getString("rto"));
                dobj.setOther_doc_name(rowSet.getString("other_doc_name"));
                listDobj.add(dobj);
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
        return listDobj;
    }

    public CashReportDobj getCashReportDetails(String appl_no) throws VahanException {
        CashReportDobj dobj = null;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = "SELECT challan.chal_no,to_char(challan.chal_date, 'dd-MON-YYYY') as chal_date,challan.regn_no,challan.comming_from,challan.going_to,\n"
                + " chalamt.appl_no,  chalamt.adv_amt, chalamt.cmpd_amt, chalamt.settlement_amt, chalamt.adv_rcpt_no, \n"
                + " chalamt.adv_rcpt_dt, chalamt.cmpd_rcpt_no, to_char(chalamt.cmpd_rcpt_dt, 'dd-MON-YYYY') as cmpd_rcpt_dt,\n"
                + " chalamt.settlement_amt as compoundAmount,\n"
                + " taxamt.appl_no, \n"
                + " CASE \n"
                + " WHEN (taxamt.chal_penalty!=0 or taxamt.chal_penalty!=null) THEN  COALESCE(taxamt.chal_penalty,0)\n"
                + " ELSE '0' END:: numeric as totalTaxPenalty,\n"
                + " CASE \n"
                + " WHEN (taxamt.chal_tax!=0 or taxamt.chal_tax!=null) THEN   (COALESCE(taxamt.chal_tax,0)+ COALESCE(taxamt.chal_interest,0))\n"
                + " ELSE '0' END:: numeric as Totaltax,\n"
                + " (COALESCE(taxamt.chal_tax,0)+ COALESCE(taxamt.chal_interest,0)+COALESCE(taxamt.chal_penalty,0)) as grandTotalTax,\n"
                + " (COALESCE(taxamt.chal_tax,0)+ COALESCE(taxamt.chal_interest,0)+ COALESCE(taxamt.chal_penalty,0)+COALESCE(chalamt.settlement_amt,0)) as grandTotal,\n"
                + " taxamt.cmpd_rcpt_no, taxamt.cmpd_rcpt_dt,owne.owner_name\n"
                + " FROM " + TableList.VT_CHALLAN + " challan\n"
                + " LEFT OUTER JOIN  " + TableList.VT_CHALLAN_AMT + " chalamt on challan.appl_no=chalamt.appl_no and challan.state_cd=chalamt.state_cd \n"
                + " LEFT OUTER JOIN  " + TableList.VT_CHALLAN_TAX + " taxamt On  challan.appl_no=taxamt.appl_no and challan.state_cd=taxamt.state_cd \n"
                + " LEFT OUTER JOIN " + TableList.VT_OWNER + " owne On  challan.regn_no=owne.regn_no and challan.state_cd=owne.state_cd and challan.off_cd=owne.off_cd \n"
                + " where chalamt.appl_no=? and challan.state_cd=?";
        try {
            tmgr = new TransactionManagerReadOnly("getCashReportDetails");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, Util.getUserStateCode());
            RowSet rowSet = tmgr.fetchDetachedRowSet();
            if (rowSet.next()) {
                dobj = new CashReportDobj();
                dobj.setChal_no(rowSet.getString("chal_no"));
                dobj.setChal_date(rowSet.getString("chal_date"));
                dobj.setRegn_no(rowSet.getString("regn_no"));
                dobj.setCmpd_rcpt_date(rowSet.getString("cmpd_rcpt_dt"));
                dobj.setCmpd_rcpt_no(rowSet.getString("cmpd_rcpt_no"));
                dobj.setCmpd_amt(rowSet.getInt("compoundAmount"));
                dobj.setTax(rowSet.getInt("Totaltax"));
                dobj.setTax_penalty(rowSet.getInt("totalTaxPenalty"));
                dobj.setGrand_total_tax(rowSet.getInt("grandTotalTax"));
                dobj.setGrand_total(rowSet.getInt("grandTotal"));
                dobj.setOwner_name(rowSet.getString("owner_name"));
                dobj.setComing_from(rowSet.getString("comming_from"));
                dobj.setGoing_to(rowSet.getString("going_to"));

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
        return dobj;
    }
// end by Raj Yadav

    ///////////////////////// Code for Surcharge,rebate,interest,prv_adjustment
    public static CashRecieptSubListDobj getTaxDetailAll(String rcptno, Integer pur_cd) throws VahanException {
        CashRecieptSubListDobj dobj = null;
        PreparedStatement ps = null;
        ResultSet rowSet = null;
        TransactionManager tmgr = null;
        String sql = "select state_cd,off_cd,rcpt_no,pur_cd,sum(prv_adjustment) as prv_adjustment,sum(tax) as tax,"
                + "sum(exempted) as exempted, sum(rebate) as rebate, sum(surcharge) as surcharge, sum(interest) as interest , "
                + "sum(tax1) as tax1, sum(tax2) as tax2"
                + " from " + TableList.VT_TAX_BREAKUP + " where rcpt_no=? and state_cd =? and off_cd = ? and pur_cd = ? "
                + "group by state_cd,off_cd,rcpt_no,pur_cd";

        try {
            tmgr = new TransactionManager("getTaxDetailAll");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, rcptno);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            ps.setInt(4, pur_cd);
            rowSet = tmgr.fetchDetachedRowSet();
            if (rowSet.next()) {
                dobj = new CashRecieptSubListDobj();
                dobj.setSurcharge(String.valueOf(rowSet.getString("surcharge").replace(".00", "")));
                dobj.setExcessAmt(String.valueOf(rowSet.getString("tax1").replace(".00", "")));
                dobj.setCashAmt(String.valueOf(rowSet.getString("tax2").replace(".00", "")));
                dobj.setAmount(String.valueOf(rowSet.getString("tax").replace(".00", "")));
                dobj.setRebate(String.valueOf(rowSet.getString("rebate").replace(".00", "")));
                dobj.setPrv_adjustment(String.valueOf(rowSet.getString("prv_adjustment")));
                dobj.setExempted(String.valueOf(rowSet.getLong("exempted")));
                dobj.setInterest(String.valueOf(rowSet.getString("interest").replace(".00", "")));
                dobj.setPurCd(rowSet.getInt("pur_cd"));

            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error("Exception for Receipt no :" + rcptno + " :" + e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dobj;
    }

    public static DisAppNoticeDobj getDisAppNoticeData(String applNo, String regnNo, int purCode) throws VahanException {
        DisAppNoticeDobj dobj = null;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        RowSet rs;
        String sql;
        try {
            tmgr = new TransactionManagerReadOnly("getDisAppNoticeData");
            if (purCode == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                    || purCode == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                    || purCode == TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
                sql = " select a.appl_no, to_char(d.appl_dt, 'dd-MON-YYYY') as entry_dt, e.descr, c.regn_no, c.off_name, c.state_name, c.owner_name,\n"
                        + " (c.c_add1 || ',' || c.c_add2 || ',' || c.c_add3 || ',' || c.c_district_name || ',' || c.p_state_name) AS address, string_agg(b.descr, ', ') AS reasons_list\n"
                        + " from  va_status a\n"
                        + " inner join vm_reasons b ON b.code = ANY(string_to_array(trim(a.public_remark),',')::integer[])\n"
                        + " inner join vva_owner c ON c.appl_no = a.appl_no \n"
                        + " inner join va_details d ON d.appl_no = a.appl_no and d.pur_cd = ? \n"
                        + " inner join tm_purpose_mast e ON e.pur_cd = d.pur_cd \n"
                        + " WHERE a.appl_no = ? and (substring(trim(a.public_remark),1,1) ~ '[0-9]+$') \n"
                        + " GROUP BY 1, 2, 3, 4, 5, 6, 7, 8";
                ps = tmgr.prepareStatement(sql);
                ps.setInt(1, purCode);
                ps.setString(2, applNo);
                rs = tmgr.fetchDetachedRowSet();
                while (rs.next()) {
                    dobj = new DisAppNoticeDobj();
                    dobj.setApplNo(rs.getString("appl_no"));
                    dobj.setRegnNo(rs.getString("regn_no"));
                    dobj.setOffName(rs.getString("off_name"));
                    dobj.setStateName(rs.getString("state_name"));
                    dobj.setOwnerName(rs.getString("owner_name"));
                    dobj.setPurpose(rs.getString("descr"));
                    dobj.setAddress(rs.getString("address"));
                    dobj.setReasonList(ServerUtil.makeList(rs.getString("reasons_list")));
                    dobj.setRcptheading(ServerUtil.getRcptHeading());
                    dobj.setEntryDate(rs.getString("entry_dt"));
                }
            } else if (purCode == TableConstants.VM_TRANSACTION_MAST_TO) {
                sql = " select a.appl_no, to_char(d.appl_dt, 'dd-MON-YYYY') as entry_dt, e.descr, c.regn_no, c.off_name, c.state_name, f.owner_name, \n"
                        + "      (f.c_add1 || ',' || f.c_add2 || ',' || f.c_add3 || ',' || g.descr || ',' || f.c_state) AS address, string_agg(b.descr, ', ') AS reasons_list\n"
                        + "  from  va_status a\n"
                        + "  inner join vm_reasons b ON b.code = ANY(string_to_array(trim(a.public_remark),',')::integer[])\n"
                        + "  inner join vv_owner c ON c.regn_no = ? and a.state_cd = c.state_cd and a.off_cd = c.off_cd  \n"
                        + "  inner join va_details d ON d.appl_no = a.appl_no and d.pur_cd = ?\n"
                        + "  inner join tm_purpose_mast e ON e.pur_cd = d.pur_cd\n"
                        + "  inner join va_to f on a.appl_no = f.appl_no	\n"
                        + "  inner join tm_district g on f.c_district = g.dist_cd\n"
                        + "  WHERE a.appl_no = ? and (substring(trim(a.public_remark),1,1) ~ '[0-9]+$')\n"
                        + "  GROUP BY 1, 2, 3, 4, 5, 6, 7, 8";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regnNo);
                ps.setInt(2, purCode);
                ps.setString(3, applNo);
                rs = tmgr.fetchDetachedRowSet();
                while (rs.next()) {
                    dobj = new DisAppNoticeDobj();
                    dobj.setApplNo(rs.getString("appl_no"));
                    dobj.setRegnNo(rs.getString("regn_no"));
                    dobj.setOffName(rs.getString("off_name"));
                    dobj.setStateName(rs.getString("state_name"));
                    dobj.setOwnerName(rs.getString("owner_name"));
                    dobj.setPurpose(rs.getString("descr"));
                    dobj.setAddress(rs.getString("address"));
                    dobj.setReasonList(ServerUtil.makeList(rs.getString("reasons_list")));
                    dobj.setRcptheading(ServerUtil.getRcptHeading());
                    dobj.setEntryDate(rs.getString("entry_dt"));

                }
            } else if (purCode == TableConstants.VM_PMT_SURRENDER_PUR_CD || purCode == TableConstants.VM_PMT_RESTORE_PUR_CD || purCode == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD
                    || purCode == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD || purCode == TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD || purCode == TableConstants.VM_PMT_CANCELATION_PUR_CD
                    || purCode == TableConstants.VM_PMT_DUPLICATE_PUR_CD || purCode == TableConstants.VM_PMT_TRANSFER_PUR_CD || purCode == TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD
                    || purCode == TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD || purCode == TableConstants.VM_PMT_FRESH_PUR_CD || purCode == TableConstants.VM_PMT_APPLICATION_PUR_CD || purCode == TableConstants.VM_PMT_RENEWAL_PUR_CD
                    || purCode == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD || purCode == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD) {
                sql = "select a.appl_no, to_char(d.appl_dt, 'dd-MON-YYYY') as entry_dt, e.descr, c.regn_no, c.off_name, c.state_name, c.owner_name, \n"
                        + "      (c.c_add1 || ',' || c.c_add2 || ',' || c.c_add3 || ',' || c.c_district_name || ',' || c.p_state_name) AS address, string_agg(b.descr, ', ') AS reasons_list\n"
                        + "  from  va_status a\n"
                        + "  inner join vm_reasons b ON b.code = ANY(string_to_array(trim(a.public_remark),',')::integer[])\n"
                        + "  inner join vv_owner c ON c.regn_no = ? and a.state_cd = c.state_cd \n"
                        + "  inner join va_details d ON d.appl_no = a.appl_no and d.pur_cd = ?\n"
                        + "  inner join tm_purpose_mast e ON e.pur_cd = d.pur_cd\n"
                        + "  WHERE a.appl_no = ? and (substring(trim(a.public_remark),1,1) ~ '[0-9]+$')\n"
                        + "  GROUP BY 1, 2, 3, 4, 5, 6, 7, 8";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regnNo);
                ps.setInt(2, purCode);
                ps.setString(3, applNo);
                rs = tmgr.fetchDetachedRowSet();
                while (rs.next()) {
                    dobj = new DisAppNoticeDobj();
                    dobj.setApplNo(rs.getString("appl_no"));
                    dobj.setRegnNo(rs.getString("regn_no"));
                    dobj.setOffName(rs.getString("off_name"));
                    dobj.setStateName(rs.getString("state_name"));
                    dobj.setOwnerName(rs.getString("owner_name"));
                    dobj.setPurpose(rs.getString("descr"));
                    dobj.setAddress(rs.getString("address"));
                    dobj.setReasonList(ServerUtil.makeList(rs.getString("reasons_list")));
                    dobj.setRcptheading(ServerUtil.getRcptHeading());
                    dobj.setEntryDate(rs.getString("entry_dt"));
                }
            } else if (purCode == TableConstants.VM_TRANSACTION_MAST_AUCTION) {
                sql = " select a.appl_no, to_char(d.appl_dt, 'dd-MON-YYYY') as entry_dt, e.descr, c.regn_no, c.off_name, c.state_name, c.owner_name, \n"
                        + "      (c.c_add1 || ',' || c.c_add2 || ',' || c.c_add3 || ',' || c.c_district_name || ',' || c.p_state_name) AS address, string_agg(b.descr, ', ') AS reasons_list\n"
                        + "  from  va_status a\n"
                        + "  inner join vm_reasons b ON b.code = ANY(string_to_array(trim(a.public_remark),',')::integer[])\n"
                        + "  inner join vv_owner c ON c.regn_no = ?\n"
                        + "  inner join va_details d ON d.appl_no = a.appl_no and d.pur_cd = ?\n"
                        + "  inner join tm_purpose_mast e ON e.pur_cd = d.pur_cd\n"
                        + "  WHERE a.appl_no = ? and (substring(trim(a.public_remark),1,1) ~ '[0-9]+$')\n"
                        + "  GROUP BY 1, 2, 3, 4, 5, 6, 7, 8";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regnNo);
                ps.setInt(2, purCode);
                ps.setString(3, applNo);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    dobj = new DisAppNoticeDobj();
                    dobj.setApplNo(rs.getString("appl_no"));
                    dobj.setRegnNo(rs.getString("regn_no"));
                    dobj.setOffName(rs.getString("off_name"));
                    dobj.setStateName(rs.getString("state_name"));
                    dobj.setOwnerName(rs.getString("owner_name"));
                    dobj.setPurpose(rs.getString("descr"));
                    dobj.setAddress(rs.getString("address"));
                    dobj.setReasonList(ServerUtil.makeList(rs.getString("reasons_list")));
                    dobj.setRcptheading(ServerUtil.getRcptHeading());
                    dobj.setEntryDate(rs.getString("entry_dt"));
                }
            } else {
                sql = "select a.appl_no, to_char(d.appl_dt, 'dd-MON-YYYY') as entry_dt, e.descr, c.regn_no, c.off_name, c.state_name, c.owner_name, \n"
                        + "      (c.c_add1 || ',' || c.c_add2 || ',' || c.c_add3 || ',' || c.c_district_name || ',' || c.p_state_name) AS address, string_agg(b.descr, ', ') AS reasons_list\n"
                        + "  from  va_status a\n"
                        + "  inner join vm_reasons b ON b.code = ANY(string_to_array(trim(a.public_remark),',')::integer[])\n"
                        + "  inner join vv_owner c ON c.regn_no = ? and a.state_cd = c.state_cd and a.off_cd = c.off_cd \n"
                        + "  inner join va_details d ON d.appl_no = a.appl_no and d.pur_cd = ?\n"
                        + "  inner join tm_purpose_mast e ON e.pur_cd = d.pur_cd\n"
                        + "  WHERE a.appl_no = ? and (substring(trim(a.public_remark),1,1) ~ '[0-9]+$')\n"
                        + "  GROUP BY 1, 2, 3, 4, 5, 6, 7, 8";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regnNo);
                ps.setInt(2, purCode);
                ps.setString(3, applNo);
                rs = tmgr.fetchDetachedRowSet();
                while (rs.next()) {
                    dobj = new DisAppNoticeDobj();
                    dobj.setApplNo(rs.getString("appl_no"));
                    dobj.setRegnNo(rs.getString("regn_no"));
                    dobj.setOffName(rs.getString("off_name"));
                    dobj.setStateName(rs.getString("state_name"));
                    dobj.setOwnerName(rs.getString("owner_name"));
                    dobj.setPurpose(rs.getString("descr"));
                    dobj.setAddress(rs.getString("address"));
                    dobj.setReasonList(ServerUtil.makeList(rs.getString("reasons_list")));
                    dobj.setRcptheading(ServerUtil.getRcptHeading());
                    dobj.setEntryDate(rs.getString("entry_dt"));
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    throw new VahanException(e.getMessage());
                }
            }
        }
        return dobj;
    }

    public static FCPrintReportDobj getFCPrintDobj(String regn_no, String appl_no, TmConfigurationDobj configurationDobj, String rcRadiobtnValue, String printForm) throws VahanException, ParseException {
        TransactionManager tmgr = null;
        FCPrintReportDobj dobj = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        PreparedStatement ps = null;
        RowSet rs;
        String sql = "";

        //boolean isRecordFound = false; 
        try {
            String userStateCd = Util.getUserStateCode();
            int selectedOffCd = Util.getSelectedSeat().getOff_cd();
            int user_cd = Integer.parseInt(Util.getEmpCode());
            tmgr = new TransactionManager("getFCPrintDobj-1");
            if (printForm != null && !printForm.isEmpty() && "FORM38A".contains(printForm)) {
                sql = "select a.appl_no,a.regn_no,a.fit_result,a.chasi_no,a.remark,b.off_name,c.descr as stateName,to_char(current_timestamp,'dd-Mon-yyyy hh24:mi:ss') as printed_on,d.user_name as fitnessInfectionOff1,e.user_name as fitnessInfectionOff2 ,f.pur_cd,g.desig_name as InspOffNameOneDesig,h.desig_name as InspOffNameTwoDesig\n"
                        + " from  " + TableList.VT_FITNESS_TEMP + " a \n"
                        + " LEFT OUTER JOIN " + TableList.TM_OFFICE + " b on b.state_cd = a.state_cd and b.off_cd =a.off_cd \n"
                        + " LEFT OUTER JOIN " + TableList.TM_STATE + " c on c.state_code = a.state_cd \n"
                        + " left join " + TableList.TM_USER_INFO + " d on d.user_cd= regexp_replace(COALESCE(trim(a.fit_off_cd1::text), '0'), '[^0-9]', '0', 'g')::numeric and d.state_cd=a.state_cd and d.off_cd=a.off_cd \n"
                        + " left join " + TableList.TM_USER_INFO + " e on e.user_cd= regexp_replace(COALESCE(trim(a.fit_off_cd2::text), '0'), '[^0-9]', '0', 'g')::numeric and e.state_cd=a.state_cd and e.off_cd=a.off_cd \n"
                        + " left join " + TableList.VA_DETAILS + " f on f.appl_no = a.appl_no and f.pur_cd in (2,14) \n"
                        + " LEFT OUTER JOIN " + TableList.TM_DESIGNATION + " g ON g.desig_cd = d.desig_cd \n"
                        + " LEFT OUTER JOIN " + TableList.TM_DESIGNATION + " h ON h.desig_cd = e.desig_cd \n"
                        + " where a.regn_no=? and a.state_cd=? and a.off_cd=? order by a.op_dt DESC limit 1 \n";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regn_no);
                ps.setString(2, userStateCd);
                ps.setInt(3, selectedOffCd);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    dobj = new FCPrintReportDobj();
                    if (rs.getString("fit_result").equals("") || rs.getString("fit_result").equals("N")) {
                        dobj.setIsfitFailed(true);
                        dobj.setFitFailMsg("Fitness Certificate for Application No " + appl_no + " can not be printed due to Failed Fitness.");
                        dobj.setFcRemark(rs.getString("remark"));
                        dobj.setRegnNO("Vehicle No: " + rs.getString("regn_no"));
                    } else {
                        dobj.setIsfitFailed(false);
                        dobj.setRegnNO(rs.getString("regn_no"));
                    }
                    dobj.setApplNO(rs.getString("appl_no"));
                    dobj.setOffName(rs.getString("off_name"));
                    dobj.setStateName(rs.getString("stateName"));
                    if (rs.getString("fitnessInfectionOff1") != null && !rs.getString("fitnessInfectionOff1").equals("")) {
                        dobj.setIsFitInfecOff1(true);
                        if (rs.getString("fitnessInfectionOff2") != null && !rs.getString("fitnessInfectionOff2").equals("")) {
                            dobj.setFitInfecOff1(rs.getString("fitnessInfectionOff1") + " / " + rs.getString("fitnessInfectionOff2"));
                        } else {
                            dobj.setFitInfecOff1(rs.getString("fitnessInfectionOff1"));
                        }
                    } else if (rs.getString("fitnessInfectionOff2") != null && !rs.getString("fitnessInfectionOff2").equals("")) {
                        dobj.setIsFitInfecOff1(true);
                        dobj.setFitInfecOff1(rs.getString("fitnessInfectionOff2"));
                    } else {
                        dobj.setIsFitInfecOff1(false);
                        dobj.setFitInfecOff1("");
                    }
                    dobj.setPrinted_on(rs.getString("printed_on"));
                    dobj.setIsFitDoneInOtherState(true);
                    if (rs.getInt("pur_cd") != 0 && rs.getInt("pur_cd") == 2) {
                        dobj.setFcReportLabel("Certificate of Fitness");
                    } else if (rs.getInt("pur_cd") != 0 && rs.getInt("pur_cd") == 14) {
                        dobj.setFcReportLabel("Certificate of Fitness (Duplicate)");
                    }
                    dobj.setHeader(configurationDobj.getRcpt_heading());
                    dobj.setSubHeader(configurationDobj.getRcpt_subheading());
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
                        if (configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan()) {
                            VmRoadSafetySloganPrintDobj vmrssdobj = getStateRoadSafetySlogan();
                            if (vmrssdobj != null) {
                                dobj.setShowRoadSafetySlogan(configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan());
                                dobj.setRoadSafetySloganDobj(vmrssdobj);
                            }
                        }
                    }
                    if (rs.getString("InspOffNameOneDesig") != null && !rs.getString("InspOffNameOneDesig").equals("")) {
                        if (rs.getString("InspOffNameTwoDesig") != null && !rs.getString("InspOffNameTwoDesig").equals("")) {
                            dobj.setInspectingOfficerOneDesig("(" + rs.getString("InspOffNameOneDesig") + " / " + rs.getString("InspOffNameTwoDesig") + ")");
                        } else {
                            dobj.setInspectingOfficerOneDesig("(" + rs.getString("InspOffNameOneDesig") + ")");
                        }
                    } else if (rs.getString("InspOffNameTwoDesig") != null && !rs.getString("InspOffNameTwoDesig").equals("")) {
                        dobj.setInspectingOfficerOneDesig("(" + rs.getString("InspOffNameTwoDesig") + ")");

                    } else {
                        dobj.setInspectingOfficerOneDesig("");
                    }
                    dobj.setFormName("FORM 38A");
                    dobj.setShowTextInCaseoftransportvehiclesonly(false);
                }
            } else {
                sql = "SELECT a.state_cd, upper(a.state_name)as state_name ,f.off_cd,g.off_name,case when a.regn_no='NEW' then 'Chassis No: ' || a.chasi_no else 'Vehicle No: ' || a.regn_no end as regn_no , \n"
                        + " a.vh_class_desc,b.fit_valid_to as fit_upto,b.fit_nid,to_char(b.fit_chk_dt,'dd-Mon-yyyy') as fit_chk_dt,b.fit_result,b.remark,to_char(current_timestamp,'dd-Mon-yyyy hh24:mi:ss') as printed_on,c.user_name as fitnessInfectionOff1 \n"
                        + " ,d.user_name as fitnessInfectionOff2,e.fees,e.rcpt_no,to_char(e.rcpt_dt,'dd-Mon-yyyy') as rcpt_dt,a.chasi_no,a.regn_no as regnno,a.eng_no,a.body_type,a.seat_cap,a.manu_yr,a.vch_catg,e.pur_cd,h.doc_sign as signFitInfOff1 ,i.doc_sign as signFitInfOff2 \n"
                        + " from  " + TableList.VIEW_VVA_OWNER + " a \n"
                        + " inner join " + TableList.VA_FITNESS + " b on b.appl_no=a.appl_no and b.state_cd=a.state_cd and b.off_cd=a.off_cd \n"
                        + " left join " + TableList.TM_USER_INFO + " c on c.user_cd= regexp_replace(COALESCE(trim(b.fit_off_cd1::text), '0'), '[^0-9]', '0', 'g')::numeric and c.state_cd=b.state_cd and c.off_cd=b.off_cd \n"
                        + " left join " + TableList.TM_USER_INFO + " d on d.user_cd= regexp_replace(COALESCE(trim(b.fit_off_cd2::text), '0'), '[^0-9]', '0', 'g')::numeric and d.state_cd=b.state_cd and d.off_cd=b.off_cd \n"
                        + " LEFT OUTER JOIN (SELECT vf.* FROM vt_fee vf, vp_appl_rcpt_mapping varm WHERE varm.appl_no=? and vf.rcpt_no=varm.rcpt_no and vf.state_cd=varm.state_cd and vf.off_cd=varm.off_cd and vf.pur_cd in(2,14) ORDER BY rcpt_dt DESC LIMIT 1) e ON e.regn_no = a.regn_no  \n"
                        + " left join " + TableList.VA_DETAILS + " f on f.appl_no = a.appl_no and f.pur_cd in (2,14) \n"
                        + " left join " + TableList.TM_OFFICE + " g on g.state_cd = b.state_cd and g.off_cd =b.off_cd \n"
                        + " left join " + TableList.TM_USER_SIGN + " h on h.user_cd = c.user_cd \n"
                        + " left join " + TableList.TM_USER_SIGN + " i on i.user_cd = d.user_cd \n"
                        + " where a.appl_no=? \n";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_no);
                ps.setString(2, appl_no);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (!rs.next() && regn_no != null && !regn_no.isEmpty() && !regn_no.equalsIgnoreCase("NEW")) {
                    //tmgr = new TransactionManager("getFCPrintDobj-2");
                    sql = "SELECT a.state_cd, upper(a.state_name)as state_name ,f.off_cd,g.off_name,'Vehicle No: ' || a.regn_no as regn_no,a.vh_class_desc,b.fit_result,b.remark,to_char(b.fit_chk_dt,'dd-Mon-yyyy') as fit_chk_dt,b.fit_valid_to as fit_upto,b.fit_nid,to_char(current_timestamp,'dd-Mon-yyyy hh24:mi:ss') as printed_on,c.user_name as fitnessInfectionOff1 \n"
                            + " ,d.user_name as fitnessInfectionOff2,e.fees,e.rcpt_no,to_char(e.rcpt_dt,'dd-Mon-yyyy') as rcpt_dt,a.chasi_no,a.regn_no as regnno,a.eng_no,a.body_type,a.seat_cap,a.manu_yr,a.vch_catg,e.pur_cd,h.doc_sign as signFitInfOff1 ,i.doc_sign as signFitInfOff2,j.appl_no as applNo38A,k.off_name as offName38A \n"
                            + " from  " + TableList.VIEW_VV_OWNER + " a \n"
                            + " inner join (SELECT * FROM " + TableList.VT_FITNESS + " WHERE regn_no =? and state_cd = ? ORDER BY OP_DT DESC LIMIT 1) b on b.regn_no=a.regn_no and b.state_cd=a.state_cd \n"
                            + " left join " + TableList.TM_USER_INFO + " c on c.user_cd=regexp_replace(COALESCE(trim(b.fit_off_cd1::text), '0'), '[^0-9]', '0', 'g')::numeric and c.state_cd=b.state_cd and c.off_cd=b.off_cd \n"
                            + " left join " + TableList.TM_USER_INFO + " d on d.user_cd=regexp_replace(COALESCE(trim(b.fit_off_cd2::text), '0'), '[^0-9]', '0', 'g')::numeric and d.state_cd=b.state_cd and d.off_cd=b.off_cd \n"
                            + " LEFT OUTER JOIN (SELECT * FROM " + TableList.VT_FEE + " WHERE regn_no =? and state_cd = ? and pur_cd in(2,14) ORDER BY rcpt_dt DESC LIMIT 1) e ON e.regn_no = a.regn_no \n"
                            + " left join " + TableList.VA_DETAILS + " f on f.regn_no = a.regn_no and f.pur_cd in (2,14) \n"
                            + " left join " + TableList.TM_OFFICE + " g on g.state_cd = b.state_cd and g.off_cd =b.off_cd \n"
                            + " left join " + TableList.TM_USER_SIGN + " h on h.user_cd = c.user_cd \n"
                            + " left join " + TableList.TM_USER_SIGN + " i on i.user_cd = d.user_cd \n"
                            + " left join " + TableList.VH_FITNESS_TEMP + " j on j.regn_no = a.regn_no \n"
                            + " left join " + TableList.TM_OFFICE + " k on k.state_cd = j.state_cd and k.off_cd =j.off_cd \n"
                            + " where a.regn_no=? and a.state_cd=? order by a.op_dt DESC limit 1";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, regn_no);
                    ps.setString(2, userStateCd);
                    ps.setString(3, regn_no);
                    ps.setString(4, userStateCd);
                    ps.setString(5, regn_no);
                    ps.setString(6, userStateCd);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                } else {
                    rs.beforeFirst();
                    if (!rs.next()) {
                        sql = "SELECT a.state_cd, upper(a.state_name)as state_name ,f.off_cd,g.off_name,case when a.regn_no='NEW' then 'Chassis No ' || a.chasi_no else 'Vehicle No: ' || a.regn_no end as regn_no , \n"
                                + " a.vh_class_desc,b.fit_valid_to as fit_upto,b.fit_nid,to_char(b.fit_chk_dt,'dd-Mon-yyyy') as fit_chk_dt,b.fit_result,b.remark,to_char(current_timestamp,'dd-Mon-yyyy hh24:mi:ss') as printed_on,c.user_name as fitnessInfectionOff1 \n"
                                + " ,d.user_name as fitnessInfectionOff2,e.fees,e.rcpt_no,to_char(e.rcpt_dt,'dd-Mon-yyyy') as rcpt_dt,a.chasi_no,a.regn_no as regnno,a.eng_no,a.body_type,a.seat_cap,a.manu_yr,a.vch_catg,e.pur_cd ,h.doc_sign as signFitInfOff1 ,i.doc_sign as signFitInfOff2\n"
                                + " from  " + TableList.VIEW_VVA_OWNER + " a \n"
                                + " inner join " + TableList.VT_FITNESS_CHASSIS + " b on b.chasi_no=a.chasi_no and b.state_cd=a.state_cd and b.off_cd=a.off_cd \n"
                                + " left join " + TableList.TM_USER_INFO + " c on c.user_cd= regexp_replace(COALESCE(trim(b.fit_off_cd1::text), '0'), '[^0-9]', '0', 'g')::numeric and c.state_cd=b.state_cd and c.off_cd=b.off_cd \n"
                                + " left join " + TableList.TM_USER_INFO + " d on d.user_cd= regexp_replace(COALESCE(trim(b.fit_off_cd2::text), '0'), '[^0-9]', '0', 'g')::numeric and d.state_cd=b.state_cd and d.off_cd=b.off_cd \n"
                                + " LEFT OUTER JOIN (SELECT vf.* FROM vt_fee vf, vp_appl_rcpt_mapping varm WHERE varm.appl_no=? and vf.rcpt_no=varm.rcpt_no and vf.state_cd=varm.state_cd and vf.off_cd=varm.off_cd and vf.pur_cd in(2,14) ORDER BY rcpt_dt DESC LIMIT 1) e ON e.regn_no = a.regn_no  \n"
                                + " left join " + TableList.VA_DETAILS + " f on f.appl_no = a.appl_no and f.pur_cd in (2,14) \n"
                                + " left join " + TableList.TM_OFFICE + " g on g.state_cd = b.state_cd and g.off_cd =b.off_cd \n"
                                + " left join " + TableList.TM_USER_SIGN + " h on h.user_cd = c.user_cd \n"
                                + " left join " + TableList.TM_USER_SIGN + " i on i.user_cd = d.user_cd \n"
                                + " where a.appl_no=? \n";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, appl_no);
                        ps.setString(2, appl_no);
                        rs = tmgr.fetchDetachedRowSet_No_release();
                    }
                }
                rs.beforeFirst();
                if (rs.next()) {
                    dobj = new FCPrintReportDobj();
                    //isRecordFound = true;
                    dobj.setApplNO(appl_no);
                    if (rs.getString("fit_result").equals("") || rs.getString("fit_result").equals("N")) {
                        dobj.setIsfitFailed(true);
                        dobj.setFitFailMsg("Fitness Certificate for Application No " + appl_no + " can not be printed due to Failed Fitness.");
                        dobj.setFcRemark(rs.getString("remark"));
                    } else {
                        dobj.setIsfitFailed(false);
                    }
                    if (rs.getInt("pur_cd") != 0 && rs.getInt("pur_cd") == 2) {
                        dobj.setFcReportLabel("Certificate of Fitness");
                    } else if (rs.getInt("pur_cd") != 0 && rs.getInt("pur_cd") == 14) {
                        dobj.setFcReportLabel("Certificate of Fitness (Duplicate)");
                    }
                    dobj.setHeader(configurationDobj.getRcpt_heading());
                    dobj.setSubHeader(configurationDobj.getRcpt_subheading());

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
                        if (configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan()) {
                            VmRoadSafetySloganPrintDobj vmrssdobj = getStateRoadSafetySlogan();
                            if (vmrssdobj != null) {
                                dobj.setShowRoadSafetySlogan(configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan());
                                dobj.setRoadSafetySloganDobj(vmrssdobj);
                            }
                        }
                    }
                    dobj.setOffName(rs.getString("off_name"));
                    dobj.setRegnNO(rs.getString("regn_no"));
                    dobj.setStateCD(rs.getString("state_cd"));
                    dobj.setStateName(rs.getString("state_name"));
                    dobj.setVchDescr(rs.getString("vh_class_desc"));
                    if (rs.getString("fitnessInfectionOff1") != null && !rs.getString("fitnessInfectionOff1").equals("")) {
                        dobj.setFitInfecOff1(rs.getString("fitnessInfectionOff1"));
                        dobj.setIsFitInfecOff1(true);
                    } else {
                        dobj.setIsFitInfecOff1(false);
                    }
                    if (rs.getString("fitnessInfectionOff2") != null && !rs.getString("fitnessInfectionOff2").equals("")) {
                        dobj.setFitInfecOff2(rs.getString("fitnessInfectionOff2"));
                        dobj.setIsFitInfecOff2(true);
                    } else {
                        dobj.setIsFitInfecOff2(false);
                    }
                    if (rs.getDate("fit_nid") != null && !rs.getDate("fit_nid").equals("")) {
                        dobj.setNid(format.format(rs.getDate("fit_nid")));
                    }
                    if (rs.getDate("fit_upto") != null && !rs.getDate("fit_upto").equals("")) {
                        dobj.setFitUpto(format.format(rs.getDate("fit_upto")));
                    }
                    if (rs.getString("fit_chk_dt") != null && !rs.getString("fit_chk_dt").equals("")) {
                        dobj.setFitCheckDate(rs.getString("fit_chk_dt"));
                    }
                    dobj.setFeeAmt(rs.getString("fees"));
                    dobj.setRcptno(rs.getString("rcpt_no"));
                    dobj.setRcptdt(rs.getString("rcpt_dt"));
                    dobj.setEng_no(rs.getString("eng_no"));
                    dobj.setBody_type(rs.getString("body_type"));
                    dobj.setSeat_cap(rs.getString("seat_cap"));
                    dobj.setManu_yr(rs.getString("manu_yr"));
                    dobj.setVch_catg(rs.getString("vch_catg"));
                    dobj.setRegn_no(rs.getString("regnno"));
                    dobj.setChasi_no(rs.getString("chasi_no"));
                    dobj.setPrinted_on(rs.getString("printed_on"));
                    if (rs.getBytes("signFitInfOff1") != null && !rs.getBytes("signFitInfOff1").equals("")) {
                        dobj.setSignFitOff1(rs.getBytes("signFitInfOff1"));
                        dobj.setIsSignFitOff1(true);
                    } else {
                        dobj.setIsSignFitOff1(false);
                    }
                    if (rs.getBytes("signFitInfOff2") != null && !rs.getBytes("signFitInfOff2").equals("")) {
                        dobj.setSignFitOff2(rs.getBytes("signFitInfOff2"));
                        dobj.setIsSignFitOff2(true);
                    } else {
                        dobj.setIsSignFitOff2(false);
                    }
                    dobj.setFormName("FORM 38");
                    dobj.setShowTextInCaseoftransportvehiclesonly(true);
                    if (!regn_no.equalsIgnoreCase("NEW") && rs.getString("applNo38A") != null && !rs.getString("applNo38A").isEmpty() && userStateCd.contains("MH")) {
                        dobj.setOffName38A(rs.getString("offName38A"));
                        dobj.setNoteOffName38A(true);
                    } else {
                        dobj.setNoteOffName38A(false);
                    }
                }
                if (dobj != null) {
                    if (rcRadiobtnValue.equals("PENFC") || rcRadiobtnValue.equalsIgnoreCase("REGNNOFC")) {
                        deleteAndSaveHistoryFC(appl_no);
                    } else if (rcRadiobtnValue.equals("PRTFC") || rcRadiobtnValue.equals("REPRINTFC")) {
                        SaveVHFCPrintHistory(appl_no);
                    }
                }
            }

        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException("Error-Record not found");

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

    public static ArrayList<PrintCertificatesDobj> getPurCdPrintDocsDetailsFC(String state_cd, int off_cd) throws VahanException {
        ArrayList<PrintCertificatesDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        VahanException vahanexecption = null;
        try {
            tmgr = new TransactionManagerReadOnly("getPurCdPrintDocsDetailsFC");
            String sql = "select distinct appl_no, regn_no, state_cd, off_cd,op_dt from  " + TableList.VA_FC_PRINT + "  where state_cd = ? and off_cd = ? and op_dt > current_date - 15 order by op_dt DESC";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                PrintCertificatesDobj dobj = new PrintCertificatesDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegno(rs.getString("regn_no"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setState_cd(rs.getString("state_cd"));
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

    public static void deleteAndSaveHistoryFC(String applno) {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("deleteAndSaveHistoryFC");
            String sql = " insert into " + TableList.VH_FC_PRINT
                    + " select current_timestamp,?,state_cd,off_cd,appl_no,regn_no,op_dt from " + TableList.VA_FC_PRINT + " where appl_no=? AND state_cd=? AND off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, applno);
            ps.setString(3, Util.getUserStateCode());
            ps.setInt(4, Util.getSelectedSeat().getOff_cd());
            ps.executeUpdate();
            sql = " delete from " + TableList.VA_FC_PRINT + " where appl_no=? AND state_cd=? AND off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applno);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            ps.executeUpdate();
            tmgr.commit();
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
    }

    public static OwnerDisclaimerReportDobj getOwnerDisclaimerDobjForRegisteredVehicle(String appl_no, String printType) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        OwnerDisclaimerReportDobj dobj = null;
        String errMsg = " transaction is pending at data entry level. <br />Please first complete this transaction!!!";
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            tmgr = new TransactionManager("getOwnerDisclaimerDobjForRegisteredVehicle-1");
            sql = "select d.regn_no,d.appl_dt,m.pur_cd,m.descr \n"
                    + " from va_details d, tm_purpose_mast m \n"
                    + " where d.pur_cd = m.pur_cd and d.state_cd = ? and d.appl_no = ? and d.off_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, appl_no);
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            TmConfigurationDobj configurationDobj = Util.getTmConfiguration();

            if (rs.next()) {
                if (!rs.getString("regn_no").equals("NEW") && !rs.getString("regn_no").equals("TEMPREG")) {
                    tmgr = new TransactionManager("getOwnerDisclaimerDobjForRegisteredVehicle-2");
                    sql = "SELECT a.state_cd, upper(a.state_name)as state_name , a.off_name,case when upper(k.regn_no) IS NULL then 'FANCY NO NOT ASSIGN' else k.regn_no end as advance_regn_no, case when upper(a.regn_no) = 'NEW' then 'REGN NO NOT ASSIGN' else a.regn_no end as regn_no  , a.regn_dt, a.purchase_dt,case when tax_mode='E' then 'EXEMPTED' else 'NOT EXEMPTED' end as isexem, case when upper(l.old_regn_no) IS NULL then 'RETEN NO NOT ASSIGN' else l.old_regn_no end as reten_regn_no, \n"
                            + " a.owner_sr, a.owner_name, a.f_name, upper(a.c_add1 || ', ' || a.c_add2 || ', ' || a.c_add3 || ', ' ||  a.c_district_name || '-' || a.c_state_name || '-' || a.c_pincode) as c_full_add,\n"
                            + "  upper(a.p_add1 || ', ' || a.p_add2 || ', ' || a.p_add3 || ', ' || a.p_district_name || ', ' || a.p_state_name || '-' || a.p_pincode) as p_full_add,\n"
                            + "  a.owner_cd,g.descr as ow_descr,case when owner_cd in (4,5) then 'GOVT' else 'PRIVATE' end as ISGOV , j.descr as regn_type, upper(a.vh_class_desc)  as vh_class_desc, a.chasi_no, a.eng_no, a.maker, a.maker_name as maker_name, a.model_name as model_name, a.body_type, a.no_cyl, a.hp,\n"
                            + "  a.seat_cap, a.stand_cap, a.sleeper_cap, a.unld_wt, a.ld_wt, a.gcw, a.fuel, a.fuel_descr, a.color, a.manu_mon, a.manu_yr, a.norms_descr, upper(a.norms_descr) as norms_descr, a.wheelbase,\n"
                            + "  a.cubic_cap, a.floor_area, case when a.ac_fitted = 'Y' then 'YES' else 'NO' end as ac_fitted, a.audio_fitted, a.video_fitted, a.vch_purchase_as, a.vch_catg, a.dealer_cd, case when a.dlr_name <> '' then upper(a.dlr_name || ', ' || a.dlr_add1 || ', ' || a.dlr_add2 || ', ' ||\n"
                            + "  a.dlr_add3 || ', ' || a.dlr_city || ',' || chr(10) || a.dlr_district || ' ' || a.dlr_pincode) else '' end as dlr_full_add , a.sale_amt, upper(a.garage_add) as garage_add, a.length, a.width, a.height, a.regn_upto, a.fit_upto, a.annual_income,\n"
                            + "  a.imported_vch, b.f_axle_descp, b.r_axle_descp, b.o_axle_descp, b.t_axle_descp, b.f_axle_weight, b.r_axle_weight, b.o_axle_weight, b.t_axle_weight,\n"
                            + "  c.chasi_no as tr_chasi_no, c.body_type as tr_body_type, c.ld_wt as tr_ld_wt, c.unld_wt as tr_unld_wt, c.f_axle_descp as tr_f_axle_descp, c.r_axle_descp as tr_r_axle_descp, c.o_axle_descp as tr_o_axle_descp, c.t_axle_descp as tr_t_axle_descp,  c.f_axle_weight as tr_f_axle_weight, c.r_axle_weight as tr_r_axle_weight, c.o_axle_weight as tr_o_axle_weight, c.t_axle_weight as tr_t_axle_weight,\n"
                            + "  date(f.from_dt) as hypth_from_dt, (select distinct max(sr_no) from vv_hypth where regn_no=f.regn_no and state_cd=f.state_cd and off_cd=f.off_cd) as sr_no,(f.fncr_name || ', ' || f.fncr_add1 || ', ' || f.fncr_add2 || ', ' || f.fncr_add3 || ', ' || chr(10) || f.fncr_district_name || ', ' || f.fncr_state_name || ', ' || f.fncr_pincode) as fncr_full_add, to_char(h.tax_from,'dd-Mon-yyyy') as tax_from , h.tax_amt , h.rcpt_no , \n"
                            + "  case when h.tax_mode IN ('L', 'O') then 'One Time' else to_char(tax_upto,'dd-Mon-yyyy') end as tax_upto,\n"
                            + "  a.op_dt, e.ins_type_descr as ins_type, e.ins_company_name as company_name, e.policy_no as policy_no,e.ins_from as ins_from, e.ins_upto as ins_upto,d.kit_srno,d.kit_type,d.kit_manuf,d.workshop,d.fitment_dt,d.hydro_test_dt,d.cyl_srno,d.approval_no,d.approval_dt,i.pan_no,i.aadhar_no,passport_no,i.voter_id,p.rcpt_heading,p.rcpt_subheading \n"
                            + "  FROM vv_owner a\n"
                            + "  LEFT OUTER JOIN vt_axle b ON b.regn_no = a.regn_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd "
                            + "  LEFT OUTER JOIN vt_trailer c ON c.regn_no = a.regn_no "
                            + "  LEFT OUTER JOIN vt_retrofitting_dtls d ON d.regn_no = a.regn_no and  d.state_cd = a.state_cd and d.off_cd = a.off_cd "
                            + "  LEFT OUTER JOIN vv_insurance e ON e.regn_no = a.regn_no and e.state_cd = a.state_cd and e.off_cd = a.off_cd "
                            + "  LEFT OUTER JOIN vv_hypth f ON f.regn_no = a.regn_no and sr_no=1 and f.state_cd = a.state_cd and f.off_cd = a.off_cd "
                            + "  LEFT OUTER JOIN vm_owcode g ON g.ow_code=a.owner_cd\n"
                            + "  LEFT OUTER JOIN vm_regn_type j ON j.regn_typecode = a.regn_type\n"
                            + " LEFT OUTER JOIN vt_advance_regn_no k ON k.regn_no=a.regn_no "
                            + " LEFT OUTER JOIN vt_surrender_retention l on l.old_regn_no=a.regn_no and l.state_cd = a.state_cd and l.off_cd = a.off_cd "
                            + " Left OUTER JOIN vt_owner_identification i on i.regn_no = a.regn_no and i.state_cd = a.state_cd and i.off_cd = a.off_cd "
                            + "  LEFT OUTER JOIN (SELECT * FROM vt_tax WHERE regn_no = ? and vt_tax.state_cd = ? and vt_tax.off_cd = ?  and pur_cd = 58 and left(tax_mode, 1) IN ('H','L', 'O', 'S', 'Y', 'Q', 'M') ORDER BY rcpt_dt DESC LIMIT 1) h ON h.regn_no = a.regn_no \n"
                            + " LEFT OUTER JOIN tm_configuration p ON p.state_cd = a.state_cd "
                            + "  WHERE a.regn_no = ? and a.state_cd=? and a.off_cd=? ";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, rs.getString("regn_no"));
                    ps.setString(2, Util.getUserStateCode());
                    ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                    ps.setString(4, rs.getString("regn_no"));
                    ps.setString(5, Util.getUserStateCode());
                    Status_dobj applInwardOthOffDobj = new ApplicationInwardImpl().getApplInwardOthOffDobj(appl_no);
                    if (applInwardOthOffDobj != null) {
                        ps.setInt(6, applInwardOthOffDobj.getOff_cd());
                    } else {
                        ps.setInt(6, Util.getSelectedSeat().getOff_cd());
                    }
                    RowSet rs1 = tmgr.fetchDetachedRowSet();
                    if (rs1.next()) {
                        dobj = new OwnerDisclaimerReportDobj();
                        dobj.setHeader(rs1.getString("rcpt_heading"));
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
                            if (configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan()) {
                                VmRoadSafetySloganPrintDobj vmrssdobj = getStateRoadSafetySlogan();
                                if (vmrssdobj != null) {
                                    dobj.setShowRoadSafetySlogan(configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan());
                                    dobj.setRoadSafetySloganDobj(vmrssdobj);
                                }
                            }
                        }
                        if (applInwardOthOffDobj != null) {
                            String offName = ServerUtil.getOfficeName(Util.getSelectedSeat().getOff_cd(), Util.getUserStateCode());
                            dobj.setOffName(offName);
                        } else {
                            if (rs1.getString("off_name") != null && !rs1.getString("off_name").equals("")) {
                                dobj.setOffName(rs1.getString("off_name"));
                            }
                        }
                        if (rs1.getString("regn_no") != null && !rs1.getString("regn_no").equals("")) {
                            dobj.setRegnNO(rs1.getString("regn_no"));
                        }
                        if (appl_no != null && !appl_no.equals("")) {
                            dobj.setApplNO(appl_no);
                        }
                        if (rs1.getDate("regn_dt") != null && !rs1.getDate("regn_dt").equals("")) {;
                            dobj.setRegnDT(format.format(rs1.getDate("regn_dt")));
                        }
                        if (rs1.getString("state_cd") != null && !rs1.getString("state_cd").equals("")) {
                            dobj.setStateCD(rs1.getString("state_cd"));
                        }
                        if (rs1.getDate("purchase_dt") != null && !rs1.getDate("purchase_dt").equals("")) {;
                            dobj.setPuechaseDT(format.format(rs1.getDate("purchase_dt")));
                        }
                        if (rs1.getString("state_name") != null && !rs1.getString("state_name").equals("")) {
                            dobj.setStateName(rs1.getString("state_name"));
                        }
                        if (rs1.getString("isexem") != null && !rs1.getString("isexem").equals("")) {
                            dobj.setIsExem(rs1.getString("isexem"));
                        }
                        if (rs1.getString("owner_sr") != null && !rs1.getString("owner_sr").equals("")) {
                            dobj.setOwnerSrNO(rs1.getString("owner_sr"));
                        }
                        if (rs1.getString("owner_name") != null && !rs1.getString("owner_name").equals("")) {
                            dobj.setOwnerName(rs1.getString("owner_name"));
                        }
                        if (rs1.getString("f_name") != null && !rs1.getString("f_name").equals("")) {
                            dobj.setFname(rs1.getString("f_name"));
                        }
                        if (rs1.getString("c_full_add") != null && !rs1.getString("c_full_add").equals("")) {
                            dobj.setCurrAddress(rs1.getString("c_full_add"));
                        }
                        if (rs1.getString("p_full_add") != null && !rs1.getString("p_full_add").equals("")) {
                            dobj.setPerAddress(rs1.getString("p_full_add"));
                        }
                        if (rs1.getString("owner_cd") != null && !rs1.getString("owner_cd").equals("")) {
                            dobj.setOwnerCD(rs1.getString("owner_cd"));
                        }
                        if (rs1.getString("ow_descr") != null && !rs1.getString("ow_descr").equals("")) {
                            dobj.setOwnerDesc(rs1.getString("ow_descr"));
                        }
                        if (rs1.getString("isgov") != null && !rs1.getString("isgov").equals("")) {
                            dobj.setIsGOV(rs1.getString("isgov"));
                        }
                        if (rs1.getString("regn_type") != null && !rs1.getString("regn_type").equals("")) {
                            if (Util.getUserStateCode() != null && Util.getUserStateCode().equals("KA")) {
                                dobj.setRegnType("");
                            } else {
                                dobj.setRegnType(rs1.getString("regn_type"));
                            }
                        }
                        if (rs1.getString("vh_class_desc") != null && !rs1.getString("vh_class_desc").equals("")) {
                            dobj.setVchDescr(rs1.getString("vh_class_desc"));
                        }
                        if (rs1.getString("chasi_no") != null && !rs1.getString("chasi_no").equals("")) {
                            dobj.setChassiNO(rs1.getString("chasi_no"));
                        }
                        if (rs1.getString("eng_no") != null && !rs1.getString("eng_no").equals("")) {
                            dobj.setEngNO(rs1.getString("eng_no"));
                        }
                        if (rs1.getString("maker") != null && !rs1.getString("maker").equals("")) {
                            dobj.setMaker(rs1.getString("maker"));
                        }
                        if (rs1.getString("maker_name") != null && !rs1.getString("maker_name").equals("")) {
                            dobj.setMakerName(rs1.getString("maker_name"));
                        }
                        if (rs1.getString("model_name") != null && !rs1.getString("model_name").equals("")) {
                            dobj.setModelName(rs1.getString("model_name"));
                        }
                        if (rs1.getString("body_type") != null && !rs1.getString("body_type").equals("")) {
                            dobj.setBodyType(rs1.getString("body_type"));
                        }
                        if (rs1.getString("hp") != null && !rs1.getString("hp").equals("")) {
                            dobj.setHorsepwr(rs1.getString("hp"));
                        }
                        if (rs1.getString("no_cyl") != null && !rs1.getString("no_cyl").equals("")) {
                            dobj.setNoOfCyl(rs1.getString("no_cyl"));
                        }
                        if (rs1.getString("seat_cap") != null && !rs1.getString("seat_cap").equals("")) {
                            dobj.setSeatCap(rs1.getString("seat_cap"));
                        }
                        if (rs1.getString("stand_cap") != null && !rs1.getString("stand_cap").equals("")) {
                            dobj.setStandCap(rs1.getString("stand_cap"));
                        }
                        if (rs1.getString("sleeper_cap") != null && !rs1.getString("sleeper_cap").equals("")) {
                            dobj.setSleepCap(rs1.getString("sleeper_cap"));
                        }
                        if (rs1.getString("unld_wt") != null && !rs1.getString("unld_wt").equals("")) {
                            dobj.setUnLdWt(rs1.getString("unld_wt"));
                        }
                        if (rs1.getString("ld_wt") != null && !rs1.getString("ld_wt").equals("")) {
                            dobj.setLdWt(rs1.getString("ld_wt"));
                        }
                        if (rs1.getString("gcw") != null && !rs1.getString("gcw").equals("")) {
                            dobj.setGcw(rs1.getString("gcw"));
                        }
                        if (rs1.getString("fuel_descr") != null && !rs1.getString("fuel_descr").equals("")) {
                            dobj.setFuelDesc(rs1.getString("fuel_descr"));
                        }
                        if (rs1.getString("fuel") != null && !rs1.getString("fuel").equals("")) {
                            dobj.setFuel(rs1.getString("fuel"));
                        }
                        if (rs1.getString("color") != null && !rs1.getString("color").equals("")) {
                            dobj.setColor(rs1.getString("color"));
                        }
                        if (rs1.getString("manu_mon") != null && !rs1.getString("manu_mon").equals("")) {
                            dobj.setManuMnt(rs1.getString("manu_mon"));
                        }
                        if (rs1.getString("manu_yr") != null && !rs1.getString("manu_yr").equals("")) {
                            dobj.setManuYrs(rs1.getString("manu_yr"));
                        }
                        if (rs1.getString("norms_descr") != null && !rs1.getString("norms_descr").equals("")) {
                            dobj.setNormsDesc(rs1.getString("norms_descr"));
                        }
                        if (rs1.getString("wheelbase") != null && !rs1.getString("wheelbase").equals("")) {
                            dobj.setWheelBase(rs1.getString("wheelbase"));
                        }
                        if (rs1.getString("cubic_cap") != null && !rs1.getString("cubic_cap").equals("")) {
                            dobj.setCubCap(rs1.getString("cubic_cap"));
                        }
                        if (rs1.getString("floor_area") != null && !rs1.getString("floor_area").equals("")) {
                            dobj.setFloorArea(rs1.getString("floor_area"));
                        }
                        if (rs1.getString("ac_fitted") != null && !rs1.getString("ac_fitted").equals("")) {
                            dobj.setAcFitted(rs1.getString("ac_fitted"));
                        }
                        if (rs1.getString("audio_fitted") != null && !rs1.getString("audio_fitted").equals("")) {
                            dobj.setAudioFitted(rs1.getString("audio_fitted"));
                        }
                        if (rs1.getString("video_fitted") != null && !rs1.getString("video_fitted").equals("")) {
                            dobj.setVideoFitted(rs1.getString("video_fitted"));
                        }
                        if (rs1.getString("vch_purchase_as") != null && !rs1.getString("vch_purchase_as").equals("")) {
                            dobj.setVhcPurchaseAs(rs1.getString("vch_purchase_as"));
                        }
                        if (rs1.getString("vch_catg") != null && !rs1.getString("vch_catg").equals("")) {
                            dobj.setVchCatg(rs1.getString("vch_catg"));
                        }
                        if (rs1.getString("dlr_full_add") != null && !rs1.getString("dlr_full_add").equals("")) {
                            dobj.setDealerAddress(rs1.getString("dlr_full_add"));
                        }
                        if (rs1.getString("sale_amt") != null && !rs1.getString("sale_amt").equals("")) {
                            dobj.setSaleAmt(rs1.getString("sale_amt"));
                        }
                        if (rs1.getString("garage_add") != null && !rs1.getString("garage_add").equals("")) {
                            dobj.setGarageAddress(rs1.getString("garage_add"));
                        }
                        if (rs1.getString("length") != null && !rs1.getString("length").equals("")) {
                            dobj.setLength(rs1.getString("length"));
                        }
                        if (rs1.getString("width") != null && !rs1.getString("width").equals("")) {
                            dobj.setWidth(rs1.getString("width"));
                        }
                        if (rs1.getString("height") != null && !rs1.getString("height").equals("")) {
                            dobj.setHeight(rs1.getString("height"));
                        }
                        if (rs1.getString("kit_manuf") != null && !rs1.getString("kit_manuf").equals("")) {
                            dobj.setKitManuf(rs1.getString("kit_manuf"));
                        }
                        if (rs1.getString("kit_srno") != null && !rs1.getString("kit_srno").equals("")) {
                            dobj.setKitSrNo(rs1.getString("kit_srno"));
                        }
                        if (rs1.getString("kit_type") != null && !rs1.getString("kit_type").equals("")) {
                            dobj.setKitType(rs1.getString("kit_type"));
                        }
                        if (rs1.getString("fitment_dt") != null && !rs1.getString("fitment_dt").equals("")) {
                            dobj.setFitmentDate(rs1.getString("fitment_dt"));
                        }
                        if (rs1.getString("hydro_test_dt") != null && !rs1.getString("hydro_test_dt").equals("")) {
                            dobj.setHydroTestDate(rs1.getString("hydro_test_dt"));
                        }
                        if (rs1.getString("approval_no") != null && !rs1.getString("approval_no").equals("")) {
                            dobj.setApprovalNo(rs1.getString("approval_no"));
                        }
                        if (rs1.getString("approval_dt") != null && !rs1.getString("approval_dt").equals("")) {
                            dobj.setApprovalDate(rs1.getString("approval_dt"));
                        }
                        if (rs1.getString("workshop") != null && !rs1.getString("workshop").equals("")) {
                            dobj.setWorkShop(rs1.getString("workshop"));
                        }
                        if (rs1.getString("cyl_srno") != null && !rs1.getString("cyl_srno").equals("")) {
                            dobj.setCylSrNo(rs1.getString("cyl_srno"));
                        }
                        if (rs1.getDate("regn_upto") != null && !rs1.getDate("regn_upto").equals("")) {
                            dobj.setRegnUpto(rs1.getString("regn_upto"));
                        }

                        if (rs1.getDate("fit_upto") != null && !rs1.getDate("fit_upto").equals("")) {
                            dobj.setFitUpto(format.format(rs1.getDate("fit_upto")));
                        }
                        if (rs1.getString("annual_income") != null && !rs1.getString("annual_income").equals("")) {
                            dobj.setAnnulIncome(rs1.getString("annual_income"));
                        }
                        if (rs1.getString("imported_vch") != null && !rs1.getString("imported_vch").equals("")) {
                            dobj.setImpVch(rs1.getString("imported_vch"));
                        }
                        if (rs1.getString("f_axle_descp") != null && !rs1.getString("f_axle_descp").equals("")) {
                            dobj.setFaxlDesc(rs1.getString("f_axle_descp"));
                        }
                        if (rs1.getString("r_axle_descp") != null && !rs1.getString("r_axle_descp").equals("")) {
                            dobj.setRaxlDesc(rs1.getString("r_axle_descp"));
                        }
                        if (rs1.getString("o_axle_descp") != null && !rs1.getString("o_axle_descp").equals("")) {
                            dobj.setOaxlDesc(rs1.getString("o_axle_descp"));
                        }
                        if (rs1.getString("t_axle_descp") != null && !rs1.getString("t_axle_descp").equals("")) {
                            dobj.setTaxlDesc(rs1.getString("t_axle_descp"));
                        }
                        if (rs1.getString("f_axle_weight") != null && !rs1.getString("f_axle_weight").equals("")) {
                            dobj.setFaxlWeight(rs1.getString("f_axle_weight"));
                        }
                        if (rs1.getString("r_axle_weight") != null && !rs1.getString("r_axle_weight").equals("")) {
                            dobj.setRaxlWeight(rs1.getString("r_axle_weight"));
                        }
                        if (rs1.getString("o_axle_weight") != null && !rs1.getString("o_axle_weight").equals("")) {
                            dobj.setOaxlWeight(rs1.getString("o_axle_weight"));
                        }
                        if (rs1.getString("t_axle_weight") != null && !rs1.getString("t_axle_weight").equals("")) {
                            dobj.setTaxlWeight(rs1.getString("t_axle_weight"));
                        }
                        if (rs1.getString("tr_chasi_no") != null && !rs1.getString("tr_chasi_no").equals("")) {
                            dobj.setTrChasiNO(rs1.getString("tr_chasi_no"));
                        }
                        if (rs1.getString("tr_body_type") != null && !rs1.getString("tr_body_type").equals("")) {
                            dobj.setTrBodyType(rs1.getString("tr_body_type"));
                        }
                        if (rs1.getString("tr_ld_wt") != null && !rs1.getString("tr_ld_wt").equals("")) {
                            dobj.setTrLdWt(rs1.getString("tr_ld_wt"));
                        }
                        if (rs1.getString("tr_unld_wt") != null && !rs1.getString("tr_unld_wt").equals("")) {
                            dobj.setTrUnldWt(rs1.getString("tr_unld_wt"));
                        }
                        if (rs1.getString("tr_f_axle_descp") != null && !rs1.getString("tr_f_axle_descp").equals("")) {
                            dobj.setTrFAxlDesc(rs1.getString("tr_f_axle_descp"));
                        }
                        if (rs1.getString("tr_r_axle_descp") != null && !rs1.getString("tr_r_axle_descp").equals("")) {
                            dobj.setTrRAxlDesc(rs1.getString("tr_r_axle_descp"));
                        }
                        if (rs1.getString("tr_o_axle_descp") != null && !rs1.getString("tr_o_axle_descp").equals("")) {
                            dobj.setTrOAxlDesc(rs1.getString("tr_o_axle_descp"));
                        }
                        if (rs1.getString("tr_t_axle_descp") != null && !rs1.getString("tr_t_axle_descp").equals("")) {
                            dobj.setTrTAxlDesc(rs1.getString("tr_t_axle_descp"));
                        }
                        if (rs1.getString("tr_f_axle_weight") != null && !rs1.getString("tr_f_axle_weight").equals("")) {
                            dobj.setTrFAxlWeight(rs1.getString("tr_f_axle_weight"));
                        }
                        if (rs1.getString("tr_r_axle_weight") != null && !rs1.getString("tr_r_axle_weight").equals("")) {
                            dobj.setTrRAxlWeight(rs1.getString("tr_r_axle_weight"));
                        }
                        if (rs1.getString("tr_o_axle_weight") != null && !rs1.getString("tr_o_axle_weight").equals("")) {
                            dobj.setTrOAxlWeight(rs1.getString("tr_o_axle_weight"));
                        }
                        if (rs1.getString("tr_t_axle_weight") != null && !rs1.getString("tr_t_axle_weight").equals("")) {
                            dobj.setTrTAxlWeight(rs1.getString("tr_t_axle_weight"));
                        }
                        if (rs1.getDate("hypth_from_dt") != null && !rs1.getDate("hypth_from_dt").equals("")) {
                            dobj.setHypthFromDT(format.format(rs1.getDate("hypth_from_dt")));
                        }
                        if (rs1.getString("sr_no") != null && !rs1.getString("sr_no").equals("")) {
                            dobj.setSrNO(rs1.getString("sr_no"));
                        }
                        if (rs1.getString("fncr_full_add") != null && !rs1.getString("fncr_full_add").equals("")) {
                            dobj.setFncrAddress(rs1.getString("fncr_full_add"));
                        }
                        if (rs1.getString("tax_from") != null && !rs1.getString("tax_from").equals("")) {
                            dobj.setTaxFromDT(rs1.getString("tax_from"));
                        }
                        if (rs1.getString("tax_upto") != null && !rs1.getString("tax_upto").equals("")) {
                            dobj.setTaxUpto(rs1.getString("tax_upto"));
                        }
                        if (rs1.getString("tax_amt") != null && !rs1.getString("tax_amt").equals("")) {
                            dobj.setTaxAmt(rs1.getString("tax_amt"));
                        }
                        if (rs1.getString("rcpt_no") != null && !rs1.getString("rcpt_no").equals("")) {
                            dobj.setRcptNO(rs1.getString("rcpt_no"));
                        }
                        if (rs1.getDate("op_dt") != null && !rs1.getDate("op_dt").equals("")) {;
                            dobj.setOpDT(String.valueOf(rs1.getDate("op_dt")));
                        }
                        if (rs1.getString("ins_type") != null && !rs1.getString("ins_type").equals("")) {
                            dobj.setInsType(rs1.getString("ins_type"));
                        }
                        if (rs1.getString("company_name") != null && !rs1.getString("company_name").equals("")) {
                            dobj.setInsCompany(rs1.getString("company_name"));
                        }
                        if (rs1.getString("policy_no") != null && !rs1.getString("policy_no").equals("")) {
                            dobj.setPolicyNO(rs1.getString("policy_no"));
                        }
                        if (rs1.getDate("ins_from") != null && !rs1.getDate("ins_from").equals("")) {
                            dobj.setInsFrom(format.format(rs1.getDate("ins_from")));
                        }
                        if (rs1.getDate("ins_upto") != null && !rs1.getDate("ins_upto").equals("")) {
                            dobj.setInsUpto(format.format(rs1.getDate("ins_upto")));
                        }

                        if (rs1.getDate("regn_dt") != null && !rs1.getDate("regn_dt").equals("")) {;
                            dobj.setRegnDate(String.valueOf(rs1.getDate("regn_dt")));
                        }

                        if (rs1.getDate("op_dt") != null && !rs1.getDate("op_dt").equals("")) {;
                            dobj.setOpDate(format.format(rs1.getDate("op_dt")));
                        }

                        if (rs1.getString("reten_regn_no").equals("RETEN NO NOT ASSIGN") && rs1.getString("advance_regn_no").equals("FANCY NO NOT ASSIGN")) {
                            dobj.setIsAdvanceRegnNoAssign(true);
                            dobj.setAdvanceRegnNo("FANCY NO NOT ASSIGN");
                            dobj.setRetenRegnNo("RETEN NO NOT ASSIGN");
                        }

                        if (!rs1.getString("reten_regn_no").equals("RETEN NO NOT ASSIGN")) {
                            dobj.setAdvanceRegnNo("FANCY NO NOT ASSIGN");
                            dobj.setRetenRegnNo(rs1.getString("reten_regn_no"));
                        }
                        if (!rs1.getString("advance_regn_no").equals("FANCY NO NOT ASSIGN")) {
                            dobj.setRetenRegnNo("RETEN NO NOT ASSIGN");
                            dobj.setAdvanceRegnNo(rs1.getString("advance_regn_no"));
                        }

                        if (rs1.getString("pan_no") != null && !rs1.getString("pan_no").equals("")) {
                            dobj.setPanNo(rs1.getString("pan_no"));
                        }
                        if (rs1.getString("aadhar_no") != null && !rs1.getString("aadhar_no").equals("")) {
                            dobj.setAadharNo(rs1.getString("aadhar_no"));
                        }
                        if (rs1.getString("voter_id") != null && !rs1.getString("voter_id").equals("")) {
                            dobj.setVoterId(rs1.getString("voter_id"));
                        }
                        if (rs1.getString("passport_no") != null && !rs1.getString("passport_no").equals("")) {
                            dobj.setPassportNo(rs1.getString("passport_no"));
                        }
                        // } commented by Afzal on 06/04/2016
                        tmgr = new TransactionManager("getOwnerDisclaimerDobjForRegisteredVehicle-3");
                        sql = "SELECT ins_company_name, ins_type, ins_type_descr, \n"
                                + " ins_from, ins_upto, policy_no\n"
                                + "  FROM vva_insurance where regn_no = ?";

                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, rs.getString("regn_no"));
                        RowSet rs4 = tmgr.fetchDetachedRowSet();
                        if (rs4.next()) {
                            if (rs4.getString("ins_type_descr") != null && !rs4.getString("ins_type_descr").equals("")) {
                                dobj.setInsType(rs4.getString("ins_type_descr"));
                            }
                            if (rs4.getString("ins_company_name") != null && !rs4.getString("ins_company_name").equals("")) {
                                dobj.setInsCompany(rs4.getString("ins_company_name"));
                            }
                            if (rs4.getString("policy_no") != null && !rs4.getString("policy_no").equals("")) {
                                dobj.setPolicyNO(rs4.getString("policy_no"));
                            }
                            if (rs4.getDate("ins_from") != null && !rs4.getDate("ins_from").equals("")) {
                                dobj.setInsFrom(format.format(rs4.getDate("ins_from")));
                            }
                            if (rs4.getDate("ins_upto") != null && !rs4.getDate("ins_upto").equals("")) {
                                dobj.setInsUpto(format.format(rs4.getDate("ins_upto")));
                            }
                        }
                        if (!printType.equals("provisionalRCAtRtoLevel")) {
                            do {
                                if (rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_CHG_ADD) {
                                    tmgr = new TransactionManager("getOwnerDisclaimerDobjForRegisteredVehicle-4");
                                    sql = "select upper(c_add1 || ', ' || c_add2 || ', ' || c_add3 || ', ' || c_district_name || '-' || c_state_name || '-' || c_pincode) as c_full_addd,\n"
                                            + "upper(p_add1 || ', ' || p_add2 || ', ' || p_add3 || ', ' ||  p_district_name || ', ' || p_state_name || '-' || p_pincode) as p_full_addd,from_dt \n"
                                            + "from " + TableList.VVA_CA + " where appl_no = ? ";
                                    ps = tmgr.prepareStatement(sql);
                                    ps.setString(1, appl_no);
                                    RowSet rs2 = tmgr.fetchDetachedRowSet();
                                    if (rs2.next()) {
                                        if (rs2.getString("c_full_addd") != null && !rs2.getString("c_full_addd").equals("")) {
                                            dobj.setCurrAddress(rs2.getString("c_full_addd"));
                                        }
                                        if (rs2.getString("p_full_addd") != null && !rs2.getString("p_full_addd").equals("")) {
                                            dobj.setPerAddress(rs2.getString("p_full_addd"));
                                        }
                                        if (rs2.getDate("from_dt") != null && !rs2.getDate("from_dt").equals("")) {
                                            dobj.setCaWef(format.format(rs2.getDate("from_dt")));
                                        }
                                    } else {
                                        throw new VahanException("" + rs.getString("descr") + " - " + errMsg);
                                    }
                                }
                                if (rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_TO) {
                                    tmgr = new TransactionManager("getOwnerDisclaimerDobjForRegisteredVehicle-5");
                                    sql = "select a.f_name, a.owner_name,a.owner_sr,a.owner_cd,a.owner_ctg,g.descr as ow_descr,a.sale_amt, upper(a.c_add1 || ', ' || a.c_add2 || ', ' || a.c_add3 || ', ' || a.c_district_name || '-' || a.c_state_name || '-' || a.c_pincode) as c_full_addd,\n"
                                            + "  upper(a.p_add1 || ', ' || a.p_add2 || ', ' || a.p_add3 || ', ' ||  a.p_district_name || ', ' || a.p_state_name || '-' || a.p_pincode) as p_full_addd,sale_dt , reason \n"
                                            + "from " + TableList.VVA_TO + " a "
                                            + " LEFT OUTER JOIN vm_owcode g ON g.ow_code=a.owner_cd"
                                            + " where a.appl_no = ? ";
                                    ps = tmgr.prepareStatement(sql);
                                    ps.setString(1, appl_no);
                                    RowSet rs2 = tmgr.fetchDetachedRowSet();
                                    if (rs2.next()) {
                                        if (rs2.getString("owner_name") != null && !rs2.getString("owner_name").equals("")) {
                                            dobj.setOwnerName(rs2.getString("owner_name"));
                                        }
                                        if (rs2.getString("f_name") != null && !rs2.getString("f_name").equals("")) {
                                            dobj.setFname(rs2.getString("f_name"));
                                        }
                                        if (rs2.getString("ow_descr") != null && !rs2.getString("ow_descr").equals("")) {
                                            dobj.setOwnerDesc(rs2.getString("ow_descr"));
                                        }
                                        if (rs2.getString("sale_amt") != null && !rs2.getString("sale_amt").equals("")) {
                                            dobj.setSaleAmt(rs2.getString("sale_amt"));
                                        }
                                        if (rs2.getString("c_full_addd") != null && !rs2.getString("c_full_addd").equals("")) {
                                            dobj.setCurrAddress(rs2.getString("c_full_addd"));
                                        }
                                        if (rs2.getString("p_full_addd") != null && !rs2.getString("p_full_addd").equals("")) {
                                            dobj.setPerAddress(rs2.getString("p_full_addd"));
                                        }
                                        if (rs2.getDate("sale_dt") != null && !rs2.getDate("sale_dt").equals("")) {
                                            dobj.setToWef(format.format(rs2.getDate("sale_dt")));
                                        }
                                        if (rs2.getString("reason") != null && !rs2.getString("reason").equals("")) {
                                            dobj.setToReason(rs2.getString("reason"));
                                        }
                                    } else {
                                        throw new VahanException("" + rs.getString("descr") + "- " + errMsg);
                                    }
                                }
                                if (rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_REM_HYPO) {
                                    tmgr = new TransactionManager("getOwnerDisclaimerDobjForRegisteredVehicle-6");
                                    sql = "SELECT (f.fncr_name || ', ' || f.fncr_add1 || ', ' || f.fncr_add2 || ', ' || f.fncr_add3 || ', ' ||  f.fncr_district || ', ' || f.fncr_district_name || ', ' || f.fncr_state || ', ' || f.fncr_state_name || ', ' || f.fncr_pincode) as fncr_full_add "
                                            + "from vva_hpt f where f.appl_no = ? ";
                                    ps = tmgr.prepareStatement(sql);
                                    ps.setString(1, appl_no);
                                    RowSet rs2 = tmgr.fetchDetachedRowSet();
                                    if (rs2.next()) {
                                        if (rs2.getString("fncr_full_add") != null && !rs2.getString("fncr_full_add").equals("")) {
                                            dobj.setFncrAddressTermination(rs2.getString("fncr_full_add"));
                                        }
                                    } else {
                                        throw new VahanException("" + rs.getString("descr") + " - " + errMsg);
                                    }

                                }
                                if (rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO) {
                                    tmgr = new TransactionManager("getOwnerDisclaimerDobjForRegisteredVehicle-7");
                                    sql = "SELECT (f.fncr_name || ', ' || f.fncr_add1 || ', ' || f.fncr_add2 || ', ' || f.fncr_add3 || ', ' ||  f.fncr_district || ', ' || f.fncr_district_name || ', ' || f.fncr_state || ', ' || f.fncr_state_name || ', ' || f.fncr_pincode) as fncr_full_add "
                                            + "from vva_hpa f where f.appl_no = ? ";

                                    ps = tmgr.prepareStatement(sql);
                                    ps.setString(1, appl_no);
                                    RowSet rs2 = tmgr.fetchDetachedRowSet();
                                    if (rs2.next()) {
                                        if (rs2.getString("fncr_full_add") != null && !rs2.getString("fncr_full_add").equals("")) {
                                            dobj.setFncrAddressAddition(rs2.getString("fncr_full_add"));
                                        }
                                    } else {
                                        throw new VahanException("" + rs.getString("descr") + "- " + errMsg);
                                    }
                                }
                                if (rs.getInt("pur_cd") == TableConstants.VM_TRANSACTION_MAST_VEH_ALTER) {
                                    tmgr = new TransactionManager("getOwnerDisclaimerDobjForRegisteredVehicle-8");
                                    sql = "SELECT a.chasi_no,a.eng_no,g.descr as vh_class_desc , a.body_type, a.no_cyl, a.hp, a.seat_cap, a.stand_cap, a.sleeper_cap, a.unld_wt, \n"
                                            + " a.ld_wt, a.gcw, a.fuel,vfh.descr as fuel_descr , a.color, a.wheelbase, a.cubic_cap, a.floor_area, a.ac_fitted, \n"
                                            + " a.audio_fitted, a.video_fitted, a.vch_catg, a.length, a.width, a.height\n"
                                            + " FROM va_alt a  \n"
                                            + " LEFT JOIN  vm_vh_class g ON g.vh_class = a.vh_class\n"
                                            + " LEFT JOIN vm_fuel vfh ON vfh.code = a.fuel\n"
                                            + " where a.appl_no = ? ";
                                    ps = tmgr.prepareStatement(sql);
                                    ps.setString(1, appl_no);
                                    RowSet rs2 = tmgr.fetchDetachedRowSet();
                                    if (rs2.next()) {
                                        if (rs2.getString("chasi_no") != null && !rs2.getString("chasi_no").equals("")) {
                                            dobj.setChassiNO(rs2.getString("chasi_no"));
                                        }
                                        if (rs2.getString("eng_no") != null && !rs2.getString("eng_no").equals("")) {
                                            dobj.setEngNO(rs2.getString("eng_no"));
                                        }
                                        if (rs2.getString("vh_class_desc") != null && !rs2.getString("vh_class_desc").equals("")) {
                                            dobj.setVchDescr(rs2.getString("vh_class_desc"));
                                        }
                                        if (rs2.getString("body_type") != null && !rs2.getString("body_type").equals("")) {
                                            dobj.setBodyType(rs2.getString("body_type"));
                                        }
                                        if (rs2.getString("hp") != null && !rs2.getString("hp").equals("")) {
                                            dobj.setHorsepwr(rs2.getString("hp"));
                                        }
                                        if (rs2.getString("no_cyl") != null && !rs2.getString("no_cyl").equals("")) {
                                            dobj.setNoOfCyl(rs2.getString("no_cyl"));
                                        }
                                        if (rs2.getString("seat_cap") != null && !rs2.getString("seat_cap").equals("")) {
                                            dobj.setSeatCap(rs2.getString("seat_cap"));
                                        }
                                        if (rs2.getString("stand_cap") != null && !rs2.getString("stand_cap").equals("")) {
                                            dobj.setStandCap(rs2.getString("stand_cap"));
                                        }
                                        if (rs2.getString("sleeper_cap") != null && !rs2.getString("sleeper_cap").equals("")) {
                                            dobj.setSleepCap(rs2.getString("sleeper_cap"));
                                        }
                                        if (rs2.getString("unld_wt") != null && !rs2.getString("unld_wt").equals("")) {
                                            dobj.setUnLdWt(rs2.getString("unld_wt"));
                                        }
                                        if (rs2.getString("ld_wt") != null && !rs2.getString("ld_wt").equals("")) {
                                            dobj.setLdWt(rs2.getString("ld_wt"));
                                        }
                                        if (rs2.getString("gcw") != null && !rs2.getString("gcw").equals("")) {
                                            dobj.setGcw(rs2.getString("gcw"));
                                        }
                                        if (rs2.getString("fuel_descr") != null && !rs2.getString("fuel_descr").equals("")) {
                                            dobj.setFuelDesc(rs2.getString("fuel_descr"));
                                        }
                                        if (rs2.getString("color") != null && !rs2.getString("color").equals("")) {
                                            dobj.setColor(rs2.getString("color"));
                                        }
                                        if (rs2.getString("wheelbase") != null && !rs2.getString("wheelbase").equals("")) {
                                            dobj.setWheelBase(rs2.getString("wheelbase"));
                                        }
                                        if (rs2.getString("cubic_cap") != null && !rs2.getString("cubic_cap").equals("")) {
                                            dobj.setCubCap(rs2.getString("cubic_cap"));
                                        }
                                        if (rs2.getString("floor_area") != null && !rs2.getString("floor_area").equals("")) {
                                            dobj.setFloorArea(rs2.getString("floor_area"));
                                        }
                                        if (rs2.getString("ac_fitted") != null && !rs2.getString("ac_fitted").equals("")) {
                                            dobj.setAcFitted(rs2.getString("ac_fitted"));
                                        }
                                        if (rs2.getString("audio_fitted") != null && !rs2.getString("audio_fitted").equals("")) {
                                            dobj.setAudioFitted(rs2.getString("audio_fitted"));
                                        }
                                        if (rs2.getString("video_fitted") != null && !rs2.getString("video_fitted").equals("")) {
                                            dobj.setVideoFitted(rs2.getString("video_fitted"));
                                        }
                                        if (rs2.getString("vch_catg") != null && !rs2.getString("vch_catg").equals("")) {
                                            dobj.setVchCatg(rs2.getString("vch_catg"));
                                        }
                                        if (rs2.getString("length") != null && !rs2.getString("length").equals("")) {
                                            dobj.setLength(rs2.getString("length"));
                                        }
                                        if (rs2.getString("width") != null && !rs2.getString("width").equals("")) {
                                            dobj.setWidth(rs2.getString("width"));
                                        }
                                        if (rs2.getString("height") != null && !rs2.getString("height").equals("")) {
                                            dobj.setHeight(rs2.getString("height"));
                                        }
                                    } else {
                                        throw new VahanException("" + rs.getString("descr") + " - " + errMsg);
                                    }
                                    tmgr = new TransactionManager("getOwnerDisclaimerDobjForRegisteredVehicle-9");
                                    sql = "SELECT appl_no, kit_srno, kit_type, kit_manuf, kit_pucc_norms, workshop, \n"
                                            + " workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, approval_no, \n"
                                            + " approval_dt \n"
                                            + " FROM va_retrofitting_dtls where appl_no = ? ";
                                    ps = tmgr.prepareStatement(sql);
                                    ps.setString(1, appl_no);
                                    RowSet rs3 = tmgr.fetchDetachedRowSet();
                                    if (rs3.next()) {
                                        if (rs3.getString("kit_manuf") != null && !rs3.getString("kit_manuf").equals("")) {
                                            dobj.setKitManuf(rs3.getString("kit_manuf"));
                                        }
                                        if (rs3.getString("kit_srno") != null && !rs3.getString("kit_srno").equals("")) {
                                            dobj.setKitSrNo(rs3.getString("kit_srno"));
                                        }
                                        if (rs3.getString("kit_type") != null && !rs3.getString("kit_type").equals("")) {
                                            dobj.setKitType(rs3.getString("kit_type"));
                                        }
                                        if (rs3.getString("fitment_dt") != null && !rs3.getString("fitment_dt").equals("")) {
                                            dobj.setFitmentDate(rs3.getString("fitment_dt"));
                                        }
                                        if (rs3.getString("hydro_test_dt") != null && !rs3.getString("hydro_test_dt").equals("")) {
                                            dobj.setHydroTestDate(rs3.getString("hydro_test_dt"));
                                        }
                                        if (rs3.getString("approval_no") != null && !rs3.getString("approval_no").equals("")) {
                                            dobj.setApprovalNo(rs3.getString("approval_no"));
                                        }
                                        if (rs3.getString("approval_dt") != null && !rs3.getString("approval_dt").equals("")) {
                                            dobj.setApprovalDate(rs3.getString("approval_dt"));
                                        }
                                        if (rs3.getString("workshop") != null && !rs3.getString("workshop").equals("")) {
                                            dobj.setWorkShop(rs3.getString("workshop"));
                                        }
                                        if (rs3.getString("cyl_srno") != null && !rs3.getString("cyl_srno").equals("")) {
                                            dobj.setCylSrNo(rs3.getString("cyl_srno"));
                                        }
                                    }
                                }
                            } while (rs.next());
                        }
                    } else {
                        throw new VahanException("This Application No belong to NEW Vehicle, Please search Owner Disclaimer/Details from the NEW VEHCILE Option below....");
                    }
                }
            } else {
                throw new VahanException("Invalid Application Number");
            }
        } catch (VahanException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Appl No:" + appl_no + "-" + e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return dobj;
    }

    public static String printOwnerDiscReport(String category, String reportEntry) {

        Map map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String appl_no = (String) map.get("APPL_NO");
        Map mapReport = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        if (appl_no.equals("null")) {
            appl_no = (String) mapReport.get("appl");
        }
        String regn_no = (String) map.get("REGN_NO");
        String pur_cd = (String) map.get("PUR_CD");
        if (pur_cd != null && Integer.parseInt(pur_cd) != 0) {
            Util.getSelectedSeat().setPur_cd(Integer.parseInt(pur_cd));
        }
        //String reportEntry = "entryFormat";
        String printType = "disclaimer";
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("applNo", appl_no);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("regnNo", regn_no);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("category", category);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("reportEntry", reportEntry);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("printType", printType);
        if (!"registeredVehicles".equals(category)) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("purCd", pur_cd);
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("purCd", "0");
        }
        return "OwnerDisclaimerReport";
    }

    public static NOCReportDobj getNOCReportDobj(String appl_no) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        NOCReportDobj dobj = null;
        Map<String, String> taxPaid = new HashMap<>();
        Date taxPaidUpto = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        TmConfigurationDobj configurationDobj = Util.getTmConfiguration();
        try {
            String sql = "select a.state_cd,a.state_to,to_char(b.regn_dt,'dd-MON-yyyy') as regn_dt,b.eng_no,b.chasi_no,b.owner_name,b.off_cd,b.off_name,b.state_name,a.appl_no,a.regn_no,c.descr to_state_name,a.off_to,g.descr,a.new_owner, "
                    + " COALESCE(t.off_name, 'NA') as to_office_name,a.rto_to,a.ncrb_ref,a.dispatch_no,a.noc_no,to_char(a.noc_dt,'dd-MON-yyyy') as noc_dt,e.rcpt_heading,e.rcpt_subheading,to_char(current_timestamp,'dd-Mon-yyyy hh24:mi:ss') as printed_on,"
                    + " (f.fncr_name || ', ' || f.fncr_add1 || ', ' || f.fncr_add2 || ', ' || f.fncr_add3 || ', ' || chr(10) || f.fncr_district_name || ', ' || f.fncr_state || ', ' || f.fncr_state_name || ', ' || f.fncr_pincode) as fncr_full_add,j.doc_sign as userSign"
                    + " from vt_noc a"
                    + " left join vv_owner b on b.regn_no=a.regn_no and a.state_cd = b.state_cd and a.off_cd = b.off_cd "
                    + " LEFT JOIN tm_office t ON  t.off_cd = a.off_to and t.state_cd=a.state_to"
                    + " LEFT JOIN tm_state c ON c.state_code = a.state_to::bpchar"
                    + " LEFT JOIN vt_tax d on d.regn_no=a.regn_no and a.state_cd = d.state_cd and a.off_cd = d.off_cd and d.rcpt_dt=(select max(rcpt_dt) from vt_tax where regn_no=a.regn_no and a.state_cd = vt_tax.state_cd and a.off_cd = vt_tax.off_cd )"
                    + " LEFT JOIN tm_configuration e ON e.state_cd = a.state_cd"
                    + " LEFT JOIN vv_hypth f ON f.regn_no = a.regn_no and f.state_cd = a.state_cd and f.off_cd = a.off_cd "
                    + " LEFT JOIN vm_noc_reason g ON g.code = a.reason_cd "
                    + " LEFT OUTER JOIN  (select * from va_details where appl_no = ?  order by confirm_date desc limit 1) h on h.regn_no=a.regn_no \n"
                    + " LEFT OUTER JOIN (select * from vha_status  where appl_no = ? order by moved_on desc limit 1) i on  i.appl_no = h.appl_no and i.pur_cd = h.pur_cd \n"
                    + " LEFT OUTER JOIN " + TableList.TM_USER_SIGN + " j ON j.user_cd = i.emp_cd \n"
                    + " where a.appl_no=? and  a.state_cd = ? and a.off_cd = ? ";
            tmgr = new TransactionManagerReadOnly("getNOCReportDobj");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, appl_no);
            ps.setString(3, appl_no);
            ps.setString(4, Util.getUserStateCode());
            ps.setInt(5, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                dobj = new NOCReportDobj();
                if (rs.getString("descr") != null && !rs.getString("descr").equals("")) {
                    dobj.setDescr(rs.getString("descr"));
                    dobj.setRender_Descr(true);
                } else {
                    dobj.setRender_Descr(false);
                }
                if (rs.getString("new_owner") != null && !rs.getString("new_owner").equals("")) {
                    dobj.setNew_own_name(rs.getString("new_owner"));
                    dobj.setRender_newName(true);
                } else {
                    dobj.setRender_newName(false);
                }
                if (rs.getString("fncr_full_add") != null && !rs.getString("fncr_full_add").equals("")) {
                    dobj.setFncr_full_add(rs.getString("fncr_full_add"));
                    dobj.setIshypothecated(true);
                } else {
                    dobj.setIshypothecated(false);
                }
                if (rs.getString("regn_no") != null && !rs.getString("regn_no").equals("")) {
                    dobj.setRegn_no(rs.getString("regn_no"));
                }
                if (rs.getString("regn_dt") != null && !rs.getString("regn_dt").equals("")) {
                    dobj.setRegn_dt(rs.getString("regn_dt"));
                }
                if (rs.getString("eng_no") != null && !rs.getString("eng_no").equals("")) {
                    dobj.setEng_no(rs.getString("eng_no"));
                }
                if (rs.getString("chasi_no") != null && !rs.getString("chasi_no").equals("")) {
                    dobj.setChassi_no(rs.getString("chasi_no"));
                }
                if (rs.getString("to_state_name") != null && !rs.getString("to_state_name").equals("")) {
                    dobj.setState_to(rs.getString("to_state_name"));
                }
                if (rs.getString("to_office_name").equals("NA")) {
                    dobj.setRto_to(rs.getString("rto_to"));
                } else {
                    dobj.setRto_to(rs.getString("to_office_name"));
                }
                if (rs.getString("dispatch_no") != null && !rs.getString("dispatch_no").equals("")) {
                    dobj.setDispatch_no(rs.getString("dispatch_no"));
                }
                if (rs.getString("ncrb_ref") != null && !rs.getString("ncrb_ref").equals("")) {
                    dobj.setNoc_ref_no(rs.getString("ncrb_ref"));
                }
                if (rs.getString("noc_dt") != null && !rs.getString("noc_dt").equals("")) {
                    dobj.setNoc_issue_dt(rs.getString("noc_dt"));
                }
                if (rs.getString("off_name") != null && !rs.getString("off_name").equals("")) {
                    dobj.setOffName(rs.getString("off_name"));
                }
                if (rs.getString("state_name") != null && !rs.getString("state_name").equals("")) {
                    dobj.setStateName(rs.getString("state_name"));
                }
                if (rs.getString("noc_no") != null && !rs.getString("noc_no").equals("")) {
                    dobj.setNoc_no(rs.getString("noc_no"));
                }
                if (rs.getString("state_cd") != null && !rs.getString("state_cd").equals("") && rs.getString("state_to") != null && !rs.getString("state_to").equals("")) {
                    if (rs.getString("state_cd").equalsIgnoreCase(rs.getString("state_to"))) {
                        if ("GJ".contains(rs.getString("state_to"))) {
                            dobj.setNoc_cc_lable("Vehicle Details for another Office");
                            dobj.setNoc_cc_fieldlable("VDO Number");
                        } else if ("OR".contains(rs.getString("state_to"))) {
                            dobj.setNoc_cc_lable("Tax Clearance Certificate");
                            dobj.setNoc_cc_fieldlable("TCC No");
                        } else {
                            dobj.setNoc_cc_lable("Clearance Certificate");
                            dobj.setNoc_cc_fieldlable("CC Number");
                        }

                    } else {
                        dobj.setNoc_cc_lable("No Objection Certificate");
                        dobj.setNoc_cc_fieldlable("NOC Number");
                    }
                }
                dobj.setHeader(rs.getString("rcpt_heading"));
                dobj.setSubHeader(rs.getString("rcpt_subheading"));
                dobj.setPrintDate(rs.getString("printed_on"));
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
                taxPaid = ServerUtil.taxPaidInfo(false, rs.getString("regn_no"), 58);
                if (!taxPaid.isEmpty()) {
                    for (Map.Entry<String, String> entry : taxPaid.entrySet()) {
                        if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("tax_mode") && entry.getValue() != null) {
                            if (!entry.getValue().isEmpty() && "L,O".contains(entry.getValue())) {
                                dobj.setTax_valid_upto("One Time");
                                break;
                            }
                        } else if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("tax_upto") && entry.getValue() != null) {
                            taxPaidUpto = DateUtils.parseDate(entry.getValue());
                            dobj.setTax_valid_upto(format.format(taxPaidUpto));
                            break;
                        }
                    }
                }
                if (rs.getBytes("userSign") != null && !rs.getBytes("userSign").equals("")) {
                    dobj.setUserSign(rs.getBytes("userSign"));
                    dobj.setIsUserSignExist(true);
                } else {
                    dobj.setIsUserSignExist(false);
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
        return dobj;

    }

    public static List<TCPrintDobj> getIssuedTradeCertificatesListToBePrinted() throws VahanException {
        List<TCPrintDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        VahanException vahanexecption = null;
        try {
            tmgr = new TransactionManagerReadOnly("getIssuedTradeCertificatesListToBePrinted");
            ps = tmgr.prepareStatement("Select distinct a.appl_no,a.cert_no,b.state_cd,b.off_cd"
                    + " from " + TableList.VA_TC_PRINT + " a , " + TableList.VT_TRADE_CERTIFICATE + " b "
                    + " where b.state_cd = ? and b.off_cd = ? "
                    + " order by  a.appl_no ");

            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                TCPrintDobj dobj = new TCPrintDobj();
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setTcNo(rs.getString("cert_no"));
                dobj.setOffCd(rs.getInt("off_cd"));
                dobj.setStateCd(rs.getString("state_cd"));

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

    public static void deleteAndSaveHistoryTC(String applNo, String certNo) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        int i = 0;
        try {
            tmgr = new TransactionManager("deleteAndSaveHistoryTC");
            String sqlQry = "insert into " + TableList.VH_TC_PRINT + " select state_cd,off_cd,appl_no,cert_no,op_dt,current_timestamp, ? from " + TableList.VA_TC_PRINT + " where appl_no = ? and cert_no = ?";
            ps = tmgr.prepareStatement(sqlQry);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, applNo);
            ps.setString(3, certNo);
            i = ps.executeUpdate();
            if (i > 0) {
                sqlQry = "delete from " + TableList.VA_TC_PRINT + " where appl_no = ? and cert_no = ?";
                ps = tmgr.prepareStatement(sqlQry);
                ps.setString(1, applNo);
                ps.setString(2, certNo);
                i = ps.executeUpdate();

                if (i > 0) {
                    tmgr.commit();
                }

            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Unable to save and delete history for Trade Certificate.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }

    }

    public String getApplNoFromVaRcPrint(String regn_no) {

        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        String appl_no = null;

        try {

            if (regn_no != null) {
                regn_no = regn_no.toUpperCase();
            }

            tmgr = new TransactionManagerReadOnly("getApplNoFromVaRcPrint");
            sql = "SELECT appl_no FROM " + TableList.VA_RC_PRINT + " WHERE regn_no=? and state_cd=? and off_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                appl_no = rs.getString("appl_no");
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
        return appl_no;
    }

    public static ArrayList<PrintCertificatesDobj> getTempPrintDocsDealerDetails(int pur_cd, String dealerCd, String state_cd, int off_cd) throws VahanException {
        ArrayList<PrintCertificatesDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        VahanException vahanexecption = null;
        try {
            tmgr = new TransactionManagerReadOnly("getTempPrintDocsDetails");
            ps = tmgr.prepareStatement("select distinct a.appl_no, a.temp_regn_no as regn_no, b.state_cd, b.off_cd,a.op_dt"
                    + " from " + TableList.VA_TEMP_RC_PRINT + " a, " + TableList.VA_DETAILS + " b"
                    + " where a.appl_no = b.appl_no and b.state_cd = ? and b.off_cd = ? and b.pur_cd IN (?,?) and a.dealer_cd =  ? and a.op_dt > current_date - 15 order by a.op_dt DESC");
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setInt(3, pur_cd);
            ps.setInt(4, TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE);
            ps.setString(5, dealerCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                PrintCertificatesDobj dobj = new PrintCertificatesDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegno(rs.getString("regn_no"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setState_cd(rs.getString("state_cd"));
                list.add(dobj);
            }
        } catch (Exception ex) {
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
    //function to print challan report

    public static ChallanReportDobj printChallanReport(String appl_no) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        ChallanReportDobj dobj = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        try {
            dobj = new ChallanReportDobj();
            String sql = " select owner.*,permit.pmt_no,permit.valid_upto,tax.tax_upto,referToCourt.hearing_date,court.court_name,court.magistrate_name,\n"
                    + " challan.chal_no, fitness.fit_valid_to,insurance.ins_upto,vh_class.descr,challan.chal_date,off.off_name,challan.chal_time,challan.chal_place,challan.remarks,challan.comming_from,challan.going_to\n"
                    + ",amt.cmpd_amt,vt_owner.owner_name,vt_owner.c_add1,vt_owner.c_add2,vt_owner.c_add3,vt_owner.c_pincode,amt.settlement_amt,amt.adv_amt,amt.adv_rcpt_no,info.user_name from "
                    + TableList.VT_CHALLAN_OWNER + " owner\n"
                    + " left outer join " + TableList.VIEW_VV_OWNER + " vt_owner on vt_owner.regn_no=owner.regn_no  \n"
                    + " left outer join " + TableList.VT_CHALLAN_TAX + " tax on tax.appl_no=owner.appl_no and  tax.state_cd=owner.state_cd \n"
                    + " left outer join " + TableList.VT_CHALLAN_AMT + " amt on amt.appl_no=owner.appl_no and  amt.state_cd=owner.state_cd \n"
                    + " left outer join " + TableList.VA_CHALLAN + " challan on challan.appl_no=owner.appl_no and  challan.state_cd=owner.state_cd \n"
                    + " left outer join " + TableList.VT_PERMIT + " permit on permit.appl_no=owner.appl_no and  permit.state_cd=owner.state_cd \n"
                    + " left outer join " + TableList.VT_FITNESS + " fitness on fitness.regn_no=owner.regn_no and  fitness.state_cd=owner.state_cd  and fitness.off_cd=owner.off_cd \n"
                    + " left outer join " + TableList.TM_USER_INFO + " info on info.user_cd=challan.chal_officer::numeric and  info.state_cd=owner.state_cd \n"
                    + " left outer join " + TableList.TM_OFFICE + " off on off.off_cd=challan.off_cd and  off.state_cd=challan.state_cd \n"
                    + " left outer join " + TableList.VT_INSURANCE + " insurance on insurance.regn_no=owner.regn_no and insurance.state_cd = owner.state_cd and insurance.off_cd = owner.off_cd "
                    + " left outer join " + TableList.VM_VH_CLASS + " vh_class on vh_class.vh_class=owner.vh_class  \n"
                    + " left outer join " + TableList.VT_CHALLAN_REFER_TO_COURT + " referToCourt on referToCourt.appl_no=owner.appl_no and referToCourt.state_cd=owner.state_cd \n "
                    + " left outer join " + TableList.VM_COURT + " court on court.court_cd=referToCourt.court_cd and court.state_cd=referToCourt.state_cd \n"
                    + "where owner.appl_no=? and owner.state_cd=?  and owner.off_cd = ? ";

            tmgr = new TransactionManagerReadOnly("printChallanReport");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj.setAppl_no(appl_no);
                dobj.setVcr_no(rs.getString("chal_no"));
                dobj.setChasi_no(rs.getString("chasi_no"));
                dobj.setVehicle_no(rs.getString("regn_no"));
                dobj.setFitness_validity(rs.getDate("fit_valid_to"));
                dobj.setInsurance_validity(rs.getDate("ins_upto"));
                dobj.setPermit_no(rs.getString("pmt_no"));
                dobj.setPermit_validity(rs.getDate("valid_upto"));
                dobj.setTax_paid_upto(rs.getDate("tax_upto"));
                dobj.setVh_class(rs.getString("descr"));
                dobj.setChal_date(rs.getDate("chal_date"));
                dobj.setChal_place(rs.getString("chal_place"));
                dobj.setChal_time(rs.getString("chal_time"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setOwner_address(rs.getString("c_add1") + " " + rs.getString("c_add2") + " " + rs.getString("c_add3") + " " + rs.getString("c_pincode"));
                //dobj.setChal_amnt(rs.getString("chal_amnt"));
                dobj.setRemark(rs.getString("remarks"));
                dobj.setChal_officer(rs.getString("user_name"));
                dobj.setRto_name(rs.getString("off_name"));
                dobj.setHearing_date(rs.getDate("hearing_date"));
                dobj.setReferCourt(rs.getString("court_name"));
                dobj.setMagistrateName(rs.getString("magistrate_name"));
                dobj.setCommingFrom(rs.getString("comming_from"));
                dobj.setGoingTo(rs.getString("going_to"));
                dobj.setAdCompFee(rs.getLong("adv_amt"));
                dobj.setCompFee(rs.getLong("cmpd_amt"));
                dobj.setReciept_no(rs.getString("adv_rcpt_no"));
                if (dobj.getAdCompFee() != 0L) {
                    dobj.setTotalFee(dobj.getCompFee() - dobj.getAdCompFee());
                } else {
                    dobj.setTotalFee(dobj.getCompFee());
                }

            }
            ps = null;
            rs = null;
            String sqlAccused = "select * from " + TableList.VT_WITNESS_DETAILS + " where appl_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sqlAccused);
            ps.setString(1, appl_no);
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            String witnessName = "";
            String witnessAdd = "";
            int i = 0;
            while (rs.next()) {
                witnessName += rs.getString("witness_name") + ",";
                witnessAdd += rs.getString("witness_address") + ",";
            }
            if (witnessName.endsWith(",")) {
                witnessName = witnessName.substring(0, witnessName.length() - 1);
            }
            if (witnessAdd.endsWith(",")) {
                witnessAdd = witnessAdd.substring(0, witnessAdd.length() - 1);
            }
            dobj.setWitness_name(witnessName);
            dobj.setWitness_address(witnessAdd);
            ps = null;
            rs = null;
            String sqlWitness = "select * from " + TableList.VT_CHALLAN_ACCUSED + " where appl_no=?";
            ps = tmgr.prepareStatement(sqlWitness);
            ps.setString(1, appl_no);
            // ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                String actg = rs.getString("accused_catg");
                if (actg.equals("O")) {
                    dobj.setOwner_name(rs.getString("accused_name"));
                    dobj.setOwner_address(rs.getString("accused_add"));
                    dobj.setDriving_licence_no(rs.getString("dl_no"));
                } else if (actg.equals("D")) {
                    dobj.setDriver_name(rs.getString("accused_name"));
                    dobj.setDriver_address(rs.getString("accused_add"));
                } else if (actg.equals("C")) {
                    dobj.setConductor_name(rs.getString("accused_name"));
                    dobj.setConductor_address(rs.getString("accused_add"));
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
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
        return dobj;
    }

    public static List<ChallanReportDobj> getOffenceAndAccusedDetails(String appl_no) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        List<ChallanReportDobj> list = new ArrayList<>();
        ChallanReportDobj dobj = null;
        String sql = "";
        try {
            sql = "select vch_offences.*,offences.offence_desc,accused.descr,offences.mva_clause from " + TableList.VT_VCH_OFFENCES + "  vch_offences\n"
                    + " left outer join " + TableList.VM_OFFENCES + " offences on offences.offence_cd=vch_offences.offence_cd and offences.state_cd=vch_offences.state_cd\n"
                    + " left outer join " + TableList.VM_ACCUSED + " accused on accused.code=vch_offences.accused_catg "
                    + "where vch_offences.appl_no=? and vch_offences.state_cd=? and vch_offences.off_cd=?";
            tmgr = new TransactionManager("getOffenceAndAccusedDetails");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new ChallanReportDobj();
                dobj.setAccused(rs.getString("descr"));
                dobj.setOffence(rs.getString("offence_desc"));
                dobj.setOffence_amnt(rs.getInt("offence_amt"));
                dobj.setSection(rs.getString("mva_clause"));
                list.add(dobj);

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
        return list;
    }

    public static List<ChallanReportDobj> getPendingChallanList(String regn_no) throws VahanException {
        List<ChallanReportDobj> list = new ArrayList<>();
        PreparedStatement pstm = null;
        PreparedStatement pstm1 = null;
        ChallanReportDobj dobj = null;
        RowSet rs = null;
        RowSet rs1 = null;
        TransactionManager tmgr = null;

        String sql = "";
        try {
            tmgr = new TransactionManager("getPendingChallanList");
            sql = "select distinct challan.appl_no, challan.chal_no,challan.chal_date,refer_to_court.hearing_date,courts.court_name,info.user_name,off.off_name from "
                    + TableList.VT_CHALLAN + " challan\n"
                    + "left join " + TableList.VT_CHALLAN_REFER_TO_COURT + " refer_to_court on refer_to_court.appl_no=challan.appl_no and refer_to_court.state_cd=challan.state_cd \n"
                    + "left join " + TableList.TM_USER_INFO + " info on info.user_cd=challan.chal_officer::numeric and  info.state_cd=challan.state_cd \n"
                    + "left join " + TableList.VM_COURT + " courts on courts.court_cd=refer_to_court.court_cd and courts.state_cd=refer_to_court.state_cd\n"
                    + "left join " + TableList.TM_OFFICE + " off on off.off_cd=challan.off_cd and  off.state_cd=challan.state_cd \n"
                    + "where challan.regn_no=? and challan.state_cd=? and challan.off_cd=?";
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(1, regn_no.toUpperCase().trim());
            pstm.setString(2, Util.getUserStateCode());
            pstm.setInt(3, Util.getSelectedSeat().getOff_cd());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new ChallanReportDobj();
                dobj.setChal_date(rs.getDate("chal_date"));
                dobj.setVcr_no(rs.getString("chal_no"));
                dobj.setReferCourt(rs.getString("court_name"));
                dobj.setHearing_date(rs.getDate("hearing_date"));
                String appl_no = rs.getString("appl_no");
                dobj.setChal_officer(rs.getString("user_name"));
                dobj.setRto_name(rs.getString("off_name"));
                sql = "select offence_cd from " + TableList.VT_VCH_OFFENCES + " where appl_no=? and state_cd=?";
                pstm1 = tmgr.prepareStatement(sql);
                pstm1.setString(1, appl_no);
                pstm1.setString(2, Util.getUserStateCode());
                rs1 = tmgr.fetchDetachedRowSet();
                String section = "";
                while (rs1.next()) {
                    section += getSectionName(rs1.getInt("offence_cd")) + ",";
                }
                if (section.endsWith(",")) {
                    section = section.substring(0, section.length() - 1);
                }
                dobj.setSection(section);
                list.add(dobj);
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
        return list;

    }

    private static String getSectionName(int offence_cd) {
        String section = "";
        PreparedStatement pstm = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        String sql = "";
        try {
            tmgr = new TransactionManager("getSectionName");
            sql = "select mva_clause from " + TableList.VM_OFFENCES + " where offence_cd=? and state_cd=?";
            pstm = tmgr.prepareStatement(sql);
            pstm.setInt(1, offence_cd);
            pstm.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                section = rs.getString("mva_clause");
            }
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
        return section;

    }

    public static List<ChallanReportDobj> getChallanHistoryList(String regn_no) {
        List<ChallanReportDobj> list = new ArrayList<>();
        PreparedStatement pstm = null;
        PreparedStatement pstm1 = null;
        ChallanReportDobj dobj = null;
        RowSet rs = null;
        RowSet rs1 = null;
        TransactionManager tmgr = null;
        String sql = "";
        try {
            tmgr = new TransactionManager("getChallanHistoryList");
            sql = "select challan.chal_officer ,challan.appl_no,challan.chal_no,challan.chal_date,challan.op_dt,courts.court_name,info.user_name ,COALESCE(amt.cmpd_rcpt_no,refer_to_court.court_rcpt_no) as rcpt_no,\n"
                    + "(amt.adv_amt+amt.cmpd_amt+amt.settlement_amt) as penalty,vtOwner.regn_dt,vtOwner.owner_name,vtOwner.p_add1,vtOwner.p_add2,vtOwner.p_add3,district.descr as district_descr,vtOwner.p_pincode,state.descr as state_descr,off.off_name\n"
                    + "from " + TableList.VH_CHALLAN + " challan\n"
                    + "left join " + TableList.VT_CHALLAN_REFER_TO_COURT + " refer_to_court on challan.appl_no=refer_to_court.appl_no and challan.state_cd=refer_to_court.state_cd\n"
                    + "left join " + TableList.VM_COURT + " courts on   refer_to_court.court_cd=courts.court_cd and refer_to_court.state_cd=courts.state_cd\n"
                    + "left join " + TableList.TM_USER_INFO + " info on info.user_cd=challan.chal_officer::numeric and  info.state_cd=challan.state_cd \n"
                    + "left join " + TableList.VT_CHALLAN_AMT + " amt on challan.appl_no=amt.appl_no and challan.state_cd=amt.state_cd  \n"
                    + "left join " + TableList.VT_OWNER + " vtOwner on vtOwner.regn_no=challan.regn_no and vtOwner.state_cd=challan.state_cd\n"
                    + "left join " + TableList.TM_DISTRICT + " district on vtOwner.p_district=district.dist_cd  and vtOwner.state_cd=district.state_cd\n"
                    + "left join " + TableList.TM_STATE + " state on  vtOwner.p_state=state.state_code\n"
                    + " left join " + TableList.TM_OFFICE + " off on off.off_cd=challan.off_cd and  off.state_cd=challan.state_cd \n"
                    + "where challan.regn_no=? and challan.state_cd=? and challan.off_cd=?";
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(1, regn_no.toUpperCase().trim());
            pstm.setString(2, Util.getUserStateCode());
            pstm.setInt(3, Util.getSelectedSeat().getOff_cd());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new ChallanReportDobj();
                dobj.setChal_date(rs.getDate("chal_date"));
                dobj.setVcr_no(rs.getString("chal_no"));
                dobj.setReferCourt(rs.getString("court_name"));
                dobj.setChal_officer(rs.getString("user_name"));
                dobj.setChal_amnt(rs.getString("penalty"));
                dobj.setDispose_date(rs.getDate("op_dt"));
                dobj.setReciept_no(rs.getString("rcpt_no"));
                String appl_no = rs.getString("appl_no");
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setChal_officer_cd(rs.getInt("chal_officer"));
                dobj.setRto_name(rs.getString("off_name"));
                dobj.setOwner_address(rs.getString("p_add1") + "," + rs.getString("p_add2") + "," + rs.getString("p_add3") + "," + rs.getString("district_descr") + "," + rs.getString("state_descr") + "," + rs.getString("p_pincode"));
                dobj.setRegn_dt(rs.getDate("regn_dt"));
                sql = "select offence_cd from " + TableList.VT_VCH_OFFENCES + " where appl_no=? and state_cd=?";
                pstm1 = tmgr.prepareStatement(sql);
                pstm1.setString(1, appl_no);
                pstm1.setString(2, Util.getUserStateCode());
                rs1 = tmgr.fetchDetachedRowSet();
                String section = "";
                while (rs1.next()) {
                    section += getSectionName(rs1.getInt("offence_cd")) + ", ";
                }
                if (section.endsWith(", ")) {
                    section = section.substring(0, section.length() - 2);
                }
                dobj.setSection(section);
                list.add(dobj);
            }
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
        return list;
    }

    public static List<ChallanReportDobj> getChallanHLCReportDetails(String chal_na) {
        List<ChallanReportDobj> list = new ArrayList<>();
        PreparedStatement pstm = null;
        ChallanReportDobj dobj = null;
        RowSet rs = null;
        TransactionManager tmgr = null;
        String sql = "";
        try {
            tmgr = new TransactionManager("get challan data");
            sql = "select challan.chal_no,challan.chal_date,challan.chal_time,challan.chal_place,challan.regn_no,vhClass.descr as vh_class_descr,owner.owner_name,witness.witness_name,witness.witness_address,\n"
                    + "owner.p_add1,owner.p_add2,owner.p_add3,owner.p_pincode,dist.descr as district,state.descr as state_descr,offence.offence_desc as offenceDescr,offence.mva_clause,info.user_name,off.off_name\n"
                    + "from " + TableList.VT_CHALLAN + " challan\n"
                    + "left join " + TableList.TM_USER_INFO + " info on info.user_cd=challan.chal_officer::numeric and  info.state_cd=challan.state_cd \n"
                    + "left join " + TableList.VT_OWNER + " owner on challan.regn_no=owner.regn_no and challan.state_cd=owner.state_cd\n"
                    + "left join " + TableList.TM_DISTRICT + " dist on owner.p_district=dist.dist_cd and dist.state_cd=owner.state_cd\n"
                    + "left join " + TableList.TM_STATE + " state on state.state_code=owner.state_cd\n"
                    + "left join " + TableList.VT_WITNESS_DETAILS + " witness on challan.appl_no=witness.appl_no and challan.state_cd=witness.state_cd\n"
                    + "left join " + TableList.VT_VCH_OFFENCES + " offences on challan.appl_no=offences.appl_no and challan.state_cd=offences.state_cd\n"
                    + "left join  " + TableList.VM_OFFENCES + " offence on offence.offence_cd=offences.offence_cd and offence.state_cd=offences.state_cd\n"
                    + " left join " + TableList.TM_OFFICE + " off on off.off_cd=challan.off_cd and  off.state_cd=challan.state_cd \n"
                    + " left join " + TableList.VM_VH_CLASS + " vhClass on vhClass.vh_class=owner.vh_class \n"
                    + "where challan.chal_no=? and challan.state_cd=? and challan.off_cd=?";
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(1, chal_na.toUpperCase().trim());
            pstm.setString(2, Util.getUserStateCode());
            pstm.setInt(3, Util.getSelectedSeat().getOff_cd());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new ChallanReportDobj();
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setOwner_address(rs.getString("p_add1") + "," + rs.getString("p_add2") + "," + rs.getString("p_add3") + "," + rs.getString("district") + "," + rs.getString("state_descr") + "," + rs.getString("p_pincode"));
                dobj.setChal_date(rs.getDate("chal_date"));
                dobj.setVcr_no(rs.getString("chal_no"));
                dobj.setChal_time(rs.getString("chal_time"));
                dobj.setChal_place(rs.getString("chal_place"));
                dobj.setVehicle_no(rs.getString("regn_no"));
                dobj.setVh_class(rs.getString("vh_class_descr"));
                dobj.setOffence(rs.getString("offenceDescr"));
                dobj.setSection(rs.getString("mva_clause"));
                dobj.setWitness_name(rs.getString("witness_name"));
                dobj.setWitness_address(rs.getString("witness_address"));
                dobj.setChal_officer(rs.getString("user_name"));
                dobj.setRto_name(rs.getString("off_name"));
                list.add(dobj);

            }
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
        return list;
    }

    public static boolean checkForHsrp(String state_cd, int off_cd) {
        TransactionManager tmgr = null;
        boolean isHsrp = false;
        try {
            tmgr = new TransactionManager("checkForHsrp");
            isHsrp = verifyForHsrp(state_cd, off_cd, tmgr);
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
        return isHsrp;
    }

    public static boolean checkForOldHsrp(String state_cd, int off_cd) {
        TransactionManager tmgr = null;
        boolean isOldHsrp = false;
        try {
            tmgr = new TransactionManager("checkForHsrp");
            isOldHsrp = ServerUtil.verifyForOldVehicleHsrp(state_cd, off_cd, tmgr);
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
        return isOldHsrp;
    }

    public static PendingReportDobj getPendingDobj(java.sql.Date appl_Date) throws VahanException, ParseException {
        List<PendingReportDobj> list = new ArrayList<PendingReportDobj>();
        TransactionManagerReadOnly tmgr = null;
        PendingReportDobj dobj = null;
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        PendingReportDobj returnlist = new PendingReportDobj();
        try {
            String sql = "select * from get_all_appl_status_on_date(?,?,?) order by 1,4,5";
            tmgr = new TransactionManagerReadOnly("getPendencyReport");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            boolean sameApplNo = false;
            boolean samePurCd = false;
            String prevApplno = "";
            String prevPurCd = "";
            int srNo = 0;
            java.sql.Date fromappl_Date = new java.sql.Date(appl_Date.getTime());
            ps.setDate(1, fromappl_Date);
            ps.setString(2, Util.getUserStateCode().toString());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new PendingReportDobj();
                if (prevApplno.equalsIgnoreCase(rs.getString("appl_no")) == false) {
                    sameApplNo = false;
                }
                if (!sameApplNo) {
                    if (prevApplno == "" || prevApplno.equalsIgnoreCase(rs.getString("appl_no")) == false) {
                        sameApplNo = true;
                        srNo = srNo + 1;
                        dobj.setSrNo(String.valueOf(srNo));
                        dobj.setApplNo(rs.getString("appl_no"));

                        dobj.setApplDate(rs.getString("appl_dt"));

                        prevApplno = rs.getString("appl_no");
                        samePurCd = false;
                    }
                } else {
                    dobj.setApplNo(" ");
                    dobj.setApplDate(" ");
                }
                if (prevPurCd.equalsIgnoreCase(rs.getString("purpose")) == false) {
                    samePurCd = false;
                }
                if (!samePurCd || prevPurCd == "") {
                    samePurCd = true;
                    dobj.setPurpose(rs.getString("purpose").toUpperCase());
                    prevPurCd = rs.getString("purpose").toUpperCase();
                } else {
                    dobj.setPurpose(" ");
                }
                dobj.setActionDescr(rs.getString("action_descr"));
                dobj.setFile_movement_slno(rs.getString("file_movement_slno"));
                dobj.setStatus(rs.getString("status"));
                dobj.setEntered_On(rs.getString("entered_on"));
                dobj.setEntered_by(rs.getString("entered_by"));
                dobj.setDelay_days(rs.getString("delay_days"));
                dobj.setKms_dt(rs.getString("kms_date"));
                list.add(dobj);
            }
            returnlist.setReturnlist(list);
        } catch (SQLException sqle) {
            throw new VahanException(sqle.getMessage());
        } catch (Exception e) {
            throw new VahanException(e.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);

            }

        }
        return returnlist;
    }

    public static ArrayList<PrintCertificatesDobj> getTempPrintDocsDealerDetailsAtRto() throws VahanException {
        ArrayList<PrintCertificatesDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        VahanException vahanexecption = null;
        try {
            tmgr = new TransactionManagerReadOnly("getTempPrintDocsDetails");
            ps = tmgr.prepareStatement("select distinct a.appl_no, a.temp_regn_no as regn_no, b.state_cd, b.off_cd"
                    + " from " + TableList.VT_OWNER_TEMP + " a, " + TableList.VHA_STATUS + " b"
                    + " where a.appl_no = b.appl_no and b.state_cd = ? and b.off_cd = ? and b.pur_cd = ? and b.action_cd = ? ");
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setInt(3, TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE);
            ps.setInt(4, TableConstants.TM_TMP_RC_APPROVAL);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                PrintCertificatesDobj dobj = new PrintCertificatesDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegno(rs.getString("regn_no"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setState_cd(rs.getString("state_cd"));
                list.add(dobj);
            }

        } catch (Exception ex) {
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

    public static PrintCertificatesDobj isApplExistForFC(String applNo) {
        boolean isExist = false;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        PrintCertificatesDobj dobj = null;
        String sql;
        RowSet rs;
        try {
            DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            Date CurrDate = new Date();
            String currentDt = format.format(CurrDate);
            tmgr = new TransactionManagerReadOnly("isApplExistForFC");
            sql = "select appl_no,regn_no,printed_on from  " + TableList.VH_FC_PRINT + " where appl_no=? and printed_by=? and to_char(printed_on,'dd-Mon-yyyy')=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, currentDt);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new PrintCertificatesDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegno(rs.getString("regn_no"));
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
            return dobj;
        }
    }

    public static void SaveVHFCPrintHistory(String applno) {
        PreparedStatement psvhrcprint = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("SaveVHFCPrintHistory");
            String vhrcprintSql = " insert into " + TableList.VH_FC_PRINT
                    + " select current_timestamp,?,state_cd,off_cd,appl_no,regn_no,op_dt from " + TableList.VH_FC_PRINT + " where appl_no=? AND state_cd=? AND off_cd=? order by op_dt desc limit 1";
            psvhrcprint = tmgr.prepareStatement(vhrcprintSql);
            psvhrcprint.setString(1, Util.getEmpCode());
            psvhrcprint.setString(2, applno);
            psvhrcprint.setString(3, Util.getUserStateCode());
            psvhrcprint.setInt(4, Util.getSelectedSeat().getOff_cd());
            psvhrcprint.executeUpdate();
            tmgr.commit();
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
    }

    public static ScrappedVehicleReportDobj getScrapVehicleDobj(String old_regn_no, String new_regn_no, String statecd, int offcd, String empcd, String scrap_reason) throws VahanException, ParseException {
        TransactionManagerReadOnly tmgr = null;
        ScrappedVehicleReportDobj dobj = null;
        String sql = "";
        TmConfigurationDobj configurationDobj = Util.getTmConfiguration();
        try {

            InsDobj ins_dobj = InsImpl.set_ins_dtls_db_to_dobj(old_regn_no, null, statecd, offcd);
            if (scrap_reason.equalsIgnoreCase(TableConstants.SCRAP_THEFT_CASE) && ins_dobj != null && (ins_dobj.getIns_type() == 1 || ins_dobj.getIns_type() == 3)/*check for insurance type COMPREHENSIVE and THEFT AND THIRD PARTY*/) {
                sql = "  SELECT e.owner_name, e.f_name,g.descr as state_name,\n"
                        + " upper ( COALESCE(e.c_add1,'') || ', ' || COALESCE(e.c_add2,'') || ', ' || COALESCE(e.c_add3,'') || ',' || chr(10) || COALESCE(tdc.descr , '') || '-' || COALESCE(f.descr,'') || '-' || COALESCE(e.c_pincode,0)) as c_full_add, \n"
                        + "  b.off_name,b.maker_name,b.model_name,b.chasi_no,b.eng_no,a.old_regn_no,a.no_dues_cert_no,a.scrap_cert_no,a.loi_no, \n"
                        + "  to_char(a.no_dues_issue_dt,'dd-Mon-yyyy')as no_dues_issue_dt,c.user_name, to_char(a.scrap_cert_issue_dt,'dd-Mon-yyyy')as scrap_cert_issue_dt,\n"
                        + "  d.descr as scrap_reason, to_char(a.op_dt,'dd-Mon-yyyy')as op_dt,to_char(current_timestamp,'dd-Mon-yyyy') as printed_on,p.rcpt_heading,p.rcpt_subheading\n"
                        + "  FROM " + TableList.VT_SCRAP_VEHICLE + " a \n"
                        + "  INNER JOIN " + TableList.VIEW_VV_OWNER + " b ON b.regn_no = a.old_regn_no and b.state_cd=a.state_cd\n"
                        + "  LEFT OUTER JOIN " + TableList.TM_USER_INFO + " c ON c.user_cd = ? and c.state_cd=a.state_cd and c.off_cd=a.off_cd \n"
                        + "  LEFT OUTER JOIN " + TableList.VM_SCRAP_REASON + "  d ON d.code = a.scrap_reason\n"
                        + "  LEFT OUTER JOIN " + TableList.VH_TO + " e ON  e.regn_no = a.old_regn_no and e.state_cd=a.state_cd and e.off_cd=a.off_cd \n"
                        + "  LEFT JOIN " + TableList.TM_DISTRICT + " tdc ON tdc.state_cd = e.c_state AND tdc.dist_cd = e.c_district\n"
                        + "  LEFT JOIN " + TableList.TM_STATE + " f ON f.state_code = e.c_state\n"
                        + "  LEFT JOIN " + TableList.TM_STATE + " g ON g.state_code = e.state_cd\n"
                        + "  LEFT JOIN " + TableList.TM_CONFIGURATION + " p ON p.state_cd = a.state_cd\n"
                        + "  WHERE a.old_regn_no=? and a.state_cd= ? and a.off_cd=? order by e.moved_on desc limit 1";
            } else {
                sql = "SELECT b.owner_name, b.f_name,b.state_name,upper(b.c_add1 || ', ' || b.c_add2 || ', ' || b.c_add3 || ',' || chr(10) || b.c_district_name || '-' || b.c_state_name || '-' || b.c_pincode) as c_full_add,"
                        + " b.off_name,b.maker_name,b.model_name,b.chasi_no,b.eng_no,a.old_regn_no,a.no_dues_cert_no,a.scrap_cert_no,a.loi_no,"
                        + " to_char(a.no_dues_issue_dt,'dd-Mon-yyyy')as no_dues_issue_dt,c.user_name,"
                        + " to_char(a.scrap_cert_issue_dt,'dd-Mon-yyyy')as scrap_cert_issue_dt,d.descr as scrap_reason,"
                        + " to_char(a.op_dt,'dd-Mon-yyyy')as op_dt,to_char(current_timestamp,'dd-Mon-yyyy') as printed_on,p.rcpt_heading,p.rcpt_subheading \n"
                        + " FROM " + TableList.VT_SCRAP_VEHICLE + " a \n"
                        + " INNER JOIN " + TableList.VIEW_VV_OWNER + " b ON b.regn_no = a.old_regn_no and b.state_cd=a.state_cd \n"
                        + " LEFT OUTER JOIN " + TableList.TM_USER_INFO + " c ON c.user_cd = ? and c.state_cd=a.state_cd and c.off_cd=a.off_cd \n"
                        + " LEFT OUTER JOIN " + TableList.VM_SCRAP_REASON + " d ON d.code = a.scrap_reason \n"
                        + "  LEFT JOIN " + TableList.TM_CONFIGURATION + " p ON p.state_cd = a.state_cd\n"
                        + " WHERE"
                        + " a.old_regn_no=? and a.state_cd= ? and a.off_cd=? ";

            }
            tmgr = new TransactionManagerReadOnly("getScrapVehicleDobj");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(empcd));
            ps.setString(2, old_regn_no);
            ps.setString(3, statecd);
            ps.setInt(4, offcd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new ScrappedVehicleReportDobj();
                dobj.setHeading(rs.getString("rcpt_heading"));
                dobj.setSubHeading(rs.getString("rcpt_subheading"));
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
                    if (configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan()) {
                        VmRoadSafetySloganPrintDobj vmrssdobj = getStateRoadSafetySlogan();
                        if (vmrssdobj != null) {
                            dobj.setShowRoadSafetySlogan(configurationDobj.getTmPrintConfgDobj().isRoad_safety_slogan());
                            dobj.setRoadSafetySloganDobj(vmrssdobj);
                        }
                    }
                }
                dobj.setPrintedon(rs.getString("printed_on"));
                if (rs.getString("owner_name") != null && !rs.getString("owner_name").equals("")) {
                    dobj.setOwnName("Mr./Ms. " + rs.getString("owner_name"));
                }
                if (rs.getString("f_name") != null && !rs.getString("f_name").equals("")) {
                    dobj.setFname("Son/Wife/Daughter of " + rs.getString("f_name"));
                }
                dobj.setCurrentAddress(rs.getString("c_full_add"));
                dobj.setOldRegnno(rs.getString("old_regn_no"));
                dobj.setNewRegnno(new_regn_no);
                dobj.setMaker(rs.getString("maker_name"));
                dobj.setModel(rs.getString("model_name"));
                dobj.setChasino(rs.getString("chasi_no"));
                dobj.setEngno(rs.getString("eng_no"));
                dobj.setNodueCertno(rs.getString("no_dues_cert_no"));
                dobj.setNodueCertdt(rs.getString("no_dues_issue_dt"));
                dobj.setScrapCertno(rs.getString("scrap_cert_no"));
                dobj.setScrapCertdt(rs.getString("scrap_cert_issue_dt"));
                dobj.setLoino(rs.getString("loi_no"));
                dobj.setOpdt(rs.getString("op_dt"));
                dobj.setStateName(rs.getString("state_name"));
                dobj.setOffname(rs.getString("off_name"));
                dobj.setUserName(rs.getString("user_name"));
                if (rs.getString("scrap_reason") != null) {
                    dobj.setScrap_reason(rs.getString("scrap_reason"));
                }
            }

        } catch (SQLException sqle) {
            throw new VahanException(sqle.getMessage());
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
    //function to get no dues certificate details

    public NoOfDuesCerificateDobj getDuesCerificateDetails(String regnNo) throws VahanException {

        NoOfDuesCerificateDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;
        try {
            dobj = new NoOfDuesCerificateDobj();
            tmgr = new TransactionManager("getDuesCerificateDetails-1");
            sql = "select owner.regn_no,owner.owner_name,owner.chasi_no,owner.eng_no,owner.f_name,owner.p_add1,owner.p_add2,owner.p_add3,owner.p_pincode,maker.descr as maker_name,state.descr as state_name,district.descr as district_descr,owner.manu_yr,off.off_name\n"
                    + " from " + TableList.VT_OWNER + " owner\n"
                    + " left outer join " + TableList.TM_STATE + " state on owner.p_state=state.state_code\n"
                    + " left join " + TableList.TM_DISTRICT + "  district on owner.p_district=district.dist_cd  and owner.state_cd=district.state_cd\n"
                    + " left join " + TableList.VM_MAKER + " maker on owner.maker=maker.code\n"
                    + " left join " + TableList.TM_OFFICE + " off on off.off_cd=owner.off_cd and  off.state_cd=owner.state_cd \n"
                    + " where owner.regn_no=? and owner.state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj.setVhicleNo(rs.getString("regn_no"));
                dobj.setOwner_name(rs.getString("owner_name"));
                dobj.setFather_name(rs.getString("f_name"));
                dobj.setChassis(rs.getString("chasi_no"));
                dobj.setEng_no(rs.getString("eng_no"));
                dobj.setMaker(rs.getString("maker_name"));
                dobj.setManu_year(rs.getString("manu_yr"));
                dobj.setRto_name(rs.getString("off_name"));
                dobj.setAddress(rs.getString("p_add1") + "," + rs.getString("p_add2") + "," + rs.getString("p_add3") + "," + rs.getString("district_descr") + "," + rs.getString("state_name") + "," + rs.getString("p_pincode"));
                dobj.setState(rs.getString("state_name"));
                dobj.setPin_code(rs.getString("p_pincode"));
            }
            sql = "";
            ps = null;
            tmgr = new TransactionManager("getDuesCerificateDetails-2");
            sql = "SELECT * from " + TableList.VT_FITNESS + " where regn_no=? and state_cd=? and fit_valid_to::Date >= current_date";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj.setFitness("YES");//if record exist in VT_FITNESS for regn_no then set PAID and YES
                dobj.setFitness_due("PAID");
            } else {
                dobj.setFitness("NO");
                dobj.setFitness_due("NOT PAID");
            }
            sql = "";
            ps = null;
            tmgr = new TransactionManager("getDuesCerificateDetails-3");
            sql = "SELECT * from " + TableList.VT_PERMIT + " where regn_no=? and state_cd=? and valid_upto::Date >= current_date";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj.setPermit_due("PAID");//if record exist in VT_PERMIT for regn_no then set PAID
                dobj.setPermit_no(rs.getString("pmt_no"));
            } else {
                dobj.setPermit_due("NOT PAID");
            }
            sql = "";
            ps = null;
            tmgr = new TransactionManager("getDuesCerificateDetails-4");
            sql = "SELECT * from " + TableList.VT_TAX + " where regn_no=? and state_cd=? and tax_upto::Date >= current_date";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {

                dobj.setRoad_tax("PAID");//if record exist in VT_TAX for regn_no then set PAID
            } else {
                dobj.setRoad_tax("NOT PAID");
            }
            sql = "";
            ps = null;
            tmgr = new TransactionManager("getDuesCerificateDetails-5");
            sql = "SELECT * from " + TableList.VT_RC_SURRENDER + " where regn_no=? and state_cd=? and rc='Y'";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {

                dobj.setRc("YES");//if record exist in VT_RC_SURRENDER for regn_no then set YES
            } else {
                dobj.setRc("NO");
            }
            sql = "";
            ps = null;
            tmgr = new TransactionManager("getDuesCerificateDetails-6");
            sql = " select to_char(challan.chal_date,'dd-MON-yyy') as chal_dt  from " + TableList.VT_OWNER + " owner\n"
                    + " join " + TableList.VT_CHALLAN + " challan on owner.regn_no=challan.regn_no and owner.state_cd=challan.state_cd\n"
                    + " where owner.regn_no=? and owner.state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj.setChallan_clearance(rs.getString("chal_dt"));//if record exist in VT_CHALLAN for regn_no then set challan date 
            } else {
                dobj.setChallan_clearance("");
            }
            sql = "";
            ps = null;
            tmgr = new TransactionManager("getDuesCerificateDetails=7");
            sql = " select * from " + TableList.VT_PERMIT_TRANSACTION
                    + " where regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {

                dobj.setPermit("YES");//if record exist in VT_PERMIT_TRANSACTION for regn_no then set YES
            } else {
                dobj.setPermit("NO");// set NO
            }

            if (dobj.getPermit_due().equals("PAID") && dobj.getFitness_due().equals("PAID") && dobj.getRoad_tax().equals("PAID")) {
                dobj.setAll_due("PAID");//set all due paid when permit,fitness and raod tax are paid 

            } else {
                dobj.setAll_due("NOT PAID");//otherwise not
            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException(sqle.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
        return dobj;
    }

    public static ArrayList<PrintCertificatesDobj> getRCTodayPrintedDocsDetails(String state_cd, int off_cd, int actionCd, String dealerCd, String userCd, boolean newRCPrintAtDealer) throws VahanException {
        ArrayList<PrintCertificatesDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        VahanException vahanexecption = null;
        String sql = null;
        String dealerCdSQL = "";
        try {
            if (newRCPrintAtDealer) {
                if (actionCd == TableConstants.TM_ROLE_DEALER_PRINT_RC) {
                    dealerCdSQL = " and d.dealer_cd = ? and d.vh_class <= 50 and c.pur_cd = " + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE + "";
                } else {
                    dealerCdSQL = " and c.pur_cd <> " + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE + " ";
                }
            }
            DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            String currentDt = format.format(new Date());
            tmgr = new TransactionManagerReadOnly("getRCTodayPrintedDocsDetails");
            sql = "select distinct a.appl_no, a.regn_no, a.state_cd, a.off_cd,to_char(a.printed_on,'dd-Mon-yyyy HH24:mi:ss') as printed_on,b.user_name as printed_by,d.regn_type,d.regn_dt,c.pur_cd,COALESCE(d.manu_yr,0) as manu_yr, COALESCE(d.manu_mon,0) as manu_mon from  " + TableList.VH_RC_PRINT + " a "
                    + " LEFT OUTER JOIN " + TableList.TM_USER_PERMISSIONS + " p on p.user_cd = regexp_replace(COALESCE(trim(a.printed_by::text), '0'), '[^0-9]', '0', 'g')::numeric"
                    + " LEFT OUTER JOIN " + TableList.TM_USER_INFO + " b on b.user_cd= regexp_replace(COALESCE(trim(a.printed_by::text), '0'), '[^0-9]', '0', 'g')::numeric and b.state_cd=a.state_cd and (b.off_cd=a.off_cd or p.all_office_auth=true) "
                    + " left outer join " + TableList.VA_DETAILS + " c on c.appl_no = a.appl_no and c.state_cd = a.state_cd and c.off_cd = a.off_cd "
                    + " left outer join " + TableList.VT_OWNER + " d on d.regn_no = a.regn_no and d.state_cd = a.state_cd and d.off_cd = a.off_cd "
                    + " where a.state_cd = ? and a.off_cd =? and b.user_catg = ? and to_char(printed_on,'dd-Mon-yyyy')=? " + dealerCdSQL + " order by 2";
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, state_cd);
            ps.setInt(i++, off_cd);
            ps.setString(i++, userCd);
            ps.setString(i++, currentDt);
            if (actionCd == TableConstants.TM_ROLE_DEALER_PRINT_RC) {
                ps.setString(i++, dealerCd);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                PrintCertificatesDobj dobj = new PrintCertificatesDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegno(rs.getString("regn_no"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setPrinted_on(rs.getString("printed_on"));
                dobj.setPrinted_by(rs.getString("printed_by"));
                dobj.setRegn_type(rs.getString("regn_type"));
                dobj.setRegn_date(rs.getDate("regn_dt"));
                dobj.setPurCd(rs.getInt("pur_cd"));
                if (rs.getInt("manu_mon") > 0 && rs.getInt("manu_mon") < 10) {
                    dobj.setMfgYearMonthYYYYMM(Integer.parseInt(rs.getString("manu_yr") + "0" + rs.getString("manu_mon")));
                } else {
                    dobj.setMfgYearMonthYYYYMM(Integer.parseInt(rs.getString("manu_yr") + rs.getString("manu_mon")));
                }
                list.add(dobj);
            }

        } catch (Exception ex) {
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

    public static ArrayList<CollectionSummaryDobj> getCollectionStatement(Date date, String userCatg) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        boolean queryExtended = false;
        String dealerCd = "";
        ArrayList<CollectionSummaryDobj> seatAllList = new ArrayList<>();
        Long grandTotal = 0l;
        int totalNoOfReceipt = 0;
        try {
            tmgr = new TransactionManagerReadOnly("getCollectionStatement");
            sql = "select a.* from dailycollectionstmt(?,?,?,?,?) a";
            if (!CommonUtils.isNullOrBlank(userCatg) && (userCatg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER) || userCatg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER_ADMIN))) {
                ApplicationTradeCertImpl obj = new ApplicationTradeCertImpl();
                dealerCd = obj.getDealerCode(Util.getEmpCode());
                queryExtended = true;
                sql = sql + " inner join tm_user_permissions b on a.user_cd = b.user_cd and b.dealer_cd = ?";
            }
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserSeatOffCode());
            ps.setInt(3, 0);
            ps.setDate(4, new java.sql.Date(date.getTime()));
            ps.setDate(5, new java.sql.Date(date.getTime()));
            if (queryExtended && !CommonUtils.isNullOrBlank(dealerCd)) {
                ps.setString(6, dealerCd);
            }

            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                CollectionSummaryDobj sd = new CollectionSummaryDobj();
                sd.setCollectedBy(rs.getString("collected_by"));
                if (rs.getString("instrument_type") != null) {
                    sd.setInstrumentType(rs.getString("instrument_type"));
                    if (!rs.getString("instrument_type").equalsIgnoreCase("ManualPaidReceipt")) {
                        grandTotal += rs.getLong("amount");
                    }
                }
                totalNoOfReceipt += rs.getInt("no_of_rcpt");
                sd.setNoOfRcpt(rs.getInt("no_of_rcpt"));
                sd.setTotalNoOfReceipt(totalNoOfReceipt);
                sd.setAmount(rs.getLong("amount"));
                sd.setGrandTotal(grandTotal);
                seatAllList.add(sd);
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Unable to Get Data");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Unable to Get Data");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return seatAllList;
    }

    public static ArrayList<CollectionSummaryDobj> getCancelCollectionStatement(Date date) {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        ArrayList<CollectionSummaryDobj> seatAllList = new ArrayList<>();
        try {
            tmgr = new TransactionManagerReadOnly("getCancelCollectionStatement");
            sql = "select * from dailycollectionstmt_cancel(?,?,?,?,?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserSeatOffCode());
            ps.setInt(3, 0);
            ps.setDate(4, new java.sql.Date(date.getTime()));
            ps.setDate(5, new java.sql.Date(date.getTime()));
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                CollectionSummaryDobj sd = new CollectionSummaryDobj();
                sd.setCollectedBy(rs.getString("collected_by"));
                sd.setInstrumentType(rs.getString("instrument_type"));
                sd.setNoOfRcpt(rs.getInt("no_of_rcpt"));
                sd.setAmount(rs.getLong("amount"));
                sd.setCancelBy(rs.getString("cancelled_by"));
                seatAllList.add(sd);
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
        return seatAllList;
    }

    public static ArrayList<TCPrintDobj> getLast15DaysTCPrintDocsDetails(boolean doNotShowNoOfVehicles, boolean displayFuel) throws VahanException {
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        ArrayList<TCPrintDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String vtTradeCertificateTable = "";
        VahanException vahanexecption = new VahanException("Exception in getting records for last 15 days.");
        String sql = null;
        ApplicationTradeCertImpl applicationTradeCertImpl = new ApplicationTradeCertImpl();
        try {
            if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("OR")) {
                vtTradeCertificateTable = TableList.VT_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC;
            } else {
                vtTradeCertificateTable = TableList.VT_TRADE_CERTIFICATE;
            }
            tmgr = new TransactionManagerReadOnly("getLast15DaysPrintDocsDetails");
            sql = "select distinct appl_no,cert_no,op_dt from  " + TableList.VA_TC_PRINT + "  where state_cd = ? and off_cd = ? and op_dt > current_date - 15 order by op_dt DESC";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) // found
            {
                TCPrintDobj dobj = new TCPrintDobj();
                dobj.setTcNo(rs.getString("cert_no"));
                dobj.setOpDt(rs.getTimestamp("op_dt"));
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setApplicationType(getApplicationType(dobj.getApplNo(), Util.getUserStateCode(), Util.getUserOffCode()));
                sql = "select dealer_cd,vch_catg,no_of_vch,valid_upto,no_of_vch_used,fuel_cd,applicant_type from  " + vtTradeCertificateTable
                        + " where state_cd=? and off_cd=? and cert_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getSelectedSeat().getOff_cd());
                ps.setString(3, dobj.getTcNo());
                RowSet rsDtls = tmgr.fetchDetachedRowSet();
                String vehCatg_NoOfVeh_NoOfVehUsed = "";
                while (rsDtls.next()) {
                    if (Util.getUserStateCode().equalsIgnoreCase("OR")) {
                        dobj.setDealerName(applicationTradeCertImpl.getApplicantNameByType(rsDtls.getString("dealer_cd"), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), rsDtls.getString("applicant_type")));
                    } else {
                        dobj.setDealerName(ApplicationTradeCertImpl.getDealerName(rsDtls.getString("dealer_cd"), Util.getUserStateCode(), Util.getUserOffCode(), rsDtls.getString("applicant_type")));
                        if (CommonUtils.isNullOrBlank(dobj.getDealerName()) || (!CommonUtils.isNullOrBlank(dobj.getDealerName()) && dobj.getDealerName().equals(rsDtls.getString("dealer_cd")))) {
                            dobj.setDealerName(ApplicationTradeCertImpl.fetchApplicantNameFromOnlineSchemaVmApplicantMastAppl(rsDtls.getString("dealer_cd"), Util.getUserStateCode()));
                        }
                    }
                    dobj.setApplicantType(rsDtls.getString("applicant_type"));
                    dobj.setNoOfVeh(rsDtls.getInt("no_of_vch") + "");
                    dobj.setValidUpto(rsDtls.getDate("valid_upto"));
                    dobj.setVchCatgCode(rsDtls.getString("vch_catg"));
                    if (rsDtls.getDate("valid_upto") != null) {
                        dobj.setValidUptoAsString(format.format(rsDtls.getDate("valid_upto")));
                    }

                    dobj.setNoOfVehUsed(rsDtls.getInt("no_of_vch_used") + "");
                    if (doNotShowNoOfVehicles) {
                        if (displayFuel) {
                            vehCatg_NoOfVeh_NoOfVehUsed += ApplicationTradeCertImpl.getVehCatgDesc(rsDtls.getString("vch_catg")) + " [ {fuel:<b>" + ApplicationTradeCertImpl.getFuelTypeDesc(rsDtls.getInt("fuel_cd")) + " </b>} ]</br>";
                        } else {
                            vehCatg_NoOfVeh_NoOfVehUsed += ApplicationTradeCertImpl.getVehCatgDesc(rsDtls.getString("vch_catg")) + "</br>";
                        }
                    } else if (sessionVariables.getStateCodeSelected().equals("OR")) {
                        vehCatg_NoOfVeh_NoOfVehUsed += ApplicationTradeCertImpl.getVehClassDesc(rsDtls.getString("vch_catg")) + " <b>[ " + dobj.getNoOfVeh() + " ]</b></br>";
                    } else {
                        if (displayFuel) {
                            vehCatg_NoOfVeh_NoOfVehUsed += ApplicationTradeCertImpl.getVehCatgDesc(rsDtls.getString("vch_catg")) + " [ {count: <b>" + dobj.getNoOfVeh() + "</b>} , {fuel:<b>" + ApplicationTradeCertImpl.getFuelTypeDesc(rsDtls.getInt("fuel_cd")) + " </b>} ]</br>";
                        } else {
                            vehCatg_NoOfVeh_NoOfVehUsed += ApplicationTradeCertImpl.getVehCatgDesc(rsDtls.getString("vch_catg")) + " <b>[ " + dobj.getNoOfVeh() + " ]</b></br>";
                        }
                    }

                }
                dobj.setVehCatgs(vehCatg_NoOfVeh_NoOfVehUsed);
                list.add(dobj);
            }
        } catch (SQLException | VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw vahanexecption;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return list;
    }

    public static ArrayList<TCPrintDobj> getTCTodayPrintedDocsDetails(boolean doNotShowNoOfVehicles, boolean displayFuel) throws VahanException {
        ArrayList<TCPrintDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        VahanException vahanexecption = new VahanException("Exception in getting records for current date of va_tc_print");
        String sql = null;
        ApplicationTradeCertImpl applicationTradeCertImpl = new ApplicationTradeCertImpl();
        try {
            DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            String currentDt = format.format(new Date());
            tmgr = new TransactionManagerReadOnly("getTCTodayPrintedDocsDetails");
            sql = "select distinct a.appl_no, a.cert_no,a.op_dt, a.state_cd, a.off_cd,to_char(a.printed_on,'dd-Mon-yyyy HH24:mi:ss') as printed_on,b.user_name as printed_by from  " + TableList.VH_TC_PRINT + " a "
                    + " LEFT OUTER JOIN " + TableList.TM_USER_INFO + " b on b.user_cd=a.printed_by::numeric "
                    + " where a.state_cd = ? and a.off_cd =? and to_char(printed_on,'dd-Mon-yyyy')=? order by 2";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setString(3, currentDt);
            RowSet rs = tmgr.fetchDetachedRowSet();
            String vtTradeCertificateTable = "";
            while (rs.next()) // found
            {
                if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("OR")) {
                    vtTradeCertificateTable = TableList.VT_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC;
                } else {
                    vtTradeCertificateTable = TableList.VT_TRADE_CERTIFICATE;
                }
                TCPrintDobj dobj = new TCPrintDobj();
                dobj.setTcNo(rs.getString("cert_no"));
                dobj.setOpDt(rs.getTimestamp("op_dt"));
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setOffCd(rs.getInt("off_cd"));
                dobj.setStateCd(rs.getString("state_cd"));
                dobj.setPrintedOn(rs.getString("printed_on"));
                dobj.setPrintedBy(rs.getString("printed_by"));
                dobj.setApplicationType(getApplicationType(dobj.getApplNo(), dobj.getStateCd(), dobj.getOffCd()));

                sql = "select dealer_cd,vch_catg,no_of_vch,valid_upto,no_of_vch_used,fuel_cd,applicant_type from  " + vtTradeCertificateTable
                        + " where state_cd=? and off_cd=? and cert_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getStateCd());
                ps.setInt(2, dobj.getOffCd());
                ps.setString(3, dobj.getTcNo());
                RowSet rsDtls = tmgr.fetchDetachedRowSet();
                String vehCatg_NoOfVeh_NoOfVehUsed = "";
                SessionVariables sessionVariables = new SessionVariables();
                while (rsDtls.next()) {
                    if (Util.getUserStateCode().equalsIgnoreCase("OR")) {
                        dobj.setDealerName(applicationTradeCertImpl.getApplicantNameByType(rsDtls.getString("dealer_cd"), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), rsDtls.getString("applicant_type")));
                    } else {
                        dobj.setDealerName(applicationTradeCertImpl.getDealerName(rsDtls.getString("dealer_cd"), Util.getUserStateCode(), Util.getUserOffCode(), rsDtls.getString("applicant_type")));
                        if (CommonUtils.isNullOrBlank(dobj.getDealerName()) || (!CommonUtils.isNullOrBlank(dobj.getDealerName()) && dobj.getDealerName().equals(rsDtls.getString("dealer_cd")))) {
                            dobj.setDealerName(ApplicationTradeCertImpl.fetchApplicantNameFromOnlineSchemaVmApplicantMastAppl(rsDtls.getString("dealer_cd"), Util.getUserStateCode()));
                        }
                    }
                    dobj.setApplicantType(rsDtls.getString("applicant_type"));
                    dobj.setNoOfVeh(rsDtls.getInt("no_of_vch") + "");
                    dobj.setValidUpto(rsDtls.getDate("valid_upto"));
                    dobj.setVchCatgCode(rsDtls.getString("vch_catg"));
                    if (rsDtls.getDate("valid_upto") != null) {
                        dobj.setValidUptoAsString(format.format(rsDtls.getDate("valid_upto")));
                    }
                    dobj.setNoOfVehUsed(rsDtls.getInt("no_of_vch_used") + "");
                    if (doNotShowNoOfVehicles) {
                        if (displayFuel) {
                            vehCatg_NoOfVeh_NoOfVehUsed += applicationTradeCertImpl.getVehCatgDesc(rsDtls.getString("vch_catg")) + " [ {fuel:<b>" + ApplicationTradeCertImpl.getFuelTypeDesc(rsDtls.getInt("fuel_cd")) + " </b>} ]</br>";
                        } else {
                            vehCatg_NoOfVeh_NoOfVehUsed += applicationTradeCertImpl.getVehCatgDesc(rsDtls.getString("vch_catg")) + "</br>";
                        }
                    } else if (sessionVariables.getStateCodeSelected().equals("OR")) {
                        vehCatg_NoOfVeh_NoOfVehUsed += applicationTradeCertImpl.getVehClassDesc(rsDtls.getString("vch_catg")) + " <b>[ " + dobj.getNoOfVeh() + " ]</b></br>";
                    } else {
                        if (displayFuel) {
                            vehCatg_NoOfVeh_NoOfVehUsed += applicationTradeCertImpl.getVehCatgDesc(rsDtls.getString("vch_catg")) + " [ {count: <b>" + dobj.getNoOfVeh() + "</b>} , {fuel:<b>" + ApplicationTradeCertImpl.getFuelTypeDesc(rsDtls.getInt("fuel_cd")) + " </b>} ]</br>";
                        } else {
                            vehCatg_NoOfVeh_NoOfVehUsed += applicationTradeCertImpl.getVehCatgDesc(rsDtls.getString("vch_catg")) + " <b>[ " + dobj.getNoOfVeh() + " ]</b></br>";
                        }
                    }
                }
                dobj.setVehCatgs(vehCatg_NoOfVeh_NoOfVehUsed);
                list.add(dobj);
            }

        } catch (Exception ex) {
            LOGGER.error(ex);
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

    private static String getApplicationType(String applNo, String stateCd, int offCd) throws VahanException {
        String applicationType = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        VahanException vahanexecption = new VahanException("Exception in fetching application type");
        String sql;
        try {
            tmgr = new TransactionManager("getApplicationType");
            sql = "(select trade_cert_appl_type from " + TableList.VA_TRADE_CERTIFICATE
                    + " where appl_no=? and state_cd=? and off_cd =?) union (select trade_cert_appl_type from "
                    + TableList.VHA_TRADE_CERTIFICATE + " where appl_no=? and state_cd=? and off_cd =?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.setString(4, applNo);
            ps.setString(5, stateCd);
            ps.setInt(6, offCd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                applicationType = rs.getString("trade_cert_appl_type");
            } else {
                throw vahanexecption;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw vahanexecption;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return applicationType;
    }

    public static List<TCPrintDobj> getTcDetailsIfTcNoExists(String tc_no, String userCategoryForLoggedInUser) throws VahanException {
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        TransactionManager tmgr = null;
        PreparedStatement ps;
        TCPrintDobj dobj = null;
        VahanException vahanexecption = new VahanException("Exception in getting trade certificate details if record is present in va_tc_print");
        String sql;
        RowSet rs;
        String vtTradeCertificateTable = "";
        ArrayList<TCPrintDobj> list = new ArrayList();
        ApplicationTradeCertImpl applicationTradeCertImpl = new ApplicationTradeCertImpl();
        try {
            tmgr = new TransactionManager("getTcDetailsIfTcNoExistsInVaTcPrint");
            sql = "select cert_no,appl_no from ";
            if (userCategoryForLoggedInUser.equalsIgnoreCase("A")) {    //for office admin (printing already printed TC by number)
                sql += TableList.VH_TC_PRINT;
            } else {
                sql += TableList.VA_TC_PRINT;
            }
            sql += " where cert_no= ? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, tc_no);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (Util.getUserStateCode().equalsIgnoreCase("OR")) {
                    vtTradeCertificateTable = TableList.VT_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC;
                } else {
                    vtTradeCertificateTable = TableList.VT_TRADE_CERTIFICATE;
                }
                sql = "select cert_no,dealer_cd,vch_catg,no_of_vch,valid_upto,no_of_vch_used,issue_dt,fuel_cd,applicant_type from  " + vtTradeCertificateTable
                        + " where cert_no = ? "
                        + " and state_cd=? and off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, tc_no);
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                RowSet rsDtls = tmgr.fetchDetachedRowSet();
                while (rsDtls.next()) {
                    dobj = new TCPrintDobj();
                    dobj.setTcNo(rs.getString("cert_no"));
                    dobj.setApplicantType(rsDtls.getString("applicant_type"));
                    dobj.setApplNo(rs.getString("appl_no"));
                    if (Util.getUserStateCode().equalsIgnoreCase("OR")) {
                        dobj.setDealerName(applicationTradeCertImpl.getApplicantNameByType(rsDtls.getString("dealer_cd"), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), rsDtls.getString("applicant_type")));
                    } else {
                        dobj.setDealerName(applicationTradeCertImpl.getDealerName(rsDtls.getString("dealer_cd"), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), rsDtls.getString("applicant_type")));
                    }
                    if (sessionVariables.getStateCodeSelected().equals("OR")) {
                        dobj.setVehCatgs(applicationTradeCertImpl.getVehClassDesc(rsDtls.getString("vch_catg")));
                    } else {
                        dobj.setVehCatgs(applicationTradeCertImpl.getVehCatgDesc(rsDtls.getString("vch_catg")));
                    }
                    dobj.setNoOfVeh(rsDtls.getInt("no_of_vch") + "");
                    dobj.setValidUpto(rsDtls.getDate("valid_upto"));
                    if (rsDtls.getDate("valid_upto") != null) {
                        dobj.setValidUptoAsString(format.format(rsDtls.getDate("valid_upto")));
                    }
                    dobj.setIssueDate(rsDtls.getDate("issue_dt"));
                    dobj.setNoOfVehUsed(rsDtls.getInt("no_of_vch_used") + "");
                    dobj.setFuelTypeName(ApplicationTradeCertImpl.getFuelTypeDesc(rsDtls.getInt("fuel_cd")));
                    list.add(dobj);
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e);
            throw vahanexecption;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
            return list;
        }
    }

    public static List<TCPrintDobj> getTcDetailsIfApplicationNoExists(String applNo, String userCategoryForLoggedInUser) throws VahanException {
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        TransactionManager tmgr = null;
        PreparedStatement ps;
        TCPrintDobj dobj;
        VahanException vahanexecption = new VahanException("Exception in getting trade certificate details for the provided application number.");
        String sql;
        RowSet rs;
        String vtTradeCertificateTable;
        ArrayList<TCPrintDobj> list = new ArrayList();
        ApplicationTradeCertImpl applicationTradeCertImpl = new ApplicationTradeCertImpl();
        try {
            tmgr = new TransactionManager("getTcDetailsIfApplicationNoExists");
            sql = "select cert_no,appl_no from ";
            if (userCategoryForLoggedInUser.equalsIgnoreCase("A")) {    //for office admin (printing already printed TC by number)
                sql += TableList.VH_TC_PRINT;
            } else {
                sql += TableList.VA_TC_PRINT;
            }
            sql += " where appl_no= ? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            rs = tmgr.fetchDetachedRowSet_No_release();
            while (rs.next()) {
                if ("OR".equals(Util.getUserStateCode())) {
                    vtTradeCertificateTable = TableList.VT_TRADE_CERTIFICATE_ONLINE_SCHEMA_TC;
                } else {
                    vtTradeCertificateTable = TableList.VT_TRADE_CERTIFICATE;
                }
                sql = "select cert_no,dealer_cd,vch_catg,no_of_vch,valid_upto,no_of_vch_used,issue_dt,fuel_cd,applicant_type from  " + vtTradeCertificateTable
                        + " where appl_no = ? "
                        + " and state_cd=? and off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, applNo);
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                RowSet rsDtls = tmgr.fetchDetachedRowSet();
                while (rsDtls.next()) {
                    dobj = new TCPrintDobj();
                    dobj.setTcNo(rs.getString("cert_no"));
                    dobj.setApplicantType(rsDtls.getString("applicant_type"));
                    dobj.setApplNo(rs.getString("appl_no"));
                    if ("OR".equals(Util.getUserStateCode())) {
                        dobj.setDealerName(applicationTradeCertImpl.getApplicantNameByType(rsDtls.getString("dealer_cd"), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), rsDtls.getString("applicant_type")));
                    } else {
                        dobj.setDealerName(ApplicationTradeCertImpl.getDealerName(rsDtls.getString("dealer_cd"), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), rsDtls.getString("applicant_type")));
                    }
                    if ("OR".equals(sessionVariables.getStateCodeSelected())) {
                        dobj.setVehCatgs(ApplicationTradeCertImpl.getVehClassDesc(rsDtls.getString("vch_catg")));
                    } else {
                        dobj.setVehCatgs(ApplicationTradeCertImpl.getVehCatgDesc(rsDtls.getString("vch_catg")));
                    }
                    dobj.setNoOfVeh(rsDtls.getInt("no_of_vch") + "");
                    dobj.setValidUpto(rsDtls.getDate("valid_upto"));
                    if (rsDtls.getDate("valid_upto") != null) {
                        dobj.setValidUptoAsString(format.format(rsDtls.getDate("valid_upto")));
                    }
                    dobj.setIssueDate(rsDtls.getDate("issue_dt"));
                    dobj.setNoOfVehUsed(rsDtls.getInt("no_of_vch_used") + "");
                    dobj.setFuelTypeName(ApplicationTradeCertImpl.getFuelTypeDesc(rsDtls.getInt("fuel_cd")));
                    list.add(dobj);
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw vahanexecption;
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
        return list;
    }

    public static ArrayList<PrintCertificatesDobj> getFCTodayPrintedDocsDetails(String state_cd, int off_cd) throws VahanException {
        ArrayList<PrintCertificatesDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        VahanException vahanexecption = null;
        String sql = null;
        try {
            DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            String currentDt = format.format(new Date());
            tmgr = new TransactionManagerReadOnly("getFCTodayPrintedDocsDetails");
            sql = "select distinct a.appl_no, a.regn_no, a.state_cd, a.off_cd,to_char(a.printed_on,'dd-Mon-yyyy HH24:mi:ss') as printed_on,b.user_name as printed_by from  " + TableList.VH_FC_PRINT + " a "
                    + " LEFT OUTER JOIN " + TableList.TM_USER_INFO + " b on b.user_cd=regexp_replace(COALESCE(trim(a.printed_by), '0'), '[^0-9]', '0', 'g')::numeric "
                    + " where a.state_cd = ? and a.off_cd =? and b.user_catg <> 'A' and to_char(printed_on,'dd-Mon-yyyy')=? order by 2";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, currentDt);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                PrintCertificatesDobj dobj = new PrintCertificatesDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegno(rs.getString("regn_no"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setPrinted_on(rs.getString("printed_on"));
                dobj.setPrinted_by(rs.getString("printed_by"));
                list.add(dobj);
            }

        } catch (Exception ex) {
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

    public static ArrayList<VehicleBlackListReportDobj> getVehicleBlackListDetails(int selected_action_code) throws VahanException {
        ArrayList<VehicleBlackListReportDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        VahanException vahanexecption = null;
        String sql = null;
        try {
            if (selected_action_code == TableConstants.TM_ROLE_BLACK_LIST_VEHICLE) {
                sql = "select temp.* from (SELECT a.state_cd, a.off_cd,a.regn_no, a.complain_type, a.fir_no, a.fir_dt, a.complain,\n"
                        + " a.complain_dt,to_char(a.complain_dt,'dd-Mon-yyyy')as complaindt,b.user_name,d.off_name,c.rcpt_heading,c.rcpt_subheading\n"
                        + " FROM " + TableList.VT_BLACKLIST + " a\n"
                        + " left outer join " + TableList.TM_USER_INFO + " b on b.user_cd = regexp_replace(COALESCE(trim(a.entered_by), '0'), '[^0-9]', '0', 'g')::numeric\n"
                        + " LEFT JOIN " + TableList.TM_CONFIGURATION + " c ON c.state_cd = a.state_cd"
                        + " left join " + TableList.TM_OFFICE + " d on d.off_cd=a.off_cd and d.state_cd=a.state_cd "
                        + " where a.state_cd=? and a.off_cd =? \n"
                        + " union all\n"
                        + " SELECT a.state_cd, a.off_cd,a.chasi_no, a.complain_type, a.fir_no, a.fir_dt, a.complain,\n"
                        + " a.complain_dt,to_char(a.complain_dt,'dd-Mon-yyyy')as complaindt,d.off_name,b.user_name,c.rcpt_heading,c.rcpt_subheading\n"
                        + " FROM " + TableList.VT_BLACKLIST_CHASSIS + " a\n"
                        + " left outer join " + TableList.TM_USER_INFO + " b on b.user_cd = regexp_replace(COALESCE(trim(a.entered_by), '0'), '[^0-9]', '0', 'g')::numeric\n"
                        + " LEFT JOIN " + TableList.TM_CONFIGURATION + " c ON c.state_cd = a.state_cd"
                        + " left join " + TableList.TM_OFFICE + " d on d.off_cd=a.off_cd and d.state_cd=a.state_cd "
                        + " where a.state_cd=? and a.off_cd =?) temp order by 8 desc";
            } else if (selected_action_code == TableConstants.TM_ROLE_RELEASE_BLACK_LIST_VEHICLE) {
                sql = "select temp.* from (SELECT a.state_cd, a.off_cd,a.regn_no, a.complain_type, a.fir_no, a.fir_dt, a.complain,\n"
                        + " a.complain_dt,to_char(a.complain_dt,'dd-Mon-yyyy')as complaindt,to_char(a.action_dt,'dd-Mon-yyyy')as actiondt,a.action_taken,e.user_name as release_by,b.user_name,d.off_name,c.rcpt_heading,c.rcpt_subheading\n"
                        + " FROM " + TableList.VH_BLACKLIST + " a\n"
                        + " left outer join " + TableList.TM_USER_INFO + " b on b.user_cd = regexp_replace(COALESCE(trim(a.entered_by), '0'), '[^0-9]', '0', 'g')::numeric\n"
                        + " LEFT JOIN " + TableList.TM_CONFIGURATION + " c ON c.state_cd = a.state_cd"
                        + " left join " + TableList.TM_OFFICE + " d on d.off_cd=a.off_cd and d.state_cd=a.state_cd "
                        + " left outer join " + TableList.TM_USER_INFO + " e on e.user_cd = regexp_replace(COALESCE(trim(a.action_taken_by), '0'), '[^0-9]', '0', 'g')::numeric\n"
                        + " where a.state_cd=? and a.off_cd =? \n"
                        + " union all\n"
                        + " SELECT a.state_cd, a.off_cd,a.chasi_no, a.complain_type, a.fir_no, a.fir_dt, a.complain,\n"
                        + " a.complain_dt,to_char(a.complain_dt,'dd-Mon-yyyy')as complaindt,to_char(a.action_dt,'dd-Mon-yyyy')as actiondt,a.action_taken,e.user_name as release_by,d.off_name,b.user_name,c.rcpt_heading,c.rcpt_subheading\n"
                        + " FROM " + TableList.VH_BLACKLIST_CHASSIS + " a\n"
                        + " left outer join " + TableList.TM_USER_INFO + " b on b.user_cd = regexp_replace(COALESCE(trim(a.entered_by), '0'), '[^0-9]', '0', 'g')::numeric\n"
                        + " LEFT JOIN " + TableList.TM_CONFIGURATION + " c ON c.state_cd = a.state_cd"
                        + " left join " + TableList.TM_OFFICE + " d on d.off_cd=a.off_cd and d.state_cd=a.state_cd "
                        + " left outer join " + TableList.TM_USER_INFO + " e on e.user_cd = regexp_replace(COALESCE(trim(a.action_taken_by), '0'), '[^0-9]', '0', 'g')::numeric\n"
                        + " where a.state_cd=? and a.off_cd =?) temp order by 8 desc";
            }
            tmgr = new TransactionManagerReadOnly("getTempPrintDocsDetails");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setString(3, Util.getUserStateCode());
            ps.setInt(4, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                VehicleBlackListReportDobj dobj = new VehicleBlackListReportDobj();
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setBlocked_date(rs.getString("complaindt"));
                dobj.setBlocked_by(rs.getString("user_name"));
                dobj.setReason(rs.getString("complain"));
                dobj.setHeader(rs.getString("rcpt_heading"));
                dobj.setOffName(rs.getString("off_name"));
                dobj.setSubHeader(rs.getString("rcpt_subheading"));
                if (selected_action_code == TableConstants.TM_ROLE_RELEASE_BLACK_LIST_VEHICLE) {
                    dobj.setAction_date(rs.getString("actiondt"));
                    dobj.setAction_taken(rs.getString("action_taken"));
                    dobj.setReleased_by(rs.getString("release_by"));
                }
                list.add(dobj);
            }

        } catch (Exception ex) {
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

    public static ArrayList<PrintCertificatesDobj> isTempRegnNoExistForRC(String regn_no, String appl_no, String state_cd, int off_cd) {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        PrintCertificatesDobj dobj = null;
        String sql;
        RowSet rs;
        ArrayList<PrintCertificatesDobj> list = new ArrayList();
        try {
            tmgr = new TransactionManagerReadOnly("PrintDocImpl.isTempRegnNoExistForRC");

            if (regn_no != null && !regn_no.equalsIgnoreCase("")) {
                sql = "select a.appl_no,a.temp_regn_no,b.pur_cd from  " + TableList.VA_TEMP_RC_PRINT + " a "
                        + " left outer join " + TableList.VA_DETAILS + " b on b.appl_no = a.appl_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd"
                        + " where a.temp_regn_no=? and a.state_cd=? and a.off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regn_no);
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
            } else if (appl_no != null && !appl_no.equalsIgnoreCase("")) {
                sql = "select a.appl_no,a.temp_regn_no,b.pur_cd from  " + TableList.VA_TEMP_RC_PRINT + " a "
                        + " left outer join " + TableList.VA_DETAILS + " b on b.appl_no = a.appl_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd"
                        + " where a.appl_no=? and a.state_cd=? and a.off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_no);
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
            }

            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new PrintCertificatesDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegno(rs.getString("temp_regn_no"));
                dobj.setPurCd(rs.getInt("pur_cd"));
                list.add(dobj);
            }
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
            return list;
        }
    }

    public static ArrayList<PrintCertificatesDobj> isTempRegnNoExistPrintDocsDealerDetails(String regn_no, String appl_no, int pur_cd, String dealerCd, String state_cd, int off_cd) throws VahanException {
        ArrayList<PrintCertificatesDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        VahanException vahanexecption = null;
        try {
            tmgr = new TransactionManagerReadOnly("getTempPrintDocsDetails");

            if (regn_no != null && !regn_no.equalsIgnoreCase("")) {
                ps = tmgr.prepareStatement("select distinct a.appl_no, a.temp_regn_no as regn_no, b.state_cd, b.off_cd"
                        + " from " + TableList.VA_TEMP_RC_PRINT + " a, " + TableList.VA_DETAILS + " b"
                        + " where a.temp_regn_no=? and  a.appl_no = b.appl_no and b.state_cd = ? and b.off_cd = ? and b.pur_cd IN (?,?) and a.dealer_cd =  ? ");
                ps.setString(1, regn_no);
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
                ps.setInt(4, pur_cd);
                ps.setInt(5, TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE);
                ps.setString(6, dealerCd);
            } else if (appl_no != null && !appl_no.equalsIgnoreCase("")) {
                ps = tmgr.prepareStatement("select distinct a.appl_no, a.temp_regn_no as regn_no, b.state_cd, b.off_cd"
                        + " from " + TableList.VA_TEMP_RC_PRINT + " a, " + TableList.VA_DETAILS + " b"
                        + " where a.appl_no=? and  a.appl_no = b.appl_no and b.state_cd = ? and b.off_cd = ? and b.pur_cd IN (?,?) and a.dealer_cd =  ? ");
                ps.setString(1, appl_no);
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
                ps.setInt(4, pur_cd);
                ps.setInt(5, TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE);
                ps.setString(6, dealerCd);
            }

            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                PrintCertificatesDobj dobj = new PrintCertificatesDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegno(rs.getString("regn_no"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setState_cd(rs.getString("state_cd"));
                list.add(dobj);
            }
        } catch (Exception ex) {
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

    public static ArrayList<PrintCertificatesDobj> getTempRCTodayPrintedDocsDetails(String state_cd, int off_cd) throws VahanException {
        ArrayList<PrintCertificatesDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        VahanException vahanexecption = null;
        String sql = null;
        try {
            DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            String currentDt = format.format(new Date());
            tmgr = new TransactionManagerReadOnly("getTempRCTodayPrintedDocsDetails");
            sql = "select distinct a.appl_no, a.temp_regn_no, a.state_cd, a.off_cd,to_char(a.printed_on,'dd-Mon-yyyy HH24:mi:ss') as printed_on,b.user_name as printed_by,c.pur_cd from  " + TableList.VH_TEMP_RC_PRINT + " a "
                    + " LEFT OUTER JOIN " + TableList.TM_USER_INFO + " b on b.user_cd=regexp_replace(COALESCE(trim(a.printed_by::text), '0'), '[^0-9]', '0', 'g')::numeric and b.state_cd=a.state_cd and b.off_cd=a.off_cd"
                    + " left outer join " + TableList.VA_DETAILS + " c on c.appl_no = a.appl_no and c.state_cd = a.state_cd and c.off_cd = a.off_cd"
                    + " where a.state_cd = ? and a.off_cd =? and to_char(printed_on,'dd-Mon-yyyy')=? order by 2";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, currentDt);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                PrintCertificatesDobj dobj = new PrintCertificatesDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegno(rs.getString("temp_regn_no"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setPrinted_on(rs.getString("printed_on"));
                dobj.setPrinted_by(rs.getString("printed_by"));
                dobj.setPurCd(rs.getInt("pur_cd"));
                list.add(dobj);
            }

        } catch (Exception ex) {
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

    public static void deleteAndSaveHistoryForTempRegnNo(String appl_no, String reg_no, String state_cd, int off_cd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("deleteAndSaveHistoryForTempRegnNo");
            String vhrcprintSql = " insert into " + TableList.VH_TEMP_RC_PRINT
                    + " select state_cd,off_cd,appl_no,temp_regn_no,dealer_cd,op_dt,current_timestamp,? from " + TableList.VA_TEMP_RC_PRINT + " where appl_no=? AND state_cd=? AND off_cd=?";
            ps = tmgr.prepareStatement(vhrcprintSql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            ps.setString(3, state_cd);
            ps.setInt(4, off_cd);
            ps.executeUpdate();

            String varcprintSql = " delete from " + TableList.VA_TEMP_RC_PRINT + " where appl_no=? AND state_cd=? AND off_cd=?";
            ps = tmgr.prepareStatement(varcprintSql);
            ps.setString(1, appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            ps.executeUpdate();
            tmgr.commit();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong, Please try again.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
    }

    public static void SaveTempRCPrintHistory(String applno, String state_cd, int off_cd) throws VahanException {
        PreparedStatement psvhrcprint = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("deleteAndSaveHistory");
            String vhrcprintSql = " insert into " + TableList.VH_TEMP_RC_PRINT
                    + " select state_cd,off_cd,appl_no,temp_regn_no,dealer_cd,op_dt,current_timestamp,? from " + TableList.VH_TEMP_RC_PRINT + " where appl_no=? AND state_cd=? AND off_cd=? order by op_dt desc limit 1";
            psvhrcprint = tmgr.prepareStatement(vhrcprintSql);
            psvhrcprint.setString(1, Util.getEmpCode());
            psvhrcprint.setString(2, applno);
            psvhrcprint.setString(3, state_cd);
            psvhrcprint.setInt(4, off_cd);
            psvhrcprint.executeUpdate();
            tmgr.commit();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong, Please try again.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
    }

    public static ArrayList<PrintCertificatesDobj> getDealerTempRCTodayPrintedDocsDetails(int pur_cd, String dealerCd, String state_cd, int off_cd) throws VahanException {
        ArrayList<PrintCertificatesDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        VahanException vahanexecption = null;
        String sql = null;
        try {
            DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            String currentDt = format.format(new Date());
            tmgr = new TransactionManagerReadOnly("getTempRCTodayPrintedDocsDetails");
            sql = "select distinct a.appl_no, a.temp_regn_no , a.state_cd, a.off_cd,to_char(a.printed_on,'dd-Mon-yyyy HH24:mi:ss') as printed_on,b.pur_cd,c.user_name as printed_by"
                    + " from " + TableList.VH_TEMP_RC_PRINT + " a "
                    + " left outer join " + TableList.VA_DETAILS + " b on b.appl_no = a.appl_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd"
                    + " LEFT OUTER JOIN " + TableList.TM_USER_INFO + " c on c.user_cd=regexp_replace(COALESCE(trim(a.printed_by::text), '0'), '[^0-9]', '0', 'g')::numeric and c.state_cd=a.state_cd and c.off_cd=a.off_cd "
                    + " where a.state_cd = ? and a.off_cd = ? and to_char(a.printed_on,'dd-Mon-yyyy')=? and b.pur_cd IN (?,?) and a.dealer_cd =  ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, currentDt);
            ps.setInt(4, pur_cd);
            ps.setInt(5, TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE);
            ps.setString(6, dealerCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                PrintCertificatesDobj dobj = new PrintCertificatesDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegno(rs.getString("temp_regn_no"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setPrinted_on(rs.getString("printed_on"));
                dobj.setPrinted_by(rs.getString("printed_by"));
                dobj.setPurCd(rs.getInt("pur_cd"));
                list.add(dobj);
            }

        } catch (Exception ex) {
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

    public static ArrayList<PrintCertificatesDobj> isTempRegnExistForPrintedRC(String regn_no, String appl_no, String state_cd, int off_cd) {
        ArrayList<PrintCertificatesDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        try {
            tmgr = new TransactionManagerReadOnly("isTempRegnExistForPrintedRC");

            if (regn_no != null && !regn_no.equalsIgnoreCase("")) {
                sql = "select distinct a.appl_no, a.temp_regn_no, a.state_cd,a.printed_on,a.off_cd,to_char(a.printed_on,'dd-Mon-yyyy HH24:mi:ss') as printedondt,b.user_name as printed_by from  " + TableList.VH_TEMP_RC_PRINT + " a "
                        + " LEFT OUTER JOIN " + TableList.TM_USER_INFO + " b on b.user_cd= regexp_replace(COALESCE(trim(a.printed_by::text), '0'), '[^0-9]', '0', 'g')::numeric and b.state_cd=a.state_cd and b.off_cd=a.off_cd "
                        + " where a.state_cd = ? and a.off_cd =? and a.temp_regn_no=? order by printed_on DESC limit 1";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, state_cd);
                ps.setInt(2, off_cd);
                ps.setString(3, regn_no);
            } else if (appl_no != null && !appl_no.equalsIgnoreCase("")) {
                sql = "select distinct a.appl_no, a.temp_regn_no, a.state_cd,a.printed_on,a.off_cd,to_char(a.printed_on,'dd-Mon-yyyy HH24:mi:ss') as printedondt,b.user_name as printed_by from  " + TableList.VH_TEMP_RC_PRINT + " a "
                        + " LEFT OUTER JOIN " + TableList.TM_USER_INFO + " b on b.user_cd= regexp_replace(COALESCE(trim(a.printed_by::text), '0'), '[^0-9]', '0', 'g')::numeric and b.state_cd=a.state_cd and b.off_cd=a.off_cd "
                        + " where a.state_cd = ? and a.off_cd =? and a.appl_no=? order by printed_on DESC limit 1";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, state_cd);
                ps.setInt(2, off_cd);
                ps.setString(3, appl_no);
            }

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                PrintCertificatesDobj dobj = new PrintCertificatesDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegno(rs.getString("temp_regn_no"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setPrinted_on(rs.getString("printedondt"));
                dobj.setPrinted_by(rs.getString("printed_by"));
                list.add(dobj);
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
        return list;
    }

    public static ArrayList<PrintCertificatesDobj> getTempRCPendingDocsDetails(String state_cd, int off_cd) throws VahanException {
        ArrayList<PrintCertificatesDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        VahanException vahanexecption = null;
        String sql = null;
        try {
            tmgr = new TransactionManagerReadOnly("getTempRCPendingDocsDetails");
            sql = "select distinct a.appl_no, a.temp_regn_no, a.state_cd, a.off_cd,a.op_dt from  " + TableList.VA_TEMP_RC_PRINT + " a "
                    + "  where a.state_cd = ? and a.off_cd = ? and a.op_dt > current_date - 15 order by a.op_dt DESC";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                PrintCertificatesDobj dobj = new PrintCertificatesDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegno(rs.getString("temp_regn_no"));
                list.add(dobj);
            }
        } catch (Exception ex) {
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

    public static List<PrintCertificatesDobj> getNewVehicleFeeDtlsForOtherStateVehicle(List<PrintCertificatesDobj> selectedCertDobj) {
        List<PrintCertificatesDobj> selectedRegnList = new ArrayList<PrintCertificatesDobj>();
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getNewVehicleFeeDtlsForOtherStateVehicle");
            for (int i = 0; i < selectedCertDobj.size(); i++) {
                if (NewImpl.isRegnNoNewVeh(tmgr, selectedCertDobj.get(i).getAppl_no())) {
                    selectedRegnList.add(selectedCertDobj.get(i));
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
            return selectedRegnList;
        }
    }

    public static List<TaxDefaulterListDobj> getTaxDNoticeReportDobj(String State_cd, int off_cd, TaxDefaulterListDobj dobj) throws VahanException, ParseException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        TaxDefaulterListDobj dobjList = null;
        TaxDefaulterListDobj dobjTaxList = null;
        Utility utility = new Utility();
        List<TaxDefaulterListDobj> selectedTDNlist = new ArrayList<>();
        List<TaxDefaulterListDobj> selectedTaxlist = null;
        RowSet rset = null;
        RowSet rs = null;
        RowSet rsTaxList = null;
        String regn_no = null;
        int vh_class = 0;
        try {
            tmgr = new TransactionManagerReadOnly("getTaxDNoticeReportDobj-1");
            String query = "SELECT a.regn_no,a.vh_class from dashboard.vt_tax_defaulter_n a "
                    + "where a.state_cd = ? and a.off_cd = ? and a.vh_class=? and a.op_dt is not null and a.tax_upto is not null group by 1,2 order by 1 ";

            ps = tmgr.prepareStatement(query);
            ps.setString(1, State_cd);
            ps.setInt(2, off_cd);
            ps.setInt(3, dobj.getVch_class_cd());
            rset = tmgr.fetchDetachedRowSet();
            while (rset.next()) {
                regn_no = rset.getString("regn_no");
                vh_class = rset.getInt("vh_class");
                if (regn_no != null && !regn_no.equals("") && vh_class != 0) {
                    tmgr = new TransactionManagerReadOnly("getTaxDNoticeReportDobj-2");
                    query = "select distinct a.regn_no,a.state_name,a.state_cd,a.off_cd,a.vh_class,a.vch_catg,a.owner_name,a.f_name,a.c_state_name,upper(a.c_add1 || ',' || a.c_add2 || ',' || a.c_add3 || ',' || a.c_district_name || ',' || a.c_state_name || '-' || COALESCE(a.c_pincode::varchar, '')) as curr_address,upper(a.p_add1 || ',' || a.p_add2 || ',' || a.p_add3 || ',' || a.p_district_name || ',' || a.p_state_name || '-' || COALESCE(a.p_pincode::varchar, '')) as permanant_address,\n"
                            + " c.rcpt_heading,c.rcpt_subheading,a.off_name,a.vh_class_desc,to_char(current_date,'dd-Mon-yyyy') as printDate,b.head1,b.head2,b.head3,to_char(a.fit_upto,'DD-MON-yyyy') as fit_upto\n"
                            + " from vv_owner a \n"
                            + " left outer join tm_configuration c on c.state_cd=a.state_cd \n"
                            + " left outer join " + TableList.VT_TAX_NOTICE_HEAD + " b on b.state_cd=a.state_cd \n"
                            + " where a.state_cd = ? and a.off_cd = ? and a.regn_no=? order by 1";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, State_cd);
                    ps.setInt(2, off_cd);
                    ps.setString(3, regn_no.toUpperCase());
                    rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        dobjList = new TaxDefaulterListDobj();
                        dobjList.setRegn_no(rs.getString("regn_no"));
                        dobjList.setRcpt_heading(rs.getString("rcpt_heading"));
                        dobjList.setRcpt_subheading(rs.getString("rcpt_subheading"));
                        dobjList.setOffName(rs.getString("off_name"));
                        dobjList.setCurr_address(rs.getString("curr_address"));
                        dobjList.setPermanant_address(rs.getString("permanant_address"));
                        //dobjList.setMemono(rs.getString("memono"));
                        dobjList.setPrintDate(rs.getString("printDate"));
                        dobjList.setOwner_name(rs.getString("owner_name"));
                        dobjList.setFatherName(rs.getString("f_name"));
                        dobjList.setVch_class_desc(rs.getString("vh_class_desc"));
                        dobjList.setVch_catg_cd(rs.getString("vch_catg"));
                        dobjList.setStateName(rs.getString("state_name"));
                        dobjList.setTaxNoticeHead1(rs.getString("head1"));
                        dobjList.setTaxNoticeHead2(rs.getString("head2"));
                        dobjList.setTaxNoticeHead3(rs.getString("head3"));
                        dobjList.setFitvalidt(rs.getString("fit_upto"));
                        tmgr = new TransactionManagerReadOnly("getTaxDNoticeReportDobj-3");
                        query = "select a.pur_cd, to_char(a.tax_from,'dd-MM-yyyy') AS taxfrom,to_char(a.tax_upto,'dd-MM-yyyy') AS taxupto,a.tax_amt,a.tax_fine,b.descr as purcd_desc \n"
                                + " from dashboard.vt_tax_defaulter_n a  \n"
                                + " left outer join tm_purpose_mast b on b.pur_cd=a.pur_cd \n"
                                + " where a.state_cd = ? and a.off_cd = ? and a.vh_class=? and a.regn_no=? and a.op_dt is not null and a.tax_upto is not null and (a.tax_amt >0 or a.tax_fine>0) order by 1";
                        ps = tmgr.prepareStatement(query);
                        ps.setString(1, State_cd);
                        ps.setInt(2, off_cd);
                        ps.setInt(3, vh_class);
                        ps.setString(4, regn_no.toUpperCase());
                        rsTaxList = tmgr.fetchDetachedRowSet();
                        selectedTaxlist = new ArrayList<>();
                        int sumAmt = 0;
                        while (rsTaxList.next()) {
                            dobjTaxList = new TaxDefaulterListDobj();
                            dobjTaxList.setTaxTenure(rsTaxList.getString("purcd_desc") + "(" + rsTaxList.getString("taxfrom") + " to " + rsTaxList.getString("taxupto") + ")");
                            dobjTaxList.setTaxamt(rsTaxList.getString("tax_amt"));
                            if (rsTaxList.getString("tax_amt") != null && !"".contains(rsTaxList.getString("tax_amt"))) {
                                sumAmt += Integer.parseInt(rsTaxList.getString("tax_amt"));
                            }
                            selectedTaxlist.add(dobjTaxList);
                        }
                        dobjList.setListTaxAmt(selectedTaxlist);
                        dobjList.setTaxamt(String.valueOf(sumAmt));
                        dobjList.setTaxAmtInWords(utility.ConvertNumberToWords(sumAmt));
                        selectedTDNlist.add(dobjList);
                    }
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
        return selectedTDNlist;
    }

    public static List<TaxDefaulterListDobj> getTaxDefaulterNoticeFromTaxAssessment(String regn_no, String State_cd, int off_cd, int vh_class) throws VahanException, ParseException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        TaxDefaulterListDobj dobjList = null;
        List<TaxDefaulterListDobj> selectedTDNlist = new ArrayList<>();
        RowSet rs = null;
        try {
            tmgr = new TransactionManagerReadOnly("getTaxDefaulterNoticeFromTaxAssessment-1");
            String query = "select distinct a.regn_no,a.state_name,a.state_cd,a.off_cd,a.vh_class,a.vch_catg,a.owner_name,a.f_name,a.c_state_name,upper(a.c_add1 || ',' || a.c_add2 || ',' || a.c_add3 || ',' || a.c_district_name || ',' || a.c_state_name || '-' || COALESCE(a.c_pincode::varchar, '')) as curr_address,upper(a.p_add1 || ',' || a.p_add2 || ',' || a.p_add3 || ',' || a.p_district_name || ',' || a.p_state_name || '-' || COALESCE(a.p_pincode::varchar, '')) as permanant_address,\n"
                    + " c.rcpt_heading,c.rcpt_subheading,a.off_name,a.vh_class_desc,to_char(current_date,'dd-Mon-yyyy') as printDate,b.head1,b.head2,b.head3,to_char(a.fit_upto,'DD-MON-yyyy') as fit_upto\n"
                    + " from vv_owner a \n"
                    + " left outer join tm_configuration c on c.state_cd=a.state_cd \n"
                    + " left outer join " + TableList.VT_TAX_NOTICE_HEAD + " b on b.state_cd=a.state_cd \n"
                    + " where a.state_cd = ? and a.off_cd = ? and a.regn_no=? and a.vh_class=? order by 1";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, State_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, regn_no.toUpperCase());
            ps.setInt(4, vh_class);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobjList = new TaxDefaulterListDobj();
                dobjList.setRegn_no(rs.getString("regn_no"));
                dobjList.setRcpt_heading(rs.getString("rcpt_heading"));
                dobjList.setRcpt_subheading(rs.getString("rcpt_subheading"));
                dobjList.setOffName(rs.getString("off_name"));
                dobjList.setCurr_address(rs.getString("curr_address"));
                dobjList.setPrintDate(rs.getString("printDate"));
                dobjList.setOwner_name(rs.getString("owner_name"));
                dobjList.setFatherName(rs.getString("f_name"));
                dobjList.setVch_class_desc(rs.getString("vh_class_desc"));
                dobjList.setVch_catg_cd(rs.getString("vch_catg"));
                dobjList.setStateName(rs.getString("state_name"));
                dobjList.setTaxNoticeHead1(rs.getString("head1"));
                dobjList.setTaxNoticeHead2(rs.getString("head2"));
                dobjList.setTaxNoticeHead3(rs.getString("head3"));
                dobjList.setFitvalidt(rs.getString("fit_upto"));
                dobjList.setPermanant_address(rs.getString("permanant_address"));
                selectedTDNlist.add(dobjList);
            }
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
        return selectedTDNlist;
    }

    public static void printSucessAndApproveStatus(TransactionManager tmgr, SessionVariables sessionVariables) throws VahanException {
        try {
            if ((sessionVariables.getSelectedWork().getPur_cd() == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS) || (sessionVariables.getSelectedWork().getPur_cd() == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE) || (sessionVariables.getSelectedWork().getPur_cd() == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_COMPANY) || (sessionVariables.getSelectedWork().getPur_cd() == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_OWNER)) {
                Status_dobj status = new Status_dobj();
                status.setAppl_dt(sessionVariables.getSelectedWork().getAppl_dt());
                status.setAppl_no(sessionVariables.getSelectedWork().getAppl_no());
                status.setPur_cd(sessionVariables.getSelectedWork().getPur_cd());
                status.setOffice_remark("");
                status.setPublic_remark("");
                status.setStatus("C");
                status.setCurrent_role(7);
                status.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status);
                status = ServerUtil.webServiceForNextStage(status, tmgr);
                ServerUtil.fileFlow(tmgr, status);
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting Vehicle Particulars, please try after sometime or contact Administrator.");
        }
    }

    public static String isRegnExistForVehParPrint(String regn_no, String state_cd, int off_cd) {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        String appl_no = null;
        String sql;
        RowSet rs;
        ArrayList<PrintCertificatesDobj> list = new ArrayList();
        try {
            tmgr = new TransactionManagerReadOnly("Check regn no for Vehicle Particular");
            sql = "select appl_no from  " + TableList.VH_VCH_PARTICULAR_PRINT + " where regn_no=? and state_cd=? and off_cd=? and printed_on::date = current_date  order by printed_on DESC limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                appl_no = rs.getString("appl_no");
            }
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
            return appl_no;
        }
    }

    public static void printSucessAndMoveInHistory(SessionVariables sessionVariables, String appl_no, VehicleParticularDobj dobj, TransactionManager tmgr) {
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO " + TableList.VH_VCH_PARTICULAR_PRINT + " (state_cd,off_cd,appl_no,regn_no,op_dt,printed_on,printed_by) "
                    + " values (?,?,?,?,current_timestamp,current_timestamp,?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, sessionVariables.getStateCodeSelected());
            ps.setInt(2, sessionVariables.getOffCodeSelected());
            ps.setString(3, appl_no);
            ps.setString(4, dobj.getRegn_no());
            ps.setString(5, Util.getEmpCode());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public static void printSucessAndMoveTodayPrintedInHistory(SessionVariables sessionVariables, String appl_no, VehicleParticularDobj dobj, TransactionManager tmgr) {
        PreparedStatement ps = null;
        try {
            String sql = "insert into " + TableList.VH_VCH_PARTICULAR_PRINT
                    + " SELECT state_cd,off_cd,?,regn_no,op_dt,current_timestamp,? from " + TableList.VH_VCH_PARTICULAR_PRINT + " a "
                    + " where a.regn_no= ? and a.state_cd = '" + sessionVariables.getStateCodeSelected() + "' and a.off_cd =" + sessionVariables.getOffCodeSelected() + " order by printed_on desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, dobj.getRegn_no());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public static ArrayList<VehicleParticularDobj> getVchParTodayPrintedDetails(String state_cd, int off_cd) throws VahanException {
        ArrayList<VehicleParticularDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        VahanException vahanexecption = null;
        String sql = null;
        try {
            DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            String currentDt = format.format(new Date());
            tmgr = new TransactionManagerReadOnly("getVchParTodayPrintedDetails");
            sql = "select distinct a.appl_no, a.regn_no,to_char(a.printed_on,'dd-Mon-yyyy HH24:mi:ss') as printed_on,b.user_name as printed_by from  " + TableList.VH_VCH_PARTICULAR_PRINT + " a "
                    + " LEFT OUTER JOIN " + TableList.TM_USER_INFO + " b on b.user_cd= regexp_replace(COALESCE(trim(a.printed_by::text), '0'), '[^0-9]', '0', 'g')::numeric and b.state_cd=a.state_cd and b.off_cd=a.off_cd "
                    + " where a.state_cd = ? and a.off_cd =? and printed_on between current_date and current_date + '1 day'::interval order by 2";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                VehicleParticularDobj dobj = new VehicleParticularDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setPrinted_on(rs.getString("printed_on"));
                dobj.setPrinted_by(rs.getString("printed_by"));
                list.add(dobj);
            }

        } catch (Exception ex) {
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

    public static List<DownloadDispatchDobj> getReturnDispatchInfoDetails(String stateCode, int offCode, String enterNo, Date from_date, Date to_dat, String radipBtnValue) {
        ArrayList<DownloadDispatchDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        RowSet rs = null;
        String wherClause = null;
        try {
            TmConfigurationDobj configurationDobj = Util.getTmConfiguration();
            java.sql.Date fromDate = new java.sql.Date(from_date.getTime());
            java.sql.Date toDate = new java.sql.Date(to_dat.getTime());
            if ("APPLNO".contains(radipBtnValue)) {
                wherClause = "and a.appl_no= '" + enterNo + "' order by 1 DESC ,2 limit 1";
            } else if ("REGNNO".contains(radipBtnValue)) {
                wherClause = "and a.regn_no ='" + enterNo + "' order by 1 DESC ,2 limit 1";
            } else if ("RETURNDT".contains(radipBtnValue)) {
                wherClause = "and a.return_on BETWEEN '" + fromDate + "'::date AND ('" + toDate + "'::date + '1 day'::interval -  '1 second'::interval) order by 1 DESC ,2";
            }

            tmgr = new TransactionManager("getReturnDispatchInfoDetails");
            sql = "SELECT a.return_on,a.regn_no,a.dispatch_ref_no,to_char(a.dispatch_on,'dd-MON-yyyy') as disp_date,to_char(a.return_on,'dd-MON-yyyy') as return_date,b.owner_name,to_char(current_date,'dd-MON-yyyy') as prit_date, \n"
                    + " c.rcpt_heading,c.rcpt_subheading,d.off_name,e.descr,b.c_address"
                    + " from " + TableList.VH_RCDISPATCH_RETURN + " a \n"
                    + " LEFT OUTER JOIN " + TableList.VHA_DISPATCH + " b on b.appl_no=a.appl_no and b.moved_on =a.dispatch_on \n"
                    + " LEFT OUTER JOIN " + TableList.TM_CONFIGURATION + " c ON c.state_cd = a.state_cd"
                    + " LEFT OUTER JOIN " + TableList.TM_OFFICE + " d on d.state_cd = a.state_cd and d.off_cd =a.off_cd "
                    + " LEFT OUTER JOIN " + TableList.VM_RCD_RETURN_REASON + " e on e.code = a.reason "
                    + " where a.state_cd = ? and a.off_cd =? " + wherClause;
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            ps.setInt(2, offCode);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                DownloadDispatchDobj dobj = new DownloadDispatchDobj();
                dobj.setDispatchdate(rs.getString("disp_date"));
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setDispatch_ref_no(rs.getString("dispatch_ref_no"));
                dobj.setDispatch_rc_return_on(rs.getString("return_date"));
                dobj.setOwnerName(rs.getString("owner_name"));
                dobj.setPrinted_on(rs.getString("prit_date"));
                dobj.setRcptHeading(rs.getString("rcpt_heading"));
                dobj.setRcptSubHeading(rs.getString("rcpt_subheading"));
                dobj.setOffName(rs.getString("off_name"));
                dobj.setReturnReason(rs.getString("descr"));
                dobj.setCaddress(rs.getString("c_address"));
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
                list.add(dobj);
            }
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
        return list;
    }

    public static ArrayList<PrintCertificatesDobj> isRegnExistForForm38AFC(String regn_no, String state_cd, int off_cd, String printFormRadBtn) {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        PrintCertificatesDobj dobj = null;
        String sql;
        RowSet rs;
        ArrayList<PrintCertificatesDobj> list = new ArrayList();
        try {
            tmgr = new TransactionManagerReadOnly("PrintDocImpl.isRegnExistForForm38AFC");
            sql = "select a.appl_no,a.regn_no from  " + TableList.VT_FITNESS_TEMP + " a \n"
                    + " INNER JOIN " + TableList.VT_OWNER + " b on b.regn_no=a.regn_no and b.state_cd != a.state_cd \n"
                    + " where a.regn_no=? and a.state_cd=? and a.off_cd=? order by a.op_dt DESC limit 1 \n";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new PrintCertificatesDobj();
                dobj.setRegno(rs.getString("regn_no"));
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setPrintForm(printFormRadBtn);
                list.add(dobj);
            }
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
            return list;
        }
    }

    public static List<DownloadDispatchDobj> getReturnRCDispatchByHandReportDetails(String stateCode, int offCode, String enterNo, Date from_date, Date to_dat, String radipBtnValue) {
        ArrayList<DownloadDispatchDobj> list = new ArrayList();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        RowSet rs = null;
        String wherClause = null;
        try {
            TmConfigurationDobj configurationDobj = Util.getTmConfiguration();
            java.sql.Date fromDate = new java.sql.Date(from_date.getTime());
            java.sql.Date toDate = new java.sql.Date(to_dat.getTime());
            if ("APPLNO".contains(radipBtnValue)) {
                wherClause = "and a.appl_no= '" + enterNo + "' order by 1 DESC ,2 limit 1";
            } else if ("REGNNO".contains(radipBtnValue)) {
                wherClause = "and a.regn_no ='" + enterNo + "' order by 1 DESC ,2 limit 1";
            } else if ("HANDEDOVERDT".contains(radipBtnValue)) {
                wherClause = "and a.handed_over_on BETWEEN '" + fromDate + "'::date AND ('" + toDate + "'::date + '1 day'::interval -  '1 second'::interval) order by 1 DESC ,2";
            }

            tmgr = new TransactionManager("getReturnDispatchInfoDetails");
            sql = "SELECT a.return_on,a.regn_no,a.dispatch_ref_no,to_char(a.return_on,'dd-MON-yyyy') as return_date,to_char(a.handed_over_on,'dd-MON-yyyy') as handed_date,b.owner_name,b.c_address,to_char(current_date,'dd-MON-yyyy') as prit_date, \n"
                    + " c.rcpt_heading,c.rcpt_subheading,d.off_name,e.user_name as handed_by,a.remark,f.descr \n"
                    + " from " + TableList.VH_RETURN_RC_DISPATCH_BY_HAND + " a \n"
                    + " LEFT OUTER JOIN " + TableList.VHA_DISPATCH + " b on b.appl_no=a.appl_no \n"
                    + " LEFT OUTER JOIN " + TableList.TM_CONFIGURATION + " c ON c.state_cd = a.state_cd \n"
                    + " LEFT OUTER JOIN " + TableList.TM_OFFICE + " d on d.state_cd = a.state_cd and d.off_cd =a.off_cd \n"
                    + " LEFT OUTER JOIN " + TableList.TM_USER_INFO + " e on e.user_cd= regexp_replace(COALESCE(trim(a.handed_over_by::text), '0'), '[^0-9]', '0', 'g')::numeric and e.state_cd=a.state_cd and e.off_cd=a.off_cd  \n"
                    + " LEFT OUTER JOIN " + TableList.VM_RCD_RETURN_REASON + " f on f.code = a.return_reason \n"
                    + " where a.state_cd = ? and a.off_cd =? " + wherClause;
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCode);
            ps.setInt(2, offCode);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                DownloadDispatchDobj dobj = new DownloadDispatchDobj();
                dobj.setDispatch_rc_handed_on(rs.getString("handed_date"));
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setDispatch_ref_no(rs.getString("dispatch_ref_no"));
                dobj.setDispatch_rc_return_on(rs.getString("return_date"));
                dobj.setOwnerName(rs.getString("owner_name"));
                dobj.setPrinted_on(rs.getString("prit_date"));
                dobj.setRcptHeading(rs.getString("rcpt_heading"));
                dobj.setRcptSubHeading(rs.getString("rcpt_subheading"));
                dobj.setOffName(rs.getString("off_name"));
                dobj.setDispatch_rc_handed_by(rs.getString("handed_by"));
                dobj.setCaddress(rs.getString("c_address"));
                dobj.setRemark(rs.getString("remark"));
                dobj.setReturnReason(rs.getString("descr"));
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
                list.add(dobj);
            }
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
        return list;
    }

    public static CashReceiptReportDobj getCashReceiptOnlineTradeReportDobj(String rcptno) throws VahanException, ParseException {

        TransactionManager tmgr = null;
        CashReceiptReportDobj masterDobj = null;
        CashReceiptReportDobj instMasterDobj = null;
        CashRecieptSubListDobj subListDOBJ = null;
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        Boolean issurcharge = false;
        Boolean isexcessamt = false;
        Boolean iscashamt = false;
        int totalInstAmt = 0;
        try {
            String sqlOwner = " select a.*,vph.transaction_no,vp.transaction_id,"
                    + " vp.return_rcpt_no as bank_ref_no,to_char(current_timestamp,'dd-Mon-yyyy hh24:mi:ss') as printedDate \n"
                    + " from " + TableList.VP_APPL_RCPT_MAPPING + " a \n"
                    + " left outer join " + TableList.VPH_RCPT_CART + " vph on vph.appl_no = a.appl_no \n"
                    + " left outer join " + TableList.VP_PGI_DETAILS + " vp on vp.payment_id = vph.transaction_no \n"
                    + " where a.state_cd = ? and a.off_cd = ? and a.rcpt_no = ?";

            tmgr = new TransactionManager("getCashReceiptReportDobj-1");
            PreparedStatement ps = tmgr.prepareStatement(sqlOwner);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setString(3, rcptno);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                masterDobj = new CashReceiptReportDobj();
                if ("HP".contains(rs.getString("state_cd"))) {
                    masterDobj.setIshpState(true);
                } else {
                    masterDobj.setIshpState(false);
                }
                if ("DN".contains(rs.getString("state_cd"))) {
                    masterDobj.setDNStatelOGO(true);
                } else {
                    masterDobj.setDNStatelOGO(false);
                }
                masterDobj.setHeader(ServerUtil.getRcptHeading());
                masterDobj.setSubHeader(ServerUtil.getRcptSubHeading());
                masterDobj.setReceiptNo(rs.getString("rcpt_no"));
                masterDobj.setOwnerName(rs.getString("owner_name"));
                masterDobj.setApplNo(rs.getString("appl_no"));

                Map map = new HashMap();
                new ApplicationFeeTradeCertImpl().getAllSrNoForSelectedApplication(masterDobj.getApplNo(), map);
                if (!map.isEmpty()) {
                    String vehCatgDesc = "";
                    for (Object key : map.keySet()) {
                        ApplicationFeeTradeCertDobj dobj = (ApplicationFeeTradeCertDobj) map.get(key);
                        vehCatgDesc += dobj.getVehCatgName() + ",";

                    }
                    masterDobj.setVhCatg(vehCatgDesc.substring(0, vehCatgDesc.lastIndexOf(",")));
                }

                masterDobj.setTransactionId(rs.getString("transaction_id"));
                masterDobj.setBankRefNo(rs.getString("bank_ref_no"));
                masterDobj.setExcessAmt(rs.getString("excess_amt"));
                if (!CommonUtils.isNullOrBlank(masterDobj.getExcessAmt())) {
                    masterDobj.setIsecsessAmt(true);
                }
                masterDobj.setCashAmt(rs.getString("cash_amt"));
                if (!CommonUtils.isNullOrBlank(masterDobj.getCashAmt())) {
                    masterDobj.setIscashAmt(true);
                }
                masterDobj.setInstrumentcd(rs.getLong("instrument_cd"));
                masterDobj.setPrintedDate(rs.getString("printedDate"));
                if (rs.getString("remarks") != null && !rs.getString("remarks").equals("")) {
                    masterDobj.setIsbalFeeRemarks(true);
                }
                masterDobj.setBalFeeRemarks(rs.getString("remarks"));

                String sqlDealer = "select vph.appl_no as appl_no "
                        + " ,vaTC.dealer_cd,vmDealer.dealer_name \n"
                        + " from vph_rcpt_cart vph inner join va_trade_certificate vaTC \n"
                        + " on vph.appl_no = vaTC.appl_no \n"
                        + " left outer join vm_dealer_mast vmDealer on vmDealer.dealer_cd = vaTC.dealer_cd \n"
                        + " where vph.state_cd = ? and vph.off_cd = ? and vph.rcpt_no = ?"
                        + "group by vaTC.dealer_cd,vmDealer.dealer_name,vph.appl_no";
                PreparedStatement psDealer = tmgr.prepareStatement(sqlDealer);
                psDealer.setString(1, Util.getUserStateCode());
                psDealer.setInt(2, Util.getSelectedSeat().getOff_cd());
                psDealer.setString(3, rcptno);
                RowSet rsDealer = tmgr.fetchDetachedRowSet_No_release();
                if (rsDealer.next()) {
                    masterDobj.setRegnNo(rsDealer.getString("dealer_cd"));
                    masterDobj.setOwnerName(rsDealer.getString("dealer_name"));
                }

                String sqlsubList = "select v.*,c.descr as purpose from " + TableList.VPH_RCPT_CART + " v "
                        + "LEFT OUTER JOIN tm_purpose_mast c ON c.pur_cd = v.pur_cd"
                        + " where state_cd = ? and off_cd = ? and rcpt_no = ?";

                PreparedStatement psSubList = tmgr.prepareStatement(sqlsubList);
                psSubList.setString(1, Util.getUserStateCode());
                psSubList.setInt(2, Util.getSelectedSeat().getOff_cd());
                psSubList.setString(3, rcptno);
                RowSet rsList = tmgr.fetchDetachedRowSet();
                int grandTotal = 0;
                Utility utility = new Utility();
                List<CashRecieptSubListDobj> list = new ArrayList<CashRecieptSubListDobj>();
                while (rsList.next()) {
                    subListDOBJ = new CashRecieptSubListDobj();
                    if (rsList.getString("rcpt_dt") != null && !rsList.getString("rcpt_dt").equals("")) {
                        masterDobj.setReceiptDate(rsList.getString("rcpt_dt"));
                    }
                    if (rsList.getString("user_cd").equalsIgnoreCase(TableConstants.ONLINE_PAYMENT)) {
                        masterDobj.setEmpName("ONLINE PAYMENT");
                    }

                    if (rsList.getInt("pur_cd") == TableConstants.TRADE_CERTIFICATE_SURCHARGE) {
                        subListDOBJ.setBlnissurcharge(true);
                    }
                    if (rsList.getString("period_from") != null && !rsList.getString("period_from").equals("")) {
                        subListDOBJ.setDateFrom(rsList.getString("period_from"));
                    }
                    if (rsList.getString("period_upto") != null && !rsList.getString("period_upto").equals("")) {
                        subListDOBJ.setDateUpto(rsList.getString("period_upto"));
                    }
                    subListDOBJ.setPurpose(rsList.getString("purpose"));
                    if (rsList.getString("amount") != null && !rsList.getString("amount").equals("")) {
                        subListDOBJ.setAmount(rsList.getInt("amount") + "");
                    } else {
                        subListDOBJ.setAmount(rsList.getInt("amount") + "");
                    }
                    subListDOBJ.setPenalty(rsList.getInt("penalty") + "");
                    subListDOBJ.setTotal(rsList.getInt("amount") + rsList.getInt("penalty"));

                    grandTotal += subListDOBJ.getTotal();
                    list.add(subListDOBJ);
                }
                if (String.valueOf(masterDobj.getInstrumentcd()) != null && !String.valueOf(masterDobj.getInstrumentcd()).equals("") && masterDobj.getInstrumentcd() > 0) {
                    tmgr = new TransactionManager("getCashReceiptReportDobj-2");
                    String instrumentSQL = "select a.sr_no::varchar || '. ' || c.descr || ' No ' || a.instrument_no || ' dtd ' || to_char(a.instrument_dt,'dd-Mon-yyyy') || ' Amt ' || a.instrument_amt::varchar || '/- of ' || b.descr as instrument,a.instrument_amt"
                            + "  from VT_INSTRUMENTS a"
                            + "  LEFT OUTER JOIN TM_BANK b ON b.bank_code = a.bank_code"
                            + "  left outer join tm_instruments c on c.code = a.instrument_type"
                            + " where a.instrument_cd =?"
                            + " order by a.sr_no";
                    PreparedStatement psinst = tmgr.prepareStatement(instrumentSQL);
                    psinst.setLong(1, masterDobj.getInstrumentcd());
                    RowSet rsinst = tmgr.fetchDetachedRowSet();
                    List<CashReceiptReportDobj> instList = new ArrayList<CashReceiptReportDobj>();
                    while (rsinst.next()) {
                        instMasterDobj = new CashReceiptReportDobj();
                        if (!Utility.isNullOrBlank(rsinst.getString("instrument_amt"))) {
                            totalInstAmt += Integer.parseInt(rsinst.getString("instrument_amt"));
                        }

                        instMasterDobj.setInstrumentDtls(rsinst.getString("instrument"));
                        instList.add(instMasterDobj);
                    }
                    if (totalInstAmt > 0) {
                        masterDobj.setInstrumentAmt(String.valueOf(totalInstAmt));
                        masterDobj.setIsinstrumentAmt(true);
                    }
                    if (masterDobj.getExcessAmt() != null && !masterDobj.getExcessAmt().equals("") && Integer.parseInt(masterDobj.getExcessAmt()) > 0) {
                        masterDobj.setIsecsessAmt(true);
                    }
                    if (masterDobj.getCashAmt() != null && !masterDobj.getCashAmt().equals("") && Integer.parseInt(masterDobj.getCashAmt()) > 0) {
                        masterDobj.setIscashAmt(true);
                    }
                    masterDobj.setPrntInstList(instList);
                }

                masterDobj.setGrandTotal(grandTotal);
                masterDobj.setGrandTotalInWords(utility.ConvertNumberToWords(grandTotal));
                masterDobj.setPrntRecieptSubList(list);
            }
        } catch (Exception e) {
            LOGGER.error("Exception for Receipt no :" + rcptno + " :" + e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Printing Cash Receipt");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error("Exception for Receipt no :" + rcptno + " :" + e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return masterDobj;
    }

    public static ArrayList<PrintCertificatesDobj> getDetailsOfRePrint38FC(String stateCd, int offCd, String regnNo) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        PrintCertificatesDobj dobj = null;
        ArrayList<PrintCertificatesDobj> list = new ArrayList<>();
        String sql = "";
        PreparedStatement ps = null;
        RowSet rs = null;
        try {
            tmgr = new TransactionManagerReadOnly("PrintDocImpl.rePrintOf38And38AFC");
            sql = "select a.appl_no,a.regn_no,b.pur_cd,c.regn_type,info.user_name,to_char(a.printed_on,'dd-MON-yyyy') as printed_date from  " + TableList.VH_FC_PRINT + " a  \n"
                    + " left outer join " + TableList.VA_DETAILS + " b on b.appl_no = a.appl_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd and b.pur_cd=1  \n"
                    + " left outer join " + TableList.VT_OWNER + " c on c.regn_no = a.regn_no and c.state_cd = a.state_cd and c.off_cd = a.off_cd  \n"
                    + " left outer join " + TableList.TM_USER_INFO + " info on a.printed_by::numeric = info.user_cd\n"
                    + " where a.regn_no = ? and  a.off_cd = ? and a.state_cd = ? order by printed_on desc limit 1 ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setInt(2, offCd);
            ps.setString(3, stateCd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new PrintCertificatesDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegno(rs.getString("regn_no"));
                dobj.setPurCd(rs.getInt("pur_cd"));
                dobj.setRegn_type(rs.getString("regn_type"));
                dobj.setPrinted_by(rs.getString("user_name"));
                dobj.setPrinted_on(rs.getString("printed_date"));
                list.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error("Exception for Regn no :" + regnNo + " :" + e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error("Exception for Regn no :" + regnNo + " :" + e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return list;
    }

    public static VmRoadSafetySloganPrintDobj getStateRoadSafetySlogan() throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        VmRoadSafetySloganPrintDobj dobj = null;
        String sql;
        RowSet rs;
        try {
            tmgr = new TransactionManagerReadOnly("PrintDocImpl.isRegnExistForForm38AFC");
            sql = "select english_lang,state_lang from  " + TableList.VM_ROAD_SAFETY_SLOGAN_PRINT + " WHERE state_cd=? order by RANDOM() limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new VmRoadSafetySloganPrintDobj();
                dobj.setEnglish_lang(rs.getString("english_lang"));
                dobj.setState_lang(rs.getString("state_lang"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
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
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
            return dobj;
        }
    }

    public static Map getPermitParticularPrint(SessionVariables sessionVariables, String regn_no) throws VahanException {
        Map<String, String> goodsMap = new LinkedHashMap<String, String>();
        RowSet rs;
        PreparedStatement ps;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getPermitParticularPrint");
            String query;
            query = "SELECT  to_char(pmt.op_dt,'DD-Mon-YYYY') as op_date,COALESCE(pmt.region_covered,'') as region_covered,service.descr as service_type_descr,pmt.state_cd,pmt.off_cd,d.auth_no,to_char(d.auth_fr,'DD-Mon-YYYY') as auth_fr,hypth.fncr_name,to_char(d.auth_to,'DD-Mon-YYYY') as auth_to,to_char(current_date,'DD-Mon-YYYY')as currentDate,to_char(pmt.replace_date,'DD-Mon-YYYY') as replace_date,pmt.PMT_NO,to_char(pmt.VALID_FROM,'DD-Mon-YYYY') as valid_from ,to_char(pmt.VALID_UPTO,'DD-Mon-YYYY') as valid_upto,VVO.OFF_NAME AS\n"
                    + " OFF_NAME,VVO.STATE_NAME, VVO.OWNER_NAME, VVO.f_name,VVO.c_add1,VVO.c_add2,VVO.c_add3,VVO.c_district_name,VVO.c_state_name,VVO.c_pincode,VVO.chasi_no,VVO.eng_no,\n"
                    + " VVO.REGN_NO,to_char(VVO.REGN_DT,'DD-Mon-YYYY') as regn_dt,VVO.manu_yr,COALESCE(VVO.ld_wt,'0') as ld_wt,COALESCE(VVO.gcw,'0') as gcw,COALESCE(VVO.unld_wt,'0') as unld_wt,VVO.VH_CLASS_DESC,COALESCE(pmt.goods_to_carry, '') as goods_to_carry,VVO.CHASI_NO,VVO.MODEL_NAME,VVO.MAKER_NAME,VVO.SEAT_CAP,c.descr as pmt_type,g.descr as pmt_catg,pmt.pmt_type as pmt_type_code ,\n"
                    + " VVO.stand_cap,VVO.body_type,VVO.sleeper_cap,COALESCE(to_char(pmt.replace_date,'dd-Mon-yyyy'),'') as replace_date,pmt.appl_no,pmt.parking,pmt.jorney_purpose\n"
                    + " FROM " + TableList.VT_PERMIT + " pmt \n"
                    + " LEFT OUTER JOIN " + TableList.VIEW_VV_OWNER + " VVO ON VVO.REGN_NO  = pmt.REGN_NO and VVO.state_cd = pmt.state_cd\n"
                    + " LEFT OUTER JOIN " + TableList.VM_PERMIT_TYPE + " c on c.code = pmt.pmt_type\n"
                    + " LEFT OUTER JOIN " + TableList.VM_PERMIT_CATG + " g on g.code = pmt.pmt_catg AND g.permit_type = pmt.pmt_type AND g.state_cd = pmt.state_cd \n"
                    + " LEFT join " + TableList.VT_PERMIT_HOME_AUTH + " d on d.regn_no = pmt.regn_no \n"
                    + " left join " + TableList.VT_HYPTH + " hypth ON hypth.regn_no=pmt.REGN_NO and hypth.state_cd=pmt.state_cd \n"
                    + " left outer join " + TableList.VM_SERVICES_TYPE + " service on service.code=pmt.service_type \n"
                    + " WHERE pmt.state_cd=? and pmt.regn_no =?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, sessionVariables.getStateCodeSelected());
            ps.setString(2, regn_no);
            rs = tmgr.fetchDetachedRowSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    goodsMap.put(rsmd.getColumnName(i), rs.getString(i));
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please contact system administrator");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return goodsMap;
    }

    public static void printSucessAndApproveStatus(SessionVariables sessionVariables, Map<String, String> getmap, String regn_no) throws VahanException {
        TransactionManager tmgr = null;
        try {
            if (getmap != null && !getmap.isEmpty() && getmap.size() > 0 && sessionVariables != null && (sessionVariables.getSelectedWork().getPur_cd() == TableConstants.VM_PMT_PERMIT_PARTICULAR_PUR_CD || sessionVariables.getSelectedWork().getPur_cd() == TableConstants.VM_PMT_PERMIT_PARTICULAR_PUR_CD_WITHOUT_FEE)) {
                tmgr = new TransactionManager("printSucessAndApproveStatus");
                Status_dobj status = new Status_dobj();
                status.setAppl_dt(sessionVariables.getSelectedWork().getAppl_dt());
                status.setAppl_no(sessionVariables.getSelectedWork().getAppl_no());
                status.setPur_cd(sessionVariables.getSelectedWork().getPur_cd());
                status.setOffice_remark("");
                status.setPublic_remark("");
                status.setStatus("C");
                status.setCurrent_role(7);
                status.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status);
                status = ServerUtil.webServiceForNextStage(status, tmgr);
                ServerUtil.fileFlow(tmgr, status);
                String[] beanData = {"14", sessionVariables.getSelectedWork().getAppl_no(), regn_no};
                insertVhPermitPrint(tmgr, beanData);
                tmgr.commit();
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in getting Vehicle Particulars, please try after sometime or contact Administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
    }

    public static void insertVhPermitPrint(TransactionManager tmgr, String[] beanData) throws VahanException {
        String sqlQuery;
        PreparedStatement ps;
        try {
            sqlQuery = "INSERT INTO " + TableList.VH_PERMIT_PRINT + "(\n"
                    + "            appl_no, regn_no, doc_id, op_dt, printed_on, printed_by)\n"
                    + "    VALUES (?, ?, ?, CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,?)";
            ps = tmgr.prepareStatement(sqlQuery);
            ps.setString(1, beanData[1]);
            ps.setString(2, beanData[2]);
            ps.setString(3, beanData[0]);
            ps.setString(4, Util.getEmpCode());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new VahanException("Function insertVhPermitPrint : Error in function >>" + e.getMessage());
        }
    }
}
