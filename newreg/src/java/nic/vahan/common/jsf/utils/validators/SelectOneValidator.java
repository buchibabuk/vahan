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
import org.primefaces.component.selectonemenu.SelectOneMenu;

/**
 *
 *
 */
@FacesValidator("selectOneValidator")
public class SelectOneValidator implements Validator {

    @Override
    public void validate(FacesContext fc, UIComponent uic, Object val) throws ValidatorException {

        if (uic instanceof SelectOneMenu) {
            SelectOneMenu temp = (SelectOneMenu) uic;
            if (val.toString().equals("-1")) {
                FacesMessage fm = new FacesMessage("Invalid " + temp.getLabel(), "Invalid " + temp.getLabel());
                fm.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(fm);
            }
        }
    }
}
