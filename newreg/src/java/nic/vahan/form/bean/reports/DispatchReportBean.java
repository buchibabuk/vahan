/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.form.dobj.TmConfigurationDispatchDobj;
import nic.vahan.form.dobj.reports.DownloadDispatchDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC103
 */
@ManagedBean(name = "disReportBean")
@ViewScoped
public class DispatchReportBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(DispatchReportBean.class);
    private String stateCd;
    private String officeName;
    private String reportGenDate;
    private String userName;
    private List<DownloadDispatchDobj> dispatchInfoReportDobjList = new ArrayList<>();
    private DownloadDispatchDobj dobj = null;
    private boolean isDispatchRefNo = false;
    private SessionVariables sessionVariables;
    private TmConfigurationDispatchDobj tmConfDispatchDobj = null;
    private boolean isStickerPrint;
    private boolean isEnvelopPrint;
    private String rcpt_header;
    private String rcpt_subheader;
    private String image_background;
    private String image_logo;
    private boolean show_image_background;
    private boolean show_image_logo;

    @PostConstruct
    public void init() {
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                    || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
                return;
            }
            setStateCd(sessionVariables.getStateCodeSelected());
            setTmConfDispatchDobj(Util.getTmConfigurationDispatch());
            if (getTmConfDispatchDobj().isIs_sticker_print()) {
                setIsStickerPrint(true);
            } else {
                setIsStickerPrint(false);
            }
            if (getTmConfDispatchDobj().isIs_envelop_print()) {
                setIsEnvelopPrint(true);
            } else {
                setIsEnvelopPrint(false);
            }
            userName = sessionVariables.getEmpNameLoggedIn();
            Map map;
            getDispatchInfoReportDobjList().clear();
            map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            setDispatchInfoReportDobjList((List<DownloadDispatchDobj>) map.get("downloadlist"));
            map.remove("downloadlist");
            if (getDispatchInfoReportDobjList() != null && !getDispatchInfoReportDobjList().isEmpty()) {
                setDobj(getDispatchInfoReportDobjList().get(getDispatchInfoReportDobjList().size() - 1));
                setReportGenDate(getDobj().getPrinted_on());
                setRcpt_header(getDobj().getRcptHeading());
                setRcpt_subheader(getDobj().getRcptSubHeading());
                setImage_background(getDobj().getImage_background());
                setShow_image_background(getDobj().isShow_image_background());
                setImage_logo(getDobj().getImage_logo());
                setShow_image_logo(getDobj().isShow_image_logo());
                setOfficeName(getDobj().getOffName());
                if ("BR".contains(getDobj().getState_cd())) {
                    getDobj().setLabelMLOORDTO("DTO");
                } else {
                    getDobj().setLabelMLOORDTO("MLO");
                }
            }
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    /**
     * @return the stateCd
     */
    public String getStateCd() {
        return stateCd;
    }

    /**
     * @param stateCd the stateCd to set
     */
    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
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
     * @return the reportGenDate
     */
    public String getReportGenDate() {
        return reportGenDate;
    }

    /**
     * @param reportGenDate the reportGenDate to set
     */
    public void setReportGenDate(String reportGenDate) {
        this.reportGenDate = reportGenDate;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
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
     * @return the dispatchInfoReportDobjList
     */
    public List<DownloadDispatchDobj> getDispatchInfoReportDobjList() {
        return dispatchInfoReportDobjList;
    }

    /**
     * @param dispatchInfoReportDobjList the dispatchInfoReportDobjList to set
     */
    public void setDispatchInfoReportDobjList(List<DownloadDispatchDobj> dispatchInfoReportDobjList) {
        this.dispatchInfoReportDobjList = dispatchInfoReportDobjList;
    }

    /**
     * @return the isDispatchRefNo
     */
    public boolean isIsDispatchRefNo() {
        return isDispatchRefNo;
    }

    /**
     * @param isDispatchRefNo the isDispatchRefNo to set
     */
    public void setIsDispatchRefNo(boolean isDispatchRefNo) {
        this.isDispatchRefNo = isDispatchRefNo;
    }

    /**
     * @return the tmConfDispatchDobj
     */
    public TmConfigurationDispatchDobj getTmConfDispatchDobj() {
        return tmConfDispatchDobj;
    }

    /**
     * @param tmConfDispatchDobj the tmConfDispatchDobj to set
     */
    public void setTmConfDispatchDobj(TmConfigurationDispatchDobj tmConfDispatchDobj) {
        this.tmConfDispatchDobj = tmConfDispatchDobj;
    }

    /**
     * @return the isStickerPrint
     */
    public boolean isIsStickerPrint() {
        return isStickerPrint;
    }

    /**
     * @param isStickerPrint the isStickerPrint to set
     */
    public void setIsStickerPrint(boolean isStickerPrint) {
        this.isStickerPrint = isStickerPrint;
    }

    /**
     * @return the isEnvelopPrint
     */
    public boolean isIsEnvelopPrint() {
        return isEnvelopPrint;
    }

    /**
     * @param isEnvelopPrint the isEnvelopPrint to set
     */
    public void setIsEnvelopPrint(boolean isEnvelopPrint) {
        this.isEnvelopPrint = isEnvelopPrint;
    }

    /**
     * @return the rcpt_header
     */
    public String getRcpt_header() {
        return rcpt_header;
    }

    /**
     * @param rcpt_header the rcpt_header to set
     */
    public void setRcpt_header(String rcpt_header) {
        this.rcpt_header = rcpt_header;
    }

    /**
     * @return the rcpt_subheader
     */
    public String getRcpt_subheader() {
        return rcpt_subheader;
    }

    /**
     * @param rcpt_subheader the rcpt_subheader to set
     */
    public void setRcpt_subheader(String rcpt_subheader) {
        this.rcpt_subheader = rcpt_subheader;
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
