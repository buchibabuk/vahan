/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.Trailer_dobj;
import static nic.vahan.server.ServerUtil.Compare;

@ManagedBean(name = "trailer_bean")
@ViewScoped
public class Trailer_bean implements Serializable {

    private String body_type;
    private String chassis_no_trailer;
    private int ld_wt;
    private int unld_wt;
    private String f_axle_descp;
    private String r_axle_descp;
    private String o_axle_descp;
    private String t_axle_descp;
    private int f_axle_weight;
    private int r_axle_weight;
    private int o_axle_weight;
    private int t_axle_weight;
    private ArrayList list_body_type;
    private ArrayList<ComparisonBean> compBeanList = new ArrayList<>();
    private Trailer_dobj trailer_dobj_prv;
    private boolean disable;

    public Trailer_bean() {
        // for displaying list of body type in the view
//        list_body_type = new ArrayList();
//        String[][] data = MasterTableFiller.masterTables.VM_BD_TYPE.getData();
//        for (int i = 0; i < data.length; i++) {
//            list_body_type.add(new SelectItem(data[i][0], data[i][1]));
//        }
    }

    public void set_trailer_dobj_to_bean(Trailer_dobj trailer_dobj) {
        //setting the value from database to trailer

        if (trailer_dobj == null) {
            return;
        }
        this.setBody_type(trailer_dobj.getBody_type());
        this.setChassis_no_trailer(trailer_dobj.getChasi_no());
        this.setLd_wt(trailer_dobj.getLd_wt());
        this.setUnld_wt(trailer_dobj.getUnld_wt());
        this.setF_axle_descp(trailer_dobj.getF_axle_descp());
        this.setR_axle_descp(trailer_dobj.getR_axle_descp());
        this.setO_axle_descp(trailer_dobj.getO_axle_descp());
        this.setT_axle_descp(trailer_dobj.getT_axle_descp());
        this.setF_axle_weight(trailer_dobj.getF_axle_weight());
        this.setR_axle_weight(trailer_dobj.getR_axle_weight());
        this.setO_axle_weight(trailer_dobj.getO_axle_weight());
        this.setT_axle_weight(trailer_dobj.getT_axle_weight());
    }

    public Trailer_dobj setTrailerBeanToDobj() {

        Trailer_dobj dobj = new Trailer_dobj();
        //setting the value from form_commercial_trailer_dtls to dobj        
        if (validateForm()) {

            dobj.setBody_type(this.getBody_type());
            dobj.setChasi_no(this.getChassis_no_trailer());
            dobj.setLd_wt(this.getLd_wt());
            dobj.setUnld_wt(this.getUnld_wt());
            dobj.setF_axle_descp(this.getF_axle_descp());
            dobj.setR_axle_descp(this.getR_axle_descp());
            dobj.setO_axle_descp(this.getO_axle_descp());
            dobj.setT_axle_descp(this.getT_axle_descp());
            dobj.setF_axle_weight(this.getF_axle_weight());
            dobj.setR_axle_weight(this.getR_axle_weight());
            dobj.setO_axle_weight(this.getO_axle_weight());
            dobj.setT_axle_weight(this.getT_axle_weight());

        }

        return dobj;
    }

    private boolean validateForm() {
        // validate is pending here...
        return true;
    }

    public void componentEditable(boolean flag) {
        flag = !flag;
        setDisable(flag);
    }

    public void componentReadOnly(boolean flag) {
        flag = !flag;
        setDisable(flag);
    }

    public ArrayList<ComparisonBean> addToComapreChangesList(ArrayList<ComparisonBean> compBeanListPrev) throws VahanException {

        ArrayList<ComparisonBean> list = compareChagnes();

        if (compBeanListPrev == null) {
            compBeanListPrev = new ArrayList<ComparisonBean>();
        }
        if (list.size() > 0) {
            compBeanListPrev.addAll(list);
        }

        return compBeanListPrev;
    }

    public ArrayList<ComparisonBean> compareChagnes() throws VahanException {

        Trailer_dobj dobj = getTrailer_dobj_prv();//getting the dobj from workbench      

        if (dobj == null) {
            return compBeanList;
        }
        compBeanList.clear();

        Compare("trailer_bd_type", dobj.getBody_type(), this.getBody_type(), compBeanList);
        Compare("Trailer RLW", dobj.getLd_wt(), this.getLd_wt(), compBeanList);
        Compare("Trailer Unladen Wt", dobj.getUnld_wt(), this.getUnld_wt(), compBeanList);
        Compare("f_axle_descp", dobj.getF_axle_descp(), this.getF_axle_descp(), compBeanList);
        Compare("r_axle_descp", dobj.getR_axle_descp(), this.getR_axle_descp(), compBeanList);
        Compare("o_axle_descp", dobj.getO_axle_descp(), this.getO_axle_descp(), compBeanList);
        Compare("t_axle_descp", dobj.getT_axle_descp(), this.getT_axle_descp(), compBeanList);
        Compare("f_axle_weight", dobj.getF_axle_weight(), this.getF_axle_weight(), compBeanList);
        Compare("r_axle_weight", dobj.getR_axle_weight(), this.getR_axle_weight(), compBeanList);
        Compare("o_axle_weight", dobj.getO_axle_weight(), this.getO_axle_weight(), compBeanList);
        Compare("t_axle_weight", dobj.getT_axle_weight(), this.getT_axle_weight(), compBeanList);

        return compBeanList;

    }

