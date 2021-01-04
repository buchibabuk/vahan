/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.smartcard;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class SmartCardDobj implements Serializable {

    private String vehregno;
    private String regdate;
    private String ownername;
    private String fname;
    private String caddress;
    private String manufacturer;
    private String modelno;
    private String colour;
    private String fuel;
    private String vehclass;
    private String bodytype;
    private String seatcap;
    private String standcap;
    private String manufdate;
    private String unladenwt;
    private String cubiccap;
    private String wheelbase;
    private String noofcylin;
    private String ownerserial;
    private String chasisno;
    private String engineno;
    private String taxpaidupto;
    private String regnvalidity;
    private String approvingauth;
    private String finname;
    private String finaddress;
    private String hypofrom;
    private String hypoto;
    private String nocno;
    private String stateto;
    private String rtoto;
    private String ncrbclearno;
    private String nocissuedt;
    private String inscompname;
    private String coverpolicyno;
    private String instype;
    private String insvalidupto;
    private String puccentercode;
    private String pucvalidupto;
    private String taxamount;
    private String fine;
    private String exemptrecptno;
    private String paymentdt;
    private String taxvalidfrom;
    private String taxvalidto;
    private String exemption;
    private String drtocode;
    private String buflag;
    private String fitvalidupto;
    private String fitinsofficer;
    private String fitlocation;
    private String grossvehwt;
    private String semitrailers;
    private String tyreinfo;
    private String axleinfo;
    private String rcpt_no;
    private String appl_no;
    private String pur_cd;
    private String deal_cd;
    private Timestamp op_dt;
    private String status;
    private String flat_file;
    private String cur_date;
    private String smartCardFileName = "";
    private String state_cd;
    private int off_cd;
    private String pendingApplnoForHsrp;
    private String pendingVehnoForHsrp;
    private String pendingReason;
    private List<SmartCardDobj> listSmartCard;
    private List<SmartCardDobj> listHsrpPending;
    private String reason;
    private List<SmartCardDobj> listSmartCardRegnSearch;
    private boolean recordsPresent;

    /**
     * @return the vehregno
     */
    public String getVehregno() {
        return vehregno;
    }

    /**
     * @param vehregno the vehregno to set
     */
    public void setVehregno(String vehregno) {
        this.vehregno = vehregno;
    }

    /**
     * @return the regdate
     */
    public String getRegdate() {
        return regdate;
    }

    /**
     * @param regdate the regdate to set
     */
    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }

    /**
     * @return the ownername
     */
    public String getOwnername() {
        return ownername;
    }

    /**
     * @param ownername the ownername to set
     */
    public void setOwnername(String ownername) {
        this.ownername = ownername;
    }

    /**
     * @return the fname
     */
    public String getFname() {
        return fname;
    }

    /**
     * @param fname the fname to set
     */
    public void setFname(String fname) {
        this.fname = fname;
    }

    /**
     * @return the caddress
     */
    public String getCaddress() {
        return caddress;
    }

    /**
     * @param caddress the caddress to set
     */
    public void setCaddress(String caddress) {
        this.caddress = caddress;
    }

    /**
     * @return the manufacturer
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * @param manufacturer the manufacturer to set
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * @return the modelno
     */
    public String getModelno() {
        return modelno;
    }

    /**
     * @param modelno the modelno to set
     */
    public void setModelno(String modelno) {
        this.modelno = modelno;
    }

    /**
     * @return the colour
     */
    public String getColour() {
        return colour;
    }

    /**
     * @param colour the colour to set
     */
    public void setColour(String colour) {
        this.colour = colour;
    }

    /**
     * @return the fuel
     */
    public String getFuel() {
        return fuel;
    }

    /**
     * @param fuel the fuel to set
     */
    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    /**
     * @return the vehclass
     */
    public String getVehclass() {
        return vehclass;
    }

    /**
     * @param vehclass the vehclass to set
     */
    public void setVehclass(String vehclass) {
        this.vehclass = vehclass;
    }

    /**
     * @return the bodytype
     */
    public String getBodytype() {
        return bodytype;
    }

    /**
     * @param bodytype the bodytype to set
     */
    public void setBodytype(String bodytype) {
        this.bodytype = bodytype;
    }

    /**
     * @return the seatcap
     */
    public String getSeatcap() {
        return seatcap;
    }

    /**
     * @param seatcap the seatcap to set
     */
    public void setSeatcap(String seatcap) {
        this.seatcap = seatcap;
    }

    /**
     * @return the standcap
     */
    public String getStandcap() {
        return standcap;
    }

    /**
     * @param standcap the standcap to set
     */
    public void setStandcap(String standcap) {
        this.standcap = standcap;
    }

    /**
     * @return the manufdate
     */
    public String getManufdate() {
        return manufdate;
    }

    /**
     * @param manufdate the manufdate to set
     */
    public void setManufdate(String manufdate) {
        this.manufdate = manufdate;
    }

    /**
     * @return the unladenwt
     */
    public String getUnladenwt() {
        return unladenwt;
    }

    /**
     * @param unladenwt the unladenwt to set
     */
    public void setUnladenwt(String unladenwt) {
        this.unladenwt = unladenwt;
    }

    /**
     * @return the cubiccap
     */
    public String getCubiccap() {
        return cubiccap;
    }

    /**
     * @param cubiccap the cubiccap to set
     */
    public void setCubiccap(String cubiccap) {
        this.cubiccap = cubiccap;
    }

    /**
     * @return the wheelbase
     */
    public String getWheelbase() {
        return wheelbase;
    }

    /**
     * @param wheelbase the wheelbase to set
     */
    public void setWheelbase(String wheelbase) {
        this.wheelbase = wheelbase;
    }

    /**
     * @return the noofcylin
     */
    public String getNoofcylin() {
        return noofcylin;
    }

    /**
     * @param noofcylin the noofcylin to set
     */
    public void setNoofcylin(String noofcylin) {
        this.noofcylin = noofcylin;
    }

    /**
     * @return the ownerserial
     */
    public String getOwnerserial() {
        return ownerserial;
    }

    /**
     * @param ownerserial the ownerserial to set
     */
    public void setOwnerserial(String ownerserial) {
        this.ownerserial = ownerserial;
    }

    /**
     * @return the chasisno
     */
    public String getChasisno() {
        return chasisno;
    }

    /**
     * @param chasisno the chasisno to set
     */
    public void setChasisno(String chasisno) {
        this.chasisno = chasisno;
    }

    /**
     * @return the engineno
     */
    public String getEngineno() {
        return engineno;
    }

    /**
     * @param engineno the engineno to set
     */
    public void setEngineno(String engineno) {
        this.engineno = engineno;
    }

    /**
     * @return the taxpaidupto
     */
    public String getTaxpaidupto() {
        return taxpaidupto;
    }

    /**
     * @param taxpaidupto the taxpaidupto to set
     */
    public void setTaxpaidupto(String taxpaidupto) {
        this.taxpaidupto = taxpaidupto;
    }

    /**
     * @return the regnvalidity
     */
    public String getRegnvalidity() {
        return regnvalidity;
    }

    /**
     * @param regnvalidity the regnvalidity to set
     */
    public void setRegnvalidity(String regnvalidity) {
        this.regnvalidity = regnvalidity;
    }

    /**
     * @return the approvingauth
     */
    public String getApprovingauth() {
        return approvingauth;
    }

    /**
     * @param approvingauth the approvingauth to set
     */
    public void setApprovingauth(String approvingauth) {
        this.approvingauth = approvingauth;
    }

    /**
     * @return the finname
     */
    public String getFinname() {
        return finname;
    }

    /**
     * @param finname the finname to set
     */
    public void setFinname(String finname) {
        this.finname = finname;
    }

    /**
     * @return the finaddress
     */
    public String getFinaddress() {
        return finaddress;
    }

    /**
     * @param finaddress the finaddress to set
     */
    public void setFinaddress(String finaddress) {
        this.finaddress = finaddress;
    }

    /**
     * @return the hypofrom
     */
    public String getHypofrom() {
        return hypofrom;
    }

    /**
     * @param hypofrom the hypofrom to set
     */
    public void setHypofrom(String hypofrom) {
        this.hypofrom = hypofrom;
    }

    /**
     * @return the hypoto
     */
    public String getHypoto() {
        return hypoto;
    }

    /**
     * @param hypoto the hypoto to set
     */
    public void setHypoto(String hypoto) {
        this.hypoto = hypoto;
    }

    /**
     * @return the nocno
     */
    public String getNocno() {
        return nocno;
    }

    /**
     * @param nocno the nocno to set
     */
    public void setNocno(String nocno) {
        this.nocno = nocno;
    }

    /**
     * @return the stateto
     */
    public String getStateto() {
        return stateto;
    }

    /**
     * @param stateto the stateto to set
     */
    public void setStateto(String stateto) {
        this.stateto = stateto;
    }

    /**
     * @return the rtoto
     */
    public String getRtoto() {
        return rtoto;
    }

    /**
     * @param rtoto the rtoto to set
     */
    public void setRtoto(String rtoto) {
        this.rtoto = rtoto;
    }

    /**
     * @return the ncrbclearno
     */
    public String getNcrbclearno() {
        return ncrbclearno;
    }

    /**
     * @param ncrbclearno the ncrbclearno to set
     */
    public void setNcrbclearno(String ncrbclearno) {
        this.ncrbclearno = ncrbclearno;
    }

    /**
     * @return the nocissuedt
     */
    public String getNocissuedt() {
        return nocissuedt;
    }

    /**
     * @param nocissuedt the nocissuedt to set
     */
    public void setNocissuedt(String nocissuedt) {
        this.nocissuedt = nocissuedt;
    }

    /**
     * @return the inscompname
     */
    public String getInscompname() {
        return inscompname;
    }

    /**
     * @param inscompname the inscompname to set
     */
    public void setInscompname(String inscompname) {
        this.inscompname = inscompname;
    }

    /**
     * @return the coverpolicyno
     */
    public String getCoverpolicyno() {
        return coverpolicyno;
    }

    /**
     * @param coverpolicyno the coverpolicyno to set
     */
    public void setCoverpolicyno(String coverpolicyno) {
        this.coverpolicyno = coverpolicyno;
    }

    /**
     * @return the instype
     */
    public String getInstype() {
        return instype;
    }

    /**
     * @param instype the instype to set
     */
    public void setInstype(String instype) {
        this.instype = instype;
    }

    /**
     * @return the insvalidupto
     */
    public String getInsvalidupto() {
        return insvalidupto;
    }

    /**
     * @param insvalidupto the insvalidupto to set
     */
    public void setInsvalidupto(String insvalidupto) {
        this.insvalidupto = insvalidupto;
    }

    /**
     * @return the puccentercode
     */
    public String getPuccentercode() {
        return puccentercode;
    }

    /**
     * @param puccentercode the puccentercode to set
     */
    public void setPuccentercode(String puccentercode) {
        this.puccentercode = puccentercode;
    }

    /**
     * @return the pucvalidupto
     */
    public String getPucvalidupto() {
        return pucvalidupto;
    }

    /**
     * @param pucvalidupto the pucvalidupto to set
     */
    public void setPucvalidupto(String pucvalidupto) {
        this.pucvalidupto = pucvalidupto;
    }

    /**
     * @return the taxamount
     */
    public String getTaxamount() {
        return taxamount;
    }

    /**
     * @param taxamount the taxamount to set
     */
    public void setTaxamount(String taxamount) {
        this.taxamount = taxamount;
    }

    /**
     * @return the fine
     */
    public String getFine() {
        return fine;
    }

    /**
     * @param fine the fine to set
     */
    public void setFine(String fine) {
        this.fine = fine;
    }

    /**
     * @return the exemptrecptno
     */
    public String getExemptrecptno() {
        return exemptrecptno;
    }

    /**
     * @param exemptrecptno the exemptrecptno to set
     */
    public void setExemptrecptno(String exemptrecptno) {
        this.exemptrecptno = exemptrecptno;
    }

    /**
     * @return the paymentdt
     */
    public String getPaymentdt() {
        return paymentdt;
    }

    /**
     * @param paymentdt the paymentdt to set
     */
    public void setPaymentdt(String paymentdt) {
        this.paymentdt = paymentdt;
    }

    /**
     * @return the taxvalidfrom
     */
    public String getTaxvalidfrom() {
        return taxvalidfrom;
    }

    /**
     * @param taxvalidfrom the taxvalidfrom to set
     */
    public void setTaxvalidfrom(String taxvalidfrom) {
        this.taxvalidfrom = taxvalidfrom;
    }

    /**
     * @return the taxvalidto
     */
    public String getTaxvalidto() {
        return taxvalidto;
    }

    /**
     * @param taxvalidto the taxvalidto to set
     */
    public void setTaxvalidto(String taxvalidto) {
        this.taxvalidto = taxvalidto;
    }

    /**
     * @return the exemption
     */
    public String getExemption() {
        return exemption;
    }

    /**
     * @param exemption the exemption to set
     */
    public void setExemption(String exemption) {
        this.exemption = exemption;
    }

    /**
     * @return the drtocode
     */
    public String getDrtocode() {
        return drtocode;
    }

    /**
     * @param drtocode the drtocode to set
     */
    public void setDrtocode(String drtocode) {
        this.drtocode = drtocode;
    }

    /**
     * @return the buflag
     */
    public String getBuflag() {
        return buflag;
    }

    /**
     * @param buflag the buflag to set
     */
    public void setBuflag(String buflag) {
        this.buflag = buflag;
    }

    /**
     * @return the fitvalidupto
     */
    public String getFitvalidupto() {
        return fitvalidupto;
    }

    /**
     * @param fitvalidupto the fitvalidupto to set
     */
    public void setFitvalidupto(String fitvalidupto) {
        this.fitvalidupto = fitvalidupto;
    }

    /**
     * @return the fitinsofficer
     */
    public String getFitinsofficer() {
        return fitinsofficer;
    }

    /**
     * @param fitinsofficer the fitinsofficer to set
     */
    public void setFitinsofficer(String fitinsofficer) {
        this.fitinsofficer = fitinsofficer;
    }

    /**
     * @return the fitlocation
     */
    public String getFitlocation() {
        return fitlocation;
    }

    /**
     * @param fitlocation the fitlocation to set
     */
    public void setFitlocation(String fitlocation) {
        this.fitlocation = fitlocation;
    }

    /**
     * @return the grossvehwt
     */
    public String getGrossvehwt() {
        return grossvehwt;
    }

    /**
     * @param grossvehwt the grossvehwt to set
     */
    public void setGrossvehwt(String grossvehwt) {
        this.grossvehwt = grossvehwt;
    }

    /**
     * @return the semitrailers
     */
    public String getSemitrailers() {
        return semitrailers;
    }

    /**
     * @param semitrailers the semitrailers to set
     */
    public void setSemitrailers(String semitrailers) {
        this.semitrailers = semitrailers;
    }

    /**
     * @return the tyreinfo
     */
    public String getTyreinfo() {
        return tyreinfo;
    }

    /**
     * @param tyreinfo the tyreinfo to set
     */
    public void setTyreinfo(String tyreinfo) {
        this.tyreinfo = tyreinfo;
    }

    /**
     * @return the axleinfo
     */
    public String getAxleinfo() {
        return axleinfo;
    }

    /**
     * @param axleinfo the axleinfo to set
     */
    public void setAxleinfo(String axleinfo) {
        this.axleinfo = axleinfo;
    }

    /**
     * @return the rcpt_no
     */
    public String getRcpt_no() {
        return rcpt_no;
    }

    /**
     * @param rcpt_no the rcpt_no to set
     */
    public void setRcpt_no(String rcpt_no) {
        this.rcpt_no = rcpt_no;
        if (rcpt_no != null) {
            setAppl_no(String.format("%1$-16s", rcpt_no));
        }
    }

    /**
     * @return the pur_cd
     */
    public String getPur_cd() {
        return pur_cd;
    }

    /**
     * @param pur_cd the pur_cd to set
     */
    public void setPur_cd(String pur_cd) {
        this.pur_cd = pur_cd;
    }

    /**
     * @return the deal_cd
     */
    public String getDeal_cd() {
        return deal_cd;
    }

    /**
     * @param deal_cd the deal_cd to set
     */
    public void setDeal_cd(String deal_cd) {
        this.deal_cd = deal_cd;
    }

    /**
     * @return the op_dt
     */
    public Timestamp getOp_dt() {
        return op_dt;
    }

    /**
     * @param op_dt the op_dt to set
     */
    public void setOp_dt(Timestamp op_dt) {
        this.op_dt = op_dt;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the flat_file
     */
    public String getFlat_file() {
        return flat_file;
    }

    /**
     * @param flat_file the flat_file to set
     */
    public void setFlat_file(String flat_file) {
        this.flat_file = flat_file;
    }

    /**
     * @return the state_cd
     */
    public String getState_cd() {
        return state_cd;
    }

    /**
     * @param state_cd the state_cd to set
     */
    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    /**
     * @return the off_cd
     */
    public int getOff_cd() {
        return off_cd;
    }

    /**
     * @param off_cd the off_cd to set
     */
    public void setOff_cd(int off_cd) {
        this.off_cd = off_cd;
    }

    /**
     * @return the c_dt
     */
    public String getCur_date() {
        return cur_date;
    }

    /**
     * @param c_dt the c_dt to set
     */
    public void setCur_date(String cur_date) {
        this.cur_date = cur_date;
    }

    /**
     * @return the listSmartCard
     */
    public List<SmartCardDobj> getListSmartCard() {
        return listSmartCard;
    }

    /**
     * @param listSmartCard the listSmartCard to set
     */
    public void setListSmartCard(List<SmartCardDobj> listSmartCard) {
        this.listSmartCard = listSmartCard;
    }

    /**
     * @return the smartCardFileName
     */
    public String getSmartCardFileName() {
        return smartCardFileName;
    }

    /**
     * @param smartCardFileName the smartCardFileName to set
     */
    public void setSmartCardFileName(String smartCardFileName) {
        this.smartCardFileName = smartCardFileName;
    }

    public String getPendingApplnoForHsrp() {
        return pendingApplnoForHsrp;
    }

    public void setPendingApplnoForHsrp(String pendingApplnoForHsrp) {
        this.pendingApplnoForHsrp = pendingApplnoForHsrp;
    }

    public List<SmartCardDobj> getListHsrpPending() {
        return listHsrpPending;
    }

    public void setListHsrpPending(List<SmartCardDobj> listHsrpPending) {
        this.listHsrpPending = listHsrpPending;
    }

    public String getPendingReason() {
        return pendingReason;
    }

    public void setPendingReason(String pendingReason) {
        this.pendingReason = pendingReason;
    }

    /**
     * @return the appl_no
     */
    public String getAppl_no() {
        return appl_no;
    }

    /**
     * @param appl_no the appl_no to set
     */
    public void setAppl_no(String appl_no) {
        this.appl_no = appl_no;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return the pendingVehnoForHsrp
     */
    public String getPendingVehnoForHsrp() {
        return pendingVehnoForHsrp;
    }

    /**
     * @param pendingVehnoForHsrp the pendingVehnoForHsrp to set
     */
    public void setPendingVehnoForHsrp(String pendingVehnoForHsrp) {
        this.pendingVehnoForHsrp = pendingVehnoForHsrp;
    }

    /**
     * @return the listSmartCardRegnSearch
     */
    public List<SmartCardDobj> getListSmartCardRegnSearch() {
        return listSmartCardRegnSearch;
    }

    /**
     * @param listSmartCardRegnSearch the listSmartCardRegnSearch to set
     */
    public void setListSmartCardRegnSearch(List<SmartCardDobj> listSmartCardRegnSearch) {
        this.listSmartCardRegnSearch = listSmartCardRegnSearch;
    }

    /**
     * @return the recordsPresent
     */
    public boolean isRecordsPresent() {
        return recordsPresent;
    }

    /**
     * @param recordsPresent the recordsPresent to set
     */
    public void setRecordsPresent(boolean recordsPresent) {
        this.recordsPresent = recordsPresent;
    }
}
