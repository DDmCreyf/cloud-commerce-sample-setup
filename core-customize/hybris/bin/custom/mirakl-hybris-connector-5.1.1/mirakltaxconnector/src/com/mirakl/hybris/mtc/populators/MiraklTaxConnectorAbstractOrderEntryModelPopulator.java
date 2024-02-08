package com.mirakl.hybris.mtc.populators;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.beans.MiraklTaxValuesData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.order.services.ShippingFeeService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.TaxValue;

public class MiraklTaxConnectorAbstractOrderEntryModelPopulator implements Populator<OfferModel, AbstractOrderEntryModel> {

  protected ShippingFeeService shippingFeeService;
  protected Converter<Pair<MiraklOrderShippingFee, AbstractOrderEntryModel>, MiraklTaxValuesData> taxValuesDataConverter;

  @Override
  public void populate(OfferModel offerModel, AbstractOrderEntryModel orderEntry) throws ConversionException {
    MiraklOrderShippingFees shippingFees =
        shippingFeeService.getStoredShippingFeesWithCartCalculationFallback(orderEntry.getOrder());
    if (shippingFees == null || isBlank(orderEntry.getOfferId())) {
      return;
    }

    List<TaxValue> taxValues = new ArrayList<>();
    if (isNotEmpty(shippingFees.getOrders())) {
      for (MiraklOrderShippingFee order : shippingFees.getOrders()) {
        MiraklTaxValuesData taxValuesData = taxValuesDataConverter.convert(Pair.of(order, orderEntry));
        taxValues.addAll(taxValuesData.getTaxValues());
      }
      orderEntry.setTaxValues(taxValues);
    }
  }

  @Required
  public void setShippingFeeService(ShippingFeeService shippingFeeService) {
    this.shippingFeeService = shippingFeeService;
  }

  @Required
  public void setMiraklTaxConnectorTaxValuesDataConverter(Converter<Pair<MiraklOrderShippingFee,
      AbstractOrderEntryModel>, MiraklTaxValuesData> miraklTaxConnectorTaxValuesDataConverter) {
    this.taxValuesDataConverter = miraklTaxConnectorTaxValuesDataConverter;
  }

}
