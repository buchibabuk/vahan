/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.EpayBean;
import nic.vahan.form.bean.OwnerBean;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.DocumentDetailsDobj;
import nic.vahan.form.dobj.ExArmyDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.ImportedVehicleDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Owner_temp_dobj;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.Trailer_dobj;
import nic.vahan.form.dobj.dealer.PendencyBankDobj;
import nic.vahan.form.dobj.dealer.TmConfigurationDealerDobj;
import nic.vahan.form.impl.FeeImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import nic.vahan.services.DmsDocCheckUtils;
import nic.vahan.services.VTDocumentModel;
import nic.vahan5.reg.commonutils.FormulaUtilities;
import nic.vahan5.reg.form.impl.DocumentUploadImplementation;
import nic.vahan5.reg.form.impl.FitnessImplementation;
import nic.vahan5.reg.form.impl.NewImplementation;
import nic.vahan5.reg.form.impl.TempRegImplementation;
import nic.vahan5.reg.rest.model.dobj.fancy.AdvanceRegnNoDobjModel;
import nic.vahan5.reg.rest.model.AxleBeanModel;
import nic.vahan5.reg.rest.model.ComparisonBeanModel;
import nic.vahan5.reg.rest.model.ContextMessageModel;
import nic.vahan5.reg.rest.model.DocumentUploadBeanModel;
import nic.vahan5.reg.rest.model.ExArmyBeanModel;
import nic.vahan5.reg.rest.model.FitnessBeanModel;
import nic.vahan5.reg.rest.model.HpaBeanModel;
import nic.vahan5.reg.rest.model.ImportedVehicleBeanModel;
import nic.vahan5.reg.rest.model.InsBeanModel;
import nic.vahan5.reg.rest.model.OwnerBeanModel;
import nic.vahan5.reg.rest.model.RetroFittingDetailsBeanModel;
import nic.vahan5.reg.rest.model.SessionVariablesModel;
import nic.vahan5.reg.rest.model.TrailerBeanModel;
import nic.vahan5.reg.rest.model.WorkBenchModel;
import nic.vahan5.reg.rest.model.WrapperModel;
import nic.vahan5.reg.server.ServerUtility;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Sai
 */
@RestController
@RequestMapping("/newregn")
public class ApproveActionPerformedController {

