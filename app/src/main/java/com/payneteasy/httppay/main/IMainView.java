package com.payneteasy.httppay.main;

public interface IMainView {


    void errorConnecting(String aErrorMessage);

    void onSuccessConnected();
    void onConnecting();

    void onMessage(String aMessage);
}
