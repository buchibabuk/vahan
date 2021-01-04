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
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.ConvDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.ToDobj;
import nic.vahan.form.dobj.common.Appl_Details_Dobj;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.NewVehicleNo;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC103
 */
public class ConvImpl {

    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(ConvImpl.class);

    public static ConvDobj set_Conversion_appl_db_to_dobj(String appl_no, String regn_no) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        ConvDobj dobj = null;
        VahanException vahanexecption = null;
        String tname = null, pname = null, pvalue = null;

        if (!CommonUtils.isNullOrBlank(appl_no)) {
            appl_no = appl_no.toUpperCase();
            tname = TableList.VA_CONVERSION;
            pname = "appl_no";
            pvalue = appl_no;
        } else if (!CommonUtils.isNullOrBlank(regn_no)) {
            regn_no = regn_no.toUpperCase();
            tname = TableList.VA_CONVERSION;
            pname = "old_regn_no";
            pvalue = regn_no;
        } else {
            vahanexecption = new VahanException("Application detail not found !");
            throw vahanexecption;
        }

        try {
            tmgr = new TransactionManager("set_Conversion_appl_db_to_dobj");
            ps = tmgr.prepareStatement("SELECT appl_no, old_regn_no, new_regn_no, old_vch_class, old_vch_catg, \n"
                    + "       old_fit_dt, new_vch_class, new_vch_catg, new_fit_dt, perm_ref_no, \n"
                    + "       perm_dt, perm_by, tax_paid_upto,excess_tax_amt,new_tax_due_from,tax_mode,new_tax_due_from_flag,other_criteria \n"
                    + "  FROM " + tname + " where " + pname + "=?");
            ps.setString(1, pvalue);

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) {
                dobj = new ConvDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setOld_regn_no(rs.getString("old_regn_no"));
                dobj.setNew_regn_no(rs.getString("new_regn_no"));
                dobj.setOld_vch_class(rs.getInt("old_vch_class"));
                dobj.setOld_vch_catg(rs.getString("old_vch_catg"));
                dobj.setOld_fit_dt(rs.getDate("old_fit_dt"));
                dobj.setNew_vch_class(rs.getInt("new_vch_class"));
                dobj.setNew_vch_catg(rs.getString("new_vch_catg"));
                dobj.setNew_fit_dt(rs.getDate("new_fit_dt"));
                dobj.setPerm_ref_no(rs.getString("perm_ref_no"));
                dobj.setPerm_dt(rs.getDate("perm_dt"));
                dobj.setPerm_by(rs.getString("perm_by"));
                dobj.setNewTaxPaidUpto(rs.getDate("tax_paid_upto"));
                dobj.setExcessAmt(rs.getLong("excess_tax_amt"));
                dobj.setNewTaxDueFrom(rs.getDate("new_tax_due_from"));
                dobj.setNewTaxMode(rs.getString("tax_mode"));
                dobj.setNewTaxDueFromFLag(rs.getString("new_tax_due_from_flag"));
                dobj.setOtherCriteria(rs.getInt("other_criteria"));

                ps = tmgr.prepareStatement("SELECT pmt_type, pmt_catg,service_type, num_generation \n"
                        + "  FROM " + TableList.VA_NUM_GEN_PERMITDETAILS + " where " + pname + "=?");
                ps.setString(1, pvalue);
                RowSet rsPmtType = tmgr.fetchDetachedRowSet_No_release();

                if (rsPmtType.next()) {
                    dobj.setPmt_type(rsPmtType.getInt("pmt_type"));
                    dobj.setPmt_catg(rsPmtType.getInt("pmt_catg"));
                    dobj.setServiceType(rsPmtType.getInt("service_type"));
                    dobj.setNextSeriesGen(rsPmtType.getBoolean("num_generation"));
                }

                String sql = "SELECT * FROM " + TableList.VA_OWNER_OTHER
                        + " where appl_no =?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_no);
                RowSet rsVaOwnrOther = tmgr.fetchDetachedRowSet_No_release();
                if (rsVaOwnrOther.next()) {
                    dobj.setOrdinary_seat(rsVaOwnrOther.getInt("ordinary_seat"));
                    dobj.setPush_bk_seat(rsVaOwnrOther.getInt("push_back_seat"));
                }
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanexecption = new VahanException("Database Error in fetching details for [ " + appl_no + "]");
            throw vahanexecption;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return dobj;
    }

    public void update_Conv_Status(ConvDobj conv_dobj, AxleDetailsDobj axleDobj, Status_dobj status_dobj, String changedData, String axleChangedData, String selectedFancyRetnetion, Owner_dobj ownerDobj, Appl_Details_Dobj sessionVariable) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        RowSet rs = null;
        String sql = null;
        boolean flag = false;
        String regn_no = null;
        boolean regnNumAlloted = false;
        String param = "";
        try {

            if (sessionVariable == null || sessionVariable.getCurrent_off_cd() == 0) {
                throw new VahanException("Something went wrong , Please try after sometime...");
            }
            ToImpl toImpl = new ToImpl();
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            Date applDate = format.parse(status_dobj.getAppl_dt());

            ownerDobj.setVh_class(conv_dobj.getNew_vch_class());
            ownerDobj.setVch_catg(conv_dobj.getNew_vch_catg());
            ownerDobj.setVehType(ServerUtil.VehicleClassType(conv_dobj.getNew_vch_class()));
            VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
            vehParameters.setOLD_VCH_TYPE(ServerUtil.VehicleClassType(conv_dobj.getOld_vch_class()));
            tmgr = new TransactionManager("update_Conv_Status");
            //=================WEB SERVICES FOR NEXTSTAGE START=========//
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);

            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_ENTRY
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_VERIFICATION
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL) {

                if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_ENTRY) {
                    insertUpdateConv(tmgr, conv_dobj.getAppl_no(), conv_dobj.getOld_regn_no(), conv_dobj);
                    ToDobj to_dobj = new ToDobj();
                    if (selectedFancyRetnetion != null && selectedFancyRetnetion.equalsIgnoreCase("YES")) {
                        to_dobj.setAppl_no(conv_dobj.getAppl_no());
                        to_dobj.setRegn_no(conv_dobj.getOld_regn_no());
                        to_dobj.setReason("Vehicle Conversion");
                        toImpl.insertIntoVaSurrenderRetention(tmgr, to_dobj, applDate);
                    }
                    if (axleDobj != null) {
                        AxleImpl.saveAxleDetails_Impl(axleDobj, conv_dobj.getAppl_no(), tmgr);
                    }
                    if (conv_dobj.getAssignRetainRegnNo() != null) {
                        if (conv_dobj.isAssignRetainNo()) {
                            toImpl.updateRetentionRegNoDetails(conv_dobj.getAppl_no(), conv_dobj.getAssignRetainRegnNo(), tmgr);
                        }
                    } else if (conv_dobj.getAssignFancyRegnNumber() != null) {
                        if (conv_dobj.isAssignFancyNumber()) {
                            Rereg_Impl.updateAdvanceRegNoDetails(conv_dobj.getAppl_no(), conv_dobj.getAssignFancyRegnNumber(), tmgr);
                        }
                    }
                    if ("HP,CH,RJ".contains(sessionVariable.getCurrent_state_cd())) {
                        insertUpdateVaNumGenPermit(tmgr, conv_dobj.getAppl_no(), conv_dobj.getOld_regn_no(), conv_dobj, sessionVariable);
                    }
                    if (Util.getUserStateCode().equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(ownerDobj.getVh_class()) + ",") && (conv_dobj.getPush_bk_seat() > 0 || conv_dobj.getOrdinary_seat() > 0)) {
                        ownerDobj.setAppl_no(conv_dobj.getAppl_no());
                        ownerDobj.setPush_bk_seat(conv_dobj.getPush_bk_seat());
                        ownerDobj.setOrdinary_seat(conv_dobj.getOrdinary_seat());
                        NewImpl.insertOrUpdateVaOwnerOther(tmgr, ownerDobj);
                    }
                } else {
                    if (!changedData.isEmpty()) {
                        insertUpdateConv(tmgr, conv_dobj.getAppl_no(), conv_dobj.getOld_regn_no(), conv_dobj);
                    }
                    if (axleDobj != null && !axleChangedData.isEmpty()) {
                        AxleImpl.saveAxleDetails_Impl(axleDobj, conv_dobj.getAppl_no(), tmgr);
                    }
                }

                if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_VERIFICATION) {
                    String purCode = toImpl.statusList(conv_dobj.getAppl_no(), conv_dobj.getOld_regn_no(), sessionVariable.getCurrent_state_cd());
                    if (purCode.contains("," + TableConstants.VM_TRANSACTION_MAST_TO + ",") && purCode.contains("," + TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION + ",")) {
                        if (conv_dobj.isAssignRetainNo() || conv_dobj.isAssignFancyNumber()) {
                            toImpl.insertVaStopDupRegnNoGen(conv_dobj.getAppl_no(), conv_dobj.getOld_regn_no(), conv_dobj.getNew_regn_no(), TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION, tmgr);
                        }
                    }
                }
            }

            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                //For Checking if Other Application is Pending for Approval Before Conversion Approval
                List<Status_dobj> statusList = ServerUtil.applicationStatus(sessionVariable.getRegn_no(), sessionVariable.getAppl_no(), sessionVariable.getCurrent_state_cd());
                if (!statusList.isEmpty() && statusList.size() > 1) {
                    for (int i = 0; i < statusList.size(); i++) {
                        if (statusList.get(i).getPur_cd() != TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION && statusList.get(i).getPur_cd() != TableConstants.VM_TRANSACTION_MAST_NOC) {
                            throw new VahanException("Other transaction is Pending for this Application No, First Approve that Application Before Approving Conversion.");
                        }
                    }
                }
