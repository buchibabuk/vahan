/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.model;

import java.util.List;
import nic.vahan.form.bean.EpayBean;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.Trailer_dobj;
//import tax.web.service.VahanTaxParameters;
import nic.vahan.form.dobj.tax.VahanTaxParameters;

/**
 *
 * @author Kartikey Singh
 */
public class WrapperModel {

    ComparisonBeanModel comparisonBean;
    SessionVariablesModel sessionVariables;
    WorkBenchModel workBench;
    Owner_dobj ownerDobj;
    InsDobj insDobj;
    TmConfigurationDobj tmConfigDobj;
    HpaBeanModel hpaBeanModel;
    ExArmyBeanModel exArmyBean;
    ImportedVehicleBeanModel importedVehicleBean;
    AxleBeanModel axleBean;
    RetroFittingDetailsBeanModel cngDetailsBean;
    Trailer_dobj trailerDobj;
    OwnerBeanModel ownerBean;
    Status_dobj statusDobj;
    ContextMessageModel contextMessageModel;
    NewVehicleFeeBeanModel newVehicleFeeBeanModel;
    DocumentUploadBeanModel documentUploadBeanModel;
    VahanTaxParameters vahanTaxParameters;
    //Created by Sai    
    private InsBeanModel ins_bean;
    private List<OwnerDetailsDobj> tempRegnDetailsList = null;
    private TrailerBeanModel trailerBeanModel;
    private FitnessBeanModel fitnessBeanModel;
    private EpayBean epayBean;
    private String hypothecationStatus;

    public String getHypothecationStatus() {
        return hypothecationStatus;
    }

    public void setHypothecationStatus(String hypothecationStatus) {
        this.hypothecationStatus = hypothecationStatus;
    }

    public EpayBean getEpayBean() {
        return epayBean;
    }

    public void setEpayBean(EpayBean epayBean) {
        this.epayBean = epayBean;
    }

    public TrailerBeanModel getTrailerBeanModel() {
        return trailerBeanModel;
    }

    public void setTrailerBeanModel(TrailerBeanModel trailerBeanModel) {
        this.trailerBeanModel = trailerBeanModel;
    }

    public FitnessBeanModel getFitnessBeanModel() {
        return fitnessBeanModel;
    }

    public void setFitnessBeanModel(FitnessBeanModel fitnessBeanModel) {
        this.fitnessBeanModel = fitnessBeanModel;
    }

    public NewVehicleFeeBeanModel getNewVehicleFeeBeanModel() {
        return newVehicleFeeBeanModel;
    }

    public void setNewVehicleFeeBeanModel(NewVehicleFeeBeanModel newVehicleFeeBeanModel) {
        this.newVehicleFeeBeanModel = newVehicleFeeBeanModel;
    }

    public WrapperModel(ComparisonBeanModel comparisonBean, SessionVariablesModel sessionVariables, WorkBenchModel workBench, Owner_dobj ownerDobj, InsDobj insDobj, TmConfigurationDobj tmConfigDobj) {
        this.comparisonBean = comparisonBean;
        this.sessionVariables = sessionVariables;
        this.workBench = workBench;
        this.ownerDobj = ownerDobj;
        this.insDobj = insDobj;
        this.tmConfigDobj = tmConfigDobj;
    }

    public WrapperModel() {
    }

    /**
     * @author Sai
     */
    public WrapperModel(SessionVariablesModel sessionVariables, TmConfigurationDobj tmConfigDobj) {
        this.sessionVariables = sessionVariables;
        this.tmConfigDobj = tmConfigDobj;

    }

    public ComparisonBeanModel getComparisonBean() {
        return comparisonBean;
    }

    public void setComparisonBean(ComparisonBeanModel comparisonBean) {
        this.comparisonBean = comparisonBean;
    }

    public SessionVariablesModel getSessionVariables() {
        return sessionVariables;
    }

    public void setSessionVariables(SessionVariablesModel sessionVariables) {
        this.sessionVariables = sessionVariables;
    }

    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    public WorkBenchModel getWorkBench() {
        return workBench;
    }

    public void setWorkBench(WorkBenchModel workBench) {
        this.workBench = workBench;
    }

    public InsDobj getInsDobj() {
        return insDobj;
    }

    public void setInsDobj(InsDobj insDobj) {
        this.insDobj = insDobj;
    }

    public TmConfigurationDobj getTmConfigDobj() {
        return tmConfigDobj;
    }

