/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services;

/**
 *
 * @author nicsi
 */
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.RowSet;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.connection.TransactionManagerReadOnly;
import nic.vahan.server.CommonUtils;
import nic.vahan.server.ServerUtil;
import nic.vahan.services.clients.DocumentUploadClient;
import nic.vahan.services.clients.dobj.RequestDto;
import nic.vahan.services.clients.dobj.ResponseDocument;
import org.apache.log4j.Logger;

public class DmsDocCheckUtils extends Throwable {

    private static final Logger LOGGER = Logger.getLogger(DmsDocCheckUtils.class);

    public static List<VTDocumentModel> getList(String urls, String applicationNo) throws VahanException {
        String output = "";
        StringBuilder success = new StringBuilder();
        List<VTDocumentModel> lists = new ArrayList<VTDocumentModel>();
        HttpURLConnection conn = null;
        BufferedReader br = null;
        URL url = null;
        try {
            String serviceURL = ServerUtil.getVahanPgiUrl(TableConstants.DMS_SERVICE_URL);
            if (!CommonUtils.isNullOrBlank(serviceURL)) {
                url = new URL(serviceURL + "vtdocumentlist/" + applicationNo);
            } else {
                throw new VahanException("DMS service URL not found.");
            }
            if (url != null) {
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(45000);
                conn.setRequestProperty("Accept", "application/json");
                if (conn.getResponseCode() != 200) {
                    throw new VahanException("DMS URL not responding. Please try after sometime.");
                }
                br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                if (br != null && output != null) {
                    while ((output = br.readLine()) != null) {
                        success.append(output);
                    }
                }
                Gson gson = new Gson();
                String data = success.toString();
                VTDocumentModelDto responses = gson.fromJson(data, VTDocumentModelDto.class);
                if (responses != null) {
                    lists = responses.getVtDocumentModelList();
                } else {
                    throw new VahanException("Unable to get response from DMS.");
                }
            }
            return lists;
        } catch (VahanException vex) {
            throw vex;
        } catch (SocketTimeoutException | ConnectException vex) {
            throw new VahanException("DMS Connection timeout. Please try after sometime.");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (conn != null) {
                    conn.disconnect();
                }
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
    }

    public static Integer countMandatoryDocs(String urls, String stateCd, int purCd) throws VahanException {
        String output = "";
        StringBuilder success = new StringBuilder();
        Integer countManDocs = 0;
        HttpURLConnection conn = null;
        BufferedReader br = null;
        try {
            //String urlss = "http://164.100.78.110/dms-app/getdocumentmandatoryornot/UP18050200512159";
            URL url = new URL(urls + "/getdocumentmandatoryornot/" + stateCd + "/" + purCd);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(45000);
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new VahanException("DMS URL not responding. Please try after sometime.");
            }
            br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            while ((output = br.readLine()) != null) {
                success.append(output);
            }
            conn.disconnect();
            String data = "0";
            if (success != null) {
                data = success.toString();
            }
            countManDocs = Integer.parseInt(data);
        } catch (VahanException vex) {
            throw vex;
        } catch (SocketTimeoutException vex) {
            throw new VahanException("DMS Connection timeout. Please try after sometime.");
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        } finally {
            try {
                if (conn != null) {
                    conn.disconnect();
                }
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return countManDocs;
    }

    public static List<VTDocumentModel> getDocumentsDescrp(String applNo) throws VahanException {
        List<VTDocumentModel> documentList;
        try {
            documentList = getList("", applNo);
        } catch (VahanException ve) {
            throw ve;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return documentList;
    }

    public static List<VTDocumentModel> getUploadedDocumentList(String applNos) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        List<VTDocumentModel> documentList = new ArrayList<>();
        VTDocumentModel vtDocumentDobj = null;
        try {
            String sql = " select appl_no, regn_no, doc_url, doc_verified, doc_approved, temp_doc_approved, doc_desc from get_dms_doc_details(?)";
            tmgr = new TransactionManagerReadOnly("getUploadedDocumentList");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, applNos);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                vtDocumentDobj = new VTDocumentModel();
                vtDocumentDobj.setAppl_no(rs.getString("appl_no"));
                vtDocumentDobj.setRegn_no(rs.getString("regn_no"));
                vtDocumentDobj.setDoc_url(rs.getString("doc_url"));
                vtDocumentDobj.setDoc_verified(rs.getBoolean("doc_verified"));
                vtDocumentDobj.setDoc_approved(rs.getBoolean("doc_approved"));
                vtDocumentDobj.setTemp_doc_approved(rs.getBoolean("temp_doc_approved"));
                vtDocumentDobj.setDoc_desc(rs.getString("doc_desc"));
                documentList.add(vtDocumentDobj);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem in getting Uploaded Documents details.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return documentList;
    }

    public static int getMandatoryDocumentCount(String stateCd, int pur_cd) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        int mandatoryCount = 0;
        try {
            String sql = "select count(distinct doc_catg) AS totalMandatory from get_dms_required_documents(?, ?) where mandatory_doc='Y';";

            tmgr = new TransactionManagerReadOnly("getMandatoryDocumentCount");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, pur_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                mandatoryCount = rs.getInt("totalMandatory");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem in getting Mandatory documents details.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return mandatoryCount;
    }

    public static int getTotalDocumentCount(String stateCd, int pur_cd) throws VahanException {
        TransactionManagerReadOnly tmgr = null;
        PreparedStatement ps = null;
        int mandatoryCount = 0;
        try {
            String sql = "select count(distinct doc_catg) AS totalDocument from get_dms_required_documents(?, ?);";

            tmgr = new TransactionManagerReadOnly("getTatalDocumentCount");
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, stateCd);
            ps.setInt(2, pur_cd);
            RowSet rs = tmgr.fetchDetachedRowSet();
            while (rs.next()) {
                mandatoryCount = rs.getInt("totalDocument");
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Problem in getting Total documents details.");
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
        return mandatoryCount;
    }

    public ResponseDocument callDocumentApiToGetDocListToBeUpload(String applNo, String stateCd, String regnNo, String offName, int purCd, boolean docUploadAtRTO, int ownerCode) throws VahanException {
        Gson gson = new Gson();
        ResponseDocument docListResponse = null;
        try {
            String dmsGetDocListURL = ServerUtil.getVahanPgiUrl(TableConstants.DMS_GET_DOCLIST);

            if (!CommonUtils.isNullOrBlank(dmsGetDocListURL)) {
                dmsGetDocListURL = dmsGetDocListURL.replace("ApplNo", applNo).replace("state_cd", stateCd).replace("regn_no", regnNo)
                        .replace("off_name", offName).replace("pur_cd", purCd + "");

                if (docUploadAtRTO) {
                    dmsGetDocListURL = dmsGetDocListURL + "&docFrom=R" + TableConstants.SECURITY_KEY;
                } else {
                    if (stateCd != null && stateCd.equals("OR")) {
                        dmsGetDocListURL = dmsGetDocListURL + "&owCode=" + ownerCode + "" + TableConstants.SECURITY_KEY;
                    } else {
                        dmsGetDocListURL = dmsGetDocListURL + TableConstants.SECURITY_KEY;
                    }
                }
            } else {
                throw new VahanException("DMS Get Doc List Service URL not found.");
            }
            if (dmsGetDocListURL != null) {
                DocumentUploadClient docClient = new DocumentUploadClient();
                String docList = docClient.getDocumentListToBeUpload(dmsGetDocListURL);
                if (docList != null && !docList.isEmpty()) {
                    docListResponse = gson.fromJson(docList, ResponseDocument.class);
                    if (docListResponse == null) {
                        throw new VahanException("Unable to get response from DMS.");
                    }
                } else {
                    throw new VahanException("Unable to get response from DMS.");
                }
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return docListResponse;
    }

    public String callDocumentApiForViewDocument(String objectId) throws VahanException {
        String dmsViewURL = null;
        try {
            dmsViewURL = ServerUtil.getVahanPgiUrl(TableConstants.DMS_VIEW_URL);

            if (!CommonUtils.isNullOrBlank(dmsViewURL)) {
                dmsViewURL = dmsViewURL + objectId;
            } else {
                throw new VahanException("DMS View Service URL not found.");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return dmsViewURL;
    }

    public RequestDto callDocumentApiForDocumentUploadAndUpdate(RequestDto requestDto, String urlConst) throws VahanException {
        Gson gson = new Gson();
        RequestDto responseDto = null;
        try {
            String dmsUploadAndUpdateURL = ServerUtil.getVahanPgiUrl(urlConst);

            if (!CommonUtils.isNullOrBlank(dmsUploadAndUpdateURL)) {
                DocumentUploadClient docClient = new DocumentUploadClient();
                String jsonReqObj = gson.toJson(requestDto);
                if (jsonReqObj != null) {
                    String rs = docClient.uploadDoc(String.class, jsonReqObj, dmsUploadAndUpdateURL);
                    if (rs != null && !rs.isEmpty()) {
                        responseDto = gson.fromJson(rs, RequestDto.class);
                        if (responseDto == null) {
                            throw new VahanException("Unable to get response from DMS.");
                        }
                    } else {
                        throw new VahanException("Unable to get response from DMS.");
                    }
                } else {
                    throw new VahanException("Unable to request DMS For Upload");
                }
            } else {
                throw new VahanException("DMS Upload Service URL not found.");
            }
        } catch (VahanException vex) {
            throw vex;
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            throw new VahanException(TableConstants.SomthingWentWrong);
        }
        return responseDto;
    }
}
