/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.vahan.form.dobj.REF_DOC_PURPOSE_dobj;
import nic.vahan.form.dobj.REF_DOC_dobj;
import nic.vahan.form.impl.REF_DOC_Impl;
import nic.vahan.form.impl.Util;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author AMBRISH
 */
@ManagedBean(name = "ref_doc_bean")
@ViewScoped
public class REF_DOC_bean implements Serializable {

    private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(REF_DOC_bean.class);
    String appl_no;
    String scan_doc_no;
    int ref_doc_source_cd;
    private String ref_doc_source_descr = "";
    private long sysDate = System.currentTimeMillis();
    private REF_DOC_dobj selected_doc_obj;
    private ArrayList<REF_DOC_dobj> imageList = new ArrayList<REF_DOC_dobj>();
    private ArrayList<REF_DOC_PURPOSE_dobj> imagePurposeList = new ArrayList<REF_DOC_PURPOSE_dobj>();
    private StreamedContent image = new DefaultStreamedContent();
    private String isverified = new String();
    private String messageText = new String();
    private String remarks = new String();
    REF_DOC_Impl ref_impl = new REF_DOC_Impl();

    public REF_DOC_bean() {
    }

    public void hideDialog() {
        //logger.info("hideDialog Method Called..");
    }

    public void verifyImage() {
        messageText = "";
        if (selected_doc_obj == null) {
            LOGGER.info("something went wrong on the server");
        } else if (isverified == null || isverified.equals("") || isverified.equalsIgnoreCase("-1")) {
            messageText = "Please select one option from Verify / Not Verify";
            FacesContext.getCurrentInstance().addMessage("dialogShowImageErrors", new FacesMessage("Please select one option from Verify / Not Verify"));
        } else {

            boolean isVerifiedLocal = new Boolean(isverified);
            try {
                boolean flag = ref_impl.verifyImage(selected_doc_obj.getAppl_no(), selected_doc_obj.getScan_doc_no(),
                        selected_doc_obj.getScan_doc_purpose_cd(), isVerifiedLocal,
                        Long.parseLong(Util.getEmpCode()), remarks);
                if (flag) {
                    messageText = "Data saved successfully.";
                    //// update list so it can display updated information on the main list.
                    for (int i = 0; i < imagePurposeList.size(); i++) {
                        REF_DOC_PURPOSE_dobj refdoc = imagePurposeList.get(i);
                        if (refdoc.getAppl_no().equals(selected_doc_obj.getAppl_no()) && refdoc.getRef_doc_purpose_cd() == selected_doc_obj.getScan_doc_purpose_cd() && refdoc.getScan_doc_no().equalsIgnoreCase(selected_doc_obj.getScan_doc_no())) {
                            refdoc.setRemarks(remarks);
                            refdoc.setVerified(isVerifiedLocal);
                            Date cur_date = new Date();
                            SimpleDateFormat sdf_dt = new SimpleDateFormat("dd-MM-yyyy hh:mm aaa");
                            String currnet_date_time = sdf_dt.format(cur_date);
                            refdoc.setVerified_on(currnet_date_time);
                            resetDialogForm();
                            break;
                        }

                    }
                }

            } catch (Exception ex) {
                messageText = "There is some problem in updating status of scanned image";

            }
        }
    }

    public void showImage(REF_DOC_PURPOSE_dobj refdoc_purpose) {

        resetDialogForm();
        int refdoc_purpose_cd = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("refdoc_purpose_cd"));
        String appl_no = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("appl_no");
        REF_DOC_Impl impl = new REF_DOC_Impl();
        selected_doc_obj = impl.fetchImageFromDB(appl_no, refdoc_purpose_cd);
        if (selected_doc_obj != null) {

            PrimeFaces.current().ajax().update(":masterLayout:workbench_tabview:ref_doc_subview:dialogShowImage :masterLayout:workbench_tabview:ref_doc_subview:txtRemarks :masterLayout:workbench_tabview:ref_doc_subview:isverified");
            PrimeFaces.current().executeScript("PF('dialogShowImage').show()");
        } else {
            messageText = "Selected image not available. ";
        }
    }

    private void resetDialogForm() {
        messageText = "";
        remarks = "";
        isverified = "-1";
    }

    public void set_REF_DOC_dobj_to_bean(ArrayList<REF_DOC_dobj> imageList, ArrayList<REF_DOC_PURPOSE_dobj> imagePurposeList) {

        try {
            this.imageList = imageList;
            this.imagePurposeList = imagePurposeList;


        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }


    }

    /**
     * @param image the image to set
     */
    public void setImage(StreamedContent image) {
        this.image = image;
    }

    /**
     * @return the imageList
     */
    public ArrayList<REF_DOC_dobj> getImageList() {
        return imageList;
    }

    /**
     * @param imageList the imageList to set
     */
    public void setImageList(ArrayList<REF_DOC_dobj> imageList) {
        this.imageList = imageList;
    }

    /**
     * @return the imagePurposeList
     */
    public ArrayList<REF_DOC_PURPOSE_dobj> getImagePurposeList() {
        return imagePurposeList;
    }

    /**
     * @param imagePurposeList the imagePurposeList to set
     */
    public void setImagePurposeList(ArrayList<REF_DOC_PURPOSE_dobj> imagePurposeList) {
        this.imagePurposeList = imagePurposeList;
    }

    /**
     * @return the sysDate
     */
    public long getSysDate() {
        return System.currentTimeMillis();
    }

    /**
     * @param sysDate the sysDate to set
     */
    public void setSysDate(long sysDate) {
        this.sysDate = sysDate;
    }

    /**
     * @return the ref_doc_source_descr
     */
    public String getRef_doc_source_descr() {
        return ref_doc_source_descr;
    }

    /**
     * @param ref_doc_source_descr the ref_doc_source_descr to set
     */
    public void setRef_doc_source_descr(String ref_doc_source_descr) {
        this.ref_doc_source_descr = ref_doc_source_descr;
    }

    /**
     * @return the selected_doc_obj
     */
    public REF_DOC_dobj getSelected_doc_obj() {
        return selected_doc_obj;
    }

    /**
     * @param selected_doc_obj the selected_doc_obj to set
     */
    public void setSelected_doc_obj(REF_DOC_dobj selected_doc_obj) {
        this.selected_doc_obj = selected_doc_obj;
    }

    /**
     * @return the isverified
     */
    public String getIsverified() {
        return isverified;
    }

    /**
     * @param isverified the isverified to set
     */
    public void setIsverified(String isverified) {
        this.isverified = isverified;
    }

    /**
     * @return the messageText
     */
    public String getMessageText() {
        return messageText;
    }

    /**
     * @param messageText the messageText to set
     */
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
