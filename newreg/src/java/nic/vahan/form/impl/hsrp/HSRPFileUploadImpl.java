/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl.hsrp;

import java.sql.BatchUpdateException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.hsrp.HSRPFileUploadDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC075
 */
public class HSRPFileUploadImpl {

    private static final Logger LOGGER = Logger.getLogger(HSRPFileUploadImpl.class);

    public String validateFileNameAlreadyExist(String fileName, String state_cd, int off_cd) throws VahanException {

        String clsDesp = "";
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement psmt = null;
        String sql = "SELECT flat_file FROM " + TableList.VT_HSRP_FLATFILE + " WHERE state_cd = ? and off_cd = ? anf flat_file = ?";
        try {
            tmgr = new TransactionManagerReadOnly("HSRPFileUploadImpl.validateFileNameAlreadyExist");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, off_cd);
            ps.setString(3, fileName);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                clsDesp = rs.getString("file_name");
            }
            rs.close();
            rs = null;
            psmt.close();
            psmt = null;
        } catch (SQLException sqlex) {
            LOGGER.error(sqlex.toString() + " " + sqlex.getStackTrace()[0]);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return clsDesp;

    }

    public String processHSRFEntryTableForBulk(ArrayList<HSRPFileUploadDobj> dobjList, String fileName) throws Exception {
        String whereiam = "HSRPFileUploadImpl.processHSRFEntryTableForBulk()";
        String res = "";
        TransactionManager tmgr = null;
        PreparedStatement preparedStatement;
        PreparedStatement preparedStatement1;
        PreparedStatement preparedStatement2;
        boolean inserted = false;
        boolean batchPrepared = false;
        boolean ifRecordDeleted = false;
        String query = "";
        String queryToMaintainHistory = "";
        String queryDeleteDuplicateRecords = "";
        String reason = "New HSRP allotted";
        String loggedInEmpCd = Util.getEmpCode();
        int loggedInOffCd = Util.getSelectedSeat().getOff_cd();
        String currentStateCd = Util.getUserStateCode();
        if (currentStateCd == null) {
            res = "Session timeout.";
            return res;
        }
        if (dobjList == null) {
            res = "Invalid data recieved";
            return res;
        }
        try {
            tmgr = new TransactionManager(whereiam);
            inserted = false;

            queryToMaintainHistory = "INSERT INTO " + TableList.VH_HSRP + "("
                    + "            state_cd, off_cd, appl_no, regn_no, hsrp_flag, user_cd, op_dt, "
                    + "            hsrp_no_front, hsrp_no_back, hsrp_fix_dt, hsrp_fix_amt, hsrp_amt_taken_on, "
                    + "            hsrp_op_dt, moved_reason, moved_on, moved_by)"
                    + "            (SELECT state_cd, off_cd, appl_no, regn_no, hsrp_flag, user_cd, op_dt,"
                    + "            hsrp_no_front, hsrp_no_back, hsrp_fix_dt, hsrp_fix_amt, hsrp_amt_taken_on, "
                    + "            hsrp_op_dt , ?, current_timestamp, ? "
                    + "            FROM " + TableList.VT_HSRP + " where regn_no = ? )";
            preparedStatement1 = tmgr.prepareStatement(queryToMaintainHistory);

            queryDeleteDuplicateRecords = "DELETE FROM hsrp.vt_hsrp WHERE regn_no = ?";
            preparedStatement2 = tmgr.prepareStatement(queryDeleteDuplicateRecords);

            query = "INSERT INTO " + TableList.VT_HSRP + " ( state_cd,  appl_no, regn_no, hsrp_flag, user_cd, off_cd ,op_dt, "
                    + "            hsrp_no_front, hsrp_no_back, hsrp_fix_dt, hsrp_fix_amt, hsrp_amt_taken_on, "
                    + "            hsrp_op_dt )"
                    + "( Select ?, ?, ?, ?, ?,"
                    + "             off_cd , op_dt ,"
                    + "                      ?, ?, ?::date, ?, ?::date,  "
                    + "                              current_timestamp  from hsrp.vha_hsrp   where appl_no = ?)";

//            query = "INSERT INTO " + TableList.VT_HSRP + " ( state_cd, off_cd, appl_no, regn_no, hsrp_flag, user_cd, op_dt, "
//                    + "            hsrp_no_front, hsrp_no_back, hsrp_fix_dt, hsrp_fix_amt, hsrp_amt_taken_on, "
//                    + "            hsrp_op_dt )"
//                    + "VALUES (?, ?, ?, ?, ?, ?, (Select op_dt from " + TableList.VHA_HSRP + " where appl_no = ?),"
//                    + "            ?, ?, ?::date, ?, ?::date, "
//                    + "            current_timestamp)";
            preparedStatement = tmgr.prepareStatement(query);
            for (HSRPFileUploadDobj dobj : dobjList) {
                ifRecordDeleted = true;
                preparedStatement1.setString(1, reason);
                preparedStatement1.setString(2, loggedInEmpCd);
                preparedStatement1.setString(3, dobj.getRegnNo().trim().toUpperCase());
                preparedStatement1.addBatch();
                preparedStatement2.setString(1, dobj.getRegnNo().trim().toUpperCase());
                preparedStatement2.addBatch();
            }
            if (ifRecordDeleted) {
                preparedStatement1.executeBatch();
                preparedStatement2.executeBatch();
            }
            for (HSRPFileUploadDobj dobj : dobjList) {
                batchPrepared = true;
                preparedStatement.setString(1, currentStateCd);
//                preparedStatement.setInt(2, loggedInOffCd);
                preparedStatement.setString(2, dobj.getApplNo().trim().toUpperCase());
                preparedStatement.setString(3, dobj.getRegnNo().trim().toUpperCase());
                preparedStatement.setString(4, dobj.getHsrpFlag().trim().toUpperCase());
                preparedStatement.setString(5, loggedInEmpCd);

                preparedStatement.setString(6, dobj.getHsrpNoFront().trim().toUpperCase());
                preparedStatement.setString(7, dobj.getHsrpNoBack().trim().toUpperCase());
                preparedStatement.setDate(8, new java.sql.Date(JSFUtils.getStringToDate(dobj.getHsrpFixDt()).getTime()));
                preparedStatement.setDouble(9, Double.parseDouble(dobj.getHsrpFixAmt()));
                preparedStatement.setDate(10, new java.sql.Date(JSFUtils.getStringToDate(dobj.getHsrpAmtTakenOn()).getTime()));
                preparedStatement.setString(11, dobj.getApplNo().trim().toUpperCase());
                preparedStatement.addBatch();
                ServerUtil.updateHSRPStatus(tmgr, dobj.getApplNo().trim().toUpperCase(), currentStateCd, dobj.getRegnNo().trim().toUpperCase(), loggedInEmpCd);

            }
            if (batchPrepared) {
                preparedStatement.executeBatch();
                inserted = true;
            }

            if (inserted) {
                res = "SUCCESS: Record inserted";
                tmgr.commit();
            } else {
                res = "FAILURE: Record can not be inserted";
            }

        } catch (BatchUpdateException sqlException) {
            LOGGER.error(sqlException.toString() + " " + sqlException.getStackTrace()[0]);
            throw sqlException;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw ex;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return res;
    }

    public List validateFile(ArrayList<HSRPFileUploadDobj> list, List recordsToBeUploadedRegNoList, List frontLaserList, List rearLaserList, List lineNumbers, int offCd) {
        int val1 = 1;
        ArrayList errorList = new ArrayList();
        String errorLine = "";
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement psmt = null;
        boolean regNoRepeatInFile = false;
        boolean spaceInReg = false;
        boolean frontLaserRepeatInFile = false;
        boolean rearLaserRepeatInFile = false;
        int lenReg = 0;
        ResultSet rsVaHsrp = null;
        ResultSet rsVhaHsrp = null;
        ResultSet rsVtHsrp = null;
        ResultSet rsVtHsrpWithoutAppl = null;
        ResultSet rs = null;
        boolean dataInVhaHsrp = false;
        boolean dataInVaHsrp = false;
        boolean dataInVtHsrp = false;
        boolean dataInVtHsrpWithoutAppl = false;
        String strSQL;
        try {
            for (int val = 0; val < list.size(); val++) {
                errorLine = "Line-" + val1 + ": Refer ";
                HSRPFileUploadDobj dobj = list.get(val);
                //-------------------------vahsrp----------------------------

                if (!(dobj.getRegnNo() == null || dobj.getRegnNo().equalsIgnoreCase(""))) {
                    tmgr = new TransactionManagerReadOnly("HSRPFileUploadImpl.validateFile()-1");
                    String strSQLVaHsrp = "SELECT * FROM " + TableList.VA_HSRP + " WHERE state_cd=? "
                            + " and regn_no = ? ";
                    psmt = tmgr.prepareStatement(strSQLVaHsrp);
                    psmt.setString(1, Util.getUserStateCode().trim().toUpperCase());
                    psmt.setString(2, dobj.getRegnNo().toUpperCase().trim());
                    rsVaHsrp = tmgr.fetchDetachedRowSet();
                    if (rsVaHsrp.next()) {
                        dataInVaHsrp = true;
                    } else {
                        dataInVaHsrp = false;
                    }

                }
                //-------------------------vhahsrp-----------------------------
                String strSQLVhaHsrp;
                if (!(dobj.getApplNo() == null || dobj.getApplNo().equalsIgnoreCase("") || (dobj.getRegnNo() == null || dobj.getRegnNo().equalsIgnoreCase("")))) {
                    String strSQLVhaHsrp1 = "SELECT * FROM " + TableList.VHA_HSRP + " WHERE state_cd=? "
                            + " and appl_no = ? and regn_no = ? ";

                    String strSQLVhaHsrp2 = " and off_cd=? ";

                    if (offCd == 0) {
                        strSQLVhaHsrp = strSQLVhaHsrp1;
                    } else {
                        strSQLVhaHsrp = strSQLVhaHsrp1 + strSQLVhaHsrp2;
                    }
                    tmgr = new TransactionManagerReadOnly("HSRPFileUploadImpl.validateFile()-2");
                    psmt = tmgr.prepareStatement(strSQLVhaHsrp);
                    psmt.setString(1, Util.getUserStateCode().trim().toUpperCase());
                    psmt.setString(2, dobj.getApplNo().toUpperCase().trim());
                    psmt.setString(3, dobj.getRegnNo().toUpperCase().trim());
                    if (offCd != 0) {
                        psmt.setInt(4, Util.getSelectedSeat().getOff_cd());
                    }

                    rsVhaHsrp = tmgr.fetchDetachedRowSet();
                    if (rsVhaHsrp.next()) {
                        dataInVhaHsrp = true;
                    } else {
                        dataInVhaHsrp = false;
                    }

                }
                //---------------------------------vthsrp-------------------------------
                if (!(dobj.getRegnNo() == null || dobj.getRegnNo().equalsIgnoreCase(""))) {
                    String strSQLvtHsrp = "SELECT * FROM " + TableList.VT_HSRP + " WHERE  "
                            + " regn_no = ? and appl_no = ? ";
                    tmgr = new TransactionManagerReadOnly("HSRPFileUploadImpl.validateFile()-3");
                    psmt = tmgr.prepareStatement(strSQLvtHsrp);
                    psmt.setString(1, dobj.getRegnNo().toUpperCase().trim());
                    psmt.setString(2, dobj.getApplNo().toUpperCase().trim());
                    rsVtHsrp = tmgr.fetchDetachedRowSet();
                    if (rsVtHsrp.next()) {
                        dataInVtHsrp = true;
                    } else {
                        dataInVtHsrp = false;
                    }
                }

                //--------------------------------------------------

                if (!(dobj.getRegnNo() == null || dobj.getRegnNo().equalsIgnoreCase(""))) {
                    String strSQLvtHsrpWithoutAppl = "SELECT * FROM " + TableList.VT_HSRP + " WHERE  "
                            + " regn_no = ?  ";
                    tmgr = new TransactionManagerReadOnly("HSRPFileUploadImpl.validateFile()-4");
                    psmt = tmgr.prepareStatement(strSQLvtHsrpWithoutAppl);
                    psmt.setString(1, dobj.getRegnNo().toUpperCase().trim());
                    rsVtHsrpWithoutAppl = tmgr.fetchDetachedRowSet();
                    if (rsVtHsrpWithoutAppl.next()) {
                        dataInVtHsrpWithoutAppl = true;
                    } else {
                        dataInVtHsrpWithoutAppl = false;
                    }
                }

                //--------------------------------------------------

                //A
                if (!(dobj.getRegnNo() == null || dobj.getRegnNo().equalsIgnoreCase(""))) {
                    lenReg = dobj.getRegnNo().length();
                    for (int i = 0; i < lenReg; i++) {
                        if ((Character.isSpaceChar(dobj.getRegnNo().charAt(i)))) {
                            spaceInReg = true;
                        }
                    }
                    if (spaceInReg || (dobj.getRegnNo().length() > 10)) {
                        if (errorLine.equalsIgnoreCase("Line-" + val1 + ": Refer ")) {
                            errorLine += " (a)";
                        } else {
                            errorLine += ", (a)";
                        }
                    }
                }
                //B  Transaction not initiated for this Vehicle.
                if (!dataInVaHsrp && !dataInVhaHsrp && !dataInVtHsrp) {
                    if (errorLine.equalsIgnoreCase("Line-" + val1 + ": Refer ")) {
                        errorLine += " (b)";
                    } else {
                        errorLine += ", (b)";
                    }
                } // D HSRP Flat File has not been generated. 
                if (dataInVaHsrp) {
                    if (errorLine.equalsIgnoreCase("Line-" + val1 + ": Refer ")) {
                        errorLine += " (c)";
                    } else {
                        errorLine += ", (c)";
                    }

                } //C  The combination of Application no and registration no is not valid.
                if (!dataInVhaHsrp) {
                    if (errorLine.equalsIgnoreCase("Line-" + val1 + ": Refer ")) {
                        errorLine += " (d)";
                    } else {
                        errorLine += ", (d)";
                    }
                }// E Record has already been uploaded for requested application no and registration no.
                if (dataInVtHsrp) {
                    if (errorLine.equalsIgnoreCase("Line-" + val1 + ": Refer ")) {
                        errorLine += " (e)";
                    } else {
                        errorLine += ", (e)";
                    }
                }

                //F
                if (dobj.getHsrpFlag() != null) {
                    if (dobj.getHsrpFlag().equals("NB") || dobj.getHsrpFlag().equals("OB")) {
                        strSQL = "(SELECT regn_no FROM " + TableList.VT_HSRP + " WHERE "
                                + " (hsrp_no_front = ? or hsrp_no_back= ?)) "
                                + " union all "
                                + " (SELECT regn_no FROM " + TableList.VH_HSRP + " WHERE "
                                + " (hsrp_no_front = ? or hsrp_no_back= ?)) ";
                        tmgr = new TransactionManagerReadOnly("HSRPFileUploadImpl.validateFile()-5");
                        psmt = tmgr.prepareStatement(strSQL);

                        psmt.setString(1, dobj.getHsrpNoFront().toUpperCase().trim());
                        psmt.setString(2, dobj.getHsrpNoBack().toUpperCase().trim());
                        psmt.setString(3, dobj.getHsrpNoFront().toUpperCase().trim());
                        psmt.setString(4, dobj.getHsrpNoBack().toUpperCase().trim());
                        rs = tmgr.fetchDetachedRowSet();
                        if (rs.next()) {
                            if (errorLine.equalsIgnoreCase("Line-" + val1 + ": Refer ")) {
                                errorLine += " (f)";
                            } else {
                                errorLine += ", (f)";
                            }

                        }
                    } //G
                    else if (dobj.getHsrpFlag().equals("DF")) {
                        boolean frontError = false;
                        boolean rearError = false;
                        strSQL = "(SELECT regn_no FROM " + TableList.VT_HSRP + " WHERE  "
                                + " hsrp_no_front = ? ) "
                                + " union all "
                                + "(SELECT regn_no FROM " + TableList.VH_HSRP + " WHERE  "
                                + " hsrp_no_front = ? )";
                        tmgr = new TransactionManagerReadOnly("HSRPFileUploadImpl.validateFile()-6");
                        psmt = tmgr.prepareStatement(strSQL);
                        psmt.setString(1, dobj.getHsrpNoFront().toUpperCase().trim());
                        psmt.setString(2, dobj.getHsrpNoFront().toUpperCase().trim());

                        rs = tmgr.fetchDetachedRowSet();
                        if (rs.next()) {
                            frontError = true;
                        }

                        strSQL = "SELECT * FROM " + TableList.VT_HSRP + " WHERE regn_no = ?  and "
                                + "hsrp_no_back = ?";
                        tmgr = new TransactionManagerReadOnly("HSRPFileUploadImpl.validateFile()-7");
                        psmt = tmgr.prepareStatement(strSQL);
                        psmt.setString(1, dobj.getRegnNo().toUpperCase().trim());
                        psmt.setString(2, dobj.getHsrpNoBack().toUpperCase().trim());
                        rs = tmgr.fetchDetachedRowSet();
                        if (!rs.next()) {
                            rearError = true;

                        }
                        if (rearError || frontError) {
                            if (errorLine.equalsIgnoreCase("Line-" + val1 + ": Refer ")) {
                                errorLine += " (g)";
                            } else {
                                errorLine += ", (g)";
                            }
                        }
                    } //H
                    else if (dobj.getHsrpFlag().equals("DR")) {
                        boolean frontError = false;
                        boolean rearError = false;
                        strSQL = "(SELECT regn_no FROM " + TableList.VT_HSRP + " WHERE  "
                                + " hsrp_no_back = ?)"
                                + " union all "
                                + "(SELECT regn_no FROM " + TableList.VH_HSRP + " WHERE  "
                                + " hsrp_no_back = ?) ";
                        tmgr = new TransactionManagerReadOnly("HSRPFileUploadImpl.validateFile()-8");
                        psmt = tmgr.prepareStatement(strSQL);
                        psmt.setString(1, dobj.getHsrpNoBack().toUpperCase().trim());
                        psmt.setString(2, dobj.getHsrpNoBack().toUpperCase().trim());
                        rs = tmgr.fetchDetachedRowSet();
                        if (rs.next()) {
                            frontError = true;

                        }
                        strSQL = "SELECT * FROM " + TableList.VT_HSRP + " WHERE regn_no = ?  and "
                                + "hsrp_no_front = ?";
                        tmgr = new TransactionManagerReadOnly("HSRPFileUploadImpl.validateFile()-9");
                        psmt = tmgr.prepareStatement(strSQL);
                        psmt.setString(1, dobj.getRegnNo().toUpperCase().trim());
                        psmt.setString(2, dobj.getHsrpNoFront().toUpperCase().trim());
                        rs = tmgr.fetchDetachedRowSet();
                        if (!rs.next()) {
                            rearError = true;
                        }


                        if (rearError || frontError) {
                            if (errorLine.equalsIgnoreCase("Line-" + val1 + ": Refer ")) {
                                errorLine += " (h)";
                            } else {
                                errorLine += ", (h)";
                            }
                        }
                    }
                }

                //I
                regNoRepeatInFile = false;
                for (int k = 0; k < recordsToBeUploadedRegNoList.size(); k++) {
                    if (!(k == val)) {
                        if (recordsToBeUploadedRegNoList.get(k).toString().equalsIgnoreCase(dobj.getRegnNo())) {
                            regNoRepeatInFile = true;
                        }
                    }
                }
                if (regNoRepeatInFile) {
                    if (errorLine.equalsIgnoreCase("Line-" + val1 + ": Refer ")) {
                        errorLine += " (i)";
                    } else {
                        errorLine += ", (i)";
                    }
                }
                //J
                frontLaserRepeatInFile = false;
                for (int k = 0; k < frontLaserList.size(); k++) {
                    if (!(k == val)) {
                        if (frontLaserList.get(k).toString().equalsIgnoreCase(dobj.getHsrpNoFront())) {
                            frontLaserRepeatInFile = true;
                        }
                    }

                }
                if (!(dobj.getHsrpNoFront() == null || dobj.getHsrpNoFront().equals(""))) {
                    if (frontLaserRepeatInFile || dobj.getHsrpNoFront().length() > 20) {
                        if (errorLine.equalsIgnoreCase("Line-" + val1 + ": Refer ")) {
                            errorLine += " (j)";
                        } else {
                            errorLine += ", (j)";
                        }
                    }
                }

                //K
                rearLaserRepeatInFile = false;
                for (int k = 0; k < rearLaserList.size(); k++) {
                    if (!(k == val)) {
                        if (rearLaserList.get(k).toString().equalsIgnoreCase(dobj.getHsrpNoBack())) {
                            rearLaserRepeatInFile = true;
                        }
                    }

                }
                if (!(dobj.getHsrpNoBack() == null || dobj.getHsrpNoBack().equals(""))) {
                    if (rearLaserRepeatInFile || dobj.getHsrpNoBack().length() > 20) {
                        if (errorLine.equalsIgnoreCase("Line-" + val1 + ": Refer ")) {
                            errorLine += " (k)";
                        } else {
                            errorLine += ", (k)";
                        }
                    }
                }
                //L
                if (dobj.getHsrpFlag().equals("DB")) {
                    boolean error = false;

                    strSQL = " (SELECT regn_no FROM " + TableList.VT_HSRP + " WHERE  "
                            + " hsrp_no_front = ? or hsrp_no_back= ? ) "
                            + " union all "
                            + " ( SELECT regn_no FROM " + TableList.VH_HSRP + " WHERE  "
                            + " hsrp_no_front = ? or hsrp_no_back= ? ) ";
                    tmgr = new TransactionManagerReadOnly("HSRPFileUploadImpl.validateFile()-8");
                    psmt = tmgr.prepareStatement(strSQL);
                    psmt.setString(1, dobj.getHsrpNoFront().toUpperCase().trim());
                    psmt.setString(2, dobj.getHsrpNoBack().toUpperCase().trim());
                    psmt.setString(3, dobj.getHsrpNoFront().toUpperCase().trim());
                    psmt.setString(4, dobj.getHsrpNoBack().toUpperCase().trim());

                    rs = tmgr.fetchDetachedRowSet();
                    if (rs.next()) {
                        error = true;

                    }


                    if (error) {
                        if (errorLine.equalsIgnoreCase("Line-" + val1 + ": Refer ")) {
                            errorLine += " (l)";
                        } else {
                            errorLine += ", (l)";
                        }
                    }
                }
                //M
                if (!(dobj.getHsrpFixDt() == null || dobj.getHsrpFixDt().equalsIgnoreCase(""))) {
                    // If HSRP Fixation Date is more than currentdate

                    boolean errorDate = checkDateLessThanOrEqualToCurrentDate(dobj.getHsrpFixDt());
                    if (errorDate) {
                        if (errorLine.equalsIgnoreCase("Line-" + val1 + ": Refer ")) {
                            errorLine += " (m)";
                        } else {
                            errorLine += ", (m)";
                        }
                    }
                }


                //N
                if (!(dobj.getHsrpAmtTakenOn() == null || dobj.getHsrpAmtTakenOn().equalsIgnoreCase(""))) {

                    boolean errorDate = checkDateLessThanOrEqualToCurrentDate(dobj.getHsrpAmtTakenOn());
                    if (errorDate) {
                        if (errorLine.equalsIgnoreCase("Line-" + val1 + ": Refer ")) {
                            errorLine += " (n)";
                        } else {
                            errorLine += ", (n)";
                        }
                    }

                }
                //O
                boolean retValue = false;
                if (String.valueOf(dobj.getHsrpFixAmt()) != null && String.valueOf(dobj.getHsrpFixAmt()).trim().length() > 0) {
                    try {
                        Double.parseDouble(String.valueOf(dobj.getHsrpFixAmt()));
                        retValue = true;
                    } catch (NumberFormatException ne) {
                        // Do nothing in the catch as retValue is already false.
                    }
                }
                if (!(dobj.getHsrpFixAmt() == null || dobj.getHsrpFixAmt().equals(""))) {
                    boolean errorInAmmount = false;
                    if (!retValue) {
                        errorInAmmount = true;
                    }

                    if (!(dobj.getHsrpFixAmt().contains("."))) {
                        if ((String.valueOf(dobj.getHsrpFixAmt()).length() > 7)) {
                            errorInAmmount = true;
                        }
                    }
                    if ((String.valueOf(dobj.getHsrpFixAmt()).length() > 10) || String.valueOf(dobj.getHsrpFixAmt()).indexOf('.') > 8) {
                        errorInAmmount = true;
                    }
                    if (String.valueOf(dobj.getHsrpFixAmt()).indexOf('.') != Integer.parseInt(
                            "-1")) {
                        String valBeforeDecimal = String.valueOf(dobj.getHsrpFixAmt()).split("\\.")[0];
                        String valAfterDecimal = String.valueOf(dobj.getHsrpFixAmt()).split("\\.")[1];
                        if (valBeforeDecimal.length() > 7 || valAfterDecimal.length() > 2) {
                            errorInAmmount = true;
                        }
                    }
                    if (errorInAmmount) {
                        if (errorLine.equalsIgnoreCase("Line-" + val1 + ": Refer ")) {
                            errorLine += " (o)";
                        } else {
                            errorLine += ", (o)";
                        }
                    }
                }

                //P
                if (dobj.getApplNo() == null || dobj.getRegnNo() == null || dobj.getHsrpFlag() == null
                        || dobj.getHsrpNoFront() == null || dobj.getHsrpNoBack() == null
                        || dobj.getHsrpFixDt() == null || dobj.getHsrpFixAmt() == null || dobj.getHsrpAmtTakenOn() == null
                        || dobj.getApplNo().equalsIgnoreCase("") || dobj.getRegnNo().equalsIgnoreCase("") || dobj.getHsrpFlag().equalsIgnoreCase("")
                        || dobj.getHsrpNoFront().equalsIgnoreCase("") || dobj.getHsrpNoBack().equalsIgnoreCase("")
                        || dobj.getHsrpFixDt().equalsIgnoreCase("") || dobj.getHsrpFixAmt().equalsIgnoreCase("")
                        || dobj.getHsrpAmtTakenOn().equalsIgnoreCase("")) {
                    if (errorLine.equalsIgnoreCase("Line-" + val1 + ": Refer ")) {
                        errorLine += " (p)";
                    } else {
                        errorLine += ", (p)";
                    }
                }
                //Q
                if ((Boolean) lineNumbers.get(val)) {
                    if (errorLine.equalsIgnoreCase("Line-" + val1 + ": Refer ")) {
                        errorLine += " (q)";
                    } else {
                        errorLine += ", (q)";
                    }
                }
                //R
                if (!(dobj.getApplNo() == null || dobj.getApplNo().equals(""))) {
                    if (dobj.getApplNo().length() > 16) {
                        if (errorLine.equalsIgnoreCase("Line-" + val1 + ": Refer ")) {
                            errorLine += " (r)";
                        } else {
                            errorLine += ", (r)";
                        }
                    }
                }
                //S
                boolean validFlag = false;
                if (!(dobj.getHsrpFlag() == null || dobj.getHsrpFlag().equals(""))) {
                    String[][] data = MasterTableFiller.masterTables.VM_HSRP_FLAG.getData();
                    for (int i = 0; i < data.length; i++) {
                        if (data[i][0].equalsIgnoreCase(dobj.getHsrpFlag().toUpperCase().trim())) {
                            validFlag = true;
                        } else {
                            validFlag = true;
                        }
                    }
                    if (!(dobj.getRegnNo() == null || dobj.getRegnNo().equalsIgnoreCase(""))) {
                        if (dobj.getHsrpFlag().equals("NB") || dobj.getHsrpFlag().equals("OB")) {
                            if (dataInVtHsrpWithoutAppl) {
                                validFlag = false;
                            } else {
                                validFlag = true;
                            }
                        } else if (dobj.getHsrpFlag().equals("DB") || dobj.getHsrpFlag().equals("DF") || dobj.getHsrpFlag().equals("DR")) {
                            if (!dataInVtHsrpWithoutAppl) {
                                validFlag = false;
                            } else {
                                validFlag = true;
                            }
                        }
                    }
                    if (!(dobj.getApplNo() == null || dobj.getApplNo().equalsIgnoreCase("") || (dobj.getRegnNo() == null || dobj.getRegnNo().equalsIgnoreCase("")))) {
                        rsVhaHsrp.beforeFirst();
                        if (rsVhaHsrp.next()) {
                            if (!rsVhaHsrp.getString("hsrp_flag").equalsIgnoreCase(dobj.getHsrpFlag())) {
                                validFlag = false;
                            } else {
                                validFlag = true;
                            }
                        }
                    }
                    if (!validFlag) {
                        if (errorLine.equalsIgnoreCase("Line-" + val1 + ": Refer ")) {
                            errorLine += " (s)";
                        } else {
                            errorLine += ", (s)";
                        }
                    }
                }

                if (!errorLine.equalsIgnoreCase("Line-" + val1 + ": Refer ")) {
                    errorList.add(errorLine);
                }
                val1++;
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }

        }
        return errorList;
    }

    public static boolean checkDateLessThanOrEqualToCurrentDate(String dateToCheck) {
        boolean errorDate = false;
        Calendar calc = Calendar.getInstance();
        Date currDate = calc.getTime();
        try {

            DateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
            dt.setLenient(false);
            Date d = dt.parse(dateToCheck);

            if (currDate.compareTo(JSFUtils.getStringToDate(dateToCheck)) < 0) {
                errorDate = true;
            }
        } catch (Exception ex) {
            errorDate = true;
        }
        return errorDate;
    }
}
