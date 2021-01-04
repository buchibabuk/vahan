/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.controller;

import nic.vahan5.reg.rest.model.ComparisonBeanModel;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import javax.faces.model.SelectItem;
import nic.java.util.CommonUtils;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.db.Dealer;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.bean.TaxFormPanelBean;
import nic.vahan.form.bean.fancy.RetenRegnNo_dobj;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.DOTaxDetail;
import nic.vahan.form.dobj.EpayDobj;
import nic.vahan.form.dobj.ExArmyDobj;
import nic.vahan.form.dobj.FeeDobj;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.Fee_Pay_Dobj;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.ImportedVehicleDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.OnlinePayDobj;
import nic.vahan.form.dobj.OtherStateVehDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.Tax_Pay_Dobj;
import nic.vahan.form.dobj.TempRegDobj;
import nic.vahan.form.dobj.TmConfigurationDMS;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.TmConfigurationOwnerIdentificationDobj;
import nic.vahan.form.dobj.Trailer_dobj;
import nic.vahan.form.dobj.configuration.TmConfigurationReceipts;
import nic.vahan.form.dobj.dealer.PendencyBankDobj;
import nic.vahan.form.dobj.dealer.TmConfigurationDealerDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.impl.CdImpl;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.DocumentUploadImpl;
import nic.vahan.form.impl.EpayImpl;
import nic.vahan.form.impl.FeeDraftimpl;
import nic.vahan.form.impl.NewImpl;
import nic.vahan.form.impl.OwnerIdentificationImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.ScrappedVehicleImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.dealer.PendencyBankDetailImpl;
import nic.vahan.form.impl.fancy.AdvanceRegnFeeImpl;
import nic.vahan.form.impl.permit.PermitImpl;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.form.dobj.fastag.FasTagDetailsDobj;
import nic.vahan.form.impl.FasTagImpl;
import nic.vahan.server.ServerUtil;
import nic.vahan.services.DmsDocCheckUtils;
import nic.vahan.services.VTDocumentModel;
import nic.vahan5.reg.commonutils.FormulaUtilities;
import nic.vahan5.reg.form.impl.AxleImplementation;
import nic.vahan5.reg.form.impl.BlackListedVehicleImplementation;
import nic.vahan5.reg.form.impl.DocumentUploadImplementation;
import nic.vahan5.reg.form.impl.EpayImplementation;
import nic.vahan5.reg.form.impl.ExArmyImplementation;
import nic.vahan5.reg.form.impl.FeeImplementation;
import nic.vahan5.reg.form.impl.FitnessImplementation;
import nic.vahan5.reg.form.impl.HpaImplementation;
import nic.vahan5.reg.form.impl.ImportedVehicleImplementation;
import nic.vahan5.reg.form.impl.InsImplementation;
import nic.vahan5.reg.form.impl.NewImplementation;
import nic.vahan5.reg.form.impl.NewVehicleFitnessImplentation;
import nic.vahan5.reg.form.impl.NocImplementation;
import nic.vahan5.reg.form.impl.OtherStateVehImplementation;
import nic.vahan5.reg.form.impl.OwnerImplementation;
import nic.vahan5.reg.form.impl.permit.PermitImplementation;
import nic.vahan5.reg.form.impl.RetroFittingDetailsImplementation;
import nic.vahan5.reg.form.impl.TaxServerImplementation;
import nic.vahan5.reg.form.impl.TrailerImplementation;
import nic.vahan5.reg.rest.model.dobj.fancy.AdvanceRegnNoDobjModel;
import nic.vahan5.reg.rest.model.AxleBeanModel;
import nic.vahan5.reg.rest.model.ContextMessageModel;
import nic.vahan5.reg.rest.model.ContextMessageModel.MessageContext;
import nic.vahan5.reg.rest.model.ContextMessageModel.MessageSeverity;
import nic.vahan5.reg.rest.model.DocumentUploadBeanModel;
import nic.vahan5.reg.rest.model.ExArmyBeanModel;
import nic.vahan5.reg.rest.model.FormFeePanelBeanModel;
import nic.vahan5.reg.rest.model.HpaBeanModel;
import nic.vahan5.reg.rest.model.ImportedVehicleBeanModel;
import nic.vahan5.reg.rest.model.InsBeanModel;
import nic.vahan5.reg.rest.model.NewVehicleFeeBeanModel;
import nic.vahan5.reg.rest.model.OwnerBeanModel;
import nic.vahan5.reg.rest.model.PaymentCollectionBeanModel;
import nic.vahan5.reg.rest.model.permit.PermitPanelBeanModel;
import nic.vahan5.reg.rest.model.RetroFittingDetailsBeanModel;
import nic.vahan5.reg.rest.model.SessionVariablesModel;
import nic.vahan5.reg.rest.model.WorkBenchModel;
import nic.vahan5.reg.rest.model.WrapperModel;
import nic.vahan5.reg.server.ServerUtility;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
//import tax.web.service.VahanTaxParameters;
import nic.vahan.form.dobj.tax.VahanTaxParameters;

/**
 *
 * @author Kartikey Singh
 */
@RestController
public class NewRegnController {

    private static Logger LOGGER = Logger.getLogger(NewRegnController.class);

    @PostMapping("/save/partial")
    public WrapperModel save_ActionListener(@RequestBody WrapperModel wrapperModel, @RequestParam String saveType,
            @RequestParam String changedData, @RequestParam Integer userLoginOffCode,
            @RequestParam String clientIpAddress, @RequestParam String selectedRoleCode) throws SQLException, VahanException, Exception {

        ComparisonBeanModel comparisonBean = wrapperModel.getComparisonBean();
        String purCd = comparisonBean.getPUR_CD();
        String applNo = comparisonBean.getAPPL_NO();
        String counterId = comparisonBean.getCOUNTER_ID();

        WorkBenchModel workBench = wrapperModel.getWorkBench();

        SessionVariablesModel sessionVariables = wrapperModel.getSessionVariables();
        String empCode = sessionVariables.getEmpCodeLoggedIn();
        String userStateCode = sessionVariables.getStateCodeSelected();
        int offCode = sessionVariables.getSelectedWork().getOff_cd();
        int actionCode = sessionVariables.getActionCodeSelected();
        String userId = sessionVariables.getUserIdForLoggedInUser();
        String stateCodeSelected = sessionVariables.getStateCodeSelected();
        String userCategory = sessionVariables.getUserCatgForLoggedInUser();

        OwnerBeanModel ownerBean = wrapperModel.getOwnerBean();

        HpaBeanModel hpaBean = wrapperModel.getHpaBeanModel();
        ExArmyBeanModel exArmyBean = wrapperModel.getExArmyBean();
        ImportedVehicleBeanModel importedVehicleBean = wrapperModel.getImportedVehicleBean();
        AxleBeanModel axleBean = wrapperModel.getAxleBean();
        RetroFittingDetailsBeanModel cngDetailsBean = wrapperModel.getCngDetailsBean();
        InsBeanModel insBean = wrapperModel.getIns_bean();
        Trailer_dobj trailerDobj = wrapperModel.getTrailerDobj();

        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager("save_ActionListener");

            TmConfigurationDealerDobj dealerConfigDobj = null;
            TmConfigurationDobj tmConfigDobj = wrapperModel.getTmConfigDobj();

            if (tmConfigDobj == null) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            } else {
                dealerConfigDobj = tmConfigDobj.getTmConfigDealerDobj();
            }

            if (ownerBean.getOwnerDobj() != null) {
                if (Integer.parseInt(purCd) == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE && !tmConfigDobj.getTmConfigDealerDobj().isTempRegnAllowNonTransVeh()) {
                    String exception = ownerBean.allowTempRegistrationForDealer(tmConfigDobj, userCategory, userStateCode);
                    if (exception != null) {
                        throw new VahanException("Temporary Registration not allowed for Non-Transport vehicle within State. Please select the option 'Entry-New Registration' at Home Page.");
                    }
                }
                if (Integer.parseInt(purCd) == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                    boolean flag = ownerBean.transportCatgValidation(String.valueOf(ownerBean.getOwnerDobj().getVehType()));
                    if (flag) {
                        throw new VahanException(TableConstants.VEH_CATG_ERR_MESS);
                    }
                }
            }

            Owner_dobj ownerDobj = wrapperModel.getOwnerDobj();

            // Added for DL
//            if ("DL".equals(sessionVariables.getStateCodeSelected()) && ownerDobj != null) {
//                if (ownerDobj.getVh_class() == TableConstants.ERICKSHAW_VCH_CLASS && (Integer.parseInt(purCd) == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
//                        || Integer.parseInt(purCd) == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE
//                        || Integer.parseInt(purCd) == TableConstants.VM_TRANSACTION_MAST_TEMP_REG
//                        || (Integer.parseInt(purCd) == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && (TableConstants.VM_REGN_TYPE_NEW.equals(ownerDobj.getRegn_type()) || TableConstants.VM_REGN_TYPE_TEMPORARY.equals(ownerDobj.getRegn_type()))))) {
//                    throw new VahanException("As per the state request, Registration of E-Rickshaw has been temporarily stoped.");
//                }
//            }
            // Add for PB
            if (ownerBean.getOwner_dobj_prv() != null && ownerDobj.getVehType() == TableConstants.VM_VEHTYPE_NON_TRANSPORT && ownerBean.getOwner_dobj_prv().getVehType() != ownerDobj.getVehType()) {
                ownerDobj.setPmt_type(-1);
                ownerDobj.setPmt_catg(-1);
                ownerDobj.setServicesType("");
            }
            //end

            if (ownerDobj != null && ownerDobj.getVh_class() == TableConstants.ERICKSHAW_VCH_CLASS && Integer.parseInt(purCd) == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE && "DL".equals(sessionVariables.getStateCodeSelected()) && ownerDobj.getOwner_cd() == TableConstants.OWNER_CODE_INDIVIDUAL) {
                if (ownerDobj.getOwner_identity() != null && CommonUtils.isNullOrBlank(ownerDobj.getOwner_identity().getAadhar_no())) {
                    throw new VahanException("Please enter the owner's aadhar no. for E-rickshaw.");
                }
                String aadharNo = ownerDobj.getOwner_identity().getAadhar_no();
                ownerDobj.getOwner_identity().setAadhar_no(null);
                if (!saveType.equalsIgnoreCase(TableConstants.NEW_APPL_SAVETYPE_PARTIAL)) {
                    PendencyBankDobj bankSubsidyDetail = workBench.getBankSubsidyDetailsDobj(ownerDobj, workBench.getBankSubsidyDetail(), stateCodeSelected, purCd, applNo);
                    if (bankSubsidyDetail != null) {
                        bankSubsidyDetail.setAadharNo(aadharNo);
                        new PendencyBankDetailImpl().saveOrUpdatePendencyBankDtls(tmgr, stateCodeSelected, offCode, bankSubsidyDetail, empCode);
                    }
                }
            }

            ownerBean.tradeCertificateValidation(ownerDobj.getDealer_cd(), ownerDobj.getVch_catg(), tmConfigDobj,
                    userCategory, userStateCode, offCode);

            if (tmConfigDobj.getTmConfigDmsDobj() != null && tmConfigDobj.getTmConfigDmsDobj().getPurCd().contains("," + purCd + ",") && tmConfigDobj.getTmConfigDmsDobj().getUploadActionCd().contains("," + actionCode + ",")
                    && !TableConstants.NEW_APPL_SAVETYPE_PARTIAL.equalsIgnoreCase(saveType) && ownerDobj != null && TableConstants.VM_REGN_TYPE_NEW.equals(ownerDobj.getRegn_type()) && !tmConfigDobj.getTmConfigDmsDobj().isDocsUploadAtOffice()) {
                List<VTDocumentModel> docsDetailsList = new DocumentUploadImpl().uploadedDocsDetails(applNo, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                if (docsDetailsList == null || docsDetailsList.isEmpty()) {
                    throw new VahanException("Document upload is pending. Upload your document first.");
                }
            }

            if (!ownerBean.isRenderOwnerDtlsPartialBtn()) {//OR
                ownerBean.checkLadenWeight(ownerBean.getOwnerDobj().getVch_catg(), ownerBean.getOwnerDobj().getLd_wt(), Integer.parseInt(purCd), ownerBean.getVch_purchase_as());
            }

            // code for noc endorsement
            // for different registration no
            if ((ownerDobj.getRegn_type() != null
                    && !ownerDobj.getRegn_type().equals(""))
                    && (ownerDobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                    || ownerDobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))) {
                NocImplementation noc_impl = new NocImplementation();
                noc_impl.checkForNocVerificationAndEndorsement(ownerDobj, null, userStateCode, offCode);
            }

            ownerBean.validateTempRegistrationData(ownerDobj, Integer.parseInt(purCd), ownerDobj.getRegn_type(), sessionVariables);

            /* Checkes if data exists in the HOMOLOGATION portal */
            if (ownerBean.getIsHomologationData() != null && !ownerBean.getIsHomologationData().equals("")) {
                if (ownerBean.getHomoUnLdWt() > ownerDobj.getUnld_wt()) {
                    throw new VahanException(TableConstants.ULD_WT_ERR_MESS + " {" + ownerBean.getHomoUnLdWt() + " kgs}");
                }
            }

            if (applNo == null || applNo.isEmpty()) {
                applNo = ServerUtility.getUniqueApplNo(tmgr, stateCodeSelected);
//                ServerUtility.saveUniqueApplNo(tmgr, applNo);
            }
            String regnNo = "NEW";
            comparisonBean.setAPPL_NO(applNo);
            comparisonBean.setREGN_NO(regnNo);

            // Populating the ownerDobj object
            ownerDobj.setAppl_no(applNo);
            ownerDobj.setRegn_no(regnNo);
            ownerDobj.getOwner_identity().setRegn_no(regnNo);
            ownerDobj.getOwner_identity().setAppl_no(applNo);
            ownerDobj.setFit_dt(new Date());
            ownerDobj.setRegn_dt(new Date());

            String regnType = workBench.getRegnType();
            String chasiNo = workBench.getChasiNo();
            String engineNo = workBench.getEngineNo();
            OtherStateVehDobj otherStateVehDobj = workBench.getOtherStateVehDobj();

            if (!CommonUtils.isNullOrBlank(saveType) && saveType.equalsIgnoreCase(TableConstants.NEW_APPL_SAVETYPE_PARTIAL)
                    && !(regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                    || regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))) {
                if (CommonUtils.isNullOrBlank(ownerDobj.getChasi_no())) {
                    ownerDobj.setChasi_no(chasiNo);
                }
                if (CommonUtils.isNullOrBlank(ownerDobj.getEng_no())) { // This one works
                    ownerDobj.setEng_no(engineNo);
                }
            }

