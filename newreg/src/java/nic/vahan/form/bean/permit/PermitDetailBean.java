/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.form.dobj.permit.PassengerPermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitDetailDobj;
import nic.vahan.form.dobj.permit.PermitHomeAuthDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "Permit_Dtls_bean")
@ViewScoped
public class PermitDetailBean implements Serializable {

    private String regn_no;
    private String state_cd;
    private int off_cd;
    private String pmt_no;
    private String issue_dt;
    private String valid_from;
    private String valid_upto;
    private String replace_dt;
    private String rcpt_no;
    private String pur_cd;
    private String pmt_type_desc;
    private String pmt_catg_desc;
    private String pmt_Services_desc;
    private String sel_pmt_type;
    private String sel_pmt_catg;
    private String sel_pmt_ser;
    private List pmt_type_array = new ArrayList();
    private List pmt_catg_array = new ArrayList();
    private List ser_type_array = new ArrayList();
    boolean readOnlyComponent = false;
    private String np_regn_no;
    private String np_auth_from;
    private String np_auth_upto;
    private String np_auth_no;
    private String np_permit_no;

    public PermitDetailBean() {
        String[][] data = MasterTableFiller.masterTables.vm_service_type.getData();
        for (int i = 0; i < data.length; i++) {
            ser_type_array.add(new SelectItem(data[i][0], data[i][1]));
        }

        data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
        for (int i = 0; i < data.length; i++) {
            pmt_type_array.add(new SelectItem(data[i][0], data[i][1]));
        }



    }

