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
 * @author acer
 */
@FacesValidator("permitTypeValidator")
public class PermitTypeValidator implements Validator {

    FacesMessage msg = null;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value != null && Integer.parseInt(value.toString()) < 0) {
            msg = new FacesMessage("Invalid Permit Type, Please Select Valid Permit Type");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }
}
