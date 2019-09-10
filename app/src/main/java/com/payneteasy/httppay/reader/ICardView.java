package com.payneteasy.httppay.reader;

import com.payneteasy.android.sdk.reader.CardError;
import com.payneteasy.android.sdk.reader.CardReaderProblem;
import com.payneteasy.paynet.processing.response.StatusResponse;

public interface ICardView {
    void setStatusText(String aText);

    void stopReaderManager(StatusResponse aResponse);

    void stopReaderManager(CardReaderProblem aProblem);

    void stopReaderManager(CardError aCardError);
}
