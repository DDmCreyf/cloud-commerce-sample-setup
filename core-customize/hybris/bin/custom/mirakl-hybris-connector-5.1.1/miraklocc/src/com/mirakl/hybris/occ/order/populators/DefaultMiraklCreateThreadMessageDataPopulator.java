package com.mirakl.hybris.occ.order.populators;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.CreateThreadMessageData;
import com.mirakl.hybris.core.enums.MiraklThreadParticipantType;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DefaultMiraklCreateThreadMessageDataPopulator implements Populator<String, CreateThreadMessageData> {
  protected MarketplaceConsignmentService marketplaceConsignmentService;

  @Override
  public void populate(String consignmentCode, CreateThreadMessageData createThreadMessageData) throws ConversionException {
    final MarketplaceConsignmentModel consignment =
        marketplaceConsignmentService.getMarketplaceConsignmentForCode(consignmentCode);
    createThreadMessageData.getTo().stream()
        .filter(threadRecipientData -> MiraklThreadParticipantType.SHOP.getCode().equals(threadRecipientData.getType()))
        .forEach(threadRecipientData -> threadRecipientData.setId(consignment.getShopId()));
  }

  @Required
  public void setMarketplaceConsignmentService(MarketplaceConsignmentService marketplaceConsignmentService) {
    this.marketplaceConsignmentService = marketplaceConsignmentService;
  }
}
