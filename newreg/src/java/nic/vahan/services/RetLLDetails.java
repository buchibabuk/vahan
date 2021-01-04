/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "LLDetails")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class RetLLDetails {

    private LlDetObj lldetObj;
    private String erormsg;
    private int errorcd;

    public RetLLDetails() {
    }

    public RetLLDetails(LlDetObj llobj) {
        this.lldetObj = llobj;
    }

    @XmlElement(name = "llObj")
    public LlDetObj getLldetObj() {
        return lldetObj;
    }

    public void setLldetObj(LlDetObj lldetObj) {
        this.lldetObj = lldetObj;
    }

    @XmlElement(name = "erormsg")
    public String getErormsg() {
        return erormsg;
    }

    public void setErormsg(String erormsg) {
        this.erormsg = erormsg;
    }

    @XmlElement(name = "errorcd")
    public int getErrorcd() {
        return errorcd;
    }

    public void setErrorcd(int errorcd) {
        this.errorcd = errorcd;
    }
}
