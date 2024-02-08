package com.mirakl.hybris.facades.order.converters.populator;

import com.mirakl.hybris.core.order.strategies.MarketplaceConsignmentMessagesStrategy;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.enums.MiraklOrderStatus;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;

import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MarketplaceConsignmentPopulator implements Populator<ConsignmentModel, ConsignmentData> {

  protected EnumerationService enumerationService;
  protected MarketplaceConsignmentMessagesStrategy messagesStrategy;

  @Override
  public void populate(ConsignmentModel consignmentModel, ConsignmentData consignmentData) throws ConversionException {
    if (consignmentModel instanceof MarketplaceConsignmentModel) {

      MarketplaceConsignmentModel marketplaceConsignment = (MarketplaceConsignmentModel) consignmentModel;
      consignmentData.setShippingModeLabel(marketplaceConsignment.getShippingTypeLabel());
      consignmentData.setCanEvaluate(marketplaceConsignment.getCanEvaluate());
      consignmentData.setCode(consignmentModel.getCode());

      MiraklOrderStatus marketplaceOrderStatus = marketplaceConsignment.getMiraklOrderStatus();
      if (marketplaceOrderStatus != null) {
        consignmentData.setMarketplaceStatus(marketplaceOrderStatus.getCode());
        consignmentData.setMarketplaceStatusLabel(enumerationService.getEnumerationName(marketplaceOrderStatus));
      }

      consignmentData.setCanWriteMessage(messagesStrategy.canWriteMessages(marketplaceConsignment));
      consignmentData.setCustomerDebited(marketplaceConsignment.getCustomerDebitDate() != null);

    }
  }

  @Required
  public void setEnumerationService(EnumerationService enumerationService) {
    this.enumerationService = enumerationService;
  }

  @Required
  public void setMessagesStrategy(MarketplaceConsignmentMessagesStrategy messagesStrategy) {
    this.messagesStrategy = messagesStrategy;
  }
}
