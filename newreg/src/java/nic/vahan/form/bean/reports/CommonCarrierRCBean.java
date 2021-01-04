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
import nic.vahan.form.dobj.reports.CommonCarrierRCDobj;
import nic.vahan.form.impl.CommonCarrierRCImpl;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author NIC
 */
@ManagedBean(name = "CommonCarrierRegnCertBean")
@ViewScoped
public class CommonCarrierRCBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(CommonCarrierRCBean.class);
    private List<CommonCarrierRCDobj> printCCRegnCertDobj = new ArrayList<CommonCarrierRCDobj>();
    private List<CommonCarrierRCDobj> selectedCCRegnCertDobj = new ArrayList<CommonCarrierRCDobj>();
    private List<CommonCarrierRCDobj> filteredSeat = null;
    private SessionVariables sessionVariables;
    private String vahanMessages = null;
    private String appl_no;
    private String regn_no;
    private String rcRadiobtn = "";
    private boolean printCCRCbyRegnNo = true;

    public void setListBeans(List<CommonCarrierRCDobj> listCCRegnCertDobj) {
        setPrintCCRegnCertDobj(listCCRegnCertDobj);
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
            setRcRadiobtn("REGNNOCCRC");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

    public void rcRadioBtnListener() {
        getPrintCCRegnCertDobj().clear();
        if (selectedCCRegnCertDobj != null) {
            selectedCCRegnCertDobj.clear();
        }
        String radioBtnvalue = rcRadiobtn;
        try {
            if (radioBtnvalue.equalsIgnoreCase("PENCCRC")) {
                setPrintCCRCbyRegnNo(false);
                this.regn_no = "";
                this.appl_no = "";
                ArrayList<CommonCarrierRCDobj> list = CommonCarrierRCImpl.getCommonCarrierRegnCertList(getSessionVariables());
                if (list.isEmpty()) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "There is no record found !!!"));
                    return;
                } else {
                    this.setListBeans(list);
                }

            } else if (radioBtnvalue.equalsIgnoreCase("REGNNOCCRC")) {
                setPrintCCRCbyRegnNo(true);
            }

        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", ve.getMessage()));
        } catch (Exception e) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Something went wrong, Please try again. !!!"));
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void getCCRCDetails() {
        getPrintCCRegnCertDobj().clear();
        if (selectedCCRegnCertDobj != null) {
            selectedCCRegnCertDobj.clear();
        }
        try {
            if ((regn_no.trim().equalsIgnoreCase("") && getAppl_no().trim().equalsIgnoreCase("")) || (regn_no.trim().equalsIgnoreCase("") && getAppl_no().trim().equalsIgnoreCase(""))) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration / Application No should not be Blank !!!"));
                return;
            }
            ArrayList<CommonCarrierRCDobj> list = CommonCarrierRCImpl.getCommonCarrierRegnCertListByRenoNoWise(getSessionVariables(), regn_no);
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

    public void confirmPrintCCRegnCertificate() {
        //RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('printCCRCertificate').show()");
    }

    public String printCCRegnCertificate() {
        if (selectedCCRegnCertDobj == null || selectedCCRegnCertDobj.size() < 1) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Please Select Atleast One Registration No For Printing !!!"));
            return "";
        }
        if (selectedCCRegnCertDobj.size() > 8) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Increase Bulk Printing Limit, Maximum 8 Common Carries RC Allowed !!!"));
            return "";
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("listPrintCCRC", selectedCCRegnCertDobj);
        return "PrintCommonCarriesRCReport";
    }

    /**
     * @return the printCCRegnCertDobj
     */
    public List<CommonCarrierRCDobj> getPrintCCRegnCertDobj() {
        return printCCRegnCertDobj;
    }

    /**
     * @param printCCRegnCertDobj the printCCRegnCertDobj to set
     */
    public void setPrintCCRegnCertDobj(List<CommonCarrierRCDobj> printCCRegnCertDobj) {
        this.printCCRegnCertDobj = printCCRegnCertDobj;
    }

    /**
     * @return the selectedCCRegnCertDobj
     */
    public List<CommonCarrierRCDobj> getSelectedCCRegnCertDobj() {
        return selectedCCRegnCertDobj;
    }

    /**
     * @param selectedCCRegnCertDobj the selectedCCRegnCertDobj to set
     */
    public void setSelectedCCRegnCertDobj(List<CommonCarrierRCDobj> selectedCCRegnCertDobj) {
        this.selectedCCRegnCertDobj = selectedCCRegnCertDobj;
    }

    /**
     * @return the filteredSeat
     */
    public List<CommonCarrierRCDobj> getFilteredSeat() {
        return filteredSeat;
    }

    /**
     * @param filteredSeat the filteredSeat to set
     */
    public void setFilteredSeat(List<CommonCarrierRCDobj> filteredSeat) {
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
     * @return the printCCRCbyRegnNo
     */
    public boolean isPrintCCRCbyRegnNo() {
        return printCCRCbyRegnNo;
    }

    /**
     * @param printCCRCbyRegnNo the printCCRCbyRegnNo to set
     */
    public void setPrintCCRCbyRegnNo(boolean printCCRCbyRegnNo) {
        this.printCCRCbyRegnNo = printCCRCbyRegnNo;
    }

    /**
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }
}
