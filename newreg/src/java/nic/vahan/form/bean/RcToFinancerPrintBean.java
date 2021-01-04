/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.HttpSession;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.impl.RcToFinancerPrintImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.primefaces.PrimeFaces;

/**
 *
 * @author acer
 */
@ManagedBean(name = "rcToFinPrint")
@ViewScoped
public class RcToFinancerPrintBean implements Serializable {

    private String applNo;
    private String regnNo;
    private int purCode;
    private OwnerDetailsDobj dobj;
    private String date;
    private String tempregn;
    private boolean print;
    private String state_cd;
    private int off_cd;
    private String empCode;
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(RcToFinancerPrintBean.class);

    @PostConstruct
    public void init() {
        try {
            reset();
            HttpSession ses = Util.getSession();
            if (ses != null) {
                off_cd = Util.getUserSeatOffCode() != null ? Util.getUserSeatOffCode() : 0;
                state_cd = Util.getUserStateCode();
                empCode = Util.getEmpCode();
                setApplNo((String) ses.getAttribute("applNofrc"));
                regnNo = (String) ses.getAttribute("regnNofrc");
                if (ses.getAttribute("purCodefrc") != null && !CommonUtils.isNullOrBlank(String.valueOf(ses.getAttribute("purCodefrc")))) {
                    purCode = (Integer) ses.getAttribute("purCodefrc");
                    ses.removeAttribute("applNofrc");
                    ses.removeAttribute("regnNofrc");
                    ses.removeAttribute("purCodefrc");
                }
                if (!CommonUtils.isNullOrBlank(applNo)
                        && !CommonUtils.isNullOrBlank(regnNo)
                        && purCode != 0) {
                    dobj = new RcToFinancerPrintImpl().initializePrintDetail(applNo, regnNo, state_cd, off_cd, empCode);
                    setDate(JSFUtils.convertToStandardDateFormat(new Date()));
                    if (dobj != null) {
                        setPrint(true);
                    }
                }

            } else {
                reset();
            }
        } catch (VahanException vex) {
            reset();
            JSFUtils.showMessagesInDialog("Alert", vex.getMessage(), FacesMessage.SEVERITY_INFO);
        } catch (Exception ex) {
            reset();
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert", "Please try after some time or contact to Administrator", FacesMessage.SEVERITY_INFO);
        }
    }

    public void printDetail() {
        try {
            if (!CommonUtils.isNullOrBlank(empCode)) {
                dobj = new RcToFinancerPrintImpl().gettingRequiredPrintDtls(applNo, tempregn, state_cd, off_cd);
                setDate(JSFUtils.convertToStandardDateFormat(new Date()));
                if (dobj == null) {
                    reset();
                    JSFUtils.showMessagesInDialog("Alert", "Record Not Found", FacesMessage.SEVERITY_INFO);
                } else {
                    regnNo = tempregn;
                    setPrint(true);
                }
            } else {
                reset();
                JSFUtils.showMessagesInDialog("Alert", "Their may be some problem occurred while getting print details", FacesMessage.SEVERITY_INFO);
            }
        } catch (VahanException ex) {
            reset();
            JSFUtils.showMessagesInDialog("Alert", ex.getMessage(), FacesMessage.SEVERITY_INFO);
        } catch (Exception ex) {
            reset();
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert", "Please try after some time or contact to Administrator", FacesMessage.SEVERITY_INFO);
        }
    }

    public void printCommand() {
        try {
            if (dobj != null && applNo != null) {
                dobj.setOff_cd(off_cd);
                dobj.setState_cd(state_cd);
                new RcToFinancerPrintImpl().printHistory(dobj, applNo, empCode);
                PrimeFaces.current().executeScript("window.print();");
            } else {
                JSFUtils.showMessagesInDialog("Alert", "Problem occured while print", FacesMessage.SEVERITY_INFO);
            }
        } catch (VahanException ex) {
            reset();
            JSFUtils.showMessagesInDialog("Alert", ex.getMessage(), FacesMessage.SEVERITY_INFO);
        } catch (Exception ex) {
            reset();
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert", "Problem occured while print", FacesMessage.SEVERITY_INFO);
        }
    }

    private void reset() {
        dobj = null;
        setApplNo(null);
        regnNo = null;
        purCode = 0;
        tempregn = null;
        print = false;
    }

    /**
     * @return the dobj
     */
    public OwnerDetailsDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(OwnerDetailsDobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the tempregn
     */
    public String getTempregn() {
        return tempregn;
    }

    /**
     * @param tempregn the tempregn to set
     */
    public void setTempregn(String tempregn) {
        this.tempregn = tempregn;
    }

    /**
     * @return the print
     */
    public boolean isPrint() {
        return print;
    }

    /**
     * @param print the print to set
     */
    public void setPrint(boolean print) {
        this.print = print;
    }

    /**
     * @return the applNo
     */
    public String getApplNo() {
        return applNo;
    }

    /**
     * @param applNo the applNo to set
     */
    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }
}
