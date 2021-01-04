/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import nic.vahan.form.dobj.UploadPollutionDataDobj;
import nic.vahan.form.impl.BlackListedVehicleImpl;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Kunal Maiti
 */
@ManagedBean(name = "uploadPollutionDataBean")
@ViewScoped
public class UploadPollutionDataBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(UploadPollutionDataBean.class);
    private List<UploadPollutionDataDobj> uploadPollutionList;
    private List<UploadPollutionDataDobj> failUploadPollutionList;
    private List<UploadPollutionDataDobj> sucessUploadPollutionList;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

    public UploadPollutionDataBean() {
    }

    @PostConstruct
    public void Init() {
        try {
            String user_catg = ServerUtil.getUserCategory(Long.parseLong(Util.getEmpCode()));
            boolean checkUserPerm = false;
            if (user_catg.equals("S") || (user_catg.equals("A"))) {
                checkUserPerm = true;
            }
            if (checkUserPerm == false) {
                JSFUtils.showMessage("YOU ARE NOT ALLOWED TO ENTER DATA IN MASTER FORM");
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile uFile = event.getFile();
        String cvsSplitBy = ",";
        int key = 0;
        int startrow = 1;
        int complainType = 9;
        String complain = "Pollution level exceeds permissible level";

        if (uFile != null) {
            String fileName = uFile.getFileName();
            String fileContentType = uFile.getContentType();
            System.out.println("fileContentType:" + fileContentType);
            if (fileContentType.equalsIgnoreCase("application/vnd.ms-excel")) {
                List<UploadPollutionDataDobj> pollutionList = fileUploadData(uFile, Util.getUserStateCode(), fileName, cvsSplitBy, startrow, complainType, complain, Util.getUserId());
                setUploadPollutionList(pollutionList);
            } else {
                FacesContext.getCurrentInstance().addMessage("uploadPollutionDataFrm:error_msg", new FacesMessage(" Uploaded file type problem(Accepted only csv file)", "  Uploaded file type problem(Accepted only csv file)"));
            }

        }

    }

    public List<UploadPollutionDataDobj> fileUploadData(UploadedFile uFile, String stateCode, String filename, String cvsSplitBy, int startrow, int complainType, String complain, String userId) {

        List<UploadPollutionDataDobj> uploadPollutionDataDobjList = new ArrayList();
        InputStream input = null;
        OutputStream output = null;
        String filePath = "/vahan_pollution_data";

        try {
            input = uFile.getInputstream();
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            output = new FileOutputStream(new File(filePath, filename));
            IOUtils.copy(input, output);

        } catch (IOException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
        } finally {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }
        BufferedReader br = null;
        String line = "";
        try {
            File dbinputFIle;
            dbinputFIle = new File(filePath + "/" + filename);
            FileReader fr = new FileReader(dbinputFIle);
            br = new BufferedReader(fr);
            String[] inputDataArr = null;
            int lineCount = 0;
            int j = 1;
            while ((line = br.readLine()) != null) {
                if (startrow > lineCount) {
                    lineCount++;
                } else {
                    inputDataArr = line.split(cvsSplitBy);
                    int length = inputDataArr.length;
                    UploadPollutionDataDobj uploadPollutionDataDobj = new UploadPollutionDataDobj();
                    String val = "";
                    uploadPollutionDataDobj.setState_cd(stateCode);
                    uploadPollutionDataDobj.setComplain_type(complainType);
                    uploadPollutionDataDobj.setComplain(complain);
                    uploadPollutionDataDobj.setEntered_by(userId);
                    uploadPollutionDataDobj.setSrNo(j);
                    for (int i = 0; i < length; i++) {
                        val = "";
                        val = inputDataArr[i];
                        if (i == 0) {
                            uploadPollutionDataDobj.setOff_cd(Integer.parseInt(val));
                        } else if (i == 1) {
                            uploadPollutionDataDobj.setRegn_no(val.trim().replaceAll("\\s+", ""));
                        } else if (i == 2) {
                            uploadPollutionDataDobj.setFir_dt(formatter.parse(val));
                        } else if (i == 3) {
                            uploadPollutionDataDobj.setFir_no(val);
                        }
                    }
                    uploadPollutionDataDobjList.add(uploadPollutionDataDobj);
                    lineCount++;
                    j++;
                }
            }
            br.close();
            fr.close();
            try {
                if (dbinputFIle.exists() && !dbinputFIle.isDirectory()) {
                    dbinputFIle.delete();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }

        } catch (IOException | NumberFormatException | ParseException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

        return uploadPollutionDataDobjList;
    }

    /**
     * @return the uploadPollutionList
     */
    public List<UploadPollutionDataDobj> getUploadPollutionList() {
        return uploadPollutionList;
    }

    /**
     * @param uploadPollutionList the uploadPollutionList to set
     */
    public void setUploadPollutionList(List<UploadPollutionDataDobj> uploadPollutionList) {
        this.uploadPollutionList = uploadPollutionList;
    }

    public void SaveDetails() {
        BlackListedVehicleImpl blackListedVehicleImpl = new BlackListedVehicleImpl();
        try {
            Map<Integer, Object> returnValuMap = blackListedVehicleImpl.SavePollutionData(uploadPollutionList);
            int totalupdatedRecord = 0;
            List<UploadPollutionDataDobj> failUploadPollutionDataDobjList = new ArrayList<>();
            List<UploadPollutionDataDobj> successUploadPollutionDataDobjList = new ArrayList<>();
            for (Map.Entry m : returnValuMap.entrySet()) {
                if (m.getKey().equals(1)) {
                    totalupdatedRecord = (int) m.getValue();
                } else if (m.getKey().equals(2)) {
                    failUploadPollutionDataDobjList = (List<UploadPollutionDataDobj>) m.getValue();
                } else if (m.getKey().equals(3)) {
                    successUploadPollutionDataDobjList = (List<UploadPollutionDataDobj>) m.getValue();
                }
            }
            setFailUploadPollutionList(failUploadPollutionDataDobjList);
            setSucessUploadPollutionList(successUploadPollutionDataDobjList);

            FacesContext.getCurrentInstance().addMessage("uploadPollutionDataFrm:error_msg", new FacesMessage(totalupdatedRecord + " Records Saved successfully", totalupdatedRecord + " Records Saved successfully"));
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(UploadPollutionDataBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * @return the failUploadPollutionList
     */
    public List<UploadPollutionDataDobj> getFailUploadPollutionList() {
        return failUploadPollutionList;
    }

    /**
     * @param failUploadPollutionList the failUploadPollutionList to set
     */
    public void setFailUploadPollutionList(List<UploadPollutionDataDobj> failUploadPollutionList) {
        this.failUploadPollutionList = failUploadPollutionList;
    }

    /**
     * @return the sucessUploadPollutionList
     */
    public List<UploadPollutionDataDobj> getSucessUploadPollutionList() {
        return sucessUploadPollutionList;
    }

    /**
     * @param sucessUploadPollutionList the sucessUploadPollutionList to set
     */
    public void setSucessUploadPollutionList(List<UploadPollutionDataDobj> sucessUploadPollutionList) {
        this.sucessUploadPollutionList = sucessUploadPollutionList;
    }
}
