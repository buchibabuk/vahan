/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.user_mgmt.dobj.DayBeginDobj;
import nic.vahan.db.user_mgmt.dobj.CloseReopenCashCounterDobj;
import nic.vahan.db.user_mgmt.impl.DayBeginImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author niraj
 */
@ManagedBean
@ViewScoped
public class CloseReopenCashCounterBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(CloseReopenCashCounterBean.class);
    private DayBeginDobj dobjObject = new DayBeginDobj();
    private List<CloseReopenCashCounterDobj> list = new ArrayList();
    private boolean open_all_counter = true;

    @PostConstruct
    public void init() {
        Exception exception = null;
        try {
            dobjObject.setOffCd(Util.getUserSeatOffCode());
            dobjObject.setStateCd(Util.getUserStateCode());
            dobjObject.setCashCounterClose(true);
            DayBeginImpl.getWorkDetail(dobjObject);
            DayBeginImpl.getCashCounterDetailForAdmin(dobjObject);
            list = new DayBeginImpl().getCounterDetails();
            for (CloseReopenCashCounterDobj closeReopenCashCounterDobj : list) {
                if (!closeReopenCashCounterDobj.isClose_cash()) {
                    setOpen_all_counter(false);
//                    return;
                }
            }
        } catch (VahanException e) {
            exception = e;
        } catch (Exception e) {
            exception = e;
        }
        if (exception != null) {
            LOGGER.error(exception);
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, exception.getMessage(), exception.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
        }

    }

    public void confirmCloseAllCashCounter() {
        PrimeFaces.current().ajax().update(":formCloseAllCashCounter:CloseAllCashCounterPanel");
        PrimeFaces.current().executeScript("PF('CashCounterClose').show()");
    }

    public void confirmOpenAllCashCounter() {
        PrimeFaces.current().ajax().update(":formCloseAllCashCounter:CloseAllCashCounterPanel");
        PrimeFaces.current().executeScript("PF('CashCounterOpen').show()");
    }

    public void updateCashCounterForAllCashier() {
        boolean isUpdated = false;
        try {
            isUpdated = DayBeginImpl.updateCashCounterForAllCashier(dobjObject);
            for (CloseReopenCashCounterDobj closeReopenCashCounterDobj : list) {
                closeReopenCashCounterDobj.setClose_cash(true);
            }
            if (isUpdated) {
                setOpen_all_counter(!open_all_counter);
                //setOpen_all_counter(true);
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "All Cash Counter Close Successfully", "All Cash Counter Close Successfully");
                FacesContext.getCurrentInstance().addMessage(null, message);

            } else {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cash counter Can not be updated!", "Cash counter can not be updated!");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-Could Not Save due to technical error in database.", "Error-Could Not Save due to technical error in database."));
        }

        //RequestContext.getCurrentInstance().update("CloseAllCashCounterPanel");
        //RequestContext.getCurrentInstance().update("previousOpenClose");
    }

    public void updateCashCounterOpenForAllCashier() {
        boolean isUpdated = false;
        try {
            if (!dobjObject.getLastWorkDay().equals(dobjObject.getCurrentDt())) {
                isUpdated = DayBeginImpl.updateDayBegin(dobjObject);
            }
            isUpdated = DayBeginImpl.updateAllCashCounterOpenThroughAdmin();
            for (CloseReopenCashCounterDobj closeReopenCashCounterDobj : list) {
                closeReopenCashCounterDobj.setClose_cash(false);
            }
            if (isUpdated) {
                setOpen_all_counter(!open_all_counter);
                //setOpen_all_counter(false);
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "All Cash Counter Open Successfully", "All Cash Counter Open Successfully");
                FacesContext.getCurrentInstance().addMessage(null, message);

            } else {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cash counter Can not be updated!", "Cash counter can not be updated!");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-Could Not Save due to technical error in database.", "Error-Could Not Save due to technical error in database."));
        }
        //RequestContext.getCurrentInstance().update("CloseAllCashCounterPanel");
        //RequestContext.getCurrentInstance().update("previousOpenClose");
    }

    public void reopenCloseCashCounter(CloseReopenCashCounterDobj dobj) {
        boolean isUpdated = false;
        try {
            if (!dobjObject.getLastWorkDay().equals(dobjObject.getCurrentDt())) {
                isUpdated = DayBeginImpl.updateDayBegin(dobjObject);
            }
            isUpdated = DayBeginImpl.updateIndividualCashCounterThroughAdmin(dobj);
            dobjObject.setCashCounterClose(dobj.isClose_cash());
            if (isUpdated) {
                DayBeginImpl.getCashCounterDetail(dobjObject);
                FacesMessage message = null;
                if (dobjObject.isCashCounterClose()) {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cash Counter Close Successfully", "Cash Counter Close Successfully");
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cash Counter Open Successfully", "Cash Counter Open Successfully");
                }
                FacesContext.getCurrentInstance().addMessage(null, message);
            } else {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cash counter Can not be updated!", "Cash counter can not be updated!");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-Could Not Save due to technical error in database.", "Error-Could Not Save due to technical error in database."));
        }
        PrimeFaces.current().ajax().update("previousOpenClose");
    }

    public List<CloseReopenCashCounterDobj> getList() {
        return list;
    }

    public void setList(List<CloseReopenCashCounterDobj> list) {
        this.list = list;
    }

    public boolean isOpen_all_counter() {
        return open_all_counter;
    }

    public void setOpen_all_counter(boolean open_all_counter) {
        this.open_all_counter = open_all_counter;
    }
}
