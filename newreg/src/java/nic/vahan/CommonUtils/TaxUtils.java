/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.CommonUtils;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import nic.java.util.DateUtils;
import nic.rto.vahan.common.VahanException;
import nic.vahan.form.dobj.DOTaxDetail;
import org.apache.log4j.Logger;

/**
 *
 * @author tranC094
 */
public class TaxUtils {

    private static Logger LOGGER = Logger.getLogger(TaxUtils.class);
    public static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    /**
     * Find the minimum tax from date and maximum tax date from this breakup of
     * tax
     *
     * @param toGroupList
     * @return DOTaxDetail
     */
    public static DOTaxDetail getTaxGroupedSummary(List<DOTaxDetail> toGroupList) {
        DOTaxDetail taxDobj = new DOTaxDetail();
        Comparator<DOTaxDetail> taxFromdateCompartor = new Comparator<DOTaxDetail>() {
            @Override
            public int compare(DOTaxDetail o1, DOTaxDetail o2) {
                Date taxFromDate1 = null;
                Date taxUptoDate2 = null;
                if (o1 != null && o2 != null) {
                    try {
                        taxFromDate1 = DateUtils.parseDate(o1.getTAX_FROM());
                        taxUptoDate2 = DateUtils.parseDate(o2.getTAX_FROM());
                    } catch (Exception e) {
                        LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    }
                    if (taxFromDate1 != null && taxUptoDate2 != null) {
                        return taxFromDate1.compareTo(taxUptoDate2);
                    }
                }
                return 0;
            }
        };

        Comparator<DOTaxDetail> taxUptodateCompartor = new Comparator<DOTaxDetail>() {
            @Override
            public int compare(DOTaxDetail o1, DOTaxDetail o2) {
                Date taxUptoDate1 = null;
                Date taxFromDate2 = null;
                if (o1 != null && o2 != null) {
                    try {
                        taxUptoDate1 = DateUtils.parseDate(o1.getTAX_UPTO());
                        taxFromDate2 = DateUtils.parseDate(o2.getTAX_UPTO());
                    } catch (Exception e) {
                        LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    }
                    if (taxUptoDate1 != null && taxFromDate2 != null) {
                        return taxUptoDate1.compareTo(taxFromDate2);
                    }
                }
                return 0;
            }
        };

        DOTaxDetail max = Collections.max(toGroupList, taxUptodateCompartor);
        DOTaxDetail min = Collections.min(toGroupList, taxFromdateCompartor);
        taxDobj.setTAX_FROM(min.getTAX_FROM());
        taxDobj.setTAX_UPTO(max.getTAX_UPTO());
        taxDobj.setPUR_CD(min.getPUR_CD());
        taxDobj.setTAX_HEAD(min.getTAX_HEAD());
        taxDobj.setTAX_MODE(min.getTAX_MODE());

        for (DOTaxDetail dobj : toGroupList) {
            if (dobj.getAMOUNT() != null) {
                taxDobj.setAMOUNT((taxDobj.getAMOUNT() == null ? 0 : taxDobj.getAMOUNT()) + dobj.getAMOUNT());
            }

            if (dobj.getFINE() != null) {
                taxDobj.setFINE((taxDobj.getFINE() == null ? 0 : taxDobj.getFINE()) + dobj.getFINE());
            }
            if (dobj.getREBATE() != null) {
                taxDobj.setREBATE((taxDobj.getREBATE() == null ? 0 : taxDobj.getREBATE()) + dobj.getREBATE());
            }

            if (dobj.getINTEREST() != null) {
                taxDobj.setINTEREST((taxDobj.getINTEREST() == null ? 0 : taxDobj.getINTEREST()) + dobj.getINTEREST());
            }

            if (dobj.getPENALTY() != null) {
                taxDobj.setPENALTY((taxDobj.getPENALTY() == null ? 0 : taxDobj.getPENALTY()) + dobj.getPENALTY());
            }

            if (dobj.getSURCHARGE() != null) {
                taxDobj.setSURCHARGE((taxDobj.getSURCHARGE() == null ? 0 : taxDobj.getSURCHARGE()) + dobj.getSURCHARGE());
            }

        }
        return taxDobj;
    }

    /**
     *
     *
     *
     */
    public static List<DOTaxDetail> sortTaxDetails(List<DOTaxDetail> toGroupList) throws VahanException {

        if (toGroupList == null) {
            throw new VahanException("Tax Details are not found from Service.");
        }

        Comparator<DOTaxDetail> taxFromdateCompartor = new Comparator<DOTaxDetail>() {
            @Override
            public int compare(DOTaxDetail o1, DOTaxDetail o2) {
                Date taxFromDate1 = null;
                Date taxUptoDate2 = null;
                if (o1 != null && o2 != null) {
                    try {
                        taxFromDate1 = DateUtils.parseDate(o1.getTAX_FROM());
                        taxUptoDate2 = DateUtils.parseDate(o2.getTAX_FROM());
                    } catch (Exception e) {
                        LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    }
                    if (taxFromDate1 != null && taxUptoDate2 != null) {
                        return taxFromDate1.compareTo(taxUptoDate2);
                    }
                }
                return 0;
            }
        };

        Comparator<DOTaxDetail> taxUptodateCompartor = new Comparator<DOTaxDetail>() {
            @Override
            public int compare(DOTaxDetail o1, DOTaxDetail o2) {
                Date taxUptoDate1 = null;
                Date taxFromDate2 = null;
                if (o1 != null && o2 != null) {
                    try {
                        taxUptoDate1 = DateUtils.parseDate(o1.getTAX_UPTO());
                        taxFromDate2 = DateUtils.parseDate(o2.getTAX_UPTO());
                    } catch (Exception e) {
                        LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
                    }
                    if (taxUptoDate1 != null && taxFromDate2 != null) {
                        return taxUptoDate1.compareTo(taxFromDate2);
                    }
                }
                return 0;
            }
        };

        DOTaxDetail max = Collections.max(toGroupList, taxUptodateCompartor);
        DOTaxDetail min = Collections.min(toGroupList, taxFromdateCompartor);
        Collections.sort(toGroupList, taxFromdateCompartor);
        return toGroupList;

    }
}
