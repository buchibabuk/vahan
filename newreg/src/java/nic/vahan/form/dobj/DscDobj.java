/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import app.eoffice.dsc.xml.common.Revocation;
import app.eoffice.dsc.xml.response.ChainCertificates.CertificateDetail;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Niraj
 */
public class DscDobj implements Serializable {

    private String serialNo;
    private String userName;
    private String certValidUpto;
    private Revocation revocationSt;
    private String rtoUser;
    private int offcd;
    private String stateCd;
    private String revocstr;
    private String certlevel;
    private String cdpPoint;
    private byte[] data;
    private StreamedContent doFile;
    private String chainCertXml;
    private String activeStatus;
    private List<CertificateDetail> chainCertificates;
    private Date certValidDate;
    private String digitalSignToPrint;
    private String certContent;
    private String dealerName;
    private String dealerCd;
    private boolean verifyStatus;
    private Long dealerAdminUserCd;
    private boolean showActionBtn;
    private int vendorCd;
    private String vendorName;
    private String serailNoInHexaDecimalForm;

    @Override
    public int hashCode() {
        String hash = this.serialNo + ":" + this.vendorName;
        return hash.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DscDobj)) {
            return false;
        }
        DscDobj ob = (DscDobj) obj;
        if (ob.serialNo != null && ob.vendorName != null && !ob.serialNo.concat(ob.vendorName).equals(this.serialNo.concat(ob.vendorName))) {
            return false;
        }
        return true;
    }

    public String getCertlevel() {
        return certlevel;
    }

    public void setCertlevel(String certlevel) {
        this.certlevel = certlevel;
    }

    public String getCdpPoint() {
        return cdpPoint;
    }

    public void setCdpPoint(String cdpPoint) {
        this.cdpPoint = cdpPoint;
    }

    public String getChainCertXml() {
        return chainCertXml;
    }

    public void setChainCertXml(String chainCertXml) {
        this.chainCertXml = chainCertXml;
    }

    public String getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }

    public String getRevocstr() {
        return revocstr;
    }

    public void setRevocstr(String revocstr) {
        this.revocstr = revocstr;
    }

    public String getRtoUser() {
        return rtoUser;
    }

    public void setRtoUser(String rtoUser) {
        this.rtoUser = rtoUser;
    }

    public int getOffcd() {
        return offcd;
    }

    public void setOffcd(int offcd) {
        this.offcd = offcd;
    }

    public String getStateCd() {
        return stateCd;
    }

    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    public Revocation getRevocationSt() {
        return revocationSt;
    }

    public void setRevocationSt(Revocation revocationSt) {
        this.revocationSt = revocationSt;
    }

    public String getCertValidUpto() {
        return certValidUpto;
    }

    public void setCertValidUpto(String certValidUpto) {
        this.certValidUpto = certValidUpto;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * @return the doFile
     */
    public StreamedContent getDoFile() {
        return doFile;
    }

    /**
     * @param doFile the doFile to set
     */
    public void setDoFile(StreamedContent doFile) {
        this.doFile = doFile;
    }

    /**
     * @return the chainCertificates
     */
    public List<CertificateDetail> getChainCertificates() {
        return chainCertificates;
    }

    /**
     * @param chainCertificates the chainCertificates to set
     */
    public void setChainCertificates(List<CertificateDetail> chainCertificates) {
        this.chainCertificates = chainCertificates;
    }

    /**
     * @return the certValidDate
     */
    public Date getCertValidDate() {
        return certValidDate;
    }

    /**
     * @param certValidDate the certValidDate to set
     */
    public void setCertValidDate(Date certValidDate) {
        this.certValidDate = certValidDate;
    }

    /**
     * @return the digitalSignToPrint
     */
    public String getDigitalSignToPrint() {
        return digitalSignToPrint;
    }

    /**
     * @param digitalSignToPrint the digitalSignToPrint to set
     */
    public void setDigitalSignToPrint(String digitalSignToPrint) {
        this.digitalSignToPrint = digitalSignToPrint;
    }

    /**
     * @return the certContent
     */
    public String getCertContent() {
        return certContent;
    }

    /**
     * @param certContent the certContent to set
     */
    public void setCertContent(String certContent) {
        this.certContent = certContent;
    }

    /**
     * @return the dealerName
     */
    public String getDealerName() {
        return dealerName;
    }

    /**
     * @param dealerName the dealerName to set
     */
    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    /**
     * @return the dealerCd
     */
    public String getDealerCd() {
        return dealerCd;
    }

    /**
     * @param dealerCd the dealerCd to set
     */
    public void setDealerCd(String dealerCd) {
        this.dealerCd = dealerCd;
    }

    /**
     * @return the verifyStatus
     */
    public boolean isVerifyStatus() {
        return verifyStatus;
    }

    /**
     * @param verifyStatus the verifyStatus to set
     */
    public void setVerifyStatus(boolean verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    /**
     * @return the dealerAdminUserCd
     */
    public Long getDealerAdminUserCd() {
        return dealerAdminUserCd;
    }

    /**
     * @param dealerAdminUserCd the dealerAdminUserCd to set
     */
    public void setDealerAdminUserCd(Long dealerAdminUserCd) {
        this.dealerAdminUserCd = dealerAdminUserCd;
    }

    /**
     * @return the showActionBtn
     */
    public boolean isShowActionBtn() {
        return showActionBtn;
    }

    /**
     * @param showActionBtn the showActionBtn to set
     */
    public void setShowActionBtn(boolean showActionBtn) {
        this.showActionBtn = showActionBtn;
    }

    /**
     * @return the vendorCd
     */
    public int getVendorCd() {
        return vendorCd;
    }

    /**
     * @param vendorCd the vendorCd to set
     */
    public void setVendorCd(int vendorCd) {
        this.vendorCd = vendorCd;
    }

    /**
     * @return the vendorName
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * @param vendorName the vendorName to set
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    /**
     * @return the serailNoInHexaDecimalForm
     */
    public String getSerailNoInHexaDecimalForm() {
        return serailNoInHexaDecimalForm;
    }

    /**
     * @param serailNoInHexaDecimalForm the serailNoInHexaDecimalForm to set
     */
    public void setSerailNoInHexaDecimalForm(String serailNoInHexaDecimalForm) {
        this.serailNoInHexaDecimalForm = serailNoInHexaDecimalForm;
    }
}
