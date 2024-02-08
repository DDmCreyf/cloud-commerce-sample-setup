package com.mirakl.hybris.core.order.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.order.MiraklOrder;
import com.mirakl.client.mmp.front.domain.order.create.MiraklCreatedOrders;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class MiraklOrderModelPopulator implements Populator<MiraklCreatedOrders, AbstractOrderModel> {

  protected JsonMarshallingService jsonMarshallingService;
  protected Converter<MiraklOrder, MarketplaceConsignmentModel> consignmentModelConverter;

  @Override
  public void populate(MiraklCreatedOrders miraklOrders, AbstractOrderModel order) throws ConversionException {
    validateParameterNotNullStandardMessage("miraklOrders", miraklOrders);
    validateParameterNotNullStandardMessage("order", order);

    order.setCreatedOrdersJSON(jsonMarshallingService.toJson(miraklOrders));
    populateConsignments(miraklOrders.getOrders(), order);
  }

  protected void populateConsignments(List<MiraklOrder> orders, AbstractOrderModel order) {
    for (MiraklOrder miraklOrder : orders) {
      for (MarketplaceConsignmentModel consignment : order.getMarketplaceConsignments()) {
        if (consignment.getCode().equals(miraklOrder.getId())) {
          consignmentModelConverter.convert(miraklOrder, consignment);
        }
      }
    }
  }

  @Required
  public void setJsonMarshallingService(JsonMarshallingService jsonMarshallingService) {
    this.jsonMarshallingService = jsonMarshallingService;
  }

  @Required
  public void setConsignmentModelConverter(Converter<MiraklOrder, MarketplaceConsignmentModel> consignmentModelConverter) {
    this.consignmentModelConverter = consignmentModelConverter;
  }


}
