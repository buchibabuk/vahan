/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.reports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.HsrpPrintDobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.HsrpPrintImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nic
 */
@ManagedBean(name = "hsrpprintBean")
@ViewScoped
public class HsrpPrintBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(HsrpPrintBean.class);
    private String applno;
    private String regnno;
    private boolean isApprovedRegnNo = false;
    private List<HsrpPrintBean> filteredSeat = null;
    private String indexValue = "";
    private List<HsrpPrintDobj> printCertDobj = new ArrayList<HsrpPrintDobj>();
    private boolean hsrpApprovedApplPanel;
    private boolean ntApprovedHsrpDataTable = false;

    public void setListBeans(List<HsrpPrintDobj> listDobjs) {
        setPrintCertDobj(listDobjs);
    }

    @PostConstruct
    public void init() {
        try {
            HttpSession session = Util.getSession();
            if (session == null || session.getAttribute("user_catg") == null) {
                throw new VahanException("Error in Getting HSRP details,Please try again ");
            }
            // String user_catg = (String) session.getAttribute("user_catg");
            //  if (!user_catg.equals(TableConstants.USER_CATG_DEALER)) {
            hsrpApprovedApplPanel = true;
            //}
            //this.setListBeans(HsrpPrintImpl.getPurCdPrintDocsDetails());
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

    public String confirmprintCertificate() {
        boolean isApprove = false;
        Map map;
        map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        if ((String) map.get("applno") != null) {
            applno = (String) map.get("applno");
            if (map.get("approveStatus") != null) {
                isApprove = Boolean.parseBoolean(String.valueOf(map.get("approveStatus")));
                setIsApprovedRegnNo(isApprove);
            }
        } else {
            if (applno.trim().equalsIgnoreCase("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Application No should not be Blank!"));
                return "";

            }
            HsrpPrintImpl hsrp_Impl;
            try {
                hsrp_Impl = new HsrpPrintImpl();
                String status = hsrp_Impl.isApplExistForNewRegistration(applno.trim());
                if (status == null) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Application does not exist or you are not authorized to print HSRP Slip for this application.!"));
                    return "";
                } else if (status.equals("A")) {
                    isApprove = true;
                } else {
                    isApprove = false;
                }
                applno = this.applno;

                if (Util.getUserCategory() != null && Util.getUserCategory().equals(TableConstants.USER_CATG_DEALER)) {
                    ArrayList<Status_dobj> statusDobj = ServerUtil.applicationStatusByApplNo(applno, Util.getUserStateCode());
                    if (statusDobj != null && !statusDobj.isEmpty()) {
                        setIsApprovedRegnNo(!isApprove);
                    } else {
                        setIsApprovedRegnNo(isApprove);
                    }
                } else {
                    setIsApprovedRegnNo(isApprove);
                }
            } catch (VahanException ve) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
                return "";
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
                return "";
            }
        }
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("applno", applno);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("isApproved", isIsApprovedRegnNo());
        return "HSRPAuthorizationReport";
    }

    /**
     * @return the appl_no
     */
    public String getApplno() {
        return applno;
    }

    /**
     * @param appl_no the applno to set
     */
    public void setApplno(String applno) {
        this.applno = applno;
    }

    /**
     * @return the indexValue
     */
    public String getIndexValue() {
        return indexValue;
    }

    /**
     * @param indexValue the indexValue to set
     */
    public void setIndexValue(String indexValue) {
        this.indexValue = indexValue;
    }

    /**
     * @return the filteredSeat
     */
    public List<HsrpPrintBean> getFilteredSeat() {
        return filteredSeat;
    }

    /**
     * @param filteredSeat the filteredSeat to set
     */
    public void setFilteredSeat(List<HsrpPrintBean> filteredSeat) {
        this.filteredSeat = filteredSeat;
    }

    /**
     * @return the printCertDobj
     */
    public List<HsrpPrintDobj> getPrintCertDobj() {
        return printCertDobj;
    }

    /**
     * @param printCertDobj the printCertDobj to set
     */
    public void setPrintCertDobj(List<HsrpPrintDobj> printCertDobj) {
        this.printCertDobj = printCertDobj;
    }

    /**
     * @return the isApprovedRegnNo
     */
    public boolean isIsApprovedRegnNo() {
        return isApprovedRegnNo;
    }

    /**
     * @param isApprovedRegnNo the isApprovedRegnNo to set
     */
    public void setIsApprovedRegnNo(boolean isApprovedRegnNo) {
        this.isApprovedRegnNo = isApprovedRegnNo;
    }

    /**
     * @return the regnno
     */
    public String getRegnno() {
        return regnno;
    }

    /**
     * @param regnno the regnno to set
     */
    public void setRegnno(String regnno) {
        this.regnno = regnno;
    }

    /**
     * @return the hsrpApprovedApplPanel
     */
    public boolean isHsrpApprovedApplPanel() {
        return hsrpApprovedApplPanel;
    }

    /**
     * @param hsrpApprovedApplPanel the hsrpApprovedApplPanel to set
     */
    public void setHsrpApprovedApplPanel(boolean hsrpApprovedApplPanel) {
        this.hsrpApprovedApplPanel = hsrpApprovedApplPanel;
    }

    /**
     * @return the ntApprovedHsrpDataTable
     */
    public boolean isNtApprovedHsrpDataTable() {
        return ntApprovedHsrpDataTable;
    }

    /**
     * @param ntApprovedHsrpDataTable the ntApprovedHsrpDataTable to set
     */
    public void setNtApprovedHsrpDataTable(boolean ntApprovedHsrpDataTable) {
        this.ntApprovedHsrpDataTable = ntApprovedHsrpDataTable;
    }
}
