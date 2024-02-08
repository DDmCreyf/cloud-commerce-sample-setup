package com.mirakl.hybris.mtc.strategies;

import java.util.Collection;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.util.TaxValue;

/**
 * Strategy focused on finding {@link TaxValue}s for the given order entry.
 */
public interface MiraklTaxConnectorFindTaxValuesStrategy {
  /**
   * Resolves tax value for the given {@link AbstractOrderEntryModel} basing on the underlying implementation.
   *
   * @param entry {@link AbstractOrderEntryModel}
   * @return collection of {@link TaxValue}s
   */
  Collection<TaxValue> findTaxValues(AbstractOrderEntryModel entry) throws CalculationException;

}
