/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

/**
 *
 * @author acer
 */
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.form.dobj.permit.SpecialRoutePermitDobj;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author MANOJ
 */
@ManagedBean(name = "splPassengerBean")
@ViewScoped
public class SpecialPassengerDetailBean {

    private static final Logger LOGGER = Logger.getLogger(SpecialPassengerDetailBean.class);
    private SpecialRoutePermitDobj spl_passenger_dobj = null;
    private List<SpecialRoutePermitDobj> passengerList = null;
    private boolean disableTable;

    public SpecialPassengerDetailBean() {
        passengerList = new ArrayList<>();
        spl_passenger_dobj = new SpecialRoutePermitDobj();
        spl_passenger_dobj.setSrl_no(1);
        passengerList.add(spl_passenger_dobj);
    }

    public void addNewRow(SpecialRoutePermitDobj dobj) {
        String mode = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
                .get("mod");
        int i = 0;
        if (passengerList.size() == 0) {
            i = 1;
        } else {
            i = passengerList.size() + 1;
        }
        try {

            if ("add".equalsIgnoreCase(mode)) {

                if (CommonUtils.isNullOrBlank(dobj.getAge())) {
                    JSFUtils.showMessagesInDialog("Information", "Please Enter Age ", FacesMessage.SEVERITY_WARN);
                    return;
                } else if (!CommonUtils.isNullOrBlank(dobj.getAge()) && Integer.valueOf(dobj.getAge()) > 100) {

                    JSFUtils.showMessagesInDialog("Information", "Age Should not be greater than 100  ",
                            FacesMessage.SEVERITY_WARN);
                    dobj.setAge("");
                    return;
                } else if (dobj.getGender().equalsIgnoreCase("-1") || dobj.getGender().equalsIgnoreCase("")) {
                    JSFUtils.showMessagesInDialog("Information", "Please Select Gender ", FacesMessage.SEVERITY_WARN);
                    return;
                } else if (CommonUtils.isNullOrBlank(dobj.getName())) {
                    JSFUtils.showMessagesInDialog("Information", "Please Enter Name ", FacesMessage.SEVERITY_WARN);
                    return;
                } else if (CommonUtils.isNullOrBlank(dobj.getAddress())) {
                    JSFUtils.showMessagesInDialog("Information", "Please Enter Address ", FacesMessage.SEVERITY_WARN);
                    return;
                }
                SpecialRoutePermitDobj passDobj = new SpecialRoutePermitDobj();
                passDobj.setSrl_no(i);
                passengerList.add(passDobj);
                PrimeFaces.current().executeScript("PF('bui').hide();");
            } else if ("minus".equalsIgnoreCase(mode)) {
                int index = getPassengerList().indexOf(dobj);
                if ((getPassengerList().size() - 1) > index) {
                    getPassengerList().get(index + 1).setSrl_no(dobj.getSrl_no());
                }
                getPassengerList().remove(dobj);
                if (getPassengerList().isEmpty() || getPassengerList().size() <= 0) {
                    passengerList.add(new SpecialRoutePermitDobj());   // check it please
                }
            }
        } catch (Exception e) {
        }

    }

    public List<SpecialRoutePermitDobj> getPassengerList() {
        return passengerList;
    }

    public void setPassengerList(List<SpecialRoutePermitDobj> passengerList) {
        this.passengerList = passengerList;
    }

    public SpecialRoutePermitDobj getSpl_passenger_dobj() {
        return spl_passenger_dobj;
    }

    public void setSpl_passenger_dobj(SpecialRoutePermitDobj spl_passenger_dobj) {
        this.spl_passenger_dobj = spl_passenger_dobj;
    }

    public boolean isDisableTable() {
        return disableTable;
    }

    public void setDisableTable(boolean disableTable) {
        this.disableTable = disableTable;
    }
}
