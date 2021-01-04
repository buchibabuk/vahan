/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import nic.rto.vahan.common.VahanException;
import static nic.vahan.CommonUtils.FormulaUtils.fillVehicleParametersFromDobj;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.VmValidityMastDobj;
import nic.vahan.form.dobj.fitness.TmConfigurationFitnessDobj;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
import org.primefaces.component.selectmanycheckbox.SelectManyCheckbox;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author tranC094
 */
@ManagedBean(name = "fitness_bean")
@ViewScoped
public class FitnessBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(FitnessBean.class);
    private SelectManyCheckbox parameters = new SelectManyCheckbox();
    private List parameters_list = new ArrayList();
    private Map<String, Integer> fit_off_cd1_list;
    private Map<String, Integer> fit_off_cd2_list;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private FitnessDobj fitnessDobj = new FitnessDobj();
    private FitnessDobj fitness_dobj_prv;
    private Date maxDate = new Date();
    private boolean isMultipleFitnessOfficer;
    private TmConfigurationDobj tmConfigurationDobj;
    private TmConfigurationFitnessDobj tmConfigFitnessDobj;
    private boolean disableFitnessOfficer1;
    private SessionVariables sessionVariables = null;

    public FitnessBean() {
        String msg = "No Message";
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null
                    || sessionVariables.getStateCodeSelected() == null
                    || (sessionVariables.getOffCodeSelected() == 0 && !Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_ADMIN))//forstateadmin
                    || sessionVariables.getEmpCodeLoggedIn() == null) {
                msg = "Something went wrong, Please try again...";
                throw new VahanException(msg);
            }

            if (sessionVariables.getActionCodeSelected() != TableConstants.TM_NEW_RC_FITNESS_INSPECTION
                    && sessionVariables.getActionCodeSelected() != TableConstants.TM_NEW_RC_FITNESS_INSPECTION_APPROVAL
                    && sessionVariables.getActionCodeSelected() != TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION
                    && sessionVariables.getActionCodeSelected() != TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION_VERIFICATION
                    && sessionVariables.getActionCodeSelected() != TableConstants.TM_NEW_VEHICLE_FITNESS_INSPECTION_APPROVAL) {
                return;
            }

            tmConfigurationDobj = Util.getTmConfiguration();
            FitnessImpl fitness_Impl = new FitnessImpl();
            //state configuration for fitness according to the States rules
            tmConfigFitnessDobj = fitness_Impl.getFitnessConfiguration(tmConfigurationDobj.getState_cd());
            if (tmConfigFitnessDobj != null) {
                isMultipleFitnessOfficer = tmConfigFitnessDobj.isMultiple_fit_officer();
            }
            fit_off_cd1_list = ServerUtil.getFitOfficerList(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());

            if (fit_off_cd1_list == null || fit_off_cd1_list.isEmpty()) {
                msg = "No Fitness/Inspection Officer found in this office, Please Contact to the System Administrator";
                throw new VahanException(msg);
            }
            if (isMultipleFitnessOfficer) {
                fit_off_cd2_list = fit_off_cd1_list;
            }

            if (!isMultipleFitnessOfficer && tmConfigFitnessDobj != null
                    && tmConfigFitnessDobj.isLogged_in_user_is_fitness_officer()) {
                if (sessionVariables.getActionCodeSelected() != 0 && sessionVariables.getActionCodeSelected() == TableConstants.TM_NEW_RC_FITNESS_INSPECTION) {
                    if (!fit_off_cd1_list.containsValue(Integer.parseInt(sessionVariables.getEmpCodeLoggedIn()))) {
                        msg = "You are not Authorized Fitness Officer, Please contact to the system administrator";
                        throw new VahanException(msg);
                    }
                    fit_off_cd1_list.clear();
                    fit_off_cd1_list.put(ServerUtil.getUserName(Long.parseLong(sessionVariables.getEmpCodeLoggedIn())), Integer.parseInt(sessionVariables.getEmpCodeLoggedIn()));
                } else {
                    disableFitnessOfficer1 = true;
                }
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
        } catch (VahanException vex) {
            msg = vex.getMessage();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
        } catch (Exception ex) {
            msg = "(Fitness of New Registration) Technical Error in Database.Please Contact to the Administrator!!!";
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
        }
    }

    ////  reading and display fitness check parameter is not avaialabe at this time.
    //// DOBJ have to modified for this facility
    //// Discussion is required for this thing.
    public void set_Fitness_appl_dobj_to_bean(FitnessDobj fitness_dobj) {

        fitnessDobj.setAppl_no(fitness_dobj.getAppl_no());
        fitnessDobj.setChasi_no(fitness_dobj.getChasi_no());
        fitnessDobj.setFare_mtr_no(fitness_dobj.getFare_mtr_no());
        fitnessDobj.setFit_chk_dt(fitness_dobj.getFit_chk_dt());
        fitnessDobj.setFit_chk_tm(fitness_dobj.getFit_chk_tm());
        fitnessDobj.setFit_off_cd1(fitness_dobj.getFit_off_cd1());
        fitnessDobj.setFit_off_cd2(fitness_dobj.getFit_off_cd2());
        fitnessDobj.setFit_result(fitness_dobj.getFit_result());
        fitnessDobj.setFit_valid_to(fitness_dobj.getFit_valid_to());
        fitnessDobj.setFit_nid(fitness_dobj.getFit_nid());
        fitnessDobj.setPucc_no(fitness_dobj.getPucc_no());
        fitnessDobj.setPucc_val(fitness_dobj.getPucc_val());
        fitnessDobj.setRegn_no(fitness_dobj.getRegn_no());
        fitnessDobj.setRemark(fitness_dobj.getRemark());
        ArrayList list = fitness_dobj.getList_parameters();
        ArrayList newlist = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).toString().equals("Y")) {
                newlist.add(i + 1);
            }
        }

        this.parameters.setSelectedValues(newlist.toArray());
    }

    public Object[] makeDobjFromBean() {
        FitnessDobj dobj = new FitnessDobj();
        Object[] returnValue = new Object[2];
        dobj.setFit_chk_dt(fitnessDobj.getFit_chk_dt());
        if (fitnessDobj.getFit_chk_dt() != null) {
            dobj.setFit_chk_tm(fitnessDobj.getFit_chk_dt().toString().substring(11, 19));
        }
        if (fitnessDobj.getFit_result() != null) {
            dobj.setFit_result(fitnessDobj.getFit_result());
        }
        if (fitnessDobj.getFit_valid_to() != null) {
            dobj.setFit_valid_to(fitnessDobj.getFit_valid_to());
        }

        if (fitnessDobj.getFit_nid() != null) {
            dobj.setFit_nid(fitnessDobj.getFit_nid());
        }

        dobj.setFit_off_cd1(fitnessDobj.getFit_off_cd1());


        if (isMultipleFitnessOfficer) {
            dobj.setFit_off_cd2(fitnessDobj.getFit_off_cd2());
        }

        if (fitnessDobj.getRemark() != null) {
            dobj.setRemark(fitnessDobj.getRemark());
        }

        if (fitnessDobj.getPucc_no() != null) {
            dobj.setPucc_no(fitnessDobj.getPucc_no());
        }

        if (fitnessDobj.getPucc_val() != null) {
            dobj.setPucc_val(fitnessDobj.getPucc_val());
        }

        if (fitnessDobj.getFare_mtr_no() != null) {
            dobj.setFare_mtr_no(fitnessDobj.getFare_mtr_no());
        }

        ArrayList allYesNoList = new ArrayList();
        for (int i = 0; i < parameters_list.size(); i++) {
            allYesNoList.add("N");

        }

        if (parameters != null) {
            for (Object selectedItemCode : parameters.getSelectedValues()) {

                for (int i = 0; i < parameters_list.size(); i++) {
                    String selectedValue = (String) ((SelectItem) parameters_list.get(i)).getValue();
                    if (selectedValue.equalsIgnoreCase((String) selectedItemCode)) {
                        allYesNoList.set(i, "Y");
                        break;
                    }

                }

            }
            returnValue[0] = dobj;               /// Now this function needs to return Fitness Dobj and list of Y/N of fitness check
            returnValue[1] = allYesNoList;        /// so we are returning object array containing fitness_dobj at 0 and fitness check list at 1 index
        }

        return returnValue;

    }

    public void inspDateChangeListener(SelectEvent event) {
        Date inspDate = (Date) event.getObject();
        OwnerBean owner_bean = (OwnerBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("owner_bean");
        Date fitValidTo = null;
        Date fitNid = null;
        try {
            if (owner_bean != null) {
                Owner_dobj owdobj = owner_bean.set_Owner_appl_bean_to_dobj();
                FitnessImpl fitnessImpl = new FitnessImpl();
                VehicleParameters vehicleParameters = fillVehicleParametersFromDobj(owdobj);
                VmValidityMastDobj validityMastDobj = fitnessImpl.getVmValidityMastDobj(vehicleParameters, TableConstants.VM_TRANSACTION_MAST_FIT_CERT, owdobj.getState_cd());
                if (validityMastDobj == null || validityMastDobj.getRe_new_value() == 0) {
                    throw new VahanException("Fitness is not Allowed due to Condition Formula is not Matched with the State Condtions.");
                } else {

                    if (!(TableConstants.MODE_YEARLY
                            + TableConstants.MODE_HALF_YEARLY
                            + TableConstants.MODE_QUARTER
                            + TableConstants.MODE_MONTHLY).contains(validityMastDobj.getMod())) {
                        throw new VahanException("There is no valid configuration for valid fitness upto for this vehicle class, Please contact to the system administrator.");
                    }


                    if (validityMastDobj.getMod().equalsIgnoreCase(TableConstants.MODE_YEARLY)) {
                        if ("WB".contains(sessionVariables.getStateCodeSelected()) && owdobj.getFit_upto() != null && owdobj.getFit_upto().after(new Date())
                                && (owdobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE) || owdobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))) {
                            fitValidTo = owdobj.getFit_upto();
                            fitNid = ServerUtil.dateRange(fitValidTo, 0, 0, tmConfigurationDobj.getNid_days());
                        } else {
                            fitValidTo = ServerUtil.dateRange(inspDate, validityMastDobj.getNew_value(), 0, -1);
                            fitNid = ServerUtil.dateRange(inspDate, validityMastDobj.getNew_value(), 0, tmConfigurationDobj.getNid_days());

                        }
                    } else if (validityMastDobj.getMod().equalsIgnoreCase(TableConstants.MODE_HALF_YEARLY)
                            || validityMastDobj.getMod().equalsIgnoreCase(TableConstants.MODE_QUARTER)
                            || validityMastDobj.getMod().equalsIgnoreCase(TableConstants.MODE_MONTHLY)) {
                        if ("WB".contains(sessionVariables.getStateCodeSelected()) && owdobj.getFit_upto() != null && owdobj.getFit_upto().after(new Date())
                                && (owdobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_STATE_VEHICLE) || owdobj.getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_OTHER_DISTRICT_VEHICLE))) {
                            fitValidTo = owdobj.getFit_upto();
                            fitNid = ServerUtil.dateRange(fitValidTo, 0, 0, tmConfigurationDobj.getNid_days());
                        } else {
                            fitValidTo = ServerUtil.dateRange(inspDate, 0, validityMastDobj.getNew_value(), -1);
                            fitNid = ServerUtil.dateRange(inspDate, 0, validityMastDobj.getNew_value(), tmConfigurationDobj.getNid_days());
                        }
                    }

                }
                fitnessDobj.setFit_valid_to(fitValidTo);
                fitnessDobj.setFit_nid(fitNid);
            }

            if (fitValidTo == null) {
                throw new VahanException("Fitness Valid Upto Can not be Empty,Please Select Fitness/Inpection Test Date again for Calculation of Fitness Valid Upto.");
            }
        } catch (VahanException vmex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vmex.getMessage(), vmex.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public List<ComparisonBean> addToComapreChangesList(ArrayList<ComparisonBean> compBeanListPrev) throws VahanException {

        List<ComparisonBean> list = compareChagnes();

        if (compBeanListPrev == null) {
            compBeanListPrev = new ArrayList<>();
        }
        if (list.size() > 0) {
            compBeanListPrev.addAll(list);
        }

        return compBeanListPrev;
    }

    public List<ComparisonBean> compareChagnes() throws VahanException {

        if (fitness_dobj_prv == null) {
            return compBeanList;
        }
        compBeanList.clear();

        Compare("Check Fit Date", fitness_dobj_prv.getFit_chk_dt(), fitnessDobj.getFit_chk_dt(), compBeanList);
        Compare("Fitness check time", fitness_dobj_prv.getFit_chk_tm(), fitnessDobj.getFit_chk_tm(), compBeanList);
        Compare("Pucc No", fitness_dobj_prv.getPucc_no(), fitnessDobj.getPucc_no(), compBeanList);
        Compare("Pucc Validity", fitness_dobj_prv.getPucc_val(), fitnessDobj.getPucc_val(), compBeanList);
        Compare("Inspected by fitOfficer 1", fitness_dobj_prv.getFit_off_cd1(), fitnessDobj.getFit_off_cd1(), compBeanList);
        if (isMultipleFitnessOfficer) {
            Compare("Inspected by fitOfficer 2", fitness_dobj_prv.getFit_off_cd2(), fitnessDobj.getFit_off_cd2(), compBeanList);
        }
        Compare("Fitness Result", fitness_dobj_prv.getFit_result(), fitnessDobj.getFit_result(), compBeanList);
        Compare("Fare Meter No", fitness_dobj_prv.getFare_mtr_no(), fitnessDobj.getFare_mtr_no(), compBeanList);
        Compare("Remark", fitness_dobj_prv.getRemark(), fitnessDobj.getRemark(), getCompBeanList());
        Compare("Fitness Valid Upto", fitness_dobj_prv.getFit_valid_to(), fitnessDobj.getFit_valid_to(), compBeanList);

        if (fitnessDobj.getFit_nid() != null) {
            Compare("NID", fitness_dobj_prv.getFit_nid(), fitnessDobj.getFit_nid(), compBeanList);
        }
        ArrayList fitCheckList = fitness_dobj_prv.getList_parameters();
        String[] selectedValues = (String[]) parameters.getSelectedValues();
        String parmValue = null;
        String parmLabel = null;
        for (int i = 0, j = 0; i < fitCheckList.size(); i++) {

            parmValue = (String) ((SelectItem) parameters_list.get(i)).getValue();
            parmLabel = (String) ((SelectItem) parameters_list.get(i)).getLabel();

            if (selectedValues != null && selectedValues.length > j) {
                if (Integer.parseInt(parmValue) == Integer.parseInt(selectedValues[j])) {
                    if (fitCheckList.get(i).equals("N")) {
                        Compare(parmLabel, "N", "Y", getCompBeanList());
                    }
                    j++;
                } else {
                    if (fitCheckList.get(i).equals("Y")) {
                        Compare(parmLabel, "Y", "N", getCompBeanList());
                    }
                }
            }
        }

        return compBeanList;

    }

    public void validateFitnessOfficer(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value != null && fitnessDobj.getFit_off_cd1() == Integer.parseInt(value.toString())) {
            FacesMessage msg = new FacesMessage("Both Inspection Officer Can't be Same.Please Choose Different Inspection Officer");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    /**
     * @return the parameters_list
     */
    public List getParameters_list() {
        return parameters_list;
    }

    /**
     * @param parameters_list the parameters_list to set
     */
    public void setParameters_list(List parameters_list) {
        this.parameters_list = parameters_list;
    }

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
     * @return the fitness_dobj_prv
     */
    public FitnessDobj getFitness_dobj_prv() {
        return fitness_dobj_prv;
    }

    /**
     * @param fitness_dobj_prv the fitness_dobj_prv to set
     */
    public void setFitness_dobj_prv(FitnessDobj fitness_dobj_prv) {
        this.fitness_dobj_prv = fitness_dobj_prv;
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
     * @return the fitnessDobj
     */
    public FitnessDobj getFitnessDobj() {
        return fitnessDobj;
    }

    /**
     * @param fitnessDobj the fitnessDobj to set
     */
    public void setFitnessDobj(FitnessDobj fitnessDobj) {
        this.fitnessDobj = fitnessDobj;
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
}
