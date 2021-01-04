/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.services.insurance;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import nic.vahan.services.insurance.dobj.InsuranceInfoDobj;
import java.text.SimpleDateFormat;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.InsDobj;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author ASHOK
 */
public class InsuranceDetailService {

    private static final Logger LOGGER = Logger.getLogger(InsuranceDetailService.class);

    public InsDobj getInsuranceDetailsByService(String regn_no, String state_cd, int off_cd) throws VahanException, IOException {
        InsDobj insDobj = null;
        InsuranceInfoDobj insuranceInfoDobj = null;
        try {
            //regn_no="AP05DG7123";
            String urlParameters = "regnNo=" + regn_no;
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;
            String BASE_URI = ServerUtil.getVahanPgiUrl("INSURANCE_URL");
            // String BASE_URI = "https://staging.parivahan.gov.in/vahanInsuranceDataWS/insuranceInfo/";
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            URL obj = new URL(BASE_URI);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(15000);

            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Accept", "application/json");
            // Send post request
            con.setDoOutput(true);
            con.setInstanceFollowRedirects(false);
            con.setRequestProperty("charset", "utf-8");
            con.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            con.setUseCaches(false);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(postData);
                wr.flush();
            }
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                if (!response.toString().contains("No record found against this regn no")) {
                    insuranceInfoDobj = mapper.readValue(response.toString(), InsuranceInfoDobj.class);
                    if (insuranceInfoDobj != null) {
                        insDobj = getInsDobjFromInsuranceInfoDobj(regn_no, state_cd, off_cd, insuranceInfoDobj);
                    }
                }
            }
        } catch (IOException ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0] + " regn_no: " + regn_no + " - ");
        }
        return insDobj;
    }

    public InsDobj getInsDobjFromInsuranceInfoDobj(String regnNo, String stateCd, int offCd, InsuranceInfoDobj insuranceInfoDobj) {
        InsDobj insDobj = null;
        try {
            if (insuranceInfoDobj != null) {
                insDobj = new InsDobj();
                insDobj.setRegn_no(regnNo);
                insDobj.setState_cd(stateCd);
                insDobj.setOff_cd(offCd);
                //insDobj.setIdv(0);
                if (insuranceInfoDobj.getInsuranceType() > 0) {
                    insDobj.setIns_type(insuranceInfoDobj.getInsuranceType());
                } else {
                    insDobj.setIns_type(2);
                }
                insDobj.setComp_cd(insuranceInfoDobj.getIssuerCd());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                if (insuranceInfoDobj.getInsFrom() != null) {
                    insDobj.setIns_from(format.parse(insuranceInfoDobj.getInsFrom()));
                } else {
                    insDobj.setIns_from(null);
                }
                if (insuranceInfoDobj.getInsUpto() != null) {
                    insDobj.setIns_upto(format.parse(insuranceInfoDobj.getInsUpto()));
                } else {
                    insDobj.setIns_upto(null);
                }
                insDobj.setPolicy_no(insuranceInfoDobj.getPolicyNo());
                insDobj.setIibData(insuranceInfoDobj.isIibData());
                insDobj.setIdv(insuranceInfoDobj.getIdv());

            }
        } catch (ParseException ex) {
            insDobj = null;
            LOGGER.error(ex.toString() + "" + ex.getStackTrace()[0]);
        }
        return insDobj;
    }

//    public static void main(String[] args) {
//        try {
//            getInsuranceDetailsByService("AP01AD4044", "DL", 1);
//        } catch (VahanException | IOException ex) {
//            java.util.logging.Logger.getLogger(InsuranceDetailService.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}
