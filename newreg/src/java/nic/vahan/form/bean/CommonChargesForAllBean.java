/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.EpayDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.TaxExemptiondobj;
import nic.vahan.form.impl.ExemptionFeeFineImpl;
import nic.vahan.server.ServerUtil;

/**
 *
 * @author nicsi
 */
@ManagedBean(name = "commonCharges")
@ViewScoped
public class CommonChargesForAllBean implements Serializable {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(CommonChargesForAllBean.class);
    List<EpayDobj> common_charge_list = new ArrayList<>();
    List<TaxExemptiondobj> taxExemList = new ArrayList<>();
    ExemptionFeeFineImpl exemImpl = new ExemptionFeeFineImpl();

    public void getCommaonCharges(Owner_dobj owner_dobj) throws VahanException {
        common_charge_list = ServerUtil.getCommonChargesForAll(owner_dobj, null, null);
        taxExemList = exemImpl.getExemptionDetails(owner_dobj.getRegn_no());
        if (taxExemList != null && !taxExemList.isEmpty()) {
            for (TaxExemptiondobj exemdobj : taxExemList) {
                EpayDobj dobj = new EpayDobj();
                dobj.setPurCd(exemdobj.getPur_cd());
                dobj.setPurCdDescr(exemdobj.getExemHead());
                dobj.setE_TaxFee(-exemdobj.getExemAmount());
                common_charge_list.add(dobj);
            }
        }
        if (common_charge_list.isEmpty()) {
            common_charge_list = null;
        }
    }

    public List<EpayDobj> getCommon_charge_list() {
        return common_charge_list;
    }

    public void setCommon_charge_list(List<EpayDobj> common_charge_list) {
        this.common_charge_list = common_charge_list;
    }
}
