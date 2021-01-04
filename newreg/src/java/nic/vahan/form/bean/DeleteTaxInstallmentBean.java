/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.java.util.CommonUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.DeleteTaxInstallmentDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.TaxInstallCollectionDobj;
import nic.vahan.form.impl.DeleteTaxInstallmentImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.TaxInstallCollectionImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;
//import org.primefaces.context.RequestContext;
import org.primefaces.PrimeFaces;

/**
 *
 * @author DELL
 */
@ViewScoped
@ManagedBean(name = "deleteTaxInstallmentBean")
public class DeleteTaxInstallmentBean implements Serializable {

    private static final Logger logger = Logger.getLogger(DeleteTaxInstallmentBean.class);
    private String regnNo;
    private String applNo;
    private String stateCd;
    private String empCd;
    private int offCode;
    private OwnerDetailsDobj ownerDetail;
    private boolean render = false;
    private String remark;
    private boolean renderBackBtm = true;
    private boolean renderReset = false;
    private Owner_dobj ownerDobj;
    private Date maxDate = new Date();
    private DeleteTaxInstallmentDobj deleteTaxInstallmentDobj = null;
    DeleteTaxInstallmentImpl deleteTaxInstallmentImpl = new DeleteTaxInstallmentImpl();

    @PostConstruct
    public void init() {
        deleteTaxInstallmentDobj = new DeleteTaxInstallmentDobj();
    }

    public void showDetails() {
        try {
            deleteTaxInstallmentDobj = new DeleteTaxInstallmentDobj();
            String msg = "";
            remark = "";
            stateCd = Util.getUserStateCode();
            offCode = Util.getSelectedSeat().getOff_cd();
            empCd = Util.getEmpCode();
            if (this.empCd == null || this.empCd.trim().equalsIgnoreCase("") || this.stateCd == null || this.stateCd.trim().equalsIgnoreCase("")) {
                msg = "Session Expired. Please try again.";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }

            OwnerImpl owner_Impl = new OwnerImpl();
            if (this.regnNo == null || this.regnNo.trim().equalsIgnoreCase("")) {
                msg = "Please Enter Valid Registration Number";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
            ownerDetail = owner_Impl.getOwnerDetails(this.regnNo.trim());
            msg = " Vehicle Details not Found !";
            if (ownerDetail == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
            if (ownerDetail != null && (!ownerDetail.getState_cd().equals(Util.getUserStateCode()) || ownerDetail.getOff_cd() != Util.getSelectedSeat().getOff_cd())) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
            String newRegnAppl = deleteTaxInstallmentImpl.getRegnApplNoFromTaxInstallmt(regnNo, ownerDetail.getState_cd(), ownerDetail.getOff_cd());
            if (!CommonUtils.isNullOrBlank(newRegnAppl)) {
                setApplNo(newRegnAppl);
            } else {
                msg = "Tax Installment not Found !";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", msg));
                return;
            }
            DeleteTaxInstallmentImpl deleteTaxInstallmentImpl = new DeleteTaxInstallmentImpl();
            deleteTaxInstallmentImpl.getPendingTaxIntlmnt(regnNo.trim());
            List<TaxInstallCollectionDobj> taxInstallCollectionDobj = TaxInstallCollectionImpl.getPendingInstallmentList(regnNo.trim(), null, ownerDobj);
            if (!taxInstallCollectionDobj.isEmpty()) {
                deleteTaxInstallmentDobj.setFilerefNo(taxInstallCollectionDobj.get(0).getFileRefNo());
                deleteTaxInstallmentDobj.setOrderIssueBy(taxInstallCollectionDobj.get(0).getOrderIssBy());
                deleteTaxInstallmentDobj.setOrderDate(taxInstallCollectionDobj.get(0).getOrderDate());
                deleteTaxInstallmentDobj.setOrderNo(taxInstallCollectionDobj.get(0).getOrderNo());
            }
            setRender(true);
            setRenderBackBtm(false);
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ve.getMessage()));
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ex.getMessage()));
        }
    }

    public void detleteTaxInstallment() {
        try {
            String msg = "";
            if (CommonUtils.isNullOrBlank(deleteTaxInstallmentDobj.getRemark()) || deleteTaxInstallmentDobj.getRemark().trim().length() < 5) {
                throw new VahanException("Remarks Field Can not be empty and Minimum 5 Character is Mandatory.");
            }
            deleteTaxInstallmentDobj.setEmpCode(empCd);
            deleteTaxInstallmentDobj.setStateCd(stateCd);
            deleteTaxInstallmentDobj.setOffcd(ownerDetail.getOff_cd());
            deleteTaxInstallmentDobj.setApplNo(applNo);
            deleteTaxInstallmentDobj.setRegnNo(regnNo);
            deleteTaxInstallmentImpl.insertOrDeleteTaxInstmnt(deleteTaxInstallmentDobj);
            String facesMessages = " Tax Installment Deleted Successfully.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, facesMessages, facesMessages));
            PrimeFaces.current().ajax().update("msgDialog");
            PrimeFaces.current().executeScript("PF('messageDialog').show();");
            setRenderBackBtm(true);
            setRender(false);
            setRenderReset(true);
            setRegnNo("");
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ve.getMessage()));
        } catch (Exception ex) {
            logger.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Alert!", ex.getMessage()));
        }

    }

    public void reset() {
        ownerDetail = null;
        applNo = "";
        regnNo = "";
        remark = "";
        setRenderBackBtm(true);
        setRender(false);
        setRenderReset(false);
        deleteTaxInstallmentDobj = null;
    }

    public String getRegnNo() {
        return regnNo;
    }

    public void setRegnNo(String regnNo) {
        this.regnNo = regnNo;
    }

    public String getApplNo() {
        return applNo;
    }

    public void setApplNo(String applNo) {
        this.applNo = applNo;
    }

    public String getStateCd() {
        return stateCd;
    }

    public void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    public String getEmpCd() {
        return empCd;
    }

    public void setEmpCd(String empCd) {
        this.empCd = empCd;
    }

    public int getOffCode() {
        return offCode;
    }

    public void setOffCode(int offCode) {
        this.offCode = offCode;
    }

    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }

    public boolean isRender() {
        return render;
    }

    public void setRender(boolean render) {
        this.render = render;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isRenderBackBtm() {
        return renderBackBtm;
    }

    public void setRenderBackBtm(boolean renderBackBtm) {
        this.renderBackBtm = renderBackBtm;
    }

    public boolean isRenderReset() {
        return renderReset;
    }

    public void setRenderReset(boolean renderReset) {
        this.renderReset = renderReset;
    }

    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public DeleteTaxInstallmentDobj getDeleteTaxInstallmentDobj() {
        return deleteTaxInstallmentDobj;
    }

    public void setDeleteTaxInstallmentDobj(DeleteTaxInstallmentDobj deleteTaxInstallmentDobj) {
        this.deleteTaxInstallmentDobj = deleteTaxInstallmentDobj;
    }
}
