/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services;

import java.io.Serializable;
import java.util.Date;

public class LlicenceObj implements Serializable {

    private String llLicno;
    private long llApplno;
    private String olaMastCode;
    private String olaName;
    private Date llIssuedt;
    private String llIssueauth;
    private String llIssuedesig;
    private String llRcptno;
    private Date llRcptdt;
    private Long llRcptamt;
    private Integer llPrntstatus;
    private Date llPrntdate;
    private String llImvnameTest;
    private String llDlno;
    private Character llPhotoImg;
    private Character llRtThumbImg;
    private Character llLtThumbImg;
    private Character llSignatureImg;
    private String llEndorsementNo;
    private Character llStatus;
    private String llRemarks;
    private Character llRecGenesis;
    private Date llVldfrdt;
    private Date llVldtodt;
    private Date llTestdt;
    private String llEndorseauth;
    private Date llEndorsedt;
    private Date llEndorsetime;
    private String llOldLicno;
    private String omRtoFullname;
    private String omRtoShortname;
    private String omOfficeTownname;

    public String getOmRtoFullname() {
        return omRtoFullname;
    }

    public void setOmRtoFullname(String omRtoFullname) {
        this.omRtoFullname = omRtoFullname;
    }

    public String getOmRtoShortname() {
        return omRtoShortname;
    }

    public void setOmRtoShortname(String omRtoShortname) {
        this.omRtoShortname = omRtoShortname;
    }

    public String getOmOfficeTownname() {
        return omOfficeTownname;
    }

    public void setOmOfficeTownname(String omOfficeTownname) {
        this.omOfficeTownname = omOfficeTownname;
    }

    public String getOlaName() {
        return olaName;
    }

    public void setOlaName(String olaName) {
        this.olaName = olaName;
    }

    public int getLltrCd() {
        return lltrCd;
    }

    public void setLltrCd(int lltrCd) {
        this.lltrCd = lltrCd;
    }

    public String getLlBioId() {
        return llBioId;
    }

    public void setLlBioId(String llBioId) {
        this.llBioId = llBioId;
    }
    private Long llTokenId;
    private byte[] llDigest;
    private long llImvidTestId;
    private int lltrCd;
    private String llBioId;

    public long getLlImvidTestId() {
        return llImvidTestId;
    }

    public void setLlImvidTestId(long llImvidTestId) {
        this.llImvidTestId = llImvidTestId;
    }

    public String getLlLicno() {
        return llLicno;
    }

    public void setLlLicno(String llLicno) {
        this.llLicno = llLicno;
    }
    /*public long getLlApplno() {
     return llApplno;
     }
     public void setLlApplno(long llApplno) {
     this.llApplno = llApplno;
     }*/

    public Date getLlIssuedt() {
        return llIssuedt;
    }

    public void setLlIssuedt(Date llIssuedt) {
        this.llIssuedt = llIssuedt;
    }

    public String getLlIssueauth() {
        return llIssueauth;
    }

    public void setLlIssueauth(String llIssueauth) {
        this.llIssueauth = llIssueauth;
    }

    public String getLlIssuedesig() {
        return llIssuedesig;
    }

    public void setLlIssuedesig(String llIssuedesig) {
        this.llIssuedesig = llIssuedesig;
    }

    public String getLlRcptno() {
        return llRcptno;
    }

    public void setLlRcptno(String llRcptno) {
        this.llRcptno = llRcptno;
    }

    public Date getLlRcptdt() {
        return llRcptdt;
    }

    public void setLlRcptdt(Date llRcptdt) {
        this.llRcptdt = llRcptdt;
    }

    public Long getLlRcptamt() {
        return llRcptamt;
    }

    public void setLlRcptamt(Long llRcptamt) {
        this.llRcptamt = llRcptamt;
    }

    public Integer getLlPrntstatus() {
        return llPrntstatus;
    }

    public void setLlPrntstatus(Integer llPrntstatus) {
        this.llPrntstatus = llPrntstatus;
    }

