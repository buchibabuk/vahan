/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.server;

import com.sun.org.apache.xerces.internal.dom.DeferredDocumentImpl;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.sql.RowSet;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import nic.vahan.db.TableList;
import nic.vahan.db.connection.TransactionManager;
import nic.vahan.server.workdistribution.GetInitialflowforVahanWSStub;
import nic.vahan.server.workdistribution.NextStageResponse;
import org.apache.axis2.AxisFault;
import org.xml.sax.InputSource;

public class FancyNumberAuction implements Runnable {

    private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(FancyNumberAuction.class);
    private String command;

    public FancyNumberAuction(String s) {
        this.command = s;
    }

    @Override
    public void run() {
        LOGGER.info(Thread.currentThread().getName() + " Start. Time = " + new Date());
        processCommand();
        LOGGER.info(Thread.currentThread().getName() + " End. Time = " + new Date());
    }

    private void processCommand() {
        TransactionManager tmgr = null;
        try {
            tmgr = new TransactionManager("fancyNumberAutoScheduledJob");
            tmgr.prepareStatement("select distinct( regn_no) from va_fancy_register");
            RowSet rsAllApplications = tmgr.fetchDetachedRowSet_No_release();

            Date cur_date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String strdate = sdf.format(cur_date);
            SimpleDateFormat sdf_dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currnet_date_time = sdf_dt.format(cur_date);
            Timestamp date_time = Timestamp.valueOf(currnet_date_time);

            int cnt = 1;
            while (rsAllApplications.next()) {
                String regn_no = rsAllApplications.getString("regn_no");
                String appl_no = strdate + cnt;
                cnt++;
                NextStageResponse seatDetails = null;
                do {
                    seatDetails = findInitialSeat(appl_no);
                } while (seatDetails.getAppl_no().startsWith("-"));
                String ins_va_status = "INSERT INTO " + TableList.VA_STATUS + " va(appl_no, appl_no_map, pur_cd, flow_slno, file_movement_slno, action_cd, seat_cd, cntr_id, status, office_remark, public_remark, emp_cd, op_dt, state_cd, off_cd, rto_code, file_movement_type)    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = tmgr.prepareStatement(ins_va_status);
                ps.setString(1, appl_no); // actual application no 
                ps.setInt(2, Integer.parseInt(seatDetails.getAppl_no())); // application _ map no
                ps.setInt(3, seatDetails.getPur_cd());
                ps.setInt(4, seatDetails.getFlow_slno());
                ps.setInt(5, 1); // default file movement no.
                ps.setInt(6, seatDetails.getAction_cd());
                ps.setString(7, ""); // blank seat code because web service is not givin data
                ps.setString(8, seatDetails.getCntr_id());
                ps.setString(9, "N"); // default Status
                ps.setString(10, ""); // default blank office remark.
                ps.setString(11, ""); // default blank public remark.
                ps.setLong(12, seatDetails.getEmp_cd());

                ps.setTimestamp(13, date_time);
                ps.setString(14, seatDetails.getState_cd());
                ps.setInt(15, seatDetails.getOff_cd());
                ps.setString(16, seatDetails.getRto_code());
                ps.setString(17, "F"); // default file movement type.
                ps.executeUpdate();

                String ins_va_details = "INSERT INTO va_details( appl_no, pur_cd, appl_dt, regn_no,user_id ,user_type,  state_cd,    off_cd)  VALUES (?, ?, ?, ?, ?,?,?,?)";
                PreparedStatement ps_va_details = tmgr.prepareStatement(ins_va_details);
                ps_va_details.setString(1, appl_no); // actual application no 
                ps_va_details.setInt(2, seatDetails.getPur_cd());
                ps_va_details.setTimestamp(3, date_time);
                ps_va_details.setString(4, regn_no);
                ps_va_details.setString(5, "");// blank User id
                ps_va_details.setString(6, ""); // blank user type.
                ps_va_details.setString(7, seatDetails.getState_cd());
                ps_va_details.setInt(8, seatDetails.getOff_cd());
                ps_va_details.executeUpdate();




            }

            tmgr.commit();

        } catch (AxisFault e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (RemoteException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (XPathExpressionException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } catch (SQLException e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        } finally {
            try {
                if (tmgr != null) {
                    tmgr.release();
                }
            } catch (Exception e) {
                LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
            }
        }
    }

    private NextStageResponse findInitialSeat(String appl_no) throws AxisFault, RemoteException, XPathExpressionException {
        NextStageResponse return_obj = new NextStageResponse();
        // logic goes here....
        GetInitialflowforVahanWSStub stub = new GetInitialflowforVahanWSStub();// the            // default
        GetInitialflowforVahanWSStub.GetInitialflowforVahanws getInitialflowforVahanws6 = new GetInitialflowforVahanWSStub.GetInitialflowforVahanws(); //(GetInitialflowforVahanWSStub.GetInitialflowforVahanws) getTestObject(nic.vahan.server.workdistribution.GetInitialflowforVahanWSStub.GetInitialflowforVahanws.class);

        getInitialflowforVahanws6.setApplno(appl_no);
        getInitialflowforVahanws6.setPrtoCd("1");
        getInitialflowforVahanws6.setPstCd("KL");
        getInitialflowforVahanws6.setTrcdslst(new int[]{95}); // purpose code
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

    @Override
    public String toString() {
        return this.command;
    }

    public static void main(String[] args) {
        //FancyNumberAuction obj = new FancyNumberAuction("Test");
        //obj.run();
    }
}