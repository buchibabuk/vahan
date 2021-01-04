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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.fillVehicleParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.ConvDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.common.RegistrationStatusParametersDobj;
import nic.vahan.form.dobj.configuration.OnlineConfigurationDobj;
import nic.vahan.form.dobj.configuration.TmConfigApplAppointmentInward;
import nic.vahan.form.dobj.configuration.TmConfigurationApplInwardDobj;
import nic.vahan.form.dobj.eChallan.ChallanReportDobj;
import nic.vahan.form.dobj.fitness.TmConfigurationFitnessDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

public class ApplicationInwardImpl {

    private static final Logger LOGGER = Logger.getLogger(ApplicationInwardImpl.class);

    public String saveAndGenerateApplication(List<Status_dobj> status_dobj, List<Status_dobj> onlineApplDataList, OwnerDetailsDobj dobj,
            String changedData, RegistrationStatusParametersDobj status, String insuranceComparison, ConvDobj convDobj) throws VahanException {

        TransactionManager tmgr = null;
        String appl_no = null;
        int action_cd = 0;
        int actionCodeArray[] = null;
        VehicleParameters parameters = null;
        String userId = "";
        boolean skipInsuranceCheck = false;
        try {
            tmgr = new TransactionManager("saveAndGenerateApplication");
            OwnerImpl impl = new OwnerImpl();
            Owner_dobj ownerDobj = impl.getOwnerDobj(dobj);

            if (onlineApplDataList == null || onlineApplDataList.isEmpty()) {
                appl_no = ServerUtil.getUniqueApplNo(tmgr, status_dobj.get(0).getState_cd());// generate a new application no.
                parameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
            } else {
                appl_no = onlineApplDataList.get(0).getAppl_no();
                String entryIp = "";
                String offRemarks = "";
                entryIp = status_dobj.get(0).getEntry_ip();
                userId = status_dobj.get(0).getUser_id();
                offRemarks = status_dobj.get(0).getOffice_remark();
                status_dobj.clear();
                for (int j = 0; j < onlineApplDataList.size(); j++) {
                    onlineApplDataList.get(j).setUser_id(userId);
                    onlineApplDataList.get(j).setEntry_ip(entryIp);
                    if (j == 0) {
                        offRemarks = offRemarks + onlineApplDataList.get(j).getOffice_remark();
                    }
                    onlineApplDataList.get(j).setOffice_remark(offRemarks);
                    if (Util.getUserCategory() != null && Util.getUserCategory().equalsIgnoreCase(TableConstants.USER_CATG_FITNESS_CENTER_USER)) {
                        onlineApplDataList.get(j).setOff_cd(Util.getSelectedSeat().getOff_cd());
                        status_dobj.add(onlineApplDataList.get(j));
                    } else {
                        status_dobj.add(onlineApplDataList.get(j));
                    }
                }
            }

            for (int i = 0; i < status_dobj.size(); i++) {

                if (onlineApplDataList == null || onlineApplDataList.isEmpty()) {
                    if (status_dobj.get(i).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {
                        FitnessImpl fitness_Impl = new FitnessImpl();//state configuration for fitness according to the States rules
                        TmConfigurationFitnessDobj tmConfigFitnessDobj = fitness_Impl.getFitnessConfiguration(status_dobj.get(i).getState_cd());
                        if (dobj.getFitnessDobj() == null) {
                            FitnessImpl fitImpl = new FitnessImpl();
                            FitnessDobj fitnessDobj = fitImpl.set_Fitness_appl_db_to_dobj(dobj.getRegn_no(), null);
                            dobj.setFitnessDobj(fitnessDobj);
                        }
                        if (tmConfigFitnessDobj != null
                                && tmConfigFitnessDobj.isSkip_fee_for_failed_fitness()
                                && dobj.getFitnessDobj() != null
                                && dobj.getFitnessDobj().getFit_result() != null
                                && dobj.getFitnessDobj().getFit_chk_dt() != null
                                && dobj.getFitnessDobj().getFit_result().equalsIgnoreCase(TableConstants.FitnessResultFail)) {
                            long graceDays = DateUtils.getDate1MinusDate2_Days(DateUtils.parseDate(DateUtils.getDateInDDMMYYYY(dobj.getFitnessDobj().getFit_chk_dt())), DateUtils.parseDate(DateUtils.getDateInDDMMYYYY(new java.util.Date())));
                            parameters.setDELAY_DAYS((int) graceDays);
                            parameters.setFIT_STATUS(dobj.getFitnessDobj().getFit_result().toUpperCase());

                            if (tmConfigFitnessDobj.isCheck_for_multiple_time_failed_fitness()) {
                                FitnessDobj fitnessHistoryDobj = fitness_Impl.set_FitnessHist_appl_db_to_dobj(dobj.getRegn_no());
                                if (fitnessHistoryDobj != null && fitnessHistoryDobj.getFit_result() != null
                                        && fitnessHistoryDobj.getFit_result().equalsIgnoreCase(TableConstants.FitnessResultFail)) {
                                    parameters.setDELAY_DAYS(tmConfigFitnessDobj.getGrace_days_for_failed_fitness() + 1);
                                }
                            }
                        }
                    }

                    actionCodeArray = ServerUtil.getInitialAction(tmgr, status_dobj.get(i).getState_cd(), status_dobj.get(i).getPur_cd(), parameters);

                    if (actionCodeArray == null) {
                        throw new VahanException("Initial Action Code is Not Available!");
                    }

                    action_cd = actionCodeArray[0];
                    status_dobj.get(i).setAppl_no(appl_no);
                    status_dobj.get(i).setAction_cd(action_cd);//Initial Action_cd
                    status_dobj.get(i).setFlow_slno(actionCodeArray[1]);//initial flow serial no.
                    status_dobj.get(i).setFile_movement_slno(actionCodeArray[1]);//initial file movement serial no.
                } else {
                    updateOnlineApplStatus(tmgr, status_dobj.get(i));

                }
                TmConfigurationApplInwardDobj tmConfigurationApplInwardDobj = getApplInwardAnywhereInStateConfig(Util.getUserStateCode());
                boolean toWithNOC = false;
                if (tmConfigurationApplInwardDobj != null) {
                    toWithNOC = tmConfigurationApplInwardDobj.isCheck_for_to_with_noc();
                }
                if (toWithNOC && status_dobj.get(i).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_TO) {
                    insertNOCDeatilsForTO(dobj, appl_no, userId, status_dobj.get(i).getUser_type(), tmgr);
                }
                ServerUtil.insertIntoVaStatus(tmgr, status_dobj.get(i));
                ServerUtil.insertIntoVaDetails(tmgr, status_dobj.get(i));
                if ((status_dobj.get(i).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS || status_dobj.get(i).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_RC_PARTICULARS_FOR_OFFICE_PURPOSE) && dobj.getBlackListedVehicleDobj() != null && dobj.getBlackListedVehicleDobj().getComplain_type() == TableConstants.BLTheftCode) {
                    skipInsuranceCheck = true;
                }
            }

            if (dobj != null && dobj.getOwnerIdentity() != null && dobj.getOwnerIdentity().getMobile_no() != null && dobj.getOwnerIdentity().getMobile_no() != 0 && (changedData != null && !changedData.equals(""))) {//for Updating OwnerIdentification/Contact Details
                ownerDobj.getOwner_identity().setRegn_no(ownerDobj.getRegn_no());
                ownerDobj.getOwner_identity().setState_cd(ownerDobj.getState_cd());
                ownerDobj.getOwner_identity().setOff_cd(ownerDobj.getOff_cd());
                OwnerIdentificationImpl.insertUpdateOwnerIdentificationVT(tmgr, ownerDobj.getOwner_identity());
            }

            if (dobj != null && dobj.getInsDobj() != null && !status.isInsuranceStatus()) {
                if (dobj.getInsDobj().getIns_from() != null) {
                    dobj.getInsDobj().setIns_from(new java.sql.Date(dobj.getInsDobj().getIns_from().getTime()));
                }
                if (dobj.getInsDobj().getIns_upto() != null) {
                    dobj.getInsDobj().setIns_upto(new java.sql.Date(dobj.getInsDobj().getIns_upto().getTime()));
                }

                //update when there is change in insurance FORM
                if (insuranceComparison == null || !insuranceComparison.equalsIgnoreCase(dobj.getInsDobj().toString())) {
                    if (!skipInsuranceCheck) {
                        InsImpl insImpl = new InsImpl();
                        insImpl.validateInsuranceForBlackListedVehicle(dobj);
                        InsImpl.insert_ins_dtls_to_Vh_insurance(tmgr, status_dobj.get(0).getRegn_no(), dobj.getState_cd(), Util.getEmpCode());
                        InsImpl.insertIntoVtInsurance(tmgr, dobj.getInsDobj(), status_dobj.get(0).getRegn_no(), dobj.getState_cd(), dobj.getOff_cd());
                        InsImpl.deleteFromVaInsurance(tmgr, status_dobj.get(0).getRegn_no(), null);
                    }
                }
            }

            if (dobj != null
                    && ((!dobj.getState_cd().equalsIgnoreCase(status_dobj.get(0).getState_cd()) && dobj.getOff_cd() != status_dobj.get(0).getOff_cd())
                    || (dobj.getState_cd().equalsIgnoreCase(status_dobj.get(0).getState_cd()) && dobj.getOff_cd() != status_dobj.get(0).getOff_cd()))) {
                status_dobj.get(0).setState_cd(dobj.getState_cd());
                status_dobj.get(0).setOff_cd(dobj.getOff_cd());
                status_dobj.get(0).setEmp_cd(Long.valueOf(Util.getEmpCode()));
                insertIntoVaInwardOtherOffice(tmgr, status_dobj.get(0));
                //MailSender sendMail = new MailSender("send2ashok@hotmail.com", "Vehicle No XXXXXX has requested for Change of Address in RTO XYZ State PQR with Application No DL18XXXXXXXX.This vehicle is registered at RTO XYZ State PQR", "Registration No DL1XXXXX has requested for Change of Address in Other RTO XYZ");
                //sendMail.start();
            }
            if (convDobj != null && convDobj.getNew_vch_class() != 0) {
                for (int i = 0; i < status_dobj.size(); i++) {
                    if (status_dobj.get(i).getPur_cd() == TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION) {
                        convDobj.setAppl_no(appl_no);
                        convDobj.setOld_regn_no(ownerDobj.getRegn_no());
                        convDobj.setOld_vch_class(ownerDobj.getVh_class());
                        convDobj.setOld_vch_catg(ownerDobj.getVch_catg());
                        ConvImpl.insertIntoInwrdConversion(tmgr, convDobj);
                    }
                }

            }
            tmgr.commit();//Commiting data here....
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error In Application Number Generation");
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error In Application Number Generation");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            }
        }
        return appl_no;
    }

    public boolean taxPaidOrClearedStatus(String regNo, TmConfigurationDobj confg, Owner_dobj dobj, int purCd) throws Exception {

        boolean taxPaidStatus = false;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("isTaxPaid");
            taxPaidStatus = ServerUtil.isTaxPaidOrCleared(regNo, tmgr, confg, dobj, purCd);
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }

        return taxPaidStatus;
    }

