/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.fancy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.fancy.ReopenAdvanceNumberDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.fancy.ReopenAdvanceNumberImpl;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author SUMIT GUPTA
 */
@ManagedBean(name = "reopenadvancenumberBean")
@ViewScoped
public class ReopenAdvanceNumberBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ReopenAdvanceNumberBean.class);
    private List<ReopenAdvanceNumberDobj> Reopenlist = new ArrayList<>();
    private List<ReopenAdvanceNumberDobj> reopenlistTemp = new ArrayList<>();
    private boolean showReopenedList = false;
    private int openedListCount;

    @PostConstruct
    public void inti() {
        ReopenAdvanceNumberImpl impl = new ReopenAdvanceNumberImpl();
        try {
            Reopenlist = impl.getAdvanceNumberDetails(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
        } catch (VahanException ve) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }

    }

    public void reOpen() {
        try {
            openedListCount = 0;
            int flag1 = 0;
            if (!reopenlistTemp.isEmpty()) {
                for (ReopenAdvanceNumberDobj dobj : reopenlistTemp) {
                    flag1 = ReopenAdvanceNumberImpl.inserIntoHistory(dobj.getRegn_no(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                    openedListCount++;
                    showReopenedList = true;
                }
            }
            if (showReopenedList) {
                Reopenlist.removeAll(reopenlistTemp);
                reopenlistTemp.clear();
                //RequestContext rc = RequestContext.getCurrentInstance();
                PrimeFaces.current().ajax().update("stmt_report_subview:db_reopened");
                PrimeFaces.current().executeScript("PF('dlgdb_reopened').show()");
            }
        } catch (VahanException ve) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
    }

    public void reOpenAdvancedSelected(AjaxBehaviorEvent ajax) {
        ReopenAdvanceNumberDobj reOpenDobj = (ReopenAdvanceNumberDobj) ajax.getComponent().getAttributes().get("onscreen");
        if (reOpenDobj != null) {
            reopenlistTemp.remove(reOpenDobj);
            if (reOpenDobj.isChecked()) {
                reopenlistTemp.add(reOpenDobj);
            }

        }
    }

    public String ok() {
        return "/ui/advregn/ReopenAdvanceNumber.xhtml?faces-redirect=true";
    }

    public List<ReopenAdvanceNumberDobj> getReopenlist() {
        return Reopenlist;
    }

    public void setReopenlist(List<ReopenAdvanceNumberDobj> Reopenlist) {
        this.Reopenlist = Reopenlist;
    }

    /**
     * @return the showReopenedList
     */
    public boolean isShowReopenedList() {
        return showReopenedList;
    }

    /**
     * @param showReopenedList the showReopenedList to set
     */
    public void setShowReopenedList(boolean showReopenedList) {
        this.showReopenedList = showReopenedList;
    }

    /**
     * @return the openedListCount
     */
    public int getOpenedListCount() {
        return openedListCount;
    }

    /**
     * @param openedListCount the openedListCount to set
     */
    public void setOpenedListCount(int openedListCount) {
        this.openedListCount = openedListCount;
    }
}
