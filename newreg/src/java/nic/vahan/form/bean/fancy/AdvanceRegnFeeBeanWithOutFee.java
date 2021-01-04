/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.fancy;

import java.io.Serializable;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.swing.event.ChangeEvent;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.CommonUtils.VehicleParameters;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.FancyAuctionEntryBean;
import nic.vahan.form.dobj.configuration.TmConfigurationAdvanceRegnDobj;
import nic.vahan.form.dobj.fancy.AdvanceRegnNo_dobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.fancy.AdvanceRegnFeeImpl;
import nic.vahan.form.impl.fancy.AdvanceRegnFeeImplWithOutFee;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "advancefancywFee")
@ViewScoped
public class AdvanceRegnFeeBeanWithOutFee extends AdvanceRegnNo_dobj implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(FancyAuctionEntryBean.class);
    private ArrayList listC_village = null;
    private ArrayList listC_taluk = null;
    private ArrayList listC_dist = null;
    private String SELECT_LABEL1 = "Reservation of New Registration Number";
    private String SELECT_LABEL2 = "Reservation of Lapsed Registration Number";
    private String SELECT_LABEL3 = "Payment of Balance of Auction Money";
    private String SELECT_VALUE1 = "1";
    private String SELECT_VALUE2 = "2";
    private String SELECT_VALUE3 = "3";
    private String rb_select_one = SELECT_VALUE1;//default Value
    private boolean saved_success = false;
    private boolean rcpt_view = false;
    private boolean bal_rcpt_view = false;
    private boolean blnAlreadyReg = false;
    private boolean blnApplNo = false;
    private boolean blnOldReg = false;
    private boolean blnCheckAlreadyReg = false;
    private boolean blnchkAdvanceRegn = true;
    private TmConfigurationAdvanceRegnDobj tmConfigChangeAdminDobj;
    String rcpt = null;
    private String statename;
    private String rtooffice;
    private String receiptno;
    private String vhowner;
    private String regnno;
    private String vhclass;
    private String receiptdate;
    private String chasino;
    private String orderNo;
    String regnType = "0";
    private int pmtType;
    String vehClass = "0";
    private int oldRegnSize = 7;
    private String vahanMessages = null;

    @PostConstruct
    public void init() {
        try {

            statename = Util.getSession().getAttribute("state_cd").toString();
            tmConfigChangeAdminDobj = ServerUtil.getTmConfigurationChangeAdminParameters(statename);
            if (tmConfigChangeAdminDobj != null) {
                setOldRegnSize(tmConfigChangeAdminDobj.getSizeOldNumber());
            }
            listC_dist = new ArrayList();
            String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
            for (int i = 0; i < data.length; i++) {
                if (data[i][2].trim().equals(Util.getUserStateCode())) {
                    listC_dist.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
            boolean flag = false;   //AdvanceRegnFeeImplWithOutFee.getCheckFancyConfiguration(statename);
            if (flag == false && tmConfigChangeAdminDobj != null && tmConfigChangeAdminDobj.isOldNumberReplace()) {
                setBlnchkAdvanceRegn(Boolean.FALSE);
                blnCheckAlreadyReg = true;
                blnAlreadyReg = true;
                blnApplNo = true;
                blnOldReg = false;

            } else {
                setBlnchkAdvanceRegn(Boolean.TRUE);
                blnCheckAlreadyReg = true;
                blnAlreadyReg = true;
                blnApplNo = true;
                blnOldReg = false;
            }
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

    public void lsnCheckAlreadyRegistered(AjaxBehaviorEvent actionEvent) {
        reset();

        if (blnCheckAlreadyReg) {
            blnApplNo = true;
            blnOldReg = false;
        } else {
            blnApplNo = false;
            blnOldReg = true;
        }
    }

    public void regnoFocusLost(AjaxBehaviorEvent actionEvent) throws VahanException {
        boolean flg = false;
        Exception e = null;
        try {
            String regnNo = getRegn_no();
            if ((getRegn_no() == null) || getRegn_no().equals("")) {
                throw new VahanException("Please Enter Registration  Number");

            } else if (getRegn_no().length() < 8) {
                throw new VahanException("Please Enter Proper Registration Number");
            }
            String seriesPart = regnNo.substring(0, regnNo.length() - 4);
            String numPart = regnNo.substring((regnNo.length() - 4), regnNo.length());

            if (tmConfigChangeAdminDobj != null && tmConfigChangeAdminDobj.isOldNumberReplace() && blnOldReg) {
                String oldNumPart = AdvanceRegnFeeBean.sanitizationNumberPart(Utility.getOnlyNumberPart(getOld_regn_no()));
                if (!numPart.equalsIgnoreCase(oldNumPart)) {
                    throw new VahanException("Please Enter the same Registration Number Part :- '" + oldNumPart + "' as Old Series. Different Number Part not allowed.");
                }
            }
            setRegn_no(AdvanceRegnFeeBean.sanitizationRegnNo(seriesPart, numPart));
            String off_cd = Util.getSession().getAttribute("selected_off_cd").toString().trim();
            String state_cd = Util.getSession().getAttribute("state_cd").toString();
            int offcd = Integer.parseInt(off_cd);
            if (ServerUtil.isTransport(Integer.parseInt(vehClass), null)) {
                regnType = "1";
            } else {
                regnType = "2";
            }
            boolean flag = AdvanceRegnFeeImpl.verifyFancyRegnNo(getRegn_no(), state_cd, offcd, regnType, vehClass, getRegn_appl_no(), pmtType);
            if (flag) {
                if (AdvanceRegnFeeImplWithOutFee.isNumberBooked(getRegn_no())) {
                    flg = true;
                }
            }
        } catch (NumberFormatException ex) {
            e = ex;
        } catch (VahanException ve) {
            e = ve;
        } catch (Exception ee) {
            e = ee;
        }

        if (e != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage(), e.getMessage()));
            resetRegnAmount();
            return;
        }
    }

    public void applnoFocusLost(AjaxBehaviorEvent actionEvent) {

        Exception e = null;
        try {
            String state_cd = Util.getSession().getAttribute("state_cd").toString();
            int offcd = Integer.parseInt(Util.getSession().getAttribute("selected_off_cd").toString().trim());


            if (blnApplNo) {
                if ((getRegn_appl_no() == null) || getRegn_appl_no().equals("")) {
                    throw new VahanException("Please Enter Application Number");
                } else {
                    String error = AdvanceRegnFeeImplWithOutFee.checkApplNo(getRegn_appl_no());
                    if (!error.equalsIgnoreCase("")) {
                        throw new VahanException(error);
                    }
                }
            } else {
                if (tmConfigChangeAdminDobj != null && tmConfigChangeAdminDobj.isOldNumberReplace()) {
                    if ((getOld_regn_no() == null) || getOld_regn_no().equals("")) {
                        throw new VahanException("Please Enter Old Registration Number");
                    } else {
                        String error = AdvanceRegnFeeImplWithOutFee.checkRegnNoAlreadyBook(getOld_regn_no());
                        if (!error.equalsIgnoreCase("")) {
                            throw new VahanException(error);
                        }
                    }
                } else {
                    throw new VahanException("Old Number Replace facility not available for your State");
                }

            }

            AdvanceRegnNo_dobj dobj = new AdvanceRegnNo_dobj();
            VehicleParameters vehDobj = new VehicleParameters();
            if (blnApplNo) {
                dobj = AdvanceRegnFeeImpl.get_fancy_appl_no(getRegn_appl_no(), state_cd, offcd);
            } else {
                dobj = AdvanceRegnFeeImpl.get_old_regn_no(getOld_regn_no(), state_cd, offcd);
            }
            if (dobj != null) {
                setOwner_name(dobj.getOwner_name());
                setF_name(dobj.getF_name());
                setC_add1(dobj.getC_add1());
                setC_add2(dobj.getC_add2());
                setC_district(dobj.getC_district());
                setC_pincode(dobj.getC_pincode());
                regnType = dobj.getRegnType();
                vehClass = dobj.getVehClass();
                setChasisNo(dobj.getChasisNo());
                vehDobj.setOWNER_CD(dobj.getOwnerCd());
                vehDobj.setVH_CLASS(Integer.parseInt(vehClass));
                vehDobj.setOFF_CD(Util.getSelectedSeat().getOff_cd());
                setMobile_no(dobj.getMobile_no());
                if ((getOld_regn_no() == null) || getOld_regn_no().equals("")) {
                    setOld_regn_no(null);
                } else {
                    setOld_regn_no(getOld_regn_no());
                }
            }
            if (ServerUtil.isTransport(Integer.parseInt(vehClass), null)) {
                regnType = "1";
            } else {
                regnType = "2";
            }
            vehDobj.setREGN_TYPE(regnType);
            int flag = AdvanceRegnFeeImplWithOutFee.getCheckFancyConfiguration(state_cd, vehDobj);
            if (flag == 1 || blnOldReg) {
                reservListener(regnType, vehClass);
            } else if (flag == 2 || flag == 0) {
                throw new VahanException("Advance Registration No is not Allowed without fee");
            } else if (flag == 3) {
                throw new VahanException("This Application/Registration No not Applicable to book advance Registration No without fee.");
            }
        } catch (VahanException ve) {
            e = ve;
        } catch (Exception ee) {
            e = ee;
        }

        if (e != null) {
            reset();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage(), e.getMessage()));
        }
    }

    public void saveFancyNo() {
        Exception e = null;
        boolean blnOldRegistration = true;

        try {

            String errMsg = validate();
            if (errMsg != null && !"".equalsIgnoreCase(errMsg)) {
                throw new VahanException(errMsg);
            } else {
                if (getRegn_appl_no() != null && !getRegn_appl_no().equalsIgnoreCase("")) {
                    String checkApplErr = AdvanceRegnFeeImplWithOutFee.checkApplNo(getRegn_appl_no());
                    blnOldRegistration = false;
                    if (!checkApplErr.equalsIgnoreCase("")) {
                        throw new VahanException(checkApplErr);
                    }
                }
                String state_cd = Util.getSession().getAttribute("state_cd").toString();
                int offcd = Integer.parseInt(Util.getSession().getAttribute("selected_off_cd").toString().trim());

                rcpt = AdvanceRegnFeeImplWithOutFee.saveFancyDetail(this, state_cd, offcd, blnOldRegistration);

                if (rcpt.equalsIgnoreCase("")) {
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("There is some problem", rcpt);
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Note Down this Application Number " + rcpt + " for further reference", rcpt));
                    reset();
                }
            }
        } catch (VahanException ve) {
            e = ve;
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            e = ee;
        }

        if (e != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage(), e.getMessage()));
        }

    }

    public String validate() throws VahanException {
        String errorMessage = null;

        if (getRegn_no().equalsIgnoreCase("")) {
            errorMessage = "Please Enter Registration Number";
        } else if (getOwner_name().equalsIgnoreCase("")) {
            errorMessage = "Please Enter Owner Name";
        } else if (getF_name().equalsIgnoreCase("")) {
            errorMessage = "Please Enter Father Name";
        } else if (getC_add1().equalsIgnoreCase("")) {
            errorMessage = "Please Enter Address Properly";
        } else if (getMobile_no() == 0L) {
            errorMessage = "Please Enter Mobile Number Properly";
        } else if (getOrderNumber().equals("")) {
            errorMessage = "Please Enter Order Number";
        }
        return errorMessage;

    }

    public void validateField(String fldName, String fieldValue, StringBuffer bf) {
        if (fieldValue == null || fieldValue.trim().equals("")) {
            bf.append("Empty " + fldName);
            bf.append("\n");
        }

    }

    public void reset() {

        //setRegn_no("");
        setRegn_appl_no("");
        setOwner_name("");
        setF_name("");
        setMobile_no(0L);
        setC_pincode(0);
        setC_add1("");
        setC_add2("");
        setRecp_no("");
        setOrderNumber("");
        setOld_regn_no("");


    }

    public void resetRegnAmount() {
        setRegn_no("");
        setTotal_amt(0);
    }

    public String reloadPage() {
        return null;
    }

    public void reservListener(String regnType, String vehClass) {

        setAvailReservedNumbers(AdvanceRegnFeeImplWithOutFee.getFancyNumbers(Util.getUserStateCode(), regnType, vehClass));
    }

    private void rb_selectListener(ChangeEvent evt) {
    }

    public void reservedNocListener() {
    }

    public boolean isBal_rcpt_view() {
        return bal_rcpt_view;
    }

    public void setBal_rcpt_view(boolean bal_rcpt_view) {
        this.bal_rcpt_view = bal_rcpt_view;
    }

    public boolean isSaved_success() {
        return saved_success;
    }

    public void setSaved_success(boolean saved_success) {
        this.saved_success = saved_success;
    }

    public void setSuucessMessageFalse() {
        this.saved_success = false;
    }

    public boolean isRcpt_view() {
        return rcpt_view;
    }

    public void setRcpt_view(boolean rcpt_view) {
        this.rcpt_view = rcpt_view;
    }

    public String getRb_select_one() {
        return rb_select_one;
    }

    public void setRb_select_one(String rb_select_one) {
        this.rb_select_one = rb_select_one;
    }

    public ArrayList getListC_village() {
        return listC_village;
    }

    public void setListC_village(ArrayList listC_village) {
        this.listC_village = listC_village;
    }

    public ArrayList getListC_taluk() {
        return listC_taluk;
    }

    public void setListC_taluk(ArrayList listC_taluk) {
        this.listC_taluk = listC_taluk;
    }

    public ArrayList getListC_dist() {
        return listC_dist;
    }

    public void setListC_dist(ArrayList listC_dist) {
        this.listC_dist = listC_dist;
    }

    /**
     * @return the SELECT_LABEL1
     */
    public String getSELECT_LABEL1() {
        return SELECT_LABEL1;
    }

    /**
     * @param SELECT_LABEL1 the SELECT_LABEL1 to set
     */
    public void setSELECT_LABEL1(String SELECT_LABEL1) {
        this.SELECT_LABEL1 = SELECT_LABEL1;
    }

    /**
     * @return the SELECT_LABEL2
     */
    public String getSELECT_LABEL2() {
        return SELECT_LABEL2;
    }

    /**
     * @param SELECT_LABEL2 the SELECT_LABEL2 to set
     */
    public void setSELECT_LABEL2(String SELECT_LABEL2) {
        this.SELECT_LABEL2 = SELECT_LABEL2;
    }

    /**
     * @return the SELECT_LABEL3
     */
    public String getSELECT_LABEL3() {
        return SELECT_LABEL3;
    }

    /**
     * @param SELECT_LABEL3 the SELECT_LABEL3 to set
     */
    public void setSELECT_LABEL3(String SELECT_LABEL3) {
        this.SELECT_LABEL3 = SELECT_LABEL3;
    }

    /**
     * @return the SELECT_VALUE1
     */
    public String getSELECT_VALUE1() {
        return SELECT_VALUE1;
    }

    /**
     * @param SELECT_VALUE1 the SELECT_VALUE1 to set
     */
    public void setSELECT_VALUE1(String SELECT_VALUE1) {
        this.SELECT_VALUE1 = SELECT_VALUE1;
    }

    /**
     * @return the SELECT_VALUE2
     */
    public String getSELECT_VALUE2() {
        return SELECT_VALUE2;
    }

    /**
     * @param SELECT_VALUE2 the SELECT_VALUE2 to set
     */
    public void setSELECT_VALUE2(String SELECT_VALUE2) {
        this.SELECT_VALUE2 = SELECT_VALUE2;
    }

    /**
     * @return the SELECT_VALUE3
     */
    public String getSELECT_VALUE3() {
        return SELECT_VALUE3;
    }

    /**
     * @param SELECT_VALUE3 the SELECT_VALUE3 to set
     */
    public void setSELECT_VALUE3(String SELECT_VALUE3) {
        this.SELECT_VALUE3 = SELECT_VALUE3;
    }

