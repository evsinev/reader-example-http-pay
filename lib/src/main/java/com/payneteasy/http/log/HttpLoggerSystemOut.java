package com.payneteasy.http.log;

public class HttpLoggerSystemOut implements IHttpLogger {

    private final LogMessageFormatter formatter = new LogMessageFormatter();

    @Override
    public void debug(String aMessage, Object... args) {
        System.out.println(formatter.format(aMessage, args));
    }

    @Override
    public void error(String aMessage) {
        System.err.println(formatter.format(aMessage));
    }

    @Override
    public void error(String aMessage, Exception aException) {
        System.err.println(formatter.formatException(aMessage, aException));
    }
}
