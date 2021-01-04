/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

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
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableList;
import nic.vahan.form.bean.reports.VehicleParticularBean;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitHomeAuthDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.PrintPermitDocInXhtmlImpl;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author DELL
 */
@ManagedBean(name = "permitParticularBean")
@RequestScoped
public class PermitParticularBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(VehicleParticularBean.class);
    private String regn_no;
    private String applno;
    private int purCd;
    private PassengerPermitDetailDobj passPmtDobj = null;
    private PermitOwnerDetailDobj ownerPmtDobj = null;
    private PermitHomeAuthDobj homeAuthDobj = null;
    private List<PassengerPermitDetailDobj> routedata = null;
    private String text;
    private boolean isKAState;
    private String returnURL = "home";
    private String vahanMessages = null;
    private SessionVariables sessionVariables = null;
    private boolean renderqrCode = false;
    PrintPermitDocInXhtmlImpl pmtDocImpl = null;
    private String image_background;
    private String image_logo;
    private boolean show_image_background = false;
    private boolean show_image_logo = false;
    private String ruleAndSection = "";
    private String textQRcode = "";
    TmConfigurationDobj configurationDobj = null;

    public PermitParticularBean() {
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
            routedata = new ArrayList<>();
            if (map_regn_no == null || "".contains(map_regn_no)) {
                regn_no = sessionVariables.getSelectedWork().getRegn_no();
                applno = sessionVariables.getSelectedWork().getAppl_no();
            } else {
                regn_no = map_regn_no;
                applno = map_appl_no;
                //setReturnURL("/ui/reports/formRePrintVehParticular.xhtml");
            }

            pmtDocImpl = new PrintPermitDocInXhtmlImpl();
            passPmtDobj = new PassengerPermitDetailDobj();
            ownerPmtDobj = new PermitOwnerDetailDobj();
            homeAuthDobj = new PermitHomeAuthDobj();

            try {
                configurationDobj = Util.getTmConfiguration();
            } catch (VahanException e) {
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", new FacesMessage(FacesMessage.SEVERITY_INFO, "", e.getMessage()));
            }
            if (configurationDobj.getTmPrintConfgDobj() != null) {
                if (configurationDobj.getTmPrintConfgDobj().getImage_background() != null && !configurationDobj.getTmPrintConfgDobj().getImage_background().isEmpty()) {
                    setImage_background(configurationDobj.getTmPrintConfgDobj().getImage_background());
                    setShow_image_background(true);
                } else {
                    setShow_image_background(false);
                }
                if (configurationDobj.getTmPrintConfgDobj().getImage_logo() != null && !configurationDobj.getTmPrintConfgDobj().getImage_logo().isEmpty()) {
                    setImage_logo(configurationDobj.getTmPrintConfgDobj().getImage_logo());
                    setShow_image_logo(true);
                } else {
                    setShow_image_logo(false);
                }
            }

            Map<String, String> getmap = PrintDocImpl.getPermitParticularPrint(sessionVariables, regn_no);
            if (!CommonUtils.isNullOrBlank(getmap.get("region_covered"))) {
                passPmtDobj.setRegion_covered(pmtDocImpl.getRegionDetailStateVise(getmap.get("appl_no"), getmap.get("region_covered")));
            }
            if ((!CommonUtils.isNullOrBlank(getmap.get("region_covered"))) && CommonUtils.isNullOrBlank(passPmtDobj.getRegion_covered())) {
                passPmtDobj.setRegion_covered(pmtDocImpl.getRegionDetail(getmap.get("appl_no"), getmap.get("state_cd"), Integer.valueOf(getmap.get("off_cd")), TableList.VT_PERMIT));
            }


            if (CommonUtils.isNullOrBlank(getmap.get("service_type_descr")) || getmap.get("service_type_descr").equalsIgnoreCase("Select Services Type")) {
                passPmtDobj.setServices_TYPE(null);
            } else {
                passPmtDobj.setServices_TYPE(getmap.get("service_type_descr"));
            }

            passPmtDobj.setOff_cd(getmap.get("off_name"));
            passPmtDobj.setState_cd(getmap.get("state_name"));
            passPmtDobj.setPmt_no(getmap.get("pmt_no"));
            passPmtDobj.setValidFromInString(getmap.get("valid_from"));
            passPmtDobj.setValidUptoInString(getmap.get("valid_upto"));
            passPmtDobj.setGoods_TO_CARRY(getmap.get("goods_to_carry"));
            passPmtDobj.setOpDateInString(getmap.get("op_date"));
            passPmtDobj.setJoreny_PURPOSE(getmap.get("jorney_purpose"));
            passPmtDobj.setParking(getmap.get("parking"));
            if (!CommonUtils.isNullOrBlank(getmap.get("pmt_type"))) {
                passPmtDobj.setPmt_type(getmap.get("pmt_type").toUpperCase());
            }
            if (CommonUtils.isNullOrBlank(getmap.get("replace_date"))) {
                passPmtDobj.setReplaceDateInString(null);
            } else {
                passPmtDobj.setReplaceDateInString(getmap.get("replace_date").toUpperCase());
            }
            if (!CommonUtils.isNullOrBlank(getmap.get("pmt_catg"))) {
                passPmtDobj.setPmtCatg(" (" + getmap.get("pmt_catg").toUpperCase() + ")");
            }
            ownerPmtDobj.setOwner_name(getmap.get("owner_name").toUpperCase());
            ownerPmtDobj.setF_name(getmap.get("f_name").toUpperCase());
            ownerPmtDobj.setC_add1(getmap.get("c_add1").toUpperCase());
            ownerPmtDobj.setC_add2(getmap.get("c_add2").toUpperCase());
            ownerPmtDobj.setC_add3(getmap.get("c_add3").toUpperCase());
            ownerPmtDobj.setC_state(getmap.get("c_state_name").toUpperCase());
            if (CommonUtils.isNullOrBlank(getmap.get("c_pincode"))) {
                ownerPmtDobj.setC_pincode(0);
            } else {
                ownerPmtDobj.setC_pincode(Integer.parseInt(getmap.get("c_pincode")));
            }

            if (CommonUtils.isNullOrBlank(getmap.get("ld_wt"))) {
                ownerPmtDobj.setLd_wt(0);
            } else {
                ownerPmtDobj.setLd_wt(Integer.parseInt(getmap.get("c_pincode")));
            }

            if (CommonUtils.isNullOrBlank(getmap.get("unld_wt"))) {
                ownerPmtDobj.setUnld_wt(0);
            } else {
                ownerPmtDobj.setUnld_wt(Integer.parseInt(getmap.get("unld_wt")));
            }
            ownerPmtDobj.setRegn_no(getmap.get("regn_no"));
            ownerPmtDobj.setRegnDateInString(getmap.get("regn_dt"));
            ownerPmtDobj.setManuYearInString(getmap.get("manu_yr"));
            ownerPmtDobj.setDistrictInString(getmap.get("c_district_name"));
            ownerPmtDobj.setCurrentDateInString(getmap.get("currentdate"));
            ownerPmtDobj.setModelName(getmap.get("model_name"));
            ownerPmtDobj.setBody_type(getmap.get("body_type"));
            ownerPmtDobj.setState_cd(sessionVariables.getStateCodeSelected());
            if (CommonUtils.isNullOrBlank(getmap.get("stand_cap"))) {
                ownerPmtDobj.setStand_cap(0);
            } else {
                ownerPmtDobj.setStand_cap(Integer.valueOf(getmap.get("stand_cap")));
            }
            if (CommonUtils.isNullOrBlank(getmap.get("sleeper_cap"))) {
                ownerPmtDobj.setSleeper_cap(0);
            } else {
                ownerPmtDobj.setSleeper_cap(Integer.valueOf(getmap.get("sleeper_cap")));
            }
            ownerPmtDobj.setChasi_no(getmap.get("chasi_no"));
            ownerPmtDobj.setVh_class_desc(getmap.get("vh_class_desc"));
            if (CommonUtils.isNullOrBlank(getmap.get("seat_cap"))) {
                ownerPmtDobj.setSeat_cap(0);
            } else {
                ownerPmtDobj.setSeat_cap(Integer.valueOf(getmap.get("seat_cap")));
            }

            if (!CommonUtils.isNullOrBlank(getmap.get("auth_no"))) {
                homeAuthDobj.setAuthNo(getmap.get("auth_no"));
            } else {
                homeAuthDobj.setAuthNo(null);
            }
            homeAuthDobj.setAuthFromInString(getmap.get("auth_fr"));
            homeAuthDobj.setAuthUptoInString(getmap.get("auth_to"));
            setRoutedata(pmtDocImpl.getRouteData(getmap.get("appl_no"), getmap.get("state_cd"), Integer.valueOf(getmap.get("off_cd")), TableList.vt_permit_route, false));
            setTextQRcode("Application No: " + getmap.get("appl_no") + " \\nValid Upto: " + getmap.get("valid_upto") + " \\nPermit No: " + getmap.get("pmt_no"));
            PrintDocImpl.printSucessAndApproveStatus(sessionVariables, getmap, regn_no);
        } catch (VahanException e) {
            ownerPmtDobj = null;
            passPmtDobj = null;
            JSFUtils.showMessage(e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getApplno() {
        return applno;
    }

    public void setApplno(String applno) {
        this.applno = applno;
    }

    public int getPurCd() {
        return purCd;
    }

    public void setPurCd(int purCd) {
        this.purCd = purCd;
    }

    public PassengerPermitDetailDobj getPassPmtDobj() {
        return passPmtDobj;
    }

    public void setPassPmtDobj(PassengerPermitDetailDobj passPmtDobj) {
        this.passPmtDobj = passPmtDobj;
    }

    public PermitOwnerDetailDobj getOwnerPmtDobj() {
        return ownerPmtDobj;
    }

    public void setOwnerPmtDobj(PermitOwnerDetailDobj ownerPmtDobj) {
        this.ownerPmtDobj = ownerPmtDobj;
    }

    public List<PassengerPermitDetailDobj> getRoutedata() {
        return routedata;
    }

    public void setRoutedata(List<PassengerPermitDetailDobj> routedata) {
        this.routedata = routedata;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getReturnURL() {
        return returnURL;
    }

    public void setReturnURL(String returnURL) {
        this.returnURL = returnURL;
    }

    public String getVahanMessages() {
        return vahanMessages;
    }

    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }

    public boolean isRenderqrCode() {
        return renderqrCode;
    }

    public void setRenderqrCode(boolean renderqrCode) {
        this.renderqrCode = renderqrCode;
    }

    public boolean isIsKAState() {
        return isKAState;
    }

    public void setIsKAState(boolean isKAState) {
        this.isKAState = isKAState;
    }

    public PermitHomeAuthDobj getHomeAuthDobj() {
        return homeAuthDobj;
    }

    public void setHomeAuthDobj(PermitHomeAuthDobj homeAuthDobj) {
        this.homeAuthDobj = homeAuthDobj;
    }

    public String getImage_background() {
        return image_background;
    }

    public void setImage_background(String image_background) {
        this.image_background = image_background;
    }

    public String getImage_logo() {
        return image_logo;
    }

    public void setImage_logo(String image_logo) {
        this.image_logo = image_logo;
    }

    public boolean isShow_image_background() {
        return show_image_background;
    }

    public void setShow_image_background(boolean show_image_background) {
        this.show_image_background = show_image_background;
    }

    public boolean isShow_image_logo() {
        return show_image_logo;
    }

    public void setShow_image_logo(boolean show_image_logo) {
        this.show_image_logo = show_image_logo;
    }

    public String getRuleAndSection() {
        return ruleAndSection;
    }

    public void setRuleAndSection(String ruleAndSection) {
        this.ruleAndSection = ruleAndSection;
    }

    public String getTextQRcode() {
        return textQRcode;
    }

    public void setTextQRcode(String textQRcode) {
        this.textQRcode = textQRcode;
    }
}
