/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.common.tax;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
import org.apache.log4j.Logger;
//import tax.web.service.VahanTaxParameters;
//import tax.web.service.Vahantax;
//import tax.web.service.Vahantax_Service;

/**
 * Utility Class to handle the web service operation for calling the tax Service
 * to calculate Tax.
 * <br>
 * All Cases where tax needed be be calculated using web service , use this
 * class only.
 *
 * @author Prashant Kumar Singh
 */
public class VahanTaxClient {

    private static Logger LOGGER = Logger.getLogger(VahanTaxClient.class);

    /**
     * Method to get tax details via web service url
     *
     * @param taxParams VahanTaxParameters Object : All Parameters needed for
     * tax calculation encapsulated as an entity and passed
     * @return Tax Service Response xml
     */
//    public java.lang.String getTaxDetails(VahanTaxParameters taxParams) {
//        Vahantax_Service taxService = new Vahantax_Service();
//        Vahantax taxServer = taxService.getVahantaxPort();
//
//        String taxServiceResponse = taxServer.getTaxDetails(taxParams);
//        return taxServiceResponse;
//    }
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
     * @see VahanTaxClient.getTaxDetails
     */
    public List<DOTaxDetail> parseTaxResponse(String taxserviceResponse, int taxPurCd, String taxMode) throws VahanException {
        List<DOTaxDetail> taxList = null;
        if (Utility.isNullOrBlank(taxserviceResponse)) {
            return taxList;
        }

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(DOTaxDetails.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StreamSource streamSource = new StreamSource(new StringReader(taxserviceResponse));
            DOTaxDetails dobj = (DOTaxDetails) unmarshaller.unmarshal(streamSource);
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
        } catch (JAXBException e) {
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
