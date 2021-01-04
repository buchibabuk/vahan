/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form;

import java.util.ArrayList;
import java.util.List;
import nic.vahan.form.bean.ComparisonBean;

public interface ApproveDisapproveInterface {

    public String save();

    public List<ComparisonBean> compareChanges();

    public String saveAndMoveFile();

    public List<ComparisonBean> getCompBeanList();

    public List<ComparisonBean> getPrevChangedDataList();

    public void setPrevChangedDataList(List<ComparisonBean> prevChangedDataList);

    public void saveEApplication();
}
