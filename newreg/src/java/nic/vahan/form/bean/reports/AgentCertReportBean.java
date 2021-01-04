/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.reports.AgentCertificateDobj;
import nic.vahan.form.impl.AgentCertificateImpl;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author NIC
 */
@ManagedBean(name = "AgentCertReportBean")
@ViewScoped
public class AgentCertReportBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(AgentCertReportBean.class);
    private AgentCertificateDobj dobj = null;
    private List<AgentCertificateDobj> selectedAgentCertlist = new ArrayList<>();

    public AgentCertReportBean() {
        Map map;
        map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        List<AgentCertificateDobj> agentCertPrintlist = (ArrayList<AgentCertificateDobj>) map.get("listPrintAgentCert");
        map.remove("listPrintAgentCert");
        try {
            for (int i = 0; i < agentCertPrintlist.size(); i++) {
                AgentCertificateDobj dobjComCarRC = AgentCertificateImpl.getAgentReportDobj(agentCertPrintlist.get(i).getAgent_licence_no(), agentCertPrintlist.get(i).getState_cd(), agentCertPrintlist.get(i).getOff_cd());
                if (dobjComCarRC != null) {
                    selectedAgentCertlist.add(dobjComCarRC);
                }
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

    /**
     * @return the dobj
     */
    public AgentCertificateDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(AgentCertificateDobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the selectedAgentCertlist
     */
    public List<AgentCertificateDobj> getSelectedAgentCertlist() {
        return selectedAgentCertlist;
    }

    /**
     * @param selectedAgentCertlist the selectedAgentCertlist to set
     */
    public void setSelectedAgentCertlist(List<AgentCertificateDobj> selectedAgentCertlist) {
        this.selectedAgentCertlist = selectedAgentCertlist;
    }
}
