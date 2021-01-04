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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.fileMonitoring.printFilePdfDobj;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import static nic.vahan.form.impl.fileMonitoring.FilePendingReportImpl.getUserCdByUserId;
import nic.vahan.server.ServerUtil;

/**
 *
 * @author Deepak
 */
@ManagedBean(name = "printpdfbean")
@ViewScoped
public class printFilePdfBean extends printFilePdfDobj {

    HttpSession session = Util.getSession();
    private String mainHeading = "";
    private String subHeading = "";

    @PostConstruct
    public void init() {
        try {
            Map mapReport;
            mapReport = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            List<printFilePdfDobj> pendingFileList = (ArrayList<printFilePdfDobj>) mapReport.get("sendFilesList");
            setFilesList(pendingFileList);
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String strDate = formatter.format(date);
            int user_cd = getUserCdByUserId(Util.getUserId());
            Map dealerData = OwnerImpl.getDealerDetail(user_cd);
            String dealerName = dealerData.get("dealer_name").toString();
            setDealer_name(dealerName);
            setRto_name((ServerUtil.getOfficeName(Util.getUserOffCode(), Util.getUserStateCode())));
            setApprove_date(strDate);
            setMainHeading(ServerUtil.getRcptHeading());
            setSubHeading(ServerUtil.getRcptSubHeading());
        } catch (VahanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ex.getMessage()));
        }
    }

    public void backtoApplication() {
        session.removeAttribute("sendFilesList");
        session.removeAttribute("DealerName");
        session.removeAttribute("RtoName");
        FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "FilesPendingReport");
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
}