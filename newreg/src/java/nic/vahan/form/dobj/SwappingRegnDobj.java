/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.util.Date;

/**
 *
 * @author nic
 */
public class SwappingRegnDobj extends Owner_dobj implements Cloneable {

    private String applnoOne;
    private String applno;
    private String link_appl_no_one;
    private String applnoTwo;
    private String link_appl_no_two;
    private String appldt;
    private String regnNoOne;
    private String ownernameone;
    private String fathernameone;
    private String c_add1One;
    private String c_add2One;
    private String c_add3One;
    private String c_stateOne;
    private String c_districtOne;
    private String c_statecodeOne;
    private int c_districtcodeOne;
    private int c_pincodeOne;
    private String regnNoTwo;
    private String ownernametwo;
    private String fathernametwo;
    private String c_add1Two;
    private String c_add2Two;
    private String c_add3Two;
    private String c_stateTwo;
    private String c_districtTwo;
    private String c_statecodeTwo;
    private int c_districtcodeTwo;
    private int c_pincodeTwo;
    private String order_no;
    private String order_by;
    private String reason;
    private String order_dt;//apprdt;
    private String opdate;
    private String usercd;
    private boolean isrendered = false;
    private int vh_class_one = 0;
    private int vh_class_two = 0;
    private int vh_class = 0;
    private int vhType = 0;
    private Date regn_upto;
    private Date fit_upto;
    private Date regn_dt;
    private Date regn_dt_two;
    private String status;
    private String tempRegnNo;
    private boolean isHyptRegnNo;
    private String chassi_one;
    private String engine_one;
    private String chassi_two;
    private String engine_two;
    private Date purchase_dt;
    private String regnTypeforold;
    private String regnTypefornew;
    private String chasiOneLastFiveChar;
    private String chasiTwoLastFiveChar;
    private String state_cd_one;
    private String state_cd_two;
    private int off_cd_one;
    private int off_cd_two;
    private int relation_code;
    private TmConfigurationSwappingDobj tmConfSwappingDobj = null;
    private String vch_catg_new_vehicle;
    private String vch_catg_old_vehicle;
    private int newVehicleOwnercd;
    private int oldVehicleOwnercd;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @return the regnNoOne
     */
    public String getRegnNoOne() {
        return regnNoOne;
    }

    /**
     * @param regnNoOne the regnNoOne to set
     */
    public void setRegnNoOne(String regnNoOne) {
        this.regnNoOne = regnNoOne;
    }

    /**
     * @return the ownernameone
     */
    public String getOwnernameone() {
        return ownernameone;
    }

    /**
     * @param ownernameone the ownernameone to set
     */
    public void setOwnernameone(String ownernameone) {
        this.ownernameone = ownernameone;
    }

    /**
     * @return the fathernameone
     */
    public String getFathernameone() {
        return fathernameone;
    }

    /**
     * @param fathernameone the fathernameone to set
     */
    public void setFathernameone(String fathernameone) {
        this.fathernameone = fathernameone;
    }

    /**
     * @return the c_add1One
     */
    public String getC_add1One() {
        return c_add1One;
    }

    /**
     * @param c_add1One the c_add1One to set
     */
    public void setC_add1One(String c_add1One) {
        this.c_add1One = c_add1One;
    }

    /**
     * @return the c_add2One
     */
    public String getC_add2One() {
        return c_add2One;
    }

    /**
     * @param c_add2One the c_add2One to set
     */
    public void setC_add2One(String c_add2One) {
        this.c_add2One = c_add2One;
    }

    /**
     * @return the c_add3One
     */
    public String getC_add3One() {
        return c_add3One;
    }

    /**
     * @param c_add3One the c_add3One to set
     */
    public void setC_add3One(String c_add3One) {
        this.c_add3One = c_add3One;
    }

    /**
     * @return the c_stateOne
     */
    public String getC_stateOne() {
        return c_stateOne;
    }

    /**
     * @param c_stateOne the c_stateOne to set
     */
    public void setC_stateOne(String c_stateOne) {
        this.c_stateOne = c_stateOne;
    }

    /**
     * @return the c_districtOne
     */
    public String getC_districtOne() {
        return c_districtOne;
    }

