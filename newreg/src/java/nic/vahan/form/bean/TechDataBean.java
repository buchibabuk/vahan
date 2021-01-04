/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.TechDataDobj;
import nic.vahan.form.impl.TechDataImpl;
import org.apache.log4j.Logger;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.permit.PermitCheckDetailsImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.primefaces.PrimeFaces;

/**
 *
 * @author NIC
 */
@ManagedBean(name = "techdataBean")
@ViewScoped
public class TechDataBean extends AbstractApplBean implements ApproveDisapproveInterface, Serializable {

    private static final Logger LOGGER = Logger.getLogger(TechDataBean.class);
    private List<TechDataDobj> techDataList = new ArrayList<>();
    private TechDataDobj techDobj = new TechDataDobj();
    private TechDataDobj techDobj_prev = null;
    TechDataImpl impl = new TechDataImpl();
    private String regn_no;
    private List list_maker;
    private List list_fuel;
    private List list_maker_model;
    private boolean renderModelEditable = false;
    private boolean modelEditable = false;
    private boolean renderModelSelectMenu = true;
    private List list_ac_audio_video_fitted;
    private boolean tech_detailPanel;
    private boolean tech_dataListPanel = true;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<ComparisonBean> prevChangedDataList;
    private String appl_no;
    private String registrationNo = "";
    private boolean enable_disable = false;
    private String state_cd;
    private int offCd;
    private SessionVariables sessionVariables;
    private String vahanMessages;
    private List list_norms;

