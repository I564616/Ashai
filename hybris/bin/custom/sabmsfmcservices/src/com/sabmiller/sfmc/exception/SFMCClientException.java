package com.sabmiller.sfmc.exception;

/**
 * SFMC Exception class if there is any problem with Clients.
 */
public class SFMCClientException extends Exception {

    public SFMCClientException() { super(); }
    public SFMCClientException(String message) { super(message); }
    public SFMCClientException(String message, Throwable cause) { super(message, cause); }
    public SFMCClientException(Throwable cause) { super(cause); }

}
