/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.login.service;

import nic.rto.vahan.common.VahanException;
import nic.vahan5.login.dao.LoginDAO;
import nic.vahan5.login.model.CustomLoginBeanModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kartikey Singh
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    LoginDAO loginDao;

    @Override
    public CustomLoginBeanModel insertUserEntry(CustomLoginBeanModel customLoginBean) throws VahanException {
        return loginDao.insertUserEntry(customLoginBean);
    }

    @Override
    public int deleteUserEntry(CustomLoginBeanModel customLoginBean) throws VahanException {
        return loginDao.deleteUserEntry(customLoginBean);
    }

    @Override
    public CustomLoginBeanModel getUserEntry(CustomLoginBeanModel customLoginBean) throws VahanException {
        return loginDao.getUserEntry(customLoginBean);
    }
}
