/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services.threads;

import java.sql.BatchUpdateException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.dobj.EpayDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.impl.SeatAllotedDetails;
import nic.vahan.services.clients.NewFeesAndTaxClient;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Anu
 */
public class ApprovalTaxThread implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ApprovalTaxThread.class);
    private Owner_dobj ownerDobj;
    private String appl_no;
    private String regn_no;
    private int purCD;
    private boolean hypo;
    private String URL;
    private String stateCd;
    private int offCd;
    EpayDobj checkFeeTax;
    private SeatAllotedDetails selectedSeat;
    private String userCatg;
    private String empCode;

    public ApprovalTaxThread(Owner_dobj ownerDobj, String appl_no, String regn_no, int purCD, boolean hypo,
            EpayDobj checkFeeTax, String URL, String stateCd, int offCd, SeatAllotedDetails selectedSeat, String userCatg, String empCode) {
        System.out.println("m in const");
        this.ownerDobj = ownerDobj;
        this.appl_no = appl_no;
        this.regn_no = regn_no;
        this.purCD = purCD;
        this.hypo = hypo;
        this.checkFeeTax = checkFeeTax;
        this.URL = URL;
        this.stateCd = stateCd;
        this.offCd = offCd;
        this.selectedSeat = selectedSeat;
        this.userCatg = userCatg;
        this.empCode = empCode;
    }

    @Override
    public void run() {
        TransactionManager tmgr = null;
        EpayDobj retDobj = new EpayDobj();
        NewFeesAndTaxClient cl = null;
        String param = "";
        try {
            //     System.out.println("m in thread");

            //////////////////////////////////////////////////////////////////////////////////
            // FeeTaxDobj feeTaxDobj = null;
            //FeeTaxInputDobj feeTaxInput = null;
            //NewFeesAndTaxClient feeAndTaxClient = null;
            List<EpayDobj> mandatoryFeeListService = null;
            List<EpayDobj> mandatoryFeeList = checkFeeTax.getList();
            // List<TaxFormPanelBean> listTaxFormService = new ArrayList();
            try {

                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//            Owner_dobj dobj = OwnerImpl.getNewVehicleApplNo("RJ17121200082318");
                ownerDobj.setAppl_no(appl_no);
                ownerDobj.setRegn_no(regn_no);
                ownerDobj.setPurCD(purCD);
                ownerDobj.setHypo(hypo);
                ownerDobj.setActionCd(selectedSeat.getAction_cd());
                ownerDobj.setUserCatg(userCatg);
                ownerDobj.setEmpCode(empCode);
                // ownerDobj.setState_cd("RJ");
                cl = new NewFeesAndTaxClient("At Approval", URL);
                param = mapper.writeValueAsString(ownerDobj);
                //commented
                //String rt = cl.NewVehicleDobj_JSON_STRING(String.class, param);
                //added---------------
                String rt = cl.newFeesRecalculation_JSON_STRING(String.class, param);
                //----------------
                retDobj = mapper.readValue(rt, EpayDobj.class);
                mandatoryFeeListService = retDobj.getList();
                boolean notmatched = false;
                if ((mandatoryFeeListService != null && mandatoryFeeList == null) || (mandatoryFeeListService == null && mandatoryFeeList != null)) {
                    notmatched = true;
                    LOGGER.info("TAX WS: Approval Either service or Vahan4 List is Empty: " + param);
                } else if (mandatoryFeeListService.size() != mandatoryFeeList.size()) {
                    notmatched = true;
                    LOGGER.info("TAX WS: Approval Length Not Matched: " + param);
                } else {
                    for (EpayDobj ep : mandatoryFeeList) {
                        if (mandatoryFeeListService.indexOf(ep) >= 0) {
                            EpayDobj epService = mandatoryFeeListService.get(mandatoryFeeListService.indexOf(ep));
                            if ((ep.getE_TaxFee() != epService.getE_TaxFee())
                                    || (ep.getE_FinePenalty() != epService.getE_FinePenalty())) {
                                notmatched = true;
                                LOGGER.info("TAX WS: Approval Fee or Fine Value not matched : pur_cd " + ep.getPurCd() + " param : " + param);
                                break;
                            }
                        } else {
                            notmatched = true;
                            LOGGER.info("TAX WS: Approval purpose code in vt_fee/Tax not found in service : " + param);
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
                    if (mandatoryFeeListService != null && !mandatoryFeeListService.isEmpty()) {
                        tmgr = new TransactionManager("thread");
                        String vtFeeSQL = "INSERT INTO vt_fee_service("
                                + " regn_no, payment_mode, fees, fine, rcpt_no, rcpt_dt, pur_cd, "
                                + " flag, collected_by, state_cd, off_cd)"
                                + " VALUES (?, ?, ?, ?, ?, current_timestamp, ?, "
                                + " ?, ?, ?, ?)";
                        long lngTotalAmt = 0;
                        PreparedStatement pstmtVtFee = null;
                        pstmtVtFee = tmgr.prepareStatement(vtFeeSQL);

                        for (EpayDobj feePurDobj : mandatoryFeeListService) {
                            EpayDobj feeDobj = null;
                            String rcp = appl_no;
                            String empCode = "123";
                            if (mandatoryFeeList.indexOf(feePurDobj) >= 0) {
                                feeDobj = mandatoryFeeList.get(mandatoryFeeList.indexOf(feePurDobj));
                                rcp = feeDobj.getRcpt_no();
                            }

                            pstmtVtFee.setString(1, regn_no);//regn_no
                            pstmtVtFee.setString(2, "A");//payment_mode
                            pstmtVtFee.setLong(3, feePurDobj.getAct_TaxFee());//fees
                            pstmtVtFee.setLong(4, feePurDobj.getAct_FinePenalty());//fine
                            lngTotalAmt = lngTotalAmt + (feePurDobj.getE_TaxFee() + feePurDobj.getE_FinePenalty());
                            pstmtVtFee.setString(5, rcp);//rcpt_no // 
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

                        tmgr.commit();
                    }
                }


            } catch (BatchUpdateException bue) {
                LOGGER.error(bue.toString() + " Param " + param + " " + bue.getStackTrace()[0]);
                LOGGER.error(bue.toString() + " " + bue.getNextException());

            } catch (Exception ex) {
                //System.out.println("m in catch " + ex.getStackTrace()[0]);
                LOGGER.error(ex.toString() + param + " " + ex.getStackTrace()[0]);

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
