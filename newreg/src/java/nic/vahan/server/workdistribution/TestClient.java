package nic.vahan.server.workdistribution;

import java.io.StringReader;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;
import com.sun.org.apache.xerces.internal.dom.*;

public class TestClient {

    private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(TestClient.class);

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try {

            GetInitialflowforVahanWSStub stub = new GetInitialflowforVahanWSStub();

            GetInitialflowforVahanWSStub.GetInitialflowforVahanws getInitialflowforVahanws6 = new GetInitialflowforVahanWSStub.GetInitialflowforVahanws(); 
            // TODO : Fill in the getInitialflowforVahanws6 here

            getInitialflowforVahanws6.setApplno("KL/SEP/3");
            getInitialflowforVahanws6.setPrtoCd("1");
            getInitialflowforVahanws6.setPstCd("KL");
            getInitialflowforVahanws6.setTrcdslst(new int[]{1}); // purpose code

            LOGGER.info("..Caling web service second time");
            GetInitialflowforVahanWSStub.GetInitialflowforVahanwsResponse rs = stub.getInitialflowforVahanws(getInitialflowforVahanws6);

            LOGGER.info("response : " + rs.get_return());

            String response = rs.get_return();
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            InputSource source = new InputSource(new StringReader(response));
            DeferredDocumentImpl doc = (DeferredDocumentImpl) xpath.evaluate("/", source, XPathConstants.NODE);

            String applno = xpath.evaluate("/applnextObj/reponsefields[1]/@applno", doc);
            String pur_cd = xpath.evaluate("/applnextObj/reponsefields[1]/@transcode", doc);
            String action_code = xpath.evaluate("/applnextObj/reponsefields[1]/@actioncode", doc);
            String flowslno = xpath.evaluate("/applnextObj/reponsefields[1]/@flowslno", doc);
            String userid = xpath.evaluate("/applnextObj/reponsefields[1]/@userid", doc);
            String counterid = xpath.evaluate("/applnextObj/reponsefields[1]/@counterid", doc);

            String statecode = xpath.evaluate("/applnextObj/reponsefields[1]/@statecode", doc);
            String rtocode = xpath.evaluate("/applnextObj/reponsefields[1]/@rtocode", doc);
            String offcd = xpath.evaluate("/applnextObj/reponsefields[1]/@offcd", doc);

            LOGGER.info("applno " + applno);
            LOGGER.info("pur_cd " + pur_cd);
            LOGGER.info("action_code " + action_code);
            LOGGER.info("flowslno " + flowslno);
            LOGGER.info("userid " + userid);
            LOGGER.info("counterid " + counterid);
            LOGGER.info("statecode " + statecode);
            LOGGER.info("rtocode " + rtocode);
            LOGGER.info("offcd " + offcd);


        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

    }
}
