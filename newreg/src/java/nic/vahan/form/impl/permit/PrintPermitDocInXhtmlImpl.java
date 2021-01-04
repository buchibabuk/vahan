/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.java.util.DateUtilsException;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.permit.AITPStateFeeDraftDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitLeaseDobj;
import nic.vahan.form.dobj.permit.PermitTimeTableDobj;
import nic.vahan.form.dobj.permit.SpecialRoutePermitDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 *
 */
public class PrintPermitDocInXhtmlImpl {

    private static final Logger LOGGER = Logger.getLogger(PrintPermitDocInXhtmlImpl.class);

    public Map getNationalAndAllIndiaDetail(String applNo) {
        Map<String, String> goodsMap = new LinkedHashMap<String, String>();
        RowSet rs;
        PreparedStatement ps;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("National Goods Detail print");
            String query;
            query = "SELECT  to_char(pmt.op_dt,'DD-Mon-YYYY') as op_date,COALESCE(pmt.region_covered,'') as region_covered,service.descr as service_type_descr,pmt.state_cd,pmt.off_cd,d.auth_no,to_char(d.auth_fr,'DD-Mon-YYYY') as auth_fr,hypth.fncr_name,to_char(d.auth_to,'DD-Mon-YYYY') as auth_to,to_char(current_date,'DD-Mon-YYYY')as currentDate,to_char(pmt.replace_date,'DD-Mon-YYYY') as replace_date,pmt.PMT_NO,to_char(pmt.VALID_FROM,'DD-Mon-YYYY') as valid_from ,to_char(pmt.VALID_UPTO,'DD-Mon-YYYY') as valid_upto,VVO.OFF_NAME AS\n"
                    + " OFF_NAME,VVO.STATE_NAME, VVO.OWNER_NAME, VVO.f_name,VVO.c_add1,VVO.c_add2,VVO.c_add3,VVO.c_district_name,VVO.c_state_name,VVO.c_pincode,VVO.chasi_no,VVO.eng_no,\n"
                    + " VVO.REGN_NO,to_char(VVO.REGN_DT,'DD-Mon-YYYY') as regn_dt,VVO.manu_yr,COALESCE(VVO.ld_wt,'0') as ld_wt,COALESCE(VVO.gcw,'0') as gcw,COALESCE(VVO.unld_wt,'0') as unld_wt,VVO.VH_CLASS_DESC,COALESCE(pmt.goods_to_carry, '') as goods_to_carry,VVO.CHASI_NO,VVO.MODEL_NAME,VVO.MAKER_NAME,VVO.SEAT_CAP,c.descr as pmt_type,g.descr as pmt_catg,pmt.pmt_type as pmt_type_code ,\n"
                    + " VVO.stand_cap,VVO.body_type,VVO.sleeper_cap,COALESCE(to_char(pmt.replace_date,'dd-Mon-yyyy'),'') as replace_date\n"
                    + " FROM " + TableList.VT_PERMIT + " pmt \n"
                    + " LEFT OUTER JOIN " + TableList.VIEW_VV_OWNER + " VVO ON VVO.REGN_NO  = pmt.REGN_NO and VVO.state_cd = pmt.state_cd\n"
                    + " LEFT OUTER JOIN " + TableList.VM_PERMIT_TYPE + " c on c.code = pmt.pmt_type\n"
                    + " LEFT OUTER JOIN " + TableList.VM_PERMIT_CATG + " g on g.code = pmt.pmt_catg AND g.permit_type = pmt.pmt_type AND g.state_cd = pmt.state_cd \n"
                    + " inner join " + TableList.VT_PERMIT_HOME_AUTH + " d on d.regn_no = pmt.regn_no \n"
                    + " left join " + TableList.VT_HYPTH + " hypth ON hypth.regn_no=pmt.REGN_NO and hypth.state_cd=pmt.state_cd \n"
                    + " left outer join " + TableList.VM_SERVICES_TYPE + " service on service.code=pmt.service_type \n"
                    + " WHERE pmt.appl_no =?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, applNo);
            rs = tmgr.fetchDetachedRowSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    goodsMap.put(rsmd.getColumnName(i), rs.getString(i));
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
        return goodsMap;
    }

    public Map getGoodsCarriageDetail(String Appl_no) {
        Map<String, String> goodsCagMap = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        RowSet rs;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("Goods Carriage Detail Print");
            String query;
            query = "SELECT  to_char(pmt.op_dt,'DD-Mon-YYYY') as op_date,to_char(current_date,'DD-Mon-YYYY') as currentDate,hypth.fncr_name,service.descr as service_type_descr,pmt.PMT_NO,to_char(pmt.replace_date,'DD-Mon-YYYY') as replace_date, to_char(pmt.VALID_FROM,'DD-Mon-YYYY') as valid_from,pmt.goods_to_carry,to_char(pmt.VALID_UPTO,'DD-Mon-YYYY') as valid_upto, VVO.OWNER_NAME,VVO.F_NAME,\n"
                    + "(VVO.OFF_NAME) AS OFF_NAME,VVO.STATE_NAME,VVO.f_name,VVO.c_add1,VVO.c_add2,VVO.c_add3,VVO.c_district_name,VVO.c_pincode,VVO.c_state_name,VVO.state_cd,VVO.off_cd,pmt.off_cd as pmt_off_cd,\n"
                    + " VVO.REGN_NO,VVO.CHASI_NO, VVO.ENG_NO,VVO.MAKER_NAME,to_char(VVO.REGN_DT,'DD-Mon-YYYY') as regn_dt,VVO.norms_descr,VVO.MODEL_NAME,VVO.manu_yr,VVO.VH_CLASS,VVO.VH_CLASS_DESC,VVO.manu_yr,VVO.SEAT_CAP,COALESCE(VVO.sleeper_cap,0) as sleeper_cap,COALESCE(VVO.STAND_CAP,0) as stand_cap,VVO.ld_wt,VVO.unld_wt,CRI.CRITERIA_DESC AS other_criteria,c.descr as pmt_type,g.descr as pmt_catg,pmt.pmt_type as pmt_type_code,pmt.pmt_catg as pmt_catg_code,\n"
                    + " pmt.parking,pmt.jorney_purpose,VVO.fuel_descr,pmt.service_type FROM " + TableList.VT_PERMIT + " pmt\n"
                    + " LEFT OUTER JOIN " + TableList.VIEW_VV_OWNER + " VVO ON VVO.REGN_NO  = pmt.REGN_NO and VVO.state_cd = pmt.state_cd\n"
                    + " LEFT OUTER JOIN " + TableList.VM_PERMIT_TYPE + " c on c.code = pmt.pmt_type\n"
                    + " LEFT OUTER JOIN " + TableList.VM_PERMIT_CATG + " g on g.code = pmt.pmt_catg AND g.permit_type = pmt.pmt_type AND g.state_cd = pmt.state_cd \n"
                    + " LEFT JOIN vm_other_criteria CRI ON CRI.CRITERIA_CD  = VVO.other_criteria  AND CRI.STATE_CD = ? \n"
                    + " left join " + TableList.VT_HYPTH + " hypth ON hypth.regn_no=pmt.REGN_NO and hypth.state_cd=pmt.state_cd \n"
                    + " left outer join " + TableList.VM_SERVICES_TYPE + " service on service.code=pmt.service_type \n"
                    + " WHERE pmt.appl_no =?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, Appl_no);
            rs = tmgr.fetchDetachedRowSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    goodsCagMap.put(rsmd.getColumnName(i), rs.getString(i));
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
        return goodsCagMap;
    }

