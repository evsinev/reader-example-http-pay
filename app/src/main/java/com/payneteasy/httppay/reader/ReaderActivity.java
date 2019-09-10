package com.payneteasy.httppay.reader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.TextView;

import com.payneteasy.android.sdk.reader.CardError;
import com.payneteasy.android.sdk.reader.CardReaderFactory;
import com.payneteasy.android.sdk.reader.CardReaderInfo;
import com.payneteasy.android.sdk.reader.CardReaderProblem;
import com.payneteasy.android.sdk.reader.CardReaderType;
import com.payneteasy.android.sdk.reader.ICardReaderManager;
import com.payneteasy.android.sdk.reader.ReversalSessionParameters;
import com.payneteasy.httppay.R;
import com.payneteasy.httppay.service.StartSessionRequest;
import com.payneteasy.httppay.util.ActivityUtil;
import com.payneteasy.httppay.util.Gsons;
import com.payneteasy.paynet.processing.response.StatusResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class ReaderActivity extends Activity implements ICardView {

    private final static Logger LOG = LoggerFactory.getLogger(ReaderActivity.class);

    private ICardReaderManager cardReaderManager;
    private TextView           statusView;
    private Handler            handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_reader);

        String              json       = ActivityUtil.getFirstParameter(this, String.class);
        StartSessionRequest parameters = Gsons.PRETTY_GSON.fromJson(json, StartSessionRequest.class);

        initUi();

        initReader(parameters);

        if ("reversal".equals(parameters.sessionType)) {
            cardReaderManager.startReversalSession(ReaderActivity.this, new ReversalSessionParameters(parameters.reversalOrderId, parameters.reversalComment));
        } else {
            cardReaderManager.startSaleSession(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cardReaderManager != null) {
            cardReaderManager.stopSession();
            cardReaderManager = null;
        }
    }

    private void initReader(StartSessionRequest parameters) {
        BigDecimal                amount         = parameters.amount;
        String                    currency       = parameters.currency;
        SimpleCardReaderPresenter presenter      = new SimpleCardReaderPresenter(this, getFilesDir(), parameters);
        CardReaderInfo            cardReaderInfo = new CardReaderInfo("tps900", CardReaderType.TELPO_TPS900, null);

        cardReaderManager = CardReaderFactory.findManager(this, cardReaderInfo, presenter, amount, currency, null);
    }

    private void initUi() {
        statusView = (TextView) findViewById(R.id.statusView);
    }

    @Override
    public void setStatusText(final String aText) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusView.setText(aText);
            }
        });
    }

    @Override
    public void stopReaderManager(StatusResponse aResponse) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("status-response", aResponse);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(20, intent);

        finishWithDelay();
    }

    private void finishWithDelay() {
        if (cardReaderManager != null) {
            finish();
        }
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (cardReaderManager != null) {
//                    finish();
//                }
//            }
//        }, 3000);
    }

    @Override
    public void stopReaderManager(CardReaderProblem aProblem) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("problem", aProblem);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(20, intent);

        finishWithDelay();
    }

    @Override
    public void stopReaderManager(CardError aCardError) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("card-error-type", aCardError.getType());
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(20, intent);

        finishWithDelay();
    }
}
