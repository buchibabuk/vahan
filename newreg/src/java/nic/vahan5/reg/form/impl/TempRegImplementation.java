/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.ExArmyDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.ImportedVehicleDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Owner_temp_dobj;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TempRegDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.Trailer_dobj;
import nic.vahan.form.impl.AxleImpl;
import nic.vahan.form.impl.ExArmyImpl;
import nic.vahan.form.impl.FasTagImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.HpaImpl;
import static nic.vahan.form.impl.HpaImpl.insertUpdateHPA;
import nic.vahan.form.impl.ImportedVehicleImpl;
import nic.vahan.form.impl.InsImpl;
import static nic.vahan.form.impl.InsImpl.insertUpdateInsurance;
import static nic.vahan.form.impl.NewImpl.insertIntoVhaOwnerWithTimeInterval;
import static nic.vahan.form.impl.NewImpl.insertOrUpdateVaOwner;
import nic.vahan.form.impl.RetroFittingDetailsImpl;
import nic.vahan.form.impl.TempRegImpl;
import nic.vahan.form.impl.Trailer_Impl;
import static nic.vahan.form.impl.Trailer_Impl.insertUpdateTrailer;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import nic.vahan5.reg.rest.model.SessionVariablesModel;

/**
 *
 * @author Sai
 */
public class TempRegImplementation {
    
     private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(TempRegImpl.class);

