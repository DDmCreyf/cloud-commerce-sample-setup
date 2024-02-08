package com.mirakl.hybris.core.order.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.order.MiraklOrder;
import com.mirakl.client.mmp.domain.order.MiraklOrderLine;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class MiraklMarketplaceConsignmentModelPopulator implements Populator<MiraklOrder, MarketplaceConsignmentModel> {

  protected MarketplaceConsignmentService marketplaceConsignmentService;
  protected Converter<MiraklOrderLine, ConsignmentEntryModel> consignmentEntriesConverter;

  @Override
  public void populate(MiraklOrder miraklOrder, MarketplaceConsignmentModel consignment) throws ConversionException {
    validateParameterNotNullStandardMessage("miraklOrder", miraklOrder);
    validateParameterNotNullStandardMessage("consignment", consignment);

    marketplaceConsignmentService.storeMarketplaceConsignmentCustomFields(miraklOrder.getOrderAdditionalFields(), consignment);
    populateConsignmentEntries(consignment, miraklOrder.getOrderLines());
  }


  protected void populateConsignmentEntries(MarketplaceConsignmentModel consignment, List<MiraklOrderLine> orderLines) {
    for (ConsignmentEntryModel consignmentEntry : consignment.getConsignmentEntries()) {
      for (MiraklOrderLine orderLine : orderLines) {
        if (consignmentEntry.getMiraklOrderLineId().equals(orderLine.getId())) {
          consignmentEntriesConverter.convert(orderLine, consignmentEntry);
        }
      }
    }
  }

  @Required
  public void setMarketplaceConsignmentService(MarketplaceConsignmentService marketplaceConsignmentService) {
    this.marketplaceConsignmentService = marketplaceConsignmentService;
  }

  @Required
  public void setConsignmentEntriesConverter(Converter<MiraklOrderLine, ConsignmentEntryModel> consignmentEntriesConverter) {
    this.consignmentEntriesConverter = consignmentEntriesConverter;
  }


}
