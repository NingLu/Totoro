package com.test.totoro.utils;

public class TotoroException extends Exception {

    private static final long serialVersionUID = -2364191086785696220L;

    public TotoroException(String arg0) {
        super(arg0);
    }


    public TotoroException(Throwable e) {
        super(e);
    }
}
