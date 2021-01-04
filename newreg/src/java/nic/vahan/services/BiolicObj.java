/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services;

import java.util.Date;

public class BiolicObj implements java.io.Serializable {

    private String bioBioId;
    private Integer bioGender;
    private String bioGenderDesc;
    private String bioBloodGroup;
    private String bioBloodGroupname;
    private Integer bioQmQualcd;
    private String bioQmQualdesc;
    private String bioCitiZen;
    private String bioFirstName;
    private String bioMiddleName;
    private String bioLastName;
    private String bioFullName;
    private String bioNatName;
    private Date bioDob;
    private String dob;
    private Character bioOrganDonor;
    private String bioDependentRelation;
    private String bioSwdFullName;
    private String bioSwdFname;
    private String bioSwdMname;
    private String bioSwdLname;
    private String bioIdentityMark1;
    private String bioIdentityMark2;
    private String bioPermAdd1;
    private String bioPermAdd2;
    private String bioPermAdd3;
    private Integer bioPermPin;
    private String bioTempAdd1;
    private String bioTempAdd2;
    private String bioTempAdd3;
    private Integer bioTempPin;
    private String bioPhoneNo;
    private String bioMobileNo;
    private String bioAltMobileNo;
    private String bioBirthplace;
    private String bioEmailId;
    private byte[] biPhoto;
    private String pht;
    private String bioDlno;
    private Integer bioPermDistCd;
    private Long bioPermVillTownCd;
    private Character bioPermLocType;
    private Integer bioTempDistCd;
    private Long bioTempVillTownCd;
    private Character bioTempLocType;
    private Integer bioPermSdcode;
    private Integer bioTempSdcode;
    private Character bioRecGenesis;
    private Long bioAadhaarNo;
    private String bioAadhaarName;
    private String bioBioidSearch;
    private Character bioPerDetAadhaar;
    private String bioEndorsementNo;
    private Date bioEndorsedt;
    private Date bioEndorsetime;
    private Long bioTokenId;
    private byte[] bioDigest;
    private long bioApplno;
    private String bioNprNo;
    private String bioPoliceStncd;
    private Short bioStayperiodPresentAddr;
    private String fullAddress;
    //Added by rajesh on 23-03-2017
    private String bioPermDistName;
    private String bioPermVillTownName;
    private String bioPermSdName;
    private String bioPermLocal;
    private String bioTempDistName;
    private String bioTempVillTownName;
    private String bioTempSdName;
    private String bioTempLocal;
    private Character bioApplicantCatg;
    private String bioCommunityCd;
    private String bioStateCd;
    private String bioTransType;
    private String bioAddclm1;
    private String bioAddclm2;
    private Long bioAddclm3;

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getBioGenderDesc() {
        return bioGenderDesc;
    }

    public void setBioGenderDesc(String bioGenderDesc) {
        this.bioGenderDesc = bioGenderDesc;
    }

    public String getPht() {
        return pht;
    }

    public void setPht(String pht) {
        this.pht = pht;
    }

    public byte[] getBiPhoto() {
        return biPhoto;
    }

    public void setBiPhoto(byte[] biPhoto) {
        this.biPhoto = biPhoto;
    }

    public String getBioPoliceStncd() {
        return bioPoliceStncd;
    }

    public void setBioPoliceStncd(String bioPoliceStncd) {
        this.bioPoliceStncd = bioPoliceStncd;
    }

    public String getBioNprNo() {
        return bioNprNo;
    }

    public void setBioNprNo(String bioNprNo) {
        this.bioNprNo = bioNprNo;
    }

    public Integer getBioGender() {
        return bioGender;
    }

    public void setBioGender(Integer bioGender) {
        this.bioGender = bioGender;
    }

    public String getBioBloodGroup() {
        return bioBloodGroup;
    }

    public void setBioBloodGroup(String bioBloodGroup) {
        this.bioBloodGroup = bioBloodGroup;
    }

    public String getBioBloodGroupname() {
        return bioBloodGroupname;
    }

    public void setBioBloodGroupname(String bioBloodGroupname) {
        this.bioBloodGroupname = bioBloodGroupname;
    }

    public Integer getBioQmQualcd() {
        return bioQmQualcd;
    }

    public void setBioQmQualcd(Integer bioQmQualcd) {
        this.bioQmQualcd = bioQmQualcd;
    }

