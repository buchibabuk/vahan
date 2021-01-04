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
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.TaxInstallmentConfigureManualDobj;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author acer
 */
public class TaxInstallmentConfigureManualImpl {

    private static final Logger LOGGER = Logger.getLogger(TaxInstallmentConfigureManualImpl.class);

    public boolean checkalreadymadeEMI(String regn_no, String stateCd, boolean flag) throws VahanException {
        boolean blnEMIfound = false;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        ArrayList list_Pay = new ArrayList();
        try {
            tmgr = new TransactionManager("checkalreadymadeEMI");
            String qry = "SELECT * FROM " + TableList.VT_TAX_INSTALLMENT_BRKUP + " WHERE regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(qry);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) // found
            {
                list_Pay.add(rs.getString("rcpt_no"));
                blnEMIfound = true;
            }
            if (flag && stateCd.equals("OR")) {
                if (!list_Pay.isEmpty()) {
                    if (list_Pay.contains(null) || list_Pay.contains("")) {
                        blnEMIfound = false;
                    } else {
                        blnEMIfound = true;
                    }
                }
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
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

    public String InsertIntoVTTaxInstallment(List<TaxInstallmentConfigureManualDobj> taxInstallmentConfigureManualDobjList, String state_Cd, int off_cd, String emp_Code) throws Exception {
        String appl_no = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;

        try {
            tmgr = new TransactionManager("insertIntoVTTaxInstallment");

            appl_no = ServerUtil.getUniqueApplNo(tmgr, state_Cd);//generate a new application no.
            if (taxInstallmentConfigureManualDobjList != null && !taxInstallmentConfigureManualDobjList.isEmpty()) {
                TaxInstallmentConfigureManualDobj taxInstallmentConfigureManualDobjni = taxInstallmentConfigureManualDobjList.get(0);
                int j = 1;
                sql = "INSERT INTO " + TableList.VT_TAX_INSTALLMENT + "(state_cd, off_cd, regn_no, appl_no, appl_regn_no, file_ref_no,"
                        + " order_iss_by, order_no, order_date, tax_from, tax_upto, tax_mode,"
                        + " user_cd, op_dt)"
                        + " VALUES (?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?,?, current_timestamp)";

                ps = tmgr.prepareStatement(sql);
                ps.setString(j++, Util.getUserStateCode());//function parameter
                ps.setInt(j++, Util.getUserOffCode());//function parameter
                ps.setString(j++, taxInstallmentConfigureManualDobjni.getRegnno());
                ps.setString(j++, appl_no);
                ps.setString(j++, taxInstallmentConfigureManualDobjni.getRegnno());
                ps.setString(j++, taxInstallmentConfigureManualDobjni.getFilerefno());
                ps.setString(j++, taxInstallmentConfigureManualDobjni.getOrderissueby());
                ps.setString(j++, taxInstallmentConfigureManualDobjni.getOrderno());
                ps.setDate(j++, new java.sql.Date(taxInstallmentConfigureManualDobjni.getOrderdate().getTime()));
                ps.setDate(j++, new java.sql.Date(taxInstallmentConfigureManualDobjni.getTaxfromdt().getTime()));
                ps.setDate(j++, new java.sql.Date(taxInstallmentConfigureManualDobjni.getTaxuptodt().getTime()));
                ps.setString(j++, "S");
                ps.setString(j++, emp_Code);
                ps.executeUpdate();

                /////////////////////////////////////////////

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
                Date returndate = null;
                for (TaxInstallmentConfigureManualDobj taxInstallmentConfigureManualDobjn : taxInstallmentConfigureManualDobjList) {
                    int i = 1;
                    sql = "INSERT INTO " + TableList.VT_TAX_INSTALLMENT_BRKUP + " (state_cd, off_cd, regn_no, serial_no, tax_amt_instl, pay_due_date,"
                            + " user_cd, op_dt)"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, current_timestamp)";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(i++, state_Cd);
                    ps.setInt(i++, off_cd);
                    ps.setString(i++, taxInstallmentConfigureManualDobjn.getRegnno());
                    ps.setString(i++, taxInstallmentConfigureManualDobjn.getSerialnotable());
                    ps.setLong(i++, taxInstallmentConfigureManualDobjn.getTaxamountinstltable());
                    returndate = formatter.parse(taxInstallmentConfigureManualDobjn.getDueDateStr());
                    ps.setDate(i++, new java.sql.Date(returndate.getTime()));
                    ps.setString(i++, emp_Code);
                    ps.executeUpdate();
                }

                tmgr.commit();//Commiting data here....
            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
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

    public boolean getBackupForReconfigureInstallment(String regn_No, String state_Code, String emp_Code, int off_Cd) throws Exception {
        TransactionManager tmgr = null;
        Exception e = null;
        PreparedStatement prstmt = null;
        boolean flag = false;
        try {
            tmgr = new TransactionManager("getBackupForReconfigureInstallment");
            String sql = "INSERT INTO " + TableList.VH_TAX_INSTALLMENT_BRKUP + "("
                    + " state_cd, off_cd,regn_no, serial_no, tax_amt_instl, pay_due_date, rcpt_no, user_cd, op_dt, "
                    + " moved_on,moved_by)"
                    + " SELECT state_cd,off_cd,regn_no, serial_no, tax_amt_instl, pay_due_date, rcpt_no, user_cd, op_dt,"
                    + " current_timestamp,?"
                    + " FROM " + TableList.VT_TAX_INSTALLMENT_BRKUP + " WHERE REGN_NO = ? and state_cd = ? and off_cd = ?";
            prstmt = tmgr.prepareStatement(sql);
            prstmt.setString(1, emp_Code);
            prstmt.setString(2, regn_No);
            prstmt.setString(3, state_Code);
            prstmt.setInt(4, off_Cd);
            prstmt.executeUpdate();

            sql = "delete from " + TableList.VT_TAX_INSTALLMENT_BRKUP + " WHERE REGN_NO = ? and state_cd = ? and off_cd = ?";
            prstmt = tmgr.prepareStatement(sql);
            prstmt.setString(1, regn_No);
            prstmt.setString(2, state_Code);
            prstmt.setInt(3, off_Cd);
            prstmt.executeUpdate();

            sql = "INSERT INTO " + TableList.VH_TAX_INSTALLMENT
                    + " SELECT state_cd, off_cd, regn_no, file_ref_no, order_iss_by, order_no, "
                    + "  order_date, tax_from, tax_upto, tax_mode,"
                    + "  user_cd, op_dt, current_timestamp as moved_on, ? as moved_by "
                    + "  FROM " + TableList.VT_TAX_INSTALLMENT
                    + " WHERE regn_no=? and state_cd = ? and off_cd = ?";
            prstmt = tmgr.prepareStatement(sql);
            prstmt.setString(1, emp_Code);
            prstmt.setString(2, regn_No);
            prstmt.setString(3, state_Code);
            prstmt.setInt(4, off_Cd);
            prstmt.executeUpdate();

            sql = "delete from " + TableList.VT_TAX_INSTALLMENT + " WHERE regn_no = ? and state_cd = ? and off_cd = ?";
            prstmt = tmgr.prepareStatement(sql);
            prstmt.setString(1, regn_No);
            prstmt.setString(2, state_Code);
            prstmt.setInt(3, off_Cd);
            prstmt.executeUpdate();
            tmgr.commit();
            flag = true;
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        } catch (Exception ex) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(Util.getLocaleSomthingMsg());
        } finally {
            if (prstmt != null) {
                prstmt.close();
            }
            if (tmgr != null) {
                tmgr.release();
            }
        }

        return flag;
    }
}
