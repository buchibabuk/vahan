/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.FileUploadRenderer;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.user_mgmt.dobj.UserAuthorityDobj;
import nic.vahan.db.user_mgmt.impl.UserMgmtImpl;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author tranC103
 */
@ManagedBean
@SessionScoped
public class UserAuthorityBean extends FileUploadRenderer implements Serializable {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(UserAuthorityBean.class);
    private boolean regnSeqRender = false;
    private int lowerBound;
    private int upperBound;
    private List vehicleTypeList;
    private List vehClassList;
    private List permitTypeList;
    private List permitCatgList;
    private List dealerList;
    private List makerList;
    private UserAuthorityDobj dobj;
    private boolean renderVehicle = true;
    private boolean renderPermit = false;
    private boolean renderDealer = false;
    private boolean renderAuthPanel = false;
    private boolean renderFitnessCheck = true;
    private boolean renderRegnSeqCheck = true;
    private boolean renderTeam_id = false;
    private boolean renderCheckBoxRow = false;
    private boolean disable = false;
    private boolean disableDealer = false;
    private boolean renderAllOfficeAuth = false;
    private boolean renderSignatureUpload = false;
    private boolean renderedRemoveSignature = false;
    private StreamedContent viewSignFile;

    @PostConstruct
    public void init() {
        vehicleTypeList = new ArrayList();
        vehClassList = new ArrayList();
        permitTypeList = new ArrayList();
        permitCatgList = new ArrayList();
        dealerList = new ArrayList();
        makerList = new ArrayList();
        viewSignFile = new DefaultStreamedContent();

        dobj = new UserAuthorityDobj();

        vehicleTypeList.add(new SelectItem("0", "-SELECT-"));
        vehicleTypeList.add(new SelectItem("1", "Transport"));
        vehicleTypeList.add(new SelectItem("2", "Non-Transport"));
        vehicleTypeList.add(new SelectItem("3", "All"));
        setDisable(true);
        setDisable(true);
    }

    public void regSeqListner() {
        if (regnSeqRender) {
            setDisable(false);
            setDisable(false);
            setLowerBound(1);
            setUpperBound(9999);
            dobj.setLowerBound(getLowerBound());
            dobj.setUpperBound(getUpperBound());
        } else {
            setDisable(true);
            setDisable(true);
            setLowerBound(1);
            setUpperBound(9999);
        }
    }

    public void checkLowerBound() {
        if (lowerBound < 0) {
            setLowerBound(1);
            JSFUtils.showMessage("Invalid Value for Lower Bound");
        } else {
            dobj.setLowerBound(getLowerBound());
        }
    }

    public void checkUpperBound() {
        if ((upperBound < lowerBound) || (upperBound > 9999)) {
            setUpperBound(9999);
            JSFUtils.showMessage("Invalid Value for Upper Bound");
        } else {
            dobj.setUpperBound(getUpperBound());
        }
    }

