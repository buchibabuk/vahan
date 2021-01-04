/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.form.impl;

import nic.vahan5.reg.form.impl.permit.PermitImplementation;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import static nic.vahan.CommonUtils.FormulaUtils.fillVehicleParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.fancy.RetenRegnNo_dobj;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.EpayDobj;
import nic.vahan.form.dobj.ExArmyDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.ImportedVehicleDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.NocDobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Owner_temp_dobj;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TempRegDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.Trailer_dobj;
import nic.vahan.form.dobj.dealer.PendencyBankDobj;
import nic.vahan.form.dobj.fancy.AdvanceRegnNo_dobj;
import nic.vahan.form.impl.AuctionImpl;
import nic.vahan.form.impl.AxleImpl;
import nic.vahan.form.impl.CdImpl;
import nic.vahan.form.impl.DocumentUploadImpl;
import nic.vahan.form.impl.EpayImpl;
import nic.vahan.form.impl.ExArmyImpl;
import nic.vahan.form.impl.FasTagImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.HpaImpl;
import static nic.vahan.form.impl.HpaImpl.insertUpdateHPA;
import nic.vahan.form.impl.ImportedVehicleImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.NocImpl;
import nic.vahan.form.impl.OtherStateVehImpl;
import nic.vahan.form.impl.OwnerChoiceNoImpl;
import nic.vahan.form.impl.OwnerIdentificationImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.RetroFittingDetailsImpl;
import nic.vahan.form.impl.ScrappedVehicleImpl;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan.form.impl.SmartCardImpl;
import nic.vahan.form.impl.TaxServer_Impl;
import nic.vahan.form.impl.Trailer_Impl;
import static nic.vahan.form.impl.Trailer_Impl.insertUpdateTrailer;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.VehicleTrackingDetailsImpl;
import nic.vahan.form.impl.dealer.OfficeCorrectionImpl;
import nic.vahan.form.impl.dealer.PendencyBankDetailImpl;
import nic.vahan.form.impl.permit.PermitImpl;
import nic.vahan.form.impl.fancy.AdvanceRegnFeeImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.NewVehicleNo;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.verifyForSmartCard;
import nic.vahan.services.DmsDocCheckUtils;
import nic.vahan.services.VTDocumentModel;
import nic.vahan5.reg.commonutils.FormulaUtilities;
import nic.vahan5.reg.form.impl.dealer.PendencyBankDetailImplementation;
import nic.vahan5.reg.rest.model.dobj.fancy.AdvanceRegnNoDobjModel;
import nic.vahan5.reg.rest.model.SessionVariablesModel;
import nic.vahan5.reg.server.NewVehicleNumber;
import nic.vahan5.reg.server.ServerUtility;
import org.apache.log4j.Logger;

/**
 *
 * @author Kartikey Singh
 */
public class NewImplementation {

    private static final Logger LOGGER = Logger.getLogger(NewImplementation.class);

    /*
    @Note: Stopped making changes to this method as of 1-12-2020
     */
    public String update_New_Status(int role, Owner_dobj owner_dobj, FitnessDobj fitness_dobj, ArrayList fitCheckList,
            Status_dobj status_dobj, String changedData, HpaDobj hpa_dobj, InsDobj ins_dobj,
            Trailer_dobj trailer_dobj, ExArmyDobj exArmyDobj, ImportedVehicleDobj imp_dobj,
            AxleDetailsDobj axle_dobj, RetroFittingDetailsDobj cng_dobj, int action_cd, SessionVariables sessionVariables,
            PendencyBankDobj bankSubsidyDetail, List<ComparisonBean> ins_change_list) throws VahanException, Exception {
        TmConfigurationDobj tmConfig = Util.getTmConfiguration();
        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        ArrayList hpa_list = null;
        String regn_no = null;
        int pur_cd = status_dobj.getPur_cd();
        String appl_no = status_dobj.getAppl_no();
        String chassis_trailer;

        try {
            tmgr = new TransactionManager("update_NEW_Status");

            if (TableConstants.STATUS_HOLD.equals(status_dobj.getStatus()) && owner_dobj != null && owner_dobj.getOwner_identity() != null) {
                ServerUtil.sendSMSForHoldApplication(owner_dobj.getOwner_identity().getMobile_no(), status_dobj.getOffice_remark(), owner_dobj.getAppl_no());
            }

            if (status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_COMPLETE)) {
                if ((owner_dobj != null
                        && !owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))
                        && isRegnNoGenerateForOtherStateVehicles(tmgr, appl_no, owner_dobj.getRegn_type())) {
                    boolean isRegnGenRqrd = true;
                    if (sessionVariables.getSelectedWork().getPur_cd() == 123 && !owner_dobj.getRegn_no().trim().equalsIgnoreCase("NEW") && owner_dobj.getAdvanceRegNoDobj() == null) {
                        isRegnGenRqrd = false;
                    }
                    if (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_EXARMY)) {
                        if (owner_dobj.getAuctionDobj() != null) {
                            if (Util.getTmConfiguration() != null && Util.getTmConfiguration().isRegnNoNotAssignOthState()) {
                                isRegnGenRqrd = isRegnNoGenerateForAuctionVehicles(tmgr, appl_no);
                            } else {
                                isRegnGenRqrd = true;
                            }
                        }
                    }

                    if (isRegnGenRqrd) {
                        String payFancyApplNo = new AdvanceRegnFeeImpl().getPaymentApplicationNumber(appl_no, null);
                        if (!CommonUtils.isNullOrBlank(payFancyApplNo)) {
                            throw new VahanException("Online Payment has been initiated for FANCY Number Booking with Application Number " + payFancyApplNo);
                        }
                        if (owner_dobj.getAdvanceRegNoDobj() != null) {
                            owner_dobj.getAdvanceRegNoDobj().setRegn_appl_no(appl_no);
                            NewImplementation.updateAdvanceRegNoDetails(owner_dobj.getAdvanceRegNoDobj(), owner_dobj, tmgr);
                            //REGN_NO = owner_bean.getAdvanceRegNoDobj().getRegn_no();
                        }
                        NewVehicleNo newVehicleNo = new NewVehicleNo();
                        regn_no = newVehicleNo.generateAssignNewRegistrationNo(sessionVariables.getOffCodeSelected(), sessionVariables.getActionCodeSelected(), owner_dobj.getAppl_no(), null, 1, owner_dobj, null, tmgr);
                        if (!CommonUtils.isNullOrBlank(regn_no) && !"NEW".equals(regn_no) && TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE == status_dobj.getPur_cd()) {
                            ServerUtil.insertDealerHSRPPendencyDetails(tmgr, owner_dobj, regn_no, tmConfig);
                        }
                    }

                    if (bankSubsidyDetail != null) {
                        if (CommonUtils.isNullOrBlank(owner_dobj.getOwner_identity().getAadhar_no())) {
                            throw new VahanException("Aadhar no. can not blank for E-rickshaw.");
                        }
                        owner_dobj.getOwner_identity().setAadhar_no(null);
                        new PendencyBankDetailImpl().saveOrUpdatePendencyBankDtls(tmgr, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), bankSubsidyDetail, sessionVariables.getEmpCodeLoggedIn());
                    }
                }

                if (regn_no != null) {
                    owner_dobj.setRegn_no(regn_no);
                    if (owner_dobj.getReflectiveTapeDobj() != null) {
                        owner_dobj.getReflectiveTapeDobj().setRegn_no(regn_no);
                    }

                    if (owner_dobj.getSpeedGovernorDobj() != null) {
                        owner_dobj.getSpeedGovernorDobj().setRegn_no(regn_no);
                    }
                }

                OwnerImpl ownerImpl = new OwnerImpl();
                if (owner_dobj != null) {//validation of seating capicity based on vehicle class
                    ownerImpl.seatingCapacityValidation(owner_dobj.getSeat_cap(), owner_dobj.getVh_class(), TableConstants.VH_CLASS_ALLOWED_ZERO_SEAT_CAP);
                    // validate Insurance period
                    if (ins_dobj.getIns_type() != TableConstants.INS_TYPE_THIRD_PARTY && ins_dobj.getIns_type() != Integer.parseInt(TableConstants.INS_TYPE_NA)) {
                        ins_dobj.setIns_type(TableConstants.INS_TYPE_THIRD_PARTY);
                    }
                    ServerUtil.validateMinInsuranceValidity(ins_dobj.getInsPeriodYears(), owner_dobj.getVh_class(), owner_dobj.getRegn_type(), pur_cd, ins_dobj.getIns_type());
                }
                VehicleParameters parameters = FormulaUtils.fillVehicleParametersFromDobj(owner_dobj);
                status_dobj.setVehicleParameters(parameters);
                parameters.setAPPL_DATE(ServerUtil.setTempApplDtAsNewApplDT(status_dobj));
                parameters.setPUR_CD(pur_cd);
                parameters.setACTION_CD(action_cd);
                boolean isValidForRegistration = ServerUtil.validateVehicleNorms(owner_dobj, pur_cd, parameters, tmConfig.getTmConfigDealerDobj());
                if (!isValidForRegistration) {
                    throw new VahanException("State Transport Department has not authorized you to further process this Registration Application of '" + MasterTableFiller.masterTables.VM_VH_CLASS.getDesc(owner_dobj.getVh_class() + "") + "' with emission norms " + MasterTableFiller.masterTables.VM_NORMS.getDesc(owner_dobj.getNorms() + "") + " , Purchase Date: " + DateUtil.parseDateToString(owner_dobj.getPurchase_dt()) + " , Application Date: " + DateUtil.parseDateToString(DateUtil.parseDate(DateUtil.convertStringYYYYMMDDToDDMMYYYY(parameters.getAPPL_DATE()))) + ", please contact respective Registering Authority regarding this.");
                }
                ServerUtil.saveVaOwnerDisclaimerDetails(owner_dobj, tmgr, Util.getEmpCode(), action_cd);
                if (TableConstants.TM_NEW_RC_APPROVAL == action_cd || TableConstants.TM_ROLE_DEALER_APPROVAL == action_cd) {
                    ServerUtil.updateApplNoOfBSIVChassis(owner_dobj, tmgr, pur_cd);
                }

            }

            if (role == TableConstants.TM_ROLE_ENTRY || role == TableConstants.TM_ROLE_VERIFICATION
                    || role == TableConstants.TM_ROLE_APPROVAL
                    || action_cd == TableConstants.TM_ROLE_NEW_APPL && owner_dobj.getAppl_no() != null
                    && !owner_dobj.getAppl_no().equals(TableConstants.EMPTY_STRING)) {

                /*This check is used to restrict the Disel Vehicle Registration in Delhi Only*/
                if (!CommonUtils.isNullOrBlank(appl_no) && tmConfig.isNew_reg_loi() && owner_dobj.getNewLoiNo() != null && pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_NEW) && owner_dobj.getVh_class() == 57) {
                    PermitImpl permitImpl = new PermitImpl();
                    permitImpl.insertVhaNewLoiDetails(appl_no, owner_dobj.getNewLoiNo(), tmgr);
                    permitImpl.updateNewLoiDetails(appl_no, owner_dobj.getNewLoiNo(), tmgr);
                }

                if (action_cd == TableConstants.TM_ROLE_NEW_APPL || action_cd == TableConstants.TM_ROLE_DEALER_VERIFICATION) {
                    VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(owner_dobj);
                    String isCriteriaMatchMsg = ServerUtil.isNewRegnNotAllowed(vehParameters);
                    if (isCriteriaMatchMsg != null && !isCriteriaMatchMsg.isEmpty()) {
                        throw new VahanException(isCriteriaMatchMsg);
                    }
                }

                if ((action_cd == TableConstants.TM_NEW_RC_APPROVAL || action_cd == TableConstants.TM_ROLE_DEALER_APPROVAL) && !owner_dobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)
                        && ServerUtil.verifyForSmartCard(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), tmgr)) {
                    if (!Util.getTmConfiguration().isSmartcard_fee_at_vendor() && !SmartCardImpl.getSmartCardFeePaid(appl_no, tmgr) && !"4,5".contains(String.valueOf(owner_dobj.getOwner_cd()))) {
                        throw new VahanException("Smartcard Fee Not Paid, Please first deposit with the Balance Fee Option.");
                    }
                }

                if (role == TableConstants.TM_ROLE_ENTRY) {
                    insertOrUpdateVaOwner(tmgr, owner_dobj);
                    if (Util.getUserStateCode().equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(owner_dobj.getVh_class()) + ",")) {
                        insertOrUpdateVaOwnerOther(tmgr, owner_dobj);
                    }
                } else if ((role == TableConstants.TM_ROLE_VERIFICATION
                        || role == TableConstants.TM_ROLE_REV_NEW_APPL || role == TableConstants.TM_ROLE_APPROVAL) && !changedData.isEmpty()) {
                    insertOrUpdateVaOwner(tmgr, owner_dobj);
                    if (role != TableConstants.TM_ROLE_APPROVAL && Util.getUserStateCode().equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(owner_dobj.getVh_class()) + ",")) {
                        insertOrUpdateVaOwnerOther(tmgr, owner_dobj);
                    }
                }

                if (hpa_dobj != null) {
                    hpa_list = new ArrayList();
                    hpa_dobj.setRegn_no(owner_dobj.getRegn_no());
                    hpa_list.add(hpa_dobj);
                    if (hpa_list.size() > 1) {
                        throw new VahanException("Multiple Hypothecation not allowed.");
                    }
                    insertUpdateHPA(tmgr, hpa_list, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                } else {
                    HpaImpl.insertDeleteFromVaHpa(tmgr, appl_no);
                }

                if (!ins_change_list.isEmpty()) {
                    if (InsImpl.validateOwnerCodeWithInsType(owner_dobj.getOwner_cd(), ins_dobj.getIns_type())) {
                        InsImpl.insertUpdateInsurance(tmgr, owner_dobj.getAppl_no(), owner_dobj.getRegn_no(), ins_dobj, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                    } else {
                        throw new VahanException("Invalid Combination of Ownership Type & Insurance Type.");
                    }
                }

                // During Verification Trailer
                if (trailer_dobj != null && trailer_dobj.getChasi_no() != null && !trailer_dobj.getChasi_no().trim().isEmpty()) {
                    Trailer_Impl.validationTrailer(trailer_dobj);
                    insertUpdateTrailer(tmgr, owner_dobj.getAppl_no(), owner_dobj.getRegn_no(), owner_dobj.getChasi_no(), trailer_dobj);
                }

                if (exArmyDobj != null) {
                    ExArmyImpl.saveExArmyVehicleDetails_Impl(exArmyDobj, owner_dobj.getAppl_no(), tmgr);
                } else {
                    ExArmyImpl.insertIntoVhaExArmy(tmgr, owner_dobj.getAppl_no());
                    ExArmyImpl.deleteFromVaExArmy(tmgr, owner_dobj.getAppl_no());
                }
                if (imp_dobj != null) {
                    ImportedVehicleImpl.saveImportedDetails_Impl(imp_dobj, owner_dobj.getAppl_no(), tmgr);
                } else {
                    ImportedVehicleImpl.insertIntoVhaImpVeh(tmgr, owner_dobj.getAppl_no());
                    ImportedVehicleImpl.deleteFromVaImp(tmgr, owner_dobj.getAppl_no());
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

                if (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                        || owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                    OtherStateVehImpl impl = new OtherStateVehImpl();
                    impl.insertUpdateOtherStateVeh(tmgr, owner_dobj.getOtherStateVehDobj());

                }

                ////////////// for e-rickshaw bank subsidy update regn. no /////////////////
                if ("DL".equals(sessionVariables.getStateCodeSelected()) && pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                        && owner_dobj.getRegn_no() != null && owner_dobj.getVh_class() == TableConstants.ERICKSHAW_VCH_CLASS
                        && action_cd == TableConstants.TM_NEW_RC_APPROVAL && owner_dobj.getOwner_cd() == TableConstants.OWNER_CODE_INDIVIDUAL) {
                    PendencyBankDetailImpl.updateRegnNoForBankSubsidy(tmgr, owner_dobj.getRegn_no(), owner_dobj.getState_cd(), owner_dobj.getOff_cd(), owner_dobj.getAppl_no());
                }

                if (tmConfig != null && tmConfig.getTmConfigOtpDobj() != null && tmConfig.getTmConfigOtpDobj().isOwner_mobile_verify_with_otp() && status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_COMPLETE) && (action_cd == TableConstants.TM_NEW_RC_APPROVAL || action_cd == TableConstants.TM_ROLE_DEALER_APPROVAL)
                        && (pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE)) {
                    ServerUtil.saveUpdateOTPVerifyDetails(tmgr, owner_dobj);
                }
                if (status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_COMPLETE) && (action_cd == TableConstants.TM_NEW_RC_APPROVAL || action_cd == TableConstants.TM_ROLE_DEALER_APPROVAL)) {
                    Map<String, String> ownerChoiceRegnMap = OwnerChoiceNoImpl.getOwnerChoiceRegnNoDetails(appl_no);
                    if (ownerChoiceRegnMap != null && !ownerChoiceRegnMap.isEmpty()) {
                        new OwnerChoiceNoImpl().insertIntoHistoryChoiceNumber(tmgr, sessionVariables.getEmpCodeLoggedIn(), TableConstants.FANCY_ACCEPTED, appl_no, sessionVariables.getStateCodeSelected());
                        String rcptNo = OfficeCorrectionImpl.getRcptNoForFeePaidForPurpose(appl_no, TableConstants.VM_TRANSACTION_MAST_CHOICE_NO);
                        new OwnerChoiceNoImpl().insertIntoVtAdvanceRegn(owner_dobj, tmgr, rcptNo, Integer.parseInt(ownerChoiceRegnMap.get("choice_fees")), owner_dobj.getOwner_identity().getMobile_no());
                    }
                }
                if (owner_dobj.getAdvanceRegNoDobj() != null) {
                    owner_dobj.getAdvanceRegNoDobj().setRegn_appl_no(appl_no);
                    NewImplementation.updateAdvanceRegNoDetails(owner_dobj.getAdvanceRegNoDobj(), owner_dobj, tmgr);
                    //REGN_NO = owner_bean.getAdvanceRegNoDobj().getRegn_no();
                }
                if (owner_dobj.getRetenRegNoDobj() != null) {
                    owner_dobj.getRetenRegNoDobj().setRegn_appl_no(appl_no);
                    NewImplementation.updateRetenRegNoDetails(owner_dobj.getRetenRegNoDobj(), tmgr);
                }

                if (owner_dobj.getCdDobj() != null) {
                    CdImpl cdImpl = new CdImpl();
                    cdImpl.insertUpdateVaCd(owner_dobj.getCdDobj(), tmgr);
                }

                if (owner_dobj.getSpeedGovernorDobj() != null) {
                    FitnessImpl.insertUpdateVaSpeedGovernor(owner_dobj.getSpeedGovernorDobj(), tmgr);
                } else {
                    FitnessImpl.insertIntoVhaSpeedGovernor(appl_no, tmgr);
                    FitnessImpl.deleteVaSpeedGovernor(appl_no, tmgr);
                }

                if (owner_dobj.getReflectiveTapeDobj() != null) {
                    new FitnessImpl().insertOrUpdateVaReflectiveTape(tmgr, owner_dobj.getReflectiveTapeDobj());
                } else {
                    FitnessImpl fitImpl = new FitnessImpl();
                    fitImpl.insertIntoVhaReflectiveTape(tmgr, appl_no, Util.getEmpCode());
                    fitImpl.deleteVaReflectiveTape(appl_no, tmgr);
                }

            }

            //for inspection of non transport vehicle
            if (action_cd == TableConstants.TM_NEW_RC_FITNESS_INSPECTION
                    && owner_dobj != null && owner_dobj.getInspectionDobj() != null
                    && owner_dobj.getInspectionDobj().getInsp_dt() != null) {
                FitnessImpl fitImpl = new FitnessImpl();
                owner_dobj.getInspectionDobj().setFit_off_cd1(Integer.parseInt(sessionVariables.getEmpCodeLoggedIn()));
                fitImpl.insertOrUpdateInspection(tmgr, owner_dobj.getInspectionDobj());
            }

            //fitness inspection validation
            if (action_cd == TableConstants.TM_NEW_RC_FITNESS_INSPECTION
                    || action_cd == TableConstants.TM_NEW_RC_FITNESS_INSPECTION_APPROVAL) {

                if (fitness_dobj != null) {

                    if (fitness_dobj.getFit_valid_to() == null) {
                        throw new VahanException("Fitness Valid Upto Can not be Empty,Please Select Fitness/Inpection Test Date again for Calculation of Fitness Valid Upto.");
                    }
                    if (fitness_dobj.getFit_result() == null || fitness_dobj.getFit_result().trim().length() <= 0) {
                        throw new VahanException("Can't Proceed due to Fitness Result (Fail or Pass) is not Provided by User");
                    }
                    if (fitness_dobj.getFit_result().equalsIgnoreCase(TableConstants.FitnessResultFail)) {
                        throw new VahanException("Can't Proceed due to Fitness is Failed.You Can Use only Save Option.");
                    }
                    if (fitness_dobj.getFit_valid_to() != null && fitness_dobj.getFit_valid_to().before(new Date())) {
                        throw new VahanException("Fitness Valid Upto Can't be less than Current Date.");
                    }
                }
            }

            if (role == TableConstants.TM_ROLE_INSPECTION || TableConstants.TM_NEW_RC_FITNESS_INSPECTION_APPROVAL == action_cd) {

                if ("KL".contains(owner_dobj.getState_cd())) {
                    AdvanceRegnNo_dobj advDobj = owner_dobj.getAdvanceRegNoDobj();
                    if (advDobj == null) {
                        advDobj = getAdvanceFeeDetailsMessage(owner_dobj.getAppl_no());
                    }
                    if (advDobj != null) {
                        Date fancyRcptDate = getFancyNoRcptDate(advDobj.getRecp_no());
                        Date fancyUpto = fancyValidUpto(Util.getTmConfiguration(), fancyRcptDate);
                        String dateString = DateUtils.getCurrentDate_YYYY_MM_DD();
                        Date currentDate = JSFUtils.getStringToDateyyyyMMdd(dateString);

                        if (fancyUpto.before(currentDate)) {
                            throw new VahanException("Fancy Receipt Validatity is Expired!!!,Please Reopen Fancy Registration Number");
                        }
                    }

                }

                if (!changedData.isEmpty()) {
                    insertOrUpdateVaOwner(tmgr, owner_dobj);
                    if (Util.getUserStateCode().equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(owner_dobj.getVh_class()) + ",")) {
                        insertOrUpdateVaOwnerOther(tmgr, owner_dobj);
                    }
                }

                if (owner_dobj.getSpeedGovernorDobj() != null) {
                    FitnessImpl.insertUpdateVaSpeedGovernor(owner_dobj.getSpeedGovernorDobj(), tmgr);
                } else {
                    FitnessImpl.insertIntoVhaSpeedGovernor(appl_no, tmgr);
                    FitnessImpl.deleteVaSpeedGovernor(appl_no, tmgr);
                }

                if (owner_dobj.getReflectiveTapeDobj() != null) {
                    new FitnessImpl().insertOrUpdateVaReflectiveTape(tmgr, owner_dobj.getReflectiveTapeDobj());
                } else {
                    FitnessImpl fitImpl = new FitnessImpl();
                    fitImpl.insertIntoVhaReflectiveTape(tmgr, appl_no, Util.getEmpCode());
                    fitImpl.deleteVaReflectiveTape(appl_no, tmgr);
                }

                if (trailer_dobj != null && !CommonUtils.isNullOrBlank(trailer_dobj.getChasi_no())) {
                    Trailer_Impl.validationTrailer(trailer_dobj);
                    insertUpdateTrailer(tmgr, owner_dobj.getAppl_no(), owner_dobj.getRegn_no(), owner_dobj.getChasi_no(), trailer_dobj);
                }

                if (fitness_dobj != null) {
                    fitness_dobj.setAppl_no(owner_dobj.getAppl_no());
                    FitnessImpl.insertOrUpdateFitnessMoveFile(tmgr, owner_dobj.getRegn_no(), owner_dobj.getChasi_no(), fitness_dobj, fitCheckList, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());

                    if (hpa_dobj != null) {
                        hpa_list = new ArrayList();
                        hpa_dobj.setRegn_no(owner_dobj.getRegn_no());
                        hpa_list.add(hpa_dobj);
                        insertUpdateHPA(tmgr, hpa_list, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                    } else {
                        HpaImpl.insertDeleteFromVaHpa(tmgr, appl_no);
                    }

                    if (InsImpl.validateOwnerCodeWithInsType(owner_dobj.getOwner_cd(), ins_dobj.getIns_type())) {
                        InsImpl.insertUpdateInsurance(tmgr, owner_dobj.getAppl_no(), owner_dobj.getRegn_no(), ins_dobj, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                    } else {
                        throw new VahanException("Invalid Combination of Ownership Type & Insurance Type.");
                    }

                    if (exArmyDobj != null) {
                        ExArmyImpl.saveExArmyVehicleDetails_Impl(exArmyDobj, owner_dobj.getAppl_no(), tmgr);
                    } else {
                        ExArmyImpl.insertIntoVhaExArmy(tmgr, owner_dobj.getAppl_no());
                        ExArmyImpl.deleteFromVaExArmy(tmgr, owner_dobj.getAppl_no());
                    }
                    if (imp_dobj != null) {
                        ImportedVehicleImpl.saveImportedDetails_Impl(imp_dobj, owner_dobj.getAppl_no(), tmgr);
                    } else {
                        ImportedVehicleImpl.insertIntoVhaImpVeh(tmgr, owner_dobj.getAppl_no());
                        ImportedVehicleImpl.deleteFromVaImp(tmgr, owner_dobj.getAppl_no());
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

                }
                if (tmConfig != null && tmConfig.getRegn_gen_type().equalsIgnoreCase(TableConstants.NO_GEN_TYPE_RAND)) {

                    sql = "INSERT INTO " + TableList.VA_RANDOM_REGN_NO + " (appl_no,state_cd,off_cd) values (?,?,?)";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, owner_dobj.getAppl_no());
                    ps.setString(2, owner_dobj.getState_cd());
                    ps.setInt(3, sessionVariables.getOffCodeSelected());
                    ps.executeUpdate();
                }

                if (FitnessImpl.isFitnessFeePaid(status_dobj.getAppl_no()) && TableConstants.TM_NEW_RC_FITNESS_INSPECTION_APPROVAL == action_cd) {

                    sql = "Select * from " + TableList.VA_FC_PRINT + " where appl_no=? and state_cd=? and off_cd=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status_dobj.getAppl_no());
                    ps.setString(2, sessionVariables.getStateCodeSelected());
                    ps.setInt(3, sessionVariables.getOffCodeSelected());

                    RowSet rs = tmgr.fetchDetachedRowSet_No_release();

                    if (!rs.next()) {
                        sql = "Insert into " + TableList.VA_FC_PRINT + " ( state_cd,off_cd,appl_no, regn_no, op_dt)  VALUES (?, ?, ?,?,current_timestamp) ";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, sessionVariables.getStateCodeSelected());
                        ps.setInt(2, sessionVariables.getOffCodeSelected());
                        ps.setString(3, status_dobj.getAppl_no());
                        ps.setString(4, owner_dobj.getRegn_no());
                        ps.executeUpdate();
                    }
                }
            }

            if (owner_dobj != null && owner_dobj.getVehicleTrackingDetailsDobj() != null) {
                VehicleTrackingDetailsImpl vehicleTrackingDetailsImpl = new VehicleTrackingDetailsImpl();
                vehicleTrackingDetailsImpl.updateStatusVehicleTrackingDetails(owner_dobj.getVehicleTrackingDetailsDobj(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), tmgr);
            }
            if (role == TableConstants.TM_ROLE_APPROVAL
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                FitnessImpl fitness_Impl = new FitnessImpl();
                FitnessDobj fitDobj = fitness_Impl.set_Fitness_appl_db_to_dobj(null, owner_dobj.getAppl_no());
                if (fitDobj != null) {
                    owner_dobj.setFit_upto(fitDobj.getFit_valid_to());
                }

                FasTagImpl.updateRegnNoInFasTag(tmgr, owner_dobj.getChasi_no(), owner_dobj.getRegn_no(), owner_dobj.getState_cd(), owner_dobj.getOff_cd());

                RowSet rs = null;
                /**
                 * For Gujrat,if user has applied for fancy number,his
                 * application should not be approved un till result of auction
                 * is out
                 */
                /*sql = "Select fn_book_appl_no from fancy.fn_book_appl_no(?)  ";
                 ps = tmgr.prepareStatement(sql);
                 ps.setString(1, owner_dobj.getAppl_no());
                 RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                 if ("GJ".contains(owner_dobj.getState_cd()) && rs.next()) {
                 String fancyApplno = rs.getString("fn_book_appl_no");
                 if (fancyApplno != null && !fancyApplno.isEmpty()) {
                 throw new VahanException("Result Of Fancy Auction is still not out ");
                 }
                 }*/
                if (owner_dobj.getVehicleTrackingDetailsDobj() != null) {
                    VehicleTrackingDetailsImpl vehicleTrackingDetailsImpl = new VehicleTrackingDetailsImpl();
                    owner_dobj.getVehicleTrackingDetailsDobj().setRegn_no(owner_dobj.getRegn_no());
                    vehicleTrackingDetailsImpl.approveStatusVehTrackDetails(owner_dobj.getVehicleTrackingDetailsDobj(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), tmgr);
                }

                AdvanceRegnNo_dobj advDobj = owner_dobj.getAdvanceRegNoDobj();
                if (advDobj == null) {
                    advDobj = getAdvanceFeeDetailsMessage(owner_dobj.getAppl_no());
                }
                if ("MH".contains(owner_dobj.getState_cd())) {
                    if (advDobj != null) {
                        Date fancyRcptDate = getFancyNoRcptDate(advDobj.getRecp_no());
                        Date fancyUpto = fancyValidUpto(Util.getTmConfiguration(), fancyRcptDate);
                        String dateString = DateUtils.getCurrentDate_YYYY_MM_DD();
                        Date currentDate = JSFUtils.getStringToDateyyyyMMdd(dateString);
                        //Checking verification date for Dealer Non-Transport
                        if (status_dobj.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE && owner_dobj.getVehType() == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                            currentDate = getDealerVerificationDate(status_dobj.getAppl_no(), TableConstants.TM_NEW_RC_VERIFICATION);
                        }
                        //
                        if (fitDobj != null && fitDobj.getFit_chk_dt() != null) {
                            currentDate = fitDobj.getFit_chk_dt();
                        }
                        if (owner_dobj.getInspectionDobj() != null && owner_dobj.getInspectionDobj().getInsp_dt() != null) {
                            currentDate = owner_dobj.getInspectionDobj().getInsp_dt();
                        }

                        if (fancyUpto.before(currentDate)) {
                            throw new VahanException("Fancy Receipt Validatity is Expired!!!,Please Reopen Fancy Registration Number");
                        }
                    }

                }

                if (advDobj != null) {
                    validateFancyVehicleCategory(advDobj, owner_dobj.getVch_catg(), tmgr);
                }

                int reg_upto = getNewRegUpto(owner_dobj);
                Date regUpto = null;
                /*
                 * For other state/district vehciles fit upto(if No inspection is done),regn upto and fit upto will
                 * be details from previous state or rto
                 */
                if (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                        || owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                    deleteFromVtTables(owner_dobj.getRegn_no(), owner_dobj.getAppl_no());
                    if (!isRegnNoGenerateForOtherStateVehicles(tmgr, appl_no, owner_dobj.getRegn_type())
                            && owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)) {
                        new NewVehicleNo().asignAndUpdateNewVehicleNo(owner_dobj.getOtherStateVehDobj().getOldRegnNo(), appl_no, owner_dobj.getState_cd(), owner_dobj.getOff_cd(), TableConstants.EMPTY_STRING, TableConstants.EMPTY_STRING, tmgr);
                        status_dobj.setRegn_no(owner_dobj.getOtherStateVehDobj().getOldRegnNo());
                        owner_dobj.setRegn_no(owner_dobj.getOtherStateVehDobj().getOldRegnNo());
                    } else if (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                        status_dobj.setRegn_no(owner_dobj.getOtherStateVehDobj().getOldRegnNo());
                    } else {
                        status_dobj.setRegn_no(owner_dobj.getRegn_no());
                    }

                } else if (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_SCRAPPED)) {
                    if (reg_upto > 0) {
                        regUpto = DateUtils.addToDate(new Date(), 3, reg_upto);
                        regUpto = DateUtils.addToDate(regUpto, 1, -1);
                        owner_dobj.setRegn_upto(regUpto);
                    }

                    if (fitDobj == null) {
                        int fit_upto = FitnessImpl.getNewFitnessUpto(owner_dobj);
                        if (fit_upto > 0) {
                            Date fitUpto = DateUtils.addToDate(new Date(), 3, fit_upto);
                            fitUpto = DateUtils.addToDate(fitUpto, 1, -1);
                            owner_dobj.setFit_upto(fitUpto);
                        }
                    }
                    status_dobj.setRegn_no(owner_dobj.getScrappedVehicleDobj().getOld_regn_no());
                } else if (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_CD)) {

                    if (reg_upto > 0) {
                        regUpto = DateUtils.addToDate(owner_dobj.getPurchase_dt(), 3, reg_upto);
                        regUpto = DateUtils.addToDate(regUpto, 1, -1);
                        owner_dobj.setRegn_upto(regUpto);
                    }

                    if (fitDobj == null) {
                        int fit_upto = FitnessImpl.getNewFitnessUpto(owner_dobj);
                        if (fit_upto > 0) {
                            Date fitUpto = DateUtils.addToDate(owner_dobj.getPurchase_dt(), 3, fit_upto);
                            fitUpto = DateUtils.addToDate(fitUpto, 1, -1);
                            owner_dobj.setFit_upto(fitUpto);
                        }
                    }
                    status_dobj.setRegn_no(owner_dobj.getRegn_no());

                } else {

                    if (reg_upto > 0) {
                        //regUpto = DateUtils.addToDate(new Date(), 3, reg_upto);
                        regUpto = DateUtils.addToDate(new OwnerImpl().getVaOwnerRegistrationDate(appl_no, tmgr), 3, reg_upto);
                        //new OwnerImpl().getVaOwnerRegistrationDate(appl_no, tmgr);
                        regUpto = DateUtils.addToDate(regUpto, 1, -1);
                        owner_dobj.setRegn_upto(regUpto);
                    }

                    if (fitDobj == null) {
                        int fit_upto = FitnessImpl.getNewFitnessUpto(owner_dobj);
                        if (fit_upto > 0) {
                            Date fitUpto = DateUtils.addToDate(new OwnerImpl().getVaOwnerRegistrationDate(appl_no, tmgr), 3, fit_upto);
                            fitUpto = DateUtils.addToDate(fitUpto, 1, -1);
                            owner_dobj.setFit_upto(fitUpto);
                        }
                    }
                    status_dobj.setRegn_no(owner_dobj.getRegn_no());
                }

                //for inspection of non-transport vehicle
                if (owner_dobj.getInspectionDobj() != null
                        && owner_dobj.getInspectionDobj().getInsp_dt() != null) {
                    FitnessImpl fitImpl = new FitnessImpl();
                    owner_dobj.getInspectionDobj().setRegn_no(owner_dobj.getRegn_no());
                    fitImpl.insertOrUpdateInspection(tmgr, owner_dobj.getInspectionDobj());
                    fitImpl.insertVtInspection(tmgr, owner_dobj.getInspectionDobj());
                    ServerUtil.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_INSPECTION);
                }

                if (status_dobj.getRegn_no() == null || status_dobj.getRegn_no().equalsIgnoreCase(TableConstants.EMPTY_STRING) || status_dobj.getRegn_no().equalsIgnoreCase("NEW")) {
                    throw new VahanException("Registration No Generation Failed!!!");
                }

                if (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_EXARMY)) {
                    if (owner_dobj.getAuctionDobj() != null) {
                        sql = "Select status from " + TableList.VT_OWNER + " WHERE regn_no=? and state_cd  = ? and off_cd =? "
                                + " and status = '" + TableConstants.VT_AUCTION_STATUS + "'";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, owner_dobj.getAuctionDobj().getRegnNo());
                        ps.setString(2, owner_dobj.getAuctionDobj().getStateCdFrom());
                        ps.setInt(3, owner_dobj.getAuctionDobj().getOffCdFrom());
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {
                            insertIntoVhOwner(tmgr, status_dobj, owner_dobj.getAuctionDobj().getStateCdFrom(), owner_dobj.getAuctionDobj().getOffCdFrom(), owner_dobj.getAuctionDobj().getRegnNo());
                            insertIntoVtOwnerNOC(tmgr, status_dobj, owner_dobj.getAuctionDobj().getStateCdFrom(), owner_dobj.getAuctionDobj().getOffCdFrom(), owner_dobj.getAuctionDobj().getRegnNo());
                            deleteFromVtOwner(tmgr, owner_dobj.getAuctionDobj().getStateCdFrom(), owner_dobj.getAuctionDobj().getOffCdFrom(), owner_dobj.getAuctionDobj().getRegnNo());
                            insertIntoVehReassign(tmgr, status_dobj.getRegn_no(), owner_dobj.getAuctionDobj().getRegnNo(), status_dobj.getAppl_no(), TableConstants.AUCTION_REMARK);
                            new AuctionImpl().insertIntoVhAuction(tmgr, status_dobj, owner_dobj.getAuctionDobj().getRegnNo(), owner_dobj.getChasi_no());
                            ServerUtil.deleteFromTable(tmgr, owner_dobj.getAuctionDobj().getRegnNo(), null, TableList.VT_AUCTION);
                            deleteFromVtTablesForAuction(tmgr, owner_dobj.getAuctionDobj().getRegnNo(), owner_dobj.getAppl_no(), owner_dobj.getAuctionDobj().getStateCdFrom(), owner_dobj.getAuctionDobj().getOffCdFrom());
                        }
                    }
                }
                /**
                 * A check is placed to avoid assignment of Fancy Numbers that
                 * are same as any old registration number.
                 */
                sql = "Select status from " + TableList.VT_OWNER + " WHERE regn_no=? and state_cd  = ? and off_cd =? "
                        + " and status='N'";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, status_dobj.getRegn_no());
                ps.setString(2, sessionVariables.getStateCodeSelected());
                ps.setInt(3, sessionVariables.getOffCodeSelected());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    throw new VahanException("Vehicle Details already available in VAHAN &"
                            + " NOC is Yet Not issued for this Vehicle!!!");
                }

                String oldRegno = owner_dobj.getOtherStateVehDobj() != null ? owner_dobj.getOtherStateVehDobj().getOldRegnNo() : null;
                NocDobj noc = null;
                if (oldRegno != null) {

                    NocImpl nocImpl = new NocImpl();
                    noc = nocImpl.set_NOC_appl_db_to_dobj(null, oldRegno);
                    if (noc != null) {
                        sql = "Select a.regn_no,b.noc_no from VT_OWNER a \n"
                                + " left join " + TableList.VT_NOC_VERIFICATION + " b on b.regn_no=a.regn_no or b.chasi_no=a.chasi_no\n"
                                + " WHERE a.regn_no = ? and a.state_cd  = ? and a.off_cd = ? and a.status = 'N'  ";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, oldRegno);
                        ps.setString(2, noc.getState_cd());
                        ps.setInt(3, noc.getOff_cd());
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {
                            if (rs.getString("noc_no") == null) {
                                if ((!noc.getState_to().equals(sessionVariables.getStateCodeSelected())
                                        || (noc.getOff_to() != 0 && noc.getOff_to() != sessionVariables.getOffCodeSelected()))) {
                                    throw new VahanException("NOC is Not Issued for this Vehicle to this State or Office!!!");
                                }
                            }
                            if (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                                    || owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                                insertIntoVtOwnerNOC(tmgr, status_dobj, noc.getState_cd(), noc.getOff_cd(), oldRegno);
                            }
                            insertIntoVhOwner(tmgr, status_dobj, noc.getState_cd(), noc.getOff_cd(), oldRegno);
                            deleteFromVtOwner(tmgr, noc.getState_cd(), noc.getOff_cd(), oldRegno);
                        }
                    }
                }

                sql = "Select * from " + TableList.VT_OWNER
                        + " WHERE (regn_no,state_cd,off_cd) in "
                        + " (select regn_no,state_cd,off_cd from " + TableList.VA_OWNER + " where appl_no=? and regn_no != 'NEW')";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, status_dobj.getAppl_no());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    throw new VahanException("Vehicle " + status_dobj.getRegn_no() + " details already available in VAHAN.");
                }

                if ("KA".contains(owner_dobj.getState_cd()) && (!"O,A,R".contains(owner_dobj.getRegn_type()) || (owner_dobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_EXARMY) && owner_dobj.getAuctionDobj() == null))) {
                    sql = "Update va_owner set regn_dt=? where appl_no=? ";
                    if (fitDobj != null) {
                        ps = tmgr.prepareStatement(sql);
                        ps.setDate(1, new java.sql.Date(fitDobj.getFit_chk_dt().getTime()));
                        ps.setString(2, status_dobj.getAppl_no());
                        ps.executeUpdate();
                        if (reg_upto > 0) {
                            regUpto = DateUtils.addToDate(new java.sql.Date(fitDobj.getFit_chk_dt().getTime()), 3, reg_upto);
                            regUpto = DateUtils.addToDate(regUpto, 1, -1);
                            owner_dobj.setRegn_upto(regUpto);
                        }
                    } else if (owner_dobj.getInspectionDobj() != null && owner_dobj.getInspectionDobj().getInsp_dt() != null) {
                        ps = tmgr.prepareStatement(sql);
                        ps.setDate(1, new java.sql.Date(owner_dobj.getInspectionDobj().getInsp_dt().getTime()));
                        ps.setString(2, status_dobj.getAppl_no());
                        ps.executeUpdate();
                        if (reg_upto > 0) {
                            regUpto = DateUtils.addToDate(new java.sql.Date(owner_dobj.getInspectionDobj().getInsp_dt().getTime()), 3, reg_upto);
                            regUpto = DateUtils.addToDate(regUpto, 1, -1);
                            owner_dobj.setRegn_upto(regUpto);
                        }
                        if (fitDobj == null) {
                            int fit_upto = FitnessImpl.getNewFitnessUpto(owner_dobj);
                            if (fit_upto > 0) {
                                Date fitUpto = DateUtils.addToDate(owner_dobj.getInspectionDobj().getInsp_dt(), 3, fit_upto);
                                fitUpto = DateUtils.addToDate(fitUpto, 1, -1);
                                owner_dobj.setFit_upto(fitUpto);
                            }
                        }
                    }
                }

                if (owner_dobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_EXARMY) && owner_dobj.getAuctionDobj() != null) {
                    if (fitDobj != null) {
                        if (reg_upto > 0) {
                            regUpto = DateUtils.addToDate(new java.sql.Date(fitDobj.getFit_chk_dt().getTime()), 3, reg_upto);
                            regUpto = DateUtils.addToDate(regUpto, 1, -1);
                            owner_dobj.setRegn_upto(regUpto);
                        }
                    } else if (owner_dobj.getInspectionDobj() != null && owner_dobj.getInspectionDobj().getInsp_dt() != null) {
                        if (reg_upto > 0) {
                            regUpto = DateUtils.addToDate(new java.sql.Date(owner_dobj.getInspectionDobj().getInsp_dt().getTime()), 3, reg_upto);
                            regUpto = DateUtils.addToDate(regUpto, 1, -1);
                            owner_dobj.setRegn_upto(regUpto);
                        }
                        if (fitDobj == null) {
                            int fit_upto = FitnessImpl.getNewFitnessUpto(owner_dobj);
                            if (fit_upto > 0) {
                                Date fitUpto = DateUtils.addToDate(owner_dobj.getInspectionDobj().getInsp_dt(), 3, fit_upto);
                                fitUpto = DateUtils.addToDate(fitUpto, 1, -1);
                                owner_dobj.setFit_upto(fitUpto);
                            }
                        }
                    }
                }

                sql = "INSERT into  " + TableList.VT_OWNER
                        + " Select state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name, "
                        + "    f_name, c_add1, c_add2, c_add3, c_district, c_pincode, c_state, "
                        + "    p_add1, p_add2, p_add3, p_district, p_pincode, p_state, owner_cd, "
                        + "    regn_type, vh_class, chasi_no, eng_no, maker, maker_model, body_type,"
                        + "    no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt, "
                        + "    gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, cubic_cap, "
                        + "    floor_area, ac_fitted, audio_fitted, video_fitted, vch_purchase_as, "
                        + "    vch_catg, dealer_cd, sale_amt, laser_code, garage_add, length, "
                        + "    width, height, ?"
                        + ", ? as fit_upto, annual_income, imported_vch, "
                        + "       other_criteria,'Y' as status"
                        + ",current_timestamp as op_dt"
                        + "  FROM " + TableList.VA_OWNER
                        + " where appl_no=? and regn_no != 'NEW'";

                ps = tmgr.prepareStatement(sql);
                ps.setDate(1, new java.sql.Date(owner_dobj.getRegn_upto().getTime()));
                ps.setDate(2, new java.sql.Date(owner_dobj.getFit_upto().getTime()));
                ps.setString(3, owner_dobj.getAppl_no());
                ServerUtil.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                if (Util.getUserStateCode().equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(owner_dobj.getVh_class()) + ",")) {
                    ownerOtherApproval(tmgr, owner_dobj);
                }
                if (owner_dobj.getPmt_type() > 0 || owner_dobj.getPmt_catg() > 0 || (owner_dobj.getServicesType() != null && !owner_dobj.getServicesType().equals("") && Integer.parseInt(owner_dobj.getServicesType()) > 0)) {
                    if (changedData.isEmpty()) {
                        ServerUtil.insertIntoVhaTable(tmgr, owner_dobj.getAppl_no(), Util.getEmpCode(), "vha_permit_new_regn", "va_permit_new_regn");
                    }
                    ServerUtil.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), "va_permit_new_regn");
                }

                if (owner_dobj.getLaser_code() != null && !owner_dobj.getLaser_code().equals(TableConstants.EMPTY_STRING) && owner_dobj.getLaser_code().equals(TableConstants.HOMOLOGATION_DATA)) {
                    this.insertintoVhaHomologationDetails(tmgr, status_dobj.getAppl_no());
                    ServerUtil.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_HOMO_DETAILS);
                }

                if (owner_dobj.getCdDobj() != null) {
                    CdImpl cdImpl = new CdImpl();
                    owner_dobj.getCdDobj().setRegNo(status_dobj.getRegn_no());
                    cdImpl.insertIntoVtCd(owner_dobj.getCdDobj(), tmgr);
                    ServerUtil.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_CD_REGN_DTL);
                }

                if (owner_dobj.getLinkedRegnNo() != null) {
                    sideTrailerApproval(tmgr, owner_dobj);
                }
                FitnessImpl fitImpl = new FitnessImpl();
                if (noc != null) {
                    if (fitDobj != null) {
                        FitnessImpl.moveFromVtFitnessToVhFitness(status_dobj.getAppl_no(), oldRegno, noc.getState_cd(), noc.getOff_cd(), tmgr);
                    }
                    FitnessImpl.moveFromVtSpeedGovToVhSpeedGovTo(appl_no, oldRegno, noc.getState_cd(), noc.getOff_cd(), tmgr);
                    fitImpl.moveFromVtReflectiveTapeToVhReflectiveTape(oldRegno, noc.getState_cd(), tmgr);
                }
                FitnessImpl.moveFromVtFitnessToVhFitness(owner_dobj.getAppl_no(), owner_dobj.getRegn_no(), owner_dobj.getState_cd(), owner_dobj.getOff_cd(), tmgr);
                FitnessImpl.moveFromVaFitnessToVTFitness(tmgr, owner_dobj.getAppl_no());

                FitnessImpl.moveFromVtSpeedGovToVhSpeedGovTo(appl_no, owner_dobj.getRegn_no(), owner_dobj.getState_cd(), owner_dobj.getOff_cd(), tmgr);
                FitnessImpl.moveFromVaSpeedGovToVtSpeedGov(appl_no, tmgr);

                fitImpl.moveFromVtReflectiveTapeToVhReflectiveTape(owner_dobj.getRegn_no(), owner_dobj.getState_cd(), tmgr);
                fitImpl.moveFromVaReflectiveTapeToVtReflectiveTape(appl_no, owner_dobj.getState_cd(), owner_dobj.getOff_cd(), tmgr);

                ServerUtil.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_FITNESS);
                ServerUtil.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_SPEED_GOVERNOR);
                ServerUtil.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_REFLECTIVE_TAPE);
                insertIntoVhaOwnerWithTimeInterval(tmgr, appl_no);
                ServerUtil.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_OWNER);
                ServerUtil.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_SIDE_TRAILER);

                if (noc != null) {
                    OwnerIdentificationImpl.insertIntoOwnerIdentificationHistoryVH(tmgr, oldRegno, noc.getState_cd(), noc.getOff_cd());
                    OwnerIdentificationImpl.deleteFromVtOwnerIdentification(tmgr, oldRegno, noc.getState_cd(), noc.getOff_cd());
                } else {
                    OwnerIdentificationImpl.insertIntoOwnerIdentificationHistoryVH(tmgr, status_dobj.getRegn_no(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                    OwnerIdentificationImpl.deleteFromVtOwnerIdentification(tmgr, status_dobj.getRegn_no(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                }
                OwnerIdentificationImpl.insertIntoVtFromVaOwnerIdentification(tmgr, status_dobj.getAppl_no());
                ServerUtil.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_OWNER_IDENTIFICATION);

                // Nitin Kumar: For New Permit Loi in Appprovl insert data va_new_reg_loi to vt_new_reg_loi table 
                if (!CommonUtils.isNullOrBlank(appl_no) && tmConfig.isNew_reg_loi() && owner_dobj.getNewLoiNo() != null && pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_NEW) && owner_dobj.getVh_class() == 57) {
                    PermitImpl permitImpl = new PermitImpl();
                    permitImpl.insertVtNewLoiDetails(owner_dobj.getRegn_no(), owner_dobj.getNewLoiNo(), tmgr);
                    ServerUtil.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_NEW_REGN_LOI);

                }

                //for updating the status of application when it is approved start
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtil.updateApprovedStatus(tmgr, status_dobj);
                //for updating the status of application when it is approved end

                if (FitnessImpl.isFitnessFeePaid(status_dobj.getAppl_no())) {
                    sql = "Select * from " + TableList.VA_FC_PRINT + " where appl_no=? and state_cd=? and off_cd=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status_dobj.getAppl_no());
                    ps.setString(2, sessionVariables.getStateCodeSelected());
                    ps.setInt(3, sessionVariables.getOffCodeSelected());
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        sql = "UPDATE " + TableList.VA_FC_PRINT + " SET regn_no=? Where appl_no=? and state_cd=? and off_cd=?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, owner_dobj.getRegn_no());
                        ps.setString(2, status_dobj.getAppl_no());
                        ps.setString(3, sessionVariables.getStateCodeSelected());
                        ps.setInt(4, sessionVariables.getOffCodeSelected());
                        ps.executeUpdate();
                    } else {
                        sql = "Insert into " + TableList.VA_FC_PRINT + " ( state_cd,off_cd,appl_no, regn_no, op_dt)  VALUES (?, ?,?,?,current_timestamp) ";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, sessionVariables.getStateCodeSelected());
                        ps.setInt(2, sessionVariables.getOffCodeSelected());
                        ps.setString(3, status_dobj.getAppl_no());
                        ps.setString(4, status_dobj.getRegn_no());
                        ps.executeUpdate();
                    }

                }

                if (hpa_list != null) { // for Single Hpa Entry

                    if (noc != null) {
                        HpaImpl.insertIntoHypthVH(tmgr, status_dobj, noc.getState_cd(), noc.getOff_cd(), oldRegno);
                        HpaImpl.deleteFromVtHypth(tmgr, oldRegno, noc.getState_cd(), noc.getOff_cd());
                    } else {
                        HpaImpl.insertIntoHypthVH(tmgr, status_dobj, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), oldRegno);
                        HpaImpl.deleteFromVtHypth(tmgr, status_dobj.getRegn_no(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                    }
                    HpaImpl.insertIntoVtHypthFromVaHpa(tmgr, status_dobj);
                    ServerUtil.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_HPA);
                }

                //Delete from vt_insurance
                if (noc != null) {
                    sql = "Insert into " + TableList.VH_INSURANCE + " Select regn_no, comp_cd, ins_type, ins_from, ins_upto,  "
                            + "       policy_no,current_timestamp,?,state_cd, off_cd,idv from " + TableList.VT_INSURANCE
                            + " where regn_no=? and state_cd= ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, sessionVariables.getEmpCodeLoggedIn());
                    ps.setString(2, noc.getRegn_no());
                    ps.setString(3, noc.getState_cd());
                    ps.executeUpdate();

                    sql = "Delete FROM  " + TableList.VT_INSURANCE
                            + " where regn_no=? and state_cd = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, oldRegno);
                    ps.setString(2, noc.getState_cd());
                    ps.executeUpdate();
                } else {

                    sql = "Insert into " + TableList.VH_INSURANCE + " Select regn_no, comp_cd, ins_type, ins_from, ins_upto,  "
                            + "       policy_no,current_timestamp,?,state_cd, off_cd,idv from " + TableList.VT_INSURANCE
                            + " where regn_no=? and state_cd= ?";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, sessionVariables.getEmpCodeLoggedIn());
                    ps.setString(2, status_dobj.getRegn_no());
                    ps.setString(3, sessionVariables.getStateCodeSelected());
                    ps.executeUpdate();

                    sql = "Delete FROM  " + TableList.VT_INSURANCE
                            + " where regn_no=? and state_cd = ?";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status_dobj.getRegn_no());
                    ps.setString(2, sessionVariables.getStateCodeSelected());
                    ps.executeUpdate();
                }

                //Inserting into vt_insurance from va_insurance
                sql = "INSERT into " + TableList.VT_INSURANCE + " "
                        + "Select ?,? ,regn_no ,  comp_cd ,  ins_type ,  ins_from ,"
                        + " ins_upto ,  policy_no, idv, current_timestamp as op_dt "
                        + " FROM " + TableList.VA_INSURANCE + " where appl_no=?";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, sessionVariables.getStateCodeSelected());
                ps.setInt(2, sessionVariables.getOffCodeSelected());
                ps.setString(3, owner_dobj.getAppl_no());
                ps.executeUpdate();

                InsImpl.insertIntoInsuranceHistoryWithInterval(tmgr, null, owner_dobj.getAppl_no(), sessionVariables.getEmpCodeLoggedIn());
                InsImpl.deleteFromVaInsurance(tmgr, null, owner_dobj.getAppl_no());

                //Update va_tcc_print for printing TCC 
                sql = "UPDATE " + TableList.VA_TCC_PRINT + " SET"
                        + " regn_no = ? "
                        + " WHERE appl_no = ? and "
                        + " state_cd = ? and off_cd = ?";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, status_dobj.getRegn_no());
                ps.setString(2, owner_dobj.getAppl_no());
                ps.setString(3, sessionVariables.getStateCodeSelected());
                ps.setInt(4, sessionVariables.getOffCodeSelected());
                ps.executeUpdate();

                // During Attached Trailer data Approval
                if (trailer_dobj != null && !trailer_dobj.getChasi_no().isEmpty()) {
                    chassis_trailer = trailer_dobj.getChasi_no().trim();
                    Trailer_Impl.validationTrailer(trailer_dobj);
                    Trailer_dobj newdobj = new Trailer_dobj();
                    newdobj = Trailer_Impl.checkTrailerChassis_VTOwnerTrailer(chassis_trailer);
                    if (newdobj != null) {
                        throw new VahanException("Duplicate Trailer Chassis No." + newdobj.getDup_chassis() + " against the registration no " + newdobj.getRegn_no() + " State " + ServerUtil.getStateNameByStateCode(newdobj.getState_cd()) + " and Office " + ServerUtil.getOfficeName(newdobj.getOff_cd(), newdobj.getState_cd()));
                    } else {
                        Trailer_Impl.movedataapprovalTrailer(tmgr, status_dobj.getRegn_no(), owner_dobj.getAppl_no(), sessionVariables.getOffCodeSelected());
                        Trailer_Impl.deleteFromVaTrailer(tmgr, null, owner_dobj.getAppl_no());
                    }
                }

                // ExArmy Details
                if (exArmyDobj != null) {
                    if (noc != null) {
                        ExArmyImpl.insertIntoOwnerExArmyVH(tmgr, status_dobj, noc.getState_cd(), noc.getOff_cd(), oldRegno);
                        ExArmyImpl.deleteFromVtOwnerExArmy(tmgr, oldRegno, noc.getState_cd(), noc.getOff_cd());
                    } else {
                        ExArmyImpl.insertIntoOwnerExArmyVH(tmgr, status_dobj, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), oldRegno);
                        ExArmyImpl.deleteFromVtOwnerExArmy(tmgr, status_dobj.getRegn_no(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                    }
                    ExArmyImpl.insertIntoVtFromVaOwnerExArmy(tmgr, status_dobj);
                    ServerUtil.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_OWNER_EX_ARMY);
                } else {
                    ExArmyImpl.insertIntoVhaExArmy(tmgr, status_dobj.getAppl_no());
                    ExArmyImpl.deleteFromVaExArmy(tmgr, status_dobj.getAppl_no());
                }

                // Imported Vehicle Details
                if (imp_dobj != null) {
                    if (noc != null) {
                        ImportedVehicleImpl.insertIntoImportedVehVH(tmgr, status_dobj, noc.getState_cd(), noc.getOff_cd(), oldRegno);
                        ImportedVehicleImpl.deleteFromVtImportedVeh(tmgr, oldRegno, noc.getState_cd(), noc.getOff_cd());
                    } else {
                        ImportedVehicleImpl.insertIntoImportedVehVH(tmgr, status_dobj, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), oldRegno);
                        ImportedVehicleImpl.deleteFromVtImportedVeh(tmgr, status_dobj.getRegn_no(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                    }
                    ImportedVehicleImpl.insertIntoVtFromVaImportedVeh(tmgr, status_dobj);
                    ServerUtil.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_IMPORT_VEH);
                } else {
                    ImportedVehicleImpl.insertIntoVhaImpVeh(tmgr, status_dobj.getAppl_no());
                    ImportedVehicleImpl.deleteFromVaImp(tmgr, status_dobj.getAppl_no());
                }

                //Axle Details
                if (axle_dobj != null) {
                    if (noc != null) {
                        AxleImpl.insertIntoAxleVH(tmgr, status_dobj, noc.getState_cd(), noc.getOff_cd(), oldRegno);
                        AxleImpl.deleteFromVtAxle(tmgr, oldRegno, noc.getState_cd(), noc.getOff_cd());
                    } else {
                        AxleImpl.insertIntoAxleVH(tmgr, status_dobj, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), oldRegno);
                        AxleImpl.deleteFromVtAxle(tmgr, status_dobj.getRegn_no(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                    }
                    AxleImpl.insertIntoVtFromVaAxle(tmgr, status_dobj);
                    ServerUtil.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_AXLE);
                } else {
                    AxleImpl.insertIntoVhaAxle(tmgr, status_dobj.getAppl_no());
                    AxleImpl.deleteFromVaAxle(tmgr, status_dobj.getAppl_no());
                }

                //CNG Vehicle Details
                if (cng_dobj != null) {
                    if (noc != null) {
                        RetroFittingDetailsImpl.insertIntoRetroFitVH(tmgr, status_dobj, noc.getState_cd(), noc.getOff_cd(), oldRegno);
                        RetroFittingDetailsImpl.deleteFromVtRetroFit(tmgr, oldRegno, noc.getState_cd(), noc.getOff_cd());
                    } else {
                        RetroFittingDetailsImpl.insertIntoRetroFitVH(tmgr, status_dobj, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), oldRegno);
                        RetroFittingDetailsImpl.deleteFromVtRetroFit(tmgr, status_dobj.getRegn_no(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                    }
                    RetroFittingDetailsImpl.insertIntoVtFromVaRetroFit(tmgr, status_dobj);
                    ServerUtil.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_RETROFITTING_DTLS);
                } else {
                    RetroFittingDetailsImpl.insertIntoVhaCng(tmgr, status_dobj.getAppl_no());
                    RetroFittingDetailsImpl.deleteFromVaRetroFittingDetails(tmgr, status_dobj.getAppl_no());
                }

                //*****************************PUCC Manupulatio Start****************************************
                sql = "INSERT INTO " + TableList.VH_PUCC
                        + " SELECT state_cd, off_cd, regn_no, pucc_from, pucc_upto, pucc_centreno,"
                        + "       pucc_no, op_dt, ? as appl_no, current_timestamp as moved_on, ? as moved_by "
                        + "  FROM " + TableList.VT_PUCC + " WHERE regn_no=? and state_cd = ? and off_cd= ?";

                ps = tmgr.prepareStatement(sql);

                ps.setString(1, status_dobj.getAppl_no());
                ps.setString(2, String.valueOf(status_dobj.getEmp_cd()));
                if (noc != null) {
                    ps.setString(3, oldRegno);
                    ps.setString(4, noc.getState_cd());
                    ps.setInt(5, noc.getOff_cd());
                } else {
                    ps.setString(3, status_dobj.getRegn_no());
                    ps.setString(4, status_dobj.getState_cd());
                    ps.setInt(5, status_dobj.getOff_cd());
                }
                ps.executeUpdate();

                sql = "DELETE FROM " + TableList.VT_PUCC + " where regn_no = ? and state_cd = ? and off_cd = ?";
                ps = tmgr.prepareStatement(sql);

                if (noc != null) {
                    ps.setString(1, oldRegno);
                    ps.setString(2, noc.getState_cd());
                    ps.setInt(3, noc.getOff_cd());
                } else {
                    ps.setString(1, status_dobj.getRegn_no());
                    ps.setString(2, sessionVariables.getStateCodeSelected());
                    ps.setInt(3, sessionVariables.getOffCodeSelected());
                }
                ps.executeUpdate();

                sql = "INSERT INTO " + TableList.VT_PUCC
                        + " SELECT ? as state_cd,? as off_cd,regn_no, pucc_from, pucc_upto, pucc_centreno, pucc_no, op_dt "
                        + "  FROM " + TableList.VA_PUCC + " WHERE appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, sessionVariables.getStateCodeSelected());
                ps.setInt(2, sessionVariables.getOffCodeSelected());
                ps.setString(3, status_dobj.getAppl_no());
                ps.executeUpdate();

                ServerUtil.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_PUCC);

                //*****************************PUCC Manupulatio End****************************************
                if (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                        || owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                    OtherStateVehImpl impl = new OtherStateVehImpl();
                    owner_dobj.getOtherStateVehDobj().setNewRegnNo(status_dobj.getRegn_no());
                    if (noc != null) {
                        impl.insertIntoOtherStateVehHistoryVH(tmgr, oldRegno, appl_no, noc.getState_cd(), noc.getOff_cd());
                        impl.deleteFromVtOtherStateVeh(tmgr, oldRegno, noc.getState_cd(), noc.getOff_cd());
                    }

                    /*
                     *Petrol Vehicle registered at other state on 1st Jan 2013
                     *OTT for 15 years paid at other state and  tax validity is 31st Dec 2027. Entry date  in Manipur : 14-Nov-2017
                     *Vehicle is more than 2 years old. Hence no tax will be charged till 31st Dec 2027 Hence, in Manipur
                     *Road Tax Amount = 0, Tax validity = 31st Dec 2027.
                     * 
                     * Diesel Vehicle registered at other state on 1st Jan 2013
                     * OTT for 10 years paid at other state and  tax validity is 31st Dec 2022. Entry date  in Manipur : 14-Nov-2017
                     * Vehicle is more than 2 years old. Hence no tax will be charged till 31st Dec 2022
                     * Hence, in Manipur Road Tax Amount = 0 Tax validity = 31st Dec 2022.
                     */
                    if ("MN".equalsIgnoreCase(Util.getUserStateCode())) {
                        List<EpayDobj> listEpay = EpayImpl.getFeePaidDetails(appl_no).getList();
                        EpayDobj ep = new EpayDobj();
                        ep.setPurCd(1);
                        int index = listEpay.lastIndexOf(ep);
                        if (index >= 0) {
                            ep = listEpay.get(index);
                            int vehicleAge = DateUtils.getDate1MinusDate2_Months(owner_dobj.getPurchase_dt(), ep.getRcptDt());
                            if (vehicleAge >= 24) {
                                impl.insertIntoOtherStateVehTaxHistoryVH(tmgr, appl_no, regn_no, owner_dobj.getRegn_upto(), owner_dobj.getOtherStateVehDobj());
                            }
                        }
                    }

                    impl.insertIntoVtOtherStateVeh(tmgr, owner_dobj.getOtherStateVehDobj());
                    ServerUtil.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_OTHER_STATE_VEH);
                }

                //SmartCard Or Print
                boolean blnSmart = true;

                if (owner_dobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                    boolean isSmartCard = verifyForSmartCard(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), tmgr);
                    if (isSmartCard) {
                        blnSmart = (SmartCardImpl.getSmartCardFeePaid(appl_no, tmgr));

                    } else {
                        sql = "SELECT fees from get_appl_rcpt_details(?) where pur_cd in (1,4,5)";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, status_dobj.getAppl_no());
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        if (!rs.next()) {
                            blnSmart = false;
                        }
                    }
                }

                if (blnSmart) {
                    //HSRP

                    boolean isOldHsrp = ServerUtil.verifyForOldVehicleHsrp(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), tmgr);
                    if (isRegnNoGenerateForOtherStateVehicles(tmgr, appl_no, owner_dobj.getRegn_type())
                            || isRegnNoGenerateForOtherDistrict(tmgr, appl_no, owner_dobj.getRegn_type()) || isOldHsrp) {
                        ServerUtil.verifyInsertNewRegHsrpDetail(owner_dobj.getAppl_no(), owner_dobj.getRegn_no(), TableConstants.HSRP_NEW_BOTH_SIDE,
                                sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), tmgr);
                    }

                    ServerUtil.VerifyInsertSmartCardPrintDetail(owner_dobj.getAppl_no(), owner_dobj.getRegn_no(),
                            sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), status_dobj.getPur_cd(), tmgr);
                }

                if (noc != null) {
                    sql = "insert into " + TableList.VH_TMP_REGN_DTL + " select state_cd, off_cd,regn_no, tmp_off_cd, regn_auth, tmp_state_cd, "
                            + "tmp_regn_no, tmp_regn_dt, tmp_valid_upto, dealer_cd,?,current_timestamp,? from " + TableList.VT_TMP_REGN_DTL
                            + " where regn_no=? and state_cd= ? and off_cd= ? ";
                    ps = tmgr.prepareStatement(sql);

                    ps.setString(1, owner_dobj.getAppl_no());
                    ps.setString(2, sessionVariables.getEmpCodeLoggedIn());
                    ps.setString(3, noc.getRegn_no());
                    ps.setString(4, noc.getState_cd());
                    ps.setInt(5, noc.getOff_cd());
                    ps.executeUpdate();

                    sql = "Delete from " + TableList.VT_TMP_REGN_DTL + " where regn_no=? and state_cd= ? and off_cd= ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, noc.getRegn_no());
                    ps.setString(2, noc.getState_cd());
                    ps.setInt(3, noc.getOff_cd());
                    ps.executeUpdate();

                }

                if (owner_dobj.getTempReg() != null) {

                    sql = "insert into " + TableList.VT_TMP_REGN_DTL
                            + " (state_cd , off_cd ,regn_no, tmp_off_cd, regn_auth, tmp_state_cd, tmp_regn_no,"
                            + " tmp_regn_dt,tmp_valid_upto, dealer_cd, op_dt )"
                            + " values(?,?,?, ?, ?, ?, ?, ?,?, ? , current_timestamp)";
                    ps = tmgr.prepareStatement(sql);
                    int i = 1;
                    ps.setString(i++, sessionVariables.getStateCodeSelected());
                    ps.setInt(i++, sessionVariables.getOffCodeSelected());
                    ps.setString(i++, owner_dobj.getRegn_no());
                    ps.setInt(i++, owner_dobj.getTempReg().getTmp_off_cd());
                    ps.setString(i++, owner_dobj.getTempReg().getRegn_auth());
                    ps.setString(i++, owner_dobj.getTempReg().getTmp_state_cd());
                    ps.setString(i++, owner_dobj.getTempReg().getTmp_regn_no());
                    ps.setDate(i++, new java.sql.Date(owner_dobj.getTempReg().getTmp_regn_dt().getTime()));
                    ps.setDate(i++, new java.sql.Date(owner_dobj.getTempReg().getTmp_valid_upto().getTime()));
                    ps.setString(i++, owner_dobj.getTempReg().getDealer_cd());
                    ps.executeUpdate();

                    sql = "delete from " + TableList.VA_TMP_REGN_DTL
                            + " where appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, appl_no);
                    ps.executeUpdate();

                    InsImpl.insert_ins_dtls_to_Vh_insurance(tmgr, owner_dobj.getTempReg().getTmp_regn_no(), sessionVariables.getStateCodeSelected(), sessionVariables.getEmpCodeLoggedIn());
                }

                ScrappedVehicleImpl scrapImpl = new ScrappedVehicleImpl();
                scrapImpl.updateNewChaisNoForScrappedVeh(owner_dobj.getChasi_no(), appl_no, tmgr);

                sql = "select a.* from vt_tax a, vp_appl_rcpt_mapping  b where  a.rcpt_no=b.rcpt_no and a.state_cd=b.state_cd and "
                        + "a.off_cd=b.off_cd and b.appl_no=? and  a.tax_mode <> 'B'";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_no);
                rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    String regno = rs.getString("regn_no");
                    Date taxUpto = rs.getDate("tax_upto");
                    int purCd = rs.getInt("pur_cd");
                    TaxServer_Impl.insertUpdateTaxDefaulter(regno, taxUpto, purCd, tmgr);
                }

            }

            //for saving the data into table those are changed by the user
            //ServerUtil.insertIntoVhaChangedData(tmgr, owner_dobj.getAppl_no(), changedData);
            if (changedData != null && !changedData.equals(TableConstants.EMPTY_STRING)) {
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
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("New Registration : Error in Database Update");
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Update Error in New Registration Application");
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
        return regn_no;
    }//end of update_New_Status method

    /**
     * @author Sai
     */
    public String update_New_Status(int role, Owner_dobj owner_dobj, FitnessDobj fitness_dobj, ArrayList fitCheckList,
            Status_dobj status_dobj, String changedData, String userStateCode, HpaDobj hpa_dobj, InsDobj ins_dobj,
            Trailer_dobj trailer_dobj, ExArmyDobj exArmyDobj, ImportedVehicleDobj imp_dobj,
            AxleDetailsDobj axle_dobj, RetroFittingDetailsDobj cng_dobj, int action_cd, SessionVariablesModel sessionVariables,
            PendencyBankDobj bankSubsidyDetail, List<ComparisonBean> ins_change_list, String clientIpAddress, String selectedRoleCode) throws VahanException, Exception {
        String empCode = sessionVariables.getEmpCodeLoggedIn();
        int selectedOffCode = sessionVariables.getOffCodeSelected();
        String userId = sessionVariables.getUserIdForLoggedInUser();
        String userCategory = sessionVariables.getUserCatgForLoggedInUser();
        //  TmConfigurationDobj tmConfig = Util.getTmConfiguration();
        TmConfigurationDobj tmConfig = Utility.getTmConfiguration(null, userStateCode);

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String sql = null;
        ArrayList hpa_list = null;
        String regn_no = null;
        int pur_cd = status_dobj.getPur_cd();
        String appl_no = status_dobj.getAppl_no();
        String chassis_trailer;

        try {
            tmgr = new TransactionManager("update_NEW_Status");

            if (TableConstants.STATUS_HOLD.equals(status_dobj.getStatus()) && owner_dobj != null && owner_dobj.getOwner_identity() != null) {
                ServerUtility.sendSMSForHoldApplication(owner_dobj.getOwner_identity().getMobile_no(), status_dobj.getOffice_remark(), owner_dobj.getAppl_no(), userStateCode);
            }

            if (status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_COMPLETE)) {
                if ((owner_dobj != null
                        && !owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))
                        && isRegnNoGenerateForOtherStateVehicles(tmgr, appl_no, owner_dobj.getRegn_type())) {
                    boolean isRegnGenRqrd = true;
                    if (sessionVariables.getSelectedWork().getPur_cd() == 123 && !owner_dobj.getRegn_no().trim().equalsIgnoreCase("NEW") && owner_dobj.getAdvanceRegNoDobj() == null) {
                        isRegnGenRqrd = false;
                    }
                    if (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_CONFISCATED_AUCTION)) {
                        if (owner_dobj.getAuctionDobj() != null) {
                            if (tmConfig != null && tmConfig.isRegnNoNotAssignOthState()) {
                                if (owner_dobj.getAuctionDobj().getRegnNo() != null && !owner_dobj.getAuctionDobj().getRegnNo().equals("NEW")) {
                                    isRegnGenRqrd = isRegnNoGenerateForAuctionVehicles(tmgr, appl_no);
                                } else {
                                    isRegnGenRqrd = true;
                                }
                            } else {
                                isRegnGenRqrd = true;
                            }
                        }
                    }
                    if (isRegnGenRqrd) {
                        String payFancyApplNo = new AdvanceRegnFeeImpl().getPaymentApplicationNumber(appl_no, null);
                        if (!CommonUtils.isNullOrBlank(payFancyApplNo)) {
                            throw new VahanException("Online Payment has been initiated for FANCY Number Booking with Application Number " + payFancyApplNo);
                        }
                        if (owner_dobj.getAdvanceRegNoDobj() != null) {
                            owner_dobj.getAdvanceRegNoDobj().setRegn_appl_no(appl_no);
                            NewImplementation.updateAdvanceRegNoDetails(owner_dobj.getAdvanceRegNoDobj(), owner_dobj, tmgr);
                            //REGN_NO = owner_bean.getAdvanceRegNoDobj().getRegn_no();
                        }
                        // NewVehicleNo newVehicleNo = new NewVehicleNo();
                        NewVehicleNumber newVehicleNumber = new NewVehicleNumber();
                        regn_no = newVehicleNumber.generateAssignNewRegistrationNo(sessionVariables.getOffCodeSelected(), sessionVariables.getActionCodeSelected(), owner_dobj.getAppl_no(), null, 1, owner_dobj, null, tmgr, owner_dobj.getPurCD(), tmConfig.getState_cd(), sessionVariables.getUserLoginOffCode(), sessionVariables.getUserCatgForLoggedInUser(), sessionVariables.getEmpCodeLoggedIn());
                        if (!CommonUtils.isNullOrBlank(regn_no) && !"NEW".equals(regn_no) && TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE == status_dobj.getPur_cd()) {
                            ServerUtility.insertDealerHSRPPendencyDetails(tmgr, owner_dobj, regn_no, tmConfig);
                        }
                    }
                    if (bankSubsidyDetail != null) {
                        if (CommonUtils.isNullOrBlank(owner_dobj.getOwner_identity().getAadhar_no())) {
                            throw new VahanException("Aadhar no. can not blank for E-rickshaw.");
                        }
                        owner_dobj.getOwner_identity().setAadhar_no(null);
                        new PendencyBankDetailImplementation().saveOrUpdatePendencyBankDtls(tmgr, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), bankSubsidyDetail, sessionVariables.getEmpCodeLoggedIn());
                    }
                }

                if (regn_no != null) {
                    owner_dobj.setRegn_no(regn_no);
                    if (owner_dobj.getReflectiveTapeDobj() != null) {
                        owner_dobj.getReflectiveTapeDobj().setRegn_no(regn_no);
                    }

                    if (owner_dobj.getSpeedGovernorDobj() != null) {
                        owner_dobj.getSpeedGovernorDobj().setRegn_no(regn_no);
                    }
                }

                OwnerImplementation ownerImpl = new OwnerImplementation();
                if (owner_dobj != null) {//validation of seating capicity based on vehicle class
                    ownerImpl.seatingCapacityValidation(owner_dobj.getSeat_cap(), owner_dobj.getVh_class(), TableConstants.VH_CLASS_ALLOWED_ZERO_SEAT_CAP);
                    // validate Insurance period
                    if (ins_dobj.getIns_type() != TableConstants.INS_TYPE_THIRD_PARTY && ins_dobj.getIns_type() != Integer.parseInt(TableConstants.INS_TYPE_NA)) {
                        ins_dobj.setIns_type(TableConstants.INS_TYPE_THIRD_PARTY);
                    }
                    ServerUtility.validateMinInsuranceValidity(ins_dobj.getInsPeriodYears(), owner_dobj.getVh_class(), owner_dobj.getRegn_type(), pur_cd, ins_dobj.getIns_type());
                }
                VehicleParameters parameters = FormulaUtilities.fillVehicleParametersFromDobj(owner_dobj, sessionVariables);
                status_dobj.setVehicleParameters(parameters);
                parameters.setAPPL_DATE(ServerUtility.setTempApplDtAsNewApplDT(status_dobj));
                parameters.setPUR_CD(pur_cd);
                parameters.setACTION_CD(action_cd);
                boolean isValidForRegistration = ServerUtility.validateVehicleNorms(owner_dobj, pur_cd, parameters, tmConfig.getTmConfigDealerDobj());
                if (!isValidForRegistration) {
                    throw new VahanException("State Transport Department has not authorized you to further process this Registration Application of '" + MasterTableFiller.masterTables.VM_VH_CLASS.getDesc(owner_dobj.getVh_class() + "") + "' with emission norms " + MasterTableFiller.masterTables.VM_NORMS.getDesc(owner_dobj.getNorms() + "") + " , Purchase Date: " + DateUtil.parseDateToString(owner_dobj.getPurchase_dt()) + " , Application Date: " + DateUtil.parseDateToString(DateUtil.parseDate(DateUtil.convertStringYYYYMMDDToDDMMYYYY(parameters.getAPPL_DATE()))) + ", please contact respective Registering Authority regarding this.");
                }
                ServerUtility.saveVaOwnerDisclaimerDetails(owner_dobj, tmgr, empCode, action_cd);
                if (TableConstants.TM_NEW_RC_APPROVAL == action_cd || TableConstants.TM_ROLE_DEALER_APPROVAL == action_cd) {
                    ServerUtility.updateApplNoOfBSIVChassis(owner_dobj, tmgr, pur_cd);
                }
            }

            if (role == TableConstants.TM_ROLE_ENTRY || role == TableConstants.TM_ROLE_VERIFICATION
                    || role == TableConstants.TM_ROLE_APPROVAL
                    || action_cd == TableConstants.TM_ROLE_NEW_APPL && owner_dobj.getAppl_no() != null
                    && !owner_dobj.getAppl_no().equals(TableConstants.EMPTY_STRING)) {

                /*This check is used to restrict the Disel Vehicle Registration in Delhi Only*/
                if (!CommonUtils.isNullOrBlank(appl_no) && tmConfig.isNew_reg_loi() && owner_dobj.getNewLoiNo() != null && pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_NEW) && owner_dobj.getVh_class() == 57) {
                    PermitImplementation permitImpl = new PermitImplementation();
                    permitImpl.insertVhaNewLoiDetails(appl_no, owner_dobj.getNewLoiNo(), tmgr);
                    permitImpl.updateNewLoiDetails(appl_no, owner_dobj.getNewLoiNo(), tmgr);
                }

                if (action_cd == TableConstants.TM_ROLE_NEW_APPL || action_cd == TableConstants.TM_ROLE_DEALER_VERIFICATION) {
                    VehicleParameters vehParameters = FormulaUtilities.fillVehicleParametersFromDobj(owner_dobj, sessionVariables);
                    String isCriteriaMatchMsg = ServerUtility.isNewRegnNotAllowed(vehParameters, userStateCode, selectedOffCode);
                    if (isCriteriaMatchMsg != null && !isCriteriaMatchMsg.isEmpty()) {
                        throw new VahanException(isCriteriaMatchMsg);
                    }
                }

                if ((action_cd == TableConstants.TM_NEW_RC_APPROVAL || action_cd == TableConstants.TM_ROLE_DEALER_APPROVAL) && !owner_dobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)
                        && ServerUtility.verifyForSmartCard(userStateCode, selectedOffCode, tmgr)) {
                    if (!tmConfig.isSmartcard_fee_at_vendor() && !SmartCardImpl.getSmartCardFeePaid(appl_no, tmgr) && !"4,5".contains(String.valueOf(owner_dobj.getOwner_cd()))) {
                        throw new VahanException("Smartcard Fee Not Paid, Please first deposit with the Balance Fee Option.");
                    }
                }

                if (role == TableConstants.TM_ROLE_ENTRY) {
                    insertOrUpdateVaOwner(tmgr, owner_dobj, empCode, pur_cd, selectedOffCode, userStateCode, action_cd);
                    if (userStateCode.equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(owner_dobj.getVh_class()) + ",")) {
                        insertOrUpdateVaOwnerOther(tmgr, owner_dobj, empCode, userStateCode, selectedOffCode);
                    }
                } else if ((role == TableConstants.TM_ROLE_VERIFICATION
                        || role == TableConstants.TM_ROLE_REV_NEW_APPL || role == TableConstants.TM_ROLE_APPROVAL) && !changedData.isEmpty()) {
                    insertOrUpdateVaOwner(tmgr, owner_dobj, empCode, pur_cd, selectedOffCode, userStateCode, action_cd);
                    if (role != TableConstants.TM_ROLE_APPROVAL && userStateCode.equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(owner_dobj.getVh_class()) + ",")) {
                        insertOrUpdateVaOwnerOther(tmgr, owner_dobj, empCode, userStateCode, selectedOffCode);
                    }
                }

                if (hpa_dobj != null) {
                    hpa_list = new ArrayList();
                    hpa_dobj.setRegn_no(owner_dobj.getRegn_no());
                    hpa_list.add(hpa_dobj);
                    if (hpa_list.size() > 1) {
                        throw new VahanException("Multiple Hypothecation not allowed.");
                    }
                    HpaImplementation.insertUpdateHPA(tmgr, hpa_list, userStateCode, selectedOffCode, empCode);
                } else {
                    HpaImplementation.insertDeleteFromVaHpa(tmgr, appl_no, empCode);
                }

                if (!ins_change_list.isEmpty()) {
                    if (InsImplementation.validateOwnerCodeWithInsType(owner_dobj.getOwner_cd(), ins_dobj.getIns_type())) {
                        InsImplementation.insertUpdateInsurance(tmgr, owner_dobj.getAppl_no(), owner_dobj.getRegn_no(), ins_dobj, userStateCode, selectedOffCode, empCode);
                    } else {
                        throw new VahanException("Invalid Combination of Ownership Type & Insurance Type.");
                    }
                }

                // During Verification Trailer
                if (trailer_dobj != null && trailer_dobj.getChasi_no() != null && !trailer_dobj.getChasi_no().trim().isEmpty()) {
                    TrailerImplementation.validationTrailer(trailer_dobj);
                    TrailerImplementation.insertUpdateTrailer(tmgr, owner_dobj.getAppl_no(), owner_dobj.getRegn_no(), owner_dobj.getChasi_no(),
                            trailer_dobj, empCode, userStateCode, selectedOffCode);
                }

                if (exArmyDobj != null) {
                    ExArmyImplementation.saveExArmyVehicleDetails_Impl(exArmyDobj, owner_dobj.getAppl_no(), tmgr, empCode, userStateCode, selectedOffCode);
                } else {
                    ExArmyImplementation.insertIntoVhaExArmy(tmgr, owner_dobj.getAppl_no(), empCode);
                    ExArmyImplementation.deleteFromVaExArmy(tmgr, owner_dobj.getAppl_no());
                }
                if (imp_dobj != null) {
                    ImportedVehicleImplementation.saveImportedDetails_Impl(imp_dobj, owner_dobj.getAppl_no(), tmgr, empCode, userStateCode, selectedOffCode);
                } else {
                    ImportedVehicleImplementation.insertIntoVhaImpVeh(tmgr, owner_dobj.getAppl_no(), empCode);
                    ImportedVehicleImplementation.deleteFromVaImp(tmgr, owner_dobj.getAppl_no());
                }
                if (axle_dobj != null) {
                    AxleImplementation.saveAxleDetails_Impl(axle_dobj, owner_dobj.getAppl_no(), tmgr, empCode, userStateCode, selectedOffCode);
                } else {
                    AxleImplementation.insertIntoVhaAxle(tmgr, owner_dobj.getAppl_no(), empCode);
                    AxleImplementation.deleteFromVaAxle(tmgr, owner_dobj.getAppl_no());
                }
                if (cng_dobj != null) {
                    RetroFittingDetailsImplementation.saveCngVehicleDetails_Impl(cng_dobj, owner_dobj.getAppl_no(), tmgr, empCode, userStateCode, selectedOffCode);
                } else {
                    RetroFittingDetailsImplementation.insertIntoVhaCng(tmgr, owner_dobj.getAppl_no(), empCode);
                    RetroFittingDetailsImplementation.deleteFromVaRetroFittingDetails(tmgr, owner_dobj.getAppl_no());
                }

                if (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                        || owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                    OtherStateVehImplementation impl = new OtherStateVehImplementation();
                    impl.insertUpdateOtherStateVeh(tmgr, owner_dobj.getOtherStateVehDobj(), empCode, userStateCode, selectedOffCode);
                }

                ////////////// for e-rickshaw bank subsidy update regn. no /////////////////
                if ("DL".equals(userStateCode) && pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                        && owner_dobj.getRegn_no() != null && owner_dobj.getVh_class() == TableConstants.ERICKSHAW_VCH_CLASS
                        && action_cd == TableConstants.TM_NEW_RC_APPROVAL && owner_dobj.getOwner_cd() == TableConstants.OWNER_CODE_INDIVIDUAL) {
                    PendencyBankDetailImpl.updateRegnNoForBankSubsidy(tmgr, owner_dobj.getRegn_no(), owner_dobj.getState_cd(), owner_dobj.getOff_cd(), owner_dobj.getAppl_no());
                }

                if (tmConfig != null && tmConfig.getTmConfigOtpDobj() != null && tmConfig.getTmConfigOtpDobj().isOwner_mobile_verify_with_otp() && status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_COMPLETE) && (action_cd == TableConstants.TM_NEW_RC_APPROVAL || action_cd == TableConstants.TM_ROLE_DEALER_APPROVAL)
                        && (pur_cd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE)) {
                    ServerUtility.saveUpdateOTPVerifyDetails(tmgr, owner_dobj);
                }
                if (status_dobj.getStatus().equalsIgnoreCase(TableConstants.STATUS_COMPLETE) && (action_cd == TableConstants.TM_NEW_RC_APPROVAL || action_cd == TableConstants.TM_ROLE_DEALER_APPROVAL)) {
                    Map<String, String> ownerChoiceRegnMap = OwnerChoiceNoImpl.getOwnerChoiceRegnNoDetails(appl_no);
                    if (ownerChoiceRegnMap != null && !ownerChoiceRegnMap.isEmpty()) {
                        new OwnerChoiceNoImpl().insertIntoHistoryChoiceNumber(tmgr, empCode, TableConstants.FANCY_ACCEPTED, appl_no, userStateCode);
                        String rcptNo = OfficeCorrectionImpl.getRcptNoForFeePaidForPurpose(appl_no, TableConstants.VM_TRANSACTION_MAST_CHOICE_NO);
                        new OwnerChoiceNoImpl().insertIntoVtAdvanceRegn(owner_dobj, tmgr, rcptNo, Integer.parseInt(ownerChoiceRegnMap.get("choice_fees")), owner_dobj.getOwner_identity().getMobile_no());
                    }
                }
                if (owner_dobj.getAdvanceRegNoDobj() != null) {
                    owner_dobj.getAdvanceRegNoDobj().setRegn_appl_no(appl_no);
                    NewImplementation.updateAdvanceRegNoDetails(owner_dobj.getAdvanceRegNoDobj(), owner_dobj, tmgr);
                    //REGN_NO = owner_bean.getAdvanceRegNoDobj().getRegn_no();
                }

                if (owner_dobj.getRetenRegNoDobj() != null) {
                    owner_dobj.getRetenRegNoDobj().setRegn_appl_no(appl_no);
                    NewImplementation.updateRetenRegNoDetails(owner_dobj.getRetenRegNoDobj(), tmgr);
                }

                if (owner_dobj.getCdDobj() != null) {
                    CdImplementation cdImpl = new CdImplementation();
                    cdImpl.insertUpdateVaCd(owner_dobj.getCdDobj(), tmgr, empCode);
                }

                if (owner_dobj.getSpeedGovernorDobj() != null) {
                    FitnessImplementation.insertUpdateVaSpeedGovernor(owner_dobj.getSpeedGovernorDobj(), tmgr, empCode);
                } else {
                    FitnessImplementation.insertIntoVhaSpeedGovernor(appl_no, tmgr, empCode);
                    FitnessImplementation.deleteVaSpeedGovernor(appl_no, tmgr);
                }

                if (owner_dobj.getReflectiveTapeDobj() != null) {
                    new FitnessImplementation().insertOrUpdateVaReflectiveTape(tmgr, owner_dobj.getReflectiveTapeDobj(), empCode);
                } else {
                    FitnessImplementation fitImpl = new FitnessImplementation();
                    fitImpl.insertIntoVhaReflectiveTape(tmgr, appl_no, empCode);
                    fitImpl.deleteVaReflectiveTape(appl_no, tmgr);
                }
            }
            //for inspection of non transport vehicle
            if (action_cd == TableConstants.TM_NEW_RC_FITNESS_INSPECTION
                    && owner_dobj != null && owner_dobj.getInspectionDobj() != null
                    && owner_dobj.getInspectionDobj().getInsp_dt() != null) {
                FitnessImplementation fitImpl = new FitnessImplementation();
                owner_dobj.getInspectionDobj().setFit_off_cd1(Integer.parseInt(empCode));
                fitImpl.insertOrUpdateInspection(tmgr, owner_dobj.getInspectionDobj(), empCode);
            }

            //fitness inspection validation
            if (action_cd == TableConstants.TM_NEW_RC_FITNESS_INSPECTION
                    || action_cd == TableConstants.TM_NEW_RC_FITNESS_INSPECTION_APPROVAL) {

                if (fitness_dobj != null) {
                    if (fitness_dobj.getFit_valid_to() == null) {
                        throw new VahanException("Fitness Valid Upto Can not be Empty,Please Select Fitness/Inpection Test Date again for Calculation of Fitness Valid Upto.");
                    }
                    if (fitness_dobj.getFit_result() == null || fitness_dobj.getFit_result().trim().length() <= 0) {
                        throw new VahanException("Can't Proceed due to Fitness Result (Fail or Pass) is not Provided by User");
                    }
                    if (fitness_dobj.getFit_result().equalsIgnoreCase(TableConstants.FitnessResultFail)) {
                        throw new VahanException("Can't Proceed due to Fitness is Failed.You Can Use only Save Option.");
                    }
                    if (fitness_dobj.getFit_valid_to() != null && fitness_dobj.getFit_valid_to().before(new Date())) {
                        throw new VahanException("Fitness Valid Upto Can't be less than Current Date.");
                    }
                }
            }

            if (role == TableConstants.TM_ROLE_INSPECTION || TableConstants.TM_NEW_RC_FITNESS_INSPECTION_APPROVAL == action_cd) {

                if ("KL".contains(status_dobj.getState_cd())) {
                    AdvanceRegnNo_dobj advDobj = owner_dobj.getAdvanceRegNoDobj();
                    if (advDobj == null) {
                        advDobj = getAdvanceFeeDetailsMessage(owner_dobj.getAppl_no(), userStateCode, selectedOffCode);
                    }
                    if (advDobj != null) {
                        Date fancyRcptDate = getFancyNoRcptDate(advDobj.getRecp_no(), userStateCode, selectedOffCode);
                        Date fancyUpto = fancyValidUpto(tmConfig, fancyRcptDate);
                        String dateString = DateUtils.getCurrentDate_YYYY_MM_DD();
                        Date currentDate = JSFUtils.getStringToDateyyyyMMdd(dateString);

                        if (fancyUpto.before(currentDate)) {
                            throw new VahanException("Fancy Receipt Validatity is Expired!!!,Please Reopen Fancy Registration Number");
                        }
                    }
                }

                if (!changedData.isEmpty()) {
                    insertOrUpdateVaOwner(tmgr, owner_dobj, empCode, pur_cd, selectedOffCode, userStateCode, action_cd);
                    if (userStateCode.equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(owner_dobj.getVh_class()) + ",")) {
                        insertOrUpdateVaOwnerOther(tmgr, owner_dobj, empCode, userStateCode, selectedOffCode);
                    }
                }

                if (owner_dobj.getSpeedGovernorDobj() != null) {
                    FitnessImplementation.insertUpdateVaSpeedGovernor(owner_dobj.getSpeedGovernorDobj(), tmgr, empCode);
                } else {
                    FitnessImplementation.insertIntoVhaSpeedGovernor(appl_no, tmgr, owner_dobj.getEmpCode());
                    FitnessImplementation.deleteVaSpeedGovernor(appl_no, tmgr);
                }

                if (owner_dobj.getReflectiveTapeDobj() != null) {
                    new FitnessImplementation().insertOrUpdateVaReflectiveTape(tmgr, owner_dobj.getReflectiveTapeDobj(), empCode);
                } else {
                    FitnessImplementation fitImpl = new FitnessImplementation();
                    fitImpl.insertIntoVhaReflectiveTape(tmgr, appl_no, owner_dobj.getEmpCode());
                    fitImpl.deleteVaReflectiveTape(appl_no, tmgr);
                }

                if (trailer_dobj != null && !CommonUtils.isNullOrBlank(trailer_dobj.getChasi_no())) {
                    TrailerImplementation.validationTrailer(trailer_dobj);
                    TrailerImplementation.insertUpdateTrailer(tmgr, owner_dobj.getAppl_no(), owner_dobj.getRegn_no(), owner_dobj.getChasi_no(),
                            trailer_dobj, empCode, userStateCode, selectedOffCode);
                }

                if (fitness_dobj != null) {
                    fitness_dobj.setAppl_no(owner_dobj.getAppl_no());
                    FitnessImplementation.insertOrUpdateFitnessMoveFile(tmgr, owner_dobj.getRegn_no(), owner_dobj.getChasi_no(), fitness_dobj, fitCheckList, userStateCode, selectedOffCode, empCode);

                    if (hpa_dobj != null) {
                        hpa_list = new ArrayList();
                        hpa_dobj.setRegn_no(owner_dobj.getRegn_no());
                        hpa_list.add(hpa_dobj);
                        HpaImplementation.insertUpdateHPA(tmgr, hpa_list, userStateCode, selectedOffCode, empCode);
                    } else {
                        HpaImplementation.insertDeleteFromVaHpa(tmgr, appl_no, empCode);
                    }

                    if (InsImplementation.validateOwnerCodeWithInsType(owner_dobj.getOwner_cd(), ins_dobj.getIns_type())) {
                        InsImplementation.insertUpdateInsurance(tmgr, owner_dobj.getAppl_no(), owner_dobj.getRegn_no(), ins_dobj, userStateCode, selectedOffCode, empCode);
                    } else {
                        throw new VahanException("Invalid Combination of Ownership Type & Insurance Type.");
                    }

                    if (exArmyDobj != null) {
                        ExArmyImplementation.saveExArmyVehicleDetails_Impl(exArmyDobj, owner_dobj.getAppl_no(), tmgr, empCode, userStateCode, selectedOffCode);
                    } else {
                        ExArmyImplementation.insertIntoVhaExArmy(tmgr, owner_dobj.getAppl_no(), empCode);
                        ExArmyImplementation.deleteFromVaExArmy(tmgr, owner_dobj.getAppl_no());
                    }
                    if (imp_dobj != null) {
                        ImportedVehicleImplementation.saveImportedDetails_Impl(imp_dobj, owner_dobj.getAppl_no(), tmgr, empCode, userStateCode, selectedOffCode);
                    } else {
                        ImportedVehicleImplementation.insertIntoVhaImpVeh(tmgr, owner_dobj.getAppl_no(), empCode);
                        ImportedVehicleImplementation.deleteFromVaImp(tmgr, owner_dobj.getAppl_no());
                    }
                    if (axle_dobj != null) {
                        AxleImplementation.saveAxleDetails_Impl(axle_dobj, owner_dobj.getAppl_no(), tmgr, empCode, userStateCode, selectedOffCode);
                    } else {
                        AxleImplementation.insertIntoVhaAxle(tmgr, owner_dobj.getAppl_no(), empCode);
                        AxleImplementation.deleteFromVaAxle(tmgr, owner_dobj.getAppl_no());
                    }
                    if (cng_dobj != null) {
                        RetroFittingDetailsImplementation.saveCngVehicleDetails_Impl(cng_dobj, owner_dobj.getAppl_no(), tmgr, empCode, userStateCode, selectedOffCode);
                    } else {
                        RetroFittingDetailsImplementation.insertIntoVhaCng(tmgr, owner_dobj.getAppl_no(), empCode);
                        RetroFittingDetailsImplementation.deleteFromVaRetroFittingDetails(tmgr, owner_dobj.getAppl_no());
                    }

                }
                if (tmConfig != null && tmConfig.getRegn_gen_type().equalsIgnoreCase(TableConstants.NO_GEN_TYPE_RAND)) {

                    sql = "INSERT INTO " + TableList.VA_RANDOM_REGN_NO + " (appl_no,state_cd,off_cd) values (?,?,?)";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, owner_dobj.getAppl_no());
                    ps.setString(2, owner_dobj.getState_cd());
                    ps.setInt(3, selectedOffCode);
                    ps.executeUpdate();
                }

                if (FitnessImplementation.isFitnessFeePaid(status_dobj.getAppl_no()) && TableConstants.TM_NEW_RC_FITNESS_INSPECTION_APPROVAL == action_cd) {

                    sql = "Select * from " + TableList.VA_FC_PRINT + " where appl_no=? and state_cd=? and off_cd=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status_dobj.getAppl_no());
                    ps.setString(2, userStateCode);
                    ps.setInt(3, selectedOffCode);

                    RowSet rs = tmgr.fetchDetachedRowSet_No_release();

                    if (!rs.next()) {
                        sql = "Insert into " + TableList.VA_FC_PRINT + " ( state_cd,off_cd,appl_no, regn_no, op_dt)  VALUES (?, ?, ?,?,current_timestamp) ";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, userStateCode);
                        ps.setInt(2, selectedOffCode);
                        ps.setString(3, status_dobj.getAppl_no());
                        ps.setString(4, owner_dobj.getRegn_no());
                        ps.executeUpdate();
                    }
                }
            }

            if (owner_dobj != null && owner_dobj.getVehicleTrackingDetailsDobj() != null) {
                new VehicleTrackingDetailsImplementation().updateStatusVehicleTrackingDetails(owner_dobj.getVehicleTrackingDetailsDobj(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), tmgr);
            }
            if (role == TableConstants.TM_ROLE_APPROVAL
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_REVERT)
                    && !status_dobj.getStatus().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                FitnessImplementation fitness_Impl = new FitnessImplementation();
                FitnessDobj fitDobj = fitness_Impl.set_Fitness_appl_db_to_dobj(null, owner_dobj.getAppl_no());
                if (fitDobj != null) {
                    owner_dobj.setFit_upto(fitDobj.getFit_valid_to());
                }

                FasTagImpl.updateRegnNoInFasTag(tmgr, owner_dobj.getChasi_no(), owner_dobj.getRegn_no(), owner_dobj.getState_cd(), owner_dobj.getOff_cd());

                RowSet rs = null;
                /**
                 * For Gujrat,if user has applied for fancy number,his
                 * application should not be approved un till result of auction
                 * is out
                 */
                /*sql = "Select fn_book_appl_no from fancy.fn_book_appl_no(?)  ";
                 ps = tmgr.prepareStatement(sql);
                 ps.setString(1, owner_dobj.getAppl_no());
                 RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                 if ("GJ".contains(owner_dobj.getState_cd()) && rs.next()) {
                 String fancyApplno = rs.getString("fn_book_appl_no");
                 if (fancyApplno != null && !fancyApplno.isEmpty()) {
                 throw new VahanException("Result Of Fancy Auction is still not out ");
                 }
                 }*/
                if (owner_dobj.getVehicleTrackingDetailsDobj() != null) {
                    owner_dobj.getVehicleTrackingDetailsDobj().setRegn_no(owner_dobj.getRegn_no());
                    new VehicleTrackingDetailsImplementation().approveStatusVehTrackDetails(owner_dobj.getVehicleTrackingDetailsDobj(), userStateCode, selectedOffCode, tmgr, empCode);
                }

                AdvanceRegnNo_dobj advDobj = owner_dobj.getAdvanceRegNoDobj();
                if (advDobj == null) {
                    advDobj = getAdvanceFeeDetailsMessage(owner_dobj.getAppl_no(), userStateCode, selectedOffCode);
                }
                if ("MH".contains(owner_dobj.getState_cd())) {
                    if (advDobj != null) {
                        Date fancyRcptDate = getFancyNoRcptDate(advDobj.getRecp_no(), userStateCode, selectedOffCode);
                        Date fancyUpto = fancyValidUpto(tmConfig, fancyRcptDate);
                        String dateString = DateUtils.getCurrentDate_YYYY_MM_DD();
                        Date currentDate = JSFUtils.getStringToDateyyyyMMdd(dateString);
                        //Checking verification date for Dealer Non-Transport
                        if (status_dobj.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE && owner_dobj.getVehType() == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                            currentDate = getDealerVerificationDate(status_dobj.getAppl_no(), TableConstants.TM_NEW_RC_VERIFICATION, userStateCode, selectedOffCode);
                        }
                        //
                        if (fitDobj != null && fitDobj.getFit_chk_dt() != null) {
                            currentDate = fitDobj.getFit_chk_dt();
                        }
                        if (owner_dobj.getInspectionDobj() != null && owner_dobj.getInspectionDobj().getInsp_dt() != null) {
                            currentDate = owner_dobj.getInspectionDobj().getInsp_dt();
                        }

                        if (fancyUpto.before(currentDate)) {
                            throw new VahanException("Fancy Receipt Validatity is Expired!!!,Please Reopen Fancy Registration Number");
                        }
                    }
                }

                if (advDobj != null) {
                    validateFancyVehicleCategory(advDobj, owner_dobj.getVch_catg(), tmgr);
                }

                int reg_upto = getNewRegUpto(owner_dobj, sessionVariables);
                Date regUpto = null;
                /*
                 * For other state/district vehciles fit upto(if No inspection is done),regn upto and fit upto will
                 * be details from previous state or rto
                 */
                if (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                        || owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                    deleteFromVtTables(owner_dobj.getRegn_no(), owner_dobj.getAppl_no(), empCode);
                    if (!isRegnNoGenerateForOtherStateVehicles(tmgr, appl_no, owner_dobj.getRegn_type())
                            && owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)) {
                        new NewVehicleNumber().asignAndUpdateNewVehicleNo(owner_dobj.getOtherStateVehDobj().getOldRegnNo(), appl_no, owner_dobj.getState_cd(), owner_dobj.getOff_cd(), TableConstants.EMPTY_STRING, TableConstants.EMPTY_STRING, tmgr);
                        status_dobj.setRegn_no(owner_dobj.getOtherStateVehDobj().getOldRegnNo());
                        owner_dobj.setRegn_no(owner_dobj.getOtherStateVehDobj().getOldRegnNo());
                    } else if (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                        status_dobj.setRegn_no(owner_dobj.getOtherStateVehDobj().getOldRegnNo());
                    } else {
                        status_dobj.setRegn_no(owner_dobj.getRegn_no());
                    }

                } else if (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_SCRAPPED)) {
                    if (reg_upto > 0) {
                        regUpto = DateUtils.addToDate(new Date(), 3, reg_upto);
                        regUpto = DateUtils.addToDate(regUpto, 1, -1);
                        owner_dobj.setRegn_upto(regUpto);
                    }

                    if (fitDobj == null) {
                        int fit_upto = FitnessImplementation.getNewFitnessUpto(owner_dobj, sessionVariables);
                        if (fit_upto > 0) {
                            Date fitUpto = DateUtils.addToDate(new Date(), 3, fit_upto);
                            fitUpto = DateUtils.addToDate(fitUpto, 1, -1);
                            owner_dobj.setFit_upto(fitUpto);
                        }
                    }
                    status_dobj.setRegn_no(owner_dobj.getScrappedVehicleDobj().getOld_regn_no());
                } else if (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_CD)) {

                    if (reg_upto > 0) {
                        regUpto = DateUtils.addToDate(owner_dobj.getPurchase_dt(), 3, reg_upto);
                        regUpto = DateUtils.addToDate(regUpto, 1, -1);
                        owner_dobj.setRegn_upto(regUpto);
                    }

                    if (fitDobj == null) {
                        int fit_upto = FitnessImplementation.getNewFitnessUpto(owner_dobj, sessionVariables);
                        if (fit_upto > 0) {
                            Date fitUpto = DateUtils.addToDate(owner_dobj.getPurchase_dt(), 3, fit_upto);
                            fitUpto = DateUtils.addToDate(fitUpto, 1, -1);
                            owner_dobj.setFit_upto(fitUpto);
                        }
                    }
                    status_dobj.setRegn_no(owner_dobj.getRegn_no());

                } else {

                    if (reg_upto > 0) {
                        //regUpto = DateUtils.addToDate(new Date(), 3, reg_upto);
                        regUpto = DateUtils.addToDate(new OwnerImplementation().getVaOwnerRegistrationDate(appl_no, tmgr), 3, reg_upto);
                        //new OwnerImpl().getVaOwnerRegistrationDate(appl_no, tmgr);
                        regUpto = DateUtils.addToDate(regUpto, 1, -1);
                        owner_dobj.setRegn_upto(regUpto);
                    }

                    if (fitDobj == null) {
                        int fit_upto = FitnessImplementation.getNewFitnessUpto(owner_dobj, sessionVariables);
                        if (fit_upto > 0) {
                            Date fitUpto = DateUtils.addToDate(new OwnerImplementation().getVaOwnerRegistrationDate(appl_no, tmgr), 3, fit_upto);
                            fitUpto = DateUtils.addToDate(fitUpto, 1, -1);
                            owner_dobj.setFit_upto(fitUpto);
                        }
                    }
                    status_dobj.setRegn_no(owner_dobj.getRegn_no());
                }

                //for inspection of non-transport vehicle
                if (owner_dobj.getInspectionDobj() != null
                        && owner_dobj.getInspectionDobj().getInsp_dt() != null) {
                    FitnessImplementation fitImpl = new FitnessImplementation();
                    owner_dobj.getInspectionDobj().setRegn_no(owner_dobj.getRegn_no());
                    fitImpl.insertOrUpdateInspection(tmgr, owner_dobj.getInspectionDobj(), empCode);
                    fitImpl.insertVtInspection(tmgr, owner_dobj.getInspectionDobj());
                    ServerUtility.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_INSPECTION);
                }

                if (status_dobj.getRegn_no() == null || status_dobj.getRegn_no().equalsIgnoreCase(TableConstants.EMPTY_STRING) || status_dobj.getRegn_no().equalsIgnoreCase("NEW")) {
                    throw new VahanException("Registration No Generation Failed!!!");
                }

                if (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_CONFISCATED_AUCTION)) {
                    if (owner_dobj.getAuctionDobj() != null && owner_dobj.getAuctionDobj().getRegnNo() != null) {
                        if (!owner_dobj.getAuctionDobj().getRegnNo().equals("NEW") && owner_dobj.getAuctionDobj().getAuctionBy() != null && owner_dobj.getAuctionDobj().getAuctionBy().equals("R")) {
                            sql = "Select status from " + TableList.VT_OWNER + " WHERE regn_no=? and state_cd  = ? and off_cd =? "
                                    + " and status = '" + TableConstants.VT_AUCTION_STATUS + "'";
                            ps = tmgr.prepareStatement(sql);
                            ps.setString(1, owner_dobj.getAuctionDobj().getRegnNo());
                            ps.setString(2, owner_dobj.getAuctionDobj().getStateCdFrom());
                            ps.setInt(3, owner_dobj.getAuctionDobj().getOffCdFrom());
                            rs = tmgr.fetchDetachedRowSet_No_release();
                            if (rs.next()) {
                                insertIntoVhOwner(tmgr, status_dobj, owner_dobj.getAuctionDobj().getStateCdFrom(), owner_dobj.getAuctionDobj().getOffCdFrom(), owner_dobj.getAuctionDobj().getRegnNo());
                                insertIntoVtOwnerNOC(tmgr, status_dobj, owner_dobj.getAuctionDobj().getStateCdFrom(), owner_dobj.getAuctionDobj().getOffCdFrom(), owner_dobj.getAuctionDobj().getRegnNo());
                                deleteFromVtOwner(tmgr, owner_dobj.getAuctionDobj().getStateCdFrom(), owner_dobj.getAuctionDobj().getOffCdFrom(), owner_dobj.getAuctionDobj().getRegnNo());
                                insertIntoVehReassign(tmgr, status_dobj.getRegn_no(), owner_dobj.getAuctionDobj().getRegnNo(), status_dobj.getAppl_no(), TableConstants.AUCTION_REMARK, userStateCode, selectedOffCode, empCode);
                                new AuctionImpl().insertIntoVhAuction(tmgr, status_dobj, owner_dobj.getAuctionDobj().getRegnNo(), owner_dobj.getAuctionDobj().getChasiNo());
                                ServerUtility.deleteFromTable(tmgr, owner_dobj.getAuctionDobj().getRegnNo(), null, TableList.VT_AUCTION);
                                deleteFromVtTablesForAuction(tmgr, owner_dobj.getAuctionDobj().getRegnNo(), owner_dobj.getAppl_no(), owner_dobj.getAuctionDobj().getStateCdFrom(), owner_dobj.getAuctionDobj().getOffCdFrom(), empCode);
                            }
                        } else {
                            if (!owner_dobj.getAuctionDobj().getRegnNo().equals("NEW")) {
                                insertIntoVehReassign(tmgr, status_dobj.getRegn_no(), owner_dobj.getAuctionDobj().getRegnNo(), status_dobj.getAppl_no(), TableConstants.AUCTION_REMARK);
                            }
                            new AuctionImpl().insertIntoVhAuction(tmgr, status_dobj, owner_dobj.getAuctionDobj().getRegnNo(), owner_dobj.getAuctionDobj().getChasiNo());
                            sql = "delete from " + TableList.VT_AUCTION + " where chasi_no = ? and state_cd = ? and off_cd = ? ";
                            ps = tmgr.prepareStatement(sql);
                            ps.setString(1, owner_dobj.getAuctionDobj().getChasiNo());
                            ps.setString(2, owner_dobj.getAuctionDobj().getStateCd());
                            ps.setInt(3, owner_dobj.getAuctionDobj().getOffCd());
                            ps.executeUpdate();
                        }
                    }
                }

                /**
                 * A check is placed to avoid assignment of Fancy Numbers that
                 * are same as any old registration number.
                 */
                sql = "Select status from " + TableList.VT_OWNER + " WHERE regn_no=? and state_cd  = ? and off_cd =? "
                        + " and status='N'";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, status_dobj.getRegn_no());
                ps.setString(2, userStateCode);
                ps.setInt(3, selectedOffCode);
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    throw new VahanException("Vehicle Details already available in VAHAN &"
                            + " NOC is Yet Not issued for this Vehicle!!!");
                }

                String oldRegno = owner_dobj.getOtherStateVehDobj() != null ? owner_dobj.getOtherStateVehDobj().getOldRegnNo() : null;
                NocDobj noc = null;
                if (oldRegno != null) {

                    NocImplementation nocImpl = new NocImplementation();
                    noc = nocImpl.set_NOC_appl_db_to_dobj(null, oldRegno);
                    if (noc != null) {
                        sql = "Select a.regn_no,b.noc_no from VT_OWNER a \n"
                                + " left join " + TableList.VT_NOC_VERIFICATION + " b on b.regn_no=a.regn_no or b.chasi_no=a.chasi_no\n"
                                + " WHERE a.regn_no = ? and a.state_cd  = ? and a.off_cd = ? and a.status = 'N'  ";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, oldRegno);
                        ps.setString(2, noc.getState_cd());
                        ps.setInt(3, noc.getOff_cd());
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        if (rs.next()) {
                            if (rs.getString("noc_no") == null) {
                                if ((!noc.getState_to().equals(userStateCode)
                                        || (noc.getOff_to() != 0 && noc.getOff_to() != selectedOffCode))) {
                                    throw new VahanException("NOC is Not Issued for this Vehicle to this State or Office!!!");
                                }
                            }
                            if (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                                    || owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                                insertIntoVtOwnerNOC(tmgr, status_dobj, noc.getState_cd(), noc.getOff_cd(), oldRegno);
                            }
                            insertIntoVhOwner(tmgr, status_dobj, noc.getState_cd(), noc.getOff_cd(), oldRegno);
                            deleteFromVtOwner(tmgr, noc.getState_cd(), noc.getOff_cd(), oldRegno);
                        }
                    }
                }

                sql = "Select * from " + TableList.VT_OWNER
                        + " WHERE (regn_no,state_cd,off_cd) in "
                        + " (select regn_no,state_cd,off_cd from " + TableList.VA_OWNER + " where appl_no=? and regn_no != 'NEW')";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, status_dobj.getAppl_no());
                rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    throw new VahanException("Vehicle " + status_dobj.getRegn_no() + " details already available in VAHAN.");
                }

                if ("KA".contains(owner_dobj.getState_cd()) && (!"O,C,R".contains(owner_dobj.getRegn_type()) || (owner_dobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_CONFISCATED_AUCTION) && owner_dobj.getAuctionDobj().getRegnNo() != null && owner_dobj.getAuctionDobj().getRegnNo().equals("NEW")))) {
                    sql = "Update va_owner set regn_dt=? where appl_no=? ";
                    if (fitDobj != null) {
                        ps = tmgr.prepareStatement(sql);
                        ps.setDate(1, new java.sql.Date(fitDobj.getFit_chk_dt().getTime()));
                        ps.setString(2, status_dobj.getAppl_no());
                        ps.executeUpdate();
                        if (reg_upto > 0) {
                            regUpto = DateUtils.addToDate(new java.sql.Date(fitDobj.getFit_chk_dt().getTime()), 3, reg_upto);
                            regUpto = DateUtils.addToDate(regUpto, 1, -1);
                            owner_dobj.setRegn_upto(regUpto);
                        }
                    } else if (owner_dobj.getInspectionDobj() != null && owner_dobj.getInspectionDobj().getInsp_dt() != null) {
                        ps = tmgr.prepareStatement(sql);
                        ps.setDate(1, new java.sql.Date(owner_dobj.getInspectionDobj().getInsp_dt().getTime()));
                        ps.setString(2, status_dobj.getAppl_no());
                        ps.executeUpdate();
                        if (reg_upto > 0) {
                            regUpto = DateUtils.addToDate(new java.sql.Date(owner_dobj.getInspectionDobj().getInsp_dt().getTime()), 3, reg_upto);
                            regUpto = DateUtils.addToDate(regUpto, 1, -1);
                            owner_dobj.setRegn_upto(regUpto);
                        }
                        if (fitDobj == null) {
                            int fit_upto = FitnessImplementation.getNewFitnessUpto(owner_dobj, sessionVariables);
                            if (fit_upto > 0) {
                                Date fitUpto = DateUtils.addToDate(owner_dobj.getInspectionDobj().getInsp_dt(), 3, fit_upto);
                                fitUpto = DateUtils.addToDate(fitUpto, 1, -1);
                                owner_dobj.setFit_upto(fitUpto);
                            }
                        }
                    }
                }

                if ((!"KA".contains(owner_dobj.getState_cd()) && owner_dobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_CONFISCATED_AUCTION) && owner_dobj.getAuctionDobj() != null) || ("KA".contains(owner_dobj.getState_cd()) && owner_dobj.getAuctionDobj() != null && owner_dobj.getAuctionDobj().getRegnNo() != null && !owner_dobj.getAuctionDobj().getRegnNo().equals("NEW"))) {
                    if (fitDobj != null) {
                        if (reg_upto > 0) {
                            regUpto = DateUtils.addToDate(new java.sql.Date(fitDobj.getFit_chk_dt().getTime()), 3, reg_upto);
                            regUpto = DateUtils.addToDate(regUpto, 1, -1);
                            owner_dobj.setRegn_upto(regUpto);
                        }
                    } else if (owner_dobj.getInspectionDobj() != null && owner_dobj.getInspectionDobj().getInsp_dt() != null) {
                        if (reg_upto > 0) {
                            regUpto = DateUtils.addToDate(new java.sql.Date(owner_dobj.getInspectionDobj().getInsp_dt().getTime()), 3, reg_upto);
                            regUpto = DateUtils.addToDate(regUpto, 1, -1);
                            owner_dobj.setRegn_upto(regUpto);
                        }
                        if (fitDobj == null) {
                            int fit_upto = FitnessImpl.getNewFitnessUpto(owner_dobj);
                            if (fit_upto > 0) {
                                Date fitUpto = DateUtils.addToDate(owner_dobj.getInspectionDobj().getInsp_dt(), 3, fit_upto);
                                fitUpto = DateUtils.addToDate(fitUpto, 1, -1);
                                owner_dobj.setFit_upto(fitUpto);
                            }
                        }
                    }
                }

                sql = "INSERT into  " + TableList.VT_OWNER
                        + " Select state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name, "
                        + "    f_name, c_add1, c_add2, c_add3, c_district, c_pincode, c_state, "
                        + "    p_add1, p_add2, p_add3, p_district, p_pincode, p_state, owner_cd, "
                        + "    regn_type, vh_class, chasi_no, eng_no, maker, maker_model, body_type,"
                        + "    no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt, "
                        + "    gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, cubic_cap, "
                        + "    floor_area, ac_fitted, audio_fitted, video_fitted, vch_purchase_as, "
                        + "    vch_catg, dealer_cd, sale_amt, laser_code, garage_add, length, "
                        + "    width, height, ?"
                        + ", ? as fit_upto, annual_income, imported_vch, "
                        + "       other_criteria,'Y' as status"
                        + ",current_timestamp as op_dt"
                        + "  FROM " + TableList.VA_OWNER
                        + " where appl_no=? and regn_no != 'NEW'";

                ps = tmgr.prepareStatement(sql);
                ps.setDate(1, new java.sql.Date(owner_dobj.getRegn_upto().getTime()));
                ps.setDate(2, new java.sql.Date(owner_dobj.getFit_upto().getTime()));
                ps.setString(3, owner_dobj.getAppl_no());
                ServerUtility.validateQueryResult(tmgr, ps.executeUpdate(), ps);

                if (userStateCode.equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(owner_dobj.getVh_class()) + ",")) {
                    ownerOtherApproval(tmgr, owner_dobj, empCode);
                }
                if (owner_dobj.getPmt_type() > 0 || owner_dobj.getPmt_catg() > 0 || (owner_dobj.getServicesType() != null && !owner_dobj.getServicesType().equals("") && Integer.parseInt(owner_dobj.getServicesType()) > 0)) {
                    if (changedData.isEmpty()) {
                        ServerUtility.insertIntoVhaTable(tmgr, owner_dobj.getAppl_no(), empCode, "vha_permit_new_regn", "va_permit_new_regn");
                    }
                    ServerUtility.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), "va_permit_new_regn");
                }

                if (owner_dobj.getLaser_code() != null && !owner_dobj.getLaser_code().equals(TableConstants.EMPTY_STRING) && owner_dobj.getLaser_code().equals(TableConstants.HOMOLOGATION_DATA)) {
                    this.insertintoVhaHomologationDetails(tmgr, status_dobj.getAppl_no(), empCode);
                    ServerUtility.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_HOMO_DETAILS);
                }

                if (owner_dobj.getCdDobj() != null) {
                    CdImpl cdImpl = new CdImpl();
                    owner_dobj.getCdDobj().setRegNo(status_dobj.getRegn_no());
                    cdImpl.insertIntoVtCd(owner_dobj.getCdDobj(), tmgr);
                    ServerUtility.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_CD_REGN_DTL);
                }

                if (owner_dobj.getLinkedRegnNo() != null) {
                    sideTrailerApproval(tmgr, owner_dobj, empCode);
                }
                FitnessImplementation fitImpl = new FitnessImplementation();
                if (noc != null) {
                    if (fitDobj != null) {
                        FitnessImplementation.moveFromVtFitnessToVhFitness(status_dobj.getAppl_no(), oldRegno, noc.getState_cd(), noc.getOff_cd(), tmgr, empCode);
                    }
                    FitnessImplementation.moveFromVtSpeedGovToVhSpeedGovTo(appl_no, oldRegno, noc.getState_cd(), noc.getOff_cd(), tmgr, empCode);
                    fitImpl.moveFromVtReflectiveTapeToVhReflectiveTape(oldRegno, noc.getState_cd(), tmgr, empCode);
                }
                FitnessImplementation.moveFromVtFitnessToVhFitness(owner_dobj.getAppl_no(), owner_dobj.getRegn_no(), owner_dobj.getState_cd(), owner_dobj.getOff_cd(), tmgr, empCode);
                FitnessImplementation.moveFromVaFitnessToVTFitness(tmgr, owner_dobj.getAppl_no(), userStateCode);

                FitnessImplementation.moveFromVtSpeedGovToVhSpeedGovTo(appl_no, owner_dobj.getRegn_no(), owner_dobj.getState_cd(), owner_dobj.getOff_cd(), tmgr, empCode);
                FitnessImplementation.moveFromVaSpeedGovToVtSpeedGov(appl_no, tmgr);

                fitImpl.moveFromVtReflectiveTapeToVhReflectiveTape(owner_dobj.getRegn_no(), owner_dobj.getState_cd(), tmgr, empCode);
                fitImpl.moveFromVaReflectiveTapeToVtReflectiveTape(appl_no, owner_dobj.getState_cd(), owner_dobj.getOff_cd(), tmgr);

                ServerUtility.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_FITNESS);
                ServerUtility.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_SPEED_GOVERNOR);
                ServerUtility.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_REFLECTIVE_TAPE);
                insertIntoVhaOwnerWithTimeInterval(tmgr, appl_no, empCode);
                ServerUtility.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_OWNER);
                ServerUtility.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_SIDE_TRAILER);

                if (noc != null) {
                    OwnerIdentificationImplementation.insertIntoOwnerIdentificationHistoryVH(tmgr, oldRegno, noc.getState_cd(), noc.getOff_cd(), empCode);
                    OwnerIdentificationImplementation.deleteFromVtOwnerIdentification(tmgr, oldRegno, noc.getState_cd(), noc.getOff_cd());
                } else {
                    OwnerIdentificationImplementation.insertIntoOwnerIdentificationHistoryVH(tmgr, status_dobj.getRegn_no(), userStateCode, selectedOffCode, empCode);
                    OwnerIdentificationImplementation.deleteFromVtOwnerIdentification(tmgr, status_dobj.getRegn_no(), userStateCode, selectedOffCode);
                }
                OwnerIdentificationImplementation.insertIntoVtFromVaOwnerIdentification(tmgr, status_dobj.getAppl_no(), userStateCode, selectedOffCode);
                ServerUtility.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_OWNER_IDENTIFICATION);

                // Nitin Kumar: For New Permit Loi in Appprovl insert data va_new_reg_loi to vt_new_reg_loi table 
                if (!CommonUtils.isNullOrBlank(appl_no) && tmConfig.isNew_reg_loi() && owner_dobj.getNewLoiNo() != null && pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_NEW) && owner_dobj.getVh_class() == 57) {
                    new PermitImplementation().insertVtNewLoiDetails(owner_dobj.getRegn_no(), owner_dobj.getNewLoiNo(), tmgr, userStateCode, selectedOffCode);
                    ServerUtility.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_NEW_REGN_LOI);
                }

                //for updating the status of application when it is approved start
                status_dobj.setEntry_status(TableConstants.STATUS_APPROVED);
                ServerUtility.updateApprovedStatus(tmgr, status_dobj, clientIpAddress);
                //for updating the status of application when it is approved end

                if (FitnessImplementation.isFitnessFeePaid(status_dobj.getAppl_no())) {
                    sql = "Select * from " + TableList.VA_FC_PRINT + " where appl_no=? and state_cd=? and off_cd=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status_dobj.getAppl_no());
                    ps.setString(2, userStateCode);
                    ps.setInt(3, selectedOffCode);
                    rs = tmgr.fetchDetachedRowSet_No_release();
                    if (rs.next()) {
                        sql = "UPDATE " + TableList.VA_FC_PRINT + " SET regn_no=? Where appl_no=? and state_cd=? and off_cd=?";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, owner_dobj.getRegn_no());
                        ps.setString(2, status_dobj.getAppl_no());
                        ps.setString(3, userStateCode);
                        ps.setInt(4, selectedOffCode);
                        ps.executeUpdate();
                    } else {
                        sql = "Insert into " + TableList.VA_FC_PRINT + " ( state_cd,off_cd,appl_no, regn_no, op_dt)  VALUES (?, ?,?,?,current_timestamp) ";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, userStateCode);
                        ps.setInt(2, selectedOffCode);
                        ps.setString(3, status_dobj.getAppl_no());
                        ps.setString(4, status_dobj.getRegn_no());
                        ps.executeUpdate();
                    }
                }

                if (hpa_list != null) { // for Single Hpa Entry

                    if (noc != null) {
                        HpaImplementation.insertIntoHypthVH(tmgr, status_dobj, noc.getState_cd(), noc.getOff_cd(), oldRegno);
                        HpaImplementation.deleteFromVtHypth(tmgr, oldRegno, noc.getState_cd(), noc.getOff_cd());
                    } else {
                        HpaImplementation.insertIntoHypthVH(tmgr, status_dobj, userStateCode, selectedOffCode, oldRegno);
                        HpaImplementation.deleteFromVtHypth(tmgr, status_dobj.getRegn_no(), userStateCode, selectedOffCode);
                    }
                    HpaImplementation.insertIntoVtHypthFromVaHpa(tmgr, status_dobj);
                    ServerUtility.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_HPA);
                }

                //Delete from vt_insurance
                if (noc != null) {
                    sql = "Insert into " + TableList.VH_INSURANCE + " Select regn_no, comp_cd, ins_type, ins_from, ins_upto,  "
                            + "       policy_no,current_timestamp,?,state_cd, off_cd,idv from " + TableList.VT_INSURANCE
                            + " where regn_no=? and state_cd= ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, empCode);
                    ps.setString(2, noc.getRegn_no());
                    ps.setString(3, noc.getState_cd());
                    ps.executeUpdate();

                    sql = "Delete FROM  " + TableList.VT_INSURANCE
                            + " where regn_no=? and state_cd = ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, oldRegno);
                    ps.setString(2, noc.getState_cd());
                    ps.executeUpdate();
                } else {

                    sql = "Insert into " + TableList.VH_INSURANCE + " Select regn_no, comp_cd, ins_type, ins_from, ins_upto,  "
                            + "       policy_no,current_timestamp,?,state_cd, off_cd,idv from " + TableList.VT_INSURANCE
                            + " where regn_no=? and state_cd= ?";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, empCode);
                    ps.setString(2, status_dobj.getRegn_no());
                    ps.setString(3, userStateCode);
                    ps.executeUpdate();

                    sql = "Delete FROM  " + TableList.VT_INSURANCE
                            + " where regn_no=? and state_cd = ?";

                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, status_dobj.getRegn_no());
                    ps.setString(2, userStateCode);
                    ps.executeUpdate();
                }

                //Inserting into vt_insurance from va_insurance
                sql = "INSERT into " + TableList.VT_INSURANCE + " "
                        + "Select ?,? ,regn_no ,  comp_cd ,  ins_type ,  ins_from ,"
                        + " ins_upto ,  policy_no, idv, current_timestamp as op_dt "
                        + " FROM " + TableList.VA_INSURANCE + " where appl_no=?";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, userStateCode);
                ps.setInt(2, selectedOffCode);
                ps.setString(3, owner_dobj.getAppl_no());
                ps.executeUpdate();

                InsImplementation.insertIntoInsuranceHistoryWithInterval(tmgr, null, owner_dobj.getAppl_no(), sessionVariables.getEmpCodeLoggedIn());
                InsImplementation.deleteFromVaInsurance(tmgr, null, owner_dobj.getAppl_no());

                //Update va_tcc_print for printing TCC 
                sql = "UPDATE " + TableList.VA_TCC_PRINT + " SET"
                        + " regn_no = ? "
                        + " WHERE appl_no = ? and "
                        + " state_cd = ? and off_cd = ?";

                ps = tmgr.prepareStatement(sql);
                ps.setString(1, status_dobj.getRegn_no());
                ps.setString(2, owner_dobj.getAppl_no());
                ps.setString(3, userStateCode);
                ps.setInt(4, selectedOffCode);
                ps.executeUpdate();

                // During Attached Trailer data Approval
                if (trailer_dobj != null && !trailer_dobj.getChasi_no().isEmpty()) {
                    chassis_trailer = trailer_dobj.getChasi_no().trim();
                    TrailerImplementation.validationTrailer(trailer_dobj);
                    Trailer_dobj newdobj = new Trailer_dobj();
                    newdobj = TrailerImplementation.checkTrailerChassis_VTOwnerTrailer(chassis_trailer);
                    if (newdobj != null) {
                        throw new VahanException("Duplicate Trailer Chassis No." + newdobj.getDup_chassis() + " against the registration no " + newdobj.getRegn_no() + " State " + ServerUtility.getStateNameByStateCode(newdobj.getState_cd()) + " and Office " + ServerUtility.getOfficeName(newdobj.getOff_cd(), newdobj.getState_cd()));
                    } else {
                        TrailerImplementation.movedataapprovalTrailer(tmgr, status_dobj.getRegn_no(), owner_dobj.getAppl_no(), empCode, userStateCode, selectedOffCode);
                        TrailerImplementation.deleteFromVaTrailer(tmgr, null, owner_dobj.getAppl_no());
                    }
                }

                // ExArmy Details
                if (exArmyDobj != null) {
                    if (noc != null) {
                        ExArmyImplementation.insertIntoOwnerExArmyVH(tmgr, status_dobj, noc.getState_cd(), noc.getOff_cd(), oldRegno);
                        ExArmyImplementation.deleteFromVtOwnerExArmy(tmgr, oldRegno, noc.getState_cd(), noc.getOff_cd());
                    } else {
                        ExArmyImplementation.insertIntoOwnerExArmyVH(tmgr, status_dobj, userStateCode, selectedOffCode, oldRegno);
                        ExArmyImplementation.deleteFromVtOwnerExArmy(tmgr, status_dobj.getRegn_no(), userStateCode, selectedOffCode);
                    }
                    ExArmyImplementation.insertIntoVtFromVaOwnerExArmy(tmgr, status_dobj, userStateCode, selectedOffCode);
                    ServerUtility.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_OWNER_EX_ARMY);
                } else {
                    ExArmyImplementation.insertIntoVhaExArmy(tmgr, status_dobj.getAppl_no(), empCode);
                    ExArmyImplementation.deleteFromVaExArmy(tmgr, status_dobj.getAppl_no());
                }

                // Imported Vehicle Details
                if (imp_dobj != null) {
                    if (noc != null) {
                        ImportedVehicleImplementation.insertIntoImportedVehVH(tmgr, status_dobj, noc.getState_cd(), noc.getOff_cd(), oldRegno);
                        ImportedVehicleImplementation.deleteFromVtImportedVeh(tmgr, oldRegno, noc.getState_cd(), noc.getOff_cd());
                    } else {
                        ImportedVehicleImplementation.insertIntoImportedVehVH(tmgr, status_dobj, userStateCode, selectedOffCode, oldRegno);
                        ImportedVehicleImplementation.deleteFromVtImportedVeh(tmgr, status_dobj.getRegn_no(), userStateCode, selectedOffCode);
                    }
                    ImportedVehicleImplementation.insertIntoVtFromVaImportedVeh(tmgr, status_dobj, userStateCode, selectedOffCode);
                    ServerUtility.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_IMPORT_VEH);
                } else {
                    ImportedVehicleImplementation.insertIntoVhaImpVeh(tmgr, status_dobj.getAppl_no(), empCode);
                    ImportedVehicleImplementation.deleteFromVaImp(tmgr, status_dobj.getAppl_no());
                }

                //Axle Details
                if (axle_dobj != null) {
                    if (noc != null) {
                        AxleImplementation.insertIntoAxleVH(tmgr, status_dobj, noc.getState_cd(), noc.getOff_cd(), oldRegno);
                        AxleImplementation.deleteFromVtAxle(tmgr, oldRegno, noc.getState_cd(), noc.getOff_cd());
                    } else {
                        AxleImplementation.insertIntoAxleVH(tmgr, status_dobj, userStateCode, selectedOffCode, oldRegno);
                        AxleImplementation.deleteFromVtAxle(tmgr, status_dobj.getRegn_no(), userStateCode, selectedOffCode);
                    }
                    AxleImplementation.insertIntoVtFromVaAxle(tmgr, status_dobj);
                    ServerUtility.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_AXLE);
                } else {
                    AxleImplementation.insertIntoVhaAxle(tmgr, status_dobj.getAppl_no(), empCode);
                    AxleImplementation.deleteFromVaAxle(tmgr, status_dobj.getAppl_no());
                }

                //CNG Vehicle Details
                if (cng_dobj != null) {
                    if (noc != null) {
                        RetroFittingDetailsImplementation.insertIntoRetroFitVH(tmgr, status_dobj, noc.getState_cd(), noc.getOff_cd(), oldRegno);
                        RetroFittingDetailsImplementation.deleteFromVtRetroFit(tmgr, oldRegno, noc.getState_cd(), noc.getOff_cd());
                    } else {
                        RetroFittingDetailsImplementation.insertIntoRetroFitVH(tmgr, status_dobj, userStateCode, selectedOffCode, oldRegno);
                        RetroFittingDetailsImplementation.deleteFromVtRetroFit(tmgr, status_dobj.getRegn_no(), userStateCode, selectedOffCode);
                    }
                    RetroFittingDetailsImplementation.insertIntoVtFromVaRetroFit(tmgr, status_dobj, userStateCode, selectedOffCode);
                    ServerUtility.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_RETROFITTING_DTLS);
                } else {
                    RetroFittingDetailsImplementation.insertIntoVhaCng(tmgr, status_dobj.getAppl_no(), empCode);
                    RetroFittingDetailsImplementation.deleteFromVaRetroFittingDetails(tmgr, status_dobj.getAppl_no());
                }

                //*****************************PUCC Manupulatio Start****************************************
                sql = "INSERT INTO " + TableList.VH_PUCC
                        + " SELECT state_cd, off_cd, regn_no, pucc_from, pucc_upto, pucc_centreno,"
                        + "       pucc_no, op_dt, ? as appl_no, current_timestamp as moved_on, ? as moved_by "
                        + "  FROM " + TableList.VT_PUCC + " WHERE regn_no=? and state_cd = ? and off_cd= ?";

                ps = tmgr.prepareStatement(sql);

                ps.setString(1, status_dobj.getAppl_no());
                ps.setString(2, String.valueOf(status_dobj.getEmp_cd()));
                if (noc != null) {
                    ps.setString(3, oldRegno);
                    ps.setString(4, noc.getState_cd());
                    ps.setInt(5, noc.getOff_cd());
                } else {
                    ps.setString(3, status_dobj.getRegn_no());
                    ps.setString(4, status_dobj.getState_cd());
                    ps.setInt(5, status_dobj.getOff_cd());
                }
                ps.executeUpdate();

                sql = "DELETE FROM " + TableList.VT_PUCC + " where regn_no = ? and state_cd = ? and off_cd = ?";
                ps = tmgr.prepareStatement(sql);

                if (noc != null) {
                    ps.setString(1, oldRegno);
                    ps.setString(2, noc.getState_cd());
                    ps.setInt(3, noc.getOff_cd());
                } else {
                    ps.setString(1, status_dobj.getRegn_no());
                    ps.setString(2, userStateCode);
                    ps.setInt(3, selectedOffCode);
                }
                ps.executeUpdate();

                sql = "INSERT INTO " + TableList.VT_PUCC
                        + " SELECT ? as state_cd,? as off_cd,regn_no, pucc_from, pucc_upto, pucc_centreno, pucc_no, op_dt "
                        + "  FROM " + TableList.VA_PUCC + " WHERE appl_no=?";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, userStateCode);
                ps.setInt(2, selectedOffCode);
                ps.setString(3, status_dobj.getAppl_no());
                ps.executeUpdate();

                ServerUtility.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_PUCC);

                //*****************************PUCC Manupulatio End****************************************
                if (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                        || owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                    OtherStateVehImplementation impl = new OtherStateVehImplementation();
                    owner_dobj.getOtherStateVehDobj().setNewRegnNo(status_dobj.getRegn_no());
                    if (noc != null) {
                        impl.insertIntoOtherStateVehHistoryVH(tmgr, oldRegno, appl_no, noc.getState_cd(), noc.getOff_cd(), empCode);
                        impl.deleteFromVtOtherStateVeh(tmgr, oldRegno, noc.getState_cd(), noc.getOff_cd());
                    }

                    /*
                     *Petrol Vehicle registered at other state on 1st Jan 2013
                     *OTT for 15 years paid at other state and  tax validity is 31st Dec 2027. Entry date  in Manipur : 14-Nov-2017
                     *Vehicle is more than 2 years old. Hence no tax will be charged till 31st Dec 2027 Hence, in Manipur
                     *Road Tax Amount = 0, Tax validity = 31st Dec 2027.
                     * 
                     * Diesel Vehicle registered at other state on 1st Jan 2013
                     * OTT for 10 years paid at other state and  tax validity is 31st Dec 2022. Entry date  in Manipur : 14-Nov-2017
                     * Vehicle is more than 2 years old. Hence no tax will be charged till 31st Dec 2022
                     * Hence, in Manipur Road Tax Amount = 0 Tax validity = 31st Dec 2022.
                     */
                    if ("MN".equalsIgnoreCase(userStateCode)) {
                        List<EpayDobj> listEpay = EpayImplementation.getFeePaidDetails(appl_no).getList();
                        EpayDobj ep = new EpayDobj();
                        ep.setPurCd(1);
                        int index = listEpay.lastIndexOf(ep);
                        if (index >= 0) {
                            ep = listEpay.get(index);
                            int vehicleAge = DateUtils.getDate1MinusDate2_Months(owner_dobj.getPurchase_dt(), ep.getRcptDt());
                            if (vehicleAge >= 24) {
                                impl.insertIntoOtherStateVehTaxHistoryVH(tmgr, appl_no, regn_no, owner_dobj.getRegn_upto(), owner_dobj.getOtherStateVehDobj(), userStateCode, selectedOffCode, userId, empCode);
                            }
                        }
                    }

                    impl.insertIntoVtOtherStateVeh(tmgr, owner_dobj.getOtherStateVehDobj());
                    ServerUtility.deleteFromTable(tmgr, null, status_dobj.getAppl_no(), TableList.VA_OTHER_STATE_VEH);
                }

                //SmartCard Or Print
                boolean blnSmart = true;

                if (owner_dobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                    boolean isSmartCard = ServerUtility.verifyForSmartCard(userStateCode, selectedOffCode, tmgr);
                    if (isSmartCard) {
                        blnSmart = (SmartCardImplementation.getSmartCardFeePaid(appl_no, tmgr));

                    } else {
                        sql = "SELECT fees from get_appl_rcpt_details(?) where pur_cd in (1,4,5)";
                        ps = tmgr.prepareStatement(sql);
                        ps.setString(1, status_dobj.getAppl_no());
                        rs = tmgr.fetchDetachedRowSet_No_release();
                        if (!rs.next()) {
                            blnSmart = false;
                        }
                    }
                }

                if (blnSmart) {
                    //HSRP

                    boolean isOldHsrp = ServerUtility.verifyForOldVehicleHsrp(userStateCode, selectedOffCode, tmgr);
                    if (isRegnNoGenerateForOtherStateVehicles(tmgr, appl_no, owner_dobj.getRegn_type())
                            || isRegnNoGenerateForOtherDistrict(tmgr, appl_no, owner_dobj.getRegn_type()) || isOldHsrp) {
                        ServerUtility.verifyInsertNewRegHsrpDetail(owner_dobj.getAppl_no(), owner_dobj.getRegn_no(), TableConstants.HSRP_NEW_BOTH_SIDE,
                                userStateCode, selectedOffCode, tmgr, empCode);
                    }

                    ServerUtility.VerifyInsertSmartCardPrintDetail(owner_dobj.getAppl_no(), owner_dobj.getRegn_no(),
                            userStateCode, selectedOffCode, status_dobj.getPur_cd(), tmgr, empCode, userCategory, tmConfig, sessionVariables);
                }

                if (noc != null) {
                    sql = "insert into " + TableList.VH_TMP_REGN_DTL + " select state_cd, off_cd,regn_no, tmp_off_cd, regn_auth, tmp_state_cd, "
                            + "tmp_regn_no, tmp_regn_dt, tmp_valid_upto, dealer_cd,?,current_timestamp,? from " + TableList.VT_TMP_REGN_DTL
                            + " where regn_no=? and state_cd= ? and off_cd= ? ";
                    ps = tmgr.prepareStatement(sql);

                    ps.setString(1, owner_dobj.getAppl_no());
                    ps.setString(2, empCode);
                    ps.setString(3, noc.getRegn_no());
                    ps.setString(4, noc.getState_cd());
                    ps.setInt(5, noc.getOff_cd());
                    ps.executeUpdate();

                    sql = "Delete from " + TableList.VT_TMP_REGN_DTL + " where regn_no=? and state_cd= ? and off_cd= ?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, noc.getRegn_no());
                    ps.setString(2, noc.getState_cd());
                    ps.setInt(3, noc.getOff_cd());
                    ps.executeUpdate();
                }

                if (owner_dobj.getTempReg() != null) {

                    sql = "insert into " + TableList.VT_TMP_REGN_DTL
                            + " (state_cd , off_cd ,regn_no, tmp_off_cd, regn_auth, tmp_state_cd, tmp_regn_no,"
                            + " tmp_regn_dt,tmp_valid_upto, dealer_cd, op_dt )"
                            + " values(?,?,?, ?, ?, ?, ?, ?,?, ? , current_timestamp)";
                    ps = tmgr.prepareStatement(sql);
                    int i = 1;
                    ps.setString(i++, userStateCode);
                    ps.setInt(i++, selectedOffCode);
                    ps.setString(i++, owner_dobj.getRegn_no());
                    ps.setInt(i++, owner_dobj.getTempReg().getTmp_off_cd());
                    ps.setString(i++, owner_dobj.getTempReg().getRegn_auth());
                    ps.setString(i++, owner_dobj.getTempReg().getTmp_state_cd());
                    ps.setString(i++, owner_dobj.getTempReg().getTmp_regn_no());
                    ps.setDate(i++, new java.sql.Date(owner_dobj.getTempReg().getTmp_regn_dt().getTime()));
                    ps.setDate(i++, new java.sql.Date(owner_dobj.getTempReg().getTmp_valid_upto().getTime()));
                    ps.setString(i++, owner_dobj.getTempReg().getDealer_cd());
                    ps.executeUpdate();

                    sql = "delete from " + TableList.VA_TMP_REGN_DTL
                            + " where appl_no=?";
                    ps = tmgr.prepareStatement(sql);
                    ps.setString(1, appl_no);
                    ps.executeUpdate();

                    InsImplementation.insert_ins_dtls_to_Vh_insurance(tmgr, owner_dobj.getTempReg().getTmp_regn_no(), userStateCode, empCode);
                }

                ScrappedVehicleImplementation scrapImpl = new ScrappedVehicleImplementation();
                scrapImpl.updateNewChaisNoForScrappedVeh(owner_dobj.getChasi_no(), appl_no, tmgr);

                sql = "select a.* from vt_tax a, vp_appl_rcpt_mapping  b where  a.rcpt_no=b.rcpt_no and a.state_cd=b.state_cd and "
                        + "a.off_cd=b.off_cd and b.appl_no=? and  a.tax_mode <> 'B'";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, appl_no);
                rs = tmgr.fetchDetachedRowSet_No_release();
                while (rs.next()) {
                    String regno = rs.getString("regn_no");
                    Date taxUpto = rs.getDate("tax_upto");
                    int purCd = rs.getInt("pur_cd");
                    TaxServerImplementation.insertUpdateTaxDefaulter(regno, taxUpto, purCd, tmgr, userStateCode);
                }
            }

            //for saving the data into table those are changed by the user
            //ServerUtil.insertIntoVhaChangedData(tmgr, owner_dobj.getAppl_no(), changedData);
            if (changedData != null && !changedData.equals(TableConstants.EMPTY_STRING)) {
                sql = "INSERT INTO VHA_CHANGED_DATA (APPL_NO,EMP_CD,CHANGED_DATA,OP_DT,STATE_CD,OFF_CD) "
                        + " VALUES(?,?,?,CURRENT_TIMESTAMP,?,?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, owner_dobj.getAppl_no());
                ps.setLong(2, Long.parseLong(empCode));
                ps.setString(3, changedData);
                ps.setString(4, userStateCode);
                ps.setInt(5, selectedOffCode);
                ps.executeUpdate();
            }

            //   ServerUtility.fileFlow(tmgr, status_dobj,actionCode,selectedRoleCode,userStateCode,offCode,empCode,clientIpAddress,allowFacelessService,defacement); // for updateing va_status and vha status for new role,seat for new emp
            ServerUtility.fileFlow(tmgr, status_dobj, sessionVariables.getActionCodeSelected(),
                    selectedRoleCode, userStateCode, selectedOffCode, empCode, userCategory, clientIpAddress,
                    tmConfig.isAllowFacelessService(), tmConfig.isDefacement()); // for updateing va_status and vha status for new role,seat for new emp

            tmgr.commit();
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("New Registration : Error in Database Update");
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Update Error in New Registration Application");
        } finally {
            if (tmgr != null) {
                tmgr.release();
            }
        }
        return regn_no;
    }//end of update_New_Status method

    public static void insertOrUpdateVaOwner(TransactionManager tmgr, Owner_dobj owner_dobj) throws VahanException, SQLException {

        PreparedStatement ps = tmgr.prepareStatement("Select * from " + TableList.VA_OWNER
                + " where appl_no=?");
        ps.setString(1, owner_dobj.getAppl_no());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();

        if (rs.next()) {
            insertIntoVhaOwner(tmgr, owner_dobj.getAppl_no());
            updateVaOwner(tmgr, owner_dobj);
        } else {
            insertVaOwner(tmgr, owner_dobj);
        }
        if (owner_dobj.getPmt_type() > 0 || owner_dobj.getPmt_catg() > 0 || (owner_dobj.getServicesType() != null && !owner_dobj.getServicesType().equals("") && Integer.parseInt(owner_dobj.getServicesType()) > 0)) {
            NewImplementation.insertOrUpdateVaPermitNewRegn(tmgr, owner_dobj);
        }

    }

    /**
     * @author Kartikey SIngh
     */
    public static void insertOrUpdateVaOwner(TransactionManager tmgr, Owner_dobj owner_dobj, String empCode,
            int purCd, int offCode, String userStateCode, int actionCode) throws VahanException, SQLException {

        PreparedStatement ps = tmgr.prepareStatement("Select * from " + TableList.VA_OWNER
                + " where appl_no=?");
        ps.setString(1, owner_dobj.getAppl_no());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();

        if (rs.next()) {
            insertIntoVhaOwner(tmgr, owner_dobj.getAppl_no(), empCode);
            updateVaOwner(tmgr, owner_dobj, purCd, offCode, userStateCode);
        } else {
            insertVaOwner(tmgr, owner_dobj, actionCode, userStateCode, offCode);
        }
        if (owner_dobj.getPmt_type() > 0 || owner_dobj.getPmt_catg() > 0 || (owner_dobj.getServicesType() != null && !owner_dobj.getServicesType().equals("") && Integer.parseInt(owner_dobj.getServicesType()) > 0)) {
            NewImplementation.insertOrUpdateVaPermitNewRegn(tmgr, owner_dobj, empCode);
        }

    }

    public static boolean isRegnNoGenerateForOtherStateVehicles(TransactionManager tmgr, String applNo, String regnType) throws VahanException {
        boolean isGenerate = true;
        if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)) {
            isGenerate = false;
            String sql = "select * from get_appl_rcpt_details(?) where pur_cd in (1,17)";
            try {
                PreparedStatement ps = tmgr.prepareStatement(sql);
                ps.setString(1, applNo);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    isGenerate = true;
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                throw new VahanException("Other State Vehicle Registration No Generation Problem.");
            }
        }
        return isGenerate;

    }

    public static boolean isRegnNoGenerateForAuctionVehicles(TransactionManager tmgr, String applNo) throws VahanException {
        boolean isGenerate = false;
        String sql = "select * from get_appl_rcpt_details(?) where pur_cd in (1,17)";
        try {
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                isGenerate = true;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Auction Vehicle Registration No Generation Problem.");
        }

        return isGenerate;
    }

    public static boolean isRegnNoGenerateForOtherDistrict(TransactionManager tmgr, String applNo, String regnType) throws VahanException {
        boolean isGenerate = true;
        if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
            isGenerate = false;
            String sql = "select * from get_appl_rcpt_details(?) where pur_cd in (1,17)";
            try {
                PreparedStatement ps = tmgr.prepareStatement(sql);
                ps.setString(1, applNo);
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    isGenerate = true;
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                throw new VahanException("Other State Vehicle Registration No Generation Problem.");
            }
        }
        return isGenerate;

    }

    public static boolean isRegnNoNewVeh(TransactionManager tmgr, String applNo) throws VahanException {
        boolean isGenerate = false;
        String sql = "select * from get_appl_rcpt_details(?) where pur_cd in (1,17)";
        try {
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                isGenerate = true;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Other State Vehicle Registration No Generation Problem.");
        }
        return isGenerate;
    }

    public static void insertIntoVhaOwner(TransactionManager tmgr, String appl_no) throws VahanException {
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
                    + " where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            ps.executeUpdate();

            insertIntoVhaOwnerId(tmgr, appl_no);
            insertIntoVhaOwnerTemp(tmgr, appl_no);
            insertIntoTempRegnDetails(tmgr, appl_no);
            insertIntoVhaSideTrailer(tmgr, appl_no);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }//end of insertIntoVhaOwner

    /**
     * @author Kartikey Singh
     */
    public static void insertIntoVhaOwner(TransactionManager tmgr, String appl_no, String empCode) throws VahanException {
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
                    + " where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, appl_no);
            ps.executeUpdate();

            insertIntoVhaOwnerId(tmgr, appl_no, empCode);
            insertIntoVhaOwnerTemp(tmgr, appl_no, empCode);
            insertIntoTempRegnDetails(tmgr, appl_no, empCode);
            insertIntoVhaSideTrailer(tmgr, appl_no, empCode);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }//end of insertIntoVhaOwner

    public static void insertIntoVhaOwnerWithTimeInterval(TransactionManager tmgr, String appl_no) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;

        try {

            sql = "INSERT INTO  " + TableList.VHA_OWNER
                    + " SELECT current_timestamp + interval '1 second' as moved_on , ? as moved_by,"
                    + "        state_cd, off_cd, appl_no, regn_no, regn_dt, purchase_dt, owner_sr, "
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
                    + " where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, appl_no);
            ps.executeUpdate();

            insertIntoVhaOwnerIdWithTimeInterval(tmgr, appl_no);
            insertIntoVhaOwnerTempWithTimeInterval(tmgr, appl_no);
            insertIntoTempRegnDetailsWithTimeInterval(tmgr, appl_no);
            insertIntoVhaSideTrailerWithTimeInterval(tmgr, appl_no);
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    /*
     * @author Kartikey Singh
     */
    public static void insertIntoVhaOwnerWithTimeInterval(TransactionManager tmgr, String appl_no, String empCode) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;

        try {

            sql = "INSERT INTO  " + TableList.VHA_OWNER
                    + " SELECT current_timestamp + interval '1 second' as moved_on , ? as moved_by,"
                    + "        state_cd, off_cd, appl_no, regn_no, regn_dt, purchase_dt, owner_sr, "
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
                    + " where appl_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, appl_no);
            ps.executeUpdate();

            insertIntoVhaOwnerIdWithTimeInterval(tmgr, appl_no, empCode);
            insertIntoVhaOwnerTempWithTimeInterval(tmgr, appl_no, empCode);
            insertIntoTempRegnDetailsWithTimeInterval(tmgr, appl_no, empCode);
            insertIntoVhaSideTrailerWithTimeInterval(tmgr, appl_no, empCode);
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public static void insertIntoVhaOwnerId(TransactionManager tmgr, String appl_no) throws SQLException {

        PreparedStatement ps = null;

        String sql = "INSERT INTO  " + TableList.VHA_OWNER_IDENTIFICATION
                + "  SELECT current_timestamp as moved_on, ? moved_by,state_cd,off_cd,appl_no, regn_no, mobile_no, email_id, pan_no, aadhar_no, passport_no, "
                + "  ration_card_no, voter_id, dl_no, verified_on, owner_ctg,op_dt,dept_cd "
                + "  FROM  " + TableList.VA_OWNER_IDENTIFICATION
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();

    }

    /**
     * @author Kartikey Singh
     */
    public static void insertIntoVhaOwnerId(TransactionManager tmgr, String appl_no, String empCode) throws SQLException {

        PreparedStatement ps = null;

        String sql = "INSERT INTO  " + TableList.VHA_OWNER_IDENTIFICATION
                + "  SELECT current_timestamp as moved_on, ? moved_by,state_cd,off_cd,appl_no, regn_no, mobile_no, email_id, pan_no, aadhar_no, passport_no, "
                + "  ration_card_no, voter_id, dl_no, verified_on, owner_ctg,op_dt,dept_cd "
                + "  FROM  " + TableList.VA_OWNER_IDENTIFICATION
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, empCode);
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public static void insertIntoVhaOwnerIdWithTimeInterval(TransactionManager tmgr, String appl_no) throws SQLException {

        PreparedStatement ps = null;

        String sql = "INSERT INTO  " + TableList.VHA_OWNER_IDENTIFICATION
                + "  SELECT current_timestamp + interval '1 second' as moved_on ,  ? as moved_by,state_cd,off_cd,appl_no, regn_no, mobile_no, email_id, pan_no, aadhar_no, passport_no, "
                + "  ration_card_no, voter_id, dl_no, verified_on, owner_ctg, op_dt,dept_cd "
                + "  FROM  " + TableList.VA_OWNER_IDENTIFICATION
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    /*
     * @author Kartikey Singh
     */
    public static void insertIntoVhaOwnerIdWithTimeInterval(TransactionManager tmgr, String appl_no, String empCode) throws SQLException {

        PreparedStatement ps = null;

        String sql = "INSERT INTO  " + TableList.VHA_OWNER_IDENTIFICATION
                + "  SELECT current_timestamp + interval '1 second' as moved_on ,  ? as moved_by,state_cd,off_cd,appl_no, regn_no, mobile_no, email_id, pan_no, aadhar_no, passport_no, "
                + "  ration_card_no, voter_id, dl_no, verified_on, owner_ctg, op_dt,dept_cd "
                + "  FROM  " + TableList.VA_OWNER_IDENTIFICATION
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, empCode);
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public static void insertIntoTempRegnDetails(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps = null;

        String sql = "INSERT INTO  " + TableList.VHA_TMP_REGN_DTL
                + "  SELECT current_timestamp as moved_on ,? as moved_by,state_cd,off_cd,appl_no, regn_no, tmp_off_cd, regn_auth, tmp_state_cd, tmp_regn_no, "
                + "       tmp_regn_dt, tmp_valid_upto, dealer_cd,  op_dt"
                + "  FROM  " + TableList.VA_TMP_REGN_DTL
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();

    }

    /**
     * @author Kartikey Singh
     */
    public static void insertIntoTempRegnDetails(TransactionManager tmgr, String appl_no, String empCode) throws SQLException {
        PreparedStatement ps = null;

        String sql = "INSERT INTO  " + TableList.VHA_TMP_REGN_DTL
                + "  SELECT current_timestamp as moved_on ,? as moved_by,state_cd,off_cd,appl_no, regn_no, tmp_off_cd, regn_auth, tmp_state_cd, tmp_regn_no, "
                + "       tmp_regn_dt, tmp_valid_upto, dealer_cd,  op_dt"
                + "  FROM  " + TableList.VA_TMP_REGN_DTL
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, empCode);
        ps.setString(2, appl_no);
        ps.executeUpdate();

    }

    public static void insertIntoTempRegnDetailsWithTimeInterval(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps = null;

        String sql = "INSERT INTO  " + TableList.VHA_TMP_REGN_DTL
                + "  SELECT current_timestamp + interval '1 second' as moved_on ,? as moved_by,state_cd,off_cd,appl_no, regn_no, tmp_off_cd, regn_auth, tmp_state_cd, tmp_regn_no, "
                + "       tmp_regn_dt, tmp_valid_upto, dealer_cd,op_dt"
                + "  FROM  " + TableList.VA_TMP_REGN_DTL
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    /*
     * @author Kartikey Singh
     */
    public static void insertIntoTempRegnDetailsWithTimeInterval(TransactionManager tmgr, String appl_no, String empCode) throws SQLException {
        PreparedStatement ps = null;

        String sql = "INSERT INTO  " + TableList.VHA_TMP_REGN_DTL
                + "  SELECT current_timestamp + interval '1 second' as moved_on ,? as moved_by,state_cd,off_cd,appl_no, regn_no, tmp_off_cd, regn_auth, tmp_state_cd, tmp_regn_no, "
                + "       tmp_regn_dt, tmp_valid_upto, dealer_cd,op_dt"
                + "  FROM  " + TableList.VA_TMP_REGN_DTL
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, empCode);
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public static void insertIntoVhaOwnerTemp(TransactionManager tmgr, String appl_no) throws SQLException {

        PreparedStatement ps = null;

        String sql = "INSERT INTO  vha_owner_temp"
                + "  SELECT current_timestamp as moved_on, ? as moved_by, state_cd, off_cd,appl_no, temp_regn_no, state_cd_to, off_cd_to,"
                + "  purpose,body_building,op_dt,temp_regn_type"
                + "  FROM  va_owner_temp"
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();

    }

    /*
     * @author Kartikey Singh
     */
    public static void insertIntoVhaOwnerTemp(TransactionManager tmgr, String appl_no, String empCode) throws SQLException {

        PreparedStatement ps = null;

        String sql = "INSERT INTO  vha_owner_temp"
                + "  SELECT current_timestamp as moved_on, ? as moved_by, state_cd, off_cd,appl_no, temp_regn_no, state_cd_to, off_cd_to,"
                + "  purpose,body_building,op_dt,temp_regn_type"
                + "  FROM  va_owner_temp"
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, empCode);
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public static void insertIntoVhaOwnerTempWithTimeInterval(TransactionManager tmgr, String appl_no) throws SQLException {

        PreparedStatement ps = null;

        String sql = "INSERT INTO  vha_owner_temp"
                + "  SELECT current_timestamp + interval '1 second' as moved_on , ? as moved_by,state_cd, off_cd,appl_no, temp_regn_no, state_cd_to, off_cd_to,"
                + "  purpose,body_building,op_dt,temp_regn_type"
                + "  FROM  va_owner_temp"
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    /*
     * @author Kartikey Singh
     */
    public static void insertIntoVhaOwnerTempWithTimeInterval(TransactionManager tmgr, String appl_no, String empCode) throws SQLException {

        PreparedStatement ps = null;

        String sql = "INSERT INTO  vha_owner_temp"
                + "  SELECT current_timestamp + interval '1 second' as moved_on , ? as moved_by,state_cd, off_cd,appl_no, temp_regn_no, state_cd_to, off_cd_to,"
                + "  purpose,body_building,op_dt,temp_regn_type"
                + "  FROM  va_owner_temp"
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, empCode);
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public static void updateVaOwner(TransactionManager tmgr, Owner_dobj owner_dobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        String rqrdTaxModesVar = " , rqrd_tax_modes = ?";
        boolean flgOtherState = false;

        try {

            if ((owner_dobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                    || owner_dobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))
                    && owner_dobj.getRegn_dt() != null) {
                flgOtherState = true;
            }

            /**
             * For other state vehicle/other district Regn Dt will previous regn
             * date
             */
            if (flgOtherState) {
                sql = "UPDATE " + TableList.VA_OWNER
                        + "   SET state_cd=?, off_cd=?, appl_no=?,"
                        + "       regn_dt=?,"
                        + "  purchase_dt=?, "
                        + "       owner_sr=?, owner_name=?, f_name=?, c_add1=?, c_add2=?, c_add3=?, "
                        + "       c_district=?, c_pincode=?, c_state=?, p_add1=?, p_add2=?, p_add3=?, "
                        + "       p_district=?, p_pincode=?, p_state=?, owner_cd=?, regn_type=?, "
                        + "       vh_class=?, chasi_no=?, eng_no=?, maker=?, maker_model=?, body_type=?, "
                        + "       no_cyl=?, hp=?, seat_cap=?, stand_cap=?, sleeper_cap=?, unld_wt=?, "
                        + "       ld_wt=?, gcw=?, fuel=?, color=?, manu_mon=?, manu_yr=?, norms=?, "
                        + "       wheelbase=?, cubic_cap=?, floor_area=?, ac_fitted=?, audio_fitted=?, "
                        + "       video_fitted=?, vch_purchase_as=?, vch_catg=?, dealer_cd=?, sale_amt=?, "
                        + "       laser_code=?, garage_add=?, length=?, width=?, height=?, regn_upto=?, "
                        + "       fit_upto=?, annual_income=?, imported_vch=?, other_criteria=?,"
                        + "       pmt_type=?,pmt_catg=? ,rqrd_tax_modes=?,op_dt=current_timestamp"
                        + " WHERE appl_no=?";

                ps = tmgr.prepareStatement(sql);
                int i = 1;
                ps.setString(i++, owner_dobj.getState_cd());
                ps.setInt(i++, owner_dobj.getOff_cd());
                ps.setString(i++, owner_dobj.getAppl_no());
                ps.setDate(i++, new java.sql.Date(owner_dobj.getRegn_dt().getTime()));

                ps.setDate(i++, owner_dobj.getPurchase_dt() == null ? null : new java.sql.Date(owner_dobj.getPurchase_dt().getTime()));
                ps.setInt(i++, owner_dobj.getOwner_sr());
                ps.setString(i++, owner_dobj.getOwner_name());
                ps.setString(i++, owner_dobj.getF_name());
                ps.setString(i++, owner_dobj.getC_add1());
                ps.setString(i++, owner_dobj.getC_add2());
                ps.setString(i++, owner_dobj.getC_add3());
                ps.setInt(i++, owner_dobj.getC_district());
                ps.setInt(i++, owner_dobj.getC_pincode());
                ps.setString(i++, owner_dobj.getC_state());
                ps.setString(i++, owner_dobj.getP_add1());
                ps.setString(i++, owner_dobj.getP_add2());
                ps.setString(i++, owner_dobj.getP_add3());
                ps.setInt(i++, owner_dobj.getP_district());
                ps.setInt(i++, owner_dobj.getP_pincode());
                ps.setString(i++, owner_dobj.getP_state());
                ps.setInt(i++, owner_dobj.getOwner_cd());
                ps.setString(i++, owner_dobj.getRegn_type());
                ps.setInt(i++, owner_dobj.getVh_class());
                ps.setString(i++, owner_dobj.getChasi_no());
                ps.setString(i++, owner_dobj.getEng_no());
                ps.setInt(i++, owner_dobj.getMaker());
                ps.setString(i++, owner_dobj.getMaker_model());
                ps.setString(i++, owner_dobj.getBody_type());
                ps.setInt(i++, owner_dobj.getNo_cyl());
                ps.setFloat(i++, owner_dobj.getHp());
                ps.setInt(i++, owner_dobj.getSeat_cap());
                ps.setInt(i++, owner_dobj.getStand_cap());
                ps.setInt(i++, owner_dobj.getSleeper_cap());
                ps.setInt(i++, owner_dobj.getUnld_wt());
                ps.setInt(i++, owner_dobj.getLd_wt());
                ps.setInt(i++, owner_dobj.getGcw());
                ps.setInt(i++, owner_dobj.getFuel());
                ps.setString(i++, owner_dobj.getColor());
                ps.setInt(i++, owner_dobj.getManu_mon());
                ps.setInt(i++, owner_dobj.getManu_yr());
                ps.setInt(i++, owner_dobj.getNorms());
                ps.setInt(i++, owner_dobj.getWheelbase());
                ps.setFloat(i++, owner_dobj.getCubic_cap());
                ps.setFloat(i++, owner_dobj.getFloor_area());
                ps.setString(i++, owner_dobj.getAc_fitted());
                ps.setString(i++, owner_dobj.getAudio_fitted());
                ps.setString(i++, owner_dobj.getVideo_fitted());
                ps.setString(i++, owner_dobj.getVch_purchase_as());
                ps.setString(i++, owner_dobj.getVch_catg());
                ps.setString(i++, owner_dobj.getDealer_cd());
                ps.setInt(i++, owner_dobj.getSale_amt());
                ps.setString(i++, owner_dobj.getLaser_code());
                ps.setString(i++, owner_dobj.getGarage_add());
                ps.setInt(i++, owner_dobj.getLength());
                ps.setInt(i++, owner_dobj.getWidth());
                ps.setInt(i++, owner_dobj.getHeight());
                ps.setDate(i++, owner_dobj.getRegn_upto() == null ? null : new java.sql.Date(owner_dobj.getRegn_upto().getTime()));
                ps.setDate(i++, owner_dobj.getFit_upto() == null ? null : new java.sql.Date(owner_dobj.getFit_upto().getTime()));
                ps.setInt(i++, owner_dobj.getAnnual_income());
                ps.setString(i++, owner_dobj.getImported_vch() == null ? "N" : owner_dobj.getImported_vch());
                ps.setInt(i++, owner_dobj.getOther_criteria());
                ps.setInt(i++, owner_dobj.getPmt_type());
                ps.setInt(i++, owner_dobj.getPmt_catg());
                ps.setString(i++, owner_dobj.getRqrd_tax_modes());
                ps.setString(i++, owner_dobj.getAppl_no());
                ps.executeUpdate();

            } else {
                if (Util.getSelectedSeat() != null && Util.getSelectedSeat().getPur_cd() == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                    int newApplOffCd = ServerUtil.getOfficeCdForDealerTempAppl(owner_dobj.getAppl_no(), owner_dobj.getState_cd(), "taxMode");
                    if (newApplOffCd != 0) {
                        rqrdTaxModesVar = "";
                    }
                }
                sql = "UPDATE " + TableList.VA_OWNER
                        + "   SET appl_no=?,"
                        //                        + "       regn_dt=?,"
                        + "  purchase_dt=?, "
                        + "       owner_sr=?, owner_name=?, f_name=?, c_add1=?, c_add2=?, c_add3=?, "
                        + "       c_district=?, c_pincode=?, c_state=?, p_add1=?, p_add2=?, p_add3=?, "
                        + "       p_district=?, p_pincode=?, p_state=?, owner_cd=?, regn_type=?, "
                        + "       vh_class=?, chasi_no=?, eng_no=?, maker=?, maker_model=?, body_type=?, "
                        + "       no_cyl=?, hp=?, seat_cap=?, stand_cap=?, sleeper_cap=?, unld_wt=?, "
                        + "       ld_wt=?, gcw=?, fuel=?, color=?, manu_mon=?, manu_yr=?, norms=?, "
                        + "       wheelbase=?, cubic_cap=?, floor_area=?, ac_fitted=?, audio_fitted=?, "
                        + "       video_fitted=?, vch_purchase_as=?, vch_catg=?, dealer_cd=?, sale_amt=?, "
                        + "       laser_code=?, garage_add=?, length=?, width=?, height=?, regn_upto=?, "
                        + "       fit_upto=?, annual_income=?, imported_vch=?, other_criteria=?,"
                        + "       pmt_type=?,pmt_catg=? " + rqrdTaxModesVar + " ,op_dt=current_timestamp"
                        + " WHERE appl_no=?";

                ps = tmgr.prepareStatement(sql);
                int i = 1;
                ps.setString(i++, owner_dobj.getAppl_no());
                // ps.setDate(i++, new java.sql.Date(owner_dobj.getRegn_dt().getTime()));
                ps.setDate(i++, owner_dobj.getPurchase_dt() == null ? null : new java.sql.Date(owner_dobj.getPurchase_dt().getTime()));
                ps.setInt(i++, owner_dobj.getOwner_sr());
                ps.setString(i++, owner_dobj.getOwner_name());
                ps.setString(i++, owner_dobj.getF_name());
                ps.setString(i++, owner_dobj.getC_add1());
                ps.setString(i++, owner_dobj.getC_add2());
                ps.setString(i++, owner_dobj.getC_add3());
                ps.setInt(i++, owner_dobj.getC_district());
                ps.setInt(i++, owner_dobj.getC_pincode());
                ps.setString(i++, owner_dobj.getC_state());
                ps.setString(i++, owner_dobj.getP_add1());
                ps.setString(i++, owner_dobj.getP_add2());
                ps.setString(i++, owner_dobj.getP_add3());
                ps.setInt(i++, owner_dobj.getP_district());
                ps.setInt(i++, owner_dobj.getP_pincode());
                ps.setString(i++, owner_dobj.getP_state());
                ps.setInt(i++, owner_dobj.getOwner_cd());
                if (CommonUtils.isNullOrBlank(owner_dobj.getRegn_type()) || "0".equals(owner_dobj.getRegn_type())) {
                    throw new VahanException("Registration Type can not be blank.");
                }
                ps.setString(i++, owner_dobj.getRegn_type());
                ps.setInt(i++, owner_dobj.getVh_class());
                ps.setString(i++, owner_dobj.getChasi_no());
                ps.setString(i++, owner_dobj.getEng_no());
                ps.setInt(i++, owner_dobj.getMaker());
                ps.setString(i++, owner_dobj.getMaker_model());
                ps.setString(i++, owner_dobj.getBody_type());
                ps.setInt(i++, owner_dobj.getNo_cyl());
                ps.setFloat(i++, owner_dobj.getHp());
                ps.setInt(i++, owner_dobj.getSeat_cap());
                ps.setInt(i++, owner_dobj.getStand_cap());
                ps.setInt(i++, owner_dobj.getSleeper_cap());
                ps.setInt(i++, owner_dobj.getUnld_wt());
                ps.setInt(i++, owner_dobj.getLd_wt());
                ps.setInt(i++, owner_dobj.getGcw());
                ps.setInt(i++, owner_dobj.getFuel());
                ps.setString(i++, owner_dobj.getColor());
                if (owner_dobj.getManu_mon() != null) {
                    ps.setInt(i++, owner_dobj.getManu_mon());
                } else {
                    ps.setInt(i++, 0);
                }
                if (owner_dobj.getManu_yr() != null) {
                    ps.setInt(i++, owner_dobj.getManu_yr());
                } else {
                    ps.setInt(i++, 0);
                }
                ps.setInt(i++, owner_dobj.getNorms());
                ps.setInt(i++, owner_dobj.getWheelbase());
                ps.setFloat(i++, owner_dobj.getCubic_cap());
                ps.setFloat(i++, owner_dobj.getFloor_area());
                ps.setString(i++, owner_dobj.getAc_fitted());
                ps.setString(i++, owner_dobj.getAudio_fitted());
                ps.setString(i++, owner_dobj.getVideo_fitted());
                ps.setString(i++, owner_dobj.getVch_purchase_as());
                ps.setString(i++, owner_dobj.getVch_catg());
                ps.setString(i++, owner_dobj.getDealer_cd());
                ps.setInt(i++, owner_dobj.getSale_amt());
                ps.setString(i++, owner_dobj.getLaser_code());
                ps.setString(i++, owner_dobj.getGarage_add());
                ps.setInt(i++, owner_dobj.getLength());
                ps.setInt(i++, owner_dobj.getWidth());
                ps.setInt(i++, owner_dobj.getHeight());
                ps.setDate(i++, owner_dobj.getRegn_upto() == null ? null : new java.sql.Date(owner_dobj.getRegn_upto().getTime()));
                ps.setDate(i++, owner_dobj.getFit_upto() == null ? null : new java.sql.Date(owner_dobj.getFit_upto().getTime()));
                ps.setInt(i++, owner_dobj.getAnnual_income());
                ps.setString(i++, owner_dobj.getImported_vch() == null ? "N" : owner_dobj.getImported_vch());
                ps.setInt(i++, owner_dobj.getOther_criteria());
                ps.setInt(i++, owner_dobj.getPmt_type());
                ps.setInt(i++, owner_dobj.getPmt_catg());
                if (!CommonUtils.isNullOrBlank(rqrdTaxModesVar)) {
                    ps.setString(i++, owner_dobj.getRqrd_tax_modes());
                }
                ps.setString(i++, owner_dobj.getAppl_no());

                ps.executeUpdate();

            }

            updateVaOwnerId(tmgr, owner_dobj.getOwner_identity());
            if (owner_dobj.getDob_temp() != null) {
                updateVaOwnerTemp(tmgr, owner_dobj.getDob_temp());
            }

            if (owner_dobj.getTempReg() != null) {
                insertOrupdateVaTempRegnDetails(tmgr, owner_dobj.getTempReg());
            }
            if (!CommonUtils.isNullOrBlank(owner_dobj.getLinkedRegnNo())) {
                String query = "SELECT appl_no from " + TableList.VA_SIDE_TRAILER + " where appl_no = ?";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, owner_dobj.getAppl_no());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    updateVaSideTrailer(tmgr, owner_dobj);
                } else {
                    insertVaSideTrailer(tmgr, owner_dobj);
                }
            }

        } catch (VahanException ve) {
            throw ve;
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }//end of updateVaOwner

    /**
     * @author Kartikey Singh
     */
    public static void updateVaOwner(TransactionManager tmgr, Owner_dobj owner_dobj, int purCd, int offCode, String userStateCode) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        String rqrdTaxModesVar = " , rqrd_tax_modes = ?";
        boolean flgOtherState = false;

        try {

            if ((owner_dobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                    || owner_dobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))
                    && owner_dobj.getRegn_dt() != null) {
                flgOtherState = true;
            }

            /**
             * For other state vehicle/other district Regn Dt will previous regn
             * date
             */
            if (flgOtherState) {
                sql = "UPDATE " + TableList.VA_OWNER
                        + "   SET state_cd=?, off_cd=?, appl_no=?,"
                        + "       regn_dt=?,"
                        + "  purchase_dt=?, "
                        + "       owner_sr=?, owner_name=?, f_name=?, c_add1=?, c_add2=?, c_add3=?, "
                        + "       c_district=?, c_pincode=?, c_state=?, p_add1=?, p_add2=?, p_add3=?, "
                        + "       p_district=?, p_pincode=?, p_state=?, owner_cd=?, regn_type=?, "
                        + "       vh_class=?, chasi_no=?, eng_no=?, maker=?, maker_model=?, body_type=?, "
                        + "       no_cyl=?, hp=?, seat_cap=?, stand_cap=?, sleeper_cap=?, unld_wt=?, "
                        + "       ld_wt=?, gcw=?, fuel=?, color=?, manu_mon=?, manu_yr=?, norms=?, "
                        + "       wheelbase=?, cubic_cap=?, floor_area=?, ac_fitted=?, audio_fitted=?, "
                        + "       video_fitted=?, vch_purchase_as=?, vch_catg=?, dealer_cd=?, sale_amt=?, "
                        + "       laser_code=?, garage_add=?, length=?, width=?, height=?, regn_upto=?, "
                        + "       fit_upto=?, annual_income=?, imported_vch=?, other_criteria=?,"
                        + "       pmt_type=?,pmt_catg=? ,rqrd_tax_modes=?,op_dt=current_timestamp"
                        + " WHERE appl_no=?";

                ps = tmgr.prepareStatement(sql);
                int i = 1;
                ps.setString(i++, owner_dobj.getState_cd());
                ps.setInt(i++, owner_dobj.getOff_cd());
                ps.setString(i++, owner_dobj.getAppl_no());
                ps.setDate(i++, new java.sql.Date(owner_dobj.getRegn_dt().getTime()));

                ps.setDate(i++, owner_dobj.getPurchase_dt() == null ? null : new java.sql.Date(owner_dobj.getPurchase_dt().getTime()));
                ps.setInt(i++, owner_dobj.getOwner_sr());
                ps.setString(i++, owner_dobj.getOwner_name());
                ps.setString(i++, owner_dobj.getF_name());
                ps.setString(i++, owner_dobj.getC_add1());
                ps.setString(i++, owner_dobj.getC_add2());
                ps.setString(i++, owner_dobj.getC_add3());
                ps.setInt(i++, owner_dobj.getC_district());
                ps.setInt(i++, owner_dobj.getC_pincode());
                ps.setString(i++, owner_dobj.getC_state());
                ps.setString(i++, owner_dobj.getP_add1());
                ps.setString(i++, owner_dobj.getP_add2());
                ps.setString(i++, owner_dobj.getP_add3());
                ps.setInt(i++, owner_dobj.getP_district());
                ps.setInt(i++, owner_dobj.getP_pincode());
                ps.setString(i++, owner_dobj.getP_state());
                ps.setInt(i++, owner_dobj.getOwner_cd());
                ps.setString(i++, owner_dobj.getRegn_type());
                ps.setInt(i++, owner_dobj.getVh_class());
                ps.setString(i++, owner_dobj.getChasi_no());
                ps.setString(i++, owner_dobj.getEng_no());
                ps.setInt(i++, owner_dobj.getMaker());
                ps.setString(i++, owner_dobj.getMaker_model());
                ps.setString(i++, owner_dobj.getBody_type());
                ps.setInt(i++, owner_dobj.getNo_cyl());
                ps.setFloat(i++, owner_dobj.getHp());
                ps.setInt(i++, owner_dobj.getSeat_cap());
                ps.setInt(i++, owner_dobj.getStand_cap());
                ps.setInt(i++, owner_dobj.getSleeper_cap());
                ps.setInt(i++, owner_dobj.getUnld_wt());
                ps.setInt(i++, owner_dobj.getLd_wt());
                ps.setInt(i++, owner_dobj.getGcw());
                ps.setInt(i++, owner_dobj.getFuel());
                ps.setString(i++, owner_dobj.getColor());
                ps.setInt(i++, owner_dobj.getManu_mon());
                ps.setInt(i++, owner_dobj.getManu_yr());
                ps.setInt(i++, owner_dobj.getNorms());
                ps.setInt(i++, owner_dobj.getWheelbase());
                ps.setFloat(i++, owner_dobj.getCubic_cap());
                ps.setFloat(i++, owner_dobj.getFloor_area());
                ps.setString(i++, owner_dobj.getAc_fitted());
                ps.setString(i++, owner_dobj.getAudio_fitted());
                ps.setString(i++, owner_dobj.getVideo_fitted());
                ps.setString(i++, owner_dobj.getVch_purchase_as());
                ps.setString(i++, owner_dobj.getVch_catg());
                ps.setString(i++, owner_dobj.getDealer_cd());
                ps.setInt(i++, owner_dobj.getSale_amt());
                ps.setString(i++, owner_dobj.getLaser_code());
                ps.setString(i++, owner_dobj.getGarage_add());
                ps.setInt(i++, owner_dobj.getLength());
                ps.setInt(i++, owner_dobj.getWidth());
                ps.setInt(i++, owner_dobj.getHeight());
                ps.setDate(i++, owner_dobj.getRegn_upto() == null ? null : new java.sql.Date(owner_dobj.getRegn_upto().getTime()));
                ps.setDate(i++, owner_dobj.getFit_upto() == null ? null : new java.sql.Date(owner_dobj.getFit_upto().getTime()));
                ps.setInt(i++, owner_dobj.getAnnual_income());
                ps.setString(i++, owner_dobj.getImported_vch() == null ? "N" : owner_dobj.getImported_vch());
                ps.setInt(i++, owner_dobj.getOther_criteria());
                ps.setInt(i++, owner_dobj.getPmt_type());
                ps.setInt(i++, owner_dobj.getPmt_catg());
                ps.setString(i++, owner_dobj.getRqrd_tax_modes());
                ps.setString(i++, owner_dobj.getAppl_no());
                ps.executeUpdate();

            } else {
                if (purCd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                    int newApplOffCd = ServerUtility.getOfficeCdForDealerTempAppl(owner_dobj.getAppl_no(), owner_dobj.getState_cd(), "taxMode", offCode);
                    if (newApplOffCd != 0) {
                        rqrdTaxModesVar = "";
                    }
                }
                sql = "UPDATE " + TableList.VA_OWNER
                        + "   SET appl_no=?,"
                        //                        + "       regn_dt=?,"
                        + "  purchase_dt=?, "
                        + "       owner_sr=?, owner_name=?, f_name=?, c_add1=?, c_add2=?, c_add3=?, "
                        + "       c_district=?, c_pincode=?, c_state=?, p_add1=?, p_add2=?, p_add3=?, "
                        + "       p_district=?, p_pincode=?, p_state=?, owner_cd=?, regn_type=?, "
                        + "       vh_class=?, chasi_no=?, eng_no=?, maker=?, maker_model=?, body_type=?, "
                        + "       no_cyl=?, hp=?, seat_cap=?, stand_cap=?, sleeper_cap=?, unld_wt=?, "
                        + "       ld_wt=?, gcw=?, fuel=?, color=?, manu_mon=?, manu_yr=?, norms=?, "
                        + "       wheelbase=?, cubic_cap=?, floor_area=?, ac_fitted=?, audio_fitted=?, "
                        + "       video_fitted=?, vch_purchase_as=?, vch_catg=?, dealer_cd=?, sale_amt=?, "
                        + "       laser_code=?, garage_add=?, length=?, width=?, height=?, regn_upto=?, "
                        + "       fit_upto=?, annual_income=?, imported_vch=?, other_criteria=?,"
                        + "       pmt_type=?,pmt_catg=? " + rqrdTaxModesVar + " ,op_dt=current_timestamp"
                        + " WHERE appl_no=?";

                ps = tmgr.prepareStatement(sql);
                int i = 1;
                ps.setString(i++, owner_dobj.getAppl_no());
                // ps.setDate(i++, new java.sql.Date(owner_dobj.getRegn_dt().getTime()));
                ps.setDate(i++, owner_dobj.getPurchase_dt() == null ? null : new java.sql.Date(owner_dobj.getPurchase_dt().getTime()));
                ps.setInt(i++, owner_dobj.getOwner_sr());
                ps.setString(i++, owner_dobj.getOwner_name());
                ps.setString(i++, owner_dobj.getF_name());
                ps.setString(i++, owner_dobj.getC_add1());
                ps.setString(i++, owner_dobj.getC_add2());
                ps.setString(i++, owner_dobj.getC_add3());
                ps.setInt(i++, owner_dobj.getC_district());
                ps.setInt(i++, owner_dobj.getC_pincode());
                ps.setString(i++, owner_dobj.getC_state());
                ps.setString(i++, owner_dobj.getP_add1());
                ps.setString(i++, owner_dobj.getP_add2());
                ps.setString(i++, owner_dobj.getP_add3());
                ps.setInt(i++, owner_dobj.getP_district());
                ps.setInt(i++, owner_dobj.getP_pincode());
                ps.setString(i++, owner_dobj.getP_state());
                ps.setInt(i++, owner_dobj.getOwner_cd());
                if (CommonUtils.isNullOrBlank(owner_dobj.getRegn_type()) || "0".equals(owner_dobj.getRegn_type())) {
                    throw new VahanException("Registration Type can not be blank.");
                }
                ps.setString(i++, owner_dobj.getRegn_type());
                ps.setInt(i++, owner_dobj.getVh_class());
                ps.setString(i++, owner_dobj.getChasi_no());
                ps.setString(i++, owner_dobj.getEng_no());
                ps.setInt(i++, owner_dobj.getMaker());
                ps.setString(i++, owner_dobj.getMaker_model());
                ps.setString(i++, owner_dobj.getBody_type());
                ps.setInt(i++, owner_dobj.getNo_cyl());
                ps.setFloat(i++, owner_dobj.getHp());
                ps.setInt(i++, owner_dobj.getSeat_cap());
                ps.setInt(i++, owner_dobj.getStand_cap());
                ps.setInt(i++, owner_dobj.getSleeper_cap());
                ps.setInt(i++, owner_dobj.getUnld_wt());
                ps.setInt(i++, owner_dobj.getLd_wt());
                ps.setInt(i++, owner_dobj.getGcw());
                ps.setInt(i++, owner_dobj.getFuel());
                ps.setString(i++, owner_dobj.getColor());
                if (owner_dobj.getManu_mon() != null) {
                    ps.setInt(i++, owner_dobj.getManu_mon());
                } else {
                    ps.setInt(i++, 0);
                }
                if (owner_dobj.getManu_yr() != null) {
                    ps.setInt(i++, owner_dobj.getManu_yr());
                } else {
                    ps.setInt(i++, 0);
                }
                ps.setInt(i++, owner_dobj.getNorms());
                ps.setInt(i++, owner_dobj.getWheelbase());
                ps.setFloat(i++, owner_dobj.getCubic_cap());
                ps.setFloat(i++, owner_dobj.getFloor_area());
                ps.setString(i++, owner_dobj.getAc_fitted());
                ps.setString(i++, owner_dobj.getAudio_fitted());
                ps.setString(i++, owner_dobj.getVideo_fitted());
                ps.setString(i++, owner_dobj.getVch_purchase_as());
                ps.setString(i++, owner_dobj.getVch_catg());
                ps.setString(i++, owner_dobj.getDealer_cd());
                ps.setInt(i++, owner_dobj.getSale_amt());
                ps.setString(i++, owner_dobj.getLaser_code());
                ps.setString(i++, owner_dobj.getGarage_add());
                ps.setInt(i++, owner_dobj.getLength());
                ps.setInt(i++, owner_dobj.getWidth());
                ps.setInt(i++, owner_dobj.getHeight());
                ps.setDate(i++, owner_dobj.getRegn_upto() == null ? null : new java.sql.Date(owner_dobj.getRegn_upto().getTime()));
                ps.setDate(i++, owner_dobj.getFit_upto() == null ? null : new java.sql.Date(owner_dobj.getFit_upto().getTime()));
                ps.setInt(i++, owner_dobj.getAnnual_income());
                ps.setString(i++, owner_dobj.getImported_vch() == null ? "N" : owner_dobj.getImported_vch());
                ps.setInt(i++, owner_dobj.getOther_criteria());
                ps.setInt(i++, owner_dobj.getPmt_type());
                ps.setInt(i++, owner_dobj.getPmt_catg());
                if (!CommonUtils.isNullOrBlank(rqrdTaxModesVar)) {
                    ps.setString(i++, owner_dobj.getRqrd_tax_modes());
                }
                ps.setString(i++, owner_dobj.getAppl_no());

                ps.executeUpdate();
            }

            updateVaOwnerId(tmgr, owner_dobj.getOwner_identity());
            if (owner_dobj.getDob_temp() != null) {
                updateVaOwnerTemp(tmgr, owner_dobj.getDob_temp());
            }

            if (owner_dobj.getTempReg() != null) {
                insertOrupdateVaTempRegnDetails(tmgr, owner_dobj.getTempReg(), userStateCode, offCode);
            }
            if (!CommonUtils.isNullOrBlank(owner_dobj.getLinkedRegnNo())) {
                String query = "SELECT appl_no from " + TableList.VA_SIDE_TRAILER + " where appl_no = ?";
                ps = tmgr.prepareStatement(query);
                ps.setString(1, owner_dobj.getAppl_no());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    updateVaSideTrailer(tmgr, owner_dobj);
                } else {
                    insertVaSideTrailer(tmgr, owner_dobj);
                }
            }

        } catch (VahanException ve) {
            throw ve;
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }//end of updateVaOwner

    public static void updateVaOwnerId(TransactionManager tmgr, OwnerIdentificationDobj dobj) throws SQLException {
        PreparedStatement ps = null;

        String sql = "UPDATE " + TableList.VA_OWNER_IDENTIFICATION
                + "   SET "
                + " mobile_no=?, email_id=?, pan_no=?, aadhar_no=?, "
                + "       passport_no=?, ration_card_no=?, voter_id=?, dl_no=?, verified_on=current_timestamp, owner_ctg = ? , op_dt = current_timestamp,dept_cd=?"
                + " WHERE appl_no=?";

        ps = tmgr.prepareStatement(sql);

        int i = 1;

        ps.setLong(i++, dobj.getMobile_no());
        ps.setString(i++, dobj.getEmail_id());
        ps.setString(i++, dobj.getPan_no());
        ps.setString(i++, dobj.getAadhar_no());
        ps.setString(i++, dobj.getPassport_no());
        ps.setString(i++, dobj.getRation_card_no());
        ps.setString(i++, dobj.getVoter_id());
        ps.setString(i++, dobj.getDl_no());
        ps.setInt(i++, dobj.getOwnerCatg());
        ps.setInt(i++, dobj.getOwnerCdDept());
        ps.setString(i++, dobj.getAppl_no());
        ps.executeUpdate();
    }//end of updateVaOwnerId

    public static void insertOrupdateVaTempRegnDetails(TransactionManager tmgr, TempRegDobj dobj) throws SQLException {
        PreparedStatement ps = null;

        String sql = "SELECT * FROM " + TableList.VA_TMP_REGN_DTL + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getAppl_no());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            updateVaTempRegnDetails(tmgr, dobj);
        } else {
            insertVaTempRegnDetails(tmgr, dobj);
        }
    }

    /**
     * @author KArtikey Singh
     */
    public static void insertOrupdateVaTempRegnDetails(TransactionManager tmgr, TempRegDobj dobj, String userStateCode, int offCode) throws SQLException {
        PreparedStatement ps = null;

        String sql = "SELECT * FROM " + TableList.VA_TMP_REGN_DTL + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getAppl_no());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            updateVaTempRegnDetails(tmgr, dobj);
        } else {
            insertVaTempRegnDetails(tmgr, dobj, userStateCode, offCode);
        }
    }

    public static void updateVaTempRegnDetails(TransactionManager tmgr, TempRegDobj dobj) throws SQLException {
        PreparedStatement ps = null;

        String sql = "UPDATE " + TableList.VA_TMP_REGN_DTL
                + "   SET  regn_no=?, tmp_off_cd=?, regn_auth=?,"
                + " tmp_state_cd=?, tmp_regn_no=?, tmp_regn_dt=?, tmp_valid_upto=?, dealer_cd=?,op_dt = current_timestamp"
                + " where appl_no=?";

        ps = tmgr.prepareStatement(sql);

        int i = 1;

        ps.setString(i++, dobj.getRegn_no());
        ps.setInt(i++, dobj.getTmp_off_cd());
        ps.setString(i++, dobj.getRegn_auth());
        ps.setString(i++, dobj.getTmp_state_cd());
        ps.setString(i++, dobj.getTmp_regn_no());
        ps.setDate(i++, new java.sql.Date(dobj.getTmp_regn_dt().getTime()));
        ps.setDate(i++, new java.sql.Date(dobj.getTmp_valid_upto().getTime()));
        ps.setString(i++, dobj.getDealer_cd());
        ps.setString(i++, dobj.getAppl_no());
        ps.executeUpdate();

    }

    public static void updateVaOwnerTemp(TransactionManager tmgr, Owner_temp_dobj dobj) throws SQLException {
        PreparedStatement ps = null;

        String sql = "UPDATE va_owner_temp"
                + "   SET appl_no=?, temp_regn_no=?, state_cd_to=?, off_cd_to=?,purpose=?,body_building=?,op_dt = current_timestamp"
                + " WHERE appl_no=?";

        ps = tmgr.prepareStatement(sql);

        int i = 1;

        ps.setString(i++, dobj.getAppl_no());
        ps.setString(i++, dobj.getTemp_regn_no());
        ps.setString(i++, dobj.getState_cd_to());
        ps.setInt(i++, dobj.getOff_cd_to());
        ps.setString(i++, dobj.getPurpose());
        ps.setString(i++, dobj.getBodyBuilding());
        //ps.setDate(i++, dobj.getVerified_on() == null ? null : new java.sql.Date(dobj.getVerified_on().getTime()));
        ps.setString(i++, dobj.getAppl_no());
        ps.executeUpdate();

    }//end of updateVaOwnerId

    /*
    Note: Note making changes as of 1-12-2020
    */
    public static void insertVaOwner(TransactionManager tmgr, Owner_dobj owner_dobj) throws VahanException {

        PreparedStatement ps = null;
        String sql = null;
        String chasiNoExistMessage = null;

        try {

            if (owner_dobj == null) {
                return;
            }
            if (owner_dobj.getChasi_no() != null
                    && owner_dobj.getRegn_type() != null
                    && owner_dobj.getChasi_no().length() > 0
                    && owner_dobj.getRegn_type().length() > 0
                    && owner_dobj.getAuctionDobj() == null) {
                OwnerImpl ownerImpl = new OwnerImpl();
                chasiNoExistMessage = ownerImpl.isChasiAlreadyExist(tmgr, owner_dobj.getChasi_no(), owner_dobj.getRegn_type());
            }
            if (chasiNoExistMessage != null) {
                throw new VahanException(chasiNoExistMessage);
            }

            if (Util.getSelectedSeat().getAction_cd() != TableConstants.TM_ROLE_DEALER_NEW_APPL
                    && Util.getSelectedSeat().getAction_cd() != TableConstants.TM_ROLE_NEW_APPL
                    && Util.getSelectedSeat().getAction_cd() != TableConstants.TM_ROLE_NEW_APPL_TEMP
                    && Util.getSelectedSeat().getAction_cd() != TableConstants.TM_ROLE_DEALER_TEMP_APPL
                    && Util.getSelectedSeat().getAction_cd() != TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION
                    && Util.getSelectedSeat().getAction_cd() != TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION_VERIFICATION
                    && Util.getSelectedSeat().getAction_cd() != TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION_APPROVAL) {
                ServerUtil.validateOwnerDobj(owner_dobj);//Need to change this on the basis of action throug parameter
            }

            sql = "INSERT INTO " + TableList.VA_OWNER
                    + " (  state_cd, off_cd, appl_no, regn_no, regn_dt, purchase_dt, owner_sr, "
                    + "     owner_name, f_name, c_add1, c_add2, c_add3, c_district, c_pincode, "
                    + "     c_state, p_add1, p_add2, p_add3, p_district, p_pincode, p_state, "
                    + "     owner_cd, regn_type, vh_class, chasi_no, eng_no, maker, maker_model, "
                    + "     body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, "
                    + "     ld_wt, gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, "
                    + "     cubic_cap, floor_area, ac_fitted, audio_fitted, video_fitted, "
                    + "     vch_purchase_as, vch_catg, dealer_cd, sale_amt, laser_code, garage_add, "
                    + "     length, width, height, regn_upto, fit_upto, annual_income, imported_vch, "
                    + "     other_criteria,pmt_type,pmt_catg,rqrd_tax_modes,op_dt)"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, "
                    + "            ?, ?, ?, ?, ?, ?, ?, "
                    + "            ?, ?, ?, ?, ?, ?, ?, "
                    + "            ?, ?, ?, ?, ?, ?, ?, "
                    + "            ?, ?, ?, ?, ?, ?, ?, "
                    + "            ?, ?, ?, ?, ?, ?, ?, ?, "
                    + "            ?, ?, ?, ?, ?, "
                    + "            ?, ?, ?, ?, ?, ?, "
                    + "            ?, ?, ?, ?, ?, ?, ?, "
                    + "            ?,?,?,?, current_timestamp)";

            ps = tmgr.prepareStatement(sql);

            int i = 1;

            ps.setString(i++, owner_dobj.getState_cd());
            ps.setInt(i++, owner_dobj.getOff_cd());
            ps.setString(i++, owner_dobj.getAppl_no());
            ps.setString(i++, owner_dobj.getRegn_no());
            ps.setDate(i++, owner_dobj.getRegn_dt() == null ? null : new java.sql.Date(owner_dobj.getRegn_dt().getTime()));
            ps.setDate(i++, owner_dobj.getPurchase_dt() == null ? null : new java.sql.Date(owner_dobj.getPurchase_dt().getTime()));
            ps.setInt(i++, owner_dobj.getOwner_sr());
            ps.setString(i++, owner_dobj.getOwner_name());
            ps.setString(i++, owner_dobj.getF_name());
            ps.setString(i++, owner_dobj.getC_add1());
            ps.setString(i++, owner_dobj.getC_add2());
            ps.setString(i++, owner_dobj.getC_add3());
            ps.setInt(i++, owner_dobj.getC_district());
            ps.setInt(i++, owner_dobj.getC_pincode());
            ps.setString(i++, owner_dobj.getC_state());
            ps.setString(i++, owner_dobj.getP_add1());
            ps.setString(i++, owner_dobj.getP_add2());
            ps.setString(i++, owner_dobj.getP_add3());
            ps.setInt(i++, owner_dobj.getP_district());
            ps.setInt(i++, owner_dobj.getP_pincode());
            ps.setString(i++, owner_dobj.getP_state());
            ps.setInt(i++, owner_dobj.getOwner_cd());
            if ((CommonUtils.isNullOrBlank(owner_dobj.getRegn_type()) || "0".equals(owner_dobj.getRegn_type()))
                    && Util.getSelectedSeat().getAction_cd() != TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION
                    && Util.getSelectedSeat().getAction_cd() != TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION_VERIFICATION
                    && Util.getSelectedSeat().getAction_cd() != TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION_APPROVAL) {
                throw new VahanException("Registration Type can not be blank.");
            }
            ps.setString(i++, owner_dobj.getRegn_type());
            ps.setInt(i++, owner_dobj.getVh_class());
            ps.setString(i++, owner_dobj.getChasi_no() == null ? TableConstants.EMPTY_STRING : owner_dobj.getChasi_no());
            ps.setString(i++, owner_dobj.getEng_no() == null ? TableConstants.EMPTY_STRING : owner_dobj.getEng_no());
            ps.setInt(i++, owner_dobj.getMaker());
            ps.setString(i++, owner_dobj.getMaker_model() == null ? TableConstants.EMPTY_STRING : owner_dobj.getMaker_model());
            ps.setString(i++, owner_dobj.getBody_type() == null ? TableConstants.EMPTY_STRING : owner_dobj.getBody_type());
            ps.setInt(i++, owner_dobj.getNo_cyl());

            if (owner_dobj.getHp() != null) {
                ps.setFloat(i++, owner_dobj.getHp());
            } else {
                ps.setNull(i++, java.sql.Types.FLOAT);
            }

            ps.setInt(i++, owner_dobj.getSeat_cap());
            ps.setInt(i++, owner_dobj.getStand_cap());
            ps.setInt(i++, owner_dobj.getSleeper_cap());
            ps.setInt(i++, owner_dobj.getUnld_wt());
            ps.setInt(i++, owner_dobj.getLd_wt());
            ps.setInt(i++, owner_dobj.getGcw());
            ps.setInt(i++, owner_dobj.getFuel());
            ps.setString(i++, owner_dobj.getColor() == null ? TableConstants.EMPTY_STRING : owner_dobj.getColor());
            ps.setInt(i++, owner_dobj.getManu_mon() == null ? 0 : owner_dobj.getManu_mon());
            ps.setInt(i++, owner_dobj.getManu_yr() == null ? 0 : owner_dobj.getManu_yr());
            ps.setInt(i++, owner_dobj.getNorms());
            ps.setInt(i++, owner_dobj.getWheelbase());
            ps.setFloat(i++, owner_dobj.getCubic_cap());
            ps.setFloat(i++, owner_dobj.getFloor_area());
            ps.setString(i++, owner_dobj.getAc_fitted());
            ps.setString(i++, owner_dobj.getAudio_fitted());
            ps.setString(i++, owner_dobj.getVideo_fitted());
            ps.setString(i++, owner_dobj.getVch_purchase_as() == null ? TableConstants.EMPTY_STRING : owner_dobj.getVch_purchase_as());
            ps.setString(i++, owner_dobj.getVch_catg());
            ps.setString(i++, owner_dobj.getDealer_cd() == null ? TableConstants.EMPTY_STRING : owner_dobj.getDealer_cd());
            ps.setInt(i++, owner_dobj.getSale_amt());
            ps.setString(i++, owner_dobj.getLaser_code() == null ? TableConstants.EMPTY_STRING : owner_dobj.getLaser_code());
            ps.setString(i++, owner_dobj.getGarage_add() == null ? TableConstants.EMPTY_STRING : owner_dobj.getGarage_add());
            ps.setInt(i++, owner_dobj.getLength());
            ps.setInt(i++, owner_dobj.getWidth());
            ps.setInt(i++, owner_dobj.getHeight());

            ps.setDate(i++, owner_dobj.getRegn_upto() == null ? null : new java.sql.Date(owner_dobj.getRegn_upto().getTime()));
            ps.setDate(i++, owner_dobj.getFit_upto() == null ? null : new java.sql.Date(owner_dobj.getFit_upto().getTime()));
            ps.setInt(i++, owner_dobj.getAnnual_income());
            ps.setString(i++, owner_dobj.getImported_vch() == null ? TableConstants.EMPTY_STRING : owner_dobj.getImported_vch());
            ps.setInt(i++, owner_dobj.getOther_criteria());
            ps.setInt(i++, owner_dobj.getPmt_type());
            ps.setInt(i++, owner_dobj.getPmt_catg());
            ps.setString(i++, owner_dobj.getRqrd_tax_modes() == null ? TableConstants.EMPTY_STRING : owner_dobj.getRqrd_tax_modes());
            ps.executeUpdate();

            if (Util.getSelectedSeat().getAction_cd() != TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION
                    && Util.getSelectedSeat().getAction_cd() != TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION_VERIFICATION
                    && Util.getSelectedSeat().getAction_cd() != TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION_APPROVAL) {
                insertVaOwnerId(tmgr, owner_dobj.getOwner_identity());
            }

            if (owner_dobj.getDob_temp() != null) {
                if (owner_dobj.getDob_temp().getState_cd_to() == null) {
                    throw new VahanException("State to & Office to is not selected for Temporary Registration.");
                }
                insertVaOwnerTemp(tmgr, owner_dobj.getDob_temp());
            }

            if (owner_dobj.getTempReg() != null) {
                insertVaTempRegnDetails(tmgr, owner_dobj.getTempReg());
            }

            if (!CommonUtils.isNullOrBlank(owner_dobj.getLinkedRegnNo())) {
                insertVaSideTrailer(tmgr, owner_dobj);
            }

        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong During Saving of Data into Database. Please Contact to the System Administrator");
        }

    }//end of insertVaOwner

    /**
     * @author Kartikey Singh
     */
    public static void insertVaOwner(TransactionManager tmgr, Owner_dobj owner_dobj, int actionCd, String userStateCode,
            int offCode) throws VahanException {

        PreparedStatement ps = null;
        String sql = null;
        String chasiNoExistMessage = null;

        try {
            if (owner_dobj == null) {
                return;
            }
            if (owner_dobj.getChasi_no() != null
                    && owner_dobj.getRegn_type() != null
                    && owner_dobj.getChasi_no().length() > 0
                    && owner_dobj.getRegn_type().length() > 0) {
                OwnerImpl ownerImpl = new OwnerImpl();
                chasiNoExistMessage = ownerImpl.isChasiAlreadyExist(tmgr, owner_dobj.getChasi_no(), owner_dobj.getRegn_type());
            }
            if (chasiNoExistMessage != null) {
                throw new VahanException(chasiNoExistMessage);
            }

            if (actionCd != TableConstants.TM_ROLE_DEALER_NEW_APPL
                    && actionCd != TableConstants.TM_ROLE_NEW_APPL
                    && actionCd != TableConstants.TM_ROLE_NEW_APPL_TEMP
                    && actionCd != TableConstants.TM_ROLE_DEALER_TEMP_APPL
                    && actionCd != TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION
                    && actionCd != TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION_VERIFICATION
                    && actionCd != TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION_APPROVAL) {
                ServerUtil.validateOwnerDobj(owner_dobj);//Need to change this on the basis of action throug parameter
            }

            sql = "INSERT INTO " + TableList.VA_OWNER
                    + " (  state_cd, off_cd, appl_no, regn_no, regn_dt, purchase_dt, owner_sr, "
                    + "     owner_name, f_name, c_add1, c_add2, c_add3, c_district, c_pincode, "
                    + "     c_state, p_add1, p_add2, p_add3, p_district, p_pincode, p_state, "
                    + "     owner_cd, regn_type, vh_class, chasi_no, eng_no, maker, maker_model, "
                    + "     body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, "
                    + "     ld_wt, gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, "
                    + "     cubic_cap, floor_area, ac_fitted, audio_fitted, video_fitted, "
                    + "     vch_purchase_as, vch_catg, dealer_cd, sale_amt, laser_code, garage_add, "
                    + "     length, width, height, regn_upto, fit_upto, annual_income, imported_vch, "
                    + "     other_criteria,pmt_type,pmt_catg,rqrd_tax_modes,op_dt)"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?, "
                    + "            ?, ?, ?, ?, ?, ?, ?, "
                    + "            ?, ?, ?, ?, ?, ?, ?, "
                    + "            ?, ?, ?, ?, ?, ?, ?, "
                    + "            ?, ?, ?, ?, ?, ?, ?, "
                    + "            ?, ?, ?, ?, ?, ?, ?, ?, "
                    + "            ?, ?, ?, ?, ?, "
                    + "            ?, ?, ?, ?, ?, ?, "
                    + "            ?, ?, ?, ?, ?, ?, ?, "
                    + "            ?,?,?,?, current_timestamp)";

            ps = tmgr.prepareStatement(sql);

            int i = 1;

            ps.setString(i++, owner_dobj.getState_cd());
            ps.setInt(i++, owner_dobj.getOff_cd());
            ps.setString(i++, owner_dobj.getAppl_no());
            ps.setString(i++, owner_dobj.getRegn_no());
            ps.setDate(i++, owner_dobj.getRegn_dt() == null ? null : new java.sql.Date(owner_dobj.getRegn_dt().getTime()));
            ps.setDate(i++, owner_dobj.getPurchase_dt() == null ? null : new java.sql.Date(owner_dobj.getPurchase_dt().getTime()));
            ps.setInt(i++, owner_dobj.getOwner_sr());
            ps.setString(i++, owner_dobj.getOwner_name());
            ps.setString(i++, owner_dobj.getF_name());
            ps.setString(i++, owner_dobj.getC_add1());
            ps.setString(i++, owner_dobj.getC_add2());
            ps.setString(i++, owner_dobj.getC_add3());
            ps.setInt(i++, owner_dobj.getC_district());
            ps.setInt(i++, owner_dobj.getC_pincode());
            ps.setString(i++, owner_dobj.getC_state());
            ps.setString(i++, owner_dobj.getP_add1());
            ps.setString(i++, owner_dobj.getP_add2());
            ps.setString(i++, owner_dobj.getP_add3());
            ps.setInt(i++, owner_dobj.getP_district());
            ps.setInt(i++, owner_dobj.getP_pincode());
            ps.setString(i++, owner_dobj.getP_state());
            ps.setInt(i++, owner_dobj.getOwner_cd());
            if ((CommonUtils.isNullOrBlank(owner_dobj.getRegn_type()) || "0".equals(owner_dobj.getRegn_type()))
                    && actionCd != TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION
                    && actionCd != TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION_VERIFICATION
                    && actionCd != TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION_APPROVAL) {
                throw new VahanException("Registration Type can not be blank.");
            }
            ps.setString(i++, owner_dobj.getRegn_type());
            ps.setInt(i++, owner_dobj.getVh_class());
            ps.setString(i++, owner_dobj.getChasi_no() == null ? TableConstants.EMPTY_STRING : owner_dobj.getChasi_no());
            ps.setString(i++, owner_dobj.getEng_no() == null ? TableConstants.EMPTY_STRING : owner_dobj.getEng_no());
            ps.setInt(i++, owner_dobj.getMaker());
            ps.setString(i++, owner_dobj.getMaker_model() == null ? TableConstants.EMPTY_STRING : owner_dobj.getMaker_model());
            ps.setString(i++, owner_dobj.getBody_type() == null ? TableConstants.EMPTY_STRING : owner_dobj.getBody_type());
            ps.setInt(i++, owner_dobj.getNo_cyl());

            if (owner_dobj.getHp() != null) {
                ps.setFloat(i++, owner_dobj.getHp());
            } else {
                ps.setNull(i++, java.sql.Types.FLOAT);
            }

            ps.setInt(i++, owner_dobj.getSeat_cap());
            ps.setInt(i++, owner_dobj.getStand_cap());
            ps.setInt(i++, owner_dobj.getSleeper_cap());
            ps.setInt(i++, owner_dobj.getUnld_wt());
            ps.setInt(i++, owner_dobj.getLd_wt());
            ps.setInt(i++, owner_dobj.getGcw());
            ps.setInt(i++, owner_dobj.getFuel());
            ps.setString(i++, owner_dobj.getColor() == null ? TableConstants.EMPTY_STRING : owner_dobj.getColor());
            ps.setInt(i++, owner_dobj.getManu_mon() == null ? 0 : owner_dobj.getManu_mon());
            ps.setInt(i++, owner_dobj.getManu_yr() == null ? 0 : owner_dobj.getManu_yr());
            ps.setInt(i++, owner_dobj.getNorms());
            ps.setInt(i++, owner_dobj.getWheelbase());
            ps.setFloat(i++, owner_dobj.getCubic_cap());
            ps.setFloat(i++, owner_dobj.getFloor_area());
            ps.setString(i++, owner_dobj.getAc_fitted());
            ps.setString(i++, owner_dobj.getAudio_fitted());
            ps.setString(i++, owner_dobj.getVideo_fitted());
            ps.setString(i++, owner_dobj.getVch_purchase_as() == null ? TableConstants.EMPTY_STRING : owner_dobj.getVch_purchase_as());
            ps.setString(i++, owner_dobj.getVch_catg());
            ps.setString(i++, owner_dobj.getDealer_cd() == null ? TableConstants.EMPTY_STRING : owner_dobj.getDealer_cd());
            ps.setInt(i++, owner_dobj.getSale_amt());
            ps.setString(i++, owner_dobj.getLaser_code() == null ? TableConstants.EMPTY_STRING : owner_dobj.getLaser_code());
            ps.setString(i++, owner_dobj.getGarage_add() == null ? TableConstants.EMPTY_STRING : owner_dobj.getGarage_add());
            ps.setInt(i++, owner_dobj.getLength());
            ps.setInt(i++, owner_dobj.getWidth());
            ps.setInt(i++, owner_dobj.getHeight());

            ps.setDate(i++, owner_dobj.getRegn_upto() == null ? null : new java.sql.Date(owner_dobj.getRegn_upto().getTime()));
            ps.setDate(i++, owner_dobj.getFit_upto() == null ? null : new java.sql.Date(owner_dobj.getFit_upto().getTime()));
            ps.setInt(i++, owner_dobj.getAnnual_income());
            ps.setString(i++, owner_dobj.getImported_vch() == null ? TableConstants.EMPTY_STRING : owner_dobj.getImported_vch());
            ps.setInt(i++, owner_dobj.getOther_criteria());
            ps.setInt(i++, owner_dobj.getPmt_type());
            ps.setInt(i++, owner_dobj.getPmt_catg());
            ps.setString(i++, owner_dobj.getRqrd_tax_modes() == null ? TableConstants.EMPTY_STRING : owner_dobj.getRqrd_tax_modes());
            ps.executeUpdate();

            /*
             * insertVaOwnerId() used here is just for reference, that we have made changes to the existing code
             */
            if (actionCd != TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION
                    && actionCd != TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION_VERIFICATION
                    && actionCd != TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION_APPROVAL) {
                insertVaOwnerId(tmgr, owner_dobj.getOwner_identity(), userStateCode, offCode);
            }

            if (owner_dobj.getDob_temp() != null) {
                if (owner_dobj.getDob_temp().getState_cd_to() == null) {
                    throw new VahanException("State to & Office to is not selected for Temporary Registration.");
                }
                insertVaOwnerTemp(tmgr, owner_dobj.getDob_temp(), userStateCode, offCode);
            }

            if (owner_dobj.getTempReg() != null) {
                insertVaTempRegnDetails(tmgr, owner_dobj.getTempReg(), userStateCode, offCode);
            }

            if (!CommonUtils.isNullOrBlank(owner_dobj.getLinkedRegnNo())) {
                insertVaSideTrailer(tmgr, owner_dobj);
            }

        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong During Saving of Data into Database. Please Contact to the System Administrator");
        }

    }//end of insertVaOwner

    public static void insertVaOwnerId(TransactionManager tmgr, OwnerIdentificationDobj dobj) throws SQLException {

        PreparedStatement ps = null;
        String sql = "INSERT INTO " + TableList.VA_OWNER_IDENTIFICATION
                + "(state_cd,off_cd,appl_no, regn_no, mobile_no, email_id, pan_no, aadhar_no, passport_no, "
                + "            ration_card_no, voter_id, dl_no, verified_on,owner_ctg,op_dt,dept_cd)"
                + "  VALUES (?,?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp,?,current_timestamp,?)";

        ps = tmgr.prepareStatement(sql);
        int i = 1;
        ps.setString(i++, Util.getUserStateCode());
        ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
        ps.setString(i++, dobj.getAppl_no());
        ps.setString(i++, dobj.getRegn_no());
        ps.setLong(i++, dobj.getMobile_no());
        ps.setString(i++, dobj.getEmail_id());
        ps.setString(i++, dobj.getPan_no());
        ps.setString(i++, dobj.getAadhar_no());
        ps.setString(i++, dobj.getPassport_no());
        ps.setString(i++, dobj.getRation_card_no());
        ps.setString(i++, dobj.getVoter_id());
        ps.setString(i++, dobj.getDl_no());
        ps.setInt(i++, dobj.getOwnerCatg());
        ps.setInt(i++, dobj.getOwnerCdDept());
        //ps.setDate(i++, dobj.getVerified_on() == null ? null : new java.sql.Date(dobj.getVerified_on().getTime()));
        ps.executeUpdate();

    }//end of insertVaOwnerId

    /**
     * @author Kartikey Singh
     */
    public static void insertVaOwnerId(TransactionManager tmgr, OwnerIdentificationDobj dobj, String userStateCode, int offCode) throws SQLException {

        PreparedStatement ps = null;
        String sql = "INSERT INTO " + TableList.VA_OWNER_IDENTIFICATION
                + "(state_cd,off_cd,appl_no, regn_no, mobile_no, email_id, pan_no, aadhar_no, passport_no, "
                + "            ration_card_no, voter_id, dl_no, verified_on,owner_ctg,op_dt,dept_cd)"
                + "  VALUES (?,?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp,?,current_timestamp,?)";

        ps = tmgr.prepareStatement(sql);
        int i = 1;
        ps.setString(i++, userStateCode);
        ps.setInt(i++, offCode);
        ps.setString(i++, dobj.getAppl_no());
        ps.setString(i++, dobj.getRegn_no());
        ps.setLong(i++, dobj.getMobile_no());
        ps.setString(i++, dobj.getEmail_id());
        ps.setString(i++, dobj.getPan_no());
        ps.setString(i++, dobj.getAadhar_no());
        ps.setString(i++, dobj.getPassport_no());
        ps.setString(i++, dobj.getRation_card_no());
        ps.setString(i++, dobj.getVoter_id());
        ps.setString(i++, dobj.getDl_no());
        ps.setInt(i++, dobj.getOwnerCatg());
        ps.setInt(i++, dobj.getOwnerCdDept());
        //ps.setDate(i++, dobj.getVerified_on() == null ? null : new java.sql.Date(dobj.getVerified_on().getTime()));
        ps.executeUpdate();

    }//end of insertVaOwnerId

    public static void insertVaTempRegnDetails(TransactionManager tmgr, TempRegDobj dobj) throws SQLException {

        PreparedStatement ps = null;
        String sql = "INSERT INTO " + TableList.VA_TMP_REGN_DTL
                + "(state_cd,off_cd,appl_no, regn_no, tmp_off_cd, regn_auth, tmp_state_cd, tmp_regn_no,tmp_regn_dt, tmp_valid_upto, dealer_cd,op_dt)"
                + "  VALUES (?,?,?, ?, ?, ?, ?, ?, ?, ?, ?,current_timestamp)";

        ps = tmgr.prepareStatement(sql);
        int i = 1;
        ps.setString(i++, Util.getUserStateCode());
        ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
        ps.setString(i++, dobj.getAppl_no());
        ps.setString(i++, dobj.getRegn_no());
        ps.setInt(i++, dobj.getTmp_off_cd());
        ps.setString(i++, dobj.getRegn_auth() == null ? TableConstants.EMPTY_STRING : dobj.getRegn_auth());
        ps.setString(i++, dobj.getTmp_state_cd());
        ps.setString(i++, dobj.getTmp_regn_no() == null ? TableConstants.EMPTY_STRING : dobj.getTmp_regn_no());
        if (dobj.getTmp_regn_dt() != null) {
            ps.setDate(i++, new java.sql.Date(dobj.getTmp_regn_dt().getTime()));
        } else {
            ps.setNull(i++, java.sql.Types.DATE);
        }
        if (dobj.getTmp_valid_upto() != null) {
            ps.setDate(i++, new java.sql.Date(dobj.getTmp_valid_upto().getTime()));
        } else {
            ps.setNull(i++, java.sql.Types.DATE);
        }
        ps.setString(i++, dobj.getDealer_cd());

        ps.executeUpdate();
    }

    /**
     * @author Kartikey Singh
     */
    public static void insertVaTempRegnDetails(TransactionManager tmgr, TempRegDobj dobj, String userStateCode,
            int offCode) throws SQLException {

        PreparedStatement ps = null;
        String sql = "INSERT INTO " + TableList.VA_TMP_REGN_DTL
                + "(state_cd,off_cd,appl_no, regn_no, tmp_off_cd, regn_auth, tmp_state_cd, tmp_regn_no,tmp_regn_dt, tmp_valid_upto, dealer_cd,op_dt)"
                + "  VALUES (?,?,?, ?, ?, ?, ?, ?, ?, ?, ?,current_timestamp)";

        ps = tmgr.prepareStatement(sql);
        int i = 1;
        ps.setString(i++, userStateCode);
        ps.setInt(i++, offCode);
        ps.setString(i++, dobj.getAppl_no());
        ps.setString(i++, dobj.getRegn_no());
        ps.setInt(i++, dobj.getTmp_off_cd());
        ps.setString(i++, dobj.getRegn_auth() == null ? TableConstants.EMPTY_STRING : dobj.getRegn_auth());
        ps.setString(i++, dobj.getTmp_state_cd());
        ps.setString(i++, dobj.getTmp_regn_no() == null ? TableConstants.EMPTY_STRING : dobj.getTmp_regn_no());
        if (dobj.getTmp_regn_dt() != null) {
            ps.setDate(i++, new java.sql.Date(dobj.getTmp_regn_dt().getTime()));
        } else {
            ps.setNull(i++, java.sql.Types.DATE);
        }
        if (dobj.getTmp_valid_upto() != null) {
            ps.setDate(i++, new java.sql.Date(dobj.getTmp_valid_upto().getTime()));
        } else {
            ps.setNull(i++, java.sql.Types.DATE);
        }
        ps.setString(i++, dobj.getDealer_cd());

        ps.executeUpdate();
    }

    public static void insertVaOwnerTemp(TransactionManager tmgr, Owner_temp_dobj dobj) throws SQLException {

        PreparedStatement ps = null;
        String sql = "INSERT INTO va_owner_temp(state_cd,off_cd,appl_no, temp_regn_no, state_cd_to, off_cd_to,purpose,body_building,op_dt,temp_regn_type)"
                + "    VALUES (?,?,?, ?, ?, ?,?,?,current_timestamp,?)";

        ps = tmgr.prepareStatement(sql);
        int i = 1;
        ps.setString(i++, Util.getUserStateCode());
        ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
        ps.setString(i++, dobj.getAppl_no());
        ps.setString(i++, dobj.getTemp_regn_no());
        ps.setString(i++, dobj.getState_cd_to());
        ps.setInt(i++, dobj.getOff_cd_to());
        ps.setString(i++, dobj.getPurpose());
        ps.setString(i++, dobj.getBodyBuilding());
        ps.setString(i++, dobj.getTemp_regn_type());
        ps.executeUpdate();

    }//end of insertVaOwnerId

    /**
     * @author Kartikey Singh
     */
    public static void insertVaOwnerTemp(TransactionManager tmgr, Owner_temp_dobj dobj, String userStateCode, int offCode) throws SQLException {

        PreparedStatement ps = null;
        String sql = "INSERT INTO va_owner_temp(state_cd,off_cd,appl_no, temp_regn_no, state_cd_to, off_cd_to,purpose,body_building,op_dt,temp_regn_type)"
                + "    VALUES (?,?,?, ?, ?, ?,?,?,current_timestamp,?)";

        ps = tmgr.prepareStatement(sql);
        int i = 1;
        ps.setString(i++, userStateCode);
        ps.setInt(i++, offCode);
        ps.setString(i++, dobj.getAppl_no());
        ps.setString(i++, dobj.getTemp_regn_no());
        ps.setString(i++, dobj.getState_cd_to());
        ps.setInt(i++, dobj.getOff_cd_to());
        ps.setString(i++, dobj.getPurpose());
        ps.setString(i++, dobj.getBodyBuilding());
        ps.setString(i++, dobj.getTemp_regn_type());
        ps.executeUpdate();

    }

    public static int getNewRegUpto(Owner_dobj own_dobj) throws VahanException {
        int regUpto = -1;
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

            tmgr = new TransactionManager("getNewRegUpto");
            ps = tmgr.prepareStatement("SELECT *  FROM vm_validity_mast where pur_cd=? and state_cd=?");
            ps.setInt(1, TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE);
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getNewRegUpto")) {
                    //break;
                    regUpto = rs.getInt("new_value");
                }
            }

        } catch (SQLException sq) {
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

    /**
     * @author Kartikey Singh
     */
    public static int getNewRegUpto(Owner_dobj own_dobj, SessionVariablesModel sessionVariables) throws VahanException {
        int regUpto = -1;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;

        Exception e = null;
        VehicleParameters vehParameters = FormulaUtilities.fillVehicleParametersFromDobj(own_dobj, sessionVariables);

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

            tmgr = new TransactionManager("getNewRegUpto");
            ps = tmgr.prepareStatement("SELECT *  FROM vm_validity_mast where pur_cd=? and state_cd=?");
            ps.setInt(1, TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE);
            ps.setString(2, sessionVariables.getStateCodeSelected());
            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getNewRegUpto")) {
                    //break;
                    regUpto = rs.getInt("new_value");
                }
            }
        } catch (SQLException sq) {
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

    public static int getReNewRegUpto(Owner_dobj own_dobj) throws VahanException {

        int regUpto = 0;
        PreparedStatement ps = null;
        TransactionManager tmgr = null;

        Exception e = null;
        VehicleParameters vehParameters = fillVehicleParametersFromDobj(own_dobj);

        try {

            vehParameters.setVCH_CATG(own_dobj.getVch_catg());
            vehParameters.setVH_CLASS(own_dobj.getVh_class());
            vehParameters.setSEAT_CAP(own_dobj.getSeat_cap());
            if (own_dobj.getRegn_upto() != null) {
                vehParameters.setREGN_UPTO(DateUtils.parseDate(own_dobj.getRegn_upto()));
            }

            tmgr = new TransactionManager("Fitness_Impl");
            ps = tmgr.prepareStatement("SELECT *  FROM vm_validity_mast where pur_cd=? and state_cd=?");
            ps.setInt(1, TableConstants.VM_TRANSACTION_MAST_REN_REG);
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet();

            while (rs.next()) {
                if (isCondition(replaceTagValues(rs.getString("condition_formula"), vehParameters), "getReNewRegUpto")) {
                    regUpto = rs.getInt("re_new_value");
                }
            }

        } catch (Exception sq) {
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

        if (regUpto == 0) {
            throw new VahanException("Renewal of Registration is Not Permitted!!!.");
        }

        return regUpto;
    }

    public static String insertNewRegistrationDetail(Owner_dobj owner_dobj, InsDobj ins_dobj, HpaDobj hpa_dobj, String stateCd, int offCd) throws Exception {
        TransactionManager tmgr = null;
        String appl_number = null;
        try {
            tmgr = new TransactionManager("insertNewRegistrationDetail");
            appl_number = ServerUtil.getUniqueApplNo(tmgr, stateCd);
            String APPL_NO = appl_number;
            String REGN_NO = "NEW";
            owner_dobj.setFit_dt(new Date());
            NewImplementation.insertOrUpdateVaOwner(tmgr, owner_dobj);
            if (ins_dobj != null) {
                if (InsImpl.validateOwnerCodeWithInsType(owner_dobj.getOwner_cd(), ins_dobj.getIns_type())) {
                    InsImpl.insertUpdateInsurance(tmgr, APPL_NO, REGN_NO, ins_dobj, stateCd, offCd);
                } else {
                    throw new VahanException("Invalid Combination of Ownership Type & Insurance Type.");
                }
            }

//            if (hpa_dobj != null) {
////        use hpa_entry_bean    HpaImpl.insertUpdateHPA(tmgr, hpa_dobj);
//            }
            Status_dobj status = new Status_dobj();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            String dt = sdf.format(new Date());
            status.setAppl_dt(dt);
            status.setAppl_no(APPL_NO);
            status.setRegn_no(REGN_NO);
            status.setPur_cd(1);
            status.setFlow_slno(2);
            status.setFile_movement_slno(2);
            status.setAction_cd(TableConstants.TM_ROLE_NEW_REGISTRATION_FEE);
            status.setOffice_remark(TableConstants.EMPTY_STRING);
            status.setPublic_remark(TableConstants.EMPTY_STRING);
            status.setStatus("N");
            status.setState_cd(Util.getSession().getAttribute("state_cd").toString());
            status.setOff_cd(Util.getSelectedSeat().getOff_cd());
            status.setEmp_cd(0);
            ServerUtil.fileFlowForNewApplication(tmgr, status);
            tmgr.commit();
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return appl_number;
    }

    public static AdvanceRegnNo_dobj getAdvanceRegNoDetails(String rcptNo) throws VahanException {
        AdvanceRegnNo_dobj dobj = null;
        TransactionManager tmgr = null;
        SeatAllotedDetails selectedObj = Util.getSelectedSeat();
        try {
            tmgr = new TransactionManager("getAdvanceRegNoDetails");
            String sql = "select * from " + TableList.VT_ADVANCE_REGN_NO
                    + " where recp_no=? and state_cd=? and off_cd=? ";
            //and regn_appl_no is null
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, rcptNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, selectedObj.getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if (rs.getString("regn_appl_no") != null && !rs.getString("regn_appl_no").isEmpty()) {
                    throw new VahanException("Receipt No " + rcptNo + " is already used against Application No " + rs.getString("regn_appl_no"));
                } else {
                    dobj = new AdvanceRegnNo_dobj();
                    dobj.setState_cd(rs.getString("state_cd"));
                    dobj.setOff_cd(rs.getInt("off_cd"));
                    dobj.setRecp_no(rs.getString("recp_no"));
                    dobj.setRegn_appl_no(rs.getString("regn_appl_no"));
                    dobj.setRegn_no(rs.getString("regn_no"));
                    dobj.setOwner_name(rs.getString("owner_name"));
                    dobj.setF_name(rs.getString("f_name"));
                    dobj.setC_add1(rs.getString("c_add1"));
                    dobj.setC_add2(rs.getString("c_add2"));
                    dobj.setC_add3(rs.getString("c_add3"));
                    dobj.setC_district(rs.getInt("c_district"));
                    dobj.setC_pincode(rs.getInt("c_pincode"));
                    dobj.setC_state(rs.getString("c_state"));
                    dobj.setMobile_no(rs.getLong("mobile_no"));
                    dobj.setTotal_amt(rs.getLong("total_amt"));
                }
            } else {
                throw new VahanException("Details Not Found For Receipt No: " + rcptNo);
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Details Not Found For Receipt No: " + rcptNo);
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

    /**
     * @author KArtikey SIngh Changed the references to userStateCode and
     * offCode by passing them from Controller Also changed references of
     * AdvanceRegnNo_dobj to AdvanceRegnNoDobjModel as one of the getters of
     * AdvanceRegnNo_dobj references the session object
     */
    public static AdvanceRegnNoDobjModel getAdvanceRegNoDetails(String rcptNo, String userStateCode, int offCode) throws VahanException {
        AdvanceRegnNoDobjModel dobj = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getAdvanceRegNoDetails");
            String sql = "select * from " + TableList.VT_ADVANCE_REGN_NO
                    + " where recp_no=? and state_cd=? and off_cd=? ";
            //and regn_appl_no is null
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, rcptNo);
            ps.setString(2, userStateCode);
            ps.setInt(3, offCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if (rs.getString("regn_appl_no") != null && !rs.getString("regn_appl_no").isEmpty()) {
                    throw new VahanException("Receipt No " + rcptNo + " is already used against Application No " + rs.getString("regn_appl_no"));
                } else {
                    dobj = new AdvanceRegnNoDobjModel();
                    dobj.setState_cd(rs.getString("state_cd"));
                    dobj.setOff_cd(rs.getInt("off_cd"));
                    dobj.setRecp_no(rs.getString("recp_no"));
                    dobj.setRegn_appl_no(rs.getString("regn_appl_no"));
                    dobj.setRegn_no(rs.getString("regn_no"));
                    dobj.setOwner_name(rs.getString("owner_name"));
                    dobj.setF_name(rs.getString("f_name"));
                    dobj.setC_add1(rs.getString("c_add1"));
                    dobj.setC_add2(rs.getString("c_add2"));
                    dobj.setC_add3(rs.getString("c_add3"));
                    dobj.setC_district(rs.getInt("c_district"));
                    dobj.setC_pincode(rs.getInt("c_pincode"));
                    dobj.setC_state(rs.getString("c_state"));
                    dobj.setMobile_no(rs.getLong("mobile_no"));
                    dobj.setTotal_amt(rs.getLong("total_amt"));
                }
            } else {
                throw new VahanException("Details Not Found For Receipt No: " + rcptNo);
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Details Not Found For Receipt No: " + rcptNo);
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

    public static Date getFancyNoRcptDate(String rcptNo) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String query = TableConstants.EMPTY_STRING;
        Date rcptDate = null;

        if (rcptNo != null) {
            rcptNo = rcptNo.toUpperCase();
        }

        try {
            tmgr = new TransactionManager("getFancyNoRcptDate");
            query = "select fn_book_date from fancy.fn_book_date(?,?,?)";

            ps = tmgr.prepareStatement(query);
            ps.setString(1, rcptNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getUserSeatOffCode());
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                rcptDate = rs.getDate("fn_book_date");
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
        return rcptDate;
    }

    /**
     * @author Kartikey Singh
     */
    public static Date getFancyNoRcptDate(String rcptNo, String userStateCode, int selectedOffCode) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String query = TableConstants.EMPTY_STRING;
        Date rcptDate = null;

        if (rcptNo != null) {
            rcptNo = rcptNo.toUpperCase();
        }

        try {
            tmgr = new TransactionManager("getFancyNoRcptDate");
            query = "select fn_book_date from fancy.fn_book_date(?,?,?)";

            ps = tmgr.prepareStatement(query);
            ps.setString(1, rcptNo);
            ps.setString(2, userStateCode);
            ps.setInt(3, selectedOffCode);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                rcptDate = rs.getDate("fn_book_date");
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
        return rcptDate;
    }

    //Fetching verification date for dealer non-transport for MH
    public static Date getDealerVerificationDate(String applNo, int actionCd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String query = TableConstants.EMPTY_STRING;
        Date verifDate = new Date();

        if (applNo != null) {
            applNo = applNo.toUpperCase();
        }

        try {
            tmgr = new TransactionManager("getDealerVerificationDate");
            query = "select coalesce(max(op_dt),current_date) as op_dt from " + TableList.VHA_STATUS + " where appl_no=? and state_cd=? and off_cd=? and action_cd=? and status='C'";

            ps = tmgr.prepareStatement(query);
            ps.setString(1, applNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, Util.getUserSeatOffCode());
            ps.setInt(4, actionCd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                verifDate = rs.getDate("op_dt");
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
        return verifDate;
    }

    /**
     * @author Kartikey Singh Fetching verification date for dealer
     * non-transport for MH
     */
    public static Date getDealerVerificationDate(String applNo, int actionCd, String userStateCode, int selectedOffCode) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String query = TableConstants.EMPTY_STRING;
        Date verifDate = new Date();

        if (applNo != null) {
            applNo = applNo.toUpperCase();
        }

        try {
            tmgr = new TransactionManager("getDealerVerificationDate");
            query = "select coalesce(max(op_dt),current_date) as op_dt from " + TableList.VHA_STATUS + " where appl_no=? and state_cd=? and off_cd=? and action_cd=? and status='C'";

            ps = tmgr.prepareStatement(query);
            ps.setString(1, applNo);
            ps.setString(2, userStateCode);
            ps.setInt(3, selectedOffCode);
            ps.setInt(4, actionCd);
            rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                verifDate = rs.getDate("op_dt");
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
        return verifDate;
    }

    public static Date getRetainNoRcptDate(String rcptNo, String stateCd, int offCd) throws VahanException {
        TransactionManager tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        String query = TableConstants.EMPTY_STRING;
        Date rcptDate = null;

        if (rcptNo != null) {
            rcptNo = rcptNo.toUpperCase();
        }

        try {
            tmgr = new TransactionManager("getFancyNoRcptDate");
            query = "select surr_dt from " + TableList.VT_SURRENDER_RETENTION + " where rcpt_no=? and state_cd=? and off_cd=?";

            ps = tmgr.prepareStatement(query);
            ps.setString(1, rcptNo);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                rcptDate = rs.getDate("surr_dt");
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
        return rcptDate;
    }

    public static RetenRegnNo_dobj getRetenRegNoDetails(String rcptNo) throws VahanException {
        RetenRegnNo_dobj dobj = null;
        TransactionManager tmgr = null;
        SeatAllotedDetails selectedObj = Util.getSelectedSeat();
        try {
            tmgr = new TransactionManager("getRetenRegNoDetails");
            String sql = "select * from " + TableList.VT_SURRENDER_RETENTION
                    + " where rcpt_no=? and state_cd=? and off_cd=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, rcptNo);
            ps.setString(2, Util.getUserStateCode());
            ps.setInt(3, selectedObj.getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if (rs.getString("regn_appl_no") != null && !rs.getString("regn_appl_no").isEmpty()) {
                    throw new VahanException("Receipt No " + rcptNo + " is already used against Application No " + rs.getString("regn_appl_no"));
                } else {
                    dobj = new RetenRegnNo_dobj();
                    dobj.setState_cd(rs.getString("state_cd"));
                    dobj.setOff_cd(rs.getInt("off_cd"));
                    dobj.setRecp_no(rs.getString("rcpt_no"));
                    dobj.setRegn_appl_no(rs.getString("regn_appl_no"));
                    dobj.setRegn_no(rs.getString("old_regn_no"));
                    dobj.setOwner_name(rs.getString("owner_name"));
                    dobj.setF_name(rs.getString("f_name"));
                    dobj.setC_add1(rs.getString("c_add1"));
                    dobj.setC_add2(rs.getString("c_add2"));
                    dobj.setC_add3(rs.getString("c_add3"));
                    dobj.setC_district(rs.getInt("c_district"));
                    dobj.setC_pincode(rs.getInt("c_pincode"));
                    dobj.setC_state(rs.getString("c_state"));
                    dobj.setMobile_no(rs.getLong("mobile_no"));
                }
            } else {
                throw new VahanException("Details Not Found For Receipt No: " + rcptNo);
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Details Not Found For Receipt No: " + rcptNo);
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

    /**
     * @author Kartikey Singh
     */
    public static RetenRegnNo_dobj getRetenRegNoDetails(String rcptNo, String stateCode, int selectedOffCode) throws VahanException {
        RetenRegnNo_dobj dobj = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getRetenRegNoDetails");
            String sql = "select * from " + TableList.VT_SURRENDER_RETENTION
                    + " where rcpt_no=? and state_cd=? and off_cd=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, rcptNo);
            ps.setString(2, stateCode);
            ps.setInt(3, selectedOffCode);
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                if (rs.getString("regn_appl_no") != null && !rs.getString("regn_appl_no").isEmpty()) {
                    throw new VahanException("Receipt No " + rcptNo + " is already used against Application No " + rs.getString("regn_appl_no"));
                } else {
                    dobj = new RetenRegnNo_dobj();
                    dobj.setState_cd(rs.getString("state_cd"));
                    dobj.setOff_cd(rs.getInt("off_cd"));
                    dobj.setRecp_no(rs.getString("rcpt_no"));
                    dobj.setRegn_appl_no(rs.getString("regn_appl_no"));
                    dobj.setRegn_no(rs.getString("old_regn_no"));
                    dobj.setOwner_name(rs.getString("owner_name"));
                    dobj.setF_name(rs.getString("f_name"));
                    dobj.setC_add1(rs.getString("c_add1"));
                    dobj.setC_add2(rs.getString("c_add2"));
                    dobj.setC_add3(rs.getString("c_add3"));
                    dobj.setC_district(rs.getInt("c_district"));
                    dobj.setC_pincode(rs.getInt("c_pincode"));
                    dobj.setC_state(rs.getString("c_state"));
                    dobj.setMobile_no(rs.getLong("mobile_no"));
                }
            } else {
                throw new VahanException("Details Not Found For Receipt No: " + rcptNo);
            }

        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Details Not Found For Receipt No: " + rcptNo);
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

    public static void updateAdvanceRegNoDetails(AdvanceRegnNo_dobj dobj, Owner_dobj ownDobj, TransactionManager tmgr) throws VahanException {

        try {
            String sql = " Select * from " + TableList.FANCY_VT_FANCY_REGISTER
                    + " where regn_no=? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getRegn_no());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (rs.getString("veh_type") != null && !rs.getString("veh_type").isEmpty()) {
                    int vehType = Integer.parseInt(rs.getString("veh_type"));
                    if (!(vehType == 0 || vehType == -1) && vehType != ServerUtil.VehicleClassType(ownDobj.getVh_class())) {
                        String message = TableConstants.EMPTY_STRING;
                        if (vehType == 1) {
                            message = "Fancy Number is Booked For Transport Vehicle";
                        } else {
                            message = "Fancy Number is Booked For Non-Transport Vehicle";
                        }
                        throw new VahanException(message);
                    }
                }

            }

            sql = "Update " + TableList.VT_ADVANCE_REGN_NO
                    + " set regn_appl_no=? where regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getRegn_appl_no());
            ps.setString(2, dobj.getRegn_no());
            ps.executeUpdate();

        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Updation of Fancy Details");
        }

    }

    /*
     * @author Kartikey Singh
     * Changed first argument to AdvanceRegnNoDobjModel as we can't create
     * object of AdvanceRegnNo_dobj in REST controller
     */
    public static void updateAdvanceRegNoDetails(AdvanceRegnNoDobjModel dobj, Owner_dobj ownDobj, TransactionManager tmgr) throws VahanException {

        try {
            String sql = " Select * from " + TableList.FANCY_VT_FANCY_REGISTER
                    + " where regn_no=? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getRegn_no());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                if (rs.getString("veh_type") != null && !rs.getString("veh_type").isEmpty()) {
                    int vehType = Integer.parseInt(rs.getString("veh_type"));
                    if (!(vehType == 0 || vehType == -1) && vehType != ServerUtil.VehicleClassType(ownDobj.getVh_class())) {
                        String message = TableConstants.EMPTY_STRING;
                        if (vehType == 1) {
                            message = "Fancy Number is Booked For Transport Vehicle";
                        } else {
                            message = "Fancy Number is Booked For Non-Transport Vehicle";
                        }
                        throw new VahanException(message);
                    }
                }

            }

            sql = "Update " + TableList.VT_ADVANCE_REGN_NO
                    + " set regn_appl_no=? where regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, dobj.getRegn_appl_no());
            ps.setString(2, dobj.getRegn_no());
            ps.executeUpdate();

        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Updation of Fancy Details");
        }

    }

    public static void updateRetenRegNoDetails(RetenRegnNo_dobj dobj, TransactionManager tmgr) throws Exception {
        String sql = "Update " + TableList.VT_SURRENDER_RETENTION
                + " set regn_appl_no=? where old_regn_no=? and rcpt_no=? ";
        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, dobj.getRegn_appl_no());
        ps.setString(2, dobj.getRegn_no());
        ps.setString(3, dobj.getRecp_no());
        ps.executeUpdate();

    }

    public static void insertIntoVhOwner(TransactionManager tmgr, Status_dobj dobj, String stateCd, int offCd, String oldRegn) throws SQLException {

        PreparedStatement ps = null;
        int pos = 1;
        String sql = "INSERT INTO " + TableList.VH_OWNER
                + " (state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name,"
                + " f_name, c_add1, c_add2, c_add3, c_district, c_pincode, c_state,"
                + " p_add1, p_add2, p_add3, p_district, p_pincode, p_state, owner_cd,"
                + " regn_type, vh_class, chasi_no, eng_no, maker, maker_model, body_type,"
                + " no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt,"
                + " gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, cubic_cap,"
                + " floor_area, ac_fitted, audio_fitted, video_fitted, vch_purchase_as,"
                + " vch_catg, dealer_cd, sale_amt, laser_code, garage_add, length,"
                + " width, height, regn_upto, fit_upto, annual_income, imported_vch,"
                + " other_criteria, status, op_dt, appl_no, moved_on, moved_by,moved_status)"
                + " SELECT state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name, \n"
                + " f_name, c_add1, c_add2, c_add3, c_district, c_pincode, \n"
                + " c_state, p_add1, p_add2, p_add3, p_district, p_pincode, p_state, \n"
                + " owner_cd, regn_type, vh_class, chasi_no, eng_no, maker, maker_model, \n"
                + " body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, \n"
                + " ld_wt, gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, \n"
                + " cubic_cap, floor_area, ac_fitted, audio_fitted, video_fitted, \n"
                + " vch_purchase_as, vch_catg, dealer_cd, sale_amt, laser_code, garage_add, \n"
                + " length, width, height, regn_upto, fit_upto, annual_income, \n"
                + " imported_vch, other_criteria, status, op_dt, ? as appl_no, current_timestamp as moved_on, \n"
                + " ? as moved_by,? "
                + "  FROM " + TableList.VT_OWNER + " WHERE regn_no = ? and state_cd = ? and off_cd =?";

        ps = tmgr.prepareStatement(sql);

        ps.setString(pos++, dobj.getAppl_no());
        ps.setString(pos++, String.valueOf(dobj.getEmp_cd()));
        ps.setString(pos++, TableConstants.VH_MOVED_STATUS_DELETE);
        if (!CommonUtils.isNullOrBlank(oldRegn)) {
            ps.setString(pos++, oldRegn);
        } else {
            ps.setString(pos++, dobj.getRegn_no());
        }
        ps.setString(pos++, stateCd);
        ps.setInt(pos++, offCd);
        ps.executeUpdate();

    }

    public static AdvanceRegnNo_dobj getAdvanceFeeDetailsMessage(String applNo) throws VahanException {

        AdvanceRegnNo_dobj dobj = null;
        TransactionManager tmgr = null;
        SeatAllotedDetails selectedObj = Util.getSelectedSeat();
        try {
            if (selectedObj != null) {
                tmgr = new TransactionManager("getAdvanceRegNoDetails");
                String sql = "select * from " + TableList.VT_ADVANCE_REGN_NO
                        + " where regn_appl_no=? and state_cd=? and off_cd=? ";
                PreparedStatement ps = tmgr.prepareStatement(sql);
                ps.setString(1, applNo);
                ps.setString(2, Util.getUserStateCode());
                ps.setInt(3, selectedObj.getOff_cd());
                RowSet rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    dobj = new AdvanceRegnNo_dobj();
                    dobj.setState_cd(rs.getString("state_cd"));
                    dobj.setOff_cd(rs.getInt("off_cd"));
                    dobj.setRecp_no(rs.getString("recp_no"));
                    dobj.setRegn_appl_no(rs.getString("regn_appl_no"));
                    dobj.setRegn_no(rs.getString("regn_no"));
                    dobj.setOwner_name(rs.getString("owner_name"));
                    dobj.setF_name(rs.getString("f_name"));
                    dobj.setC_add1(rs.getString("c_add1"));
                    dobj.setC_add2(rs.getString("c_add2"));
                    dobj.setC_add3(rs.getString("c_add3"));
                    dobj.setC_district(rs.getInt("c_district"));
                    dobj.setC_pincode(rs.getInt("c_pincode"));
                    dobj.setC_state(rs.getString("c_state"));
                    dobj.setMobile_no(rs.getLong("mobile_no"));
                    dobj.setTotal_amt(rs.getLong("total_amt"));
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("No Details Found For Applno: " + applNo);
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

    /**
     * @author Kartikey Singh
     */
    public static AdvanceRegnNo_dobj getAdvanceFeeDetailsMessage(String applNo, String userStateCode, int selectedOffCode) throws VahanException {

        AdvanceRegnNo_dobj dobj = null;
        TransactionManager tmgr = null;
        try {
            if (selectedOffCode >= 0) {
                tmgr = new TransactionManager("getAdvanceRegNoDetails");
                String sql = "select * from " + TableList.VT_ADVANCE_REGN_NO
                        + " where regn_appl_no=? and state_cd=? and off_cd=? ";
                PreparedStatement ps = tmgr.prepareStatement(sql);
                ps.setString(1, applNo);
                ps.setString(2, userStateCode);
                ps.setInt(3, selectedOffCode);
                RowSet rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    dobj = new AdvanceRegnNo_dobj();
                    dobj.setState_cd(rs.getString("state_cd"));
                    dobj.setOff_cd(rs.getInt("off_cd"));
                    dobj.setRecp_no(rs.getString("recp_no"));
                    dobj.setRegn_appl_no(rs.getString("regn_appl_no"));
                    dobj.setRegn_no(rs.getString("regn_no"));
                    dobj.setOwner_name(rs.getString("owner_name"));
                    dobj.setF_name(rs.getString("f_name"));
                    dobj.setC_add1(rs.getString("c_add1"));
                    dobj.setC_add2(rs.getString("c_add2"));
                    dobj.setC_add3(rs.getString("c_add3"));
                    dobj.setC_district(rs.getInt("c_district"));
                    dobj.setC_pincode(rs.getInt("c_pincode"));
                    dobj.setC_state(rs.getString("c_state"));
                    dobj.setMobile_no(rs.getLong("mobile_no"));
                    dobj.setTotal_amt(rs.getLong("total_amt"));
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("No Details Found For Applno: " + applNo);
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

    public static String getAdvanceRegnNo(String applNo) {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String regnNo = null;
        String sql = "SELECT regn_no FROM " + TableList.VT_ADVANCE_REGN_NO
                + " WHERE regn_appl_no=?";

        try {
            tmgr = new TransactionManager("getAdvanceRegnNo");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo.toUpperCase());

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

    public static String getAdvanceRetenNo(String applNo) {

        PreparedStatement ps = null;
        TransactionManager tmgr = null;
        String regnNo = null;
        String sql = "SELECT old_regn_no FROM " + TableList.VT_SURRENDER_RETENTION
                + " WHERE regn_appl_no=?";
        try {
            tmgr = new TransactionManager("getRetenNo");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo.toUpperCase());

            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) // found
            {
                regnNo = rs.getString("old_regn_no");
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

    public static String getTaxModes(Owner_dobj dobj) {
        String rqrd_tax_modes = null;
        if (dobj != null && dobj.getTaxModList() != null && !dobj.getTaxModList().isEmpty()) {
            rqrd_tax_modes = dobj.getTaxModList().toString();
            rqrd_tax_modes = rqrd_tax_modes.replace("[", TableConstants.EMPTY_STRING).replace("]", TableConstants.EMPTY_STRING).replaceAll(" ", TableConstants.EMPTY_STRING);
        }
        return rqrd_tax_modes;
    }

    public static Map<Integer, String> getRqrdTaxModes(String rqrdTaxModes) {
        String split[] = null;
        split = rqrdTaxModes.split(",");
        Map<Integer, String> taxModes = new TreeMap<>();
        for (int i = 0; i < split.length; i++) {
            String subSplit[] = split[i].split("=");
            if (subSplit.length > 0 && !subSplit[0].trim().equals(TableConstants.EMPTY_STRING)) {
                taxModes.put(Integer.valueOf(subSplit[0].trim()), subSplit[1].trim());
            }
        }
        return taxModes;
    }

    public static Map<Integer, Integer> getRqrdTaxNoOfAdvUnits(String rqrdTaxUnits) {
        String split[] = null;
        split = rqrdTaxUnits.split(",");
        Map<Integer, Integer> taxNoOfUnits = new TreeMap<>();
        for (int i = 0; i < split.length; i++) {
            String subSplit[] = split[i].split("=");
            if (subSplit.length > 0 && !subSplit[0].trim().equals(TableConstants.EMPTY_STRING)) {
                taxNoOfUnits.put(Integer.valueOf(subSplit[0].trim()), Integer.valueOf(subSplit[1].trim()));
            }
        }
        return taxNoOfUnits;
    }

    public static void validationFancyRcptDate(TmConfigurationDobj tmConfDobj, String rcptno, Date rcptDate) throws VahanException {
        String dateString = DateUtils.getCurrentDate_YYYY_MM_DD();
        Date currentDate = JSFUtils.getStringToDateyyyyMMdd(dateString);
        Date dateUpto = fancyValidUpto(tmConfDobj, rcptDate);
        if (dateUpto.before(currentDate)) {
            throw new VahanException("Receipt Date is Expired!!!");
        }
    }

    public static void validationRetainNoRcptDate(TmConfigurationDobj tmConfDobj, String rcptno, Date rcptDate) throws VahanException {
        String dateString = DateUtils.getCurrentDate_YYYY_MM_DD();
        Date currentDate = JSFUtils.getStringToDateyyyyMMdd(dateString);
        Date dateUpto = retentionValidUpto(tmConfDobj, rcptDate);
        if (dateUpto.before(currentDate)) {
            throw new VahanException("Receipt Date is Expired!!!");
        }
    }

    public static void validateFancyVehicleCategory(AdvanceRegnNo_dobj advDobj, String vchCatg) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("NewImpl:validateFancyVehicleCategory");
            if (advDobj != null) {
                validateFancyVehicleCategory(advDobj, vchCatg, tmgr);
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(" Error in Fancy Receipt mapping");
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

    /**
     * @author Kartikey Singh Changed the references to userStateCode and
     * offCode by passing them from Controller Also changed references of
     * AdvanceRegnNo_dobj to AdvanceRegnNoDobjModel as one of the getters of
     * AdvanceRegnNo_dobj references the session object
     */
    public static void validateFancyVehicleCategory(AdvanceRegnNoDobjModel advDobj, String vchCatg) throws VahanException {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("NewImpl:validateFancyVehicleCategory");
            if (advDobj != null) {
                validateFancyVehicleCategory(advDobj, vchCatg, tmgr);
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException(" Error in Fancy Receipt mapping");
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

    public static void validateFancyVehicleCategory(AdvanceRegnNo_dobj advDobj, String vchCatg, TransactionManager tmgr) throws VahanException {
        if (advDobj != null) {
            try {
                String sql = "select * from fancy.fn_veh_catg(?,?,?)";
                PreparedStatement ps = tmgr.prepareStatement(sql);
                ps.setString(1, advDobj.getRecp_no());
                ps.setString(2, advDobj.getState_cd());
                ps.setInt(3, advDobj.getOff_cd());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    String fancyVehCatg = rs.getString("fn_veh_catg");
                    if (!CommonUtils.isNullOrBlank(fancyVehCatg) && !CommonUtils.isNullOrBlank(vchCatg)) {
                        if (!fancyVehCatg.equals(vchCatg)) {
                            throw new VahanException("Fancy Receipt is Collected for Different Vehicle Category");
                        }
                    }
                }
            } catch (SQLException ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                throw new VahanException(" Error in Fancy Receipt mapping");
            }
        }
    }

    /*
     * @author Kartikey Singh
     * Changed the references to userStateCode and offCode by passing them from Controller
     * Also changed references of AdvanceRegnNo_dobj to AdvanceRegnNoDobjModel as one of the
     * getters of AdvanceRegnNo_dobj references the session object
     */
    public static void validateFancyVehicleCategory(AdvanceRegnNoDobjModel advDobj, String vchCatg, TransactionManager tmgr) throws VahanException {
        if (advDobj != null) {
            try {
                String sql = "select * from fancy.fn_veh_catg(?,?,?)";
                PreparedStatement ps = tmgr.prepareStatement(sql);
                ps.setString(1, advDobj.getRecp_no());
                ps.setString(2, advDobj.getState_cd());
                ps.setInt(3, advDobj.getOff_cd());
                RowSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (rs.next()) {
                    String fancyVehCatg = rs.getString("fn_veh_catg");
                    if (!CommonUtils.isNullOrBlank(fancyVehCatg) && !CommonUtils.isNullOrBlank(vchCatg)) {
                        if (!fancyVehCatg.equals(vchCatg)) {
                            throw new VahanException("Fancy Receipt is Collected for Different Vehicle Category");
                        }
                    }
                }
            } catch (SQLException ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                throw new VahanException(" Error in Fancy Receipt mapping");
            }
        }
    }

    public static Date fancyValidUpto(TmConfigurationDobj tmConfDobj, Date rcptDate) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(rcptDate);
        if (tmConfDobj.getFancyFeeValidMode().equals("M")) {
            c.add(java.util.Calendar.MONTH, tmConfDobj.getFancyFeeValidPeriod());
            c.add(java.util.Calendar.DATE, -1);
        } else if (tmConfDobj.getFancyFeeValidMode().equals("Y")) {
            c.add(java.util.Calendar.YEAR, tmConfDobj.getFancyFeeValidPeriod());
            c.add(java.util.Calendar.DATE, -1);
        } else {
            c.add(java.util.Calendar.DATE, tmConfDobj.getFancyFeeValidPeriod());
            c.add(java.util.Calendar.DATE, -1);
        }

        return c.getTime();

    }

    public static Date retentionValidUpto(TmConfigurationDobj tmConfDobj, Date rcptDate) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(rcptDate);
        if (tmConfDobj.getRetentionFeeValidMode().equals("M")) {
            c.add(java.util.Calendar.MONTH, tmConfDobj.getRetentionFeeValidPeriod());
            c.add(java.util.Calendar.DATE, -1);
        } else if (tmConfDobj.getRetentionFeeValidMode().equals("Y")) {
            c.add(java.util.Calendar.YEAR, tmConfDobj.getRetentionFeeValidPeriod());
            c.add(java.util.Calendar.DATE, -1);
        } else {
            c.add(java.util.Calendar.DATE, tmConfDobj.getRetentionFeeValidPeriod());
            c.add(java.util.Calendar.DATE, -1);
        }

        return c.getTime();

    }

    public static void insertOrUpdateVaSideTrailer(TransactionManager tmgr, Owner_dobj owner_dobj) throws SQLException {
        PreparedStatement ps = null;
        RowSet rs = null;
        String query = "SELECT appl_no from " + TableList.VA_SIDE_TRAILER + " where appl_no = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, owner_dobj.getAppl_no());
        rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            insertIntoVhaSideTrailer(tmgr, owner_dobj.getAppl_no());
            updateVaSideTrailer(tmgr, owner_dobj);
        } else {
            insertVaSideTrailer(tmgr, owner_dobj);
        }
    }

    public static void insertVaSideTrailer(TransactionManager tmgr, Owner_dobj owner_dobj) throws SQLException {
        PreparedStatement ps = null;
        String query = "INSERT INTO " + TableList.VA_SIDE_TRAILER + "(\n"
                + "            state_cd, off_cd, appl_no, regn_no, link_regn_no,  op_dt)\n"
                + "    VALUES (?, ?, ?, ?, ?, current_timestamp)";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, owner_dobj.getState_cd());
        ps.setInt(2, owner_dobj.getOff_cd());
        ps.setString(3, owner_dobj.getAppl_no());
        ps.setString(4, owner_dobj.getRegn_no());
        ps.setString(5, owner_dobj.getLinkedRegnNo());

        ps.executeUpdate();
    }

    public static void updateVaSideTrailer(TransactionManager tmgr, Owner_dobj owner_dobj) throws SQLException {
        PreparedStatement ps = null;
        String query = "UPDATE " + TableList.VA_SIDE_TRAILER + "\n"
                + "   SET link_regn_no=?, op_dt=current_timestamp\n"
                + " WHERE appl_no = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, owner_dobj.getLinkedRegnNo());
        ps.setString(2, owner_dobj.getAppl_no());
        ps.executeUpdate();
    }

    public static void insertIntoVhaSideTrailer(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String query = "INSERT INTO " + TableList.VHA_SIDE_TRAILER + "(\n"
                + "   moved_on, moved_by, state_cd, off_cd, appl_no, regn_no, link_regn_no,  op_dt) \n"
                + "   SELECT current_timestamp,?, state_cd, off_cd, appl_no, regn_no, link_regn_no, op_dt\n"
                + "  FROM " + TableList.VA_SIDE_TRAILER + " where appl_no = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    /**
     * @author Kartikey Singh
     */
    public static void insertIntoVhaSideTrailer(TransactionManager tmgr, String appl_no, String empCode) throws SQLException {
        PreparedStatement ps = null;
        String query = "INSERT INTO " + TableList.VHA_SIDE_TRAILER + "(\n"
                + "   moved_on, moved_by, state_cd, off_cd, appl_no, regn_no, link_regn_no,  op_dt) \n"
                + "   SELECT current_timestamp,?, state_cd, off_cd, appl_no, regn_no, link_regn_no, op_dt\n"
                + "  FROM " + TableList.VA_SIDE_TRAILER + " where appl_no = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, empCode);
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public static void insertIntoVhaSideTrailerWithTimeInterval(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String query = "INSERT INTO " + TableList.VHA_SIDE_TRAILER + "(\n"
                + "   moved_on, moved_by, state_cd, off_cd, appl_no, regn_no, link_regn_no, op_dt )\n"
                + "   SELECT current_timestamp + interval '1 second' as moved_on, ?, state_cd, off_cd, appl_no, regn_no, link_regn_no, op_dt \n"
                + "  FROM " + TableList.VA_SIDE_TRAILER + " where appl_no = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    /*
     * @author Kartikey Singh
     */
    public static void insertIntoVhaSideTrailerWithTimeInterval(TransactionManager tmgr, String appl_no, String empCode) throws SQLException {
        PreparedStatement ps = null;
        String query = "INSERT INTO " + TableList.VHA_SIDE_TRAILER + "(\n"
                + "   moved_on, moved_by, state_cd, off_cd, appl_no, regn_no, link_regn_no, op_dt )\n"
                + "   SELECT current_timestamp + interval '1 second' as moved_on, ?, state_cd, off_cd, appl_no, regn_no, link_regn_no, op_dt \n"
                + "  FROM " + TableList.VA_SIDE_TRAILER + " where appl_no = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, empCode);
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public void sideTrailerApproval(TransactionManager tmgr, Owner_dobj owner_dobj) throws SQLException {
        PreparedStatement ps = null;
        String query = "INSERT INTO " + TableList.VH_SIDE_TRAILER + "(\n"
                + "            regn_no, link_regn_no, state_cd, off_cd, op_dt, moved_on, moved_by)\n"
                + "    SELECT regn_no, link_regn_no, state_cd, off_cd, op_dt,current_timestamp,?\n"
                + "  FROM " + TableList.VT_SIDE_TRAILER + " where regn_no = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, owner_dobj.getRegn_no());
        ps.executeUpdate();

        query = "INSERT INTO " + TableList.VT_SIDE_TRAILER + "(\n"
                + "   regn_no, link_regn_no, state_cd, off_cd, op_dt)\n"
                + "    SELECT regn_no, link_regn_no, state_cd, off_cd, current_timestamp\n"
                + "  FROM " + TableList.VA_SIDE_TRAILER + " where appl_no = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, owner_dobj.getAppl_no());
        ps.executeUpdate();
    }

    /**
     * @author Kartikey Singh
     */
    public void sideTrailerApproval(TransactionManager tmgr, Owner_dobj owner_dobj, String empCode) throws SQLException {
        PreparedStatement ps = null;
        String query = "INSERT INTO " + TableList.VH_SIDE_TRAILER + "(\n"
                + "            regn_no, link_regn_no, state_cd, off_cd, op_dt, moved_on, moved_by)\n"
                + "    SELECT regn_no, link_regn_no, state_cd, off_cd, op_dt,current_timestamp,?\n"
                + "  FROM " + TableList.VT_SIDE_TRAILER + " where regn_no = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, empCode);
        ps.setString(2, owner_dobj.getRegn_no());
        ps.executeUpdate();

        query = "INSERT INTO " + TableList.VT_SIDE_TRAILER + "(\n"
                + "   regn_no, link_regn_no, state_cd, off_cd, op_dt)\n"
                + "    SELECT regn_no, link_regn_no, state_cd, off_cd, current_timestamp\n"
                + "  FROM " + TableList.VA_SIDE_TRAILER + " where appl_no = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, owner_dobj.getAppl_no());
        ps.executeUpdate();
    }

    public static void insertHomologationDetails(TransactionManager tmgr, Owner_dobj owner_dobj, String applNo, String stateCd, int offCd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;

        try {
            if (owner_dobj != null && applNo != null && !applNo.equals(TableConstants.EMPTY_STRING)) {
                String selectSQL = "SELECT * FROM " + TableList.VA_HOMO_DETAILS + " where  appl_no = ? ";
                ps = tmgr.prepareStatement(selectSQL);
                ps.setString(1, applNo);
                ResultSet rs = tmgr.fetchDetachedRowSet_No_release();
                if (!rs.next()) {
                    sql = "INSERT INTO " + TableList.VA_HOMO_DETAILS + "( \n"
                            + " state_cd, off_cd, appl_no, chasi_no, eng_no, maker, maker_model, \n"
                            + " vch_purchase_as, hp, seat_cap, unld_wt, ld_wt, fuel, body_type, \n"
                            + " no_cyl, wheelbase, norms, cubic_cap, length, width, height, color, \n"
                            + " manu_mon, manu_yr, sale_amt, f_axle_descp, r_axle_descp, o_axle_descp, \n"
                            + " t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, t_axle_weight, \n"
                            + " op_dt, model_manu_loc, gcw)\n"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp, ?, ?) ";

                    ps = tmgr.prepareStatement(sql);

                    int i = 1;

                    ps.setString(i++, stateCd);
                    ps.setInt(i++, offCd);
                    ps.setString(i++, applNo);
                    ps.setString(i++, owner_dobj.getChasi_no());
                    ps.setString(i++, owner_dobj.getEng_no());
                    ps.setInt(i++, owner_dobj.getMaker());
                    ps.setString(i++, owner_dobj.getMaker_model());
                    ps.setString(i++, owner_dobj.getVch_purchase_as());
                    ps.setFloat(i++, owner_dobj.getHp() == null ? 0l : owner_dobj.getHp());
                    ps.setInt(i++, owner_dobj.getSeat_cap());
                    ps.setInt(i++, owner_dobj.getUnld_wt());
                    ps.setInt(i++, owner_dobj.getLd_wt());
                    ps.setInt(i++, owner_dobj.getFuel());
                    ps.setString(i++, owner_dobj.getBody_type());
                    ps.setInt(i++, owner_dobj.getNo_cyl());
                    ps.setInt(i++, owner_dobj.getWheelbase());
                    ps.setInt(i++, owner_dobj.getNorms());
                    ps.setFloat(i++, owner_dobj.getCubic_cap());
                    ps.setInt(i++, owner_dobj.getLength());
                    ps.setInt(i++, owner_dobj.getWidth());
                    ps.setInt(i++, owner_dobj.getHeight());
                    ps.setString(i++, owner_dobj.getColor());
                    ps.setInt(i++, owner_dobj.getManu_mon() == null ? 0 : owner_dobj.getManu_mon());
                    ps.setInt(i++, owner_dobj.getManu_yr() == null ? 0 : owner_dobj.getManu_yr());
                    ps.setInt(i++, owner_dobj.getSale_amt());
                    if (owner_dobj.getAxleDobj() != null) {
                        ps.setString(i++, owner_dobj.getAxleDobj().getTf_Front1());
                        ps.setString(i++, owner_dobj.getAxleDobj().getTf_Rear1());
                        ps.setString(i++, owner_dobj.getAxleDobj().getTf_Other1());
                        ps.setString(i++, owner_dobj.getAxleDobj().getTf_Tandem1());
                        ps.setInt(i++, owner_dobj.getAxleDobj().getTf_Front());
                        ps.setInt(i++, owner_dobj.getAxleDobj().getTf_Rear());
                        ps.setInt(i++, owner_dobj.getAxleDobj().getTf_Other());
                        ps.setInt(i++, owner_dobj.getAxleDobj().getTf_Tandem());
                    } else {
                        ps.setString(i++, TableConstants.EMPTY_STRING);
                        ps.setString(i++, TableConstants.EMPTY_STRING);
                        ps.setString(i++, TableConstants.EMPTY_STRING);
                        ps.setString(i++, TableConstants.EMPTY_STRING);
                        ps.setInt(i++, 0);
                        ps.setInt(i++, 0);
                        ps.setInt(i++, 0);
                        ps.setInt(i++, 0);

                    }
                    ps.setString(i++, owner_dobj.getModelManuLocCode());
                    ps.setInt(i++, owner_dobj.getGcw());
                    ps.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in inserting homo details");
        }
    }

    public void insertintoVhaHomologationDetails(TransactionManager tmgr, String applNo) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;

        try {

            sql = "INSERT INTO " + TableList.VHA_HOMO_DETAILS + "(moved_on, moved_by, state_cd, off_cd, appl_no, chasi_no, eng_no, \n"
                    + " maker, maker_model, vch_purchase_as, hp, seat_cap, unld_wt, ld_wt, \n"
                    + " fuel, body_type, no_cyl, wheelbase, norms, cubic_cap, length, \n"
                    + " width, height, color, manu_mon, manu_yr, sale_amt, f_axle_descp, \n"
                    + " r_axle_descp, o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight, \n"
                    + " o_axle_weight, t_axle_weight, op_dt, model_manu_loc, gcw) \n"
                    + " SELECT current_timestamp, ? , state_cd, off_cd, appl_no, chasi_no, eng_no,  maker, maker_model, vch_purchase_as, hp, seat_cap, unld_wt, ld_wt, \n"
                    + " fuel, body_type, no_cyl, wheelbase, norms, cubic_cap, length, width, height, color, manu_mon, manu_yr, sale_amt, f_axle_descp, \n "
                    + " r_axle_descp, o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, t_axle_weight, op_dt, model_manu_loc, gcw FROM " + TableList.VA_HOMO_DETAILS + " where appl_no = ? ";

            ps = tmgr.prepareStatement(sql);

            int i = 1;

            ps.setString(i++, Util.getEmpCode());
            ps.setString(i++, applNo);
            ps.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in inserting homo details");
        }
    }

    /**
     * @author Kartikey Singh
     */
    public void insertintoVhaHomologationDetails(TransactionManager tmgr, String applNo, String empCode) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;

        try {

            sql = "INSERT INTO " + TableList.VHA_HOMO_DETAILS + "(moved_on, moved_by, state_cd, off_cd, appl_no, chasi_no, eng_no, \n"
                    + " maker, maker_model, vch_purchase_as, hp, seat_cap, unld_wt, ld_wt, \n"
                    + " fuel, body_type, no_cyl, wheelbase, norms, cubic_cap, length, \n"
                    + " width, height, color, manu_mon, manu_yr, sale_amt, f_axle_descp, \n"
                    + " r_axle_descp, o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight, \n"
                    + " o_axle_weight, t_axle_weight, op_dt, model_manu_loc, gcw) \n"
                    + " SELECT current_timestamp, ? , state_cd, off_cd, appl_no, chasi_no, eng_no,  maker, maker_model, vch_purchase_as, hp, seat_cap, unld_wt, ld_wt, \n"
                    + " fuel, body_type, no_cyl, wheelbase, norms, cubic_cap, length, width, height, color, manu_mon, manu_yr, sale_amt, f_axle_descp, \n "
                    + " r_axle_descp, o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, t_axle_weight, op_dt, model_manu_loc, gcw FROM " + TableList.VA_HOMO_DETAILS + " where appl_no = ? ";

            ps = tmgr.prepareStatement(sql);

            int i = 1;

            ps.setString(i++, empCode);
            ps.setString(i++, applNo);
            ps.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in inserting homo details");
        }
    }

    /**
     * delete records from tables vt_axle,vt_insurance etc with no related
     * records in tables vt_owner (regn_no,state_cd,off_cd)
     *
     * @param regn_no
     * @param stateCd
     * @param offCd
     */
    public void deleteFromVtTables(String regn_no, String applNo) throws VahanException {
        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager("deleteFromVtTables");
            String sql = "INSERT INTO vh_axle "
                    + " SELECT state_cd, off_cd, regn_no, f_axle_descp, r_axle_descp, o_axle_descp, "
                    + "       t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, t_axle_weight, ?, "
                    + "       current_timestamp,?, no_of_axles "
                    + "  FROM vt_axle where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, regn_no);
            ps.setString(4, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_axle where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = " INSERT INTO vh_insurance SELECT regn_no, comp_cd, ins_type, ins_from, ins_upto, policy_no,current_timestamp,?, state_cd, off_cd, idv "
                    + "  FROM vt_insurance where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, regn_no);
            ps.setString(3, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_insurance where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "INSERT INTO vh_other_stat_veh "
                    + " SELECT state_cd, off_cd, new_regn_no, entry_dt, old_regn_no, old_rgst_auth, "
                    + " old_off_cd, old_state_cd, ncrb_ref, confirm_ref, noc_dt, noc_no, "
                    + "  op_dt,?,current_timestamp,?"
                    + "  FROM vt_other_stat_veh where new_regn_no = ? and (new_regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, regn_no);
            ps.setString(4, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_other_stat_veh where new_regn_no = ? and (new_regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "INSERT INTO vh_hypth "
                    + " SELECT state_cd, off_cd, regn_no, sr_no, hp_type, fncr_name, fncr_add1, "
                    + "       fncr_add2, fncr_add3, fncr_district, fncr_pincode, fncr_state, "
                    + "       from_dt, op_dt,?,current_timestamp,?"
                    + "  FROM vt_hypth where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, regn_no);
            ps.setString(4, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_hypth where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "INSERT INTO vh_import_veh "
                    + "SELECT state_cd, off_cd, regn_no, contry_code, dealer, place, foreign_regno, "
                    + "       manu_year, ?,current_timestamp,? "
                    + "  FROM vt_import_veh where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, regn_no);
            ps.setString(4, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_import_veh where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "INSERT INTO vh_owner_ex_army "
                    + " SELECT state_cd, off_cd, regn_no, voucher_no, voucher_dt, place, ?,current_timestamp,? "
                    + "  FROM vt_owner_ex_army where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, regn_no);
            ps.setString(4, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_owner_ex_army where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "INSERT INTO vh_owner_identification "
                    + "SELECT state_cd, off_cd, regn_no, mobile_no, email_id, pan_no, aadhar_no, "
                    + "       passport_no, ration_card_no, voter_id, dl_no, verified_on,current_timestamp,?, owner_ctg,dept_cd "
                    + "  FROM vt_owner_identification where  regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, regn_no);
            ps.setString(3, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_owner_identification where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "INSERT INTO vh_pucc "
                    + " SELECT state_cd, off_cd, regn_no, pucc_from, pucc_upto, pucc_centreno, "
                    + "       pucc_no, op_dt,?,current_timestamp,? "
                    + "  FROM vt_pucc where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, regn_no);
            ps.setString(4, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_pucc where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "INSERT INTO vh_tmp_regn_dtl "
                    + " SELECT state_cd, off_cd, regn_no, tmp_off_cd, regn_auth, tmp_state_cd, "
                    + "       tmp_regn_no, tmp_regn_dt, tmp_valid_upto, dealer_cd, ?,current_timestamp,? "
                    + "  FROM vt_tmp_regn_dtl where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, regn_no);
            ps.setString(4, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_tmp_regn_dtl where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "INSERT INTO vh_trailer "
                    + " SELECT state_cd, off_cd, regn_no, chasi_no, body_type, ld_wt, unld_wt, f_axle_descp, r_axle_descp, "
                    + "       o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, "
                    + "       t_axle_weight,  ?,current_timestamp,? "
                    + "  FROM vt_trailer where  regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, regn_no);
            ps.setString(4, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_trailer where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "INSERT INTO vh_cd_regn_dtl "
                    + "  SELECT state_cd, off_cd, regn_no, cd_regn_no, sale_dt,current_timestamp,? "
                    + "  FROM vt_cd_regn_dtl where  regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, regn_no);
            ps.setString(3, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_cd_regn_dtl where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "insert into vh_inspection "
                    + " SELECT current_timestamp,?, state_cd, off_cd, ?, regn_no, insp_dt, remark, op_dt, fit_off_cd1 "
                    + "  FROM vt_inspection where  regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, applNo);
            ps.setString(3, regn_no);
            ps.setString(4, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_inspection where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "INSERT INTO vh_retrofitting_dtls "
                    + " SELECT state_cd, off_cd, regn_no, kit_srno, kit_type, kit_manuf, kit_pucc_norms, "
                    + "       workshop, workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, "
                    + "       approval_no, approval_dt, op_dt,?,current_timestamp,? "
                    + "  FROM vt_retrofitting_dtls where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, regn_no);
            ps.setString(4, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_retrofitting_dtls where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "INSERT INTO vh_side_trailer "
                    + " SELECT regn_no, link_regn_no, state_cd, off_cd, op_dt,current_timestamp,? "
                    + "  FROM vt_side_trailer where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, regn_no);
            ps.setString(3, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_side_trailer where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            tmgr.commit();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Deleting Old data from Database");
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

    /**
     * delete records from tables vt_axle,vt_insurance etc with no related
     * records in tables vt_owner (regn_no,state_cd,off_cd)
     *
     */
    public void deleteFromVtTables(String regn_no, String applNo, String empCode) throws VahanException {
        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager("deleteFromVtTables");
            String sql = "INSERT INTO vh_axle "
                    + " SELECT state_cd, off_cd, regn_no, f_axle_descp, r_axle_descp, o_axle_descp, "
                    + "       t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, t_axle_weight, ?, "
                    + "       current_timestamp,?, no_of_axles "
                    + "  FROM vt_axle where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCode);
            ps.setString(3, regn_no);
            ps.setString(4, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_axle where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = " INSERT INTO vh_insurance SELECT regn_no, comp_cd, ins_type, ins_from, ins_upto, policy_no,current_timestamp,?, state_cd, off_cd, idv "
                    + "  FROM vt_insurance where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, regn_no);
            ps.setString(3, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_insurance where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "INSERT INTO vh_other_stat_veh "
                    + " SELECT state_cd, off_cd, new_regn_no, entry_dt, old_regn_no, old_rgst_auth, "
                    + " old_off_cd, old_state_cd, ncrb_ref, confirm_ref, noc_dt, noc_no, "
                    + "  op_dt,?,current_timestamp,?"
                    + "  FROM vt_other_stat_veh where new_regn_no = ? and (new_regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCode);
            ps.setString(3, regn_no);
            ps.setString(4, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_other_stat_veh where new_regn_no = ? and (new_regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "INSERT INTO vh_hypth "
                    + " SELECT state_cd, off_cd, regn_no, sr_no, hp_type, fncr_name, fncr_add1, "
                    + "       fncr_add2, fncr_add3, fncr_district, fncr_pincode, fncr_state, "
                    + "       from_dt, op_dt,?,current_timestamp,?"
                    + "  FROM vt_hypth where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCode);
            ps.setString(3, regn_no);
            ps.setString(4, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_hypth where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "INSERT INTO vh_import_veh "
                    + "SELECT state_cd, off_cd, regn_no, contry_code, dealer, place, foreign_regno, "
                    + "       manu_year, ?,current_timestamp,? "
                    + "  FROM vt_import_veh where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCode);
            ps.setString(3, regn_no);
            ps.setString(4, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_import_veh where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "INSERT INTO vh_owner_ex_army "
                    + " SELECT state_cd, off_cd, regn_no, voucher_no, voucher_dt, place, ?,current_timestamp,? "
                    + "  FROM vt_owner_ex_army where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCode);
            ps.setString(3, regn_no);
            ps.setString(4, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_owner_ex_army where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "INSERT INTO vh_owner_identification "
                    + "SELECT state_cd, off_cd, regn_no, mobile_no, email_id, pan_no, aadhar_no, "
                    + "       passport_no, ration_card_no, voter_id, dl_no, verified_on,current_timestamp,?, owner_ctg,dept_cd "
                    + "  FROM vt_owner_identification where  regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, regn_no);
            ps.setString(3, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_owner_identification where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "INSERT INTO vh_pucc "
                    + " SELECT state_cd, off_cd, regn_no, pucc_from, pucc_upto, pucc_centreno, "
                    + "       pucc_no, op_dt,?,current_timestamp,? "
                    + "  FROM vt_pucc where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCode);
            ps.setString(3, regn_no);
            ps.setString(4, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_pucc where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "INSERT INTO vh_tmp_regn_dtl "
                    + " SELECT state_cd, off_cd, regn_no, tmp_off_cd, regn_auth, tmp_state_cd, "
                    + "       tmp_regn_no, tmp_regn_dt, tmp_valid_upto, dealer_cd, ?,current_timestamp,? "
                    + "  FROM vt_tmp_regn_dtl where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCode);
            ps.setString(3, regn_no);
            ps.setString(4, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_tmp_regn_dtl where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "INSERT INTO vh_trailer "
                    + " SELECT state_cd, off_cd, regn_no, chasi_no, body_type, ld_wt, unld_wt, f_axle_descp, r_axle_descp, "
                    + "       o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, "
                    + "       t_axle_weight,  ?,current_timestamp,? "
                    + "  FROM vt_trailer where  regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCode);
            ps.setString(3, regn_no);
            ps.setString(4, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_trailer where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "INSERT INTO vh_cd_regn_dtl "
                    + "  SELECT state_cd, off_cd, regn_no, cd_regn_no, sale_dt,current_timestamp,? "
                    + "  FROM vt_cd_regn_dtl where  regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, regn_no);
            ps.setString(3, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_cd_regn_dtl where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "insert into vh_inspection "
                    + " SELECT current_timestamp,?, state_cd, off_cd, ?, regn_no, insp_dt, remark, op_dt, fit_off_cd1 "
                    + "  FROM vt_inspection where  regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, applNo);
            ps.setString(3, regn_no);
            ps.setString(4, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_inspection where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "INSERT INTO vh_retrofitting_dtls "
                    + " SELECT state_cd, off_cd, regn_no, kit_srno, kit_type, kit_manuf, kit_pucc_norms, "
                    + "       workshop, workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, "
                    + "       approval_no, approval_dt, op_dt,?,current_timestamp,? "
                    + "  FROM vt_retrofitting_dtls where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCode);
            ps.setString(3, regn_no);
            ps.setString(4, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_retrofitting_dtls where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            sql = "INSERT INTO vh_side_trailer "
                    + " SELECT regn_no, link_regn_no, state_cd, off_cd, op_dt,current_timestamp,? "
                    + "  FROM vt_side_trailer where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, regn_no);
            ps.setString(3, regn_no);
            ps.executeUpdate();

            sql = "Delete from vt_side_trailer where regn_no = ? and (regn_no, state_cd, off_cd) not in "
                    + "  (select regn_no, state_cd, off_cd from vt_owner where regn_no = ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, regn_no);
            ps.executeUpdate();

            tmgr.commit();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Deleting Old data from Database");
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

    public void deleteFromVtTablesForAuction(TransactionManager tmgr, String regn_no, String applNo, String stateCd, int offCd) throws VahanException {
        try {
            String sql = "INSERT INTO vh_axle "
                    + " SELECT state_cd, off_cd, regn_no, f_axle_descp, r_axle_descp, o_axle_descp, "
                    + "       t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, t_axle_weight, ?, "
                    + "       current_timestamp,?, no_of_axles "
                    + "  FROM vt_axle where regn_no = ?  and state_cd = ? and off_cd = ? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, regn_no);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_axle where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = " INSERT INTO vh_insurance SELECT regn_no, comp_cd, ins_type, ins_from, ins_upto, policy_no,current_timestamp,?, state_cd, off_cd, idv "
                    + "  FROM vt_insurance where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, regn_no);
            ps.setString(3, stateCd);
            ps.setInt(4, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_insurance where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "INSERT INTO vh_other_stat_veh "
                    + " SELECT state_cd, off_cd, new_regn_no, entry_dt, old_regn_no, old_rgst_auth, "
                    + " old_off_cd, old_state_cd, ncrb_ref, confirm_ref, noc_dt, noc_no, "
                    + "  op_dt,?,current_timestamp,?"
                    + "  FROM vt_other_stat_veh where new_regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, regn_no);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_other_stat_veh where new_regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "INSERT INTO vh_hypth "
                    + " SELECT state_cd, off_cd, regn_no, sr_no, hp_type, fncr_name, fncr_add1, "
                    + "       fncr_add2, fncr_add3, fncr_district, fncr_pincode, fncr_state, "
                    + "       from_dt, op_dt,?,current_timestamp,?"
                    + "  FROM vt_hypth where regn_no = ? and state_cd = ? and off_cd = ? ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, regn_no);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_hypth where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "INSERT INTO vh_import_veh "
                    + "SELECT state_cd, off_cd, regn_no, contry_code, dealer, place, foreign_regno, "
                    + "       manu_year, ?,current_timestamp,? "
                    + "  FROM vt_import_veh where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, regn_no);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_import_veh where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "INSERT INTO vh_owner_ex_army "
                    + " SELECT state_cd, off_cd, regn_no, voucher_no, voucher_dt, place, ?,current_timestamp,? "
                    + "  FROM vt_owner_ex_army where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, regn_no);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_owner_ex_army where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "INSERT INTO vh_owner_identification "
                    + "SELECT state_cd, off_cd, regn_no, mobile_no, email_id, pan_no, aadhar_no, "
                    + "       passport_no, ration_card_no, voter_id, dl_no, verified_on,current_timestamp,?, owner_ctg,dept_cd "
                    + "  FROM vt_owner_identification where  regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, regn_no);
            ps.setString(3, stateCd);
            ps.setInt(4, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_owner_identification where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "INSERT INTO vh_pucc "
                    + " SELECT state_cd, off_cd, regn_no, pucc_from, pucc_upto, pucc_centreno, "
                    + "       pucc_no, op_dt,?,current_timestamp,? "
                    + "  FROM vt_pucc where regn_no = ? and state_cd = ? and off_cd = ? ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, regn_no);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_pucc where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "INSERT INTO vh_tmp_regn_dtl "
                    + " SELECT state_cd, off_cd, regn_no, tmp_off_cd, regn_auth, tmp_state_cd, "
                    + "       tmp_regn_no, tmp_regn_dt, tmp_valid_upto, dealer_cd, ?,current_timestamp,? "
                    + "  FROM vt_tmp_regn_dtl where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, regn_no);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_tmp_regn_dtl where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "INSERT INTO vh_trailer "
                    + " SELECT state_cd, off_cd, regn_no, chasi_no, body_type, ld_wt, unld_wt, f_axle_descp, r_axle_descp, "
                    + "       o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, "
                    + "       t_axle_weight,  ?,current_timestamp,? "
                    + "  FROM vt_trailer where  regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, regn_no);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_trailer where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "INSERT INTO vh_cd_regn_dtl "
                    + "  SELECT state_cd, off_cd, regn_no, cd_regn_no, sale_dt,current_timestamp,? "
                    + "  FROM vt_cd_regn_dtl where  regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, regn_no);
            ps.setString(3, stateCd);
            ps.setInt(4, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_cd_regn_dtl where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "insert into vh_inspection "
                    + " SELECT current_timestamp,?, state_cd, off_cd, ?, regn_no, insp_dt, remark, op_dt, fit_off_cd1 "
                    + "  FROM vt_inspection where  regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, applNo);
            ps.setString(3, regn_no);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_inspection where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "INSERT INTO vh_retrofitting_dtls "
                    + " SELECT state_cd, off_cd, regn_no, kit_srno, kit_type, kit_manuf, kit_pucc_norms, "
                    + "       workshop, workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, "
                    + "       approval_no, approval_dt, op_dt,?,current_timestamp,? "
                    + "  FROM vt_retrofitting_dtls where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, Util.getEmpCode());
            ps.setString(3, regn_no);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_retrofitting_dtls where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "INSERT INTO vh_side_trailer "
                    + " SELECT regn_no, link_regn_no, state_cd, off_cd, op_dt,current_timestamp,? "
                    + "  FROM vt_side_trailer where regn_no = ? and state_cd = ? and off_cd = ? ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, Util.getEmpCode());
            ps.setString(2, regn_no);
            ps.setString(3, stateCd);
            ps.setInt(4, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_side_trailer where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Deleting Old data from Database");
        }
    }

    /*
     * @Kartikey Singh
     */
    public void deleteFromVtTablesForAuction(TransactionManager tmgr, String regn_no, String applNo, String stateCd, int offCd, String empCode) throws VahanException {
        try {
            String sql = "INSERT INTO vh_axle "
                    + " SELECT state_cd, off_cd, regn_no, f_axle_descp, r_axle_descp, o_axle_descp, "
                    + "       t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, t_axle_weight, ?, "
                    + "       current_timestamp,?, no_of_axles "
                    + "  FROM vt_axle where regn_no = ?  and state_cd = ? and off_cd = ? ";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCode);
            ps.setString(3, regn_no);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_axle where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = " INSERT INTO vh_insurance SELECT regn_no, comp_cd, ins_type, ins_from, ins_upto, policy_no,current_timestamp,?, state_cd, off_cd, idv "
                    + "  FROM vt_insurance where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, regn_no);
            ps.setString(3, stateCd);
            ps.setInt(4, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_insurance where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "INSERT INTO vh_other_stat_veh "
                    + " SELECT state_cd, off_cd, new_regn_no, entry_dt, old_regn_no, old_rgst_auth, "
                    + " old_off_cd, old_state_cd, ncrb_ref, confirm_ref, noc_dt, noc_no, "
                    + "  op_dt,?,current_timestamp,?"
                    + "  FROM vt_other_stat_veh where new_regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCode);
            ps.setString(3, regn_no);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_other_stat_veh where new_regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "INSERT INTO vh_hypth "
                    + " SELECT state_cd, off_cd, regn_no, sr_no, hp_type, fncr_name, fncr_add1, "
                    + "       fncr_add2, fncr_add3, fncr_district, fncr_pincode, fncr_state, "
                    + "       from_dt, op_dt,?,current_timestamp,?"
                    + "  FROM vt_hypth where regn_no = ? and state_cd = ? and off_cd = ? ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCode);
            ps.setString(3, regn_no);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_hypth where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "INSERT INTO vh_import_veh "
                    + "SELECT state_cd, off_cd, regn_no, contry_code, dealer, place, foreign_regno, "
                    + "       manu_year, ?,current_timestamp,? "
                    + "  FROM vt_import_veh where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCode);
            ps.setString(3, regn_no);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_import_veh where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "INSERT INTO vh_owner_ex_army "
                    + " SELECT state_cd, off_cd, regn_no, voucher_no, voucher_dt, place, ?,current_timestamp,? "
                    + "  FROM vt_owner_ex_army where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCode);
            ps.setString(3, regn_no);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_owner_ex_army where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "INSERT INTO vh_owner_identification "
                    + "SELECT state_cd, off_cd, regn_no, mobile_no, email_id, pan_no, aadhar_no, "
                    + "       passport_no, ration_card_no, voter_id, dl_no, verified_on,current_timestamp,?, owner_ctg,dept_cd "
                    + "  FROM vt_owner_identification where  regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, regn_no);
            ps.setString(3, stateCd);
            ps.setInt(4, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_owner_identification where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "INSERT INTO vh_pucc "
                    + " SELECT state_cd, off_cd, regn_no, pucc_from, pucc_upto, pucc_centreno, "
                    + "       pucc_no, op_dt,?,current_timestamp,? "
                    + "  FROM vt_pucc where regn_no = ? and state_cd = ? and off_cd = ? ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCode);
            ps.setString(3, regn_no);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_pucc where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "INSERT INTO vh_tmp_regn_dtl "
                    + " SELECT state_cd, off_cd, regn_no, tmp_off_cd, regn_auth, tmp_state_cd, "
                    + "       tmp_regn_no, tmp_regn_dt, tmp_valid_upto, dealer_cd, ?,current_timestamp,? "
                    + "  FROM vt_tmp_regn_dtl where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCode);
            ps.setString(3, regn_no);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_tmp_regn_dtl where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "INSERT INTO vh_trailer "
                    + " SELECT state_cd, off_cd, regn_no, chasi_no, body_type, ld_wt, unld_wt, f_axle_descp, r_axle_descp, "
                    + "       o_axle_descp, t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, "
                    + "       t_axle_weight,  ?,current_timestamp,? "
                    + "  FROM vt_trailer where  regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCode);
            ps.setString(3, regn_no);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_trailer where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "INSERT INTO vh_cd_regn_dtl "
                    + "  SELECT state_cd, off_cd, regn_no, cd_regn_no, sale_dt,current_timestamp,? "
                    + "  FROM vt_cd_regn_dtl where  regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, regn_no);
            ps.setString(3, stateCd);
            ps.setInt(4, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_cd_regn_dtl where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "insert into vh_inspection "
                    + " SELECT current_timestamp,?, state_cd, off_cd, ?, regn_no, insp_dt, remark, op_dt, fit_off_cd1 "
                    + "  FROM vt_inspection where  regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, applNo);
            ps.setString(3, regn_no);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_inspection where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "INSERT INTO vh_retrofitting_dtls "
                    + " SELECT state_cd, off_cd, regn_no, kit_srno, kit_type, kit_manuf, kit_pucc_norms, "
                    + "       workshop, workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, "
                    + "       approval_no, approval_dt, op_dt,?,current_timestamp,? "
                    + "  FROM vt_retrofitting_dtls where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCode);
            ps.setString(3, regn_no);
            ps.setString(4, stateCd);
            ps.setInt(5, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_retrofitting_dtls where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

            sql = "INSERT INTO vh_side_trailer "
                    + " SELECT regn_no, link_regn_no, state_cd, off_cd, op_dt,current_timestamp,? "
                    + "  FROM vt_side_trailer where regn_no = ? and state_cd = ? and off_cd = ? ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCode);
            ps.setString(2, regn_no);
            ps.setString(3, stateCd);
            ps.setInt(4, offCd);
            ps.executeUpdate();

            sql = "Delete from vt_side_trailer where regn_no = ? and state_cd = ? and off_cd = ? ";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regn_no);
            ps.setString(2, stateCd);
            ps.setInt(3, offCd);
            ps.executeUpdate();

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Deleting Old data from Database");
        }
    }

    public static void insertVaOwnerOther(TransactionManager tmgr, Owner_dobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = "INSERT INTO " + TableList.VA_OWNER_OTHER
                + "(state_cd,off_cd,appl_no,regn_no,push_back_seat, ordinary_seat,op_dt)"
                + "  VALUES (?,?,?,?,?,?,current_timestamp)";
        ps = tmgr.prepareStatement(sql);
        int i = 1;
        ps.setString(i++, Util.getUserStateCode());
        ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
        ps.setString(i++, dobj.getAppl_no());
        ps.setString(i++, dobj.getRegn_no());
        ps.setInt(i++, dobj.getPush_bk_seat());
        ps.setInt(i++, dobj.getOrdinary_seat());
        ps.executeUpdate();
    }

    /**
     * @author Kartikey Singh
     */
    public static void insertVaOwnerOther(TransactionManager tmgr, Owner_dobj dobj, String userStateCode,
            int offCode) throws SQLException {
        PreparedStatement ps = null;
        String sql = "INSERT INTO " + TableList.VA_OWNER_OTHER
                + "(state_cd,off_cd,appl_no,regn_no,push_back_seat, ordinary_seat,op_dt)"
                + "  VALUES (?,?,?,?,?,?,current_timestamp)";
        ps = tmgr.prepareStatement(sql);
        int i = 1;
        ps.setString(i++, userStateCode);
        ps.setInt(i++, offCode);
        ps.setString(i++, dobj.getAppl_no());
        ps.setString(i++, dobj.getRegn_no());
        ps.setInt(i++, dobj.getPush_bk_seat());
        ps.setInt(i++, dobj.getOrdinary_seat());
        ps.executeUpdate();
    }

    public static void insertVhaOwnerOther(TransactionManager tmgr, String appl_no) throws SQLException {
        PreparedStatement ps = null;
        String sql = "INSERT INTO  " + TableList.VHA_OWNER_OTHER
                + "  SELECT current_timestamp as moved_on, ? moved_by,state_cd,off_cd,appl_no, regn_no, "
                + "  push_back_seat,ordinary_seat,op_dt "
                + "  FROM  " + TableList.VA_OWNER_OTHER
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    /**
     * @author Kartikey Singh
     */
    public static void insertVhaOwnerOther(TransactionManager tmgr, String appl_no, String empCode) throws SQLException {
        PreparedStatement ps = null;
        String sql = "INSERT INTO  " + TableList.VHA_OWNER_OTHER
                + "  SELECT current_timestamp as moved_on, ? moved_by,state_cd,off_cd,appl_no, regn_no, "
                + "  push_back_seat,ordinary_seat,op_dt "
                + "  FROM  " + TableList.VA_OWNER_OTHER
                + " where appl_no=?";
        ps = tmgr.prepareStatement(sql);
        ps.setString(1, empCode);
        ps.setString(2, appl_no);
        ps.executeUpdate();
    }

    public static void updateVaOwnerOther(TransactionManager tmgr, Owner_dobj dobj) throws SQLException {
        PreparedStatement ps = null;
        String sql = "UPDATE " + TableList.VA_OWNER_OTHER
                + "   SET push_back_seat=?, ordinary_seat=?,op_dt = current_timestamp"
                + " WHERE appl_no=?";
        ps = tmgr.prepareStatement(sql);
        int i = 1;
        ps.setInt(i++, dobj.getPush_bk_seat());
        ps.setInt(i++, dobj.getOrdinary_seat());
        ps.setString(i++, dobj.getAppl_no());
        ps.executeUpdate();
    }

    public static void insertOrUpdateVaOwnerOther(TransactionManager tmgr, Owner_dobj owner_dobj) throws VahanException, SQLException {
        PreparedStatement ps = tmgr.prepareStatement("Select * from " + TableList.VA_OWNER_OTHER
                + " where appl_no=?");
        ps.setString(1, owner_dobj.getAppl_no());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            insertVhaOwnerOther(tmgr, owner_dobj.getAppl_no());
            updateVaOwnerOther(tmgr, owner_dobj);
        } else {
            insertVaOwnerOther(tmgr, owner_dobj);
        }
    }

    /**
     * @author Kartikey Singh
     */
    public static void insertOrUpdateVaOwnerOther(TransactionManager tmgr, Owner_dobj owner_dobj,
            String empCode, String userStateCode, int offCode) throws VahanException, SQLException {
        PreparedStatement ps = tmgr.prepareStatement("Select * from " + TableList.VA_OWNER_OTHER
                + " where appl_no=?");
        ps.setString(1, owner_dobj.getAppl_no());
        RowSet rs = tmgr.fetchDetachedRowSet_No_release();
        if (rs.next()) {
            insertVhaOwnerOther(tmgr, owner_dobj.getAppl_no(), empCode);
            updateVaOwnerOther(tmgr, owner_dobj);
        } else {
            insertVaOwnerOther(tmgr, owner_dobj, userStateCode, offCode);
        }
    }

    public void ownerOtherApproval(TransactionManager tmgr, Owner_dobj owner_dobj) throws SQLException, VahanException {
        PreparedStatement ps = null;
        String query = "INSERT INTO " + TableList.VH_OWNER_OTHER + "(\n"
                + "          state_cd, off_cd, regn_no, push_back_seat,ordinary_seat,op_dt ,moved_on, moved_by)\n"
                + "    SELECT state_cd, off_cd, regn_no, push_back_seat,ordinary_seat,op_dt,current_timestamp,?\n"
                + "  FROM " + TableList.VT_OWNER_OTHER + " where regn_no = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, owner_dobj.getRegn_no());
        ps.executeUpdate();
        query = "DELETE FROM " + TableList.VT_OWNER_OTHER + " WHERE regn_no=?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, owner_dobj.getRegn_no());
        ps.executeUpdate();
        query = "INSERT into  " + TableList.VT_OWNER_OTHER
                + " Select state_cd, off_cd, regn_no, push_back_seat,ordinary_seat,"
                + " current_timestamp as op_dt"
                + "  FROM " + TableList.VA_OWNER_OTHER
                + " where appl_no=? and regn_no != 'NEW'";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, owner_dobj.getAppl_no());
        ps.executeUpdate();
        query = "INSERT INTO  " + TableList.VHA_OWNER_OTHER
                + "  SELECT current_timestamp as moved_on, ? moved_by,state_cd,off_cd,appl_no, regn_no, "
                + "  push_back_seat,ordinary_seat,op_dt "
                + "  FROM  " + TableList.VA_OWNER_OTHER
                + " where appl_no=? and regn_no != 'NEW' ";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, owner_dobj.getAppl_no());
        ps.executeUpdate();
        query = "DELETE FROM " + TableList.VA_OWNER_OTHER + " WHERE regn_no=?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, owner_dobj.getRegn_no());
        ps.executeUpdate();
        //ServerUtil.validateQueryResult(ps.executeUpdate(), ps);
    }

    /**
     * @author Kartikey Singh
     */
    public void ownerOtherApproval(TransactionManager tmgr, Owner_dobj owner_dobj, String empCode) throws SQLException, VahanException {
        PreparedStatement ps = null;
        String query = "INSERT INTO " + TableList.VH_OWNER_OTHER + "(\n"
                + "          state_cd, off_cd, regn_no, push_back_seat,ordinary_seat,op_dt ,moved_on, moved_by)\n"
                + "    SELECT state_cd, off_cd, regn_no, push_back_seat,ordinary_seat,op_dt,current_timestamp,?\n"
                + "  FROM " + TableList.VT_OWNER_OTHER + " where regn_no = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, empCode);
        ps.setString(2, owner_dobj.getRegn_no());
        ps.executeUpdate();
        query = "DELETE FROM " + TableList.VT_OWNER_OTHER + " WHERE regn_no=?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, owner_dobj.getRegn_no());
        ps.executeUpdate();
        query = "INSERT into  " + TableList.VT_OWNER_OTHER
                + " Select state_cd, off_cd, regn_no, push_back_seat,ordinary_seat,"
                + " current_timestamp as op_dt"
                + "  FROM " + TableList.VA_OWNER_OTHER
                + " where appl_no=? and regn_no != 'NEW'";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, owner_dobj.getAppl_no());
        ps.executeUpdate();
        query = "INSERT INTO  " + TableList.VHA_OWNER_OTHER
                + "  SELECT current_timestamp as moved_on, ? moved_by,state_cd,off_cd,appl_no, regn_no, "
                + "  push_back_seat,ordinary_seat,op_dt "
                + "  FROM  " + TableList.VA_OWNER_OTHER
                + " where appl_no=? and regn_no != 'NEW' ";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, empCode);
        ps.setString(2, owner_dobj.getAppl_no());
        ps.executeUpdate();
        query = "DELETE FROM " + TableList.VA_OWNER_OTHER + " WHERE regn_no=?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, owner_dobj.getRegn_no());
        ps.executeUpdate();
        //ServerUtil.validateQueryResult(ps.executeUpdate(), ps);
    }

    public void moveFromVtToVhOwnerOther(TransactionManager tmgr, Owner_dobj owner_dobj) throws SQLException {
        PreparedStatement ps = null;
        String query = "INSERT INTO " + TableList.VH_OWNER_OTHER + "(\n"
                + "          state_cd, off_cd, regn_no, push_back_seat,ordinary_seat,op_dt ,moved_on, moved_by)\n"
                + "    SELECT state_cd, off_cd, regn_no, push_back_seat,ordinary_seat,op_dt,current_timestamp,?\n"
                + "  FROM " + TableList.VT_OWNER_OTHER + " where regn_no = ?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, Util.getEmpCode());
        ps.setString(2, owner_dobj.getRegn_no());
        ps.executeUpdate();
        query = "DELETE FROM " + TableList.VT_OWNER_OTHER + " WHERE regn_no=?";
        ps = tmgr.prepareStatement(query);
        ps.setString(1, owner_dobj.getRegn_no());
        ps.executeUpdate();
    }

    public void updateOffCodeInRegnTables(TransactionManager tmgr, String applNo, String regnNo, String stateCd, int newOffCd, String empCd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            //remove duplicate record
            removeDuplicateInconsistantRecords(tmgr, regnNo, applNo, empCd);

            sql = "INSERT INTO " + TableList.VH_OWNER
                    + " (state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name,"
                    + " f_name, c_add1, c_add2, c_add3, c_district, c_pincode, c_state,"
                    + " p_add1, p_add2, p_add3, p_district, p_pincode, p_state, owner_cd,"
                    + " regn_type, vh_class, chasi_no, eng_no, maker, maker_model, body_type,"
                    + " no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt,"
                    + " gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, cubic_cap,"
                    + " floor_area, ac_fitted, audio_fitted, video_fitted, vch_purchase_as,"
                    + " vch_catg, dealer_cd, sale_amt, laser_code, garage_add, length,"
                    + " width, height, regn_upto, fit_upto, annual_income, imported_vch,"
                    + " other_criteria, status, op_dt, appl_no, moved_on, moved_by,moved_status)"
                    + " SELECT state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name,"
                    + " f_name, c_add1, c_add2, c_add3, c_district, c_pincode,"
                    + " c_state, p_add1, p_add2, p_add3, p_district, p_pincode, p_state,"
                    + " owner_cd, regn_type, vh_class, chasi_no, eng_no, maker, maker_model,"
                    + " body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt,"
                    + " ld_wt, gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase,"
                    + " cubic_cap, floor_area, ac_fitted, audio_fitted, video_fitted,"
                    + " vch_purchase_as, vch_catg, dealer_cd, sale_amt, laser_code, garage_add,"
                    + " length, width, height, regn_upto, fit_upto, annual_income,"
                    + " imported_vch, other_criteria, status, op_dt, ? as appl_no, current_timestamp as moved_on,"
                    + " ? as moved_by,? "
                    + "  FROM " + TableList.VT_OWNER + " WHERE regn_no = ? and state_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCd);
            ps.setString(3, TableConstants.VH_MOVED_STATUS_UPDATE);
            ps.setString(4, regnNo);
            ps.setString(5, stateCd);
            ps.executeUpdate();

            sql = "UPDATE " + TableList.VT_OWNER + " SET off_cd=?,op_dt=current_timestamp WHERE regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, newOffCd);
            ps.setString(2, regnNo);
            ps.setString(3, stateCd);
            ps.executeUpdate();

            sql = "INSERT INTO " + TableList.VH_OWNER_IDENTIFICATION
                    + " SELECT state_cd,off_cd,regn_no, mobile_no, email_id, pan_no, aadhar_no, passport_no,"
                    + "            ration_card_no, voter_id, dl_no, verified_on, current_timestamp as moved_on, ? moved_by,owner_ctg,dept_cd "
                    + "   FROM " + TableList.VT_OWNER_IDENTIFICATION
                    + " WHERE regn_no = ? and state_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCd);
            ps.setString(2, regnNo);
            ps.setString(3, stateCd);
            ps.executeUpdate();

            sql = "UPDATE " + TableList.VT_OWNER_IDENTIFICATION + " SET off_cd=?,verified_on=current_timestamp WHERE regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, newOffCd);
            ps.setString(2, regnNo);
            ps.setString(3, stateCd);
            ps.executeUpdate();

            sql = "INSERT INTO " + TableList.VH_INSURANCE
                    + " SELECT regn_no, comp_cd, ins_type, ins_from, ins_upto,"
                    + "        policy_no,current_timestamp,?,state_cd, off_cd,idv"
                    + " FROM " + TableList.VT_INSURANCE
                    + " WHERE regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCd);
            ps.setString(2, regnNo);
            ps.setString(3, stateCd);
            ps.executeUpdate();

            sql = "UPDATE " + TableList.VT_INSURANCE + " SET off_cd=?,op_dt=current_timestamp WHERE regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, newOffCd);
            ps.setString(2, regnNo);
            ps.setString(3, stateCd);
            ps.executeUpdate();

            sql = "INSERT INTO " + TableList.VH_HYPTH
                    + " SELECT state_cd, off_cd, regn_no, sr_no, hp_type, fncr_name, fncr_add1,"
                    + "        fncr_add2, fncr_add3, fncr_district, fncr_pincode, fncr_state,"
                    + "        from_dt, op_dt, ? as appl_no, current_timestamp as moved_on, ? as moved_by "
                    + "  FROM " + TableList.VT_HYPTH + " WHERE regn_no = ? and state_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCd);
            ps.setString(3, regnNo);
            ps.setString(4, stateCd);
            ps.executeUpdate();

            sql = "UPDATE " + TableList.VT_HYPTH + " SET off_cd=?,op_dt=current_timestamp WHERE regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, newOffCd);
            ps.setString(2, regnNo);
            ps.setString(3, stateCd);
            ps.executeUpdate();

            sql = " Insert into  " + TableList.VH_FITNESS
                    + " SELECT state_cd, off_cd, ?, regn_no, chasi_no, fit_chk_dt, fit_chk_tm, "
                    + "       fit_result, fit_valid_to, fit_nid, fit_off_cd1, fit_off_cd2, "
                    + "       remark, fare_mtr_no, speedgov_no, speedgov_compname, brake, steer, "
                    + "       susp, engin, tyre, horn, lamp, embo, speed, paint, wiper, dimen, "
                    + "       body, fare, elec, finis, road, poll, transm, glas, emis, rear, "
                    + "       others, op_dt,current_timestamp,? "
                    + "  FROM " + TableList.VT_FITNESS + " where regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCd);
            ps.setString(3, regnNo);
            ps.setString(4, stateCd);
            ps.executeUpdate();

            sql = "UPDATE " + TableList.VT_FITNESS + " SET off_cd=?,op_dt=current_timestamp WHERE regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, newOffCd);
            ps.setString(2, regnNo);
            ps.setString(3, stateCd);
            ps.executeUpdate();

            sql = " INSERT INTO " + TableList.VH_SPEED_GOVERNOR
                    + " SELECT current_timestamp,?,"
                    + " state_cd, off_cd, regn_no,?, sg_no, sg_fitted_on, sg_fitted_at,"
                    + " op_dt, emp_cd,sg_type,sg_type_approval_no,sg_test_report_no,sg_fitment_cert_no"
                    + " FROM " + TableList.VT_SPEED_GOVERNOR
                    + " WHERE state_cd=? and regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCd);
            ps.setString(2, applNo);
            ps.setString(3, stateCd);
            ps.setString(4, regnNo);
            ps.executeUpdate();

            sql = "UPDATE " + TableList.VT_SPEED_GOVERNOR + " SET off_cd=?,op_dt=current_timestamp WHERE regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, newOffCd);
            ps.setString(2, regnNo);
            ps.setString(3, stateCd);
            ps.executeUpdate();

            sql = " INSERT INTO " + TableList.VH_REFLECTIVE_TAPE
                    + " SELECT current_timestamp, ?, state_cd, off_cd, regn_no, certificate_no, fitment_date, manu_name,current_timestamp as op_dt\n"
                    + "  FROM " + TableList.VT_REFLECTIVE_TAPE
                    + "  WHERE state_cd=? and regn_no=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCd);
            ps.setString(2, stateCd);
            ps.setString(3, regnNo);
            ps.executeUpdate();

            sql = "UPDATE " + TableList.VT_REFLECTIVE_TAPE + " SET off_cd=?,op_dt=current_timestamp WHERE regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, newOffCd);
            ps.setString(2, regnNo);
            ps.setString(3, stateCd);
            ps.executeUpdate();

            sql = "INSERT INTO " + TableList.VH_OWNER_EX_ARMY
                    + "    SELECT  state_cd, off_cd, regn_no, voucher_no, "
                    + "            voucher_dt, place, ? as appl_no, current_timestamp as moved_on, ? as moved_by "
                    + "      FROM " + TableList.VT_OWNER_EX_ARMY + " WHERE regn_no = ? and state_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCd);
            ps.setString(3, regnNo);
            ps.setString(4, stateCd);
            ps.executeUpdate();

            sql = "UPDATE " + TableList.VT_OWNER_EX_ARMY + " SET off_cd=?,op_dt=current_timestamp WHERE regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, newOffCd);
            ps.setString(2, regnNo);
            ps.setString(3, stateCd);
            ps.executeUpdate();

            sql = "INSERT INTO " + TableList.VH_IMPORT_VEH
                    + "    SELECT  state_cd, off_cd, regn_no, contry_code, dealer, place, foreign_regno, "
                    + "            manu_year, ? as appl_no, current_timestamp as moved_on, ? as moved_by "
                    + "      FROM " + TableList.VT_IMPORT_VEH + " WHERE regn_no = ? and state_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCd);
            ps.setString(3, regnNo);
            ps.setString(4, stateCd);
            ps.executeUpdate();

            sql = "UPDATE " + TableList.VT_IMPORT_VEH + " SET off_cd=?,op_dt=current_timestamp WHERE regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, newOffCd);
            ps.setString(2, regnNo);
            ps.setString(3, stateCd);
            ps.executeUpdate();

            sql = "INSERT INTO " + TableList.VH_AXLE
                    + " SELECT state_cd, off_cd, regn_no, f_axle_descp, r_axle_descp, o_axle_descp, "
                    + "       t_axle_descp, f_axle_weight, r_axle_weight, o_axle_weight, t_axle_weight, "
                    + "       ? as appl_no, current_timestamp as moved_on, ? as moved_by,no_of_axles,"
                    + "       r_overhang, f_axle_tyre, r_axle_tyre, o_axle_tyre, t_axle_tyre "
                    + "  FROM " + TableList.VT_AXLE + " WHERE regn_no = ? and state_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCd);
            ps.setString(3, regnNo);
            ps.setString(4, stateCd);
            ps.executeUpdate();

            sql = "UPDATE " + TableList.VT_AXLE + " SET off_cd=?,op_dt=current_timestamp WHERE regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, newOffCd);
            ps.setString(2, regnNo);
            ps.setString(3, stateCd);
            ps.executeUpdate();

            sql = "INSERT INTO " + TableList.VH_RETROFITTING_DTLS
                    + " SELECT state_cd, off_cd, regn_no, kit_srno, kit_type, kit_manuf, kit_pucc_norms,"
                    + "        workshop, workshop_lic_no, fitment_dt, hydro_test_dt, cyl_srno, "
                    + "        approval_no, approval_dt, op_dt, ? as appl_no, current_timestamp as moved_on, ? as moved_by "
                    + "  FROM " + TableList.VT_RETROFITTING_DTLS + " WHERE regn_no = ? and state_cd = ?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCd);
            ps.setString(3, regnNo);
            ps.setString(4, stateCd);
            ps.executeUpdate();

            sql = "UPDATE " + TableList.VT_RETROFITTING_DTLS + " SET off_cd=?,op_dt=current_timestamp WHERE regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, newOffCd);
            ps.setString(2, regnNo);
            ps.setString(3, stateCd);
            ps.executeUpdate();

            sql = "INSERT INTO " + TableList.VH_PUCC
                    + " SELECT state_cd, off_cd, regn_no, pucc_from, pucc_upto, pucc_centreno,"
                    + "       pucc_no, op_dt, ? as appl_no, current_timestamp as moved_on, ? as moved_by "
                    + "  FROM " + TableList.VT_PUCC + " WHERE regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCd);
            ps.setString(3, regnNo);
            ps.setString(4, stateCd);
            ps.executeUpdate();

            sql = "UPDATE " + TableList.VT_PUCC + " SET off_cd=?,op_dt=current_timestamp WHERE regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, newOffCd);
            ps.setString(2, regnNo);
            ps.setString(3, stateCd);
            ps.executeUpdate();
            sql = "INSERT INTO " + TableList.VHA_INWARD_OTH_OFFICE + "(moved_on, moved_by, appl_no, state_cd_fr, off_cd_fr, regn_no, emp_cd, op_dt)"
                    + "SELECT current_timestamp,? ,appl_no, state_cd_fr, off_cd_fr, regn_no, emp_cd, op_dt  "
                    + "FROM va_inward_oth_office where appl_no=? and state_cd_fr=? ";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, empCd);
            ps.setString(2, applNo);
            ps.setString(3, stateCd);
            ps.executeUpdate();

            sql = "UPDATE " + TableList.VA_INWARD_OTH_OFFICE + " SET off_cd_fr=?,op_dt=current_timestamp WHERE appl_no=? and state_cd_fr=?";
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, newOffCd);
            ps.setString(2, applNo);
            ps.setString(3, stateCd);
            ps.executeUpdate();

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "" + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public void removeDuplicateInconsistantRecords(TransactionManager tmgr, String regnNo, String applNo, String empCd) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "delete from " + TableList.VT_OWNER_IDENTIFICATION + " where regn_no =? and (state_cd,off_cd,regn_no) not in (select state_cd,off_cd,regn_no from vt_owner where regn_no=?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, regnNo);
            ps.executeUpdate();

            sql = "delete from " + TableList.VT_INSURANCE + " where regn_no =? and (state_cd,off_cd,regn_no) not in (select state_cd,off_cd,regn_no from vt_owner where regn_no=?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, regnNo);
            ps.executeUpdate();

            sql = " Insert into  " + TableList.VH_FITNESS
                    + " SELECT state_cd, off_cd, ?, regn_no, chasi_no, fit_chk_dt, fit_chk_tm, "
                    + " fit_result, fit_valid_to, fit_nid, fit_off_cd1, fit_off_cd2, "
                    + " remark, fare_mtr_no, speedgov_no, speedgov_compname, brake, steer, "
                    + " susp, engin, tyre, horn, lamp, embo, speed, paint, wiper, dimen, "
                    + " body, fare, elec, finis, road, poll, transm, glas, emis, rear, "
                    + " others, op_dt,current_timestamp,? "
                    + " FROM " + TableList.VT_FITNESS + " a where regn_no =? and fit_valid_to < "
                    + " (select max(fit_valid_to) from " + TableList.VT_FITNESS + " where (state_cd,regn_no) in (select state_cd,regn_no from " + TableList.VT_OWNER + " where regn_no=?))";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCd);
            ps.setString(3, regnNo);
            ps.setString(4, regnNo);
            ps.executeUpdate();

            sql = "delete from " + TableList.VT_FITNESS + "  a where regn_no =? and fit_valid_to < "
                    + " (select max(fit_valid_to) from " + TableList.VT_FITNESS + " where (state_cd,regn_no) in (select state_cd,regn_no from " + TableList.VT_OWNER + " where regn_no=?))";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, regnNo);
            ps.executeUpdate();

            sql = "select count(1) as count from " + TableList.VT_FITNESS + " where regn_no=? and state_cd=?";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, Util.getUserStateCode());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            int counter = 0;
            if (rs.next()) {
                counter = rs.getInt("count");
            }
            if (counter > 1) {
                sql = " Insert into  " + TableList.VH_FITNESS
                        + " SELECT state_cd, off_cd, ?, regn_no, chasi_no, fit_chk_dt, fit_chk_tm, "
                        + " fit_result, fit_valid_to, fit_nid, fit_off_cd1, fit_off_cd2, "
                        + " remark, fare_mtr_no, speedgov_no, speedgov_compname, brake, steer, "
                        + " susp, engin, tyre, horn, lamp, embo, speed, paint, wiper, dimen, "
                        + " body, fare, elec, finis, road, poll, transm, glas, emis, rear, "
                        + " others, op_dt,current_timestamp,? "
                        + " FROM " + TableList.VT_FITNESS + " a where regn_no =? and (state_cd,off_cd,regn_no) not in (select state_cd,off_cd,regn_no from vt_owner where regn_no=?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, applNo);
                ps.setString(2, empCd);
                ps.setString(3, regnNo);
                ps.setString(4, regnNo);
                ps.executeUpdate();

                sql = "delete from " + TableList.VT_FITNESS + " where regn_no =? and (state_cd,off_cd,regn_no) not in (select state_cd,off_cd,regn_no from vt_owner where regn_no=?)";
                ps = tmgr.prepareStatement(sql);
                ps.setString(1, regnNo);
                ps.setString(2, regnNo);
                ps.executeUpdate();
            }

            sql = "delete from " + TableList.VT_AXLE + " where regn_no =? and (state_cd,off_cd,regn_no) not in (select state_cd,off_cd,regn_no from vt_owner where regn_no=?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, regnNo);
            ps.executeUpdate();

            sql = "delete from " + TableList.VT_RETROFITTING_DTLS + " where regn_no =? and (state_cd,off_cd,regn_no) not in (select state_cd,off_cd,regn_no from vt_owner where regn_no=?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, regnNo);
            ps.executeUpdate();

            sql = "delete from " + TableList.VT_TMP_REGN_DTL + " where regn_no =? and (state_cd,off_cd,regn_no) not in (select state_cd,off_cd,regn_no from vt_owner where regn_no=?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, regnNo);
            ps.executeUpdate();

            sql = "delete from " + TableList.VT_INSPECTION + " where regn_no =? and (state_cd,off_cd,regn_no) not in (select state_cd,off_cd,regn_no from vt_owner where regn_no=?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, regnNo);
            ps.executeUpdate();

            sql = "INSERT INTO " + TableList.VH_HYPTH
                    + " SELECT state_cd, off_cd, regn_no, sr_no, hp_type, fncr_name, fncr_add1,"
                    + " fncr_add2, fncr_add3, fncr_district, fncr_pincode, fncr_state,"
                    + " from_dt, op_dt,?,current_timestamp,?"
                    + " FROM " + TableList.VT_HYPTH + " where regn_no = ? and ? not in (select appl_no from va_details where regn_no = ? and state_cd = ? and off_cd = ? and pur_cd = 6) and (state_cd,off_cd,regn_no) not in (select state_cd,off_cd,regn_no from vt_owner where regn_no= ?)";

            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNo);
            ps.setString(2, empCd);
            ps.setString(3, regnNo);
            ps.setString(4, applNo);
            ps.setString(5, regnNo);
            ps.setString(6, Util.getUserStateCode());
            ps.setInt(7, Util.getUserLoginOffCode());
            ps.setString(8, regnNo);
            ps.executeUpdate();

            sql = "delete from " + TableList.VT_HYPTH + " where regn_no = ? and ? not in (select appl_no from va_details where regn_no = ? and state_cd = ? and off_cd = ? and pur_cd = 6) and (state_cd,off_cd,regn_no) not in (select state_cd,off_cd,regn_no from vt_owner where regn_no= ?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, applNo);
            ps.setString(3, regnNo);
            ps.setString(4, Util.getUserStateCode());
            ps.setInt(5, Util.getUserLoginOffCode());
            ps.setString(6, regnNo);
            ps.executeUpdate();

            sql = "delete from " + TableList.VT_PUCC + " where regn_no =? and (state_cd,off_cd,regn_no) not in (select state_cd,off_cd,regn_no from vt_owner where regn_no=?)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, regnNo);
            ps.setString(2, regnNo);
            ps.executeUpdate();

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "" + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public static void insertOrUpdateVaPermitNewRegn(TransactionManager tmgr, Owner_dobj owner_dobj) throws VahanException {
        try {
            String qry = "select * from va_permit_new_regn where appl_no=?";
            PreparedStatement ps = tmgr.prepareStatement(qry);
            ps.setString(1, owner_dobj.getAppl_no());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                ServerUtil.insertIntoVhaTable(tmgr, owner_dobj.getAppl_no(), Util.getEmpCode(), "vha_permit_new_regn", "va_permit_new_regn");
                qry = "UPDATE vahan4.va_permit_new_regn\n"
                        + "   SET pmt_type=?, pmt_catg=?, service_type=?, \n"
                        + "       emp_code=?, op_dt=current_timestamp,region_covered=?\n"
                        + " WHERE appl_no=?";
                ps = tmgr.prepareStatement(qry);
                ps.setInt(1, owner_dobj.getPmt_type());
                ps.setInt(2, owner_dobj.getPmt_catg());
                if (owner_dobj.getServicesType() != null && !owner_dobj.getServicesType().equals("")) {
                    ps.setInt(3, Integer.parseInt(owner_dobj.getServicesType()));
                } else {
                    ps.setInt(3, -1);
                }
                ps.setString(4, Util.getEmpCode());
                ps.setString(5, owner_dobj.getRegion_covered_str());
                ps.setString(6, owner_dobj.getAppl_no());
                ps.executeUpdate();
            } else {
                insertIntoVaPermitNew(tmgr, owner_dobj);
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "" + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }

    }

    /**
     * @author Kartikey Singh
     */
    public static void insertOrUpdateVaPermitNewRegn(TransactionManager tmgr, Owner_dobj owner_dobj, String empCode) throws VahanException {
        try {
            String qry = "select * from va_permit_new_regn where appl_no=?";
            PreparedStatement ps = tmgr.prepareStatement(qry);
            ps.setString(1, owner_dobj.getAppl_no());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                ServerUtility.insertIntoVhaTable(tmgr, owner_dobj.getAppl_no(), empCode, "vha_permit_new_regn", "va_permit_new_regn");
                qry = "UPDATE vahan4.va_permit_new_regn\n"
                        + "   SET pmt_type=?, pmt_catg=?, service_type=?, \n"
                        + "       emp_code=?, op_dt=current_timestamp,region_covered=?\n"
                        + " WHERE appl_no=?";
                ps = tmgr.prepareStatement(qry);
                ps.setInt(1, owner_dobj.getPmt_type());
                ps.setInt(2, owner_dobj.getPmt_catg());
                if (owner_dobj.getServicesType() != null && !owner_dobj.getServicesType().equals("")) {
                    ps.setInt(3, Integer.parseInt(owner_dobj.getServicesType()));
                } else {
                    ps.setInt(3, -1);
                }
                ps.setString(4, empCode);
                ps.setString(5, owner_dobj.getRegion_covered_str());
                ps.setString(6, owner_dobj.getAppl_no());
                ps.executeUpdate();
            } else {
                insertIntoVaPermitNew(tmgr, owner_dobj, empCode);
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "" + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }

    }

    public static void insertIntoVaPermitNew(TransactionManager tmgr, Owner_dobj owner_dobj) throws VahanException {
        try {
            String qry = "INSERT INTO vahan4.va_permit_new_regn(\n"
                    + "            state_cd, off_cd, appl_no, pmt_type, pmt_catg, service_type, \n"
                    + "            emp_code, op_dt, region_covered)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, \n"
                    + "            ?, current_timestamp,?);";
            PreparedStatement ps = tmgr.prepareStatement(qry);
            ps.setString(1, owner_dobj.getState_cd());
            ps.setInt(2, owner_dobj.getOff_cd());
            ps.setString(3, owner_dobj.getAppl_no());
            ps.setInt(4, owner_dobj.getPmt_type());
            ps.setInt(5, owner_dobj.getPmt_catg());
            if (owner_dobj.getServicesType() != null && !owner_dobj.getServicesType().equals("")) {
                ps.setInt(6, Integer.parseInt(owner_dobj.getServicesType()));
            } else {
                ps.setInt(6, -1);
            }
            ps.setString(7, Util.getEmpCode());
            ps.setString(8, owner_dobj.getRegion_covered_str());
            ps.executeUpdate();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "" + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    /**
     * @author Kartikey Singh
     */
    public static void insertIntoVaPermitNew(TransactionManager tmgr, Owner_dobj owner_dobj, String empCode) throws VahanException {
        try {
            String qry = "INSERT INTO vahan4.va_permit_new_regn(\n"
                    + "            state_cd, off_cd, appl_no, pmt_type, pmt_catg, service_type, \n"
                    + "            emp_code, op_dt, region_covered)\n"
                    + "    VALUES (?, ?, ?, ?, ?, ?, \n"
                    + "            ?, current_timestamp,?);";
            PreparedStatement ps = tmgr.prepareStatement(qry);
            ps.setString(1, owner_dobj.getState_cd());
            ps.setInt(2, owner_dobj.getOff_cd());
            ps.setString(3, owner_dobj.getAppl_no());
            ps.setInt(4, owner_dobj.getPmt_type());
            ps.setInt(5, owner_dobj.getPmt_catg());
            if (owner_dobj.getServicesType() != null && !owner_dobj.getServicesType().equals("")) {
                ps.setInt(6, Integer.parseInt(owner_dobj.getServicesType()));
            } else {
                ps.setInt(6, -1);
            }
            ps.setString(7, empCode);
            ps.setString(8, owner_dobj.getRegion_covered_str());
            ps.executeUpdate();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "" + ex.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
    }

    public String getServiceTypeFromVaPermitNew(Owner_dobj owner_dobj) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        try {

            tmgr = new TransactionManagerReadOnly("In method getServiceTypeFromVaPermitNew");
            String qry = "select * from va_permit_new_regn where appl_no=?";
            PreparedStatement ps = tmgr.prepareStatement(qry);
            ps.setString(1, owner_dobj.getAppl_no());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                return rs.getString("service_type");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "" + ex.getStackTrace()[0]);
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
        return "";
    }

    public static void UpdatePmtDtlsIntoVaOwner(TransactionManager tmgr, Owner_dobj owner_dobj) throws VahanException {

        PreparedStatement ps = null;
        String sql = null;
        try {
            sql = "UPDATE " + TableList.VA_OWNER
                    + "   SET  pmt_type=?,pmt_catg=?,op_dt=current_timestamp"
                    + " WHERE appl_no=?";
            ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setInt(i++, owner_dobj.getPmt_type());
            ps.setInt(i++, owner_dobj.getPmt_catg());
            ps.setString(i++, owner_dobj.getAppl_no());
            ps.executeUpdate();
        } catch (SQLException sqle) {
            LOGGER.error(sqle.toString() + " " + sqle.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong During Saving of Data into Database. Please Contact to the System Administrator");
        }

    }

    public static void insertIntoVtOwnerNOC(TransactionManager tmgr, Status_dobj dobj, String stateCd, int offCd, String oldRegn) throws SQLException {

        PreparedStatement ps = null;
        int pos = 1;
        String sql = "INSERT INTO " + TableList.VT_OWNER_NOC
                + " (state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name,"
                + " f_name, c_add1, c_add2, c_add3, c_district, c_pincode, c_state,"
                + " p_add1, p_add2, p_add3, p_district, p_pincode, p_state, owner_cd,"
                + " regn_type, vh_class, chasi_no, eng_no, maker, maker_model, body_type,"
                + " no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, ld_wt,"
                + " gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, cubic_cap,"
                + " floor_area, ac_fitted, audio_fitted, video_fitted, vch_purchase_as,"
                + " vch_catg, dealer_cd, sale_amt, laser_code, garage_add, length,"
                + " width, height, regn_upto, fit_upto, annual_income, imported_vch,"
                + " other_criteria, status, op_dt, appl_no, moved_on, moved_by)"
                + " SELECT state_cd, off_cd, regn_no, regn_dt, purchase_dt, owner_sr, owner_name, \n"
                + " f_name, c_add1, c_add2, c_add3, c_district, c_pincode, \n"
                + " c_state, p_add1, p_add2, p_add3, p_district, p_pincode, p_state, \n"
                + " owner_cd, regn_type, vh_class, chasi_no, eng_no, maker, maker_model, \n"
                + " body_type, no_cyl, hp, seat_cap, stand_cap, sleeper_cap, unld_wt, \n"
                + " ld_wt, gcw, fuel, color, manu_mon, manu_yr, norms, wheelbase, \n"
                + " cubic_cap, floor_area, ac_fitted, audio_fitted, video_fitted, \n"
                + " vch_purchase_as, vch_catg, dealer_cd, sale_amt, laser_code, garage_add, \n"
                + " length, width, height, regn_upto, fit_upto, annual_income, \n"
                + " imported_vch, other_criteria, status, op_dt, ? as appl_no, current_timestamp as moved_on, \n"
                + " ? as moved_by "
                + "  FROM " + TableList.VT_OWNER + " WHERE regn_no = ? and state_cd = ? and off_cd =?";

        ps = tmgr.prepareStatement(sql);

        ps.setString(pos++, dobj.getAppl_no());
        ps.setString(pos++, String.valueOf(dobj.getEmp_cd()));
        ps.setString(pos++, oldRegn);
        ps.setString(pos++, stateCd);
        ps.setInt(pos++, offCd);
        ps.executeUpdate();
    }//

    public static void deleteFromVtOwner(TransactionManager tmgr, String stateCd, int offCd, String oldRegnNo) throws SQLException {

        String sql = "DELETE FROM " + TableList.VT_OWNER + " WHERE regn_no=? and state_cd  = ? and off_cd=?";

        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, oldRegnNo);
        ps.setString(2, stateCd);
        ps.setInt(3, offCd);
        ps.executeUpdate();
    }

    public static void insertIntoVehReassign(TransactionManager tmgr, String newRegnNo, String oldRegnNo, String applNo, String reason) throws SQLException {

        String sql = "INSERT INTO " + TableList.VH_RE_ASSIGN + "("
                + "            state_cd, off_cd, appl_no, old_regn_no, new_regn_no, reason,"
                + "            moved_on, moved_by)"
                + "    VALUES (?, ?, ?, ?, ?, ?,"
                + "            current_timestamp, ?)";

        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, Util.getUserStateCode());
        ps.setInt(2, Util.getSelectedSeat().getOff_cd());
        ps.setString(3, applNo);
        ps.setString(4, oldRegnNo);
        ps.setString(5, newRegnNo);
        ps.setString(6, reason);
        ps.setString(7, Util.getEmpCode());
        ps.executeUpdate();
    }

    /**
     * @author Kartikey Singh
     */
    public static void insertIntoVehReassign(TransactionManager tmgr, String newRegnNo, String oldRegnNo, String applNo, String reason,
            String stateCode, int selectedOffCode, String empCode) throws SQLException {

        String sql = "INSERT INTO " + TableList.VH_RE_ASSIGN + "("
                + "            state_cd, off_cd, appl_no, old_regn_no, new_regn_no, reason,"
                + "            moved_on, moved_by)"
                + "    VALUES (?, ?, ?, ?, ?, ?,"
                + "            current_timestamp, ?)";

        PreparedStatement ps = tmgr.prepareStatement(sql);
        ps.setString(1, stateCode);
        ps.setInt(2, selectedOffCode);
        ps.setString(3, applNo);
        ps.setString(4, oldRegnNo);
        ps.setString(5, newRegnNo);
        ps.setString(6, reason);
        ps.setString(7, empCode);
        ps.executeUpdate();
    }

    public String[] getRegionCoveredFromVaPermitNew(Owner_dobj owner_dobj) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        String regionCoveredStr = "";
        String[] regionCovered = null;
        try {
            tmgr = new TransactionManagerReadOnly("In method getServiceTypeFromVaPermitNew");
            String qry = "select * from va_permit_new_regn where appl_no=?";
            PreparedStatement ps = tmgr.prepareStatement(qry);
            ps.setString(1, owner_dobj.getAppl_no());
            RowSet rs = tmgr.fetchDetachedRowSet();
            if (rs.next()) {
                regionCoveredStr = rs.getString("region_covered");
                if ((!CommonUtils.isNullOrBlank(regionCoveredStr)) && regionCoveredStr.length() > 0) {
                    regionCoveredStr = regionCoveredStr.substring(0, regionCoveredStr.length() - 1);
                    regionCovered = regionCoveredStr.split(",");
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + "" + ex.getStackTrace()[0]);
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
        return regionCovered;
    }

    public String checkMultiRegion(String applNo) {
        TransactionManagerReadOnly tmgr = null;
        String multiRegion = "false";
        PreparedStatement ps;
        RowSet rs;
        String Query = "";
        try {
            tmgr = new TransactionManagerReadOnly("In method checkMultiRegion");
            Query = "select region_covered,state_cd,off_cd from va_permit_new_regn where appl_no = ?";
            ps = tmgr.prepareStatement(Query);
            ps.setString(1, applNo);
            rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                String region = rs.getString(1);
                if (!CommonUtils.isNullOrBlank(region)) {
                    region = region.substring(0, (region.length() - 1));
                    Query = "select regions_covered from " + TableList.VM_REGION + " where region_cd = ANY(string_to_array(?, ',')::numeric[]) and state_cd = ? and off_cd = ?";
                    ps = tmgr.prepareStatement(Query);
                    ps.setString(1, region);
                    ps.setString(2, rs.getString("state_cd"));
                    ps.setInt(3, rs.getInt("off_cd"));
                    RowSet rs1 = tmgr.fetchDetachedRowSet_No_release();
                    while (rs1.next()) {
                        if (rs1.getInt("regions_covered") == 99) {
                            multiRegion = "true";
                            break;
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            return "false";
        } catch (NullPointerException e) {
            return "false";
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
        return multiRegion;
    }

    public static void validateFancyCubicCapacity(String stateCd, float cubicCap, String fancyRcptNo, int offCd, int ownerCd) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        RowSet rs = null;
        try {
            if ("PY".equals(stateCd) && ownerCd != TableConstants.VEH_TYPE_STATE_GOVT) {
                if (cubicCap == 0) {
                    throw new VahanException("First complete the vehicle technical details than attach the Fancy Receipt Number.");
                }
                tmgr = new TransactionManagerReadOnly("validateFancyCubicCapacity");
                String fnSQl = "select fn_cubic_cap_check from fancy.fn_cubic_cap_check(?, ?, ?)";
                ps = tmgr.prepareStatement(fnSQl);
                ps.setString(1, fancyRcptNo);
                ps.setString(2, stateCd);
                ps.setInt(3, offCd);
                rs = tmgr.fetchDetachedRowSet();
                if (rs.next()) {
                    if (!CommonUtils.isNullOrBlank(rs.getString("fn_cubic_cap_check"))) {
                        if (Integer.parseInt(rs.getString("fn_cubic_cap_check")) == 1000
                                && cubicCap > 1800) {
                            throw new VahanException("Fancy number booked with Cubic Capacity less than or equal (1800 CC) whereas VAHAN4.0 Cubic Capacity (" + cubicCap + ").");
                        }
                    } else {
                        throw new VahanException("Cubic Capacity not found on Fancy Portal for the Fancy Receipt Number :" + fancyRcptNo);
                    }
                }
            }
        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error in Fancy Receipt cubic capicity check !!!");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
                if (ps != null) {
                    ps = null;
                }
                if (rs != null) {
                    rs = null;
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
    }
}
