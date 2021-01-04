/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TaxExemptiondobj;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class TaxExemptionImpl {

    private static final Logger LOGGER = Logger.getLogger(TaxExemptionImpl.class);

    public TaxExemptiondobj getTaxExemptDetails(String regn_no, String appl_no) {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        TaxExemptiondobj dobj = null;

        try {
            tmgr = new TransactionManager("getTaxExemptDetails");
            ps = tmgr.prepareStatement("SELECT * FROM " + TableList.VA_TAX_EXEM + " WHERE regn_no=? and appl_no=?");
            ps.setString(1, regn_no);
            ps.setString(2, appl_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                dobj = new TaxExemptiondobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setExemFromDt(rs.getDate("exem_fr"));
                dobj.setExemTo(rs.getDate("exem_to"));
                dobj.setAuthBy(rs.getString("exem_by"));
                dobj.setPermissionNo(rs.getString("perm_no"));
                dobj.setPermissionDt(rs.getDate("perm_dt"));
                dobj.setPerpose(rs.getString("remark"));
            }

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
        return dobj;
    }

    public TaxExemptiondobj getDeleteTaxExemptDetails(String regn_no, String appl_no) {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        TaxExemptiondobj dobj = null;
        SimpleDateFormat sd = new SimpleDateFormat("dd-MMM-yyyy");

        try {
            tmgr = new TransactionManager("getDeleteTaxExemptDetails");
            ps = tmgr.prepareStatement("SELECT * FROM " + TableList.VA_TAX_EXEM_CANCEL + " WHERE regn_no=? and appl_no=?");
            ps.setString(1, regn_no);
            ps.setString(2, appl_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                dobj = new TaxExemptiondobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setExemFromDt(rs.getDate("exem_fr"));
                dobj.setExemTo(rs.getDate("exem_to"));
                dobj.setAuthBy(rs.getString("exem_by"));
                dobj.setPermissionNo(rs.getString("perm_no"));
                dobj.setPermissionDt(rs.getDate("perm_dt"));
                dobj.setPerpose(rs.getString("remark"));
            }

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
        return dobj;
    }

    public ArrayList<TaxExemptiondobj> getTaxExemptDetailsVT(String regn_no) {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        ArrayList<TaxExemptiondobj> exemList = new ArrayList();
        TaxExemptiondobj dobj = null;
        SimpleDateFormat sd = new SimpleDateFormat("dd-MMM-yyyy");

        try {
            tmgr = new TransactionManager("getTaxExemptDetails");
            ps = tmgr.prepareStatement("SELECT * FROM " + TableList.VT_TAX_EXEM + " WHERE regn_no=? ");
            ps.setString(1, regn_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) // found
            {
                dobj = new TaxExemptiondobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setExemFromDt(rs.getDate("exem_fr"));
                dobj.setExemTo(rs.getDate("exem_to"));
                dobj.setAuthBy(rs.getString("exem_by"));
                dobj.setPermissionNo(rs.getString("perm_no"));
                dobj.setPermissionDt(rs.getDate("perm_dt"));
                dobj.setPerpose(rs.getString("remark"));
                exemList.add(dobj);
            }

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
        return exemList;
    }

    public static String updateTaxExemptionDetail(Status_dobj status, TaxExemptiondobj dobj) throws Exception {
        String appl_no = "";
        int i = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-YYYY");
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        int action_cd = 0;
        int actionCodeArray[] = null;

        String sql = "INSERT INTO " + TableList.VA_TAX_EXEM + "(state_cd,off_cd,regn_no,appl_no,exem_fr,"
                + " exem_to,exem_by,perm_no,perm_dt,remark,user_cd,op_dt)"
                + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";


        try {
            tmgr = new TransactionManager("updateTaxExemptionDetail");
            appl_no = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());//generate a new application no. 

            //if appl no is null check is required

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setString(3, dobj.getRegn_no());
            ps.setString(4, appl_no);
            ps.setDate(5, new java.sql.Date(dobj.getExemFromDt().getTime()));
            ps.setDate(6, new java.sql.Date(dobj.getExemTo().getTime()));
            ps.setString(7, dobj.getAuthBy());
            ps.setString(8, dobj.getPermissionNo());
            ps.setDate(9, new java.sql.Date(dobj.getPermissionDt().getTime()));
            ps.setString(10, dobj.getPerpose());
            ps.setLong(11, Long.parseLong(Util.getEmpCode()));
            i = ps.executeUpdate();
            if (i > 0) {
                actionCodeArray = ServerUtil.getInitialAction(tmgr, status.getState_cd(), status.getPur_cd(), null);

                if (actionCodeArray == null) {
                    throw new VahanException("Initial Action Code is Not Available!");
                }

                action_cd = actionCodeArray[0];
                status.setAppl_no(appl_no);
                status.setAction_cd(action_cd);//Initial Action_cd
                ServerUtil.insertIntoVaStatus(tmgr, status);
                ServerUtil.insertIntoVaDetails(tmgr, status);

                status.setStatus(TableConstants.STATUS_COMPLETE);
                status = ServerUtil.webServiceForNextStage(status, tmgr);
                ServerUtil.fileFlow(tmgr, status); //for updateing va_status

                tmgr.commit();
            } else {
                appl_no = "";
            }
        } catch (VahanException ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);

        } finally {
            if (ps != null) {
                ps.close();
            }
            if (tmgr != null) {
                tmgr.release();
            }
        }
        return appl_no;
    }

    public static String deleteTaxExemptionDetail(Status_dobj status, TaxExemptiondobj dobj) throws VahanException {
        String appl_no = "";
        int i = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-YYYY");
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        int action_cd = 0;
        int actionCodeArray[] = null;

        String sql = "INSERT INTO " + TableList.VA_TAX_EXEM_CANCEL + "(state_cd,off_cd,regn_no,appl_no,exem_fr,"
                + " exem_to,exem_by,perm_no,perm_dt,remark,user_cd,op_dt)"
                + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?,  ?,?, ?, current_timestamp)";


        try {
            tmgr = new TransactionManager("updateTaxExemptionDetail");
            appl_no = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());//generate a new application no. 

            //if appl no is null check is required

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            ps.setString(3, dobj.getRegn_no());
            ps.setString(4, appl_no);
            ps.setDate(5, new java.sql.Date(dobj.getExemFromDt().getTime()));
            ps.setDate(6, new java.sql.Date(dobj.getExemTo().getTime()));
            ps.setString(7, dobj.getAuthBy());
            ps.setString(8, dobj.getPermissionNo());
            ps.setDate(9, new java.sql.Date(dobj.getPermissionDt().getTime()));
            ps.setString(10, dobj.getPerpose());
            ps.setLong(11, Long.parseLong(Util.getEmpCode()));
            i = ps.executeUpdate();
            if (i > 0) {
                actionCodeArray = ServerUtil.getInitialAction(tmgr, status.getState_cd(), status.getPur_cd(), null);

                if (actionCodeArray == null) {
                    throw new VahanException("Initial Action Code is Not Available!");
                }

                action_cd = actionCodeArray[0];
                status.setAppl_no(appl_no);
                status.setAction_cd(action_cd);//Initial Action_cd
                ServerUtil.insertIntoVaStatus(tmgr, status);
                ServerUtil.insertIntoVaDetails(tmgr, status);

                status.setStatus(TableConstants.STATUS_COMPLETE);
                status = ServerUtil.webServiceForNextStage(status, tmgr);
                ServerUtil.fileFlow(tmgr, status); //for updateing va_status

                tmgr.commit();
            } else {
                appl_no = "";
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return appl_no;
    }

    public void update_TaxExempt_Status(TaxExemptiondobj taxExemptDobj, TaxExemptiondobj taxExempt_dobj_prv, Status_dobj status_dobj, String changedData) throws Exception {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;

        try {

            tmgr = new TransactionManager("update_TaxExempt_Status");
            //=================WEB SERVICES FOR NEXTSTAGE START=========//
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);

            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_TAX_EXEMPT_ENTRY
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_TAX_EXEMPT_VERIFICATION
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_TAX_EXEMPT_APPROVAL) {

                if ((changedData != null && !changedData.equals("")) || taxExempt_dobj_prv == null) {
                    insertUpdateTaxExempt(tmgr, taxExemptDobj);//when there is change by user or Entry Scrutiny
                }

            }

            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_TAX_EXEMPT_APPROVAL
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                sql = "insert into " + TableList.VT_TAX_EXEM
                        + " SELECT state_cd, off_cd, regn_no, exem_fr,exem_to,"
                        + "       exem_by,perm_no,perm_dt,remark, ? as user_cd, current_timestamp "
                        + " FROM " + TableList.VA_TAX_EXEM
                        + " WHERE appl_no=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, taxExemptDobj.getAppl_no());
                ps.executeUpdate();

                sql = "SELECT state_cd, off_cd, regn_no, exem_fr,exem_to,"
                        + "       exem_by,perm_no,perm_dt,remark,  current_timestamp "
                        + " FROM " + TableList.VA_TAX_EXEM
                        + " WHERE appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, taxExemptDobj.getAppl_no());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    String regno = rs.getString("regn_no");
                    Date taxUpto = rs.getDate("exem_to");
                    int purCd = 58;
                    TaxServer_Impl.insertUpdateTaxDefaulter(regno, taxUpto, purCd, tmgr);

                }

                sql = "INSERT INTO " + TableList.VHA_TAX_EXEM
                        + " SELECT *, current_timestamp + interval '1 second' as moved_on, ? as moved_by "
                        + "  FROM  " + TableList.VA_TAX_EXEM
                        + " WHERE  appl_no=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, taxExemptDobj.getAppl_no());
                ps.executeUpdate();

                sql = "DELETE FROM " + TableList.VA_TAX_EXEM + " where appl_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, taxExemptDobj.getAppl_no());
                ps.executeUpdate();

                //for updating the status of application when it is approved start
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                //for updating the status of application when it is approved end
            }

            insertIntoVhaChangedData(tmgr, taxExemptDobj.getAppl_no(), changedData);//for saving the data into table those are changed by the user

            ServerUtil.fileFlow(tmgr, status_dobj); //for updateing va_status and vha status for new role,seat for new emp

            tmgr.commit();//Commiting data here....

        } catch (VahanException ve) {
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);

        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }

    }

    public void update_CancelTaxExempt_Status(TaxExemptiondobj taxExemptDobj, TaxExemptiondobj taxExempt_dobj_prv, Status_dobj status_dobj, String changedData) throws Exception {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;

        try {

            tmgr = new TransactionManager("update_TaxExempt_Status");
            //=================WEB SERVICES FOR NEXTSTAGE START=========//
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);

            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_TAX_EXEMPT_CANCEL_ENTRY
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_TAX_EXEMPT_CANCEL_VERIFICATION
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_TAX_EXEMPT_CANCEL_APPROVAL) {

                if ((changedData != null && !changedData.equals("")) || taxExempt_dobj_prv == null) {
                    insertUpdateTaxExemptCancel(tmgr, taxExemptDobj);//when there is change by user or Entry Scrutiny
                }

            }
            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_TAX_EXEMPT_CANCEL_APPROVAL
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                sql = "insert into " + TableList.VH_TAX_EXEM
                        + " SELECT current_timestamp as moved_on ,? as moved_by,state_cd, off_cd,? as appl_no, regn_no, exem_fr,exem_to,"
                        + "       exem_by,perm_no,perm_dt,remark, ? as user_cd, current_timestamp "
                        + " FROM " + TableList.VA_TAX_EXEM_CANCEL
                        + " WHERE appl_no=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, taxExemptDobj.getAppl_no());
                ps.setString(3, Util.getEmpCode());
                ps.setString(4, taxExemptDobj.getAppl_no());
                ps.executeUpdate();
                sql = "INSERT INTO " + TableList.VHA_TAX_EXEM_CANCEL
                        + " SELECT current_timestamp + interval '1 second' as moved_on, ? as moved_by , *  "
                        + "  FROM  " + TableList.VA_TAX_EXEM_CANCEL
                        + " WHERE  appl_no=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, taxExemptDobj.getAppl_no());
                ps.executeUpdate();

                ServerUtil.deleteFromTable(tmgr, "", taxExemptDobj.getAppl_no(), TableList.VA_TAX_EXEM_CANCEL);
                sql = "DELETE FROM  " + TableList.VT_TAX_EXEM + " WHERE  regn_no=? and state_cd=? and off_cd=? and exem_fr=? and exem_to=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, taxExemptDobj.getRegn_no());
                ps.setString(2, taxExemptDobj.getState_cd());
                ps.setInt(3, taxExemptDobj.getOff_cd());
                ps.setDate(4, (java.sql.Date) taxExemptDobj.getExemFromDt());
                ps.setDate(5, (java.sql.Date) taxExemptDobj.getExemTo());
                ps.executeUpdate();
                //for updating the status of application when it is approved start
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                //for updating the status of application when it is approved end
            }

            insertIntoVhaChangedData(tmgr, taxExemptDobj.getAppl_no(), changedData);//for saving the data into table those are changed by the user

            ServerUtil.fileFlow(tmgr, status_dobj); //for updateing va_status and vha status for new role,seat for new emp

            tmgr.commit();//Commiting data here....

        } catch (VahanException ve) {
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);

        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }

    }

    public static void insertUpdateTaxExempt(TransactionManager tmgr, TaxExemptiondobj taxExemptDobj) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;
        sql = "SELECT regn_no FROM " + TableList.VA_TAX_EXEM + " where appl_no = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, taxExemptDobj.getAppl_no());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) { //if any record is exist then update otherwise insert it
            insertIntoTaxExemptHistory(tmgr, taxExemptDobj.getAppl_no());
            updateTaxExem(tmgr, taxExemptDobj);
        } else {
            insertIntoTaxExam(tmgr, taxExemptDobj);
        }
    }

    public static void insertUpdateTaxExemptCancel(TransactionManager tmgr, TaxExemptiondobj taxExemptDobj) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;
        sql = "SELECT regn_no FROM " + TableList.VA_TAX_EXEM_CANCEL + " where appl_no = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, taxExemptDobj.getAppl_no());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) { //if any record is exist then update otherwise insert it
            insertIntoTaxExemptCancelHistory(tmgr, taxExemptDobj.getAppl_no());
            updateTaxExemCancel(tmgr, taxExemptDobj);
        } else {
            insertIntoTaxExamCancel(tmgr, taxExemptDobj);
        }
    }

    public static void insertIntoTaxExemptHistory(TransactionManager tmgr, String appl_no) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;

        //inserting data into vha_tax_exem from va_tax_exem
        sql = "INSERT INTO " + TableList.VHA_TAX_EXEM
                + " SELECT *,current_timestamp as moved_on, ? as moved_by "
                + "  FROM  " + TableList.VA_TAX_EXEM
                + " WHERE  appl_no=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setLong(1, Long.parseLong(Util.getEmpCode()));
        ps.setString(2, appl_no);
        ps.executeUpdate();
    } // end of insertIntoTaxExemptHistory

    public static void insertIntoTaxExemptCancelHistory(TransactionManager tmgr, String appl_no) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;

        //inserting data into vha_tax_exem from va_tax_exem
        sql = "INSERT INTO " + TableList.VHA_TAX_EXEM_CANCEL
                + " SELECT current_timestamp as moved_on, ? as moved_by ,* "
                + "  FROM  " + TableList.VA_TAX_EXEM_CANCEL
                + " WHERE  appl_no=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setLong(1, Long.parseLong(Util.getEmpCode()));
        ps.setString(2, appl_no);
        ps.executeUpdate();
    } // end of insertIntoTaxExemptCancelHistory.

    public static void updateTaxExemCancel(TransactionManager tmgr, TaxExemptiondobj taxExempDobj) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;

        //updation of va_tax_exem
        sql = " update " + TableList.VA_TAX_EXEM_CANCEL
                + " set exem_fr=?,"
                + " exem_to=?,"
                + " exem_by=?,"
                + " perm_no=?,"
                + " perm_dt=?,"
                + " remark=?,"
                + " user_cd=?,"
                + " op_dt=current_timestamp"
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setDate(1, new java.sql.Date(taxExempDobj.getExemFromDt().getTime()));
        ps.setDate(2, new java.sql.Date(taxExempDobj.getExemTo().getTime()));
        ps.setString(3, taxExempDobj.getAuthBy());
        ps.setString(4, taxExempDobj.getPermissionNo());
        ps.setDate(5, new java.sql.Date(taxExempDobj.getPermissionDt().getTime()));
        ps.setString(6, taxExempDobj.getPerpose());
        ps.setLong(7, Long.parseLong(Util.getEmpCode()));
        ps.setString(8, taxExempDobj.getAppl_no());
        ps.executeUpdate();
    } // end of update VA_TAX_EXEM_cancel

    public static void updateTaxExem(TransactionManager tmgr, TaxExemptiondobj taxExempDobj) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;

        //updation of va_tax_exem
        sql = " update " + TableList.VA_TAX_EXEM
                + " set exem_fr=?,"
                + " exem_to=?,"
                + " exem_by=?,"
                + " perm_no=?,"
                + " perm_dt=?,"
                + " remark=?,"
                + " user_cd=?,"
                + " op_dt=current_timestamp"
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setDate(1, new java.sql.Date(taxExempDobj.getExemFromDt().getTime()));
        ps.setDate(2, new java.sql.Date(taxExempDobj.getExemTo().getTime()));
        ps.setString(3, taxExempDobj.getAuthBy());
        ps.setString(4, taxExempDobj.getPermissionNo());
        ps.setDate(5, new java.sql.Date(taxExempDobj.getPermissionDt().getTime()));
        ps.setString(6, taxExempDobj.getPerpose());
        ps.setLong(7, Long.parseLong(Util.getEmpCode()));
        ps.setString(8, taxExempDobj.getAppl_no());
        ps.executeUpdate();
    } // end of update VA_TAX_EXEM

    public static void insertIntoTaxExam(TransactionManager tmgr, TaxExemptiondobj taxExempDobj) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VA_TAX_EXEM + "(state_cd,off_cd,regn_no,appl_no,exem_fr,"
                + " exem_to,exem_by,perm_no,perm_dt,remark,user_cd,op_dt)"
                + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getUserStateCode());
        ps.setInt(2, Util.getUserSeatOffCode());
        ps.setString(3, taxExempDobj.getRegn_no());
        ps.setString(4, taxExempDobj.getAppl_no());
        ps.setDate(6, new java.sql.Date(taxExempDobj.getExemFromDt().getTime()));
        ps.setDate(7, new java.sql.Date(taxExempDobj.getExemTo().getTime()));
        ps.setString(8, taxExempDobj.getAuthBy());
        ps.setString(9, taxExempDobj.getPermissionNo());
        ps.setDate(10, new java.sql.Date(taxExempDobj.getPermissionDt().getTime()));
        ps.setString(11, taxExempDobj.getPerpose());
        ps.setLong(12, Long.parseLong(Util.getEmpCode()));
        ps.executeUpdate();
    }

    public static void insertIntoTaxExamCancel(TransactionManager tmgr, TaxExemptiondobj taxExempDobj) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;

        sql = "INSERT INTO " + TableList.VA_TAX_EXEM_CANCEL + "(state_cd,off_cd,regn_no,appl_no,exem_fr,"
                + " exem_to,exem_by,perm_no,perm_dt,remark,user_cd,op_dt)"
                + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getUserStateCode());
        ps.setInt(2, Util.getUserSeatOffCode());
        ps.setString(3, taxExempDobj.getRegn_no());
        ps.setString(4, taxExempDobj.getAppl_no());
        ps.setDate(6, new java.sql.Date(taxExempDobj.getExemFromDt().getTime()));
        ps.setDate(7, new java.sql.Date(taxExempDobj.getExemTo().getTime()));
        ps.setString(8, taxExempDobj.getAuthBy());
        ps.setString(9, taxExempDobj.getPermissionNo());
        ps.setDate(10, new java.sql.Date(taxExempDobj.getPermissionDt().getTime()));
        ps.setString(11, taxExempDobj.getPerpose());
        ps.setLong(12, Long.parseLong(Util.getEmpCode()));
        ps.executeUpdate();
    }

    public static void makeChangeTaxExem(TaxExemptiondobj taxExamDobj, String changedata) throws SQLException, Exception {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("makeChangeTaxExem");
            insertUpdateTaxExempt(tmgr, taxExamDobj);
            ComparisonBeanImpl.updateChangedData(taxExamDobj.getAppl_no(), changedata, tmgr);
            tmgr.commit();
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
    }

    public static void makeChangeTaxExemCancel(TaxExemptiondobj taxExamDobj, String changedata) throws SQLException, Exception {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("makeChangeTaxExem");
            insertUpdateTaxExemptCancel(tmgr, taxExamDobj);
            ComparisonBeanImpl.updateChangedData(taxExamDobj.getAppl_no(), changedata, tmgr);
            tmgr.commit();
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
    }

    public static String checkDurationAlreadyExemptionDetail(String regn_no, Date exemFrDate, Date exemToDate) throws VahanException {
        String exemptFoundBetween = "";
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            tmgr = new TransactionManager("checkDurationAlreadyExemptionDetail");
            ps = tmgr.prepareStatement("select * from " + TableList.VT_TAX_EXEM + " where regn_no =? and ((? between exem_fr and exem_to) or (? between exem_fr and exem_to )) and state_cd=? and off_cd=?");
            ps.setString(1, regn_no);
            ps.setDate(2, new java.sql.Date(exemFrDate.getTime()));
            ps.setDate(3, new java.sql.Date(exemToDate.getTime()));
            ps.setString(4, Util.getUserStateCode());
            ps.setInt(5, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                exemptFoundBetween = "Tax exemption detail not saved!!! Tax already exempted from " + format.format(rs.getDate("exem_fr")) + " to " + format.format(rs.getDate("exem_to"));
            }
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
        return exemptFoundBetween;
    }

    public static String checkDurationAlreadyTaxPaidDetail(String regn_no, Date exemFrDate, Date exemToDate) throws VahanException {
        String exemptFoundBetween = "";
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            tmgr = new TransactionManager("checkDurationAlreadyTaxPaidDetail");
            ps = tmgr.prepareStatement("select * from " + TableList.VT_TAX + " where regn_no =? and tax_upto >=? and state_cd=? and off_cd=?");
            ps.setString(1, regn_no);
            ps.setDate(2, new java.sql.Date(exemToDate.getTime()));
            ps.setString(3, Util.getUserStateCode());
            ps.setInt(4, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                exemptFoundBetween = "Tax exemption Cancel not allowed!!! Tax already pay from " + exemFrDate + " to " + exemToDate;
            }
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
        return exemptFoundBetween;
    }

    public boolean isTaxExempted(String regnNo) {

        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        boolean taxExeptedStatus = false;
        try {

            tmgr = new TransactionManager("isTaxExempted");
            sql = "SELECT exem_to FROM " + TableList.VT_TAX_EXEM
                    + " WHERE regn_no=? order by op_dt desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo.toUpperCase());

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                Date exem_to = rs.getDate("exem_to");
             if (DateUtils.compareDates(DateUtils.getCurrentLocalDate(), exem_to) <= 1) {
                    taxExeptedStatus = true;
                }
            }

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
        return taxExeptedStatus;
    }

    public void insertIntoTaxExemptVH(TransactionManager tmgr, String applNo, String regnNo, String empCode) throws Exception {
        PreparedStatement ps = null;
        String sql = null;
        sql = "INSERT INTO " + TableList.VH_TAX_EXEM
                + " SELECT current_timestamp as moved_on, ? as moved_by,"
                + " a.state_cd, a.off_cd, ?, a.regn_no, a.exem_fr, a.exem_to,"
                + " a.exem_by, a.perm_no, a.perm_dt, a.remark, a.user_cd, a.op_dt"
                + " FROM " + TableList.VT_TAX_EXEM + " a "
                + " WHERE  regn_no=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setLong(1, Long.parseLong(empCode));
        ps.setString(2, applNo);
        ps.setString(3, regnNo);
        ps.executeUpdate();
    } // end of insertIntoTaxExemptVH
}
