/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.eChallan;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Administrator
 */
public class ChallanReportDobj implements Serializable {

    private String vcr_no;
    private String Court_name;
    private String appl_no;
    private String offence_place;
    private String vehicle_no;
    private String chasi_no;
    private String owner_name;
    private Date opdate = new Date();
    private String owner_address;
    private String owner_mobile_no;
    private String driver_name;
    private String driver_address;
    private String conductor_name;
    private String conductor_address;
    private String witness_name;
    private String witness_address;
    private String permit_no;
    private String Police_station;
    private Date permit_validity;
    private Date tax_paid_upto;
    private Date insurance_validity;
    private Date fitness_validity;
    private String driving_licence_no;
    private String remark;
    private String vh_class;
    private Date chal_date;
    private String chal_time;
    private String chal_place;
    private String rcpt_heading;
    private String rcpt_sub_heading;
    private String chal_amnt;
    private String chal_officer;
    private String rto_name;
    private String offence;
    private String accused;
    private String recieptHeading;
    private String recieptSubHeading;
    private String accused_addr;
    private int offence_amnt;
    private String section;
    private String Reporting_off;
    private Date hearing_date;
    private String referCourt;
    private String magistrateName;
    private Date dispose_date;
    private String reciept_no;
    private Date regn_dt;
    private int chal_officer_cd;
    private String commingFrom;
    private String goingTo;
    private long compFee;
    private long adCompFee;
    private long totalFee;
    private String challan_no;
    private String challan_date_descr;
    private String offenceDesc;
    private String challan_dt;
    private String officeDescr;
    private String officerDesig;

    /**
     * @return the vcr_no
     */
    public String getVcr_no() {
        return vcr_no;
    }

    /**
     * @param vcr_no the vcr_no to set
     */
    public void setVcr_no(String vcr_no) {
        this.vcr_no = vcr_no;
    }

    /**
     * @return the offence_place
     */
    public String getOffence_place() {
        return offence_place;
    }

    /**
     * @param offence_place the offence_place to set
     */
    public void setOffence_place(String offence_place) {
        this.offence_place = offence_place;
    }

    /**
     * @return the vehicle_no
     */
    public String getVehicle_no() {
        return vehicle_no;
    }

