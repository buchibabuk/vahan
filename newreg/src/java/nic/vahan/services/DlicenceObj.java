package nic.vahan.services;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Date;

/**
 *
 * @author acer
 */
public class DlicenceObj implements java.io.Serializable {

    private String dlLicno;
    private String bioid;
    private String olacode;
    private String olaName;
    private String statecd;
    private String stateName;
    private long dlApplno;
    private String dlStateCode;
    private String dlolaCode;
    private String dlBioId;
    private long dlUsid;
    private String dlOldLicno;
    private Date dlIssuedt;
    private Date dlTrValdfrDt;
    private Date dlTrValdtoDt;
    private Date dlNtValdfrDt;
    private Date dlNtValdtoDt;
    private Date dlHzValdfrDt;
    private Date dlHzValdtoDt;
    private Date dlHlValdfrDt;
    private Date dlHlValdtoDt;
    private String dlIssueauth;
    private String dlIssuedesig;
    private String dlEndorseno;
    private String dlEndorseAuth;
    private String dlInvcrgNo;
    private String dlAuthNo;
    private String dlAuthIssauth;
    private Date dlAuthDt;
    private String dlAuthCov;
    private Character dlRecGenesis;
    private int dlLatestTrcode;
    private String dlStatus;
    private String dlRemarks;
    private Date dlEndorsedt;
    private Date dlEndorsetime;
    private Long dlTokenId;
    private byte[] dlDigest;
    private String dlSeqno;
    private String dlRtoCode;
    private String omRtoFullname;
    private String omRtoShortname;
    private String omOfficeTownname;
    private String dlIssueDate;
    private String dlNtValdtoDate;
    private String dlTrValdtoDate;
    private String dlHzValdtoDate;
    private String dlHlValdtoDate;
    private Character dlDispatchStatus;
    private Date dlPrintDate;
    private Character dlPrintStatus;

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

    public String getDlStateCode() {
        return dlStateCode;
    }

    public void setDlStateCode(String dlStateCode) {
        this.dlStateCode = dlStateCode;
    }

    public String getDlolaCode() {
        return dlolaCode;
    }

    public void setDlolaCode(String dlolaCode) {
        this.dlolaCode = dlolaCode;
    }

    public String getDlBioId() {
        return dlBioId;
    }

    public void setDlBioId(String dlBioId) {
        this.dlBioId = dlBioId;
    }

    public long getDlUsid() {
        return dlUsid;
    }

    public void setDlUsid(long dlUsid) {
        this.dlUsid = dlUsid;
    }

    public void setDlDigest(byte[] dlDigest) {
        this.dlDigest = dlDigest;
    }

    public String getDlLicno() {
        return dlLicno;
    }

    public void setDlLicno(String dlLicno) {
        this.dlLicno = dlLicno;
    }

    public String getBioid() {
        return bioid;
    }

    public void setBioid(String bioid) {
        this.bioid = bioid;
    }

    public String getOlacode() {
        return olacode;
    }

    public void setOlacode(String olacode) {
        this.olacode = olacode;
    }

    public String getOlaName() {
        return olaName;
    }

    public void setOlaName(String olaName) {
        this.olaName = olaName;
    }

    public String getStatecd() {
        return statecd;
    }

