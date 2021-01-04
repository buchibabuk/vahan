/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.form.dobj.TaxSlabAddNewDobj;
import nic.vahan.form.dobj.TaxSlabDobj;
import nic.vahan.form.impl.TaxSlab_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.inputtextarea.InputTextarea;
import org.primefaces.component.selectmanymenu.SelectManyMenu;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.PrimeFaces;

@ManagedBean
@ViewScoped
public class TaxSlab_bean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(TaxSlab_bean.class);
    private Date date_from = null;
    private Date date_to = null;
    private InputText descr = new InputText();
    private InputText slab_code = new InputText();
    private InputTextarea condition_fromula = new InputTextarea();
    private InputTextarea rate_fromula = new InputTextarea();
    private SelectOneMenu type_flag = new SelectOneMenu();
    private SelectOneMenu tax_mode = new SelectOneMenu();
    private SelectManyMenu tax_on_vch = new SelectManyMenu();
    private SelectOneMenu class_type = new SelectOneMenu();
    private SelectOneMenu pur_cd = new SelectOneMenu();
    private List<TaxSlabDobj> taxslabnewlist = null;
    private List<TaxSlabDobj> taxSlabNewListFilter = null;
    private List<TaxSlabAddNewDobj> taxslabaddnewlist = null;
    private Map<String, Object> taxModeLabelValue;
    private Map<String, Object> purposeLabelValue;
    private Map<String, Object> taxOnVehicleLabelValue;
    private List<SelectItem> classtypelist;
    private String header;
    private String tax_slab_header;
    private TaxSlabDobj selectedDetails;
    private boolean taxSlabRendered;
    private String task;
    private boolean isNew;
    private String class_desc;
    private String pur_desc;
    private String makeIfCondition;
    private int add_code;
    private List<Entry<String, String>> tagFieldsList;
    private boolean selectAllCheckBox;

    @PostConstruct
    public void init() {
        taxslabnewlist = null;
        taxModeLabelValue = ServerUtil.getTaxModeList();
        purposeLabelValue = TaxSlab_Impl.getPurposeList();
        taxOnVehicleLabelValue = null;
        taxslabaddnewlist = null;
        header = "NO SELECTION";
        tax_slab_header = "";

        ///////////////Putting the values for Class Type////
        classtypelist = new ArrayList<SelectItem>();
        classtypelist.add(new SelectItem(1, "Transport"));
        classtypelist.add(new SelectItem(2, "Non-Transport"));
        ////////////////////////////////////////////////////
        tagFieldsList = new ArrayList(FormulaUtils.TagDescrDisplay.entrySet());

    }

    public void onRowSelectToggler(TaxSlabDobj dobj) {

        String descr = dobj.getDescr();
        int slab_cd = dobj.getSlab_code();
        header = "Additional Slabs for Slab Code: " + slab_cd + " (" + descr + ")";
        taxslabaddnewlist = TaxSlab_Impl.getTaxSlabAddNewDetail(slab_cd);
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.setAttribute("selected_slab_cd", slab_cd);
    }

    public void onSelectAddNewRecord(ActionEvent e) {
        int cls_type = Integer.parseInt(getClass_type().getValue().toString());
        taxOnVehicleLabelValue = ServerUtil.vehicleClassList(cls_type);
        isNew = true;
        resetComponent();
    }

    public void onSelectGetDetails() {
        int class_typ = Integer.parseInt(getClass_type().getValue().toString());
        int pur_code = Integer.parseInt(getPur_cd().getValue().toString());


        for (int i = 0; i < classtypelist.size(); i++) {
            if (classtypelist.get(i).getValue().toString().equals(class_type.getValue().toString())) {
                setClass_desc(classtypelist.get(i).getLabel());
            }
        }

        for (Object key : purposeLabelValue.keySet()) {
            if (Integer.parseInt(purposeLabelValue.get(key).toString()) == Integer.parseInt(pur_cd.getValue().toString())) {
                pur_desc = key.toString();
                break;
            }
        }

        tax_slab_header = "Available Slabs For " + getPur_desc() + " (" + getClass_desc() + ")";
        taxslabaddnewlist = null;
        taxslabnewlist = TaxSlab_Impl.getTaxSlabNewDetail(class_typ, pur_code);
        taxSlabRendered = true;
    }

    public void saveTaxSlabNewRecord(boolean isNew) {

        TaxSlabDobj slab_dobj = new TaxSlabDobj();
        slab_dobj.setEmp_cd(Util.getEmpCode());
        slab_dobj.setClass_type(Integer.parseInt(this.class_type.getValue().toString()));
        slab_dobj.setTax_mode(this.tax_mode.getValue().toString());
        slab_dobj.setPur_cd(Integer.parseInt(this.pur_cd.getValue().toString()));
        slab_dobj.setState_cd(Util.getUserStateCode());
        slab_dobj.setDescr(this.descr.getValue().toString().trim().toUpperCase());
        slab_dobj.setDate_from(this.date_from);
        slab_dobj.setDate_to(this.date_to);


        if (isNew) { // this will be set when new slab will be added
            slab_dobj.setNewSlab(isNew);//true
            String tax_vch = ",";
            for (int i = 0; i < this.tax_on_vch.getSelectedValues().length; i++) {
                tax_vch = tax_vch + tax_on_vch.getSelectedValues()[i] + ",";
            }
            slab_dobj.setTax_on_vch(tax_vch.trim());
        } else {
            slab_dobj.setNewSlab(isNew);//false
            slab_dobj.setSlab_code(getSelectedDetails().getSlab_code());//selected slab code from data_table        

            //this is temporary need to be updated start
            String tax_vch = ",";
            for (int i = 0; i < this.tax_on_vch.getSelectedValues().length; i++) {
                tax_vch = tax_vch + tax_on_vch.getSelectedValues()[i] + ",";
            }
            slab_dobj.setTax_on_vch(tax_vch.trim());
            //this is temporary need to be updated end
        }

        try {
            TaxSlab_Impl.insertUpdateTaxSlabNewTable(slab_dobj);

            // this is temporary need to find optimum answer for updating the datatable start
            taxslabnewlist = TaxSlab_Impl.getTaxSlabNewDetail(slab_dobj.getClass_type(), slab_dobj.getPur_cd());
            // this is temporary need to find optimum answer end

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

    }

    public void viewTaxSlabAddNew(ActionEvent event) {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("modal", true);
        options.put("closable", false);
        options.put("draggable", true);
        options.put("resizable", true);
        options.put("contentHeight", 600);
        options.put("contentWidth", 800);

        String rate = (String) event.getComponent().getAttributes().get("rate");
        String condition = (String) event.getComponent().getAttributes().get("condition");
        String type_flg = (String) event.getComponent().getAttributes().get("type_flag");
        int add_code = (int) event.getComponent().getAttributes().get("add_code");
        int slab_cd = (int) event.getComponent().getAttributes().get("slab_code");
        String strMakeIfCondition = FormulaUtils.makeIfCondition(type_flg, condition, rate);
        String slab_descr = (String) event.getComponent().getAttributes().get("slab_descr");

        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.setAttribute("rate", rate);
        session.setAttribute("condition", condition);
        session.setAttribute("type_flag", type_flg);
        session.setAttribute("add_code", add_code);
        session.setAttribute("slab_code", slab_cd);
        session.setAttribute("strMakeIfCondition", strMakeIfCondition);
        session.setAttribute("slab_descr", slab_descr);
        //we can set parameter to the view so we can also remove the use of session to fill the form
        HashMap<String, List<String>> req_map = new HashMap();
        ArrayList list = new ArrayList();
        list.add("update");
        req_map.put("task", list);
        PrimeFaces.current().dialog().openDynamic("form_tax_slab_add_new", options, req_map);
    }

    public void viewEmptyTaxSlabAddNewForm(ActionEvent event) {

        Map<String, Object> options = new HashMap<String, Object>();
        options.put("modal", true);
        options.put("closable", false);
        options.put("draggable", true);
        options.put("resizable", true);
        options.put("contentHeight", 600);
        options.put("contentWidth", 800);

        int slab_cd = (int) event.getComponent().getAttributes().get("slab_code");
        String slab_descr = (String) event.getComponent().getAttributes().get("slab_descr");

        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.setAttribute("slab_code", slab_cd);
        session.setAttribute("slab_descr", slab_descr);

        HashMap<String, List<String>> req_map = new HashMap();
        ArrayList list = new ArrayList();
        list.add("addNew");
        req_map.put("task", list);
        PrimeFaces.current().dialog().openDynamic("form_tax_slab_add_new", options, req_map);
    }

    public void flahFormLoad(String task) {

        this.task = task;

        if (task.equals("update")) {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

            rate_fromula.setValue(session.getAttribute("rate").toString());
            condition_fromula.setValue(session.getAttribute("condition").toString());
            type_flag.setValue(session.getAttribute("type_flag").toString());
            makeIfCondition = session.getAttribute("strMakeIfCondition").toString();
            this.add_code = Integer.parseInt(session.getAttribute("add_code").toString());
            this.slab_code.setValue(session.getAttribute("slab_code").toString());

        }
    }

    public void saveTaxSlabAddNewRecord() {

        Exception e = null;
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        TaxSlabAddNewDobj dobj = new TaxSlabAddNewDobj();
        try {

            //for update
            dobj.setCondition_formula(getCondition_fromula().getValue().toString().trim().toUpperCase());
            dobj.setRate_formula(getRate_fromula().getValue().toString().trim().toUpperCase());
            dobj.setType_flag(getType_flag().getValue().toString().trim().toUpperCase());
            dobj.setEmp_cd(Util.getEmpCode());
            dobj.setState_cd(Util.getUserStateCode());

            if (task.equals("update")) {
                dobj.setAdd_code(Integer.parseInt(session.getAttribute("add_code").toString()));
                dobj.setSlab_code(Integer.parseInt(session.getAttribute("slab_code").toString()));
                dobj.setNewAddSlab(false);
            } else {
                dobj.setSlab_code(Integer.parseInt(session.getAttribute("selected_slab_cd").toString()));
                dobj.setNewAddSlab(true);
            }

            TaxSlab_Impl.insertUpdateTaxSlabAddNewTable(dobj);

        } catch (Exception ee) {
            e = ee;
        }

        if (e != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error!", "Data can Not be saved."));
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } else {
            PrimeFaces.current().dialog().closeDynamic(null); //for closing the dialog
        }

    }

    public void closeDialog() {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void onSaveAddTaxSlabNew(int slab_code) {
        //this is used for updating the datatable need to be optimized later this is only temporary start
        taxslabaddnewlist = TaxSlab_Impl.getTaxSlabAddNewDetail(slab_code);
        //this is used for updating the datatable need to be optimized later this is only temporary end 
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Data Successfully Saved."));
    }

    public void resetComponent() {
        this.descr.resetValue();
        this.date_from = null;
        this.date_to = null;
        this.tax_mode.resetValue();
        this.tax_on_vch.resetValue();
    }

    public void onClickTaxSlabNewUpdate(TaxSlabDobj dobj) {

        ////////////////////////////////////////////////////////////////
        int cls_type = Integer.parseInt(getClass_type().getValue().toString());
        taxOnVehicleLabelValue = ServerUtil.vehicleClassList(cls_type);
        //////////////////////////////////////////////////////////////

        isNew = false;

        setSelectedDetails(dobj);

        String str = getSelectedDetails().getTax_on_vch();
        StringTokenizer token = new StringTokenizer(str, ",");


        ArrayList<Integer> old_selected_tax_on_vch = new ArrayList();
        while (token.hasMoreTokens()) {
            old_selected_tax_on_vch.add(Integer.parseInt(token.nextToken()));
        }
        this.descr.setValue(getSelectedDetails().getDescr().trim().toUpperCase());
        this.tax_mode.setValue(getSelectedDetails().getTax_mode());
        this.date_from = getSelectedDetails().getDate_from();
        this.date_to = getSelectedDetails().getDate_to();
        this.tax_on_vch.setValue(old_selected_tax_on_vch);
    }

//    public void formulaeValidator(FacesContext context, UIComponent componentToValidate, Object value) throws ValidatorException {
    public void formulaeValidator(ComponentSystemEvent event) {
        FacesContext fc = FacesContext.getCurrentInstance();

        UIComponent components = event.getComponent();

        //for condition formula
        UIInput uiConditionFormula = (UIInput) components.findComponent("condition_formula");
        String conditionFormula = uiConditionFormula.getValue() == null ? "" : uiConditionFormula.getLocalValue().toString();
        String conditionFormulaId = uiConditionFormula.getClientId();
        //for rate formula
        UIInput uiRateFormula = (UIInput) components.findComponent("rate_formula");
        String rateFormula = uiRateFormula.getValue() == null ? "" : uiRateFormula.getLocalValue().toString();
        String rateFormulaId = uiRateFormula.getClientId();


        UIInput uiType_flag = (UIInput) components.findComponent("type_flag");
        String type_flg = uiType_flag.getLocalValue().toString();


        setMakeIfCondition(FormulaUtils.makeIfCondition(type_flg, conditionFormula, rateFormula));

        if (!FormulaUtils.isVerifyFormula(FormulaUtils.replaceTagValues(conditionFormula))) {
            FacesMessage msg = new FacesMessage("Invalid Condition Formula.");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fc.addMessage(conditionFormulaId, msg);
            fc.renderResponse();
        }

        try {
            FormulaUtils.getFormulaValue(FormulaUtils.replaceTagValues(rateFormula));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesMessage msg = new FacesMessage("Invalid Rate Formula.");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fc.addMessage(rateFormulaId, msg);
            fc.renderResponse();
        }

    }

    /**
     * @return the tax_slab_new_list
     */
    public List<TaxSlabDobj> getTaxslabnewlist() {
        return taxslabnewlist;
    }

    /**
     * @param tax_slab_new_list the tax_slab_new_list to set
     */
    public void setTaxslabnewlist(List<TaxSlabDobj> taxslabnewlist) {
        this.taxslabnewlist = taxslabnewlist;
    }

    /**
     * @return the taxModeLabelValue
     */
    public Map<String, Object> getTaxModeLabelValue() {
        return taxModeLabelValue;
    }

    /**
     * @param taxModeLabelValue the taxModeLabelValue to set
     */
    public void setTaxModeLabelValue(Map<String, Object> taxModeLabelValue) {
        this.taxModeLabelValue = taxModeLabelValue;
    }

    /**
     * @return the purposeLabelValue
     */
    public Map<String, Object> getPurposeLabelValue() {
        return purposeLabelValue;
    }

    /**
     * @param purposeLabelValue the purposeLabelValue to set
     */
    public void setPurposeLabelValue(Map<String, Object> purposeLabelValue) {
        this.purposeLabelValue = purposeLabelValue;
    }

    /**
     * @return the taxOnVehicleLabelValue
     */
    public Map<String, Object> getTaxOnVehicleLabelValue() {
        return taxOnVehicleLabelValue;
    }

    /**
     * @param taxOnVehicleLabelValue the taxOnVehicleLabelValue to set
     */
    public void setTaxOnVehicleLabelValue(Map<String, Object> taxOnVehicleLabelValue) {
        this.taxOnVehicleLabelValue = taxOnVehicleLabelValue;
    }

    /**
     * @return the class_type
     */
    public SelectOneMenu getClass_type() {
        return class_type;
    }

    /**
     * @param class_type the class_type to set
     */
    public void setClass_type(SelectOneMenu class_type) {
        this.class_type = class_type;
    }

    /**
     * @return the tax_slab_add_new_list
     */
    public List<TaxSlabAddNewDobj> getTaxslabaddnewlist() {
        return taxslabaddnewlist;
    }

    /**
     * @param tax_slab_add_new_list the tax_slab_add_new_list to set
     */
    public void setTaxslabaddnewlist(List<TaxSlabAddNewDobj> taxslabaddnewlist) {
        this.taxslabaddnewlist = taxslabaddnewlist;
    }

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @return the tax_slab_header
     */
    public String getTax_slab_header() {
        return tax_slab_header;
    }

    /**
     * @param tax_slab_header the tax_slab_header to set
     */
    public void setTax_slab_header(String tax_slab_header) {
        this.tax_slab_header = tax_slab_header;
    }

    /**
     * @return the pur_cd
     */
    public SelectOneMenu getPur_cd() {
        return pur_cd;
    }

    /**
     * @param pur_cd the pur_cd to set
     */
    public void setPur_cd(SelectOneMenu pur_cd) {
        this.pur_cd = pur_cd;
    }

    /**
     * @return the tax_on_vch
     */
    public SelectManyMenu getTax_on_vch() {
        return tax_on_vch;
    }

    /**
     * @param tax_on_vch the tax_on_vch to set
     */
    public void setTax_on_vch(SelectManyMenu tax_on_vch) {
        this.tax_on_vch = tax_on_vch;
    }

    /**
     * @return the tax_mode
     */
    public SelectOneMenu getTax_mode() {
        return tax_mode;
    }

    /**
     * @param tax_mode the tax_mode to set
     */
    public void setTax_mode(SelectOneMenu tax_mode) {
        this.tax_mode = tax_mode;
    }

    /**
     * @return the descr
     */
    public InputText getDescr() {
        return descr;
    }

    /**
     * @param descr the descr to set
     */
    public void setDescr(InputText descr) {
        this.descr = descr;
    }

    /**
     * @return the date_from
     */
    public Date getDate_from() {
        return date_from;
    }

    /**
     * @param date_from the date_from to set
     */
    public void setDate_from(Date date_from) {
        this.date_from = date_from;
    }

    /**
     * @return the date_to
     */
    public Date getDate_to() {
        return date_to;
    }

    /**
     * @param date_to the date_to to set
     */
    public void setDate_to(Date date_to) {
        this.date_to = date_to;
    }

    /**
     * @return the slab_code
     */
    public InputText getSlab_code() {
        return slab_code;
    }

    /**
     * @param slab_code the slab_code to set
     */
    public void setSlab_code(InputText slab_code) {
        this.slab_code = slab_code;
    }

    /**
     * @return the condition_fromula
     */
    public InputTextarea getCondition_fromula() {
        return condition_fromula;
    }

    /**
     * @param condition_fromula the condition_fromula to set
     */
    public void setCondition_fromula(InputTextarea condition_fromula) {
        this.condition_fromula = condition_fromula;
    }

    /**
     * @return the rate_fromula
     */
    public InputTextarea getRate_fromula() {
        return rate_fromula;
    }

    /**
     * @param rate_fromula the rate_fromula to set
     */
    public void setRate_fromula(InputTextarea rate_fromula) {
        this.rate_fromula = rate_fromula;
    }

    /**
     * @return the type_flag
     */
    public SelectOneMenu getType_flag() {
        return type_flag;
    }

    /**
     * @param type_flag the type_flag to set
     */
    public void setType_flag(SelectOneMenu type_flag) {
        this.type_flag = type_flag;
    }

    /**
     * @return the taxSlabRendered
     */
    public boolean isTaxSlabRendered() {
        return taxSlabRendered;
    }

    /**
     * @param taxSlabRendered the taxSlabRendered to set
     */
    public void setTaxSlabRendered(boolean taxSlabRendered) {
        this.taxSlabRendered = taxSlabRendered;
    }

    /**
     * @return the task
     */
    public String getTask() {
        return task;
    }

    /**
     * @param task the task to set
     */
    public void setTask(String task) {
        this.task = task;
    }

    /**
     * @return the selectedDetails
     */
    public TaxSlabDobj getSelectedDetails() {
        return selectedDetails;
    }

    /**
     * @param selectedDetails the selectedDetails to set
     */
    public void setSelectedDetails(TaxSlabDobj selectedDetails) {
        this.selectedDetails = selectedDetails;
    }

    /**
     * @return the isNew
     */
    public boolean isIsNew() {
        return isNew;
    }

    /**
     * @param isNew the isNew to set
     */
    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    /**
     * @return the class_desc
     */
    public String getClass_desc() {
        return class_desc;
    }

    /**
     * @param class_desc the class_desc to set
     */
    public void setClass_desc(String class_desc) {
        this.class_desc = class_desc;
    }

    /**
     * @return the pur_desc
     */
    public String getPur_desc() {
        return pur_desc;
    }

    /**
     * @param pur_desc the pur_desc to set
     */
    public void setPur_desc(String pur_desc) {
        this.pur_desc = pur_desc;
    }

    /**
     * @return the class_type_list
     */
    public List<SelectItem> getClasstypelist() {
        return classtypelist;
    }

    /**
     * @param class_type_list the class_type_list to set
     */
    public void setClasstypelist(List<SelectItem> classtypelist) {
        this.classtypelist = classtypelist;
    }

    /**
     * @return the makeIfCondition
     */
    public String getMakeIfCondition() {
        return makeIfCondition;
    }

    /**
     * @param makeIfCondition the makeIfCondition to set
     */
    public void setMakeIfCondition(String makeIfCondition) {
        this.makeIfCondition = makeIfCondition;
    }

    /**
     * @return the tagFieldsList
     */
    public List<Entry<String, String>> getTagFieldsList() {
        return tagFieldsList;
    }

    /**
     * @param tagFieldsList the tagFieldsList to set
     */
    public void setTagFieldsList(List<Entry<String, String>> tagFieldsList) {
        this.tagFieldsList = tagFieldsList;
    }

    /**
     * @return the add_code
     */
    public int getAdd_code() {
        return add_code;
    }

    /**
     * @param add_code the add_code to set
     */
    public void setAdd_code(int add_code) {
        this.add_code = add_code;
    }

    /**
     * @return the selectAllCheckBox
     */
    public boolean isSelectAllCheckBox() {
        return selectAllCheckBox;
    }

    /**
     * @param selectAllCheckBox the selectAllCheckBox to set
     */
    public void setSelectAllCheckBox(boolean selectAllCheckBox) {
        this.selectAllCheckBox = false;
    }

    /**
     * @return the taxSlabNewListFilter
     */
    public List<TaxSlabDobj> getTaxSlabNewListFilter() {
        return taxSlabNewListFilter;
    }

    /**
     * @param taxSlabNewListFilter the taxSlabNewListFilter to set
     */
    public void setTaxSlabNewListFilter(List<TaxSlabDobj> taxSlabNewListFilter) {
        this.taxSlabNewListFilter = taxSlabNewListFilter;
    }
}