    @PostMapping(value = "/approveActionPerformed")
    public WrapperModel approveActionPerformed(@RequestBody WrapperModel wrapperModel, @RequestParam String clientIpAddress,
            @RequestParam String selectedRoleCode, @RequestParam Integer otherStateVehOldOffCd, @RequestParam String approveImplPrevAction) throws VahanException, Exception {

        HpaBeanModel hpa_bean;
        InsBeanModel ins_bean;
        ExArmyBeanModel exArmyBean;
        ImportedVehicleBeanModel imp_bean;
        AxleBeanModel axle_bean;
        EpayBean epay;
        RetroFittingDetailsBeanModel cng_bean;
        OwnerBeanModel owner_bean = null;
        TrailerBeanModel trailer_bean;
        HpaDobj hpa_dobj = null;
        InsDobj ins_dobj = null;
        Trailer_dobj trailer_dobj = null;
        ExArmyDobj exArmyDobj = null;
        ImportedVehicleDobj imp_dobj = null;
        AxleDetailsDobj axle_dobj = null;
        RetroFittingDetailsDobj cng_dobj = null;
        String hypothecationStatus = null;
        PendencyBankDobj bankSubsidyDetail = null;
        //  OwnerBean owner_bean = null;
        FitnessBeanModel fitness_bean = null;
        Owner_dobj owner_dobj = null;
        String changedData = null;
        String generatedRegnNo = null;
        List<OwnerDetailsDobj> tempRegnDetailsList = null;
        List<ComparisonBean> ins_chage_List = null;
        List<ComparisonBean> compBeanList = null;

        Status_dobj status;
        TmConfigurationDobj tmConfDobj;
        SessionVariablesModel sessionVariables;
        WorkBenchModel workBenchModel;
        ComparisonBeanModel comparisonBeanModel;

        status = wrapperModel.getStatusDobj();
        tmConfDobj = wrapperModel.getTmConfigDobj();
        sessionVariables = wrapperModel.getSessionVariables();
        workBenchModel = wrapperModel.getWorkBench();
        String counterId = workBenchModel.getCOUNTER_ID();
        comparisonBeanModel = wrapperModel.getComparisonBean();
        DocumentUploadBeanModel documentUploadBeanModel = wrapperModel.getDocumentUploadBeanModel();
        wrapperModel.setDocumentUploadBeanModel(documentUploadBeanModel);

        int purcd = status.getPur_cd();
        String APPL_NO = status.getAppl_no();
        String newStatus = status.getStatus();
        workBenchModel.setRenderFasTagDialog(false);
        status.getEmp_cd();
        status.getOff_cd();
        status.getState_cd();
        status.getOffice_remark();
        status.getPublic_remark();
        int ROLE_CD = workBenchModel.getROLE_CD();
        String PUR_CD = workBenchModel.getPUR_CD();
        int ACTION_CDOE = workBenchModel.getACTION_CDOE();
        changedData = comparisonBeanModel.getChanged_data();
        String userStateCode = status.getState_cd();
        TmConfigurationDealerDobj dealerConfigDobj = tmConfDobj.getTmConfigDealerDobj();

        if (purcd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || purcd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || purcd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE || purcd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {

            FeeImpl feeImpl = new FeeImpl();
            boolean isVehicleHypothecated = feeImpl.isHypothecated(APPL_NO, purcd);
//            wrapperModel.setIsVehicleHypothecated(isVehicleHypothecated);
            hpa_bean = wrapperModel.getHpaBeanModel();
            wrapperModel.setHpaBeanModel(hpa_bean);
            if (workBenchModel.isHypothecated()) {
                if (hpa_bean != null) {
                    hpa_dobj = hpa_bean.getHpaDobj();
                    hpa_bean.getHpaDobj().setAppl_no(APPL_NO);
                    hpa_bean.setHpaDobj(hpa_dobj);
                }
            } else {
                hpa_dobj = null;
            }

            if (isVehicleHypothecated == false && hpa_dobj != null) {
                hypothecationStatus = "HP details inserted";
                wrapperModel.setHypothecationStatus(hypothecationStatus);
            } else if (isVehicleHypothecated == true && hpa_dobj == null) {
                hypothecationStatus = "HP details deleted";
                wrapperModel.setHypothecationStatus(hypothecationStatus);
            }
            ins_bean = wrapperModel.getIns_bean();
            wrapperModel.setIns_bean(ins_bean);
            if (ins_bean != null) {
                ins_dobj = ins_bean.set_InsBean_to_dobj();
                ins_chage_List = ins_bean.compareChagnes();
                ins_bean.setIns_dobj_prv(ins_dobj);
            }
            trailer_bean = wrapperModel.getTrailerBeanModel();
            wrapperModel.setTrailerBeanModel(trailer_bean);
            if (trailer_bean != null) {
                trailer_dobj = trailer_bean.setTrailerBeanToDobj();
                trailer_bean.setTrailer_dobj_prv(trailer_dobj);
            }

            if (newStatus.equals(TableConstants.STATUS_COMPLETE)
                    || newStatus.equals(TableConstants.STATUS_REVERT)
                    || newStatus.equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                owner_bean = wrapperModel.getOwnerBean();

                fitness_bean = wrapperModel.getFitnessBeanModel();

                owner_dobj = owner_bean.getOwnerDobj();

                if (owner_bean.getInspDobj() != null) {
                    owner_dobj.setInspectionDobj(owner_bean.getInspDobj());
                }
                owner_dobj.setAppl_no(APPL_NO); //need to check can we remove it??
                owner_bean.checkLadenWeight(owner_bean.getOwnerDobj().getVch_catg(), owner_bean.getOwnerDobj().getLd_wt(), Integer.parseInt(PUR_CD), owner_dobj.getVch_purchase_as());

                if (owner_dobj.getLaser_code() != null && !owner_dobj.getLaser_code().equals("") && owner_dobj.getLaser_code().equals(TableConstants.HOMOLOGATION_DATA)) {
                    if (owner_bean.getHomoUnLdWt() > owner_dobj.getUnld_wt()) {
                        throw new VahanException(TableConstants.ULD_WT_ERR_MESS + " {" + owner_bean.getHomoUnLdWt() + " kgs}");
                    }
                }

                // checkSaleAmount as per Homologation data.
                if (dealerConfigDobj != null && dealerConfigDobj.isValidateHomoSaleAmt()) {
                    if (owner_bean.getHomoDobj() != null && owner_dobj.getOwner_cd() != TableConstants.VEH_TYPE_STATE_GOVT
                            && owner_dobj.getOwner_cd() != TableConstants.VEH_TYPE_GOVT) {
                        try {
                            checkSaleAmount(owner_bean.getHomoDobj().getSale_amt(), owner_dobj.getSale_amt());
                        } catch (VahanException e) {
                            wrapperModel.setContextMessageModel(new ContextMessageModel(ContextMessageModel.MessageContext.REQUEST,
                                    ContextMessageModel.MessageSeverity.INFO, true, "workbench_tabview:sale_amt", e.getMessage()));
                            return wrapperModel;
                        }
                    }
                }

                owner_bean.validateHomoGCW(owner_bean.getHomoDobj(), owner_dobj);

                if (newStatus.equals(TableConstants.STATUS_COMPLETE) && owner_bean.getHomoDobj() == null) {
                    if (owner_dobj.getNorms() != 4) {
                        owner_bean.validateHomoMaker(owner_dobj);
                    }
                }

                // check for jharkhand inspection and inspection approval
                if (owner_dobj.getLaser_code() != null && tmConfDobj != null
                        && !owner_dobj.getLaser_code().equals("")
                        && owner_dobj.getLaser_code().equals(TableConstants.HOMOLOGATION_DATA) && tmConfDobj.isValidateHomoSeatCap()) {
                    if (ROLE_CD == TableConstants.TM_ROLE_INSPECTION || TableConstants.TM_NEW_RC_FITNESS_INSPECTION_APPROVAL == ACTION_CDOE) {
                        if (owner_bean.getHomoDobj() != null) {
                            checkSeatCap(owner_bean.getHomoDobj().getSeat_cap(), owner_dobj.getSeat_cap());
                        }
                    }
                }

                if (ROLE_CD == TableConstants.TM_ROLE_SCRUTINY_E_PAY) {
                    epay = wrapperModel.getEpayBean();
                    if (epay.getAct_total() != epay.getE_total()) {
                        throw new VahanException("Please pay Rest of Amount : " + (epay.getAct_total() - epay.getE_total()));
                    }
                }
//                        changedData = ComparisonBeanImpl.changedDataContents(compBeanList);
//                    if (hypothecationStatus != null && !hypothecationStatus.equals("")) {
//                        changedData = changedData + hypothecationStatus;
//                    }
                exArmyBean = wrapperModel.getExArmyBean();
                wrapperModel.setExArmyBean(exArmyBean);
                if (exArmyBean != null) {
                    exArmyDobj = exArmyBean.setExArmyBean_To_Dobj();
                    exArmyBean.setExArmy_dobj_prv(exArmyDobj);
                }

                imp_bean = wrapperModel.getImportedVehicleBean();
                wrapperModel.setImportedVehicleBean(imp_bean);
                if (imp_bean != null) {
                    imp_dobj = imp_bean.setBean_to_Dobj();
                    imp_bean.setImp_dobj_prv(imp_dobj);
                }
                axle_bean = wrapperModel.getAxleBean();
                wrapperModel.setAxleBean(axle_bean);
                if (axle_bean != null) {
                    axle_dobj = axle_bean.setBean_To_Dobj();
                    axle_bean.setAxle_dobj_prv(axle_dobj);
                }
                cng_bean = wrapperModel.getCngDetailsBean();
                wrapperModel.setCngDetailsBean(cng_bean);
                if (cng_bean != null) {
                    cng_dobj = cng_bean.setBean_To_Dobj();
                    cng_bean.setCng_dobj_prv(cng_dobj);
                }

                VehicleParameters parameters = FormulaUtilities.fillVehicleParametersFromDobj(owner_dobj, sessionVariables);
                status.setVehicleParameters(parameters);
                if (newStatus.equals(TableConstants.STATUS_COMPLETE)) {
                    parameters.setAPPL_DATE(ServerUtil.setTempApplDtAsNewApplDT(status));
                    parameters.setPUR_CD(purcd);
                    parameters.setACTION_CD(ACTION_CDOE);
                    boolean isValidForRegistration = ServerUtility.validateVehicleNorms(owner_dobj, purcd, parameters, tmConfDobj.getTmConfigDealerDobj());
                    if (!isValidForRegistration) {
                        throw new VahanException("State Transport Department has not authorized you to further process this Registration Application of '" + MasterTableFiller.masterTables.VM_VH_CLASS.getDesc(owner_dobj.getVh_class() + "") + "' with emission norms " + MasterTableFiller.masterTables.VM_NORMS.getDesc(owner_dobj.getNorms() + "") + " , Purchase Date: " + DateUtil.parseDateToString(owner_dobj.getPurchase_dt()) + " , Application Date: " + DateUtil.parseDateToString(DateUtil.parseDate(DateUtil.convertStringYYYYMMDDToDDMMYYYY(parameters.getAPPL_DATE()))) + ", please contact respective Registering Authority regarding this.");
                    }

                    // FastTag code
                    owner_bean.validatePurchaseDate(DateUtils.parseDate(status.getAppl_dt()));
                    owner_bean.validateInsuranceValidity(ins_dobj); // Validate Insurance validity
                }
//                if (dealerConfigDobj != null) {
//                    if (newStatus.equals(TableConstants.STATUS_COMPLETE)) {
//                        parameters.setPUR_CD(purcd);
//                        if (isCondition(replaceTagValues(dealerConfigDobj.getNormsConditionFormulas(), parameters), "NormsConditionFormulas")) {
//                            throw new VahanException("Registration is prohibited for '" + MasterTableFiller.masterTables.VM_VH_CLASS.getDesc(owner_dobj.getVh_class() + "") + "' with emission norms " + MasterTableFiller.masterTables.VM_NORMS.getDesc(owner_dobj.getNorms() + ""));
//                        }
//                    }
//                } else {
//                    throw new VahanException("Problem occurred during getting configuration.");
//                }
            }
        }
        if (purcd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || purcd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {

            // fitness_bean = wrapperModel.getFitness_bean();
            //   NewImpl new_Impl = new NewImpl();
            NewImplementation new_Impl = new NewImplementation();

            if (newStatus.equals(TableConstants.STATUS_COMPLETE)
                    || newStatus.equals(TableConstants.STATUS_REVERT)
                    || newStatus.equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                if (documentUploadBeanModel != null && documentUploadBeanModel.isRenderApiBasedDMSDocPanel() && status.getStatus().equalsIgnoreCase(TableConstants.STATUS_COMPLETE) && tmConfDobj != null && tmConfDobj.getTmConfigDmsDobj() != null && tmConfDobj.getTmConfigDmsDobj().getPurCd().contains("," + purcd + ",") && (tmConfDobj.getTmConfigDmsDobj().getDocUploadAllotedOff().contains("ALL") || tmConfDobj.getTmConfigDmsDobj().getDocUploadAllotedOff().contains("," + status.getOff_cd() + ","))) {
                    if (tmConfDobj.getTmConfigDmsDobj().getVerfyActionCd().contains("," + ACTION_CDOE + ",") || tmConfDobj.getTmConfigDmsDobj().getApproveActionCd().contains("," + ACTION_CDOE + ",")) {
                        List<DocumentDetailsDobj> docsDetailsList = documentUploadBeanModel.getUploadedList();
                        if (docsDetailsList != null && !docsDetailsList.isEmpty() && docsDetailsList.size() > 0) {
                            documentUploadBeanModel.setEmailMessage("");
                            for (DocumentDetailsDobj docsList : docsDetailsList) {
                                if (!docsList.isDocVerified()) {
                                    documentUploadBeanModel.setEmailMessage(docsList.getCatName() + "," + documentUploadBeanModel.getEmailMessage());
                                }
                            }
                            if (!documentUploadBeanModel.getEmailMessage().isEmpty() && purcd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                                if (sessionVariables.getUserCatgForLoggedInUser() != null && sessionVariables.getUserCatgForLoggedInUser().equals("B") && tmConfDobj.getTmConfigDmsDobj().getApproveActionCd().contains("," + ACTION_CDOE + ",")) {
                                    throw new VahanException("Either Verify documents or modify Documents !!!.");
                                } else {
                                    if (tmConfDobj.getTmConfigDmsDobj().getVerfyActionCd().contains("," + ACTION_CDOE + ",") || tmConfDobj.getTmConfigDmsDobj().getApproveActionCd().contains("," + ACTION_CDOE + ",")) {
                                        documentUploadBeanModel.setRenderedMailButton(true);
                                        throw new VahanException("Either Verify documents or Send Mail to Dealer to modify Documents !!!.");
                                    }
                                }
                            } else if (!documentUploadBeanModel.getEmailMessage().isEmpty() && purcd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE) {
                                if (tmConfDobj.getTmConfigDmsDobj().getVerfyActionCd().contains("," + ACTION_CDOE + ",") || tmConfDobj.getTmConfigDmsDobj().getApproveActionCd().contains("," + ACTION_CDOE + ",")) {
                                    throw new VahanException("Either Verify documents or Revert Back to modify Documents !!!.");
                                }
                            }
                            if (purcd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                                new DocumentUploadImplementation().updateDMSStatus(APPL_NO, String.valueOf(status.getEmp_cd()), status.getState_cd());
                            }
                        }
                    }
                }

                if (documentUploadBeanModel != null && documentUploadBeanModel.isRenderUiBasedDMSDocPanel() && status.getStatus().equalsIgnoreCase(TableConstants.STATUS_COMPLETE) && tmConfDobj != null && tmConfDobj.getTmConfigDmsDobj() != null && tmConfDobj.getTmConfigDmsDobj().getPurCd().contains("," + purcd + ",") && (tmConfDobj.getTmConfigDmsDobj().getDocUploadAllotedOff().contains("ALL") || tmConfDobj.getTmConfigDmsDobj().getDocUploadAllotedOff().contains("," + status.getOff_cd() + ","))) {
                    if (tmConfDobj.getTmConfigDmsDobj().getVerfyActionCd().contains("," + ACTION_CDOE + ",") || tmConfDobj.getTmConfigDmsDobj().getApproveActionCd().contains("," + ACTION_CDOE + ",")) {
                        List<VTDocumentModel> docsDetailsList = DmsDocCheckUtils.getUploadedDocumentList(APPL_NO);
                        if (docsDetailsList != null && !docsDetailsList.isEmpty() && docsDetailsList.size() > 0) {
                            if (tmConfDobj.getTmConfigDmsDobj().getVerfyActionCd().contains("," + ACTION_CDOE + ",")) {
                                for (VTDocumentModel docsList : docsDetailsList) {
                                    if (!docsList.isDoc_verified()) {
                                        throw new VahanException("Please verify documents before application verification.");
                                    }
                                }
                            } else if (tmConfDobj.getTmConfigDmsDobj().getApproveActionCd().contains("," + ACTION_CDOE + ",")) {
                                for (VTDocumentModel docsList : docsDetailsList) {
                                    if (!docsList.isDoc_approved()) {
                                        throw new VahanException("Please approve documents before application approval.");
                                    }
                                }
                            }
                            if (purcd == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                                new DocumentUploadImplementation().updateDMSStatus(APPL_NO, String.valueOf(status.getEmp_cd()), status.getState_cd());
                            }
                        }
                    }
                }

                FitnessDobj fitness_dobj = null;
                ArrayList fitCheckList = null;

                /**
                 * @UpdatedBy Kartikey
                 */
//                fitness_dobj = fitness_bean.getFitnessDobj();
                if (owner_bean.getAdvanceRegnNoDobj() != null) {
                    owner_dobj.setAdvanceRegNoDobj(AdvanceRegnNoDobjModel.convertModelToDobj(owner_bean.getAdvanceRegnNoDobj()));
                }
                if (owner_bean.getRetenRegNoDobj() != null) {
                    owner_dobj.setRetenRegNoDobj(owner_bean.getRetenRegNoDobj());
                }
                if (newStatus.equals(TableConstants.STATUS_COMPLETE) && !CommonUtils.isNullOrBlank(workBenchModel.getFancyRcptNo())) {
                    NewImplementation.validateFancyCubicCapacity(sessionVariables.getStateCodeSelected(), owner_dobj.getCubic_cap(), workBenchModel.getFancyRcptNo(), sessionVariables.getOffCodeSelected(), owner_dobj.getOwner_cd());
                }

                owner_bean.validateTempRegistrationData(owner_dobj, Integer.parseInt(PUR_CD), owner_dobj.getRegn_type(), sessionVariables);

                if (ROLE_CD == TableConstants.TM_ROLE_INSPECTION || TableConstants.TM_NEW_RC_FITNESS_INSPECTION_APPROVAL == ACTION_CDOE) { //this is used for inspection
                    if (owner_bean.isRenderInspectionPanel())//; getInspection_panel().isRendered())
                    {
                        fitness_dobj = fitness_bean.getFitnessDobj();
                        fitCheckList = fitness_bean.getAllYesNoList();
                        owner_dobj.setFit_dt(fitness_bean.getFitnessDobj().getFit_valid_to());
                    }
//                       
                    int fit_upto = FitnessImplementation.getNewFitnessUpto(owner_dobj, sessionVariables);
                    if (fit_upto > 0) {
                        Date fitUpto = DateUtils.addToDate(new Date(), 3, fit_upto);
                        fitUpto = DateUtils.addToDate(fitUpto, 1, -1);
                        owner_dobj.setFit_upto(fitUpto);
                    }
                }

                /**
                 * @AddedBy Kartikey
                 */
                if (status.getVehicleParameters() != null && owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                    status.getVehicleParameters().setLOGIN_OFF_CD(Util.getSelectedSeat().getOff_cd());
                    if (otherStateVehOldOffCd != null && otherStateVehOldOffCd > 0) {
                        status.getVehicleParameters().setPREV_OFF_CD(otherStateVehOldOffCd);
                    }
                }

                //=================WEB SERVICES FOR NEXTSTAGE START=========//
                ServerUtility.webServiceForNextStage(status, newStatus, counterId, APPL_NO, ACTION_CDOE, purcd, approveImplPrevAction,
                        sessionVariables.getEmpCodeLoggedIn(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                //===============WEB SERVICES FOR NEXTSTAGE END============//

                /**
                 * @UpdatedBy Kartikey
                 */
                if (owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                        || owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                } else {
                    //Check Duplicate Chassis No Except in case of Vehicle from Other State / Other Office
                    if (!(owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_CONFISCATED_AUCTION)) && (owner_bean.getOwnerDobj().getAuctionDobj() == null)) {
                        ServerUtility.checkChasiNoExist(owner_dobj.getChasi_no().toUpperCase());
                    }
                }

                if (workBenchModel.getCheckFeeTax() != null && status.getStatus().equalsIgnoreCase(TableConstants.STATUS_COMPLETE) && ACTION_CDOE == TableConstants.TM_ROLE_DEALER_APPROVAL) {
                    if ((workBenchModel.getCheckFeeTax().getE_total() - workBenchModel.getCheckFeeTax().getAct_total()) < 0) {
                        throw new VahanException("Total Fee/Tax paid for application " + APPL_NO + " is less than Actual Amount. Please pay balance amount of Rs." + (workBenchModel.getCheckFeeTax().getE_total() - workBenchModel.getCheckFeeTax().getAct_total()) + "/- before approval.");
                    }
                }
                bankSubsidyDetail = getBankSubsidyDetailsDobj(owner_dobj, bankSubsidyDetail);

                String regionStr = "";
                if (owner_dobj.getRegion_covered() != null) {
                    for (String region : owner_dobj.getRegion_covered()) {
                        regionStr += region + ",";
                    }
                }
                if (!CommonUtils.isNullOrBlank(regionStr)) {
                    owner_dobj.setRegion_covered_str(regionStr);
                } else {
                    owner_dobj.setRegion_covered_str(null);
                }

                //   wrapperModel.setBankSubsidyDetail(bankSubsidyDetail);
                //  String userStateCode = owner_dobj.getState_cd();
                String regn_no = new_Impl.update_New_Status(ROLE_CD, owner_dobj, fitness_dobj, fitCheckList, status, changedData, userStateCode, hpa_dobj, ins_dobj, trailer_dobj, exArmyDobj, imp_dobj, axle_dobj, cng_dobj, ACTION_CDOE, sessionVariables, bankSubsidyDetail, ins_chage_List, clientIpAddress, selectedRoleCode);
                generatedRegnNo = regn_no;
            }
        }
        if (purcd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || purcd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {

            if (newStatus.equals(TableConstants.STATUS_COMPLETE)
                    || newStatus.equals(TableConstants.STATUS_REVERT)
                    || newStatus.equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                if (documentUploadBeanModel != null && documentUploadBeanModel.isRenderApiBasedDMSDocPanel() && status.getStatus().equalsIgnoreCase(TableConstants.STATUS_COMPLETE) && tmConfDobj != null && tmConfDobj.getTmConfigDmsDobj() != null && tmConfDobj.getTmConfigDmsDobj().getTempApproveActionCd().contains("," + ACTION_CDOE + ",") && tmConfDobj.getTmConfigDmsDobj().getPurCd().contains("," + purcd + ",")
                        && (tmConfDobj.getTmConfigDmsDobj().getDocUploadAllotedOff().contains("ALL") || tmConfDobj.getTmConfigDmsDobj().getDocUploadAllotedOff().contains("," + status.getOff_cd() + ","))) {
                    List<DocumentDetailsDobj> docsDetailsList = documentUploadBeanModel.getUploadedList();
                    if (docsDetailsList != null && !docsDetailsList.isEmpty() && docsDetailsList.size() > 0) {
                        documentUploadBeanModel.setEmailMessage("");
                        for (DocumentDetailsDobj docsList : docsDetailsList) {
                            if (!docsList.isDocVerified()) {
                                documentUploadBeanModel.setEmailMessage(docsList.getCatName() + "," + documentUploadBeanModel.getEmailMessage());
                            }
                        }

                        if (!documentUploadBeanModel.getEmailMessage().isEmpty() && purcd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                            if (sessionVariables.getUserCatgForLoggedInUser() != null && sessionVariables.getUserCatgForLoggedInUser().equals("B") && tmConfDobj.getTmConfigDmsDobj().getTempApproveActionCd().contains("," + ACTION_CDOE + ",")) {
                                throw new VahanException("Either Verify documents or modify Documents !!!.");
                            } else {
                                if (tmConfDobj.getTmConfigDmsDobj().getTempApproveActionCd().contains("," + ACTION_CDOE + ",")) {
                                    // Need to verify this
                                    documentUploadBeanModel.setRenderedMailButton(true);
                                    throw new VahanException("Either Verify documents or Send Mail to Dealer to modify Documents !!!.");
                                }
                            }
                        } else if (!documentUploadBeanModel.getEmailMessage().isEmpty() && purcd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
                            if (tmConfDobj.getTmConfigDmsDobj().getTempApproveActionCd().contains("," + ACTION_CDOE + ",")) {
                                throw new VahanException("Either Verify documents or Revert Back to modify Documents !!!.");
                            }
                        }

                    }
                }

                if (documentUploadBeanModel != null && documentUploadBeanModel.isRenderUiBasedDMSDocPanel() && status.getStatus().equalsIgnoreCase(TableConstants.STATUS_COMPLETE) && tmConfDobj != null && tmConfDobj.getTmConfigDmsDobj() != null && tmConfDobj.getTmConfigDmsDobj().getTempApproveActionCd().contains("," + ACTION_CDOE + ",") && tmConfDobj.getTmConfigDmsDobj().getPurCd().contains("," + purcd + ",")
                        && (tmConfDobj.getTmConfigDmsDobj().getDocUploadAllotedOff().contains("ALL") || tmConfDobj.getTmConfigDmsDobj().getDocUploadAllotedOff().contains("," + status.getOff_cd() + ","))) {
                    List<VTDocumentModel> docsDetailsList = DmsDocCheckUtils.getUploadedDocumentList(APPL_NO);
                    if (docsDetailsList != null && !docsDetailsList.isEmpty() && docsDetailsList.size() > 0) {
                        for (VTDocumentModel docsList : docsDetailsList) {
                            if (!docsList.isTemp_doc_approved()) {
                                throw new VahanException("Please approve documents before application approval.");
                            }
                        }
                    }
                }

                FitnessDobj fitness_dobj = null;
                ArrayList fitCheckList = null;

                if (ROLE_CD == TableConstants.TM_ROLE_INSPECTION) { //this is used for inspection
                    if (owner_bean.isRenderInspectionPanel()) {
                        fitness_dobj = (FitnessDobj) fitness_bean.makeDobjFromBean()[0];
                        fitCheckList = (ArrayList) fitness_bean.makeDobjFromBean()[1];
                        owner_dobj.setFit_dt(fitness_bean.getFitnessDobj().getFit_valid_to());
                    } else {
                        if (!(owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                                || owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))) {

                            int fit_upto = FitnessImpl.getNewFitnessUpto(owner_dobj);
                            if (fit_upto > 0) {
                                Date fitUpto = DateUtils.addToDate(new Date(), 3, fit_upto);
                                fitUpto = DateUtils.addToDate(fitUpto, 1, -1);
                                owner_dobj.setFit_upto(fitUpto);
                            }
                        }
                    }
                }

                //=================WEB SERVICES FOR NEXTSTAGE START=========//
                ServerUtility.webServiceForNextStage(status, newStatus, counterId, APPL_NO, ACTION_CDOE, purcd, approveImplPrevAction,
                        sessionVariables.getEmpCodeLoggedIn(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                //===============WEB SERVICES FOR NEXTSTAGE END============//

                Owner_temp_dobj ownerTempDobjPrev = null;
                if (tempRegnDetailsList != null && !tempRegnDetailsList.isEmpty() && tempRegnDetailsList.size() >= 1) {
                    OwnerDetailsDobj ownerDetailsDobj = tempRegnDetailsList.get(tempRegnDetailsList.size() - 1);
                    if (owner_dobj != null && ownerDetailsDobj != null && ownerDetailsDobj.getOwnerTempDobj() != null) {
                        ownerTempDobjPrev = ownerDetailsDobj.getOwnerTempDobj();
                    }
                }
                TempRegImplementation TempRegImpl = new TempRegImplementation();
                String tempRegNo = TempRegImpl.update_New_Status(ROLE_CD, owner_dobj, fitness_dobj,
                        fitCheckList, status, changedData, hpa_dobj, ins_dobj, trailer_dobj, exArmyDobj, imp_dobj, axle_dobj, cng_dobj, ACTION_CDOE, ownerTempDobjPrev, sessionVariables);
//                wrapperModel.setTempRegNo(tempRegNo);
            }
        }
//        if (sessionVariables.getStateCodeSelected().equalsIgnoreCase("GA") && ACTION_CDOE == TableConstants.TM_NEW_RC_APPROVAL && newStatus.equals(TableConstants.STATUS_COMPLETE) && !CommonUtils.isNullOrBlank(generatedRegnNo)) {
//            HttpSession ses = Util.getSession();
//            ses.setAttribute("regn_no", generatedRegnNo);
//            return "regnNoacknowledgement";
//        }
//        if (newStatus.equals(TableConstants.STATUS_DISPATCH_PENDING)) {
//            return "disAppNotice";
//        } else {
//            return "home";
//        }
        return wrapperModel;
    }

    public void checkSaleAmount(int homoSaleAmt, int saleAmt) throws VahanException {
        WrapperModel wrapperModel = new WrapperModel();
        int ACTION_CDOE = wrapperModel.getStatusDobj().getAction_cd();
        OwnerBean owner_bean = null;
        if (homoSaleAmt > saleAmt) {
            if (ACTION_CDOE != TableConstants.TM_NEW_RC_APPROVAL && ACTION_CDOE != TableConstants.TM_ROLE_DEALER_APPROVAL
                    && ACTION_CDOE != TableConstants.TM_ROLE_DEALER_TEMP_APPROVAL && ACTION_CDOE != TableConstants.TM_TMP_RC_APPROVAL) {
                owner_bean.setDealerSaleAmt(false);
                throw new VahanException(TableConstants.SALE_AMT_ERR_MESS + " {Rs." + homoSaleAmt + "/-} Now Modify Sale Amount.");
            }
        }
    }

    public PendencyBankDobj getBankSubsidyDetailsDobj(Owner_dobj owner_dobj, PendencyBankDobj bankSubsidy) {
        WrapperModel wrapperModel = new WrapperModel();
        Status_dobj status = wrapperModel.getStatusDobj();

        String APPL_NO = null;
        int PUR_CD = 0;
        PendencyBankDobj bankSubsidyDetail = null;
        if (owner_dobj != null && bankSubsidy != null && !"".equals(bankSubsidy) && "DL".equals(wrapperModel.getSessionVariables().getStateCodeSelected()) && owner_dobj.getVh_class() == TableConstants.ERICKSHAW_VCH_CLASS
                && Integer.valueOf(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE && owner_dobj.getOwner_cd() == TableConstants.OWNER_CODE_INDIVIDUAL) {
            bankSubsidyDetail.setAadharNo(owner_dobj.getOwner_identity().getAadhar_no());
            bankSubsidyDetail.setDlLlNo(owner_dobj.getOwner_identity().getDl_no());
            bankSubsidyDetail.setApplNo(APPL_NO);
            bankSubsidyDetail.setStatusCode(TableConstants.SUBSIDY_PENDING_STATUS);
        }
        return bankSubsidyDetail;
    }

    public void checkSeatCap(int homoSeatCap, int seatCap) throws VahanException {
        if (homoSeatCap > seatCap) {
            throw new VahanException(TableConstants.SEAT_CAP_ERR_MESS + " {" + homoSeatCap + "}");
        }
    }
}
