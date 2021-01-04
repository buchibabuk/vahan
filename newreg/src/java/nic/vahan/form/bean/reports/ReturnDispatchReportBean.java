/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.form.dobj.reports.DownloadDispatchDobj;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.server.CommonUtils;

/**
 *
 * @author afzal
 */
@ManagedBean(name = "dispatchReturnReportBean")
@ViewScoped
public class ReturnDispatchReportBean implements Serializable {

    private DownloadDispatchDobj dobj = null;
    private String enterNo;
    private Date from_date;
    private Date to_date;
    private String officeName;
    private String printed_on;
    private String rcpt_heading;
    private String rcpt_subheading;
    private String fromDateStr;
    private String fromDateTo;
    private String radipBtnValue;
    private String rptSearchLabel;
    private List<DownloadDispatchDobj> ReturnDispatchInfoReportDobjList = new ArrayList<>();
    private boolean retDtWiseReport;
    private boolean applOrRegnWiseReport;
    private SessionVariables sessionVariables;
    private String image_background;
    private String image_logo;
    private boolean show_image_background;
    private boolean show_image_logo;

    public ReturnDispatchReportBean() {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
            return;
        }
        Map map;
        map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        enterNo = (String) map.get("enterNo");
        radipBtnValue = (String) map.get("radioBtnValue");
        rptSearchLabel = (String) map.get("rptSearchLabel");
        if ((radipBtnValue != null && !"".contains(radipBtnValue)) && ("APPLNO".contains(radipBtnValue) || "REGNNO".contains(radipBtnValue))) {
            setApplOrRegnWiseReport(true);
        } else {
            setApplOrRegnWiseReport(false);
        }
        if ((radipBtnValue != null && !"".contains(radipBtnValue)) && ("RETURNDT".contains(radipBtnValue))) {
            setRetDtWiseReport(true);
        } else {
            setRetDtWiseReport(false);
        }
        from_date = (Date) map.get("from_date");
        to_date = (Date) map.get("to_date");
        ReturnDispatchInfoReportDobjList = PrintDocImpl.getReturnDispatchInfoDetails(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), enterNo, from_date, to_date, radipBtnValue);
        if (!ReturnDispatchInfoReportDobjList.isEmpty()) {
            DownloadDispatchDobj LastRowdobj = ReturnDispatchInfoReportDobjList.get(ReturnDispatchInfoReportDobjList.size() - 1);
            officeName = LastRowdobj.getOffName();
            rcpt_heading = LastRowdobj.getRcptHeading();
            rcpt_subheading = LastRowdobj.getRcptSubHeading();
            printed_on = LastRowdobj.getPrinted_on();
            DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            fromDateStr = format.format(from_date);
            fromDateTo = format.format(to_date);
            image_background = LastRowdobj.getImage_background();
            show_image_background = LastRowdobj.isShow_image_background();
            image_logo = LastRowdobj.getImage_logo();
            show_image_logo = LastRowdobj.isShow_image_logo();
        }

    }

    /**
     * @return the dobj
     */
    public DownloadDispatchDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(DownloadDispatchDobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the enterNo
     */
    public String getEnterNo() {
        return enterNo;
    }

    /**
     * @param enterNo the enterNo to set
     */
    public void setEnterNo(String enterNo) {
        this.enterNo = enterNo;
    }

    /**
     * @return the from_date
     */
    public Date getFrom_date() {
        return from_date;
    }

    /**
     * @param from_date the from_date to set
     */
    public void setFrom_date(Date from_date) {
        this.from_date = from_date;
    }

    /**
     * @return the to_date
     */
    public Date getTo_date() {
        return to_date;
    }

    /**
     * @param to_date the to_date to set
     */
    public void setTo_date(Date to_date) {
        this.to_date = to_date;
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
     * @return the printed_on
     */
    public String getPrinted_on() {
        return printed_on;
    }

    /**
     * @param printed_on the printed_on to set
     */
    public void setPrinted_on(String printed_on) {
        this.printed_on = printed_on;
    }

    /**
     * @return the rcpt_heading
     */
    public String getRcpt_heading() {
        return rcpt_heading;
    }

    /**
     * @param rcpt_heading the rcpt_heading to set
     */
    public void setRcpt_heading(String rcpt_heading) {
        this.rcpt_heading = rcpt_heading;
    }

    /**
     * @return the rcpt_subheading
     */
    public String getRcpt_subheading() {
        return rcpt_subheading;
    }

    /**
     * @param rcpt_subheading the rcpt_subheading to set
     */
    public void setRcpt_subheading(String rcpt_subheading) {
        this.rcpt_subheading = rcpt_subheading;
    }

    /**
     * @return the fromDateStr
     */
    public String getFromDateStr() {
        return fromDateStr;
    }

    /**
     * @param fromDateStr the fromDateStr to set
     */
    public void setFromDateStr(String fromDateStr) {
        this.fromDateStr = fromDateStr;
    }

    /**
     * @return the fromDateTo
     */
    public String getFromDateTo() {
        return fromDateTo;
    }

    /**
     * @param fromDateTo the fromDateTo to set
     */
    public void setFromDateTo(String fromDateTo) {
        this.fromDateTo = fromDateTo;
    }

    /**
     * @return the ReturnDispatchInfoReportDobjList
     */
    public List<DownloadDispatchDobj> getReturnDispatchInfoReportDobjList() {
        return ReturnDispatchInfoReportDobjList;
    }

    /**
     * @param ReturnDispatchInfoReportDobjList the
     * ReturnDispatchInfoReportDobjList to set
     */
    public void setReturnDispatchInfoReportDobjList(List<DownloadDispatchDobj> ReturnDispatchInfoReportDobjList) {
        this.ReturnDispatchInfoReportDobjList = ReturnDispatchInfoReportDobjList;
    }

    /**
     * @return the radipBtnValue
     */
    public String getRadipBtnValue() {
        return radipBtnValue;
    }

    /**
     * @param radipBtnValue the radipBtnValue to set
     */
    public void setRadipBtnValue(String radipBtnValue) {
        this.radipBtnValue = radipBtnValue;
    }

    /**
     * @return the retDtWiseReport
     */
    public boolean isRetDtWiseReport() {
        return retDtWiseReport;
    }

    /**
     * @param retDtWiseReport the retDtWiseReport to set
     */
    public void setRetDtWiseReport(boolean retDtWiseReport) {
        this.retDtWiseReport = retDtWiseReport;
    }

    /**
     * @return the applOrRegnWiseReport
     */
    public boolean isApplOrRegnWiseReport() {
        return applOrRegnWiseReport;
    }

    /**
     * @param applOrRegnWiseReport the applOrRegnWiseReport to set
     */
    public void setApplOrRegnWiseReport(boolean applOrRegnWiseReport) {
        this.applOrRegnWiseReport = applOrRegnWiseReport;
    }

    /**
     * @return the rptSearchLabel
     */
    public String getRptSearchLabel() {
        return rptSearchLabel;
    }

    /**
     * @param rptSearchLabel the rptSearchLabel to set
     */
    public void setRptSearchLabel(String rptSearchLabel) {
        this.rptSearchLabel = rptSearchLabel;
    }

    /**
     * @return the image_background
     */
    public String getImage_background() {
        return image_background;
    }

    /**
     * @param image_background the image_background to set
     */
    public void setImage_background(String image_background) {
        this.image_background = image_background;
    }

    /**
     * @return the image_logo
     */
    public String getImage_logo() {
        return image_logo;
    }

    /**
     * @param image_logo the image_logo to set
     */
    public void setImage_logo(String image_logo) {
        this.image_logo = image_logo;
    }

    /**
     * @return the show_image_background
     */
    public boolean isShow_image_background() {
        return show_image_background;
    }

    /**
     * @param show_image_background the show_image_background to set
     */
    public void setShow_image_background(boolean show_image_background) {
        this.show_image_background = show_image_background;
    }

    /**
     * @return the show_image_logo
     */
    public boolean isShow_image_logo() {
        return show_image_logo;
    }

    /**
     * @param show_image_logo the show_image_logo to set
     */
    public void setShow_image_logo(boolean show_image_logo) {
        this.show_image_logo = show_image_logo;
    }
}
