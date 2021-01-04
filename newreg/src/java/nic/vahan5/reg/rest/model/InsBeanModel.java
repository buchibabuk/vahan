/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.ComparisonBean;
import nic.vahan.form.bean.InsBean;
import nic.vahan.form.dobj.InsDobj;
import static nic.vahan.server.ServerUtil.Compare;
import nic.vahan5.reg.server.ServerUtility;
import org.apache.log4j.Logger;

/**
 *
 * @author BITTU
 */
public class InsBeanModel implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(InsBeanModel.class);
    private int compCode;
    private int insType;
    private Date ins_from;
    private Date ins_upto;
    private Date max_dt;
    private Date min_dt;
    private String policyNo;
    private List list_comp_cd;
    private List list_ins_type;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private InsDobj ins_dobj_prv;
    private String appl_no;
    private String regn_no;
    private int pur_cd;
    private boolean disable;
    private boolean render_ins_update;
    private boolean eflag;
    private boolean blockInsPanel;
    private boolean govtVehFlag;
    private boolean disableInsType;
    private long idv;
    private boolean insUptoDisable;
    private boolean addminusInsbtn;
    private int selectedYear;
    private boolean iibData;
    private String insuranceFromServiceMessage;
    private String insuranceFromVahanMessage;

    public InsBeanModel() {
    }

    public InsBeanModel(InsBean insBean) {
        this.compCode = insBean.getCompCode();
        this.insType = insBean.getInsType();
        this.ins_from = insBean.getIns_from();
        this.ins_upto = insBean.getIns_upto();
        this.max_dt = insBean.getMax_dt();
        this.min_dt = insBean.getMin_dt();
        this.policyNo = insBean.getPolicyNo();
//        this.list_comp_cd = insBean.getList_comp_cd();
//        this.list_ins_type = insBean.getList_ins_type();
        this.ins_dobj_prv = insBean.getIns_dobj_prv();
        this.appl_no = insBean.getAppl_no();
        this.regn_no = insBean.getRegn_no();
        this.pur_cd = insBean.getPur_cd();
        this.disable = insBean.isDisable();
        this.render_ins_update = insBean.isRender_ins_update();
        this.eflag = insBean.isEflag();
        this.blockInsPanel = insBean.isBlockInsPanel();
        this.govtVehFlag = insBean.isGovtVehFlag();
        this.disableInsType = insBean.isDisableInsType();
        this.idv = insBean.getIdv();
        this.insUptoDisable = insBean.isInsUptoDisable();
        this.addminusInsbtn = insBean.isAddminusInsbtn();
        this.selectedYear = insBean.getSelectedYear();
        this.iibData = insBean.isIibData();
        this.insuranceFromServiceMessage = insBean.getInsuranceFromServiceMessage();
        this.insuranceFromVahanMessage = insBean.getInsuranceFromVahanMessage();
    }

    public void populateBeanFromModel(InsBean insBean) {
        insBean.setCompCode(this.compCode);
        insBean.setInsType(this.insType);
        insBean.setIns_from(this.ins_from);
        insBean.setIns_upto(this.ins_upto);
        insBean.setMax_dt(this.max_dt);
        insBean.setMin_dt(this.min_dt);
        insBean.setPolicyNo(this.policyNo);
//        insBean.setList_comp_cd(this.list_comp_cd);
//        insBean.setList_ins_type(this.list_ins_type);
        insBean.setIns_dobj_prv(this.ins_dobj_prv);
        insBean.setAppl_no(this.appl_no);
        insBean.setRegn_no(this.regn_no);
        insBean.setPur_cd(this.pur_cd);
        insBean.setDisable(this.disable);
        insBean.setRender_ins_update(this.render_ins_update);
        insBean.setEflag(this.eflag);
        insBean.setBlockInsPanel(this.blockInsPanel);
        insBean.setGovtVehFlag(this.govtVehFlag);
        insBean.setDisableInsType(this.disableInsType);
        insBean.setIdv(this.idv);
        insBean.setInsUptoDisable(this.insUptoDisable);
        insBean.setAddminusInsbtn(this.addminusInsbtn);
        insBean.setSelectedYear(this.selectedYear);
        insBean.setIibData(this.iibData);
        insBean.setInsuranceFromServiceMessage(this.insuranceFromServiceMessage);
        insBean.setInsuranceFromVahanMessage(this.insuranceFromVahanMessage);
    }

    public InsDobj set_InsBean_to_dobj() {

        InsDobj dobj = new InsDobj();
        //setting the value from form_insurance to dobj
        dobj.setIns_from(this.ins_from);
        dobj.setIns_upto(this.ins_upto);
        dobj.setComp_cd(this.compCode);
        dobj.setIns_type(this.insType);
        dobj.setPolicy_no(this.policyNo);
        dobj.setIdv(this.getIdv());
        dobj.setInsPeriodYears(this.getSelectedYear());
        dobj.setIibData(this.iibData);
        return dobj;
    }

    public List<ComparisonBean> compareChagnes() throws VahanException {

        InsDobj dobj = getIns_dobj_prv();  //getting the dobj from workbench

        if (dobj == null) {
            return compBeanList;
        }
        compBeanList.clear();

        Compare("Ins_Type", dobj.getIns_type(), this.insType, (ArrayList) compBeanList);
        Compare("Ins_comp", dobj.getComp_cd(), this.compCode, (ArrayList) compBeanList);
        Compare("Policy_no", dobj.getPolicy_no(), this.policyNo, (ArrayList) compBeanList);
        Compare("Ins_from_dt", dobj.getIns_from(), this.ins_from, (ArrayList) compBeanList);
        Compare("Ins_upto_dt", dobj.getIns_upto(), this.ins_upto, (ArrayList) compBeanList);
        Compare("Ins_declared_value", dobj.getIdv(), this.getIdv(), (ArrayList) compBeanList);
        return compBeanList;

    }

    public void set_Ins_dobj_to_bean(InsDobj ins_dobj) {

        if (ins_dobj == null) {
            JSFUtils.setFacesMessage("Insurance Details are not available !!!", null, JSFUtils.INFO);
            return;
        }
        this.compCode = ins_dobj.getComp_cd();
        this.insType = ins_dobj.getIns_type();
        this.ins_from = ins_dobj.getIns_from();
        this.ins_upto = ins_dobj.getIns_upto();
        this.policyNo = ins_dobj.getPolicy_no();
        this.iibData = ins_dobj.isIibData();
        this.setIdv(ins_dobj.getIdv());
        if (ins_dobj.getIns_type() == Integer.parseInt(TableConstants.INS_TYPE_NA)) {
            blockInsPanel = true;
            disableInsType = true;
            insUptoDisable = true;
        }
        if (ins_dobj.getIns_type() == Integer.parseInt(TableConstants.INS_TYPE_NA)) {
            this.govtVehFlag = true;
        }
        try {
            if (ins_from != null && ins_upto != null) {
                selectedYear = DateUtils.getDate1MinusDate2_Months(ins_from, ins_upto) / 12;
            }
            if (ins_from != null && ins_from.before(min_dt)) {
                this.min_dt = ServerUtility.dateRange(ins_from, 0, 0, 0);
            }
        } catch (Exception e) {
        }

    }

    public void insTypeListener() {
        if (insType != 0 && insType == Integer.parseInt(TableConstants.INS_TYPE_NA)) {
            setCompCode(9999);
            policyNo = "NA";
            ins_from = null;
            ins_upto = null;
            setBlockInsPanel(true);
            govtVehFlag = true;
            insUptoDisable = true;
            setIdv(0);
        } else {
            setBlockInsPanel(false);
            govtVehFlag = false;
            insUptoDisable = false;
            //policyNo = "";
            //setCompCode(-1);
        }
    }

    public void componentReadOnly(boolean flag) {
        boolean flag1;
        flag1 = !flag;
        setDisable(flag1);
        setGovtVehFlag(flag1);
    }

    public int getCompCode() {
        return compCode;
    }

    public void setCompCode(int compCode) {
        this.compCode = compCode;
    }

    public int getInsType() {
        return insType;
    }

    public void setInsType(int insType) {
        this.insType = insType;
    }

    public Date getIns_from() {
        return ins_from;
    }

    public void setIns_from(Date ins_from) {
        this.ins_from = ins_from;
    }

    public Date getIns_upto() {
        return ins_upto;
    }

    public void setIns_upto(Date ins_upto) {
        this.ins_upto = ins_upto;
    }

    public Date getMax_dt() {
        return max_dt;
    }

    public void setMax_dt(Date max_dt) {
        this.max_dt = max_dt;
    }

    public Date getMin_dt() {
        return min_dt;
    }

    public void setMin_dt(Date min_dt) {
        this.min_dt = min_dt;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public List getList_comp_cd() {
        return list_comp_cd;
    }

    public void setList_comp_cd(List list_comp_cd) {
        this.list_comp_cd = list_comp_cd;
    }

    public List getList_ins_type() {
        return list_ins_type;
    }

    public void setList_ins_type(List list_ins_type) {
        this.list_ins_type = list_ins_type;
    }

    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    public InsDobj getIns_dobj_prv() {
        return ins_dobj_prv;
    }

    public void setIns_dobj_prv(InsDobj ins_dobj_prv) {
        this.ins_dobj_prv = ins_dobj_prv;
    }

    public String getAppl_no() {
        return appl_no;
    }

    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public int getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public boolean isRender_ins_update() {
        return render_ins_update;
    }

    public void setRender_ins_update(boolean render_ins_update) {
        this.render_ins_update = render_ins_update;
    }

    public boolean isEflag() {
        return eflag;
    }

    public void setEflag(boolean eflag) {
        this.eflag = eflag;
    }

    public boolean isBlockInsPanel() {
        return blockInsPanel;
    }

    public void setBlockInsPanel(boolean blockInsPanel) {
        this.blockInsPanel = blockInsPanel;
    }

    public boolean isGovtVehFlag() {
        return govtVehFlag;
    }

    public void setGovtVehFlag(boolean govtVehFlag) {
        this.govtVehFlag = govtVehFlag;
    }

    public boolean isDisableInsType() {
        return disableInsType;
    }

    public void setDisableInsType(boolean disableInsType) {
        this.disableInsType = disableInsType;
    }

    public long getIdv() {
        return idv;
    }

    public void setIdv(long idv) {
        this.idv = idv;
    }

    public boolean isInsUptoDisable() {
        return insUptoDisable;
    }

    public void setInsUptoDisable(boolean insUptoDisable) {
        this.insUptoDisable = insUptoDisable;
    }

    public boolean isAddminusInsbtn() {
        return addminusInsbtn;
    }

    public void setAddminusInsbtn(boolean addminusInsbtn) {
        this.addminusInsbtn = addminusInsbtn;
    }

    public int getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(int selectedYear) {
        this.selectedYear = selectedYear;
    }

    public boolean isIibData() {
        return iibData;
    }

    public void setIibData(boolean iibData) {
        this.iibData = iibData;
    }

    public String getInsuranceFromServiceMessage() {
        return insuranceFromServiceMessage;
    }

    public void setInsuranceFromServiceMessage(String insuranceFromServiceMessage) {
        this.insuranceFromServiceMessage = insuranceFromServiceMessage;
    }

    public String getInsuranceFromVahanMessage() {
        return insuranceFromVahanMessage;
    }

    public void setInsuranceFromVahanMessage(String insuranceFromVahanMessage) {
        this.insuranceFromVahanMessage = insuranceFromVahanMessage;
    }

    @Override
    public String toString() {
        return "InsBeanModel{" + "compCode=" + compCode + ", insType=" + insType + ", ins_from=" + ins_from + ", ins_upto=" + ins_upto + ", max_dt=" + max_dt + ", min_dt=" + min_dt + ", policyNo=" + policyNo + ", list_comp_cd=" + list_comp_cd + ", list_ins_type=" + list_ins_type + ", compBeanList=" + compBeanList + ", ins_dobj_prv=" + ins_dobj_prv + ", appl_no=" + appl_no + ", regn_no=" + regn_no + ", pur_cd=" + pur_cd + ", disable=" + disable + ", render_ins_update=" + render_ins_update + ", eflag=" + eflag + ", blockInsPanel=" + blockInsPanel + ", govtVehFlag=" + govtVehFlag + ", disableInsType=" + disableInsType + ", idv=" + idv + ", insUptoDisable=" + insUptoDisable + ", addminusInsbtn=" + addminusInsbtn + ", selectedYear=" + selectedYear + '}';
    }
}
