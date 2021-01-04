/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.sql.RowSet;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.bean.PaymentCollectionBean;
import nic.vahan.form.bean.TaxFormPanelBean;
import nic.vahan.form.dobj.DOTaxDetail;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.PaymentCollectionDobj;
import nic.vahan.form.dobj.SingleDraftPaymentDobj;
import nic.vahan.form.dobj.Tax_Pay_Dobj;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC075
 */
public class SingleDraftPaymentImpl {

    private static final Logger LOGGER = Logger.getLogger(SingleDraftPaymentImpl.class);

    public List<SingleDraftPaymentDobj> setInstrumentCart(List<PaymentCollectionDobj> paymentlist, List<TaxFormPanelBean> listTaxForm, String regnNo, PassengerPermitDetailDobj permitDobj) throws VahanException {

        List<SingleDraftPaymentDobj> list = new ArrayList<SingleDraftPaymentDobj>();
        TransactionManager tmgr = null;
        String whereiam = "SingleDraftPaymentImpl.setInstrumentCart()";
        PreparedStatement psmt = null;
        RowSet rs = null;
        try {
            SingleDraftPaymentDobj sdbObj = null;
            tmgr = new TransactionManager(whereiam);
            String sql = "INSERT INTO vp_instrument_cart(\n"
                    + "            state_cd, off_cd, user_cd, regn_no, pur_cd, period_mode, period_from,  "
                    + "            period_upto, amount, exempted, rebate, surcharge, penalty, interest,  "
                    + "             sr_no, instrument_type, instrument_no, instrument_dt,  "
                    + "            instrument_amt, bank_code, branch_name ,received_dt, pmt_type, "
                    + "            pmt_catg, service_type, route_class, route_length, no_of_trips, "
                    + "            domain_cd, distance_run_in_quarter ) "
                    + "            VALUES (?, ?, ?, ?, ?, ?, ?,  "
                    + "             ?, ?, ?, ?, ?, ?,  "
                    + "            ?, ?, ?, ?, ?,  "
                    + "            ?, ?, ?,current_timestamp , ?, ?, ?, ?, ?, ?, ?, ? ) ";

            int i = 1;
            int p = 0;
            for (TaxFormPanelBean dobj : listTaxForm) {
                if (dobj.getTaxDescriptionList().size() > 0) {
                    for (PaymentCollectionDobj pcd : paymentlist) {
                        psmt = tmgr.prepareStatement(sql);

                        psmt.setString(1, Util.getUserStateCode().trim().toUpperCase());
                        psmt.setInt(2, Util.getSelectedSeat().getOff_cd());
                        psmt.setInt(3, Integer.parseInt(Util.getEmpCode()));
                        psmt.setString(4, regnNo.trim().toUpperCase());
                        psmt.setInt(5, dobj.getPur_cd());
                        psmt.setString(6, dobj.getTaxMode()); //period_mode // 
                        psmt.setDate(7, new java.sql.Date(JSFUtils.getStringToDateddMMMyyyy(dobj.getFinalTaxFrom()).getTime())); //period_from tax from
                        psmt.setDate(8, new java.sql.Date(JSFUtils.getStringToDateddMMMyyyy(dobj.getFinalTaxUpto()).getTime())); //period_upto tax upto
                        psmt.setLong(9, dobj.getFinalTaxAmount()); // tax ammount
                        psmt.setLong(10, 0);    // exempted zeo 
                        psmt.setLong(11, dobj.getTotalPaybaleRebate()); //rebate
                        psmt.setLong(12, dobj.getTotalPaybaleSurcharge()); //surcharge
                        psmt.setLong(13, dobj.getTotalPaybalePenalty()); //penalty
                        psmt.setLong(14, dobj.getTotalPaybaleInterest()); //interest
                        //   psmt.setString(15, ""); //transaction_no
                        psmt.setInt(15, i); //sr_no
                        psmt.setString(16, pcd.getInstrument()); //instrument_type
                        psmt.setString(17, pcd.getNumber()); //instrument_no
                        psmt.setTimestamp(18, new java.sql.Timestamp(pcd.getDated().getTime()));
                        // psmt.setDate(18, (Date) pcd.getDated()); //instrument_dt
                        psmt.setLong(19, Long.parseLong(pcd.getAmount())); //instrument_amt
                        psmt.setString(20, pcd.getBank_name()); //bank_code
                        psmt.setString(21, pcd.getBranch().toUpperCase()); //branch_name
                        //  psmt.setString(23, ""); //received_dt
                        //psmt.setString(24, ""); //rcpt_no
                        //psmt.setString(25, ""); //rcpt_dt
                        //psmt.setString(3, dobj.get.trim().toUpperCase());

                        if (permitDobj != null && permitDobj.getPmt_type_code() != null && !permitDobj.getPmt_type_code().equals("")) {
                            psmt.setInt(22, Integer.parseInt(permitDobj.getPmt_type_code()));
                        } else {
                            psmt.setNull(22, java.sql.Types.INTEGER);
                        }

                        if (permitDobj != null && permitDobj.getPmtCatg() != null && !permitDobj.getPmtCatg().equals("")) {
                            psmt.setInt(23, Integer.parseInt(permitDobj.getPmtCatg()));
                        } else {
                            psmt.setNull(23, java.sql.Types.INTEGER);
                        }

                        if (permitDobj != null && permitDobj.getServices_TYPE() != null && !permitDobj.getServices_TYPE().equals("")) {
                            psmt.setInt(24, Integer.parseInt(permitDobj.getServices_TYPE()));
                        } else {
                            psmt.setNull(24, java.sql.Types.INTEGER);
                        }


                        if (permitDobj != null && permitDobj.getRout_code() != null && !permitDobj.getRout_code().equals("")) {
                            psmt.setInt(25, Integer.parseInt(permitDobj.getRout_code()));
                        } else {
                            psmt.setNull(25, java.sql.Types.INTEGER);
                        }

                        if (permitDobj != null && permitDobj.getRout_length() != null && !permitDobj.getRout_length().equals("")) {
                            psmt.setInt(26, Integer.parseInt(permitDobj.getRout_length()));
                        } else {
                            psmt.setNull(26, java.sql.Types.INTEGER);
                        }

                        if (permitDobj != null && permitDobj.getNumberOfTrips() != null && !permitDobj.getNumberOfTrips().equals("")) {
                            psmt.setInt(27, Integer.parseInt(permitDobj.getNumberOfTrips()));
                        } else {
                            psmt.setNull(27, java.sql.Types.INTEGER);
                        }

                        if (permitDobj != null && permitDobj.getDomain_CODE() != null && !permitDobj.getDomain_CODE().equals("")) {
                            psmt.setInt(28, Integer.parseInt(permitDobj.getDomain_CODE()));
                        } else {
                            psmt.setNull(28, java.sql.Types.INTEGER);
                        }

                        psmt.setNull(29, java.sql.Types.INTEGER);//ps.setInt(i++, permitDobj != null && permitDobj.getDistance_run_in_quarter());
                        psmt.executeUpdate();
                        p = i;
                        i++;
                    }



                    String sqlBreakup = "INSERT INTO vp_instrument_tax_breakup( "
                            + "            state_cd, off_cd, regn_no, sr_no, tax_from, tax_upto, pur_cd,  "
                            + "            prv_adjustment, tax, exempted, rebate, surcharge, penalty, interest,user_cd,tax1,tax2) "
                            + "    VALUES (?, ?, ?, ?, ?, ?, ?,  "
                            + "            ?, ?, ?, ?, ?, ?, ?,?,?,?) ";
                    p = 1;
                    for (DOTaxDetail taxDobj : dobj.getTaxDescriptionList()) {
                        psmt = tmgr.prepareStatement(sqlBreakup);
                        psmt.setString(1, Util.getUserStateCode().trim().toUpperCase());
                        psmt.setInt(2, Util.getSelectedSeat().getOff_cd());
                        psmt.setString(3, regnNo.trim().toUpperCase()); //regn_no
                        psmt.setInt(4, p); //sr_no
                        psmt.setDate(5, new java.sql.Date(JSFUtils.getStringToDateddMMMyyyy(taxDobj.getTAX_FROM()).getTime()));//tax_from
                        psmt.setDate(6, new java.sql.Date(JSFUtils.getStringToDateddMMMyyyy(taxDobj.getTAX_UPTO()).getTime())); //tax_upto
                        psmt.setInt(7, dobj.getPur_cd()); //pur_cd
                        psmt.setLong(8, taxDobj.getPRV_ADJ()); //prv_adjustment  tax upto
                        psmt.setDouble(9, taxDobj.getAMOUNT()); //tax
                        psmt.setDouble(10, 0);    // exempted o
                        psmt.setDouble(11, taxDobj.getREBATE()); //rebate
                        psmt.setDouble(12, taxDobj.getSURCHARGE()); //surcharge
                        psmt.setDouble(13, taxDobj.getPENALTY()); //penalty
                        psmt.setDouble(14, taxDobj.getINTEREST()); //interest  
                        psmt.setInt(15, Integer.parseInt(Util.getEmpCode()));//user_cd
                        psmt.setDouble(16, taxDobj.getAMOUNT1()); //tax1
                        psmt.setDouble(17, taxDobj.getAMOUNT2()); //tax2
                        psmt.executeUpdate();
                        p++;
                    }

                }
            }
            tmgr.commit();
            list = getCartList();
            return list;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Error In Adding Details to Cart");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }

            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

    }

    public List<SingleDraftPaymentDobj> getCartList() {
        List<SingleDraftPaymentDobj> list = new ArrayList<SingleDraftPaymentDobj>();
        TransactionManager tmgr = null;
        String whereiam = "SingleDraftPaymentImpl.getCartList()";
        PreparedStatement psmt = null;

        RowSet rs = null;
        RowSet rs1 = null;
        String sql2 = "select distinct regn_no, amount,pur_cd,rebate,surcharge,penalty,interest,period_from,period_upto,"
                + " period_mode  from vp_instrument_cart  where user_cd = ?";
        try {
            tmgr = new TransactionManager(whereiam);
            psmt = tmgr.prepareStatement(sql2);
            psmt.setInt(1, Integer.parseInt(Util.getEmpCode()));
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                SingleDraftPaymentDobj dobj = new SingleDraftPaymentDobj();
                dobj.setRegn_no(rs.getString("regn_no"));
                dobj.setAmount(rs.getLong("amount"));
                String sql3 = "select distinct regn_no ,pur_cd,rebate,surcharge,penalty,interest,period_from,period_upto,"
                        + " period_mode , amount, sum(amount+surcharge+penalty+interest-exempted-rebate) as total  from vp_instrument_cart "
                        + " where regn_no = ? and pur_cd =? and user_cd = ? group by 1,2,3,4,5,6,7,8,9,10";
                psmt = tmgr.prepareStatement(sql3);

                psmt.setString(1, rs.getString("regn_no"));
                psmt.setInt(2, rs.getInt("pur_cd"));
                psmt.setInt(3, Integer.parseInt(Util.getEmpCode()));
                rs1 = tmgr.fetchDetachedRowSet();
                List<TaxFormPanelBean> listTaxForm = new ArrayList<TaxFormPanelBean>();
                while (rs1.next()) {
                    TaxFormPanelBean tfpbDobj = new TaxFormPanelBean();
                    tfpbDobj.setTotalPaybaleTax(rs1.getLong("total"));
                    tfpbDobj.setFinalTaxAmount(rs1.getLong("amount"));
                    tfpbDobj.setPur_cd(rs1.getInt("pur_cd"));
                    tfpbDobj.setTotalPaybaleRebate(rs1.getLong("rebate"));
                    tfpbDobj.setTotalPaybaleSurcharge(rs1.getLong("surcharge"));
                    tfpbDobj.setTotalPaybalePenalty(rs1.getLong("penalty"));
                    tfpbDobj.setTotalPaybaleInterest(rs1.getLong("interest"));
                    tfpbDobj.setFinalTaxFrom(DateUtils.getDateInDDMMYYYY(rs1.getDate("period_from")));
                    tfpbDobj.setFinalTaxUpto(DateUtils.getDateInDDMMYYYY(rs1.getDate("period_upto")));
                    tfpbDobj.setTaxMode(rs1.getString("period_mode"));
                    listTaxForm.add(tfpbDobj);


                }
                dobj.setListTaxFormCart(listTaxForm);
                list.add(dobj);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

        return list;
    }

    public static void deleteCartList(String regnNo) throws Exception {
        String sql = "Delete from vp_instrument_cart where  user_cd = ? and "
                + "  regn_no =?";
        TransactionManager tmgr = null;

        try {
            tmgr = new TransactionManager("deleteCartList");
            PreparedStatement ps = tmgr.prepareStatement(sql);

            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(Util.getEmpCode()));
            ps.setString(2, regnNo);
            ps.executeUpdate();

            sql = "Delete from vp_instrument_tax_breakup where  "
                    + "  regn_no =? and user_cd = ? ";

            ps = tmgr.prepareStatement(sql);

            ps.setString(1, regnNo);
            ps.setInt(2, Integer.parseInt(Util.getEmpCode()));
            ps.executeUpdate();
            tmgr.commit();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            if (tmgr != null) {
                try {
                    tmgr.release();
                } catch (Exception ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);

                }

            }
        }

    }

