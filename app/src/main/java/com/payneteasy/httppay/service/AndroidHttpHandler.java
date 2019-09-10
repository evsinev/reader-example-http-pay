package com.payneteasy.httppay.service;

import com.payneteasy.android.sdk.logger.ILogger;
import com.payneteasy.android.sdk.util.LoggerUtil;
import com.payneteasy.http.IHttpRequestHandler;
import com.payneteasy.http.request.HttpRequest;
import com.payneteasy.http.response.HttpResponse;
import com.payneteasy.httppay.main.IMainRouter;
import com.payneteasy.httppay.util.Gsons;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

import static com.payneteasy.http.response.HttpResponseBuilder.statusOk;

public class AndroidHttpHandler implements IHttpRequestHandler {

    private static final ILogger LOG = LoggerUtil.create(AndroidHttpHandler.class);

    private final IMainRouter mainRouter;
    private final Exchanger<StartSessionResponse> exchanger = new Exchanger<>();

    public AndroidHttpHandler(IMainRouter mainRouter) {
        this.mainRouter = mainRouter;
    }

    @Override
    public HttpResponse handleRequest(HttpRequest aRequest) {

        String requestJson = aRequest.getBody().asString(StandardCharsets.UTF_8);
        LOG.debug("Received {}", requestJson);

        mainRouter.showCardReaderScreen(requestJson);

        try {
            StartSessionResponse response = exchanger.exchange(null, 1, TimeUnit.MINUTES);
            String responseJson = Gsons.PRETTY_GSON.toJson(response);
            return statusOk()
                    .body(responseJson.getBytes(StandardCharsets.UTF_8))
                    .build();
        } catch (Exception e) {
            LOG.error("Cannot process request", e);
            StartSessionResponse response = new StartSessionResponse(null, e.getMessage());
            String responseJson = Gsons.PRETTY_GSON.toJson(response);
            return statusOk()
                    .body(responseJson.getBytes(StandardCharsets.UTF_8))
                    .build();
        }
    }

    public void sendResponse(StartSessionResponse aResponse) {
        try {
            exchanger.exchange(aResponse, 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOG.error("Cannot send response", e);
        }
    }
}
