/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.common.tax;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import nic.rto.vahan.common.VahanException;
import nic.vahan.CommonUtils.Utility;
import nic.vahan.form.dobj.DOTaxDetail;
import nic.vahan.form.dobj.DOTaxDetails;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import nic.vahan5.reg.server.ServerUtility;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Utility Class to handle the web service operation for calling the tax Service
 * to calculate Tax. All Cases where tax needed be be calculated using web
 * service , use this class only.
 *
 * @author Kartikey Singh
 */
public class VahanTaxClientRest {

    private static Logger LOGGER = Logger.getLogger(nic.vahan.common.tax.VahanTaxClient.class);

    private WebResource webResource;
    private Client client;
    private static String BASE_URI = "http://127.0.0.1:{port}/vahantaxws/webresources/";//Production

    public VahanTaxClientRest() throws VahanException {
        String baseURL = null;
        try {
//            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
//            int serverPort = request.getLocalPort();
            // Need to change this                        
            int serverPort = 80;
            BASE_URI = BASE_URI.replace("{port}", serverPort + "");
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Can not establish connection with Tax Service");
        }

    }

    public String mvTax_JSON_STRING(String requestEntity) throws VahanException {
        String status = "";
        try {
            //return webResource.type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(responseType, requestEntity);

            String baseURL = BASE_URI + "mvtax/taxDetails";

            URL obj = new URL(baseURL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setConnectTimeout(15000);
            //con.setReadTimeout(120000);
            // add reuqest header
            con.setRequestMethod("POST");
            //con.setRequestProperty("User-Agent", USER_AGENT);
            //con.setRequestProperty("Authorization", "Bearer " + TOKEN);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            // Send post request
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(requestEntity.getBytes("UTF-8"));
            os.flush();
            os.close();
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
//                HttpEntity httpEntity = httpresponse.getEntity();
//                status = EntityUtils.toString(httpEntity);
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                status = response.toString();

            } else {
                throw new VahanException("Can not establish connection with Tax Service");
            }
        } catch (VahanException ex) {
            throw ex;
            //java.util.logging.Logger.getLogger(VahanTaxRestClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            LOGGER.error(ex.toString() + " " + ex.getStackTrace()[0]);
            throw new VahanException("Can not establish connection with Tax Service");

        }
        return status;

    }

    /**
     * Method to parse the tax service response xml
     *
     * @param taxserviceResponse Tax Service response from the web service
     * @param taxPurCd
     * @param taxMode
     * @return List of DOTaxDetail objects containing tax details or null if tax
     * service response String is null or if unable to parse the tax service
     * response
     * @throws VahanException
     * @since 28-OCT-2014
     * @see VahanTaxClientRest.getTaxDetails
     */
    public List<DOTaxDetail> parseTaxResponse(String taxserviceResponse, int taxPurCd, String taxMode) throws VahanException {
        List<DOTaxDetail> taxList = null;
        if (Utility.isNullOrBlank(taxserviceResponse)) {
            return taxList;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            DOTaxDetails dobj = mapper.readValue(taxserviceResponse, DOTaxDetails.class);
            String taxHead = ServerUtil.getTaxHead(taxPurCd);
            taxList = dobj.getTaxList();
            DateFormat parser = new SimpleDateFormat("dd-MM-yyyy");
            if (taxList != null) {
                for (DOTaxDetail taxDobj : taxList) {
                    Date dateFrom = (Date) parser.parse(taxDobj.getTAX_FROM());
                    String writedateFrom = new SimpleDateFormat("dd-MMM-yyyy").format(dateFrom);
                    Date dateUpto = (Date) parser.parse(taxDobj.getTAX_UPTO());
                    String writedateUpto = "";
                    // Add by Afzal on 17 June 2016. To display the OTT in Known Your MV Tax form.
                    if (Util.getUserStateCode() == null) {
                        if ("S,O,L".contains(taxMode)) {
                            writedateUpto = "One Time Tax";
                        } else {
                            writedateUpto = new SimpleDateFormat("dd-MMM-yyyy").format(dateUpto);
                        }
                    } else {
                        writedateUpto = new SimpleDateFormat("dd-MMM-yyyy").format(dateUpto);
                    }

                    taxDobj.setTAX_HEAD(taxHead);
                    taxDobj.setPUR_CD(taxPurCd);
                    taxDobj.setTAX_MODE(taxMode);
                    taxDobj.setTAX_FROM(writedateFrom);
                    taxDobj.setTAX_UPTO(writedateUpto);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            taxList = null;
            throw new VahanException("Unable to Parse Tax Response");
        } catch (ParseException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            taxList = null;
            throw new VahanException("Unable to Parse Tax Date Response");
        }

        return taxList;

    }

    /**
     * @author Kartikey Singh Method to parse the tax service response xml
     *
     * @param taxserviceResponse Tax Service response from the web service
     * @param taxPurCd
     * @param taxMode
     * @return List of DOTaxDetail objects containing tax details or null if tax
     * service response String is null or if unable to parse the tax service
     * response
     * @throws VahanException
     * @since 28-OCT-2014
     * @see VahanTaxClientRest.getTaxDetails
     */
    public List<DOTaxDetail> parseTaxResponse(String taxserviceResponse, int taxPurCd, String taxMode, String stateCode) throws VahanException {
        List<DOTaxDetail> taxList = null;
        if (Utility.isNullOrBlank(taxserviceResponse)) {
            return taxList;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            DOTaxDetails dobj = mapper.readValue(taxserviceResponse, DOTaxDetails.class);
            String taxHead = ServerUtility.getTaxHead(taxPurCd);
            taxList = dobj.getTaxList();
            DateFormat parser = new SimpleDateFormat("dd-MM-yyyy");
            if (taxList != null) {
                for (DOTaxDetail taxDobj : taxList) {
                    Date dateFrom = (Date) parser.parse(taxDobj.getTAX_FROM());
                    String writedateFrom = new SimpleDateFormat("dd-MMM-yyyy").format(dateFrom);
                    Date dateUpto = (Date) parser.parse(taxDobj.getTAX_UPTO());
                    String writedateUpto = "";
                    // Add by Afzal on 17 June 2016. To display the OTT in Known Your MV Tax form.
                    if (stateCode == null) {
                        if ("S,O,L".contains(taxMode)) {
                            writedateUpto = "One Time Tax";
                        } else {
                            writedateUpto = new SimpleDateFormat("dd-MMM-yyyy").format(dateUpto);
                        }
                    } else {
                        writedateUpto = new SimpleDateFormat("dd-MMM-yyyy").format(dateUpto);
                    }

                    taxDobj.setTAX_HEAD(taxHead);
                    taxDobj.setPUR_CD(taxPurCd);
                    taxDobj.setTAX_MODE(taxMode);
                    taxDobj.setTAX_FROM(writedateFrom);
                    taxDobj.setTAX_UPTO(writedateUpto);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            taxList = null;
            throw new VahanException("Unable to Parse Tax Response");
        } catch (ParseException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            taxList = null;
            throw new VahanException("Unable to Parse Tax Date Response");
        }

        return taxList;

    }
}