    public List<PaymentCollectionDobj> setPaymentList() {
        List<PaymentCollectionDobj> list = new ArrayList<PaymentCollectionDobj>();
        TransactionManager tmgr = null;
        String whereiam = "SingleDraftPaymentImpl.setPaymentList()";
        PreparedStatement psmt = null;
        RowSet rs = null;
        String sql2 = "SELECT distinct instrument_type, instrument_no, instrument_dt, to_char( instrument_dt,'dd-Mon-yyyy') as formatDate,"
                + "    instrument_amt, bank_code, branch_name "
                + "    FROM vp_instrument_cart "
                + "where user_cd = ?";
        try {
            tmgr = new TransactionManager(whereiam);
            psmt = tmgr.prepareStatement(sql2);
            psmt.setInt(1, Integer.parseInt(Util.getEmpCode()));
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                PaymentCollectionDobj dobj = new PaymentCollectionDobj();
                dobj.setInstrument(rs.getString("instrument_type"));
                dobj.setNumber(rs.getString("instrument_no"));
                dobj.setDated(rs.getDate("instrument_dt"));
                dobj.setAmount(String.valueOf(rs.getLong("instrument_amt")));
                dobj.setBank_name(rs.getString("bank_code"));
                dobj.setBranch(rs.getString("branch_name").toUpperCase());
                String[][] instrmentType = MasterTableFiller.masterTables.TM_INSTRUMENTS.getData();
                String[][] data = MasterTableFiller.masterTables.TM_BANK.getData();
                for (int i = 0; i < instrmentType.length; i++) {
                    if (instrmentType[i][0].equals(rs.getString("instrument_type"))) {
                        dobj.setInstrumentDesc(instrmentType[i][1]);
                    }
                }
                for (int i = 0; i < data.length; i++) {
                    if (data[i][0].equals(rs.getString("bank_code"))) {
                        dobj.setBankNameDesc(data[i][1]);
                    }
                }
                dobj.setDateInFormat(rs.getString("formatDate"));
                list.add(dobj);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

        return list;
    }

    public String saveReceiptDetails(List<SingleDraftPaymentDobj> addToCartList, List<PaymentCollectionDobj> paymentlist, long excessAmount) {
        OwnerDetailsDobj ownerDtlsDobj = null;
        Owner_dobj ownerDobj = null;
        //PermitPanelBean permitPanelBean = null;
        String[] generatedRcptNo = null;
        //List<TaxFormPanelBean> listTaxForm = null;
        PaymentCollectionBean paymentCollectionBean = null;
        ArrayList listOfRcptNo = new ArrayList();
        Long userChrg;
        List<SingleDraftPaymentDobj> list = new ArrayList<SingleDraftPaymentDobj>();
        SingleDraftPaymentImpl taxServer = new SingleDraftPaymentImpl();
        TransactionManager tmgr = null;
        // permitPanelBean = new PermitPanelBean();
        paymentCollectionBean = new PaymentCollectionBean();
        try {
            OwnerImpl ownerImpl = new OwnerImpl();
            tmgr = new TransactionManager("saveReceiptDetails");
            PassengerPermitDetailDobj permitDob = null;
            FeeDraftDobj feeDraftDobj = null;
            long inscd = 0;
            feeDraftDobj = new FeeDraftDobj();
            feeDraftDobj.setCollected_by(Util.getEmpCode());
            feeDraftDobj.setState_cd(Util.getUserStateCode());
            feeDraftDobj.setOff_cd(String.valueOf(Util.getUserOffCode()));
            feeDraftDobj.setDraftPaymentList(paymentlist);
            FeeDraftimpl feeDraft_impl = new FeeDraftimpl();
            inscd = feeDraft_impl.saveDraftDetails(feeDraftDobj, tmgr);
            Map<String, List<Tax_Pay_Dobj>> mp = new HashMap<>();

            for (SingleDraftPaymentDobj cart : addToCartList) {
                List<Tax_Pay_Dobj> listTaxDob = new ArrayList<>();
                for (TaxFormPanelBean bean : cart.getListTaxFormCart()) {
                    ownerDtlsDobj = ownerImpl.getOwnerDetails(cart.getRegn_no(), Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                    ownerDobj = ownerImpl.getOwnerDobj(ownerDtlsDobj);
                    if (ownerDobj != null) {
                        permitDob = TaxServer_Impl.getPermitInfoForVpInstrumentCart(ownerDobj.getRegn_no());
                    }
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
                        taxDobj.setRegnNo(cart.getRegn_no());
                        taxDobj.setTotalAmount((long) (cart.getAmount()));
                        taxDobj.setTaxBreakDetails(bean.getTaxDescriptionList());

                        listTaxDob.add(taxDobj);
                        if (!mp.containsKey(cart.getRegn_no())) {
                            mp.put(cart.getRegn_no(), listTaxDob);
                        } else {
                            mp.get(cart.getRegn_no()).add(taxDobj);
                        }

                    }
                }

            }

            Set<Map.Entry<String, List<Tax_Pay_Dobj>>> entrySet = mp.entrySet();
            int listsize = entrySet.size();
            int loopValue = 1;
            for (Entry<String, List<Tax_Pay_Dobj>> entry : entrySet) {
                List<Tax_Pay_Dobj> listCard = entry.getValue();
                String regnNo = entry.getKey();
                ownerDtlsDobj = ownerImpl.getOwnerDetails(regnNo, Util.getUserStateCode(), Util.getSelectedSeat().getOff_cd());
                ownerDobj = ownerImpl.getOwnerDobj(ownerDtlsDobj);

                String rcptNo = Receipt_Master_Impl.generateNewRcptNo(Util.getSelectedSeat().getOff_cd(), tmgr);
                String applNo = ServerUtil.getUniqueApplNo(tmgr, Util.getUserStateCode());

                Long userCharges = 0L;
                //String payMode = paymentCollectionBean.getPayment_mode();
                String payMode = "M"; //for mixed mode
                // Long userCharges = EpayImpl.getUserChargesFee(ownerDobj, listPurCd, null);
                generatedRcptNo = taxServer.saveTaxTransactionDetails(rcptNo, applNo, userCharges, payMode, listCard, feeDraftDobj, ownerDobj, permitDob, tmgr, inscd);
                FeeImpl feeImpl = new FeeImpl();
                //    FeeImpl.PaymentGenInfo paymentInfo = feeImpl.getPaymentInfo(listCard, feeDraftDobj, userCharges);

                if (loopValue == listsize) {
                    saveExcessAmountRecord(regnNo, payMode, rcptNo, excessAmount, tmgr);
                }
                FeeImpl.PaymentGenInfo paymentInfo = new FeeImpl.PaymentGenInfo();
                paymentInfo.setCashAmt(0);
                paymentInfo.setExcessAmt(0);
                feeImpl.saveRecptInstMap(inscd, generatedRcptNo[1], generatedRcptNo[0], paymentInfo, ownerDobj, tmgr, "Single Draft Payment");

                for (Tax_Pay_Dobj rcptList : listCard) {
                    SingleDraftPaymentDobj dobj = new SingleDraftPaymentDobj();
                    dobj.setReceiptNo(generatedRcptNo[0]);
                    dobj.setRegn_no(rcptList.getRegnNo());
                    dobj.setAmount(rcptList.getTotalAmount());
                    list.add(dobj);
                }
                listOfRcptNo.add(generatedRcptNo);

                loopValue++;
            }
            tmgr.commit();

            if (list != null) {
                StringBuffer rcptNo = new StringBuffer();
                for (int i = 0; i < list.size(); i++) {
                    rcptNo.append(list.get(i).getReceiptNo() + ",");
                }

                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful Transaction", " Receipt Number Generated : " + rcptNo);
                FacesContext.getCurrentInstance().addMessage(null, msg);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("list", list);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("paymentlist", paymentlist);
                return "PrintMultipleDraftReceiptReport";
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception ee) {
                LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            }
        }
        return "";
    }

    public void saveExcessAmountRecord(String regnNo, String paymode, String rcptNo, long excessAmount, TransactionManager tmgr) throws SQLException {
        try {
            String sql = "INSERT INTO " + TableList.VT_FEE
                    + "( regn_no, payment_mode, fees, fine, rcpt_no, rcpt_dt, pur_cd, "
                    + " collected_by, state_cd, off_cd )"
                    + "    VALUES (?, ?, ?, ?, ?, current_timestamp  , ?, ?, ?,?)";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, regnNo);
            ps.setString(i++, paymode);
            ps.setLong(i++, excessAmount);
            ps.setLong(i++, 0);
            ps.setString(i++, rcptNo);
            ps.setInt(i++, 48);
            ps.setString(i++, Util.getEmpCode());
            ps.setString(i++, Util.getUserStateCode());
            ps.setInt(i++, Util.getSelectedSeat().getOff_cd());
            ps.executeUpdate();
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public String[] saveTaxTransactionDetails(String rcptNo, String applNo, Long userChg, String payMode, List<Tax_Pay_Dobj> listTaxDobj, FeeDraftDobj feeDraftDobj, Owner_dobj dobj, PassengerPermitDetailDobj permitDobj, TransactionManager tmgr, long inscd) throws VahanException {

        String[] rets = new String[2];

        Exception e = null;
        try {


            rets[0] = rcptNo;
            String sql = "Select rcpt_no from " + TableList.VT_FEE + " where rcpt_no=? and state_cd=? and off_cd=?";
            PreparedStatement ps = tmgr.prepareStatement(sql);
            ps.setString(1, rcptNo);
            ps.setString(2, dobj.getState_cd());
            ps.setInt(3, dobj.getOff_cd());
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            if (rs.next()) {
                throw new VahanException("Receipt No " + rcptNo + " is already assigned to Fee");
            }
            if (userChg != null && userChg != 0) {
                sql = "INSERT INTO vt_fee("
                        + " regn_no, payment_mode, fees, fine, rcpt_no, rcpt_dt, pur_cd, "
                        + " flag, collected_by, state_cd, off_cd)"
                        + "  (SELECT regn_no ,instrument_type , ?,?,?,current_timestamp ,?,?,?,?,?  "
                        + "  FROM vp_instrument_cart where regn_no = ? and user_cd =?  limit 1)";

                ps = tmgr.prepareStatement(sql);
                ps.setLong(1, userChg);//fees
                ps.setLong(2, 0);//fine
                ps.setString(3, rcptNo);//rcpt_no
                ps.setInt(4, TableConstants.VM_TRANSACTION_MAST_USER_CHARGES);//   pur_cd
                ps.setNull(5, java.sql.Types.VARCHAR);//   flag
                ps.setString(6, Util.getEmpCode());//   collected_by
                ps.setString(7, Util.getUserStateCode());//   state_cd
                ps.setInt(8, Util.getUserOffCode());//   off_cd
                ps.setString(9, dobj.getRegn_no());//regn_no
                ps.setInt(10, Integer.parseInt(Util.getEmpCode()));//   user_cd
                ps.executeUpdate();
            }

            savetaxDetails(listTaxDobj, rcptNo, dobj, permitDobj, tmgr, inscd);
            rets[1] = applNo;


        } catch (VahanException ex) {
            throw ex;
        } catch (Exception ee) {
            LOGGER.error(ee.toString() + " " + ee.getStackTrace()[0]);
            e = ee;
        }

        if (e != null) {
            throw new VahanException("Receipt Number Generation Failed");
        }
        return rets;
    }

    public boolean savetaxDetails(List<Tax_Pay_Dobj> listTaxDobj, String receiptNo, Owner_dobj ownerDobj, PassengerPermitDetailDobj permitDobj, TransactionManager tmgr, long inscd) throws VahanException {
        boolean isSavedFlag = false;

        try {
            //Generate a receipt No
            // off_cd = rto_cd            
            for (Tax_Pay_Dobj taxDobj : listTaxDobj) {
                if (taxDobj.getTaxMode().equals("0")) {
                    continue;
                }

                if (taxDobj.getFinalTaxAmount() <= 0) {
                    /*
                     * Tax Amount can't be less than equal to Zero and Final Tax Amount can't be less than 0 
                     */
                    if (taxDobj.getTotalPaybaleTax() <= 0 || taxDobj.getFinalTaxAmount() < 0) {
                        throw new VahanException("Tax Amount Less than 0 can't be saved");
                    }
                }
                if (!taxDobj.getTaxMode().equals("O") && !taxDobj.getTaxMode().equals("L")
                        && !taxDobj.getTaxMode().equals("S")) {

                    if (taxDobj.getFinalTaxUpto() == null) {
                        throw new VahanException("Tax Paid Upto Date can't be NULL for Selected Tax Mode");
                    }

                    if (DateUtils.isAfter(DateUtils.getCurrentDate(), taxDobj.getFinalTaxUpto())) {
                        throw new VahanException("Tax Paid Upto Date can't be less than current date");
                    }
                }

                String vtTaxSQL = "INSERT INTO vt_tax("
                        + " regn_no, tax_mode, payment_mode, tax_amt, tax_fine, rcpt_no, rcpt_dt, tax_from, tax_upto, pur_cd, flag, collected_by, state_cd,off_cd)"
                        + "  (SELECT distinct regn_no ,period_mode , ? ,(amount-penalty) ,penalty, ? ,current_timestamp ,"
                        + "   period_from, period_upto,pur_cd, ? ,  ? ,   state_cd, off_cd "
                        + "  FROM vp_instrument_cart where regn_no = ? and pur_cd=?  and user_cd = ? )";

                PreparedStatement pstmtVtTax = tmgr.prepareStatement(vtTaxSQL);
                int i = 1;
                pstmtVtTax.setString(i++, "M");//PAYMENT MODE
                pstmtVtTax.setString(i++, receiptNo);//rcpt_no
                pstmtVtTax.setNull(i++, java.sql.Types.VARCHAR);//   flag
                pstmtVtTax.setString(i++, Util.getEmpCode());//   collected_by
                pstmtVtTax.setString(i++, taxDobj.getRegnNo());//regn_no
                pstmtVtTax.setInt(i++, taxDobj.getPur_cd());//pur_cd
                pstmtVtTax.setInt(i++, Integer.parseInt(Util.getEmpCode()));//   user_cd
                pstmtVtTax.executeUpdate();


                // insert from vp_instrument_breakup 

                String vtTaxBrkupSQL = "INSERT INTO vt_tax_breakup("
                        + " state_cd, off_cd ,rcpt_no, sr_no, tax_from, tax_upto, pur_cd, prv_adjustment, tax, "
                        + " exempted, rebate, surcharge, penalty, interest, tax1, tax2 )"
                        + " (SELECT distinct state_cd, off_cd, ? , sr_no, tax_from, tax_upto, pur_cd, "
                        + "   prv_adjustment, tax, exempted, rebate, surcharge, penalty, interest , tax1 , tax2 "
                        + "  FROM vp_instrument_tax_breakup where regn_no = ?  and user_cd = ? and pur_cd=?  )";

                PreparedStatement pstmtVtTaxBrkUp = tmgr.prepareStatement(vtTaxBrkupSQL);
                pstmtVtTaxBrkUp.setString(1, receiptNo);//rcpt_no
                pstmtVtTaxBrkUp.setString(2, taxDobj.getRegnNo());//regn_no
                pstmtVtTaxBrkUp.setInt(3, Integer.parseInt(Util.getEmpCode()));//   user_cd
                pstmtVtTaxBrkUp.setInt(4, taxDobj.getPur_cd());//pur_cd
                pstmtVtTaxBrkUp.executeUpdate();

                String vphInstrumentCart = "INSERT INTO vph_instrument_cart(\n"
                        + "            state_cd, off_cd, user_cd, regn_no, pur_cd, period_mode, period_from,  "
                        + "            period_upto, amount, exempted, rebate, surcharge, penalty, interest,  "
                        + "            transaction_no, sr_no, instrument_type, instrument_no, instrument_dt,  "
                        + "            instrument_amt, bank_code, branch_name, received_dt, rcpt_no, "
                        + "            rcpt_dt, pmt_type, pmt_catg, service_type, route_class, route_length,  "
                        + "            no_of_trips, domain_cd, distance_run_in_quarter, moved_on, moved_by , instrument_cd ) "
                        + "     (SELECT state_cd, off_cd, user_cd, regn_no, pur_cd, period_mode, period_from,  "
                        + "       period_upto, amount, exempted, rebate, surcharge, penalty, interest,  "
                        + "       transaction_no, sr_no, instrument_type, instrument_no, instrument_dt,  "
                        + "       instrument_amt, bank_code, branch_name, received_dt, ?,  "
                        + "       current_timestamp , pmt_type, pmt_catg, service_type, route_class, route_length,  "
                        + "       no_of_trips, domain_cd, distance_run_in_quarter , current_timestamp , ? , ?  "
                        + "  FROM vp_instrument_cart where regn_no = ? and user_cd = ? and pur_cd=?)";
                PreparedStatement pstmtVphInstrumentCart = tmgr.prepareStatement(vphInstrumentCart);
                pstmtVphInstrumentCart.setString(1, receiptNo);//rcpt_no
                pstmtVphInstrumentCart.setString(2, Util.getEmpCode());//   collected_by
                pstmtVphInstrumentCart.setLong(3, inscd);//regn_no
                pstmtVphInstrumentCart.setString(4, taxDobj.getRegnNo());//regn_no
                pstmtVphInstrumentCart.setInt(5, Integer.parseInt(Util.getEmpCode()));//   collected_by 
                pstmtVphInstrumentCart.setInt(6, taxDobj.getPur_cd());//pur_cd
                pstmtVphInstrumentCart.executeUpdate();

                String deleteBreakupSql = "Delete from vp_instrument_tax_breakup where regn_no = ? and user_cd = ? and pur_cd=?";
                PreparedStatement pstmtVpBrkUp = tmgr.prepareStatement(deleteBreakupSql);
                pstmtVpBrkUp.setString(1, taxDobj.getRegnNo());//regn_no
                pstmtVpBrkUp.setInt(2, Integer.parseInt(Util.getEmpCode()));//   user_cd
                pstmtVpBrkUp.setInt(3, taxDobj.getPur_cd());//pur_cd
                pstmtVpBrkUp.executeUpdate();


                String deleteVpInstrumentSql = " DELETE FROM vp_instrument_cart where regn_no = ?  and user_cd = ? and pur_cd=?";
                PreparedStatement pstmtVpInstrument = tmgr.prepareStatement(deleteVpInstrumentSql);
                pstmtVpInstrument.setString(1, taxDobj.getRegnNo());//regn_no
                pstmtVpInstrument.setInt(2, Integer.parseInt(Util.getEmpCode()));//   user_cd
                pstmtVpInstrument.setInt(3, taxDobj.getPur_cd());//pur_cd
                pstmtVpInstrument.executeUpdate();
                isSavedFlag = true;

            }
            if (isSavedFlag) {
                new TaxServer_Impl().saveTaxBasedInformation(permitDobj, ownerDobj, receiptNo, null, tmgr);
            }

        } catch (VahanException ve) {
            isSavedFlag = false;
            throw ve;
        } catch (Exception e) {
            isSavedFlag = false;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException("Saving Tax Information Failed");
        }

        return isSavedFlag;

    }

    public List<SingleDraftPaymentDobj> getOldReceiptList(Date oldDate) {
        List<SingleDraftPaymentDobj> list = new ArrayList<SingleDraftPaymentDobj>();
        TransactionManager tmgr = null;
        String whereiam = "SingleDraftPaymentImpl.getOldReceiptList()";
        PreparedStatement psmt = null;
        long insCd = 0;
        RowSet rs = null;

        String sql2 = "select distinct instrument_cd from vph_instrument_cart where user_cd = ? and date(moved_on)=date(?) ";
        try {
            tmgr = new TransactionManager(whereiam);
            psmt = tmgr.prepareStatement(sql2);
            psmt.setInt(1, Integer.parseInt(Util.getEmpCode()));
            psmt.setDate(2, new java.sql.Date(oldDate.getTime()));
            rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                SingleDraftPaymentDobj dobj = new SingleDraftPaymentDobj();
                insCd = rs.getLong("instrument_cd");
                dobj.setInscd(insCd);
                list.add(dobj);

            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }

        return list;
    }

    public SingleDraftPaymentDobj getPaymentAndReceiptDetailsAgainstInstrumentCd(long insCd) {
        SingleDraftPaymentDobj dobj = new SingleDraftPaymentDobj();
        TransactionManager tmgr = null;
        String whereiam = "getPaymentAndReceiptDetailsAgainstInstrumentCd()";
        PreparedStatement psmt = null;
        //long insCd = Long.parseLong(instrumentCd);
        RowSet rs = null;

        try {
            tmgr = new TransactionManager(whereiam);
            String sql3 = "select distinct regn_no ,amount , rcpt_no  from vph_instrument_cart "
                    + " where instrument_cd = ? and user_cd = ? ";
            psmt = tmgr.prepareStatement(sql3);
            psmt.setLong(1, insCd);
            psmt.setInt(2, Integer.parseInt(Util.getEmpCode()));
            rs = tmgr.fetchDetachedRowSet();
            List<SingleDraftPaymentDobj> listReceiptList = new ArrayList<SingleDraftPaymentDobj>();
            while (rs.next()) {
                SingleDraftPaymentDobj sibleDraftDobj = new SingleDraftPaymentDobj();
                sibleDraftDobj.setRegn_no(rs.getString("regn_no"));
                sibleDraftDobj.setAmount(rs.getFloat("amount"));
                sibleDraftDobj.setReceiptNo(rs.getString("rcpt_no"));
                listReceiptList.add(sibleDraftDobj);
            }
            dobj.setListSinglePaymentDraftDobj(listReceiptList);


            //setting paymentList

            String sql4 = "select distinct instrument_type , instrument_no , instrument_dt , instrument_amt ,"
                    + " bank_code , branch_name ,to_char( instrument_dt,'dd-Mon-yyyy') as formatDate "
                    + " from vph_instrument_cart "
                    + " where instrument_cd = ? and user_cd = ?  ";
            psmt = tmgr.prepareStatement(sql4);
            psmt.setLong(1, insCd);
            psmt.setInt(2, Integer.parseInt(Util.getEmpCode()));
            rs = tmgr.fetchDetachedRowSet();
            List<PaymentCollectionDobj> listPaymentList = new ArrayList<PaymentCollectionDobj>();
            while (rs.next()) {
                PaymentCollectionDobj singleDraftDobj = new PaymentCollectionDobj();
                singleDraftDobj.setInstrument(rs.getString("instrument_type"));
                singleDraftDobj.setNumber(rs.getString("instrument_no"));
                singleDraftDobj.setDated(rs.getDate("instrument_dt"));
                singleDraftDobj.setAmount(String.valueOf(rs.getLong("instrument_amt")));
                singleDraftDobj.setBank_name(rs.getString("bank_code"));
                singleDraftDobj.setBranch(rs.getString("branch_name"));
                String[][] instrmentType = MasterTableFiller.masterTables.TM_INSTRUMENTS.getData();
                String[][] data = MasterTableFiller.masterTables.TM_BANK.getData();
                for (int i = 0; i < instrmentType.length; i++) {
                    if (instrmentType[i][0].equals(rs.getString("instrument_type"))) {
                        singleDraftDobj.setInstrumentDesc(instrmentType[i][1]);
                    }
                }
                for (int i = 0; i < data.length; i++) {
                    if (data[i][0].equals(rs.getString("bank_code"))) {
                        singleDraftDobj.setBankNameDesc(data[i][1]);
                    }
                }
                singleDraftDobj.setDateInFormat(rs.getString("formatDate"));
                listPaymentList.add(singleDraftDobj);
            }
            dobj.setPaymentlist(listPaymentList);


        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            try {
                tmgr.release();
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
        }
        return dobj;
    }
}
