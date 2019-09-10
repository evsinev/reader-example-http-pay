package com.payneteasy.http.log;

public interface IHttpLogger {

    void debug(String aMessage, Object ... args);

    void error(String aMessage);

    void error(String aMessage, Exception aException);
}
