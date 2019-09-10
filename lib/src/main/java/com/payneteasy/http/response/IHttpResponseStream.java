package com.payneteasy.http.response;

import java.io.IOException;

public interface IHttpResponseStream {

    void writeStatusLine(HttpResponseStatusLine aStatusLine) throws IOException;

    void writeHeaders(HttpResponseHeaders aHeaders) throws IOException;

    void writeBody(HttpResponseMessageBody aBody) throws IOException;
}
