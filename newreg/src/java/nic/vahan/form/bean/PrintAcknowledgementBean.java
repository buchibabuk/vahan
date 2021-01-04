/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.FitnessDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.common.RegistrationStatusParametersDobj;
import nic.vahan.form.dobj.permit.PermitPaidFeeDtlsDobj;
import nic.vahan.form.dobj.tax.TaxDobj;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.PermitPaidFeeDtlsImpl;
import nic.vahan.form.impl.tax.TaxImpl;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Sumit Gupta
 */
@ViewScoped
@ManagedBean(name = "regnPrintBean")
public class PrintAcknowledgementBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(PrintAcknowledgementBean.class);
    private OwnerDetailsDobj ownerDetail;
    private String regn_no;
    private String stateCd;
    private int offCd;
    private String status;
    private boolean showStatus = false;
    private boolean render = true;
    private String searchByValue = "regnNo";
    private String engineNo;
    private String chassisNo;
    private boolean gaRender = false;
    private String regnNo = "";
    private String regnDt = "";
    private String backButton;
    HttpSession ses = Util.getSession();

    public PrintAcknowledgementBean() {
        try {
            stateCd = Util.getUserStateCode();
            if (stateCd == null) {
                //any custome message;
                return;
            }
            offCd = Util.getSelectedSeat().getOff_cd();
            regnNo = (String) ses.getAttribute("regn_no");
            if (!CommonUtils.isNullOrBlank(regnNo)) {
                render = false;
                gaRender = true;
                backButton = "home";
                showDetails(regnNo, stateCd, offCd);
            } else if (CommonUtils.isNullOrBlank(regnNo)) {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/vahan/vahan/home.xhtml");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", TableConstants.SomthingWentWrong));
        }
    }

    public void showAllRegnNosForm() {
        if (this.regn_no == null || this.regn_no.trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Please Enter Valid Registration Number"));
            return;
        }
        try {
            showDetails(regn_no, stateCd, offCd);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            //facessmess
        }
    }

    public void showDetails(String regnNo, String stateCd, int offCd) {
        Utility utility = new Utility();
        try {
            OwnerImpl owner_Impl = new OwnerImpl();
            if (!CommonUtils.isNullOrBlank(regnNo)) {
                ownerDetail = owner_Impl.getOwnerDetails(regnNo, stateCd, offCd);
                regnDt = ownerDetail.getRegnDateDescr();

            }
            if (ownerDetail == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Registration Number Not Found"));
                return;
            }
            render = false;
            gaRender = true;
            ses.removeAttribute("regn_no");


        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ve.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", TableConstants.SomthingWentWrong));
        }
    }

    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isShowStatus() {
        return showStatus;
    }

    public void setShowStatus(boolean showStatus) {
        this.showStatus = showStatus;
    }

    public boolean isRender() {
        return render;
    }

    public void setRender(boolean render) {
        this.render = render;
    }

    public String getSearchByValue() {
        return searchByValue;
    }

    public void setSearchByValue(String searchByValue) {
        this.searchByValue = searchByValue;
    }

    public String getEngineNo() {
        return engineNo;
    }

    public void setEngineNo(String engineNo) {
        this.engineNo = engineNo;
    }

    public String getChassisNo() {
        return chassisNo;
    }

    public void setChassisNo(String chassisNo) {
        this.chassisNo = chassisNo;
    }

    public boolean isGaRender() {
        return gaRender;
    }

    public void setGaRender(boolean gaRender) {
        this.gaRender = gaRender;
    }

    public String getBackButton() {
        return backButton;
    }

    public void setBackButton(String backButton) {
        this.backButton = backButton;
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public static void setLOGGER(Logger aLOGGER) {
        LOGGER = aLOGGER;
    }

    public String getRegnDt() {
        return regnDt;
    }

    public void setRegnDt(String regnDt) {
        this.regnDt = regnDt;
    }
}
