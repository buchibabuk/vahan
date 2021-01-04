/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.PrintCertificatesDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.reports.FCPrintReportDobj;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author nic
 */
@ManagedBean(name = "FCPrint")
@RequestScoped
public class FCPrintReportBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(FCPrintReportBean.class);
    private FCPrintReportDobj dobj = null;
    private String currentDt;
    private TmConfigurationDobj tmConfDobj = null;
    private List<FCPrintReportDobj> selectedRclist = new ArrayList<>();
    private StreamedContent viewSignFileOff1;
    private StreamedContent viewSignFileOff2;
    private boolean iswbstatecd;
    private boolean isSKstatecd;

    public FCPrintReportBean() {
        Map map;
        Date date = null;
        map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        String rcRadiobtnValue = (String) map.get("rcRadiobtn");
        List<PrintCertificatesDobj> listPrintRc = (ArrayList<PrintCertificatesDobj>) map.get("listPrintRc");
        dobj = new FCPrintReportDobj();
        viewSignFileOff1 = new DefaultStreamedContent();
        viewSignFileOff2 = new DefaultStreamedContent();
        try {

            tmConfDobj = Util.getTmConfiguration();
            for (int i = 0; i < listPrintRc.size(); i++) {
                dobj = PrintDocImpl.getFCPrintDobj(listPrintRc.get(i).getRegno(), listPrintRc.get(i).getAppl_no(), tmConfDobj, rcRadiobtnValue, listPrintRc.get(i).getPrintForm());
                if (dobj != null) {
                    if (dobj.getSignFitOff1() != null && !dobj.getSignFitOff1().equals("")) {
                        setViewSignFileOff1(new DefaultStreamedContent(new ByteArrayInputStream(dobj.getSignFitOff1())));
                    }
                    if (dobj.getSignFitOff2() != null && !dobj.getSignFitOff2().equals("")) {
                        setViewSignFileOff2(new DefaultStreamedContent(new ByteArrayInputStream(dobj.getSignFitOff2())));
                    }
                    if (dobj.getStateCD() != null && !dobj.getStateCD().isEmpty() && "WB".contains(dobj.getStateCD())) {
                        setIswbstatecd(true);
                    } else {
                        setIswbstatecd(false);
                    }
                    if (dobj.getStateCD() != null && !dobj.getStateCD().isEmpty() && "SK".contains(dobj.getStateCD())) {
                        setIsSKstatecd(true);
                    } else {
                        setIsSKstatecd(false);
                    }
                    dobj.setQrText("Vehicle No: " + dobj.getRegn_no() + " Chassis No: " + dobj.getChasi_no() + " Certificate will expire on: " + dobj.getFitUpto());
                    selectedRclist.add(dobj);
                }
            }

        } catch (VahanException e) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", e.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);

        }
    }

    /**
     * @return the dobj
     */
    public FCPrintReportDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(FCPrintReportDobj dobj) {
        this.dobj = dobj;
    }

    public String getCurrentDt() {
        return currentDt;
    }

    public void setCurrentDt(String currentDt) {
        this.currentDt = currentDt;
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
     * @return the selectedRclist
     */
    public List<FCPrintReportDobj> getSelectedRclist() {
        return selectedRclist;
    }

    /**
     * @param selectedRclist the selectedRclist to set
     */
    public void setSelectedRclist(List<FCPrintReportDobj> selectedRclist) {
        this.selectedRclist = selectedRclist;
    }

    /**
     * @return the viewSignFileOff1
     */
    public StreamedContent getViewSignFileOff1() {
        return viewSignFileOff1;
    }

    /**
     * @param viewSignFileOff1 the viewSignFileOff1 to set
     */
    public void setViewSignFileOff1(StreamedContent viewSignFileOff1) {
        this.viewSignFileOff1 = viewSignFileOff1;
    }

    /**
     * @return the viewSignFileOff2
     */
    public StreamedContent getViewSignFileOff2() {
        return viewSignFileOff2;
    }

    /**
     * @param viewSignFileOff2 the viewSignFileOff2 to set
     */
    public void setViewSignFileOff2(StreamedContent viewSignFileOff2) {
        this.viewSignFileOff2 = viewSignFileOff2;
    }

    /**
     * @return the iswbstatecd
     */
    public boolean isIswbstatecd() {
        return iswbstatecd;
    }

    /**
     * @param iswbstatecd the iswbstatecd to set
     */
    public void setIswbstatecd(boolean iswbstatecd) {
        this.iswbstatecd = iswbstatecd;
    }

    /**
     * @return the isSKstatecd
     */
    public boolean isIsSKstatecd() {
        return isSKstatecd;
    }

    /**
     * @param isSKstatecd the isSKstatecd to set
     */
    public void setIsSKstatecd(boolean isSKstatecd) {
        this.isSKstatecd = isSKstatecd;
    }
}
