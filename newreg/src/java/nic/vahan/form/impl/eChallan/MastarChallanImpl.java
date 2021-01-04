/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.eChallan;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.eChallan.VmChlnBookDobj;
import nic.vahan.form.dobj.eChallan.VmCourtDobj;
import nic.vahan.form.dobj.eChallan.VmDaDobj;
import nic.vahan.form.dobj.eChallan.VmDaPenaltyDobj;
import nic.vahan.form.dobj.eChallan.VmExScheduleDobj;
import nic.vahan.form.dobj.eChallan.VmOffenceDobj;
import nic.vahan.form.dobj.eChallan.VmOffencePenaltyDobj;
import nic.vahan.form.dobj.eChallan.VmSectionsDobj;
import nic.vahan.form.dobj.eChallan.VmOvlScheduleDobj;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC111
 */
public class MastarChallanImpl {

    private static final Logger LOGGER = Logger.getLogger(MastarChallanImpl.class);

    public boolean addChallanBookRecord(VmChlnBookDobj chln_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("addChallanBookRecord");
            String sql = "INSERT INTO  " + TableList.VM_CHALLAN_BOOK + " (\n"
                    + "            user_cd, book_no,chal_no_from,chal_no_upto,chal_no_current,expired,issue_dt,issue_by,state_cd,off_cd)\n"
                    + "    VALUES (?,?,?,?,?,?,?,?,?,?)";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, (chln_dobj.getUsercode()));
            psmt.setString(2, chln_dobj.getBook_no());
            psmt.setInt(3, chln_dobj.getChln_frm());
            psmt.setInt(4, chln_dobj.getChln_upto());
            psmt.setInt(5, chln_dobj.getCurr_chln_no());
            psmt.setString(6, chln_dobj.getExpired());
            if (chln_dobj.getIss_dt() == null) {
                psmt.setTimestamp(7, null); //valid upto
            } else {
                psmt.setTimestamp(7, Utility.convertStringToTimestamp(chln_dobj.getIss_dt()));
            }
            psmt.setString(8, chln_dobj.getIss_by());
            psmt.setString(9, Util.getUserStateCode());
            psmt.setInt(10, chln_dobj.getOff_cd());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();
        }
        return flag;

    }

    public boolean addCourtRecord(VmCourtDobj court_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("addCourtRecord");
            String sql = "INSERT INTO  " + TableList.VM_COURT + "(\n"
                    + "            court_cd,court_name,state_cd)\n"
                    + "    VALUES (?, ?, ?)";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, (court_dobj.getCourtcode()));
            psmt.setString(2, court_dobj.getCourtname());
            psmt.setString(3, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();
        }
        return flag;

    }

    public boolean addDARecord(VmDaDobj da_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("addDARecord");
            String sql = "INSERT INTO  " + TableList.VM_DA + " (\n"
                    + "            code,descr,offence_cd,da_schedule,da_sub_shcedule,state_cd)\n"
                    + "    VALUES (?,?,?,?,?,?)";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, da_dobj.getCode());
            psmt.setString(2, da_dobj.getDescription());
            psmt.setInt(3, Integer.parseInt(da_dobj.getOffence_code()));
            psmt.setString(4, da_dobj.getDa_schedule());
            psmt.setString(5, da_dobj.getDa_sub_schedule());
            psmt.setString(6, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean addDAPenaltyRecord(VmDaPenaltyDobj dapenalty_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("addDAPenaltyRecord");
            String sql = "INSERT INTO  " + TableList.VM_DA_PENALTY + " (\n"
                    + "    sr_no,da_code,vh_class,o_penalty,d_penalty,c_penalty,sus_days,o_penalty1,d_penalty1,c_penalty1,sus_days1,o_penalty2,d_penalty2,c_penalty2,sus_days2,sus_action,state_cd)\n"
                    + "    VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, dapenalty_dobj.getSrno());
            psmt.setInt(2, dapenalty_dobj.getDacode());
            psmt.setInt(3, Integer.parseInt(dapenalty_dobj.getVhclass()));
            psmt.setInt(4, dapenalty_dobj.getOpenalty());
            psmt.setInt(5, dapenalty_dobj.getDpenalty());
            psmt.setInt(6, dapenalty_dobj.getCpenalty());
            psmt.setInt(7, dapenalty_dobj.getSusdays());
            psmt.setInt(8, dapenalty_dobj.getOpenalty1());
            psmt.setInt(9, dapenalty_dobj.getDpenalty1());
            psmt.setInt(10, dapenalty_dobj.getCpenalty1());
            psmt.setInt(11, dapenalty_dobj.getSusdays1());
            psmt.setInt(12, dapenalty_dobj.getOpenalty2());
            psmt.setInt(13, dapenalty_dobj.getDpenalty2());
            psmt.setInt(14, dapenalty_dobj.getCpenalty2());
            psmt.setInt(15, dapenalty_dobj.getSusdays2());
            psmt.setString(16, dapenalty_dobj.getSusaction());
            psmt.setString(17, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean addExScheduleRecord(VmExScheduleDobj exsch_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("addExScheduleRecord");
            String sql = "INSERT INTO  " + TableList.VM_EXCESS_SCHEDULE + " (\n"
                    + "            sr_no,type,vh_class,flat_rate,unit_rate,state_cd)\n"
                    + "    VALUES (?,?,?,?,?,?)";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, exsch_dobj.getSrno());
            psmt.setString(2, exsch_dobj.getType());
            psmt.setString(3, exsch_dobj.getVh_class());
            psmt.setInt(4, exsch_dobj.getFlatrate());
            psmt.setInt(5, exsch_dobj.getUnitrate());
            psmt.setString(6, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean addOffenceRecord(VmOffenceDobj off_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("addOffenceRecord");
            String sql = "INSERT INTO  " + TableList.VM_OFFENCES + " (\n"
                    + " offence_cd,offence_desc,mva_clause,cmvr_clause,smvr_clause,addl_desc,punishment_1,punishment_2,is_active_for_tpt,is_active_for_police,state_cd)\n"
                    + "    VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, off_dobj.getOff_code());
            psmt.setString(2, off_dobj.getOffence_desc());
            psmt.setString(3, off_dobj.getMva_clause());
            psmt.setString(4, off_dobj.getCmvr_clause());
            psmt.setString(5, off_dobj.getSmvr_clause());
            psmt.setString(6, off_dobj.getAddl_desc());
            psmt.setString(7, off_dobj.getPunishment1());
            psmt.setString(8, off_dobj.getPunishment2());
            psmt.setString(9, off_dobj.getIsactivefor_tpt());
            psmt.setString(10, off_dobj.getIsactivefor_police());
            psmt.setString(11, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean addSectionRecord(VmSectionsDobj sec_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("addSectionRecord");
            String sql = "INSERT INTO  " + TableList.VM_SECTION + "(\n"
                    + " section_cd,level_flag,section_name,is_active,state_cd)\n"
                    + "    VALUES (?,?,?,?,?)";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, sec_dobj.getSectionCode());
            psmt.setString(2, sec_dobj.getLevel_flag());
            psmt.setString(3, sec_dobj.getSection_name());
            psmt.setString(4, sec_dobj.getIs_active());
            psmt.setString(5, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();
        }
        return flag;
    }

    public boolean addOvlScheduleRecord(VmOvlScheduleDobj ovl_sch_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("addOvlScheduleRecord");
            String sql = "INSERT INTO  " + TableList.VM_OVERLOAD_SCHEDULE + " (\n"
                    + " sr_no,ld_wt_lower,ld_wt_upper,ld_wt_unit,flat_cf_amt,unit_cf_amt,wt_unit_val,state_cd)\n"
                    + "    VALUES (?,?,?,?,?,?,?,?)";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, ovl_sch_dobj.getSrno());
            psmt.setInt(2, ovl_sch_dobj.getLd_wt_lower());
            psmt.setInt(3, ovl_sch_dobj.getLd_wt_upper());
            psmt.setString(4, ovl_sch_dobj.getLd_wt_unit());
            psmt.setInt(5, ovl_sch_dobj.getFlat_cf_amt());
            psmt.setInt(6, ovl_sch_dobj.getUnit_cf_amt());
            psmt.setInt(7, ovl_sch_dobj.getWt_unit_val());
            psmt.setString(8, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean addOffPeanltyRecord(VmOffencePenaltyDobj offpenalty_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        String select_vh_class = "";
        int sr_no = offpenalty_dobj.getSrno();
        String selectedAccused = "";
        for (int j = 0; j < offpenalty_dobj.getSelected_accused().size(); j++) {
            selectedAccused += offpenalty_dobj.getSelected_accused().get(j) + ",";
        }
        if (selectedAccused.endsWith(",")) {
            selectedAccused = selectedAccused.substring(0, selectedAccused.length() - 1);
        }
        try {
            tmgr = new TransactionManager("addOffPeanltyRecord");
            String sql = "INSERT INTO " + TableList.VM_OFFENCE_PENALTY + "(\n"
                    + "            sr_no, offence_cd, vh_class, section_cd, penalty1, penalty2, \n"
                    + "            penalty3, offence_applied_on, state_cd)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, ?)";
            psmt = tmgr.prepareStatement(sql);
            for (Object vh_class : offpenalty_dobj.getSelected_vhclass()) {
                select_vh_class = vh_class.toString();
                psmt.setInt(1, sr_no++);
                psmt.setInt(2, Integer.parseInt(offpenalty_dobj.getNewoffcode()));
                psmt.setInt(3, Integer.parseInt(select_vh_class));
                psmt.setInt(4, Integer.parseInt(offpenalty_dobj.getSectioncd()));
                psmt.setInt(5, offpenalty_dobj.getOpenalty());
                psmt.setInt(6, offpenalty_dobj.getDpenalty());
                psmt.setInt(7, offpenalty_dobj.getCpenalty());
                psmt.setString(8, selectedAccused);
                psmt.setString(9, Util.getUserStateCode());
                psmt.executeUpdate();
            }
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean updateChallanMasterRecord(VmChlnBookDobj chlnbook_dobj, VmChlnBookDobj previous_chlnbook_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("updateChallanMasterRecord");
            String sql = "UPDATE " + TableList.VM_CHALLAN_BOOK + "\n"
                    + "   SET user_cd=?,chal_no_from=?,chal_no_upto=?,chal_no_current=?,expired=?,issue_dt=?,issue_by=?\n"
                    + " WHERE book_no=?  and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, chlnbook_dobj.getUsercode());
            psmt.setInt(2, chlnbook_dobj.getChln_frm());
            psmt.setInt(3, chlnbook_dobj.getChln_upto());
            psmt.setInt(4, chlnbook_dobj.getCurr_chln_no());
            psmt.setString(5, chlnbook_dobj.getExpired());
            if (chlnbook_dobj.getIss_dt() == null) {
                psmt.setTimestamp(6, null); //valid upto
            } else {
                psmt.setTimestamp(6, Utility.convertStringToTimestamp(chlnbook_dobj.getIss_dt()));
            }
            psmt.setString(7, chlnbook_dobj.getIss_by());
            psmt.setString(8, chlnbook_dobj.getBook_no());
            psmt.setString(9, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean updateCourtMasterRecord(VmCourtDobj court_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("updateBodyTypeMasterRecord");
            String sql = "UPDATE " + TableList.VM_COURT + "\n"
                    + "   SET court_name=?\n"
                    + " WHERE court_cd=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, court_dobj.getCourtname());
            psmt.setInt(2, (court_dobj.getCourtcode()));
            psmt.setString(3, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean updateDAMasterRecord(VmDaDobj da_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("updateDAMasterRecord");
            String sql = "UPDATE " + TableList.VM_DA + " \n"
                    + "   SET descr=?,offence_cd=?,da_schedule=?,da_sub_shcedule=?\n"
                    + " WHERE code=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, da_dobj.getDescription());
            psmt.setInt(2, Integer.parseInt(da_dobj.getOffence_code()));
            psmt.setString(3, (da_dobj.getDa_schedule()));
            psmt.setString(4, (da_dobj.getDa_sub_schedule()));
            psmt.setInt(5, (da_dobj.getCode()));
            psmt.setString(6, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean updateDaPenaltyMasterRecord(VmDaPenaltyDobj dapenalty_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("updateDAPENALTYMasterRecord");
            String sql = "UPDATE " + TableList.VM_DA_PENALTY + " \n"
                    + "   SET da_code=?,vh_class=?,o_penalty=?,d_penalty=?,c_penalty=?,sus_days=?,o_penalty1=?,d_penalty1=?,c_penalty1=?,sus_days1=?,o_penalty2=?,d_penalty2=?,c_penalty2=?,sus_days2=?,sus_action=?\n"
                    + " WHERE sr_no=?  and state_cd=?";
            psmt = tmgr.prepareStatement(sql);

            psmt.setInt(1, dapenalty_dobj.getDacode());
            psmt.setInt(2, Integer.parseInt(dapenalty_dobj.getVhclass()));
            psmt.setInt(3, (dapenalty_dobj.getOpenalty()));
            psmt.setInt(4, (dapenalty_dobj.getDpenalty()));
            psmt.setInt(5, (dapenalty_dobj.getCpenalty()));
            psmt.setInt(6, (dapenalty_dobj.getSusdays()));
            psmt.setInt(7, (dapenalty_dobj.getOpenalty1()));
            psmt.setInt(8, (dapenalty_dobj.getDpenalty1()));
            psmt.setInt(9, (dapenalty_dobj.getCpenalty1()));
            psmt.setInt(10, (dapenalty_dobj.getSusdays1()));
            psmt.setInt(11, (dapenalty_dobj.getOpenalty2()));
            psmt.setInt(12, (dapenalty_dobj.getDpenalty2()));
            psmt.setInt(13, (dapenalty_dobj.getCpenalty2()));
            psmt.setInt(14, (dapenalty_dobj.getSusdays2()));
            psmt.setString(15, (dapenalty_dobj.getSusaction()));
            psmt.setInt(16, (dapenalty_dobj.getSrno()));
            psmt.setString(17, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean updateExScheduleMasterRecord(VmExScheduleDobj exsch_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("updateEXSCHEDULEMasterRecord");
            String sql = "UPDATE " + TableList.VM_EXCESS_SCHEDULE + " \n"
                    + "   SET sr_no=?,type=?,vh_class=?,flat_rate=?,unit_rate=?\n"
                    + " WHERE sr_no=?  and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, (exsch_dobj.getSrno()));
            psmt.setString(2, exsch_dobj.getType());
            psmt.setString(3, (exsch_dobj.getVh_class()));
            psmt.setInt(4, (exsch_dobj.getFlatrate()));
            psmt.setInt(5, exsch_dobj.getUnitrate());
            psmt.setInt(6, (exsch_dobj.getSrno()));
            psmt.setString(7, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean updateOffenceMasterRecord(VmOffenceDobj off_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("updateOFFENCEMasterRecord");
            String sql = "UPDATE " + TableList.VM_OFFENCES + " \n"
                    + "   SET offence_desc=?,mva_clause=?,cmvr_clause=?,smvr_clause=?,addl_desc=?,punishment_1=?,punishment_2=?,is_active_for_tpt=?,is_active_for_police=?\n"
                    + " WHERE offence_cd=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, off_dobj.getOffence_desc());
            psmt.setString(2, (off_dobj.getMva_clause()));
            psmt.setString(3, (off_dobj.getCmvr_clause()));
            psmt.setString(4, off_dobj.getSmvr_clause());
            psmt.setString(5, (off_dobj.getAddl_desc()));
            psmt.setString(6, off_dobj.getPunishment1());
            psmt.setString(7, off_dobj.getPunishment2());
            psmt.setString(8, off_dobj.getIsactivefor_tpt());
            psmt.setString(9, off_dobj.getIsactivefor_police());
            psmt.setInt(10, (off_dobj.getOff_code()));
            psmt.setString(11, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean updateOffencePenaltyMasterRecord(VmOffencePenaltyDobj offpenalty_dobj)
            throws SQLException, Exception {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("updateOFFENCEPENALTYMasterRecord");
            String sql = "UPDATE " + TableList.VM_OFFENCE_PENALTY + "\n"
                    + " SET \n"
                    + " penalty1=?,penalty2=?,penalty3=?\n"
                    + " WHERE offence_cd=? and vh_class=?  and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, offpenalty_dobj.getOpenalty());
            psmt.setInt(2, (offpenalty_dobj.getDpenalty()));
            psmt.setInt(3, offpenalty_dobj.getCpenalty());
            psmt.setInt(4, Integer.parseInt(getOffnCd(offpenalty_dobj.getOldoffcode())));
            psmt.setInt(5, Integer.parseInt(getVhClassCode(offpenalty_dobj.getVhclass())));
            psmt.setString(6, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean updateSectionMasterRecord(VmSectionsDobj sec_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("updateSECTIONMasterRecord");

            String sql = "UPDATE " + TableList.VM_SECTION + " \n"
                    + "   SET level_flag=?,section_name=?,is_active=?\n"
                    + " WHERE section_cd=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setString(1, sec_dobj.getLevel_flag());
            psmt.setString(2, (sec_dobj.getSection_name()));
            psmt.setString(3, (sec_dobj.getIs_active()));
            psmt.setInt(4, sec_dobj.getSectionCode());
            psmt.setString(5, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean updateOvlScheduleMasterRecord(VmOvlScheduleDobj ovl_sch_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("updateOvlScheduleMasterRecord");
            String sql = "UPDATE " + TableList.VM_OVERLOAD_SCHEDULE + " \n"
                    + "   SET sr_no=?,ld_wt_lower=?,ld_wt_upper=?,ld_wt_unit=?,flat_cf_amt=?,unit_cf_amt=?,wt_unit_val=?"
                    + " WHERE sr_no=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, (ovl_sch_dobj.getSrno()));
            psmt.setInt(2, ovl_sch_dobj.getLd_wt_lower());
            psmt.setInt(3, (ovl_sch_dobj.getLd_wt_upper()));
            psmt.setString(4, (ovl_sch_dobj.getLd_wt_unit()));
            psmt.setInt(5, ovl_sch_dobj.getFlat_cf_amt());
            psmt.setInt(6, ovl_sch_dobj.getUnit_cf_amt());
            psmt.setInt(7, ovl_sch_dobj.getWt_unit_val());
            psmt.setInt(8, (ovl_sch_dobj.getSrno()));
            psmt.setString(9, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public List<VmOffencePenaltyDobj> getoffenceList(List vclass) throws Exception, SQLException {
        TransactionManager tmgr = null;
        PreparedStatement pstoffence;
        List offenceList = new ArrayList();
        VmOffencePenaltyDobj dobj = null;
        String vhclass = "";
        ResultSet rs = null;
        try {
            String sqlAccusedDetails = "select * from " + TableList.VM_OFFENCE_PENALTY + " where vh_class=? and state_cd=?";
            tmgr = new TransactionManager("getoffenceList");
            pstoffence = tmgr.prepareStatement(sqlAccusedDetails);
            for (Object vh : vclass) {
                vhclass = vh.toString();
                pstoffence.setInt(1, Integer.parseInt(vhclass));
                pstoffence.setString(2, Util.getUserStateCode());
                rs = tmgr.fetchDetachedRowSet();
                while (rs.next()) {
                    dobj = new VmOffencePenaltyDobj(rs.getInt("sr_no"), rs.getString("offence_cd"), rs.getString("offence_cd"), rs.getString("vh_class"), rs.getString("section_cd"), rs.getInt("o_penalty"), rs.getInt("d_penalty"), rs.getInt("c_penalty"), rs.getInt("o_penalty1"), rs.getInt("d_penalty1"), rs.getInt("c_penalty1"), rs.getInt("o_penalty2"), rs.getInt("d_penalty2"), rs.getInt("c_penalty2"));
                    offenceList.add(dobj);
                }
            }
        } finally {
            tmgr.release();

        }
        return offenceList;
    }

    public int fetchDaSrNo() throws Exception, SQLException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        int maxdasrno = 0;
        try {
            tmgr = new TransactionManager("fetchDaSrNo");
            String maxsrno = "SELECT max(sr_no)+1 as sr_no from " + TableList.VM_DA_PENALTY + " where state_cd=? ";
            ps = tmgr.prepareStatement(maxsrno);
            ps.setString(1, Util.getUserStateCode());
            java.sql.ResultSet rs = null;
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                maxdasrno = rs.getInt("sr_no");
                if (maxdasrno == 0) {
                    maxdasrno = 1;
                }
            }
        } finally {
            tmgr.release();

        }
        return maxdasrno;
    }

    public int fetchExSchSrno() throws Exception, SQLException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        int maxexsrno = 0;
        try {
            tmgr = new TransactionManager("fetchExSchSrno");
            String maxsrno = "SELECT max(sr_no)+1 as sr_no from " + TableList.VM_EXCESS_SCHEDULE + " where state_cd=? ";
            ps = tmgr.prepareStatement(maxsrno);
            ps.setString(1, Util.getUserStateCode());
            java.sql.ResultSet rs = null;
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                maxexsrno = rs.getInt("sr_no");
                if (maxexsrno == 0) {
                    maxexsrno = 1;
                }
            }
        } finally {
            tmgr.release();

        }
        return maxexsrno;
    }

    public boolean checkOffenceCodeExist(VmOffenceDobj off_dobj) throws Exception, SQLException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        boolean flag = false;

        java.sql.ResultSet rs = null;
        try {
            tmgr = new TransactionManager("checkOffenceCodeExit");
            String maxsrno = " Select * from  " + TableList.VM_OFFENCES + "  where offence_cd = ? and state_cd =?";
            ps = tmgr.prepareStatement(maxsrno);
            ps.setInt(1, (off_dobj.getOff_code()));
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                flag = true;
            }



        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean checkDaCodeExist(VmDaDobj da_dobj) throws Exception, SQLException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        boolean flag = false;

        java.sql.ResultSet rs = null;
        try {
            tmgr = new TransactionManager("checkDaCodeExist");
            String maxsrno = " Select * from  " + TableList.VM_DA + "  where code = ? and state_cd=?";
            ps = tmgr.prepareStatement(maxsrno);
            ps.setInt(1, (da_dobj.getCode()));
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                flag = true;
            }


        } finally {
            tmgr.release();

        }
        return flag;
    }

    public int fetchOffPenlSrno() throws Exception, SQLException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        int maxoffpnlsrno = 0;
        try {
            tmgr = new TransactionManager("fetchOffPenlSrno");
            String maxsrno = "SELECT max(sr_no)+1 as sr_no from " + TableList.VM_OFFENCE_PENALTY + " where state_cd =?";
            ps = tmgr.prepareStatement(maxsrno);
            ps.setString(1, Util.getUserStateCode());
            java.sql.ResultSet rs = null;
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                maxoffpnlsrno = rs.getInt("sr_no");
                if (maxoffpnlsrno == 0) {
                    maxoffpnlsrno = 1;
                }
            }
        } finally {
            tmgr.release();

        }
        return maxoffpnlsrno;

    }

    public int fetchOvrldSrno() throws Exception, SQLException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        int maxovrldsrno = 0;
        try {
            tmgr = new TransactionManager("fetchOvrldSrno");
            String maxsrno = "SELECT max(sr_no)+1 as sr_no from " + TableList.VM_OVERLOAD_SCHEDULE + " where state_cd=? ";
            ps = tmgr.prepareStatement(maxsrno);
            ps.setString(1, Util.getUserStateCode());
            java.sql.ResultSet rs = null;
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                maxovrldsrno = rs.getInt("sr_no");
                if (maxovrldsrno == 0) {
                    maxovrldsrno = 1;
                }
            }
        } finally {
            tmgr.release();

        }
        return maxovrldsrno;
    }

    public boolean checkChallanBookCodeExist(VmChlnBookDobj chlnbook_dobj) throws Exception, SQLException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        boolean flag = false;

        java.sql.ResultSet rs = null;
        try {
            tmgr = new TransactionManager("checkChallanBookCodeExist");
            String maxsrno = " Select * from  " + TableList.VM_CHALLAN_BOOK + "  where user_cd = ? and state_cd=?";
            ps = tmgr.prepareStatement(maxsrno);
            ps.setInt(1, (chlnbook_dobj.getUsercode()));
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {

                flag = true;
            }


        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean checkCourtCodeExist(VmCourtDobj court_dobj) throws Exception, SQLException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        boolean flag = false;

        java.sql.ResultSet rs = null;
        try {
            tmgr = new TransactionManager("checkCourtCodeExist");
            String maxsrno = " Select * from  " + TableList.VM_COURT + "  where court_cd = ? and state_cd=?";
            ps = tmgr.prepareStatement(maxsrno);
            ps.setInt(1, (court_dobj.getCourtcode()));
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                flag = true;
            }

        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean checkOffencePenaltyCodeExist(VmOffencePenaltyDobj offpenalty_dobj) throws Exception, SQLException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        boolean flag = false;

        RowSet rs = null;
        try {
            tmgr = new TransactionManager("checkOffencePenaltyCodeExist");
            String maxsrno = " select * from " + TableList.VM_OFFENCE_PENALTY + " where offence_cd=? and vh_class=? and state_cd =?";
            ps = tmgr.prepareStatement(maxsrno);
            ps.setInt(1, Integer.parseInt(offpenalty_dobj.getNewoffcode()));
            ps.setInt(2, Integer.parseInt(offpenalty_dobj.getVhclass()));
            ps.setString(3, Util.getUserStateCode());

            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {

                flag = true;
            }

        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean DeleteCourtMasterRecord(VmCourtDobj court_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("DeleteCourtMasterRecord");
            String sql = "delete from " + TableList.VM_COURT + " where court_cd=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, (court_dobj.getCourtcode()));
            psmt.setString(2, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean DeleteChallanMasterRecord(VmChlnBookDobj chlnbook_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("DeleteChallanMasterRecord");
            String sql = "delete from " + TableList.VM_CHALLAN_BOOK + " where user_cd=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, (chlnbook_dobj.getUsercode()));
            psmt.setString(2, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean DeleteDAMasterRecord(VmDaDobj da_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("DeleteDAMasterRecord");
            String sql = "delete from " + TableList.VM_DA + " where code=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, (da_dobj.getCode()));
            psmt.setString(2, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean DeleteDaPenaltyMasterRecord(VmDaPenaltyDobj dapenalty_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("DeleteDaPenaltyMasterRecord");
            String sql = "delete from " + TableList.VM_DA_PENALTY + " where sr_no=? and da_code=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, (dapenalty_dobj.getSrno()));
            psmt.setInt(2, dapenalty_dobj.getDacode());
            psmt.setString(3, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean DeleteExScheduleMasterRecord(VmExScheduleDobj exsch_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("DeleteExScheduleMasterRecord");
            String sql = "delete from " + TableList.VM_EXCESS_SCHEDULE + " where sr_no=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, (exsch_dobj.getSrno()));
            psmt.setString(2, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean DeleteOffenceMasterRecord(VmOffenceDobj off_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("DeleteOffenceMasterRecord");
            String sql = "delete from " + TableList.VM_OFFENCES + " where offence_cd=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, (off_dobj.getOff_code()));
            psmt.setString(2, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean DeleteOffencePenaltyMasterRecord(VmOffencePenaltyDobj offpenalty_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("DeleteOffencePenaltyMasterRecord");
            String sql = "delete from " + TableList.VM_OFFENCE_PENALTY + " where sr_no=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, (offpenalty_dobj.getSrno()));
            psmt.setString(2, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public boolean DeleteOvlScheduleMasterRecord(VmOvlScheduleDobj ovl_sch_dobj)
            throws Exception, SQLException {
        boolean flag = false;
        PreparedStatement psmt = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("DeleteOvlScheduleMasterRecord");
            String sql = "delete from " + TableList.VM_OVERLOAD_SCHEDULE + " where sr_no=? and state_cd=?";
            psmt = tmgr.prepareStatement(sql);
            psmt.setInt(1, (ovl_sch_dobj.getSrno()));
            psmt.setString(2, Util.getUserStateCode());
            psmt.executeUpdate();
            flag = true;
            tmgr.commit();
        } finally {
            tmgr.release();

        }
        return flag;
    }

    public List<VmCourtDobj> getCourtList() throws Exception, SQLException {
        TransactionManager tmgr = null;
        PreparedStatement pstmtPendingCase = null;
        VmCourtDobj dobjcourt = null;
        List<VmCourtDobj> CourtList = new ArrayList<VmCourtDobj>();
        try {
            String sqlPendigCases = "select * from " + TableList.VM_COURT + " where state_cd=? order by court_cd ";
            tmgr = new TransactionManager("getCourtList");
            pstmtPendingCase = tmgr.prepareStatement(sqlPendigCases);
            pstmtPendingCase.setString(1, Util.getUserStateCode());
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobjcourt = new VmCourtDobj(rs.getInt("court_cd"), rs.getString("court_name"));
                CourtList.add(dobjcourt);
            }
        } finally {
            tmgr.release();

        }
        return CourtList;
    }

    public List<VmChlnBookDobj> getChallanBookList() throws Exception, SQLException {
        TransactionManager tmgr = null;
        PreparedStatement pstmtPendingCase = null;
        VmChlnBookDobj dobjchallan = null;
        List<VmChlnBookDobj> ChallanList = new ArrayList<VmChlnBookDobj>();
        try {
            String sqlPendigCases = "select  chalbook.user_cd, chalbook.book_no, chalbook.chal_no_from, chalbook.chal_no_upto, chalbook.chal_no_current, \n"
                    + "       chalbook.expired, to_char(chalbook.issue_dt,'dd-MM-yyyy') as issue_dt, chalbook.issue_by, chalbook.state_cd, chalbook.off_cd,tmUinfo.user_name\n"
                    + "        from " + TableList.VM_CHALLAN_BOOK + "  chalbook\n"
                    + "Left Outer JOin " + TableList.TM_USER_INFO + " tmUinfo on chalbook.user_cd=tmUinfo.user_cd and chalbook.state_cd=tmUinfo.state_cd\n"
                    + " where chalbook.state_cd=? and chalbook.off_cd=? order by chalbook.user_cd ";
            tmgr = new TransactionManager("getChallanBookList");
            pstmtPendingCase = tmgr.prepareStatement(sqlPendigCases);
            pstmtPendingCase.setString(1, Util.getUserStateCode());
            pstmtPendingCase.setInt(2, Util.getSelectedSeat().getOff_cd());
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobjchallan = new VmChlnBookDobj(rs.getInt("user_cd"), rs.getString("book_no"), rs.getInt("chal_no_from"), rs.getInt("chal_no_upto"), rs.getInt("chal_no_current"), rs.getString("expired"), rs.getString("issue_dt"), rs.getString("issue_by"), rs.getInt("off_cd"), rs.getString("user_name"));
                ChallanList.add(dobjchallan);
            }
        } finally {
            tmgr.release();

        }
        return ChallanList;
    }

    public List<VmDaDobj> getDaList() throws Exception, SQLException {
        TransactionManager tmgr = null;
        PreparedStatement pstmtPendingCase = null;
        VmDaDobj dobjda = null;
        List<VmDaDobj> DaList = new ArrayList<VmDaDobj>();
        try {
            String sqlPendigCases = "select d.code,d.descr,o.offence_cd,o.offence_desc,d.da_schedule,d.da_sub_shcedule from " + TableList.VM_DA
                    + " d inner join " + TableList.VM_OFFENCES + " o on d.offence_cd=o.offence_cd and d.state_cd=o.state_cd where d.state_cd=? order by d.code ";
            tmgr = new TransactionManager("getDaList");
            pstmtPendingCase = tmgr.prepareStatement(sqlPendigCases);
            pstmtPendingCase.setString(1, Util.getUserStateCode());
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobjda = new VmDaDobj(rs.getInt("code"), rs.getString("descr"), rs.getString("offence_cd"), rs.getString("da_schedule"), rs.getString("da_sub_shcedule"));
                DaList.add(dobjda);
            }
        } finally {
            tmgr.release();
        }
        return DaList;
    }

    public List<VmDaPenaltyDobj> getDaPenaltyList() throws Exception, SQLException {
        TransactionManager tmgr = null;
        PreparedStatement pstmtPendingCase = null;
        VmDaPenaltyDobj dobjdapenalty = null;
        List<VmDaPenaltyDobj> DapenaltyList = new ArrayList<VmDaPenaltyDobj>();
        try {
            String sqlPendigCases = " select d.sr_no,d.da_code,d.vh_class,v.descr as vh_class_descr,d.o_penalty,d.d_penalty,d.c_penalty,d.sus_days,d.o_penalty1,d.d_penalty1,d.c_penalty1,d.sus_days1,d.o_penalty2,d.d_penalty2,d.c_penalty2,\n"
                    + " d.sus_days2,d.sus_action "
                    + "from " + TableList.VM_DA_PENALTY + " d "
                    + "inner join " + TableList.VM_VH_CLASS + " v on d.vh_class=v.vh_class where d.state_cd=? order by d.sr_no ";
            tmgr = new TransactionManager("getDaPenaltyList");
            pstmtPendingCase = tmgr.prepareStatement(sqlPendigCases);
            pstmtPendingCase.setString(1, Util.getUserStateCode());
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobjdapenalty = new VmDaPenaltyDobj(rs.getInt("sr_no"), rs.getInt("da_code"), rs.getString("vh_class"), rs.getInt("o_penalty"), rs.getInt("d_penalty"), rs.getInt("c_penalty"), rs.getInt("sus_days"), rs.getInt("o_penalty1"), rs.getInt("d_penalty1"), rs.getInt("c_penalty1"), rs.getInt("sus_days1"), rs.getInt("o_penalty2"), rs.getInt("d_penalty2"), rs.getInt("c_penalty2"), rs.getInt("sus_days2"), rs.getString("sus_action"));
                DapenaltyList.add(dobjdapenalty);
            }
        } finally {
            tmgr.release();
        }
        return DapenaltyList;
    }

    public List<VmExScheduleDobj> getExSchList() throws Exception, SQLException {
        TransactionManager tmgr = null;
        PreparedStatement pstmtPendingCase = null;
        VmExScheduleDobj dobjexsch = null;
        List<VmExScheduleDobj> ExschList = new ArrayList<VmExScheduleDobj>();
        try {
            String sqlPendigCases = "select e.sr_no,e.type,e.vh_class,v.descr,e.flat_rate,e.unit_rate from " + TableList.VM_EXCESS_SCHEDULE + " e inner join " + TableList.VM_VH_CLASS + " v on v.vh_class::varchar = e.vh_class where e.state_cd=? order by e.sr_no ";
            tmgr = new TransactionManager("getExSchList");
            pstmtPendingCase = tmgr.prepareStatement(sqlPendigCases);
            pstmtPendingCase.setString(1, Util.getUserStateCode());
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobjexsch = new VmExScheduleDobj(rs.getInt("sr_no"), rs.getString("type"), rs.getString("vh_class"), rs.getInt("flat_rate"), rs.getInt("unit_rate"));
                ExschList.add(dobjexsch);
            }

        } finally {
            tmgr.release();

        }
        return ExschList;
    }

    public List<VmOffenceDobj> getOffenceList() throws Exception, SQLException {
        TransactionManager tmgr = null;
        PreparedStatement pstmtPendingCase = null;
        VmOffenceDobj dobjoffence = null;
        List<VmOffenceDobj> offenceList = new ArrayList<VmOffenceDobj>();
        try {
            String sqlPendigCases = "select * from " + TableList.VM_OFFENCES + " where state_cd =? order by offence_cd ";
            tmgr = new TransactionManager("getOffenceList");
            pstmtPendingCase = tmgr.prepareStatement(sqlPendigCases);
            pstmtPendingCase.setString(1, Util.getUserStateCode());
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobjoffence = new VmOffenceDobj(rs.getInt("offence_cd"), rs.getString("offence_desc"), rs.getString("mva_clause"), rs.getString("cmvr_clause"), rs.getString("smvr_clause"), rs.getString("addl_desc"), rs.getString("punishment_1"), rs.getString("punishment_2"), rs.getString("is_active_for_tpt"), rs.getString("is_active_for_police"));
                offenceList.add(dobjoffence);
            }
        } finally {
            tmgr.release();

        }
        return offenceList;
    }

    public List<VmOffencePenaltyDobj> getOffencePenaltyList() throws SQLException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement pstmtPendingCase = null;
        VmOffencePenaltyDobj dobjoffPnl = null;
        String descr = "";
        List<VmOffencePenaltyDobj> offPnlList = new ArrayList<>();
        try {
            String sqlPendigCases = "select o.sr_no,of.offence_desc,of.mva_clause,v.descr,\n"
                    + "                    o.penalty1,o.penalty2,o.penalty3,o.offence_applied_on\n"
                    + "                    from " + TableList.VM_OFFENCE_PENALTY + " o \n"
                    + "                    left outer join  " + TableList.VM_OFFENCES + " of on o.offence_cd=of.offence_cd and o.state_cd=of.state_cd\n"
                    + "                     left outer join " + TableList.VM_VH_CLASS + "  v on o.vh_class=v.vh_class\n"
                    + "                     where o.state_cd =? order by o.sr_no ";
            tmgr = new TransactionManager("getOffencePenaltyList");
            pstmtPendingCase = tmgr.prepareStatement(sqlPendigCases);
            pstmtPendingCase.setString(1, Util.getUserStateCode());
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobjoffPnl = new VmOffencePenaltyDobj();
                dobjoffPnl.setSrno(rs.getInt("sr_no"));
                dobjoffPnl.setOldoffcode(rs.getString("offence_desc"));
                dobjoffPnl.setVhclass(rs.getString("descr"));
                dobjoffPnl.setSectioncd(rs.getString("mva_clause"));
                dobjoffPnl.setOpenalty(rs.getInt("penalty1"));
                dobjoffPnl.setDpenalty(rs.getInt("penalty2"));
                dobjoffPnl.setCpenalty(rs.getInt("penalty3"));
                String accused = rs.getString("offence_applied_on");
                for (int i = 0; i < accused.length(); i++) {
                    String code = accused.substring(i, ++i);
                    descr += getAccusedDesc(code) + ",";
                }
                if (descr.endsWith(",")) {
                    descr = descr.substring(0, descr.length() - 1);
                }

                dobjoffPnl.setAccused(descr);
                descr = "";
                offPnlList.add(dobjoffPnl);
            }
        } finally {
            tmgr.release();

        }
        return offPnlList;
    }

    public List<VmSectionsDobj> getSectionList() throws SQLException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement pstmtPendingCase = null;
        VmSectionsDobj dobjSectn = null;
        List<VmSectionsDobj> sectnList = new ArrayList<>();
        try {
            String sqlPendigCases = "select * from " + TableList.VM_SECTION + " where state_cd=? order by section_cd ";
            tmgr = new TransactionManager("getSectionList");
            pstmtPendingCase = tmgr.prepareStatement(sqlPendigCases);
            pstmtPendingCase.setString(1, Util.getUserStateCode());
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobjSectn = new VmSectionsDobj(rs.getInt("section_cd"), rs.getString("level_flag"), rs.getString("section_cd"), rs.getString("is_active"));
                sectnList.add(dobjSectn);
            }
        } finally {
            tmgr.release();

        }
        return sectnList;
    }

    public List<VmOvlScheduleDobj> getOverLoadList() throws SQLException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement pstmtPendingCase = null;
        VmOvlScheduleDobj dobjovl = null;
        List<VmOvlScheduleDobj> ovlList = new ArrayList<>();
        try {
            String sqlPendigCases = "select * from " + TableList.VM_OVERLOAD_SCHEDULE + " where state_cd =? order by sr_no ";
            tmgr = new TransactionManager("getOverLoadList");
            pstmtPendingCase = tmgr.prepareStatement(sqlPendigCases);
            pstmtPendingCase.setString(1, Util.getUserStateCode());
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobjovl = new VmOvlScheduleDobj(rs.getInt("sr_no"), rs.getInt("ld_wt_lower"), rs.getInt("ld_wt_upper"), rs.getString("ld_wt_unit"), rs.getInt("flat_cf_amt"), rs.getInt("unit_cf_amt"), rs.getInt("wt_unit_val"));
                ovlList.add(dobjovl);
            }
        } finally {
            tmgr.release();
        }
        return ovlList;
    }

    public List getVHclassList() throws SQLException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement pstmtPendingCase = null;
        List vh = new ArrayList();
        SelectItem item = new SelectItem("-1", "Select vh");
        try {
            String sqlPendigCases = "select vh_class,descr from " + TableList.VM_VH_CLASS + " order by descr ";
            tmgr = new TransactionManager("getVHclassList");
            pstmtPendingCase = tmgr.prepareStatement(sqlPendigCases);
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                item = new SelectItem(rs.getInt("vh_class"), rs.getString("descr"));
                vh.add(item);
            }
        } finally {
            tmgr.release();

        }
        return vh;
    }

    public List getOffencecomboList() throws SQLException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement pstmtPendingCase = null;
        List vh = new ArrayList();
        SelectItem item = new SelectItem("-1", "Select vh");
        try {
            String sqlPendigCases = "select offence_cd,offence_desc from " + TableList.VM_OFFENCES + " where state_cd =? order by offence_desc ";
            tmgr = new TransactionManager("getOffencecomboList");
            pstmtPendingCase = tmgr.prepareStatement(sqlPendigCases);
            pstmtPendingCase.setString(1, Util.getUserStateCode());
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                item = new SelectItem(rs.getInt("offence_cd"), rs.getString("offence_desc"));
                vh.add(item);
            }
        } finally {
            tmgr.release();

        }
        return vh;
    }

    public List getSectioncomboList() throws SQLException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement pstmtPendingCase = null;
        List sectionList = new ArrayList();
        SelectItem item = new SelectItem("-1", "Select Section");
        try {
            String sqlPendigCases = "select mva_clause,offence_cd  from " + TableList.VM_OFFENCES + "  where state_cd =? ";
            tmgr = new TransactionManager("getSectioncomboList");
            pstmtPendingCase = tmgr.prepareStatement(sqlPendigCases);
            pstmtPendingCase.setString(1, Util.getUserStateCode());
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                item = new SelectItem(rs.getInt("offence_cd"), rs.getString("mva_clause"));
                sectionList.add(item);
            }

        } finally {
            tmgr.release();

        }
        return sectionList;
    }

    public String getOffnCd(String desc) throws SQLException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement pstmtPendingCase = null;
        String code = "";
        try {
            String sqlPendigCases = "select offence_cd from " + TableList.VM_OFFENCES + " where offence_desc=? ";
            tmgr = new TransactionManager("getOffnCd");
            pstmtPendingCase = tmgr.prepareStatement(sqlPendigCases);
            pstmtPendingCase.setString(1, desc);
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                code = Integer.toString(rs.getInt("offence_cd"));
            }
        } finally {
            tmgr.release();
        }
        return code;
    }
    // function to get vehicle class code

    public String getVhClassCode(String desc) throws SQLException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement pstmtPendingCase = null;
        String code = "";
        try {
            String sqlPendigCases = "select vh_class from " + TableList.VM_VH_CLASS + " where descr=? ";
            tmgr = new TransactionManager("getVhClassCode");
            pstmtPendingCase = tmgr.prepareStatement(sqlPendigCases);
            pstmtPendingCase.setString(1, desc);
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                code = Integer.toString(rs.getInt("vh_class"));
            }
        } finally {
            tmgr.release();

        }
        return code;
    }

    public List getUserCodeList() throws SQLException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement pstmtUserCode = null;
        List UserCode = new ArrayList();
        SelectItem item = new SelectItem("-1", "Select");
        try {
            String sqlPendigCases = "select user_cd ,user_name from " + TableList.TM_USER_INFO + "  where state_cd=? and off_cd=? order by user_name ";
            tmgr = new TransactionManager("getUserCodeList");
            pstmtUserCode = tmgr.prepareStatement(sqlPendigCases);
            pstmtUserCode.setString(1, Util.getUserStateCode());
            pstmtUserCode.setInt(2, Util.getSelectedSeat().getOff_cd());
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                item = new SelectItem(rs.getInt("user_cd"), rs.getString("user_name"));
                UserCode.add(item);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        }
        return UserCode;
    }

    public List getofficeList() throws SQLException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement pstmtUserCode = null;
        List officeCode = new ArrayList();
        SelectItem item = new SelectItem("-1", "Select");
        try {
            String sqlPendigCases = "select off_cd,off_name from " + TableList.TM_OFFICE + "  where  state_cd=? order by off_name ";
            tmgr = new TransactionManager("getofficeList");
            pstmtUserCode = tmgr.prepareStatement(sqlPendigCases);
            pstmtUserCode.setString(1, Util.getUserStateCode());
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                item = new SelectItem(rs.getInt("off_cd"), rs.getString("off_name"));
                officeCode.add(item);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return officeCode;
    }

    public List getOfficeCode(int user_cd) throws SQLException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement pstmtUserCode = null;
        List officeCode = new ArrayList();
        SelectItem item = new SelectItem("-1", "Select");
        try {
            String sqlPendigCases = "select off.off_cd,off.off_name from " + TableList.TM_OFFICE + " off\n"
                    + "join " + TableList.TM_USER_INFO + " info on info.off_cd=off.off_cd and info.state_cd=off.state_cd\n"
                    + "where info.user_cd=? and info.state_cd=?";
            tmgr = new TransactionManager("getOfficeCode");
            pstmtUserCode = tmgr.prepareStatement(sqlPendigCases);
            pstmtUserCode.setInt(1, user_cd);
            pstmtUserCode.setString(2, Util.getUserStateCode());
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                item = new SelectItem(rs.getInt("off_cd"), rs.getString("off_name"));
                officeCode.add(item);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return officeCode;
    }

    //function to check this offence already applied to particular vehicle class
    public boolean isOffenceAlreadyApply(String Offence, String vh_class) {
        TransactionManager tmgr = null;
        PreparedStatement pstm = null;
        boolean flag = false;
        try {
            tmgr = new TransactionManager("isOffenceAlreadyApply");
            String sql = "select * from " + TableList.VM_OFFENCE_PENALTY + " where offence_cd=? and vh_class= ? and state_cd=?";
            pstm = tmgr.prepareStatement(sql);
            pstm.setInt(1, Integer.parseInt(Offence));
            pstm.setInt(2, Integer.parseInt(vh_class));
            pstm.setString(3, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                flag = true;
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return flag;
    }
    // function to get accused list

    public Map<String, Object> getAccusedList() throws SQLException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        Map<String, Object> accused = new LinkedHashMap<String, Object>();
        try {
            String sql = "select code,descr  from " + TableList.VM_ACCUSED;
            tmgr = new TransactionManager("getAccusedList");
            pstmt = tmgr.prepareStatement(sql);
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                accused.put(rs.getString("descr"), rs.getString("code"));

            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return accused;
    }
    // function to get accused Description

    public String getAccusedDesc(String code) throws SQLException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement pstmtPendingCase = null;
        String descr = "";
        try {
            String sqlPendigCases = "select descr from " + TableList.VM_ACCUSED + " where code=? ";
            tmgr = new TransactionManager("getAccusedDesc");
            pstmtPendingCase = tmgr.prepareStatement(sqlPendigCases);
            pstmtPendingCase.setString(1, code);
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                descr = rs.getString("descr");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return descr;
    }
    // function to get vehicle class Description

    public String getVhClassDescr(String code) throws SQLException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement pstmtPendingCase = null;
        String descr = "";
        try {
            String sqlPendigCases = "select descr from " + TableList.VM_VH_CLASS + " where vh_class=? ";
            tmgr = new TransactionManager("getVhClassDescr");
            pstmtPendingCase = tmgr.prepareStatement(sqlPendigCases);
            pstmtPendingCase.setInt(1, Integer.parseInt(code));
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                descr = rs.getString("descr");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }


        }
        return descr;
    }
    //function to check this accused already applied to particular vehicle class

    public boolean isAccusedAlreadyApply(String vh_class, String accused, String off_cd) {
        TransactionManager tmgr = null;
        PreparedStatement pstm = null;
        boolean flag = false;
        try {
            tmgr = new TransactionManager("isAccusedAlreadyApply");
            String sql = "select penalty.* from " + TableList.VM_OFFENCE_PENALTY + " penalty\n"
                    + "join " + TableList.VM_OFFENCES + " offence on offence.offence_cd=penalty.offence_cd\n"
                    + "join " + TableList.VM_ACCUSED + " accused on ? =ANY(string_to_array(penalty.offence_applied_on,','))\n"
                    + "where accused.code=? and penalty.vh_class=? and penalty.offence_cd=? and penalty.state_cd=?";
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(1, accused);
            pstm.setString(2, accused);
            pstm.setInt(3, Integer.parseInt(vh_class));
            pstm.setInt(4, Integer.parseInt(off_cd));
            pstm.setString(5, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                flag = true;
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return flag;
    }

    // function to get offence description
    public String getoff_descr(String code) throws SQLException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement pstmt = null;
        String descr = "";
        try {
            String sqlPendigCases = "select offence_desc from " + TableList.VM_OFFENCES + " where offence_cd=? ";
            tmgr = new TransactionManager("getPendingCaseDetails");
            pstmt = tmgr.prepareStatement(sqlPendigCases);
            pstmt.setInt(1, Integer.parseInt(code));
            ResultSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                descr = rs.getString("offence_desc");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return descr;
    }

    //function to get max offence code
    public int getMaxOffenceCode() throws Exception, SQLException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        int maxoffpnlsrno = 0;
        try {
            tmgr = new TransactionManager("fetchchallanNo");
            String maxsrno = "SELECT max(offence_cd)+1 as offence_cd from " + TableList.VM_OFFENCES + " where state_cd =? and offence_cd!=999";
            ps = tmgr.prepareStatement(maxsrno);
            ps.setString(1, Util.getUserStateCode());
            java.sql.ResultSet rs = null;
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                maxoffpnlsrno = rs.getInt("offence_cd");
                if (maxoffpnlsrno == 0) {
                    maxoffpnlsrno = 1;
                }
            }
        } finally {
            tmgr.release();

        }
        return maxoffpnlsrno;

    }
    //function to check book number is already exist in database

    public boolean checkBookNoAlreadyExist(String bookNo, Integer userCOde) throws Exception {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        boolean flag = false;
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("checkBookNoAlreadyExist");
            String maxsrno = " Select * from  " + TableList.VM_CHALLAN_BOOK + "  where book_no = ? and user_cd=? and state_cd=?";
            ps = tmgr.prepareStatement(maxsrno);
            ps.setString(1, bookNo.toUpperCase());
            ps.setInt(2, userCOde);
            ps.setString(3, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                flag = true;
            }
        } finally {
            tmgr.release();
        }
        return flag;
    }
    //check da code is allready exist

    public boolean isDaCodeAlreadyExist(String dacode) throws Exception {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        boolean flag = false;
        RowSet rs = null;
        try {
            tmgr = new TransactionManager("checkBookNoAlreadyExist");
            String maxsrno = " Select * from  " + TableList.VM_DA_PENALTY + "  where da_code = ? and state_cd=?";
            ps = tmgr.prepareStatement(maxsrno);
            ps.setString(1, dacode.toUpperCase());
            ps.setString(2, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                flag = true;
            }
        } finally {
            tmgr.release();
        }
        return flag;
    }
}
