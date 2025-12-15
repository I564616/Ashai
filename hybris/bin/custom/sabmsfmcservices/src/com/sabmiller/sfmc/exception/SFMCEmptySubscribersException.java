package com.sabmiller.sfmc.exception;

/**
 * Exception class when there are n o subscribers for an email or sms
 */
public class SFMCEmptySubscribersException  extends Exception {

    public SFMCEmptySubscribersException() { super(); }
    public SFMCEmptySubscribersException(String message) { super(message); }
    public SFMCEmptySubscribersException(String message, Throwable cause) { super(message, cause); }
    public SFMCEmptySubscribersException(Throwable cause) { super(cause); }
}
