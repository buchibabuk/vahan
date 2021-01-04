/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.homologation.managagebean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.vahan.form.impl.Util;
import nic.vahan.homologation.daoImpl.ModelDetailsDaoImpl;
import nic.vahan.homologation.dobj.Vm_model;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

/**
 *
 * @author nprpE070
 */
@ManagedBean
@ViewScoped
public class Modeldetails implements Serializable {

    private boolean panelflag = false;
    private static Logger logger = Logger.getLogger(Modeldetails.class);
    private List makerList = new ArrayList();
    private int cb_maker;
    private DualListModel<String> modelNameList;
    private List<Vm_model> sourceList = new ArrayList<>();
    private List<Vm_model> selectedSourceList = null;
    private List<Vm_model> targetList = new ArrayList<>();
    private List<Vm_model> selectedTargetList = null;
    private List<String> actionSource = new ArrayList<String>();
    private List<String> actionTarget = new ArrayList<String>();
    Vm_model dobj = null;
    private String strTarget = "";
    private String savelabel = "Save Models For Regristration For " + Util.getUserStateCode();
    private String selectedTargetId;

    public Modeldetails() {
        modelNameList = new DualListModel(actionSource, actionTarget);
        try {
            setMakerList(ModelDetailsDaoImpl.getMakerNameListForModels());
            try {
                dobj = new Vm_model();
                // dobj.setMaker_code(Integer.parseInt((cb_maker.getValue().toString())));
                //  modelNameList=  ModelDetailsDaoImpl.getModelCodewithDespForManuf(dobj);
            } catch (Exception ex) {
                logger.error("Exception Occured-" + ex);
            }
            modelNameList = new DualListModel(actionSource, actionTarget);

        } catch (Exception ex) {
            logger.error("Exception Occured in fectchin maker list-" + ex);
        }

    }

    public void reset() {

        if (actionSource.size() > 0 || actionTarget.size() > 0) {
            actionSource.clear();
            actionTarget.clear();
            modelNameList = new DualListModel(actionSource, actionTarget);
        }
        cb_maker = -1;
        panelflag = false;


    }

    public void saveModelDetails() {
        FacesContext context = FacesContext.getCurrentInstance();
        dobj = new Vm_model();
        dobj.setTargetstr(strTarget);
        dobj.setState_cd(Util.getUserStateCode());
        dobj.setMaker_code(getCb_maker());
        dobj.setEntered_by(Util.getEmpCode());
        String m = "";
        if (selectedSourceList.size() > 0) {
            m = ModelDetailsDaoImpl.addAndRemoveModelDetailsController(selectedSourceList, getCb_maker(), true);
            if (m.equalsIgnoreCase("success")) {
                targetList.addAll(selectedSourceList);
                sourceList.removeAll(selectedSourceList);
                context.addMessage(null, new FacesMessage("Assigned  Models Details Save Successfully!!!"));
            } else if (m.equalsIgnoreCase("failure")) {
                context.addMessage(null, new FacesMessage("Cannot Process Your Request Please Try After Some Time"));
            }

        } else {

            context.addMessage(null, new FacesMessage("Please Select Atleat one Available model"));
        }


    }

    public void removeModelDetails() {
        FacesContext context = FacesContext.getCurrentInstance();
        dobj = new Vm_model();
        dobj.setTargetstr(strTarget);
        dobj.setState_cd(Util.getUserStateCode());
        dobj.setMaker_code(getCb_maker());
        dobj.setEntered_by(Util.getEmpCode());
        String m = "";
        if (selectedTargetList.size() > 0) {
            m = ModelDetailsDaoImpl.addAndRemoveModelDetailsController(selectedTargetList, getCb_maker(), false);
            if (m.equalsIgnoreCase("success")) {
                sourceList.addAll(selectedTargetList);
                targetList.removeAll(selectedTargetList);

                context.addMessage(null, new FacesMessage("Models Removed Successfully!!!"));
            } else if (m.equalsIgnoreCase("failure")) {
                context.addMessage(null, new FacesMessage("Cannot Process Your Request Please Try After Some Time"));
            }

        } else {

            context.addMessage(null, new FacesMessage("Please Select Atleast one Available model "));
        }


    }

