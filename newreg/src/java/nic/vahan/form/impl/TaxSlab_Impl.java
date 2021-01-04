/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.TaxSlabAddNewDobj;
import nic.vahan.form.dobj.TaxSlabDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author Ashok Kumar <send2ashok@hotmail.com>
 */
public class TaxSlab_Impl {

    private static Logger LOGGER = Logger.getLogger(TaxSlab_Impl.class);

    public static ArrayList<TaxSlabDobj> getTaxSlabNewDetail(int class_type, int pur_code) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String sql = null;
        ArrayList<TaxSlabDobj> taxSlab_dobjs_list = new ArrayList<TaxSlabDobj>();
        try {
            tmgr = new TransactionManager("getTaxSlabNewDetail()");
            sql = "select slab_code,descr,pur_cd,tax_mode,class_type,tax_on_vch,"
                    + " to_char(date_from,'DD-MON-YYYY')::date as date_from,to_char(date_to,'DD-MON-YYYY')::date as date_to "
                    + " from vm_tax_slab_new where state_cd=? and class_type=? and pur_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, class_type);
            ps.setInt(3, pur_code);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                TaxSlabDobj taxSlab_dobj = new TaxSlabDobj();
                taxSlab_dobj.setSlab_code(rs.getInt("slab_code"));
                taxSlab_dobj.setDescr(rs.getString("descr"));
                taxSlab_dobj.setPur_cd(rs.getInt("pur_cd"));
                taxSlab_dobj.setTax_mode(rs.getString("tax_mode"));
                taxSlab_dobj.setClass_type(rs.getInt("class_type"));
                taxSlab_dobj.setTax_on_vch(rs.getString("tax_on_vch"));
                taxSlab_dobj.setDate_from(rs.getDate("date_from"));
                taxSlab_dobj.setDate_to(rs.getDate("date_to"));
                // taxSlab_dobj.setAddl_tax_slab_list(getAddlTaxSlabsDetails(rs.getInt("slab_code")));
                taxSlab_dobjs_list.add(taxSlab_dobj);
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
        return taxSlab_dobjs_list;
    }//end of getTaxSlabNewDetail()

    public static List<TaxSlabAddNewDobj> getAddlTaxSlabsDetails(int slab_code) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String sql = null;
        ArrayList<TaxSlabAddNewDobj> addlTaxSlabList = new ArrayList<TaxSlabAddNewDobj>();
        try {
            tmgr = new TransactionManager("getTaxSlabAddNewDetail()");
            sql = "select slab_code,type_flag,add_code,condition_formula,rate_formula "
                    + "  from vm_tax_slab_add_new "
                    + " where state_cd=? and slab_code=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, slab_code);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                TaxSlabAddNewDobj taxSlabAddNew_dobj = new TaxSlabAddNewDobj();
                taxSlabAddNew_dobj.setSlab_code(rs.getInt("slab_code"));
                taxSlabAddNew_dobj.setType_flag(rs.getString("type_flag"));
                taxSlabAddNew_dobj.setAdd_code(rs.getInt("add_code"));
                taxSlabAddNew_dobj.setCondition_formula(rs.getString("condition_formula"));
                taxSlabAddNew_dobj.setRate_formula(rs.getString("rate_formula"));
                taxSlabAddNew_dobj.setIfCondFormula(FormulaUtils.makeIfCondition(rs.getString("type_flag"), rs.getString("condition_formula"), rs.getString("rate_formula")));
                addlTaxSlabList.add(taxSlabAddNew_dobj);
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
        return addlTaxSlabList;
    }//end of getAddlTaxSlabsDetails()

    public static ArrayList<TaxSlabAddNewDobj> getTaxSlabAddNewDetail(int slab_code) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String sql = null;
        ArrayList<TaxSlabAddNewDobj> taxSlabAddNew_dobjs_list = new ArrayList<TaxSlabAddNewDobj>();
        try {
            tmgr = new TransactionManager("getTaxSlabAddNewDetail()");
            sql = "select slab_code,type_flag,add_code,condition_formula,rate_formula "
                    + "  from vm_tax_slab_add_new "
                    + " where state_cd=? and slab_code=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, slab_code);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                TaxSlabAddNewDobj taxSlabAddNew_dobj = new TaxSlabAddNewDobj();
                taxSlabAddNew_dobj.setSlab_code(rs.getInt("slab_code"));
                taxSlabAddNew_dobj.setType_flag(rs.getString("type_flag"));
                taxSlabAddNew_dobj.setAdd_code(rs.getInt("add_code"));
                taxSlabAddNew_dobj.setCondition_formula(rs.getString("condition_formula"));
                taxSlabAddNew_dobj.setRate_formula(rs.getString("rate_formula"));
                taxSlabAddNew_dobj.setIfCondFormula(FormulaUtils.makeIfCondition(rs.getString("type_flag"), rs.getString("condition_formula"), rs.getString("rate_formula")));
                taxSlabAddNew_dobjs_list.add(taxSlabAddNew_dobj);
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
        return taxSlabAddNew_dobjs_list;
    }//end of getTaxSlabAddNewDetail()

