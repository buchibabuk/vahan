/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.NocDobj;
import nic.vahan.form.dobj.OtherStateVehDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.impl.AxleImpl;
import nic.vahan.form.impl.BlackListedVehicleImpl;
import nic.vahan.form.impl.DocumentUploadImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.NewVehicleFitnessImpl;
import nic.vahan.form.impl.NocImpl;
import nic.vahan.form.impl.RetroFittingDetailsImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "preregfit")
@ViewScoped
public class NewVehicleFitnessBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(NewVehicleFitnessBean.class);
    @ManagedProperty(value = "#{owner_bean}")
    private OwnerBean owner_bean;
    private String eng_no;
    private String chassis_no;
    private boolean renderVehicleDetails;
    private SessionVariables sessionVariables = null;
    private String vahanMessages = null;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<ComparisonBean> prevChangedDataList;
    private boolean disableChassisNo;
    private String appl_no_massage;
    List<OwnerDetailsDobj> tempRegnDetailsList = null;
    private Date minDate = null;
    private String regn_no;
    private String rb_value = "newVeh";
    private boolean renderFitnessType = false;
    private boolean renderRegisteredVehFitness = false;
    private boolean renderInsuranceDetails = false;
    private OwnerDetailsDobj ownerDetailsDobj = null;
    @ManagedProperty(value = "#{ins_bean}")
    private InsBean ins_bean;
    private List list_office_to = new ArrayList();

    public NewVehicleFitnessBean() {
        renderVehicleDetails = false;
        sessionVariables = new SessionVariables();
        if (sessionVariables == null
                || sessionVariables.getStateCodeSelected() == null
                || sessionVariables.getOffCodeSelected() == 0
                || sessionVariables.getEmpCodeLoggedIn() == null) {
            vahanMessages = "Something went wrong, Please try again...";
            return;
        }
        if ("DL".equals(sessionVariables.getStateCodeSelected())) {
            setRenderFitnessType(true);
        }
    }

    @PostConstruct
    public void init() {
        try {
            String user_catg = Util.getUserCategory();
            if (appl_details.getCurrent_action_cd() != TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION) {
                Owner_dobj owner_dobj = null;
                OwnerImpl owner_Impl = new OwnerImpl();
                owner_dobj = owner_Impl.set_Owner_appl_db_to_dobj(null, appl_details.getAppl_no(), null, appl_details.getPur_cd());
                owner_bean.setOwnerDobj(owner_dobj);
                owner_bean.set_Owner_appl_dobj_to_bean(owner_dobj);
                owner_bean.setOwner_dobj_prv(owner_dobj);//for holding current dobj for using in the comparison.
                fitnessCode(owner_bean, owner_dobj, user_catg, false, appl_details.getAppl_no());
                setChassis_no(owner_dobj.getChasi_no());
                setEng_no(owner_dobj.getEng_no());
                minDate = NewVehicleFitnessImpl.getExemptedOldRcptDate(appl_details.getAppl_no(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE_FITNESS);
                if (minDate != null) {
                    owner_dobj.setPurchase_dt(minDate);
                }
                owner_bean.setSale_amt(owner_dobj.getSale_amt());
                owner_bean.setNorms(owner_dobj.getNorms());
                owner_bean.setComboBoxesForDobj(owner_dobj);
                owner_bean.vehClassListener();
                owner_bean.setRenderInspectionPanel(true);
                setDisableChassisNo(true);
                owner_bean.disableForHomologationData(true);
                owner_bean.setDisableManuMonth(true);
                owner_bean.setDisableManuYr(true);
                owner_bean.setRenderTaxPanel(false);
                setRenderVehicleDetails(true);
                owner_bean.setTempReg(owner_dobj.getTempReg());
                owner_bean.setForNewTempRegnType();
                owner_bean.setVehicleComponentEditable(true);
                if (owner_dobj.getSpeedGovernorDobj() != null) {
                    owner_bean.setRenderSpeedGov(true);
                }
                if (owner_dobj.getReflectiveTapeDobj() != null) {
                    owner_bean.setRenderReflectiveTape(true);
                }
                AxleBean axle_bean = new AxleBean();
                AxleDetailsDobj axle_dobj = AxleImpl.setAxleVehDetails_db_to_dobj(appl_details.getAppl_no(), null, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                if (axle_dobj != null) {
                    axle_bean.setDobj_To_Bean(axle_dobj);
                    owner_bean.setAxleDetail_Visibility_tab(true);
                    axle_bean.setAxle_dobj_prv(axle_dobj);
                    FacesContext.getCurrentInstance().getViewRoot().getViewMap().put("axleBean", axle_bean);
                }
                RetroFittingDetailsBean cng_bean = new RetroFittingDetailsBean();
                if (owner_bean.getOwnerDobj().getFuel() > 0) {
                    int fuel_type = owner_bean.getOwnerDobj().getFuel();
                    if (fuel_type == TableConstants.VM_FUEL_CNG_TYPE
                            || fuel_type == TableConstants.VM_FUEL_TYPE_PETROL_CNG
                            || fuel_type == TableConstants.VM_FUEL_TYPE_LPG
                            || fuel_type == TableConstants.VM_FUEL_TYPE_PETROL_LPG) {
                        RetroFittingDetailsDobj cng_dobj = RetroFittingDetailsImpl.setCngDetails_db_to_dobj(appl_details.getAppl_no());
                        owner_bean.setCngDetails_Visibility_tab(true);
                        if (cng_dobj != null) {
                            cng_bean.setDobj_To_Bean(cng_dobj);
                            cng_bean.setCng_dobj_prv(cng_dobj);
                            FacesContext.getCurrentInstance().getViewRoot().getViewMap().put("retroFittingDetailsBean", cng_bean);
                            owner_bean.purchaseDateListener();
                        }
                    }
                }


                if (!CommonUtils.isNullOrBlank(owner_dobj.getRegn_no()) && !"NEW".equals(owner_dobj.getRegn_no())) {
                    rb_value = "regVeh";
                    renderFitnessType = true;
                    renderRegisteredVehFitness = true;
                    regn_no = owner_dobj.getRegn_no();
                    ownerDetailsDobj = new OwnerImpl().getOwnerDetails(owner_dobj.getRegn_no());
                    NocDobj noc_dobj = new NocImpl().set_NOC_appl_db_to_dobj(null, owner_dobj.getRegn_no());
                    if (noc_dobj != null) {
                        this.setNOCAndInsuranceDetails(noc_dobj);
                    }
                }


                //for showing old details of temporary registration on same chasi no
                if (owner_dobj != null && owner_dobj.getRegn_type() != null && owner_dobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_TEMPORARY)) {
                    OwnerImpl ownerImpl = new OwnerImpl();
                    tempRegnDetailsList = ownerImpl.getTempRegistrationDetailsList(owner_dobj.getChasi_no());
                }
                if (owner_dobj != null) {
                    owner_bean.setSaleAmtInWords("Rs. " + new Utility().ConvertNumberToWords(owner_dobj.getSale_amt()).toLowerCase());
                }
            }
        } catch (VahanException e) {
            vahanMessages = e.getMessage();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanMessages = TableConstants.SomthingWentWrong;
        }
    }

    public void fitnessCode(OwnerBean owner_bean, Owner_dobj owner_dobj, String userCatg, boolean isHomologationData, String appl_no) throws VahanException, Exception {
        owner_bean.setSamePermAdd_Rend(false);
        NewVehicleFitnessImpl impl = new NewVehicleFitnessImpl();
        FitnessDobj fitness_dobj = impl.set_Fitness_appl_db_to_dobj(null, appl_no);
        if (fitness_dobj != null) {
            FitnessBean fitness_bean = (FitnessBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("fitness_bean");
            if (fitness_bean == null) {
                fitness_bean = new FitnessBean();
            }
            fitness_bean.set_Fitness_appl_dobj_to_bean(fitness_dobj);
            fitness_bean.setFitness_dobj_prv(fitness_dobj);//for holding current dobj for using in the comparison.
            FacesContext.getCurrentInstance().getViewRoot().getViewMap().put("fitness_bean", fitness_bean);
        }
        if (owner_bean.getVehType().trim().equals(String.valueOf(TableConstants.VM_VEHTYPE_TRANSPORT))) {
            owner_bean.setRenderInspectionPanel(true);
            if (sessionVariables.getStateCodeSelected() != null && sessionVariables.getStateCodeSelected().equals("JH")) {
                String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
                String transCatg = null;
                for (int i = 0; i < data.length; i++) {
                    if (data[i][0].equalsIgnoreCase(String.valueOf(owner_dobj.getVh_class()))) {
                        transCatg = data[i][3];
                        break;
                    }
                }
                if (transCatg != null && transCatg.equals("P")) {
                    owner_bean.setDisableSeatCap(false);
                } else {
                    if (isHomologationData) {
                        if (userCatg != null && !userCatg.equals(TableConstants.USER_CATG_DEALER)) {
                            if (owner_bean.getVch_purchase_as().equals(TableConstants.PURCHASE_AS_CHASIS)) {
                                owner_bean.setDisableSeatCap(false);
                            } else {
                                owner_bean.setDisableSeatCap(true);
                            }
                        } else {
                            owner_bean.setDisableSeatCap(true);
                        }
                    } else {
                        owner_bean.setDisableSeatCap(false);
                    }
                }
            }
        } else {
            if (FitnessImpl.getNewFitnessUpto(owner_dobj) >= 15) {
                owner_bean.setRenderInspectionPanel(false);
            } else {
                owner_bean.setRenderInspectionPanel(true);
            }
        }
    }

    public void showDetails() {
        Owner_dobj owneTmpDobj = null;
        OwnerImpl owner_Impl = new OwnerImpl();

        BlackListedVehicleImpl blkImpl = new BlackListedVehicleImpl();
        Owner_dobj ownerHomo = null;
        Owner_dobj owner_dobj = null;
        Long user_cd = Long.parseLong(sessionVariables.getEmpCodeLoggedIn());
        String BlackListedDetails = null;
        if (sessionVariables == null
                || sessionVariables.getStateCodeSelected() == null
                || sessionVariables.getOffCodeSelected() == 0
                || sessionVariables.getEmpCodeLoggedIn() == null) {
            return;
        }
        try {
            TmConfigurationDobj tmConfDobj = Util.getTmConfiguration();
            if (chassis_no != null) {
                owner_bean.getOwnerDobj().setChasi_no(chassis_no);
                owner_bean.getOwnerDobj().setEng_no(eng_no);
                owner_bean.setDisableChasiNo(true);
                owner_bean.setDisableEngineNo(true);

                if (chassis_no != null) {
                    owner_dobj = owner_Impl.set_Owner_appl_db_to_dobj("", "", this.chassis_no.trim().toUpperCase(), TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE_FITNESS);
                    if (owner_dobj != null) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Alert!", "Entry is Already Done for this Chassis No: " + this.chassis_no.trim().toUpperCase() + "," + (!"NEW".equals(owner_dobj.getRegn_no()) ? "[Regn.No: " + owner_dobj.getRegn_no() + "]" : "") + "," + (owner_dobj.getAppl_no() != null ? "[Appl.No: " + owner_dobj.getAppl_no() + "]" : "")));
                        return;
                    }
                }
                BlackListedDetails = blkImpl.checkChassisNoForBlackList(chassis_no);
                if (this.chassis_no.trim().length() > 5 && BlackListedDetails != null) {
                    throw new VahanException(BlackListedDetails);
                }

                if (!CommonUtils.isNullOrBlank(chassis_no) && !CommonUtils.isNullOrBlank(eng_no)) {
                    ownerHomo = ServerUtil.getOnwerDobjFromHomologation(chassis_no.toUpperCase().trim(), eng_no.toUpperCase().trim(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), true);
                    owner_dobj = ownerHomo;
                }

                owneTmpDobj = owner_Impl.getValidateVtOwnerTempDobj(chassis_no);
                if (owneTmpDobj != null) {
                    /*
                     * Check if wrong regn_type is selected for temporary registration
                     */
                    //temporary solution for displaying temporary details if temp.regn is done more than one time on same chassi no
                    tempRegnDetailsList = owner_Impl.getTempRegistrationDetailsList(chassis_no);
//                    if (owneTmpDobj.getDob_temp() != null && owneTmpDobj.getDob_temp().getValidUpto() != null) {
//                        if (DateUtils.compareDates(owneTmpDobj.getDob_temp().getValidUpto(), ServerUtil.dateRange(new Date(), 0, -3, 0)) == 1) {
//                            throw new VahanException("Renewal of temporary registration is not allowed due to validity [" + ServerUtil.parseDateToString(owneTmpDobj.getDob_temp().getValidUpto()) + "] expired 3 months back.");
//                        }
//                    }

                    if (tempRegnDetailsList != null && !tempRegnDetailsList.isEmpty()
                            && tempRegnDetailsList.size() >= 3
                            && tempRegnDetailsList.get(tempRegnDetailsList.size() - 1) != null
                            && tempRegnDetailsList.get(tempRegnDetailsList.size() - 1).getOwnerTempDobj().getPurpose() != null
                            && tempRegnDetailsList.get(tempRegnDetailsList.size() - 1).getOwnerTempDobj().getPurpose().equalsIgnoreCase("BB")) {
                        throw new VahanException("Chassis No is already registered , Please use different Chassis No or Multiple Temporary Registration is allowed for 'Body Building' 3 Times Only.");
                    }
                    if (owner_bean != null && owner_bean.getTemp_dobj() != null) {
                        owner_bean.getTemp_dobj().setTemp_regn_type("R");//Renewal of Temporary Registration
                    }
                    if (ownerHomo == null) {
                        owner_dobj = owneTmpDobj;
                    } else {
                        owner_dobj = owner_bean.setOwnerTempDetails(owner_dobj, owneTmpDobj);
                    }
                }
            }

            if (owner_dobj != null) {
                boolean isExShoowroomPriceDisable = false;
                if (tmConfDobj != null) {
                    isExShoowroomPriceDisable = tmConfDobj.isEx_showroom_price_homologation();
                }
                if (ownerHomo != null) {
                    if (isExShoowroomPriceDisable) {
                        if (ownerHomo.getSale_amt() == 0) {
                            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Inventory detail found but"
                                    + " Ex-Showroom Price has not been uploaded for the state " + ServerUtil.getStateNameByStateCode(sessionVariables.getStateCodeSelected()) + " <br/> "
                                    + " UMRN/Model Code: " + ownerHomo.getMaker_model() + " | "
                                    + " Color Code : " + ownerHomo.getColorCode() + " | "
                                    + " Feature Code : " + ownerHomo.getFeatureCode() + " "));
                            return;
                        }
                    }

                    owner_bean.setIsHomologationData(TableConstants.HOMOLOGATION_DATA);
                    owner_bean.disableDealerFields(ownerHomo.getManu_mon(), ownerHomo.getManu_yr());
                    owner_bean.setHomoLdWt(ownerHomo.getLd_wt());
                    owner_bean.setHomoUnLdWt(ownerHomo.getUnld_wt());
                    owner_bean.setHomoDobj(ownerHomo);
                }
                owner_dobj.setSpeedGovernerList(new ArrayList());
                String[][] data = MasterTableFiller.masterTables.VM_SPEEDGOV_MANUFACTURE.getData();
                for (int i = 0; i < data.length; i++) {
                    owner_dobj.getSpeedGovernerList().add(new SelectItem(data[i][0], data[i][1]));
                }
                owner_bean.setOwnerDobj(owner_dobj);
                owner_bean.set_Owner_appl_dobj_to_bean(owner_dobj);
                owner_bean.setOwner_dobj_prv(owner_dobj);//for holding current dobj for using in the comparison.
                owner_bean.setSale_amt(owner_dobj.getSale_amt());
                owner_bean.setNorms(owner_dobj.getNorms());
                owner_bean.setComboBoxesForDobj(owner_dobj);
                getOwner_bean().setRenderTaxPanel(false);
                setRenderVehicleDetails(true);
                setDisableChassisNo(true);
                owner_bean.disableForHomologationData(true);
                // owner_bean.setVehicleComponentEditable(true);
                RetroFittingDetailsBean cng_bean = new RetroFittingDetailsBean();
                if (owner_bean.getOwnerDobj().getFuel() > 0) {
                    int fuel_type = owner_bean.getOwnerDobj().getFuel();
                    if (fuel_type == TableConstants.VM_FUEL_CNG_TYPE
                            || fuel_type == TableConstants.VM_FUEL_TYPE_PETROL_CNG
                            || fuel_type == TableConstants.VM_FUEL_TYPE_LPG
                            || fuel_type == TableConstants.VM_FUEL_TYPE_PETROL_LPG) {
                        RetroFittingDetailsDobj cng_dobj = RetroFittingDetailsImpl.setCngDetails_db_to_dobj(appl_details.getAppl_no());
                        owner_bean.setCngDetails_Visibility_tab(true);
                        if (cng_dobj != null) {
                            cng_bean.setDobj_To_Bean(cng_dobj);
                            cng_bean.setCng_dobj_prv(cng_dobj);
                            FacesContext.getCurrentInstance().getViewRoot().getViewMap().put("retroFittingDetailsBean", cng_bean);
                            owner_bean.purchaseDateListener();
                        }
                    }
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Inventory details against this Chassis No[" + chassis_no + "]Engine No(last 5 chars)[" + eng_no + "] Combination"
                        + " is not uploaded by the manufacturer.!", "Inventory details against this Chassis No[" + chassis_no + "]Engine No(last 5 chars)[" + eng_no + "] Combination"
                        + " is not uploaded by the manufacturer."));
                return;
            }

            if (owneTmpDobj != null) {

//                owner_bean.setTempReg(owneTmpDobj.getTempReg());
//                owner_bean.setForNewTempRegnType();

                if (ownerHomo == null) {
                    owner_dobj = owneTmpDobj;
                } else {
                    owner_dobj = owner_bean.setOwnerTempDetails(owner_dobj, owneTmpDobj);
                }

                owner_dobj.setOwner_sr(1);

                owner_bean.setTempLdWt(owner_dobj.getLd_wt());
                owner_bean.setTempUnLdWt(owner_dobj.getUnld_wt());

                if (owneTmpDobj.getDob_temp() != null && owneTmpDobj.getDob_temp().getPurpose() != null
                        && !owneTmpDobj.getDob_temp().getPurpose().equalsIgnoreCase("OM")) {
                    String message = "Vehicle Details are filled from Temporary Registered Vehicle";
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, message));
                }

            }
            if (owner_dobj != null) {
                owner_bean.setSaleAmtInWords("Rs. " + new Utility().ConvertNumberToWords(owner_dobj.getSale_amt()).toLowerCase());
            }

        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

    public void saveNewVehicleFitnessDeatils() {
        boolean isHomologation = false;
        AxleBean axle_bean = null;
        AxleDetailsDobj axle_dobj = null;
        RetroFittingDetailsBean cng_bean = null;
        RetroFittingDetailsDobj cng_dobj = null;
        FitnessDobj fitness_dobj = null;
        ArrayList fitCheckList = null;
        if (CommonUtils.isNullOrBlank(owner_bean.getOwnerDobj().getRegn_no())) {
            owner_bean.getOwnerDobj().setRegn_no("NEW");
        }
        owner_bean.getOwnerDobj().setState_cd(sessionVariables.getStateCodeSelected());
        owner_bean.getOwnerDobj().setOff_cd(sessionVariables.getOffCodeSelected());
        Status_dobj status_dobj = new Status_dobj();
        status_dobj.setRegn_no(owner_bean.getOwnerDobj().getRegn_no());
        status_dobj.setPur_cd(TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE_FITNESS);
        status_dobj.setAction_cd(TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION);
        status_dobj.setFlow_slno(1);
        status_dobj.setFile_movement_slno(1);
        status_dobj.setState_cd(sessionVariables.getStateCodeSelected());
        status_dobj.setOff_cd(sessionVariables.getOffCodeSelected());
        status_dobj.setEmp_cd(0);

        try {
            NewVehicleFitnessImpl newVehicleFitnessImpl = new NewVehicleFitnessImpl();
            axle_bean = (AxleBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("axleBean");
            if (axle_bean != null) {
                axle_dobj = axle_bean.setBean_To_Dobj();
            }
            cng_bean = (RetroFittingDetailsBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("retroFittingDetailsBean");
            if (cng_bean != null) {
                cng_dobj = cng_bean.setBean_To_Dobj();
            }
            if (owner_bean.getOwnerDobj().getSpeedGovernorDobj() != null) {
                owner_bean.getOwnerDobj().getSpeedGovernorDobj().setRegn_no(owner_bean.getOwnerDobj().getRegn_no());
            }
            if (owner_bean.getOwnerDobj().getReflectiveTapeDobj() != null) {
                owner_bean.getOwnerDobj().getReflectiveTapeDobj().setRegn_no(owner_bean.getOwnerDobj().getRegn_no());
            }
            owner_bean.getOwnerDobj().setNorms(owner_bean.getNorms());
            if (owner_bean.getIsHomologationData() != null
                    && !owner_bean.getIsHomologationData().equals("")
                    && owner_bean.getIsHomologationData().equals(TableConstants.HOMOLOGATION_DATA)) {
                owner_bean.getOwnerDobj().setLaser_code(TableConstants.HOMOLOGATION_DATA);
                isHomologation = true;
            }
            String appl_no = newVehicleFitnessImpl.saveFitnessDetails(owner_bean.getOwnerDobj(), fitness_dobj, fitCheckList, compBeanList, eng_no, chassis_no, status_dobj, axle_dobj, cng_dobj, isHomologation, owner_bean.getHomoDobj());
            if (!CommonUtils.isNullOrBlank(appl_no)) {
                appl_no_massage = "Application No: " + appl_no + " Generated Successfully ";
                PrimeFaces.current().ajax().update("preRegFitness:op_showGenApplNo");
                PrimeFaces.current().executeScript("PF('successDialog').show()");
            }
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, vex.getMessage(), vex.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

    @Override
    public String save() {
        boolean isHomologation = false;
        AxleBean axle_bean = null;
        AxleDetailsDobj axle_dobj = null;
        RetroFittingDetailsBean cng_bean = null;
        RetroFittingDetailsDobj cng_dobj = null;
        FitnessBean fitness_bean = null;
        FitnessDobj fitness_dobj = null;
        ArrayList fitCheckList = null;
        String returnLocation = "";
        Status_dobj status_dobj = new Status_dobj();
        status_dobj.setAction_cd(appl_details.getCurrent_action_cd());
        status_dobj.setAppl_dt(appl_details.getAppl_dt());
        status_dobj.setAppl_no(appl_details.getAppl_no());
        status_dobj.setRegn_no(appl_details.getRegn_no());
        status_dobj.setPur_cd(appl_details.getPur_cd());
        status_dobj.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
        status_dobj.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
        status_dobj.setStatus(getApp_disapp_dobj().getNew_status());
        status_dobj.setCurrent_role(appl_details.getCurrent_role());

        try {
            NewVehicleFitnessImpl newVehicleFitnessImpl = new NewVehicleFitnessImpl();
            fitness_bean = (FitnessBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("fitness_bean");
            fitness_dobj = (FitnessDobj) fitness_bean.makeDobjFromBean()[0];
            fitCheckList = (ArrayList) fitness_bean.makeDobjFromBean()[1];
            axle_bean = (AxleBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("axleBean");
            if (axle_bean != null) {
                axle_dobj = axle_bean.setBean_To_Dobj();
            }
            cng_bean = (RetroFittingDetailsBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("retroFittingDetailsBean");
            if (cng_bean != null) {
                cng_dobj = cng_bean.setBean_To_Dobj();
            }
            owner_bean.getOwnerDobj().setFit_upto(fitness_bean.getFitnessDobj().getFit_valid_to());
            fitness_dobj.setAppl_no(appl_details.getAppl_no());
            if (owner_bean.getIsHomologationData() != null
                    && !owner_bean.getIsHomologationData().equals("")
                    && owner_bean.getIsHomologationData().equals(TableConstants.HOMOLOGATION_DATA)) {
                owner_bean.getOwnerDobj().setLaser_code(TableConstants.HOMOLOGATION_DATA);
                isHomologation = true;
            }
            newVehicleFitnessImpl.saveOnlyPreRegFitnessDetails(owner_bean.getOwnerDobj(), fitness_dobj, fitCheckList, compBeanList, eng_no, chassis_no, status_dobj, axle_dobj, cng_dobj, isHomologation, owner_bean.getHomoDobj());
            returnLocation = "seatwork";
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, vex.getMessage(), vex.getMessage()));
            returnLocation = "";
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            returnLocation = "";
        }
        return returnLocation;
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        compBeanList.clear();
        try {
            compBeanList = owner_bean.compareChagnes();
        } catch (VahanException e) {
            vahanMessages = e.getMessage();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanMessages = TableConstants.SomthingWentWrong;
        }


        return compBeanList;
    }

    @Override
    public String saveAndMoveFile() {
        boolean isHomologation = false;
        AxleBean axle_bean = null;
        AxleDetailsDobj axle_dobj = null;
        RetroFittingDetailsBean cng_bean = null;
        RetroFittingDetailsDobj cng_dobj = null;
        FitnessBean fitness_bean = null;
        FitnessDobj fitness_dobj = null;
        ArrayList fitCheckList = null;
        String returnLocation = "";
        Status_dobj status_dobj = new Status_dobj();
        status_dobj.setAction_cd(appl_details.getCurrent_action_cd());
        status_dobj.setAppl_dt(appl_details.getAppl_dt());
        status_dobj.setAppl_no(appl_details.getAppl_no());
        status_dobj.setRegn_no(appl_details.getRegn_no());
        status_dobj.setPur_cd(appl_details.getPur_cd());
        status_dobj.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
        status_dobj.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
        status_dobj.setStatus(getApp_disapp_dobj().getNew_status());
        status_dobj.setCurrent_role(appl_details.getCurrent_role());

        try {
            NewVehicleFitnessImpl newVehicleFitnessImpl = new NewVehicleFitnessImpl();
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                    || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                // owner_bean = (OwnerBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("owner_bean");
                compBeanList = owner_bean.compareChagnes();
                fitness_bean = (FitnessBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("fitness_bean");
                fitness_dobj = (FitnessDobj) fitness_bean.makeDobjFromBean()[0];
                fitCheckList = (ArrayList) fitness_bean.makeDobjFromBean()[1];
                axle_bean = (AxleBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("axleBean");
                if (axle_bean != null) {
                    axle_dobj = axle_bean.setBean_To_Dobj();
                }
                cng_bean = (RetroFittingDetailsBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("retroFittingDetailsBean");
                if (cng_bean != null) {
                    cng_dobj = cng_bean.setBean_To_Dobj();
                }
                owner_bean.getOwnerDobj().setFit_upto(fitness_bean.getFitnessDobj().getFit_valid_to());

                fitness_dobj.setAppl_no(appl_details.getAppl_no());
                if (owner_bean.getIsHomologationData() != null
                        && !owner_bean.getIsHomologationData().equals("")
                        && owner_bean.getIsHomologationData().equals(TableConstants.HOMOLOGATION_DATA)) {
                    owner_bean.getOwnerDobj().setLaser_code(TableConstants.HOMOLOGATION_DATA);
                    isHomologation = true;
                    // NewImpl.insertHomologationDetails(tmgr, owner_bean.getHomoDobj(), appl_no, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                }
                newVehicleFitnessImpl.saveAndMovePreRegFitnessDetails(owner_bean.getOwnerDobj(), fitness_dobj, fitCheckList, compBeanList, eng_no, chassis_no, status_dobj, axle_dobj, cng_dobj, isHomologation, owner_bean.getHomoDobj());
                returnLocation = "seatwork";
            }
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)) {
                status_dobj.setPrev_action_cd_selected(app_disapp_dobj.getPre_action_code());
                fitness_bean = (FitnessBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("fitness_bean");
                fitness_dobj = (FitnessDobj) fitness_bean.makeDobjFromBean()[0];
                fitCheckList = (ArrayList) fitness_bean.makeDobjFromBean()[1];
                newVehicleFitnessImpl.reBack(owner_bean.getOwnerDobj(), fitness_dobj, fitCheckList, compBeanList, eng_no, chassis_no, status_dobj, axle_dobj, cng_dobj);
                returnLocation = "seatwork";
            }
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, vex.getMessage(), vex.getMessage()));
            returnLocation = "";
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            returnLocation = "";
        }

        return returnLocation;

    }

    public void fitnessTypeListener() {

        if ("newVeh".equals(rb_value)) {
            renderRegisteredVehFitness = false;
        } else if ("regVeh".equals(rb_value)) {
            renderRegisteredVehFitness = true;
        }
    }

    public void showDetailsForRegisteredVehicle() {
        OwnerImpl owner_Impl = new OwnerImpl();
        BlackListedVehicleImpl blkImpl = new BlackListedVehicleImpl();
        Owner_dobj owner_dobj = null;
        String BlackListedDetails = null;
        if (sessionVariables == null
                || sessionVariables.getStateCodeSelected() == null
                || sessionVariables.getOffCodeSelected() == 0
                || sessionVariables.getEmpCodeLoggedIn() == null) {
            return;
        }
        try {

            owner_dobj = owner_Impl.set_Owner_appl_db_to_dobj(regn_no, null, null, TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE_FITNESS);
            if (owner_dobj != null) {
                ownerDetailsDobj = new OwnerImpl().getOwnerDetails(regn_no);
                NocDobj noc_dobj = new NocImpl().set_NOC_appl_db_to_dobj(null, owner_dobj.getRegn_no());
                if (noc_dobj == null) {
                    throw new VahanException("NOC details not found for Vehicle No." + owner_dobj.getRegn_no());
                }

                if (!(sessionVariables.getStateCodeSelected().equals(noc_dobj.getState_to()))) {
                    throw new VahanException("Noc is issued from state " + ServerUtil.getStateNameByStateCode(noc_dobj.getState_to()) + " and Office " + ServerUtil.getOfficeName(noc_dobj.getOff_to(), noc_dobj.getState_to()) + ". ");
                }

                OwnerDetailsDobj ownerDtlsDobj = new DocumentUploadImpl().getVaOwnerDetailsForDocumentUpload(null, sessionVariables.getStateCodeSelected(), owner_dobj.getRegn_no());
                if (ownerDtlsDobj != null) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Alert!", "Entry is Already Done for this Chassis No: " + ownerDtlsDobj.getChasi_no().toUpperCase() + "," + "[Regn.No: " + ownerDtlsDobj.getRegn_no() + "]"));
                    return;
                }

                BlackListedDetails = blkImpl.checkChassisNoForBlackList(chassis_no);
                if (owner_dobj.getChasi_no().trim().length() > 5 && BlackListedDetails != null) {
                    throw new VahanException(BlackListedDetails);
                }
                owner_dobj.setSpeedGovernerList(new ArrayList());
                String[][] data = MasterTableFiller.masterTables.VM_SPEEDGOV_MANUFACTURE.getData();
                for (int i = 0; i < data.length; i++) {
                    owner_dobj.getSpeedGovernerList().add(new SelectItem(data[i][0], data[i][1]));
                }
                owner_dobj.setRegn_type(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE);
                owner_bean.setOwnerDobj(owner_dobj);
                owner_bean.set_Owner_appl_dobj_to_bean(owner_dobj);
                owner_bean.setOwner_dobj_prv(owner_dobj);//for holding current dobj for using in the comparison.
                owner_bean.setSale_amt(owner_dobj.getSale_amt());
                owner_bean.setNorms(owner_dobj.getNorms());
                owner_bean.setComboBoxesForDobj(owner_dobj);
                getOwner_bean().setRenderTaxPanel(false);
                setRenderVehicleDetails(true);
                setDisableChassisNo(true);
                owner_bean.disableForHomologationData(true);
                // owner_bean.setVehicleComponentEditable(true);
                RetroFittingDetailsBean cng_bean = new RetroFittingDetailsBean();
                if (owner_bean.getOwnerDobj().getFuel() > 0) {
                    int fuel_type = owner_bean.getOwnerDobj().getFuel();
                    if (fuel_type == TableConstants.VM_FUEL_CNG_TYPE
                            || fuel_type == TableConstants.VM_FUEL_TYPE_PETROL_CNG
                            || fuel_type == TableConstants.VM_FUEL_TYPE_LPG
                            || fuel_type == TableConstants.VM_FUEL_TYPE_PETROL_LPG) {
                        RetroFittingDetailsDobj cng_dobj = RetroFittingDetailsImpl.setCngDetails_db_to_dobj(appl_details.getAppl_no());
                        owner_bean.setCngDetails_Visibility_tab(true);
                        if (cng_dobj != null) {
                            cng_bean.setDobj_To_Bean(cng_dobj);
                            cng_bean.setCng_dobj_prv(cng_dobj);
                            FacesContext.getCurrentInstance().getViewRoot().getViewMap().put("retroFittingDetailsBean", cng_bean);
                            owner_bean.purchaseDateListener();
                        }
                    }
                }
                this.setNOCAndInsuranceDetails(noc_dobj);
                owner_bean.setSaleAmtInWords("Rs. " + new Utility().ConvertNumberToWords(owner_dobj.getSale_amt()).toLowerCase());
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Vehicle Details not found for the Registration no." + regn_no, "Vehicle Details not found for the Registration no." + regn_no));
                return;
            }

        } catch (VahanException vex) {
            renderVehicleDetails = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
            return;
        } catch (Exception ex) {
            renderVehicleDetails = false;
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

    public void setNOCAndInsuranceDetails(NocDobj noc_dobj) throws VahanException {
        InsDobj ins_dobj = InsImpl.set_ins_dtls_db_to_dobj(regn_no, null, null, 0);
        if (ins_dobj != null) {
            ins_bean.set_Ins_dobj_to_bean(ins_dobj);
            renderInsuranceDetails = true;
        }
        OtherStateVehDobj dobj = new OtherStateVehDobj();
        dobj.setNocNo(noc_dobj.getNoc_no());
        dobj.setNocDate(noc_dobj.getNoc_dt());
        dobj.setMaxDate(noc_dobj.getMin_dt());
        dobj.setOldStateCD(noc_dobj.getState_cd());
        dobj.setOldOffCD(noc_dobj.getOff_cd());
        owner_bean.setOtherStateDobj(dobj);
        renderVehicleDetails = true;
        stateToListner();
    }

    public void stateToListner() {
        if (owner_bean != null && owner_bean.getOwnerDobj() != null && !CommonUtils.isNullOrBlank(owner_bean.getOwnerDobj().getState_cd())) {
            list_office_to.clear();
            String[][] data = MasterTableFiller.masterTables.TM_OFFICE.getData();
            for (int i = 0; i < data.length; i++) {
                if (data[i][13].equals(ownerDetailsDobj.getState_cd())) {
                    list_office_to.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        }
    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
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

    public OwnerBean getOwner_bean() {
        return owner_bean;
    }

    public void setOwner_bean(OwnerBean owner_bean) {
        this.owner_bean = owner_bean;
    }

    public String getEng_no() {
        return eng_no;
    }

    public void setEng_no(String eng_no) {
        this.eng_no = eng_no;
    }

    public String getChassis_no() {
        return chassis_no;
    }

    public void setChassis_no(String chassis_no) {
        this.chassis_no = chassis_no;
    }

    public boolean isRenderVehicleDetails() {
        return renderVehicleDetails;
    }

    public void setRenderVehicleDetails(boolean renderVehicleDetails) {
        this.renderVehicleDetails = renderVehicleDetails;
    }

    public String getVahanMessages() {
        return vahanMessages;
    }

    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }

    public boolean isDisableChassisNo() {
        return disableChassisNo;
    }

    public void setDisableChassisNo(boolean disableChassisNo) {
        this.disableChassisNo = disableChassisNo;
    }

    public String getAppl_no_massage() {
        return appl_no_massage;
    }

    public void setAppl_no_massage(String appl_no_massage) {
        this.appl_no_massage = appl_no_massage;
    }

    public List<OwnerDetailsDobj> getTempRegnDetailsList() {
        return tempRegnDetailsList;
    }

    public void setTempRegnDetailsList(List<OwnerDetailsDobj> tempRegnDetailsList) {
        this.tempRegnDetailsList = tempRegnDetailsList;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getRb_value() {
        return rb_value;
    }

    public void setRb_value(String rb_value) {
        this.rb_value = rb_value;
    }

    public boolean isRenderFitnessType() {
        return renderFitnessType;
    }

    public void setRenderFitnessType(boolean renderFitnessType) {
        this.renderFitnessType = renderFitnessType;
    }

    /**
     * @return the ownerDetailsDobj
     */
    public OwnerDetailsDobj getOwnerDetailsDobj() {
        return ownerDetailsDobj;
    }

    /**
     * @param ownerDetailsDobj the ownerDetailsDobj to set
     */
    public void setOwnerDetailsDobj(OwnerDetailsDobj ownerDetailsDobj) {
        this.ownerDetailsDobj = ownerDetailsDobj;
    }

    public void setIns_bean(InsBean ins_bean) {
        this.ins_bean = ins_bean;
    }

    /**
     * @return the renderRegisteredVehFitness
     */
    public boolean isRenderRegisteredVehFitness() {
        return renderRegisteredVehFitness;
    }

    /**
     * @param renderRegisteredVehFitness the renderRegisteredVehFitness to set
     */
    public void setRenderRegisteredVehFitness(boolean renderRegisteredVehFitness) {
        this.renderRegisteredVehFitness = renderRegisteredVehFitness;
    }

    /**
     * @return the renderInsuranceDetails
     */
    public boolean isRenderInsuranceDetails() {
        return renderInsuranceDetails;
    }

    /**
     * @param renderInsuranceDetails the renderInsuranceDetails to set
     */
    public void setRenderInsuranceDetails(boolean renderInsuranceDetails) {
        this.renderInsuranceDetails = renderInsuranceDetails;
    }

    public List getList_office_to() {
        return list_office_to;
    }

    public void setList_office_to(List list_office_to) {
        this.list_office_to = list_office_to;
    }
}
