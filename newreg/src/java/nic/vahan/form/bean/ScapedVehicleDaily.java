/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import nic.vahan.form.dobj.reports.ScrappedVehicleReportDobj;
import nic.vahan.form.impl.ScrappedVehicleImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author manoj
 */
@ManagedBean(name = "scrap_vehicle")
@SessionScoped
public class ScapedVehicleDaily implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(ScapedVehicleDaily.class);
    private Date valid_from;
    private Date valid_to;
    private Date currentDate = new Date();
    FacesMessage message = null;
    private String header = "Scraped Vehicle Daily Report";
    private boolean renderPanel = false;
    private int total_record;
    private String scrap_from = "";
    private String scrap_upto = "";
    private List<ScrappedVehicleReportDobj> scrapList = null;
    private List<ScrappedVehicleReportDobj> scrapListPrint = new ArrayList<>();
    private ScrappedVehicleReportDobj reportDobj = null;
    private ScrappedVehicleImpl impl = new ScrappedVehicleImpl();
    private boolean renderprint = false;

    public void scrappedVehicle() {
        scrapList = new ArrayList<>();
        try {
            scrapList = impl.getScrapdelatils(getValid_from(), getValid_to());
            if (!scrapList.isEmpty()) {
                setRenderprint(true);
                setScrap_from(ServerUtil.parseDateToString(getValid_from()));
                setScrap_upto(ServerUtil.parseDateToString(getValid_to()));
                renderPanel = true;
                setTotal_record(scrapList.size());
            } else {
                setRenderprint(false);
                setScrap_from("");
                setScrap_upto("");
                renderPanel = false;
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "No Record Found !!", "No Record Found !!");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return;
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public String printReport() {
        reportDobj = new ScrappedVehicleReportDobj();
        if (scrapList.size() > 0) {
            reportDobj.setPrintedon(ServerUtil.parseDateToString(currentDate));
            reportDobj.setUserName(Util.getUserName());
            reportDobj.setStateName(scrapList.get(0).getStateName());
            reportDobj.setOffname(scrapList.get(0).getOffname());
            setScrapListPrint(scrapList);
        } else {
            setScrapListPrint(null);
        }
        return "/ui/reports/ScrappedVehicleDailyReport.xhtml?faces-redirect=true";
    }

    public List<ScrappedVehicleReportDobj> getScrapList() {
        return scrapList;
    }

    public void setScrapList(List<ScrappedVehicleReportDobj> scrapList) {
        this.scrapList = scrapList;
    }

    public Date getValid_from() {
        return valid_from;
    }

    public void setValid_from(Date valid_from) {
        this.valid_from = valid_from;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public boolean isRenderPanel() {
        return renderPanel;
    }

    public void setRenderPanel(boolean renderPanel) {
        this.renderPanel = renderPanel;
    }

    public String getHeader() {
        return header;
    }

    public List<ScrappedVehicleReportDobj> getScrapListPrint() {
        return scrapListPrint;
    }

    public void setScrapListPrint(List<ScrappedVehicleReportDobj> scrapListPrint) {
        this.scrapListPrint = scrapListPrint;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public ScrappedVehicleReportDobj getReportDobj() {
        return reportDobj;
    }

    public void setReportDobj(ScrappedVehicleReportDobj reportDobj) {
        this.reportDobj = reportDobj;
    }

    public int getTotal_record() {
        return total_record;
    }

    public void setTotal_record(int total_record) {
        this.total_record = total_record;
    }

    public String getScrap_from() {
        return scrap_from;
    }

    public void setScrap_from(String scrap_from) {
        this.scrap_from = scrap_from;
    }

    public String getScrap_upto() {
        return scrap_upto;
    }

    public void setScrap_upto(String scrap_upto) {
        this.scrap_upto = scrap_upto;
    }

    public boolean isRenderprint() {
        return renderprint;
    }

    public void setRenderprint(boolean renderprint) {
        this.renderprint = renderprint;
    }

    public Date getValid_to() {
        return valid_to;
    }

    public void setValid_to(Date valid_to) {
        this.valid_to = valid_to;
    }
}
