package com.sabmiller.facades.order;

public class CartStateException extends RuntimeException {

    public CartStateException(final String msg){
        super(msg);
    }

    public CartStateException(final String msg, final Throwable e){
        super(msg, e);
    }
}
