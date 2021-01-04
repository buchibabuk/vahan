/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.fillVehicleParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.DocumentType;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.dobj.EpayDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.InspectionDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.ReflectiveTapeDobj;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;
import nic.vahan.form.dobj.SpeedGovernorDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.VehicleTrackingDetailsDobj;
import nic.vahan.form.dobj.VmSmartCardHsrpDobj;
import nic.vahan.form.dobj.VmValidityMastDobj;
import nic.vahan.form.dobj.common.Appl_Details_Dobj;
import nic.vahan.form.dobj.fitness.TmConfigurationFitnessDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.dobj.permit.TemporaryPermitDobj;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.permit.PassengerPermitDetailImpl;
import nic.vahan.form.impl.permit.PermitDetailImpl;
import nic.vahan.form.impl.permit.TemporaryPermitImpl;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.insertIntoVhaChangedData;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC094
 */
public class FitnessImpl {

    private static final Logger LOGGER = Logger.getLogger(FitnessImpl.class);

    public FitnessDobj set_Fitness_appl_db_to_dobj_withStateCdOffCd(String regn_no, String appl_no, String state_cd, int off_cd) throws VahanException {

        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        FitnessDobj fitness_dobj = null;
        VahanException vahanexecption = null;
        String sql = null;
        String param = null;
        try {

            if (regn_no != null) {
                regn_no = regn_no.toUpperCase().trim();
                sql = "SELECT  a.*,b.user_name as fit_user1, c.user_name as fit_user2  FROM " + TableList.VT_FITNESS
                        + " a left outer join tm_user_info b on a.fit_off_cd1 = b.user_cd "
                        + " left outer join tm_user_info c on a.fit_off_cd2 = c.user_cd  "
                        + " where a.regn_no=? order by a.op_dt desc limit 1";
                param = regn_no;
            }

            if (appl_no != null) {
                appl_no = appl_no.toUpperCase().trim();
                sql = "SELECT a.* ,b.user_name as fit_user1, c.user_name as fit_user2 FROM " + TableList.VA_FITNESS
                        + " a left outer join tm_user_info b on a.fit_off_cd1 = b.user_cd "
                        + " left outer join tm_user_info c on a.fit_off_cd2 = c.user_cd  "
                        + " where a.appl_no=?";
                param = appl_no;
            }

            tmgr = new TransactionManagerReadOnly("FitnessImpl.set_Fitness_appl_db_to_dobj_withStateCdOffCd");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, param);

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
                fitness_dobj.setFit_officer_name1(rs.getString("fit_user1"));
                fitness_dobj.setFit_officer_name2(rs.getString("fit_user2"));
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

    public FitnessDobj set_Fitness_appl_db_to_dobj(String regn_no, String appl_no) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        FitnessDobj fitness_dobj = null;
        VahanException vahanexecption = null;
        String sql = null;
        String param = null;
        try {

            if (regn_no != null) {
                regn_no = regn_no.toUpperCase().trim();
                sql = "SELECT  a.*,b.user_name as fit_user1, c.user_name as fit_user2,d.descr as state_name,e.off_name"
                        + "  FROM " + TableList.VT_FITNESS + " a "
                        + "  left outer join " + TableList.TM_USER_INFO + " b on a.fit_off_cd1 = b.user_cd "
                        + "  left outer join  " + TableList.TM_USER_INFO + " c on a.fit_off_cd2 = c.user_cd  "
                        + "  left outer join  " + TableList.TM_STATE + " d on a.state_cd = d.state_code "
                        + "  left outer join  " + TableList.TM_OFFICE + " e on a.off_cd = e.off_cd and a.state_cd=e.state_cd "
                        + "  where a.regn_no=? order by op_dt desc limit 1";
                param = regn_no;
            }

            if (appl_no != null) {
                appl_no = appl_no.toUpperCase().trim();
                sql = "SELECT  a.*,b.user_name as fit_user1, c.user_name as fit_user2,d.descr as state_name,e.off_name"
                        + "  FROM " + TableList.VA_FITNESS + " a "
                        + "  left outer join " + TableList.TM_USER_INFO + " b on a.fit_off_cd1 = b.user_cd "
                        + "  left outer join  " + TableList.TM_USER_INFO + " c on a.fit_off_cd2 = c.user_cd  "
                        + "  left outer join  " + TableList.TM_STATE + " d on a.state_cd = d.state_code "
                        + "  left outer join  " + TableList.TM_OFFICE + " e on a.off_cd = e.off_cd and a.state_cd=e.state_cd "
                        + "  where a.appl_no=?";
                param = appl_no;
            }

            tmgr = new TransactionManager("Fitness_Impl.set_Fitness_appl_db_to_dobj");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, param);

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
                fitness_dobj.setFit_officer_name1(rs.getString("fit_user1"));
                fitness_dobj.setFit_officer_name2(rs.getString("fit_user2"));
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
                fitness_dobj.setState_name(rs.getString("state_name"));
                fitness_dobj.setOff_name(rs.getString("off_name"));

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

    public FitnessDobj getVtFitnessTempDetails(String regnNo) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        FitnessDobj fitness_dobj = null;
        String sql = null;
        try {
            sql = "SELECT  a.*,to_char(a.op_dt,'dd-MON-yyyy') as op_dt_descr,b.user_name as fit_user1, c.user_name as fit_user2,d.descr as state_name,e.off_name"
                    + "  FROM " + TableList.VT_FITNESS_TEMP + " a "
                    + "  left outer join " + TableList.TM_USER_INFO + " b on a.fit_off_cd1 = b.user_cd "
                    + "  left outer join  " + TableList.TM_USER_INFO + " c on a.fit_off_cd2 = c.user_cd  "
                    + "  left outer join  " + TableList.TM_STATE + " d on a.state_cd = d.state_code "
                    + "  left outer join  " + TableList.TM_OFFICE + " e on a.off_cd = e.off_cd and a.state_cd=e.state_cd "
                    + "  where a.regn_no=?";
            tmgr = new TransactionManagerReadOnly("getVtFitnessTempDetails");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                fitness_dobj = new FitnessDobj();
                fitness_dobj.setAppl_no(rs.getString("appl_no"));
                fitness_dobj.setPucc_no(rs.getString("pucc_no"));
                fitness_dobj.setPucc_val(rs.getDate("pucc_val"));
                fitness_dobj.setRegn_no(rs.getString("regn_no"));
                fitness_dobj.setChasi_no(rs.getString("chasi_no"));
                fitness_dobj.setFare_mtr_no(rs.getString("fare_mtr_no"));
                fitness_dobj.setFit_chk_dt(rs.getDate("fit_chk_dt"));
                if (fitness_dobj.getFit_chk_dt() != null) {
                    fitness_dobj.setFit_chk_dt_descr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(String.valueOf(fitness_dobj.getFit_chk_dt()))));
                }
                fitness_dobj.setFit_chk_tm(rs.getString("fit_chk_tm"));
                fitness_dobj.setFit_officer_name1(rs.getString("fit_user1"));
                fitness_dobj.setFit_officer_name2(rs.getString("fit_user2"));
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
                fitness_dobj.setOp_dt_descr(rs.getString("op_dt_descr"));
                fitness_dobj.setFit_nid(rs.getDate("fit_nid"));
                if (fitness_dobj.getFit_nid() != null) {
                    fitness_dobj.setFit_nid_descr(JSFUtils.convertToStandardDateFormat(JSFUtils.getStringToDateyyyyMMdd(String.valueOf(fitness_dobj.getFit_nid()))));
                }
                fitness_dobj.setState_name(rs.getString("state_name"));
                fitness_dobj.setOff_name(rs.getString("off_name"));

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
            throw new VahanException("Something went wrong when fetching details of Temporary Fitness, Please Contact to the System Administrator");
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

