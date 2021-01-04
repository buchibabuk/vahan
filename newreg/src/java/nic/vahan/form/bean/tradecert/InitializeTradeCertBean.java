package nic.vahan.form.bean.tradecert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import nic.rto.vahan.common.VahanException;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.form.dobj.tradecert.InitializeTradeCertDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.tradecert.InitializeTradeCertImpl;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "initializeTC")
@ViewScoped
public class InitializeTradeCertBean implements Serializable {

    private static final String MODULE_PREFIX_PATTERN;
    private static final String SELECT;
    private final List<SelectItem> applicantTypeList;
    private final Map<String, String> applicantTypeMap;
    private InitializeTradeCertDobj initializeTradeCertDobj;
    private boolean renderTCSeriesUpdatePanel;
    private boolean renderTCSeriesEntryPanel;
    private boolean validationNotSuccessful;
    private String prefixSeriesPart1;
    private String nextSeriesSeqPattern;
    private String nextSeriesPart2;

    static {
        MODULE_PREFIX_PATTERN = "[A-Z]{2}";
        SELECT = "-SELECT-";
    }

    public InitializeTradeCertBean() {
        initializeTradeCertDobj = new InitializeTradeCertDobj();
        applicantTypeMap = new HashMap();
        applicantTypeList = new ArrayList<>();

    }

    @PostConstruct
    public void init() {
        try {
            getInitializeTradeCertDobj().setStateCd(Util.getUserStateCode());
            getInitializeTradeCertDobj().setOffCd(Util.getUserOffCode());
            getInitializeTradeCertDobj().setUserCd(Util.getUserName());
            InitializeTradeCertImpl.fillApplicantTypeMap(getApplicantTypeMap());
            String offCdString = getInitializeTradeCertDobj().getOffCd().toString().trim();
            if (offCdString.length() == 1) {
                offCdString = "0" + offCdString;
            }
            setPrefixSeriesPart1(getInitializeTradeCertDobj().getStateCd().trim() + offCdString);
            setValidationNotSuccessful(true);
            setRenderTCSeriesEntryPanel(false);
            setRenderTCSeriesUpdatePanel(false);
            setTypesInApplicantList();
        } catch (VahanException ve) {
            JSFUtils.setFacesMessage(ve.getMessage(), null, JSFUtils.ERROR);
        }
    }

    /*
     * Method to save new Trade Certificate Series
     */
    public void addTCSeries() {
        if (getInitializeTradeCertDobj().getSequenceNo() == null) {
            JSFUtils.setFacesMessage("Please Enter Valid Initialization Number For Trade Certificate", null, JSFUtils.ERROR);
            return;
        }
        try {
            boolean isSaved = InitializeTradeCertImpl.saveTCInitializationNo(getInitializeTradeCertDobj());
            if (isSaved) {
                String applTypeDesc = getApplicantTypeMap().get(getInitializeTradeCertDobj().getApplicantType()).toString();
                setRenderTCSeriesEntryPanel(false);
                setRenderTCSeriesUpdatePanel(false);
                String sequence_no_string = setTCSequenceNoToShow(getInitializeTradeCertDobj().getSequenceNo() + 1);
                JSFUtils.showMessagesInDialog("Successful", "Trade Certificate Series For " + applTypeDesc + " Will Be Represented As " + getPrefixSeriesPart1() + getInitializeTradeCertDobj().getModulePrefix() + sequence_no_string, FacesMessage.SEVERITY_INFO);
                getInitializeTradeCertDobj().setApplicantType(SELECT);
                getInitializeTradeCertDobj().setSequenceNo(null);
                getInitializeTradeCertDobj().setModulePrefix(null);
            }
        } catch (VahanException ve) {
            JSFUtils.setFacesMessage("Error In Initializing Sequence For Trade Certificate, Please Contact System Administrator", null, JSFUtils.ERROR);
        }
    }

    /*
     * Method to modify applicant list according to state
     */
    private void setTypesInApplicantList() throws VahanException {
        List<String> applicantTypeForState = new ArrayList<>();
        InitializeTradeCertImpl.fillRequiredApplicantsForState(applicantTypeForState);
        outer:
        for (String applicantTypeCode : applicantTypeForState) {
            for (Object applicantTypeKey : getApplicantTypeMap().keySet()) {
                if ((applicantTypeKey.toString().equals(applicantTypeCode.toString()))) {
                    getApplicantTypeList().add(new SelectItem(applicantTypeKey.toString(), getApplicantTypeMap().get(applicantTypeKey).toString()));
                    continue outer;
                }
            }
        }
    }

