/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import static nic.vahan.server.ServerUtil.Compare;

/**
 *
 * @author tranC103
 */
@ManagedBean
@ViewScoped
public class AxleBean implements Serializable {

    private String tf_Front1 = "";
    private String tf_Rear1 = "";
    private String tf_Other1 = "";
    private String tf_Tandem1 = "";
    private Integer tf_Front = null;
    private Integer tf_Rear = null;
    private Integer tf_Other = null;
    private Integer tf_Tandem = null;
    private Integer noOfAxle = null;
    private AxleDetailsDobj axle_dobj_prv;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private Integer r_overhang = 0;
    private Integer tf_Front_tyre = null;
    private Integer tf_Rear_tyre = null;
    private Integer tf_Other_tyre = null;
    private Integer tf_Tandem_tyre = null;

    public AxleDetailsDobj setBean_To_Dobj() {
        AxleDetailsDobj obj = null;
        String valid = validateAxleForm();
        if (CommonUtils.isNullOrBlank(valid)) {
            obj = new AxleDetailsDobj();
            obj.setTf_Front(tf_Front);
            obj.setTf_Front_tyre(tf_Front_tyre);
            obj.setTf_Front1(tf_Front1.trim());
            obj.setTf_Other(tf_Other);
            obj.setTf_Other_tyre(tf_Other_tyre);
            obj.setTf_Other1(tf_Other1.trim());
            obj.setTf_Rear(tf_Rear);
            obj.setTf_Rear_tyre(tf_Rear_tyre);
            obj.setTf_Rear1(tf_Rear1.trim());
            obj.setTf_Tandem(tf_Tandem);
            obj.setTf_Tandem_tyre(tf_Tandem_tyre);
            obj.setTf_Tandem1(tf_Tandem1.trim());
            obj.setTf_Rear_Over(r_overhang);
            obj.setNoOfAxle(noOfAxle);
        }
        return obj;
    }

    public void setDobj_To_Bean(AxleDetailsDobj obj) {
        if (obj != null) {
            setTf_Front(obj.getTf_Front());
            setTf_Front1(obj.getTf_Front1());
            setTf_Other(obj.getTf_Other());
            setTf_Other1(obj.getTf_Other1());
            setTf_Rear(obj.getTf_Rear());
            setTf_Rear1(obj.getTf_Rear1());
            setTf_Tandem(obj.getTf_Tandem());
            setTf_Tandem1(obj.getTf_Tandem1());
            setR_overhang(obj.getTf_Rear_Over());
            setTf_Front_tyre(obj.getTf_Front_tyre());
            setTf_Rear_tyre(obj.getTf_Rear_tyre());
            setTf_Other_tyre(obj.getTf_Other_tyre());
            setTf_Tandem_tyre(obj.getTf_Tandem_tyre());
            setNoOfAxle(obj.getNoOfAxle());
        } else {
            return;
        }
    }

    private String validateAxleForm() {
        String errorMsg = "";
        if (getTf_Front1().equalsIgnoreCase("")) {
            errorMsg = "Blank Front Axle Detail";
        } else if (getTf_Rear1().equalsIgnoreCase("")) {
            errorMsg = "Blank Rear Axle Detail";
        } else if (getTf_Front() == null) {
            errorMsg = "Blank Front Axle Detail";
        } else if (getTf_Rear() == null) {
            errorMsg = "Blank Rear Axle Detail";
        }
        return errorMsg;
    }

    public void validateNoOfAxle() {
        boolean flag = this.validateAxle(noOfAxle);
        if (flag) {
            JSFUtils.showMessage("No Of Axle's should be more than or equals 2");
            setNoOfAxle(null);
        }
    }

    public void axleServerValidator(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        int axle = 0;
        if (value != null) {
            axle = Integer.parseInt(String.valueOf(value));
        }
        boolean flag = this.validateAxle(axle);
        if (flag) {
            throw new ValidatorException(new FacesMessage("No Of Axle's should be more than or equals 2"));
        }
    }

    private boolean validateAxle(int noOfAxle) {
        boolean isAllow = true;
        OwnerBean ownerBean = (OwnerBean) FacesContext.getCurrentInstance().getViewRoot().getViewMap().get("owner_bean");
        if ("CG".equals(Util.getUserStateCode()) && noOfAxle == 1 && ownerBean != null && ownerBean.getOwnerDobj() != null && ownerBean.getOwnerDobj().getVh_class() == 89
                && (TableConstants.VM_REGN_TYPE_NEW.equals(ownerBean.getOwnerDobj().getRegn_type())
                || TableConstants.VM_REGN_TYPE_TEMPORARY.equals(ownerBean.getOwnerDobj().getRegn_type()))) {
            isAllow = false;
        }
        if (noOfAxle < 2 && isAllow) {
            isAllow = true;
        } else {
            isAllow = false;
        }
        return isAllow;
    }

