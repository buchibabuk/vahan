/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.login.controller;

import nic.rto.vahan.common.VahanException;
import nic.vahan5.login.model.CustomLoginBeanModel;
import nic.vahan5.login.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Kartikey Singh
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    LoginService loginService;

    @PostMapping("/insert")
    public CustomLoginBeanModel insertUserEntry(@RequestBody CustomLoginBeanModel customLoginBean) throws VahanException {
        return loginService.insertUserEntry(customLoginBean);
    }

    @PostMapping("/delete")
    public int deleteUserEntry(@RequestBody CustomLoginBeanModel customLoginBean) throws VahanException {
        return loginService.deleteUserEntry(customLoginBean);        
    }

    @PostMapping("/get")
    public CustomLoginBeanModel getUserEntry(@RequestBody CustomLoginBeanModel customLoginBean) throws VahanException {
        return loginService.getUserEntry(customLoginBean);        
    }
}
