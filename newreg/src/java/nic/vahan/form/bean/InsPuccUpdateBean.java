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
import nic.java.util.CommonUtils;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.PuccDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.pucc.TmConfigurationPUCCdobj;
import nic.vahan.form.impl.BlackListedVehicleImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.InsPuccUpdateImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import nic.vahan.services.insurance.InsuranceDetailService;
import nic.vahan.services.insurance.dobj.InsuranceInfoDobj;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.primefaces.PrimeFaces;

/**
 *
 * @author tranC103
 */
@ManagedBean
@ViewScoped
public class InsPuccUpdateBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(InsPuccUpdateBean.class);
    private PuccDobj puccDobj = null;
    private Owner_dobj ownerDobj;
    private String regnNo;
    private String applNo;
    private boolean insuranceExpired;
    private boolean puccExpired;
    InsPuccUpdateImpl impl = new InsPuccUpdateImpl();
    OwnerImpl ownerImpl = new OwnerImpl();
    @ManagedProperty(value = "#{ins_bean}")
    private InsBean insBean;
    private boolean renderInsDetails = false;
    private boolean renderPuccDetails = false;
    private boolean renderOwnerIdDetails = false;
    private boolean renderCheckboxPanel = false;
    private boolean renderSavePanel = false;
    private Date today = new Date();
    private boolean disable = false;
    private String[] selectedCheckBox;
    private OwnerIdentificationDobj ownerIdentity = new OwnerIdentificationDobj();
    private List listOwnerCatg = new ArrayList();

    @PostConstruct
    public void init() {
        applNo = null;
        insuranceExpired = false;
        puccExpired = false;

        //Master Filler for Owner Category
        String[][] data = MasterTableFiller.masterTables.VM_OWCATG.getData();
        for (int i = 0; i < data.length; i++) {
            listOwnerCatg.add(new SelectItem(data[i][0], data[i][1]));
        }

        //if insurance//pucc expired and page redirection is from home.xhtml
        if (Util.getSelectedSeat() != null) {
            regnNo = Util.getSelectedSeat().getRegn_no();
            applNo = Util.getSelectedSeat().getAppl_no();
            if (Util.getSelectedSeat().isIsInsuranceCheck()) {
                insuranceExpired = true;
            }
            if (Util.getSelectedSeat().isPuccCheck()) {
                puccExpired = true;
            }
            if (insuranceExpired || puccExpired) {
                showDetails();
            }
        }
    }

    public void reset() {
        renderInsDetails = false;
        renderPuccDetails = false;
        renderOwnerIdDetails = false;
        renderCheckboxPanel = false;
        renderSavePanel = false;
        selectedCheckBox = null;
        if (Util.getSelectedSeat() != null) {
            Util.getSelectedSeat().setPuccCheck(false);
            Util.getSelectedSeat().setIsInsuranceCheck(false);
            Util.getSelectedSeat().setAppl_no(null);
            Util.getSelectedSeat().setRegn_no(null);
        }
    }

    public void showDetails() {
        InsDobj insDobjVt = null;
        InsDobj insDobjVa = null;
        puccDobj = null;
        reset();

        try {
            TmConfigurationPUCCdobj configurationPUCCdobj = impl.getTmConfigurationPUCCdobj(Util.getUserStateCode());
            if (regnNo == null || regnNo.trim().isEmpty() || regnNo.equalsIgnoreCase("NEW") || regnNo.equalsIgnoreCase("TEMPREG")) {
                throw new VahanException("Invalid Registration No.");
            }
            setOwnerDobj(ownerImpl.set_Owner_appl_db_to_dobj(regnNo, null, null, TableConstants.VM_TRANSACTION_MAST_NEW_VEHICLE));
            if (ownerDobj != null) {
                insDobjVa = InsImpl.set_ins_dtls_db_to_dobjVA(regnNo);
                insDobjVt = InsImpl.set_ins_dtls_db_to_dobj(regnNo, null, null, 0);
                ownerIdentity = ownerDobj.getOwner_identity();
                if (insuranceExpired) {//when redirected from home.xhtml
                    selectedCheckBox = new String[]{"I"};
                    if (insDobjVt != null) {
                        insBean.set_Ins_dobj_to_bean(insDobjVt);
                        selectedCheckBox = new String[]{"I"};
                        setRenderInsDetails(true);
                    }
                } else if (puccExpired) {//when redirected from home.xhtml
                    selectedCheckBox = new String[]{"P"};
                    puccDobj = impl.getPuccDetails(regnNo);
                    if (puccDobj == null) {
                        puccDobj = new PuccDobj();
                    }
                    setRenderPuccDetails(true);
                } else {
                    puccDobj = impl.getPuccDetails(regnNo);
                    validatePuccDetails(puccDobj);
                    if (puccDobj == null) {
                        puccDobj = new PuccDobj();
                    }
                    //setRenderInsDetails(true);
                    //setRenderPuccDetails(true);
                    //selectedCheckBox = new String[]{"I", "P"};
                }
                InsDobj insDobj = null;
                if (!(ownerDobj.getOwner_cd() == TableConstants.VEH_TYPE_STATE_GOVT
                        || ownerDobj.getOwner_cd() == TableConstants.VEH_TYPE_GOVT_UNDERTAKING
                        || ownerDobj.getOwner_cd() == TableConstants.VEH_TYPE_STATE_TRANS_DEPT)) {

                    //start of getting insurance details from service
                    InsuranceDetailService detailService = new InsuranceDetailService();
                    insDobj = detailService.getInsuranceDetailsByService(regnNo, ownerDobj.getState_cd(), ownerDobj.getOff_cd());
                    if (insDobj != null) {
                        if (insBean.getIns_upto() != null) {
                            insDobj.setIdv(insBean.getIdv());
                        }

                        if ((DateUtils.compareDates(DateUtils.addToDate(insDobj.getIns_from(), DateUtils.MONTH, -1), ownerDobj.getPurchase_dt()) == 1)) {
                            // do Nothing
                        } else {

                            insBean.set_Ins_dobj_to_bean(insDobj);
                            if (insDobj.isIibData()) {
                                insBean.componentReadOnly(false);
                                insBean.setGovtVehFlag(true);
                            } else {
                                insBean.componentReadOnly(true);
                                insBean.setGovtVehFlag(false);
                            }
                        }
//                    }
                        if (!insDobj.isIibData()) {
                            insDobj = InsImpl.set_ins_dtls_db_to_dobj(regnNo, null, null, 0);
                            if (insDobj != null) {
                                insBean.set_Ins_dobj_to_bean(insDobj);
                                ownerDobj.setInsDobj(insDobj);
                            }
                        }//end of getting insurance details from service 
                    }
                }

                insBean.validateInsurance(insDobjVa, insDobj, false);
                setRenderCheckboxPanel(true);
                setRenderSavePanel(true);
                //Black Listed Check
                boolean isblacklistedvehicle;
                TmConfigurationDobj tmConfig = Util.getTmConfiguration();
                isblacklistedvehicle = ServerUtil.isPurposeCodeOnBlacklistedVehicle(ownerDobj.getRegn_no(), Util.getSelectedSeat().getAction_cd(), Util.getUserStateCode(), tmConfig);
                if (isblacklistedvehicle) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", "Vehicle No. has been Blacklisted."));
                    ownerDobj = null;
                    renderCheckboxPanel = false;
                    renderSavePanel = false;
                    return;
                }
                BlackListedVehicleImpl obj = new BlackListedVehicleImpl();
                BlackListedVehicleDobj blacklistedStatus = obj.getBlacklistedVehicleDetails(regnNo, ownerDobj.getChasi_no());

                if (blacklistedStatus != null) {
                    ownerDobj.setBlackListedVehicleDobj(blacklistedStatus);
                }
                if (blacklistedStatus != null
                        && (blacklistedStatus.getComplain_type() == TableConstants.BLTheftCode
                        || blacklistedStatus.getComplain_type() == TableConstants.BLDestroyedAccidentCode)) {
                    insBean.setMin_dt(ownerDobj.getPurchase_dt());
                }
            } else {
                JSFUtils.showMessage("Invalid Registration Number");
            }

        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + "-" + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void checkBoxListener() {
        setRenderPuccDetails(false);
        setRenderInsDetails(false);
        setRenderOwnerIdDetails(false);
        for (int i = 0; i < selectedCheckBox.length; i++) {
            if (selectedCheckBox[i].equals("P")) {
                setRenderPuccDetails(true);
            }
            if (selectedCheckBox[i].equals("I")) {
                setRenderInsDetails(true);
            }
            if (selectedCheckBox[i].equals("O")) {
                setRenderOwnerIdDetails(true);
            }
        }
    }

    private boolean validatePuccDetails(PuccDobj puccDobj) {
        boolean puccValidate = false;
        if (puccDobj != null && puccDobj.getPuccUpto() != null) {
            if (puccDobj.getPuccUpto().after(today)) {
                puccValidate = true;
            } else {
                JSFUtils.showMessage("PUCC Expired");
            }
        }
        return puccValidate;
    }

    public void updateInsPuccDetails() {
        try {
            String msg = "";
            InsDobj insDobj = insBean.set_InsBean_to_dobj();
            if (!renderInsDetails) {
                insDobj = null;
            }
            if (!renderPuccDetails) {
                puccDobj = null;
            }
            msg = impl.saveInsPuccDetails(insDobj, puccDobj, regnNo, ownerDobj, ownerIdentity, renderOwnerIdDetails, ownerDobj.getState_cd(), ownerDobj.getOff_cd(), Util.getEmpCode());
            regnNo = "";
            setOwnerDobj(null);
            setRenderCheckboxPanel(false);
            setRenderInsDetails(false);
            setRenderOwnerIdDetails(false);
            setRenderPuccDetails(false);
            setRenderSavePanel(false);
            insuranceExpired = false;
            puccExpired = false;
            JSFUtils.showMessage(msg);
        } catch (VahanException e) {
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage(TableConstants.SomthingWentWrong);
        }
    }

    public void pucc_no_blur_listener() {
        try {
            if (!CommonUtils.isNullOrBlank(puccDobj.getPuccNo()) && (!puccDobj.getPuccNo().equalsIgnoreCase("NA"))) {
                String regn_No = impl.checkPUCCNoUniqueness(puccDobj.getPuccNo(), puccDobj.getPuccCentreno());
                if (!CommonUtils.isNullOrBlank(regn_No)) {
                    puccDobj.setPuccNo("");
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Duplicate PUCC number Against Vehicle " + regn_No + ".", null));
                }
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), null));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, null));
        }
    }

    /**
     * @return the puccDobj
     */
    public PuccDobj getPuccDobj() {
        return puccDobj;
    }

    /**
     * @param puccDobj the puccDobj to set
     */
    public void setPuccDobj(PuccDobj puccDobj) {
        this.puccDobj = puccDobj;
    }

    /**
     * @return the regnNo
     */
    public String getRegnNo() {
        return regnNo;
    }

    /**
     * @param regnNo the regnNo to set
     */
    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    /**
     * @param insBean the insBean to set
     */
    public void setInsBean(InsBean insBean) {
        this.insBean = insBean;
    }

    /**
     * @return the renderInsDetails
     */
    public boolean isRenderInsDetails() {
        return renderInsDetails;
    }

    /**
     * @param renderInsDetails the renderInsDetails to set
     */
    public void setRenderInsDetails(boolean renderInsDetails) {
        this.renderInsDetails = renderInsDetails;
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

    /**
     * @return the disable
     */
    public boolean isDisable() {
        return disable;
    }

    /**
     * @param disable the disable to set
     */
    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    /**
     * @return the renderPuccDetails
     */
    public boolean isRenderPuccDetails() {
        return renderPuccDetails;
    }

    /**
     * @param renderPuccDetails the renderPuccDetails to set
     */
    public void setRenderPuccDetails(boolean renderPuccDetails) {
        this.renderPuccDetails = renderPuccDetails;
    }

    /**
     * @return the selectedCheckBox
     */
    public String[] getSelectedCheckBox() {
        return selectedCheckBox;
    }

    /**
     * @param selectedCheckBox the selectedCheckBox to set
     */
    public void setSelectedCheckBox(String[] selectedCheckBox) {
        this.selectedCheckBox = selectedCheckBox;
    }

    /**
     * @return the renderCheckboxPanel
     */
    public boolean isRenderCheckboxPanel() {
        return renderCheckboxPanel;
    }

    /**
     * @param renderCheckboxPanel the renderCheckboxPanel to set
     */
    public void setRenderCheckboxPanel(boolean renderCheckboxPanel) {
        this.renderCheckboxPanel = renderCheckboxPanel;
    }

    /**
     * @return the renderSavePanel
     */
    public boolean isRenderSavePanel() {
        return renderSavePanel;
    }

    /**
     * @param renderSavePanel the renderSavePanel to set
     */
    public void setRenderSavePanel(boolean renderSavePanel) {
        this.renderSavePanel = renderSavePanel;
    }

    /**
     * @return the applNo
     */
    public String getApplNo() {
        return applNo;
    }

    /**
     * @param applNo the applNo to set
     */
    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    /**
     * @return the insuranceExpired
     */
    public boolean isInsuranceExpired() {
        return insuranceExpired;
    }

    /**
     * @param insuranceExpired the insuranceExpired to set
     */
    public void setInsuranceExpired(boolean insuranceExpired) {
        this.insuranceExpired = insuranceExpired;
    }

    /**
     * @return the ownerIdentity
     */
    public OwnerIdentificationDobj getOwnerIdentity() {
        return ownerIdentity;
    }

    /**
     * @param ownerIdentity the ownerIdentity to set
     */
    public void setOwnerIdentity(OwnerIdentificationDobj ownerIdentity) {
        this.ownerIdentity = ownerIdentity;
    }

    /**
     * @return the listOwnerCatg
     */
    public List getListOwnerCatg() {
        return listOwnerCatg;
    }

    /**
     * @param listOwnerCatg the listOwnerCatg to set
     */
    public void setListOwnerCatg(List listOwnerCatg) {
        this.listOwnerCatg = listOwnerCatg;
    }

    public boolean isRenderOwnerIdDetails() {
        return renderOwnerIdDetails;
    }

    public void setRenderOwnerIdDetails(boolean renderOwnerIdDetails) {
        this.renderOwnerIdDetails = renderOwnerIdDetails;
    }

    /**
     * @return the puccExpired
     */
    public boolean isPuccExpired() {
        return puccExpired;
    }

    /**
     * @param puccExpired the puccExpired to set
     */
    public void setPuccExpired(boolean puccExpired) {
        this.puccExpired = puccExpired;
    }

}
