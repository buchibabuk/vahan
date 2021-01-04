/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.bean.common.AbstractApplBean;
import nic.vahan.form.dobj.AdvertisementOnVehicleDobj;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.impl.AdvertisementOnVehicleImpl;
import nic.vahan.form.impl.ComparisonBeanImpl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.server.ServerUtil;
import static nic.vahan.server.ServerUtil.Compare;
import org.apache.log4j.Logger;
//import org.primefaces.context.RequestContext;
import org.primefaces.PrimeFaces;

/**
 *
 * @author acer
 */
@ManagedBean(name = "adv_veh_bean")
@ViewScoped
public class AdvertisementOnVehicleBean extends AbstractApplBean implements Serializable, ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(AltBean.class);
    private List list_ow_catg = new ArrayList();
    private OwnerDetailsDobj ownerDetail;
    private List listOwnerCatg = new ArrayList();
    private boolean isPermitVehicle;
    private List<ComparisonBean> prevChangedDataList;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private AdvertisementOnVehicleDobj adv_dobj_prv = null;
    private AdvertisementOnVehicleDobj adv_dobj = new AdvertisementOnVehicleDobj();
    private OwnerIdentificationDobj owner_identification = new OwnerIdentificationDobj();
    private OwnerIdentificationDobj ownerIdentificationPrev = null;
    private ArrayList<OwnerDetailsDobj> regnNameList = null;
    private boolean showRegList = false;
    @ManagedProperty(value = "#{permitDtls}")
    private PermitDisplayDetailBean permitDetailBean;
    private String tf_permit_no = null;
    private String tf_RTA = null;
    private String tf_permit_upto = null;
    private Owner_dobj ownerDobj;
    private String tf_ad_from_dt = null;
    private String tf_ad_upto_dt = null;

    public AdvertisementOnVehicleBean() {

        if (getAppl_details() == null
                || getAppl_details().getCurrent_state_cd() == null
                || getAppl_details().getCurrent_off_cd() == 0) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", TableConstants.SomthingWentWrong));
            return;
        }

    }

    @PostConstruct
    public void init() {
        try {
            OwnerImpl owner_Impl = new OwnerImpl();

            setRegnNameList((ArrayList<OwnerDetailsDobj>) owner_Impl.getAllRegnNoFromAllStatesAndRto(appl_details.getRegn_no()));
            if (getRegnNameList().isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "The registration details for this no. is not available, as the number is not allotted to any vehicle."));
                return;
            }
            if (getRegnNameList().size() > 1) {
                setShowRegList(true);
            } else {
                showDetails(appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd());
            }

        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

    public void showDetails(String stateCd, int offCd) {

        Exception ex = null;

        try {

            if (appl_details.getOwnerDetailsDobj() == null) {
                OwnerImpl owner_Impl = new OwnerImpl();
                ownerDetail = owner_Impl.getOwnerDetails(appl_details.getRegn_no(), appl_details.getCurrent_state_cd(), appl_details.getCurrent_off_cd());
                if (ownerDetail == null) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Owner Details not Found in the Database, Please Contact to the System Administrator"));
                    return;
                }
            } else {
                ownerDetail = appl_details.getOwnerDetailsDobj();
            }

            //for Owner Identification Fields disallow typing
            if (ownerDetail.getOwnerIdentity() != null) {
                ownerDetail.getOwnerIdentity().setFlag(true);
                ownerDetail.getOwnerIdentity().setMobileNoEditable(true);
                ownerDetail.getOwnerIdentity().setOwnerCatgEditable(true);
            }

            if (getPermitDetailBean() != null && ownerDetail.getVehType() == TableConstants.VM_VEHTYPE_TRANSPORT) {
                isPermitVehicle = getPermitDetailBean().setRegNo(appl_details.getRegn_no());
                tf_permit_no = permitDetailBean.getPermitNo();
                tf_RTA = ownerDetail.getOff_name();
                tf_permit_upto = permitDetailBean.getValidUpto();
            }

            AdvertisementOnVehicleImpl adv_Impl = new AdvertisementOnVehicleImpl();

            adv_dobj = adv_Impl.set_VAAdvertisementOnVehicle_appl_to_dobj(appl_details.getAppl_no());
            if (adv_dobj != null) {
                tf_ad_from_dt = adv_dobj.getFrom_dt().toString();
                tf_ad_upto_dt = adv_dobj.getUpto_dt().toString();
            }

            render = true;
            ownerDetail.setVehTypeDescr("NONTRansport");

        } catch (VahanException ve) {
            ex = ve;
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", ex.getMessage()));
        } catch (Exception exp) {
            ex = exp;
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", TableConstants.SomthingWentWrong));
        }
    }

    @Override
    public List<ComparisonBean> compareChanges() {

        if (this.adv_dobj_prv == null) {
            return compBeanList;
        }
        compBeanList.clear(); // for clearing the list in case of clicking compare changes button again and again.

        Compare("From_dt", adv_dobj_prv.getFrom_dt(), adv_dobj.getFrom_dt(), compBeanList);
        Compare("Upto_dt", adv_dobj_prv.getUpto_dt(), adv_dobj.getUpto_dt(), compBeanList);

        return compBeanList;

    }

    @Override
    public String save() {
        String return_location = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            Date applDate = format.parse(appl_details.getAppl_dt());

            List<ComparisonBean> compareChanges = compareChanges();
            adv_dobj.setAppl_no(appl_details.getAppl_no());
            adv_dobj.setRegn_no(appl_details.getRegn_no());

            if (!compareChanges.isEmpty() || getAdv_dobj_prv() == null) { //save only when data is really changed by user
                AdvertisementOnVehicleImpl impl = new AdvertisementOnVehicleImpl();
                impl.makeChangeAdvertisementOnVehicle(getAdv_dobj(), getOwner_identification(), ComparisonBeanImpl.changedDataContents(compareChanges), applDate);
            }
            return_location = "seatwork";
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
        }
        return return_location;
    }

    @Override
    public String saveAndMoveFile() {
        String return_location = "";
        AdvertisementOnVehicleImpl adv_Impl = null;
        try {

            Status_dobj status = new Status_dobj();
            status.setAppl_dt(appl_details.getAppl_dt());
            status.setAppl_no(appl_details.getAppl_no());
            status.setPur_cd(appl_details.getPur_cd());
            status.setOffice_remark(getApp_disapp_dobj().getOffice_remark() == null ? " " : getApp_disapp_dobj().getOffice_remark());
            status.setPublic_remark(getApp_disapp_dobj().getPublic_remark() == null ? " " : getApp_disapp_dobj().getPublic_remark());
            status.setStatus(getApp_disapp_dobj().getNew_status());
            status.setCurrent_role(appl_details.getCurrent_role());
            status.setAction_cd(appl_details.getCurrent_action_cd());

            if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_COMPLETE)
                    || getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_REVERT)
                    || getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {

                status.setPrev_action_cd_selected(getApp_disapp_dobj().getPre_action_code());

                List<ComparisonBean> compareChanges = compareChanges();

                adv_dobj.setAppl_no(appl_details.getAppl_no());
                adv_dobj.setRegn_no(appl_details.getRegn_no());

                adv_Impl = new AdvertisementOnVehicleImpl();
                status.setVehicleParameters(appl_details.getVehicleParameters());
                status.setPrev_action_cd(appl_details.getCurrent_action_cd());
                adv_Impl.update_Advertisement_Status(adv_dobj, adv_dobj_prv, status, ComparisonBeanImpl.changedDataContents(compareChanges()), getOwnerDobj(), appl_details);

                if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_DISPATCH_PENDING)) {
                    return_location = disapprovalPrint();
                } else {
                    return_location = "seatwork";
                }

            }
            if (getApp_disapp_dobj().getNew_status().trim().equals(TableConstants.STATUS_COMPLETE) && appl_details.getCurrent_role() == 4) {
                if (!ServerUtil.getDataEntryIncomplete(appl_details.getAppl_no())) {
                    PrimeFaces.current().ajax().update("app_disapp_new_form:showOwnerDiscPopup");
                    PrimeFaces.current().executeScript("PF('successDialog').show()");
                    // RequestContext.getCurrentInstance().update(":app_disapp_form:showOwnerDiscPopup");
                    //RequestContext.getCurrentInstance().execute("PF('successDialog').show()");
                    return_location = "";
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error-File Could Not Save and Move Due to Technical Error in Database", "Error-File Could Not Save and Move Due to Technical Error in Database"));
        }
        return return_location;
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the list_ow_catg
     */
    public List getList_ow_catg() {
        return list_ow_catg;
    }

    /**
     * @param list_ow_catg the list_ow_catg to set
     */
    public void setList_ow_catg(List list_ow_catg) {
        this.list_ow_catg = list_ow_catg;
    }

    /**
     * @return the ownerDetail
     */
    public OwnerDetailsDobj getOwnerDetail() {
        return ownerDetail;
    }

    /**
     * @param ownerDetail the ownerDetail to set
     */
    public void setOwnerDetail(OwnerDetailsDobj ownerDetail) {
        this.ownerDetail = ownerDetail;
    }

    /**
     * @return the listOwnerCatg
     */
    public List getListOwnerCatg() {
        return listOwnerCatg;
    }

    /**
     * @param listOwnerCatg the listOwnerCatg to set
     */
    public void setListOwnerCatg(List listOwnerCatg) {
        this.listOwnerCatg = listOwnerCatg;
    }

    /**
     * @return the isPermitVehicle
     */
    public boolean isIsPermitVehicle() {
        return isPermitVehicle;
    }

    /**
     * @param isPermitVehicle the isPermitVehicle to set
     */
    public void setIsPermitVehicle(boolean isPermitVehicle) {
        this.isPermitVehicle = isPermitVehicle;
    }

    /**
     * @return the prevChangedDataList
     */
    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        return prevChangedDataList;
    }

    /**
     * @param prevChangedDataList the prevChangedDataList to set
     */
    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        this.prevChangedDataList = prevChangedDataList;
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
     * @return the owner_identification
     */
    public OwnerIdentificationDobj getOwner_identification() {
        return owner_identification;
    }

    /**
     * @param owner_identification the owner_identification to set
     */
    public void setOwner_identification(OwnerIdentificationDobj owner_identification) {
        this.owner_identification = owner_identification;
    }

    /**
     * @return the ownerIdentificationPrev
     */
    public OwnerIdentificationDobj getOwnerIdentificationPrev() {
        return ownerIdentificationPrev;
    }

    /**
     * @param ownerIdentificationPrev the ownerIdentificationPrev to set
     */
    public void setOwnerIdentificationPrev(OwnerIdentificationDobj ownerIdentificationPrev) {
        this.ownerIdentificationPrev = ownerIdentificationPrev;
    }

    /**
     * @return the regnNameList
     */
    public ArrayList<OwnerDetailsDobj> getRegnNameList() {
        return regnNameList;
    }

    /**
     * @param regnNameList the regnNameList to set
     */
    public void setRegnNameList(ArrayList<OwnerDetailsDobj> regnNameList) {
        this.regnNameList = regnNameList;
    }

    /**
     * @return the showRegList
     */
    public boolean isShowRegList() {
        return showRegList;
    }

    /**
     * @param showRegList the showRegList to set
     */
    public void setShowRegList(boolean showRegList) {
        this.showRegList = showRegList;
    }

    /**
     * @return the permitDetailBean
     */
    public PermitDisplayDetailBean getPermitDetailBean() {
        return permitDetailBean;
    }

    /**
     * @param permitDetailBean the permitDetailBean to set
     */
    public void setPermitDetailBean(PermitDisplayDetailBean permitDetailBean) {
        this.permitDetailBean = permitDetailBean;
    }

    /**
     * @return the tf_permit_no
     */
    public String getTf_permit_no() {
        return tf_permit_no;
    }

    /**
     * @param tf_permit_no the tf_permit_no to set
     */
    public void setTf_permit_no(String tf_permit_no) {
        this.tf_permit_no = tf_permit_no;
    }

    /**
     * @return the tf_RTA
     */
    public String getTf_RTA() {
        return tf_RTA;
    }

    /**
     * @param tf_RTA the tf_RTA to set
     */
    public void setTf_RTA(String tf_RTA) {
        this.tf_RTA = tf_RTA;
    }

    /**
     * @return the tf_permit_upto
     */
    public String getTf_permit_upto() {
        return tf_permit_upto;
    }

    /**
     * @param tf_permit_upto the tf_permit_upto to set
     */
    public void setTf_permit_upto(String tf_permit_upto) {
        this.tf_permit_upto = tf_permit_upto;
    }

    /**
     * @return the adv_dobj_prv
     */
    public AdvertisementOnVehicleDobj getAdv_dobj_prv() {
        return adv_dobj_prv;
    }

    /**
     * @param adv_dobj_prv the adv_dobj_prv to set
     */
    public void setAdv_dobj_prv(AdvertisementOnVehicleDobj adv_dobj_prv) {
        this.adv_dobj_prv = adv_dobj_prv;
    }

    /**
     * @return the adv_dobj
     */
    public AdvertisementOnVehicleDobj getAdv_dobj() {
        return adv_dobj;
    }

    /**
     * @param adv_dobj the adv_dobj to set
     */
    public void setAdv_dobj(AdvertisementOnVehicleDobj adv_dobj) {
        this.adv_dobj = adv_dobj;
    }

    /**
     * @return the ownerDobj
     */
    public Owner_dobj getOwnerDobj() {
        return ownerDobj;
    }

    /**
     * @param ownerDobj the ownerDobj to set
     */
    public void setOwnerDobj(Owner_dobj ownerDobj) {
        this.ownerDobj = ownerDobj;
    }

    /**
     * @return the tf_ad_from_dt
     */
    public String getTf_ad_from_dt() {
        return tf_ad_from_dt;
    }

    /**
     * @param tf_ad_from_dt the tf_ad_from_dt to set
     */
    public void setTf_ad_from_dt(String tf_ad_from_dt) {
        this.tf_ad_from_dt = tf_ad_from_dt;
    }

    /**
     * @return the tf_ad_upto_dt
     */
    public String getTf_ad_upto_dt() {
        return tf_ad_upto_dt;
    }

    /**
     * @param tf_ad_upto_dt the tf_ad_upto_dt to set
     */
    public void setTf_ad_upto_dt(String tf_ad_upto_dt) {
        this.tf_ad_upto_dt = tf_ad_upto_dt;
    }
}
