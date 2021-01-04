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
 * @author pramod
 */
@FacesValidator("ifscValidator")
public class IFSCValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        FacesMessage msg = new FacesMessage("Invalid combination of IFSC code.", "Invalid combination of IFSC code.");
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        try {
            final String regex = "([A-Z]{4}[0]{1}[A-Z0-9]{6})";
            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(value.toString().toUpperCase());
            if (!matcher.find()) {
                throw new ValidatorException(msg);
            }
        } catch (NumberFormatException ex) {
            throw new ValidatorException(msg);
        }
    }
}
