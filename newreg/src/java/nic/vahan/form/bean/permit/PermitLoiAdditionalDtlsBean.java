/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.permit;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import nic.vahan.CommonUtils.FileUploadRenderer;
import nic.vahan.form.dobj.permit.PermitLoiAdditionalDtlsDobj;
import nic.vahan.server.CommonUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author R Gautam
 */
@ManagedBean(name = "loiAddDlts")
@SessionScoped
public class PermitLoiAdditionalDtlsBean extends FileUploadRenderer implements Serializable {

    private PermitLoiAdditionalDtlsDobj dobj = null;
    private String urlPath;
    private StreamedContent viewSignFile;

    @PostConstruct
    void init() {
        dobj = null;
        viewSignFile = new DefaultStreamedContent();
    }

    public void setLoiAdditionalDobj(PermitLoiAdditionalDtlsDobj loiDobj) {
        if (loiDobj != null) {
            dobj = new PermitLoiAdditionalDtlsDobj();
            if (CommonUtils.isNullOrBlank(loiDobj.getBatchNo())) {
                dobj.setBatchNo("");
            } else {
                dobj.setBatchNo(loiDobj.getBatchNo());
            }

            if (CommonUtils.isNullOrBlank(loiDobj.getDlNo())) {
                dobj.setDlNo("");
            } else {
                dobj.setDlNo(loiDobj.getDlNo());
            }

            if (CommonUtils.isNullOrBlank(loiDobj.getAdharCardNo())) {
                dobj.setAdharCardNo("");
            } else {
                dobj.setAdharCardNo(loiDobj.getAdharCardNo());
            }
            if (loiDobj.getImage() != null) {
                setViewSignFile(new DefaultStreamedContent(new ByteArrayInputStream(loiDobj.getImage())));
            }
        } else {
            dobj = null;
        }
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        if (context.getExternalContext().getRequestContentType().toLowerCase().startsWith("multipart/")) {
            super.decode(context, component);
        }
    }

    public PermitLoiAdditionalDtlsDobj getDobj() {
        return dobj;
    }

    public void setDobj(PermitLoiAdditionalDtlsDobj dobj) {
        this.dobj = dobj;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public StreamedContent getViewSignFile() {
        return viewSignFile;
    }

    public void setViewSignFile(StreamedContent viewSignFile) {
        this.viewSignFile = viewSignFile;
    }
}
