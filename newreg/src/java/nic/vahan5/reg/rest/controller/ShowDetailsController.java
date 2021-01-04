/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.controller;

import nic.rto.vahan.common.VahanException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import nic.java.util.DateUtils;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.State;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.AuctionDobj;
import nic.vahan.form.dobj.CdDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.OtherStateVehDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.NocDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;
import nic.vahan.form.dobj.ScrappedVehicleDobj;
import nic.vahan.form.dobj.TempRegDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.impl.AuctionImpl;
import nic.vahan.form.impl.BlackListedVehicleImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.NocImpl;
import nic.vahan5.reg.form.impl.NewVehicleFitnessImplentation;
import nic.vahan.form.impl.OtherStateVehImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.RetroFittingDetailsImpl;
import nic.vahan.form.impl.ScrappedVehicleImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import nic.vahan5.reg.form.impl.OwnerImplementation;
import nic.vahan5.reg.rest.model.AxleBeanModel;
import nic.vahan5.reg.rest.model.HpaBeanModel;
import nic.vahan5.reg.rest.model.ImportedVehicleBeanModel;
import nic.vahan5.reg.rest.model.InsBeanModel;
import nic.vahan5.reg.rest.model.OwnerBeanModel;
import nic.vahan5.reg.rest.model.RetroFittingDetailsBeanModel;
import nic.vahan5.reg.rest.model.SessionVariablesModel;
import nic.vahan5.reg.rest.model.WorkBenchModel;
import nic.vahan5.reg.rest.model.WrapperModel;
import nic.vahan5.reg.server.CommonUtil;
import nic.vahan5.reg.server.ServerUtility;
import nic.vahan5.util.VahanUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 *
 * @author Sai
 */
@RestController
@RequestMapping("/newregn")
public class ShowDetailsController {

    private static final Logger LOGGER = Logger.getLogger(ShowDetailsController.class);

    @Autowired
    WebClient.Builder webClientBuilder;
    @Autowired
    VahanUtil vahanUtil;

