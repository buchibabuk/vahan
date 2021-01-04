/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import nic.vahan.db.TableConstants;
import nic.vahan.form.bean.dealer.Form21Bean;
import nic.vahan.form.dobj.DealerCollectionDobj;
import nic.vahan.form.impl.DealerCollectionImpl;
import nic.vahan.form.impl.Util;
import nic.vahan.server.ServerUtil;
import org.apache.log4j.Logger;

@ManagedBean(name = "rtoWiseCollectionReport")
@RequestScoped
public class DealerCollectionBeanReport implements Serializable {

    private String header;
    private List<DealerCollectionDobj> rtoWiseCollectionDobj;
    private static Logger LOGGER = Logger.getLogger(Form21Bean.class);
    private long grandTotalRegn;
    private long grandFees;
    private long grandTax;
    private long grandTotalAmount;
    private String offName;
    private Date fromDate;
    private Date uptoDate;
    private String formattedFromDate;
    private String formattedUptoDate;
    private String printedDate;
    private boolean grandTotalVisibility = false;

    public DealerCollectionBeanReport() {
        try {
            Map map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            DealerCollectionImpl report_impl = new DealerCollectionImpl();
            fromDate = (Date) map.get("startDate");
            uptoDate = (Date) map.get("uptoDate");
            if (fromDate != null && uptoDate != null) {
                rtoWiseCollectionDobj = report_impl.getRtoWiseTotalCollection(fromDate, uptoDate);
                header = ServerUtil.getRcptHeading();
                offName = rtoWiseCollectionDobj.get(0).getOffName();
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
                Date dateobj = new Date();
                printedDate = df.format(dateobj);
                formattedFromDate = date.format(fromDate);
                formattedUptoDate = date.format(uptoDate);
                String user_catg = Util.getUserCategory();
                if (user_catg != null && !user_catg.equalsIgnoreCase(TableConstants.USER_CATG_DEALER)) {
                    grandTotalVisibility = true;
                    for (int i = rtoWiseCollectionDobj.size(); i == rtoWiseCollectionDobj.size(); i--) {
                        DealerCollectionDobj rtoCollection = rtoWiseCollectionDobj.get(i - 1);
                        if (rtoCollection.getDealerName().equals("<-GRAND TOTAL->")) {
                            grandTotalRegn = rtoCollection.getGrandTotalRegn();
                            grandFees = rtoCollection.getGrandFees();
                            grandTax = rtoCollection.getGrandTax();
                            grandTotalAmount = rtoCollection.getGrandTotalAmount();
                            rtoWiseCollectionDobj.remove(i - 1);
                        }
                        if (rtoCollection.getDealerName().equals("<-SUB TOTAL->")) {
                            rtoWiseCollectionDobj.remove(i - 1);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }
    }

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @return the rtoWiseCollectionDobj
     */
    public List<DealerCollectionDobj> getRtoWiseCollectionDobj() {
        return rtoWiseCollectionDobj;
    }

    /**
     * @param rtoWiseCollectionDobj the rtoWiseCollectionDobj to set
     */
    public void setRtoWiseCollectionDobj(List<DealerCollectionDobj> rtoWiseCollectionDobj) {
        this.rtoWiseCollectionDobj = rtoWiseCollectionDobj;
    }

    /**
     * @return the grandTotalRegn
     */
    public long getGrandTotalRegn() {
        return grandTotalRegn;
    }

    /**
     * @param grandTotalRegn the grandTotalRegn to set
     */
    public void setGrandTotalRegn(long grandTotalRegn) {
        this.grandTotalRegn = grandTotalRegn;
    }

    /**
     * @return the grandFees
     */
    public long getGrandFees() {
        return grandFees;
    }

    /**
     * @param grandFees the grandFees to set
     */
    public void setGrandFees(long grandFees) {
        this.grandFees = grandFees;
    }

    /**
     * @return the grandTax
     */
    public long getGrandTax() {
        return grandTax;
    }

    /**
     * @param grandTax the grandTax to set
     */
    public void setGrandTax(long grandTax) {
        this.grandTax = grandTax;
    }

    /**
     * @return the grandTotalAmount
     */
    public long getGrandTotalAmount() {
        return grandTotalAmount;
    }

    /**
     * @param grandTotalAmount the grandTotalAmount to set
     */
    public void setGrandTotalAmount(long grandTotalAmount) {
        this.grandTotalAmount = grandTotalAmount;
    }

    /**
     * @return the offName
     */
    public String getOffName() {
        return offName;
    }

    /**
     * @param offName the offName to set
     */
    public void setOffName(String offName) {
        this.offName = offName;
    }

    /**
     * @return the fromDate
     */
    public Date getFromDate() {
        return fromDate;
    }

    /**
     * @param fromDate the fromDate to set
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * @return the uptoDate
     */
    public Date getUptoDate() {
        return uptoDate;
    }

    /**
     * @param uptoDate the uptoDate to set
     */
    public void setUptoDate(Date uptoDate) {
        this.uptoDate = uptoDate;
    }

    /**
     * @return the printedDate
     */
    public String getPrintedDate() {
        return printedDate;
    }

    /**
     * @param printedDate the printedDate to set
     */
    public void setPrintedDate(String printedDate) {
        this.printedDate = printedDate;
    }

    /**
     * @return the LOGGER
     */
    public static Logger getLOGGER() {
        return LOGGER;
    }

    /**
     * @param aLOGGER the LOGGER to set
     */
    public static void setLOGGER(Logger aLOGGER) {
        LOGGER = aLOGGER;
    }

    /**
     * @return the formattedFromDate
     */
    public String getFormattedFromDate() {
        return formattedFromDate;
    }

    /**
     * @param formattedFromDate the formattedFromDate to set
     */
    public void setFormattedFromDate(String formattedFromDate) {
        this.formattedFromDate = formattedFromDate;
    }

    /**
     * @return the formattedUptoDate
     */
    public String getFormattedUptoDate() {
        return formattedUptoDate;
    }

    /**
     * @param formattedUptoDate the formattedUptoDate to set
     */
    public void setFormattedUptoDate(String formattedUptoDate) {
        this.formattedUptoDate = formattedUptoDate;
    }

    /**
     * @return the grandTotalVisibility
     */
    public boolean isGrandTotalVisibility() {
        return grandTotalVisibility;
    }

    /**
     * @param grandTotalVisibility the grandTotalVisibility to set
     */
    public void setGrandTotalVisibility(boolean grandTotalVisibility) {
        this.grandTotalVisibility = grandTotalVisibility;
    }
}
