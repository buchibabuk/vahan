/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.dealer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.form.dobj.dealer.DealertReportsDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.dealer.DealerReportsImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author DELL
 */
@ManagedBean(name = "dealerHSRPReportBean")
@ViewScoped
public class DealerHSRP_ReportBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(DealerHSRP_ReportBean.class);
    private Date fromDate;
    private Date uptoDate;
    private Date currentDate = new Date();
    private SessionVariables sessionVariables = null;
    private List<DealertReportsDobj> hsrpDobjList = new ArrayList<>();
    private String dealerCd;
    private String dealerName;
    private int offCodeSelected = 0;
    private Date minDate = null;

    public DealerHSRP_ReportBean() {
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null
                    || sessionVariables.getStateCodeSelected() == null
                    || sessionVariables.getEmpCodeLoggedIn() == null
                    || sessionVariables.getUserCatgForLoggedInUser() == null) {
                throw new VahanException("Something went wrong, Please try again...");
            }
            if (Util.getUserOffCode() != null) {
                offCodeSelected = Util.getUserOffCode();
                minDate = DateUtil.parseDate("01-04-2019");
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getMessage()));
        }
    }

    public void findHSRPDetils() {
        try {
            if (offCodeSelected != 0) {
                if (!CommonUtils.isNullOrBlank(sessionVariables.getEmpCodeLoggedIn())) {
                    dealerCd = ServerUtil.getDealerCode(Long.parseLong(sessionVariables.getEmpCodeLoggedIn()), sessionVariables.getStateCodeSelected(), offCodeSelected);
                    if (!CommonUtils.isNullOrBlank(dealerCd)) {
                        hsrpDobjList = new DealerReportsImpl().getHSRPPendencyReports(dealerCd, sessionVariables.getStateCodeSelected(), fromDate, uptoDate);
                        if (!hsrpDobjList.isEmpty()) {
                            dealerName = hsrpDobjList.get(0).getDealerName();
                        } else {
                            throw new VahanException("Records not available for selected date range. Please selected another date.");
                        }
                    } else {
                        throw new VahanException("Unable to get dealer details.");
                    }
                }
            } else {
                throw new VahanException("Go to the Home Page and select the office code to desire result.");
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, ve.getMessage(), ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Problem occurred while processing the request.", "Problem occurred while processing the request."));
        }
    }

    public void fromDateChangeListener() {
        if (hsrpDobjList != null && !hsrpDobjList.isEmpty()) {
            hsrpDobjList.clear();
        }
        if (fromDate.compareTo(currentDate) < 0) {
            Date maxDate = ServerUtil.dateRange(fromDate, 0, 0, 7);
            if (maxDate.compareTo(currentDate) < 0) {
                uptoDate = maxDate;
            } else {
                uptoDate = currentDate;
            }
        } else {
            fromDate = currentDate;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "From date out of Current Date", "From date out of current date"));
        }
    }

    /**
     * @return the fromDate
     */
    public Date getFromDate() {
        return fromDate;
    }

    /**
     * @param fromDate the fromDate to set
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * @return the uptoDate
     */
    public Date getUptoDate() {
        return uptoDate;
    }

    /**
     * @param uptoDate the uptoDate to set
     */
    public void setUptoDate(Date uptoDate) {
        this.uptoDate = uptoDate;
    }

    /**
     * @return the currentDate
     */
    public Date getCurrentDate() {
        return currentDate;
    }

    /**
     * @param currentDate the currentDate to set
     */
    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    /**
     * @return the hsrpDobjList
     */
    public List<DealertReportsDobj> getHsrpDobjList() {
        return hsrpDobjList;
    }

    /**
     * @param hsrpDobjList the hsrpDobjList to set
     */
    public void setHsrpDobjList(List<DealertReportsDobj> hsrpDobjList) {
        this.hsrpDobjList = hsrpDobjList;
    }

    /**
     * @return the dealerName
     */
    public String getDealerName() {
        return dealerName;
    }

    /**
     * @param dealerName the dealerName to set
     */
    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
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
}