    @PostMapping(value = "/showDetails")
    public WrapperModel showDetails(@RequestBody WrapperModel wrapperModel, @RequestParam String serviceTicket) throws VahanException, Exception {
        boolean disableSaleAmount = false;
        boolean isDealer = false;
        boolean isDetailsFromHomologation = false;
        boolean isNewVehicleFitness = false;
        boolean isExShoowroomPriceDisable;
        boolean isValidChassiNo;
        ScrappedVehicleDobj scrapDobj;
        String BlackListedDetails;
        Date vahan4StartDate = null;
        AuctionDobj auctionDobj = null;

        OwnerBeanModel owner_bean = wrapperModel.getOwnerBean();
        wrapperModel.setOwnerBean(owner_bean);

        OwnerImplementation owner_Impl = new OwnerImplementation();
        Owner_dobj owner_dobj = owner_bean.getOwnerDobj();

        Owner_dobj owBacklog = null;
        Owner_dobj owneTmpDobj = null;
        Owner_dobj ownerScrap = null;
        Owner_dobj ownerHomo = null;
        Owner_dobj ownerNewVehicleFitness;
        NocDobj nocDobj = null;

        String user_catg;
        Long user_cd;
        String chasiNo;
        Boolean regnTypeOSorORTO;
        String engineNo;
        String regnType;
        String PUR_CD;
        String APPL_NO;
        String userStateCode;
        String regnNo;
        List officeList;
        List<OwnerDetailsDobj> tempRegnDetailsList;

        TmConfigurationDobj tmConfDobj;
        SessionVariablesModel sessionVariables;

        WorkBenchModel workBenchModel = wrapperModel.getWorkBench();
        tmConfDobj = wrapperModel.getTmConfigDobj();
        sessionVariables = wrapperModel.getSessionVariables();
        int selectedOffCode = sessionVariables.getOffCodeSelected();
        officeList = workBenchModel.getOfficeList();

        user_catg = sessionVariables.getUserCatgForLoggedInUser();
        chasiNo = workBenchModel.getChasiNo();
        regnTypeOSorORTO = workBenchModel.isRegnTypeOSorORTO();
        engineNo = workBenchModel.getEngineNo();
        regnType = workBenchModel.getRegnType();
        PUR_CD = workBenchModel.getPUR_CD();
        APPL_NO = workBenchModel.getAPPL_NO();
        userStateCode = sessionVariables.getStateCodeSelected();
        regnNo = workBenchModel.getRegnNo();

        user_cd = Long.parseLong(sessionVariables.getEmpCodeLoggedIn());
        workBenchModel.setUser_cd(user_cd);

        BlackListedVehicleImpl blkImpl = new BlackListedVehicleImpl();
        if (regnNo != null && !regnNo.trim().isEmpty()) {
            regnNo = regnNo.trim().replaceAll(" ", "");//for replacing space if any exist
            if (!regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_SCRAPPED) && !regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_TEMPORARY)) {
                if (!(regnNo.matches("([A-Z]{2}[A-Z0-9]{4,10})"))) {
                    throw new VahanException("First two character's of Registration No should be Alphabet");
                }
            }
            if (!JSFUtils.isNumeric(regnNo.substring(regnNo.length() - 4, regnNo.length()))) {
                throw new VahanException("Invalid Registration No ( Last 4 Character of Registration No Should be Numeric)");
            }
            if (Integer.parseInt(regnNo.substring(regnNo.length() - 4, regnNo.length())) <= 0) {
                throw new VahanException("Invalid Registration No ( Last 4 Character of Registration No Can't be All ZERO )");
            }
            nocDobj = new NocImpl().set_NOC_appl_db_to_dobj(null, regnNo.trim().toUpperCase());
        }

        if (nocDobj != null) {
            vahan4StartDate = ServerUtil.getVahan4StartDate(nocDobj.getState_cd(), nocDobj.getOff_cd());
        }

        if (regnType != null && regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
            if (vahan4StartDate != null) {
                owner_bean.disableForHomologationData(true);
                if (owner_dobj == null) {
                    owner_bean.setDisableChasiNo(false);
                }
            }
        }
        if (chasiNo != null) {
            if (chasiNo.trim().length() > 5) {
                auctionDobj = new AuctionImpl().getDetailsFromVtAuction(chasiNo, null);
            } else {
                auctionDobj = new AuctionImpl().getDetailsFromVtAuction(null, regnNo);
            }

            if (regnType != null && !regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_CONFISCATED_AUCTION) && auctionDobj != null) {
                throw new VahanException("Vehicle has been auctioned.Please select NEW REGN AGAINST AUCTION/CONFISCATED VEH Registration Type.");
            }

            if (regnType != null && regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_CONFISCATED_AUCTION) && auctionDobj == null) {
                throw new VahanException("Vehicle has not been auctioned.Either select other Registration Type/do auction.");
            }

            if (!regnTypeOSorORTO) {
                if (auctionDobj == null) {
                    owneTmpDobj = owner_Impl.getValidateVtOwnerTempDobj(chasiNo, userStateCode, selectedOffCode, tmConfDobj.isCnginfo_from_cngmaker());
                }
            }
            if (owneTmpDobj != null && owneTmpDobj.getDob_temp() != null && !"OM".equalsIgnoreCase(owneTmpDobj.getDob_temp().getPurpose())) {
                owner_bean.setBlnDisableRegnTypeTemp(true);
            }
            /*
             * Check if wrong regn_type is selected for temporary registration
             */
            if (owneTmpDobj != null && !regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_TEMPORARY)) {
                if (owneTmpDobj.getDob_temp() != null
                        && owneTmpDobj.getDob_temp().getPurpose() != null
                        && owneTmpDobj.getDob_temp().getPurpose().equalsIgnoreCase(TableConstants.STOCK_TRANSFER)
                        && regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_NEW)) {
                    //skip for the case of stock transfer
                } else {
                    throw new VahanException("Temporary Registration No. [" + owneTmpDobj.getTempReg().getTmp_regn_no() + "] issued from [" + ServerUtil.getOfficeName(owneTmpDobj.getTempReg().getTmp_off_cd(), owneTmpDobj.getTempReg().getTmp_state_cd()) + "] against this Chassis. Please select Registration Type as Temporary Registered Vehicle.");
                }
            } else if (owneTmpDobj != null && regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_TEMPORARY)) {
                //temporary solution for displaying temporary details if temp.regn is done more than one time on same chassi no
                tempRegnDetailsList = owner_Impl.getTempRegistrationDetailsList(chasiNo);
                wrapperModel.setTempRegnDetailsList(tempRegnDetailsList);

                if ((Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_TEMP_REG
                        || Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE)) {

                    if (owneTmpDobj.getDob_temp() != null && owneTmpDobj.getDob_temp().getValidUpto() != null
                            && owneTmpDobj.getDob_temp().getPurpose() != null && !"BB,OM".contains(owneTmpDobj.getDob_temp().getPurpose())) {
                        if (DateUtils.compareDates(owneTmpDobj.getDob_temp().getValidUpto(), ServerUtil.dateRange(new Date(), 0, -6, 0)) == 1) {
                            throw new VahanException("Renewal of temporary registration is not allowed due to validity [" + ServerUtil.parseDateToString(owneTmpDobj.getDob_temp().getValidUpto()) + "] expired 6 months back or more.");
                        }
                    }

                    if (owneTmpDobj.getDob_temp() != null && owneTmpDobj.getDob_temp().getPurpose() != null
                            && !"BB,OM".contains(owneTmpDobj.getDob_temp().getPurpose())) {
                        throw new VahanException("Chassis No is already registered [Appl.No: " + owneTmpDobj.getDob_temp().getAppl_no() + "], [Regn.No:" + owneTmpDobj.getDob_temp().getTemp_regn_no() + "], Please use different Chassis No or Multiple Temporary Registration is allowed for 'Body Building/Stock Transfer' Only.");
                    }
                    if (owneTmpDobj.getDob_temp() != null && owneTmpDobj.getDob_temp().getPurpose() != null
                            && !"OM".contains(owneTmpDobj.getDob_temp().getPurpose()) && owner_bean != null && owner_bean.getTemp_dobj() != null) {
                        owner_bean.getTemp_dobj().setTemp_regn_type("R");//Renewal of Temporary Registration
                    }
                }
            }

            BlackListedDetails = blkImpl.checkChassisNoForBlackList(chasiNo);

            if (chasiNo.trim().length() > 5 && BlackListedDetails != null) {
                throw new VahanException(BlackListedDetails);
            }
        }

        if (!Utility.isNullOrBlank(regnNo)) {
            owBacklog = OwnerImpl.getBacklogDetails(regnNo, null);
        }
        if (owBacklog == null && !Utility.isNullOrBlank(chasiNo)) {
            owBacklog = OwnerImpl.getBacklogDetails(null, chasiNo);

        }
        if (!CommonUtils.isNullOrBlank(chasiNo) && !CommonUtils.isNullOrBlank(engineNo)) {
            boolean homoEngValidate = true;
            if (owneTmpDobj != null && Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && TableConstants.VM_REGN_TYPE_TEMPORARY.equals(regnType)) {
                homoEngValidate = false;
            }

            String restUrl = vahanUtil.getHomologationUrl() + "?chasiNo=" + chasiNo
                    + "&engineNo=" + engineNo + "&stateCodeSelected=" + sessionVariables.getStateCodeSelected()
                    + "&offCodeSelected=" + sessionVariables.getOffCodeSelected() + "&homoEngValidate=" + homoEngValidate
                    + "&serviceTicket=" + serviceTicket;
            try {
                ownerHomo = webClientBuilder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .build()
                        .get()
                        .uri(restUrl)
                        .retrieve()
                        .bodyToMono(Owner_dobj.class)
                        .block();
                if (ownerHomo == null) {
                    return null;
                }
            } catch (WebClientResponseException e) {
                LOGGER.error(e.getMessage());
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }

            OwnerImpl.validatePurchaseAs(ownerHomo, regnType, Integer.parseInt(PUR_CD));
        }
        if (chasiNo != null) {
            owner_dobj = owner_Impl.set_Owner_appl_db_to_dobj("", "", chasiNo.trim().toUpperCase(), TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE, userStateCode, selectedOffCode);
            if (owner_dobj != null && TableConstants.VT_RC_CANCEL_STATUS.equals(owner_dobj.getStatus())) {
                if (TableConstants.VM_REGN_TYPE_NEW.equalsIgnoreCase(regnType)) {
                    owner_dobj = null;
                } else {
                    if (!TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE.equalsIgnoreCase(regnType) && !TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE.equalsIgnoreCase(regnType)) {
                        throw new VahanException("RC is cancel you can register this vehicle Registration type as New. ");
                    }
                }
            }
        }
        if (!regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                && !regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)
                && !regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_SCRAPPED) && ownerHomo != null && !workBenchModel.isIsVehicleNoScrapRetain()) {
            owner_bean.disableForHomologationData(true);
        }
        if (!CommonUtil.isNullOrBlank(chasiNo) && !CommonUtil.isNullOrBlank(engineNo)) {
            NewVehicleFitnessImplentation newVehicleFitnessImpl = new NewVehicleFitnessImplentation();
            isNewVehicleFitness = newVehicleFitnessImpl.checkForPreRegFitness(chasiNo, engineNo, userStateCode);
            if (isNewVehicleFitness && !(Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                    || Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE)) {

                throw new VahanException("Fitness Of Vehicle is already done,Only New Registration is Allowed");
            }
            if (isNewVehicleFitness) {
                ownerNewVehicleFitness = owner_Impl.set_Owner_appl_db_to_dobj("", "", chasiNo, TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE);
                if (!ownerNewVehicleFitness.getState_cd().equalsIgnoreCase(Util.getUserStateCode())) {
                    throw new VahanException("New Vehicle Fitness is Done in State [" + ownerNewVehicleFitness.getState_cd() + " New Registration Is allowed With in The State Only");
                }
                FitnessDobj new_vehicle_fitness_dobj = newVehicleFitnessImpl.set_Fitness_appl_db_to_dobj(ownerNewVehicleFitness.getChasi_no(), null);

                if (new_vehicle_fitness_dobj != null) {
                    if ("N".equalsIgnoreCase(new_vehicle_fitness_dobj.getFit_result())) {
                        throw new VahanException("New Vehicle Fitness is Failed of   Chassis No:  " + chasiNo + ".");
                    }
                    //main_panal_visibililty = true;
                    owner_dobj = ownerNewVehicleFitness;
                    /*
                     * @Added By: Kartikey Singh
                     */
                    if (ownerHomo != null) {
                        owner_bean.getOwnerDobj().setOwnerDetailsDo(new OwnerDetailsDobj());
                        owner_bean.getOwnerDobj().getOwnerDetailsDo().setMaker_name(ownerHomo.getOwnerDetailsDo().getMaker_name());
                        owner_bean.getOwnerDobj().getOwnerDetailsDo().setModel_name(ownerHomo.getOwnerDetailsDo().getModel_name());
                        owner_bean.getOwnerDobj().getOwnerDetailsDo().setFuel_descr(ownerHomo.getOwnerDetailsDo().getFuel_descr());
                    }
                    RetroFittingDetailsBeanModel cng_bean = new RetroFittingDetailsBeanModel();
                    if (owner_dobj.getFuel() > 0) {
                        int fuel_type = owner_dobj.getFuel();
                        if (fuel_type == TableConstants.VM_FUEL_CNG_TYPE
                                || fuel_type == TableConstants.VM_FUEL_TYPE_PETROL_CNG
                                || fuel_type == TableConstants.VM_FUEL_TYPE_LPG
                                || fuel_type == TableConstants.VM_FUEL_TYPE_PETROL_LPG) {
                            Owner_dobj newVehicleFitnessowner_dobj = null;
                            newVehicleFitnessowner_dobj = owner_Impl.set_Owner_appl_db_to_dobj(null, null, owner_dobj.getChasi_no(), Integer.parseInt(PUR_CD));
                            RetroFittingDetailsDobj cng_dobj = RetroFittingDetailsImpl.setCngDetails_db_to_dobj(newVehicleFitnessowner_dobj.getAppl_no());
                            owner_bean.setCngDetails_Visibility_tab(true);
                            if (cng_dobj != null) {
                                cng_bean.setDobj_To_Bean(cng_dobj);
                                cng_bean.setCng_dobj_prv(cng_dobj);
                                wrapperModel.setCngDetailsBean(cng_bean);
                                workBenchModel.setFacesMessage("retroFittingDetailsBean");
                            }
                        }
                    }

                    owner_bean.setVehicleComponentEditable(true);
                    if (ownerNewVehicleFitness != null) {
                        workBenchModel.setPending(newVehicleFitnessImpl.checkPendingChassisFCPrint(ownerNewVehicleFitness.getAppl_no(), ownerNewVehicleFitness.getState_cd()));

                    }
                }
            }

        }
        if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_SCRAPPED) || workBenchModel.isIsVehicleNoScrapRetain()) {
            ScrappedVehicleImpl scrapImpl = new ScrappedVehicleImpl();
            scrapDobj = scrapImpl.getOldScrappedInformation(regnNo, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());

            if (scrapDobj == null) {
                throw new VahanException("Vehicle is not scrapped");
            }

            if (scrapDobj.getRegn_appl_no() != null && !scrapDobj.getRegn_appl_no().isEmpty()) {
                throw new VahanException("Application Number " + scrapDobj.getRegn_appl_no()
                        + " is inwarded for this Registration Number");
            }

            ownerScrap = owner_Impl.set_Owner_appl_db_to_dobj(regnNo, "", "", TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE);

            if (ownerScrap == null || !ownerScrap.getStatus().equalsIgnoreCase("T")) {
                throw new VahanException("Vehicle is not scrapped");
            }
            wrapperModel.setOwnerDobj(ownerScrap);
            InsDobj ins_dobj = InsImpl.set_ins_dtls_db_to_dobj(regnNo, null, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());

            if (scrapDobj.getScrap_reason().trim().equals("3")) {
                ownerScrap.setF_name(ownerScrap.getOwner_name());
                ownerScrap.setOwner_name("");
            } else if (scrapDobj.getScrap_reason().trim().equals("5") && ins_dobj != null && (ins_dobj.getIns_type() == 1 || ins_dobj.getIns_type() == 3)) {
                scrapImpl.fillOwnerDobjInTheftCaseFronVhTo(regnNo, ownerScrap);

            }

            // blnScrappedVehicle = true;
            workBenchModel.setBlnScrappedVehicle(true);
            owner_bean.setDisableSamePerm(true);
        }

        if (user_catg != null && user_catg.trim().equals(TableConstants.User_Dealer)) {

            owner_bean.setIsDealer(true);

            //Fill Details from Backlog 
            if (owner_dobj == null) {
                owner_dobj = owBacklog;
                wrapperModel.setOwnerDobj(owner_dobj);
            }
            if (tmConfDobj != null && owner_bean != null && owner_bean.getOwnerDobj().getRegn_type().equals(TableConstants.VM_REGN_TYPE_NEW) && tmConfDobj.isConsiderFMSDealer()) {
                String dealerCd = ServerUtil.getDealerCode(user_cd, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                String pendingAppls = ServerUtil.validateApplicationInward(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), dealerCd, tmConfDobj.getFmsGraceDays());

                workBenchModel.setPendingAppls(pendingAppls);
                return wrapperModel;

            }
            if (tmConfDobj != null && owner_bean != null && owner_bean.getOwnerDobj() != null && TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE == Integer.parseInt(PUR_CD) && TableConstants.VM_REGN_TYPE_NEW.equals(owner_bean.getOwnerDobj().getRegn_type()) && tmConfDobj.getTmConfigDealerDobj() != null && tmConfDobj.getTmConfigDealerDobj().isCheckHSRPPendency()) {
                String dealerCd = ServerUtil.getDealerCode(user_cd, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                ServerUtil.validateHSRPPendency(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), dealerCd, tmConfDobj.getFmsGraceDays());
            }
            owner_dobj = ownerHomo;
            if (ownerHomo != null) {
                // boolean isExShoowroomPriceDisable = false;
                workBenchModel.setExShoowroomPriceDisable(false);
                if (tmConfDobj != null) {

                    isExShoowroomPriceDisable = tmConfDobj.isEx_showroom_price_homologation();
                    workBenchModel.setExShoowroomPriceDisable(isExShoowroomPriceDisable);
                }
                List<SelectItem> makerList = owner_Impl.getAssignedMakerList(user_cd);
                // boolean found = false;
                workBenchModel.setFound(false);
                for (SelectItem item : makerList) {
                    if (item.getValue().toString().trim().equals(String.valueOf(ownerHomo.getMaker()))) {
                        // found = true;
                        workBenchModel.setFound(true);
                        break;
                    }
                }
                isDealer = true;
                owner_bean.setIsHomologationData(TableConstants.HOMOLOGATION_DATA);
                owner_bean.disableDealerFields(ownerHomo.getManu_mon(), ownerHomo.getManu_yr());
                owner_bean.setHomoLdWt(ownerHomo.getLd_wt());
                owner_bean.setHomoUnLdWt(ownerHomo.getUnld_wt());
                owner_bean.setHomoDobj(ownerHomo);
            }
        }
        if (ownerHomo != null && ownerHomo.getSale_amt() > 0) {
            if (tmConfDobj != null && tmConfDobj.isEx_showroom_price_homologation() && tmConfDobj.getTmConfigDealerDobj() != null
                    && !tmConfDobj.getTmConfigDealerDobj().isValidateHomoSaleAmt()) {
                disableSaleAmount = true;
            }
        }
        OtherStateVehImpl otherStateVehImpl = new OtherStateVehImpl();
        OtherStateVehDobj otherStateVehDobj = new OtherStateVehDobj();

        //check for other state for not repeating for same registration no which are already registered.
        if ((regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                || regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))) {
            String pendingOtherStateAppl = otherStateVehImpl.getApplNoOfOtherState(regnNo);
            if (pendingOtherStateAppl != null && pendingOtherStateAppl.trim().length() > 0) {
                //need to find the solution when registration no perform other state within same state and again perform other state for different state
                throw new VahanException("This Registration No. already exist in Other State/Region Vehicle transaction details and pending application no. for this transaction is [" + pendingOtherStateAppl + "]. Please Check with this Application No.");
            }
        }
        if (!isDealer) {
            if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                    || regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                //Add Conditions of invalid noc data....
                NocDobj nocEndorseDobj = ServerUtil.getNocEndorsementData(regnNo.trim().toUpperCase(), null);
                NocDobj nocVerificationDobj = ServerUtil.getNocVerifiedData(regnNo.trim().toUpperCase(), null);
                if (nocDobj != null && nocEndorseDobj == null && nocVerificationDobj == null) {
                    if ((nocDobj.getState_to().equalsIgnoreCase(sessionVariables.getStateCodeSelected()) && nocDobj.getOff_to() == 0)
                            || (nocDobj.getState_to().equalsIgnoreCase(sessionVariables.getStateCodeSelected()) && nocDobj.getOff_to() != sessionVariables.getOffCodeSelected())) {
                        State stateTo = MasterTableFiller.state.get(nocDobj.getState_to());
                        State stateFrom = MasterTableFiller.state.get(nocDobj.getState_cd());
                        workBenchModel.setStLabel(stateTo == null ? "Unknown State" : stateTo.getStateDescr());
                        workBenchModel.setStFromLabel(stateTo == null ? "Unknown State" : stateTo.getStateDescr());
                        workBenchModel.setOffCdLabel("Unknown Off Code");
                        workBenchModel.setOffCdFromLabel("Unknown Off Code");
                        if (stateTo != null) {
                            workBenchModel.setStLabel(stateTo.getStateDescr());
                            List<SelectItem> listOff = stateTo.getOffice();
                            for (SelectItem off : listOff) {
                                if (off.getValue().equals(nocDobj.getOff_to())) {
                                    workBenchModel.setOffCdLabel(off.getLabel());
                                    break;
                                }
                            }
                        }
                        if (stateFrom != null) {
                            workBenchModel.setStFromLabel(stateFrom.getStateDescr());
                            List<SelectItem> listOff = stateFrom.getOffice();
                            for (SelectItem off : listOff) {
                                if (off.getValue().equals(nocDobj.getOff_cd())) {
                                    workBenchModel.setOffCdFromLabel(off.getLabel());
                                    break;
                                }
                            }
                        }
                    }
                }
                owner_dobj = otherStateVehImpl.set_Owner_appl_db_to_dobj(regnNo.trim().toUpperCase());
                otherStateVehDobj.setOldRegnNo(regnNo.trim().toUpperCase());
                if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                    otherStateVehDobj.setOldStateCD(sessionVariables.getStateCodeSelected());
                    State curSt = MasterTableFiller.state.get(otherStateVehDobj.getOldStateCD());

                    officeList.clear();
                    officeList.addAll(curSt.getOffice());
                }
                owner_bean.setDisableOwnerSr(false);

                if (owner_dobj != null) {
                    isValidChassiNo = OwnerImpl.checkLast5ChassisChar(owner_dobj.getChasi_no(), chasiNo);
                    workBenchModel.setIsValidChassiNo(isValidChassiNo);

                    owner_bean.setPrevCurrAdd(otherStateVehImpl.getCurrentAddressInString(owner_dobj.getRegn_no(), owner_dobj.getState_cd(), owner_dobj.getOff_cd()));
                    owner_bean.setRenderPrevCurrentAdd(true);
                    owner_dobj.setC_add1(null);
                    owner_dobj.setC_add2(null);
                    owner_dobj.setC_add3(null);
                    owner_dobj.setC_state(sessionVariables.getStateCodeSelected());
                    owner_dobj.setC_pincode(0);
                    owner_bean.setDisableOtherState(true);
                }

            } else if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_SCRAPPED)) {
            } else {
                //owner_dobj = owner_Impl.set_Owner_appl_db_to_dobj("", "", chasiNo.trim().toUpperCase(), TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE);
                if (!Utility.isNullOrBlank(regnNo)) {
                    owBacklog = OwnerImpl.getBacklogDetails(regnNo, null);
                }
                if (owBacklog == null && !Utility.isNullOrBlank(chasiNo)) {
                    owBacklog = OwnerImpl.getBacklogDetails(null, chasiNo);
                }

                //Fill Details from Backlog 
                if (owner_dobj == null) {
                    owner_dobj = owBacklog;
                }
            }
            if ((owner_dobj == null
                    && !regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                    && !regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)
                    && !regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_SCRAPPED)) || (isNewVehicleFitness && ownerHomo != null)) {

                if (!isNewVehicleFitness) {
                    owner_dobj = ownerHomo;
                }
                if (ownerHomo != null) {
                    isDetailsFromHomologation = true;
                    owner_bean.setIsHomologationData(TableConstants.HOMOLOGATION_DATA);
                    owner_bean.setHomoLdWt(ownerHomo.getLd_wt());
                    owner_bean.setHomoUnLdWt(ownerHomo.getUnld_wt());
                    owner_bean.setHomoDobj(ownerHomo);
                }
            }

        }
        if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_TEMPORARY)
                && ((Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE)
                || (Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE && tmConfDobj.isTempRegnToNewRegnAtDealer())
                || (owneTmpDobj != null && owneTmpDobj.getDob_temp() != null && owneTmpDobj.getDob_temp().getPurpose() != null && owneTmpDobj.getDob_temp().getPurpose().equalsIgnoreCase("BB") && (Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_TEMP_REG || Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE)))) {

            // disableCurrentState = true;
            workBenchModel.setDisableCurrentState(true);
            // condition for dealer
            if (user_catg != null && user_catg.equals(TableConstants.USER_CATG_DEALER)) {
                Map makerAndDealerDetail = OwnerImpl.getDealerDetail(user_cd);
                workBenchModel.setDealerCd(makerAndDealerDetail.get("dealer_cd").toString());
            }
            if (owneTmpDobj == null) {
                TempRegDobj tempDobj = new TempRegDobj();
                owner_bean.setTempReg(tempDobj);
            } else {
                if (!isNewVehicleFitness) {
                    if (ownerHomo == null) {
                        owner_dobj = owneTmpDobj;
                    } else {
                        owner_dobj = owner_bean.setOwnerTempDetails(owner_dobj, owneTmpDobj);
                    }
                    owner_dobj.setOwner_sr(1);
                    owner_bean.setTempLdWt(owner_dobj.getLd_wt());
                    owner_bean.setTempUnLdWt(owner_dobj.getUnld_wt());
                } else {
                    if (owneTmpDobj != null && owner_dobj != null) {
                        owner_dobj.setTempReg(owneTmpDobj.getTempReg());
                    }
                }
                if (owneTmpDobj.getDob_temp() != null && owneTmpDobj.getDob_temp().getPurpose() != null
                        && owneTmpDobj.getDob_temp().getPurpose().equalsIgnoreCase("OM")) {
                    owner_dobj.setOwner_name("");
                    owner_dobj.setF_name("");
                    owner_dobj.setOwner_cd(-1);
                    owner_dobj.setDealer_cd("0");
                    owner_dobj.setC_add1(null);
                    owner_dobj.setC_add2(null);
                    owner_dobj.setC_add3(null);
                    owner_dobj.setC_state(sessionVariables.getStateCodeSelected());
                    owner_dobj.setC_district(-1);
                    owner_dobj.setC_pincode(0);
                    owner_dobj.setP_add1(null);
                    owner_dobj.setP_add2(null);
                    owner_dobj.setP_add3(null);
                    owner_dobj.setP_state(sessionVariables.getStateCodeSelected());
                    owner_dobj.setP_district(-1);
                    owner_dobj.setP_pincode(0);
                    if (owner_dobj.getTempReg() != null) {
                        owner_dobj.getTempReg().setTmp_off_cd(0);
                        owner_dobj.getTempReg().setTmp_regn_no(null);
                        owner_dobj.getTempReg().setTmp_state_cd(null);
                        owner_dobj.getTempReg().setTmp_valid_upto(null);
                        owner_dobj.getTempReg().setTmp_regn_dt(null);
                        owner_dobj.getTempReg().setDealer_cd("0");
                    }
                    if (owner_dobj.getOwner_identity() != null) {
                        owner_dobj.setOwner_identity(null);
                        OwnerIdentificationDobj identificationDobj = new OwnerIdentificationDobj();
                        owner_dobj.setOwner_identity(identificationDobj);
                    }
                }
            }
            owner_bean.setBlnRegnTypeTemp(true);
        } else {
            owner_bean.setBlnRegnTypeTemp(false);
            owner_bean.setTempReg(null);
        }
        if (Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
            //this method is already used with variable owneTmpDobj
            //and below code should also be used for dealer temporary registration action code
            //wnd why this check was used?
            // disableCurrentState = false;
            workBenchModel.setDisableCurrentState(false);
        }

        if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_EXARMY)) {
            owner_bean.setExArmyVehicle_Visibility_tab(true);
        }

        if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_CONFISCATED_AUCTION)) {
            if (owner_dobj != null && owner_dobj.getAuctionDobj() != null) {
                setForAuctionVehicle(owner_dobj.getAuctionDobj(), owner_dobj.getPurchase_dt(), owner_bean, owner_dobj, true, sessionVariables);
            } else {
                owner_dobj = ownerHomo;
                setForAuctionVehicle(auctionDobj, null, owner_bean, null, true, sessionVariables);
            }
        }
        InsBeanModel ins_bean = wrapperModel.getIns_bean();
        if (owner_dobj != null) {
            if (owner_dobj.getSpeedGovernorDobj() != null) {
                owner_bean.setRenderSpeedGov(true);
            }

            if (owner_dobj.getReflectiveTapeDobj() != null) {
                owner_bean.setRenderReflectiveTape(true);
            }

            if ((regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                    || regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))
                    && owner_dobj.getStatus().equalsIgnoreCase(TableConstants.VT_NOC_ISSUE_STATUS)) {
                NocImpl impl = new NocImpl();
                owner_bean.setDisableChasiNo(true);
                owner_bean.setRenderChasiOs(true);
                InsDobj insDobj = owner_dobj.getInsDobj();
                if (insDobj != null) {
                    ins_bean.set_Ins_dobj_to_bean(insDobj);
                    if (insDobj.isIibData()) {
                        ins_bean.componentReadOnly(false);
                    }
                    if (insDobj.getIns_type() == Integer.parseInt(TableConstants.INS_TYPE_NA)) {
                        ins_bean.insTypeListener();
                    }
                    wrapperModel.setIns_bean(ins_bean);
                }

                if (owner_dobj.getBlackListedVehicleDobj() != null) {
                    throw new VahanException("Vehicle is BlackListed due to Reason [" + owner_dobj.getBlackListedVehicleDobj().getComplainDesc().toUpperCase() + "] Dated [ " + owner_dobj.getBlackListedVehicleDobj().getComplaindt() + " ] in State [ " + owner_dobj.getBlackListedVehicleDobj().getStateName() + " ] at Office [ " + owner_dobj.getBlackListedVehicleDobj().getOfficeName() + " ]");
                }
                if (nocDobj != null) {
                    if (nocDobj.getState_to().equalsIgnoreCase(sessionVariables.getStateCodeSelected())
                            && nocDobj.getOff_to() == sessionVariables.getOffCodeSelected()) {

                        otherStateVehDobj = otherStateVehImpl.setNocDtailsToOtherStateVeh(nocDobj);
                        //Temporary Solution...Need to Fix it
                        Map<Object, Object> office = ServerUtility.getOfficeListOfState(otherStateVehDobj.getOldStateCD());
                        officeList.clear();
                        for (Map.Entry<Object, Object> entry : office.entrySet()) {
                            int off_cd = (int) entry.getKey();
                            String off_name = (String) entry.getValue();
                            officeList.add(new SelectItem(off_cd, off_name));
                        }

                        if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                        } else if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)) {
                        }
                    } else {
                        // for Same Registration No
                        nocVerifiedData(regnNo, otherStateVehImpl, officeList);
                    }
                } else {
                    // for Different Registration No
                    nocVerifiedData(regnNo, otherStateVehImpl, officeList);
                }
            }
            if (!isNewVehicleFitness) {
                if (owner_dobj.getFuel() == TableConstants.VM_FUEL_CNG_TYPE
                        || owner_dobj.getFuel() == TableConstants.VM_FUEL_TYPE_PETROL_CNG
                        || owner_dobj.getFuel() == TableConstants.VM_FUEL_TYPE_LPG
                        || owner_dobj.getFuel() == TableConstants.VM_FUEL_TYPE_PETROL_LPG) {
                    owner_bean.setCngDetails_Visibility_tab(true);
                    RetroFittingDetailsBeanModel cngBean = new RetroFittingDetailsBeanModel();
                    wrapperModel.setCngDetailsBean(cngBean);
                    cngBean.setDobj_To_Bean(owner_dobj.getCng_dobj());
                    workBenchModel.setFacesMessage("retroFittingDetailsBean");
                }
            }
            //-----------Filling Axle Details Start---------------------
            AxleBeanModel axleBean = new AxleBeanModel();
            wrapperModel.setAxleBean(axleBean);
            workBenchModel.setFacesMessage("axleBean");
            axleBean.setDobj_To_Bean(owner_dobj.getAxleDobj());

            //HPA Details
            if (owner_dobj.getListHpaDobj() != null) {
                if (!owner_dobj.getListHpaDobj().isEmpty() && owner_dobj.getListHpaDobj().get(0) != null) {
                    workBenchModel.setFlag(true);
                    workBenchModel.setHypothecated(true);
                    HpaBeanModel hpa_bean = new HpaBeanModel();
                    wrapperModel.setHpaBeanModel(hpa_bean);
                    workBenchModel.setFacesMessage("hpa_bean");
                    hpa_bean.setHpaDobj(owner_dobj.getListHpaDobj().get(0));
                    hpa_bean.StateFncrListener();
                }
            }

            /////////////////////// Filling Imported Vehicle Details //////////////
            if (owner_bean.getImported_veh() != null && owner_bean.getImported_veh().trim().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_IMPORTED_YES)) {
                ImportedVehicleBeanModel imp_bean = new ImportedVehicleBeanModel();
                if (owner_dobj.getImp_Dobj() != null) {
                    workBenchModel.setFacesMessage("importedVehicleBean");
                    wrapperModel.setImportedVehicleBean(imp_bean);
                    imp_bean.setDobj_to_Bean(owner_dobj.getImp_Dobj());
                    owner_bean.setImportedVehicle_Visibility_tab(true);
                    imp_bean.setImp_dobj_prv(owner_dobj.getImp_Dobj());
                }
            }
            if (owner_bean != null) {
                owner_bean.purchaseDateListener(wrapperModel.getCngDetailsBean(), wrapperModel.getHpaBeanModel(), wrapperModel.getIns_bean());
            }
        } else if ((regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                || regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) && owner_dobj == null) {
            owner_bean.setDisableOwnerSr(false);
            owner_bean.setRenderChasiOs(true);
        }
        if (regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_CD)) {
            owner_bean.getOwnerDobj().setCdDobj(new CdDobj());
            // setRenderCDVehicle(true);
            workBenchModel.setRenderCDVehicle(true);
        }
        if (Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                || Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_DEALER_NEW_VEHICLE) {
            if (regnType != null && !regnType.equals("") && regnType.equals(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)) {
                if (!owner_bean.isToRetention()) {
                    owner_bean.setBlnPgAdvancedRegNo(false);
                } else {
                    owner_bean.setBlnPgAdvancedRegNo(true);
                }
            } else {
                owner_bean.setBlnPgAdvancedRegNo(true);
            }
        }
        /**
         * @UpdatedBy Kartikey Singh
         */
        if (owner_dobj != null) {
            owner_dobj.setRegn_type(regnType);
        }

        // tabView = true;
        workBenchModel.setTabView(true);

        owner_bean.setDealerSaleAmt(disableSaleAmount);

        if (isDetailsFromHomologation) {
            if (owner_dobj.getManu_mon() == null || owner_dobj.getManu_mon() == 0) {
                owner_bean.setDisableManuMonth(false);
            } else {
                owner_bean.setDisableManuMonth(true);
            }
            if (owner_dobj.getManu_yr() == null || owner_dobj.getManu_yr() == 0) {
                owner_bean.setDisableManuYr(false);
            } else {
                owner_bean.setDisableManuYr(true);
            }
            owner_bean.setRenderModelEditable(false);
        }
        if (user_catg != null && user_catg.trim().equals(TableConstants.User_Dealer)) {
            Map makerAndDealerDetail = OwnerImplementation.getDealerDetail(user_cd, userStateCode, selectedOffCode);
            owner_bean.getOwnerDobj().setDealer_cd(makerAndDealerDetail.get("dealer_cd").toString());
            if (owner_dobj != null) {
                owner_dobj.setDealer_cd(makerAndDealerDetail.get("dealer_cd").toString());
            }
        } else {//RTO Official
            if (isDetailsFromHomologation && owner_bean.getVch_purchase_as() != null && owner_bean.getVch_purchase_as().equals(TableConstants.PURCHASE_AS_CHASIS)) {
                owner_bean.enableForDriveAwayChassisInDealer(false);
            }
        }
