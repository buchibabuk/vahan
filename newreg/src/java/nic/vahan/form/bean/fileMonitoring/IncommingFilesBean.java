/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.fileMonitoring;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.fileMonitoring.IncommingFilesReportDO;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.fileMonitoring.IncommingFilesImpl;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Deepak
 */
@ManagedBean(name = "incommingFiles")
@ViewScoped
public class IncommingFilesBean extends IncommingFilesReportDO {

    private static final Logger LOGGER = Logger.getLogger(IncommingFilesBean.class);
    static String whereami = "IncommingFilesBean";

    @PostConstruct
    public void init() {
        try {
            setDearlist(IncommingFilesImpl.getDealers(Util.getUserStateCode(), Util.getUserLoginOffCode()));
            setModuleServiceDataList(IncommingFilesImpl.getModuleDesc());
        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage(), e.getMessage()));
        }
    }

    public void getPendingFiles() throws Exception {
        if (getDealerName().equalsIgnoreCase("-1")) {
            getFilelist().clear();
        } else {
            getFilelist().clear();
            setFilelist(IncommingFilesImpl.getPendingFiles(this));
            if (getFilelist().isEmpty()) {
                IncommingFilesImpl.sendMessage("No Record Found");
            }
        }
    }

    public void getResetTables() throws Exception {
        HttpSession session = Util.getSession();
        if (getFilterfilelist() != null) {
            getFilterfilelist().clear();
        }
        if (getFilelist() != null) {
            getFilelist().clear();
        }
        setFilelist(IncommingFilesImpl.getPendingFiles(this));
        setModuleServiceDataList(IncommingFilesImpl.getModuleDesc());
        getModuleServiceDataList1().clear();
        session.removeAttribute("MissingDocs");
        session.removeAttribute("RtoName");
        session.removeAttribute("REASON");
        session.removeAttribute("FILENUMBER");
        setReason("");
        if (getFilelist().isEmpty()) {
            setDealerName("-1");
        }
    }

    public void getSessionFileNumber() throws Exception {
        HttpSession session = Util.getSession();
        getModuleServiceDataList1().clear();
        session.removeAttribute("FILENUMBER");
        setModuleServiceDataList(IncommingFilesImpl.getModuleDesc());
        getModuleServiceDataList1().clear();
        if (getFilelist().isEmpty()) {
            setDealerName("-1");
        }
        setReason("");
    }

    public void submitFile() {
        Exception e = null;
        boolean flag = false;
        String error = "";
        String status = null;
        boolean linkflg = false;
        try {
            HttpSession session = Util.getSession();
            String fileNumber = (String) session.getAttribute("FILENUMBER");
            if (error.equalsIgnoreCase("")) {
                Iterator itr = null;
                itr = getModuleServiceDataList().iterator();
                while (itr.hasNext()) {
                    IncommingFilesReportDO lmst = (IncommingFilesReportDO) itr.next();
                    if (lmst.isEnter()) {
                        linkflg = true;
                    }
                }
                if (!linkflg) {
                    flag = true;
                    IncommingFilesImpl.sendMessage("Please Select Atleast One Document");
                }
                if (flag == false) {
                    if (error.equalsIgnoreCase("")) {
                        boolean testflag = IncommingFilesImpl.saveFileDetail(getModuleServiceDataList(), fileNumber);
                        if (testflag) {
                            IncommingFilesImpl.sendMessage("File Submitted Successfully.");
                            setEnter(false);
                            setReason("");
                        }
                    }
                }
            }
            if (!error.equalsIgnoreCase("")) {
                IncommingFilesImpl.sendMessage(error);
            }
        } catch (Exception ex) {
            e = ex;
        }
        if (e != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage(), e.getMessage()));
        }
    }

    public void fetchFileNo() {
        try {
            HttpSession session = Util.getSession();
            String fileNumber = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("filenumber");
            session.setAttribute("FILENUMBER", fileNumber);
            //RequestContext context = RequestContext.getCurrentInstance();
            PrimeFaces.current().executeScript("PF('dlgdocumentselection').show();");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void rejectFile() {
        HttpSession session = Util.getSession();
        String fileNumber = (String) session.getAttribute("FILENUMBER");
        Exception e = null;
        boolean flag = false;
        String error = "";
        boolean linkflg = false;
        IncommingFilesReportDO usr1 = null;
        List<IncommingFilesReportDO> listForPrint = new ArrayList<IncommingFilesReportDO>();
        try {
            if (error.equalsIgnoreCase("")) {
                Iterator itr = null;
                itr = getModuleServiceDataList().iterator();
                int j = 1;
                while (itr.hasNext()) {
                    IncommingFilesReportDO lmst = (IncommingFilesReportDO) itr.next();
                    if (lmst.isEnter()) {
                        linkflg = true;
                        usr1 = new IncommingFilesReportDO();
                        usr1.setSerialNo("" + j++);
                        usr1.setServiceName(lmst.getServiceName());
                        listForPrint.add(usr1);
                    }
                }
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("missingdocs", listForPrint);
                if (getReason().equalsIgnoreCase("")) {
                    flag = true;
                    IncommingFilesImpl.sendMessage("Please Enter Reason For Rejection");
                }
                if (flag == false) {
                    if (error.equalsIgnoreCase("")) {
                        for (int i = 0; i < getModuleServiceDataList().size(); i++) {
                            if (getModuleServiceDataList().get(i).isEnter()) {
                                usr1 = null;
                                usr1 = new IncommingFilesReportDO();
                                usr1.setServiceID(getModuleServiceDataList().get(i).getServiceID());
                                usr1.setServiceName(getModuleServiceDataList().get(i).getServiceName());
                                usr1.setEnter(getModuleServiceDataList().get(i).isEnter());
                                getModuleServiceDataList1().add(usr1);
                            }
                        }
                        session.setAttribute("REASON", getReason());
                        boolean testflag = IncommingFilesImpl.saveRejectFilesDetail(getModuleServiceDataList1(), fileNumber, getReason());
                        if (testflag) {
                            FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "printRejectFilePdf");
                        } else {
                            IncommingFilesImpl.sendMessage("There is Some Problem in Reject File");
                        }
                    }
                }
            }
            if (!error.equalsIgnoreCase("")) {
                IncommingFilesImpl.sendMessage(error);
            }
        } catch (Exception ex) {
            e = ex;
        }
        if (e != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage(), e.getMessage()));
        }
    }
}