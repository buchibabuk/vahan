/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.form.impl.Util;
import org.apache.log4j.Logger;
import org.primefaces.json.JSONObject;

/**
 *
 * @author ASHOK
 */
public class POSImpl {

    private static final Logger LOGGER = Logger.getLogger(POSImpl.class);

    public PosDobj getDL_POSRespose(String refNo) throws VahanException {

        if (refNo == null) {
            return null;
        }
        PosDobj posResponse = null;
        //String demoURL = "https://demo.ezetap.com/api/2.0/txn/details";
        //String appKeyDemo = "7d1f896f-8d48-4319-adf9-48b5c6e202d0";
        String prodURL = "https://www.ezetap.com/api/2.0/txn/details";
        String appKeyProd = "fdc1ef99-49b6-4932-a011-991f48da71b2";
        try {
            String userCd = Util.getEmpCode();
            URL url = new URL(prodURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            PosDobj posParam = new PosDobj();
            posParam.setAppKey(appKeyProd);//applKey
            posParam.setExternalRefNumber(refNo);//applNo
            posParam.setUsername(userCd);//UserName for demo "1314151617"
            JSONObject jSONObject = new JSONObject(posParam);
            String jsonStr = jSONObject.toString();
            //System.out.println("jsonString---" + jsonStr);

            StringBuilder requestParams = new StringBuilder();
            //System.out.println("The ezeTap URL ====>" + url);
            //System.out.println("The ezeTap param are ====>" + requestParams);
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(4000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            conn.setRequestProperty("Content-Type", "application/json");
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            //wr.writeBytes(requestParams.toString());
            wr.writeBytes(jsonStr);
            //wr.flush();
            //wr.close();
            int responseCode = conn.getResponseCode();
            if (conn.getResponseCode() != 200) {
                posResponse = null;
                throw new VahanException("Something Went Wrong during fetching Details of Payment by POS Machiine, Please Contact to the System Administrator");
            }
            InputStream stream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            stream.close();
            String response = sb.toString().trim();
            //System.out.println("ezeTAP ApplNo: " + applNo + "-" + response);

            if (response != null) {
                JSONObject nObject = new JSONObject(response);
                posResponse = new PosDobj();
                posResponse.setSuccess(nObject.get("success").toString());
                if (posResponse.getSuccess().equalsIgnoreCase("false")) {
                    System.out.println("ezeTAP RefNo: " + refNo + "-Success[" + nObject.get("success").toString() + "],Response Code[" + responseCode + "]");
                    throw new VahanException("POS Payment Details not found against RefNo# " + refNo + ", Please Swipe the Card Properly");
                } else if (posResponse.getSuccess().equalsIgnoreCase("true")) {
                    posResponse.setStatus(nObject.getString("status"));
                    if (!posResponse.getStatus().equalsIgnoreCase("AUTHORIZED")) {
                        System.out.println("ezeTAP ReflNo: " + refNo + "-Success[" + nObject.get("success").toString() + "],Response Code[" + responseCode + "],Status[" + nObject.getString("status") + "]");
                        throw new VahanException("POS Payment are not AUTHORIZED , Please try again");
                    } else if (posResponse.getStatus().equalsIgnoreCase("AUTHORIZED")) {
                        posResponse.setTxnId(nObject.getString("txnId"));
                        posResponse.setAuthCode(nObject.getString("authCode"));
                        posResponse.setRrNumber(nObject.getString("rrNumber"));
                        posResponse.setStatus(nObject.getString("status"));
                        posResponse.setTotalAmount(nObject.getLong("totalAmount"));
                        posResponse.setTid(nObject.getString("tid"));
                        posResponse.setExternalRefNumber(nObject.getString("externalRefNumber"));
                        posResponse.setCardLastFourDigit(nObject.getString("cardLastFourDigit"));
                        posResponse.setServiceResponseCode(String.valueOf(responseCode));
                        posResponse.setInvoiceNumber(nObject.getString("invoiceNumber"));
                        posResponse.setPostingDate(nObject.getLong("postingDate"));
                        posResponse.setChargeSlipDate(nObject.getString("chargeSlipDate"));

                        jSONObject = null;
                        jSONObject = new JSONObject(posResponse);
                        System.out.println("ezeTAP RefNo: " + refNo + "-Response Code[" + responseCode + "] Response-" + jSONObject.toString());
                    } else {
                        posResponse = null;
                    }
                }
            }

            if (response == null) {
                System.out.println("ezeTAP RefNo: " + refNo + "-Response Code-" + responseCode);
            }

        } catch (VahanException vex) {
            posResponse = null;
            throw vex;
        } catch (IOException ioe) {
            LOGGER.error(ioe.toString() + " " + ioe.getStackTrace()[0]);
            posResponse = null;
            throw new VahanException("Something Went Wrong during fetching Details of Payment by POS Machiine, Please Contact to the System Administrator");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            posResponse = null;
            throw new VahanException("Something Went Wrong during fetching Details of Payment by POS Machiine, Please Contact to the System Administrator");
        }
        return posResponse;
    }

    public void insertIntoVtPOSResponse(TransactionManager tmgr, PosDobj posResponseDobj) throws VahanException {
        PreparedStatement ps = null;
        String sql = null;
        try {
            if (posResponseDobj == null) {
                throw new VahanException("POS Payment Details not Found , Please Verify POS Paid Amount Again");
            }

            if (posResponseDobj.getApplNo() == null) {
                throw new VahanException("POS Payment Details Save Failed due to Application No Missing , Please Contact to the System Administrator");
            }

            sql = "INSERT INTO " + TableList.VT_POS_RESPONSE
                    + " (state_cd, off_cd, appl_no, ref_no, txn_id, status, response, op_dt)"
                    + "    VALUES (?, ?, ?, ?, ?, ?, ?::json, current_timestamp)";
            ps = tmgr.prepareStatement(sql);
            ps.setString(1, posResponseDobj.getStateCd());
            ps.setInt(2, posResponseDobj.getOffCd());
            ps.setString(3, posResponseDobj.getApplNo());
            ps.setString(4, posResponseDobj.getExternalRefNumber());
            ps.setString(5, posResponseDobj.getTxnId());
            ps.setString(6, posResponseDobj.getServiceResponseCode());
            JSONObject jSONObject = new JSONObject(posResponseDobj);//converting response to json format
            ps.setString(7, jSONObject.toString());
            int resp = ps.executeUpdate();
            if (resp <= 0) {
                throw new VahanException("Something Went Wrong during Updation of POS Details, Please Contact to System Administrator");
            }

        } catch (VahanException vex) {
            throw vex;
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Something Went Wrong during POS Amount Updation or Payment is Already Paid with this Reference No, Please Contact to System Administrator");
        }
    }
}
