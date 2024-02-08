package com.mirakl.hybris.core.payment.services;


import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderPayment;

public interface MiraklDebitService {

    /**
     * Handles a Mirakl Debit when received
     *
     * @param miraklOrderPayment The debit order.
     */
    void saveReceivedDebitRequest(MiraklOrderPayment miraklOrderPayment);
}
