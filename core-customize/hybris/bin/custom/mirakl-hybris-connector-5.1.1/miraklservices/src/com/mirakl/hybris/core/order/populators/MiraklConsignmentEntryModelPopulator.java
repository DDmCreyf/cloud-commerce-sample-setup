package com.mirakl.hybris.core.order.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.order.MiraklOrderLine;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklConsignmentEntryModelPopulator implements Populator<MiraklOrderLine, ConsignmentEntryModel> {

  protected MarketplaceConsignmentService marketplaceConsignmentService;

  @Override
  public void populate(MiraklOrderLine orderLine, ConsignmentEntryModel consignmentEntry) throws ConversionException {
    validateParameterNotNullStandardMessage("orderLine", orderLine);
    validateParameterNotNullStandardMessage("consignmentEntry", consignmentEntry);

    marketplaceConsignmentService.storeMarketplaceConsignmentEntryCustomFields(orderLine.getAdditionalFields(), consignmentEntry);
  }

  @Required
  public void setMarketplaceConsignmentService(MarketplaceConsignmentService marketplaceConsignmentService) {
    this.marketplaceConsignmentService = marketplaceConsignmentService;
  }

}
