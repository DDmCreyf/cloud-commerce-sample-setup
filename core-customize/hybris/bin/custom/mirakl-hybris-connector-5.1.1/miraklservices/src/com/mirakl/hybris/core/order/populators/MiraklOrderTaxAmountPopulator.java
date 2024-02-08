package com.mirakl.hybris.core.order.populators;

import static com.mirakl.client.core.internal.util.Preconditions.checkNotNull;

import java.math.BigDecimal;

import com.mirakl.client.mmp.domain.order.tax.MiraklOrderTaxAmount;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.util.TaxValue;

public class MiraklOrderTaxAmountPopulator implements Populator<TaxValue, MiraklOrderTaxAmount> {

  @Override
  public void populate(TaxValue taxValue, MiraklOrderTaxAmount miraklOrderTaxAmount) throws ConversionException {
    checkNotNull(taxValue);
    checkNotNull(miraklOrderTaxAmount);
    miraklOrderTaxAmount.setCode(taxValue.getCode());
    miraklOrderTaxAmount.setAmount(BigDecimal.valueOf(taxValue.getAppliedValue()));
  }
}
