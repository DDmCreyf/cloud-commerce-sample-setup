package com.mirakl.hybris.core.fulfilment.strategies.impl;

import static com.mirakl.hybris.core.enums.MarketplaceConsignmentPaymentStatus.FAILURE;
import static com.mirakl.hybris.core.enums.MarketplaceConsignmentPaymentStatus.INITIAL;
import static com.mirakl.hybris.core.enums.MarketplaceConsignmentPaymentStatus.SUCCESS;
import static java.math.BigDecimal.valueOf;
import static java.util.Collections.singletonList;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.payment.MiraklPaymentStatus;
import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderPayment;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.request.payment.debit.MiraklConfirmOrderDebitRequest;
import com.mirakl.hybris.core.fulfilment.strategies.ProcessMarketplacePaymentStrategy;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.order.services.TakePaymentService;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultProcessMarketplacePaymentStrategy implements ProcessMarketplacePaymentStrategy {

  protected ModelService modelService;
  protected TakePaymentService takePaymentService;
  protected MiraklMarketplacePlatformFrontApi mmpApi;
  protected MarketplaceConsignmentService marketplaceConsignmentService;

  @Override
  public boolean processPayment(MarketplaceConsignmentModel consignment, MiraklOrderPayment miraklOrderPayment) {
    boolean success = true;
    if (INITIAL.equals(consignment.getPaymentStatus())) {
      success = capturePayment(consignment, miraklOrderPayment);
    }
    confirmOrderDebitToMirakl(consignment, miraklOrderPayment);
    return success;
  }

  @Override
  public boolean processPayment(MarketplaceConsignmentModel consignment) {
    MiraklOrderPayment miraklOrderPayment = marketplaceConsignmentService.loadDebitRequest(consignment);
    if (miraklOrderPayment != null) {
      return processPayment(consignment, miraklOrderPayment);
    }
    return false;
  }

  protected boolean capturePayment(MarketplaceConsignmentModel consignment, MiraklOrderPayment miraklOrderPayment) {
    AbstractOrderModel order = consignment.getOrder();
    PaymentTransactionEntryModel txnEntry;
    if (valueOf(order.getTotalPrice()).compareTo(miraklOrderPayment.getAmount()) == 0) {
      txnEntry = takePaymentService.fullCapture(order);
    } else {
      txnEntry = takePaymentService.partialCapture(order, miraklOrderPayment.getAmount().doubleValue());
    }
    updatePaymentInformation(consignment, txnEntry);

    return SUCCESS.equals(consignment.getPaymentStatus());
  }

  protected void updatePaymentInformation(MarketplaceConsignmentModel consignment, PaymentTransactionEntryModel txnEntry) {
    boolean accepted = TransactionStatus.ACCEPTED.name().equals(txnEntry.getTransactionStatus());
    consignment.setPaymentStatus(accepted ? SUCCESS : FAILURE);
    consignment.setPaymentTransactionEntry(txnEntry);
    modelService.save(consignment);
  }

  protected void confirmOrderDebitToMirakl(MarketplaceConsignmentModel consignment, MiraklOrderPayment miraklOrderPayment) {
    if (SUCCESS.equals(consignment.getPaymentStatus())) {
      mmpApi.confirmOrderDebit(buildConfirmDebitRequest(miraklOrderPayment, consignment, true));
      return;
    }
    if (FAILURE.equals(consignment.getPaymentStatus())) {
      mmpApi.confirmOrderDebit(buildConfirmDebitRequest(miraklOrderPayment, consignment, false));
    }
  }

  protected MiraklConfirmOrderDebitRequest buildConfirmDebitRequest(MiraklOrderPayment miraklOrderPayment,
      MarketplaceConsignmentModel consignment, boolean success) {
    miraklOrderPayment.setPaymentStatus(success ? MiraklPaymentStatus.OK : MiraklPaymentStatus.REFUSED);
    if (consignment.getPaymentTransactionEntry() != null) {
      miraklOrderPayment.setTransactionDate(consignment.getPaymentTransactionEntry().getTime());
      miraklOrderPayment.setTransactionNumber(consignment.getPaymentTransactionEntry().getRequestId());
    }

    return new MiraklConfirmOrderDebitRequest(singletonList(miraklOrderPayment));
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setTakePaymentService(TakePaymentService takePaymentService) {
    this.takePaymentService = takePaymentService;
  }

  @Required
  public void setMmpApi(MiraklMarketplacePlatformFrontApi mmpApi) {
    this.mmpApi = mmpApi;
  }

  @Required
  public void setMarketplaceConsignmentService(MarketplaceConsignmentService marketplaceConsignmentService) {
    this.marketplaceConsignmentService = marketplaceConsignmentService;
  }
}
