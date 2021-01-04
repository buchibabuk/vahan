/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import javax.sql.RowSet;
import nic.java.util.CommonUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.bean.permit.PermitRouteList;
import nic.vahan.form.dobj.permit.TemporaryPermitAdminDobj;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author MukulRaiDutta
 */
public class TemporaryPermitAdminImpl {

    private static final Logger LOGGER = Logger.getLogger(TemporaryPermitAdminImpl.class);

    public TemporaryPermitAdminDobj getPmtDtlsThroughVTTEMPPERMIT(String value, Boolean route_Status, int off_cd) {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        TemporaryPermitAdminDobj tpDobj = null;
        try {
            tmgr = new TransactionManager("getPmtDtlsThroughVTTEMPPERMIT");
            String Query = "SELECT off_cd,appl_no,pmt_no,regn_no,issue_dt,valid_from,valid_upto,rcpt_no,pur_cd,pmt_type,pmt_catg,reason,route_fr,route_to,op_dt,via,goods_to_carry FROM " + TableList.VT_TEMP_PERMIT + " where state_cd=? and (regn_no = ? OR pmt_no= ?) and pur_cd in (?,?) order by op_dt desc limit 1";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, value);
            ps.setString(3, value);
            ps.setInt(4, TableConstants.VM_PMT_TEMP_PUR_CD);
            ps.setInt(5, TableConstants.VM_PMT_RENEW_TEMP_PUR_CD);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                tpDobj = new TemporaryPermitAdminDobj();
                tpDobj.setOff_cd(rs.getInt("off_cd"));
                tpDobj.setAppl_no(rs.getString("appl_no"));
                tpDobj = route_Status ? getRouteThroughVtTempPermitRoute(tpDobj, tmgr, off_cd) : tpDobj;
                tpDobj.setPmt_no(rs.getString("pmt_no"));
                tpDobj.setRegn_no(rs.getString("regn_no"));
                tpDobj.setIssue_dt(rs.getDate("issue_dt"));
                tpDobj.setValid_from(rs.getDate("valid_from"));
                tpDobj.setValid_upto(rs.getDate("valid_upto"));
                tpDobj.setRcpt_no(rs.getString("rcpt_no"));
                tpDobj.setPur_cd(rs.getInt("pur_cd"));
                tpDobj.setPmt_type(rs.getInt("pmt_type"));
                tpDobj.setPmt_catg(rs.getInt("pmt_catg"));
                tpDobj.setReason(rs.getString("reason"));
                tpDobj.setRoute_fr(rs.getString("route_fr"));
                tpDobj.setRoute_to(rs.getString("route_to"));
                tpDobj.setVt_temp_Route_via(rs.getString("via"));
                tpDobj.setOp_dt(rs.getDate("op_dt"));
                if (!CommonUtils.isNullOrBlank("goods_to_carry")) {
                    tpDobj.setGoods_to_carry(rs.getString("goods_to_carry"));
                } else {
                    tpDobj.setGoods_to_carry("");
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
        return tpDobj;
    }

    public TemporaryPermitAdminDobj getRouteThroughVtTempPermitRoute(TemporaryPermitAdminDobj tpDobj, TransactionManager tmgr, int issue_off_cd) throws SQLException {
        PreparedStatement ps;
        String regionCovered = "";
        List selectCode = new ArrayList<>();
        List selectdesc = new ArrayList<>();
        List unselectedCode = new ArrayList<>();
        List unselecteddesc = new ArrayList<>();
        String query = "select tempRoute.region_covered,\n"
                + "case when tempRoute.route_cd = routeMaster.code and tempRoute.state_cd = routeMaster.state_cd and tempRoute.off_cd = routeMaster.off_cd  then routeMaster.code end selectedRouteCode,\n"
                + "case when tempRoute.route_cd = routeMaster.code and tempRoute.state_cd = routeMaster.state_cd and tempRoute.off_cd = routeMaster.off_cd then routeMaster.floc||' - '||routeMaster.tloc end selectedRouteDesc,\n"
                + "case when tempRoute.route_cd = routeMaster.code and tempRoute.state_cd = routeMaster.state_cd and tempRoute.off_cd = routeMaster.off_cd then routeMaster.code||' : '||routeMaster.via end selectedRoutevia,\n"
                + "case when tempRoute.route_cd != routeMaster.code and tempRoute.state_cd = routeMaster.state_cd and tempRoute.off_cd = routeMaster.off_cd then routeMaster.code end unselectedRouteCode,\n"
                + "case when tempRoute.route_cd != routeMaster.code and tempRoute.state_cd = routeMaster.state_cd and tempRoute.off_cd = routeMaster.off_cd then routeMaster.floc||' - '||routeMaster.tloc end unselectedRouteDesc\n"
                + "from permit.vt_temp_permit_route tempRoute\n"
                + "left outer join permit.vm_route_master routeMaster \n"
                + "on routeMaster.state_cd = tempRoute.state_cd and  routeMaster.off_cd=tempRoute.off_cd\n"
                + "where tempRoute.state_cd = ? and tempRoute.off_cd=? and tempRoute.appl_no = ?";
        ps = tmgr.prepareStatement(query);
        int i = 1;
        ps.setString(i++, Util.getUserStateCode());
        ps.setInt(i++, issue_off_cd);
        ps.setString(i, tpDobj.getAppl_no());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        while (rs.next()) {
            if (!CommonUtils.isNullOrBlank(rs.getString(1))) {
                if (!regionCovered.contains(rs.getString(1))) {
                    regionCovered = rs.getString(1);
                }
            }
            if (!CommonUtils.isNullOrBlank(rs.getString(2))) {
                tpDobj.getRouteActionTarget().add(new PermitRouteList(rs.getString(2), rs.getString(3)));
                tpDobj.setVia(tpDobj.getVia() + "</br>" + rs.getString(4));
                selectCode.add(rs.getString(2));
                selectdesc.add(rs.getString(3));
            }
            if (!CommonUtils.isNullOrBlank(rs.getString(5))) {
                unselectedCode.add(rs.getString(5));
                unselecteddesc.add(rs.getString(6));
            }
        }

        LinkedHashSet linked = new LinkedHashSet();
        for (int j = 0; j < unselectedCode.size(); j++) {
            if (!selectCode.contains(unselectedCode.get(j).toString()) && !linked.contains(unselectedCode.get(j).toString())) {
                tpDobj.getRouteActionSource().add(new PermitRouteList(unselectedCode.get(j).toString(), unselecteddesc.get(j).toString()));
                linked.add(unselectedCode.get(j).toString());
            }
        }
        tpDobj = getRegionThroughVM_Region(regionCovered, tpDobj, tmgr);
        return tpDobj;
    }

    public boolean checkRegn_Pmt_Appl_NoValidOrNot(String regn_no, String pmt_no, String appl_no) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        String Query = "";
        boolean value = false;
        try {
            tmgr = new TransactionManager("checkRegn_Pmt_NoValidOrNot");
            Query = "SELECT count(*) as count from " + TableList.VT_TEMP_PERMIT + " where ";
            if (!CommonUtils.isNullOrBlank(regn_no)) {
                Query = Query + " regn_no=? ";
            } else if (!CommonUtils.isNullOrBlank(pmt_no)) {
                Query = Query + " pmt_no=? ";
            } else if (!CommonUtils.isNullOrBlank(appl_no)) {
                Query = Query + " appl_no=? ";
            }
            ps = tmgr.prepareStatement(Query);
            if (!CommonUtils.isNullOrBlank(regn_no)) {
                ps.setString(1, regn_no);
            } else if (!CommonUtils.isNullOrBlank(pmt_no)) {
                ps.setString(1, pmt_no);
            } else if (!CommonUtils.isNullOrBlank(appl_no)) {
                ps.setString(1, appl_no);
            }
            if (!CommonUtils.isNullOrBlank(Query)) {
                RowSet rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    int count = rs.getInt("count");
                    if (count >= 1) {
                        value = true;
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
        return value;
    }

    public TemporaryPermitAdminDobj getRegionThroughVM_Region(String region_covered, TemporaryPermitAdminDobj tpDobj, TransactionManager tmgr) throws SQLException {
        PreparedStatement ps;
        String query = "select \n"
                + "case when a.region_cd :: text = ANY(string_to_array(?,',')) \n"
                + "then a.region_cd::text \n"
                + "end selectedregioncod ,\n"
                + "case when a.region_cd :: text = ANY(string_to_array(?,',')) \n"
                + "then a.region\n"
                + "end selectedregiondesc ,\n"
                + "case when a.region_cd :: text = ANY(string_to_array(?,',')) \n"
                + "then \n"
                + "null\n"
                + "else\n"
                + "a.region_cd::text \n"
                + "end unSelectedregioncod,\n"
                + "case when a.region_cd :: text = ANY(string_to_array(?,',')) \n"
                + "then \n"
                + "null\n"
                + "else\n"
                + "a.region\n"
                + "end unSelectedregiondesc\n"
                + "from permit.vm_region a where a.state_cd =? and a.off_cd=?";
        ps = tmgr.prepareStatement(query);
        int i = 1;
        while (i <= 4) {
            ps.setString(i, region_covered);
            i++;
        }
        ps.setString(i++, Util.getUserStateCode());
        ps.setInt(i, Util.getUserOffCode());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        while (rs.next()) {
            if (!CommonUtils.isNullOrBlank(rs.getString(1))) {
                tpDobj.getRegionActionTarget().add(new PermitRouteList(rs.getString(1), rs.getString(2)));
            }
            if (!CommonUtils.isNullOrBlank(rs.getString(3))) {
                tpDobj.getRegionActionSource().add(new PermitRouteList(rs.getString(3), rs.getString(4)));
            }
        }
        return tpDobj;
    }

    public void deleteRouteDetailsInVtTables(String appl_no, TransactionManager tmgr) throws SQLException {
        String Query;
        PreparedStatement ps;
        Query = "SELECT * FROM " + TableList.VT_TEMP_PERMIT_ROUTE + " WHERE appl_no = ?";
        ps = tmgr.prepareStatement(Query);
        ps.setString(1, appl_no);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            Query = "DELETE FROM " + TableList.VT_TEMP_PERMIT + " WHERE appl_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
        }
    }

    public boolean saveDetailsInVtTables(TemporaryPermitAdminDobj dobj, TemporaryPermitAdminDobj prvDobj, List route_code, List<TemporaryPermitAdminDobj> compairValue) throws VahanException {
        boolean flag = false;
        PreparedStatement ps;
        TransactionManager tmgr = null;
        String Query;
        int pur_cd = 0;
        String pmt_no = dobj.getPmt_no();
        try {
            tmgr = new TransactionManager("SaveDetailsInVtTables");
            if (!CommonUtils.isNullOrBlank(prvDobj.getAppl_no())
                    && !CommonUtils.isNullOrBlank(prvDobj.getRegn_no())
                    && !CommonUtils.isNullOrBlank(pmt_no)) {
                Query = "SELECT * FROM " + TableList.VT_TEMP_PERMIT + " WHERE PMT_NO = ?";
                ps = tmgr.prepareStatement(Query);
                ps.setString(1, pmt_no);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    pur_cd = rs.getInt("pur_cd");
                    Query = "INSERT INTO " + TableList.VH_TEMP_PERMIT + "(\n"
                            + "            state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                            + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, reason, route_fr, route_to, op_dt, via, goods_to_carry,\n"
                            + "            pmt_status, pmt_reason, moved_on, moved_by)\n"
                            + "    SELECT  state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                            + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg, reason, route_fr, route_to, op_dt, via, goods_to_carry, \n"
                            + "            ?,?,CURRENT_TIMESTAMP,?\n"
                            + "  FROM " + TableList.VT_TEMP_PERMIT + " where pmt_no = ? ORDER BY permit.vt_temp_permit.valid_upto LIMIT 1";
                    ps = tmgr.prepareStatement(Query);
                    int i = 1;
                    ps.setString(i++, "RAT");
                    ps.setString(i++, "Permit Ratification");
                    ps.setString(i++, Util.getEmpCode());
                    ps.setString(i++, pmt_no);
                    ps.executeUpdate();

                    Query = "DELETE FROM " + TableList.VT_TEMP_PERMIT + " where pmt_no = ?";
                    ps = tmgr.prepareStatement(Query);
                    ps.setString(1, pmt_no);
                    ps.executeUpdate();
                    deleteRouteDetailsInVtTables(dobj.getAppl_no(), tmgr);
                }
            }
            Query = "INSERT INTO " + TableList.VT_TEMP_PERMIT + "(\n"
                    + "            state_cd, off_cd, appl_no, pmt_no, regn_no, issue_dt, valid_from, \n"
                    + "            valid_upto, rcpt_no, pur_cd, pmt_type, pmt_catg,reason, \n"
                    + "            route_fr, route_to, op_dt, via, goods_to_carry)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, ?, ?, ?, ?,\n"
                    + "            ?, ?, CURRENT_TIMESTAMP, ?, ? )";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, dobj.getOff_cd());
            ps.setString(i++, dobj.getAppl_no().toUpperCase());
            ps.setString(i++, dobj.getPmt_no().toUpperCase());
            ps.setString(i++, dobj.getRegn_no().toUpperCase());
            ps.setDate(i++, new java.sql.Date(dobj.getIssue_dt().getTime()));
            ps.setDate(i++, new java.sql.Date(dobj.getValid_from().getTime()));
            ps.setDate(i++, new java.sql.Date(dobj.getValid_upto().getTime()));
            ps.setString(i++, dobj.getRcpt_no().toUpperCase());
            if (pur_cd == 0) {
                ps.setInt(i++, dobj.getPur_cd());
            } else {
                ps.setInt(i++, pur_cd);
            }
            ps.setInt(i++, dobj.getPmt_type());
            ps.setInt(i++, dobj.getPmt_catg());
            ps.setString(i++, dobj.getReason().toUpperCase());
            ps.setString(i++, dobj.getRoute_fr().toUpperCase());
            ps.setString(i++, dobj.getRoute_to().toUpperCase());
            ps.setString(i++, dobj.getVt_temp_Route_via().toUpperCase());
            ps.setString(i++, dobj.getGoods_to_carry().toUpperCase());
            ps.executeUpdate();
            insertRouteDetailsInVtTables(dobj, prvDobj, route_code, tmgr);
            if (pur_cd == 0) {
                pur_cd = TableConstants.VM_PMT_TEMP_PUR_CD;
            }
            String docId = CommonPermitPrintImpl.getPermitDocIdForQuery(CommonPermitPrintImpl.getPermitDocId(tmgr, 0, pur_cd, Util.getUserStateCode()));
            String[] beanData = {docId, dobj.getAppl_no().toUpperCase(), dobj.getRegn_no().toUpperCase()};
            CommonPermitPrintImpl.insertVaPermitPrint(tmgr, beanData);
            flag = true;
            tmgr.commit();
        } catch (VahanException e) {
            flag = false;
            throw e;
        } catch (Exception e) {
            flag = false;
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                flag = false;
            }
        }
        return flag;
    }

    public void insertRouteDetailsInVtTables(TemporaryPermitAdminDobj dobj, TemporaryPermitAdminDobj prvdobj, List route_code, TransactionManager tmgr) throws SQLException {
        String query;
        PreparedStatement ps;
        if (!route_code.isEmpty()) {
            query = "DELETE FROM permit.vt_temp_permit_route WHERE appl_no = ?";
            ps = tmgr.prepareStatement(query);
            if (dobj.getAppl_no().equalsIgnoreCase(prvdobj.getAppl_no())) {
                ps.setString(1, dobj.getAppl_no());
            } else {
                ps.setString(1, prvdobj.getAppl_no());
            }
            ps.executeUpdate();
            for (Object s : route_code) {
                query = "INSERT INTO " + TableList.VT_TEMP_PERMIT_ROUTE + "(\n"
                        + "            state_cd, off_cd, appl_no, route_cd,region_covered,no_of_trips)\n"
                        + "    VALUES (?, ?, ?, ?, ?, ?); ";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, dobj.getOff_cd());
                ps.setString(3, dobj.getAppl_no());
                ps.setString(4, (String) s);
                if (!CommonUtils.isNullOrBlank(dobj.getRegion_covered())) {
                    ps.setString(5, dobj.getRegion_covered());
                    dobj.setRegion_covered("");
                } else {
                    ps.setString(5, null);
                }
                ps.setInt(6, dobj.getNo_of_trips());
                ps.executeUpdate();
            }
        } else {
            query = "DELETE FROM permit.vt_temp_permit_route WHERE appl_no = ?";
            ps = tmgr.prepareStatement(query);
            if (dobj.getAppl_no().equalsIgnoreCase(prvdobj.getAppl_no())) {
                ps.setString(1, dobj.getAppl_no());
            } else {
                ps.setString(1, prvdobj.getAppl_no());
            }
            ps.executeUpdate();
            query = "INSERT INTO " + TableList.VT_TEMP_PERMIT_ROUTE + "(\n"
                    + "            state_cd, off_cd, appl_no, route_cd,region_covered,no_of_trips)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?); ";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, dobj.getOff_cd());
            ps.setString(3, dobj.getAppl_no());
            ps.setString(4, "");
            if (!CommonUtils.isNullOrBlank(dobj.getRegion_covered())) {
                ps.setString(5, dobj.getRegion_covered());
            } else {
                ps.setString(5, null);
            }
            ps.setInt(6, dobj.getNo_of_trips());
            ps.executeUpdate();
        }
    }
}