//    public ReceiptMasterBean getRcpt_bean() {
//        return rcpt_bean;
//    }
//
//    public void setRcpt_bean(ReceiptMasterBean rcpt_bean) {
//        this.rcpt_bean = rcpt_bean;
//    }
    /**
     * @return the blnAlreadyReg
     */
    public boolean isBlnAlreadyReg() {
        return blnAlreadyReg;
    }

    /**
     * @param blnAlreadyReg the blnAlreadyReg to set
     */
    public void setBlnAlreadyReg(boolean blnAlreadyReg) {
        this.blnAlreadyReg = blnAlreadyReg;
    }

    /**
     * @return the blnCheckAlreadyReg
     */
    public boolean isBlnCheckAlreadyReg() {
        return blnCheckAlreadyReg;
    }

    /**
     * @param blnCheckAlreadyReg the blnCheckAlreadyReg to set
     */
    public void setBlnCheckAlreadyReg(boolean blnCheckAlreadyReg) {
        this.blnCheckAlreadyReg = blnCheckAlreadyReg;
    }

    public String getStatename() {
        return statename;
    }

    /**
     * @param statename the statename to set
     */
    public void setStatename(String statename) {
        this.statename = statename;
    }

    /**
     * @return the rtooffice
     */
    public String getRtooffice() {
        return rtooffice;
    }

    /**
     * @param rtooffice the rtooffice to set
     */
    public void setRtooffice(String rtooffice) {
        this.rtooffice = rtooffice;
    }

    /**
     * @return the receiptno
     */
    public String getReceiptno() {
        return receiptno;
    }

    /**
     * @param receiptno the receiptno to set
     */
    public void setReceiptno(String receiptno) {
        this.receiptno = receiptno;
    }

    /**
     * @return the vhowner
     */
    public String getVhowner() {
        return vhowner;
    }

    /**
     * @param vhowner the vhowner to set
     */
    public void setVhowner(String vhowner) {
        this.vhowner = vhowner;
    }

    /**
     * @return the regnno
     */
    public String getRegnno() {
        return regnno;
    }

    /**
     * @param regnno the regnno to set
     */
    public void setRegnno(String regnno) {
        this.regnno = regnno;
    }

    /**
     * @return the vhclass
     */
    public String getVhclass() {
        return vhclass;
    }

    /**
     * @param vhclass the vhclass to set
     */
    public void setVhclass(String vhclass) {
        this.vhclass = vhclass;
    }

    /**
     * @return the receiptdate
     */
    public String getReceiptdate() {
        return receiptdate;
    }

    /**
     * @param receiptdate the receiptdate to set
     */
    public void setReceiptdate(String receiptdate) {
        this.receiptdate = receiptdate;
    }

    /**
     * @return the chasino
     */
    public String getChasino() {
        return chasino;
    }

    /**
     * @param chasino the chasino to set
     */
    public void setChasino(String chasino) {
        this.chasino = chasino;
    }

    /**
     * @return the blnchkAdvanceRegn
     */
    public boolean isBlnchkAdvanceRegn() {
        return blnchkAdvanceRegn;
    }

    /**
     * @param blnchkAdvanceRegn the blnchkAdvanceRegn to set
     */
    public void setBlnchkAdvanceRegn(boolean blnchkAdvanceRegn) {
        this.blnchkAdvanceRegn = blnchkAdvanceRegn;
    }

    /**
     * @return the orderNo
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * @param orderNo the orderNo to set
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * @return the blnOldReg
     */
    public boolean isBlnOldReg() {
        return blnOldReg;
    }

    /**
     * @param blnOldReg the blnOldReg to set
     */
    public void setBlnOldReg(boolean blnOldReg) {
        this.blnOldReg = blnOldReg;
    }

    /**
     * @return the blnApplNo
     */
    public boolean isBlnApplNo() {
        return blnApplNo;
    }

    /**
     * @param blnApplNo the blnApplNo to set
     */
    public void setBlnApplNo(boolean blnApplNo) {
        this.blnApplNo = blnApplNo;
    }

    /**
     * @return the oldRegnSize
     */
    public int getOldRegnSize() {
        return oldRegnSize;
    }

    /**
     * @param oldRegnSize the oldRegnSize to set
     */
    public void setOldRegnSize(int oldRegnSize) {
        this.oldRegnSize = oldRegnSize;
    }

    /**
     * @return the vahanMessages
     */
    public String getVahanMessages() {
        return vahanMessages;
    }

    /**
     * @param vahanMessages the vahanMessages to set
     */
    public void setVahanMessages(String vahanMessages) {
        this.vahanMessages = vahanMessages;
    }
}
