/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.BlackListedVehicleDobj;
import nic.vahan.form.impl.BlackListedVehicleImpl;
//import org.primefaces.context.RequestContext;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.HttpSession;
import nic.vahan.common.jsf.utils.DateUtil;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.OwnerDetailsDobj;
import nic.vahan.form.impl.Home_Impl;
import nic.vahan.form.impl.OwnerImpl;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;
import org.primefaces.component.calendar.Calendar;

/**
 *
 * @author Afzal on 21/01/15
 */
@ManagedBean(name = "blacklistvehicle")
@ViewScoped
public class BlackListedVehicleBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(BlackListedVehicleBean.class);
    private String FirNo;
    private String PoliceStation;
    private boolean renderfir;
    private boolean renderTxtArea;
    private Date FirDate;
    private Date maxfir;
    private boolean flag = false;
    private boolean disableUnblockPanel = false;
    private boolean renderunblock = false;
    private boolean renderpanel = false;
    private boolean rendarea = false;
    private boolean renderchassisNo = false;
    private String forchassisNo;
    private boolean renderRegpanel = false;
    private boolean rendervisibleRegn = false;
    private boolean rendervisibleChassis = false;
    private String buttonId;
    private String header;
    private String userInput;
    private String saveButton;
    private boolean search_by_regn_no = false;
    private boolean showunblockpanel = false;
    private boolean add_by_regn_no = false;
    private boolean bt_save = false;
    private boolean bt_cancel = false;
    private ArrayList listTheftDetails;
    private String state_cd;
    private int off_cd;
    private String entered_by;
    private int rowid;
    private boolean flagfirdetails;
    private BlackListedVehicleBean black_listed_vehicle_bean = null;
    private List<BlackListedVehicleDobj> blacklist = new ArrayList<BlackListedVehicleDobj>();
    private List<BlackListedVehicleDobj> releasedlist = new ArrayList<BlackListedVehicleDobj>();
    private List<BlackListedVehicleBean> currlist = new ArrayList();
    private String regn_no;
    private String regin_no;
    private String chasiRegnRadiobtn = "";
    private String entered_no;
    private String chasi_no;
    private int complain_type;
    private String complaintype_Desc;
    private String actiontaken;
    private Date action_dt;
    Map mapBlacklistVehicles;
    private String complain;
    private Date complain_dt = DateUtil.parseDate(DateUtil.getCurrentDate());
    private String complaindt;
    private String deletecomplaindt;
    private boolean rennewpanel = false;
    private BlackListedVehicleDobj blacklistedVehicle_dobj = null;
    private ArrayList list_blacklistedvehicle;
    private String indexValue = "";
    private String indexValueRelease = "";
    private int maxlenght;
    private Date today = new Date();
    private String searchLabel = "";
    private boolean btnGetDetails = true;
    private List<BlackListedVehicleDobj> blacklistvehicledobjlist = null;
    private List<BlackListedVehicleDobj> releasedlistvehicledobjlist = null;
    private String offname;
    private String offcd;
    private String statecd;
    private String usercd;
    private String username;
    private String statename;
    private Calendar fromFirDate = new Calendar();
    private List maxRegndt = new ArrayList();
    private boolean rendercompundingdetails;
    private Long compoundingamount = 0L;
    private String untracedRadiobtn = "";
    private boolean renderUntracedEntry = true;
    private boolean renderUntracedReport = false;
    private int vh_class;
    private List list_vh_class = new ArrayList();
    private String underSection;
    private int district;
    private List list_district = new ArrayList();
    private String colour;
    private Date order_dt;
    private List<BlackListedVehicleDobj> untracedVehicleList = null;
    private boolean renderUntracedPanel;
    private String court_order_no;
    private String untracedFirNo;
    private String untracedPoliceStation;
    private String untracedChasiNo;
    private boolean render_regn_chasi_no_untraced_radio_btn = false;
    private Calendar fromOrder_dt = new Calendar();
    private String untracedRegnChasiNoRadiobtn = "";
    private boolean disableUntracedRegnno = false;
    private String viewReportbyLabel;

    public BlackListedVehicleBean() {
        HttpSession session = Util.getSession();
        this.usercd = session.getAttribute("emp_cd").toString();
        this.offcd = String.valueOf(Util.getSelectedSeat().getOff_cd());
        this.statecd = session.getAttribute("state_cd").toString();
        this.statename = session.getAttribute("state_name").toString();
        this.username = session.getAttribute("emp_name").toString();
        this.offname = getOfficeName();
        FirDate = today;
        setComplain_dt(DateUtil.parseDate(DateUtil.getCurrentDate()));
        listTheftDetails = new ArrayList();
        renderTxtArea = false;
        renderfir = false;
        action_dt = new Date();
        chasiRegnRadiobtn = "R";
        searchLabel = "Get Detail By Registration No";
        btnGetDetails = false;
        maxlenght = 10;
        list_blacklistedvehicle = new ArrayList();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String[][] data = MasterTableFiller.masterTables.VM_BLACKLIST.getData();
        listTheftDetails.add(new SelectItem("-1", "Select"));
        for (int i = 0; i < data.length; i++) {
            listTheftDetails.add(new SelectItem(data[i][0], data[i][1]));
        }
        untracedRadiobtn = "E";
        untracedRegnChasiNoRadiobtn = "REGNNO";
        data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
        list_vh_class.add(new SelectItem("-1", "--Select--"));
        for (int i = 0; i < data.length; i++) {
            list_vh_class.add(new SelectItem(data[i][0], data[i][1]));
        }
        data = MasterTableFiller.masterTables.TM_DISTRICT.getData();
        list_district.clear();
        for (int i = 0; i < data.length; i++) {
            list_district.add(new SelectItem(data[i][0], data[i][1]));
        }
        setOrder_dt(today);
        setRender_regn_chasi_no_untraced_radio_btn(true);
        setViewReportbyLabel("Registration No");
    }

    public void unblockAction() {
        setHeader("Action to Release Vehicle");
        setBt_save(false);
        setBt_cancel(false);
        String selectedNo;
        String complaine_dt;
        showunblockpanel = true;
        List<BlackListedVehicleDobj> untracedblackListedDobj;
        String selectedRegnNo = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selectedregn_no");
        String selectedChasiNo = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("selectedchasi_no");
        indexValue = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("indexValue");
        complaine_dt = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("complainant_dt");
        BlackListedVehicleImpl blacklistedVehicle_Impl = new BlackListedVehicleImpl();
        try {
            if (selectedRegnNo != null && !selectedRegnNo.isEmpty()) {
                untracedblackListedDobj = blacklistedVehicle_Impl.getUntracedRCDetail(selectedRegnNo, null);
                if (untracedblackListedDobj.size() >= 1) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert", "Vehicle Can't Released because the Untraced report is recorded"));
                    return;
                }
            }
            if (chasiRegnRadiobtn.equalsIgnoreCase("R")) {
                selectedNo = selectedRegnNo;
                add_by_regn_no = true;
            } else {
                selectedNo = selectedChasiNo;
                add_by_regn_no = false;
            }
            DateFormat readFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            DateFormat writeFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            Date date = readFormat.parse(complaine_dt);
            setComplaindt(writeFormat.format(date));
            setDeletecomplaindt(writeFormat.format(date));
            setBlacklistedVehicle_dobj(blacklistedVehicle_Impl.unblockActionTaken(selectedNo, getDeletecomplaindt(), search_by_regn_no));
            if (getBlacklistedVehicle_dobj() != null) {
                flag = true;
                disableUnblockPanel = true;
                if (getBlacklistedVehicle_dobj().getComplain_type() == 1) {
                    renderfir = true;
                    renderTxtArea = false;
                    FirNo = getBlacklistedVehicle_dobj().getFirNo();
                    FirDate = getBlacklistedVehicle_dobj().getFirDate();
                    PoliceStation = getBlacklistedVehicle_dobj().getPoliceStation();
                } else if (getBlacklistedVehicle_dobj().getComplain_type() == 62) {
                    rendercompundingdetails = true;
                    renderTxtArea = false;
                    renderfir = false;
                    FirNo = getBlacklistedVehicle_dobj().getFirNo();
                    compoundingamount = getBlacklistedVehicle_dobj().getCompounding_amt();
                } else {
                    renderfir = false;
                    renderTxtArea = true;
                    rendercompundingdetails = false;
                }
                complain_dt = getBlacklistedVehicle_dobj().getComplain_dt();
                regin_no = getBlacklistedVehicle_dobj().getRegin_no();
                chasi_no = getBlacklistedVehicle_dobj().getChasi_no();
                complain_type = getBlacklistedVehicle_dobj().getComplain_type();
                entered_by = getBlacklistedVehicle_dobj().getEntered_by();
                actiontaken = "";
                setComplaintype_Desc(String.valueOf(complain_type));
                complain = getBlacklistedVehicle_dobj().getComplain();
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
//        PrimeFaces.current().ajax().update("releaseVehicle");
        PrimeFaces.current().executeScript("PF('saveAndReleaseVehicle').show();");
    }

    public void saveUntracedVehicleDetail() {
        BlackListedVehicleImpl blacklistedVehicle_Impl;
        try {
            BlackListedVehicleDobj dobj = setBeantoUntracedDobj();
            if (dobj.getRegin_no() == null || dobj.getRegin_no().trim().equalsIgnoreCase("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration No should not be blank!!!"));
                return;
            }
            if (dobj.getChasi_no().trim().equalsIgnoreCase("") || dobj.getChasi_no() == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Chassis No should not be blank!!!"));
                return;
            }
            if (dobj.getVh_class() == 0) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Vehicle Class Should Not Blank!!!"));
                return;
            }
            if (dobj.getColour() == null || dobj.getColour().trim().equalsIgnoreCase("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Vehicle Colour Should Not Blank!!!"));
                return;
            }
            if (dobj.getDistrict() == 0) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Dictrict Should Not Blank!!!"));
                return;
            }
            if (dobj.getFirNo() == null || dobj.getFirNo().trim().equalsIgnoreCase("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Case FIR no Should Not Blank!!!"));
                return;
            }
            if (dobj.getPoliceStation() == null || dobj.getPoliceStation().trim().equalsIgnoreCase("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Police Station Should Not Blank!!!"));
                return;
            }
            if (dobj.getUnderSection() == null || dobj.getUnderSection().trim().equalsIgnoreCase("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "U/S Should Not Blank!!!"));
                return;
            }
            if (dobj.getOrder_dt() == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Court Order Date Should Not Blank!!!"));
                return;
            }
            if (dobj.getCourt_order_no() == null || dobj.getCourt_order_no().trim().equalsIgnoreCase("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Court Order No Should Not Blank!!!"));
                return;
            }
            blacklistedVehicle_Impl = new BlackListedVehicleImpl();
            boolean insertrecord = blacklistedVehicle_Impl.saveUntracedVehicleDetail(dobj);
            if (insertrecord) {
                reset();
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", "Untraced Vehicle save Successfully!!!!!"));
                //RequestContext context = RequestContext.getCurrentInstance();
                PrimeFaces.current().executeScript("PF('saveAndReleaseVehicle').hide();");
                return;
            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!!!", "Error in Saving Untraced Vehicle!!!!!"));
                return;
            }

        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

    public void onFirDateSelect() throws ParseException {
        String maxFirDt = getFromFirDate().getValue().toString();
        BlackListedVehicleImpl imp = new BlackListedVehicleImpl();
        java.sql.Date firDate = new java.sql.Date((((java.util.Date) fromFirDate.getValue()).getTime()));
        String regnDt = "";
        maxRegndt.clear();
        try {
            if (!regin_no.equalsIgnoreCase("")) {
                maxRegndt = imp.getDetailsRegnNo(regin_no.toUpperCase());

                if (maxRegndt.size() > 0) {
                    regnDt = maxRegndt.get(3).toString();
                }
            } else {
                maxRegndt = imp.getDetailsChassisNo(getChasi_no().toUpperCase());

                if (maxRegndt.size() <= 0) {
                    maxRegndt.add(firDate);
                }
            }
            SimpleDateFormat farmater = new SimpleDateFormat("yyyy-MM-dd");
            if (maxRegndt.size() > 1) {
                regnDt = maxRegndt.get(3).toString();
            } else {
                regnDt = maxRegndt.get(0).toString();
            }
            if (!regnDt.isEmpty()) {
                Date regDt = farmater.parse(regnDt);
                if (firDate.before(regDt)) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Fir Date Should Not Less Than Rgistration Date! Please Select Another Date !!!"));
                    this.FirDate = null;
                    return;
                }
            } else {
                return;
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

    public void radioBtnListener() {
        blacklist.clear();
        releasedlist.clear();
        this.regn_no = "";
        String radioBtnvalue = chasiRegnRadiobtn;
        setRenderUntracedReport(false);
        if (radioBtnvalue.equalsIgnoreCase("R")) {
            setBtnGetDetails(false);
            setSearchLabel("Get Detail By Registration No");
            setMaxlenght(10);
        } else {
            setBtnGetDetails(false);
            setSearchLabel("Get Detail By Chassis No");
            setMaxlenght(30);
        }
    }

    public void radioUntracedTabListener() {
        blacklist.clear();
        releasedlist.clear();
        if (untracedVehicleList != null) {
            untracedVehicleList.clear();
        }
        reset();
        this.untracedRegnChasiNoRadiobtn = "REGNNO";
        setViewReportbyLabel("Registration No");
        setMaxlenght(10);
        setDisableUntracedRegnno(false);
        String untracedradioBtnvalue = untracedRadiobtn;
        if (untracedradioBtnvalue.equalsIgnoreCase("E")) {
            setRenderUntracedEntry(true);
            setRenderUntracedReport(false);
            setRender_regn_chasi_no_untraced_radio_btn(true);
        } else {
            setRenderUntracedEntry(false);
            setRenderUntracedReport(true);
            setRender_regn_chasi_no_untraced_radio_btn(true);
        }
    }

    public void radioUntracedRegnChasiNoListener() {
        blacklist.clear();
        releasedlist.clear();
        if (untracedVehicleList != null) {
            untracedVehicleList.clear();
        }
        reset();
        if (untracedRadiobtn.equalsIgnoreCase("V")) {
            if (untracedRegnChasiNoRadiobtn.equalsIgnoreCase("REGNNO")) {
                setViewReportbyLabel("Registration No");
                setMaxlenght(10);
                setDisableUntracedRegnno(false);

            } else if (untracedRegnChasiNoRadiobtn.equalsIgnoreCase("CHASINO")) {
                setViewReportbyLabel("Chassis No");
                setMaxlenght(30);
                setDisableUntracedRegnno(true);
            }
        } else if (untracedRadiobtn.equalsIgnoreCase("E")) {
            if (untracedRegnChasiNoRadiobtn.equalsIgnoreCase("CHASINO")) {
                setDisableUntracedRegnno(true);
            } else {
                setDisableUntracedRegnno(false);
            }
        }
    }

    public void fetchRegnNo() {
        if (regin_no.trim().equalsIgnoreCase("")) {
            return;
        }
        if (regin_no == null || regin_no.trim().equalsIgnoreCase("") || regin_no.trim().length() < 4) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration No should not be less than 4 digit!"));
            return;
        }
        BlackListedVehicleImpl blacklistedVehicle_Impl;
        try {
            blacklistedVehicle_Impl = new BlackListedVehicleImpl();

            List<String> list1 = blacklistedVehicle_Impl.getAlreadyRegnNoBlacklisted(regin_no.trim().toUpperCase());

            if (!list1.isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration No Aleady Blacklisted on Registering Authority      \"" + list1.get(3).toUpperCase() + "\"    by User     \"" + list1.get(4).toUpperCase() + "\""));
                return;

            }
            List<String> list = blacklistedVehicle_Impl.getDetailsRegnNo(regin_no.trim().toUpperCase());
            if (list.size() <= 0) {
                this.regin_no = null;
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration does not exist!"));
                return;
            }
            if (Boolean.parseBoolean(list.get(1))) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "This Vehicle is already Black Listed with Chassis No \"" + list.get(2).toUpperCase() + "\" Please try again!"));
                this.regin_no = null;
                return;
            }
            if (!Boolean.parseBoolean(list.get(1))) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", " Chassis No \"" + list.get(2).toUpperCase() + "\""));

                return;
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

    public void onComplaineSelect() {
        setComplain(this.complain);
    }

    public void onSelectFirNo() {
        setComplain(this.FirNo);
    }

    public void onSelectPS() {
        setPoliceStation(this.PoliceStation);
    }

    public void onBlurOfFileNo() {
        setFirNo(this.FirNo);
    }

    public void onBlurOfCompAmt() {
        setCompoundingamount(this.compoundingamount);
    }

    public void onComplaineDateSelect() {
        DateFormat writeFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        String Complaine_dt = writeFormat.format(this.complain_dt);
        String regnOrChasiNo = null;
        if (chasiRegnRadiobtn.equals("R")) {
            regnOrChasiNo = regin_no.trim().toUpperCase();
        }
        if (chasiRegnRadiobtn.equals("C")) {
            regnOrChasiNo = chasi_no.trim().toUpperCase();
        }
        BlackListedVehicleImpl blacklistedVehicle_Impl;
        try {
            blacklistedVehicle_Impl = new BlackListedVehicleImpl();
            if (blacklistedVehicle_Impl.isDupComplainDate(Complaine_dt, chasiRegnRadiobtn, regnOrChasiNo)) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "This Vehicle is already Black Listed with this Complaine DateTime. Please select different Complaine DateTime!"));
                return;
            }

        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

    public void fetchChassisNo() {
        if (chasi_no.trim().equalsIgnoreCase("")) {
            return;
        }
        if (chasi_no == null || chasi_no.trim().equalsIgnoreCase("") || chasi_no.trim().length() < 5) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Chassis No should not be less than 5 digit!"));
            return;
        }
        BlackListedVehicleImpl blacklistedVehicle_Impl;
        try {
            blacklistedVehicle_Impl = new BlackListedVehicleImpl();

            List<String> list1 = blacklistedVehicle_Impl.getAlreadyChassisNoBlacklisted(chasi_no.trim().toUpperCase());

            if (!list1.isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Chassis No Aleady Blacklisted on Registering Authority      \"" + list1.get(3).toUpperCase() + "\"    by User     \"" + list1.get(4).toUpperCase() + "\""));
                return;

            }
            List<String> list = blacklistedVehicle_Impl.getDetailsChassisNo(chasi_no.trim().toUpperCase());

            if (list.size() <= 0) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Chassis No does not exist!"));

            }

            if (!list.isEmpty() && Boolean.parseBoolean(list.get(1))) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "This Vehicle is already Black Listed with Registration No \"" + list.get(2).toUpperCase() + "\" Please try again!"));
                this.chasi_no = null;
                return;
            }
            if (!list.isEmpty() && !Boolean.parseBoolean(list.get(1))) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", " Registration No \"" + list.get(2).toUpperCase() + "\""));

                return;
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, TableConstants.SomthingWentWrong, TableConstants.SomthingWentWrong));
            return;
        }
    }

    public void cancel() {
        reset();
    }

    public void getUntracedRCDetail() {
        if (regn_no == null || regn_no.trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please Enter Registration No!"));
            return;
        }
        BlackListedVehicleImpl blacklistedVehicle_Impl;
        try {
            if (getUntracedVehicleList() != null && !getUntracedVehicleList().isEmpty()) {
                getUntracedVehicleList().clear();
            }
            blacklistedVehicle_Impl = new BlackListedVehicleImpl();
            //Check details found in vt_blacklist

            setUntracedVehicleList(blacklistedVehicle_Impl.getUntracedRCDetail(regn_no, untracedRegnChasiNoRadiobtn));
            if (getUntracedVehicleList() == null || getUntracedVehicleList().isEmpty()) {
                setRenderUntracedPanel(false);
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Untraced Vehicle Report not found !!"));
                return;
            } else {
                setRenderUntracedPanel(true);
            }

        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return;
        }
    }

    public String printUntracedVehicleReportRC(BlackListedVehicleDobj Dobj) {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("UntracedDobj", Dobj);
        return "printUntracedVehicleReport";
    }

    public void showblacklist() {
        blacklist.clear();
        String radioBtnvalue = chasiRegnRadiobtn;
        Object[] blackAndReleasedArray = null;
        if (regn_no == null || regn_no.trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please Enter Registration / Chassis No!"));
            return;
        }
        if (radioBtnvalue.equalsIgnoreCase("R")) {
            search_by_regn_no = true;
            BlackListedVehicleImpl blacklistedVehicle_Impl;
            try {
                blacklistedVehicle_Impl = new BlackListedVehicleImpl();
                //Check details found in vt_blacklist
                Boolean isExit = blacklistedVehicle_Impl.checkRegnNoForBlackList(regn_no.trim().toUpperCase());
                //Check details found in vt_owner
                List<String> list = blacklistedVehicle_Impl.getDetailsRegnNo(regn_no.trim().toUpperCase());
                if (isExit) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "BlackList Record is found !!!"));
                }
                if (list.isEmpty() && isExit) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration No does not exist  But BlackList Record is Found!!!"));
                }
                if (!list.isEmpty() && isExit) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", " BlackList Record is Found!!!"));
                }
                if (list.isEmpty() && !isExit) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "No Black list Details found for Registration No \"" + regn_no.toUpperCase() + "\" Please try again!"));
                    //return;
                }
                if (!isExit) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", " BlackList Record Not Found!!!"));
                }
                if (!list.isEmpty() && Boolean.parseBoolean(list.get(1))) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "This Vehicle is already Black Listed with Chassis No \"" + list.get(2).toUpperCase() + "\" Please try again!"));
                    //  return;
                }
                blacklistvehicledobjlist = new ArrayList<>();
                releasedlistvehicledobjlist = new ArrayList<>();
                Vector vecDataVector = blacklistedVehicle_Impl.getCurrentStatus(regn_no.toUpperCase(), radioBtnvalue);
                if (!vecDataVector.isEmpty()) {
                    blacklistvehicledobjlist = (List<BlackListedVehicleDobj>) vecDataVector.get(0);
                    if (vecDataVector.size() > 1) {
                        releasedlistvehicledobjlist = (List<BlackListedVehicleDobj>) vecDataVector.get(1);
                    }
                }
                blacklist.clear();
                if (blacklistvehicledobjlist.size() > 0) {
                    blacklist.addAll(blacklistvehicledobjlist);
                }
                releasedlist.clear();
                if (releasedlistvehicledobjlist.size() > 0) {
                    releasedlist.addAll(releasedlistvehicledobjlist);
                    return;
                }
                if (releasedlistvehicledobjlist.size() <= 0 && blacklistvehicledobjlist.size() <= 0) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "No Black list Details found for Registration No \"" + regn_no.toUpperCase() + "\" Please try again!"));
                    return;
                }
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("blacklistbean", blacklist);

                for (int i = 0; i < blacklist.size(); i++) {
                    BlackListedVehicleDobj bl = blacklist.get(i);
                }
            } catch (VahanException ve) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
                return;
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
                return;
            }
        } else if (radioBtnvalue.equalsIgnoreCase("C")) {
            search_by_regn_no = false;
            BlackListedVehicleImpl blacklistedVehicle_Impl;
            try {
                blacklistedVehicle_Impl = new BlackListedVehicleImpl();
                String BlackListedDetails = null;
                BlackListedDetails = blacklistedVehicle_Impl.checkChassisNoForBlackList(regn_no.trim().toUpperCase());
                List<String> list = blacklistedVehicle_Impl.getDetailsChassisNo(regn_no.trim().toUpperCase());
                if (BlackListedDetails != null) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "BlackList Record is found !!!"));
                }
                if (list.isEmpty() && BlackListedDetails != null) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Chassis No does not exist  But BlackList Record is Found!!!"));
                }
                if (!list.isEmpty() && BlackListedDetails != null) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", " BlackList Record is Found!!!"));
                }
                if (list.isEmpty() && BlackListedDetails == null) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "No Black list Details found for Chassis No \"" + regn_no.toUpperCase() + "\" Please try again!"));
                    //return;
                }
                if (BlackListedDetails == null) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", " BlackList Record Not Found!!!"));
                }
                if (!list.isEmpty() && Boolean.parseBoolean(list.get(1))) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "This Vehicle is already Black Listed with Registration No \"" + list.get(2).toUpperCase() + "\" Please try again!"));
                    //  return;
                }
                List<BlackListedVehicleDobj> blacklistvehicledobjlist = new ArrayList<>();
                List<BlackListedVehicleDobj> releasedlistvehicledobjlist = new ArrayList<>();
                Vector vecDataVector = blacklistedVehicle_Impl.getCurrentStatus(regn_no.toUpperCase(), radioBtnvalue);
                if (!vecDataVector.isEmpty()) {
                    blacklistvehicledobjlist = (List<BlackListedVehicleDobj>) vecDataVector.get(0);
                    if (vecDataVector.size() > 1) {
                        releasedlistvehicledobjlist = (List<BlackListedVehicleDobj>) vecDataVector.get(1);
                    }
                }
                blacklist.clear();
                blacklist.addAll(blacklistvehicledobjlist);
                releasedlist.clear();
                releasedlist.addAll(releasedlistvehicledobjlist);
                return;
            } catch (VahanException ve) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
                return;
            } catch (Exception ex) {
                LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
                return;
            }
        } else {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Please Enter Registration / Chassis No!"));
        }
    }

    private String getOfficeName() {
        String officeName = "";
        Map<String, Object> allotedOffCodeList = Home_Impl.getAllotedOfficeCodeDescr(false);
        for (Map.Entry<String, Object> offcdEntry : allotedOffCodeList.entrySet()) {
            officeName = offcdEntry.getKey();
            break;
        }
        return officeName;
    }

    public void confirmprintBlackListReports() {
        PrimeFaces.current().ajax().update("printreleasehistory");
        PrimeFaces.current().executeScript("PF('printblacklist').show();");
    }

    public String printBlackListReports() {
        return "BlacklistReport";
    }

    public void reset() {
        this.regin_no = null;
        this.chasi_no = null;
        this.complain_type = -1;
        this.regn_no = "";
        this.regin_no = "";
        regin_no = "";
        this.chasi_no = "";
        this.complain = "";
        setComplain_type(-1);
        this.complain_type = -1;
        this.complaintype_Desc = "";
        this.FirNo = "";
        this.FirDate = null;
        this.PoliceStation = "";
        this.setCompoundingamount(0L);
        this.underSection = "";
        this.untracedPoliceStation = "";
        this.untracedChasiNo = null;
        this.colour = null;
        this.court_order_no = null;
        this.order_dt = null;
        this.colour = "";

    }

    public void addToBlackList() {
        String radioBtnvalue = chasiRegnRadiobtn;
        if (radioBtnvalue.equalsIgnoreCase("R")) {
            this.regn_no = "";
            this.regin_no = "";
            setHeader("New Complain with Registration No");
            setSaveButton("Save With Regn No");
            rendervisibleRegn = true;
            add_by_regn_no = true;
        } else if (radioBtnvalue.equalsIgnoreCase("C")) {
            setHeader("New Complain with Chassis No");
            setSaveButton("Save With Chassis No");
            rendervisibleChassis = true;
            add_by_regn_no = false;
            this.chasi_no = "";
        }
        this.FirDate = null;
        this.complain_type = -1;
        reset();
        setBt_save(true);
        setBt_cancel(true);
        flag = true;
        setShowunblockpanel(false);
        disableUnblockPanel = false;
        renderTxtArea = false;
        renderfir = false;
        setRennewpanel(true);
        setComplain_dt(DateUtil.parseDate(DateUtil.getCurrentDate()));
//        PrimeFaces.current().ajax().update("msgdlg");
        PrimeFaces.current().executeScript("PF('saveAndReleaseVehicle').show();");

    }

    public void UnblockVehicle(BlackListedVehicleBean dobj) {
        try {
            blacklist = (List) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("backlistbean");
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("recordToUnblock_dobj", dobj);
            PrimeFaces.current().dialog().openDynamic("unblockvehicle");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void saveApplication() {

        HttpSession session = Util.getSession();
        this.setUsercd(session.getAttribute("emp_cd").toString());
        this.setOffcd(session.getAttribute("off_cd").toString());
        this.setStatecd(session.getAttribute("state_cd").toString());
        this.setStatename(session.getAttribute("state_name").toString());
        this.setUsername(session.getAttribute("emp_name").toString());
        this.setOffname(getOfficeName());
        try {
            BlackListedVehicleDobj dobj = setBeantoDobj();
            BlackListedVehicleImpl imp = new BlackListedVehicleImpl();
            if (dobj.getComplain_type() == 0) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Complain Type should not be blank!!!"));
                return;
            }
            if ((this.complain.trim().equalsIgnoreCase("") || dobj.getComplain() == null) && dobj.getComplain_type() != 1 && dobj.getComplain_type() != TableConstants.BLCompoundingAmtCode) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Complain  should not be blank!!!"));
                return;
            }
            if (dobj.getComplain_dt() == null || dobj.getComplain_dt().equals("")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Complaine Date Should Not Blank!!!"));
                return;
            }
            if (add_by_regn_no) {
                BlackListedVehicleImpl blacklistedVehicle_Impl;
                blacklistedVehicle_Impl = new BlackListedVehicleImpl();
                if (dobj.getRegin_no() == null || dobj.getRegin_no().equals("")) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration No  should not be blank!!!"));
                    return;
                }
                if (dobj.getComplain_type() == 1) {
                    if (dobj.getFirNo() == null || dobj.getFirNo().equals("")) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Fir No Should Not Blank!!!"));
                        return;
                    }
                    if (dobj.getFirDate() == null || dobj.getFirDate().equals("")) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Fir Date Should Not Blank!!!"));
                        return;
                    }
                    if (dobj.getPoliceStation() == null || dobj.getPoliceStation().equals("")) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Police Station Should Not Blank!!!"));
                        return;
                    }
                }
                if (dobj.getComplain_type() == TableConstants.BLCompoundingAmtCode) {
                    if (dobj.getFirNo() == null || dobj.getFirNo().equals("")) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "File No Should Not Blank!!!"));
                        return;
                    }
                    if (dobj.getCompounding_amt() <= 0) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Compounding Amount Should Not be 0!!!"));
                        return;
                    }
                }
            } else {
                if (dobj.getChasi_no() == null || dobj.getChasi_no().equals("")) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Chassis No should not Blank!!"));
                    return;
                }
                if (dobj.getComplain_type() == 1) {
                    if (dobj.getFirNo() == null || dobj.getFirNo().equals("")) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Fir No Should Not Blank!!!"));
                        return;
                    }
                    if (dobj.getFirDate() == null || dobj.getFirDate().equals("")) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Fir Date Should Not Blank!!!"));
                        return;
                    }
                    if (dobj.getPoliceStation() == null || dobj.getPoliceStation().equals("")) {
                        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Police Station Should Not Blank!!!"));
                        return;
                    }
                } else if (dobj.getComplain_type() == TableConstants.BLCompoundingAmtCode) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!!!", "Vehicle can not be blacklisted for compounding amount through chassis number!!!!!"));
                    return;
                }
            }
            boolean insertrecord = imp.insertIntoBlacklistedVehicle(dobj, add_by_regn_no);
            if (insertrecord) {
                reset();
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Info", "Vehicle Black Listed Successfully!!!!!"));
                PrimeFaces.current().executeScript("PF('saveAndReleaseVehicle').hide();");
                return;
            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!!!", "Error in Saving Black Listed!!!!!"));
                return;
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return;
        }
    }

    public BlackListedVehicleDobj setBeantoDobj() throws VahanException {
        BlackListedVehicleDobj dobj = null;
        try {
            dobj = new BlackListedVehicleDobj();
            if (this.getRegin_no() != null) {
                dobj.setRegin_no(this.getRegin_no().trim().toUpperCase());
            }
            if (this.getComplain_dt() != null) {
                dobj.setComplain_dt(this.getComplain_dt());
            }
            if (this.getChasi_no() != null && !this.getChasi_no().isEmpty()) {
                dobj.setChasi_no(this.getChasi_no().trim().toUpperCase());
            }
            if (this.getComplain() != null) {
                dobj.setComplain(this.getComplain().toUpperCase());
            }
            if (this.getComplain_type() != 0 && this.getComplain_type() != -1) {
                dobj.setComplain_type(this.complain_type);
            }
            if (this.off_cd != 0) {
                dobj.setOff_cd(this.off_cd);
            }
            if (this.getState_cd() != null) {
                dobj.setState_cd(this.state_cd);
            }
            if (this.entered_by != null) {
                dobj.setEntered_by(this.entered_by);
            }
            if (this.getFirNo() != null) {
                dobj.setFirNo(this.getFirNo().toUpperCase());
            }
            if (this.getPoliceStation() != null) {
                dobj.setPoliceStation(this.getPoliceStation().toUpperCase());
            }
            if (this.getFirDate() != null) {
                dobj.setFirDate(this.getFirDate());
            }
            if (this.action_dt != null) {
                dobj.setAction_dt(action_dt);
            }
            if (this.actiontaken != null) {
                dobj.setActiontaken(this.getActiontaken().toUpperCase());
            }
            dobj.setComplaindt(getComplaindt());
            dobj.setDeletecomplaindt(getDeletecomplaindt());
            dobj.setCompounding_amt(getCompoundingamount());
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return dobj;
    }

    public BlackListedVehicleDobj setBeantoUntracedDobj() throws VahanException {
        BlackListedVehicleDobj dobj = null;
        try {
            dobj = new BlackListedVehicleDobj();
            if (this.getRegn_no() != null) {
                dobj.setRegin_no(this.getRegn_no().trim().toUpperCase());
            }
            if (this.getUntracedChasiNo() != null) {
                dobj.setChasi_no(this.getUntracedChasiNo().trim().toUpperCase());
            }

            if (this.getUnderSection() != null && !this.getUnderSection().isEmpty()) {
                dobj.setUnderSection(this.underSection.trim().toUpperCase());
            }

            if (this.vh_class != 0) {
                dobj.setVh_class(this.vh_class);
            }

            if (this.entered_by != null) {
                dobj.setEntered_by(Util.getEmpCode());
            }
            if (this.getUntracedFirNo() != null) {
                dobj.setFirNo(this.getUntracedFirNo().trim().toUpperCase());
            }
            if (this.getUntracedPoliceStation() != null) {
                dobj.setPoliceStation(this.getUntracedPoliceStation().trim().toUpperCase());
            }
            if (this.getDistrict() != 0) {
                dobj.setDistrict(this.getDistrict());
            }
            if (this.colour != null) {
                dobj.setColour(this.colour.trim().toUpperCase());
            }
            if (this.getOrder_dt() != null) {
                dobj.setOrder_dt(this.getOrder_dt());
            }
            if (this.getCourt_order_no() != null) {
                dobj.setCourt_order_no(this.getCourt_order_no());
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return dobj;
    }

    public void fetchUntracedRegnNo() {
        if (regn_no.trim().equalsIgnoreCase("")) {
            return;
        }
        if (regn_no == null || regn_no.trim().equalsIgnoreCase("") || regn_no.trim().length() < 4) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration No should not be blank and less than 4 digit!"));
            return;
        }
        BlackListedVehicleImpl blacklistedVehicle_Impl;
        try {
            blacklistedVehicle_Impl = new BlackListedVehicleImpl();
            List<String> list = blacklistedVehicle_Impl.getAlreadyRegnNoBlacklisted(regn_no.trim().toUpperCase());
            if (list.isEmpty()) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration No " + regn_no.trim().toUpperCase() + " Either does'nt exist OR Not Blacklisted"));
                this.regn_no = null;
                return;
            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Registration No " + regn_no.trim().toUpperCase() + " is Blacklisted on Registering Authority      \"" + list.get(3).toUpperCase() + "\"    by User     \"" + list.get(4).toUpperCase() + "\""));
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return;
        }
    }

    public void fetchUntracedChassisNo() {
        if (untracedChasiNo.trim().equalsIgnoreCase("")) {
            return;
        }
        if (this.regn_no != null && !this.regn_no.isEmpty()) {
            return;
        }
        if (untracedChasiNo == null || untracedChasiNo.trim().equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Chassis No " + untracedChasiNo.trim().toUpperCase() + " should not be blank!"));
            return;
        }
        BlackListedVehicleImpl blacklistedVehicle_Impl;
        try {
            blacklistedVehicle_Impl = new BlackListedVehicleImpl();

            String BlackListedDetails = blacklistedVehicle_Impl.checkChassisNoForBlackList(untracedChasiNo.trim().toUpperCase());
            if (BlackListedDetails == null) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Vehicle with chassis no " + untracedChasiNo.trim().toUpperCase() + " Either does'nt exist OR Not Blacklisted"));
                this.untracedChasiNo = null;
                return;
            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", BlackListedDetails));
            }
        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return;
        }
    }

    public void onUntracedFirDateSelect() throws ParseException {
        BlackListedVehicleImpl imp = new BlackListedVehicleImpl();
        java.sql.Date orderFirDt = new java.sql.Date(getOrder_dt().getTime());
        Date fir_date = null;
        String whereClauseForFIRDate = null;
        try {
            if (untracedRegnChasiNoRadiobtn.equals("REGNNO")) {
                whereClauseForFIRDate = "select fir_dt from vt_blacklist where  regn_no ='" + this.regn_no.toUpperCase() + "' order by complain_dt desc limit 1";
            } else if (untracedRegnChasiNoRadiobtn.equals("CHASISNO")) {
                whereClauseForFIRDate = "select fir_dt from vt_blacklist_chassis where  chasi_no =' " + this.untracedChasiNo.toUpperCase() + "' order by complain_dt desc limit 1";
            }
            fir_date = imp.getFirDetail(whereClauseForFIRDate);
            if (orderFirDt != null && fir_date != null && orderFirDt.before(fir_date)) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Order Date Should Not Less Than FIR Date of Blacklist record! Please Select Another Date !!!"));
                this.order_dt = null;
                return;
            }

        } catch (VahanException ve) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ve.getMessage(), ve.getMessage()));
            return;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            return;
        }
    }

    public void fillFirDetailsPanel(AjaxBehaviorEvent event) {
        this.FirDate = null;
        this.FirNo = null;
        this.PoliceStation = null;
        int firdetails = this.complain_type;
        if (firdetails == 1) {
            renderTxtArea = false;
            renderfir = true;
            rendercompundingdetails = false;
        } else if (firdetails == TableConstants.BLCompoundingAmtCode) {
            if (!add_by_regn_no) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!!!", "Vehicle can not be blacklisted for compounding amount through chassis number!!!!!"));
                return;
            }
            try {
                OwnerImpl ownerImpl = new OwnerImpl();
                List<OwnerDetailsDobj> ownerDetailsDobjList = ownerImpl.getOwnerDetailsList(regin_no.trim().toUpperCase(), statecd);
                if (ownerDetailsDobjList == null || ownerDetailsDobjList.isEmpty()) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Invalid Registration Number or Registration No not found in the Record"));
                    return;
                }
                if (ownerDetailsDobjList.size() > 1) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Duplicate Records found for this Registration Number... Please remove duplicate records first."));
                    return;
                }
                if ("TN".contains(statecd) && ownerDetailsDobjList.get(0).getOff_cd() != Util.getSelectedSeat().getOff_cd()) {
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert", "Vehicle does not belong to this RTO!!!!"));
                    return;
                }

            } catch (VahanException e) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!!!", "Vehicle details not found or Vehicle does not belong to this RTO!!!!"));
                return;
            }

            rendercompundingdetails = true;
            renderTxtArea = false;
            renderfir = false;
        } else {
            renderTxtArea = true;
            renderfir = false;
            rendercompundingdetails = false;
        }
    }

    public void SaveAction() throws VahanException {

        String selectedRegnNo = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("releaseRegn");
        String selectedChasiNo = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("releaseChassis");
        String releaseSeleectedDate = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("releaseDate");
        Exception e = null;
        if (this.actiontaken == null || this.actiontaken.equalsIgnoreCase("")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Action Taken should not be blank!", "Action Taken should not be blank"));
            return;
        }
        BlackListedVehicleDobj dobj = setBeantoDobj();
        BlackListedVehicleImpl blacklistedVehicle_Impl = new BlackListedVehicleImpl();
        try {
            if (dobj != null) {
                boolean transactionCompleted = blacklistedVehicle_Impl.unblockcurrentrow(dobj, add_by_regn_no);
                if (transactionCompleted) {
                    blacklist.remove(Integer.parseInt(indexValue));
                    reset();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Info!", "Vehicle Released Successfully"));
                    //PrimeFaces.current().ajax().update("releaseVehicle");
                    PrimeFaces.current().executeScript("PF('saveAndReleaseVehicle').hide();");
                    return;
                }
            }
        } catch (VahanException ee) {
            e = ee;
        } catch (Exception ee) {
            e = ee;
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        if (e != null) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
            return;
        }
    }

    public ArrayList getList_blacklistedvehicle() {
        return list_blacklistedvehicle;
    }

    public void onItemSelect(SelectEvent event) {
        PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Item Selected!", event.getObject().toString()));

    }

    public List<BlackListedVehicleDobj> getBlacklist() {
        return blacklist;
    }

    public List<BlackListedVehicleBean> getCurrlist() {
        return currlist;
    }

    public void setCurrlist(List<BlackListedVehicleBean> currlist) {
        this.currlist = currlist;
    }

    public String getState_cd() {
        return state_cd;
    }

    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getChasi_no() {
        return chasi_no;
    }

    public void setChasi_no(String chasi_no) {
        this.chasi_no = chasi_no;
    }

    public String getComplain() {
        return complain;
    }

    public void setComplain(String complain) {
        this.complain = complain;
    }

    public String getEntered_by() {
        return entered_by;
    }

    public int getRowid() {
        return this.rowid;
    }

    public Date getAction_dt() {
        return action_dt;
    }

    public Date getFirDate() {
        return FirDate;
    }

    public void setFirDate(Date FirDate) {
        this.FirDate = FirDate;
    }

    public void setAction_dt(Date action_dt) {
        this.action_dt = action_dt;
    }

    public boolean isRenderfir() {
        return renderfir;
    }

    public void setRenderfir(boolean renderfir) {
        this.renderfir = renderfir;
    }

    public String getFirNo() {
        return FirNo;
    }

    public void setFirNo(String FirNo) {
        this.FirNo = FirNo;
    }

    /**
     * @return the complaintype_Desc
     */
    public String getComplaintype_Desc() {
        return complaintype_Desc;
    }

    /**
     * @param complaintype_Desc the complaintype_Desc to set
     */
    public void setComplaintype_Desc(String complaintype_Desc) {
        this.complaintype_Desc = complaintype_Desc;
    }

    /**
     * @return the renderTxtArea
     */
    public boolean isRenderTxtArea() {
        return renderTxtArea;
    }

    /**
     * @param renderTxtArea the renderTxtArea to set
     */
    public void setRenderTxtArea(boolean renderTxtArea) {
        this.renderTxtArea = renderTxtArea;
    }

    /**
     * @return the entered_no
     */
    public String getEntered_no() {
        return entered_no;
    }

    /**
     * @param entered_no the entered_no to set
     */
    public void setEntered_no(String entered_no) {
        this.entered_no = entered_no;
    }

    /**
     * @return the complaindt
     */
    public String getComplaindt() {
        return complaindt;
    }

    /**
     * @param complaindt the complaindt to set
     */
    public void setComplaindt(String complaindt) {
        this.complaindt = complaindt;
    }

    /**
     * @return the black_listed_vehicle_bean
     */
    public BlackListedVehicleBean getBlack_listed_vehicle_bean() {
        return black_listed_vehicle_bean;
    }

    /**
     * @param black_listed_vehicle_bean the black_listed_vehicle_bean to set
     */
    public void setBlack_listed_vehicle_bean(BlackListedVehicleBean black_listed_vehicle_bean) {
        this.black_listed_vehicle_bean = black_listed_vehicle_bean;
    }

    /**
     * @return the disableUnblockPanel
     */
    public boolean isDisableUnblockPanel() {
        return disableUnblockPanel;
    }

    /**
     * @param disableUnblockPanel the disableUnblockPanel to set
     */
    public void setDisableUnblockPanel(boolean disableUnblockPanel) {
        this.disableUnblockPanel = disableUnblockPanel;
    }

    /**
     * @return the search_by_regn_no
     */
    public boolean isSearch_by_regn_no() {
        return search_by_regn_no;
    }

    /**
     * @param search_by_regn_no the search_by_regn_no to set
     */
    public void setSearch_by_regn_no(boolean search_by_regn_no) {
        this.search_by_regn_no = search_by_regn_no;
    }

    /**
     * @param listTheftDetails the listTheftDetails to set
     */
    public void setListTheftDetails(ArrayList listTheftDetails) {
        this.listTheftDetails = listTheftDetails;
    }

    /**
     * @return the add_by_regn_no
     */
    public boolean isAdd_by_regn_no() {
        return add_by_regn_no;
    }

    /**
     * @param add_by_regn_no the add_by_regn_no to set
     */
    public void setAdd_by_regn_no(boolean add_by_regn_no) {
        this.add_by_regn_no = add_by_regn_no;
    }

    /**
     * @return the showunblockpanel
     */
    public boolean isShowunblockpanel() {
        return showunblockpanel;
    }

    /**
     * @param showunblockpanel the showunblockpanel to set
     */
    public void setShowunblockpanel(boolean showunblockpanel) {
        this.showunblockpanel = showunblockpanel;
    }

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @return the saveButton
     */
    public String getSaveButton() {
        return saveButton;
    }

    /**
     * @param saveButton the saveButton to set
     */
    public void setSaveButton(String saveButton) {
        this.saveButton = saveButton;
    }

    /**
     * @return the bt_save
     */
    public boolean isBt_save() {
        return bt_save;
    }

    /**
     * @param bt_save the bt_save to set
     */
    public void setBt_save(boolean bt_save) {
        this.bt_save = bt_save;
    }

    /**
     * @param entered_by the entered_by to set
     */
    public void setEntered_by(String entered_by) {
        this.entered_by = entered_by;
    }

    /**
     * @return the chasiRegnRadiobtn
     */
    public String getChasiRegnRadiobtn() {
        return chasiRegnRadiobtn;
    }

    /**
     * @param chasiRegnRadiobtn the chasiRegnRadiobtn to set
     */
    public void setChasiRegnRadiobtn(String chasiRegnRadiobtn) {
        this.chasiRegnRadiobtn = chasiRegnRadiobtn;
    }

    /**
     * @return the rennewpanel
     */
    public boolean isRennewpanel() {
        return rennewpanel;
    }

    /**
     * @param rennewpanel the rennewpanel to set
     */
    public void setRennewpanel(boolean rennewpanel) {
        this.rennewpanel = rennewpanel;
    }

    /**
     * @return the blacklistedVehicle_dobj
     */
    public BlackListedVehicleDobj getBlacklistedVehicle_dobj() {
        return blacklistedVehicle_dobj;
    }

    /**
     * @param blacklistedVehicle_dobj the blacklistedVehicle_dobj to set
     */
    public void setBlacklistedVehicle_dobj(BlackListedVehicleDobj blacklistedVehicle_dobj) {
        this.blacklistedVehicle_dobj = blacklistedVehicle_dobj;
    }

    /**
     * @return the indexValue
     */
    public String getIndexValue() {
        return indexValue;
    }

    /**
     * @param indexValue the indexValue to set
     */
    public void setIndexValue(String indexValue) {
        this.indexValue = indexValue;
    }

    /**
     * @return the maxlenght
     */
    public int getMaxlenght() {
        return maxlenght;
    }

    /**
     * @param maxlenght the maxlenght to set
     */
    public void setMaxlenght(int maxlenght) {
        this.maxlenght = maxlenght;
    }

    /**
     * @return the today
     */
    public Date getToday() {
        return today;
    }

    /**
     * @param today the today to set
     */
    public void setToday(Date today) {
        this.today = today;
    }

    /**
     * @return the searchLabel
     */
    public String getSearchLabel() {
        return searchLabel;
    }

    /**
     * @param searchLabel the searchLabel to set
     */
    public void setSearchLabel(String searchLabel) {
        this.searchLabel = searchLabel;
    }

    /**
     * @return the btnGetDetails
     */
    public boolean isBtnGetDetails() {
        return btnGetDetails;
    }

    /**
     * @param btnGetDetails the btnGetDetails to set
     */
    public void setBtnGetDetails(boolean btnGetDetails) {
        this.btnGetDetails = btnGetDetails;
    }

    /**
     * @return the releasedlist
     */
    public List<BlackListedVehicleDobj> getReleasedlist() {
        return releasedlist;
    }

    /**
     * @param releasedlist the releasedlist to set
     */
    public void setReleasedlist(List<BlackListedVehicleDobj> releasedlist) {
        this.releasedlist = releasedlist;
    }

    /**
     * @return the indexValueRelease
     */
    public String getIndexValueRelease() {
        return indexValueRelease;
    }

    /**
     * @param indexValueRelease the indexValueRelease to set
     */
    public void setIndexValueRelease(String indexValueRelease) {
        this.indexValueRelease = indexValueRelease;
    }

    public String getPoliceStation() {
        return PoliceStation;
    }

    public void setPoliceStation(String PoliceStation) {
        this.PoliceStation = PoliceStation;
    }

    public boolean isRendervisibleRegn() {
        return rendervisibleRegn;
    }

    public void setRendervisibleRegn(boolean rendervisibleRegn) {
        this.rendervisibleRegn = rendervisibleRegn;
    }

    public boolean isRendervisibleChassis() {
        return rendervisibleChassis;
    }

    public void setRendervisibleChassis(boolean rendervisibleChassis) {
        this.rendervisibleChassis = rendervisibleChassis;
    }

    public void setRenderRegpanel(boolean renderRegpanel) {
        this.renderRegpanel = renderRegpanel;
    }

    public String getForchassisNo() {
        return forchassisNo;
    }

    public String getButtonId() {
        return buttonId;
    }

    public void setButtonId(String buttonId) {
        this.buttonId = buttonId;
    }

    public void setForchassisNo(String forchassisNo) {
        this.forchassisNo = forchassisNo;
    }

    public boolean isRenderchassisNo() {
        return renderchassisNo;
    }

    public void setRenderchassisNo(boolean renderchassisNo) {
        this.renderchassisNo = renderchassisNo;
    }

    public boolean isRenderpanel() {
        return renderpanel;
    }

    public boolean isRendarea() {
        return rendarea;
    }

    public void setRendarea(boolean rendarea) {
        this.rendarea = rendarea;
    }

    public void setRenderpanel(boolean renderpanel) {
        this.renderpanel = renderpanel;
    }

    public boolean isRenderunblock() {
        return renderunblock;
    }

    public void setRenderunblock(boolean renderunblock) {
        this.renderunblock = renderunblock;
    }

    public ArrayList getListTheftDetails() {
        return listTheftDetails;
    }

    public Map getMapBlacklistVehicles() {
        return mapBlacklistVehicles;
    }

    public void setMapBlacklistVehicles(Map mapBlacklistVehicles) {
        this.mapBlacklistVehicles = mapBlacklistVehicles;
    }

    public boolean isFlagfirdetails() {
        return flagfirdetails;
    }

    public void setFlagfirdetails(boolean flagfirdetails) {
        this.flagfirdetails = flagfirdetails;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getActiontaken() {
        return actiontaken;
    }

    public void setActiontaken(String actiontaken) {
        this.actiontaken = actiontaken;
    }

    public boolean isRenderRegpanel() {
        return renderRegpanel;
    }

    public Date getComplain_dt() {
        return complain_dt;
    }

    public void setComplain_dt(Date complain_dt) {
        this.complain_dt = complain_dt;
    }

    public int getComplain_type() {
        return complain_type;
    }

    public void setComplain_type(int complain_type) {
        this.complain_type = complain_type;
    }

    public int getOff_cd() {
        return off_cd;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    public void setblacklist(List<BlackListedVehicleDobj> dobjlist) {
    }

    /**
     * @return the regin_no
     */
    public String getRegin_no() {
        return regin_no;
    }

    /**
     * @param regin_no the regin_no to set
     */
    public void setRegin_no(String regin_no) {
        this.regin_no = regin_no;
    }

    /**
     * @return the bt_cancel
     */
    public boolean isBt_cancel() {
        return bt_cancel;
    }

    /**
     * @param bt_cancel the bt_cancel to set
     */
    public void setBt_cancel(boolean bt_cancel) {
        this.bt_cancel = bt_cancel;
    }

    /**
     * @return the deletecomplaindt
     */
    public String getDeletecomplaindt() {
        return deletecomplaindt;
    }

    /**
     * @param deletecomplaindt the deletecomplaindt to set
     */
    public void setDeletecomplaindt(String deletecomplaindt) {
        this.deletecomplaindt = deletecomplaindt;
    }

    /**
     * @return the offname
     */
    public String getOffname() {
        return offname;
    }

    /**
     * @param offname the offname to set
     */
    public void setOffname(String offname) {
        this.offname = offname;
    }

    /**
     * @return the offcd
     */
    public String getOffcd() {
        return offcd;
    }

    /**
     * @param offcd the offcd to set
     */
    public void setOffcd(String offcd) {
        this.offcd = offcd;
    }

    /**
     * @return the statecd
     */
    public String getStatecd() {
        return statecd;
    }

    /**
     * @param statecd the statecd to set
     */
    public void setStatecd(String statecd) {
        this.statecd = statecd;
    }

    /**
     * @return the usercd
     */
    public String getUsercd() {
        return usercd;
    }

    /**
     * @param usercd the usercd to set
     */
    public void setUsercd(String usercd) {
        this.usercd = usercd;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the statename
     */
    public String getStatename() {
        return statename;
    }

    /**
     * @param statename the statename to set
     */
    public void setStatename(String statename) {
        this.statename = statename;
    }

    /**
     * @return the fromFirDate
     */
    public Calendar getFromFirDate() {
        return fromFirDate;
    }

    /**
     * @param fromFirDate the fromFirDate to set
     */
    public void setFromFirDate(Calendar fromFirDate) {
        this.fromFirDate = fromFirDate;
    }

    /**
     * @return the maxRegndt
     */
    public List getMaxRegndt() {
        return maxRegndt;
    }

    /**
     * @param maxRegndt the maxRegndt to set
     */
    public void setMaxRegndt(List maxRegndt) {
        this.maxRegndt = maxRegndt;
    }

    /**
     * @return the maxFirDate
     */
    /**
     * @return the maxfirDate
     */
    /**
     * @return the maxfir
     */
    public Date getMaxfir() {
        return maxfir;
    }

    /**
     * @param maxfir the maxfir to set
     */
    public void setMaxfir(Date maxfir) {
        this.maxfir = maxfir;
    }

    /**
     * @return the rendercompundingdetails
     */
    public boolean isRendercompundingdetails() {
        return rendercompundingdetails;
    }

    /**
     * @param rendercompundingdetails the rendercompundingdetails to set
     */
    public void setRendercompundingdetails(boolean rendercompundingdetails) {
        this.rendercompundingdetails = rendercompundingdetails;
    }

    /**
     * @return the compoundingamount
     */
    public Long getCompoundingamount() {
        return compoundingamount;
    }

    /**
     * @param compoundingamount the compoundingamount to set
     */
    public void setCompoundingamount(Long compoundingamount) {
        this.compoundingamount = compoundingamount;
    }

    /**
     * @return the untracedRadiobtn
     */
    public String getUntracedRadiobtn() {
        return untracedRadiobtn;
    }

    /**
     * @param untracedRadiobtn the untracedRadiobtn to set
     */
    public void setUntracedRadiobtn(String untracedRadiobtn) {
        this.untracedRadiobtn = untracedRadiobtn;
    }

    /**
     * @return the renderUntracedEntry
     */
    public boolean isRenderUntracedEntry() {
        return renderUntracedEntry;
    }

    /**
     * @param renderUntracedEntry the renderUntracedEntry to set
     */
    public void setRenderUntracedEntry(boolean renderUntracedEntry) {
        this.renderUntracedEntry = renderUntracedEntry;
    }

    /**
     * @return the renderUntracedReport
     */
    public boolean isRenderUntracedReport() {
        return renderUntracedReport;
    }

    /**
     * @param renderUntracedReport the renderUntracedReport to set
     */
    public void setRenderUntracedReport(boolean renderUntracedReport) {
        this.renderUntracedReport = renderUntracedReport;
    }

    /**
     * @return the vh_class
     */
    public int getVh_class() {
        return vh_class;
    }

    /**
     * @param vh_class the vh_class to set
     */
    public void setVh_class(int vh_class) {
        this.vh_class = vh_class;
    }

    /**
     * @return the list_vh_class
     */
    public List getList_vh_class() {
        return list_vh_class;
    }

    /**
     * @param list_vh_class the list_vh_class to set
     */
    public void setList_vh_class(List list_vh_class) {
        this.list_vh_class = list_vh_class;
    }

    /**
     * @return the underSection
     */
    public String getUnderSection() {
        return underSection;
    }

    /**
     * @param underSection the underSection to set
     */
    public void setUnderSection(String underSection) {
        this.underSection = underSection;
    }

    /**
     * @return the district
     */
    public int getDistrict() {
        return district;
    }

    /**
     * @param district the district to set
     */
    public void setDistrict(int district) {
        this.district = district;
    }

    /**
     * @return the list_district
     */
    public List getList_district() {
        return list_district;
    }

    /**
     * @param list_district the list_district to set
     */
    public void setList_district(List list_district) {
        this.list_district = list_district;
    }

    /**
     * @return the colour
     */
    public String getColour() {
        return colour;
    }

    /**
     * @param colour the colour to set
     */
    public void setColour(String colour) {
        this.colour = colour;
    }

    /**
     * @return the order_dt
     */
    public Date getOrder_dt() {
        return order_dt;
    }

    /**
     * @param order_dt the order_dt to set
     */
    public void setOrder_dt(Date order_dt) {
        this.order_dt = order_dt;
    }

    /**
     * @return the untracedVehicleList
     */
    public List<BlackListedVehicleDobj> getUntracedVehicleList() {
        return untracedVehicleList;
    }

    /**
     * @param untracedVehicleList the untracedVehicleList to set
     */
    public void setUntracedVehicleList(List<BlackListedVehicleDobj> untracedVehicleList) {
        this.untracedVehicleList = untracedVehicleList;
    }

    /**
     * @return the renderUntracedPanel
     */
    public boolean isRenderUntracedPanel() {
        return renderUntracedPanel;
    }

    /**
     * @param renderUntracedPanel the renderUntracedPanel to set
     */
    public void setRenderUntracedPanel(boolean renderUntracedPanel) {
        this.renderUntracedPanel = renderUntracedPanel;
    }

    /**
     * @return the court_order_no
     */
    public String getCourt_order_no() {
        return court_order_no;
    }

    /**
     * @param court_order_no the court_order_no to set
     */
    public void setCourt_order_no(String court_order_no) {
        this.court_order_no = court_order_no;
    }

    /**
     * @return the fromOrder_dt
     */
    public Calendar getFromOrder_dt() {
        return fromOrder_dt;
    }

    /**
     * @param fromOrder_dt the fromOrder_dt to set
     */
    public void setFromOrder_dt(Calendar fromOrder_dt) {
        this.fromOrder_dt = fromOrder_dt;
    }

    /**
     * @return the untracedFirNo
     */
    public String getUntracedFirNo() {
        return untracedFirNo;
    }

    /**
     * @param untracedFirNo the untracedFirNo to set
     */
    public void setUntracedFirNo(String untracedFirNo) {
        this.untracedFirNo = untracedFirNo;
    }

    /**
     * @return the untracedPoliceStation
     */
    public String getUntracedPoliceStation() {
        return untracedPoliceStation;
    }

    /**
     * @param untracedPoliceStation the untracedPoliceStation to set
     */
    public void setUntracedPoliceStation(String untracedPoliceStation) {
        this.untracedPoliceStation = untracedPoliceStation;
    }

    /**
     * @return the untracedChasiNo
     */
    public String getUntracedChasiNo() {
        return untracedChasiNo;
    }

    /**
     * @param untracedChasiNo the untracedChasiNo to set
     */
    public void setUntracedChasiNo(String untracedChasiNo) {
        this.untracedChasiNo = untracedChasiNo;
    }

    /**
     * @return the render_regn_chasi_no_untraced_radio_btn
     */
    public boolean isRender_regn_chasi_no_untraced_radio_btn() {
        return render_regn_chasi_no_untraced_radio_btn;
    }

    /**
     * @param render_regn_chasi_no_untraced_radio_btn the
     * render_regn_chasi_no_untraced_radio_btn to set
     */
    public void setRender_regn_chasi_no_untraced_radio_btn(boolean render_regn_chasi_no_untraced_radio_btn) {
        this.render_regn_chasi_no_untraced_radio_btn = render_regn_chasi_no_untraced_radio_btn;
    }

    /**
     * @return the untracedRegnChasiNoRadiobtn
     */
    public String getUntracedRegnChasiNoRadiobtn() {
        return untracedRegnChasiNoRadiobtn;
    }

    /**
     * @param untracedRegnChasiNoRadiobtn the untracedRegnChasiNoRadiobtn to set
     */
    public void setUntracedRegnChasiNoRadiobtn(String untracedRegnChasiNoRadiobtn) {
        this.untracedRegnChasiNoRadiobtn = untracedRegnChasiNoRadiobtn;
    }

    /**
     * @return the disableUntracedRegnno
     */
    public boolean isDisableUntracedRegnno() {
        return disableUntracedRegnno;
    }

    /**
     * @param disableUntracedRegnno the disableUntracedRegnno to set
     */
    public void setDisableUntracedRegnno(boolean disableUntracedRegnno) {
        this.disableUntracedRegnno = disableUntracedRegnno;
    }

    /**
     * @return the viewReportbyLabel
     */
    public String getViewReportbyLabel() {
        return viewReportbyLabel;
    }

    /**
     * @param viewReportbyLabel the viewReportbyLabel to set
     */
    public void setViewReportbyLabel(String viewReportbyLabel) {
        this.viewReportbyLabel = viewReportbyLabel;
    }
}