//        if (dealerConfigDobj != null && owner_bean.getHomoDobj() != null) {
//            if (owner_bean.getHomoDobj().getSale_amt() > owner_dobj.getSale_amt()
//                    && (owner_dobj.getOwner_cd() == TableConstants.VEH_TYPE_STATE_GOVT || owner_dobj.getOwner_cd() == TableConstants.VEH_TYPE_GOVT)
//                    && (ACTION_CODE == TableConstants.TM_ROLE_NEW_APPL
//                    || ACTION_CODE == TableConstants.TM_ROLE_NEW_APPL_TEMP
//                    || ACTION_CODE == TableConstants.TM_ROLE_DEALER_NEW_APPL
//                    || ACTION_CODE == TableConstants.TM_ROLE_DEALER_TEMP_APPL)) {
//                ServerUtil.Compare("Home_sale_amt", owner_bean.getHomoDobj().getSale_amt(), owner_dobj.getSale_amt(), compBeanList);
//            }
//            if (dealerConfigDobj.isValidateHomoSaleAmt() && owner_dobj.getOwner_cd() != TableConstants.VEH_TYPE_STATE_GOVT && owner_dobj.getOwner_cd() != TableConstants.VEH_TYPE_GOVT) {
//                workBench.checkSaleAmount(owner_bean.getHomoDobj().getSale_amt(), owner_dobj.getSale_amt());
//            }
//        }
            /*      Insert changed data into VHA        */
            ServerUtility.insertIntoVhaChangedData(tmgr, applNo, changedData, empCode, userStateCode, offCode, tmConfigDobj.isAllowFacelessService(), userCategory);

            if (tmConfigDobj != null && tmConfigDobj.getTmConfigOtpDobj() != null && tmConfigDobj.getTmConfigOtpDobj().isOwner_mobile_verify_with_otp() && !CommonUtils.isNullOrBlank(saveType)
                    && saveType.equalsIgnoreCase(TableConstants.NEW_APPL_SAVETYPE_PARTIAL) && (Integer.parseInt(purCd) == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                    || Integer.parseInt(purCd) == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE)) {
                ServerUtility.saveUpdateOTPVerifyDetails(tmgr, ownerDobj);
            }
            if (ownerBean.getIsHomologationData() != null && !ownerBean.getIsHomologationData().equals("")
                    && ownerBean.getIsHomologationData().equals(TableConstants.HOMOLOGATION_DATA)) {
                ownerDobj.setLaser_code(TableConstants.HOMOLOGATION_DATA);
                NewImplementation.insertHomologationDetails(tmgr, ownerBean.getHomoDobj(), applNo, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());

                ownerBean.validateHomoGCW(ownerBean.getHomoDobj(), ownerDobj);
            }

            if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                    || regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                //check for Other State and Other District vehicles when data is not available in Vahan4
                if (otherStateVehDobj != null) {
                    if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)) {
                        if (otherStateVehDobj.getOldStateCD() != null && otherStateVehDobj.getOldStateCD().equalsIgnoreCase(userStateCode)) {
                            throw new VahanException("Invalid Registration Number for Other State Registration.");
                        }
                    } else if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                        if (otherStateVehDobj.getOldStateCD() != null && !otherStateVehDobj.getOldStateCD().equalsIgnoreCase(userStateCode)) {
                            throw new VahanException("Invalid Registration Number for Other RTO Registration.");
                        }
                    }
                }

                OtherStateVehImplementation impl = new OtherStateVehImplementation();
                otherStateVehDobj.setApplNo(applNo);
                impl.insertUpdateOtherStateVeh(tmgr, otherStateVehDobj, empCode, userStateCode, offCode);
                //impl.insertIntoVaOtherStateVeh(tmgr, this.otherStateVehDobj);
                if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)
                        || tmConfigDobj.isRegnNoNotAssignOthState()) {
                    VehicleParameters vehParameters = FormulaUtilities.fillVehicleParametersFromDobj(ownerDobj, sessionVariables);
                    if (otherStateVehDobj != null) {
                        vehParameters.setLOGIN_OFF_CD(offCode);
                        if (otherStateVehDobj.getOldOffCD() != null) {
                            vehParameters.setPREV_OFF_CD(otherStateVehDobj.getOldOffCD());
                        }
                    }

                    if (!isCondition(replaceTagValues(tmConfigDobj.getOther_rto_number_change(), vehParameters), "Comparison Bean Other/RTO")) {
                        regnNo = otherStateVehDobj.getOldRegnNo();
                        comparisonBean.setREGN_NO(regnNo);
                        ownerDobj.setRegn_no(regnNo);
                        ownerDobj.getOwner_identity().setRegn_no(regnNo);
                    }
                }
                ownerDobj.setRegn_dt(ownerBean.getOwnerDobj().getRegn_dt());
                ownerDobj.setRegn_upto(ownerBean.getOwnerDobj().getRegn_upto());
                ownerDobj.setFit_upto(ownerBean.getOwnerDobj().getFit_upto());
            } else if (workBench.getRegnType().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_CONFISCATED_AUCTION) && ownerBean.getOwnerDobj().getAuctionDobj() != null && ownerBean.getOwnerDobj().getAuctionDobj().getRegnNo() != null && !ownerBean.getOwnerDobj().getAuctionDobj().getRegnNo().equals("NEW")) {
                    if (ownerBean.getOwnerDobj().getAuctionDobj().getAuctionBy() != null && ownerBean.getOwnerDobj().getAuctionDobj().getAuctionBy().equals("C")) {
                        ownerDobj.setRegn_dt(ownerBean.getOwnerDobj().getAuctionDobj().getRegnDt());
                    } else {
                        ownerDobj.setRegn_dt(ownerBean.getOwnerDobj().getRegn_dt());
                    }
                if (tmConfigDobj.isRegnNoNotAssignOthState()) {
                    regnNo = ownerBean.getOwnerDobj().getAuctionDobj().getRegnNo();
                    ownerDobj.setRegn_no(regnNo);
                    ownerDobj.getOwner_identity().setRegn_no(regnNo);
                }
            } else {

                //Check Duplicate Chassis No Except in case of Vehicle from Other State / Other Office
                ServerUtility.checkChasiNoExist(ownerDobj.getChasi_no().toUpperCase());
            }

            if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_CD)) {
                CdImpl cdImpl = new CdImpl();
                ownerDobj.getCdDobj().setState_cd(ownerDobj.getState_cd());
                ownerDobj.getCdDobj().setOff_cd(ownerDobj.getOff_cd());
                ownerDobj.getCdDobj().setRegNo(regnNo);
                ownerDobj.getCdDobj().setApplNo(applNo);
                cdImpl.insertOrUpdateCdVehicleDtls(ownerDobj.getCdDobj(), tmgr);
            }

            if (Integer.parseInt(purCd) == TableConstants.VM_TRANSACTION_MAST_TEMP_REG
                    || Integer.parseInt(purCd) == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                ownerDobj.setRegn_no("TEMPREG");
                ownerDobj.getOwner_identity().setRegn_no("TEMPREG");
                ownerDobj.getDob_temp().setAppl_no(applNo);
                ownerDobj.getDob_temp().setTemp_regn_no("TEMPREG");
            }

            if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_SCRAPPED)
                    || ownerBean.isIsScrapVehicleNORetain()) {
                VehicleParameters vehParameters = FormulaUtilities.fillVehicleParametersFromDobj(ownerDobj, sessionVariables);
                if (tmConfigDobj != null && isCondition(replaceTagValues(tmConfigDobj.getScrap_veh_type(), vehParameters), "getScrapVehType")) {
                    ScrappedVehicleImpl scrapImpl = new ScrappedVehicleImpl();
                    scrapImpl.updateApplNoForScrappedVeh(regnType, applNo, stateCodeSelected,
                            offCode, tmgr);
                } else {
                    throw new VahanException("This  Vehicle Type Can't be Scrapped Registered For This State .");
                }
            }

            // Nitin Kumar: For Saving Permit Loi Details at the time of new registration when vehicle  class is Auto.
            if (!CommonUtils.isNullOrBlank(ownerDobj.getNewLoiNo()) && ownerBean.getTmConfDobj().isNew_reg_loi() && purCd.equalsIgnoreCase(String.valueOf(TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE)) && ownerDobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_NEW) && ownerDobj.getVh_class() == 57) {
                PermitImpl permitImpl = new PermitImpl();
                permitImpl.verifyLoiDetails(applNo, ownerDobj.getNewLoiNo(), ownerDobj.getState_cd(), ownerDobj.getOff_cd(), tmgr);
                permitImpl.insertNewLoiDetails(applNo, ownerDobj.getNewLoiNo(), regnNo, tmgr);
            }

            /*      Fill vehicle parameters and find the available series number for the vehicle        */
            //**********Save Advanced Number Booking
            String seriesAvailMess = "";
            VehicleParameters vehicleParameters = FormulaUtilities.fillVehicleParametersFromDobj(ownerDobj, sessionVariables);

            comparisonBean.setVehicleParameters(vehicleParameters);

            // Update tables for Advance Registration based on AdvanceRegNo or RetenRegNo
            if (ownerBean.getAdvanceRegnNoDobj() != null) {
                AdvanceRegnNoDobjModel advanceRegNoDobj = ownerBean.getAdvanceRegnNoDobj();
                advanceRegNoDobj.setRegn_appl_no(applNo);
                NewImplementation.updateAdvanceRegNoDetails(advanceRegNoDobj, ownerDobj, tmgr);
                seriesAvailMess = "Vehicle Registration No " + advanceRegNoDobj.getRegn_no() + " will be allotted (as per booked Fancy/Advance Registration No)";

                BlackListedVehicleImplementation blkImpl = new BlackListedVehicleImplementation();
                if (blkImpl.checkRegnNoForBlackList(advanceRegNoDobj.getRegn_no(), userStateCode, offCode)) {
                    throw new VahanException(advanceRegNoDobj.getRegn_no() + " : is blacklisted ");
                }
                OwnerImplementation owImpl = new OwnerImplementation();
                if (owImpl.getOwnerDetails(advanceRegNoDobj.getRegn_no(), stateCodeSelected, String.valueOf(offCode),
                        tmConfigDobj.isAllow_fitness_all_RTO()) != null) {
                    throw new VahanException(advanceRegNoDobj.getRegn_no() + " : is already Assigned ");
                }
                if (owImpl.getVaOwnerDetailsForRegnNo(advanceRegNoDobj.getRegn_no()) != null) {
                    throw new VahanException(" Application is already inwarded for Regn No: " + advanceRegNoDobj.getRegn_no());
                }
            } else if (ownerBean.getRetenRegNoDobj() != null) {
                RetenRegnNo_dobj retenRegNoDobj = ownerBean.getRetenRegNoDobj();
                retenRegNoDobj.setRegn_appl_no(applNo);
                NewImplementation.updateRetenRegNoDetails(retenRegNoDobj, tmgr);
                seriesAvailMess = "Vehicle Registration No " + retenRegNoDobj.getRegn_no() + " will be allotted (as per Retention No)";

                BlackListedVehicleImplementation blkImpl = new BlackListedVehicleImplementation();
                if (blkImpl.checkRegnNoForBlackList(retenRegNoDobj.getRegn_no(), userStateCode, offCode)) {
                    throw new VahanException(retenRegNoDobj.getRegn_no() + " : is blacklisted ");
                }

                OwnerImplementation owImpl = new OwnerImplementation();
                if (owImpl.getOwnerDetails(retenRegNoDobj.getRegn_no(), stateCodeSelected, String.valueOf(offCode),
                        tmConfigDobj.isAllow_fitness_all_RTO()) != null) {
                    throw new VahanException(retenRegNoDobj.getRegn_no() + " : is already Assigned ");
                }
                if (owImpl.getVaOwnerDetailsForRegnNo(retenRegNoDobj.getRegn_no()) != null) {
                    throw new VahanException(" Application is already inwarded for Regn No: " + retenRegNoDobj.getRegn_no());
                }
            } else {

                seriesAvailMess = ServerUtility.getAvailablePrefixSeries(vehicleParameters, userStateCode, offCode);
                if (!seriesAvailMess.equals(TableConstants.SERIES_EXHAUST_MESSAGE) && !seriesAvailMess.equals("")) {
                    seriesAvailMess = "Vehicle Registration No will be Generated from the Series " + seriesAvailMess + ".";
                }
            }

            if (TableConstants.USER_CATG_DEALER.equals(sessionVariables.getUserCatgForLoggedInUser())) {
                vehicleParameters.setPUR_CD(Integer.parseInt(purCd));
                if (isCondition(replaceTagValues(tmConfigDobj.getTmConfigDealerDobj().getRegnRestrictionAtDealer(), vehicleParameters), "getRegnRestrictionAtDealer")) {
                    throw new VahanException(tmConfigDobj.getTmConfigDealerDobj().getRegnRestrictionMessage());
                }
            }
