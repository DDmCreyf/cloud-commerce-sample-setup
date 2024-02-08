package com.mirakl.hybris.core.payment.services;

import com.mirakl.hybris.beans.MiraklRefundRequestData;

public interface MiraklRefundService {

    /**
     * Handles a Mirakl Refund when received
     *
     * @param miraklRefundRequestData The data required to save a refund request.
     */
    void saveReceivedRefundRequest(MiraklRefundRequestData miraklRefundRequestData);
}