    /*
     * Event method on applicant selection to check if Trade Certificate Series already exists
     */
    public void selectionOfApplicantType(AjaxBehaviorEvent event) {
        getInitializeTradeCertDobj().setModulePrefix(null);
        getInitializeTradeCertDobj().setSequenceNo(null);
        if (getInitializeTradeCertDobj().getApplicantType().equalsIgnoreCase(SELECT)) {
            setRenderTCSeriesEntryPanel(false);
            setRenderTCSeriesUpdatePanel(false);
            JSFUtils.setFacesMessage("Please select applicant type to initialize Trade Certificate series", null, JSFUtils.ERROR);
        } else {
            try {
                InitializeTradeCertImpl impl = new InitializeTradeCertImpl();
                boolean seriesExist = impl.checkIfTCSeriesExist(getInitializeTradeCertDobj());
                if (seriesExist) {
                    setRenderTCSeriesEntryPanel(false);
                    setRenderTCSeriesUpdatePanel(true);
                } else {
                    setRenderTCSeriesEntryPanel(true);
                    setRenderTCSeriesUpdatePanel(false);
                }
            } catch (VahanException ve) {
                JSFUtils.setFacesMessage(ve.getMessage(), null, JSFUtils.ERROR);
            }
        }
    }

    /*
     * Event Method to set the sequence for next Trade Certificate
     */
    public void nextSeriesAddRunningNo(AjaxBehaviorEvent ae) {
        setNextSeriesSeqPattern(setTCSequenceNoToShow(getInitializeTradeCertDobj().getSequenceNo() + 1));
    }

    /*
     * Method to validate modulePrefix and sequenceNo before saving series
     */
    public void validateAndShowConfirmBox() {
        this.validationNotSuccessful = true;
        if (getInitializeTradeCertDobj().getModulePrefix() != null || !getInitializeTradeCertDobj().getModulePrefix().isEmpty()) {
            if (Pattern.compile(MODULE_PREFIX_PATTERN).matcher(getInitializeTradeCertDobj().getModulePrefix()).matches()) {
                setValidationNotSuccessful(false);
            } else {
                setValidationNotSuccessful(true);
                JSFUtils.setFacesMessage("Prefix Series Part-2 Field Allows Only Two Letters From [A-Z]", null, JSFUtils.ERROR);
                return;
            }
        } else {
            setValidationNotSuccessful(true);
            JSFUtils.setFacesMessage("Blank! Prefix Series Part-2", null, JSFUtils.ERROR);
            return;
        }

        if (getInitializeTradeCertDobj().getSequenceNo() == null) {
            setValidationNotSuccessful(true);
            JSFUtils.setFacesMessage("Blank! Running No.", null, JSFUtils.ERROR);
            return;
        }
        if (!validationNotSuccessful) {
            PrimeFaces.current().executeScript("PF('confirmationPopup').show();");
        }
    }


    /*
     * Method to represesnt given number  in TradeCertificate Series 'Sequence Part' Format
     */
    private String setTCSequenceNoToShow(int sequenceNo) {
        String sequence_no_string = String.valueOf(sequenceNo);
        int sequence_no_length = sequence_no_string.length();
        switch (sequence_no_length) {
            case 1:
                sequence_no_string = "00" + sequence_no_string;
                break;
            case 2:
                sequence_no_string = "0" + sequence_no_string;
                break;
        }
        return sequence_no_string;
    }

    public List<SelectItem> getApplicantTypeList() {
        return applicantTypeList;
    }

    public Map getApplicantTypeMap() {
        return applicantTypeMap;
    }

    public boolean isRenderTCSeriesUpdatePanel() {
        return renderTCSeriesUpdatePanel;
    }

    public void setRenderTCSeriesUpdatePanel(boolean renderTCSeriesUpdatePanel) {
        this.renderTCSeriesUpdatePanel = renderTCSeriesUpdatePanel;
    }

    public boolean isRenderTCSeriesEntryPanel() {
        return renderTCSeriesEntryPanel;
    }

    public void setRenderTCSeriesEntryPanel(boolean renderTCSeriesEntryPanel) {
        this.renderTCSeriesEntryPanel = renderTCSeriesEntryPanel;
    }

    public String getPrefixSeriesPart1() {
        return prefixSeriesPart1;
    }

    public void setPrefixSeriesPart1(String prefixSeriesPart1) {
        this.prefixSeriesPart1 = prefixSeriesPart1;
    }

    public InitializeTradeCertDobj getInitializeTradeCertDobj() {
        return initializeTradeCertDobj;
    }

    public void setInitializeTradeCertDobj(InitializeTradeCertDobj initializeTradeCertDobj) {
        this.initializeTradeCertDobj = initializeTradeCertDobj;
    }

    public boolean isValidationNotSuccessful() {
        return validationNotSuccessful;
    }

    public void setValidationNotSuccessful(boolean validationNotSuccessful) {
        this.validationNotSuccessful = validationNotSuccessful;
    }

    public String getNextSeriesSeqPattern() {
        return nextSeriesSeqPattern;
    }

    public void setNextSeriesSeqPattern(String nextSeriesSeqPattern) {
        this.nextSeriesSeqPattern = nextSeriesSeqPattern;
    }

    public String getNextSeriesPart2() {
        return nextSeriesPart2;
    }

    public void setNextSeriesPart2(String nextSeriesPart2) {
        this.nextSeriesPart2 = nextSeriesPart2;
    }
}
