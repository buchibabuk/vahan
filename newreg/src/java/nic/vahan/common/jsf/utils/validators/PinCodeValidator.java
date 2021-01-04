/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.common.jsf.utils.validators;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author Ashok Kumar <send2ashok@hotmail.com>
 */
@FacesValidator("pinCodeValidator")
public class PinCodeValidator implements Validator {

    private final static int PIN_SIZE = 6;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String pinCode;
        pinCode = value.toString();

        if (pinCode.length() > 0) {
            if (pinCode.trim().length() != PIN_SIZE || (Integer.parseInt(pinCode.trim()) <= 99999)) {
                FacesMessage msg = new FacesMessage("Size of PIN Code is less than Expected or Invalid PIN Code", "Invalid PIN Code");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
    }
}
