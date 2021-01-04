/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.NonUseDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.TmConfigurationNonUseDobj;
import nic.vahan.form.dobj.reports.CashRecieptSubListDobj;
import nic.vahan.form.dobj.tax.TaxDobj;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

/**
 *
 * @author nicsi
 */
public class NonUseImpl {

    private static final Logger LOGGER = Logger.getLogger(NonUseImpl.class);

    public void insertNonUseDetails(TransactionManager tmgr, NonUseDobj dobj, Status_dobj statusDobj, TmConfigurationNonUseDobj configDobj) throws VahanException {
        String sql = "";
        PreparedStatement pstm = null;
        int i = 1;
        try {

            if (statusDobj.getPur_cd() == TableConstants.VM_MAST_NON_USE) {

                sql = "INSERT INTO " + TableList.VA_NON_USE_TAX_EXEM + "(\n"
                        + "            regn_no, exem_fr, exem_to, exem_by, perm_no, perm_dt,remark, \n"
                        + "            deal_cd, op_dt, appl_no, place, nonuse_adjust_amt, state_cd, off_cd,garage_add1 , garage_add2 , garage_add3, garage_district, garage_state , garage_pincode )\n"
                        + "    VALUES (?, ?, ?, ?, ?, ?, ?,?, \n"
                        + "            current_timestamp,?,?, ?, ?,?,?,?,?,?,?,?)";
                pstm = tmgr.prepareStatement(sql);
                pstm.setString(i++, statusDobj.getRegn_no());
                pstm.setDate(i++, new java.sql.Date(dobj.getExemp_from().getTime()));
                pstm.setDate(i++, new java.sql.Date(dobj.getExemp_to().getTime()));
                pstm.setString(i++, dobj.getAuthorised_by());
                pstm.setString(i++, dobj.getPermission_no());
                pstm.setDate(i++, new java.sql.Date(dobj.getPermission_dt().getTime()));
                pstm.setString(i++, dobj.getNon_use_purpose());
                pstm.setString(i++, Util.getEmpCode());
                pstm.setString(i++, statusDobj.getAppl_no());
                pstm.setString(i++, dobj.getLocation_of_garage());
                pstm.setInt(i++, 0);
                pstm.setString(i++, Util.getUserStateCode());
                pstm.setInt(i++, Util.getSelectedSeat().getOff_cd());
                pstm.setString(i++, dobj.getGarage_add1());
                pstm.setString(i++, dobj.getGarage_add2());
                pstm.setString(i++, dobj.getGarage_add3());
                pstm.setInt(i++, dobj.getGarage_district());
                pstm.setString(i++, dobj.getGarage_state());
                pstm.setInt(i++, dobj.getGarage_pincode());

                pstm.executeUpdate();
            } else if (statusDobj.getPur_cd() == TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE) {
                i = 1;
                if (dobj.getValueOfRadioEvent().equalsIgnoreCase("R") || (configDobj != null && configDobj.isVehicle_inspect_for_removalshift() && dobj.getValueOfRadioEvent().equalsIgnoreCase("C"))) {
                    sql = "INSERT INTO " + TableList.VA_NON_USE_RESTORE_REMOVE + "(\n"
                            + "            state_cd, off_cd, appl_no, regn_no, exem_fr, exem_to, exem_by, \n"
                            + "            perm_no, perm_dt, remark, deal_cd,"
                            + "  place, cr_no, insp_off, insp_date, insp_flag, doc_flag, \n"
                            + "            nonuse_adjust_amt, penalty, op_dt,garage_add1 , garage_add2 , "
                            + " garage_add3, garage_district, garage_state , garage_pincode,rr_flag,vehicle_use_frm)\n"
                            + "    VALUES (?, ?, ?, \n"
                            + "            ?, ?, ?, ?, ?, ?, ?, \n"
                            + "            ?, ?, ?, ?, ?, ?, ?, \n"
                            + "            ?, ?, current_timestamp,?,?,?,?,?,?,?,?)";
                    pstm = tmgr.prepareStatement(sql);
                    pstm.setString(i++, Util.getUserStateCode());
                    pstm.setInt(i++, Util.getSelectedSeat().getOff_cd());
                    pstm.setString(i++, statusDobj.getAppl_no());
                    pstm.setString(i++, statusDobj.getRegn_no());
                    pstm.setDate(i++, new java.sql.Date(dobj.getExemp_from().getTime()));
                    pstm.setDate(i++, new java.sql.Date(dobj.getExemp_to().getTime()));
                    pstm.setString(i++, dobj.getAuthorised_by());
                    pstm.setString(i++, dobj.getPermission_no());
                    pstm.setDate(i++, new java.sql.Date(dobj.getPermission_dt().getTime()));
                    pstm.setString(i++, dobj.getNon_use_purpose());
                    pstm.setString(i++, Util.getEmpCode());
                    pstm.setString(i++, dobj.getLocation_of_garage());
                    if (!"".equalsIgnoreCase(dobj.getInspectionReportNo()) && dobj.getInspectionReportNo() != null) {
                        pstm.setString(i++, dobj.getInspectionReportNo());
                    } else {
                        pstm.setString(i++, null);
                    }
                    if (!"".equalsIgnoreCase(dobj.getInspectedBy()) && dobj.getInspectedBy() != null) {
                        pstm.setInt(i++, Integer.parseInt(dobj.getInspectedBy()));
                    } else {
                        pstm.setInt(i++, 0);
                    }
                    if (dobj.getInspectionDate() != null) {
                        pstm.setDate(i++, new java.sql.Date(dobj.getInspectionDate().getTime()));
                    } else {
                        pstm.setDate(i++, null);
                    }
                    if (!"".equalsIgnoreCase(dobj.getInsFlag()) && dobj.getInsFlag() != null) {
                        pstm.setString(i++, dobj.getInsFlag());
                    } else {
                        pstm.setString(i++, null);
                    }
                    pstm.setString(i++, dobj.getDoc_flag());
                    pstm.setLong(i++, dobj.getAdjustmentAmount());
                    pstm.setLong(i++, dobj.getNonUsePenalty());
                    pstm.setString(i++, dobj.getGarage_add1());
                    pstm.setString(i++, dobj.getGarage_add2());
                    pstm.setString(i++, dobj.getGarage_add3());
                    pstm.setInt(i++, dobj.getGarage_district());
                    pstm.setString(i++, dobj.getGarage_state());
                    pstm.setInt(i++, dobj.getGarage_pincode());
                    pstm.setString(i++, dobj.getValueOfRadioEvent());
                    if (configDobj != null && configDobj.isDeclare_withdrawl_date()) {
                        pstm.setDate(i++, new java.sql.Date(dobj.getVehicle_use_frm().getTime()));
                    } else {
                        pstm.setDate(i++, null);
                    }

                    pstm.executeUpdate();
                } else if (dobj.getValueOfRadioEvent().equalsIgnoreCase("C")) {
                    i = 1;
                    sql = "INSERT INTO " + TableList.VA_NON_USE_RESTORE_REMOVE + "(\n"
                            + "            state_cd, off_cd, appl_no, regn_no, exem_fr, exem_to, exem_by, \n"
                            + "            perm_no, perm_dt, remark, deal_cd,"
                            + "  place, towed_veh_no,cunt_reson, op_dt,garage_add1 , garage_add2 ,"
                            + " garage_add3, garage_district, garage_state , garage_pincode,rr_flag,vehicle_use_frm)\n"
                            + "    VALUES (?, ?, ?, \n"
                            + "            ?, ?, ?, ?, ?, ?, ?, \n"
                            + "            ?, ?, ?,?, current_timestamp,?,?,?,?,?,?,?,?)";


                    pstm = tmgr.prepareStatement(sql);
                    pstm.setString(i++, Util.getUserStateCode());
                    if (!"".equalsIgnoreCase(dobj.getRegisAuthority()) && dobj.getRegisAuthority() != null) {
                        pstm.setInt(i++, Integer.parseInt(dobj.getRegisAuthority()));
                    } else {
                        pstm.setInt(i++, 0);
                    }

                    pstm.setString(i++, statusDobj.getAppl_no());
                    pstm.setString(i++, statusDobj.getRegn_no());
                    pstm.setDate(i++, new java.sql.Date(dobj.getExemp_from().getTime()));
                    pstm.setDate(i++, new java.sql.Date(dobj.getExemp_to().getTime()));
                    pstm.setString(i++, dobj.getAuthorised_by());
                    pstm.setString(i++, dobj.getPermission_no());
                    pstm.setDate(i++, new java.sql.Date(dobj.getPermission_dt().getTime()));
                    pstm.setString(i++, dobj.getNon_use_purpose());
                    pstm.setString(i++, Util.getEmpCode());
                    pstm.setString(i++, dobj.getNewGarageLocation());
                    pstm.setString(i++, dobj.getTowedVehicleNo());
                    pstm.setString(i++, dobj.getRemarks());
                    pstm.setString(i++, dobj.getGarage_add1());
                    pstm.setString(i++, dobj.getGarage_add2());
                    pstm.setString(i++, dobj.getGarage_add3());
                    pstm.setInt(i++, dobj.getGarage_district());
                    pstm.setString(i++, dobj.getGarage_state());
                    pstm.setInt(i++, dobj.getGarage_pincode());
                    pstm.setString(i++, dobj.getValueOfRadioEvent());
                    if (configDobj != null && configDobj.isDeclare_withdrawl_date()) {
                        pstm.setDate(i++, new java.sql.Date(dobj.getVehicle_use_frm().getTime()));
                    } else {
                        pstm.setDate(i++, null);
                    }
                    pstm.executeUpdate();
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error : Problem In Saving Data");
        }

    }

    public void updateNonUseDetails(TransactionManager tmgr, NonUseDobj dobj, Status_dobj statusDobj, TmConfigurationNonUseDobj configDobj) throws VahanException {
        String sql = "";
        PreparedStatement pstm = null;
        int i = 1;
        try {
            if (statusDobj.getPur_cd() == TableConstants.VM_MAST_NON_USE) {


                sql = "UPDATE " + TableList.VA_NON_USE_TAX_EXEM + " SET  exem_fr=?, exem_to=?, exem_by=?, perm_no=?, perm_dt=?,remark=?,  op_dt=current_timestamp,place=?, garage_add1=? , garage_add2 =?, garage_add3=?, garage_district=?, garage_state=? , garage_pincode=?  "
                        + " WHERE state_cd=? and off_cd=? and regn_no=? and appl_no=?";
                pstm = tmgr.prepareStatement(sql);
                pstm.setDate(i++, new java.sql.Date(dobj.getExemp_from().getTime()));
                pstm.setDate(i++, new java.sql.Date(dobj.getExemp_to().getTime()));
                pstm.setString(i++, dobj.getAuthorised_by());
                pstm.setString(i++, dobj.getPermission_no());
                pstm.setDate(i++, new java.sql.Date(dobj.getPermission_dt().getTime()));
                pstm.setString(i++, dobj.getNon_use_purpose());
                pstm.setString(i++, dobj.getLocation_of_garage());
                pstm.setString(i++, dobj.getGarage_add1());
                pstm.setString(i++, dobj.getGarage_add2());
                pstm.setString(i++, dobj.getGarage_add3());
                pstm.setInt(i++, dobj.getGarage_district());
                pstm.setString(i++, dobj.getGarage_state());
                pstm.setInt(i++, dobj.getGarage_pincode());
                pstm.setString(i++, Util.getUserStateCode());
                pstm.setInt(i++, Util.getSelectedSeat().getOff_cd());
                pstm.setString(i++, statusDobj.getRegn_no());
                pstm.setString(i++, statusDobj.getAppl_no());
                pstm.executeUpdate();
                if (!"".equalsIgnoreCase(dobj.getInspectedBy()) && (statusDobj.getAction_cd() == TableConstants.VM_ROLE_NON_USE_INTIMATION_VERIFY || statusDobj.getAction_cd() == TableConstants.VM_ROLE_NON_USE_INTIMATION_APPROVE)) {
                    i = 1;
                    sql = "UPDATE va_non_use_tax_exem\n"
                            + "   SET cr_no=?, insp_off=?, \n"
                            + "       insp_date=?, insp_flag=?, doc_flag=?, op_dt=current_timestamp\n"
                            + " WHERE state_cd=? and off_cd=? and regn_no=? and appl_no=?";
                    pstm = tmgr.prepareStatement(sql);
                    pstm.setString(i++, dobj.getInspectionReportNo());
                    pstm.setInt(i++, Integer.parseInt(dobj.getInspectedBy()));
                    pstm.setDate(i++, new java.sql.Date(dobj.getInspectionDate().getTime()));
                    pstm.setString(i++, dobj.getInsFlag());
                    pstm.setString(i++, dobj.getDoc_flag());
                    pstm.setString(i++, Util.getUserStateCode());
                    pstm.setInt(i++, Util.getSelectedSeat().getOff_cd());
                    pstm.setString(i++, statusDobj.getRegn_no());
                    pstm.setString(i++, statusDobj.getAppl_no());
                    pstm.executeUpdate();

                }

                if (configDobj != null && configDobj.isNonuse_adjust_in_tax_amt() && statusDobj.getAction_cd() == TableConstants.VM_ROLE_NON_USE_INTIMATION_APPROVE) {
                    i = 1;
                    sql = "UPDATE " + TableList.VA_NON_USE_TAX_EXEM + " SET  cl_perm_dt=?,clear_by=?, "
                            + " clear_dt=?, nonuse_adjust_amt=?,penalty=?, op_dt=current_timestamp "
                            + " WHERE state_cd=? and off_cd=? and regn_no=? and appl_no=?";

                    pstm = tmgr.prepareStatement(sql);
                    pstm.setDate(i++, new java.sql.Date(dobj.getCertiPermissionDt().getTime()));
                    pstm.setString(i++, dobj.getCertifiedBy());
                    pstm.setDate(i++, new java.sql.Date(dobj.getCertificationDt().getTime()));
                    pstm.setLong(i++, dobj.getAdjustmentAmount());
                    pstm.setLong(i++, dobj.getNonUsePenalty());
                    pstm.setString(i++, Util.getUserStateCode());
                    pstm.setInt(i++, Util.getSelectedSeat().getOff_cd());
                    pstm.setString(i++, statusDobj.getRegn_no());
                    pstm.setString(i++, statusDobj.getAppl_no());
                    pstm.executeUpdate();
                }

            } else if (statusDobj.getPur_cd() == TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE) {
                if (dobj.getValueOfRadioEvent().equalsIgnoreCase("R")) {
                    updateRestoreDetails(dobj, tmgr, statusDobj, configDobj);
                } else if (dobj.getValueOfRadioEvent().equalsIgnoreCase("C") || dobj.getValueOfRadioEvent().equalsIgnoreCase("NC")) {
                    updateRemovalDetails(dobj, tmgr, statusDobj, configDobj);
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error : Not Able To Update Data");
        }

    }

    public void moveInHistoryNonUseDetails(TransactionManager tmgr, NonUseDobj dobj, Status_dobj statusDobj) throws VahanException {
        String sql = "";
        PreparedStatement pstm = null;
        int i = 1;
        try {

            if (statusDobj.getPur_cd() == TableConstants.VM_MAST_NON_USE) {
                sql = "INSERT INTO " + TableList.VHA_NON_USE_TAX_EXEM + "(\n"
                        + "            moved_on, moved_by, state_cd, off_cd, appl_no, regn_no, exem_fr, \n"
                        + "            exem_to, exem_by, perm_no, perm_dt, remark, deal_cd, cl_perm_dt, \n"
                        + "            clear_by, clear_dt, c_deal_cd, place, cr_no, insp_off, insp_date, \n"
                        + "            insp_flag, doc_flag, nonuse_adjust_amt, penalty, op_dt ,garage_add1 , garage_add2 , garage_add3, garage_district, garage_state , garage_pincode  )\n"
                        + "  SELECT current_timestamp + interval '1 second' as moved_on, ? as moved_by,state_cd, off_cd, appl_no, regn_no, exem_fr, exem_to, exem_by, \n"
                        + "       perm_no, perm_dt, remark, deal_cd, cl_perm_dt, clear_by, clear_dt, \n"
                        + "       c_deal_cd, place, cr_no, insp_off, insp_date, insp_flag, doc_flag, \n"
                        + "       nonuse_adjust_amt,penalty, op_dt ,garage_add1 , garage_add2 , garage_add3, garage_district, garage_state , garage_pincode  \n"
                        + "  FROM " + TableList.VA_NON_USE_TAX_EXEM + ""
                        + " WHERE state_cd=? and off_cd=? and regn_no=? and appl_no=?";

                pstm = tmgr.prepareStatement(sql);
                pstm.setString(i++, Util.getEmpCode());
                pstm.setString(i++, Util.getUserStateCode());
                pstm.setInt(i++, Util.getSelectedSeat().getOff_cd());
                pstm.setString(i++, statusDobj.getRegn_no());
                pstm.setString(i++, statusDobj.getAppl_no());
                pstm.executeUpdate();
            } else if (statusDobj.getPur_cd() == TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE) {
                sql = "INSERT INTO " + TableList.VHA_NON_USE_RESTORE_REMOVE + "(\n"
                        + "            moved_on, moved_by, state_cd, off_cd, appl_no, regn_no, exem_fr, \n"
                        + "            exem_to, exem_by, perm_no, perm_dt, remark, deal_cd, cl_perm_dt, \n"
                        + "            clear_by, clear_dt, c_deal_cd, place, cr_no, insp_off, insp_date, \n"
                        + "            insp_flag, doc_flag, nonuse_adjust_amt, towed_veh_no, penalty, op_dt,garage_add1 , garage_add2 , garage_add3, garage_district, garage_state , garage_pincode,rr_flag,vehicle_use_frm )\n"
                        + "  SELECT current_timestamp + interval '1 second' as moved_on, ? as moved_by,state_cd, off_cd, appl_no, regn_no, exem_fr, exem_to, exem_by, \n"
                        + "       perm_no, perm_dt, remark, deal_cd, cl_perm_dt, clear_by, clear_dt, \n"
                        + "       c_deal_cd, place, cr_no, insp_off, insp_date, insp_flag, doc_flag, \n"
                        + "       nonuse_adjust_amt,towed_veh_no, penalty , op_dt,garage_add1 , garage_add2 , garage_add3, garage_district, garage_state , garage_pincode ,rr_flag,vehicle_use_frm\n"
                        + "  FROM " + TableList.VA_NON_USE_RESTORE_REMOVE + ""
                        + " WHERE state_cd=?  and regn_no=? and appl_no=?";

                pstm = tmgr.prepareStatement(sql);
                pstm.setString(i++, Util.getEmpCode());
                pstm.setString(i++, Util.getUserStateCode());
                pstm.setString(i++, statusDobj.getRegn_no());
                pstm.setString(i++, statusDobj.getAppl_no());
                pstm.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }

    }

    public NonUseDobj getNonUseDetails(String appl_no, String regn_no, String actionCode, int pur_cd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstm = null;
        NonUseDobj dobj = null;
        RowSet rs = null;
        String sql = "";
        String tableName = "";
        String sqlfield = "";
        try {
            tmgr = new TransactionManager("getNonUseDetails");
            boolean isRestoreRemoveExist = checkExist(tmgr, appl_no, regn_no, pur_cd);

            if (pur_cd == TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE && !isRestoreRemoveExist) {
                tableName = TableList.VT_NON_USE_TAX_EXEM;
            } else if (pur_cd == TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE && isRestoreRemoveExist) {
                tableName = TableList.VA_NON_USE_RESTORE_REMOVE;
                sqlfield = ",towed_veh_no,cunt_reson,rr_flag,vehicle_use_frm";
            } else {
                tableName = TableList.VA_NON_USE_TAX_EXEM;
            }

            sql = "SELECT state_cd, off_cd, appl_no, regn_no, exem_fr, exem_to, exem_by, \n"
                    + " perm_no, perm_dt, remark, deal_cd, cl_perm_dt, clear_by, clear_dt, \n"
                    + " c_deal_cd, place, cr_no, insp_off, insp_date, insp_flag, doc_flag, \n"
                    + " nonuse_adjust_amt, op_dt " + sqlfield + ", \n"
                    + " garage_add1 , garage_add2 , garage_add3, garage_district, garage_state , garage_pincode \n"
                    + " FROM " + tableName + "  where  regn_no=? and state_cd=?";

            pstm = tmgr.prepareStatement(sql);
            pstm.setString(1, regn_no);
            pstm.setString(2, Util.getUserStateCode());
            //pstm.setInt(3, Util.getSelectedSeat().getOff_cd());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new NonUseDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setLocation_of_garage(rs.getString("place"));
                dobj.setExemp_from(rs.getDate("exem_fr"));
                dobj.setExemp_to(rs.getDate("exem_to"));
                dobj.setPermission_dt(rs.getDate("perm_dt"));
                dobj.setPermission_no(rs.getString("perm_no"));
                dobj.setAuthorised_by(rs.getString("exem_by"));
                dobj.setNon_use_purpose(rs.getString("remark"));
                dobj.setInspectionReportNo(rs.getString("cr_no"));
                dobj.setInspectedBy(rs.getString("insp_off"));
                dobj.setInspectionDate(rs.getDate("insp_date"));
                dobj.setInsFlag(rs.getString("insp_flag"));
                dobj.setDoc_flag(rs.getString("doc_flag"));
                dobj.setAdjustmentAmount(rs.getInt("nonuse_adjust_amt"));
                dobj.setCertiPermissionDt(rs.getDate("cl_perm_dt"));
                dobj.setCertificationDt(rs.getDate("clear_dt"));
                dobj.setCertifiedBy(rs.getString("clear_by"));
                if (tableName == null ? TableList.VA_NON_USE_RESTORE_REMOVE == null : tableName.equals(TableList.VA_NON_USE_RESTORE_REMOVE)) {
                    dobj.setTowedVehicleNo(rs.getString("towed_veh_no"));
                    dobj.setRegisAuthority(rs.getString("off_cd"));
                    dobj.setNewGarageLocation(rs.getString("place"));
                    dobj.setRemarks(rs.getString("cunt_reson"));
                    dobj.setValueOfRadioEvent(rs.getString("rr_flag"));
                    dobj.setVehicle_use_frm(rs.getDate("vehicle_use_frm"));
                }
                dobj.setGarage_add1(rs.getString("garage_add1"));
                dobj.setGarage_add2(rs.getString("garage_add2"));
                dobj.setGarage_add3(rs.getString("garage_add3"));
                dobj.setGarage_state(rs.getString("garage_state"));
                dobj.setGarage_district(rs.getInt("garage_district"));
                dobj.setGarage_pincode(rs.getInt("garage_pincode"));


            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Non Use Record Not Found");
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

    public int getMonthOfDate(Date date) {
        java.util.Date dateLocal = date;
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateLocal);
        int month = cal.get(Calendar.MONTH) + 1;
        return month;
    }

    public void saveNonUse(NonUseDobj dobj, Status_dobj statusDobj, String changedDataContents, TmConfigurationNonUseDobj configDobj) throws VahanException {
        TransactionManager tmgr = null;
        boolean flag = false;
        try {
            tmgr = new TransactionManager("saveNonUse");

            NonUseDobj nonUseDobj = getPreviousDetails(statusDobj.getRegn_no());
            if (nonUseDobj != null && statusDobj.getAction_cd() == TableConstants.VM_ROLE_NON_USE_INTIMATION_ENTRY) {
                if (configDobj != null && configDobj.isDisable_nonuse_fromdate_in_nonuse_continue()) {
                    int nonuse_uoto_month = getMonthOfDate(nonUseDobj.getExemp_to());
                    int current_date_month = getMonthOfDate(new Date());
                    if (nonuse_uoto_month != current_date_month) {
                        throw new VahanException("Non Use Entry Already Found.You Can Continue Only In NonUse Upto Month");
                    }
                }

            }
            boolean dataExist = checkExist(tmgr, statusDobj.getAppl_no(), statusDobj.getRegn_no(), statusDobj.getPur_cd());
            if (dataExist) {
                if (!changedDataContents.isEmpty()) {
                    moveInHistoryNonUseDetails(tmgr, dobj, statusDobj);
                    insertIntoVhaChangedData(tmgr, statusDobj.getAppl_no(), changedDataContents);
                    updateNonUseDetails(tmgr, dobj, statusDobj, configDobj);
                    flag = true;
                }

            } else {
                insertNonUseDetails(tmgr, dobj, statusDobj, configDobj);
                flag = true;
            }
            if (flag) {
                tmgr.commit();
            }
        } catch (VahanException ve) {
            throw ve;
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

    public boolean checkExist(TransactionManager tmgr, String appl_no, String regn_no, int pur_cd) {
        PreparedStatement pstm = null;
        boolean flag = false;
        RowSet rs = null;
        String sql = "";
        try {
            if (pur_cd == TableConstants.VM_MAST_NON_USE) {
                sql = "select regn_no from " + TableList.VA_NON_USE_TAX_EXEM + " "
                        + " where regn_no=? and appl_no=? and state_cd=?";
            } else if (pur_cd == TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE) {
                sql = "select regn_no from " + TableList.VA_NON_USE_RESTORE_REMOVE + " "
                        + " where regn_no=? and appl_no=? and state_cd=? ";
            }
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(1, regn_no);
            pstm.setString(2, appl_no);
            pstm.setString(3, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                flag = true;
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return flag;
    }

    public void isSaveAndMove(NonUseDobj dobj, Status_dobj statusDobj, String changedDataContents, TmConfigurationNonUseDobj configDobj) throws VahanException, Exception {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("isSaveAndMove");
            NonUseDobj previousNonUseDobj = getPreviousDetails(statusDobj.getRegn_no());
            if (previousNonUseDobj != null && statusDobj.getAction_cd() == TableConstants.VM_ROLE_NON_USE_INTIMATION_ENTRY) {
                if (configDobj != null && configDobj.isDisable_nonuse_fromdate_in_nonuse_continue()) {
                    int nonuse_uoto_month = getMonthOfDate(previousNonUseDobj.getExemp_to());
                    int current_date_month = getMonthOfDate(new Date());
                    if (nonuse_uoto_month != current_date_month) {
                        throw new VahanException("Non Use Entry Already Found.You Can Continue Only In NonUse Upto Month");
                    }
                }
            }

            boolean flag = checkExist(tmgr, statusDobj.getAppl_no(), statusDobj.getRegn_no(), statusDobj.getPur_cd());
            if (flag) {
                if (!changedDataContents.isEmpty()) {
                    moveInHistoryNonUseDetails(tmgr, dobj, statusDobj);
                    insertIntoVhaChangedData(tmgr, statusDobj.getAppl_no(), changedDataContents);
                    updateNonUseDetails(tmgr, dobj, statusDobj, configDobj);
                }
                if (statusDobj.getAction_cd() == TableConstants.VM_ROLE_NON_USE_INTIMATION_APPROVE) {
                    if (previousNonUseDobj != null) {
                        insertInVhNonUseFromVtNonUse(previousNonUseDobj, tmgr);
                        deleteFromVtNonUse(previousNonUseDobj, tmgr);
                        insertInVhNonUseAdjustFromVtNonUseAdjust(previousNonUseDobj, tmgr);
                        deleteFromVtNonUseAdjust(previousNonUseDobj, tmgr);
                    }
                    insertInVtNonUseFromVaNonUse(tmgr, dobj, statusDobj, configDobj);
                    statusDobj.setEntry_status(TableConstants.STATUS_APPROVED);
                    ServerUtil.updateApprovedStatus(tmgr, statusDobj);

                } else if (statusDobj.getAction_cd() == TableConstants.VM_ROLE_NON_USE_RESTORE_REMOVE_APPROVAL) {
                    if (dobj.getValueOfRadioEvent().equalsIgnoreCase("R")) {
                        if (configDobj != null && configDobj.isNonuse_adjust_in_tax_amt()) {
                            insertIntoVhaNonuseFormVtNonUse(tmgr, dobj, statusDobj);
                            updateRestoreDetails(dobj, tmgr, statusDobj, configDobj);
                            insertUpdateInVtNonUseTaxAdjustFromVaNonUseRestore(tmgr, dobj, statusDobj);//Add new table for Non-Use Adjustment Amount in saperate table
                        } else {
                            insertIntoVhaNonuseFormVtNonUse(tmgr, dobj, statusDobj);
                            updateRestoreDetails(dobj, tmgr, statusDobj, configDobj);

                            if (configDobj != null && configDobj.isTaxclear_for_nonuse_rebate_in_duration()) {
                                insertIntoVtTaxClear(dobj, tmgr);
                            }

                        }
                        insertInVhNonUseFromVtNonUse(dobj, tmgr);
                        deleteFromVtNonUse(dobj, tmgr);
                        deleteRemovalRestoreDetails(dobj, tmgr);
                    } else if (dobj.getValueOfRadioEvent().equalsIgnoreCase("C")) {
                        insertIntoVhaNonuseFormVtNonUse(tmgr, dobj, statusDobj);
                        //insertInVhNonUseFromVtNonUse(dobj, tmgr);
                        updateRemovalDetails(dobj, tmgr, statusDobj, configDobj);
                        insertIntoVhaNonuseFormVtNonUse(tmgr, dobj, statusDobj);
                        if (configDobj != null && configDobj.isRemove_frm_nonuse_in_removalshift()) {
                            insertInVhNonUseFromVtNonUse(dobj, tmgr);
                            deleteFromVtNonUse(dobj, tmgr);

                            if (configDobj != null && configDobj.isTaxclear_for_nonuse_rebate_in_duration()) {
                                insertIntoVtTaxClear(dobj, tmgr);
                            }

                        }
                        deleteRemovalRestoreDetails(dobj, tmgr);
                    } else if (dobj.getValueOfRadioEvent().equalsIgnoreCase("NC")) {
                        insertIntoVhaNonuseFormVtNonUse(tmgr, dobj, statusDobj);
                        insertInVhNonUseFromVtNonUse(dobj, tmgr);
                        updateRemovalDetails(dobj, tmgr, statusDobj, configDobj);
                        deleteRemovalRestoreDetails(dobj, tmgr);
                    }
                    statusDobj.setEntry_status(TableConstants.STATUS_APPROVED);
                    ServerUtil.updateApprovedStatus(tmgr, statusDobj);
                }
            } else {
                insertNonUseDetails(tmgr, dobj, statusDobj, configDobj);
            }

            ServerUtil.webServiceForNextStage(statusDobj, tmgr);
            ServerUtil.fileFlow(tmgr, statusDobj);
            tmgr.commit();

        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
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

    public List getInspectionOfficer() throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        List InspectionOfficer = new ArrayList<>();
        SelectItem item = new SelectItem("-1", "Select Inspection Officer");
        InspectionOfficer.add(item);
        try {
            tmgr = new TransactionManager("getInspectionOfficer");
            String sql = "select user_cd,user_name from " + TableList.TM_USER_INFO + " where state_cd=? and user_catg='L' ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            //ps.setInt(2, Util.getSelectedSeat().getOff_cd());
            RowSet rowSet = tmgr.fetchDetachedRowSet();
            while (rowSet.next()) {
                item = new SelectItem(rowSet.getString("user_cd"), rowSet.getString("user_name"));
                InspectionOfficer.add(item);
            }
        } catch (SQLException e) {
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
        return InspectionOfficer;
    }

    public Date getTaxUpto(String regn_no) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement pstm = null;
        Date taxUpto = null;
        RowSet rs = null;
        String sql = "";
        try {
            tmgr = new TransactionManagerReadOnly("getTaxUpto");
            sql = "select tax_upto from " + TableList.VT_TAX + " where regn_no=? and tax_upto=(select max(tax_upto) from vt_tax where regn_no=?)";
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(1, regn_no);
            pstm.setString(2, regn_no);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                taxUpto = rs.getDate("tax_upto");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Unabe to Get Tax Details For The Vehicle " + regn_no);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return taxUpto;
    }

    public static boolean isTaxPaidOrClearedNonUse(String regNo, Date exemFromDt) throws VahanException {
        boolean isTaxPaid = false;
        TransactionManagerReadOnly tmgr = null;
        String taxMode = null;
        Date taxPaidUpto = null;
        try {
            tmgr = new TransactionManagerReadOnly("isTaxPaidOrClearedNonUse");
            String sql = "Select tax_upto,tax_mode from " + TableList.VT_TAX
                    + " where regn_no=? and state_cd=? and pur_cd=? and tax_mode != 'B' order by rcpt_dt desc limit 1 ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, TableConstants.TM_ROAD_TAX);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                taxMode = rs.getString("tax_mode");
                if (!"OLS".contains(taxMode)) {
                    taxPaidUpto = rs.getDate("tax_upto");
                } else {
                    return isTaxPaid = true;
                }
            }

            Date taxClearFrom = null;
            Date taxClearTo = null;
            sql = "Select clear_fr,clear_to from " + TableList.VT_TAX_CLEAR + " where regn_no=? and state_cd=? and pur_cd=? order by op_dt desc limit 1 ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, TableConstants.TM_ROAD_TAX);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                taxClearFrom = rs.getDate("clear_fr");
                taxClearTo = rs.getDate("clear_to");
            }

            if (taxPaidUpto != null && DateUtils.compareDates(exemFromDt, taxPaidUpto) <= 1) {
                isTaxPaid = true;
            } else if (taxClearTo != null && DateUtils.compareDates(exemFromDt, taxClearTo) <= 1) {
                isTaxPaid = true;
            }
            return isTaxPaid;

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching Details of Tax Paid or Clear, Please Contact to the System Administrator.");
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

    private void insertInVtNonUseFromVaNonUse(TransactionManager tmgr, NonUseDobj dobj, Status_dobj statusDobj, TmConfigurationNonUseDobj configDobj) throws VahanException {
        PreparedStatement pstm = null;
        RowSet rs = null;
        int i = 1;
        try {
            String sql = "INSERT INTO " + TableList.VT_NON_USE_TAX_EXEM + "(\n"
                    + " state_cd, off_cd, appl_no, regn_no, exem_fr, exem_to, exem_by, \n"
                    + " perm_no, perm_dt, remark, deal_cd, cl_perm_dt, clear_by, clear_dt, \n"
                    + " c_deal_cd, place, cr_no, insp_off, insp_date, insp_flag, doc_flag, \n"
                    + " nonuse_adjust_amt, penalty, op_dt ,garage_add1 , garage_add2 , garage_add3, garage_district, garage_state , garage_pincode)\n"
                    + " SELECT state_cd, off_cd, appl_no, regn_no, exem_fr, exem_to, exem_by, \n"
                    + " perm_no, perm_dt, remark, deal_cd, cl_perm_dt, clear_by, clear_dt, \n"
                    + " c_deal_cd, place, cr_no, insp_off, insp_date, insp_flag, doc_flag, \n"
                    + " nonuse_adjust_amt, penalty, op_dt,garage_add1 , garage_add2 , garage_add3, garage_district, garage_state , garage_pincode\n"
                    + "  FROM " + TableList.VA_NON_USE_TAX_EXEM + " "
                    + " WHERE state_cd=? and off_cd=? and regn_no=? and appl_no=?";
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(i++, Util.getUserStateCode());
            pstm.setInt(i++, Util.getSelectedSeat().getOff_cd());
            pstm.setString(i++, statusDobj.getRegn_no());
            pstm.setString(i++, statusDobj.getAppl_no());
            pstm.executeUpdate();

            if (configDobj != null && configDobj.isNonuse_adjust_in_tax_amt()) {
                i = 1;
                sql = "select nonuse_adjust_amt,penalty from " + TableList.VT_NON_USE_TAX_ADJUST + " where state_cd=? and off_cd=? and regn_no=? ";
                pstm = tmgr.prepareStatement(sql);
                pstm.setString(i++, Util.getUserStateCode());
                pstm.setInt(i++, Util.getSelectedSeat().getOff_cd());
                pstm.setString(i++, statusDobj.getRegn_no());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    if ((long) rs.getDouble("nonuse_adjust_amt") == 0 && (long) rs.getDouble("penalty") == 0) {
                        insertInVhNonUseAdjustFromVtNonUseAdjust(dobj, tmgr);
                        deleteFromVtNonUseAdjust(dobj, tmgr);
                    }
                }
                i = 1;
                sql = "INSERT INTO " + TableList.VT_NON_USE_TAX_ADJUST + "(\n"
                        + " state_cd, off_cd, appl_no, regn_no, exem_fr, exem_to, exem_by, \n"
                        + " nonuse_adjust_amt, penalty, op_dt)\n"
                        + " SELECT state_cd, off_cd, appl_no, regn_no, exem_fr, exem_to, exem_by, \n"
                        + " nonuse_adjust_amt, penalty, op_dt \n"
                        + "  FROM " + TableList.VA_NON_USE_TAX_EXEM + " "
                        + " WHERE state_cd=? and off_cd=? and regn_no=? and appl_no=?";
                pstm = tmgr.prepareStatement(sql);
                pstm.setString(i++, Util.getUserStateCode());
                pstm.setInt(i++, Util.getSelectedSeat().getOff_cd());
                pstm.setString(i++, statusDobj.getRegn_no());
                pstm.setString(i++, statusDobj.getAppl_no());
                pstm.executeUpdate();

            }

            i = 1;
            sql = "DELETE FROM " + TableList.VA_NON_USE_TAX_EXEM + "\n"
                    + " WHERE state_cd=? and off_cd=? and regn_no=? and appl_no=?";
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(i++, Util.getUserStateCode());
            pstm.setInt(i++, Util.getSelectedSeat().getOff_cd());
            pstm.setString(i++, statusDobj.getRegn_no());
            pstm.setString(i++, statusDobj.getAppl_no());
            pstm.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: There is some problem in approval");
        }
    }

    private void insertUpdateInVtNonUseTaxAdjustFromVaNonUseRestore(TransactionManager tmgr, NonUseDobj dobj, Status_dobj statusDobj) throws VahanException {
        PreparedStatement pstm = null;
        RowSet rs = null;
        String sql = "";

        try {
            int i = 1;
            sql = "select appl_no,exem_fr,exem_to,nonuse_adjust_amt,penalty,op_dt from " + TableList.VT_NON_USE_TAX_ADJUST + " where state_cd=? and off_cd=? and regn_no=? ";
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(i++, Util.getUserStateCode());
            pstm.setInt(i++, Util.getSelectedSeat().getOff_cd());
            pstm.setString(i++, statusDobj.getRegn_no());
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                i = 1;
                sql = "UPDATE " + TableList.VT_NON_USE_TAX_ADJUST + " a\n"
                        + "  SET appl_no=b.appl_no, exem_fr=b.exem_fr, exem_to=b.exem_to, nonuse_adjust_amt=b.nonuse_adjust_amt, penalty=b.penalty,op_dt=current_timestamp \n"
                        + "  FROM " + TableList.VA_NON_USE_RESTORE_REMOVE + "  b"
                        + " WHERE a.regn_no=b.regn_no and b.appl_no=? and b.regn_no=? and b.state_cd=?";
                pstm = tmgr.prepareStatement(sql);
                pstm.setString(i++, dobj.getAppl_no());
                pstm.setString(i++, dobj.getRegn_no());
                pstm.setString(i++, Util.getUserStateCode());
                pstm.executeUpdate();
            } else {
                i = 1;
                sql = "select na.regn_no from " + TableList.VH_NON_USE_TAX_ADJUST + " na inner join " + TableList.VA_NON_USE_RESTORE_REMOVE + " nr on na.regn_no=nr.regn_no and na.exem_fr=nr.exem_fr and na.state_cd=nr.state_cd "
                        + " WHERE nr.state_cd=? and nr.off_cd=? and nr.regn_no=? order by nr.op_dt desc limit 1 ";
                pstm = tmgr.prepareStatement(sql);
                pstm.setString(i++, Util.getUserStateCode());
                pstm.setInt(i++, Util.getSelectedSeat().getOff_cd());
                pstm.setString(i++, statusDobj.getRegn_no());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (!rs.next()) {
                    i = 1;
                    sql = "INSERT INTO " + TableList.VT_NON_USE_TAX_ADJUST + "(\n"
                            + " state_cd, off_cd, appl_no, regn_no, exem_fr, exem_to, exem_by, \n"
                            + " nonuse_adjust_amt, penalty, op_dt)\n"
                            + " SELECT state_cd, off_cd, appl_no, regn_no, exem_fr, exem_to, exem_by, \n"
                            + " nonuse_adjust_amt, penalty, op_dt \n"
                            + "  FROM " + TableList.VA_NON_USE_RESTORE_REMOVE + " "
                            + " WHERE state_cd=? and off_cd=? and regn_no=? and appl_no=?";
                    pstm = tmgr.prepareStatement(sql);
                    pstm.setString(i++, Util.getUserStateCode());
                    pstm.setInt(i++, Util.getSelectedSeat().getOff_cd());
                    pstm.setString(i++, statusDobj.getRegn_no());
                    pstm.setString(i++, statusDobj.getAppl_no());
                    pstm.executeUpdate();
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: There is some problem in approval");
        }
    }

    public String getTransportCategory(int vh_class) throws VahanException {
        String isGoodsVehicle = "";
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            String sql = "SELECT vh_class, descr, class_type, transport_catg\n"
                    + "  FROM " + TableList.VM_VH_CLASS + " where vh_class=?";
            tmgr = new TransactionManager("getTransportCategory");
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, vh_class);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isGoodsVehicle = rs.getString("transport_catg");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Not Able to Get Transport Category");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return isGoodsVehicle;
    }

    public NonUseDobj geTotalNonUseAmount(Owner_dobj ownerDobj) throws VahanException {
        long totalNonUseAmount = 0L;
        long totalNonUsePenalty = 0L;
        NonUseDobj nonUseDobj = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            String sql = "select regn_no,nonuse_adjust_amt,penalty from " + TableList.VT_NON_USE_TAX_ADJUST + " "
                    + " where regn_no=?  and state_cd=? ";
            tmgr = new TransactionManager("geTotalNonUseAmount");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getRegn_no());
            ps.setString(2, ownerDobj.getState_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                nonUseDobj = new NonUseDobj();
                totalNonUseAmount = (long) rs.getDouble("nonuse_adjust_amt") + totalNonUseAmount;
                totalNonUsePenalty = (long) rs.getDouble("penalty") + totalNonUsePenalty;
                nonUseDobj.setRegn_no(rs.getString("regn_no"));
                nonUseDobj.setAdjustmentAmount(totalNonUseAmount);
                nonUseDobj.setNonUsePenalty(totalNonUsePenalty);
            }

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error:Unable to Calculate NonUse Amount");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return nonUseDobj;
    }

    public static boolean nonUseDetailsExist(String regnNo, String stateCd) {
        boolean isDetailsExist = false;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            String sql = "select regn_no from " + TableList.VA_NON_USE_TAX_EXEM + " where regn_no=? and state_cd=?";
            tmgr = new TransactionManager("nonUseDetailsExist");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                isDetailsExist = true;
            }
            sql = "select regn_no from " + TableList.VT_NON_USE_TAX_EXEM + " where regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);

            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isDetailsExist = true;
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
        return isDetailsExist;
    }

    public NonUseDobj getAdjustmentAmount(TaxDobj taxDobj, String goodsVehicle, int nonUseNoOfMonth, String veh_flag, Long tax_amt) {
        NonUseDobj nonUseDobj = null;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        try {

            String sql = "select replace(split_part(vahan4.getnonuseadjustmentamount(?,?,?,?,?,?)::varchar,',',1),'(','')::numeric as nonUseAmount,"
                    + " replace(split_part(vahan4.getnonuseadjustmentamount(?,?,?,?,?,?)::varchar,',',2),')','')::numeric as nonUsePenalty";
            tmgr = new TransactionManagerReadOnly("getAdjustmentAmount");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setLong(2, tax_amt);
            ps.setInt(3, nonUseNoOfMonth);
            ps.setString(4, taxDobj.getTax_mode());
            ps.setString(5, goodsVehicle);
            ps.setString(6, veh_flag);
            ps.setString(7, Util.getUserStateCode());
            ps.setLong(8, tax_amt);
            ps.setInt(9, nonUseNoOfMonth);
            ps.setString(10, taxDobj.getTax_mode());
            ps.setString(11, goodsVehicle);
            ps.setString(12, veh_flag);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                nonUseDobj = new NonUseDobj();
                if (rs.getDouble("nonUseAmount") > 0) {
                    nonUseDobj.setAdjustmentAmount((long) (rs.getDouble("nonUseAmount")));
                } else {
                    nonUseDobj.setAdjustmentAmount((long) 0);
                }
                nonUseDobj.setNonUsePenalty((long) (rs.getDouble("nonUsePenalty")));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Unable to get Adjustment Amount");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
            return nonUseDobj;
        }
    }

    public void updateRestoreDetails(NonUseDobj dobj, TransactionManager tmgr, Status_dobj statusDobj, TmConfigurationNonUseDobj configDobj) throws VahanException {
        PreparedStatement ps = null;
        int i = 1;
        try {
            String sql = " UPDATE " + TableList.VA_NON_USE_RESTORE_REMOVE + "\n"
                    + "   SET exem_fr=?, exem_to=?, cr_no=?, insp_off=?, \n"
                    + "       insp_date=?,insp_flag=?,nonuse_adjust_amt=?,penalty=?,garage_add1=? , garage_add2 =?, garage_add3=?, garage_district=?, garage_state=? , garage_pincode=?,vehicle_use_frm=? \n"
                    + " WHERE appl_no=? and regn_no=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setDate(i++, new java.sql.Date(dobj.getExemp_from().getTime()));
            ps.setDate(i++, new java.sql.Date(dobj.getExemp_to().getTime()));
            if (!"".equalsIgnoreCase(dobj.getInspectionReportNo()) && dobj.getInspectionReportNo() != null) {
                ps.setString(i++, dobj.getInspectionReportNo());
            } else {
                ps.setString(i++, null);
            }
            if (!"".equalsIgnoreCase(dobj.getInspectedBy()) && dobj.getInspectedBy() != null) {
                ps.setInt(i++, Integer.parseInt(dobj.getInspectedBy()));
            } else {
                ps.setInt(i++, 0);
            }
            if (dobj.getInspectionDate() != null) {
                ps.setDate(i++, new java.sql.Date(dobj.getInspectionDate().getTime()));
            } else {
                ps.setDate(i++, null);
            }
            if (!"".equalsIgnoreCase(dobj.getInsFlag()) && dobj.getInsFlag() != null) {
                ps.setString(i++, dobj.getInsFlag());
            } else {
                ps.setString(i++, null);
            }
            ps.setLong(i++, dobj.getAdjustmentAmount());
            ps.setLong(i++, dobj.getNonUsePenalty());
            ps.setString(i++, dobj.getGarage_add1());
            ps.setString(i++, dobj.getGarage_add2());
            ps.setString(i++, dobj.getGarage_add3());
            ps.setInt(i++, dobj.getGarage_district());
            ps.setString(i++, dobj.getGarage_state());
            ps.setInt(i++, dobj.getGarage_pincode());
            if (configDobj != null && configDobj.isDeclare_withdrawl_date()) {
                ps.setDate(i++, new java.sql.Date(dobj.getVehicle_use_frm().getTime()));
            } else {
                ps.setDate(i++, null);
            }
            ps.setString(i++, dobj.getAppl_no());
            ps.setString(i++, dobj.getRegn_no());
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.executeUpdate();

            if (statusDobj.getAction_cd() == TableConstants.VM_ROLE_NON_USE_RESTORE_REMOVE_APPROVAL) {
                sql = "UPDATE " + TableList.VT_NON_USE_TAX_EXEM + " a\n"
                        + "          SET exem_fr=b.exem_fr, exem_to=b.exem_to, cr_no=b.cr_no, insp_off=b.insp_off, insp_date=b.insp_date, insp_flag=b.insp_flag, doc_flag=b.doc_flag, \n"
                        + "           nonuse_adjust_amt=b.nonuse_adjust_amt, penalty=b.penalty,op_dt=current_timestamp,garage_add1=b.garage_add1 , garage_add2 =b.garage_add2, garage_add3=b.garage_add3, garage_district=b.garage_district, garage_state=b.garage_state , garage_pincode=b.garage_pincode\n"
                        + "  FROM " + TableList.VA_NON_USE_RESTORE_REMOVE + "  b"
                        + " WHERE a.regn_no=b.regn_no and b.appl_no=? and b.regn_no=? and b.state_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getAppl_no());
                ps.setString(2, dobj.getRegn_no());
                ps.setString(3, Util.getUserStateCode());
                ps.executeUpdate();

            }


        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Unable to Update Restore Details");
        }

    }

    public void updateRemovalDetails(NonUseDobj dobj, TransactionManager tmgr, Status_dobj statusDobj, TmConfigurationNonUseDobj configDobj) throws VahanException {
        PreparedStatement ps = null;
        int i = 1;
        try {
            String sql = "UPDATE " + TableList.VA_NON_USE_RESTORE_REMOVE + "\n"
                    + "   SET exem_fr=?, exem_to=?,  off_cd=?, place=?,op_dt=current_timestamp,towed_veh_no=?,cunt_reson=? ,garage_add1=? , garage_add2 =?, garage_add3=?, garage_district=?, garage_state=? , garage_pincode=?,vehicle_use_frm=?\n"
                    + " WHERE appl_no=? and regn_no=? and state_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setDate(i++, new java.sql.Date(dobj.getExemp_from().getTime()));
            ps.setDate(i++, new java.sql.Date(dobj.getExemp_to().getTime()));
            if (!"".equalsIgnoreCase(dobj.getRegisAuthority()) && dobj.getRegisAuthority() != null) {
                ps.setInt(i++, Integer.parseInt(dobj.getRegisAuthority()));
            } else {
                ps.setInt(i++, 0);
            }
            ps.setString(i++, dobj.getNewGarageLocation());
            ps.setString(i++, dobj.getTowedVehicleNo());
            ps.setString(i++, dobj.getRemarks());
            ps.setString(i++, dobj.getGarage_add1());
            ps.setString(i++, dobj.getGarage_add2());
            ps.setString(i++, dobj.getGarage_add3());
            ps.setInt(i++, dobj.getGarage_district());
            ps.setString(i++, dobj.getGarage_state());
            ps.setInt(i++, dobj.getGarage_pincode());
            if (configDobj != null && configDobj.isDeclare_withdrawl_date()) {
                ps.setDate(i++, new java.sql.Date(dobj.getVehicle_use_frm().getTime()));
            } else {
                ps.setDate(i++, null);
            }
            ps.setString(i++, dobj.getAppl_no());
            ps.setString(i++, dobj.getRegn_no());
            ps.setString(i++, Util.getUserStateCode());
            ps.executeUpdate();

            if (statusDobj.getAction_cd() == TableConstants.VM_ROLE_NON_USE_RESTORE_REMOVE_APPROVAL) {
                sql = "update " + TableList.VT_NON_USE_TAX_EXEM + " a set exem_fr=b.exem_fr,exem_to=b.exem_to, off_cd=b.off_cd, place=b.place, op_dt=current_timestamp, towed_veh_no=b.towed_veh_no ,garage_add1=b.garage_add1 , garage_add2 =b.garage_add2, garage_add3=b.garage_add3, garage_district=b.garage_district, garage_state=b.garage_state , garage_pincode=b.garage_pincode\n"
                        + "from " + TableList.VA_NON_USE_RESTORE_REMOVE + " b \n"
                        + "where a.regn_no=b.regn_no and b.appl_no=? and b.regn_no=? and b.state_cd=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, dobj.getAppl_no());
                ps.setString(2, dobj.getRegn_no());
                ps.setString(3, Util.getUserStateCode());
                ps.executeUpdate();
            }


        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Unable to Update Removal Details");
        }
    }

    public void deleteRemovalRestoreDetails(NonUseDobj dobj, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        try {
            String sql = "DELETE FROM " + TableList.VA_NON_USE_RESTORE_REMOVE + "\n"
                    + " WHERE appl_no=? and regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getAppl_no());
            ps.setString(2, dobj.getRegn_no());
            ps.setString(3, Util.getUserStateCode());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Unable to Delete Removal/Restore Details");
        }
    }

    public void insertInVhNonUseFromVtNonUse(NonUseDobj dobj, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        try {

            String sql = "INSERT INTO " + TableList.VH_NON_USE_TAX_EXEM + "(\n"
                    + " state_cd, off_cd, appl_no, regn_no, exem_fr, exem_to, exem_by, \n"
                    + " perm_no, perm_dt, remark, deal_cd, cl_perm_dt, clear_by, clear_dt, \n"
                    + " c_deal_cd, place, cr_no, insp_off, insp_date, insp_flag, doc_flag, \n"
                    + " nonuse_adjust_amt, op_dt, moved_on, moved_by,towed_veh_no,penalty,garage_add1 , garage_add2 , garage_add3, garage_district, garage_state , garage_pincode )\n"
                    + " SELECT state_cd, off_cd, appl_no, regn_no, exem_fr, exem_to, exem_by, \n"
                    + " perm_no, perm_dt, remark, deal_cd, cl_perm_dt, clear_by, clear_dt, \n"
                    + " c_deal_cd, place, cr_no, insp_off, insp_date, insp_flag, doc_flag, \n"
                    + " nonuse_adjust_amt, op_dt, statement_timestamp(),? as moved_by,towed_veh_no,penalty,garage_add1 , garage_add2 , garage_add3, garage_district, garage_state , garage_pincode \n"
                    + " FROM " + TableList.VT_NON_USE_TAX_EXEM + ""
                    + " where regn_no=?  and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, dobj.getRegn_no());
            ps.setString(3, Util.getUserStateCode());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Unable to Move Non Use Data In History");
        }
    }

    public void deleteFromVtNonUse(NonUseDobj dobj, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        try {
            String sql = " delete  from " + TableList.VT_NON_USE_TAX_EXEM + " "
                    + " where regn_no=?  and state_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getRegn_no());
            ps.setString(2, Util.getUserStateCode());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Unable to Delete Non Use Record");
        }
    }

    public void insertInVhNonUseAdjustFromVtNonUseAdjust(NonUseDobj dobj, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO " + TableList.VH_NON_USE_TAX_ADJUST + "(\n"
                    + " state_cd, off_cd, appl_no, regn_no, exem_fr, exem_to, exem_by, \n"
                    + " nonuse_adjust_amt, penalty, op_dt, moved_on, moved_by)\n"
                    + " SELECT state_cd, off_cd, appl_no, regn_no, exem_fr, exem_to, exem_by, \n"
                    + " nonuse_adjust_amt, penalty, op_dt, statement_timestamp(),? as moved_by \n"
                    + " FROM " + TableList.VT_NON_USE_TAX_ADJUST + ""
                    + " where regn_no=?  and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, dobj.getRegn_no());
            ps.setString(3, Util.getUserStateCode());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Unable to Move Non Use Data In History");
        }
    }

    public void deleteFromVtNonUseAdjust(NonUseDobj dobj, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        try {
            String sql = " delete  from " + TableList.VT_NON_USE_TAX_ADJUST + " "
                    + " where regn_no=?  and state_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getRegn_no());
            ps.setString(2, Util.getUserStateCode());
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Unable to Delete Non Use Record");
        }
    }

    private void insertIntoVtTaxClear(NonUseDobj dobj, TransactionManager tmgr) throws VahanException {
        PreparedStatement ps = null;
        try {
            String sql = "INSERT INTO vahan4.vt_tax_clear(\n"
                    + " state_cd, off_cd, appl_no, regn_no, pur_cd, clear_fr, clear_to, \n"
                    + " tcr_no, iss_auth, iss_dt, remark, user_cd, op_dt)\n"
                    + " SELECT state_cd, off_cd, appl_no, regn_no, ?, exem_fr, exem_to, \n"
                    + " perm_no,  clear_by, cl_perm_dt, remark, deal_cd, op_dt\n"
                    + " FROM vahan4.vt_non_use_tax_exem where regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, TableConstants.TM_ROAD_TAX);
            ps.setString(2, dobj.getRegn_no());
            ps.setString(3, Util.getUserStateCode());
            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: There is some problem In Restore / Remove Details");
        }

    }

    public void insertIntoVhaNonuseFormVtNonUse(TransactionManager tmgr, NonUseDobj dobj, Status_dobj statusDobj) throws VahanException {
        String sql = "";
        PreparedStatement pstm = null;
        int i = 1;
        try {
            if (statusDobj.getPur_cd() == TableConstants.VM_MAST_NON_USE) {
                sql = "INSERT INTO " + TableList.VHA_NON_USE_TAX_EXEM + "(\n"
                        + "            moved_on, moved_by, state_cd, off_cd, appl_no, regn_no, exem_fr, \n"
                        + "            exem_to, exem_by, perm_no, perm_dt, remark, deal_cd, cl_perm_dt, \n"
                        + "            clear_by, clear_dt, c_deal_cd, place, cr_no, insp_off, insp_date, \n"
                        + "            insp_flag, doc_flag, nonuse_adjust_amt, penalty, op_dt,garage_add1 , garage_add2 , garage_add3, garage_district, garage_state , garage_pincode    )\n"
                        + "  SELECT statement_timestamp(), ? as moved_by,state_cd, off_cd, appl_no, regn_no, exem_fr, exem_to, exem_by, \n"
                        + "       perm_no, perm_dt, remark, deal_cd, cl_perm_dt, clear_by, clear_dt, \n"
                        + "       c_deal_cd, place, cr_no, insp_off, insp_date, insp_flag, doc_flag, \n"
                        + "       nonuse_adjust_amt,penalty, op_dt ,garage_add1 , garage_add2 , garage_add3, garage_district, garage_state , garage_pincode   \n"
                        + "  FROM " + TableList.VT_NON_USE_TAX_EXEM + ""
                        + " WHERE state_cd=? and off_cd=? and regn_no=? and appl_no=?";

                pstm = tmgr.prepareStatement(sql);
                pstm.setString(i++, Util.getEmpCode());
                pstm.setString(i++, Util.getUserStateCode());
                pstm.setInt(i++, Util.getSelectedSeat().getOff_cd());
                pstm.setString(i++, statusDobj.getRegn_no());
                pstm.setString(i++, statusDobj.getAppl_no());
                pstm.executeUpdate();
            }

            if (statusDobj.getPur_cd() == TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE) {
                sql = "INSERT INTO " + TableList.VHA_NON_USE_RESTORE_REMOVE + "(\n"
                        + "            moved_on, moved_by, state_cd, off_cd, appl_no, regn_no, exem_fr, \n"
                        + "            exem_to, exem_by, perm_no, perm_dt, remark, deal_cd, cl_perm_dt, \n"
                        + "            clear_by, clear_dt, c_deal_cd, place, cr_no, insp_off, insp_date, \n"
                        + "            insp_flag, doc_flag, nonuse_adjust_amt, towed_veh_no, penalty, op_dt ,garage_add1 , garage_add2 , garage_add3, garage_district, garage_state , garage_pincode  )\n"
                        + "  SELECT current_timestamp + interval '1 second' as moved_on, ? as moved_by,state_cd, off_cd, appl_no, regn_no, exem_fr, exem_to, exem_by, \n"
                        + "       perm_no, perm_dt, remark, deal_cd, cl_perm_dt, clear_by, clear_dt, \n"
                        + "       c_deal_cd, place, cr_no, insp_off, insp_date, insp_flag, doc_flag, \n"
                        + "       nonuse_adjust_amt,towed_veh_no, penalty , op_dt,garage_add1 , garage_add2 , garage_add3, garage_district, garage_state , garage_pincode \n"
                        + "  FROM " + TableList.VT_NON_USE_TAX_EXEM + ""
                        + " WHERE state_cd=?  and regn_no=? and appl_no=?";
                pstm = tmgr.prepareStatement(sql);
                pstm.setString(i++, Util.getEmpCode());
                pstm.setString(i++, Util.getUserStateCode());
                pstm.setString(i++, statusDobj.getRegn_no());
                pstm.setString(i++, statusDobj.getAppl_no());
                pstm.executeUpdate();
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error : Problem In Moving Data ");
        }

    }

    public CashRecieptSubListDobj gettaxBreakupDetailsForNonUse(TaxDobj taxDobj) throws VahanException {
        CashRecieptSubListDobj cashRecieptSubListDobj = null;
        cashRecieptSubListDobj = getTaxAmount(taxDobj.getRcpt_no(), taxDobj.getPur_cd());
        return cashRecieptSubListDobj;
    }

    public static CashRecieptSubListDobj getTaxAmount(String rcptno, Integer pur_cd) throws VahanException {
        CashRecieptSubListDobj dobj = null;
        PreparedStatement ps = null;
        ResultSet rowSet = null;
        TransactionManager tmgr = null;
        String sql = "select tax from " + TableList.VT_TAX_BREAKUP + " where rcpt_no=? and state_cd =? and off_cd = ? and pur_cd = ? "
                + " ORDER BY tax_from desc limit 1";
        try {
            tmgr = new TransactionManager("getTaxAmountForNonuse");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, rcptno);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            ps.setInt(4, pur_cd);
            rowSet = tmgr.fetchDetachedRowSet();
            if (rowSet.next()) {
                dobj = new CashRecieptSubListDobj();
                dobj.setAmount(String.valueOf(rowSet.getString("tax").replace(".00", "")));
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error("Exception for Receipt no :" + rcptno + " :" + e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dobj;
    }

    public void reback(NonUseDobj dobj, Status_dobj statusDobj, String changedDataContents, TmConfigurationNonUseDobj configDobj) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("isSaveAndMove");
            boolean flag = checkExist(tmgr, statusDobj.getAppl_no(), statusDobj.getRegn_no(), statusDobj.getPur_cd());
            if (flag) {
                if (!changedDataContents.isEmpty()) {
                    moveInHistoryNonUseDetails(tmgr, dobj, statusDobj);
                    insertIntoVhaChangedData(tmgr, statusDobj.getAppl_no(), changedDataContents);
                    updateNonUseDetails(tmgr, dobj, statusDobj, configDobj);
                }
            }
            ServerUtil.webServiceForNextStage(statusDobj, tmgr);
            ServerUtil.fileFlow(tmgr, statusDobj);
            tmgr.commit();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Unable to Revet Back File");
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

    public NonUseDobj getPreviousDetails(String regn_no) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstm = null;
        NonUseDobj dobj = null;
        RowSet rs = null;
        String sql = "";

        try {
            tmgr = new TransactionManager("getPreviousDetails");

            sql = "SELECT state_cd, off_cd, appl_no, regn_no, exem_fr, exem_to, exem_by, \n"
                    + " perm_no, perm_dt, remark, deal_cd, cl_perm_dt, clear_by, clear_dt, \n"
                    + " c_deal_cd, place, cr_no, insp_off, insp_date, insp_flag, doc_flag, \n"
                    + " nonuse_adjust_amt, op_dt , \n"
                    + " garage_add1 , garage_add2 , garage_add3, garage_district, garage_state , garage_pincode \n"
                    + " FROM " + TableList.VT_NON_USE_TAX_EXEM + "  where  regn_no=? and state_cd=?";

            pstm = tmgr.prepareStatement(sql);
            pstm.setString(1, regn_no);
            pstm.setString(2, Util.getUserStateCode());

            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new NonUseDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setLocation_of_garage(rs.getString("place"));
                dobj.setExemp_from(rs.getDate("exem_fr"));
                dobj.setExemp_to(rs.getDate("exem_to"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Unable to get the details of nonuse");
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

    public boolean checkMFormStatus(String regn_no) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstm = null;
        RowSet rs = null;
        String sql = "";
        boolean isExist = false;
        try {
            tmgr = new TransactionManager("getPreviousDetails");

            sql = "select regn_no from " + TableList.VT_PERMIT_TRANSACTION + " where regn_no=? and state_cd =? and trans_pur_cd = ? ";
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(1, regn_no);
            pstm.setString(2, Util.getUserStateCode());
            pstm.setInt(3, 404);

            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isExist = true;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Unable to get the details of M Form");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return isExist;
    }

    public boolean isNonUseAdjustMform(String regn_no) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement pstm = null;
        RowSet rs = null;
        String sql = "";
        boolean isExist = false;
        try {
            tmgr = new TransactionManager("isNonUseAdjustMform");

            sql = "select appl_no from " + TableList.VA_DETAILS + " where pur_cd= " + TableConstants.VM_PMT_SURRENDER_PUR_CD + " and regn_no=? and state_cd=? and appl_no in (select appl_no from " + TableList.VT_NON_USE_TAX_ADJUST + " where regn_no=? and state_cd =?)";
            pstm = tmgr.prepareStatement(sql);
            pstm.setString(1, regn_no);
            pstm.setString(2, Util.getUserStateCode());
            pstm.setString(3, regn_no);
            pstm.setString(4, Util.getUserStateCode());
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isExist = true;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Unable to verify the details of M Form Surrender");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return isExist;
    }
}
