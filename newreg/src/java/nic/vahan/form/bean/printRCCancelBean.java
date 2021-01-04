/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.printRCCancelDobj;
import nic.vahan.form.impl.PrintRCCancelImpl;
import org.apache.log4j.Logger;

/**
 *
 * @author acer
 */
@ManagedBean(name = "rccancelintimation")
@ViewScoped
public class printRCCancelBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(printRCCancelBean.class);
    private printRCCancelDobj rcCancellDobj = new printRCCancelDobj();
    private boolean renderReportPanel = false;

    public void checkRCCancelDetails() {
        boolean isRCCancel = false;
        try {
            PrintRCCancelImpl impl = new PrintRCCancelImpl();
            isRCCancel = impl.checkRCCancel(rcCancellDobj.getRegn_no());
            if (!isRCCancel) {
                throw new VahanException("RC is not canceled for this Vehicle");
            } else {
                rcCancellDobj = impl.getRCCancelDetails(rcCancellDobj.getRegn_no());
                String date = (new SimpleDateFormat("dd-MMM-yyyy").format(new Date()));
                rcCancellDobj.setPrintDate(date);
                setRenderReportPanel(true);
            }
        } catch (VahanException ve) {
            FacesMessage message = null;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
    }

    /**
     * @return the rcCancellDobj
     */
    public printRCCancelDobj getRcCancellDobj() {
        return rcCancellDobj;
    }

    /**
     * @param rcCancellDobj the rcCancellDobj to set
     */
    public void setRcCancellDobj(printRCCancelDobj rcCancellDobj) {
        this.rcCancellDobj = rcCancellDobj;
    }

    /**
     * @return the renderReportPanel
     */
    public boolean isRenderReportPanel() {
        return renderReportPanel;
    }

    /**
     * @param renderReportPanel the renderReportPanel to set
     */
    public void setRenderReportPanel(boolean renderReportPanel) {
        this.renderReportPanel = renderReportPanel;
    }
    /**
     * @return the rcCancell
     */
}
