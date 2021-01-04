/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitHomeAuthDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.dobj.permit.VmPermitRouteDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class PermitEndorsementAppImpl {

    private static final Logger LOGGER = Logger.getLogger(PermitEndorsementAppImpl.class);
    RowSet rs;
    PreparedStatement ps;
    PassengerPermitDetailImpl passPermitImpl = null;

    public List getRouteCode(String state_cd, int off_cd, String applNo) {
        String route_flag = null;
        List list = new ArrayList();
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getRouteCode");
            String query;

            query = "select route_cd from permit.va_permit_route Where state_cd = ? AND off_cd = ? and appl_no=?";

            ps = tmgr.prepareStatement(query);
            ps.setString(1, state_cd);
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setString(3, applNo);

            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                list.add(rs.getString("route_cd"));
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

    public List getVtRouteCode(String state_cd, int off_cd, String applNo) {
        String route_flag = null;
        List list = new ArrayList();
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getVtRouteCode");
            String query;

            query = "select route_cd from " + TableList.vt_permit_route + " Where state_cd = ? AND off_cd = ? and appl_no=?";

            ps = tmgr.prepareStatement(query);
            ps.setString(1, state_cd);
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setString(3, applNo);

            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                list.add(rs.getString("route_cd"));
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

    public String getRouteFlagByCode(String state_cd, int off_cd, String code) {
        String route_flag = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getRouteFlagByCode");
            String query;

            query = "select route_flag from " + TableList.VM_ROUTE_MASTER + " Where state_cd = ? AND off_cd = ? and code=?";

            ps = tmgr.prepareStatement(query);
            ps.setString(1, state_cd);
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setString(3, code);

            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                route_flag = rs.getString("route_flag");
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
        return route_flag;
    }

    public Map getSourcesRouteMap(String Appl_no, String TableName, String state_cd, int off_cd, String route_flag) {
        Map<String, String> routeMap = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getSourcesRouteMap");
            String query;
            if (route_flag == null || route_flag.trim().equals("")) {
                query = "select code, code || ' - ' || Floc || ' TO ' || Tloc as descr from " + TableList.VM_ROUTE_MASTER + " Where code not in (SELECT route_cd from " + TableName + " where appl_no=?) AND state_cd = ? AND off_cd = ? and route_flag is null ORDER BY descr";
            } else {
                query = "select code, code || ' - ' || Floc || ' TO ' || Tloc as descr from " + TableList.VM_ROUTE_MASTER + " Where code not in (SELECT route_cd from " + TableName + " where appl_no=?) AND state_cd = ? AND off_cd = ? and route_flag=? ORDER BY descr";
            }
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            if (route_flag != null && !route_flag.trim().equals("")) {
                ps.setString(4, route_flag);
            }

            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                routeMap.put(rs.getString(1), rs.getString(2));
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
        return routeMap;
    }

    public Map getTargetRouteMap(String Appl_no, String TableName, String state_cd, int off_cd, boolean otherOffRouteAllow) {
        Map<String, String> priRouteMap = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getTargetRouteMap");
            String query;
            if (otherOffRouteAllow) {
                query = "select code, code || ' - ' || Floc || ' TO ' || Tloc as descr from " + TableList.VM_ROUTE_MASTER + " Where code in (SELECT route_cd from " + TableName + " where appl_no=? and off_cd=" + off_cd + ")AND state_cd = ? AND off_cd = ? and route_flag is null ORDER BY descr";
            } else {
                query = "select code, code || ' - ' || Floc || ' TO ' || Tloc as descr from " + TableList.VM_ROUTE_MASTER + " Where code in (SELECT route_cd from " + TableName + " where appl_no=?)AND state_cd = ? AND off_cd = ? and route_flag is null ORDER BY descr";
            }
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                priRouteMap.put(rs.getString(1), rs.getString(2));
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
        return priRouteMap;
    }

    public Map getTargetRouteMapByFlag(String Appl_no, String TableName, String state_cd, int off_cd) {
        Map<String, String> priRouteMap = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getTargetRouteMapByFlag");
            String query;
            query = "select code, code || ' - ' || Floc || ' TO ' || Tloc as descr from " + TableList.VM_ROUTE_MASTER + " Where code in (SELECT route_cd from " + TableName + " where appl_no=?)AND state_cd = ? AND off_cd = ? and route_flag is not null ORDER BY descr";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                priRouteMap.put(rs.getString(1), rs.getString(2));
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
        return priRouteMap;
    }

    public Map getTargetAreaMap(String state_cd, int off_cd) {
        Map<String, String> priRouteMap = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getTargetAreaMap");
            String query;

            query = "select region_cd,region from " + TableList.VM_REGION + " where state_cd=? AND off_cd=? ORDER BY region";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, state_cd);
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                priRouteMap.put(rs.getString(1), rs.getString(2));
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
        return priRouteMap;
    }

    public Map getSourcesAreaMapWithStrBuil(String stringBuilder, String state_cd, int off_cd) {
        Map<String, String> priRouteMap = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getSourcesAreaMapWithStrBuil");
            String query;
            String rr = stringBuilder;
            if (rr.endsWith(",")) {

                rr = rr.substring(0, rr.length() - 1);
            }

            query = "select region_cd,region from " + TableList.VM_REGION + " where (region_cd) not in (" + rr + ") AND state_cd = ? AND off_cd = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, state_cd);
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                priRouteMap.put(rs.getString(1), rs.getString(2));
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
        return priRouteMap;
    }

    public Map getTargetAreaMapWithStrBuil(String stringBuilder, String state_cd, int off_cd) {
        Map<String, String> priRouteMap = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getTargetAreaMapWithStrBuil");
            String query;
            String rr = stringBuilder;
            if (rr.endsWith(",")) {

                rr = rr.substring(0, rr.length() - 1);
            }

            query = "select region_cd,region from " + TableList.VM_REGION + " where (region_cd) in (" + rr + ") AND state_cd = ? AND off_cd = ? ";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, state_cd);
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                priRouteMap.put(rs.getString(1), rs.getString(2));
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
        return priRouteMap;
    }

    public String detailRegn_no(String app_no) {
        String flage = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("detailRegn_no");
            String query;
            query = "select regn_no from " + TableList.VA_PERMIT + " where appl_no=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, app_no);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                flage = rs.getString("regn_no");
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
        return flage;
    }

    public void stayOnTheSameStage(PassengerPermitDetailDobj dobj, List route_code, String appl_no, String changedata) {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("stayOnTheSameStage");
            if (!changedata.isEmpty()) {
                passPermitImpl = new PassengerPermitDetailImpl();
                passPermitImpl.insertVHA_PermitandUpdateVa_Permit(tmgr, dobj, appl_no, null);
                passPermitImpl.insertVHA_RouteandUpdateVa_Route(tmgr, dobj, appl_no, route_code, false, null);
                insertIntoVhaChangedData(tmgr, appl_no, changedata);
                tmgr.commit();
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
    }

    public boolean getVastatueRoute(String appl_no) {
        boolean flage = false;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getVastatueRoute");
            String query;
            query = "select appl_no from " + TableList.va_permit_route + " where appl_no = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                flage = true;
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
        return flage;
    }

    public String getNoOfTrip(String appl_no) {
        String trip = "";
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getNoOfTrip");
            String query;
            query = "select no_of_trips from " + TableList.vt_permit_route + " where appl_no=?  ";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                trip = String.valueOf(rs.getInt("no_of_trips"));
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
        return trip;
    }

    public static PassengerPermitDetailDobj getPermitdetails(String regn_no) {
        PassengerPermitDetailDobj dobj = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getPermitdetails");
            String sql = " SELECT state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from,"
                    + "valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, "
                    + " service_type, goods_to_carry, jorney_purpose, parking, replace_date, "
                    + " remarks, op_dt"
                    + " FROM " + TableList.VT_PERMIT + " where regn_no=?";

            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new PassengerPermitDetailDobj();
                dobj.setRegnNo(rs.getString("regn_no"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(String.valueOf(rs.getInt("off_cd")));
                dobj.setPmt_no(rs.getString("pmt_no"));
                dobj.setValid_from(rs.getDate("valid_from"));
                dobj.setValid_upto(rs.getDate("valid_upto"));
                dobj.setGoods_TO_CARRY(rs.getString("goods_to_carry"));
                dobj.setJoreny_PURPOSE(rs.getString("jorney_purpose"));
                dobj.setParking(rs.getString("parking"));
                dobj.setApplNo(rs.getString("appl_no"));
                dobj.setDomain_CODE(rs.getString("domain_cd"));
                dobj.setRegion_covered(rs.getString("region_covered"));
                dobj.setRemarks(rs.getString("remarks"));
                dobj.setReplaceDate(rs.getDate("replace_date"));
                dobj.setPmt_type(String.valueOf(rs.getInt("pmt_type")));
                dobj.setPmtCatg(rs.getString("pmt_catg"));
                dobj.setOrder_dt(rs.getDate("op_dt"));
                dobj.setServices_TYPE(String.valueOf(rs.getInt("service_type")));
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
        return dobj;
    }

    public String getRouteViaRouteList(String appl_no, String state_cd, String TableName, int off_cd, boolean otherOfficeRoute) {
        String via = "";
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("get Route Via");
            String Query = "";
            if (otherOfficeRoute) {
                Query = "Select code ||': '|| via ||'.<br/><br/> ' as via from " + TableList.VM_ROUTE_MASTER + " where code in (SELECT route_cd from " + TableName + " where appl_no=? and off_cd=" + off_cd + ") AND state_cd = ? and off_cd=? and route_flag is null";
            } else {
                Query = "Select code ||': '|| via ||'.<br/><br/> ' as via from " + TableList.VM_ROUTE_MASTER + " where code in (SELECT route_cd from " + TableName + " where appl_no=?) AND state_cd = ? and off_cd=? and route_flag is null";
            }
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                via += rs.getString("via");
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
        return via;
    }

    public String getRouteViaRouteListByFlag(String appl_no, String state_cd, String TableName, int off_cd) {
        String via = "";
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("get Route Via");
            String Query = "Select code ||': '|| via ||'.<br/><br/> ' as via from " + TableList.VM_ROUTE_MASTER + " where code in (SELECT route_cd from " + TableName + " where appl_no=?) AND state_cd = ? and off_cd=? and route_flag is not null";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                via += rs.getString("via");
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
        return via;
    }

    public List<PassengerPermitDetailDobj> getRouteAndOtherData(String state_cd, int off_cd, PassengerPermitDetailDobj pmtDobj, int actionCd) {
        String floc = "";
        String tloc = "";
        String code = "";
        String via = "";
        String tableName = "";
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        RowSet rs = null;
        List<PassengerPermitDetailDobj> routeDetailslist = null;
        PassengerPermitDetailDobj dobj = null;
        PermitEndorsementAppImpl impl = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            tmgr = new TransactionManager("Get Permit Route & Other Detail");
            routeDetailslist = new ArrayList<PassengerPermitDetailDobj>();
            impl = new PermitEndorsementAppImpl();
            dobj = new PassengerPermitDetailDobj();

            if (actionCd == TableConstants.TM_ROLE_PMT_VARIATION_ENDORSEMENT_APPL || actionCd == TableConstants.TM_ROLE_PMT_COUNTER_SIGNATURE_ENTRY) {
                tableName = TableList.VT_PERMIT;
            } else if (actionCd == TableConstants.TM_ROLE_PMT_VARIATION_ENDORSEMENT_APPROVAL
                    || actionCd == TableConstants.TM_ROLE_PMT_VARIATION_ENDORSEMENT_VERIFICATION) {
                tableName = TableList.VH_PERMIT;
            }
            String regionDetails = getRegionDetail(pmtDobj.getApplNo(), tableName);
            if (!CommonUtils.isNullOrBlank(regionDetails)) {
                dobj.setRegion_covered(regionDetails);
            } else {
                dobj.setRegion_covered("");
            }
            if (actionCd == TableConstants.TM_ROLE_PMT_COUNTER_SIGNATURE_ENTRY) {
                String sql = "select code, floc ,tloc,via  from " + TableList.VM_ROUTE_MASTER + " Where code in (SELECT route_cd from " + TableList.vt_permit_route + " where appl_no=?) AND state_cd = ? and off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, pmtDobj.getApplNo());
                ps.setString(2, state_cd);
                ps.setInt(3, off_cd);
            } else {
                String sql = "select code, floc ,tloc,via  from " + TableList.VM_ROUTE_MASTER + " Where (state_cd,off_cd,code) in (SELECT state_cd,off_cd,route_cd from " + TableList.vt_permit_route + " where appl_no=?) AND state_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, pmtDobj.getApplNo());
                ps.setString(2, state_cd);
            }

            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                floc += rs.getString("floc") + "|";
                tloc += rs.getString("tloc") + "|";
                code += rs.getString("code") + "|";
                via += rs.getString("via") + "|";
            }

            if (!CommonUtils.isNullOrBlank(floc)) {
                dobj.setFloc(floc);
            } else {
                dobj.setFloc("");
            }

            if (!CommonUtils.isNullOrBlank(tloc)) {
                dobj.setTloc(tloc);
            } else {
                dobj.setTloc("");
            }

            if (!CommonUtils.isNullOrBlank(code)) {
                dobj.setRout_code(code);
            } else {
                dobj.setRout_code("");
            }

            if (!CommonUtils.isNullOrBlank(via)) {
                dobj.setStart_POINT(via);
            } else {
                dobj.setStart_POINT("");
            }

            if (!CommonUtils.isNullOrBlank(pmtDobj.getParking())) {
                dobj.setParking(pmtDobj.getParking());
            } else {
                dobj.setParking("");
            }

            if (!CommonUtils.isNullOrBlank(pmtDobj.getGoods_TO_CARRY())) {
                dobj.setGoods_TO_CARRY(pmtDobj.getGoods_TO_CARRY());
            } else {
                dobj.setGoods_TO_CARRY("");
            }

            if (!CommonUtils.isNullOrBlank(pmtDobj.getJoreny_PURPOSE())) {
                dobj.setJoreny_PURPOSE(pmtDobj.getJoreny_PURPOSE());
            } else {
                dobj.setJoreny_PURPOSE("");
            }

            if (pmtDobj.getOrder_dt() != null) {
                dobj.setValidFromInString(formatter.format(pmtDobj.getOrder_dt()));
            } else {
                dobj.setValidFromInString("");
            }

            if (!CommonUtils.isNullOrBlank(impl.getNoOfTrip(pmtDobj.getApplNo()))) {
                dobj.setNumberOfTrips(impl.getNoOfTrip(pmtDobj.getApplNo()));
            } else {
                dobj.setNumberOfTrips("");
            }
            routeDetailslist.add(dobj);
        } catch (Exception e) {
            routeDetailslist = null;
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
        return routeDetailslist;
    }

    public PassengerPermitDetailDobj getDataFromVhtoVt(String regn_no) {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        RowSet rs = null;

        PassengerPermitDetailDobj dobj = null;
        try {
            tmgr = new TransactionManager("getDataFromVhPermit");
            String query = "select pmt_no,issue_dt,valid_from,valid_upto,replace_date from " + TableList.VH_PERMIT + "\n"
                    + " where vh_permit.regn_no=? and pmt_status=? AND MOVED_ON=(SELECT MAX(MOVED_ON) FROM PERMIT.VH_PERMIT WHERE REGN_NO=?) ";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, regn_no);
            ps.setString(2, "VAR");
            ps.setString(3, regn_no);
            rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {

                dobj = new PassengerPermitDetailDobj();
                dobj.setPmt_no(rs.getString("pmt_no"));
                //dobj.setIssue_date(rs.getDate("issue_dt"));
                dobj.setValid_from(rs.getDate("valid_from"));
                dobj.setValid_upto(rs.getDate("valid_upto"));
                dobj.setReplaceDate(rs.getDate("replace_date"));
            }

        } catch (Exception e) {
            dobj = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                    rs.close();
                }
            } catch (Exception e) {
                dobj = null;
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dobj;
    }

    public String get_ApplicationNumber(PassengerPermitDetailDobj permit_Dobj, List route_code, InsDobj ins_dobj, List route_list, boolean tripExtended) throws VahanException {
        String app_no = null;
        TransactionManager tmgr = null;
        PermitCheckDetailsImpl pmtDtlsImpl = null;
        try {
            tmgr = new TransactionManager("get_ApplicationNumber with insert");
            app_no = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
            passPermitImpl = new PassengerPermitDetailImpl();
            if (!("").equals(app_no) && !(app_no == null)) {
                passPermitImpl.insert_into_vaPermit(app_no, permit_Dobj, tmgr);
                insertVH_PermitfromVt_Permit(tmgr, permit_Dobj);
                if (!(permit_Dobj.getPmt_type_code().equalsIgnoreCase(TableConstants.AITP)
                        || permit_Dobj.getPmt_type_code().equalsIgnoreCase(TableConstants.NATIONAL_PERMIT))) {
                    passPermitImpl.insert_into_vaPermitRoute(app_no, permit_Dobj, route_code, tmgr, route_list);
                }
                if (ins_dobj != null) {
                    pmtDtlsImpl = new PermitCheckDetailsImpl();
                    pmtDtlsImpl.insertIntoVaInsurance(app_no, permit_Dobj.getRegnNo(), ins_dobj, tmgr);
                }
                passPermitImpl.newApplPermitApplicatonFee(app_no, permit_Dobj, tmgr, TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD, TableConstants.TM_ROLE_PMT_VARIATION_ENDORSEMENT_APPL, tripExtended);
                tmgr.commit();
            }
        } catch (VahanException e) {
            app_no = "";
            throw e;
        } catch (Exception e) {
            app_no = "";
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
        return app_no;
    }

    public void insertVH_PermitfromVt_Permit(TransactionManager tmgr, PassengerPermitDetailDobj dobj) throws SQLException {
        String query;

        query = "INSERT INTO " + TableList.VH_PERMIT + "(\n"
                + "            state_cd, off_cd, appl_no, pmt_no,regn_no,  issue_dt, valid_from, \n"
                + "            valid_upto,rcpt_no,pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, service_type, \n"
                + "            goods_to_carry, jorney_purpose, parking, replace_date,remarks, op_dt, pmt_status,reason, \n"
                + "             moved_on,moved_by)\n"
                + "   SELECT  state_cd, off_cd, appl_no, pmt_no,regn_no,issue_dt, valid_from, \n"
                + "           valid_upto,rcpt_no,pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, service_type, \n"
                + "           goods_to_carry, jorney_purpose, parking, replace_date, \n"
                + "           remarks, op_dt,?,?,Current_timestamp,\n"
                + "           ?\n"
                + "  FROM " + TableList.VT_PERMIT + " where regn_no=?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, "END");
        ps.setString(2, "PERMIT ENDORSEMENT");
        ps.setString(3, Util.getEmpCode());
        ps.setString(4, dobj.getRegnNo());
        ps.executeUpdate();
        query = "DELETE FROM " + TableList.VT_PERMIT + " where regn_no=?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, dobj.getRegnNo());
        ps.executeUpdate();
    }

    public String getRouteVia(List route_code, String state_cd, int offCd) {
        String via = "";
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("get Route Via");
            for (Object code : route_code) {
                String Query = "Select code ||': '|| via ||'.<br/><br/> ' as via from " + TableList.VM_ROUTE_MASTER + " where code = ? AND State_cd = ? AND off_cd = ?";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, (String) code);
                ps.setString(2, state_cd);
                ps.setInt(3, offCd);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    via += rs.getString("via");
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
        return via;
    }

    public List<PassengerPermitDetailDobj> getOtherDetail(PassengerPermitDetailDobj dobjs) {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        RowSet rs = null;
        List<PassengerPermitDetailDobj> routeDetailslist = null;
        PassengerPermitDetailDobj dobj = null;
        try {
            routeDetailslist = new ArrayList<PassengerPermitDetailDobj>();
            dobj = new PassengerPermitDetailDobj();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
            dobj.setParking(dobjs.getParking());
            dobj.setGoods_TO_CARRY(dobjs.getGoods_TO_CARRY());
            dobj.setJoreny_PURPOSE(dobjs.getJoreny_PURPOSE());
            if (dobjs.getOrder_dt() != null) {
                dobj.setValidFromInString(formatter.format(dobjs.getOrder_dt()));
            }
            routeDetailslist.add(dobj);
            if (dobj == null) {
                routeDetailslist = null;
            }
        } catch (Exception e) {
            routeDetailslist = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                    rs.close();
                }
            } catch (Exception e) {
                routeDetailslist = null;
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return routeDetailslist;
    }

    public String getRegionDetail(String appl_no, String tableName) {
        String region = null;
        TransactionManager tmgr = null;
        RowSet rs = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("Get Region Detail");
            String query;
            query = "select array_to_string (array(select r.region from " + tableName + " pmt \n"
                    + "LEFT JOIN " + TableList.VM_REGION + " r on r.off_cd=pmt.off_cd and r.state_cd=pmt.state_cd \n"
                    + "WHERE pmt.appl_no=? and r.region_cd = ANY(string_to_array(regexp_replace(pmt.region_covered,',$','') ,',')::integer[])),',') as regionAll";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                region = rs.getString("regionAll");
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
        return region;
    }

    public String endorsementPermitApproval(String appl_no, List route_code, String regn_no, Status_dobj status_dobj, PassengerPermitDetailDobj dobj, boolean otherOffRouteAllow, List<VmPermitRouteDobj> otherOffRoutecode) throws VahanException {
        TransactionManager tmgr = null;
        boolean valid = false;
        String query;
        int pmt_type;
        String return_location = "";
        try {
            passPermitImpl = new PassengerPermitDetailImpl();
            tmgr = new TransactionManager("endorsementPermitApproval");
            if (status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_DISPATCH_PENDING)) {
                ServerUtil.fileFlow(tmgr, status_dobj);
                tmgr.commit();
            } else {
                passPermitImpl.insertVHA_PermitandUpdateVa_Permit(tmgr, dobj, appl_no, null);
                query = "select * from " + TableList.VH_PERMIT + " where vh_permit.regn_no=? and pmt_status=? AND MOVED_ON=(SELECT MAX(MOVED_ON) FROM " + TableList.VH_PERMIT + " WHERE REGN_NO=?)";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, regn_no);
                ps.setString(2, "END");
                ps.setString(3, regn_no);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    query = "INSERT INTO " + TableList.VT_PERMIT + " (\n"
                            + "            state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                            + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered, \n"
                            + "            service_type, goods_to_carry, jorney_purpose, parking, replace_date, \n"
                            + "            remarks, op_dt)\n"
                            + "    SELECT  state_cd, off_cd, appl_no , ?, regn_no, ?, ?,\n"
                            + "            ?,rcpt_no, pur_cd, ?, ?, domain_cd, region_covered, service_type, \n"
                            + "            goods_to_carry, jorney_purpose, parking, ?,\n"
                            + "            remarks, CURRENT_TIMESTAMP\n"
                            + "  FROM " + TableList.VA_PERMIT + " where permit.va_permit.appl_no = ?";
                    ps = tmgr.prepareStatement(query);
                    int i = 1;
                    ps.setString(i++, rs.getString("pmt_no"));
                    ps.setDate(i++, rs.getDate("issue_dt"));
                    ps.setDate(i++, rs.getDate("valid_from"));
                    ps.setDate(i++, rs.getDate("valid_upto"));
                    ps.setInt(i++, rs.getInt("pmt_type"));
                    pmt_type = rs.getInt("pmt_type");
                    ps.setInt(i++, rs.getInt("pmt_catg"));
                    ps.setDate(i++, rs.getDate("replace_date"));
                    ps.setString(i++, appl_no);
                    ps.executeUpdate();
                    passPermitImpl.insertVHA_RouteandUpdateVa_Route(tmgr, dobj, appl_no, route_code, otherOffRouteAllow, otherOffRoutecode);
                    valid = getVastatueRoute(appl_no);
                    if (valid) {
                        query = "INSERT INTO " + TableList.vt_permit_route + " (state_cd,off_cd,appl_no,route_cd,no_of_trips)\n"
                                + "SELECT * FROM " + TableList.va_permit_route + "\n"
                                + "WHERE appl_no=?";
                        ps = tmgr.prepareStatement(query);
                        ps.setString(1, appl_no);
                        ps.executeUpdate();
                    }
                    query = "DELETE FROM " + TableList.VA_PERMIT + " where appl_no=?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, appl_no);
                    ps.executeUpdate();

                    String docId = CommonPermitPrintImpl.getPermitDocIdForQuery(CommonPermitPrintImpl.getPermitDocId(tmgr, pmt_type, TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD, Util.getUserStateCode()));
                    String[] beanData = {docId, appl_no, regn_no};
                    CommonPermitPrintImpl.insertVaPermitPrint(tmgr, beanData);
                    status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
                    ServerUtil.fileFlow(tmgr, status_dobj);
                    tmgr.commit();
                    return_location = "seatwork";
                }
            }
        } catch (VahanException e) {
            return_location = "";
            throw e;
        } catch (Exception e) {
            return_location = "";
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
        return return_location;
    }

    public String update_in_tables(PassengerPermitDetailDobj dobj, List route_code, String appl_no, Status_dobj status_dobj, String CompairChange, boolean otherOffRouteAllow, List<VmPermitRouteDobj> otherOffRoutecode) throws VahanException {
        String flag = "fail";
        TransactionManager tmgr = null;
        Status_dobj statDobj = status_dobj;
        try {
            tmgr = new TransactionManager("update_in_tables");
            if (statDobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_DISPATCH_PENDING)) {
                ServerUtil.fileFlow(tmgr, statDobj);
                tmgr.commit();
            } else {
                passPermitImpl = new PassengerPermitDetailImpl();
                passPermitImpl.insertVHA_PermitandUpdateVa_Permit(tmgr, dobj, appl_no, null);
                passPermitImpl.insertVHA_RouteandUpdateVa_Route(tmgr, dobj, appl_no, route_code, otherOffRouteAllow, otherOffRoutecode);
                insertIntoVhaChangedData(tmgr, appl_no, CompairChange);
                statDobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
                ServerUtil.fileFlow(tmgr, statDobj);
                tmgr.commit();
                flag = "seatwork";
            }
        } catch (VahanException e) {
            flag = "";
            throw e;
        } catch (Exception e) {
            flag = "";
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
        return flag;
    }

    public void rebackStatus(PermitOwnerDetailDobj owner_dobj, Status_dobj status_dobj, String Change, PassengerPermitDetailDobj dobj, List route_code, String appl_no) {
        TransactionManager tmgr = null;
        boolean valid = false;
        try {
            tmgr = new TransactionManager("rebackStatus");
            if (!Change.isEmpty()) {
                passPermitImpl.insertVHA_PermitandUpdateVa_Permit(tmgr, dobj, appl_no, null);
                valid = getVastatueRoute(appl_no);
                if (valid) {
                    passPermitImpl.insertVHA_RouteandUpdateVa_Route(tmgr, dobj, appl_no, route_code, false, null);
                }
            }
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            ServerUtil.fileFlow(tmgr, status_dobj);
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

    public PassengerPermitDetailDobj set_prv_permit_appl_to_dobj(String regn_no) {
        TransactionManager tmgr = null;
        PassengerPermitDetailDobj permit_dobj = null;
        try {
            tmgr = new TransactionManager("Owner_Impl");
            String query;
            query = "select vh_permit.*,to_char(replace_date,'DD-MON-YYYY') as replaceDateInString from " + TableList.VH_PERMIT + " where vh_permit.regn_no=? and pmt_status=? order by MOVED_ON DESC limit 1";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, regn_no);
            ps.setString(2, "END");
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                permit_dobj = new PassengerPermitDetailDobj();
                permit_dobj.setApplNo(rs.getString("appl_no"));
                permit_dobj.setRegion_covered(rs.getString("region_covered"));
                permit_dobj.setRegnNo(rs.getString("regn_no"));
                permit_dobj.setPmt_no(rs.getString("pmt_no"));
                //permit_dobj.setIssue_date(rs.getDate("issue_dt"));
                permit_dobj.setValid_from(rs.getDate("valid_from"));
                permit_dobj.setValid_upto(rs.getDate("valid_upto"));
                permit_dobj.setPmt_type(String.valueOf(rs.getInt("pmt_type")));
                permit_dobj.setPmtCatg(String.valueOf(rs.getInt("pmt_catg")));
                permit_dobj.setDomain_CODE(String.valueOf(rs.getInt("domain_cd")));
                permit_dobj.setServices_TYPE(String.valueOf(rs.getInt("service_type")));
                permit_dobj.setGoods_TO_CARRY(rs.getString("goods_to_carry"));
                permit_dobj.setJoreny_PURPOSE(rs.getString("jorney_purpose"));
                permit_dobj.setParking(rs.getString("parking"));
                permit_dobj.setPaction(String.valueOf(rs.getInt("pur_cd")));
                permit_dobj.setOrder_dt(rs.getDate("op_dt"));
                permit_dobj.setStart_POINT(rs.getString("remarks"));
                if (CommonUtils.isNullOrBlank(rs.getString("replaceDateInString"))) {
                    permit_dobj.setReplaceDateInString("");
                } else {
                    permit_dobj.setReplaceDateInString(rs.getString("replaceDateInString"));
                }
                if (rs.getDate("replace_date") != null) {
                    permit_dobj.setReplaceDate(rs.getDate("replace_date"));
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
        return permit_dobj;
    }

    public void vha_status_to_va_status(TransactionManager tmgr, Status_dobj status_dobj) throws SQLException {
        String Query = "";
        Query = "INSERT INTO " + TableList.VHA_STATUS + "(\n"
                + "            state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno, \n"
                + "            action_cd, seat_cd, cntr_id, status, office_remark, public_remark, \n"
                + "            file_movement_type, emp_cd, op_dt, moved_on, entry_ip)\n"
                + "   SELECT   state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno, \n"
                + "            action_cd, seat_cd, cntr_id, status, office_remark, public_remark, \n"
                + "            file_movement_type, emp_cd, op_dt,CURRENT_TIMESTAMP, ?"
                + "  FROM " + TableList.VA_STATUS + " where appl_no=? and pur_cd=?";
        ps = tmgr.prepareStatement(Query);
        ps.setString(1, Util.getClientIpAdress());
        ps.setString(2, status_dobj.getAppl_no());
        ps.setInt(3, status_dobj.getPur_cd());
        ps.executeUpdate();

        Query = "delete from " + TableList.VA_STATUS + " where appl_no=? and pur_cd=?";
        ps = tmgr.prepareStatement(Query);
        ps.setString(1, status_dobj.getAppl_no());
        ps.setInt(2, status_dobj.getPur_cd());
        ps.executeUpdate();
    }

    public void revertEndorsmentApplication(TransactionManager tmgr, String regn_no) throws SQLException {
        String Query = "";
        Query = "INSERT INTO " + TableList.VT_PERMIT + "(\n"
                + "            state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from,\n"
                + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered,\n"
                + "            service_type, goods_to_carry, jorney_purpose, parking, replace_date,\n"
                + "            remarks, op_dt)\n"
                + "  SELECT state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from,\n"
                + "       valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, domain_cd, region_covered,\n"
                + "       service_type, goods_to_carry, jorney_purpose, parking, replace_date,\n"
                + "       remarks, op_dt\n"
                + "  FROM " + TableList.VH_PERMIT + " where regn_no  = ? AND pmt_status = ? order by moved_on DESC limit 1";
        ps = tmgr.prepareStatement(Query);
        ps.setString(1, regn_no);
        ps.setString(2, "END");
        ps.executeUpdate();
    }

    public PermitHomeAuthDobj getAuthDetails(String regn_no, String pmt_no) {
        String Query = "";
        PreparedStatement ps;
        TransactionManagerReadOnly tmgr = null;
        PermitHomeAuthDobj auth_dobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("authValidate");
            Query = "SELECT auth_no,auth_fr,auth_to from " + TableList.VT_PERMIT_HOME_AUTH + "  where regn_no = ? and pmt_no=? ";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regn_no);
            ps.setString(2, pmt_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                auth_dobj = new PermitHomeAuthDobj();
                auth_dobj.setAuthNo(rs.getString("auth_no"));
                auth_dobj.setAuthFrom(rs.getDate("auth_fr"));
                auth_dobj.setAuthUpto(rs.getDate("auth_to"));
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
        return auth_dobj;
    }
}
