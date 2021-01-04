/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.eChallan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.eChallan.ChallanConfigrationDobj;
import nic.vahan.form.dobj.eChallan.ChallanReferToDobj;
import nic.vahan.form.dobj.eChallan.OffenceReferDetailsDobj;
import nic.vahan.form.dobj.eChallan.OffencesDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.eChallan.ChallanReferToImpl;
import nic.vahan.form.impl.eChallan.ChallanUtil;
import nic.vahan.form.impl.eChallan.SaveChallanImpl;
import org.apache.log4j.Logger;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author nicsi
 */
@ManagedBean(name = "challanRefer")
@ViewScoped
public class ChallanReferToBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(ChallanReferToBean.class);
    OffencesDobj dobjOffence = null;
    private String applicationNo;
    private String OffenceCode;
    private String sectionCode;
    private String challnReferTo;
    private String penalty;
    private String accusedDescr;
    private String courtCode;
    private String authorityCode;
    private Date hearingDate;
    private boolean hearingdate;
    Map<String, Object> applicationNoList;
    Map<String, Object> OffenceList;
    Map<String, Object> sectionList;
    Map<String, Object> authorityList;
    private Date maxDate = new Date();
    private Date minDate = new Date();
    private List courtList = new ArrayList();
    private List<OffenceReferDetailsDobj> challanReferDetailList = new ArrayList();
    ChallanReferToImpl impl = new ChallanReferToImpl();
    boolean referToCourtDisabled;
    boolean referToAuthorityDisabled;
    private boolean printButtonenable;
    ChallanReferToDobj dobjAppLNo = new ChallanReferToDobj();
    SaveChallanImpl saveImpl = new SaveChallanImpl();
    private List magistrateList = new ArrayList();
    private String magistrate;
    private boolean isMagistrateExist;
    private boolean showMagistrateSom;
    private boolean showSavePenal;
    private boolean showPrintPenal;
    private String vahanMessages = null;

    public ChallanReferToBean() {
        Exception exception = null;
        try {
            applicationNoList = impl.getChallanApplicationNoList();
            showPrintPenal = false;
            showSavePenal = false;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            exception = e;
        }
        if (exception != null) {
            LOGGER.error(exception.toString() + " " + exception.getStackTrace()[0]);
            vahanMessages = exception.getMessage();
        }
    }

    public void getChallanWiseOffenceList() {
        ChallanConfigrationDobj config_dobj = null;
        ChallanUtil challanUtil = new ChallanUtil();
        dobjOffence = null;
        setSectionList(null);
        setOffenceList(null);
        setCourtList(null);
        setAuthorityList(null);
        if ("-1".equalsIgnoreCase(challnReferTo)) {
            setReferToAuthorityDisabled(false);
            setReferToCourtDisabled(false);
            JSFUtils.setFacesMessage("Please Select Where U Want TO Send The Challan", "message", JSFUtils.WARN);
            return;
        } else if ("1".equalsIgnoreCase(challnReferTo)) {
            setReferToAuthorityDisabled(false);
            setReferToCourtDisabled(true);
        } else if ("2".equalsIgnoreCase(challnReferTo)) {
            setReferToAuthorityDisabled(true);
            setReferToCourtDisabled(true);
        } else if ("3".equalsIgnoreCase(challnReferTo)) {
            setReferToAuthorityDisabled(true);
            setReferToCourtDisabled(false);
        }
        try {

            OffenceList = impl.getOffenceOfChallan(applicationNo);
            if (OffenceList == null) {
                setReferToCourtDisabled(false);
                throw new VahanException("Record Not Found Please Enter Valid Application No");
            }
            courtList = MasterTableFiller.getCourtList(Util.getUserStateCode());
            authorityList = impl.getAuthorityList(Util.getUserStateCode());
            config_dobj = challanUtil.getChallanConfigration(Util.getUserStateCode());
            showSavePenal = true;
            if (config_dobj.isIsmagistrate_exist()) {
                magistrateList = MasterTableFiller.getMagistrateList(Util.getUserStateCode());
                setShowMagistrateSom(true);
            }


        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }

    }

    public void getOffenceWiseSection() {
        try {
            sectionList = impl.getSectionOffenceWise(getOffenceCode(), applicationNo);
            dobjOffence = impl.getOffenceDetails(getOffenceCode(), applicationNo);
            if (dobjOffence != null) {
                setAccusedDescr(dobjOffence.getAccusedDescr());
                setPenalty(dobjOffence.getPenalty());
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong);
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
    }

    public void offenceDetailsForRefer() {
        String authertyName = "";
        String OffenceName = "";
        String sectionName = "";
        if (authorityCode.equals("-1") || authorityCode == null) {
            JSFUtils.setFacesMessage("Please Select Authority", "messages", JSFUtils.FATAL);
            return;
        }
        if (OffenceCode.equals("-1") || OffenceCode == null) {
            JSFUtils.setFacesMessage("Please SelectOffence ", "messages", JSFUtils.FATAL);
            return;
        }
        if ("".equals(accusedDescr) || accusedDescr == null) {
            JSFUtils.setFacesMessage("Please Enter Accused ", "messages", JSFUtils.FATAL);
            return;
        }
        if (penalty.equals("") || penalty == null) {
            JSFUtils.setFacesMessage("Please Enter The Penalty", "messages", JSFUtils.FATAL);
            return;
        }
        for (OffenceReferDetailsDobj dobj : challanReferDetailList) {
            String auth_cd = dobj.getAuthorityCode();
            String offence_cd = dobj.getOffenceCode();
            if (getOffenceCode().endsWith(offence_cd)) {
                JSFUtils.setFacesMessage("Offence  already Sent To Authority", "message", JSFUtils.ERROR);
                return;
            }
        }
        for (Object key : authorityList.keySet()) {
            if (authorityList.get(key).toString().equals(authorityCode.toString())) {
                authertyName = key.toString();
                break;
            }
        }
        for (Object key : OffenceList.keySet()) {
            if (OffenceList.get(key).toString().equals(OffenceCode.toString())) {
                OffenceName = key.toString();
                break;
            }
        }
        for (Object key : sectionList.keySet()) {
            if (sectionList.get(key).toString().equals(sectionCode.toString())) {
                sectionName = key.toString();
                break;
            }
        }
        OffenceReferDetailsDobj dobj = new OffenceReferDetailsDobj(OffenceCode, sectionCode, OffenceName, sectionName, authorityCode, authertyName, penalty, accusedDescr);
        challanReferDetailList.add(dobj);
        setAuthorityCode("-1");
        setOffenceCode("-1");
        setSectionCode("-1");
        setPenalty("");
        setAccusedDescr("");
    }

    public void editChallanRefrenceDetails(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Offnce Refer Detail Updated ", ((OffenceReferDetailsDobj) event.getObject()).getAuthorityCode());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void deleteChallanRefrenceDetails(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Offnce Refer Detail Deleted");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        challanReferDetailList.remove((OffenceReferDetailsDobj) event.getObject());
    }

    public void getSelectedValues(String name, String value) {
        try {
            if ("court".equalsIgnoreCase(name)) {
                setMagistrate(saveImpl.getSelectedMagistrate("court_cd", value));
            }
            if ("magistrate".equalsIgnoreCase(name)) {
                setCourtCode(saveImpl.getSelectedMagistrate("magistrate_cd", value));
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

    public void saveChallanReferDetais() {
        ChallanReferToDobj dobj = new ChallanReferToDobj();
        if ("-1".equalsIgnoreCase(challnReferTo)) {
            JSFUtils.setFacesMessage("Please Select Where U Want TO Send The Challan", "message", JSFUtils.WARN);
            return;
        }
        if (applicationNo.isEmpty() || applicationNo == null) {
            JSFUtils.setFacesMessage("Select Application NO", "message", JSFUtils.WARN);
            return;
        } else {
            dobj.setAppl_no(getApplicationNo());
        }

        if ("1".equalsIgnoreCase(challnReferTo) || "2".equalsIgnoreCase(challnReferTo)) {
            if ("-1".equalsIgnoreCase(courtCode)) {
                JSFUtils.setFacesMessage("Please Select The Court", "Message", JSFUtils.INFO);
                return;
            } else {
                dobj.setCourtCode(getCourtCode());
            }
            if ("OR".equalsIgnoreCase(Util.getUserStateCode())) {
                hearingdate = false;
            } else {
                hearingdate = true;
                if (hearingDate == null) {
                    JSFUtils.setFacesMessage("Please Select Hearing Date", "Message", JSFUtils.INFO);
                    return;
                } else {
                    dobj.setHearingDate(getHearingDate());
                }
            }
        }
        if ("2".equalsIgnoreCase(challnReferTo) || "3".equalsIgnoreCase(challnReferTo)) {
            if (challanReferDetailList.isEmpty() || challanReferDetailList == null) {
                JSFUtils.setFacesMessage("Please Enter The Offence Refer Details Or Select the Other Option", "Message", JSFUtils.INFO);
                return;
            }
        }
        try {

            boolean saveStatus = impl.saveReferedOffencesOrChallan(dobj, challanReferDetailList);
            if (saveStatus) {
                dobjAppLNo.setAppl_no(dobj.getAppl_no());
                setPrintButtonenable(true);
                JSFUtils.setFacesMessage("Challan/Offence Successfully Refered", "Message", JSFUtils.INFO);
                applicationNoList = impl.getChallanApplicationNoList();
                reset();
                showPrintPenal = true;
                showSavePenal = false;

            } else {
                JSFUtils.setFacesMessage("There is Some Problem To Refer Challan/Offence ", "Message", JSFUtils.INFO);
                showPrintPenal = false;
                showSavePenal = true;
            }
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);

        }
    }

    public void printReport() {
        try {

            HttpSession session = Util.getSession();
            session.setAttribute("appl_no", dobjAppLNo.getAppl_no());
            if ("OR".equalsIgnoreCase(Util.getUserStateCode())) {
                FacesContext.getCurrentInstance().getExternalContext().redirect(JSFUtils.getIpPath() + "/vahan/ui/reports/formChallanProsecutionReports.xhtml?faces-redirect=true");
            } else {
                FacesContext.getCurrentInstance().getExternalContext().redirect(JSFUtils.getIpPath() + "/vahan/ui/reports/formInformationViolationReport.xhtml?faces-redirect=true");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void reset() {
        challanReferDetailList.clear();
        setChallnReferTo("-1");
        setAuthorityCode("-1");
        setOffenceCode("-1");
        setSectionCode("-1");
        setAccusedDescr("");
        setPenalty("");
        setApplicationNo(null);
        setHearingDate(new Date());
        setCourtCode("-1");
        setSectionList(null);

    }

    public Map<String, Object> getApplicationNoList() {
        return applicationNoList;
    }

    public void setApplicationNoList(Map<String, Object> applicationNoList) {
        this.applicationNoList = applicationNoList;
    }

    public Map<String, Object> getOffenceList() {
        return OffenceList;
    }

    public void setOffenceList(Map<String, Object> OffenceList) {
        this.OffenceList = OffenceList;
    }

    public Map<String, Object> getSectionList() {
        return sectionList;
    }

    public void setSectionList(Map<String, Object> sectionList) {
        this.sectionList = sectionList;
    }

    public String getOffenceCode() {
        return OffenceCode;
    }

    public void setOffenceCode(String OffenceCode) {
        this.OffenceCode = OffenceCode;
    }

    public List getCourtList() {
        return courtList;
    }

    public void setCourtList(List courtList) {
        this.courtList = courtList;
    }

    public String getCourtCode() {
        return courtCode;
    }

    public void setCourtCode(String courtCode) {
        this.courtCode = courtCode;
    }

    public Date getHearingDate() {
        return hearingDate;
    }

    public void setHearingDate(Date hearingDate) {
        this.hearingDate = hearingDate;
    }

    public String getAuthorityCode() {
        return authorityCode;
    }

    public void setAuthorityCode(String authorityCode) {
        this.authorityCode = authorityCode;
    }

    public Map<String, Object> getAuthorityList() {
        return authorityList;
    }

    public void setAuthorityList(Map<String, Object> authorityList) {
        this.authorityList = authorityList;
    }

    public boolean isReferToCourtDisabled() {
        return referToCourtDisabled;
    }

    public void setReferToCourtDisabled(boolean referToCourtDisabled) {
        this.referToCourtDisabled = referToCourtDisabled;
    }

    public boolean isReferToAuthorityDisabled() {
        return referToAuthorityDisabled;
    }

    public void setReferToAuthorityDisabled(boolean referToAuthorityDisabled) {
        this.referToAuthorityDisabled = referToAuthorityDisabled;
    }

    public String getChallnReferTo() {
        return challnReferTo;
    }

    public void setChallnReferTo(String challnReferTo) {
        this.challnReferTo = challnReferTo;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public OffencesDobj getDobjOffence() {
        return dobjOffence;
    }

    public void setDobjOffence(OffencesDobj dobjOffence) {
        this.dobjOffence = dobjOffence;
    }

    public String getPenalty() {
        return penalty;
    }

    public void setPenalty(String penalty) {
        this.penalty = penalty;
    }

    public String getAccusedDescr() {
        return accusedDescr;
    }

    public void setAccusedDescr(String accusedDescr) {
        this.accusedDescr = accusedDescr;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public void setSectionCode(String sectionCode) {
        this.sectionCode = sectionCode;
    }

    public List<OffenceReferDetailsDobj> getChallanReferDetailList() {
        return challanReferDetailList;
    }

    public void setChallanReferDetailList(List<OffenceReferDetailsDobj> challanReferDetailList) {
        this.challanReferDetailList = challanReferDetailList;
    }

    /**
     * @return the printButtonenable
     */
    public boolean isPrintButtonenable() {
        return printButtonenable;
    }

    /**
     * @param printButtonenable the printButtonenable to set
     */
    public void setPrintButtonenable(boolean printButtonenable) {
        this.printButtonenable = printButtonenable;
    }

    public List getMagistrateList() {
        return magistrateList;
    }

    public void setMagistrateList(List magistrateList) {
        this.magistrateList = magistrateList;
    }

    public String getMagistrate() {
        return magistrate;
    }

    public void setMagistrate(String magistrate) {
        this.magistrate = magistrate;
    }

    public boolean isIsMagistrateExist() {
        return isMagistrateExist;
    }

    public void setIsMagistrateExist(boolean isMagistrateExist) {
        this.isMagistrateExist = isMagistrateExist;
    }

    public boolean isShowMagistrateSom() {
        return showMagistrateSom;
    }

    public void setShowMagistrateSom(boolean showMagistrateSom) {
        this.showMagistrateSom = showMagistrateSom;
    }

    public String getVahanMessages() {
        return vahanMessages;
    }

    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }

    public boolean isShowSavePenal() {
        return showSavePenal;
    }

    public void setShowSavePenal(boolean showSavePenal) {
        this.showSavePenal = showSavePenal;
    }

    public boolean isShowPrintPenal() {
        return showPrintPenal;
    }

    public void setShowPrintPenal(boolean showPrintPenal) {
        this.showPrintPenal = showPrintPenal;
    }

    public boolean isHearingdate() {
        return hearingdate;
    }

    public void setHearingdate(boolean hearingdate) {
        this.hearingdate = hearingdate;
    }

    public ChallanReferToDobj getDobjAppLNo() {
        return dobjAppLNo;
    }

    public void setDobjAppLNo(ChallanReferToDobj dobjAppLNo) {
        this.dobjAppLNo = dobjAppLNo;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }
}
