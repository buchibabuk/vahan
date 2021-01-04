/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.TaxClearanceCertificateDetailDobj;
import nic.vahan.form.dobj.reports.TaxClearanceCertificatePrintReportDobj;
import nic.vahan.form.impl.TaxClearanceCertificatePrintImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;

/**
 *
 * @author ankur
 */
@ManagedBean(name = "TCCPrint")
@ViewScoped
public class TaxClearanceCertificatePrintReportBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(TaxClearanceCertificatePrintReportBean.class);
    private TaxClearanceCertificatePrintReportDobj dobj = null;
    private String currentDt;
    private List<TaxClearanceCertificatePrintReportDobj> selectedTCClist = new ArrayList<>();
    private ArrayList<TaxClearanceCertificatePrintReportDobj> li = new ArrayList<>();
    private ArrayList<TaxClearanceCertificatePrintReportDobj> liTemp = null;
    private boolean render_reportTcc_No;

    public TaxClearanceCertificatePrintReportBean() {
        Map map;
        map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        List<TaxClearanceCertificateDetailDobj> listPrintTCC = (ArrayList<TaxClearanceCertificateDetailDobj>) map.get("listPrintTCC");
        try {
            if (Util.getUserStateCode().equals("RJ")) {
                render_reportTcc_No = true;
            } else {
                render_reportTcc_No = false;
            }
            for (int i = 0; i < listPrintTCC.size(); i++) {
                TaxClearanceCertificatePrintReportDobj TCCData = TaxClearanceCertificatePrintImpl.getTCCPrintDobj(listPrintTCC.get(i).getRegno(), listPrintTCC.get(i).getAppl_no());
                if (TCCData != null) {
                    TCCData.setQrText("Regn. No." + TCCData.getRegnNO() + " Tax Clear Upto." + TCCData.getClear_to() + " Tcc No " + TCCData.getTcc_no());
                    TaxClearanceCertificatePrintImpl.deleteAndSaveHistoryTCC(listPrintTCC);
                    TCCData.setTccCount(i + 1);
                    li.add(TCCData);
                }
            }
        } catch (VahanException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

    }

    public TaxClearanceCertificatePrintReportDobj getDobj() {
        return dobj;
    }

    public void setDobj(TaxClearanceCertificatePrintReportDobj dobj) {
        this.dobj = dobj;
    }

    public String getCurrentDt() {
        return currentDt;
    }

    public void setCurrentDt(String currentDt) {
        this.currentDt = currentDt;
    }

    public ArrayList<TaxClearanceCertificatePrintReportDobj> getLi() {
        return li;
    }

    public void setLi(ArrayList<TaxClearanceCertificatePrintReportDobj> li) {
        this.li = li;
    }

    public ArrayList<TaxClearanceCertificatePrintReportDobj> getLiTemp() {
        return liTemp;
    }

    public void setLiTemp(ArrayList<TaxClearanceCertificatePrintReportDobj> liTemp) {
        this.liTemp = liTemp;
    }

    public List<TaxClearanceCertificatePrintReportDobj> getSelectedTCClist() {
        return selectedTCClist;
    }

    public void setSelectedTCClist(List<TaxClearanceCertificatePrintReportDobj> selectedTCClist) {
        this.selectedTCClist = selectedTCClist;
    }

    public boolean isRender_reportTcc_No() {
        return render_reportTcc_No;
    }

    public void setRender_reportTcc_No(boolean render_reportTcc_No) {
        this.render_reportTcc_No = render_reportTcc_No;
    }
}
