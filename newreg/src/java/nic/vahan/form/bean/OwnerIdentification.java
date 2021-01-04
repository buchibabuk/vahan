/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import static nic.vahan.server.ServerUtil.Compare;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.form.ApproveDisapproveInterface;
import nic.vahan.form.dobj.OwnerIdentificationDobj;
import nic.vahan.form.impl.OwnerIdentificationImpl;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC106
 */
@ManagedBean(name = "owneridentification")
@ViewScoped
public class OwnerIdentification implements ApproveDisapproveInterface {

    private static final Logger LOGGER = Logger.getLogger(OwnerIdentification.class);
    private OwnerIdentificationDobj owner_identification = null;
    private OwnerIdentificationDobj prv_owner_identification = null;
    private List<ComparisonBean> compBeanList = new ArrayList<>();
    private String regn_no;
    private String mobile_no;
    private String email_id;
    private String pan_no;
    private String aadhar_no;
    private String passport_no;
    private String ration_card_no;
    private String voter_id;
    private String dl_no;
    private Date verified_on;
    private int owner_ctg;
    private String header;
    private String user_regn_no;
    private List listOwnerCatg;
    private boolean showUpdatePanel = false;

    public OwnerIdentification() {
        setHeader("UPDATE OWNER IDENTIFICATION DETAILS");
        owner_identification = new OwnerIdentificationDobj();
        listOwnerCatg = new ArrayList();
        reset();
        String[][] data = MasterTableFiller.masterTables.VM_OWCATG.getData();
        for (int i = 0; i < data.length; i++) {
            listOwnerCatg.add(new SelectItem(data[i][0], data[i][1]));
        }

    }

    public void bt_get_Details() throws CloneNotSupportedException {
        FacesMessage message;
        owner_identification = OwnerIdentificationImpl.selectOwnerIdentificationDetail(getUser_regn_no().toUpperCase());
        if (owner_identification != null) {
            prv_owner_identification = (OwnerIdentificationDobj) owner_identification.clone();
            setOwnerDetailDobjToBean(owner_identification);
            setShowUpdatePanel(true);
        } else {
            reset();
            setShowUpdatePanel(false);
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No Record found");
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
        }
    }

    public void setOwnerDetailDobjToBean(OwnerIdentificationDobj dobj) {
        setRegn_no(dobj.getRegn_no());
        setMobile_no(String.valueOf(dobj.getMobile_no()));
        setEmail_id(dobj.getEmail_id());
        setPan_no(dobj.getPan_no());
        setAadhar_no(dobj.getAadhar_no());
        setPassport_no(dobj.getPassport_no());
        setRation_card_no(dobj.getRation_card_no());
        setVoter_id(dobj.getVoter_id());
        setDl_no(dobj.getDl_no());
        setVerified_on(dobj.getVerified_on());
        setOwner_ctg(dobj.getOwnerCatg());

    }

