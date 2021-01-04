/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javax.sql.RowSet;
import nic.java.util.CommonUtils;
import nic.rto.vahan.common.VahanException;
import static nic.vahan.CommonUtils.FormulaUtils.fillVehicleParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.RenewalDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.common.Appl_Details_Dobj;
import nic.vahan.server.NewVehicleNo;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author acer
 */
public class RenewalImpl {

    private static final Logger LOGGER = Logger.getLogger(RenewalImpl.class);

    public RenewalDobj setRenewalApplDbToDobj(String applNo) {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        RenewalDobj renewal_dobj = null;
        try {
            tmgr = new TransactionManager("setRenewalApplDbToDobj");
            ps = tmgr.prepareStatement("SELECT appl_no, "
                    + "regn_no, "
                    + "old_fit_dt, "
                    + "new_fit_dt, "
                    + "inspected_by, "
                    + "inspected_dt, "
                    + "op_dt "
                    + "FROM va_renewal "
                    + "where appl_no=?");
            ps.setString(1, applNo);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                renewal_dobj = new RenewalDobj();
                renewal_dobj.setApplNo(rs.getString("appl_no"));
                renewal_dobj.setRegnNo(rs.getString("regn_no"));
                renewal_dobj.setOldFitDt(rs.getDate("old_fit_dt"));
                renewal_dobj.setNewFitDt(rs.getDate("new_fit_dt"));
                renewal_dobj.setInspectedBy(rs.getString("inspected_by"));
                renewal_dobj.setInspectedDt(rs.getDate("inspected_dt"));
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
        return renewal_dobj;


    }

    public void makeChangeRenewal(RenewalDobj renewalDobj, String changedDataContents) {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("makeChangeRenewal");
            insertUpdateRenewal(tmgr, renewalDobj);
            ComparisonBeanImpl.updateChangedData(renewalDobj.getApplNo(), changedDataContents, tmgr);
            tmgr.commit();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
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

    private void insertUpdateRenewal(TransactionManager tmgr, RenewalDobj renewalDobj) {
        PreparedStatement ps = null;
        String applNo = renewalDobj.getApplNo();
        String sql = null;
        try {

            sql = "SELECT appl_no FROM " + TableList.VA_RENEWAL + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) { //if any record is exist then update otherwise insert it
                insertIntoRenewalHistory(tmgr, applNo);
                updateRenewal(tmgr, renewalDobj);
            } else {
                insertIntoRenewal(tmgr, renewalDobj);
            }

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    private void insertIntoRenewal(TransactionManager tmgr, RenewalDobj renewalDobj) {
        PreparedStatement ps = null;
        String sql = null;
        int i = 1;
        try {
            sql = "INSERT INTO " + TableList.VA_RENEWAL + "(\n"
                    + "           state_cd,off_cd,appl_no, regn_no, old_fit_dt, new_fit_dt, inspected_by, inspected_dt, \n"
                    + "            op_dt)\n"
                    + "    VALUES (? , ?, ?, ?, ?, ?, ?, ?, \n"
                    + "            current_timestamp)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.setString(i++, renewalDobj.getApplNo());
            ps.setString(i++, renewalDobj.getRegnNo());
            ps.setDate(i++, new java.sql.Date(renewalDobj.getOldFitDt().getTime()));
            ps.setDate(i++, new java.sql.Date(renewalDobj.getNewFitDt().getTime()));
            ps.setString(i++, renewalDobj.getInspectedBy());
            if (renewalDobj.getInspectedDt() != null) {
                ps.setDate(i++, new java.sql.Date(renewalDobj.getInspectedDt().getTime()));
            } else {
                ps.setDate(i++, null);
            }


            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    private void updateRenewal(TransactionManager tmgr, RenewalDobj renewalDobj) {
        PreparedStatement ps = null;
        String sql = null;
        int i = 1;
        try {
            //updation of va_renewal
            sql = "UPDATE " + TableList.VA_RENEWAL + "\n"
                    + "   SET new_fit_dt=?, inspected_by=?, \n"
                    + "       inspected_dt=?, op_dt = current_timestamp\n"
                    + " WHERE appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            //ps statement
            ps.setDate(i++, new java.sql.Date(renewalDobj.getNewFitDt().getTime()));
            ps.setString(i++, renewalDobj.getInspectedBy());
            if (renewalDobj.getInspectedDt() != null) {
                ps.setDate(i++, new java.sql.Date(renewalDobj.getInspectedDt().getTime()));
            } else {
                ps.setDate(i++, null);
            }
            ps.setString(i++, renewalDobj.getApplNo());

            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public static void insertIntoRenewalHistory(TransactionManager tmgr, String applNo) {
        PreparedStatement ps = null;
        String sql = null;
        try {
            //inserting data into vha_renewal from va_renewal
            sql = "INSERT INTO " + TableList.VHA_RENEWAL + "(moved_on, moved_by,state_cd,off_cd,appl_no, regn_no, old_fit_dt, new_fit_dt, inspected_by, inspected_dt, op_dt)\n"
                    + "SELECT current_timestamp as moved_on, ? as moved_by,state_cd,off_cd,appl_no, regn_no, old_fit_dt, new_fit_dt, inspected_by, inspected_dt,op_dt   FROM " + TableList.VA_RENEWAL + " where appl_no = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, applNo);
            ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void updateRenewalStatus(RenewalDobj renewalDobj, Status_dobj statusDobj, String changedData, Appl_Details_Dobj applDetailsDobj, Owner_dobj ownerDobj) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        try {
            VehicleParameters vehParameters = null;
            TmConfigurationDobj tmConfi = Util.getTmConfiguration();
            vehParameters = fillVehicleParametersFromDobj(ownerDobj);
            tmgr = new TransactionManager("update_Conv_Status");
            //=================WEB SERVICES FOR NEXTSTAGE START=========//
            statusDobj = ServerUtil.webServiceForNextStage(statusDobj, tmgr);

            if (statusDobj.getCurrent_role() == TableConstants.TM_ROLE_ENTRY
                    || statusDobj.getCurrent_role() == TableConstants.TM_ROLE_INSPECTION
                    || statusDobj.getCurrent_role() == TableConstants.TM_ROLE_VERIFICATION
                    || statusDobj.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL) {

                insertUpdateRenewal(tmgr, renewalDobj);
            }

            if (statusDobj.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL
                    && !statusDobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !statusDobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {



                //inserting data into vh_renewal from vt_owner
                sql = "INSERT INTO " + TableList.VH_RENEWAL + "(state_cd, off_cd, appl_no, regn_no, old_fit_dt, new_fit_dt, inspected_by, \n"
                        + " inspected_dt, op_dt, moved_on, moved_by)\n"
                        + "SELECT ownr.state_cd, ownr.off_cd, re.appl_no, ownr.regn_no, re.old_fit_dt, re.new_fit_dt, re.inspected_by, re.inspected_dt, re.op_dt, \n"
                        + "current_timestamp as moved_on, ? as moved_by\n"
                        + "  FROM " + TableList.VT_OWNER + " ownr," + TableList.VA_RENEWAL + " re "
                        + "WHERE re.appl_no = ? and re.regn_no = ownr.regn_no and ownr.state_cd = ? ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, renewalDobj.getApplNo());
                ps.setString(3, Util.getUserStateCode());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                //if current office and vehicle registration office is not same
                if (applDetailsDobj.getCurrent_state_cd().equalsIgnoreCase(applDetailsDobj.getOwnerDobj().getState_cd())
                        && applDetailsDobj.getCurrent_off_cd() != applDetailsDobj.getOwnerDobj().getOff_cd()) {
                    NewImpl newImpl = new NewImpl();
                    newImpl.updateOffCodeInRegnTables(tmgr, renewalDobj.getApplNo(), renewalDobj.getRegnNo(), applDetailsDobj.getCurrent_state_cd(), applDetailsDobj.getCurrent_off_cd(), applDetailsDobj.getCurrentEmpCd());
                }


                int transportVhType = ServerUtil.VehicleClassType(ownerDobj.getVh_class());
                if ((Util.getUserStateCode().equalsIgnoreCase("RJ")) && transportVhType == TableConstants.VM_VEHTYPE_TRANSPORT) { ///// rj renew changed
                    //For Checking if Other Application is Pending for Approval Before Renewal Approval
                    List<Status_dobj> statusList = ServerUtil.applicationStatus(applDetailsDobj.getRegn_no(), applDetailsDobj.getAppl_no(), applDetailsDobj.getCurrent_state_cd());
                    if (!statusList.isEmpty() && statusList.size() > 1) {
                        for (int i = 0; i < statusList.size(); i++) {
                            if (statusList.get(i).getPur_cd() != TableConstants.VM_TRANSACTION_MAST_REN_REG) {
                                throw new VahanException("Other transaction is Pending for this Application No, First Approve that Application Before Approving Renewal.");
                            }
                        }
                    }

                    boolean numGenAllow = RegnNumGenerationAllow(applDetailsDobj.getCurrent_state_cd(), applDetailsDobj.getCurrent_action_cd(), applDetailsDobj.getOwnerDobj(), tmgr);
                    String newRegn_no = null;
                    if (numGenAllow) {
                        NewVehicleNo newVehicleNo = new NewVehicleNo();
                        newRegn_no = newVehicleNo.generateAssignNewRegistrationNo(applDetailsDobj.getCurrent_off_cd(), applDetailsDobj.getCurrent_action_cd(), renewalDobj.getApplNo(), renewalDobj.getRegnNo(), 1, null, null, tmgr);
                    }
                    if (!CommonUtils.isNullOrBlank(newRegn_no)) {
                        OwnerImpl.insertUpdateFastagSchedular(renewalDobj.getApplNo(), renewalDobj.getStateCd(), renewalDobj.getOffCd(), renewalDobj.getRegnNo(),
                                renewalDobj.getStateCd(), renewalDobj.getOffCd(), newRegn_no, applDetailsDobj.getChasi_no(),tmgr);
                    }
                    if (!CommonUtils.isNullOrBlank(newRegn_no)) {
                        //  updateFeeAndTaxForRenewalRegnNo(tmgr, newRegn_no, renewalDobj);
                        SwappingRegnImpl.updateTablesForRetention(tmgr, renewalDobj.getRegnNo(), newRegn_no);
                        sql = "INSERT INTO " + TableList.VH_RE_ASSIGN + "("
                                + "            state_cd, off_cd, appl_no, old_regn_no, new_regn_no, reason,"
                                + "            moved_on, moved_by)"
                                + "    VALUES (?, ?, ?, ?, ?, ?,"
                                + "            current_timestamp, ?)";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, applDetailsDobj.getCurrent_state_cd());
                        ps.setInt(2, applDetailsDobj.getCurrent_off_cd());
                        ps.setString(3, renewalDobj.getApplNo());
                        ps.setString(4, renewalDobj.getRegnNo());
                        ps.setString(5, newRegn_no);
                        ps.setString(6, "Transport Renewal For RJ");
                        ps.setString(7, applDetailsDobj.getCurrentEmpCd());
                        ps.executeUpdate();

                        renewalDobj.setRegnNo(newRegn_no);

                    }
                    sql = "UPDATE " + TableList.VT_OWNER + " SET regn_upto=?, op_dt=current_timestamp "
                            + "WHERE regn_no=? and state_cd = ? ";
                    ps = tmgr.prepareStatement(sql);
                    //ps.setDate(1, new java.sql.Date(renewalDobj.getNewFitDt().getTime()));
                    ps.setDate(1, new java.sql.Date(renewalDobj.getNewFitDt().getTime()));
                    if (!CommonUtils.isNullOrBlank(newRegn_no)) {
                        ps.setString(2, newRegn_no);
                    } else {
                        ps.setString(2, renewalDobj.getRegnNo());
                    }
                    ps.setString(3, Util.getUserStateCode());
                    ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);


                } else if ((Util.getUserStateCode().equalsIgnoreCase("RJ")) && tmConfi != null && isCondition(replaceTagValues(tmConfi.getFitness_rqrd_for(), vehParameters), "updateRenewalStatus")) { ///// rj renew changed
                    sql = "UPDATE " + TableList.VT_OWNER + " SET regn_upto=?, op_dt=current_timestamp "
                            + "WHERE regn_no=? and state_cd = ? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setDate(1, new java.sql.Date(renewalDobj.getNewFitDt().getTime()));
                    ps.setString(2, renewalDobj.getRegnNo());
                    ps.setString(3, Util.getUserStateCode());
                    ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                } else {
                    sql = "UPDATE " + TableList.VT_OWNER + " SET fit_upto=?,regn_upto=?, op_dt=current_timestamp "
                            + "WHERE regn_no=? and state_cd = ? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setDate(1, new java.sql.Date(renewalDobj.getNewFitDt().getTime()));
                    ps.setDate(2, new java.sql.Date(renewalDobj.getNewFitDt().getTime()));
                    ps.setString(3, renewalDobj.getRegnNo());
                    ps.setString(4, Util.getUserStateCode());
                    ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                }

                //insert data into VHA_RENEWAL from VA_RENEWAL with 1 Second interval
                sql = "INSERT INTO " + TableList.VHA_RENEWAL + "(moved_on, moved_by,state_cd,off_cd,appl_no, regn_no, old_fit_dt, new_fit_dt, inspected_by, inspected_dt, "
                        + "op_dt)\n"
                        + "SELECT current_timestamp + interval '1 second' as moved_on, ? as moved_by,state_cd,off_cd,appl_no, regn_no, old_fit_dt, new_fit_dt, inspected_by, inspected_dt,op_dt "
                        + " FROM " + TableList.VA_RENEWAL + " where appl_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getEmpCode());
                ps.setString(2, renewalDobj.getApplNo());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                //delete from VA_RENEWAL
                sql = "Delete from " + TableList.VA_RENEWAL + " where appl_no = ?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, renewalDobj.getApplNo());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                //for updating the status of application when it is approved start
                statusDobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, statusDobj);
                //for updating the status of application when it is approved end

                //SmartCard Or Print
                ServerUtil.VerifyInsertSmartCardPrintDetail(renewalDobj.getApplNo(), renewalDobj.getRegnNo(),
                        Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), statusDobj.getPur_cd(), tmgr);

            }

            ServerUtil.insertIntoVhaChangedData(tmgr, renewalDobj.getApplNo(), changedData);//for saving the data into table those are changed by the user
            ServerUtil.fileFlow(tmgr, statusDobj); //for updateing va_status and vha status for new role,seat for new emp
            tmgr.commit();//Commiting data here....
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ee) {
                    LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
                }
            }
        }

    }//End of Renewal_Update_Status()

    public int getFlowSrNo(String state_cd, int pur_cd, int action_cd) throws VahanException {
        int flow_sr_no = 0;
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        try {
            String sql = "SELECT flow_srno from tm_purpose_action_flow where state_cd=? and pur_cd=? and action_cd=?";
            tmgr = new TransactionManager("getFlowSrNo");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, state_cd);
            ps.setInt(2, pur_cd);
            ps.setInt(3, action_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                flow_sr_no = rs.getInt("flow_srno");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Something went wrong, please try again.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ee) {
                    LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
                }
            }
        }
        return flow_sr_no;
    }

    public boolean RegnNumGenerationAllow(String state_cd, int action_cd, Owner_dobj owner_dobj, TransactionManager tmgr) throws VahanException {
        String sqlAppl = null;
        PreparedStatement ps;
        RowSet rs;
        VehicleParameters vehParameters = null;
        boolean numGenAllow = false;
        try {
            sqlAppl = "Select * from vm_regn_gen_action  where state_cd=?  and action_cd=?";
            ps = tmgr.prepareStatement(sqlAppl);
            ps.setString(1, state_cd);
            ps.setInt(2, action_cd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                vehParameters = fillVehicleParametersFromDobj(owner_dobj);
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "RegnNumGenerationAllow")) {
                    numGenAllow = true;
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(ex.getMessage());
        }

        return numGenAllow;
    }
}
