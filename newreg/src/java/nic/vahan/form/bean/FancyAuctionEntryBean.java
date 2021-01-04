/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.form.dobj.FancyAuctionDobj;
import nic.vahan.form.dobj.common.Draft_dobj;
import nic.vahan.form.impl.FancyAuctionVerifyImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.panel.Panel;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.component.selectoneradio.SelectOneRadio;
import org.primefaces.PrimeFaces;

/**
 *
 * @author AMBRISH
 */
@ManagedBean(name = "fancynumberauctionbean")
@ViewScoped
public class FancyAuctionEntryBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(FancyAuctionEntryBean.class);
    private ArrayList<String> listAuctionRegnNumbers = new ArrayList<String>();
    private SelectOneMenu selectedRegnNo = new SelectOneMenu();
    private ArrayList<FancyAuctionDobj> listAuctionApplicationNumbers = new ArrayList<FancyAuctionDobj>();
    private String currect_draft_appl_no;
    private Draft_dobj currect_draft = new Draft_dobj();
    private HashMap<String, ArrayList<Draft_dobj>> draft_details = new HashMap<String, ArrayList<Draft_dobj>>();
    private ArrayList<FancyAuctionDobj> listCommonHighest = new ArrayList<FancyAuctionDobj>();
    private ArrayList list_bank = new ArrayList();
    private Panel individualAuctionDetailsEntry = new Panel();
    private CommandButton btn_save = new CommandButton();
    private CommandButton btn_draft_add = new CommandButton();
    private CommandButton btn_getDetails = new CommandButton();
    private InputText recp_no = new InputText();
    private InputText recp_dt = new InputText();
    private InputText regn_appl_no = new InputText();
    private InputText regn_no = new InputText();
    private InputText app_auth = new InputText();
    private InputText dt_of_app = new InputText();
    private InputText file_no = new InputText();
    private InputText owner_name = new InputText();
    private InputText c_add1 = new InputText();
    private InputText c_add2 = new InputText();
    private InputText c_village = new InputText();
    private InputText c_taluk = new InputText();
    private InputText c_district = new InputText();
    private InputText c_pincode = new InputText();
    private InputText reserve_amt = new InputText();
    private InputText draft_amt = new InputText();
    private InputText auction_amt = new InputText();
    private InputText offer_amt = new InputText();
    private InputText total_amt = new InputText();
    private InputText bal_amt = new InputText();
    private InputText auction_dt = new InputText();
    private InputText auction_amt_recp_no = new InputText();
    private InputText aution_amt_recp_dt = new InputText();
    private SelectOneRadio attendance_at_auction = new SelectOneRadio();
    private InputText status = new InputText();
    private InputText state_cd = new InputText();
    private InputText rto_cd = new InputText();
    FancyAuctionVerifyImpl impl = new FancyAuctionVerifyImpl();

    @PostConstruct
    public void init() {

        try {
            Map map = (Map) Util.getSession().getAttribute("seat_map");

            setListAuctionRegnNumbers(impl.getAllFancyNumberAuction());
            individualAuctionDetailsEntry.setRendered(false);
            selectedRegnNo.setValue(map.get("regn_no").toString());

            selectedRegnNo.setDisabled(false);
            btn_save.setValue("Lottery Pending");
            btn_draft_add.setValue("Add Draft");
            btn_save.setDisabled(true);
            btn_getDetails.setDisabled(false);
            auctionDetailsEntry();

            // loading bank list
            String[][] data = MasterTableFiller.masterTables.TM_BANK.getData();
            for (int i = 0; i < data.length; i++) {
                getList_bank().add(new SelectItem(data[i][0], data[i][1]));
                getList_bank().add(new SelectItem(data[i][0], data[i][1]));
            }
        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, ex.getMessage(), null));
        }
    }

    public void saveAsDraft(ActionEvent ae) {
        try {

            //check offer and draft validities
            if (offer_amt != null && Integer.parseInt(offer_amt.getValue().toString()) > 0) {
                if (draft_amt != null && Integer.parseInt(draft_amt.getValue().toString()) > 0) {
                    int offer_amt1 = Integer.parseInt(offer_amt.getValue().toString());
                    int draft_amt1 = Integer.parseInt(draft_amt.getValue().toString());
                    if (draft_amt1 < offer_amt1 / 2) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Draft amount can not be less than 50% of the Offer Amount", null));
                        return;
                    }
                    if (draft_amt1 > offer_amt1) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Draft amount can not be greater than Offer Amount", null));
                        return;
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please enter valid draft amount", null));
                    return;

                }
            }

            for (int i = 0; i < listAuctionApplicationNumbers.size(); i++) {

                FancyAuctionDobj dobj = listAuctionApplicationNumbers.get(i);
                if (dobj.getRegn_no().equalsIgnoreCase(regn_no.getValue().toString()) && dobj.getRegn_appl_no().equalsIgnoreCase(regn_appl_no.getValue().toString())) {
                    //check entry

                    if (true)//if(draft_amt.getValue()!=null && total_amt.getValue()!=null &&  !total_amt.getValue().toString().equalsIgnoreCase("0"))
                    {
                        dobj.setReserve_amt(Integer.parseInt(reserve_amt.getValue().toString()));
                        dobj.setDraft_amt(Integer.parseInt(draft_amt.getValue().toString()));
                        dobj.setAttendance_at_auction(attendance_at_auction.getValue().toString());
                        dobj.setAuction_amt(Integer.parseInt(auction_amt.getValue().toString()));
                        dobj.setOffer_amt(Integer.parseInt(offer_amt.getValue().toString()));

                        int ReserveOffer = Integer.parseInt(reserve_amt.getValue().toString()) + Integer.parseInt(offer_amt.getValue().toString());
                        int ReserveAuction = Integer.parseInt(reserve_amt.getValue().toString()) + Integer.parseInt(auction_amt.getValue().toString());
                        int total = ReserveAuction > ReserveOffer ? ReserveAuction : ReserveOffer;
                        int bal_amount = total - Integer.parseInt(reserve_amt.getValue().toString()) - Integer.parseInt(draft_amt.getValue().toString());
                        dobj.setTotal_amt(total);
                        dobj.setBal_amt(bal_amount);

                        // reset form
                        reserve_amt.setValue(null);
                        draft_amt.setValue(null);
                        auction_amt.setValue("0");
                        total_amt.setValue(null);

                        attendance_at_auction.setValue("A");
                        individualAuctionDetailsEntry.setRendered(false);
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid total amount for  Application Number [ " + dobj.getRegn_appl_no() + " ]", null));
                    }
                    break;
                }

            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void auctionDetailsEntryReset() {
        listAuctionApplicationNumbers.clear();
        listCommonHighest.clear();
        selectedRegnNo.setDisabled(false);
        selectedRegnNo.setValue("-1");
        btn_save.setValue("Lottery Pending");
        btn_save.setDisabled(true);
        btn_getDetails.setDisabled(false);

    }

    public void auctionWinner() {
        String appl_no = "";
        listCommonHighest.clear();
        if (listAuctionApplicationNumbers.size() <= 0) {
            return;
        }
        if (listAuctionApplicationNumbers.size() == 1) {
            listAuctionApplicationNumbers.get(0).setStatus("A");
            btn_save.setValue("Save Data");
            btn_save.setDisabled(false);
            return;
        }


        Collections.sort(listAuctionApplicationNumbers, FancyAuctionDobj.FancyNumberTotalComparator);
//Sorting object using Comparator in Java
        Collections.sort(listAuctionApplicationNumbers, new FancyAuctionDobj.OrderByTotalAmount());

        getListCommonHighest().add(listAuctionApplicationNumbers.get(0));
        for (int i = 1; i < listAuctionApplicationNumbers.size(); i++) {
            FancyAuctionDobj dobj = listAuctionApplicationNumbers.get(i);
            if (getListCommonHighest().get(0).getTotal_amt() == dobj.getTotal_amt()) {
                getListCommonHighest().add(dobj);
            }
        }
        if (getListCommonHighest().size() == 1) {
            for (int i = 0; i < listAuctionApplicationNumbers.size(); i++) {

                FancyAuctionDobj dobj = listAuctionApplicationNumbers.get(i);
                if (dobj.getRegn_appl_no().equalsIgnoreCase(getListCommonHighest().get(0).getRegn_appl_no())) {
                    dobj.setStatus("A");
                } else {
                    dobj.setStatus("R");
                }
                btn_save.setValue("Save Data");
                btn_save.setDisabled(false);
            }
            return;
        } else {
            //RequestContext context = RequestContext.getCurrentInstance();
            PrimeFaces.current().executeScript("dialogLotterydlg.show()");
        }
    }

    public void auctionDetailsEntry() {
        try {
            listCommonHighest.clear();
            btn_save.setValue("Lottery Pending");
            btn_save.setDisabled(true);
            if (selectedRegnNo.getValue().equals("-1")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please Select Regn No", null));
                return;
            }
            btn_getDetails.setDisabled(true);
            listAuctionApplicationNumbers = impl.getFancyApplicationDetailsNew(selectedRegnNo.getValue().toString());
            if (listAuctionApplicationNumbers.size() > 0) {
                selectedRegnNo.setDisabled(true);
            }
        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public String auctionDetailsSaveEntry() {
        try {
            //impl.save();
            if (validateEntries(listAuctionApplicationNumbers)) {
                if (impl.saveAuctionDetailsEntry(listAuctionApplicationNumbers, draft_details)) {
                    listAuctionRegnNumbers.remove(listAuctionApplicationNumbers.get(0).getRegn_no());
                    listAuctionApplicationNumbers.clear();
                    listCommonHighest.clear();
                    selectedRegnNo.setDisabled(false);
                    btn_save.setValue("Lottery Pending");
                    btn_save.setDisabled(true);
                    btn_getDetails.setDisabled(false);
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please check that all application is properly entred", null));
            }

        } catch (VahanException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
        return "seatwork";
    }

    private boolean validateEntries(ArrayList<FancyAuctionDobj> listAuctionApplicationNumbers) {
        boolean validate = true;
        for (int i = 0; i < listAuctionApplicationNumbers.size(); i++) {
            FancyAuctionDobj dobj = listAuctionApplicationNumbers.get(i);
            if (dobj.getAttendance_at_auction() == null || dobj.getAttendance_at_auction().equalsIgnoreCase("")) {
                validate = false;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please Select Attandence for  Application Number [ " + dobj.getRegn_appl_no() + " ]", null));
                break;
            }
            if (dobj.getTotal_amt() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid total amount for  Application Number [ " + dobj.getRegn_appl_no() + " ]", null));
                validate = false;
                break;
            }

        }

        return validate;
    }

    public void auctionDetailsPaymentEntry(ActionEvent ae) {
        try {
            String selected_regn_no = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selected_regn_no").toString();
            String selected_appl_no = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selected_appl_no").toString();
            individualAuctionDetailsEntry.setRendered(false);
            for (int i = 0; i < listAuctionApplicationNumbers.size(); i++) {
                FancyAuctionDobj dobj = listAuctionApplicationNumbers.get(i);
                if (dobj.getRegn_no().equalsIgnoreCase(selected_regn_no) && dobj.getRegn_appl_no().equalsIgnoreCase(selected_appl_no)) {

                    app_auth.setValue(dobj.getApp_auth());
                    dt_of_app.setValue(dobj.getDt_of_app());
                    regn_no.setValue(dobj.getRegn_no());
                    recp_no.setValue(dobj.getRecp_no());
                    recp_dt.setValue(dobj.getRecp_dt());
                    file_no.setValue(dobj.getFile_no());
                    owner_name.setValue(dobj.getOwner_name());
                    reserve_amt.setValue(dobj.getReserve_amt());
                    draft_amt.setValue(dobj.getDraft_amt());
                    offer_amt.setValue(dobj.getOffer_amt());
                    attendance_at_auction.setValue(dobj.getAttendance_at_auction());
                    auction_amt.setValue(dobj.getAuction_amt());
                    regn_appl_no.setValue(dobj.getRegn_appl_no());
                    if (dobj.getAttendance_at_auction() != null && !dobj.getAttendance_at_auction().equals("")) {
                        attendance_at_auction.setValue(dobj.getAttendance_at_auction());
                    } else {
                        attendance_at_auction.setValue("A");
                        auction_amt.setDisabled(true);
                    }
                    individualAuctionDetailsEntry.setRendered(true);
                    break;
                }

            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void auctionDetailsSetLotteryWinner(ActionEvent ae) {
        try {
            String selected_regn_no = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selected_regn_no").toString();
            String selected_appl_no = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selected_appl_no").toString();

            for (int i = 0; i < listAuctionApplicationNumbers.size(); i++) {

                FancyAuctionDobj dobj = listAuctionApplicationNumbers.get(i);
                if (dobj.getRegn_no().equalsIgnoreCase(selected_regn_no) && dobj.getRegn_appl_no().equalsIgnoreCase(selected_appl_no)) {

                    dobj.setStatus("A");

                } else {
                    dobj.setStatus("R");
                }
                btn_save.setValue("Save Data");
                btn_save.setDisabled(false);

            }
            //RequestContext context = RequestContext.getCurrentInstance();
            PrimeFaces.current().executeScript("dialogLotterydlg.hide()");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        }
    }

    public void auctionDetailsEntryAttendanceChanged(ValueChangeEvent ae) {
        if (attendance_at_auction.getValue().toString().equalsIgnoreCase("P")) {
            auction_amt.setDisabled(false);
        } else {
            auction_amt.setDisabled(true);
            auction_amt.setValue("0");
        }
    }

    public String addAction() {

        if (btn_draft_add.getValue().toString().equalsIgnoreCase("Add Draft")) {
            Draft_dobj dobj = new Draft_dobj(currect_draft);
            dobj.setAppl_no(regn_appl_no.getValue().toString());

            ArrayList<Draft_dobj> draf_list = draft_details.get(regn_appl_no.getValue());

            if (draft_details.get(regn_appl_no.getValue()) == null) {
                draf_list = new ArrayList<>();
                draft_details.put(regn_appl_no.getValue().toString(), draf_list);
            } else {
                // Some previous record found
            }
            draf_list.add(dobj);

            currect_draft = new Draft_dobj();
            Util.getSession().removeAttribute("draft_edit");
        } else // Edit draft details goes here
        {
            Util.getSession().removeAttribute("draft_edit");
            btn_draft_add.setValue("Add Draft");
            currect_draft = new Draft_dobj();
        }

        return null;
    }

    public void resetDraftDetails() {
        //// Revert Changes 
        Draft_dobj temp = (Draft_dobj) Util.getSession().getAttribute("draft_edit");
        if (temp != null) {
            currect_draft.setAppl_no(temp.getAppl_no());
            currect_draft.setDraft_cd(temp.getDraft_cd());
            currect_draft.setFlag(temp.getFlag());
            currect_draft.setDraft_num(temp.getDraft_num());
            currect_draft.setDated(temp.getDated());
            currect_draft.setAmount(temp.getAmount());
            currect_draft.setBank_code(temp.getBank_code());
            currect_draft.setBranch_name(temp.getBranch_name());
            currect_draft.setReceived_dt(temp.getReceived_dt());
            currect_draft.setStatus(temp.getStatus());
            currect_draft.setBank_response_dt(temp.getBank_response_dt());
            currect_draft.setStatus_cd(temp.getStatus_cd());
            currect_draft.setRemarks(temp.getRemarks());
            currect_draft.setCollected_by(temp.getCollected_by());
            currect_draft.setState_cd(temp.getState_cd());
            currect_draft.setOff_cd(temp.getOff_cd());
            Util.getSession().removeAttribute("draft_edit");
        }
        //////////// End
        currect_draft = new Draft_dobj();
        btn_draft_add.setValue("Add Draft");
    }

    public void deleteDraftRecord(Draft_dobj dobj) {
        draft_details.get(regn_appl_no.getValue().toString()).remove(dobj);
    }

    public void editDraftRecord(Draft_dobj dobj) {
        currect_draft = dobj;
        Util.getSession().setAttribute("draft_edit", new Draft_dobj(dobj));

        btn_draft_add.setValue("Edit Draft");
    }

    public ArrayList<Draft_dobj> getListDraftNoBeingAdded() {
        return draft_details.get(regn_appl_no.getValue());
    }

    /**
     * @return the listAuctionRegnNumbers
     */
    public ArrayList<String> getListAuctionRegnNumbers() {
        return listAuctionRegnNumbers;
    }

    /**
     * @param listAuctionRegnNumbers the listAuctionRegnNumbers to set
     */
    public void setListAuctionRegnNumbers(ArrayList<String> listAuctionRegnNumbers) {
        this.listAuctionRegnNumbers = listAuctionRegnNumbers;
    }

    /**
     * @return the recp_no
     */
    public InputText getRecp_no() {
        return recp_no;
    }

    /**
     * @param recp_no the recp_no to set
     */
    public void setRecp_no(InputText recp_no) {
        this.recp_no = recp_no;
    }

    /**
     * @return the recp_dt
     */
    public InputText getRecp_dt() {
        return recp_dt;
    }

    /**
     * @param recp_dt the recp_dt to set
     */
    public void setRecp_dt(InputText recp_dt) {
        this.recp_dt = recp_dt;
    }

    /**
     * @return the regn_appl_no
     */
    public InputText getRegn_appl_no() {
        return regn_appl_no;
    }

    /**
     * @param regn_appl_no the regn_appl_no to set
     */
    public void setRegn_appl_no(InputText regn_appl_no) {
        this.regn_appl_no = regn_appl_no;
    }

    /**
     * @return the regn_no
     */
    public InputText getRegn_no() {
        return regn_no;
    }

    /**
     * @param regn_no the regn_no to set
     */
    public void setRegn_no(InputText regn_no) {
        this.regn_no = regn_no;
    }

    /**
     * @return the app_auth
     */
    public InputText getApp_auth() {
        return app_auth;
    }

    /**
     * @param app_auth the app_auth to set
     */
    public void setApp_auth(InputText app_auth) {
        this.app_auth = app_auth;
    }

    /**
     * @return the dt_of_app
     */
    public InputText getDt_of_app() {
        return dt_of_app;
    }

    /**
     * @param dt_of_app the dt_of_app to set
     */
    public void setDt_of_app(InputText dt_of_app) {
        this.dt_of_app = dt_of_app;
    }

    /**
     * @return the file_no
     */
    public InputText getFile_no() {
        return file_no;
    }

    /**
     * @param file_no the file_no to set
     */
    public void setFile_no(InputText file_no) {
        this.file_no = file_no;
    }

    /**
     * @return the owner_name
     */
    public InputText getOwner_name() {
        return owner_name;
    }

    /**
     * @param owner_name the owner_name to set
     */
    public void setOwner_name(InputText owner_name) {
        this.owner_name = owner_name;
    }

    /**
     * @return the c_add1
     */
    public InputText getC_add1() {
        return c_add1;
    }

    /**
     * @param c_add1 the c_add1 to set
     */
    public void setC_add1(InputText c_add1) {
        this.c_add1 = c_add1;
    }

    /**
     * @return the c_add2
     */
    public InputText getC_add2() {
        return c_add2;
    }

    /**
     * @param c_add2 the c_add2 to set
     */
    public void setC_add2(InputText c_add2) {
        this.c_add2 = c_add2;
    }

    /**
     * @return the c_village
     */
    public InputText getC_village() {
        return c_village;
    }

    /**
     * @param c_village the c_village to set
     */
    public void setC_village(InputText c_village) {
        this.c_village = c_village;
    }

    /**
     * @return the c_taluk
     */
    public InputText getC_taluk() {
        return c_taluk;
    }

    /**
     * @param c_taluk the c_taluk to set
     */
    public void setC_taluk(InputText c_taluk) {
        this.c_taluk = c_taluk;
    }

    /**
     * @return the c_district
     */
    public InputText getC_district() {
        return c_district;
    }

    /**
     * @param c_district the c_district to set
     */
    public void setC_district(InputText c_district) {
        this.c_district = c_district;
    }

    /**
     * @return the c_pincode
     */
    public InputText getC_pincode() {
        return c_pincode;
    }

    /**
     * @param c_pincode the c_pincode to set
     */
    public void setC_pincode(InputText c_pincode) {
        this.c_pincode = c_pincode;
    }

    /**
     * @return the reserve_amt
     */
    public InputText getReserve_amt() {
        return reserve_amt;
    }

    /**
     * @param reserve_amt the reserve_amt to set
     */
    public void setReserve_amt(InputText reserve_amt) {
        this.reserve_amt = reserve_amt;
    }

    /**
     * @return the draft_amt
     */
    public InputText getDraft_amt() {
        return draft_amt;
    }

    /**
     * @param draft_amt the draft_amt to set
     */
    public void setDraft_amt(InputText draft_amt) {
        this.draft_amt = draft_amt;
    }

    /**
     * @return the auction_amt
     */
    public InputText getAuction_amt() {
        return auction_amt;
    }

    /**
     * @param auction_amt the auction_amt to set
     */
    public void setAuction_amt(InputText auction_amt) {
        this.auction_amt = auction_amt;
    }

    /**
     * @return the total_amt
     */
    public InputText getTotal_amt() {
        return total_amt;
    }

    /**
     * @param total_amt the total_amt to set
     */
    public void setTotal_amt(InputText total_amt) {
        this.total_amt = total_amt;
    }

    /**
     * @return the auction_dt
     */
    public InputText getAuction_dt() {
        return auction_dt;
    }

    /**
     * @param auction_dt the auction_dt to set
     */
    public void setAuction_dt(InputText auction_dt) {
        this.auction_dt = auction_dt;
    }

    /**
     * @return the auction_amt_recp_no
     */
    public InputText getAuction_amt_recp_no() {
        return auction_amt_recp_no;
    }

    /**
     * @param auction_amt_recp_no the auction_amt_recp_no to set
     */
    public void setAuction_amt_recp_no(InputText auction_amt_recp_no) {
        this.auction_amt_recp_no = auction_amt_recp_no;
    }

    /**
     * @return the aution_amt_recp_dt
     */
    public InputText getAution_amt_recp_dt() {
        return aution_amt_recp_dt;
    }

    /**
     * @param aution_amt_recp_dt the aution_amt_recp_dt to set
     */
    public void setAution_amt_recp_dt(InputText aution_amt_recp_dt) {
        this.aution_amt_recp_dt = aution_amt_recp_dt;
    }

    /**
     * @return the attendance_at_auction
     */
    public SelectOneRadio getAttendance_at_auction() {
        return attendance_at_auction;
    }

    /**
     * @param attendance_at_auction the attendance_at_auction to set
     */
    public void setAttendance_at_auction(SelectOneRadio attendance_at_auction) {
        this.attendance_at_auction = attendance_at_auction;
    }

    /**
     * @return the status
     */
    public InputText getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(InputText status) {
        this.status = status;
    }

    /**
     * @return the state_cd
     */
    public InputText getState_cd() {
        return state_cd;
    }

    /**
     * @param state_cd the state_cd to set
     */
    public void setState_cd(InputText state_cd) {
        this.state_cd = state_cd;
    }

    /**
     * @return the rto_cd
     */
    public InputText getRto_cd() {
        return rto_cd;
    }

    /**
     * @param rto_cd the rto_cd to set
     */
    public void setRto_cd(InputText rto_cd) {
        this.rto_cd = rto_cd;
    }

    /**
     * @return the listAuctionApplicationNumbers
     */
    public ArrayList<FancyAuctionDobj> getListAuctionApplicationNumbers() {
        return listAuctionApplicationNumbers;
    }

    /**
     * @param listAuctionApplicationNumbers the listAuctionApplicationNumbers to
     * set
     */
    public void setListAuctionApplicationNumbers(ArrayList<FancyAuctionDobj> listAuctionApplicationNumbers) {
        this.listAuctionApplicationNumbers = listAuctionApplicationNumbers;
    }

    /**
     * @return the individualAuctionDetailsEntry
     */
    public Panel getIndividualAuctionDetailsEntry() {
        return individualAuctionDetailsEntry;
    }

    /**
     * @param individualAuctionDetailsEntry the individualAuctionDetailsEntry to
     * set
     */
    public void setIndividualAuctionDetailsEntry(Panel individualAuctionDetailsEntry) {
        this.individualAuctionDetailsEntry = individualAuctionDetailsEntry;
    }

    /**
     * @return the offer_amt
     */
    public InputText getOffer_amt() {
        return offer_amt;
    }

    /**
     * @param offer_amt the offer_amt to set
     */
    public void setOffer_amt(InputText offer_amt) {
        this.offer_amt = offer_amt;
    }

    /**
     * @return the bal_amt
     */
    public InputText getBal_amt() {
        return bal_amt;
    }

    /**
     * @param bal_amt the bal_amt to set
     */
    public void setBal_amt(InputText bal_amt) {
        this.bal_amt = bal_amt;
    }

    /**
     * @return the selectedRegnNo
     */
    public SelectOneMenu getSelectedRegnNo() {
        return selectedRegnNo;
    }

    /**
     * @param selectedRegnNo the selectedRegnNo to set
     */
    public void setSelectedRegnNo(SelectOneMenu selectedRegnNo) {
        this.selectedRegnNo = selectedRegnNo;
    }

    /**
     * @return the listCommonHighest
     */
    public ArrayList<FancyAuctionDobj> getListCommonHighest() {
        return listCommonHighest;
    }

    /**
     * @param listCommonHighest the listCommonHighest to set
     */
    public void setListCommonHighest(ArrayList<FancyAuctionDobj> listCommonHighest) {
        this.listCommonHighest = listCommonHighest;
    }

    /**
     * @return the btn_save
     */
    public CommandButton getBtn_save() {
        return btn_save;
    }

    /**
     * @param btn_save the btn_save to set
     */
    public void setBtn_save(CommandButton btn_save) {
        this.btn_save = btn_save;
    }

    /**
     * @return the btn_getDetails
     */
    public CommandButton getBtn_getDetails() {
        return btn_getDetails;
    }

    /**
     * @param btn_getDetails the btn_getDetails to set
     */
    public void setBtn_getDetails(CommandButton btn_getDetails) {
        this.btn_getDetails = btn_getDetails;
    }

    /**
     * @return the currect_draft_appl_no
     */
    public String getCurrect_draft_appl_no() {
        return currect_draft_appl_no;
    }

    /**
     * @param currect_draft_appl_no the currect_draft_appl_no to set
     */
    public void setCurrect_draft_appl_no(String currect_draft_appl_no) {
        this.currect_draft_appl_no = currect_draft_appl_no;
    }

    /**
     * @return the currect_draft
     */
    public Draft_dobj getCurrect_draft() {
        return currect_draft;
    }

    /**
     * @param currect_draft the currect_draft to set
     */
    public void setCurrect_draft(Draft_dobj currect_draft) {
        this.currect_draft = currect_draft;
    }

    /**
     * @return the draft_details
     */
    public HashMap<String, ArrayList<Draft_dobj>> getDraft_details() {
        return draft_details;
    }

    /**
     * @param draft_details the draft_details to set
     */
    public void setDraft_details(HashMap<String, ArrayList<Draft_dobj>> draft_details) {
        this.draft_details = draft_details;
    }

    /**
     * @return the btn_draft_add
     */
    public CommandButton getBtn_draft_add() {
        return btn_draft_add;
    }

    /**
     * @param btn_draft_add the btn_draft_add to set
     */
    public void setBtn_draft_add(CommandButton btn_draft_add) {
        this.btn_draft_add = btn_draft_add;
    }

    /**
     * @return the list_bank
     */
    public ArrayList getList_bank() {
        return list_bank;
    }

    /**
     * @param list_bank the list_bank to set
     */
    public void setList_bank(ArrayList list_bank) {
        this.list_bank = list_bank;
    }
}
