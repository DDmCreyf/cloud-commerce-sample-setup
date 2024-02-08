package com.mirakl.hybris.mtc.populators;

import static com.mirakl.client.core.internal.util.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.mirakl.client.mmp.domain.order.tax.MiraklOrderTaxAmount;
import com.mirakl.hybris.beans.MiraklTaxValuesData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.util.TaxValue;

public class MiraklOrderTaxConnectorTaxAmountPopulator implements Populator<MiraklTaxValuesData, Pair<List<MiraklOrderTaxAmount>, List<MiraklOrderTaxAmount>>> {

  @Override
  public void populate(MiraklTaxValuesData taxValuesData, Pair<List<MiraklOrderTaxAmount>, List<MiraklOrderTaxAmount>> miraklOrderTaxAmounts) throws ConversionException {
    List<TaxValue> taxes = taxValuesData.getTaxValues();
    List<TaxValue> shippingTaxes = taxValuesData.getShippingTaxValues();
    Long quantity = taxValuesData.getQuantity();

    checkNotNull(taxes);
    checkNotNull(shippingTaxes);
    checkNotNull(quantity);

    List<MiraklOrderTaxAmount> miraklTaxes = miraklOrderTaxAmounts.getLeft();
    for (TaxValue tax : taxes) {
      miraklTaxes.add(new MiraklOrderTaxAmount(BigDecimal.valueOf(tax.getAppliedValue() * quantity), tax.getCode()));
    }
    List<MiraklOrderTaxAmount> miraklShippingTaxes = miraklOrderTaxAmounts.getRight();
    for (TaxValue tax : shippingTaxes) {
      miraklShippingTaxes.add(new MiraklOrderTaxAmount(BigDecimal.valueOf(tax.getAppliedValue() * quantity), tax.getCode()));
    }
  }
}
