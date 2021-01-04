/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.bean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.user_mgmt.dobj.DayBeginDobj;
import nic.vahan.db.user_mgmt.impl.DayBeginImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Administrator
 */
@ManagedBean
@ViewScoped
public class DayBeginBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(DayBeginBean.class);
    private DayBeginDobj dobjObject = new DayBeginDobj();
    private List<DayBeginBean> filteredSeat = null;
    private List<DayBeginDobj> list = new ArrayList();

    @PostConstruct
    public void init() {
        Exception exception = null;
        try {
            int offCode = Util.getUserSeatOffCode();
            String stateCd = Util.getUserStateCode();
            dobjObject.setOffCd(offCode);
            dobjObject.setStateCd(stateCd);
            dobjObject.setCashCounterClose(true);
            DayBeginImpl.getWorkDetail(dobjObject);
            DayBeginImpl.getCashCounterDetail(dobjObject);
            DayBeginImpl.getDateTimeForOpenCloseCashCounter(getList());

            diffDay();
        } catch (VahanException e) {
            exception = e;
        } catch (Exception e) {
            exception = e;
        }
        if (exception != null) {
            LOGGER.error(exception);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-Could Not Save Due to Technical Error in Database", "Error-Could Not Save Due to Technical Error in Database"));
        }

    }

    public void confirmDayBegin() {

        try {
            DayBeginImpl.getWorkDetail(dobjObject);
            if (dobjObject.getLastWorkDay().equals(dobjObject.getCurrentDt())) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Day Begin Process Already Started!", "Day Begin Process Already Started!");
                FacesContext.getCurrentInstance().addMessage(null, message);
            } else {
                PrimeFaces.current().ajax().update(":masterLayout:isConfirmDayBegin");
                PrimeFaces.current().executeScript("PF('isDayBegin').show()");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-Could Not Save Due to Technical Error in Database", "Error-Could Not Save Due to Technical Error in Database"));
        }
    }

    public void confirmCashOpen() {
        PrimeFaces.current().ajax().update(":formDayBeginProcess:DayBeginPanel");
        PrimeFaces.current().executeScript("PF('isCashOpen').show()");
    }

    public void confirmCashOpenMsgIndi() {
        PrimeFaces.current().ajax().update(":formCloseCashCounter:CloseCashCounterPanel");
        PrimeFaces.current().executeScript("PF('CashCounterClose').show()");
    }

    public void diffDay() throws Exception {

        Calendar cal1LWD = Calendar.getInstance();
        Calendar calcDt = Calendar.getInstance();
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        if (this.dobjObject.getLastWorkDay() == null || this.dobjObject.getLastWorkDay().equalsIgnoreCase("")) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Missing Entry of Smart Card / HSRP Flag in configuration File!!!", "Missing Entry of Smart Card / HSRP Flag in configuration File!!!");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        cal1LWD.setTime(format.parse(this.dobjObject.getLastWorkDay()));
        calcDt.setTime((format.parse(this.dobjObject.getCurrentDt())));
        long daydiff = (CommonUtils.daysBetween(cal1LWD, calcDt));
        if (daydiff > 0) {
            daydiff = daydiff - 1;
        } else if (daydiff <= 0) {
            daydiff = 0;
        }
        this.dobjObject.setNoofHoliday(daydiff);

    }

    public void updateDayBegin() {
        boolean isUpdated = false;
        try {
            isUpdated = DayBeginImpl.updateDayBegin(dobjObject);

            if (isUpdated) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Day Begin Process Started Successfully!", "Day Begin Process Started Successfully!");
                FacesContext.getCurrentInstance().addMessage(null, message);
            } else {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Day Begin Process Can not be Started.!", "Day Begin Process Can not be Started.!");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-Could Not Save Due to Technical Error in Database", "Error-Could Not Save Due to Technical Error in Database"));
        }
    }

    public void updateCashCounterIndividual() {
        boolean isUpdated = false;
        try {
            if ("OR,KL".contains(Util.getUserStateCode())) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Only Administrator can open/close cash counter", "Only Administrator can open/close cash counter");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
            if (dobjObject != null && dobjObject.getCashCounterOpenDt() != null) {
                if ("WB".contains(Util.getUserStateCode()) && dobjObject.isCashCounterClose() && dobjObject.getCashCounterOpenDt().equals(dobjObject.getCurrentDt())) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Only Administrator can Reopen cash counter", "Only Administrator can Reopen cash counter");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    return;
                }
            }
            if (dobjObject.getLastWorkDay() == null || dobjObject.getLastWorkDay().equalsIgnoreCase("")) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Missing Entry of Smart Card / HSRP Flag in configuration File!!!", "Missing Entry of Smart Card / HSRP Flag in configuration File!!!");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return;
            }
            if (!dobjObject.getLastWorkDay().equals(dobjObject.getCurrentDt())) {
                isUpdated = DayBeginImpl.updateDayBegin(dobjObject);
            }
            isUpdated = DayBeginImpl.updateIndividualCashCounter(dobjObject);
            if (isUpdated) {
                DayBeginImpl.getCashCounterDetail(dobjObject);
                FacesMessage message = null;
                if (dobjObject.isCashCounterClose()) {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cash Counter Close Successfully", "Cash Counter Close Successfully");
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cash Counter Open Successfully", "Cash Counter Open Successfully");
                }
                FacesContext.getCurrentInstance().addMessage(null, message);
                try {
                    list.clear();
                    DayBeginImpl.getDateTimeForOpenCloseCashCounter(getList());
                } catch (VahanException e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            } else {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cash counter Can not be updated!", "Cash counter can not be updated!");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-Could Not Save due to technical error in database.", "Error-Could Not Save due to technical error in database."));
        }
        PrimeFaces.current().ajax().update("CloseCashCounterPanel");
    }

    public String getCashCounterMassage() {
        String strMsg = "";
        if (this.dobjObject.isCashCounterClose()) {
            strMsg = "Are you sure to open the cash counter? After that you can do any furter transaction?";
        } else {
            strMsg = "Are you sure to close the cash counter? After that you can not do any furter transaction?";
        }
        return strMsg;
    }

    /**
     * @return the dobjObject
     */
    public DayBeginDobj getDobjObject() {
        return dobjObject;
    }

    /**
     * @param dobjObject the dobjObject to set
     */
    public void setDobjObject(DayBeginDobj dobjObject) {
        this.dobjObject = dobjObject;
    }

    public List<DayBeginBean> getFilteredSeat() {
        return filteredSeat;
    }

    public void setFilteredSeat(List<DayBeginBean> filteredSeat) {
        this.filteredSeat = filteredSeat;
    }

    /**
     * @return the list
     */
    public List<DayBeginDobj> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(List<DayBeginDobj> list) {
        this.list = list;
    }
}
