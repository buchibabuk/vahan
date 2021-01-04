/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.sql.RowSet;
import nic.vahan.db.MasterTableFiller;
import nic.vahan.db.connection.TransactionManager;
import org.primefaces.component.datagrid.DataGrid;
import org.primefaces.component.selectonemenu.SelectOneMenu;

/**
 *
 * @author AMBRISH
 */
@ManagedBean(name = "feetest")
@ViewScoped
public class FeeTestBean implements Serializable {

    public class feeDetails {

        private String purpose_cd;
        private String purpose;
        private String fee;
        private String service_charge;
        private String cess;

        /**
         * @return the purpose
         */
        public String getPurpose() {
            return purpose;
        }

        /**
         * @param purpose the purpose to set
         */
        public void setPurpose(String purpose) {
            this.purpose = purpose;
        }

        /**
         * @return the fee
         */
        public String getFee() {
            return fee;
        }

        /**
         * @param fee the fee to set
         */
        public void setFee(String fee) {
            this.fee = fee;
        }

        /**
         * @return the service_charge
         */
        public String getService_charge() {
            return service_charge;
        }

        /**
         * @param service_charge the service_charge to set
         */
        public void setService_charge(String service_charge) {
            this.service_charge = service_charge;
        }

        /**
         * @return the cess
         */
        public String getCess() {
            return cess;
        }

        /**
         * @param cess the cess to set
         */
        public void setCess(String cess) {
            this.cess = cess;
        }

        /**
         * @return the purpose_cd
         */
        public String getPurpose_cd() {
            return purpose_cd;
        }

        /**
         * @param purpose_cd the purpose_cd to set
         */
        public void setPurpose_cd(String purpose_cd) {
            this.purpose_cd = purpose_cd;
        }
    }
    private SelectOneMenu vhclass = new SelectOneMenu();
    private SelectOneMenu vchcatg = new SelectOneMenu();
    private SelectOneMenu purpose = new SelectOneMenu();
    private ArrayList listvhclass = new ArrayList();
    private ArrayList listvchcatg = new ArrayList();
    private ArrayList listpurpose = new ArrayList();
    private List<feeDetails> feelist = new ArrayList<feeDetails>();

    public FeeTestBean() {
        String[][] data = MasterTableFiller.masterTables.VM_VH_CLASS.getData();
        for (int i = 0; i < data.length; i++) {
            listvhclass.add(new SelectItem(data[i][0], data[i][1]));
        }




        /*data = MasterTableFiller.masterTables.VM_VHCLASS_CATG_MAP.getData();
         for (int i = 0; i < data.length; i++) {
         listvchcatg.add(new SelectItem(data[i][0], data[i][1]));
         }*/


        //  data = MasterTableFiller.masterTables.TM_PURPOSE_MAST.getData();
        // for (int i = 0; i < data.length; i++) {
        //     listpurpose.add(new SelectItem(data[i][0], data[i][1]));
        // }
    }

    public void displayfee() {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getFees");
            //listvchcatg
            String vhcatgStr = "";
            for (int i = 0; i < listvchcatg.size(); i++) {
                SelectItem object = (SelectItem) listvchcatg.get(i);
                if (object.getValue().toString().equalsIgnoreCase(vchcatg.getValue().toString())) {
                    vhcatgStr = object.getLabel();
                    break;
                }

            }

            feelist.clear();

            feeDetails feedetails = new feeDetails();
            //pupose code 1 New Registration
            tmgr.prepareStatement("select * from getfee_new(1," + vhclass.getValue().toString() + ",'" + vhcatgStr + "')");
            RowSet rs = tmgr.fetchDetachedRowSet_No_release();
            String message = "";
            if (rs.next()) {
                feedetails.setPurpose_cd("1");
                feedetails.setPurpose("New Registration ");
                feedetails.setFee(rs.getString(1) != null ? rs.getString(1) : "0");
                feedetails.setService_charge(rs.getString(2) != null ? rs.getString(2) : "0");
                feedetails.setCess(rs.getString(3) != null ? rs.getString(3) : "0");
                feelist.add(feedetails);
            }

            rs = null;

            //pupose code 2 Fitness Certificate
            tmgr.prepareStatement("select * from getfee_new(2," + vhclass.getValue().toString() + ",'" + vhcatgStr + "')");
            rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) {
                feedetails = new feeDetails();
                feedetails.setPurpose_cd("2");
                feedetails.setPurpose("Fitness Certificate ");
                feedetails.setFee(rs.getString(1) != null ? rs.getString(1) : "0");
                feedetails.setService_charge(rs.getString(2) != null ? rs.getString(2) : "0");
                feedetails.setCess(rs.getString(3) != null ? rs.getString(3) : "0");
                feelist.add(feedetails);
            }