    public String getBioCitiZen() {
        return bioCitiZen;
    }

    public void setBioCitiZen(String bioCitiZen) {
        this.bioCitiZen = bioCitiZen;
    }

    public String getBioBioId() {
        return bioBioId;
    }

    public void setBioBioId(String bioBioId) {
        this.bioBioId = bioBioId;
    }

    public String getBioFirstName() {
        return bioFirstName;
    }

    public void setBioFirstName(String bioFirstName) {
        this.bioFirstName = bioFirstName;
    }

    public String getBioMiddleName() {
        return bioMiddleName;
    }

    public void setBioMiddleName(String bioMiddleName) {
        this.bioMiddleName = bioMiddleName;
    }

    public String getBioLastName() {
        return bioLastName;
    }

    public void setBioLastName(String bioLastName) {
        this.bioLastName = bioLastName;
    }

    public String getBioFullName() {
        return bioFullName;
    }

    public void setBioFullName(String bioFullName) {
        this.bioFullName = bioFullName;
    }

    public String getBioNatName() {
        return bioNatName;
    }

    public void setBioNatName(String bioNatName) {
        this.bioNatName = bioNatName;
    }

    public Date getBioDob() {
        return bioDob;
    }

    public void setBioDob(Date bioDob) {
        this.bioDob = bioDob;
    }

    public Character getBioOrganDonor() {
        return bioOrganDonor;
    }

    public void setBioOrganDonor(Character bioOrganDonor) {
        this.bioOrganDonor = bioOrganDonor;
    }

    public String getBioDependentRelation() {
        return bioDependentRelation;
    }

    public void setBioDependentRelation(String bioDependentRelation) {
        this.bioDependentRelation = bioDependentRelation;
    }

    public String getBioSwdFullName() {
        return bioSwdFullName;
    }

    public void setBioSwdFullName(String bioSwdFullName) {
        this.bioSwdFullName = bioSwdFullName;
    }

    public String getBioSwdFname() {
        return bioSwdFname;
    }

    public void setBioSwdFname(String bioSwdFname) {
        this.bioSwdFname = bioSwdFname;
    }

    public String getBioSwdMname() {
        return bioSwdMname;
    }

    public void setBioSwdMname(String bioSwdMname) {
        this.bioSwdMname = bioSwdMname;
    }

    public String getBioSwdLname() {
        return bioSwdLname;
    }

    public void setBioSwdLname(String bioSwdLname) {
        this.bioSwdLname = bioSwdLname;
    }

    public String getBioIdentityMark1() {
        return bioIdentityMark1;
    }

    public void setBioIdentityMark1(String bioIdentityMark1) {
        this.bioIdentityMark1 = bioIdentityMark1;
    }

    public String getBioIdentityMark2() {
        return bioIdentityMark2;
    }

    public void setBioIdentityMark2(String bioIdentityMark2) {
        this.bioIdentityMark2 = bioIdentityMark2;
    }

    public String getBioPermAdd1() {
        return bioPermAdd1;
    }

    public void setBioPermAdd1(String bioPermAdd1) {
        this.bioPermAdd1 = bioPermAdd1;
    }

    public String getBioPermAdd2() {
        return bioPermAdd2;
    }

    public void setBioPermAdd2(String bioPermAdd2) {
        this.bioPermAdd2 = bioPermAdd2;
    }

    public String getBioPermAdd3() {
        return bioPermAdd3;
    }

    public void setBioPermAdd3(String bioPermAdd3) {
        this.bioPermAdd3 = bioPermAdd3;
    }

    public Integer getBioPermPin() {
        return bioPermPin;
    }

    public void setBioPermPin(Integer bioPermPin) {
        this.bioPermPin = bioPermPin;
    }

    public String getBioTempAdd1() {
        return bioTempAdd1;
    }

    public void setBioTempAdd1(String bioTempAdd1) {
        this.bioTempAdd1 = bioTempAdd1;
    }

    public String getBioTempAdd2() {
        return bioTempAdd2;
    }

    public void setBioTempAdd2(String bioTempAdd2) {
        this.bioTempAdd2 = bioTempAdd2;
    }

    public String getBioTempAdd3() {
        return bioTempAdd3;
    }

    public void setBioTempAdd3(String bioTempAdd3) {
        this.bioTempAdd3 = bioTempAdd3;
    }