    /**
     * @return the list_body_type
     */
    public ArrayList getList_body_type() {
        return list_body_type;
    }

    /**
     * @param list_body_type the list_body_type to set
     */
    public void setList_body_type(ArrayList list_body_type) {
        this.list_body_type = list_body_type;
    }

    /**
     * @return the compBeanList
     */
    public ArrayList<ComparisonBean> getCompBeanList() {
        return compBeanList;
    }

    /**
     * @param compBeanList the compBeanList to set
     */
    public void setCompBeanList(ArrayList<ComparisonBean> compBeanList) {
        this.compBeanList = compBeanList;
    }

    /**
     * @return the trailer_dobj_prv
     */
    public Trailer_dobj getTrailer_dobj_prv() {
        return trailer_dobj_prv;
    }

    /**
     * @param trailer_dobj_prv the trailer_dobj_prv to set
     */
    public void setTrailer_dobj_prv(Trailer_dobj trailer_dobj_prv) {
        this.trailer_dobj_prv = trailer_dobj_prv;
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
     * @return the body_type
     */
    public String getBody_type() {
        return body_type;
    }

    /**
     * @param body_type the body_type to set
     */
    public void setBody_type(String body_type) {
        this.body_type = body_type;
    }

    /**
     * @return the ld_wt
     */
    public int getLd_wt() {
        return ld_wt;
    }

    /**
     * @param ld_wt the ld_wt to set
     */
    public void setLd_wt(int ld_wt) {
        this.ld_wt = ld_wt;
    }

    /**
     * @return the unld_wt
     */
    public int getUnld_wt() {
        return unld_wt;
    }

    /**
     * @param unld_wt the unld_wt to set
     */
    public void setUnld_wt(int unld_wt) {
        this.unld_wt = unld_wt;
    }

    /**
     * @return the f_axle_descp
     */
    public String getF_axle_descp() {
        return f_axle_descp;
    }

    /**
     * @param f_axle_descp the f_axle_descp to set
     */
    public void setF_axle_descp(String f_axle_descp) {
        this.f_axle_descp = f_axle_descp;
    }

    /**
     * @return the r_axle_descp
     */
    public String getR_axle_descp() {
        return r_axle_descp;
    }

    /**
     * @param r_axle_descp the r_axle_descp to set
     */
    public void setR_axle_descp(String r_axle_descp) {
        this.r_axle_descp = r_axle_descp;
    }

    /**
     * @return the o_axle_descp
     */
    public String getO_axle_descp() {
        return o_axle_descp;
    }

    /**
     * @param o_axle_descp the o_axle_descp to set
     */
    public void setO_axle_descp(String o_axle_descp) {
        this.o_axle_descp = o_axle_descp;
    }

    /**
     * @return the t_axle_descp
     */
    public String getT_axle_descp() {
        return t_axle_descp;
    }

    /**
     * @param t_axle_descp the t_axle_descp to set
     */
    public void setT_axle_descp(String t_axle_descp) {
        this.t_axle_descp = t_axle_descp;
    }

    /**
     * @return the f_axle_weight
     */
    public int getF_axle_weight() {
        return f_axle_weight;
    }

    /**
     * @param f_axle_weight the f_axle_weight to set
     */
    public void setF_axle_weight(int f_axle_weight) {
        this.f_axle_weight = f_axle_weight;
    }

    /**
     * @return the r_axle_weight
     */
    public int getR_axle_weight() {
        return r_axle_weight;
    }

    /**
     * @param r_axle_weight the r_axle_weight to set
     */
    public void setR_axle_weight(int r_axle_weight) {
        this.r_axle_weight = r_axle_weight;
    }

    /**
     * @return the o_axle_weight
     */
    public int getO_axle_weight() {
        return o_axle_weight;
    }

    /**
     * @param o_axle_weight the o_axle_weight to set
     */
    public void setO_axle_weight(int o_axle_weight) {
        this.o_axle_weight = o_axle_weight;
    }

    /**
     * @return the t_axle_weight
     */
    public int getT_axle_weight() {
        return t_axle_weight;
    }

    /**
     * @param t_axle_weight the t_axle_weight to set
     */
    public void setT_axle_weight(int t_axle_weight) {
        this.t_axle_weight = t_axle_weight;
    }
    /**
     * @return the chassis_no_trailer
     */
    public String getChassis_no_trailer() {
        return chassis_no_trailer;
    }

    /**
     * @param chassis_no_trailer the chassis_no_trailer to set
     */
    public void setChassis_no_trailer(String chassis_no_trailer) {
        this.chassis_no_trailer = chassis_no_trailer;
    }
}
