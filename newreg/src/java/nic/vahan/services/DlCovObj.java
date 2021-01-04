package nic.vahan.services;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author acer
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DlCovObj {

    private String dcLicno;
    private int dcCovcd;
    private long endouserid;
    private String olacd;
    private String olaName;
    private long dcApplno;
    private String dcCovStatus;
    private String dcReflicType;
    private String dcReflicNo;
    private String dcEndorseNo;
    private String dcInvrgdesc;
    private String dcInvcrgNo;
    private Date dcAuthDt;
    private String dcAuthNo;
    private Date dcEndorsedt;
    private Date dcEndorsetime;
    private Long dcTokenId;
    private Character veBadgeIssue;
    private String covdesc;
    private String covabbrv;
    private String badgeNo;
    private String badgeIssuedt;
    private String badgeIssuedAuth;
    private String vecatg;
    private String covIssueAuthCode;
    private Date dlBacklogCovEndtime;
    private Date dcIssuedt;
    private String veShortdesc;
    // Two column added by rajesh on 23/07/2015 for dl backlog cov details
    private String dbcImvName;
    private String dbcImvDesig;
    private Date dlTestdate;
    private String covIssuedt;

    public Date getDlBacklogCovEndtime() {
        return dlBacklogCovEndtime;
    }

    public String getCovIssuedt() {
        return covIssuedt;
    }

    public void setCovIssuedt(String covIssuedt) {
        this.covIssuedt = covIssuedt;
    }

    public void setDlBacklogCovEndtime(Date dlBacklogCovEndtime) {
        this.dlBacklogCovEndtime = dlBacklogCovEndtime;
    }

    public String getCovIssueAuthCode() {
        return covIssueAuthCode;
    }

    public void setCovIssueAuthCode(String covIssueAuthCode) {
        this.covIssueAuthCode = covIssueAuthCode;
    }

    public Character getVeBadgeIssue() {
        return veBadgeIssue;
    }

    public void setVeBadgeIssue(Character veBadgeIssue) {
        this.veBadgeIssue = veBadgeIssue;
    }

    public String getCovdesc() {
        return covdesc;
    }

    public void setCovdesc(String covdesc) {
        this.covdesc = covdesc;
    }

    public String getVecatg() {
        return vecatg;
    }

    public void setVecatg(String vecatg) {
        this.vecatg = vecatg;
    }

    public String getDcLicno() {
        return dcLicno;
    }

    public void setDcLicno(String dcLicno) {
        this.dcLicno = dcLicno;
    }

    public int getDcCovcd() {
        return dcCovcd;
    }

    public void setDcCovcd(int dcCovcd) {
        this.dcCovcd = dcCovcd;
    }

    public String getCovabbrv() {
        return covabbrv;
    }

    public void setCovabbrv(String covabbrv) {
        this.covabbrv = covabbrv;
    }

    public long getEndouserid() {
        return endouserid;
    }

    public void setEndouserid(long endouserid) {
        this.endouserid = endouserid;
    }

    public String getOlacd() {
        return olacd;
    }

    public void setOlacd(String olacd) {
        this.olacd = olacd;
    }

    public String getOlaName() {
        return olaName;
    }

    public void setOlaName(String olaName) {
        this.olaName = olaName;
    }

    public long getDcApplno() {
        return dcApplno;
    }

    public void setDcApplno(long dcApplno) {
        this.dcApplno = dcApplno;
    }

    public String getDcCovStatus() {
        return dcCovStatus;
    }

    public void setDcCovStatus(String dcCovStatus) {
        this.dcCovStatus = dcCovStatus;
    }

    public String getDcReflicType() {
        return dcReflicType;
    }

    public void setDcReflicType(String dcReflicType) {
        this.dcReflicType = dcReflicType;
    }

    public String getDcReflicNo() {
        return dcReflicNo;
    }

    public void setDcReflicNo(String dcReflicNo) {
        this.dcReflicNo = dcReflicNo;
    }

    public String getDcEndorseNo() {
        return dcEndorseNo;
    }

    public void setDcEndorseNo(String dcEndorseNo) {
        this.dcEndorseNo = dcEndorseNo;
    }

    public String getDcInvrgdesc() {
        return dcInvrgdesc;
    }

    public void setDcInvrgdesc(String dcInvrgdesc) {
        this.dcInvrgdesc = dcInvrgdesc;
    }

    public String getDcInvcrgNo() {
        return dcInvcrgNo;
    }

    public void setDcInvcrgNo(String dcInvcrgNo) {
        this.dcInvcrgNo = dcInvcrgNo;
    }

    public Date getDcAuthDt() {
        return dcAuthDt;
    }

    public void setDcAuthDt(Date dcAuthDt) {
        this.dcAuthDt = dcAuthDt;
    }

    public String getDcAuthNo() {
        return dcAuthNo;
    }

    public void setDcAuthNo(String dcAuthNo) {
        this.dcAuthNo = dcAuthNo;
    }

    public Date getDcEndorsedt() {
        return dcEndorsedt;
    }

    public void setDcEndorsedt(Date dcEndorsedt) {
        this.dcEndorsedt = dcEndorsedt;
    }

    public Date getDcEndorsetime() {
        return dcEndorsetime;
    }

    public void setDcEndorsetime(Date dcEndorsetime) {
        this.dcEndorsetime = dcEndorsetime;
    }

    public Long getDcTokenId() {
        return dcTokenId;
    }

    public void setDcTokenId(Long dcTokenId) {
        this.dcTokenId = dcTokenId;
    }

    public Date getDcIssuedt() {
        return dcIssuedt;
    }

    public void setDcIssuedt(Date dcIssuedt) {
        this.dcIssuedt = dcIssuedt;
    }

    public String getVeShortdesc() {
        return veShortdesc;
    }

    public void setVeShortdesc(String veShortdesc) {
        this.veShortdesc = veShortdesc;
    }

    public String getDbcImvName() {
        return dbcImvName;
    }

    public void setDbcImvName(String dbcImvName) {
        this.dbcImvName = dbcImvName;
    }

    public String getDbcImvDesig() {
        return dbcImvDesig;
    }

    public void setDbcImvDesig(String dbcImvDesig) {
        this.dbcImvDesig = dbcImvDesig;
    }

    public String getBadgeNo() {
        return badgeNo;
    }

    public String getBadgeIssuedt() {
        return badgeIssuedt;
    }

    public String getBadgeIssuedAuth() {
        return badgeIssuedAuth;
    }

    public void setBadgeNo(String badgeNo) {
        this.badgeNo = badgeNo;
    }

    public void setBadgeIssuedt(String badgeIssuedt) {
        this.badgeIssuedt = badgeIssuedt;
    }

    public void setBadgeIssuedAuth(String badgeIssuedAuth) {
        this.badgeIssuedAuth = badgeIssuedAuth;
    }

    public Date getDlTestdate() {
        return dlTestdate;
    }

    public void setDlTestdate(Date dlTestdate) {
        this.dlTestdate = dlTestdate;
    }
}
