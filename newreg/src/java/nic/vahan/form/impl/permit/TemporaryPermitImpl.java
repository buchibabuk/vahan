/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.bean.permit.PermitRouteList;
import nic.vahan.form.bean.permit.SpecialPassengerDetailBean;
import nic.vahan.form.bean.permit.SpecialRouteDtlsDescBean;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.permit.PreviousTempPermitDtlsDobj;
import nic.vahan.form.dobj.permit.PreviousTempPermitDtlsList;
import nic.vahan.form.bean.permit.SpecialRoutePermitBean;
import nic.vahan.form.dobj.permit.SpecialRoutePermitDobj;
import nic.vahan.form.dobj.permit.TemporaryPermitDobj;
import nic.vahan.form.impl.Receipt_Master_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class TemporaryPermitImpl {

    private static final Logger LOGGER = Logger.getLogger(TemporaryPermitImpl.class);
    private static String recpt_no = "";
    private static String appl_no = "";
    RowSet rs = null;
    int pur_cd;
    String action_cd = String.valueOf(Util.getSelectedSeat().getAction_cd());

    public String createRecpt_no(TransactionManager tmgr) {
        try {
            recpt_no = Receipt_Master_Impl.generateNewRcptNo(Util.getUserOffCode(), tmgr);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return recpt_no;
    }
// manoj

    public String getRouteVia(List route_code, String state_cd) {
        String via = "";
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getRouteVia");
            for (Object code : route_code) {
                String Query = "Select code ||': '|| via ||'.<br/><br/> ' as via from " + TableList.VM_ROUTE_MASTER + " where code = ? AND State_cd = ?";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, (String) code);
                ps.setString(2, state_cd);
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
// manoj

    public String getRegion_Area(String appl_no, String table_name) {
        String region = "";
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getRegion_Area");
            String Query = "select region_covered from  " + table_name + " where appl_no=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                region = rs.getString("region_covered");
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
// manoj

    public String getRouteViaRouteList(String appl_no, String state_cd, String TableName) {
        String via = "";
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("get Route Via");
            String Query = "Select code ||': '|| via ||'.<br/><br/> ' as via from " + TableList.VM_ROUTE_MASTER + " where code in (SELECT route_cd from " + TableName + " where appl_no=?) AND State_cd = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            ps.setString(2, state_cd);
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

    public String savefeeDetails(PermitDetailDobj pmt_dobj, TemporaryPermitDobj temp_dobj, List route_code, boolean route_status, boolean renew_temp, boolean spl_route_status, List<PreviousTempPermitDtlsList> priviousTempData, InsDobj ins_dobj, SpecialRoutePermitBean splbean, boolean spl_pass_status, SpecialPassengerDetailBean splPassBean, SpecialRouteDtlsDescBean splRouteDtlsDesc, boolean spl_route_length) {
        TransactionManager tmgr = null;
        PermitCheckDetailsImpl pmtDtlsImpl = null;
        try {
            tmgr = new TransactionManager("Save Fee Details");
            appl_no = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
//          madhurendra on 28-2-19
            if (spl_route_length) {
                insertIntoVaSplRouteLengthPermit(tmgr, temp_dobj, appl_no);
            }
//          end
            insertIntoVaTempPermit(tmgr, pmt_dobj, temp_dobj, appl_no, renew_temp, priviousTempData);
            if (route_status) {
                insert_into_vatempPermitRoute(appl_no, temp_dobj, route_code, tmgr);
            }
            if (spl_route_status && Integer.parseInt(action_cd) == TableConstants.TM_ROLE_PMT_SPECIAL_APPL && !splbean.getSpecialRouteList().isEmpty()) {
                insert_into_va_spl_route_passenger(appl_no, temp_dobj, splbean.getSpecialRouteList(), tmgr, splbean.isCheckPassengerList());
            }
            if (Integer.parseInt(action_cd) == TableConstants.TM_ROLE_PMT_SPECIAL_APPL && !splRouteDtlsDesc.getSplRouteDobjList().isEmpty()) {
                insert_into_va_spl_route_passenger(appl_no, temp_dobj, splRouteDtlsDesc.getSplRouteDobjList(), tmgr, false);
            }

            if (ins_dobj != null) {
                pmtDtlsImpl = new PermitCheckDetailsImpl();
                pmtDtlsImpl.insertIntoVaInsurance(appl_no, temp_dobj.getRegn_no().toUpperCase(), ins_dobj, tmgr);
            }
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
            status.setAppl_no(appl_no);
            if (action_cd == null) {
                if (renew_temp && priviousTempData != null) {
                    status.setPur_cd(TableConstants.VM_PMT_RENEW_TEMP_PUR_CD);
                    status.setAction_cd(TableConstants.TM_ROLE_PMT_TEMP_APPL);
                } else {
                    if (pur_cd == TableConstants.VM_PMT_TEMP_PUR_CD) {
                        status.setPur_cd(TableConstants.VM_PMT_TEMP_PUR_CD);
                        status.setAction_cd(TableConstants.TM_ROLE_PMT_TEMP_APPL);
                    } else if (pur_cd == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                        status.setPur_cd(TableConstants.VM_PMT_SPECIAL_PUR_CD);
                        status.setAction_cd(TableConstants.TM_ROLE_PMT_SPECIAL_APPL);
                    }
                }
            } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_APPL))) {
                status.setPur_cd(TableConstants.VM_PMT_SPECIAL_PUR_CD);
                status.setAction_cd(TableConstants.TM_ROLE_PMT_SPECIAL_APPL);
            } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_TEMP_APPL))) {
                if (renew_temp && priviousTempData != null) {
                    status.setPur_cd(TableConstants.VM_PMT_RENEW_TEMP_PUR_CD);
                    status.setAction_cd(TableConstants.TM_ROLE_PMT_TEMP_APPL);
                } else {
                    status.setPur_cd(TableConstants.VM_PMT_TEMP_PUR_CD);
                    status.setAction_cd(TableConstants.TM_ROLE_PMT_TEMP_APPL);
                }
            }
            status.setFlow_slno(1);
            status.setFile_movement_slno(1);
            if (action_cd == null) {
                status.setEmp_cd(0);
            } else {
                status.setEmp_cd(Long.parseLong(Util.getEmpCode()));
            }
            status.setRegn_no(temp_dobj.getRegn_no().toUpperCase());
            status.setOffice_remark("");
            status.setPublic_remark("");
            status.setStatus("N");
            if (action_cd == null) {
                status.setState_cd(temp_dobj.getState_cd());
                status.setOff_cd(temp_dobj.getOff_cd());
            } else {
                status.setState_cd(Util.getUserStateCode());
                status.setOff_cd(Util.getSelectedSeat().getOff_cd());
            }

            ServerUtil.fileFlowForNewApplication(tmgr, status);

            status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
            status.setStatus(TableConstants.STATUS_COMPLETE);
            status.setAppl_no(appl_no);
            status.setOffice_remark("");
            status.setPublic_remark("");
            if (action_cd == null) {
                if (renew_temp && priviousTempData != null) {
                    ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, appl_no,
                            TableConstants.TM_ROLE_PMT_TEMP_APPL, TableConstants.VM_PMT_RENEW_TEMP_PUR_CD, null, tmgr);
                } else {
                    if (pur_cd == TableConstants.VM_PMT_TEMP_PUR_CD) {
                        ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, appl_no,
                                TableConstants.TM_ROLE_PMT_TEMP_APPL, TableConstants.VM_PMT_TEMP_PUR_CD, null, tmgr);
                    } else if (pur_cd == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                        ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, appl_no,
                                TableConstants.TM_ROLE_PMT_SPECIAL_APPL, TableConstants.VM_PMT_SPECIAL_PUR_CD, null, tmgr);
                    }
                }
            } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_APPL))) {
                ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, appl_no,
                        TableConstants.TM_ROLE_PMT_SPECIAL_APPL, TableConstants.VM_PMT_SPECIAL_PUR_CD, null, tmgr);
            } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_TEMP_APPL))) {
                if (renew_temp && priviousTempData != null) {
                    ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, appl_no,
                            TableConstants.TM_ROLE_PMT_TEMP_APPL, TableConstants.VM_PMT_RENEW_TEMP_PUR_CD, null, tmgr);
                } else {
                    ServerUtil.webServiceForNextStage(status, TableConstants.FORWARD, null, appl_no,
                            TableConstants.TM_ROLE_PMT_TEMP_APPL, TableConstants.VM_PMT_TEMP_PUR_CD, null, tmgr);
                }
            }
            ServerUtil.fileFlow(tmgr, status);
            tmgr.commit();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            appl_no = "";
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                appl_no = "";
            }
        }
        return appl_no;
    }

    public void insert_into_va_spl_route_passenger(String appl_no, TemporaryPermitDobj PermitDobj, List<SpecialRoutePermitDobj> splbeanList, TransactionManager tmgr, boolean checkPassengerList) throws SQLException {
        String query;
        PreparedStatement ps = null;
        RowSet rst = null;
        query = "INSERT INTO " + TableList.VA_SPL_ROUTE + " (\n"
                + "            state_cd, off_cd, appl_no, regn_no,sr_no,journey_dt,from_loc,via,to_loc,op_dt,psnger_list)\n"
                + "    VALUES (?,?,?,?,?,?,?,?,?,current_timestamp,?); ";
        ps = tmgr.prepareStatement(query);
        if (!splbeanList.isEmpty()) {
            for (SpecialRoutePermitDobj s : splbeanList) {
                int i = 1;
                ps.setString(i++, PermitDobj.getState_cd());
                ps.setInt(i++, PermitDobj.getOff_cd());
                ps.setString(i++, appl_no);
                ps.setString(i++, PermitDobj.getRegn_no().toUpperCase());
                ps.setInt(i++, s.getSrl_no());
                ps.setDate(i++, new java.sql.Date(s.getValid_from().getTime()));
                ps.setString(i++, s.getRoute_fr().toUpperCase());
                ps.setString(i++, s.getVia().toUpperCase());
                ps.setString(i++, s.getRoute_to().toUpperCase());
                ps.setBoolean(i++, checkPassengerList);
                ps.executeUpdate();
            }
        }
    }

    public Map getTargetAreaMapWithStrBuil(String stringBuilder, String state_cd, int off_cd) {
        Map<String, String> priRouteMap = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("getTargetAreaMapWithStrBuil");
            String query;

            query = "select region_cd,region from " + TableList.VM_REGION + " where (region_cd) in (" + stringBuilder + ") AND state_cd = ? AND off_cd = ? ";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
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

    public Map getTargetRouteMap(String Appl_no, String TableName, String state_cd, int off_cd) {
        Map<String, String> priRouteMap = new LinkedHashMap<String, String>();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getTargetRouteMap");
            String query;
            query = "select code, Floc || ' TO ' || Tloc as descr from " + TableList.VM_ROUTE_MASTER + " Where code in (SELECT route_cd from " + TableName + " where appl_no=?)AND state_cd = ? AND off_cd = ? ORDER BY descr";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
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
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("getSourcesAreaMapWithStrBuil");
            String query;
            query = "select region_cd,region from " + TableList.VM_REGION + " where (region_cd) not in (" + stringBuilder + ") AND state_cd = ? AND off_cd = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
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

    public void insert_into_vatempPermitRoute(String appl_no, TemporaryPermitDobj PermitDobj, List route_code, TransactionManager tmgr) throws SQLException {
        String query;
        PreparedStatement ps = null;
        query = "INSERT INTO " + TableList.VA_TEMP_PERMIT_ROUTE + "(\n"
                + "            state_cd, off_cd, appl_no, route_cd,region_covered,no_of_trips)\n"
                + "    VALUES (?, ?, ?, ?, ?,?); ";
        ps = tmgr.prepareStatement(query);
        if (!route_code.isEmpty()) {
            for (Object s : route_code) {
                ps.setString(1, PermitDobj.getState_cd());
                ps.setInt(2, PermitDobj.getOff_cd());
                ps.setString(3, appl_no);
                ps.setString(4, (String) s);
                ps.setString(5, PermitDobj.getRegion_covered());
                ps.setInt(6, 0);
                ps.executeUpdate();
            }
        } else {
            ps.setString(1, PermitDobj.getState_cd());
            ps.setInt(2, PermitDobj.getOff_cd());
            ps.setString(3, appl_no);
            ps.setString(4, "");
            ps.setString(5, PermitDobj.getRegion_covered());
            ps.setInt(6, 0);
            ps.executeUpdate();
        }
    }

    public void insert_into_vttempPermitRoute(String appl_no, TransactionManager tmgr) throws SQLException {
        String query;
        PreparedStatement ps = null;
        query = "INSERT INTO " + TableList.VT_TEMP_PERMIT_ROUTE + "(\n"
                + "            state_cd, off_cd, appl_no, route_cd,region_covered, no_of_trips)\n"
                + "    SELECT state_cd, off_cd, appl_no, route_cd,region_covered, no_of_trips \n"
                + "  FROM " + TableList.VA_TEMP_PERMIT_ROUTE + " where appl_no = ? ";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, appl_no);
        ps.executeUpdate();

        query = "DELETE  from  " + TableList.VA_TEMP_PERMIT_ROUTE + "  WHERE appl_no=?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, appl_no);
        ps.executeUpdate();

    }

    public Map getSourcesRouteMap(String Appl_no, String TableName, String state_cd, int off_cd) {
        Map<String, String> routeMap = new LinkedHashMap<String, String>();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getRouteMap");
            String query;
            query = "select code, Floc || ' TO ' || Tloc as descr from " + TableList.VM_ROUTE_MASTER + " Where code not in (SELECT route_cd from " + TableName + " where appl_no=?) AND state_cd = ? AND off_cd = ? ORDER BY descr";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
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

    public Map getTargetAreaMap(String state_cd, int off_cd) {
        Map<String, String> priRouteMap = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("getTargetRouteMap");
            String query;

            query = "select region_cd,region from " + TableList.VM_REGION + " where state_cd=? AND off_cd=? ORDER BY region";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
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

//madhurendra 28-2-19
    public void insertIntoVaSplRouteLengthPermit(TransactionManager tmgr, TemporaryPermitDobj temp_dobj, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String Query = "INSERT INTO " + TableList.VA_TEMPSPL_TAX_BASED_ON_PERMIT + "(\n"
                + "            state_cd, off_cd, appl_no, regn_no,pur_cd,\n"
                + "            route_length,service_type,op_dt)\n"
                + "     VALUES (?, ?, ?, ? ,?,?, ?,CURRENT_TIMESTAMP);";
        ps = tmgr.prepareStatement(Query);
        int i = 1;
        ps.setString(i++, Util.getUserStateCode());
        ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
        ps.setString(i++, appl_no);
        ps.setString(i++, temp_dobj.getRegn_no().toUpperCase());
        if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_APPL))) {
            ps.setInt(i++, TableConstants.VM_PMT_SPECIAL_PUR_CD);
        } else {
            ps.setInt(i++, TableConstants.VM_PMT_TEMP_PUR_CD);
        }
        ps.setInt(i++, temp_dobj.getRoute_length());
        ps.setInt(i++, temp_dobj.getService_type());
        ps.executeUpdate();
    }