    public void vehTypeListener() {
        vehClassList.clear();
        if ((dobj.getVehType() == TableConstants.VM_VEHTYPE_TRANSPORT)
                || (dobj.getVehType() == TableConstants.VM_VEHTYPE_ALL)) {
            renderPermit = true;
        } else {
            renderPermit = false;
        }
        if (dobj.getVehType() == TableConstants.VM_VEHTYPE_ALL) {
            String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
            for (int i = 0; i < data.length; i++) {
                vehClassList.add(new SelectItem(data[i][0], data[i][1]));
            }
        } else {
            String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
            for (int i = 0; i < data.length; i++) {
                if (data[i][2].equals(dobj.getVehType() + "")) {
                    vehClassList.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        }
    }

    public void team_idListener() {
        if (dobj.isIsEnforcementOfficer()) {
            setRenderTeam_id(true);
        } else {
            setRenderTeam_id(false);
        }
    }

    public void vehTypeCatgListener() {
        if (dobj.getSelectedVehClass().isEmpty()) {
            for (Object obj : vehClassList) {
                String tmp = (String) ((SelectItem) obj).getValue();
                dobj.getSelectedVehClass().add(tmp);
            }
        }
        List vhClass = dobj.getSelectedVehClass();
        boolean passFlag = false;
        boolean goodsFlag = false;
        String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
        for (Object obj : vhClass) {
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equalsIgnoreCase(String.valueOf(obj))) {
                    if (data[i][3].equalsIgnoreCase("P")) {
                        passFlag = true;
                    } else if (data[i][3].equalsIgnoreCase("G")) {
                        goodsFlag = true;
                    }
                }
            }
        }
        data = MasterTableFiller.masterTables.VM_PERMIT_TYPE.getData();
        if (passFlag && goodsFlag) {
            permitTypeList.clear();
            for (int i = 0; i < data.length; i++) {
                permitTypeList.add(new SelectItem(data[i][0], data[i][1]));
            }
        } else if (passFlag) {
            permitTypeList.clear();
            for (int i = 0; i < data.length; i++) {
                if (data[i][2].equalsIgnoreCase("P")) {
                    permitTypeList.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        } else if (goodsFlag) {
            permitTypeList.clear();
            for (int i = 0; i < data.length; i++) {
                if (data[i][2].equalsIgnoreCase("G")) {
                    permitTypeList.add(new SelectItem(data[i][0], data[i][1]));
                }
            }
        }
    }

    public void permitTypeListener() {
        permitCatgList.clear();
        String[][] data = MasterTableFiller.masterTables.VM_PMT_CATEGORY.getData();
        if (dobj.getPermitType().isEmpty()) {
            for (Object obj : permitTypeList) {
                String tmp = (String) ((SelectItem) obj).getValue();
                dobj.getPermitType().add(tmp);
            }
        }
        for (int j = 0; j < dobj.getPermitType().size(); j++) {
            String tempPermitType = dobj.getPermitType().get(j).toString();
            for (int i = 0; i < data.length; i++) {
                if (data[i][0].equals(Util.getUserStateCode()) && (data[i][3].equals(tempPermitType))) {
                    permitCatgList.add(new SelectItem(data[i][1], data[i][2]));
                }
            }
        }

    }

    public void handelSignatureFile(FileUploadEvent e) {
        try {
            dobj.setSignatureFile(ServerUtil.ImageToByte(e.getFile().getInputstream()));
            setViewSignFile(new DefaultStreamedContent(new ByteArrayInputStream(dobj.getSignatureFile())));
            dobj.setIsSiqnUpload(true);
        } catch (IOException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void removeUserSignature() {
        String msg = "";
        try {
            long userCd =(long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userCd");
            msg = UserMgmtImpl.deleteFromTmUserSign(userCd);
            setRenderedRemoveSignature(false);
            JSFUtils.showMessage(msg);
        } catch (VahanException ve) {
            JSFUtils.showMessage(ve.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }

    }

    public void dealerListener() {
        try {
            UserMgmtImpl implObj = new UserMgmtImpl();
            if (Util.getUserStateCode().equalsIgnoreCase("DL") && !implObj.isSelfDealer(dobj, Util.getUserStateCode())) {
                setRenderAllOfficeAuth(true);
            } else {
                TmConfigurationDobj tmConfig = Util.getTmConfiguration();
                if (tmConfig != null && tmConfig.isDealer_auth_for_all_office()) {
                    setRenderAllOfficeAuth(true);
                } else {
                    setRenderAllOfficeAuth(false);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        if (context.getExternalContext().getRequestContentType().toLowerCase().startsWith("multipart/")) {
            super.decode(context, component);
        }
    }

    public boolean isRegnSeqRender() {
        return regnSeqRender;
    }

    public void setRegnSeqRender(boolean regnSeqRender) {
        this.regnSeqRender = regnSeqRender;
    }

    public UserAuthorityDobj getDobj() {
        return dobj;
    }

    public void setDobj(UserAuthorityDobj dobj) {
        this.dobj = dobj;
    }

    public List getVehicleTypeList() {
        return vehicleTypeList;
    }

    public void setVehicleTypeList(List vehicleTypeList) {
        this.vehicleTypeList = vehicleTypeList;
    }

    public List getVehClassList() {
        return vehClassList;
    }

    public void setVehClassList(List vehClassList) {
        this.vehClassList = vehClassList;
    }

    public List getPermitTypeList() {
        return permitTypeList;
    }

    public void setPermitTypeList(List permitTypeList) {
        this.permitTypeList = permitTypeList;
    }

    public List getPermitCatgList() {
        return permitCatgList;
    }

    public void setPermitCatgList(List permitCatgList) {
        this.permitCatgList = permitCatgList;
    }

    /**
     * @return the renderPermit
     */
    public boolean isRenderPermit() {
        return renderPermit;
    }

    /**
     * @param renderPermit the renderPermit to set
     */
    public void setRenderPermit(boolean renderPermit) {
        this.renderPermit = renderPermit;
    }

    /**
     * @return the renderDealer
     */
    public boolean isRenderDealer() {
        return renderDealer;
    }

    /**
     * @param renderDealer the renderDealer to set
     */
    public void setRenderDealer(boolean renderDealer) {
        this.renderDealer = renderDealer;
    }

    public List getDealerList() {
        return dealerList;
    }

    public void setDealerList(List dealerList) {
        this.dealerList = dealerList;
    }

    public boolean isRenderAuthPanel() {
        return renderAuthPanel;
    }

    public void setRenderAuthPanel(boolean renderAuthPanel) {
        this.renderAuthPanel = renderAuthPanel;
    }

    public List getMakerList() {
        return makerList;
    }

    public void setMakerList(List makerList) {
        this.makerList = makerList;
    }

    public boolean isRenderFitnessCheck() {
        return renderFitnessCheck;
    }

    public void setRenderFitnessCheck(boolean renderFitnessCheck) {
        this.renderFitnessCheck = renderFitnessCheck;
    }

    public boolean isRenderRegnSeqCheck() {
        return renderRegnSeqCheck;
    }

    public void setRenderRegnSeqCheck(boolean renderRegnSeqCheck) {
        this.renderRegnSeqCheck = renderRegnSeqCheck;
    }

    public boolean isRenderTeam_id() {
        return renderTeam_id;
    }

    public void setRenderTeam_id(boolean renderTeam_id) {
        this.renderTeam_id = renderTeam_id;
    }

    /**
     * @return the renderCheckBoxRow
     */
    public boolean isRenderCheckBoxRow() {
        return renderCheckBoxRow;
    }

    /**
     * @param renderCheckBoxRow the renderCheckBoxRow to set
     */
    public void setRenderCheckBoxRow(boolean renderCheckBoxRow) {
        this.renderCheckBoxRow = renderCheckBoxRow;
    }

    /**
     * @return the renderVehicle
     */
    public boolean isRenderVehicle() {
        return renderVehicle;
    }

    /**
     * @param renderVehicle the renderVehicle to set
     */
    public void setRenderVehicle(boolean renderVehicle) {
        this.renderVehicle = renderVehicle;
    }

    /**
     * @return the lowerBound
     */
    public int getLowerBound() {
        return lowerBound;
    }

    /**
     * @param lowerBound the lowerBound to set
     */
    public void setLowerBound(int lowerBound) {
        this.lowerBound = lowerBound;
    }

    /**
     * @return the upperBound
     */
    public int getUpperBound() {
        return upperBound;
    }

    /**
     * @param upperBound the upperBound to set
     */
    public void setUpperBound(int upperBound) {
        this.upperBound = upperBound;
    }

    /**
     * @return the disable
     */
    public boolean isDisable() {
        return disable;
    }

    /**
     * @param disable the disable to set
     */
    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    /**
     * @return the disableDealer
     */
    public boolean isDisableDealer() {
        return disableDealer;
    }

    /**
     * @param disableDealer the disableDealer to set
     */
    public void setDisableDealer(boolean disableDealer) {
        this.disableDealer = disableDealer;
    }

    /**
     * @return the renderAllOfficeAuth
     */
    public boolean isRenderAllOfficeAuth() {
        return renderAllOfficeAuth;
    }

    /**
     * @param renderAllOfficeAuth the renderAllOfficeAuth to set
     */
    public void setRenderAllOfficeAuth(boolean renderAllOfficeAuth) {
        this.renderAllOfficeAuth = renderAllOfficeAuth;
    }

    /**
     * @return the renderSignatureUpload
     */
    public boolean isRenderSignatureUpload() {
        return renderSignatureUpload;
    }

    /**
     * @param renderSignatureUpload the renderSignatureUpload to set
     */
    public void setRenderSignatureUpload(boolean renderSignatureUpload) {
        this.renderSignatureUpload = renderSignatureUpload;
    }

    /**
     * @return the viewSignFile
     */
    public StreamedContent getViewSignFile() {
        return viewSignFile;

    }

    /**
     * @param viewSignFile the viewSignFile to set
     */
    public void setViewSignFile(StreamedContent viewSignFile) {
        this.viewSignFile = viewSignFile;
    }

    /**
     * @return the renderedRemoveSignature
     */
    public boolean isRenderedRemoveSignature() {
        return renderedRemoveSignature;
    }

    /**
     * @param renderedRemoveSignature the renderedRemoveSignature to set
     */
    public void setRenderedRemoveSignature(boolean renderedRemoveSignature) {
        this.renderedRemoveSignature = renderedRemoveSignature;
    }
}
