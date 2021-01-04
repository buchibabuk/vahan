/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.db;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author tranC103
 */
@ManagedBean
@RequestScoped
public class ReloadMasters {

    public void reloadMasterData() {
        MasterTableFiller.ReloadMasterTables();
    }
}
