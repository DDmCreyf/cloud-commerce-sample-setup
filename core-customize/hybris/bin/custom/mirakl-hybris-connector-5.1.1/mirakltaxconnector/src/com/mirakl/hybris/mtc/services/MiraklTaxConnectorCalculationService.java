package com.mirakl.hybris.mtc.services;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.util.TaxValue;

public interface MiraklTaxConnectorCalculationService {

  /**
   * This implementation overrides to allow the calculation to be performed with the Mirakl Tax Connector if enable at the Base
   * Store level:
   *
   * @see de.hybris.platform.order.impl.DefaultCalculationService#calculateSubtotal
   */
  Map<TaxValue, Map<Set<TaxValue>, Double>> calculateSubtotal(final AbstractOrderModel order, final boolean recalculate);

  /**
   * This implementation overrides to allow the calculation to be performed with the Mirakl Tax Connector if enable at the Base
   * Store level:
   *
   * @see de.hybris.platform.order.impl.DefaultCalculationService#findTaxValues(AbstractOrderEntryModel)
   */
  Collection<TaxValue> findTaxValues(final AbstractOrderEntryModel entry) throws CalculationException;

  /**
   * This implementation overrides to allow the calculation to be performed with the Mirakl Tax Connector if enable at the Base
   * Store level:
   *
   * @see de.hybris.platform.order.impl.DefaultCalculationService#calculateTotals
   */
  void calculateTotals(final AbstractOrderModel order, final boolean recalculate,
      final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap) throws CalculationException;

  /**
   * This implementation is a cleaned copy of:
   *
   * @see de.hybris.platform.order.impl.DefaultCalculationService#calculateTotalTaxValues
   */
  double calculateTotalTaxValues(final AbstractOrderModel order, final boolean recalculate, final int digits,
      final double taxAdjustmentFactor, final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap);

  /**
   * This implementation is a copy of:
   *
   * @see de.hybris.platform.order.impl.DefaultCalculationService#addEntryTaxValue
   */
  void addEntryTaxValue(final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap, final AbstractOrderEntryModel entry,
      final double entryTotal, final Set<TaxValue> relativeTaxGroupKey, final TaxValue taxValue);

  /**
   * This implementation is a copy of:
   *
   * @see de.hybris.platform.order.impl.DefaultCalculationService#applyTaxValue
   */
  TaxValue applyTaxValue(final int digits, final double taxAdjustmentFactor, final CurrencyModel curr, final String iso,
      final boolean net, final TaxValue unappliedTaxValue, final Map<Set<TaxValue>, Double> taxGroups);

}