    public List<ComparisonBean> compareChagnes() throws VahanException {

        AxleDetailsDobj dobj = getAxle_dobj_prv();

        if (dobj == null) {
            return getCompBeanList();
        }
        getCompBeanList().clear();

        Compare("Front", dobj.getTf_Front(), this.tf_Front, (ArrayList) getCompBeanList());
        Compare("Rear", dobj.getTf_Rear(), this.tf_Rear, (ArrayList) getCompBeanList());
        Compare("Other", dobj.getTf_Other(), this.tf_Other, (ArrayList) getCompBeanList());
        Compare("Tandem", dobj.getTf_Tandem(), this.tf_Tandem, (ArrayList) getCompBeanList());
        Compare("Front Desc", dobj.getTf_Front1(), this.getTf_Front1(), (ArrayList) getCompBeanList());
        Compare("Rear Desc", dobj.getTf_Rear1(), this.tf_Rear1, (ArrayList) getCompBeanList());
        Compare("Other Desc", dobj.getTf_Other1(), this.tf_Other1, (ArrayList) getCompBeanList());
        Compare("Tandem Desc", dobj.getTf_Tandem1(), this.tf_Tandem1, (ArrayList) getCompBeanList());
        Compare("No Of Axle", dobj.getNoOfAxle(), this.noOfAxle, (ArrayList) getCompBeanList());
        Compare("Front Tyre", dobj.getTf_Other_tyre(), this.tf_Other_tyre, (ArrayList) getCompBeanList());
        Compare("Other Tyre", dobj.getTf_Front_tyre(), this.tf_Front_tyre, (ArrayList) getCompBeanList());
        Compare("Rear Tyre", dobj.getTf_Rear_tyre(), this.tf_Rear_tyre, (ArrayList) getCompBeanList());
        Compare("Tendam Tyre", dobj.getTf_Tandem_tyre(), this.tf_Tandem_tyre, (ArrayList) getCompBeanList());

        return getCompBeanList();

    }

    public List<ComparisonBean> addToComapreChangesList(List<ComparisonBean> compBeanListPrev) throws VahanException {

        List<ComparisonBean> list = compareChagnes();

        if (compBeanListPrev == null) {
            compBeanListPrev = new ArrayList<ComparisonBean>();
        }
        if (!list.isEmpty()) {
            compBeanListPrev.addAll(list);
        }

        return compBeanListPrev;
    }

    public String getTf_Front1() {
        return tf_Front1;
    }

    public void setTf_Front1(String tf_Front1) {
        this.tf_Front1 = tf_Front1;
    }

    public String getTf_Rear1() {
        return tf_Rear1;
    }

    public void setTf_Rear1(String tf_Rear1) {
        this.tf_Rear1 = tf_Rear1;
    }

    public String getTf_Other1() {
        return tf_Other1;
    }

    public void setTf_Other1(String tf_Other1) {
        this.tf_Other1 = tf_Other1;
    }

    public String getTf_Tandem1() {
        return tf_Tandem1;
    }

    public void setTf_Tandem1(String tf_Tandem1) {
        this.tf_Tandem1 = tf_Tandem1;
    }

    public Integer getTf_Front() {
        return tf_Front;
    }

    public void setTf_Front(Integer tf_Front) {
        this.tf_Front = tf_Front;
    }

    public Integer getTf_Rear() {
        return tf_Rear;
    }

    public void setTf_Rear(Integer tf_Rear) {
        this.tf_Rear = tf_Rear;
    }

    public Integer getTf_Other() {
        return tf_Other;
    }

    public void setTf_Other(Integer tf_Other) {
        this.tf_Other = tf_Other;
    }

    public Integer getTf_Tandem() {
        return tf_Tandem;
    }

    public void setTf_Tandem(Integer tf_Tandem) {
        this.tf_Tandem = tf_Tandem;
    }

    public AxleDetailsDobj getAxle_dobj_prv() {
        return axle_dobj_prv;
    }

    public void setAxle_dobj_prv(AxleDetailsDobj axle_dobj_prv) {
        this.axle_dobj_prv = axle_dobj_prv;
    }

    /**
     * @return the compBeanList
     */
    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    /**
     * @param compBeanList the compBeanList to set
     */
    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    /**
     * @return the noOfAxle
     */
    public Integer getNoOfAxle() {
        return noOfAxle;
    }

    /**
     * @param noOfAxle the noOfAxle to set
     */
    public void setNoOfAxle(Integer noOfAxle) {
        this.noOfAxle = noOfAxle;
    }

    public Integer getR_overhang() {
        return r_overhang;
    }

    public void setR_overhang(Integer r_overhang) {
        this.r_overhang = r_overhang;
    }

    public Integer getTf_Front_tyre() {
        return tf_Front_tyre;
    }

    public void setTf_Front_tyre(Integer tf_Front_tyre) {
        this.tf_Front_tyre = tf_Front_tyre;
    }

    public Integer getTf_Rear_tyre() {
        return tf_Rear_tyre;
    }

    public void setTf_Rear_tyre(Integer tf_Rear_tyre) {
        this.tf_Rear_tyre = tf_Rear_tyre;
    }

    public Integer getTf_Other_tyre() {
        return tf_Other_tyre;
    }

    public void setTf_Other_tyre(Integer tf_Other_tyre) {
        this.tf_Other_tyre = tf_Other_tyre;
    }

    public Integer getTf_Tandem_tyre() {
        return tf_Tandem_tyre;
    }

    public void setTf_Tandem_tyre(Integer tf_Tandem_tyre) {
        this.tf_Tandem_tyre = tf_Tandem_tyre;
    }
}
