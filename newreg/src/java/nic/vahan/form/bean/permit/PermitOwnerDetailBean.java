/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.user_mgmt.dobj.UserAuthorityDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.permit.PermitOwnerDetailDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.permit.CommonPermitPrintImpl;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;

@ManagedBean(name = "pmt_owner_dtls")
@ViewScoped
public class PermitOwnerDetailBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(PermitOwnerDetailBean.class);
    private List list_c_district = new ArrayList();
    private List list_p_district = new ArrayList();
    private List list_c_state = new ArrayList();
    private PermitOwnerDetailDobj pmt_dobj = new PermitOwnerDetailDobj();
    private PermitOwnerDetailDobj pmt_dobj_prv = new PermitOwnerDetailDobj();
    private List list_p_state = new ArrayList();
    private List c_district_array = new ArrayList();
    private List p_district_array = new ArrayList();
    private List vh_class_array = new ArrayList();
    private List vh_catg_array = new ArrayList();
    private List vm_fuel_list = new ArrayList();
    private List own_catg_array = new ArrayList();
    private boolean flag_Disable = true;
    private String vh_class = null;
    private String c_district = "";
    private String p_district = "";
    private String owner_name = "";
    private String f_name = "";
    private String c_add1 = "";
    private String c_add2 = "";
    private String c_pincode = "";
    private String p_add1 = "";
    private String p_add2 = "";
    private String p_pincode = "";
    private String mobile_no = "";
    private String email_id = "";
    private String seat_cap = "";
    private String chasi_no = "";
    private String unld_wt = "";
    private String ld_wt = "";
    private String gcw = "";
    private String vh_class_pram = "";
    private String regn_no = "";
    private String c_state = "";
    private String c_add3 = "";
    private String p_add3 = "";
    private String p_state = "";
    private int owner_catg;
    private int owner_cd;
    private String vch_catg = "";
    private int fuel_type;
    private boolean sameAsCurrAddress;
    private UserAuthorityDobj authorityDobj = null;
    private String permissionVhClass = "";
    private String regn_dt = null;
    private List<OwnerDetailsDobj> dupRegnList = new ArrayList<>();
    private String dl_no = "";
    private String voter_id = "";
    private Map<String, String> stateConfigMap = null;

    public PermitOwnerDetailBean() {
        authorityDobj = Util.getUserAuthority();

        list_c_state = MasterTableFiller.getStateList();
        list_p_state = MasterTableFiller.getStateList();

        stateConfigMap = CommonPermitPrintImpl.getVmPermitStateConfiguration(Util.getUserStateCode());
        boolean spl_vh_class = Boolean.parseBoolean(stateConfigMap.get("allow_special_vh_class"));
        String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
        if (authorityDobj != null) {
            if (authorityDobj.getSelectedVehClass().size() == 1) {
                for (Object obj : authorityDobj.getSelectedVehClass()) {
                    permissionVhClass = String.valueOf(obj);
                }
            }
        }

        if (authorityDobj == null || ("ANY").equalsIgnoreCase(permissionVhClass)) {
            for (int i = 0; i < data.length; i++) {
                if (("1").equalsIgnoreCase(data[i][2]) && (("P").equalsIgnoreCase(data[i][3]) || ("G").equalsIgnoreCase(data[i][3]))) {
                    vh_class_array.add(new SelectItem(data[i][0], data[i][1]));
                } else if (("1").equalsIgnoreCase(data[i][2]) && ("S").equalsIgnoreCase(data[i][3]) && spl_vh_class) {
                    vh_class_array.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        } else {
            for (int i = 0; i < data.length; i++) {
                if (("1").equalsIgnoreCase(data[i][2]) && (("P").equalsIgnoreCase(data[i][3]) || ("G").equalsIgnoreCase(data[i][3]))) {
                    for (Object obj : authorityDobj.getSelectedVehClass()) {
                        if (String.valueOf(obj).equalsIgnoreCase(data[i][0])) {
                            vh_class_array.add(new SelectItem(data[i][0], data[i][1]));
                        }
                    }
                } else if (("1").equalsIgnoreCase(data[i][2]) && ("S").equalsIgnoreCase(data[i][3])) {
                    for (Object obj : authorityDobj.getSelectedVehClass()) {
                        if (String.valueOf(obj).equalsIgnoreCase(data[i][0]) && spl_vh_class) {
                            vh_class_array.add(new SelectItem(data[i][0], data[i][1]));
                        }
                    }

                }
            }
        }

        data = MasterTableFiller.masterTables.VM_OWCATG.getData();
        for (int i = 0; i < data.length; i++) {
            own_catg_array.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.VM_FUEL.getData();
        for (int i = 0; i < data.length; i++) {
            vm_fuel_list.add(new SelectItem(data[i][0], data[i][1]));
        }

        data = MasterTableFiller.masterTables.VM_VCH_CATG.getData();
        for (int i = 0; i < data.length; i++) {
            vh_catg_array.add(new SelectItem(data[i][0], data[i][1]));
        }
    }

    public void vehClassListener(AjaxBehaviorEvent event) {
        FacesMessage message = null;
        if (getVh_class().equalsIgnoreCase("-1")) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Please select vehicle class.");
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
        } else {
            setVh_class_pram(getVh_class());
        }
        vhClassCatgaction(vh_class);
    }

    public void vhClassCatgaction(String vh_class) {
        vh_catg_array.clear();
        String[][] data1 = MasterTableFiller.masterTables.VM_VHCLASS_CATG_MAP.getData();
        String[][] data2 = MasterTableFiller.masterTables.VM_VCH_CATG.getData();
        for (int j = 0; j < data1.length; j++) {
            if (Integer.valueOf(vh_class) == Integer.valueOf(data1[j][0])) {
                for (int k = 0; k < data2.length; k++) {
                    if (data1[j][1].equalsIgnoreCase(data2[k][0])) {
                        vh_catg_array.add(new SelectItem(data2[k][0], data2[k][1]));
                        break;
                    }
                }
            }
        }
    }

    public void cStateListener(AjaxBehaviorEvent event) {
        list_c_district.clear();
        if (getC_state() != null) {
            String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
            for (int i = 0; i < data.length; i++) {
                if (getC_state().equalsIgnoreCase(data[i][2])) {
                    list_c_district.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        }
    }

    public void pStateListener(AjaxBehaviorEvent event) {
        list_p_district.clear();
        if (getP_state() != null) {
            String[][] data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
            for (int i = 0; i < data.length; i++) {
                if (getP_state().equalsIgnoreCase(data[i][2])) {
                    list_p_district.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        }
    }

    public void sameAsCurrentAddressListener(AjaxBehaviorEvent event) {
        if (this.sameAsCurrAddress) {
            setP_add1(getC_add1());
            setP_add2(getC_add2());
            setP_add3(getC_add3());
            setP_state(getC_state());
            setP_pincode(getC_pincode());
            pStateListener(null);
            setP_district(getC_district());
        } else {
            setP_add1("");
            setP_add2("");
            setP_add3("");
            setP_pincode("");
            setP_district("-1");
            setP_state(Util.getUserStateCode());
            pStateListener(null);
        }
    }

    public void setValueinDOBJ(PermitOwnerDetailDobj dobj) {
        if (dobj != null) {
            this.setOwner_name(dobj.getOwner_name());
            this.setF_name(dobj.getF_name());
            setVh_class(String.valueOf(dobj.getVh_class()));
            this.setMobile_no(String.valueOf(dobj.getMobile_no()));
            this.setEmail_id(dobj.getEmail_id());
            this.setDl_no(dobj.getDl_no());
            this.setVoter_id(dobj.getVoter_id());
            this.setC_add1(dobj.getC_add1());
            this.setC_add2(dobj.getC_add2());
            this.setC_add3(dobj.getC_add3());
            this.setC_state(dobj.getC_state());
            list_c_district = MasterTableFiller.getDistrictList(getC_state());
            this.setC_district(String.valueOf(dobj.getC_district()));
            this.setC_pincode(String.valueOf(dobj.getC_pincode()));
            this.setP_add1(dobj.getP_add1());
            this.setP_add2(dobj.getP_add2());
            this.setP_add3(dobj.getP_add3());
            this.setP_state(dobj.getP_state());
            list_p_district = MasterTableFiller.getDistrictList(getP_state());
            this.setP_district(String.valueOf(dobj.getP_district()));
            this.setP_pincode(String.valueOf(dobj.getP_pincode()));
            this.setRegn_no(dobj.getRegn_no());
            this.setSeat_cap(String.valueOf(dobj.getSeat_cap()));
            this.setUnld_wt(String.valueOf(dobj.getUnld_wt()));
            this.setLd_wt(String.valueOf(dobj.getLd_wt()));
            this.setOwner_catg(dobj.getOwner_catg());
            this.setVch_catg(dobj.getVch_catg());
            this.setFuel_type(dobj.getFuelCd());
            if (!("NEW").equalsIgnoreCase(dobj.getRegn_no())) {
                this.setChasi_no(dobj.getChasi_no());
                this.setGcw(String.valueOf(dobj.getGcw()));
                this.setOwner_cd(dobj.getOwner_cd());
                if (dobj.getRegnDt() != null) {
                    this.setRegn_dt(CommonPermitPrintImpl.getDateStringDD_MMM_YYYY(dobj.getRegnDt()));
                }
            }
        }
    }

    public PermitOwnerDetailDobj setValueinDOBJ() {
        PermitOwnerDetailDobj dobj = null;
        try {
            dobj = new PermitOwnerDetailDobj();
            dobj.setOwner_name(getOwner_name().toUpperCase());
            dobj.setF_name(getF_name().toUpperCase());
            if (CommonUtils.isNullOrBlank(getVh_class())) {
                dobj.setVh_class(-1);
            } else {
                dobj.setVh_class(Integer.parseInt(getVh_class()));
            }
            if (CommonUtils.isNullOrBlank(getMobile_no())) {
                dobj.setMobile_no(0);
            } else {
                dobj.setMobile_no(Long.parseLong(getMobile_no()));
            }
            if (getEmail_id() == null) {
                dobj.setEmail_id("");
            } else {
                dobj.setEmail_id((String) getEmail_id());
            }
            if (CommonUtils.isNullOrBlank(getDl_no())) {
                dobj.setDl_no("");
            } else {
                dobj.setDl_no(getDl_no());
            }

            if (CommonUtils.isNullOrBlank(getVoter_id())) {
                dobj.setVoter_id("");
            } else {
                dobj.setVoter_id(getVoter_id());
            }

            dobj.setC_add1(getC_add1().toUpperCase());
            dobj.setC_add2(getC_add2().toUpperCase());
            dobj.setC_add3(getC_add3().toUpperCase());
            if (CommonUtils.isNullOrBlank(getC_district())) {
                dobj.setC_district(-1);
            } else {
                dobj.setC_district(Integer.parseInt(getC_district()));
            }

            if (CommonUtils.isNullOrBlank(getC_pincode())) {
                dobj.setC_pincode(0);
            } else {
                dobj.setC_pincode(Integer.parseInt(getC_pincode()));
            }
            dobj.setC_state(getC_state().toUpperCase());
            dobj.setP_add1(getP_add1().toUpperCase());
            dobj.setP_add2(getP_add2().toUpperCase());
            dobj.setP_add3(getP_add3().toUpperCase());
            if (CommonUtils.isNullOrBlank(getP_district())) {
                dobj.setP_district(-1);
            } else {
                dobj.setP_district(Integer.parseInt(getP_district()));
            }

            if (CommonUtils.isNullOrBlank(getP_pincode())) {
                dobj.setP_pincode(0);
            } else {
                dobj.setP_pincode(Integer.parseInt(getP_pincode()));
            }

            dobj.setP_state(getP_state().toUpperCase());
            if (CommonUtils.isNullOrBlank(getSeat_cap())) {
                dobj.setSeat_cap(0);
            } else {
                dobj.setSeat_cap(Integer.parseInt(getSeat_cap()));
            }
            if (CommonUtils.isNullOrBlank(getUnld_wt())) {
                dobj.setUnld_wt(0);
            } else {
                dobj.setUnld_wt(Integer.parseInt(getUnld_wt()));
            }
            if (CommonUtils.isNullOrBlank(getLd_wt())) {
                dobj.setLd_wt(0);
            } else {
                dobj.setLd_wt(Integer.parseInt(getLd_wt()));
            }
            if (CommonUtils.isNullOrBlank(getGcw())) {
                dobj.setGcw(0);
            } else {
                dobj.setGcw(Integer.parseInt(getGcw()));
            }
            dobj.setOwner_catg(getOwner_catg());
            dobj.setVch_catg(getVch_catg());
            dobj.setFuelCd(getFuel_type());
            if (getRegn_dt() != null) {
                dobj.setRegnDt(JSFUtils.getStringToDateyyyyMMdd(getRegn_dt()));
            }
        } catch (Exception e) {
            dobj = null;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return dobj;
    }

    public void setValueReset() {
        try {
            this.setOwner_name("");
            this.setF_name("");
            this.setVh_class("-1");
            this.setMobile_no("");
            this.setEmail_id("");
            this.setC_add1("");
            this.setC_add2("");
            this.setC_add3("");
            this.setC_district("-1");
            this.setC_pincode("");
            this.setC_state(Util.getUserStateCode());
            cStateListener(null);
            this.setP_add1("");
            this.setP_add2("");
            this.setP_add3("");
            this.setP_district("-1");
            this.setP_pincode("");
            this.setP_state(Util.getUserStateCode());
            pStateListener(null);
            this.setSeat_cap("");
            this.setUnld_wt("");
            this.setLd_wt("");
            this.setOwner_catg(-1);
            this.setFuel_type(-1);
            this.setVch_catg("");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public PermitOwnerDetailDobj getOwnerDetailsDobj(OwnerDetailsDobj ownerDetail) {

        PermitOwnerDetailDobj owner_dobj = new PermitOwnerDetailDobj();

        owner_dobj.setRegn_no(ownerDetail.getRegn_no());
        owner_dobj.setOwner_name(ownerDetail.getOwner_name());
        owner_dobj.setF_name(ownerDetail.getF_name());
        owner_dobj.setVh_class(ownerDetail.getVh_class());
        owner_dobj.setSeat_cap(ownerDetail.getSeat_cap());
        owner_dobj.setLd_wt(ownerDetail.getLd_wt());
        owner_dobj.setUnld_wt(ownerDetail.getUnld_wt());
        owner_dobj.setGcw(ownerDetail.getGcw());
        owner_dobj.setState_cd(ownerDetail.getState_cd());
        if (ownerDetail.getOwnerIdentity().getMobile_no() != null) {
            owner_dobj.setMobile_no(ownerDetail.getOwnerIdentity().getMobile_no());
        }
        if (ownerDetail.getOwnerIdentity().getEmail_id() != null) {
            owner_dobj.setEmail_id(ownerDetail.getOwnerIdentity().getEmail_id());
        }
        if (ownerDetail.getOwnerIdentity().getOwnerCatg() != 0) {
            owner_dobj.setOwner_catg(ownerDetail.getOwnerIdentity().getOwnerCatg());
        }
        if (ownerDetail.getOwnerIdentity().getDl_no() != null) {
            owner_dobj.setDl_no(ownerDetail.getOwnerIdentity().getDl_no());
        }
        if (ownerDetail.getOwnerIdentity().getVoter_id() != null) {
            owner_dobj.setVoter_id(ownerDetail.getOwnerIdentity().getVoter_id());
        }

        owner_dobj.setC_add1(ownerDetail.getC_add1());
        owner_dobj.setC_add2(ownerDetail.getC_add2());
        owner_dobj.setC_add3(ownerDetail.getC_add3());
        owner_dobj.setC_state(ownerDetail.getC_state());
        owner_dobj.setC_district(ownerDetail.getC_district());
        owner_dobj.setC_pincode(ownerDetail.getC_pincode());
        owner_dobj.setP_add1(ownerDetail.getP_add1());
        owner_dobj.setP_add2(ownerDetail.getP_add2());
        owner_dobj.setP_add3(ownerDetail.getP_add3());
        owner_dobj.setP_state(ownerDetail.getP_state());
        owner_dobj.setP_district(ownerDetail.getP_district());
        owner_dobj.setP_pincode(ownerDetail.getP_pincode());
        owner_dobj.setChasi_no(ownerDetail.getChasi_no());
        owner_dobj.setOff_cd(ownerDetail.getOff_cd());
        owner_dobj.setRegnDt(JSFUtils.getStringToDateyyyyMMdd(ownerDetail.getRegn_dt()));
        owner_dobj.setReplaceDateByVtOwner(JSFUtils.getStringToDateyyyyMMdd(ownerDetail.getRegn_upto()));
        owner_dobj.setFuelCd(ownerDetail.getFuel());
        vhClassCatgaction(String.valueOf(ownerDetail.getVh_class()));
        owner_dobj.setVch_catg(ownerDetail.getVch_catg());
        owner_dobj.setOwnerStatus(ownerDetail.getStatus());
        owner_dobj.setOwner_cd(ownerDetail.getOwner_cd());
        return owner_dobj;
    }

    public List getC_district_array() {
        return c_district_array;
    }

    public void setC_district_array(List c_district_array) {
        this.c_district_array = c_district_array;
    }

    public List getP_district_array() {
        return p_district_array;
    }

    public void setP_district_array(List p_district_array) {
        this.p_district_array = p_district_array;
    }

    public List getVh_class_array() {
        return vh_class_array;
    }

    public void setVh_class_array(List vh_class_array) {
        this.vh_class_array = vh_class_array;
    }

    public boolean isFlag_Disable() {
        return flag_Disable;
    }

    public void setFlag_Disable(boolean flag_Disable) {
        this.flag_Disable = flag_Disable;
    }

    public String getVh_class() {
        return vh_class;
    }

    public void setVh_class(String vh_class) {
        this.vh_class = vh_class;
    }

    public String getVh_class_pram() {
        return vh_class_pram;
    }

    public void setVh_class_pram(String vh_class_pram) {
        this.vh_class_pram = vh_class_pram;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public List getList_c_district() {
        return list_c_district;
    }

    public void setList_c_district(List list_c_district) {
        this.list_c_district = list_c_district;
    }

    public List getList_p_district() {
        return list_p_district;
    }

    public void setList_p_district(List list_p_district) {
        this.list_p_district = list_p_district;
    }

    public List getList_c_state() {
        return list_c_state;
    }

    public void setList_c_state(List list_c_state) {
        this.list_c_state = list_c_state;
    }

    public List getList_p_state() {
        return list_p_state;
    }

    public void setList_p_state(List list_p_state) {
        this.list_p_state = list_p_state;
    }

    public PermitOwnerDetailDobj getPmt_dobj() {
        return pmt_dobj;
    }

    public void setPmt_dobj(PermitOwnerDetailDobj pmt_dobj) {
        this.pmt_dobj = pmt_dobj;
    }

    public PermitOwnerDetailDobj getPmt_dobj_prv() {
        return pmt_dobj_prv;
    }

    public void setPmt_dobj_prv(PermitOwnerDetailDobj pmt_dobj_prv) {
        this.pmt_dobj_prv = pmt_dobj_prv;

    }

    public boolean isSameAsCurrAddress() {
        return sameAsCurrAddress;
    }

    public void setSameAsCurrAddress(boolean sameAsCurrAddress) {
        this.sameAsCurrAddress = sameAsCurrAddress;
    }

    public String getRegn_dt() {
        return regn_dt;
    }

    public void setRegn_dt(String regn_dt) {
        this.regn_dt = regn_dt;
    }

    public String getC_district() {
        return c_district;
    }

    public void setC_district(String c_district) {
        this.c_district = c_district;
    }

    public String getP_district() {
        return p_district;
    }

    public void setP_district(String p_district) {
        this.p_district = p_district;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getC_add1() {
        return c_add1;
    }

    public void setC_add1(String c_add1) {
        this.c_add1 = c_add1;
    }

    public String getC_add2() {
        return c_add2;
    }

    public void setC_add2(String c_add2) {
        this.c_add2 = c_add2;
    }

    public String getC_pincode() {
        return c_pincode;
    }

    public void setC_pincode(String c_pincode) {
        this.c_pincode = c_pincode;
    }

    public String getP_add1() {
        return p_add1;
    }

    public void setP_add1(String p_add1) {
        this.p_add1 = p_add1;
    }

    public String getP_add2() {
        return p_add2;
    }

    public void setP_add2(String p_add2) {
        this.p_add2 = p_add2;
    }

    public String getP_pincode() {
        return p_pincode;
    }

    public void setP_pincode(String p_pincode) {
        this.p_pincode = p_pincode;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getSeat_cap() {
        return seat_cap;
    }

    public void setSeat_cap(String seat_cap) {
        this.seat_cap = seat_cap;
    }

    public String getChasi_no() {
        return chasi_no;
    }

    public void setChasi_no(String chasi_no) {
        this.chasi_no = chasi_no;
    }

    public String getUnld_wt() {
        return unld_wt;
    }

    public void setUnld_wt(String unld_wt) {
        this.unld_wt = unld_wt;
    }

    public String getLd_wt() {
        return ld_wt;
    }

    public void setLd_wt(String ld_wt) {
        this.ld_wt = ld_wt;
    }

    public String getC_state() {
        return c_state;
    }

    public void setC_state(String c_state) {
        this.c_state = c_state;
    }

    public String getC_add3() {
        return c_add3;
    }

    public void setC_add3(String c_add3) {
        this.c_add3 = c_add3;
    }

    public String getP_add3() {
        return p_add3;
    }

    public void setP_add3(String p_add3) {
        this.p_add3 = p_add3;
    }

    public String getP_state() {
        return p_state;
    }

    public void setP_state(String p_state) {
        this.p_state = p_state;
    }

    public String getPermissionVhClass() {
        return permissionVhClass;
    }

    public void setPermissionVhClass(String permissionVhClass) {
        this.permissionVhClass = permissionVhClass;
    }

    public List<OwnerDetailsDobj> getDupRegnList() {
        return dupRegnList;
    }

    public void setDupRegnList(List<OwnerDetailsDobj> dupRegnList) {
        this.dupRegnList = dupRegnList;
    }

    public int getOwner_catg() {
        return owner_catg;
    }

    public void setOwner_catg(int owner_catg) {
        this.owner_catg = owner_catg;
    }

    public String getVch_catg() {
        return vch_catg;
    }

    public void setVch_catg(String vch_catg) {
        this.vch_catg = vch_catg;
    }

    public List getVh_catg_array() {
        return vh_catg_array;
    }

    public void setVh_catg_array(List vh_catg_array) {
        this.vh_catg_array = vh_catg_array;
    }

    public List getOwn_catg_array() {
        return own_catg_array;
    }

    public void setOwn_catg_array(List own_catg_array) {
        this.own_catg_array = own_catg_array;
    }

    public int getFuel_type() {
        return fuel_type;
    }

    public void setFuel_type(int fuel_type) {
        this.fuel_type = fuel_type;
    }

    public List getVm_fuel_list() {
        return vm_fuel_list;
    }

    public void setVm_fuel_list(List vm_fuel_list) {
        this.vm_fuel_list = vm_fuel_list;
    }

    public String getGcw() {
        return gcw;
    }

    public void setGcw(String gcw) {
        this.gcw = gcw;
    }

    public int getOwner_cd() {
        return owner_cd;
    }

    public void setOwner_cd(int owner_cd) {
        this.owner_cd = owner_cd;
    }

    public String getDl_no() {
        return dl_no;
    }

    public void setDl_no(String dl_no) {
        this.dl_no = dl_no;
    }

    public String getVoter_id() {
        return voter_id;
    }

    public void setVoter_id(String voter_id) {
        this.voter_id = voter_id;
    }
}
