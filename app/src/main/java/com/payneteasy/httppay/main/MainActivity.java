package com.payneteasy.httppay.main;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.payneteasy.android.sdk.reader.CardErrorType;
import com.payneteasy.android.sdk.reader.CardReaderProblem;
import com.payneteasy.httppay.R;
import com.payneteasy.httppay.reader.ReaderActivity;
import com.payneteasy.httppay.service.AndroidHttpHandler;
import com.payneteasy.httppay.service.HttpServerThread;
import com.payneteasy.httppay.service.StartSessionResponse;
import com.payneteasy.httppay.util.ActivityUtil;
import com.payneteasy.paynet.processing.response.StatusResponse;

public class MainActivity extends Activity implements IMainView, IMainRouter {

    IMainPresenter     presenter;
    TextView           connectionStatus;
    HttpServerThread   httpServerThread;
    AndroidHttpHandler httpHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        connectionStatus = findViewById(R.id.connectionStatus);

        presenter = new MainPresenterImpl(this);
        presenter.connect();

        Button dumpButton = findViewById(R.id.dumpButton);
        dumpButton.setOnClickListener(v -> presenter.dumpState());

        httpHandler      = new AndroidHttpHandler(this);
        httpServerThread = new HttpServerThread(this, httpHandler);
        httpServerThread.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        httpServerThread.stopServer();
    }

    @Override
    public void errorConnecting(String aErrorMessage) {
        runOnUiThread(() -> {
            connectionStatus.setText("ERROR: " + aErrorMessage);
            System.out.println("ERROR: " + aErrorMessage);
        });

    }

    @Override
    public void onSuccessConnected() {
        runOnUiThread(() -> {
            connectionStatus.setText("Connected");
            System.out.println("Connected");
        });
    }

    @Override
    public void onConnecting() {
        connectionStatus.setText("Connecting ...");
    }

    @Override
    public void onMessage(String aMessage) {
        runOnUiThread(() -> {
            connectionStatus.setText(aMessage);
        });
    }

    @Override
    public void showCardReaderScreen(String aJson) {
        ActivityUtil.startActivityForResult(100, this, ReaderActivity.class, aJson, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data == null) {
            httpHandler.sendResponse(new StartSessionResponse(null, "data is null"));
            return;
        }
        Bundle extras = data.getExtras();
        if(extras == null) {
            httpHandler.sendResponse(new StartSessionResponse(null, "extras is null"));
            return;
        }

        StatusResponse    statusResponse = (StatusResponse) extras.get("status-response");
        CardReaderProblem problem        = (CardReaderProblem) extras.get("problem");
        CardErrorType     errorType      = (CardErrorType) extras.get("card-error-type");

        String errorMessage;
        if(problem != null) {
            errorMessage = problem.name();
        } else if(errorType != null) {
            errorMessage = errorType.name();
        } else {
            errorMessage = null;
        }

        httpHandler.sendResponse(new StartSessionResponse(statusResponse, errorMessage));

        String message;
        if(problem != null) {
            message = "Error: " + problem;
        } else if( statusResponse != null) {
            message = statusResponse.getOrderId() + " " + statusResponse.getStatus();
        } else if( errorType != null ) {
            message = "Error: " + errorType;
        } else {
            message = "Unknown error";
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