    /**
     * @param c_districtOne the c_districtOne to set
     */
    public void setC_districtOne(String c_districtOne) {
        this.c_districtOne = c_districtOne;
    }

    /**
     * @return the c_statecodeOne
     */
    public String getC_statecodeOne() {
        return c_statecodeOne;
    }

    /**
     * @param c_statecodeOne the c_statecodeOne to set
     */
    public void setC_statecodeOne(String c_statecodeOne) {
        this.c_statecodeOne = c_statecodeOne;
    }

    /**
     * @return the c_districtcodeOne
     */
    public int getC_districtcodeOne() {
        return c_districtcodeOne;
    }

    /**
     * @param c_districtcodeOne the c_districtcodeOne to set
     */
    public void setC_districtcodeOne(int c_districtcodeOne) {
        this.c_districtcodeOne = c_districtcodeOne;
    }

    /**
     * @return the c_pincodeOne
     */
    public int getC_pincodeOne() {
        return c_pincodeOne;
    }

    /**
     * @param c_pincodeOne the c_pincodeOne to set
     */
    public void setC_pincodeOne(int c_pincodeOne) {
        this.c_pincodeOne = c_pincodeOne;
    }

    /**
     * @return the regnNoTwo
     */
    public String getRegnNoTwo() {
        return regnNoTwo;
    }

    /**
     * @param regnNoTwo the regnNoTwo to set
     */
    public void setRegnNoTwo(String regnNoTwo) {
        this.regnNoTwo = regnNoTwo;
    }

    /**
     * @return the ownernametwo
     */
    public String getOwnernametwo() {
        return ownernametwo;
    }

    /**
     * @param ownernametwo the ownernametwo to set
     */
    public void setOwnernametwo(String ownernametwo) {
        this.ownernametwo = ownernametwo;
    }

    /**
     * @return the fathernametwo
     */
    public String getFathernametwo() {
        return fathernametwo;
    }

    /**
     * @param fathernametwo the fathernametwo to set
     */
    public void setFathernametwo(String fathernametwo) {
        this.fathernametwo = fathernametwo;
    }

    /**
     * @return the c_add1Two
     */
    public String getC_add1Two() {
        return c_add1Two;
    }

    /**
     * @param c_add1Two the c_add1Two to set
     */
    public void setC_add1Two(String c_add1Two) {
        this.c_add1Two = c_add1Two;
    }

    /**
     * @return the c_add2Two
     */
    public String getC_add2Two() {
        return c_add2Two;
    }

    /**
     * @param c_add2Two the c_add2Two to set
     */
    public void setC_add2Two(String c_add2Two) {
        this.c_add2Two = c_add2Two;
    }

    /**
     * @return the c_add3Two
     */
    public String getC_add3Two() {
        return c_add3Two;
    }

    /**
     * @param c_add3Two the c_add3Two to set
     */
    public void setC_add3Two(String c_add3Two) {
        this.c_add3Two = c_add3Two;
    }

    /**
     * @return the c_stateTwo
     */
    public String getC_stateTwo() {
        return c_stateTwo;
    }

    /**
     * @param c_stateTwo the c_stateTwo to set
     */
    public void setC_stateTwo(String c_stateTwo) {
        this.c_stateTwo = c_stateTwo;
    }

    /**
     * @return the c_districtTwo
     */
    public String getC_districtTwo() {
        return c_districtTwo;
    }

    /**
     * @param c_districtTwo the c_districtTwo to set
     */
    public void setC_districtTwo(String c_districtTwo) {
        this.c_districtTwo = c_districtTwo;
    }

    /**
     * @return the c_statecodeTwo
     */
    public String getC_statecodeTwo() {
        return c_statecodeTwo;
    }

    /**
     * @param c_statecodeTwo the c_statecodeTwo to set
     */
    public void setC_statecodeTwo(String c_statecodeTwo) {
        this.c_statecodeTwo = c_statecodeTwo;
    }

    /**
     * @return the c_districtcodeTwo
     */
    public int getC_districtcodeTwo() {
        return c_districtcodeTwo;
    }

