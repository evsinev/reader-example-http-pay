package com.payneteasy.httppay.service;

import com.payneteasy.http.HttpServer;
import com.payneteasy.http.IHttpRequestHandler;
import com.payneteasy.http.log.HttpLoggerSystemOut;
import com.payneteasy.httppay.main.IMainView;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class HttpServerThread extends Thread {

    private final IMainView                   view;
    private final AtomicReference<HttpServer> serverRef = new AtomicReference<>();
    private final IHttpRequestHandler         httpHandler;

    public HttpServerThread(IMainView aView, IHttpRequestHandler aHandler) {
        view        = aView;
        httpHandler = aHandler;
    }

    @Override
    public void run() {
        try {
            HttpServer server = new HttpServer(
                    new InetSocketAddress(8081)
                    , new HttpLoggerSystemOut()
                    , Executors.newFixedThreadPool(10)
                    , httpHandler
                    , 60_000);
            serverRef.set(server);
            server.acceptSocketAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            view.errorConnecting("Server error:" + e.getMessage());
        }


    }

    public void stopServer() {
        HttpServer server = serverRef.get();
        server.stop();
    }
}
