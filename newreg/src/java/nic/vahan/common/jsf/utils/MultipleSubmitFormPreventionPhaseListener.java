/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.common.jsf.utils;

import java.util.Date;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class MultipleSubmitFormPreventionPhaseListener implements PhaseListener {

    private static final String KEYVAL_AJAX = "faces-request";
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(MultipleSubmitFormPreventionPhaseListener.class);
    private static final String KEYVAL_AJAX4JSF_REQUEST = "AJAXREQUEST";
    private static final String KEYVAL_AJAXANYWHERE_REQUEST = "aaxmlrequest";
    private static final String SYSTEM_PROPERTY_IGNORE_TOKEN = "ignoreMultipleSubmit";
    private static boolean isMultipleSubmitCheckActivated = true;

    static {
        if (System.getProperty(MultipleSubmitFormPreventionPhaseListener.SYSTEM_PROPERTY_IGNORE_TOKEN) != null) {
            MultipleSubmitFormPreventionPhaseListener.isMultipleSubmitCheckActivated = false;
            MultipleSubmitFormPreventionPhaseListener.LOGGER.warn("---- INACTIVATING MULTIPLE SUBMIT CHECK ----");
        } else {
            MultipleSubmitFormPreventionPhaseListener.LOGGER.warn("---- ACTIVATING MULTIPLE SUBMIT CHECK ----");
        }
    }

    @Override
    public void afterPhase(final PhaseEvent phaseEvent) {
        if (!MultipleSubmitFormPreventionPhaseListener.isMultipleSubmitCheckActivated) {
            return;
        }
        final FacesContext facesContext = phaseEvent.getFacesContext();
        if (isRequestedByAjax4JSF(facesContext)) {
            if (MultipleSubmitFormPreventionPhaseListener.LOGGER.isDebugEnabled()) {
                MultipleSubmitFormPreventionPhaseListener.LOGGER
                        .info("Request is an Ajax4JSF Request. Multiple-submit-check will not be done.");
            }
        } else if (isRequestedByAjaxAnywhere(facesContext)) {
            if (MultipleSubmitFormPreventionPhaseListener.LOGGER.isDebugEnabled()) {
                MultipleSubmitFormPreventionPhaseListener.LOGGER
                        .info("Request is an AjaxAnywhere Request. Multiple-submit-check will not be done.");
            }
        } else {
            checkForDoubleSubmit(facesContext);
        }
    }

    @Override
    public void beforePhase(final PhaseEvent phaseEvent) {
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

    private void checkForDoubleSubmit(final FacesContext facesContext) {

        final Map requestParamMap = facesContext.getExternalContext().getRequestParameterMap();
        final String submittedUniqueToken = (String) requestParamMap.get("uniqueToken");
        if (!StringUtils.isEmpty(submittedUniqueToken)) {
            if (MultipleSubmitFormPreventionPhaseListener.LOGGER.isDebugEnabled()) {
                MultipleSubmitFormPreventionPhaseListener.LOGGER.debug("submitted UniqueToken: " + submittedUniqueToken);
            }
            if (!isInVisitedTokenList(facesContext, submittedUniqueToken)) {
                if (MultipleSubmitFormPreventionPhaseListener.LOGGER.isInfoEnabled()) {
                    /*MultipleSubmitFormPreventionPhaseListener.LOGGER
                     .info("Submitted token is not in visited token list. Preventing multiple submit. Rendering response with warn message.");*/
                    /*MultipleSubmitFormPreventionPhaseListener.LOGGER
                     .info("Multiple Submit Occured on Page -- " + FacesContext.getCurrentInstance().getExternalContext().getRequestPathInfo());*/
                    System.out.println("Multiple Submit Occured on Page -- " + FacesContext.getCurrentInstance().getExternalContext().getRequestPathInfo());
                }

                HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
                if (request.getSession().getAttribute("emp_cd") != null && request.getSession().getAttribute("off_cd") != null) {
                    facesContext.getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "home");
                } else {
                    facesContext.renderResponse();
                }

            } else {
                getVisitedTokenMap(facesContext).remove(getRequestURI());
            }
        }
        final String newUniqueToken = getUniqueToken(facesContext);
        if (MultipleSubmitFormPreventionPhaseListener.LOGGER.isDebugEnabled()) {
            MultipleSubmitFormPreventionPhaseListener.LOGGER.info("adding token: " + newUniqueToken + " to visited token list");
        }
        getVisitedTokenMap(facesContext).put(getRequestURI(), newUniqueToken);

    }

    private String getRequestURI() {
        final HttpServletRequest servletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return servletRequest.getRequestURI().substring(0, servletRequest.getRequestURI().lastIndexOf("."));
    }

    private String getUniqueToken(final FacesContext facesContext) {
        final HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        return request.getSession().getId() + new Date().getTime();
    }

    private Map getVisitedTokenMap(final FacesContext context) {
        return (Map) context.getApplication().createValueBinding("#{visitedTokenMap}").getValue(context);
    }

    private boolean isInVisitedTokenList(final FacesContext context, final String submittedUniqueToken) {
        return getVisitedTokenMap(context).containsValue(submittedUniqueToken);
    }

    private boolean isRequestedByAjax4JSF(final FacesContext facesContext) {
        final String ajaxReqVal = facesContext.getExternalContext().getRequestParameterMap()
                .get(MultipleSubmitFormPreventionPhaseListener.KEYVAL_AJAX4JSF_REQUEST);
        if (StringUtils.isNotEmpty(ajaxReqVal)) {
            return true;
        }
        return false;
    }

    private boolean isRequestedByAjaxAnywhere(final FacesContext facesContext) {
        final Map requestHeaderMap = facesContext.getExternalContext().getRequestHeaderMap();
        final String ajaxAnywhereReq = (String) requestHeaderMap.get(MultipleSubmitFormPreventionPhaseListener.KEYVAL_AJAXANYWHERE_REQUEST);
        if ("true".equals(ajaxAnywhereReq)) {
            return true;
        }
        final String partialAjax = (String) requestHeaderMap.get(MultipleSubmitFormPreventionPhaseListener.KEYVAL_AJAX);
        if (partialAjax != null && partialAjax.contains("ajax")) {
            return true;
        }
        return false;
    }
}
