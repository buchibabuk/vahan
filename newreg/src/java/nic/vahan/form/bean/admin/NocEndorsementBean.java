/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.AxleBean;
import nic.vahan.form.bean.ExArmyBean;
import nic.vahan.form.bean.HSRPDetailsBean;
import nic.vahan.form.bean.HypothecationDetailsBean;
import nic.vahan.form.bean.ImportedVehicleBean;
import nic.vahan.form.bean.InsBean;
import nic.vahan.form.bean.RetroFittingDetailsBean;
import nic.vahan.form.bean.SCDetailsBean;
import nic.vahan.form.bean.Trailer_bean;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.dobj.ExArmyDobj;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.HpaDobj;
import nic.vahan.form.dobj.ImportedVehicleDobj;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.NocDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.RetroFittingDetailsDobj;
import nic.vahan.form.dobj.Trailer_dobj;
import nic.vahan.form.dobj.common.RegistrationStatusParametersDobj;
import nic.vahan.form.impl.AxleImpl;
import nic.vahan.form.impl.ExArmyImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.HSRPDetailsImpl;
import nic.vahan.form.impl.HpaImpl;
import nic.vahan.form.impl.ImportedVehicleImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.RetroFittingDetailsImpl;
import nic.vahan.form.impl.SmartCardImpl;
import nic.vahan.form.impl.Trailer_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.admin.NocEndorsementImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "nocEndorBean")
@ViewScoped
public class NocEndorsementBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(NocEndorsementBean.class);
    private String regnNo;
    private OwnerDetailsDobj ownerDetail;
    @ManagedProperty(value = "#{trailer_bean}")
    private Trailer_bean trailer_bean;
    @ManagedProperty(value = "#{ins_bean}")
    private InsBean ins_bean;
    @ManagedProperty(value = "#{hypothecationDetails_bean}")
    private HypothecationDetailsBean hypothecationDetails_bean;
    @ManagedProperty(value = "#{exArmyBean}")
    private ExArmyBean exArmyBean;
    @ManagedProperty(value = "#{axleBean}")
    private AxleBean axleBean;
    @ManagedProperty(value = "#{hsrp_bean}")
    private HSRPDetailsBean hsrpDetailsBean;
    @ManagedProperty(value = "#{smartcard_bean}")
    private SCDetailsBean scDetailsBean;
    @ManagedProperty(value = "#{importedVehicleBean}")
    private ImportedVehicleBean importedVehbean;
    @ManagedProperty(value = "#{retroFittingDetailsBean}")
    private RetroFittingDetailsBean cNG_Details_Bean;
    private RegistrationStatusParametersDobj regStatus = new RegistrationStatusParametersDobj();
    private FitnessDobj fitnessDobj = null;
    private ExArmyDobj exArmyDobj = null;
    private AxleDetailsDobj axleDetailsdobj = null;
    private Trailer_dobj trailer_dobj = null;
    private InsDobj ins_dobj = null;
    private ImportedVehicleDobj imp_dobj = null;
    private RetroFittingDetailsDobj cng_dobj = null;
    List<HpaDobj> hypth = null;
    private boolean smartcardStatus = false;
    private boolean hsrpStatus = false;
    private boolean isTransport = false;
    private boolean regnNoPanelShow = false;
    private boolean detailsPanelShow = false;
    private String successNocEndorsementMessg = "";
    private List listOwnerCatg = new ArrayList();
    private NocDobj nocDobj = null;

    public NocEndorsementBean() {
        regnNoPanelShow = true;
    }

    public void showDetails() {
        Exception ex = null;
        OwnerImpl owner_Impl = new OwnerImpl();
        NocEndorsementImpl noc_impl = new NocEndorsementImpl();
        Trailer_Impl trailer_Impl = new Trailer_Impl();

        try {
            if (this.getRegnNo().trim() == null || this.getRegnNo().trim().equalsIgnoreCase("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Please Enter Valid Registration Number"));
                return;
            }

            setOwnerDetail(owner_Impl.getOwnerDetails(getRegnNo(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd()));
            if (getOwnerDetail() == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration Number Not Found"));
                return;
            }

            // check for one month
            Date currentDate = Calendar.getInstance().getTime();
            nocDobj = noc_impl.getVtNocData(getRegnNo());

            if (nocDobj == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", TableConstants.NOC_DATA_NOT_FOUND));
                return;
            }

            if (nocDobj.getNoc_dt() == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", TableConstants.NOC_DATA_NOT_FOUND));
                return;
            }

            Date vahan4StartDate = ServerUtil.getVahan4StartDate(nocDobj.getState_to(), nocDobj.getOff_to());
            if (vahan4StartDate != null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", TableConstants.INVALID_FOR_NOC_ENDORSEMENT));
                return;
            }

            int diffInDays = (int) ((currentDate.getTime() - nocDobj.getNoc_dt().getTime()) / (1000 * 60 * 60 * 24));
            if (diffInDays < 30) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", TableConstants.NOC_MONTH_ERROR));
                return;
            }

            if (getOwnerDetail().getStatus().equalsIgnoreCase(TableConstants.VT_NOC_ISSUE_STATUS)) {
                setSmartcardStatus(SmartCardImpl.isSmartCard(getOwnerDetail().getState_cd(), getOwnerDetail().getOff_cd()));
                setHsrpStatus(HSRPDetailsImpl.isHsrp(getOwnerDetail().getState_cd(), getOwnerDetail().getOff_cd()));
                setIsTransport(ServerUtil.isTransport(getOwnerDetail().getVh_class(), null));

                //Master Filler for Owner Category
                String[][] data = MasterTableFiller.masterTables.VM_OWCATG.getData();
                for (int i = 0; i < data.length; i++) {
                    listOwnerCatg.add(new SelectItem(data[i][0], data[i][1]));
                }

                if (getOwnerDetail().getOwnerIdentity() != null) {
                    getOwnerDetail().getOwnerIdentity().setFlag(true);
                    getOwnerDetail().getOwnerIdentity().setMobileNoEditable(true);
                    getOwnerDetail().getOwnerIdentity().setOwnerCatgEditable(true);
                }

                if (getOwnerDetail().getRegn_type().equalsIgnoreCase(TableConstants.VM_REGN_TYPE_EXARMY)) {
                    exArmyDobj = ExArmyImpl.setExArmyDetails_db_to_dobj(null, getRegnNo(), getOwnerDetail().getState_cd(), getOwnerDetail().getOff_cd());
                    if (exArmyDobj != null) {
                        getExArmyBean().setDobj_To_Bean(exArmyDobj);
                    }
                }

                if (getOwnerDetail().getImported_vch() != null && getOwnerDetail().getImported_vch().equals(TableConstants.VM_REGN_TYPE_IMPORTED_YES)) {
                    imp_dobj = ImportedVehicleImpl.setImpVehDetails_db_to_dobj(null, getRegnNo(), getOwnerDetail().getState_cd(), getOwnerDetail().getOff_cd());
                    if (imp_dobj != null) {
                        getImportedVehbean().setDobj_to_Bean(imp_dobj);
                    }
                }


                if (getOwnerDetail().getVehTypeDescr().equalsIgnoreCase(TableConstants.VM_VEHTYPE_TRANSPORT_DESCR)) {
                    FitnessImpl fitImpl = new FitnessImpl();
                    axleDetailsdobj = AxleImpl.setAxleVehDetails_db_to_dobj(null, getRegnNo(), getOwnerDetail().getState_cd(), getOwnerDetail().getOff_cd());
                    if (axleDetailsdobj != null) {
                        getAxleBean().setDobj_To_Bean(axleDetailsdobj);
                    }

                    fitnessDobj = fitImpl.set_Fitness_appl_db_to_dobj(getRegnNo(), null);
                }

                if (getOwnerDetail().getFuel() > 0) {
                    int fuel_type = getOwnerDetail().getFuel();
                    if (fuel_type == TableConstants.VM_FUEL_CNG_TYPE
                            || fuel_type == TableConstants.VM_FUEL_TYPE_PETROL_CNG
                            || fuel_type == TableConstants.VM_FUEL_TYPE_LPG
                            || fuel_type == TableConstants.VM_FUEL_TYPE_PETROL_LPG) {
                        cng_dobj = RetroFittingDetailsImpl.setCngDetails_db_to_dobj_VT(getRegnNo(), getOwnerDetail().getState_cd(), getOwnerDetail().getOff_cd());
                        if (cng_dobj != null) {
                            getcNG_Details_Bean().setDobj_To_Bean(cng_dobj);

                        }
                    }
                }

                if (getHsrpDetailsBean() != null) {
                    getHsrpDetailsBean().setHSRPFlag(getOwnerDetail().getState_cd(), getOwnerDetail().getOff_cd());
                    getHsrpDetailsBean().setRegNo(this.getRegnNo().trim(), getOwnerDetail().getState_cd(), getOwnerDetail().getOff_cd());
                }

                if (getScDetailsBean() != null) {
                    getScDetailsBean().setRegNo(this.getRegnNo().trim(), getOwnerDetail().getState_cd(), getOwnerDetail().getOff_cd(), smartcardStatus);
                }

                trailer_dobj = trailer_Impl.set_trailer_dtls_to_dobj("", this.getRegnNo().trim(), 0);
                if (trailer_dobj != null) {
                    getTrailer_bean().set_trailer_dobj_to_bean(trailer_dobj);
                }

                ins_dobj = InsImpl.set_ins_dtls_db_to_dobj(this.getRegnNo().trim(), null, getOwnerDetail().getState_cd(), getOwnerDetail().getOff_cd());
                if (ins_dobj != null) {
                    getIns_bean().set_Ins_dobj_to_bean(ins_dobj);
                    getOwnerDetail().setInsDobj(ins_dobj);
                }

                HpaImpl hpa_Impl = new HpaImpl();
                hypth = hpa_Impl.getHypoDetails(null, TableConstants.VM_TRANSACTION_MAST_REM_HYPO, this.getRegnNo().trim(), true, getOwnerDetail().getState_cd());
                getHypothecationDetails_bean().setListHypthDetails(hypth);

                setRegStatus(ServerUtil.fillRegStatusParameters(getOwnerDetail()));

                getIns_bean().componentReadOnly(false);
                getIns_bean().setGovtVehFlag(true);
                getTrailer_bean().componentReadOnly(false);
                regnNoPanelShow = false;
                detailsPanelShow = true;
            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", TableConstants.NOC_DATA_NOT_FOUND));
                return;
            }
        } catch (VahanException ve) {
            ex = ve;
        } catch (Exception exp) {
            ex = exp;
        }

        if (ex != null) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", "Problem In Data, Please contact Administrator"));
        }
    }

    public void endorse() {
        NocEndorsementImpl noc_endorse_impl = new NocEndorsementImpl();
        Exception ex = null;
        try {
            noc_endorse_impl.insertDeleteForNocEndorsement(regnNo, ownerDetail.getChasi_no(), nocDobj, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), fitnessDobj, exArmyDobj, axleDetailsdobj, ins_dobj, imp_dobj, cng_dobj, trailer_dobj, hypth);
            setSuccessNocEndorsementMessg("Noc Endorsement Done Succesfully");
            PrimeFaces.current().ajax().update("showNocEndorMessg");
            PrimeFaces.current().executeScript("PF('successNocEndorDialog').show()");
        } catch (VahanException vex) {
            ex = vex;
        } catch (Exception e) {
            ex = e;
        }
        if (ex != null) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", "Problem In Endorsement of Data"));
        }
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

    public boolean isIsTransport() {
        return isTransport;
    }

    /**
     * @param isTransport the isTransport to set
     */
    public void setIsTransport(boolean isTransport) {
        this.isTransport = isTransport;
    }

    /**
     * @return the regStatus
     */
    public RegistrationStatusParametersDobj getRegStatus() {
        return regStatus;
    }

    /**
     * @param regStatus the regStatus to set
     */
    public void setRegStatus(RegistrationStatusParametersDobj regStatus) {
        this.regStatus = regStatus;
    }

    /**
     * @return the smartcardStatus
     */
    public boolean isSmartcardStatus() {
        return smartcardStatus;
    }

    /**
     * @param smartcardStatus the smartcardStatus to set
     */
    public void setSmartcardStatus(boolean smartcardStatus) {
        this.smartcardStatus = smartcardStatus;
    }

    /**
     * @return the hsrpStatus
     */
    public boolean isHsrpStatus() {
        return hsrpStatus;
    }

    /**
     * @param hsrpStatus the hsrpStatus to set
     */
    public void setHsrpStatus(boolean hsrpStatus) {
        this.hsrpStatus = hsrpStatus;
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
     * @return the trailer_bean
     */
    public Trailer_bean getTrailer_bean() {
        return trailer_bean;
    }

    /**
     * @param trailer_bean the trailer_bean to set
     */
    public void setTrailer_bean(Trailer_bean trailer_bean) {
        this.trailer_bean = trailer_bean;
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

    /**
     * @return the hypothecationDetails_bean
     */
    public HypothecationDetailsBean getHypothecationDetails_bean() {
        return hypothecationDetails_bean;
    }

    /**
     * @param hypothecationDetails_bean the hypothecationDetails_bean to set
     */
    public void setHypothecationDetails_bean(HypothecationDetailsBean hypothecationDetails_bean) {
        this.hypothecationDetails_bean = hypothecationDetails_bean;
    }

    /**
     * @return the exArmyBean
     */
    public ExArmyBean getExArmyBean() {
        return exArmyBean;
    }

    /**
     * @param exArmyBean the exArmyBean to set
     */
    public void setExArmyBean(ExArmyBean exArmyBean) {
        this.exArmyBean = exArmyBean;
    }

    /**
     * @return the axleBean
     */
    public AxleBean getAxleBean() {
        return axleBean;
    }

    /**
     * @param axleBean the axleBean to set
     */
    public void setAxleBean(AxleBean axleBean) {
        this.axleBean = axleBean;
    }

    /**
     * @return the hsrpDetailsBean
     */
    public HSRPDetailsBean getHsrpDetailsBean() {
        return hsrpDetailsBean;
    }

    /**
     * @param hsrpDetailsBean the hsrpDetailsBean to set
     */
    public void setHsrpDetailsBean(HSRPDetailsBean hsrpDetailsBean) {
        this.hsrpDetailsBean = hsrpDetailsBean;
    }

    /**
     * @return the scDetailsBean
     */
    public SCDetailsBean getScDetailsBean() {
        return scDetailsBean;
    }

    /**
     * @param scDetailsBean the scDetailsBean to set
     */
    public void setScDetailsBean(SCDetailsBean scDetailsBean) {
        this.scDetailsBean = scDetailsBean;
    }

    /**
     * @return the regnNoPanelShow
     */
    public boolean isRegnNoPanelShow() {
        return regnNoPanelShow;
    }

    /**
     * @param regnNoPanelShow the regnNoPanelShow to set
     */
    public void setRegnNoPanelShow(boolean regnNoPanelShow) {
        this.regnNoPanelShow = regnNoPanelShow;
    }

    /**
     * @return the detailsPanelShow
     */
    public boolean isDetailsPanelShow() {
        return detailsPanelShow;
    }

    /**
     * @param detailsPanelShow the detailsPanelShow to set
     */
    public void setDetailsPanelShow(boolean detailsPanelShow) {
        this.detailsPanelShow = detailsPanelShow;
    }

    /**
     * @return the cng_dobj
     */
    public RetroFittingDetailsDobj getCng_dobj() {
        return cng_dobj;
    }

    /**
     * @param cng_dobj the cng_dobj to set
     */
    public void setCng_dobj(RetroFittingDetailsDobj cng_dobj) {
        this.cng_dobj = cng_dobj;
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
     * @return the importedVehbean
     */
    public ImportedVehicleBean getImportedVehbean() {
        return importedVehbean;
    }

    /**
     * @param importedVehbean the importedVehbean to set
     */
    public void setImportedVehbean(ImportedVehicleBean importedVehbean) {
        this.importedVehbean = importedVehbean;
    }

    /**
     * @return the successNocEndorsementMessg
     */
    public String getSuccessNocEndorsementMessg() {
        return successNocEndorsementMessg;
    }

    /**
     * @param successNocEndorsementMessg the successNocEndorsementMessg to set
     */
    public void setSuccessNocEndorsementMessg(String successNocEndorsementMessg) {
        this.successNocEndorsementMessg = successNocEndorsementMessg;
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

    /**
     * @return the nocDobj
     */
    public NocDobj getNocDobj() {
        return nocDobj;
    }

    /**
     * @param nocDobj the nocDobj to set
     */
    public void setNocDobj(NocDobj nocDobj) {
        this.nocDobj = nocDobj;
    }
}
