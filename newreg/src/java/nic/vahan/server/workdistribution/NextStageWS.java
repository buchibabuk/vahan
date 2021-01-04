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
import org.xml.sax.InputSource;

/**
 *
 * @author Ashok Kumar <send2ashok@hotmail.com>
 */
public class NextStageWS {

    private static XPathFactory xpathFactory = XPathFactory.newInstance();
    private static XPath xpath = xpathFactory.newXPath();

    public static NextStageResponse getNextStageResponse(NextStageRequest request) throws VahanException {

        NextStageResponse responseObject = null;
        Exception exe = null;
        PreparedStatement ps = null;
        String sql = "select url from vm_wsdl_url where code=?";
        String url = null;
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("getNextStageResponse");
            ps = tmgr.prepareStatement(sql);
            ps.setInt(1, TableConstants.NEXT_FLOW_WS_URL);
            RowSet rs = tmgr.fetchDetachedRowSet();

            if (rs.next()) //found
            {
                url = rs.getString("url");
                System.out.println("NEXT FLOW URL = " + url);
            }


            NextStageVahanWSStub stub1 = new NextStageVahanWSStub(TableConstants.NEXT_FLOW_WS_URL, url);

            NextStageVahanWSStub.NextStgWS nextStgWS = new NextStageVahanWSStub.NextStgWS();

            nextStgWS.setAccd(request.getAction_cd()); //currrent Action code (Role Code with Purpose Code)
            nextStgWS.setApplNo(request.getAppl_no()); //current Application No.
            nextStgWS.setMovtype(request.getFile_movement_type());
            nextStgWS.setPuserid(request.getEmp_cd());//current emp_cd
            nextStgWS.setRtocode(String.valueOf(request.getOff_cd()));//current off code
            nextStgWS.setTrCd(request.getPur_cd());//current purpose code
            nextStgWS.setStCd(request.getState_cd()); //current state code

            NextStageVahanWSStub.NextStgWSResponse res = stub1.nextStgWS(nextStgWS);

            System.out.println("Response is below \n" + res.get_return());

            String response = res.get_return();


            InputSource source = new InputSource(new StringReader(response));
            DeferredDocumentImpl doc = (DeferredDocumentImpl) xpath.evaluate("/", source, XPathConstants.NODE);

            //XPathExpression xpexpression = XPathFactory.newInstance().

            System.out.println("=========APPLICATION NEXT STAGE===============");
            responseObject = new NextStageResponse();
            responseObject.setAppl_no(xpath.evaluate("/response/applnextStg[1]/@applno", doc));
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
                System.out.println(e.getMessage());
            }
        }

        if (exe != null) {
            throw new VahanException("Error in Web Services for getting Next Stage");
        }


        return responseObject;
    }
}
