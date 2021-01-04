package nic.vahan.form.bean.permit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import nic.vahan.CommonUtils.FormulaUtils;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.form.dobj.permit.Fees_Slab_dobj;
import nic.vahan.form.impl.permit.MasterPermitTableImpl;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 *
 * @author MRD
 */
@ManagedBean(name = "fees_Slab_bean")
@ViewScoped
public class Fees_Slab_bean implements Serializable {

    private String purpose_code = "";
    private String descrPurpose = "";
    private List purposeLabelValue = new ArrayList();
    private MasterPermitTableImpl master_impl = new MasterPermitTableImpl();
    private List<Fees_Slab_dobj> fees_slab_list;
    private List fees_slab_FilteredHlist;
    private List<Fees_Slab_dobj> fees_slab_FilteredList;
    private List<Map.Entry<String, String>> hintList = new ArrayList();
    private List periodModeList = new ArrayList();
    private boolean feesSlabRendered;
    private boolean maxAmountDisabled;
    private boolean summary;
    private String fees_slab_header = "";
    private Fees_Slab_dobj fsbd = null;
    private String amount_Fine_txtBox = "";
    private String select_Amount_Fine = "";
    private String input_saveButton_textBox = "";
    private String saveButton = "";
    private boolean dialogPanel = false;
    private static final Logger logger = Logger.getLogger(Fees_Slab_bean.class);

    public Fees_Slab_bean() {
        purposeLabelValue = master_impl.getArrayPurposeType();
        String[][] periodMode1 = MasterTableFiller.masterTables.VM_TAX_MODE.getData();
        for (int i = 0; i < periodMode1.length; i++) {
            if (periodMode1[i][0].equals("Y") || periodMode1[i][0].equals("M") || periodMode1[i][0].equals("W") || periodMode1[i][0].equals("D")) {
                periodModeList.add(new SelectItem(periodMode1[i][0], periodMode1[i][1]));
            }
        }
    }

