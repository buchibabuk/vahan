/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import nic.vahan.form.dobj.NoOfDuesCerificateDobj;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Administrator
 */
@SessionScoped
@ManagedBean(name = "noOfDuesCerificateBean")
public class NoOfDuesCerificateBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(NoOfDuesCerificateBean.class);
    private String regn_no;
    private NoOfDuesCerificateDobj dobj = new NoOfDuesCerificateDobj();
    PrintDocImpl printImpl = null;

    public NoOfDuesCerificateBean() {
    }

    public String printReport() {
        String redirect = "";
        try {
            printImpl = new PrintDocImpl();
            dobj = printImpl.getDuesCerificateDetails(regn_no);
            if (dobj != null) {
                dobj.setRcpt_heading(ServerUtil.getRcptHeading());
                dobj.setRcpt_sub_heading(ServerUtil.getRcptSubHeading());
                redirect = "/ui/reports/formNoDuesCertificateReport.xhtml?faces-redirect=true";
                setRegn_no("");
            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Details Not Found for Registration No." + regn_no + ""));
                redirect = "";
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Error in fetching data!"));
        }
        return redirect;
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
     * @return the dobj
     */
    public NoOfDuesCerificateDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(NoOfDuesCerificateDobj dobj) {
        this.dobj = dobj;
    }
}
