/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.ApplRegStatus_dobj;
import javax.faces.bean.ManagedProperty;
import nic.vahan.form.impl.ApplRegStatus_Impl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import nic.vahan.services.DmsDocCheckUtils;
import nic.vahan.services.VTDocumentModel;
import org.apache.log4j.Logger;

import org.primefaces.PrimeFaces;

/**
 *
 * @author nic5912
 */
@ManagedBean(name = "applRegStatus")
@ViewScoped
public class ApplRegStatus_bean implements Serializable {

    private static Logger LOGGER = Logger.getLogger(ApplRegStatus_bean.class);
    private String appl_no;
    private String regno;
    private int maxlen = 16;
    private String chasi_no;
    private String state_cd;
    private int off_cd;
    private List<ApplRegStatus_dobj> curlist = new ArrayList();
    private List<ApplRegStatus_dobj> sellist = new ArrayList();
    private List<ApplRegStatus_dobj> statusList = new ArrayList();
    private List<ApplRegStatus_dobj> filterValue = new ArrayList();
    private boolean applStatus;
    private boolean applCurStatus;
    private String option = "applno";
    private String entry_text;
    private boolean documentViewButton = false;
    @ManagedProperty(value = "#{documentUploadBean}")
    private DocumentUploadBean documentUpload_bean;