    public void setStatecd(String statecd) {
        this.statecd = statecd;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public long getDlApplno() {
        return dlApplno;
    }

    public void setDlApplno(long dlApplno) {
        this.dlApplno = dlApplno;
    }

    public String getDlOldLicno() {
        return dlOldLicno;
    }

    public void setDlOldLicno(String dlOldLicno) {
        this.dlOldLicno = dlOldLicno;
    }

    public Date getDlIssuedt() {
        return dlIssuedt;
    }

    public void setDlIssuedt(Date dlIssuedt) {
        this.dlIssuedt = dlIssuedt;
    }

    public Date getDlTrValdfrDt() {
        return dlTrValdfrDt;
    }

    public void setDlTrValdfrDt(Date dlTrValdfrDt) {
        this.dlTrValdfrDt = dlTrValdfrDt;
    }

    public Date getDlTrValdtoDt() {
        return dlTrValdtoDt;
    }

    public void setDlTrValdtoDt(Date dlTrValdtoDt) {
        this.dlTrValdtoDt = dlTrValdtoDt;
    }

    public Date getDlNtValdfrDt() {
        return dlNtValdfrDt;
    }

    public void setDlNtValdfrDt(Date dlNtValdfrDt) {
        this.dlNtValdfrDt = dlNtValdfrDt;
    }

    public Date getDlNtValdtoDt() {
        return dlNtValdtoDt;
    }

    public void setDlNtValdtoDt(Date dlNtValdtoDt) {
        this.dlNtValdtoDt = dlNtValdtoDt;
    }

    public Date getDlHzValdfrDt() {
        return dlHzValdfrDt;
    }

    public void setDlHzValdfrDt(Date dlHzValdfrDt) {
        this.dlHzValdfrDt = dlHzValdfrDt;
    }

    public Date getDlHzValdtoDt() {
        return dlHzValdtoDt;
    }

    public void setDlHzValdtoDt(Date dlHzValdtoDt) {
        this.dlHzValdtoDt = dlHzValdtoDt;
    }

    public Date getDlHlValdfrDt() {
        return dlHlValdfrDt;
    }

    public void setDlHlValdfrDt(Date dlHlValdfrDt) {
        this.dlHlValdfrDt = dlHlValdfrDt;
    }

    public Date getDlHlValdtoDt() {
        return dlHlValdtoDt;
    }

    public void setDlHlValdtoDt(Date dlHlValdtoDt) {
        this.dlHlValdtoDt = dlHlValdtoDt;
    }

    public String getDlIssueauth() {
        return dlIssueauth;
    }

    public void setDlIssueauth(String dlIssueauth) {
        this.dlIssueauth = dlIssueauth;
    }

    public String getDlIssuedesig() {
        return dlIssuedesig;
    }

    public void setDlIssuedesig(String dlIssuedesig) {
        this.dlIssuedesig = dlIssuedesig;
    }

    public String getDlEndorseno() {
        return dlEndorseno;
    }

    public void setDlEndorseno(String dlEndorseno) {
        this.dlEndorseno = dlEndorseno;
    }

    public String getDlEndorseAuth() {
        return dlEndorseAuth;
    }

    public void setDlEndorseAuth(String dlEndorseAuth) {
        this.dlEndorseAuth = dlEndorseAuth;
    }

    public String getDlInvcrgNo() {
        return dlInvcrgNo;
    }

    public void setDlInvcrgNo(String dlInvcrgNo) {
        this.dlInvcrgNo = dlInvcrgNo;
    }

    public String getDlAuthNo() {
        return dlAuthNo;
    }

    public void setDlAuthNo(String dlAuthNo) {
        this.dlAuthNo = dlAuthNo;
    }

    public String getDlAuthIssauth() {
        return dlAuthIssauth;
    }

    public void setDlAuthIssauth(String dlAuthIssauth) {
        this.dlAuthIssauth = dlAuthIssauth;
    }

    public Date getDlAuthDt() {
        return dlAuthDt;
    }

    public void setDlAuthDt(Date dlAuthDt) {
        this.dlAuthDt = dlAuthDt;
    }

    public String getDlAuthCov() {
        return dlAuthCov;
    }

    public void setDlAuthCov(String dlAuthCov) {
        this.dlAuthCov = dlAuthCov;
    }

    public Character getDlRecGenesis() {
        return dlRecGenesis;
    }

    public void setDlRecGenesis(Character dlRecGenesis) {
        this.dlRecGenesis = dlRecGenesis;
    }

    public int getDlLatestTrcode() {
        return dlLatestTrcode;
    }

    public void setDlLatestTrcode(int dlLatestTrcode) {
        this.dlLatestTrcode = dlLatestTrcode;
    }

    public String getDlStatus() {
        return dlStatus;
    }

    public void setDlStatus(String dlStatus) {
        this.dlStatus = dlStatus;
    }

    public String getDlRemarks() {
        return dlRemarks;
    }

    public void setDlRemarks(String dlRemarks) {
        this.dlRemarks = dlRemarks;
    }

    public Date getDlEndorsedt() {
        return dlEndorsedt;
    }

    public void setDlEndorsedt(Date dlEndorsedt) {
        this.dlEndorsedt = dlEndorsedt;
    }

    public Date getDlEndorsetime() {
        return dlEndorsetime;
    }

    public void setDlEndorsetime(Date dlEndorsetime) {
        this.dlEndorsetime = dlEndorsetime;
    }

    public Long getDlTokenId() {
        return dlTokenId;
    }

    public void setDlTokenId(Long dlTokenId) {
        this.dlTokenId = dlTokenId;
    }

    public byte[] getDlDigest() {
        return dlDigest;
    }

    public String getDlSeqno() {
        return dlSeqno;
    }

    public void setDlSeqno(String dlSeqno) {
        this.dlSeqno = dlSeqno;
    }

    public String getDlRtoCode() {
        return dlRtoCode;
    }

    public void setDlRtoCode(String dlRtoCode) {
        this.dlRtoCode = dlRtoCode;
    }

    public String getDlIssueDate() {
        return dlIssueDate;
    }

    public void setDlIssueDate(String dlIssueDate) {
        this.dlIssueDate = dlIssueDate;
    }

    public String getDlNtValdtoDate() {
        return dlNtValdtoDate;
    }

    public void setDlNtValdtoDate(String dlNtValdtoDate) {
        this.dlNtValdtoDate = dlNtValdtoDate;
    }

    public String getDlTrValdtoDate() {
        return dlTrValdtoDate;
    }

    public void setDlTrValdtoDate(String dlTrValdtoDate) {
        this.dlTrValdtoDate = dlTrValdtoDate;
    }

    public String getDlHzValdtoDate() {
        return dlHzValdtoDate;
    }

    public void setDlHzValdtoDate(String dlHzValdtoDate) {
        this.dlHzValdtoDate = dlHzValdtoDate;
    }

    public String getDlHlValdtoDate() {
        return dlHlValdtoDate;
    }

    public void setDlHlValdtoDate(String dlHlValdtoDate) {
        this.dlHlValdtoDate = dlHlValdtoDate;
    }

    public Character getDlDispatchStatus() {
        return dlDispatchStatus;
    }

    public void setDlDispatchStatus(Character dlDispatchStatus) {
        this.dlDispatchStatus = dlDispatchStatus;
    }

    public Date getDlPrintDate() {
        return dlPrintDate;
    }

    public void setDlPrintDate(Date dlPrintDate) {
        this.dlPrintDate = dlPrintDate;
    }

    public Character getDlPrintStatus() {
        return dlPrintStatus;
    }

    public void setDlPrintStatus(Character dlPrintStatus) {
        this.dlPrintStatus = dlPrintStatus;
    }
}
