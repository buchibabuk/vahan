/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.permit;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.permit.PermitReservationDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author hcl
 */
public class PermitReservationImpl {

    private static final Logger LOGGER = Logger.getLogger(PermitReservationImpl.class);

    private PermitReservationImpl() {
    }

    public static List<PermitReservationDobj> getPermitReservationDtls(String state_cd, int pmt_type, int pmt_catg, int own_catg, int fuel_type, int region_covered, int off_cd, String route_cd) {
        List<PermitReservationDobj> resList = new ArrayList<>();
        TransactionManager tmgr = null;
        String Query;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManager("getPermitReservationDtls");
            Query = "SELECT b.descr as pmt_type_descr ,COALESCE(c.descr,'All Permit Category') as pmt_catg_descr ,COALESCE(d.descr,'All Owner Category') as owner_catg_descr ,a.running_no ,a.max_no from " + TableList.VM_PERMIT_RESERVATION + "  a\n"
                    + "left join " + TableList.VM_PERMIT_TYPE + "  b on a.pmt_type = b.code \n"
                    + "left join " + TableList.VM_PERMIT_CATG + "  c on a.state_cd = c.state_cd and a.pmt_type = c.permit_type and a.pmt_catg = c.code\n"
                    + "left join " + TableList.VM_OWCATG + "   d on d.owcatg_code = a.owner_catg \n"
                    + "where a.state_cd = ? and a.off_cd=? and a.pmt_type = ? and pmt_catg in (0,?) and owner_catg in (0,?) and ( 0::text = any(string_to_array(fuel,',')) or ?::text = any(string_to_array(fuel,','))) and region_covered in (0,?) and route_cd = ?";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, state_cd);
            ps.setInt(i++, off_cd);
            ps.setInt(i++, pmt_type);
            ps.setInt(i++, pmt_catg);
            ps.setInt(i++, own_catg);
            ps.setInt(i++, fuel_type);
            ps.setInt(i++, region_covered);
            ps.setString(i++, route_cd);
            RowSet rs = tmgr.fetchDetachedRowSetWithoutTrim_No_release();
            while (rs.next()) {
                PermitReservationDobj dobj = new PermitReservationDobj();
                dobj.setPmt_type_descr(rs.getString("pmt_type_descr"));
                dobj.setPmt_catg_descr(rs.getString("pmt_catg_descr"));
                dobj.setOwner_ctg_descr(rs.getString("owner_catg_descr"));
                dobj.setRunning_no(rs.getInt("running_no"));
                dobj.setMax_no(rs.getInt("max_no"));
                resList.add(dobj);
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
        return resList;
    }

    public static void AddRunningNoByOne(String state_cd, int off_cd, int pmt_type, int pmt_catg, int own_catg, int fuel_cd, int region_covered, String route_cd, TransactionManager tmgr) {
        String Query;
        PreparedStatement ps = null;
        try {
            Query = "SELECT * from " + TableList.VM_PERMIT_RESERVATION + "  where state_cd = ? and off_cd = ? and pmt_type = ? and pmt_catg = ? and owner_catg in (0,?) and region_covered in (0,?) and route_cd = ? and ( 0::text = any(string_to_array(fuel,',')) or ?::text = any(string_to_array(fuel,',')))";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, state_cd);
            ps.setInt(i++, off_cd);
            ps.setInt(i++, pmt_type);
            ps.setInt(i++, pmt_catg);
            ps.setInt(i++, own_catg);
            ps.setInt(i++, region_covered);
            ps.setString(i++, route_cd);
            ps.setInt(i, fuel_cd);
            RowSet rs = tmgr.fetchDetachedRowSetWithoutTrim_No_release();
            if (rs.next()) {
                Query = "UPDATE " + TableList.VM_PERMIT_RESERVATION + " SET running_no = running_no + 1  where state_cd = ? and off_cd = ? and pmt_type = ? and pmt_catg = ? and owner_catg in (0,?) and ? = region_covered and ? = route_cd and ?::text = fuel::text";
                ps = tmgr.prepareStatement(Query);
                i = 1;
                ps.setString(i++, state_cd);
                ps.setInt(i++, rs.getInt("off_cd"));
                ps.setInt(i++, pmt_type);
                ps.setInt(i++, pmt_catg);
                ps.setInt(i++, own_catg);
                ps.setInt(i++, rs.getInt("region_covered"));
                ps.setString(i++, rs.getString("route_cd"));
                ps.setString(i, rs.getString("fuel"));
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public static void minusRunningNoByOne(String appl_no, TransactionManager tmgr) {
        String Query;
        PreparedStatement ps = null;
        RowSet rs, rs1 = null;
        try {
            Query = "SELECT * from " + TableList.VA_PERMIT + " where appl_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, appl_no);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                Query = "SELECT * from " + TableList.VM_PERMIT_RESERVATION + "  where state_cd = ? and pmt_type = ? and pmt_catg = ? and owner_catg = ?";
                ps = tmgr.prepareStatement(Query);
                int i = 1;
                ps.setString(i++, rs.getString("state_cd"));
                ps.setInt(i++, rs.getInt("pmt_type"));
                ps.setInt(i++, rs.getInt("pmt_catg"));
                ps.setInt(i++, rs.getInt("own_catg"));
                rs1 = tmgr.fetchDetachedRowSetWithoutTrim_No_release();
                if (rs1.next()) {
                    Query = "UPDATE " + TableList.VM_PERMIT_RESERVATION + " SET running_no = running_no - 1  where state_cd = ? and pmt_type = ? and pmt_catg = ? and owner_catg = ?";
                    ps = tmgr.prepareStatement(Query);
                    i = 1;
                    ps.setString(i++, rs.getString("state_cd"));
                    ps.setInt(i++, rs.getInt("pmt_type"));
                    ps.setInt(i++, rs.getInt("pmt_catg"));
                    ps.setInt(i++, rs.getInt("own_catg"));
                    ps.executeUpdate();
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }
}
