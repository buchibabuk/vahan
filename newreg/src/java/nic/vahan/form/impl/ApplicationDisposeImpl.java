/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.ApplDisposeVerifyByAdminDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.common.Appl_Details_Dobj;
import nic.vahan.form.dobj.smartcard.DeleteSmartcardFlatFileDobj;
import static nic.vahan.form.impl.FitnessImpl.insertIntoVhaSpeedGovernor;
import nic.vahan.form.impl.agentlicence.AgentDetailImpl;
import nic.vahan.form.impl.commoncarrier.DetailCommonCarrierImpl;
import nic.vahan.form.impl.hsrp.HSRPRequestImpl;
import nic.vahan.form.impl.hsrp.HsrpEntryImpl;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.CounterSignatureImpl;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.form.impl.permit.PermitEndorsementAppImpl;
import nic.vahan.form.impl.permit.PermitHomeAuthImpl;
import nic.vahan.form.impl.permit.PrintPermitImpl;
import nic.vahan.form.impl.permit.SurrenderPermitImpl;
import nic.vahan.form.impl.permit.TemporaryPermitImpl;
import nic.vahan.form.impl.tradecert.ApplicationTradeCertImpl;
import nic.vahan.form.impl.tradecert.CancelTradeCertImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

public class ApplicationDisposeImpl {

    private static final Logger LOGGER = Logger.getLogger(ApplicationDisposeImpl.class);

    public static String getRegNo(String appl_no) {

        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        String regnNo = "";
        try {

            tmgr = new TransactionManager("getRegNo");
            sql = "SELECT distinct regn_no from " + TableList.VA_DETAILS
                    + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                regnNo = rs.getString("regn_no");
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
        return regnNo;
    }

    public void disposeApplications(Status_dobj status, int sizeOfSelectedApplforDispose, int sizsOfApplInwarded, Owner_dobj ownerDobj) throws Exception {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String tableName = "";
        String sql = "";
        PassengerPermitDetailImpl passImpl = null;
        TemporaryPermitImpl tempImpl = null;
        PermitHomeAuthImpl authImpl = null;
        TmConfigurationDobj configDobj = null;
        boolean pmtFlag = false;

        try {
            tmgr = new TransactionManager("disposeApplications");
            configDobj = Util.getTmConfiguration();
            String current_date = JSFUtils.getDateInDD_MMM_YYYY(DateUtils.parseDate(new Date()));
            switch (status.getPur_cd()) {
                case TableConstants.ADMIN_OWNER_DATA_CHANGE://
                case TableConstants.STATEADMIN_OWNER_DATA_CHANGE:
                case TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE:
                case TableConstants.VM_TRANSACTION_MAST_TEMP_REG:
                case TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE:
                case TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE:
                    for (int i = 0; i < TableList.VAList.size(); i++) {
                        ServerUtil.insertIntoVhaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHAList.get(i).toString(), TableList.VAList.get(i).toString());
                        ServerUtil.deleteFromTable(tmgr, null, status.getAppl_no(), TableList.VAList.get(i).toString());
                    }
                    //add restore
                    ServerUtil.insertIntoVhaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), "vha_owner_temp", "va_owner_temp");
                    ServerUtil.deleteFromTable(tmgr, null, status.getAppl_no(), "va_owner_temp");
                    if (status.getPur_cd() == TableConstants.ADMIN_OWNER_DATA_CHANGE || status.getPur_cd() == TableConstants.STATEADMIN_OWNER_DATA_CHANGE) {
                        ServerUtil.insertIntoVhaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_OWNER, TableList.VA_OWNER_TEMP_ADMIN);
                        ServerUtil.deleteFromTable(tmgr, null, status.getAppl_no(), TableList.VA_OWNER_TEMP_ADMIN);
                    }