    /**
     * @param c_districtcodeTwo the c_districtcodeTwo to set
     */
    public void setC_districtcodeTwo(int c_districtcodeTwo) {
        this.c_districtcodeTwo = c_districtcodeTwo;
    }

    /**
     * @return the c_pincodeTwo
     */
    public int getC_pincodeTwo() {
        return c_pincodeTwo;
    }

    /**
     * @param c_pincodeTwo the c_pincodeTwo to set
     */
    public void setC_pincodeTwo(int c_pincodeTwo) {
        this.c_pincodeTwo = c_pincodeTwo;
    }

    /**
     * @return the order_no
     */
    public String getOrder_no() {
        return order_no;
    }

    /**
     * @param order_no the order_no to set
     */
    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    /**
     * @return the order_by
     */
    public String getOrder_by() {
        return order_by;
    }

    /**
     * @param order_by the order_by to set
     */
    public void setOrder_by(String order_by) {
        this.order_by = order_by;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return the order_dt
     */
    public String getOrder_dt() {
        return order_dt;
    }

    /**
     * @param order_dt the order_dt to set
     */
    public void setOrder_dt(String order_dt) {
        this.order_dt = order_dt;
    }

    /**
     * @return the opdate
     */
    public String getOpdate() {
        return opdate;
    }

    /**
     * @param opdate the opdate to set
     */
    public void setOpdate(String opdate) {
        this.opdate = opdate;
    }

    /**
     * @return the usercd
     */
    public String getUsercd() {
        return usercd;
    }

    /**
     * @param usercd the usercd to set
     */
    public void setUsercd(String usercd) {
        this.usercd = usercd;
    }

    /**
     * @return the appldt
     */
    public String getAppldt() {
        return appldt;
    }

    /**
     * @param appldt the appldt to set
     */
    public void setAppldt(String appldt) {
        this.appldt = appldt;
    }

    /**
     * @return the applnoOne
     */
    public String getApplnoOne() {
        return applnoOne;
    }

    /**
     * @param applnoOne the applnoOne to set
     */
    public void setApplnoOne(String applnoOne) {
        this.applnoOne = applnoOne;
    }

    /**
     * @return the link_appl_no_one
     */
    public String getLink_appl_no_one() {
        return link_appl_no_one;
    }

    /**
     * @param link_appl_no_one the link_appl_no_one to set
     */
    public void setLink_appl_no_one(String link_appl_no_one) {
        this.link_appl_no_one = link_appl_no_one;
    }

    /**
     * @return the applnoTwo
     */
    public String getApplnoTwo() {
        return applnoTwo;
    }

    /**
     * @param applnoTwo the applnoTwo to set
     */
    public void setApplnoTwo(String applnoTwo) {
        this.applnoTwo = applnoTwo;
    }

    /**
     * @return the link_appl_no_two
     */
    public String getLink_appl_no_two() {
        return link_appl_no_two;
    }

    /**
     * @param link_appl_no_two the link_appl_no_two to set
     */
    public void setLink_appl_no_two(String link_appl_no_two) {
        this.link_appl_no_two = link_appl_no_two;
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
     * @return the isrendered
     */
    public boolean isIsrendered() {
        return isrendered;
    }

    /**
     * @param isrendered the isrendered to set
     */
    public void setIsrendered(boolean isrendered) {
        this.isrendered = isrendered;
    }

    /**
     * @return the regn_upto
     */
    public Date getRegn_upto() {
        return regn_upto;
    }

    /**
     * @param regn_upto the regn_upto to set
     */
    public void setRegn_upto(Date regn_upto) {
        this.regn_upto = regn_upto;
    }

    /**
     * @return the vhType
     */
    public int getVhType() {
        return vhType;
    }

    /**
     * @param vhType the vhType to set
     */
    public void setVhType(int vhType) {
        this.vhType = vhType;
    }

    /**
     * @return the fit_upto
     */
    public Date getFit_upto() {
        return fit_upto;
    }

    /**
     * @param fit_upto the fit_upto to set
     */
    public void setFit_upto(Date fit_upto) {
        this.fit_upto = fit_upto;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the tempRegnNo
     */
    public String getTempRegnNo() {
        return tempRegnNo;
    }

    /**
     * @param tempRegnNo the tempRegnNo to set
     */
    public void setTempRegnNo(String tempRegnNo) {
        this.tempRegnNo = tempRegnNo;
    }

    /**
     * @return the vh_class_one
     */
    public int getVh_class_one() {
        return vh_class_one;
    }

    /**
     * @param vh_class_one the vh_class_one to set
     */
    public void setVh_class_one(int vh_class_one) {
        this.vh_class_one = vh_class_one;
    }

    /**
     * @return the vh_class_two
     */
    public int getVh_class_two() {
        return vh_class_two;
    }

    /**
     * @param vh_class_two the vh_class_two to set
     */
    public void setVh_class_two(int vh_class_two) {
        this.vh_class_two = vh_class_two;
    }

    /**
     * @return the vh_class
     */
    public int getVh_class() {
        return vh_class;
    }

    /**
     * @param vh_class the vh_class to set
     */
    public void setVh_class(int vh_class) {
        this.vh_class = vh_class;
    }

    /**
     * @return the isHyptRegnNo
     */
    public boolean isIsHyptRegnNo() {
        return isHyptRegnNo;
    }

    /**
     * @param isHyptRegnNo the isHyptRegnNo to set
     */
    public void setIsHyptRegnNo(boolean isHyptRegnNo) {
        this.isHyptRegnNo = isHyptRegnNo;
    }

    /**
     * @return the regn_dt
     */
    public Date getRegn_dt() {
        return regn_dt;
    }

    /**
     * @param regn_dt the regn_dt to set
     */
    public void setRegn_dt(Date regn_dt) {
        this.regn_dt = regn_dt;
    }

    /**
     * @return the regn_dt_two
     */
    public Date getRegn_dt_two() {
        return regn_dt_two;
    }

    /**
     * @param regn_dt_two the regn_dt_two to set
     */
    public void setRegn_dt_two(Date regn_dt_two) {
        this.regn_dt_two = regn_dt_two;
    }

    /**
     * @return the chassi_one
     */
    public String getChassi_one() {
        return chassi_one;
    }

    /**
     * @param chassi_one the chassi_one to set
     */
    public void setChassi_one(String chassi_one) {
        this.chassi_one = chassi_one;
    }

    /**
     * @return the engine_one
     */
    public String getEngine_one() {
        return engine_one;
    }

    /**
     * @param engine_one the engine_one to set
     */
    public void setEngine_one(String engine_one) {
        this.engine_one = engine_one;
    }

    /**
     * @return the chassi_two
     */
    public String getChassi_two() {
        return chassi_two;
    }

    /**
     * @param chassi_two the chassi_two to set
     */
    public void setChassi_two(String chassi_two) {
        this.chassi_two = chassi_two;
    }

    /**
     * @return the engine_two
     */
    public String getEngine_two() {
        return engine_two;
    }

    /**
     * @param engine_two the engine_two to set
     */
    public void setEngine_two(String engine_two) {
        this.engine_two = engine_two;
    }

    /**
     * @return the purchase_dt
     */
    public Date getPurchase_dt() {
        return purchase_dt;
    }

    /**
     * @param purchase_dt the purchase_dt to set
     */
    public void setPurchase_dt(Date purchase_dt) {
        this.purchase_dt = purchase_dt;
    }

    /**
     * @return the regnTypeforold
     */
    public String getRegnTypeforold() {
        return regnTypeforold;
    }

    /**
     * @param regnTypeforold the regnTypeforold to set
     */
    public void setRegnTypeforold(String regnTypeforold) {
        this.regnTypeforold = regnTypeforold;
    }

    /**
     * @return the regnTypefornew
     */
    public String getRegnTypefornew() {
        return regnTypefornew;
    }

    /**
     * @param regnTypefornew the regnTypefornew to set
     */
    public void setRegnTypefornew(String regnTypefornew) {
        this.regnTypefornew = regnTypefornew;
    }

    /**
     * @return the chasiOneLastFiveChar
     */
    public String getChasiOneLastFiveChar() {
        return chasiOneLastFiveChar;
    }

    /**
     * @param chasiOneLastFiveChar the chasiOneLastFiveChar to set
     */
    public void setChasiOneLastFiveChar(String chasiOneLastFiveChar) {
        this.chasiOneLastFiveChar = chasiOneLastFiveChar;
    }

    /**
     * @return the chasiTwoLastFiveChar
     */
    public String getChasiTwoLastFiveChar() {
        return chasiTwoLastFiveChar;
    }

    /**
     * @param chasiTwoLastFiveChar the chasiTwoLastFiveChar to set
     */
    public void setChasiTwoLastFiveChar(String chasiTwoLastFiveChar) {
        this.chasiTwoLastFiveChar = chasiTwoLastFiveChar;
    }

    /**
     * @return the state_cd_one
     */
    public String getState_cd_one() {
        return state_cd_one;
    }

    /**
     * @param state_cd_one the state_cd_one to set
     */
    public void setState_cd_one(String state_cd_one) {
        this.state_cd_one = state_cd_one;
    }

    /**
     * @return the state_cd_two
     */
    public String getState_cd_two() {
        return state_cd_two;
    }

    /**
     * @param state_cd_two the state_cd_two to set
     */
    public void setState_cd_two(String state_cd_two) {
        this.state_cd_two = state_cd_two;
    }

    /**
     * @return the off_cd_one
     */
    public int getOff_cd_one() {
        return off_cd_one;
    }

    /**
     * @param off_cd_one the off_cd_one to set
     */
    public void setOff_cd_one(int off_cd_one) {
        this.off_cd_one = off_cd_one;
    }

    /**
     * @return the off_cd_two
     */
    public int getOff_cd_two() {
        return off_cd_two;
    }

    /**
     * @param off_cd_two the off_cd_two to set
     */
    public void setOff_cd_two(int off_cd_two) {
        this.off_cd_two = off_cd_two;
    }

    /**
     * @return the relation_code
     */
    public int getRelation_code() {
        return relation_code;
    }

    /**
     * @param relation_code the relation_code to set
     */
    public void setRelation_code(int relation_code) {
        this.relation_code = relation_code;
    }

    /**
     * @return the tmConfSwappingDobj
     */
    public TmConfigurationSwappingDobj getTmConfSwappingDobj() {
        return tmConfSwappingDobj;
    }

    /**
     * @param tmConfSwappingDobj the tmConfSwappingDobj to set
     */
    public void setTmConfSwappingDobj(TmConfigurationSwappingDobj tmConfSwappingDobj) {
        this.tmConfSwappingDobj = tmConfSwappingDobj;
    }

    /**
     * @return the vch_catg_new_vehicle
     */
    public String getVch_catg_new_vehicle() {
        return vch_catg_new_vehicle;
    }

    /**
     * @param vch_catg_new_vehicle the vch_catg_new_vehicle to set
     */
    public void setVch_catg_new_vehicle(String vch_catg_new_vehicle) {
        this.vch_catg_new_vehicle = vch_catg_new_vehicle;
    }

    /**
     * @return the vch_catg_old_vehicle
     */
    public String getVch_catg_old_vehicle() {
        return vch_catg_old_vehicle;
    }

    /**
     * @param vch_catg_old_vehicle the vch_catg_old_vehicle to set
     */
    public void setVch_catg_old_vehicle(String vch_catg_old_vehicle) {
        this.vch_catg_old_vehicle = vch_catg_old_vehicle;
    }

    /**
     * @return the newVehicleOwnercd
     */
    public int getNewVehicleOwnercd() {
        return newVehicleOwnercd;
    }

    /**
     * @param newVehicleOwnercd the newVehicleOwnercd to set
     */
    public void setNewVehicleOwnercd(int newVehicleOwnercd) {
        this.newVehicleOwnercd = newVehicleOwnercd;
    }

    /**
     * @return the oldVehicleOwnercd
     */
    public int getOldVehicleOwnercd() {
        return oldVehicleOwnercd;
    }

    /**
     * @param oldVehicleOwnercd the oldVehicleOwnercd to set
     */
    public void setOldVehicleOwnercd(int oldVehicleOwnercd) {
        this.oldVehicleOwnercd = oldVehicleOwnercd;
    }
}