    public Integer getBioTempPin() {
        return bioTempPin;
    }

    public void setBioTempPin(Integer bioTempPin) {
        this.bioTempPin = bioTempPin;
    }

    public String getBioPhoneNo() {
        return bioPhoneNo;
    }

    public void setBioPhoneNo(String bioPhoneNo) {
        this.bioPhoneNo = bioPhoneNo;
    }

    public String getBioMobileNo() {
        return bioMobileNo;
    }

    public void setBioMobileNo(String bioMobileNo) {
        this.bioMobileNo = bioMobileNo;
    }

    public String getBioAltMobileNo() {
        return bioAltMobileNo;
    }

    public void setBioAltMobileNo(String bioAltMobileNo) {
        this.bioAltMobileNo = bioAltMobileNo;
    }

    public String getBioBirthplace() {
        return bioBirthplace;
    }

    public void setBioBirthplace(String bioBirthplace) {
        this.bioBirthplace = bioBirthplace;
    }

    public String getBioEmailId() {
        return bioEmailId;
    }

    public void setBioEmailId(String bioEmailId) {
        this.bioEmailId = bioEmailId;
    }

    public Long getBioAadhaarNo() {
        return bioAadhaarNo;
    }

    public void setBioAadhaarNo(Long bioAadhaarNo) {
        this.bioAadhaarNo = bioAadhaarNo;
    }

    public String getBioAadhaarName() {
        return bioAadhaarName;
    }

    public void setBioAadhaarName(String bioAadhaarName) {
        this.bioAadhaarName = bioAadhaarName;
    }

    public String getBioBioidSearch() {
        return bioBioidSearch;
    }

    public void setBioBioidSearch(String bioBioidSearch) {
        this.bioBioidSearch = bioBioidSearch;
    }

    public Character getBioPerDetAadhaar() {
        return bioPerDetAadhaar;
    }

    public void setBioPerDetAadhaar(Character bioPerDetAadhaar) {
        this.bioPerDetAadhaar = bioPerDetAadhaar;
    }

    public String getBioEndorsementNo() {
        return bioEndorsementNo;
    }

    public void setBioEndorsementNo(String bioEndorsementNo) {
        this.bioEndorsementNo = bioEndorsementNo;
    }

    public Date getBioEndorsedt() {
        return bioEndorsedt;
    }

    public void setBioEndorsedt(Date bioEndorsedt) {
        this.bioEndorsedt = bioEndorsedt;
    }

    public Date getBioEndorsetime() {
        return bioEndorsetime;
    }

    public void setBioEndorsetime(Date bioEndorsetime) {
        this.bioEndorsetime = bioEndorsetime;
    }

    public Long getBioTokenId() {
        return bioTokenId;
    }

    public void setBioTokenId(Long bioTokenId) {
        this.bioTokenId = bioTokenId;
    }

    public byte[] getBioDigest() {
        return bioDigest;
    }

    public void setBioDigest(byte[] bioDigest) {
        this.bioDigest = bioDigest;
    }

    public long getBioApplno() {
        return bioApplno;
    }

    public void setBioApplno(long bioApplno) {
        this.bioApplno = bioApplno;
    }

    public Short getBioStayperiodPresentAddr() {
        return bioStayperiodPresentAddr;
    }

    public void setBioStayperiodPresentAddr(Short bioStayperiodPresentAddr) {
        this.bioStayperiodPresentAddr = bioStayperiodPresentAddr;
    }

    public String getBioDlno() {
        return bioDlno;
    }

    public void setBioDlno(String bioDlno) {
        this.bioDlno = bioDlno;
    }

    public Integer getBioPermDistCd() {
        return bioPermDistCd;
    }

    public void setBioPermDistCd(Integer bioPermDistCd) {
        this.bioPermDistCd = bioPermDistCd;
    }

    public Long getBioPermVillTownCd() {
        return bioPermVillTownCd;
    }

    public void setBioPermVillTownCd(Long bioPermVillTownCd) {
        this.bioPermVillTownCd = bioPermVillTownCd;
    }

    public Character getBioPermLocType() {
        return bioPermLocType;
    }

    public void setBioPermLocType(Character bioPermLocType) {
        this.bioPermLocType = bioPermLocType;
    }