                    sql = "select * from " + TableList.VA_FC_PRINT
                            + " where appl_no=? and state_cd=? and off_cd=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status.getAppl_no());
                    ps.setString(2, status.getState_cd());
                    ps.setInt(3, status.getOff_cd());
                    RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        throw new VahanException("Application No [" + status.getAppl_no() + "] is Approved for Fitness / Inspection");
                    }

                    if (status.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                        if ("DL".equals(status.getState_cd())) {
                            //add for restore dispose application
                            sql = "insert into " + TableList.VPH_BANK_SUBSIDY + " select current_timestamp as moved_on,? as moved_by,a.* from " + TableList.VP_BANK_SUBSIDY + " a WHERE a.appl_no=? ";
                            ps = tmgr.prepareStatement(sql);
                            ps.setString(1, Util.getEmpCode());
                            ps.setString(2, status.getAppl_no());
                            ps.executeUpdate();

                            //end 
                            sql = "delete from " + TableList.VP_BANK_SUBSIDY + " where  appl_no = ? ";
                            ps = tmgr.prepareStatement(sql);
                            ps.setString(1, status.getAppl_no());
                            ps.executeUpdate();
                        }
                        ServerUtil.deleteFromTable(tmgr, null, status.getAppl_no(), TableList.VA_DEALER_PENDENCY);
                    }
                    if ((status.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || status.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE)
                            && configDobj != null && configDobj.getTmConfigOtpDobj() != null && configDobj.getTmConfigOtpDobj().isOwner_mobile_verify_with_otp()) {
                        ServerUtil.deleteFromTable(tmgr, null, status.getAppl_no(), TableList.VT_OTP_VERIFY);
                    }

                    //for reuse of receipt amount paid for fancy no
                    //add for restore dispose application
                    sql = "insert into " + TableList.VH_ADVANCE_REGN_NO
                            + " select *,current_timestamp as reopen_on, ? as reopen_by"
                            + " from " + TableList.VT_ADVANCE_REGN_NO
                            + " where regn_appl_no = ? ";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getEmpCode());
                    ps.setString(2, status.getAppl_no());
                    ps.executeUpdate();
                    //end

                    sql = "UPDATE " + TableList.VT_ADVANCE_REGN_NO
                            + " SET regn_appl_no = null WHERE regn_appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status.getAppl_no());
                    ps.executeUpdate();
                    //for reuse of receipt amount paid for fancy no
                    sql = "insert into " + TableList.VH_SURRENDER_RETENTION
                            + " select current_timestamp as moved_on, ? as moved_by,*"
                            + " from " + TableList.VT_SURRENDER_RETENTION
                            + " where regn_appl_no = ? ";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getEmpCode());
                    ps.setString(2, status.getAppl_no());
                    ps.executeUpdate();
                    //end

                    //for reuse of receipt amount paid for retained no
                    sql = "Update " + TableList.VT_SURRENDER_RETENTION + " Set regn_appl_no = ? Where regn_appl_no = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setNull(1, java.sql.Types.NULL);
                    ps.setString(2, status.getAppl_no());
                    ps.executeUpdate();

                    //for handling case new regn against scrapped vehicle --Start--
                    //for reuse of receipt amount paid for fancy no******
                    sql = "insert into " + TableList.VH_SCRAP_VEHICLE
                            + " select current_timestamp as moved_on, ? as moved_by,*"
                            + " from " + TableList.VT_SCRAP_VEHICLE
                            + " where regn_appl_no = ? ";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getEmpCode());
                    ps.setString(2, status.getAppl_no());
                    ps.executeUpdate();
                    //end
                    sql = "UPDATE " + TableList.VT_SCRAP_VEHICLE
                            + " SET new_regn_no=?,new_chasi_no=?,regn_appl_no=?"
                            + " WHERE regn_appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setNull(1, java.sql.Types.VARCHAR);
                    ps.setNull(2, java.sql.Types.VARCHAR);
                    ps.setNull(3, java.sql.Types.VARCHAR);
                    ps.setString(4, status.getAppl_no());
                    ps.executeUpdate();
                    //for handling case new regn against scrapped vehicle --End---

                    if (ownerDobj != null
                            && (status.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                            || status.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) && !"NEW".equalsIgnoreCase(ownerDobj.getRegn_no())) {

                        RegVehCancelReceiptImpl regVehCancelReceiptImpl = new RegVehCancelReceiptImpl();
                        int seriesId = 0;
                        String allotedNo = ServerUtil.getRegnNoAllotedDetail(status.getAppl_no(), status.getState_cd(), status.getOff_cd());
                        if (allotedNo != null && allotedNo.trim().length() > 0) {
                            VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
                            seriesId = regVehCancelReceiptImpl.getSeriesId(vehParameters);
                        }

                        if (allotedNo != null && allotedNo.trim().length() > 0 && seriesId != 0) {
                            sql = "INSERT INTO " + TableList.VM_REGN_AVAILABLE
                                    + " (state_cd, off_cd, regn_no, status, amount, entered_by, entered_on, prefix_series, series_id)"
                                    + " VALUES (?, ?, ?, ?, ?, ?, current_timestamp, ?, ?)";
                            int suffixSize = 4;
                            ps = tmgr.prepareStatement(sql);
                            ps.setString(1, status.getState_cd());
                            ps.setInt(2, status.getOff_cd());
                            ps.setString(3, ownerDobj.getRegn_no());
                            ps.setString(4, "A");
                            ps.setInt(5, 0);
                            ps.setString(6, Util.getEmpCode());
                            ps.setString(7, ownerDobj.getRegn_no().substring(0, (ownerDobj.getRegn_no().length() - suffixSize)));
                            ps.setInt(8, seriesId);
                            ps.executeUpdate();

                            sql = "DELETE FROM " + TableList.VM_REGN_ALLOTED
                                    + " WHERE REGN_NO=? AND APPL_NO=?";
                            ps = tmgr.prepareStatement(sql);
                            ps.setString(1, ownerDobj.getRegn_no());
                            ps.setString(2, status.getAppl_no());
                            ps.executeUpdate();
                        }

                        new HsrpEntryImpl().insertIntoVH_HSRP(status.getRegn_no(), status.getState_cd(), status.getOff_cd(), tmgr, String.valueOf(status.getEmp_cd()), "Dispose Application");
                        ServerUtil.deleteFromTable(tmgr, status.getRegn_no(), null, TableList.VT_HSRP);
                    }

                    //Reverting Chassis Fitness data if vehicle has Chassis FC issued against it
                    if (ownerDobj != null && (status.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                            || status.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE)) {
                        boolean isNewVehicleFitness = false;
                        NewVehicleFitnessImpl newVehicleFitnessImpl = new NewVehicleFitnessImpl();
                        isNewVehicleFitness = newVehicleFitnessImpl.checkForPreRegFitness(ownerDobj.getChasi_no(), ownerDobj.getEng_no());
                        if (!isNewVehicleFitness) {
                            String chassisFitApplNo = "";
                            chassisFitApplNo = newVehicleFitnessImpl.checkForPreRegFitnessHistory(ownerDobj.getChasi_no(), ownerDobj.getState_cd());
                            if (!CommonUtils.isNullOrBlank(chassisFitApplNo)) {
                                new NewVehicleFitnessImpl().saveInVtFitChassisFromVhFitChassis(tmgr, ownerDobj.getChasi_no(), ownerDobj.getState_cd());
                                new RestoreDisposeApplicationImpl().insertIntoVaTable(tmgr, chassisFitApplNo, String.valueOf(status.getEmp_cd()), TableList.VHA_OWNER, TableList.VA_OWNER);
                                new RestoreDisposeApplicationImpl().insertIntoVaTable(tmgr, chassisFitApplNo, String.valueOf(status.getEmp_cd()), TableList.VHA_TMP_REGN_DTL, TableList.VA_TMP_REGN_DTL);
                                new RestoreDisposeApplicationImpl().insertIntoVaTable(tmgr, chassisFitApplNo, String.valueOf(status.getEmp_cd()), TableList.VHA_RETROFITTING_DTLS, TableList.VA_RETROFITTING_DTLS);
                                new RestoreDisposeApplicationImpl().insertIntoVaTable(tmgr, chassisFitApplNo, String.valueOf(status.getEmp_cd()), TableList.VHA_AXLE, TableList.VA_AXLE);
                                new RestoreDisposeApplicationImpl().insertIntoVaTable(tmgr, chassisFitApplNo, String.valueOf(status.getEmp_cd()), TableList.VHA_SPEED_GOVERNOR, TableList.VA_SPEED_GOVERNOR);
                                new RestoreDisposeApplicationImpl().insertIntoVaTable(tmgr, chassisFitApplNo, String.valueOf(status.getEmp_cd()), TableList.VHA_REFLECTIVE_TAPE, TableList.VA_REFLECTIVE_TAPE);
                            }
                        }
                    }
                    if (status.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                            || status.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE
                            || status.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                            || status.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
                        if (configDobj != null && configDobj.getTmConfigDmsDobj() != null && configDobj.getTmConfigDmsDobj().isDigitalSignAllowStateWise()) {
                            ServerUtil.insertIntoVhaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VH_DSC_DOC_DETAILS, TableList.VT_DSC_DOC_DETAILS);
                            ServerUtil.deleteFromTable(tmgr, null, status.getAppl_no(), TableList.VT_DSC_DOC_DETAILS);
                        }
                    }
                    break;

                case TableConstants.VM_TRANSACTION_MAST_CHG_ADD:
                    CaImpl.insertIntoCAHistory(tmgr, status.getAppl_no());
                    tableName = TableList.VA_CA;
                    //for case of CA without NOC
                    FitnessImpl fitnessImpl = new FitnessImpl();
                    fitnessImpl.insertIntoVhaInspection(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()));
                    ServerUtil.deleteFromTable(tmgr, null, status.getAppl_no(), TableList.VA_INSPECTION);
                    break;

                case TableConstants.VM_TRANSACTION_MAST_TO:
                case TableConstants.VM_TRANSACTION_MAST_FRESH_RC:
                    ToImpl.insertIntoToHistory(tmgr, status.getAppl_no());
                    ToImpl toImpl = new ToImpl();
                    toImpl.insertIntoVhaSurrenderRetention(tmgr, status.getAppl_no());
                    tableName = TableList.VA_SURRENDER_RETENTION;
                    ServerUtil.deleteFromTable(tmgr, null, status.getAppl_no(), tableName);

                    //for reuse of receipt amount paid for fancy no
                    //Restore
                    sql = "insert into " + TableList.VH_SURRENDER_RETENTION
                            + " select current_timestamp as moved_on, ? as moved_by,* "
                            + " from " + TableList.VT_SURRENDER_RETENTION
                            + " where regn_appl_no = ? ";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getEmpCode());
                    ps.setString(2, status.getAppl_no());
                    ps.executeUpdate();
                    //end
                    String query = "Update " + TableList.VT_SURRENDER_RETENTION + " Set regn_appl_no = ? Where regn_appl_no = ?";
                    ps = tmgr.prepareStatement(query);
                    ps.setNull(1, java.sql.Types.NULL);
                    ps.setString(2, status.getAppl_no());
                    ps.executeUpdate();
                    tableName = TableList.VA_TO;

                    //Restore
                    sql = "insert into " + TableList.VHA_FRC_PRINT
                            + " select current_timestamp as moved_on, ? as moved_by,*"
                            + " from " + TableList.VA_FRC_PRINT
                            + " where appl_no = ? ";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getEmpCode());
                    ps.setString(2, status.getAppl_no());
                    ps.executeUpdate();
                    //end
                    sql = "Delete From " + TableList.VA_FRC_PRINT + " WHERE appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status.getAppl_no());
                    ps.executeUpdate();
                    //for reuse of advance number receipt

                    sql = "insert into " + TableList.VH_ADVANCE_REGN_NO
                            + " select *,current_timestamp as reopen_on, ? as reopen_by"
                            + " from " + TableList.VT_ADVANCE_REGN_NO
                            + " where regn_appl_no = ? ";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getEmpCode());
                    ps.setString(2, status.getAppl_no());
                    ps.executeUpdate();

                    sql = "UPDATE " + TableList.VT_ADVANCE_REGN_NO
                            + " SET regn_appl_no = null WHERE regn_appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status.getAppl_no());
                    ps.executeUpdate();
                    break;

                case TableConstants.VM_TRANSACTION_MAST_NOC:
                    NocImpl.insertIntoNOCHistory(tmgr, status.getAppl_no());
                    tableName = TableList.VA_NOC;
                    break;

                case TableConstants.VM_TRANSACTION_MAST_NOC_CANCEL:
                    NocImpl.insertIntoCancelNOCHistory(tmgr, status.getAppl_no());
                    tableName = TableList.VA_NOC_CANCEL;
                    break;

                case TableConstants.VM_TRANSACTION_MAST_DUP_RC:
                case TableConstants.VM_TRANSACTION_MAST_DUP_FC:
                    DupImpl.insertIntoDupHistory(tmgr, status.getAppl_no(), status.getPur_cd());
                    tableName = TableList.VA_DUP;
                    break;

                case TableConstants.VM_TRANSACTION_MAST_FIT_CERT:
                    FitnessImpl.insertVhaFitness(tmgr, status.getAppl_no());
                    tableName = TableList.VA_FITNESS;
                    FitnessImpl.insertIntoVhaSpeedGovernor(status.getAppl_no(), tmgr);
                    FitnessImpl.deleteVaSpeedGovernor(status.getAppl_no(), tmgr);
                    break;

                case TableConstants.VM_TRANSACTION_MAST_ADD_HYPO:
                    HpaImpl.insertIntoHPAHistory(tmgr, status.getAppl_no());
                    tableName = TableList.VA_HPA;
                    break;

                case TableConstants.VM_TRANSACTION_MAST_REM_HYPO:
                    HpaImpl.insertInto_HPT_History(tmgr, status.getAppl_no());
                    tableName = TableList.VA_HPT;
                    break;

                case TableConstants.VM_TRANSACTION_MAST_HPC:
                    break;

                case TableConstants.VM_TRANSACTION_MAST_REN_REG:
                    if (configDobj != null && configDobj.getRegn_gen_type().equalsIgnoreCase("P")) {
                        String regnAlloted = ServerUtil.getRegnNoAllotedDetail(status.getAppl_no(), status.getState_cd(), status.getOff_cd());
                        if (!CommonUtils.isNullOrBlank(regnAlloted)) {
                            throw new VahanException("New Registration Number Generated for this transaction :" + status.getPurCdDescr());
                        }
                    }
                    RenewalImpl.insertIntoRenewalHistory(tmgr, status.getAppl_no());
                    tableName = TableList.VA_RENEWAL;
                    break;

                case TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION:
                    if (configDobj != null && configDobj.getRegn_gen_type().equalsIgnoreCase("P")) {
                        String regnAlloted = ServerUtil.getRegnNoAllotedDetail(status.getAppl_no(), status.getState_cd(), status.getOff_cd());
                        if (!CommonUtils.isNullOrBlank(regnAlloted)) {
                            throw new VahanException("New Registration Number Generated for this transaction :" + status.getPurCdDescr());
                        }
                    }
                    Appl_Details_Dobj dobj = new Appl_Details_Dobj();
                    dobj.setCurrentEmpCd(String.valueOf(status.getEmp_cd()));
                    ConvImpl.insertIntoConversionHistory(tmgr, status.getAppl_no());
                    ConvImpl.insertIntoVhaNumGenPermitHistory(tmgr, status.getAppl_no(), dobj);
                    ConvImpl.insertIntoVhaAndDeleteVaInwrdConversion(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()));
                    ServerUtil.deleteFromTable(tmgr, "", status.getAppl_no(), TableList.VA_NUM_GEN_PERMITDETAILS);
                    tableName = TableList.VA_CONVERSION;
                    break;
                case TableConstants.VM_TRANSACTION_MAST_VEH_ALTER:
                    AltImpl.insertIntoALTHistory(tmgr, status.getAppl_no());
                    new AltImpl().insertIntoTrailerHistory(tmgr, status.getAppl_no());
                    ServerUtil.deleteFromTable(tmgr, "", status.getAppl_no(), TableList.VA_TRAILER);
                    ServerUtil.deleteFromTable(tmgr, "", status.getAppl_no(), TableList.VA_SIDE_TRAILER);
                    tableName = TableList.VA_ALT;
                    break;
                case TableConstants.VM_MAST_RC_SURRENDER:
                    RcSurrenderReleaseCancellationImpl.insertVhaRcReleaseSurrenderCancellantionDispose(status, tmgr);
                    tableName = TableList.VA_RC_SURRENDER;
                    break;
                case TableConstants.VM_MAST_RC_RELEASE:
                    RcSurrenderReleaseCancellationImpl.insertVhaRcReleaseSurrenderCancellantionDispose(status, tmgr);
                    tableName = TableList.VA_RC_RELEASE;
                    break;
                case TableConstants.VM_MAST_RC_CANCELLATION:
                    RcSurrenderReleaseCancellationImpl.insertVhaRcReleaseSurrenderCancellantionDispose(status, tmgr);
                    tableName = TableList.VA_RC_CANCEL;
                    break;
                case TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE_FITNESS:
                    NewImpl.insertIntoVhaOwner(tmgr, status.getAppl_no());
                    RetroFittingDetailsImpl.insertIntoVhaCng(tmgr, status.getAppl_no());
                    AxleImpl.insertIntoVhaAxle(tmgr, status.getAppl_no());
                    insertIntoVhaSpeedGovernor(status.getAppl_no(), tmgr);
                    NewVehicleFitnessImpl.insertVhaFitnessByAppl_no(tmgr, status.getAppl_no());
                    new FitnessImpl().insertIntoVhaReflectiveTape(tmgr, status.getAppl_no(), Util.getEmpCode());
                    ServerUtil.deleteFromTable(tmgr, "", status.getAppl_no(), TableList.VA_OWNER);
                    ServerUtil.deleteFromTable(tmgr, "", status.getAppl_no(), TableList.VA_HOMO_DETAILS);
                    RetroFittingDetailsImpl.deleteFromVaRetroFittingDetails(tmgr, status.getAppl_no());
                    AxleImpl.deleteFromVaAxle(tmgr, status.getAppl_no());
                    FitnessImpl.deleteVaSpeedGovernor(status.getAppl_no(), tmgr);
                    new FitnessImpl().deleteVaReflectiveTape(status.getAppl_no(), tmgr);
                    tableName = TableList.VA_FITNESS_CHASSIS;
                    break;
                case TableConstants.VM_TRANSACTION_TRADE_CERT_NEW:
                case TableConstants.VM_TRANSACTION_TRADE_CERT_ONLINE_APPLICATION:
                case TableConstants.VM_TRANSACTION_TRADE_CERT_DUP:
                    ApplicationTradeCertImpl.insertTCApplicationToHistory(tmgr, status.getAppl_no());
                    tableName = TableList.VA_TRADE_CERTIFICATE;
                    break;
                case TableConstants.VM_CANCEL_TRADE_CERT:
                    CancelTradeCertImpl.insertTCCancelApplicationToHistory(tmgr, status.getAppl_no());
                    tableName = TableList.VA_SURRENDER_TRADE_CERTIFICATE;
                    break;
                case TableConstants.VM_PMT_FRESH_PUR_CD:
                case TableConstants.VM_PMT_APPLICATION_PUR_CD:
                    passImpl = new PassengerPermitDetailImpl();
                    passImpl.insertVHA_PermitandUpdateVa_Permit(tmgr, null, status.getAppl_no(), null);
                    passImpl.insertVHA_Owner_PermitandUpdateVa_Owner_Permit(tmgr, null, status.getAppl_no());
                    passImpl.insertVaToVhaMultiAppAllow(tmgr, null, status.getAppl_no());
                    CommonPermitPrintImpl.deleteFromTable(tmgr, TableList.VA_PERMIT_OWNER, status.getAppl_no());
                    pmtFlag = passImpl.applPermitIsThere(status.getAppl_no(), TableConstants.VM_PMT_APPLICATION_PUR_CD);
                    new PrintPermitImpl().vaPermitPrintTOvhPermitPrint(status.getAppl_no(), tmgr, "3");
                    tableName = TableList.VA_PERMIT;
                    ServerUtil.vatovhNPAuthData(status.getAppl_no(), tmgr);
                    break;
                case TableConstants.VM_PMT_RENEWAL_PUR_CD:
                    passImpl = new PassengerPermitDetailImpl();
                    passImpl.insertVHA_PermitandUpdateVa_Permit(tmgr, null, status.getAppl_no(), null);
                    tableName = TableList.VA_PERMIT;
                    break;
                case TableConstants.VM_PMT_TEMP_PUR_CD:
                case TableConstants.VM_PMT_RENEW_TEMP_PUR_CD:
                    tempImpl = new TemporaryPermitImpl();
                    tempImpl.va_temp_permit_To_vha_temp_Permit(status.getAppl_no(), tmgr, true);
                    tableName = TableList.VA_TEMP_PERMIT;
                    break;
                case TableConstants.VM_PMT_SPECIAL_PUR_CD:
                    tempImpl = new TemporaryPermitImpl();
                    tempImpl.va_temp_permit_To_vha_temp_Permit(status.getAppl_no(), tmgr, true);
                    tableName = TableList.VA_TEMP_PERMIT;
                    break;
                case TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD:
                    authImpl = new PermitHomeAuthImpl();
                    authImpl.moveDataIntoVaToVha(tmgr, status.getAppl_no());
                    authImpl.deleteIntoHomeAuth(tmgr, status.getAppl_no(), TableList.VA_PERMIT_HOME_AUTH, null);
                    tableName = TableList.VA_PERMIT_HOME_AUTH;
                    ServerUtil.vatovhNPAuthData(status.getAppl_no(), tmgr);
                    break;
                case TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO:
                    Rereg_Impl.insertIntoReRegHistory(tmgr, status.getAppl_no());
                    //for reuse of receipt amount paid for fancy no
                    //add for restore dispose application
                    sql = "insert into " + TableList.VH_ADVANCE_REGN_NO
                            + " select *,current_timestamp as reopen_on, ? as reopen_by"
                            + " from " + TableList.VT_ADVANCE_REGN_NO
                            + " where regn_appl_no = ? ";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getEmpCode());
                    ps.setString(2, status.getAppl_no());
                    ps.executeUpdate();
                    //end
                    sql = "UPDATE " + TableList.VT_ADVANCE_REGN_NO
                            + " SET regn_appl_no = null WHERE regn_appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status.getAppl_no());
                    ps.executeUpdate();

                    //for reuse of receipt amount paid for retained no
                    //for reuse of receipt amount paid for fancy no
                    sql = "insert into " + TableList.VH_SURRENDER_RETENTION
                            + " select current_timestamp as moved_on, ? as moved_by,*"
                            + " from " + TableList.VT_SURRENDER_RETENTION
                            + " where regn_appl_no = ? ";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, Util.getEmpCode());
                    ps.setString(2, status.getAppl_no());
                    ps.executeUpdate();
                    //end

                    sql = "Update " + TableList.VT_SURRENDER_RETENTION + " Set regn_appl_no = ? Where regn_appl_no = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setNull(1, java.sql.Types.NULL);
                    ps.setString(2, status.getAppl_no());
                    ps.executeUpdate();

                    tableName = TableList.VA_RE_ASSIGN;
                    break;
                case TableConstants.VM_PMT_DUPLICATE_PUR_CD:
                case TableConstants.VM_PMT_DUP_COUNTER_SIGNATURE_PUR_CD:
                    DupImpl.insertIntoDupHistory(tmgr, status.getAppl_no(), status.getPur_cd());
                    tableName = TableList.VA_DUP;
                    break;
                case TableConstants.VM_PMT_SURRENDER_PUR_CD:
                case TableConstants.VM_PMT_TRANSFER_PUR_CD:
                case TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD:
                case TableConstants.VM_PMT_CA_PUR_CD:
                case TableConstants.VM_PMT_REPLACE_VEH_PUR_CD:
                case TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD:
                case TableConstants.VM_PMT_RESTORE_PUR_CD:
                case TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD:
                case TableConstants.VM_PMT_SUSPENSION_PUR_CD:
                    SurrenderPermitImpl.vhaPermitTransactionToVaPermitTransaction(tmgr, status.getAppl_no());
                    if (status.getPur_cd() == TableConstants.VM_PMT_RESTORE_PUR_CD
                            || status.getPur_cd() == TableConstants.VM_PMT_REPLACE_VEH_PUR_CD
                            || status.getPur_cd() == TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD
                            || status.getPur_cd() == TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD
                            || status.getPur_cd() == TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD) {
                        String pmt_status = SurrenderPermitImpl.checkPermitIn_VT_Permit(status.getRegn_no());
                        if (CommonUtils.isNullOrBlank(pmt_status)) {
                            SurrenderPermitImpl.insertVtPermitTranactionToVhPermitTranaction(tmgr, status.getRegn_no());
                        }
                    }
                    tableName = TableList.VA_PERMIT_TRANSACTION;
                    break;
                case TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS:
                case TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE:
                    break;
                case TableConstants.VM_MAST_VEHICLE_SCRAPE:
                    ScrappedVehicleImpl scaImpl = new ScrappedVehicleImpl();
                    scaImpl.insertInVhaScrrapeDetails(null, status, tmgr);
                    tableName = TableList.VA_SCRAP_VEHICLE;
                    break;
                case TableConstants.TM_PURPOSE_FITNESS_CANCELLATION:
                    FitnessCancellationImpl fitnessCancellationImpl = new FitnessCancellationImpl();
                    fitnessCancellationImpl.insertIntoFitCancelHistory(tmgr, status.getAppl_no(), Util.getEmpCode());
                    tableName = TableList.VA_FIT_CANCEL;
                    break;
                case TableConstants.VM_PMT_CANCELATION_PUR_CD:
                case TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD:
                    SurrenderPermitImpl.vhaPermitTransactionToVaPermitTransaction(tmgr, status.getAppl_no());
                    tableName = TableList.VA_PERMIT_TRANSACTION;
                    ServerUtil.vatovhNPAuthData(status.getAppl_no(), tmgr);
                    break;
                case TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD:
                    CounterSignatureImpl counterImp = new CounterSignatureImpl();
                    counterImp.moveVaToVha(tmgr, status.getAppl_no());
                    counterImp.deleteVatables(tmgr, status.getAppl_no(), null, TableList.VA_PERMIT_COUNTERSIGNATURE);
                    break;
                case TableConstants.VM_PMT_RENEW_COUNTER_SIGNATURE_PUR_CD:
                    CounterSignatureImpl counterImpl = new CounterSignatureImpl();
                    counterImpl.moveVaToVha(tmgr, status.getAppl_no());
                    counterImpl.deleteVatables(tmgr, status.getAppl_no(), null, TableList.VA_PERMIT_COUNTERSIGNATURE);
                    break;
                case TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD:
                    PermitEndorsementAppImpl endImp = new PermitEndorsementAppImpl();
                    endImp.revertEndorsmentApplication(tmgr, status.getRegn_no());
                    break;
                case TableConstants.TAX_EXAMPT_PUR_CD:
                    TaxExemptionImpl.insertIntoTaxExemptHistory(tmgr, status.getAppl_no());
                    tableName = TableList.VA_TAX_EXEM;
                    break;
                case TableConstants.TAX_CLEAR_PUR_CD:
                    TaxClearImpl.insertIntoTaxClearHistory(tmgr, status.getAppl_no());
                    tableName = TableList.VA_TAX_CLEAR;
                    break;
                case TableConstants.TAX_INSTALLMENT_PUR_CD:
                    TaxInstallmentConfigImpl.insertIntoTaxInstallmentHistory(tmgr, status.getAppl_no());
                    tableName = TableList.VA_TAX_INSTALLMENT;
                    TaxInstallmentConfigImpl.insertIntoTaxInstallmentBrkHistory(tmgr, status.getAppl_no());
                    TaxInstallmentConfigImpl.deleteVaTaxInsBrk(tmgr, status.getAppl_no());
                    break;
                case TableConstants.VM_DUPLICATE_TO_TAX_CARD:
                    TaxClearanceCertificatePrintImpl.insertIntoTCCHistory(tmgr, status.getAppl_no());
                    tableName = TableList.VA_TCC_PRINT;
                    break;
                case TableConstants.VM_MAST_NON_USE:
                    NonUseImpl impl = new NonUseImpl();
                    impl.moveInHistoryNonUseDetails(tmgr, null, status);
                    tableName = TableList.VA_NON_USE_TAX_EXEM;
                    break;
                case TableConstants.VM_MAST_NON_USE_RESTORE_REMOVE:
                    NonUseImpl nonUseImpl = new NonUseImpl();
                    nonUseImpl.moveInHistoryNonUseDetails(tmgr, null, status);
                    tableName = TableList.VA_NON_USE_RESTORE_REMOVE;
                    break;
                case TableConstants.RE_POSTAL_PUR_CD:
                    break;
                case TableConstants.SWAPPING_REGN_PUR_CD:
                    tableName = TableList.VA_RETENTION;
                    SwappingRegnImpl.insertIntoVaRetentionHistory(tmgr, status.getAppl_no());
                    SwappingRegnImpl.deleteVaRetention(tmgr, status.getAppl_no());
                    break;
                case TableConstants.VM_TRANSACTION_MAST_FIT_REVOKE:
                    FitnessCancellationImpl fitCancellationImpl = new FitnessCancellationImpl();
                    fitCancellationImpl.insertIntoFitRevokeHistory(tmgr, status.getAppl_no(), Util.getEmpCode());
                    tableName = TableList.VA_FIT_REVOKE;
                    break;
                case TableConstants.AGENT_DETAIL_REN_PUR_CD:
                case TableConstants.AGENT_DETAIL_DUP_PUR_CD:
                case TableConstants.AGENT_DETAIL_PUR_CD:
                    AgentDetailImpl agentDetailImpl = new AgentDetailImpl();
                    agentDetailImpl.insertIntoVhaAgentDetails(status.getAppl_no(), tmgr);
                    tableName = TableList.VA_AGENT_DETAILS;
                    break;
                case TableConstants.VM_TRANSACTION_CARRIER_REGN:
                case TableConstants.VM_TRANSACTION_CARRIER_RENEWAL:
                    DetailCommonCarrierImpl detailCommonCarrierImpl = new DetailCommonCarrierImpl();
                    detailCommonCarrierImpl.insertIntoCCHistory(tmgr, status.getAppl_no());
                    tableName = TableList.VA_COMMON_CARRIERS;
                    break;
                case TableConstants.VM_TRANSACTION_MAST_RC_TO_SMART_CARD_CONVERSION:
                    break;
                case TableConstants.VM_MAST_TEMP_RC_CANCEL:
                    TempRcCancelImpl TempCanImpl = new TempRcCancelImpl();
                    TempCanImpl.insertInVhaTempRcCanApplNo(tmgr, status.getAppl_no());
                    tableName = TableList.VA_TEMP_RC_CANCEL;
                    break;
                case TableConstants.DELETE_SMART_CARD_FLAT_FILE:
                    DeleteSmartcardFlatFileDobj deleteDobj = new DeleteSmartcardFlatFileDobj();
                    deleteDobj.setAppl_no(status.getAppl_no());
                    SmartCardImpl.insertIntoDeleteFlatFileHistory(tmgr, deleteDobj);
                    tableName = TableList.VA_REMOVE_RC_BE_TO_BO;
                    break;
                case TableConstants.TAX_EXAMPT_CANCEL_PUR_CD:
                    TaxExemptionImpl.insertIntoTaxExemptCancelHistory(tmgr, status.getAppl_no());
                    tableName = TableList.VA_TAX_EXEM_CANCEL;
                    break;
                case TableConstants.RETENTION_OF_REGISTRATION_NO_PUR_CD:
                    tableName = TableList.VA_SURRENDER_RETENTION;
                    ToImpl tImpl = new ToImpl();
                    tImpl.insertIntoVhaSurrenderRetention(tmgr, status.getAppl_no());
                    break;
                case TableConstants.DUPLICATE_HSRP_PUR_CD:
                    HSRPRequestImpl.insertIntoHsrpHistory(tmgr, status.getRegn_no(), status.getAppl_no());
                    tableName = TableList.VA_HSRP_DUP;
                    break;
                case TableConstants.ENDORSMENT_TAX:
                    ServerUtil.insertIntoVhaTable(tmgr, status.getAppl_no(), Util.getEmpCode(), "vha_endorsement_tax", "va_endorsement_tax");
                    ServerUtil.deleteFromTable(tmgr, null, status.getAppl_no(), "va_endorsement_tax");
                    tableName = "va_endorsement_tax";
                    break;
                case TableConstants.VM_MAST_MANUAL_RECEIPT:
                    ManualReceiptEntryImpl.insertIntoVhaManualReceiptEntry(tmgr, status.getAppl_no());
                    ServerUtil.deleteFromTable(tmgr, null, status.getAppl_no(), "va_manual_receipt");
                    tableName = TableList.VA_MANUAL_RECEIPT;
                    break;
                case TableConstants.VM_MAST_ENFORCEMENT:
                    insertIntoVhChallanTable(tmgr, status.getAppl_no(), "echallan.vh_challan", "echallan.va_challan");
                    insertIntoVhChallanTable(tmgr, status.getAppl_no(), "echallan.vh_challan", "echallan.vt_challan");
                    insertIntoOffencesTable(tmgr, status.getAppl_no(), Util.getEmpCode(), "echallan.vt_vch_offences_hist", "echallan.vt_vch_offences ");
                    ServerUtil.deleteFromTable(tmgr, null, status.getAppl_no(), TableList.VA_CHALLAN);
                    ServerUtil.deleteFromTable(tmgr, null, status.getAppl_no(), TableList.VT_CHALLAN);
                    ServerUtil.deleteFromTable(tmgr, null, status.getAppl_no(), TableList.VT_VCH_OFFENCES);
                    break;
                case TableConstants.MISSING_PERMIT_INFO_PUR_CD:
                    ServerUtil.insertIntoVhaTable(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()), TableList.VHA_TAX_BASED_ON_PERMIT_INFO, TableList.VA_TAX_BASED_ON_PERMIT_INFO);
                    tableName = TableList.VA_TAX_BASED_ON_PERMIT_INFO;
                    break;
                case TableConstants.VM_TRANSACTION_MAST_AUCTION:
                    new AuctionImpl().insertIntoAuctionHistory(tmgr, status.getAppl_no());
                    tableName = TableList.VA_AUCTION;
                    break;
                case TableConstants.VM_TRANSACTION_MAST_ADVERTISEMENT_ON_VEHICLE:
                    break;
                case TableConstants.VM_PMT_PERMIT_PARTICULAR_PUR_CD:
                case TableConstants.VM_PMT_PERMIT_PARTICULAR_PUR_CD_WITHOUT_FEE:
                    break;
                case TableConstants.VM_TRANSACTION_NDC:
                    NdcImpl.insertIntoNDCHistory(tmgr, status.getAppl_no(), String.valueOf(status.getEmp_cd()));
                    tableName = TableList.VA_NDC;
                    break;
                default:
                    throw new VahanException("Entry for This Transaction (Purpose Code-" + status.getPur_cd() + ") is not Available. Please Contact to System Administrator.");
            }

            if (status.getPur_cd() != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS
                    && status.getPur_cd() != TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE
                    && status.getPur_cd() != TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                    && status.getPur_cd() != TableConstants.VM_TRANSACTION_MAST_TEMP_REG
                    && status.getPur_cd() != TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                    && status.getPur_cd() != TableConstants.VM_TRANSACTION_MAST_HPC
                    && status.getPur_cd() != TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE
                    && status.getPur_cd() != TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD
                    && status.getPur_cd() != TableConstants.VM_PMT_RENEW_COUNTER_SIGNATURE_PUR_CD
                    && status.getPur_cd() != TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD
                    && status.getPur_cd() != TableConstants.RE_POSTAL_PUR_CD
                    && status.getPur_cd() != TableConstants.ADMIN_OWNER_DATA_CHANGE
                    && status.getPur_cd() != TableConstants.STATEADMIN_OWNER_DATA_CHANGE
                    && status.getPur_cd() != TableConstants.VM_TRANSACTION_MAST_RC_TO_SMART_CARD_CONVERSION
                    && status.getPur_cd() != TableConstants.VM_MAST_ENFORCEMENT
                    && status.getPur_cd() != TableConstants.VM_TRANSACTION_MAST_ADVERTISEMENT_ON_VEHICLE
                    && status.getPur_cd() != TableConstants.VM_PMT_PERMIT_PARTICULAR_PUR_CD
                    && status.getPur_cd() != TableConstants.VM_PMT_PERMIT_PARTICULAR_PUR_CD_WITHOUT_FEE) {
                ServerUtil.deleteFromTable(tmgr, null, status.getAppl_no(), tableName);
            }
            //add for restore dispose application
            sql = "insert into vh_special_order select current_timestamp as moved_on, ? as moved_by,a.* from vt_special_order a WHERE a.regn_no=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, status.getRegn_no());
            ps.executeUpdate();
            //End
            sql = "UPDATE " + TableList.VT_SPECIAL_ORDER + " SET appl_no=?,op_dt=current_timestamp"
                    + " WHERE regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setNull(1, java.sql.Types.VARCHAR);
            ps.setString(2, status.getRegn_no());
            ps.executeUpdate();

            if (sizsOfApplInwarded == sizeOfSelectedApplforDispose) {
                InsImpl.insertIntoInsuranceHistory(tmgr, status.getAppl_no(), null);
                ServerUtil.deleteFromTable(tmgr, null, status.getAppl_no(), TableList.VA_INSURANCE);
            }

            //for handling HPC/Duplicate TO TAX fee cancellation because there is no next flow for HPC/Duplicate TO TAX
            if (status.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_HPC
                    || status.getPur_cd() == TableConstants.VM_DUPLICATE_TO_TAX_CARD) {
                sql = " INSERT INTO " + TableList.VHA_STATUS
                        + "       SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno+1,"
                        + "       action_cd, seat_cd, cntr_id, ? as status, ? as office_remark, public_remark,"
                        + "       file_movement_type, ? as emp_cd, op_dt, current_timestamp as moved_on,? "
                        + "       FROM " + TableList.VA_STATUS
                        + " WHERE appl_no=? and pur_cd=?";
            } else if ((status.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD
                    || status.getPur_cd() == TableConstants.VM_PMT_APPLICATION_PUR_CD)
                    && pmtFlag) {
                sql = " INSERT INTO " + TableList.VHA_STATUS
                        + "       SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno+1,"
                        + "       action_cd, seat_cd, cntr_id, ? as status, ? as office_remark, public_remark,"
                        + "       file_movement_type, ? as emp_cd, op_dt, current_timestamp as moved_on,? "
                        + "       FROM " + TableList.VA_STATUS
                        + " WHERE appl_no=?";
            } else {
                sql = " INSERT INTO " + TableList.VHA_STATUS
                        + "       SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno,"
                        + "       action_cd, seat_cd, cntr_id, ? as status, ? as office_remark, public_remark,"
                        + "       file_movement_type, ? as emp_cd, op_dt, current_timestamp as moved_on,? "
                        + "       FROM " + TableList.VA_STATUS
                        + " WHERE appl_no=? and pur_cd=?";
            }
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, TableConstants.DISPOSE);
            ps.setString(i++, status.getOffice_remark());
            ps.setLong(i++, status.getEmp_cd());
            ps.setString(i++, Util.getClientIpAdress());
            ps.setString(i++, status.getAppl_no());
            if (!((status.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD
                    || status.getPur_cd() == TableConstants.VM_PMT_APPLICATION_PUR_CD)
                    && pmtFlag)) {
                ps.setInt(i++, status.getPur_cd());
            }
            ps.executeUpdate();

            if ((status.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD
                    || status.getPur_cd() == TableConstants.VM_PMT_APPLICATION_PUR_CD)
                    && pmtFlag) {
                sql = "DELETE FROM " + TableList.VA_STATUS + " WHERE appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, status.getAppl_no());
                ps.executeUpdate();
            } else {
                sql = "DELETE FROM " + TableList.VA_STATUS + " WHERE appl_no=? and pur_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, status.getAppl_no());
                ps.setInt(2, status.getPur_cd());
                ps.executeUpdate();
            }

            if ((status.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD
                    || status.getPur_cd() == TableConstants.VM_PMT_APPLICATION_PUR_CD)
                    && pmtFlag) {
                sql = "INSERT INTO " + TableList.VHA_DETAILS
                        + "     SELECT a.*,current_timestamp as moved_on,? as moved_by FROM " + TableList.VA_DETAILS
                        + "     a WHERE a.appl_no=?";
            } else {
                sql = "INSERT INTO " + TableList.VHA_DETAILS
                        + "     SELECT a.*,current_timestamp as moved_on,? as moved_by FROM " + TableList.VA_DETAILS
                        + "     a WHERE a.appl_no=? and pur_cd=?";
            }
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, status.getEmp_cd());
            ps.setString(2, status.getAppl_no());
            if (!((status.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD
                    || status.getPur_cd() == TableConstants.VM_PMT_APPLICATION_PUR_CD)
                    && pmtFlag)) {
                ps.setInt(3, status.getPur_cd());
            }
            ps.executeUpdate();

            if ((status.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD
                    || status.getPur_cd() == TableConstants.VM_PMT_APPLICATION_PUR_CD)
                    && pmtFlag) {
                sql = "DELETE FROM " + TableList.VA_DETAILS + " WHERE appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, status.getAppl_no());
                ps.executeUpdate();
            } else {
                sql = "DELETE FROM " + TableList.VA_DETAILS + " WHERE appl_no=? and pur_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, status.getAppl_no());
                ps.setInt(2, status.getPur_cd());
                ps.executeUpdate();
            }

            //********for disposing online application 
            sql = "INSERT INTO " + TableList.VHA_STATUS_APPL
                    + " SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno,"
                    + "       action_cd, seat_cd, cntr_id, status, office_remark, public_remark,"
                    + "       file_movement_type, ? as emp_cd, op_dt, moved_from_online, current_timestamp as moved_on"
                    + "  FROM " + TableList.VA_STATUS_APPL + " where appl_no=? and pur_cd=? ";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, status.getEmp_cd());
            ps.setString(2, status.getAppl_no());
            ps.setInt(3, status.getPur_cd());
            ps.executeUpdate();

            sql = "Delete FROM " + TableList.VA_STATUS_APPL + " WHERE appl_no=? and pur_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, status.getAppl_no());
            ps.setInt(2, status.getPur_cd());
            ps.executeUpdate();

            sql = "INSERT INTO " + TableList.VHA_DETAILS_APPL
                    + " SELECT appl_no, pur_cd, appl_dt, regn_no, user_id, user_type, entry_ip, "
                    + "        entry_status, confirm_ip, confirm_status, confirm_date, state_cd, "
                    + "        off_cd, current_timestamp as moved_on, ? as moved_by"
                    + "  FROM " + TableList.VA_DETAILS_APPL
                    + " WHERE appl_no=? and pur_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setLong(1, status.getEmp_cd());
            ps.setString(2, status.getAppl_no());
            ps.setInt(3, status.getPur_cd());
            ps.executeUpdate();

            sql = "Delete FROM " + TableList.VA_DETAILS_APPL + " WHERE appl_no=? and pur_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, status.getAppl_no());
            ps.setInt(2, status.getPur_cd());
            ps.executeUpdate();

            if (status.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                new OwnerChoiceNoImpl().insertIntoHistoryChoiceNumber(tmgr, String.valueOf(status.getEmp_cd()), TableConstants.DISPOSE, status.getAppl_no(), status.getState_cd());
            }

            //**************************************
            tmgr.commit();//Commiting data here....
            if (ownerDobj != null && ownerDobj.getOwner_identity() != null && ownerDobj.getOwner_identity().getMobile_no() != null && ownerDobj.getOwner_identity().getMobile_no() != 0 && (status.getPur_cd() == TableConstants.VM_PMT_APPLICATION_PUR_CD || status.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD)) {
                ServerUtil.sendSMS(String.valueOf(ownerDobj.getOwner_identity().getMobile_no()), "Application No " + status.getAppl_no() + " for grant of permit against the vehicle no " + ownerDobj.getRegn_no() + " has been rejected on " + current_date + ".");
            }
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
    }

    public void saveSkipFee(Status_dobj status, String receiptNo) throws Exception {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;

        try {
            tmgr = new TransactionManager("disposeApplications");

            sql = "SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno,"
                    + "  action_cd, seat_cd, cntr_id, "
                    + "  file_movement_type, emp_cd "
                    + "  FROM VA_STATUS where appl_no=? and pur_cd=? and action_cd in (?,?,?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, status.getAppl_no());
            ps.setInt(2, status.getPur_cd());
            ps.setInt(3, TableConstants.TM_ROLE_NEW_REGISTRATION_FIT_FEE);
            ps.setInt(4, TableConstants.TM_ACTION_REGISTERED_VEH_FEE);
            ps.setInt(5, TableConstants.TM_ROLE_PMT_FEE);

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                status.setAction_cd(rs.getInt("action_cd"));
                status.setPrev_action_cd(rs.getInt("action_cd"));
                status.setFlow_slno(rs.getInt("flow_slno"));
                status.setFile_movement_slno(rs.getInt("file_movement_slno"));
                status.setFile_movement_type(rs.getString("file_movement_type"));
            } else {
                throw new VahanException("File is Already Moved");
            }

            int i = 1;
            sql = "INSERT INTO " + TableList.VT_FEE_EXEMPTED
                    + " (state_cd, off_cd, appl_no, rcpt_no, pur_cd, flow_slno, file_movement_slno, "
                    + "  action_cd, status, office_remark, public_remark, file_movement_type, emp_cd, op_dt)"
                    + "  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(i++, status.getState_cd());
            ps.setInt(i++, status.getOff_cd());
            ps.setString(i++, status.getAppl_no());
            ps.setString(i++, receiptNo);
            ps.setInt(i++, status.getPur_cd());
            ps.setInt(i++, status.getFlow_slno());
            ps.setInt(i++, status.getFile_movement_slno());
            ps.setInt(i++, status.getAction_cd());
            ps.setString(i++, status.getStatus());
            ps.setString(i++, status.getOffice_remark());
            ps.setString(i++, status.getPublic_remark());
            ps.setString(i++, status.getFile_movement_type());
            ps.setLong(i++, Long.parseLong(Util.getEmpCode()));

            ps.executeUpdate();

            if (status.getPur_cd() == TableConstants.VM_PMT_FRESH_PUR_CD
                    || status.getPur_cd() == TableConstants.VM_PMT_RENEWAL_PUR_CD) {
                sql = "UPDATE " + TableList.VA_PERMIT + " SET rcpt_no = ? where appl_no = ?";
                ps = tmgr.prepareStatement(sql);
                i = 1;
                ps.setString(i++, receiptNo);
                ps.setString(i++, status.getAppl_no());
                ps.executeUpdate();
            }

            ServerUtil.webServiceForNextStage(status, tmgr);
            ServerUtil.fileFlow(tmgr, status);

            tmgr.commit();//Commiting data here....
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
    }

    public static Map<String, Integer> listApplicationDispose(String appl_no, String state_cd, int off_cd, long userCode, String userCatg) {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        StringBuilder sql = null;
        StringBuilder whereConditionVar = null;

        StringBuilder selectVar = new StringBuilder("SELECT a.pur_cd,a.descr from tm_purpose_mast a "
                + " left join va_status status on a.pur_cd = status.pur_cd  "
                + " left join va_details detail on a.pur_cd = detail.pur_cd and status.appl_no=detail.appl_no ");

        if (userCatg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER)) {
            whereConditionVar = new StringBuilder(" WHERE (a.inward_appl='Y' OR a.pur_cd IN "
                    + " (" + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                    + " ," + TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE
                    + " )) "
                    + "  and status.appl_no=? and (detail.entry_status <> 'A' or detail.entry_status is null) and status.state_cd=? and status.off_cd=? ");
        } else {
            whereConditionVar = new StringBuilder(" WHERE (a.inward_appl='Y' OR a.pur_cd IN "
                    + " (" + TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                    + " ," + TableConstants.VM_TRANSACTION_MAST_TEMP_REG
                    + " ," + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                    + " ," + TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE
                    + " ," + TableConstants.VM_TRANSACTION_TRADE_CERT_NEW
                    + " ," + TableConstants.VM_TRANSACTION_TRADE_CERT_DUP
                    + " ," + TableConstants.VM_TRANSACTION_TRADE_CERT_ONLINE_APPLICATION
                    + " ," + TableConstants.VM_CANCEL_TRADE_CERT
                    + " ," + TableConstants.VM_PMT_FRESH_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_RENEWAL_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_TEMP_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_RENEW_TEMP_PUR_CD // Added by Manoj
                    + " ," + TableConstants.VM_PMT_SPECIAL_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_SURRENDER_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_CANCELATION_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_TRANSFER_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_SUSPENSION_PUR_CD // Added by Manoj
                    + " ," + TableConstants.VM_PMT_CA_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_REPLACE_VEH_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_TRANSFER_REPLACE_OTHER_OFFICE_PUR_CD // Added by Manoj
                    + " ," + TableConstants.VM_PMT_RESTORE_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_APPLICATION_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_DUPLICATE_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_COUNTER_SIGNATURE_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_RENEW_COUNTER_SIGNATURE_PUR_CD // Added by Manoj
                    + " ," + TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD // Added by Naman
                    + " ," + TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD // Added by NAMAN
                    + " ," + TableConstants.TAX_EXAMPT_PUR_CD // Added by Niraj
                    + " ," + TableConstants.TAX_CLEAR_PUR_CD // Added by Ankur
                    + " ," + TableConstants.TAX_INSTALLMENT_PUR_CD // Added by Ankur
                    + " ," + TableConstants.VM_DUPLICATE_TO_TAX_CARD // Added by Ankur
                    + " ," + TableConstants.SWAPPING_REGN_PUR_CD // Added by Afzal
                    + " ," + TableConstants.ADMIN_OWNER_DATA_CHANGE
                    + " ," + TableConstants.AGENT_DETAIL_PUR_CD
                    + " ," + TableConstants.AGENT_DETAIL_DUP_PUR_CD
                    + " ," + TableConstants.AGENT_DETAIL_REN_PUR_CD
                    + " ," + TableConstants.VM_TRANSACTION_CARRIER_REGN
                    + " ," + TableConstants.VM_TRANSACTION_CARRIER_RENEWAL
                    + " ," + TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE_FITNESS
                    + " ," + TableConstants.VM_MAST_TEMP_RC_CANCEL
                    + " ," + TableConstants.RE_POSTAL_PUR_CD
                    + " ," + TableConstants.DELETE_SMART_CARD_FLAT_FILE
                    + " ," + TableConstants.DUPLICATE_HSRP_PUR_CD
                    + " ," + TableConstants.ENDORSMENT_TAX
                    + " ," + TableConstants.VM_MAST_MANUAL_RECEIPT
                    + " ," + TableConstants.VM_MAST_ENFORCEMENT
                    + " ," + TableConstants.MISSING_PERMIT_INFO_PUR_CD
                    + " ," + TableConstants.VM_TRANSACTION_MAST_AUCTION
                    + " ," + TableConstants.VM_TRANSACTION_MAST_ADVERTISEMENT_ON_VEHICLE
                    + " ," + TableConstants.STATEADMIN_OWNER_DATA_CHANGE
                    + " )) "
                    + "  and status.appl_no=? and (detail.entry_status <> 'A' OR (detail.entry_status = 'A' AND detail.pur_cd = 25) or detail.entry_status is null) and status.state_cd=? and status.off_cd=? ");
        }
        if (appl_no != null) {
            appl_no = appl_no.toUpperCase().trim();
        }

        Map<String, Integer> purMap = new LinkedHashMap<>();

        try {
            tmgr = new TransactionManager("listApplicationDispose");
            if (userCatg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER)) {
                sql = selectVar.append(" left join va_owner vo on vo.appl_no=detail.appl_no \n"
                        + " left join tm_user_permissions p on p.dealer_cd=vo.dealer_cd").append(whereConditionVar).append("and p.user_cd = ?");
            } else {
                sql = selectVar.append(whereConditionVar);
            }
            ps = tmgr.prepareStatement(sql.toString());
            ps.setString(1, appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            if (userCatg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER)) {
                ps.setLong(4, userCode);
            }
            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next())//found
            {
                purMap.put(rs.getString("descr"), rs.getInt("pur_cd")); //label,value
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

        return purMap;
    }

    public static int getActionCode(String appl_no) {

        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        int actionCd = 0;
        try {

            tmgr = new TransactionManager("getActionCode");
            sql = "SELECT action_cd from " + TableList.VA_STATUS
                    + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                actionCd = rs.getInt("action_cd");
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
        return actionCd;
    }

    public boolean disposeSameDayCheck(String appl_no, String stateCd, int offCd) {

        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        boolean isSameDayDispose = false;
        try {

            tmgr = new TransactionManager("disposeSameDayCheck");

            sql = "select rcpt_dt from " + TableList.VT_FEE
                    + " where rcpt_no in (SELECT distinct(rcpt_no) from " + TableList.VP_APPL_RCPT_MAPPING
                    + " where appl_no =?) and state_cd=? and off_cd=? and rcpt_dt::date=current_date "
                    + " union "
                    + " select rcpt_dt from " + TableList.VT_TAX
                    + " where  rcpt_no in (SELECT distinct(rcpt_no) from " + TableList.VP_APPL_RCPT_MAPPING
                    + " where appl_no =?) and state_cd=? and off_cd=? and rcpt_dt::date=current_date";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.setString(4, appl_no);
            ps.setString(5, stateCd);
            ps.setInt(6, offCd);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                isSameDayDispose = true;
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
        return isSameDayDispose;
    }

    public String getlistOfOffCdForPermit(String appl_no, String state_cd, int off_cd) {
        String listOffCd = null;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManagerReadOnly("getlistOfOffCdForPermit");
            String Query = "SELECT allotted_off_cd from " + TableList.VM_OFF_ALLOTMENT + " a "
                    + " left join va_status status on a.state_cd = status.state_cd AND a.off_cd = status.off_cd"
                    + " WHERE status.pur_cd IN "
                    + " ( "
                    + TableConstants.VM_PMT_FRESH_PUR_CD
                    + " ," + TableConstants.VM_PMT_RENEWAL_PUR_CD
                    + " ," + TableConstants.VM_PMT_TEMP_PUR_CD
                    + " ," + TableConstants.VM_PMT_SPECIAL_PUR_CD
                    + " ," + TableConstants.VM_PMT_RENEW_TEMP_PUR_CD
                    + " ," + TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD
                    + " ," + TableConstants.VM_PMT_SURRENDER_PUR_CD
                    + " ," + TableConstants.VM_PMT_CANCELATION_PUR_CD
                    + " ," + TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD
                    + " ," + TableConstants.VM_PMT_TRANSFER_PUR_CD
                    + " ," + TableConstants.VM_PMT_CA_PUR_CD
                    + " ," + TableConstants.VM_PMT_REPLACE_VEH_PUR_CD
                    + " ," + TableConstants.VM_PMT_RESTORE_PUR_CD
                    + " ," + TableConstants.VM_PMT_APPLICATION_PUR_CD
                    + " ," + TableConstants.VM_PMT_DUPLICATE_PUR_CD
                    + " ," + TableConstants.VM_PMT_PERMANENT_SURRENDER_PUR_CD
                    + " ," + TableConstants.VM_PMT_VARIATION_ENDORSEMENT_PUR_CD
                    + " ," + TableConstants.VM_PMT_RE_ASSIGMENT_PUR_CD
                    + " ) "
                    + " and status.appl_no=? and a.state_cd=? and a.off_cd=?";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, appl_no);
            ps.setString(i++, state_cd);
            ps.setInt(i++, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                listOffCd = rs.getString("allotted_off_cd");
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
        return listOffCd;
    }

    /**
     * Skip Step from Fee to Next
     *
     * @param appl_no
     * @param receiptNo
     * @param state_cd
     * @param off_cd
     * @return
     * @throws VahanException
     */
    public static Map<String, Integer> mapPurCdDescr(String appl_no, String receiptNo, String state_cd, int off_cd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;

        if (appl_no != null) {
            appl_no = appl_no.toUpperCase().trim();
        }

        Map<String, Integer> purMap = new LinkedHashMap<>();

        try {
            tmgr = new TransactionManager("listApplicationDispose");

            sql = "SELECT distinct a.pur_cd,a.descr from tm_purpose_mast a "
                    + " left join va_status status on a.pur_cd = status.pur_cd "
                    + " left join va_details detail on a.pur_cd in (detail.pur_cd,2) and status.appl_no=detail.appl_no "
                    + " WHERE  status.appl_no=? and (detail.entry_status <> 'A' or detail.entry_status is null) "
                    + " and status.state_cd=? and status.off_cd=? and status.action_cd in (?,?,?) ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            ps.setInt(4, TableConstants.TM_ROLE_NEW_REGISTRATION_FIT_FEE);
            ps.setInt(5, TableConstants.TM_ACTION_REGISTERED_VEH_FEE);
            ps.setInt(6, TableConstants.TM_ROLE_PMT_FEE);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            String pur_cd = "";
            while (rs.next())//found
            {
                purMap.put(rs.getString("descr"), rs.getInt("pur_cd")); //label,value 
                pur_cd += rs.getInt("pur_cd") + ",";

                sql = "Select a.*,b.appl_no,\n"
                        + " case when c.old_fee_valid_mod = 'Y' then a.rcpt_dt + (c.old_fee_valid_period::text || ' year')::interval\n"
                        + "     when c.old_fee_valid_mod = 'M' then a.rcpt_dt + (c.old_fee_valid_period::text || ' month')::interval\n"
                        + "     when c.old_fee_valid_mod = 'D' then a.rcpt_dt + (c.old_fee_valid_period::text || ' day')::interval\n"
                        + "     else a.rcpt_dt END as valid_range,current_date as current_date\n"
                        + " from " + TableList.VT_FEE + " a \n"
                        + " left join " + TableList.VP_APPL_RCPT_MAPPING + " b on a.rcpt_no=b.rcpt_no and a.state_cd=b.state_cd and a.off_cd=b.off_cd \n"
                        + " left join " + TableList.TM_CONFIGURATION + " c on  a.state_cd = c.state_cd\n"
                        + " where a.rcpt_no=? and a.state_cd=? and a.off_cd=?";
                if (rs.getInt("pur_cd") == TableConstants.VM_PMT_SURRENDER_PUR_CD) {
                    sql += " and a.pur_cd in(?,?,?,?,?)";
                } else if (rs.getInt("pur_cd") == TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD
                        || rs.getInt("pur_cd") == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD) {
                    sql += " and a.pur_cd in(?,?)";
                } else {
                    sql += " and a.pur_cd in(?,2)";
                }
                ps = tmgr.prepareStatement(sql);
                int i = 1;
                ps.setString(i++, receiptNo);
                ps.setString(i++, state_cd);
                ps.setInt(i++, off_cd);
                if (rs.getInt("pur_cd") == TableConstants.VM_PMT_SURRENDER_PUR_CD) {
                    ps.setInt(i++, TableConstants.VM_PMT_TRANSFER_PUR_CD);
                    ps.setInt(i++, TableConstants.VM_PMT_TRANSFER_DEATH_CASE_PUR_CD);
                    ps.setInt(i++, TableConstants.VM_PMT_TRANSFER_REPLACE_VEH_PUR_CD);
                    ps.setInt(i++, TableConstants.VM_PMT_REPLACE_VEH_PUR_CD);
                    ps.setInt(i++, rs.getInt("pur_cd"));
                } else if (rs.getInt("pur_cd") == TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD
                        || rs.getInt("pur_cd") == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD) {
                    ps.setInt(i++, TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD);
                    ps.setInt(i++, TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD);
                } else {
                    ps.setInt(i++, rs.getInt("pur_cd"));
                }

                RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                //If Fee is collected in Vahan2 but Not in Vahan 4
                if (rs1.next()) {
                    if (rs1.getDate("valid_range").compareTo(rs1.getDate("current_date")) < 1) {
                        throw new VahanException("Receipt Expired");
                    }
                    String apNo = rs1.getString("appl_no");
                    if (apNo != null && !apNo.isEmpty()) {
                        throw new VahanException("Fee Is Not Collected from Older Version Software");
                    }
                } else {
                    throw new VahanException("Invalid Receipt Number");
                }
            }
            if (!pur_cd.isEmpty()) {
                pur_cd = pur_cd.substring(0, pur_cd.lastIndexOf(","));

                sql = "Select * from " + TableList.VT_FEE_EXEMPTED + " where rcpt_no=? and state_cd=? and off_cd=? and pur_cd in (" + pur_cd + ")";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, receiptNo);
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    throw new VahanException("Skipped information for Receipt is already saved");
                }
            }

            if (purMap.isEmpty()) {
                throw new VahanException("There is no Valid Transaction for this Application No");
            }

        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Fetching of Details");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return purMap;
    }

    public static void checkPmtAppIsApprove(String appl_no) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps;
        try {
            tmgr = new TransactionManager("checkPmtAppIsApprove");
            String Query = "select entry_status from " + TableList.VA_DETAILS + " where appl_no = ? and pur_cd = ?";
            ps = tmgr.prepareStatement(Query);
            int i = 1;
            ps.setString(i++, appl_no);
            ps.setInt(i++, TableConstants.VM_PMT_APPLICATION_PUR_CD);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if (rs.getString("entry_status").equalsIgnoreCase("A")) {
                    throw new VahanException("Permit Application is approved by department, therefore application can not be disposed");
                }
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
    }

    public boolean dealerCheckForDispose(String appl_no) throws VahanException {

        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        boolean dealerCheckForDispose = false;
        try {

            tmgr = new TransactionManager("dealerCheckForDispose");

            sql = "SELECT distinct vp.appl_no FROM " + TableList.VPH_RCPT_CART
                    + " vp  inner join " + TableList.VHA_STATUS
                    + " s on vp.appl_no = s.appl_no WHERE vp.appl_no = ? and s.action_cd IN (" + TableConstants.TM_ROLE_DEALER_CART_PAYMENT + "," + TableConstants.TM_ROLE_NEW_REGISTRATION_FIT_FEE + "," + TableConstants.TM_ROLE_NEW_REGISTRATION_FEE + "," + TableConstants.TM_ROLE_DEALER_NEW_REGISTRATION_FIT_FEE + ")";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                dealerCheckForDispose = true;
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Fetching of Details");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return dealerCheckForDispose;
    }

    public boolean dealerCheckForTempRegDispose(String applNo) {

        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        boolean dealerCheckForTempRegnDispose = false;
        try {

            tmgr = new TransactionManager("dealerCheckForDispose");

            sql = "SELECT pur_cd from " + TableList.VA_DETAILS
                    + " where appl_no =? and pur_cd not in (124,18)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                dealerCheckForTempRegnDispose = true;
            }

        } catch (Exception ex) {
            dealerCheckForTempRegnDispose = true;
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
        return dealerCheckForTempRegnDispose;
    }

    public boolean disposeOnlinePaymentOptedCheck(String appl_no) {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        boolean isOnlinePayment = false;
        try {

            tmgr = new TransactionManager("disposeOnlinePaymentOptedCheck");
            sql = "SELECT user_cd from vp_online_pay_user_info where user_cd = (Select user_cd from vp_rcpt_cart where appl_no = ? limit 1)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                isOnlinePayment = true;
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
        return isOnlinePayment;
    }

    public ApplDisposeVerifyByAdminDobj getApplicationDisposedVerificationDetails(String appl_no) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        ApplDisposeVerifyByAdminDobj applDisposeVerifyByAdminDobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("getApplicationDisposedVerificationDetails");
            sql = "SELECT * FROM " + TableList.VA_APPL_DISPOSE_VERIFY_BY_ADMIN + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) // found
            {
                applDisposeVerifyByAdminDobj = new ApplDisposeVerifyByAdminDobj();
                applDisposeVerifyByAdminDobj.setAppl_no(rs.getString("appl_no"));
                applDisposeVerifyByAdminDobj.setState_cd(rs.getString("state_cd"));
                applDisposeVerifyByAdminDobj.setOff_cd(rs.getInt("off_cd"));
                applDisposeVerifyByAdminDobj.setRemarks(rs.getString("remarks"));
                //emp_cd,op_dt remaining for setting
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            applDisposeVerifyByAdminDobj = null;
            throw new VahanException("Something went wrong during fetching details of verification for dispose on the Application No-" + appl_no + ",Please contact to the System Admininstrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return applDisposeVerifyByAdminDobj;
    }

    public void ApplicationDisposeVerification(ApplDisposeVerifyByAdminDobj verifyByAdminDobj) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = "";
        try {
            tmgr = new TransactionManager("ApplicationDisposeVerification");
            sql = "INSERT INTO " + TableList.VA_APPL_DISPOSE_VERIFY_BY_ADMIN
                    + " (appl_no, state_cd, off_cd, remarks, emp_cd, op_dt)"
                    + " VALUES (?,?,?,?,?,current_timestamp)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, verifyByAdminDobj.getAppl_no());
            ps.setString(2, verifyByAdminDobj.getState_cd());
            ps.setInt(3, verifyByAdminDobj.getOff_cd());
            ps.setString(4, verifyByAdminDobj.getRemarks());
            ps.setString(5, verifyByAdminDobj.getEmp_cd());
            ps.executeUpdate();
            tmgr.commit();
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
    }
    //For challan application dispose insert into vh_challan to va_challan

    public static void insertIntoVhChallanTable(TransactionManager tmgr, String applNo, String tableNameVha, String tableNameVa) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        if (applNo != null && applNo.trim().length() > 0) {
            applNo = applNo.toUpperCase().trim();
            sql = " INSERT INTO " + tableNameVha
                    + "  SELECT appl_no, chal_no, regn_no, chal_date, chal_time, chal_place, \n"
                    + "       is_doc_impound, is_vch_impdound, chal_officer, nc_c_ref_no, remarks, \n"
                    + "       'Dispose Application',op_dt, state_cd, off_cd, comming_from, going_to, settled_spot\n"
                    + "  FROM " + tableNameVa
                    + " WHERE appl_no=? ";
        }
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, applNo);
        ps.executeUpdate();
    }

    public static void insertIntoOffencesTable(TransactionManager tmgr, String applNo, String empCode, String tableNameVha, String tableNameVa) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        if (applNo != null && applNo.trim().length() > 0) {
            applNo = applNo.toUpperCase().trim();
            sql = " INSERT INTO " + tableNameVha
                    + " SELECT appl_no, offence_cd, accused_catg, offence_amt, remarks, da_status, "
                    + " offence_status, state_cd, off_cd, statement_timestamp() as moved_on, ? as moved_by, section_cd "
                    + "  FROM " + tableNameVa
                    + " WHERE appl_no=? ";
        }
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, empCode);
        ps.setString(2, applNo);
        ps.executeUpdate();

    }
}//End of Class ApplicationDisposeImpl