    public List<SpecialRoutePermitDobj> getRouteDataForSpecial(String appl_no) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        RowSet rst;
        List<SpecialRoutePermitDobj> routeDetailslist = null;
        SpecialRoutePermitDobj dobj = null;
        try {
            tmgr = new TransactionManager("getRouteDataForSpecial");
            String sql = "select sr_no, to_char(journey_dt,'DD-Mon-YYYY') as jny_dt,from_loc,via,to_loc  from " + TableList.VT_SPL_ROUTE + " Where appl_no=? order by sr_no";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            rst = tmgr.fetchDetachedRowSet();
            routeDetailslist = new ArrayList<>();
            while (rst.next()) {
                dobj = new SpecialRoutePermitDobj();
                dobj.setSrl_no(rst.getInt("sr_no"));
                dobj.setValid_fr(rst.getString("jny_dt"));
                dobj.setRoute_fr(rst.getString("from_loc"));
                dobj.setVia(rst.getString("via"));
                dobj.setRoute_to(rst.getString("to_loc"));
                routeDetailslist.add(dobj);
            }
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
                }
            } catch (Exception e) {
                routeDetailslist = null;
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return routeDetailslist;
    }

    public List<PassengerPermitDetailDobj> getRouteDataForTemp(String appl_no, String state_cd, int off_cd) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        RowSet rs;
        List<PassengerPermitDetailDobj> routeDetailslist = null;
        PassengerPermitDetailDobj dobj = null;
        try {
            tmgr = new TransactionManager("getRouteDataForTemp");
            String sql = "select code, floc ,tloc,via  from " + TableList.VM_ROUTE_MASTER + " Where code in (SELECT route_cd from " + TableList.VT_TEMP_PERMIT_ROUTE + " where appl_no=?)AND state_cd = ? AND off_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            rs = tmgr.fetchDetachedRowSet();
            routeDetailslist = new ArrayList<PassengerPermitDetailDobj>();
            while (rs.next()) {
                dobj = new PassengerPermitDetailDobj();
                dobj.setFloc(rs.getString("floc"));
                dobj.setTloc(rs.getString("tloc"));
                dobj.setRout_code(rs.getString("code"));
                dobj.setStart_POINT(rs.getString("via"));
                routeDetailslist.add(dobj);
            }
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
                }
            } catch (Exception e) {
                routeDetailslist = null;
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return routeDetailslist;
    }

    public String getRegionVtTemp(String appl_no) {
        String region = "";
        TransactionManager tmgr = null;
        RowSet rs;
        PreparedStatement ps;

        try {
            tmgr = new TransactionManager("getRegionVtTemp");
            String query;
            query = "select * from " + TableList.VT_TEMP_PERMIT_ROUTE + "  \n"
                    + " WHERE state_cd=? and appl_no=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, appl_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next() && !CommonUtils.isNullOrBlank(rs.getString("region_covered"))) {
                if (JSFUtils.isNumeric(String.valueOf(rs.getString("region_covered").charAt(0)))) {
                    String[] temp = rs.getString("region_covered").split(",");
                    for (int i = 0; i < temp.length; i++) {
                        query = "select region as regionAll from " + TableList.VM_REGION + " where state_cd=? and off_cd=? and region_cd =?";
                        ps = tmgr.prepareStatement(query);
                        ps.setString(1, rs.getString("state_cd"));
                        ps.setInt(2, rs.getInt("off_cd"));
                        ps.setInt(3, Integer.parseInt(temp[i]));
                        RowSet rs1 = tmgr.fetchDetachedRowSet();
                        if (rs1.next()) {
                            region += rs1.getString("regionAll") + ",";
                        }
                    }
                } else {
                    query = "select string_agg(descr,',') as region_covered from permit.vt_temp_permit_route pmt"
                            + " left outer JOIN tm_state r on r.state_code=any(string_to_array(region_covered,','))"
                            + " where appl_no = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, appl_no);
                    RowSet rs1 = tmgr.fetchDetachedRowSet();
                    if (rs1.next()) {
                        region = rs1.getString("region_covered");
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            region = "";
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

    public String[] getHeadingNote(String pmt_type, String pmt_catg) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        String[] requiredData = new String[2];
        String sqlString;
        RowSet rs;
        try {
            tmgr = new TransactionManager("getHeadingNote");
            sqlString = "SELECT  rule_heading, formno_heading \n"
                    + "  FROM " + TableList.VM_PERMIT_RULE_HEADING + " where state_cd = ? AND pmt_type=? and pmt_catg=?";
            ps = tmgr.prepareStatement(sqlString);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Integer.parseInt(pmt_type));
            ps.setInt(3, Integer.parseInt(pmt_catg));
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                requiredData[0] = rs.getString("rule_heading");
                requiredData[1] = rs.getString("formno_heading");
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
        return requiredData;
    }

    public Map getPrivateServicePermit(String Appl_no) {
        Map<String, String> goodsCagMap = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        RowSet rs;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("Private Service Permit Print");
            String query;
            query = "select to_char(pmt.op_dt,'DD-Mon-YYYY') as op_date,to_char(current_date,'DD-Mon-YYYY') as current_date,service.descr as service_type_descr,pmt.PMT_NO,to_char(pmt.VALID_FROM,'DD-Mon-YYYY') AS valid_from,pmt.goods_to_carry,to_char(pmt.VALID_UPTO,'DD-Mon-YYYY') as valid_upto,VVO.OWNER_NAME,VVO.F_NAME,(VVO.OFF_NAME) AS OFF_NAME,VVO.STATE_NAME,VVO.off_cd,pmt.off_cd as pmt_off_cd,VVO.state_cd,\n"
                    + " VVO.f_name,VVO.c_add1,VVO.c_add2,VVO.c_add3,VVO.c_district_name,VVO.c_pincode,VVO.c_state_name,VVO.REGN_NO,VVO.CHASI_NO, VVO.ENG_NO,VVO.MAKER_NAME, VVO.MODEL_NAME,VVO.VH_CLASS_DESC,VVO.SEAT_CAP,COALESCE(VVO.sleeper_cap,0) as sleeper_cap,COALESCE(VVO.STAND_CAP,0) as stand_cap,VVO.ld_wt,VVO.unld_wt,c.descr as pmt_type,g.descr as pmt_catg,pmt.pmt_type as pmt_type_code,pmt.pmt_catg as pmt_catg_code FROM " + TableList.VT_PERMIT + " pmt\n"
                    + " LEFT OUTER JOIN " + TableList.VIEW_VV_OWNER + " VVO ON VVO.REGN_NO = pmt.REGN_NO and VVO.state_cd = pmt.state_cd \n"
                    + " LEFT JOIN " + TableList.VVA_HPT + " VVH ON VVH.REGN_NO = pmt.REGN_NO\n"
                    + " LEFT OUTER JOIN " + TableList.VM_PERMIT_TYPE + " c on c.code = pmt.pmt_type\n"
                    + " LEFT OUTER JOIN " + TableList.VM_PERMIT_CATG + " g on g.code = pmt.pmt_catg AND g.permit_type = pmt.pmt_type AND g.state_cd = pmt.state_cd \n"
                    + " left outer join " + TableList.VM_SERVICES_TYPE + " service on service.code=pmt.service_type \n"
                    + " WHERE pmt.appl_no =?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Appl_no);
            rs = tmgr.fetchDetachedRowSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    goodsCagMap.put(rsmd.getColumnName(i), rs.getString(i));
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
        return goodsCagMap;
    }

    public Map getTempAndSpecialPermit(String Appl_no) {
        Map<String, String> tempDetails = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        RowSet rs;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("Get Temp And Special Permit");
            String query;
            query = "SELECT to_char(current_date,'DD-Mon-YYYY') as currentDate,pmt.PMT_NO,pmt.pur_cd,to_char(VVO.regn_upto,'DD-Mon-YYYY') as replace_date, to_char(pmt.VALID_FROM,'DD-Mon-YYYY') as valid_from,to_char(pmt.VALID_UPTO,'DD-Mon-YYYY') as valid_upto, VVO.OWNER_NAME,VVO.F_NAME,\n"
                    + " (VVO.OFF_NAME) AS OFF_NAME,pmt.off_cd as pmt_off_cd,VVO.STATE_NAME,VVO.f_name,VVO.c_add1,VVO.c_add2,VVO.c_add3,VVO.c_district_name,VVO.c_pincode,VVO.c_state_name,VVO.state_cd,VVO.off_cd, \n"
                    + " VVO.REGN_NO,VVO.CHASI_NO, VVO.ENG_NO,VVO.MAKER_NAME,to_char(VVO.REGN_DT,'DD-Mon-YYYY') as regn_dt,VVO.MODEL_NAME,VVO.manu_yr,VVO.VH_CLASS_DESC,VVO.SEAT_CAP,VVO.STAND_CAP,VVO.sleeper_cap ,VVO.ld_wt,VVO.unld_wt,VVO.vh_class, \n"
                    + " to_char(vtPmt.valid_from,'DD-Mon-YYYY') as vtPmtvalidFrom,to_char(vtPmt.valid_upto,'DD-Mon-YYYY') as vtPmtvalidUpto,vtPmt.pmt_no as vtPmtNO,VVO.chasi_no,x.descr as pmt_type,w.descr as temp_pmt_type_desc,y.descr as pmt_catg,pmt.via,pmt.goods_to_carry,z.descr as temp_pmt_catg_desc,\n"
                    + " PMT.route_fr as route_from,PMT.route_to as route_to,PMT.reason AS purpose,pmt.pmt_type as temp_pmt_type, pmt.pmt_catg as temp_pmt_catg,pmt.pur_cd FROM " + TableList.VT_TEMP_PERMIT + " pmt\n"
                    + " LEFT OUTER JOIN " + TableList.VIEW_VV_OWNER + "  VVO ON VVO.REGN_NO=pmt.REGN_NO and VVO.state_cd = pmt.state_cd\n"
                    + " LEFT OUTER JOIN " + TableList.VT_PERMIT + " vtPmt ON vtPmt.REGN_NO=pmt.REGN_NO\n"
                    + " LEFT OUTER JOIN " + TableList.VM_PERMIT_TYPE + " x on x.code = vtPmt.pmt_type\n"
                    + " LEFT OUTER JOIN " + TableList.VM_PERMIT_TYPE + " w on w.code = pmt.pmt_type\n"
                    + " LEFT OUTER JOIN " + TableList.VM_PERMIT_CATG + " y on y.code = vtPmt.pmt_catg AND y.permit_type = vtPmt.pmt_type AND y.state_cd = vtPmt.state_cd \n"
                    + " LEFT OUTER JOIN " + TableList.VM_PERMIT_CATG + " z on z.code = pmt.pmt_catg AND z.permit_type = pmt.pmt_type AND z.state_cd = pmt.state_cd \n"
                    + " WHERE pmt.appl_no =?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Appl_no);
            rs = tmgr.fetchDetachedRowSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    tempDetails.put(rsmd.getColumnName(i), rs.getString(i));
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
        return tempDetails;
    }

    public Map getHomeAuthCer(String Appl_no) {
        Map<String, String> authDetails = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        RowSet rs;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("Get Home Auth Cer");
            String query;
            query = "SELECT to_char(current_date,'DD-Mon-YYYY') as currentDate,auth.pmt_no,to_char(VVO.regn_upto,'DD-Mon-YYYY') as replace_date, to_char(auth.auth_fr,'DD-Mon-YYYY') as valid_from,to_char(auth.auth_to,'DD-Mon-YYYY') as valid_upto, VVO.OWNER_NAME,VVO.F_NAME,\n"
                    + "(VVO.OFF_NAME) AS OFF_NAME,VVO.STATE_NAME,VVO.f_name,VVO.c_add1,VVO.c_add2,VVO.c_add3,VVO.c_district_name,VVO.c_pincode,VVO.c_state_name,VVO.state_cd,VVO.off_cd,pmt.pmt_type,vvo.manu_yr,to_char(pmt.valid_upto,'DD-Mon-YYYY') as pmt_valid_upto,\n"
                    + "VVO.REGN_NO,VVO.CHASI_NO, VVO.ENG_NO,VVO.MAKER_NAME,to_char(VVO.REGN_DT,'DD-Mon-YYYY') as regn_dt,VVO.MODEL_NAME,VVO.VH_CLASS_DESC,VVO.SEAT_CAP,VVO.ld_wt,VVO.unld_wt,auth.auth_no,to_char(pmt.VALID_FROM,'DD-Mon-YYYY') as pmt_valid_from, COALESCE(to_char(pmt.replace_date,'DD-Mon-YYYY'),'') as replace_date,\n"
                    + "pmt.region_covered,pmt.pmt_catg,COALESCE(VVO.sleeper_cap,'0') as sleeper_cap \n"
                    + "FROM  " + TableList.VHA_PERMIT_HOME_AUTH + "  VHA_AUTH\n"
                    + "LEFT OUTER JOIN " + TableList.VT_PERMIT_HOME_AUTH + " auth ON auth.REGN_NO=VHA_AUTH.REGN_NO \n"
                    + "LEFT OUTER JOIN " + TableList.VT_PERMIT + " pmt ON pmt.REGN_NO=auth.REGN_NO \n"
                    + "LEFT OUTER JOIN " + TableList.VIEW_VV_OWNER + " VVO ON VVO.REGN_NO=auth.REGN_NO and VVO.state_cd = pmt.state_cd \n"
                    + "WHERE VHA_AUTH.appl_no =?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Appl_no);
            rs = tmgr.fetchDetachedRowSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    authDetails.put(rsmd.getColumnName(i), rs.getString(i));
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
        return authDetails;
    }

    public Map getHomeAuthCerThroughRegnNo(String regnNo) {
        Map<String, String> authDetails = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        RowSet rs;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("Get Home Auth Cer");
            String query;
            query = "SELECT to_char(a.op_dt,'DD-Mon-YYYY') as op_date,to_char(current_date,'DD-Mon-YYYY') as currentDate,a.pmt_no,to_char(b.regn_upto,'DD-Mon-YYYY') as replace_date, to_char(a.auth_fr,'DD-Mon-YYYY') as valid_from,to_char(a.auth_to,'DD-Mon-YYYY') as valid_upto, b.OWNER_NAME,b.F_NAME,\n"
                    + "(b.OFF_NAME) AS OFF_NAME,b.STATE_NAME,b.f_name,b.c_add1,b.c_add2,b.c_add3,b.c_district_name,b.c_pincode,b.c_state_name,b.state_cd,b.off_cd,c.pmt_type,b.manu_yr,to_char(c.valid_upto,'DD-Mon-YYYY') as pmt_valid_upto,\n"
                    + "b.REGN_NO,b.CHASI_NO, b.ENG_NO,b.MAKER_NAME,to_char(b.REGN_DT,'DD-Mon-YYYY') as regn_dt,b.MODEL_NAME,b.VH_CLASS_DESC,b.SEAT_CAP,b.ld_wt,b.unld_wt,a.auth_no,to_char(c.VALID_FROM,'DD-Mon-YYYY') as pmt_valid_from,COALESCE(to_char(c.replace_date,'DD-Mon-YYYY'),'') as replace_date,c.region_covered,c.pmt_catg,COALESCE(b.sleeper_cap,'0') as sleeper_cap \n"
                    + "FROM  " + TableList.VT_PERMIT_HOME_AUTH + "  a\n"
                    + "LEFT OUTER JOIN " + TableList.VT_PERMIT + " c ON c.REGN_NO=a.REGN_NO \n"
                    + "LEFT OUTER JOIN " + TableList.VIEW_VV_OWNER + " b ON b.REGN_NO=a.REGN_NO and b.state_cd = c.state_cd\n"
                    + "WHERE c.state_cd = ? and a.REGN_NO = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, regnNo);
            rs = tmgr.fetchDetachedRowSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    authDetails.put(rsmd.getColumnName(i), rs.getString(i));
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
        return authDetails;
    }

    public Map getOfferLetterDetails(String Appl_no, String regn_no) {
        Map<String, String> olDtls = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        RowSet rs;
        PreparedStatement ps;
        String query;
        try {
            tmgr = new TransactionManager("getOfferLetterDetails");
            if (regn_no.equalsIgnoreCase("NEW")) {
                query = "SELECT a.pmt_type as pmt_type_code,a.pmt_catg as pmt_catg_code,'' as old_regn_no,to_char(v.appl_dt,'DD-Mon-YYYY') as appl_dt,'NEW' as regn_no,a.state_cd,a.off_cd,f.descr as state_name,to_char(current_date,'DD-Mon-YYYY') as currentDate,offer_no,order_no,to_char(order_dt,'DD-Mon-YYYY') as order_dt,\n"
                        + " c.descr as pmt_type,b.owner_name,b.f_name,'' as norms_descr,period,CASE WHEN a.period_mode  = 'Y' THEN 'YEAR(S)' WHEN a.period_mode  = 'M' THEN 'MONTH(S)' END as period_mode,e.rcpt_no,g.descr as pmt_catg,i.descr as vh_class,\n"
                        + " coalesce(b.c_add1, '') ||', '|| coalesce(b.c_add2, '') ||', '|| coalesce(b.c_add3, '') ||', '|| coalesce(z.descr, '') ||', '|| coalesce(b.c_pincode,0) AS address,b.seat_cap,k.descr as fuel_type from " + TableList.VA_PERMIT + " a \n"
                        + "                        left outer join " + TableList.VA_PERMIT_OWNER + "  b on b.APPL_NO = a.APPL_NO \n"
                        + "                        left outer join " + TableList.VA_DETAILS + " v on v.appl_no = a.appl_no and v.state_cd = a.state_cd and v.off_cd=a.off_cd \n"
                        + "                        left outer join " + TableList.TM_DISTRICT + " z On b.c_district=z.dist_cd \n"
                        + "                        left outer join " + TableList.VM_PERMIT_TYPE + "  c on c.code = a.pmt_type \n"
                        + "                        left outer join " + TableList.VM_PERMIT_CATG + "  g on g.code = a.pmt_catg AND g.permit_type = a.pmt_type AND g.state_cd = a.state_cd  \n"
                        + "                        left outer join " + TableList.VT_FEE + " e on e.pur_cd = ? and e.rcpt_no = a.rcpt_no and e.state_cd = a.state_cd and e.off_cd = a.off_cd  \n"
                        + "                        left outer join " + TableList.TM_STATE + "  f on f.state_code = a.state_cd \n"
                        + "                        left outer join " + TableList.VM_FUEL + "  k on k.code = b.fuel \n"
                        + "                        left outer join " + TableList.VM_VH_CLASS + "  i on i.vh_class = b.vh_class \n"
                        + "                        where a.appl_no = ? and a.state_cd = ?";
                ps = tmgr.prepareStatement(query);
                ps.setInt(1, TableConstants.VM_PMT_FRESH_PUR_CD);
                ps.setString(2, Appl_no);
                ps.setString(3, Util.getUserStateCode());
            } else {
                query = "SELECT a.pmt_type as pmt_type_code,a.pmt_catg as pmt_catg_code,h.old_regn_no, to_char(f.appl_dt,'DD-Mon-YYYY') as appl_dt,b.c_state_name as state_name,a.state_cd,a.off_cd,a.regn_no,to_char(current_date,'DD-Mon-YYYY') as currentDate,offer_no,order_no,to_char(order_dt,'DD-Mon-YYYY') as order_dt,c.descr as pmt_type,b.owner_name,b.f_name,norms_descr,period,CASE WHEN a.period_mode  = 'Y' THEN 'YEAR(S)' WHEN a.period_mode  = 'M' THEN 'MONTH(S)' ELSE d.descr END as period_mode,e.rcpt_no,g.descr as pmt_catg,b.vh_class_desc as vh_class,"
                        + " b.c_add1||', '||b.c_add2||', '||b.c_add3||', '|| b.c_district_name||', '|| b.c_pincode||', '|| b.c_state_name AS address,b.seat_cap,b.fuel_descr as fuel_type from " + TableList.VA_PERMIT + " a\n"
                        + " left outer join " + TableList.VIEW_VV_OWNER + " b on b.regn_no = a.regn_no and b.state_cd = a.state_cd \n"
                        + " left outer join " + TableList.VA_DETAILS + " f on f.appl_no = a.appl_no and f.state_cd = a.state_cd and f.off_cd=a.off_cd \n"
                        + " left outer join " + TableList.VM_PERMIT_TYPE + " c on c.code = a.pmt_type\n"
                        + " left outer join " + TableList.VM_PERMIT_CATG + " g on g.code = a.pmt_catg AND g.permit_type = a.pmt_type AND g.state_cd = a.state_cd \n"
                        + " left outer join " + TableList.VM_TAX_MODE + " d on d.tax_mode = a.period_mode\n"
                        + " left outer join " + TableList.VT_FEE + " e on e.regn_no = a.regn_no and e.pur_cd = ? \n"
                        + " left outer join " + TableList.VH_CONVERSION + " h on h.new_regn_no = a.regn_no and h.state_cd = a.state_cd  \n"
                        + " where a.appl_no = ? order by e.rcpt_dt,h.moved_on  DESC limit 1";
                ps = tmgr.prepareStatement(query);
                ps.setInt(1, TableConstants.VM_PMT_APPLICATION_PUR_CD);
                ps.setString(2, Appl_no);
            }
            rs = tmgr.fetchDetachedRowSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    olDtls.put(rsmd.getColumnName(i), rs.getString(i));
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
        return olDtls;
    }

    public Map getCounterSignatureDetails(String Appl_no) {
        Map<String, String> countSign = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        RowSet rs;
        PreparedStatement ps;
        String query;
        try {
            tmgr = new TransactionManager("getCounterSignatureDetails");
            query = " SELECT a.regn_no,count_sign_no,pmt_no,v.descr as vh_class,vc.catg_desc,a.seat_cap,COALESCE(d.sleeper_cap,'0') as sleeper_cap,COALESCE(d.stand_cap,'0') as stand_cap,to_char(count_valid_from,'dd-Mon-yyyy') as count_valid_from ,\n"
                    + "                    to_char(count_valid_upto,'dd-Mon-yyyy') as count_valid_upto,a.owner_name,a.f_name,a.unld_wt,a.ld_wt,a.state_cd_from,a.off_cd_from,a.rcpt_no as receipt_number ,   \n"
                    + "                    to_char(current_date,'DD-Mon-YYYY') as currentDate,b.descr as state_name,\n"
                    + "                    a.c_add1,a.c_add2,a.c_add3,c.descr as c_state_name,a.c_pincode,a.state_cd as state_code,a.off_cd,d.maker_name,d.model_name,d.body_type,e.region as area \n"
                    + "                    from permit.vt_permit_countersignature a\n"
                    + "                    left outer join TM_STATE  b on b.state_code = a.state_cd \n"
                    + "                    left join vm_vh_class v on v.vh_class=a.vh_class\n"
                    + "                    left outer join TM_STATE  c on c.state_code = a.c_state \n"
                    + "                    left outer join vm_vch_catg vc on vc.catg = a.vch_catg\n"
                    + "                    left outer join  vv_owner d  on d.regn_no=a.regn_no \n"
                    + "                    left outer join permit.vm_region e on e.region_cd=a.region and e.state_cd=a.state_cd and e.off_cd=a.off_cd\n"
                    + "                    where appl_no =?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Appl_no);
            rs = tmgr.fetchDetachedRowSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    countSign.put(rsmd.getColumnName(i), rs.getString(i));
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
        return countSign;
    }

    public int getVacancyNoForCounterSign(String state_from, String regn_no) {
        int vacancy_no = 0;
        TransactionManager tmgr = null;
        RowSet rs;
        PreparedStatement ps;
        String query;
        try {
            tmgr = new TransactionManager("getVacancyNoForCounterSign");
            query = "select vacancy_no from " + TableList.VT_OTHER_STATE_VCH_FOR_CS + " where state_cd=? and off_cd=? and state_from=? and regn_no=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserOffCode());
            ps.setString(3, state_from);
            ps.setString(4, regn_no);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                vacancy_no = rs.getInt("vacancy_no");
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
        return vacancy_no;
    }

    public List<PassengerPermitDetailDobj> getRouteData(String appl_no, String state_cd, int off_cd, String tableName, boolean otherOffRouteAllow) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        RowSet rs;
        String sql;
        List<PassengerPermitDetailDobj> routeDetailslist = null;
        PassengerPermitDetailDobj dobj = null;
        if (Util.getSelectedSeat() != null) {
            off_cd = Util.getSelectedSeat().getOff_cd();
        }

        try {
            tmgr = new TransactionManager("Get Permit Route Detail");
            if (otherOffRouteAllow) {
                sql = " select a.state_to,c.descr as state_to_name,a.code, a.floc ,a.tloc,a.via,a.rlength,b.no_of_trips ,a.route_flag,a.overlapping,a.overlapping_length, Case When a.route_flag='E' Then 'Extension' When a.route_flag='D' Then 'Diversion' When a.route_flag='C' Then 'Curtailment'  END r_flag from " + TableList.VM_ROUTE_MASTER + " a  join " + tableName + " b ON a.code= b.route_cd and a.state_cd=b.state_cd and a.off_cd=b.off_cd \n"
                        + " LEFT JOIN " + TableList.TM_STATE + " c on a.state_to=c.state_code \n"
                        + " Where a.code in (SELECT route_cd from " + tableName + " where appl_no=?) AND a.state_cd = ? and b.appl_no=?";
            } else {
                sql = " select a.state_to,c.descr as state_to_name,a.code, a.floc ,a.tloc,a.via,a.rlength,b.no_of_trips ,a.route_flag,a.overlapping,a.overlapping_length, Case When a.route_flag='E' Then 'Extension' When a.route_flag='D' Then 'Diversion' When a.route_flag='C' Then 'Curtailment'  END r_flag from " + TableList.VM_ROUTE_MASTER + " a  join " + tableName + " b ON a.code= b.route_cd and a.state_cd=b.state_cd and a.off_cd=b.off_cd \n"
                        + " LEFT JOIN " + TableList.TM_STATE + " c on a.state_to=c.state_code \n"
                        + " Where a.code in (SELECT route_cd from " + tableName + " where appl_no=?) AND a.state_cd = ? AND a.off_cd = " + off_cd + " and b.appl_no=?";
            }
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, state_cd);
            ps.setString(3, appl_no);
            rs = tmgr.fetchDetachedRowSet();
            routeDetailslist = new ArrayList<PassengerPermitDetailDobj>();
            while (rs.next()) {
                dobj = new PassengerPermitDetailDobj();
                dobj.setFloc(rs.getString("floc"));
                dobj.setTloc(rs.getString("tloc"));
                dobj.setRout_code(rs.getString("code"));
                dobj.setStart_POINT(rs.getString("via"));
                dobj.setRout_length(String.valueOf(rs.getInt("rlength")));
                dobj.setNumberOfTrips(String.valueOf(rs.getInt("no_of_trips")));
                if (!CommonUtils.isNullOrBlank(rs.getString("r_flag"))) {
                    dobj.setRouteFlag(rs.getString("r_flag"));
                } else {
                    dobj.setRouteFlag(null);
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("overlapping"))) {
                    dobj.setNhOverlapping(rs.getString("overlapping"));
                    dobj.setNhOverlappingLength(rs.getInt("overlapping_length"));
                } else {
                    dobj.setNhOverlapping(null);
                }
                dobj.setState_cd_to(rs.getString("state_to_name"));
                routeDetailslist.add(dobj);
            }
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
                }
            } catch (Exception e) {
                routeDetailslist = null;
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return routeDetailslist;
    }

    public List<AITPStateFeeDraftDobj> getAitpPaymentData(String regn_no) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        List<AITPStateFeeDraftDobj> paymentList = new ArrayList<>();
        AITPStateFeeDraftDobj dobj = null;
        String sql = "";
        try {
            tmgr = new TransactionManager("getAitpPaymentDetails");
            sql = "Select state_cd,off_cd,appl_no, sr_no, a.regn_no, pay_state_cd,instrument_type, "
                    + " instrument_no, instrument_dt,to_char(instrument_dt,'DD-Mon-YYYY') as instrument_dt_str, instrument_amt,a.bank_code,branch_name,payable_to,recieved_dt,to_char(recieved_dt,'DD-Mon-YYYY') as recieved_dt_str,a.op_dt,b.descr,c.descr as bank_name,to_char(d.auth_fr,'DD-Mon-YYYY') as auth_fr,to_char(d.auth_to,'DD-Mon-YYYY') as auth_to from "
                    + TableList.VT_INSTRUMENT_AITP + " a inner join " + TableList.TM_STATE + " b on a.pay_state_cd=b.state_code inner join " + TableList.TM_BANK + " c on a.bank_code=c.bank_code inner join " + TableList.VT_PERMIT_HOME_AUTH + " d on d.regn_no = a.regn_no where a.regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new AITPStateFeeDraftDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setSr_no(rs.getInt("sr_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setPay_state_cd(rs.getString("pay_state_cd"));
                dobj.setPay_state_descr(rs.getString("descr"));
                dobj.setInstrument_type(rs.getString("instrument_type"));
                dobj.setInstrument_no(rs.getString("instrument_no"));
                dobj.setInstrument_dt(rs.getDate("instrument_dt"));
                dobj.setInstrument_amt(rs.getLong("instrument_amt"));
                dobj.setBank_code(rs.getString("bank_code"));
                dobj.setBank_name(rs.getString("bank_name"));
                dobj.setBranch_name(rs.getString("branch_name"));
                dobj.setPayable_to(rs.getString("payable_to"));
                dobj.setRecieved_dt(rs.getDate("recieved_dt"));
                dobj.setInstrument_dtString(rs.getString("instrument_dt_str"));
                dobj.setRecieved_dtString(rs.getString("recieved_dt_str"));
                dobj.setPeriodFromDt(rs.getString("auth_fr"));
                dobj.setPeriodToDate(rs.getString("auth_to"));
                dobj.setMax_draft_date(DateUtil.parseDate(DateUtil.getCurrentDate()));
                dobj.setMin_draft_date(DateUtils.addToDate(dobj.getMax_draft_date(), 2, -3));
                if (dobj.getInstrument_dt() != null && dobj.getMin_draft_date() != null && dobj.getInstrument_dt().before(dobj.getMin_draft_date())) {
                    dobj.setMin_draft_date(dobj.getInstrument_dt());
                }
                paymentList.add(dobj);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (DateUtilsException e) {
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
        return paymentList;
    }

    public String getRegionDetail(String appl_no, String stateCd, int offCd, String TableName) {
        String region = null;
        TransactionManager tmgr = null;
        RowSet rs;
        PreparedStatement ps;
        if (Util.getSelectedSeat() != null) {
            offCd = Util.getSelectedSeat().getOff_cd();
        }
        try {
            tmgr = new TransactionManager("Get Region Detail");
            String query;
            query = "select region_covered from " + TableName + " WHERE appl_no=? AND state_cd = ? AND off_cd = ? \n";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                region = rs.getString("region_covered");
                if (CommonUtils.isNullOrBlank(region)) {
                    return null;
                }
                String[] tempString = region.split(",");
                for (int i = 0; i < tempString.length; i++) {
                    try {
                        Integer.parseInt(tempString[i]);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }
            query = "select array_to_string (array(select r.region from " + TableName + " pmt \n"
                    + "LEFT JOIN " + TableList.VM_REGION + " r on r.off_cd=pmt.off_cd\n"
                    + "WHERE pmt.appl_no=? AND r.state_cd = ? AND r.off_cd = ?  and r.region_cd = ANY(string_to_array(regexp_replace(pmt.region_covered,',$','') ,',')::integer[])),',') as regionAll";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                region = rs.getString("regionAll");
                if (CommonUtils.isNullOrBlank(region)) {
                    region = null;
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
        return region;
    }

    public String getRegionDetailOnAuth(String regn_no, String stateCd, int offCd, String TableName) {
        String region = null;
        TransactionManager tmgr = null;
        RowSet rs;
        PreparedStatement ps;
        if (Util.getSelectedSeat() != null) {
            offCd = Util.getSelectedSeat().getOff_cd();
        }
        try {
            tmgr = new TransactionManager("getRegionDetailOnAuth");
            String query = "select array_to_string (array(select r.region from " + TableName + " pmt \n"
                    + " LEFT JOIN " + TableList.VM_REGION + " r on r.off_cd=pmt.off_cd\n"
                    + " WHERE pmt.regn_no=? AND r.state_cd = ? AND r.off_cd = ?  and r.region_cd = ANY(string_to_array(regexp_replace(pmt.region_covered,',$','') ,',')::integer[])),',') as regionAll";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                region = rs.getString("regionAll");
                if (CommonUtils.isNullOrBlank(region)) {
                    region = null;
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
        return region;
    }

    public String getRegionDetailStateVise(String appl_no, String regionCovered) {
        String region = null;
        TransactionManager tmgr = null;
        try {
            String[] temp = regionCovered.split(",");
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < temp.length; i++) {
                stringBuilder.append("(").append("'").append(temp[i].trim()).append("'").append("),");
            }
            tmgr = new TransactionManager("getRegionDetailStateVise");
            String query = "select string_agg(descr,',') as regionAll from tm_state  where (state_code) in (" + stringBuilder.substring(0, stringBuilder.length() - 1) + "); ";
            tmgr.prepareStatement(query);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                region = rs.getString("regionAll");
                if (CommonUtils.isNullOrBlank(region)) {
                    region = null;
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
        return region;
    }

    public Map getHeadingFooterNote(String state_cd) {
        Map<String, String> confiTableValue = null;
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("Get Heading Footer Note");
            String query;
            query = "SELECT sc_rule_heading, sc_formno_heading, cc_rule_heading, \n"
                    + "       cc_formno_heading, aitp_rule_heading, aitp_formno_heading, psvp_rule_heading, \n"
                    + "       psvp_formno_heading, gp_rule_heading, gp_formno_heading, np_rule_heading, \n"
                    + "       np_formno_heading, temp_rule_heading, temp_formno_heading, spec_rule_heading, \n"
                    + "       spec_formno_heading, auth_aitp_rule_heading, auth_aitp_formno_heading, \n"
                    + "       auth_np_rule_heading, auth_np_formno_heading, note_in_footer,permit_main_header,permit_main_footer\n"
                    + "  FROM " + TableList.VM_PERMIT_STATE_CONFIGURATION + " where state_cd = ?";
            confiTableValue = new LinkedHashMap<String, String>();
            ps = tmgr.prepareStatement(query);
            ps.setString(1, state_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    confiTableValue.put(rsmd.getColumnName(i), rs.getString(i));
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
        return confiTableValue;
    }

    public Map getNocDetails(String Appl_no, String docId) {
        Map<String, String> nocDetails = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        RowSet rs;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("getNocDetails");
            String query;
            query = "select to_char((NOW() + interval '2 month')::date,'dd-Mon-yyyy') AS after2_month_dt,e.off_name,state_name,a.regn_no,b.pmt_no,c.owner_name,c.c_add1,c.c_add2,c.c_add3,c.c_state_name,c.c_pincode,to_char(a.op_dt,'dd-Mon-yyyy') as op_dt,to_char(current_date,'dd-Mon-yyyy') as currentdate,to_char(regn_dt,'yyyy') as year,d.trans_pur_cd from " + TableList.VH_PERMIT_PRINT + " a\n"
                    + "left join " + TableList.VH_PERMIT + " b on b.regn_no = a.regn_no AND b.state_cd = ?\n"
                    + "left join " + TableList.VIEW_VV_OWNER + " c on c.regn_no = a.regn_no AND c.state_cd = ?\n"
                    + "left join " + TableList.VT_PERMIT_TRANSACTION + " d on d.regn_no = a.regn_no AND d.appl_no = a.appl_no\n"
                    + "left join " + TableList.TM_OFFICE + " e on e.state_cd = c.state_cd and e.off_cd = c.off_cd \n"
                    + "where a.appl_no=? AND doc_id=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, Util.getUserStateCode());
            ps.setString(3, Appl_no);
            ps.setString(4, docId);
            rs = tmgr.fetchDetachedRowSet_No_release();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    nocDetails.put(rsmd.getColumnName(i), rs.getString(i));
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
        return nocDetails;
    }

    public Map getAckDetails(String applNo, String regnNo, String docId) {
        Map<String, String> ackDetails = new HashMap<String, String>();
        TransactionManager tmgr = null;
        String query = null;
        try {
            tmgr = new TransactionManager("getAckDetails");
            if (regnNo.equalsIgnoreCase("NEW")) {
                query = "SELECT b.seat_cap,'' as color,'' as regn_dt,a.state_cd,a.off_cd,a.appl_no,a.regn_no,e.descr as pur_cd ,c.descr as pmt_type,COALESCE(d.descr,'Not Mention') as pmt_catg,b.owner_name,b.f_name,b.c_add1,b.c_add2,b.c_add3,\n"
                        + "       k.descr as c_district_name,j.descr as c_state_name,COALESCE(b.c_pincode,'0') as c_pincode,'Not Mention' as chasi_no,'Not Mention' as eng_no,h.descr as vh_class_desc,g.catg_desc,i.descr as fuel_descr,\n"
                        + "       j.descr as state_name,COALESCE(b.mobile_no,'0') as mobile_no,COALESCE(b.email_id,'Not Mention') as email_id,to_char(current_timestamp,'DD-Mon-YYYY') as op_dt\n"
                        + "from " + TableList.VA_PERMIT + " a \n"
                        + "left join " + TableList.VA_PERMIT_OWNER + " b on a.regn_no = b.regn_no and a.appl_no = b.appl_no\n"
                        + "inner join " + TableList.VM_PERMIT_TYPE + " c on c.code = a.pmt_type\n"
                        + "left join " + TableList.VM_PERMIT_CATG + " d on d.code = a.pmt_catg and d.state_cd = a.state_cd and d.permit_type = a.pmt_type\n"
                        + "inner join " + TableList.TM_PURPOSE_MAST + " e on e.pur_cd = a.pur_cd\n"
                        + "left join " + TableList.VM_VCH_CATG + " g on g.catg = b.vch_catg\n"
                        + "left join " + TableList.VM_VH_CLASS + " h on h.vh_class = b.vh_class\n"
                        + "left join " + TableList.VM_FUEL + " i on i.code = b.fuel\n"
                        + "left join " + TableList.TM_STATE + " j on j.state_code = a.state_cd\n"
                        + "left join " + TableList.TM_DISTRICT + " k on k.dist_cd = b.c_district  and k.state_cd = a.state_cd\n"
                        + "where a.appl_no = ? and a.regn_no = ?";
            } else {
                query = "SELECT b.seat_cap,b.color,to_char(b.regn_dt,'DD-Mon-YYYY') as regn_dt,a.state_cd,a.off_cd,a.appl_no,a.regn_no,e.descr as pur_cd ,c.descr as pmt_type,COALESCE(d.descr,'Not Mention') as pmt_catg,b.owner_name,b.f_name,b.c_add1,b.c_add2,b.c_add3,b.c_district_name,b.c_state_name,COALESCE(b.c_pincode,'0') as c_pincode,\n"
                        + " b.chasi_no,b.eng_no,b.vh_class_desc,g.catg_desc,b.fuel_descr,b.state_name,COALESCE(f.mobile_no,'0') as mobile_no,COALESCE(f.email_id,'Not Mention') as email_id,to_char(current_timestamp,'DD-Mon-YYYY') as op_dt "
                        + " from " + TableList.VA_PERMIT + " a\n"
                        + " left join vv_owner b on a.regn_no = b.regn_no and b.state_cd = a.state_cd\n"
                        + " inner join " + TableList.VM_PERMIT_TYPE + " c on c.code = a.pmt_type\n"
                        + " left join " + TableList.VM_PERMIT_CATG + " d on d.code = a.pmt_catg and d.state_cd = a.state_cd and d.permit_type = a.pmt_type\n"
                        + " inner join " + TableList.TM_PURPOSE_MAST + " e on e.pur_cd = a.pur_cd\n"
                        + " left join " + TableList.VT_OWNER_IDENTIFICATION + " f on f.regn_no = b.regn_no and f.state_cd = a.state_cd\n"
                        + " left join " + TableList.VM_VCH_CATG + " g on g.catg = b.vch_catg\n"
                        + " where a.appl_no = ? and a.regn_no = ?";
            }
            PreparedStatement ps = tmgr.prepareStatement(query);
            ps.setString(1, applNo);
            ps.setString(2, regnNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    ackDetails.put(rsmd.getColumnName(i), rs.getString(i));
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
        return ackDetails;
    }

    public Map getSurrenderSlipDetails(String ApplNo, String docId) {
        Map<String, String> surrSlipDetails = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        RowSet rs;
        PreparedStatement ps;
        ResultSetMetaData rsmd;
        try {
            tmgr = new TransactionManager("getSurrenderSlipDetails");
            String query;

            query = "select c.state_cd,e.descr as state_name,f.off_name,b.trans_pur_cd,b.pmt_no,b.regn_no,to_char(b.op_dt,'DD-Mon-YYYY') as op_dt,c.owner_name,c.f_name,c.c_add1,c.c_add2,c.c_add3,c.c_pincode,to_char(d.valid_upto,'DD-Mon-YYYY') as valid_upto from " + TableList.VH_PERMIT_PRINT + " a \n"
                    + "inner join " + TableList.VT_PERMIT_TRANSACTION + " b on b.regn_no = a.regn_no AND a.appl_no = b.appl_no\n"
                    + "inner join " + TableList.VT_OWNER + " c on c.regn_no = b.regn_no\n"
                    + "inner join " + TableList.VH_PERMIT + " d on d.regn_no = b.regn_no AND d.pmt_no = b.pmt_no\n"
                    + "left join " + TableList.TM_STATE + " e on e.state_code = c.state_cd \n"
                    + "left join " + TableList.TM_OFFICE + " f on f.state_cd = c.state_cd and f.off_cd = c.off_cd \n"
                    + "where a.doc_id = ? AND b.appl_no = ? order by d.moved_on DESC limit 1";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, docId);
            ps.setString(2, ApplNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            rsmd = rs.getMetaData();
            if (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    surrSlipDetails.put(rsmd.getColumnName(i), rs.getString(i));
                }
            } else {
                query = "select c.state_cd,e.descr as state_name,f.off_name,b.trans_pur_cd,b.pmt_no,b.regn_no,to_char(b.op_dt,'DD-Mon-YYYY') as op_dt,c.owner_name,c.f_name,c.c_add1,c.c_add2,c.c_add3,c.c_pincode,to_char(d.valid_upto,'DD-Mon-YYYY') as valid_upto from " + TableList.VH_PERMIT_PRINT + " a \n"
                        + "inner join " + TableList.VHA_PERMIT_TRANSACTION + " b on b.regn_no = a.regn_no AND a.appl_no = b.appl_no AND b.trans_pur_cd in (?,?)\n"
                        + "inner join " + TableList.VT_OWNER + " c on c.regn_no = b.regn_no\n"
                        + "inner join " + TableList.VH_PERMIT + " d on d.regn_no = b.regn_no AND d.pmt_no = b.pmt_no\n"
                        + "inner join " + TableList.TM_STATE + " e on e.state_code = c.state_cd \n"
                        + "left join " + TableList.TM_OFFICE + " f on f.state_cd = c.state_cd and f.off_cd = c.off_cd \n"
                        + "where a.doc_id = ? AND b.appl_no = ? and c.state_cd=? order by d.moved_on DESC limit 1";
                ps = tmgr.prepareStatement(query);
                ps.setInt(1, TableConstants.VM_PMT_CANCELATION_PUR_CD);
                ps.setInt(2, TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD);
                ps.setString(3, docId);
                ps.setString(4, ApplNo);
                ps.setString(5, Util.getUserStateCode());
                rs = tmgr.fetchDetachedRowSet_No_release();
                rsmd = rs.getMetaData();
                if (rs.next()) {
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        surrSlipDetails.put(rsmd.getColumnName(i), rs.getString(i));
                    }
                } else {

                    query = "select c.state_cd,e.descr as state_name,f.off_name,b.trans_pur_cd,b.pmt_no,b.regn_no,to_char(b.op_dt,'DD-Mon-YYYY') as op_dt,c.owner_name,c.f_name,c.c_add1,c.c_add2,c.c_add3,c.c_pincode,to_char(d.valid_upto,'DD-Mon-YYYY') as valid_upto from " + TableList.VH_PERMIT_PRINT + " a \n"
                            + "inner join " + TableList.VHA_PERMIT_TRANSACTION + " b on b.regn_no = a.regn_no AND a.appl_no = b.appl_no AND b.trans_pur_cd in (?,?)\n"
                            + "inner join " + TableList.VT_OWNER + " c on c.regn_no = b.regn_no\n"
                            + "inner join " + TableList.VH_TEMP_PERMIT + " d on d.regn_no = b.regn_no AND d.pmt_no = b.pmt_no\n"
                            + "inner join " + TableList.TM_STATE + " e on e.state_code = c.state_cd \n"
                            + "left join " + TableList.TM_OFFICE + " f on f.state_cd = c.state_cd and f.off_cd = c.off_cd \n"
                            + "where a.doc_id = ? AND b.appl_no = ? order by d.moved_on DESC limit 1";
                    ps = tmgr.prepareStatement(query);
                    ps.setInt(1, TableConstants.VM_PMT_CANCELATION_PUR_CD);
                    ps.setInt(2, TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD);
                    ps.setString(3, docId);
                    ps.setString(4, ApplNo);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    rsmd = rs.getMetaData();
                    if (rs.next()) {
                        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                            surrSlipDetails.put(rsmd.getColumnName(i), rs.getString(i));
                        }
                    }
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
        return surrSlipDetails;
    }

    public Map getPreviousOwnerDetails(String regnNo, int purCd) {
        Map<String, String> preOwnerDetails = new LinkedHashMap<String, String>();
        TransactionManager tmgr = null;
        RowSet rs;
        PreparedStatement ps;
        ResultSetMetaData rsmd;
        String query = "";
        try {
            tmgr = new TransactionManager("getNocDetails");
            query = "select regn_no,owner_name,f_name from " + TableList.VT_OWNER + " where regn_no in "
                    + " (select regn_no from " + TableList.VHA_PERMIT_TRANSACTION + " where state_cd = ? AND off_cd = ? AND trans_pur_cd = ? AND pur_cd = ? AND new_regn_no = ? order by op_dt DESC limit 1)";
            ps = tmgr.prepareStatement(query);
            int j = 1;
            ps.setString(j++, Util.getUserStateCode());
            ps.setInt(j++, Util.getSelectedSeat().getOff_cd());
            ps.setInt(j++, purCd);
            ps.setInt(j++, TableConstants.VM_PMT_RESTORE_PUR_CD);
            ps.setString(j++, regnNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            rsmd = rs.getMetaData();
            if (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    preOwnerDetails.put(rsmd.getColumnName(i), rs.getString(i));
                }
            } else {
                query = "select regn_no,owner_name,f_name from " + TableList.VT_OWNER + " where state_cd = ? AND regn_no = ? ";
                ps = tmgr.prepareStatement(query);
                j = 1;
                ps.setString(j++, Util.getUserStateCode());
                ps.setString(j++, regnNo);
                rs = tmgr.fetchDetachedRowSet_No_release();
                rsmd = rs.getMetaData();
                if (rs.next()) {
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        preOwnerDetails.put(rsmd.getColumnName(i), rs.getString(i));
                    }
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
        return preOwnerDetails;
    }

    public List<OwnerDetailsDobj> getTrailerDetails(String link_regn_no, String state_cd, int off_cd) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        RowSet rs;
        OwnerDetailsDobj tDobj = null;
        List<OwnerDetailsDobj> trailerList = null;
        try {
            tmgr = new TransactionManager("getTrailerDetails");
            String sql = " select ld_wt,unld_wt,regn_no, vh_class_desc,maker_name,model_name,manu_yr from " + TableList.VIEW_VV_OWNER + "   "
                    + " where  regn_no = (select regn_no from " + TableList.VT_SIDE_TRAILER + " where state_cd=? and link_regn_no=? and off_cd=? order by op_dt desc limit 1) and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setString(2, link_regn_no);
            ps.setInt(3, off_cd);
            ps.setString(4, state_cd);
            rs = tmgr.fetchDetachedRowSet();
            trailerList = new ArrayList<>();
            if (rs.next()) {
                tDobj = new OwnerDetailsDobj();
                tDobj.setVh_class_desc(rs.getString("vh_class_desc"));
                tDobj.setMaker_name(rs.getString("maker_name"));
                tDobj.setModel_name(rs.getString("model_name"));
                tDobj.setManu_yr(rs.getInt("manu_yr"));
                tDobj.setLd_wt(rs.getInt("ld_wt"));
                tDobj.setUnld_wt(rs.getInt("unld_wt"));
                tDobj.setRegn_no(rs.getString("regn_no"));
                trailerList.add(tDobj);
            }
            if (tDobj == null) {
                trailerList = null;
            }
        } catch (Exception e) {
            trailerList = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                trailerList = null;
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return trailerList;
    }

    public String getDupReason(String regnNo, int purCd) {
        TransactionManagerReadOnly tmgr = null;
        String reason = "";
        try {
            tmgr = new TransactionManagerReadOnly("getDupReason");
            String query = "SELECT reason from vh_dup where regn_no = ? and pur_cd = ? order by moved_on DESC limit 1";
            PreparedStatement ps = tmgr.prepareStatement(query);
            int j = 1;
            ps.setString(j++, regnNo);
            ps.setInt(j++, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                reason = rs.getString("reason");
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
        return reason;
    }

    public String getPrintOpDate(String regn_no, String doc_id) {
        TransactionManager tmgr = null;
        String op_dt = null;
        try {
            tmgr = new TransactionManager("getPrintOpDate");
            String query = "SELECT to_char(op_dt,'DD-Mon-YYYY') as op_date from " + TableList.VH_PERMIT_PRINT + " where regn_no = ? and doc_id = ? order by printed_on DESC limit 1";
            PreparedStatement ps = tmgr.prepareStatement(query);
            int j = 1;
            ps.setString(j++, regn_no);
            ps.setString(j++, doc_id);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                op_dt = rs.getString("op_date");
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
        return op_dt;
    }

    public static byte[] getSignDetailsForPermit(String regnNo, String appl_no, String state_cd) {
        TransactionManager tmgr = null;
        byte[] reason = null;
        try {
            tmgr = new TransactionManager("getSignDetailsForPermit");
            String query = " select sign.doc_sign from va_details details \n"
                    + " inner join vha_status status on details.state_cd=status.state_cd and details.off_cd=status.off_cd and details.pur_cd=status.pur_cd and details.appl_no=status.appl_no \n"
                    + " inner join tm_action action on action.action_cd=status.action_cd \n"
                    + " inner join tm_user_sign sign on sign.user_cd =  status.emp_cd \n"
                    + " where details.appl_no = ? and details.regn_no = ? and action.role_cd=6 and details.state_cd=? and action.action_cd <> ?";
            PreparedStatement ps = tmgr.prepareStatement(query);
            ps.setString(1, appl_no);
            ps.setString(2, regnNo);
            ps.setString(3, state_cd);
            ps.setInt(4, TableConstants.TM_ROLE_PMT_APPLICATION_APPROVAL);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (rs.getBytes("doc_sign") != null && !rs.getBytes("doc_sign").equals("")) {
                    reason = rs.getBytes("doc_sign");
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
        return reason;
    }

    public List<PermitLeaseDobj> getLeasePermitDetails(String regn_no, String appl_no) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        RowSet rs;
        PermitLeaseDobj lDobj = null;
        List<PermitLeaseDobj> leaseList = null;
        try {
            tmgr = new TransactionManager("getLeasePermitDetails");
            String sql = " select l_owner_name,l_f_name,l_regn_no,to_char(lease_valid_from,'dd-Mon-yyyy') as valid_from,to_char(lease_valid_upto,'dd-Mon-yyyy') as valid_upto from permit.vt_lease_permit  \n"
                    + " where m_regn_no =? order by op_dt desc limit 1 ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            rs = tmgr.fetchDetachedRowSet();
            leaseList = new ArrayList<>();
            if (rs.next()) {
                lDobj = new PermitLeaseDobj();
                lDobj.setLeaseRegnNo(rs.getString("l_regn_no").toUpperCase());
                lDobj.setOwner_name(rs.getString("l_owner_name").toUpperCase());
                lDobj.setF_name(rs.getString("l_f_name"));
                lDobj.setValidFromInString(rs.getString("valid_from"));
                lDobj.setValidUptoInString(rs.getString("valid_upto"));
                leaseList.add(lDobj);
            }
            if (lDobj == null) {
                leaseList = null;
            }
        } catch (Exception e) {
            leaseList = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                leaseList = null;
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return leaseList;
    }

    public Map getNPHomeAuthFromNpPortal(String regn_no, String appl_no) {
        Map<String, String> authDetails = null;
        TransactionManager tmgr = null;
        RowSet rs;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("getNPHomeAuthFromNpPortal");
            String query;
            query = "select * from GET_NPAUTH_PRINTDETAILS(?)";
            if (appl_no != null) {
                query = "select * from GET_NPAUTH_PRINTDETAILS(?,?)";
            }
            ps = tmgr.prepareStatement(query);
            ps.setString(1, regn_no);
            if (appl_no != null) {
                ps.setString(2, appl_no);;
            }
            rs = tmgr.fetchDetachedRowSet();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
                authDetails = new LinkedHashMap<String, String>();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    authDetails.put(rsmd.getColumnName(i), rs.getString(i));
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
        return authDetails;
    }

    public String getOffCdFormStateConfig(String conditon) {
        String offCd = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getOffCdFormStateConfig");
            if (conditon != null && !conditon.isEmpty()) {
                String Query = "SELECT " + conditon + " as conditon";
                tmgr.prepareStatement(Query);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    offCd = rs.getString("conditon");
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
        return offCd;
    }

    public List<PermitTimeTableDobj> getPmtTimeTableData(String regn_no, String appl_no, String state_cd, int off_cd) throws SQLException {
        String query;
        PreparedStatement ps;
        TransactionManagerReadOnly tmgr = null;
        List<PermitTimeTableDobj> listTimeTable = new ArrayList<>();
        int tripNo = 0;
        query = "select route_cd,stoppage,journey_day,arrival_time,departure_time,trip from " + TableList.VT_PERMIT_ROUTE_TIME_TABLE + "  where regn_no=? and appl_no=? and state_cd=? and off_cd=?  order by sr_no";
        try {
            tmgr = new TransactionManagerReadOnly("getPmtTimeTable");
            ps = tmgr.prepareStatement(query);
            ps.setString(1, regn_no);
            ps.setString(2, appl_no);
            ps.setString(3, state_cd);
            ps.setInt(4, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                tripNo++;
                PermitTimeTableDobj dobj = new PermitTimeTableDobj();
                dobj.setRoute_cd(rs.getString("route_cd"));
                dobj.setStoppage(rs.getString("stoppage"));
                dobj.setDay(rs.getInt("journey_day"));
                dobj.setRoute_fr_time(rs.getString("arrival_time"));
                dobj.setRoute_to_time(rs.getString("departure_time"));
                dobj.setTrip_no(String.valueOf(rs.getInt("trip")));
                listTimeTable.add(dobj);

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
        return listTimeTable;
    }

    public List<PermitTimeTableDobj> getPmtTimeTableDataVa(String regn_no, String appl_no, String state_cd, int off_cd) throws SQLException {
        String query;
        PreparedStatement ps;
        TransactionManagerReadOnly tmgr = null;
        List<PermitTimeTableDobj> listTimeTable = new ArrayList<>();
        int tripNo = 0;
        query = "select route_cd,stoppage,journey_day,arrival_time,departure_time,trip from " + TableList.VA_PERMIT_ROUTE_TIME_TABLE + "  where regn_no=? and appl_no=? and state_cd=? and off_cd=?  order by sr_no";
        try {
            tmgr = new TransactionManagerReadOnly("getPmtTimeTableDataVa");
            ps = tmgr.prepareStatement(query);
            ps.setString(1, regn_no);
            ps.setString(2, appl_no);
            ps.setString(3, state_cd);
            ps.setInt(4, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                tripNo++;
                PermitTimeTableDobj dobj = new PermitTimeTableDobj();
                dobj.setRoute_cd(rs.getString("route_cd"));
                dobj.setStoppage(rs.getString("stoppage"));
                dobj.setDay(rs.getInt("journey_day"));
                dobj.setRoute_fr_time(rs.getString("arrival_time"));
                dobj.setRoute_to_time(rs.getString("departure_time"));
                dobj.setTrip_no(String.valueOf(rs.getInt("trip")));
                listTimeTable.add(dobj);

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
        return listTimeTable;
    }
}
