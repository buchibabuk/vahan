/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.permit.PermitPaidFeeDtlsDobj;
import nic.vahan.form.impl.permit.PermitPaidFeeDtlsImpl;
import nic.vahan.form.impl.permit.PrintPermitDocInXhtmlImpl;
import org.primefaces.PrimeFaces;

/**
 *
 * @author R Gautam
 */
@ManagedBean(name = "paidFeeDtls")
@ViewScoped
public class PermitPaidFeeDtlsBean implements Serializable {

    List<PermitPaidFeeDtlsDobj> paidFeeList = new ArrayList<>();

    public void getListOfPaidPmtFee(String appl_no) throws VahanException {
        PermitPaidFeeDtlsImpl impl = new PermitPaidFeeDtlsImpl();
        setPaidFeeList(impl.getListOfPaidFee(appl_no));
        if (paidFeeList != null && paidFeeList.size() > 0) {
            for (int i = 0; i <= paidFeeList.size(); i++) {
                if (paidFeeList.get(i).getPurCd() == TableConstants.VM_PMT_FRESH_PUR_CD || paidFeeList.get(i).getPurCd() == TableConstants.VM_PMT_HOME_AUTH_PERMIT_PUR_CD || paidFeeList.get(i).getPurCd() == TableConstants.VM_PMT_RENEWAL_HOME_AUTH_PERMIT_PUR_CD) {
                    Map<String, String> getNpMap = new PrintPermitDocInXhtmlImpl().getNPHomeAuthFromNpPortal(paidFeeList.get(i).getRegn_no(), appl_no);
                    if (getNpMap != null && !getNpMap.isEmpty()) {
                        PermitPaidFeeDtlsDobj dobj = new PermitPaidFeeDtlsDobj();
                        dobj.setRcpt_no(getNpMap.get("bank_ref_no"));
                        dobj.setFees(Integer.parseInt(getNpMap.get("amount")));
                        dobj.setFine(0);
                        dobj.setRegn_no(paidFeeList.get(i).getRegn_no());
                        dobj.setRcpt_dt(getNpMap.get("issue_dt"));
                        dobj.setPurpose("NP-AUTH/Composite Fee");
                        paidFeeList.add(dobj);
                    }
                }
                break;
            }
        }

    }

    public List<PermitPaidFeeDtlsDobj> getPaidFeeList() {
        return paidFeeList;
    }

    public void setPaidFeeList(List<PermitPaidFeeDtlsDobj> paidFeeList) {
        this.paidFeeList = paidFeeList;
    }
}
