package com.sabmiller.facades.customer;

/**
 * Created by zhuo.a.jiang on 2/02/2018.
 */
public class InvoiceUpdateException extends Exception{


    public InvoiceUpdateException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public InvoiceUpdateException(final String message)
    {
        super(message);
    }

    public InvoiceUpdateException(final Throwable cause)
    {
        super(cause);
    }

    public InvoiceUpdateException(final String message, String stackTraceInfo)
    {
        super(message);
    }
}
