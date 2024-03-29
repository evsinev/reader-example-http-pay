package com.payneteasy.http.request;

import java.io.IOException;

public interface IHttpInputStream {

    String readUntilSpace() throws IOException;

    String readUntilCrlf() throws IOException;

    String readerUntilColonOrCrlf() throws IOException;

    byte[] readBytes(int aLength) throws IOException;
}
