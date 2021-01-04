/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.dobj;

import java.io.Serializable;

/**
 *
 * @author nic5912
 */
public class Receipt_Master_dobj implements Serializable {

    private String book_no = "";
    private int current_rcpt_no;
    private String book_rcpt_no = "_";
    private int rcpt_start;
    private int rcpt_end;
    private Integer new_rcpt_no = 0;

    public String getBook_no() {
        return book_no;
    }

    public void setBook_no(String book_no) {
        this.book_no = book_no;
    }

    public int getCurrent_rcpt_no() {
        return current_rcpt_no;
    }

    public void setCurrent_rcpt_no(int current_rcpt_no) {
        this.current_rcpt_no = current_rcpt_no;
    }

    public Integer getNew_rcpt_no() {
        return new_rcpt_no;
    }

    public void setNew_rcpt_no(Integer new_rcpt_no) {
        this.new_rcpt_no = new_rcpt_no;
    }

    public String getBook_rcpt_no() {
        return book_rcpt_no;
    }

    public void setBook_rcpt_no(String book_rcpt_no) {
        this.book_rcpt_no = book_rcpt_no;
    }

    public int getRcpt_start() {
        return rcpt_start;
    }

    public void setRcpt_start(int rcpt_start) {
        this.rcpt_start = rcpt_start;
    }

    public int getRcpt_end() {
        return rcpt_end;
    }

    public void setRcpt_end(int rcpt_end) {
        this.rcpt_end = rcpt_end;
    }
}
