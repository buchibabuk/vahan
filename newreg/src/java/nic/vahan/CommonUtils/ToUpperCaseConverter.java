/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.CommonUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author nic5912
 */
@FacesConverter("toUpperCaseConverter")
public class ToUpperCaseConverter implements Converter {

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        //return (String) value; // Or (value != null) ? value.toString().toUpperCase() : null;
        return (value != null) ? value.toString().toUpperCase() : "";
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return (value != null) ? value.toUpperCase() : "";
    }
    
    }
