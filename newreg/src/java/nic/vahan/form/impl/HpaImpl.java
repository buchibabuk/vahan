/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.common.Appl_Details_Dobj;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.*;
import org.apache.log4j.Logger;

/**
 *
 * @author AMBRISH
 */
public class HpaImpl {

    private static final Logger LOGGER = Logger.getLogger(HpaImpl.class);

    public HpaDobj set_HPA_appl_db_to_dobj(String appl_no, String regn_no, int pur_cd, String stateCd, int offCd) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        HpaDobj hpa_dobj = null;
        VahanException vahanexecption = null;
        String query;
        String parameterValue;
        boolean vtFlag = false;
        try {

            if (TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE == pur_cd || TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE == pur_cd) {
                query = "Select appl_no, regn_no, sr_no, hp_type, fncr_name, fncr_add1, fncr_add2, "
                        + "       fncr_add3, fncr_district, fncr_pincode, fncr_state, from_dt, op_dt"
                        + "  FROM  " + TableList.VA_HPA
                        + " where appl_no=?";
                parameterValue = appl_no;
                vtFlag = false;
            } else if (TableConstants.VM_TRANSACTION_MAST_TEMP_REG == pur_cd || TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE == pur_cd) {
//                query = "SELECT   sr_no, hp_type, fncr_name, fncr_add1, "
//                        + "       fncr_add2, fncr_village, fncr_taluk, fncr_district, fncr_pincode, "
//                        + "       from_dt, op_dt, state_cd, off_cd "
//                        + "  FROM va_hypth_temp"
//                        + " where appl_no=?";
//                parameterValue = appl_no;

                query = "Select appl_no, regn_no, sr_no, hp_type, fncr_name, fncr_add1, fncr_add2, "
                        + "       fncr_add3, fncr_district, fncr_pincode, fncr_state, from_dt, op_dt"
                        + "  FROM  " + TableList.VA_HPA
                        + " where appl_no=?";
                parameterValue = appl_no;
                vtFlag = false;
            } else {
                query = "SELECT   regn_no, sr_no, hp_type, fncr_name, fncr_add1, fncr_add2, fncr_add3,"
                        + "  fncr_district, fncr_pincode, fncr_state, from_dt, op_dt "
                        + "  FROM  " + TableList.VT_HYPTH
                        + " where regn_no=? and state_cd= ? and off_cd= ? ";
                parameterValue = regn_no;
                vtFlag = true;
            }
            tmgr = new TransactionManager("HPA_Impl");
            ps = tmgr.prepareStatement(query);
            if (vtFlag) {
                ps.setString(1, parameterValue);
                ps.setString(2, stateCd);
                ps.setInt(3, offCd);
            } else {
                ps.setString(1, parameterValue);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                hpa_dobj = new HpaDobj();
                // hpa_dobj.setAppl_no(rs.getString("appl_no"));
                hpa_dobj.setRegn_no(rs.getString("regn_no"));
                hpa_dobj.setSr_no(rs.getInt("sr_no"));
                hpa_dobj.setHp_type(rs.getString("hp_type"));
                hpa_dobj.setFncr_name(rs.getString("fncr_name"));
                hpa_dobj.setFncr_add1(rs.getString("fncr_add1"));
                hpa_dobj.setFncr_add2(rs.getString("fncr_add2"));
                hpa_dobj.setFncr_add3(rs.getString("fncr_add3"));
                hpa_dobj.setFncr_district(rs.getInt("fncr_district"));
                hpa_dobj.setFncr_pincode(rs.getInt("fncr_pincode"));
                hpa_dobj.setFncr_state(rs.getString("fncr_state"));
                hpa_dobj.setFrom_dt(rs.getDate("from_dt"));

            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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

        return hpa_dobj;
    }

    public HpaDobj set_HPA_Entry_appl_db_to_dobj(String appl_no) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        HpaDobj hpa_dobj = null;
        VahanException vahanexecption = null;

        try {

            String query = "SELECT  appl_no, regn_no, sr_no, hp_type, fncr_name, fncr_add1, fncr_add2, fncr_add3, "
                    + "     fncr_state, fncr_district, fncr_pincode, from_dt "
                    + "  FROM " + TableList.VA_HPA + " where appl_no=?";
            String parameterValue = appl_no;

            tmgr = new TransactionManager("HPA_Impl");
            ps = tmgr.prepareStatement(query);
            ps.setString(1, parameterValue);
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                hpa_dobj = new HpaDobj();

                hpa_dobj.setAppl_no(rs.getString("appl_no"));
                hpa_dobj.setRegn_no(rs.getString("regn_no"));
                hpa_dobj.setFrom_dt(rs.getDate("from_dt"));
                hpa_dobj.setHp_type(rs.getString("hp_type"));
                hpa_dobj.setSr_no(rs.getInt("sr_no"));
                hpa_dobj.setFncr_add1(rs.getString("fncr_add1"));
                hpa_dobj.setFncr_add2(rs.getString("fncr_add2"));
                hpa_dobj.setFncr_add3(rs.getString("fncr_add3"));
                hpa_dobj.setFncr_district(rs.getInt("fncr_district"));
                hpa_dobj.setFncr_name(rs.getString("fncr_name"));
                hpa_dobj.setFncr_pincode(rs.getInt("fncr_pincode"));
                hpa_dobj.setFncr_state(rs.getString("fncr_state"));

            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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

        return hpa_dobj;
    }

    public List<HpaDobj> getHypoDetails(String appl_no, int pur_cd, String regn_no, boolean eapp, String stateCd) throws SQLException, Exception {

        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        HpaDobj hpa_dobj = null;
        List<HpaDobj> listHypoDetails = new ArrayList<>();

        if (appl_no != null) {
            appl_no = appl_no.toUpperCase();
        }

        if (regn_no != null) {
            regn_no = regn_no.toUpperCase();
        }

        try {

            tmgr = new TransactionManager("getHypoDetails");

            if (pur_cd == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO) {
                sql = " SELECT  appl_no,regn_no,sr_no,hp_type,h.hp_type_descr,fncr_name,fncr_add1,fncr_add2,fncr_add3,"
                        + "         fncr_district,d.descr as fncr_district_descr,fncr_state,s.descr as fncr_state_name,fncr_pincode,"
                        + "         from_dt,to_char(from_dt,'DD-MON-YYYY') as from_dt_descr"
                        + "   FROM  " + TableList.VA_HPA
                        + "         left join tm_state s on fncr_state = s.state_code "
                        + "         left join tm_district d on fncr_district = d.dist_cd "
                        + "         left join vm_hp_type h on hp_type = h.hp_type_cd "
                        + "  where  appl_no=? order by sr_no";
            } else if (pur_cd == TableConstants.VM_TRANSACTION_MAST_REM_HYPO && !eapp) {
                sql = " SELECT   regn_no,sr_no,hp_type,h.hp_type_descr,fncr_name,fncr_add1,fncr_add2,fncr_add3,"
                        + "      fncr_district,d.descr as fncr_district_descr,"
                        + "      fncr_state,s.descr as fncr_state_name,fncr_pincode,from_dt,to_char(from_dt,'DD-MON-YYYY') as from_dt_descr "
                        + " FROM " + TableList.VT_HYPTH + " hpt "
                        + "       left join tm_state s on fncr_state = s.state_code "
                        + "       left join tm_district d on fncr_district = d.dist_cd"
                        + "       left join vm_hp_type h on hp_type = h.hp_type_cd"
                        + " where regn_no=(select distinct regn_no from va_details where appl_no=?) "
                        + " and hpt.state_cd= ? order by sr_no";
            }

            if (pur_cd == TableConstants.VM_TRANSACTION_MAST_REM_HYPO && eapp) {
                sql = " SELECT   regn_no,sr_no,hp_type,h.hp_type_descr,fncr_name,fncr_add1,fncr_add2,fncr_add3,"
                        + "      fncr_district,d.descr as fncr_district_descr ,"
                        + "      fncr_state,s.descr as fncr_state_name,fncr_pincode,from_dt,to_char(from_dt,'DD-MON-YYYY') as from_dt_descr "
                        + " FROM " + TableList.VT_HYPTH + " hpt "
                        + "       left join tm_state s on fncr_state = s.state_code "
                        + "       left join tm_district d on fncr_district = d.dist_cd"
                        + "       left join vm_hp_type h on hp_type = h.hp_type_cd"
                        + " where regn_no=? and hpt.state_cd= ? order by sr_no";
            }

            ps = tmgr.prepareStatement(sql);
            if (pur_cd == TableConstants.VM_TRANSACTION_MAST_REM_HYPO && eapp) {
                ps.setString(1, regn_no);
                ps.setString(2, stateCd);
            } else if (pur_cd == TableConstants.VM_TRANSACTION_MAST_REM_HYPO && !eapp) {
                ps.setString(1, appl_no);
                ps.setString(2, stateCd);
            } else {
                ps.setString(1, appl_no);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) // found
            {

                hpa_dobj = new HpaDobj();

                if (pur_cd == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO) {
                    hpa_dobj.setAppl_no(rs.getString("appl_no"));
                }
                hpa_dobj.setRegn_no(rs.getString("regn_no"));
                hpa_dobj.setSr_no(rs.getInt("sr_no"));
                hpa_dobj.setFncr_add1(rs.getString("fncr_add1"));
                hpa_dobj.setFncr_add2(rs.getString("fncr_add2"));
                hpa_dobj.setFncr_add3(rs.getString("fncr_add3"));
                hpa_dobj.setFncr_name(rs.getString("fncr_name"));
                hpa_dobj.setFncr_district(rs.getInt("fncr_district"));
                hpa_dobj.setFncr_district_descr(rs.getString("fncr_district_descr"));
                hpa_dobj.setFncr_state(rs.getString("fncr_state"));
                hpa_dobj.setFncr_state_name(rs.getString("fncr_state_name"));
                hpa_dobj.setFncr_pincode(rs.getInt("fncr_pincode"));
                hpa_dobj.setFrom_dt(rs.getDate("from_dt"));
                hpa_dobj.setFrom_dt_descr(rs.getString("from_dt_descr"));
                hpa_dobj.setHp_type(rs.getString("hp_type"));
                hpa_dobj.setHp_type_descr(rs.getString("hp_type_descr"));

                listHypoDetails.add(hpa_dobj);

            }

        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return listHypoDetails;
    }

    public List<HpaDobj> getHptDetails(String appl_no) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        HpaDobj hpa_dobj = null;
        VahanException vahanexecption = null;
        List<HpaDobj> listHptDetails = new ArrayList<>();

        String query_va_hpt = "select * from " + TableList.VVA_HPT + " where appl_no=? order by sr_no";

        try {

            tmgr = new TransactionManager("getHptDetails");
            ps = tmgr.prepareStatement(query_va_hpt);
            ps.setString(1, appl_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) // found
            {
                hpa_dobj = new HpaDobj();
                hpa_dobj.setAppl_no(rs.getString("appl_no"));
                hpa_dobj.setRegn_no(rs.getString("regn_no"));
                hpa_dobj.setSr_no(rs.getInt("sr_no"));
                hpa_dobj.setFncr_add1(rs.getString("fncr_add1"));
                hpa_dobj.setFncr_add2(rs.getString("fncr_add2"));
                hpa_dobj.setFncr_add3(rs.getString("fncr_add3"));
                hpa_dobj.setFncr_name(rs.getString("fncr_name"));
                hpa_dobj.setFncr_pincode(rs.getInt("fncr_pincode"));
                hpa_dobj.setFncr_district(rs.getInt("fncr_district"));
                hpa_dobj.setFncr_district_descr(rs.getString("fncr_district_name"));
                hpa_dobj.setFncr_state(rs.getString("fncr_state"));
                hpa_dobj.setFncr_state_name(rs.getString("fncr_state_name"));
                hpa_dobj.setFrom_dt(rs.getDate("from_dt"));
                hpa_dobj.setFrom_dt_descr(rs.getString("from_dt_descr"));
                hpa_dobj.setUpto_dt(rs.getDate("upto_dt"));
                hpa_dobj.setUpto_dt_descr(rs.getString("upto_dt_descr"));
                hpa_dobj.setHp_type(rs.getString("hp_type"));
                hpa_dobj.setHp_type_descr(rs.getString("hp_type_descr"));

                listHptDetails.add(hpa_dobj);

            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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

        return listHptDetails;

    }

    public void update_HPA_Status(List<HpaDobj> hpa_dobj_list, Status_dobj status_dobj, String changedData, Appl_Details_Dobj applDtlsDobj) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        try {

            if (applDtlsDobj == null
                    || applDtlsDobj.getCurrent_state_cd() == null
                    || applDtlsDobj.getCurrent_off_cd() == 0) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }

            tmgr = new TransactionManager("update_HPA_Status");
            //=================WEB SERVICES FOR NEXTSTAGE START=========//
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);

            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_ENTRY
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_VERIFICATION
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL) {
                insertUpdateHPA(tmgr, hpa_dobj_list, applDtlsDobj.getCurrent_state_cd(), applDtlsDobj.getCurrent_off_cd());//save when there is a change.....pending....
            }

            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                //if data already in vt_hypth for same regn_no
                int max_sr = 0;
                sql = "select max(sr_no) as max_sr from " + TableList.VT_HYPTH + "  where regn_no= ? and state_cd= ? and off_cd= ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, hpa_dobj_list.get(0).getRegn_no());
                ps.setString(2, applDtlsDobj.getCurrent_state_cd());
                ps.setInt(3, applDtlsDobj.getCurrent_off_cd());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) // found
                {
                    max_sr = rs.getInt("max_sr");
                }

