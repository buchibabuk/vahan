/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.form.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nic.vahan.form.dobj.REF_DOC_dobj;
import nic.vahan.form.impl.REF_DOC_Impl;
import org.primefaces.model.DefaultStreamedContent;

/**
 *
 * @author Ashok Kumar <send2ashok@hotmail.com>
 */
@WebServlet(name = "refdocview", urlPatterns = {"/refdocview"})
public class refdocview extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        //PrintWriter out = response.getWriter();
        try {
            // String imageId = String.valueOf(request.getPathInfo().substring(1)); // Gets string that goes after "/images/".
            String appl_no = request.getParameter("appl_no");
            String scan_doc_no = request.getParameter("scan_doc_no");


            REF_DOC_Impl refImpl = new REF_DOC_Impl();
            ArrayList<REF_DOC_dobj> list = refImpl.set_ref_doc_db_to_dobj("KL/APP/1");

            response.setHeader("Content-Type", "image/png");
            byte[] xyz;

            BufferedInputStream input = null;
            BufferedOutputStream output = null;
            REF_DOC_dobj rEF_DOC_dobj = null;
            try {
                InputStream inStream = null;
                for (int i = 0; i < list.size(); i++) {
                    rEF_DOC_dobj = list.get(i);
                    inStream = new ByteArrayInputStream(rEF_DOC_dobj.getScan_doc());
                    break;
                }

                input = new BufferedInputStream(inStream);
                output = new BufferedOutputStream(response.getOutputStream());
                byte[] buffer = new byte[209600];

                // DefaultStreamedContent content =   new DefaultStreamedContent(new ByteArrayInputStream(imagedata), "image/png");


                //for (int length = 0; (length = input.read(buffer)) > 0;) {
                //  output.write(buffer, 0, length);
                // }
                output.write(rEF_DOC_dobj.getScan_doc());

            } finally {
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException logOrIgnore) {
                    }
                }
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException logOrIgnore) {
                    }
                }
            }
        } finally {
            // out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