    public Date getLlPrntdate() {
        return llPrntdate;
    }

    public void setLlPrntdate(Date llPrntdate) {
        this.llPrntdate = llPrntdate;
    }

    public String getLlImvnameTest() {
        return llImvnameTest;
    }

    public void setLlImvnameTest(String llImvnameTest) {
        this.llImvnameTest = llImvnameTest;
    }

    public String getLlDlno() {
        return llDlno;
    }

    public void setLlDlno(String llDlno) {
        this.llDlno = llDlno;
    }

    public Character getLlPhotoImg() {
        return llPhotoImg;
    }

    public void setLlPhotoImg(Character llPhotoImg) {
        this.llPhotoImg = llPhotoImg;
    }

    public Character getLlRtThumbImg() {
        return llRtThumbImg;
    }

    public void setLlRtThumbImg(Character llRtThumbImg) {
        this.llRtThumbImg = llRtThumbImg;
    }

    public Character getLlLtThumbImg() {
        return llLtThumbImg;
    }

    public void setLlLtThumbImg(Character llLtThumbImg) {
        this.llLtThumbImg = llLtThumbImg;
    }

    public Character getLlSignatureImg() {
        return llSignatureImg;
    }

    public void setLlSignatureImg(Character llSignatureImg) {
        this.llSignatureImg = llSignatureImg;
    }

    public String getLlEndorsementNo() {
        return llEndorsementNo;
    }

    public void setLlEndorsementNo(String llEndorsementNo) {
        this.llEndorsementNo = llEndorsementNo;
    }

    public Character getLlStatus() {
        return llStatus;
    }

    public void setLlStatus(Character llStatus) {
        this.llStatus = llStatus;
    }

    public String getLlRemarks() {
        return llRemarks;
    }

    public void setLlRemarks(String llRemarks) {
        this.llRemarks = llRemarks;
    }

    public Character getLlRecGenesis() {
        return llRecGenesis;
    }

    public void setLlRecGenesis(Character llRecGenesis) {
        this.llRecGenesis = llRecGenesis;
    }

    public Date getLlVldfrdt() {
        return llVldfrdt;
    }

    public void setLlVldfrdt(Date llVldfrdt) {
        this.llVldfrdt = llVldfrdt;
    }

    public Date getLlVldtodt() {
        return llVldtodt;
    }

    public void setLlVldtodt(Date llVldtodt) {
        this.llVldtodt = llVldtodt;
    }

    public Date getLlTestdt() {
        return llTestdt;
    }

    public void setLlTestdt(Date llTestdt) {
        this.llTestdt = llTestdt;
    }

    public String getLlEndorseauth() {
        return llEndorseauth;
    }

    public void setLlEndorseauth(String llEndorseauth) {
        this.llEndorseauth = llEndorseauth;
    }

    public Date getLlEndorsedt() {
        return llEndorsedt;
    }

    public void setLlEndorsedt(Date llEndorsedt) {
        this.llEndorsedt = llEndorsedt;
    }

    public Date getLlEndorsetime() {
        return llEndorsetime;
    }

    public void setLlEndorsetime(Date llEndorsetime) {
        this.llEndorsetime = llEndorsetime;
    }

    public Long getLlTokenId() {
        return llTokenId;
    }

    public void setLlTokenId(Long llTokenId) {
        this.llTokenId = llTokenId;
    }

    public byte[] getLlDigest() {
        return llDigest;
    }

    public void setLlDigest(byte[] llDigest) {
        this.llDigest = llDigest;
    }

    public String getOlaMastCode() {
        return olaMastCode;
    }

    public void setOlaMastCode(String olaMastCode) {
        this.olaMastCode = olaMastCode;
    }

    public long getLlApplno() {
        return llApplno;
    }

    public void setLlApplno(long llApplno) {
        this.llApplno = llApplno;
    }

    public String getLlOldLicno() {
        return llOldLicno;
    }

    public void setLlOldLicno(String llOldLicno) {
        this.llOldLicno = llOldLicno;
    }
}
