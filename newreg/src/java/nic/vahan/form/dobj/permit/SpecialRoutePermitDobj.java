/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author manoj
 */
public class SpecialRoutePermitDobj implements Cloneable, Serializable {

    private Date valid_from;
    private String route_fr;
    private String route_to;
    private String valid_fr;
    private String via;
    private String old_via;
    private String old_valid_from;
    private String old_route_fr;
    private String old_route_to;
    private boolean psnger_list;
    private int srl_no;
    private String age;
    private String gender;
    private String name;
    private String address;

    public SpecialRoutePermitDobj(int srl_no, Date valid_from, String route_fr, String route_to, String via) {
        this.srl_no = srl_no;
        this.valid_from = valid_from;
        this.route_fr = route_fr;
        this.route_to = route_to;
        this.via = via;
    }

    public SpecialRoutePermitDobj() {
    }

    public Date getValid_from() {
        return valid_from;
    }

    public void setValid_from(Date valid_from) {
        this.valid_from = valid_from;
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String getValid_fr() {
        return valid_fr;
    }

    public void setValid_fr(String valid_fr) {
        this.valid_fr = valid_fr;
    }

    public String getOld_via() {
        return old_via;
    }

    public void setOld_via(String old_via) {
        this.old_via = old_via;
    }

    public String getOld_valid_from() {
        return old_valid_from;
    }

    public void setOld_valid_from(String old_valid_from) {
        this.old_valid_from = old_valid_from;
    }

    public String getOld_route_fr() {
        return old_route_fr;
    }

    public void setOld_route_fr(String old_route_fr) {
        this.old_route_fr = old_route_fr;
    }

    public String getOld_route_to() {
        return old_route_to;
    }

    public void setOld_route_to(String old_route_to) {
        this.old_route_to = old_route_to;
    }

    public int getSrl_no() {
        return srl_no;
    }

    public void setSrl_no(int srl_no) {
        this.srl_no = srl_no;
    }

    public boolean isPsnger_list() {
        return psnger_list;
    }

    public void setPsnger_list(boolean psnger_list) {
        this.psnger_list = psnger_list;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
