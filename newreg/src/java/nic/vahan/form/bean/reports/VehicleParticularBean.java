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
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.reports.VehicleParticularDobj;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.permit.PrintPermitDocInXhtmlImpl;
import org.apache.log4j.Logger;
import nic.vahan.server.CommonUtils;
import org.primefaces.PrimeFaces;

/**
 *
 * @author nic
 */
@ManagedBean(name = "vehicleParticularBean")
@RequestScoped
public class VehicleParticularBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(VehicleParticularBean.class);
    private String regn_no;
    private String applno;
    private int purCd;
    private VehicleParticularDobj dobj = null;
    private List<PassengerPermitDetailDobj> routedata = null;
    private String text;
    private PrintPermitDocInXhtmlImpl permitImpl = null;
    private boolean isKAState;
    private String returnURL = "home";
    private String vahanMessages = null;
    private SessionVariables sessionVariables = null;
    private boolean renderqrCode = false;

    public VehicleParticularBean() {
        try {

            sessionVariables = new SessionVariables();
            if (sessionVariables == null || CommonUtils.isNullOrBlank(sessionVariables.getStateCodeSelected())
                    || sessionVariables.getOffCodeSelected() == 0 || sessionVariables.getActionCodeSelected() == 0) {
                vahanMessages = "Session time Out";
                return;
            }
            Map map;
            map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            String map_regn_no = (String) map.get("veh_par_regn_no");
            String map_appl_no = (String) map.get("veh_par_appl_no");
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("veh_par_regn_no");
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("veh_par_appl_no");
            if ("KA".contains(sessionVariables.getStateCodeSelected())) {
                setIsKAState(true);
            }

            permitImpl = new PrintPermitDocInXhtmlImpl();
            dobj = new VehicleParticularDobj();
            routedata = new ArrayList<>();
            if (map_regn_no == null || "".contains(map_regn_no)) {
                regn_no = sessionVariables.getSelectedWork().getRegn_no();
                applno = sessionVariables.getSelectedWork().getAppl_no();
            } else {
                regn_no = map_regn_no;
                applno = map_appl_no;
                setReturnURL("/ui/reports/formRePrintVehParticular.xhtml");
            }

            dobj = PrintDocImpl.getVehicleParticularDobj(sessionVariables, regn_no, applno);
            if (dobj != null) {
                if (TableConstants.VT_NOC_ISSUE_STATUS.equalsIgnoreCase(dobj.getVchStatus())) {
                    dobj.setVchStatusDescr("NOC ISSUED");
                } else if (TableConstants.VT_RC_SURRENDER_STATUS.equalsIgnoreCase(dobj.getVchStatus())) {
                    dobj.setVchStatusDescr("RC SURRENDERED");
                } else if (TableConstants.VT_RC_CANCEL_STATUS.equalsIgnoreCase(dobj.getVchStatus())) {
                    dobj.setVchStatusDescr("RC CANCELLED");
                } else if (TableConstants.VT_SCRAP_VEHICLE_STATUS.equalsIgnoreCase(dobj.getVchStatus())) {
                    dobj.setVchStatusDescr("VEHICLE SCRAPPED");
                } else {
                    dobj.setVchStatusDescr("ACTIVE");
                }
                if (dobj.getPassPmtDobj() != null) {
                    setRoutedata(permitImpl.getRouteData(dobj.getPassPmtDobj().getApplNo(), dobj.getPassPmtDobj().getState_cd(), Integer.valueOf(dobj.getPassPmtDobj().getOff_cd()), TableList.vt_permit_route, false));
                } else {
                    setRoutedata(null);
                }
                if (dobj.getRegn_no() != null && !dobj.getRegn_no().isEmpty() && dobj.getChassis_no() != null && !dobj.getChassis_no().isEmpty()) {
                    text = "Regn. No." + dobj.getRegn_no() + " Chassis No." + dobj.getChassis_no();
                    renderqrCode = true;
                } else {
                    renderqrCode = false;
                }
            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Details not found!"));
                return;
            }

        } catch (VahanException e) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", e.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

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
     * @return the applno
     */
    public String getApplno() {
        return applno;
    }

    /**
     * @param applno the applno to set
     */
    public void setApplno(String applno) {
        this.applno = applno;
    }

    /**
     * @return the purCd
     */
    public int getPurCd() {
        return purCd;
    }

    /**
     * @param purCd the purCd to set
     */
    public void setPurCd(int purCd) {
        this.purCd = purCd;
    }

    /**
     * @return the dobj
     */
    public VehicleParticularDobj getDobj() {
        return dobj;
    }

    /**
     * @param dobj the dobj to set
     */
    public void setDobj(VehicleParticularDobj dobj) {
        this.dobj = dobj;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    public List<PassengerPermitDetailDobj> getRoutedata() {
        return routedata;
    }

    public void setRoutedata(List<PassengerPermitDetailDobj> routedata) {
        this.routedata = routedata;
    }

    /**
     * @return the isKAState
     */
    public boolean isIsKAState() {
        return isKAState;
    }

    /**
     * @param isKAState the isKAState to set
     */
    public void setIsKAState(boolean isKAState) {
        this.isKAState = isKAState;
    }

    /**
     * @return the returnURL
     */
    public String getReturnURL() {
        return returnURL;
    }

    /**
     * @param returnURL the returnURL to set
     */
    public void setReturnURL(String returnURL) {
        this.returnURL = returnURL;
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
     * @return the renderqrCode
     */
    public boolean isRenderqrCode() {
        return renderqrCode;
    }

    /**
     * @param renderqrCode the renderqrCode to set
     */
    public void setRenderqrCode(boolean renderqrCode) {
        this.renderqrCode = renderqrCode;
    }
}