    public void makeChange_fitness_renewal(FitnessDobj fit_dobj, SpeedGovernorDobj speedGovernorDobj,
            boolean isSpeedGovFitted, ReflectiveTapeDobj reflectiveTapeDobj, boolean isReflectiveTap,
            RetroFittingDetailsDobj cngDetails, RetroFittingDetailsDobj cngDetailsPrev,
            boolean isRetroFitted, String changedata, String stateCd, int offCd, VehicleTrackingDetailsDobj vltdDobj) throws VahanException {
        TransactionManager tmgr = null;
        try {
            if (fit_dobj != null) {
                tmgr = new TransactionManager("makeChangeFitness_Renewal");
                insertOrUpdateFitness(tmgr, fit_dobj.getRegn_no(), fit_dobj.getChasi_no(), fit_dobj, fit_dobj.getList_parameters(), stateCd, offCd);

                if (isSpeedGovFitted && speedGovernorDobj != null) {
                    insertUpdateVaSpeedGovernor(speedGovernorDobj, tmgr);
                }
                if (isReflectiveTap && reflectiveTapeDobj != null) {
                    insertOrUpdateVaReflectiveTape(tmgr, reflectiveTapeDobj);
                }

                if (isRetroFitted && cngDetails != null && cngDetails.getHydro_dt() != null
                        && cngDetailsPrev != null && cngDetailsPrev.getHydro_dt() != null
                        && cngDetails.getHydro_dt().compareTo(cngDetailsPrev.getHydro_dt()) != 0) {
                    RetroFittingDetailsImpl.saveCngVehicleDetails_Impl(cngDetails, fit_dobj.getAppl_no(), tmgr);
                }
                if (vltdDobj != null) {
                    VehicleTrackingDetailsImpl vehicleTrackingDetailsImpl = new VehicleTrackingDetailsImpl();
                    vehicleTrackingDetailsImpl.updateStatusVehicleTrackingDetails(vltdDobj, stateCd, offCd, tmgr);
                }
                ComparisonBeanImpl.updateChangedData(fit_dobj.getAppl_no(), changedata, tmgr);
                tmgr.commit();
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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

    /**
     * *
     *
     * @param role
     * @param fitness_dobj
     * @param status_dobj
     * @throws VahanException This function is used for renewal of Fitness.
     */
    public void update_Fitness_Status(FitnessDobj fitness_dobj, FitnessDobj fitness_dobj_perv, SpeedGovernorDobj speedGovernorDobj, SpeedGovernorDobj speedGovernorDobjPrev, boolean isSpeedGovFitted, Status_dobj status_dobj, String changedData, TmConfigurationDobj tmConfigurationDobj, ReflectiveTapeDobj reflectiveTapeDobj, ReflectiveTapeDobj reflectiveTapeDobjPrev, boolean isReflectiveTap, RetroFittingDetailsDobj cngDetails, RetroFittingDetailsDobj cngDetailsPrev, boolean isRetroFiited, Appl_Details_Dobj applDetailsDobj, EpayDobj checkFeeTax, VehicleTrackingDetailsDobj vltdDobj) throws VahanException {
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;

        try {

            if (applDetailsDobj == null
                    || applDetailsDobj.getCurrent_off_cd() == 0
                    || applDetailsDobj.getCurrent_state_cd() == null) {
                throw new VahanException("Something went wrong, Please try again later...");
            }

            tmgr = new TransactionManager("update_Fitness_Status");
            //=================WEB SERVICES FOR NEXTSTAGE START=========//
            status_dobj = ServerUtil.webServiceForNextStage(status_dobj, tmgr);
            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_ENTRY
                    || status_dobj.getCurrent_role() == TableConstants.TM_ROLE_VERIFICATION) {
                if ((changedData != null && !changedData.equals(""))
                        || fitness_dobj_perv == null
                        || (speedGovernorDobjPrev == null && isSpeedGovFitted)
                        || (reflectiveTapeDobjPrev == null && isReflectiveTap)) {
                    insertOrUpdateFitness(tmgr, fitness_dobj.getRegn_no(), fitness_dobj.getChasi_no(), fitness_dobj, fitness_dobj.getList_parameters(), applDetailsDobj.getCurrent_state_cd(), applDetailsDobj.getCurrent_off_cd());
                    if (isSpeedGovFitted) {
                        insertUpdateVaSpeedGovernor(speedGovernorDobj, tmgr);
                    }
                    if (isReflectiveTap) {
                        insertOrUpdateVaReflectiveTape(tmgr, reflectiveTapeDobj);
                    }
                    if (isRetroFiited && cngDetails != null && cngDetails.getHydro_dt() != null
                            && cngDetailsPrev != null && cngDetailsPrev.getHydro_dt() != null
                            && cngDetails.getHydro_dt().compareTo(cngDetailsPrev.getHydro_dt()) != 0) {
                        RetroFittingDetailsImpl.saveCngVehicleDetails_Impl(cngDetails, fitness_dobj.getAppl_no(), tmgr);
                    }
                }
                if (speedGovernorDobjPrev != null && !isSpeedGovFitted) {
                    ServerUtil.deleteFromTable(tmgr, null, fitness_dobj.getAppl_no(), TableList.VA_SPEED_GOVERNOR);
                }
                if (reflectiveTapeDobjPrev != null && !isReflectiveTap) {
                    ServerUtil.deleteFromTable(tmgr, null, fitness_dobj.getAppl_no(), TableList.VA_REFLECTIVE_TAPE);
                }
                if (vltdDobj != null) {
                    VehicleTrackingDetailsImpl vehicleTrackingDetailsImpl = new VehicleTrackingDetailsImpl();
                    vehicleTrackingDetailsImpl.updateStatusVehicleTrackingDetails(vltdDobj, applDetailsDobj.getCurrent_state_cd(), applDetailsDobj.getCurrent_off_cd(), tmgr);
                }
            }

            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL
                    && (status_dobj.getStatus().equals(TableConstants.STATUS_REVERT) || status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING))) {
                if ((changedData != null && !changedData.equals(""))) {
                    insertOrUpdateFitness(tmgr, fitness_dobj.getRegn_no(), fitness_dobj.getChasi_no(), fitness_dobj, fitness_dobj.getList_parameters(), applDetailsDobj.getCurrent_state_cd(), applDetailsDobj.getCurrent_off_cd());
                }
                if ((speedGovernorDobjPrev == null && isSpeedGovFitted) || (changedData != null && !changedData.equals("")) && isSpeedGovFitted) {
                    insertUpdateVaSpeedGovernor(speedGovernorDobj, tmgr);
                } else {
                    ServerUtil.deleteFromTable(tmgr, null, fitness_dobj.getAppl_no(), TableList.VA_SPEED_GOVERNOR);
                }
            }

            if (status_dobj.getCurrent_role() == TableConstants.TM_ROLE_APPROVAL
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                //this below check will work on same state only
                if (fitness_dobj.getFit_valid_to() == null && applDetailsDobj.getCurrent_state_cd().equalsIgnoreCase(applDetailsDobj.getOwnerDobj().getState_cd())) {
                    throw new VahanException("Blank Fitness Valid Upto.");
                }
                // To Store paid,actual,fine amount for future reference  
                if (checkFeeTax != null) {
                    status_dobj.setListFeeTaxDifference(checkFeeTax.getList());
                }
                if ((changedData != null && !changedData.equals(""))) {//if data is changed at approval
                    insertOrUpdateFitness(tmgr, fitness_dobj.getRegn_no(), fitness_dobj.getChasi_no(), fitness_dobj, fitness_dobj.getList_parameters(), applDetailsDobj.getCurrent_state_cd(), applDetailsDobj.getCurrent_off_cd());
                    sql = "insert into " + TableList.VHA_FITNESS
                            + " SELECT current_timestamp + interval '1 second' as moved_on, ? as moved_by,a.* "
                            + " FROM " + TableList.VA_FITNESS
                            + " a WHERE appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, applDetailsDobj.getCurrentEmpCd());
                    ps.setString(2, fitness_dobj.getAppl_no());
                    ps.executeUpdate();
                } else {
                    insertVhaFitness(tmgr, fitness_dobj.getAppl_no());
                }

                //update records of vt and vh tables of fitness for same state  
                if (applDetailsDobj.getCurrent_state_cd().equalsIgnoreCase(applDetailsDobj.getOwnerDobj().getState_cd())) {
                    moveFromVaFitenessToVTFiteness_Renwal(tmgr, fitness_dobj, tmConfigurationDobj, applDetailsDobj.getOwnerDobj());
                    if (Util.getUserStateCode().equals("MH")) {
                        updateFitnessAppointment(tmgr, fitness_dobj.getAppl_no());
                    }
                    ServerUtil.insertForQRDetails(fitness_dobj.getAppl_no(), fitness_dobj.getRegn_no(), fitness_dobj.getChasi_no(), null, false, DocumentType.FC_QR_38, applDetailsDobj.getCurrent_state_cd(), applDetailsDobj.getCurrent_off_cd(), tmgr);
                } else {
                    //for anywhere fitness for other state case
                    moveFromVtFitenessTempToVhFitenessTemp(tmgr, fitness_dobj.getRegn_no(), "FITNESS INSPECTION IN OTHER STATE", applDetailsDobj.getCurrentEmpCd());
                    ServerUtil.deleteFromTable(tmgr, fitness_dobj.getRegn_no(), null, TableList.VT_FITNESS_TEMP);
                    moveFromVaFitenessToVTFitenessTemp(tmgr, fitness_dobj.getAppl_no());
                    ServerUtil.deleteFromTable(tmgr, null, fitness_dobj.getAppl_no(), TableList.VA_FITNESS);
                    ServerUtil.insertForQRDetails(fitness_dobj.getAppl_no(), fitness_dobj.getRegn_no(), fitness_dobj.getChasi_no(), null, false, DocumentType.FC_QR_38A, applDetailsDobj.getCurrent_state_cd(), applDetailsDobj.getCurrent_off_cd(), tmgr);
                }
                if (vltdDobj != null) {
                    VehicleTrackingDetailsImpl vehicleTrackingDetailsImpl = new VehicleTrackingDetailsImpl();
                    vehicleTrackingDetailsImpl.approveStatusVehTrackDetails(vltdDobj, applDetailsDobj.getCurrent_state_cd(), applDetailsDobj.getCurrent_off_cd(), tmgr);
                }
                if ((speedGovernorDobjPrev == null && isSpeedGovFitted) || (changedData != null && !changedData.equals("") && isSpeedGovFitted)) {
                    insertUpdateVaSpeedGovernor(speedGovernorDobj, tmgr);
                    sql = "INSERT INTO " + TableList.VHA_SPEED_GOVERNOR
                            + " SELECT current_timestamp + interval '1 second', ?, * FROM " + TableList.VA_SPEED_GOVERNOR + " WHERE appl_no=? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, applDetailsDobj.getCurrentEmpCd());
                    ps.setString(2, fitness_dobj.getAppl_no());
                    ps.executeUpdate();
                } else {
                    insertIntoVhaSpeedGovernor(fitness_dobj.getAppl_no(), tmgr);
                }

                if (isSpeedGovFitted) {
                    moveFromVtSpeedGovToVhSpeedGovTo(fitness_dobj.getAppl_no(), fitness_dobj.getRegn_no(), fitness_dobj.getState_cd(), fitness_dobj.getOff_cd(), tmgr);
                    moveFromVaSpeedGovToVtSpeedGov(fitness_dobj.getAppl_no(), tmgr);

                }
                ServerUtil.deleteFromTable(tmgr, null, fitness_dobj.getAppl_no(), TableList.VA_SPEED_GOVERNOR);

                if ((reflectiveTapeDobjPrev == null && isReflectiveTap) || (changedData != null && !changedData.equals("") && isReflectiveTap)) {
                    insertOrUpdateVaReflectiveTape(tmgr, reflectiveTapeDobj);
                    sql = "INSERT INTO " + TableList.VHA_REFLECTIVE_TAPE
                            + " SELECT current_timestamp + interval '1 second', ?, * FROM " + TableList.VA_REFLECTIVE_TAPE + " WHERE appl_no=? ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, applDetailsDobj.getCurrentEmpCd());
                    ps.setString(2, fitness_dobj.getAppl_no());
                    ps.executeUpdate();
                } else {
                    insertIntoVhaReflectiveTape(tmgr, fitness_dobj.getAppl_no(), applDetailsDobj.getCurrentEmpCd());
                }
                if (isReflectiveTap) {
                    moveFromVtReflectiveTapeToVhReflectiveTape(fitness_dobj.getRegn_no(), fitness_dobj.getState_cd(), tmgr);
                    moveFromVaReflectiveTapeToVtReflectiveTape(fitness_dobj.getAppl_no(), fitness_dobj.getState_cd(), fitness_dobj.getOff_cd(), tmgr);

                }
                ServerUtil.deleteFromTable(tmgr, null, fitness_dobj.getAppl_no(), TableList.VA_REFLECTIVE_TAPE);

                if (isRetroFiited && cngDetails != null && cngDetails.getHydro_dt() != null) {
                    if (cngDetailsPrev != null && cngDetailsPrev.getHydro_dt() != null
                            && cngDetails.getHydro_dt().compareTo(cngDetailsPrev.getHydro_dt()) != 0) {
                        RetroFittingDetailsImpl.saveCngVehicleDetails_Impl(cngDetails, fitness_dobj.getAppl_no(), tmgr);
                    }
                    RetroFittingDetailsImpl.insertIntoRetroFitVH(tmgr, status_dobj, fitness_dobj.getState_cd(), fitness_dobj.getOff_cd(), fitness_dobj.getRegn_no());
                    RetroFittingDetailsImpl.deleteFromVtRetroFit(tmgr, fitness_dobj.getRegn_no(), fitness_dobj.getState_cd(), fitness_dobj.getOff_cd());
                    RetroFittingDetailsImpl.insertIntoVtFromVaRetroFit(tmgr, status_dobj);
                    ServerUtil.deleteFromTable(tmgr, null, fitness_dobj.getAppl_no(), TableList.VA_RETROFITTING_DTLS);
                }

                //for updating the status of application when it is approved start
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                //for updating the status of application when it is approved end

                if (applDetailsDobj.getCurrent_state_cd().equalsIgnoreCase(applDetailsDobj.getOwnerDobj().getState_cd())) {

                    //Start*** for handling other state inspection case
                    moveFromVtFitenessTempToVhFitenessTemp(tmgr, fitness_dobj.getRegn_no(), "FITNESS INSPECTION OF OTHER STATE APPROVED IN HOME STATE", applDetailsDobj.getCurrentEmpCd());
                    ServerUtil.deleteFromTable(tmgr, fitness_dobj.getRegn_no(), null, TableList.VT_FITNESS_TEMP);
                    //End***

                    sql = "Insert into " + TableList.VA_FC_PRINT + " ( state_cd,off_cd,appl_no, regn_no, op_dt)  VALUES (?, ?, ?,?,current_timestamp) ";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, applDetailsDobj.getCurrent_state_cd());
                    ps.setInt(2, applDetailsDobj.getCurrent_off_cd());
                    ps.setString(3, fitness_dobj.getAppl_no());
                    ps.setString(4, fitness_dobj.getRegn_no());
                    ps.executeUpdate();
                    TmConfigurationFitnessDobj tmConfigFitnessDobj = getFitnessConfiguration(applDetailsDobj.getOwnerDobj().getState_cd());
                    if (tmConfigFitnessDobj.isFcAfterHSRP()) {
                        if (ServerUtil.verifyForOldVehicleHsrp(applDetailsDobj.getCurrent_state_cd(), applDetailsDobj.getCurrent_off_cd(), tmgr)) {
                            sql = "select regn_no from " + TableList.VT_HSRP
                                    + " where regn_no =? ";
                            ps = tmgr.prepareStatement(sql);
                            ps.setString(1, fitness_dobj.getRegn_no());
                            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                            if (!rs.next()) {
                                sql = "select regn_no from " + TableList.VH_HSRP
                                        + " where regn_no =? and state_cd=? ";
                                ps = tmgr.prepareStatement(sql);
                                ps.setString(1, fitness_dobj.getRegn_no());
                                ps.setString(2, applDetailsDobj.getCurrent_state_cd());
                                rs = tmgr.fetchDetachedRowSet_No_release();
                                if (!rs.next()) {
                                    sql = "select regn_no from " + TableList.VA_HSRP
                                            + " where regn_no =? and state_cd=? ";
                                    ps = tmgr.prepareStatement(sql);
                                    ps.setString(1, fitness_dobj.getRegn_no());
                                    ps.setString(2, applDetailsDobj.getCurrent_state_cd());
                                    rs = tmgr.fetchDetachedRowSet_No_release();
                                    if (!rs.next()) {
                                        sql = "select regn_no from " + TableList.VHA_HSRP
                                                + " where appl_no =? and state_cd=? and off_cd=? ";
                                        ps = tmgr.prepareStatement(sql);
                                        ps.setString(1, fitness_dobj.getAppl_no());
                                        ps.setString(2, applDetailsDobj.getCurrent_state_cd());
                                        ps.setInt(3, applDetailsDobj.getCurrent_off_cd());
                                        rs = tmgr.fetchDetachedRowSet_No_release();
                                        if (!rs.next()) {
                                            ServerUtil.insertHsrpDetail(fitness_dobj.getAppl_no(), fitness_dobj.getRegn_no(), "OB", applDetailsDobj.getCurrent_state_cd(), applDetailsDobj.getCurrent_off_cd(), tmgr);
                                        }
                                    }
                                } else {
                                    ServerUtil.insertHsrpDetailFromVhHsrp(fitness_dobj.getRegn_no(), applDetailsDobj.getOwnerDobj().getState_cd(), applDetailsDobj.getOwnerDobj().getOff_cd(), tmgr);
                                }
                            }
                        }
                    }
                }
                //Approve Insurance
                InsImpl.approvalInsurance(tmgr, fitness_dobj.getRegn_no(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), Util.getEmpCode());
            }
            status_dobj.setState_cd(Util.getUserStateCode());
            insertIntoVhaChangedData(tmgr, fitness_dobj.getAppl_no(), changedData);//for saving the data into table those are changed by the user
            ServerUtil.fileFlow(tmgr, status_dobj); //for updateing va_status and vha status for new role,seat for new emp
            tmgr.commit();//Commiting data here....
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Database Error in processing application [ " + fitness_dobj.getAppl_no() + " ]");
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in processing application [ " + fitness_dobj.getAppl_no() + " ]");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }

    public static void insertOrUpdateFitnessMoveFile(TransactionManager tmgr, String regn_no, String chasisNo,
            FitnessDobj fitness_dobj, ArrayList fitCheckList, String stateCd, int offCd) throws VahanException, SQLException {

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
        insertOrUpdateFitness(tmgr, regn_no, chasisNo, fitness_dobj, fitCheckList, stateCd, offCd);
    }

    public static void insertOrUpdateFitness(TransactionManager tmgr, String regn_no, String chasisNo,
            FitnessDobj fitness_dobj, ArrayList fitCheckList, String stateCd, int offCd) throws VahanException, SQLException {

        PreparedStatement ps = tmgr.prepareStatement("Select * from va_fitness where appl_no=?");
        ps.setString(1, fitness_dobj.getAppl_no());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();

        if (rs.next()) {
            insertVhaFitness(tmgr, fitness_dobj.getAppl_no());
            updateVaFitness(tmgr, regn_no, chasisNo, fitness_dobj, fitCheckList);
        } else {
            insertVa_fitness(tmgr, regn_no, chasisNo, fitness_dobj, fitCheckList, stateCd, offCd);
        }
    }

    public static void updateVaFitness(TransactionManager tmgr, String regn_no, String chasisNo,
            FitnessDobj fitness_dobj, ArrayList fitCheckList) throws VahanException {
        try {
            ///////////////Fitness Data Updation////////////////////////
            int preparedPositionCnt = 1;
            String sql = "update " + TableList.VA_FITNESS
                    + " SET chasi_no=?, "
                    + " fit_chk_dt=?, fit_chk_tm=?, "
                    + " fit_result=?,fit_valid_to=?,fit_nid=?, fit_off_cd1=?, "
                    + " fit_off_cd2=?, remark=?, pucc_no=?, pucc_val=?,"
                    + " fare_mtr_no=?,brake=?, steer=?, susp=?, engin=?,"
                    + " tyre=?, horn=?, lamp=?, embo=?,speed=?, paint=?, wiper=?,"
                    + " dimen=?, body=?, fare=?, elec=?, finis=?, road=?, "
                    + " poll=?,transm=?, glas=?, emis=?, rear=?, others=?,"
                    + " op_dt=current_timestamp "
                    + " where appl_no=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);

            ps.setString(preparedPositionCnt++, chasisNo);
            ps.setDate(preparedPositionCnt++, new java.sql.Date(fitness_dobj.getFit_chk_dt().getTime()));
            ps.setString(preparedPositionCnt++, fitness_dobj.getFit_chk_tm());
            ps.setString(preparedPositionCnt++, fitness_dobj.getFit_result());
            if (fitness_dobj.getFit_valid_to() != null) {
                ps.setDate(preparedPositionCnt++, new java.sql.Date(fitness_dobj.getFit_valid_to().getTime()));
            } else {
                ps.setDate(preparedPositionCnt++, null);
            }
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

                if (preparedPositionCnt == 36) {
                    break; // break at ps 36 beacaus at 35th position application no need to set.
                }

                ps.setString(preparedPositionCnt, (String) fitCheckList.get(fitCheckListCnt));
            }
            ps.setString(preparedPositionCnt, fitness_dobj.getAppl_no());
            ps.executeUpdate();

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-RegNo-[" + regn_no + "]-" + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public static void insertVhaFitness(TransactionManager tmgr, String appl_no) throws VahanException, SQLException {

        String sql = "insert into " + TableList.VHA_FITNESS
                + " SELECT current_timestamp as moved_on, ? as moved_by,a.* "
                + " FROM " + TableList.VA_FITNESS
                + " a WHERE appl_no=?";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public static void insertVa_fitness(TransactionManager tmgr, String regn_no, String chasisNo,
            FitnessDobj fitness_dobj, ArrayList fitCheckList, String stateCd, int offCd) throws VahanException {

        PreparedStatement ps = null;
        String sql = null;
        int preparedPositionCnt = 1;

        try {

            sql = "insert into " + TableList.VA_FITNESS
                    + "  (state_cd ,  off_cd, appl_no ,  regn_no ,  chasi_no ,fit_chk_dt, "
                    + "  fit_chk_tm ,  fit_result , fit_valid_to, fit_nid,  fit_off_cd1 , "
                    + "  fit_off_cd2 ,  remark ,  pucc_no , "
                    + "  pucc_val ,  fare_mtr_no ,  brake ,  steer ,   "
                    + "  susp ,  engin ,  tyre ,  horn ,  lamp ,  "
                    + "  embo ,  speed ,  paint ,  wiper ,  dimen ,   "
                    + "  body ,  fare ,  elec ,  finis ,  road ,   "
                    + "  poll ,  transm ,  glas ,  emis ,  rear ,  "
                    + "  others ,  op_dt)"
                    + "  values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,current_timestamp)";

            ps = tmgr.prepareStatement(sql);

            ps.setString(preparedPositionCnt++, stateCd);
            ps.setInt(preparedPositionCnt++, offCd);
            ps.setString(preparedPositionCnt++, fitness_dobj.getAppl_no());
            ps.setString(preparedPositionCnt++, regn_no);
            ps.setString(preparedPositionCnt++, chasisNo);
            ps.setDate(preparedPositionCnt++, new java.sql.Date(fitness_dobj.getFit_chk_dt().getTime()));
            ps.setString(preparedPositionCnt++, fitness_dobj.getFit_chk_tm());
            ps.setString(preparedPositionCnt++, fitness_dobj.getFit_result());
            if (fitness_dobj.getFit_valid_to() != null) {
                ps.setDate(preparedPositionCnt++, new java.sql.Date(fitness_dobj.getFit_valid_to().getTime()));
            } else {
                ps.setDate(preparedPositionCnt++, null);
            }
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
                if (preparedPositionCnt == 40) {
                    break;
                }
                ps.setString(preparedPositionCnt, (String) fitCheckList.get(fitCheckListCnt));
            }

            ps.executeUpdate();

        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + "-" + sqle.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "-" + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }//end of insertVa_fitness

    public static void moveFromVtFitnessToVhFitness(String applNo, String regnNo, String stateCd, int offCd, TransactionManager tmgr) throws VahanException {

        String sql = " Insert into  " + TableList.VH_FITNESS
                + " SELECT state_cd, off_cd, ?,regn_no, chasi_no, fit_chk_dt, fit_chk_tm, "
                + "       fit_result, fit_valid_to, fit_nid, fit_off_cd1, fit_off_cd2, "
                + "       remark, fare_mtr_no, speedgov_no, speedgov_compname, brake, steer, "
                + "       susp, engin, tyre, horn, lamp, embo, speed, paint, wiper, dimen, "
                + "       body, fare, elec, finis, road, poll, transm, glas, emis, rear, "
                + "       others, op_dt,current_timestamp,? "
                + "  FROM " + TableList.VT_FITNESS + " where regn_no=?";

        try {
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, regnNo);
            ps.executeUpdate();

            sql = "Delete from " + TableList.VT_FITNESS + " where regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("Error in Updation of Previous Fitness Information ");
        }

    }

    public static void moveFromVaFitnessToVTFitness(TransactionManager tmgr, String appl_no) throws SQLException {

        String sql = "INSERT into " + TableList.VT_FITNESS
                + "  SELECT fit.state_cd , fit.off_cd, fit.regn_no ,  fit.chasi_no ,  fit_chk_dt ,  "
                + "  fit_chk_tm , fit.fit_result ,  fit.fit_valid_to ,  "
                + "  fit.fit_nid, fit.fit_off_cd1 , fit.fit_off_cd2 ,  fit.remark ,  fit.fare_mtr_no ,"
                + "  null, null, fit.brake , fit.steer ,  fit.susp ,  fit.engin ,  fit.tyre ,  fit.horn , "
                + "  fit.lamp ,  fit.embo ,  fit.speed ,  fit.paint ,  fit.wiper ,  fit.dimen ,"
                + "  fit.body ,  fit.fare ,  fit.elec ,  fit.finis ,  fit.road ,  fit.poll ,"
                + "  fit.transm ,  fit.glas ,  fit.emis ,  fit.rear ,  fit.others ,"
                + "  current_timestamp as op_dt FROM " + TableList.VA_FITNESS
                + "  fit inner join  " + TableList.VA_OWNER
                + "  ownr on fit.appl_no = ownr.appl_no where fit.appl_no=? and fit.state_cd = ?";

        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, appl_no);
        ps.setString(2, Util.getUserStateCode());
        ps.executeUpdate();
    }