            //pupose code 4 Change of Address in RC
            tmgr.prepareStatement("select * from getfee_new(4," + vhclass.getValue().toString() + ",'" + vhcatgStr + "')");
            rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) {
                feedetails = new feeDetails();
                feedetails.setPurpose_cd("4");
                feedetails.setPurpose("Change of Address in RC ");
                feedetails.setFee(rs.getString(1) != null ? rs.getString(1) : "0");
                feedetails.setService_charge(rs.getString(2) != null ? rs.getString(2) : "0");
                feedetails.setCess(rs.getString(3) != null ? rs.getString(3) : "0");
                feelist.add(feedetails);
            }



            //pupose code 5 Transfer of Ownership
            tmgr.prepareStatement("select * from getfee_new(5," + vhclass.getValue().toString() + ",'" + vhcatgStr + "')");
            rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) {
                feedetails = new feeDetails();
                feedetails.setPurpose_cd("5");
                feedetails.setPurpose("Transfer of Ownership ");
                feedetails.setFee(rs.getString(1) != null ? rs.getString(1) : "0");
                feedetails.setService_charge(rs.getString(2) != null ? rs.getString(2) : "0");
                feedetails.setCess(rs.getString(3) != null ? rs.getString(3) : "0");
                feelist.add(feedetails);
            }


            //pupose code 6 Hypothecation Entry
            tmgr.prepareStatement("select * from getfee_new(6," + vhclass.getValue().toString() + ",'" + vhcatgStr + "')");
            rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) {
                feedetails = new feeDetails();
                feedetails.setPurpose_cd("6");
                feedetails.setPurpose("Hypothecation Entry ");
                feedetails.setFee(rs.getString(1) != null ? rs.getString(1) : "0");
                feedetails.setService_charge(rs.getString(2) != null ? rs.getString(2) : "0");
                feedetails.setCess(rs.getString(3) != null ? rs.getString(3) : "0");
                feelist.add(feedetails);
            }



            //pupose code 7 Hypothecation Termination
            tmgr.prepareStatement("select * from getfee_new(7," + vhclass.getValue().toString() + ",'" + vhcatgStr + "')");
            rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) {
                feedetails = new feeDetails();
                feedetails.setPurpose_cd("7");
                feedetails.setPurpose("Hypothecation Termination ");
                feedetails.setFee(rs.getString(1) != null ? rs.getString(1) : "0");
                feedetails.setService_charge(rs.getString(2) != null ? rs.getString(2) : "0");
                feedetails.setCess(rs.getString(3) != null ? rs.getString(3) : "0");
                feelist.add(feedetails);
            }


            //pupose code 9 NOC
            tmgr.prepareStatement("select * from getfee_new(9," + vhclass.getValue().toString() + ",'" + vhcatgStr + "')");
            rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) {
                feedetails = new feeDetails();
                feedetails.setPurpose_cd("9");
                feedetails.setPurpose("NOC ");
                feedetails.setFee(rs.getString(1) != null ? rs.getString(1) : "0");
                feedetails.setService_charge(rs.getString(2) != null ? rs.getString(2) : "0");
                feedetails.setCess(rs.getString(3) != null ? rs.getString(3) : "0");
                feelist.add(feedetails);
            }



            //pupose code 18 Temporary Registration
            tmgr.prepareStatement("select * from getfee_new(18," + vhclass.getValue().toString() + ",'" + vhcatgStr + "')");
            rs = tmgr.fetchDetachedRowSet_No_release();

            if (rs.next()) {
                feedetails = new feeDetails();
                feedetails.setPurpose_cd("18");
                feedetails.setPurpose("Temporary Registration ");
                feedetails.setFee(rs.getString(1) != null ? rs.getString(1) : "0");
                feedetails.setService_charge(rs.getString(2) != null ? rs.getString(2) : "0");
                feedetails.setCess(rs.getString(3) != null ? rs.getString(3) : "0");
                feelist.add(feedetails);
            }

            return;
        } catch (SQLException ex) {
            FacesContext.getCurrentInstance().addMessage("feedetails", new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void vhclassListener(ValueChangeEvent event) {
        String distCd = (String) event.getNewValue();
        String[][] data = MasterTableFiller.masterTables.VM_VHCLASS_CATG_MAP.getData();
        listvchcatg.clear();
        for (int i = 0; i < data.length; i++) {
            if (data[i][0].equals(distCd)) {
                listvchcatg.add(new SelectItem(data[i][1], data[i][1]));
            }

        }

    }

    /**
     * @return the vhclass
     */
    public SelectOneMenu getVhclass() {
        return vhclass;
    }

    /**
     * @param vhclass the vhclass to set
     */
    public void setVhclass(SelectOneMenu vhclass) {
        this.vhclass = vhclass;
    }

    /**
     * @return the vchcatg
     */
    public SelectOneMenu getVchcatg() {
        return vchcatg;
    }

    /**
     * @param vchcatg the vchcatg to set
     */
    public void setVchcatg(SelectOneMenu vchcatg) {
        this.vchcatg = vchcatg;
    }

    /**
     * @return the purpose
     */
    public SelectOneMenu getPurpose() {
        return purpose;
    }

    /**
     * @param purpose the purpose to set
     */
    public void setPurpose(SelectOneMenu purpose) {
        this.purpose = purpose;
    }

    /**
     * @return the listvhclass
     */
    public ArrayList getListvhclass() {
        return listvhclass;
    }

    /**
     * @param listvhclass the listvhclass to set
     */
    public void setListvhclass(ArrayList listvhclass) {
        this.listvhclass = listvhclass;
    }

    /**
     * @return the listvchcatg
     */
    public ArrayList getListvchcatg() {
        return listvchcatg;
    }

    /**
     * @param listvchcatg the listvchcatg to set
     */
    public void setListvchcatg(ArrayList listvchcatg) {
        this.listvchcatg = listvchcatg;
    }

    /**
     * @return the listpurpose
     */
    public ArrayList getListpurpose() {
        return listpurpose;
    }

    /**
     * @param listpurpose the listpurpose to set
     */
    public void setListpurpose(ArrayList listpurpose) {
        this.listpurpose = listpurpose;
    }

    /**
     * @return the feelist
     */
    public List<feeDetails> getFeelist() {
        return feelist;
    }

    /**
     * @param feelist the feelist to set
     */
    public void setFeelist(ArrayList feelist) {
        this.feelist = feelist;
    }
}
