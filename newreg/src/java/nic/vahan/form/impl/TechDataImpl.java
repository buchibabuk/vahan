/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.TechDataDobj;
import org.apache.log4j.Logger;
import nic.vahan.db.TableList;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;

/**
 *
 * @author NIC
 */
public class TechDataImpl implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(TechDataImpl.class);

    public List<TechDataDobj> getDataVerifyRequest(String state_cd, int off_cd) throws VahanException {
        List<TechDataDobj> techDataList = new ArrayList<>();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getDataVerifyRequest");
            ps = tmgr.prepareStatement("select regn_no,to_char(request_dt::date,'dd-Mon-yyyy') as request_dt,mobile_no from " + TableList.VA_DATA_VERIFY_REQUEST + " where state_cd = ? and off_cd = ? order by request_dt limit 50");
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                TechDataDobj dobj = new TechDataDobj();
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setRequest_dt(rs.getString("request_dt"));
                dobj.setMobile_no(rs.getLong("mobile_no"));
                techDataList.add(dobj);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Getting Technical Verification Number List");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return techDataList;
    }

    public TechDataDobj getVehicleData(String regn_no, SessionVariables sessionVariables) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        TechDataDobj dobj = null;
        try {
            tmgr = new TransactionManager("getVehicleDetails");
            ps = tmgr.prepareStatement("select a.*,b.mobile_no, r.mobile_no as requested_mobileno,vvc.class_type,vvc.transport_catg from " + TableList.VIEW_VV_OWNER
                    + " a left join " + TableList.VT_OWNER_IDENTIFICATION
                    + " b on b.regn_no = a.regn_no and b.state_cd = a.state_cd and b.off_cd = a.off_cd"
                    + " left join " + TableList.VA_DATA_VERIFY_REQUEST
                    + " r on r.regn_no= a.regn_no"
                    + " inner join " + TableList.VM_VH_CLASS + " vvc on a.vh_class = vvc.vh_class"
                    + " where a.regn_no = ? and a.state_cd = ? and a.off_cd = ?");
            ps.setString(1, regn_no);
            ps.setString(2, sessionVariables.getStateCodeSelected());
            ps.setInt(3, sessionVariables.getOffCodeSelected());
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                dobj = new TechDataDobj();
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setMaker(rs.getInt("maker"));
                dobj.setMaker_model(rs.getString("model_cd"));
                dobj.setFuel(rs.getInt("fuel"));
                dobj.setSeat_cap(rs.getInt("seat_cap"));
                dobj.setStand_cap(rs.getInt("stand_cap"));
                dobj.setUnld_wt(rs.getInt("unld_wt"));
                dobj.setLd_wt(rs.getInt("ld_wt"));
                dobj.setCubic_cap(rs.getFloat("cubic_cap"));
                dobj.setHp(rs.getFloat("hp"));
                dobj.setNo_cyl(rs.getInt("no_cyl"));
                dobj.setSale_amt(rs.getInt("sale_amt"));
                dobj.setAc_fitted(rs.getString("ac_fitted"));
                dobj.setAudio_fitted(rs.getString("audio_fitted"));
                dobj.setVideo_fitted(rs.getString("video_fitted"));
                dobj.setNorms(rs.getInt("norms"));
                if (rs.getInt("class_type") == TableConstants.VM_VEHTYPE_TRANSPORT && "P".equals(rs.getString("transport_catg")) && !"JH".equals(rs.getString("state_cd"))) {
                    dobj.setPmtPanelRendered(true);
                    dobj.setTransportCatg(rs.getString("transport_catg"));
                }
                if (rs.getLong("requested_mobileno") == 0) {
                    dobj.setMobile_no(rs.getLong("mobile_no"));
                } else {
                    dobj.setMobile_no(rs.getLong("requested_mobileno"));
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error In Getting Vehicle Detail");
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

    public boolean checkHistory(String regn_no, SessionVariables sessionVariables) throws VahanException {
        boolean flag = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("checkHistory");
            ps = tmgr.prepareStatement("select * from " + TableList.VHA_DATA_VERIFY_REQUEST + " where regn_no = ? and state_cd = ? and off_cd = ?");
            ps.setString(1, regn_no);
            ps.setString(2, sessionVariables.getStateCodeSelected());
            ps.setInt(3, sessionVariables.getOffCodeSelected());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                flag = true;
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Technical data is already Verified");
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

    public String insertIntoVaTechDataVerify(Status_dobj status, TechDataDobj techDataDobj, List<ComparisonBean> compareChanges, SessionVariables sessionVariables) throws VahanException {
        String appl_no = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        int action_cd = 0;
        int actionCodeArray[] = null;
        try {
            tmgr = new TransactionManager("InsertIntoVaTechDataVerify");
            sql = "select * from " + TableList.VA_DATA_VERIFY_REQUEST + " where regn_no = ? and state_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, techDataDobj.getRegn_no());
            ps.setString(2, status.getState_cd());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                sql = "INSERT INTO " + TableList.VHA_DATA_VERIFY_REQUEST
                        + " SELECT current_timestamp, ?,* "
                        + "  FROM  " + TableList.VA_DATA_VERIFY_REQUEST
                        + " WHERE  regn_no=? and state_cd=? and off_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, sessionVariables.getEmpCodeLoggedIn());
                ps.setString(2, techDataDobj.getRegn_no());
                ps.setString(3, status.getState_cd());
                ps.setInt(4, status.getOff_cd());
                ps.executeUpdate();

                sql = "delete from " + TableList.VA_DATA_VERIFY_REQUEST + " where regn_no = ? and state_cd = ? and off_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, techDataDobj.getRegn_no());
                ps.setString(2, status.getState_cd());
                ps.setInt(3, status.getOff_cd());
                ps.executeUpdate();
            } else {
                sql = "select * from " + TableList.VHA_DATA_VERIFY_REQUEST + " where regn_no = ? and state_cd = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, techDataDobj.getRegn_no());
                ps.setString(2, status.getState_cd());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (!rs.next()) {
                    sql = "INSERT INTO " + TableList.VHA_DATA_VERIFY_REQUEST
                            + " (moved_on, moved_by, state_cd, off_cd, regn_no, request_dt,mobile_no) "
                            + " VALUES (current_timestamp, ?, ?, ?, ?,current_timestamp,?)";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, sessionVariables.getEmpCodeLoggedIn());
                    ps.setString(2, status.getState_cd());
                    ps.setInt(3, status.getOff_cd());
                    ps.setString(4, techDataDobj.getRegn_no());
                    ps.setLong(5, techDataDobj.getMobile_no());
                    ps.executeUpdate();

                }
            }
            appl_no = ServerUtil.getUniqueApplNo(tmgr, status.getState_cd());//generate a new application no.
            sql = " INSERT INTO " + TableList.VA_TECH_DATA_VERIFY
                    + " (state_cd, off_cd, appl_no, regn_no, maker, maker_model, fuel,"
                    + " seat_cap, stand_cap, unld_wt, ld_wt, cubic_cap, hp, no_cyl, sale_amt,"
                    + " ac_fitted, audio_fitted, video_fitted, mobile_no, reason, op_dt, pmt_type, pmt_catg,norms)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp, ?, ?,?)";
            int i = 1;
            ps = tmgr.prepareStatement(sql);
            ps.setString(i++, status.getState_cd());
            ps.setInt(i++, status.getOff_cd());
            ps.setString(i++, appl_no);
            ps.setString(i++, techDataDobj.getRegn_no());
            ps.setInt(i++, techDataDobj.getMaker());
            ps.setString(i++, techDataDobj.getMaker_model());
            ps.setInt(i++, techDataDobj.getFuel());
            ps.setInt(i++, techDataDobj.getSeat_cap());
            ps.setInt(i++, techDataDobj.getStand_cap());
            ps.setInt(i++, techDataDobj.getUnld_wt());
            ps.setInt(i++, techDataDobj.getLd_wt());
            ps.setFloat(i++, techDataDobj.getCubic_cap());
            ps.setFloat(i++, techDataDobj.getHp());
            ps.setInt(i++, techDataDobj.getNo_cyl());
            ps.setInt(i++, techDataDobj.getSale_amt());
            ps.setString(i++, techDataDobj.getAc_fitted());
            ps.setString(i++, techDataDobj.getAudio_fitted());
            ps.setString(i++, techDataDobj.getVideo_fitted());
            ps.setLong(i++, techDataDobj.getMobile_no());
            ps.setString(i++, techDataDobj.getReason());
            if (techDataDobj.getPermitType() == 0) {
                ps.setInt(i++, 0);
            } else {
                ps.setInt(i++, techDataDobj.getPermitType());
            }
            if (techDataDobj.getPermitCatg() == 0) {
                ps.setInt(i++, 0);
            } else {
                ps.setInt(i++, techDataDobj.getPermitCatg());
            }
            ps.setInt(i, techDataDobj.getNorms());
            ps.executeUpdate();

            actionCodeArray = ServerUtil.getInitialAction(tmgr, status.getState_cd(), status.getPur_cd(), null);
            if (actionCodeArray == null) {
                throw new VahanException("Initial Action Code is Not Available!");
            }

            action_cd = actionCodeArray[0];
            status.setAppl_no(appl_no);
            status.setAction_cd(action_cd);//Initial Action_cd
            status.setAppl_date(ServerUtil.getSystemDateInPostgres());
            techDataDobj.setAppl_no(appl_no);
            ServerUtil.insertIntoVaStatus(tmgr, status);
            ServerUtil.insertIntoVaDetails(tmgr, status);
            insertUpdateTechData(tmgr, techDataDobj, sessionVariables);
            ComparisonBeanImpl.updateChangedData(techDataDobj.getAppl_no(), ComparisonBeanImpl.changedDataContents(compareChanges), tmgr);
            status.setStatus(TableConstants.STATUS_COMPLETE);
            status = ServerUtil.webServiceForNextStage(status, tmgr);
            ServerUtil.fileFlow(tmgr, status); //for updateing va_status and vha status for new role,seat for new emp

            tmgr.commit();//Commiting data here....

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error In Application Number Generation");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return appl_no;
    }

    public static void makeChangeTechData(TechDataDobj techDataDobj, String changedata, SessionVariables sessionVariables) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("makeChangeTachData");
            insertUpdateTechData(tmgr, techDataDobj, sessionVariables);
            ComparisonBeanImpl.updateChangedData(techDataDobj.getAppl_no(), changedata, tmgr);
            tmgr.commit();
        } catch (Exception ex) {
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
    }

    public static void insertUpdateTechData(TransactionManager tmgr, TechDataDobj techDataDobj, SessionVariables sessionVariables) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "SELECT regn_no FROM " + TableList.VA_TECH_DATA_VERIFY + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, techDataDobj.getAppl_no());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                insertIntoTechDataHistory(tmgr, techDataDobj.getAppl_no(), sessionVariables);
                updateTechData(tmgr, techDataDobj);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public static void insertIntoTechDataHistory(TransactionManager tmgr, String appl_no, SessionVariables sessionVariables) throws Exception {
        PreparedStatement ps = null;
        String sql = null;
        sql = "INSERT INTO " + TableList.VHA_TECH_DATA_VERIFY
                + " SELECT current_timestamp, ? ,* "
                + "  FROM  " + TableList.VA_TECH_DATA_VERIFY
                + " WHERE  appl_no=? and state_cd=? and off_cd=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, sessionVariables.getEmpCodeLoggedIn());
        ps.setString(2, appl_no);
        ps.setString(3, sessionVariables.getStateCodeSelected());
        ps.setInt(4, sessionVariables.getOffCodeSelected());
        ps.executeUpdate();
    }

    public static void updateTechData(TransactionManager tmgr, TechDataDobj techDataDobj) throws Exception {
        PreparedStatement ps = null;
        String sql = null;
        sql = "UPDATE " + TableList.VA_TECH_DATA_VERIFY
                + " SET maker=?,"
                + " maker_model=?,"
                + " fuel=?,"
                + " seat_cap=?,"
                + " stand_cap=?,"
                + " unld_wt=?,"
                + " ld_wt=?,"
                + " cubic_cap=?,"
                + " hp=?,"
                + " no_cyl=?,"
                + " sale_amt=?,"
                + " ac_fitted=?,"
                + " audio_fitted=?,"
                + " video_fitted=?,"
                + " mobile_no=?,"
                + " reason=?,"
                + " op_dt=current_timestamp,"
                + " pmt_type=?,"
                + " pmt_catg=?,"
                + " norms=?"
                + " WHERE appl_no = ?";
        int i = 1;
        ps = tmgr.prepareStatement(sql);
        ps.setInt(i++, techDataDobj.getMaker());
        ps.setString(i++, techDataDobj.getMaker_model());
        ps.setInt(i++, techDataDobj.getFuel());
        ps.setInt(i++, techDataDobj.getSeat_cap());
        ps.setInt(i++, techDataDobj.getStand_cap());
        ps.setInt(i++, techDataDobj.getUnld_wt());
        ps.setInt(i++, techDataDobj.getLd_wt());
        ps.setFloat(i++, techDataDobj.getCubic_cap());
        ps.setFloat(i++, techDataDobj.getHp());
        ps.setInt(i++, techDataDobj.getNo_cyl());
        ps.setInt(i++, techDataDobj.getSale_amt());
        ps.setString(i++, techDataDobj.getAc_fitted());
        ps.setString(i++, techDataDobj.getAudio_fitted());
        ps.setString(i++, techDataDobj.getVideo_fitted());
        ps.setLong(i++, techDataDobj.getMobile_no());
        ps.setString(i++, techDataDobj.getReason());
        if (techDataDobj.getPermitType() == 0) {
            ps.setNull(i++, java.sql.Types.NULL);
        } else {
            ps.setInt(i++, techDataDobj.getPermitType());
        }
        if (techDataDobj.getPermitCatg() == 0) {
            ps.setNull(i++, java.sql.Types.NULL);
        } else {
            ps.setInt(i++, techDataDobj.getPermitCatg());
        }
        ps.setInt(i++, techDataDobj.getNorms());
        ps.setString(i++, techDataDobj.getAppl_no());
        ps.executeUpdate();
    }

    public TechDataDobj getVehicleDataFromVaTable(String appl_no) throws Exception {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        TechDataDobj dobj = null;
        try {
            tmgr = new TransactionManager("getVehicleDataFromVaTable");
            ps = tmgr.prepareStatement("select * from " + TableList.VA_TECH_DATA_VERIFY + " where appl_no =?");
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new TechDataDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setMaker(rs.getInt("maker"));
                dobj.setMaker_model(rs.getString("maker_model"));
                dobj.setFuel(rs.getInt("fuel"));
                dobj.setSeat_cap(rs.getInt("seat_cap"));
                dobj.setStand_cap(rs.getInt("stand_cap"));
                dobj.setUnld_wt(rs.getInt("unld_wt"));
                dobj.setLd_wt(rs.getInt("ld_wt"));
                dobj.setCubic_cap(rs.getFloat("cubic_cap"));
                dobj.setHp(rs.getFloat("hp"));
                dobj.setNo_cyl(rs.getInt("no_cyl"));
                dobj.setSale_amt(rs.getInt("sale_amt"));
                dobj.setAc_fitted(rs.getString("ac_fitted"));
                dobj.setAudio_fitted(rs.getString("audio_fitted"));
                dobj.setVideo_fitted(rs.getString("video_fitted"));
                dobj.setMobile_no(rs.getLong("mobile_no"));
                dobj.setReason(rs.getString("reason"));
                int pmt_type = rs.getInt("pmt_type");
                int pmt_catg = rs.getInt("pmt_catg");
                if (pmt_type != 0 && pmt_catg >= 0) {
                    dobj.setPermitType(rs.getInt("pmt_type"));
                    dobj.setPermitCatg(rs.getInt("pmt_catg"));
                    dobj.setPmtPanelRendered(true);
                    dobj.setTransportCatg("P");
                }
                dobj.setNorms(rs.getInt("norms"));
            }
            if (dobj == null) {
                throw new VahanException("No Record Found for this Application");
            }
        } catch (VahanException ex) {
            throw ex;
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
        return dobj;
    }

    public void update_techData_Status(TechDataDobj techDobj, TechDataDobj techDobj_prev, Status_dobj statusDobj, String changeData, SessionVariables sessionVariables) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        try {
            tmgr = new TransactionManager("update_TechData_Status");
            statusDobj = ServerUtil.webServiceForNextStage(statusDobj, tmgr);

            if ((changeData != null && !changeData.equals("")) || techDobj_prev == null) {
                insertUpdateTechData(tmgr, techDobj, sessionVariables);
            }
            sql = "INSERT INTO " + TableList.VH_OWNER
                    + " (state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name,"
                    + " f_name, c_add1, c_add2, c_add3, c_district, c_pincode, c_state,"
                    + " p_add1, p_add2, p_add3, p_district, p_pincode, p_state, owner_cd,"
                    + " regn_type, vh_class, chasi_no, eng_no, maker, maker_model, body_type,"
                    + " no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt,"
                    + " gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, cubic_cap,"
                    + " floor_area, ac_fitted, audio_fitted, video_fitted, vch_purchase_as,"
                    + " vch_catg, dealer_cd, sale_amt, laser_code, garage_add, length,"
                    + " width, height, regn_upto, fit_upto, annual_income, imported_vch,"
                    + " other_criteria, status, op_dt, appl_no, moved_on, moved_by,moved_status)"
                    + "   SELECT state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name,"
                    + "       f_name, c_add1, c_add2, c_add3, c_district, c_pincode, c_state,"
                    + "       p_add1, p_add2, p_add3, p_district, p_pincode, p_state, owner_cd,"
                    + "       regn_type, vh_class, chasi_no, eng_no, maker, maker_model, body_type,"
                    + "       no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt,"
                    + "       gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, cubic_cap,"
                    + "       floor_area, ac_fitted, audio_fitted, video_fitted, vch_purchase_as,"
                    + "       vch_catg, dealer_cd, sale_amt, laser_code, garage_add, length,"
                    + "       width, height, regn_upto, fit_upto, annual_income, imported_vch,"
                    + "       other_criteria, status, op_dt, ?,current_timestamp,?,?"
                    + "  FROM " + TableList.VT_OWNER
                    + "  where regn_no=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, techDobj.getAppl_no());
            ps.setString(2, sessionVariables.getEmpCodeLoggedIn());
            ps.setString(3, TableConstants.VH_MOVED_STATUS_UPDATE);
            ps.setString(4, techDobj.getRegn_no());
            ps.setString(5, sessionVariables.getStateCodeSelected());
            ps.setInt(6, sessionVariables.getOffCodeSelected());
            ps.executeUpdate();

            sql = "UPDATE " + TableList.VT_OWNER
                    + " SET maker=?,"
                    + "	maker_model=?,"
                    + " no_cyl=?,"
                    + " hp=?,"
                    + "	seat_cap=?,"
                    + "	stand_cap=?,"
                    + "	unld_wt=?,"
                    + "	ld_wt=?,"
                    + " fuel=?,"
                    + " cubic_cap=?,"
                    + "	ac_fitted=?,"
                    + "	audio_fitted=?,"
                    + "	video_fitted=?,"
                    + " sale_amt=?,"
                    + " norms=?,"
                    + " op_dt=current_timestamp"
                    + " WHERE regn_no= ? and state_cd = ? and off_cd = ?";
            int i = 1;
            ps = tmgr.prepareStatement(sql);
            ps.setInt(i++, techDobj.getMaker());
            ps.setString(i++, techDobj.getMaker_model());
            ps.setInt(i++, techDobj.getNo_cyl());
            ps.setFloat(i++, techDobj.getHp());
            ps.setInt(i++, techDobj.getSeat_cap());
            ps.setInt(i++, techDobj.getStand_cap());
            ps.setInt(i++, techDobj.getUnld_wt());
            ps.setInt(i++, techDobj.getLd_wt());
            ps.setInt(i++, techDobj.getFuel());
            ps.setFloat(i++, techDobj.getCubic_cap());
            ps.setString(i++, techDobj.getAc_fitted());
            ps.setString(i++, techDobj.getAudio_fitted());
            ps.setString(i++, techDobj.getVideo_fitted());
            ps.setInt(i++, techDobj.getSale_amt());
            ps.setInt(i++, techDobj.getNorms());
            ps.setString(i++, techDobj.getRegn_no());
            ps.setString(i++, sessionVariables.getStateCodeSelected());
            ps.setInt(i++, sessionVariables.getOffCodeSelected());
            ps.executeUpdate();

            sql = "select * from " + TableList.VT_OWNER_IDENTIFICATION
                    + " WHERE regn_no= ? and state_cd = ? and off_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, techDobj.getRegn_no());
            ps.setString(2, sessionVariables.getStateCodeSelected());
            ps.setInt(3, sessionVariables.getOffCodeSelected());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                sql = "insert into " + TableList.VH_OWNER_IDENTIFICATION
                        + " Select state_cd,off_cd,regn_no, mobile_no, email_id, pan_no, aadhar_no, passport_no,"
                        + " ration_card_no, voter_id, dl_no, verified_on, current_timestamp as moved_on, "
                        + " ? as moved_by,owner_ctg from "
                        + TableList.VT_OWNER_IDENTIFICATION + " where regn_no=? and state_cd= ? and off_cd= ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, sessionVariables.getEmpCodeLoggedIn());
                ps.setString(2, techDobj.getRegn_no());
                ps.setString(3, sessionVariables.getStateCodeSelected());
                ps.setInt(4, sessionVariables.getOffCodeSelected());
                ps.executeUpdate();

                sql = "UPDATE " + TableList.VT_OWNER_IDENTIFICATION
                        + " SET mobile_no=?,"
                        + " verified_on=current_timestamp"
                        + " WHERE regn_no= ? and state_cd = ? and off_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setLong(1, techDobj.getMobile_no());
                ps.setString(2, techDobj.getRegn_no());
                ps.setString(3, sessionVariables.getStateCodeSelected());
                ps.setInt(4, sessionVariables.getOffCodeSelected());
                ps.executeUpdate();
            } else {
                sql = "INSERT INTO " + TableList.VT_OWNER_IDENTIFICATION
                        + " ( state_cd, off_cd, regn_no, mobile_no,verified_on)"
                        + " VALUES (?, ?, ?, ?, current_timestamp)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, sessionVariables.getStateCodeSelected());
                ps.setInt(2, sessionVariables.getOffCodeSelected());
                ps.setString(3, techDobj.getRegn_no());
                ps.setLong(4, techDobj.getMobile_no());
                ps.executeUpdate();

            }
            sql = "insert into " + TableList.VHA_TECH_DATA_VERIFY
                    + " select current_timestamp + interval '1 second' as moved_on, ? as moved_by, *"
                    + " from " + TableList.VA_TECH_DATA_VERIFY
                    + " where appl_no= ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, sessionVariables.getEmpCodeLoggedIn());
            ps.setString(2, techDobj.getAppl_no());
            ps.executeUpdate();


            //vt_tax_based_on

            if (techDobj.isPmtPanelRendered()) {
                sql = "INSERT INTO " + TableList.VT_TAX_BASED_ON + " ("
                        + " state_cd, off_cd, rcpt_no, regn_no, purchase_dt, regn_type, vh_class, "
                        + " no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt, "
                        + " gcw, fuel, wheelbase, cubic_cap, floor_area, ac_fitted, audio_fitted, "
                        + " video_fitted, vch_purchase_as, vch_catg, sale_amt, length, width, "
                        + " height, imported_vch, other_criteria, fin_yr_sale_amt, pmt_type, "
                        + " pmt_catg, op_dt)"
                        + " SELECT ?, ?, ?, ?, purchase_dt, regn_type, vh_class, "
                        + " ?, ?, ?, ?, sleeper_cap, ?, ?, "
                        + " gcw, ?, wheelbase, ?, floor_area, ?, ?, "
                        + " ?, vch_purchase_as, vch_catg,  ?, length, width, "
                        + " height,  imported_vch, other_criteria, ?, ?, ?, CURRENT_TIMESTAMP"
                        + " FROM " + TableList.VT_OWNER + " where state_cd=? and off_cd=? and regn_no=?;";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, sessionVariables.getStateCodeSelected());
                ps.setInt(2, sessionVariables.getOffCodeSelected());
                ps.setString(3, techDobj.getAppl_no());
                ps.setString(4, techDobj.getRegn_no());
                ps.setInt(5, techDobj.getNo_cyl());
                ps.setFloat(6, techDobj.getHp());
                ps.setInt(7, techDobj.getSeat_cap());
                ps.setInt(8, techDobj.getStand_cap());
                ps.setInt(9, techDobj.getUnld_wt());
                ps.setInt(10, techDobj.getLd_wt());
                ps.setInt(11, techDobj.getFuel());
                ps.setFloat(12, techDobj.getCubic_cap());
                ps.setString(13, techDobj.getAc_fitted());
                ps.setString(14, techDobj.getAudio_fitted());
                ps.setString(15, techDobj.getVideo_fitted());
                ps.setInt(16, techDobj.getSale_amt());
                ps.setInt(17, techDobj.getSale_amt());
                ps.setInt(18, techDobj.getPermitType());
                ps.setInt(19, techDobj.getPermitCatg());
                ps.setString(20, sessionVariables.getStateCodeSelected());
                ps.setInt(21, sessionVariables.getOffCodeSelected());
                ps.setString(22, techDobj.getRegn_no());
                ps.executeUpdate();
            }



            sql = "delete from " + TableList.VA_TECH_DATA_VERIFY + " where appl_no =?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, techDobj.getAppl_no());
            ps.executeUpdate();

            //for updating the status of application when it is approved start
            statusDobj.setEntry_status(TableConstants.STATUS_APPROVED);
            ServerUtil.updateApprovedStatus(tmgr, statusDobj);
            //for updating the status of application when it is approved end

            insertIntoVhaChangedData(tmgr, techDobj.getAppl_no(), changeData);//for saving the data into table those are changed by the user

            ServerUtil.fileFlow(tmgr, statusDobj); //for updateing va_status and vha status for new role,seat for new emp

            tmgr.commit();//Commiting data here....


            //-----SMS------
            String msgMobileCollection = "";
            String officeName = getOfficeName(Util.getSelectedSeat().getOff_cd(), Util.getUserStateCode());
            String mobileNo = String.valueOf(techDobj.getMobile_no());
            msgMobileCollection = "Technical Data is Verified For Vehicle Number" + techDobj.getRegn_no() + "]%0D%0A"
                    + " dated " + JSFUtils.convertToStandardDateFormat(new java.util.Date()) + ".%0D%0A"
                    + " Now you can pay online Tax."
                    + " Thanks " + officeName;
            ServerUtil.sendSMS(mobileNo, msgMobileCollection);

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error In Database Update");
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

    public String getOfficeName(int offCode, String stateCode) {
        String officeName = "";
        TransactionManager tmgr = null;
        String ChasiSQL = "select off_name from tm_office where off_cd=? and state_cd = ?";
        try {
            tmgr = new TransactionManager("getOfficeName");
            PreparedStatement ps = tmgr.prepareStatement(ChasiSQL);
            ps.setInt(1, offCode);
            ps.setString(2, stateCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                officeName = rs.getString("off_name");
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
        return officeName;
    }
}