    public Integer getBioTempDistCd() {
        return bioTempDistCd;
    }

    public void setBioTempDistCd(Integer bioTempDistCd) {
        this.bioTempDistCd = bioTempDistCd;
    }

    public Long getBioTempVillTownCd() {
        return bioTempVillTownCd;
    }

    public void setBioTempVillTownCd(Long bioTempVillTownCd) {
        this.bioTempVillTownCd = bioTempVillTownCd;
    }

    public Character getBioTempLocType() {
        return bioTempLocType;
    }

    public void setBioTempLocType(Character bioTempLocType) {
        this.bioTempLocType = bioTempLocType;
    }

    public Integer getBioPermSdcode() {
        return bioPermSdcode;
    }

    public void setBioPermSdcode(Integer bioPermSdcode) {
        this.bioPermSdcode = bioPermSdcode;
    }

    public Integer getBioTempSdcode() {
        return bioTempSdcode;
    }

    public void setBioTempSdcode(Integer bioTempSdcode) {
        this.bioTempSdcode = bioTempSdcode;
    }

    public Character getBioRecGenesis() {
        return bioRecGenesis;
    }

    public void setBioRecGenesis(Character bioRecGenesis) {
        this.bioRecGenesis = bioRecGenesis;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getBioPermDistName() {
        return bioPermDistName;
    }

    public void setBioPermDistName(String bioPermDistName) {
        this.bioPermDistName = bioPermDistName;
    }

    public String getBioPermVillTownName() {
        return bioPermVillTownName;
    }

    public void setBioPermVillTownName(String bioPermVillTownName) {
        this.bioPermVillTownName = bioPermVillTownName;
    }

    public String getBioPermSdName() {
        return bioPermSdName;
    }

    public void setBioPermSdName(String bioPermSdName) {
        this.bioPermSdName = bioPermSdName;
    }

    public String getBioTempDistName() {
        return bioTempDistName;
    }

    public void setBioTempDistName(String bioTempDistName) {
        this.bioTempDistName = bioTempDistName;
    }

    public String getBioTempVillTownName() {
        return bioTempVillTownName;
    }

    public void setBioTempVillTownName(String bioTempVillTownName) {
        this.bioTempVillTownName = bioTempVillTownName;
    }

    public String getBioTempSdName() {
        return bioTempSdName;
    }

    public void setBioTempSdName(String bioTempSdName) {
        this.bioTempSdName = bioTempSdName;
    }

    public String getBioPermLocal() {
        return bioPermLocal;
    }

    public void setBioPermLocal(String bioPermLocal) {
        this.bioPermLocal = bioPermLocal;
    }

    public String getBioTempLocal() {
        return bioTempLocal;
    }

    public void setBioTempLocal(String bioTempLocal) {
        this.bioTempLocal = bioTempLocal;
    }

    public Character getBioApplicantCatg() {
        return bioApplicantCatg;
    }

    public void setBioApplicantCatg(Character bioApplicantCatg) {
        this.bioApplicantCatg = bioApplicantCatg;
    }

    public String getBioCommunityCd() {
        return bioCommunityCd;
    }

    public void setBioCommunityCd(String bioCommunityCd) {
        this.bioCommunityCd = bioCommunityCd;
    }

    public String getBioStateCd() {
        return bioStateCd;
    }

    public void setBioStateCd(String bioStateCd) {
        this.bioStateCd = bioStateCd;
    }

    public String getBioTransType() {
        return bioTransType;
    }

    public void setBioTransType(String bioTransType) {
        this.bioTransType = bioTransType;
    }

    public String getBioAddclm1() {
        return bioAddclm1;
    }

    public void setBioAddclm1(String bioAddclm1) {
        this.bioAddclm1 = bioAddclm1;
    }

    public String getBioAddclm2() {
        return bioAddclm2;
    }

    public void setBioAddclm2(String bioAddclm2) {
        this.bioAddclm2 = bioAddclm2;
    }

    public Long getBioAddclm3() {
        return bioAddclm3;
    }

    public void setBioAddclm3(Long bioAddclm3) {
        this.bioAddclm3 = bioAddclm3;
    }

    public String getBioQmQualdesc() {
        return bioQmQualdesc;
    }

    public void setBioQmQualdesc(String bioQmQualdesc) {
        this.bioQmQualdesc = bioQmQualdesc;
    }
}