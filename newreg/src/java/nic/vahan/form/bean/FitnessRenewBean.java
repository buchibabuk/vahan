/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.ConvDobj;
import nic.vahan.form.dobj.EpayDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.ReflectiveTapeDobj;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;
import nic.vahan.form.dobj.SpeedGovernorDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.VmValidityMastDobj;
import nic.vahan.form.dobj.fitness.TmConfigurationFitnessDobj;
import nic.vahan.form.impl.ApplicationInwardImpl;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.ConvImpl;
import nic.vahan.form.impl.EpayImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.RetroFittingDetailsImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.primefaces.component.selectmanycheckbox.SelectManyCheckbox;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import static nic.vahan.CommonUtils.FormulaUtils.fillVehicleParametersFromDobj;
import static nic.vahan.CommonUtils.FormulaUtils.isCondition;
import static nic.vahan.CommonUtils.FormulaUtils.replaceTagValues;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.form.dobj.PuccDobj;
import nic.vahan.form.impl.InsPuccUpdateImpl;
import nic.vahan.form.impl.VehicleTrackingDetailsImpl;
import nic.vahan.services.DmsDocCheckUtils;
import nic.vahan.services.VTDocumentModel;
import org.primefaces.PrimeFaces;
import nic.vahan.server.CommonUtils;

/**
 *
 * @author tranC094
 */
