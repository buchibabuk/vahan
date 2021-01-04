/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author tranC103
 */
public class AxleDetailsDobj implements Serializable, Cloneable {

    private Integer tf_Front = null;
    private Integer tf_Rear = null;
    private Integer tf_Other = null;
    private Integer tf_Tandem = null;
    private String tf_Front1 = "";
    private Integer tf_Front_tyre = null;
    private String tf_Rear1 = "";
    private Integer tf_Rear_tyre = null;
    private String tf_Other1 = "";
    private Integer tf_Other_tyre = null;
    private String tf_Tandem1 = "";
    private Integer tf_Tandem_tyre = null;
    private Integer noOfAxle = null;
    private Integer tf_Rear_Over = 0;
    private boolean axelInsertUpdateFlag;

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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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

    public Integer getTf_Rear_Over() {
        return tf_Rear_Over;
    }

    public void setTf_Rear_Over(Integer tf_Rear_Over) {
        this.tf_Rear_Over = tf_Rear_Over;
    }

    public boolean isAxelInsertUpdateFlag() {
        return axelInsertUpdateFlag;
    }

    public void setAxelInsertUpdateFlag(boolean axelInsertUpdateFlag) {
        this.axelInsertUpdateFlag = axelInsertUpdateFlag;
    }
    
}
