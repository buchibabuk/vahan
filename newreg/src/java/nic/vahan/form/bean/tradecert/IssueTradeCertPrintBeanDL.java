/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.tradecert;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.common.FileMovementAbstractApplBean;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.tradecert.IssueTradeCertDobj;
import nic.vahan.form.dobj.tradecert.TCPrintDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.tradecert.ApplicationTradeCertImpl;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.PrinterDocDetails;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nicsi
 */
@ManagedBean(name = "issueTradeCertPrintBeanDL")
@RequestScoped
public class IssueTradeCertPrintBeanDL extends FileMovementAbstractApplBean {

    private static final Logger LOGGER = Logger.getLogger(IssueTradeCertPrintBean.class);
    private IssueTradeCertDobj dobj = null;
    private final String currentDate;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy h:mm:ss a");
    private int purCd;
    private String tcType;
    private final String reportHeading;
    private final String reportSubHeading;
    private String outcome;
    private List selectedTcDobjList;
    private boolean tcForEachVehCatg;
    private SessionVariables sessionVariable;
    private TmConfigurationDobj tmConfigDobj;
    private boolean show_image_background;
    private String image_background;

    public IssueTradeCertPrintBeanDL() {

        sessionVariable = new SessionVariables();
        currentDate = sdf.format(new Date());
        reportHeading = ServerUtil.getRcptHeading();
        reportSubHeading = ServerUtil.getRcptSubHeading();
        String applNo = null;
        int purCd = 0;
        try {
            tmConfigDobj = Util.getTmConfiguration();
            if (tmConfigDobj.isTcNoForEachVehCatg()) {
                tcForEachVehCatg = true;
            }
        } catch (VahanException ex) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            return;
        }
        this.outcome = "/ui/tradecert/formTradeCertPrintingList.xhtml?faces-redirect=true";

        selectedTcDobjList = (List) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("selectedTcDobjList");

        if (selectedTcDobjList != null) {
            for (Object selectedTcDobjListObj : selectedTcDobjList) {
                TCPrintDobj tcPrintDobj = (TCPrintDobj) selectedTcDobjListObj;
                ///////////////////////// 
                try {
                    Map dataMap = null;
                    String tcNo = tcPrintDobj.getTcNo();
                    String vchCatgCode = tcPrintDobj.getVchCatgCode();
                    String applicationType = tcPrintDobj.getApplicationType();
                    try {
                        if (tmConfigDobj.getTmTradeCertConfigDobj().isTcPrintingDataForDealer()) { //// tc_printing_data_for_dealer work For states except Maharasthra
                            if (!CommonUtils.isNullOrBlank(applicationType) && TableConstants.TRADE_CERT_DUPLICATE_CONSTANT_VAL.equals(applicationType)) {
                                dataMap = PrinterDocDetails.getTradeCertificatePrintingFromVtForDL(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), tcNo, new ArrayList(), vchCatgCode);
                            } else {
                                dataMap = PrinterDocDetails.getTradeCertificatePrintingFromVtForDL(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), tcNo, new ArrayList());
                            }
                        } else {
                            dataMap = PrinterDocDetails.getTradeCertificatePrintingFromVt(Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd(), tcNo, false, false, tcPrintDobj.getApplicantType());
                        }
                        tcPrintDobj.setDobjSubList((List) dataMap.get("dobjSubList"));
                        applNo = (String) dataMap.get("applNo");
                        purCd = (int) dataMap.get("purCd");
                    } catch (VahanException ex) {
                        LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Exception in fetching and mapping data for trade certificate printing."));
                    }

                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Exception in printing trade certificate after data fetching and mapping."));
                }
            }
        }

        if (tmConfigDobj.getTmPrintConfgDobj() != null) {
            if (tmConfigDobj.getTmPrintConfgDobj().getImage_background() != null
                    && !tmConfigDobj.getTmPrintConfgDobj().getImage_background().isEmpty()
                    && tmConfigDobj.getTmTradeCertConfigDobj().isWatermarkReq()) {
                setImage_background(tmConfigDobj.getTmPrintConfgDobj().getImage_background());
                setShow_image_background(true);
            } else {
                setShow_image_background(false);
            }
        }

        try {
            if (ApplicationTradeCertImpl.isTradeCertificateProcessNotCompleted(applNo, purCd)) {
                Status_dobj status = new Status_dobj();
                status.setAppl_dt(DateUtils.parseDate(new java.util.Date()));
                status.setStatus(TableConstants.STATUS_COMPLETE);
                status.setAppl_no(applNo);
                status.setOffice_remark(TableConstants.TRADE_CERTIFICATE_PRINTED);
                status.setPublic_remark(TableConstants.TRADE_CERTIFICATE_PRINTED);
                status.setState_cd(Util.getUserStateCode());
                status.setPur_cd(purCd);
                status.setEmp_cd(Util.getEmpCodeLong());
                status.setCurrent_role(getAppl_details().getCurrent_role());
                status.setAction_cd(getAppl_details().getCurrent_action_cd());
                new ApplicationTradeCertImpl().fileMove(status);
            }
        } catch (VahanException ve) {
            LOGGER.error(ve.getMessage() + " " + ve.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Exception occurred during file movement."));
        }

    }

    public IssueTradeCertDobj getDobj() {
        return dobj;
    }

    public void setDobj(IssueTradeCertDobj dobj) {
        this.dobj = dobj;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public int getPurCd() {
        return purCd;
    }

    public String getTcType() {
        return tcType;
    }

    public String getReportHeading() {
        return reportHeading;
    }

    public String getReportSubHeading() {
        return reportSubHeading;
    }

    public String getOutcome() {
        return outcome;
    }

    public List getSelectedTcDobjList() {
        return selectedTcDobjList;
    }

    public void setSelectedTcDobjList(List selectedTcDobjList) {
        this.selectedTcDobjList = selectedTcDobjList;
    }

    public boolean isTcForEachVehCatg() {
        return tcForEachVehCatg;
    }

    public void setTcForEachVehCatg(boolean tcForEachVehCatg) {
        this.tcForEachVehCatg = tcForEachVehCatg;
    }

    public boolean isShow_image_background() {
        return show_image_background;
    }

    public void setShow_image_background(boolean show_image_background) {
        this.show_image_background = show_image_background;
    }

    public String getImage_background() {
        return image_background;
    }

    public void setImage_background(String image_background) {
        this.image_background = image_background;
    }
}
