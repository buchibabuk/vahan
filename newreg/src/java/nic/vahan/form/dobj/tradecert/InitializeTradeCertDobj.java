package nic.vahan.form.dobj.tradecert;

import java.io.Serializable;
import java.util.Date;

public class InitializeTradeCertDobj implements Serializable {

    private String stateCd;
    private Integer offCd;
    private String userCd;
    private Date tcStartingDate;
    private String modulePrefix;
    private Integer sequenceNo;
    private String applicantType;
    private String enteredBy;

    /**
     * @return the stateCd
     */
    public String getStateCd() {
        return stateCd;
    }

    /**
     * @param stateCd the stateCd to set
     */
    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    /**
     * @return the offCd
     */
    public Integer getOffCd() {
        return offCd;
    }

    /**
     * @param offCd the offCd to set
     */
    public void setOffCd(Integer offCd) {
        this.offCd = offCd;
    }

    /**
     * @return the userCd
     */
    public String getUserCd() {
        return userCd;
    }

    /**
     * @param userCd the userCd to set
     */
    public void setUserCd(String userCd) {
        this.userCd = userCd;
    }

    /**
     * @return the tcStartingDate
     */
    public Date getTcStartingDate() {
        return tcStartingDate;
    }

    /**
     * @param tcStartingDate the tcStartingDate to set
     */
    public void setTcStartingDate(Date tcStartingDate) {
        this.tcStartingDate = tcStartingDate;
    }

    /**
     * @return the modulePrefix
     */
    public String getModulePrefix() {
        return modulePrefix;
    }

    /**
     * @param modulePrefix the modulePrefix to set
     */
    public void setModulePrefix(String modulePrefix) {
        this.modulePrefix = modulePrefix;
    }

    /**
     * @return the sequenceNo
     */
    public Integer getSequenceNo() {
        return sequenceNo;
    }

    /**
     * @param sequenceNo the sequenceNo to set
     */
    public void setSequenceNo(Integer sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    /**
     * @return the applicantType
     */
    public String getApplicantType() {
        return applicantType;
    }

    /**
     * @param applicantType the applicantType to set
     */
    public void setApplicantType(String applicantType) {
        this.applicantType = applicantType;
    }

    /**
     * @return the enteredBy
     */
    public String getEnteredBy() {
        return enteredBy;
    }

    /**
     * @param enteredBy the enteredBy to set
     */
    public void setEnteredBy(String enteredBy) {
        this.enteredBy = enteredBy;
    }
}
