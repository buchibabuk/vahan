/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.commoncarrier;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author acer
 */
public class DetailCommonCarrierDobj implements Serializable, Cloneable {

    private String rcptNo;
    private String applNo;
    private String organijationName;
    private long contactNumber;
    private String address;
    private Date validFrom;
    private Date validUpto;
    private String locState;
    private String addContact;
    private String certre;
    private Date dateCommencement;
    private String personAuthorised;
    private String regnNo;
    private Integer vehcleClass;
    private String dealerCd;
    private Date issueDate;
    private Integer offCd;
    private String stateCd;
    private String branchLocation;
    private String branchAddress;
    private String branchCenter;
    private Date branchDateCommencement;
    private String regnNoFirstPart;
    private Integer regnNoSecondPart;
    private boolean isRowRemove;
    private Integer srNo;
    private String duplicateRenewCert = "New_CC_Certificate";

    public String getRcptNo() {
        return rcptNo;
    }

    public void setRcptNo(String rcptNo) {
        this.rcptNo = rcptNo;
    }

    public String getOrganijationName() {
        return organijationName;
    }

    public void setOrganijationName(String organijationName) {
        this.organijationName = organijationName;
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    public long getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(long contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidUpto() {
        return validUpto;
    }

    public void setValidUpto(Date validUpto) {
        this.validUpto = validUpto;
    }

    public String getLocState() {
        return locState;
    }

    public void setLocState(String locState) {
        this.locState = locState;
    }

    public String getAddContact() {
        return addContact;
    }

    public void setAddContact(String addContact) {
        this.addContact = addContact;
    }

    public String getCertre() {
        return certre;
    }

    public void setCertre(String certre) {
        this.certre = certre;
    }

    public Date getDateCommencement() {
        return dateCommencement;
    }

    public void setDateCommencement(Date dateCommencement) {
        this.dateCommencement = dateCommencement;
    }

    public String getPersonAuthorised() {
        return personAuthorised;
    }

    public void setPersonAuthorised(String personAuthorised) {
        this.personAuthorised = personAuthorised;
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    public Integer getVehcleClass() {
        return vehcleClass;
    }

    public void setVehcleClass(Integer vehcleClass) {
        this.vehcleClass = vehcleClass;
    }

    public String getDealerCd() {
        return dealerCd;
    }

    public void setDealerCd(String dealerCd) {
        this.dealerCd = dealerCd;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Integer getOffCd() {
        return offCd;
    }

    public void setOffCd(Integer offCd) {
        this.offCd = offCd;
    }

    public String getStateCd() {
        return stateCd;
    }

    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    public String getBranchLocation() {
        return branchLocation;
    }

    public void setBranchLocation(String branchLocation) {
        this.branchLocation = branchLocation;
    }

    public String getBranchAddress() {
        return branchAddress;
    }

    public void setBranchAddress(String branchAddress) {
        this.branchAddress = branchAddress;
    }

    public String getBranchCenter() {
        return branchCenter;
    }

    public void setBranchCenter(String branchCenter) {
        this.branchCenter = branchCenter;
    }

    public Date getBranchDateCommencement() {
        return branchDateCommencement;
    }

    public void setBranchDateCommencement(Date branchDateCommencement) {
        this.branchDateCommencement = branchDateCommencement;
    }

    public String getRegnNoFirstPart() {
        return regnNoFirstPart;
    }

    public void setRegnNoFirstPart(String regnNoFirstPart) {
        this.regnNoFirstPart = regnNoFirstPart;
    }

    public Integer getRegnNoSecondPart() {
        return regnNoSecondPart;
    }

    public void setRegnNoSecondPart(Integer regnNoSecondPart) {
        this.regnNoSecondPart = regnNoSecondPart;
    }

    public boolean isIsRowRemove() {
        return isRowRemove;
    }

    public void setIsRowRemove(boolean isRowRemove) {
        this.isRowRemove = isRowRemove;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Integer getSrNo() {
        return srNo;
    }

    public void setSrNo(Integer srNo) {
        this.srNo = srNo;
    }

    @Override
    public String toString() {
        String str = "stateCd : " + stateCd + " branchLocation: " + branchLocation + " branchAddress: " + branchAddress
                + " branchCenter: " + branchCenter;
        return str;
    }

    public String getDuplicateRenewCert() {
        return duplicateRenewCert;
    }

    public void setDuplicateRenewCert(String duplicateRenewCert) {
        this.duplicateRenewCert = duplicateRenewCert;
    }
}
