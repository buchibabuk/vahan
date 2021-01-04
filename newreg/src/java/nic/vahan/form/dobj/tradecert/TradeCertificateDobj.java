/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.tradecert;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.faces.model.SelectItem;

/**
 *
 * @author acer
 */
public class TradeCertificateDobj implements Serializable {

    private String applicantType, applicant_name, applicant_cd;
    private String applicant_state, applicant_state_cd;
    private String applicant_off, applicant_off_cd;
    private String applicantRelation, applicantAddress;
    private String appl_no, pur_cd;
    private int no_of_tc_required;
    private int totalFeeAmount, totalTaxAmount, totalFineAmount, totalNoOfVehicles, totalSurcharge, totalAmount;
    private String vehCatgCode, vehClassAppliedFromApplicant;
    private List<SelectItem> VehicleCatgApplicable;
    private String rupeesInWords;
    private Date applicantValidUpto;
    private String validUptoDateString;
    //////////After Payment//////////
    private String receiptNumber;
    private String paymentMode;
    private String flag;
    private String mobileOtp;
    private String emailOtp;
    private String applicantMobileNumber;
    private String applicantMailId;
    private List<String> multipleApplicantCodeList;
    private String add1;
    private String add2;
    private String district;
    private String pincode;
    private String IFSC;

    public String getApplicantType() {
        return applicantType;
    }

    public void setApplicantType(String applicantType) {
        this.applicantType = applicantType;
    }

    public String getMobileOtp() {
        return mobileOtp;
    }

    public void setMobileOtp(String mobileOtp) {
        this.mobileOtp = mobileOtp;
    }

    public String getEmailOtp() {
        return emailOtp;
    }

    public void setEmailOtp(String emailOtp) {
        this.emailOtp = emailOtp;
    }

    public String getApplicantMobileNumber() {
        return applicantMobileNumber;
    }

    public void setApplicantMobileNumber(String applicantMobileNumber) {
        this.applicantMobileNumber = applicantMobileNumber;
    }

    public String getApplicantMailId() {
        return applicantMailId;
    }

    public void setApplicantMailId(String applicantMailId) {
        this.applicantMailId = applicantMailId;
    }

    public List<String> getMultipleApplicantCodeList() {
        return multipleApplicantCodeList;
    }

    public void setMultipleApplicantCodeList(List<String> multipleApplicantCodeList) {
        this.multipleApplicantCodeList = multipleApplicantCodeList;
    }

    public String getAdd1() {
        return add1;
    }

    public void setAdd1(String add1) {
        this.add1 = add1;
    }

    public String getAdd2() {
        return add2;
    }

