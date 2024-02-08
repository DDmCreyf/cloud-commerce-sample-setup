package com.mirakl.hybris.mtc.strategies;

import com.mirakl.hybris.beans.MiraklTaxValuesData;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

public interface MiraklTaxConnectorEmptyTaxesStrategy {

  void setEmptyTaxValues(AbstractOrderEntryModel orderEntry);

  void resetEmptyTaxValues(MiraklTaxValuesData taxValuesData);
}