//               ServerUtil.checkApprovalStatusOfAppls(tmgr, conv_dobj.getAppl_no(), TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION, TableConstants.VM_TRANSACTION_MAST_FIT_CERT);
//               ServerUtil.checkApprovalStatusOfAppls(tmgr, conv_dobj.getAppl_no(), TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION, TableConstants.VM_TRANSACTION_MAST_TO);
                if (ServerUtil.VehicleClassType(conv_dobj.getOld_vch_class()) == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                    FitnessImpl fitImpl = new FitnessImpl();
                    FitnessDobj fitnessDobj = fitImpl.set_Fitness_appl_db_to_dobj(conv_dobj.getOld_regn_no(), null);
                    if (fitnessDobj != null && fitnessDobj.getFit_result() != null && fitnessDobj.getFit_result().equalsIgnoreCase(TableConstants.FitnessResultFail)) {
                        throw new VahanException("This Application is not perform Due to fitness Fail.");
                    }
                }
                //Approve Insurance
                InsImpl.approvalInsurance(tmgr, conv_dobj.getOld_regn_no(), sessionVariable.getCurrent_state_cd(), sessionVariable.getCurrent_off_cd(), sessionVariable.getCurrentEmpCd());

                sql = "select a.* from vt_tax a, vp_appl_rcpt_mapping  b where  a.rcpt_no=b.rcpt_no and a.state_cd=b.state_cd and "
                        + "a.off_cd=b.off_cd and b.appl_no=? and  a.tax_mode <> 'B'";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, conv_dobj.getAppl_no());
                rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    String regno = rs.getString("regn_no");
                    Date taxUpto = rs.getDate("tax_upto");
                    int purCd = rs.getInt("pur_cd");
                    TaxServer_Impl.insertUpdateTaxDefaulter(regno, taxUpto, purCd, tmgr);
                }

                TmConfigurationDobj configDobj = Util.getTmConfiguration();
                flag = checkReAssignRegnFee(conv_dobj);
                if (configDobj != null) {
                    if (configDobj.getRegn_gen_type().equalsIgnoreCase(TableConstants.NO_GEN_TYPE_MIX_P) && flag) {
                        regn_no = ServerUtil.getRegnNoAllotedDetail(conv_dobj.getAppl_no(), ownerDobj.getState_cd(), ownerDobj.getOff_cd());
                        if (!CommonUtils.isNullOrBlank(regn_no)) {
                            regnNumAlloted = true;
                        }
                    } else {
                        if ("HP,CH,RJ".contains(sessionVariable.getCurrent_state_cd()) && !conv_dobj.isNextSeriesGen()) {
                            regn_no = sessionVariable.getRegn_no();
                        } else {
                            if (flag && !conv_dobj.isAssignRetainNo() && !conv_dobj.isAssignFancyNumber()) {
                                NewVehicleNo newVehicleNo = new NewVehicleNo();
                                regn_no = newVehicleNo.generateAssignNewRegistrationNo(sessionVariable.getCurrent_off_cd(), sessionVariable.getCurrent_action_cd(), conv_dobj.getAppl_no(), null, 1, null, vehParameters, tmgr);
                            }
                        }
                        if (conv_dobj.isAssignRetainNo()) {
                            regn_no = conv_dobj.getAssignRetainRegnNo();
                        }
                        if (conv_dobj.isAssignFancyNumber()) {
                            regn_no = conv_dobj.getAssignFancyRegnNumber();
                        }
                        if (selectedFancyRetnetion != null && (selectedFancyRetnetion.equalsIgnoreCase("YES") || selectedFancyRetnetion.equalsIgnoreCase("NO"))) {
                            toImpl.insertIntoVhaSurrenderRetention(tmgr, status_dobj.getAppl_no());
                            if (selectedFancyRetnetion.equalsIgnoreCase("YES")) {
                                NewVehicleNo newVehicleNo = new NewVehicleNo();
                                regn_no = newVehicleNo.generateAssignNewRegistrationNo(sessionVariable.getCurrent_off_cd(), sessionVariable.getCurrent_action_cd(), conv_dobj.getAppl_no(), null, 1, null, vehParameters, tmgr);
                                //regn_no = toImpl.insertIntoVtSurrenderRetention(tmgr, ownerDobj, "Conversion", status_dobj.getAppl_no(), applDate, TableConstants.VM_TRANSACTION_MAST_NOC, false);
                                insertIntovtRetentionRegNoDetails(tmgr, ownerDobj, "Conversion", status_dobj.getAppl_no(), applDate, TableConstants.VM_TRANSACTION_MAST_NOC, false, regn_no);
                                ServerUtil.deleteFromTable(tmgr, null, conv_dobj.getAppl_no(), TableList.VA_SURRENDER_RETENTION);
                            }
                        }
                    }
                }

                if (!CommonUtils.isNullOrBlank(regn_no)) {
                    conv_dobj.setNew_regn_no(regn_no);
                } else {
                    conv_dobj.setNew_regn_no(conv_dobj.getOld_regn_no());
                }
                TmConfigurationDobj tmConfig = Util.getTmConfiguration();
                boolean NumGenrateWithTOReRegCon = toImpl.isNumGenrateWithTOReRegCon(conv_dobj.getAppl_no(), TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION);
                if (!(tmConfig.isHold_regnNo_with_conversion() && (toImpl.isFancyNo(sessionVariable.getRegn_no()) || tmConfig.isTo_retention_for_all_regn()))) {
                    if (NumGenrateWithTOReRegCon) {
                        if (isNumRetentionWithTOConv(sessionVariable.getAppl_no(), sessionVariable.getCurrent_off_cd(), sessionVariable.getCurrent_state_cd())) {
                            if (CommonUtils.isNullOrBlank(conv_dobj.getNew_regn_no())) {
                                NewVehicleNo newVehicleNo = new NewVehicleNo();
                                regn_no = newVehicleNo.generateAssignNewRegistrationNo(sessionVariable.getCurrent_off_cd(), sessionVariable.getCurrent_action_cd(), conv_dobj.getAppl_no(), null, 1, null, vehParameters, tmgr);
                                if (!CommonUtils.isNullOrBlank(regn_no)) {
                                    conv_dobj.setNew_regn_no(regn_no);
                                }
                            }
                            toImpl.insertIntoVtSurrenderRetention(sessionVariable.getOwnerDobj(), status_dobj.getAppl_no(), "toWithRetention", applDate, conv_dobj.getNew_regn_no(), conv_dobj.getOld_regn_no(), tmgr);
                        }
                    }
                }
                ToImpl.insertIntoVhaVaStopDupRegnNoHistory(tmgr, conv_dobj.getAppl_no(), sessionVariable);
                if ("HP,CH,RJ".contains(sessionVariable.getCurrent_state_cd())) {
                    updateVaNumGenPermit(tmgr, conv_dobj);
                    insertIntoVhaNumGenPermitHistory(tmgr, conv_dobj.getAppl_no(), sessionVariable);
                    ServerUtil.deleteFromTable(tmgr, "", conv_dobj.getAppl_no(), TableList.VA_NUM_GEN_PERMITDETAILS);
                }
                //Approval of Axle Details
                if (axleDobj != null) {
                    status_dobj.setRegn_no(conv_dobj.getNew_regn_no());
                    //Insert into vh_retrofitting_details from vt_retrofitting_details
                    AxleImpl.insertIntoAxleVH(tmgr, status_dobj, sessionVariable.getCurrent_state_cd(), sessionVariable.getCurrent_off_cd(), conv_dobj.getOld_regn_no());
                    //Delete from vt_retrofitting _details
                    AxleImpl.deleteFromVtAxle(tmgr, conv_dobj.getOld_regn_no().trim(), sessionVariable.getCurrent_state_cd(), sessionVariable.getCurrent_off_cd());
                    //Insert into vt_retrofitting_detials from va_retrofitting_details
                    AxleImpl.insertIntoVtFromVaAxle(tmgr, status_dobj);
                    //Insert into vha_retrofitting_detail from va_retrofitting_detail
                    AxleImpl.insertIntoVhaAxle(tmgr, conv_dobj.getAppl_no());
                    //Delete from va_retrofitting_detail
                    AxleImpl.deleteFromVaAxle(tmgr, conv_dobj.getAppl_no());
                }

                //Update vh_re_assign
                if (!CommonUtils.isNullOrBlank(regn_no)) {
                    String query = "Select appl_no from " + TableList.VH_RE_ASSIGN + " where state_cd = ? and off_cd =? and appl_no = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setString(1, sessionVariable.getCurrent_state_cd());
                    ps.setInt(2, sessionVariable.getCurrent_off_cd());
                    ps.setString(3, conv_dobj.getAppl_no());
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (!rs.next()) {
                        sql = "INSERT INTO " + TableList.VH_RE_ASSIGN + "(\n"
                                + "            state_cd, off_cd, appl_no, old_regn_no, new_regn_no, reason, \n"
                                + "            moved_on, moved_by)\n"
                                + "    VALUES (?, ?, ?, ?, ?, ?, \n"
                                + "            current_timestamp, ?)";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, sessionVariable.getCurrent_state_cd());
                        ps.setInt(2, sessionVariable.getCurrent_off_cd());
                        ps.setString(3, conv_dobj.getAppl_no());
                        ps.setString(4, conv_dobj.getOld_regn_no());
                        ps.setString(5, conv_dobj.getNew_regn_no());
                        ps.setString(6, conv_dobj.getPerm_by());
                        ps.setString(7, sessionVariable.getCurrentEmpCd());
                        ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                    }
                }

                //Update Tax deatails 
                if (conv_dobj.getNewTaxPaidUpto() != null) {
                    sql = "INSERT INTO " + TableList.VH_TAX_CLEAR + "(\n"
                            + "            state_cd, off_cd, appl_no, regn_no, pur_cd, clear_fr, clear_to, \n"
                            + "            tcr_no, iss_auth, iss_dt, remark, user_cd, op_dt, moved_on, moved_by)\n"
                            + "    SELECT state_cd, off_cd, appl_no, regn_no, pur_cd, clear_fr, clear_to, \n"
                            + "       tcr_no, iss_auth, iss_dt, remark, user_cd, op_dt,current_timestamp as moved_on,? as moved_by\n"
                            + "  FROM " + TableList.VT_TAX_CLEAR + " WHERE regn_no = ? and state_cd = ? and off_cd = ? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, sessionVariable.getCurrentEmpCd());
                    ps.setString(2, conv_dobj.getOld_regn_no());
                    ps.setString(3, sessionVariable.getCurrent_state_cd());
                    ps.setInt(4, sessionVariable.getCurrent_off_cd());
                    ps.executeUpdate();

                    sql = "Delete from " + TableList.VT_TAX_CLEAR + " where regn_no = ? and state_cd = ? and off_cd = ? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, conv_dobj.getOld_regn_no());
                    ps.setString(2, sessionVariable.getCurrent_state_cd());
                    ps.setInt(3, sessionVariable.getCurrent_off_cd());
                    ps.executeUpdate();

                    sql = "INSERT INTO " + TableList.VT_TAX_CLEAR + "(\n"
                            + "            state_cd, off_cd, appl_no, regn_no, pur_cd, clear_fr, clear_to, \n"
                            + "            tcr_no, iss_auth, iss_dt, remark, user_cd, op_dt)\n"
                            + "    VALUES (?, ?, ?, ?, ?, ?, ?, \n"
                            + "            ?, ?, current_timestamp, ?, ?, current_timestamp)";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, sessionVariable.getCurrent_state_cd());
                    ps.setInt(2, sessionVariable.getCurrent_off_cd());
                    ps.setString(3, conv_dobj.getAppl_no());
                    ps.setString(4, conv_dobj.getNew_regn_no());
                    ps.setInt(5, TableConstants.TM_ROAD_TAX);
                    ps.setDate(6, new java.sql.Date(conv_dobj.getPurchaseDate().getTime()));
                    if (conv_dobj.getNewTaxPaidUpto() != null) {
                        ps.setDate(7, new java.sql.Date(conv_dobj.getNewTaxPaidUpto().getTime()));
                    } else {
                        ps.setNull(7, java.sql.Types.NULL);
                    }
                    ps.setString(8, "Conversion");
                    ps.setString(9, sessionVariable.getCurrent_seat_cd());
                    ps.setString(10, "Conversion Tax Entry");
                    ps.setString(11, sessionVariable.getCurrentEmpCd());
                    ps.executeUpdate();

                    if (Util.getUserStateCode().equalsIgnoreCase("JH") && conv_dobj.getNew_vch_class() >= 51) {
                        String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
                        for (int i = 0; i < data.length; i++) {

                            if (data[i][0].equalsIgnoreCase(Integer.toString(conv_dobj.getNew_vch_class()))) {
                                if (data[i][3].equalsIgnoreCase("P") || data[i][3].equalsIgnoreCase("G")) {

                                    sql = "INSERT INTO " + TableList.VT_TAX_CLEAR + "(\n"
                                            + "            state_cd, off_cd, appl_no, regn_no, pur_cd, clear_fr, clear_to, \n"
                                            + "            tcr_no, iss_auth, iss_dt, remark, user_cd, op_dt)\n"
                                            + "    VALUES (?, ?, ?, ?, ?, ?, ?, \n"
                                            + "            ?, ?, current_timestamp, ?, ?, current_timestamp)";
                                    ps = tmgr.prepareStatement(sql);
                                    ps.setString(1, sessionVariable.getCurrent_state_cd());
                                    ps.setInt(2, sessionVariable.getCurrent_off_cd());
                                    ps.setString(3, conv_dobj.getAppl_no());
                                    ps.setString(4, conv_dobj.getNew_regn_no());
                                    ps.setInt(5, TableConstants.TM_ADDN_ROAD_TAX);
                                    ps.setDate(6, new java.sql.Date(conv_dobj.getPurchaseDate().getTime()));
                                    if (conv_dobj.getNewTaxPaidUpto() != null) {
                                        ps.setDate(7, new java.sql.Date(conv_dobj.getNewTaxPaidUpto().getTime()));
                                    } else {
                                        ps.setNull(7, java.sql.Types.NULL);
                                    }
                                    ps.setString(8, "Conversion");
                                    ps.setString(9, sessionVariable.getCurrent_seat_cd());
                                    ps.setString(10, "Conversion Tax Entry");
                                    ps.setString(11, sessionVariable.getCurrentEmpCd());
                                    ps.executeUpdate();
                                }
                            }
                        }
                    }
                }
                //Update va_conversion with new assigned regn_no(if applicable) otherwise with same new_regn_no as old_regn_no.
                sql = "UPDATE " + TableList.VA_CONVERSION + " SET new_regn_no = ?, op_dt = current_timestamp WHERE appl_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, conv_dobj.getNew_regn_no());
                ps.setString(2, conv_dobj.getAppl_no());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                //inserting data into vh_conversion from vt_owner
                sql = "INSERT INTO " + TableList.VH_CONVERSION + "(\n"
                        + "            state_cd, off_cd, appl_no, old_regn_no, new_regn_no, old_vch_class, \n"
                        + "            old_vch_catg, old_fit_dt, new_vch_class, new_vch_catg, new_fit_dt, \n"
                        + "            perm_ref_no, perm_dt, perm_by, moved_on, moved_by)\n"
                        + "	Select ? as state_cd, ? as off_cd, ? as appl_no, vt.regn_no, va.new_regn_no, vt.vh_class, vt.vch_catg,"
                        + " vt.fit_upto, va.new_vch_class, va.new_vch_catg, va.new_fit_dt, va.perm_ref_no,\n"
                        + "	va.perm_dt, va.perm_by, current_timestamp as moved_on, ? as moved_by \n"
                        + "from " + TableList.VT_OWNER + " vt\n"
                        + "inner join " + TableList.VA_CONVERSION + " va on vt.regn_no=va.old_regn_no where va.appl_no = ? and  vt.regn_no=?"
                        + " and vt.state_cd = ? and vt.off_cd = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, sessionVariable.getCurrent_state_cd());
                ps.setInt(2, Util.getSelectedSeat().getOff_cd());
                ps.setString(3, conv_dobj.getAppl_no());
                ps.setString(4, sessionVariable.getCurrentEmpCd());
                ps.setString(5, conv_dobj.getAppl_no());
                ps.setString(6, conv_dobj.getOld_regn_no());
                ps.setString(7, sessionVariable.getCurrent_state_cd());
                ps.setInt(8, sessionVariable.getCurrent_off_cd());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                sql = "Select appl_no from " + TableList.VA_DETAILS + " where appl_no = ? and pur_cd = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, conv_dobj.getAppl_no());
                ps.setInt(2, TableConstants.VM_TRANSACTION_MAST_FIT_CERT);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (!rs.next()) {
                    //Moving fitness data to history
                    FitnessImpl.moveFromVtFitnessToVhFitness(conv_dobj.getAppl_no(), conv_dobj.getOld_regn_no(), sessionVariable.getCurrent_state_cd(), sessionVariable.getCurrent_off_cd(), tmgr);
                }

                //updation of vt_owner                
                if (!ServerUtil.isTransport(conv_dobj.getNew_vch_class(), null)) {//Private Vehicle
                    ApplicationInwardImpl appInwrdImpl = new ApplicationInwardImpl();
                    TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
                    if (tmConfigurationDobj == null) {
                        tmConfigurationDobj = ServerUtil.getTmConfigurationParameters(status_dobj.getState_cd());
                    }

                    sql = "UPDATE " + TableList.VT_OWNER + " SET regn_no=?, vh_class=?,vch_catg=?,regn_upto=?,fit_upto=?,other_criteria=?, op_dt=? WHERE regn_no=?"
                            + " and state_cd = ? and off_cd = ? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, conv_dobj.getNew_regn_no());
                    ps.setInt(2, conv_dobj.getNew_vch_class());
                    ps.setString(3, conv_dobj.getNew_vch_catg());

                    if (appInwrdImpl.getFitnessRequiredFor(ownerDobj, tmConfigurationDobj) && !Util.getUserStateCode().equalsIgnoreCase("JH")) {
                        ps.setDate(4, new java.sql.Date(conv_dobj.getNew_fit_dt().getTime()));
                        ps.setDate(5, new java.sql.Date((ServerUtil.dateRange(new Date(), 0, 0, -1)).getTime()));
                    } else {
                        ps.setDate(4, new java.sql.Date(conv_dobj.getNew_fit_dt().getTime()));
                        ps.setDate(5, new java.sql.Date(conv_dobj.getNew_fit_dt().getTime()));
                    }
                    ps.setInt(6, conv_dobj.getOtherCriteria());
                    ps.setDate(7, new java.sql.Date(new java.util.Date().getTime()));
                    ps.setString(8, conv_dobj.getOld_regn_no());
                    ps.setString(9, sessionVariable.getCurrent_state_cd());
                    ps.setInt(10, sessionVariable.getCurrent_off_cd());

                } else {//Commercial Vehicle
                    sql = "UPDATE " + TableList.VT_OWNER + " SET regn_no=?, vh_class=?,vch_catg=?,other_criteria=? ,op_dt=? WHERE regn_no=? "
                            + "and state_cd = ? and off_cd = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, conv_dobj.getNew_regn_no());
                    ps.setInt(2, conv_dobj.getNew_vch_class());
                    ps.setString(3, conv_dobj.getNew_vch_catg());
                    ps.setInt(4, conv_dobj.getOtherCriteria());
                    ps.setDate(5, new java.sql.Date(new java.util.Date().getTime()));
                    ps.setString(6, conv_dobj.getOld_regn_no());
                    ps.setString(7, sessionVariable.getCurrent_state_cd());
                    ps.setInt(8, sessionVariable.getCurrent_off_cd());
                }
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                if (Util.getUserStateCode().equals("KL")) {
                    NewImpl newImpl = new NewImpl();
                    if (TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(ownerDobj.getVh_class()) + ",")) {
                        ownerDobj.setAppl_no(conv_dobj.getAppl_no());
                        ownerDobj.setPush_bk_seat(conv_dobj.getPush_bk_seat());
                        ownerDobj.setOrdinary_seat(conv_dobj.getOrdinary_seat());
                        newImpl.ownerOtherApproval(tmgr, ownerDobj);
                    } else {
                        newImpl.moveFromVtToVhOwnerOther(tmgr, ownerDobj);
                    }
                }
                //insert data into vha_conversion from va_conversion with 1 Second interval
                sql = "INSERT INTO " + TableList.VHA_CONVERSION + "(\n"
                        + "            moved_on, moved_by,state_cd, off_cd, appl_no, old_regn_no, new_regn_no, old_vch_class, old_vch_catg, \n"
                        + "            old_fit_dt, new_vch_class, new_vch_catg, new_fit_dt, perm_ref_no, \n"
                        + "            perm_dt, perm_by, tax_paid_upto, excess_tax_amt,new_tax_due_from,tax_mode,new_tax_due_from_flag,other_criteria,op_dt) \n"
                        + "SELECT current_timestamp + interval '1 second' as moved_on, ? as moved_by, state_cd, off_cd, appl_no, old_regn_no, new_regn_no, old_vch_class, old_vch_catg, \n"
                        + "       old_fit_dt, new_vch_class, new_vch_catg, new_fit_dt, perm_ref_no, \n"
                        + "       perm_dt, perm_by, tax_paid_upto,"
                        + " excess_tax_amt,new_tax_due_from,tax_mode,new_tax_due_from_flag,other_criteria,op_dt \n"
                        + "  FROM " + TableList.VA_CONVERSION + " where appl_no=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, sessionVariable.getCurrentEmpCd());
                ps.setString(2, conv_dobj.getAppl_no());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                //delete from va_conversion
                sql = "Delete from " + TableList.VA_CONVERSION + " where appl_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, conv_dobj.getAppl_no());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                insertIntoVhaAndDeleteVaInwrdConversion(tmgr, conv_dobj.getAppl_no(), sessionVariable.getCurrentEmpCd());
                //Update new vehicle number in corresponding tables
                if (!CommonUtils.isNullOrBlank(regn_no)) {
                    // for updating va_details for newreg generation
                    sql = "update " + TableList.VA_DETAILS + " set regn_no=? where appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, conv_dobj.getNew_regn_no());
                    ps.setString(2, conv_dobj.getAppl_no());
                    ps.executeUpdate();

                    sql = "update " + TableList.VA_FC_PRINT + " set regn_no=? where regn_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, conv_dobj.getNew_regn_no());
                    ps.setString(2, conv_dobj.getOld_regn_no());
                    ps.executeUpdate();

                    updateTablesOnRegnNoChange(tmgr, conv_dobj.getOld_regn_no(), conv_dobj.getNew_regn_no(), conv_dobj.getAppl_no());
                }
                //data insert for fastag schedular
                if (axleDobj != null || !CommonUtils.isNullOrBlank(regn_no)) {
                    OwnerImpl.insertUpdateFastagSchedular(conv_dobj.getAppl_no(), sessionVariable.getCurrent_state_cd(), sessionVariable.getCurrent_off_cd(), conv_dobj.getOld_regn_no(),
                            sessionVariable.getCurrent_state_cd(), sessionVariable.getCurrent_off_cd(), conv_dobj.getNew_regn_no(), sessionVariable.getChasi_no(), tmgr);
                }
                //for updating the status of application when it is approved start
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                //for updating the status of application when it is approved end

                //For New Vehicle HSRP Details
                if ("WB,HR".contains(sessionVariable.getCurrent_state_cd())
                        || ("RJ".contains(sessionVariable.getCurrent_state_cd()) && !",2WN,2WT,".contains(conv_dobj.getNew_vch_catg()))) {
                    if (!ServerUtil.verifyForOldVehicleHsrp(sessionVariable.getCurrent_state_cd(), sessionVariable.getCurrent_off_cd(), tmgr)) {
                        ServerUtil.verifyInsertNewRegHsrpDetail(conv_dobj.getAppl_no(), conv_dobj.getNew_regn_no(), TableConstants.HSRP_NEW_BOTH_SIDE,
                                sessionVariable.getCurrent_state_cd(), sessionVariable.getCurrent_off_cd(), tmgr);
                    }
                }

                //SmartCard Or Print
                ServerUtil.VerifyInsertSmartCardPrintDetail(conv_dobj.getAppl_no(), conv_dobj.getNew_regn_no(),
                        sessionVariable.getCurrent_state_cd(), sessionVariable.getCurrent_off_cd(), status_dobj.getPur_cd(), tmgr);
            }

            ServerUtil.insertIntoVhaChangedData(tmgr, conv_dobj.getAppl_no(), changedData + axleChangedData);//for saving the data into table those are changed by the user

            ServerUtil.fileFlow(tmgr, status_dobj); //for updating va_status and vha status for new role,seat for new emp

            tmgr.commit();//Commiting data here....

        } catch (VahanException ve) {
            throw ve;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Conversion Application : Error in Database Update");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Update Error in Conversion Application");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
    }//End of Conversion_Update_Status()
