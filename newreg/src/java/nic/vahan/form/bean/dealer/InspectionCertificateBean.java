/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.dealer;

import java.io.Serializable;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.dealer.DealertReportsDobj;
import nic.vahan.form.impl.dealer.DealerReportsImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

@ManagedBean(name = "inspCertBean")
@ViewScoped
public class InspectionCertificateBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(InspectionCertificateBean.class);
    private DealertReportsDobj dobj = null;
    private String renderMethod;
    private String text;
    private String label;
    private int mode;
    private int size;
    private String fillColor;
    private String applNo = null;
    private int purCd;
    private String regnNo = null;
    private String printType = null;
    private boolean renderOwnerDisclBtn = true;

    public InspectionCertificateBean() {
        try {
            dobj = new DealertReportsDobj();
            DealerReportsImpl dealer_report_impl = new DealerReportsImpl();
            Map map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            applNo = (String) map.get("applNo");
            if (map.get("purCd") != null) {
                if (map.get("purCd") instanceof String) {
                    String purCode = (String) map.get("purCd");
                    if (!"".equals(purCode)) {
                        purCd = Integer.parseInt(purCode);
                    }
                } else if (map.get("purCd") instanceof Integer) {
                    purCd = (Integer) map.get("purCd");
                }
            }
            if(purCd == TableConstants.VM_TRANSACTION_MAST_DEALER_TEMP_VEHICLE){
                renderOwnerDisclBtn = false;
            }
            regnNo = (String) map.get("regnNo");
            printType = (String) map.get("printType");
            if (applNo == null) {
                return;
            }
            dobj = dealer_report_impl.getInspectionCertificateData(applNo);
            if (dobj == null) {
                return;
            }
            renderMethod = "canvas";
            label = "QR Code ";
            mode = 0;
            fillColor = "7d767d";
            size = 110;
            text = "Regn. No." + dobj.getRegnNo() + " Chassis No." + dobj.getChasiNo() + " Engine No." + dobj.getEngineNo();
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public String printReports(String printType) throws VahanException {
        return ServerUtil.printReports(printType, applNo, purCd, regnNo);
    }

    /**
     * @return the dobj
     */
    public DealertReportsDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(DealertReportsDobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the renderMethod
     */
    public String getRenderMethod() {
        return renderMethod;
    }

    /**
     * @param renderMethod the renderMethod to set
     */
    public void setRenderMethod(String renderMethod) {
        this.renderMethod = renderMethod;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the mode
     */
    public int getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return the fillColor
     */
    public String getFillColor() {
        return fillColor;
    }

    /**
     * @param fillColor the fillColor to set
     */
    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    /**
     * @return the LOGGER
     */
    public static Logger getLOGGER() {
        return LOGGER;
    }

    /**
     * @param aLOGGER the LOGGER to set
     */
    public static void setLOGGER(Logger aLOGGER) {
        LOGGER = aLOGGER;
    }

    /**
     * @return the purCd
     */
    public int getPurCd() {
        return purCd;
    }

    /**
     * @param purCd the purCd to set
     */
    public void setPurCd(int purCd) {
        this.purCd = purCd;
    }

    /**
     * @return the regnNo
     */
    public String getRegnNo() {
        return regnNo;
    }

    /**
     * @param regnNo the regnNo to set
     */
    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    /**
     * @return the printType
     */
    public String getPrintType() {
        return printType;
    }

    /**
     * @param printType the printType to set
     */
    public void setPrintType(String printType) {
        this.printType = printType;
    }

    /**
     * @return the applNo
     */
    public String getApplNo() {
        return applNo;
    }

    /**
     * @param applNo the applNo to set
     */
    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    /**
     * @return the renderOwnerDisclBtn
     */
    public boolean isRenderOwnerDisclBtn() {
        return renderOwnerDisclBtn;
    }

    /**
     * @param renderOwnerDisclBtn the renderOwnerDisclBtn to set
     */
    public void setRenderOwnerDisclBtn(boolean renderOwnerDisclBtn) {
        this.renderOwnerDisclBtn = renderOwnerDisclBtn;
    }
}