    public String taxPaidStatusForSmartCard(String regNo, TmConfigurationDobj confg, Owner_dobj dobj, int purCd, TransactionManager tmgr) throws Exception {
        String taxPaidStatus = "";
        taxPaidStatus = ServerUtil.getTaxStatus(regNo, tmgr, confg, dobj, purCd);
        return taxPaidStatus;
    }

    public boolean taxPaidOrClearedStatusOnVehType(String regNo) throws Exception {

        boolean taxPaidStatus = false;
        TransactionManagerReadOnly tmgr = null;
        Map<String, String> taxPaid = new HashMap<>();
        Date taxPaidUpto = null;
        Date nocDate = null;
        String sql = null;
        PreparedStatement ps = null;
        try {

            taxPaid = ServerUtil.taxPaidInfo(false, regNo, 58);

            if (!taxPaid.isEmpty()) {
                for (Map.Entry<String, String> entry : taxPaid.entrySet()) {
                    if (entry.getKey().equalsIgnoreCase("tax_upto") && entry.getValue() != null) {
                        taxPaidUpto = DateUtils.parseDate(entry.getValue());
                    }
                }

                if (taxPaidUpto != null) {
                    tmgr = new TransactionManagerReadOnly("taxPaidOrClearedStatusOnVehType");
                    sql = "SELECT noc_dt FROM " + TableList.VT_OTHER_STATE_VEH
                            + " WHERE new_regn_no=? and state_cd=? and off_cd=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, regNo);
                    ps.setString(2, Util.getUserStateCode());
                    ps.setInt(3, Util.getSelectedSeat().getOff_cd());
                    RowSet rs = tmgr.fetchDetachedRowSet();

                    if (rs.next()) {
                        nocDate = rs.getDate("noc_dt");
                    }

                    if (nocDate != null && DateUtils.compareDates(nocDate, taxPaidUpto) <= 1) {
                        taxPaidStatus = true;
                    }
                } else if (taxPaidUpto == null) {
                    taxPaidStatus = true;
                }
            }

        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

        return taxPaidStatus;
    }

    public static int getRegValidityYear(Owner_dobj own_dobj) throws VahanException {

        int regUpto = 0;
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        Exception e = null;
        VehicleParameters vehParameters = null;
        String sql = null;

        try {
            vehParameters = fillVehicleParametersFromDobj(own_dobj);
            tmgr = new TransactionManagerReadOnly("ApplicationInwardImpl");
            sql = "SELECT * FROM " + TableList.VM_VALIDITY_MAST
                    + " WHERE pur_cd=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, 0);//for all purpose 
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getRegValidityYear")) {
                    regUpto = rs.getInt("new_value");
                }
            }

        } catch (SQLException sq) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            e = sq;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