    private void updateFitnessAppointment(TransactionManager tmgr, String appl_no) throws Exception {

        String serviceId = null;
        int slotId = 0;
        String dayName = null;
        int dayNum = 0;
        String sql = "SELECT service_id FROM " + TableList.VT_APPT_DTLS + " where appl_no=?";

        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, appl_no);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            serviceId = rs.getString("service_id");
            sql = "select * from " + TableList.VM_HOLIDAY_MAST + " where state_cd=? and (off_cd=? OR off_cd=0) and holiday_date =?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getUserStateCode());
            ps.setInt(2, Util.getUserOffCode());
            ps.setDate(3, new java.sql.Date(new Date().getTime()));
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                throw new VahanException("Application Process Date is Already Declared As Holiday, Kindly Process Application on Working Day");
            } else {

                sql = "select to_char(?::date,'DAY') as day_name, to_char(?::date,'D') as day_num";
                ps = tmgr.prepareStatement(sql);
                ps.setDate(1, new java.sql.Date(new Date().getTime()));
                ps.setDate(2, new java.sql.Date(new Date().getTime()));
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    dayName = rs.getString("day_name");
                    dayNum = rs.getInt("day_num");
                }
                rs = null;
                sql = "SELECT * from " + TableList.VM_SLOT_SERVICE + " where state_cd=? and off_cd=? and slot_day=? and service_id=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, Util.getUserStateCode());
                ps.setInt(2, Util.getUserOffCode());
                ps.setInt(3, dayNum);
                ps.setString(4, serviceId);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    slotId = rs.getInt("slot_id");
                }
                if (slotId != 0) {
                    if (dayNum == 1) {
                        throw new VahanException("Service is not available on holiday" + "Because Current Day is " + dayName);
                    } else {
                        sql = "INSERT INTO " + TableList.VH_APPT_DTLS
                                + "(state_cd, off_cd, appt_id, day_of_week, slot_id, service_id,"
                                + " counter_id, appl_no, regn_no, appointment_dt, op_date, count_reschedule,"
                                + " moved_on, book_status, payment_status, new_appl_no)"
                                + " SELECT state_cd, off_cd, appt_id, day_of_week, slot_id, service_id,"
                                + " counter_id, appl_no, regn_no, appointment_dt, op_date, count_reschedule, current_timestamp,"
                                + " book_status, payment_status, new_appl_no"
                                + " FROM " + TableList.VT_APPT_DTLS + " where appl_no=? AND SERVICE_ID=?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, appl_no);
                        ps.setString(2, serviceId);
                        ps.executeUpdate();

                        sql = "UPDATE  " + TableList.VT_APPT_DTLS
                                + " SET day_of_week=?,slot_id=?, appointment_dt=current_timestamp where appl_no=? AND SERVICE_ID=?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setInt(1, dayNum);
                        ps.setInt(2, slotId);
                        ps.setString(3, appl_no);
                        ps.setString(4, serviceId);
                        ps.executeUpdate();
                    }
                } else {
                    throw new VahanException("Service not Configured for day " + dayName + " .Kindly Configure Through Appointment Module");
                }
            }
        }
    }

    public static int getNewFitnessUpto(Owner_dobj own_dobj) throws VahanException {

        int fitUpto = -1;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;

        Exception e = null;
        VehicleParameters vehParameters = fillVehicleParametersFromDobj(own_dobj);

        try {
            String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].trim().equals(String.valueOf(own_dobj.getVh_class()).trim())) {
                    if (data[i][2].equals(String.valueOf(TableConstants.VM_VEHTYPE_TRANSPORT))) {
                        vehParameters.setVCH_TYPE(TableConstants.VM_VEHTYPE_TRANSPORT);
                        break;
                    } else {
                        vehParameters.setVCH_TYPE(TableConstants.VM_VEHTYPE_NON_TRANSPORT);
                        break;
                    }
                }
            }

            tmgr = new TransactionManager("Fitness_Impl");
            ps = tmgr.prepareStatement("SELECT *  FROM vm_validity_mast where pur_cd=? and state_cd=?");
            ps.setInt(1, TableConstants.VM_TRANSACTION_MAST_FIT_CERT);
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getNewFitnessUpto")) {
                    //break;
                    fitUpto = rs.getInt("new_value");
                }
            }

        } catch (SQLException sq) {
            e = sq;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ee) {
                LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            }
        }

        if (e != null) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Getting Validity Upto Date");
        }

        return fitUpto;
    }

    public static int getReNewFitnessUpto(Owner_dobj own_dobj, int purCode) throws VahanException {
        int fitUpto = 0;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        Exception e = null;
        VehicleParameters vehParameters = null;
        String sql = null;

        if (own_dobj == null || own_dobj.getState_cd() == null) {
            throw new VahanException("Owner Details not Found for Calculating Fitness Upto, Please Contact to the Systemm Administrator.");
        }

        try {
            vehParameters = fillVehicleParametersFromDobj(own_dobj);
            vehParameters.setREGN_UPTO(DateUtils.parseDate(own_dobj.getRegn_upto()));
            tmgr = new TransactionManager("getReNewFitnessUpto");
            sql = "SELECT * FROM " + TableList.VM_VALIDITY_MAST
                    + " WHERE pur_cd=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, purCode);
            ps.setString(2, own_dobj.getState_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getReNewFitnessUpto")) {
                    fitUpto = rs.getInt("re_new_value");
                    break;
                }
            }
        } catch (SQLException sq) {
            e = sq;
        } catch (Exception ex) {
            e = ex;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ee) {
                LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            }
        }

        if (e != null) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Error in Getting Fitness Upto, Please Contact to the System Administrator.");
        }
        if (fitUpto == 0) {
            throw new VahanException("No Configuration found for Renewal of Fitness for this Vehicle , Please Contact to the System Administrator.");
        }
        return fitUpto;
    }

    public static void moveFromVtSpeedGovToVhSpeedGovTo(String applNo, String regnNo, String stateCd, int offCd, TransactionManager tmgr) throws VahanException {

        String sql = " Insert into vh_speed_governor select current_timestamp,?,"
                + " state_cd, off_cd, regn_no,?, sg_no, sg_fitted_on, sg_fitted_at,"
                + " op_dt, emp_cd,sg_type,sg_type_approval_no,sg_test_report_no,sg_fitment_cert_no"
                + "  FROM vt_speed_governor where regn_no=?";
        try {
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, applNo);
            ps.setString(3, regnNo);
            ps.executeUpdate();

            sql = "Delete from vt_speed_governor where regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException("Error in Updation of Previous Speed Governer Information ");
        }

    }

    public static void moveFromVaSpeedGovToVtSpeedGov(String appl_no, TransactionManager tmgr) throws SQLException {
        String sql = "INSERT INTO  " + TableList.VT_SPEED_GOVERNOR
                + " SELECT state_cd, off_cd, regn_no, sg_no, sg_fitted_on, sg_fitted_at, "
                + " current_timestamp, emp_cd,sg_type,sg_type_approval_no,sg_test_report_no,sg_fitment_cert_no  "
                + " FROM  " + TableList.VA_SPEED_GOVERNOR + " WHERE appl_no=? ";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, appl_no);
        ps.executeUpdate();
    }

    private void moveFromVaFitenessToVTFiteness_Renwal(TransactionManager tmgr, FitnessDobj fitness_dobj, TmConfigurationDobj tmConfigurationDobj, Owner_dobj owner_dobj) throws Exception {

        String sql = null;

        sql = "INSERT INTO " + TableList.VH_FITNESS
                + " SELECT state_cd, off_cd, ? as appl_no,regn_no, chasi_no, fit_chk_dt, fit_chk_tm, fit_result, fit_valid_to, fit_nid,  "
                + "        fit_off_cd1, fit_off_cd2, remark, fare_mtr_no, speedgov_no, speedgov_compname,  "
                + "        brake, steer, susp, engin, tyre, horn, lamp, embo, speed, paint,  "
                + "        wiper, dimen, body, fare, elec, finis, road, poll, transm, glas,  "
                + "        emis, rear, others, op_dt, current_timestamp,? as moved_by "
                + "  FROM " + TableList.VT_FITNESS
                + "  WHERE regn_no=?";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, fitness_dobj.getAppl_no());
        ps.setString(2, Util.getEmpCode());
        ps.setString(3, fitness_dobj.getRegn_no());
        ps.executeUpdate();

        String VT_FITNESS_DELETE = "DELETE FROM " + TableList.VT_FITNESS
                + " WHERE regn_no=?";
        ps = tmgr.prepareStatement(VT_FITNESS_DELETE);
        ps.setString(1, fitness_dobj.getRegn_no());
        ps.executeUpdate();

        sql = "INSERT INTO " + TableList.VT_FITNESS
                + " (          state_cd, off_cd, regn_no, chasi_no, fit_chk_dt, fit_chk_tm, fit_result, fit_valid_to, fit_nid,  "
                + "            fit_off_cd1, fit_off_cd2, remark, fare_mtr_no,   "
                + "            brake, steer, susp, engin, tyre, horn, lamp, embo, speed, paint,  "
                + "            wiper, dimen, body, fare, elec, finis, road, poll, transm, glas,  "
                + "            emis, rear, others, op_dt) "
                + "    SELECT  state_cd, off_cd , regn_no, chasi_no, fit_chk_dt, fit_chk_tm, fit_result,  "
                + "       fit_valid_to, fit_nid, fit_off_cd1, fit_off_cd2, remark, "
                + "       fare_mtr_no, brake, steer, susp, engin, tyre, horn, lamp, embo,  "
                + "       speed, paint, wiper, dimen, body, fare, elec, finis, road, poll,  "
                + "       transm, glas, emis, rear, others, current_timestamp as op_dt"
                + "  FROM  " + TableList.VA_FITNESS
                + "  WHERE appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, fitness_dobj.getAppl_no());
        ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

        if (fitness_dobj.getFit_result().equalsIgnoreCase(TableConstants.FitnessResultPass)) {
            //If Application With Conversion then update regn_upto & fit_upto
            boolean fitnessWithConv = false;
            ArrayList<Status_dobj> applStatus = ServerUtil.applicationStatusByApplNo(fitness_dobj.getAppl_no(), Util.getUserStateCode());
            if (applStatus != null && !applStatus.isEmpty()) {
                for (Status_dobj status_dobj : applStatus) {
                    if (status_dobj.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION) {
                        fitnessWithConv = true;
                    }
                }
            }
            if (fitnessWithConv) {
                if (Util.getUserStateCode().equalsIgnoreCase("RJ") || Util.getUserStateCode().equalsIgnoreCase("JH")) {
                    sql = "Select regn_upto from " + TableList.VT_OWNER + " WHERE regn_no=? order by op_dt desc limit 1";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, fitness_dobj.getRegn_no());
                    RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        if (rs.getDate("regn_upto").after(new Date())) {
                            sql = "UPDATE  " + TableList.VT_OWNER
                                    + " SET fit_upto= ?,op_dt=current_timestamp WHERE regn_no=?";
                            ps = tmgr.prepareStatement(sql);
                            ps.setDate(1, new java.sql.Date(fitness_dobj.getFit_valid_to().getTime()));
                            ps.setString(2, fitness_dobj.getRegn_no());
                            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                        } else {
                            sql = "UPDATE  " + TableList.VT_OWNER
                                    + " SET fit_upto= ?,regn_upto=?,op_dt=current_timestamp WHERE regn_no=?";
                            ps = tmgr.prepareStatement(sql);
                            ps.setDate(1, new java.sql.Date(fitness_dobj.getFit_valid_to().getTime()));
                            ps.setDate(2, new java.sql.Date(fitness_dobj.getFit_valid_to().getTime()));
                            ps.setString(3, fitness_dobj.getRegn_no());
                            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                        }
                    }
                } else {
                    sql = "UPDATE  " + TableList.VT_OWNER
                            + " SET fit_upto= ?,regn_upto=?,op_dt=current_timestamp WHERE regn_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setDate(1, new java.sql.Date(fitness_dobj.getFit_valid_to().getTime()));
                    ps.setDate(2, new java.sql.Date(fitness_dobj.getFit_valid_to().getTime()));
                    ps.setString(3, fitness_dobj.getRegn_no());
                    ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                }
            } else {
                if (tmConfigurationDobj != null && tmConfigurationDobj.isRegn_upto_as_fit_upto()) {
                    sql = "UPDATE  " + TableList.VT_OWNER
                            + " SET regn_upto= ?,fit_upto= ?,op_dt=current_timestamp WHERE regn_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setDate(1, new java.sql.Date(fitness_dobj.getFit_valid_to().getTime()));
                    ps.setDate(2, new java.sql.Date(fitness_dobj.getFit_valid_to().getTime()));
                    ps.setString(3, fitness_dobj.getRegn_no());
                    ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                } else {
                    sql = "UPDATE  " + TableList.VT_OWNER
                            + " SET fit_upto= ?,op_dt=current_timestamp WHERE regn_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setDate(1, new java.sql.Date(fitness_dobj.getFit_valid_to().getTime()));
                    ps.setString(2, fitness_dobj.getRegn_no());
                    ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                }
            }
            if ("UP".contains(Util.getUserStateCode())) {
                moveFromVtFitenessFailDetailsToVtFitenessFailDetails(tmgr, fitness_dobj);
                ServerUtil.deleteFromTable(tmgr, fitness_dobj.getRegn_no(), null, TableList.VT_FITNESS_FAIL_DETAILS);
            }

        } else if (fitness_dobj.getFit_result().equalsIgnoreCase(TableConstants.FitnessResultFail)) {
            if ("UP".contains(Util.getUserStateCode())) {
                VmSmartCardHsrpDobj smartCardHsrpDobj = ServerUtil.getVmSmartCardHsrpParameters(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                VehicleParameters vehicleParameters = fillVehicleParametersFromDobj(owner_dobj);
                if (smartCardHsrpDobj != null && isCondition(replaceTagValues(smartCardHsrpDobj.getAutomaticFitness(), vehicleParameters), "moveFromVaFitenessToVTFiteness_Renwal")) {
                    sql = "select * from " + TableList.VT_FITNESS_FAIL_DETAILS + " where regn_no=? and state_cd=? and off_cd=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, fitness_dobj.getRegn_no());
                    ps.setString(2, fitness_dobj.getState_cd());
                    ps.setInt(3, fitness_dobj.getOff_cd());
                    RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        moveFromVtFitenessFailDetailsToVtFitenessFailDetails(tmgr, fitness_dobj);
                        sql = "UPDATE " + TableList.VT_FITNESS_FAIL_DETAILS + " set failed_counter = failed_counter +1,appl_no=? where regn_no=? and state_cd=? and off_cd=?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, fitness_dobj.getAppl_no());
                        ps.setString(2, fitness_dobj.getRegn_no());
                        ps.setString(3, fitness_dobj.getState_cd());
                        ps.setInt(4, fitness_dobj.getOff_cd());
                        ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                    } else {
                        sql = "INSERT INTO vt_fitness_fail_details(state_cd, off_cd, regn_no, appl_no, failed_counter, fit_chk_dt,op_dt) "
                                + "VALUES (?, ?, ?, ?, ?, ?,current_timestamp)";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, fitness_dobj.getState_cd());
                        ps.setInt(2, fitness_dobj.getOff_cd());
                        ps.setString(3, fitness_dobj.getRegn_no());
                        ps.setString(4, fitness_dobj.getAppl_no());
                        ps.setInt(5, 1);
                        ps.setDate(6, new java.sql.Date(fitness_dobj.getFit_chk_dt().getTime()));
                        ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
                    }
                }
            }
            sql = "UPDATE  " + TableList.VT_FITNESS
                    + " SET fit_valid_to=?, fit_nid=? WHERE regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setNull(1, java.sql.Types.DATE);
            ps.setNull(2, java.sql.Types.DATE);
            ps.setString(3, fitness_dobj.getRegn_no());
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
        }
        ServerUtil.deleteFromTable(tmgr, null, fitness_dobj.getAppl_no(), TableList.VA_FITNESS);
    }

    private void moveFromVaFitenessToVTFitenessTemp(TransactionManager tmgr, String applNo) throws VahanException {

        String sql = "INSERT INTO " + TableList.VT_FITNESS_TEMP + " ( "
                + "            state_cd, off_cd, appl_no, regn_no, chasi_no, fit_chk_dt, fit_chk_tm,"
                + "            fit_result, fit_valid_to, fit_nid, fit_off_cd1, fit_off_cd2,"
                + "            remark, pucc_no, pucc_val, fare_mtr_no, brake, steer, susp, engin,"
                + "            tyre, horn, lamp, embo, speed, paint, wiper, dimen, body, fare,"
                + "            elec, finis, road, poll, transm, glas, emis, rear, others, op_dt) "
                + "   SELECT   state_cd, off_cd, appl_no, regn_no, chasi_no, fit_chk_dt, fit_chk_tm,"
                + "            fit_result, fit_valid_to, fit_nid, fit_off_cd1, fit_off_cd2,"
                + "            remark, pucc_no, pucc_val, fare_mtr_no, brake, steer, susp, engin,"
                + "            tyre, horn, lamp, embo, speed, paint, wiper, dimen, body, fare,"
                + "            elec, finis, road, poll, transm, glas, emis, rear, others, op_dt as current_timestamp "
                + "  FROM " + TableList.VA_FITNESS
                + "  WHERE appl_no=? ";
        try {
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "" + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during movement of data to other state fitness Inspection table, Please contact to the system administrator.");
        }
    }

    private void moveFromVtFitenessTempToVhFitenessTemp(TransactionManager tmgr, String regnNo, String reason, String empCd) throws VahanException {

        String sql = "INSERT INTO " + TableList.VH_FITNESS_TEMP + " ( "
                + "            moved_on, moved_by, reason, state_cd, off_cd, appl_no, regn_no, chasi_no, fit_chk_dt, fit_chk_tm,"
                + "            fit_result, fit_valid_to, fit_nid, fit_off_cd1, fit_off_cd2,"
                + "            remark, pucc_no, pucc_val, fare_mtr_no, brake, steer, susp, engin,"
                + "            tyre, horn, lamp, embo, speed, paint, wiper, dimen, body, fare,"
                + "            elec, finis, road, poll, transm, glas, emis, rear, others, op_dt) "
                + "   SELECT   current_timestamp as moved_on, ? as moved_by, ? as reason, state_cd, off_cd, appl_no, regn_no, chasi_no, fit_chk_dt, fit_chk_tm,"
                + "            fit_result, fit_valid_to, fit_nid, fit_off_cd1, fit_off_cd2,"
                + "            remark, pucc_no, pucc_val, fare_mtr_no, brake, steer, susp, engin,"
                + "            tyre, horn, lamp, embo, speed, paint, wiper, dimen, body, fare,"
                + "            elec, finis, road, poll, transm, glas, emis, rear, others, op_dt "
                + "  FROM " + TableList.VT_FITNESS_TEMP
                + "  WHERE regn_no=? ";
        try {
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCd);
            ps.setString(2, reason);
            ps.setString(3, regnNo);
            ps.executeUpdate();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "" + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during movement of data to other state fitness Inspection table, Please contact to the system administrator.");
        }
    }

    public String[] present_fitness_details(String regn_no) {
        String present_fitnessDetails[] = null;

        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("present_c_addressDetails");
            String sql = "SELECT regn_no,'Result '|| case when fit_result is not null and fit_result ='Y' then '[Pass]' else '[Fail]' end ||', Valid Upto [ '|| to_char(fit_valid_to,'dd-MON-yyyy')||'],NID(Next Inspection Date) [ '|| to_char(fit_nid,'dd-MON-yyyy')||'] , Fare Meter No ['||case when fare_mtr_no is not null then fare_mtr_no else 'NA' end  ||'], Speed Gov. No['||case when speedgov_no is not null then speedgov_no else 'NA' end ||'], Speed Gov Company ['||case when speedgov_compname is not null then speedgov_compname else 'NA' end ||'], Remark ['||case when remark is not null then remark else 'NA' end ||'], Fitness Done By [' "
                    + "||case when fit_off_cd1::text is not null and fit_off_cd1::text !='0' then fit_off_cd1::text else 'bbb' end ||case when fit_off_cd2::text is not null and fit_off_cd2::text !='0' then ','||fit_off_cd2::text else 'aaaa'  end ||']' as current_fit_detail "
                    + "FROM vt_fitness where regn_no=? order by op_dt desc limit 1";

            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                present_fitnessDetails = new String[1];
                present_fitnessDetails[0] = rs.getString("current_fit_detail");

            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        return present_fitnessDetails;

    }

    public static boolean isFitnessFeePaid(String applNo) {
        boolean fitFeePaid = false;
        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager("isFitnessFeePaid");
            String sql = "SELECT appl_no  FROM vp_appl_rcpt_mapping map,vt_fee fee where map.rcpt_no=fee.rcpt_no and map.state_cd=fee.state_cd and map.off_cd=fee.off_cd "
                    + "  and map.appl_no=? and pur_cd=" + TableConstants.VM_TRANSACTION_MAST_FIT_CERT;
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                fitFeePaid = true;
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

        return fitFeePaid;
    }

    public boolean isFailedFitness(String regnNo, int purCd, int offCd, String stateCd) throws VahanException {
        boolean isFitnessFailed = false;
        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager("isFailedFitness");

            String sql = " SELECT regn_no, op_dt FROM " + TableList.VT_FITNESS
                    + " WHERE regn_no = ? and fit_result=? order by op_dt desc limit 1";
            PreparedStatement ps = tmgr.prepareStatement(sql);

            ps.setString(1, regnNo);
            ps.setString(2, TableConstants.FitnessResultFail);

            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                isFitnessFailed = true;
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

        return isFitnessFailed;
    }

    public VmValidityMastDobj getVmValidityMastDobj(VehicleParameters vehParameters, int purCd, String stateCd) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        VmValidityMastDobj validityMastDobj = null;
        Exception e = null;
        try {

            tmgr = new TransactionManager("getVmValidityMastDobj");
            sql = "SELECT *  FROM " + TableList.VM_VALIDITY_MAST
                    + " WHERE pur_cd=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, purCd);
            ps.setString(2, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getVmValidityMastDobj")) {

                    validityMastDobj = new VmValidityMastDobj();
                    validityMastDobj.setState_cd(rs.getString("state_cd"));
                    validityMastDobj.setSr_no(rs.getInt("sr_no"));
                    validityMastDobj.setCondition_formula(rs.getString("condition_formula"));
                    validityMastDobj.setPur_cd(rs.getInt("pur_cd"));
                    validityMastDobj.setNew_value(rs.getInt("new_value"));
                    validityMastDobj.setRe_new_value(rs.getInt("re_new_value"));
                    validityMastDobj.setMod(rs.getString("mod"));
                    break;
                }
            }

        } catch (SQLException sq) {
            e = sq;
            LOGGER.error(sq.toString() + " " + sq.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ee) {
                LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            }
        }

        if (e != null) {
            throw new VahanException("Error in Getting Validity Master based on Criteria Condion.");
        }

        return validityMastDobj;
    }

    public int getVehAgeValidity(Owner_dobj ownerDobj) throws VahanException {
        String sql = null;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        int vehAge = 0;
        Exception e = null;
        try {
            tmgr = new TransactionManager("getVehAgeValidity");
            sql = "SELECT *  FROM " + TableList.VM_VEHICLE_AGE_VALIDITY
                    + " WHERE state_cd=? and pmt_type in(?,0) and pmt_catg in(?,0) and fuel in(?,0) "
                    + " order by pmt_type desc, pmt_catg desc";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, ownerDobj.getState_cd());
            ps.setInt(2, ownerDobj.getPmt_type());
            ps.setInt(3, ownerDobj.getPmt_catg());
            ps.setInt(4, ownerDobj.getFuel());
            RowSet rs = tmgr.fetchDetachedRowSet();

            int counter = 0;
            while (rs.next()) {
                vehAge = rs.getInt("veh_age");
                if ((rs.getInt("pmt_type") == ownerDobj.getPmt_type() && rs.getInt("pmt_catg") == 0)
                        || (rs.getInt("pmt_type") == ownerDobj.getPmt_type() && rs.getInt("pmt_catg") == ownerDobj.getPmt_catg())) {
                    break;
                }
                counter++;
                if (counter > 1) {
                    throw new VahanException("Multiple Record Found in Database for Calculating Vehicle Age Validity, Please Contact to the System Administrator.");
                }
            }

        } catch (SQLException sq) {
            e = sq;
            LOGGER.error(sq.toString() + " " + sq.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception ee) {
                LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            }
        }

        if (e != null) {
            throw new VahanException("Error in Getting Maximum Vehicle Age of the Vehicle, Please Contact to the System Administrator.");
        }

        if (vehAge == 0) {

            if (ownerDobj.getPmt_type() > 0) {
                throw new VahanException("Maximum Age of Vehicle Can not be Zero, Please Contact to the System Administrator.");
            }

            String transportVchType = ServerUtil.getTransportVchType(ownerDobj.getVh_class());

            if (transportVchType != null && ownerDobj.getPmt_type() == 0) {

                TmConfigurationFitnessDobj tmConfigurationFitnessDobj = getFitnessConfiguration(ownerDobj.getState_cd());
                VehicleParameters parameters = FormulaUtils.fillVehicleParametersFromDobj(ownerDobj);
                boolean isPermitCheckExempted = false;
                if (tmConfigurationFitnessDobj != null && tmConfigurationFitnessDobj.getPermit_exemption_check_for_veh_age_calculation() != null) {
                    if (isCondition(replaceTagValues(tmConfigurationFitnessDobj.getPermit_exemption_check_for_veh_age_calculation(), parameters), "getVehAgeValidity")) {
                        isPermitCheckExempted = true;
                    }
                }

                boolean isPermitDetailsNotAvlbl = false;

                if (("P").contains(transportVchType) && !isPermitCheckExempted) {
                    isPermitDetailsNotAvlbl = true;
                }
                if (("G").contains(transportVchType) && !isPermitCheckExempted && !((ownerDobj.getLd_wt() <= 3000) || (ownerDobj.getGcw() <= 3000))) {
                    isPermitDetailsNotAvlbl = true;
                }
                if (isPermitDetailsNotAvlbl) {
                    TemporaryPermitImpl permitImpl = new TemporaryPermitImpl();
                    TemporaryPermitDobj permitDobj = permitImpl.getTempPermitDetials(ownerDobj.getRegn_no());
                    if (permitDobj != null && permitDobj.getValid_upto() != null
                            && ownerDobj.getFit_dt() != null
                            && DateUtils.compareDates(permitDobj.getValid_upto(), ownerDobj.getFit_dt()) == 2) {
                    } else {
                        throw new VahanException("Permit Details not found on this Vehicle and Fitness Validity is Dependent on the Permit, Please Contact to the System Administrator.");
                    }
                }
            }
        }

        return vehAge;
    }

    public Date getMaxRegnUptoDate(Owner_dobj ownerDobj) throws Exception {
        Date maxRegnUpto = null;
        PermitDetailDobj permitDetailDobj = PermitDetailImpl.getPermitdetailsFromRegnNo(ownerDobj.getRegn_no());
        if (permitDetailDobj == null) {
            PassengerPermitDetailImpl permitDetailImpl = new PassengerPermitDetailImpl();
            PassengerPermitDetailDobj permitDetailDobjHistory = permitDetailImpl.getPermitHistory(ownerDobj.getRegn_no(), ownerDobj.getState_cd());
            boolean vehSurrPermCacelMode = CommonPermitPrintImpl.isVehicleSurrInPermanentMode(ownerDobj.getRegn_no());
            if (permitDetailDobjHistory != null && (vehSurrPermCacelMode || "DL".equalsIgnoreCase(ownerDobj.getState_cd()))) {
                permitDetailDobj = new PermitDetailDobj();
                permitDetailDobj.setPmt_type(Integer.parseInt(permitDetailDobjHistory.getPmt_type()));
                permitDetailDobj.setPmt_catg(Integer.parseInt(permitDetailDobjHistory.getPmtCatg()));
            } else {
                permitDetailDobj = new PermitDetailDobj();
                permitDetailDobj.setPmt_type(0);
                permitDetailDobj.setPmt_catg(0);
            }
        }

        ownerDobj.setPmt_type(permitDetailDobj.getPmt_type());
        ownerDobj.setPmt_catg(permitDetailDobj.getPmt_catg());
        int vehAge = getVehAgeValidity(ownerDobj);
        if (vehAge > 0) {
            maxRegnUpto = ServerUtil.dateRange(ownerDobj.getRegn_dt(), vehAge, 0, -1);
        }

        return maxRegnUpto;
    }

    public SpeedGovernorDobj getSpeedGovernorDetails(String applNo) throws VahanException {
        SpeedGovernorDobj dobj = null;
        String sql = "SELECT * FROM " + TableList.VA_SPEED_GOVERNOR + " WHERE appl_no=?";
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getSpeedGovernorDetails");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new SpeedGovernorDobj();
                dobj.setAppl_no(rs.getString("appl_no"));
                dobj.setState_cd(rs.getString("state_cd"));
                dobj.setOff_cd(rs.getInt("off_cd"));
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setSg_no(rs.getString("sg_no"));
                dobj.setSg_fitted_on(rs.getDate("sg_fitted_on"));
                dobj.setSg_fitted_at(rs.getString("sg_fitted_at"));
                dobj.setSgGovType(rs.getInt("sg_type"));
                dobj.setSgTypeApprovalNo(rs.getString("sg_type_approval_no"));
                dobj.setSgTestReportNo(rs.getString("sg_test_report_no"));
                dobj.setSgFitmentCerticateNo(rs.getString("sg_fitment_cert_no"));
            }

        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex);
                }
            }
        }

        return dobj;
    }

    //to get Speed Governor Details from Service (imported data from Scheduler Service) ---added by Amitesh
    public SpeedGovernorDobj getSpeedGovernorDetailsFrmServiceData(String applNo, String regnNo, int offCd, String stateCd) throws VahanException {
        SpeedGovernorDobj dobj = null;
        String sql = "select regn_no,sg_no,sg_fitted_on,sg_fitted_at,sg_type,sg_type_approval_no,sg_test_report_no,sg_fitment_cert_no from get_sg_rf_details (?)";
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("getSpeedGovernorDetailsFrmServiceData");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                dobj = new SpeedGovernorDobj();
                dobj.setAppl_no(applNo);
                dobj.setState_cd(stateCd);
                dobj.setOff_cd(offCd);
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setSg_no(rs.getString("sg_no"));
                dobj.setSg_fitted_on(rs.getDate("sg_fitted_on"));
                dobj.setSg_fitted_at(rs.getString("sg_fitted_at"));
                dobj.setSgGovType(rs.getInt("sg_type"));
                dobj.setSgTypeApprovalNo(rs.getString("sg_type_approval_no"));
                dobj.setSgTestReportNo(rs.getString("sg_test_report_no"));
                dobj.setSgFitmentCerticateNo(rs.getString("sg_fitment_cert_no"));
            }

        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            dobj = null;
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex);
                }
            }
        }

        return dobj;
    }