    public void set_Permit_Dtls_dobj_to_bean(PermitDetailDobj dobj) {
        if (dobj == null) {
            return;
        }
        String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
        SimpleDateFormat dt1 = new SimpleDateFormat("dd-MMM-yyyy");
        this.setRegn_no(dobj.getRegn_no());
        this.setIssue_dt(dt1.format(dobj.getIssue_dt()));
        this.setPmt_Services_desc(dobj.getPmt_Services_desc());
        this.setPmt_catg_desc(dobj.getPmt_catg_desc());
        this.setPmt_no(dobj.getPmt_no());
        this.setPmt_type_desc(dobj.getPmt_type_desc());
        this.setRcpt_no(dobj.getRcpt_no());
        this.setValid_from(dt1.format(dobj.getValid_from()));
        this.setValid_upto(dt1.format(dobj.getValid_upto()));
        //manoj
        if (null != dobj.getReplaceDt()) {
            this.setReplace_dt(dt1.format(dobj.getReplaceDt()));
        }
        this.setSel_pmt_type(String.valueOf(dobj.getPmt_type()));
        this.setSel_pmt_ser(String.valueOf(dobj.getService_type()));
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].equalsIgnoreCase(Util.getUserStateCode())
                    && Integer.parseInt(data[i][3]) == dobj.getPmt_type()
                    && Integer.parseInt(data[i][1]) == dobj.getPmt_catg()) {
                pmt_catg_array.add(new SelectItem(data[i][1], data[i][2]));
            }
        }
        this.setSel_pmt_catg(String.valueOf(dobj.getPmt_catg()));
    }

    public PassengerPermitDetailDobj setPmtDobj(PermitDetailDobj dobj) {
        PassengerPermitDetailDobj passDobj = new PassengerPermitDetailDobj();
        if (dobj == null) {
            return null;
        }
        String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
        passDobj.setRegnNo(dobj.getRegn_no());
        passDobj.setPmt_no(dobj.getPmt_no());
        passDobj.setValid_from(dobj.getValid_from());
        passDobj.setValid_upto(dobj.getValid_upto());
        //manoj
        passDobj.setReplaceDate(dobj.getReplaceDt());
        passDobj.setPmt_type_code(String.valueOf(dobj.getPmt_type()));
        passDobj.setServices_TYPE(String.valueOf(dobj.getService_type()));
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].equalsIgnoreCase(Util.getUserStateCode())
                    && Integer.parseInt(data[i][3]) == dobj.getPmt_type()
                    && Integer.parseInt(data[i][1]) == dobj.getPmt_catg()) {
                pmt_catg_array.add(new SelectItem(data[i][1], data[i][2]));
            }
        }
        passDobj.setPmtCatg(String.valueOf(dobj.getPmt_catg()));
        return passDobj;
    }

    public void permit_ResetValue() {
        this.setRegn_no("");
        this.setIssue_dt(null);
        this.setPmt_Services_desc("");
        this.setPmt_catg_desc("");
        this.setPmt_no("");
        this.setPmt_type_desc("");
        this.setRcpt_no("");
        this.setValid_from(null);
        this.setValid_upto(null);
        this.setSel_pmt_type("-1");
        this.setSel_pmt_ser("-1");
        this.setSel_pmt_catg("-1");
    }

    public void setNationalPermitAuthDetails(String authRegnNo) throws VahanException {
        PermitHomeAuthDobj np_dobj = null;
        //   ServerUtil serverUtil = new ServerUtil();
        //   String updated_auth_regn_no = serverUtil.getRegnNoWithSpace(authRegnNo);
        np_dobj = ServerUtil.getPermitDetailsFromNp(authRegnNo);
        if (np_dobj != null) {
            setNp_regn_no(np_dobj.getRegnNo());
            if (np_dobj.getAuthUpto() != null) {
                setNp_auth_upto(new SimpleDateFormat("dd-MMM-yy").format(np_dobj.getAuthUpto()));
            }
            if (np_dobj.getAuthFrom() != null) {
                setNp_auth_from(new SimpleDateFormat("dd-MMM-yy").format(np_dobj.getAuthFrom()));
            }
            setNp_auth_no(np_dobj.getAuthNo());
        } else {
            setNp_regn_no("");
            setNp_auth_upto(null);
            setNp_auth_from(null);
            setNp_auth_no("");
        }
    }

    public void permitComponentReadOnly(boolean flag) {
        readOnlyComponent = flag;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public int getOff_cd() {
        return off_cd;
    }

    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    public List getPmt_type_array() {
        return pmt_type_array;
    }

    public void setPmt_type_array(List pmt_type_array) {
        this.pmt_type_array = pmt_type_array;
    }

    public List getPmt_catg_array() {
        return pmt_catg_array;
    }

    public void setPmt_catg_array(List pmt_catg_array) {
        this.pmt_catg_array = pmt_catg_array;
    }

    public List getSer_type_array() {
        return ser_type_array;
    }

    public void setSer_type_array(List ser_type_array) {
        this.ser_type_array = ser_type_array;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getPmt_no() {
        return pmt_no;
    }

    public void setPmt_no(String pmt_no) {
        this.pmt_no = pmt_no;
    }

    public String getIssue_dt() {
        return issue_dt;
    }

    public void setIssue_dt(String issue_dt) {
        this.issue_dt = issue_dt;
    }

    public String getValid_from() {
        return valid_from;
    }

    public void setValid_from(String valid_from) {
        this.valid_from = valid_from;
    }

    public String getValid_upto() {
        return valid_upto;
    }

    public void setValid_upto(String valid_upto) {
        this.valid_upto = valid_upto;
    }

    public String getRcpt_no() {
        return rcpt_no;
    }

    public void setRcpt_no(String rcpt_no) {
        this.rcpt_no = rcpt_no;
    }

    public String getPur_cd() {
        return pur_cd;
    }

    public void setPur_cd(String pur_cd) {
        this.pur_cd = pur_cd;
    }

    public String getPmt_type_desc() {
        return pmt_type_desc;
    }

    public void setPmt_type_desc(String pmt_type_desc) {
        this.pmt_type_desc = pmt_type_desc;
    }

    public String getPmt_catg_desc() {
        return pmt_catg_desc;
    }

    public void setPmt_catg_desc(String pmt_catg_desc) {
        this.pmt_catg_desc = pmt_catg_desc;
    }

    public String getPmt_Services_desc() {
        return pmt_Services_desc;
    }

    public void setPmt_Services_desc(String pmt_Services_desc) {
        this.pmt_Services_desc = pmt_Services_desc;
    }

    public String getSel_pmt_type() {
        return sel_pmt_type;
    }

    public void setSel_pmt_type(String sel_pmt_type) {
        this.sel_pmt_type = sel_pmt_type;
    }

    public String getSel_pmt_catg() {
        return sel_pmt_catg;
    }

    public void setSel_pmt_catg(String sel_pmt_catg) {
        this.sel_pmt_catg = sel_pmt_catg;
    }

    public String getSel_pmt_ser() {
        return sel_pmt_ser;
    }

    public void setSel_pmt_ser(String sel_pmt_ser) {
        this.sel_pmt_ser = sel_pmt_ser;
    }

    public boolean isReadOnlyComponent() {
        return readOnlyComponent;
    }

    public void setReadOnlyComponent(boolean readOnlyComponent) {
        this.readOnlyComponent = readOnlyComponent;
    }

    public String getNp_regn_no() {
        return np_regn_no;
    }

    public void setNp_regn_no(String np_regn_no) {
        this.np_regn_no = np_regn_no;
    }

    public String getNp_auth_from() {
        return np_auth_from;
    }

    public void setNp_auth_from(String np_auth_from) {
        this.np_auth_from = np_auth_from;
    }

    public String getNp_auth_upto() {
        return np_auth_upto;
    }

    public void setNp_auth_upto(String np_auth_upto) {
        this.np_auth_upto = np_auth_upto;
    }

    public String getNp_permit_no() {
        return np_permit_no;
    }

    public void setNp_permit_no(String np_permit_no) {
        this.np_permit_no = np_permit_no;
    }

    public String getNp_auth_no() {
        return np_auth_no;
    }

    public void setNp_auth_no(String np_auth_no) {
        this.np_auth_no = np_auth_no;
    }

    public String getReplace_dt() {
        return replace_dt;
    }

    public void setReplace_dt(String replace_dt) {
        this.replace_dt = replace_dt;
    }
}
