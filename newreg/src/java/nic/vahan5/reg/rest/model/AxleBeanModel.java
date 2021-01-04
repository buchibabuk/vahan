/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.model;

import java.util.ArrayList;
import java.util.List;
import nic.vahan.form.bean.AxleBean;
import nic.vahan.form.dobj.AxleDetailsDobj;
import nic.vahan.server.CommonUtils;

/**
 *
 * @author Kartikey Singh
 */
public class AxleBeanModel {

    private String tf_Front1;
    private String tf_Rear1;
    private String tf_Other1;
    private String tf_Tandem1;
    private Integer tf_Front;
    private Integer tf_Rear;
    private Integer tf_Other;
    private Integer tf_Tandem;
    private Integer noOfAxle;
    private AxleDetailsDobj axle_dobj_prv;
    private List<ComparisonBeanModel> compBeanList = new ArrayList<>();
    private Integer r_overhang;
    private Integer tf_Front_tyre;
    private Integer tf_Rear_tyre;
    private Integer tf_Other_tyre;
    private Integer tf_Tandem_tyre;

    public AxleBeanModel() {
    }

    public AxleBeanModel(AxleBean axleBean) {
        this.tf_Front1 = axleBean.getTf_Front1();
        this.tf_Rear1 = axleBean.getTf_Rear1();
        this.tf_Other1 = axleBean.getTf_Other1();
        this.tf_Tandem1 = axleBean.getTf_Tandem1();
        this.tf_Front = axleBean.getTf_Front();
        this.tf_Rear = axleBean.getTf_Rear();
        this.tf_Other = axleBean.getTf_Other();
        this.tf_Tandem = axleBean.getTf_Tandem();
        this.noOfAxle = axleBean.getNoOfAxle();
        this.axle_dobj_prv = axleBean.getAxle_dobj_prv();
        this.r_overhang = axleBean.getR_overhang();
        this.tf_Front_tyre = axleBean.getTf_Front_tyre();
        this.tf_Rear_tyre = axleBean.getTf_Rear_tyre();
        this.tf_Other_tyre = axleBean.getTf_Other_tyre();
        this.tf_Tandem_tyre = axleBean.getTf_Tandem_tyre();
    }

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

    public Integer getNoOfAxle() {
        return noOfAxle;
    }

    public void setNoOfAxle(Integer noOfAxle) {
        this.noOfAxle = noOfAxle;
    }

    public AxleDetailsDobj getAxle_dobj_prv() {
        return axle_dobj_prv;
    }

    public void setAxle_dobj_prv(AxleDetailsDobj axle_dobj_prv) {
        this.axle_dobj_prv = axle_dobj_prv;
    }

    public List<ComparisonBeanModel> getCompBeanList() {
        return compBeanList;
    }

    public void setCompBeanList(List<ComparisonBeanModel> compBeanList) {
        this.compBeanList = compBeanList;
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
 /*
     * Created by Sai
     */
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

    @Override
    public String toString() {
        return "AxleBeanModel{" + "tf_Front1=" + tf_Front1 + ", tf_Rear1=" + tf_Rear1 + ", tf_Other1=" + tf_Other1 + ", tf_Tandem1=" + tf_Tandem1 + ", tf_Front=" + tf_Front + ", tf_Rear=" + tf_Rear + ", tf_Other=" + tf_Other + ", tf_Tandem=" + tf_Tandem + ", noOfAxle=" + noOfAxle + ", axle_dobj_prv=" + axle_dobj_prv + ", compBeanList=" + compBeanList + ", r_overhang=" + r_overhang + ", tf_Front_tyre=" + tf_Front_tyre + ", tf_Rear_tyre=" + tf_Rear_tyre + ", tf_Other_tyre=" + tf_Other_tyre + ", tf_Tandem_tyre=" + tf_Tandem_tyre + '}';
    }

}
