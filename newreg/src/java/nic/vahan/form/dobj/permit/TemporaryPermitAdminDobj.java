/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nic.vahan.form.bean.permit.PermitRouteList;
import org.primefaces.model.DualListModel;

/**
 *
 * @author MukulRaiDutta
 */
public class TemporaryPermitAdminDobj implements Cloneable, Serializable {

    private String purpose;
    private String oldValue;
    private String newValue;
    //vt_temp_permit
    private int off_cd;
    private String appl_no;//*
    private String pmt_no;//*
    private String regn_no;//*
    private Date issue_dt;//*
    private Date valid_from;//timestamp without time zone NOT NULL,
    private Date valid_upto;//timestamp without time zone NOT NULL,
    private String rcpt_no;//character varying(16),
    private int pur_cd;//numeric(3,0),
    private int pmt_type;// numeric(5,0),
    private int pmt_catg;//numeric(5,0),
    private String reason;//character varying(100) NOT NULL,
    private String route_fr;// character varying(300) NOT NULL,
    private String route_to;// character varying(300) NOT NULL,
    private Date op_dt;// timestamp without time zone NOT NULL,
    private String via = "";//character varying(100),
    private String goods_to_carry;//character varying(40),
    private String vt_temp_Route_via = "";//character varying(100),
    //vt_temp_permit_route
    private int no_of_trips;
    private DualListModel<PermitRouteList> regionManage;
    private List<PermitRouteList> regionActionSource = new ArrayList<>();
    private List<PermitRouteList> regionActionTarget = new ArrayList<>();
    private DualListModel<PermitRouteList> routeManage;
    private List<PermitRouteList> routeActionSource = new ArrayList<>();
    private List<PermitRouteList> routeActionTarget = new ArrayList<>();
    private String region_covered = "";

    public TemporaryPermitAdminDobj() {
        regionManage = new DualListModel<>(regionActionSource, regionActionTarget);
        routeManage = new DualListModel<>(routeActionSource, routeActionTarget);
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getPmt_no() {
        return pmt_no;
    }

    public void setPmt_no(String pmt_no) {
        this.pmt_no = pmt_no;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public Date getIssue_dt() {
        return issue_dt;
    }

    public void setIssue_dt(Date issue_dt) {
        this.issue_dt = issue_dt;
    }

    public Date getValid_from() {
        return valid_from;
    }

    public void setValid_from(Date valid_from) {
        this.valid_from = valid_from;
    }

    public Date getValid_upto() {
        return valid_upto;
    }

    public void setValid_upto(Date valid_upto) {
        this.valid_upto = valid_upto;
    }

    public String getRcpt_no() {
        return rcpt_no;
    }

    public void setRcpt_no(String rcpt_no) {
        this.rcpt_no = rcpt_no;
    }

    public int getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    public int getPmt_type() {
        return pmt_type;
    }

    public void setPmt_type(int pmt_type) {
        this.pmt_type = pmt_type;
    }

    public int getPmt_catg() {
        return pmt_catg;
    }

    public void setPmt_catg(int pmt_catg) {
        this.pmt_catg = pmt_catg;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

    public Date getOp_dt() {
        return op_dt;
    }

    public void setOp_dt(Date op_dt) {
        this.op_dt = op_dt;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String getGoods_to_carry() {
        return goods_to_carry;
    }

    public void setGoods_to_carry(String goods_to_carry) {
        this.goods_to_carry = goods_to_carry;
    }

    public DualListModel<PermitRouteList> getRegionManage() {
        return regionManage;
    }

    public void setRegionManage(DualListModel<PermitRouteList> regionManage) {
        this.regionManage = regionManage;
    }

    public List<PermitRouteList> getRegionActionSource() {
        return regionActionSource;
    }

    public void setRegionActionSource(List<PermitRouteList> regionActionSource) {
        this.regionActionSource = regionActionSource;
    }

    public List<PermitRouteList> getRegionActionTarget() {
        return regionActionTarget;
    }

    public void setRegionActionTarget(List<PermitRouteList> regionActionTarget) {
        this.regionActionTarget = regionActionTarget;
    }

    public DualListModel<PermitRouteList> getRouteManage() {
        return routeManage;
    }

    public void setRouteManage(DualListModel<PermitRouteList> routeManage) {
        this.routeManage = routeManage;
    }

    public List<PermitRouteList> getRouteActionSource() {
        return routeActionSource;
    }

    public void setRouteActionSource(List<PermitRouteList> routeActionSource) {
        this.routeActionSource = routeActionSource;
    }

    public List<PermitRouteList> getRouteActionTarget() {
        return routeActionTarget;
    }

    public void setRouteActionTarget(List<PermitRouteList> routeActionTarget) {
        this.routeActionTarget = routeActionTarget;
    }

    public int getNo_of_trips() {
        return no_of_trips;
    }

    public void setNo_of_trips(int no_of_trips) {
        this.no_of_trips = no_of_trips;
    }

    public String getRegion_covered() {
        return region_covered;
    }

    public void setRegion_covered(String region_covered) {
        this.region_covered = region_covered;
    }

    public int getOff_cd() {
        return off_cd;
    }

    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getVt_temp_Route_via() {
        return vt_temp_Route_via;
    }

    public void setVt_temp_Route_via(String vt_temp_Route_via) {
        this.vt_temp_Route_via = vt_temp_Route_via;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
