/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.admin;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.form.dobj.UpdateTaxNoticeHeadsDobj;
import nic.vahan.form.impl.admin.UpdateTaxNoticeHeadsImpl;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author afzal
 */
@ManagedBean(name = "updateTNHeads")
@ViewScoped
public class UpdateTaxNoticeHeadsBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(UpdateTaxNoticeHeadsBean.class);
    private String headValue;
    private String radiobtn = "";
    private String vahanMessages = null;
    private SessionVariables sessionVariables = null;
    private String headLabel;

    @PostConstruct
    public void init() {
        sessionVariables = new SessionVariables();
        if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                || sessionVariables.getActionCodeSelected() == 0) {
            setVahanMessages("Session time Out");
            return;
        }
        try {
            setRadiobtn("H1");
            setHeadLabel("Head1");
            UpdateTaxNoticeHeadsImpl TaxHeadimpl = new UpdateTaxNoticeHeadsImpl();;
            UpdateTaxNoticeHeadsDobj dobj = TaxHeadimpl.getHeadDetails(sessionVariables.getStateCodeSelected(), getHeadLabel());
            if (dobj == null || dobj.getHead().isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "There is no record found !!"));
                return;
            } else {
                setHeadValue(dobj.getHead());
            }
        } catch (Exception e) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Something went wrong, Please try again."));
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void getHeadValueListener() {
        String radioBtnvalue = getRadiobtn();
        UpdateTaxNoticeHeadsImpl TaxHeadimpl = null;
        try {
            TaxHeadimpl = new UpdateTaxNoticeHeadsImpl();
            if (radioBtnvalue.equalsIgnoreCase("H1")) {
                setRadiobtn("H1");
                setHeadLabel("Head1");
            } else if (radioBtnvalue.equalsIgnoreCase("H2")) {
                setRadiobtn("H2");
                setHeadLabel("Head2");
            } else if (radioBtnvalue.equalsIgnoreCase("H3")) {
                setRadiobtn("H3");
                setHeadLabel("Head3");
            } else if (radioBtnvalue.equalsIgnoreCase("H4")) {
                setRadiobtn("H4");
                setHeadLabel("Head4");
            } else if (radioBtnvalue.equalsIgnoreCase("H5")) {
                setRadiobtn("H5");
                setHeadLabel("Head5");
            }
            UpdateTaxNoticeHeadsDobj dobj = TaxHeadimpl.getHeadDetails(sessionVariables.getStateCodeSelected(), getHeadLabel());
            if (dobj != null & dobj.getHead().isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "There is no record found !!"));
                return;
            } else {
                setHeadValue(dobj.getHead());
            }
        } catch (Exception e) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Something went wrong, Please try again."));
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void updateHead() {
        UpdateTaxNoticeHeadsImpl TaxHeadimpl = null;
        try {
            TaxHeadimpl = new UpdateTaxNoticeHeadsImpl();
            boolean status = TaxHeadimpl.updateHeadDetails(sessionVariables.getStateCodeSelected(), getHeadLabel(), getHeadValue());
            if (status) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Record Updated Successfully !!"));
            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "There are some problem in updating records !!"));
            }
        } catch (Exception e) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Something went wrong, Please try again."));
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void confirmSaveHead() {
        if (getHeadValue() == null || getHeadValue().trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Head value should not be Blank !!"));
            return;
        }
        //RequestContext ca = RequestContext.getCurrentInstance();
        PrimeFaces.current().executeScript("PF('saveHead').show()");
    }

    /**
     * @return the headValue
     */
    public String getHeadValue() {
        return headValue;
    }

    /**
     * @param headValue the headValue to set
     */
    public void setHeadValue(String headValue) {
        this.headValue = headValue;
    }

    /**
     * @return the radiobtn
     */
    public String getRadiobtn() {
        return radiobtn;
    }

    /**
     * @param radiobtn the radiobtn to set
     */
    public void setRadiobtn(String radiobtn) {
        this.radiobtn = radiobtn;
    }

    /**
     * @return the headLabel
     */
    public String getHeadLabel() {
        return headLabel;
    }

    /**
     * @param headLabel the headLabel to set
     */
    public void setHeadLabel(String headLabel) {
        this.headLabel = headLabel;
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
}
