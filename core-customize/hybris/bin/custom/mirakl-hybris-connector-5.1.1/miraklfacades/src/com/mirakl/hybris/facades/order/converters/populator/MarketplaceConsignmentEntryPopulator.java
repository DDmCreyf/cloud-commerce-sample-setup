package com.mirakl.hybris.facades.order.converters.populator;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.enums.MiraklOrderLineStatus;

import de.hybris.platform.commercefacades.order.data.ConsignmentEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MarketplaceConsignmentEntryPopulator implements Populator<ConsignmentEntryModel, ConsignmentEntryData> {

  protected EnumerationService enumerationService;

  @Override
  public void populate(ConsignmentEntryModel consignmentEntryModel, ConsignmentEntryData consignmentEntryData)
      throws ConversionException {
    consignmentEntryData.setMiraklOrderLineId(consignmentEntryModel.getMiraklOrderLineId());
    consignmentEntryData.setConsignmentCode(consignmentEntryModel.getConsignment().getCode());
    MiraklOrderLineStatus miraklOrderLineStatus = consignmentEntryModel.getMiraklOrderLineStatus();
    if (miraklOrderLineStatus != null) {
      consignmentEntryData.setMiraklOrderLineStatus(miraklOrderLineStatus);
      consignmentEntryData.setMiraklOrderLineStatusLabel(enumerationService.getEnumerationName(miraklOrderLineStatus));
    }
    consignmentEntryData.setCanOpenIncident(consignmentEntryModel.getCanOpenIncident());
  }

  @Required
  public void setEnumerationService(EnumerationService enumerationService) {
    this.enumerationService = enumerationService;
  }
}
