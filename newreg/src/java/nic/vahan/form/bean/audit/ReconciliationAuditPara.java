/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.audit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.java.util.DateUtils;
import nic.java.util.DateUtilsException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.AuditRecoveryDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.audit.EntryAuditRecoveryImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Kunal Maiti
 */
@ManagedBean(name = "reconciliationAuditPara")
@ViewScoped
public class ReconciliationAuditPara implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ReconciliationAuditPara.class);
    private String regn_no;
    private boolean showRegList = false;
    private boolean showAuditRecoveryDetails = false;
    private boolean showReconcilitionDtls = false;
    private ArrayList<OwnerDetailsDobj> regnNameList = null;
    private List listOwnerCatg = new ArrayList();
    private OwnerDetailsDobj ownerDetail;
    private String paraNo;
    private String year;
    private int amount;
    private String objection;
    private Date periodFrom;
    private Date periodTo;
    private String auditType;
    private EntryAuditRecoveryImpl entryAuditRecoveryImpl = new EntryAuditRecoveryImpl();
    private AuditRecoveryDobj auditRecoveryDobj;
    private String reconcilationChange;
    private boolean audutTyDisabled = false;
    private boolean paraNoDisabled = false;
    private boolean yearDisabled = false;
    private boolean regnNoDisabled = false;
    private boolean showDtlsBtnDisabled = false;

    @PostConstruct
    public void Init() {
        try {
//            fillmastercombo();
//            disabledatatablepanels();
            String user_catg = ServerUtil.getUserCategory(Long.parseLong(Util.getEmpCode()));
            boolean checkUserPerm = false;
            if (user_catg.equals("S") || (user_catg.equals("A"))) {
                checkUserPerm = true;
            }
            if (checkUserPerm == false) {
//                combofilllist.clear();
                JSFUtils.showMessage("YOU ARE NOT ALLOWED TO ENTER DATA IN MASTER FORM");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void saveDetails() {

        Date currentdate = new Date();

        if (reconcilationChange.equalsIgnoreCase("1")) {
            if (this.periodFrom == null) {
                JSFUtils.showMessage("Please Select period from");
                return;
            }
            if (this.periodTo == null) {
                JSFUtils.showMessage("Please Select period To");
                return;
            }

            AuditRecoveryDobj auditRecoveryDobj = new AuditRecoveryDobj();

            auditRecoveryDobj.setAmount(amount);
            auditRecoveryDobj.setFrom_dt(periodFrom);
            auditRecoveryDobj.setObjection(objection);
            auditRecoveryDobj.setOp_date(currentdate);
            auditRecoveryDobj.setTo_dt(periodTo);
            auditRecoveryDobj.setReconcil_flag("R");
            auditRecoveryDobj.setDeal_cd(Util.getEmpCode());


            int val = EntryAuditRecoveryImpl.saveReconciliationDtls(getAuditRecoveryDobj(), auditRecoveryDobj);
            if (val == 1) {
                reset();
                JSFUtils.showMessage("Reconcilation Audit details saved successfully");
            } else {
                JSFUtils.showMessage("Reconcilation Audit details saved fail");
            }
        } else if (reconcilationChange.equalsIgnoreCase("2")) {
            AuditRecoveryDobj auditRecoveryDobj = new AuditRecoveryDobj();

            auditRecoveryDobj.setAmount(0);
            auditRecoveryDobj.setFrom_dt(null);
            auditRecoveryDobj.setObjection(objection);
            auditRecoveryDobj.setOp_date(currentdate);
            auditRecoveryDobj.setTo_dt(null);
            auditRecoveryDobj.setReconcil_flag("D");
            auditRecoveryDobj.setDeal_cd(Util.getEmpCode());
            int val = EntryAuditRecoveryImpl.dropReconciliationDtls(getAuditRecoveryDobj(), auditRecoveryDobj);
            if (val == 1) {
                reset();
                JSFUtils.showMessage("Reconcilation Audit details delete successfully");
            } else {
                JSFUtils.showMessage("Reconcilation Audit details deleted fail");
            }
        } else if (reconcilationChange.equalsIgnoreCase("3")) {
            AuditRecoveryDobj auditRecoveryDobj = new AuditRecoveryDobj();

            auditRecoveryDobj.setAmount(0);
            auditRecoveryDobj.setFrom_dt(null);
            auditRecoveryDobj.setObjection(objection);
            auditRecoveryDobj.setOp_date(currentdate);
            auditRecoveryDobj.setTo_dt(null);
            auditRecoveryDobj.setReconcil_flag("S");
            auditRecoveryDobj.setDeal_cd(Util.getEmpCode());
            int val = EntryAuditRecoveryImpl.stayReconciliationDtls(getAuditRecoveryDobj(), auditRecoveryDobj);
            if (val == 1) {
                reset();
                JSFUtils.showMessage("Reconcilation Audit details saved successfully");
            } else {
                JSFUtils.showMessage("Reconcilation Audit details saved fail");
            }
        }
    }

    public void showDetails() {
        Exception ex = null;
        if ("".equals(auditType)) {
            JSFUtils.showMessage("Please Select Audit Type");
            return;
        }
        if (this.paraNo.trim() == null || this.paraNo.trim().equalsIgnoreCase("")) {
            JSFUtils.showMessage("Please Insert Para no");
            return;
        }
        if (this.year.trim() == null || this.year.trim().equalsIgnoreCase("")) {
            JSFUtils.showMessage("Please Insert Year");
            return;
        }
        if (year.length() < 4) {
            JSFUtils.showMessage(" Year should be 4 character length");
            return;
        } else {
            int yearvl = Integer.parseInt(year);
            try {
                int curr_year = DateUtils.getDatePart(DateUtils.parseDate(new Date()), DateUtils.YEAR);
                if (yearvl > curr_year) {
                    JSFUtils.showMessage(" Year should not be grater than Cuurent Year.");
                    return;
                }else if(yearvl<1930){
                    JSFUtils.showMessage(" Year should not be Less than 1930");
                    return;
                }

            } catch (DateUtilsException ex1) {
                java.util.logging.Logger.getLogger(ReconciliationAuditPara.class.getName()).log(Level.SEVERE, null, ex1);
            }

        }
        if (this.regn_no.trim() == null || this.regn_no.trim().equalsIgnoreCase("")) {
            JSFUtils.showMessage("Please Enter Valid Registration Number");
            return;
        }
        try {
            setReconcilationChange("1");
            showReconcilitionDtls = true;
            setAuditRecoveryDobj(entryAuditRecoveryImpl.getvtAuditRecovery(auditType, paraNo, year, regn_no));
            if (getAuditRecoveryDobj().getRegn_no() != null) {
                setOwnerDetail(entryAuditRecoveryImpl.getOwnerDetails(regn_no));
                setShowAuditRecoveryDetails(true);
                audutTyDisabled = true;
                paraNoDisabled = true;
                yearDisabled = true;
                regnNoDisabled = true;
                showDtlsBtnDisabled = true;
                setAmount(getAuditRecoveryDobj().getAmount());
                setPeriodFrom(getAuditRecoveryDobj().getFrom_dt());
                setPeriodTo(getAuditRecoveryDobj().getTo_dt());
            } else {
                JSFUtils.showMessage("Details not found");
            }

        } catch (Exception exp) {
            ex = exp;
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", TableConstants.SomthingWentWrong));
        }
    }

    public void reset() {
        setYear("");
        setAuditType("-1");
        setRegn_no("");
        setRegn_no("");
        setParaNo("");
        showAuditRecoveryDetails = false;
        setReconcilationChange("1");
        setAmount(0);
        setObjection("");
        setPeriodFrom(null);
        setPeriodTo(null);
        audutTyDisabled = false;
        paraNoDisabled = false;
        yearDisabled = false;
        regnNoDisabled = false;
        showDtlsBtnDisabled = false;

    }

    public void changeReconcilationType() {

        if (reconcilationChange.equalsIgnoreCase("1")) {
            setShowReconcilitionDtls(true);
            setAmount(getAuditRecoveryDobj().getAmount());
            setPeriodFrom(getAuditRecoveryDobj().getFrom_dt());
            setPeriodTo(getAuditRecoveryDobj().getTo_dt());
        } else {
            setShowReconcilitionDtls(false);
        }

    }

    /**
     * @return the regn_no
     */
    public String getRegn_no() {
        return regn_no;
    }

    /**
     * @param regn_no the regn_no to set
     */
    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    /**
     * @return the regnNameList
     */
    public ArrayList<OwnerDetailsDobj> getRegnNameList() {
        return regnNameList;
    }

    /**
     * @param regnNameList the regnNameList to set
     */
    public void setRegnNameList(ArrayList<OwnerDetailsDobj> regnNameList) {
        this.regnNameList = regnNameList;
    }

    /**
     * @return the showRegList
     */
    public boolean isShowRegList() {
        return showRegList;
    }

    /**
     * @param showRegList the showRegList to set
     */
    public void setShowRegList(boolean showRegList) {
        this.showRegList = showRegList;
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
     * @return the paraNo
     */
    public String getParaNo() {
        return paraNo;
    }

    /**
     * @param paraNo the paraNo to set
     */
    public void setParaNo(String paraNo) {
        this.paraNo = paraNo;
    }

    /**
     * @return the periodFrom
     */
    public Date getPeriodFrom() {
        return periodFrom;
    }

    /**
     * @param periodFrom the periodFrom to set
     */
    public void setPeriodFrom(Date periodFrom) {
        this.periodFrom = periodFrom;
    }

    /**
     * @return the periodTo
     */
    public Date getPeriodTo() {
        return periodTo;
    }

    /**
     * @param periodTo the periodTo to set
     */
    public void setPeriodTo(Date periodTo) {
        this.periodTo = periodTo;
    }

    /**
     * @return the auditType
     */
    public String getAuditType() {
        return auditType;
    }

    /**
     * @param auditType the auditType to set
     */
    public void setAuditType(String auditType) {
        this.auditType = auditType;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * @return the objection
     */
    public String getObjection() {
        return objection;
    }

    /**
     * @param objection the objection to set
     */
    public void setObjection(String objection) {
        this.objection = objection;
    }

    /**
     * @return the year
     */
    public String getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * @return the auditRecoveryDobj
     */
    public AuditRecoveryDobj getAuditRecoveryDobj() {
        return auditRecoveryDobj;
    }

    /**
     * @param auditRecoveryDobj the auditRecoveryDobj to set
     */
    public void setAuditRecoveryDobj(AuditRecoveryDobj auditRecoveryDobj) {
        this.auditRecoveryDobj = auditRecoveryDobj;
    }

    /**
     * @return the showAuditRecoveryDetails
     */
    public boolean isShowAuditRecoveryDetails() {
        return showAuditRecoveryDetails;
    }

    /**
     * @param showAuditRecoveryDetails the showAuditRecoveryDetails to set
     */
    public void setShowAuditRecoveryDetails(boolean showAuditRecoveryDetails) {
        this.showAuditRecoveryDetails = showAuditRecoveryDetails;
    }

    /**
     * @return the reconcilationChange
     */
    public String getReconcilationChange() {
        return reconcilationChange;
    }

    /**
     * @param reconcilationChange the reconcilationChange to set
     */
    public void setReconcilationChange(String reconcilationChange) {
        this.reconcilationChange = reconcilationChange;
    }

    /**
     * @return the showReconcilitionDtls
     */
    public boolean isShowReconcilitionDtls() {
        return showReconcilitionDtls;
    }

    /**
     * @param showReconcilitionDtls the showReconcilitionDtls to set
     */
    public void setShowReconcilitionDtls(boolean showReconcilitionDtls) {
        this.showReconcilitionDtls = showReconcilitionDtls;
    }

    /**
     * @return the audutTyDisabled
     */
    public boolean isAudutTyDisabled() {
        return audutTyDisabled;
    }

    /**
     * @param audutTyDisabled the audutTyDisabled to set
     */
    public void setAudutTyDisabled(boolean audutTyDisabled) {
        this.audutTyDisabled = audutTyDisabled;
    }

    /**
     * @return the paraNoDisabled
     */
    public boolean isParaNoDisabled() {
        return paraNoDisabled;
    }

    /**
     * @param paraNoDisabled the paraNoDisabled to set
     */
    public void setParaNoDisabled(boolean paraNoDisabled) {
        this.paraNoDisabled = paraNoDisabled;
    }

    /**
     * @return the yearDisabled
     */
    public boolean isYearDisabled() {
        return yearDisabled;
    }

    /**
     * @param yearDisabled the yearDisabled to set
     */
    public void setYearDisabled(boolean yearDisabled) {
        this.yearDisabled = yearDisabled;
    }

    /**
     * @return the regnNoDisabled
     */
    public boolean isRegnNoDisabled() {
        return regnNoDisabled;
    }

    /**
     * @param regnNoDisabled the regnNoDisabled to set
     */
    public void setRegnNoDisabled(boolean regnNoDisabled) {
        this.regnNoDisabled = regnNoDisabled;
    }

    /**
     * @return the showDtlsBtnDisabled
     */
    public boolean isShowDtlsBtnDisabled() {
        return showDtlsBtnDisabled;
    }

    /**
     * @param showDtlsBtnDisabled the showDtlsBtnDisabled to set
     */
    public void setShowDtlsBtnDisabled(boolean showDtlsBtnDisabled) {
        this.showDtlsBtnDisabled = showDtlsBtnDisabled;
    }
}
