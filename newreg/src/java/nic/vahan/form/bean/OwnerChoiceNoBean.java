/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.TableConstants;
import static nic.vahan.form.bean.fancy.AdvanceRegnFeeBean.sanitizationNumberPart;
import static nic.vahan.form.bean.fancy.AdvanceRegnFeeBean.sanitizationRegnNo;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Regn_series_dobj;
import nic.vahan.form.impl.OwnerChoiceNoImpl;
import nic.vahan.form.impl.RegnSeriesImpl;
import nic.vahan.form.impl.fancy.AdvanceRegnFeeImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author DELL
 */
@ManagedBean(name = "ownerChoiceNoBean")
@ViewScoped
public class OwnerChoiceNoBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(OwnerChoiceNoBean.class);
    private String applNo;
    private String prefixRegnSeries;
    private String sufixRegnNo;
    private String regnNo;
    private int regnChoiceAmount = 0;
    private Owner_dobj ownerDobj;
    private SessionVariables sessionVariables = null;
    private boolean renderBookedChoiceNoBtn = false;
    private boolean renderSuccessChoiceDialog = false;
    private List<Regn_series_dobj> regnList;
    private boolean renderClearChoiceNo = false;
    private String choiceRegnNoMsg;
    private String vehicleRegnGenType = "-1";
    private boolean renderChoiceNopanel = false;

    public OwnerChoiceNoBean() {
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null
                    || sessionVariables.getStateCodeSelected() == null
                    || sessionVariables.getOffCodeSelected() == 0
                    || sessionVariables.getEmpCodeLoggedIn() == null
                    || sessionVariables.getUserCatgForLoggedInUser() == null) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            }
        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, e.getMessage(), e.getMessage()));
        }
    }

    public void bookRegistrationMark() {
        try {
            if (ownerDobj != null && ownerDobj.isAdvanceOrRetenNoSelected()) {
                throw new VahanException("As Advance/Reten no. has been selected so, Please select another option.");
            }
            boolean isChoiceNoTaken = new OwnerChoiceNoImpl().saveChoiceRegistrationNo(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), regnNo, applNo, regnChoiceAmount, sessionVariables.getEmpCodeLoggedIn());
            if (isChoiceNoTaken) {
                renderClearChoiceNo = true;
                renderBookedChoiceNoBtn = false;
                renderSuccessChoiceDialog = true;
                renderChoiceNopanel = true;
                choiceRegnNoMsg = "As per selection the Choice Registration No  <b>" + regnNo + "</b> reserved against Application No " + applNo;
                PrimeFaces.current().ajax().update("workbench_tabview:op_show_panel_success");
                PrimeFaces.current().executeScript("PF('successDlgChoiceVar').show();");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Unable to assign the Choice Number, Please try again.", "Unable to assign the Choice Number, Please try again."));
            }
        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, e.getMessage(), e.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void choiceAvailableNumbers() {
        try {
            regnList = new OwnerChoiceNoImpl().getAllAvilableRegistrationNos(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), prefixRegnSeries, ownerDobj);
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ve.getMessage(), ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void clearChoiceNumber() {
        try {
            boolean isClearChoiceNo = new OwnerChoiceNoImpl().clearSelectedChoiceNumber(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), sessionVariables.getEmpCodeLoggedIn(), applNo);
            if (isClearChoiceNo) {
                renderBookedChoiceNoBtn = false;
                renderClearChoiceNo = false;
                sufixRegnNo = "";
                vehicleRegnGenType = "-1";
                renderSuccessChoiceDialog = true;
                regnChoiceAmount = 0;
                renderChoiceNopanel = false;
                if (regnList != null && !regnList.isEmpty()) {
                    regnList.clear();
                }
                choiceRegnNoMsg = "Choice Registration No Removed against Application No." + applNo;
                PrimeFaces.current().ajax().update("workbench_tabview:op_show_panel_success");
                PrimeFaces.current().executeScript("PF('successDlgChoiceVar').show();");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Unable to clear choice Number.", "Unable to clear choice Number."));
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ve.getMessage(), ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void choiceSelectionActionListener() {
        try {
            if ("P".equals(vehicleRegnGenType)) {
                if (ownerDobj != null && ownerDobj.isAdvanceOrRetenNoSelected()) {
                    vehicleRegnGenType = "-1";
                    throw new VahanException("As Advance/Reten no. has been selected so, Please select another option.");
                }
                String dealerCd = ServerUtil.getDealerCode(Long.parseLong(sessionVariables.getEmpCodeLoggedIn()), sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                if (!CommonUtils.isNullOrBlank(dealerCd)) {
                    String isValidFortakeChoiceNo = OwnerChoiceNoImpl.checkPaymentMadeForChoiceAppl(dealerCd, sessionVariables.getStateCodeSelected());
                    if (CommonUtils.isNullOrBlank(isValidFortakeChoiceNo)) {
                        renderChoiceNopanel = true;
                        regnList = new OwnerChoiceNoImpl().getAllAvilableRegistrationNos(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), prefixRegnSeries, ownerDobj);
                    } else {
                        vehicleRegnGenType = "-1";
                        renderChoiceNopanel = false;
                        throw new VahanException("As Payment has not been made for the taken Choice No against Application No. (" + isValidFortakeChoiceNo + "), first make the payment then take another Choice No.");
                    }
                } else {
                    throw new VahanException("Problem occurred while getting dealer details.");
                }
            } else {
                if (regnList != null) {
                    regnList.clear();
                }
                renderBookedChoiceNoBtn = false;
                renderChoiceNopanel = false;
                sufixRegnNo = "";
                regnChoiceAmount = 0;
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ve.getMessage(), ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

//    public void renderSuccessDialogDisable() {
//        renderSuccessChoiceDialog = false;
//        if (regnList != null) {
//            regnList.clear();
//        }
//    }
    public void validateSelectedChoiceNo(String selectedChoiceNo) {
        renderBookedChoiceNoBtn = false;
        try {
            if (!CommonUtils.isNullOrBlank(selectedChoiceNo)) {
                sufixRegnNo = selectedChoiceNo;
            }
            if (CommonUtils.isNullOrBlank(sufixRegnNo)) {
                throw new VahanException("Please Enter Choice Registration Number");
            }
            if (sanitizationNumberPart(sufixRegnNo).equalsIgnoreCase("0000")) {
                throw new VahanException("Choice Registration Number not valid.");
            }
            regnNo = sanitizationRegnNo(prefixRegnSeries, sufixRegnNo);
            sufixRegnNo = sanitizationNumberPart(sufixRegnNo);
            if (ownerDobj != null && ownerDobj.isAdvanceOrRetenNoSelected()) {
                throw new VahanException("As Advance/Reten no has been selected so, Please select another option.");
            }
            boolean isFancyNo = new RegnSeriesImpl().isRunningNoIsFancyNo(sufixRegnNo, sessionVariables.getStateCodeSelected());
            if (!isFancyNo) {
                boolean isValidTakeRegno = AdvanceRegnFeeImpl.verifyFancyRegnNo(regnNo, sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected(), String.valueOf(ownerDobj.getVehType()), String.valueOf(ownerDobj.getVh_class()), applNo, ownerDobj.getPmt_type());
                if (isValidTakeRegno) {
                    boolean isRegnoBooked = AdvanceRegnFeeImpl.isNumberBooked(regnNo);
                    if (!isRegnoBooked) {
                        regnChoiceAmount = OwnerChoiceNoImpl.getAmountForRegistrationNo(ownerDobj, regnNo, sessionVariables.getStateCodeSelected());
                        if (regnChoiceAmount > 0) {
                            renderBookedChoiceNoBtn = true;
                            choiceRegnNoMsg = "Registration Mark <b>(" + regnNo + ")</b> is available for booking with the amount of <b>Rs. " + regnChoiceAmount + " /-</b>";
                            PrimeFaces.current().ajax().update("workbench_tabview:op_show_msg_panel");
                            PrimeFaces.current().executeScript("PF('msgDlgChoiceVar').show();");
                        } else {
                            throw new VahanException("Choice Number fee not Configure/Allowed for this vehicle.");
                        }
                    } else {
                        throw new VahanException("Registration Mark (" + regnNo + ") already booked.");
                    }
                }
            } else {
                throw new VahanException("Registration no is fancy.");
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, ve.getMessage(), ve.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    /**
     * @return the applNo
     */
    public String getApplNo() {
        return applNo;
    }

    /**
     * @param applNo the applNo to set
     */
    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    /**
     * @return the sufixRegnNo
     */
    public String getSufixRegnNo() {
        return sufixRegnNo;
    }

    /**
     * @param sufixRegnNo the sufixRegnNo to set
     */
    public void setSufixRegnNo(String sufixRegnNo) {
        this.sufixRegnNo = sufixRegnNo;
    }

    /**
     * @return the regnNo
     */
    public String getRegnNo() {
        return regnNo;
    }

    /**
     * @param regnNo the regnNo to set
     */
    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    /**
     * @return the ownerDobj
     */
    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    /**
     * @param ownerDobj the ownerDobj to set
     */
    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    /**
     * @return the regnChoiceAmount
     */
    public int getRegnChoiceAmount() {
        return regnChoiceAmount;
    }

    /**
     * @param regnChoiceAmount the regnChoiceAmount to set
     */
    public void setRegnChoiceAmount(int regnChoiceAmount) {
        this.regnChoiceAmount = regnChoiceAmount;
    }

    /**
     * @return the renderSuccessChoiceDialog
     */
    public boolean isRenderSuccessChoiceDialog() {
        return renderSuccessChoiceDialog;
    }

    /**
     * @param renderSuccessChoiceDialog the renderSuccessChoiceDialog to set
     */
    public void setRenderSuccessChoiceDialog(boolean renderSuccessChoiceDialog) {
        this.renderSuccessChoiceDialog = renderSuccessChoiceDialog;
    }

    /**
     * @return the regnList
     */
    public List<Regn_series_dobj> getRegnList() {
        return regnList;
    }

    /**
     * @param regnList the regnList to set
     */
    public void setRegnList(List<Regn_series_dobj> regnList) {
        this.regnList = regnList;
    }

    /**
     * @return the renderClearChoiceNo
     */
    public boolean isRenderClearChoiceNo() {
        return renderClearChoiceNo;
    }

    /**
     * @param renderClearChoiceNo the renderClearChoiceNo to set
     */
    public void setRenderClearChoiceNo(boolean renderClearChoiceNo) {
        this.renderClearChoiceNo = renderClearChoiceNo;
    }

    /**
     * @return the renderBookedChoiceNoBtn
     */
    public boolean isRenderBookedChoiceNoBtn() {
        return renderBookedChoiceNoBtn;
    }

    /**
     * @param renderBookedChoiceNoBtn the renderBookedChoiceNoBtn to set
     */
    public void setRenderBookedChoiceNoBtn(boolean renderBookedChoiceNoBtn) {
        this.renderBookedChoiceNoBtn = renderBookedChoiceNoBtn;
    }

    /**
     * @return the choiceRegnNoMsg
     */
    public String getChoiceRegnNoMsg() {
        return choiceRegnNoMsg;
    }

    /**
     * @param choiceRegnNoMsg the choiceRegnNoMsg to set
     */
    public void setChoiceRegnNoMsg(String choiceRegnNoMsg) {
        this.choiceRegnNoMsg = choiceRegnNoMsg;
    }

    /**
     * @return the vehicleRegnGenType
     */
    public String getVehicleRegnGenType() {
        return vehicleRegnGenType;
    }

    /**
     * @param vehicleRegnGenType the vehicleRegnGenType to set
     */
    public void setVehicleRegnGenType(String vehicleRegnGenType) {
        this.vehicleRegnGenType = vehicleRegnGenType;
    }

    /**
     * @return the renderChoiceNopanel
     */
    public boolean isRenderChoiceNopanel() {
        return renderChoiceNopanel;
    }

    /**
     * @param renderChoiceNopanel the renderChoiceNopanel to set
     */
    public void setRenderChoiceNopanel(boolean renderChoiceNopanel) {
        this.renderChoiceNopanel = renderChoiceNopanel;
    }

    /**
     * @return the prefixRegnSeries
     */
    public String getPrefixRegnSeries() {
        return prefixRegnSeries;
    }

    /**
     * @param prefixRegnSeries the prefixRegnSeries to set
     */
    public void setPrefixRegnSeries(String prefixRegnSeries) {
        this.prefixRegnSeries = prefixRegnSeries;
    }
}
