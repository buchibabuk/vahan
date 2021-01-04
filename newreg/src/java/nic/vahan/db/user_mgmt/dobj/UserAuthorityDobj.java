/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.dobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tranC103
 */
public class UserAuthorityDobj implements Serializable {

    private int lowerBound = 1;
    private int upperBound = 9999;
    private int vehType;
    private List selectedVehClass = new ArrayList();
    private List permitType = new ArrayList();
    private List selectedPermitCatg = new ArrayList();
    private List selectedMakerType = new ArrayList();
    private String dealerCode;
    private boolean isFitOfficer = false;
    private boolean isEnforcementOfficer = false;
    private int team_id;
    private int permitTypeCount;
    private int permitTypeCatgCount;
    private String assignedOffice = "";
    private boolean allOfficeAuth = false;
    private boolean isSiqnUpload = false;
    private byte[] signatureFile;

    public int getVehType() {
        return vehType;
    }

    public void setVehType(int vehType) {
        this.vehType = vehType;
    }

    public List getSelectedVehClass() {
        return selectedVehClass;
    }

    public void setSelectedVehClass(List selectedVehClass) {
        this.selectedVehClass = selectedVehClass;
    }

    public List getPermitType() {
        return permitType;
    }

    public void setPermitType(List permitType) {
        this.permitType = permitType;
    }

    public List getSelectedPermitCatg() {
        return selectedPermitCatg;
    }

    public void setSelectedPermitCatg(List selectedPermitCatg) {
        this.selectedPermitCatg = selectedPermitCatg;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(int lowerBound) {
        this.lowerBound = lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(int upperBound) {
        this.upperBound = upperBound;
    }

    public String getDealerCode() {
        return dealerCode;
    }

    public void setDealerCode(String dealerCode) {
        this.dealerCode = dealerCode;
    }

    public boolean isIsFitOfficer() {
        return isFitOfficer;
    }

    public void setIsFitOfficer(boolean isFitOfficer) {
        this.isFitOfficer = isFitOfficer;
    }

    public boolean isIsEnforcementOfficer() {
        return isEnforcementOfficer;
    }

    public void setIsEnforcementOfficer(boolean isEnforcementOfficer) {
        this.isEnforcementOfficer = isEnforcementOfficer;
    }

    public List getSelectedMakerType() {
        return selectedMakerType;
    }

    public void setSelectedMakerType(List selectedMakerType) {
        this.selectedMakerType = selectedMakerType;
    }

    public int getTeam_id() {
        return team_id;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
    }

    /**
     * @return the permitTypeCount
     */
    public int getPermitTypeCount() {
        return permitTypeCount;
    }

    /**
     * @param permitTypeCount the permitTypeCount to set
     */
    public void setPermitTypeCount(int permitTypeCount) {
        this.permitTypeCount = permitTypeCount;
    }

    /**
     * @return the permitTypeCatgCount
     */
    public int getPermitTypeCatgCount() {
        return permitTypeCatgCount;
    }

    /**
     * @param permitTypeCatgCount the permitTypeCatgCount to set
     */
    public void setPermitTypeCatgCount(int permitTypeCatgCount) {
        this.permitTypeCatgCount = permitTypeCatgCount;
    }

    public String getAssignedOffice() {
        return assignedOffice;
    }

    public void setAssignedOffice(String assignedOffice) {
        this.assignedOffice = assignedOffice;
    }

    /**
     * @return the allOfficeAuth
     */
    public boolean isAllOfficeAuth() {
        return allOfficeAuth;
    }

    /**
     * @param allOfficeAuth the allOfficeAuth to set
     */
    public void setAllOfficeAuth(boolean allOfficeAuth) {
        this.allOfficeAuth = allOfficeAuth;
    }

    /**
     * @return the isSiqnUpload
     */
    public boolean isIsSiqnUpload() {
        return isSiqnUpload;
    }

    /**
     * @param isSiqnUpload the isSiqnUpload to set
     */
    public void setIsSiqnUpload(boolean isSiqnUpload) {
        this.isSiqnUpload = isSiqnUpload;
    }

    /**
     * @return the signatureFile
     */
    public byte[] getSignatureFile() {
        return signatureFile;
    }

    /**
     * @param signatureFile the signatureFile to set
     */
    public void setSignatureFile(byte[] signatureFile) {
        this.signatureFile = signatureFile;
    }
}
