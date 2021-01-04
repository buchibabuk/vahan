/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

public class VmSectionsDobj implements Cloneable {

    private int sectionCode;
    private String level_flag;
    private String section_name;
    private String is_active;
    private String state_cd;

    public VmSectionsDobj(int sectioncode, String level_flag, String section_name, String is_active) {
        this.sectionCode = sectioncode;
        this.level_flag = level_flag;
        this.section_name = section_name;
        this.is_active = is_active;
    }

    public VmSectionsDobj() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    public String getLevel_flag() {
        return level_flag;
    }

    public void setLevel_flag(String level_flag) {
        this.level_flag = level_flag;
    }

    public String getSection_name() {
        return section_name;
    }

    public void setSection_name(String section_name) {
        this.section_name = section_name;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public int getSectionCode() {
        return sectionCode;
    }

    public void setSectionCode(int sectionCode) {
        this.sectionCode = sectionCode;
    }
}
