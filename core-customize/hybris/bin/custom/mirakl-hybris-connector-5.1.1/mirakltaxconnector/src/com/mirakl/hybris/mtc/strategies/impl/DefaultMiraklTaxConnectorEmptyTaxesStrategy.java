package com.mirakl.hybris.mtc.strategies.impl;

import java.util.Collections;
import java.util.List;

import com.mirakl.hybris.beans.MiraklTaxValuesData;
import com.mirakl.hybris.mtc.constants.MirakltaxconnectorConstants;
import com.mirakl.hybris.mtc.strategies.MiraklTaxConnectorEmptyTaxesStrategy;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.util.TaxValue;

public class DefaultMiraklTaxConnectorEmptyTaxesStrategy implements MiraklTaxConnectorEmptyTaxesStrategy {

  private static final List<TaxValue> ZERO_TAX_VALUES = Collections.singletonList(MirakltaxconnectorConstants.MTC_NO_TAXES);

  @Override
  public void setEmptyTaxValues(AbstractOrderEntryModel orderEntry) {
    orderEntry.setTaxValues(ZERO_TAX_VALUES);
  }

  @Override
  public void resetEmptyTaxValues(MiraklTaxValuesData taxValuesData) {
    taxValuesData.getTaxValues().remove(MirakltaxconnectorConstants.MTC_NO_TAXES);
    taxValuesData.getShippingTaxValues().remove(MirakltaxconnectorConstants.MTC_NO_TAXES);
  }
}