//    end

    public void insertIntoVaTempPermit(TransactionManager tmgr, PermitDetailDobj pmt_dobj, TemporaryPermitDobj temp_dobj, String appl_no, boolean renew_temp, List<PreviousTempPermitDtlsList> priviousTempData) throws SQLException {
        PreparedStatement ps = null;
        String Query = "INSERT INTO " + TableList.VA_TEMP_PERMIT + "(\n"
                + "            state_cd, off_cd, appl_no, regn_no, issue_dt, valid_from, valid_upto, \n"
                + "             pur_cd, pmt_type, pmt_catg, reason, route_fr, route_to, \n"
                + "            op_dt, period_mode, period, via, goods_to_carry)\n"
                + "    VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?, \n"
                + "            ?, ?, ?, ?, ?, ?, \n"
                + "            CURRENT_TIMESTAMP, ?, ?, ?, ?);";

        ps = tmgr.prepareStatement(Query);
        int i = 1;
        if (action_cd == null) {
            ps.setString(i++, temp_dobj.getState_cd());
            ps.setInt(i++, temp_dobj.getOff_cd());
        } else {
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
        }
        ps.setString(i++, appl_no);
        ps.setString(i++, temp_dobj.getRegn_no().toUpperCase());
        ps.setDate(i++, new java.sql.Date(temp_dobj.getValid_from().getTime()));
        ps.setDate(i++, new java.sql.Date(temp_dobj.getValid_upto().getTime()));
        if (action_cd == null) {
            if (renew_temp && priviousTempData != null) {
                ps.setInt(i++, TableConstants.VM_PMT_RENEW_TEMP_PUR_CD);
            } else {
                pur_cd = temp_dobj.getPur_cd();
                if (pur_cd == TableConstants.VM_PMT_TEMP_PUR_CD) {
                    ps.setInt(i++, TableConstants.VM_PMT_TEMP_PUR_CD);
                } else if (pur_cd == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                    ps.setInt(i++, TableConstants.VM_PMT_SPECIAL_PUR_CD);
                }
            }
        } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_APPL))) {
            ps.setInt(i++, TableConstants.VM_PMT_SPECIAL_PUR_CD);
        } else {
            if (renew_temp && priviousTempData != null) {
                ps.setInt(i++, TableConstants.VM_PMT_RENEW_TEMP_PUR_CD);
            } else {
                ps.setInt(i++, TableConstants.VM_PMT_TEMP_PUR_CD);
            }
        }
        if (pmt_dobj != null) {
            ps.setInt(i++, pmt_dobj.getPmt_type());
            ps.setInt(i++, pmt_dobj.getPmt_catg());
        } else if (pmt_dobj == null && temp_dobj.getPmt_type() != null) {
            ps.setInt(i++, Integer.parseInt(temp_dobj.getPmt_type()));
            ps.setInt(i++, Integer.parseInt(temp_dobj.getPmt_catg()));
        } else {
            ps.setInt(i++, 0);
            ps.setInt(i++, 0);
        }
        ps.setString(i++, temp_dobj.getPurpose().toUpperCase());
        ps.setString(i++, temp_dobj.getRoute_fr().toUpperCase());
        ps.setString(i++, temp_dobj.getRoute_to().toUpperCase());
        ps.setString(i++, temp_dobj.getPeriod_mode());
        ps.setInt(i++, Integer.parseInt(temp_dobj.getPeriod_in_no()));
        ps.setString(i++, temp_dobj.getVia());
        ps.setString(i++, temp_dobj.getGoods_to_carry());
        ps.executeUpdate();
    }

    public TemporaryPermitDobj getVa_Permit_Details(String appl_no) {
        RowSet rs = null;
        TemporaryPermitDobj temp_dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String Query = "";
        try {
            tmgr = new TransactionManager("getVaPermitDetails");
            Query = "SELECT state_cd, off_cd, appl_no, regn_no, issue_dt,valid_from as valid_fr_dt,valid_upto as valid_to_dt,to_char(valid_from,'dd-MON-yyyy') as valid_from , \n"
                    + "       to_char(valid_upto,'dd-MON-yyyy') as valid_upto , rcpt_no, pur_cd, pmt_type, pmt_catg, reason, route_fr, \n"
                    + "       route_to, op_dt,period_mode,period, via, goods_to_carry\n"
                    + "  FROM " + TableList.VA_TEMP_PERMIT + " where appl_no=?;";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                temp_dobj = new TemporaryPermitDobj();
                temp_dobj.setRegn_no(rs.getString("regn_no"));
                temp_dobj.setValid_from(rs.getDate("valid_fr_dt"));
                temp_dobj.setValid_upto(rs.getDate("valid_to_dt"));
                temp_dobj.setPrv_valid_fr(rs.getString("valid_from"));
                temp_dobj.setPrv_valid_to(rs.getString("valid_upto"));
                temp_dobj.setPmt_type(String.valueOf(rs.getInt("pmt_type")));
                temp_dobj.setPmt_catg(String.valueOf(rs.getInt("pmt_catg")));
                if (rs.getString("route_fr") == null) {
                    temp_dobj.setRoute_fr("");
                } else {
                    temp_dobj.setRoute_fr(rs.getString("route_fr"));
                }
                if (rs.getString("route_to") == null) {
                    temp_dobj.setRoute_to("");
                } else {
                    temp_dobj.setRoute_to(rs.getString("route_to"));
                }
                if (rs.getString("reason") == null) {
                    temp_dobj.setPurpose("");
                } else {
                    temp_dobj.setPurpose(rs.getString("reason"));
                }
                temp_dobj.setPeriod_mode(rs.getString("period_mode"));
                temp_dobj.setPeriod_in_no(rs.getString("period"));
                temp_dobj.setVia(rs.getString("via"));
                temp_dobj.setGoods_to_carry(rs.getString("goods_to_carry"));
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
        return temp_dobj;
    }

    public ArrayList getVaSplRouteDetail(String appl_no) {
        RowSet rs = null;
        ArrayList<SpecialRoutePermitDobj> route_detail = null;
        SpecialRoutePermitDobj spl_dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String Query = "";
        try {
            tmgr = new TransactionManager("getVaSplRouteDetail");
            route_detail = new ArrayList<>();
            Query = "SELECT sr_no,journey_dt,from_loc,via,to_loc,psnger_list  \n"
                    + "  FROM " + TableList.VA_SPL_ROUTE + " where appl_no=? order by sr_no";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                spl_dobj = new SpecialRoutePermitDobj();
                spl_dobj.setSrl_no(rs.getInt("sr_no"));
                if (rs.getDate("journey_dt") == null) {
                    spl_dobj.setValid_from(null);
                    spl_dobj.setOld_valid_from(null);
                } else {
                    spl_dobj.setValid_from(rs.getDate("journey_dt"));
                    spl_dobj.setOld_valid_from(JSFUtils.convertToStandardDateFormat(rs.getDate("journey_dt")));
                }
                if (rs.getString("from_loc") == null) {
                    spl_dobj.setRoute_fr("");
                    spl_dobj.setOld_route_fr("");
                } else {
                    spl_dobj.setRoute_fr(rs.getString("from_loc"));
                    spl_dobj.setOld_route_fr(rs.getString("from_loc"));
                }
                if (rs.getString("via") == null) {
                    spl_dobj.setVia("");
                    spl_dobj.setOld_via("");
                } else {
                    spl_dobj.setVia(rs.getString("via"));
                    spl_dobj.setOld_via(rs.getString("via"));
                }
                if (rs.getString("to_loc") == null) {
                    spl_dobj.setRoute_to("");
                    spl_dobj.setOld_route_to("");
                } else {
                    spl_dobj.setRoute_to(rs.getString("to_loc"));
                    spl_dobj.setOld_route_to(rs.getString("to_loc"));
                }
                spl_dobj.setPsnger_list(rs.getBoolean("psnger_list"));
                route_detail.add(spl_dobj);
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
        return route_detail;
    }

    public void saveOnly(boolean status, boolean spl_route_status, String appl_no, SpecialRoutePermitBean splBean, TemporaryPermitDobj temp_dobj, List route, List area_cd, String compairChange) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String region = "";
        List area = area_cd;
        for (Object s : area) {
            region = region + s + ",";
        }
        temp_dobj.setRegion_covered(region);

        try {
            if (!compairChange.isEmpty()) {
                tmgr = new TransactionManager("save Only");
                va_temp_permit_To_vha_temp_Permit(appl_no, tmgr, false);
                updateVa_temp_permit(appl_no, tmgr, temp_dobj);
                if (spl_route_status && !splBean.getSpecialRouteList().isEmpty() && compairChange.contains("Special Route Details")) {
                    va_spl_route_To_vha_spl_route(appl_no, tmgr, false);
                    updateVa_spl_route_passenger(appl_no, tmgr, splBean);
                }
                if (status) {
                    insertVHA_TempRouteandUpdateVa_TempRoute(tmgr, temp_dobj, appl_no, route);
                }
                insertIntoVhaChangedData(tmgr, appl_no, compairChange);
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

    public void saveAndmove(Status_dobj status_dobj, boolean route_status, boolean spl_route_status, SpecialRoutePermitBean splBean, String appl_no, TemporaryPermitDobj temp_dobj, List area_cd, List route_cd, String compairChange) {
        TransactionManager tmgr = null;
        Status_dobj statDobj = status_dobj;
        String region = "";
        List area = area_cd;
        if (area != null && area.size() > 0) {
            for (Object s : area) {
                if (s instanceof String) {
                    region = region + ((String) s) + ",";
                } else if (s instanceof PermitRouteList) {
                    region = region + ((PermitRouteList) s).getKey() + ",";
                }
            }
        }
        temp_dobj.setRegion_covered(region);
        try {
            tmgr = new TransactionManager("save And move");
            if (!compairChange.isEmpty()) {
                va_temp_permit_To_vha_temp_Permit(appl_no, tmgr, false);
                updateVa_temp_permit(appl_no, tmgr, temp_dobj);
                if (spl_route_status && status_dobj.getPur_cd() == TableConstants.VM_PMT_SPECIAL_PUR_CD && compairChange.contains("Special Route Details")) {
                    va_spl_route_To_vha_spl_route(appl_no, tmgr, false);
                    updateVa_spl_route_passenger(appl_no, tmgr, splBean);
                }
                if (route_status) {
                    insertVHA_TempRouteandUpdateVa_TempRoute(tmgr, temp_dobj, appl_no, route_cd);
                }

                insertIntoVhaChangedData(tmgr, appl_no, compairChange);
            }
            statDobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            ServerUtil.fileFlow(tmgr, statDobj);
            tmgr.commit();
        } catch (VahanException ve) {
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

    public void insertVHA_TempRouteandUpdateVa_TempRoute(TransactionManager tmgr, TemporaryPermitDobj dobj, String appl_no, List route_code) throws SQLException {
        String query;
        PreparedStatement ps = null;

        query = "SELECT * FROM " + TableList.VA_TEMP_PERMIT_ROUTE + " where appl_no = ? ";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, appl_no);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            query = "INSERT INTO " + TableList.VHA_TEMP_PERMIT_ROUTE + "(\n"
                    + "            state_cd, off_cd, appl_no, route_cd,region_covered, no_of_trips, moved_on, moved_by)\n"
                    + "    SELECT state_cd, off_cd, appl_no, route_cd,region_covered, no_of_trips,CURRENT_TIMESTAMP,?\n"
                    + "  FROM " + TableList.VA_TEMP_PERMIT_ROUTE + " where appl_no = ? ";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            ps.executeUpdate();

            query = "DELETE  from  " + TableList.VA_TEMP_PERMIT_ROUTE + "  WHERE appl_no=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            ps.executeUpdate();
        }

        query = "INSERT INTO " + TableList.VA_TEMP_PERMIT_ROUTE + "(\n"
                + "            state_cd, off_cd, appl_no, route_cd,region_covered,no_of_trips)\n"
                + "    VALUES (?, ?, ?, ?, ?,?); ";
        ps = tmgr.prepareStatement(query);
        if (!route_code.isEmpty()) {
            for (Object s : route_code) {
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getUserOffCode());
                ps.setString(3, appl_no);
                ps.setString(4, (String) s);
                ps.setString(5, dobj.getRegion_covered());
                ps.setInt(6, 0);
                ps.executeUpdate();
            }
        } else {
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserOffCode());
            ps.setString(3, appl_no);
            ps.setString(4, "");
            ps.setString(5, dobj.getRegion_covered());
            ps.setInt(6, 0);
            ps.executeUpdate();

        }

    }

    public void saveWithApprove(Status_dobj status_dobj, boolean route_status, boolean spl_route_status, boolean renew_temp, String appl_no, TemporaryPermitDobj temp_dobj, List area_cd, List route_cd, String compairChange) {
        TransactionManager tmgr = null;
        Status_dobj statDobj = status_dobj;
        PreparedStatement ps;
        String region = "";
        List area = area_cd;
        if (area != null && area.size() > 0) {
            for (Object s : area) {
                if (s instanceof String) {
                    region = region + ((String) s) + ",";
                } else if (s instanceof PermitRouteList) {
                    region = region + ((PermitRouteList) s).getKey() + ",";
                }
            }
        }
        temp_dobj.setRegion_covered(region);
        try {
            tmgr = new TransactionManager("saveWithApprove");
            if (!compairChange.isEmpty()) {
                updateVa_temp_permit(appl_no, tmgr, temp_dobj);
                if (route_status) {
                    insertVHA_TempRouteandUpdateVa_TempRoute(tmgr, temp_dobj, appl_no, route_cd);
                }
                insertIntoVhaChangedData(tmgr, appl_no, compairChange);
            }
            va_temp_permit_To_vha_temp_Permit(appl_no, tmgr, false);
            va_temp_permit_To_vt_temp_Permit(appl_no, tmgr, renew_temp, temp_dobj, route_status);
            vaTempSpl_RlengthTovtTempspl_Rlength(appl_no, tmgr, temp_dobj);
            if (spl_route_status && temp_dobj.getPur_cd() == TableConstants.VM_PMT_SPECIAL_PUR_CD) {
                va_spl_route_To_vt_spl_route(appl_no, tmgr);
            }
            if (route_status) {
                insert_into_vttempPermitRoute(appl_no, tmgr);
            }

            String query = "SELECT * FROM " + TableList.VA_INSURANCE + " WHERE APPL_NO = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                PassengerPermitDetailImpl passImpl = new PassengerPermitDetailImpl();
                passImpl.insertIntoVTinsAndDeleteVaIns(tmgr, appl_no, temp_dobj.getRegn_no());
            }

            query = "DELETE FROM " + TableList.VA_TEMP_PERMIT + " where appl_no=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            ps.executeUpdate();
            String docId = CommonPermitPrintImpl.getPermitDocIdForQuery(CommonPermitPrintImpl.getPermitDocId(tmgr, 0, temp_dobj.getPur_cd(), Util.getUserStateCode()));
            String[] beanData = {docId, appl_no, temp_dobj.getRegn_no()};
            CommonPermitPrintImpl.insertVaPermitPrint(tmgr, beanData);
            statDobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            ServerUtil.fileFlow(tmgr, statDobj);
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

    public void rebackStatus(Status_dobj status_dobj, boolean route_status, boolean spl_route_status, SpecialRoutePermitBean splBean, List route, List area_cd, String Change, TemporaryPermitDobj dobj, String appl_no) {
        TransactionManager tmgr = null;
        Status_dobj statDobj = status_dobj;
        String region = "";
        List area = area_cd;
        for (Object s : area) {
            region = region + s + ",";
        }
        dobj.setRegion_covered(region);
        try {
            tmgr = new TransactionManager("rebackStatus");
            if (!Change.isEmpty()) {
                va_temp_permit_To_vha_temp_Permit(appl_no, tmgr, false);
                updateVa_temp_permit(appl_no, tmgr, dobj);
                if (spl_route_status && status_dobj.getPur_cd() == TableConstants.VM_PMT_SPECIAL_PUR_CD && Change.contains("Special Route Details")) {
                    va_spl_route_To_vha_spl_route(appl_no, tmgr, false);
                    updateVa_spl_route_passenger(appl_no, tmgr, splBean);
                }
                if (route_status) {
                    insertVHA_TempRouteandUpdateVa_TempRoute(tmgr, dobj, appl_no, route);
                }
                insertIntoVhaChangedData(tmgr, appl_no, Change);
            }
            statDobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            ServerUtil.fileFlow(tmgr, statDobj);
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

    public void va_temp_permit_To_vha_temp_Permit(String appl_no, TransactionManager tmgr, boolean disposeApplFalg) throws SQLException {
        String Query = "";
        PreparedStatement ps = null;
        Query = " INSERT INTO " + TableList.VHA_TEMP_PERMIT + "(\n"
                + "            state_cd, off_cd, appl_no, regn_no, issue_dt, valid_from, valid_upto, \n"
                + "            rcpt_no, pur_cd, pmt_type, pmt_catg, reason, route_fr, route_to, \n"
                + "            op_dt, period_mode, period, moved_on, moved_by, via, goods_to_carry)"
                + " SELECT state_cd, off_cd, appl_no, regn_no, issue_dt, valid_from, valid_upto, \n"
                + "       rcpt_no, pur_cd, pmt_type, pmt_catg, reason, route_fr, route_to, \n"
                + "       op_dt, period_mode, period,CURRENT_TIMESTAMP,?, via, goods_to_carry"
                + "  FROM " + TableList.VA_TEMP_PERMIT + " Where appl_no = ?;";
        ps = tmgr.prepareStatement(Query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();

        if (disposeApplFalg) {
            String query = "DELETE FROM " + TableList.VA_TEMP_PERMIT + " where appl_no=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            ps.executeUpdate();
        }
    }

    public void va_spl_route_To_vha_spl_route(String appl_no, TransactionManager tmgr, boolean disposeApplFalg) throws SQLException {
        String Query = "";
        PreparedStatement ps = null;
        Query = " INSERT INTO " + TableList.VHA_SPL_ROUTE + " (\n"
                + "            state_cd, off_cd, appl_no, regn_no,sr_no,journey_dt,from_loc,via,to_loc,op_dt, moved_on, moved_by,psngr_list)"
                + " SELECT state_cd, off_cd, appl_no, regn_no, sr_no, journey_dt, from_loc, \n"
                + "       via,to_loc,op_dt, CURRENT_TIMESTAMP, ?,psnger_list"
                + "  FROM " + TableList.VA_SPL_ROUTE + " Where appl_no = ?;";
        ps = tmgr.prepareStatement(Query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();

        if (disposeApplFalg) {
            String query = "DELETE FROM " + TableList.VA_SPL_ROUTE + " where appl_no=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            ps.executeUpdate();
        }
    }

    public void updateVa_temp_permit(String appl_no, TransactionManager tmgr, TemporaryPermitDobj temp_dobj) throws SQLException {
        String Query = "";
        PreparedStatement ps = null;
        Query = " UPDATE " + TableList.VA_TEMP_PERMIT + " \n"
                + "   SET  valid_from=?,valid_upto=?,reason=?, route_fr=?, route_to=?, period_mode=?, period=?,\n"
                + "   via = ?, goods_to_carry = ?,pmt_catg =? \n"
                + " WHERE appl_no=?;";
        ps = tmgr.prepareStatement(Query);
        int i = 1;
        ps.setDate(i++, new java.sql.Date(temp_dobj.getValid_from().getTime()));
        ps.setDate(i++, new java.sql.Date(temp_dobj.getValid_upto().getTime()));
        ps.setString(i++, temp_dobj.getPurpose().toUpperCase());
        ps.setString(i++, temp_dobj.getRoute_fr().toUpperCase());
        ps.setString(i++, temp_dobj.getRoute_to().toUpperCase());
        ps.setString(i++, temp_dobj.getPeriod_mode().toUpperCase());
        ps.setInt(i++, Integer.valueOf(temp_dobj.getPeriod_in_no()));
        ps.setString(i++, temp_dobj.getVia().toUpperCase());
        ps.setString(i++, temp_dobj.getGoods_to_carry().toUpperCase());
        ps.setInt(i++, Integer.valueOf(temp_dobj.getPmt_catg()));
        ps.setString(i++, appl_no);
        ps.executeUpdate();
    }

    public void updateVa_spl_route_passenger(String appl_no, TransactionManager tmgr, SpecialRoutePermitBean spl_route_bean) throws SQLException {
        String Query = "";
        PreparedStatement ps = null;
        Query = " UPDATE " + TableList.VA_SPL_ROUTE + " \n"
                + "   SET  sr_no=?,journey_dt=?,from_loc=?, via=?, to_loc=? \n"
                + " WHERE appl_no=? and sr_no=?;";
        ps = tmgr.prepareStatement(Query);
        if (!spl_route_bean.getSpecialRouteList().isEmpty()) {
            for (SpecialRoutePermitDobj s : spl_route_bean.getSpecialRouteList()) {
                int i = 1;
                ps.setInt(i++, s.getSrl_no());
                if (s.getValid_from() == null) {
                    ps.setDate(i++, null);
                } else {
                    ps.setDate(i++, new java.sql.Date(s.getValid_from().getTime()));
                }
                if (CommonUtils.isNullOrBlank(s.getRoute_fr())) {
                    ps.setString(i++, null);
                } else {
                    ps.setString(i++, s.getRoute_fr().toUpperCase());
                }
                if (CommonUtils.isNullOrBlank(s.getVia())) {
                    ps.setString(i++, null);
                } else {
                    ps.setString(i++, s.getVia().toUpperCase());
                }
                if (CommonUtils.isNullOrBlank(s.getRoute_to())) {
                    ps.setString(i++, null);
                } else {
                    ps.setString(i++, s.getRoute_to().toUpperCase());
                }
                ps.setString(i++, appl_no);
                ps.setInt(i++, s.getSrl_no());
                ps.executeUpdate();
            }
        }
    }

    public String updateRecpt_no(Status_dobj status_dobj, String amount, String appl_no, String rec, TransactionManager tmgr, TemporaryPermitDobj dobj, String paymant_mode) throws SQLException {
        String status = "failer";
        PreparedStatement ps = null;
        try {
            String Query = "";

            String sql = "insert into " + TableList.VT_FEE + " (regn_no,payment_mode,fees,fine,rcpt_no, rcpt_dt,pur_cd,collected_by,state_cd,off_cd) values"
                    + "(?,?,?,?,?,CURRENT_TIMESTAMP,?,?,?,?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getRegn_no());
            ps.setString(2, paymant_mode);
            ps.setInt(3, Integer.parseInt(amount));
            ps.setInt(4, 0);
            ps.setString(5, rec);
            if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_APPL))) {
                ps.setInt(6, TableConstants.VM_PMT_SPECIAL_PUR_CD);
            } else {
                ps.setInt(6, TableConstants.VM_PMT_TEMP_PUR_CD);
            }
            ps.setString(7, Util.getUserOffCode().toString());
            ps.setString(8, Util.getUserStateCode());
            ps.setInt(9, Util.getUserOffCode());
            ps.executeUpdate();

            Query = " UPDATE " + TableList.VA_TEMP_PERMIT + "\n"
                    + "   SET rcpt_no=?"
                    + " WHERE appl_no=?;";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, rec);
            ps.setString(2, appl_no);
            ps.executeUpdate();
            status = "success";
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
        return status;
    }
//  madhurendra on 28-2-19

    public void vaTempSpl_RlengthTovtTempspl_Rlength(String appl_no, TransactionManager tmgr, TemporaryPermitDobj temp_dobj) throws VahanException {
        String Query = "";
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        pur_cd = temp_dobj.getPur_cd();
        try {
            Query = "SELECT * FROM   " + TableList.VA_TEMPSPL_TAX_BASED_ON_PERMIT + " where appl_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                Query = "INSERT INTO " + TableList.VHA_TEMPSPL_TAX_BASED_ON_PERMIT + "(\n"
                        + "            moved_by,moved_on,state_cd, off_cd, appl_no,regn_no, pur_cd, route_length,service_type,op_dt) \n"
                        + "            SELECT ?,Current_timestamp,state_cd, off_cd, appl_no,regn_no, pur_cd,\n"
                        + "            route_length,service_type,op_dt \n"
                        + "            FROM " + TableList.VA_TEMPSPL_TAX_BASED_ON_PERMIT + " where appl_no = ?;";
                ps = tmgr.prepareStatement(Query);
                int j = 1;
                ps.setString(j++, Util.getEmpCode());
                ps.setString(j++, appl_no);
                ps.executeUpdate();
                Query = "INSERT INTO " + TableList.VT_TEMPSPL_TAX_BASED_ON_PERMIT + "(\n"
                        + "            state_cd, off_cd, appl_no,regn_no, pur_cd, route_length,service_type,op_dt) \n"
                        + "            SELECT state_cd, off_cd, appl_no,regn_no, pur_cd,\n"
                        + "            route_length,service_type,op_dt\n"
                        + "            FROM   " + TableList.VA_TEMPSPL_TAX_BASED_ON_PERMIT + " where appl_no = ?;";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, appl_no);
                ps.executeUpdate();

                Query = "DELETE FROM " + TableList.VA_TEMPSPL_TAX_BASED_ON_PERMIT + " where appl_no=?";
                ps1 = tmgr.prepareStatement(Query);
                ps1.setString(1, appl_no);
                ps1.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error(">>>Error in save vaTempSpl_RlengthTovtTempspl_Rlength." + e.getMessage());
            throw new VahanException(e.getMessage());
        }
    }
//    end

    public void va_temp_permit_To_vt_temp_Permit(String appl_no, TransactionManager tmgr, boolean renew_temp, TemporaryPermitDobj temp_dobj, boolean route_temp) throws VahanException {
        String Query = "";
        String pmt_no = "";
        String prv_appl_no = "";
        PreparedStatement ps = null;
        String message = null;
        pur_cd = temp_dobj.getPur_cd();
        if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_APPROVAL))) {
            pmt_no = ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getUserOffCode(), 0, 0, "S");
        } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_TEMP_APPROVAL))) {
            pmt_no = ServerUtil.getUniquePermitNo(tmgr, Util.getUserStateCode(), Util.getUserOffCode(), 0, 0, "T");
        } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_RENEW_TEMP_APPROVAL))) {
            if (renew_temp) {
                String[] temp = getTempPmtNo(temp_dobj.getRegn_no().toUpperCase());
                if (CommonUtils.isNullOrBlank(temp[0])) {
                    throw new VahanException("Permit no is not found. Please contact to system administrator..");
                } else {
                    pmt_no = temp[0];
                }

                if (CommonUtils.isNullOrBlank(temp[1])) {
                    throw new VahanException("Application no is not found. Please contact to system administrator..");
                } else {
                    prv_appl_no = temp[1];
                }
            }

        }
        if (CommonUtils.isNullOrBlank(pmt_no) || ("").equalsIgnoreCase(pmt_no)) {
            LOGGER.error("va_temp_permit_To_vt_temp_Permit--->Permit Number not genrated.");
            throw new VahanException("Permit Number not genrated.");
        } else {
            try {
                if (renew_temp) {
                    if (!pmt_no.equals("") && pur_cd == TableConstants.VM_PMT_RENEW_TEMP_PUR_CD) {
                        Query = "INSERT INTO " + TableList.VH_TEMP_PERMIT + "(\n"
                                + "            state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                                + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, reason, route_fr, \n"
                                + "            route_to, op_dt, pmt_status, pmt_reason, moved_on, moved_by,via,goods_to_carry)\n"
                                + "     SELECT state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                                + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, reason, route_fr, \n"
                                + "            route_to, op_dt,?,?,CURRENT_TIMESTAMP,?,via,goods_to_carry \n"
                                + "  FROM " + TableList.VT_TEMP_PERMIT + " where pmt_no=?";
                        ps = tmgr.prepareStatement(Query);
                        int j = 1;
                        ps.setString(j++, "REN");
                        ps.setString(j++, "RENEW OF PERMIT");
                        ps.setString(j++, Util.getEmpCode());
                        ps.setString(j++, pmt_no);
                        ps.executeUpdate();
                        Query = "DELETE FROM " + TableList.VT_TEMP_PERMIT + " where pmt_no=?";
                        ps = tmgr.prepareStatement(Query);
                        ps.setString(1, pmt_no);
                        ps.executeUpdate();
                    }
                    if (route_temp) {
                        Query = "DELETE  from  " + TableList.VT_TEMP_PERMIT_ROUTE + "  WHERE appl_no=? and state_cd=?";
                        ps = tmgr.prepareStatement(Query);
                        ps.setString(1, prv_appl_no);
                        ps.setString(2, Util.getUserStateCode());
                        ps.executeUpdate();
                    }
                }
                Query = "INSERT INTO " + TableList.VT_TEMP_PERMIT + "(\n"
                        + "            state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                        + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, reason, route_fr, \n"
                        + "            route_to, op_dt, via, goods_to_carry)\n"
                        + "     SELECT state_cd, off_cd, appl_no,?, regn_no, issue_dt, valid_from, \n"
                        + "            valid_upto,rcpt_no, pur_cd, pmt_type, pmt_catg, reason, route_fr, \n"
                        + "            route_to,Current_timestamp, via, goods_to_carry\n"
                        + "     FROM   " + TableList.VA_TEMP_PERMIT + " where appl_no = ?;";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, pmt_no);
                ps.setString(2, appl_no);
                ps.executeUpdate();

                if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_SPECIAL_APPROVAL))) {
                    message = "Special Permit No " + pmt_no + " generated against Application No " + appl_no;
                } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_TEMP_APPROVAL))) {
                    message = "Temporary Permit No " + pmt_no + " generated against Application No " + appl_no;
                } else if (action_cd.equalsIgnoreCase(String.valueOf(TableConstants.TM_ROLE_PMT_RENEW_TEMP_APPROVAL))) {
                    message = "Renew Temporary Permit No " + pmt_no + " generated against Application No " + appl_no;
                }
                ServerUtil.insertUsersTransactionMessage(message, 1, tmgr);
            } catch (SQLException e) {
                LOGGER.error("va_temp_permit_To_vt_temp_Permit-->>>Error in save VA_TEMP_PERMIT to VT_TEMP_PERMIT." + e.getMessage());
                throw new VahanException(e.getMessage());
            }
        }
    }

    public void va_spl_route_To_vt_spl_route(String appl_no, TransactionManager tmgr) throws VahanException {
        String Query = "";
        PreparedStatement ps = null;
        try {
            Query = "INSERT INTO " + TableList.VT_SPL_ROUTE + " (\n"
                    + "            state_cd, off_cd, appl_no, regn_no,sr_no,journey_dt,from_loc,via,to_loc,op_dt,psngr_list)\n"
                    + "     SELECT state_cd, off_cd, appl_no, regn_no, sr_no, journey_dt, \n"
                    + "            from_loc,via, to_loc,current_timestamp,psnger_list \n"
                    + "     FROM  " + TableList.VA_SPL_ROUTE + " where appl_no = ? order by sr_no";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            ps.executeUpdate();
            Query = "DELETE  from  " + TableList.VA_SPL_ROUTE + "  WHERE appl_no=?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("va_spl_route_To_vt_spl_route-->>>Error in save VA_SPL_ROUTE to VT_SPL_ROUTE." + e.getMessage());
            throw new VahanException(e.getMessage());
        }

    }

    public int[] getValidDaysWeeks(String state_cd) throws VahanException {
        int[] validity = null;
        String Query;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("getValidDaysWeeks");
            Query = "SELECT temp_days_valid_upto, temp_weeks_valid_upto, spl_days_valid_upto, spl_weeks_valid_upto, "
                    + " temp_days_valid_from, temp_weeks_valid_from, spl_days_valid_from, spl_weeks_valid_from, temp_months_valid_upto "
                    + " FROM " + TableList.TM_TEMP_PMT_STATE_CONFIGURATION + " a inner join " + TableList.TM_SPL_PMT_STATE_CONFIGURATION + " b on a.state_cd=b.state_cd WHERE a.state_cd = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, state_cd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                validity = new int[9];
                validity[0] = rs.getInt("temp_days_valid_upto");
                validity[1] = rs.getInt("temp_weeks_valid_upto");
                validity[2] = rs.getInt("spl_days_valid_upto");
                validity[3] = rs.getInt("spl_weeks_valid_upto");
                validity[4] = rs.getInt("temp_days_valid_from");
                validity[5] = rs.getInt("temp_weeks_valid_from");
                validity[6] = rs.getInt("spl_days_valid_from");
                validity[7] = rs.getInt("spl_weeks_valid_from");
                validity[8] = rs.getInt("temp_months_valid_upto");
            }
        } catch (SQLException e) {
            LOGGER.error("getValidDaysWeeks-->>> Define DAYS/WEEKS in DATA BASE." + e.getMessage());
            throw new VahanException("getValidDaysWeeks-->>> Define DAYS/WEEKS in DATA BASE" + e.getMessage());
        }
        return validity;
    }

    public List<PreviousTempPermitDtlsList> getPreviouseTempDtls(List<PreviousTempPermitDtlsList> preTempDataTable, String regnNo, int purCd) {
        String Query;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManagerReadOnly("getPriviouseTempDtls");
            if (purCd == TableConstants.VM_PMT_TEMP_PUR_CD) {
                Query = "select string_agg(distinct to_char(op_dt,'yyyy'),',') as year from " + TableList.VT_TEMP_PERMIT + " where regn_no=? AND pur_cd IN (?,?)";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, regnNo);
                ps.setInt(2, purCd);
                ps.setInt(3, TableConstants.VM_PMT_RENEW_TEMP_PUR_CD);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    String[] year = rs.getString("year").split(",");
                    if (year != null && !year[0].toString().equals("")) {
                        preTempDataTable = new ArrayList<>();
                        for (int i = 0; i < year.length; i++) {
                            PreviousTempPermitDtlsList dtlsList = new PreviousTempPermitDtlsList(year[i]);
                            Query = "select appl_no,pmt_type,pmt_catg,pmt_no,route_fr,route_to,to_char(valid_from,'dd-MON-yyyy') as valid_from,to_char(valid_upto,'dd-MON-yyyy') as valid_upto,(DATE_PART('day',valid_upto-valid_from) :: int)+1 as days,\n"
                                    + "to_char(issue_dt,'dd-MON-yyyy') as issue_dt\n"
                                    + "from " + TableList.VT_TEMP_PERMIT + " where regn_no=? AND pur_cd IN (?,?) AND to_char(issue_dt,'yyyy') = ? order by op_dt DESC";
                            ps = tmgr.prepareStatement(Query);
                            ps.setString(1, regnNo);
                            ps.setInt(2, purCd);
                            ps.setInt(3, TableConstants.VM_PMT_RENEW_TEMP_PUR_CD);
                            ps.setString(4, year[i]);
                            RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                            while (rs1.next()) {
                                dtlsList.getDtlsList().add(new PreviousTempPermitDtlsDobj(rs1.getString("route_fr"), rs1.getString("route_to"), rs1.getString("valid_from"), rs1.getString("valid_upto"), rs1.getInt("days"), rs1.getString("issue_dt"), rs1.getString("pmt_type"), rs1.getString("pmt_catg"), rs1.getString("pmt_no"), rs1.getString("appl_no")));
                            }
                            preTempDataTable.add(dtlsList);
                        }
                    }
                }
            } else {
                Query = "select string_agg(distinct to_char(op_dt,'yyyy'),',') as year from " + TableList.VT_TEMP_PERMIT + " where regn_no=? AND pur_cd = ? ";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, regnNo);
                ps.setInt(2, purCd);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    String[] year = rs.getString("year").split(",");
                    if (year != null && !year[0].toString().equals("")) {
                        preTempDataTable = new ArrayList<>();
                        for (int i = 0; i < year.length; i++) {
                            PreviousTempPermitDtlsList dtlsList = new PreviousTempPermitDtlsList(year[i]);
                            Query = "select appl_no,pmt_type,pmt_catg,pmt_no,route_fr,route_to,to_char(valid_from,'dd-MON-yyyy') as valid_from,to_char(valid_upto,'dd-MON-yyyy') as valid_upto,(DATE_PART('day',valid_upto-valid_from) :: int)+1 as days,\n"
                                    + "to_char(issue_dt,'dd-MON-yyyy') as issue_dt\n"
                                    + "from " + TableList.VT_TEMP_PERMIT + " where regn_no=? AND pur_cd = ? AND to_char(issue_dt,'yyyy') = ? order by op_dt DESC";
                            ps = tmgr.prepareStatement(Query);
                            ps.setString(1, regnNo);
                            ps.setInt(2, purCd);
                            ps.setString(3, year[i]);
                            RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                            while (rs1.next()) {
                                dtlsList.getDtlsList().add(new PreviousTempPermitDtlsDobj(rs1.getString("route_fr"), rs1.getString("route_to"), rs1.getString("valid_from"), rs1.getString("valid_upto"), rs1.getInt("days"), rs1.getString("issue_dt"), rs1.getString("pmt_type"), rs1.getString("pmt_catg"), rs1.getString("pmt_no"), rs1.getString("appl_no")));
                            }
                            preTempDataTable.add(dtlsList);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error(regnNo + " - " + e);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return preTempDataTable;
    }

    public String[] getTempPmtNo(String regnNo) throws VahanException {
        String Query;
        String[] values = new String[2];
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("getTempPmtNo");
            Query = "select appl_no,pmt_no from " + TableList.VT_TEMP_PERMIT + " where regn_no=? and state_cd=? and pur_cd in (?,?) order by op_dt DESC limit 1";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regnNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, TableConstants.VM_PMT_TEMP_PUR_CD);
            ps.setInt(4, TableConstants.VM_PMT_RENEW_TEMP_PUR_CD);
            RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
            if (rs1.next()) {
                values[0] = rs1.getString("pmt_no"); //Permit No.
                values[1] = rs1.getString("appl_no"); //Application No.
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace());
            throw new VahanException("Problem to find the details in Temporary Permit");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return values;
    }

    public int getCountTempPmtNo(String regnNo, String pmt_no) {
        String Query;
        int count = 0;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("getCountTempPmtNo");
            Query = "SELECT count(*) from (SELECT regn_no,pmt_no,appl_no from " + TableList.VT_TEMP_PERMIT + " where regn_no = ? and pmt_no = ?\n"
                    + "UNION\n"
                    + "SELECT regn_no,pmt_no,appl_no from " + TableList.VH_TEMP_PERMIT + " where regn_no = ? and pmt_no = ? and pmt_status=?) as a";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, regnNo);
            ps.setString(2, pmt_no);
            ps.setString(3, regnNo);
            ps.setString(4, pmt_no);
            ps.setString(5, "REN");
            RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
            if (rs1.next()) {
                count = rs1.getInt("count");
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
        return count;
    }

    public TemporaryPermitDobj getTempPermitDetials(String regnNo) {
        TemporaryPermitDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        RowSet rs;
        try {
            tmgr = new TransactionManager("getTempPermitdetailsFromRegnNo");
            String sql = " SELECT regn_no,pmt_no,valid_from,valid_upto,b.descr as purcd,a.pur_cd,c.descr as permitType,d.descr as permitCatg,goods_to_carry,route_fr,route_to,via,reason,pmt_type,pmt_catg  FROM " + TableList.VT_TEMP_PERMIT + " a"
                    + " left join " + TableList.TM_PURPOSE_MAST + " b on b.pur_cd=a.pur_cd "
                    + " left join " + TableList.VM_PERMIT_TYPE + " c on c.code = a.pmt_type  "
                    + " left join " + TableList.VM_PERMIT_CATG + " d on d.code = a.pmt_catg and a.state_cd = d.state_cd"
                    + " where regn_no=? and a.pur_cd in (?,?) order by op_dt DESC limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setInt(2, TableConstants.VM_PMT_TEMP_PUR_CD);
            ps.setInt(3, TableConstants.VM_PMT_RENEW_TEMP_PUR_CD);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new TemporaryPermitDobj();
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setPmt_no(rs.getString("pmt_no"));
                dobj.setValid_from(rs.getDate("valid_from"));
                dobj.setValid_upto(rs.getDate("valid_upto"));
                dobj.setPur_cd(rs.getInt("pur_cd"));
                dobj.setGoods_to_carry(rs.getString("goods_to_carry"));
                dobj.setRoute_fr(rs.getString("route_fr"));
                dobj.setRoute_to(rs.getString("route_to"));
                dobj.setVia(rs.getString("via"));
                dobj.setPurpose(rs.getString("reason"));
                dobj.setPmt_type(rs.getString("permitType"));
                dobj.setPmt_catg(rs.getString("permitCatg"));
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

    public boolean isExistInVtInstrumentAITP(String regn_no) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;
        boolean found = false;
        try {
            tmgr = new TransactionManager("isExistInVtInstrumentAITP");
            sql = " Select * FROM " + TableList.VT_INSTRUMENT_AITP
                    + " WHERE regn_no = ? and state_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                found = true;
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
        return found;
    }
}
