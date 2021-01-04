/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.eChallan;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.HttpSession;
import nic.vahan.common.jsf.utils.JSFUtils;

import nic.vahan.form.dobj.eChallan.ChallanReportDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.eChallan.DuplicateAndCancellationChallanImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "cancelchallan")
@ViewScoped
public class ChallanCancellationBean {

    private static final Logger LOGGER = Logger.getLogger(ChallanCancellationBean.class);
    private String applNo;
    private String reason;
    DuplicateAndCancellationChallanImpl impl = null;
    private ChallanReportDobj reportDobj = new ChallanReportDobj();
    private List applNoList = new ArrayList();

    public ChallanCancellationBean() {
        try {

            HttpSession ses = Util.getSession();
            applNo = (String) ses.getAttribute("duplcate_appl_no");
            String reason = (String) ses.getAttribute("reason");
            impl = new DuplicateAndCancellationChallanImpl();
            boolean cancelChallan = impl.cancelChallan(applNo, reason);
            if (cancelChallan) {
                JSFUtils.showMessage("Challan Has Been Cancel Successfully...");
                reportDobj = impl.printChallanReport(applNo);
                reportDobj.setRcpt_heading(ServerUtil.getRcptHeading());
                reportDobj.setRcpt_sub_heading(ServerUtil.getRcptSubHeading());
                reset();
                setApplNoList(impl.getApplNo());

            } else {
                JSFUtils.showMessage("There Is Some Problem...");
                return;

            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void reset() {
        setApplNo("");
        setReason("");
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    public ChallanReportDobj getReportDobj() {
        return reportDobj;
    }

    public void setReportDobj(ChallanReportDobj reportDobj) {
        this.reportDobj = reportDobj;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List getApplNoList() {
        return applNoList;
    }

    public void setApplNoList(List applNoList) {
        this.applNoList = applNoList;
    }
}