    public ApplRegStatus_bean() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("redirectTo", "documentsStatus");
            state_cd = Util.getUserStateCode();
            off_cd = Util.getUserOffCode();
        } catch (Exception e) {
        }
    }

    @PostConstruct
    public void init() {
        resettables();
    }

    public void resettables() {
        curlist.clear();
        sellist.clear();
        statusList.clear();
    }

    public void setCurList(List<ApplRegStatus_dobj> listDobjs) {

        for (int i = 0; i < listDobjs.size(); i++) {
            ApplRegStatus_dobj bean = new ApplRegStatus_dobj();
            ApplRegStatus_dobj dobj = listDobjs.get(i);
            bean.setAppl_no(dobj.getAppl_no());
            bean.setPurCd(dobj.getPurCd());
            bean.setPurCdDescr(dobj.getPurCdDescr());
            bean.setRegno(dobj.getRegno());
            bean.setStatusDesc(dobj.getStatusDesc());
            bean.setApprovalStatus(dobj.getApprovalStatus());
            bean.setAppl_dt(dobj.getAppl_dt());
            bean.setRcPrinted(dobj.getRcPrinted());
            bean.setFcPrinted(dobj.getFcPrinted());
            bean.setHsrpDone(dobj.getHsrpDone());
            bean.setSmartCardDone(dobj.getSmartCardDone());
            bean.setDealerCd(dobj.getDealerCd());
            bean.setDispatchRCDone(dobj.getDispatchRCDone());
            bean.setOffName(dobj.getOffName());
            getCurlist().add(bean);

        }
    }

    public void updatemaxlen() {
        entry_text = "";
        curlist.clear();
        sellist.clear();
        statusList.clear();
        if (documentUpload_bean != null && documentUpload_bean.getDocDescrList() != null) {
            documentUpload_bean.getDocDescrList().clear();
        }
        documentViewButton = false;
        if (option.equalsIgnoreCase("applno")) {
            maxlen = 16;
        } else if (option.equalsIgnoreCase("regno")) {
            maxlen = 10;
        } else if (option.equalsIgnoreCase("chasino")) {
            maxlen = 30;
        }
    }

    public void setStatuslist(List<ApplRegStatus_dobj> listDobjs) {

        for (int i = 0; i < listDobjs.size(); i++) {
            ApplRegStatus_dobj bean = new ApplRegStatus_dobj();
            ApplRegStatus_dobj dobj = listDobjs.get(i);
            bean.setOffice(dobj.getOffice());
            bean.setPurCdDescr(dobj.getPurCdDescr());
            bean.setStatusDesc(dobj.getStatusDesc());
            bean.setAction_descr(dobj.getAction_descr());
            bean.setOffice_remark(dobj.getOffice_remark());
            bean.setPublicRemark(dobj.getPublicRemark());
            bean.setEntered_by(dobj.getEntered_by());
            bean.setEntered_on(dobj.getEntered_on());
            bean.setEntry_ip((dobj.getEntry_ip()));

            getStatusList().add(bean);
        }
    }

    public void showList() {
        try {
            resettables();
            if (statusList.size() > 0) {
                statusList.clear();
            }
            setApplStatus(false);
            appl_no = "";
            regno = "";
            chasi_no = "";

            if ("applno".equalsIgnoreCase(option)) {
                appl_no = entry_text;

            } else if ("regno".equalsIgnoreCase(option)) {
                regno = entry_text;

            } else if ("chasino".equalsIgnoreCase(option)) {
                chasi_no = entry_text;

            }

            // Appl Status for State Admin 11-7-2017
            if (Util.getUserCategory().equals(TableConstants.USER_CATG_STATE_ADMIN) && off_cd == 0) {
                off_cd = ApplRegStatus_Impl.getShowRtoDetails(entry_text, state_cd, option);
                if (off_cd == 0) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "There is no application details in VAHAN 4.0"));
                    return;
                }
            }

            if (chasi_no.length() > 0) {
                appl_no = ApplRegStatus_Impl.getApplNo(chasi_no, state_cd, off_cd);
                if (appl_no == null) {
                    regno = ApplRegStatus_Impl.getRegnNo(chasi_no, state_cd, off_cd);
                }
            }

            Long user_cd = Long.parseLong(Util.getEmpCode());
            String dealerCd = ServerUtil.getDealerCode(user_cd, state_cd, off_cd);
            if ((appl_no != null && !appl_no.equals("")) || (regno != null && !regno.equals(""))) {
                setCurList(ApplRegStatus_Impl.getCurrentStatus(appl_no, regno, state_cd, off_cd));
            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "There is no application details in VAHAN 4.0"));
                return;
            }
            if (getCurlist().isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "There is no application details in VAHAN 4.0"));
            }
            if (Util.getUserCategory() != null && Util.getUserCategory().equals(TableConstants.User_Dealer) && !getCurlist().isEmpty()) {
                if (!dealerCd.equals(getCurlist().get(0).getDealerCd())) {
                    curlist.clear();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "You are Not Authorised to see the Application Status of other Dealer's Application."));
                }
            }

            if (Util.getUserCategory() != null && (Util.getUserCategory().equals(TableConstants.User_Dealer) || TableConstants.USER_CATG_OFF_STAFF.equals(Util.getUserCategory()))) {
                if (chasi_no.length() > 0) {
                    if (appl_no == null && regno != null) {
                        appl_no = ApplRegStatus_Impl.getApplNoByRegnNo(regno, state_cd, off_cd, Util.getUserCategory());
                    }
                } else if (appl_no.isEmpty() && regno != null) {
                    appl_no = ApplRegStatus_Impl.getApplNoByRegnNo(regno, state_cd, off_cd, Util.getUserCategory());
                }
                if (documentUpload_bean != null) {
                    List<VTDocumentModel> docDescrList = DmsDocCheckUtils.getUploadedDocumentList(appl_no);
                    if (docDescrList != null && !docDescrList.isEmpty() && curlist != null && !curlist.isEmpty()) {
                        documentUpload_bean.setDocDescrList(docDescrList);
                        documentUpload_bean.setApplNo(appl_no);
                        setDocumentViewButton(true);
                        if (Util.getTmConfiguration() != null && Util.getTmConfiguration().getTmConfigDmsDobj() != null) {
                            boolean isApiBasedDocUpload = Util.getTmConfiguration().getTmConfigDmsDobj().isApiBasedDocUpload();
                            if (isApiBasedDocUpload) {
                                documentUpload_bean.setRenderApiBasedDMSDocPanel(true);
                                documentUpload_bean.setRenderUiBasedDMSDocPanel(false);
                            } else {
                                documentUpload_bean.setRenderApiBasedDMSDocPanel(false);
                                documentUpload_bean.setRenderUiBasedDMSDocPanel(true);
                            }
                        }
                    } else {
                        setDocumentViewButton(false);
                    }
                }
            }
        } catch (VahanException ex) {
            JSFUtils.setFacesMessage(ex.getMessage(), ex.getMessage(), JSFUtils.WARN);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void showDetail(ApplRegStatus_dobj dobj) {
        Map map;
        map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String applNo = (String) map.get("appl_no");
        String regn_no = (String) map.get("regnno");
        String purCd = (String) map.get("pur_cd");
        if (CommonUtils.isNullOrBlank(applNo)) {
            applNo = "";
        }
        if (CommonUtils.isNullOrBlank(regn_no)) {
            regn_no = "";
        }
        if (CommonUtils.isNullOrBlank(purCd)) {
            purCd = "0";
        }
        try {
            getStatusList().clear();
            sellist.clear();
            statusList = ApplRegStatus_Impl.getOldStatus(applNo, regn_no, purCd);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("status_bean", getStatusList());
            sellist.add(dobj);
            if (statusList.size() > 0) {
                applStatus = true;
            }
            if (sellist.size() > 0) {
                applCurStatus = true;
            }
        } catch (VahanException ex) {
            JSFUtils.setFacesMessage(ex.getMessage(), ex.getMessage(), JSFUtils.WARN);
            return;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public int getMaxlen() {
        return maxlen;
    }

    public void setMaxlen(int maxlen) {
        this.maxlen = maxlen;
    }

    /**
     * @return the option
     */
    public String getOption() {
        return option;
    }

    /**
     * @param option the option to set
     */
    public void setOption(String option) {
        this.option = option;
    }

    /**
     * @return the entry_text
     */
    public String getEntry_text() {
        return entry_text;
    }

    /**
     * @param entry_text the entry_text to set
     */
    public void setEntry_text(String entry_text) {
        this.entry_text = entry_text;
    }

    /**
     * @return the documentUpload_bean
     */
    public DocumentUploadBean getDocumentUpload_bean() {
        return documentUpload_bean;
    }

    /**
     * @param documentUpload_bean the documentUpload_bean to set
     */
    public void setDocumentUpload_bean(DocumentUploadBean documentUpload_bean) {
        this.documentUpload_bean = documentUpload_bean;
    }

    /**
     * @return the documentViewButton
     */
    public boolean isDocumentViewButton() {
        return documentViewButton;
    }

    /**
     * @param documentViewButton the documentViewButton to set
     */
    public void setDocumentViewButton(boolean documentViewButton) {
        this.documentViewButton = documentViewButton;
    }

    public class SelectOneView {

        private String option;

        public String getOption() {
            return option;
        }

        public void setOption(String option) {
            this.option = option;
        }
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

    /**
     * @return the regno
     */
    public String getRegno() {
        return regno;
    }

    /**
     * @param regno the regno to set
     */
    public void setRegno(String regno) {
        this.regno = regno;
    }

    /**
     * @return the applStatus
     */
    public boolean isApplStatus() {
        return applStatus;
    }

    /**
     * @param applStatus the applStatus to set
     */
    public void setApplStatus(boolean applStatus) {
        this.applStatus = applStatus;
    }

    /**
     * @return the statusList
     */
    public List<ApplRegStatus_dobj> getStatusList() {
        return statusList;
    }

    /**
     * @param statusList the statusList to set
     */
    public void setStatusList(List<ApplRegStatus_dobj> statusList) {
        this.statusList = statusList;
    }

    /**
     * @return the curlist
     */
    public List<ApplRegStatus_dobj> getCurlist() {
        return curlist;
    }

    /**
     * @param curlist the curlist to set
     */
    public void setCurlist(List<ApplRegStatus_dobj> curlist) {
        this.curlist = curlist;
    }

    /**
     * @return the filterValue
     */
    public List<ApplRegStatus_dobj> getFilterValue() {
        return filterValue;
    }

    /**
     * @param filterValue the filterValue to set
     */
    public void setFilterValue(List<ApplRegStatus_dobj> filterValue) {
        this.filterValue = filterValue;
    }

    /**
     * @return the chasi_no
     */
    public String getChasi_no() {
        return chasi_no;
    }

    /**
     * @param chasi_no the chasi_no to set
     */
    public void setChasi_no(String chasi_no) {
        this.chasi_no = chasi_no;
    }

    public List<ApplRegStatus_dobj> getSellist() {
        return sellist;
    }

    public void setSellist(List<ApplRegStatus_dobj> sellist) {
        this.sellist = sellist;
    }

    /**
     * @return the applCurStatus
     */
    public boolean isApplCurStatus() {
        return applCurStatus;
    }

    /**
     * @param applCurStatus the applCurStatus to set
     */
    public void setApplCurStatus(boolean applCurStatus) {
        this.applCurStatus = applCurStatus;
    }
}
