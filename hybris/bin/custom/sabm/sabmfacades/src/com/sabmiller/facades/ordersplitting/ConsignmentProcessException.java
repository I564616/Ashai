package com.sabmiller.facades.ordersplitting;

/**
 * Created by zhuo.a.jiang on 2/02/2018.
 */
public class ConsignmentProcessException extends Exception{


    public ConsignmentProcessException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public ConsignmentProcessException(final String message)
    {
        super(message);
    }

    public ConsignmentProcessException(final Throwable cause)
    {
        super(cause);
    }

    public ConsignmentProcessException(final String message, String stackTraceInfo)
    {
        super(message);
    }
}
