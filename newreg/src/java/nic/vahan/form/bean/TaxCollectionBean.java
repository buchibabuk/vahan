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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.CommonUtils.TaxUtils;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.common.tax.VahanTaxClient;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.form.dobj.DOTaxDetail;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.TaxFieldDobj;
import nic.vahan.form.dobj.TaxcollectDobj;
import nic.vahan.form.impl.TaxCollectionImpl;
import nic.vahan.form.impl.TaxServer_Impl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
//import tax.web.service.VahanTaxParameters;
import nic.vahan.form.dobj.tax.VahanTaxParameters;
import static nic.vahan.form.impl.TaxServer_Impl.callTaxService;

/**
 *
 * @author tranC113
 */
@ManagedBean(name = "taxCollectionBean")
@ViewScoped
public class TaxCollectionBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(TaxCollectionBean.class);
    private String pmt_type;
    private List list_vh_pmttype = new ArrayList();
    private String pmt_catg;
    private List list_vh_pmtcatg = new ArrayList();
    private String numberoftrips;
    private String routeLen;
    private String vh_class;
    private List list_vh_class = new ArrayList();
    private String vch_catg;
    private List list_vm_catg = new ArrayList();
    private String seat_cap;
    private String stand_cap;
    private String sleep_cap;
    private String ul_weight;
    private String l_weight;
    private String fuel;
    private List list_fuel = new ArrayList();
    private String cubic_cap;
    private String f_area;
    private String ac_fitted;
    private String owner_cd;
    private List list_owner_cd = new ArrayList();
    private String other_criteria;
    private List list_other_criteria = new ArrayList();
    private String imported_veh;
    private List list_imported_veh = new ArrayList();
    private String vch_purchase_as;
    private List list_purchase_as = new ArrayList();
    private String sale_amt;
    private String hp;
    private String tax_mode;
    private List list_tax_mode = new ArrayList();
    private String state_cd;
    private String vehType = "-1";
    private List list_veh_type = new ArrayList();
    private boolean disable;
    private TaxFormPanelBean taxFormBean = null;
    private TaxCollectionImpl taxImp = null;
    private TaxcollectDobj taxcollectdobj;
    private Date today = new Date();
    private String masterLayout = "/masterLayoutPage.xhtml";
    private String state_code;
    private List list_vm_state = new ArrayList();
    private boolean pmt_dtls_render = false;
    private String services_type;
    private List ser_type = new ArrayList();
    private String domain_cd;
    private List list_domain_cd = new ArrayList();
    private boolean showTaxBrkupPanel;
    private boolean ispmt_type;
    private boolean ispmt_catg;
    private boolean isrouteLen;
    private boolean isseat_cap;
    private boolean isstand_cap;
    private boolean issleep_cap;
    private boolean isul_weight;
    private boolean isl_weight;
    private boolean isfuel;
    private boolean iscubic_cap;
    private boolean isf_area;
    private boolean isac_fitted;
    private boolean isowner_cd;
    private boolean isother_criteria;
    private boolean isimported_veh;
    private boolean isvch_purchase_as;
    private boolean issale_amt;
    private boolean istax_mode;
    private boolean ishp;
    private boolean isservices_type;
    private boolean isdomain_cd;
    private boolean isregn_dt;
    private boolean isnumberoftrips;
    private List<TaxFormPanelBean> taxModList = new ArrayList();
    private TaxFieldDobj taxFieldDobj = null;
    private Date purchase_dt;
    private Date regn_dt;
    private String regnType;
    private List listRegnType;
    private boolean isRegnType;
    private boolean vhClassDisable;
    private boolean vhCatgDisable;
    private Owner_dobj owner_dobj = new Owner_dobj();
    private VahanTaxParameters taxParameters = new VahanTaxParameters();
    private boolean taxmodeDisable;

    public TaxCollectionBean() {

        masterLayout = "/ui/eapplication/masterLayoutEapplication.xhtml";

        setShowTaxBrkupPanel(false);
        this.regn_dt = today;
        this.purchase_dt = today;
        setVhClassDisable(true);
        setVhCatgDisable(true);
        setTaxmodeDisable(true);
        String[][] data = MasterTableFiller.masterTables.VM_OWCODE.getData();
        for (int i = 0; i < data.length; i++) {
            list_owner_cd.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_VCH_CATG.getData();
        list_vm_catg.add(new SelectItem("-1", "--Select--"));
        for (int i = 0; i < data.length; i++) {
            list_vm_catg.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.TM_STATE.getData();
        for (int i = 0; i < data.length; i++) {
            if (data[i][3] != null && !data[i][3].isEmpty()) {
                list_vm_state.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
        data = MasterTableFiller.masterTables.VM_REGION.getData();
        for (int i = 0; i < data.length; i++) {
            list_domain_cd.add(new SelectItem(data[i][1], data[i][2]));
        }
        data = MasterTableFiller.masterTables.vm_service_type.getData();
        for (int i = 0; i < data.length; i++) {
            ser_type.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
        list_vh_class.add(new SelectItem("-1", "--Select--"));
        for (int i = 0; i < data.length; i++) {
            list_vh_class.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_FUEL.getData();
        for (int i = 0; i < data.length; i++) {
            list_fuel.add(new SelectItem(data[i][0], data[i][1]));
        }
        list_other_criteria = new ArrayList();
        data = MasterTableFiller.masterTables.VM_OTHER_CRITERIA.getData();
        for (int i = 0; i < data.length; i++) {
            list_other_criteria.add(new SelectItem(data[i][0], data[i][1]));
        }
        list_imported_veh = new ArrayList();
        list_imported_veh.add(new SelectItem("N", "No"));
        list_imported_veh.add(new SelectItem("Y", "Yes"));

        list_purchase_as = new ArrayList();
        list_purchase_as.add(new SelectItem("B", "Fully Built"));
        list_purchase_as.add(new SelectItem("C", "Drive Away Chasis"));
        list_veh_type.clear();
        list_veh_type.add(new SelectItem("-1", "--Select--"));
        list_veh_type.add(new SelectItem("1", "Transport"));
        list_veh_type.add(new SelectItem("2", "Non-Transport"));
        listRegnType = new ArrayList();
        data = MasterTableFiller.masterTables.VM_REGN_TYPE.getData();
        for (int i = 0; i < data.length; i++) {
            listRegnType.add(new SelectItem(data[i][0], data[i][1]));
        }
    }

    public void showTax() {
        try {
            TaxcollectDobj tax_collect_dobj = set_Tax_appl_bean_to_dobj();
            setOwnerAndVehicleparameter(tax_collect_dobj);
            getTaxFormBean().reset();
            VahanTaxClient taxClient = new VahanTaxClient();
            List<DOTaxDetail> tempTaxList = null;
            if (!getTaxModList().isEmpty()) {
                for (TaxFormPanelBean taxModeList : getTaxModList()) {
                    taxParameters.setPUR_CD(taxModeList.getPur_cd());
                    tempTaxList = callTaxService(taxParameters);
                    //String taxServiceResponse = taxClient.getTaxDetails(taxParameters);
                    //tempTaxList = taxClient.parseTaxResponse(taxServiceResponse, taxModeList.getPur_cd(), getTaxParameters().getTAXMODE());
                    if (tempTaxList != null && !tempTaxList.isEmpty()) {
                        //tempTaxList = TaxUtils.sortTaxDetails(tempTaxList);
                        getTaxFormBean().getTaxDescriptionList().addAll(tempTaxList);
                    }
                }
            }

            getTaxFormBean().updateTaxBean();
            if (getTaxFormBean().getFinalTaxAmount() == 0) {
                setShowTaxBrkupPanel(false);
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Either Tax Slab not found or Tax Slab for selected criteria not defined!!!"));
                return;
            } else {
                setShowTaxBrkupPanel(true);
            }
        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage("MessageId", message);
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void setOwnerAndVehicleparameter(TaxcollectDobj tax_collect_dobj) {
        try {
            if (tax_collect_dobj != null) {
                setTaxFieldDobj(TaxServer_Impl.getTaxFields(tax_collect_dobj.getState_cd()));
                getTaxParameters().setAC_FITTED(tax_collect_dobj.getAc_fitted());
                getOwner_dobj().setAc_fitted(tax_collect_dobj.getAc_fitted());
                getTaxParameters().setOWNER_CD((Integer) tax_collect_dobj.getOwner_cd());
                getOwner_dobj().setOwner_cd(tax_collect_dobj.getOwner_cd());
                getTaxParameters().setCC(tax_collect_dobj.getCubic_cap());//float type
                getOwner_dobj().setCubic_cap(tax_collect_dobj.getCubic_cap());
                getTaxParameters().setFLOOR_AREA(tax_collect_dobj.getFloor_area());
                getOwner_dobj().setFloor_area(tax_collect_dobj.getFloor_area());
                getTaxParameters().setFUEL((Integer) tax_collect_dobj.getFuel());
                getOwner_dobj().setFuel(tax_collect_dobj.getFuel());
                getTaxParameters().setLD_WT((Integer) tax_collect_dobj.getLd_wt());
                getOwner_dobj().setLd_wt(tax_collect_dobj.getLd_wt());
                getTaxParameters().setUNLD_WT((Integer) tax_collect_dobj.getUnld_wt());
                getOwner_dobj().setUnld_wt((int) tax_collect_dobj.getUnld_wt());
                getTaxParameters().setSALE_AMT((Integer) tax_collect_dobj.getSale_amt());
                getOwner_dobj().setSale_amt((int) tax_collect_dobj.getSale_amt());
                getTaxParameters().setHP(tax_collect_dobj.getHp());
                getOwner_dobj().setHp(tax_collect_dobj.getHp());
                getTaxParameters().setSLEEPAR_CAP((Integer) tax_collect_dobj.getSleeper_cap());
                getOwner_dobj().setSleeper_cap((int) tax_collect_dobj.getSleeper_cap());
                getTaxParameters().setSEAT_CAP((Integer) tax_collect_dobj.getSeat_cap());
                getOwner_dobj().setSeat_cap((int) tax_collect_dobj.getSeat_cap());
                getTaxParameters().setVCH_IMPORTED(tax_collect_dobj.getImported_veh());
                getOwner_dobj().setImported_vch(tax_collect_dobj.getImported_veh());
                getTaxParameters().setSTAND_CAP((Integer) tax_collect_dobj.getStand_cap());
                getOwner_dobj().setStand_cap((int) tax_collect_dobj.getStand_cap());
                getTaxParameters().setSTATE_CD(tax_collect_dobj.getState_cd());
                getOwner_dobj().setState_cd(tax_collect_dobj.getState_cd());
                getTaxParameters().setVH_CLASS((Integer) tax_collect_dobj.getVh_class());
                getOwner_dobj().setVh_class((int) tax_collect_dobj.getVh_class());
                getTaxParameters().setTAX_MODE(tax_collect_dobj.getTax_mode());
                getOwner_dobj().setTax_mode(tax_collect_dobj.getTax_mode());
                getTaxParameters().setVCH_CATG(tax_collect_dobj.getVch_catg());
                getOwner_dobj().setVch_catg(tax_collect_dobj.getVch_catg());
                getTaxParameters().setOTHER_CRITERIA((Integer) tax_collect_dobj.getOther_criteria());
                getOwner_dobj().setOther_criteria(tax_collect_dobj.getOther_criteria());
                getTaxParameters().setVEH_PURCHASE_AS(tax_collect_dobj.getVch_purchase_as());
                getOwner_dobj().setVch_purchase_as(tax_collect_dobj.getVch_purchase_as());
                getOwner_dobj().setVehType(Integer.parseInt(this.getVehType()));
                getOwner_dobj().setPurchase_dt(tax_collect_dobj.getPurchase_dt());
                if (this.getVehType().equalsIgnoreCase("1")) {

                    if (tax_collect_dobj.getPmt_type() != null && !tax_collect_dobj.getPmt_type().trim().isEmpty()) {
                        if (!tax_collect_dobj.getPmt_type().equalsIgnoreCase("-1")) {
                            getTaxParameters().setPERMIT_TYPE((Integer) Integer.parseInt(tax_collect_dobj.getPmt_type().trim()));
                        }
                    }
                    if (tax_collect_dobj.getPmt_catg() != null && !tax_collect_dobj.getPmt_catg().trim().isEmpty()) {
                        if (!tax_collect_dobj.getPmt_catg().equalsIgnoreCase("-1")) {
                            getTaxParameters().setPERMIT_SUB_CATG((Integer) Integer.parseInt(tax_collect_dobj.getPmt_catg().trim()));
                            getOwner_dobj().setPmt_catg(Integer.parseInt(tax_collect_dobj.getPmt_catg().trim()));
                        }
                    }
                    if (tax_collect_dobj.getServices_type() != 0) {
                        getTaxParameters().setSERVICE_TYPE((Integer) tax_collect_dobj.getServices_type());
                    }
                    if (tax_collect_dobj.getDomain_cd() != 0) {
                        getTaxParameters().setDOMAIN_CD((Integer) tax_collect_dobj.getDomain_cd());
                    }
                    getTaxParameters().setNO_OF_TRIPS_PER_DAY((Integer) tax_collect_dobj.getNumberoftrips());
                    getTaxParameters().setROUTE_LENGTH(tax_collect_dobj.getRouteLen());
                }
                //DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                //Date date = new Date();
                getTaxParameters().setREGN_DATE(DateUtils.parseDate(tax_collect_dobj.getTax_frdt()));
                getOwner_dobj().setRegn_dt(tax_collect_dobj.getTax_frdt());
                if (tax_collect_dobj.getRegnType() != null && !tax_collect_dobj.getRegnType().equals("")) {
                    if (tax_collect_dobj.getRegnType().equalsIgnoreCase("O")) {
                        getTaxParameters().setTAX_DUE_FROM_DATE(DateUtils.parseDate(tax_collect_dobj.getTax_frdt()));
                        getTaxParameters().setREGN_DATE(DateUtils.parseDate(tax_collect_dobj.getPurchase_dt()));
                    } else {
                        getTaxParameters().setTAX_DUE_FROM_DATE(DateUtils.parseDate(tax_collect_dobj.getPurchase_dt()));
                    }
                }

                getTaxParameters().setNEW_VCH("Y");
                getTaxParameters().setREGN_TYPE(tax_collect_dobj.getRegnType());
                getTaxParameters().setVCH_TYPE(tax_collect_dobj.getVchType());
                getTaxModList().clear();
                if (this.getVehType() != null && getOwner_dobj() != null && tax_collect_dobj.getVch_catg() != null && (int) tax_collect_dobj.getVh_class() > 0) {
                    VehicleParameters vehicleParameters = FormulaUtils.fillVehicleParametersFromDobj(getOwner_dobj());
                    setTaxModList(TaxServer_Impl.getListTaxForm(getOwner_dobj(), vehicleParameters));

                    if (!getTaxModList().isEmpty()) {
                        for (TaxFormPanelBean taxModeList : getTaxModList()) {
                            list_tax_mode = getListTaxModes(getOwner_dobj(), taxModeList.getPur_cd());
                        }
                    }

                } else {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", "Please Select Vehicle Class and Vehicle Category for Loading Tax Mode(s)"));
                }
            } else {
                setShowTaxBrkupPanel(false);
            }
        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage("MessageId", message);
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

    }

    private List<SelectItem> getListTaxModes(Owner_dobj ownerDobj, int pur_cd) throws VahanException {
        String[] taxModes = TaxServer_Impl.getAvailalableTaxModes(ownerDobj, pur_cd, null);
        String[][] dataTaxModes = MasterTableFiller.masterTables.VM_TAX_MODE.getData();
        List<SelectItem> list = null;
        if (taxModes != null) {
            if (list_tax_mode.isEmpty()) {
                list_tax_mode.add(new SelectItem("-1", "--Select--"));
            } else {
                list = list_tax_mode;
            }
            for (int i = 0; i < taxModes.length; i++) {
                outer:
                for (int ii = 0; ii < dataTaxModes.length; ii++) {
                    if (dataTaxModes[ii][0].trim().equals(taxModes[i].trim())) {
                        if (list != null) {
                            for (int j = 0; j < list.size(); j++) {
                                if (taxModes[i].trim().contains(list.get(j).getValue().toString())) {
                                    break outer;
                                }
                            }
                        }
                        list_tax_mode.add(new SelectItem(dataTaxModes[ii][0], dataTaxModes[ii][1]));
                        break;
                    }
                }
            }
        }
        return list_tax_mode;
    }

    public void purchaseDateListener() {
        if (getRegn_dt() != null) {
            if (getPurchase_dt().after(getRegn_dt())) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Purchase date must be less than Registration date!!!"));
                return;
            }
        }
    }

    public void regnDateListener() {
        if (getPurchase_dt() != null) {
            if (getPurchase_dt().after(getRegn_dt())) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Purchase date must be less than Registration date!!!"));
                return;
            }
        }
    }

    public TaxcollectDobj set_Tax_appl_bean_to_dobj() {
        FacesMessage message = null;
        TaxcollectDobj tax_collect_dobj = null;
        try {
            tax_collect_dobj = new TaxcollectDobj();
            if (("-1").equalsIgnoreCase(getState_code())) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select the State ", "Please select the State");
                FacesContext.getCurrentInstance().addMessage("MessageId", message);
                return tax_collect_dobj = null;
            }
            if (("-1").equalsIgnoreCase((String) getVehType())) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select the Vehicle Type ", "Please select the Vehicle Type");
                FacesContext.getCurrentInstance().addMessage("MessageId", message);
                return tax_collect_dobj = null;
            }
            if (("-1").equalsIgnoreCase(getVh_class())) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select the Vehicle Class ", "Please select the Vehicle class");
                FacesContext.getCurrentInstance().addMessage("MessageId", message);
                return tax_collect_dobj = null;
            }
            if (("-1").equalsIgnoreCase(getVch_catg())) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select the Vehicle Category", "Please select the Vehicle Category");
                FacesContext.getCurrentInstance().addMessage("MessageId", message);
                return tax_collect_dobj = null;
            }
            if (("-1").equalsIgnoreCase((String) getTax_mode())) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select the Tax Mode", "Please select the Tax Mode");
                FacesContext.getCurrentInstance().addMessage("MessageId", message);
                return tax_collect_dobj = null;
            }
            if (("-1").equalsIgnoreCase(getFuel())) {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select the Fuel", "Please select the Fuel");
                FacesContext.getCurrentInstance().addMessage("MessageId", message);
                return tax_collect_dobj = null;
            }
            if (this.getOwner_cd() != null && this.getOwner_cd().trim().length() > 0) {
                tax_collect_dobj.setOwner_cd(Integer.parseInt(this.getOwner_cd().trim()));
            } else {
                tax_collect_dobj.setOwner_cd(0);
            }
            if (this.getVh_class() != null && this.getVh_class().trim().length() > 0) {
                tax_collect_dobj.setVh_class(Integer.parseInt(this.getVh_class().trim()));
            } else {
                tax_collect_dobj.setVh_class(0);
            }
            if (this.seat_cap != null && this.seat_cap.trim().length() > 0) {
                tax_collect_dobj.setSeat_cap(Integer.parseInt(this.seat_cap.trim()));
            } else {
                tax_collect_dobj.setSeat_cap(0);
            }
            if (this.stand_cap != null && this.stand_cap.trim().length() > 0) {
                tax_collect_dobj.setStand_cap(Integer.parseInt(this.stand_cap.trim()));
            } else {
                tax_collect_dobj.setStand_cap(0);
            }
            if (this.sleep_cap != null && this.sleep_cap.trim().length() > 0) {
                tax_collect_dobj.setSleeper_cap(Integer.parseInt(this.sleep_cap.trim()));
            } else {
                tax_collect_dobj.setSleeper_cap(0);
            }
            if (this.ul_weight != null && this.ul_weight.trim().length() > 0) {
                tax_collect_dobj.setUnld_wt(Integer.parseInt(this.ul_weight.trim()));
            } else {
                tax_collect_dobj.setUnld_wt(0);
            }
            if (this.l_weight != null && this.l_weight.trim().length() > 0) {
                tax_collect_dobj.setLd_wt(Integer.parseInt(this.l_weight.trim()));
            } else {
                tax_collect_dobj.setLd_wt(0);
            }
            if (this.cubic_cap != null && this.cubic_cap.trim().length() > 0) {
                tax_collect_dobj.setCubic_cap(Float.parseFloat(this.cubic_cap.trim()));
            } else {
                tax_collect_dobj.setCubic_cap(0);
            }
            if (this.getFuel() != null && this.getFuel().trim().length() > 0) {
                tax_collect_dobj.setFuel(Integer.parseInt(this.getFuel().trim()));
            } else {
                tax_collect_dobj.setFuel(0);
            }
            if (this.sale_amt != null && this.sale_amt.trim().length() > 0) {
                tax_collect_dobj.setSale_amt(Integer.parseInt(this.sale_amt.trim()));
            } else {
                tax_collect_dobj.setSale_amt(0);
            }
            if (this.getTax_mode() != null) {
                tax_collect_dobj.setTax_mode(this.getTax_mode());
            } else {
                tax_collect_dobj.setTax_mode("");
            }
            if (this.getImported_veh() != null) {
                tax_collect_dobj.setImported_veh(this.getImported_veh());
            } else {
                tax_collect_dobj.setImported_veh("");
            }

            if (this.f_area != null && this.f_area.trim().length() > 0) {
                tax_collect_dobj.setFloor_area(Float.parseFloat(this.f_area.trim()));
            } else {
                tax_collect_dobj.setFloor_area(0);
            }
            if (this.getAc_fitted() != null) {
                tax_collect_dobj.setAc_fitted(getAc_fitted().trim());
            } else {
                tax_collect_dobj.setAc_fitted("");
            }
            if (this.getVch_purchase_as() != null) {
                tax_collect_dobj.setVch_purchase_as(this.getVch_purchase_as());
            } else {
                tax_collect_dobj.setVch_purchase_as("");
            }
            if (this.getVch_catg() != null) {
                tax_collect_dobj.setVch_catg(this.getVch_catg());
            } else {
                tax_collect_dobj.setVch_catg("");
            }
            if (this.getState_code() != null) {
                tax_collect_dobj.setState_cd(this.getState_code().trim());
            } else {
                tax_collect_dobj.setState_cd("");
            }
            if (this.getOther_criteria() != null && this.getOther_criteria().trim().length() > 0) {
                tax_collect_dobj.setOther_criteria(Integer.parseInt(this.getOther_criteria().trim()));
            } else {
                tax_collect_dobj.setOther_criteria(0);
            }
            if (this.getRegn_dt() != null) {
                tax_collect_dobj.setTax_frdt((java.util.Date) this.getRegn_dt());
            }
            if (this.getPmt_type() != null && !this.pmt_type.isEmpty() && !this.pmt_type.trim().equalsIgnoreCase("-1")) {
                tax_collect_dobj.setPmt_type(this.getPmt_type().trim());
            } else {
                tax_collect_dobj.setPmt_type("");
            }
            if (this.getPmt_catg() != null && !this.pmt_catg.trim().isEmpty() && !this.pmt_catg.trim().equalsIgnoreCase("-1")) {
                tax_collect_dobj.setPmt_catg(this.getPmt_catg().trim());
            } else {
                tax_collect_dobj.setPmt_catg("");
            }
            if (this.numberoftrips != null && this.numberoftrips.trim().length() > 0) {
                tax_collect_dobj.setNumberoftrips(Integer.parseInt(this.numberoftrips.trim()));
            } else {
                tax_collect_dobj.setNumberoftrips(0);
            }
            if (this.getHp() != null && this.getHp().trim().length() > 0) {
                tax_collect_dobj.setHp(Float.parseFloat(this.hp.trim()));
            } else {
                tax_collect_dobj.setHp(0);
            }
            if (this.getRouteLen() != null && this.routeLen.trim().length() > 0) {
                tax_collect_dobj.setRouteLen(Double.parseDouble(this.routeLen.trim()));
            } else {
                tax_collect_dobj.setRouteLen(0);
            }
            if (this.getServices_type() != null && this.getServices_type().trim().length() > 0 && !this.getServices_type().trim().equalsIgnoreCase("-1")) {
                tax_collect_dobj.setServices_type(Integer.parseInt(this.getServices_type().trim()));
            } else {
                tax_collect_dobj.setServices_type(0);
            }
            if (this.getDomain_cd() != null && this.getDomain_cd().trim().length() > 0 && !this.getDomain_cd().trim().equalsIgnoreCase("-1")) {
                tax_collect_dobj.setDomain_cd(Integer.parseInt(this.getDomain_cd().trim()));
            } else {
                tax_collect_dobj.setDomain_cd(0);
            }
            if (this.getPurchase_dt() != null) {
                tax_collect_dobj.setPurchase_dt(this.getPurchase_dt());
            }
            if (this.getRegnType() != null && !this.getRegnType().isEmpty() && !this.getRegnType().equals("")) {
                tax_collect_dobj.setRegnType(this.regnType);
            } else {
                tax_collect_dobj.setRegnType("N");
            }
            if (this.getVehType() != null && this.getVehType().trim().length() > 0) {
                tax_collect_dobj.setVchType(Integer.parseInt(this.getVehType()));
            } else {
                tax_collect_dobj.setVchType(0);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

        return tax_collect_dobj;
    }

    public void vehStateClassListener(AjaxBehaviorEvent event) {
        reset();
        setVehType("-1");
        setState_cd(getState_code().trim());
        taxImp = new TaxCollectionImpl();
        list_domain_cd.clear();
        setList_domain_cd(taxImp.getDomainListOnStateCode(getState_cd()));
        list_other_criteria.clear();
        setList_other_criteria(taxImp.getOtherCriteriaListOnStateCode(getState_cd()));
        setPmt_dtls_render(false);
        setShowTaxBrkupPanel(false);
        vehicleComponentEditable(false);
        setVh_class(list_vh_class.get(0).toString());
        setVch_catg(list_vm_catg.get(0).toString());
    }

    public void vehTypeListener(AjaxBehaviorEvent event) {
        vehicleComponentEditable(true);
        String vehtype = getVehType();//(String) event.getNewValue();       
        String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
        list_vm_catg.clear();
        if (vehtype.equalsIgnoreCase("-1")) {
            list_vh_class.add(new SelectItem("-1", "Select"));
            list_vm_catg.add(new SelectItem("-1", "Select"));
            vehicleComponentEditable(false);
            setPmt_dtls_render(false);
        }
        vehicleComponentEditable(true);
        setShowTaxBrkupPanel(false);

        if (vehtype.equalsIgnoreCase("1")) {
            list_vm_catg.clear();
            list_vh_class.clear();
            list_vm_catg.add(new SelectItem("-1", "Select"));
            list_vh_class.add(new SelectItem("-1", "Select"));
            for (int i = 0; i < data.length; i++) {
                if (data[i][2].equalsIgnoreCase("1")) {
                    list_vh_class.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        } else if (vehtype.equalsIgnoreCase("2")) {
            list_vh_class.clear();
            list_vm_catg.clear();
            list_vm_catg.add(new SelectItem("-1", "Select"));
            list_vh_class.add(new SelectItem("-1", "Select"));
            for (int i = 0; i < data.length; i++) {
                if (data[i][2].equalsIgnoreCase("2")) {
                    list_vh_class.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        }

        if (list_vh_class.size() > 0) {
            setVh_class(list_vh_class.get(0).toString());
        }
        resetAllReqFields();
    }

    public void vehicleTypeAndCatg(String vehClass) {
        String transport_catg = "";
        String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].equalsIgnoreCase(vehClass)) {
                transport_catg = data[i][3];
                break;
            }
        }
        data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
        list_vh_pmttype.clear();
        for (int i = 0; i < data.length; i++) {
            if (data[i][2].equalsIgnoreCase(transport_catg)) {
                list_vh_pmttype.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
    }

    public void onSelectPermitType(AjaxBehaviorEvent event) {
        list_vh_pmtcatg.clear();
        taxImp = new TaxCollectionImpl();
        String permit_type = getPmt_type().trim();
        setList_vh_pmtcatg(taxImp.getSelectePermitTypeAndGetPermitcage(Integer.parseInt(permit_type), getState_cd()));
    }

    public void vehClassListener(AjaxBehaviorEvent event) {
        reset();
        resetAllReqFields();
        String vehClass = getVh_class().trim();
        String taxRqrdField[] = ServerUtil.getFieldsReqForTax(getState_code(), Integer.parseInt(vehClass));
        if (taxRqrdField != null && taxRqrdField.length > 0) {
            for (int i = 0; i < taxRqrdField.length; i++) {
                if (taxRqrdField[i].equalsIgnoreCase("<20>")) {
                    setIsowner_cd(true);
                    continue;
                } else {
                }
                if (taxRqrdField[i].equalsIgnoreCase("<50>")) {
                    setIsregn_dt(true);
                    continue;
                }
                if (taxRqrdField[i].equalsIgnoreCase("<22>")) {
                    setIsfuel(true);
                    continue;
                }
                if (taxRqrdField[i].equalsIgnoreCase("<0>")) {
                    setIsseat_cap(true);
                    continue;
                }
                if (taxRqrdField[i].equalsIgnoreCase("<8>")) {
                    setIsstand_cap(true);
                    continue;
                }
                if (taxRqrdField[i].equalsIgnoreCase("<34>")) {
                    setIssleep_cap(true);
                    continue;
                }
                if (taxRqrdField[i].equalsIgnoreCase("<1>")) {
                    setIsul_weight(true);
                    continue;
                }
                if (taxRqrdField[i].equalsIgnoreCase("<2>")) {
                    setIsl_weight(true);
                    continue;
                }
                if (taxRqrdField[i].equalsIgnoreCase("<35>")) {
                    setIsac_fitted(true);
                    continue;
                }
                if (taxRqrdField[i].equalsIgnoreCase("<4>")) {
                    setIscubic_cap(true);
                    continue;
                }
                if (taxRqrdField[i].equalsIgnoreCase("<3>")) {
                    setIshp(true);
                    continue;
                }
                if (taxRqrdField[i].equalsIgnoreCase("<10>")) {
                    setIsf_area(true);
                    continue;
                }

                if (taxRqrdField[i].equalsIgnoreCase("<29>")) {
                    setIsother_criteria(true);
                    continue;
                }
                if (taxRqrdField[i].equalsIgnoreCase("<26>")) {
                    setIsvch_purchase_as(true);
                    continue;
                }
                if (taxRqrdField[i].equalsIgnoreCase("<47>")) {
                    setIsimported_veh(true);
                    continue;
                }
                if (taxRqrdField[i].equalsIgnoreCase("<59>")) {
                    setIstax_mode(true);
                    continue;
                }
                if (taxRqrdField[i].equalsIgnoreCase("<25>")) {
                    setIspmt_type(true);
                    setPmt_dtls_render(true);
                    continue;
                }
                if (taxRqrdField[i].equalsIgnoreCase("<28>")) {
                    setIspmt_catg(true);
                    setIspmt_type(true);
                    setPmt_dtls_render(true);
                    continue;
                }
                if (taxRqrdField[i].equalsIgnoreCase("<24>")) {
                    setIsservices_type(true);
                    if (getState_cd().equalsIgnoreCase("JH")) {
                        setPmt_dtls_render(true);
                    }
                    continue;
                }
                if (taxRqrdField[i].equalsIgnoreCase("<6>")) {
                    setIsnumberoftrips(true);
                    continue;
                } else {
                }
                if (taxRqrdField[i].equalsIgnoreCase("<7>")) {
                    setIsrouteLen(true);
                    continue;
                }
                if (taxRqrdField[i].equalsIgnoreCase("<21>") || taxRqrdField[i].equalsIgnoreCase("<27>")) {
                    setIsdomain_cd(true);
                    continue;
                }
                if (taxRqrdField[i].equalsIgnoreCase("<5>") || taxRqrdField[i].equalsIgnoreCase("<13>") || taxRqrdField[i].equalsIgnoreCase("<14>")) {
                    setIssale_amt(true);
                    continue;
                }
                if (taxRqrdField[i].equalsIgnoreCase("<42>")) {
                    setIsRegnType(true);
                    continue;
                }
            }
        }

        String[][] dataMap = MasterTableFiller.masterTables.VM_VHCLASS_CATG_MAP.getData();
        String[][] dataCatg = MasterTableFiller.masterTables.VM_VCH_CATG.getData();
        if (vehClass.equalsIgnoreCase("-1")) {
            list_vm_catg.clear();
            list_vm_catg.add(new SelectItem("-1", "Select"));
        } else {
            list_vm_catg.clear();
            list_vm_catg.add(new SelectItem("-1", "Select"));
            for (int i = 0; i < dataMap.length; i++) {
                if (dataMap[i][0].equals(vehClass)) {
                    for (int j = 0; j < dataCatg.length; j++) {
                        if (dataCatg[j][0].equals(dataMap[i][1])) {
                            list_vm_catg.add(new SelectItem(dataCatg[j][0], dataCatg[j][1]));
                        }
                    }
                }
            }
        }
        if (list_vm_catg.size() > 0) {
            setVch_catg(list_vm_catg.get(0).toString());
        }
        setTaxmodeDisable(false);
        vehicleTypeAndCatg(vehClass);
        getList_tax_mode().clear();
        TaxcollectDobj tax_collect_dobj = set_Tax_appl_bean_to_dobj();
        setOwnerAndVehicleparameter(tax_collect_dobj);

    }

    public void resetAllReqFields() {
        setIsowner_cd(false);
        setIsregn_dt(false);
        setIsfuel(false);
        setIsseat_cap(false);
        setIsstand_cap(false);
        setIssleep_cap(false);
        setIsul_weight(false);
        setIsl_weight(false);
        setIsac_fitted(false);
        setIscubic_cap(false);
        setIshp(false);
        setIsf_area(false);
        setIsother_criteria(false);
        setIsvch_purchase_as(false);
        setIsimported_veh(false);
        setIstax_mode(false);
        setIspmt_type(false);
        setIspmt_catg(false);
        setIsservices_type(false);
        setIsnumberoftrips(false);
        setIsrouteLen(false);
        setIsdomain_cd(false);
        setIssale_amt(false);
    }

    public void vehicleComponentEditable(boolean flag) {
        flag = !flag;
        setVhClassDisable(flag);
        setVhCatgDisable(flag);
    }

    private void setTotal() {
        long sumOfTotalPayableInterest = 0;
        long sumOfTotalPayablePenalty = 0;
        long sumOfTotalPayableRebate = 0;
        long sumOfTotalPayableSurcharge = 0;
        long sumOfTotalPayableTax = 0;
        for (DOTaxDetail dobj : getTaxFormBean().getTaxDescriptionList()) {

            sumOfTotalPayableInterest = (long) (sumOfTotalPayableInterest + dobj.getINTEREST());
            sumOfTotalPayablePenalty = (long) (sumOfTotalPayablePenalty + dobj.getPENALTY());
            sumOfTotalPayableRebate = (long) (sumOfTotalPayableRebate + dobj.getREBATE());
            sumOfTotalPayableSurcharge = (long) (sumOfTotalPayableSurcharge + dobj.getSURCHARGE());
            sumOfTotalPayableTax = (long) (sumOfTotalPayableTax + dobj.getAMOUNT());
            getTaxFormBean().setTotalPaybaleInterest(sumOfTotalPayableInterest);
            getTaxFormBean().setTotalPaybalePenalty(sumOfTotalPayablePenalty);
            getTaxFormBean().setTotalPaybaleRebate(sumOfTotalPayableRebate);
            getTaxFormBean().setTotalPaybaleSurcharge(sumOfTotalPayableSurcharge);
            getTaxFormBean().setTotalPaybaleTax(sumOfTotalPayableTax);

        }
        getTaxFormBean().setTotalPayableAmount(
                getTaxFormBean().getTotalPaybaleInterest() + getTaxFormBean().getTotalPaybalePenalty()
                - getTaxFormBean().getTotalPaybaleRebate() + getTaxFormBean().getTotalPaybaleSurcharge()
                + getTaxFormBean().getTotalPaybaleTax());

    }

    public void reset() {
        this.setOwner_cd(null);
        this.setSeat_cap(null);
        this.setStand_cap(null);
        this.setSleep_cap(null);
        this.setCubic_cap(null);
        this.setL_weight(null);
        this.setUl_weight(null);
        this.setF_area(null);
        this.setFuel(null);
        this.setAc_fitted(null);
        this.tax_mode = null;
        this.setSale_amt(null);
        this.setHp(null);
        this.vch_purchase_as = null;
        this.setOther_criteria(null);
        this.imported_veh = null;
        this.regn_dt = today;
    }

    public void regnTypeChangeListener(AjaxBehaviorEvent event) {
        String regnType = getRegnType();
        if (getTaxcollectdobj() == null) {
            TaxcollectDobj dobj = new TaxcollectDobj();
            setTaxcollectdobj(dobj);
        }
        getTaxcollectdobj().setRegnType(regnType);

    }

    public TaxFormPanelBean getTaxFormBean() {
        if (taxFormBean == null) {
            taxFormBean = new TaxFormPanelBean();
        }
        return taxFormBean;
    }

    public void setTaxFormBean(TaxFormPanelBean taxFormBean) {
        this.taxFormBean = taxFormBean;
    }

    /**
     * @return the list_vh_class
     */
    public List getList_vh_class() {
        return list_vh_class;
    }

    /**
     * @return the list_vm_catg
     */
    public List getList_vm_catg() {
        return list_vm_catg;
    }

    /**
     * @return the seat_cap
     */
    public String getSeat_cap() {
        return seat_cap;
    }

    /**
     * @return the stand_cap
     */
    public String getStand_cap() {
        return stand_cap;
    }

    /**
     * @return the sleep_cap
     */
    public String getSleep_cap() {
        return sleep_cap;
    }

    /**
     * @return the ul_weight
     */
    public String getUl_weight() {
        return ul_weight;
    }

    /**
     * @return the l_weight
     */
    public String getL_weight() {
        return l_weight;
    }

    /**
     * @return the list_fuel
     */
    public List getList_fuel() {
        return list_fuel;
    }

    /**
     * @return the cubic_cap
     */
    public String getCubic_cap() {
        return cubic_cap;
    }

    /**
     * @return the f_area
     */
    public String getF_area() {
        return f_area;
    }

    /**
     * @return the list_owner_cd
     */
    public List getList_owner_cd() {
        return list_owner_cd;
    }

    /**
     * @return the list_other_criteria
     */
    public List getList_other_criteria() {
        return list_other_criteria;
    }

    /**
     * @return the imported_veh
     */
    public String getImported_veh() {
        return imported_veh;
    }

    /**
     * @return the list_imported_veh
     */
    public List getList_imported_veh() {
        return list_imported_veh;
    }

    /**
     * @return the vch_purchase_as
     */
    public String getVch_purchase_as() {
        return vch_purchase_as;
    }

    /**
     * @return the list_purchase_as
     */
    public List getList_purchase_as() {
        return list_purchase_as;
    }

    /**
     * @return the tax_mode
     */
    public String getTax_mode() {
        return tax_mode;
    }

    /**
     * @return the list_tax_mode
     */
    public List getList_tax_mode() {
        return list_tax_mode;
    }

    /**
     * @return the state_cd
     */
    public String getState_cd() {
        return state_cd;
    }

    /**
     * @param state_cd the state_cd to set
     */
    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    /**
     * @param list_vh_class the list_vh_class to set
     */
    public void setList_vh_class(List list_vh_class) {
        this.list_vh_class = list_vh_class;
    }

    /**
     * @param list_vm_catg the list_vm_catg to set
     */
    public void setList_vm_catg(List list_vm_catg) {
        this.list_vm_catg = list_vm_catg;
    }

    /**
     * @param seat_cap the seat_cap to set
     */
    public void setSeat_cap(String seat_cap) {
        this.seat_cap = seat_cap;
    }

    /**
     * @param stand_cap the stand_cap to set
     */
    public void setStand_cap(String stand_cap) {
        this.stand_cap = stand_cap;
    }

    /**
     * @param sleep_cap the sleep_cap to set
     */
    public void setSleep_cap(String sleep_cap) {
        this.sleep_cap = sleep_cap;
    }

    /**
     * @param ul_weight the ul_weight to set
     */
    public void setUl_weight(String ul_weight) {
        this.ul_weight = ul_weight;
    }

    /**
     * @param l_weight the l_weight to set
     */
    public void setL_weight(String l_weight) {
        this.l_weight = l_weight;
    }

    /**
     * @param list_fuel the list_fuel to set
     */
    public void setList_fuel(List list_fuel) {
        this.list_fuel = list_fuel;
    }

    /**
     * @param cubic_cap the cubic_cap to set
     */
    public void setCubic_cap(String cubic_cap) {
        this.cubic_cap = cubic_cap;
    }

    /**
     * @param f_area the f_area to set
     */
    public void setF_area(String f_area) {
        this.f_area = f_area;
    }

    /**
     * @param list_owner_cd the list_owner_cd to set
     */
    public void setList_owner_cd(List list_owner_cd) {
        this.list_owner_cd = list_owner_cd;
    }

    /**
     * @param list_other_criteria the list_other_criteria to set
     */
    public void setList_other_criteria(List list_other_criteria) {
        this.list_other_criteria = list_other_criteria;
    }

    /**
     * @param imported_veh the imported_veh to set
     */
    public void setImported_veh(String imported_veh) {
        this.imported_veh = imported_veh;
    }

    /**
     * @param list_imported_veh the list_imported_veh to set
     */
    public void setList_imported_veh(List list_imported_veh) {
        this.list_imported_veh = list_imported_veh;
    }

    /**
     * @param vch_purchase_as the vch_purchase_as to set
     */
    public void setVch_purchase_as(String vch_purchase_as) {
        this.vch_purchase_as = vch_purchase_as;
    }

    /**
     * @param list_purchase_as the list_purchase_as to set
     */
    public void setList_purchase_as(List list_purchase_as) {
        this.list_purchase_as = list_purchase_as;
    }

    /**
     * @param tax_mode the tax_mode to set
     */
    public void setTax_mode(String tax_mode) {
        this.tax_mode = tax_mode;
    }

    /**
     * @param list_tax_mode the list_tax_mode to set
     */
    public void setList_tax_mode(List list_tax_mode) {
        this.list_tax_mode = list_tax_mode;
    }

    /**
     * @param tax_collect_dobj the tax_collect_dobj to set
     */
    public void setTaxcollectdobj(TaxcollectDobj taxcollectdobj) {
        this.taxcollectdobj = taxcollectdobj;
    }

    /**
     * @return the tax_collect_dobj
     */
    public TaxcollectDobj getTaxcollectdobj() {
        return taxcollectdobj;
    }

    /**
     * @return the sale_amt
     */
    public String getSale_amt() {
        return sale_amt;
    }

    /**
     * @param sale_amt the sale_amt to set
     */
    public void setSale_amt(String sale_amt) {
        this.sale_amt = sale_amt;
    }

    /**
     * @return the vehType
     */
    public String getVehType() {
        return vehType;
    }

    /**
     * @param vehType the vehType to set
     */
    public void setVehType(String vehType) {
        this.vehType = vehType;
    }

    /**
     * @return the list_veh_type
     */
    public List getList_veh_type() {
        return list_veh_type;
    }

    /**
     * @param list_veh_type the list_veh_type to set
     */
    public void setList_veh_type(List list_veh_type) {
        this.list_veh_type = list_veh_type;
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
     * @return the today
     */
    public Date getToday() {
        return new Date();
    }

    /**
     * @return the masterLayout
     */
    public String getMasterLayout() {
        return masterLayout;
    }

    /**
     * @param masterLayout the masterLayout to set
     */
    public void setMasterLayout(String masterLayout) {
        this.masterLayout = masterLayout;
    }

    /**
     * @return the list_vm_state
     */
    public List getList_vm_state() {
        return list_vm_state;
    }

    /**
     * @param list_vm_state the list_vm_state to set
     */
    public void setList_vm_state(List list_vm_state) {
        this.list_vm_state = list_vm_state;
    }

    /**
     * @return the pmt_dtls_render
     */
    public boolean isPmt_dtls_render() {
        return pmt_dtls_render;
    }

    /**
     * @param pmt_dtls_render the pmt_dtls_render to set
     */
    public void setPmt_dtls_render(boolean pmt_dtls_render) {
        this.pmt_dtls_render = pmt_dtls_render;
    }

    /**
     * @return the list_vh_pmttype
     */
    public List getList_vh_pmttype() {
        return list_vh_pmttype;
    }

    /**
     * @param list_vh_pmttype the list_vh_pmttype to set
     */
    public void setList_vh_pmttype(List list_vh_pmttype) {
        this.list_vh_pmttype = list_vh_pmttype;
    }

    /**
     * @return the list_vh_pmtcatg
     */
    public List getList_vh_pmtcatg() {
        return list_vh_pmtcatg;
    }

    /**
     * @param list_vh_pmtcatg the list_vh_pmtcatg to set
     */
    public void setList_vh_pmtcatg(List list_vh_pmtcatg) {
        this.list_vh_pmtcatg = list_vh_pmtcatg;
    }

    /**
     * @return the numberoftrips
     */
    public String getNumberoftrips() {
        return numberoftrips;
    }

    /**
     * @param numberoftrips the numberoftrips to set
     */
    public void setNumberoftrips(String numberoftrips) {
        this.numberoftrips = numberoftrips;
    }

    /**
     * @return the hp
     */
    public String getHp() {
        return hp;
    }

    /**
     * @param hp the hp to set
     */
    public void setHp(String hp) {
        this.hp = hp;
    }

    /**
     * @return the routeLen
     */
    public String getRouteLen() {
        return routeLen;
    }

    /**
     * @param routeLen the routeLen to set
     */
    public void setRouteLen(String routeLen) {
        this.routeLen = routeLen;
    }

    /**
     * @return the ser_type
     */
    public List getSer_type() {
        return ser_type;
    }

    /**
     * @param ser_type the ser_type to set
     */
    public void setSer_type(List ser_type) {
        this.ser_type = ser_type;
    }

    /**
     * @return the list_domain_cd
     */
    public List getList_domain_cd() {
        return list_domain_cd;
    }

    /**
     * @param list_domain_cd the list_domain_cd to set
     */
    public void setList_domain_cd(List list_domain_cd) {
        this.list_domain_cd = list_domain_cd;
    }

    /**
     * @return the showTaxBrkupPanel
     */
    public boolean isShowTaxBrkupPanel() {
        return showTaxBrkupPanel;
    }

    /**
     * @param showTaxBrkupPanel the showTaxBrkupPanel to set
     */
    public void setShowTaxBrkupPanel(boolean showTaxBrkupPanel) {
        this.showTaxBrkupPanel = showTaxBrkupPanel;
    }

    /**
     * @return the ispmt_type
     */
    public boolean isIspmt_type() {
        return ispmt_type;
    }

    /**
     * @param ispmt_type the ispmt_type to set
     */
    public void setIspmt_type(boolean ispmt_type) {
        this.ispmt_type = ispmt_type;
    }

    /**
     * @return the ispmt_catg
     */
    public boolean isIspmt_catg() {
        return ispmt_catg;
    }

    /**
     * @param ispmt_catg the ispmt_catg to set
     */
    public void setIspmt_catg(boolean ispmt_catg) {
        this.ispmt_catg = ispmt_catg;
    }

    /**
     * @return the isrouteLen
     */
    public boolean isIsrouteLen() {
        return isrouteLen;
    }

    /**
     * @param isrouteLen the isrouteLen to set
     */
    public void setIsrouteLen(boolean isrouteLen) {
        this.isrouteLen = isrouteLen;
    }

    /**
     * @return the isseat_cap
     */
    public boolean isIsseat_cap() {
        return isseat_cap;
    }

    /**
     * @param isseat_cap the isseat_cap to set
     */
    public void setIsseat_cap(boolean isseat_cap) {
        this.isseat_cap = isseat_cap;
    }

    /**
     * @return the isstand_cap
     */
    public boolean isIsstand_cap() {
        return isstand_cap;
    }

    /**
     * @param isstand_cap the isstand_cap to set
     */
    public void setIsstand_cap(boolean isstand_cap) {
        this.isstand_cap = isstand_cap;
    }

    /**
     * @return the issleep_cap
     */
    public boolean isIssleep_cap() {
        return issleep_cap;
    }

    /**
     * @param issleep_cap the issleep_cap to set
     */
    public void setIssleep_cap(boolean issleep_cap) {
        this.issleep_cap = issleep_cap;
    }

    /**
     * @return the isul_weight
     */
    public boolean isIsul_weight() {
        return isul_weight;
    }

    /**
     * @param isul_weight the isul_weight to set
     */
    public void setIsul_weight(boolean isul_weight) {
        this.isul_weight = isul_weight;
    }

    /**
     * @return the isl_weight
     */
    public boolean isIsl_weight() {
        return isl_weight;
    }

    /**
     * @param isl_weight the isl_weight to set
     */
    public void setIsl_weight(boolean isl_weight) {
        this.isl_weight = isl_weight;
    }

    /**
     * @return the isfuel
     */
    public boolean isIsfuel() {
        return isfuel;
    }

    /**
     * @param isfuel the isfuel to set
     */
    public void setIsfuel(boolean isfuel) {
        this.isfuel = isfuel;
    }

    /**
     * @return the iscubic_cap
     */
    public boolean isIscubic_cap() {
        return iscubic_cap;
    }

    /**
     * @param iscubic_cap the iscubic_cap to set
     */
    public void setIscubic_cap(boolean iscubic_cap) {
        this.iscubic_cap = iscubic_cap;
    }

    /**
     * @return the isf_area
     */
    public boolean isIsf_area() {
        return isf_area;
    }

    /**
     * @param isf_area the isf_area to set
     */
    public void setIsf_area(boolean isf_area) {
        this.isf_area = isf_area;
    }

    /**
     * @return the isac_fitted
     */
    public boolean isIsac_fitted() {
        return isac_fitted;
    }

    /**
     * @param isac_fitted the isac_fitted to set
     */
    public void setIsac_fitted(boolean isac_fitted) {
        this.isac_fitted = isac_fitted;
    }

    /**
     * @return the isowner_cd
     */
    public boolean isIsowner_cd() {
        return isowner_cd;
    }

    /**
     * @param isowner_cd the isowner_cd to set
     */
    public void setIsowner_cd(boolean isowner_cd) {
        this.isowner_cd = isowner_cd;
    }

    /**
     * @return the isother_criteria
     */
    public boolean isIsother_criteria() {
        return isother_criteria;
    }

    /**
     * @param isother_criteria the isother_criteria to set
     */
    public void setIsother_criteria(boolean isother_criteria) {
        this.isother_criteria = isother_criteria;
    }

    /**
     * @return the isimported_veh
     */
    public boolean isIsimported_veh() {
        return isimported_veh;
    }

    /**
     * @param isimported_veh the isimported_veh to set
     */
    public void setIsimported_veh(boolean isimported_veh) {
        this.isimported_veh = isimported_veh;
    }

    /**
     * @return the isvch_purchase_as
     */
    public boolean isIsvch_purchase_as() {
        return isvch_purchase_as;
    }

    /**
     * @param isvch_purchase_as the isvch_purchase_as to set
     */
    public void setIsvch_purchase_as(boolean isvch_purchase_as) {
        this.isvch_purchase_as = isvch_purchase_as;
    }

    /**
     * @return the issale_amt
     */
    public boolean isIssale_amt() {
        return issale_amt;
    }

    /**
     * @param issale_amt the issale_amt to set
     */
    public void setIssale_amt(boolean issale_amt) {
        this.issale_amt = issale_amt;
    }

    /**
     * @return the istax_mode
     */
    public boolean isIstax_mode() {
        return istax_mode;
    }

    /**
     * @param istax_mode the istax_mode to set
     */
    public void setIstax_mode(boolean istax_mode) {
        this.istax_mode = istax_mode;
    }

    /**
     * @return the ishp
     */
    public boolean isIshp() {
        return ishp;
    }

    /**
     * @param ishp the ishp to set
     */
    public void setIshp(boolean ishp) {
        this.ishp = ishp;
    }

    /**
     * @return the isservices_type
     */
    public boolean isIsservices_type() {
        return isservices_type;
    }

    /**
     * @param isservices_type the isservices_type to set
     */
    public void setIsservices_type(boolean isservices_type) {
        this.isservices_type = isservices_type;
    }

    /**
     * @return the isdomain_cd
     */
    public boolean isIsdomain_cd() {
        return isdomain_cd;
    }

    /**
     * @param isdomain_cd the isdomain_cd to set
     */
    public void setIsdomain_cd(boolean isdomain_cd) {
        this.isdomain_cd = isdomain_cd;
    }

    /**
     * @return the isregn_dt
     */
    public boolean isIsregn_dt() {
        return isregn_dt;
    }

    /**
     * @param isregn_dt the isregn_dt to set
     */
    public void setIsregn_dt(boolean isregn_dt) {
        this.isregn_dt = isregn_dt;
    }

    /**
     * @return the isnumberoftrips
     */
    public boolean isIsnumberoftrips() {
        return isnumberoftrips;
    }

    /**
     * @param isnumberoftrips the isnumberoftrips to set
     */
    public void setIsnumberoftrips(boolean isnumberoftrips) {
        this.isnumberoftrips = isnumberoftrips;
    }

    /**
     * @return the taxModList
     */
    public List<TaxFormPanelBean> getTaxModList() {
        return taxModList;
    }

    /**
     * @param taxModList the taxModList to set
     */
    public void setTaxModList(List<TaxFormPanelBean> taxModList) {
        this.taxModList = taxModList;
    }

    /**
     * @return the taxFieldDobj
     */
    public TaxFieldDobj getTaxFieldDobj() {
        return taxFieldDobj;
    }

    /**
     * @param taxFieldDobj the taxFieldDobj to set
     */
    public void setTaxFieldDobj(TaxFieldDobj taxFieldDobj) {
        this.taxFieldDobj = taxFieldDobj;
    }

    /**
     * @return the purchase_dt
     */
    public Date getPurchase_dt() {
        return purchase_dt;
    }

    /**
     * @param purchase_dt the purchase_dt to set
     */
    public void setPurchase_dt(Date purchase_dt) {
        this.purchase_dt = purchase_dt;
    }

    /**
     * @return the regn_dt
     */
    public Date getRegn_dt() {
        return regn_dt;
    }

    /**
     * @param regn_dt the regn_dt to set
     */
    public void setRegn_dt(Date regn_dt) {
        this.regn_dt = regn_dt;
    }

    /**
     * @return the regnType
     */
    public String getRegnType() {
        return regnType;
    }

    /**
     * @param regnType the regnType to set
     */
    public void setRegnType(String regnType) {
        this.regnType = regnType;
    }

    /**
     * @return the listRegnType
     */
    public List getListRegnType() {
        return listRegnType;
    }

    /**
     * @param listRegnType the listRegnType to set
     */
    public void setListRegnType(List listRegnType) {
        this.listRegnType = listRegnType;
    }

    /**
     * @return the isRegnType
     */
    public boolean isIsRegnType() {
        return isRegnType;
    }

    /**
     * @param isRegnType the isRegnType to set
     */
    public void setIsRegnType(boolean isRegnType) {
        this.isRegnType = isRegnType;
    }

    /**
     * @return the vh_class
     */
    public String getVh_class() {
        return vh_class;
    }

    /**
     * @param vh_class the vh_class to set
     */
    public void setVh_class(String vh_class) {
        this.vh_class = vh_class;
    }

    /**
     * @return the pmt_type
     */
    public String getPmt_type() {
        return pmt_type;
    }

    /**
     * @param pmt_type the pmt_type to set
     */
    public void setPmt_type(String pmt_type) {
        this.pmt_type = pmt_type;
    }

    /**
     * @return the pmt_catg
     */
    public String getPmt_catg() {
        return pmt_catg;
    }

    /**
     * @param pmt_catg the pmt_catg to set
     */
    public void setPmt_catg(String pmt_catg) {
        this.pmt_catg = pmt_catg;
    }

    /**
     * @return the vch_catg
     */
    public String getVch_catg() {
        return vch_catg;
    }

    /**
     * @param vch_catg the vch_catg to set
     */
    public void setVch_catg(String vch_catg) {
        this.vch_catg = vch_catg;
    }

    /**
     * @return the fuel
     */
    public String getFuel() {
        return fuel;
    }

    /**
     * @param fuel the fuel to set
     */
    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    /**
     * @return the ac_fitted
     */
    public String getAc_fitted() {
        return ac_fitted;
    }

    /**
     * @param ac_fitted the ac_fitted to set
     */
    public void setAc_fitted(String ac_fitted) {
        this.ac_fitted = ac_fitted;
    }

    /**
     * @return the owner_cd
     */
    public String getOwner_cd() {
        return owner_cd;
    }

    /**
     * @param owner_cd the owner_cd to set
     */
    public void setOwner_cd(String owner_cd) {
        this.owner_cd = owner_cd;
    }

    /**
     * @return the other_criteria
     */
    public String getOther_criteria() {
        return other_criteria;
    }

    /**
     * @param other_criteria the other_criteria to set
     */
    public void setOther_criteria(String other_criteria) {
        this.other_criteria = other_criteria;
    }

    /**
     * @return the state_code
     */
    public String getState_code() {
        return state_code;
    }

    /**
     * @param state_code the state_code to set
     */
    public void setState_code(String state_code) {
        this.state_code = state_code;
    }

    /**
     * @return the services_type
     */
    public String getServices_type() {
        return services_type;
    }

    /**
     * @param services_type the services_type to set
     */
    public void setServices_type(String services_type) {
        this.services_type = services_type;
    }

    /**
     * @return the domain_cd
     */
    public String getDomain_cd() {
        return domain_cd;
    }

    /**
     * @param domain_cd the domain_cd to set
     */
    public void setDomain_cd(String domain_cd) {
        this.domain_cd = domain_cd;
    }

    /**
     * @return the vhClassDisable
     */
    public boolean isVhClassDisable() {
        return vhClassDisable;
    }

    /**
     * @param vhClassDisable the vhClassDisable to set
     */
    public void setVhClassDisable(boolean vhClassDisable) {
        this.vhClassDisable = vhClassDisable;
    }

    /**
     * @return the vhCatgDisable
     */
    public boolean isVhCatgDisable() {
        return vhCatgDisable;
    }

    /**
     * @param vhCatgDisable the vhCatgDisable to set
     */
    public void setVhCatgDisable(boolean vhCatgDisable) {
        this.vhCatgDisable = vhCatgDisable;
    }

    /**
     * @return the owner_dobj
     */
    public Owner_dobj getOwner_dobj() {
        return owner_dobj;
    }

    /**
     * @param owner_dobj the owner_dobj to set
     */
    public void setOwner_dobj(Owner_dobj owner_dobj) {
        this.owner_dobj = owner_dobj;
    }

    /**
     * @return the taxParameters
     */
    public VahanTaxParameters getTaxParameters() {
        return taxParameters;
    }

    /**
     * @param taxParameters the taxParameters to set
     */
    public void setTaxParameters(VahanTaxParameters taxParameters) {
        this.taxParameters = taxParameters;
    }

    /**
     * @return the taxmodeDisable
     */
    public boolean isTaxmodeDisable() {
        return taxmodeDisable;
    }

    /**
     * @param taxmodeDisable the taxmodeDisable to set
     */
    public void setTaxmodeDisable(boolean taxmodeDisable) {
        this.taxmodeDisable = taxmodeDisable;
    }
}
