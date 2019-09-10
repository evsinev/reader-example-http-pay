package com.payneteasy.http;

import com.payneteasy.http.request.HttpRequest;
import com.payneteasy.http.response.HttpResponse;

public interface IHttpRequestHandler {

    HttpResponse handleRequest(HttpRequest aRequest);

}
