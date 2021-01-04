/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.rto.vahan.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author kaptan singh
 */
final public class Encryption {

     public static String md5_hex(String passwd) {
        String rtnMd5 = "";
        if (passwd != null && !passwd.equalsIgnoreCase("")) {
            try {
                rtnMd5 = MD5(passwd);
            } catch (Exception ex) {
               System.out.println("Problem in md5 encryption....!!!!");
            }
        }
        return rtnMd5;
    }

    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String MD5(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("MD5");
        byte[] md5hash = new byte[32];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        md5hash = md.digest();
        return convertToHex(md5hash);
    }
}

