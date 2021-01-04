/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.fileMonitoring;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import nic.vahan.form.dobj.fileMonitoring.RejectedFilePdfDO;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;

/**
 *
 * @author Deepak
 */
@ManagedBean(name = "rejectedfilepdf")
@ViewScoped
public class RejectedFilePdfBean extends RejectedFilePdfDO {

    HttpSession session = Util.getSession();
    private String mainHeading = "";
    private String subHeading = "";
    private String rto_name = "";
    private String dealerName = "";

    @PostConstruct
    public void init() {
        Map mapReport;
        mapReport = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        List<RejectedFilePdfDO> rejectedFileList = (ArrayList<RejectedFilePdfDO>) mapReport.get("rejectedfilelist");
        setFilelist(rejectedFileList);
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = formatter.format(date);
        setRejectedBy(String.valueOf(Util.getUserOffCode()));
        setMainHeading(ServerUtil.getRcptHeading());
        setSubHeading(ServerUtil.getRcptSubHeading());
        setRto_name((ServerUtil.getOfficeName(Util.getUserOffCode(), Util.getUserStateCode())));
        setDealerName(String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("dealername")));
    }

    public void backtoApplication() {
        session.removeAttribute("rejectedfilelist");
        FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "rejectedFilereport");
    }

    /**
     * @return the mainHeading
     */
    public String getMainHeading() {
        return mainHeading;
    }

    /**
     * @param mainHeading the mainHeading to set
     */
    public void setMainHeading(String mainHeading) {
        this.mainHeading = mainHeading;
    }

    /**
     * @return the subHeading
     */
    public String getSubHeading() {
        return subHeading;
    }

    /**
     * @param subHeading the subHeading to set
     */
    public void setSubHeading(String subHeading) {
        this.subHeading = subHeading;
    }

    /**
     * @return the rto_name
     */
    public String getRto_name() {
        return rto_name;
    }

    /**
     * @param rto_name the rto_name to set
     */
    public void setRto_name(String rto_name) {
        this.rto_name = rto_name;
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
}