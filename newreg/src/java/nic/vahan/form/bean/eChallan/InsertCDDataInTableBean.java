/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.eChallan;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.form.dobj.eChallan.InsertCDDataInTableDobj;
import org.apache.log4j.Logger;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Administrator
 */
@SessionScoped
@ManagedBean
public class InsertCDDataInTableBean {

    private static final Logger LOGGER = Logger.getLogger(InsertCDDataInTableBean.class);
    private UploadedFile file;
    private static final String PATH = "C:\\eChallan_data";
    InsertCDDataInTableDobj dobj = null;
    private List<InsertCDDataInTableDobj> list = new ArrayList<>();

    public void upload() throws FileNotFoundException, IOException {
        if (file != null) {
            String chal_date = "";
            String reg_no = "";
            String op_date = "";
            String chal_time = "";
            String amnt = "";
            String chal_place = "";
            String address = "";
            String State = "";
            String off_cd = "";
            String number = "";
            String mobile = "";
            String name = "";
            try {
                dobj = new InsertCDDataInTableDobj();
                if (file.getFileName().equals("")) {
                    JSFUtils.showMessage("Please Select File To Upload..");
                    return;
                }
                InputStream inpstrm = file.getInputstream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inpstrm));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] splitData = line.split("@#@");
                    chal_date = splitData[1];
                    reg_no = splitData[2];
                    op_date = splitData[3];
                    chal_time = splitData[4];
                    amnt = splitData[5];
                    name = splitData[6];
                    chal_place = splitData[7];
                    address = splitData[8];
                    State = splitData[9];
                    off_cd = splitData[10];
                    dobj.setChal_date(splitData[1]);
                    dobj.setRegn_no(splitData[2]);
                    dobj.setOp_date(splitData[3]);
                    dobj.setChal_time(splitData[4]);
                    dobj.setChal_no(splitData[5]);
                    dobj.setOwner_name(splitData[6]);
                    dobj.setChal_place(splitData[7]);
                    dobj.setAddress(splitData[8]);
                    dobj.setState(splitData[9]);
                    dobj.setOffice(splitData[10]);
                    dobj.setMobile_no(splitData[12]);
                    getList().add(dobj);
                }
                copyFileUsingStream(file.getFileName(), file.getInputstream());
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
            FacesMessage msg = new FacesMessage("File Succesful,  " + file.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

    }

    private static void copyFileUsingStream(String fileName, InputStream inpt) throws IOException {
        String dest = "D:\\jsf notes\\New folder\\";
        OutputStream os = null;
        try {

            os = new FileOutputStream(dest + fileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inpt.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            inpt.close();
            os.close();
        }
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    /**
     * @return the list
     */
    public List<InsertCDDataInTableDobj> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(List<InsertCDDataInTableDobj> list) {
        this.list = list;
    }
}
