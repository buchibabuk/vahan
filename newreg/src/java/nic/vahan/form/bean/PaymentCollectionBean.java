/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import nic.java.util.DateUtils;
import nic.java.util.DateUtilsException;
import nic.rto.vahan.common.CryptographyAES;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.PaymentCollectionDobj;
import nic.vahan.form.dobj.TmConfigurationPayVerifyDobj;
import nic.vahan.form.impl.FeeDraftimpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import nic.vahan.services.POSImpl;
import nic.vahan.services.PosDobj;
import org.apache.commons.httpclient.HttpStatus;
import org.primefaces.PrimeFaces;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC105
 */
@ManagedBean(name = "paymentCollection")
@ViewScoped
public class PaymentCollectionBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(PaymentCollectionBean.class);
    private List<PaymentCollectionDobj> paymentlist = new ArrayList<>();
    private String bank;
    private String payment_mode = "C";
    private String header_name;
    private boolean render_payment_table = false;
    private boolean render_button = false;
    private long balanceAmount;
    private long amountInCash;
    private long excessAmount;
    private long cash;
    private boolean isCashSelected = false;
    private List instrumentList = null;
    private PaymentCollectionDobj paydobj;
    private String popUpMessage = null;
    private boolean paymentCollectionPanelVisibility = true;
    private boolean renderColumn = true;
    private TmConfigurationPayVerifyDobj payVerifyDobj;

    public PaymentCollectionBean() {

        String[][] instrmentType = MasterTableFiller.masterTables.TM_INSTRUMENTS.getData();
        paydobj = new PaymentCollectionDobj();

        instrumentList = new ArrayList();
        instrumentList.add(new SelectItem("-1", "Select"));
        for (int i = 0; i < instrmentType.length; i++) {
            if ((instrmentType[i][2].split(",")).length > 1) {
                String[] statecdArray = instrmentType[i][2].split(",");
                for (String statecd : statecdArray) {
                    if (statecd.equals(Util.getUserStateCode())) {
                        instrumentList.add(new SelectItem(instrmentType[i][0], instrmentType[i][1]));
                    }
                }
            } else {
                if (instrmentType[i][2].equals(Util.getUserStateCode()) || instrmentType[i][2].equals("ALL")) {
                    instrumentList.add(new SelectItem(instrmentType[i][0], instrmentType[i][1]));
                }
            }
        }
        if (paymentlist.isEmpty()) {
            getPaymentlist().add(new PaymentCollectionDobj());
        }
        try {
            if (Util.getTmConfiguration() != null) {
                payVerifyDobj = Util.getTmConfiguration().getTmConfigPayVerifyDobj();
            }
        } catch (VahanException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }

//        if (Util.getUserStateCode().equalsIgnoreCase("DL")) {
//            renderColumn = false;
//        }
    }

    public void reset() {
        getPaymentlist().clear();
        getPaymentlist().add(new PaymentCollectionDobj());
        setRender_payment_table(false);
    }

    @PostConstruct
    public void postConstruct() {
    }

    public void calculateBalanceAmount(String totalAmount) {
        long totalBal = 0;

        for (PaymentCollectionDobj dobj : paymentlist) {

            totalBal = totalBal + Long.parseLong(dobj.getAmount());
        }
        setBalanceAmount(Long.parseLong(totalAmount) - totalBal);
        setAmountInCash(totalBal);
    }

    public void showTable() {
        getPaymentlist().clear();
        getPaymentlist().add(new PaymentCollectionDobj());
        setRender_button(true);
        String pay_mode = payment_mode;

        if (pay_mode.equals("C")) {
            header_name = "Cash Payment";
            setRender_payment_table(false);
            setIsCashSelected(true);
        } else if (pay_mode.equals("M")) {
            header_name = "Other than Cash";
            setRender_payment_table(true);
            setIsCashSelected(false);
        } else {
            LOGGER.info("Not Valid Payment Mode");
        }
    }

    public void validateInstrumentNumber(AjaxBehaviorEvent event) {

        PaymentCollectionDobj selectedDobj = (PaymentCollectionDobj) event.getComponent().getAttributes().get("paymentDobj");
        String error_message = null;

        if (selectedDobj.getInstrument() == null || selectedDobj.getInstrument().equals("-1")) {
            error_message = "Please Select Instrument Type";
        }

        if (selectedDobj.getBank_name() == null || selectedDobj.getBank_name().equals("-1")) {
            error_message = "Please Select Bank Name";
        }

        if (selectedDobj.getNumber() != null && !selectedDobj.getNumber().isEmpty() && selectedDobj.isRenderDraftInstmnt() && selectedDobj.getInstrument() != null && !(("D," + TableConstants.INSTRUMENT_CODE_CHEQUE + "").contains(selectedDobj.getInstrument()))) {
            //Only Numeric without 0 prefix required.           
            selectedDobj.setNumber(String.valueOf(Long.parseLong(selectedDobj.getNumber())));
        }

        if (selectedDobj.getInstrument() != null && selectedDobj.getInstrument().equals("D")) {
            if (selectedDobj.getNumber() != null && selectedDobj.getNumber().length() < 6) {
                int selectedNumber = Integer.parseInt(selectedDobj.getNumber());
                String paddedSelectedNumber = String.format("%06d", selectedNumber);
                selectedDobj.setNumber(paddedSelectedNumber);
            }
        }

        if (selectedDobj.getInstrument().equals(TableConstants.INSTRUMENT_CODE_BANK_RECEIPT) && getPayVerifyDobj().isBankReceiptVerify()) {
            String regex = "^[0-9A-Z]{5}-[0-9]{5}-[0-9]*$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(selectedDobj.getNumber());
            if (!matcher.find()) {
                error_message = "Invalid Instrument Number";
            }
        }

        String dupInstrument = FeeDraftimpl.chkDuplicateInstrumentNo(String.valueOf(selectedDobj.getNumber()),
                selectedDobj.getBank_name(), selectedDobj.getInstrument(), Util.getUserStateCode());

        if (dupInstrument != null) {
            error_message = dupInstrument;
        }

        int commonInstrumentsNo = 0;
        for (PaymentCollectionDobj dobj : getPaymentlist()) {
            if (dobj.getNumber() != null && selectedDobj.getNumber() != null && dobj.getNumber().equals(selectedDobj.getNumber())
                    && dobj.getBank_name() != null && selectedDobj.getNumber() != null && dobj.getBank_name().equals(dobj.getBank_name())
                    && dobj.getInstrument() != null && selectedDobj.getInstrument() != null && dobj.getInstrument().equals(selectedDobj.getInstrument())) {

                commonInstrumentsNo++;
            }
        }

        if (commonInstrumentsNo > 1) {
            error_message = "Duplicate Instrument Number";
        }

        if (error_message != null) {
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", error_message));
            selectedDobj.setNumber("");
        }


    }

    public void checkDate(ValueChangeEvent ve) {
        if (ve != null) {
            LOGGER.info("" + ve.getNewValue().toString());
        }
    }

    public List addPaymentData() {

        List<PaymentCollectionDobj> list = new ArrayList<>();
        PaymentCollectionDobj dobj = new PaymentCollectionDobj();
        Iterator itr = paymentlist.iterator();
        PaymentCollectionDobj d = null;
        while (itr.hasNext()) {
            d = (PaymentCollectionDobj) itr.next();
            dobj.setNumber(d.getNumber());
            dobj.setInstrument(d.getInstrument());
            dobj.setDated(d.getDated());
            dobj.setAmount(d.getAmount());
            dobj.setBank_name(d.getBank_name());
            dobj.setBranch(d.getBranch());
        }
        list.add(dobj);
        paymentlist.add(d);

        return list;

    }

    public void addRow(String number, String bankCode) {
        String mode = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("actionmode");
        if ("add".equalsIgnoreCase(mode)) {
            if (number == null || "-1".equals(number) || "".equals(number) || bankCode == null || "-1".equals(bankCode) || "".equals(bankCode)) {
                FacesMessage message = null;
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please Select Bank! And Valid Number", "Please Select Bank! And Valid Number");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                return;
            }
            // add row in the payment panel
            if (getPaymentlist().size() == 5) {
                LOGGER.info(" Maximum payment Selected");
            } else {
                getPaymentlist().add(new PaymentCollectionDobj());
            }

        } else if ("minus".equalsIgnoreCase(mode)) {
            PaymentCollectionDobj selectedNumber = new PaymentCollectionDobj(number, bankCode);
            int index = getPaymentlist().indexOf(selectedNumber);
            if (getPaymentlist().size() == 1) {//can't remove last element from row
                return;
            }
            getPaymentlist().remove(index);
        }
    }

    public void validateDraftDate(AjaxBehaviorEvent event) {
        PaymentCollectionDobj selectedDobj = (PaymentCollectionDobj) event.getComponent().getAttributes().get("paymentDobj");
        String error_message = null;
        if (selectedDobj.getInstrument() == null || selectedDobj.getInstrument().equals("-1")) {
            error_message = "Please Select Instrument Type";
        }

        int lastIndex = getPaymentlist().lastIndexOf(selectedDobj);
        getPaymentlist().remove(lastIndex);
        selectedDobj.setMax_draft_date(new Date());
        selectedDobj.setBank_name("-1");
        List bank_list = new ArrayList();
        String[][] data = MasterTableFiller.masterTables.TM_BANK.getData();
        bank_list.add(new SelectItem("-1", "Select Bank"));
        if (selectedDobj.getInstrument().equals(TableConstants.EGRASS_INSTRUMENT_CODE) && getPayVerifyDobj().iseGrassVerify()) {
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equals(String.valueOf(TableConstants.EGRASS_BANK_CODE))) {
                    bank_list.add(new SelectItem(data[i][0], data[i][1]));
                    break;
                }
            }
            selectedDobj.setBank_list(bank_list);
            selectedDobj.setDisableBranch(true);
            selectedDobj.setShowVerifyBtn(true);
            selectedDobj.setBranch("NA");
            selectedDobj.setAmount(null);
            selectedDobj.setNumber(null);
            // Manual Receipt
        } else if (selectedDobj.getInstrument().equals(TableConstants.INSTRUMENT_CODE_BANK_RECEIPT) && getPayVerifyDobj().isBankReceiptVerify()) {
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equals(String.valueOf(TableConstants.SBS_BANK_CODE))) {
                    bank_list.add(new SelectItem(data[i][0], data[i][1]));
                    break;
                }
            }
            selectedDobj.setBank_list(bank_list);
            selectedDobj.setDisableBranch(true);
            selectedDobj.setShowVerifyBtn(true);
            selectedDobj.setBranch("NA");
            selectedDobj.setAmount(null);
            selectedDobj.setNumber(null);
        } else if (selectedDobj.getInstrument().equals(TableConstants.MANUAL_RCPT_INSTRUMENT_CODE)) {
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equals(String.valueOf(TableConstants.ANY_OTHER_BANK_CODE))) {
                    bank_list.add(new SelectItem(data[i][0], data[i][1]));
                    break;
                }
            }
            selectedDobj.setBank_list(bank_list);
            selectedDobj.setDisableBranch(true);
            selectedDobj.setBranch("NA");
            selectedDobj.setAmount(null);
            selectedDobj.setNumber(null);
        } else {
            for (int i = 0; i < data.length; i++) {
                if (!data[i][0].equals(String.valueOf(TableConstants.EGRASS_BANK_CODE)) && !data[i][0].equals(String.valueOf(TableConstants.ANY_OTHER_BANK_CODE))) {
                    bank_list.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
            selectedDobj.setBank_list(bank_list);
            selectedDobj.setDisableBranch(false);
            selectedDobj.setShowVerifyBtn(false);
            selectedDobj.setBranch(null);
            selectedDobj.setAmount(null);
            selectedDobj.setNumber(null);
        }

        if (selectedDobj.getInstrument().equals("D")) {
            try {
                selectedDobj.setMin_draft_date(DateUtils.addToDate(selectedDobj.getMax_draft_date(), 2, -3));
            } catch (DateUtilsException ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        } else if (selectedDobj.getInstrument().equals("G") || selectedDobj.getInstrument().equals("M") || (selectedDobj.getInstrument().equals(TableConstants.INSTRUMENT_CODE_BANK_RECEIPT) && getPayVerifyDobj().isBankReceiptVerify())) {
            try {
                if ("RJ".equals(Util.getUserStateCode()) || "SK".equals(Util.getUserStateCode())) {
                    selectedDobj.setMin_draft_date(null);
                } else {
                    selectedDobj.setMin_draft_date(DateUtils.addToDate(selectedDobj.getMax_draft_date(), 2, -24));
                }
            } catch (DateUtilsException ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        } else if (selectedDobj.getInstrument().equals("L")) {
            try {
                selectedDobj.setMin_draft_date(DateUtils.addToDate(selectedDobj.getMax_draft_date(), 2, -24));
            } catch (DateUtilsException ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }


        if (selectedDobj.getInstrument() != null && (selectedDobj.getInstrument().equals(TableConstants.MANUAL_RCPT_INSTRUMENT_CODE)
                || selectedDobj.getInstrument().equals(TableConstants.INSTRUMENT_CODE_BANK_CHALLAN)
                || selectedDobj.getInstrument().equals(TableConstants.INSTRUMENT_TRANSFER)
                || selectedDobj.getInstrument().equals(TableConstants.INSTRUMENT_CODE_BANK_RECEIPT)
                || selectedDobj.getInstrument().equals(TableConstants.INSTRUMENT_NEFT_RTGS)
                || selectedDobj.getInstrument().equals(TableConstants.INSTRUMENT_O_GRASS))) {

            selectedDobj.setRenderChallanInstmnt(true);
            selectedDobj.setRenderDraftInstmnt(false);

        } else {
            selectedDobj.setRenderChallanInstmnt(false);
            selectedDobj.setRenderDraftInstmnt(true);
        }

        Exception exceptionMessage = null;
        try {
            //for Calling POS url for DoubleVerification
            if (selectedDobj.getInstrument().equalsIgnoreCase(TableConstants.MPOS_INSTRUMENT_CODE)) {//MPOS
                // Only For Delhi State
                if ("DL".equalsIgnoreCase(Util.getUserStateCode())) {
                    selectedDobj.setNumber(null);
                    selectedDobj.setAmount(null);
                    selectedDobj.setBranch(null);
                    bank_list.clear();
                    bank_list.add(new SelectItem(TableConstants.MPOS_BANK_CODE, TableConstants.MPOS_BANK_DESCR));
                    selectedDobj.setBank_list(bank_list);
                    selectedDobj.setBank_name(TableConstants.MPOS_BANK_CODE);
                    selectedDobj.setBankNameDesc(TableConstants.MPOS_BANK_DESCR);
                    selectedDobj.setDisableBranch(true);
                    selectedDobj.setDisableAmt(true);
                    selectedDobj.setDisableBankList(true);
                    selectedDobj.setDisableDate(true);
                    selectedDobj.setMin_draft_date(new Date());
                    selectedDobj.setMax_draft_date(new Date());
                    selectedDobj.setRenderDraftInstmnt(false);
                    selectedDobj.setRenderChallanInstmnt(false);
                    selectedDobj.setRenderPOSInstmnt(true);
                } else {
                    selectedDobj.setRenderDraftInstmnt(false);
                    selectedDobj.setRenderChallanInstmnt(false);
                    selectedDobj.setRenderPOSInstmnt(true);
                }
//            } else {
//                selectedDobj.setRenderPOSInstmnt(false);
//                selectedDobj.setDisableBranch(false);
//                selectedDobj.setDisableAmt(false);
//                selectedDobj.setDisableNumber(false);
//                selectedDobj.setDisableBankList(false);
//                selectedDobj.setDisableDate(false);
            }
        } catch (Exception ex) {
            exceptionMessage = ex;
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }

        if (exceptionMessage != null && (exceptionMessage instanceof VahanException || exceptionMessage instanceof Exception)) {
            bank_list.clear();
            selectedDobj.setInstrument("-1");
            if (getPaymentlist() != null) {
                getPaymentlist().clear();
            }
        }

        getPaymentlist().add(selectedDobj);

        if (error_message != null) { //need to handle use addmessage instead of it
            setPopUpMessage(error_message);
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("popUpError");
            PrimeFaces.current().executeScript("PF('dlgPopUpError').show()");
            selectedDobj.setInstrument("");
        }
    }

    public void verifyPosPayment(PaymentCollectionDobj selectedDobj) {
        String message = null;
        try {

            if (selectedDobj == null) {
                message = "Selected Record not found, Please Contact to the System Administrator";
                throw new VahanException(message);
            }

            if (selectedDobj.getInstrument() == null || selectedDobj.getInstrument().equals("-1")) {
                message = "Please Select Instrument Type";
                throw new VahanException(message);
            }

            if (selectedDobj.getNumber() != null) {
                POSImpl pOSImpl = new POSImpl();
                PosDobj posResponseDobj = pOSImpl.getDL_POSRespose(selectedDobj.getNumber());//pos web service calling               
                if (posResponseDobj != null) {
                    selectedDobj.setNumber(selectedDobj.getNumber());
                    selectedDobj.setAmount(String.valueOf(posResponseDobj.getTotalAmount()));
                    selectedDobj.setBranch(posResponseDobj.getAuthCode() + "/" + posResponseDobj.getRrNumber());
                    selectedDobj.setDated(new Date(posResponseDobj.getPostingDate()));
                    posResponseDobj.setStateCd(Util.getUserStateCode());
                    posResponseDobj.setOffCd(Util.getSelectedSeat().getOff_cd());
                    posResponseDobj.setInstrumentCode(TableConstants.MPOS_INSTRUMENT_CODE);
                    selectedDobj.setPosDobj(posResponseDobj);
                    selectedDobj.setDisableNumber(true);
                    getPaymentlist().remove(selectedDobj);
                    getPaymentlist().add(selectedDobj);
                } else {
                    throw new VahanException("No Record Found by fetching Details of Payment by POS Machiine, Please Contact to the System Administrator");
                }
            }
        } catch (VahanException vex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, vex.getMessage(), vex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            message = "Something Went Wrong during Verification of POS Payment, Please Contact to the System Administrator";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
        }
    }

    public void validatePaymentAmt(AjaxBehaviorEvent event) {
        if ("SK".contains(Util.getUserStateCode())) {
            PaymentCollectionDobj selectedDobj = (PaymentCollectionDobj) event.getComponent().getAttributes().get("paymentDobj");
            String error_message = null;
            if (selectedDobj.getInstrument() == null || selectedDobj.getInstrument().equals("-1")) {
                error_message = "Please Select Instrument Type";
            }

            if (selectedDobj.getBank_name() == null || selectedDobj.getBank_name().equals("-1")) {
                error_message = "Please Select Bank Name";
            }
            if (selectedDobj.getAmount() == null || selectedDobj.getAmount().equals("-1")) {
                error_message = "Please Enter the Amount";
            }

            if (error_message != null) {
                setPopUpMessage(error_message);
                //RequestContext rc = RequestContext.getCurrentInstance();
                PrimeFaces.current().ajax().update("popUpError");
                PrimeFaces.current().executeScript("PF('dlgPopUpError').show()");
                selectedDobj.setAmount("");
            }
        }
    }

    public void doubleVerification(PaymentCollectionDobj payCollectionDobj) {
        CryptographyAES aes = new CryptographyAES();
        String vahan_pgi_egrass_url = "";
        String error_message = null;
        String data = null;
        try {
            if (payCollectionDobj.getInstrument() != null && payCollectionDobj.getInstrument().equals(TableConstants.INSTRUMENT_CODE_BANK_RECEIPT) && getPayVerifyDobj().isBankReceiptVerify()) {
                vahan_pgi_egrass_url = ServerUtil.getVahanPgiUrl(TableConstants.VAHAN_PGI_SBS_CHECK_FAIL_CONNTYPE);
                data = "grn_no=" + payCollectionDobj.getNumber() + "|" + "amount=" + payCollectionDobj.getAmount() + "|" + "state_cd=" + Util.getUserStateCode() + "|" + "nic_merchant_cd=" + TableConstants.VAHAN_PGI_MERCHANT_CODE + "|" + "pay_gate=" + TableConstants.PAY_GATE_SBS + "|" + "transaction_date=" + payCollectionDobj.getDated();
            } else if (payCollectionDobj.getInstrument() != null && payCollectionDobj.getInstrument().equals(TableConstants.EGRASS_INSTRUMENT_CODE)) {
                vahan_pgi_egrass_url = ServerUtil.getVahanPgiUrl(TableConstants.VAHAN_PGI_EGRASS_CHECK_FAIL_CONNTYPE);
                data = "grn_no=" + payCollectionDobj.getNumber() + "|" + "amount=" + payCollectionDobj.getAmount() + "|" + "state_cd=" + Util.getUserStateCode() + "|" + "nic_merchant_cd=" + TableConstants.VAHAN_PGI_MERCHANT_CODE + "|" + "pay_gate=" + TableConstants.PAY_GATE;
            }


            data = aes.getEncriptedString(data);
            String sendData = vahan_pgi_egrass_url + data;
            String response = sendGet(sendData);
            if (response == null || response.equalsIgnoreCase("")) {
                throw new VahanException("Unable to get the response from the Bank.Please Try Again");
            } else if (response != null && !response.equals("")) {
                //Example : response = "status=S,BankDate=00000"
                response = response.split(",")[0].split("=")[1];
            }

            if (response.equals(TableConstants.VAHAN_PGI_SUCCESS)) {
                payCollectionDobj.setDisableInstrument(true);
                payCollectionDobj.setDisableBankList(true);
                payCollectionDobj.setDisableBranch(true);
                payCollectionDobj.setDisableAmt(true);
                payCollectionDobj.setDisableDate(true);
                payCollectionDobj.setDisableNumber(true);
                payCollectionDobj.seteGrassStatus("Successful");
                payCollectionDobj.setShowEgrassStatus(true);
                payCollectionDobj.seteGrassVerified(true);
                payCollectionDobj.setPaymentVerificationMessage("Payment Verification Successful");
                payCollectionDobj.setShowMinusBtn(false);
                payCollectionDobj.setShowVerifyBtn(false);
            } else {
                payCollectionDobj.setDisableInstrument(false);
                payCollectionDobj.setDisableBankList(false);
                payCollectionDobj.setDisableBranch(true);
                payCollectionDobj.setDisableAmt(false);
                payCollectionDobj.setDisableDate(false);
                payCollectionDobj.setDisableNumber(false);
                payCollectionDobj.setShowEgrassStatus(true);
                payCollectionDobj.seteGrassVerified(false);
                if ((response.equals(TableConstants.VAHAN_PGI_FAIL))) {
                    payCollectionDobj.seteGrassStatus("UnSuccessful");
                    payCollectionDobj.setPaymentVerificationMessage("PGI Verification Service Failed.Either Incorrect Data/Invalid Data is Entered.");
                }
                if (response.equals("No Response")) {
                    payCollectionDobj.seteGrassStatus("UnSuccessful");
                    payCollectionDobj.setPaymentVerificationMessage("Vahan Pgi Service Not Responding");
                }
                payCollectionDobj.setShowMinusBtn(true);
                payCollectionDobj.setShowVerifyBtn(true);
            }
        } catch (Exception e) {
            LOGGER.error(e);
            error_message = "Some Problem in getting response";
        }
        if (error_message != null) {
            setPopUpMessage(error_message);
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().ajax().update("popUpError");
            PrimeFaces.current().executeScript("PF('dlgPopUpError').show()");
        }
    }

    private String sendGet(String url) throws Exception {
        StringBuffer response = null;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            //add request header
            con.setRequestProperty("User-Agent", "Mozilla");
            int responseCode = con.getResponseCode();
            if (responseCode == HttpStatus.SC_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            }
        } catch (Exception e) {
            response = null;
            LOGGER.error(e);
        }
        return response.toString();
    }

    public String getIpPath() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String contextPath = request.getContextPath();
        StringBuffer requestURL = request.getRequestURL();
        String ipPath = requestURL.substring(0, requestURL.indexOf(contextPath)) + contextPath;
        return ipPath.replaceAll("http:", "https:");
    }

    /**
     * @return the payment_list
     */
    public List<PaymentCollectionDobj> getPaymentlist() {
        if (paymentlist == null) {
            paymentlist = new ArrayList<PaymentCollectionDobj>();
        }
        return paymentlist;
    }

    /**
     * @param payment_list the payment_list to set
     */
    public void setPaymentlist(List<PaymentCollectionDobj> paymentlist) {
        this.paymentlist = paymentlist;
    }

    /**
     * @return the bank
     */
    public String getBank() {
        return bank;
    }

    /**
     * @param bank the bank to set
     */
    public void setBank(String bank) {
        this.bank = bank;
    }

    /**
     * @return the payment_mode
     */
    public String getPayment_mode() {
        return payment_mode;
    }

    /**
     * @param payment_mode the payment_mode to set
     */
    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    /**
     * @return the header_name
     */
    public String getHeader_name() {
        return header_name;
    }

    /**
     * @param header_name the header_name to set
     */
    public void setHeader_name(String header_name) {
        this.header_name = header_name;
    }

    /**
     * @return the render_payment_table
     */
    public boolean isRender_payment_table() {
        return render_payment_table;
    }

    /**
     * @param render_payment_table the render_payment_table to set
     */
    public void setRender_payment_table(boolean render_payment_table) {
        this.render_payment_table = render_payment_table;
    }

    /**
     * @return the render_button
     */
    public boolean isRender_button() {
        return render_button;
    }

    /**
     * @param render_button the render_button to set
     */
    public void setRender_button(boolean render_button) {
        this.render_button = render_button;
    }

    public long getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(long balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public long getAmountInCash() {
        return amountInCash;
    }

    public void setAmountInCash(long amountInCash) {
        this.amountInCash = amountInCash;
    }

    public boolean isIsCashSelected() {
        return isCashSelected;
    }

    public void setIsCashSelected(boolean isCashSelected) {
        this.isCashSelected = isCashSelected;
    }

    public List getInstrumentList() {
        return instrumentList;
    }

    public void setInstrumentList(List instrumentList) {
        this.instrumentList = instrumentList;
    }

    public PaymentCollectionDobj getPaydobj() {
        return paydobj;
    }

    /**
     * @return the popUpMessage
     */
    public String getPopUpMessage() {
        return popUpMessage;
    }

    /**
     * @param popUpMessage the popUpMessage to set
     */
    public void setPopUpMessage(String popUpMessage) {
        this.popUpMessage = popUpMessage;
    }

    /**
     * @return the paymentCollectionPanelVisibility
     */
    public boolean isPaymentCollectionPanelVisibility() {
        return paymentCollectionPanelVisibility;
    }

    /**
     * @param paymentCollectionPanelVisibility the
     * paymentCollectionPanelVisibility to set
     */
    public void setPaymentCollectionPanelVisibility(boolean paymentCollectionPanelVisibility) {
        this.paymentCollectionPanelVisibility = paymentCollectionPanelVisibility;
    }

    /**
     * @return the excessAmount
     */
    public long getExcessAmount() {
        return excessAmount;
    }

    /**
     * @param excessAmount the excessAmount to set
     */
    public void setExcessAmount(long excessAmount) {
        this.excessAmount = excessAmount;
    }

    /**
     * @return the cash
     */
    public long getCash() {
        return cash;
    }

    /**
     * @param cash the cash to set
     */
    public void setCash(long cash) {
        this.cash = cash;
    }

    /**
     * @return the renderColumn
     */
    public boolean isRenderColumn() {
        return renderColumn;
    }

    /**
     * @param renderColumn the renderColumn to set
     */
    public void setRenderColumn(boolean renderColumn) {
        this.renderColumn = renderColumn;
    }

    /**
     * @return the payVerifyDobj
     */
    public TmConfigurationPayVerifyDobj getPayVerifyDobj() {
        return payVerifyDobj;
    }

    /**
     * @param payVerifyDobj the payVerifyDobj to set
     */
    public void setPayVerifyDobj(TmConfigurationPayVerifyDobj payVerifyDobj) {
        this.payVerifyDobj = payVerifyDobj;
    }

    public void setPaydobj(PaymentCollectionDobj paydobj) {
        this.paydobj = paydobj;
    }
}
