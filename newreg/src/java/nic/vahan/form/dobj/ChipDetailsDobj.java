package nic.vahan.form.dobj;

import java.io.Serializable;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author nicsi
 */
public class ChipDetailsDobj implements Serializable {

    private String state_cd;
    private Integer off_cd;
    private String test_server_ip4;
    private String stage_server_ip4;
    private String prod_server_ip4;
    private boolean test_server_status;
    private boolean statge_server_status;
    private boolean prod_server_status;
    private String ia1_chip_no;
    private String ia2_chip_no;
    private String ia3_chip_no;
    private String ia4_chip_no;
    private String ia5_chip_no;
    private String ia6_chip_no;
    private String stateName;
    private String officeName;
    private boolean saveButton;

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public Integer getOff_cd() {
        return off_cd;
    }

    public void setOff_cd(Integer off_cd) {
        this.off_cd = off_cd;
    }

    public String getTest_server_ip4() {
        return test_server_ip4;
    }

    public void setTest_server_ip4(String test_server_ip4) {
        this.test_server_ip4 = test_server_ip4;
    }

    public String getStage_server_ip4() {
        return stage_server_ip4;
    }

    public void setStage_server_ip4(String stage_server_ip4) {
        this.stage_server_ip4 = stage_server_ip4;
    }

    public String getProd_server_ip4() {
        return prod_server_ip4;
    }

    public void setProd_server_ip4(String prod_server_ip4) {
        this.prod_server_ip4 = prod_server_ip4;
    }

    public boolean isTest_server_status() {
        return test_server_status;
    }

    public void setTest_server_status(boolean test_server_status) {
        this.test_server_status = test_server_status;
    }

    public boolean isStatge_server_status() {
        return statge_server_status;
    }

    public void setStatge_server_status(boolean statge_server_status) {
        this.statge_server_status = statge_server_status;
    }

    public boolean isProd_server_status() {
        return prod_server_status;
    }

    public void setProd_server_status(boolean prod_server_status) {
        this.prod_server_status = prod_server_status;
    }

    public String getIa1_chip_no() {
        return ia1_chip_no;
    }

    public void setIa1_chip_no(String ia1_chip_no) {
        this.ia1_chip_no = ia1_chip_no;
    }

    public String getIa2_chip_no() {
        return ia2_chip_no;
    }

    public void setIa2_chip_no(String ia2_chip_no) {
        this.ia2_chip_no = ia2_chip_no;
    }

    public String getIa3_chip_no() {
        return ia3_chip_no;
    }

    public void setIa3_chip_no(String ia3_chip_no) {
        this.ia3_chip_no = ia3_chip_no;
    }

    public String getIa4_chip_no() {
        return ia4_chip_no;
    }

    public void setIa4_chip_no(String ia4_chip_no) {
        this.ia4_chip_no = ia4_chip_no;
    }

    public String getIa5_chip_no() {
        return ia5_chip_no;
    }

    public void setIa5_chip_no(String ia5_chip_no) {
        this.ia5_chip_no = ia5_chip_no;
    }

    public String getIa6_chip_no() {
        return ia6_chip_no;
    }

    public void setIa6_chip_no(String ia6_chip_no) {
        this.ia6_chip_no = ia6_chip_no;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public boolean isSaveButton() {
        return saveButton;
    }

    public void setSaveButton(boolean saveButton) {
        this.saveButton = saveButton;
    }
}
