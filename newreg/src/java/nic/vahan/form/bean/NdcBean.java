/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.form.dobj.NdcDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.form.impl.InsImpl;
import nic.vahan.form.impl.NdcImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;

@ManagedBean(name = "ndc_bean")
@ViewScoped
public class NdcBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(NdcBean.class);
    private List state_list;
    private List office_list;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private List<ComparisonBean> prevChangedDataList;
    private NdcDobj ndc_dobj_prv;
    private Date today = new Date();
    private NdcDobj ndc_dobj = new NdcDobj();
    private boolean comp_disable = false;
    private int pur_cd;
    @ManagedProperty(value = "#{ins_bean}")
    private InsBean ins_bean;
    private String vahanMessages = null;
    private SessionVariables sessionVariables = null;
    private String regn_no;
    private OwnerDetailsDobj ownerDetail;
    private boolean isPermitVehicle;

    //end
    public NdcBean() {

        sessionVariables = new SessionVariables();
        if (sessionVariables == null
                || sessionVariables.getStateCodeSelected() == null
                || sessionVariables.getOffCodeSelected() == 0
                || sessionVariables.getEmpCodeLoggedIn() == null
                || sessionVariables.getUserCatgForLoggedInUser() == null) {
            vahanMessages = "Something went wrong, Please try again...";
            return;
        }

        if (appl_details == null || appl_details.getCurrent_state_cd() == null || appl_details.getCurrent_off_cd() == 0) {
            vahanMessages = "Something went wrong, Please try again...";
            return;
        }
        state_list = new ArrayList();
        office_list = new ArrayList();
        state_list = MasterTableFiller.getStateList();
    }

    @PostConstruct
    public void init() {
        InsDobj ins_dobj_ret = null;
        InsDobj ins_dobj_retVA = null;
        try {
            if (getAppl_details() != null) {
                ins_bean.setAppl_no(appl_details.getAppl_no());
                ins_bean.setRegn_no(appl_details.getRegn_no());
                ins_bean.setPur_cd(appl_details.getPur_cd());
                ins_dobj_ret = InsImpl.set_ins_dtls_db_to_dobj(appl_details.getRegn_no(), null, appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd());
                ins_dobj_retVA = InsImpl.set_ins_dtls_db_to_dobjVA(appl_details.getRegn_no());
                ins_bean.validateInsurance(ins_dobj_ret, ins_dobj_retVA, false);
                if (!CommonUtils.isNullOrBlank(appl_details.getAppl_no())) {
                    NdcDobj dobj = new NdcImpl().getNDCDetails(appl_details.getAppl_no());
                    if (dobj != null) {
                        ndc_dobj = dobj;
                        ndc_dobj_prv = (NdcDobj) ndc_dobj.clone();
                        prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());

                    } else if (getAppl_details().getCurrent_action_cd() == TableConstants.ISSUE_OF_NDC_ENTRY && getAppl_details().getOwnerDobj() != null) {
                        ndc_dobj.setState_to(getAppl_details().getOwnerDobj().getState_cd());
                        ndc_dobj.setOff_to(getAppl_details().getOwnerDobj().getOff_cd());
                        String permitNo = CommonPermitPrintImpl.getPmtNoThroughVtPermit(appl_details.getRegn_no());
                        if (!CommonUtils.isNullOrBlank(permitNo)) {
                            ndc_dobj.setPmt_no(permitNo);
                        } else {
                            ndc_dobj.setPmt_no("NA");
                        }

                    }
                }
                filterNDCOffcList();
            }

        } catch (VahanException ve) {
            vahanMessages = ve.getMessage();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " [ApplNo-" + appl_details.getAppl_no() + ",RegnNo-" + appl_details.getRegn_no() + ",PurCd-" + appl_details.getPur_cd() + "] " + ex.getStackTrace()[0]);
            vahanMessages = "Something Went Wrong, Please Contact to the System Administrator";
        }
    }

    @Override
    public List<ComparisonBean> compareChanges() {

        if (ndc_dobj_prv == null) {
            return compBeanList;
        }
        compBeanList.clear();

        Compare("ncrb_no", ndc_dobj_prv.getNcrb_no(), ndc_dobj.getNcrb_no(), (ArrayList) compBeanList);
        Compare("dl_no", ndc_dobj_prv.getDl_no(), ndc_dobj.getDl_no(), (ArrayList) compBeanList);
        Compare("badge_no", ndc_dobj_prv.getBadge_no(), ndc_dobj.getBadge_no(), (ArrayList) compBeanList);
        Compare("remark", ndc_dobj_prv.getRemark(), ndc_dobj.getRemark(), (ArrayList) compBeanList);


        return compBeanList;

    }

    @Override
    public String save() {
        String return_location = "";
        try {
            InsDobj ins_dobj_new = ins_bean.set_InsBean_to_dobj();
            if (ins_dobj_new != null && ins_bean.validateInsurance(ins_dobj_new)) {
                ins_bean.ins_update(appl_details.getOwnerDetailsDobj());
                List<ComparisonBean> compareChanges = compareChanges();
                if (!compareChanges.isEmpty() || ndc_dobj_prv == null) { //save only when data is really changed by user and when form is empty
                    ndc_dobj.setState_cd(appl_details.getCurrent_state_cd());
                    ndc_dobj.setOff_cd(appl_details.getCurrent_off_cd());
                    ndc_dobj.setPur_cd(pur_cd);
                    NdcImpl.makeChangeNDC(ndc_dobj, ComparisonBeanImpl.changedDataContents(compareChanges), sessionVariables.getEmpCodeLoggedIn());
                }
                return_location = "seatwork";
            } else {
                if (ins_dobj_new == null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Insurance Details not available !!!"));
                }
            }

        } catch (VahanException ve) {
            JSFUtils.showMessagesInDialog("Warn !!!", ve.getMessage(), FacesMessage.SEVERITY_WARN);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Error !!!", "Error-File Could Not Save and Move Due to Technical Error in Database", FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
        }
        return return_location;
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        try {
            InsDobj ins_dobj_new = ins_bean.set_InsBean_to_dobj();
            if (ins_dobj_new != null) {
                Status_dobj status = new Status_dobj();
                status.setAppl_dt(appl_details.getAppl_dt());
                status.setAppl_no(appl_details.getAppl_no());
                status.setPur_cd(appl_details.getPur_cd());
                status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
                status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
                status.setStatus(getApp_disapp_dobj().getNew_status());
                status.setCurrent_role(appl_details.getCurrent_action_cd() % 100);

                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                        || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)
                        || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                    ndc_dobj.setState_cd(appl_details.getCurrent_state_cd());
                    ndc_dobj.setOff_cd(appl_details.getCurrent_off_cd());
                    ndc_dobj.setAppl_no(appl_details.getAppl_no());
                    ndc_dobj.setRegn_no(appl_details.getRegn_no());
                    ndc_dobj.setPur_cd(pur_cd);
                    if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE) && this.appl_details.isDocVerifyForFacelessAppl()) {
                        String[] notVerifiedDocDetails = ServerUtil.verifyDocUploaded(appl_details.getAppl_no(), getAppl_details().getCurrent_action_cd());
                        if (notVerifiedDocDetails != null) {
                            appl_details.setNotVerifiedDocdetails(notVerifiedDocDetails[1]);
                            throw new VahanException(notVerifiedDocDetails[0]);
                        }
                    }
                    List<ComparisonBean> compareChanges = compareChanges();
                    NdcImpl.saveChangesAndFileMove(ndc_dobj, ComparisonBeanImpl.changedDataContents(compareChanges), sessionVariables.getEmpCodeLoggedIn(), status);
                    if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                        ServerUtil.sendSmsInFacelessService(appl_details.getAppl_no(), Util.getUserStateCode(), Util.getUserSeatOffCode(), appl_details.getRegn_no(), appl_details.getNotVerifiedDocdetails());
                        return_location = disapprovalPrint();
                    } else {
                        return_location = "seatwork";
                    }
                }
            } else {
                if (ins_dobj_new == null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Insurance Details not available !!!"));
                }
            }

        } catch (VahanException ve) {
            JSFUtils.showMessagesInDialog("Warn !!!", ve.getMessage(), FacesMessage.SEVERITY_WARN);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Error !!!", "Error-File Could Not Save and Move Due to Technical Error in Database", FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
        }
        return return_location;
    }

    public void updateRtoFromStateListener(AjaxBehaviorEvent even) {
        try {
            filterNDCOffcList();
            if (Util.getUserStateCode().equalsIgnoreCase(ndc_dobj.getState_to())) {
                Iterator ite = office_list.iterator();
                while (ite.hasNext()) {
                    SelectItem obj = (SelectItem) ite.next();
                    if (Integer.parseInt(obj.getValue().toString()) == appl_details.getCurrent_off_cd()) {
                        office_list.remove(obj);
                        break;
                    }
                }
            }
        } catch (VahanException ve) {
            vahanMessages = ve.getMessage();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " [ApplNo-" + appl_details.getAppl_no() + ",RegnNo-" + appl_details.getRegn_no() + ",PurCd-" + appl_details.getPur_cd() + "] " + ex.getStackTrace()[0]);
            vahanMessages = "Something Went Wrong, Please Contact to the System Administrator";
        }
    }

    private void filterNDCOffcList() throws VahanException {
        office_list.clear();
        List<Integer> offTypeCd = Arrays.asList(0, 1, 2);
        office_list = ServerUtil.getOfficeBasedOnType(ndc_dobj.getState_to(), offTypeCd);
    }

    /**
     * @return the state_list
     */
    public List getState_list() {
        return state_list;
    }

    /**
     * @param state_list the state_list to set
     */
    public void setState_list(List state_list) {
        this.state_list = state_list;
    }

    /**
     * @return the compBeanList
     */
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
     * @return the ndc_dobj_prv
     */
    public NdcDobj getNdc_dobj_prv() {
        return ndc_dobj_prv;
    }

    /**
     * @param ndc_dobj_prv the ndc_dobj_prv to set
     */
    public void setNdc_dobj_prv(NdcDobj ndc_dobj_prv) {
        this.ndc_dobj_prv = ndc_dobj_prv;
    }

    /**
     * @return the ndc_dobj
     */
    public NdcDobj getNdc_dobj() {
        return ndc_dobj;
    }

    /**
     * @param ndc_dobj the ndc_dobj to set
     */
    public void setNdc_dobj(NdcDobj ndc_dobj) {
        this.ndc_dobj = ndc_dobj;
    }

    /**
     * @return the prevChangedDataList
     */
    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return prevChangedDataList;
    }

    /**
     * @param prevChangedDataList the prevChangedDataList to set
     */
    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList;
    }

    @Override
    public void saveEApplication() {
    }

    /**
     * @return the office_list
     */
    public List getOffice_list() {
        return office_list;
    }

    /**
     * @param office_list the office_list to set
     */
    public void setOffice_list(List office_list) {
        this.office_list = office_list;
    }

    /**
     * @return the today
     */
    public Date getToday() {
        return today;
    }

    /**
     * @param today the today to set
     */
    public void setToday(Date today) {
        this.today = today;
    }

    /**
     * @return the comp_disable
     */
    public boolean isComp_disable() {
        return comp_disable;
    }

    /**
     * @param comp_disable the comp_disable to set
     */
    public void setComp_disable(boolean comp_disable) {
        this.comp_disable = comp_disable;
    }

    /**
     * @param ins_bean the ins_bean to set
     */
    public void setIns_bean(InsBean ins_bean) {
        this.ins_bean = ins_bean;
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

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
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

    public boolean isIsPermitVehicle() {
        return isPermitVehicle;
    }

    public void setIsPermitVehicle(boolean isPermitVehicle) {
        this.isPermitVehicle = isPermitVehicle;
    }
}
