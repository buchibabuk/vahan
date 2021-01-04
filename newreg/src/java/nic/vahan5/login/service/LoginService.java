/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.login.service;

import nic.rto.vahan.common.VahanException;
import nic.vahan5.login.model.CustomLoginBeanModel;

/**
 *
 * @author Kartikey Singh
 */
public interface LoginService {

    public CustomLoginBeanModel insertUserEntry(CustomLoginBeanModel customLoginBean) throws VahanException;

    public int deleteUserEntry(CustomLoginBeanModel customLoginBean) throws VahanException;

    public CustomLoginBeanModel getUserEntry(CustomLoginBeanModel customLoginBean) throws VahanException;
}
