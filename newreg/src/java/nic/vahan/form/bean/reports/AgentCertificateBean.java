/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.reports.AgentCertificateDobj;
import nic.vahan.form.impl.AgentCertificateImpl;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author NIC
 */
@ManagedBean(name = "agentCertBean")
@ViewScoped
public class AgentCertificateBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(AgentCertificateBean.class);
    private List<AgentCertificateDobj> selectedCCRegnCertDobj1 = new ArrayList<AgentCertificateDobj>();
    private List<AgentCertificateDobj> printAgentCertDobj = new ArrayList<AgentCertificateDobj>();
    private List<AgentCertificateDobj> selectedAgentCertDobj = new ArrayList<AgentCertificateDobj>();
    private List<AgentCertificateDobj> filteredSeat = null;
    private SessionVariables sessionVariables;
    private String vahanMessages = null;
    private String appl_no;
    private String regn_no;
    private String rcRadiobtn = "";
    private boolean printAgentbyRegnNo = true;

    public void setListBeans(List<AgentCertificateDobj> printAgentCertDobjList) {
        setPrintAgentCertDobj(printAgentCertDobjList);
    }

    @PostConstruct
    public void init() {
        try {
            setSessionVariables(new SessionVariables());
            if (getSessionVariables() == null || CommonUtils.isNullOrBlank(getSessionVariables().getStateCodeSelected())
                    || getSessionVariables().getOffCodeSelected() == 0 || getSessionVariables().getActionCodeSelected() == 0) {
                setVahanMessages("Session time Out");
                return;
            }
            setRcRadiobtn("REGNNO");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

    public void agentRBListener() {
        getPrintAgentCertDobj().clear();
        if (selectedAgentCertDobj != null) {
            selectedAgentCertDobj.clear();
        }
        String radioBtnvalue = rcRadiobtn;
        try {
            if (radioBtnvalue.equalsIgnoreCase("PENREGNNO")) {
                setPrintAgentbyRegnNo(false);
                this.regn_no = "";
                this.appl_no = "";
                ArrayList<AgentCertificateDobj> list = AgentCertificateImpl.getAgentCertPendingList(getSessionVariables());
                if (list.isEmpty()) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "There is no record found !!!"));
                    return;
                } else {
                    this.setListBeans(list);
                }

            } else if (radioBtnvalue.equalsIgnoreCase("REGNNO")) {
                setPrintAgentbyRegnNo(true);
            }

        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", ve.getMessage()));
        } catch (Exception e) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Something went wrong, Please try again. !!!"));
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void getAgentCertDetails() {
        getPrintAgentCertDobj().clear();
        if (selectedAgentCertDobj != null) {
            selectedAgentCertDobj.clear();
        }
        try {
            if ((regn_no.trim().equalsIgnoreCase("") && getAppl_no().trim().equalsIgnoreCase("")) || (regn_no.trim().equalsIgnoreCase("") && getAppl_no().trim().equalsIgnoreCase(""))) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration / Application No should not be Blank !!!"));
                return;
            }
            ArrayList<AgentCertificateDobj> list = AgentCertificateImpl.getAgentCertListByAgentLincNoWise(getSessionVariables(), regn_no);
            if (list.isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "There is no record found !!!"));
                return;
            } else {
                this.setListBeans(list);
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

    public void confirmPrintAgentCert() {
//        RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('printAgentCertificate').show()");
    }

    public String printAgentCertificateReport() {

        if (selectedAgentCertDobj == null || selectedAgentCertDobj.size() < 1) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Please Select Atleast One Agent Licence No. For Printing !!!"));
            return "";
        }
        if (selectedAgentCertDobj.size() > 8) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Increase Bulk Printing Limit ,Maximum 8 Agent Licence No Allowed !!!"));
            return "";
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("listPrintAgentCert", selectedAgentCertDobj);
        return "PrintAgentCertReport";
    }

    /**
     * @return the printAgentCertDobj
     */
    public List<AgentCertificateDobj> getPrintAgentCertDobj() {
        return printAgentCertDobj;
    }

    /**
     * @param printAgentCertDobj the printAgentCertDobj to set
     */
    public void setPrintAgentCertDobj(List<AgentCertificateDobj> printAgentCertDobj) {
        this.printAgentCertDobj = printAgentCertDobj;
    }

    /**
     * @return the selectedAgentCertDobj
     */
    public List<AgentCertificateDobj> getSelectedAgentCertDobj() {
        return selectedAgentCertDobj;
    }

    /**
     * @param selectedAgentCertDobj the selectedAgentCertDobj to set
     */
    public void setSelectedAgentCertDobj(List<AgentCertificateDobj> selectedAgentCertDobj) {
        this.selectedAgentCertDobj = selectedAgentCertDobj;
    }

    /**
     * @return the filteredSeat
     */
    public List<AgentCertificateDobj> getFilteredSeat() {
        return filteredSeat;
    }

    /**
     * @param filteredSeat the filteredSeat to set
     */
    public void setFilteredSeat(List<AgentCertificateDobj> filteredSeat) {
        this.filteredSeat = filteredSeat;
    }

    /**
     * @return the sessionVariables
     */
    public SessionVariables getSessionVariables() {
        return sessionVariables;
    }

    /**
     * @param sessionVariables the sessionVariables to set
     */
    public void setSessionVariables(SessionVariables sessionVariables) {
        this.sessionVariables = sessionVariables;
    }

    /**
     * @return the vahanMessages
     */
    public String getVahanMessages() {
        return vahanMessages;
    }

    /**
     * @param vahanMessages the vahanMessages to set
     */
    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }

    /**
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }

    /**
     * @param appl_no the appl_no to set
     */
    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    /**
     * @return the regn_no
     */
    public String getRegn_no() {
        return regn_no;
    }

    /**
     * @param regn_no the regn_no to set
     */
    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    /**
     * @return the rcRadiobtn
     */
    public String getRcRadiobtn() {
        return rcRadiobtn;
    }

    /**
     * @param rcRadiobtn the rcRadiobtn to set
     */
    public void setRcRadiobtn(String rcRadiobtn) {
        this.rcRadiobtn = rcRadiobtn;
    }

    /**
     * @return the printAgentbyRegnNo
     */
    public boolean isPrintAgentbyRegnNo() {
        return printAgentbyRegnNo;
    }

    /**
     * @param printAgentbyRegnNo the printAgentbyRegnNo to set
     */
    public void setPrintAgentbyRegnNo(boolean printAgentbyRegnNo) {
        this.printAgentbyRegnNo = printAgentbyRegnNo;
    }

    /**
     * @return the selectedCCRegnCertDobj1
     */
    public List<AgentCertificateDobj> getSelectedCCRegnCertDobj1() {
        return selectedCCRegnCertDobj1;
    }

    /**
     * @param selectedCCRegnCertDobj1 the selectedCCRegnCertDobj1 to set
     */
    public void setSelectedCCRegnCertDobj1(List<AgentCertificateDobj> selectedCCRegnCertDobj1) {
        this.selectedCCRegnCertDobj1 = selectedCCRegnCertDobj1;
    }
}
