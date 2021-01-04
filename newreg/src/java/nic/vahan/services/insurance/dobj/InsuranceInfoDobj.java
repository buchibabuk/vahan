/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services.insurance.dobj;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ASHOK
 */
@XmlRootElement(name = "InsuranceInfoDobj")
public class InsuranceInfoDobj {

    private String regnNo;
    private String policyNo;
    private String insFrom;
    private String insUpto;
    private String opdt;
    private String issuerName;
    private int issuerCd;
    private int insuranceType;
    private boolean vahanVerify;
    private boolean iibData;
    private long idv;

    public InsuranceInfoDobj() {
    }

    public InsuranceInfoDobj(String regnNo, String policyNo, String insFrom, String insUpto, String opdt, String issuerName, int issuerCd, int insuranceType, boolean vahanVerify, boolean iibData, long idv) {
        this.regnNo = regnNo;
        this.policyNo = policyNo;
        this.insFrom = insFrom;
        this.insUpto = insUpto;
        this.opdt = opdt;
        this.issuerName = issuerName;
        this.issuerCd = issuerCd;
        this.insuranceType = insuranceType;
        this.vahanVerify = vahanVerify;
        this.iibData = iibData;
        this.idv = idv;
    }

    /**
     * @return the regnNo
     */
    public String getRegnNo() {
        return regnNo;
    }

    /**
     * @param regnNo the regnNo to set
     */
    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    /**
     * @return the policyNo
     */
    public String getPolicyNo() {
        return policyNo;
    }

    /**
     * @param policyNo the policyNo to set
     */
    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    /**
     * @return the insFrom
     */
    public String getInsFrom() {
        return insFrom;
    }

    /**
     * @param insFrom the insFrom to set
     */
    public void setInsFrom(String insFrom) {
        this.insFrom = insFrom;
    }

    /**
     * @return the insUpto
     */
    public String getInsUpto() {
        return insUpto;
    }

    /**
     * @param insUpto the insUpto to set
     */
    public void setInsUpto(String insUpto) {
        this.insUpto = insUpto;
    }

    /**
     * @return the opdt
     */
    public String getOpdt() {
        return opdt;
    }

    /**
     * @param opdt the opdt to set
     */
    public void setOpdt(String opdt) {
        this.opdt = opdt;
    }

    /**
     * @return the issuerName
     */
    public String getIssuerName() {
        return issuerName;
    }

    /**
     * @param issuerName the issuerName to set
     */
    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    /**
     * @return the issuerCd
     */
    public int getIssuerCd() {
        return issuerCd;
    }

    /**
     * @param issuerCd the issuerCd to set
     */
    public void setIssuerCd(int issuerCd) {
        this.issuerCd = issuerCd;
    }

    /**
     * @return the insuranceType
     */
    public int getInsuranceType() {
        return insuranceType;
    }

    /**
     * @param insuranceType the insuranceType to set
     */
    public void setInsuranceType(int insuranceType) {
        this.insuranceType = insuranceType;
    }

    /**
     * @return the vahanVerify
     */
    public boolean isVahanVerify() {
        return vahanVerify;
    }

    /**
     * @param vahanVerify the vahanVerify to set
     */
    public void setVahanVerify(boolean vahanVerify) {
        this.vahanVerify = vahanVerify;
    }

    public boolean isIibData() {
        return iibData;
    }

    public void setIibData(boolean iibData) {
        this.iibData = iibData;
    }

    public long getIdv() {
        return idv;
    }

    public void setIdv(long idv) {
        this.idv = idv;
    }

}
