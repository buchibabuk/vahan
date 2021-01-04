/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import app.eoffice.dsc.service.DscService;
import app.eoffice.dsc.util.XmlUtil;
import app.eoffice.dsc.xml.common.Revocation;
import app.eoffice.dsc.xml.response.CertListResponse;
import app.eoffice.dsc.xml.response.ChainCertificates;
import app.eoffice.dsc.xml.response.RegistrationResponse;
import java.io.Serializable;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.DscDobj;
import nic.vahan.form.impl.DscRegistrationImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import nic.java.util.DateUtils;

/**
 *
 * @author komal
 */
@ManagedBean(name = "dscRegisBean")
@ViewScoped
public class DscRegistrationBean implements Serializable {

    private String certificatexml;
    private List<DscDobj> dscServiceCertificateList = null;
    private List<DscDobj> dscConnectedList = null;
    private String registrationxml;
    private String registrationResponseXml;
    private static Logger LOGGER = Logger.getLogger(DscRegistrationBean.class);
    private List<DscDobj> dealerVerifiedList;
    private List<DscDobj> dealerUnVerifiedList;
    private List<DscDobj> filteredList;
    private SessionVariables sessionVariables = null;
    private int actionCd;
    private boolean noteMessage;
    private String userCatg;

    public DscRegistrationBean() {
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null
                    || sessionVariables.getStateCodeSelected() == null
                    || sessionVariables.getOffCodeSelected() == 0
                    || sessionVariables.getUserCatgForLoggedInUser() == null
                    || sessionVariables.getActionCodeSelected() == 0
                    || sessionVariables.getSelectedWork() == null) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            } else {
                userCatg = sessionVariables.getUserCatgForLoggedInUser();
                actionCd = sessionVariables.getActionCodeSelected();
            }
            if (actionCd == TableConstants.TM_ROLE_DEALER_ADMIN_DSC_REGISTRATION || actionCd == TableConstants.TM_ROLE_OFF_ADMIN_DSC_REGISTRATION) {
                if (userCatg != null && userCatg.equals(TableConstants.USER_CATG_DEALER_ADMIN)) {
                    noteMessage = true;
                }
                this.registeredDscList();
            } else if (actionCd == TableConstants.TM_ROLE_OFF_ADMIN_DSC_VERIFICATION) {
                this.registeredDscListForVerification();
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert !!!", "Something Went Wrong!!!", FacesMessage.SEVERITY_WARN);
        }
    }

    public void verifyDlrDetails() {
        try {
            int countChecked = 0;
            int successVerfied = 0;
            for (int i = 0; i < getDealerUnVerifiedList().size(); i++) {
                if (getDealerUnVerifiedList().get(i).isVerifyStatus()) {
                    countChecked++;
                    break;
                }
            }
            if (countChecked > 0) {
                DscRegistrationImpl regisImpl = new DscRegistrationImpl();
                successVerfied = regisImpl.verifyDealer(getDealerUnVerifiedList());
                this.registeredDscListForVerification();
            } else {
                throw new VahanException("Please Select atleast one Dealer DSC to Verify");
            }

            if (successVerfied > 0) {
                JSFUtils.showMessagesInDialog("Info !!!", "Selected Dealer DSC are Successfully Verified!!!", FacesMessage.SEVERITY_INFO);
            }
        } catch (VahanException ves) {
            JSFUtils.showMessagesInDialog("Alert !!!", ves.getMessage(), FacesMessage.SEVERITY_WARN);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert !!!", TableConstants.SomthingWentWrong, FacesMessage.SEVERITY_WARN);

        }
    }

    public void connectToDsc() {
        DscService dscserv = new DscService();
        try {        
            if (getCertificatexml() != null && getCertificatexml().length() > 0) {
                String decodedResp = new String(Base64.decodeBase64(getCertificatexml().getBytes()));
                CertListResponse obj1 = dscserv.parseDscList(decodedResp);

                if (Util.getTmConfiguration() != null && Util.getTmConfiguration().getTmConfigDmsDobj() != null) {
                    if (!Util.getTmConfiguration().getTmConfigDmsDobj().getDigitalSignAllowOffWise().contains("," + Util.getUserLoginOffCode() + ",")) {
                        throw new VahanException("Login Office Code is not enable for DSC Registration");
                    }
                }

                dscServiceCertificateList = new ArrayList<>();

                for (CertListResponse.CertificateDetails str : obj1.getCertificates()) {
                    DscDobj dobj = new DscDobj();

//                LOGGER.info(str.getSerialNumber() + ":   " + str.getNotAfter());
                    String usernm = str.getIssuedTo().substring(str.getIssuedTo().indexOf("CN=") + 3).substring(0, str.getIssuedTo().substring(str.getIssuedTo().indexOf("CN=") + 3).indexOf(","));

                    DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
                    Date date = (Date) formatter.parse(str.getNotAfter());

                    String formatedDate = this.getFormattedDate(date);
                    if (str.getIssuedBy().indexOf("O=") != -1) {
                        String vendorName = str.getIssuedBy().substring(str.getIssuedBy().indexOf("O=")).substring(0, str.getIssuedBy().substring(str.getIssuedBy().indexOf("O=")).indexOf(","));
                        dobj.setVendorName(vendorName.split("=")[1]);
                    }
                    dobj.setRevocationSt(str.getRevocation());
                    dobj.setCertValidUpto(formatedDate);
                    dobj.setUserName(usernm);
                    dobj.setSerialNo(str.getSerialNumber());
                    getDscServiceCertificateList().add(dobj);
                }
            } else {
                throw new VahanException("Problem while connecting to DSC");
            }
        } catch (VahanException ve) {
            JSFUtils.showMessagesInDialog("Alert !!!", ve.getMessage(), FacesMessage.SEVERITY_WARN);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert !!!", "Problem while connecting to DSC", FacesMessage.SEVERITY_WARN);
        }

    }

    public void revocStr(DscDobj dob) {
        DscService dscserv = new DscService();
        DscRegistrationImpl regisImpl = new DscRegistrationImpl();
        try {
            // if (dob.getRevocationSt() != null && dob.getRevocationSt().getRevReq() != null && !dob.getRevocationSt().getRevReq().isEmpty() && dob.getRevocationSt().getRevReq().size() > 3) {           
            Date dscValidity = new SimpleDateFormat("yyyy-MM-dd").parse(dob.getCertValidUpto());
            if (DateUtils.compareDates(new Date(), dscValidity) == 2) {
                throw new VahanException("DSC with Serial No " + dob.getSerialNo() + " Validity has been expired!!!");
            }
            String crlURL = ServerUtil.getVahanPgiUrl(TableConstants.CRL_SERVICE_URL);
            String revocXmlString = XmlUtil.marshallOjectToXml(Revocation.class, dob.getRevocationSt());
//            LOGGER.info("=====xmlString======= " + revocXmlString);
            String revokationStatusXML = dscserv.isRevoked(revocXmlString, crlURL, "");// Just Skipped till CRL Implementation
//            LOGGER.info("revokationStatusXML = " + revokationStatusXML);
            String registrationrequestxml = dscserv.createRegistrationRequest(dob.getSerialNo(), revokationStatusXML);
//            LOGGER.info("======registrationrequestxml ==== : " + registrationrequestxml);
            setRegistrationxml(registrationrequestxml);
            dob.setRevocstr(revocXmlString);
            PrimeFaces.current().ajax().update("formDscAuthenticate:registXMl");
            PrimeFaces.current().executeScript("registerCert();");
//           }else {
//                throw new VahanException("Chain Certificates not found !!!");
//            }
        } catch (VahanException ves) {
            JSFUtils.showMessagesInDialog("Alert !!!", ves.getMessage(), FacesMessage.SEVERITY_WARN);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert !!!", "Problem while connecting to DSC", FacesMessage.SEVERITY_WARN);
        }
    }

    public void dscRegistration() {
        DscRegistrationImpl registerImpl = new DscRegistrationImpl();
        DscDobj dscDobj = new DscDobj();
        DateFormat df2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            String decodedResp = new String(Base64.decodeBase64(getRegistrationResponseXml().getBytes()));
//            LOGGER.info("registrationResponseXml : " + decodedResp);
            RegistrationResponse registerationResObj = (RegistrationResponse) XmlUtil.unmarshalXmlToObject(RegistrationResponse.class, decodedResp);
            if (registerationResObj != null) {
//                LOGGER.info("status" + registerationResObj.getStatus());
//                LOGGER.info("serial No" + registerationResObj.getSerialNumber());
//                LOGGER.info("msg" + registerationResObj.getMsg());
                if (registerationResObj.getStatus() != null && registerationResObj.getStatus().equals("0")) {
                    throw new VahanException(registerationResObj.getMsg());
                }
                dscDobj.setSerialNo(registerationResObj.getSerialNumber());
                dscDobj.setCertlevel(registerationResObj.getCertType());
                dscDobj.setCdpPoint(registerationResObj.getCdpPoint());
                dscDobj.setActiveStatus(registerationResObj.getStatus());
                String usernm = registerationResObj.getIssuedTo().substring(registerationResObj.getIssuedTo().indexOf("CN=") + 3).substring(0, registerationResObj.getIssuedTo().substring(registerationResObj.getIssuedTo().indexOf("CN=") + 3).indexOf(","));
                dscDobj.setUserName(usernm);
                Date validDate = df2.parse(registerationResObj.getNotAfter());
                dscDobj.setCertValidDate(validDate);

                ChainCertificates certificates = registerationResObj.getChainCerts();
                if (certificates != null) {
                    for (ChainCertificates.CertificateDetail chainCert : certificates.getChainCerts()) {
                        if (chainCert.getCertLevel() != null && chainCert.getCertLevel().equalsIgnoreCase("0")) {
                            dscDobj.setCertContent(chainCert.getCertContent());
                            break;
                        }
                    }
                    dscDobj.setChainCertificates(certificates.getChainCerts());
                }

                String digitalSignToPrint = this.digitalSignToPrint(registerationResObj.getIssuedTo(), registerationResObj.getAliasName());
                dscDobj.setDigitalSignToPrint(digitalSignToPrint);

                if (registerationResObj.getStatus() != null && registerationResObj.getStatus().equals("1")) {

                    if (registerationResObj.getIssuedBy().indexOf("O=") != -1) {
                        String vendorName = registerationResObj.getIssuedBy().substring(registerationResObj.getIssuedBy().indexOf("O=")).substring(0, registerationResObj.getIssuedBy().substring(registerationResObj.getIssuedBy().indexOf("O=")).indexOf(","));
                        dscDobj.setVendorName(vendorName.split("=")[1]);
                    }

                    boolean isRegistrationDone = registerImpl.dscRegistration(dscDobj);
                    if (isRegistrationDone) {
                        this.registeredDscList();
                        JSFUtils.showMessagesInDialog("Info !!!", "DSC Registration Done Successfully!!!", FacesMessage.SEVERITY_INFO);
                    } else {
                        throw new VahanException("Problem while registration of DSC");
                    }
                } else {
                    throw new VahanException("Problem while registration of DSC : In Active Status");
                }
            } else {
                throw new VahanException("Problem in getting response for registration of DSC");
            }
        } catch (VahanException ex) {
            JSFUtils.showMessagesInDialog("Alert !!!", ex.getMessage(), FacesMessage.SEVERITY_WARN);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            JSFUtils.showMessagesInDialog("Alert !!!", "Problem while registration of DSC", FacesMessage.SEVERITY_WARN);
        }
    }

    public String digitalSignToPrint(String issuedTo, String userName) throws VahanException {
        String digitalSign = "";
        if (!CommonUtils.isNullOrBlank(issuedTo)) {
            digitalSign = userName + "\n" + "DN: ";
            if (issuedTo.indexOf("C=") != -1) {
                digitalSign = digitalSign + issuedTo.substring(issuedTo.indexOf("C=")) + ", ";
            }
            if (issuedTo.indexOf("O=") != -1) {
                digitalSign = digitalSign + issuedTo.substring(issuedTo.indexOf("O=")).substring(0, issuedTo.substring(issuedTo.indexOf("O=")).indexOf(",")) + ", " + "\n";
            }
            if (issuedTo.indexOf("OID.2.5.4.20=") != -1) {
                digitalSign = digitalSign + (issuedTo.substring(issuedTo.indexOf("OID.2.5.4.20=")).substring(0, issuedTo.substring(issuedTo.indexOf("OID.2.5.4.20=")).indexOf(","))).substring(4) + ", ";
            }
            if (issuedTo.indexOf("OU=") != -1) {
                digitalSign = digitalSign + issuedTo.substring(issuedTo.indexOf("OU=")).substring(0, issuedTo.substring(issuedTo.indexOf("OU=")).indexOf(",")) + ", ";
            }
            if (issuedTo.indexOf("OID.2.5.4.17=") != -1) {
                String val = issuedTo.substring(issuedTo.indexOf("OID.2.5.4.17=")).substring(0, issuedTo.substring(issuedTo.indexOf("OID.2.5.4.17=")).indexOf(","));
                String newVal = val.replace("OID.2.5.4.17", "postalCode");
                digitalSign = digitalSign + newVal + ", " + "\n";
            }
            if (issuedTo.indexOf("ST=") != -1) {
                digitalSign = digitalSign + issuedTo.substring(issuedTo.indexOf("ST=")).substring(0, issuedTo.substring(issuedTo.indexOf("ST=")).indexOf(",")) + ", " + "\n";
            }
            if (issuedTo.indexOf("SERIALNUMBER=") != -1) {
                digitalSign = digitalSign + issuedTo.substring(issuedTo.indexOf("SERIALNUMBER=")).substring(0, issuedTo.substring(issuedTo.indexOf("SERIALNUMBER=")).indexOf(",")) + ", ";
            }
            if (issuedTo.indexOf("CN=") != -1) {
                digitalSign = digitalSign + issuedTo.substring(issuedTo.indexOf("CN=")).substring(0, issuedTo.substring(issuedTo.indexOf("CN=")).indexOf(","));
            }
        } else {
            throw new VahanException("Problem in making digital sign");
        }
        return digitalSign;
    }

    private void registeredDscList() throws VahanException, Exception {
        DscRegistrationImpl regisImpl = new DscRegistrationImpl();
        dscConnectedList = new ArrayList<>();
        ArrayList<DscDobj> dscDobjList = regisImpl.fetchDscRegistrationDtls(null, null, 0, "dscRegistration");
        if (dscDobjList != null && !dscDobjList.isEmpty()) {
            for (DscDobj dobj : dscDobjList) {
                if (dobj.getCertlevel() != null && dobj.getCertlevel().equalsIgnoreCase("0")) {
                    String formatedDate = this.getFormattedDate(dobj.getCertValidDate());
                    dobj.setCertValidUpto(formatedDate);
                    dscConnectedList.add(dobj);
                }
            }
        }
    }

    private void registeredDscListForVerification() throws VahanException, Exception {
        DscRegistrationImpl regisImpl = new DscRegistrationImpl();
        ArrayList<DscDobj> dscDobjList = regisImpl.fetchDscRegistrationDtls(null, null, 0, "dscVerification");
        if (dscDobjList != null && !dscDobjList.isEmpty()) {
            dealerVerifiedList = new ArrayList<>();
            dealerUnVerifiedList = new ArrayList<>();
            for (DscDobj dobj : dscDobjList) {
                if (dobj.getCertlevel() != null && dobj.getCertlevel().equalsIgnoreCase("0")) {
                    String formatedDate = this.getFormattedDate(dobj.getCertValidDate());
                    dobj.setCertValidUpto(formatedDate);
                    String hexaDecimalSerialNo = this.getHexaDeciamlFormat(dobj.getSerialNo());
                    dobj.setSerailNoInHexaDecimalForm(hexaDecimalSerialNo);
                    if (dobj.isVerifyStatus()) {
                        dealerVerifiedList.add(dobj);
                    } else {
                        dealerUnVerifiedList.add(dobj);
                    }
                }
            }
        }
    }

    public String getHexaDeciamlFormat(String serialNoInDecimalFormat) throws Exception {
        BigInteger c = new BigInteger(serialNoInDecimalFormat);
        String serialNoInHexaDecimalFormat = c.toString(16);
        return serialNoInHexaDecimalFormat;
    }

    public String getFormattedDate(Date date) throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String formatedDate = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE);
        return formatedDate;
    }

    /**
     * @return the certificatexml
     */
    public String getCertificatexml() {
        return certificatexml;
    }

    /**
     * @param certificatexml the certificatexml to set
     */
    public void setCertificatexml(String certificatexml) {
        this.certificatexml = certificatexml;
    }

    /**
     * @return the dscServiceCertificateList
     */
    public List<DscDobj> getDscServiceCertificateList() {
        return dscServiceCertificateList;
    }

    /**
     * @param dscServiceCertificateList the dscServiceCertificateList to set
     */
    public void setDscServiceCertificateList(List<DscDobj> dscServiceCertificateList) {
        this.dscServiceCertificateList = dscServiceCertificateList;
    }

    /**
     * @return the registrationxml
     */
    public String getRegistrationxml() {
        return registrationxml;
    }

    /**
     * @param registrationxml the registrationxml to set
     */
    public void setRegistrationxml(String registrationxml) {
        this.registrationxml = registrationxml;
    }

    /**
     * @return the registrationResponseXml
     */
    public String getRegistrationResponseXml() {
        return registrationResponseXml;
    }

    /**
     * @param registrationResponseXml the registrationResponseXml to set
     */
    public void setRegistrationResponseXml(String registrationResponseXml) {
        this.registrationResponseXml = registrationResponseXml;
    }

    /**
     * @return the dscConnectedList
     */
    public List<DscDobj> getDscConnectedList() {
        return dscConnectedList;
    }

    /**
     * @param dscConnectedList the dscConnectedList to set
     */
    public void setDscConnectedList(List<DscDobj> dscConnectedList) {
        this.dscConnectedList = dscConnectedList;
    }

    /**
     * @return the dealerVerifiedList
     */
    public List<DscDobj> getDealerVerifiedList() {
        return dealerVerifiedList;
    }

    /**
     * @param dealerVerifiedList the dealerVerifiedList to set
     */
    public void setDealerVerifiedList(List<DscDobj> dealerVerifiedList) {
        this.dealerVerifiedList = dealerVerifiedList;
    }

    /**
     * @return the dealerUnVerifiedList
     */
    public List<DscDobj> getDealerUnVerifiedList() {
        return dealerUnVerifiedList;
    }

    /**
     * @param dealerUnVerifiedList the dealerUnVerifiedList to set
     */
    public void setDealerUnVerifiedList(List<DscDobj> dealerUnVerifiedList) {
        this.dealerUnVerifiedList = dealerUnVerifiedList;
    }

    /**
     * @return the filteredList
     */
    public List<DscDobj> getFilteredList() {
        return filteredList;
    }

    /**
     * @param filteredList the filteredList to set
     */
    public void setFilteredList(List<DscDobj> filteredList) {
        this.filteredList = filteredList;
    }

    /**
     * @return the noteMessage
     */
    public boolean isNoteMessage() {
        return noteMessage;
    }

    /**
     * @param noteMessage the noteMessage to set
     */
    public void setNoteMessage(boolean noteMessage) {
        this.noteMessage = noteMessage;
    }

    /**
     * @return the userCatg
     */
    public String getUserCatg() {
        return userCatg;
    }

    /**
     * @param userCatg the userCatg to set
     */
    public void setUserCatg(String userCatg) {
        this.userCatg = userCatg;
    }
}
