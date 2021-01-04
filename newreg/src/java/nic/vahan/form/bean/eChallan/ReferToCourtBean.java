package nic.vahan.form.bean.eChallan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import nic.vahan.form.dobj.eChallan.RefertoCourtDobj;
import nic.vahan.form.impl.eChallan.ReferToCourtImpl;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.form.dobj.eChallan.CourtCasesDobj;
import nic.vahan.form.dobj.eChallan.InformationViolationReportDobj;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "refTocourt")
@SessionScoped
public class ReferToCourtBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(ReferToCourtBean.class);
    InformationViolationReportDobj irDobj;
    InformationViolationReportDobj dobjValue;
    private String applicationno;
    private String challanNo;
    private String vehicleNo;
    private Date challanDate;
    private Date opdate;
    private Date hearingDate;
    private List courtcode;
    private String ownerName;
    private String courtName;
    private boolean pendingCase;
    private boolean vehicleDetailPenal;
    private boolean challanDetailPenal;
    private boolean pendingCasePenal;
    private List<RefertoCourtDobj> listOfPendingCase = new ArrayList<RefertoCourtDobj>();
    private List<CourtCasesDobj> listOfCourtCases = new ArrayList<CourtCasesDobj>();
    private List courtlist = new ArrayList();
    private boolean flag = true;
    private boolean saveButtonDisabled;
    private List selectedPendingCases = new ArrayList();
    private boolean SelectRow;

    @PostConstruct
    public void Init() {
        try {

            ReferToCourtImpl impl = new ReferToCourtImpl();
            RefertoCourtDobj dobj = new RefertoCourtDobj();
            setCourtlist(impl.getCourtMaster());
            dobj.setHearingDate(hearingDate);
            dobj.setOpdate(new Date());
            ReferToCourtImpl referimpl = new ReferToCourtImpl();
            setListOfPendingCase(referimpl.getPendingCaseDetails());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void reset() {
        setSelectRow(false);
        setCourtName("-1");
        setHearingDate(new Date());
        JSFUtils.showMessage("");

    }

    public void saveRefferedChallanToCourt() {

        RefertoCourtDobj refertocourtDobj = new RefertoCourtDobj();
        ReferToCourtImpl impl = new ReferToCourtImpl();
        if (courtName.equals("-1") || courtName == null) {

            JSFUtils.showMessage("Please Select Court Name .");
            flag = false;
        } else {
            refertocourtDobj.setCourtNAme(getCourtName());
        }
        if (hearingDate == null) {
            JSFUtils.showMessage("Please Enter The Hearing Date .");
            flag = false;
        } else {
            refertocourtDobj.setHearingDate(getHearingDate());
        }

        for (RefertoCourtDobj dobj : getListOfPendingCase()) {
            if (dobj.isSelectRow()) {
                dobj.setCourtNAme(getCourtName());
                dobj.setHearingDate(getHearingDate());
                selectedPendingCases.add(dobj);
                refertocourtDobj = dobj;
            }
        }
        try {

            boolean savependingcases = impl.saveChallanData(selectedPendingCases, refertocourtDobj);
            if (savependingcases) {
                JSFUtils.showMessage("Data Saved Successfully");
                setListOfPendingCase(impl.getPendingCaseDetails());
                selectedPendingCases.clear();
                Init();

            } else {
                JSFUtils.showMessage(" There Is Some Problem To Save The Data");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void getreferToCourtCases() {
        ReferToCourtImpl impl = new ReferToCourtImpl();
        listOfCourtCases = impl.getAllreferToCourtCases();
        //RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('abc').show()");
    }

    public void getRowDataForPrintCourtCertificate(CourtCasesDobj dobj) {
        try {

            ReferToCourtImpl impl = new ReferToCourtImpl();
            irDobj = impl.getCourtDetailsForIr(dobj);
            irDobj.setRecieptHeading(ServerUtil.getRcptHeading());
            irDobj.setRecieptSubHeading(ServerUtil.getRcptSubHeading());
            if (irDobj != null) {
                FacesContext.getCurrentInstance().getExternalContext().redirect(JSFUtils.getIpPath() + "/vahan/ui/reports/formInformationViolationReport.xhtml?faces-redirect=true");
            } else {
                JSFUtils.setFacesMessage("Unable to Print Information Violation Report. Try Agter SOme Time", "message", JSFUtils.ERROR);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);

        }
    }

    public String getApplicationno() {
        return applicationno;
    }

    public void setApplicationno(String applicationno) {
        this.applicationno = applicationno;
    }

    public Date getOpdate() {
        return opdate;
    }

    public void setOpdate(Date opdate) {
        this.opdate = opdate;
    }

    public List<RefertoCourtDobj> getListOfPendingCase() {
        return listOfPendingCase;
    }

    public void setListOfPendingCase(List<RefertoCourtDobj> listOfPendingCase) {
        this.listOfPendingCase = listOfPendingCase;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isSaveButtonDisabled() {
        return saveButtonDisabled;
    }

    public void setSaveButtonDisabled(boolean saveButtonDisabled) {
        this.saveButtonDisabled = saveButtonDisabled;
    }

    public boolean isSelectRow() {
        return SelectRow;
    }

    public void setSelectRow(boolean SelectRow) {
        this.SelectRow = SelectRow;
    }

    public String getChallanNo() {
        return challanNo;
    }

    public void setChallanNo(String challanNo) {
        this.challanNo = challanNo;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public Date getChallanDate() {
        return challanDate;
    }

    public void setChallanDate(Date challanDate) {
        this.challanDate = challanDate;
    }

    public Date getHearingDate() {
        return hearingDate;
    }

    public void setHearingDate(Date hearingDate) {
        this.hearingDate = hearingDate;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public boolean isPendingCase() {
        return pendingCase;
    }

    public void setPendingCase(boolean pendingCase) {
        this.pendingCase = pendingCase;
    }

    public boolean isVehicleDetailPenal() {
        return vehicleDetailPenal;
    }

    public void setVehicleDetailPenal(boolean vehicleDetailPenal) {
        this.vehicleDetailPenal = vehicleDetailPenal;
    }

    public boolean isChallanDetailPenal() {
        return challanDetailPenal;
    }

    public void setChallanDetailPenal(boolean challanDetailPenal) {
        this.challanDetailPenal = challanDetailPenal;
    }

    public boolean isPendingCasePenal() {
        return pendingCasePenal;
    }

    public void setPendingCasePenal(boolean pendingCasePenal) {
        this.pendingCasePenal = pendingCasePenal;
    }

    public List getCourtlist() {
        return courtlist;
    }

    public void setCourtlist(List courtlist) {
        this.courtlist = courtlist;
    }

    public List getSelectedPendingCases() {
        return selectedPendingCases;
    }

    public void setSelectedPendingCases(List selectedPendingCases) {
        this.selectedPendingCases = selectedPendingCases;
    }

    public List getCourtcode() {
        return courtcode;
    }

    public void setCourtcode(List courtcode) {
        this.courtcode = courtcode;
    }

    public List<CourtCasesDobj> getListOfCourtCases() {
        return listOfCourtCases;
    }

    public void setListOfCourtCases(List<CourtCasesDobj> listOfCourtCases) {
        this.listOfCourtCases = listOfCourtCases;
    }

    public InformationViolationReportDobj getIrDobj() {
        return irDobj;
    }

    public void setIrDobj(InformationViolationReportDobj irDobj) {
        this.irDobj = irDobj;
    }

    public InformationViolationReportDobj getDobjValue() {
        return dobjValue;
    }

    public void setDobjValue(InformationViolationReportDobj dobjValue) {
        this.dobjValue = dobjValue;
    }
}
