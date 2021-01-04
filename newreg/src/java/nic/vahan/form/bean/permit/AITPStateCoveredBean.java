/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.java.util.DateUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.form.dobj.permit.AITPStateCoveredDobj;
import nic.vahan.form.dobj.permit.AITPStateFeeDraftDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author R Gautam
 */
@ManagedBean(name = "StateCovered")
@ViewScoped
public class AITPStateCoveredBean implements Serializable {

    private AITPStateCoveredDobj statecoveredBeanObj = null;
    private List<AITPStateCoveredDobj> stateList = new ArrayList<>();
    private List stateMasterData = new ArrayList();
    private List<AITPStateFeeDraftDobj> paymentList = new ArrayList<>();
    private String header_name;
    private boolean render_payment_table_aitp = false;
    private List bank_list = new ArrayList();
    private List instrumentList = new ArrayList();
    Map<String, String> config = null;
    private static final Logger LOGGER = Logger.getLogger(AITPStateCoveredBean.class);

    public AITPStateCoveredBean() {

        config = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
        render_payment_table_aitp = Boolean.parseBoolean(config.get("render_payment_table_aitp").toString());

        String[][] data = MasterTableFiller.masterTables.TM_STATE.getData();
        for (int i = 0; i < data.length; i++) {
            stateMasterData.add(new SelectItem(data[i][0], data[i][1]));
        }

        data = MasterTableFiller.masterTables.TM_BANK.getData();
        bank_list.add(new SelectItem("-1", "Select Bank"));

        for (int i = 0; i < data.length; i++) {
            bank_list.add(new SelectItem(data[i][0], data[i][1]));
        }

        String[][] instrmentType = MasterTableFiller.masterTables.TM_INSTRUMENTS.getData();

        instrumentList = new ArrayList();
        instrumentList.add(new SelectItem("-1", "Select"));
        for (int i = 0; i < instrmentType.length; i++) {
            if (instrmentType[i][0].equals("D") || instrmentType[i][0].equals("L")) {
                instrumentList.add(new SelectItem(instrmentType[i][0], instrmentType[i][1]));
            }
        }
    }

    @PostConstruct
    public void init() {
        statecoveredBeanObj = new AITPStateCoveredDobj();
    }

    public void createNew() {
        if (stateList.contains(statecoveredBeanObj)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Dublicated", "This State has already been added"));
        } else {
            String[][] data = MasterTableFiller.masterTables.TM_STATE.getData();
            for (int i = 0; i < data.length; i++) {
                if (statecoveredBeanObj.getStateCd().equalsIgnoreCase(data[i][0])) {
                    statecoveredBeanObj.setStateName(data[i][1]);
                    break;
                }
            }
            stateList.add(statecoveredBeanObj);
            try {
                if (isRender_payment_table_aitp()) {
                    AITPStateFeeDraftDobj aITPStateFeeDraftDobj = new AITPStateFeeDraftDobj();
                    aITPStateFeeDraftDobj.setPay_state_cd(statecoveredBeanObj.getStateCd());
                    aITPStateFeeDraftDobj.setPay_state_descr(statecoveredBeanObj.getStateName());
                    aITPStateFeeDraftDobj.setMax_draft_date(new Date());
                    aITPStateFeeDraftDobj.setMin_draft_date(DateUtils.addToDate(aITPStateFeeDraftDobj.getMax_draft_date(), 2, -3));

                    paymentList.add(aITPStateFeeDraftDobj);
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            }
            statecoveredBeanObj = new AITPStateCoveredDobj();
        }
    }

    public void validateInstrumentNumber(AjaxBehaviorEvent event) {

        AITPStateFeeDraftDobj selectedDobj = (AITPStateFeeDraftDobj) event.getComponent().getAttributes().get("paymentDobj");
        String error_message = null;

        if (selectedDobj.getInstrument_type() == null || selectedDobj.getInstrument_type().equals("-1")) {
            error_message = "Please Select Instrument Type";
        }

        if (selectedDobj.getBank_code() == null || selectedDobj.getBank_code().equals("-1")) {
            error_message = "Please Select Bank Name";
        }

        if (selectedDobj.getInstrument_amt() == 0) {
            error_message = "Please enter amount";
        }

        String dupInstrument = null;
        if (dupInstrument != null) {
            error_message = dupInstrument;
        }

        int commonInstrumentsNo = 0;
        for (AITPStateFeeDraftDobj dobj : getPaymentList()) {
            if (dobj.getInstrument_no() != null && selectedDobj.getInstrument_no() != null && dobj.getInstrument_no().equals(selectedDobj.getInstrument_no())
                    && dobj.getBank_code() != null && selectedDobj.getInstrument_no() != null && dobj.getBank_code().equals(dobj.getBank_code())
                    && dobj.getInstrument_no() != null && selectedDobj.getInstrument_no() != null && dobj.getInstrument_no().equals(selectedDobj.getInstrument_no())) {

                commonInstrumentsNo++;
            }
        }

        if (commonInstrumentsNo > 1) {
            error_message = "Duplicate Instrument Number";
        }

        if (error_message != null) {
            //RequestContext rc = RequestContext.getCurrentInstance();
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", error_message));
            selectedDobj.setInstrument_no("");
        }


    }

    public void remove(String stateCd, String stateName) {
        statecoveredBeanObj = new AITPStateCoveredDobj();
        statecoveredBeanObj.setStateCd(stateCd);
        statecoveredBeanObj.setStateName(stateName);
        if (stateList.contains(statecoveredBeanObj)) {
            stateList.remove(statecoveredBeanObj);
        }

        if (isRender_payment_table_aitp()) {
            Iterator itr = paymentList.iterator();
            while (itr.hasNext()) {

                AITPStateFeeDraftDobj aITPStateFeeDraftDobj = (AITPStateFeeDraftDobj) itr.next();
                if (aITPStateFeeDraftDobj.getPay_state_cd().equalsIgnoreCase(stateCd)) {
                    itr.remove();
                }
            }
        }
    }

    public List<AITPStateCoveredDobj> getStateList() {
        return stateList;
    }

    public AITPStateCoveredDobj getStatecoveredBeanObj() {
        return statecoveredBeanObj;
    }

    public List getStateMasterData() {
        return stateMasterData;
    }

    public void setStateMasterData(List stateMasterData) {
        this.stateMasterData = stateMasterData;
    }

    public void setStateList(List<AITPStateCoveredDobj> stateList) {
        this.stateList = stateList;
    }

    public String getHeader_name() {
        return header_name;
    }

    public void setHeader_name(String header_name) {
        this.header_name = header_name;
    }

    public List<AITPStateFeeDraftDobj> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<AITPStateFeeDraftDobj> paymentList) {
        this.paymentList = paymentList;
    }

    public List getBank_list() {
        return bank_list;
    }

    public void setBank_list(List bank_list) {
        this.bank_list = bank_list;
    }

    public List getInstrumentList() {
        return instrumentList;
    }

    public void setInstrumentList(List instrumentList) {
        this.instrumentList = instrumentList;
    }

    public boolean isRender_payment_table_aitp() {
        return render_payment_table_aitp;
    }

    public void setRender_payment_table_aitp(boolean render_payment_table_aitp) {
        this.render_payment_table_aitp = render_payment_table_aitp;
    }
}
