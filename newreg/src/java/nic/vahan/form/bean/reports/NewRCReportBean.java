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
 * @author nic
 */
@ManagedBean(name = "rcRptBean")
@RequestScoped
public class NewRCReportBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(NewRCReportBean.class);
    private NewRCReportDobj dobj = null;
    private List<NewRCReportDobj> selectedRclist = new ArrayList<>();
    private TmConfigurationDobj tmConfDobj = null;
    private StreamedContent viewUserSign;

    public NewRCReportBean() {
        Map map;
        map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        String rcRadiobtnValue = (String) map.get("rcRadiobtn");
        List<PrintCertificatesDobj> listPrintRc = (ArrayList<PrintCertificatesDobj>) map.get("listPrintRc");
        String paperrc = (String) map.get("paperrc");
        viewUserSign = new DefaultStreamedContent();
        try {
            String state_cd = Util.getUserStateCode();
            int off_cd = Util.getSelectedSeat().getOff_cd();
            tmConfDobj = Util.getTmConfiguration();
            for (int i = 0; i < listPrintRc.size(); i++) {
                NewRCReportDobj dobjRc = PrintDocImpl.getNewRCReportDobj(listPrintRc.get(i).getRegno(), listPrintRc.get(i).getAppl_no(), getTmConfDobj().isIs_rc_dispatch(), getTmConfDobj().isIs_rc_dispatch_without_postal_fee(), rcRadiobtnValue, paperrc, state_cd, off_cd);
                if (dobjRc != null) {
                    dobjRc.setQrText("Regn. No." + dobjRc.getRegnNo() + " Chassis No." + dobjRc.getChasiNo());
                    if (dobjRc.getUserSign() != null && !dobjRc.getUserSign().equals("")) {
                        setViewUserSign(new DefaultStreamedContent(new ByteArrayInputStream(dobjRc.getUserSign())));
                    }
                    selectedRclist.add(dobjRc);
                }
            }

        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

    }

    /**
     * @return the dobj
     */
    public NewRCReportDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(NewRCReportDobj dobj) {
        this.dobj = dobj;
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
