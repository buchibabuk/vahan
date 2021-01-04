/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.exception;

import nic.rto.vahan.common.VahanException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 *
 * @author Kartikey Singh
 */
@ControllerAdvice
public class VahanExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {VahanException.class})
    protected ResponseEntity<Object> handleConflict(VahanException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), ex.getCode(), request);
    }
}
