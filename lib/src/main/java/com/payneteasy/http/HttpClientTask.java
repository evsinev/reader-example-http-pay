package com.payneteasy.http;

import com.payneteasy.http.log.IHttpLogger;
import com.payneteasy.http.request.HttpInputStreamImpl;
import com.payneteasy.http.request.HttpRequest;
import com.payneteasy.http.request.HttpRequestHeaders;
import com.payneteasy.http.request.HttpRequestHeadersParser;
import com.payneteasy.http.request.HttpRequestLine;
import com.payneteasy.http.request.HttpRequestLineParser;
import com.payneteasy.http.request.HttpRequestMessageBody;
import com.payneteasy.http.request.HttpRequestMessageBodyParser;
import com.payneteasy.http.request.IHttpInputStream;
import com.payneteasy.http.response.HttpResponse;
import com.payneteasy.http.response.HttpResponseStreamImpl;
import com.payneteasy.http.response.IHttpResponseStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpClientTask implements Runnable {

    private final Socket                       socket;
    private final IHttpLogger                  logger;
    private final HttpRequestLineParser        requestLineParser;
    private final HttpRequestHeadersParser     requestHeadersParser;
    private final HttpRequestMessageBodyParser requestBodyParser;
    private final IHttpRequestHandler          requestHandler;

    public HttpClientTask(Socket aSocket, IHttpLogger aLogger, IHttpRequestHandler aHandler) {
        socket               = aSocket;
        logger               = aLogger;
        requestLineParser    = new HttpRequestLineParser(aLogger);
        requestHeadersParser = new HttpRequestHeadersParser(aLogger);
        requestBodyParser    = new HttpRequestMessageBodyParser(aLogger);
        requestHandler       = aHandler;
    }

    @Override
    public void run() {
        try {

            processStreams(socket.getInputStream(), socket.getOutputStream());

        } catch (Exception e) {
            logger.error("Error while processing client", e);
        } finally {
            closeSocket();
        }
    }

    private void processStreams(InputStream aIn, OutputStream aOut) throws IOException {
        IHttpInputStream       httpInputStream = new HttpInputStreamImpl(aIn);
        HttpRequestLine        requestLine     = requestLineParser.parseRequestLine(httpInputStream);
        HttpRequestHeaders     requestHeaders  = requestHeadersParser.parseHeaders(httpInputStream);
        HttpRequestMessageBody requestBody     = requestBodyParser.parseMessageBody(httpInputStream, requestHeaders);
        HttpRequest            request         = new HttpRequest(requestLine, requestHeaders, requestBody);

        logger.debug("Request", "line", requestLine, "headers", requestHeaders, "body", requestBody);

        HttpResponse        response       = requestHandler.handleRequest(request);
        IHttpResponseStream responseStream = new HttpResponseStreamImpl(aOut);

        responseStream.writeStatusLine  ( response.getStatusLine() );
        responseStream.writeHeaders     ( response.getHeaders()    );
        responseStream.writeBody        ( response.getBody()       );

        logger.debug("Wrote response", "status", response.getStatusLine(), "headers", response.getHeaders(), "body", response.getBody());
    }

    private void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            logger.error("Cannot close socket", e);
        }
    }
}
