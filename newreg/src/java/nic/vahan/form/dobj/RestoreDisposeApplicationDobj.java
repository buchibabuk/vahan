/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author nicsi
 */
public class RestoreDisposeApplicationDobj implements Serializable {

    private boolean checkPurcdOwnerAdmin;

    public boolean isCheckPurcdOwnerAdmin() {
        return checkPurcdOwnerAdmin;
    }

    public void setCheckPurcdOwnerAdmin(boolean checkPurcdOwnerAdmin) {
        this.checkPurcdOwnerAdmin = checkPurcdOwnerAdmin;
    }
}