    public String searchModelDetails() {

        try {
            if (getCb_maker() == -1) {
                FacesContext context = FacesContext.getCurrentInstance();
                context.addMessage(null, new FacesMessage("Please Select manufacturer"));
            } else {
                dobj = new Vm_model();
                dobj.setState_cd(Util.getUserStateCode());

                dobj.setMaker_code(getCb_maker());
                //   modelNameList = ModelDetailsDaoImpl.getModelCodewithDespForManuf(dobj);
                sourceList = ModelDetailsDaoImpl.getModelCodewithDespForManuf(dobj);
                targetList = ModelDetailsDaoImpl.fillActionTarget(dobj);
                panelflag = true;
            }
        } catch (Exception ex) {
            logger.error("Exception Occured-" + ex);
        }

        return "";
    }

    public void onTransferEvent(TransferEvent event) {
        for (Object item : event.getItems()) {
            setStrTarget(getStrTarget() + item + ",");

        }



    }

    /**
     * @return the makerList
     */
    public List getMakerList() {
        return makerList;
    }

    /**
     * @param makerList the makerList to set
     */
    public void setMakerList(List makerList) {
        this.makerList = makerList;
    }

    /**
     * @return the actionSource
     */
    public List<String> getActionSource() {
        return actionSource;
    }

    /**
     * @param actionSource the actionSource to set
     */
    public void setActionSource(List<String> actionSource) {
        this.actionSource = actionSource;
    }

    /**
     * @return the actionTarget
     */
    public List<String> getActionTarget() {
        return actionTarget;
    }

    /**
     * @param actionTarget the actionTarget to set
     */
    public void setActionTarget(List<String> actionTarget) {
        this.actionTarget = actionTarget;
    }

    /**
     * @return the modelNameList
     */
    public DualListModel<String> getModelNameList() {
        return modelNameList;
    }

    /**
     * @param modelNameList the modelNameList to set
     */
    public void setModelNameList(DualListModel<String> modelNameList) {
        this.modelNameList = modelNameList;
    }

    /**
     * @return the cb_maker
     */
    public int getCb_maker() {
        return cb_maker;
    }

    /**
     * @param cb_maker the cb_maker to set
     */
    public void setCb_maker(int cb_maker) {
        this.cb_maker = cb_maker;
    }

    /**
     * @return the strTarget
     */
    public String getStrTarget() {
        return strTarget;
    }

    /**
     * @param strTarget the strTarget to set
     */
    public void setStrTarget(String strTarget) {
        this.strTarget = strTarget;
    }

    /**
     * @return the panelflag
     */
    public boolean isPanelflag() {
        return panelflag;
    }

    /**
     * @param panelflag the panelflag to set
     */
    public void setPanelflag(boolean panelflag) {
        this.panelflag = panelflag;
    }

    /**
     * @return the savelabel
     */
    public String getSavelabel() {
        return savelabel;
    }

    /**
     * @param savelabel the savelabel to set
     */
    public void setSavelabel(String savelabel) {
        this.savelabel = savelabel;
    }

    /**
     * @return the selectedTargetId
     */
    public String getSelectedTargetId() {
        return selectedTargetId;
    }

    /**
     * @param selectedTargetId the selectedTargetId to set
     */
    public void setSelectedTargetId(String selectedTargetId) {
        this.selectedTargetId = selectedTargetId;
    }

    /**
     * @return the sourceList
     */
    public List<Vm_model> getSourceList() {
        return sourceList;
    }

    /**
     * @param sourceList the sourceList to set
     */
    public void setSourceList(List<Vm_model> sourceList) {
        this.sourceList = sourceList;
    }

    /**
     * @return the selectedSourceList
     */
    public List<Vm_model> getSelectedSourceList() {
        return selectedSourceList;
    }

    /**
     * @param selectedSourceList the selectedSourceList to set
     */
    public void setSelectedSourceList(List<Vm_model> selectedSourceList) {
        this.selectedSourceList = selectedSourceList;
    }

    /**
     * @return the targetList
     */
    public List<Vm_model> getTargetList() {
        return targetList;
    }

    /**
     * @param targetList the targetList to set
     */
    public void setTargetList(List<Vm_model> targetList) {
        this.targetList = targetList;
    }

    /**
     * @return the selectedTargetList
     */
    public List<Vm_model> getSelectedTargetList() {
        return selectedTargetList;
    }

    /**
     * @param selectedTargetList the selectedTargetList to set
     */
    public void setSelectedTargetList(List<Vm_model> selectedTargetList) {
        this.selectedTargetList = selectedTargetList;
    }
}
