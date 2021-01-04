/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.permit;

import java.io.Serializable;

/**
 *
 * @author tranC106
 */
public class VmPermitRouteDobj implements Serializable {

    private String state_code;
    private int off_code;
    private String off_cd_descr;
    private String route_code;
    private String from_loc;
    private String via;
    private String to_loc;
    private int length;
    private int region_cover;
    private String route_flag;
    private String new_route_code;
    private String new_tloc;
    private String nhOverlapping = "";
    private int nhOverlappingLength = 0;
    private String state_to = "";
    private String rowKey;

    public String getState_code() {
        return state_code;
    }

    public void setState_code(String state_code) {
        this.state_code = state_code;
    }

    public int getOff_code() {
        return off_code;
    }

    public void setOff_code(int off_code) {
        this.off_code = off_code;
    }

    public String getOff_cd_descr() {
        return off_cd_descr;
    }

    public void setOff_cd_descr(String off_cd_descr) {
        this.off_cd_descr = off_cd_descr;
    }

    public String getRoute_code() {
        return route_code;
    }

    public void setRoute_code(String route_code) {
        this.route_code = route_code;
    }

    public String getFrom_loc() {
        return from_loc;
    }

    public void setFrom_loc(String from_loc) {
        this.from_loc = from_loc;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String getTo_loc() {
        return to_loc;
    }

    public void setTo_loc(String to_loc) {
        this.to_loc = to_loc;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getRegion_cover() {
        return region_cover;
    }

    public void setRegion_cover(int region_cover) {
        this.region_cover = region_cover;
    }

    public String getRoute_flag() {
        return route_flag;
    }

    public void setRoute_flag(String route_flag) {
        this.route_flag = route_flag;
    }

    public String getNew_route_code() {
        return new_route_code;
    }

    public void setNew_route_code(String new_route_code) {
        this.new_route_code = new_route_code;
    }

    public String getNew_tloc() {
        return new_tloc;
    }

    public void setNew_tloc(String new_tloc) {
        this.new_tloc = new_tloc;
    }

    public String getNhOverlapping() {
        return nhOverlapping;
    }

    public void setNhOverlapping(String nhOverlapping) {
        this.nhOverlapping = nhOverlapping;
    }

    public int getNhOverlappingLength() {
        return nhOverlappingLength;
    }

    public void setNhOverlappingLength(int nhOverlappingLength) {
        this.nhOverlappingLength = nhOverlappingLength;
    }

    public String getState_to() {
        return state_to;
    }

    public void setState_to(String state_to) {
        this.state_to = state_to;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }
}