    public void setAdd2(String add2) {
        this.add2 = add2;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getIFSC() {
        return IFSC;
    }

    public void setIFSC(String IFSC) {
        this.IFSC = IFSC;
    }

    public class ListOfFeeDetail implements Serializable {

        private String vch_class_appliedDescr;
        private String vehCatgCode;
        private int no_of_tc_required, feeAmount, taxAmount, surCharge, sr_no, total;
        //renewal
        private String tradeCertNum;
        private Date previouslyValidUpto;
        private int previousNoOfTcs;

        /**
         * @return the vch_class_appliedDescr
         */
        public String getVch_class_appliedDescr() {
            return vch_class_appliedDescr;
        }

        /**
         * @param vch_class_appliedDescr the vch_class_appliedDescr to set
         */
        public void setVch_class_appliedDescr(String vch_class_appliedDescr) {
            this.vch_class_appliedDescr = vch_class_appliedDescr;
        }

        /**
         * @return the vehCatgCode
         */
        public String getVehCatgCode() {
            return vehCatgCode;
        }

        /**
         * @param vehCatgCode the vehCatgCode to set
         */
        public void setVehCatgCode(String vehCatgCode) {
            this.vehCatgCode = vehCatgCode;
        }

        /**
         * @return the no_of_tc_required
         */
        public int getNo_of_tc_required() {
            return no_of_tc_required;
        }

        /**
         * @param no_of_tc_required the no_of_tc_required to set
         */
        public void setNo_of_tc_required(int no_of_tc_required) {
            this.no_of_tc_required = no_of_tc_required;
        }

        /**
         * @return the feeAmount
         */
        public int getFeeAmount() {
            return feeAmount;
        }

        /**
         * @param feeAmount the feeAmount to set
         */
        public void setFeeAmount(int feeAmount) {
            this.feeAmount = feeAmount;
        }

        /**
         * @return the taxAmount
         */
        public int getTaxAmount() {
            return taxAmount;
        }

        /**
         * @param taxAmount the taxAmount to set
         */
        public void setTaxAmount(int taxAmount) {
            this.taxAmount = taxAmount;
        }

        /**
         * @return the surCharge
         */
        public int getSurCharge() {
            return surCharge;
        }

        /**
         * @param surCharge the surCharge to set
         */
        public void setSurCharge(int surCharge) {
            this.surCharge = surCharge;
        }

        /**
         * @return the sr_no
         */
        public int getSr_no() {
            return sr_no;
        }

        /**
         * @param sr_no the sr_no to set
         */
        public void setSr_no(int sr_no) {
            this.sr_no = sr_no;
        }

        /**
         * @return the total
         */
        public int getTotal() {
            return total;
        }

        /**
         * @param total the total to set
         */
        public void setTotal(int total) {
            this.total = total;
        }

        /**
         * @return the tradeCertNum
         */
        public String getTradeCertNum() {
            return tradeCertNum;
        }

        /**
         * @param tradeCertNum the tradeCertNum to set
         */
        public void setTradeCertNum(String tradeCertNum) {
            this.tradeCertNum = tradeCertNum;
        }

        /**
         * @return the previouslyValidUpto
         */
        public Date getPreviouslyValidUpto() {
            return previouslyValidUpto;
        }

        /**
         * @param previouslyValidUpto the previouslyValidUpto to set
         */
        public void setPreviouslyValidUpto(Date previouslyValidUpto) {
            this.previouslyValidUpto = previouslyValidUpto;
        }

        /**
         * @return the previousNoOfTcs
         */
        public int getPreviousNoOfTcs() {
            return previousNoOfTcs;
        }

        /**
         * @param previousNoOfTcs the previousNoOfTcs to set
         */
        public void setPreviousNoOfTcs(int previousNoOfTcs) {
            this.previousNoOfTcs = previousNoOfTcs;
        }
    }

    /**
     * @return the applicant_name
     */
    public String getApplicant_name() {
        return applicant_name;
    }

    /**
     * @param applicant_name the applicant_name to set
     */
    public void setApplicant_name(String applicant_name) {
        this.applicant_name = applicant_name;
    }

    /**
     * @return the applicant_state_cd
     */
    public String getApplicant_state_cd() {
        return applicant_state_cd;
    }

    /**
     * @param applicant_state_cd the applicant_state_cd to set
     */
    public void setApplicant_state_cd(String applicant_state_cd) {
        this.applicant_state_cd = applicant_state_cd;
    }

    /**
     * @return the applicant_state
     */
    public String getApplicant_state() {
        return applicant_state;
    }

    /**
     * @param applicant_state the applicant_state to set
     */
    public void setApplicant_state(String applicant_state) {
        this.applicant_state = applicant_state;
    }

    /**
     * @return the applicant_off
     */
    public String getApplicant_off() {
        return applicant_off;
    }

    /**
     * @param applicant_off the applicant_off to set
     */
    public void setApplicant_off(String applicant_off) {
        this.applicant_off = applicant_off;
    }

    /**
     * @return the applicant_off_cd
     */
    public String getApplicant_off_cd() {
        return applicant_off_cd;
    }

    /**
     * @param applicant_off_cd the applicant_off_cd to set
     */
    public void setApplicant_off_cd(String applicant_off_cd) {
        this.applicant_off_cd = applicant_off_cd;
    }

    /**
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }

    /**
     * @param appl_no the appl_no to set
     */
    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    /**
     * @return the applicant_cd
     */
    public String getApplicant_cd() {
        return applicant_cd;
    }

    /**
     * @param applicant_cd the applicant_cd to set
     */
    public void setApplicant_cd(String applicant_cd) {
        this.applicant_cd = applicant_cd;
    }

    /**
     * @return the VehicleCatgApplicable
     */
    public List<SelectItem> getVehicleCatgApplicable() {
        return VehicleCatgApplicable;
    }

    /**
     * @param VehicleCatgApplicable the VehicleCatgApplicable to set
     */
    public void setVehicleCatgApplicable(List<SelectItem> VehicleCatgApplicable) {
        this.VehicleCatgApplicable = VehicleCatgApplicable;
    }

    /**
     * @return the pur_cd
     */
    public String getPur_cd() {
        return pur_cd;
    }

    /**
     * @param pur_cd the pur_cd to set
     */
    public void setPur_cd(String pur_cd) {
        this.pur_cd = pur_cd;
    }

    /**
     * @return the no_of_tc_required
     */
    public int getNo_of_tc_required() {
        return no_of_tc_required;
    }

    /**
     * @param no_of_tc_required the no_of_tc_required to set
     */
    public void setNo_of_tc_required(int no_of_tc_required) {
        this.no_of_tc_required = no_of_tc_required;
    }

    /**
     * @return the vehCatgCode
     */
    public String getVehCatgCode() {
        return vehCatgCode;
    }

    /**
     * @param vehCatgCode the vehCatgCode to set
     */
    public void setVehCatgCode(String vehCatgCode) {
        this.vehCatgCode = vehCatgCode;
    }

    /**
     * @return the totalFeeAmount
     */
    public int getTotalFeeAmount() {
        return totalFeeAmount;
    }

    /**
     * @param totalFeeAmount the totalFeeAmount to set
     */
    public void setTotalFeeAmount(int totalFeeAmount) {
        this.totalFeeAmount = totalFeeAmount;
    }

    /**
     * @return the totalTaxAmount
     */
    public int getTotalTaxAmount() {
        return totalTaxAmount;
    }

    /**
     * @param totalTaxAmount the totalTaxAmount to set
     */
    public void setTotalTaxAmount(int totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
    }

    /**
     * @return the totalFineAmount
     */
    public int getTotalFineAmount() {
        return totalFineAmount;
    }

    /**
     * @param totalFineAmount the totalFineAmount to set
     */
    public void setTotalFineAmount(int totalFineAmount) {
        this.totalFineAmount = totalFineAmount;
    }

    /**
     * @return the totalNoOfVehicles
     */
    public int getTotalNoOfVehicles() {
        return totalNoOfVehicles;
    }

    /**
     * @param totalNoOfVehicles the totalNoOfVehicles to set
     */
    public void setTotalNoOfVehicles(int totalNoOfVehicles) {
        this.totalNoOfVehicles = totalNoOfVehicles;
    }

    /**
     * @return the totalSurcharge
     */
    public int getTotalSurcharge() {
        return totalSurcharge;
    }

    /**
     * @param totalSurcharge the totalSurcharge to set
     */
    public void setTotalSurcharge(int totalSurcharge) {
        this.totalSurcharge = totalSurcharge;
    }

    /**
     * @return the totalAmount
     */
    public int getTotalAmount() {
        return totalAmount;
    }

    /**
     * @param totalAmount the totalAmount to set
     */
    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * @return the rupeesInWords
     */
    public String getRupeesInWords() {
        return rupeesInWords;
    }

    /**
     * @param rupeesInWords the rupeesInWords to set
     */
    public void setRupeesInWords(String rupeesInWords) {
        this.rupeesInWords = rupeesInWords;
    }

    /**
     * @return the applicantRelation
     */
    public String getApplicantRelation() {
        return applicantRelation;
    }

    /**
     * @param applicantRelation the applicantRelation to set
     */
    public void setApplicantRelation(String applicantRelation) {
        this.applicantRelation = applicantRelation;
    }

    /**
     * @return the applicantAddress
     */
    public String getApplicantAddress() {
        return applicantAddress;
    }

    /**
     * @param applicantAddress the applicantAddress to set
     */
    public void setApplicantAddress(String applicantAddress) {
        this.applicantAddress = applicantAddress;
    }

    /**
     * @return the applicantValidUpto
     */
    public Date getApplicantValidUpto() {
        return applicantValidUpto;
    }

    /**
     * @param applicantValidUpto the applicantValidUpto to set
     */
    public void setApplicantValidUpto(Date applicantValidUpto) {
        this.applicantValidUpto = applicantValidUpto;
    }

    /**
     * @return the validUptoDateString
     */
    public String getValidUptoDateString() {
        return validUptoDateString;
    }

    /**
     * @param validUptoDateString the validUptoDateString to set
     */
    public void setValidUptoDateString(String validUptoDateString) {
        this.validUptoDateString = validUptoDateString;
    }

    /*
     * vehClassAppliedFromApplicant is for appended Vehicle Category
     */
    public String getVehClassAppliedFromApplicant() {
        return vehClassAppliedFromApplicant;
    }

    public void setVehClassAppliedFromApplicant(String vehClassAppliedFromApplicant) {
        this.vehClassAppliedFromApplicant = vehClassAppliedFromApplicant;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}