//        if (ins_bean != null && ins_bean.getInsType() != 0 && ins_bean.getInsType() != Integer.parseInt(TableConstants.INS_TYPE_NA)) {
//            ins_bean.setInsUptoDisable(false);
//        }

        if (owner_dobj != null && owner_dobj.getVh_class() != 0) {
            if (ServerUtil.isTransport(owner_dobj.getVh_class(), owner_dobj)) {
                owner_bean.setDisableLdWtCap(false);
                owner_bean.setDisableUnLdWtCap(false);
            } else {
                if (isDetailsFromHomologation) {
                    if (user_catg != null && user_catg.trim().equals(TableConstants.User_Dealer)) {
                        owner_bean.setDisableLdWtCap(true);
                        owner_bean.setDisableUnLdWtCap(true);
                    } else {
                        if (owner_bean.getVch_purchase_as() != null && !owner_bean.getVch_purchase_as().equals(TableConstants.PURCHASE_AS_CHASIS)) {
                            owner_bean.setDisableLdWtCap(true);
                            owner_bean.setDisableUnLdWtCap(true);
                        } else {
                            owner_bean.setDisableLdWtCap(false);
                            owner_bean.setDisableUnLdWtCap(false);
                        }
                    }
                }
            }
        }
        if (tmConfDobj != null && tmConfDobj.getTmConfigDealerDobj() != null && owner_dobj == null) {
            boolean flag = false;
            if (tmConfDobj.getTmConfigDealerDobj().isAllowRegnIfDataFound()) {
                if ((Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && (TableConstants.VM_REGN_TYPE_NEW.equals(regnType) || TableConstants.VM_REGN_TYPE_IMPORTED_YES.equals(regnType) || TableConstants.VM_REGN_TYPE_SCRAPPED.equals(regnType))) || Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_TEMP_REG) {
                    flag = true;
                }
            }
            if (workBenchModel.isRenderTempStateOffPanel()) {
                if (tmConfDobj.isAllowRegnDataFoundForOS_OR() && Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE && (TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE.equals(regnType) || TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE.equals(regnType))) {
                    if (owner_bean != null && owner_bean.getTemp_dobj() != null && !"KL".equals(owner_bean.getTemp_dobj().getTempState_cd_from())) {
                        Date v4StartDate = ServerUtil.getVahan4StartDate(owner_bean.getTemp_dobj().getTempState_cd_from(), owner_bean.getTemp_dobj().getTempOff_cd_from());
                        if (v4StartDate != null) {
                            flag = true;
                        }
                    }
                }
            }
            if (flag) {
                //tabView = false;
                workBenchModel.setTabView(false);

                workBenchModel.setRenderAdvanceNoOption(false);
                throw new VahanException("Application can't be inwarded as vehicle/chassis details not available in V4.");
            }
        }
        if (Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE
                && regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_TEMPORARY)) {
            if (owner_dobj != null && owner_dobj.getVh_class() != 0) {
                if (ServerUtil.isTransport(owner_dobj.getVh_class(), owner_dobj) && sessionVariables.getStateCodeSelected().equalsIgnoreCase("DL")) {
                    owner_bean.setDisableFuelType(false);
                }
            }
        }

        if (sessionVariables.getSelectedWork().getAction_cd() == TableConstants.TM_ROLE_DEALER_NEW_APPL
                || sessionVariables.getSelectedWork().getAction_cd() == TableConstants.TM_ROLE_NEW_APPL
                || sessionVariables.getSelectedWork().getAction_cd() == TableConstants.TM_ROLE_NEW_APPL_TEMP
                || sessionVariables.getSelectedWork().getAction_cd() == TableConstants.TM_ROLE_DEALER_TEMP_APPL) {
            if (CommonUtils.isNullOrBlank(APPL_NO)) {
                if ((regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE)
                        || regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE)
                        || regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_TEMPORARY))
                        && owner_dobj != null) {
                } else {
                    owner_bean.setRenderOwnerDtlsPartialBtn(true);
                    owner_bean.setRenderHpDtls(false);
                    owner_bean.setRenderVehDtls(false);
                    owner_bean.setBlnRegnTypeTemp(false);
                    workBenchModel.setTrailer_tab(false);
                    workBenchModel.setOtherDistrictVehicle(false);
                    workBenchModel.setOtherStateVehicle(false);
                    workBenchModel.setRenderCDVehicle(false);
                    workBenchModel.setBlnScrappedVehicle(false);
                    workBenchModel.setRenderAppDisapp(false);
                }
            } else {
                owner_bean.setRenderVehicleDtlsPartialBtn(true);
            }
        }

        //Fancy & Retention number mapping not allowed on temporary regn number
        if ((Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE
                || Integer.parseInt(PUR_CD) == TableConstants.VM_TRANSACTION_MAST_TEMP_REG)
                && regnType.equalsIgnoreCase(TableConstants.VM_REGN_TYPE_TEMPORARY)) {
            owner_bean.setBlnPgAdvancedRegNo(false);
        }
        if (owner_dobj != null) {
            owner_bean.setSaleAmtInWords("Rs. " + new Utility().ConvertNumberToWords(owner_dobj.getSale_amt()).toLowerCase());
        }
        if (owner_dobj != null) {
            owner_bean.setOwnerDobj(owner_dobj);
        }
        owner_bean.setHomoDobj(ownerHomo);

        wrapperModel.setOwnerBean(owner_bean);
        wrapperModel.setWorkBench(workBenchModel);
        return wrapperModel;
    }

    public void nocVerifiedData(String regnNo, OtherStateVehImpl otherStateVehImpl, List officeList) throws VahanException {;
        OtherStateVehDobj otherStateVehDobj = new OtherStateVehDobj();
        NocDobj nocVerificationdobj = ServerUtil.getNocVerifiedData(regnNo, null);
        otherStateVehDobj = otherStateVehImpl.setNocVerificationDetailsToOtherStateVeh(nocVerificationdobj);
        //Temporary Solution...Need to Fix it
        Map<Object, Object> office = ServerUtil.getOfficeListOfState(otherStateVehDobj.getOldStateCD());
        officeList.clear();
        for (Map.Entry<Object, Object> entry : office.entrySet()) {
            int off_cd = (int) entry.getKey();
            String off_name = (String) entry.getValue();
            officeList.add(new SelectItem(off_cd, off_name));
        }
    }

    public void setForAuctionVehicle(AuctionDobj auctionDobj, Date purchaseDate, OwnerBeanModel owner_bean, Owner_dobj owner_dobj,
            boolean clearAddressDetail, SessionVariablesModel sessionVariables) {
        if (auctionDobj != null) {
            owner_bean.setAuctionVisibilityTab(true);
            auctionDobj.setPurchaseDate(purchaseDate);
            auctionDobj.setDisableAuctionPanel(true);
            if (clearAddressDetail) {
                if (owner_dobj != null) {
                    owner_dobj.setOwner_name("");
                    owner_dobj.setF_name("");
                    owner_dobj.setOwner_cd(-1);
                    owner_dobj.setDealer_cd("0");
                    owner_dobj.setC_add1(null);
                    owner_dobj.setC_add2(null);
                    owner_dobj.setC_add3(null);
                    owner_dobj.setC_state(sessionVariables.getStateCodeSelected());
                    owner_dobj.setC_district(-1);
                    owner_dobj.setC_pincode(0);
                    owner_dobj.setP_add1(null);
                    owner_dobj.setP_add2(null);
                    owner_dobj.setP_add3(null);
                    owner_dobj.setP_state(sessionVariables.getStateCodeSelected());
                    owner_dobj.setP_district(-1);
                    owner_dobj.setP_pincode(0);
                    owner_dobj.getOwner_identity().setMobile_no(0l);
                    owner_dobj.getOwner_identity().setOwnerCatg(-1);
                    owner_dobj.getOwner_identity().setAadhar_no(null);
                    owner_dobj.getOwner_identity().setDl_no(null);
                    owner_dobj.getOwner_identity().setEmail_id(null);
                    owner_dobj.getOwner_identity().setPan_no(null);
                    owner_dobj.getOwner_identity().setPassport_no(null);
                    owner_dobj.getOwner_identity().setRation_card_no(null);
                    owner_dobj.getOwner_identity().setVoter_id(null);
                    owner_dobj.setOwner_sr((owner_dobj.getOwner_sr() + 1));
                }
            }
            owner_bean.getOwnerDobj().setAuctionDobj(auctionDobj);
        }
    }
}
