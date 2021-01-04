/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.fee;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.PaymentCollectionBean;
import nic.vahan.form.bean.ReceiptMasterBean;
import nic.vahan.form.bean.common.FileMovementAbstractApplBean;
import nic.vahan.form.dobj.FeeDraftDobj;
import nic.vahan.form.dobj.fee.FeeAgentRegnDobj;
import nic.vahan.form.impl.agentlicence.AgentDetailImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "feeAgentRegn")
@ViewScoped
public class FeeAgentRegnBean extends FileMovementAbstractApplBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(FeeAgentRegnBean.class);
    private List<SelectItem> listFeeType;
    private FeeAgentRegnDobj dobj;
    private int feetype;
    @ManagedProperty(value = "#{rcpt_bean}")
    private ReceiptMasterBean rcpt_bean;
    AgentDetailImpl impl = new AgentDetailImpl();
    private String generatedRcpt = null;
    private PaymentCollectionBean paymentBean = new PaymentCollectionBean();
    private Long totalDraftAmount;
    private Long totalAmount;
    private Long excessAmount;
    private String appl_no;
    private SessionVariables sessionvariable = new SessionVariables();
    private String feeremark = "";

    public FeeAgentRegnBean() {
        try {
            listFeeType = new ArrayList();
            listFeeType = impl.purCdList();
            appl_no = sessionvariable.getSelectedWork().getAppl_no();
            if (!CommonUtils.isNullOrBlank(appl_no)) {
                dobj = new FeeAgentRegnDobj();
                dobj = impl.agentDetails(appl_no);
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), null));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, null));
        }
    }

    public void feeDetailsListener(AjaxBehaviorEvent event) {
        try {
            int purCd = feetype;
            if (purCd != -1) {
                dobj = impl.getFeeData(dobj, purCd);
            }
        } catch (VahanException ve) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), null));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, null));
        }
    }

    public void agentDetails() {
        try {
            if (dobj != null) {
                boolean allGood = false;
                if (Utility.isNullOrBlank(getPaymentBean().getPayment_mode())
                        || "-1".equalsIgnoreCase(getPaymentBean().getPayment_mode())) {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Please select payment mode", "Please select payment mode");
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    allGood = true;
                    return;
                }
                setTotalDraftAmount(0l);
                if (!getPaymentBean().getPayment_mode().equals("C")) {
                    for (nic.vahan.form.dobj.PaymentCollectionDobj payDobj : getPaymentBean().getPaymentlist()) {
                        if (!(payDobj.validateDobj())) {
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, payDobj.getValidationMessage(), payDobj.getValidationMessage());
                            FacesContext.getCurrentInstance().addMessage(null, message);
                            allGood = true;
                            break;
                        } else {
                            setTotalDraftAmount((Long) getTotalDraftAmount() + Long.parseLong(payDobj.getAmount()));
                        }
                    }
                    if (dobj.getGrandTotal() < getTotalDraftAmount()) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Excess Total Instrument Amount (Rs." + (getTotalDraftAmount() - dobj.getGrandTotal()) + "/- must be adjusted in Installment fine amount"));
                        return;
                    }
                }
                if (!allGood) {
                    if (getTotalDraftAmount() <= dobj.getGrandTotal()) {
                        setTotalAmount(dobj.getGrandTotal() - getTotalDraftAmount());
                    } else {
                        setTotalAmount((Long) 0l);
                        setExcessAmount(getTotalDraftAmount() - dobj.getGrandTotal());
                    }
                    if (dobj.getGrandTotal() == 0) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Total Fee Amount is Zero, select atleast One Fee."));
                        return;
                    }
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().ajax().update("form_registered_vehicle_fee:popup");
                    PrimeFaces.current().executeScript("PF('confDlgFee').show()");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public String saveFeeDetails() {
        try {
            FeeAgentRegnDobj feePayDobj = new FeeAgentRegnDobj();
            FeeDraftDobj feeDraftDobj = null;
            feePayDobj.setLicence_No(appl_details.getRegn_no());
            feePayDobj.setAppl_no(dobj.getAppl_no());
            feePayDobj.setRcptDt(new java.util.Date());
            feePayDobj.setPaymentMode(getPaymentBean().getPayment_mode());
            feePayDobj.setStateCD(dobj.getStateCD());
            feePayDobj.setGrandTotal(dobj.getGrandTotal());
            feePayDobj.setOwnName(dobj.getOwnName());
            feePayDobj.setMobileNo(dobj.getMobileNo());
            feePayDobj.setPurCd(dobj.getPurCd());
            if (!Utility.isNullOrBlank(getPaymentBean().getPayment_mode())
                    && (!getPaymentBean().getPayment_mode().equals("-1"))
                    && (!getPaymentBean().getPayment_mode().equals("C"))) {
                feeDraftDobj = new FeeDraftDobj();
                String pay_mode = getPaymentBean().getPayment_mode();
                feeDraftDobj.setAppl_no(dobj.getAppl_no());
                feeDraftDobj.setFlag("A");
                feeDraftDobj.setCollected_by(Util.getEmpCode());
                feeDraftDobj.setState_cd(dobj.getStateCD());
                feeDraftDobj.setOff_cd(String.valueOf(Util.getUserOffCode()));
                feeDraftDobj.setDraftPaymentList(getPaymentBean().getPaymentlist());
            }
            String[] applNo = impl.saveFeeDetailsInstrument(feePayDobj, feeDraftDobj, getFeeremark());
            if (applNo == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Fee Can't be Submitted because One of the Transaction is Pending on Data Entry Level.", "Fee Can't be Submitted because One of the Transaction is Pending on Data Entry Level."));
                return null;
            }
            generatedRcpt = applNo[1];
            if (!Utility.isNullOrBlank(generatedRcpt)) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Successful Transaction!", "Receipt Number: " + applNo[1]));
                reset();
                Util.getSession().removeAttribute("seat_map");
                // RequestContext rc = RequestContext.getCurrentInstance();
                PrimeFaces.current().ajax().update("form_registered_vehicle_fee:pg_button");
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("rcptno", generatedRcpt);
                return "PrintCashReceiptReport";
            }
        } catch (VahanException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, null));
        }
        return "";
    }

    public void reset() {
        dobj.setOwnName("");
        dobj.setFname("");
        dobj.setMobileNo("");
        dobj.setFees(0);
        dobj.setGrandTotal(0);
        dobj.setRecpeNo("");
    }

    public FeeAgentRegnDobj getDobj() {
        return dobj;
    }

    public void setDobj(FeeAgentRegnDobj dobj) {
        this.dobj = dobj;
    }

    public List getListFeeType() {
        return listFeeType;
    }

    public void setListFeeType(List listFeeType) {
        this.listFeeType = listFeeType;
    }

    public int getFeetype() {
        return feetype;
    }

    public void setFeetype(int feetype) {
        this.feetype = feetype;
    }

    public String getGeneratedRcpt() {
        return generatedRcpt;
    }

    public void setGeneratedRcpt(String generatedRcpt) {
        this.generatedRcpt = generatedRcpt;
    }

    public ReceiptMasterBean getRcpt_bean() {
        return rcpt_bean;
    }

    public void setRcpt_bean(ReceiptMasterBean rcpt_bean) {
        this.rcpt_bean = rcpt_bean;
    }

    public PaymentCollectionBean getPaymentBean() {
        return paymentBean;
    }

    public void setPaymentBean(PaymentCollectionBean paymentBean) {
        this.paymentBean = paymentBean;
    }

    public Long getTotalDraftAmount() {
        return totalDraftAmount;
    }

    public void setTotalDraftAmount(Long totalDraftAmount) {
        this.totalDraftAmount = totalDraftAmount;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getExcessAmount() {
        return excessAmount;
    }

    public void setExcessAmount(Long excessAmount) {
        this.excessAmount = excessAmount;
    }

    public String getFeeremark() {
        return feeremark;
    }

    public void setFeeremark(String feeremark) {
        this.feeremark = feeremark;
    }
}
