/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj.smartcard;

import com.opencsv.*;
import com.opencsv.bean.*;

/**
 *
 * @author Niraj
 */
public class CsvFormatDO {

    @CsvBindByName(column = "")
    @CsvBindByPosition(position = 0)
    private String smartCardString;

   
    /**
     * @return the smartCardString
     */
    public String getSmartCardString() {
        return smartCardString;
    }

    /**
     * @param smartCardString the smartCardString to set
     */
    public void setSmartCardString(String smartCardString) {
        this.smartCardString = smartCardString;
    }
}