//        }
            comparisonBean.setSeriesAvailMess(seriesAvailMess);

            // Check if temp registration dates are correct/valid
            if (ownerDobj.getTempReg() != null) {
                ownerDobj.getTempReg().setRegn_no(regnNo);
                ownerDobj.getTempReg().setAppl_no(applNo);
                if (DateUtils.compareDates(ownerDobj.getTempReg().getTmp_valid_upto(),
                        ownerDobj.getTempReg().getTmp_regn_dt()) <= 1) {
                    throw new VahanException("Temp Valid Upto can not be less than Temp Regn Dt ");
                }
            }
            // Ttested
            /*      Check fitness for the new vehicle       */
            NewVehicleFitnessImplentation newVehicleFitnessImpl = new NewVehicleFitnessImplentation();
            boolean isNewVehicleFitness = newVehicleFitnessImpl.checkForPreRegFitness(chasiNo, engineNo, userStateCode);

            if (isNewVehicleFitness && (Integer.parseInt(purCd) == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                    || Integer.parseInt(purCd) == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE)) {

                Owner_dobj preRegFitowner_dobj = null;
                OwnerImpl owner_Impl = new OwnerImpl();
                preRegFitowner_dobj = owner_Impl.set_Owner_appl_db_to_dobj(null, null, ownerDobj.getChasi_no(), Integer.parseInt(purCd));
                newVehicleFitnessImpl.saveInVtFitnessFromVtFitnessChassis(tmgr, ownerDobj.getChasi_no(), ownerDobj.getEng_no(), applNo);
                NewImplementation.insertIntoVhaOwner(tmgr, preRegFitowner_dobj.getAppl_no(), empCode);
                ServerUtility.deleteFromTable(tmgr, "", preRegFitowner_dobj.getAppl_no(), TableList.VA_OWNER);
                ServerUtility.deleteFromTable(tmgr, "", preRegFitowner_dobj.getAppl_no(), TableList.VA_TMP_REGN_DTL);
                ServerUtility.deleteFromTableByChassis(tmgr, preRegFitowner_dobj.getChasi_no(), TableList.VT_FITNESS_CHASSIS);
                new NewImplementation().insertintoVhaHomologationDetails(tmgr, preRegFitowner_dobj.getAppl_no(), empCode);
                ServerUtility.deleteFromTable(tmgr, "", preRegFitowner_dobj.getAppl_no(), TableList.VA_HOMO_DETAILS);
                RetroFittingDetailsImplementation.insertIntoVhaCng(tmgr, preRegFitowner_dobj.getAppl_no(), empCode);
                AxleImplementation.insertIntoVhaAxle(tmgr, preRegFitowner_dobj.getAppl_no(), empCode);
                FitnessImplementation.insertIntoVhaSpeedGovernor(preRegFitowner_dobj.getAppl_no(), tmgr, empCode);
                new FitnessImplementation().insertIntoVhaReflectiveTape(tmgr, preRegFitowner_dobj.getAppl_no(), empCode);
                RetroFittingDetailsImplementation.deleteFromVaRetroFittingDetails(tmgr, preRegFitowner_dobj.getAppl_no());
                AxleImplementation.deleteFromVaAxle(tmgr, preRegFitowner_dobj.getAppl_no());
                FitnessImplementation.deleteVaSpeedGovernor(preRegFitowner_dobj.getAppl_no(), tmgr);
                new FitnessImplementation().deleteVaReflectiveTape(preRegFitowner_dobj.getAppl_no(), tmgr);
                ownerDobj.setAppl_no(applNo);
            }

            String regionStr = "";
            if (ownerDobj.getRegion_covered() != null) {
                for (String region : ownerDobj.getRegion_covered()) {
                    regionStr += region + ",";
                }
            }
            if (!CommonUtils.isNullOrBlank(regionStr)) {
                ownerDobj.setRegion_covered_str(regionStr);
            } else {
                ownerDobj.setRegion_covered_str(null);
            }

            /*      Insert/Update vehicle owner     */
            NewImplementation.insertOrUpdateVaOwner(tmgr, ownerDobj, empCode, Integer.parseInt(purCd), offCode, userStateCode, actionCode);
            if (userStateCode.equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(ownerDobj.getVh_class()) + ",")) {
                NewImplementation.insertOrUpdateVaOwnerOther(tmgr, ownerDobj, empCode, userStateCode, offCode);
            }
            /*      Check validity/authenticity of ownerShipType and Insurance      */
            InsDobj insDobj = wrapperModel.getInsDobj();

            if (!CommonUtils.isNullOrBlank(insDobj.getPolicy_no())) {
                if (InsImplementation.validateOwnerCodeWithInsType(ownerDobj.getOwner_cd(), insDobj.getIns_type())) {
                    InsImplementation.insertUpdateInsurance(tmgr, applNo, regnNo, insDobj, stateCodeSelected, offCode, empCode);
                } else {
                    throw new VahanException("Invalid Combination of Ownership Type & Insurance Type.");
                }
            }

            if (trailerDobj != null && trailerDobj.getChasi_no() != null && !trailerDobj.getChasi_no().trim().isEmpty()) {
                // Checks if values aren't empty
                TrailerImplementation.validationTrailer(trailerDobj);
                Trailer_dobj validateTrailerChassisDobj = TrailerImplementation.checkTrailerChassis_owner(trailerDobj.getChasi_no());
                if (validateTrailerChassisDobj != null) {
                    throw new VahanException("Duplicate Trailer Chassis No." + validateTrailerChassisDobj.getDup_chassis()
                            + " against the registration no " + validateTrailerChassisDobj.getRegn_no() + " State "
                            + ServerUtility.getStateNameByStateCode(validateTrailerChassisDobj.getState_cd()) + " and Office "
                            + ServerUtility.getOfficeName(validateTrailerChassisDobj.getOff_cd(), validateTrailerChassisDobj.getState_cd()));
                } else {
                    validateTrailerChassisDobj = TrailerImplementation.checkTrailerChassis_trailer(trailerDobj.getChasi_no());
                    if (validateTrailerChassisDobj != null) {
                        throw new VahanException("Duplicate Trailer Chassis No." + validateTrailerChassisDobj.getDup_chassis()
                                + " against the registration no " + validateTrailerChassisDobj.getRegn_no() + " State "
                                + ServerUtility.getStateNameByStateCode(validateTrailerChassisDobj.getState_cd()) + " and Office "
                                + ServerUtility.getOfficeName(validateTrailerChassisDobj.getOff_cd(), validateTrailerChassisDobj.getState_cd()));
                    } else {
                        TrailerImplementation.insertUpdateTrailer(tmgr, applNo, regnNo, ownerDobj.getChasi_no(), trailerDobj,
                                empCode, userStateCode, offCode);
                    }
                }
            }

            /**
             * Not Tested HPA Bean Details
             */
            if (hpaBean.getHpaDobj().getHp_type() != null && !hpaBean.getHpaDobj().getHp_type().equalsIgnoreCase("-1")) {
                hpaBean.getHpaDobj().setRegn_no(regnNo);
                hpaBean.getHpaDobj().setAppl_no(applNo);
                String errorHpa = validateHPAEntry(hpaBean.getHpaDobj());
                ArrayList listHpaDobj = new ArrayList();
                listHpaDobj.add(hpaBean.getHpaDobj());
                if (errorHpa.length() > 0) {
                    throw new VahanException(errorHpa);
                } else {
                    if (!workBench.isHypothecated()) {// delete from va_hpa during Hypothication not Selected
                        HpaImplementation.insertDeleteFromVaHpa(tmgr, hpaBean.getHpaDobj().getAppl_no(), empCode);
                    } else {
                        if (listHpaDobj.size() > 1) {
                            throw new VahanException("Multiple Hypothecation not allowed.");
                        }
                        HpaImplementation.insertUpdateHPA(tmgr, listHpaDobj, stateCodeSelected, offCode, empCode);// use hpa_entry_bean
                    }
                }
            }

            /**
             * Not Tested ExArmyVehicle Details
             */
            if (ownerBean.isExArmyVehicle_Visibility_tab()) {
                String error_exArmy = validateExArmyForm(exArmyBean);
                if (error_exArmy.length() > 0) {
                    wrapperModel.setContextMessageModel(new ContextMessageModel(MessageContext.FACES, MessageSeverity.INFO, false, error_exArmy, "Error..."));
                } else {
                    ExArmyDobj exArmy_dobj = exArmyBean.setExArmyBean_To_Dobj();
                    ExArmyImplementation.saveExArmyVehicleDetails_Impl(exArmy_dobj, applNo, tmgr, empCode, userStateCode, offCode);
                }
            } else {
                ExArmyImplementation.insertIntoVhaExArmy(tmgr, applNo, empCode);
                ExArmyImplementation.deleteFromVaExArmy(tmgr, applNo);
            }

            /**
             * Not tested Imported Vehicle Details
             */
            if (ownerBean.isImportedVehicle_Visibility_tab() && !ownerBean.isRenderOwnerDtlsPartialBtn()) {
                String error_impVehicle = validateImpVehicleForm(importedVehicleBean);
                if (error_impVehicle.length() > 0) {
                    wrapperModel.setContextMessageModel(new ContextMessageModel(MessageContext.FACES, MessageSeverity.INFO, false, error_impVehicle, "Error..."));
                } else {
                    ImportedVehicleDobj impDobj = importedVehicleBean.setBean_to_Dobj();
                    ImportedVehicleImplementation.saveImportedDetails_Impl(impDobj, applNo, tmgr, empCode, userStateCode, offCode);
                }
            } else {
                ImportedVehicleImplementation.insertIntoVhaImpVeh(tmgr, applNo, empCode);
                ImportedVehicleImplementation.deleteFromVaImp(tmgr, applNo);
            }

            /**
             * Not tested Axle Details
             */
            if ((ownerBean.isAxleDetail_Visibility_tab()
                    && !ownerBean.isRenderOwnerDtlsPartialBtn()) || (isNewVehicleFitness && axleBean.getTf_Other1() != null && !"".equalsIgnoreCase(axleBean.getTf_Other1()))) {
                String error_axleDetail = validateAxleForm(axleBean);
                if (axleBean.getTf_Other1() != null && axleBean.getTf_Other1().length() != 0 && axleBean.getTf_Other_tyre() == 0) {
                    wrapperModel.setContextMessageModel(new ContextMessageModel(MessageContext.REQUEST, MessageSeverity.INFO, true, "Alert!", " Please fill Other Tyre!  "));
                    return wrapperModel;
                }
                if (axleBean.getTf_Other_tyre() > 0 && (axleBean.getTf_Other1() == null || axleBean.getTf_Other1().equals(""))) {
                    wrapperModel.setContextMessageModel(new ContextMessageModel(MessageContext.REQUEST, MessageSeverity.INFO, true, "Alert!", " Please fill Other !  "));
//                RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", " Please fill Other !  "));
                    return wrapperModel;
                }
                if (axleBean.getTf_Tandem1() != null && axleBean.getTf_Tandem1().length() != 0 && axleBean.getTf_Tandem_tyre() == 0) {
                    wrapperModel.setContextMessageModel(new ContextMessageModel(MessageContext.REQUEST, MessageSeverity.INFO, true, "Alert!", " Please fill Tandem Tyre!  "));
                    return wrapperModel;
                }
                if (axleBean.getTf_Tandem_tyre() > 0 && (axleBean.getTf_Tandem1() == null || axleBean.getTf_Tandem1().equals(""))) {
                    wrapperModel.setContextMessageModel(new ContextMessageModel(MessageContext.REQUEST, MessageSeverity.INFO, true, "Alert!", " Please fill Tandem !  "));
                    return wrapperModel;
                }

                if (error_axleDetail.length() > 0) {
                    wrapperModel.setContextMessageModel(new ContextMessageModel(MessageContext.FACES, MessageSeverity.INFO, false, error_axleDetail, "Error..."));
                } else {
                    AxleDetailsDobj axleDobj = axleBean.setBean_To_Dobj();
                    AxleImplementation.saveAxleDetails_Impl(axleDobj, applNo, tmgr, empCode, userStateCode, offCode);
                }
            } else {
                AxleImplementation.insertIntoVhaAxle(tmgr, applNo, empCode);
                AxleImplementation.deleteFromVaAxle(tmgr, applNo);
            }

            /**
             * Note tested Retro Fitting Details
             */
            if ((ownerBean.isCngDetails_Visibility_tab() && !ownerBean.isRenderOwnerDtlsPartialBtn()) || (isNewVehicleFitness && !(CommonUtils.isNullOrBlank(cngDetailsBean.getTf_kit_no())))) {
                String error_cngDetail = validateCngForm(cngDetailsBean);
                if (error_cngDetail.length() > 0) {
                    wrapperModel.setContextMessageModel(new ContextMessageModel(MessageContext.FACES, MessageSeverity.INFO, false, error_cngDetail, "Error..."));
                } else {
                    RetroFittingDetailsDobj cng_dobj = cngDetailsBean.setBean_To_Dobj();
                    RetroFittingDetailsImplementation.saveCngVehicleDetails_Impl(cng_dobj, applNo, tmgr, empCode, userStateCode, offCode);
                }
            } else {
                RetroFittingDetailsImplementation.insertIntoVhaCng(tmgr, applNo, empCode);
                RetroFittingDetailsImplementation.deleteFromVaRetroFittingDetails(tmgr, applNo);
            }

            /*This check is used to restrict the Diseel Vehicle Registration in Delhi Only */
            String isCriteriaMatchMsg = ServerUtility.isNewRegnNotAllowed(vehicleParameters, userStateCode, offCode);
            if (isCriteriaMatchMsg != null && !isCriteriaMatchMsg.isEmpty()) {
                throw new VahanException(isCriteriaMatchMsg);
            }

            /*  check for DL validation according to the state configuration    */
            OwnerIdentificationImpl identificationImpl = new OwnerIdentificationImpl();
            TmConfigurationOwnerIdentificationDobj tmConfigOwnerId = identificationImpl.getTmConfigurationOwnerIdentification(userStateCode);
            /*      Check if DL is valid if DL is mandatory for the given state.        */
            if (tmConfigOwnerId != null) {
                VehicleParameters vehParameters = FormulaUtilities.fillVehicleParametersFromDobj(ownerDobj, sessionVariables);
                if (tmConfigOwnerId.isDl_required() != null
                        && isCondition(replaceTagValues(tmConfigOwnerId.isDl_required(), vehParameters), "isDl_required()")) {
                    if (ownerDobj.getOwner_identity() != null
                            && (ownerDobj.getOwner_identity().getDl_no() == null || ownerDobj.getOwner_identity().getDl_no().trim().isEmpty())) {
                        throw new VahanException("Invalid Driving Licence No, It is Mandatory to Fill Proper DL No on this Vehicle.");
                    }
                }
            }

            /*      Validate Purchase       */
            OwnerImplementation.validatePurchaseAs(ownerDobj, regnType, Integer.parseInt(purCd));

            // Validating Vehicle Norms
            ArrayList<Status_dobj> applicationDetails = ServerUtility.applicationStatusByApplNo(applNo, sessionVariables.getStateCodeSelected());
            Date applDt = new Date();
            if (!applicationDetails.isEmpty()) {
                vehicleParameters.setAPPL_DATE(applicationDetails.get(0).getAppl_dt());
                applDt = DateUtil.parseDate(DateUtil.convertStringYYYYMMDDToDDMMYYYY(applicationDetails.get(0).getAppl_dt()));
            } else {
                vehicleParameters.setAPPL_DATE(DateUtil.getCurrentDate_YYYY_MM_DD());
            }
            if (!CommonUtils.isNullOrBlank(saveType)) {
                if ((saveType.equalsIgnoreCase(TableConstants.NEW_APPL_SAVETYPE_PARTIAL) && ownerBean.getHomoDobj() != null) || saveType.equalsIgnoreCase(TableConstants.NEW_APPL_SAVETYPE_COMPLETE)) {
                    vehicleParameters.setPUR_CD(Integer.parseInt(purCd));
                    vehicleParameters.setACTION_CD(actionCode);
                    boolean isValidForRegn = ServerUtility.validateVehicleNorms(ownerDobj, Integer.parseInt(purCd), vehicleParameters, tmConfigDobj.getTmConfigDealerDobj());
                    if (!isValidForRegn) {
                        throw new VahanException("State Transport Department has not authorized you to enter NEW Registration Application for '" + MasterTableFiller.masterTables.VM_VH_CLASS.getDesc(ownerDobj.getVh_class() + "") + "' with emission norms " + MasterTableFiller.masterTables.VM_NORMS.getDesc(ownerDobj.getNorms() + "") + " , Purchase Date: " + DateUtil.parseDateToString(ownerDobj.getPurchase_dt()) + " , Application Date: " + DateUtil.parseDateToString(applDt) + ", please contact respective Registering Authority regarding this.");
                    }
                    ownerBean.validatePurchaseDate(applDt); // Validate purchase date.
                    // fasTag validation here 
                    if (tmConfigDobj != null && tmConfigDobj.getTmConfigurationFasTag() != null) {
                        if (isCondition(replaceTagValues(tmConfigDobj.getTmConfigurationFasTag().getFasTagCondition(), vehicleParameters), "getFasTagCondition()")) {
                            workBench.setRenderFasTagDialog(true);
                            FasTagDetailsDobj fasTagDobjByService = null;
                            FasTagDetailsDobj fasTagDobj = FasTagImpl.getFasTagDetails(ownerDobj.getChasi_no(), ownerDobj.getRegn_no(), ownerDobj.getRegn_type());
                            if (fasTagDobj == null) {
                                try {
                                    fasTagDobjByService = FasTagImpl.getFasTagDetailsByService(ownerDobj.getChasi_no(), ownerDobj.getEng_no());
                                } catch (VahanException ve) {
                                    if (tmConfigDobj.getTmConfigurationFasTag().isFasTagMandatory()) {
                                        throw ve;
                                    }
                                }
                            } else {
                                workBench.setIsFasTagInstalled(true);
                                if (!TableConstants.VM_REGN_TYPE_NEW.equals(ownerDobj.getRegn_type()) && !TableConstants.VM_REGN_TYPE_TEMPORARY.equals(ownerDobj.getRegn_type())
                                        && !TableConstants.VM_REGN_TYPE_EXARMY.equals(ownerDobj.getRegn_type())) {
                                    new FasTagImpl().moveToHistory(tmgr, ownerDobj, sessionVariables.getEmpCodeLoggedIn());
                                }
                            }
                            if (fasTagDobj == null && fasTagDobjByService == null) {
                                if (tmConfigDobj.getTmConfigurationFasTag().isFasTagMandatory()) {
                                    throw new VahanException("FASTag installation is mandatory, first install the FASTag then try again !!!");
                                }
                            } else if (fasTagDobjByService != null) {
                                workBench.setIsFasTagInstalled(true);
                                FasTagImpl.insertFasTagDetails(tmgr, ownerDobj, sessionVariables.getEmpCodeLoggedIn(), fasTagDobjByService);
                            }
                        }
                    }
                }
            } else {
                throw new VahanException("Something Went Wrong, Please go to HOME page and try again.");
            }

            //validation of seating capicity based on vehicle class
            if (!CommonUtils.isNullOrBlank(saveType)
                    && !saveType.equalsIgnoreCase(TableConstants.NEW_APPL_SAVETYPE_PARTIAL)) {
                OwnerImplementation ownerImpl = new OwnerImplementation();
                ownerImpl.seatingCapacityValidation(ownerDobj.getSeat_cap(), ownerDobj.getVh_class(), TableConstants.VH_CLASS_ALLOWED_ZERO_SEAT_CAP);

                ServerUtility.saveVaOwnerDisclaimerDetails(ownerDobj, tmgr, sessionVariables.getEmpCodeLoggedIn(), actionCode);

                if (ownerBean.getHomoDobj() == null) {
                    ownerBean.validateHomoMaker(ownerDobj);
                }
                ServerUtility.validateMinInsuranceValidity(insBean.getSelectedYear(), ownerDobj.getVh_class(), ownerDobj.getRegn_type(), Integer.parseInt(purCd), insBean.getInsType());
            }

            /*      Get the status object       */
            Status_dobj status = new Status_dobj();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            String dt = sdf.format(new Date());
            status.setAppl_dt(dt);
            status.setAppl_no(applNo);
            status.setRegn_no(regnNo);
            status.setPur_cd(Integer.parseInt(purCd));
            if (Integer.parseInt(purCd) == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                    && actionCode == TableConstants.TM_ROLE_NEW_APPL) {
                //    status.setAction_cd(TableConstants.TM_ROLE_NEW_REGISTRATION_FEE);
                status.setEmp_cd(0);
            } else if (Integer.parseInt(purCd) == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE
                    && actionCode == TableConstants.TM_ROLE_DEALER_NEW_APPL) {
                //     status.setAction_cd(TableConstants.TM_ROLE_DEALER_NEW_REGISTRATION_FEE);
                status.setEmp_cd(Long.valueOf(empCode));
                ServerUtility.insertDealerHSRPPendencyDetails(tmgr, ownerDobj, ownerDobj.getRegn_no(), tmConfigDobj);
            } else if (Integer.parseInt(purCd) == TableConstants.VM_TRANSACTION_MAST_TEMP_REG
                    && actionCode == TableConstants.TM_ROLE_NEW_APPL_TEMP) {
                //    status.setAction_cd(TableConstants.TM_ROLE_TEMP_REGISTRATION_FEE);
                status.setRegn_no("TEMPREG");
            } else if (Integer.parseInt(purCd) == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE
                    && actionCode == TableConstants.TM_ROLE_DEALER_TEMP_APPL) {
                status.setEmp_cd(Long.valueOf(empCode));
                status.setRegn_no("TEMPREG");
            }

            // Insert or update speed governor Dobj in VA
            if (ownerDobj.getSpeedGovernorDobj() != null) {
                ownerDobj.getSpeedGovernorDobj().setAppl_no(applNo);
                ownerDobj.getSpeedGovernorDobj().setRegn_no(status.getRegn_no());
                ownerDobj.getSpeedGovernorDobj().setState_cd(sessionVariables.getStateCodeSelected());
                ownerDobj.getSpeedGovernorDobj().setOff_cd(sessionVariables.getOffCodeSelected());
                FitnessImplementation.insertUpdateVaSpeedGovernor(ownerDobj.getSpeedGovernorDobj(), tmgr, empCode);
            }

            // Insert or update reflective tape Dobj in VA
            if (ownerDobj.getReflectiveTapeDobj() != null) {
                ownerDobj.getReflectiveTapeDobj().setApplNo(applNo);
                ownerDobj.getReflectiveTapeDobj().setRegn_no(status.getRegn_no());
                ownerDobj.getReflectiveTapeDobj().setStateCd(sessionVariables.getStateCodeSelected());
                ownerDobj.getReflectiveTapeDobj().setOffCd(sessionVariables.getOffCodeSelected());
                new FitnessImplementation().insertOrUpdateVaReflectiveTape(tmgr, ownerDobj.getReflectiveTapeDobj(), empCode);
            }

            status.setOffice_remark("");
            status.setPublic_remark("");
            status.setStatus("C");
            status.setOff_cd(offCode);
            status.setState_cd(stateCodeSelected);
            int initialFlow[] = ServerUtility.getInitialAction(tmgr, status.getState_cd(), Integer.parseInt(purCd), vehicleParameters);

            if (initialFlow != null) {
                status.setFlow_slno(initialFlow[1]);
                status.setFile_movement_slno(initialFlow[1]);
                status.setAction_cd(initialFlow[0]);
            } else {
                throw new VahanException("Please contact to System Administrator to configure the file flow for the Purpose " + MasterTableFiller.masterTables.TM_PURPOSE_MAST.getDesc(purCd));
            }

            if (otherStateVehDobj != null && vehicleParameters != null) {
                vehicleParameters.setLOGIN_OFF_CD(offCode);
                if (otherStateVehDobj.getOldOffCD() != null) {
                    vehicleParameters.setPREV_OFF_CD(otherStateVehDobj.getOldOffCD());
                }
            }
            status.setVehicleParameters(vehicleParameters);

            /*  Insert the application in the VA_STATUS table if it doesn't exist there  */
            ArrayList<Status_dobj> applicationStatus = ServerUtility.applicationStatusByApplNo(applNo, status.getState_cd());
            if (applicationStatus.isEmpty()) {
                ServerUtility.fileFlowForNewApplication(tmgr, status, userId, clientIpAddress);
            }

            if (!CommonUtils.isNullOrBlank(applNo) && !CommonUtils.isNullOrBlank(saveType) && saveType.equalsIgnoreCase(TableConstants.NEW_APPL_SAVETYPE_PARTIAL)) {
            } else {
                ServerUtility.webServiceForNextStage(status, counterId, applNo, actionCode, Integer.parseInt(purCd), null, tmgr,
                        selectedRoleCode, empCode, offCode);
                ServerUtility.fileFlow(tmgr, status, actionCode, selectedRoleCode, userStateCode, offCode, empCode, userCategory, clientIpAddress,
                        tmConfigDobj.isAllowFacelessService(), tmConfigDobj.isDefacement());
            }

            tmgr.commit();

            workBench.setOtherStateVehDobj(otherStateVehDobj);

            wrapperModel.setTmConfigDobj(tmConfigDobj);
            wrapperModel.setWorkBench(workBench);
            wrapperModel.setComparisonBean(comparisonBean);
            wrapperModel.setOwnerDobj(ownerDobj);
            wrapperModel.setStatusDobj(status);

        } catch (VahanException e) {
            throw e;
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                tmgr.release();
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(NewRegnController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return wrapperModel;
    }

    /**
     * @author Deependra Singh
     */
    @PostMapping("/save/final")
    public Map<String, String> saveFinalDataRecords(@RequestBody OwnerBeanModel ownerModel) {
        Map<String, String> saveDetails = new TreeMap<String, String>();

        if (Integer.parseInt(ownerModel.getVehType()) == TableConstants.VM_VEHTYPE_TRANSPORT) {
            saveDetails.put("Vehicle Type", "Transport");
        }
        if (Integer.parseInt(ownerModel.getVehType()) == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
            saveDetails.put("Vehicle Type", "Non-Transport");
        }
        if (ownerModel.getSale_amt() != null) {
            saveDetails.put("Sale Amount", ownerModel.getSale_amt().toString());
        }

        if (ownerModel.getOwnerDobj().getVch_catg() != null) {
            String vmCatgLabel = ServerUtility.getCustomLableFromSelectedListToShow(ownerModel.getList_vm_catg(), ownerModel.getOwnerDobj().getVch_catg());
            saveDetails.put("Vehicle Category", vmCatgLabel);
        }
        if (ownerModel.getOwnerDobj().getVh_class() > 0) {
            String vhClassLabel = ServerUtility.getCustomLableFromSelectedListToShow(ownerModel.getList_vh_class(), String.valueOf(ownerModel.getOwnerDobj().getVh_class()));

            saveDetails.put("Vehicle Class", vhClassLabel);
        }
        return saveDetails;
    }

    @PostMapping("/receipt/validate")
    public WrapperModel receiptChecker(@RequestBody WrapperModel wrapperModel) throws VahanException {
        String AdvRegnRetenRadiobtn = wrapperModel.getOwnerBean().getAdvRegnRetenRadiobtn();
        RetenRegnNo_dobj retenRegNoDobj = wrapperModel.getOwnerBean().getRetenRegNoDobj();
        AdvanceRegnNoDobjModel advanceRegnNoDobj = wrapperModel.getOwnerBean().getAdvanceRegnNoDobj();
        TempRegDobj tempRegDobj = wrapperModel.getOwnerBean().getTempReg();

        String stateCodeSelected = wrapperModel.getSessionVariables().getStateCodeSelected();
        int offCodeSelected = wrapperModel.getSessionVariables().getOffCodeSelected();
        Owner_dobj ownerDobj = wrapperModel.getOwnerDobj();
        TmConfigurationDobj tmConfigDobj = wrapperModel.getTmConfigDobj();
        List list_adv_district = wrapperModel.getOwnerBean().getList_adv_district();

        if (AdvRegnRetenRadiobtn.equals("retenno")) {
            String rcptno = retenRegNoDobj.getRecp_no();
            if (rcptno == null || rcptno.isEmpty()) {
                return null;
            }
            AdvanceRegnFeeImpl.checkOfficeOnReceipt(rcptno, stateCodeSelected, offCodeSelected);
            Date rcptDate = NewImplementation.getRetainNoRcptDate(rcptno, stateCodeSelected, offCodeSelected);
            if (rcptDate != null) {
                if ("SK".contains(stateCodeSelected)) {
                    if (ownerDobj.getOwner_cd() == TableConstants.VEH_TYPE_GOVT
                            || ownerDobj.getOwner_cd() == TableConstants.VEH_TYPE_STATE_GOVT
                            || ownerDobj.getOwner_cd() == TableConstants.VEH_TYPE_GOVT_UNDERTAKING) {
                        // Do Nothing
                    } else {
                        try {
                            NewImplementation.validationRetainNoRcptDate(tmConfigDobj, rcptno, rcptDate);
                        } catch (VahanException ex) {
                            wrapperModel.getOwnerBean().setRetRcptMsg(rcptno + " has been expire on " + DateUtil.parseDate(rcptDate) + ". If you want to continue Please pay 5000/year amount at the time of fee, otherwise cancel and proceed !!!");
                            wrapperModel.setContextMessageModel(new ContextMessageModel(MessageContext.REQUEST, "PF('retExpire').show();"));
//                            RequestContext.getCurrentInstance().execute("PF('retExpire').show();");
                            return wrapperModel;
                        }
                    }
                    // Do Nothing
                } else {
                    NewImplementation.validationRetainNoRcptDate(tmConfigDobj, rcptno, rcptDate);
                }
                RetenRegnNo_dobj dobj = NewImpl.getRetenRegNoDetails(rcptno);
                wrapperModel.getOwnerBean().setRetenRegNoDobj(dobj);
                // To be done in OwnerBean
//                    setRetenRegNoDobj(dobj);
                String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
                // To be done in OwnerBean
//                    list_adv_district.clear();

                for (int i = 0; i < data.length; i++) {
                    if (data[i][2].trim().equals(retenRegNoDobj.getState_cd())) {
                        list_adv_district.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
                wrapperModel.getOwnerBean().setList_adv_district(list_adv_district);
            } else {
                throw new VahanException("Receipt Date Not Found");
            }
        } else {
            String rcptno = advanceRegnNoDobj.getRecp_no();
            if (rcptno == null || rcptno.isEmpty()) {
                return null;
            }
            Date rcptDate = NewImplementation.getFancyNoRcptDate(rcptno, stateCodeSelected, offCodeSelected);
            if (rcptDate != null) {
                NewImplementation.validationFancyRcptDate(tmConfigDobj, rcptno, rcptDate);
                NewImplementation.validateFancyCubicCapacity(stateCodeSelected, ownerDobj.getCubic_cap(), rcptno, offCodeSelected, ownerDobj.getOwner_cd());
                if ("UP".contains(stateCodeSelected) && ownerDobj != null) {
                    int dayToBeAdded = 0;
                    Date purchaseDate = ownerDobj.getPurchase_dt();
                    int result = 0;
                    if (purchaseDate != null) {
                        Date comparedDate = purchaseDate;
                        if (TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE.equalsIgnoreCase(ownerDobj.getRegn_type())
                                || TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE.equalsIgnoreCase(ownerDobj.getRegn_type())) {
                            dayToBeAdded = 30;
                        } else if (TableConstants.VM_REGN_TYPE_NEW.equalsIgnoreCase(ownerDobj.getRegn_type())) {
                            dayToBeAdded = 7;
                        } else if (TableConstants.VM_REGN_TYPE_TEMPORARY.equalsIgnoreCase(ownerDobj.getRegn_type())) {
                            if (tempRegDobj == null || tempRegDobj.getTmp_valid_upto() == null) {
                                throw new VahanException("Please select valid Temporary Registration Validity Date");
                            }
                            comparedDate = tempRegDobj.getTmp_valid_upto();
                        }

                        comparedDate = ServerUtility.dateRange(comparedDate, 0, 0, dayToBeAdded);
                        result = DateUtils.compareDates(comparedDate, rcptDate);
                        if (result == 1) {
                            throw new VahanException("Fancy Registration Fee Collected After " + dayToBeAdded + " Days Of Purchase Date");
                        }
                    }
                }
                AdvanceRegnNoDobjModel dobj = NewImplementation.getAdvanceRegNoDetails(rcptno, stateCodeSelected, offCodeSelected);
                wrapperModel.getOwnerBean().setAdvanceRegnNoDobj(dobj);
                // To be done in OwnerBean
//                setAdvanceRegNoDobj(dobj);
                if (ownerDobj != null) {
                    NewImplementation.validateFancyVehicleCategory(dobj, ownerDobj.getVch_catg());
                }

                String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
                // To be done in OwnerBean
//                list_adv_district.clear();

                for (int i = 0; i < data.length; i++) {
                    if (data[i][2].trim().equals(advanceRegnNoDobj.getState_cd())) {
                        list_adv_district.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
            } else {
                throw new VahanException("Receipt Date Not Found");
            }
        }
        return wrapperModel;
    }

    @PostMapping("/fee/save")
    public WrapperModel saveNewRegistrationFee(@RequestBody WrapperModel wrapperModel, @RequestParam String clientIpAddress,
            @RequestParam String selectedRoleCode) throws VahanException, Exception {
        NewVehicleFeeBeanModel newVehicleFeeBeanModel = wrapperModel.getNewVehicleFeeBeanModel();
        wrapperModel.setNewVehicleFeeBeanModel(newVehicleFeeBeanModel);

        List<TaxFormPanelBean> listTaxForm = newVehicleFeeBeanModel.getListTaxForm();
        String regnNo = newVehicleFeeBeanModel.getRegn_no();
        String applNo = newVehicleFeeBeanModel.getAppl_no();
        Owner_dobj ownerDobj = newVehicleFeeBeanModel.getOwnerDobj();
        PaymentCollectionBeanModel paymentBean = newVehicleFeeBeanModel.getPaymentBeanModel();

        SessionVariablesModel sessionVariables = wrapperModel.getSessionVariables();
        String empCode = sessionVariables.getEmpCodeLoggedIn();
        String stateCode = sessionVariables.getStateCodeSelected();
        String userStateCode = stateCode;
        int offCode = sessionVariables.getSelectedWork().getOff_cd();
        int actionCode = sessionVariables.getActionCodeSelected();

        int purCode = newVehicleFeeBeanModel.getPur_cd();
        String applDt = newVehicleFeeBeanModel.getAppl_dt();
        TmConfigurationDobj tmConfigDobj = wrapperModel.getTmConfigDobj();

        String rcptNo[] = null;
        long checkTotalAmount = 0L;
        long manualRcptAmount = 0l;
        TmConfigurationReceipts configFeeFineZero = null;

        // List of all the individual taxes
        Fee_Pay_Dobj feePayDobj = new Fee_Pay_Dobj();
        feePayDobj.setIsNewCollectionForm(newVehicleFeeBeanModel.isNewRegistration());
        feePayDobj.setBlnTempRegn(newVehicleFeeBeanModel.isTempRegistration());
        //By Afzal
        //feePayDobj.setCollectedFeeList(getFeePanelBean().getPayableFeeCollectionList());

        feePayDobj.setCash(paymentBean.getCash());
        feePayDobj.setExcessAmount(paymentBean.getExcessAmount());
        // For Tax Collection Information
        // Goes through
        if (newVehicleFeeBeanModel.isNewRegistration() || newVehicleFeeBeanModel.isTempRegistration()) {
            if (newVehicleFeeBeanModel.isRenderTaxPanel()) {
                if (listTaxForm.size() > 0) {

                    List<Tax_Pay_Dobj> listTaxDobj = new ArrayList<>();

                    for (TaxFormPanelBean bean : listTaxForm) {
                        if (!bean.getTaxMode().equals("0")) {
                            Tax_Pay_Dobj taxDobj = new Tax_Pay_Dobj();

                            taxDobj.setFinalTaxAmount(bean.getFinalTaxAmount());
                            taxDobj.setTotalPaybaleTax(bean.getTotalPaybaleTax());
                            taxDobj.setTotalPaybalePenalty(bean.getTotalPaybalePenalty());
                            taxDobj.setTotalPaybaleSurcharge(bean.getTotalPaybaleSurcharge());
                            taxDobj.setTotalPaybaleRebate(bean.getTotalPaybaleRebate());
                            taxDobj.setTotalPaybaleInterest(bean.getTotalPaybaleInterest());
                            taxDobj.setFinalTaxFrom(bean.getFinalTaxFrom());
                            taxDobj.setFinalTaxUpto(bean.getFinalTaxUpto());
                            taxDobj.setTaxMode(bean.getTaxMode());
                            taxDobj.setPur_cd(bean.getPur_cd());
                            taxDobj.setRegnNo(regnNo);
                            taxDobj.setPaymentMode(paymentBean.getPayment_mode());
                            taxDobj.setTaxBreakDetails(bean.getTaxDescriptionList());
                            if (ownerDobj != null) {
                                taxDobj.setRegnNo(ownerDobj.getRegn_no());
                                taxDobj.setApplNo(ownerDobj.getAppl_no());
                            }

                            taxDobj.setDeal_cd(empCode);
                            taxDobj.setOff_cd(offCode);

                            taxDobj.setOp_dt(new java.util.Date());
                            taxDobj.setRcptDate(new java.util.Date());
                            //taxDobj.setPayMode("1");
                            // feePayDobj.setCollectedTaxDobj(taxDobj);
                            taxDobj.setApplNo(applNo);
                            taxDobj.setNoOfAdvUnits(bean.getNoOfUnits());
                            taxDobj.setTotalPaybaleTax1(bean.getTotalTax1());
                            taxDobj.setTotalPaybaleTax2(bean.getTotalTax2());
                            listTaxDobj.add(taxDobj);
                        }
                    }
                    feePayDobj.setListTaxDobj(listTaxDobj);
                    feePayDobj.setPermitDobj(newVehicleFeeBeanModel.getPermitPanelBeanModel().getPermitDobj());
                }
            }
        }
        feePayDobj.setRegnNo(ownerDobj.getRegn_no());
        feePayDobj.setOp_dt(new java.util.Date());
        feePayDobj.setRegnNo(regnNo);

        feePayDobj.setApplNo(applNo.toUpperCase());
        feePayDobj.setRcptDt(new java.util.Date());
        feePayDobj.setPaymentMode(paymentBean.getPayment_mode());

        FeeDraftDobj feeDraftDobj = null;
        // Skips
        if (!nic.vahan.CommonUtils.Utility.isNullOrBlank(paymentBean.getPayment_mode()) && (!paymentBean.getPayment_mode().equals("-1"))
                && (!paymentBean.getPayment_mode().equals("C"))) {

            feeDraftDobj = new FeeDraftDobj();
            FeeDraftimpl feeDraftImpl = new FeeDraftimpl();
            String pay_mode = paymentBean.getPayment_mode();
            feeDraftDobj.setAppl_no(applNo);
            feeDraftDobj.setFlag("A");
            feeDraftDobj.setCollected_by(empCode);
            feeDraftDobj.setState_cd(stateCode);
            feeDraftDobj.setOff_cd(String.valueOf(offCode));
            feeDraftDobj.setDraftPaymentList(paymentBean.getPaymentlist());
        }
        feePayDobj.setOwnerDobj(ownerDobj);
        configFeeFineZero = ServerUtil.getTmConfigurationReceipts(sessionVariables.getStateCodeSelected());
        // List containing each type of tax levied for the vehicle
        List<FeeDobj> feeDobjList = newVehicleFeeBeanModel.getFeeCollectionLists();
        // Goes through
        if (feeDobjList != null) {
            for (FeeDobj feedobj : feeDobjList) {
                if (feedobj.getPurCd() == 99 || feedobj.getPurCd() == 80) {
                    continue;
                }
                Long totalAmount = feedobj.getTotalAmount();
                int pur_cd = feedobj.getPurCd();
                if (pur_cd == -1) {
                    throw new VahanException("Please select purpose of fee collection.");
                }
                VehicleParameters vehParameters = FormulaUtilities.fillVehicleParametersFromDobj(ownerDobj, sessionVariables);
                vehParameters.setPUR_CD(pur_cd);
                vehParameters.setTRANSACTION_PUR_CD(pur_cd);
                if ((totalAmount == null || totalAmount == 0L)) {
                    if (configFeeFineZero != null) {
                        if (!isCondition(replaceTagValues(configFeeFineZero.getFeeAmtZero(), vehParameters), "saveNewRegistrationFee-tm_configuration-Fee_amt_zero(State:" + stateCode + ")")) {
                            throw new VahanException("Record cannot be saved with zero value for " + ServerUtil.getTaxHead(feedobj.getPurCd()));
                        }
                    } else {
                        throw new VahanException("Record cannot be saved with zero value for " + ServerUtil.getTaxHead(feedobj.getPurCd()));
                    }
                }
            }
        }

        List<Tax_Pay_Dobj> taxDobjList = feePayDobj.getListTaxDobj();
        // Goes through
        if (taxDobjList != null) {
            for (Tax_Pay_Dobj taxdobj : taxDobjList) {
                int pur_cd = taxdobj.getPur_cd();
                if (taxdobj.getTaxMode().equals("E")) {
                    continue;
                }
                Long totalAmount = taxdobj.getFinalTaxAmount();
                if (totalAmount <= 0) {
                    if (taxdobj.getTotalPaybaleTax() <= 0 || taxdobj.getFinalTaxAmount() < 0) {
                        throw new VahanException("Record cannot be saved with zero value for " + ServerUtil.getTaxHead(taxdobj.getPur_cd()));
                    }
                }
            }
        }

        List<FeeDobj> feeCollectionCloneLists = new ArrayList<>(newVehicleFeeBeanModel.getFeeCollectionLists());
        // Skips

        if (newVehicleFeeBeanModel.isRenderUserChargesAmountPanel()) {
            FeeDobj userCharge = new FeeDobj();
            userCharge.setFeeAmount((long) newVehicleFeeBeanModel.getTotalUserChrg());
            userCharge.setFineAmount((long) 0l);
            userCharge.setPurCd(99);
            newVehicleFeeBeanModel.getFeePanelBeanModel().getFeeCollectionList().add(userCharge);
            feeCollectionCloneLists.add(userCharge);
        }

        // Goes through
        if (newVehicleFeeBeanModel.isRenderSmartCardFeePanel()) {
            FeeDobj smtCrdCharge = new FeeDobj();
            smtCrdCharge.setFeeAmount((long) newVehicleFeeBeanModel.getSmartCardFee());
            smtCrdCharge.setFineAmount((long) 0l);
            smtCrdCharge.setPurCd(80);
            newVehicleFeeBeanModel.getFeePanelBeanModel().getFeeCollectionList().add(smtCrdCharge);
            feeCollectionCloneLists.add(smtCrdCharge);

        }

        feePayDobj.setCollectedFeeList(feeCollectionCloneLists);

        /*This check is used to restrict the Disel Vehicle Registration in Delhi Only */
        // Goes through
        if (ownerDobj != null) {
            VehicleParameters vehParameters = FormulaUtilities.fillVehicleParametersFromDobj(ownerDobj, sessionVariables);
            String isCriteriaMatchMsg = ServerUtility.isNewRegnNotAllowed(vehParameters, userStateCode, offCode);
            if (isCriteriaMatchMsg != null && !isCriteriaMatchMsg.isEmpty()) {
                throw new VahanException(isCriteriaMatchMsg);
            }
            vehParameters.setPUR_CD(purCode);
            vehParameters.setACTION_CD(actionCode);
            vehParameters.setAPPL_DATE(DateUtil.convertStringDDMMMYYYYToYYYYMMDD(applDt));
            boolean isValidForRegn = ServerUtility.validateVehicleNorms(ownerDobj, purCode, vehParameters, tmConfigDobj.getTmConfigDealerDobj());
            if (!isValidForRegn) {
                throw new VahanException("State Transport Department has not authorized you to take Payment for '" + MasterTableFiller.masterTables.VM_VH_CLASS.getDesc(ownerDobj.getVh_class() + "") + "' with emission norms " + MasterTableFiller.masterTables.VM_NORMS.getDesc(ownerDobj.getNorms() + "") + ", please contact respective Registering Authority regarding this.");
            }
        }
        feePayDobj.setTaxInstallment(newVehicleFeeBeanModel.isTaxInstallment());
        feePayDobj.setTaxInstallMode(newVehicleFeeBeanModel.getTaxInstallMode());

        // Skips
        if (newVehicleFeeBeanModel.isRenderPermitPanel() && newVehicleFeeBeanModel.getPermitPanelBeanModel().getPermitDobj() != null) {
            newVehicleFeeBeanModel.compareChanges();
            if (!newVehicleFeeBeanModel.getCompBeanList().isEmpty()) {
                String changedData = ComparisonBeanImpl.changedDataContents(newVehicleFeeBeanModel.getCompBeanList());
                if (feePayDobj.getPermitDobj() != null) {
                    feePayDobj.getPermitDobj().setChangeData(changedData);
                }
            }
        }
        // Goes through
        if (newVehicleFeeBeanModel.getPaymentTypeBtn().equals("RtoPayment")) {
            rcptNo = new FeeImplementation().saveFeeDetailsInstrument(feePayDobj, feeDraftDobj, sessionVariables, selectedRoleCode, clientIpAddress);
            newVehicleFeeBeanModel.setRcptNo(rcptNo);
        } else if (newVehicleFeeBeanModel.getPaymentTypeBtn().equals("OnlinePayment")) {
            boolean isManualRcptAttach = false;
            if (feePayDobj.getCollectedFeeList() != null && !feePayDobj.getCollectedFeeList().isEmpty()) {
                for (FeeDobj dobj : feePayDobj.getCollectedFeeList()) {
                    if (dobj.getPurCd() == TableConstants.VM_MAST_MANUAL_RECEIPT) {
                        manualRcptAmount = Math.abs(dobj.getTotalAmount());
                        continue;
                    }
                    Long totalAmount = dobj.getTotalAmount();
                    checkTotalAmount = checkTotalAmount + totalAmount;
                }
            }

            List<Tax_Pay_Dobj> taxList = feePayDobj.getListTaxDobj();
            if (taxList != null && !taxList.isEmpty()) {
                for (Tax_Pay_Dobj taxdobj : taxList) {
                    if (taxdobj.getPur_cd() == TableConstants.TM_ROAD_TAX && manualRcptAmount > 0L
                            && taxdobj.getTotalPaybaleTax() > manualRcptAmount && taxdobj.getFinalTaxAmount() > manualRcptAmount) {
                        taxdobj.setTotalPaybaleTax(taxdobj.getTotalPaybaleTax() - manualRcptAmount);
                        taxdobj.setFinalTaxAmount(taxdobj.getFinalTaxAmount() - manualRcptAmount);
                        isManualRcptAttach = true;
                    }
                    Long totalAmount = taxdobj.getFinalTaxAmount();
                    checkTotalAmount = checkTotalAmount + totalAmount;
                }
            }
            if (manualRcptAmount > 0l && !isManualRcptAttach) {
                throw new VahanException("You can not procceed Online Payment as Tax amount is less than Manual receipt amount. ");
            }
            String userPwd = new FeeImplementation().saveOnlinePaymentData(feePayDobj, checkTotalAmount, applNo, ownerDobj.getOwner_identity().getMobile_no(),
                    stateCode, offCode, empCode, null, null, null, isManualRcptAttach, false, userStateCode);
            newVehicleFeeBeanModel.setUserPwd(userPwd);
            if (userPwd == null) {
                throw new VahanException("Error in Saving Details !!");
            }
        }

        newVehicleFeeBeanModel.setFeePayDobj(feePayDobj);
        newVehicleFeeBeanModel.setFeeDeaftDobj(feeDraftDobj);
        newVehicleFeeBeanModel.setFeeCollectionLists(feeCollectionCloneLists);

        return wrapperModel;
    }

    private String validateHPAEntry(HpaDobj hpaDobj) {
        String errorMsg = "";
        if (hpaDobj == null) {
            return errorMsg = "HPA Details are blank";
        }
        if (CommonUtils.isNullOrBlank(hpaDobj.getRegn_no())) {
            errorMsg = "Blank Registration Number";
        } else if (CommonUtils.isNullOrBlank(hpaDobj.getHp_type())) {
            errorMsg = "Blank HP Type";
        } else if (CommonUtils.isNullOrBlank(hpaDobj.getFncr_name())) {
            errorMsg = "Blank Financer Name";
        } else if (CommonUtils.isNullOrBlank(hpaDobj.getFncr_add1())) {
            errorMsg = "Blank Financer Address";
        } else if (CommonUtils.isNullOrBlank(hpaDobj.getFncr_state())) {
            errorMsg = "Blank Financer State";
        } else if (hpaDobj.getFncr_district() == 0) {
            errorMsg = "Blank Financer District";
        } else if (hpaDobj.getFncr_pincode() == null || hpaDobj.getFncr_pincode() == 0) {
            errorMsg = "Blank Financer Pin Code";
        }
        return errorMsg;
    }

    private String validateExArmyForm(ExArmyBeanModel exArmyBean) {
        String errorMsg = "";
        if (exArmyBean.getTf_Voucher_no().equalsIgnoreCase("")) {
            errorMsg = "Blank Voucher number.";
        } else if (exArmyBean.getTf_VoucherDate() == null) {
            errorMsg = "Blank Voucher Date.";
        } else if (exArmyBean.getTf_POP().equalsIgnoreCase("")) {
            errorMsg = "Blank Place of Purchase.";
        }
        return errorMsg;
    }

    private String validateImpVehicleForm(ImportedVehicleBeanModel importedVehicleBean) {
        String errorMsg = "";
        if (importedVehicleBean.getCm_country_imp() == 0) {
            errorMsg = "Blank Country Name.";
        } else if (importedVehicleBean.getTf_dealer_imp().equalsIgnoreCase("")) {
            errorMsg = "Blank Dealer Name.";
        } else if (importedVehicleBean.getTf_foreign_imp().equalsIgnoreCase("")) {
            errorMsg = "Blank Foreign Registration Number.";
        } else if (importedVehicleBean.getTf_place_imp().equalsIgnoreCase("")) {
            errorMsg = "Blank Place of RTO Office/Purchase.";
        } else if (importedVehicleBean.getTf_YOM_imp() == null) {
            errorMsg = "Blank Year of Manufacture.";
        }
        return errorMsg;
    }

    private String validateAxleForm(AxleBeanModel axleBean) {
        String errorMsg = "";
        if (axleBean.getTf_Front1().equalsIgnoreCase("")) {
            errorMsg = "Blank Front Axle Detail";
        } else if (axleBean.getTf_Rear1().equalsIgnoreCase("")) {
            errorMsg = "Blank Rear Axle Detail";
        } else if (axleBean.getTf_Front() == null) {
            errorMsg = "Blank Front Axle Detail";
        } else if (axleBean.getTf_Rear() == null) {
            errorMsg = "Blank Rear Axle Detail";
        }
        return errorMsg;
    }

    private String validateCngForm(RetroFittingDetailsBeanModel cngDetailsBean) {
        String errorMsg = "";
        if (CommonUtils.isNullOrBlank(cngDetailsBean.getTf_kit_no())) {
            errorMsg = "Blank Kit Number Detail";
        } else if (CommonUtils.isNullOrBlank(cngDetailsBean.getTf_kit_type())) {
            errorMsg = "Blank Kit Type Detail";
        } else if (CommonUtils.isNullOrBlank(cngDetailsBean.getTf_workshop())) {
            errorMsg = "Blank Workshop Detail";
        } else if (CommonUtils.isNullOrBlank(cngDetailsBean.getTf_license_no())) {
            errorMsg = "Blank Workshop License Number";
        } else if (CommonUtils.isNullOrBlank(cngDetailsBean.getTf_manu_name())) {
            errorMsg = "Blank Manufacture Name";
        } else if (CommonUtils.isNullOrBlank(cngDetailsBean.getTf_cyc_sr_no())) {
            errorMsg = "Blank Cyclinder Sr. Number";
        } else if (CommonUtils.isNullOrBlank(cngDetailsBean.getTf_poll_norms())) {
            errorMsg = "Blank Pollution Norms Details";
        } else if (CommonUtils.isNullOrBlank(cngDetailsBean.getTf_approv_lettr_no())) {
            errorMsg = "Blank Approval Letter Number";
        } else if (cngDetailsBean.getCal_approv_dt() == null) {
            errorMsg = "Blank Approval Date";
        }
        return errorMsg;
    }

    @PostMapping("/fee/addToCart")
    public WrapperModel addToCart(@RequestBody WrapperModel wrapperModel, @RequestParam String clientIpAddress,
            @RequestParam String selectedRoleCode) throws VahanException, Exception {
        int roadTaxCount = 0;
        int newRegnFeeAndTempFee = 0;
        long checkTotalAmount = 0L;
        VehicleParameters vehParameters = null;
        String stateCd = null;
        int offCd = 0;
        String empCode = null;
        int actionCode = 0;
        String userCategory = null;
        TmConfigurationDealerDobj tmDealerConfigDobj = null;
        TmConfigurationReceipts configFeeFineZero = null;

        NewVehicleFeeBeanModel newVehicleFeeBeanModel = wrapperModel.getNewVehicleFeeBeanModel();
        PermitPanelBeanModel permitPanelBean = newVehicleFeeBeanModel.getPermitPanelBeanModel();

        List<TaxFormPanelBean> listTaxForm = newVehicleFeeBeanModel.getListTaxForm();
        String regnNo = newVehicleFeeBeanModel.getRegn_no();
        String applNo = newVehicleFeeBeanModel.getAppl_no();
        int purCode = newVehicleFeeBeanModel.getPur_cd();
        String applDt = newVehicleFeeBeanModel.getAppl_dt();
        PaymentCollectionBeanModel paymentBean = newVehicleFeeBeanModel.getPaymentBeanModel();

        if (applNo == null || applNo.equals("")) {
            return null;
        }

        TmConfigurationDobj tmConfigDobj = wrapperModel.getTmConfigDobj();
        SessionVariablesModel sessionVariables = wrapperModel.getSessionVariables();
        Owner_dobj ownerDobj = wrapperModel.getOwnerDobj();

        if (sessionVariables.getStateCodeSelected() != null && sessionVariables.getOffCodeSelected() != 0 && sessionVariables.getEmpCodeLoggedIn() != null && tmConfigDobj != null && tmConfigDobj.getTmConfigDealerDobj() != null) {
            stateCd = sessionVariables.getStateCodeSelected();
            offCd = sessionVariables.getOffCodeSelected();
            empCode = sessionVariables.getEmpCodeLoggedIn();
            tmDealerConfigDobj = tmConfigDobj.getTmConfigDealerDobj();
            actionCode = sessionVariables.getActionCodeSelected();
            userCategory = sessionVariables.getUserCatgForLoggedInUser();
        } else {
            return null;
        }

        configFeeFineZero = ServerUtility.getTmConfigurationReceipts(sessionVariables.getStateCodeSelected());
        vehParameters = FormulaUtilities.fillVehicleParametersFromDobj(ownerDobj, sessionVariables);

        if (newVehicleFeeBeanModel.isRenderPermitPanel() && permitPanelBean.getPermitDobj() != null) {
            String permitTaxDtl = newVehicleFeeBeanModel.getPermitTaxDtl();
            String permitRefreshDtl = newVehicleFeeBeanModel.getPermitRefreshDtl();
            String permitFeeDtl = newVehicleFeeBeanModel.getPermitFeeDtl();

            permitRefreshDtl = permitPanelBean.getPermitDobj().toString();
            if (permitTaxDtl != null && !permitTaxDtl.equals(permitRefreshDtl)) {
                wrapperModel.setContextMessageModel(new ContextMessageModel(MessageContext.FACES, MessageSeverity.WARN, true,
                        "Tax is Calculated for Different Permit Details,Please Press Get Fee-Tax Details button",
                        "Tax is Calculated for Different Permit Details,Please Press Get Fee-Tax Details button"));
                return wrapperModel;
            }

            if (permitFeeDtl != null && !permitFeeDtl.equals(permitRefreshDtl)) {
                wrapperModel.setContextMessageModel(new ContextMessageModel(MessageContext.FACES, MessageSeverity.WARN, true,
                        "Fee is Calculated for Different Permit Details,Please Press Get Fee-Tax Details button",
                        "Fee is Calculated for Different Permit Details,Please Press Get Fee-Tax Details button"));
                return wrapperModel;
            }
        }

        if (ownerDobj != null) {
            if (newVehicleFeeBeanModel.isNewRegistration()) {
                String seriesAvailableStatus = ServerUtility.getAvailablePrefixSeries(vehParameters, stateCd, offCd);
                if (seriesAvailableStatus.equalsIgnoreCase(TableConstants.SERIES_EXHAUST_MESSAGE)) {
                    throw new VahanException(TableConstants.SERIES_EXHAUST_MESSAGE);
                }

                String isCriteriaMatchMsg = ServerUtility.isNewRegnNotAllowed(vehParameters, stateCd, offCd);
                if (isCriteriaMatchMsg != null && !isCriteriaMatchMsg.isEmpty()) {
                    throw new VahanException(isCriteriaMatchMsg);
                }
            }

            vehParameters.setPUR_CD(purCode);
            vehParameters.setACTION_CD(actionCode);
            vehParameters.setAPPL_DATE(DateUtil.convertStringDDMMMYYYYToYYYYMMDD(applDt));
            boolean isValidForRegn = ServerUtility.validateVehicleNorms(ownerDobj, purCode, vehParameters, tmDealerConfigDobj);
            if (!isValidForRegn) {
                throw new VahanException("State Transport Department has not authorized you to take Online Payment for '" + MasterTableFiller.masterTables.VM_VH_CLASS.getDesc(ownerDobj.getVh_class() + "") + "' with emission norms " + MasterTableFiller.masterTables.VM_NORMS.getDesc(ownerDobj.getNorms() + "") + ", please contact respective Registering Authority regarding this.");
            }
        }

        Fee_Pay_Dobj feePayDobj = new Fee_Pay_Dobj();

        List<FeeDobj> feeDobjList = newVehicleFeeBeanModel.getFeeCollectionLists();
        if (feeDobjList != null) {
            for (FeeDobj feedobj : feeDobjList) {
                if (feedobj.getPurCd() == 99 || feedobj.getPurCd() == 80) {
                    continue;
                }
            }
        }

        if (newVehicleFeeBeanModel.isNewRegistration() || newVehicleFeeBeanModel.isTempRegistration()) {
            if (newVehicleFeeBeanModel.isRenderTaxPanel()) {
                List<DOTaxDetail> taxBreakUpList = null;
                if (listTaxForm.size() > 0) {
                    List<Tax_Pay_Dobj> listTaxDobj = new ArrayList<Tax_Pay_Dobj>();

                    taxBreakUpList = new ArrayList();
                    for (TaxFormPanelBean bean : listTaxForm) {
                        if (!bean.getTaxMode().equals("0")) {
                            taxBreakUpList.addAll(bean.getTaxDescriptionList());
                            Tax_Pay_Dobj taxDobj = new Tax_Pay_Dobj();

                            taxDobj.setFinalTaxAmount(bean.getFinalTaxAmount());
                            taxDobj.setTotalPaybaleTax(bean.getTotalPaybaleTax());
                            taxDobj.setTotalPaybalePenalty(bean.getTotalPaybalePenalty());
                            taxDobj.setTotalPaybaleSurcharge(bean.getTotalPaybaleSurcharge());
                            taxDobj.setTotalPaybaleRebate(bean.getTotalPaybaleRebate());
                            taxDobj.setTotalPaybaleInterest(bean.getTotalPaybaleInterest());
                            taxDobj.setFinalTaxFrom(bean.getFinalTaxFrom());
                            taxDobj.setFinalTaxUpto(bean.getFinalTaxUpto());
                            taxDobj.setTaxMode(bean.getTaxMode());
                            taxDobj.setPur_cd(bean.getPur_cd());
                            taxDobj.setRegnNo(regnNo);
                            taxDobj.setPaymentMode(paymentBean.getPayment_mode());
                            taxDobj.setTaxBreakDetails(bean.getTaxDescriptionList());
                            if (ownerDobj != null) {
                                taxDobj.setRegnNo(ownerDobj.getRegn_no());
                                taxDobj.setApplNo(ownerDobj.getAppl_no().toUpperCase());
                            }

                            taxDobj.setDeal_cd(empCode);
                            taxDobj.setOff_cd(offCd);

                            taxDobj.setOp_dt(new java.util.Date());
                            taxDobj.setRcptDate(new java.util.Date());
                            taxDobj.setApplNo(applNo);
                            taxDobj.setNoOfAdvUnits(bean.getNoOfUnits());
                            taxDobj.setTotalPaybaleTax1(bean.getTotalTax1());
                            taxDobj.setTotalPaybaleTax2(bean.getTotalTax2());
                            listTaxDobj.add(taxDobj);
                        }
                    }

                    feePayDobj.setListTaxDobj(listTaxDobj);
                    feePayDobj.setPermitDobj(permitPanelBean.getPermitDobj());
                }
            }
        }
        feePayDobj.setOp_dt(new java.util.Date());
        feePayDobj.setRegnNo(regnNo);
        feePayDobj.setApplNo(applNo.toUpperCase());
        feePayDobj.setRcptDt(new java.util.Date());
        feePayDobj.setPaymentMode(paymentBean.getPayment_mode());

        List<FeeDobj> feeCollectionCloneLists = new ArrayList<>(newVehicleFeeBeanModel.getFeeCollectionLists());
        if (newVehicleFeeBeanModel.isRenderUserChargesAmountPanel()) {
            FeeDobj userCharge = new FeeDobj();
            userCharge.setFeeAmount((long) newVehicleFeeBeanModel.getTotalUserChrg());
            userCharge.setFineAmount((long) 0l);
            userCharge.setPurCd(99);
            newVehicleFeeBeanModel.getFeePanelBeanModel().getFeeCollectionList().add(userCharge);
            feeCollectionCloneLists.add(userCharge);
        }
        if (newVehicleFeeBeanModel.isRenderSmartCardFeePanel()) {
            FeeDobj smtCrdCharge = new FeeDobj();
            smtCrdCharge.setFeeAmount((long) newVehicleFeeBeanModel.getSmartCardFee());
            smtCrdCharge.setFineAmount((long) 0l);
            smtCrdCharge.setPurCd(80);
            newVehicleFeeBeanModel.getFeePanelBeanModel().getFeeCollectionList().add(smtCrdCharge);
            feeCollectionCloneLists.add(smtCrdCharge);
        }

        feePayDobj.setCollectedFeeList(feeCollectionCloneLists);
        boolean flg = false;
        if (listTaxForm != null) {
            for (TaxFormPanelBean bean : listTaxForm) {
                if (!bean.getTaxMode().equals("0")) {
                    flg = true;
                }
            }

            if (newVehicleFeeBeanModel.isRenderTaxPanel() && listTaxForm.size() > 0 && !flg) {
                wrapperModel.setContextMessageModel(new ContextMessageModel(MessageContext.FACES, MessageSeverity.WARN, true,
                        "Please select Tax Mode", "Please select Tax Mode"));
                return wrapperModel;
            }
        }

        if (feePayDobj.getCollectedFeeList() != null && !feePayDobj.getCollectedFeeList().isEmpty()) {
            for (FeeDobj dobj : feePayDobj.getCollectedFeeList()) {
                int pur_cd = dobj.getPurCd();
                if (purCode == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                    if (pur_cd == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE || pur_cd == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {
                        newRegnFeeAndTempFee++;
                    }
                } else if (purCode == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE) {
                    if (pur_cd == TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
                        newRegnFeeAndTempFee++;
                    }
                }
                Long totalAmount = dobj.getTotalAmount();
                checkTotalAmount = checkTotalAmount + totalAmount;
                vehParameters.setTRANSACTION_PUR_CD(pur_cd);
                if ((totalAmount == null || totalAmount == 0L)) {
                    if (configFeeFineZero != null) {
                        if (!isCondition(replaceTagValues(configFeeFineZero.getFeeAmtZero(), vehParameters), "addToCart-tm_configuration-Fee_amt_zero(State:" + stateCd + ")")) {
                            throw new VahanException("Fee Head / Fee Amount cannot be zero value for " + ServerUtility.getTaxHead(pur_cd));
                        }
                    } else {
                        throw new VahanException("Fee Head / Fee Amount cannot be zero value for " + ServerUtility.getTaxHead(pur_cd));
                    }
                }
            }

            if (newRegnFeeAndTempFee <= 0) {
                throw new VahanException("Not Valid For add To Cart");
            }
        }

        List<Tax_Pay_Dobj> taxDobjList = feePayDobj.getListTaxDobj();
        if (taxDobjList != null && !taxDobjList.isEmpty()) {
            for (Tax_Pay_Dobj taxdobj : taxDobjList) {
                int pur_cd = taxdobj.getPur_cd();
                Long totalAmount = taxdobj.getFinalTaxAmount();
                checkTotalAmount = checkTotalAmount + totalAmount;
                if (totalAmount == null || totalAmount == 0L || pur_cd <= 0) {
                    if (taxdobj.getTotalPaybaleTax() <= 0 || taxdobj.getFinalTaxAmount() < 0) {
                        throw new VahanException("Record cannot be saved with zero value for " + ServerUtility.getTaxHead(taxdobj.getPur_cd()));
                    }
                }
            }
        }

        if (newVehicleFeeBeanModel.getTotalAmountPayable() == 0l) {
            List<OnlinePayDobj> payDobj = new FeeImplementation().moveFileWithZeroAmtAtDealerPoint(feePayDobj, applNo, empCode, stateCd, offCd, checkTotalAmount, actionCode, selectedRoleCode,
                    userCategory, clientIpAddress, tmConfigDobj, purCode, sessionVariables.getUserLoginOffCode(), sessionVariables.getUserIdForLoggedInUser());
            newVehicleFeeBeanModel.setPayDobj(payDobj);
        }

        if (feePayDobj.getCollectedFeeList() != null && !feePayDobj.getCollectedFeeList().isEmpty()) {
            if (purCode == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
                vehParameters.setPUR_CD(purCode);
                EpayImplementation.checkTempFeeInNewRegn(tmConfigDobj, purCode, sessionVariables.getOffCodeSelected(), stateCd, feePayDobj, sessionVariables.getEmpCodeLoggedIn(), vehParameters);
                if (taxDobjList == null) {
                    if ((tmDealerConfigDobj.isTaxExemptionAllowed() && (isCondition(replaceTagValues(tmConfigDobj.getTax_exemption(), vehParameters), "addToCart-2"))) || "NEWFT".equalsIgnoreCase(newVehicleFeeBeanModel.getFEE_ACTION())) {
                        new FeeImplementation().saveAddToCartDetails(feePayDobj, applNo, checkTotalAmount, stateCd, offCd, empCode,
                                tmConfigDobj, actionCode, selectedRoleCode, userCategory, clientIpAddress);
                    } else {
                        throw new VahanException("Problem in tax Details!!!.");
                    }
                } else {
                    if (!taxDobjList.isEmpty()) {
                        for (Tax_Pay_Dobj taxdobj : taxDobjList) {
                            int pur_cd = taxdobj.getPur_cd();
                            if (pur_cd == TableConstants.TM_ROAD_TAX || ("KL".equals(stateCd) && pur_cd == TableConstants.TM_CESS_TAX)) {
                                roadTaxCount++;
                                break;
                            }
                        }
                        if (roadTaxCount > 0) {
                            new FeeImplementation().saveAddToCartDetails(feePayDobj, applNo, checkTotalAmount, stateCd, offCd, empCode,
                                    tmConfigDobj, actionCode, selectedRoleCode, userCategory, clientIpAddress);
                        } else {
                            throw new VahanException("Problem in tax Details!!!.");
                        }
                    }
                }
            } else {
                new FeeImplementation().saveAddToCartDetails(feePayDobj, applNo, checkTotalAmount, stateCd, offCd, empCode,
                        tmConfigDobj, actionCode, selectedRoleCode, userCategory, clientIpAddress);
            }
        } else {
            throw new VahanException("Payable Amount cannot be zero.Please check Fee and Tax Details!!!.");
        }

        wrapperModel.setNewVehicleFeeBeanModel(newVehicleFeeBeanModel);
        return wrapperModel;
    }

    @PostMapping("/fee/reverBack")
    public WrapperModel reverBackForRectification(@RequestBody WrapperModel wrapperModel, @RequestParam String clientIpAddress,
            @RequestParam String selectedRoleCode) throws VahanException {
        String stateCode;
        int offCode;
        String empCode;
        int actionCode;
        String userCategory;
        SessionVariablesModel sessionVariables = wrapperModel.getSessionVariables();
        if (sessionVariables != null) {
            stateCode = sessionVariables.getStateCodeSelected();
            offCode = sessionVariables.getOffCodeSelected();
            empCode = sessionVariables.getEmpCodeLoggedIn();
            actionCode = sessionVariables.getActionCodeSelected();
            userCategory = sessionVariables.getUserCatgForLoggedInUser();
        } else {
            return null;
        }

        NewVehicleFeeBeanModel newVehicleFeeBeanModel = wrapperModel.getNewVehicleFeeBeanModel();
        int purCd = newVehicleFeeBeanModel.getPur_cd();
        String applNo = newVehicleFeeBeanModel.getAppl_no();
        Owner_dobj ownerDobj = newVehicleFeeBeanModel.getOwnerDobj();

        int prevAcCode = 0;
        prevAcCode = ServerUtility.getPreviousActionCode(actionCode, purCd, applNo,
                FormulaUtilities.fillVehicleParametersFromDobj(ownerDobj, sessionVariables), stateCode);
        Status_dobj status_dobj = new Status_dobj();
        status_dobj.setPrev_action_cd_selected(prevAcCode);
        status_dobj.setAppl_no(applNo);
        status_dobj.setPur_cd(purCd);
        status_dobj.setStatus(TableConstants.STATUS_REVERT);
        status_dobj.setSeat_cd(TableConstants.STATUS_REVERT);

        TmConfigurationDobj tmConfigDobj = wrapperModel.getTmConfigDobj();
        FeeImplementation.revertBackForRectification(status_dobj, empCode, stateCode, offCode, actionCode, selectedRoleCode, userCategory, clientIpAddress,
                tmConfigDobj.isAllowFacelessService(), tmConfigDobj.isDefacement());

        return wrapperModel;
    }

    @PostMapping("/fee/updateForNewRegAppl")
    public WrapperModel updateForNewRegAppl(@RequestBody WrapperModel wrapperModel) throws VahanException, Exception {
        TmConfigurationDobj tmConfigDobj = wrapperModel.getTmConfigDobj();

        SessionVariablesModel sessionVariables = wrapperModel.getSessionVariables();
        String stateCode = sessionVariables.getStateCodeSelected();
        int offCode = sessionVariables.getOffCodeSelected();
        String userCategory = sessionVariables.getUserCatgForLoggedInUser();

        NewVehicleFeeBeanModel newVehicleFeeBeanModel = wrapperModel.getNewVehicleFeeBeanModel();
        wrapperModel.setNewVehicleFeeBeanModel(newVehicleFeeBeanModel);
        Owner_dobj ownerDobj = newVehicleFeeBeanModel.getOwnerDobj();
        OwnerDetailsDobj ownerDetailsDobj = newVehicleFeeBeanModel.getOwnerDetailsDobj();
        newVehicleFeeBeanModel.setOwnerDobj(ownerDobj);
        newVehicleFeeBeanModel.setOwnerDetailsDobj(ownerDetailsDobj);

        String applNo = newVehicleFeeBeanModel.getAppl_no();
        int purCd = newVehicleFeeBeanModel.getPur_cd();
        String FEE_ACTION = newVehicleFeeBeanModel.getFEE_ACTION();

        PermitPanelBeanModel permitPanelBean = newVehicleFeeBeanModel.getPermitPanelBeanModel();
        FormFeePanelBeanModel feePanelBeanModel = newVehicleFeeBeanModel.getFeePanelBeanModel();
        List<FeeDobj> feeCollectionLists = newVehicleFeeBeanModel.getFeeCollectionLists();
        newVehicleFeeBeanModel.setPermitPanelBeanModel(permitPanelBean);
        newVehicleFeeBeanModel.setFeePanelBeanModel(feePanelBeanModel);
        newVehicleFeeBeanModel.setFeeCollectionLists(feeCollectionLists);

        OwnerImplementation ownerImplementation = new OwnerImplementation();
        FeeImplementation feeImplementation = new FeeImplementation();

        if (ownerDobj == null) {
            ownerDobj = ownerImplementation.set_Owner_appl_db_to_dobj(null, applNo.toUpperCase(), "", purCd, stateCode, offCode);
        }
        if (ownerDetailsDobj == null) {//need to remove, It is Unnecessary to use.
            ownerDetailsDobj = ownerImplementation.getVaOwnerDetails(applNo.toUpperCase(), stateCode, offCode, userCategory, tmConfigDobj.isAllow_fitness_all_RTO());
        }

        if (ownerDobj == null) {
            throw new VahanException("No Application Details Found :" + applNo);
        }

        if (newVehicleFeeBeanModel.isRenderPermitPanel() && permitPanelBean.getPermitDobj() != null) {
            newVehicleFeeBeanModel.setPermitFeeDtl(permitPanelBean.getPermitDobj().toString());
        }

        ownerDobj.setAppl_no(applNo.toUpperCase());
        newVehicleFeeBeanModel.setChasino(ownerDobj.getChasi_no());
        //by komal
        boolean isVehicleHypothecated = feeImplementation.isHypothecated(ownerDobj.getAppl_no().toUpperCase(), purCd);
        ownerDobj.setHypothecatedFlag(isVehicleHypothecated);

        if (ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_TEMPORARY)) {
            newVehicleFeeBeanModel.setRenderTempregPanel(true);
            newVehicleFeeBeanModel.setTempReg(ownerDobj.getTempReg());
            newVehicleFeeBeanModel.setBlnDisableRegnTypeTemp(true);
            newVehicleFeeBeanModel.setRenderTempregPanel(true);
            List list_c_state = new ArrayList();
            newVehicleFeeBeanModel.setList_c_state(list_c_state);

            list_c_state = MasterTableFiller.getStateList();

            if (newVehicleFeeBeanModel.getTempReg() != null && newVehicleFeeBeanModel.getTempReg().getTmp_state_cd() != null) {
                List list_office_to = new ArrayList();
                list_office_to = MasterTableFiller.getOfficeList(newVehicleFeeBeanModel.getTempReg().getTmp_state_cd());
                newVehicleFeeBeanModel.setList_office_to(list_office_to);
            }

            List list_dealer_cd = new ArrayList();
            newVehicleFeeBeanModel.setList_dealer_cd(list_dealer_cd);
            List<Dealer> listDealer = ServerUtil.getDealerList(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());

            for (Dealer dl : listDealer) {
                list_dealer_cd.add(new SelectItem(dl.getDealer_cd(), dl.getDealer_name()));
            }

        }
        if (ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)) {
            ownerDobj.setOtherStateVehDobj(new OtherStateVehImplementation().setOtherVehicleDetailsToDobj(applNo));
        }

        if (ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_CD)) {
            newVehicleFeeBeanModel.setRenderCdPanel(true);
        }

        ownerDobj.setAppl_no(applNo);
        if (permitPanelBean != null && permitPanelBean.getPermitDobj() != null) {
            String[] regionCoveredArr = permitPanelBean.getPermitDobj().getRegionCoveredArr();
            if (regionCoveredArr != null && regionCoveredArr.length > 0) {
                permitPanelBean.getPermitDobj().setRegion_covered(String.valueOf(regionCoveredArr.length));
                permitPanelBean.getPermitDobj().setMultiRegion(new NewImplementation().checkMultiRegion(applNo));
            }
        }

        // Automatic Fee Panel Updation
        // Setting mandatory Fee For this Form
        List<EpayDobj> mandatoryFeeList = EpayImplementation.getFeeDetailsByAction(ownerDobj, FEE_ACTION,
                ownerDobj.getVh_class(), ownerDobj.getVch_catg(), permitPanelBean.getPermitDobj(), new java.util.Date(),
                sessionVariables, purCd, tmConfigDobj);
        if (mandatoryFeeList != null && !mandatoryFeeList.isEmpty()) {
            feePanelBeanModel.strictResetPaymentList();
        }
        VehicleParameters vehParams = FormulaUtilities.fillVehicleParametersFromDobj(ownerDobj, sessionVariables);
        TmConfigurationReceipts configFeeFineZero = ServerUtility.getTmConfigurationReceipts(sessionVariables.getStateCodeSelected());
        for (EpayDobj feeDobj : mandatoryFeeList) {

            FeeDobj collectedFeeDobj = new FeeDobj();
            collectedFeeDobj.setFeeAmount((long) feeDobj.getE_TaxFee());
            collectedFeeDobj.setFineAmount((long) feeDobj.getE_FinePenalty());
            collectedFeeDobj.setPurCd(feeDobj.getPurCd());
            collectedFeeDobj.setDisableDropDown(true);
            collectedFeeDobj.setFromDate(feeDobj.getFromDate());
            collectedFeeDobj.setUptoDate(feeDobj.getUptoDate());
            collectedFeeDobj.setDueDate(feeDobj.getDueDate());
            collectedFeeDobj.setDueDateString(feeDobj.getDueDateString());
            collectedFeeDobj.setReadOnlyFine(true);
            if (collectedFeeDobj.getFromDate() != null && collectedFeeDobj.getUptoDate() != null) {
                collectedFeeDobj.setRenderFromDate(true);
                collectedFeeDobj.setRenderUptoDate(true);
            }
            // Clear previoulsy Set lists
            if (TableConstants.VM_TRANSACTION_MAST_ADD_HYPO == feeDobj.getPurCd()) {
                if (ownerDobj.isHypothecatedFlag()) {
                    vehParams.setTRANSACTION_PUR_CD(feeDobj.getPurCd());
                    if (configFeeFineZero != null) {
                        if (isCondition(replaceTagValues(configFeeFineZero.getFeeAmtZero(), vehParams), "updateForNewRegAppl")) {
                            collectedFeeDobj.setFeeAmount(0L);
                        }
                        if (isCondition(replaceTagValues(configFeeFineZero.getFineAmtZero(), vehParams), "updateForNewRegAppl")) {
                            collectedFeeDobj.setFineAmount(0L);
                        }
                    }
                    feePanelBeanModel.getPayableFeeCollectionList().add(collectedFeeDobj);
                    feePanelBeanModel.getFeeCollectionList().add(collectedFeeDobj);
                    feeCollectionLists.add(collectedFeeDobj);
                }
//                

            } else {
                if (configFeeFineZero != null) {
                    vehParams.setTRANSACTION_PUR_CD(feeDobj.getPurCd());
                    if (isCondition(replaceTagValues(configFeeFineZero.getFeeAmtZero(), vehParams), "updateForNewRegAppl")) {
                        collectedFeeDobj.setFeeAmount(0L);
                    }
                    if (isCondition(replaceTagValues(configFeeFineZero.getFineAmtZero(), vehParams), "updateForNewRegAppl")) {
                        collectedFeeDobj.setFineAmount(0L);
                    }
                }

                feePanelBeanModel.getPayableFeeCollectionList().add(collectedFeeDobj);
                feePanelBeanModel.getFeeCollectionList().add(collectedFeeDobj);
                feeCollectionLists.add(collectedFeeDobj);
            }

            if ((ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                    || ownerDobj.getRegn_type().equals(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) && TableConstants.VM_TRANSACTION_MAST_CHG_ADD == feeDobj.getPurCd()) {
                collectedFeeDobj.setDisableDropDown(false);
            }

            if ("OR".equalsIgnoreCase(sessionVariables.getStateCodeSelected()) && TableConstants.POSTAL_PUR_CD == feeDobj.getPurCd()) {
                int fee = ServerUtility.getPincodeFee(sessionVariables.getStateCodeSelected(), ownerDobj.getC_pincode());
                collectedFeeDobj.setFeeAmount((long) fee);
            }
        }

        newVehicleFeeBeanModel.setFinePenaltyDetails(applNo, stateCode);

        // NEED TO TEST THIS
        feePanelBeanModel.calculateTotal(tmConfigDobj, sessionVariables);

        VahanTaxParameters taxParameters = FormulaUtilities.fillTaxParametersFromDobj(ownerDobj, permitPanelBean.getPermitDobj(), sessionVariables.getOffCodeSelected(), sessionVariables.getUserLoginOffCode());
        wrapperModel.setVahanTaxParameters(taxParameters);
        taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
        if (ownerDobj.getRegn_dt() == null) {
            taxParameters.setREGN_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));
        }

        taxParameters.setTAX_DUE_FROM_DATE(DateUtils.parseDate(ownerDobj.getPurchase_dt()));

        // by komal
        PassengerPermitDetailDobj permitDobj = new PermitImplementation().getPermitDetails(applNo.toUpperCase(), null, purCd);
        if (null != permitDobj) {
            String permitType = permitDobj.getPmt_type_code();
            String domainCode = permitDobj.getDomain_CODE();
            String serviceType = permitDobj.getServices_TYPE();
            String permitCatg = permitDobj.getPmtCatg();
            String period = permitDobj.getPeriod();

            permitPanelBean.getPermitDobj().setPmt_type_code(permitType);
            permitPanelBean.getPermitDobj().setDomain_CODE(domainCode);
            permitPanelBean.getPermitDobj().setServices_TYPE(serviceType);
            permitPanelBean.getPermitDobj().setPmtCatg(permitCatg);
            permitPanelBean.getPermitDobj().setPeriod(period);

            //setting permit detials in VahanTaxParameters to calculate tax
            if (!Utility.isNullOrBlank(domainCode) && !"-1".equalsIgnoreCase(domainCode)) {
                taxParameters.setDOMAIN_CD(Integer.parseInt(domainCode));
            }
            if (!Utility.isNullOrBlank(permitType) && !"-1".equalsIgnoreCase(permitType)) {
                taxParameters.setPERMIT_TYPE(Integer.parseInt(permitType));
            }
            if (!Utility.isNullOrBlank(serviceType) && !"-1".equalsIgnoreCase(serviceType)) {
                taxParameters.setSERVICE_TYPE(Integer.parseInt(serviceType));
            }
            if (!Utility.isNullOrBlank(permitCatg) && !"-1".equalsIgnoreCase(permitCatg)) {
                taxParameters.setPERMIT_SUB_CATG(Integer.parseInt(permitCatg));
            }
        }

//        fillTaxBeanList(taxParameters, FEE_ACTION);
//        updateTotalPayableAmount();
        if (ownerDobj.getRqrd_tax_modes() != null && !ownerDobj.getRqrd_tax_modes().isEmpty() && newVehicleFeeBeanModel.isRenderTaxPanel()) {
            Map<Integer, String> rqrdTaxModes = NewImplementation.getRqrdTaxModes(ownerDobj.getRqrd_tax_modes());
            if (rqrdTaxModes != null && !rqrdTaxModes.isEmpty()) {
                for (Map.Entry<Integer, String> entry : rqrdTaxModes.entrySet()) {
                    if (entry.getKey() == 58) {
                        newVehicleFeeBeanModel.setTaxInstallMode(entry.getValue());
                        break;
                    }
                }
            }

            VehicleParameters vehParameters = FormulaUtilities.fillVehicleParametersFromDobj(ownerDobj, sessionVariables);
            vehParameters.setTAX_MODE(newVehicleFeeBeanModel.getTaxInstallMode());
            if (isCondition(replaceTagValues(tmConfigDobj.getTax_installment(), vehParameters), "updateForNewRegAppl")) {
                newVehicleFeeBeanModel.setRenderTaxInstallment(true);
            }

            // Specific scenario for Physically Handicapped Owners registering in Orissa
            if (sessionVariables.getStateCodeSelected().equals("OR") && ownerDobj.getOwner_identity() != null) {
                if (ownerDobj.getOwner_identity().getOwnerCatg() == TableConstants.OWNER_CATG_PHYSICALLY_HANDICAPPED
                        && ownerDobj.getVh_class() == Integer.parseInt(TableConstants.INVALID_CARRIAGE)) {
                    newVehicleFeeBeanModel.setRenderTaxExemption(true);
                } else {
                    newVehicleFeeBeanModel.setRenderTaxExemption(false);
                }
            }
        }

        return wrapperModel;
    }

    @PostMapping("/fee/fillTaxBeanList")
    public WrapperModel fillTaxBeanList(@RequestBody WrapperModel wrapperModel, @RequestParam String feeAction) throws VahanException {
        SessionVariablesModel sessionVariablesModel = wrapperModel.getSessionVariables();
        int actionCode = sessionVariablesModel.getActionCodeSelected();
        int offCode = sessionVariablesModel.getOffCodeSelected();

        VahanTaxParameters taxParameters = wrapperModel.getVahanTaxParameters();

        TmConfigurationDobj tmConfigDobj = wrapperModel.getTmConfigDobj();
        NewVehicleFeeBeanModel newVehicleFeeBeanModel = wrapperModel.getNewVehicleFeeBeanModel();
        wrapperModel.setNewVehicleFeeBeanModel(newVehicleFeeBeanModel);
        Owner_dobj ownerDobj = newVehicleFeeBeanModel.getOwnerDobj();
        newVehicleFeeBeanModel.setOwnerDobj(ownerDobj);

        //For Fitness Fee No Tax Fee is collected
        if ((actionCode == TableConstants.TM_ROLE_NEW_REGISTRATION_FIT_FEE) || (actionCode == TableConstants.TM_ROLE_DEALER_NEW_REGISTRATION_FIT_FEE)) {
            return null;
        }
        String[][] dataTaxModes = MasterTableFiller.masterTables.VM_TAX_MODE.getData();
        TaxFormPanelBean bean = null;
        Map<Integer, String> rqrdTaxModes = null;
        VehicleParameters parameters = FormulaUtilities.fillVehicleParametersFromDobj(ownerDobj, sessionVariablesModel);
        parameters.setLOGIN_OFF_CD(offCode);
        if (ownerDobj.getOtherStateVehDobj() != null && ownerDobj.getOtherStateVehDobj().getOldOffCD() != null) {
            parameters.setPREV_OFF_CD(ownerDobj.getOtherStateVehDobj().getOldOffCD());
        }
        if (isCondition(replaceTagValues(tmConfigDobj.getTax_exemption(), parameters), "fillTaxBeanList")) {
            newVehicleFeeBeanModel.setRenderTaxExemption(true);
        }
        if (ownerDobj.getRqrd_tax_modes() != null && !ownerDobj.getRqrd_tax_modes().isEmpty()) {
            newVehicleFeeBeanModel.setRenderTaxPanel(true);
            if (!newVehicleFeeBeanModel.isAutoRunTaxListener()) {
//                isViewIsRendered();
                return wrapperModel;
            }
        } else {
            List<EpayDobj> listTaxTypes = EpayImplementation.getFeeDetailsByActionTax(ownerDobj, feeAction, sessionVariablesModel);
            if (listTaxTypes.size() > 0) {
                for (EpayDobj ePay : listTaxTypes) {
                    if (ePay.isTaxExempted()) {
                        newVehicleFeeBeanModel.setRenderTaxExemption(true);
                    }
                }
                newVehicleFeeBeanModel.setRenderTaxPanel(true);
            } else {
                newVehicleFeeBeanModel.setRenderTaxPanel(false);
            }

            for (EpayDobj dobj : listTaxTypes) {
                taxParameters.setPUR_CD(dobj.getPurCd());
                taxParameters.setNEW_VCH("Y");
                VehicleParameters vehParameters = FormulaUtilities.fillVehicleParametersFromDobj(ownerDobj, sessionVariablesModel);
                vehParameters.setNEW_VCH("Y");
                //AvailableTax modes:
                List listTaxModes = new ArrayList();
                listTaxModes.add(new SelectItem("0", "--Select--"));
                String[] taxModes = TaxServerImplementation.getAvailalableTaxModes(ownerDobj, dobj.getPurCd(), vehParameters, sessionVariablesModel);

                if (taxModes != null) {
                    for (int i = 0; i < taxModes.length; i++) {
                        for (int ii = 0; ii < dataTaxModes.length; ii++) {
                            if (dataTaxModes[ii][0].trim().equals(taxModes[i].trim())) {
                                listTaxModes.add(new SelectItem(dataTaxModes[ii][0], dataTaxModes[ii][1]));
                                break;
                            }
                        }
                    }

                    if (newVehicleFeeBeanModel.isTempRegistration()) {
                        //If Tempor
                        listTaxModes.add(new SelectItem("M", "Monthly"));
                        bean = new TaxFormPanelBean();
                        bean.setPur_cd(dobj.getPurCd());
                        bean.setTaxPurcdDesc(dobj.getPurCdDescr());
                        bean.updateTaxBean();
                        bean.setListTaxModes(listTaxModes);
                        newVehicleFeeBeanModel.getListTaxForm().add(bean);
                        //continue;
                    } else {
                        bean = new TaxFormPanelBean();
                        bean.setPur_cd(dobj.getPurCd());
                        bean.setTaxPurcdDesc(dobj.getPurCdDescr());
                        bean.updateTaxBean();
                        bean.setListTaxModes(listTaxModes);
                        newVehicleFeeBeanModel.getListTaxForm().add(bean);
                    }
                }
            }
        }
        newVehicleFeeBeanModel.setListTaxFormBackup(new ArrayList<>(newVehicleFeeBeanModel.getListTaxForm()));
        //tax exemption for other District/RTO within same state
        if ((taxParameters != null
                && taxParameters.getREGN_TYPE().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))
                && !isCondition(replaceTagValues(Util.getTmConfiguration().getOther_rto_number_change(), parameters), "fillTaxBeanList")) {
            newVehicleFeeBeanModel.setRenderTaxPanel(false);
            newVehicleFeeBeanModel.setRenderTaxExemption(false);
        }

        return wrapperModel;
    }

    @PostMapping("/fee/refreshPermit1")
    public String refreshPermit1(@RequestBody WrapperModel wrapperModel) throws VahanException {
        SessionVariablesModel sessionVariablesModel = wrapperModel.getSessionVariables();

        NewVehicleFeeBeanModel newVehicleFeeBeanModel = wrapperModel.getNewVehicleFeeBeanModel();
        wrapperModel.setNewVehicleFeeBeanModel(newVehicleFeeBeanModel);
        Owner_dobj ownerDobj = newVehicleFeeBeanModel.getOwnerDobj();

        VehicleParameters parameters = FormulaUtilities.fillVehicleParametersFromDobj(ownerDobj, newVehicleFeeBeanModel.getPermitPanelBeanModel().getPermitDobj(), sessionVariablesModel.getOffCodeSelected(), sessionVariablesModel.getUserLoginOffCode());
        String seriesAvailMessage = ServerUtility.getAvailablePrefixSeries(parameters, sessionVariablesModel.getStateCodeSelected(), sessionVariablesModel.getOffCodeSelected());
        return seriesAvailMessage;
    }

    @PostMapping("/fee/refreshPermit2")
    public Long refreshPermit2(@RequestBody WrapperModel wrapperModel) throws VahanException {
        SessionVariablesModel sessionVariablesModel = wrapperModel.getSessionVariables();

        NewVehicleFeeBeanModel newVehicleFeeBeanModel = wrapperModel.getNewVehicleFeeBeanModel();
        wrapperModel.setNewVehicleFeeBeanModel(newVehicleFeeBeanModel);
        Owner_dobj ownerDobj = newVehicleFeeBeanModel.getOwnerDobj();

        if ((sessionVariablesModel.getActionCodeSelected() != TableConstants.TM_ROLE_NEW_REGISTRATION_FIT_FEE || sessionVariablesModel.getActionCodeSelected() != TableConstants.TM_ROLE_DEALER_NEW_REGISTRATION_FIT_FEE)
                && new FeeImplementation().verifySmartCard(sessionVariablesModel.getStateCodeSelected(), sessionVariablesModel.getOffCodeSelected())) {
            return EpayImpl.getPurposeCodeFee(ownerDobj, ownerDobj.getVh_class(), 80, ownerDobj.getVch_catg());
        } else {
            throw new VahanException("Doesn't meet the required condition verifySmartCard");
        }
    }

    /**
     * @author bhuvan
     */
    @PostMapping("/validate/fileupload")
    public WrapperModel validateApplAndFileUpload(@RequestBody WrapperModel wrapperModel, @RequestParam boolean tempFeeInNewRegis) throws VahanException, Exception {

        int fileCount = 0;
        String documentStauts = "";
        OwnerDetailsDobj ownerDobj = null;
        String dmsFileUploadUrl = null;
        OwnerDetailsDobj ownerDetailsDobj = null;
        SessionVariablesModel sessionVariables = wrapperModel.getSessionVariables();
        int purCd = sessionVariables.getSelectedWork().getPur_cd();
        int actionCd = sessionVariables.getActionCodeSelected();
        String stateCd = sessionVariables.getStateCodeSelected();
        String applNo = sessionVariables.getSelectedWork().getAppl_no();
        String userCatg = sessionVariables.getUserCatgForLoggedInUser();

        long userCode = Long.parseLong(sessionVariables.getEmpCodeLoggedIn());

        int offCd = sessionVariables.getOffCodeSelected();
        DocumentUploadBeanModel documentuploadbeanmodel = null;
        // try {

        documentuploadbeanmodel = wrapperModel.getDocumentUploadBeanModel();
        String dmsURL = documentuploadbeanmodel.getDmsURL();
        String regnNo = documentuploadbeanmodel.getRegnNo();
        TmConfigurationDMS confgDMS = documentuploadbeanmodel.getConfgDMS();
        ownerDetailsDobj = documentuploadbeanmodel.getOwnerDetailsDobj();
        List<VTDocumentModel> docDescrList = documentuploadbeanmodel.getDocDescrList();

        if (!CommonUtils.isNullOrBlank(regnNo) && !regnNo.equals("NEW") && TableConstants.VM_TRANSACTION_MAST_AUCTION == purCd) {
            List<OwnerDetailsDobj> ownerDetailsDobjList = new OwnerImplementation().getOwnerDetailsList(regnNo.trim(), null);
            if (ownerDetailsDobjList == null || ownerDetailsDobjList.isEmpty()) {
                throw new VahanException("Invalid Registration Number or Registration No not found in the Record");
            }
            if (ownerDetailsDobjList.size() == 1) {
                ownerDobj = ownerDetailsDobjList.get(0);
            } else if (ownerDetailsDobjList.size() >= 2) {
                throw new VahanException("Duplicate Record Found!!!");
            }
        } else {
            ownerDetailsDobj = new DocumentUploadImplementation().getVaOwnerDetailsForDocumentUpload(applNo.toUpperCase(), stateCd, null);
        }

        ArrayList<Status_dobj> applStatus = ServerUtility.applicationStatusByApplNo(applNo, stateCd);
        if (applStatus != null && !applStatus.isEmpty() && (ownerDetailsDobj != null || ownerDobj != null)) {
            if (applStatus.size() == 2) {
                purCd = TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE;
            } else {
                purCd = applStatus.get(0).getPur_cd();
            }
            if (actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_MODIFY) {
                docDescrList = DmsDocCheckUtils.getUploadedDocumentList(applNo);
                if (confgDMS != null && confgDMS.isDocsFolwRequired() && (docDescrList == null || docDescrList.isEmpty())) {
                    throw new VahanException("Documents can be uploaded after payment and will come in pending work on Home Page.");
                }
            }
            String dealerCd = ServerUtility.getDealerCode(userCode, stateCd, offCd);
            if (TableConstants.USER_CATG_DEALER.equals(userCatg)) {
                if (dealerCd != null && !dealerCd.equals(ownerDetailsDobj.getDealer_cd()) || (TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE != purCd && TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE != purCd)) {
                    throw new VahanException("You are Not Authorised to upload the documents against this application generated by other dealer.");
                }
            }
            switch (userCatg) {
                case TableConstants.USER_CATG_DEALER:
                    if (docDescrList != null && !docDescrList.isEmpty() && docDescrList.size() > 0) {
                        if (confgDMS.getPurCd().contains("," + purCd + ",") || (confgDMS.getPurCd().contains("," + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE + ",") && tempFeeInNewRegis)) {
                            if (confgDMS.getTempApproveActionCd().contains("," + actionCd + ",")) {
                                fileCount = docDescrList.size();
                                documentStauts = TableConstants.DOCUMENT_TEMP_APPROVAL;
                            } else if (confgDMS.getApproveActionCd().contains("," + actionCd + ",")) {
                                fileCount = docDescrList.size();
                                documentStauts = TableConstants.DOCUMENT_RTO_APPROVAL;
                            }
                            if (CommonUtils.isNullOrBlank(dmsURL) && CommonUtils.isNullOrBlank(documentStauts)) {
                                if (actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_MODIFY) {
                                    fileCount = docDescrList.size();
                                    documentStauts = TableConstants.REJECTED_DOCUMENT_MODIFY_STATUS;
                                } else {
                                    fileCount = docDescrList.size();
                                    documentStauts = TableConstants.DOCUMENT_MODIFY_STATUS;
                                }
                            }
                        }
                    }
                    break;
                case TableConstants.USER_CATG_OFF_STAFF:
                    if (confgDMS.getPurCd().contains("," + purCd + ",") || (confgDMS.getPurCd().contains("," + TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE + ",") && Util.getTmConfiguration().isTempFeeInNewRegis())) {
                        if (docDescrList != null && !docDescrList.isEmpty() && docDescrList.size() > 0) {
                            if (confgDMS.getVerfyActionCd().contains("," + actionCd + ",")) {
                                fileCount = docDescrList.size();
                                documentStauts = TableConstants.DOCUMENT_RTO_VERIFICATION;
                            } else if (confgDMS.getApproveActionCd().contains("," + actionCd + ",")) {
                                fileCount = docDescrList.size();
                                documentStauts = TableConstants.DOCUMENT_RTO_APPROVAL;
                            } else if (confgDMS.getTempApproveActionCd().contains("," + actionCd + ",")) {
                                fileCount = docDescrList.size();
                                documentStauts = TableConstants.DOCUMENT_TEMP_APPROVAL;
                            }
                            if (CommonUtils.isNullOrBlank(dmsURL) && CommonUtils.isNullOrBlank(documentStauts)) {
                                fileCount = docDescrList.size();
                                documentStauts = TableConstants.DOCUMENT_MODIFY_STATUS;
                            }
                            break;
                        }
                    }
                    break;
            }

            documentuploadbeanmodel.setPurCd(purCd);
            documentuploadbeanmodel.setUserCatg(userCatg);
            documentuploadbeanmodel.setDocStatus(documentStauts);
            documentuploadbeanmodel.setFileCount(fileCount);
            documentuploadbeanmodel.setOwnerDetailsDobj(ownerDetailsDobj);

            wrapperModel.setDocumentUploadBeanModel(documentuploadbeanmodel);
        } else {
            throw new VahanException("Invalid application no. or application no. already approved");
        }

        return wrapperModel;
    }

    /**
     * @author bhuvan
     */
    @PostMapping("/dialog/close")
    public WrapperModel dialogCloseEventListener(@RequestBody WrapperModel wrapperModel) throws VahanException, Exception {
        TmConfigurationDMS confgDMS = null;
        DocumentUploadBeanModel documentuploadbeanmodel = wrapperModel.getDocumentUploadBeanModel();
        List<VTDocumentModel> docDescrList = documentuploadbeanmodel.getDocDescrList();
        SessionVariablesModel sessionVariables = wrapperModel.getSessionVariables();
        int purCd = sessionVariables.getSelectedWork().getPur_cd();
        int actionCd = sessionVariables.getActionCodeSelected();
        String stateCd = sessionVariables.getStateCodeSelected();
        String applNo = sessionVariables.getSelectedWork().getAppl_no();
        String userCatg = sessionVariables.getUserCatgForLoggedInUser();
        String empCode = sessionVariables.getEmpCodeLoggedIn();
        String remarks = documentuploadbeanmodel.getRemarks();
        String emailMsg = documentuploadbeanmodel.getEmailMessage();
        String userstatecode = sessionVariables.getStateCodeSelected();
        int selectedoffcode = sessionVariables.getOffCodeSelected();
        String successUplodedMsg;
        confgDMS = documentuploadbeanmodel.getConfgDMS();
        if (docDescrList != null && !docDescrList.isEmpty() && docDescrList.size() > 0) {
            switch (userCatg) {
                case TableConstants.USER_CATG_DEALER:
                    if (confgDMS.getTempApproveActionCd().contains("," + actionCd + ",")) {
                        documentuploadbeanmodel.setSuccessUplodedMsg(TableConstants.DOCUMENTS_APPROVE_SUCCESS + applNo + ".");
                        for (VTDocumentModel docsList : docDescrList) {
                            if (!docsList.isTemp_doc_approved()) {
                                throw new VahanException(TableConstants.DOCUMENTS_NOT_APPROVE_MESSG + applNo + "");
                            }
                        }
                    } else {
                        if (actionCd == TableConstants.TM_ROLE_DEALER_DOCUMENT_UPLOAD && confgDMS.isDocsFolwRequired()) {
//                            boolean uploadFlag = DocumentUploadImplementation.fileFlowAfterDocumentUpload(applNo, actionCd, purCd, remarks, empCode, userstatecode, selectedoffcode);
                            boolean uploadFlag = false;
                            if (uploadFlag) {
                                documentuploadbeanmodel.setSuccessUplodedMsg(TableConstants.DOCUMENTS_UPLOAD_SUCCESS + applNo);
                            } else {
                                documentuploadbeanmodel.setSuccessUplodedMsg(TableConstants.DOCUMENTS_NOT_UPLOAD_ERR_MESSG + applNo + "");
                            }
                        } else {
                            documentuploadbeanmodel.setSuccessUplodedMsg(TableConstants.DOCUMENTS_UPLOAD_SUCCESS + applNo);
                        }
                    }
                    break;
                case TableConstants.USER_CATG_OFF_STAFF:
                    if (confgDMS.getUploadActionCd().contains("," + actionCd + ",")) {
                        documentuploadbeanmodel.setSuccessUplodedMsg(TableConstants.DOCUMENTS_UPLOAD_SUCCESS + applNo);
                    } else if (docDescrList == null || docDescrList.isEmpty()) {
                        documentuploadbeanmodel.setSuccessUplodedMsg(TableConstants.DOCUMENTS_NOT_UPLOAD_ERR_MESSG + applNo + "");
                    } else if (actionCd == TableConstants.TM_ROLE_UPLOAD_MODIFY_DOCUMENT && confgDMS.isDocsUploadAtOffice()) {
                        boolean uploadFlag = DocumentUploadImpl.fileFlowAfterDocumentUpload(applNo, actionCd, purCd, remarks);
                        if (uploadFlag) {
                            documentuploadbeanmodel.setSuccessUplodedMsg(TableConstants.DOCUMENTS_UPLOAD_SUCCESS + applNo);
                        } else {
                            documentuploadbeanmodel.setSuccessUplodedMsg(TableConstants.DOCUMENTS_NOT_UPLOAD_ERR_MESSG + applNo + "");
                        }
                    } else if (confgDMS.getVerfyActionCd().contains("," + actionCd + ",")) {
                        documentuploadbeanmodel.setSuccessUplodedMsg(TableConstants.DOCUMENTS_VERIFY_SUCCESS + applNo + ".");

                        for (VTDocumentModel docsList : docDescrList) {
                            if (!docsList.isDoc_verified()) {
                                successUplodedMsg = TableConstants.DOCUMENTS_NOT_VERIFY_MESSG + applNo;
                                documentuploadbeanmodel.setEmailMessage(docsList.getDoc_desc() + "," + emailMsg);
                                //renderedMailButton = true;
                                documentuploadbeanmodel.setRenderedMailButton(true);
                                successUplodedMsg = successUplodedMsg + "<br/><br/>" + emailMsg;
                            }
                        }
                    } else if (confgDMS.getApproveActionCd().contains("," + actionCd + ",")) {
                        documentuploadbeanmodel.setSuccessUplodedMsg(TableConstants.DOCUMENTS_APPROVE_SUCCESS + applNo + ".");
                        for (VTDocumentModel docsList : docDescrList) {
                            if (!docsList.isDoc_approved()) {
                                successUplodedMsg = TableConstants.DOCUMENTS_NOT_APPROVE_MESSG + applNo;
                                documentuploadbeanmodel.setEmailMessage(docsList.getDoc_desc() + "," + emailMsg);
                                documentuploadbeanmodel.setRenderedMailButton(true);
                                successUplodedMsg = successUplodedMsg + "<br/><br/>" + documentuploadbeanmodel.getEmailMessage();
                            }
                        }
                    } else if (confgDMS.getTempApproveActionCd().contains("," + actionCd + ",")) {
                        documentuploadbeanmodel.setSuccessUplodedMsg(TableConstants.DOCUMENTS_APPROVE_SUCCESS + applNo + "");
                        for (VTDocumentModel docsList : docDescrList) {
                            if (!docsList.isTemp_doc_approved()) {
                                successUplodedMsg = TableConstants.DOCUMENTS_NOT_APPROVE_MESSG + applNo;
                                documentuploadbeanmodel.setEmailMessage(docsList.getDoc_desc() + "," + documentuploadbeanmodel.getEmailMessage());

                                documentuploadbeanmodel.setRenderedMailButton(true);
                                successUplodedMsg = successUplodedMsg + "<br/><br/>" + documentuploadbeanmodel.getEmailMessage();
                            }
                        }
                    } else {
                        documentuploadbeanmodel.setSuccessUplodedMsg(TableConstants.DOCUMENTS_UPLOAD_SUCCESS + applNo + "");
                    }
                    break;
            }

        } else {
            documentuploadbeanmodel.setSuccessUplodedMsg(TableConstants.DOCUMENTS_NOT_UPLOAD_ERR_MESSG + applNo + "");
        }
        wrapperModel.setDocumentUploadBeanModel(documentuploadbeanmodel);
        return wrapperModel;
    }

    /**
     * @author bhuvan
     */
    @PostMapping("/upload/pendingdocs")
    public WrapperModel uploadPendingDocuments(@RequestBody WrapperModel wrapperModel) throws VahanException {
        SessionVariablesModel sessionVariables = wrapperModel.getSessionVariables();
        String applNo = sessionVariables.getSelectedWork().getAppl_no();
        String stateCd = sessionVariables.getStateCodeSelected();
        DocumentUploadBeanModel documentuploadbeanmodel = null;
        documentuploadbeanmodel = wrapperModel.getDocumentUploadBeanModel();
        ArrayList<Status_dobj> applStatus = documentuploadbeanmodel.getApplStatus();
        String dmsFileUploadUrl;
        int offCd = sessionVariables.getOffCodeSelected();
        if (applStatus != null && !applStatus.isEmpty()) {
            List<VTDocumentModel> docListForPendingUpload = DmsDocCheckUtils.getUploadedDocumentList(applNo);
            if (docListForPendingUpload == null || docListForPendingUpload.isEmpty()) {
                throw new VahanException("Please use Upload/Modify button to upload the documents.");
            }
            dmsFileUploadUrl = ServerUtility.getVahanPgiUrl(TableConstants.DMS_SARTHI_URL);
            if (dmsFileUploadUrl.isEmpty()) {
                throw new VahanException("Problem in view documents.");
            }
            dmsFileUploadUrl = dmsFileUploadUrl.replace("ApplNo", applNo);
            dmsFileUploadUrl = dmsFileUploadUrl.replace("state_cd", stateCd);
            dmsFileUploadUrl = dmsFileUploadUrl.replace("regn_no", applStatus.get(0).getRegn_no());
            dmsFileUploadUrl = dmsFileUploadUrl.replace("off_name", ServerUtility.getOfficeName(offCd, stateCd));
            dmsFileUploadUrl = dmsFileUploadUrl.replace("pur_cd", applStatus.get(0).getPur_cd() + "");
            dmsFileUploadUrl = dmsFileUploadUrl + TableConstants.SECURITY_KEY;

            documentuploadbeanmodel.setDmsFileUploadUrl(dmsFileUploadUrl);
            wrapperModel.setDocumentUploadBeanModel(documentuploadbeanmodel);
        }
        return wrapperModel;
    }
}
