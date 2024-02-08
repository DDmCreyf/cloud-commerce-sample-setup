package com.mirakl.hybris.core.payment.services.impl;

import com.mirakl.hybris.beans.MiraklRefundRequestData;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;
import com.mirakl.hybris.core.payment.services.MiraklRefundService;
import com.mirakl.hybris.core.returns.strategies.MiraklRefundValidationStrategy;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.returns.ReturnService;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public class DefaultMiraklRefundService implements MiraklRefundService {
  private static final Logger LOG = Logger.getLogger(DefaultMiraklRefundService.class);

  protected MarketplaceConsignmentService marketplaceConsignmentService;
  protected ReturnService returnService;
  protected ModelService modelService;
  protected MiraklRefundValidationStrategy validationStrategy;

  @Override
  public void saveReceivedRefundRequest(MiraklRefundRequestData refundRequest) {
    checkNotNull(refundRequest, "Received a null refund request");

    if (!validationStrategy.isValidRefundRequest(refundRequest)) {
      LOG.warn(format("Invalid refund request [%s]. Skipping..", refundRequest.getRefundId()));
      return;
    }

    ConsignmentEntryModel consignmentEntry =
        marketplaceConsignmentService.getConsignmentEntryForMiraklLineId(refundRequest.getMiraklOrderLineId());
    OrderModel order = (OrderModel) consignmentEntry.getConsignment().getOrder();

    ReturnRequestModel request = createReturnRequest(order);
    createRefundEntry(consignmentEntry, request, refundRequest);
  }

  protected ReturnRequestModel createReturnRequest(OrderModel order) {
    ReturnRequestModel request = returnService.createReturnRequest(order);
    if (LOG.isDebugEnabled()) {
      LOG.debug(format("Created a return request for order [%s]", order.getCode()));
    }
    return request;
  }

  protected void createRefundEntry(ConsignmentEntryModel consignmentEntry, ReturnRequestModel request,
      MiraklRefundRequestData refundRequest) {
    AbstractOrderEntryModel orderEntry = consignmentEntry.getOrderEntry();
    RefundEntryModel refundEntry = returnService.createRefund(request, orderEntry, null, consignmentEntry.getQuantity(),
        ReturnAction.IMMEDIATE, RefundReason.MARKETPLACE_SELLER_REFUND);
    refundEntry.setMiraklRefundId(refundRequest.getRefundId());
    refundEntry.setAmount(refundRequest.getAmount());
    modelService.save(refundEntry);

    if (LOG.isDebugEnabled()) {
      LOG.debug(format("Created a refund for order entry [%s-%s]", orderEntry.getOrder().getCode(), orderEntry.getEntryNumber()));
    }
  }

  @Required
  public void setMarketplaceConsignmentService(MarketplaceConsignmentService marketplaceConsignmentService) {
    this.marketplaceConsignmentService = marketplaceConsignmentService;
  }

  @Required
  public void setReturnService(ReturnService returnService) {
    this.returnService = returnService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setValidationStrategy(MiraklRefundValidationStrategy validationStrategy) {
    this.validationStrategy = validationStrategy;
  }

}
