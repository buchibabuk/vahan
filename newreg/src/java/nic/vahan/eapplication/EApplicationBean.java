/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.eapplication;

import java.io.Serializable;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.TableList;
import nic.vahan.form.dobj.Owner_dobj;
import nic.vahan.form.dobj.Status_dobj;
import nic.vahan.form.dobj.smartcard.SmartCardDobj;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.component.blockui.BlockUI;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "eapplicationBean")
@ViewScoped
public class EApplicationBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(EApplicationBean.class);
    private boolean render = false;
    private EApplicationDobj eApplication_Dobj = new EApplicationDobj();
    private String src = "";
    private int pur_cd;
    private String descr = "XXX";
    private String tableName = "";
    private String msg = "";
    private String appl_no;
    List<Status_dobj> eAppStatusList;
    private String button_id;

    public EApplicationBean() {
//
//        Object obj = FacesContext.getCurrentInstance().getExternalContext().getFlash().get("pur_cd");
//        if (obj == null) {
//            FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "homePage");
//            return;
//        }
//        pur_cd = Integer.parseInt(obj.toString());
//        setPurposeDescr(pur_cd);
    }

    public void showResult() throws VahanException {
        boolean isValid = false;
        String statusNOC = "";
        SmartCardDobj smartCardDobj = null;
        EApplicationImpl impl = null;

        if ((eApplication_Dobj.getRegn_no() != null)
                && (eApplication_Dobj.getChasis_no() != null)
                && (eApplication_Dobj.getChasis_no().length() == 5)) {
            impl = new EApplicationImpl();
            isValid = impl.isValidRegistration(eApplication_Dobj.getRegn_no(), eApplication_Dobj.getChasis_no());

            if (isValid) {

                //START check for registration in NOC state or Entry in Smartcard table
                List<Owner_dobj> ownerInfo = ServerUtil.getExistingOwnerDetails(eApplication_Dobj.getRegn_no());
                if (ownerInfo != null) {
                    statusNOC = ownerInfo.get(0).getStatus();
                }
                if (statusNOC.equalsIgnoreCase(TableConstants.VT_NOC_ISSUE_STATUS)) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Can't Inward Application due to NOC Issued for this Vehicle!!!", "Can't Inward Application due to NOC Issued for this Vehicle!!!"));
                    eApplication_Dobj.setRegn_no("");
                    eApplication_Dobj.setChasis_no("");
                    return;
                }

                smartCardDobj = ServerUtil.getSmartCardDetailsFromRcBeToBo(eApplication_Dobj.getRegn_no());
                if (smartCardDobj != null) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Can't Inward Application due to Smart Card is pending for activation for Registration No " + eApplication_Dobj.getRegn_no() + " against Application No " + smartCardDobj.getAppl_no(), "Can't Inward Application due to Smart Card is pending for activation for Registration No " + eApplication_Dobj.getRegn_no() + " against Application No " + smartCardDobj.getAppl_no()));
                    eApplication_Dobj.setRegn_no("");
                    eApplication_Dobj.setChasis_no("");
                    return;
                }
                //START check for registration in NOC state or Entry in Smartcard table

                eAppStatusList = impl.getEApplicationStatus(eApplication_Dobj.getRegn_no().toUpperCase().trim(), pur_cd, tableName.trim());

                if (!eAppStatusList.isEmpty()) {
                    PrimeFaces.current().dialog().showMessageDynamic(
                            new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!",
                                    "You have Already Applied for " + descr + " and Application No is " + eAppStatusList.get(0).getAppl_no()));
                    return;
                }
            }

            if (isValid) {
                render = isValid;
                eApplication_Dobj.setPur_cd(pur_cd);
                switch (pur_cd) {
                    case TableConstants.VM_TRANSACTION_MAST_CHG_ADD:
                        src = "/ui/registration/form_ca.xhtml";
                        break;
                    case TableConstants.VM_TRANSACTION_MAST_TO:
                        src = "/ui/registration/form_to.xhtml";
                        break;
                    case TableConstants.VM_TRANSACTION_MAST_NOC:
                        src = "/ui/registration/form_noc.xhtml";
                        break;
                    case TableConstants.VM_TRANSACTION_MAST_DUP_RC:
                        src = "/ui/registration/form_dup.xhtml";
                        break;
                    case TableConstants.VM_TRANSACTION_MAST_DUP_FC:
                        src = "/ui/registration/form_dup.xhtml";
                        break;
                    case TableConstants.VM_TRANSACTION_MAST_REM_HYPO:
                        src = "/ui/registration/form_hpt.xhtml";
                        break;
                    case TableConstants.VM_PMT_FRESH_PUR_CD:
                        src = "/ui/permit/form_passenger_permit_detail.xhtml";
                        break;
                    case TableConstants.VM_PMT_TEMP_PUR_CD:
                        src = "/ui/permit/form_Temporary_Permit.xhtml";
                        break;
                    case TableConstants.VM_PMT_SPECIAL_PUR_CD:
                        src = "/ui/permit/form_Temporary_Permit.xhtml";
                        break;
                }//end of switch

            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Combination of Registration/Chasis No is not valid!!!"));
                return;
            }
        } else {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please Enter proper registration no. and Chasis No."));
            return;
        }

        UIComponent component = FacesContext.getCurrentInstance().getViewRoot().findComponent("form_eapp");
        if (component != null) {
            BlockUI block_ui = new BlockUI();
            block_ui.setBlock("pnlgrd_eapp");
            block_ui.setBlocked(true);
            component.getChildren().add(block_ui);
        }

    }

    public void setPurposeDescr(int pur_cd) {
        switch (pur_cd) {
            case TableConstants.VM_TRANSACTION_MAST_CHG_ADD:
                descr = "Change of Address";
                msg = this.getInstruction("CA");
                tableName = TableList.VA_CA;
                break;

            case TableConstants.VM_TRANSACTION_MAST_TO:
                descr = "Transfer of Ownership";
                msg = this.getInstruction("TO");
                tableName = TableList.VA_TO;
                break;

            case TableConstants.VM_TRANSACTION_MAST_NOC:
                descr = "No Objection Certificates";
                msg = this.getInstruction("NOC");
                tableName = TableList.VA_NOC;
                break;

            case TableConstants.VM_TRANSACTION_MAST_DUP_RC:
                descr = "Duplicate Certificate RC";
                msg = this.getInstruction("DUP");
                tableName = TableList.VA_DUP;
                break;

            case TableConstants.VM_TRANSACTION_MAST_DUP_FC:
                descr = "Duplicate Certificate FC";
                msg = this.getInstruction("DUP");
                tableName = TableList.VA_DUP;
                break;

            case TableConstants.VM_TRANSACTION_MAST_REM_HYPO:
                descr = "Hypothecation Termination";
                msg = this.getInstruction("HT");
                tableName = TableList.VA_HPT;
                break;
            case TableConstants.VM_PMT_FRESH_PUR_CD:
                descr = "Fresh/Renewal Permit";
                msg = this.getInstruction("PMT");
                break;
            case TableConstants.VM_PMT_TEMP_PUR_CD:
                descr = "Temporary Permit";
                msg = this.getInstruction("TPMT");
                break;
            case TableConstants.VM_PMT_SPECIAL_PUR_CD:
                descr = "Special Permit";
                msg = this.getInstruction("SPMT");
                break;
        }
    }

    /**
     *
     * @param key It is used for getting key from properties file.
     * @return It Will return value against the key
     *
     */
    public String getInstruction(String key) {

        String result = null;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
            result = bundle.getString(key);
        } catch (MissingResourceException e) {
            result = "???" + key + "??? not found";
        }
        return result;
    }

    /**
     * @return the render
     */
    public boolean isRender() {
        return render;
    }

    /**
     * @param render the render to set
     */
    public void setRender(boolean render) {
        this.render = render;
    }

    /**
     * @return the eApplication_Dobj
     */
    public EApplicationDobj geteApplication_Dobj() {
        return eApplication_Dobj;
    }

    /**
     * @param eApplication_Dobj the eApplication_Dobj to set
     */
    public void seteApplication_Dobj(EApplicationDobj eApplication_Dobj) {
        this.eApplication_Dobj = eApplication_Dobj;
    }

    /**
     * @return the src
     */
    public String getSrc() {
        return src;
    }

    /**
     * @param src the src to set
     */
    public void setSrc(String src) {
        this.src = src;
    }

    /**
     * @return the pur_cd
     */
    public int getPur_cd() {
        return pur_cd;
    }

    /**
     * @param pur_cd the pur_cd to set
     */
    public void setPur_cd(int pur_cd) {
        this.pur_cd = pur_cd;
    }

    /**
     * @return the descr
     */
    public String getDescr() {
        return descr;
    }

    /**
     * @param descr the descr to set
     */
    public void setDescr(String descr) {
        this.descr = descr;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
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
     * @return the button_id
     */
    public String getButton_id() {
        return button_id;
    }

    /**
     * @param button_id the button_id to set
     */
    public void setButton_id(String button_id) {
        this.button_id = button_id;
    }
}
