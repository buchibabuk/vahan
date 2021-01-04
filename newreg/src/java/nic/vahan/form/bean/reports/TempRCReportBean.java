/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.form.dobj.PrintCertificatesDobj;
import nic.vahan.form.dobj.reports.TempRCReportDobj;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author nic
 */
@ManagedBean(name = "tempRCBean")
@RequestScoped
public class TempRCReportBean implements Serializable {

    private TempRCReportDobj dobj = null;
    private List<TempRCReportDobj> selectedRclist = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(TempRCReportBean.class);
    private boolean labelFormCRTem = false;
    private StreamedContent viewUserSign;

    public TempRCReportBean() {
        Map mapReport;

        try {
            viewUserSign = new DefaultStreamedContent();
            String state_cd = Util.getUserStateCode();
            int off_cd = Util.getSelectedSeat().getOff_cd();
            mapReport = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            String rcRadiobtnValue = (String) mapReport.get("rcRadiobtn");
            List<PrintCertificatesDobj> listPrintRc = (ArrayList<PrintCertificatesDobj>) mapReport.get("listPrintRc");
            if (state_cd != null && "KL".contains(state_cd)) {
                setLabelFormCRTem(true);
            } else {
                setLabelFormCRTem(false);
            }
            if (listPrintRc != null && !listPrintRc.isEmpty()) {
                for (int i = 0; i < listPrintRc.size(); i++) {
                    TempRCReportDobj dobjTempRC = PrintDocImpl.getTempRcReportDobj(listPrintRc.get(i).getRegno(), listPrintRc.get(i).getAppl_no(), state_cd, off_cd, rcRadiobtnValue);
                    if (dobjTempRC != null && dobjTempRC.getTempRegnNo() != null && dobjTempRC.getChasiNo() != null) {
                        dobjTempRC.setQrText("Regn. No." + dobjTempRC.getTempRegnNo() + " Chassis No." + dobjTempRC.getChasiNo());
                        if (dobjTempRC.getUserSign() != null && !dobjTempRC.getUserSign().equals("")) {
                            setViewUserSign(new DefaultStreamedContent(new ByteArrayInputStream(dobjTempRC.getUserSign())));
                        }
                        selectedRclist.add(dobjTempRC);
                    }
                }
            }
        } catch (VahanException vex) {
            JSFUtils.setFacesMessage(vex.getMessage(), null, JSFUtils.WARN);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.setFacesMessage("Something Went wrong.Please try again", null, JSFUtils.WARN);
        }
    }

    /**
     * @return the dobj
     */
    public TempRCReportDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(TempRCReportDobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the selectedRclist
     */
    public List<TempRCReportDobj> getSelectedRclist() {
        return selectedRclist;
    }

    /**
     * @param selectedRclist the selectedRclist to set
     */
    public void setSelectedRclist(List<TempRCReportDobj> selectedRclist) {
        this.selectedRclist = selectedRclist;
    }

    /**
     * @return the labelFormCRTem
     */
    public boolean isLabelFormCRTem() {
        return labelFormCRTem;
    }

    /**
     * @param labelFormCRTem the labelFormCRTem to set
     */
    public void setLabelFormCRTem(boolean labelFormCRTem) {
        this.labelFormCRTem = labelFormCRTem;
    }

    /**
     * @return the viewUserSign
     */
    public StreamedContent getViewUserSign() {
        return viewUserSign;
    }

    /**
     * @param viewUserSign the viewUserSign to set
     */
    public void setViewUserSign(StreamedContent viewUserSign) {
        this.viewUserSign = viewUserSign;
    }
}
