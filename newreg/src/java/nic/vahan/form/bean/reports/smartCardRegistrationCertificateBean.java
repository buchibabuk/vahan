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
import nic.vahan.form.dobj.PrintCertificatesDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.reports.NewRCReportDobj;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Afzal
 */
@ManagedBean(name = "smartcardReg")
@RequestScoped
public class smartCardRegistrationCertificateBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(smartCardRegistrationCertificateBean.class);
    private List<NewRCReportDobj> selectedRclist = new ArrayList<>();
    private boolean renderPageBreak;
    private boolean isSikkimState;
    private TmConfigurationDobj tmConfDobj = null;
    private StreamedContent viewUserSign;

    public smartCardRegistrationCertificateBean() {
        if ("SK".contains(Util.getUserStateCode())) {
            setIsSikkimState(true);
        }
        Map mapReport;
        mapReport = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        String rcRadiobtnValue = (String) mapReport.get("rcRadiobtn");
        List<PrintCertificatesDobj> listPrintRc = (ArrayList<PrintCertificatesDobj>) mapReport.get("listPrintRc");
        String paperrc = (String) mapReport.get("paperrc");
        viewUserSign = new DefaultStreamedContent();
        try {
            String state_cd = Util.getUserStateCode();
            int off_cd = Util.getSelectedSeat().getOff_cd();
            tmConfDobj = Util.getTmConfiguration();
            for (int i = 0; i < listPrintRc.size(); i++) {
                NewRCReportDobj smartcardRCData = PrintDocImpl.getNewRCReportDobj(listPrintRc.get(i).getRegno(), listPrintRc.get(i).getAppl_no(), getTmConfDobj().isIs_rc_dispatch(), getTmConfDobj().isIs_rc_dispatch_without_postal_fee(), rcRadiobtnValue, paperrc, state_cd, off_cd);
                String qrText = "Regn No" + smartcardRCData.getRegnNo() + " Chassis No" + smartcardRCData.getChasiNo();
                if (smartcardRCData.getTransferDate() != null && !smartcardRCData.getTransferDate().isEmpty()) {
                    qrText += " Transfer Date " + smartcardRCData.getTransferDate();
                }
                if (smartcardRCData.getPreOwner() != null && !smartcardRCData.getPreOwner().isEmpty()) {
                    qrText += " Previous Owner " + smartcardRCData.getPreOwner();
                }
                if (smartcardRCData.getEntryDate() != null && !smartcardRCData.getEntryDate().isEmpty()) {
                    qrText += " Entry Date " + smartcardRCData.getEntryDate();
                }
                if (smartcardRCData.getPreRegno() != null && !smartcardRCData.getPreRegno().isEmpty()) {
                    qrText += " Previous RegNo " + smartcardRCData.getPreRegno();
                }
                if (smartcardRCData.getOldState() != null && !smartcardRCData.getOldState().isEmpty()) {
                    qrText += " Old State " + smartcardRCData.getOldState();
                }
                if (smartcardRCData.getConversionDate() != null && !smartcardRCData.getConversionDate().isEmpty()) {
                    qrText += " Conversion Date " + smartcardRCData.getConversionDate();
                }
                smartcardRCData.setQrText(qrText);
                smartcardRCData.setRcCount(i + 1);
                if (smartcardRCData.getUserSign() != null && !smartcardRCData.getUserSign().equals("")) {
                    setViewUserSign(new DefaultStreamedContent(new ByteArrayInputStream(smartcardRCData.getUserSign())));
                }
                selectedRclist.add(smartcardRCData);
            }
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    /**
     * @return the selectedRclist
     */
    public List<NewRCReportDobj> getSelectedRclist() {
        return selectedRclist;
    }

    /**
     * @param selectedRclist the selectedRclist to set
     */
    public void setSelectedRclist(List<NewRCReportDobj> selectedRclist) {
        this.selectedRclist = selectedRclist;
    }

    /**
     * @return the renderPageBreak
     */
    public boolean isRenderPageBreak() {
        return renderPageBreak;
    }

    /**
     * @param renderPageBreak the renderPageBreak to set
     */
    public void setRenderPageBreak(boolean renderPageBreak) {
        this.renderPageBreak = renderPageBreak;
    }

    /**
     * @return the isSikkimState
     */
    public boolean isIsSikkimState() {
        return isSikkimState;
    }

    /**
     * @param isSikkimState the isSikkimState to set
     */
    public void setIsSikkimState(boolean isSikkimState) {
        this.isSikkimState = isSikkimState;
    }

    /**
     * @return the tmConfDobj
     */
    public TmConfigurationDobj getTmConfDobj() {
        return tmConfDobj;
    }

    /**
     * @param tmConfDobj the tmConfDobj to set
     */
    public void setTmConfDobj(TmConfigurationDobj tmConfDobj) {
        this.tmConfDobj = tmConfDobj;
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
