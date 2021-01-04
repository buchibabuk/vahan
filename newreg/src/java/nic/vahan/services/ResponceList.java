package nic.vahan.services;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author acer
 */
@XmlRootElement(name = "responseList")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class ResponceList {

    public ResponceList() {
    }

    public ResponceList(DlDetObj[] dlobj) {

        if (dlobj != null && dlobj.length > 0) {
            this.dldetObj = new DlDetObj[dlobj.length];
            for (int i = 0; i < dlobj.length; i++) {
                dldetObj[i] = dlobj[i];
            }

        }

//		System.out.println("b4 return...................."+dlobj.getDlobj().getDlStatus());
    }
    private DlDetObj[] dldetObj;

    @XmlElement(name = "dldetobj")
    public DlDetObj[] getDldetObj() {
        return dldetObj;
    }

    public void setDldetObj(DlDetObj[] dldetObj) {
        this.dldetObj = dldetObj;
    }
}