@ManagedBean(name = "fitness_renew_bean")
@ViewScoped
public class FitnessRenewBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(FitnessRenewBean.class);
    private FitnessDobj fitness_renew_dobj = new FitnessDobj();
    private FitnessDobj fitnessDetails = null;
    private FitnessDobj tempFitnessDetails = null;
    private FitnessDobj fitness_renew_dobj_prv;
    private SelectManyCheckbox parameters = new SelectManyCheckbox();
    private Map<String, Integer> fit_off_cd1_list;
    private Map<String, Integer> fit_off_cd2_list;
    private ArrayList parameters_list = new ArrayList();
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<ComparisonBean> prevChangedDataList;
    private Date maxDate = new Date();
    private Date minDate = new Date();
    private Owner_dobj owner;
    private OwnerDetailsDobj ownerDetail;
    private boolean isMultipleFitnessOfficer;
    private TmConfigurationDobj tmConfigurationDobj;
    private TmConfigurationFitnessDobj tmConfigFitnessDobj;
    private String vahanMessages = null;
    private EpayDobj checkFeeTax = null;
    private boolean autoRunCheckFeeTax = false;
    private boolean isDisableFitnessUpto;
    private boolean disableFitnessOfficer1;
    private Date fitUptoMaxDate = new Date();
    private Date fitUptoMinDate = new Date();
    private SpeedGovernorDobj speedGovernorDobjPrev;
    private boolean renderSpeedGov = false;
    private ConvDobj convDobj = null;
    private List listSpeedGovTypes;
    private boolean renderReflectiveTape;
    private ReflectiveTapeDobj reflectiveTapeDobjPrev = null;
    private boolean isReflectiveTapeAllowed = false;
    private RetroFittingDetailsDobj retroFittingDetailsDobj = new RetroFittingDetailsDobj();
    private RetroFittingDetailsDobj retroFittingDetailsDobjPrev = null;
    private boolean renderRetroDetails;
    private boolean renderSpeedGoverner = true;
    private boolean isDisableSpeedGovernerChkBox = false;
    private boolean isDisableReflectiveTapeChkBox = false;
    private boolean speedGovernorSrvcData = false;
    private boolean reflectiveTapeSrvcData = false;
    private boolean skipPuccValidation = false;
    private boolean renderFileUploadPanel = false;
    private String dmsFileUploadUrl;
    private boolean render_vltd_details = false;
    private boolean renderVltdDialog = false;
    private String paymentRemarks = null;
    private boolean renderSLDDialog = false;
    private PuccDobj puccDobj = null;

    public FitnessRenewBean() {
        try {
            if (getAppl_details() == null
                    || getAppl_details().getCurrent_state_cd() == null
                    || getAppl_details().getCurrent_off_cd() == 0) {
                vahanMessages = "Something went wrong, Please try again...";
                return;
            }
            if (getAppl_details() != null) {
                owner = getAppl_details().getOwnerDobj();

                ownerDetail = getAppl_details().getOwnerDetailsDobj();
            }
            if (owner == null || ownerDetail == null) {
                vahanMessages = "Owner Details not Found in the Database, Please Contact to the System Administrator";
                return;
            }

            isDisableFitnessUpto = true;
            fit_off_cd1_list = ServerUtil.getFitOfficerList(appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd());

            if (fit_off_cd1_list == null || fit_off_cd1_list.isEmpty()) {
                vahanMessages = "No Fitness/Inspection Officer found in this office, Please Contact to the System Administrator";
                return;
            }

            if (appl_details.getCurrent_action_cd() == TableConstants.FITNESS_INSPECTION_APP) {
                vahanMessages = "This Action Can Perform only on Fitness Mobile App (Android based mVahan App), please contact Office Admin !!!!";
                return;
            }

            // for Adding CheckBox in the Inspection Role start
            parameters_list.add(new SelectItem("1", "BRAKES"));
            parameters_list.add(new SelectItem("2", "STEERING"));
            parameters_list.add(new SelectItem("3", "SUSPENSION"));
            parameters_list.add(new SelectItem("4", "ENGINE"));
            parameters_list.add(new SelectItem("5", "TYRES"));
            parameters_list.add(new SelectItem("6", "HORN"));
            parameters_list.add(new SelectItem("7", "LAMP"));
            parameters_list.add(new SelectItem("8", "EMBOSSING"));
            parameters_list.add(new SelectItem("9", "SPEEDOMETER"));
            parameters_list.add(new SelectItem("10", "PAINT"));
            parameters_list.add(new SelectItem("11", "WIPER"));
            parameters_list.add(new SelectItem("12", "DIMENSION"));
            parameters_list.add(new SelectItem("13", "BODY"));
            parameters_list.add(new SelectItem("14", "FAREMETER"));
            parameters_list.add(new SelectItem("15", "ELECTRICAL"));
            parameters_list.add(new SelectItem("16", "FINISHING"));
            parameters_list.add(new SelectItem("17", "ROAD WORTHINESS"));
            parameters_list.add(new SelectItem("18", "POLLUTION"));
            parameters_list.add(new SelectItem("19", "TRANSMISSION"));
            parameters_list.add(new SelectItem("20", "GLASS"));
            parameters_list.add(new SelectItem("21", "EMISSION"));
            parameters_list.add(new SelectItem("22", "REAR"));
            parameters_list.add(new SelectItem("23", "OTHERS"));
            // adding checkbox end           

            tmConfigurationDobj = Util.getTmConfiguration();//getting state configuration from session
            if (tmConfigurationDobj == null) {
                tmConfigurationDobj = ServerUtil.getTmConfigurationParameters(getAppl_details().getCurrent_state_cd());
            }

            //state configuration for fitness according to the States rules
            FitnessImpl fitness_Impl = new FitnessImpl();
            tmConfigFitnessDobj = fitness_Impl.getFitnessConfiguration(getAppl_details().getCurrent_state_cd());
            if (tmConfigFitnessDobj != null) {
                isReflectiveTapeAllowed = tmConfigFitnessDobj.isReflective_tape_allowed();
                isMultipleFitnessOfficer = tmConfigFitnessDobj.isMultiple_fit_officer();//checking if there is multiple fitness officer or not
                if (tmConfigFitnessDobj.isUploadFitnessImageAllowed() && getAppl_details().getPur_cd() == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) { //if uploaded image to be shown
                    appl_details.setDocumentUploadShow(true);
                }
            }

            if (isMultipleFitnessOfficer) {
                fit_off_cd2_list = fit_off_cd1_list;
            }
            if (!isMultipleFitnessOfficer && tmConfigFitnessDobj != null
                    && tmConfigFitnessDobj.isLogged_in_user_is_fitness_officer()) {
                if (getAppl_details().getCurrent_action_cd() == TableConstants.FITNESS_INSPECTION_CERTIFICATE_VERIFICATION
                        || getAppl_details().getCurrent_action_cd() == TableConstants.FITNESS_APPROVAL) {
                    if (!fit_off_cd1_list.containsValue(Integer.parseInt(getAppl_details().getCurrentEmpCd()))) {
                        vahanMessages = "You are not Authorized Fitness Officer, Please contact to the system administrator";
                        return;
                    }
                }
            }

            if (getAppl_details() != null) {
                owner.setSpeedGovernerList(new ArrayList());
                setListSpeedGovTypes(OwnerImpl.getListSpeedGovTypes());
                String[][] data = MasterTableFiller.masterTables.VM_SPEEDGOV_MANUFACTURE.getData();
                for (int i = 0; i < data.length; i++) {
                    owner.getSpeedGovernerList().add(new SelectItem(data[i][0], data[i][1]));
                }
                //**Start--for setting checking if there is any temporary fitness exist or not
                tempFitnessDetails = fitness_Impl.getVtFitnessTempDetails(owner.getRegn_no());
                //**End
                fitness_renew_dobj = fitness_Impl.set_Fitness_appl_db_to_dobj(null, getAppl_details().getAppl_no());
                fitnessDetails = fitness_Impl.set_Fitness_appl_db_to_dobj(getAppl_details().getRegn_no(), null);

                SpeedGovernorDobj speedGovernorDobj = fitness_Impl.getSpeedGovernorDetails(getAppl_details().getAppl_no());
                ReflectiveTapeDobj reflectiveTapeDobj = fitness_Impl.getVaOrVtReflectiveTapeDobj(getAppl_details().getAppl_no(), null);

                //to Get & Set SPEED_GOVERNOR_REFLECTIVE_TAPE, imported data from SUVAS Service --aded by Amitesh
                if (tmConfigFitnessDobj != null) {
                    if (tmConfigFitnessDobj.isSpeed_governor_reflective_tape_data_from_suvas()) {
                        if (speedGovernorDobj == null) {
                            speedGovernorDobj = fitness_Impl.getSpeedGovernorDetailsFrmServiceData(getAppl_details().getAppl_no(), getAppl_details().getRegn_no(), owner.getOff_cd(), owner.getState_cd());
                            if (speedGovernorDobj != null) {
                                speedGovernorSrvcData = true;
                                owner.setSpeedGovernorDobj(speedGovernorDobj);
                            }
                        }
                        if (reflectiveTapeDobj == null) {
                            reflectiveTapeDobj = fitness_Impl.getVtReflectiveTapeFrmServiceData(getAppl_details().getAppl_no(), getAppl_details().getRegn_no(), owner.getOff_cd(), owner.getState_cd());
                            if (reflectiveTapeDobj != null) {
                                reflectiveTapeSrvcData = true;
                                owner.setReflectiveTapeDobj(reflectiveTapeDobj);
                            }
                        }
                    }
                    if (speedGovernorDobj == null) {
                        VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(owner);
                        if (isCondition(replaceTagValues(tmConfigFitnessDobj.getCheckSldFitment(), vehParameters), "getsld_condition_formula")) {
                            if (speedGovernorDobj == null) {
                                speedGovernorDobj = fitness_Impl.getSpeedGovernorDetails(getAppl_details().getRegn_no(), owner.getOff_cd(), owner.getState_cd());
                            }
                            if (speedGovernorDobj == null) {
                                speedGovernorDobj = fitness_Impl.getSLDInfo(getAppl_details().getAppl_no(), getAppl_details().getRegn_no(), owner.getOff_cd(), owner.getState_cd());
                                if (speedGovernorDobj == null) {
                                    renderSLDDialog = true;
                                } else {
                                    owner.setSpeedGovernorDobj(speedGovernorDobj);
                                }
                            } else {
                                speedGovernorDobj.setAppl_no(appl_details.getAppl_no());
                                owner.setSpeedGovernorDobj(speedGovernorDobj);
                            }
                        }
                    }

                    //Disable all field of Speed Governer Details 
                    if (speedGovernorDobj != null && !speedGovernorDobj.getSgFitmentCerticateNo().isEmpty()
                            && !speedGovernorDobj.getSgFitmentCerticateNo().equalsIgnoreCase("Null")) {
                        speedGovernorDobj.setIsDisable_sg_no(true);
                        speedGovernorDobj.setIsDisable_sg_fitted_on(true);
                        speedGovernorDobj.setIsDisable_sg_fitted_at(true);
                        speedGovernorDobj.setIsDisable_sgTypeApprovalNo(true);
                        speedGovernorDobj.setIsDisable_sgTestReportNo(true);
                        speedGovernorDobj.setIsDisable_sgGovType(true);
                        speedGovernorDobj.setIsDisable_sgFitmentCerticateNo(true);
                        this.setIsDisableSpeedGovernerChkBox(true);
                        renderSpeedGov = true;
                    }
                    //Disable all field of Reflective Tape Details 
                    if (reflectiveTapeDobj != null
                            && !reflectiveTapeDobj.getCertificateNo().isEmpty()
                            && !reflectiveTapeDobj.getCertificateNo().equalsIgnoreCase("Null")) {
                        reflectiveTapeDobj.setIsDisable_certificateNo(true);
                        reflectiveTapeDobj.setIsDisable_fitmentDate(true);
                        reflectiveTapeDobj.setIsDisable_manuName(true);
                        this.setIsDisableReflectiveTapeChkBox(true);
                        renderReflectiveTape = true;
                    }
                }

                if (fitness_renew_dobj != null) {
                    fitness_renew_dobj.setAppl_no(getAppl_details().getAppl_no());
                    fitness_renew_dobj.setRegn_no(getAppl_details().getRegn_no());
                    ArrayList list = fitness_renew_dobj.getList_parameters();
                    ArrayList newlist = new ArrayList();
                    if (list != null) {
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).toString().equals("Y")) {
                                newlist.add(i + 1);
                            }
                        }
                        this.parameters.setSelectedValues(newlist.toArray());
                    }
                    fitness_renew_dobj_prv = (FitnessDobj) fitness_renew_dobj.clone();

                    if (speedGovernorDobj != null) {
                        speedGovernorDobjPrev = (SpeedGovernorDobj) speedGovernorDobj.clone();
                        owner.setSpeedGovernorDobj(speedGovernorDobj);
                        renderSpeedGov = true;
                    }
                    if (reflectiveTapeDobj != null) {
                        reflectiveTapeDobjPrev = (ReflectiveTapeDobj) reflectiveTapeDobj.clone();
                        owner.setReflectiveTapeDobj(reflectiveTapeDobj);
                        renderReflectiveTape = true;
                    }

                    if (fitness_renew_dobj.getFit_off_cd1() != 0 && !fit_off_cd1_list.containsValue(fitness_renew_dobj.getFit_off_cd1())) {
                        fit_off_cd1_list.put(ServerUtil.getUserName(fitness_renew_dobj.getFit_off_cd1()), fitness_renew_dobj.getFit_off_cd1());
                    }
                    if (isMultipleFitnessOfficer && fitness_renew_dobj.getFit_off_cd2() != 0 && !fit_off_cd2_list.containsValue(fitness_renew_dobj.getFit_off_cd2())) {
                        fit_off_cd2_list.put(ServerUtil.getUserName(fitness_renew_dobj.getFit_off_cd2()), fitness_renew_dobj.getFit_off_cd2());
                    }
                } else {
                    fitness_renew_dobj = new FitnessDobj();
                    fitness_renew_dobj.setAppl_no(getAppl_details().getAppl_no());
                    fitness_renew_dobj.setRegn_no(getAppl_details().getRegn_no());
                    if (!isMultipleFitnessOfficer) {
                        fitness_renew_dobj.setFit_off_cd1(Integer.parseInt(Util.getEmpCode()));
                    }
                    if (tempFitnessDetails != null) {
                        //filling the inspection data which was done in other state
                        fitness_renew_dobj.setRemark(tempFitnessDetails.getRemark());
                        fitness_renew_dobj.setFit_chk_dt(tempFitnessDetails.getFit_chk_dt());
                        fitness_renew_dobj.setPucc_no(tempFitnessDetails.getPucc_no());
                        fitness_renew_dobj.setPucc_val(tempFitnessDetails.getPucc_val());
                        fitness_renew_dobj.setFare_mtr_no(tempFitnessDetails.getFare_mtr_no());
                        fitness_renew_dobj.setFit_result(tempFitnessDetails.getFit_result());
                        fitnessValidUptoDateChangeListener(null);
                        ArrayList newParameterlist = new ArrayList();
                        if (tempFitnessDetails.getList_parameters() != null) {
                            for (int i = 0; i < tempFitnessDetails.getList_parameters().size(); i++) {
                                if (tempFitnessDetails.getList_parameters().get(i).toString().equalsIgnoreCase("Y")) {
                                    newParameterlist.add(i + 1);
                                }
                            }
                            this.parameters.setSelectedValues(newParameterlist.toArray());
                        }
                    }
                }
                paymentRemarks = fitness_Impl.getRemarksofApplication(appl_details.getCurrent_off_cd(), appl_details.getCurrent_state_cd(), appl_details.getAppl_no());
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date fitUpto = format.parse(appl_details.getOwnerDetailsDobj().getFit_upto());

                format = new SimpleDateFormat("dd-MMM-yyyy");
                Date applDate = format.parse(appl_details.getAppl_dt());

                if (applDate != null && appl_details.getCurrent_state_cd().equalsIgnoreCase("RJ")) {
                    applDate = ServerUtil.dateRange(applDate, 0, 0, -6);
                    // there should be configuration for this and record must be save in remark mandatory feilds
                    // there should be checked so that it should less than NID days
                }

                validateFitnessFromDate(fitUpto, applDate);

                if (owner != null && appl_details.getCurrent_action_cd() == TableConstants.FITNESS_APPROVAL) {
                    autoRunCheckFeeTax = true;
                }
                if (owner.getVehType() == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                    convDobj = ConvImpl.set_Conversion_appl_db_to_dobj(null, appl_details.getRegn_no());
                    if (convDobj == null) {
                        //Check if fitness appl with conv then do conv entry first.
                        boolean fitnessWithConv = false;
                        ArrayList<Status_dobj> applStatus = ServerUtil.applicationStatusByApplNo(appl_details.getAppl_no(), appl_details.getCurrent_state_cd());
                        if (applStatus != null && !applStatus.isEmpty()) {
                            for (Status_dobj status_dobj : applStatus) {
                                if (status_dobj.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_VEH_CONVERSION) {
                                    fitnessWithConv = true;
                                }
                            }
                        }
                        if (fitnessWithConv) {
                            vahanMessages = "Please do the Conversion Entry First.";
                        }
                    } else {
                        if (convDobj.getNew_fit_dt().after(new Date())) {
                            maxDate = convDobj.getNew_fit_dt();
                        } else {
                            maxDate = new Date();
                        }
                    }
                }

                //is fitness done by SUVAS Center --added by Amitesh
                if (tmConfigFitnessDobj != null && tmConfigFitnessDobj.isFitness_done_in_suvas_center()) {
                    Date fitNid = null;
                    if (fitnessDetails != null) {
                        if (fitnessDetails.getFit_nid() != null) {
                            fitNid = fitnessDetails.getFit_nid();
                        } else {
                            Date fitValidUpto = null;
                            if (fitnessDetails.getFit_valid_to() != null) {
                                fitValidUpto = fitnessDetails.getFit_valid_to();
                            } else {
                                fitValidUpto = fitUpto;
                            }
                            fitNid = ServerUtil.dateRange(fitValidUpto, 0, 0, tmConfigurationDobj.getNid_days());
                        }
                    } else {
                        fitNid = ServerUtil.dateRange(owner.getFit_upto(), 0, 0, tmConfigurationDobj.getNid_days());
                    }

                    //if conversion of vehicle(non-transport to transport) with fitness Certificate
                    if (owner.getVehType() == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                        if (convDobj != null) {
                            fitNid = convDobj.getNew_fit_dt();
                        }
                    }

                    if (convDobj != null) {
                        if (ServerUtil.VehicleClassType(convDobj.getNew_vch_class()) != TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                            if (!fitness_Impl.isSUVASFitness(getAppl_details().getRegn_no(), applDate)) {
                                vahanMessages = "After Conversion Entry, Fitness/Inspection detail has not been captured in SUVAS center, Please visit SUVAS Center for Fitness/Inspection.";
                            }
                        }
                    } else {
                        if (!fitness_Impl.isSUVASFitness(getAppl_details().getRegn_no(), fitNid)) {
                            vahanMessages = "No Fitness/Inspection detail has been captured in SUVAS center, Please visit SUVAS Center for Fitness/Inspection.";
                        }
                    }
                }

                int fuelType = owner.getFuel();
                if (fuelType == TableConstants.VM_FUEL_CNG_TYPE
                        || fuelType == TableConstants.VM_FUEL_TYPE_PETROL_CNG
                        || fuelType == TableConstants.VM_FUEL_TYPE_LPG
                        || fuelType == TableConstants.VM_FUEL_TYPE_PETROL_LPG) {
                    retroFittingDetailsDobj = RetroFittingDetailsImpl.setCngDetails_db_to_dobj(getAppl_details().getAppl_no());
                    if (retroFittingDetailsDobj == null) {
                        retroFittingDetailsDobj = RetroFittingDetailsImpl.setCngDetails_db_to_dobj_VT(owner.getRegn_no(), owner.getState_cd(), owner.getOff_cd());
                    }
                    if (retroFittingDetailsDobj != null) {
                        renderRetroDetails = true;
                        retroFittingDetailsDobjPrev = (RetroFittingDetailsDobj) retroFittingDetailsDobj.clone();
                    }
                }
                if (fuelType == TableConstants.VM_FUEL_TYPE_ELECTRIC
                        || fuelType == TableConstants.VM_FUEL_TYPE_OTHERS
                        || TableConstants.TRAILER_VEH_CLASS.contains("," + owner.getVh_class() + ",")) {
                    skipPuccValidation = true;
                } else {
                    if (fitness_renew_dobj != null && CommonUtils.isNullOrBlank(fitness_renew_dobj.getPucc_no())) {
                        puccDobj = new InsPuccUpdateImpl().getPuccDetails(owner.getRegn_no());
                        if (puccDobj != null) {
                            fitness_renew_dobj.setPucc_no(puccDobj.getPuccNo());
                            fitness_renew_dobj.setPucc_val(puccDobj.getPuccUpto());
                            if (DateUtils.compareDates(puccDobj.getPuccUpto(), new Date()) == 2 || DateUtils.compareDates(puccDobj.getPuccUpto(), new Date()) == 0) {
                                skipPuccValidation = true;
                            }
                        }
                    }
                }

                //for allowing updating records only in same state of the vehicle registration
                if (!owner.getState_cd().equalsIgnoreCase(appl_details.getCurrent_state_cd())) {
                    isReflectiveTapeAllowed = false;
                    renderRetroDetails = false;
                    renderSpeedGoverner = false;
                    renderSpeedGov = false;
                }
            }
            if (tmConfigFitnessDobj.isDocument_upload()) {
                setRenderFileUploadPanel(true);
            }


            if (owner != null && owner.getChasi_no() != null && owner.getRegn_no() != null) {
                VehicleParameters vehParameters = FormulaUtils.fillVehicleParametersFromDobj(owner);
                if (tmConfigurationDobj != null && isCondition(replaceTagValues(tmConfigurationDobj.getVltd_condition_formula(), vehParameters), "getVltd_condition_formula")) {
                    setRenderVltdDialog(false);
                    VehicleTrackingDetailsImpl vehicleTrackingDetailsImpl = new VehicleTrackingDetailsImpl();
                    if (getAppl_details() != null && getAppl_details().getAppl_no() != null) {
                        owner.setVehicleTrackingDetailsDobj(vehicleTrackingDetailsImpl.getVehicleTrackingDetailsByAppl_no(getAppl_details().getAppl_no(), appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd()));
                    }
                    if (owner.getVehicleTrackingDetailsDobj() == null) {
                        owner.setVehicleTrackingDetailsDobj(vehicleTrackingDetailsImpl.getVltdDetails(owner.getChasi_no(), owner.getRegn_no()));
                    }
                    if (owner.getVehicleTrackingDetailsDobj() != null) {
                        setRender_vltd_details(true);
                        owner.getVehicleTrackingDetailsDobj().setAppl_no(getAppl_details().getAppl_no());
                    }
//                    if (owner.getVehicleTrackingDetailsDobj() == null) {
//                        setRenderVltdDialog(true);
//                        RequestContext rc = RequestContext.getCurrentInstance();
//                        rc.update("opvltd");
//                        rc.execute("PF('dlgvltd').show()");
//                    }
                }
                //nitin kumar/ for getting vltd info 29-01-2019
            }
        } catch (VahanException vex) {
            vahanMessages = vex.getMessage();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " [ApplNo-" + appl_details.getAppl_no() + ",RegnNo-" + appl_details.getRegn_no() + ",PurCd-" + appl_details.getPur_cd() + "] " + ex.getStackTrace()[0]);
            vahanMessages = TableConstants.SomthingWentWrong;
        }
    }

    public void feeCompare() {
        try {
            checkFeeTax = EpayImpl.getPurCdPaymentsRegisteredVehicle(appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd(), owner, appl_details.getAppl_no(), appl_details.getRegn_no(), TableConstants.VM_TRANSACTION_MAST_FIT_CERT, paymentRemarks);
            autoRunCheckFeeTax = false;
        } catch (VahanException vex) {
            vahanMessages = "Technical Error in Database due to " + vex.getMessage() + ".Please Contact to the Administrator!!!";
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " [ApplNo-" + appl_details.getAppl_no() + ",RegnNo-" + appl_details.getRegn_no() + ",PurCd-" + appl_details.getPur_cd() + "] " + ex.getStackTrace()[0]);
            vahanMessages = "Something Went Wrong, Please Contact to the System Administrator";
        }
    }

    public void fitnessValidUptoDateChangeListener(SelectEvent event) {

        Date inspDate = null;
        if (event != null) {
            inspDate = (Date) event.getObject();
        } else {
            //if details filled from inspection details which is done in other state
            if (owner.getState_cd().equalsIgnoreCase(appl_details.getCurrent_state_cd())) {
                inspDate = fitness_renew_dobj.getFit_chk_dt();
            }
        }

        Date fitValidTo = null;
        Date fitNid = null;
        Date maxUptoDate = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date fitUpto = format.parse(appl_details.getOwnerDetailsDobj().getFit_upto());
            if (fitnessDetails != null && fitnessDetails.getFit_valid_to() != null) {
                fitUpto = fitnessDetails.getFit_valid_to();
            }
            if (owner != null) {

                //for no calclutation of fitness upto for other state vehicle
                if (!owner.getState_cd().equalsIgnoreCase(appl_details.getCurrent_state_cd())) {
                    return;
                }

                VehicleParameters vehicleParameters = fillVehicleParametersFromDobj(owner);
                if (owner.getRegn_dt() != null) {
                    vehicleParameters.setVCH_AGE((int) Math.ceil(DateUtils.getDate1MinusDate2_Months(owner.getRegn_dt(), new Date()) / 12.0));
                }
                //if conversion of vehicle(non-transport to transport) with fitness Certificate
                if (owner.getVehType() == TableConstants.VM_VEHTYPE_NON_TRANSPORT) {
                    if (convDobj != null) {
                        vehicleParameters.setVH_CLASS(convDobj.getNew_vch_class());
                        vehicleParameters.setVCH_TYPE(TableConstants.VM_VEHTYPE_TRANSPORT);
                        vehicleParameters.setVCH_CATG(convDobj.getNew_vch_catg());
                        fitUpto = convDobj.getNew_fit_dt();
                    }
                }
                //Add check for validating that fitness appl is with conv appl or not     
                FitnessImpl fitnessImpl = new FitnessImpl();
                VmValidityMastDobj validityMastDobj = fitnessImpl.getVmValidityMastDobj(vehicleParameters, TableConstants.VM_TRANSACTION_MAST_FIT_CERT, appl_details.getCurrent_state_cd());

                if (validityMastDobj == null || validityMastDobj.getRe_new_value() == 0) {
                    throw new VahanException("Renewal of Fitness is not Allowed due to Condition Formula is not Matched with the State Condtions.");
                } else {

                    if (!(TableConstants.MODE_YEARLY
                            + TableConstants.MODE_HALF_YEARLY
                            + TableConstants.MODE_QUARTER
                            + TableConstants.MODE_MONTHLY).contains(validityMastDobj.getMod())) {
                        throw new VahanException("There is no valid configuration for valid fitness upto for this vehicle class, Please contact to the system administrator.");
                    }

                    if (fitUpto.before(inspDate) || (tmConfigFitnessDobj != null && tmConfigFitnessDobj.isFit_upto_from_insp_dt_even_appl_before_fit_expiry())) {//if fitness expired
                        if (validityMastDobj.getMod().equalsIgnoreCase(TableConstants.MODE_YEARLY)) {
                            fitValidTo = ServerUtil.dateRange(inspDate, validityMastDobj.getRe_new_value(), 0, -1);
                            fitNid = ServerUtil.dateRange(inspDate, validityMastDobj.getRe_new_value(), 0, tmConfigurationDobj.getNid_days());
                        } else if (validityMastDobj.getMod().equalsIgnoreCase(TableConstants.MODE_HALF_YEARLY)
                                || validityMastDobj.getMod().equalsIgnoreCase(TableConstants.MODE_QUARTER)
                                || validityMastDobj.getMod().equalsIgnoreCase(TableConstants.MODE_MONTHLY)) {
                            fitValidTo = ServerUtil.dateRange(inspDate, 0, validityMastDobj.getRe_new_value(), -1);
                            fitNid = ServerUtil.dateRange(inspDate, 0, validityMastDobj.getRe_new_value(), tmConfigurationDobj.getNid_days());
                        }
                    } else {
                        if (validityMastDobj.getMod().equalsIgnoreCase(TableConstants.MODE_YEARLY)) {
                            fitValidTo = ServerUtil.dateRange(ServerUtil.dateRange(fitUpto, 0, 0, 1), validityMastDobj.getRe_new_value(), 0, -1);
                            fitNid = ServerUtil.dateRange(ServerUtil.dateRange(fitUpto, 0, 0, 1), validityMastDobj.getRe_new_value(), 0, tmConfigurationDobj.getNid_days());
                        } else if (validityMastDobj.getMod().equalsIgnoreCase(TableConstants.MODE_HALF_YEARLY)
                                || validityMastDobj.getMod().equalsIgnoreCase(TableConstants.MODE_QUARTER)
                                || validityMastDobj.getMod().equalsIgnoreCase(TableConstants.MODE_MONTHLY)) {
                            fitValidTo = ServerUtil.dateRange(ServerUtil.dateRange(fitUpto, 0, 0, 1), 0, validityMastDobj.getRe_new_value(), -1);
                            fitNid = ServerUtil.dateRange(ServerUtil.dateRange(fitUpto, 0, 0, 1), 0, validityMastDobj.getRe_new_value(), tmConfigurationDobj.getNid_days());
                        }
                    }
                }


                if (convDobj != null && convDobj.getAppl_no() != null && convDobj.getAppl_no().equalsIgnoreCase(appl_details.getAppl_no())) {
                    //************if fitness is with conversion**********
                } else {
                    owner.setFit_dt(inspDate);//for setting inspection date for the check of permit for vehicle age calculation
                    maxUptoDate = fitnessImpl.getMaxRegnUptoDate(owner);
                    if (maxUptoDate == null) {
                        int regValidityYear = ApplicationInwardImpl.getRegValidityYear(owner);
                        Date regnDate = owner.getRegn_dt();
                        if (regValidityYear == 0) {
                            regnDate = owner.getRegn_upto();
                        }
                        maxUptoDate = ServerUtil.dateRange(regnDate, regValidityYear, 0, (regValidityYear > 0 ? -1 : 0));
                    }
                    Date maxFitValidTo = fitValidTo;
                    if (maxUptoDate != null && fitValidTo != null && maxUptoDate.compareTo(fitValidTo) <= 0) {
                        fitValidTo = maxUptoDate;
                        fitNid = ServerUtil.dateRange(fitValidTo, 0, 0, tmConfigurationDobj.getNid_days());
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Fitness Upto Can't Exceed the Age of Vehicle so New Fitness Upto is " + ServerUtil.parseDateToString(fitValidTo), "Fitness Upto Can't Exceed the Age of Vehicle so New Fitness Upto is " + ServerUtil.parseDateToString(fitValidTo)));
                    }

                    if (fitValidTo != null && maxUptoDate != null && DateUtils.compareDates(maxUptoDate, fitValidTo) <= 1) {
                        this.isDisableFitnessUpto = false;
                        fitNid = null;
                        fitUptoMaxDate = maxFitValidTo;
                        fitUptoMinDate = inspDate;
                        if (this.fitness_renew_dobj.getFit_valid_to() == null) {
                            fitValidTo = null;
                        }
                    } else if ("RJ".equalsIgnoreCase(Util.getUserStateCode()) && fitValidTo != null && maxUptoDate != null && (DateUtils.getDate1MinusDate2_Months(owner.getRegn_dt(), new Date()) > 120) && (DateUtils.getDate1MinusDate2_Months(owner.getRegn_dt(), new Date()) < 180)) {
                        this.isDisableFitnessUpto = false;
                        fitNid = null;
                        fitUptoMaxDate = maxFitValidTo;
                        fitUptoMinDate = inspDate;
                        if (this.fitness_renew_dobj.getFit_valid_to() == null) {
                            fitValidTo = null;
                        }
                    }
                }

                this.fitness_renew_dobj.setFit_valid_to(fitValidTo);
                this.fitness_renew_dobj.setFit_nid(fitNid);
            }
        } catch (VahanException vmex) {
            vahanMessages = vmex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vmex.getMessage(), vmex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public List<ComparisonBean> addToComapreChangesList(ArrayList<ComparisonBean> compBeanListPrev) throws VahanException {

        List<ComparisonBean> list = compareChanges();

        if (compBeanListPrev == null) {
            compBeanListPrev = new ArrayList<>();
        }
        if (list.size() > 0) {
            compBeanListPrev.addAll(list);
        }

        return compBeanListPrev;
    }

    /**
     * @return the fitness_renew_dobj
     */
    public FitnessDobj getFitness_renew_dobj() {
        return fitness_renew_dobj;
    }

    /**
     * @param fitness_renew_dobj the fitness_renew_dobj to set
     */
    public void setFitness_renew_dobj(FitnessDobj fitness_renew_dobj) {
        this.fitness_renew_dobj = fitness_renew_dobj;
        ArrayList list = fitness_renew_dobj.getList_parameters();
        ArrayList newlist = new ArrayList();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).toString().equals("Y")) {
                    newlist.add(i + 1);
                }
            }

            this.parameters.setSelectedValues(newlist.toArray());
        }
    }

    /**
     * @return the compBeanList
     */
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    /**
     * @param compBeanList the compBeanList to set
     */
    public void setCompBeanList(ArrayList<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    /**
     * @return the fitness_renew_dobj_prv
     */
    public FitnessDobj getFitness_renew_dobj_prv() {
        return fitness_renew_dobj_prv;
    }

    /**
     * @param fitness_renew_dobj_prv the fitness_renew_dobj_prv to set
     */
    public void setFitness_renew_dobj_prv(FitnessDobj dobj_prv) {
        this.fitness_renew_dobj_prv = dobj_prv;


    }

    /**
     * @return the parameters_list
     */
    public ArrayList getParameters_list() {
        return parameters_list;
    }

    /**
     * @param parameters_list the parameters_list to set
     */
    public void setParameters_list(ArrayList parameters_list) {
        this.parameters_list = parameters_list;
    }

    /**
     * @return the parameters
     */
    public SelectManyCheckbox getParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(SelectManyCheckbox parameters) {
        this.parameters = parameters;
    }

    @Override
    public String save() {
        String return_location = "";
        try {

            //check for logged in user is fitness officer who is doing inspection
            if (getAppl_details().getCurrent_action_cd() == TableConstants.FITNESS_APPROVAL) {
                if (!isMultipleFitnessOfficer && tmConfigFitnessDobj != null
                        && tmConfigFitnessDobj.isLogged_in_user_is_fitness_officer()
                        && fitness_renew_dobj != null && fitness_renew_dobj.getFit_off_cd1() != 0
                        && !getAppl_details().getCurrentEmpCd().equalsIgnoreCase(String.valueOf(fitness_renew_dobj.getFit_off_cd1()))) {
                    throw new VahanException("Fitness Officer and Logged in User Must be Same Person.Please Select Valid Fitness Officer Name");
                }
            }

            ArrayList allYesNoList = new ArrayList();
            for (int i = 0; i < getParameters_list().size(); i++) {
                allYesNoList.add("N");
            }
            for (Object selectedItemCode : getParameters().getSelectedValues()) {
                for (int i = 0; i < getParameters_list().size(); i++) {
                    String selectedValue = (String) ((SelectItem) getParameters_list().get(i)).getValue();
                    if (selectedValue.equalsIgnoreCase((String) selectedItemCode)) {
                        allYesNoList.set(i, "Y");
                        break;
                    }
                }
            }//update yes no list            
            fitness_renew_dobj.setList_parameters(allYesNoList);
            List<ComparisonBean> compareChanges = compareChanges();
            fitness_renew_dobj.setChasi_no(appl_details.getChasi_no());
            //set time part of the fitness check date...
            if (getAppl_details().getCurrent_action_cd() == TableConstants.FITNESS_APPROVAL) {
                fitness_renew_dobj.setFit_chk_tm(getFitness_renew_dobj().getFit_chk_tm());
            } else {
                fitness_renew_dobj.setFit_chk_tm(getFitness_renew_dobj().getFit_chk_dt().toString().substring(11, 19));
            }

            //save only when data is really changed by user
            if (!compareChanges.isEmpty()
                    || fitness_renew_dobj_prv == null
                    || (speedGovernorDobjPrev == null && renderSpeedGov)
                    || (reflectiveTapeDobjPrev == null && renderReflectiveTape)
                    || speedGovernorSrvcData
                    || reflectiveTapeSrvcData) {
                FitnessImpl fit_impl = new FitnessImpl();
                fit_impl.makeChange_fitness_renewal(fitness_renew_dobj, owner.getSpeedGovernorDobj(),
                        renderSpeedGov, owner.getReflectiveTapeDobj(), renderReflectiveTape,
                        retroFittingDetailsDobj, retroFittingDetailsDobjPrev, renderRetroDetails,
                        ComparisonBeanImpl.changedDataContents(compareChanges),
                        appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd(), owner.getVehicleTrackingDetailsDobj());
            }

            if (speedGovernorDobjPrev != null && !renderSpeedGov) {
                FitnessImpl fit_impl = new FitnessImpl();
                fit_impl.deleteFromVaSpeedGovernor(fitness_renew_dobj.getAppl_no());
            }

            if (reflectiveTapeDobjPrev != null && !renderReflectiveTape) {
                FitnessImpl fit_impl = new FitnessImpl();
                fit_impl.deleteFromVaReflectiveTape(fitness_renew_dobj.getAppl_no(), Util.getEmpCode());
            }

            return_location = "seatwork";

        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-Could Not Save Due to Technical Error in Database", "Error-Could Not Save Due to Technical Error in Database"));
        }
        return return_location;
    }

    @Override
    public List<ComparisonBean> compareChanges() {

        if (fitness_renew_dobj_prv == null) {
            return getCompBeanList();
        }
        getCompBeanList().clear();
        Compare("Check Fit Date", fitness_renew_dobj_prv.getFit_chk_dt(), fitness_renew_dobj.getFit_chk_dt(), getCompBeanList());
        Compare("Fitness check time", fitness_renew_dobj_prv.getFit_chk_tm(), fitness_renew_dobj.getFit_chk_tm(), getCompBeanList());
        Compare("Pucc No", fitness_renew_dobj_prv.getPucc_no(), fitness_renew_dobj.getPucc_no(), getCompBeanList());
        Compare("Pucc Validity", fitness_renew_dobj_prv.getPucc_val(), fitness_renew_dobj.getPucc_val(), getCompBeanList());
        Compare("Inspected By officer1", fitness_renew_dobj_prv.getFit_off_cd1(), fitness_renew_dobj.getFit_off_cd1(), getCompBeanList());
        Compare("Inspected By officer2", fitness_renew_dobj_prv.getFit_off_cd2(), fitness_renew_dobj.getFit_off_cd2(), getCompBeanList());
        Compare("Fitness Result", fitness_renew_dobj_prv.getFit_result(), fitness_renew_dobj.getFit_result(), getCompBeanList());
        Compare("Fare Meter No", fitness_renew_dobj_prv.getFare_mtr_no(), fitness_renew_dobj.getFare_mtr_no(), getCompBeanList());
        Compare("Remark", fitness_renew_dobj_prv.getRemark(), fitness_renew_dobj.getRemark(), getCompBeanList());
        Compare("Fitness Valid Upto", fitness_renew_dobj_prv.getFit_valid_to(), fitness_renew_dobj.getFit_valid_to(), getCompBeanList());
        if (fitness_renew_dobj.getFit_nid() != null) {
            Compare("NID", fitness_renew_dobj_prv.getFit_nid(), fitness_renew_dobj.getFit_nid(), getCompBeanList());
        }
        ArrayList fitCheckList = fitness_renew_dobj_prv.getList_parameters();
        ArrayList fitCheckListNew = fitness_renew_dobj.getList_parameters();
        String parmValue = null;
        String parmLabel = null;
        for (int i = 0; i < fitCheckList.size(); i++) {
            parmValue = (String) ((SelectItem) getParameters_list().get(i)).getValue();
            parmLabel = (String) ((SelectItem) getParameters_list().get(i)).getLabel();
            Compare(parmLabel, fitCheckList.get(i).toString(), fitCheckListNew.get(i).toString(), getCompBeanList());
        }

        if (renderSpeedGov && owner != null && owner.getSpeedGovernorDobj() != null && speedGovernorDobjPrev != null) {
            Compare("Speed Gov. No", speedGovernorDobjPrev.getSg_no(), owner.getSpeedGovernorDobj().getSg_no(), getCompBeanList());
            Compare("Speed Gov. Fitted On", speedGovernorDobjPrev.getSg_fitted_on(), owner.getSpeedGovernorDobj().getSg_fitted_on(), getCompBeanList());
            Compare("Speed Gov. Fitted at", speedGovernorDobjPrev.getSg_fitted_at(), owner.getSpeedGovernorDobj().getSg_fitted_at(), getCompBeanList());
            Compare("Speed Gov. Type", speedGovernorDobjPrev.getSgGovType(), owner.getSpeedGovernorDobj().getSgGovType(), getCompBeanList());
            Compare("Speed Gov. Type Approval No", speedGovernorDobjPrev.getSgTypeApprovalNo(), owner.getSpeedGovernorDobj().getSgTypeApprovalNo(), getCompBeanList());
            Compare("Speed Gov. Test Report No", speedGovernorDobjPrev.getSgTestReportNo(), owner.getSpeedGovernorDobj().getSgTestReportNo(), getCompBeanList());
            Compare("Speed Gov. Fit Cert No", speedGovernorDobjPrev.getSgFitmentCerticateNo(), owner.getSpeedGovernorDobj().getSgFitmentCerticateNo(), getCompBeanList());
        }

        if (renderReflectiveTape && owner != null && owner.getReflectiveTapeDobj() != null && reflectiveTapeDobjPrev != null) {
            Compare("Ref Cert No", reflectiveTapeDobjPrev.getCertificateNo(), owner.getReflectiveTapeDobj().getCertificateNo(), getCompBeanList());
            Compare("Ref Fitment Date", reflectiveTapeDobjPrev.getFitmentDate(), owner.getReflectiveTapeDobj().getFitmentDate(), getCompBeanList());
            Compare("Ref Manu Name", reflectiveTapeDobjPrev.getManuName(), owner.getReflectiveTapeDobj().getManuName(), getCompBeanList());
        }

        if (retroFittingDetailsDobj != null && retroFittingDetailsDobjPrev != null) {
            Compare("Hydro Test Date", retroFittingDetailsDobjPrev.getHydro_dt(), retroFittingDetailsDobj.getHydro_dt(), getCompBeanList());
        }


        return getCompBeanList();
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        String msg = null;
        try {

            //check for logged in user is fitness officer who is doing inspection
            if (getAppl_details().getCurrent_action_cd() == TableConstants.FITNESS_APPROVAL) {
                if (!isMultipleFitnessOfficer && tmConfigFitnessDobj != null
                        && tmConfigFitnessDobj.isLogged_in_user_is_fitness_officer()
                        && fitness_renew_dobj != null && fitness_renew_dobj.getFit_off_cd1() != 0
                        && !getAppl_details().getCurrentEmpCd().equalsIgnoreCase(String.valueOf(fitness_renew_dobj.getFit_off_cd1()))) {
                    throw new VahanException("Fitness Officer and Logged in User Must be Same Person.Please Select Valid Fitness Officer Name");
                }
            }

            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setRegn_no(appl_details.getRegn_no());
            if (Util.getEmpCode() != null) {
                status.setEmp_cd(Long.parseLong(Util.getEmpCode()));
            }
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());
            status.setVehicleParameters(appl_details.getVehicleParameters());
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                    || getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_REVERT)
                    || getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                ArrayList allYesNoList = new ArrayList();
                for (int i = 0; i < getParameters_list().size(); i++) {
                    allYesNoList.add("N");
                }
                for (Object selectedItemCode : getParameters().getSelectedValues()) {
                    for (int i = 0; i < getParameters_list().size(); i++) {
                        String selectedValue = (String) ((SelectItem) getParameters_list().get(i)).getValue();
                        if (selectedValue.equalsIgnoreCase((String) selectedItemCode)) {
                            allYesNoList.set(i, "Y");
                            break;
                        }
                    }
                }

                for (int i = 0; i < allYesNoList.size(); i++) {
                    if (allYesNoList.get(i).equals("N") && fitness_renew_dobj.getFit_result().equalsIgnoreCase(TableConstants.FitnessResultPass)) {
                        /*
                         * 14th position is faremeter field and is mandatory only if fare_mtr_no is not null or non empty
                         */
                        if (i == 13 && (fitness_renew_dobj.getFare_mtr_no() == null || fitness_renew_dobj.getFare_mtr_no().isEmpty())) {
                            continue;
                        }
                        if (i == 22) {//23rd position is others feild and it is optional
                            continue;
                        }

                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "All Parameters in Vehicle Detail Should be Selected to Proceed Further if Fitness/Inspection is Passed", "All Parameters in Vehicle Detail Should be Selected to Proceed Further if Fitness/Inspection is Passed"));
                        return "";
                    }
                }// update yes no list
                fitness_renew_dobj.setList_parameters(allYesNoList);

                if (getAppl_details().getCurrent_action_cd() == TableConstants.FITNESS_APPROVAL) {// Set the time part from the selected date.
                    fitness_renew_dobj.setFit_chk_tm(getFitness_renew_dobj().getFit_chk_tm());
                } else {
                    fitness_renew_dobj.setFit_chk_tm(getFitness_renew_dobj().getFit_chk_dt().toString().substring(11, 19));
                }
                fitness_renew_dobj.setChasi_no(appl_details.getChasi_no());
                //DOCUMENT UPLOAD CHECK
                if (tmConfigFitnessDobj.isDocument_upload()) {
                    List<VTDocumentModel> docUploadDobjList = DmsDocCheckUtils.getUploadedDocumentList(appl_details.getAppl_no());
                    if (docUploadDobjList != null && docUploadDobjList.size() > 0) {
                        for (VTDocumentModel dobj : docUploadDobjList) {
                            if (appl_details.getCurrent_action_cd() == TableConstants.FITNESS_INSPECTION_CERTIFICATE_VERIFICATION && !dobj.isDoc_verified()) {
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Document Verification Pending",
                                        "Document Verification Pending"));
                                return "";
                            } else if (appl_details.getCurrent_action_cd() == TableConstants.FITNESS_APPROVAL && !dobj.isDoc_approved()) {
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Document Approval Pending ",
                                        "Document Approval Pending "));
                                return "";
                            }
                        }
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Document Upload Pending ",
                                "Document Upload Pending "));
                        return "";
                    }
                }

                if (fitness_renew_dobj != null && !skipPuccValidation && fitness_renew_dobj.getPucc_val() != null && (DateUtils.compareDates(fitness_renew_dobj.getPucc_val(), new Date()) == 1)) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "PUCC validity expired. Please update PUCC details.",
                            "PUCC validity expired. Please update PUCC details."));
                    return "";
                }
                FitnessImpl fit_impl = new FitnessImpl();
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE) && this.appl_details.isDocVerifyForFacelessAppl()) {
                    String[] notVerifiedDocDetails = ServerUtil.verifyDocUploaded(appl_details.getAppl_no(), getAppl_details().getCurrent_action_cd());
                    if (notVerifiedDocDetails != null) {
                        appl_details.setNotVerifiedDocdetails(notVerifiedDocDetails[1]);
                        throw new VahanException(notVerifiedDocDetails[0]);
                    }
                }
                fit_impl.update_Fitness_Status(fitness_renew_dobj, fitness_renew_dobj_prv, owner.getSpeedGovernorDobj(), speedGovernorDobjPrev, renderSpeedGov, status, ComparisonBeanImpl.changedDataContents(compareChanges()), tmConfigurationDobj, owner.getReflectiveTapeDobj(), reflectiveTapeDobjPrev, isRenderReflectiveTape(), retroFittingDetailsDobj, retroFittingDetailsDobjPrev, renderRetroDetails, appl_details, checkFeeTax, owner.getVehicleTrackingDetailsDobj());
                if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    ServerUtil.sendSmsInFacelessService(appl_details.getAppl_no(), Util.getUserStateCode(), Util.getUserSeatOffCode(), appl_details.getRegn_no(), appl_details.getNotVerifiedDocdetails());
                    return_location = disapprovalPrint();
                } else {
                    return_location = "seatwork";
                }
            }

        } catch (VahanException ve) {
            msg = ve.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            msg = "Error-File Could Not Save and Move Due to Technical Error in Database";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
        }

        return return_location;
    }

    public void speedGovListener(AjaxBehaviorEvent event) {
        FitnessImpl fitness_Impl = new FitnessImpl();
        try {
            if (renderSpeedGov && owner.getSpeedGovernorDobj() == null) {
                renderSpeedGov = true;
                SpeedGovernorDobj sgDobj = new SpeedGovernorDobj();
                sgDobj = fitness_Impl.getSLDInfo(getAppl_details().getAppl_no(), getAppl_details().getRegn_no(), owner.getOff_cd(), owner.getState_cd());
                if (sgDobj == null) {
                    sgDobj = new SpeedGovernorDobj();
                    sgDobj.setAppl_no(Util.getSelectedSeat().getAppl_no());
                    sgDobj.setRegn_no(Util.getSelectedSeat().getRegn_no());
                    sgDobj.setState_cd(Util.getUserStateCode());
                    sgDobj.setOff_cd(Util.getSelectedSeat().getOff_cd());
                } else {
                    sgDobj.setIsDisable_sg_no(true);
                    sgDobj.setIsDisable_sg_fitted_on(true);
                    sgDobj.setIsDisable_sg_fitted_at(true);
                    sgDobj.setIsDisable_sgTypeApprovalNo(true);
                    sgDobj.setIsDisable_sgTestReportNo(true);
                    sgDobj.setIsDisable_sgGovType(true);
                    sgDobj.setIsDisable_sgFitmentCerticateNo(true);
                    this.setIsDisableSpeedGovernerChkBox(true);
                }
                owner.setSpeedGovernorDobj(sgDobj);
            } else if (renderSpeedGov) {
                renderSpeedGov = true;
            } else {
                renderSpeedGov = false;
            }

        } catch (Exception ex) {
            LOGGER.error(ex.toString() + ex.getStackTrace()[0]);
            vahanMessages = TableConstants.SomthingWentWrong;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vahanMessages, vahanMessages));
        }
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return prevChangedDataList;
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList;
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void validateFitnessFromDate(Date fitUpto, Date applDate) {

        if (fitUpto.before(applDate)) {
            this.maxDate = new Date();
            this.minDate = applDate;
        } else {
            this.maxDate = new Date();//ServerUtil.dateRange(fitUpto, 0, 0, 1);
            this.minDate = applDate;//ServerUtil.dateRange(fitUpto, 0, 0, 1);
        }

        //minimum date of inspection is inspection date of other state inspection
        if (tempFitnessDetails != null && tempFitnessDetails.getFit_chk_dt() != null) {
            minDate = tempFitnessDetails.getFit_chk_dt();
        }
    }

    public void validateFitnessOfficer(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value != null && fitness_renew_dobj.getFit_off_cd1() == Integer.parseInt(value.toString())) {
            FacesMessage msg = new FacesMessage("Both Fitness Officer Can't be Same.Please Choose Different Fitness Officer");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public void reflectiveTapeListener(AjaxBehaviorEvent event) {
        ReflectiveTapeDobj refTape = null;
        if (renderReflectiveTape) {
            setRenderReflectiveTape(true);
            refTape = new ReflectiveTapeDobj();
            refTape.setApplNo(appl_details.getAppl_no());
            refTape.setRegn_no(appl_details.getRegn_no());
            refTape.setStateCd(appl_details.getCurrent_state_cd());
            refTape.setOffCd(appl_details.getCurrent_off_cd());
            owner.setReflectiveTapeDobj(refTape);
        } else {
            setRenderReflectiveTape(false);
            owner.setReflectiveTapeDobj(refTape);
        }
    }

    public void viewUploadedDocumentsForFitness() {
        String dmsURL = "";
        try {
            if (appl_details.getPur_cd() == TableConstants.VM_TRANSACTION_MAST_FIT_CERT) {
                List<VTDocumentModel> documentUploadStatus = DmsDocCheckUtils.getUploadedDocumentList(appl_details.getAppl_no());
                if (documentUploadStatus != null && !documentUploadStatus.isEmpty()) {
                    switch (appl_details.getCurrent_action_cd()) {
                        case TableConstants.FITNESS_INSPECTION_CERTIFICATE_ENTRY:
                            dmsURL = ServerUtil.getVahanPgiUrl(TableConstants.DMS_CON_VER);
                            dmsURL = dmsURL.replace("ApplNo", appl_details.getAppl_no());
                            dmsURL = dmsURL.replace("ApplStatus", TableConstants.DOCUMENT_MODIFY_STATUS);
                            break;
                        case TableConstants.FITNESS_INSPECTION_CERTIFICATE_VERIFICATION:
                            dmsURL = ServerUtil.getVahanPgiUrl(TableConstants.DMS_CON_VER);
                            dmsURL = dmsURL.replace("ApplNo", appl_details.getAppl_no());
                            dmsURL = dmsURL.replace("ApplStatus", TableConstants.DOCUMENT_RTO_VERIFICATION);
                            break;
                        case TableConstants.FITNESS_APPROVAL:
                            dmsURL = ServerUtil.getVahanPgiUrl(TableConstants.DMS_CON_VER);
                            dmsURL = dmsURL.replace("ApplNo", appl_details.getAppl_no());
                            dmsURL = dmsURL.replace("ApplStatus", TableConstants.DOCUMENT_RTO_APPROVAL);
                            break;
                    }
                } else {
                    if (appl_details.getCurrent_action_cd() == TableConstants.FITNESS_INSPECTION_CERTIFICATE_ENTRY) {
                        dmsURL = ServerUtil.getVahanPgiUrl(TableConstants.DMS_CON);
                        dmsURL = dmsURL.replace("ApplNo", appl_details.getAppl_no());
                    }
                }

            }
            if (dmsURL.isEmpty()) {
                throw new VahanException("Problem in view documents.");
            }
            dmsURL = dmsURL + TableConstants.SECURITY_KEY;
            setDmsFileUploadUrl(dmsURL);
            PrimeFaces.current().ajax().update("dmsFileUploadReg_panel");
            PrimeFaces.current().executeScript("PF('dmsfileUploadedReg').show();");

        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.setFacesMessage(ex.getMessage(), ex.getMessage(), JSFUtils.WARN);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    /**
     * @return the fit_off_cd1_list
     */
    public Map<String, Integer> getFit_off_cd1_list() {
        return fit_off_cd1_list;
    }

    /**
     * @param fit_off_cd1_list the fit_off_cd1_list to set
     */
    public void setFit_off_cd1_list(Map<String, Integer> fit_off_cd1_list) {
        this.fit_off_cd1_list = fit_off_cd1_list;
    }

    /**
     * @return the maxDate
     */
    public Date getMaxDate() {
        return maxDate;
    }

    /**
     * @param maxDate the maxDate to set
     */
    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    /**
     * @return the owner
     */
    public Owner_dobj getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(Owner_dobj owner) {
        this.owner = owner;
    }

    /**
     * @return the minDate
     */
    public Date getMinDate() {
        return minDate;
    }

    /**
     * @param minDate the minDate to set
     */
    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    /**
     * @return the fit_off_cd2_list
     */
    public Map<String, Integer> getFit_off_cd2_list() {
        return fit_off_cd2_list;
    }

    /**
     * @param fit_off_cd2_list the fit_off_cd2_list to set
     */
    public void setFit_off_cd2_list(Map<String, Integer> fit_off_cd2_list) {
        this.fit_off_cd2_list = fit_off_cd2_list;
    }

    /**
     * @return the isMultipleFitnessOfficer
     */
    public boolean isIsMultipleFitnessOfficer() {
        return isMultipleFitnessOfficer;
    }

    /**
     * @param isMultipleFitnessOfficer the isMultipleFitnessOfficer to set
     */
    public void setIsMultipleFitnessOfficer(boolean isMultipleFitnessOfficer) {
        this.isMultipleFitnessOfficer = isMultipleFitnessOfficer;
    }

    /**
     * @return the tmConfigurationDobj
     */
    public TmConfigurationDobj getTmConfigurationDobj() {
        return tmConfigurationDobj;
    }

    /**
     * @param tmConfigurationDobj the tmConfigurationDobj to set
     */
    public void setTmConfigurationDobj(TmConfigurationDobj tmConfigurationDobj) {
        this.tmConfigurationDobj = tmConfigurationDobj;
    }

    /**
     * @return the fitnessDetails
     */
    public FitnessDobj getFitnessDetails() {
        return fitnessDetails;
    }

    /**
     * @param fitnessDetails the fitnessDetails to set
     */
    public void setFitnessDetails(FitnessDobj fitnessDetails) {
        this.fitnessDetails = fitnessDetails;
    }

    /**
     * @return the vahanMessages
     */
    public String getVahanMessages() {
        return vahanMessages;
    }

    /**
     * @param vahanMessages the vahanMessages to set
     */
    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }

    /**
     * @return the checkFeeTax
     */
    public EpayDobj getCheckFeeTax() {
        return checkFeeTax;
    }

    /**
     * @param checkFeeTax the checkFeeTax to set
     */
    public void setCheckFeeTax(EpayDobj checkFeeTax) {
        this.checkFeeTax = checkFeeTax;
    }

    /**
     * @return the autoRunCheckFeeTax
     */
    public boolean isAutoRunCheckFeeTax() {
        return autoRunCheckFeeTax;
    }

    /**
     * @param autoRunCheckFeeTax the autoRunCheckFeeTax to set
     */
    public void setAutoRunCheckFeeTax(boolean autoRunCheckFeeTax) {
        this.autoRunCheckFeeTax = autoRunCheckFeeTax;
    }

    /**
     * @return the ownerDetail
     */
    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    /**
     * @param ownerDetail the ownerDetail to set
     */
    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }

    /**
     * @return the isDisableFitnessUpto
     */
    public boolean isIsDisableFitnessUpto() {
        return isDisableFitnessUpto;
    }

    /**
     * @param isDisableFitnessUpto the isDisableFitnessUpto to set
     */
    public void setIsDisableFitnessUpto(boolean isDisableFitnessUpto) {
        this.isDisableFitnessUpto = isDisableFitnessUpto;
    }

    /**
     * @return the fitUptoMaxDate
     */
    public Date getFitUptoMaxDate() {
        return fitUptoMaxDate;
    }

    /**
     * @param fitUptoMaxDate the fitUptoMaxDate to set
     */
    public void setFitUptoMaxDate(Date fitUptoMaxDate) {
        this.fitUptoMaxDate = fitUptoMaxDate;
    }

    /**
     * @return the fitUptoMinDate
     */
    public Date getFitUptoMinDate() {
        return fitUptoMinDate;
    }

    /**
     * @param fitUptoMinDate the fitUptoMinDate to set
     */
    public void setFitUptoMinDate(Date fitUptoMinDate) {
        this.fitUptoMinDate = fitUptoMinDate;
    }

    /**
     * @return the speedGovernorDobjPrev
     */
    public SpeedGovernorDobj getSpeedGovernorDobjPrev() {
        return speedGovernorDobjPrev;
    }

    /**
     * @param speedGovernorDobjPrev the speedGovernorDobjPrev to set
     */
    public void setSpeedGovernorDobjPrev(SpeedGovernorDobj speedGovernorDobjPrev) {
        this.speedGovernorDobjPrev = speedGovernorDobjPrev;
    }

    /**
     * @return the renderSpeedGov
     */
    public boolean isRenderSpeedGov() {
        return renderSpeedGov;
    }

    /**
     * @param renderSpeedGov the renderSpeedGov to set
     */
    public void setRenderSpeedGov(boolean renderSpeedGov) {
        this.renderSpeedGov = renderSpeedGov;
    }

    /**
     * @return the tmConfigFitnessDobj
     */
    public TmConfigurationFitnessDobj getTmConfigFitnessDobj() {
        return tmConfigFitnessDobj;
    }

    /**
     * @param tmConfigFitnessDobj the tmConfigFitnessDobj to set
     */
    public void setTmConfigFitnessDobj(TmConfigurationFitnessDobj tmConfigFitnessDobj) {
        this.tmConfigFitnessDobj = tmConfigFitnessDobj;
    }

    /**
     * @return the disableFitnessOfficer1
     */
    public boolean isDisableFitnessOfficer1() {
        return disableFitnessOfficer1;
    }

    /**
     * @param disableFitnessOfficer1 the disableFitnessOfficer1 to set
     */
    public void setDisableFitnessOfficer1(boolean disableFitnessOfficer1) {
        this.disableFitnessOfficer1 = disableFitnessOfficer1;
    }

    /**
     * @return the listSpeedGovTypes
     */
    public List getListSpeedGovTypes() {
        return listSpeedGovTypes;
    }

    /**
     * @param listSpeedGovTypes the listSpeedGovTypes to set
     */
    public void setListSpeedGovTypes(List listSpeedGovTypes) {
        this.listSpeedGovTypes = listSpeedGovTypes;
    }

    /**
     * @return the renderReflectiveTape
     */
    public boolean isRenderReflectiveTape() {
        return renderReflectiveTape;
    }

    /**
     * @param renderReflectiveTape the renderReflectiveTape to set
     */
    public void setRenderReflectiveTape(boolean renderReflectiveTape) {
        this.renderReflectiveTape = renderReflectiveTape;
    }

    /**
     * @return the reflectiveTapeDobjPrev
     */
    public ReflectiveTapeDobj getReflectiveTapeDobjPrev() {
        return reflectiveTapeDobjPrev;
    }

    /**
     * @param reflectiveTapeDobjPrev the reflectiveTapeDobjPrev to set
     */
    public void setReflectiveTapeDobjPrev(ReflectiveTapeDobj reflectiveTapeDobjPrev) {
        this.reflectiveTapeDobjPrev = reflectiveTapeDobjPrev;
    }

    /**
     * @return the isReflectiveTapeAllowed
     */
    public boolean isIsReflectiveTapeAllowed() {
        return isReflectiveTapeAllowed;
    }

    /**
     * @param isReflectiveTapeAllowed the isReflectiveTapeAllowed to set
     */
    public void setIsReflectiveTapeAllowed(boolean isReflectiveTapeAllowed) {
        this.isReflectiveTapeAllowed = isReflectiveTapeAllowed;
    }

    /**
     * @return the retroFittingDetailsDobj
     */
    public RetroFittingDetailsDobj getRetroFittingDetailsDobj() {
        return retroFittingDetailsDobj;
    }

    /**
     * @param retroFittingDetailsDobj the retroFittingDetailsDobj to set
     */
    public void setRetroFittingDetailsDobj(RetroFittingDetailsDobj retroFittingDetailsDobj) {
        this.retroFittingDetailsDobj = retroFittingDetailsDobj;
    }

    /**
     * @return the renderRetroDetails
     */
    public boolean isRenderRetroDetails() {
        return renderRetroDetails;
    }

    /**
     * @param renderRetroDetails the renderRetroDetails to set
     */
    public void setRenderRetroDetails(boolean renderRetroDetails) {
        this.renderRetroDetails = renderRetroDetails;
    }

    /**
     * @return the retroFittingDetailsDobjPrev
     */
    public RetroFittingDetailsDobj getRetroFittingDetailsDobjPrev() {
        return retroFittingDetailsDobjPrev;
    }

    /**
     * @param retroFittingDetailsDobjPrev the retroFittingDetailsDobjPrev to set
     */
    public void setRetroFittingDetailsDobjPrev(RetroFittingDetailsDobj retroFittingDetailsDobjPrev) {
        this.retroFittingDetailsDobjPrev = retroFittingDetailsDobjPrev;
    }

    /**
     * @return the renderSpeedGoverner
     */
    public boolean isRenderSpeedGoverner() {
        return renderSpeedGoverner;
    }

    /**
     * @param renderSpeedGoverner the renderSpeedGoverner to set
     */
    public void setRenderSpeedGoverner(boolean renderSpeedGoverner) {
        this.renderSpeedGoverner = renderSpeedGoverner;
    }

    /**
     * @return the tempFitnessDetails
     */
    public FitnessDobj getTempFitnessDetails() {
        return tempFitnessDetails;
    }

    /**
     * @param tempFitnessDetails the tempFitnessDetails to set
     */
    public void setTempFitnessDetails(FitnessDobj tempFitnessDetails) {
        this.tempFitnessDetails = tempFitnessDetails;
    }

    /**
     * @return the isDisableReflectiveTapeChkBox
     */
    public boolean isIsDisableReflectiveTapeChkBox() {
        return isDisableReflectiveTapeChkBox;
    }

    /**
     * @param isDisableReflectiveTapeChkBox the isDisableReflectiveTapeChkBox to
     * set
     */
    public void setIsDisableReflectiveTapeChkBox(boolean isDisableReflectiveTapeChkBox) {
        this.isDisableReflectiveTapeChkBox = isDisableReflectiveTapeChkBox;
    }

    /**
     * @return the isDisableSpeedGovernerChkBox
     */
    public boolean isIsDisableSpeedGovernerChkBox() {
        return isDisableSpeedGovernerChkBox;
    }

    /**
     * @param isDisableSpeedGovernerChkBox the isDisableSpeedGovernerChkBox to
     * set
     */
    public void setIsDisableSpeedGovernerChkBox(boolean isDisableSpeedGovernerChkBox) {
        this.isDisableSpeedGovernerChkBox = isDisableSpeedGovernerChkBox;
    }

    /**
     * @return the skipPuccValidation
     */
    public boolean isSkipPuccValidation() {
        return skipPuccValidation;
    }

    /**
     * @param skipPuccValidation the skipPuccValidation to set
     */
    public void setSkipPuccValidation(boolean skipPuccValidation) {
        this.skipPuccValidation = skipPuccValidation;
    }

    public boolean isRenderFileUploadPanel() {
        return renderFileUploadPanel;
    }

    public void setRenderFileUploadPanel(boolean renderFileUploadPanel) {
        this.renderFileUploadPanel = renderFileUploadPanel;
    }

    public String getDmsFileUploadUrl() {
        return dmsFileUploadUrl;
    }

    public void setDmsFileUploadUrl(String dmsFileUploadUrl) {
        this.dmsFileUploadUrl = dmsFileUploadUrl;
    }

    public boolean isRender_vltd_details() {
        return render_vltd_details;
    }

    public void setRender_vltd_details(boolean render_vltd_details) {
        this.render_vltd_details = render_vltd_details;
    }

    public boolean isRenderVltdDialog() {
        return renderVltdDialog;
    }

    public void setRenderVltdDialog(boolean renderVltdDialog) {
        this.renderVltdDialog = renderVltdDialog;
    }

    public String getPaymentRemarks() {
        return paymentRemarks;
    }

    public void setPaymentRemarks(String paymentRemarks) {
        this.paymentRemarks = paymentRemarks;
    }

    /**
     * @return the renderSLDDialog
     */
    public boolean isRenderSLDDialog() {
        return renderSLDDialog;
    }

    /**
     * @param renderSLDDialog the renderSLDDialog to set
     */
    public void setRenderSLDDialog(boolean renderSLDDialog) {
        this.renderSLDDialog = renderSLDDialog;
    }
}
