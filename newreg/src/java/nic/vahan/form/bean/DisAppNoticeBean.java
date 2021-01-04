/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.servlet.http.HttpSession;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.DisAppNoticeDobj;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC103
 */
@ManagedBean
@RequestScoped
public class DisAppNoticeBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(DisAppNoticeBean.class);
    private DisAppNoticeDobj dobj;
    private Date today = new Date();
    private String currentDate;
    private String applNo;
    private String regnNo;
    private int purCode;

    public DisAppNoticeBean() {
        dobj = new DisAppNoticeDobj();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        sdf.applyPattern("dd-MMM-yyyy");
        currentDate = sdf.format(today);
        try {
            HttpSession ses = Util.getSession();
            applNo = (String) ses.getAttribute("applNo");
            regnNo = (String) ses.getAttribute("regnNo");
            if (ses.getAttribute("purCode") != null && !CommonUtils.isNullOrBlank(String.valueOf(ses.getAttribute("purCode")))) {
                purCode = (Integer) ses.getAttribute("purCode");
            }
            if (!CommonUtils.isNullOrBlank(applNo)
                    && !CommonUtils.isNullOrBlank(regnNo)
                    && purCode != 0) {
                dobj = PrintDocImpl.getDisAppNoticeData(applNo, regnNo, purCode);
            } else {
                if (Util.getSelectedSeat() != null) {
                    purCode = Util.getSelectedSeat().getPur_cd();
                    applNo = Util.getSelectedSeat().getAppl_no();
                    regnNo = Util.getSelectedSeat().getRegn_no();
                    dobj = PrintDocImpl.getDisAppNoticeData(applNo, regnNo, purCode);
                }
            }
        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public DisAppNoticeDobj getDobj() {
        return dobj;
    }

    public void setDobj(DisAppNoticeDobj dobj) {
        this.dobj = dobj;
    }

    public Date getToday() {
        return today;
    }

    public void setToday(Date today) {
        this.today = today;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
}