    public TechDataBean() {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
            vahanMessages = "Session time Out";
            return;
        }
        state_cd = sessionVariables.getStateCodeSelected();
        offCd = sessionVariables.getOffCodeSelected();
        list_maker = new ArrayList();
        list_fuel = new ArrayList();
        list_maker_model = new ArrayList();
        list_ac_audio_video_fitted = new ArrayList();
        techDobj = new TechDataDobj();
        list_norms = new ArrayList();
        init();
    }

    public void init() {

        try {
            String[][] data;
            data = MasterTableFiller.masterTables.VM_MAKER.getData();
            for (int i = 0; i < data.length; i++) {
                list_maker.add(new SelectItem(data[i][0], data[i][1]));
            }
            data = MasterTableFiller.masterTables.VM_FUEL.getData();
            for (int i = 0; i < data.length; i++) {
                list_fuel.add(new SelectItem(data[i][0], data[i][1]));
            }
            data = MasterTableFiller.masterTables.VM_NORMS.getData();
            for (int i = 0; i < data.length; i++) {
                list_norms.add(new SelectItem(data[i][0], data[i][1]));
            }
            list_ac_audio_video_fitted.add(new SelectItem("N", "NO"));
            list_ac_audio_video_fitted.add(new SelectItem("Y", "YES"));

            techDataList = impl.getDataVerifyRequest(state_cd, offCd);
            if (appl_details.getCurrent_action_cd() == TableConstants.TECHNICAL_DATA_VERIFICATION_APPROVAL) {
                tech_dataListPanel = false;
                techDobj = impl.getVehicleDataFromVaTable(appl_details.getAppl_no());
                techDobj_prev = (TechDataDobj) techDobj.clone();//for holding current dobj for using in the comparison.
                if (list_maker_model.isEmpty()) {
                    this.setModelEditable(true);
                    this.setRenderModelSelectMenu(false);
                }
                if (state_cd != null && "WB".equals(state_cd) && techDobj.isPmtPanelRendered() && "P".equals(techDobj.getTransportCatg())) {
                    fillListPmtType();
                    fillListPmtCatg();
                }
                tech_detailPanel = true;
                regn_no = techDobj.getRegn_no();
                enable_disable = true;
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
            }
        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Could not Processed due to Technical Error in Database.", "Could not Processed due to Technical Error in Database."));
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void getRegnNoFromList(TechDataDobj dobj) throws Exception {
        try {
            tech_detailPanel = false;
            regn_no = dobj.getRegn_no();
            vehicleDetails();
            enable_disable = true;
        } catch (VahanException ve) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
    }

    public void vehicleDetails() throws Exception {
        if (regn_no == null || regn_no == "") {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please enter Registration Number.", "Please enter Registration Number."));
            return;
        }
        try {
            boolean check = impl.checkHistory(regn_no, sessionVariables);
            if (!check) {
                techDobj = impl.getVehicleData(regn_no, sessionVariables);
                if (techDobj != null) {
                    techDobj_prev = (TechDataDobj) techDobj.clone();//for holding current dobj for using in the comparison.
                    setRegn_no(techDobj.getRegn_no());
                    if (list_maker_model.isEmpty()) {
                        this.setModelEditable(true);
                        this.setRenderModelSelectMenu(false);
                    }
                    tech_detailPanel = true;
                    if (state_cd != null && "WB".equals(state_cd) && techDobj.isPmtPanelRendered()) {
                        String[] permitDetails = new PermitCheckDetailsImpl().getPermitDetailsOnTaxBasedOn(regn_no, state_cd, offCd, null);
                        if (permitDetails[0] != null && !permitDetails[0].equals("")) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Permit details already updated.", "Permit details already updated."));
                            tech_detailPanel = false;
                            regn_no = "";
                            techDobj = null;
                            return;
                        }
                        fillListPmtType();
                        fillListPmtCatg();
                    }
                } else {
                    tech_detailPanel = false;
                    regn_no = "";
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Vehicle Not Found.", "Vehicle Not Found."));
                }
            } else {
                tech_detailPanel = false;
                regn_no = "";
                throw new VahanException("Technical data is already Verified Or application is Inverded for this number.");
            }
        } catch (VahanException ve) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
    }

    public void vehMakerListener(AjaxBehaviorEvent event) {
        String vehMaker = String.valueOf(this.techDobj.getMaker());
        String[][] dataMap = null;


        dataMap = MasterTableFiller.masterTables.VM_MODELS.getData();

        list_maker_model.clear();
        for (int i = 0; i < dataMap.length; i++) {
            if (dataMap[i][0].equals(vehMaker)) {
                list_maker_model.add(new SelectItem(dataMap[i][1], dataMap[i][2]));
            }
        }

        renderModelSelectMenu = true;

    }

    public void renderSelectMenuModelListener(AjaxBehaviorEvent ajax) {
        if (modelEditable) {
            renderModelSelectMenu = false;
        } else {
            renderModelSelectMenu = true;
        }
    }

    public void validateLadenWeight(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        int ulWeight = this.getTechDobj().getUnld_wt();
        if (ulWeight > Integer.parseInt(value.toString().trim())) {
            FacesMessage msg = new FacesMessage("Un-Laden Weight Must Less Than Laden Weight");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }

    public boolean isTech_detailPanel() {
        return tech_detailPanel;
    }

    public void setTech_detailPanel(boolean tech_detailPanel) {
        this.tech_detailPanel = tech_detailPanel;
    }

    public List<TechDataDobj> getTechDataList() {
        return techDataList;
    }

    public void setTechDataList(List<TechDataDobj> techDataList) {
        this.techDataList = techDataList;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public List getList_maker() {
        return list_maker;
    }

    public void setList_maker(List list_maker) {
        this.list_maker = list_maker;
    }

    public List getList_fuel() {
        return list_fuel;
    }

    public void setList_fuel(List list_fuel) {
        this.list_fuel = list_fuel;
    }

    public TechDataDobj getTechDobj() {
        return techDobj;
    }

    public void setTechDobj(TechDataDobj techDobj) {
        this.techDobj = techDobj;
    }

    public List getList_maker_model() {
        return list_maker_model;
    }

    public void setList_maker_model(List list_maker_model) {
        this.list_maker_model = list_maker_model;
    }

    public boolean isRenderModelEditable() {
        return renderModelEditable;
    }

    public void setRenderModelEditable(boolean renderModelEditable) {
        this.renderModelEditable = renderModelEditable;
    }

    public boolean isModelEditable() {
        return modelEditable;
    }

    public void setModelEditable(boolean modelEditable) {
        this.modelEditable = modelEditable;
    }

    public boolean isRenderModelSelectMenu() {
        return renderModelSelectMenu;
    }

    public void setRenderModelSelectMenu(boolean renderModelSelectMenu) {
        this.renderModelSelectMenu = renderModelSelectMenu;
    }

    public List getList_ac_audio_video_fitted() {
        return list_ac_audio_video_fitted;
    }

    public void setList_ac_audio_video_fitted(List list_ac_audio_video_fitted) {
        this.list_ac_audio_video_fitted = list_ac_audio_video_fitted;
    }

    public void saveTechEntry() {

        Exception e = null;
        try {
            Status_dobj status_dobj = new Status_dobj();
            status_dobj.setRegn_no(techDobj.getRegn_no());
            status_dobj.setPur_cd(TableConstants.TECHNICAL_DATA_VERIFICATION);
            status_dobj.setFlow_slno(1);//initial flow serial no.
            status_dobj.setFile_movement_slno(1);//initial file movement serial no.
            status_dobj.setState_cd(state_cd);
            status_dobj.setOff_cd(getAppl_details().getCurrent_off_cd());
            status_dobj.setEmp_cd(0);
            status_dobj.setSeat_cd("");
            status_dobj.setCntr_id("");
            status_dobj.setStatus("N");
            status_dobj.setOffice_remark("");
            status_dobj.setPublic_remark("");
            status_dobj.setFile_movement_type("F");
            status_dobj.setUser_id(sessionVariables.getUserIdForLoggedInUser());
            status_dobj.setAppl_date(ServerUtil.getSystemDateInPostgres());
            status_dobj.setUser_type("");
            status_dobj.setEntry_ip("");
            status_dobj.setEntry_status("");//for New File
            status_dobj.setConfirm_ip("");
            status_dobj.setConfirm_status("");
            status_dobj.setConfirm_date(new java.util.Date());

            List<ComparisonBean> compareChanges = compareChanges();
            if (!compareChanges.isEmpty() || techDobj_prev == null) { //save only when data is really changed by user
                appl_no = impl.insertIntoVaTechDataVerify(status_dobj, techDobj, compareChanges, sessionVariables);
                techDobj.setAppl_no(appl_no);
            }

        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Something went wrong, Please try again."));
            return;
        }
        //RequestContext rc = RequestContext.getCurrentInstance();
        PrimeFaces.current().ajax().update("db_reopened");
        PrimeFaces.current().executeScript("PF('dlgdb_reopened').show()");
    }

    @Override
    public String save() {
        String return_location = "";
        try {
            List<ComparisonBean> compareChanges = compareChanges();
            if (!compareChanges.isEmpty() || techDobj_prev == null) { //save only when data is really changed by user
                TechDataImpl.makeChangeTechData(techDobj, ComparisonBeanImpl.changedDataContents(compareChanges), sessionVariables);
            }
            return_location = "seatwork";
        } catch (VahanException vme) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", vme.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Error-File Could Not Save and Move Due to Technical Error in Database"));
        }
        return return_location;
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        if (techDobj_prev == null) {
            return compBeanList;
        }
        compBeanList.clear();
        Compare("maker", techDobj_prev.getMaker(), techDobj.getMaker(), compBeanList);
        Compare("maker_model", techDobj_prev.getMaker_model(), techDobj.getMaker_model(), compBeanList);
        Compare("fuel", techDobj_prev.getFuel(), techDobj.getFuel(), compBeanList);
        Compare("seat_cap", techDobj_prev.getSeat_cap(), techDobj.getSeat_cap(), compBeanList);
        Compare("stand_cap", techDobj_prev.getStand_cap(), techDobj.getStand_cap(), compBeanList);
        Compare("unld_wt", techDobj_prev.getUnld_wt(), techDobj.getUnld_wt(), compBeanList);
        Compare("ld_wt", techDobj_prev.getLd_wt(), techDobj.getLd_wt(), compBeanList);
        Compare("cubic_cap", techDobj_prev.getCubic_cap(), techDobj.getCubic_cap(), compBeanList);
        Compare("hp", techDobj_prev.getHp(), techDobj.getHp(), compBeanList);
        Compare("no_cyl", techDobj_prev.getNo_cyl(), techDobj.getNo_cyl(), compBeanList);
        Compare("sale_amt", techDobj_prev.getSale_amt(), techDobj.getSale_amt(), compBeanList);
        Compare("ac_fitted", techDobj_prev.getAc_fitted(), techDobj.getAc_fitted(), compBeanList);
        Compare("audio_fitted", techDobj_prev.getAudio_fitted(), techDobj.getAudio_fitted(), compBeanList);
        Compare("video_fitted", techDobj_prev.getVideo_fitted(), techDobj.getVideo_fitted(), compBeanList);
        Compare("mobile_no", techDobj_prev.getMobile_no(), techDobj.getMobile_no(), compBeanList);
        Compare("permit_type", techDobj_prev.getPermitType(), techDobj.getPermitType(), compBeanList);
        Compare("permit_category", techDobj_prev.getPermitCatg(), techDobj.getPermitCatg(), compBeanList);
        Compare("reason", techDobj_prev.getReason(), techDobj.getReason(), compBeanList);
        Compare("norms", techDobj_prev.getNorms(), techDobj.getNorms(), compBeanList);
        return getCompBeanList();
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        try {
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(getAppl_details().getAppl_dt());
            status.setAppl_no(getAppl_details().getAppl_no());
            status.setPur_cd(getAppl_details().getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());

            status.setCurrent_role(appl_details.getCurrent_action_cd());
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)) {
                status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                impl.update_techData_Status(techDobj, techDobj_prev, status, ComparisonBeanImpl.changedDataContents(compareChanges()), sessionVariables);
                return_location = "seatwork";
            }

        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-TEB-1Could not Processed due to Technical Error in Database.", "Error-TEB1-Could not Processed due to Technical Error in Database."));
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
        return return_location;
    }

    private void fillListPmtType() {
        String[][] data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
        techDobj.getPermitTypeList().clear();
        for (int i = 0; i < data.length; i++) {
            if (techDobj.getTransportCatg().equals(data[i][2])) {
                techDobj.getPermitTypeList().add(new SelectItem(data[i][0], data[i][1]));
            }
        }
    }

    public void fillListPmtCatg() {
        String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
        techDobj.getPermitCatgList().clear();
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].equalsIgnoreCase(state_cd)
                    && Integer.parseInt(data[i][3]) == techDobj.getPermitType()) {
                techDobj.getPermitCatgList().add(new SelectItem(data[i][1], data[i][2]));
            }
        }
    }

    private String descrFromList(List<SelectItem> list, String code) {
        String descr = null;
        for (SelectItem si : list) {
            if (si.getValue().toString().equals(code)) {
                descr = si.getLabel().toString();
                break;
            }
        }
        return descr;
    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return this.compBeanList;
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return this.prevChangedDataList;
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
    }

    @Override
    public void saveEApplication() {
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public boolean isTech_dataListPanel() {
        return tech_dataListPanel;
    }

    public void setTech_dataListPanel(boolean tech_dataListPanel) {
        this.tech_dataListPanel = tech_dataListPanel;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public boolean isEnable_disable() {
        return enable_disable;
    }

    public void setEnable_disable(boolean enable_disable) {
        this.enable_disable = enable_disable;
    }

    /**
     * @return the offCd
     */
    public int getOffCd() {
        return offCd;
    }

    /**
     * @param offCd the offCd to set
     */
    public void setOffCd(int offCd) {
        this.offCd = offCd;
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

    public List getList_norms() {
        return list_norms;
    }

    public void setList_norms(List list_norms) {
        this.list_norms = list_norms;
    }
}
