/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db.user_mgmt.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.SessionVariables;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.db.user_mgmt.dobj.UserInformationDobj;
import nic.vahan.db.user_mgmt.impl.UserInformationImpl;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author acer
 */
@ManagedBean(name = "userinformationbean")
@ViewScoped
public class UserInformationBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(UserInformationBean.class);
    private UserInformationDobj userinfo = new UserInformationDobj();
    private SessionVariables sessionVariables = null;
    private String state_name;
    private String officeName;
    private String userOfficeName;
    private String userName;
    private String user_catg;
    private boolean renderOffice = false;
    private List filterList = null;
    private String msg = "";
    private List<SelectItem> officeList = null;
    private List<UserInformationDobj> listOfficeAdmin = new ArrayList<>();
    private List<UserInformationDobj> listUser = new ArrayList<>();
    private List<UserInformationDobj> dealerUser = new ArrayList<>();
    private List<UserInformationDobj> dealerStaff = new ArrayList<>();
    private List<UserInformationDobj> actionlist = new ArrayList<>();
    private List<UserInformationDobj> offtiminglist = new ArrayList<>();
    private List<UserInformationDobj> offiplist = new ArrayList<>();
    private List<UserInformationDobj> blockUserList = new ArrayList<>();
    private List<UserInformationDobj> ipDetailList = new ArrayList<>();
    UserInformationImpl userinformationimpl = new UserInformationImpl();
    private UserInformationDobj dobjOfficeAdmin = null;
    private UserInformationDobj dobjofftiming = null;

    public UserInformationBean() {
        try {
            sessionVariables = new SessionVariables();
            if (sessionVariables == null
                    || sessionVariables.getStateCodeSelected() == null
                    || sessionVariables.getEmpCodeLoggedIn() == null) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            } else {
                state_name = MasterTableFiller.state.get(sessionVariables.getStateCodeSelected()).getStateDescr();
                officeList = (ArrayList<SelectItem>) ((ArrayList<SelectItem>) MasterTableFiller.state.get(sessionVariables.getStateCodeSelected()).getOffice()).clone();
                user_catg = sessionVariables.getUserCatgForLoggedInUser();
                for (SelectItem e : officeList) {
                    if (Integer.valueOf(e.getValue().toString()) == sessionVariables.getOffCodeSelected()) {
                        officeName = e.getLabel();
                        break;
                    }
                }
                userinfo.setOfficeName(this.officeName);
                if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)) {
                    listOfficeAdmin = userinformationimpl.getOfficeAdmin();
                    offtiminglist = userinformationimpl.getOfficeTiming();
                    offiplist = userinformationimpl.getOfficeIp();
                } else {
                    renderOffice = true;
                    dobjOfficeAdmin = userinformationimpl.getOfficeAdminRecord();
                    if (dobjOfficeAdmin != null) {
                        listOfficeAdmin.add(dobjOfficeAdmin);
                    }
                    dobjofftiming = userinformationimpl.getOfficeTimings();
                    if (dobjofftiming != null) {
                        offtiminglist.add(dobjofftiming);
                    }
                    offiplist = userinformationimpl.getOfficeIpDetails();
                }
            }
        } catch (VahanException ve) {
            JSFUtils.showMessage(ve.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage("Some Error occurred while fetching the Record");
        }
    }

    public void fillDataTable(UserInformationDobj userinfo) throws VahanException {
        try {
            listUser = userinformationimpl.getUserUnderOffAdmin(userinfo.getOff_cd());
            if (listUser != null && !listUser.isEmpty()) {
                userOfficeName = listUser.get(0).getOfficeName();
                PrimeFaces.current().executeScript("PF('assignActionListUser').hide()");
                PrimeFaces.current().executeScript("PF('userlistUnderOffAdmin').show()");
            } else {
                msg = "No User Found Under Selected Office Admin !! ";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(JSFUtils.INFO, msg));
            }
        } catch (VahanException ve) {
            JSFUtils.showMessage(ve.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void fillDealerDataTable(UserInformationDobj userinfo) throws VahanException {
        try {
            dealerUser = userinformationimpl.getDealerAdminUnderOffAdmin(userinfo.getOff_cd());
            if (dealerUser != null && !dealerUser.isEmpty()) {
                userOfficeName = dealerUser.get(0).getOfficeName();
                PrimeFaces.current().executeScript("PF('assignActionListUser').hide()");
                PrimeFaces.current().executeScript("PF('dlrAdminlistUnderOffAdmin').show()");
            } else {
                msg = "No Dealer Record Found Under Selected Office Admin !! ";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(JSFUtils.INFO, msg));
            }
        } catch (VahanException ve) {
            JSFUtils.showMessage(ve.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void fillDealerStaffDataTable(UserInformationDobj userinfo) throws VahanException {
        try {
            dealerStaff = userinformationimpl.getDealerStaffDetails(userinfo.getDealer_cd());
            if (dealerStaff != null && !dealerStaff.isEmpty()) {
                userOfficeName = dealerStaff.get(0).getOfficeName();
                PrimeFaces.current().executeScript("PF('assignActionListUser').hide()");
                PrimeFaces.current().executeScript("PF('dlrStafflistUnderDealerAdmin').show()");
            } else {
                msg = "No Dealer Staff Found Under Selected Dealer Admin !! ";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(JSFUtils.INFO, msg));
            }
        } catch (VahanException ve) {
            JSFUtils.showMessage(ve.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void getAssignAction(UserInformationDobj assigninfo) throws VahanException {
        try {
            actionlist = userinformationimpl.getAssignAction(assigninfo);
            if (actionlist != null && !actionlist.isEmpty()) {
                userName = actionlist.get(0).getUser_name();
                PrimeFaces.current().executeScript("PF('assignActionListUser').show()");
            } else {
                msg = "No Action Available For Selected User !! ";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(JSFUtils.INFO, msg));
            }
        } catch (VahanException ve) {
            JSFUtils.showMessage(ve.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }

    }

    public void getblockUser() throws VahanException {
        try {
            if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN)) {
                blockUserList = userinformationimpl.getBlockUserDetails();
            } else {
                blockUserList = userinformationimpl.getOfficeBlockUserDetails();
            }
            if (blockUserList != null && !blockUserList.isEmpty()) {
                PrimeFaces.current().executeScript("PF('blockUSerDetails').show()");
            } else {
                msg = "No Block User Found !! ";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(JSFUtils.INFO, msg));
            }
        } catch (VahanException ve) {
            JSFUtils.showMessage(ve.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }
    }

    public void getipDetails(UserInformationDobj iplogin) throws VahanException {
        try {
            ipDetailList = userinformationimpl.getIpDetails();
            if (ipDetailList != null && !ipDetailList.isEmpty()) {
                userOfficeName = ipDetailList.get(0).getOfficeName();
                PrimeFaces.current().executeScript("PF('officeIpDetails').show()");
            } else {
                msg = "No IP Record Available";
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(JSFUtils.INFO, msg));
            }
        } catch (VahanException ve) {
            JSFUtils.showMessage(ve.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
        }

    }

    /**
     * @return the userinfo
     */
    public UserInformationDobj getUserinfo() {
        return userinfo;
    }

    /**
     * @param userinfo the userinfo to set
     */
    public void setUserinfo(UserInformationDobj userinfo) {
        this.userinfo = userinfo;
    }

    /**
     * @return the officeList
     */
    public List<SelectItem> getOfficeList() {
        return officeList;
    }

    /**
     * @param officeList the officeList to set
     */
    public void setOfficeList(List<SelectItem> officeList) {
        this.officeList = officeList;
    }

    /**
     * @return the state_name
     */
    public String getState_name() {
        return state_name;
    }

    /**
     * @param state_name the state_name to set
     */
    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    /**
     * @return the officeName
     */
    public String getOfficeName() {
        return officeName;
    }

    /**
     * @param officeName the officeName to set
     */
    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    /**
     * @return the listOfficeAdmin
     */
    public List<UserInformationDobj> getListOfficeAdmin() {
        return listOfficeAdmin;
    }

    /**
     * @param listOfficeAdmin the listOfficeAdmin to set
     */
    public void setListOfficeAdmin(List<UserInformationDobj> listOfficeAdmin) {
        this.listOfficeAdmin = listOfficeAdmin;
    }

    /**
     * @return the listUser
     */
    public List<UserInformationDobj> getListUser() {
        return listUser;
    }

    /**
     * @param listUser the listUser to set
     */
    public void setListUser(List<UserInformationDobj> listUser) {
        this.listUser = listUser;
    }

    /**
     * @return the actionlist
     */
    public List<UserInformationDobj> getActionlist() {
        return actionlist;
    }

    /**
     * @param actionlist the actionlist to set
     */
    public void setActionlist(List<UserInformationDobj> actionlist) {
        this.actionlist = actionlist;
    }

    /**
     * @return the offtiminglist
     */
    public List<UserInformationDobj> getOfftiminglist() {
        return offtiminglist;
    }

    /**
     * @param offtiminglist the offtiminglist to set
     */
    public void setOfftiminglist(List<UserInformationDobj> offtiminglist) {
        this.offtiminglist = offtiminglist;
    }

    /**
     * @return the offiplist
     */
    public List<UserInformationDobj> getOffiplist() {
        return offiplist;
    }

    /**
     * @param offiplist the offiplist to set
     */
    public void setOffiplist(List<UserInformationDobj> offiplist) {
        this.offiplist = offiplist;
    }

    /**
     * @return the blockUserList
     */
    public List<UserInformationDobj> getBlockUserList() {
        return blockUserList;
    }

    /**
     * @param blockUserList the blockUserList to set
     */
    public void setBlockUserList(List<UserInformationDobj> blockUserList) {
        this.blockUserList = blockUserList;
    }

    /**
     * @return the userOfficeName
     */
    public String getUserOfficeName() {
        return userOfficeName;
    }

    /**
     * @param userOfficeName the userOfficeName to set
     */
    public void setUserOfficeName(String userOfficeName) {
        this.userOfficeName = userOfficeName;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the filterList
     */
    public List getFilterList() {
        return filterList;
    }

    /**
     * @param filterList the filterList to set
     */
    public void setFilterList(List filterList) {
        this.filterList = filterList;
    }

    /**
     * @return the ipDetailList
     */
    public List<UserInformationDobj> getIpDetailList() {
        return ipDetailList;
    }

    /**
     * @param ipDetailList the ipDetailList to set
     */
    public void setIpDetailList(List<UserInformationDobj> ipDetailList) {
        this.ipDetailList = ipDetailList;
    }

    /**
     * @return the user_catg
     */
    public String getUser_catg() {
        return user_catg;
    }

    /**
     * @param user_catg the user_catg to set
     */
    public void setUser_catg(String user_catg) {
        this.user_catg = user_catg;
    }

    /**
     * @return the renderOffice
     */
    public boolean isRenderOffice() {
        return renderOffice;
    }

    /**
     * @param renderOffice the renderOffice to set
     */
    public void setRenderOffice(boolean renderOffice) {
        this.renderOffice = renderOffice;
    }

    /**
     * @return the dealerUser
     */
    public List<UserInformationDobj> getDealerUser() {
        return dealerUser;
    }

    /**
     * @param dealerUser the dealerUser to set
     */
    public void setDealerUser(List<UserInformationDobj> dealerUser) {
        this.dealerUser = dealerUser;
    }

    /**
     * @return the dealerStaff
     */
    public List<UserInformationDobj> getDealerStaff() {
        return dealerStaff;
    }

    /**
     * @param dealerStaff the dealerStaff to set
     */
    public void setDealerStaff(List<UserInformationDobj> dealerStaff) {
        this.dealerStaff = dealerStaff;
    }
}
