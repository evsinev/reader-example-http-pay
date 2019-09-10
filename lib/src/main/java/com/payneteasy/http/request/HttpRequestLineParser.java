package com.payneteasy.http.request;

import com.payneteasy.http.log.IHttpLogger;

import java.io.IOException;

public class HttpRequestLineParser {

    private final IHttpLogger logger;

    public HttpRequestLineParser(IHttpLogger aLogger) {
        logger = aLogger;
    }

    public HttpRequestLine parseRequestLine(IHttpInputStream aStream) throws IOException {

        String method  = aStream.readUntilSpace();
        String uri     = aStream.readUntilSpace();
        String version = aStream.readUntilCrlf();

        return new HttpRequestLine(method, uri, version);
    }
}
