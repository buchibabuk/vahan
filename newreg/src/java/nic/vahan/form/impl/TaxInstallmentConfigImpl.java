/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.Fee_Pay_Dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TaxExemptiondobj;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;
import nic.vahan.form.dobj.TaxInstallmentConfigDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;

/**
 *
 * @author tranC106
 */
public class TaxInstallmentConfigImpl {
    
    private static final Logger LOGGER = Logger.getLogger(TaxInstallmentConfigImpl.class);

    /////////////////////////////////////
    public static TaxInstallmentConfigDobj getTaxInstFileNoDetails(String regn_no, String appl_no) {
        
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        TaxInstallmentConfigDobj dobjn = null;
        
        try {
            tmgr = new TransactionManager("TaxInstallmentImplFileNo");
            ps = tmgr.prepareStatement("SELECT * FROM " + TableList.VA_TAX_INSTALLMENT + " WHERE regn_no=? and appl_no=?");
            ps.setString(1, regn_no);
            ps.setString(2, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                dobjn = new TaxInstallmentConfigDobj();
                dobjn.setFilerefno(rs.getString("file_ref_no"));
                dobjn.setOrderissueby(rs.getString("order_iss_by"));
                dobjn.setOrderno(rs.getString("order_no"));
                dobjn.setTaxmode(rs.getString("tax_mode"));
                dobjn.setTaxfromdt(rs.getDate("tax_from"));
                dobjn.setTaxuptodt(rs.getDate("tax_upto"));
                dobjn.setOrderdate(rs.getDate("order_date"));
                
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
        return dobjn;
    }
    ////////////////////////////////////

    public static List<TaxInstallmentConfigDobj> getTaxInstCongigDetails(String regn_no, String appl_no) {
        
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        TaxInstallmentConfigDobj dobj = null;
        List<TaxInstallmentConfigDobj> listimpl = new ArrayList();
        
        try {
            tmgr = new TransactionManager("TaxInstallmentImpl");
            ps = tmgr.prepareStatement("SELECT * FROM " + TableList.VA_TAX_INSTALLMENT_BRKUP + " WHERE regn_no=? and appl_no=?");
            ps.setString(1, regn_no);
            ps.setString(2, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                dobj = new TaxInstallmentConfigDobj();
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegnno(rs.getString("regn_no"));
                dobj.setSerialno(rs.getString("serial_no"));
                dobj.setSerialnotable(rs.getString("serial_no"));
                dobj.setTaxamountinstltable(rs.getLong("tax_amt_instl"));
                dobj.setPayduedt((rs.getDate("pay_due_date")));
                Date addedDate = dobj.getPayduedt();
                DateFormat frm_dte_formatter = new SimpleDateFormat("dd-MMM-yyyy");
                String returnDate = frm_dte_formatter.format(((java.util.Date) addedDate));
                dobj.setDueDateStr(returnDate);
                listimpl.add(dobj);
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
        
        return listimpl;
        
    }
    
    public String InsertIntoVATaxInstallmentConfigInward(Status_dobj status, List<TaxInstallmentConfigDobj> taxInstallmentConfigDobjList) throws VahanException, Exception {
        String appl_no = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        
        int action_cd = 0;
        int actionCodeArray[] = null;
        try {
            tmgr = new TransactionManager("InsertIntoVATaxInstallmentBrkup");
            
            appl_no = ServerUtil.getUniqueApplNo(tmgr, status.getState_cd());//generate a new application no.    

            //////////////////////////////////////////
            TaxInstallmentConfigDobj taxInstallmentConfigDobjni = taxInstallmentConfigDobjList.get(0);
            int j = 1;
            sql = "INSERT INTO " + TableList.VA_TAX_INSTALLMENT + "(state_cd, off_cd, appl_no, regn_no,"
                    + " file_ref_no, order_iss_by, order_no, order_date, tax_from, tax_upto, tax_mode,"
                    + " user_cd, op_dt)"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";
            
            ps = tmgr.prepareStatement(sql);
            ps.setString(j++, status.getState_cd());
            ps.setInt(j++, status.getOff_cd());
            ps.setString(j++, appl_no);
            ps.setString(j++, taxInstallmentConfigDobjni.getRegnno());
            ps.setString(j++, taxInstallmentConfigDobjni.getFilerefno());
            ps.setString(j++, taxInstallmentConfigDobjni.getOrderissueby());
            ps.setString(j++, taxInstallmentConfigDobjni.getOrderno());
            ps.setDate(j++, new java.sql.Date(taxInstallmentConfigDobjni.getOrderdate().getTime()));
            ps.setDate(j++, new java.sql.Date(taxInstallmentConfigDobjni.getTaxfromdt().getTime()));
            ps.setDate(j++, new java.sql.Date(taxInstallmentConfigDobjni.getTaxuptodt().getTime()));
            ps.setString(j++, taxInstallmentConfigDobjni.getTaxmode());
            ps.setString(j++, Util.getEmpCode());
            ps.executeUpdate();

            /////////////////////////////////////////////

            for (TaxInstallmentConfigDobj taxInstallmentConfigDobjn : taxInstallmentConfigDobjList) {
                int i = 1;
                
                sql = "INSERT INTO " + TableList.VA_TAX_INSTALLMENT_BRKUP + "(state_cd, off_cd, appl_no, regn_no,"
                        + "  serial_no,"
                        + " tax_amt_instl, pay_due_date, user_cd, op_dt)"
                        + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";
                
                ps = tmgr.prepareStatement(sql);
                ps.setString(i++, status.getState_cd());
                ps.setInt(i++, status.getOff_cd());
                ps.setString(i++, appl_no);
                ps.setString(i++, taxInstallmentConfigDobjn.getRegnno());
                ps.setString(i++, taxInstallmentConfigDobjn.getSerialnotable());
                ps.setLong(i++, taxInstallmentConfigDobjn.getTaxamountinstltable());
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
                Date returndate = formatter.parse(taxInstallmentConfigDobjn.getDueDateStr());
                ps.setDate(i++, new java.sql.Date(returndate.getTime()));
                ps.setString(i++, Util.getEmpCode());
                ps.executeUpdate();
            }
            
            actionCodeArray = ServerUtil.getInitialAction(tmgr, status.getState_cd(), status.getPur_cd(), null);
            if (actionCodeArray == null) {
                throw new VahanException(Util.getLocaleMsg("invalidActionCode"));
            }
            action_cd = actionCodeArray[0];
            status.setAppl_no(appl_no);
            status.setAction_cd(action_cd);//Initial Action_cd
            status.setAppl_date(ServerUtil.getSystemDateInPostgres());
            ServerUtil.insertIntoVaStatus(tmgr, status);
            ServerUtil.insertIntoVaDetails(tmgr, status);
            
            status.setStatus(TableConstants.STATUS_COMPLETE);
            status = ServerUtil.webServiceForNextStage(status, tmgr);
            ServerUtil.fileFlow(tmgr, status); //for updateing va_status and vha status for new role,seat for new emp

            tmgr.commit();//Commiting data here....

            
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        } catch (VahanException ex) {
            throw ex;
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
    
    public static void makeChangeTaxInstallmentConfig(TaxInstallmentConfigDobj taxInstallmentConfigDobj, String changedata, List<TaxInstallmentConfigDobj> taxInstallmentConfigDobjList) throws SQLException, Exception {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("makeChangeTaxInstallmentConfig");
            insertUpdateTaxInstallmentConfig(tmgr, taxInstallmentConfigDobj, taxInstallmentConfigDobjList);
            ComparisonBeanImpl.updateChangedData(taxInstallmentConfigDobj.getAppl_no(), changedata, tmgr);
            tmgr.commit();
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
    }
    
    public static void insertUpdateTaxInstallmentConfig(TransactionManager tmgr, TaxInstallmentConfigDobj taxInstallmentConfigDobj, List<TaxInstallmentConfigDobj> taxInstallmentConfigDobjList) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;
        sql = "SELECT regn_no FROM " + TableList.VA_TAX_INSTALLMENT_BRKUP + " where appl_no = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, taxInstallmentConfigDobj.getAppl_no());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) { //if any record is exist then update otherwise insert it
            insertIntoTaxInstallmentConfigHistory(tmgr, taxInstallmentConfigDobj.getAppl_no(), taxInstallmentConfigDobjList);
            updateTaxInstallmentConfig(tmgr, taxInstallmentConfigDobj, taxInstallmentConfigDobjList);
        } else {
            insertIntoTaxInstallmentConfig(tmgr, taxInstallmentConfigDobj, taxInstallmentConfigDobjList);
        }
    }
    
    public static void insertIntoTaxInstallmentConfigHistory(TransactionManager tmgr, String appl_no, List<TaxInstallmentConfigDobj> taxInstallmentConfigDobjList) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;
////////////////////////////////////////////////
        //inserting data into VHA_TAX_INSTALLMENT from VA_TAX_INSTALLMENT
        sql = "INSERT INTO " + TableList.VHA_TAX_INSTALLMENT
                + " SELECT *,current_timestamp as moved_on, ? as moved_by "
                + "  FROM  " + TableList.VA_TAX_INSTALLMENT
                + " WHERE  appl_no=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
        /////////////////////////////////////////////

        //inserting data into VHA_TAX_INSTALLMENT_BRKUP from VA_TAX_INSTALLMENT_BRKUP
        sql = "INSERT INTO " + TableList.VHA_TAX_INSTALLMENT_BRKUP
                + " SELECT *,current_timestamp as moved_on, ? as moved_by "
                + "  FROM  " + TableList.VA_TAX_INSTALLMENT_BRKUP
                + " WHERE  appl_no=? ";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }
    
    public static void updateTaxInstallmentConfig(TransactionManager tmgr, TaxInstallmentConfigDobj taxInstallmentConfigDobj, List<TaxInstallmentConfigDobj> taxInstallmentConfigDobjList) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;
        /////////////////////////////////////////////
        //      =======================================================================================
        sql = "DELETE FROM " + TableList.VA_TAX_INSTALLMENT + " where regn_no = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, taxInstallmentConfigDobj.getRegnno());
        ps.executeUpdate();
        //      =======================================================================================                
        TaxInstallmentConfigDobj taxInstallmentConfigDobjni = taxInstallmentConfigDobjList.get(0);
        int j = 1;
        
        sql = "INSERT INTO " + TableList.VA_TAX_INSTALLMENT + "(state_cd, off_cd, appl_no, regn_no,"
                + " file_ref_no, order_iss_by, order_no, order_date, tax_from, tax_upto, tax_mode,"
                + " user_cd, op_dt)"
                + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";
        
        ps = tmgr.prepareStatement(sql);
        ps.setString(j++, Util.getUserStateCode());
        ps.setInt(j++, Util.getSelectedSeat().getOff_cd());
        ps.setString(j++, taxInstallmentConfigDobjni.getAppl_no());
        ps.setString(j++, taxInstallmentConfigDobjni.getRegnno());
        ps.setString(j++, taxInstallmentConfigDobjni.getFilerefno());
        ps.setString(j++, taxInstallmentConfigDobjni.getOrderissueby());
        ps.setString(j++, taxInstallmentConfigDobjni.getOrderno());
        ps.setDate(j++, new java.sql.Date(taxInstallmentConfigDobjni.getOrderdate().getTime()));
        ps.setDate(j++, new java.sql.Date(taxInstallmentConfigDobjni.getTaxfromdt().getTime()));
        ps.setDate(j++, new java.sql.Date(taxInstallmentConfigDobjni.getTaxuptodt().getTime()));
        ps.setString(j++, taxInstallmentConfigDobjni.getTaxmode());
        ps.setString(j++, Util.getEmpCode());
        ps.executeUpdate();
        /////////////////////////////////////////

        //      =======================================================================================
        sql = "DELETE FROM " + TableList.VA_TAX_INSTALLMENT_BRKUP + " where regn_no = ? and state_cd=? and off_cd=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, taxInstallmentConfigDobj.getRegnno());
        ps.setString(2, Util.getUserStateCode());
        ps.setInt(3, Util.getUserSeatOffCode());
        ps.executeUpdate();
        //      =======================================================================================                
        for (TaxInstallmentConfigDobj taxInstallmentConfigDobjnew : taxInstallmentConfigDobjList) {
            int i = 1;
            
            sql = "INSERT INTO " + TableList.VA_TAX_INSTALLMENT_BRKUP + "(state_cd, off_cd, appl_no, regn_no,"
                    + " serial_no,"
                    + " tax_amt_instl, pay_due_date, user_cd, op_dt)"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";
            
            ps = tmgr.prepareStatement(sql);
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.setString(i++, taxInstallmentConfigDobjnew.getAppl_no());
            ps.setString(i++, taxInstallmentConfigDobjnew.getRegnno());
            ps.setString(i++, taxInstallmentConfigDobjnew.getSerialnotable());
            ps.setLong(i++, taxInstallmentConfigDobjnew.getTaxamountinstltable());
            
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
            Date returndate = formatter.parse(taxInstallmentConfigDobjnew.getDueDateStr());
            ps.setDate(i++, new java.sql.Date(returndate.getTime()));
            
            ps.setString(i++, Util.getEmpCode());
            ps.executeUpdate();
        }
    } // end of update VA_TAX_INSTALLMENT_BRKUP

    public static void insertIntoTaxInstallmentConfig(TransactionManager tmgr, TaxInstallmentConfigDobj taxInstallmentConfigDobj, List<TaxInstallmentConfigDobj> taxInstallmentConfigDobjList) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;
        /////////////////////////////////////////////

        TaxInstallmentConfigDobj taxInstallmentConfigDobjni = taxInstallmentConfigDobjList.get(0);
        int j = 1;
        
        sql = "INSERT INTO " + TableList.VA_TAX_INSTALLMENT + "(state_cd, off_cd, appl_no, regn_no,"
                + " file_ref_no, order_iss_by, order_no, order_date, tax_from, tax_upto, tax_mode,"
                + " user_cd, op_dt)"
                + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";
        
        ps = tmgr.prepareStatement(sql);
        ps.setString(j++, Util.getUserStateCode());
        ps.setInt(j++, Util.getSelectedSeat().getOff_cd());
        ps.setString(j++, taxInstallmentConfigDobjni.getAppl_no());
        ps.setString(j++, taxInstallmentConfigDobjni.getRegnno());
        ps.setString(j++, taxInstallmentConfigDobjni.getFilerefno());
        ps.setString(j++, taxInstallmentConfigDobjni.getOrderissueby());
        ps.setString(j++, taxInstallmentConfigDobjni.getOrderno());
        ps.setDate(j++, new java.sql.Date(taxInstallmentConfigDobjni.getOrderdate().getTime()));
        ps.setDate(j++, new java.sql.Date(taxInstallmentConfigDobjni.getTaxfromdt().getTime()));
        ps.setDate(j++, new java.sql.Date(taxInstallmentConfigDobjni.getTaxuptodt().getTime()));
        ps.setString(j++, taxInstallmentConfigDobjni.getTaxmode());
        ps.setString(j++, Util.getEmpCode());
        ps.executeUpdate();
        /////////////////////////////////////////

        for (TaxInstallmentConfigDobj taxInstallmentConfigDobjnew : taxInstallmentConfigDobjList) {
            int i = 1;
            //yyyy-MM-dd
            sql = "INSERT INTO " + TableList.VA_TAX_INSTALLMENT_BRKUP + "(state_cd, off_cd, appl_no, regn_no,"
                    + " serial_no,"
                    + " tax_amt_instl, pay_due_date, user_cd, op_dt)"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";
            
            ps = tmgr.prepareStatement(sql);
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.setString(i++, taxInstallmentConfigDobjnew.getAppl_no());
            ps.setString(i++, taxInstallmentConfigDobjnew.getRegnno());
            ps.setString(i++, taxInstallmentConfigDobjnew.getSerialnotable());
            ps.setLong(i++, taxInstallmentConfigDobjnew.getTaxamountinstltable());
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
            Date returndate = formatter.parse(taxInstallmentConfigDobjnew.getDueDateStr());
            ps.setDate(i++, new java.sql.Date(returndate.getTime()));
            ps.setString(i++, Util.getEmpCode());
            ps.executeUpdate();
            
        }
    }
    
    public void update_TAXINSTALLMENTCONFIG_Status(TaxInstallmentConfigDobj taxInstallmentConfigDobj, TaxInstallmentConfigDobj taxInstallmentConfigDobj_prv, Status_dobj status_dobj, String changedData, List<TaxInstallmentConfigDobj> taxInstallmentConfigDobjList, List<TaxExemptiondobj> taxExemption) throws Exception {
        
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        
        try {
            
            tmgr = new TransactionManager("update_TAXINSTALLMENTCONFIG_Status");
            //=================WEB SERVICES FOR NEXTSTAGE START=========//
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            
            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_INSTALLMENT_BRKUP_ENTRY
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_INSTALLMENT_BRKUP_VERIFICATION
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_INSTALLMENT_BRKUP_APPROVAL) {
                if (!changedData.equals("")) {
                    if ((changedData != null && !changedData.equals("")) || taxInstallmentConfigDobj_prv == null) {
                        insertUpdateTaxInstallmentConfig(tmgr, taxInstallmentConfigDobj, taxInstallmentConfigDobjList);//when there is change by user or Entry Scrutiny
                    }
                }
                
            }
            
            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_INSTALLMENT_BRKUP_APPROVAL
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                
                sql = "INSERT INTO " + TableList.VH_TAX_INSTALLMENT
                        + " SELECT state_cd, off_cd, regn_no, file_ref_no, order_iss_by, order_no, "
                        + "  order_date, tax_from, tax_upto, tax_mode,"
                        + "  user_cd, op_dt, current_timestamp as moved_on, ? as moved_by "
                        + "  FROM " + TableList.VT_TAX_INSTALLMENT
                        + " WHERE regn_no=? and state_cd = ? and off_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, taxInstallmentConfigDobj.getRegnno());
                ps.setString(3, Util.getUserStateCode());
                ps.setInt(4, Util.getUserSeatOffCode());
                ps.executeUpdate();
                
                sql = "INSERT INTO " + TableList.VH_TAX_INSTALLMENT_BRKUP
                        + " SELECT state_cd, off_cd, regn_no, "
                        + "  serial_no, tax_amt_instl, pay_due_date,"
                        + "  rcpt_no, user_cd, op_dt, current_timestamp as moved_on, ? as moved_by "
                        + "  FROM " + TableList.VT_TAX_INSTALLMENT_BRKUP
                        + " WHERE regn_no=? and state_cd = ? and off_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, taxInstallmentConfigDobj.getRegnno());
                ps.setString(3, Util.getUserStateCode());
                ps.setInt(4, Util.getUserSeatOffCode());
                ps.executeUpdate();
                
                sql = "DELETE FROM " + TableList.VT_TAX_INSTALLMENT + " where regn_no = ? and state_cd = ? and off_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, taxInstallmentConfigDobj.getRegnno());
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getUserSeatOffCode());
                ps.executeUpdate();
                
                sql = "DELETE FROM " + TableList.VT_TAX_INSTALLMENT_BRKUP + " where regn_no = ? and state_cd = ? and off_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, taxInstallmentConfigDobj.getRegnno());
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getUserSeatOffCode());
                ps.executeUpdate();


