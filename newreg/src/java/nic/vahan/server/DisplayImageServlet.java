/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nic.java.util.CommonUtils;
import nic.vahan.form.impl.REF_DOC_Impl;

/**
 *
 * @author AMBRISH
 */
public class DisplayImageServlet extends HttpServlet {

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
        PrintWriter out = null;
        OutputStream os = null;
        try {


            /// validation logic need 
            REF_DOC_Impl impl = new REF_DOC_Impl();


            String appl_no = request.getParameter("appl_no");
            String scan_doc_no = request.getParameter("scan_doc_no");
            String scan_doc_purpose_cd = request.getParameter("scan_doc_purpose_cd");
            if (!CommonUtils.isNullOrBlank(appl_no) && !CommonUtils.isNullOrBlank(scan_doc_no)) {
                byte[] b = impl.fetchImageByteArray(appl_no, scan_doc_no);
                if (b != null) {
                    response.setContentType("image/jpeg");
                    response.setContentLength((int) b.length);
                    // response.setContentLength(10);
                    os = response.getOutputStream();
                    os.write(b);
                    os.close();
                } else {
                    response.setContentType("text/html;charset=UTF-8");
                    out = response.getWriter();
                    out.print("Image not found");
                }
            }
            else // in case required parameters not found
            {
                response.setContentType("text/html;charset=UTF-8");
                out = response.getWriter();
                out.print("Required parameters not found");

            }
        } finally {
            if (out != null) {
                out.close();
            }
            if (os != null) {
                os.close();
            }

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