                for (int i = 0; i < hpa_dobj_list.size(); i++) {

                    sql = "INSERT into  " + TableList.VT_HYPTH
                            + " SELECT ?,?,regn_no,?,hp_type,fncr_name,fncr_add1,fncr_add2,fncr_add3,"
                            + " fncr_district,fncr_pincode,fncr_state,from_dt,"
                            + " current_timestamp as op_dt "
                            + " FROM " + TableList.VA_HPA + " where appl_no=? and sr_no = ?";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, applDtlsDobj.getCurrent_state_cd());
                    ps.setInt(2, applDtlsDobj.getCurrent_off_cd());
                    ps.setInt(3, max_sr + i + 1);
                    ps.setString(4, hpa_dobj_list.get(i).getAppl_no());
                    ps.setInt(5, i + 1);
                    ps.executeUpdate();
                }

                for (int i = 0; i < hpa_dobj_list.size(); i++) {
                    sql = "INSERT INTO " + TableList.VHA_HPA
                            + " SELECT  current_timestamp + interval '1 second' as moved_on, ? as moved_by ,state_cd,off_cd, appl_no, regn_no, sr_no, hp_type, fncr_name, fncr_add1, fncr_add2, "
                            + "        fncr_add3, fncr_district, fncr_pincode, fncr_state, from_dt, "
                            + "        op_dt FROM " + TableList.VA_HPA
                            + "  WHERE appl_no = ? and sr_no=?";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, applDtlsDobj.getCurrentEmpCd());
                    ps.setString(2, hpa_dobj_list.get(i).getAppl_no());
                    ps.setInt(3, i + 1);
                    ps.executeUpdate();
                }

                sql = "DELETE FROM " + TableList.VA_HPA + " where appl_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, hpa_dobj_list.get(0).getAppl_no());
                ps.executeUpdate();

                //for updating the status of application when it is approved start
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                //for updating the status of application when it is approved end
                //SmartCard Or Print
                ServerUtil.VerifyInsertSmartCardPrintDetail(hpa_dobj_list.get(0).getAppl_no(), hpa_dobj_list.get(0).getRegn_no(),
                        applDtlsDobj.getCurrent_state_cd(), applDtlsDobj.getCurrent_off_cd(), status_dobj.getPur_cd(), tmgr);
            }

            insertIntoVhaChangedData(tmgr, hpa_dobj_list.get(0).getAppl_no(), changedData);//for saving the data into table those are changed by the user

            ServerUtil.fileFlow(tmgr, status_dobj); //for updateing va_status and vha status for new role,seat for new emp

            tmgr.commit();//Commiting data here....

        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
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
    }

    public void makeChange_hpa_enrty(List<HpaDobj> hpa_dobj_list, String stateCd, int offCd, String changedata) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("makeChange_hpa_enrty");
            insertUpdateHPA(tmgr, hpa_dobj_list, stateCd, offCd);
            ComparisonBeanImpl.updateChangedData(hpa_dobj_list.get(0).getAppl_no(), changedata, tmgr);
            tmgr.commit();
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
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
    }

    public void makeChange_hpt(List<HpaDobj> hpt_dobj_list, String changedata) throws SQLException, Exception {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("makeChangeHPT");
            insertUpdateHPT(tmgr, hpt_dobj_list);
            ComparisonBeanImpl.updateChangedData(hpt_dobj_list.get(0).getAppl_no(), changedata, tmgr);
            tmgr.commit();
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }


    }

    public void update_HPT_Status(List<HpaDobj> hpt_dobj_list, Status_dobj status_dobj, String changedData) throws SQLException, Exception {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;

        try {
            //=================WEB SERVICES FOR NEXTSTAGE START=========//
            tmgr = new TransactionManager("update_HPT_Status");
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);

            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_ENTRY
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_VERIFICATION
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL) {

                insertUpdateHPT(tmgr, hpt_dobj_list);//save when there is a change or new entry for HPT..pending
            }

            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                for (int i = 0; i < hpt_dobj_list.size(); i++) {
                    sql = "INSERT INTO  " + TableList.VH_HPT
                            + "     SELECT a.state_cd, a.off_cd, ?, a.regn_no, a.sr_no, a.hp_type, a.fncr_name, a.fncr_add1, a.fncr_add2, a.fncr_add3,"
                            + "            a.fncr_district, a.fncr_state, a.fncr_pincode, a.from_dt,"
                            + "            ?, current_timestamp as term_dt, current_timestamp as moved_on, ? as moved_by "
                            + "      FROM " + TableList.VT_HYPTH + " a "
                            + "      WHERE a.regn_no=? and a.sr_no =? and a.state_cd = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, hpt_dobj_list.get(i).getAppl_no());
                    ps.setDate(2, new java.sql.Date(hpt_dobj_list.get(i).getUpto_dt().getTime()));
                    ps.setString(3, Util.getEmpCode());
                    ps.setString(4, hpt_dobj_list.get(i).getRegn_no());
                    ps.setInt(5, hpt_dobj_list.get(i).getSr_no());
                    ps.setString(6, Util.getUserStateCode());
                    ps.executeUpdate();

                    sql = "delete from " + TableList.VT_HYPTH + " where regn_no=? and sr_no=? and state_cd = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, hpt_dobj_list.get(i).getRegn_no());
                    ps.setInt(2, hpt_dobj_list.get(i).getSr_no());
                    ps.setString(3, Util.getUserStateCode());
                    ps.executeUpdate();
                }

                for (int i = 0; i < hpt_dobj_list.size(); i++) {

                    sql = "INSERT INTO " + TableList.VHA_HPT
                            + " SELECT current_timestamp + interval '1 second' as moved_on, ? as moved_by,state_cd,off_cd, appl_no, regn_no, sr_no, hp_type, fncr_name, "
                            + " fncr_add1, fncr_add2,fncr_add3, "
                            + " fncr_district, fncr_state, fncr_pincode, from_dt, "
                            + " upto_dt,op_dt "
                            + " FROM " + TableList.VA_HPT + " where appl_no = ? and sr_no = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getEmpCode());
                    ps.setString(2, hpt_dobj_list.get(i).getAppl_no());
                    ps.setInt(3, hpt_dobj_list.get(i).getSr_no());
                    ps.executeUpdate();
                }

                sql = "DELETE FROM " + TableList.VA_HPT + " where appl_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, hpt_dobj_list.get(0).getAppl_no());
                ps.executeUpdate();

                //Approve Insurance
                InsImpl.approvalInsurance(tmgr, hpt_dobj_list.get(0).getRegn_no(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), Util.getEmpCode());

                //for updating the status of application when it is approved start
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                //for updating the status of application when it is approved end
                if (ServerUtil.verifyForSmartCard(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), tmgr)) {
                    if (!Util.getTmConfiguration().isSmartcard_fee_at_vendor() && !SmartCardImpl.isSmartCardFeePaid(hpt_dobj_list.get(0).getRegn_no(), tmgr)) {
                        String latestSmart = ServerUtil.getLatestSmartCardSerialNo(hpt_dobj_list.get(0).getRegn_no());
                        if (latestSmart == null) {
                            throw new VahanException("First time preparing the Smart Card, Please first Deposit the Smart Card Fee with the Balance Fee Option.");
                        }
                    }
                }

                //SmartCard Or Print
                ServerUtil.VerifyInsertSmartCardPrintDetail(hpt_dobj_list.get(0).getAppl_no(), hpt_dobj_list.get(0).getRegn_no(),
                        Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), status_dobj.getPur_cd(), tmgr);

            }

            insertIntoVhaChangedData(tmgr, hpt_dobj_list.get(0).getAppl_no(), changedData);//for saving the data into table those are changed by the user

            ServerUtil.fileFlow(tmgr, status_dobj); //for updateing va_status and vha status for new role,seat for new emp

            tmgr.commit();//Commiting data here....

        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

    }

    /**
     *
     *
     * @param tmgr
     * @param dobj
     * @param appl_no
     *
     * Note : This method is work under the registration of new vwhicle so
     * commit / rollback is managed by the parent process i.e New_Impl.java who
     * is basically passing the transacation manager object to this function
     *
     */
    public static void updateHPA(TransactionManager tmgr, HpaDobj dobj, String appl_no) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;

        sql = "UPDATE " + TableList.VA_HPA
                + "   SET hp_type=?, fncr_name=?, fncr_add1=?, fncr_add2=?, fncr_add3=?, "
                + "       fncr_state=?, fncr_district=?, fncr_pincode=?,"
                + "       from_dt=?, op_dt=current_timestamp "
                + " WHERE appl_no=? and sr_no = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getHp_type());
        ps.setString(2, dobj.getFncr_name());
        ps.setString(3, dobj.getFncr_add1());
        ps.setString(4, dobj.getFncr_add2());
        ps.setString(5, dobj.getFncr_add3());
        ps.setString(6, dobj.getFncr_state());
        ps.setInt(7, dobj.getFncr_district());
        ps.setInt(8, dobj.getFncr_pincode());
        ps.setDate(9, new java.sql.Date(dobj.getFrom_dt().getTime()));
        ps.setString(10, appl_no);
        ps.setInt(11, dobj.getSr_no());
        ps.executeUpdate();
    } // end of updateHPA

    public static void insertIntoHPAHistory(TransactionManager tmgr, String appl_no, int sr_no) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VHA_HPA
                + " SELECT  current_timestamp as moved_on, ? as moved_by, state_cd, off_cd,  appl_no, regn_no, sr_no, hp_type, fncr_name, fncr_add1, fncr_add2, "
                + "        fncr_add3, fncr_district, fncr_pincode, fncr_state, from_dt, "
                + "        op_dt FROM " + TableList.VA_HPA
                + "  WHERE appl_no = ? and sr_no=?";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.setInt(3, sr_no);
        ps.executeUpdate();

    } // end of insertIntoHPAHistory

    public static void insertIntoHPAHistory(TransactionManager tmgr, String appl_no) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VHA_HPA
                + " SELECT  current_timestamp as moved_on, ? as moved_by , state_cd ,off_cd , appl_no, regn_no, sr_no, hp_type, fncr_name, fncr_add1, fncr_add2, "
                + "        fncr_add3, fncr_district, fncr_pincode, fncr_state, from_dt, "
                + "        op_dt FROM " + TableList.VA_HPA
                + "  WHERE appl_no = ?";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();

    } // end of insertIntoHPAHistory

    public static void insertIntoHPA(TransactionManager tmgr, HpaDobj dobj, String stateCd, int offCd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            HpaImpl hpaImpl = new HpaImpl();
            int maxSrNo = hpaImpl.getHpaMaxSrNo(dobj.getAppl_no(), tmgr);

            sql = "INSERT INTO " + TableList.VA_HPA + " ( state_cd, off_cd, appl_no, regn_no, sr_no, hp_type, fncr_name, fncr_add1, fncr_add2, "
                    + "            fncr_add3, fncr_district, fncr_pincode, fncr_state, from_dt, op_dt) "
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp) ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, offCd);
            ps.setString(3, dobj.getAppl_no());
            ps.setString(4, dobj.getRegn_no());
            ps.setInt(5, maxSrNo + 1);
            ps.setString(6, dobj.getHp_type());
            ps.setString(7, dobj.getFncr_name());
            ps.setString(8, dobj.getFncr_add1());
            ps.setString(9, dobj.getFncr_add2());
            ps.setString(10, dobj.getFncr_add3());
            ps.setInt(11, dobj.getFncr_district());
            ps.setInt(12, dobj.getFncr_pincode() == null ? 0 : dobj.getFncr_pincode());
            ps.setString(13, dobj.getFncr_state());
            ps.setDate(14, new java.sql.Date(dobj.getFrom_dt().getTime()));
            ps.executeUpdate();
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    } // end of insertIntoHPA

    public static void insertUpdateHPA(TransactionManager tmgr, List<HpaDobj> hpa_dobj_list, String stateCd, int offCd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            if (hpa_dobj_list == null) {
                return;
            }
            sql = "SELECT regn_no FROM  " + TableList.VA_HPA + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, hpa_dobj_list.get(0).getAppl_no());

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) { //if any record is exist then update otherwise insert it

                for (int i = 0; i < hpa_dobj_list.size(); i++) {
                    if (hpa_dobj_list.get(i).getSr_no() == 0) { // if new record added HPA_dobj_list.get(i).setSr_no(old+1);
                        insertIntoHPA(tmgr, hpa_dobj_list.get(i), stateCd, offCd);
                    } else {
                        insertIntoHPAHistory(tmgr, hpa_dobj_list.get(i).getAppl_no(), hpa_dobj_list.get(i).getSr_no());
                        updateHPA(tmgr, hpa_dobj_list.get(i), hpa_dobj_list.get(i).getAppl_no());
                    }
                }
            } else {
                for (int i = 0; i < hpa_dobj_list.size(); i++) {
                    insertIntoHPA(tmgr, hpa_dobj_list.get(i), stateCd, offCd);
                }
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    } // end of insertUpdateHPA

    public void insertUpdateHPT(TransactionManager tmgr, List<HpaDobj> hpt_dobj_list) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;

        sql = "SELECT appl_no FROM " + TableList.VA_HPT + " where appl_no = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, hpt_dobj_list.get(0).getAppl_no());//this need to be updated...hpt can be from diffrent appl_no
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();

        if (rs.next()) { //if any record is exist then update otherwise insert it
            for (int i = 0; i < hpt_dobj_list.size(); i++) {
                insertInto_HPT_History(tmgr, hpt_dobj_list.get(i).getAppl_no(), hpt_dobj_list.get(i).getSr_no());
                update_HPT(tmgr, hpt_dobj_list.get(i), hpt_dobj_list.get(i).getAppl_no());
            }
        } else {
            for (int i = 0; i < hpt_dobj_list.size(); i++) {
                insertInto_HPT(tmgr, hpt_dobj_list.get(i), hpt_dobj_list.get(i).getAppl_no(), hpt_dobj_list.get(i).getRegn_no());
            }
        }
    } // end of insertUpdateHPT

    /**
     * *
     * Note Hypothecation Termination is sharing the dobj class of HPA so be
     * carefull.
     *
     * @param tmgr
     * @param appl_no
     * @param hpt_dobj
     */
    public void insertInto_HPT_History(TransactionManager tmgr, String appl_no, int sr_no) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VHA_HPT
                + " SELECT current_timestamp as moved_on, ? as moved_by, state_cd, off_cd, appl_no, regn_no, sr_no, hp_type, fncr_name, "
                + " fncr_add1, fncr_add2,fncr_add3, "
                + " fncr_district, fncr_state, fncr_pincode, from_dt, "
                + " upto_dt ,op_dt"
                + " FROM " + TableList.VA_HPT + " where appl_no = ? and sr_no = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.setInt(3, sr_no);
        ps.executeUpdate();
    }

    public static void insertInto_HPT_History(TransactionManager tmgr, String appl_no) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VHA_HPT
                + " SELECT current_timestamp as moved_on, ? as moved_by ,state_cd,off_cd, appl_no, regn_no, sr_no, hp_type, fncr_name, "
                + " fncr_add1, fncr_add2,fncr_add3, "
                + " fncr_district, fncr_state, fncr_pincode, from_dt, "
                + " upto_dt, op_dt"
                + " FROM " + TableList.VA_HPT + " where appl_no = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public void update_HPT(TransactionManager tmgr, HpaDobj hpt_dobj, String appl_no) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;

        sql = "UPDATE " + TableList.VA_HPT
                + " SET upto_dt=?,op_dt=current_timestamp"
                + " WHERE appl_no=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setDate(1, new java.sql.Date(hpt_dobj.getUpto_dt().getTime()));
        ps.setString(2, appl_no);
        ps.executeUpdate();
    } // end of updateHPT

    public void insertInto_HPT(TransactionManager tmgr, HpaDobj hpt_dobj, String appl_no, String regn_no) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;
        sql = "select op_dt,from_dt from " + TableList.VT_HYPTH + "  where regn_no= ? and state_cd= ? order by(op_dt,from_dt) desc limit 1";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, hpt_dobj.getRegn_no());
        ps.setString(2, Util.getUserStateCode());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        Date fromDate = null, opDate = null;
        if (rs.next()) {
            opDate = rs.getDate("op_dt");
            fromDate = rs.getDate("from_dt");
        }
        sql = "INSERT INTO " + TableList.VA_HPT
                + " (state_cd, off_cd, appl_no, regn_no, sr_no, hp_type, fncr_name, fncr_add1, fncr_add2,"
                + " fncr_add3, fncr_district, fncr_state, fncr_pincode, from_dt, upto_dt, op_dt) "
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,current_timestamp)";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getUserStateCode());
        ps.setInt(2, Util.getSelectedSeat().getOff_cd());
        ps.setString(3, appl_no);
        ps.setString(4, regn_no);
        ps.setInt(5, hpt_dobj.getSr_no());
        ps.setString(6, hpt_dobj.getHp_type());
        ps.setString(7, hpt_dobj.getFncr_name());
        ps.setString(8, hpt_dobj.getFncr_add1());
        ps.setString(9, hpt_dobj.getFncr_add2());
        ps.setString(10, hpt_dobj.getFncr_add3());
        ps.setInt(11, hpt_dobj.getFncr_district());
        ps.setString(12, hpt_dobj.getFncr_state());
        ps.setInt(13, hpt_dobj.getFncr_pincode());
        if (fromDate == null) {
            ps.setDate(14, opDate);
        } else {
            ps.setDate(14, new java.sql.Date(hpt_dobj.getFrom_dt().getTime()));
        }
        ps.setDate(15, new java.sql.Date(hpt_dobj.getUpto_dt().getTime()));
        ps.executeUpdate();
    } // end of insertIntoHPT

    public static void insertUpdateHPA_Temp(TransactionManager tmgr, HpaDobj hpa_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {

            sql = "SELECT * FROM va_hypth_temp where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, hpa_dobj.getAppl_no());

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) { //if any record is exist then update otherwise insert it
                insertIntoHPAHistory_Temp(tmgr, hpa_dobj.getAppl_no());
                updateHPA_Temp(tmgr, hpa_dobj, hpa_dobj.getAppl_no());
            } else {
                insertIntoHPA_Temp(tmgr, hpa_dobj);
            }

        } catch (SQLException sqle) {
            throw new VahanException(sqle.getMessage());
        }
    } // end of insertUpdateHPA

    public static void insertIntoHPAHistory_Temp(TransactionManager tmgr, String appl_no) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;

        try {
            sql = "INSERT INTO vha_hypth_temp "
                    + "SELECT appl_no , temp_regn_no ,"
                    + "  sr_no ,  hp_type ,"
                    + "  fncr_name ,  fncr_add1 ,"
                    + "  fncr_add2 ,  fncr_village ,"
                    + "  fncr_taluk ,  fncr_district ,"
                    + "  fncr_pincode ,  from_dt ,"
                    + "  op_dt,current_timestamp as moved_on,? as moved_by, state_cd, off_cd"
                    + "  FROM va_hypth_temp where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            ps.executeUpdate();
        } catch (SQLException sqle) {
            throw new VahanException(sqle.getMessage());
        }
    } // end of insertIntoHPAHistory

    public static void updateHPA_Temp(TransactionManager tmgr, HpaDobj dobj, String appl_no) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "UPDATE va_hypth_temp "
                    + "   SET hp_type=?, fncr_name=?, fncr_add1=?, fncr_add2=?, "
                    + "       fncr_district=?, fncr_pincode=?,"
                    + "       from_dt=?, op_dt=current_timestamp "
                    + " WHERE appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getHp_type());
            ps.setString(2, dobj.getFncr_name());
            ps.setString(3, dobj.getFncr_add1());
            ps.setString(4, dobj.getFncr_add2());
            ps.setInt(5, dobj.getFncr_district());
            ps.setInt(6, dobj.getFncr_pincode());
            ps.setDate(7, new java.sql.Date(dobj.getFrom_dt().getTime()));
            ps.setString(8, appl_no);
            ps.executeUpdate();
        } catch (SQLException sqle) {
            throw new VahanException(sqle.getMessage());
        }
    }

    public static void insertIntoHPA_Temp(TransactionManager tmgr, HpaDobj dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "INSERT INTO va_hypth_temp(appl_no, temp_regn_no, sr_no, hp_type, fncr_name, fncr_add1, fncr_add2,"
                    + "            fncr_district, fncr_pincode, from_dt, op_dt, state_cd, off_cd)"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp, ?, ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getAppl_no());
            ps.setString(2, dobj.getRegn_no());
            ps.setInt(3, dobj.getSr_no() + 1);
            ps.setString(4, dobj.getHp_type());
            ps.setString(5, dobj.getFncr_name());
            ps.setString(6, dobj.getFncr_add1());
            ps.setString(7, dobj.getFncr_add2());
            ps.setInt(8, dobj.getFncr_district());
            ps.setInt(9, dobj.getFncr_pincode());
            ps.setDate(10, new java.sql.Date(dobj.getFrom_dt().getTime()));
            ps.setString(11, Util.getUserStateCode());
            ps.setInt(12, Util.getSelectedSeat().getOff_cd());
            ps.executeUpdate();
        } catch (SQLException sqle) {
            throw new VahanException(sqle.getMessage());
        }
    }

    public static void insertIntoVtHypthFromVaHpa(TransactionManager tmgr, Status_dobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT into  " + TableList.VT_HYPTH
                + " select state_cd,off_cd ,regn_no, sr_no, hp_type, fncr_name, fncr_add1, fncr_add2, fncr_add3, "
                + "       fncr_district, fncr_pincode, fncr_state, from_dt,current_timestamp as op_dt"
                + " FROM  " + TableList.VA_HPA
                + " where appl_no=?";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getAppl_no());
        ps.executeUpdate();

    } // end of insertIntoVtHypthFromVaHpa

    public static void insertIntoHypthVH(TransactionManager tmgr, Status_dobj dobj, String stateCode, int offCode, String oldRegn) throws SQLException {
        PreparedStatement ps = null;
        int pos = 1;

        String sql = "INSERT INTO " + TableList.VH_HYPTH
                + " SELECT state_cd, off_cd, regn_no, sr_no, hp_type, fncr_name, fncr_add1,"
                + "       fncr_add2, fncr_add3, fncr_district, fncr_pincode, fncr_state,"
                + "       from_dt, op_dt, ? as appl_no, current_timestamp as moved_on, ? as moved_by "
                + "  FROM " + TableList.VT_HYPTH + " WHERE regn_no = ? and state_cd = ? and off_cd=?";

        ps = tmgr.prepareStatement(sql);

        ps.setString(pos++, dobj.getAppl_no());
        ps.setString(pos++, String.valueOf(dobj.getEmp_cd()));
        if (!CommonUtils.isNullOrBlank(oldRegn)) {
            ps.setString(pos++, oldRegn);
        } else {
            ps.setString(pos++, dobj.getRegn_no());
        }
        ps.setString(pos++, stateCode);
        ps.setInt(pos++, offCode);
        ps.executeUpdate();

    } // end of insertIntoHypthVH

    public static void deleteFromVtHypth(TransactionManager tmgr, String regnNo, String stateCode, int offCode) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "DELETE FROM " + TableList.VT_HYPTH + " WHERE regn_no=? and state_cd = ? and off_cd=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, regnNo);
        ps.setString(2, stateCode);
        ps.setInt(3, offCode);
        ps.executeUpdate();

    }

    public static void insertDeleteFromVaHpa(TransactionManager tmgr, String applNo) throws SQLException {
        PreparedStatement psVaHpa = null;
        PreparedStatement psVhaHpa = null;
        String vaHpasql = null;
        String vhaHpasql = null;


        vhaHpasql = "INSERT INTO " + TableList.VHA_HPA
                + " SELECT  current_timestamp as moved_on, ? as moved_by, state_cd , off_cd, appl_no, regn_no, sr_no, hp_type, fncr_name, fncr_add1, fncr_add2, "
                + " fncr_add3, fncr_district, fncr_pincode, fncr_state, from_dt, op_dt FROM " + TableList.VA_HPA + " WHERE appl_no = ? ";

        psVhaHpa = tmgr.prepareStatement(vhaHpasql);
        psVhaHpa.setString(1, String.valueOf(Util.getEmpCode()));
        psVhaHpa.setString(2, applNo);
        psVhaHpa.executeUpdate();

        vaHpasql = "DELETE FROM " + TableList.VA_HPA + " WHERE appl_no=?";
        psVaHpa = tmgr.prepareStatement(vaHpasql);
        psVaHpa.setString(1, applNo);
        psVaHpa.executeUpdate();
    }
    // end of deleteFromVtHypth

    public void updateConfirmStatus(String applNo, int purCode, String statusFlag) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        try {
            tmgr = new TransactionManager("updateConfirmStatus");
            sql = "UPDATE " + TableList.VA_DETAILS
                    + "   SET  confirm_status=? "
                    + " WHERE  appl_no=? and pur_cd=?";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, statusFlag);
            ps.setString(2, applNo);
            ps.setInt(3, purCode);
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

    public static boolean isVehicleDestroyedOrNot(String applNo, int purCode) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        boolean flag = false;
        String sql = null;
        try {
            tmgr = new TransactionManager("isVehicleDestroyedOrNot");
            sql = "Select confirm_status from " + TableList.VA_DETAILS
                    + " WHERE  appl_no=? and pur_cd=?";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setInt(2, purCode);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if (rs.getString("confirm_status").equalsIgnoreCase("D")) {
                    flag = true;
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
        return flag;
    }

    public List<HpaDobj> getHypothecationList(String regnNo, String stateCd, int offCd) throws VahanException {
        List<HpaDobj> listHpa = new ArrayList<>();
        HpaDobj dobj = null;
        String sql = "Select * from " + TableList.VT_HYPTH + " where regn_no=? and state_cd = ? and off_cd = ? ";
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getHypothecationList");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                dobj = new HpaDobj();
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setSr_no(rs.getInt("sr_no"));
                dobj.setHp_type(rs.getString("hp_type"));
                dobj.setFncr_name(rs.getString("fncr_name"));
                dobj.setFncr_add1(rs.getString("fncr_add1"));
                dobj.setFncr_add2(rs.getString("fncr_add2"));
                dobj.setFncr_add3(rs.getString("fncr_add3"));
                dobj.setFncr_district(rs.getInt("fncr_district"));
                dobj.setFncr_pincode(rs.getInt("fncr_pincode"));
                dobj.setFncr_state(rs.getString("fncr_state"));
                dobj.setFrom_dt(rs.getDate("from_dt"));
                //dobj.setOp_dt(rs.getString("op_dt"));
                listHpa.add(dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching of Hypothecation List");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        if (listHpa.isEmpty()) {
            return null;
        }

        return listHpa;

    }

    public int maxHypothecationNo(String applNo, String regnNo, int purCd) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        int total = 1;
        String sql = null;
        try {
            tmgr = new TransactionManager("maxHypothecationNo");

            if (purCd == TableConstants.VM_TRANSACTION_MAST_ADD_HYPO) {
                sql = "SELECT count(1) as total FROM " + TableList.VA_HPA
                        + " WHERE regn_no=? and appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regnNo);
                ps.setString(2, applNo);
            }

            if (purCd == TableConstants.VM_TRANSACTION_MAST_REM_HYPO) {
                sql = "SELECT count(1) as total FROM " + TableList.VA_HPT
                        + " WHERE regn_no=? and appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regnNo);
                ps.setString(2, applNo);
            }

            if (purCd == TableConstants.VM_TRANSACTION_MAST_HPC) {
                sql = "SELECT count(1) as total FROM " + TableList.VT_HYPTH
                        + " WHERE regn_no=? and state_cd = ? and off_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regnNo);
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            }

            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                total = rs.getInt("total");
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
        return total;
    }

    public HpaDobj getVhHptDetails(String applNo, String regnNo, int offCd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String sql = null;
        HpaDobj hpaDobj = null;
        try {
            tmgr = new TransactionManager("getVhHptDetails");
            sql = "SELECT * FROM " + TableList.VH_HPT
                    + " WHERE appl_no=? and regn_no=? order by moved_on desc limit 1";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, regnNo);

            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                hpaDobj = new HpaDobj();
                hpaDobj.setAppl_no(rs.getString("appl_no"));
                hpaDobj.setRegn_no(rs.getString("regn_no"));
                hpaDobj.setSr_no(rs.getInt("sr_no"));
                hpaDobj.setHp_type(rs.getString("hp_type"));
                hpaDobj.setFncr_name(rs.getString("fncr_name"));
                hpaDobj.setFncr_add1(rs.getString("fncr_add1"));
                hpaDobj.setFncr_add2(rs.getString("fncr_add2"));
                hpaDobj.setFncr_add3(rs.getString("fncr_add3"));
                hpaDobj.setFncr_district(rs.getInt("fncr_district"));
                hpaDobj.setFncr_pincode(rs.getInt("fncr_pincode"));
                hpaDobj.setFncr_state(rs.getString("fncr_state"));
                hpaDobj.setFrom_dt(rs.getDate("from_dt"));
                hpaDobj.setUpto_dt(rs.getDate("upto_dt"));
                hpaDobj.setTerm_dt(rs.getDate("term_dt"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Getting Error when Fetching History Details of Hypothecation Termination");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return hpaDobj;
    }

    public void update_HPC_Status(HpaDobj hpa_dobj, Status_dobj status_dobj) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        try {
            tmgr = new TransactionManager("update_HPC_Status");

            sql = "INSERT INTO " + TableList.VH_HPC
                    + " (moved_on, moved_by, state_cd, off_cd, appl_no, regn_no, continuation_dt)"
                    + "  VALUES (current_timestamp, ?, ?, ?, ?, ?, ?)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, status_dobj.getUser_id());
            ps.setString(2, status_dobj.getState_cd());
            ps.setInt(3, status_dobj.getOff_cd());
            ps.setString(4, status_dobj.getAppl_no());
            ps.setString(5, hpa_dobj.getRegn_no());
            ps.setDate(6, new java.sql.Date(hpa_dobj.getUpto_dt().getTime()));
            ps.executeUpdate();

            //for updating the status of application when it is approved start
            status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
            ServerUtil.updateApprovedStatus(tmgr, status_dobj);
            fileFlow(tmgr, status_dobj); // for updating va_status and vha_status for new role,seat for the other employee
            tmgr.commit();//Commiting data here....
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
    }

    public int getHpaMaxSrNo(String applNo, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        int maxSrNo = 0;
        try {
            String query = "SELECT max(sr_no) as max_sr_no FROM " + TableList.VA_HPA + " where appl_no=?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) // found
            {
                maxSrNo = rs.getInt("max_sr_no");
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during fetching Max Sr. No of Hypothecation Details of Appl. No-" + applNo);
        }
        return maxSrNo;
    }
    /*
     * This method will move the data from temporary table to it's history table and delete from temporary table.
     */

    public static void insertToVhaHpaDeleteFromVaHpa(HpaDobj hpa_dobj, String applNo, int srNo) throws SQLException {
        PreparedStatement psVaHpa, psVhaHpa;
        String vaHpasql, vhaHpasql;
        TransactionManager tmgr = new TransactionManager("insertToVhaHpaDeleteFromVaHpa");

        vhaHpasql = "INSERT INTO " + TableList.VHA_HPA
                + " SELECT  current_timestamp as moved_on, ? as moved_by, state_cd , off_cd, appl_no, regn_no, sr_no, hp_type, fncr_name, fncr_add1, fncr_add2, "
                + " fncr_add3, fncr_district, fncr_pincode, fncr_state, from_dt, op_dt FROM " + TableList.VA_HPA + " WHERE appl_no = ? and sr_no=?";

        psVhaHpa = tmgr.prepareStatement(vhaHpasql);
        psVhaHpa.setString(1, String.valueOf(Util.getEmpCode()));
        psVhaHpa.setString(2, applNo);
        psVhaHpa.setInt(3, srNo);
        psVhaHpa.executeUpdate();

        vaHpasql = "DELETE FROM " + TableList.VA_HPA + " WHERE appl_no=? and sr_no=?";
        psVaHpa = tmgr.prepareStatement(vaHpasql);
        psVaHpa.setString(1, applNo);
        psVaHpa.setInt(2, srNo);
        psVaHpa.executeUpdate();
        tmgr.commit();
    }
}//end of HpaImpl
