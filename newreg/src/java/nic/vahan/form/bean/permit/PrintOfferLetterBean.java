/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.vahan.form.impl.permit.PrintPermitShowDetails;
import nic.vahan.form.impl.permit.PrintPermitImpl;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
//import org.primefaces.component.selectoneradio.SelectOneRadio;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "offer_letter")
@ViewScoped
public class PrintOfferLetterBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PrintOfferLetterBean.class);
    private String sel_pmt_type;
    private List<PrintPermitShowDetails> listPermit = null;
    private List array_pmt_typ = new ArrayList();
    private List<PrintPermitShowDetails> allPrintWork = null;
    private PrintPermitImpl pmtImpl = null;
    private PrintPermitShowDetails pmtOfferDetails = null;
    private String app_notApp = "";

    public PrintOfferLetterBean() {
        changeSelectMenu();
    }

    public void select_Radio_Button() {
        changeSelectMenu();
    }

    public void changeSelectMenu() {
        try {
            pmtImpl = new PrintPermitImpl();
            if (!CommonUtils.isNullOrBlank(getApp_notApp())) {
                allPrintWork = pmtImpl.getOfferLeter(getApp_notApp());
            } else {
                allPrintWork = pmtImpl.getOfferLeter("A");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void singlePrint(String appl_no) {
    }

    public String getSel_pmt_type() {
        return sel_pmt_type;
    }

    public void setSel_pmt_type(String sel_pmt_type) {
        this.sel_pmt_type = sel_pmt_type;
    }

    public List<PrintPermitShowDetails> getListPermit() {
        return listPermit;
    }

    public void setListPermit(List<PrintPermitShowDetails> listPermit) {
        this.listPermit = listPermit;
    }

    public List getArray_pmt_typ() {
        return array_pmt_typ;
    }

    public void setArray_pmt_typ(List array_pmt_typ) {
        this.array_pmt_typ = array_pmt_typ;
    }

    public List<PrintPermitShowDetails> getAllPrintWork() {
        return allPrintWork;
    }

    public void setAllPrintWork(List<PrintPermitShowDetails> allPrintWork) {
        this.allPrintWork = allPrintWork;
    }

    public PrintPermitShowDetails getPmtOfferDetails() {
        return pmtOfferDetails;
    }

    public void setPmtOfferDetails(PrintPermitShowDetails pmtOfferDetails) {
        this.pmtOfferDetails = pmtOfferDetails;
    }

    public String getApp_notApp() {
        return app_notApp;
    }

    public void setApp_notApp(String app_notApp) {
        this.app_notApp = app_notApp;
    }
}
