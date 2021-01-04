/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.eChallan;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.impl.eChallan.MastarChallanImpl;
import org.apache.log4j.Logger;
import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.PrimeFaces;
import nic.vahan.form.dobj.eChallan.VmChlnBookDobj;
import nic.vahan.form.dobj.eChallan.VmCourtDobj;
import nic.vahan.form.dobj.eChallan.VmDaDobj;
import nic.vahan.form.dobj.eChallan.VmDaPenaltyDobj;
import nic.vahan.form.dobj.eChallan.VmExScheduleDobj;
import nic.vahan.form.dobj.eChallan.VmOffenceDobj;
import nic.vahan.form.dobj.eChallan.VmOffencePenaltyDobj;
import nic.vahan.form.dobj.eChallan.VmSectionsDobj;
import nic.vahan.form.dobj.eChallan.VmOvlScheduleDobj;
import nic.vahan.form.dobj.eChallan.VmAccussedDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author tranC111
 */
@ManagedBean(name = "challanbean")
@ViewScoped
public class MasterChallanBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(MasterChallanBean.class);
    MastarChallanImpl impl = new MastarChallanImpl();
    VmAccussedDobj acc_dobj = new VmAccussedDobj();
    VmAccussedDobj Previous_acc_dobj;
    VmChlnBookDobj chlnbook_dobj = new VmChlnBookDobj();
    VmChlnBookDobj previous_chlnbook_dobj;
    VmCourtDobj court_dobj = new VmCourtDobj();
    VmCourtDobj previous_court_dobj;
    VmDaDobj da_dobj = new VmDaDobj();
    VmDaDobj previous_da_dobj;
    VmDaPenaltyDobj dapenalty_dobj = new VmDaPenaltyDobj();
    VmDaPenaltyDobj previous_dapenalty_dobj;
    VmExScheduleDobj exsch_dobj = new VmExScheduleDobj();
    VmExScheduleDobj previous_exsch_dobj;
    VmOffenceDobj off_dobj = new VmOffenceDobj();
    VmOffenceDobj previous_off_dobj;
    VmOffencePenaltyDobj offpenalty_dobj = new VmOffencePenaltyDobj();
    VmOffencePenaltyDobj previous_offpenalty_dobj;
    VmOvlScheduleDobj ovl_sch_dobj = new VmOvlScheduleDobj();
    VmOvlScheduleDobj previous_ovl_sch_dobj = new VmOvlScheduleDobj();
    private String widgetVar = "select";
    private String statecode = "";
    private String vechile_class = "";
    private String offencecode = "";
    private String sectioncode = "";
    private List yesnolist = new ArrayList();
    private String yesno = "";
    private InputText VM_ACCUSEDCODE = new InputText();
    private InputText VM_ACCUSEDDESCR = new InputText();
    private Integer VM_CHALLANBOOKEMPCODE;
    private String VM_CHALLANBOOKNO = "";
    private Integer VM_CHALLANBOOK_NOFROM;
    private Integer VM_CHALLANBOOK_NOUPTO;
    private Integer VM_CHALLANBOOK_NOCURRENT;
    private String VM_CHALLANBOOK_EXPIRED = "";
    private Calendar VM_CHALLANBOOK_ISSDATE = new Calendar();
    private Date VM_CHALLANBOOK_ISSDATE1;
    private String VM_CHALLANBOOK_ISSBY = "";
    private Integer VM_CHALLANBOOK_OFFICECODE;
    private Integer VM_COURT_CODE;
    private String VM_COURT_NAME = "";
    private Integer VM_DA_CODE;
    private String VM_DA_DESCR = "";
    private String VM_DA_OFFCODE = "";
    private String VM_DA_OFFNCE_CD = "";
    private String VM_DA_SCHEDULE = "";
    private String VM_DA_SUB_SCHEDULE = "";
    private int VM_DA_PENALTY_SRNO = 0;
    private String VM_DA_PENALTY_DACODE = "";
    private String VM_DA_PENALTY_VHCLASS = "";
    private Integer VM_DA_PENALTY_OPENALTY;
    private Integer VM_DA_PENALTY_DPENALTY;
    private Integer VM_DA_PENALTY_CPENALTY;
    private Integer VM_DA_PENALTY_SUSDAYS;
    private Integer VM_DA_PENALTY_OPENALTY1;
    private Integer VM_DA_PENALTY_DPENALTY1;
    private Integer VM_DA_PENALTY_CPENALTY1;
    private Integer VM_DA_PENALTY_SUSDAYS1;
    private Integer VM_DA_PENALTY_OPENALTY2;
    private Integer VM_DA_PENALTY_DPENALTY2;
    private Integer VM_DA_PENALTY_CPENALTY2;
    private Integer VM_DA_PENALTY_SUSDAYS2;
    private String VM_DA_PENALTY_SUSACTION = "";
    private int VM_EX_SCHEDULE_SRNO = 0;
    private String VM_EX_SCHEDULE_TYPE1 = "";
    private String VM_EX_SCHEDULE_VHCLASS = "";
    private Integer VM_EX_SCHEDULE_FLATRATE;
    private Integer VM_EX_SCHEDULE_UNITRATE;
    private Integer VM_OFFENCE_CODE;
    private String VM_OFFENCE_DESC = "";
    private String VM_OFFENCE_MVACLAUSE = "";
    private String VM_OFFENCE_CMVRCLAUSE = "";
    private String VM_OFFENCE_SMVRCLAUSE = "";
    private String VM_OFFENCE_ADDLDESC = "";
    private String VM_OFFENCE_PUNISHMENT1 = "";
    private String VM_OFFENCE_PUNISHMENT2 = "";
    private String VM_OFFENCE_ISACT_TPT = "";
    private String VM_OFFENCE_ISACT_POLICE = "";
    private int VM_OFF_PENALTY_SRNO = 0;
    private String VM_OFF_PENALTY_CODE = "";
    private String VM_OFF_PENALTY_VHCLASS = "";
    private List SELECT_VM_OFF_PENALTY_VHCLASS = new ArrayList();
    private List SELECTED_ACCUESD = new ArrayList();
    private String VM_OFF_PENALTY_SECTIONCODE = "";
    private Integer VM_OFF_PENALTY_OPENALTY;
    private Integer VM_OFF_PENALTY_DPENALTY;
    private Integer VM_OFF_PENALTY_CPENALTY;
    private Integer VM_OFF_PENALTY_OPENALTY1;
    private Integer VM_OFF_PENALTY_DPENALTY1;
    private Integer VM_OFF_PENALTY_CPENALTY1;
    private Integer VM_OFF_PENALTY_OPENALTY2;
    private Integer VM_OFF_PENALTY_DPENALTY2;
    private Integer VM_OFF_PENALTY_CPENALTY2;
    private Integer VM_SECTION_CODE;
    private String VM_SECTION_LEVELFLAG = "";
    private String VM_SECTION_NAME = "";
    private String VM_SECTION_ISACTIVE = "";
    private int VM_OVLD_SCHEDULE_SRNO = 0;
    private Integer VM_OVLD_SCHEDULE_LDWTLOWER;
    private Integer VM_OVLD_SCHEDULE_LDWTUPPER;
    private Integer VM_OVLD_SCHEDULE_LDWTUNIT;
    private Integer VM_OVLD_SCHEDULE_FLATCFAMT;
    private Integer VM_OVLD_SCHEDULE_UNITCFAMT;
    private Integer VM_OVLD_SCHEDULE_WTUNITVAL;
    private String errLabelMsg = "";
    private static List VechileclassList = new ArrayList();
    private static List offenceList = new ArrayList();
    private String createUpdateTitle = "";
    private String offence_cd = "";
    private List<VmOffencePenaltyDobj> newoffencelist = new ArrayList<>();
    private List<VmCourtDobj> courtlist = new ArrayList<>();
    private List<VmChlnBookDobj> challanlist = new ArrayList<VmChlnBookDobj>();
    private List<VmDaDobj> Dalist = new ArrayList<VmDaDobj>();
    private List<VmDaPenaltyDobj> DaPenaltylist = new ArrayList<VmDaPenaltyDobj>();
    private List<VmExScheduleDobj> Exslist = new ArrayList<VmExScheduleDobj>();
    private List<VmOffenceDobj> Offlist = new ArrayList<VmOffenceDobj>();
    private List<VmOffencePenaltyDobj> OffPnllist = new ArrayList<VmOffencePenaltyDobj>();
    private List<VmSectionsDobj> Sectnlist = new ArrayList<VmSectionsDobj>();
    private List<VmOvlScheduleDobj> ovllist = new ArrayList<VmOvlScheduleDobj>();
    private static List combofilllist = new ArrayList();
    private String combovalue = "";
    private List vechilelist = new ArrayList();
    private List userCodeList = new ArrayList();
    private List officeList = new ArrayList();
    private List sectionList = new ArrayList();
    private Map<String, Object> accuesdList;
    private boolean challanpanel;
    private boolean courtpanel;
    private boolean dapanel;
    private boolean dapenaltypanel;
    private boolean Exspanel;
    private boolean Offencepanel;
    private boolean OffPnlpanel;
    private boolean Sectnpanel;
    private boolean ovlpanel;
    private String section_name;

    public MasterChallanBean() {
    }

    @PostConstruct
    public void Init() {
        try {
            fillmastercombo();
            disabledatatablepanels();
            String user_catg = Util.getUserCategory();
            boolean checkUserPerm = false;
            if (user_catg.equals("S") || (user_catg.equals("A"))) {
                checkUserPerm = true;
            }
            if (checkUserPerm == false) {
                combofilllist.clear();
                JSFUtils.showMessage("YOU ARE NOT ALLOWED TO ENTER DATA IN MASTER FORM");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    /**
     * Method to generate the show table data
     */
    public void disabledatatablepanels() {
        setChallanpanel(false);
        setCourtpanel(false);
        setDapanel(false);
        setDapenaltypanel(false);
        setExspanel(false);
        setOffencepanel(false);
        setOffPnlpanel(false);
        setSectnpanel(false);
        setOvlpanel(false);
    }

    public void getsectionOffenceWise() {
        setVM_OFF_PENALTY_SECTIONCODE(getVM_OFF_PENALTY_CODE());
    }

    public void fillmastercombo() {
        combofilllist.clear();
        List l = new ArrayList();
        l.add("SELECT");
        l.add("CHALLAN BOOK MASTER");
        l.add("COURT MASTER");
        l.add("DA MASTER");
        l.add("DA PENALTY MASTER");
        l.add("EXCESS SCHEDULE MASTER");
        l.add("OFFENCE MASTER");
        l.add("OFFENCE PENALTY MASTER");

        l.add("OVERLOAD SCHEDULE MASTER");
        combofilllist.addAll(l);
    }

    public void checkcombovalues() {
        try {
            offenceList.clear();
            vechilelist.clear();
            sectionList.clear();
            setAccuesdList(null);
            courtlist.clear();
            userCodeList.clear();
            challanlist.clear();
            officeList.clear();
            setVechilelist(impl.getVHclassList());
            setOffenceList(impl.getOffencecomboList());
            setSectionList(impl.getSectioncomboList());
            setUserCodeList(impl.getUserCodeList());
            setOfficeList(impl.getofficeList());
            setAccuesdList(impl.getAccusedList());
            if (combovalue.equals("COURT MASTER")) {
                setCourtpanel(true);
                setOffencepanel(false);
                setChallanpanel(false);
                setDapanel(false);
                setDapenaltypanel(false);
                setOffPnlpanel(false);
                setExspanel(false);
                setSectnpanel(false);
                setOvlpanel(false);
                setCourtlist(impl.getCourtList());
            }

            if (combovalue.equals("CHALLAN BOOK MASTER")) {
                setChallanpanel(true);
                setCourtpanel(false);
                setDapanel(false);
                setDapenaltypanel(false);
                setOffencepanel(false);
                setExspanel(false);
                setOffPnlpanel(false);
                setSectnpanel(false);
                setOvlpanel(false);
                setChallanlist(impl.getChallanBookList());
            }

            if (combovalue.equals("DA MASTER")) {
                setCourtpanel(false);
                setChallanpanel(false);
                setDapanel(true);
                setDapenaltypanel(false);
                setExspanel(false);
                setOffencepanel(false);
                setOffPnlpanel(false);
                setSectnpanel(false);
                setOvlpanel(false);
                setDalist(impl.getDaList());
            }

            if (combovalue.equals("DA PENALTY MASTER")) {
                setCourtpanel(false);
                setChallanpanel(false);
                setDapanel(false);
                setDapenaltypanel(true);

                setExspanel(false);
                setOffencepanel(false);
                setOffPnlpanel(false);
                setSectnpanel(false);
                setOvlpanel(false);
                setDaPenaltylist(impl.getDaPenaltyList());
            }


            if (combovalue.equals("EXCESS SCHEDULE MASTER")) {
                setCourtpanel(false);
                setChallanpanel(false);
                setDapanel(false);
                setDapenaltypanel(false);
                setExspanel(true);
                setOffencepanel(false);
                setOffPnlpanel(false);
                setSectnpanel(false);
                setOvlpanel(false);
                setExslist(impl.getExSchList());
            }

            if (combovalue.equals("OFFENCE MASTER")) {
                setCourtpanel(false);
                setChallanpanel(false);
                setDapanel(false);
                setDapenaltypanel(false);
                setExspanel(false);
                setOffencepanel(true);
                setOffPnlpanel(false);
                setSectnpanel(false);
                setOvlpanel(false);
                setOfflist(impl.getOffenceList());
            }

            if (combovalue.equals("OFFENCE PENALTY MASTER")) {
                setCourtpanel(false);
                setChallanpanel(false);
                setDapanel(false);
                setDapenaltypanel(false);
                setExspanel(false);
                setOffencepanel(false);
                setOffPnlpanel(true);
                setSectnpanel(false);
                setOvlpanel(false);
                setOffPnllist(impl.getOffencePenaltyList());
            }

            if (combovalue.equals("OVERLOAD SCHEDULE MASTER")) {
                setCourtpanel(false);
                setChallanpanel(false);
                setDapanel(false);
                setDapenaltypanel(false);
                setExspanel(false);
                setOffencepanel(false);
                setOffPnlpanel(false);
                setSectnpanel(false);
                setOvlpanel(true);
                setOvllist(impl.getOverLoadList());
            }
            if (combovalue.equals("SELECT")) {
                setCourtpanel(false);
                setChallanpanel(false);
                setDapanel(false);
                setDapenaltypanel(false);
                setExspanel(false);
                setOffencepanel(false);
                setOffPnlpanel(false);
                setSectnpanel(false);
                setOvlpanel(false);

            }
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    /**
     * *
     * Method to pop up the dialog box
     *
     * @param str
     */
    public void hideandshowpanel(String str) {
        boolean flag = false;
        reset();
        createUpdateTitle = "";
        if (combovalue.equals("CHALLAN BOOK MASTER")) {
            str = "echallan.vm_challan_book";
        } else if (combovalue.equals("COURT MASTER")) {
            str = "echallan.vm_courts";
        } else if (combovalue.equals("DA MASTER")) {
            str = "echallan.vm_da";
        } else if (combovalue.equals("DA PENALTY MASTER")) {
            str = "echallan.vm_da_penalty";
        } else if (combovalue.equals("EXCESS SCHEDULE MASTER")) {
            str = "echallan.vm_excess_schedule";
        } else if (combovalue.equals("OFFENCE MASTER")) {
            str = "echallan.vm_offences";
        } else if (combovalue.equals("OFFENCE PENALTY MASTER")) {
            str = "echallan.vm_offence_penalty";
        } else if (combovalue.equals("OVERLOAD SCHEDULE MASTER")) {
            str = "echallan.vm_overload_schedule";
        } else if (combovalue.equals("SELECT")) {
            str = "select";
        }
        if (!combovalue.equalsIgnoreCase("SELECT")) {
            reset();
            PrimeFaces.current().executeScript("PF('" + str + "').show()");
            PrimeFaces.current().ajax().update("dialog_Panel");
            if (str.trim().equalsIgnoreCase("echallan.vm_challan_book")) {
                reset();
                createUpdateTitle = "Add New Record In Challan Book Master";
            } else if (str.trim().equalsIgnoreCase("echallan.vm_courts")) {
                reset();
                createUpdateTitle = "Add New Record In Court Master";
            } else if (str.trim().equalsIgnoreCase("echallan.vm_da")) {
                reset();
                createUpdateTitle = "Add New Record In DA Master";
            } else if (str.trim().equalsIgnoreCase("echallan.vm_da_penalty")) {
                reset();
                createUpdateTitle = "Add New Record In DA Penalty Master";
            } else if (str.trim().equalsIgnoreCase("echallan.vm_excess_schedule")) {
                reset();
                createUpdateTitle = "Add New Record In Excess Schedule Master";
            } else if (str.trim().equalsIgnoreCase("echallan.vm_offences")) {
                reset();
                createUpdateTitle = "Add New Record In Offence Master";
            } else if (str.trim().equalsIgnoreCase("echallan.vm_offence_penalty")) {
                reset();
                createUpdateTitle = "Add New Record In Offence Penalty Master";
            } else if (str.trim().equalsIgnoreCase("echallan.vm_overload_schedule")) {
                reset();
                createUpdateTitle = "Add New Record In Overload Schedule Master";
            }
        } else {
            reset();
            JSFUtils.showMessage("Please Select Value From List");
        }
    }

    /**
     * Add new record in master table
     *
     * @param event
     * @return
     */
    public void addNewRecordTable() {

        errLabelMsg = "";
//checks for user
        if (combovalue.equals("CHALLAN BOOK MASTER")) {

            String expired = "";
            String iss_date = null;
            try {
                expired = getVM_CHALLANBOOK_EXPIRED();
                if (getVM_CHALLANBOOKEMPCODE() == -1) {
                    JSFUtils.showMessage("Please Select The Emp Code");
                    return;
                } else {
                    chlnbook_dobj.setUsercode(getVM_CHALLANBOOKEMPCODE());


                }
                if (getVM_CHALLANBOOKNO() == null || getVM_CHALLANBOOKNO().equals("")) {
                    JSFUtils.showMessage("Please Enter The Book No");
                    return;
                } else {
                    chlnbook_dobj.setBook_no(getVM_CHALLANBOOKNO().toUpperCase());

                }
                if (getVM_CHALLANBOOK_NOFROM() == 0) {
                    JSFUtils.showMessage(" Challan No From must be greater then Zero ");
                    return;
                } else {
                    chlnbook_dobj.setChln_frm(getVM_CHALLANBOOK_NOFROM());

                }
                if (getVM_CHALLANBOOK_NOUPTO() == 0) {
                    JSFUtils.showMessage("Challan No Upto must be greater then Zero ");
                    return;
                } else {
                    chlnbook_dobj.setChln_upto(getVM_CHALLANBOOK_NOUPTO());

                }
                if (getVM_CHALLANBOOK_NOUPTO() > getVM_CHALLANBOOK_NOFROM()) {
                } else {
                    JSFUtils.showMessage("Challan No Upto must be greater than Challan No From");
                    return;
                }
                if (getVM_CHALLANBOOK_NOCURRENT() == 0) {
                    JSFUtils.showMessage("Current Challan No must be greater then Zero ");
                    return;
                } else {
                    chlnbook_dobj.setCurr_chln_no(getVM_CHALLANBOOK_NOCURRENT());

                }
                if (expired.equals("-1") || expired.equals("Select")) {
                    JSFUtils.showMessage("Please Select Expired or not ");
                    return;
                } else {
                    chlnbook_dobj.setExpired(expired);

                }
                DateFormat df = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy");
                Date iss_dt = getVM_CHALLANBOOK_ISSDATE1();
                if (iss_dt != null) {
                    iss_date = df.format(iss_dt);
                    chlnbook_dobj.setIss_dt(iss_date);
                } else {
                    JSFUtils.showMessage("Please Enter Issue Date ");
                    return;
                }
                if (getVM_CHALLANBOOK_ISSBY() == null || getVM_CHALLANBOOK_ISSBY().equals("")) {
                    JSFUtils.showMessage("Please Enter Issue By ");
                    return;
                } else {
                    chlnbook_dobj.setIss_by(getVM_CHALLANBOOK_ISSBY().toUpperCase());

                }
                if (getVM_CHALLANBOOK_OFFICECODE() == -1) {
                    JSFUtils.showMessage("Please Select Office Code ");
                    return;
                } else {
                    chlnbook_dobj.setOff_cd(getVM_CHALLANBOOK_OFFICECODE());

                }

                previous_chlnbook_dobj = chlnbook_dobj;


                boolean flag = impl.addChallanBookRecord(chlnbook_dobj);
                if (flag) {
                    JSFUtils.showMessage("Data saved successfully ");
                    setChallanlist(impl.getChallanBookList());
                    setWidgetVar("select");
                    reset();
                } else {
                    JSFUtils.showMessage("Data not saved ");
                }
                reset();

            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        } else if (combovalue.equals("COURT MASTER")) {

            try {


                if (getVM_COURT_CODE() == 0) {
                    JSFUtils.showMessage("Please Enter The Court Code");
                    return;
                } else {
                    court_dobj.setCourtcode(getVM_COURT_CODE());


                }
                if (getVM_COURT_NAME() == null || getVM_COURT_NAME().equals("")) {
                    JSFUtils.showMessage("Please Enter The Court Name");
                    return;
                } else {
                    court_dobj.setCourtname(getVM_COURT_NAME().toUpperCase());

                }

                boolean checkflag = impl.checkCourtCodeExist(court_dobj);
                if (checkflag) {
                    JSFUtils.showMessage("Data already Exist for this CODE");
                } else {
                    boolean flag = impl.addCourtRecord(court_dobj);
                    if (flag) {
                        JSFUtils.showMessage("Data saved successfully ");
                        setWidgetVar("select");
                        setCourtlist(impl.getCourtList());
                        reset();
                    } else {
                        JSFUtils.showMessage("Data not saved ");
                    }
                    reset();
                }

            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        } else if (combovalue.equals("DA MASTER")) {

            try {

                if (getVM_DA_CODE() == 0) {
                    JSFUtils.showMessage("Please Enter The Da Code");
                    return;
                } else {
                    da_dobj.setCode(getVM_DA_CODE());


                }
                if (getVM_DA_DESCR() == null || getVM_DA_DESCR().equals("")) {
                    JSFUtils.showMessage("Please Enter The Da Description");
                    return;
                } else {
                    da_dobj.setDescription(getVM_DA_DESCR().toUpperCase());

                }
                if (getVM_DA_OFFCODE().equals("Select") || getVM_DA_OFFCODE().equals("-1")) {
                    JSFUtils.showMessage("Please Select The Da Offence Code");
                    return;
                } else {
                    da_dobj.setOffence_code((getVM_DA_OFFCODE()));

                }
                if (getVM_DA_SCHEDULE() == null || getVM_DA_SCHEDULE().equals("")) {
                    JSFUtils.showMessage("Please Enter The Da Schedule");
                    return;
                } else {
                    da_dobj.setDa_schedule(getVM_DA_SCHEDULE());

                }
                if (getVM_DA_SUB_SCHEDULE() == null || getVM_DA_SUB_SCHEDULE().equals("")) {
                    JSFUtils.showMessage("Please Enter The Da Sub Schedule");
                    return;
                } else {
                    da_dobj.setDa_sub_schedule(getVM_DA_SUB_SCHEDULE());

                }

                previous_da_dobj = da_dobj;
                boolean checkflag = impl.checkDaCodeExist(da_dobj);
                if (checkflag) {
                    JSFUtils.showMessage("Data already Exist for this CODE");
                } else {
                    boolean flag = impl.addDARecord(da_dobj);
                    if (flag) {
                        JSFUtils.showMessage("Data saved successfully ");
                        setDalist(impl.getDaList());
                        setWidgetVar("select");
                        reset();
                    } else {
                        JSFUtils.showMessage("Data not saved ");
                    }
                    reset();
                }

            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        } else if (combovalue.equals("DA PENALTY MASTER")) {


            try {

                if (getVM_DA_PENALTY_SRNO() == 0) {
                    JSFUtils.showMessage("Please Enter The Sr.No");
                    return;
                } else {
                    dapenalty_dobj.setSrno(getVM_DA_PENALTY_SRNO());

                }
                if (getVM_DA_PENALTY_DACODE() == null || getVM_DA_PENALTY_DACODE().equals("")) {
                    JSFUtils.showMessage("Please Enter The Da Code");
                    return;
                } else {
                    dapenalty_dobj.setDacode(Integer.parseInt(getVM_DA_PENALTY_DACODE()));

                }
                if (getVM_DA_PENALTY_VHCLASS().equals("Select") || getVM_DA_PENALTY_VHCLASS().equals("-1")) {
                    JSFUtils.showMessage("Please Select The Vehicle Class");
                    return;
                } else {
                    dapenalty_dobj.setVhclass(getVM_DA_PENALTY_VHCLASS());

                }
                if (getVM_DA_PENALTY_OPENALTY() == 0) {
                    JSFUtils.showMessage("Please Enter The Owner Penalty");
                    return;
                } else {
                    dapenalty_dobj.setOpenalty(getVM_DA_PENALTY_OPENALTY());

                }
                if (getVM_DA_PENALTY_DPENALTY() == 0) {
                    JSFUtils.showMessage("Please Enter The Driver Penalty");

                } else {
                    dapenalty_dobj.setDpenalty(getVM_DA_PENALTY_DPENALTY());

                }
                if (getVM_DA_PENALTY_CPENALTY() == 0) {
                    JSFUtils.showMessage("Please Enter The Conductor Penalty");
                    return;
                } else {
                    dapenalty_dobj.setCpenalty(getVM_DA_PENALTY_CPENALTY());

                }
                if (getVM_DA_PENALTY_SUSDAYS() == 0) {
                    JSFUtils.showMessage("Please Enter The Sus Days");
                    return;
                } else {
                    dapenalty_dobj.setSusdays(getVM_DA_PENALTY_SUSDAYS());

                }
                if (getVM_DA_PENALTY_OPENALTY1() == 0) {
                    JSFUtils.showMessage("Please Enter The Owner Penalty1");
                    return;
                } else {
                    dapenalty_dobj.setOpenalty1(getVM_DA_PENALTY_OPENALTY1());

                }
                if (getVM_DA_PENALTY_DPENALTY1() == 0) {
                    JSFUtils.showMessage("Please Enter The Driver Penalty1");
                    return;
                } else {
                    dapenalty_dobj.setDpenalty1(getVM_DA_PENALTY_DPENALTY1());

                }
                if (getVM_DA_PENALTY_CPENALTY1() == 0) {
                    JSFUtils.showMessage("Please Enter The Conductor Penalty1");
                    return;
                } else {
                    dapenalty_dobj.setCpenalty1(getVM_DA_PENALTY_CPENALTY1());

                }
                if (getVM_DA_PENALTY_SUSDAYS1() == 0) {
                    JSFUtils.showMessage("Please Enter The Sus Days1");
                    return;
                } else {
                    dapenalty_dobj.setSusdays1(getVM_DA_PENALTY_SUSDAYS1());

                }
                if (getVM_DA_PENALTY_OPENALTY2() == 0) {
                    JSFUtils.showMessage("Please Enter The Owner Penalty2");
                    return;
                } else {
                    dapenalty_dobj.setOpenalty2(getVM_DA_PENALTY_OPENALTY2());

                }
                if (getVM_DA_PENALTY_DPENALTY2() == 0) {
                    JSFUtils.showMessage("Please Enter The Driver Penalty2");
                    return;
                } else {
                    dapenalty_dobj.setDpenalty2(getVM_DA_PENALTY_DPENALTY2());

                }
                if (getVM_DA_PENALTY_CPENALTY2() == 0) {
                    JSFUtils.showMessage("Please Enter The Conductor Penalty2");
                    return;
                } else {
                    dapenalty_dobj.setCpenalty2(getVM_DA_PENALTY_CPENALTY2());

                }
                if (getVM_DA_PENALTY_SUSDAYS2() == 0) {
                    JSFUtils.showMessage("Please Enter Sus Days2");
                    return;
                } else {
                    dapenalty_dobj.setSusdays2(getVM_DA_PENALTY_SUSDAYS2());

                }
                if (getVM_DA_PENALTY_SUSACTION() == null || getVM_DA_PENALTY_SUSACTION().equals("")) {
                    JSFUtils.showMessage("Please Enter The Sus Action");
                    return;
                } else {
                    dapenalty_dobj.setSusaction(getVM_DA_PENALTY_SUSACTION());

                }

                boolean flag = impl.addDAPenaltyRecord(dapenalty_dobj);
                if (flag) {
                    setDaPenaltylist(impl.getDaPenaltyList());
                    JSFUtils.showMessage("Data saved successfully ");
                    setWidgetVar("select");
                } else {
                    JSFUtils.showMessage("Data not saved ");
                }
                reset();

            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        } else if (combovalue.equals("EXCESS SCHEDULE MASTER")) {


            try {

                if (getVM_EX_SCHEDULE_SRNO() == 0) {
                    JSFUtils.showMessage("Please Enter The Excess Schedule Sr.No");
                    return;
                } else {
                    exsch_dobj.setSrno(getVM_EX_SCHEDULE_SRNO());

                }
                if (getVM_EX_SCHEDULE_TYPE1() == null || getVM_EX_SCHEDULE_TYPE1().equals("")) {
                    JSFUtils.showMessage("Please Enter The Excess Schedule Type");
                    return;
                } else {
                    exsch_dobj.setType(getVM_EX_SCHEDULE_TYPE1());

                }
                if (getVM_EX_SCHEDULE_VHCLASS().equals("-1") || getVM_EX_SCHEDULE_VHCLASS().equals("Select")) {
                    JSFUtils.showMessage("Please Select The Excess Schedule Vehicle class");
                    return;
                } else {
                    exsch_dobj.setVh_class(getVM_EX_SCHEDULE_VHCLASS());

                }
                if (getVM_EX_SCHEDULE_FLATRATE() == 0) {
                    JSFUtils.showMessage("Please Enter The Excess Schedule Flat Rate");
                    return;
                } else {
                    exsch_dobj.setFlatrate(getVM_EX_SCHEDULE_FLATRATE());

                }
                if (getVM_EX_SCHEDULE_UNITRATE() == 0) {
                    JSFUtils.showMessage("Please Enter The Excess Schedule Unit Rate");
                    return;
                } else {
                    exsch_dobj.setUnitrate(getVM_EX_SCHEDULE_UNITRATE());

                }

                boolean flag = impl.addExScheduleRecord(exsch_dobj);
                if (flag) {
                    setExslist(impl.getExSchList());
                    JSFUtils.showMessage("Data saved successfully ");
                    setWidgetVar("select");
                    reset();
                } else {
                    JSFUtils.showMessage("Data not saved ");
                }
                reset();

            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        } else if (combovalue.equals("OFFENCE MASTER")) {



            try {



                if (getVM_OFFENCE_CODE() == 0) {
                    JSFUtils.showMessage("Please Enter The Offence Code");
                    return;
                } else {
                    off_dobj.setOff_code(getVM_OFFENCE_CODE());


                }
                if (getVM_OFFENCE_DESC() == null || getVM_OFFENCE_DESC().equals("")) {
                    JSFUtils.showMessage("Please Enter The Offence Description");
                    return;
                } else {
                    off_dobj.setOffence_desc(getVM_OFFENCE_DESC().toUpperCase());

                }

                if (getVM_OFFENCE_SMVRCLAUSE() == null || getVM_OFFENCE_SMVRCLAUSE().equals("")) {
                    JSFUtils.showMessage("Please Enter The SMVR Clause");
                    return;
                } else {
                    off_dobj.setSmvr_clause(getVM_OFFENCE_SMVRCLAUSE());

                }
                if (getVM_OFFENCE_ADDLDESC() == null || getVM_OFFENCE_ADDLDESC().equals("")) {
                    JSFUtils.showMessage("Please Enter The Additional Description");
                    return;
                } else {
                    off_dobj.setAddl_desc(getVM_OFFENCE_ADDLDESC().toUpperCase());

                }
                if (getVM_OFFENCE_PUNISHMENT1() == null || getVM_OFFENCE_PUNISHMENT1().equals("")) {
                    JSFUtils.showMessage("Please Enter The Punishment 1");
                    return;
                } else {
                    off_dobj.setPunishment1(getVM_OFFENCE_PUNISHMENT1().toUpperCase());

                }
                if (getVM_OFFENCE_PUNISHMENT2() == null || getVM_OFFENCE_PUNISHMENT2().equals("")) {
                    JSFUtils.showMessage("Please Enter The Punishment 2");
                    return;
                } else {
                    off_dobj.setPunishment2(getVM_OFFENCE_PUNISHMENT2().toUpperCase());

                }
                if (getVM_OFFENCE_ISACT_TPT().equals("-1") || getVM_OFFENCE_ISACT_TPT().equals("Select")) {
                    JSFUtils.showMessage("Please Select Is active for TPT or not");
                    return;
                } else {
                    off_dobj.setIsactivefor_tpt(getVM_OFFENCE_ISACT_TPT().toUpperCase());

                }
                if (getVM_OFFENCE_ISACT_POLICE().equals("-1") || getVM_OFFENCE_ISACT_POLICE().equals("Select")) {
                    JSFUtils.showMessage("Please Select Is active for Police or not");
                    return;
                } else {
                    off_dobj.setIsactivefor_police(getVM_OFFENCE_ISACT_POLICE());

                }


                boolean checkflag = impl.checkOffenceCodeExist(off_dobj);
                if (checkflag) {
                    JSFUtils.showMessage("Data Already Exist for this CODE ");
                } else {
                    boolean flag = impl.addOffenceRecord(off_dobj);
                    if (flag) {
                        setOfflist(impl.getOffenceList());
                        JSFUtils.showMessage("Data saved successfully ");
                        setWidgetVar("select");
                        reset();
                    } else {
                        JSFUtils.showMessage("Data not saved ");
                    }
                    reset();
                }

            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        } else if (combovalue.equals("OVERLOAD SCHEDULE MASTER")) {

            try {

                if (getVM_OVLD_SCHEDULE_SRNO() == 0) {
                    JSFUtils.showMessage("Please Enter The Sr.No");
                    return;
                } else {
                    ovl_sch_dobj.setSrno(getVM_OVLD_SCHEDULE_SRNO());

                }
                if (getVM_OVLD_SCHEDULE_LDWTLOWER() == 0) {
                    JSFUtils.showMessage("Please Enter The Laden Weight Lower");
                    return;
                } else {
                    ovl_sch_dobj.setLd_wt_lower(getVM_OVLD_SCHEDULE_LDWTLOWER());

                }
                if (getVM_OVLD_SCHEDULE_LDWTUPPER() == 0) {
                    JSFUtils.showMessage("Please Enter The Laden Weight Upper");
                    return;
                } else {
                    ovl_sch_dobj.setLd_wt_upper(getVM_OVLD_SCHEDULE_LDWTUPPER());

                }
                if (getVM_OVLD_SCHEDULE_LDWTLOWER() >= getVM_OVLD_SCHEDULE_LDWTUPPER()) {
                    JSFUtils.showMessage("Laden  Upper Weight greater than Laden  Lower Weight.");
                    return;
                }
                if (getVM_OVLD_SCHEDULE_LDWTUNIT() == 0) {
                    JSFUtils.showMessage("Please Enter The Laden Weight Unit");
                    return;
                } else {
                    ovl_sch_dobj.setLd_wt_unit(Integer.toString(getVM_OVLD_SCHEDULE_LDWTUNIT()));

                }
                if (getVM_OVLD_SCHEDULE_FLATCFAMT() == 0) {
                    JSFUtils.showMessage("Please Enter The Flat Compounding Amount");
                    return;
                } else {
                    ovl_sch_dobj.setFlat_cf_amt(getVM_OVLD_SCHEDULE_FLATCFAMT());

                }
                if (getVM_OVLD_SCHEDULE_UNITCFAMT() == 0) {
                    JSFUtils.showMessage("Please Enter The Unit Compounding Amount");
                    return;
                } else {
                    ovl_sch_dobj.setUnit_cf_amt(getVM_OVLD_SCHEDULE_UNITCFAMT());

                }
                if (getVM_OVLD_SCHEDULE_WTUNITVAL() == 0) {
                    JSFUtils.showMessage("Please Enter The Weight Unit Value");
                    return;
                } else {
                    ovl_sch_dobj.setWt_unit_val(getVM_OVLD_SCHEDULE_WTUNITVAL());

                }

                boolean flag = impl.addOvlScheduleRecord(ovl_sch_dobj);
                if (flag) {
                    JSFUtils.showMessage("Data saved successfully ");
                    setWidgetVar("select");
                    setOvllist(impl.getOverLoadList());
                } else {
                    JSFUtils.showMessage("Data not saved ");
                }
                reset();

            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        } else if (combovalue.equals("OFFENCE PENALTY MASTER")) {

            try {


                if (getVM_OFF_PENALTY_SRNO() == 0) {
                    JSFUtils.showMessage("Please Enter The Sr.No");
                    return;
                } else {
                    offpenalty_dobj.setSrno((getVM_OFF_PENALTY_SRNO()));

                }
                if (getVM_OFF_PENALTY_CODE().equals("-1")) {
                    JSFUtils.showMessage("Please Select The Offence Code");
                    return;
                } else {
                    offpenalty_dobj.setNewoffcode(getVM_OFF_PENALTY_CODE());
                    offpenalty_dobj.setOldoffcode(getVM_OFF_PENALTY_CODE());

                }
                if (getSELECT_VM_OFF_PENALTY_VHCLASS() == null) {
                    JSFUtils.showMessage("Please Select The Vehicle Class");
                    return;
                } else {
                    offpenalty_dobj.setSelected_vhclass(getSELECT_VM_OFF_PENALTY_VHCLASS());

                }
                if (getVM_OFF_PENALTY_SECTIONCODE().equals("-1")) {
                    JSFUtils.showMessage("Please Select The Section Code");
                    return;
                } else {
                    offpenalty_dobj.setSectioncd((getVM_OFF_PENALTY_SECTIONCODE()));

                }
                if (getVM_OFF_PENALTY_OPENALTY() == 0 && !getVM_OFF_PENALTY_CODE().equals(TableConstants.OMVT_NON_PAYMENT_OF_TAX_UNDER_OMVT)) {
                    JSFUtils.showMessage("Value should be Greater than zero in Penalty 1");
                    return;
                } else {
                    offpenalty_dobj.setOpenalty((getVM_OFF_PENALTY_OPENALTY()));

                }
                if (getVM_OFF_PENALTY_DPENALTY() == 0 && !getVM_OFF_PENALTY_CODE().equals(TableConstants.OMVT_NON_PAYMENT_OF_TAX_UNDER_OMVT)) {
                    JSFUtils.showMessage("Value should be Greater than zero in Penalty 2");
                    return;
                } else {
                    offpenalty_dobj.setDpenalty((getVM_OFF_PENALTY_DPENALTY()));

                }
                if (getVM_OFF_PENALTY_CPENALTY() == 0 && !getVM_OFF_PENALTY_CODE().equals(TableConstants.OMVT_NON_PAYMENT_OF_TAX_UNDER_OMVT)) {
                    JSFUtils.showMessage("Value should be Greater than zero in Penalty 3");
                    return;
                } else {
                    offpenalty_dobj.setCpenalty((getVM_OFF_PENALTY_CPENALTY()));

                }
                if (SELECTED_ACCUESD == null) {
                    JSFUtils.showMessage("Please Select The Accused");
                    return;
                } else {
                    offpenalty_dobj.setSelected_accused(getSELECTED_ACCUESD());
                }

                boolean flag = impl.addOffPeanltyRecord(offpenalty_dobj);
                if (flag) {
                    JSFUtils.showMessage("Data saved successfully ");
                    setWidgetVar("select");
                    setOffPnllist(impl.getOffencePenaltyList());
                    setNewoffencelist(impl.getoffenceList(SELECT_VM_OFF_PENALTY_VHCLASS));

                } else {
                    JSFUtils.showMessage("Data not saved ");
                }
                reset();
            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        reset();
    }

    /**
     * @return the masterTableList
     */
    /**
     *
     */
    public String popUPForAddNewRecrod() {
        if (combovalue.equalsIgnoreCase("SELECT")) {
            JSFUtils.showMessage("Please Select Table from Drop Down Box ");
        }
        return "";
    }

    private void reset() {
        try {
            String blank = "";
            setVM_CHALLANBOOKEMPCODE(null);
            setVM_CHALLANBOOKNO("");
            setVM_CHALLANBOOK_NOFROM(null);
            setVM_CHALLANBOOK_NOUPTO(null);
            setVM_CHALLANBOOK_NOCURRENT(null);
            setVM_CHALLANBOOK_EXPIRED("");
            setVM_CHALLANBOOK_ISSDATE1(null);
            setVM_CHALLANBOOK_ISSBY("");
            setVM_CHALLANBOOK_OFFICECODE(null);
            setVM_COURT_CODE(null);
            setVM_COURT_NAME("");
            setVM_DA_CODE(null);
            setVM_DA_DESCR("");
            setVM_DA_SCHEDULE("");
            setVM_DA_SUB_SCHEDULE("");
            setVM_DA_OFFCODE("");
            SELECT_VM_OFF_PENALTY_VHCLASS.clear();
            SELECTED_ACCUESD.clear();
            int maxdasrno = impl.fetchDaSrNo();
            setVM_DA_PENALTY_SRNO(maxdasrno);
            setVM_DA_PENALTY_DACODE("");
            setVM_DA_PENALTY_VHCLASS("");
            setVM_DA_PENALTY_OPENALTY(null);
            setVM_DA_PENALTY_DPENALTY(null);
            setVM_DA_PENALTY_CPENALTY(null);
            setVM_DA_PENALTY_SUSDAYS(null);
            setVM_DA_PENALTY_OPENALTY1(null);
            setVM_DA_PENALTY_DPENALTY1(null);
            setVM_DA_PENALTY_CPENALTY1(null);
            setVM_DA_PENALTY_SUSDAYS1(null);
            setVM_DA_PENALTY_OPENALTY2(null);
            setVM_DA_PENALTY_DPENALTY2(null);
            setVM_DA_PENALTY_CPENALTY2(null);
            setVM_DA_PENALTY_SUSDAYS2(null);
            setVM_DA_PENALTY_SUSACTION("");
            int maxexschsrno = impl.fetchExSchSrno();
            setVM_EX_SCHEDULE_SRNO(maxexschsrno);
            setVM_EX_SCHEDULE_TYPE1("");
            setVM_EX_SCHEDULE_VHCLASS("");
            setVM_EX_SCHEDULE_FLATRATE(null);
            setVM_EX_SCHEDULE_UNITRATE(null);
            setVM_OFFENCE_CODE(null);
            setVM_OFFENCE_DESC("");
            setVM_OFFENCE_MVACLAUSE("");
            setVM_OFFENCE_CMVRCLAUSE("");
            setVM_OFFENCE_SMVRCLAUSE("");
            setVM_OFFENCE_ADDLDESC("");
            setVM_OFFENCE_PUNISHMENT1("");
            setVM_OFFENCE_PUNISHMENT2("");
            setVM_OFFENCE_ISACT_TPT("");
            setVM_OFFENCE_ISACT_POLICE("");
            int maxoffpnlsrno = impl.fetchOffPenlSrno();
            setVM_OFF_PENALTY_SRNO(maxoffpnlsrno);
            setVM_OFFENCE_CODE(impl.getMaxOffenceCode());
            setVM_OFF_PENALTY_CODE("");
            setVM_OFF_PENALTY_VHCLASS("-1");
            setVM_OFF_PENALTY_SECTIONCODE("");
            setVM_OFF_PENALTY_OPENALTY(null);
            setVM_OFF_PENALTY_DPENALTY(null);
            setVM_OFF_PENALTY_CPENALTY(null);
            setVM_OFF_PENALTY_OPENALTY1(null);
            setVM_OFF_PENALTY_DPENALTY1(null);
            setVM_OFF_PENALTY_CPENALTY1(null);
            setVM_OFF_PENALTY_OPENALTY2(null);
            setVM_OFF_PENALTY_DPENALTY2(null);
            setVM_OFF_PENALTY_CPENALTY2(null);
            setVM_SECTION_CODE(null);
            setVM_SECTION_LEVELFLAG("");
            setVM_SECTION_NAME("");
            setVM_SECTION_ISACTIVE("");
            int maxovrldsrno = impl.fetchOvrldSrno();
            setVM_OVLD_SCHEDULE_SRNO(maxovrldsrno);
            setVM_OVLD_SCHEDULE_LDWTLOWER(null);
            setVM_OVLD_SCHEDULE_LDWTUPPER(null);
            setVM_OVLD_SCHEDULE_LDWTUNIT(null);
            setVM_OVLD_SCHEDULE_FLATCFAMT(null);
            setVM_OVLD_SCHEDULE_UNITCFAMT(null);
            setVM_OVLD_SCHEDULE_WTUNITVAL(null);
            errLabelMsg = "";
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public static Timestamp getTimeStamp(String Date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy");
        Timestamp st = null;
        try {
            java.util.Date date = sdf.parse(Date);
            st = new Timestamp(date.getTime());
        } catch (ParseException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return st;
    }

    public void UpdateMasterTable(RowEditEvent event) {
        if (combovalue.equals("CHALLAN BOOK MASTER")) {

            String expired = "";

            try {
                VmChlnBookDobj chlnbook_dobj = (VmChlnBookDobj) event.getObject();

                expired = chlnbook_dobj.getExpired();
                if (chlnbook_dobj.getUsercode() == 0) {
                    JSFUtils.showMessage("Please Enter The Emp Code");
                    return;
                } else {
                    chlnbook_dobj.setUsercode(chlnbook_dobj.getUsercode());

                }

                if (chlnbook_dobj.getChln_frm() == 0) {
                    JSFUtils.showMessage(" Challan No From must be greater then Zero ");

                    return;
                } else {
                    chlnbook_dobj.setChln_frm(chlnbook_dobj.getChln_frm());

                }
                if (chlnbook_dobj.getChln_upto() == 0) {
                    JSFUtils.showMessage("Challan No Upto must be greater then Zero ");
                    return;
                } else {
                    chlnbook_dobj.setChln_upto(chlnbook_dobj.getChln_upto());

                }
                if (chlnbook_dobj.getCurr_chln_no() == 0) {
                    JSFUtils.showMessage("Current Challan No must be greater then Zero ");
                    return;
                } else {
                    chlnbook_dobj.setCurr_chln_no(chlnbook_dobj.getCurr_chln_no());

                }
                if (expired.equals("-1") || expired.equals("Select")) {
                    JSFUtils.showMessage("Please Select Expired or not ");
                    return;
                } else {
                    chlnbook_dobj.setExpired(expired);

                }
                if (chlnbook_dobj.getIss_dt() == null || chlnbook_dobj.getIss_dt().equals("")) {
                    JSFUtils.showMessage("Please Enter Issue Date ");
                    return;
                } else {
                    chlnbook_dobj.setIss_dt(chlnbook_dobj.getIss_dt());

                }
                if (chlnbook_dobj.getIss_by() == null || chlnbook_dobj.getIss_by().equals("")) {
                    JSFUtils.showMessage("Please Enter Issue By ");
                    return;
                } else {
                    chlnbook_dobj.setIss_by(chlnbook_dobj.getIss_by().toUpperCase());

                }

                boolean flag = impl.updateChallanMasterRecord(chlnbook_dobj, previous_chlnbook_dobj);
                if (flag) {
                    JSFUtils.showMessage("Data updated sucessfully");
                    setChallanlist(impl.getChallanBookList());
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Data updated sucessfully"));

                } else if (flag == false) {
                    setChallanlist(impl.getChallanBookList());
                    JSFUtils.showMessage("Data not updated ");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Data not updated sucessfully"));
                }

            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        } else if (combovalue.equals("COURT MASTER")) {

            VmCourtDobj court_dobj = (VmCourtDobj) event.getObject();

            try {


                if (court_dobj.getCourtname() == null || court_dobj.getCourtname().equals("")) {
                    JSFUtils.showMessage("Please Enter The Court Name");
                    return;
                } else {
                    court_dobj.setCourtname(court_dobj.getCourtname().toUpperCase());

                }


                boolean flag = impl.updateCourtMasterRecord(court_dobj);
                if (flag) {
                    reset();
                    setCourtlist(impl.getCourtList());
                    JSFUtils.showMessage("Data updated sucessfully");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Data  updated sucessfully"));
                } else {
                    JSFUtils.showMessage("Data not updated ");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Data not updated sucessfully"));
                }
                reset();



            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        } else if (combovalue.equals("DA MASTER")) {


            try {
                VmDaDobj da_dobj = (VmDaDobj) event.getObject();
                if (da_dobj.getDescription() == null || da_dobj.getDescription().equals("")) {
                    JSFUtils.showMessage("Please Enter The Da Description");
                    return;
                } else {
                    da_dobj.setDescription(da_dobj.getDescription().toUpperCase());

                }
                if (da_dobj.getOffence_code().equals("Select") || da_dobj.getOffence_code().equals("-1")) {
                    JSFUtils.showMessage("Please Select The Da Offence Code");
                    return;
                } else {
                    da_dobj.setOffence_code(da_dobj.getOffence_code());

                }
                if (da_dobj.getDa_schedule() == null || da_dobj.getDa_schedule().equals("")) {
                    JSFUtils.showMessage("Please Enter The Da Schedule");
                    return;
                } else {
                    da_dobj.setDa_schedule(da_dobj.getDa_schedule());

                }
                if (da_dobj.getDa_sub_schedule() == null || da_dobj.getDa_sub_schedule().equals("")) {
                    JSFUtils.showMessage("Please Enter The Da Sub Schedule");
                    return;
                } else {
                    da_dobj.setDa_sub_schedule(da_dobj.getDa_sub_schedule());

                }


                boolean flag = impl.updateDAMasterRecord(da_dobj);
                if (flag) {
                    Dalist.clear();
                    setDalist(impl.getDaList());
                    reset();
                    JSFUtils.showMessage("Data updated sucessfully");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Data updated sucessfully"));
                } else {
                    JSFUtils.showMessage("Data not updated ");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Data not updated "));
                }
                reset();


            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        } else if (combovalue.equals("DA PENALTY MASTER")) {


            VmDaPenaltyDobj dapenalty_dobj = (VmDaPenaltyDobj) event.getObject();
            try {


                if (dapenalty_dobj.getDacode() == 0) {
                    JSFUtils.showMessage("Please Enter The Da Code");
                    return;
                } else {
                    dapenalty_dobj.setDacode(dapenalty_dobj.getDacode());

                }
                if (dapenalty_dobj.getVhclass().equals("Select") || dapenalty_dobj.getVhclass().equals("-1")) {
                    JSFUtils.showMessage("Please Select The Vehicle Class");
                    return;
                } else {
                    dapenalty_dobj.setVhclass(dapenalty_dobj.getVhclass());

                }
                if (dapenalty_dobj.getOpenalty() == 0) {
                    JSFUtils.showMessage("Please Enter The Owner Penalty");
                    return;
                } else {
                    dapenalty_dobj.setOpenalty(dapenalty_dobj.getOpenalty());

                }
                if (dapenalty_dobj.getDpenalty() == 0) {
                    JSFUtils.showMessage("Please Enter The Driver Penalty");
                    return;
                } else {
                    dapenalty_dobj.setDpenalty(dapenalty_dobj.getDpenalty());

                }
                if (dapenalty_dobj.getCpenalty() == 0) {
                    JSFUtils.showMessage("Please Enter The Conductor Penalty");
                    return;
                } else {
                    dapenalty_dobj.setCpenalty(dapenalty_dobj.getDpenalty());

                }
                if (dapenalty_dobj.getSusdays() == 0) {
                    JSFUtils.showMessage("Please Enter The Sus Days");
                    return;
                } else {
                    dapenalty_dobj.setSusdays(dapenalty_dobj.getSusdays());

                }
                if (dapenalty_dobj.getOpenalty1() == 0) {
                    JSFUtils.showMessage("Please Enter The Owner Penalty1");
                    return;
                } else {
                    dapenalty_dobj.setOpenalty1(dapenalty_dobj.getOpenalty1());

                }
                if (dapenalty_dobj.getDpenalty1() == 0) {
                    JSFUtils.showMessage("Please Enter The Driver Penalty1");
                    return;
                } else {
                    dapenalty_dobj.setDpenalty1(dapenalty_dobj.getDpenalty1());

                }
                if (dapenalty_dobj.getCpenalty1() == 0) {
                    JSFUtils.showMessage("Please Enter The Conductor Penalty1");
                    return;
                } else {
                    dapenalty_dobj.setCpenalty1(dapenalty_dobj.getDpenalty1());

                }
                if (dapenalty_dobj.getSusdays1() == 0) {
                    JSFUtils.showMessage("Please Enter The Sus Days1");
                    return;
                } else {
                    dapenalty_dobj.setSusdays1(dapenalty_dobj.getSusdays1());

                }
                if (dapenalty_dobj.getOpenalty2() == 0) {
                    JSFUtils.showMessage("Please Enter The Owner Penalty2");
                    return;
                } else {
                    dapenalty_dobj.setOpenalty2(dapenalty_dobj.getOpenalty2());

                }
                if (dapenalty_dobj.getDpenalty2() == 0) {
                    JSFUtils.showMessage("Please Enter The Driver Penalty2");
                    return;
                } else {
                    dapenalty_dobj.setDpenalty2(dapenalty_dobj.getDpenalty2());

                }
                if (dapenalty_dobj.getCpenalty2() == 0) {
                    JSFUtils.showMessage("Please Enter The Conductor Penalty2");
                    return;
                } else {
                    dapenalty_dobj.setCpenalty2(dapenalty_dobj.getDpenalty2());

                }
                if (dapenalty_dobj.getSusdays2() == 0) {
                    JSFUtils.showMessage("Please Enter Sus Days2");
                    return;
                } else {
                    dapenalty_dobj.setSusdays2(dapenalty_dobj.getSusdays2());

                }
                if (dapenalty_dobj.getSusaction() == null || dapenalty_dobj.getSusaction().equals("")) {
                    JSFUtils.showMessage("Please Enter The Sus Action");
                    return;
                } else {
                    dapenalty_dobj.setSusaction(dapenalty_dobj.getSusaction());

                }

                boolean flag = impl.updateDaPenaltyMasterRecord(dapenalty_dobj);
                if (flag) {
                    setDaPenaltylist(impl.getDaPenaltyList());
                    JSFUtils.showMessage("Data updated sucessfully");
                    // RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Data updated sucessfully"));
                } else {
                    JSFUtils.showMessage("Data not updated ");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Data not updated "));
                }
                reset();

            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        } else if (combovalue.equals("EXCESS SCHEDULE MASTER")) {



            try {
                VmExScheduleDobj exsch_dobj = (VmExScheduleDobj) event.getObject();

                if (exsch_dobj.getType() == null || exsch_dobj.getType().equals("")) {
                    JSFUtils.showMessage("Please Enter The Excess Schedule Type");
                    return;
                } else {
                    exsch_dobj.setType(exsch_dobj.getType());

                }
                if (exsch_dobj.getVh_class().equals("") || exsch_dobj.getVh_class().equals("-1")) {
                    JSFUtils.showMessage("Please Select The Excess Schedule Vehicle class");
                    return;
                } else {
                    exsch_dobj.setVh_class(exsch_dobj.getVh_class());

                }
                if (exsch_dobj.getFlatrate() == 0) {
                    JSFUtils.showMessage("Please Enter The Excess Schedule Flat Rate");
                    return;
                } else {
                    exsch_dobj.setFlatrate(exsch_dobj.getFlatrate());

                }
                if (exsch_dobj.getUnitrate() == 0) {
                    JSFUtils.showMessage("Please Enter The Excess Schedule Unit Rate");
                    return;
                } else {
                    exsch_dobj.setUnitrate(exsch_dobj.getUnitrate());

                }

                boolean flag = impl.updateExScheduleMasterRecord(exsch_dobj);
                if (flag) {
                    setExslist(impl.getExSchList());
                    JSFUtils.showMessage("Data updated sucessfully");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Data  updated sucessfully"));
                } else {
                    JSFUtils.showMessage("Data not updated ");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Data not updated "));
                }
                reset();

            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        } else if (combovalue.equals("OFFENCE MASTER")) {



            try {
                VmOffenceDobj off_dobj = (VmOffenceDobj) event.getObject();

                if (off_dobj.getOffence_desc() == null || off_dobj.getOffence_desc().equals("")) {
                    JSFUtils.showMessage("Please Enter The Offence Description");
                    return;
                } else {
                    off_dobj.setOffence_desc(off_dobj.getOffence_desc().toUpperCase());

                }
                if (off_dobj.getSmvr_clause() == null || off_dobj.getSmvr_clause().equals("")) {
                    JSFUtils.showMessage("Please Enter The SMVR Clause");
                    return;
                } else {
                    off_dobj.setSmvr_clause(off_dobj.getSmvr_clause());

                }
                if (off_dobj.getAddl_desc() == null || off_dobj.getAddl_desc().equals("")) {
                    JSFUtils.showMessage("Please Enter The Additional Description");
                    return;
                } else {
                    off_dobj.setAddl_desc(off_dobj.getAddl_desc().toUpperCase());

                }
                if (off_dobj.getPunishment1() == null || off_dobj.getPunishment1().equals("")) {
                    JSFUtils.showMessage("Please Enter The Punishment 1");
                    return;
                } else {
                    off_dobj.setPunishment1(off_dobj.getPunishment1().toUpperCase());

                }
                if (off_dobj.getPunishment2() == null || off_dobj.getPunishment2().equals("")) {
                    JSFUtils.showMessage("Please Enter The Punishment 2");
                    return;
                } else {
                    off_dobj.setPunishment2(off_dobj.getPunishment2().toUpperCase());

                }
                if (off_dobj.getIsactivefor_tpt().equals("-1") || off_dobj.getIsactivefor_tpt().equals("Select")) {
                    JSFUtils.showMessage("Please Select Is active for TPT or not");
                    return;
                } else {
                    off_dobj.setIsactivefor_tpt(off_dobj.getIsactivefor_tpt());

                }
                if (off_dobj.getIsactivefor_police().equals("-1") || off_dobj.getIsactivefor_police().equals("Select")) {
                    JSFUtils.showMessage("Please Select Is active for Police or not");
                    return;
                } else {
                    off_dobj.setIsactivefor_police(off_dobj.getIsactivefor_police());

                }


                boolean flag = impl.updateOffenceMasterRecord(off_dobj);
                if (flag) {
                    setOfflist(impl.getOffenceList());
                    reset();
                    JSFUtils.showMessage("Data updated sucessfully");
                    // RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Data updated sucessfully"));
                } else {
                    JSFUtils.showMessage("Data not updated sucessfully");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Data not updated sucessfully"));
                }

                reset();

            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        } else if (combovalue.equals("OFFENCE PENALTY MASTER")) {



            try {
                VmOffencePenaltyDobj offpenalty_dobj = (VmOffencePenaltyDobj) event.getObject();


                if (offpenalty_dobj.getOpenalty() == 0) {
                    JSFUtils.showMessage("Please Enter The Owner Penalty");
                    return;
                } else {
                    offpenalty_dobj.setOpenalty(offpenalty_dobj.getOpenalty());

                }
                if (offpenalty_dobj.getDpenalty() == 0) {
                    JSFUtils.showMessage("Please Enter The Driver Penalty");
                    return;
                } else {
                    offpenalty_dobj.setDpenalty(offpenalty_dobj.getDpenalty());

                }
                if (offpenalty_dobj.getCpenalty() == 0) {
                    JSFUtils.showMessage("Please Enter The Conductor Penalty");
                    return;
                } else {
                    offpenalty_dobj.setCpenalty(offpenalty_dobj.getCpenalty());

                }

                boolean flag = impl.updateOffencePenaltyMasterRecord(offpenalty_dobj);
                if (flag) {
                    setOffPnllist(impl.getOffencePenaltyList());
                    JSFUtils.showMessage("Data updated sucessfully");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Data  updated sucessfully"));
                } else {
                    JSFUtils.showMessage("Data not updated ");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Data not updated sucessfully"));
                }

                reset();

            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);

            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        } else if (combovalue.equals("OVERLOAD SCHEDULE MASTER")) {



            try {
                VmOvlScheduleDobj ovl_sch_dobj = (VmOvlScheduleDobj) event.getObject();

                if (ovl_sch_dobj.getLd_wt_lower() == 0) {
                    JSFUtils.showMessage("Please Enter The Laden Weight Lower");
                    JSFUtils.showMessagesInDialog("Alert", "Please Enter The Laden Weight Lower", FacesMessage.SEVERITY_ERROR);
                    return;
                } else {
                    ovl_sch_dobj.setLd_wt_lower(ovl_sch_dobj.getLd_wt_lower());

                }
                if (ovl_sch_dobj.getLd_wt_upper() == 0) {
                    JSFUtils.showMessage("Please Enter The Laden Weight Upper");
                    JSFUtils.showMessagesInDialog("Alert", "Please Enter The Laden Weight Upper", FacesMessage.SEVERITY_ERROR);
                    return;
                } else {
                    ovl_sch_dobj.setLd_wt_upper(ovl_sch_dobj.getLd_wt_upper());

                }
                if (ovl_sch_dobj.getLd_wt_upper() > ovl_sch_dobj.getLd_wt_lower()) {
                } else {
                    JSFUtils.showMessagesInDialog("Alert", "Laden Weight Upper greater than Laden Weight Lower", FacesMessage.SEVERITY_ERROR);
                    return;
                }
                if (ovl_sch_dobj.getLd_wt_unit() == null || ovl_sch_dobj.getLd_wt_unit().equals("")) {
                    JSFUtils.showMessage("Please Enter The Laden Weight Unit");
                    return;
                } else {
                    ovl_sch_dobj.setLd_wt_unit(ovl_sch_dobj.getLd_wt_unit());

                }
                if (ovl_sch_dobj.getFlat_cf_amt() == 0) {
                    JSFUtils.showMessage("Please Enter The Flat Compounding Amount");
                    return;
                } else {
                    ovl_sch_dobj.setFlat_cf_amt(ovl_sch_dobj.getFlat_cf_amt());

                }
                if (ovl_sch_dobj.getUnit_cf_amt() == 0) {
                    JSFUtils.showMessage("Please Enter The Unit Compounding Amount");
                    return;
                } else {
                    ovl_sch_dobj.setUnit_cf_amt(ovl_sch_dobj.getUnit_cf_amt());

                }
                if (ovl_sch_dobj.getWt_unit_val() == 0) {
                    JSFUtils.showMessage("Please Enter The Weight Unit Value");
                    return;
                } else {
                    ovl_sch_dobj.setWt_unit_val(ovl_sch_dobj.getWt_unit_val());

                }

                boolean flag = impl.updateOvlScheduleMasterRecord(ovl_sch_dobj);
                if (flag) {
                    setOvllist(impl.getOverLoadList());
                    JSFUtils.showMessage("Data updated sucessfully");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Data  updated sucessfully"));
                } else {
                    JSFUtils.showMessage("Data not updated ");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!", "Data not updated "));
                }
                reset();

            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

    }

    public void DeleteRowwiseData(RowEditEvent event) {
        if (combovalue.equals("CHALLAN BOOK MASTER")) {


            try {
                VmChlnBookDobj chlnbook_dobj = (VmChlnBookDobj) event.getObject();



                boolean flag = impl.DeleteChallanMasterRecord(chlnbook_dobj);
                if (flag) {
                    JSFUtils.showMessage("Data Deleted sucessfully");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Data Deleted sucessfully"));
                    challanlist.clear();
                    setChallanlist(impl.getChallanBookList());
                } else if (flag == false) {
                    challanlist.clear();
                    setChallanlist(impl.getChallanBookList());
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Data not updated"));
                }

            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        } else if (combovalue.equals("COURT MASTER")) {



            try {
                VmCourtDobj court_dobj = (VmCourtDobj) event.getObject();


                boolean flag = impl.DeleteCourtMasterRecord(court_dobj);
                if (flag) {
                    reset();
                    setCourtlist(impl.getCourtList());
                    JSFUtils.showMessage("Data Deleted sucessfully");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Data Deleted sucessfully"));
                } else {
                    JSFUtils.showMessage("Data not updated ");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Data not  updated"));
                }
                reset();

            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        } else if (combovalue.equals("DA MASTER")) {


            try {
                VmDaDobj da_dobj = (VmDaDobj) event.getObject();


                boolean flag = impl.DeleteDAMasterRecord(da_dobj);
                if (flag) {
                    setDalist(impl.getDaList());
                    reset();
                    JSFUtils.showMessage("Data Deleted sucessfully");
                    // RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Data Deleted sucessfully"));
                } else {
                    JSFUtils.showMessage("Data not updated ");
                    // RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Data not  updated"));
                }
                reset();

            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        } else if (combovalue.equals("DA PENALTY MASTER")) {


            try {
                VmDaPenaltyDobj dapenalty_dobj = (VmDaPenaltyDobj) event.getObject();


                boolean flag = impl.DeleteDaPenaltyMasterRecord(dapenalty_dobj);
                if (flag) {
                    setDaPenaltylist(impl.getDaPenaltyList());
                    JSFUtils.showMessage("Data Deleted sucessfully");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Data Deleted sucessfully"));
                } else {
                    JSFUtils.showMessage("Data not updated ");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Data not  updated"));
                }
                reset();

            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        } else if (combovalue.equals("EXCESS SCHEDULE MASTER")) {



            try {
                VmExScheduleDobj exsch_dobj = (VmExScheduleDobj) event.getObject();

                boolean flag = impl.DeleteExScheduleMasterRecord(exsch_dobj);
                if (flag) {
                    Exslist.clear();
                    setExslist(impl.getExSchList());
                    JSFUtils.showMessage("Data Deleted sucessfully");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Data Deleted sucessfully"));
                } else {
                    JSFUtils.showMessage("Data not updated ");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Data not  updated"));
                }
                reset();

            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        } else if (combovalue.equals("OFFENCE MASTER")) {



            try {
                VmOffenceDobj off_dobj = (VmOffenceDobj) event.getObject();


                boolean flag = impl.DeleteOffenceMasterRecord(off_dobj);
                if (flag) {
                    setOfflist(impl.getOffenceList());
                    reset();
                    JSFUtils.showMessage("Data Deleted sucessfully");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Data Deleted sucessfully"));
                } else {
                    JSFUtils.showMessage("Data not updated sucessfully");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Data not  updated"));
                }

            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        } else if (combovalue.equals("OFFENCE PENALTY MASTER")) {




            try {
                VmOffencePenaltyDobj offpenalty_dobj = (VmOffencePenaltyDobj) event.getObject();


                boolean flag = impl.DeleteOffencePenaltyMasterRecord(offpenalty_dobj);
                if (flag) {
                    setOffPnllist(impl.getOffencePenaltyList());
                    JSFUtils.showMessage("Data Deleted sucessfully");
                    // RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Data Deleted sucessfully"));
                } else {
                    setOffPnllist(impl.getOffencePenaltyList());
                    JSFUtils.showMessage("Data not updated ");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Data not  updated"));
                }

            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        } else if (combovalue.equals("OVERLOAD SCHEDULE MASTER")) {



            try {

                VmOvlScheduleDobj ovl_sch_dobj = (VmOvlScheduleDobj) event.getObject();
                boolean flag = impl.DeleteOvlScheduleMasterRecord(ovl_sch_dobj);
                if (flag) {
                    setOvllist(impl.getOverLoadList());
                    JSFUtils.showMessage("Data Deleted sucessfully");
                    // RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_INFO, "Alert!", "Data Deleted sucessfully"));
                } else {
                    JSFUtils.showMessage("Data not updated ");
                    //RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Data not  updated"));
                }
                reset();

            } catch (SQLException e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
    }

    public void showOfficeList() {
        try {
            setOfficeList(impl.getOfficeCode(getVM_CHALLANBOOKEMPCODE()));

        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void checkOffence() throws Exception {
        boolean check = false;
        String vhClass = "";
        for (Object vh_class : SELECT_VM_OFF_PENALTY_VHCLASS) {
            vhClass = vh_class.toString();
            check = impl.isOffenceAlreadyApply(VM_OFF_PENALTY_CODE, vhClass);
            if (check) {
                SELECT_VM_OFF_PENALTY_VHCLASS.remove(vhClass);
                String vh_classdesc = impl.getVhClassDescr(vhClass);
                String offDescr = impl.getoff_descr(VM_OFF_PENALTY_CODE);
                JSFUtils.showMessage(offDescr + " Offence Is Already Applied on " + vh_classdesc + ".");
                //  RequestContext rc = RequestContext.getCurrentInstance();
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", " " + offDescr + " Offence Is Already Applied on " + vh_classdesc + ""));
                return;
            }

        }

    }

    public void checkAccused() throws Exception {
        boolean check = false;
        String vhClass = "";
        String accused = "";
        for (Object accuse : SELECTED_ACCUESD) {
            accused = accuse.toString();
            for (Object vh_vlass : SELECT_VM_OFF_PENALTY_VHCLASS) {
                vhClass = vh_vlass.toString();
                check = impl.isAccusedAlreadyApply(vhClass, accused, VM_OFF_PENALTY_CODE);
                if (check) {
                    String desc = impl.getVhClassDescr(vhClass);
                    String accDescr = impl.getAccusedDesc(accused);
                    JSFUtils.showMessage(" Accused " + accDescr + " Is Already Applied on " + desc + ".");
                    // RequestContext rc = RequestContext.getCurrentInstance();
                    PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", " Accused " + accDescr + " Is Already Applied on " + desc + ""));
                    SELECTED_ACCUESD.remove(accused);
                    // return;
                }

            }

            return;
        }

    }

    public static Date getStringToDateYYYY_DD_MM(String strDt) {
        Date dt = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            dt = sdf.parse(strDt);
        } catch (ParseException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
        return dt;
    }
    //check book number

    public void isBookNoExist() {
        boolean check;
        try {
            check = impl.checkBookNoAlreadyExist(VM_CHALLANBOOKNO, VM_CHALLANBOOKEMPCODE);
            if (check) {
                setVM_CHALLANBOOKNO("");
                // RequestContext rc = RequestContext.getCurrentInstance();
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "Book Number Is Already Exist"));
                return;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

    }
    //check DA Code

    public void isDaCodeExist() {
        boolean check;
        try {
            check = impl.isDaCodeAlreadyExist(VM_DA_PENALTY_DACODE);
            if (check) {
                setVM_CHALLANBOOKNO("");
                // RequestContext rc = RequestContext.getCurrentInstance();
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_WARN, "Alert!", "DA Code Is Already Exist"));
                return;
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

    }

    /**
     * @return the errLabelMsg
     */
    public String getErrLabelMsg() {
        return errLabelMsg;
    }

    /**
     * @param errLabelMsg the errLabelMsg to set
     */
    public void setErrLabelMsg(String errLabelMsg) {
        this.errLabelMsg = errLabelMsg;
    }

    /**
     * @return the widgetVar
     */
    public String getWidgetVar() {
        return widgetVar;
    }

    /**
     * @param widgetVar the widgetVar to set
     */
    public void setWidgetVar(String widgetVar) {
        this.widgetVar = widgetVar;
    }

    public List getOffenceList() {
        return offenceList;
    }

    public void setOffenceList(List aOffenceList) {
        offenceList = aOffenceList;
    }

    /**
     * @return the isUpdateBtn
     */
    /**
     * @return the districtList
     */
    public List getVechileclassList() {
        return VechileclassList;
    }

    public void setVechileclassList(List aVechileclassList) {
        VechileclassList = aVechileclassList;
    }

    /**
     * @return the createUpdateTitle
     */
    public String getCreateUpdateTitle() {
        return createUpdateTitle;
    }

    /**
     * @param createUpdateTitle the createUpdateTitle to set
     */
    public void setCreateUpdateTitle(String createUpdateTitle) {
        this.createUpdateTitle = createUpdateTitle;
    }

    /**
     * @return the isExceedPage
     */
    public InputText getVM_ACCUSEDCODE() {
        return VM_ACCUSEDCODE;
    }

    public void setVM_ACCUSEDCODE(InputText VM_ACCUSEDCODE) {
        this.VM_ACCUSEDCODE = VM_ACCUSEDCODE;
    }

    public InputText getVM_ACCUSEDDESCR() {
        return VM_ACCUSEDDESCR;
    }

    public void setVM_ACCUSEDDESCR(InputText VM_ACCUSEDDESCR) {
        this.VM_ACCUSEDDESCR = VM_ACCUSEDDESCR;
    }

    public Calendar getVM_CHALLANBOOK_ISSDATE() {
        return VM_CHALLANBOOK_ISSDATE;
    }

    public void setVM_CHALLANBOOK_ISSDATE(Calendar VM_CHALLANBOOK_ISSDATE) {
        this.VM_CHALLANBOOK_ISSDATE = VM_CHALLANBOOK_ISSDATE;
    }

    public String getStatecode() {
        return statecode;
    }

    public void setStatecode(String statecode) {
        this.statecode = statecode;
    }

    public String getVechile_class() {
        return vechile_class;
    }

    public void setVechile_class(String vechile_class) {
        this.vechile_class = vechile_class;
    }

    public String getOffencecode() {
        return offencecode;
    }

    public void setOffencecode(String offencecode) {
        this.offencecode = offencecode;
    }

    public String getSectioncode() {
        return sectioncode;
    }

    public void setSectioncode(String sectioncode) {
        this.sectioncode = sectioncode;
    }

    public List<VmOffencePenaltyDobj> getNewoffencelist() {
        return newoffencelist;
    }

    public void setNewoffencelist(List<VmOffencePenaltyDobj> newoffencelist) {
        this.newoffencelist = newoffencelist;
    }

    public List getYesnolist() {
        return yesnolist;
    }

    public void setYesnolist(List yesnolist) {
        this.yesnolist = yesnolist;
    }

    public String getVM_EX_SCHEDULE_TYPE1() {
        return VM_EX_SCHEDULE_TYPE1;
    }

    public void setVM_EX_SCHEDULE_TYPE1(String VM_EX_SCHEDULE_TYPE1) {
        this.VM_EX_SCHEDULE_TYPE1 = VM_EX_SCHEDULE_TYPE1;
    }

    public String getYesno() {
        return yesno;
    }

    public void setYesno(String yesno) {
        this.yesno = yesno;
    }

    public String getVM_DA_OFFCODE() {
        return VM_DA_OFFCODE;
    }

    public void setVM_DA_OFFCODE(String VM_DA_OFFCODE) {
        this.VM_DA_OFFCODE = VM_DA_OFFCODE;
    }

    public String getOffence_cd() {
        return offence_cd;
    }

    public void setOffence_cd(String offence_cd) {
        this.offence_cd = offence_cd;
    }

    public Date getVM_CHALLANBOOK_ISSDATE1() {
        return VM_CHALLANBOOK_ISSDATE1;
    }

    public void setVM_CHALLANBOOK_ISSDATE1(Date VM_CHALLANBOOK_ISSDATE1) {
        this.VM_CHALLANBOOK_ISSDATE1 = VM_CHALLANBOOK_ISSDATE1;
    }

    public List<VmCourtDobj> getCourtlist() {
        return courtlist;
    }

    public void setCourtlist(List<VmCourtDobj> courtlist) {
        this.courtlist = courtlist;
    }

    public List getCombofilllist() {
        return combofilllist;
    }

    public void setCombofilllist(List combofilllist) {
        this.combofilllist = combofilllist;
    }

    public String getCombovalue() {
        return combovalue;
    }

    public void setCombovalue(String combovalue) {
        this.combovalue = combovalue;
    }

    public List<VmChlnBookDobj> getChallanlist() {
        return challanlist;
    }

    public void setChallanlist(List<VmChlnBookDobj> challanlist) {
        this.challanlist = challanlist;
    }

    public List<VmDaDobj> getDalist() {
        return Dalist;
    }

    public void setDalist(List<VmDaDobj> Dalist) {
        this.Dalist = Dalist;
    }

    public List<VmDaPenaltyDobj> getDaPenaltylist() {
        return DaPenaltylist;
    }

    public void setDaPenaltylist(List<VmDaPenaltyDobj> DaPenaltylist) {
        this.DaPenaltylist = DaPenaltylist;
    }

    public List getVechilelist() {
        return vechilelist;
    }

    public void setVechilelist(List vechilelist) {
        this.vechilelist = vechilelist;
    }

    public boolean isChallanpanel() {
        return challanpanel;
    }

    public void setChallanpanel(boolean challanpanel) {
        this.challanpanel = challanpanel;
    }

    public boolean isCourtpanel() {
        return courtpanel;
    }

    public void setCourtpanel(boolean courtpanel) {
        this.courtpanel = courtpanel;
    }

    public boolean isDapanel() {
        return dapanel;
    }

    public void setDapanel(boolean dapanel) {
        this.dapanel = dapanel;
    }

    public boolean isDapenaltypanel() {
        return dapenaltypanel;
    }

    public void setDapenaltypanel(boolean dapenaltypanel) {
        this.dapenaltypanel = dapenaltypanel;
    }

    public List<VmExScheduleDobj> getExslist() {
        return Exslist;
    }

    public void setExslist(List<VmExScheduleDobj> Exslist) {
        this.Exslist = Exslist;
    }

    public boolean isExspanel() {
        return Exspanel;
    }

    public void setExspanel(boolean Exspanel) {
        this.Exspanel = Exspanel;
    }

    public List<VmOffenceDobj> getOfflist() {
        return Offlist;
    }

    public void setOfflist(List<VmOffenceDobj> Offlist) {
        this.Offlist = Offlist;
    }

    public boolean isOffencepanel() {
        return Offencepanel;
    }

    public void setOffencepanel(boolean Offencepanel) {
        this.Offencepanel = Offencepanel;
    }

    public List<VmOffencePenaltyDobj> getOffPnllist() {
        return OffPnllist;
    }

    public void setOffPnllist(List<VmOffencePenaltyDobj> OffPnllist) {
        this.OffPnllist = OffPnllist;
    }

    public boolean isOffPnlpanel() {
        return OffPnlpanel;
    }

    public void setOffPnlpanel(boolean OffPnlpanel) {
        this.OffPnlpanel = OffPnlpanel;
    }

    public List<VmSectionsDobj> getSectnlist() {
        return Sectnlist;
    }

    public void setSectnlist(List<VmSectionsDobj> Sectnlist) {
        this.Sectnlist = Sectnlist;
    }

    public boolean isSectnpanel() {
        return Sectnpanel;
    }

    public void setSectnpanel(boolean Sectnpanel) {
        this.Sectnpanel = Sectnpanel;
    }

    public List<VmOvlScheduleDobj> getOvllist() {
        return ovllist;
    }

    public void setOvllist(List<VmOvlScheduleDobj> ovllist) {
        this.ovllist = ovllist;
    }

    public boolean isOvlpanel() {
        return ovlpanel;
    }

    public void setOvlpanel(boolean ovlpanel) {
        this.ovlpanel = ovlpanel;
    }

    public Integer getVM_CHALLANBOOKEMPCODE() {
        return VM_CHALLANBOOKEMPCODE;
    }

    public void setVM_CHALLANBOOKEMPCODE(Integer VM_CHALLANBOOKEMPCODE) {
        this.VM_CHALLANBOOKEMPCODE = VM_CHALLANBOOKEMPCODE;
    }

    public String getVM_CHALLANBOOKNO() {
        return VM_CHALLANBOOKNO;
    }

    public void setVM_CHALLANBOOKNO(String VM_CHALLANBOOKNO) {
        this.VM_CHALLANBOOKNO = VM_CHALLANBOOKNO;
    }

    public Integer getVM_CHALLANBOOK_NOFROM() {
        return VM_CHALLANBOOK_NOFROM;
    }

    public void setVM_CHALLANBOOK_NOFROM(Integer VM_CHALLANBOOK_NOFROM) {
        this.VM_CHALLANBOOK_NOFROM = VM_CHALLANBOOK_NOFROM;
    }

    public Integer getVM_CHALLANBOOK_NOUPTO() {
        return VM_CHALLANBOOK_NOUPTO;
    }

    public void setVM_CHALLANBOOK_NOUPTO(Integer VM_CHALLANBOOK_NOUPTO) {
        this.VM_CHALLANBOOK_NOUPTO = VM_CHALLANBOOK_NOUPTO;
    }

    public Integer getVM_CHALLANBOOK_NOCURRENT() {
        return VM_CHALLANBOOK_NOCURRENT;
    }

    public void setVM_CHALLANBOOK_NOCURRENT(Integer VM_CHALLANBOOK_NOCURRENT) {
        this.VM_CHALLANBOOK_NOCURRENT = VM_CHALLANBOOK_NOCURRENT;
    }

    public String getVM_CHALLANBOOK_EXPIRED() {
        return VM_CHALLANBOOK_EXPIRED;
    }

    public void setVM_CHALLANBOOK_EXPIRED(String VM_CHALLANBOOK_EXPIRED) {
        this.VM_CHALLANBOOK_EXPIRED = VM_CHALLANBOOK_EXPIRED;
    }

    public String getVM_CHALLANBOOK_ISSBY() {
        return VM_CHALLANBOOK_ISSBY;
    }

    public void setVM_CHALLANBOOK_ISSBY(String VM_CHALLANBOOK_ISSBY) {
        this.VM_CHALLANBOOK_ISSBY = VM_CHALLANBOOK_ISSBY;
    }

    public Integer getVM_CHALLANBOOK_OFFICECODE() {
        return VM_CHALLANBOOK_OFFICECODE;
    }

    public void setVM_CHALLANBOOK_OFFICECODE(Integer VM_CHALLANBOOK_OFFICECODE) {
        this.VM_CHALLANBOOK_OFFICECODE = VM_CHALLANBOOK_OFFICECODE;
    }

    public Integer getVM_COURT_CODE() {
        return VM_COURT_CODE;
    }

    public void setVM_COURT_CODE(Integer VM_COURT_CODE) {
        this.VM_COURT_CODE = VM_COURT_CODE;
    }

    public String getVM_COURT_NAME() {
        return VM_COURT_NAME;
    }

    public void setVM_COURT_NAME(String VM_COURT_NAME) {
        this.VM_COURT_NAME = VM_COURT_NAME;
    }

    public Integer getVM_DA_CODE() {
        return VM_DA_CODE;
    }

    public void setVM_DA_CODE(Integer VM_DA_CODE) {
        this.VM_DA_CODE = VM_DA_CODE;
    }

    public String getVM_DA_DESCR() {
        return VM_DA_DESCR;
    }

    public void setVM_DA_DESCR(String VM_DA_DESCR) {
        this.VM_DA_DESCR = VM_DA_DESCR;
    }

    public String getVM_DA_OFFNCE_CD() {
        return VM_DA_OFFNCE_CD;
    }

    public void setVM_DA_OFFNCE_CD(String VM_DA_OFFNCE_CD) {
        this.VM_DA_OFFNCE_CD = VM_DA_OFFNCE_CD;
    }

    public String getVM_DA_SCHEDULE() {
        return VM_DA_SCHEDULE;
    }

    public void setVM_DA_SCHEDULE(String VM_DA_SCHEDULE) {
        this.VM_DA_SCHEDULE = VM_DA_SCHEDULE;
    }

    public String getVM_DA_SUB_SCHEDULE() {
        return VM_DA_SUB_SCHEDULE;
    }

    public void setVM_DA_SUB_SCHEDULE(String VM_DA_SUB_SCHEDULE) {
        this.VM_DA_SUB_SCHEDULE = VM_DA_SUB_SCHEDULE;
    }

    public int getVM_DA_PENALTY_SRNO() {
        return VM_DA_PENALTY_SRNO;
    }

    public void setVM_DA_PENALTY_SRNO(int VM_DA_PENALTY_SRNO) {
        this.VM_DA_PENALTY_SRNO = VM_DA_PENALTY_SRNO;
    }

    public String getVM_DA_PENALTY_DACODE() {
        return VM_DA_PENALTY_DACODE;
    }

    public void setVM_DA_PENALTY_DACODE(String VM_DA_PENALTY_DACODE) {
        this.VM_DA_PENALTY_DACODE = VM_DA_PENALTY_DACODE;
    }

    public String getVM_DA_PENALTY_VHCLASS() {
        return VM_DA_PENALTY_VHCLASS;
    }

    public void setVM_DA_PENALTY_VHCLASS(String VM_DA_PENALTY_VHCLASS) {
        this.VM_DA_PENALTY_VHCLASS = VM_DA_PENALTY_VHCLASS;
    }

    public Integer getVM_DA_PENALTY_OPENALTY() {
        return VM_DA_PENALTY_OPENALTY;
    }

    public void setVM_DA_PENALTY_OPENALTY(Integer VM_DA_PENALTY_OPENALTY) {
        this.VM_DA_PENALTY_OPENALTY = VM_DA_PENALTY_OPENALTY;
    }

    public Integer getVM_DA_PENALTY_DPENALTY() {
        return VM_DA_PENALTY_DPENALTY;
    }

    public void setVM_DA_PENALTY_DPENALTY(Integer VM_DA_PENALTY_DPENALTY) {
        this.VM_DA_PENALTY_DPENALTY = VM_DA_PENALTY_DPENALTY;
    }

    public Integer getVM_DA_PENALTY_CPENALTY() {
        return VM_DA_PENALTY_CPENALTY;
    }

    public void setVM_DA_PENALTY_CPENALTY(Integer VM_DA_PENALTY_CPENALTY) {
        this.VM_DA_PENALTY_CPENALTY = VM_DA_PENALTY_CPENALTY;
    }

    public Integer getVM_DA_PENALTY_SUSDAYS() {
        return VM_DA_PENALTY_SUSDAYS;
    }

    public void setVM_DA_PENALTY_SUSDAYS(Integer VM_DA_PENALTY_SUSDAYS) {
        this.VM_DA_PENALTY_SUSDAYS = VM_DA_PENALTY_SUSDAYS;
    }

    public Integer getVM_DA_PENALTY_OPENALTY1() {
        return VM_DA_PENALTY_OPENALTY1;
    }

    public void setVM_DA_PENALTY_OPENALTY1(Integer VM_DA_PENALTY_OPENALTY1) {
        this.VM_DA_PENALTY_OPENALTY1 = VM_DA_PENALTY_OPENALTY1;
    }

    public Integer getVM_DA_PENALTY_DPENALTY1() {
        return VM_DA_PENALTY_DPENALTY1;
    }

    public void setVM_DA_PENALTY_DPENALTY1(Integer VM_DA_PENALTY_DPENALTY1) {
        this.VM_DA_PENALTY_DPENALTY1 = VM_DA_PENALTY_DPENALTY1;
    }

    public Integer getVM_DA_PENALTY_CPENALTY1() {
        return VM_DA_PENALTY_CPENALTY1;
    }

    public void setVM_DA_PENALTY_CPENALTY1(Integer VM_DA_PENALTY_CPENALTY1) {
        this.VM_DA_PENALTY_CPENALTY1 = VM_DA_PENALTY_CPENALTY1;
    }

    public Integer getVM_DA_PENALTY_SUSDAYS1() {
        return VM_DA_PENALTY_SUSDAYS1;
    }

    public void setVM_DA_PENALTY_SUSDAYS1(Integer VM_DA_PENALTY_SUSDAYS1) {
        this.VM_DA_PENALTY_SUSDAYS1 = VM_DA_PENALTY_SUSDAYS1;
    }

    public Integer getVM_DA_PENALTY_OPENALTY2() {
        return VM_DA_PENALTY_OPENALTY2;
    }

    public void setVM_DA_PENALTY_OPENALTY2(Integer VM_DA_PENALTY_OPENALTY2) {
        this.VM_DA_PENALTY_OPENALTY2 = VM_DA_PENALTY_OPENALTY2;
    }

    public Integer getVM_DA_PENALTY_DPENALTY2() {
        return VM_DA_PENALTY_DPENALTY2;
    }

    public void setVM_DA_PENALTY_DPENALTY2(Integer VM_DA_PENALTY_DPENALTY2) {
        this.VM_DA_PENALTY_DPENALTY2 = VM_DA_PENALTY_DPENALTY2;
    }

    public Integer getVM_DA_PENALTY_CPENALTY2() {
        return VM_DA_PENALTY_CPENALTY2;
    }

    public void setVM_DA_PENALTY_CPENALTY2(Integer VM_DA_PENALTY_CPENALTY2) {
        this.VM_DA_PENALTY_CPENALTY2 = VM_DA_PENALTY_CPENALTY2;
    }

    public Integer getVM_DA_PENALTY_SUSDAYS2() {
        return VM_DA_PENALTY_SUSDAYS2;
    }

    public void setVM_DA_PENALTY_SUSDAYS2(Integer VM_DA_PENALTY_SUSDAYS2) {
        this.VM_DA_PENALTY_SUSDAYS2 = VM_DA_PENALTY_SUSDAYS2;
    }

    public String getVM_DA_PENALTY_SUSACTION() {
        return VM_DA_PENALTY_SUSACTION;
    }

    public void setVM_DA_PENALTY_SUSACTION(String VM_DA_PENALTY_SUSACTION) {
        this.VM_DA_PENALTY_SUSACTION = VM_DA_PENALTY_SUSACTION;
    }

    public int getVM_EX_SCHEDULE_SRNO() {
        return VM_EX_SCHEDULE_SRNO;
    }

    public void setVM_EX_SCHEDULE_SRNO(int VM_EX_SCHEDULE_SRNO) {
        this.VM_EX_SCHEDULE_SRNO = VM_EX_SCHEDULE_SRNO;
    }

    public String getVM_EX_SCHEDULE_VHCLASS() {
        return VM_EX_SCHEDULE_VHCLASS;
    }

    public void setVM_EX_SCHEDULE_VHCLASS(String VM_EX_SCHEDULE_VHCLASS) {
        this.VM_EX_SCHEDULE_VHCLASS = VM_EX_SCHEDULE_VHCLASS;
    }

    public Integer getVM_EX_SCHEDULE_FLATRATE() {
        return VM_EX_SCHEDULE_FLATRATE;
    }

    public void setVM_EX_SCHEDULE_FLATRATE(Integer VM_EX_SCHEDULE_FLATRATE) {
        this.VM_EX_SCHEDULE_FLATRATE = VM_EX_SCHEDULE_FLATRATE;
    }

    public Integer getVM_EX_SCHEDULE_UNITRATE() {
        return VM_EX_SCHEDULE_UNITRATE;
    }

    public void setVM_EX_SCHEDULE_UNITRATE(Integer VM_EX_SCHEDULE_UNITRATE) {
        this.VM_EX_SCHEDULE_UNITRATE = VM_EX_SCHEDULE_UNITRATE;
    }

    public Integer getVM_OFFENCE_CODE() {
        return VM_OFFENCE_CODE;
    }

    public void setVM_OFFENCE_CODE(Integer VM_OFFENCE_CODE) {
        this.VM_OFFENCE_CODE = VM_OFFENCE_CODE;
    }

    public String getVM_OFFENCE_DESC() {
        return VM_OFFENCE_DESC;
    }

    public void setVM_OFFENCE_DESC(String VM_OFFENCE_DESC) {
        this.VM_OFFENCE_DESC = VM_OFFENCE_DESC;
    }

    public String getVM_OFFENCE_MVACLAUSE() {
        return VM_OFFENCE_MVACLAUSE;
    }

    public void setVM_OFFENCE_MVACLAUSE(String VM_OFFENCE_MVACLAUSE) {
        this.VM_OFFENCE_MVACLAUSE = VM_OFFENCE_MVACLAUSE;
    }

    public String getVM_OFFENCE_CMVRCLAUSE() {
        return VM_OFFENCE_CMVRCLAUSE;
    }

    public void setVM_OFFENCE_CMVRCLAUSE(String VM_OFFENCE_CMVRCLAUSE) {
        this.VM_OFFENCE_CMVRCLAUSE = VM_OFFENCE_CMVRCLAUSE;
    }

    public String getVM_OFFENCE_SMVRCLAUSE() {
        return VM_OFFENCE_SMVRCLAUSE;
    }

    public void setVM_OFFENCE_SMVRCLAUSE(String VM_OFFENCE_SMVRCLAUSE) {
        this.VM_OFFENCE_SMVRCLAUSE = VM_OFFENCE_SMVRCLAUSE;
    }

    public String getVM_OFFENCE_ADDLDESC() {
        return VM_OFFENCE_ADDLDESC;
    }

    public void setVM_OFFENCE_ADDLDESC(String VM_OFFENCE_ADDLDESC) {
        this.VM_OFFENCE_ADDLDESC = VM_OFFENCE_ADDLDESC;
    }

    public String getVM_OFFENCE_PUNISHMENT1() {
        return VM_OFFENCE_PUNISHMENT1;
    }

    public void setVM_OFFENCE_PUNISHMENT1(String VM_OFFENCE_PUNISHMENT1) {
        this.VM_OFFENCE_PUNISHMENT1 = VM_OFFENCE_PUNISHMENT1;
    }

    public String getVM_OFFENCE_PUNISHMENT2() {
        return VM_OFFENCE_PUNISHMENT2;
    }

    public void setVM_OFFENCE_PUNISHMENT2(String VM_OFFENCE_PUNISHMENT2) {
        this.VM_OFFENCE_PUNISHMENT2 = VM_OFFENCE_PUNISHMENT2;
    }

    public String getVM_OFFENCE_ISACT_TPT() {
        return VM_OFFENCE_ISACT_TPT;
    }

    public void setVM_OFFENCE_ISACT_TPT(String VM_OFFENCE_ISACT_TPT) {
        this.VM_OFFENCE_ISACT_TPT = VM_OFFENCE_ISACT_TPT;
    }

    public String getVM_OFFENCE_ISACT_POLICE() {
        return VM_OFFENCE_ISACT_POLICE;
    }

    public void setVM_OFFENCE_ISACT_POLICE(String VM_OFFENCE_ISACT_POLICE) {
        this.VM_OFFENCE_ISACT_POLICE = VM_OFFENCE_ISACT_POLICE;
    }

    public int getVM_OFF_PENALTY_SRNO() {
        return VM_OFF_PENALTY_SRNO;
    }

    public void setVM_OFF_PENALTY_SRNO(int VM_OFF_PENALTY_SRNO) {
        this.VM_OFF_PENALTY_SRNO = VM_OFF_PENALTY_SRNO;
    }

    public String getVM_OFF_PENALTY_CODE() {
        return VM_OFF_PENALTY_CODE;
    }

    public void setVM_OFF_PENALTY_CODE(String VM_OFF_PENALTY_CODE) {
        this.VM_OFF_PENALTY_CODE = VM_OFF_PENALTY_CODE;
    }

    public String getVM_OFF_PENALTY_VHCLASS() {
        return VM_OFF_PENALTY_VHCLASS;
    }

    public void setVM_OFF_PENALTY_VHCLASS(String VM_OFF_PENALTY_VHCLASS) {
        this.VM_OFF_PENALTY_VHCLASS = VM_OFF_PENALTY_VHCLASS;
    }

    public String getVM_OFF_PENALTY_SECTIONCODE() {
        return VM_OFF_PENALTY_SECTIONCODE;
    }

    public void setVM_OFF_PENALTY_SECTIONCODE(String VM_OFF_PENALTY_SECTIONCODE) {
        this.VM_OFF_PENALTY_SECTIONCODE = VM_OFF_PENALTY_SECTIONCODE;
    }

    public Integer getVM_OFF_PENALTY_OPENALTY() {
        return VM_OFF_PENALTY_OPENALTY;
    }

    public void setVM_OFF_PENALTY_OPENALTY(Integer VM_OFF_PENALTY_OPENALTY) {
        this.VM_OFF_PENALTY_OPENALTY = VM_OFF_PENALTY_OPENALTY;
    }

    public Integer getVM_OFF_PENALTY_DPENALTY() {
        return VM_OFF_PENALTY_DPENALTY;
    }

    public void setVM_OFF_PENALTY_DPENALTY(Integer VM_OFF_PENALTY_DPENALTY) {
        this.VM_OFF_PENALTY_DPENALTY = VM_OFF_PENALTY_DPENALTY;
    }

    public Integer getVM_OFF_PENALTY_CPENALTY() {
        return VM_OFF_PENALTY_CPENALTY;
    }

    public void setVM_OFF_PENALTY_CPENALTY(Integer VM_OFF_PENALTY_CPENALTY) {
        this.VM_OFF_PENALTY_CPENALTY = VM_OFF_PENALTY_CPENALTY;
    }

    public Integer getVM_OFF_PENALTY_OPENALTY1() {
        return VM_OFF_PENALTY_OPENALTY1;
    }

    public void setVM_OFF_PENALTY_OPENALTY1(Integer VM_OFF_PENALTY_OPENALTY1) {
        this.VM_OFF_PENALTY_OPENALTY1 = VM_OFF_PENALTY_OPENALTY1;
    }

    public Integer getVM_OFF_PENALTY_DPENALTY1() {
        return VM_OFF_PENALTY_DPENALTY1;
    }

    public void setVM_OFF_PENALTY_DPENALTY1(Integer VM_OFF_PENALTY_DPENALTY1) {
        this.VM_OFF_PENALTY_DPENALTY1 = VM_OFF_PENALTY_DPENALTY1;
    }

    public Integer getVM_OFF_PENALTY_CPENALTY1() {
        return VM_OFF_PENALTY_CPENALTY1;
    }

    public void setVM_OFF_PENALTY_CPENALTY1(Integer VM_OFF_PENALTY_CPENALTY1) {
        this.VM_OFF_PENALTY_CPENALTY1 = VM_OFF_PENALTY_CPENALTY1;
    }

    public Integer getVM_OFF_PENALTY_OPENALTY2() {
        return VM_OFF_PENALTY_OPENALTY2;
    }

    public void setVM_OFF_PENALTY_OPENALTY2(Integer VM_OFF_PENALTY_OPENALTY2) {
        this.VM_OFF_PENALTY_OPENALTY2 = VM_OFF_PENALTY_OPENALTY2;
    }

    public Integer getVM_OFF_PENALTY_DPENALTY2() {
        return VM_OFF_PENALTY_DPENALTY2;
    }

    public void setVM_OFF_PENALTY_DPENALTY2(Integer VM_OFF_PENALTY_DPENALTY2) {
        this.VM_OFF_PENALTY_DPENALTY2 = VM_OFF_PENALTY_DPENALTY2;
    }

    public Integer getVM_OFF_PENALTY_CPENALTY2() {
        return VM_OFF_PENALTY_CPENALTY2;
    }

    public void setVM_OFF_PENALTY_CPENALTY2(Integer VM_OFF_PENALTY_CPENALTY2) {
        this.VM_OFF_PENALTY_CPENALTY2 = VM_OFF_PENALTY_CPENALTY2;
    }

    public int getVM_OVLD_SCHEDULE_SRNO() {
        return VM_OVLD_SCHEDULE_SRNO;
    }

    public void setVM_OVLD_SCHEDULE_SRNO(int VM_OVLD_SCHEDULE_SRNO) {
        this.VM_OVLD_SCHEDULE_SRNO = VM_OVLD_SCHEDULE_SRNO;
    }

    public Integer getVM_OVLD_SCHEDULE_LDWTLOWER() {
        return VM_OVLD_SCHEDULE_LDWTLOWER;
    }

    public void setVM_OVLD_SCHEDULE_LDWTLOWER(Integer VM_OVLD_SCHEDULE_LDWTLOWER) {
        this.VM_OVLD_SCHEDULE_LDWTLOWER = VM_OVLD_SCHEDULE_LDWTLOWER;
    }

    public Integer getVM_OVLD_SCHEDULE_LDWTUPPER() {
        return VM_OVLD_SCHEDULE_LDWTUPPER;
    }

    public void setVM_OVLD_SCHEDULE_LDWTUPPER(Integer VM_OVLD_SCHEDULE_LDWTUPPER) {
        this.VM_OVLD_SCHEDULE_LDWTUPPER = VM_OVLD_SCHEDULE_LDWTUPPER;
    }

    public Integer getVM_OVLD_SCHEDULE_LDWTUNIT() {
        return VM_OVLD_SCHEDULE_LDWTUNIT;
    }

    public void setVM_OVLD_SCHEDULE_LDWTUNIT(Integer VM_OVLD_SCHEDULE_LDWTUNIT) {
        this.VM_OVLD_SCHEDULE_LDWTUNIT = VM_OVLD_SCHEDULE_LDWTUNIT;
    }

    public Integer getVM_OVLD_SCHEDULE_FLATCFAMT() {
        return VM_OVLD_SCHEDULE_FLATCFAMT;
    }

    public void setVM_OVLD_SCHEDULE_FLATCFAMT(Integer VM_OVLD_SCHEDULE_FLATCFAMT) {
        this.VM_OVLD_SCHEDULE_FLATCFAMT = VM_OVLD_SCHEDULE_FLATCFAMT;
    }

    public Integer getVM_OVLD_SCHEDULE_UNITCFAMT() {
        return VM_OVLD_SCHEDULE_UNITCFAMT;
    }

    public void setVM_OVLD_SCHEDULE_UNITCFAMT(Integer VM_OVLD_SCHEDULE_UNITCFAMT) {
        this.VM_OVLD_SCHEDULE_UNITCFAMT = VM_OVLD_SCHEDULE_UNITCFAMT;
    }

    public Integer getVM_OVLD_SCHEDULE_WTUNITVAL() {
        return VM_OVLD_SCHEDULE_WTUNITVAL;
    }

    public void setVM_OVLD_SCHEDULE_WTUNITVAL(Integer VM_OVLD_SCHEDULE_WTUNITVAL) {
        this.VM_OVLD_SCHEDULE_WTUNITVAL = VM_OVLD_SCHEDULE_WTUNITVAL;
    }

    public Integer getVM_SECTION_CODE() {
        return VM_SECTION_CODE;
    }

    public void setVM_SECTION_CODE(Integer VM_SECTION_CODE) {
        this.VM_SECTION_CODE = VM_SECTION_CODE;
    }

    public String getVM_SECTION_LEVELFLAG() {
        return VM_SECTION_LEVELFLAG;
    }

    public void setVM_SECTION_LEVELFLAG(String VM_SECTION_LEVELFLAG) {
        this.VM_SECTION_LEVELFLAG = VM_SECTION_LEVELFLAG;
    }

    public String getVM_SECTION_NAME() {
        return VM_SECTION_NAME;
    }

    public void setVM_SECTION_NAME(String VM_SECTION_NAME) {
        this.VM_SECTION_NAME = VM_SECTION_NAME;
    }

    public String getVM_SECTION_ISACTIVE() {
        return VM_SECTION_ISACTIVE;
    }

    public void setVM_SECTION_ISACTIVE(String VM_SECTION_ISACTIVE) {
        this.VM_SECTION_ISACTIVE = VM_SECTION_ISACTIVE;
    }

    public List getUserCodeList() {
        return userCodeList;
    }

    public void setUserCodeList(List userCodeList) {
        this.userCodeList = userCodeList;
    }

    public String getSection_name() {
        return section_name;
    }

    public void setSection_name(String section_name) {
        this.section_name = section_name;
    }

    /**
     * @return the officeList
     */
    public List getOfficeList() {
        return officeList;
    }

    /**
     * @param officeList the officeList to set
     */
    public void setOfficeList(List officeList) {
        this.officeList = officeList;
    }

    /**
     * @return the SELECT_VM_OFF_PENALTY_VHCLASS
     */
    public List getSELECT_VM_OFF_PENALTY_VHCLASS() {
        return SELECT_VM_OFF_PENALTY_VHCLASS;
    }

    /**
     * @param SELECT_VM_OFF_PENALTY_VHCLASS the SELECT_VM_OFF_PENALTY_VHCLASS to
     * set
     */
    public void setSELECT_VM_OFF_PENALTY_VHCLASS(List SELECT_VM_OFF_PENALTY_VHCLASS) {
        this.SELECT_VM_OFF_PENALTY_VHCLASS = SELECT_VM_OFF_PENALTY_VHCLASS;
    }

    /**
     * @return the SELECTED_ACCUESD
     */
    public List getSELECTED_ACCUESD() {
        return SELECTED_ACCUESD;
    }

    /**
     * @param SELECTED_ACCUESD the SELECTED_ACCUESD to set
     */
    public void setSELECTED_ACCUESD(List SELECTED_ACCUESD) {
        this.SELECTED_ACCUESD = SELECTED_ACCUESD;
    }

    /**
     * @return the accuesdList
     */
    public Map<String, Object> getAccuesdList() {
        return accuesdList;
    }

    /**
     * @param accuesdList the accuesdList to set
     */
    public void setAccuesdList(Map<String, Object> accuesdList) {
        this.accuesdList = accuesdList;
    }

    /**
     * @param sectionList the sectionList to set
     */
    public void setSectionList(List sectionList) {
        this.sectionList = sectionList;
    }

    /**
     * @return the sectionList
     */
    public List getSectionList() {
        return sectionList;
    }
}
