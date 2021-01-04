/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import nic.vahan.form.dobj.permit.PermitTimeTableDobj;
import org.apache.log4j.Logger;

/**
 *
 * @author WS39
 */
@ManagedBean(name = "pmttimetable")
@ViewScoped
public class PermitTimeTableBean implements Serializable {

    private List viaList = new ArrayList();
    private static final Logger LOGGER = Logger.getLogger(PermitTimeTableBean.class);
    private PermitTimeTableDobj pmtTimeTableDobj = new PermitTimeTableDobj();
    private List<PermitTimeTableDobj> timeTableList = new ArrayList();
    private List routeViaList = new ArrayList();
    private int no_of_trips = 0;
    private String routeVia;
    private PermitTimeTableDobj selectTimeTableDobj;


    public PermitTimeTableBean() {
    }

    /**
     * @return the viaList
     */
    public void createRows() {
        routeViaList.clear();
        int a = getNo_of_trips();
        String routeVia = getRouteVia();

        String[] arrRouteVia = routeVia.split(" ");

        for (int i = 1; i <= arrRouteVia.length - 1; i++) {
            routeViaList.add(new SelectItem(arrRouteVia[i], arrRouteVia[i]));
        }
        for (int i = 0; i < a; i++) {
            PermitTimeTableDobj pmtTimeTableDobj = new PermitTimeTableDobj();
            timeTableList.add(pmtTimeTableDobj);
        }
    }

    public List getViaList() {
        return viaList;
    }

    /**
     * @param viaList the viaList to set
     */
    public void setViaList(List viaList) {
        this.viaList = viaList;
    }

    public PermitTimeTableDobj getPmtTimeTableDobj() {
        return pmtTimeTableDobj;
    }

    public void setPmtTimeTableDobj(PermitTimeTableDobj pmtTimeTableDobj) {
        this.pmtTimeTableDobj = pmtTimeTableDobj;
    }

//    public PassengerPermitDetailBean getPermitDetailBean() {
//        return permitDetailBean;
//    }
//
//    public void setPermitDetailBean(PassengerPermitDetailBean permitDetailBean) {
//        this.permitDetailBean = permitDetailBean;
//    }
    public List<PermitTimeTableDobj> getTimeTableList() {
        return timeTableList;
    }

    public void setTimeTableList(List<PermitTimeTableDobj> timeTableList) {
        this.timeTableList = timeTableList;
    }

    public List getRouteViaList() {
        return routeViaList;
    }

    public void setRouteViaList(List routeViaList) {
        this.routeViaList = routeViaList;
    }

    public int getNo_of_trips() {
        return no_of_trips;
    }

    public void setNo_of_trips(int no_of_trips) {
        this.no_of_trips = no_of_trips;
    }

    public String getRouteVia() {
        return routeVia;
    }

    public void setRouteVia(String routeVia) {
        this.routeVia = routeVia;
    }

    public PermitTimeTableDobj getSelectTimeTableDobj() {
        return selectTimeTableDobj;
    }

    public void setSelectTimeTableDobj(PermitTimeTableDobj selectTimeTableDobj) {
        this.selectTimeTableDobj = selectTimeTableDobj;
    }

}