        if (e != null) {
            throw new VahanException("Error in Getting Validity Upto Date");
        }

        return regUpto;
    }

    public List<Status_dobj> getOnlineApplicationDetails(String regnNo) throws VahanException {
        List<Status_dobj> onlineApplDateList = new ArrayList<>();
        PreparedStatement ps = null;
        TransactionManager tmg = null;
        String sql = null;

        try {
            tmg = new TransactionManager("getOnlineApplicationDetails");
            sql = "SELECT a.*,b.*,c.application_status as application_status FROM " + TableList.VA_STATUS_APPL
                    + "  a inner join " + TableList.VA_DETAILS_APPL
                    + "  b on a.appl_no=b.appl_no and a.pur_cd=b.pur_cd and b.regn_no=? and a.moved_from_online=?"
                    + "  inner join " + TableList.VT_TEMP_APPL_TRANSACTION
                    + "  c on b.regn_no=c.regn_no and b.pur_cd=c.pur_cd and c.application_status in (?,?) and b.appl_no=c.transaction_no "
                    + "  WHERE b.regn_no=?";
            ps = tmg.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, TableConstants.MOVED_FROM_ONLINE_APPL_STATUS_NO);
            ps.setString(3, TableConstants.ONLINE_APPL_STATUS_PAYMENT_SUCCESSFUL);
            ps.setString(4, TableConstants.ONLINE_APPL_STATUS_PAYMENT_REQUEST);
            ps.setString(5, regnNo);

            RowSet rs = tmg.fetchDetachedRowSet();
            while (rs.next()) {
                Status_dobj status_dobj = new Status_dobj();
                status_dobj.setAppl_no(rs.getString("appl_no"));
                status_dobj.setAction_cd(rs.getInt("action_cd"));
                status_dobj.setRegn_no(rs.getString("regn_no"));
                status_dobj.setPur_cd(rs.getInt("pur_cd"));
                status_dobj.setFlow_slno(rs.getInt("flow_slno"));
                status_dobj.setFile_movement_slno(rs.getInt("file_movement_slno"));
                status_dobj.setState_cd(rs.getString("state_cd"));
                status_dobj.setOff_cd(rs.getInt("off_cd"));
                status_dobj.setEmp_cd(rs.getInt("emp_cd"));
                status_dobj.setSeat_cd(rs.getString("seat_cd"));
                status_dobj.setCntr_id(rs.getString("cntr_id"));
                status_dobj.setStatus(rs.getString("status"));
                status_dobj.setOffice_remark("Online Application");
                status_dobj.setPublic_remark(rs.getString("public_remark"));
                status_dobj.setFile_movement_type(rs.getString("file_movement_type"));
                status_dobj.setUser_id(rs.getString("user_id"));
                status_dobj.setAppl_date(rs.getTimestamp("appl_dt"));
                status_dobj.setUser_type(rs.getString("user_type"));
                status_dobj.setEntry_ip(rs.getString("entry_ip"));
                status_dobj.setEntry_status(rs.getString("entry_status"));
                status_dobj.setConfirm_ip(rs.getString("confirm_ip"));
                status_dobj.setConfirm_status(rs.getString("confirm_status"));
                status_dobj.setConfirm_date(rs.getDate("confirm_date"));

                if (rs.getString("application_status").equalsIgnoreCase(TableConstants.ONLINE_APPL_STATUS_PAYMENT_REQUEST)) {
                    throw new VahanException("Transaction Already Pending at Online Service, Please Check Failed/Pending Transaction Form.");
                }

                onlineApplDateList.add(status_dobj);
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmg != null) {
                    tmg.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return onlineApplDateList;

    }

    public void updateOnlineApplStatus(TransactionManager tmgr, Status_dobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;

        sql = " UPDATE " + TableList.VA_STATUS_APPL
                + "   SET moved_from_online=?"
                + " WHERE appl_no=? and pur_cd=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(pos++, TableConstants.MOVED_FROM_ONLINE_APPL_STATUS_YES);
        ps.setString(pos++, dobj.getAppl_no());
        ps.setInt(pos++, dobj.getPur_cd());
        ps.executeUpdate();

    } // end of updateOnlineApplStatus

    public void updateApprovedStatusForOnlineAppl(TransactionManager tmgr, Status_dobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = null;

        sql = "UPDATE " + TableList.VA_DETAILS_APPL
                + "   SET  entry_status=?,confirm_ip=?,confirm_date=current_timestamp"
                + " WHERE  appl_no=? and pur_cd=?";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getEntry_status());
        ps.setString(2, Util.getClientIpAdress());
        ps.setString(3, dobj.getAppl_no());
        ps.setInt(4, dobj.getPur_cd());
        ps.executeUpdate();
    } // end of updateApprovedStatusForOnlineAppl

    public boolean isOnlineApplication(String applNo, int purCd) throws VahanException {
        boolean isOnlineApp = false;
        PreparedStatement ps = null;
        TransactionManager tmg = null;
        String sql = null;
        try {
            tmg = new TransactionManager("isOnlineApplication");
            sql = "SELECT a.appl_no,a.pur_cd FROM " + TableList.VA_STATUS_APPL
                    + "  a inner join " + TableList.VA_DETAILS_APPL
                    + "  b on a.appl_no=b.appl_no and a.pur_cd=b.pur_cd and a.moved_from_online=?"
                    + "  WHERE b.appl_no=? and b.pur_cd=?";
            ps = tmg.prepareStatement(sql);
            ps.setString(1, TableConstants.MOVED_FROM_ONLINE_APPL_STATUS_YES);
            ps.setString(2, applNo);
            ps.setInt(3, purCd);

            RowSet rs = tmg.fetchDetachedRowSet();
            if (rs.next()) {
                isOnlineApp = true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong, Please Contact to the System Administrator.");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong, Please Contact to the System Administrator.");
        } finally {
            try {
                if (tmg != null) {
                    tmg.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return isOnlineApp;
    }

    public boolean getFitnessRequiredFor(Owner_dobj own_dobj, TmConfigurationDobj tmConfigurationDobj) throws VahanException {

        boolean fitReqFor = false;
        Exception e = null;
        VehicleParameters vehParameters = null;
        try {
            vehParameters = fillVehicleParametersFromDobj(own_dobj);
            if (tmConfigurationDobj != null && isCondition(replaceTagValues(tmConfigurationDobj.getFitness_rqrd_for(), vehParameters), "getFitnessRequiredFor")) {
                fitReqFor = true;
            }

        } catch (Exception sq) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            e = sq;
        }

        if (e != null) {
            throw new VahanException("Error in Getting Fitness Required for Condition Formula");
        }

        return fitReqFor;
    }

    public boolean getRenewRegRequiredFor(Owner_dobj own_dobj, TmConfigurationDobj tmConfigurationDobj) throws VahanException {

        boolean renewRegReqFor = false;
        Exception e = null;
        VehicleParameters vehParameters = null;
        try {
            vehParameters = fillVehicleParametersFromDobj(own_dobj);
            if (tmConfigurationDobj != null && isCondition(replaceTagValues(tmConfigurationDobj.getRenewal_regn_rqrd_for(), vehParameters), "getRenewRegRequiredFor")) {
                renewRegReqFor = true;
            }

        } catch (Exception sq) {
            LOGGER.error(sq.toString() + " " + sq.getStackTrace()[0]);
            e = sq;
        }

        if (e != null) {
            throw new VahanException("Error in Getting Renewal of Registration Required for Condition Formula");
        }

        return renewRegReqFor;
    }

    public String isApplApproved(String regnNo, int purCd, String stateCd) throws VahanException {
        String isApplApproved = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("isApplApproved");

            String sql = " SELECT * FROM " + TableList.VA_DETAILS
                    + " WHERE regn_no = ? and pur_cd = ? and state_cd = ? and entry_status <> ?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setInt(2, purCd);
            ps.setString(3, stateCd);
            ps.setString(4, TableConstants.STATUS_APPROVED);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                isApplApproved = "Application No [ " + rs.getString("appl_no") + " ]" + " in State [ " + ServerUtil.getStateNameByStateCode(rs.getString("state_cd")) + " ] at Office [ " + ServerUtil.getOfficeName(rs.getInt("off_cd"), rs.getString("state_cd")) + " ]"
                        + " is pending and not yet approved";
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        return isApplApproved;
    }

    /**
     * *
     * get application status based on purpose code
     *
     * @param applNo
     * @param purCd
     * @param offCd
     * @param stateCd
     * @return
     * @throws VahanException
     */
    public String isApplApprovedByApplNo(String applNo, int purCd, int offCd, String stateCd) throws Exception {
        String isApplApproved = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("isApplApprovedByApplNo");

            String sql = " SELECT entry_status FROM " + TableList.VA_DETAILS
                    + " WHERE appl_no = ? and pur_cd = ? and state_cd = ? and off_cd = ? and entry_status = ?";
            PreparedStatement ps = tmgr.prepareStatement(sql);

            ps.setString(1, applNo);
            ps.setInt(2, purCd);
            ps.setString(3, stateCd);
            ps.setInt(4, offCd);
            ps.setString(5, TableConstants.STATUS_APPROVED);

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isApplApproved = rs.getString("entry_status");
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        return isApplApproved;
    }

    public void regVehRevertBackfileFlow(String applNo, String stateCd, List<String> purCdList, Owner_dobj ownerDobj) throws VahanException {
        int action_cd = 0;
        int flowSrlNo = 0;
        int actionCodeArray[] = null;
        VehicleParameters parameters = null;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        String sql = null;
        try {
            tmgr = new TransactionManager("regVehRevertBackfileFlow");
            parameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
            for (int i = 0; i < purCdList.size(); i++) {
                actionCodeArray = ServerUtil.getInitialAction(tmgr, stateCd, Integer.parseInt(purCdList.get(i)), parameters);
                if (actionCodeArray == null) {
                    throw new VahanException("Initial Action Code is Not Available. Please Contact to the System Administrator");
                }
                action_cd = actionCodeArray[0];//initial flow Action Code.
                flowSrlNo = actionCodeArray[1];//initial flow serial no.

                sql = "INSERT INTO " + TableList.VHA_STATUS
                        + " SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno,"
                        + "  action_cd, seat_cd, cntr_id, ? as status, ? as office_remark, public_remark,"
                        + "  file_movement_type, ? , op_dt, current_timestamp, ? "
                        + "  from  " + TableList.VA_STATUS + " where appl_no=? and pur_cd=? ";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, TableConstants.STATUS_REVERT);
                ps.setString(2, "Revert Back for Rectification");
                ps.setLong(3, Long.parseLong(Util.getEmpCode()));
                ps.setString(4, Util.getClientIpAdress());
                ps.setString(5, applNo);
                ps.setInt(6, Integer.parseInt(purCdList.get(i)));
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                sql = "UPDATE " + TableList.VA_STATUS
                        + " SET action_cd=?,"
                        + " seat_cd = '" + TableConstants.STATUS_REVERT + "',"
                        + " flow_slno=?,"
                        + " file_movement_slno=file_movement_slno+1,"
                        + " op_dt=current_timestamp"
                        + " WHERE appl_no=? and pur_cd=?";
                ps = tmgr.prepareStatement(sql);
                ps.setInt(1, action_cd);
                ps.setInt(2, flowSrlNo);
                ps.setString(3, applNo);
                ps.setInt(4, Integer.parseInt(purCdList.get(i)));
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
            }
            tmgr.commit();//Commiting data here....
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Processing of Revert Back for Rectification, Please Contact to System Administrator");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
    }

    public Map<String, Integer> getPurCodeDescr(String applNo) {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        Map<String, Integer> keyValueList = new LinkedHashMap<>();

        try {
            tmgr = new TransactionManagerReadOnly("getPurCodeDescr()");
            sql = "SELECT a.*,b.pur_cd as purpose_code,b.descr FROM " + TableList.VA_STATUS + " a"
                    + " left join  " + TableList.TM_PURPOSE_MAST + " b on a.pur_cd=b.pur_cd and "
                    + "b.inward_appl = (CASE WHEN a.pur_cd = 1 OR a.pur_cd = 123 OR a.pur_cd = 124 THEN 'N' ELSE 'Y' END )  "
                    + "where appl_no =?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);

            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next())//found
            {
                keyValueList.put(rs.getString("descr"), rs.getInt("purpose_code"));
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

        return keyValueList;
    }

    public boolean isSpecialOrdered(String regnNo) throws VahanException {
        boolean isSpecialOrder = false;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("isSpecialOrdered");
            String sql = "SELECT regn_no FROM " + TableList.VT_SPECIAL_ORDER + " WHERE regn_no=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isSpecialOrder = true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong when getting Details of Specail Order.Please Contact to the System Administrator.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }

        return isSpecialOrder;
    }

    public void updtaeSpecialOrder(TransactionManager tmgr, String regnNo, String applNo) throws Exception {

        PreparedStatement ps = null;
        String sql = null;
        sql = "UPDATE " + TableList.VT_SPECIAL_ORDER + " SET appl_no=?,op_dt=current_timestamp"
                + " WHERE regn_no=? and appl_no is null";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, applNo);
        ps.setString(2, regnNo);
        ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
    }

    public boolean verifyNumberGenInConversion() {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        RowSet rs = null;
        String sql = null;
        boolean status = false;
        try {
            tmgr = new TransactionManagerReadOnly("verifyNumberGenInConversion");
            sql = "SELECT * from dealer.vc_action_purpose_map where state_cd = ? and action=? and pur_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setString(2, "CON");
            ps.setInt(3, TableConstants.VM_TRANSACTION_MAST_REASSIGN_REGN_NO);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                status = true;
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
        return status;
    }

    public boolean isVehAgeExpired(Owner_dobj ownerDobj, PermitDetailDobj permitDetailDobj) throws VahanException {
        boolean isVehValidityExpired = false;
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date today = new Date();
        Date currentDate = null;
        int regValidityYear = 0;
        int vehAge = 0;

        try {
            currentDate = formatter.parse(formatter.format(today));
            regValidityYear = ApplicationInwardImpl.getRegValidityYear(ownerDobj);
            FitnessImpl fitnessImpl = new FitnessImpl();
            if (permitDetailDobj != null) {
                if (permitDetailDobj.getValid_upto() != null && ServerUtil.dateRange(permitDetailDobj.getValid_upto(), 0, -1, 0).compareTo(currentDate) >= 0) {
                    vehAge = 0;
                } else {
                    ownerDobj.setPmt_type(permitDetailDobj.getPmt_type());
                    ownerDobj.setPmt_catg(permitDetailDobj.getPmt_catg());
                    vehAge = fitnessImpl.getVehAgeValidity(ownerDobj);
                }
            } else if (permitDetailDobj == null && ownerDobj.getVehType() == TableConstants.VM_VEHTYPE_TRANSPORT) {//for getting from vh_permit
                PassengerPermitDetailImpl permitDetailImpl = new PassengerPermitDetailImpl();
                PassengerPermitDetailDobj permitDetailDobjHistory = permitDetailImpl.getPermitHistory(ownerDobj.getRegn_no(), ownerDobj.getState_cd());
                if (permitDetailDobjHistory != null) {
                    ownerDobj.setPmt_type(Integer.parseInt(permitDetailDobjHistory.getPmt_type()));
                    ownerDobj.setPmt_catg(Integer.parseInt(permitDetailDobjHistory.getPmtCatg()));
                    vehAge = fitnessImpl.getVehAgeValidity(ownerDobj);
                }
            }
            if (vehAge != 0 && vehAge < regValidityYear) {
                regValidityYear = vehAge;
            }
            Date regnDate = ownerDobj.getRegn_dt();
            if (regValidityYear == 0) {
                regnDate = ownerDobj.getRegn_upto();
            }
            //for knowing that if vehicle registration is expired or not
            isVehValidityExpired = ServerUtil.dateRange(regnDate, regValidityYear, 0, (regValidityYear > 0 ? -1 : 0)).compareTo(currentDate) < 0;
        } catch (VahanException vme) {
            throw vme;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong During Vehicle Age Calculation for Expirity, Please Contact to the Administrator.");
        }
        return isVehValidityExpired;
    }

    public void moveToVaStatusHistory(TransactionManager tmgr, Status_dobj status_dobj) throws Exception {
        PreparedStatement ps = null;
        String sql = null;
        sql = "INSERT INTO " + TableList.VHA_STATUS + " "
                + " SELECT state_cd, off_cd, appl_no, pur_cd, flow_slno, file_movement_slno,"
                + "  action_cd, seat_cd, cntr_id, ?, office_remark, public_remark,"
                + "  file_movement_type, ? , op_dt, current_timestamp, ? "
                + "  from  " + TableList.VA_STATUS + " where appl_no=? and pur_cd=? ";

        ps = tmgr.prepareStatement(sql);
        ps.setString(1, status_dobj.getStatus());
        ps.setLong(2, status_dobj.getEmp_cd());
        ps.setString(3, status_dobj.getConfirm_ip());
        ps.setString(4, status_dobj.getAppl_no());
        ps.setInt(5, status_dobj.getPur_cd());
        ps.executeUpdate();
    }

    public void deleteVaStatus(TransactionManager tmgr, String applNo, int purCd) throws Exception {
        PreparedStatement ps = null;
        String sql = null;
        sql = "Delete From " + TableList.VA_STATUS + " WHERE appl_no=? and pur_cd=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, applNo);
        ps.setInt(2, purCd);
        ps.executeUpdate();
    }

    public boolean getRegisteredVehDocView(String regnNo, String stateCd, int purCd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmg = null;
        String sql = null;
        String purCdArr = "";
        boolean isDocsView = false;
        try {
            if (!CommonUtils.isNullOrBlank(regnNo) && !"NEW".equals(regnNo)) {
                tmg = new TransactionManagerReadOnly("getRegisteredVehDocView");
                sql = "SELECT o.upload_doc,o.pur_cd FROM " + TableList.VA_STATUS_APPL + "  a \n"
                        + " inner join " + TableList.VA_DETAILS_APPL + "  b on a.appl_no = b.appl_no and a.pur_cd = b.pur_cd and b.regn_no = ? \n"
                        + " inner join " + TableList.VT_TEMP_APPL_TRANSACTION + "  c on b.regn_no = c.regn_no and b.pur_cd = c.pur_cd and c.application_status = ? and b.appl_no = c.transaction_no \n"
                        + " left join onlineschema.vm_online_configuration o on o.state_cd = a.state_cd \n"
                        + " WHERE b.state_cd = ? and b.regn_no= ? and o.upload_doc = true and a.moved_from_online = ?;";
                ps = tmg.prepareStatement(sql);
                ps.setString(1, regnNo);
                ps.setString(2, TableConstants.ONLINE_APPL_STATUS_PAYMENT_SUCCESSFUL);
                ps.setString(3, stateCd);
                ps.setString(4, regnNo);
                ps.setString(5, TableConstants.MOVED_FROM_ONLINE_APPL_STATUS_YES);
                RowSet rs = tmg.fetchDetachedRowSet();
                if (rs.next()) {
                    purCdArr = rs.getString("pur_cd");
                    purCdArr = purCdArr + ",";
                    if (purCdArr.contains(purCd + ",")) {
                        isDocsView = true;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("problem in getting uploaded documents status.");
        } finally {
            try {
                if (tmg != null) {
                    tmg.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return isDocsView;
    }

    public List<ChallanReportDobj> getEChallanInfo(String regnNo) {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        List<ChallanReportDobj> eChallanInfoList = null;
        ChallanReportDobj eChallanInfoDobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("getEChallanInfo");
            sql = " SELECT *,to_char(date_time,'dd-Mon-yyyy') as challan_date_descr FROM get_echallan_info(?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (eChallanInfoList == null) {
                    eChallanInfoList = new ArrayList<>();
                }
                eChallanInfoDobj = new ChallanReportDobj();
                eChallanInfoDobj.setOffence(rs.getString("offence"));
                eChallanInfoDobj.setChal_amnt(rs.getString("challan_amount"));
                eChallanInfoDobj.setChal_date(rs.getDate("date_time"));
                eChallanInfoDobj.setOffence_place(rs.getString("location"));
                eChallanInfoDobj.setChallan_date_descr(rs.getString("challan_date_descr"));
                eChallanInfoDobj.setChallan_no(rs.getString("challan_no"));
                eChallanInfoDobj.setCompFee(rs.getInt("penalty") + rs.getInt("challan_amount"));
                eChallanInfoList.add(eChallanInfoDobj);
            }
        } catch (Exception e) {
            //LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            //throw new VahanException("Something went wrong when fetching details of Echallan, Please contact to the System Administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return eChallanInfoList;
    }

    public TmConfigurationApplInwardDobj getApplInwardAnywhereInStateConfig(String stateCd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        TmConfigurationApplInwardDobj inwardDobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("getApplInwardAnywhereInStateConfig");
            sql = " SELECT  state_cd, pur_code, ncr_office, veh_age_verification_required, \n"
                    + " check_for_to_without_noc, transactions_allowed_for_expired_vehicle, \n"
                    + " check_for_anywhere_fitness, transaction_not_allow_without_hsrp, \n"
                    + " check_for_to_with_noc, pur_cd_inward_not_allow_in_office FROM " + TableList.TM_CONFIGURATION_APPL_INWARD_ANYWHERE_IN_STATE
                    + " WHERE state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                inwardDobj = new TmConfigurationApplInwardDobj();
                inwardDobj.setState_cd(stateCd);
                inwardDobj.setPur_code(rs.getString("pur_code"));
                inwardDobj.setNcr_office(rs.getString("ncr_office"));
                inwardDobj.setVeh_age_verification_required(rs.getBoolean("veh_age_verification_required"));
                inwardDobj.setCheck_for_TO_without_NOC(rs.getString("check_for_to_without_noc"));
                inwardDobj.setTransactions_allowed_for_expired_vehicle(rs.getString("transactions_allowed_for_expired_vehicle"));
                inwardDobj.setCheck_for_anywhere_fitness(rs.getString("check_for_anywhere_fitness"));
                inwardDobj.setTransactionNotAllowWithoutHSRP(rs.getString("transaction_not_allow_without_hsrp"));
                inwardDobj.setCheck_for_to_with_noc(rs.getBoolean("check_for_to_with_noc"));
                inwardDobj.setPurCdInwardNotAllowInOffice(rs.getString("pur_cd_inward_not_allow_in_office"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong when fetching details of Application Inward Configuration , Please contact to the System Administrator.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return inwardDobj;
    }

    public static void insertIntoVaInwardOtherOffice(TransactionManager tmgr, Status_dobj statusDobj) throws VahanException {
        try {
            PreparedStatement ps = null;
            String sql = "INSERT INTO " + TableList.VA_INWARD_OTH_OFFICE + " ("
                    + "  appl_no, state_cd_fr, off_cd_fr, regn_no, emp_cd, op_dt) "
                    + "  VALUES (?, ?, ?, ?, ?, current_timestamp);";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, statusDobj.getAppl_no());
            ps.setString(2, statusDobj.getState_cd());
            ps.setInt(3, statusDobj.getOff_cd());
            ps.setString(4, statusDobj.getRegn_no());
            ps.setLong(5, statusDobj.getEmp_cd());
            ps.executeUpdate();
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public Status_dobj getApplInwardOthOffDobj(String applNo) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        Status_dobj applInwardOthOffdobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("getApplInwardOthOffDobj");
            sql = "SELECT * FROM " + TableList.VA_INWARD_OTH_OFFICE + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                applInwardOthOffdobj = new Status_dobj();
                applInwardOthOffdobj.setAppl_no(applNo);
                applInwardOthOffdobj.setState_cd(rs.getString("state_cd_fr"));
                applInwardOthOffdobj.setOff_cd(rs.getInt("off_cd_fr"));
                applInwardOthOffdobj.setRegn_no(rs.getString("regn_no"));
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
        return applInwardOthOffdobj;
    }

    public boolean getSmartCardFeeDetails(String regn_no) throws VahanException {
        boolean feePaid = false;
        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager("getSmartCardFeeDetails");
            feePaid = SmartCardImpl.isSmartCardFeePaid(regn_no, tmgr);
        } catch (VahanException e) {
            throw e;
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
        return feePaid;
    }

    public static Date getAppointmentDate(String applNO, Owner_dobj ownerDobj) throws VahanException {
        PreparedStatement ps = null;
        RowSet rs = null;
        Date apptDate = null;
        String sql = "SELECT appointment_dt::date,payment_status  FROM appointment.vt_appt_dtls where appl_no= ? and book_status='N' order by appointment_dt desc limit 1 ";
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("getAppointmentDate");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNO);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if (!CommonUtils.isNullOrBlank(rs.getString("appointment_dt")) && (ownerDobj.getOwner_cd() == TableConstants.VEH_TYPE_GOVT || ownerDobj.getOwner_cd() == TableConstants.VEH_TYPE_STATE_GOVT)) {
                    apptDate = rs.getDate("appointment_dt");
                } else if (!CommonUtils.isNullOrBlank(rs.getString("payment_status")) && rs.getString("payment_status").equalsIgnoreCase(TableConstants.ONLINE_APPL_STATUS_PAYMENT_SUCCESSFUL) && (ownerDobj.getOwner_cd() != TableConstants.VEH_TYPE_GOVT || ownerDobj.getOwner_cd() != TableConstants.VEH_TYPE_STATE_GOVT)) {
                    apptDate = rs.getDate("appointment_dt");
                } else {
                    throw new VahanException("Your appointment has been booked on " + DateUtils.getDateInDDMMYYYY(rs.getDate("appointment_dt")) + ", But Payment not done against application number : " + applNO);
                }
            }
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in fetching online Appointment date for this application no.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
        return apptDate;
    }

    public static int getAppointmentTakenOffice(String applNO, Owner_dobj ownerDobj) throws VahanException {
        PreparedStatement ps = null;
        RowSet rs = null;
        int offCd = 0;
        String sql = "SELECT off_cd FROM appointment.vt_appt_dtls where appl_no= ? and book_status='N' order by appointment_dt desc limit 1 ";
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("getAppointmentOffice");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNO);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                offCd = rs.getInt("off_cd");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Problem in fetching online Appointment taken office for this application no.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
        return offCd;
    }

    public void insertNOCDeatilsForTO(OwnerDetailsDobj dobj, String applNo, String userId, String userType, TransactionManager tmgr) throws VahanException, SQLException, Exception {
        OwnerImpl impl = new OwnerImpl();
        Owner_dobj ownerDobj = impl.getOwnerDobj(dobj);
        VehicleParameters parameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
        Status_dobj status = new Status_dobj();
        status.setAppl_date(ServerUtil.getSystemDateInPostgres());
        status.setAppl_no(applNo);
        status.setRegn_no(dobj.getRegn_no());
        status.setPur_cd(TableConstants.VM_TRANSACTION_MAST_NOC);
        status.setEmp_cd(Long.parseLong(Util.getEmpCode()));
        status.setState_cd(ownerDobj.getState_cd());
        if (Util.getUserStateCode().equalsIgnoreCase("KL")) {
            status.setOff_cd(Util.getUserSeatOffCode());
        } else {
            status.setOff_cd(ownerDobj.getOff_cd());
        }
        status.setOffice_remark("");
        status.setPublic_remark("");
        status.setStatus("N");
        status.setSeat_cd("");
        status.setFile_movement_type("F");
        status.setUser_id(userId);
        status.setUser_type("");
        status.setEntry_status("");
        String selectSql = "select * from tm_purpose_action_flow where state_cd = ? and pur_cd = ? order by flow_srno  limit 1;";
        PreparedStatement psSelect = tmgr.prepareStatement(selectSql);
        psSelect.setString(1, ownerDobj.getState_cd());
        psSelect.setInt(2, TableConstants.VM_TRANSACTION_MAST_NOC);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            status.setFlow_slno(rs.getInt("flow_srno"));
            status.setFile_movement_slno(1);
            status.setAction_cd(rs.getInt("action_cd"));
        }
        status.setVehicleParameters(parameters);
        ServerUtil.insertIntoVaStatus(tmgr, status);
        ServerUtil.insertIntoVaDetails(tmgr, status);

    }

    public String checkNationalPermitValidity(String regNo) throws VahanException {
        String permitStatus = "";
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        try {
            Date nPValidityDate = new Date();;
            tmgr = new TransactionManagerReadOnly("checkNationalPermitValidity");
            String sql = "SELECT to_char(val_to,'dd-Mon-yyyy') as val_to_dt, * FROM getnationalpermitinfo(?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                // checking for np validaty
                nPValidityDate = rs.getDate("val_to");
                if (nPValidityDate != null && nPValidityDate.compareTo(new Date()) < 0) {
                    permitStatus = " as NP validity has expired. <br/><br/>"
                            + "  <b>NP Auth No: </b>" + rs.getString("pmt_no") + " <br/>"
                            + "<b> Expiry Date: </b> " + rs.getString("val_to_dt") + "<br/><br/>"
                            + " <u><a href='https://parivahan.gov.in/npermit/' target=\"_blank\"> For more details please click here to visit the website. </a></u> ";
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in getting National Permit info.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return permitStatus;
    }

    public boolean checkPermit(int vehicleClass) throws VahanException {
        boolean checkPermit = false;
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        try {
            Date nPValidityDate = new Date();;
            String vhClass = "," + String.valueOf(vehicleClass) + ",";
            tmgr = new TransactionManagerReadOnly("checkNationalPermitValidity");
            String sql = "  select case when (','||value||',' ~ ?) then true else false end as value from dblink(((SELECT tm_dblink_list.conn_dblink "
                    + "     FROM tm_dblink_list "
                    + "     WHERE tm_dblink_list.conn_type::text = 'NPERMIT'::text))::text,'select * from vm_alloted_vh_class') "
                    + "     as (key varchar, value varchar )";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, vhClass);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                checkPermit = rs.getBoolean("value");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in getting National Permit info.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return checkPermit;
    }

    public int getoffUnderCd(int offCode, String stateCode) {
        int off_under_cd = 0;
        TransactionManager tmgr = null;
        String ChasiSQL = "select off_under_cd from tm_office where off_cd=? and state_cd = ?";
        try {
            tmgr = new TransactionManager("getOfficeName");
            PreparedStatement ps = tmgr.prepareStatement(ChasiSQL);
            ps.setInt(1, offCode);
            ps.setString(2, stateCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                off_under_cd = rs.getInt("off_under_cd");
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
        return off_under_cd;
    }

    public OnlineConfigurationDobj getOnlineConfigurationDobj(String stateCd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        OnlineConfigurationDobj dobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("getOnlineConfigurationDobj");
            String sql = "select state_cd, upload_doc, pur_cd from onlineschema.vm_online_configuration where state_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new OnlineConfigurationDobj();
                dobj.setStateCd(stateCd);
                dobj.setDocUpload(rs.getBoolean("upload_doc"));
                dobj.setPurCd(rs.getString("pur_cd"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("problem in getting Online configuration details.");
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

    public TmConfigApplAppointmentInward getApplInwardAppointmentInStateConfig(String stateCd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        String sql = null;
        TmConfigApplAppointmentInward dobj = null;
        try {
            tmgr = new TransactionManagerReadOnly("getApplInwardAppointmentInStateConfig");
            sql = " SELECT * FROM " + TableList.TM_CONFIGURATION_APPOINTMENT_INWARD + " WHERE state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new TmConfigApplAppointmentInward();
                dobj.setState_cd(stateCd);
                dobj.setRestrictedPurCdInwardOnline(rs.getString("pur_cd_restricted"));
                dobj.setAppointmentOnDate(rs.getBoolean("appointment_onDate"));
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong when fetching details of Application Inward Configuration For Appointment, Please contact to the System Administrator.");
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

    public boolean getRegisteredVehFacelessDocView(String regnNo, String stateCd, int purCd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmg = null;
        String sql = null;
        String purCdArr = "";
        boolean isDocsView = false;
        try {
            if (!CommonUtils.isNullOrBlank(regnNo) && !"NEW".equals(regnNo)) {
                tmg = new TransactionManagerReadOnly("getRegisteredVehFacelessDocView");
                sql = "SELECT o.upload_doc,o.faceless_service_purcd FROM " + TableList.VA_STATUS_APPL + "  a \n"
                        + " inner join " + TableList.VA_DETAILS_APPL + "  b on a.appl_no = b.appl_no and a.pur_cd = b.pur_cd and b.regn_no = ? \n"
                        + " inner join " + TableList.VT_TEMP_APPL_TRANSACTION + "  c on b.regn_no = c.regn_no and b.pur_cd = c.pur_cd and c.application_status = ? and b.appl_no = c.transaction_no \n"
                        + " left join onlineschema.vm_online_configuration o on o.state_cd = a.state_cd \n"
                        + " WHERE b.state_cd = ? and b.regn_no= ? and o.upload_doc = true and a.moved_from_online = ?;";
                ps = tmg.prepareStatement(sql);
                ps.setString(1, regnNo);
                ps.setString(2, TableConstants.ONLINE_APPL_STATUS_PAYMENT_SUCCESSFUL);
                ps.setString(3, stateCd);
                ps.setString(4, regnNo);
                ps.setString(5, TableConstants.MOVED_FROM_ONLINE_APPL_STATUS_YES);
                RowSet rs = tmg.fetchDetachedRowSet();
                if (rs.next()) {
                    purCdArr = rs.getString("faceless_service_purcd");
                    purCdArr = purCdArr + ",";
                    if (purCdArr.contains(purCd + ",")) {
                        isDocsView = true;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("problem in getting faceless uploaded documents status.");
        } finally {
            try {
                if (tmg != null) {
                    tmg.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return isDocsView;
    }

    public String getFancyNumberModule(String regNo) throws VahanException {
        String flag = "";
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        try {
            tmgr = new TransactionManagerReadOnly("getFancyNumberModule");
            String sql = "SELECT * from fancy.get_booked_number_module(?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                flag = rs.getString("get_booked_number_module");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in getting Fancy Number booked module details.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return flag;
    }

    public void getOnlineApplicationDocUploadPendingDetails(String regnNo, String stateCd) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmg = null;
        String sql = null;
        try {
            tmg = new TransactionManagerReadOnly("getOnlineApplicationDocUploadPendingDetails");
            sql = "SELECT a.appl_no as appl_no FROM " + TableList.VA_STATUS_APPL_TEMP
                    + "  a inner join " + TableList.VA_DETAILS_APPL_TEMP
                    + "  b on a.appl_no=b.appl_no and a.pur_cd=b.pur_cd and b.state_cd=a.state_cd and b.off_cd=a.off_cd"
                    + "  WHERE b.regn_no=? and a.state_cd=?";
            ps = tmg.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            RowSet rs = tmg.fetchDetachedRowSet();
            if (rs.next()) {
                throw new VahanException("Document upload is pending at Online Service for application number " + rs.getString("appl_no") + ". Kindly upload the document.");
            }
        } catch (VahanException ex) {
            throw ex;
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in checking online document upload.");
        } finally {
            try {
                if (tmg != null) {
                    tmg.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
    }
}
