/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.AltDobj;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.InspectionDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.ReflectiveTapeDobj;
import nic.vahan.form.dobj.SpeedGovernorDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.Trailer_dobj;
import nic.vahan.form.dobj.fitness.TmConfigurationFitnessDobj;
import nic.vahan.form.impl.AltImpl;
import nic.vahan.form.impl.AxleImpl;
import nic.vahan.form.impl.RetroFittingDetailsImpl;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import nic.vahan.services.insurance.InsuranceDetailService;
import nic.vahan.services.insurance.dobj.InsuranceInfoDobj;
import org.apache.log4j.Logger;
//import org.primefaces.context.RequestContext;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "alt_bean")
@ViewScoped
public class AltBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(AltBean.class);
    private AltDobj alt_dobj = null;
    private AltDobj alt_dobj_prev;
    private List listVhClass = new ArrayList();
    private List listbodytype = new ArrayList();
    private List list_fuel = new ArrayList();
    private List list_vm_catg = new ArrayList();
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<ComparisonBean> prevChangedDataList;
    private boolean cng_tab_visibility;
    RetroFittingDetailsDobj cng_dobj = null;
    @ManagedProperty(value = "#{retroFittingDetailsBean}")
    private RetroFittingDetailsBean cNG_Details_Bean;
    private List<OwnerDetailsDobj> listExistingOwnerDetails = new ArrayList<>();
    @ManagedProperty(value = "#{ins_bean}")
    private InsBean ins_bean;
    private int activeIndex = 0;
    private Trailer_dobj trailerDobj = null;
    private List<Trailer_dobj> listTrailerDetails = new ArrayList<>();//List to show trailer details
    private List<Trailer_dobj> listTrailerDetailsSave = new ArrayList<>();//:List to save attached trailer details.
    private List<Trailer_dobj> listTrailerDetach = new ArrayList<>();
    private List<Trailer_dobj> listTrailerDobjPrev;
    private Trailer_dobj trailerDobjPrev;
    private String editOrAdd = "";
    private int index = 0;
    private int indexSave = 0;
    private String trailerModified = "";
    private String vahanMessages = null;
    private boolean renderLinkedVeh;
    private String linkedVehDtls;
    private boolean renderIsSpeedGov = false;
    private boolean renderSpeedGovt = false;
    private Owner_dobj ownerDobj = null;
    private SessionVariables sessionVariables = null;
    private boolean renderIsReflectiveTape = false;
    private boolean renderReflectiveTape = false;
    private int offCodeSelected;
    private String stateCodeSelected = null;
    private String empCodeSelected = null;
    private List listSpeedGovTypes;
    private boolean isReflectiveTapeAllowed = false;
    private TmConfigurationFitnessDobj tmConfigFitnessDobj = null;
    private boolean axleDetail_Visibility_tab = false;
    private AxleDetailsDobj axleDobj = null;
    @ManagedProperty(value = "#{axleBean}")
    private AxleBean axleBean;
    private InspectionDobj inspDobj = new InspectionDobj();
    private InspectionDobj inspDobjPrev = null;
    private boolean renderInspBody;

    @PostConstruct
    public void init() {
        InsDobj ins_dobj_ret = null;
        InsDobj ins_dobj_retVA = null;
        String[] currentInfos = null;
        renderInspBody = false;

        sessionVariables = new SessionVariables();
        if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
            vahanMessages = "Session time Out";
            return;
        }
        stateCodeSelected = sessionVariables.getStateCodeSelected();
        offCodeSelected = sessionVariables.getOffCodeSelected();
        empCodeSelected = sessionVariables.getEmpCodeLoggedIn();

        String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
        for (int i = 0; i < data.length; i++) {
            listVhClass.add(new SelectItem(data[i][0], data[i][1]));
        }

        data = MasterTableFiller.masterTables.VM_FUEL.getData();
        for (int i = 0; i < data.length; i++) {
            list_fuel.add(new SelectItem(data[i][0], data[i][1]));
        }

        data = MasterTableFiller.masterTables.VM_VCH_CATG.getData();
        for (int i = 0; i < data.length; i++) {
            list_vm_catg.add(new SelectItem(data[i][0], data[i][1]));
        }

        try {
            if (getAppl_details() != null) {
                if (appl_details.getOwnerDetailsDobj() == null) {
                    vahanMessages = "Owner Details not Found in the Database, Please Contact to the System Administrator";
                    return;
                }
                setListSpeedGovTypes(OwnerImpl.getListSpeedGovTypes());
                if (appl_details.getOwnerDobj() != null) {
                    setOwnerDobj(appl_details.getOwnerDobj());
                }
                AltImpl alt_Impl = new AltImpl();
                alt_dobj = alt_Impl.set_ALT_appl_db_to_dobj(getAppl_details().getAppl_no());
                //PUSH BACK TARUN KL
                if (stateCodeSelected.equals("KL")) {
                    String val = alt_Impl.set_ALT_appl_db_to_dobj_vaOwnerOther(getAppl_details().getAppl_no());
                    if (!CommonUtils.isNullOrBlank(val)) {
                        alt_dobj.setPush_bk_seat(Integer.valueOf(val.split(",")[0]));
                        alt_dobj.setOrdinary_seat(Integer.valueOf(val.split(",")[1]));
                    }
                }
                listTrailerDetach = alt_Impl.setTrailerDetachApplDBtoDObj(getAppl_details().getAppl_no());
                listTrailerDetailsSave = alt_Impl.setTrailerDetailsVA(getAppl_details().getAppl_no());
                listTrailerDetails = alt_Impl.setTrailerApplDBtoDObj(getAppl_details().getAppl_no(), null);

                if (alt_dobj != null) {
                    alt_dobj.setAppl_no(getAppl_details().getAppl_no());
                    alt_dobj.setRegn_no(getAppl_details().getRegn_no());
                    alt_dobj.setOldVahanRecords(false);

                    if (stateCodeSelected.equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(alt_dobj.getVh_class()) + ",")) {
                        alt_dobj.setPushBkSeatRender(true);
                        PrimeFaces.current().ajax().update("alterationTabview:pushbackseatfinalalt");
                        //RequestContext.getCurrentInstance().update("alterationTabview:alt_info:pushbackseatfinalalt");
                    } else {
                        alt_dobj.setPushBkSeatRender(false);
                        PrimeFaces.current().ajax().update("alterationTabview:pushbackseatfinalalt");
                        // PrimeFaces.current().ajax().update("alterationTabview:alt_info:pushbackseatfinalalt");
                        //RequestContext.getCurrentInstance().update("alterationTabview:alt_info:pushbackseatfinalalt");
                    }

                } else {
                    alt_dobj = new AltDobj();
                    alt_dobj = alt_Impl.set_ALT_appl_db_to_dobj_from_VT_OWNER(getAppl_details().getRegn_no(), sessionVariables);
                    alt_dobj.setAppl_no(getAppl_details().getAppl_no());
                    alt_dobj.setRegn_no(getAppl_details().getRegn_no());
                    alt_dobj.setOldVahanRecords(true);
                    if (stateCodeSelected.equals("KL") && TableConstants.VHC_TRANSPORT_CATG.contains("," + String.valueOf(alt_dobj.getVh_class()) + ",")) {
                        alt_dobj.setPushBkSeatRender(true);
                        PrimeFaces.current().ajax().update("alterationTabview:pushbackseatfinalalt");
                        // RequestContext.getCurrentInstance().update("alterationTabview:alt_info:pushbackseatfinalalt");
                    } else {
                        alt_dobj.setPushBkSeatRender(false);
                        PrimeFaces.current().ajax().update("alterationTabview:pushbackseatfinalalt");
                        //RequestContext.getCurrentInstance().update("alterationTabview:alt_info:pushbackseatfinalalt");
                    }

                }
                alt_dobj_prev = (AltDobj) alt_dobj.clone();
                if (listTrailerDetails != null && !listTrailerDetails.isEmpty()) {
                    trailerDobj = new Trailer_dobj();
                    listTrailerDobjPrev = (ArrayList<Trailer_dobj>) ((ArrayList) listTrailerDetails).clone();
                } else {
                    listTrailerDetails = alt_Impl.setTrailerApplDBtoDObj(null, getAppl_details().getRegn_no());
                    if (listTrailerDetails != null && !listTrailerDetails.isEmpty()) {
                        trailerDobj = new Trailer_dobj();
                        listTrailerDobjPrev = (ArrayList<Trailer_dobj>) ((ArrayList) listTrailerDetails).clone();
                    } else {
                        trailerDobj = new Trailer_dobj();
                    }
                }

                listExistingOwnerDetails.add(appl_details.getOwnerDetailsDobj());

                if (listExistingOwnerDetails.get(0).getFuel() == TableConstants.VM_FUEL_CNG_TYPE
                        || listExistingOwnerDetails.get(0).getFuel() == TableConstants.VM_FUEL_TYPE_PETROL_CNG
                        || listExistingOwnerDetails.get(0).getFuel() == TableConstants.VM_FUEL_TYPE_LPG
                        || listExistingOwnerDetails.get(0).getFuel() == TableConstants.VM_FUEL_TYPE_PETROL_LPG) {

                    cNG_Details_Bean.setMinDate(JSFUtils.getStringToDate(listExistingOwnerDetails.get(0).getPurchase_dt()));
                    currentInfos = alt_Impl.present_technicalDetail(getAppl_details().getRegn_no());
                }

                String currentData = currentTechDetails(listExistingOwnerDetails, currentInfos);
                if (currentData != null) {
                    currentdata.put("Vehicle current Technical Detail", currentData);
                }

                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());

                ins_bean.setAppl_no(appl_details.getAppl_no());
                ins_bean.setRegn_no(appl_details.getRegn_no());
                ins_bean.setPur_cd(appl_details.getPur_cd());
                ins_dobj_ret = InsImpl.set_ins_dtls_db_to_dobj(appl_details.getRegn_no(), null, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                ins_dobj_retVA = InsImpl.set_ins_dtls_db_to_dobjVA(appl_details.getRegn_no());
                //start of getting insurance details from service
                InsuranceDetailService detailService = new InsuranceDetailService();
                InsDobj insDobj = detailService.getInsuranceDetailsByService(getAppl_details().getRegn_no(), getAppl_details().getCurrent_state_cd(), getAppl_details().getCurrent_off_cd());
                if (insDobj != null) {
                    if ((DateUtils.compareDates(DateUtils.addToDate(insDobj.getIns_from(), DateUtils.MONTH, -1), appl_details.getOwnerDetailsDobj().getPurchase_date()) != 1)) {
                        if (ins_dobj_ret != null) {
                            insDobj.setIdv(ins_dobj_ret.getIdv());
                        } else if (ins_dobj_retVA != null) {
                            insDobj.setIdv(ins_dobj_retVA.getIdv());
                        }
                        ins_bean.set_Ins_dobj_to_bean(insDobj);
                        ins_bean.setDisable(true);
                        //end of getting insurance details from service 
                    }

                    //for checking insurance availablity and expiration start
                    if (!insDobj.isIibData()) {
                        ins_bean.componentReadOnly(true);
                        ins_bean.setGovtVehFlag(false);
                        if (ins_bean.validateInsurance(ins_dobj_ret, ins_dobj_retVA, false)) {
                            setActiveIndex(0);
                        } else {
                            setActiveIndex(1);
                        }
                    } //for checking insurance availablity and expiration end  
                }
                if (TableConstants.TRACTOR_VEH_CLASS.contains("," + appl_details.getOwnerDetailsDobj().getVh_class() + ",")) {
                    setRenderLinkedVeh(true);
                } else {
                    setRenderLinkedVeh(false);
                }
                if (appl_details.getOwnerDobj() != null && ServerUtil.isTransport(appl_details.getOwnerDobj().getVh_class(), appl_details.getOwnerDobj())) {

                    //state configuration for fitness according to the States rules
                    FitnessImpl fitness_Impl = new FitnessImpl();
                    tmConfigFitnessDobj = fitness_Impl.getFitnessConfiguration(getAppl_details().getCurrent_state_cd());
                    if (tmConfigFitnessDobj != null) {
                        isReflectiveTapeAllowed = tmConfigFitnessDobj.isReflective_tape_allowed();
                    }

                    ownerDobj.setSpeedGovernerList(new ArrayList());
                    data = MasterTableFiller.masterTables.VM_SPEEDGOV_MANUFACTURE.getData();
                    for (int i = 0; i < data.length; i++) {
                        ownerDobj.getSpeedGovernerList().add(new SelectItem(data[i][0], data[i][1]));
                    }

                    ownerDobj.setSpeedGovernorDobj(new FitnessImpl().getSpeedGovernorDetails(getAppl_details().getAppl_no()));
                    ownerDobj.setReflectiveTapeDobj(new FitnessImpl().getVaOrVtReflectiveTapeDobj(getAppl_details().getAppl_no(), null));

                    if (ownerDobj.getReflectiveTapeDobj() != null) {
                        ownerDobj.getReflectiveTapeDobj().setApplNo(appl_details.getAppl_no());
                        renderReflectiveTape = true;
                    } else {
                        ownerDobj.setReflectiveTapeDobj(new FitnessImpl().getVaOrVtReflectiveTapeDobj(null, getAppl_details().getRegn_no()));
                        if (ownerDobj.getReflectiveTapeDobj() != null) {
                            ownerDobj.getReflectiveTapeDobj().setApplNo(appl_details.getAppl_no());
                            renderReflectiveTape = true;
                        } else {
                            renderReflectiveTape = false;
                        }
                    }

                    if (ownerDobj.getSpeedGovernorDobj() != null) {
                        renderSpeedGovt = true;
                    } else {
                        renderSpeedGovt = false;
                    }
                    renderIsReflectiveTape = true;
                    renderIsSpeedGov = true;
                } else {
                    renderIsSpeedGov = false;
                }
                //for displaying data of inspection which is filled by operator
                if (appl_details.getCurrent_action_cd() == TableConstants.INSPECTION_FOR_ALT || appl_details.getCurrent_action_cd() == TableConstants.VM_TRANSACTION_MAST_VEH_ALTER_VERIFY
                        || appl_details.getCurrent_action_cd() == TableConstants.VM_TRANSACTION_MAST_VEH_ALTER_APPROVE) {
                    if (appl_details.getCurrent_action_cd() == TableConstants.INSPECTION_FOR_ALT) {
                        renderInspBody = true;
                    }
                    FitnessImpl fitnessImpl = new FitnessImpl();
                    InspectionDobj inspectionDobj = fitnessImpl.getVaInspectionDobj(appl_details.getAppl_no());
                    if (inspectionDobj != null) {
                        inspDobj = inspectionDobj;
                    }
                    if (inspDobj != null && inspDobj.getInsp_dt() != null) {
                        inspDobjPrev = (InspectionDobj) inspDobj.clone();
                    }
                    if (inspectionDobj != null && (appl_details.getCurrent_action_cd() == TableConstants.VM_TRANSACTION_MAST_VEH_ALTER_VERIFY
                            || appl_details.getCurrent_action_cd() == TableConstants.VM_TRANSACTION_MAST_VEH_ALTER_APPROVE)) {
                        renderInspBody = true;
                        inspDobj.setDisableInsDetail(true);
                    }

                }
                int vehType = ServerUtil.VehicleClassType(appl_details.getOwnerDetailsDobj().getVh_class());
                processAxleDetails(vehType);

            }
        } catch (VahanException e) {
            vahanMessages = e.getMessage();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            vahanMessages = "Something Went Wrong, Please Contact to the System Administrator";
        }
        //RetroFitting Details
        processCngDetails();

    }

    private void processAxleDetails(int vehType) {
        if (vehType == TableConstants.VM_VEHTYPE_TRANSPORT) {
            axleDobj = AxleImpl.setAxleVehDetails_db_to_dobj(getAppl_details().getAppl_no(), null, appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd());
            if (axleDobj != null) {
                axleDetail_Visibility_tab = true;
                axleBean.setDobj_To_Bean(axleDobj);
                axleBean.setAxle_dobj_prv(axleDobj);
            } else {
                axleDobj = AxleImpl.setAxleVehDetails_db_to_dobj(null, getAppl_details().getRegn_no(), appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd());
                if (axleDobj != null) {
                    axleDetail_Visibility_tab = true;
                    axleBean.setDobj_To_Bean(axleDobj);
                    axleBean.setAxle_dobj_prv(axleDobj);
                }
            }
        } else {
            axleDetail_Visibility_tab = false;
        }
    }

    public void validateLadenWeight() {
        if (alt_dobj.getUnld_wt() != 0) {
            if (alt_dobj.getUnld_wt() > alt_dobj.getLd_wt()) {
                alt_dobj.setLd_wt(0);
                JSFUtils.setFacesMessage("Un-Laden Weight Must Less Than Laden Weight", null, JSFUtils.ERROR);
            }
        }
    }

    public List<ComparisonBean> addToComapreChangesList(List<ComparisonBean> compBeanListPrev) throws VahanException {
        List<ComparisonBean> list = compareChanges();
        if (compBeanListPrev == null) {
            compBeanListPrev = new ArrayList<>();
        }
        if (!list.isEmpty()) {
            compBeanListPrev.addAll(list);
        }
        return compBeanListPrev;
    }

    private void processCngDetails() {
        if (alt_dobj.getFuel() == TableConstants.VM_FUEL_CNG_TYPE
                || alt_dobj.getFuel() == TableConstants.VM_FUEL_TYPE_PETROL_CNG
                || alt_dobj.getFuel() == TableConstants.VM_FUEL_TYPE_LPG
                || alt_dobj.getFuel() == TableConstants.VM_FUEL_TYPE_PETROL_LPG) {
            try {
                cng_dobj = RetroFittingDetailsImpl.setCngDetails_db_to_dobj(alt_dobj.getAppl_no());
                if (cng_dobj != null) {
                    cNG_Details_Bean.setDobj_To_Bean(cng_dobj);
                    cNG_Details_Bean.setCng_dobj_prv(cng_dobj);
                    if (cNG_Details_Bean.getCal_install_dt() != null && cNG_Details_Bean.getCal_install_dt().before(cNG_Details_Bean.getMinDate())) {
                        cNG_Details_Bean.setMinDate(appl_details.getOwnerDobj().getPurchase_dt());
                    }
                    if (cNG_Details_Bean.getCal_hydro_dt() != null && cNG_Details_Bean.getCal_hydro_dt().before(cNG_Details_Bean.getMinHydroDate())) {
                        cNG_Details_Bean.setMinHydroDate(cNG_Details_Bean.getCal_hydro_dt());
                    }
                } else {
                    cng_dobj = RetroFittingDetailsImpl.setCngDetails_db_to_dobj_VT(alt_dobj.getRegn_no(), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                    if (cng_dobj != null) {
                        cNG_Details_Bean.setDobj_To_Bean(cng_dobj);
                        cNG_Details_Bean.setCng_dobj_prv(cng_dobj);
                        if (cNG_Details_Bean.getCal_install_dt() != null && cNG_Details_Bean.getCal_install_dt().before(cNG_Details_Bean.getMinDate())) {
                            cNG_Details_Bean.setMinDate(appl_details.getOwnerDobj().getPurchase_dt());
                        }
                        if (cNG_Details_Bean.getCal_hydro_dt() != null && cNG_Details_Bean.getCal_hydro_dt().before(cNG_Details_Bean.getMinHydroDate())) {
                            cNG_Details_Bean.setMinHydroDate(cNG_Details_Bean.getCal_hydro_dt());
                        }
                    }
                }
                setCng_tab_visibility(true);
            } catch (VahanException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
    }

    private String currentTechDetails(List<OwnerDetailsDobj> listExistingOwnerDetails, String[] currentInfos) {
        String currentData = null;
        if (listExistingOwnerDetails != null) {
            currentData = "Chasis No[" + listExistingOwnerDetails.get(0).getChasi_no()
                    + "], Engine No[" + listExistingOwnerDetails.get(0).getEng_no()
                    + "], Body Type[" + listExistingOwnerDetails.get(0).getBody_type()
                    + "], Seating Capacity[" + listExistingOwnerDetails.get(0).getSeat_cap()
                    + "], Standing Capacity[" + listExistingOwnerDetails.get(0).getStand_cap()
                    + "], Sleeper Capacity[" + listExistingOwnerDetails.get(0).getSleeper_cap()
                    + "], No of Cyclinders[" + listExistingOwnerDetails.get(0).getNo_cyl()
                    + "], Unladen Wt[" + listExistingOwnerDetails.get(0).getUnld_wt()
                    + "], Laden Wt[" + listExistingOwnerDetails.get(0).getLd_wt()
                    + "], Horse Power[" + listExistingOwnerDetails.get(0).getHp()
                    + "], Fuel Descr[" + listExistingOwnerDetails.get(0).getFuel_descr()
                    + "], Color[" + listExistingOwnerDetails.get(0).getColor()
                    + "], Wheelbase[" + listExistingOwnerDetails.get(0).getWheelbase()
                    + "], Cubic Capacity[" + listExistingOwnerDetails.get(0).getCubic_cap()
                    + "], Fitness Upto Date[" + listExistingOwnerDetails.get(0).getFit_upto()
                    + "], AC Fitted[" + listExistingOwnerDetails.get(0).getAc_fitted()
                    + "], Audio Fitted[" + listExistingOwnerDetails.get(0).getAudio_fitted()
                    + "], Video Fitted[" + listExistingOwnerDetails.get(0).getVideo_fitted()
                    + "], Length[" + listExistingOwnerDetails.get(0).getLength()
                    + "], Width[" + listExistingOwnerDetails.get(0).getWidth()
                    + "], Height[" + listExistingOwnerDetails.get(0).getHeight() + "],";

        }
        if (currentInfos != null) {
            currentData = currentData.replace(",", "&nbsp; <font color=\"red\">|</font> &nbsp;");
            currentData = currentData + currentInfos[0];
        }
        return currentData;
    }

    public void fuel_TypeSelectEvent() {
        try {
            OwnerImpl owner_Impl = new OwnerImpl();
            int fuel_type = alt_dobj.getFuel();
            if (fuel_type == TableConstants.VM_FUEL_CNG_TYPE
                    || fuel_type == TableConstants.VM_FUEL_TYPE_PETROL_CNG
                    || fuel_type == TableConstants.VM_FUEL_TYPE_LPG
                    || fuel_type == TableConstants.VM_FUEL_TYPE_PETROL_LPG) {
                setCng_tab_visibility(true);
                Owner_dobj owner_dobj = owner_Impl.set_Owner_appl_db_to_dobj_with_state_off_cd(alt_dobj.getRegn_no(), "", "", TableConstants.VM_TRANSACTION_MAST_VEH_ALTER);
                cNG_Details_Bean.setMinDate(owner_dobj.getPurchase_dt());
                TmConfigurationDobj dobj = Util.getTmConfiguration();
                if (dobj.isCnginfo_from_cngmaker()) {
                    if (stateCodeSelected.equals("DL")) {
                        cNG_Details_Bean.setDobj_To_Bean(ServerUtil.getCngMakerInfo(alt_dobj.getRegn_no()));
                    } else {
                        cNG_Details_Bean.setDobj_To_Bean(ServerUtil.getCngMakerInfoforAllState(alt_dobj.getRegn_no()));
                    }
                    if (cNG_Details_Bean.isDisable()) {
                        JSFUtils.setFacesMessage("CNG Kit Info filled from CNG Maker", null, JSFUtils.INFO);
                    } else {
                        alt_dobj.setFuel(appl_details.getOwnerDetailsDobj().getFuel());
                        setCng_tab_visibility(false);
                        JSFUtils.setFacesMessage("CNG Kit Info not available. Please make sure cng is fitted through CNGMAKER", null, JSFUtils.INFO);
                    }
                }
            } else {
                setCng_tab_visibility(false);
            }

        } catch (VahanException e) {
            JSFUtils.setFacesMessage(e.getMessage(), null, JSFUtils.ERROR);
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.setFacesMessage(TableConstants.SomthingWentWrong, null, JSFUtils.ERROR);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.setFacesMessage(TableConstants.SomthingWentWrong, null, JSFUtils.ERROR);
        }
    }

    private boolean validateChasisNo(String oldChasisNo, String newChasisNo) {
        if (oldChasisNo.equalsIgnoreCase(newChasisNo)) {
            return true;
        } else {
            try {
                if (!CommonUtils.isNullOrBlank(alt_dobj.getChasi_no())) {
                    alt_dobj.setChasi_no(alt_dobj.getChasi_no().toUpperCase().trim());
                    OwnerImpl owner_Impl = new OwnerImpl();
                    Owner_dobj owner_dobj = owner_Impl.set_Owner_appl_db_to_dobj_with_state_off_cd(null, null, alt_dobj.getChasi_no(), TableConstants.VM_TRANSACTION_MAST_VEH_ALTER);
                    if (owner_dobj != null) {
                        alt_dobj.setChasi_no("");
                        return false;
                    } else {
                        return true;
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return false;
    }

    public void getLinkedTrailerDetails() {
        try {
            if (!CommonUtils.isNullOrBlank(trailerDobj.getLinkedRegnNo())) {
                AltImpl implObj = new AltImpl();
                implObj.getLinkedTrailerDtls(trailerDobj);
            }
        } catch (VahanException e) {
            trailerDobj.setLinkedRegnNo("");
            JSFUtils.setFacesMessage(e.getMessage(), null, JSFUtils.INFO);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.setFacesMessage(TableConstants.SomthingWentWrong, null, JSFUtils.INFO);
        }
    }

    public void reflectiveTapeListener(AjaxBehaviorEvent event) {
        ReflectiveTapeDobj refTape = null;
        if (renderReflectiveTape) {
            setRenderReflectiveTape(true);
            refTape = new ReflectiveTapeDobj();
            refTape.setApplNo(Util.getSelectedSeat().getAppl_no());
            refTape.setRegn_no(Util.getSelectedSeat().getRegn_no());
            refTape.setStateCd(stateCodeSelected);
            refTape.setOffCd(offCodeSelected);

            getOwnerDobj().setReflectiveTapeDobj(refTape);
        } else {
            setRenderReflectiveTape(false);
            getOwnerDobj().setReflectiveTapeDobj(refTape);
        }

    }

    /**
     * @return the listVhClass
     */
    public List getListVhClass() {
        return listVhClass;
    }

    /**
     * @param listVhClass the listVhClass to set
     */
    public void setListVhClass(List listVhClass) {
        this.listVhClass = listVhClass;
    }

    /**
     * @return the listbodytype
     */
    public List getListbodytype() {
        return listbodytype;
    }

    /**
     * @param listbodytype the listbodytype to set
     */
    public void setListbodytype(List listbodytype) {
        this.listbodytype = listbodytype;
    }

    /**
     * @return the alt_dobj
     */
    public AltDobj getAlt_dobj() {
        return alt_dobj;
    }

    /**
     * @param alt_dobj the alt_dobj to set
     */
    public void setAlt_dobj(AltDobj alt_dobj) {
        this.alt_dobj = alt_dobj;
    }

    /**
     * @return the alt_dobj_prev
     */
    public AltDobj getAlt_dobj_prev() {
        return alt_dobj_prev;
    }

    /**
     * @param prev
     */
    public void setAlt_dobj_prev(AltDobj prev) {
        // make copy the given object.
        this.alt_dobj_prev = prev;

    }

    /**
     * @return the compBeanList
     */
    @Override
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    /**
     * @param compBeanList the compBeanList to set
     */
    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    /**
     * @return the list_fuel
     */
    public List getList_fuel() {
        return list_fuel;
    }

    /**
     * @param list_fuel the list_fuel to set
     */
    public void setList_fuel(List list_fuel) {
        this.list_fuel = list_fuel;
    }

    @Override
    public String save() {
        String return_location = "";
        cng_dobj = null;
        if (validateChasisNo(listExistingOwnerDetails.get(0).getChasi_no(), alt_dobj.getChasi_no())) {
            try {
                List<ComparisonBean> compareChanges = compareChanges();
                InsDobj ins_dobj_new = ins_bean.set_InsBean_to_dobj();
                if (appl_details.getCurrent_action_cd() == TableConstants.INSPECTION_FOR_ALT) {
                    inspDobj.setAppl_no(appl_details.getAppl_no());
                    inspDobj.setRegn_no(appl_details.getRegn_no());
                    inspDobj.setState_cd(getAppl_details().getCurrent_state_cd());
                    inspDobj.setOff_cd(getAppl_details().getCurrent_off_cd());
                    inspDobj.setFit_off_cd1(Integer.parseInt(getAppl_details().getCurrentEmpCd()));
                }
                if (isAxleDetail_Visibility_tab()) {
                    axleDobj = axleBean.setBean_To_Dobj();
                } else {
                    axleDobj = null;
                }
                if (ins_dobj_new != null && ins_bean.validateInsurance(ins_dobj_new)) {
                    if (isCng_tab_visibility()) {
                        cng_dobj = cNG_Details_Bean.setBean_To_Dobj();
                    }

                    //for storing the changed data into vha_changed_data
                    String changedData = ComparisonBeanImpl.changedDataContents(compareChanges);
                    if (trailerModified != null && !trailerModified.equals("")) {
                        changedData = changedData + trailerModified;
                    }
                    //for updating or inserting insurance details with CA details
                    ins_bean.ins_update(appl_details.getOwnerDetailsDobj());
                    if (!compareChanges.isEmpty() || alt_dobj_prev == null || cng_dobj != null || listTrailerDobjPrev == null || !changedData.isEmpty() || (inspDobjPrev == null && appl_details.getCurrent_action_cd() == TableConstants.INSPECTION_FOR_ALT)) { //save only when data is really changed by user and when form is empty
                        AltImpl alt_impl = new AltImpl();
                        alt_impl.makeChange_alteration(alt_dobj, cng_dobj, listTrailerDetailsSave, ownerDobj, listTrailerDetach, changedData, ComparisonBeanImpl.changedDataContents(cNG_Details_Bean.compareChagnes()), renderSpeedGovt, stateCodeSelected, empCodeSelected, inspDobj, axleDobj);
                    }

                    return_location = "seatwork";
                } else {
                    if (ins_dobj_new == null) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Insurance Details not available !!!"));
                    }
                }
            } catch (VahanException vme) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), vme.getMessage()));
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("wb_errorMessages", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Already entry done of this chassis number : " + alt_dobj.getChasi_no(), null));
        }
        return return_location;
    }

    @Override
    public List<ComparisonBean> compareChanges() {

        if (alt_dobj_prev == null || (inspDobjPrev == null
                && appl_details.getCurrent_action_cd() == TableConstants.INSPECTION_FOR_ALT)) {
            return getCompBeanList();
        }
        getCompBeanList().clear();

        Compare("Appl_no", alt_dobj_prev.getAppl_no(), alt_dobj.getAppl_no(), getCompBeanList());
        Compare("Body_type ", alt_dobj_prev.getBody_type(), alt_dobj.getBody_type(), getCompBeanList());
        Compare("Chasi_no", alt_dobj_prev.getChasi_no(), alt_dobj.getChasi_no(), getCompBeanList());
        Compare("Color", alt_dobj_prev.getColor(), alt_dobj.getColor(), getCompBeanList());
        Compare("Cubic_cap", alt_dobj_prev.getCubic_cap(), alt_dobj.getCubic_cap(), getCompBeanList());
        Compare("Eng_no", alt_dobj_prev.getEng_no(), alt_dobj.getEng_no(), getCompBeanList());
        Compare("Floor_area", alt_dobj_prev.getFloor_area(), alt_dobj.getFloor_area(), getCompBeanList());
        Compare("Fuel", alt_dobj_prev.getFuel(), alt_dobj.getFuel(), getCompBeanList());
        Compare("HP", alt_dobj_prev.getHp(), alt_dobj.getHp(), getCompBeanList());
        Compare("LD_WT", alt_dobj_prev.getLd_wt(), alt_dobj.getLd_wt(), getCompBeanList());
        Compare("NO_CYL", alt_dobj_prev.getNo_cyl(), alt_dobj.getNo_cyl(), getCompBeanList());
        Compare("OFF_CD", alt_dobj_prev.getOff_cd(), alt_dobj.getOff_cd(), getCompBeanList());
        Compare("REGN NO", alt_dobj_prev.getRegn_no(), alt_dobj.getRegn_no(), getCompBeanList());
        Compare("SEAT CAP", alt_dobj_prev.getSeat_cap(), alt_dobj.getSeat_cap(), getCompBeanList());
        Compare("SLEEPER CAP", alt_dobj_prev.getSleeper_cap(), alt_dobj.getSleeper_cap(), getCompBeanList());
        Compare("STAND CAP", alt_dobj_prev.getStand_cap(), alt_dobj.getStand_cap(), getCompBeanList());
        Compare("Unld wt", alt_dobj_prev.getUnld_wt(), alt_dobj.getUnld_wt(), getCompBeanList());
        Compare("Wheel Base", alt_dobj_prev.getWheelbase(), alt_dobj.getWheelbase(), getCompBeanList());
        Compare("Vh_class", alt_dobj_prev.getVh_class(), alt_dobj.getVh_class(), getCompBeanList());
        Compare("ac_fitted", alt_dobj_prev.getAc_fitted(), alt_dobj.getAc_fitted(), getCompBeanList());
        Compare("audio_fitted", alt_dobj_prev.getAudio_fitted(), alt_dobj.getAudio_fitted(), getCompBeanList());
        Compare("video_fitted", alt_dobj_prev.getVideo_fitted(), alt_dobj.getVideo_fitted(), getCompBeanList());
        Compare("length", alt_dobj_prev.getLength(), alt_dobj.getLength(), getCompBeanList());
        Compare("width", alt_dobj_prev.getWidth(), alt_dobj.getWidth(), getCompBeanList());
        Compare("height", alt_dobj_prev.getHeight(), alt_dobj.getHeight(), getCompBeanList());
        Compare("fit_upto", alt_dobj_prev.getFit_date_upto(), alt_dobj.getFit_date_upto(), getCompBeanList());
        Compare("vch_catg", alt_dobj_prev.getVch_catg(), alt_dobj.getVch_catg(), getCompBeanList());
        Compare("Push Back Seat", alt_dobj_prev.getPush_bk_seat(), alt_dobj.getPush_bk_seat(), getCompBeanList());
        Compare("Ordinary Seat", alt_dobj_prev.getOrdinary_seat(), alt_dobj.getOrdinary_seat(), getCompBeanList());
        if (trailerDobjPrev != null && trailerDobj != null) {
            Compare("trailer_bd_type", trailerDobjPrev.getBody_type(), trailerDobj.getBody_type(), compBeanList);
            Compare("Trailer Laden Wt", trailerDobjPrev.getLd_wt(), trailerDobj.getLd_wt(), compBeanList);
            Compare("Trailer Unladen Wt", trailerDobjPrev.getUnld_wt(), trailerDobj.getUnld_wt(), compBeanList);
            Compare("f_axle_descp", trailerDobjPrev.getF_axle_descp(), trailerDobj.getF_axle_descp(), compBeanList);
            Compare("r_axle_descp", trailerDobjPrev.getR_axle_descp(), trailerDobj.getR_axle_descp(), compBeanList);
            Compare("o_axle_descp", trailerDobjPrev.getO_axle_descp(), trailerDobj.getO_axle_descp(), compBeanList);
            Compare("t_axle_descp", trailerDobjPrev.getT_axle_descp(), trailerDobj.getT_axle_descp(), compBeanList);
            Compare("f_axle_weight", trailerDobjPrev.getF_axle_weight(), trailerDobj.getF_axle_weight(), compBeanList);
            Compare("r_axle_weight", trailerDobjPrev.getR_axle_weight(), trailerDobj.getR_axle_weight(), compBeanList);
            Compare("o_axle_weight", trailerDobjPrev.getO_axle_weight(), trailerDobj.getO_axle_weight(), compBeanList);
            Compare("t_axle_weight", trailerDobjPrev.getT_axle_weight(), trailerDobj.getT_axle_weight(), compBeanList);
        }
        if (inspDobj != null && inspDobj.getInsp_dt() != null) {
            Compare("INSP_REMARK", inspDobjPrev.getRemark(), inspDobj.getRemark(), compBeanList);
            Compare("W_E_From", inspDobjPrev.getInsp_dt(), inspDobj.getInsp_dt(), compBeanList);
        }
        return getCompBeanList();

    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        InsDobj ins_dobj_new = null;
        ins_dobj_new = ins_bean.set_InsBean_to_dobj();
        if (validateChasisNo(listExistingOwnerDetails.get(0).getChasi_no(), alt_dobj.getChasi_no())
                && ins_dobj_new != null) {
            List<ComparisonBean> compareChanges = compareChanges();
            try {
                if (!getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    if (ins_bean.validateInsurance(ins_dobj_new)) {
                        ins_bean.ins_update(appl_details.getOwnerDetailsDobj());
                    } else {
                        return return_location;
                    }
                }

                //for storing the changed data into vha_changed_data
                String changedData = ComparisonBeanImpl.changedDataContents(compareChanges);
                if (trailerModified != null && !trailerModified.equals("")) {
                    changedData = changedData + trailerModified;
                }
                if (isCng_tab_visibility()) {
                    cng_dobj = cNG_Details_Bean.setBean_To_Dobj();
                }
                if (isAxleDetail_Visibility_tab()) {
                    axleDobj = axleBean.setBean_To_Dobj();
                } else {
                    axleDobj = null;
                }

                Status_dobj status = new Status_dobj();
                status.setState_cd(sessionVariables.getStateCodeSelected());
                status.setOff_cd(sessionVariables.getOffCodeSelected());
                status.setAppl_dt(appl_details.getAppl_dt());
                status.setAppl_no(appl_details.getAppl_no());
                status.setPur_cd(appl_details.getPur_cd());
                status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
                status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
                status.setStatus(getApp_disapp_dobj().getNew_status());
                status.setCurrent_role(appl_details.getCurrent_role());
                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                        || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)
                        || getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)
                        || appl_details.getCurrent_action_cd() == TableConstants.INSPECTION_FOR_ALT) {

                    status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                    status.setVehicleParameters(appl_details.getVehicleParameters());
                    if (appl_details.getCurrent_action_cd() == TableConstants.INSPECTION_FOR_ALT) {
                        inspDobj.setAppl_no(appl_details.getAppl_no());
                        inspDobj.setRegn_no(appl_details.getRegn_no());
                        inspDobj.setState_cd(getAppl_details().getCurrent_state_cd());
                        inspDobj.setOff_cd(getAppl_details().getCurrent_off_cd());
                        inspDobj.setFit_off_cd1(Integer.parseInt(getAppl_details().getCurrentEmpCd()));
                    }
                    AltImpl alt_impl = new AltImpl();
                    if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE) && this.appl_details.isDocVerifyForFacelessAppl()) {
                        String[] notVerifiedDocDetails = ServerUtil.verifyDocUploaded(appl_details.getAppl_no(), getAppl_details().getCurrent_action_cd());
                        if (notVerifiedDocDetails != null) {
                            appl_details.setNotVerifiedDocdetails(notVerifiedDocDetails[1]);
                            throw new VahanException(notVerifiedDocDetails[0]);
                        }
                    }
                    alt_impl.update_ALT_Status(alt_dobj, cng_dobj, listTrailerDetailsSave, listTrailerDetach,
                            status, changedData, ComparisonBeanImpl.changedDataContents(cNG_Details_Bean.compareChagnes()),
                            ownerDobj, renderSpeedGovt, stateCodeSelected, empCodeSelected, axleDobj, inspDobj, inspDobjPrev);

                    if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                        ServerUtil.sendSmsInFacelessService(appl_details.getAppl_no(), Util.getUserStateCode(), Util.getUserSeatOffCode(), appl_details.getRegn_no(), appl_details.getNotVerifiedDocdetails());
                        return_location = disapprovalPrint();
                    } else {
                        return_location = "seatwork";
                    }
                }
                if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_COMPLETE) && appl_details.getCurrent_role() == 4) {
                    if (!ServerUtil.getDataEntryIncomplete(appl_details.getAppl_no())) {
                        PrimeFaces.current().ajax().update("app_disapp_new_form:showOwnerDiscPopup");
                        PrimeFaces.current().executeScript("PF('successDialog').show()");
                        // RequestContext.getCurrentInstance().update(":app_disapp_form:showOwnerDiscPopup");
                        //RequestContext.getCurrentInstance().execute("PF('successDialog').show()");
                        return_location = "";
                    }
                }
            } catch (VahanException vme) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), vme.getMessage()));
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
            }

        } else {
            if (!validateChasisNo(listExistingOwnerDetails.get(0).getChasi_no(), alt_dobj.getChasi_no())) {
                FacesContext.getCurrentInstance().addMessage("wb_errorMessages", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Already entry done of this chassis number : " + alt_dobj.getChasi_no(), null));
            }
        }

        return return_location;
    }

    public String printDisclaimer() {
        return PrintDocImpl.printOwnerDiscReport("registeredVehicles", "reportFormat");
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

    /**
     * @return the cng_tab_visibility
     */
    public boolean isCng_tab_visibility() {
        return cng_tab_visibility;
    }

    /**
     * @param cng_tab_visibility the cng_tab_visibility to set
     */
    public void setCng_tab_visibility(boolean cng_tab_visibility) {
        this.cng_tab_visibility = cng_tab_visibility;
    }

    /**
     * @return the cNG_Details_Bean
     */
    public RetroFittingDetailsBean getcNG_Details_Bean() {
        return cNG_Details_Bean;
    }

    /**
     * @param cNG_Details_Bean the cNG_Details_Bean to set
     */
    public void setcNG_Details_Bean(RetroFittingDetailsBean cNG_Details_Bean) {
        this.cNG_Details_Bean = cNG_Details_Bean;
    }

    /**
     * @return the list_vm_catg
     */
    public List getList_vm_catg() {
        return list_vm_catg;
    }

    /**
     * @param list_vm_catg the list_vm_catg to set
     */
    public void setList_vm_catg(List list_vm_catg) {
        this.list_vm_catg = list_vm_catg;
    }

    /**
     * @return the listExistingOwnerDetails
     */
    public List<OwnerDetailsDobj> getListExistingOwnerDetails() {
        return listExistingOwnerDetails;
    }

    /**
     * @param listExistingOwnerDetails the listExistingOwnerDetails to set
     */
    public void setListExistingOwnerDetails(List<OwnerDetailsDobj> listExistingOwnerDetails) {
        this.listExistingOwnerDetails = listExistingOwnerDetails;
    }

    /**
     * @return the ins_bean
     */
    public InsBean getIns_bean() {
        return ins_bean;
    }

    /**
     * @param ins_bean the ins_bean to set
     */
    public void setIns_bean(InsBean ins_bean) {
        this.ins_bean = ins_bean;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    /**
     * @return the trailerDobj
     */
    public Trailer_dobj getTrailerDobj() {
        return trailerDobj;
    }

    /**
     * @param trailerDobj the trailerDobj to set
     */
    public void setTrailerDobj(Trailer_dobj trailerDobj) {
        this.trailerDobj = trailerDobj;
    }

    /**
     * @return the listTrailerDetails
     */
    public List<Trailer_dobj> getListTrailerDetails() {
        return listTrailerDetails;
    }

    /**
     * @param listTrailerDetails the listTrailerDetails to set
     */
    public void setListTrailerDetails(List<Trailer_dobj> listTrailerDetails) {
        this.listTrailerDetails = listTrailerDetails;
    }

    /**
     * @return the listTrailerDetach
     */
    public List<Trailer_dobj> getListTrailerDetach() {
        return listTrailerDetach;
    }

    /**
     * @param listTrailerDetach the listTrailerDetach to set
     */
    public void setListTrailerDetach(List<Trailer_dobj> listTrailerDetach) {
        this.listTrailerDetach = listTrailerDetach;
    }

    public void modifyTrailer(Trailer_dobj trailerEditDobj, int editIndex) {
        index = editIndex;
        indexSave = getIndexOfSavingList(trailerEditDobj, listTrailerDetailsSave);
        Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        editOrAdd = map.get("key");
        if (trailerEditDobj != null) {
            this.trailerDobj.setAppl_no(appl_details.getAppl_no());
            this.trailerDobj.setRegn_no(appl_details.getRegn_no());
            this.trailerDobj.setChasi_no(trailerEditDobj.getChasi_no());
            this.trailerDobj.setBody_type(trailerEditDobj.getBody_type());
            this.trailerDobj.setLd_wt(trailerEditDobj.getLd_wt());
            this.trailerDobj.setUnld_wt(trailerEditDobj.getUnld_wt());
            this.trailerDobj.setF_axle_descp(trailerEditDobj.getF_axle_descp());
            this.trailerDobj.setR_axle_descp(trailerEditDobj.getR_axle_descp());
            this.trailerDobj.setO_axle_descp(trailerEditDobj.getO_axle_descp());
            this.trailerDobj.setT_axle_descp(trailerEditDobj.getT_axle_descp());
            this.trailerDobj.setF_axle_weight(trailerEditDobj.getF_axle_weight());
            this.trailerDobj.setR_axle_weight(trailerEditDobj.getR_axle_weight());
            this.trailerDobj.setO_axle_weight(trailerEditDobj.getO_axle_weight());
            this.trailerDobj.setT_axle_weight(trailerEditDobj.getT_axle_weight());
            this.trailerDobj.setSrNo(trailerEditDobj.getSrNo());
            this.trailerDobj.setModifiable(trailerEditDobj.isModifiable());
            this.trailerDobj.setLinkedRegnNo(trailerEditDobj.getLinkedRegnNo());
        }
    }

    public void detachTrailer(Trailer_dobj trailerDobj, int index) {
        listTrailerDetails.remove(index);
        listTrailerDetach.add(trailerDobj);
    }

    public void attachTrailer() {
        if (trailerDobj != null && !trailerDobj.getChasi_no().isEmpty()) {
            if (editOrAdd.equalsIgnoreCase("add")) {
                Trailer_dobj trailer_Dobj = new Trailer_dobj();
                trailer_Dobj.setAppl_no(appl_details.getAppl_no());
                trailer_Dobj.setRegn_no(appl_details.getRegn_no());
                trailer_Dobj.setChasi_no(trailerDobj.getChasi_no());
                trailer_Dobj.setBody_type(trailerDobj.getBody_type());
                trailer_Dobj.setLd_wt(trailerDobj.getLd_wt());
                trailer_Dobj.setUnld_wt(trailerDobj.getUnld_wt());
                trailer_Dobj.setF_axle_descp(trailerDobj.getF_axle_descp());
                trailer_Dobj.setR_axle_descp(trailerDobj.getR_axle_descp());
                trailer_Dobj.setO_axle_descp(trailerDobj.getO_axle_descp());
                trailer_Dobj.setT_axle_descp(trailerDobj.getT_axle_descp());
                trailer_Dobj.setF_axle_weight(trailerDobj.getF_axle_weight());
                trailer_Dobj.setR_axle_weight(trailerDobj.getR_axle_weight());
                trailer_Dobj.setO_axle_weight(trailerDobj.getO_axle_weight());
                trailer_Dobj.setT_axle_weight(trailerDobj.getT_axle_weight());
                trailer_Dobj.setLinkedRegnNo(trailerDobj.getLinkedRegnNo());
                trailer_Dobj.setDisable(trailerDobj.isDisable());
                trailer_Dobj.setModifiable(true);
                listTrailerDetails.add(trailer_Dobj);
                listTrailerDetailsSave.add(trailer_Dobj);
            } else if (editOrAdd.equalsIgnoreCase("edit")) {
                Trailer_dobj trailer_Dobj = new Trailer_dobj();
                trailer_Dobj.setAppl_no(appl_details.getAppl_no());
                trailer_Dobj.setRegn_no(appl_details.getRegn_no());
                trailer_Dobj.setChasi_no(trailerDobj.getChasi_no());
                trailer_Dobj.setBody_type(trailerDobj.getBody_type());
                trailer_Dobj.setLd_wt(trailerDobj.getLd_wt());
                trailer_Dobj.setUnld_wt(trailerDobj.getUnld_wt());
                trailer_Dobj.setF_axle_descp(trailerDobj.getF_axle_descp());
                trailer_Dobj.setR_axle_descp(trailerDobj.getR_axle_descp());
                trailer_Dobj.setO_axle_descp(trailerDobj.getO_axle_descp());
                trailer_Dobj.setT_axle_descp(trailerDobj.getT_axle_descp());
                trailer_Dobj.setF_axle_weight(trailerDobj.getF_axle_weight());
                trailer_Dobj.setR_axle_weight(trailerDobj.getR_axle_weight());
                trailer_Dobj.setO_axle_weight(trailerDobj.getO_axle_weight());
                trailer_Dobj.setT_axle_weight(trailerDobj.getT_axle_weight());
                trailer_Dobj.setSrNo(trailerDobj.getSrNo());
                trailer_Dobj.setLinkedRegnNo(trailerDobj.getLinkedRegnNo());
                trailer_Dobj.setModifiable(trailerDobj.isModifiable());
                trailer_Dobj.setDisable(trailerDobj.isDisable());
                listTrailerDetails.remove(index);
                listTrailerDetailsSave.remove(indexSave);
                listTrailerDetails.add(index, trailer_Dobj);
                listTrailerDetailsSave.add(indexSave, trailer_Dobj);
                trailerModified = "Trailer Modified.";
            }
            PrimeFaces.current().executeScript("PF('trailerDtls').hide()");
        } else {
            JSFUtils.showMessagesInDialog("Important", "Blank Chassis Number", FacesMessage.SEVERITY_ERROR);
        }
    }

    public void addTrailerListener() {
        Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        editOrAdd = map.get("key");
        resetTrailer();
        if (TableConstants.TRACTOR_VEH_CLASS.contains("," + alt_dobj.getVh_class() + ",")) {
            setRenderLinkedVeh(true);
        } else {
            setRenderLinkedVeh(false);
        }
        PrimeFaces.current().executeScript("PF('trailerDtls').show()");
    }

    public void resetTrailer() {
        trailerDobj.setChasi_no("");
        trailerDobj.setBody_type("");
        trailerDobj.setLd_wt(0);
        trailerDobj.setUnld_wt(0);
        trailerDobj.setF_axle_descp("");
        trailerDobj.setR_axle_descp("");
        trailerDobj.setO_axle_descp("");
        trailerDobj.setT_axle_descp("");
        trailerDobj.setF_axle_weight(0);
        trailerDobj.setR_axle_weight(0);
        trailerDobj.setO_axle_weight(0);
        trailerDobj.setT_axle_weight(0);
        trailerDobj.setLinkedRegnNo("");
        trailerDobj.setDisable(false);
    }

    private int getIndexOfSavingList(Trailer_dobj compareDobj, List<Trailer_dobj> listTrailerDetailsSave) {
        int index = 0;
        if (!listTrailerDetailsSave.isEmpty()) {
            for (Trailer_dobj list_dobj : listTrailerDetailsSave) {
                if (compareDobj(compareDobj, list_dobj)) {
                    break;
                }
                index++;
            }
        }
        return index;
    }

    private boolean compareDobj(Trailer_dobj compDobj, Trailer_dobj listDobj) {
        boolean flag = false;
        if (compDobj.getChasi_no().equalsIgnoreCase(listDobj.getChasi_no())
                && compDobj.getBody_type().equalsIgnoreCase(listDobj.getBody_type())
                && compDobj.getUnld_wt() == listDobj.getUnld_wt()
                && compDobj.getLd_wt() == listDobj.getLd_wt()
                && compDobj.getF_axle_descp().equalsIgnoreCase(listDobj.getF_axle_descp())
                && compDobj.getR_axle_descp().equalsIgnoreCase(listDobj.getR_axle_descp())
                && compDobj.getO_axle_descp().equalsIgnoreCase(listDobj.getO_axle_descp())
                && compDobj.getT_axle_descp().equalsIgnoreCase(listDobj.getT_axle_descp())
                && compDobj.getF_axle_weight() == listDobj.getF_axle_weight()
                && compDobj.getR_axle_weight() == listDobj.getR_axle_weight()
                && compDobj.getO_axle_weight() == listDobj.getO_axle_weight()
                && compDobj.getT_axle_weight() == listDobj.getT_axle_weight()) {
            flag = true;
        }
        return flag;
    }

    public void speedGovListener(AjaxBehaviorEvent event) {
        SpeedGovernorDobj sg = null;
        if (renderSpeedGovt) {
            setRenderSpeedGovt(true);
            sg = new SpeedGovernorDobj();
            sg.setAppl_no(appl_details.getAppl_no());
            sg.setRegn_no(appl_details.getRegn_no());
            sg.setState_cd(sessionVariables.getStateCodeSelected());
            sg.setOff_cd(sessionVariables.getOffCodeSelected());
            ownerDobj.setSpeedGovernorDobj(sg);
        } else {
            setRenderSpeedGovt(false);
            ownerDobj.setSpeedGovernorDobj(sg);
        }
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
     * @return the renderLinkedVeh
     */
    public boolean isRenderLinkedVeh() {
        return renderLinkedVeh;
    }

    /**
     * @param renderLinkedVeh the renderLinkedVeh to set
     */
    public void setRenderLinkedVeh(boolean renderLinkedVeh) {
        this.renderLinkedVeh = renderLinkedVeh;
    }

    /**
     * @return the linkedVehDtls
     */
    public String getLinkedVehDtls() {
        return linkedVehDtls;
    }

    /**
     * @param linkedVehDtls the linkedVehDtls to set
     */
    public void setLinkedVehDtls(String linkedVehDtls) {
        this.linkedVehDtls = linkedVehDtls;
    }

    public boolean isRenderIsSpeedGov() {
        return renderIsSpeedGov;
    }

    public void setRenderIsSpeedGov(boolean renderIsSpeedGov) {
        this.renderIsSpeedGov = renderIsSpeedGov;
    }

    public boolean isRenderSpeedGovt() {
        return renderSpeedGovt;
    }

    public void setRenderSpeedGovt(boolean renderSpeedGovt) {
        this.renderSpeedGovt = renderSpeedGovt;
    }

    /**
     * @return the ownerDobj
     */
    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    /**
     * @param ownerDobj the ownerDobj to set
     */
    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    public boolean isRenderIsReflectiveTape() {
        return renderIsReflectiveTape;
    }

    public void setRenderIsReflectiveTape(boolean renderIsReflectiveTape) {
        this.renderIsReflectiveTape = renderIsReflectiveTape;
    }

    public boolean isRenderReflectiveTape() {
        return renderReflectiveTape;
    }

    public void setRenderReflectiveTape(boolean renderReflectiveTape) {
        this.renderReflectiveTape = renderReflectiveTape;
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

    public AxleDetailsDobj getAxleDobj() {
        return axleDobj;
    }

    public void setAxleDobj(AxleDetailsDobj axleDobj) {
        this.axleDobj = axleDobj;
    }

    public boolean isAxleDetail_Visibility_tab() {
        return axleDetail_Visibility_tab;
    }

    public void setAxleDetail_Visibility_tab(boolean axleDetail_Visibility_tab) {
        this.axleDetail_Visibility_tab = axleDetail_Visibility_tab;
    }

    public AxleBean getAxleBean() {
        return axleBean;
    }

    public void setAxleBean(AxleBean axleBean) {
        this.axleBean = axleBean;
    }

    public InspectionDobj getInspDobj() {
        return inspDobj;
    }

    public void setInspDobj(InspectionDobj inspDobj) {
        this.inspDobj = inspDobj;
    }

    public InspectionDobj getInspDobjPrev() {
        return inspDobjPrev;
    }

    public void setInspDobjPrev(InspectionDobj inspDobjPrev) {
        this.inspDobjPrev = inspDobjPrev;
    }

    public boolean isRenderInspBody() {
        return renderInspBody;
    }

    public void setRenderInspBody(boolean renderInspBody) {
        this.renderInspBody = renderInspBody;
    }
}