    /**
     * @param vehicle_no the vehicle_no to set
     */
    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }

    /**
     * @return the chasi_no
     */
    public String getChasi_no() {
        return chasi_no;
    }

    /**
     * @param chasi_no the chasi_no to set
     */
    public void setChasi_no(String chasi_no) {
        this.chasi_no = chasi_no;
    }

    /**
     * @return the owner_name
     */
    public String getOwner_name() {
        return owner_name;
    }

    /**
     * @param owner_name the owner_name to set
     */
    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    /**
     * @return the owner_address
     */
    public String getOwner_address() {
        return owner_address;
    }

    /**
     * @param owner_address the owner_address to set
     */
    public void setOwner_address(String owner_address) {
        this.owner_address = owner_address;
    }

    /**
     * @return the owner_mobile_no
     */
    public String getOwner_mobile_no() {
        return owner_mobile_no;
    }

    /**
     * @param owner_mobile_no the owner_mobile_no to set
     */
    public void setOwner_mobile_no(String owner_mobile_no) {
        this.owner_mobile_no = owner_mobile_no;
    }

    /**
     * @return the driver_name
     */
    public String getDriver_name() {
        return driver_name;
    }

    /**
     * @param driver_name the driver_name to set
     */
    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    /**
     * @return the driver_address
     */
    public String getDriver_address() {
        return driver_address;
    }

    /**
     * @param driver_address the driver_address to set
     */
    public void setDriver_address(String driver_address) {
        this.driver_address = driver_address;
    }

    /**
     * @return the conductor_name
     */
    public String getConductor_name() {
        return conductor_name;
    }

    /**
     * @param conductor_name the conductor_name to set
     */
    public void setConductor_name(String conductor_name) {
        this.conductor_name = conductor_name;
    }

    /**
     * @return the conductor_address
     */
    public String getConductor_address() {
        return conductor_address;
    }

    /**
     * @param conductor_address the conductor_address to set
     */
    public void setConductor_address(String conductor_address) {
        this.conductor_address = conductor_address;
    }

    /**
     * @return the witness_name
     */
    public String getWitness_name() {
        return witness_name;
    }

    /**
     * @param witness_name the witness_name to set
     */
    public void setWitness_name(String witness_name) {
        this.witness_name = witness_name;
    }

    /**
     * @return the witness_address
     */
    public String getWitness_address() {
        return witness_address;
    }

    /**
     * @param witness_address the witness_address to set
     */
    public void setWitness_address(String witness_address) {
        this.witness_address = witness_address;
    }

    /**
     * @return the permit_no
     */
    public String getPermit_no() {
        return permit_no;
    }

    /**
     * @param permit_no the permit_no to set
     */
    public void setPermit_no(String permit_no) {
        this.permit_no = permit_no;
    }

    /**
     * @return the permit_validity
     */
    public Date getPermit_validity() {
        return permit_validity;
    }

    /**
     * @param permit_validity the permit_validity to set
     */
    public void setPermit_validity(Date permit_validity) {
        this.permit_validity = permit_validity;
    }

    /**
     * @return the tax_paid_upto
     */
    public Date getTax_paid_upto() {
        return tax_paid_upto;
    }

    /**
     * @param tax_paid_upto the tax_paid_upto to set
     */
    public void setTax_paid_upto(Date tax_paid_upto) {
        this.tax_paid_upto = tax_paid_upto;
    }

    /**
     * @return the insurance_validity
     */
    public Date getInsurance_validity() {
        return insurance_validity;
    }

    /**
     * @param insurance_validity the insurance_validity to set
     */
    public void setInsurance_validity(Date insurance_validity) {
        this.insurance_validity = insurance_validity;
    }

    /**
     * @return the fitness_validity
     */
    public Date getFitness_validity() {
        return fitness_validity;
    }

    /**
     * @param fitness_validity the fitness_validity to set
     */
    public void setFitness_validity(Date fitness_validity) {
        this.fitness_validity = fitness_validity;
    }

    /**
     * @return the driving_licence_no
     */
    public String getDriving_licence_no() {
        return driving_licence_no;
    }

    /**
     * @param driving_licence_no the driving_licence_no to set
     */
    public void setDriving_licence_no(String driving_licence_no) {
        this.driving_licence_no = driving_licence_no;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark the remark to set
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the vh_class
     */
    public String getVh_class() {
        return vh_class;
    }

    /**
     * @param vh_class the vh_class to set
     */
    public void setVh_class(String vh_class) {
        this.vh_class = vh_class;
    }

    /**
     * @return the chal_date
     */
    public Date getChal_date() {
        return chal_date;
    }

    /**
     * @param chal_date the chal_date to set
     */
    public void setChal_date(Date chal_date) {
        this.chal_date = chal_date;
    }

    /**
     * @return the chal_time
     */
    public String getChal_time() {
        return chal_time;
    }

    /**
     * @param chal_time the chal_time to set
     */
    public void setChal_time(String chal_time) {
        this.chal_time = chal_time;
    }

    /**
     * @return the chal_place
     */
    public String getChal_place() {
        return chal_place;
    }

    /**
     * @param chal_place the chal_place to set
     */
    public void setChal_place(String chal_place) {
        this.chal_place = chal_place;
    }

    /**
     * @return the rcpt_heading
     */
    public String getRcpt_heading() {
        return rcpt_heading;
    }

    /**
     * @param rcpt_heading the rcpt_heading to set
     */
    public void setRcpt_heading(String rcpt_heading) {
        this.rcpt_heading = rcpt_heading;
    }

    /**
     * @return the rcpt_sub_heading
     */
    public String getRcpt_sub_heading() {
        return rcpt_sub_heading;
    }

    /**
     * @param rcpt_sub_heading the rcpt_sub_heading to set
     */
    public void setRcpt_sub_heading(String rcpt_sub_heading) {
        this.rcpt_sub_heading = rcpt_sub_heading;
    }

    /**
     * @return the chal_amnt
     */
    public String getChal_amnt() {
        return chal_amnt;
    }

    /**
     * @param chal_amnt the chal_amnt to set
     */
    public void setChal_amnt(String chal_amnt) {
        this.chal_amnt = chal_amnt;
    }

    /**
     * @return the chal_officer
     */
    public String getChal_officer() {
        return chal_officer;
    }

    /**
     * @param chal_officer the chal_officer to set
     */
    public void setChal_officer(String chal_officer) {
        this.chal_officer = chal_officer;
    }

    /**
     * @return the rto_name
     */
    public String getRto_name() {
        return rto_name;
    }

    /**
     * @param rto_name the rto_name to set
     */
    public void setRto_name(String rto_name) {
        this.rto_name = rto_name;
    }

    /**
     * @return the offence
     */
    public String getOffence() {
        return offence;
    }

    /**
     * @param offence the offence to set
     */
    public void setOffence(String offence) {
        this.offence = offence;
    }

    /**
     * @return the accused
     */
    public String getAccused() {
        return accused;
    }

    /**
     * @param accused the accused to set
     */
    public void setAccused(String accused) {
        this.accused = accused;
    }

    /**
     * @return the offence_amnt
     */
    public int getOffence_amnt() {
        return offence_amnt;
    }

    /**
     * @param offence_amnt the offence_amnt to set
     */
    public void setOffence_amnt(int offence_amnt) {
        this.offence_amnt = offence_amnt;
    }

    /**
     * @return the section
     */
    public String getSection() {
        return section;
    }

    /**
     * @param section the section to set
     */
    public void setSection(String section) {
        this.section = section;
    }

    /**
     * @return the hearing_date
     */
    public Date getHearing_date() {
        return hearing_date;
    }

    /**
     * @param hearing_date the hearing_date to set
     */
    public void setHearing_date(Date hearing_date) {
        this.hearing_date = hearing_date;
    }

    /**
     * @return the referCourt
     */
    public String getReferCourt() {
        return referCourt;
    }

    /**
     * @param referCourt the referCourt to set
     */
    public void setReferCourt(String referCourt) {
        this.referCourt = referCourt;
    }

    /**
     * @return the magistrateName
     */
    public String getMagistrateName() {
        return magistrateName;
    }

    /**
     * @param magistrateName the magistrateName to set
     */
    public void setMagistrateName(String magistrateName) {
        this.magistrateName = magistrateName;
    }

    /**
     * @return the dispose_date
     */
    public Date getDispose_date() {
        return dispose_date;
    }

    /**
     * @param dispose_date the dispose_date to set
     */
    public void setDispose_date(Date dispose_date) {
        this.dispose_date = dispose_date;
    }

    /**
     * @return the reciept_no
     */
    public String getReciept_no() {
        return reciept_no;
    }

    /**
     * @param reciept_no the reciept_no to set
     */
    public void setReciept_no(String reciept_no) {
        this.reciept_no = reciept_no;
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
     * @return the chal_officer_cd
     */
    public int getChal_officer_cd() {
        return chal_officer_cd;
    }

    /**
     * @param chal_officer_cd the chal_officer_cd to set
     */
    public void setChal_officer_cd(int chal_officer_cd) {
        this.chal_officer_cd = chal_officer_cd;
    }

    public String getCommingFrom() {
        return commingFrom;
    }

    public void setCommingFrom(String commingFrom) {
        this.commingFrom = commingFrom;
    }

    public String getGoingTo() {
        return goingTo;
    }

    public void setGoingTo(String goingTo) {
        this.goingTo = goingTo;
    }

    public long getCompFee() {
        return compFee;
    }

    public void setCompFee(long compFee) {
        this.compFee = compFee;
    }

    public long getAdCompFee() {
        return adCompFee;
    }

    public void setAdCompFee(long adCompFee) {
        this.adCompFee = adCompFee;
    }

    public long getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(long totalFee) {
        this.totalFee = totalFee;
    }

    /**
     * @return the challan_no
     */
    public String getChallan_no() {
        return challan_no;
    }

    /**
     * @param challan_no the challan_no to set
     */
    public void setChallan_no(String challan_no) {
        this.challan_no = challan_no;
    }

    /**
     * @return the challan_date_descr
     */
    public String getChallan_date_descr() {
        return challan_date_descr;
    }

    /**
     * @param challan_date_descr the challan_date_descr to set
     */
    public void setChallan_date_descr(String challan_date_descr) {
        this.challan_date_descr = challan_date_descr;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getCourt_name() {
        return Court_name;
    }

    public void setCourt_name(String Court_name) {
        this.Court_name = Court_name;
    }

    public String getAccused_addr() {
        return accused_addr;
    }

    public void setAccused_addr(String accused_addr) {
        this.accused_addr = accused_addr;
    }

    public String getReporting_off() {
        return Reporting_off;
    }

    public void setReporting_off(String Reporting_off) {
        this.Reporting_off = Reporting_off;
    }

    public String getPolice_station() {
        return Police_station;
    }

    public void setPolice_station(String Police_station) {
        this.Police_station = Police_station;
    }

    public String getRecieptHeading() {
        return recieptHeading;
    }

    public void setRecieptHeading(String recieptHeading) {
        this.recieptHeading = recieptHeading;
    }

    public String getRecieptSubHeading() {
        return recieptSubHeading;
    }

    public void setRecieptSubHeading(String recieptSubHeading) {
        this.recieptSubHeading = recieptSubHeading;
    }

    public Date getOpdate() {
        return opdate;
    }

    public void setOpdate(Date opdate) {
        this.opdate = opdate;
    }

    /**
     * @return the offenceDesc
     */
    public String getOffenceDesc() {
        return offenceDesc;
    }

    /**
     * @param offenceDesc the offenceDesc to set
     */
    public void setOffenceDesc(String offenceDesc) {
        this.offenceDesc = offenceDesc;
    }

    /**
     * @return the challan_dt
     */
    public String getChallan_dt() {
        return challan_dt;
    }

    /**
     * @param challan_dt the challan_dt to set
     */
    public void setChallan_dt(String challan_dt) {
        this.challan_dt = challan_dt;
    }

    /**
     * @return the officeDescr
     */
    public String getOfficeDescr() {
        return officeDescr;
    }

    /**
     * @param officeDescr the officeDescr to set
     */
    public void setOfficeDescr(String officeDescr) {
        this.officeDescr = officeDescr;
    }

    /**
     * @return the officerDesig
     */
    public String getOfficerDesig() {
        return officerDesig;
    }

    /**
     * @param officerDesig the officerDesig to set
     */
    public void setOfficerDesig(String officerDesig) {
        this.officerDesig = officerDesig;
    }
}
