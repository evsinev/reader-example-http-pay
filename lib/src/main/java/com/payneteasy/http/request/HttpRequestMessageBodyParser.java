package com.payneteasy.http.request;

import com.payneteasy.http.log.IHttpLogger;

import java.io.IOException;

public class HttpRequestMessageBodyParser {

    private final IHttpLogger logger;

    public HttpRequestMessageBodyParser(IHttpLogger aLogger) {
        logger = aLogger;
    }

    public HttpRequestMessageBody parseMessageBody(IHttpInputStream aStream, HttpRequestHeaders aHeaders) throws IOException {
        int    length = aHeaders.getContentLength();
        byte[] bytes  = aStream.readBytes(length);
        return new HttpRequestMessageBody(bytes);
    }
}
