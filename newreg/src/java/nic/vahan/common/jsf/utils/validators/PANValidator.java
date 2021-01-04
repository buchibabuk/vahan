/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.common.jsf.utils.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
@FacesValidator("panValidator")
public class PANValidator implements Validator {

    private final static int PAN_SIZE = 10;
    private final static String PAN_PATTERN = "^([a-zA-Z]){5}([0-9]){4}([a-zA-Z]){1}?$";
    private Pattern pattern;
    private Matcher matcher;

    public PANValidator() {
        pattern = Pattern.compile(PAN_PATTERN);
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String pan;
        FacesMessage msg = null;
        boolean validPan = false;
        pan = value.toString();

        if (pan.length() > 0) {
            if (pan.trim().length() != PAN_SIZE) {
                msg = new FacesMessage("Size of PAN is less than Expected", "Invalid PAN");
                validPan = true;
            } else {
                matcher = pattern.matcher(value.toString());
                if (!matcher.matches()) {
                    msg = new FacesMessage("Invalid PAN.", "Invalid PAN format.");
                    validPan = true;
                }
            }
        }

        if (validPan) {
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }

    }//end of validate method
}//end of PANValidator class
