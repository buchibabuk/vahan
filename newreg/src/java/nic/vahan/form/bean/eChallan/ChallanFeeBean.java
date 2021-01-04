package nic.vahan.form.bean.eChallan;

import nic.vahan.form.impl.eChallan.ChallanFeeImpl;
import nic.vahan.form.dobj.eChallan.ChallanFeeDobj;
import nic.vahan.form.dobj.eChallan.ChallanOwnerDobj;
import nic.vahan.form.dobj.eChallan.ChallanOwnerTaxDobj;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.vahan.server.CommonUtils;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.ReceiptMasterBean;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.eChallan.CompoundingInOfficeDobj;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "challanFee")
@ViewScoped
public class ChallanFeeBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static Logger LOGGER = Logger.getLogger(ChallanFeeBean.class);
    private String tfBookNo;
    private String tfChallanNo;
    private String applicationNo;
    private String cmAccussedType;
    private ChallanOwnerDobj challanOwnerDobj = new ChallanOwnerDobj();
    private long payableAmount;
    private String cmPaymentMode;
    private String tfAmount;
    private String ownerName;
    private String advancecmpfee;
    private String feercptno;
    private String taxrcptno;
    private String advancetax;
    private boolean feeDetailsPanel = true;
    private boolean taxDetailsPanel = true;
    private boolean vehicleDetailsPanel = true;
    List<ChallanOwnerTaxDobj> taxDobjList = new ArrayList<ChallanOwnerTaxDobj>();
    private List accussedList = new ArrayList();
    private List stateList = new ArrayList();
    private List rtoList = new ArrayList();
    private List vehicleClassList = new ArrayList();
    private ChallanFeeImpl challanDAO = null;
    List<ChallanOwnerTaxDobj> challanTaxList = null;
    List<ChallanFeeDobj> challanFeeList = null;
    boolean disableSaveButton;
    boolean disablePrintButton;
    private boolean hidetaxpanel;
    private boolean hidefeespanel;
    @ManagedProperty(value = "#{rcpt_bean}")
    private ReceiptMasterBean rcptBean;
    CompoundingInOfficeDobj compDobj = new CompoundingInOfficeDobj();
    private List<ComparisonBean> compBeanList = new ArrayList<ComparisonBean>();
    ChallanFeeDobj dobjrcpt = new ChallanFeeDobj();
    private SessionVariables sessionVariables = null;

    public ChallanFeeBean() throws Exception {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null || nic.vahan.server.CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
            return;
        }
        initilize();
    }

    private void initilize() {
        setDisablePrintButton(false);
        setDisableSaveButton(true);
        setHidefeespanel(false);
        setHidetaxpanel(false);
        try {
            applicationNo = String.valueOf(appl_details.getAppl_no());
            String[][] data = MasterTableFiller.masterTables.TM_OFFICE.getData();
            rtoList.add(new SelectItem("-1", "---Select---"));
            for (int i = 0; i < data.length; i++) {
                rtoList.add(new SelectItem(data[i][0], data[i][1]));
            }
            data = MasterTableFiller.masterTables.TM_STATE.getData();
            stateList.add(new SelectItem("-1", "---Select---"));
            for (int i = 0; i < data.length; i++) {
                stateList.add(new SelectItem(data[i][0], data[i][1]));
            }
            data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
            vehicleClassList.add(new SelectItem("-1", "---Select---"));
            for (int i = 0; i < data.length; i++) {
                vehicleClassList.add(new SelectItem(data[i][0], data[i][1]));
            }

            accusedSelectListener();
        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void getChallanDetailsEvent(AjaxBehaviorEvent event) throws Exception {
        boolean flafFeeDetails = true;
        if (CommonUtils.isNullOrBlank(getApplicationNo()) || CommonUtils.isNullOrBlank(getApplicationNo())) {
            JSFUtils.setFacesMessage("Please Enter A valid Application No", "messages", JSFUtils.ERROR);
            flafFeeDetails = false;
        }
        if (flafFeeDetails) {
            getChallanDetails(getApplicationNo());
        }
    }

    private void getChallanDetails(String applicationNo) {
        boolean isValid = false;
        try {
            isValid = getChallanDAO().isValidChallanNoforFeeCollection(applicationNo);
            if (isValid) {
                JSFUtils.setFacesMessage("Please proceed with the Fee Payment procedure", "messages", JSFUtils.INFO);
            }
            challanOwnerDobj = getChallanDAO().getChallanOwnerDetails(applicationNo);
            if (challanOwnerDobj == null) {
                JSFUtils.setFacesMessage("Challan Owner Details not Found.", "messages", JSFUtils.FATAL);
                reset();
                return;
            }
            setVehicleDetailsPanel(true);
            setFeeDetailsPanel(true);
            setRtoList(getChallanDAO().getRtoByState(challanOwnerDobj.getStateFrom()));
            setChallanOwnerDobj(challanOwnerDobj);
            setOwnerName(getChallanOwnerDobj().getOwnerName());
            challanFeeList = getChallanDAO().getChallanFeeDetails(applicationNo, getCmAccussedType());
            updateAccussedList(getChallanFeeList());
        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    private void updateAccussedList(List<ChallanFeeDobj> list) {
        if (list != null && list.size() > 0) {
            getAccussedList().clear();
            String arr[][] = null;
            int i = 0;
            if (list.size() > 1) {
                arr = new String[list.size() + 1][2];
                arr[0][0] = "A";
                arr[0][1] = "Select All";
                i++;
            } else {
                arr = new String[list.size()][2];
            }
            for (ChallanFeeDobj feeDobj : list) {
                arr[i][0] = feeDobj.getAccusedCatg();
                if ("C".equalsIgnoreCase(feeDobj.getAccusedCatg())) {
                    arr[i][1] = "Conductor";
                } else if ("O".equalsIgnoreCase(feeDobj.getAccusedCatg())) {
                    arr[i][1] = "Owner";
                } else if ("D".equalsIgnoreCase(feeDobj.getAccusedCatg())) {
                    arr[i][1] = "Driver";
                }
                i++;
            }
            getAccussedList().addAll(JSFUtils.fillSelectOne(arr, "Select Accused Category"));
        }
    }

    public void accusedSelectListener() throws Exception {
        boolean isValid = false;
        long comp_fee = 0L;
        try {
            isValid = getChallanDAO().isValidChallanNoforFeeCollection(applicationNo);

            compDobj = getChallanDAO().fatchDetailsOfCourt(applicationNo);
            if (compDobj != null) {
                if (!CommonUtils.isNullOrBlank(compDobj.getCourtPaidAmount())) {
                    JSFUtils.setFacesMessage("Challan Fee Already Paid At Court", "massege", JSFUtils.ERROR);
                    JSFUtils.showMessagesInDialog("Challan Fee Status", "Challan Fee Paid At Court No need to pay Here", FacesMessage.SEVERITY_FATAL);

                }
            } else {
                challanFeeList = getChallanDAO().getChallanFeeDetails(applicationNo, getCmAccussedType());
                updateAccussedList(getChallanFeeList());
                for (ChallanFeeDobj dobjfee : challanFeeList) {
                    comp_fee = Long.parseLong(dobjfee.getcFee());
                    advancecmpfee = dobjfee.getAcFee();
                    feercptno = dobjfee.getRecpNo();
                }
                if (!("0".equals(advancecmpfee)) && (!("".equals(feercptno) || feercptno == null)) && !(Long.parseLong(advancecmpfee) < comp_fee)) {
                    setHidefeespanel(false);
                } else {
                    setHidefeespanel(true);
                }

            }
            challanOwnerDobj = getChallanDAO().getChallanOwnerDetails(applicationNo);
            if (challanOwnerDobj == null) {
                JSFUtils.setFacesMessage("Challan Owner Details not Found.", "messages", JSFUtils.FATAL);
                reset();
                return;
            }
            setVehicleDetailsPanel(true);
            setFeeDetailsPanel(true);
            setRtoList(getChallanDAO().getRtoByState(challanOwnerDobj.getStateFrom()));
            setChallanOwnerDobj(challanOwnerDobj);
            setOwnerName(getChallanOwnerDobj().getOwnerName());

            taxDobjList = getChallanDAO().getChallanOwnerTaxDetails(applicationNo);
            for (ChallanOwnerTaxDobj dobjtax : taxDobjList) {
                advancetax = dobjtax.getAdvtax();
                taxrcptno = dobjtax.getRecpNo();
            }
            if (taxDobjList.size() > 0 && taxDobjList != null) {
                setHidetaxpanel(true);
            } else {
                setHidetaxpanel(false);
            }
            if (!getChallanDAO().isFeeCollectionAllowedAgainstAccusedCategory(applicationNo, getCmAccussedType())) {
                JSFUtils.setFacesMessage("Fee Already Taken for Selected accussed category", "messages", JSFUtils.WARN);
                return;
            } else {
            }
            if (!CommonUtils.isNullOrBlank(getApplicationNo().toUpperCase())) {
                getChallanTaxList().clear();
                setTaxDetailsPanel(true);
                taxDobjList = getChallanDAO().getChallanOwnerTaxDetails(applicationNo);
                if (taxDobjList != null) {
                    getChallanTaxList().addAll(taxDobjList);
                }
            }
            long fees = 0l;
            if (getChallanFeeList() != null) {
                for (ChallanFeeDobj dobj : getChallanFeeList()) {
                    fees = dobj.getsFee() != null ? Long.parseLong(dobj.getsFee()) : 0l;
                    break;
                }
            }
            updateTotalPayableAmount(String.valueOf(fees));


        } catch (VahanException e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void saveFromData() {
        boolean flagCheck = true;
        if (CommonUtils.isNullOrBlank(applicationNo)) {
            JSFUtils.setFacesMessage("Please enter the Application No", "message", JSFUtils.FATAL);
            flagCheck = false;
        }
        if (flagCheck) {
            boolean isSaved = false;
            String officerID = Util.getEmpCode();
            Status_dobj statusDobj = new Status_dobj();
            statusDobj.setRegn_no(challanOwnerDobj.getVehicleNo());
            statusDobj.setPur_cd(TableConstants.VM_MAST_ENFORCEMENT_FEE_TAX);
            statusDobj.setAction_cd(TableConstants.VM_ROLE_ENFORCEMENT_FEE_TAX);
            statusDobj.setFlow_slno(1);
            statusDobj.setFile_movement_slno(1);
            statusDobj.setState_cd(sessionVariables.getStateCodeSelected());
            statusDobj.setOff_cd(sessionVariables.getOffCodeSelected());
            statusDobj.setEmp_cd(0);
            statusDobj.setAppl_dt(appl_details.getAppl_dt());
            statusDobj.setAppl_no(appl_details.getAppl_no());
            statusDobj.setPur_cd(appl_details.getPur_cd());
            statusDobj.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            statusDobj.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            statusDobj.setStatus(getApp_disapp_dobj().getNew_status());
            statusDobj.setCurrent_role(appl_details.getCurrent_role());
            ChallanFeeDobj dobj = new ChallanFeeDobj();
            try {
                if (applicationNo.equals("") || applicationNo == null) {
                    JSFUtils.setFacesMessage("please enter the application No", "message", JSFUtils.FATAL);
                }
                dobj.setApplicationNo(getApplicationNo());
                dobj.setDealCd(officerID);
                dobj.setsFee(getTfAmount());
                dobj.setVehicleNo(getChallanOwnerDobj().getVehicleNo());
                dobj.setAccusedCatg(getCmAccussedType());
                dobj.setStateCd(getChallanOwnerDobj().getStateFrom());
                dobj.setRtoCd(getChallanOwnerDobj().getRtoFrom());
                dobj.setDobjTax(getChallanTaxList());
                dobj.setCmPaymentMode(getCmPaymentMode());
                isSaved = getChallanDAO().saveChallanFee(dobj, taxDobjList, statusDobj, challanOwnerDobj);
                if (isSaved) {
                    reset();
                    JSFUtils.setFacesMessage("Data Saved Successfully.", "messages", JSFUtils.INFO);
                    JSFUtils.setFacesMessage("Your ReceiptNo IS-::" + dobj.getRecpNo(), "message", JSFUtils.INFO);
                    dobjrcpt.setRecpNo(dobj.getRecpNo());
                    setDisablePrintButton(true);
                    setDisableSaveButton(false);
                } else {
                    JSFUtils.setFacesMessage("Unable to process request. Please try after some time.", "messages", JSFUtils.FATAL);
                }
            } catch (VahanException e) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, message);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        }
    }

    public void confirmprintCertificate() {
        // RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('printParticular').show()");
    }

    public void clearForm() {
        reset();
    }

    private void reset() {
        setTfBookNo("");
        setTfChallanNo("");
        setCmAccussedType("-1");
        setPayableAmount(0l);
        getRtoList().clear();
        setCmPaymentMode("-1");
        getChallanTaxList().clear();
        getStateList().clear();
        setChallanOwnerDobj(new ChallanOwnerDobj());
        getVehicleClassList().clear();
        setTfAmount("");
        getAccussedList().clear();

    }

    private void updateTotalPayableAmount(String fees) {
        long totalPayableAmount = 0l;
        long taxTabelAmount = 0l;
        long tFee = 0l;
        if (!CommonUtils.isNullOrBlank(fees)) {
            tFee = Long.parseLong(fees);
        }
        if (getChallanTaxList() != null && getChallanTaxList().size() > 0) {
            for (ChallanOwnerTaxDobj dobj : getChallanTaxList()) {
                String amount = dobj.getTotalAmount();
                if (!CommonUtils.isNullOrBlank(amount)) {
                    taxTabelAmount = taxTabelAmount + Long.parseLong(amount);
                }
            }
        }
        totalPayableAmount = taxTabelAmount + tFee;
        setPayableAmount(totalPayableAmount);
        setTfAmount(String.valueOf(tFee));
    }

    public String getTfBookNo() {
        return tfBookNo;
    }

    public void setTfBookNo(String tfBookNo) {
        this.tfBookNo = tfBookNo;
    }

    public String getTfChallanNo() {
        return tfChallanNo;
    }

    public void setTfChallanNo(String tfChallanNo) {
        this.tfChallanNo = tfChallanNo;
    }

    public String getCmAccussedType() {
        return cmAccussedType;
    }

    public void setCmAccussedType(String cmAccussedType) {
        this.cmAccussedType = cmAccussedType;
    }

    public ChallanOwnerDobj getChallanOwnerDobj() {
        if (challanOwnerDobj == null) {
            challanOwnerDobj = new ChallanOwnerDobj();
        }
        return challanOwnerDobj;
    }

    public void setChallanOwnerDobj(ChallanOwnerDobj challanOwnerDobj) {
        this.challanOwnerDobj = challanOwnerDobj;
    }

    public long getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(long payableAmount) {
        this.payableAmount = payableAmount;
    }

    public List getAccussedList() {
        return accussedList;
    }

    public void setAccussedList(List accussedList) {
        this.accussedList = accussedList;
    }

    public List getStateList() {
        return stateList;
    }

    public void setStateList(List stateList) {
        this.stateList = stateList;
    }

    public List getRtoList() {
        return rtoList;
    }

    public void setRtoList(List rtoList) {
        this.rtoList = rtoList;
    }

    public List getVehicleClassList() {
        return vehicleClassList;
    }

    public void setVehicleClassList(List vehicleClassList) {
        this.vehicleClassList = vehicleClassList;
    }

    public ChallanFeeImpl getChallanDAO() {
        if (challanDAO == null) {
            challanDAO = new ChallanFeeImpl();
        }
        return challanDAO;
    }

    public List<ChallanOwnerTaxDobj> getChallanTaxList() {
        if (challanTaxList == null) {
            challanTaxList = new ArrayList<ChallanOwnerTaxDobj>();
        }
        return challanTaxList;
    }

    public void setChallanTaxList(List<ChallanOwnerTaxDobj> challanTaxList) {
        this.challanTaxList = challanTaxList;
    }

    public String getCmPaymentMode() {
        return cmPaymentMode;
    }

    public void setCmPaymentMode(String cmPaymentMode) {
        this.cmPaymentMode = cmPaymentMode;
    }

    public String getTfAmount() {
        return tfAmount;
    }

    public void setTfAmount(String tfAmount) {
        this.tfAmount = tfAmount;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public List<ChallanFeeDobj> getChallanFeeList() {
        return challanFeeList;
    }

    public void setChallanFeeList(List<ChallanFeeDobj> challanFeeList) {
        this.challanFeeList = challanFeeList;
    }

    public boolean isFeeDetailsPanel() {
        return feeDetailsPanel;
    }

    public void setFeeDetailsPanel(boolean feeDetailsPanel) {
        this.feeDetailsPanel = feeDetailsPanel;
    }

    public boolean isTaxDetailsPanel() {
        return taxDetailsPanel;
    }

    public void setTaxDetailsPanel(boolean taxDetailsPanel) {
        this.taxDetailsPanel = taxDetailsPanel;
    }

    public boolean isVehicleDetailsPanel() {
        return vehicleDetailsPanel;
    }

    public void setVehicleDetailsPanel(boolean vehicleDetailsPanel) {
        this.vehicleDetailsPanel = vehicleDetailsPanel;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    public List<ChallanOwnerTaxDobj> getTaxDobjList() {
        return taxDobjList;
    }

    public void setTaxDobjList(List<ChallanOwnerTaxDobj> taxDobjList) {
        this.taxDobjList = taxDobjList;
    }

    public boolean isDisableSaveButton() {
        return disableSaveButton;
    }

    public void setDisableSaveButton(boolean disableSaveButton) {
        this.disableSaveButton = disableSaveButton;
    }

    public ReceiptMasterBean getRcptBean() {
        return rcptBean;
    }

    public void setRcptBean(ReceiptMasterBean rcptBean) {
        this.rcptBean = rcptBean;
    }

    @Override
    public String save() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        return compBeanList;
    }

    @Override
    public String saveAndMoveFile() {
        String returnLocation = "";

        return returnLocation;
    }

    @Override
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isHidetaxpanel() {
        return hidetaxpanel;
    }

    public void setHidetaxpanel(boolean hidetaxpanel) {
        this.hidetaxpanel = hidetaxpanel;
    }

    public boolean isHidefeespanel() {
        return hidefeespanel;
    }

    public void setHidefeespanel(boolean hidefeespanel) {
        this.hidefeespanel = hidefeespanel;
    }

    public boolean isDisablePrintButton() {
        return disablePrintButton;
    }

    public void setDisablePrintButton(boolean disablePrintButton) {
        this.disablePrintButton = disablePrintButton;
    }
}