    public static LinkedHashMap<String, Object> getPurposeList() {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        LinkedHashMap<String, Object> purposeLabelValue = new LinkedHashMap<String, Object>();

        try {
            tmgr = new TransactionManager("getPurposeList()");

            sql = "select pur_cd,descr from tm_purpose_mast where amt_from_wsdl order by descr";
            ps = tmgr.prepareStatement(sql);
            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next())//found
            {
                purposeLabelValue.put(rs.getString("descr"), rs.getInt("pur_cd")); //label,value
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

        return purposeLabelValue;
    }//end of getPurposeList()

    public static void insertUpdateTaxSlabNewTable(TaxSlabDobj dobj) throws VahanException, Exception {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        int next_slab_code = 0;

        try {
            tmgr = new TransactionManager("insertIntoTaxSlabNewTable");

            if (dobj.isNewSlab()) {
                sql = "select max(slab_code)+1 as slab_code from vm_tax_slab_new where state_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                RowSet rs = tmgr.fetchDetachedRowSet();

                if (rs.next()) {
                    next_slab_code = rs.getInt("slab_code");
                    if (next_slab_code == 0) {
                        next_slab_code = 1;
                    }
                }

                if (next_slab_code > 0) {
                    sql = "INSERT INTO vm_tax_slab_new(state_cd, slab_code, descr, pur_cd, tax_mode, class_type, tax_on_vch,"
                            + "date_from, date_to, emp_cd, op_dt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, dobj.getState_cd());
                    ps.setInt(2, next_slab_code);
                    ps.setString(3, dobj.getDescr());
                    ps.setInt(4, dobj.getPur_cd());
                    ps.setString(5, dobj.getTax_mode());
                    ps.setInt(6, dobj.getClass_type());
                    ps.setString(7, dobj.getTax_on_vch());
                    ps.setDate(8, new java.sql.Date(dobj.getDate_from().getTime()));
                    ps.setDate(9, new java.sql.Date(dobj.getDate_to().getTime()));
                    ps.setString(10, dobj.getEmp_cd());
                    ps.executeUpdate();
                }
            } else {

                sql = "INSERT INTO " + TableList.VHM_TAX_SLAB_NEW
                        + " SELECT *,current_timestamp as moved_on,? as moved_by  FROM " + TableList.VM_TAX_SLAB_NEW
                        + " WHERE state_cd = ? and slab_code=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getEmp_cd());
                ps.setString(2, dobj.getState_cd());
                ps.setInt(3, dobj.getSlab_code());
                ps.executeUpdate();

                sql = "UPDATE vm_tax_slab_new set "
                        + " descr = ?,"
                        + " pur_cd = ?,"
                        + " tax_mode = ?,"
                        + " class_type = ?,"
                        + " tax_on_vch = ?,"
                        + " date_from = ?,"
                        + " date_to = ?,"
                        + " emp_cd = ?,"
                        + " op_dt = current_timestamp"
                        + " where state_cd = ? and slab_code=?";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getDescr());
                ps.setInt(2, dobj.getPur_cd());
                ps.setString(3, dobj.getTax_mode());
                ps.setInt(4, dobj.getClass_type());
                ps.setString(5, dobj.getTax_on_vch());
                ps.setDate(6, new java.sql.Date(dobj.getDate_from().getTime()));
                ps.setDate(7, new java.sql.Date(dobj.getDate_to().getTime()));
                ps.setString(8, dobj.getEmp_cd());
                ps.setString(9, dobj.getState_cd());
                ps.setInt(10, dobj.getSlab_code());
                ps.executeUpdate();
            }
            tmgr.commit();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
    }

    public static void insertUpdateTaxSlabAddNewTable(TaxSlabAddNewDobj dobj) throws VahanException, Exception {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        int next_add_code = 0;
        try {
            tmgr = new TransactionManager("insertUpdateTaxSlabAddNewTable");

            if (dobj.isNewAddSlab()) {
                sql = "select max(add_code) + 1 as add_code from vm_tax_slab_add_new where state_cd = ? and slab_code = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, dobj.getSlab_code());
                RowSet rs = tmgr.fetchDetachedRowSet();

                if (rs.next()) {
                    next_add_code = rs.getInt("add_code");

                    if (next_add_code == 0) {
                        next_add_code = 1;
                    }
                }

                //  if (next_add_code > 0) {
                sql = "INSERT INTO vm_tax_slab_add_new(state_cd, slab_code, add_code, "
                        + "condition_formula, type_flag,rate_formula, emp_cd, op_dt)"
                        + "    VALUES (?, ?, ?, ?, ?,?, ?, current_timestamp)";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getState_cd());
                ps.setInt(2, dobj.getSlab_code());
                ps.setInt(3, next_add_code);
                ps.setString(4, dobj.getCondition_formula());
                ps.setString(5, dobj.getType_flag());
                ps.setString(6, dobj.getRate_formula());
                ps.setString(7, dobj.getEmp_cd());
                ps.executeUpdate();
                //  }
            } else {

                sql = "INSERT INTO " + TableList.VHM_TAX_SLAB_ADD_NEW
                        + " SELECT *,current_timestamp as moved_on,? as moved_by"
                        + " FROM " + TableList.VM_TAX_SLAB_ADD_NEW
                        + " WHERE state_cd = ? and slab_code=? and add_code=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getEmp_cd());
                ps.setString(2, dobj.getState_cd());
                ps.setInt(3, dobj.getSlab_code());
                ps.setInt(4, dobj.getAdd_code());
                ps.executeUpdate();

                sql = "UPDATE vm_tax_slab_add_new SET "
                        + " type_flag = ?, "
                        + " condition_formula = ?, "
                        + " rate_formula = ?, "
                        + " emp_cd = ?, "
                        + " op_dt = current_timestamp"
                        + " where state_cd = ? and slab_code = ? and add_code = ?";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getType_flag());
                ps.setString(2, dobj.getCondition_formula());
                ps.setString(3, dobj.getRate_formula());
                ps.setString(4, dobj.getEmp_cd());
                ps.setString(5, dobj.getState_cd());
                ps.setInt(6, dobj.getSlab_code());
                ps.setInt(7, dobj.getAdd_code());
                ps.executeUpdate();
            }
            tmgr.commit();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Updation of Slabs ");
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
}
