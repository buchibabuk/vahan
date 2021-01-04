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

@FacesValidator("aadharValidator")
public class AadharValidator implements Validator {

    private final static int AADHAR_NO_SIZE = 12;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        long aadharNo;
        FacesMessage msg = new FacesMessage("Invalid AAdhar No", "Invalid AAdhar No");
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        try {
            if (value.toString().length() > 0) {
                aadharNo = Long.valueOf(value.toString().trim());
                if (value.toString().trim().length() != AADHAR_NO_SIZE || aadharNo <= 99999999999l) {
                    throw new ValidatorException(msg);
                }
            }
        } catch (NumberFormatException nfex) {
            throw new ValidatorException(msg);
        }
    }
}