    public void updateOwnerIdentification() {

        FacesMessage message;
        owner_identification = new OwnerIdentificationDobj();
        owner_identification.setRegn_no(getRegn_no());
        owner_identification.setMobile_no(Long.parseLong(getMobile_no()));
        owner_identification.setEmail_id(getEmail_id());
        owner_identification.setPan_no(getPan_no());
        owner_identification.setAadhar_no(getAadhar_no());
        owner_identification.setPassport_no(getPassport_no());
        owner_identification.setRation_card_no(getRation_card_no());
        owner_identification.setVoter_id(getVoter_id());
        owner_identification.setDl_no(getDl_no());
        owner_identification.setOwnerCatg(getOwner_ctg());
        List<ComparisonBean> compareChanges = compareChanges();
        if (!compareChanges.isEmpty()) {
            boolean update = OwnerIdentificationImpl.updateOwnerIdentification(owner_identification);
            if (update) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", "Identification Detail Updated Successfully");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
                reset();
                setShowUpdatePanel(false);
                setUser_regn_no("");
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Identification Detail Not Updated");
                FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);
            }
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please Update the Data");
            FacesContext.getCurrentInstance().addMessage("paymentMessageId", message);

        }

    }

    public String back() {
        return "/ui/formOwnerIdentification.xhtml?faces-redirect=true";
    }

    public void reset() {
        setRegn_no("");
        setMobile_no("");
        setEmail_id("");
        setPan_no("");
        setAadhar_no("");
        setPassport_no("");
        setRation_card_no("");
        setVoter_id("");
        setDl_no("");
        setVerified_on(null);
        setOwner_ctg(-1);

    }

    public String getRegn_no() {
        return regn_no;
    }

    public void setRegn_no(String regn_no) {
        this.regn_no = regn_no;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getPan_no() {
        return pan_no;
    }

    public void setPan_no(String pan_no) {
        this.pan_no = pan_no;
    }

    public String getAadhar_no() {
        return aadhar_no;
    }

    public void setAadhar_no(String aadhar_no) {
        this.aadhar_no = aadhar_no;
    }

    public String getPassport_no() {
        return passport_no;
    }

    public void setPassport_no(String passport_no) {
        this.passport_no = passport_no;
    }

    public String getRation_card_no() {
        return ration_card_no;
    }

    public void setRation_card_no(String ration_card_no) {
        this.ration_card_no = ration_card_no;
    }

    public String getVoter_id() {
        return voter_id;
    }

    public void setVoter_id(String voter_id) {
        this.voter_id = voter_id;
    }

    public String getDl_no() {
        return dl_no;
    }

    public void setDl_no(String dl_no) {
        this.dl_no = dl_no;
    }

    public Date getVerified_on() {
        return verified_on;
    }

    public void setVerified_on(Date verified_on) {
        this.verified_on = verified_on;
    }

    public int getOwner_ctg() {
        return owner_ctg;
    }

    public void setOwner_ctg(int owner_ctg) {
        this.owner_ctg = owner_ctg;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getUser_regn_no() {
        return user_regn_no;
    }

    public void setUser_regn_no(String user_regn_no) {
        this.user_regn_no = user_regn_no;
    }

    public OwnerIdentificationDobj getOwner_identification() {
        return owner_identification;
    }

    public void setOwner_identification(OwnerIdentificationDobj owner_identification) {
        this.owner_identification = owner_identification;
    }

    public List getListOwnerCatg() {
        return listOwnerCatg;
    }

    public void setListOwnerCatg(List listOwnerCatg) {
        this.listOwnerCatg = listOwnerCatg;
    }

    public boolean isShowUpdatePanel() {
        return showUpdatePanel;
    }

    public void setShowUpdatePanel(boolean showUpdatePanel) {
        this.showUpdatePanel = showUpdatePanel;
    }

    @Override
    public String save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ComparisonBean> compareChanges() {
        try {
            if (prv_owner_identification == null) {
                return getCompBeanList();
            }
            compBeanList.clear();
            Compare("Owner Category", prv_owner_identification.getOwnerCatg(), owner_identification.getOwnerCatg(), compBeanList);
            Compare("Mobile No.", prv_owner_identification.getMobile_no(), owner_identification.getMobile_no(), compBeanList);
            Compare("Email ID", prv_owner_identification.getEmail_id(), owner_identification.getEmail_id(), compBeanList);
            Compare("Pan No.", prv_owner_identification.getPan_no(), owner_identification.getPan_no(), compBeanList);
            Compare("Aadhar No.", prv_owner_identification.getAadhar_no(), owner_identification.getAadhar_no(), compBeanList);
            Compare("Passport No.", prv_owner_identification.getPassport_no(), owner_identification.getPassport_no(), compBeanList);
            Compare("Ration Card No.", prv_owner_identification.getRation_card_no(), owner_identification.getRation_card_no(), compBeanList);
            Compare("Voter Id No.", prv_owner_identification.getVoter_id(), owner_identification.getVoter_id(), compBeanList);
            Compare("Dl No.", prv_owner_identification.getDl_no(), owner_identification.getDl_no(), compBeanList);

        } catch (Exception e) {
            LOGGER.error("Regn No : " + prv_owner_identification.getRegn_no() + "--" + e.getStackTrace()[0]);
        }
        return getCompBeanList();

    }

    public OwnerIdentificationDobj getPrv_owner_identification() {
        return prv_owner_identification;
    }

    public void setPrv_owner_identification(OwnerIdentificationDobj prv_owner_identification) {
        this.prv_owner_identification = prv_owner_identification;
    }

    public List<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    public void setCompBeanList(List<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    @Override
    public String saveAndMoveFile() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ComparisonBean> getPrevChangedDataList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveEApplication() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
