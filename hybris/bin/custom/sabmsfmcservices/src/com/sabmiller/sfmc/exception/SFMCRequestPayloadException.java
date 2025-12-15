package com.sabmiller.sfmc.exception;

/**
 * Exception class when there is a problem with Request Payload sent to
 * SFMC
 */
public class SFMCRequestPayloadException extends Exception {

    public SFMCRequestPayloadException() { super(); }
    public SFMCRequestPayloadException(String message) { super(message); }
    public SFMCRequestPayloadException(String message, Throwable cause) { super(message, cause); }
    public SFMCRequestPayloadException(Throwable cause) { super(cause); }

}
