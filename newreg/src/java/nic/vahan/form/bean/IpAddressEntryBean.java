/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

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
import nic.vahan.form.dobj.IpAddressEntryDobj;
import nic.vahan.form.dobj.TmConfigurationDobj;
import nic.vahan.form.impl.IpAddressEntryImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.CommonUtils;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
//import org.primefaces.context.RequestContext;

/**
 *
 * @author acer
 */
@ManagedBean(name = "IpAddressEntryBean")
@ViewScoped
public class IpAddressEntryBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(IpAddressEntryBean.class);
    private SessionVariables sessionVariables = null;
    private IpAddressEntryDobj ipAddressDobj = new IpAddressEntryDobj();
    private String state_name;
    private String officeName;
    private String ipAddress = "";
    private int offCd;
    private List<IpAddressEntryDobj> ip_address_list = new ArrayList<>();
    private List<SelectItem> officeList = null;
    private List<IpAddressEntryDobj> ipAddressFinalList = new ArrayList<>();
    private boolean officeNameAdmin = false;
    private boolean officeListStateAdmin = false;
    private String user_catg;
    private String msg = "";

    public IpAddressEntryBean() {
        try {
            user_catg = Util.getUserCategory();
            sessionVariables = new SessionVariables();
            TmConfigurationDobj tmConfigurationDobj = Util.getTmConfiguration();
            if (sessionVariables == null
                    || sessionVariables.getStateCodeSelected() == null
                    || sessionVariables.getEmpCodeLoggedIn() == null) {
                throw new VahanException(TableConstants.SomthingWentWrong);
            } else {
                if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_OFFICE_ADMIN)) {
                    officeNameAdmin = true;
                    offCd = sessionVariables.getOffCodeSelected();
                    ip_address_list = IpAddressEntryImpl.getIpAddressList(sessionVariables.getStateCodeSelected(), sessionVariables.getOffCodeSelected());
                    if (ip_address_list.isEmpty()) {
                        ip_address_list.add(new IpAddressEntryDobj());
                    }
                } else if (user_catg.equalsIgnoreCase(TableConstants.USER_CATG_STATE_ADMIN) && tmConfigurationDobj != null
                        && tmConfigurationDobj.getTmConfigUserMgmtDobj() != null
                        && tmConfigurationDobj.getTmConfigUserMgmtDobj().isIp_config_by_state_admin()) {
                    officeListStateAdmin = true;

                }
                state_name = MasterTableFiller.state.get(sessionVariables.getStateCodeSelected()).getStateDescr();
                officeList = (ArrayList<SelectItem>) ((ArrayList<SelectItem>) MasterTableFiller.state.get(sessionVariables.getStateCodeSelected()).getOffice()).clone();
                for (SelectItem e : officeList) {
                    if (Integer.valueOf(e.getValue().toString()) == sessionVariables.getOffCodeSelected()) {
                        officeName = e.getLabel();
                        break;
                    }
                }
            }
        } catch (VahanException ve) {
            JSFUtils.showMessagesInDialog("Alert", ve.getMessage(), FacesMessage.SEVERITY_ERROR);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage("Problem in getting record.");
        }
    }

    public void addRemoveRow(IpAddressEntryDobj dobj) {
        String mode = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("actionmode");
        try {
            if ("add".equalsIgnoreCase(mode)) {
                if (dobj == null) {
                    JSFUtils.showMessagesInDialog("Information", "Please Enter IP Address!", FacesMessage.SEVERITY_WARN);
                } else {
                    ip_address_list.add(new IpAddressEntryDobj());
                }

            } else if ("minus".equalsIgnoreCase(mode)) {
                ip_address_list.remove(dobj);
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage("Problem in adding new record.");
        }
    }

    public void save() {
        try {

            String[] ipArray = new IpAddressEntryImpl().getIpDetails();
            ipAddressFinalList = ip_address_list;
            for (int i = 0; i < ipAddressFinalList.size(); i++) {
                if (ipArray[0].equalsIgnoreCase(ipAddressFinalList.get(i).getIpAddress())) {
                    throw new VahanException("Invalid IP, Please Enter Valid IP ");
                } else if (CommonUtils.isNullOrBlank(ipAddressFinalList.get(i).getIpAddress())) {
                    throw new VahanException("You Can't Save Blank IP Address");
                }
            }
            msg = IpAddressEntryImpl.insertDataintoDataTable(ipAddressFinalList, sessionVariables.getStateCodeSelected(), getOffCd(), sessionVariables.getEmpCodeLoggedIn());
            JSFUtils.showMessage(msg);
        } catch (VahanException ex) {
            JSFUtils.showMessage(ex.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage("Problem in Saving Record.");
        }
    }

    public void deleteIpRecord() {
        try {
            ipAddressFinalList = ip_address_list;
            msg = IpAddressEntryImpl.deleteAllIpRecord(ipAddressFinalList, sessionVariables.getStateCodeSelected(), offCd, sessionVariables.getEmpCodeLoggedIn());
            JSFUtils.showMessage(msg);
            ip_address_list.clear();
            ip_address_list.add(new IpAddressEntryDobj());
        } catch (VahanException ex) {
            JSFUtils.showMessage(ex.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            JSFUtils.showMessage("Problem in Delete Record.");
        }
    }

    public void getOfficeIpList() throws VahanException {
        try {
            ip_address_list = IpAddressEntryImpl.getIpAddressList(sessionVariables.getStateCodeSelected(), offCd);
            if (ip_address_list.isEmpty()) {
                ip_address_list.clear();
                ip_address_list.add(new IpAddressEntryDobj());
                msg = "No IP Available For Selected Office !! ";
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
     * @return the ip_address_list
     *
     *
     * /
     **
     * @return the ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * @param ipAddress the ipAddress to set
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * @return the ip_address_list
     */
    public List<IpAddressEntryDobj> getIp_address_list() {
        return ip_address_list;
    }

    /**
     * @param ip_address_list the ip_address_list to set
     */
    public void setIp_address_list(List<IpAddressEntryDobj> ip_address_list) {
        this.ip_address_list = ip_address_list;
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
     * @return the officeNameAdmin
     */
    public boolean isOfficeNameAdmin() {
        return officeNameAdmin;
    }

    /**
     * @param officeNameAdmin the officeNameAdmin to set
     */
    public void setOfficeNameAdmin(boolean officeNameAdmin) {
        this.officeNameAdmin = officeNameAdmin;
    }

    /**
     * @return the officeListStateAdmin
     */
    public boolean isOfficeListStateAdmin() {
        return officeListStateAdmin;
    }

    /**
     * @param officeListStateAdmin the officeListStateAdmin to set
     */
    public void setOfficeListStateAdmin(boolean officeListStateAdmin) {
        this.officeListStateAdmin = officeListStateAdmin;
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
     * @return the ipAddressDobj
     */
    public IpAddressEntryDobj getIpAddressDobj() {
        return ipAddressDobj;
    }

    /**
     * @param ipAddressDobj the ipAddressDobj to set
     */
    public void setIpAddressDobj(IpAddressEntryDobj ipAddressDobj) {
        this.ipAddressDobj = ipAddressDobj;
    }

    /**
     * @return the offCd
     */
    public int getOffCd() {
        return offCd;
    }

    /**
     * @param offCd the offCd to set
     */
    public void setOffCd(int offCd) {
        this.offCd = offCd;
    }
}
