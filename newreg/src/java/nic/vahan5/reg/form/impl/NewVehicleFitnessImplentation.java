/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.DocumentType;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.AxleImpl;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.NewImpl;
import static nic.vahan.form.impl.NewVehicleFitnessImpl.insertOrUpdateFitness;
import static nic.vahan.form.impl.NewVehicleFitnessImpl.insertOrUpdatePreRegFitness;
import static nic.vahan.form.impl.NewVehicleFitnessImpl.insertVa_fitness;
import static nic.vahan.form.impl.NewVehicleFitnessImpl.insertVhaFitness;
import static nic.vahan.form.impl.NewVehicleFitnessImpl.updateVaFitness;
import nic.vahan.form.impl.RetroFittingDetailsImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

/**
 *
 * @author Kartikey Singh
 */
public class NewVehicleFitnessImplentation {

    private static final Logger LOGGER = Logger.getLogger(NewVehicleFitnessImplentation.class);

    public String saveFitnessDetails(Owner_dobj owner_dobj, FitnessDobj fitness_dobj, ArrayList fitCheckList, List<ComparisonBean> compBeanList, String eng_no, String chassis_no, Status_dobj status_dobj, AxleDetailsDobj axle_dobj, RetroFittingDetailsDobj cng_dobj, boolean isHomologation, Owner_dobj homo_dobj) throws VahanException {
        TransactionManager tmgr = null;
        String appl_no = null;
        try {
            tmgr = new TransactionManager("saveFitnessDetails");
            appl_no = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());
            owner_dobj.setAppl_no(appl_no);

            if (homo_dobj != null && isHomologation) {
                NewImpl.insertHomologationDetails(tmgr, homo_dobj, appl_no, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
            }
            fillOwnerDobjWithBlankValues(owner_dobj);

            if (owner_dobj.getTempReg() != null) {
                owner_dobj.getTempReg().setAppl_no(appl_no);
                owner_dobj.getTempReg().setRegn_no(owner_dobj.getRegn_no());
                owner_dobj.setDob_temp(null);
            }

            NewImpl.insertOrUpdateVaOwner(tmgr, owner_dobj);

            if (owner_dobj.getSpeedGovernorDobj() != null) {
                owner_dobj.getSpeedGovernorDobj().setAppl_no(appl_no);
                FitnessImpl.insertUpdateVaSpeedGovernor(owner_dobj.getSpeedGovernorDobj(), tmgr);
            } else {
                FitnessImpl.insertIntoVhaSpeedGovernor(appl_no, tmgr);
                FitnessImpl.deleteVaSpeedGovernor(appl_no, tmgr);
            }
            if (owner_dobj.getReflectiveTapeDobj() != null) {
                owner_dobj.getReflectiveTapeDobj().setApplNo(appl_no);
                new FitnessImpl().insertOrUpdateVaReflectiveTape(tmgr, owner_dobj.getReflectiveTapeDobj());
            } else {
                FitnessImpl fitImpl = new FitnessImpl();
                fitImpl.insertIntoVhaReflectiveTape(tmgr, appl_no, Util.getEmpCode());
                fitImpl.deleteVaReflectiveTape(appl_no, tmgr);
            }
            if (axle_dobj != null) {
                AxleImpl.saveAxleDetails_Impl(axle_dobj, owner_dobj.getAppl_no(), tmgr);
            } else {
                AxleImpl.insertIntoVhaAxle(tmgr, owner_dobj.getAppl_no());
                AxleImpl.deleteFromVaAxle(tmgr, owner_dobj.getAppl_no());
            }
            if (cng_dobj != null) {
                RetroFittingDetailsImpl.saveCngVehicleDetails_Impl(cng_dobj, owner_dobj.getAppl_no(), tmgr);
            } else {
                RetroFittingDetailsImpl.insertIntoVhaCng(tmgr, owner_dobj.getAppl_no());
                RetroFittingDetailsImpl.deleteFromVaRetroFittingDetails(tmgr, owner_dobj.getAppl_no());
            }
            if (!appl_no.equals("") && !(appl_no == null)) {
                status_dobj.setAppl_no(appl_no);
                ServerUtil.fileFlowForNewApplication(tmgr, status_dobj);
                status_dobj.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
                status_dobj.setStatus(TableConstants.STATUS_COMPLETE);
                status_dobj.setAppl_no(appl_no);
                status_dobj.setOffice_remark("");
                status_dobj.setPublic_remark("");
                ServerUtil.webServiceForNextStage(status_dobj, TableConstants.FORWARD, null, appl_no,
                        status_dobj.getAction_cd(), status_dobj.getPur_cd(), null, tmgr);
                ServerUtil.fileFlow(tmgr, status_dobj);
            }
            tmgr.commit();
            return appl_no;
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
    }

    public void saveOnlyPreRegFitnessDetails(Owner_dobj owner_dobj, FitnessDobj fitness_dobj, ArrayList fitCheckList, List<ComparisonBean> compBeanList, String eng_no, String chassis_no, Status_dobj status_dobj, AxleDetailsDobj axle_dobj, RetroFittingDetailsDobj cng_dobj, boolean isHomologation, Owner_dobj homo_dobj) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("saveOnlyPreRegFitnessDetails");

            insertIntoVhaChangedData(tmgr, status_dobj.getAppl_no(), ComparisonBeanImpl.changedDataContents(compBeanList));
            insertOrUpdatePreRegFitness(tmgr, owner_dobj.getRegn_no(), owner_dobj.getChasi_no(), fitness_dobj, fitCheckList, eng_no);
            owner_dobj.setAppl_no(status_dobj.getAppl_no());

            NewImpl.insertOrUpdateVaOwner(tmgr, owner_dobj);
            if (homo_dobj != null && isHomologation) {
                NewImpl.insertHomologationDetails(tmgr, homo_dobj, owner_dobj.getAppl_no(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
            }
            if (owner_dobj.getSpeedGovernorDobj() != null) {
                FitnessImpl.insertUpdateVaSpeedGovernor(owner_dobj.getSpeedGovernorDobj(), tmgr);
            } else {
                FitnessImpl.insertIntoVhaSpeedGovernor(owner_dobj.getAppl_no(), tmgr);
                FitnessImpl.deleteVaSpeedGovernor(owner_dobj.getAppl_no(), tmgr);
            }
            if (owner_dobj.getReflectiveTapeDobj() != null) {
                new FitnessImpl().insertOrUpdateVaReflectiveTape(tmgr, owner_dobj.getReflectiveTapeDobj());
            } else {
                FitnessImpl fitImpl = new FitnessImpl();
                fitImpl.insertIntoVhaReflectiveTape(tmgr, owner_dobj.getAppl_no(), Util.getEmpCode());
                fitImpl.deleteVaReflectiveTape(owner_dobj.getAppl_no(), tmgr);
            }
            if (axle_dobj != null) {
                AxleImpl.saveAxleDetails_Impl(axle_dobj, owner_dobj.getAppl_no(), tmgr);
            } else {
                AxleImpl.insertIntoVhaAxle(tmgr, owner_dobj.getAppl_no());
                AxleImpl.deleteFromVaAxle(tmgr, owner_dobj.getAppl_no());
            }
            if (cng_dobj != null) {
                RetroFittingDetailsImpl.saveCngVehicleDetails_Impl(cng_dobj, owner_dobj.getAppl_no(), tmgr);
            } else {
                RetroFittingDetailsImpl.insertIntoVhaCng(tmgr, owner_dobj.getAppl_no());
                RetroFittingDetailsImpl.deleteFromVaRetroFittingDetails(tmgr, owner_dobj.getAppl_no());
            }

            tmgr.commit();


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
    }

    public void saveAndMovePreRegFitnessDetails(Owner_dobj owner_dobj, FitnessDobj fitness_dobj, ArrayList fitCheckList, List<ComparisonBean> compBeanList, String eng_no, String chassis_no, Status_dobj status_dobj, AxleDetailsDobj axle_dobj, RetroFittingDetailsDobj cng_dobj, boolean isHomologation, Owner_dobj homo_dobj) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("saveAndMovePreRegFitnessDetails");
            insertIntoVhaChangedData(tmgr, status_dobj.getAppl_no(), ComparisonBeanImpl.changedDataContents(compBeanList));
            insertOrUpdatePreRegFitness(tmgr, owner_dobj.getRegn_no(), owner_dobj.getChasi_no(), fitness_dobj, fitCheckList, eng_no);
            owner_dobj.setAppl_no(status_dobj.getAppl_no());
            NewImpl.insertOrUpdateVaOwner(tmgr, owner_dobj);
            if (homo_dobj != null && isHomologation) {
                NewImpl.insertHomologationDetails(tmgr, homo_dobj, owner_dobj.getAppl_no(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
            }
            if (owner_dobj.getSpeedGovernorDobj() != null) {
                FitnessImpl.insertUpdateVaSpeedGovernor(owner_dobj.getSpeedGovernorDobj(), tmgr);
            } else {
                FitnessImpl.insertIntoVhaSpeedGovernor(owner_dobj.getAppl_no(), tmgr);
                FitnessImpl.deleteVaSpeedGovernor(owner_dobj.getAppl_no(), tmgr);
            }
            if (owner_dobj.getReflectiveTapeDobj() != null) {
                new FitnessImpl().insertOrUpdateVaReflectiveTape(tmgr, owner_dobj.getReflectiveTapeDobj());
            } else {
                FitnessImpl fitImpl = new FitnessImpl();
                fitImpl.insertIntoVhaReflectiveTape(tmgr, owner_dobj.getAppl_no(), Util.getEmpCode());
                fitImpl.deleteVaReflectiveTape(owner_dobj.getAppl_no(), tmgr);
            }
            if (axle_dobj != null) {
                AxleImpl.saveAxleDetails_Impl(axle_dobj, owner_dobj.getAppl_no(), tmgr);
            } else {
                AxleImpl.insertIntoVhaAxle(tmgr, owner_dobj.getAppl_no());
                AxleImpl.deleteFromVaAxle(tmgr, owner_dobj.getAppl_no());
            }
            if (cng_dobj != null) {
                RetroFittingDetailsImpl.saveCngVehicleDetails_Impl(cng_dobj, owner_dobj.getAppl_no(), tmgr);
            } else {
                RetroFittingDetailsImpl.insertIntoVhaCng(tmgr, owner_dobj.getAppl_no());
                RetroFittingDetailsImpl.deleteFromVaRetroFittingDetails(tmgr, owner_dobj.getAppl_no());
            }

            if (status_dobj.getAction_cd() == TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION_APPROVAL && "Y".equalsIgnoreCase(fitness_dobj.getFit_result())) {
                insertIntoVaFcPrint(tmgr, owner_dobj.getAppl_no(), owner_dobj.getRegn_no(), owner_dobj.getState_cd(), owner_dobj.getOff_cd());
                moveFromVaFitenessChassisToVTFitenessChassis(tmgr, fitness_dobj, owner_dobj);
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                ServerUtil.insertForQRDetails(owner_dobj.getAppl_no(), owner_dobj.getRegn_no(), owner_dobj.getChasi_no(), null, false, DocumentType.FC_QR_38, owner_dobj.getState_cd(), owner_dobj.getOff_cd(), tmgr);
            }
            ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            if ("N".equalsIgnoreCase(fitness_dobj.getFit_result())) {
                status_dobj.setAction_cd(TableConstants.TM_ROLE_NEW_REGISTRATION_FIT_FEE);
                status_dobj.setFlow_slno(2);
            }
            ServerUtil.fileFlow(tmgr, status_dobj);
            tmgr.commit();
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
    }

    public void reBack(Owner_dobj owner_dobj, FitnessDobj fitness_dobj, ArrayList fitCheckList, List<ComparisonBean> compBeanList, String eng_no, String chassis_no, Status_dobj status_dobj, AxleDetailsDobj axle_dobj, RetroFittingDetailsDobj cng_dobj) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("reBack");
            insertIntoVhaChangedData(tmgr, status_dobj.getAppl_no(), ComparisonBeanImpl.changedDataContents(compBeanList));
            insertOrUpdatePreRegFitness(tmgr, owner_dobj.getRegn_no(), owner_dobj.getChasi_no(), fitness_dobj, fitCheckList, eng_no);
            NewImpl.insertOrUpdateVaOwner(tmgr, owner_dobj);
            if (owner_dobj.getSpeedGovernorDobj() != null) {
                FitnessImpl.insertUpdateVaSpeedGovernor(owner_dobj.getSpeedGovernorDobj(), tmgr);
            } else {
                FitnessImpl.insertIntoVhaSpeedGovernor(owner_dobj.getAppl_no(), tmgr);
                FitnessImpl.deleteVaSpeedGovernor(owner_dobj.getAppl_no(), tmgr);
            }
            if (owner_dobj.getReflectiveTapeDobj() != null) {
                new FitnessImpl().insertOrUpdateVaReflectiveTape(tmgr, owner_dobj.getReflectiveTapeDobj());
            } else {
                FitnessImpl fitImpl = new FitnessImpl();
                fitImpl.insertIntoVhaReflectiveTape(tmgr, owner_dobj.getAppl_no(), Util.getEmpCode());
                fitImpl.deleteVaReflectiveTape(owner_dobj.getAppl_no(), tmgr);
            }
            if (axle_dobj != null) {
                AxleImpl.saveAxleDetails_Impl(axle_dobj, owner_dobj.getAppl_no(), tmgr);
            } else {
                AxleImpl.insertIntoVhaAxle(tmgr, owner_dobj.getAppl_no());
                AxleImpl.deleteFromVaAxle(tmgr, owner_dobj.getAppl_no());
            }
            if (cng_dobj != null) {
                RetroFittingDetailsImpl.saveCngVehicleDetails_Impl(cng_dobj, owner_dobj.getAppl_no(), tmgr);
            } else {
                RetroFittingDetailsImpl.insertIntoVhaCng(tmgr, owner_dobj.getAppl_no());
                RetroFittingDetailsImpl.deleteFromVaRetroFittingDetails(tmgr, owner_dobj.getAppl_no());
            }

            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            ServerUtil.fileFlow(tmgr, status_dobj);
            tmgr.commit();
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
    }

    public static void insertOrUpdatePreRegFitness(TransactionManager tmgr, String regn_no, String chasisNo,
            FitnessDobj fitness_dobj, ArrayList fitCheckList, String eng_no) throws VahanException, SQLException {

        for (int i = 0; i < fitCheckList.size(); i++) {

            if (fitCheckList.get(i).equals("N")) {
                /*
                 * 14th position is faremeter field and is mandatory only if fare_mtr_no is not null or non empty
                 */
                if (i == 13 && (fitness_dobj.getFare_mtr_no() == null || fitness_dobj.getFare_mtr_no().isEmpty())) {
                    continue;
                }
                throw new VahanException("All Parameters in Vehicle Detail should be selected to proceed further");
            }
        }
        insertOrUpdateFitness(tmgr, regn_no, chasisNo, fitness_dobj, fitCheckList, eng_no);
    }

    public static void insertOrUpdateFitness(TransactionManager tmgr, String regn_no, String chasisNo,
            FitnessDobj fitness_dobj, ArrayList fitCheckList, String eng_no) throws VahanException, SQLException {

        PreparedStatement ps = tmgr.prepareStatement("Select * from " + TableList.VA_FITNESS_CHASSIS + " where chasi_no=?");
        ps.setString(1, chasisNo);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();

        if (rs.next()) {
            insertVhaFitness(tmgr, chasisNo, eng_no);
            updateVaFitness(tmgr, regn_no, chasisNo, fitness_dobj, fitCheckList, eng_no);
        } else {
            insertVa_fitness(tmgr, regn_no, chasisNo, fitness_dobj, fitCheckList, eng_no);
        }
    }

    public static void updateVaFitness(TransactionManager tmgr, String regn_no, String chasisNo,
            FitnessDobj fitness_dobj, ArrayList fitCheckList, String eng_no) throws VahanException {
        try {
            ///////////////Fitness Data Updation////////////////////////
            int preparedPositionCnt = 1;
            String sql = "update " + TableList.VA_FITNESS_CHASSIS
                    + " SET chasi_no=?, eng_no=?, "
                    + " fit_chk_dt=?, fit_chk_tm=?, "
                    + " fit_result=?,fit_valid_to=?,fit_nid=?, fit_off_cd1=?, "
                    + " fit_off_cd2=?, remark=?, pucc_no=?, pucc_val=?,"
                    + " fare_mtr_no=?,brake=?, steer=?, susp=?, engin=?,"
                    + " tyre=?, horn=?, lamp=?, embo=?,speed=?, paint=?, wiper=?,"
                    + " dimen=?, body=?, fare=?, elec=?, finis=?, road=?, "
                    + " poll=?,transm=?, glas=?, emis=?, rear=?, others=?,"
                    + " op_dt=current_timestamp "
                    + " where chasi_no=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);

            ps.setString(preparedPositionCnt++, chasisNo);
            ps.setString(preparedPositionCnt++, eng_no);
            ps.setDate(preparedPositionCnt++, new java.sql.Date(fitness_dobj.getFit_chk_dt().getTime()));
            ps.setString(preparedPositionCnt++, fitness_dobj.getFit_chk_tm());
            ps.setString(preparedPositionCnt++, fitness_dobj.getFit_result());
            if (fitness_dobj.getFit_valid_to() == null) {
                throw new VahanException("Blank Fitness Valid Upto.");
            }
            ps.setDate(preparedPositionCnt++, new java.sql.Date(fitness_dobj.getFit_valid_to().getTime()));

            if (fitness_dobj.getFit_nid() != null) {
                ps.setDate(preparedPositionCnt++, new java.sql.Date(fitness_dobj.getFit_nid().getTime()));
            } else {
                ps.setDate(preparedPositionCnt++, null);
            }
            ps.setInt(preparedPositionCnt++, fitness_dobj.getFit_off_cd1());
            ps.setInt(preparedPositionCnt++, fitness_dobj.getFit_off_cd2());
            ps.setString(preparedPositionCnt++, fitness_dobj.getRemark());
            ps.setString(preparedPositionCnt++, fitness_dobj.getPucc_no());
            if (fitness_dobj.getPucc_val() != null) {
                ps.setDate(preparedPositionCnt++, new java.sql.Date(fitness_dobj.getPucc_val().getTime()));
            } else {
                ps.setDate(preparedPositionCnt++, null);
            }

            ps.setString(preparedPositionCnt++, fitness_dobj.getFare_mtr_no());
            //ps.setString(12, fitCheckList.get(0));        
            int fitCheckListCnt = 0;

            for (fitCheckListCnt = 0; fitCheckListCnt < fitCheckList.size(); fitCheckListCnt++, preparedPositionCnt++) {

                if (preparedPositionCnt == 37) {
                    break; // break at ps 36 beacaus at 35th position application no need to set.
                }

                ps.setString(preparedPositionCnt, (String) fitCheckList.get(fitCheckListCnt));
            }
            ps.setString(preparedPositionCnt, chasisNo);
            ps.executeUpdate();

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public static void insertVhaFitness(TransactionManager tmgr, String chasisNo, String eng_no) throws VahanException {
        try {


            String sql = "insert into " + TableList.VHA_FITNESS_CHASSIS
                    + " SELECT current_timestamp as moved_on, ? as moved_by,a.* "
                    + " FROM " + TableList.VA_FITNESS_CHASSIS
                    + " a WHERE chasi_no=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, chasisNo);
            ps.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Unable to insert New Vehicle Fitness Details in History");


        }
    }

    public static void insertVa_fitness(TransactionManager tmgr, String regn_no, String chasisNo,
            FitnessDobj fitness_dobj, ArrayList fitCheckList, String eng_no) throws VahanException {

        PreparedStatement ps = null;
        String sql = null;
        int preparedPositionCnt = 1;

        try {

            sql = "insert into " + TableList.VA_FITNESS_CHASSIS
                    + "  (state_cd ,  off_cd,appl_no,  regn_no ,  chasi_no ,eng_no,fit_chk_dt, "
                    + "  fit_chk_tm ,  fit_result , fit_valid_to, fit_nid,  fit_off_cd1 , "
                    + "  fit_off_cd2 ,  remark ,  pucc_no , "
                    + "  pucc_val ,  fare_mtr_no ,  brake ,  steer ,   "
                    + "  susp ,  engin ,  tyre ,  horn ,  lamp ,  "
                    + "  embo ,  speed ,  paint ,  wiper ,  dimen ,   "
                    + "  body ,  fare ,  elec ,  finis ,  road ,   "
                    + "  poll ,  transm ,  glas ,  emis ,  rear ,  "
                    + "  others ,  op_dt)"
                    + "  values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,current_timestamp)";


            ps = tmgr.prepareStatement(sql);

            ps.setString(preparedPositionCnt++, Util.getUserStateCode());
            ps.setInt(preparedPositionCnt++, Util.getUserSeatOffCode());
            ps.setString(preparedPositionCnt++, fitness_dobj.getAppl_no());
            ps.setString(preparedPositionCnt++, regn_no);
            ps.setString(preparedPositionCnt++, chasisNo);
            ps.setString(preparedPositionCnt++, eng_no);
            ps.setDate(preparedPositionCnt++, new java.sql.Date(fitness_dobj.getFit_chk_dt().getTime()));
            ps.setString(preparedPositionCnt++, fitness_dobj.getFit_chk_tm());
            ps.setString(preparedPositionCnt++, fitness_dobj.getFit_result());
            if (fitness_dobj.getFit_valid_to() == null) {
                throw new VahanException("Blank Fitness Valid Upto.");
            }
            ps.setDate(preparedPositionCnt++, new java.sql.Date(fitness_dobj.getFit_valid_to().getTime()));

            if (fitness_dobj.getFit_nid() != null) {
                ps.setDate(preparedPositionCnt++, new java.sql.Date(fitness_dobj.getFit_nid().getTime()));
            } else {
                ps.setDate(preparedPositionCnt++, null);
            }
            ps.setInt(preparedPositionCnt++, fitness_dobj.getFit_off_cd1());
            ps.setInt(preparedPositionCnt++, fitness_dobj.getFit_off_cd2());
            ps.setString(preparedPositionCnt++, fitness_dobj.getRemark());
            ps.setString(preparedPositionCnt++, fitness_dobj.getPucc_no());
            if (fitness_dobj.getPucc_val() != null) {
                ps.setDate(preparedPositionCnt++, new java.sql.Date(fitness_dobj.getPucc_val().getTime()));
            } else {
                ps.setDate(preparedPositionCnt++, null);
            }
            ps.setString(preparedPositionCnt++, fitness_dobj.getFare_mtr_no());

            int fitCheckListCnt = 0;
            for (fitCheckListCnt = 0; fitCheckListCnt < fitCheckList.size(); fitCheckListCnt++, preparedPositionCnt++) {
                if (preparedPositionCnt == 41) {
                    break;
                }
                ps.setString(preparedPositionCnt, (String) fitCheckList.get(fitCheckListCnt));
            }

            ps.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Unable to insert New Vehicle Fitness Details");


        }
    }//end of insertVa_fitness

    public FitnessDobj set_Fitness_appl_db_to_dobj(String chassis_no, String appl_no) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        FitnessDobj fitness_dobj = null;
        VahanException vahanexecption = null;
        String sql = null;
        String param = null;
        try {


            if (appl_no != null) {
                appl_no = appl_no.toUpperCase().trim();
                sql = "SELECT *  FROM " + TableList.VA_FITNESS_CHASSIS
                        + " where appl_no=? and state_cd = ?";
                param = appl_no;
            }
            if (chassis_no != null) {
                chassis_no = chassis_no.toUpperCase().trim();
                sql = "SELECT *  FROM " + TableList.VT_FITNESS_CHASSIS
                        + " where chasi_no=? and state_cd = ?";
                param = chassis_no;
            }


            tmgr = new TransactionManager("set_Fitness_appl_db_to_dobj");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, param);
            ps.setString(2, Util.getUserStateCode());

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                fitness_dobj = new FitnessDobj();

                if (appl_no != null) {
                    fitness_dobj.setAppl_no(rs.getString("appl_no"));
                    fitness_dobj.setPucc_no(rs.getString("pucc_no"));
                    fitness_dobj.setPucc_val(rs.getDate("pucc_val"));
                }

                fitness_dobj.setRegn_no(rs.getString("regn_no"));
                fitness_dobj.setChasi_no(rs.getString("chasi_no"));
                fitness_dobj.setFare_mtr_no(rs.getString("fare_mtr_no"));
                fitness_dobj.setFit_chk_dt(rs.getDate("fit_chk_dt"));
                if (fitness_dobj.getFit_chk_dt() != null) {
                    fitness_dobj.setFit_chk_dt_descr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(String.valueOf(fitness_dobj.getFit_chk_dt()))));
                }
                fitness_dobj.setFit_chk_tm(rs.getString("fit_chk_tm"));
                fitness_dobj.setFit_off_cd1(rs.getInt("fit_off_cd1"));
                fitness_dobj.setFit_off_cd2(rs.getInt("fit_off_cd2"));
                fitness_dobj.setFit_result(rs.getString("fit_result"));
                fitness_dobj.setFit_valid_to(rs.getDate("fit_valid_to"));
                if (fitness_dobj.getFit_valid_to() != null) {
                    fitness_dobj.setFit_valid_to_descr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(String.valueOf(fitness_dobj.getFit_valid_to()))));
                }
                fitness_dobj.setRemark(rs.getString("remark"));
                fitness_dobj.setOff_cd(rs.getInt("off_cd"));
                fitness_dobj.setState_cd(rs.getString("state_cd"));
                fitness_dobj.setOp_dt(rs.getDate("op_dt"));
                fitness_dobj.setFit_nid(rs.getDate("fit_nid"));
                if (fitness_dobj.getFit_nid() != null) {
                    fitness_dobj.setFit_nid_descr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(String.valueOf(fitness_dobj.getFit_nid()))));
                }

                ArrayList listparam = new ArrayList();
                listparam.add(rs.getString("brake"));
                listparam.add(rs.getString("steer"));
                listparam.add(rs.getString("susp"));
                listparam.add(rs.getString("engin"));
                listparam.add(rs.getString("tyre"));
                listparam.add(rs.getString("horn"));

                listparam.add(rs.getString("lamp"));
                listparam.add(rs.getString("embo"));
                listparam.add(rs.getString("speed"));
                listparam.add(rs.getString("paint"));
                listparam.add(rs.getString("wiper"));
                listparam.add(rs.getString("dimen"));

                listparam.add(rs.getString("body"));
                listparam.add(rs.getString("fare"));
                listparam.add(rs.getString("elec"));
                listparam.add(rs.getString("finis"));
                listparam.add(rs.getString("road"));
                listparam.add(rs.getString("poll"));

                listparam.add(rs.getString("transm"));
                listparam.add(rs.getString("glas"));
                listparam.add(rs.getString("emis"));
                listparam.add(rs.getString("rear"));
                listparam.add(rs.getString("others"));
                fitness_dobj.setList_parameters(listparam);


            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            vahanexecption = new VahanException("Error in fetching details for Fitness");
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
        return fitness_dobj;
    }

    public boolean checkForPreRegFitness(String chasiNo, String engineNo) throws VahanException {
        boolean isPreRegFitness = false;
        VahanException vahanexecption = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("checkForPreRegFitness");
            PreparedStatement ps = tmgr.prepareStatement("Select * from " + TableList.VT_FITNESS_CHASSIS + " where chasi_no=? and state_cd=?");
            ps.setString(1, chasiNo);
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                isPreRegFitness = true;
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            vahanexecption = new VahanException("Error in fetching details for Fitness");
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
        return isPreRegFitness;
    }
    
    /*
     * Author: Kartikey Singh
     */
    public boolean checkForPreRegFitness(String chasiNo, String engineNo, String userStateCode) throws VahanException {
        boolean isPreRegFitness = false;
        VahanException vahanexecption = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("checkForPreRegFitness");
            PreparedStatement ps = tmgr.prepareStatement("Select * from " + TableList.VT_FITNESS_CHASSIS + " where chasi_no=? and state_cd=?");
            ps.setString(1, chasiNo);
            ps.setString(2, userStateCode);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                isPreRegFitness = true;
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            vahanexecption = new VahanException("Error in fetching details for Fitness");
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
        return isPreRegFitness;
    }

    private void moveFromVaFitenessChassisToVTFitenessChassis(TransactionManager tmgr, FitnessDobj fitness_dobj, Owner_dobj owner_dobj) throws Exception {

        String sql = null;
        PreparedStatement ps = null;
        try {


            sql = "INSERT INTO vahan4.vt_fitness_chassis(\n"
                    + "            state_cd, off_cd, regn_no, chasi_no, eng_no, fit_chk_dt, fit_chk_tm, \n"
                    + "            fit_result, fit_valid_to, fit_nid, fit_off_cd1, fit_off_cd2, \n"
                    + "            remark, pucc_no, pucc_val, fare_mtr_no, brake, steer, susp, engin, \n"
                    + "            tyre, horn, lamp, embo, speed, paint, wiper, dimen, body, fare, \n"
                    + "            elec, finis, road, poll, transm, glas, emis, rear, others, op_dt)\n"
                    + "SELECT state_cd, off_cd,  regn_no, chasi_no, eng_no, fit_chk_dt, \n"
                    + "       fit_chk_tm, fit_result, fit_valid_to, fit_nid, fit_off_cd1, fit_off_cd2, \n"
                    + "       remark, pucc_no, pucc_val, fare_mtr_no, brake, steer, susp, engin, \n"
                    + "       tyre, horn, lamp, embo, speed, paint, wiper, dimen, body, fare, \n"
                    + "       elec, finis, road, poll, transm, glas, emis, rear, others, op_dt\n"
                    + "  FROM vahan4.va_fitness_chassis  WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, owner_dobj.getAppl_no());
            ps.executeUpdate();

            if (fitness_dobj.getFit_result().equalsIgnoreCase(TableConstants.FitnessResultPass)) {
                sql = "UPDATE  " + TableList.VA_OWNER
                        + " SET fit_upto= ? WHERE appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setDate(1, new java.sql.Date(fitness_dobj.getFit_valid_to().getTime()));
                ps.setString(2, owner_dobj.getAppl_no());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                if (fitness_dobj.getFit_result().equalsIgnoreCase(TableConstants.FitnessResultFail)) {
                    sql = "UPDATE  " + TableList.VT_FITNESS_CHASSIS
                            + " SET fit_valid_to=?, fit_nid=? WHERE appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setNull(1, java.sql.Types.DATE);
                    ps.setNull(2, java.sql.Types.DATE);
                    ps.setString(3, owner_dobj.getAppl_no());
                    ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                }

                ServerUtil.deleteFromTable(tmgr, null, owner_dobj.getAppl_no(), TableList.VA_FITNESS_CHASSIS);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Unable to insert Fitness Data");
        }

    }

    public void saveInVtFitnessFromVtFitnessChassis(TransactionManager tmgr, String chasi_no, String eng_no, String appl_no) throws VahanException {
        PreparedStatement ps = null;

        try {
            ps = tmgr.prepareStatement("Select * from " + TableList.VA_FITNESS + " where appl_no=?");
            ps.setString(1, appl_no);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (!rs.next()) {
                String sql = "INSERT INTO vahan4.va_fitness(\n"
                        + "            state_cd, off_cd, appl_no, regn_no, chasi_no, fit_chk_dt, fit_chk_tm, \n"
                        + "            fit_result, fit_valid_to, fit_nid, fit_off_cd1, fit_off_cd2, \n"
                        + "            remark, pucc_no, pucc_val, fare_mtr_no, brake, steer, susp, engin, \n"
                        + "            tyre, horn, lamp, embo, speed, paint, wiper, dimen, body, fare, \n"
                        + "            elec, finis, road, poll, transm, glas, emis, rear, others, op_dt)\n"
                        + "    SELECT state_cd, off_cd,?, regn_no, chasi_no,  fit_chk_dt, fit_chk_tm, \n"
                        + "       fit_result, fit_valid_to, fit_nid, fit_off_cd1, fit_off_cd2, \n"
                        + "       remark, pucc_no, pucc_val, fare_mtr_no, brake, steer, susp, engin, \n"
                        + "       tyre, horn, lamp, embo, speed, paint, wiper, dimen, body, fare, \n"
                        + "       elec, finis, road, poll, transm, glas, emis, rear, others, op_dt\n"
                        + "  FROM vahan4.vt_fitness_chassis where chasi_no=?";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_no);
                ps.setString(2, chasi_no);
                ps.executeUpdate();
                saveInVhFitnessChassisFromVtFitnessChassis(tmgr, chasi_no, eng_no);
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error: Unable to  Insert Record of New Vehicle  Fitness");

        }
    }

    public void insertIntoVhaOwnerBYChassisNo(TransactionManager tmgr, String chassis_no) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "INSERT INTO  " + TableList.VHA_OWNER
                    + " SELECT current_timestamp as moved_on , ? as moved_by, state_cd, off_cd, appl_no, regn_no, regn_dt, purchase_dt, owner_sr, "
                    + "        owner_name, f_name, c_add1, c_add2, c_add3, c_district, c_pincode, "
                    + "        c_state, p_add1, p_add2, p_add3, p_district, p_pincode, p_state, "
                    + "        owner_cd, regn_type, vh_class, chasi_no, eng_no, maker, maker_model, "
                    + "        body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, "
                    + "        ld_wt, gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, "
                    + "        cubic_cap, floor_area, ac_fitted, audio_fitted, video_fitted, "
                    + "        vch_purchase_as, vch_catg, dealer_cd, sale_amt, laser_code, garage_add, "
                    + "        length, width, height, regn_upto, fit_upto, annual_income, imported_vch, "
                    + "        other_criteria, pmt_type, pmt_catg, rqrd_tax_modes, op_dt "
                    + "  FROM  " + TableList.VA_OWNER
                    + " where chasi_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, chassis_no);
            ps.executeUpdate();

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }//end of insertIntoVhaOwner

    public void insertIntoVaFcPrint(TransactionManager tmgr, String appl_no, String regn_no, String state_cd, int off_cd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;

        try {

            sql = "Select * from " + TableList.VA_FC_PRINT + " where appl_no=? and state_cd=? and off_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, appl_no);
            ps.setString(2, state_cd);
            ps.setInt(3, off_cd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();

            if (!rs.next()) {
                sql = "Insert into " + TableList.VA_FC_PRINT + " ( state_cd,off_cd,appl_no, regn_no, op_dt)  VALUES (?, ?, ?,?,current_timestamp) ";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, state_cd);
                ps.setInt(2, off_cd);
                ps.setString(3, appl_no);
                ps.setString(4, regn_no);
                ps.executeUpdate();
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }//end of insertIntoVhaOwner

    private void fillOwnerDobjWithBlankValues(Owner_dobj owner_dobj) {
        owner_dobj.setPurchase_dt(owner_dobj.getPurchase_dt() == null ? new Date() : owner_dobj.getPurchase_dt());
        owner_dobj.setOwner_name(owner_dobj.getOwner_name() == null ? TableConstants.EMPTY_STRING : owner_dobj.getOwner_name());
        owner_dobj.setF_name(owner_dobj.getF_name() == null ? TableConstants.EMPTY_STRING : owner_dobj.getF_name());
        owner_dobj.setC_add1(owner_dobj.getC_add1() == null ? TableConstants.EMPTY_STRING : owner_dobj.getC_add1());
        owner_dobj.setC_add2(owner_dobj.getC_add2() == null ? TableConstants.EMPTY_STRING : owner_dobj.getC_add2());
        owner_dobj.setC_add3(owner_dobj.getC_add3() == null ? TableConstants.EMPTY_STRING : owner_dobj.getC_add3());
        owner_dobj.setC_pincode(owner_dobj.getC_pincode() == 0 ? TableConstants.EMPTY_INT : owner_dobj.getC_pincode());
        owner_dobj.setC_district(owner_dobj.getC_district() == 0 ? TableConstants.EMPTY_INT : owner_dobj.getC_district());
        owner_dobj.setC_state(owner_dobj.getC_state() == null ? TableConstants.EMPTY_STRING : owner_dobj.getC_state());
        owner_dobj.setP_add1(owner_dobj.getP_add1() == null ? TableConstants.EMPTY_STRING : owner_dobj.getP_add1());
        owner_dobj.setP_add2(owner_dobj.getP_add2() == null ? TableConstants.EMPTY_STRING : owner_dobj.getP_add2());
        owner_dobj.setP_add3(owner_dobj.getP_add3() == null ? TableConstants.EMPTY_STRING : owner_dobj.getP_add3());
        owner_dobj.setP_pincode(owner_dobj.getP_pincode() == 0 ? TableConstants.EMPTY_INT : owner_dobj.getP_pincode());
        owner_dobj.setP_district(owner_dobj.getP_district() == 0 ? TableConstants.EMPTY_INT : owner_dobj.getP_district());
        owner_dobj.setP_state(owner_dobj.getP_state() == null ? TableConstants.EMPTY_STRING : owner_dobj.getP_state());


    }

    public boolean isFailedFitness(String appl_no, int purCd, int offCd, String stateCd) throws VahanException {
        boolean isFitnessFailed = false;
        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager("isFailedFitness");

            String sql = " SELECT regn_no, op_dt FROM " + TableList.VA_FITNESS_CHASSIS
                    + " WHERE appl_no = ? and fit_result=? order by op_dt desc limit 1";
            PreparedStatement ps = tmgr.prepareStatement(sql);

            ps.setString(1, appl_no);
            ps.setString(2, TableConstants.FitnessResultFail);

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                isFitnessFailed = true;
            }
        } catch (SQLException e) {
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

        return isFitnessFailed;
    }

    public static void insertVhaFitnessByAppl_no(TransactionManager tmgr, String appl_no) throws VahanException, SQLException {
        try {
            String sql = "insert into " + TableList.VHA_FITNESS_CHASSIS
                    + " SELECT current_timestamp as moved_on, ? as moved_by,a.* "
                    + " FROM " + TableList.VA_FITNESS_CHASSIS
                    + " a WHERE appl_no=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            ps.executeUpdate();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void saveInVhFitnessChassisFromVtFitnessChassis(TransactionManager tmgr, String chasi_no, String eng_no) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;

        try {
            sql = "INSERT INTO vh_fitness_chassis(\n"
                    + "            moved_on, moved_by, state_cd, off_cd, regn_no, chasi_no, eng_no, \n"
                    + "            fit_chk_dt, fit_chk_tm, fit_result, fit_valid_to, fit_nid, fit_off_cd1, \n"
                    + "            fit_off_cd2, remark, pucc_no, pucc_val, fare_mtr_no, brake, steer, \n"
                    + "            susp, engin, tyre, horn, lamp, embo, speed, paint, wiper, dimen, \n"
                    + "            body, fare, elec, finis, road, poll, transm, glas, emis, rear, \n"
                    + "            others, op_dt)\n"
                    + "    SELECT current_timestamp as moved_on ,? as moved_by , state_cd, off_cd, regn_no, chasi_no, eng_no, fit_chk_dt, fit_chk_tm, \n"
                    + "       fit_result, fit_valid_to, fit_nid, fit_off_cd1, fit_off_cd2, \n"
                    + "       remark, pucc_no, pucc_val, fare_mtr_no, brake, steer, susp, engin, \n"
                    + "       tyre, horn, lamp, embo, speed, paint, wiper, dimen, body, fare, \n"
                    + "       elec, finis, road, poll, transm, glas, emis, rear, others, op_dt\n"
                    + "  FROM vt_fitness_chassis where chasi_no=?";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, chasi_no);
            ps.executeUpdate();


        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error: Unable to  Insert Record of New Vehicle  Fitness in History");

        }
    }

    public static Date getExemptedOldRcptDate(String applNo, String stateCd, int offCd, int purCd) throws VahanException {
        Date rcptDate = null;
        String sql = "select * from " + TableList.VT_FEE + " a \n"
                + " inner join " + TableList.VT_FEE_EXEMPTED + " b \n"
                + " on a.rcpt_no=b.rcpt_no and a.state_cd=b.state_cd and a.off_cd=b.off_cd \n"
                + " where b.appl_no=? and b.state_cd=? and b.off_cd=? and b.pur_cd =? ";
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getExemptedOldRcptDate");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.setInt(4, purCd);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                rcptDate = rs.getDate("rcpt_dt");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error: Unable to fetch exempted old receipt date.");
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return rcptDate;
    }

    public String checkPendingChassisFCPrint(String applNo, String stateCd) throws VahanException {
        String isPending = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("checkPendingChassisFCPrint");
            String sql = "SELECT a.*,c.chasi_no FROM " + TableList.VA_FC_PRINT + " a \n"
                    + " join " + TableList.VA_OWNER + " b on a.state_cd=b.state_cd and a.appl_no=b.appl_no \n"
                    + " join " + TableList.VT_FITNESS_CHASSIS + " c on b.state_cd=c.state_cd and b.chasi_no=c.chasi_no \n"
                    + " WHERE a.state_cd=? and a.appl_no = ? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setString(2, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                isPending = "Application No [ " + applNo + " ]" + " in State [ " + ServerUtil.getStateNameByStateCode(stateCd) + " ] at Office [ " + ServerUtil.getOfficeName(rs.getInt("off_cd"), stateCd) + " ]"
                        + " is pending for Chassis Fitness Certificate printing.";
            }
        } catch (Exception e) {
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
        return isPending;
    }

    public String checkForPreRegFitnessHistory(String chasiNo, String stateCd) throws VahanException {
        String applNo = null;
        VahanException vahanexecption = null;
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("checkForPreRegFitnessHistory");
            String sql = "select distinct a.appl_no from " + TableList.VA_DETAILS + " a \n"
                    + " inner join " + TableList.VP_APPL_RCPT_MAPPING + " b on a.state_cd=b.state_cd and a.off_cd=b.off_cd and a.appl_no=b.appl_no \n"
                    + " inner join " + TableList.VH_FITNESS_CHASSIS + " c on b.state_cd=c.state_cd and b.off_cd=c.off_cd and b.chasi_no=c.chasi_no \n"
                    + " where a.state_cd=? and a.pur_cd=? and a.entry_status=? and c.chasi_no=? and c.fit_valid_to >= current_timestamp";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE_FITNESS);
            ps.setString(3, TableConstants.STATUS_APPROVED);
            ps.setString(4, chasiNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                applNo = rs.getString("appl_no");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            vahanexecption = new VahanException("Error in fetching details for Fitness");
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
        return applNo;
    }

    public void saveInVtFitChassisFromVhFitChassis(TransactionManager tmgr, String chasiNo, String stateCd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "INSERT INTO vt_fitness_chassis(\n"
                    + "            state_cd, off_cd, regn_no, chasi_no, eng_no, \n"
                    + "            fit_chk_dt, fit_chk_tm, fit_result, fit_valid_to, fit_nid, fit_off_cd1, \n"
                    + "            fit_off_cd2, remark, pucc_no, pucc_val, fare_mtr_no, brake, steer, \n"
                    + "            susp, engin, tyre, horn, lamp, embo, speed, paint, wiper, dimen, \n"
                    + "            body, fare, elec, finis, road, poll, transm, glas, emis, rear, \n"
                    + "            others, op_dt)\n"
                    + "    SELECT state_cd, off_cd, regn_no, chasi_no, eng_no, fit_chk_dt, fit_chk_tm, \n"
                    + "       fit_result, fit_valid_to, fit_nid, fit_off_cd1, fit_off_cd2, \n"
                    + "       remark, pucc_no, pucc_val, fare_mtr_no, brake, steer, susp, engin, \n"
                    + "       tyre, horn, lamp, embo, speed, paint, wiper, dimen, body, fare, \n"
                    + "       elec, finis, road, poll, transm, glas, emis, rear, others, op_dt\n"
                    + "  FROM vh_fitness_chassis where state_cd=? and chasi_no=? order by moved_on desc limit 1";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setString(2, chasiNo);
            ps.executeUpdate();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error: Unable to Revert Record of New Vehicle Chassis Fitness");
        }
    }
    
}
