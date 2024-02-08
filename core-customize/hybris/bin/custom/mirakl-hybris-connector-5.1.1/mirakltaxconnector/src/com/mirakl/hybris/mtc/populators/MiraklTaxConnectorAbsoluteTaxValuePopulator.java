package com.mirakl.hybris.mtc.populators;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.List;

import com.mirakl.client.mmp.front.domain.order.create.MiraklOrderTaxEstimation;
import com.mirakl.hybris.mtc.beans.MiraklTaxEstimation;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.util.TaxValue;

public class MiraklTaxConnectorAbsoluteTaxValuePopulator implements Populator<MiraklTaxEstimation, List<TaxValue>> {

  @Override
  public void populate(MiraklTaxEstimation miraklTaxEstimations, List<TaxValue> taxValues) throws ConversionException {
    Long quantity = miraklTaxEstimations.getQuantity();
    String currencyIsocode = miraklTaxEstimations.getCurrencyIsocode();
    if (currencyIsocode == null || quantity == null || quantity == 0L || isEmpty(miraklTaxEstimations.getTaxEstimations())) {
      return;
    }
    for (MiraklOrderTaxEstimation shippingTax : miraklTaxEstimations.getTaxEstimations()) {
      taxValues.add(new TaxValue(shippingTax.getType(), shippingTax.getAmount().doubleValue() / quantity, true,
          shippingTax.getAmount().doubleValue() / quantity, currencyIsocode));
    }
  }
}
