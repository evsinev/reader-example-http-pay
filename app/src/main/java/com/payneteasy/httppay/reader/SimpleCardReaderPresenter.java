package com.payneteasy.httppay.reader;

import android.os.Handler;
import android.os.Looper;

import com.payneteasy.android.sdk.card.BankCard;
import com.payneteasy.android.sdk.processing.ConfigurationContinuation;
import com.payneteasy.android.sdk.processing.IProcessingStageListener;
import com.payneteasy.android.sdk.processing.ProcessingContinuation;
import com.payneteasy.android.sdk.processing.ProcessingStageEvent;
import com.payneteasy.android.sdk.reader.*;
import com.payneteasy.httppay.service.StartSessionRequest;
import com.payneteasy.reader.i18n.IReaderI18nService;
import com.payneteasy.reader.i18n.ReaderI18nServiceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class SimpleCardReaderPresenter implements ICardReaderPresenter {

    private final static Logger LOG = LoggerFactory.getLogger(SimpleCardReaderPresenter.class);

    private final Handler             handler       = new Handler(Looper.getMainLooper());
    private final File                filesDir;
    private final ICardView           cardView;
    private final IReaderI18nService  translationService;
    private final Locale              defaultLocale = new Locale("ru");
    private final StartSessionRequest parameters;

    public SimpleCardReaderPresenter(ICardView aCardView, File aFilesDir, StartSessionRequest aParameters) {
        filesDir   = aFilesDir;
        cardView   = aCardView;
        parameters = aParameters;
        try {
            translationService = new ReaderI18nServiceBuilder()
                    .addPropertyFile(Locale.ENGLISH, "reader_en.properties")
                    .addPropertyFile(new Locale("ru"), "reader_ru.properties")
                    .build();
        } catch (IOException e) {
            throw new IllegalStateException("Error creating i18n service", e);
        }
    }


    @Override
    public ProcessingContinuation onCard(BankCard bankCard) {

        setStatus("onCard: %s", bankCard);

        return ProcessingContinuation.Builder
                .startSaleOnline()
                .processingBaseUrl  ( parameters.processingBaseUrl)
                .merchantLogin      ( parameters.merchantLogin )
                .merchantControlKey ( parameters.merchantControlKey   )
                .merchantEndPointId ( parameters.endpointId   )
                .orderDescription   ( "test description"    )
                .orderInvoiceNumber ( parameters.invoice )
                .orderMerchantData  ( "custom merchant data for internal use")
                .customerPhone      ( "+7 (499) 918-64-41"  )
                .customerEmail      ( "info@payneteasy.com" )
                .customerCountry    ( "RUS"                 )
                .listener(new IProcessingStageListener() {
                    @Override
                    public void onStageChanged(final ProcessingStageEvent aEvent) {
                        String message = translationService.translateProcessingEvent(defaultLocale, aEvent);
                        setStatus(message);
                        if(aEvent.type == ProcessingStageEvent.Type.RESULT) {
                            handler.post(() -> cardView.stopReaderManager(aEvent.response));
                        }
                    }
                })
                .build();

    }

    private void setStatus(final String aFormat, final Object ... args) {
        final String outputText = String.format(aFormat, args);
        LOG.debug("Status: {}", outputText);

        cardView.setStatusText(outputText);
    }

    @Override
    public void onReaderSerialNumber(String aKsn) {
        setStatus("onReaderSerialNumber: %s", aKsn);
    }

    @Override
    public void cardReaderStateChanged(CardReaderEvent cardReaderEvent) {
        setStatus("cardReaderStateChanged: %s", translationService.translateReaderEvent(defaultLocale, cardReaderEvent));
    }

    @Override
    public void onCardError(CardError cardError) {
        setStatus("onCardError: %s", translationService.translateCardError(defaultLocale, cardError));
        cardView.stopReaderManager(cardError);
    }

    @Override
    public void onReaderNotSupported(CardReaderProblem aProblem) {
        setStatus("onReaderNotSupported: %s", translationService.translateCardReaderProblem(defaultLocale, aProblem));
        cardView.stopReaderManager(aProblem);
    }

    @Override
    public void onAudioData(short[] shorts, int i) {
        // for visualization
    }

    @Override
    public ConfigurationContinuation onConfiguration() {
        return new ConfigurationContinuation.Builder()
                .configDir              ( new File(filesDir, "miura-config"))
                .configurationBaseUrl   ( parameters.configBaseUrl  )
                .merchantLogin          ( parameters.merchantLogin     )
                .merchantControlKey     ( parameters.merchantControlKey       )
                .merchantEndPointId     ( parameters.endpointId       )
                .merchantName           ( parameters.merchantLogin      ) // For Spire SPm2. Only Latin characters supported.
                .build();
    }
}
