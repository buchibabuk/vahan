/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean.hsrp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import nic.vahan.common.jsf.utils.JSFUtils;
import nic.vahan.db.TableConstants;
import nic.vahan.form.dobj.hsrp.HSRPFileUploadDobj;
import nic.vahan.form.impl.Util;
import nic.vahan.form.impl.hsrp.HSRPFileUploadImpl;
import org.primefaces.event.FileUploadEvent;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC075
 */
@ManagedBean(name = "fileUploadBean")
@ViewScoped
public class HSRPFileUploadBean implements Serializable {

    private static final Logger LOGGER = Logger.getLogger(HSRPFileUploadBean.class);
    private String txtFileName = "";
//    private String filePath = "D:/vahan4/hsrp/";
    private String filePath = "/vahan4/hsrp/";
    private String fileName = "";
    byte[] data;
    private List recordsToBeUploadedList = null;
    private List lineNumbers = null;
    private List recordsToBeUploadedRegNoList = null;
    private List recordsToBeUploadedFrontLaserList = null;
    private List recordsToBeUploadedRearLaserList = null;
    private List validationErrorList = new ArrayList();
    private String validationLevel = "";
    FacesMessage message = null;
    private String stateCode;
    private int offCode;
    private boolean forAllStates = false;
    String userCatg = "";

    @PostConstruct
    private void init() {
        stateCode = (Util.getUserStateCode());
        offCode = (Util.getSelectedSeat().getOff_cd());
        userCatg = Util.getUserCategory();
        if (userCatg.endsWith(TableConstants.USER_CATG_STATE_HSRP)) {
            forAllStates = true;
        }
    }