    public void onSelectGetDetails() {
        resetDialogfields();
        if (purpose_code == null || purpose_code.equals("") || purpose_code.equals("-1")) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Alert!!", "Please select valid purpose"));

        } else {
            for (int i = 0; i < purposeLabelValue.size(); i++) {
                javax.faces.model.SelectItem obj = (javax.faces.model.SelectItem) purposeLabelValue.get(i);
                if (obj.getValue().toString().equalsIgnoreCase(purpose_code)) {
                    setDescrPurpose(obj.getLabel());
                    break;
                }
            }
            List<Fees_Slab_dobj> fees_slab_list1 = master_impl.getFeesSlabNewDetail(Integer.parseInt(purpose_code));
            setFeesSlabRendered(true);
            setFees_slab_header("Available slab for Purpose : (" + getDescrPurpose() + ") ");
            setFees_slab_list(fees_slab_list1);
            String[][] data = MasterTableFiller.masterTables.VM_TAX_SLAB_FIELDS.getData();
            Map<String, String> mp = new HashMap();
            for (int i = 0; i < data.length; i++) {
                mp.put(data[i][0], data[i][1]);
            }
            mp.put("<pDay>", "PMT_DAYS");
            mp.put("<pMonth>", "PMT_MONTHS");
            mp.put("<pCMonth>", "PMT_CEL_MONTH");
            mp.put("<pYear>", "PMT_YEAR");
            mp.put("<state_cd>", "STATE_CD");
            mp.put("<regn_no>", "REGN_NO");
            mp.put("<per_route>", "ROUTE_COUNT");
            mp.put("<per_region>", "REGION_COUNT");
            mp.put("<noc_ret>", "NOC_RETENTION");
            mp.put("<tmp_purpose>", "TMP_PURPOSE");
            mp.put("<exem_amount>", "EXEM_AMOUNT");
            mp.put("<fine_to_be_taken>", "FINE_TO_BE_TAKEN");
            mp.put("<multi_region>", "MULTI_REGION");
            mp.put("<multi_doc>", "MULTI_DOC");
            mp.put("<online_permit>", "ONLINE_PERMIT");
            setHintList(new ArrayList<Map.Entry<String, String>>(mp.entrySet()));

        }
        dialogPanel = false;
        PrimeFaces.current().ajax().update("openDlg1");
        PrimeFaces.current().ajax().update("panelDT");
    }

    public void onAddNewSlab() {
        saveButton = "AddNewSlab";
        fsbd = new Fees_Slab_dobj();
        int i = master_impl.missingSr_no(Integer.parseInt(purpose_code));
        if (i != 0) {
            fsbd.setSr_no(i);
        } else {
            fsbd.setSr_no(1);
        }
        onSelectCheckMaxAmount();
        setSelect_Amount_Fine("Amount");
        dialogPanel = true;
        PrimeFaces.current().ajax().update("openDlg1");
    }

    public void onEditSlabDetail(Fees_Slab_dobj fsb) {
        saveButton = "EditSlab";
        fsbd = fsb;
        onSelectCheckMaxAmount();
        setSelect_Amount_Fine("Amount");
        onSelect_Amount_Fine();
        dialogPanel = true;
    }

    public void onSelect_Amount_Fine() {
        switch (getSelect_Amount_Fine()) {
            case "Amount":
                setAmount_Fine_txtBox(fsbd.getFee_rate_formula());
                setInput_saveButton_textBox(FormulaUtils.makeIfCondition("Amount", fsbd.getCondition_formula(), getAmount_Fine_txtBox()) + FormulaUtils.makeIfCondition(" and Fine", "", fsbd.getFine_rate_formula()));
                break;
            case "Fine":
                setAmount_Fine_txtBox(fsbd.getFine_rate_formula());
                setInput_saveButton_textBox(FormulaUtils.makeIfCondition("Amount", fsbd.getCondition_formula(), fsbd.getFee_rate_formula()) + FormulaUtils.makeIfCondition(" and Fine", "", getAmount_Fine_txtBox()));
                break;
        }
        if (!getInput_saveButton_textBox().isEmpty() && getInput_saveButton_textBox() != null) {
            summary = true;
        }
    }

    public void onSelectCheckMaxAmount() {
        switch (fsbd.getCheck_max_amt()) {
            case "true":
                setMaxAmountDisabled(false);

                break;
            case "false":
                setMaxAmountDisabled(true);
                fsbd.setFine_max_amt("0");
                break;
        }
    }

    public void commonFunction() {
        switch (getSelect_Amount_Fine()) {
            case "Amount":
                fsbd.setFee_rate_formula(getAmount_Fine_txtBox());
                setInput_saveButton_textBox(FormulaUtils.makeIfCondition("Amount", fsbd.getCondition_formula(), fsbd.getFee_rate_formula()) + FormulaUtils.makeIfCondition(" and Fine", "", fsbd.getFine_rate_formula()));
                break;
            case "Fine":
                fsbd.setFine_rate_formula(getAmount_Fine_txtBox());
                setInput_saveButton_textBox(FormulaUtils.makeIfCondition("Amount", fsbd.getCondition_formula(), fsbd.getFee_rate_formula()) + FormulaUtils.makeIfCondition(" and Fine", "", fsbd.getFine_rate_formula()));
                break;
        }
        if (!getInput_saveButton_textBox().isEmpty() && getInput_saveButton_textBox() != null) {
            summary = true;
        }
    }

    public void closeDialog() {
        resetDialogfields();
        fees_slab_list.clear();
        setFees_slab_list(master_impl.getFeesSlabNewDetail(Integer.parseInt(purpose_code)));
        PrimeFaces.current().ajax().update("openDlg1");
        PrimeFaces.current().ajax().update("panel_fees_slab_new");
    }

    public void resetDialogfields() {
        fsbd = new Fees_Slab_dobj();
        summary = false;
        amount_Fine_txtBox = "";
        input_saveButton_textBox = "";
        dialogPanel = false;
    }

    public void onSave() {
        StringBuilder message = new StringBuilder("</br>");
        int count = 0;
        if (fsbd.getCondition_formula() == null || fsbd.getCondition_formula().equals("")) {
            message.append("</br>");
            message.append(" Please Enter Condition Formula ");
            ++count;
        }
        if (fsbd.getFee_rate_formula() == null || fsbd.getFee_rate_formula().equals("")) {
            message.append("</br>");
            message.append(" Please Enter Amount ");
            ++count;
        }
        if (fsbd.getFine_rate_formula() == null || fsbd.getFine_rate_formula().equals("")) {
            message.append("</br>");
            message.append(" Please Enter Fine ");
            ++count;
        }
        if (fsbd.getFine_max_amt() == null || fsbd.getFine_max_amt().equals("")) {
            message.append("</br>");
            message.append(" Please Enter Maximum Fine ");
            ++count;
        }
        if (count > 0) {
            PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage("Info", message.toString()));
        } else {
            master_impl.addUpdateRecords(fsbd, saveButton, purpose_code);
            if (saveButton.equalsIgnoreCase("AddNewSlab")) {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage("Message", "Data Inserted sucessfully"));
            } else {
                PrimeFaces.current().dialog().showMessageDynamic(new FacesMessage("Message", "Data updated sucessfully"));
            }
            dialogPanel = false;
            onSelectGetDetails();

        }
    }

    public void trans_des(Fees_Slab_dobj dobj) {
        for (int i = 0; i < purposeLabelValue.size(); i++) {
            javax.faces.model.SelectItem obj = (javax.faces.model.SelectItem) purposeLabelValue.get(i);
            if (obj.getValue().toString().equalsIgnoreCase(dobj.getTrans_pur_cd())) {
                dobj.setTrans_pur_descr(obj.getLabel());
                break;
            }
        }
    }

    public String getPurpose_code() {
        return purpose_code;
    }

    public void setPurpose_code(String purpose_code) {
        this.purpose_code = purpose_code;
    }

    public String getDescrPurpose() {
        return descrPurpose;
    }

    public void setDescrPurpose(String descrPurpose) {
        this.descrPurpose = descrPurpose;
    }

    public List getPurposeLabelValue() {
        return purposeLabelValue;
    }

    public void setPurposeLabelValue(List purposeLabelValue) {
        this.purposeLabelValue = purposeLabelValue;
    }

    public List<Fees_Slab_dobj> getFees_slab_list() {
        return fees_slab_list;
    }

    public void setFees_slab_list(List<Fees_Slab_dobj> fees_slab_list) {
        this.fees_slab_list = fees_slab_list;
    }

    public List<Map.Entry<String, String>> getHintList() {
        return hintList;
    }

    public void setHintList(List<Map.Entry<String, String>> hintList) {
        this.hintList = hintList;
    }

    public List getPeriodModeList() {
        return periodModeList;
    }

    public void setPeriodModeList(List periodModeList) {
        this.periodModeList = periodModeList;
    }

    public boolean isFeesSlabRendered() {
        return feesSlabRendered;
    }

    public void setFeesSlabRendered(boolean feesSlabRendered) {
        this.feesSlabRendered = feesSlabRendered;
    }

    public boolean isMaxAmountDisabled() {
        return maxAmountDisabled;
    }

    public void setMaxAmountDisabled(boolean maxAmountDisabled) {
        this.maxAmountDisabled = maxAmountDisabled;
    }

    public String getFees_slab_header() {
        return fees_slab_header;
    }

    public void setFees_slab_header(String fees_slab_header) {
        this.fees_slab_header = fees_slab_header;
    }

    public Fees_Slab_dobj getFsbd() {
        return fsbd;
    }

    public void setFsbd(Fees_Slab_dobj fsbd) {
        this.fsbd = fsbd;
    }

    public String getAmount_Fine_txtBox() {
        return amount_Fine_txtBox;
    }

    public void setAmount_Fine_txtBox(String amount_Fine_txtBox) {
        this.amount_Fine_txtBox = amount_Fine_txtBox;
    }

    public String getSelect_Amount_Fine() {
        return select_Amount_Fine;
    }

    public void setSelect_Amount_Fine(String select_Amount_Fine) {
        this.select_Amount_Fine = select_Amount_Fine;
    }

    public String getInput_saveButton_textBox() {
        return input_saveButton_textBox;
    }

    public void setInput_saveButton_textBox(String input_saveButton_textBox) {
        this.input_saveButton_textBox = input_saveButton_textBox;
    }

    public List<Fees_Slab_dobj> getFees_slab_FilteredList() {
        return fees_slab_FilteredList;
    }

    public void setFees_slab_FilteredList(List<Fees_Slab_dobj> fees_slab_FilteredList) {
        this.fees_slab_FilteredList = fees_slab_FilteredList;
    }

    public boolean isSummary() {
        return summary;
    }

    public void setSummary(boolean summary) {
        this.summary = summary;
    }

    public List getFees_slab_FilteredHlist() {
        return fees_slab_FilteredHlist;
    }

    public void setFees_slab_FilteredHlist(List fees_slab_FilteredHlist) {
        this.fees_slab_FilteredHlist = fees_slab_FilteredHlist;
    }

    public boolean isDialogPanel() {
        return dialogPanel;
    }

    public void setDialogPanel(boolean dialogPanel) {
        this.dialogPanel = dialogPanel;
    }
}