//to get SLD from Service
    public SpeedGovernorDobj getSLDInfo(String applNo, String regnNo, int offCd, String stateCd) throws VahanException {
        SpeedGovernorDobj dobj = null;
        String sql = "select regn_no, sld_serial_no, roto_seal_no, speed, sld_manufacturer, fitted_date, fitment_center, sg_type,tac_no from vahan4.getsldinfo (?)";
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("getSLDInfo");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new SpeedGovernorDobj();
                dobj.setAppl_no(applNo);
                dobj.setState_cd(stateCd);
                dobj.setOff_cd(offCd);
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setSg_no(rs.getString("sld_serial_no"));
                dobj.setSg_fitted_on(rs.getDate("fitted_date"));
                dobj.setSg_fitted_at(rs.getString("sld_manufacturer"));
                dobj.setSgGovType(rs.getInt("sg_type"));
                dobj.setSgTypeApprovalNo(rs.getString("tac_no"));
                dobj.setSgTestReportNo("NA");
                dobj.setSgFitmentCerticateNo("NA");
            }
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            dobj = null;
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex);
                }
            }
        }
        return dobj;
    }

    public SpeedGovernorDobj getSpeedGovernorDetails(String regnNo, int offCd, String stateCd) throws VahanException {
        SpeedGovernorDobj dobj = null;
        String sql = "SELECT state_cd, off_cd, regn_no, sg_no, sg_fitted_on, sg_fitted_at, \n"
                + " op_dt, emp_cd, sg_type, sg_type_approval_no, sg_test_report_no, \n"
                + " sg_fitment_cert_no FROM vt_speed_governor where regn_no=? and state_cd=? and off_cd=?";
        TransactionManagerReadOnly tmgr = null;
        try {
            tmgr = new TransactionManagerReadOnly("getSpeedGovernorDetails");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                dobj = new SpeedGovernorDobj();
                dobj.setState_cd(stateCd);
                dobj.setOff_cd(offCd);
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setSg_no(rs.getString("sg_no"));
                dobj.setSg_fitted_on(rs.getDate("sg_fitted_on"));
                dobj.setSg_fitted_at(rs.getString("sg_fitted_at"));
                dobj.setSgGovType(rs.getInt("sg_type"));
                dobj.setSgTypeApprovalNo(rs.getString("sg_type_approval_no"));
                dobj.setSgTestReportNo(rs.getString("sg_test_report_no"));
                dobj.setSgFitmentCerticateNo(rs.getString("sg_fitment_cert_no"));
            }
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            dobj = null;
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex);
                }
            }
        }
        return dobj;
    }

    //to get Reflective Tape Details from Service (imported data from Scheduler Service)
    public ReflectiveTapeDobj getVtReflectiveTapeFrmServiceData(String applNo, String regnNo, int offCd, String stateCd) throws VahanException {
        ReflectiveTapeDobj refDobj = null;
        String sql = "select regn_no,rt_certificate_no,rt_fitment_date,rt_manu_name from get_sg_rf_details (?)";
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getReflectiveTapeFrmServiceData");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                refDobj = new ReflectiveTapeDobj();
                refDobj.setStateCd(stateCd);
                refDobj.setOffCd(offCd);
                refDobj.setRegn_no(rs.getString("regn_no"));
                refDobj.setApplNo(applNo);
                refDobj.setCertificateNo(rs.getString("rt_certificate_no"));
                refDobj.setFitmentDate(rs.getDate("rt_fitment_date"));
                refDobj.setManuName(rs.getString("rt_manu_name"));
            }

        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            refDobj = null;
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex);
                }
            }
        }
        return refDobj;
    }

    //for check, Is Vehicle Fitness Test processed by SUVAS Center
    public boolean isSUVASFitness(String regnNo, Date fitNid) {
        boolean isSUVASFitness = false;
        TransactionManager tmgr = null;
        Date fitChkDateSUVASSrvc = null;

        try {
            tmgr = new TransactionManager("isSUVASFitness");

            String sql = "select regn_no,upload_dt,op_dt from get_sg_rf_details (?)";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);

            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                fitChkDateSUVASSrvc = format.parse(rs.getString("upload_dt"));

                if (fitChkDateSUVASSrvc != null && fitNid != null) {
                    if (fitChkDateSUVASSrvc.compareTo(fitNid) >= 0) {
                        isSUVASFitness = true;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            isSUVASFitness = false;
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        return isSUVASFitness;
    }

    public static void insertUpdateVaSpeedGovernor(SpeedGovernorDobj dobj, TransactionManager tmgr) throws VahanException {

        try {
            if (dobj == null) {
                return;
            }
            String sql = "SELECT * FROM " + TableList.VA_SPEED_GOVERNOR + " WHERE appl_no=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getAppl_no());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                insertIntoVhaSpeedGovernor(dobj.getAppl_no(), tmgr);
                updateVaSpeedGovernor(dobj, tmgr);
            } else {
                insertIntoVaSpeedGovernor(dobj, tmgr);
            }
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public static void updateVaSpeedGovernor(SpeedGovernorDobj dobj, TransactionManager tmgr) throws VahanException {
        String sql = "UPDATE " + TableList.VA_SPEED_GOVERNOR
                + " SET appl_no=?, state_cd=?, off_cd=?,  sg_no=?, sg_fitted_on=?, "
                + " sg_fitted_at=?,op_dt=current_timestamp,"
                + " sg_type=?,sg_type_approval_no=?,sg_test_report_no=?,sg_fitment_cert_no=? "
                + " WHERE appl_no=? ";
        try {
            int i = 1;
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(i++, dobj.getAppl_no());
            ps.setString(i++, dobj.getState_cd());
            ps.setInt(i++, dobj.getOff_cd());
            //ps.setString(i++, dobj.getRegn_no());
            ps.setString(i++, dobj.getSg_no());
            ps.setDate(i++, new java.sql.Date(dobj.getSg_fitted_on().getTime()));
            ps.setString(i++, dobj.getSg_fitted_at());
            ps.setInt(i++, dobj.getSgGovType());
            ps.setString(i++, dobj.getSgTypeApprovalNo());
            ps.setString(i++, dobj.getSgTestReportNo());
            ps.setString(i++, dobj.getSgFitmentCerticateNo());

            ps.setString(i++, dobj.getAppl_no());
            ps.executeUpdate();

        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            throw new VahanException("Error in Update Speed Governor Details");
        }
    }

    public static void insertIntoVaSpeedGovernor(SpeedGovernorDobj dobj, TransactionManager tmgr) throws VahanException {

        String sql = "INSERT INTO " + TableList.VA_SPEED_GOVERNOR
                + " (appl_no, state_cd, off_cd, regn_no, sg_no, sg_fitted_on, sg_fitted_at,op_dt, emp_cd,"
                + "  sg_type,sg_type_approval_no,sg_test_report_no,sg_fitment_cert_no) "
                + " VALUES (?, ?, ?, ?, ?, ?, ?, current_timestamp, ?,"
                + "?,?,?,?)";
        try {
            int i = 1;
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(i++, dobj.getAppl_no());
            ps.setString(i++, dobj.getState_cd());
            ps.setInt(i++, dobj.getOff_cd());
            ps.setString(i++, dobj.getRegn_no());
            ps.setString(i++, dobj.getSg_no());
            ps.setDate(i++, new java.sql.Date(dobj.getSg_fitted_on().getTime()));
            ps.setString(i++, dobj.getSg_fitted_at());
            ps.setString(i++, Util.getEmpCode());
            ps.setInt(i++, dobj.getSgGovType());
            ps.setString(i++, dobj.getSgTypeApprovalNo());
            ps.setString(i++, dobj.getSgTestReportNo());
            ps.setString(i++, dobj.getSgFitmentCerticateNo());
            ps.executeUpdate();

        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }

    }

    public static void deleteVaSpeedGovernor(String applNo, TransactionManager tmgr) throws VahanException {
        String sql = "DELETE FROM " + TableList.VA_SPEED_GOVERNOR + "  WHERE appl_no=? ";
        try {
            int i = 1;
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(i++, applNo);
            ps.executeUpdate();

        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }

    }

    public static void insertIntoVhaSpeedGovernor(String applNo, TransactionManager tmgr) throws VahanException {
        String sql = "INSERT INTO " + TableList.VHA_SPEED_GOVERNOR
                + " SELECT current_timestamp, ?, * FROM " + TableList.VA_SPEED_GOVERNOR + " WHERE appl_no=? ";
        try {
            int i = 1;
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(i++, Util.getEmpCode());
            ps.setString(i++, applNo);
            ps.executeUpdate();

        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }

    }

    public void deleteFromVaSpeedGovernor(String applNo) throws VahanException {
        String sql = "DELETE FROM " + TableList.VA_SPEED_GOVERNOR + "  WHERE appl_no=? ";
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("deleteVaSpeedGovernor");
            insertIntoVhaSpeedGovernor(applNo, tmgr);
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.executeUpdate();
            tmgr.commit();
        } catch (VahanException vme) {
            throw vme;
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
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

    public void deleteFromVaReflectiveTape(String applNo, String empCode) throws VahanException {
        String sql = "DELETE FROM " + TableList.VA_REFLECTIVE_TAPE + "  WHERE appl_no=? ";
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("deleteFromVaReflectiveTape");
            insertIntoVhaReflectiveTape(tmgr, applNo, empCode);
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.executeUpdate();
            tmgr.commit();
        } catch (VahanException vme) {
            throw vme;
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
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

    public void insertOrUpdateInspection(TransactionManager tmgr, InspectionDobj inspectionDobj) throws Exception {

        if (inspectionDobj == null) {
            return;
        }
        InspectionDobj isInspecDobjExist = getVaInspectionDobj(inspectionDobj.getAppl_no());
        if (isInspecDobjExist != null) {
            insertIntoVhaInspection(tmgr, inspectionDobj.getAppl_no(), Util.getEmpCode());
            updateVaInspection(tmgr, inspectionDobj);
        } else {
            insertVaInspection(tmgr, inspectionDobj);
        }
    }

    public InspectionDobj getVaInspectionDobj(String applNo) throws VahanException {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        InspectionDobj inspectionDobj = null;
        VahanException vahanexecption = null;
        String sql = null;
        try {
            applNo = applNo.toUpperCase().trim();
            sql = "SELECT *  FROM " + TableList.VA_INSPECTION
                    + " where appl_no=?";
            tmgr = new TransactionManager("getVaInspectionDobj");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                inspectionDobj = new InspectionDobj();
                inspectionDobj.setState_cd(rs.getString("state_cd"));
                inspectionDobj.setOff_cd(rs.getInt("off_cd"));
                inspectionDobj.setAppl_no(rs.getString("appl_no"));
                inspectionDobj.setRegn_no(rs.getString("regn_no"));
                inspectionDobj.setInsp_dt(rs.getDate("insp_dt"));
                inspectionDobj.setRemark(rs.getString("remark"));
                inspectionDobj.setOp_dt(rs.getDate("op_dt"));
                inspectionDobj.setFit_off_cd1(rs.getInt("fit_off_cd1"));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            vahanexecption = new VahanException(TableConstants.SomthingWentWrong);
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
        return inspectionDobj;
    }

    public void insertVaInspection(TransactionManager tmgr, InspectionDobj inspectionDobj) throws VahanException {

        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;
        try {

            sql = "INSERT INTO " + TableList.VA_INSPECTION
                    + "  (state_cd,off_cd,appl_no,regn_no,insp_dt,remark,op_dt,fit_off_cd1)"
                    + "  values(?,?,?,?,?,?,current_timestamp,?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(pos++, inspectionDobj.getState_cd());
            ps.setInt(pos++, inspectionDobj.getOff_cd());
            ps.setString(pos++, inspectionDobj.getAppl_no());
            ps.setString(pos++, inspectionDobj.getRegn_no());
            ps.setDate(pos++, new java.sql.Date(inspectionDobj.getInsp_dt().getTime()));
            ps.setString(pos++, inspectionDobj.getRemark());
            ps.setInt(pos++, inspectionDobj.getFit_off_cd1());
            ps.executeUpdate();

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }//end of insertVaInspection  

    public void insertVtInspection(TransactionManager tmgr, InspectionDobj inspectionDobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;
        try {
            sql = "INSERT INTO " + TableList.VT_INSPECTION
                    + " SELECT state_cd,off_cd,appl_no,regn_no,insp_dt,remark,current_timestamp as op_dt,fit_off_cd1 FROM " + TableList.VA_INSPECTION
                    + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(pos++, inspectionDobj.getAppl_no());
            ps.executeUpdate();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }//end of insertVtInspection  

    public void insertVtToVhInspection(TransactionManager tmgr, InspectionDobj inspectionDobj, String empCode) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;
        try {
            sql = "INSERT INTO " + TableList.VH_INSPECTION
                    + " SELECT current_timestamp as moved_on,? as moved_by ,state_cd,off_cd,appl_no,regn_no,insp_dt,remark,op_dt,fit_off_cd1 FROM " + TableList.VT_INSPECTION
                    + " WHERE regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(pos++, empCode);
            ps.setString(pos++, inspectionDobj.getRegn_no());
            ps.executeUpdate();
            sql = "DELETE FROM  " + TableList.VT_INSPECTION + " WHERE regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, inspectionDobj.getRegn_no());
            ps.executeUpdate();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }//end of insertVtToVhInspection  

    public void updateVaInspection(TransactionManager tmgr, InspectionDobj inspectionDobj) throws VahanException {

        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;
        try {
            sql = "UPDATE " + TableList.VA_INSPECTION
                    + "  SET insp_dt=?,remark=?,op_dt=current_timestamp,fit_off_cd1=?,regn_no=?"
                    + "  WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setDate(pos++, new java.sql.Date(inspectionDobj.getInsp_dt().getTime()));
            ps.setString(pos++, inspectionDobj.getRemark());
            ps.setInt(pos++, inspectionDobj.getFit_off_cd1());
            ps.setString(pos++, inspectionDobj.getRegn_no());
            ps.setString(pos++, inspectionDobj.getAppl_no());
            ps.executeUpdate();

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }//end of insertVaInspection 

    public void insertIntoVhaInspection(TransactionManager tmgr, String applNo, String empCode) throws VahanException {
        String sql = null;
        try {
            int i = 1;
            sql = "INSERT INTO " + TableList.VHA_INSPECTION
                    + " SELECT current_timestamp, ?, * FROM " + TableList.VA_INSPECTION + " WHERE appl_no=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(i++, empCode);
            ps.setString(i++, applNo);
            ps.executeUpdate();
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + "[appNo-" + applNo + "] " + ee.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public TmConfigurationFitnessDobj getFitnessConfiguration(String stateCd) throws VahanException {

        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        TmConfigurationFitnessDobj fitnessConfigDobj = null;
        String sql = null;
        try {
            sql = "SELECT * FROM " + TableList.TM_CONFIGURATION_FITNESS
                    + " WHERE state_cd = ?";
            tmgr = new TransactionManagerReadOnly("FitnessImpl.getFitnessConfiguration");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next())//found
            {
                fitnessConfigDobj = new TmConfigurationFitnessDobj();
                fitnessConfigDobj.setState_cd(rs.getString("state_cd"));
                fitnessConfigDobj.setLogged_in_user_is_fitness_officer(rs.getBoolean("logged_in_user_is_fitness_officer"));
                fitnessConfigDobj.setReflective_tape_allowed(rs.getBoolean("is_reflective_tape_allowed"));
                fitnessConfigDobj.setSkip_fee_for_failed_fitness(rs.getBoolean("skip_fee_for_failed_fitness"));
                fitnessConfigDobj.setFitness_revoke_allowed(rs.getBoolean("fitness_revoke_allowed"));
                fitnessConfigDobj.setFitness_revoke_allowed_days(rs.getInt("fitness_revoke_allowed_days"));
                fitnessConfigDobj.setSkip_user_chg_fitness_centre(rs.getBoolean("skip_user_chg_fitness_centre"));
                fitnessConfigDobj.setGrace_days_for_failed_fitness(rs.getInt("grace_days_for_failed_fitness"));
                fitnessConfigDobj.setCheck_for_multiple_time_failed_fitness(rs.getBoolean("check_for_multiple_time_failed_fitness"));
                fitnessConfigDobj.setFit_upto_from_insp_dt_even_appl_before_fit_expiry(rs.getBoolean("fit_upto_from_insp_dt_even_appl_before_fit_expiry"));
                fitnessConfigDobj.setPermit_exemption_check_for_veh_age_calculation(rs.getString("permit_exemption_check_for_veh_age_calculation"));
                fitnessConfigDobj.setMultiple_fit_officer(rs.getBoolean("multiple_fit_officer"));
                fitnessConfigDobj.setSkip_fitness_check_if_veh_age_expire(rs.getString("skip_fitness_check_if_veh_age_expire"));
                fitnessConfigDobj.setNewVehicleFitness(rs.getBoolean("is_new_veh_fitness"));
                fitnessConfigDobj.setFitness_done_in_suvas_center(rs.getBoolean("is_fitness_done_in_suvas_center"));
                fitnessConfigDobj.setSpeed_governor_reflective_tape_data_from_suvas(rs.getBoolean("is_speed_governor_reflective_tape_data_from_suvas"));
                fitnessConfigDobj.setDocument_upload(rs.getBoolean("is_document_upload"));
                fitnessConfigDobj.setAllowInspectionForVeh(rs.getString("allow_inspection_for_veh"));
                fitnessConfigDobj.setFcAfterHSRP(rs.getBoolean("fc_after_hsrp"));
                fitnessConfigDobj.setCheckSldFitment(rs.getString("check_sld_fitment"));
                fitnessConfigDobj.setUploadFitnessImageAllowed(rs.getBoolean("upload_fit_img_allowed"));
                fitnessConfigDobj.setCheck_vltd(rs.getString("check_vltd"));
            }
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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
        return fitnessConfigDobj;
    }

    public ReflectiveTapeDobj getVaOrVtReflectiveTapeDobj(String applNo, String regnNo) throws VahanException {
        TransactionManager tmgr = null;
        ReflectiveTapeDobj refDobj = null;
        String sql = "";
        String param = "";
        try {
            if (regnNo != null) {
                regnNo = regnNo.toUpperCase().trim();
                sql = "SELECT *  FROM " + TableList.VT_REFLECTIVE_TAPE
                        + " where regn_no=? order by op_dt desc";
                param = regnNo;
            }

            if (applNo != null) {
                applNo = applNo.toUpperCase().trim();
                sql = "SELECT *  FROM " + TableList.VA_REFLECTIVE_TAPE
                        + " where appl_no=?";
                param = applNo;
            }

            tmgr = new TransactionManager("getVaORVtReflectiveTapeDobj");
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, param);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                refDobj = new ReflectiveTapeDobj();
                refDobj.setRegn_no(rs.getString("regn_no"));
                if (applNo != null) {
                    refDobj.setApplNo(rs.getString("appl_no"));
                }
                refDobj.setCertificateNo(rs.getString("certificate_no"));
                refDobj.setFitmentDate(rs.getDate("fitment_date"));
                refDobj.setManuName(rs.getString("manu_name"));
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
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
        return refDobj;
    }

    public ReflectiveTapeDobj getVaReflectiveTapeDobj(String applNo, TransactionManager tmgr) throws SQLException {
        ReflectiveTapeDobj refDobj = null;
        String sql = "SELECT * FROM " + TableList.VA_REFLECTIVE_TAPE + " where appl_no =?";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, applNo);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            refDobj = new ReflectiveTapeDobj();
            refDobj.setRegn_no(rs.getString("regn_no"));
            refDobj.setApplNo(rs.getString("appl_no"));
            refDobj.setCertificateNo(rs.getString("certificate_no"));
            refDobj.setFitmentDate(rs.getDate("fitment_date"));
            refDobj.setManuName(rs.getString("manu_name"));
        }
        return refDobj;
    }

    public ReflectiveTapeDobj getVtReflectiveTapeDobj(String regn_no, String stateCd, int offCd, TransactionManager tmgr) throws SQLException {
        ReflectiveTapeDobj refDobj = null;
        String sql = "SELECT state_cd ,  off_cd ,  regn_no , certificate_no ,  fitment_date ,  manu_name "
                + " from " + TableList.VT_REFLECTIVE_TAPE + " where  regn_no=? order by op_dt desc limit 1";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, regn_no);
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            refDobj = new ReflectiveTapeDobj();
            refDobj.setStateCd(rs.getString("state_cd"));
            refDobj.setOffCd(rs.getInt("off_cd"));
            refDobj.setRegn_no(rs.getString("regn_no"));
            refDobj.setCertificateNo(rs.getString("certificate_no"));
            refDobj.setFitmentDate(rs.getDate("fitment_date"));
            refDobj.setManuName(rs.getString("manu_name"));
        }
        return refDobj;
    }

    public void insertOrUpdateVaReflectiveTape(TransactionManager tmgr, ReflectiveTapeDobj refDobj) throws Exception {

        if (refDobj == null) {
            return;
        }
        ReflectiveTapeDobj refDobjExist = getVaReflectiveTapeDobj(refDobj.getApplNo(), tmgr);
        if (refDobjExist != null) {
            insertIntoVhaReflectiveTape(tmgr, refDobj.getApplNo(), Util.getEmpCode());
            updateVaReflectiveTape(tmgr, refDobj);
        } else {
            insertVaReflectiveTape(tmgr, refDobj);
        }
    }

    public void insertIntoVhaReflectiveTape(TransactionManager tmgr, String applNo, String empCode) throws VahanException {
        String sql = null;
        try {
            int i = 1;
            sql = "INSERT INTO " + TableList.VHA_REFLECTIVE_TAPE
                    + " SELECT current_timestamp, ?, * FROM " + TableList.VA_REFLECTIVE_TAPE + " WHERE appl_no=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(i++, empCode);
            ps.setString(i++, applNo);
            ps.executeUpdate();
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + "[appNo-" + applNo + "] " + ee.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void updateVaReflectiveTape(TransactionManager tmgr, ReflectiveTapeDobj refDobj) throws VahanException {
        String sql = "UPDATE " + TableList.VA_REFLECTIVE_TAPE
                + " SET  certificate_no=?, fitment_date=?, manu_name=?,op_dt=current_timestamp  "
                + " WHERE appl_no=?";
        try {
            int i = 1;
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(i++, refDobj.getCertificateNo());
            ps.setDate(i++, new java.sql.Date(refDobj.getFitmentDate().getTime()));
            ps.setString(i++, refDobj.getManuName());
            ps.setString(i++, refDobj.getApplNo());
            ps.executeUpdate();
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + "[appNo-" + refDobj.getRegn_no() + "] " + ee.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void insertVaReflectiveTape(TransactionManager tmgr, ReflectiveTapeDobj refDobj) throws VahanException {

        PreparedStatement ps = null;
        String sql = null;
        int pos = 1;
        try {
            sql = "INSERT INTO " + TableList.VA_REFLECTIVE_TAPE
                    + " (appl_no, certificate_no, fitment_date, manu_name, regn_no,op_dt)"
                    + "    VALUES (?, ?, ?, ?, ?,current_timestamp)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(pos++, refDobj.getApplNo());
            ps.setString(pos++, refDobj.getCertificateNo());
            ps.setDate(pos++, new java.sql.Date(refDobj.getFitmentDate().getTime()));
            ps.setString(pos++, refDobj.getManuName());
            ps.setString(pos++, refDobj.getRegn_no());
            ps.executeUpdate();

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void deleteVaReflectiveTape(String applNo, TransactionManager tmgr) throws VahanException {
        String sql = "DELETE FROM " + TableList.VA_REFLECTIVE_TAPE + "  WHERE appl_no=? ";
        try {
            int i = 1;
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(i++, applNo);
            ps.executeUpdate();
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void moveFromVtReflectiveTapeToVhReflectiveTape(String regnNo, String stateCd, TransactionManager tmgr) throws VahanException {

        String sql = " Insert into " + TableList.VH_REFLECTIVE_TAPE + " SELECT current_timestamp,?,state_cd, off_cd, regn_no, certificate_no, fitment_date, manu_name,current_timestamp as op_dt\n"
                + "  FROM " + TableList.VT_REFLECTIVE_TAPE + " where state_cd=? and regn_no=? ";
        try {
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, stateCd);
            ps.setString(3, regnNo);
            ps.executeUpdate();

            sql = "Delete from " + TableList.VT_REFLECTIVE_TAPE + " where regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, stateCd);
            ps.executeUpdate();

        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }

    }

    public void moveFromVaReflectiveTapeToVtReflectiveTape(String appl_no, String stateCd, int offCd, TransactionManager tmgr) throws SQLException {
        String sql = "INSERT INTO  " + TableList.VT_REFLECTIVE_TAPE
                + " SELECT ?, ?, regn_no, certificate_no, fitment_date, manu_name,current_timestamp "
                + " FROM  " + TableList.VA_REFLECTIVE_TAPE + " WHERE appl_no=? ";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, stateCd);
        ps.setInt(2, offCd);
        ps.setString(3, appl_no);
        ps.executeUpdate();
    }

    public FitnessDobj set_FitnessHist_appl_db_to_dobj(String regn_no) throws VahanException {
        PreparedStatement ps = null;
        TransactionManagerReadOnly tmgr = null;
        FitnessDobj fitness_dobj = null;
        VahanException vahanexecption = null;
        String sql = null;
        try {
            sql = "SELECT *  FROM " + TableList.VH_FITNESS
                    + " where regn_no=? order by op_dt desc limit 1";

            tmgr = new TransactionManagerReadOnly("FitnessImpl.set_FitnessHist_appl_db_to_dobj");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) {
                fitness_dobj = new FitnessDobj();

                fitness_dobj.setAppl_no(rs.getString("appl_no"));
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
                fitness_dobj.setMovedOn(rs.getDate("moved_on"));
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
            vahanexecption = new VahanException(TableConstants.SomthingWentWrong);
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

    public String getRemarksofApplication(int offCode, String stateCode, String applNo) {
        String remarks = null;
        TransactionManager tmgr = null;
        String ChasiSQL = "select remarks from vp_appl_rcpt_mapping where off_cd=? and state_cd = ? and appl_no = ?";
        try {
            tmgr = new TransactionManager("getRemarksofApplication");
            PreparedStatement ps = tmgr.prepareStatement(ChasiSQL);
            ps.setInt(1, offCode);
            ps.setString(2, stateCode);
            ps.setString(3, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                remarks = rs.getString("remarks");
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
        return remarks;
    }

    private void moveFromVtFitenessFailDetailsToVtFitenessFailDetails(TransactionManager tmgr, FitnessDobj fitness_dobj) throws VahanException {

        String sql = "INSERT INTO vh_fitness_fail_details(state_cd, off_cd, regn_no, appl_no, failed_counter, fit_chk_dt,op_dt, moved_on, moved_by) "
                + " SELECT state_cd, off_cd, regn_no, appl_no, failed_counter, fit_chk_dt,op_dt,current_timestamp,? "
                + "  FROM vt_fitness_fail_details where regn_no=? and state_cd=? and off_cd=?";

        try {
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, fitness_dobj.getRegn_no());
            ps.setString(3, fitness_dobj.getState_cd());
            ps.setInt(4, fitness_dobj.getOff_cd());
            ps.executeUpdate();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "" + ex.getStackTrace()[0]);
            throw new VahanException("Something went wrong during movement of fitness fail details");
        }
    }
}//end of class FitnessImpl