                // for checking update or insert data in VT_TAX_INSTALLMENT
                sql = "SELECT * FROM " + TableList.VT_TAX_INSTALLMENT + " a "
                        + " left join " + TableList.VA_DETAILS + " b  on a.appl_regn_no = b.appl_no "
                        + "where b.regn_no = ? AND b.pur_cd IN (1,11) AND b.state_cd = ?";
                
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, taxInstallmentConfigDobj.getRegnno());
                ps.setString(2, Util.getUserStateCode());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) { //if any record is exist then update otherwise insert it

                    sql = "UPDATE " + TableList.VT_TAX_INSTALLMENT + " a SET"
                            + " regn_no = b.regn_no, appl_no = b.appl_no, file_ref_no = b.file_ref_no"
                            + ", order_no = b.order_no, order_date = b.order_date, tax_from = b.tax_from, tax_upto = b.tax_upto"
                            + ", tax_mode = b.tax_mode, order_iss_by = b.order_iss_by"
                            + ", user_cd = ?, op_dt = current_timestamp"
                            + " from " + TableList.VA_TAX_INSTALLMENT + " b, " + TableList.VA_DETAILS + " c "
                            + " WHERE c.regn_no = b.regn_no and c.appl_no = a.appl_regn_no "
                            + " and c.pur_cd  IN (1,11) and b.appl_no=? and b.regn_no =  ? and c.state_cd = ? and c.off_cd = ?";
                    
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getEmpCode());
                    ps.setString(2, taxInstallmentConfigDobj.getAppl_no());
                    ps.setString(3, taxInstallmentConfigDobj.getRegnno());
                    ps.setString(4, Util.getUserStateCode());
                    ps.setInt(5, Util.getUserSeatOffCode());
                    ps.executeUpdate();
                    
                    
                } else {
                    // direct insert for old vehicles Yearly to Lumsum
                    sql = "insert into " + TableList.VT_TAX_INSTALLMENT
                            + " SELECT state_cd, off_cd, regn_no, appl_no, regn_no, file_ref_no, order_iss_by, order_no,"
                            + " order_date, tax_from, tax_upto, tax_mode,"
                            + "  ? as user_cd, current_timestamp "
                            + " FROM " + TableList.VA_TAX_INSTALLMENT
                            + " WHERE appl_no=? ";
                    
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getEmpCode());
                    ps.setString(2, taxInstallmentConfigDobj.getAppl_no());
                    ps.executeUpdate();
                }
                // for checking update or insert data in VT_TAX_INSTALLMENT End

                sql = "insert into " + TableList.VT_TAX_INSTALLMENT_BRKUP
                        + " SELECT state_cd, off_cd, regn_no,"
                        + " serial_no, tax_amt_instl, pay_due_date,"
                        + " NULL, ? as user_cd, current_timestamp "
                        + " FROM " + TableList.VA_TAX_INSTALLMENT_BRKUP
                        + " WHERE appl_no=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, taxInstallmentConfigDobj.getAppl_no());
                ps.executeUpdate();
                
                
                sql = "INSERT INTO " + TableList.VHA_TAX_INSTALLMENT
                        + " SELECT *, current_timestamp + interval '1 second' as moved_on, ? as moved_by "
                        + "  FROM  " + TableList.VA_TAX_INSTALLMENT
                        + " WHERE  appl_no=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, taxInstallmentConfigDobj.getAppl_no());
                ps.executeUpdate();
                
                sql = "INSERT INTO " + TableList.VHA_TAX_INSTALLMENT_BRKUP
                        + " SELECT *, current_timestamp + interval '1 second' as moved_on, ? as moved_by "
                        + "  FROM  " + TableList.VA_TAX_INSTALLMENT_BRKUP
                        + " WHERE  appl_no=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, taxInstallmentConfigDobj.getAppl_no());
                ps.executeUpdate();
                
                
                sql = "DELETE FROM " + TableList.VA_TAX_INSTALLMENT + " where appl_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, taxInstallmentConfigDobj.getAppl_no());
                ps.executeUpdate();
                
                sql = "DELETE FROM " + TableList.VA_TAX_INSTALLMENT_BRKUP + " where appl_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, taxInstallmentConfigDobj.getAppl_no());
                ps.executeUpdate();

                //Saving Tax Installment Details
                FeeImpl feeImpl = new FeeImpl();
                if (taxExemption != null && !taxExemption.isEmpty()) {
                    Fee_Pay_Dobj fee_Pay_Dobj = new Fee_Pay_Dobj();
                    fee_Pay_Dobj.setApplNo(taxInstallmentConfigDobj.getRegnno());
                    feeImpl.saveFinePaneltyExemDetails(fee_Pay_Dobj.getApplNo(), taxInstallmentConfigDobj.getAppl_no(), tmgr, Util.getEmpCode());
                }

                //for updating the status of application when it is approved start
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                //for updating the status of application when it is approved end
            }
            
            insertIntoVhaChangedData(tmgr, taxInstallmentConfigDobj.getAppl_no(), changedData);//for saving the data into table those are changed by the user

            ServerUtil.fileFlow(tmgr, status_dobj); //for updateing va_status and vha status for new role,seat for new emp

            tmgr.commit();//Commiting data here....

        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
        
    }
    
    public Map<String, String> taxModeInfo(String regNo) throws VahanException, Exception {
        Map<String, String> taxPaid = new HashMap<>();
        try {
            taxPaid = ServerUtil.taxPaidInfo(false, regNo, 58);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        
        return taxPaid;
    }
    
    public Boolean checkalreadymadeEMI(String regn_no) {
        Boolean blnEMIfound = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("checkalreadymadeEMI");
            String qry = "SELECT * FROM " + TableList.VT_TAX_INSTALLMENT_BRKUP + " WHERE regn_no=? and state_cd=?";
            if (!Util.getUserStateCode().equals("RJ")) {
                qry += " and rcpt_no is null";
            }
            ps = tmgr.prepareStatement(qry);
            ps.setString(1, regn_no);
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                blnEMIfound = true;
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
        return blnEMIfound;
    }

    //inserting data into vha_tax_installment from va_tax_installment
    public static void insertIntoTaxInstallmentHistory(TransactionManager tmgr, String appl_no) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;
        sql = "INSERT INTO " + TableList.VHA_TAX_INSTALLMENT
                + " SELECT *,current_timestamp as moved_on, ? as moved_by "
                + "  FROM  " + TableList.VA_TAX_INSTALLMENT
                + " WHERE  appl_no=? and state_cd=? and off_cd=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.setString(3, Util.getUserStateCode());
        ps.setInt(4, Util.getUserSeatOffCode());
        ps.executeUpdate();
    }

    //inserting data into vha_tax_installment_brk from va_tax_installment_brk
    public static void insertIntoTaxInstallmentBrkHistory(TransactionManager tmgr, String appl_no) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;
        
        sql = "INSERT INTO " + TableList.VHA_TAX_INSTALLMENT_BRKUP
                + " SELECT *,current_timestamp as moved_on, ? as moved_by "
                + "  FROM  " + TableList.VA_TAX_INSTALLMENT_BRKUP
                + " WHERE  appl_no=? and state_cd=? and off_cd=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.setString(3, Util.getUserStateCode());
        ps.setInt(4, Util.getUserSeatOffCode());
        ps.executeUpdate();
    }

    // deleting from VA_TAX_INSTALLMENT_BRKUP
    public static void deleteVaTaxInsBrk(TransactionManager tmgr, String appl_no) throws SQLException, Exception {
        String sql = "DELETE FROM " + TableList.VA_TAX_INSTALLMENT_BRKUP + "  WHERE appl_no=? ";
        int i = 1;
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(i++, appl_no);
        ps.executeUpdate();
        
    }
    
    public PassengerPermitDetailDobj getPermitBaseOnTaxPermitInfo(String stateCd, int offCd, String regn_no) {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        PassengerPermitDetailDobj permitDobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("getPermitBaseOnTaxPermitInfo");
            String query = "select * from " + TableList.VT_TAX_BASED_ON_PERMIT_INFO + " WHERE  state_cd= ? and regn_no = ? and off_cd=? order by op_dt desc limit 1";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, stateCd);
            ps.setString(2, regn_no);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                permitDobj = new PassengerPermitDetailDobj();
                permitDobj.setOff_cd(String.valueOf(rs.getInt("off_cd")));
                permitDobj.setRegnNo(rs.getString("regn_no"));
                permitDobj.setPmt_type(String.valueOf(rs.getInt("pmt_type")));
                permitDobj.setPmt_type_code(String.valueOf(rs.getInt("pmt_type")));
                permitDobj.setPmtCatg(String.valueOf(rs.getInt("pmt_catg")));
                permitDobj.setDomain_CODE(String.valueOf(rs.getInt("domain_cd")));
                permitDobj.setServices_TYPE(String.valueOf(rs.getInt("service_type")));
                permitDobj.setRout_code(rs.getString("route_class"));
                permitDobj.setRout_length(rs.getString("route_length"));
                permitDobj.setNumberOfTrips(rs.getString("no_of_trips"));
                permitDobj.setOp_dt(rs.getDate("op_dt"));
                
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
        return permitDobj;
    }
}
