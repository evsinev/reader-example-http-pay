package com.payneteasy.http;

import com.payneteasy.http.log.HttpLoggerSystemOut;
import com.payneteasy.http.log.IHttpLogger;
import com.payneteasy.http.response.HttpResponseBuilder;
import com.payneteasy.http.response.HttpResponseStatusLine;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class HttpServer {

    private final ServerSocket        serverSocket;
    private final AtomicBoolean       isRunning = new AtomicBoolean(true);
    private final IHttpLogger         log;
    private final ExecutorService     executorService;
    private final IHttpRequestHandler handler;
    private final Thread              thread;
    private final int                 readTimeoutMs;

    public HttpServer(InetSocketAddress aBindAddress
            , IHttpLogger aLog
            , ExecutorService aExecutor
            , IHttpRequestHandler aHandler
            , int aReadTimeoutMs
    ) throws IOException {
        log             = aLog;
        executorService = aExecutor;
        handler         = aHandler;
        thread          = Thread.currentThread();
        readTimeoutMs   = aReadTimeoutMs;

        serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(aBindAddress);
    }

    public void acceptSocketAndWait() {
        if (!thread.equals(Thread.currentThread())) {
            throw new IllegalStateException("invoke acceptSocketAndWait() in the same thread as constructor");
        }

        log.debug("Start listening...", "serverSocket", serverSocket);
        while (!Thread.currentThread().isInterrupted() && isRunning.get()) {
            try {
                Socket clientSocket = serverSocket.accept();
                clientSocket.setSoTimeout(readTimeoutMs);
                clientSocket.setTcpNoDelay(true);

                log.debug("Connected from client", "remote address", clientSocket.getRemoteSocketAddress());

                executorService.execute(new HttpClientTask(clientSocket, log, handler));

            } catch (Exception e) {
                if(Thread.currentThread().isInterrupted() || !isRunning.get()) {
                    log.error("Exiting from the listening server socket ...");
                    break;
                }
                log.error("Cannot deal with socket", e);
            }
        }
        log.debug("Exited from accept loop.");
    }

    public void stop() {
        log.debug("Stopping ...");
        isRunning.set(false);
        thread.interrupt();
        try {
            serverSocket.close();
        } catch (Exception e) {
            log.error("Cannot close server socket", e);
        }
    }

    public static void main(String[] args) throws IOException {
        IHttpRequestHandler handler = aRequest -> HttpResponseBuilder.status(HttpResponseStatusLine.OK)
                .addHeader("Server", "Test")
                .body("Hello".getBytes())
                .build();
        
        HttpServer server = new HttpServer(
                new InetSocketAddress(8081)
                , new HttpLoggerSystemOut()
                , Executors.newCachedThreadPool()
                , handler
                , 10_000
        );
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        server.acceptSocketAndWait();
    }

}
