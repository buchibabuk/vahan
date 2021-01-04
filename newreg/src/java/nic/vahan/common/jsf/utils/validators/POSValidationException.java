/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.common.jsf.utils.validators;

import nic.rto.vahan.common.VahanException;

/**
 *
 * @author Aftab Khan 12-07-2016
 */
public class POSValidationException extends VahanException {

    public POSValidationException(String msg) {
        super(msg);
    }

    public POSValidationException() {
        super("This is POSValidationException");
    }
}
