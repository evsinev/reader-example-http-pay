package com.payneteasy.httppay.main;

public class MainPresenterImpl implements IMainPresenter {

    private final IMainView view;

    public MainPresenterImpl(IMainView view) {
        this.view = view;
    }


    @Override
    public void connect() {
        view.onConnecting();
    }


    @Override
    public void dumpState() {
    }
}