    public static String update_New_Status(int role, Owner_dobj owner_dobj, FitnessDobj fitness_dobj, ArrayList fitCheckList,
            Status_dobj status_dobj, String changedData, HpaDobj hpa_dobj, InsDobj ins_dobj, Trailer_dobj trailer_dobj, ExArmyDobj exArmyDobj, ImportedVehicleDobj imp_dobj,
            AxleDetailsDobj axle_dobj, RetroFittingDetailsDobj cng_dobj, int actionCode, Owner_temp_dobj ownerTempDobjPrev, SessionVariablesModel sessionVariables) throws VahanException {
        String tempRegNo = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        ArrayList hpa_list = null;
        String appl_no = status_dobj.getAppl_no();
        boolean validForTempDelete = false;
        int purCd = status_dobj.getPur_cd();
        int feeAmt = 0;


        try {
            tmgr = new TransactionManager("update_NEW_Status");
            TmConfigurationDobj tmConf = Util.getTmConfiguration();

            if ((role == TableConstants.TM_ROLE_ENTRY
                    || role == TableConstants.TM_ROLE_VERIFICATION
                    || role == TableConstants.TM_ROLE_APPROVAL || actionCode == TableConstants.TM_ROLE_NEW_APPL_TEMP
                    || actionCode == TableConstants.TM_TMP_RC_FITNESS_INSPECTION) && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)) {

                if (role == TableConstants.TM_ROLE_ENTRY) {
                    insertOrUpdateVaOwner(tmgr, owner_dobj);
                } else if ((role == TableConstants.TM_ROLE_VERIFICATION
                        || role == TableConstants.TM_ROLE_APPROVAL
                        || role == TableConstants.TM_ROLE_REV_NEW_APPL_TEMP) && !changedData.isEmpty()) {
                    insertOrUpdateVaOwner(tmgr, owner_dobj);
                }

                if (hpa_dobj != null) {
                    hpa_list = new ArrayList();
                    hpa_dobj.setRegn_no(owner_dobj.getRegn_no());
                    hpa_list.add(hpa_dobj);
                    insertUpdateHPA(tmgr, hpa_list, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                } else {
                    HpaImpl.insertDeleteFromVaHpa(tmgr, appl_no);
                }

                if (InsImpl.validateOwnerCodeWithInsType(owner_dobj.getOwner_cd(), ins_dobj.getIns_type())) {
                    insertUpdateInsurance(tmgr, owner_dobj.getAppl_no(), owner_dobj.getRegn_no(), ins_dobj, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                } else {
                    throw new VahanException("Invalid Combination of Ownership Type & Insurance Type.");
                }

                if (trailer_dobj != null && !trailer_dobj.getChasi_no().isEmpty()) {
                    Trailer_Impl.validationTrailer(trailer_dobj);
                    insertUpdateTrailer(tmgr, owner_dobj.getAppl_no(), owner_dobj.getRegn_no(), owner_dobj.getChasi_no(), trailer_dobj);
                }

                if (exArmyDobj != null) {
                    ExArmyImpl.saveExArmyVehicleDetails_Impl(exArmyDobj, owner_dobj.getAppl_no(), tmgr);
                }

                if (imp_dobj != null) {
                    ImportedVehicleImpl.saveImportedDetails_Impl(imp_dobj, owner_dobj.getAppl_no(), tmgr);
                }

                if (axle_dobj != null) {
                    AxleImpl.saveAxleDetails_Impl(axle_dobj, owner_dobj.getAppl_no(), tmgr);
                }

                if (cng_dobj != null) {
                    RetroFittingDetailsImpl.saveCngVehicleDetails_Impl(cng_dobj, owner_dobj.getAppl_no(), tmgr);
                }

                if (owner_dobj.getSpeedGovernorDobj() != null) {
                    FitnessImpl.insertUpdateVaSpeedGovernor(owner_dobj.getSpeedGovernorDobj(), tmgr);
                } else {
                    FitnessImpl.insertIntoVhaSpeedGovernor(appl_no, tmgr);
                    FitnessImpl.deleteVaSpeedGovernor(appl_no, tmgr);
                }
                if (actionCode == TableConstants.TM_TMP_RC_FITNESS_INSPECTION
                        && owner_dobj != null && owner_dobj.getInspectionDobj() != null
                        && owner_dobj.getInspectionDobj().getInsp_dt() != null) {
                    FitnessImpl fitImpl = new FitnessImpl();
                    owner_dobj.getInspectionDobj().setFit_off_cd1(Integer.parseInt(sessionVariables.getEmpCodeLoggedIn()));
                    fitImpl.insertOrUpdateInspection(tmgr, owner_dobj.getInspectionDobj());
                }
            }

            if (TableConstants.STATUS_HOLD.equals(status_dobj.getStatus()) && owner_dobj != null && owner_dobj.getOwner_identity() != null) {
                ServerUtil.sendSMSForHoldApplication(owner_dobj.getOwner_identity().getMobile_no(), status_dobj.getOffice_remark(), owner_dobj.getAppl_no());
            }


            if (role == TableConstants.TM_ROLE_APPROVAL && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT) && !TableConstants.STATUS_HOLD.equals(status_dobj.getStatus())) {
                //Generate New TempRegNo
                tempRegNo = ServerUtil.getUniquePermitNo(tmgr, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), 0, 0, "M");
                if (tempRegNo == null || tempRegNo.equalsIgnoreCase("") || tempRegNo.equalsIgnoreCase("TEMPREG")) {
                    throw new VahanException("Temporary Registration No Generation Failed!!!");
                }

                if (tempRegNo != null) {
                    String message = "Temp Registration No " + tempRegNo + " generated against Application No " + appl_no;
                    ServerUtil.insertUsersTransactionMessage(message, 1, tmgr);
                }

                VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(owner_dobj);
                //Temp Upto Date
                ps = tmgr.prepareStatement("SELECT *  FROM vm_validity_mast where pur_cd=? and state_cd=?");
                ps.setInt(1, TableConstants.VM_TRANSACTION_MAST_TEMP_REG);
                ps.setString(2, sessionVariables.getStateCodeSelected());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                Date dtTempFrom = new Date();
                Date dtTempUpto = new Date();
                if ("JH,BR".contains(vehParameters.getSTATE_CD())) {
                    dtTempFrom = owner_dobj.getPurchase_dt();
                }
                if ("KA,GJ".contains(vehParameters.getSTATE_CD())) {
                    dtTempFrom = DateUtils.parseDate(status_dobj.getAppl_dt());
                }
                vehParameters.setTMP_PURPOSE(owner_dobj.getDob_temp().getPurpose());
                while (rs.next()) {
                    if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "update_New_Status")) {
                        int newUpto = rs.getInt("new_value");
                        String mod = rs.getString("mod");
                        if (mod.equals("D")) {
                            dtTempUpto = DateUtils.addToDate(dtTempFrom, 1, newUpto);
                        } else if (mod.equals("M")) {
                            dtTempUpto = DateUtils.addToDate(dtTempFrom, 2, newUpto);
                            dtTempUpto = DateUtils.addToDate(dtTempUpto, 1, -1);
                        } else if (mod.equals("Y")) {
                            dtTempUpto = DateUtils.addToDate(dtTempFrom, 3, newUpto);
                            dtTempUpto = DateUtils.addToDate(dtTempUpto, 1, -1);
                        }
                    }
                }
                FasTagImpl.updateRegnNoInFasTag(tmgr, owner_dobj.getChasi_no(), tempRegNo, owner_dobj.getState_cd(), owner_dobj.getOff_cd());
                if ("KA".contains(vehParameters.getSTATE_CD())) {
                    ps = tmgr.prepareStatement("select fees from get_appl_rcpt_details(?) where pur_cd = ?");
                    ps.setString(1, status_dobj.getAppl_no());
                    ps.setInt(2, TableConstants.VM_TRANSACTION_MAST_TEMP_REG);

                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        feeAmt = rs.getInt("fees");
                    }
                    if (feeAmt == 25) {
                        dtTempUpto = DateUtils.parseDate(DateUtils.getLastOfMonthDate(status_dobj.getAppl_dt()));
                    }
                }
                /**
                 * If calculated date is same as current date(means no proper
                 * entry is done in Database) Or they are pending cases temp
                 * validity upto date is set to one month from approval date
                 */
                if (DateUtils.compareDates(dtTempUpto, new Date()) <= 1) {
                    dtTempUpto = DateUtils.addToDate(dtTempFrom, 2, 1);
                    dtTempUpto = DateUtils.addToDate(dtTempUpto, 1, -1);
                }

                //for Renewal of Temporary Registration
                if (ownerTempDobjPrev != null
                        && ownerTempDobjPrev.getPurpose() != null
                        && "BB,OM".contains(ownerTempDobjPrev.getPurpose())
                        && ownerTempDobjPrev.getValidUpto() != null) {
                    //if date1 is less than equal to date2
                    if ("BB".contains(ownerTempDobjPrev.getPurpose()) && DateUtils.compareDates(new Date(), ownerTempDobjPrev.getValidUpto()) <= 1) {
                        dtTempFrom = ServerUtil.dateRange(ownerTempDobjPrev.getValidUpto(), 0, 0, 1);
                    } else {
                        dtTempFrom = new Date();
                    }
                    dtTempUpto = ServerUtil.dateRange(dtTempFrom, 0, 1, -1);
                }

                sql = "INSERT into  " + TableList.VT_OWNER_TEMP
                        + " Select a.state_cd, t.off_cd, a.appl_no, ?"//regno
                        + ", ?, ?"
                        //+ ", current_date, current_date + cast('1 months' as interval)-cast('1 days' as interval)"
                        + ",      purchase_dt, owner_name, f_name, c_add1, c_add2, c_add3, c_district, \n"
                        + "       c_pincode, c_state, p_add1, p_add2, p_add3, p_district, p_pincode, \n"
                        + "       p_state, owner_cd, regn_type, vh_class, chasi_no, eng_no, maker, \n"
                        + "       maker_model, body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap, \n"
                        + "       unld_wt, ld_wt, gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, \n"
                        + "       cubic_cap, floor_area, ac_fitted, audio_fitted, video_fitted, \n"
                        + "       vch_purchase_as, vch_catg, dealer_cd, sale_amt, laser_code, garage_add, \n"
                        + "       length, width, height, regn_upto, fit_upto, annual_income, imported_vch, \n"
                        + "       other_criteria, "
                        + " ?," //state_cd_to
                        + " ?," //off_cd_to
                        + " current_timestamp as op_dt,purpose,body_building"
                        + "  FROM " + TableList.VA_OWNER
                        + " a,va_owner_temp t where a.appl_no=t.appl_no and  a.appl_no=?";


                ps = tmgr.prepareStatement(sql);

                ps.setString(1, tempRegNo);
                ps.setDate(2, new java.sql.Date(dtTempFrom.getTime()));
                ps.setDate(3, new java.sql.Date(dtTempUpto.getTime()));
                ps.setString(4, owner_dobj.getDob_temp().getState_cd_to());
                ps.setInt(5, owner_dobj.getDob_temp().getOff_cd_to());
                ps.setString(6, owner_dobj.getAppl_no());
                ps.executeUpdate();

                if (tmConf != null && tmConf.isTempFeeInNewRegis()) {
                    ArrayList<Status_dobj> statusDobjList = ServerUtil.applicationStatusByApplNo(owner_dobj.getAppl_no(), sessionVariables.getStateCodeSelected());
                    if (statusDobjList.size() == 2) {
                        validForTempDelete = true;
                    }
                }

                if (validForTempDelete) {
                    sql = "update " + TableList.VA_TMP_REGN_DTL + " set tmp_regn_no = ? , tmp_regn_dt = ?, tmp_valid_upto = ? where appl_no = ? and state_cd = ? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, tempRegNo);
                    ps.setDate(2, new java.sql.Date(dtTempFrom.getTime()));
                    ps.setDate(3, new java.sql.Date(dtTempUpto.getTime()));
                    ps.setString(4, owner_dobj.getAppl_no());
                    ps.setString(5, sessionVariables.getStateCodeSelected());
                    ps.executeUpdate();
                }

                //Delete From VA_OWNER
                if (!validForTempDelete) {
                    insertIntoVhaOwnerWithTimeInterval(tmgr, owner_dobj.getAppl_no());
                    sql = "Delete from " + TableList.VA_OWNER + " where appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, owner_dobj.getAppl_no());
                    ps.executeUpdate();
                }


                sql = "Delete from " + TableList.VA_OWNER_TEMP + " where appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, owner_dobj.getAppl_no());
                ps.executeUpdate();


                //insert into vt_owner_identification

                sql = "INSERT into  " + TableList.VT_OWNER_IDENTIFICATION
                        + " select ?,?,?," //regn_no
                        + " mobile_no, email_id, pan_no, aadhar_no, passport_no, \n"
                        + "       ration_card_no, voter_id, dl_no, current_timestamp,owner_ctg"
                        + "  FROM " + TableList.VA_OWNER_IDENTIFICATION
                        + " where appl_no=?";


                ps = tmgr.prepareStatement(sql);
                ps.setString(1, sessionVariables.getStateCodeSelected());
                ps.setInt(2, sessionVariables.getOffCodeSelected());
                ps.setString(3, tempRegNo);
                ps.setString(4, owner_dobj.getAppl_no());
                ps.executeUpdate();

                //Delete from va_owner_identification
                if (!validForTempDelete) {
                    sql = "Delete from " + TableList.VA_OWNER_IDENTIFICATION + " where appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, owner_dobj.getAppl_no());
                    ps.executeUpdate();
                }

                // Modified Query to get speedgov_no,speedgov_compname data from va_owner
                // By Ambrish 12-Sep-2014
                // Fitness_Impl.moveFromVaFitenessToVTFiteness(tmgr, owner_dobj.getAppl_no());

                /*sql = "INSERT into vt_hypth "
                 + "SELECT regn_no , sr_no ,  hp_type,fncr_name,  fncr_add1,  fncr_add2, fncr_village, "
                 + " fncr_taluk,  fncr_district,fncr_pincode,  from_dt,"
                 + " current_timestamp as op_dt,state_cd ,  off_cd  "
                 + " FROM va_hypth where appl_no=?";

                 ps = tmgr.prepareStatement(sql);
                 ps.setString(1, owner_dobj.getAppl_no());
                 ps.executeUpdate();*/
                if (hpa_list != null) {
                    for (int i = 0; i < hpa_list.size(); i++) {
                        sql = "INSERT into  " + TableList.VT_HYPTH
                                + " select ?,?,?,"
                                + " sr_no, hp_type, fncr_name, fncr_add1, fncr_add2, fncr_add3, "
                                + "       fncr_district, fncr_pincode, fncr_state, from_dt,current_timestamp as op_dt"
                                + " FROM  " + TableList.VA_HPA
                                + " where appl_no=?";

                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, sessionVariables.getStateCodeSelected());
                        ps.setInt(2, sessionVariables.getOffCodeSelected());
                        ps.setString(3, tempRegNo);
                        ps.setString(4, owner_dobj.getAppl_no());
                        ps.executeUpdate();
                    }
                }

                //Delete from va_hpa
                if (!validForTempDelete) {
                    sql = "Delete from " + TableList.VA_HPA + " where appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, owner_dobj.getAppl_no());
                    ps.executeUpdate();
                }

                sql = "INSERT into " + TableList.VT_INSURANCE + " "
                        + "Select ?,?,? ,"
                        + "  comp_cd ,  ins_type ,  ins_from ,"
                        + " ins_upto ,  policy_no,idv,current_timestamp as op_dt"
                        + " FROM " + TableList.VA_INSURANCE + " where appl_no=?";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, sessionVariables.getStateCodeSelected());
                ps.setInt(2, sessionVariables.getOffCodeSelected());
                ps.setString(3, tempRegNo);
                ps.setString(4, owner_dobj.getAppl_no());
                ps.executeUpdate();

                //Delete from VA_INSURANCE
                if (!validForTempDelete) {
                    sql = "Delete from " + TableList.VA_INSURANCE + " where appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, owner_dobj.getAppl_no());
                    ps.executeUpdate();
                }

                sql = "select DISTINCT fee.rcpt_no from vp_appl_rcpt_mapping map,vt_fee fee"
                        + " where map.appl_no=? and map.rcpt_no=fee.rcpt_no and map.state_cd=fee.state_cd and map.off_cd=fee.off_cd ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, owner_dobj.getAppl_no());

                rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    sql = "UPDATE vt_fee set regn_no= ? WHERE rcpt_no=? and state_cd=? and off_cd=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, tempRegNo);
                    ps.setString(2, rs.getString("rcpt_no"));
                    ps.setString(3, sessionVariables.getStateCodeSelected());
                    ps.setInt(4, sessionVariables.getOffCodeSelected());
                    ps.executeUpdate();
                }
                sql = "select DISTINCT tax.rcpt_no from vp_appl_rcpt_mapping map,vt_tax tax"
                        + " where map.appl_no=? and map.rcpt_no=tax.rcpt_no and map.state_cd=tax.state_cd and map.off_cd=tax.off_cd ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, owner_dobj.getAppl_no());
                rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    sql = "UPDATE vt_tax set regn_no= ? WHERE rcpt_no=? and state_cd=? and off_cd=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, tempRegNo);
                    ps.setString(2, rs.getString("rcpt_no"));
                    ps.setString(3, sessionVariables.getStateCodeSelected());
                    ps.setInt(4, sessionVariables.getOffCodeSelected());
                    ps.executeUpdate();
                }
                // ExArmy Details
                if (exArmyDobj != null) {
                    sql = "INSERT INTO vt_owner_ex_army(\n"
                            + "     state_cd ,off_cd, regn_no, voucher_no, voucher_dt, place, op_dt )\n"
                            + "SELECT ?,?, ?,voucher_no, voucher_dt, place , current_timestamp \n"
                            + "  FROM va_owner_ex_army where appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, sessionVariables.getStateCodeSelected());
                    ps.setInt(2, sessionVariables.getOffCodeSelected());
                    ps.setString(3, tempRegNo);
                    ps.setString(4, owner_dobj.getAppl_no());
                    ps.executeUpdate();
                }

                //Delete from VA_OWNER_EX_ARMY
                if (!validForTempDelete) {
                    sql = "Delete from " + TableList.VA_OWNER_EX_ARMY + " where appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, owner_dobj.getAppl_no());
                    ps.executeUpdate();
                }

                // Imported Vehicle Details
                if (imp_dobj != null) {
                    sql = "INSERT INTO vt_import_veh(\n"
                            + "           state_cd,off_cd, regn_no, contry_code, dealer, place, foreign_regno, \n"
                            + "            manu_year,op_dt)\n"
                            + "    SELECT ?,?,?, contry_code, dealer, place, foreign_regno, manu_year, current_timestamp \n"
                            + "  FROM va_import_veh where appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, sessionVariables.getStateCodeSelected());
                    ps.setInt(2, sessionVariables.getOffCodeSelected());
                    ps.setString(3, tempRegNo);
                    ps.setString(4, owner_dobj.getAppl_no());

                    ps.executeUpdate();
                }

                //Delete from VA_IMPORT_VEH
                if (!validForTempDelete) {
                    sql = "Delete from " + TableList.VA_IMPORT_VEH + " where appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, owner_dobj.getAppl_no());
                    ps.executeUpdate();
                }

                //Axle Details
                if (axle_dobj != null) {
                    sql = "INSERT INTO vt_axle(\n"
                            + "           state_cd, off_cd, regn_no, f_axle_descp, r_axle_descp, o_axle_descp, \n"
                            + "            t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, t_axle_weight"
                            + ", op_dt,no_of_axles ) \n"
                            + " SELECT ?,?,?, f_axle_descp, r_axle_descp, o_axle_descp, t_axle_descp, \n"
                            + "       f_axle_weight, r_axle_weight, o_axle_weight, t_axle_weight,current_timestamp,no_of_axles \n"
                            + "  FROM va_axle where appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, sessionVariables.getStateCodeSelected());
                    ps.setInt(2, sessionVariables.getOffCodeSelected());
                    ps.setString(3, tempRegNo);
                    ps.setString(4, owner_dobj.getAppl_no());
                    ps.executeUpdate();
                }

                //Delete from VA_AXLE
                if (!validForTempDelete) {
                    sql = "Delete from " + TableList.VA_AXLE + " where appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, owner_dobj.getAppl_no());
                    ps.executeUpdate();
                }


                //CNG Vehicle Details
                if (cng_dobj != null) {
                    sql = "INSERT INTO vt_retrofitting_dtls(\n"
                            + "            state_cd , off_cd , regn_no, kit_srno, kit_type, kit_manuf, kit_pucc_norms, \n"
                            + "            workshop, workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, \n"
                            + "            approval_no, approval_dt, op_dt)\n"
                            + "SELECT ?,?,?, kit_srno, kit_type, kit_manuf, kit_pucc_norms, workshop, \n"
                            + "       workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, approval_no, \n"
                            + "       approval_dt,current_timestamp\n"
                            + "  FROM va_retrofitting_dtls where appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, sessionVariables.getStateCodeSelected());
                    ps.setInt(2, sessionVariables.getOffCodeSelected());
                    ps.setString(3, tempRegNo);
                    ps.setString(4, owner_dobj.getAppl_no());
                    ps.executeUpdate();
                }

                //Delete from VA_RETROFITTING_DTLS
                if (!validForTempDelete) {
                    sql = "Delete from " + TableList.VA_RETROFITTING_DTLS + " where appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, owner_dobj.getAppl_no());
                    ps.executeUpdate();
                }


                if (owner_dobj.getSpeedGovernorDobj() != null) {
                    sql = "update " + TableList.VA_SPEED_GOVERNOR + " set regn_no=?   where appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, tempRegNo);
                    ps.setString(2, owner_dobj.getAppl_no());
                    ps.executeUpdate();
                    FitnessImpl.moveFromVaSpeedGovToVtSpeedGov(appl_no, tmgr);
                    ServerUtil.deleteFromTable(tmgr, null, appl_no, TableList.VA_SPEED_GOVERNOR);
                }

                if (owner_dobj.getInspectionDobj() != null
                        && owner_dobj.getInspectionDobj().getInsp_dt() != null) {
                    FitnessImpl fitImpl = new FitnessImpl();
                    sql = "UPDATE " + TableList.VA_INSPECTION
                            + "  SET regn_no=?"
                            + "  WHERE appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, tempRegNo);
                    ps.setString(2, status_dobj.getAppl_no());
                    ps.executeUpdate();

                    fitImpl.insertVtInspection(tmgr, owner_dobj.getInspectionDobj());
                    ServerUtil.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_INSPECTION);
                }

                sql = "INSERT INTO " + TableList.VA_TEMP_RC_PRINT + " (state_cd,off_cd,appl_no,temp_regn_no,dealer_cd,op_dt) "
                        + " VALUES(?,?,?,?,?,CURRENT_TIMESTAMP)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, sessionVariables.getStateCodeSelected());
                ps.setInt(2, sessionVariables.getOffCodeSelected());
                ps.setString(3, owner_dobj.getAppl_no());
                ps.setString(4, tempRegNo);
                ps.setString(5, owner_dobj.getDealer_cd());
                ps.executeUpdate();

                // for update of entry status as "A" in va_details at time of approval start
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                status_dobj.setRegn_no(tempRegNo);
                updateApprovedStatus(tmgr, status_dobj);
                // for update of entry status as "A" in va_details end
            }


            if (changedData != null && !changedData.equals("")) {
                sql = "INSERT INTO VHA_CHANGED_DATA (APPL_NO,EMP_CD,CHANGED_DATA,OP_DT,STATE_CD,OFF_CD) "
                        + " VALUES(?,?,?,CURRENT_TIMESTAMP,?,?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, owner_dobj.getAppl_no());
                ps.setLong(2, Long.parseLong(sessionVariables.getEmpCodeLoggedIn()));
                ps.setString(3, changedData);
                ps.setString(4, sessionVariables.getStateCodeSelected());
                ps.setInt(5, sessionVariables.getOffCodeSelected());
                ps.executeUpdate();
            }

            ServerUtil.fileFlow(tmgr, status_dobj); // for updateing va_status and vha status for new role,seat for new emp
            tmgr.commit();
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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
        return tempRegNo;
    }//end of update_New_Status method

    public static void updateApprovedStatus(TransactionManager tmgr, Status_dobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "UPDATE " + TableList.VA_DETAILS
                + "   SET  entry_status=?,confirm_ip=?,confirm_date=current_timestamp,regn_no=?"
                + " WHERE  appl_no=? and pur_cd=?";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getEntry_status());
        ps.setString(2, Util.getClientIpAdress());
        ps.setString(3, dobj.getRegn_no());
        ps.setString(4, dobj.getAppl_no());
        ps.setInt(5, dobj.getPur_cd());
        ps.executeUpdate();
    }

    public TempRegDobj getVtTempRegnDtl(String regnNo, String stateCd, int offCd) throws VahanException {
        TempRegDobj dobj = null;
        String sql = "SELECT regn_no, tmp_off_cd, regn_auth, tmp_state_cd, tmp_regn_no, tmp_regn_dt, "
                + "       tmp_valid_upto, dealer_cd "
                + "  FROM " + TableList.VT_TMP_REGN_DTL + " where regn_no=? and state_cd = ? and off_cd = ? ";
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getVtTempRegnDtl");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new TempRegDobj();
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setTmp_off_cd(rs.getInt("tmp_off_cd"));
                dobj.setRegn_auth(rs.getString("regn_auth"));
                dobj.setTmp_state_cd(rs.getString("tmp_state_cd"));
                dobj.setTmp_regn_no(rs.getString("tmp_regn_no"));
                dobj.setTmp_regn_dt(rs.getDate("tmp_regn_dt"));
                dobj.setTmp_valid_upto(rs.getDate("tmp_valid_upto"));
                dobj.setDealer_cd(rs.getString("dealer_cd"));

            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fething of regn_no : " + regnNo + " from table VT_TMP_REGN_DTL ");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        return dobj;
    }

    public static void deleteFromVtTmpRegnDtls(TransactionManager tmgr, Status_dobj dobj, String stateCode, int offCode, String oldRegn) throws SQLException, Exception {
        PreparedStatement ps = null;
        String sql = null;
        sql = "INSERT INTO " + TableList.VH_TMP_REGN_DTL + " (SELECT state_cd, off_cd, regn_no, tmp_off_cd, regn_auth, tmp_state_cd, \n"
                + "       tmp_regn_no, tmp_regn_dt, tmp_valid_upto, dealer_cd,  ? as appl_no , current_timestamp as moved_on, ? as moved_by\n"
                + "  FROM " + TableList.VT_TMP_REGN_DTL + " where regn_no=? and state_cd = ? and off_cd = ? )";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getAppl_no());
        ps.setString(2, String.valueOf(dobj.getEmp_cd()));
        ps.setString(3, oldRegn);
        ps.setString(4, stateCode);
        ps.setInt(5, offCode);

        ps.executeUpdate();

        sql = "Delete from " + TableList.VT_TMP_REGN_DTL + " where regn_no=? and state_cd = ? and off_cd = ?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, oldRegn);
        ps.setString(2, stateCode);
        ps.setInt(3, offCode);

        ps.executeUpdate();
    }

    public static boolean checkApprovalStatusOfTempAppl(String applNo, int firstPurCd, int secondPurCd) throws VahanException {
        boolean vaild = false;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("checkApprovalStatusOfTempAppl");
            vaild = ServerUtil.checkApprovalStatusOfAppls(tmgr, applNo, firstPurCd, secondPurCd);
        } catch (VahanException vex) {
            throw vex;
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
        return vaild;
    }
    
}
