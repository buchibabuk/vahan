/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nic.vahan5.reg.rest.model;

/**
 *
 * @author Kartikey Singh
 */
public class ContextMessageModel {

    public static enum MessageContext {
        FACES, REQUEST
    }

    public static enum MessageSeverity {
        INFO, ERROR, WARN
    }
    private MessageContext messageContext;
    private MessageSeverity messageSeverity;
    private boolean isReturn;
    private String message1;
    private String message2;

    public ContextMessageModel() {
    }

    public MessageContext getMessageContext() {
        return messageContext;
    }

    public void setMessageContext(MessageContext messageContext) {
        this.messageContext = messageContext;
    }

    public MessageSeverity getMessageSeverity() {
        return messageSeverity;
    }

    public void setMessageSeverity(MessageSeverity messageSeverity) {
        this.messageSeverity = messageSeverity;
    }

    public boolean isIsReturn() {
        return isReturn;
    }

    public void setIsReturn(boolean isReturn) {
        this.isReturn = isReturn;
    }

    public String getMessage1() {
        return message1;
    }

    public void setMessage1(String message1) {
        this.message1 = message1;
    }

    public String getMessage2() {
        return message2;
    }

    public void setMessage2(String message2) {
        this.message2 = message2;
    }

    public ContextMessageModel(MessageContext messageContext, MessageSeverity messageSeverity, boolean isReturn, String message1, String message2) {
        this.messageContext = messageContext;
        this.messageSeverity = messageSeverity;
        this.isReturn = isReturn;
        this.message1 = message1;
        this.message2 = message2;
    }

    public ContextMessageModel(MessageContext messageContext, String message1) {
        this.messageContext = messageContext;
        this.message1 = message1;
    }
}
