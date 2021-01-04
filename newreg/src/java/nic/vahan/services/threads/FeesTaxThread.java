/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services.threads;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.bean.TaxFormPanelBean;
import nic.vahan.form.bean.permit.PermitPanelBean;
import nic.vahan.form.dobj.DOTaxDetail;
import nic.vahan.form.dobj.EpayDobj;
import nic.vahan.form.dobj.FeeDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Tax_Pay_Dobj;
import static nic.vahan.form.impl.TaxServer_Impl.insertUpdateTaxDefaulter;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import nic.vahan.services.clients.dobj.FeeTaxDobj;
import nic.vahan.services.clients.dobj.FeeTaxInputDobj;
import nic.vahan.services.clients.NewFeesAndTaxClient;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Anu
 */
public class FeesTaxThread implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(FeesTaxThread.class);
    private Owner_dobj ownerDobj;
    private PermitPanelBean permitPanelBean;
    private List<TaxFormPanelBean> listTaxForm = null;
    private List<FeeDobj> feeCollectionLists = null;
    Integer offCd;
    int purCd;
    String feeAction;
    String rcptNo;
    List<EpayDobj> mandatoryFeeList = new ArrayList<>();
    String empCode;
    String stateCd;
    String URL;
    int actionCd;

    public FeesTaxThread(Owner_dobj ownerDobj, PermitPanelBean permitPanelBean, List<FeeDobj> feeCollectionLists,
            List<TaxFormPanelBean> listTaxForm, Integer offCd, int purCd, String feeAction, String rcptNo,
            String empCode, String stateCd, String URL, int actionCd) {

        this.ownerDobj = ownerDobj;
        this.permitPanelBean = permitPanelBean;
        this.feeCollectionLists = feeCollectionLists;
        this.listTaxForm = listTaxForm;
        this.purCd = purCd;
        this.offCd = offCd;
        this.feeAction = feeAction;
        this.rcptNo = rcptNo;
        this.empCode = empCode;
        this.stateCd = stateCd;
        this.URL = URL;
        this.actionCd = actionCd;
    }

    @Override
    public void run() {
        TransactionManager tmgr = null;
        try {


            //////////////////////////////////////////////////////////////////////////////////
            FeeTaxDobj feeTaxDobj = null;
            FeeTaxInputDobj feeTaxInput = null;
            NewFeesAndTaxClient feeAndTaxClient = null;
            List<EpayDobj> mandatoryFeeListService = null;

            List<TaxFormPanelBean> listTaxFormService = new ArrayList();
            try {
                tmgr = new TransactionManager("thread");
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                feeAndTaxClient = new NewFeesAndTaxClient(1, URL);
                feeTaxInput = new FeeTaxInputDobj();
                ownerDobj.setFeeAction(feeAction);
                //if (Util.getUserOffCode() != null) {
                ownerDobj.setUserOffCd(offCd);
                ownerDobj.setActionCd(actionCd);
                // }
                feeTaxInput.setOwnerDobj(ownerDobj);
                feeTaxInput.setPassengerPermitDetailDobj(permitPanelBean.getPermitDobj());
                feeTaxInput.setDate(new java.util.Date());
                feeTaxInput.setPurcode(purCd);
                String param = mapper.writeValueAsString(feeTaxInput);
                //System.out.println("param: " + param);
                String rt = feeAndTaxClient.newFeesTaxCalculation_JSON_STRING(String.class, param);
                feeTaxDobj = mapper.readValue(rt, FeeTaxDobj.class);
                mandatoryFeeListService = feeTaxDobj.getListOfFees();
//                if (feeAction.equalsIgnoreCase("NEW")) {
//                    EpayDobj epDobj = new EpayDobj();
//                    epDobj.setPurCd(80);
//                    mandatoryFeeList.remove(epDobj);
//                }
                for (FeeDobj feeDobj : feeCollectionLists) {

                    EpayDobj collectedFeeDobj = new EpayDobj();
                    collectedFeeDobj.setE_TaxFee((long) feeDobj.getFeeAmount());
                    collectedFeeDobj.setE_FinePenalty((long) feeDobj.getFineAmount());
                    collectedFeeDobj.setPurCd(feeDobj.getPurCd());
                    //collectedFeeDobj.setDisableDropDown(true);
                    collectedFeeDobj.setFromDate(feeDobj.getFromDate());
                    collectedFeeDobj.setUptoDate(feeDobj.getUptoDate());
                    collectedFeeDobj.setDueDate(feeDobj.getDueDate());
                    collectedFeeDobj.setDueDateString(feeDobj.getDueDateString());
                    // collectedFeeDobj.setReadOnlyFine(true);
                    if (collectedFeeDobj.getFromDate() != null && collectedFeeDobj.getUptoDate() != null) {
                        //  collectedFeeDobj.setRenderFromDate(true);
                        //  collectedFeeDobj.setRenderUptoDate(true);
                    }
                    mandatoryFeeList.add(collectedFeeDobj);
                }

                boolean notmatched = false;


                if (mandatoryFeeListService == null && mandatoryFeeList == null) {
                    mandatoryFeeListService = new ArrayList<>();
                } else if ((mandatoryFeeListService != null && mandatoryFeeList == null)
                        || (mandatoryFeeListService == null && mandatoryFeeList != null)) {
                    notmatched = true;
                    //LOGGER.info("TAX WS: AT Fee Payment Vahan4  one of them is empty Fee List Not matched From Service : " + param);
                } else if (mandatoryFeeListService.size() != mandatoryFeeList.size()) {
                    notmatched = true;
                    //LOGGER.info("TAX WS: AT Fee Payment Vahan4 Fee List length Not matched From Service : " + param);
                } else {
                    for (EpayDobj ep : mandatoryFeeList) {
                        if (mandatoryFeeListService.indexOf(ep) >= 0) {
                            EpayDobj epService = mandatoryFeeListService.get(mandatoryFeeListService.indexOf(ep));
                            if ((ep.getE_TaxFee() != epService.getE_TaxFee())
                                    || (ep.getE_FinePenalty() != epService.getE_FinePenalty())) {
                                notmatched = true;
                                // LOGGER.info("TAX WS: AT Fee Payment Not service fee list not matched with vahan4   : " + param);
                                break;
                            }
                        } else {
                            notmatched = true;
                            break;
                        }

                    }
                }


                if (notmatched) {

                    if (!(mandatoryFeeListService != null && !mandatoryFeeListService.isEmpty())) {
                        mandatoryFeeListService = mandatoryFeeList;
                    }


                    if (mandatoryFeeListService == null) {
                        mandatoryFeeListService = new ArrayList<>();
                    }

                    String vtFeeSQL = "INSERT INTO vt_fee_service("
                            + " regn_no, payment_mode, fees, fine, rcpt_no, rcpt_dt, pur_cd, "
                            + " flag, collected_by, state_cd, off_cd)"
                            + " VALUES (?, ?, ?, ?, ?, current_timestamp, ?, "
                            + " ?, ?, ?, ?)";
                    long lngTotalAmt = 0;
                    PreparedStatement pstmtVtFee = null;
                    pstmtVtFee = tmgr.prepareStatement(vtFeeSQL);
                    for (EpayDobj feePurDobj : mandatoryFeeListService) {
                        pstmtVtFee.setString(1, feeAction);//regn_no
                        pstmtVtFee.setString(2, "S");//payment_mode
                        pstmtVtFee.setLong(3, feePurDobj.getE_TaxFee());//fees
                        pstmtVtFee.setLong(4, feePurDobj.getE_FinePenalty());//fine
                        lngTotalAmt = lngTotalAmt + (feePurDobj.getE_TaxFee() + feePurDobj.getE_FinePenalty());
                        pstmtVtFee.setString(5, rcptNo);//rcpt_no // 
                        pstmtVtFee.setInt(6, feePurDobj.getPurCd());//   pur_cd
                        pstmtVtFee.setNull(7, java.sql.Types.VARCHAR);//   flag
                        pstmtVtFee.setString(8, empCode);//   collected_by
                        pstmtVtFee.setString(9, stateCd);//   state_cd
                        pstmtVtFee.setInt(10, offCd);//   off_cd
                        pstmtVtFee.addBatch();
                    }
                    if (pstmtVtFee != null) {
                        pstmtVtFee.executeBatch();
                    }
                }

                Comparator<DOTaxDetail> taxFromdateCompartor = new Comparator<DOTaxDetail>() {
                    @Override
                    public int compare(DOTaxDetail o1, DOTaxDetail o2) {
                        Date taxFromDate1 = null;
                        Date taxUptoDate2 = null;
                        if (o1 != null && o2 != null) {
                            try {
                                taxFromDate1 = DateUtils.parseDate(o1.getTAX_FROM());
                                taxUptoDate2 = DateUtils.parseDate(o2.getTAX_FROM());
                            } catch (Exception e) {
                                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                            }
                            if (taxFromDate1 != null && taxUptoDate2 != null) {
                                return taxFromDate1.compareTo(taxUptoDate2);
                            }
                        }
                        return 0;
                    }
                };
                notmatched = false;
                listTaxFormService = feeTaxDobj.getListTaxForm();
                if (listTaxFormService == null && listTaxForm == null) {
                    listTaxFormService = new ArrayList<>();
                } else if ((listTaxFormService != null && listTaxForm == null)
                        || ((listTaxFormService == null && listTaxForm != null))) {
                    notmatched = true;
                    // LOGGER.info("TAX WS: AT Fee Payment Vahan Tax List,one of them is empty : " + param);

                } else if (listTaxFormService.size() != listTaxForm.size()) {
                    notmatched = true;
                    //LOGGER.info("TAX WS: AT Fee Payment Vahan Tax List length Not matched From Service : " + param);

                } else {
                    if (listTaxForm != null) {
                        for (TaxFormPanelBean ep : listTaxForm) {
                            TaxFormPanelBean epService = listTaxFormService.get(listTaxFormService.indexOf(ep));
                            List<DOTaxDetail> epList = ep.getTaxDescriptionList();
                            List<DOTaxDetail> epserviceList = epService.getTaxDescriptionList();
                            if (epList == null && epserviceList == null) {
                                continue;
                            }
                            if ((epList == null && epserviceList != null)
                                    && (epList != null && epserviceList == null)) {
                                notmatched = true;
                                // LOGGER.info("TAX WS: AT Fee Payment Vahan Tax breakup list either of them is empty : " + param);
                                break;
                            }

                            Collections.sort(epList, taxFromdateCompartor);
                            Collections.sort(epserviceList, taxFromdateCompartor);
                            if (!epList.toString().equals(epserviceList.toString())) {
                                notmatched = true;
                                //LOGGER.info("TAX WS: AT Fee Payment Not service tax list not matched with vahan4  : " + param);
                                break;
                            }
                        }
                    }
                }


                if (notmatched) {
                    if (!(listTaxFormService != null && !listTaxFormService.isEmpty())) {
                        listTaxFormService = listTaxForm;
                    }
                    if (listTaxFormService == null) {
                        listTaxFormService = new ArrayList<>();
                    }
                    List<Tax_Pay_Dobj> listTaxDobj = new ArrayList<>();
                    for (TaxFormPanelBean bean : listTaxFormService) {
                        if (!bean.getTaxMode().equals("0")) {
                            Tax_Pay_Dobj taxDobj = new Tax_Pay_Dobj();

                            taxDobj.setFinalTaxAmount(bean.getFinalTaxAmount());
                            taxDobj.setTotalPaybaleTax(bean.getTotalPaybaleTax());
                            taxDobj.setTotalPaybalePenalty(bean.getTotalPaybalePenalty());
                            taxDobj.setTotalPaybaleSurcharge(bean.getTotalPaybaleSurcharge());
                            taxDobj.setTotalPaybaleRebate(bean.getTotalPaybaleRebate());
                            taxDobj.setTotalPaybaleInterest(bean.getTotalPaybaleInterest());
                            taxDobj.setFinalTaxFrom(bean.getFinalTaxFrom());
                            taxDobj.setFinalTaxUpto(bean.getFinalTaxUpto());
                            taxDobj.setTaxMode(bean.getTaxMode());
                            taxDobj.setPur_cd(bean.getPur_cd());
                            taxDobj.setRegnNo(feeAction);
                            taxDobj.setPaymentMode("S");
                            taxDobj.setTaxBreakDetails(bean.getTaxDescriptionList());
                            taxDobj.setDeal_cd(empCode);
                            taxDobj.setOff_cd(offCd);

                            taxDobj.setOp_dt(new java.util.Date());
                            taxDobj.setRcptDate(new java.util.Date());
                            taxDobj.setApplNo("");
                            taxDobj.setNoOfAdvUnits(bean.getNoOfUnits());
                            taxDobj.setTotalPaybaleTax1(bean.getTotalTax1());
                            taxDobj.setTotalPaybaleTax2(bean.getTotalTax2());
                            listTaxDobj.add(taxDobj);

                        }

                    }
                    for (Tax_Pay_Dobj taxDobj : listTaxDobj) {
                        String vtTaxSQL = "INSERT INTO vt_tax_service("
                                + " regn_no, tax_mode, payment_mode, tax_amt, tax_fine, rcpt_no, rcpt_dt, tax_from, tax_upto, pur_cd, flag, collected_by, state_cd,off_cd)"
                                + " VALUES (?, ?, ?, ?, ?, ?, current_timestamp, ?, ?, ?, ?, ?, ?, ?)";

                        PreparedStatement pstmtVtTax = tmgr.prepareStatement(vtTaxSQL);
                        int i = 1;
                        pstmtVtTax.setString(i++, taxDobj.getRegnNo());//regn_no
                        pstmtVtTax.setString(i++, taxDobj.getTaxMode());//tax_mode
                        pstmtVtTax.setString(i++, "S");//payment_mode
                        pstmtVtTax.setLong(i++, taxDobj.getFinalTaxAmount() - taxDobj.getTotalPaybalePenalty());//tax_amt
                        pstmtVtTax.setLong(i++, taxDobj.getTotalPaybalePenalty());//tax_fine
                        pstmtVtTax.setString(i++, rcptNo);//rcpt_no
                        pstmtVtTax.setDate(i++, new java.sql.Date(DateUtils.parseDate(taxDobj.getFinalTaxFrom()).getTime()));//tax_from
                        if (taxDobj.getFinalTaxUpto() != null) {
                            pstmtVtTax.setDate(i++, new java.sql.Date(DateUtils.parseDate(taxDobj.getFinalTaxUpto()).getTime()));//tax_upto
                        } else {
                            pstmtVtTax.setNull(i++, java.sql.Types.DATE);//tax_upto
                        }

                        pstmtVtTax.setInt(i++, taxDobj.getPur_cd());//   pur_cd
                        pstmtVtTax.setNull(i++, java.sql.Types.VARCHAR);//   flag
                        pstmtVtTax.setString(i++, empCode);//   collected_by
                        pstmtVtTax.setString(i++, stateCd);//   state_cd
                        pstmtVtTax.setInt(i++, offCd);//   off_cd
                        pstmtVtTax.executeUpdate();

                        ownerDobj.setAppl_no(null);
                        int srNo = 0;
                        for (DOTaxDetail taxBrkUpDobj : taxDobj.getTaxBreakDetails()) {
                            String vtTaxBrkupSQL = "INSERT INTO vt_tax_breakup_service("
                                    + " rcpt_no, sr_no, tax_from, tax_upto, pur_cd, prv_adjustment, tax, "
                                    + " exempted, rebate, surcharge, penalty, interest, tax1, tax2, "
                                    + " state_cd, off_cd)"
                                    + " VALUES (?, ?, ?, ?, ?, ?, ?, "
                                    + " ?, ?, ?, ?, ?, ?, ?, "
                                    + " ?, ?)";

                            PreparedStatement pstmtVtTaxBrkUp = tmgr.prepareStatement(vtTaxBrkupSQL);

                            // Mulitple Entry for Tax Breakup
                            java.sql.Timestamp taxFrom = null;
                            java.sql.Timestamp taxUpto = null;

                            taxFrom = new java.sql.Timestamp(DateUtils.parseDate(taxBrkUpDobj.getTAX_FROM()).getTime());
                            srNo++;
                            pstmtVtTaxBrkUp.setString(1, rcptNo);//rcpt_no
                            pstmtVtTaxBrkUp.setInt(2, srNo);//sr_no
                            pstmtVtTaxBrkUp.setTimestamp(3, taxFrom);//tax_from
                            if (taxBrkUpDobj.getTAX_UPTO() != null) {
                                taxUpto = new java.sql.Timestamp(DateUtils.parseDate(taxBrkUpDobj.getTAX_UPTO()).getTime());
                                pstmtVtTaxBrkUp.setTimestamp(4, taxUpto);//tax_upto
                            } else {
                                pstmtVtTaxBrkUp.setNull(4, java.sql.Types.DATE);//tax_upto
                            }

                            pstmtVtTaxBrkUp.setInt(5, taxBrkUpDobj.getPUR_CD());//pur_cd
                            pstmtVtTaxBrkUp.setLong(6, taxBrkUpDobj.getPRV_ADJ());//prv_adjustment
                            pstmtVtTaxBrkUp.setDouble(7, taxBrkUpDobj.getAMOUNT());//tax
                            pstmtVtTaxBrkUp.setLong(8, 0);//exempted
                            pstmtVtTaxBrkUp.setDouble(9, taxBrkUpDobj.getREBATE());//rebate
                            pstmtVtTaxBrkUp.setDouble(10, taxBrkUpDobj.getSURCHARGE());//surcharge
                            pstmtVtTaxBrkUp.setDouble(11, taxBrkUpDobj.getPENALTY());//penalty
                            pstmtVtTaxBrkUp.setDouble(12, taxBrkUpDobj.getINTEREST());//interest
                            pstmtVtTaxBrkUp.setDouble(13, taxBrkUpDobj.getAMOUNT1());//excess_amt
                            pstmtVtTaxBrkUp.setDouble(14, taxBrkUpDobj.getAMOUNT2());//cash_amt
                            pstmtVtTaxBrkUp.setString(15, stateCd);//   state_cd
                            pstmtVtTaxBrkUp.setInt(16, offCd);//   off_cd
                            pstmtVtTaxBrkUp.executeUpdate();
                            //isSavedFlag = true;

                        }
                    }
                }
                tmgr.commit();


            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
            //////////////////////////////////////////////////////////////////////////////////
        } catch (Exception ex) {
            //LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception e) {
                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                }
            }
        }
    }
}
