/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.reports;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author ankur
 */
public class TaxClearanceCertificatePrintReportDobj implements Serializable {

    private String header;
    private String stateCD;
    private String applNO;
    private String stateName;
    private String offName;
    private String regnNO;
    private String vchDescr;
    private String subHeader;
    private String ownerName;
    private String regDate;
    private String makerName;
    private String modelName;
    private String clear_fr;
    private String clear_to;
    private String pur_cd;
    private String pur_cd_descr;
    private int tccCount;
    private String qrText;
    private ArrayList<TaxClearanceCertificatePrintReportSubDobj> taxPurposeList;
    private ArrayList<TaxClearanceCertificatePrintReportSubDobj> vtTaxDetailsList;
    private ArrayList<TaxClearanceCertificatePrintReportSubDobj> diffofTaxxDetailsList;
    private String tcc_no;
    private String trc_no;
    private String remark_vt_tax_clr;
    private String remark_vt_tax_exem;
    private String exem_fr;
    private String exem_to;
    private String image_background;
    private String image_logo;
    private boolean show_image_background;
    private boolean show_image_logo;
    private boolean showRoadSafetySlogan;
    private VmRoadSafetySloganPrintDobj roadSafetySloganDobj = null;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getStateCD() {
        return stateCD;
    }

    public void setStateCD(String stateCD) {
        this.stateCD = stateCD;
    }

    public String getApplNO() {
        return applNO;
    }

    public void setApplNO(String applNO) {
        this.applNO = applNO;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getOffName() {
        return offName;
    }

    public void setOffName(String offName) {
        this.offName = offName;
    }

    public String getRegnNO() {
        return regnNO;
    }

    public void setRegnNO(String regnNO) {
        this.regnNO = regnNO;
    }

    public String getVchDescr() {
        return vchDescr;
    }

    public void setVchDescr(String vchDescr) {
        this.vchDescr = vchDescr;
    }

    public String getSubHeader() {
        return subHeader;
    }

    public void setSubHeader(String subHeader) {
        this.subHeader = subHeader;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getMakerName() {
        return makerName;
    }

    public void setMakerName(String makerName) {
        this.makerName = makerName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getClear_fr() {
        return clear_fr;
    }

    public void setClear_fr(String clear_fr) {
        this.clear_fr = clear_fr;
    }

    public String getClear_to() {
        return clear_to;
    }

    public void setClear_to(String clear_to) {
        this.clear_to = clear_to;
    }

    public String getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(String pur_cd) {
        this.pur_cd = pur_cd;
    }

    public String getPur_cd_descr() {
        return pur_cd_descr;
    }

    public void setPur_cd_descr(String pur_cd_descr) {
        this.pur_cd_descr = pur_cd_descr;
    }

    public int getTccCount() {
        return tccCount;
    }

    public void setTccCount(int tccCount) {
        this.tccCount = tccCount;
    }

    public ArrayList<TaxClearanceCertificatePrintReportSubDobj> getTaxPurposeList() {
        return taxPurposeList;
    }

    public void setTaxPurposeList(ArrayList<TaxClearanceCertificatePrintReportSubDobj> taxPurposeList) {
        this.taxPurposeList = taxPurposeList;
    }

    /**
     * @return the vtTaxDetailsList
     */
    public ArrayList<TaxClearanceCertificatePrintReportSubDobj> getVtTaxDetailsList() {
        return vtTaxDetailsList;
    }

    /**
     * @param vtTaxDetailsList the vtTaxDetailsList to set
     */
    public void setVtTaxDetailsList(ArrayList<TaxClearanceCertificatePrintReportSubDobj> vtTaxDetailsList) {
        this.vtTaxDetailsList = vtTaxDetailsList;
    }

    /**
     * @return the diffofTaxxDetailsList
     */
    public ArrayList<TaxClearanceCertificatePrintReportSubDobj> getDiffofTaxxDetailsList() {
        return diffofTaxxDetailsList;
    }

    /**
     * @param diffofTaxxDetailsList the diffofTaxxDetailsList to set
     */
    public void setDiffofTaxxDetailsList(ArrayList<TaxClearanceCertificatePrintReportSubDobj> diffofTaxxDetailsList) {
        this.diffofTaxxDetailsList = diffofTaxxDetailsList;
    }

    /**
     * @return the qrText
     */
    public String getQrText() {
        return qrText;
    }

    /**
     * @param qrText the qrText to set
     */
    public void setQrText(String qrText) {
        this.qrText = qrText;
    }

    public String getTcc_no() {
        return tcc_no;
    }

    public void setTcc_no(String tcc_no) {
        this.tcc_no = tcc_no;
    }

    public String getTrc_no() {
        return trc_no;
    }

    public void setTrc_no(String trc_no) {
        this.trc_no = trc_no;
    }

    public String getRemark_vt_tax_clr() {
        return remark_vt_tax_clr;
    }

    public void setRemark_vt_tax_clr(String remark_vt_tax_clr) {
        this.remark_vt_tax_clr = remark_vt_tax_clr;
    }

    public String getRemark_vt_tax_exem() {
        return remark_vt_tax_exem;
    }

    public void setRemark_vt_tax_exem(String remark_vt_tax_exem) {
        this.remark_vt_tax_exem = remark_vt_tax_exem;
    }

    public String getExem_fr() {
        return exem_fr;
    }

    public void setExem_fr(String exem_fr) {
        this.exem_fr = exem_fr;
    }

    public String getExem_to() {
        return exem_to;
    }

    public void setExem_to(String exem_to) {
        this.exem_to = exem_to;
    }

    /**
     * @return the image_background
     */
    public String getImage_background() {
        return image_background;
    }

    /**
     * @param image_background the image_background to set
     */
    public void setImage_background(String image_background) {
        this.image_background = image_background;
    }

    /**
     * @return the image_logo
     */
    public String getImage_logo() {
        return image_logo;
    }

    /**
     * @param image_logo the image_logo to set
     */
    public void setImage_logo(String image_logo) {
        this.image_logo = image_logo;
    }

    /**
     * @return the show_image_background
     */
    public boolean isShow_image_background() {
        return show_image_background;
    }

    /**
     * @param show_image_background the show_image_background to set
     */
    public void setShow_image_background(boolean show_image_background) {
        this.show_image_background = show_image_background;
    }

    /**
     * @return the show_image_logo
     */
    public boolean isShow_image_logo() {
        return show_image_logo;
    }

    /**
     * @param show_image_logo the show_image_logo to set
     */
    public void setShow_image_logo(boolean show_image_logo) {
        this.show_image_logo = show_image_logo;
    }

    /**
     * @return the showRoadSafetySlogan
     */
    public boolean isShowRoadSafetySlogan() {
        return showRoadSafetySlogan;
    }

    /**
     * @param showRoadSafetySlogan the showRoadSafetySlogan to set
     */
    public void setShowRoadSafetySlogan(boolean showRoadSafetySlogan) {
        this.showRoadSafetySlogan = showRoadSafetySlogan;
    }

    /**
     * @return the roadSafetySloganDobj
     */
    public VmRoadSafetySloganPrintDobj getRoadSafetySloganDobj() {
        return roadSafetySloganDobj;
    }

    /**
     * @param roadSafetySloganDobj the roadSafetySloganDobj to set
     */
    public void setRoadSafetySloganDobj(VmRoadSafetySloganPrintDobj roadSafetySloganDobj) {
        this.roadSafetySloganDobj = roadSafetySloganDobj;
    }
}
