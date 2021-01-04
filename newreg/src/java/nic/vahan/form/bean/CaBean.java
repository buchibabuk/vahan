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
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.CaDobj;
import nic.vahan.form.dobj.InspectionDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.CaImpl;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.FitnessImpl;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
//import org.primefaces.context.RequestContext;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "ca_bean")
@ViewScoped
public class CaBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(CaBean.class);
    private List list_c_district;
    private List list_p_district;
    private List list_c_state;
    private List list_p_state;
    private CaDobj ca_dobj = new CaDobj();
    private CaDobj ca_dobj_prv = null;
    private List<ComparisonBean> prevChangedDataList;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private String masterLayout = "/masterLayoutPage_new.xhtml";
    private boolean sameAsCurrAddress;
    private List<OwnerDetailsDobj> listExistingOwnerDetails = new ArrayList<>();
    private Date maxDate = new Date();
    private Date minDate;
    private String generated_appl_no = null;
    private boolean rendersavebutton = false;
    private boolean renderprintbutton = false;
    private String vahanMessages = null;
    private boolean sameAsPreviousPermanentAddress;
    private InspectionDobj inspDobj = new InspectionDobj();
    private InspectionDobj inspDobjPrev = null;
    private boolean renderInspBody;

    public CaBean() {
        list_c_district = new ArrayList();
        list_p_district = new ArrayList();
        list_c_state = new ArrayList();
        list_p_state = new ArrayList();
        rendersavebutton = true;
        renderprintbutton = false;
        renderInspBody = false;
    }

    @PostConstruct
    public void init() {
        CaImpl ca_Impl = new CaImpl();
        try {

            if (getAppl_details() == null
                    || getAppl_details().getCurrent_state_cd() == null
                    || getAppl_details().getCurrent_off_cd() == 0) {
                vahanMessages = "Something went wrong, Please try again later...";
                return;
            }

            if (getAppl_details() != null) {
                list_c_state = MasterTableFiller.getStateList();
                list_p_state = MasterTableFiller.getStateList();
                list_c_district = MasterTableFiller.getDistrictList(getAppl_details().getCurrent_state_cd());
                list_p_district = MasterTableFiller.getDistrictList(getAppl_details().getCurrent_state_cd());
                String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
                ca_dobj = ca_Impl.set_CA_appl_db_to_dobj(getAppl_details().getAppl_no());
                if (ca_dobj == null) {
                    ca_dobj = new CaDobj();
                    ca_dobj.setAppl_no(appl_details.getAppl_no());
                    ca_dobj.setRegn_no(appl_details.getRegn_no());
                    ca_dobj.setC_state(appl_details.getCurrent_state_cd());
                    ca_dobj.setP_state(appl_details.getCurrent_state_cd());
                    ca_dobj.setState_cd(appl_details.getCurrent_state_cd());
                    ca_dobj.setOff_cd(appl_details.getCurrent_off_cd());
                } else {
                    ca_dobj_prv = (CaDobj) ca_dobj.clone();
                    list_p_district.clear();
                    for (int i = 0; i < data.length; i++) {//need to fix this for setting only list according to state..pending..
                        if (ca_dobj.getP_state().equalsIgnoreCase(data[i][2])) {
                            list_p_district.add(new SelectItem(data[i][0], data[i][1]));
                        }
                    }
                }

                //for displaying data of inspection which is filled by operator
                if (appl_details.getCurrent_action_cd() == TableConstants.INSPECTION_FOR_CHANGE_OF_ADDRESS
                        || appl_details.getCurrent_action_cd() == TableConstants.CHANGE_OF_ADDRESS_IN_RC_APPROVAL) {

                    if (appl_details.getCurrent_action_cd() == TableConstants.INSPECTION_FOR_CHANGE_OF_ADDRESS) {
                        renderInspBody = true;
                    }
                    FitnessImpl fitnessImpl = new FitnessImpl();
                    InspectionDobj inspectionDobj = fitnessImpl.getVaInspectionDobj(appl_details.getAppl_no());
                    if (inspectionDobj != null) {
                        inspDobj = inspectionDobj;
                    }
                    if (inspDobj != null && inspDobj.getInsp_dt() != null) {
                        inspDobjPrev = (InspectionDobj) inspDobj.clone();
                    }
                }

                //setting the current address details in the current information about the address
                listExistingOwnerDetails.add(appl_details.getOwnerDetailsDobj());

                if (appl_details.getOwnerDetailsDobj() == null) {
                    vahanMessages = "Owner Details not Found in the Database, Please Contact to the System Administrator";
                    return;
                }
                prevChangedDataList = ComparisonBeanImpl.dataChangedByPreviousUsers(getAppl_details().getAppl_no(), getAppl_details().getPur_cd());
                //minDate = ServerUtil.dateRange(getAppl_details().getOwnerDobj().getRegn_dt(), 0, 0, 1);
                minDate = getAppl_details().getOwnerDobj().getRegn_dt();
            }
        } catch (VahanException vex) {
            vahanMessages = vex.getMessage();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " [ApplNo-" + appl_details.getAppl_no() + ",RegnNo-" + appl_details.getRegn_no() + ",PurCd-" + appl_details.getPur_cd() + "] " + ex.getStackTrace()[0]);
            vahanMessages = TableConstants.SomthingWentWrong;
        }
    }

    /**
     * @return the list_c_district
     */
    public List getList_c_district() {
        return list_c_district;
    }

    /**
     * @param list_c_district the list_c_district to set
     */
    public void setList_c_district(List list_c_district) {
        this.list_c_district = list_c_district;
    }

    /**
     * @return the list_p_district
     */
    public List getList_p_district() {
        return list_p_district;
    }

    /**
     * @param list_p_district the list_p_district to set
     */
    public void setList_p_district(List list_p_district) {
        this.list_p_district = list_p_district;
    }

    public void cStateListener(AjaxBehaviorEvent event) {

        String cStateCd = String.valueOf(ca_dobj.getC_state());
        String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
        list_c_district.clear();
        for (int i = 0; i < data.length; i++) {
            if (data[i][2].equals(cStateCd)) {
                list_c_district.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
    }//end of vehCStateListener

    public void pStateListener(AjaxBehaviorEvent event) {

        String pStateCd = String.valueOf(ca_dobj.getP_state());
        String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
        list_p_district.clear();
        for (int i = 0; i < data.length; i++) {
            if (data[i][2].equals(pStateCd)) {
                list_p_district.add(new SelectItem(data[i][0], data[i][1]));
            }
        }
    }

    public CaDobj getCa_dobj_prv() {
        return ca_dobj_prv;
    }

    /**
     * @param ca_dobj_prv the ca_dobj_prv to set
     */
    public void setCa_dobj_prv(CaDobj ca_dobj_prv) {

        this.ca_dobj_prv = ca_dobj_prv;
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
     * @return the appl_details
     */
    @Override
    public String save() {//for updating or inserting insurance details with CA details
        String return_location = "";
        try {

            if (appl_details.getCurrent_action_cd() == TableConstants.INSPECTION_FOR_CHANGE_OF_ADDRESS) {
                inspDobj.setAppl_no(appl_details.getAppl_no());
                inspDobj.setRegn_no(appl_details.getRegn_no());
                inspDobj.setState_cd(getAppl_details().getCurrent_state_cd());
                inspDobj.setOff_cd(getAppl_details().getCurrent_off_cd());
                inspDobj.setFit_off_cd1(Integer.parseInt(getAppl_details().getCurrentEmpCd()));
            }
            List<ComparisonBean> compareChanges = compareChanges();
            if (!compareChanges.isEmpty() || ca_dobj_prv == null
                    || (inspDobjPrev == null && appl_details.getCurrent_action_cd() == TableConstants.INSPECTION_FOR_CHANGE_OF_ADDRESS)) { //save only when data is really changed by user
                CaImpl.makeChangeCA(ca_dobj, ComparisonBeanImpl.changedDataContents(compareChanges), inspDobj, appl_details.getCurrent_action_cd());
            }
            return_location = "seatwork";
        } catch (VahanException vme) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), vme.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
        }
        return return_location;
    }

    @Override
    public List<ComparisonBean> compareChanges() {

        if (ca_dobj_prv == null
                || (inspDobjPrev == null
                && appl_details.getCurrent_action_cd() == TableConstants.INSPECTION_FOR_CHANGE_OF_ADDRESS)) {
            return compBeanList;
        }

        compBeanList.clear();
        Compare("C_Add", ca_dobj_prv.getC_add1(), ca_dobj.getC_add1(), compBeanList);
        Compare("C_Add", ca_dobj_prv.getC_add2(), ca_dobj.getC_add2(), compBeanList);
        Compare("C_Add", ca_dobj_prv.getC_add3(), ca_dobj.getC_add3(), compBeanList);
        Compare("C_Dist", ca_dobj_prv.getC_district(), ca_dobj.getC_district(), compBeanList);
        Compare("C_Pin", ca_dobj_prv.getC_pincode(), ca_dobj.getC_pincode(), compBeanList);
        Compare("P_Add", ca_dobj_prv.getP_add1(), ca_dobj.getP_add1(), compBeanList);
        Compare("P_Add", ca_dobj_prv.getP_add2(), ca_dobj.getP_add2(), compBeanList);
        Compare("P_Add", ca_dobj_prv.getP_add3(), ca_dobj.getP_add3(), compBeanList);
        Compare("P_State", ca_dobj_prv.getP_state(), ca_dobj.getP_state(), compBeanList);
        Compare("P_Dist", ca_dobj_prv.getP_district(), ca_dobj.getP_district(), compBeanList);
        Compare("P_Pin", ca_dobj_prv.getP_pincode(), ca_dobj.getP_pincode(), compBeanList);
        Compare("W_E_From", ca_dobj_prv.getFrom_dt(), ca_dobj.getFrom_dt(), compBeanList);

        if (inspDobj != null && inspDobj.getInsp_dt() != null) {
            Compare("INSP_REMARK", inspDobjPrev.getRemark(), inspDobj.getRemark(), compBeanList);
            Compare("W_E_From", inspDobjPrev.getInsp_dt(), inspDobj.getInsp_dt(), compBeanList);
        }

        return getCompBeanList();
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        try {
            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());
            if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE)
                    || getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_REVERT)
                    || getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)
                    || appl_details.getCurrent_action_cd() == TableConstants.INSPECTION_FOR_CHANGE_OF_ADDRESS) {

                status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());
                status.setVehicleParameters(appl_details.getVehicleParameters());

                if (getApp_disapp_dobj().getNew_status().equals(TableConstants.STATUS_COMPLETE) && this.appl_details.isDocVerifyForFacelessAppl()) {
                    String[] notVerifiedDocDetails = ServerUtil.verifyDocUploaded(appl_details.getAppl_no(), getAppl_details().getCurrent_action_cd());
                    if (notVerifiedDocDetails != null) {
                        appl_details.setNotVerifiedDocdetails(notVerifiedDocDetails[1]);
                        throw new VahanException(notVerifiedDocDetails[0]);
                    }
                }

                //for setting condition for inspection flow in change of address 
                if (appl_details.getCurrent_state_cd().equalsIgnoreCase(appl_details.getOwnerDobj().getState_cd())
                        && appl_details.getCurrent_off_cd() != appl_details.getOwnerDobj().getOff_cd()) {
                    status.getVehicleParameters().setINSP_RQRD("true");
                }

                if (appl_details.getCurrent_action_cd() == TableConstants.INSPECTION_FOR_CHANGE_OF_ADDRESS) {
                    inspDobj.setAppl_no(appl_details.getAppl_no());
                    inspDobj.setRegn_no(appl_details.getRegn_no());
                    inspDobj.setState_cd(getAppl_details().getCurrent_state_cd());
                    inspDobj.setOff_cd(getAppl_details().getCurrent_off_cd());
                    inspDobj.setFit_off_cd1(Integer.parseInt(getAppl_details().getCurrentEmpCd()));
                }

                CaImpl ca_Impl = new CaImpl();
                ca_Impl.update_CA_Status(ca_dobj, ca_dobj_prv, status, ComparisonBeanImpl.changedDataContents(compareChanges()), inspDobj, inspDobjPrev, appl_details);
                if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    ServerUtil.sendSmsInFacelessService(appl_details.getAppl_no(), Util.getUserStateCode(), Util.getUserSeatOffCode(), appl_details.getRegn_no(),appl_details.getNotVerifiedDocdetails());
                    return_location = disapprovalPrint();
                } else {
                    return_location = "seatwork";
                }
            }
            if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_COMPLETE) && appl_details.getCurrent_role() == 4) {
                if (!ServerUtil.getDataEntryIncomplete(appl_details.getAppl_no())) {
                    PrimeFaces.current().ajax().update("app_disapp_new_form:showOwnerDiscPopup");
                    PrimeFaces.current().executeScript("PF('successDialog').show()");
                    return_location = "";
                }
            }
        } catch (VahanException vme) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vme.getMessage(), vme.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
        return return_location;
    }

    public String printDisclaimer() {
        return PrintDocImpl.printOwnerDiscReport("registeredVehicles", "reportFormat");
    }

    /**
     * @return the ca_dobj
     */
    public CaDobj getCa_dobj() {
        return ca_dobj;
    }

    /**
     * @param ca_dobj the ca_dobj to set
     */
    public void setCa_dobj(CaDobj ca_dobj) {
        this.ca_dobj = ca_dobj;
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

    public void sameAsCurrentAddressListener(AjaxBehaviorEvent event) {

        if (this.sameAsCurrAddress) {
            this.sameAsPreviousPermanentAddress = false;
            this.ca_dobj.setP_add1(this.ca_dobj.getC_add1());
            this.ca_dobj.setP_add2(this.ca_dobj.getC_add2());
            this.ca_dobj.setP_add3(this.ca_dobj.getC_add3());
            this.ca_dobj.setP_pincode(this.ca_dobj.getC_pincode());
            this.ca_dobj.setP_state(this.ca_dobj.getC_state());

            if (this.ca_dobj.getC_district() != -1) {
                String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
                list_p_district.clear();
                for (int i = 0; i < data.length; i++) {
                    if (this.ca_dobj.getC_state().equalsIgnoreCase(data[i][2])) {
                        list_p_district.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
                this.ca_dobj.setP_district(this.ca_dobj.getC_district());
            }

        } else {
            this.ca_dobj.setP_add1("");
            this.ca_dobj.setP_add2("");
            this.ca_dobj.setP_add3("");
            this.ca_dobj.setP_pincode(0);
            this.ca_dobj.setP_district(-1);
        }
    }

    public void sameAsPreviousPermanentAddressListener(AjaxBehaviorEvent event) {
        if (this.sameAsPreviousPermanentAddress) {
            this.sameAsCurrAddress = false;
            this.ca_dobj.setP_add1(this.listExistingOwnerDetails.get(0).getP_add1());
            this.ca_dobj.setP_add2(this.listExistingOwnerDetails.get(0).getP_add2());
            this.ca_dobj.setP_add3(this.listExistingOwnerDetails.get(0).getP_add3());
            this.ca_dobj.setP_pincode(this.listExistingOwnerDetails.get(0).getP_pincode());
            this.ca_dobj.setP_state(this.listExistingOwnerDetails.get(0).getP_state());

            if (this.ca_dobj.getC_district() != -1) {
                String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
                list_p_district.clear();
                for (int i = 0; i < data.length; i++) {
                    if (this.ca_dobj.getC_state().equalsIgnoreCase(data[i][2])) {
                        list_p_district.add(new SelectItem(data[i][0], data[i][1]));
                    }
                }
                this.ca_dobj.setP_district(this.listExistingOwnerDetails.get(0).getP_district());
            }
        } else {
            this.ca_dobj.setP_add1("");
            this.ca_dobj.setP_add2("");
            this.ca_dobj.setP_add3("");
            this.ca_dobj.setP_pincode(0);
            this.ca_dobj.setP_district(-1);
        }

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
     * @return the sameAsCurrAddress
     */
    public boolean isSameAsCurrAddress() {
        return sameAsCurrAddress;
    }

    /**
     * @param sameAsCurrAddress the sameAsCurrAddress to set
     */
    public void setSameAsCurrAddress(boolean sameAsCurrAddress) {
        this.sameAsCurrAddress = sameAsCurrAddress;
    }

    /**
     * @return the list_c_state
     */
    public List getList_c_state() {
        return list_c_state;
    }

    /**
     * @param list_c_state the list_c_state to set
     */
    public void setList_c_state(List list_c_state) {
        this.list_c_state = list_c_state;
    }

    /**
     * @return the list_p_state
     */
    public List getList_p_state() {
        return list_p_state;
    }

    /**
     * @param list_p_state the list_p_state to set
     */
    public void setList_p_state(List list_p_state) {
        this.list_p_state = list_p_state;
    }

    /**
     * @return the listExistingOwnerDetails
     */
    public List<OwnerDetailsDobj> getListExistingOwnerDetails() {
        return listExistingOwnerDetails;
    }

    /**
     * @param listExistingOwnerDetails the listExistingOwnerDetails to set
     */
    public void setListExistingOwnerDetails(List<OwnerDetailsDobj> listExistingOwnerDetails) {
        this.listExistingOwnerDetails = listExistingOwnerDetails;
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
     * @return the generated_appl_no
     */
    public String getGenerated_appl_no() {
        return generated_appl_no;
    }

    /**
     * @param generated_appl_no the generated_appl_no to set
     */
    public void setGenerated_appl_no(String generated_appl_no) {
        this.generated_appl_no = generated_appl_no;
    }

    /**
     * @return the rendersavebutton
     */
    public boolean isRendersavebutton() {
        return rendersavebutton;
    }

    /**
     * @param rendersavebutton the rendersavebutton to set
     */
    public void setRendersavebutton(boolean rendersavebutton) {
        this.rendersavebutton = rendersavebutton;
    }

    /**
     * @return the renderprintbutton
     */
    public boolean isRenderprintbutton() {
        return renderprintbutton;
    }

    /**
     * @param renderprintbutton the renderprintbutton to set
     */
    public void setRenderprintbutton(boolean renderprintbutton) {
        this.renderprintbutton = renderprintbutton;
    }

    /**
     * @return the minDate
     */
    public Date getMinDate() {
        return minDate;
    }

    /**
     * @param minDate the minDate to set
     */
    public void setMinDate(Date minDate) {
        this.minDate = minDate;
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

    public boolean isSameAsPreviousPermanentAddress() {
        return sameAsPreviousPermanentAddress;
    }

    public void setSameAsPreviousPermanentAddress(boolean sameAsPreviousPermanentAddress) {
        this.sameAsPreviousPermanentAddress = sameAsPreviousPermanentAddress;
    }

    /**
     * @return the inspDobj
     */
    public InspectionDobj getInspDobj() {
        return inspDobj;
    }

    /**
     * @param inspDobj the inspDobj to set
     */
    public void setInspDobj(InspectionDobj inspDobj) {
        this.inspDobj = inspDobj;
    }

    /**
     * @return the inspDobjPrev
     */
    public InspectionDobj getInspDobjPrev() {
        return inspDobjPrev;
    }

    /**
     * @param inspDobjPrev the inspDobjPrev to set
     */
    public void setInspDobjPrev(InspectionDobj inspDobjPrev) {
        this.inspDobjPrev = inspDobjPrev;
    }

    /**
     * @return the renderInspBody
     */
    public boolean isRenderInspBody() {
        return renderInspBody;
    }

    /**
     * @param renderInspBody the renderInspBody to set
     */
    public void setRenderInspBody(boolean renderInspBody) {
        this.renderInspBody = renderInspBody;
    }
}