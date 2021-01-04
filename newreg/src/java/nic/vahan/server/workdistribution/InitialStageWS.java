/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.server.workdistribution;

import com.sun.org.apache.xerces.internal.dom.DeferredDocumentImpl;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.RowSet;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import nic.rto.vahan.common.VahanException;
import nic.vahan.db.TableConstants;
import nic.vahan.db.connection.TransactionManager;
import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

/**
 *
 * @author AMBRISH
 */
public class InitialStageWS {

    private static Logger LOGGER = Logger.getLogger(InitialStageWS.class);
    private static XPathFactory xpathFactory = XPathFactory.newInstance();
    private static XPath xpath = xpathFactory.newXPath();

    public static NextStageResponse webServiceInitialSeatResponse(NextStageRequest request) throws VahanException {

        NextStageResponse responseObject = null;
        Exception exe = null;
        PreparedStatement ps = null;
        String sql = "select url from vm_wsdl_url where code=?";
        String url = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getNextStageResponse");
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, TableConstants.INITIAL_FLOW_WS_URL);
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) //found
            {
                url = rs.getString("url");
            }


            GetInitialflowforVahanWSStub stub1 = new GetInitialflowforVahanWSStub(TableConstants.INITIAL_FLOW_WS_URL, url);

            GetInitialflowforVahanWSStub.GetInitialflowforVahanws nextStgWS = new GetInitialflowforVahanWSStub.GetInitialflowforVahanws();

            nextStgWS.setApplno(request.getAppl_no());
            nextStgWS.setPrtoCd(String.valueOf(request.getOff_cd()));
            nextStgWS.setPstCd(request.getState_cd());
            nextStgWS.setTrcdslst(new int[]{request.getPur_cd()});

            GetInitialflowforVahanWSStub.GetInitialflowforVahanwsResponse res = stub1.getInitialflowforVahanws(nextStgWS);
            String response = res.get_return();
            InputSource source = new InputSource(new StringReader(response));
            DeferredDocumentImpl doc = (DeferredDocumentImpl) xpath.evaluate("/", source, XPathConstants.NODE);

            responseObject = new NextStageResponse();
            responseObject.setAppl_no(xpath.evaluate("/response/applnextStg[1]/@applno", doc)); // actually this is application map
            responseObject.setPur_cd(Integer.parseInt(xpath.evaluate("/response/applnextStg[1]/@transcode", doc).trim()));
            responseObject.setAction_cd(Integer.parseInt(xpath.evaluate("/response/applnextStg[1]/@actioncode", doc).trim()));
            responseObject.setFlow_slno(Integer.parseInt(xpath.evaluate("/response/applnextStg[1]/@flowslno", doc).trim()));
            responseObject.setEmp_cd(Integer.parseInt(xpath.evaluate("/response/applnextStg[1]/@userid", doc).trim()));
            responseObject.setCntr_id(xpath.evaluate("/response/applnextStg[1]/@counterid", doc));
            responseObject.setState_cd(xpath.evaluate("/response/applnextStg[1]/@statecode", doc));
            responseObject.setRto_code(xpath.evaluate("/response/applnextStg[1]/@rtocode", doc));
            responseObject.setOff_cd(Integer.parseInt(xpath.evaluate("/response/applnextStg[1]/@offcd", doc).trim()));


        } catch (RemoteException re) {
            exe = re;
        } catch (XPathExpressionException xpee) {
            exe = xpee;
        } catch (SQLException sqle) {
            exe = sqle;
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }

        if (exe != null) {
            LOGGER.error(exe);
            throw new VahanException("Error in Web Services for getting Next Stage");
        }
        return responseObject;
    }

    public static NextStageResponse findInitialSeat(String appl_no, int pur_cd_input, String state_cd, int off_cd) throws AxisFault, RemoteException, XPathExpressionException {
        NextStageResponse return_obj = new NextStageResponse();
        // logic goes here....
        GetInitialflowforVahanWSStub stub = new GetInitialflowforVahanWSStub();// the            // default
        GetInitialflowforVahanWSStub.GetInitialflowforVahanws getInitialflowforVahanws6 = new GetInitialflowforVahanWSStub.GetInitialflowforVahanws(); //(GetInitialflowforVahanWSStub.GetInitialflowforVahanws) getTestObject(nic.vahan.server.workdistribution.GetInitialflowforVahanWSStub.GetInitialflowforVahanws.class);

        getInitialflowforVahanws6.setApplno(appl_no);
        getInitialflowforVahanws6.setPrtoCd(off_cd + "");
        getInitialflowforVahanws6.setPstCd(state_cd);
        getInitialflowforVahanws6.setTrcdslst(new int[]{pur_cd_input}); // purpose code

        GetInitialflowforVahanWSStub.GetInitialflowforVahanwsResponse rs = stub.getInitialflowforVahanws(getInitialflowforVahanws6);

        String response = rs.get_return();
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        InputSource source = new InputSource(new StringReader(response));
        DeferredDocumentImpl doc = (DeferredDocumentImpl) xpath.evaluate("/", source, XPathConstants.NODE);
//
//            //XPathExpression xpexpression = XPathFactory.newInstance().
//


        String applno = xpath.evaluate("/applnextObj/reponsefields[1]/@applno", doc);
        String pur_cd = xpath.evaluate("/applnextObj/reponsefields[1]/@transcode", doc);
        String action_code = xpath.evaluate("/applnextObj/reponsefields[1]/@actioncode", doc);
        String flowslno = xpath.evaluate("/applnextObj/reponsefields[1]/@flowslno", doc);
        String userid = xpath.evaluate("/applnextObj/reponsefields[1]/@userid", doc);
        String counterid = xpath.evaluate("/applnextObj/reponsefields[1]/@counterid", doc);

        String statecode = xpath.evaluate("/applnextObj/reponsefields[1]/@statecode", doc);
        String rtocode = xpath.evaluate("/applnextObj/reponsefields[1]/@rtocode", doc);
        String offcd = xpath.evaluate("/applnextObj/reponsefields[1]/@offcd", doc);
        return_obj.setAppl_no(applno); // actually this is map application no
        return_obj.setPur_cd(Integer.parseInt(pur_cd));
        return_obj.setAction_cd(Integer.parseInt(action_code));
        return_obj.setFlow_slno(Integer.parseInt(flowslno));
        return_obj.setEmp_cd(Integer.parseInt(userid));
        return_obj.setCntr_id(counterid);
        return_obj.setState_cd(statecode);
        return_obj.setRto_code(rtocode);
        return_obj.setOff_cd(Integer.parseInt(offcd));


        return return_obj;

    }
}