//InsertUpdate Conversion

    public static void insertUpdateConv(TransactionManager tmgr, String appl_no, String regn_no, ConvDobj conv_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {

            sql = "SELECT old_regn_no FROM " + TableList.VA_CONVERSION + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) { //if any record is exist then update otherwise insert it
                insertIntoConversionHistory(tmgr, appl_no);
                updateConversion(tmgr, conv_dobj);
            } else {
                insertIntoConversion(tmgr, conv_dobj);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        }
    }

    public static void insertIntoConversionHistory(TransactionManager tmgr, String appl_no) {
        PreparedStatement ps = null;
        String sql = null;
        try {
            //inserting data into vha_conversion from va_conversion
            sql = "INSERT INTO " + TableList.VHA_CONVERSION + "(\n"
                    + "            moved_on, moved_by, state_cd, off_cd, appl_no, old_regn_no, new_regn_no, old_vch_class, old_vch_catg, \n"
                    + "            old_fit_dt, new_vch_class, new_vch_catg, new_fit_dt, perm_ref_no, \n"
                    + "            perm_dt, perm_by,  tax_paid_upto, excess_tax_amt,new_tax_due_from,tax_mode,new_tax_due_from_flag,other_criteria,op_dt) \n"
                    + "SELECT current_timestamp as moved_on, ? as moved_by, state_cd, off_cd, appl_no, old_regn_no, new_regn_no, old_vch_class, old_vch_catg, \n"
                    + "       old_fit_dt, new_vch_class, new_vch_catg, new_fit_dt, perm_ref_no, \n"
                    + "       perm_dt, perm_by, tax_paid_upto, excess_tax_amt,new_tax_due_from,tax_mode,new_tax_due_from_flag,other_criteria,op_dt \n"
                    + "  FROM " + TableList.VA_CONVERSION + " where appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public static void updateConversion(TransactionManager tmgr, ConvDobj dobj) {
        PreparedStatement ps = null;
        String sql = null;
        try {
            //updation of va_conversion
            sql = "UPDATE " + TableList.VA_CONVERSION + "\n"
                    + "   SET  old_regn_no=?, new_regn_no=?, old_vch_class=?, old_vch_catg=?, \n"
                    + "       old_fit_dt=?, new_vch_class=?, new_vch_catg=?, new_fit_dt=?, \n"
                    + "       perm_ref_no=?, perm_dt=?, perm_by=?, tax_paid_upto=?, excess_tax_amt=?,new_tax_due_from=?,tax_mode=?,new_tax_due_from_flag=?,other_criteria=?,op_dt = current_timestamp\n"
                    + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            //ps statement
            ps.setString(1, dobj.getOld_regn_no());
            ps.setString(2, dobj.getNew_regn_no());
            ps.setInt(3, dobj.getOld_vch_class());
            ps.setString(4, dobj.getOld_vch_catg());
            ps.setDate(5, new java.sql.Date(dobj.getOld_fit_dt().getTime()));
            ps.setInt(6, dobj.getNew_vch_class());
            ps.setString(7, dobj.getNew_vch_catg());
            ps.setDate(8, new java.sql.Date(dobj.getNew_fit_dt().getTime()));
            ps.setString(9, dobj.getPerm_ref_no());
            ps.setDate(10, new java.sql.Date(dobj.getPerm_dt().getTime()));
            ps.setString(11, dobj.getPerm_by());
            if (dobj.getNewTaxPaidUpto() != null) {
                ps.setDate(12, new java.sql.Date(dobj.getNewTaxPaidUpto().getTime()));
            } else {
                ps.setNull(12, java.sql.Types.NULL);
            }
            ps.setLong(13, dobj.getExcessAmt());
            if (dobj.getNewTaxDueFromFLag().equalsIgnoreCase("U")) {
                ps.setDate(14, new java.sql.Date(dobj.getNewTaxPaidUpto().getTime()));
            } else {
                ps.setDate(14, new java.sql.Date(dobj.getNewTaxDueFrom().getTime()));
            }
            ps.setString(15, dobj.getNewTaxMode());
            ps.setString(16, dobj.getNewTaxDueFromFLag());
            ps.setInt(17, dobj.getOtherCriteria());
            ps.setString(18, dobj.getAppl_no());

            ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }
    //Insert into va_conversion

    public static void insertIntoConversion(TransactionManager tmgr, ConvDobj dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        int i = 1;
        try {
            sql = "INSERT INTO " + TableList.VA_CONVERSION + " (\n"
                    + "            state_cd, off_cd, appl_no, old_regn_no, new_regn_no, old_vch_class, old_vch_catg, \n"
                    + "            old_fit_dt, new_vch_class, new_vch_catg, new_fit_dt, perm_ref_no, \n"
                    + "            perm_dt, perm_by,tax_paid_upto,excess_tax_amt,new_tax_due_from,tax_mode,new_tax_due_from_flag,other_criteria,op_dt)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?,?,?,?,?,?,?,current_timestamp)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.setString(i++, dobj.getAppl_no());
            ps.setString(i++, dobj.getOld_regn_no());
            ps.setString(i++, dobj.getNew_regn_no());
            ps.setInt(i++, dobj.getOld_vch_class());
            ps.setString(i++, dobj.getOld_vch_catg());
            ps.setDate(i++, new java.sql.Date(dobj.getOld_fit_dt().getTime()));
            ps.setInt(i++, dobj.getNew_vch_class());
            ps.setString(i++, dobj.getNew_vch_catg());
            ps.setDate(i++, new java.sql.Date(dobj.getNew_fit_dt().getTime()));
            ps.setString(i++, dobj.getPerm_ref_no());
            ps.setDate(i++, new java.sql.Date(dobj.getPerm_dt().getTime()));
            ps.setString(i++, dobj.getPerm_by());
            if (dobj.getNewTaxPaidUpto() != null) {
                ps.setDate(i++, new java.sql.Date(dobj.getNewTaxPaidUpto().getTime()));
            } else {
                ps.setNull(i++, java.sql.Types.NULL);
            }
            ps.setLong(i++, dobj.getExcessAmt());
            if (dobj.getNewTaxDueFrom() != null) {
                ps.setDate(i++, new java.sql.Date(dobj.getNewTaxDueFrom().getTime()));
            } else {
                ps.setDate(i++, new java.sql.Date(dobj.getNewTaxPaidUpto().getTime()));
            }
            ps.setString(i++, dobj.getNewTaxMode());
            ps.setString(i++, dobj.getNewTaxDueFromFLag());
            ps.setInt(i++, dobj.getOtherCriteria());

            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        }
    }

    public static void makeChangeConv(ConvDobj conv_dobj, AxleDetailsDobj axleDobj, String changedata, String axleChangedData, String selectedFancyRetnetion, Date applDate) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("makeChangeConv");
            insertUpdateConv(tmgr, conv_dobj.getAppl_no(), conv_dobj.getOld_regn_no(), conv_dobj);
            if (selectedFancyRetnetion != null && selectedFancyRetnetion.equalsIgnoreCase("YES")) {
                ToDobj to_dobj = new ToDobj();
                ToImpl toImpl = new ToImpl();
                to_dobj.setAppl_no(conv_dobj.getAppl_no());
                to_dobj.setRegn_no(conv_dobj.getOld_regn_no());
                to_dobj.setReason("Vehicle Conversion");
                toImpl.insertIntoVaSurrenderRetention(tmgr, to_dobj, applDate);
            }
            if (!axleChangedData.isEmpty() || axleDobj != null) {
                AxleImpl.saveAxleDetails_Impl(axleDobj, conv_dobj.getAppl_no(), tmgr);
            }
            ComparisonBeanImpl.updateChangedData(conv_dobj.getAppl_no(), changedata + axleChangedData, tmgr);
            tmgr.commit();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
    }

    public static Object[] present_technicalDetail(String regn_no) {
        Object current_technical_detail[] = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("present_c_addressDetails");
            String sql = "select vh_class,vch_catg,tax_mode,tax_amt,tax_from,tax_upto, 'Vehicle Class ['||vh_class_desc||'], Vehicle Category ['||vch_catg||'], Fitment Date Upto ['||to_char(fit_upto,'dd-MON-YYYY')||'],Tax Mode ['||tax_mode||'],Tax From ['||to_char(tax_from,'dd-MON-YYYY')||'],Tax Paid Upto ['||to_char(tax_upto,'dd-MON-YYYY')||'], Tax Amount [Rs.'||tax_amt||'/-]' as current_technical_detail\n"
                    + "  from vv_owner inner join vt_tax on vv_owner.regn_no=vt_tax.regn_no and vv_owner.state_cd = vt_tax.state_cd \n"
                    + "  where vv_owner.regn_no=? and rcpt_dt = (Select max(rcpt_dt) from vt_tax where regn_no = ? and vv_owner.state_cd = vt_tax.state_cd )"
                    + " and vv_owner.state_cd =? and vt_tax.pur_cd=58 order by vt_tax.rcpt_dt desc";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.setString(3, Util.getUserStateCode());
//            ps.setInt(4, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                current_technical_detail = new Object[5];
                current_technical_detail[0] = rs.getString("current_technical_detail").replace(",", "&nbsp; <font color=\"red\">|</font> &nbsp;");
                current_technical_detail[1] = rs.getString("tax_mode");
                current_technical_detail[2] = rs.getInt("tax_amt");
                current_technical_detail[3] = rs.getDate("tax_upto");
                current_technical_detail[4] = rs.getDate("tax_from");
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
        return current_technical_detail;
    }

    public boolean checkReAssignRegnFee(ConvDobj dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        RowSet rs = null;
        boolean flag = false;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("checkReAssignRegnFee");
            sql = "SELECT REGN_NO from vt_fee fee join vp_appl_rcpt_mapping map on fee.rcpt_no=map.rcpt_no"
                    + " where map.appl_no=? and fee.state_cd=? and fee.off_cd=? and fee.pur_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getAppl_no());
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            ps.setInt(4, TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                flag = true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("checkReAssignRegnFee : " + e.getMessage());
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

    //Update corresponding tables when registration number genereated.
    private static void updateTablesOnRegnNoChange(TransactionManager tmgr, String oldRegnNo, String newRegnNo, String applNo) throws SQLException {
        PreparedStatement ps;
        String sqlQuery = "";
        List tableList = new ArrayList();
        List historyTableList = new ArrayList();
        tableList.add(TableList.VT_OWNER_IDENTIFICATION);
        tableList.add(TableList.VT_INSURANCE);
        tableList.add(TableList.VT_IMPORT_VEH);
        tableList.add(TableList.VT_OWNER_EX_ARMY);
        tableList.add(TableList.VT_AXLE);
        tableList.add(TableList.VT_PUCC);
        tableList.add(TableList.VT_RETROFITTING_DTLS);
        tableList.add(TableList.VT_HYPTH);
        tableList.add(TableList.VT_TRAILER);
        tableList.add(TableList.VT_FITNESS);

        historyTableList.add(TableList.VH_PUCC);
        historyTableList.add(TableList.VH_RETROFITTING_DTLS);
        historyTableList.add(TableList.VH_HYPTH);

        for (Object historyTableName : historyTableList) {
            String tableName = String.valueOf(historyTableName).replace("vh_", "vt_");
            sqlQuery = "INSERT INTO " + historyTableName + " SELECT *,? as appl_no,current_timestamp as moved_on, ? as moved_by FROM " + tableName + " WHERE regn_no = ? and state_cd = ? and off_cd = ?";
            ps = tmgr.prepareStatement(sqlQuery);
            ps.setString(1, applNo);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, oldRegnNo);
            ps.setString(4, Util.getUserStateCode());
            ps.setInt(5, Util.getSelectedSeat().getOff_cd());
            ps.executeUpdate();
        }

        sqlQuery = "INSERT INTO " + TableList.VH_IMPORT_VEH
                + "    SELECT  state_cd, off_cd, regn_no, contry_code, dealer, place, foreign_regno, "
                + "       manu_year, ? as appl_no, current_timestamp as moved_on, ? as moved_by "
                + "  FROM " + TableList.VT_IMPORT_VEH + " WHERE regn_no = ? and state_cd = ? and off_cd = ?";

        executeQueryForHistoryTables(sqlQuery, tmgr, applNo, oldRegnNo);

        sqlQuery = "INSERT INTO " + TableList.VH_AXLE
                + " SELECT state_cd, off_cd, regn_no, f_axle_descp, r_axle_descp, o_axle_descp, "
                + "       t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, t_axle_weight, "
                + "       ? as appl_no, current_timestamp + interval '1 second' as moved_on, ? as moved_by "
                + "  FROM " + TableList.VT_AXLE + " WHERE regn_no = ? and state_cd = ? and off_cd = ? ";
        executeQueryForHistoryTables(sqlQuery, tmgr, applNo, oldRegnNo);

        sqlQuery = "INSERT INTO " + TableList.VH_OWNER_EX_ARMY
                + "    SELECT  state_cd, off_cd, regn_no, voucher_no, "
                + "    voucher_dt, place, ? as appl_no, current_timestamp as moved_on, ? as moved_by "
                + "  FROM " + TableList.VT_OWNER_EX_ARMY + " WHERE regn_no = ? and state_cd = ? and off_cd = ? ";
        executeQueryForHistoryTables(sqlQuery, tmgr, applNo, oldRegnNo);

        sqlQuery = "INSERT INTO " + TableList.VH_TRAILER + "(state_cd, off_cd, regn_no, chasi_no, body_type, ld_wt, unld_wt,f_axle_descp, r_axle_descp, o_axle_descp, t_axle_descp, f_axle_weight,r_axle_weight, o_axle_weight, t_axle_weight, appl_no, moved_on,moved_by) \n"
                + "SELECT state_cd, off_cd,regn_no, chasi_no, body_type, ld_wt, unld_wt, f_axle_descp, r_axle_descp,o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight,t_axle_weight,? as appl_no, current_timestamp as moved_on,? as moved_by \n"
                + "FROM " + TableList.VT_TRAILER + " where regn_no = ? and state_cd = ? and off_cd = ?";
        executeQueryForHistoryTables(sqlQuery, tmgr, applNo, oldRegnNo);

        sqlQuery = "INSERT INTO " + TableList.VH_OTHER_STATE_VEH + " SELECT *,? as appl_no,current_timestamp as moved_on, ? as moved_by FROM " + TableList.VT_OTHER_STATE_VEH + " WHERE new_regn_no = ? "
                + "and state_cd = ? and off_cd = ? ";
        executeQueryForHistoryTables(sqlQuery, tmgr, applNo, oldRegnNo);

        sqlQuery = "INSERT into " + TableList.VH_INSURANCE + " "
                + "Select regn_no ,  comp_cd ,  ins_type ,  ins_from ,"
                + " ins_upto ,  policy_no , current_timestamp + interval '1 second' as moved_on, ? as moved_by, state_cd, off_cd, idv"
                + " FROM " + TableList.VT_INSURANCE + " where regn_no=? and state_cd = ? and off_cd = ?";
        ps = tmgr.prepareStatement(sqlQuery);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, oldRegnNo);
        ps.setString(3, Util.getUserStateCode());
        ps.setInt(4, Util.getSelectedSeat().getOff_cd());
        ps.executeUpdate();


        sqlQuery = "INSERT INTO " + TableList.VH_OWNER_IDENTIFICATION
                + "            SELECT state_cd,off_cd,regn_no, mobile_no, email_id, pan_no, aadhar_no, passport_no,"
                + "            ration_card_no, voter_id, dl_no, verified_on, current_timestamp as moved_on, ? moved_by,owner_ctg "
                + "  FROM " + TableList.VT_OWNER_IDENTIFICATION
                + " WHERE regn_no = ? and state_cd = ? and off_cd = ? ";
        ps = tmgr.prepareStatement(sqlQuery);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, oldRegnNo);
        ps.setString(3, Util.getUserStateCode());
        ps.setInt(4, Util.getSelectedSeat().getOff_cd());
        ps.executeUpdate();


        sqlQuery = " Insert into  " + TableList.VH_FITNESS
                + " SELECT state_cd, off_cd, ?,regn_no, chasi_no, fit_chk_dt, fit_chk_tm, "
                + "       fit_result, fit_valid_to, fit_nid, fit_off_cd1, fit_off_cd2, "
                + "       remark, fare_mtr_no, speedgov_no, speedgov_compname, brake, steer, "
                + "       susp, engin, tyre, horn, lamp, embo, speed, paint, wiper, dimen, "
                + "       body, fare, elec, finis, road, poll, transm, glas, emis, rear, "
                + "       others, op_dt,current_timestamp,? "
                + "  FROM " + TableList.VT_FITNESS + " where regn_no=? and state_cd=? and off_cd = ?";
        executeQueryForHistoryTables(sqlQuery, tmgr, applNo, oldRegnNo);


        for (Object tabelName : tableList) {
            sqlQuery = "UPDATE " + tabelName + " SET regn_no=? WHERE regn_no=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sqlQuery);
            ps.setString(1, newRegnNo);
            ps.setString(2, oldRegnNo);
            ps.setString(3, Util.getUserStateCode());
            ps.setInt(4, Util.getSelectedSeat().getOff_cd());
            ps.executeUpdate();
        }

        sqlQuery = "UPDATE " + TableList.VT_OTHER_STATE_VEH + " SET new_regn_no=? WHERE new_regn_no=? and state_cd = ? and off_cd= ?";
        executeUpdateQuery(sqlQuery, newRegnNo, oldRegnNo, tmgr);

        sqlQuery = "UPDATE " + TableList.VT_TAX + " set regn_no= ? WHERE regn_no = ? and state_cd = ? and off_cd=?";
        executeUpdateQuery(sqlQuery, newRegnNo, oldRegnNo, tmgr);

        sqlQuery = "UPDATE " + TableList.VT_TAX_EXEM + " set regn_no= ? WHERE regn_no = ? and state_cd = ? and off_cd=?";
        executeUpdateQuery(sqlQuery, newRegnNo, oldRegnNo, tmgr);


        sqlQuery = "UPDATE  " + TableList.VT_TAX_BASED_ON
                + " set regn_no= ? WHERE regn_no=? and state_cd = ? and off_cd=?";
        executeUpdateQuery(sqlQuery, newRegnNo, oldRegnNo, tmgr);

        sqlQuery = "UPDATE " + TableList.VT_FEE + " set regn_no= ? WHERE regn_no=? and state_cd = ? and off_cd=?";
        executeUpdateQuery(sqlQuery, newRegnNo, oldRegnNo, tmgr);
    }

    private static void executeQueryForHistoryTables(String sqlQuery, TransactionManager tmgr, String applNo, String oldRegnNo) throws SQLException {
        PreparedStatement ps;
        ps = tmgr.prepareStatement(sqlQuery);
        ps.setString(1, applNo);
        ps.setString(2, Util.getEmpCode());
        ps.setString(3, oldRegnNo);
        ps.setString(4, Util.getUserStateCode());
        ps.setInt(5, Util.getSelectedSeat().getOff_cd());
        ps.executeUpdate();
    }

    private static void executeUpdateQuery(String sqlQuery, String newRegnNo, String oldRegnNo, TransactionManager tmgr) throws SQLException {
        PreparedStatement ps;
        ps = tmgr.prepareStatement(sqlQuery);
        ps.setString(1, newRegnNo);
        ps.setString(2, oldRegnNo);
        ps.setString(3, Util.getUserStateCode());
        ps.setInt(4, Util.getSelectedSeat().getOff_cd());
        ps.executeUpdate();
    }

    public boolean taxPaidOrClearedStatusUsingNOC(String regNo, int ownerCode) throws Exception {
        boolean taxPaidStatus = false;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("taxPaidOrClearedStatusUsingNOC");
            if (ownerCode == TableConstants.VEH_TYPE_GOVT
                    || ownerCode == TableConstants.VEH_TYPE_STATE_GOVT
                    || ownerCode == TableConstants.VEH_TYPE_GOVT_UNDERTAKING) {
                taxPaidStatus = true;
            } else {
                taxPaidStatus = isTaxPaidOrCleared(regNo, tmgr);
            }
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }

        return taxPaidStatus;
    }

    public static boolean isTaxPaidOrCleared(String regNo, TransactionManager tmgr) throws VahanException {
        boolean isTaxPaid = false;
        String taxMode = null;
        Date taxPaidUpto = null;
        Date nocDate = null;
        try {
            String sql = "Select noc_dt from " + TableList.VT_OTHER_STATE_VEH + " where old_regn_no = ?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                nocDate = rs.getDate("noc_dt");
            } else {
                nocDate = new Date();
            }

            sql = "Select tax_upto,tax_mode from " + TableList.VT_TAX
                    + " where regn_no=? and pur_cd = 58 order by rcpt_dt desc limit 1 ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                taxMode = rs.getString("tax_mode");
                if (!"OLS".contains(taxMode)) {
                    taxPaidUpto = rs.getDate("tax_upto");
                } else {
                    return true;
                }
            }


            Date taxClearTo = null;
            sql = "Select clear_to from " + TableList.VT_TAX_CLEAR + " where regn_no=? order by op_dt desc limit 1 ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                taxClearTo = rs.getDate("clear_to");
            }


            if (taxPaidUpto != null && nocDate != null && DateUtils.compareDates(nocDate, taxPaidUpto) <= 1) {
                return true;
            } else if (taxClearTo != null && nocDate != null && DateUtils.compareDates(nocDate, taxClearTo) <= 1) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            throw new VahanException("Error in Fetching of Details");
        }


        //return isTaxPaid;
    }

    public List getConvertibleClassesList(int vh_cls) {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        RowSet rs;
        String query;
        List vhClass = new ArrayList();
        try {
            tmgr = new TransactionManager("getConvertibleClassesList");
            query = "SELECT vh_class,descr from " + TableList.VM_VH_CLASS + " where vh_class::text IN (Select regexp_split_to_table(convertible_classes,',') from " + TableList.VM_VH_CLASS + " where vh_class = ?)";
            ps = tmgr.prepareStatement(query);
            ps.setInt(1, vh_cls);
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                vhClass.add(new SelectItem(rs.getInt("vh_class"), rs.getString("descr")));
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
        return vhClass;
    }

    public static String getTaxModeInfo(String regNo, int purCd) throws VahanException {
        TransactionManager tmgr = null;
        String taxMode = "";
        try {
            tmgr = new TransactionManager("getTaxModeInfo");

            String sql = "Select tax_mode from " + TableList.VT_TAX + " where regn_no=? and state_cd=? and off_cd=? and pur_cd=? order by rcpt_dt desc limit 1 ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            ps.setInt(4, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                taxMode = rs.getString("tax_mode");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching of Tax Details");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return taxMode;
    }

    public static void insertUpdateVaNumGenPermit(TransactionManager tmgr, String appl_no, String regn_no, ConvDobj conv_dobj, Appl_Details_Dobj sessionVariable) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {

            sql = "SELECT old_regn_no FROM  " + TableList.VA_NUM_GEN_PERMITDETAILS + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) { //if any record is exist then update otherwise insert it
                insertIntoVhaNumGenPermitHistory(tmgr, appl_no, sessionVariable);
                updateVaNumGenPermit(tmgr, conv_dobj);
            } else {
                insertIntoVaNumGenPermit(tmgr, conv_dobj, sessionVariable);
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public static void insertIntoVhaNumGenPermitHistory(TransactionManager tmgr, String appl_no, Appl_Details_Dobj sessionVariable) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            //inserting data into VhaNumGenPermitHistory from VaNumGenPermitHistory
            sql = "INSERT INTO " + TableList.VHA_NUM_GEN_PERMITDETAILS + " (\n"
                    + "            moved_on, moved_by, state_cd, off_cd, appl_no, old_regn_no, new_regn_no,pur_cd, pmt_type , pmt_catg ,num_generation ,op_dt,service_type)  \n"
                    + "SELECT current_timestamp as moved_on, ? as moved_by, state_cd, off_cd, appl_no, old_regn_no, new_regn_no,pur_cd, pmt_type , pmt_catg ,num_generation ,op_dt ,service_type \n"
                    + "  FROM " + TableList.VA_NUM_GEN_PERMITDETAILS + " where appl_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, sessionVariable.getCurrentEmpCd());
            ps.setString(2, appl_no);
            ps.executeUpdate();

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public static void updateVaNumGenPermit(TransactionManager tmgr, ConvDobj dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            //updation of updateVaNumGenPermit
            sql = "UPDATE " + TableList.VA_NUM_GEN_PERMITDETAILS
                    + "   SET  old_regn_no=?, new_regn_no=?,  \n"
                    + "    pmt_type=? , pmt_catg=? ,num_generation=?,service_type=?,op_dt = current_timestamp\n"
                    + "  WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            //ps statement
            ps.setString(1, dobj.getOld_regn_no());
            ps.setString(2, dobj.getNew_regn_no());
            ps.setInt(3, dobj.getPmt_type());
            ps.setInt(4, dobj.getPmt_catg());
            ps.setBoolean(5, dobj.isNextSeriesGen());
            ps.setInt(6, dobj.getServiceType());
            ps.setString(7, dobj.getAppl_no());
            ps.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public static void insertIntoVaNumGenPermit(TransactionManager tmgr, ConvDobj dobj, Appl_Details_Dobj sessionVariable) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        int i = 1;
        try {
            sql = "INSERT INTO " + TableList.VA_NUM_GEN_PERMITDETAILS + " (\n"
                    + "            state_cd, off_cd, appl_no, old_regn_no, new_regn_no,pur_cd, pmt_type , pmt_catg ,num_generation ,  op_dt,service_type)\n"
                    + "    VALUES (?, ?, ?, ?, ?,?, ?, \n"
                    + "            ?, ?, current_timestamp,?)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(i++, sessionVariable.getCurrent_state_cd());
            ps.setInt(i++, sessionVariable.getCurrent_off_cd());
            ps.setString(i++, dobj.getAppl_no());
            ps.setString(i++, dobj.getOld_regn_no());
            ps.setString(i++, dobj.getNew_regn_no());
            ps.setInt(i++, TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION);
            ps.setInt(i++, dobj.getPmt_type());
            ps.setInt(i++, dobj.getPmt_catg());
            ps.setBoolean(i++, dobj.isNextSeriesGen());
            ps.setInt(i++, dobj.getServiceType());

            ps.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public boolean isTaxCollectedWithConv(String stateCode, String action, int purCode) throws VahanException {
        PreparedStatement ps;
        TransactionManager tmgr = null;
        RowSet rs;
        String query;
        boolean status = false;
        try {
            tmgr = new TransactionManager("isTaxCollectedWithConv");
            query = "Select pur_cd from " + TableList.VC_ACTION_PURPOSE_MAP + " Where state_cd = ? and action = ? and pur_cd = ?";
            ps = tmgr.prepareStatement(query);
            ps.setString(1, stateCode);
            ps.setString(2, action);
            ps.setInt(3, purCode);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                status = true;
            }
        } catch (SQLException e) {
            throw new VahanException(e.getMessage());
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return status;
    }

    public void insertIntovtRetentionRegNoDetails(TransactionManager tmgr, Owner_dobj ownerDobj, String reason, String applNo, Date applDate, int pur_cd, boolean isTowithRetentionOrConv, String newRegnNo) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        //String newRegnNo = null;
        String rcptNo = null;
        String oldRegnNo = null;
        int pos = 1;
        TransactionManager tmgrSel = null;
        try {
            tmgrSel = new TransactionManager("dfff");
            sql = "select a.rcpt_no  from " + TableList.VT_FEE + " a , " + TableList.VP_APPL_RCPT_MAPPING + "  b where  a.rcpt_no=b.rcpt_no and a.state_cd=b.state_cd and a.off_cd=b.off_cd and b.appl_no=? and pur_cd=? and a.state_cd = ? and a.off_cd= ?";
            ps = tmgrSel.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setInt(2, TableConstants.SWAPPING_REGN_PUR_CD);
            ps.setString(3, Util.getUserStateCode());
            ps.setInt(4, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgrSel.fetchDetachedRowSet();

            if (rs.next()) {
                rcptNo = rs.getString("rcpt_no");
            }
            if (rcptNo == null || rcptNo.trim().equalsIgnoreCase("")) {
                throw new VahanException("Can't Approve as Fees is not Paid for Fancy No Surrender Retention.");
            }

            sql = "INSERT INTO " + TableList.VT_SURRENDER_RETENTION
                    + "  ( state_cd, off_cd, regn_appl_no, old_regn_no, new_regn_no, owner_name, f_name, c_add1, c_add2, "
                    + "    c_add3, c_district, c_pincode, c_state, vh_class, rcpt_no, mobile_no, surr_dt, "
                    + "    file_no, reason, dt_of_app, approved_by, op_dt) "
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ps = tmgr.prepareStatement(sql);

            ps.setString(pos++, Util.getUserStateCode());
            ps.setInt(pos++, Util.getSelectedSeat().getOff_cd());
            ps.setNull(pos++, java.sql.Types.VARCHAR);
            if (isTowithRetentionOrConv && !CommonUtils.isNullOrBlank(oldRegnNo)) {
                ps.setString(pos++, oldRegnNo);
            } else {
                ps.setString(pos++, ownerDobj.getRegn_no());
            }
            ps.setString(pos++, newRegnNo);
            ps.setString(pos++, ownerDobj.getOwner_name());
            ps.setString(pos++, ownerDobj.getF_name());
            ps.setString(pos++, ownerDobj.getC_add1());
            ps.setString(pos++, ownerDobj.getC_add2());
            ps.setString(pos++, ownerDobj.getC_add3());
            ps.setInt(pos++, ownerDobj.getC_district());
            ps.setInt(pos++, ownerDobj.getC_pincode());
            ps.setString(pos++, ownerDobj.getC_state());
            ps.setInt(pos++, ownerDobj.getVh_class());
            ps.setString(pos++, rcptNo);
            if (ownerDobj.getOwner_identity().getMobile_no() != null) {
                ps.setLong(pos++, ownerDobj.getOwner_identity().getMobile_no());
            } else {
                ps.setLong(pos++, 0);
            }
            ps.setDate(pos++, new java.sql.Date(new Date().getTime()));//surrender date is the approval date
            ps.setNull(pos++, java.sql.Types.VARCHAR);//file no is not known so currently putting it as NULL
            ps.setString(pos++, reason);
            ps.setDate(pos++, new java.sql.Date(applDate.getTime()));
            ps.setString(pos++, Util.getEmpCode());
            ps.setTimestamp(pos++, ServerUtil.getSystemDateInPostgres());
            ps.executeUpdate();
            // int count = ps.executeUpdate();
//        if (count == 0) {
//            throw new VahanMessageException("This Retained Registration Number already used by another application.");
//        }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (tmgrSel != null) {
                    tmgrSel.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
    }

    public boolean isNumRetentionWithTOConv(String applNo, int offCd, String stateCd) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        boolean isNumRetentionWithTOCon = false;
        RowSet rs = null;
        String sql = null;
        try {
            tmgr = new TransactionManager("isNumRetentionWithTOCon");
            sql = "select * from " + TableList.VHA_SURRENDER_RETENTION + "  where appl_no=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                isNumRetentionWithTOCon = true;
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
        return isNumRetentionWithTOCon;
    }

    public Date prevConvDtlsNtToTr(String regnNo, int offCd, String stateCd) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        Date fitUpto = null;
        RowSet rs = null;
        String sql = null;
        try {
            tmgr = new TransactionManager("prevConvDtlsNtToTr");
            sql = "select vha.old_fit_dt as old_fit_dt from " + TableList.VH_CONVERSION + " vh inner join vha_conversion vha on vh.appl_no= vha.appl_no where vh.old_regn_no=? and vh.state_cd=? and vh.off_cd=? and vh.old_vch_class<51 and vh.new_vch_class>50 order by vh.moved_on asc  limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                fitUpto = rs.getDate("old_fit_dt");
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
        return fitUpto;
    }

    public static ConvDobj set_ConversionInward_appl_db_to_dobj(String appl_no, String regn_no) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        ConvDobj dobj = null;
        VahanException vahanexecption = null;
        String tname = null, pname = null, pvalue = null;

        if (!CommonUtils.isNullOrBlank(appl_no)) {
            appl_no = appl_no.toUpperCase();
            tname = TableList.VA_CONVERSION_INWARD;
            pname = "appl_no";
            pvalue = appl_no;
        } else if (!CommonUtils.isNullOrBlank(regn_no)) {
            regn_no = regn_no.toUpperCase();
            tname = TableList.VA_CONVERSION_INWARD;
            pname = "old_regn_no";
            pvalue = regn_no;
        } else {
            vahanexecption = new VahanException("Application detail not found !");
            throw vahanexecption;
        }

        try {
            tmgr = new TransactionManager("set_Conversion_appl_db_to_dobj");
            ps = tmgr.prepareStatement("SELECT appl_no, old_vch_class,\n"
                    + "       new_vch_class, new_vch_catg \n"
                    + "  FROM " + tname + " where " + pname + "=?");
            ps.setString(1, pvalue);

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) {
                dobj = new ConvDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setNew_vch_class(rs.getInt("new_vch_class"));
                dobj.setOld_vch_class(rs.getInt("old_vch_class"));
                dobj.setNew_vch_catg(rs.getString("new_vch_catg"));
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanexecption = new VahanException("Database Error in fetching details for [ " + appl_no + "]");
            throw vahanexecption;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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
        return dobj;
    }

    public static void insertIntoInwrdConversion(TransactionManager tmgr, ConvDobj dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        int i = 1;
        try {
            sql = "INSERT INTO " + TableList.VA_CONVERSION_INWARD + " (\n"
                    + "            state_cd, off_cd, appl_no, old_regn_no, old_vch_class, old_vch_catg, \n"
                    + "            new_vch_class, new_vch_catg,op_dt)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, \n"
                    + "            ?, ?,current_timestamp)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.setString(i++, dobj.getAppl_no());
            ps.setString(i++, dobj.getOld_regn_no());
            ps.setInt(i++, dobj.getOld_vch_class());
            ps.setString(i++, dobj.getOld_vch_catg());
            ps.setInt(i++, dobj.getNew_vch_class());
            ps.setString(i++, dobj.getNew_vch_catg());
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        }
    }

    public static void insertIntoVhaAndDeleteVaInwrdConversion(TransactionManager tmgr, String applNo, String empCd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "select * from " + TableList.VA_CONVERSION_INWARD + "  where appl_no=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getSelectedSeat().getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                sql = "INSERT INTO " + TableList.VHA_CONVERSION_INWARD + " (moved_on, moved_by, state_cd, off_cd, appl_no, old_regn_no, old_vch_class, old_vch_catg, new_vch_class, new_vch_catg, op_dt)  "
                        + "SELECT current_timestamp as moved_on, ? as moved_by, state_cd, off_cd, appl_no, old_regn_no, old_vch_class, old_vch_catg, new_vch_class, new_vch_catg, op_dt \n"
                        + "  FROM " + TableList.VA_CONVERSION_INWARD + " where appl_no=? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, empCd);
                ps.setString(2, applNo);
                ps.executeUpdate();
                ServerUtil.deleteFromTable(tmgr, "", applNo, TableList.VA_CONVERSION_INWARD);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(e.getMessage());
        }
    }
}
