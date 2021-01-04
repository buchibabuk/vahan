/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.form.dobj.permit.SpecialRoutePermitDobj;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author MANOJ
 */
@ManagedBean(name = "splRouteBean")
@ViewScoped
public class SpecialRoutePermitBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(SpecialRoutePermitBean.class);
    private SpecialRoutePermitDobj spl_route_dobj = null;
    private Date currentDate = DateUtil.parseDate(DateUtil.getCurrentDate());
    private Date valid_upto = null;
    private boolean renderfield = true;
    private String route_fr = "";
    private String route_to = "";
    private boolean disableTable = true;
    private boolean disablePassanger = false;
    private List<SpecialRoutePermitDobj> specialRouteList = null;
    private List<SpecialRoutePermitDobj> specialRouteList1 = null;
    private boolean checkPassengerList = false;

    public SpecialRoutePermitBean() {
        specialRouteList = new ArrayList<>();
        spl_route_dobj = new SpecialRoutePermitDobj();
    }

    public void addRoute(SpecialRoutePermitDobj dobj) {
        int i = 0;
        if (specialRouteList.size() == 0) {
            i = 1;
        } else {
            i = specialRouteList.size() + 1;
        }
        if (dobj.getValid_from() == null) {
            JSFUtils.showMessagesInDialog("Information", "Please select From Date", FacesMessage.SEVERITY_INFO);
            return;
        } else if (dobj.getRoute_fr().equalsIgnoreCase("")) {
            JSFUtils.showMessagesInDialog("Information", "Please Enter Route From ", FacesMessage.SEVERITY_INFO);
            return;
        } else if (dobj.getRoute_to().equalsIgnoreCase("")) {
            JSFUtils.showMessagesInDialog("Information", "Please Enter Route To", FacesMessage.SEVERITY_INFO);
            return;
        } else if (dobj.getVia().equalsIgnoreCase("")) {
            JSFUtils.showMessagesInDialog("Information", "Please Enter Via", FacesMessage.SEVERITY_INFO);
            return;
        } else if (!dobj.getRoute_fr().equalsIgnoreCase("") && !dobj.getRoute_to().equalsIgnoreCase("") && dobj.getSrl_no() != 1) {
            String from = dobj.getRoute_fr().replaceAll("\\s+", "");
            String to = dobj.getRoute_to().replaceAll("\\s+", "");
            if (from.equalsIgnoreCase(to)) {
                JSFUtils.showMessage("Route From and Route To can't be Same");
                return;
            }
        }
        Date from_date = dobj.getValid_from();
        if (from_date.after(valid_upto)) {
            JSFUtils.showMessagesInDialog("Information", "This Date is not valid", FacesMessage.SEVERITY_WARN);
            return;
        }
        dobj.setSrl_no(i);
        dobj.setOld_valid_from(JSFUtils.convertToStandardDateFormat(from_date));
        dobj.setOld_route_fr(dobj.getRoute_fr());
        dobj.setOld_route_to(dobj.getRoute_to());
        dobj.setOld_via(dobj.getVia());
        specialRouteList.add(dobj);
        spl_route_dobj = new SpecialRoutePermitDobj();
        Date next_dt = ServerUtil.dateRange(from_date, 0, 0, 1);
        spl_route_dobj.setValid_from(next_dt);
        if (specialRouteList.size() > 0) {
            spl_route_dobj.setRoute_fr(specialRouteList.get(specialRouteList.size() - 1).getRoute_to());
        }
    }

    public void onCellEdit(SpecialRoutePermitDobj dobj) {
        specialRouteList1 = new ArrayList<>();
        specialRouteList1.addAll(getSpecialRouteList());
        if (CommonUtils.isNullOrBlank(dobj.getRoute_fr()) || CommonUtils.isNullOrBlank(dobj.getRoute_to()) || CommonUtils.isNullOrBlank(dobj.getVia()) || dobj.getValid_from() == null) {
            if (specialRouteList1.size() > 0) {
                specialRouteList.clear();
                for (SpecialRoutePermitDobj obj : specialRouteList1) {
                    if (obj.equals(dobj)) {
                        obj.setRoute_fr(dobj.getOld_route_fr());
                        obj.setRoute_to(dobj.getOld_route_to());
                        obj.setVia(dobj.getOld_via());
                        obj.setValid_from(JSFUtils.getStringToDateddMMMyyyy(dobj.getOld_valid_from()));
                    }
                    specialRouteList.add(obj);
                }
            }
            JSFUtils.showMessagesInDialog("Information", "Field Can not be blank !!", FacesMessage.SEVERITY_WARN);
        } else {
            if (specialRouteList1.size() > 0) {
                specialRouteList.clear();
                for (SpecialRoutePermitDobj obj : specialRouteList1) {
                    if (obj.equals(dobj)) {
                        String from = dobj.getRoute_fr().replaceAll("\\s+", "");
                        String to = dobj.getRoute_to().replaceAll("\\s+", "");
                        if (from.equalsIgnoreCase(to) && specialRouteList1.size() > 1) {
                            JSFUtils.showMessage("Origin and Destination can't be Same !!");
                            obj.setRoute_to(dobj.getOld_route_to());
                        } else {
                            obj.setRoute_fr(dobj.getRoute_fr());
                            obj.setRoute_to(dobj.getRoute_to());
                            obj.setVia(dobj.getVia());
                            obj.setValid_from(dobj.getValid_from());
                            obj.setOld_route_fr(dobj.getRoute_fr());
                            obj.setOld_route_to(dobj.getRoute_to());
                            obj.setOld_via(dobj.getVia());
                            obj.setOld_valid_from(JSFUtils.convertToStandardDateFormat(dobj.getValid_from()));
                        }
                    }
                    specialRouteList.add(obj);
                }
            }
        }
    }

    public SpecialRoutePermitDobj getSpl_route_dobj() {
        return spl_route_dobj;
    }

    public void setSpl_route_dobj(SpecialRoutePermitDobj spl_route_dobj) {
        this.spl_route_dobj = spl_route_dobj;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public List<SpecialRoutePermitDobj> getSpecialRouteList() {
        return specialRouteList;
    }

    public void setSpecialRouteList(List<SpecialRoutePermitDobj> specialRouteList) {
        this.specialRouteList = specialRouteList;
    }

    public Date getValid_upto() {
        return valid_upto;
    }

    public void setValid_upto(Date valid_upto) {
        this.valid_upto = valid_upto;
    }

    public List<SpecialRoutePermitDobj> getSpecialRouteList1() {
        return specialRouteList1;
    }

    public void setSpecialRouteList1(List<SpecialRoutePermitDobj> specialRouteList1) {
        this.specialRouteList1 = specialRouteList1;
    }

    public boolean isRenderfield() {
        return renderfield;
    }

    public void setRenderfield(boolean renderfield) {
        this.renderfield = renderfield;
    }

    public boolean isDisableTable() {
        return disableTable;
    }

    public void setDisableTable(boolean disableTable) {
        this.disableTable = disableTable;
    }

    public boolean isDisablePassanger() {
        return disablePassanger;
    }

    public void setDisablePassanger(boolean disablePassanger) {
        this.disablePassanger = disablePassanger;
    }

    public boolean isCheckPassengerList() {
        return checkPassengerList;
    }

    public void setCheckPassengerList(boolean checkPassengerList) {
        this.checkPassengerList = checkPassengerList;
    }

    public String getRoute_fr() {
        return route_fr;
    }

    public void setRoute_fr(String route_fr) {
        this.route_fr = route_fr;
    }

    public String getRoute_to() {
        return route_to;
    }

    public void setRoute_to(String route_to) {
        this.route_to = route_to;
    }
}
