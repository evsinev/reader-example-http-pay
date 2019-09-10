package com.payneteasy.httppay.service;

import com.payneteasy.paynet.processing.response.StatusResponse;

public class StartSessionResponse {

    public final StatusResponse    statusResponse;
    public final String            errorMessage;

    public StartSessionResponse(StatusResponse statusResponse, String errorMessage) {
        this.statusResponse = statusResponse;
        this.errorMessage   = errorMessage;
    }

}
