/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.FitnessBean;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.fitness.TmConfigurationFitnessDobj;
import org.primefaces.component.selectmanycheckbox.SelectManyCheckbox;

/**
 *
 * @author Sai
 */
public class FitnessBeanModel {

    private SelectManyCheckbox parameters;
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
    // Added by Kartikey: Not part of the original class
    private ArrayList allYesNoList;

    public FitnessBeanModel(FitnessBean fitness_bean) {
        this.compBeanList = fitness_bean.getCompBeanList();
        this.disableFitnessOfficer1 = fitness_bean.isDisableFitnessOfficer1();
        this.fit_off_cd1_list = fitness_bean.getFit_off_cd1_list();
        this.fit_off_cd2_list = fitness_bean.getFit_off_cd2_list();
        this.fitnessDobj = fitness_bean.getFitnessDobj();
        this.fitness_dobj_prv = fitness_bean.getFitness_dobj_prv();
        this.isMultipleFitnessOfficer = fitness_bean.isIsMultipleFitnessOfficer();
        this.maxDate = fitness_bean.getMaxDate();
        this.parameters = fitness_bean.getParameters();
        this.parameters_list = fitness_bean.getParameters_list();
        this.tmConfigFitnessDobj = fitness_bean.getTmConfigFitnessDobj();
        this.tmConfigurationDobj = fitness_bean.getTmConfigurationDobj();

    }

    public FitnessBeanModel() {
    }

    public List getParameters_list() {
        return parameters_list;
    }

    public void setParameters_list(List parameters_list) {
        this.parameters_list = parameters_list;
    }

    public Map<String, Integer> getFit_off_cd1_list() {
        return fit_off_cd1_list;
    }

    public void setFit_off_cd1_list(Map<String, Integer> fit_off_cd1_list) {
        this.fit_off_cd1_list = fit_off_cd1_list;
    }

    public Map<String, Integer> getFit_off_cd2_list() {
        return fit_off_cd2_list;
    }

    public void setFit_off_cd2_list(Map<String, Integer> fit_off_cd2_list) {
        this.fit_off_cd2_list = fit_off_cd2_list;
    }

    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    public FitnessDobj getFitnessDobj() {
        return fitnessDobj;
    }

    public void setFitnessDobj(FitnessDobj fitnessDobj) {
        this.fitnessDobj = fitnessDobj;
    }

    public FitnessDobj getFitness_dobj_prv() {
        return fitness_dobj_prv;
    }

    public void setFitness_dobj_prv(FitnessDobj fitness_dobj_prv) {
        this.fitness_dobj_prv = fitness_dobj_prv;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public boolean isIsMultipleFitnessOfficer() {
        return isMultipleFitnessOfficer;
    }

    public void setIsMultipleFitnessOfficer(boolean isMultipleFitnessOfficer) {
        this.isMultipleFitnessOfficer = isMultipleFitnessOfficer;
    }

    public TmConfigurationDobj getTmConfigurationDobj() {
        return tmConfigurationDobj;
    }

    public void setTmConfigurationDobj(TmConfigurationDobj tmConfigurationDobj) {
        this.tmConfigurationDobj = tmConfigurationDobj;
    }

    public TmConfigurationFitnessDobj getTmConfigFitnessDobj() {
        return tmConfigFitnessDobj;
    }

    public void setTmConfigFitnessDobj(TmConfigurationFitnessDobj tmConfigFitnessDobj) {
        this.tmConfigFitnessDobj = tmConfigFitnessDobj;
    }

    public boolean isDisableFitnessOfficer1() {
        return disableFitnessOfficer1;
    }

    public void setDisableFitnessOfficer1(boolean disableFitnessOfficer1) {
        this.disableFitnessOfficer1 = disableFitnessOfficer1;
    }

    public SessionVariables getSessionVariables() {
        return sessionVariables;
    }

    public void setSessionVariables(SessionVariables sessionVariables) {
        this.sessionVariables = sessionVariables;
    }

    @Override
    public String toString() {
        return "FitnessBeanModel{" + ", parameters_list=" + parameters_list + ", fit_off_cd1_list=" + fit_off_cd1_list + ", fit_off_cd2_list=" + fit_off_cd2_list + ", compBeanList=" + compBeanList + ", fitnessDobj=" + fitnessDobj + ", fitness_dobj_prv=" + fitness_dobj_prv + ", maxDate=" + maxDate + ", isMultipleFitnessOfficer=" + isMultipleFitnessOfficer + ", tmConfigurationDobj=" + tmConfigurationDobj + ", tmConfigFitnessDobj=" + tmConfigFitnessDobj + ", disableFitnessOfficer1=" + disableFitnessOfficer1 + ", sessionVariables=" + sessionVariables + '}';
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

//    public SelectManyCheckbox getParameters() {
//        return parameters;
//    }
//
//    public void setParameters(SelectManyCheckbox parameters) {
//        this.parameters = parameters;
//    }

    public ArrayList getAllYesNoList() {
        return allYesNoList;
    }

    public void setAllYesNoList(ArrayList allYesNoList) {
        this.allYesNoList = allYesNoList;
    }
}
