/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.common.jsf.utils;

import java.io.IOException;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;
import com.sun.faces.renderkit.html_basic.FormRenderer;
 
public class MyFormRenderer extends FormRenderer {
 
@Override
public void encodeBegin(final FacesContext context, final UIComponent component) throws IOException {
   final ResponseWriter writer = context.getResponseWriter();
   super.encodeBegin(context, component);
   renderUniqueToken(writer, component);
}
 
private String getRequestURI() {
   final HttpServletRequest servletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
   return servletRequest.getRequestURI().substring(0, servletRequest.getRequestURI().lastIndexOf("."));
}
 
private String getUniqueTokenFromSession(final FacesContext facesContext) {
   String token = null;
   final Map tokenMap = (Map) facesContext.getApplication().createValueBinding("#{visitedTokenMap}").getValue(facesContext);
   if (tokenMap != null) {
      token = (String) tokenMap.get(getRequestURI());
   }
   return token;
}
 
private void renderUniqueToken(final ResponseWriter writer, final UIComponent component) throws IOException {
   writer.startElement("input", component);
   writer.writeAttribute("type", "hidden", "type");
   writer.writeAttribute("name", "uniqueToken", "name");
   final String uniqueToken = getUniqueTokenFromSession(FacesContext.getCurrentInstance());
   writer.writeAttribute("value", uniqueToken == null ? "" : uniqueToken, "value");
   writer.endElement("input");
   }
}
