package com.sabmiller.sfmc.exception;

/**
 *
 * Exception class when Key is not found
 */
public class SFMCRequestKeyNotFoundException  extends Exception {
    public SFMCRequestKeyNotFoundException() { super(); }
    public SFMCRequestKeyNotFoundException(String message) { super(message); }
    public SFMCRequestKeyNotFoundException(String message, Throwable cause) { super(message, cause); }
    public SFMCRequestKeyNotFoundException(Throwable cause) { super(cause); }
}

