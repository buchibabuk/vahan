/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.hsrp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.hsrp.NewHsrpDo;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC111
 */
public class HSRPRequestImpl {

    private static final Logger LOGGER = Logger.getLogger(HSRPRequestImpl.class);

    public NewHsrpDo applicationDetails(String[] args) throws VahanException, Exception {
        TransactionManagerReadOnly tmgr = null;
        ResultSet rs = null;
        NewHsrpDo newHsrpdobj = null;
        String regn_no = args[0];
        String state_cd = args[1];
        int off_cd = Integer.parseInt(args[2]);
        PreparedStatement psmt = null;
        String query = "";
        try {
            query = "SELECT state_cd, off_cd, appl_no, regn_no, hsrp_flag, user_cd, op_dt, \n"
                    + "       hsrp_no_front, hsrp_no_back, hsrp_fix_dt, hsrp_fix_amt, hsrp_amt_taken_on, \n"
                    + "       hsrp_op_dt\n"
                    + "  FROM hsrp.va_hsrp where regn_no=? and state_cd=? and off_cd=? ORDER BY op_dt DESC";
            tmgr = new TransactionManagerReadOnly("HSRPRequestImpl.applicationDetails");
            psmt = tmgr.prepareStatement(query);
            psmt.setString(1, regn_no);
            psmt.setString(2, state_cd);
            psmt.setInt(3, off_cd);
            rs = psmt.executeQuery();
            if (rs.next()) {
                newHsrpdobj = new NewHsrpDo();
                newHsrpdobj.setStatus("PENDING");
                newHsrpdobj.getHsrpInfodobj().setRegn_no(regn_no);
                newHsrpdobj.getHsrpInfodobj().setHsrp_flag(Utility.checkAndReturnData(rs.getString("hsrp_flag")));
                newHsrpdobj.getHsrpInfodobj().setAppl_no(Utility.checkAndReturnData(rs.getString("appl_no")));

            } else {
                query = "select temp.moved_on > temp.hsrp_op_dt as flag from ( select a.moved_on,b.hsrp_op_dt from hsrp.vha_hsrp a\n"
                        + "   left outer join hsrp.vt_hsrp b on a.regn_no=b.regn_no and a.state_cd= b.state_cd and a.off_cd = b.off_cd \n"
                        + "   where a.regn_no = ? and a.state_cd = ? and a.off_cd = ? order by hsrp_op_dt,moved_on desc limit 1) as temp";
                psmt = tmgr.prepareStatement(query);
                psmt.setString(1, regn_no);
                psmt.setString(2, state_cd);
                psmt.setInt(3, off_cd);
                rs = psmt.executeQuery();
                String appln = "";
                boolean flag = false;
                if (rs.next()) {
                    flag = rs.getBoolean("flag");
                }
                if (flag) {
                    query = "Select appl_no,hsrp_flag  from hsrp.vha_hsrp where regn_no = ? and state_cd = ? and off_cd = ? Order by moved_on desc limit 1";
                    psmt = tmgr.prepareStatement(query);
                    psmt.setString(1, regn_no);
                    psmt.setString(2, state_cd);
                    psmt.setInt(3, off_cd);
                    rs = psmt.executeQuery();
                    if (rs.next()) {
                        newHsrpdobj = new NewHsrpDo();
                        newHsrpdobj.setStatus("FlatFileGeneratedNotApproved");
                        newHsrpdobj.getHsrpInfodobj().setAppl_no(appln);
                        newHsrpdobj.getHsrpInfodobj().setHsrp_flag(Utility.checkAndReturnData(rs.getString("hsrp_flag")));
                    }

                } else {
                    query = "SELECT state_cd, off_cd, appl_no, regn_no, hsrp_flag, user_cd, op_dt, \n"
                            + "       hsrp_no_front, hsrp_no_back, hsrp_fix_dt, hsrp_fix_amt, hsrp_amt_taken_on, \n"
                            + "       hsrp_op_dt\n"
                            + "  FROM hsrp.vt_hsrp where regn_no=? and state_cd=?";
                    psmt = tmgr.prepareStatement(query);
                    psmt.setString(1, regn_no);
                    psmt.setString(2, state_cd);
                    rs = psmt.executeQuery();
                    if (rs.next()) {
                        newHsrpdobj = new NewHsrpDo();
                        newHsrpdobj.setStatus("DUPLICATE");
                        newHsrpdobj.getHsrpInfodobj().setRegn_no(regn_no);
                        newHsrpdobj.getHsrpInfodobj().setHsrp_no_front(Utility.checkAndReturnData(rs.getString("hsrp_no_front")));
                        newHsrpdobj.getHsrpInfodobj().setHsrp_no_back(Utility.checkAndReturnData(rs.getString("hsrp_no_back")));
                        newHsrpdobj.getHsrpInfodobj().setHsrp_fix_dt(Utility.checkAndReturnData(rs.getString("hsrp_fix_dt")));
                        newHsrpdobj.getHsrpInfodobj().setHsrp_fix_amt(Float.parseFloat(Utility.checkAndReturnData(rs.getString("hsrp_fix_amt"))));
                        newHsrpdobj.getHsrpInfodobj().setHsrp_amt_taken_on(Utility.checkAndReturnData(rs.getString("hsrp_amt_taken_on")));
                    }
                    if (newHsrpdobj == null) {
                        query = "SELECT state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name, \n"
                                + "       f_name, c_add1, c_add2, c_add3, c_district, c_pincode, c_state, \n"
                                + "       p_add1, p_add2, p_add3, p_district, p_pincode, p_state, owner_cd, \n"
                                + "       regn_type, vh_class, chasi_no, eng_no, maker, maker_model, body_type, \n"
                                + "       no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt, \n"
                                + "       gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, cubic_cap, \n"
                                + "       floor_area, ac_fitted, audio_fitted, video_fitted, vch_purchase_as, \n"
                                + "       vch_catg, dealer_cd, sale_amt, laser_code, garage_add, length, \n"
                                + "       width, height, regn_upto, fit_upto, annual_income, imported_vch, \n"
                                + "       other_criteria, status, op_dt\n"
                                + "  FROM vt_owner where regn_no=? and state_cd=? and off_cd=?";
                        psmt = tmgr.prepareStatement(query);
                        psmt.setString(1, regn_no);
                        psmt.setString(2, state_cd);
                        psmt.setInt(3, off_cd);
                        rs = psmt.executeQuery();
                        if (rs.next()) {
                            newHsrpdobj = new NewHsrpDo();
                            newHsrpdobj.getHsrpInfodobj().setRegn_no(regn_no);
                            newHsrpdobj.setStatus("OLD BOTH");
                        }
                    }
                }

            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is some problem in processing your request, Please try after sometime or contact Administrator.");
        } finally {
            tmgr.release();
        }
        return newHsrpdobj;
    }

    public NewHsrpDo CheckDuplicateHsrpForFlatFile(NewHsrpDo newHsrpdobj, TransactionManager tmgr) throws VahanException {

        String appln = newHsrpdobj.getHsrpInfodobj().getAppl_no();
        String regn_no = newHsrpdobj.getHsrpInfodobj().getRegn_no();
        String state_cd = newHsrpdobj.getHsrpInfodobj().getState_cd();
        int off_cd = newHsrpdobj.getHsrpInfodobj().getOff_cd();
        String query = "";
        PreparedStatement psmt = null;
        ResultSet rs = null;
        try {
            if (appln.length() > 0) {
                query = "SELECT state_cd, off_cd, appl_no, regn_no, hsrp_flag, user_cd, op_dt, \n"
                        + "       hsrp_no_front, hsrp_no_back, hsrp_fix_dt, hsrp_fix_amt, hsrp_amt_taken_on, \n"
                        + "       hsrp_op_dt\n"
                        + "  FROM hsrp.vt_hsrp where regn_no=? and state_cd=? and off_cd=? and appl_no = ?";
                psmt = tmgr.prepareStatement(query);
                psmt.setString(1, regn_no);
                psmt.setString(2, state_cd);
                psmt.setInt(3, off_cd);
                psmt.setString(4, appln);
                rs = psmt.executeQuery();
                if (rs.next()) {
                    newHsrpdobj.setStatus("DUPLICATE");
                    newHsrpdobj.getHsrpInfodobj().setRegn_no(regn_no);
                    newHsrpdobj.getHsrpInfodobj().setHsrp_no_front(Utility.checkAndReturnData(rs.getString("hsrp_no_front")));
                    newHsrpdobj.getHsrpInfodobj().setHsrp_no_back(Utility.checkAndReturnData(rs.getString("hsrp_no_back")));
                    newHsrpdobj.getHsrpInfodobj().setHsrp_fix_dt(Utility.checkAndReturnData(rs.getString("hsrp_fix_dt")));
                    newHsrpdobj.getHsrpInfodobj().setHsrp_fix_amt(Float.parseFloat(Utility.checkAndReturnData(rs.getString("hsrp_fix_amt"))));
                    newHsrpdobj.getHsrpInfodobj().setHsrp_amt_taken_on(Utility.checkAndReturnData(rs.getString("hsrp_amt_taken_on")));
                } else {
                    newHsrpdobj.setStatus("FlatFileGeneratedNotApproved");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is some problem in processing your request, Please try after sometime or contact Administrator.");
        }
        return newHsrpdobj;

    }

    public String[] checkVehicleRegistration(String[] args) throws VahanException, Exception {
        String regn_no = args[0];
        String state_cd = args[1];
        int off_cd = Integer.parseInt(args[2]);
        PreparedStatement psmt = null;
        TransactionManagerReadOnly tmgr = null;
        String[] returnargs = new String[3];
        ResultSet rs = null;
        try {
            String query = "SELECT  hsrp_flag, hsrp_no_front, hsrp_no_back, hsrp_fix_dt, hsrp_fix_amt, hsrp_amt_taken_on, \n"
                    + "  hsrp_op_dt\n"
                    + "  FROM hsrp.vt_hsrp where state_cd = ? and regn_no =?";
            tmgr = new TransactionManagerReadOnly("HSRPRequestImplcheckVechileRegistraion");
            psmt = tmgr.prepareStatement(query);
            psmt.setString(1, state_cd);
            psmt.setString(2, regn_no);
            rs = psmt.executeQuery();
            while (rs.next()) {
                returnargs[0] = rs.getString("hsrp_flag");
                returnargs[1] = rs.getString("hsrp_no_front");
                returnargs[2] = rs.getString("hsrp_no_back");

            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is some problem in processing your request, Please try after sometime or contact Administrator.");
        } finally {
            tmgr.release();
        }

        return returnargs;
    }

    public String saveHsrpData(NewHsrpDo dobj, Status_dobj status_dobj, String changedDataContents) throws VahanException, Exception {
        boolean flag = false;
        TransactionManager tmgr = null;
        PreparedStatement psmt = null;
        String whereim = "saveHsrpData";
        String appl_no = "";

        try {
            tmgr = new TransactionManager(whereim);
            if (!changedDataContents.isEmpty() && status_dobj.getAction_cd() == TableConstants.DUPLICATE_HSRP_ENTRY_CD) {
                UpdateHsrpRequest(dobj, tmgr);
                flag = true;
            } else if (!changedDataContents.isEmpty() && status_dobj.getAction_cd() == TableConstants.DUPLICATE_HSRP_APPROVAL_CD) {
                UpdateHsrpRequest(dobj, tmgr);
                flag = true;
            }
            tmgr.commit();
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is some problem in processing your request, Please try after sometime or contact Administrator.");
        } finally {
            tmgr.release();
        }
        return appl_no;
    }

    public String saveAndMoveHsrpData(NewHsrpDo dobj, Status_dobj status_dobj, String changedDataContents) throws VahanException, Exception {
        boolean flag = false;
        TransactionManager tmgr = null;
        PreparedStatement psmt = null;
        String whereim = "saveHsrpData";
        String appl_no = "";
        try {
            tmgr = new TransactionManager(whereim);
            if (status_dobj.getAction_cd() == TableConstants.DUPLICATE_HSRP_ENTRY_CD && !changedDataContents.isEmpty()) {
                UpdateHsrpRequest(dobj, tmgr);
                status_dobj.setStatus(TableConstants.STATUS_COMPLETE);
                ServerUtil.webServiceForNextStage(status_dobj, TableConstants.FORWARD, null, appl_no,
                        status_dobj.getAction_cd(), status_dobj.getPur_cd(), null, tmgr);
            } else if (status_dobj.getAction_cd() == TableConstants.DUPLICATE_HSRP_ENTRY_CD && changedDataContents.isEmpty()) {
                status_dobj.setStatus(TableConstants.STATUS_COMPLETE);
                ServerUtil.webServiceForNextStage(status_dobj, TableConstants.FORWARD, null, status_dobj.getAppl_no(),
                        status_dobj.getAction_cd(), status_dobj.getPur_cd(), null, tmgr);
            } else if (status_dobj.getAction_cd() == TableConstants.DUPLICATE_HSRP_APPROVAL_CD) {
                approveHsrpRequest(dobj, tmgr);
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                status_dobj.setStatus(TableConstants.STATUS_COMPLETE);
            }
            ServerUtil.fileFlow(tmgr, status_dobj);
            tmgr.commit();
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is some problem in processing your request, Please try after sometime or contact Administrator.");
        } finally {
            tmgr.release();
        }
        return "seatwork";
    }

    public boolean approveRequest(NewHsrpDo dobj, Status_dobj status_dobj) throws VahanException, Exception {
        TransactionManager tmgr = null;
        String whereim = "approveRequest";
        boolean flag = false;
        try {
            tmgr = new TransactionManager(whereim);
            if (status_dobj.getAction_cd() == TableConstants.DUPLICATE_HSRP_APPROVAL_CD || status_dobj.getAction_cd() == TableConstants.DUPLICATE_HSRP_APPROVAL_CD_VENDOR) {
                approveHsrpRequest(dobj, tmgr);
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                status_dobj.setStatus(TableConstants.STATUS_COMPLETE);
            }
            ServerUtil.fileFlow(tmgr, status_dobj);
            tmgr.commit();
            flag = true;
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is some problem in processing your request, Please try after sometime or contact Administrator.");
        } finally {
            tmgr.release();
        }
        return flag;
    }

    public NewHsrpDo DuplicateHsrpInfo(String applicationNo) throws VahanException, Exception {

        TransactionManagerReadOnly tmgr = null;
        ResultSet rs = null;
        NewHsrpDo newHsrpdobj = null;
        PreparedStatement psmt = null;
        String query = "";
        try {
            query = "SELECT appl_no, pur_cd, regn_no,hsrp_flag, reason, fir_no, fir_dt, police_station, \n"
                    + "       op_dt\n"
                    + "  FROM hsrp.va_hsrp_dup where appl_no = ?";
            tmgr = new TransactionManagerReadOnly("HSRPRequestImpl.DuplicateHsrpInfo");
            psmt = tmgr.prepareStatement(query);
            psmt.setString(1, applicationNo);
            rs = psmt.executeQuery();
            if (rs.next()) {
                newHsrpdobj = new NewHsrpDo();
                newHsrpdobj.getHsrpInfodobj().setRegn_no(Utility.checkAndReturnData(rs.getString("regn_no")));
                newHsrpdobj.getHsrpInfodobj().setHsrpReason(Utility.checkAndReturnData(rs.getString("reason")));
                newHsrpdobj.getHsrpInfodobj().setHsrp_flag(Utility.checkAndReturnData(rs.getString("hsrp_flag")));
                newHsrpdobj.setFirno(Utility.checkAndReturnData(rs.getString("fir_no")));
                newHsrpdobj.setFirDate(JSFUtils.getStringToDateyyyyMMdd(Utility.checkAndReturnData(rs.getString("fir_dt"))));
                newHsrpdobj.setPolicestation(Utility.checkAndReturnData(rs.getString("police_station")));
                newHsrpdobj.getHsrpInfodobj().setAppl_no(applicationNo);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is some problem in processing your request, Please try after sometime or contact Administrator.");
        } finally {
            tmgr.release();
        }
        return newHsrpdobj;
    }

    public boolean approveHsrpRequest(NewHsrpDo dobj, TransactionManager tmgr) throws VahanException, Exception {
        ResultSet rs = null;
        NewHsrpDo newHsrpdobj = null;
        String whereim = "applicationDetails()";
        PreparedStatement psmt = null;
        String query = "";
        boolean flag = false;
        int count = 1;
        try {
            String query1 = "Insert into hsrp.vha_hsrp_dup(SELECT appl_no, pur_cd, regn_no,hsrp_flag, reason, fir_no, fir_dt, police_station, \n"
                    + "       op_dt,current_timestamp,? FROM hsrp.va_hsrp_dup where regn_no =? and appl_no =? )";
            psmt = tmgr.prepareStatement(query1);
            psmt.setString(1, Util.getEmpCode());
            psmt.setString(2, dobj.getHsrpInfodobj().getRegn_no());
            psmt.setString(3, dobj.getHsrpInfodobj().getAppl_no());
            psmt.executeUpdate();
            psmt.close();
            query = "INSERT INTO hsrp.va_hsrp( select  ?, ?, appl_no, regn_no, hsrp_flag ,?,current_timestamp"
                    + " from hsrp.va_hsrp_dup where  regn_no=? and appl_no =? )";
            psmt = tmgr.prepareStatement(query);
            psmt.setString(1, Util.getUserStateCode());
            psmt.setInt(2, Util.getSelectedSeat().getOff_cd());
            psmt.setString(3, Util.getEmpCode());
            psmt.setString(4, dobj.getHsrpInfodobj().getRegn_no());
            psmt.setString(5, dobj.getHsrpInfodobj().getAppl_no());
            int i = psmt.executeUpdate();
            psmt.close();
            String query2 = "Delete from hsrp.va_hsrp_dup where regn_no = ? and appl_no = ?";
            psmt = tmgr.prepareStatement(query2);
            psmt.setString(1, dobj.getHsrpInfodobj().getRegn_no());
            psmt.setString(2, dobj.getHsrpInfodobj().getAppl_no());
            psmt.executeUpdate();
            psmt.close();
            flag = true;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is some problem in processing your request, Please try after sometime or contact Administrator.");
        }
        return flag;
    }

    public static void insertIntoHsrpHistory(TransactionManager tmgr, String regn_no, String appl_no) throws SQLException, Exception {
        PreparedStatement ps;
        String sql;
        //inserting data into vha_hsrp_dup from va_hsrp_dup
        sql = "Insert into hsrp.vha_hsrp_dup(SELECT appl_no, pur_cd, regn_no,hsrp_flag, reason, fir_no, fir_dt, police_station, \n"
                + "       op_dt,current_timestamp,? FROM hsrp.va_hsrp_dup where regn_no =? and appl_no =? )";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, regn_no);
        ps.setString(3, appl_no);
        ps.executeUpdate();
        ps.close();
    } // end of insertIntoHsrpHistory

    public String saveHsrpRequest(NewHsrpDo dobj, Status_dobj status_dobj) throws VahanException, Exception {
        TransactionManager tmgr = null;
        PreparedStatement psmt = null;
        String appl_no = "";
        String query3 = "INSERT INTO hsrp.va_hsrp_dup(\n"
                + "            appl_no, pur_cd, regn_no,hsrp_flag, reason, fir_no, fir_dt, police_station, \n"
                + "            op_dt)\n"
                + "    VALUES (?, ?, ?, ?, ?, ?, ?,?, current_timestamp)";
        try {
            tmgr = new TransactionManager("saveHsrpRequest");
            psmt = tmgr.prepareStatement(query3);
            appl_no = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());//generate a new application no.
            psmt.setString(1, appl_no);
            if (status_dobj.getAction_cd() == TableConstants.DUPLICATE_HSRP_ENTRY_CD_VENDOR) {
                psmt.setInt(2, TableConstants.DUPLICATE_HSRP_PUR_CD_VENDOR);
            } else {
                psmt.setInt(2, TableConstants.DUPLICATE_HSRP_PUR_CD);
            }
            psmt.setString(3, dobj.getRegn_no());
            psmt.setString(4, dobj.getHsrpInfodobj().getHsrp_flag());//hsrpflag od,db
            psmt.setString(5, dobj.getHsrpInfodobj().getHsrpReason());//hsrpflag od,db
            psmt.setString(6, dobj.getFirno());
            if (dobj.getFirDate() != null) {
                long dt = dobj.getFirDate().getTime();
                psmt.setDate(7, new java.sql.Date(dt));
            } else {
                psmt.setDate(7, null);
            }
            psmt.setString(8, dobj.getPolicestation());
            psmt.executeUpdate();
            status_dobj.setFlow_slno(1);//initial flow serial no.
            status_dobj.setFile_movement_slno(1);//initial file movement serial no.
            status_dobj.setRegn_no(dobj.getRegn_no());
            status_dobj.setUser_id(Util.getUserId());
            status_dobj.setSeat_cd("");
            status_dobj.setCntr_id("");
            status_dobj.setStatus("N");
            status_dobj.setOffice_remark("");
            status_dobj.setPublic_remark("");
            status_dobj.setEntry_ip("");
            status_dobj.setEntry_status("");//for New File
            status_dobj.setConfirm_ip("");
            status_dobj.setConfirm_status("");
            status_dobj.setUser_type("");
            status_dobj.setConfirm_date(new java.util.Date());
            status_dobj.setAppl_no(appl_no);
            status_dobj.setRegn_no(dobj.getRegn_no());
            if (status_dobj.getAction_cd() == TableConstants.DUPLICATE_HSRP_ENTRY_CD_VENDOR) {
                status_dobj.setPur_cd(TableConstants.DUPLICATE_HSRP_PUR_CD_VENDOR);
            } else {
                status_dobj.setPur_cd(TableConstants.DUPLICATE_HSRP_PUR_CD);
            }
            ServerUtil.fileFlowForNewApplication(tmgr, status_dobj);
            status_dobj.setStatus(TableConstants.STATUS_COMPLETE);
            ServerUtil.webServiceForNextStage(status_dobj, TableConstants.FORWARD, null, appl_no,
                    status_dobj.getAction_cd(), status_dobj.getPur_cd(), null, tmgr);
            ServerUtil.fileFlow(tmgr, status_dobj);
            tmgr.commit();
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is some problem in processing your request, Please try after sometime or contact Administrator.");
        } finally {
            tmgr.release();
        }

        return appl_no;
    }

    public void UpdateHsrpRequest(NewHsrpDo dobj, TransactionManager tmgr) throws VahanException {
        PreparedStatement psmt = null;
        try {
            String query = "UPDATE hsrp.va_hsrp_dup \n"
                    + "   SET   reason=?, fir_no=?, fir_dt=?, \n"
                    + "       police_station=?, op_dt=current_timestamp , hsrp_flag =? "
                    + " WHERE appl_no=?";
            psmt = tmgr.prepareStatement(query);
            psmt.setString(1, dobj.getHsrpInfodobj().getHsrpReason());//reson
            psmt.setString(2, dobj.getFirno());
            if (dobj.getFirDate() != null) {
                long dt = dobj.getFirDate().getTime();
                psmt.setDate(3, new java.sql.Date(dt));
            } else {
                psmt.setDate(3, null);
            }
            psmt.setString(4, dobj.getPolicestation());
            psmt.setString(5, dobj.getHsrpInfodobj().getHsrp_flag());
            psmt.setString(6, dobj.getHsrpInfodobj().getAppl_no());
            psmt.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("There is some problem in processing your request, Please try after sometime or contact Administrator.");
        }

    }
}
