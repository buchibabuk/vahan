/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.eChallan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.eChallan.DocumentReleaseDobj;
import nic.vahan.form.impl.PrintDocImpl;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
@ManagedBean
@ViewScoped
public class DocumentReleaseBean extends AbstractApplBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(CashReportBean.class);
    private int srNo;
    private String chal_no;
    private String appl_no;
    private String vehicle_no;
    private String release_Date;
    private String release_by;
    private String doc_desc;
    private String validity;
    private String doc_no;
    private String iss_auth;
    private String accused_catg;
    private String rto_name;
    private String otherDocName;
    private String rcpt_header;
    private String rcpt_sub_header;
    private boolean renderPanel;
    PrintDocImpl impl = new PrintDocImpl();
    List<DocumentReleaseDobj> listDobj = new ArrayList<DocumentReleaseDobj>();

    public DocumentReleaseBean() throws VahanException {

        if (appl_details != null) {
            appl_no = appl_details.getAppl_no();
        }
        setListDobj(impl.getDocumentReleaseDetails(appl_no));
        getDocumenReleasetDetails(appl_no);
    }

    public void getDocumenReleasetDetails(String appl_no) {
        try {
            for (DocumentReleaseDobj dobj : listDobj) {
                if (dobj != null) {
                    if (dobj.getDoc_no() != null && !dobj.getDoc_no().equals("")) {
                        setSrNo(dobj.getSrNo());
                        setChal_no(dobj.getChal_no());
                        setRelease_Date(dobj.getRelease_Date());
                        setRelease_by(dobj.getRelease_by());
                        setVehicle_no(dobj.getVehicle_no());
                        setIss_auth(dobj.getIss_auth());
                        setDoc_desc(dobj.getDoc_desc());
                        setDoc_no(dobj.getDoc_no());
                        setValidity(dobj.getValidity());
                        setAccused_catg(dobj.getAccused_catg());
                        setRto_name(dobj.getRto_name());
                        setOtherDocName(dobj.getOther_doc_name());
                        setRcpt_header(ServerUtil.getRcptHeading());
                        setRcpt_sub_header(ServerUtil.getRcptSubHeading());
                        setRenderPanel(true);
                    }

                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    /**
     * @return the srNo
     */
    public int getSrNo() {
        return srNo;
    }

    /**
     * @param srNo the srNo to set
     */
    public void setSrNo(int srNo) {
        this.srNo = srNo;
    }

    /**
     * @return the chal_no
     */
    public String getChal_no() {
        return chal_no;
    }

    /**
     * @param chal_no the chal_no to set
     */
    public void setChal_no(String chal_no) {
        this.chal_no = chal_no;
    }

    /**
     * @return the vehicle_no
     */
    public String getVehicle_no() {
        return vehicle_no;
    }

    /**
     * @param vehicle_no the vehicle_no to set
     */
    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }

    /**
     * @return the release_Date
     */
    public String getRelease_Date() {
        return release_Date;
    }

    /**
     * @param release_Date the release_Date to set
     */
    public void setRelease_Date(String release_Date) {
        this.release_Date = release_Date;
    }

    /**
     * @return the release_by
     */
    public String getRelease_by() {
        return release_by;
    }

    /**
     * @param release_by the release_by to set
     */
    public void setRelease_by(String release_by) {
        this.release_by = release_by;
    }

    /**
     * @return the doc_desc
     */
    public String getDoc_desc() {
        return doc_desc;
    }

    /**
     * @param doc_desc the doc_desc to set
     */
    public void setDoc_desc(String doc_desc) {
        this.doc_desc = doc_desc;
    }

    /**
     * @return the validity
     */
    public String getValidity() {
        return validity;
    }

    /**
     * @param validity the validity to set
     */
    public void setValidity(String validity) {
        this.validity = validity;
    }

    /**
     * @return the doc_no
     */
    public String getDoc_no() {
        return doc_no;
    }

    /**
     * @param doc_no the doc_no to set
     */
    public void setDoc_no(String doc_no) {
        this.doc_no = doc_no;
    }

    /**
     * @return the iss_auth
     */
    public String getIss_auth() {
        return iss_auth;
    }

    /**
     * @param iss_auth the iss_auth to set
     */
    public void setIss_auth(String iss_auth) {
        this.iss_auth = iss_auth;
    }

    /**
     * @return the accused_catg
     */
    public String getAccused_catg() {
        return accused_catg;
    }

    /**
     * @param accused_catg the accused_catg to set
     */
    public void setAccused_catg(String accused_catg) {
        this.accused_catg = accused_catg;
    }

    /**
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }

    /**
     * @param appl_no the appl_no to set
     */
    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    public List<DocumentReleaseDobj> getListDobj() {
        return listDobj;
    }

    public void setListDobj(List<DocumentReleaseDobj> listDobj) {
        this.listDobj = listDobj;
    }

    /**
     * @return the rto_name
     */
    public String getRto_name() {
        return rto_name;
    }

    /**
     * @param rto_name the rto_name to set
     */
    public void setRto_name(String rto_name) {
        this.rto_name = rto_name;
    }

    /**
     * @return the rcpt_header
     */
    public String getRcpt_header() {
        return rcpt_header;
    }

    /**
     * @param rcpt_header the rcpt_header to set
     */
    public void setRcpt_header(String rcpt_header) {
        this.rcpt_header = rcpt_header;
    }

    /**
     * @return the rcpt_sub_header
     */
    public String getRcpt_sub_header() {
        return rcpt_sub_header;
    }

    /**
     * @param rcpt_sub_header the rcpt_sub_header to set
     */
    public void setRcpt_sub_header(String rcpt_sub_header) {
        this.rcpt_sub_header = rcpt_sub_header;
    }

    /**
     * @return the renderPanel
     */
    public boolean isRenderPanel() {
        return renderPanel;
    }

    /**
     * @param renderPanel the renderPanel to set
     */
    public void setRenderPanel(boolean renderPanel) {
        this.renderPanel = renderPanel;
    }

    /**
     * @return the otherDocName
     */
    public String getOtherDocName() {
        return otherDocName;
    }

    /**
     * @param otherDocName the otherDocName to set
     */
    public void setOtherDocName(String otherDocName) {
        this.otherDocName = otherDocName;
    }
}
