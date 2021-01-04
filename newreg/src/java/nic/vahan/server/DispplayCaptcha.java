/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan.server;

import java.awt.Color;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.captcha.Captcha;
import nl.captcha.backgrounds.BackgroundProducer;
import nl.captcha.backgrounds.FlatColorBackgroundProducer;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.backgrounds.SquigglesBackgroundProducer;
import nl.captcha.backgrounds.TransparentBackgroundProducer;
import nl.captcha.gimpy.BlockGimpyRenderer;
import nl.captcha.gimpy.DropShadowGimpyRenderer;
import nl.captcha.gimpy.FishEyeGimpyRenderer;
import nl.captcha.gimpy.GimpyRenderer;
import nl.captcha.gimpy.RippleGimpyRenderer;
import nl.captcha.gimpy.ShearGimpyRenderer;
import nl.captcha.gimpy.StretchGimpyRenderer;
import nl.captcha.noise.CurvedLineNoiseProducer;
import nl.captcha.noise.NoiseProducer;
import nl.captcha.noise.StraightLineNoiseProducer;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.text.producer.DefaultTextProducer;
import nl.captcha.text.producer.FiveLetterFirstNameTextProducer;
import nl.captcha.text.producer.NumbersAnswerProducer;
import nl.captcha.text.producer.TextProducer;
import org.apache.log4j.Logger;

/**
 *
 * @author Ashok Kumar <send2ashok@hotmail.com>
 */
public class DispplayCaptcha extends HttpServlet {

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
    private static final long serialVersionUID = 1L;
    private static final String PARAM_HEIGHT = "height";
    private static final String PARAM_WIDTH = "width";
    protected int _width = 130;
    protected int _height = 50;
    private static final Logger LOGGER = Logger.getLogger(DispplayCaptcha.class);

    @Override
    public void init() throws ServletException {
        if (getInitParameter(PARAM_HEIGHT) != null) {
            _height = Integer.valueOf(getInitParameter(PARAM_HEIGHT));
        }
        if (getInitParameter(PARAM_WIDTH) != null) {
            _width = Integer.valueOf(getInitParameter(PARAM_WIDTH));
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
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            TextProducer txtp = new DefaultTextProducer();
            BackgroundProducer bkgp = new FlatColorBackgroundProducer(java.awt.Color.green);
            NoiseProducer noisep = new StraightLineNoiseProducer(Color.yellow, 2);
            GimpyRenderer gmren = new BlockGimpyRenderer();

            int txtp_cd = 1;
            int bkgp_cd = 1;
            int gimp_cd = 1;
            int noise_cd = 1;
            int txtp_length = 2;
            try {
                txtp_cd = Integer.parseInt(req.getParameter("txtp_cd"));
                if (req.getParameter("txtp_cd") != null) {
                    txtp_length = Integer.parseInt(req.getParameter("txtp_length"));
                }
                if (txtp_cd == 2) {

                    txtp = new NumbersAnswerProducer(txtp_length);
                }
                if (txtp_cd == 3) {

                    txtp = new FiveLetterFirstNameTextProducer();
                }

            } catch (Exception e) {
            }

            try {

                bkgp_cd = Integer.parseInt(req.getParameter("bkgp_cd"));

                if (bkgp_cd == 2) {

                    bkgp = new GradiatedBackgroundProducer();
                }
                if (bkgp_cd == 3) {

                    bkgp = new TransparentBackgroundProducer();
                }
                if (bkgp_cd == 4) {

                    bkgp = new SquigglesBackgroundProducer();
                }


            } catch (Exception e) {
            }

            try {

                noise_cd = Integer.parseInt(req.getParameter("noise_cd"));

                if (bkgp_cd == 2) {

                    noisep = new CurvedLineNoiseProducer();
                }



            } catch (Exception e) {
            }
            try {

                gimp_cd = Integer.parseInt(req.getParameter("gimp_cd"));

                if (gimp_cd == 2) {

                    gmren = new BlockGimpyRenderer();
                }
                if (gimp_cd == 3) {

                    gmren = new DropShadowGimpyRenderer();
                }
                if (gimp_cd == 4) {

                    gmren = new FishEyeGimpyRenderer();
                }
                if (gimp_cd == 5) {

                    gmren = new RippleGimpyRenderer();
                }
                if (gimp_cd == 6) {

                    gmren = new ShearGimpyRenderer();
                }
                if (gimp_cd == 7) {

                    gmren = new StretchGimpyRenderer();
                }

            } catch (Exception e) {
            }


            Captcha captcha = new Captcha.Builder(_width, _height)
                    .addText(txtp)
                    .addBackground(bkgp)
                    // .gimp(gmren)
                    .addNoise(noisep)
                    .addBorder()
                    .build();
            CaptchaServletUtil.writeImage(resp, captcha.getImage());

            req.getSession().setAttribute("serverCaptcha", captcha);
        } catch (Throwable t) {
        }
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        //processRequest(request, response);
        try {
            throw new UnsupportedOperationException("Method not supported.");
        } catch (Throwable e) {
            LOGGER.error(e.toString() + " " + e.getStackTrace()[0]);
        }

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
