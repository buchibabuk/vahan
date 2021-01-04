/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.db.user_mgmt.dobj.OfficeModificationDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author acer
 */
public class OfficeModificationImpl {

    private static final Logger LOGGER = Logger.getLogger(OfficeModificationImpl.class);

    public static List<OfficeModificationDobj> getAllOfficeListInState(String stateCd) throws VahanException {
        List<OfficeModificationDobj> listOffice = new ArrayList<OfficeModificationDobj>();
        String sql = "select a.off_cd ,a.state_cd ,a.off_name ,a.off_add1 ,a.off_add2,a.pin_cd,a.village_cd,a.taluk_cd ,a.dist_cd ,a.mobile_no ,a.landline ,a.email_id ,a.off_under_cd ,a.off_type_cd \n"
                + ",a.off_name || '-' || a.state_cd || a.off_cd as statecdWithOffcd,a.vow4,b.smart_card,b.hsrp,b.old_veh_hsrp,b.paper_rc from " + TableList.TM_OFFICE + " a "
                + " LEFT OUTER JOIN " + TableList.VM_SMART_CARD_HSRP + " b ON b.state_cd=a.state_cd and b.off_cd=a.off_cd"
                + " where a.state_cd=?";;
        TransactionManager tmgr = null;
        OfficeModificationDobj dobj = null;
        PreparedStatement ps = null;
        Exception e = null;
        try {
            tmgr = new TransactionManager("getUserOfficeList");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new OfficeModificationDobj();
                if (!CommonUtils.isNullOrBlank(rs.getString("off_cd"))) {
                    dobj.setOff_cd(Integer.parseInt(rs.getString("off_cd")));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("state_cd"))) {
                    dobj.setState_cd(rs.getString("state_cd").toUpperCase());
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("off_name"))) {
                    dobj.setOff_name(rs.getString("off_name").toUpperCase());
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("off_add1"))) {
                    dobj.setOff_add1(rs.getString("off_add1").toUpperCase());
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("off_add2"))) {
                    dobj.setOff_add2(rs.getString("off_add2").toUpperCase());
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("pin_cd"))) {
                    dobj.setPinCode(rs.getString("pin_cd"));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("village_cd"))) {
                    dobj.setVillage_Cd(Integer.parseInt(rs.getString("village_cd")));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("taluk_cd"))) {
                    dobj.setTaluk_Cd(Integer.parseInt(rs.getString("taluk_cd")));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("dist_cd"))) {
                    dobj.setDistrict_Cd(Integer.parseInt(rs.getString("dist_cd")));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("mobile_no"))) {
                    dobj.setMobileNo(rs.getString("mobile_no").toUpperCase());
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("landline"))) {
                    dobj.setLandLine_No(rs.getString("landline").toUpperCase());
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("email_id"))) {
                    dobj.setEmail_Id(rs.getString("email_id").toUpperCase());
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("off_under_cd"))) {
                    dobj.setOff_under_cd(Integer.parseInt(rs.getString("off_under_cd")));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("off_type_cd"))) {
                    dobj.setOff_type_cd(Integer.parseInt(rs.getString("off_type_cd")));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("vow4"))) {
                    dobj.setVow4_dt(JSFUtils.getStringToDateyyyyMMdd(rs.getString("vow4")));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("statecdWithOffcd"))) {
                    dobj.setStatecdWithOffcd(rs.getString("statecdWithOffcd").toUpperCase());
                }
                dobj.setSmartcardFlag(rs.getBoolean("smart_card"));
                dobj.setHsrpFlag(rs.getBoolean("hsrp"));
                dobj.setOldHsrp(rs.getBoolean("old_veh_hsrp"));
                dobj.setPaperRc(rs.getString("paper_rc").toUpperCase());
                if (rs.getBoolean("smart_card")) {
                    dobj.setRcOption("S");
                } else {
                    if (dobj.getPaperRc().equalsIgnoreCase(TableConstants.VM_PAPER_RC_CD)) {
                        dobj.setRcOption("R");
                    } else if (dobj.getPaperRc().equalsIgnoreCase(TableConstants.VM_PLASTIC_CARD_RC_CD)) {
                        dobj.setRcOption("P");
                    } else if (dobj.getPaperRc().equalsIgnoreCase(TableConstants.VM_PLASTIC_CARD_RC_PVC_CD)) {
                        dobj.setRcOption("V");
                    }
                }
                listOffice.add(dobj);
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
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        if (e != null) {
            throw new VahanException("Error in fetching the Record for Modifing the Office details !!!");
        }
        return listOffice;
    }

    public static String insertOfficeRecords(OfficeModificationDobj dobj) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String msg = null;
        Exception e = null;
        String rcValue = "";
        try {
            tmgr = new TransactionManager("insertDataintoDataTable");
            String sql = " INSERT INTO " + TableList.THM_OFFICE + "  "
                    + " SELECT *, current_timestamp,? as moved_by FROM " + TableList.TM_OFFICE + " where state_cd=? and off_cd=? ";
            int n = 1;
            ps = tmgr.prepareStatement(sql);
            ps.setString(n++, Util.getEmpCode());
            ps.setString(n++, Util.getUserStateCode());
            ps.setInt(n++, dobj.getOff_cd());
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
            sql = " UPDATE  " + TableList.TM_OFFICE + " SET off_name= ?, off_add1= ?, off_add2= ?, pin_cd= ?,"
                    + " dist_cd= ?, mobile_no= ?, landline= ?, email_id= ?, off_under_cd= ?, "
                    + " off_type_cd= ?  WHERE state_cd= ? and off_cd= ?";
            int k = 1;
            ps = tmgr.prepareStatement(sql);
            ps.setString(k++, dobj.getOff_name());
            ps.setString(k++, dobj.getOff_add1());
            ps.setString(k++, dobj.getOff_add2());
            ps.setString(k++, dobj.getPinCode());
            ps.setInt(k++, dobj.getDistrict_Cd());
            ps.setString(k++, dobj.getMobileNo());
            ps.setString(k++, dobj.getLandLine_No());
            ps.setString(k++, dobj.getEmail_Id());
            ps.setInt(k++, dobj.getOff_under_cd());
            ps.setInt(k++, dobj.getOff_type_cd());
            ps.setString(k++, Util.getUserStateCode());
            ps.setInt(k++, dobj.getOff_cd()); //Sanjay Sir
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
            sql = " INSERT INTO " + TableList.VHM_SMART_CARD_HSRP + "  "
                    + " SELECT state_cd, off_cd, smart_card, hsrp, old_veh_hsrp, paper_rc, current_timestamp,? as moved_by "
                    + "  FROM " + TableList.VM_SMART_CARD_HSRP + " where state_cd=? and off_cd=? ";
            int m = 1;
            ps = tmgr.prepareStatement(sql);
            ps.setString(m++, Util.getEmpCode());
            ps.setString(m++, Util.getUserStateCode());
            ps.setInt(m++, dobj.getOff_cd());
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
            if ("R".equalsIgnoreCase(dobj.getRcOption())) {
                rcValue = TableConstants.VM_PAPER_RC_CD;
            }
            if ("P".equalsIgnoreCase(dobj.getRcOption())) {
                rcValue = TableConstants.VM_PLASTIC_CARD_RC_CD;
            }
            if ("V".equalsIgnoreCase(dobj.getRcOption())) {
                rcValue = TableConstants.VM_PLASTIC_CARD_RC_PVC_CD;
            }
            if ("S".equalsIgnoreCase(dobj.getRcOption())) {
                dobj.setSmartcardFlag(true);
                rcValue = TableConstants.VM_PAPER_RC_CD;
            } else {
                dobj.setSmartcardFlag(false);
            }
            sql = "UPDATE  " + TableList.VM_SMART_CARD_HSRP + " SET smart_card=?, paper_rc=?  , hsrp=? , old_veh_hsrp=? WHERE state_cd= ? and off_cd= ?";
            int l = 1;
            ps = tmgr.prepareStatement(sql);
            ps.setBoolean(l++, dobj.isSmartcardFlag());
            ps.setString(l++, rcValue);
            ps.setBoolean(l++, dobj.isHsrpFlag());
            ps.setBoolean(l++, dobj.isOldHsrp());
            ps.setString(l++, Util.getUserStateCode());
            ps.setInt(l++, dobj.getOff_cd());
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
            msg = "Office Modify Sucessfully";
            tmgr.commit();
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
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        if (e != null) {
            throw new VahanException("Error in Modifing the Office details !!!");
        }
        return msg;
    }

    public static OfficeModificationDobj getOfficeDetails(OfficeModificationDobj offModfyDOBJ) throws VahanException {
        List<OfficeModificationDobj> allOfficesList = null;
        OfficeModificationDobj obj = null;
        String sql = "select a.off_cd ,a.state_cd ,a.off_name ,a.off_add1 ,a.off_add2,a.pin_cd,a.village_cd,a.taluk_cd ,a.dist_cd ,a.mobile_no ,a.landline ,a.email_id ,a.off_under_cd ,a.off_type_cd \n"
                + ",a.off_name || '-' || a.state_cd || a.off_cd as statecdWithOffcd,a.vow4,b.smart_card,b.hsrp,b.old_veh_hsrp,b.paper_rc from " + TableList.TM_OFFICE + " a "
                + " LEFT OUTER JOIN " + TableList.VM_SMART_CARD_HSRP + " b ON b.state_cd=a.state_cd and b.off_cd=a.off_cd"
                + " where a.state_cd=? and a.off_cd=?";
        TransactionManagerReadOnly tmgr = null;
        try {
            allOfficesList = new ArrayList<OfficeModificationDobj>();
            tmgr = new TransactionManagerReadOnly("getSmartcardHsrpDetails");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, offModfyDOBJ.getState_cd());
            ps.setInt(2, offModfyDOBJ.getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                obj = new OfficeModificationDobj();
                if (!CommonUtils.isNullOrBlank(rs.getString("off_cd"))) {
                    obj.setOff_cd(Integer.parseInt(rs.getString("off_cd")));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("state_cd"))) {
                    obj.setState_cd(rs.getString("state_cd").toUpperCase());
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("off_name"))) {
                    obj.setOff_name(rs.getString("off_name").toUpperCase());
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("off_add1"))) {
                    obj.setOff_add1(rs.getString("off_add1").toUpperCase());
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("off_add2"))) {
                    obj.setOff_add2(rs.getString("off_add2").toUpperCase());
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("pin_cd"))) {
                    obj.setPinCode(rs.getString("pin_cd"));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("village_cd"))) {
                    obj.setVillage_Cd(Integer.parseInt(rs.getString("village_cd")));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("taluk_cd"))) {
                    obj.setTaluk_Cd(Integer.parseInt(rs.getString("taluk_cd")));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("dist_cd"))) {
                    obj.setDistrict_Cd(Integer.parseInt(rs.getString("dist_cd")));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("mobile_no"))) {
                    obj.setMobileNo(rs.getString("mobile_no"));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("landline"))) {
                    obj.setLandLine_No(rs.getString("landline"));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("email_id"))) {
                    obj.setEmail_Id(rs.getString("email_id").toUpperCase());
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("off_under_cd"))) {
                    obj.setOff_under_cd(Integer.parseInt(rs.getString("off_under_cd")));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("off_type_cd"))) {
                    obj.setOff_type_cd(Integer.parseInt(rs.getString("off_type_cd")));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("vow4"))) {
                    obj.setVow4_dt(JSFUtils.getStringToDateyyyyMMdd(rs.getString("vow4")));
                }
                if (!CommonUtils.isNullOrBlank(rs.getString("statecdWithOffcd"))) {
                    obj.setStatecdWithOffcd(rs.getString("statecdWithOffcd").toUpperCase());
                }
                obj.setSmartcardFlag(rs.getBoolean("smart_card"));
                obj.setHsrpFlag(rs.getBoolean("hsrp"));
                obj.setOldHsrp(rs.getBoolean("old_veh_hsrp"));
                obj.setPaperRc(rs.getString("paper_rc").toUpperCase());
                if (rs.getBoolean("smart_card")) {
                    obj.setRcOption("S");
                } else {
                    if (obj.getPaperRc().equalsIgnoreCase(TableConstants.VM_PAPER_RC_CD)) {
                        obj.setRcOption("R");
                    } else if (obj.getPaperRc().equalsIgnoreCase(TableConstants.VM_PLASTIC_CARD_RC_CD)) {
                        obj.setRcOption("P");
                    } else if (obj.getPaperRc().equalsIgnoreCase(TableConstants.VM_PLASTIC_CARD_RC_PVC_CD)) {
                        obj.setRcOption("V");
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some Error occurred while fetching the Record");

        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return obj;
    }

    public static OfficeModificationDobj getSmartcardHsrpDetails(String stateCd, OfficeModificationDobj ob) throws VahanException {
        String sql = "select smart_card,hsrp,old_veh_hsrp,paper_rc from " + TableList.VM_SMART_CARD_HSRP + " where state_cd=? and off_cd=?";
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("getSmartcardHsrpDetails");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, ob.getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                ob.setSmartcardFlag(rs.getBoolean("smart_card"));
                ob.setHsrpFlag(rs.getBoolean("hsrp"));
                ob.setOldHsrp(rs.getBoolean("old_veh_hsrp"));
                ob.setPaperRc(rs.getString("paper_rc"));
                if (rs.getBoolean("smart_card")) {
                    ob.setRcOption("S");
                } else {
                    if (ob.getPaperRc().equalsIgnoreCase(TableConstants.VM_PAPER_RC_CD)) {
                        ob.setRcOption("R");
                    } else if (ob.getPaperRc().equalsIgnoreCase(TableConstants.VM_PLASTIC_CARD_RC_CD)) {
                        ob.setRcOption("P");
                    } else if (ob.getPaperRc().equalsIgnoreCase(TableConstants.VM_PLASTIC_CARD_RC_PVC_CD)) {
                        ob.setRcOption("V");
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Some Error occurred while fetching the Record");

        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return ob;
    }

    public static Boolean CheckPendingSmartcardFlatfile(int off_cd, String state_cd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        String query = "";
        boolean status = false;
        RowSet rs;
        try {
            tmgr = new TransactionManager("CheckPendingSmartcardFlatfile");
            query = "Select off_cd from " + TableList.VA_SMART_CARD + " where off_cd = ? and state_cd= ? ";
            ps = tmgr.prepareStatement(query);
            ps.setInt(1, off_cd);
            ps.setString(2, state_cd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                status = true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                throw new VahanException(e.getMessage());
            }
        }
        return status;
    }
}
