/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.tradecert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.tradecert.DeduplicateTradeCertDobj;
import nic.vahan.form.dobj.tradecert.MergeDeDuplPrintDobj;
import nic.vahan.form.dobj.tradecert.MergeTradeCertDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.tradecert.MergeDeDuplPrintImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nicsi
 */
@ManagedBean(name = "printMergeDeDuplBean")
@RequestScoped
public class PrintMergeDeDuplBean {

    private static final Logger LOGGER = Logger.getLogger(PrintMergeDeDuplBean.class);
    private String mergeOrDeDupl;
    private String actionTaken;
    private String dealerCd;
    private String vehCatg;
    private String dealerChoiceCondition;
    private String tcNumber;
    private String outcome;
    private String currentDate;
    private String userName;
    private String officeName;
    private String qrText;
    private List dobjsList;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy h:mm:ss a");
    private String reportHeading;
    private String reportSubHeading;
    private SessionVariables sessionVariables;
    private TmConfigurationDobj tmConfigDobj;
    private MergeDeDuplPrintDobj mergeDeDuplPrintDobj;

    public PrintMergeDeDuplBean() {
        mergeDeDuplPrintDobj = new MergeDeDuplPrintDobj();
    }

    @PostConstruct
    public void init() {

        setMergeOrDeDupl((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("mergeOrDeDupl"));

        if (getMergeOrDeDupl() != null && "MERGE".equals(getMergeOrDeDupl())) {
            setOutcome("/ui/tradecert/form_trade_cert_merge.xhtml?faces-redirect=true");
        } else if (getMergeOrDeDupl() != null && "DEDUPL".equals(getMergeOrDeDupl())) {
            setOutcome("/ui/tradecert/form_trade_cert_deduplicate.xhtml?faces-redirect=true");
        } else if (getMergeOrDeDupl() == null) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Print generation status...", "Details of records updated cannot printed as essential attributes are not set."));
            return;
        }
        setActionTaken((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("actionTaken"));
        setDealerCd((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("dealerCd"));
        setDealerChoiceCondition((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("dealerChoiceCondition"));
        if ("MERGE".equals(getMergeOrDeDupl())) {
            setVehCatg((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("vehCatg"));
        } else {
            setTcNumber((String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("tcNumber"));
        }
        Object dobj = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("dobj");
        if (dobj instanceof MergeTradeCertDobj) {
            setDobjsList((List<MergeTradeCertDobj>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("tcDobjList"));
            MergeTradeCertDobj mergeTradeCertDobj = (MergeTradeCertDobj) dobj;
            getMergeDeDuplPrintDobj().setDealerName(mergeTradeCertDobj.getDealerName());
            getMergeDeDuplPrintDobj().setVehCatgName(mergeTradeCertDobj.getVehCatgName());
            if ("UPDATE".equals(getActionTaken())) {
                getMergeDeDuplPrintDobj().setValidityPeriod("(" + mergeTradeCertDobj.getValidFromStr() + " - " + mergeTradeCertDobj.getValidUptoStr() + ")");
                getMergeDeDuplPrintDobj().setNoOfVeh(mergeTradeCertDobj.getNoOfVeh());
            }
        } else if (dobj instanceof DeduplicateTradeCertDobj) {
            setDobjsList((List<DeduplicateTradeCertDobj>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("deduplicatedTcDobjList"));
            DeduplicateTradeCertDobj deDuplicateTradeCertDobj = (DeduplicateTradeCertDobj) dobj;
            getMergeDeDuplPrintDobj().setDealerName(deDuplicateTradeCertDobj.getDealerName());
            getMergeDeDuplPrintDobj().setTcNumber(deDuplicateTradeCertDobj.getCertNo());
        }
        setSessionVariables(new SessionVariables());
        setCurrentDate(getSdf().format(new Date()));
        setUserName(Util.getUserName());
        setOfficeName(ServerUtil.getOfficeName(sessionVariables.getOffCodeSelected(), sessionVariables.getStateCodeSelected()));
        setReportHeading(ServerUtil.getRcptHeading());
        setReportSubHeading(ServerUtil.getRcptSubHeading());
        try {
            setTmConfigDobj(Util.getTmConfiguration());
        } catch (VahanException ex) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            return;
        }

        if (getMergeDeDuplPrintDobj() != null && getMergeDeDuplPrintDobj().getDealerName() != null) {
            String qrText = " State:" + Util.getUserStateCode()
                    + ", Office: " + Util.getSelectedSeat().getOff_name()
                    + ", Form Name:" + getMergeOrDeDupl()
                    + ", Action Taken:" + getActionTaken()
                    + ", Dealer: " + getMergeDeDuplPrintDobj().getDealerName()
                    + ", Dealer Code: " + getDealerCd()
                    + ", Dealer Choice Condition: " + getDealerChoiceCondition();
            if ("MERGE".equals(getMergeOrDeDupl())) {
                qrText += ", Vehicle Category Name: " + getMergeDeDuplPrintDobj().getVehCatgName();
                if ("UPDATE".equals(getActionTaken())) {
                    qrText += ", Validity Period: " + getMergeDeDuplPrintDobj().getValidityPeriod()
                            + ", Number Of Vehicles: " + getMergeDeDuplPrintDobj().getNoOfVeh();
                }
            } else if ("DEDUPL".equals(getMergeOrDeDupl())) {
                qrText += ", T.C Number: " + getMergeDeDuplPrintDobj().getTcNumber();
            }
            setQrText(qrText);
        }

        try {
            Set uniqueApplNoSet = new HashSet();
            if ("MERGE".equals(getMergeOrDeDupl())) {
                if ("UPDATE".equals(getActionTaken())) { /// MERGE:UPDATE
                    getMergeDeDuplPrintDobj().setVhTradeCertDobjSubList(MergeDeDuplPrintImpl.fetchVhTradeCertRecords((MergeTradeCertDobj) getDobjsList().get(0), getMergeOrDeDupl(), getActionTaken()));
                    for (MergeTradeCertDobj dobjFromDobjsList : (List<MergeTradeCertDobj>) getDobjsList()) {
                        if (uniqueApplNoSet.contains(dobjFromDobjsList.getApplNo())) {
                            continue;
                        }
                        getMergeDeDuplPrintDobj().getVtTradeCertDobjSubList().addAll(MergeDeDuplPrintImpl.fetchVtTradeCertRecords(dobjFromDobjsList, getMergeOrDeDupl(), getActionTaken()));
                        uniqueApplNoSet.add(dobjFromDobjsList);
                    }
                } else { //// MERGE:DELETE
                    for (MergeTradeCertDobj dobjFromDobjsList : (List<MergeTradeCertDobj>) getDobjsList()) {
                        if (uniqueApplNoSet.contains(dobjFromDobjsList.getApplNo())) {
                            continue;
                        }
                        getMergeDeDuplPrintDobj().getVhTradeCertDobjSubList().addAll(MergeDeDuplPrintImpl.fetchVhTradeCertRecords(dobjFromDobjsList, getMergeOrDeDupl(), getActionTaken()));
                        uniqueApplNoSet.add(dobjFromDobjsList);
                    }
                }
            } else { //// DEDUPLICATE
                for (DeduplicateTradeCertDobj dobjFromDobjsList : (List<DeduplicateTradeCertDobj>) getDobjsList()) {
                    if (uniqueApplNoSet.contains(dobjFromDobjsList.getApplNo())) {
                        continue;
                    }
                    getMergeDeDuplPrintDobj().getVhTradeCertDobjSubList().addAll(MergeDeDuplPrintImpl.fetchVhTradeCertRecords(dobjFromDobjsList, getMergeOrDeDupl(), getActionTaken()));
                    uniqueApplNoSet.add(dobjFromDobjsList);
                }
                if ("UPDATE".equals(getActionTaken())) { /// DEDUPLICATE:UPDATE
                    getMergeDeDuplPrintDobj().getVtTradeCertDobjSubList().addAll(MergeDeDuplPrintImpl.fetchVtTradeCertRecords(((DeduplicateTradeCertDobj) getDobjsList().get(0)), getMergeOrDeDupl(), getActionTaken()));
                }
            }


        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Print generation status...", "Details of records updated cannot printed as " + ve.getMessage()));
            LOGGER.error("Details of records updated cannot printed as " + ve.getMessage());
            LOGGER.error(ve.toString() + " " + ve.getStackTrace()[0]);
        }

    }

    /**
     * @return the mergeOrDeDupl
     */
    public String getMergeOrDeDupl() {
        return mergeOrDeDupl;
    }

    /**
     * @param mergeOrDeDupl the mergeOrDeDupl to set
     */
    public void setMergeOrDeDupl(String mergeOrDeDupl) {
        this.mergeOrDeDupl = mergeOrDeDupl;
    }

    /**
     * @return the outcome
     */
    public String getOutcome() {
        return outcome;
    }

    /**
     * @param outcome the outcome to set
     */
    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    /**
     * @return the actionTaken
     */
    public String getActionTaken() {
        return actionTaken;
    }

    /**
     * @param actionTaken the actionTaken to set
     */
    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    /**
     * @return the dealerCd
     */
    public String getDealerCd() {
        return dealerCd;
    }

    /**
     * @param dealerCd the dealerCd to set
     */
    public void setDealerCd(String dealerCd) {
        this.dealerCd = dealerCd;
    }

    /**
     * @return the vehCatg
     */
    public String getVehCatg() {
        return vehCatg;
    }

    /**
     * @param vehCatg the vehCatg to set
     */
    public void setVehCatg(String vehCatg) {
        this.vehCatg = vehCatg;
    }

    /**
     * @return the dealerChoiceCondition
     */
    public String getDealerChoiceCondition() {
        return dealerChoiceCondition;
    }

    /**
     * @param dealerChoiceCondition the dealerChoiceCondition to set
     */
    public void setDealerChoiceCondition(String dealerChoiceCondition) {
        this.dealerChoiceCondition = dealerChoiceCondition;
    }

    /**
     * @return the tcNumber
     */
    public String getTcNumber() {
        return tcNumber;
    }

    /**
     * @param tcNumber the tcNumber to set
     */
    public void setTcNumber(String tcNumber) {
        this.tcNumber = tcNumber;
    }

    /**
     * @return the currentDate
     */
    public String getCurrentDate() {
        return currentDate;
    }

    /**
     * @param currentDate the currentDate to set
     */
    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    /**
     * @return the sdf
     */
    public SimpleDateFormat getSdf() {
        return sdf;
    }

    /**
     * @param sdf the sdf to set
     */
    public void setSdf(SimpleDateFormat sdf) {
        this.sdf = sdf;
    }

    /**
     * @return the reportHeading
     */
    public String getReportHeading() {
        return reportHeading;
    }

    /**
     * @param reportHeading the reportHeading to set
     */
    public void setReportHeading(String reportHeading) {
        this.reportHeading = reportHeading;
    }

    /**
     * @return the reportSubHeading
     */
    public String getReportSubHeading() {
        return reportSubHeading;
    }

    /**
     * @param reportSubHeading the reportSubHeading to set
     */
    public void setReportSubHeading(String reportSubHeading) {
        this.reportSubHeading = reportSubHeading;
    }

    /**
     * @return the sessionVariables
     */
    public SessionVariables getSessionVariables() {
        return sessionVariables;
    }

    /**
     * @param sessionVariables the sessionVariables to set
     */
    public void setSessionVariables(SessionVariables sessionVariables) {
        this.sessionVariables = sessionVariables;
    }

    /**
     * @return the tmConfigDobj
     */
    public TmConfigurationDobj getTmConfigDobj() {
        return tmConfigDobj;
    }

    /**
     * @param tmConfigDobj the tmConfigDobj to set
     */
    public void setTmConfigDobj(TmConfigurationDobj tmConfigDobj) {
        this.tmConfigDobj = tmConfigDobj;
    }

    /**
     * @return the mergeDeDuplPrintDobj
     */
    public MergeDeDuplPrintDobj getMergeDeDuplPrintDobj() {
        return mergeDeDuplPrintDobj;
    }

    /**
     * @param mergeDeDuplPrintDobj the mergeDeDuplPrintDobj to set
     */
    public void setMergeDeDuplPrintDobj(MergeDeDuplPrintDobj mergeDeDuplPrintDobj) {
        this.mergeDeDuplPrintDobj = mergeDeDuplPrintDobj;
    }

    /**
     * @return the officeName
     */
    public String getOfficeName() {
        return officeName;
    }

    /**
     * @param officeName the officeName to set
     */
    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    /**
     * @return the dobjsList
     */
    public List getDobjsList() {
        return dobjsList;
    }

    /**
     * @param dobjsList the dobjsList to set
     */
    public void setDobjsList(List dobjsList) {
        this.dobjsList = dobjsList;
    }

    /**
     * @return the qrText
     */
    public String getQrText() {
        return qrText;
    }

    /**
     * @param qrText the qrText to set
     */
    public void setQrText(String qrText) {
        this.qrText = qrText;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