    public void listener(FileUploadEvent event) {
        try {
            if (true) {
                this.txtFileName = event.getFile().getFileName();
                if (txtFileName != null && txtFileName.length() > 0) {
                    txtFileName = getSaveFileName(txtFileName);
                }
                if (txtFileName.endsWith(".txt")) {
                    this.fileName = txtFileName.substring(0, txtFileName.length() - 4);
                    if (txtFileName != null && txtFileName.length() > 0) {
                        if (true) {
                            this.data = InputToByte(event.getFile().getInputstream());
                            if (data == null || data.length <= 0) {
                                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, " File contains no records.  ", null);
                                FacesContext.getCurrentInstance().addMessage(null, message);
                            } else {
                                boolean isUploaded = false;
                                if (!isUploaded) {
                                    File writefile = new File(filePath + txtFileName);
                                    FileOutputStream foStream = new FileOutputStream(writefile);
                                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                                    b.write(data);
                                    b.writeTo(foStream);
                                    boolean errorInFileName = false;
                                    String fileNameError = "";
                                    String fileAlreadyExistError = "";
                                    if (!validateFileName(this.fileName)) {
                                        errorInFileName = true;
                                        fileNameError = ("File Name Format should be unique Alphanumeric (without special chars & spaces) with .txt extension and length should be less than 30");
                                    }

                                    if (errorInFileName) {
                                        message = new FacesMessage(FacesMessage.SEVERITY_ERROR, fileNameError + fileAlreadyExistError, null);
                                        FacesContext.getCurrentInstance().addMessage(null, message);
                                    }
                                    if (!errorInFileName) {
                                        HashMap manufacturerFileDetails = getManufacturerTableEntryDetails(txtFileName.toLowerCase());
                                        String err = (String) manufacturerFileDetails.get("err");
                                        String line = (String) manufacturerFileDetails.get("line");
                                        if (err.equalsIgnoreCase("specialChar")) {
                                            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Line-" + line + ": File contains invalid Data(Special Characters)", null);
                                            FacesContext.getCurrentInstance().addMessage(null, message);
                                            return;
                                        }
                                        if (!err.equalsIgnoreCase("Line no -")) {
                                            ArrayList<HSRPFileUploadDobj> manufacturerFileTrans = (ArrayList<HSRPFileUploadDobj>) manufacturerFileDetails.get("Data");
                                            HashMap bankFiles = null;
                                            validationErrorList.clear();
                                            validationLevel = "";
                                            if (validateFileRecords(manufacturerFileTrans)) {
                                                bankFiles = saveManufacturerFileRecordsInBulk(manufacturerFileTrans);
                                                boolean isSuccess = (Boolean) bankFiles.get("result");
                                                if (isSuccess) {
                                                    message = new FacesMessage(FacesMessage.SEVERITY_INFO, "File " + txtFileName.trim() + " is uploaded successfully", null);
                                                    FacesContext.getCurrentInstance().addMessage(null, message);
                                                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                                    Calendar cal = Calendar.getInstance();

                                                } else {
                                                    File f = new File(filePath + this.fileName + ".txt");
                                                    boolean bool = f.renameTo(new File(filePath + this.fileName + "_wrong.txt"));
                                                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "  File " + this.fileName + " is not uploaded Please try after some time.  ", null);
                                                    FacesContext.getCurrentInstance().addMessage(null, message);
                                                }
                                            } else {
                                                File f = new File(filePath + this.fileName + ".txt");
                                                f.renameTo(new File(filePath + this.fileName + "_wrong.txt"));
                                                if (validationLevel.equals("level_1")) {
                                                } else if (validationLevel.equals("level_2")) {
                                                }
                                                for (Object validationError : validationErrorList) {
                                                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, (String) validationError, null);
                                                    FacesContext.getCurrentInstance().addMessage(null, message);
                                                }
                                            }
                                        } else {
                                            return;
                                        }

                                    }
                                } else {
                                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, " Duplicate File Name, already uploaded the file with this same   ", null);
                                    FacesContext.getCurrentInstance().addMessage(null, message);
                                }
                            }
                        }
                    } else {
                    }
                } else {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "  File Name Format should be unique Alphanumeric (without special chars & spaces) with .txt extension and length should be less than 30  ", null);
                    FacesContext.getCurrentInstance().addMessage(null, message);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "  File Uploaded Contains Error. The following records are not in proper format. ReLoad File with Rectified Values separate by semi colon(;).  ", null);
            FacesContext.getCurrentInstance().addMessage(null, message);
            File f = new File(filePath + this.fileName + ".txt");
            f.renameTo(new File(filePath + this.fileName + "_wrong.txt"));
        }
    }

    private String getSaveFileName(String fileName) {
        String rtnFileName = "";
        if (fileName != null && fileName.length() > 0) {
            fileName = fileName.toLowerCase();
            int txtIndex = fileName.indexOf(".txt");
            if (txtIndex > 0) {
                rtnFileName = fileName;
                int slashIndex = fileName.lastIndexOf('/');
                if (slashIndex > 0) {
                    rtnFileName = fileName.substring(slashIndex + 1);
                }
                slashIndex = 0;
                slashIndex = fileName.lastIndexOf('\\');
                if (slashIndex > 0) {
                    rtnFileName = fileName.substring(slashIndex + 1);
                }
            }
        }
        return rtnFileName;
    }

    public static byte[] InputToByte(InputStream file) throws FileNotFoundException, IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            for (int readNum; (readNum = file.read(buf)) != -1;) {
                bos.write(buf, 0, readNum);
            }
        } catch (IOException ex) {
        }
        byte[] bytes = bos.toByteArray();

        return bytes;
    }

    private boolean validateFileName(String fileName) {
        if (JSFUtils.isAlphaNumeric(fileName)) {
            int len = fileName.length();
            if (len <= 30) {
                for (int i = 0; i < len; i++) {
                    if ((Character.isSpaceChar(fileName.charAt(i)))) {
                        return false;
                    }
                }
            } else {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean checkSpecialChar(String s) {

        if (s == null || s.trim().isEmpty()) {
            return false;
        }
        Pattern p = Pattern.compile("[^.a-z0-9 ;-]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(s);
        if (matcher.find()) {
            return true;//means special character
        } // boolean b = m.matches();
        else {
            //means no speacial character
            return false;
        }

    }

    public HashMap getManufacturerTableEntryDetails(String fileName) {
        ArrayList<HSRPFileUploadDobj> dobjs = new ArrayList<HSRPFileUploadDobj>();
        recordsToBeUploadedList = new ArrayList();
        recordsToBeUploadedFrontLaserList = new ArrayList();
        recordsToBeUploadedRearLaserList = new ArrayList();
        lineNumbers = new ArrayList();
        recordsToBeUploadedRegNoList = new ArrayList();
        String err = "";
        HashMap hm = new HashMap();
        if (fileName != null && fileName.length() > 0) {
            FacesContext fcontext = FacesContext.getCurrentInstance();
            ServletContext scontext = (ServletContext) fcontext.getExternalContext().getContext();
            String inputPath = scontext.getRealPath("/hsrp/" + fileName);
            boolean emptyFile = true;
            File inputFile = new File(filePath + fileName);
            if (inputFile.exists()) {
                try {
                    BufferedReader input = new BufferedReader(new FileReader(inputFile));
                    try {
                        String line = null;
                        String[] str = null;
                        String[] str1 = null;
                        int i = 1;
                        HSRPFileUploadDobj dobj = null;
                        while ((line = input.readLine()) != null) {
                            if (checkSpecialChar(line)) {
                                err = "specialChar";
                                hm.put("err", err);
                                hm.put("line", String.valueOf(i));
                                return hm;
                            }
                            if (line.equalsIgnoreCase("")) {
                                continue;
                            }
                            try {
                                emptyFile = false;
                                line = line.trim();
                                str = line.split(";");
                            } catch (Exception e) {
                                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                            }
                            for (int j = 0; j < str.length; j++) {
                                if (str[j] != null) {
                                    if (str[j].contains("ï»¿")) {
                                        str[j] = str[j].replace("ï»¿", "");
                                    }
                                    str[j] = str[j].trim();
                                }
                            }
                            if (str != null && str.length == 8) {
                                lineNumbers.add(false);
                                recordsToBeUploadedList.add(str[0]);
                                recordsToBeUploadedRegNoList.add(str[1]);
                                recordsToBeUploadedFrontLaserList.add(str[3]);
                                recordsToBeUploadedRearLaserList.add(str[4]);
                                try {
                                    dobj = new HSRPFileUploadDobj();
                                    dobj.setApplNo(str[0]);
                                    dobj.setRegnNo(str[1]);
                                    dobj.setHsrpFlag(str[2]);
                                    dobj.setHsrpNoFront(str[3]);
                                    dobj.setHsrpNoBack(str[4]);
                                    dobj.setHsrpFixDt(str[5]);
                                    dobj.setHsrpFixAmt(str[6]);
                                    dobj.setHsrpAmtTakenOn(str[7]);
                                    dobjs.add(dobj);
                                } catch (Exception e) {
                                    err = "Line no -";
                                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), null);
                                    FacesContext.getCurrentInstance().addMessage(null, message);
                                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                                }
                                i++;
                            } else {
                                lineNumbers.add(true);
                                try {
                                    dobj = new HSRPFileUploadDobj();
                                    for (int k = 0; k < str.length; k++) {
                                        dobj.setSerialNo(i);
                                        if (k == 0) {
                                            recordsToBeUploadedList.add(str[0]);
                                            dobj.setApplNo(str[0]);
                                        } else if (k == 1) {
                                            recordsToBeUploadedRegNoList.add(str[1]);
                                            dobj.setRegnNo(str[1]);
                                        } else if (k == 2) {
                                            dobj.setHsrpFlag(str[2]);
                                        } else if (k == 3) {
                                            recordsToBeUploadedFrontLaserList.add(str[3]);
                                            dobj.setHsrpNoFront(str[3]);
                                        } else if (k == 4) {
                                            recordsToBeUploadedRearLaserList.add(str[4]);
                                            dobj.setHsrpNoBack(str[4]);
                                        } else if (k == 5) {
                                            dobj.setHsrpFixDt(str[5]);
                                        } else if (k == 6) {
                                            dobj.setHsrpFixAmt(str[6]);
                                        } else if (k == 7) {
                                            dobj.setHsrpAmtTakenOn(str[7]);
                                        }
                                    }
                                    dobjs.add(dobj);
                                } catch (Exception e) {
                                    err = "Line no -";
                                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), null);
                                    FacesContext.getCurrentInstance().addMessage(null, message);
                                    LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                                    line = line.replaceAll("<", "");
                                    line = line.replaceAll(">", "");
                                }

                            }
                        }
                        if (emptyFile) {
                            err = "Line no -";
                            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, " File contains no records", null);
                            FacesContext.getCurrentInstance().addMessage(null, message);
                        }
                        if (!err.contains("Line no -")) {
                            err = "";
                        }
                    } finally {
                        input.close();
                    }
                } catch (IOException ex) {
                    LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
                }
            }
        }
        hm.put("Data", dobjs);
        hm.put("err", err);
        return hm;

    }

    public boolean validateFileRecords(ArrayList<HSRPFileUploadDobj> list) {
        validationErrorList.clear();
        List errorList = new HSRPFileUploadImpl().validateFile(list, recordsToBeUploadedRegNoList, recordsToBeUploadedFrontLaserList, recordsToBeUploadedRearLaserList, lineNumbers, offCode);
        if (!errorList.isEmpty()) {
            validationLevel = "level_2";
            for (Object error : errorList) {
                validationErrorList.add((String) error);
            }
            return (validationErrorList.isEmpty());
        }
        return (validationErrorList.isEmpty());
    }

    public HashMap saveManufacturerFileRecordsInBulk(ArrayList<HSRPFileUploadDobj> list) {
        String res = null;
        boolean result = false;
        HashMap map = new HashMap();
        try {
            res = new HSRPFileUploadImpl().processHSRFEntryTableForBulk(list, this.fileName);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, " Error found while saving record:" + ex.getMessage(), null);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        if (res != null && res.contains("SUCCESS")) {
            result = true;
        }
        map.put("result", result);
        return map;
    }
}
