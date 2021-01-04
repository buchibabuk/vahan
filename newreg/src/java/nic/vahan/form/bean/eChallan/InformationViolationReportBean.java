/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.eChallan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.HttpSession;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.eChallan.ChallanReportDobj;
import nic.vahan.form.dobj.eChallan.InformationViolationReportDobj;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.eChallan.ChallanReferToImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
@ManagedBean(name = "informationViolationReportBean")
@ViewScoped
public class InformationViolationReportBean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(InformationViolationReportBean.class);
    private InformationViolationReportDobj irDobj;
    private ChallanReportDobj crDodj;
    private List<InformationViolationReportDobj> coutAndAuthList = new ArrayList();
    ChallanReferToImpl impl = new ChallanReferToImpl();
    private List<ChallanReportDobj> accusedOffenceList = new ArrayList<>();
    private String renderMethod;
    private String text;
    private String label;
    private int mode;
    private int size;
    private boolean renderqrCode = false;
    private OwnerDetailsDobj ownerDetailsDobj;

    @PostConstruct
    public void Init() {
        try {
            irDobj = new InformationViolationReportDobj();
            HttpSession session = Util.getSession();
            String applNo = (String) session.getAttribute("appl_no");
            crDodj = impl.getCourtDetailsForIr(applNo);
            setCoutAndAuthList(impl.getcourtAndAuthDetails(applNo));
            irDobj.setRecieptHeading(ServerUtil.getRcptHeading());
            irDobj.setRecieptSubHeading(ServerUtil.getRcptSubHeading());
            OwnerImpl ownerImp = new OwnerImpl();
            ownerDetailsDobj = ownerImp.getOwnerDetails(crDodj.getVehicle_no());
            setAccusedOffenceList(PrintDocImpl.getOffenceAndAccusedDetails(applNo));
            if ((crDodj.getVehicle_no() != null && !crDodj.getVehicle_no().equals("")) && (crDodj.getAppl_no() != null && !crDodj.getAppl_no().equals("")) && (crDodj.getChallan_no() != null && !crDodj.getChallan_no().equals(""))) {
                renderqrCode = true;
                //**********Generating QRCode**********
                renderMethod = "canvas";
                text = "RegnNo:" + crDodj.getVehicle_no() + " \\nChallan No: " + crDodj.getChallan_no() + "\\nOwner:" + crDodj.getOwner_name() + "\\nOffence:" + crDodj.getOffence();
                mode = 0;
                label = "QR Code";
                size = 110;

            }
            session.removeAttribute("appl_no");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    /**
     * @return the coutAndAuthList
     */
    public List<InformationViolationReportDobj> getCoutAndAuthList() {
        return coutAndAuthList;
    }

    /**
     * @param coutAndAuthList the coutAndAuthList to set
     */
    public void setCoutAndAuthList(List<InformationViolationReportDobj> coutAndAuthList) {
        this.coutAndAuthList = coutAndAuthList;
    }

    /**
     * @return the irDobj
     */
    public InformationViolationReportDobj getIrDobj() {
        return irDobj;
    }

    /**
     * @param irDobj the irDobj to set
     */
    public void setIrDobj(InformationViolationReportDobj irDobj) {
        this.irDobj = irDobj;
    }

    public ChallanReportDobj getCrDodj() {
        return crDodj;
    }

    public void setCrDodj(ChallanReportDobj crDodj) {
        this.crDodj = crDodj;
    }

    public List<ChallanReportDobj> getAccusedOffenceList() {
        return accusedOffenceList;
    }

    public void setAccusedOffenceList(List<ChallanReportDobj> accusedOffenceList) {
        this.accusedOffenceList = accusedOffenceList;
    }

    /**
     * @return the renderMethod
     */
    public String getRenderMethod() {
        return renderMethod;
    }

    /**
     * @param renderMethod the renderMethod to set
     */
    public void setRenderMethod(String renderMethod) {
        this.renderMethod = renderMethod;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the mode
     */
    public int getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return the renderqrCode
     */
    public boolean isRenderqrCode() {
        return renderqrCode;
    }

    /**
     * @param renderqrCode the renderqrCode to set
     */
    public void setRenderqrCode(boolean renderqrCode) {
        this.renderqrCode = renderqrCode;
    }

    /**
     * @return the ownerDetailsDobj
     */
    public OwnerDetailsDobj getOwnerDetailsDobj() {
        return ownerDetailsDobj;
    }

    /**
     * @param ownerDetailsDobj the ownerDetailsDobj to set
     */
    public void setOwnerDetailsDobj(OwnerDetailsDobj ownerDetailsDobj) {
        this.ownerDetailsDobj = ownerDetailsDobj;
    }
}
