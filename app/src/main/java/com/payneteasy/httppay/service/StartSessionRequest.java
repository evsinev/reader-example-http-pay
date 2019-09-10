package com.payneteasy.httppay.service;

import java.math.BigDecimal;

public class StartSessionRequest {

    public final String     cardReaderType;
    public final String     sessionType; // sale or reversal
    public final String     processingBaseUrl;
    public final String     configBaseUrl;
    public final String     merchantLogin;
    public final String     merchantControlKey;
    public final long       endpointId;
    public final String     invoice;
    public final BigDecimal amount;
    public final String     currency;
    public final Long       reversalOrderId;
    public final String     reversalComment;

    public StartSessionRequest(String cardReaderType, String sessionType, String processingBaseUrl, String configBaseUrl, String merchantLogin, String merchantControlKey, long endpointId, String invoice, BigDecimal amount, String currency, Long reversalOrderId, String reversalComment) {
        this.cardReaderType     = cardReaderType;
        this.sessionType        = sessionType;
        this.processingBaseUrl  = processingBaseUrl;
        this.configBaseUrl      = configBaseUrl;
        this.merchantLogin      = merchantLogin;
        this.merchantControlKey = merchantControlKey;
        this.endpointId         = endpointId;
        this.invoice            = invoice;
        this.amount             = amount;
        this.currency           = currency;
        this.reversalOrderId    = reversalOrderId;
        this.reversalComment    = reversalComment;
    }
}