    public void setTmConfigDobj(TmConfigurationDobj tmConfigDobj) {
        this.tmConfigDobj = tmConfigDobj;
    }

    public Status_dobj getStatusDobj() {
        return statusDobj;
    }

    public void setStatusDobj(Status_dobj statusDobj) {
        this.statusDobj = statusDobj;
    }

    public OwnerBeanModel getOwnerBean() {
        return ownerBean;
    }

    public void setOwnerBean(OwnerBeanModel ownerBean) {
        this.ownerBean = ownerBean;
    }

    public HpaBeanModel getHpaBeanModel() {
        return hpaBeanModel;
    }

    public void setHpaBeanModel(HpaBeanModel hpaBeanModel) {
        this.hpaBeanModel = hpaBeanModel;
    }

    public ExArmyBeanModel getExArmyBean() {
        return exArmyBean;
    }

    public void setExArmyBean(ExArmyBeanModel exArmyBean) {
        this.exArmyBean = exArmyBean;
    }

    public ImportedVehicleBeanModel getImportedVehicleBean() {
        return importedVehicleBean;
    }

    public void setImportedVehicleBean(ImportedVehicleBeanModel importedVehicleBean) {
        this.importedVehicleBean = importedVehicleBean;
    }

    public AxleBeanModel getAxleBean() {
        return axleBean;
    }

    public void setAxleBean(AxleBeanModel axleBean) {
        this.axleBean = axleBean;
    }

    public RetroFittingDetailsBeanModel getCngDetailsBean() {
        return cngDetailsBean;
    }

    public void setCngDetailsBean(RetroFittingDetailsBeanModel cngDetailsBean) {
        this.cngDetailsBean = cngDetailsBean;
    }

    public Trailer_dobj getTrailerDobj() {
        return trailerDobj;
    }

    public void setTrailerDobj(Trailer_dobj trailerDobj) {
        this.trailerDobj = trailerDobj;
    }

    public ContextMessageModel getContextMessageModel() {
        return contextMessageModel;
    }

    public void setContextMessageModel(ContextMessageModel contextMessageModel) {
        this.contextMessageModel = contextMessageModel;
    }

    public InsBeanModel getIns_bean() {
        return ins_bean;
    }

    public void setIns_bean(InsBeanModel ins_bean) {
        this.ins_bean = ins_bean;
    }

    public List<OwnerDetailsDobj> getTempRegnDetailsList() {
        return tempRegnDetailsList;
    }

    public void setTempRegnDetailsList(List<OwnerDetailsDobj> tempRegnDetailsList) {
        this.tempRegnDetailsList = tempRegnDetailsList;
    }

    public DocumentUploadBeanModel getDocumentUploadBeanModel() {
        return documentUploadBeanModel;
    }

    public void setDocumentUploadBeanModel(DocumentUploadBeanModel documentUploadBeanModel) {
        this.documentUploadBeanModel = documentUploadBeanModel;
    }

    public VahanTaxParameters getVahanTaxParameters() {
        return vahanTaxParameters;
    }

    public void setVahanTaxParameters(VahanTaxParameters vahanTaxParameters) {
        this.vahanTaxParameters = vahanTaxParameters;
    }

    @Override
    public String toString() {
        return "WrapperModel{" + "comparisonBean=" + comparisonBean + ", sessionVariables=" + sessionVariables + ", workBench=" + workBench + ", ownerDobj=" + ownerDobj + ", insDobj=" + insDobj + ", tmConfigDobj=" + tmConfigDobj + ", hpaBeanModel=" + hpaBeanModel + ", exArmyBean=" + exArmyBean + ", importedVehicleBean=" + importedVehicleBean + ", axleBean=" + axleBean + ", cngDetailsBean=" + cngDetailsBean + ", trailerDobj=" + trailerDobj + ", ownerBean=" + ownerBean + ", statusDobj=" + statusDobj + ", contextMessageModel=" + contextMessageModel + ", newVehicleFeeBeanModel=" + newVehicleFeeBeanModel + ", documentUploadBeanModel=" + documentUploadBeanModel + ", vahanTaxParameters=" + vahanTaxParameters + ", ins_bean=" + ins_bean + ", tempRegnDetailsList=" + tempRegnDetailsList + ", trailerBeanModel=" + trailerBeanModel + ", fitnessBeanModel=" + fitnessBeanModel + ", epayBean=" + epayBean + ", hypothecationStatus=" + hypothecationStatus + '}';
    }
}
