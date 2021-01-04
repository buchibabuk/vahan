/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.rtoLaunchDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author nicsi
 */
public class rtoLaunchImpl {

    private static Logger LOGGER = Logger.getLogger(rtoLaunchImpl.class);

    public rtoLaunchDobj getAllRtoList(int launchRto) throws VahanException {
        rtoLaunchDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            String sql = "SELECT a.off_cd, a.off_name, a.off_add1, a.off_add2, a.pin_cd, a.village_cd, a.taluk_cd,\n"
                    + "a.dist_cd, a.mobile_no, a.landline, a.email_id, a.off_under_cd, a.off_type_cd, \n"
                    + "a.state_cd, a.vow4, b.smart_card,b.hsrp,b.paper_rc,b.old_veh_hsrp\n"
                    + "FROM tm_office a\n"
                    + "left outer join VM_SMART_CARD_HSRP b on a.state_cd=b.state_cd and a.off_cd=b.off_cd\n"
                    + "where a.state_cd=? and a.off_cd=? and vow4 is null;";

            tmgr = new TransactionManager("getAllRtoList");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, launchRto);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new rtoLaunchDobj();

                dobj.setOff_name(rs.getString("off_name"));
                dobj.setOff_add1(rs.getString("off_add1"));
                dobj.setOff_add2(rs.getString("off_add2"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setVow4_launch_date(rs.getDate("vow4"));
                dobj.setIsHsrp(rs.getBoolean("hsrp"));
                dobj.setIsSmartCard(rs.getBoolean("smart_card"));
                dobj.setRcType(rs.getString("paper_rc"));
                dobj.setIsOldHsrp(rs.getBoolean("old_veh_hsrp"));
                if (dobj.isIsHsrp()) {
                    dobj.setHsrp("YES");
                } else {
                    dobj.setHsrp("NO");
                }
                if (dobj.isIsOldHsrp()) {
                    dobj.setOld_veh_hsrp("YES");
                } else {
                    dobj.setOld_veh_hsrp("NO");
                }
                if (dobj.isIsSmartCard()) {
                    dobj.setRcOption("S");
                    dobj.setSmartCard("YES");
                } else if (TableConstants.VM_PLASTIC_CARD_RC_CD.equalsIgnoreCase(dobj.getRcType())) {
                    dobj.setPlasticCard("YES");
                    dobj.setRcOption("P");
                } else if (TableConstants.VM_PLASTIC_CARD_RC_PVC_CD.equalsIgnoreCase(dobj.getRcType())) {
                    dobj.setPlasticCardpvc("YES");
                    dobj.setRcOption("V");
                } else if (TableConstants.VM_PAPER_RC_CD.equalsIgnoreCase(dobj.getRcType())) {
                    dobj.setPaperRC("YES");
                    dobj.setRcOption("R");
                } else {
                    dobj.setSmartCard("NO");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Problem in getting the rto to be launched");

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

    public ArrayList<rtoLaunchDobj> getRunningRtoList() throws VahanException {
        rtoLaunchDobj dobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        ArrayList<rtoLaunchDobj> rto_list = new ArrayList();
        try {
            String sql = "SELECT a.off_cd, a.off_name, a.off_add1, a.off_add2, a.pin_cd, a.village_cd, a.taluk_cd, \n"
                    + " a.dist_cd, a.mobile_no, a.landline, a.email_id, a.off_under_cd, a.off_type_cd, \n"
                    + " a.state_cd, a.vow4,b.smart_card,b.hsrp,b.paper_rc,b.old_veh_hsrp\n"
                    + " FROM tm_office a\n"
                    + " left outer join VM_SMART_CARD_HSRP b on a.state_cd=b.state_cd and a.off_cd=b.off_cd\n"
                    + " where a.state_cd=? and vow4 is not null order by off_cd;";
            tmgr = new TransactionManager("getRunningRtoList");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new rtoLaunchDobj();
                dobj.setOff_name(rs.getString("off_name"));
                dobj.setOff_add1(rs.getString("off_add1"));
                dobj.setOff_add2(rs.getString("off_add2"));
                dobj.setState_cd(rs.getString("state_cd") + "-" + rs.getInt("off_cd"));
                dobj.setVow4_launch_date(rs.getDate("vow4"));
                dobj.setIsHsrp(rs.getBoolean("hsrp"));
                dobj.setIsSmartCard(rs.getBoolean("smart_card"));
                dobj.setRcType(rs.getString("paper_rc"));
                dobj.setIsOldHsrp(rs.getBoolean("old_veh_hsrp"));
                if (dobj.isIsHsrp()) {
                    dobj.setHsrp("YES");
                } else {
                    dobj.setHsrp("NO");
                }
                if (dobj.isIsOldHsrp()) {
                    dobj.setOld_veh_hsrp("YES");
                } else {
                    dobj.setOld_veh_hsrp("NO");
                }

                if (dobj.isIsSmartCard()) {
                    dobj.setSmartCard("YES");
                } else if (TableConstants.VM_PLASTIC_CARD_RC_CD.equalsIgnoreCase(dobj.getRcType())) {
                    dobj.setPlasticCard("YES");
                } else if (TableConstants.VM_PLASTIC_CARD_RC_PVC_CD.equalsIgnoreCase(dobj.getRcType())) {
                    dobj.setPlasticCardpvc("YES");
                } else if (TableConstants.VM_PAPER_RC_CD.equalsIgnoreCase(dobj.getRcType())) {
                    dobj.setPaperRC("YES");
                } else {
                    dobj.setSmartCard("NO");
                }
                rto_list.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Problem in getting the running rto");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return rto_list;

    }

    public boolean saveRtoToBeLaunched(rtoLaunchDobj dobj) throws VahanException, SQLException {
        boolean flag = false;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        int i = 0;
        try {

            String sql = "UPDATE tm_office\n"
                    + "   SET  off_add1=?, off_add2=?, vow4=?\n"
                    + " WHERE state_cd= ? and off_cd= ?";
            tmgr = new TransactionManager("saveRtoToBeLaunched");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getOff_add1());
            ps.setString(2, dobj.getOff_add2());
            ps.setDate(3, new java.sql.Date(dobj.getVow4_launch_date().getTime()));
            ps.setString(4, Util.getUserStateCode());
            ps.setInt(5, dobj.getOff_cd());
            ps.executeUpdate();
            saveOrUpdateHsrpAndSmartCard(dobj, tmgr);
            tmgr.commit();
            flag = true;

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("there is some problem in Saving Launch Details  !!!!!");
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

    public void saveOrUpdateHsrpAndSmartCard(rtoLaunchDobj dobj, TransactionManager tmgr) throws VahanException {
        String rcDescForInsert = "";
        String rcDescForUpdate = "";
        String rcValue = "";
        PreparedStatement ps = null;
        try {
            if ("S".equalsIgnoreCase(dobj.getRcOption())) {
                rcDescForInsert = "smart_card";
                rcDescForUpdate = "smart_card=?";
                rcValue = "true";
            }
            if ("R".equalsIgnoreCase(dobj.getRcOption())) {
                rcDescForInsert = "paper_rc";
                rcDescForUpdate = "paper_rc=?";
                rcValue = TableConstants.VM_PAPER_RC_CD;
            }
            if ("P".equalsIgnoreCase(dobj.getRcOption())) {
                rcDescForInsert = "paper_rc";
                rcDescForUpdate = "paper_rc=?";
                rcValue = TableConstants.VM_PLASTIC_CARD_RC_CD;
            }
            if ("V".equalsIgnoreCase(dobj.getRcOption())) {
                rcDescForInsert = "paper_rc";
                rcDescForUpdate = "paper_rc=?";
                rcValue = TableConstants.VM_PLASTIC_CARD_RC_PVC_CD;
            }
            String sql = "SELECT * from vm_smart_card_hsrp where state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, dobj.getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {

                updateHsrpAndSmartCard(dobj, tmgr, rcDescForUpdate, rcValue);

            } else {
                saveHsrpAndSmartCard(dobj, tmgr, rcDescForInsert, rcValue);
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("there is some problem in Saving Details Of SmartCard And Hsrp !!!!!");
        }


    }

    public void saveHsrpAndSmartCard(rtoLaunchDobj dobj, TransactionManager tmgr, String rcDescForInsert, String rcValue) throws VahanException {
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO vm_smart_card_hsrp(\n"
                    + "            state_cd, off_cd, " + rcDescForInsert + " , hsrp, day_begin, cash_counter_closed, \n"
                    + "            last_working_day, \n"
                    + "            old_veh_hsrp)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, ? )";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, dobj.getOff_cd());
            if ("S".equalsIgnoreCase(dobj.getRcOption())) {
                ps.setBoolean(3, Boolean.valueOf(rcValue));
            } else {
                ps.setString(3, rcValue);
            }

            ps.setBoolean(4, dobj.isIsHsrp());
            ps.setDate(5, new java.sql.Date(dobj.getVow4_launch_date().getTime()));
            ps.setBoolean(6, true);
            ps.setDate(7, new java.sql.Date(dobj.getVow4_launch_date().getTime()));
            ps.setBoolean(8, dobj.isIsOldHsrp());
            ps.execute();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("there is some problem in Saving Details !!!!!");
        }

    }

    public void updateHsrpAndSmartCard(rtoLaunchDobj dobj, TransactionManager tmgr, String rcDescForUpdate, String rcValue) throws VahanException {

        PreparedStatement ps = null;
        try {
            String sql = "";
            sql = "UPDATE vm_smart_card_hsrp\n"
                    + "   SET smart_card =? WHERE state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setBoolean(1, false);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, dobj.getOff_cd());
            ps.executeUpdate();

            sql = "UPDATE vm_smart_card_hsrp\n"
                    + "   SET  " + rcDescForUpdate + ", hsrp=?, day_begin=?,last_working_day=?, old_veh_hsrp=?  WHERE state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            if ("S".equalsIgnoreCase(dobj.getRcOption())) {
                ps.setBoolean(1, Boolean.valueOf(rcValue));
            } else {
                ps.setString(1, rcValue);
            }
            ps.setBoolean(2, dobj.isIsHsrp());
            ps.setDate(3, new java.sql.Date(dobj.getVow4_launch_date().getTime()));
            ps.setDate(4, new java.sql.Date(dobj.getVow4_launch_date().getTime()));
            ps.setBoolean(5, dobj.isIsOldHsrp());
            ps.setString(6, Util.getUserStateCode());
            ps.setInt(7, dobj.getOff_cd());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("there is some problem in Updating Details !!!!!");
        }
    }

    public Map<String, Object> getRtosForLaunch() throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        Map<String, Object> launchRtoList = new LinkedHashMap<String, Object>();
        launchRtoList.put("Please Select Rto", "-1");
        try {
            tmgr = new TransactionManager("getRtosForLaunch");
            String sql = "select off_cd, off_name from " + TableList.TM_OFFICE + "   WHERE state_cd=? and  vow4 is null order By off_name";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            RowSet rowSet = tmgr.fetchDetachedRowSet();
            while (rowSet.next()) {
                launchRtoList.put(rowSet.getString("off_name"), rowSet.getString("off_cd"));
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
        return launchRtoList;
    }
}
