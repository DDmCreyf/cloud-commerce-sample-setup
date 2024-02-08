package com.mirakl.hybris.mtc.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.order.services.ShippingFeeService;
import com.mirakl.hybris.core.order.services.impl.DefaultMiraklCalculationService;
import com.mirakl.hybris.core.order.strategies.MarketplaceDeliveryCostStrategy;
import com.mirakl.hybris.core.promotions.strategies.MiraklPromotionsActivationStrategy;
import com.mirakl.hybris.mtc.services.MiraklTaxConnectorCalculationService;
import com.mirakl.hybris.mtc.strategies.MiraklTaxConnectorActivationStrategy;
import com.mirakl.hybris.mtc.strategies.MiraklTaxConnectorEmptyTaxesStrategy;
import com.mirakl.hybris.mtc.strategies.MiraklTaxConnectorFindTaxValuesStrategy;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.strategies.calculation.FindDeliveryCostStrategy;
import de.hybris.platform.order.strategies.calculation.FindTaxValuesStrategy;
import de.hybris.platform.order.strategies.calculation.OrderRequiresCalculationStrategy;
import de.hybris.platform.util.TaxValue;

public class DefaultMiraklTaxConnectorCalculationService extends DefaultMiraklCalculationService
    implements MiraklTaxConnectorCalculationService {
  private static final Logger LOG = Logger.getLogger(DefaultMiraklCalculationService.class);

  protected ShippingFeeService shippingFeeService;
  protected List<FindTaxValuesStrategy> findTaxesStrategies;
  protected OrderRequiresCalculationStrategy orderRequiresCalculationStrategy;
  protected MiraklTaxConnectorFindTaxValuesStrategy miraklTaxConnectorFindTaxValuesStrategy;
  protected FindDeliveryCostStrategy operatorFindDeliveryCostStrategy;
  protected MiraklTaxConnectorActivationStrategy miraklTaxConnectorActivationStrategy;
  protected MiraklPromotionsActivationStrategy miraklPromotionsActivationStrategy;
  protected MiraklTaxConnectorEmptyTaxesStrategy miraklTaxConnectorEmptyTaxesStrategy;
  protected MarketplaceDeliveryCostStrategy marketplaceDeliveryCostStrategy;

  @Override
  public Map<TaxValue, Map<Set<TaxValue>, Double>> calculateSubtotal(final AbstractOrderModel order, final boolean recalculate) {
    if (!miraklTaxConnectorActivationStrategy.isMiraklTaxConnectorComputation(order)) {
      return super.calculateSubtotal(order, recalculate);
    }

    if (recalculate || orderRequiresCalculationStrategy.requiresCalculation(order)) {
      double subtotal = 0D;
      final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap = new LinkedHashMap<>(order.getEntries().size() * 2);
      for (final AbstractOrderEntryModel entry : order.getOperatorEntries()) {
        calculateTotals(entry, recalculate);
        final double entryTotal = entry.getTotalPrice();
        subtotal += entryTotal;
        final Collection<TaxValue> allTaxValues = entry.getTaxValues();
        final Set<TaxValue> relativeTaxGroupKey = getUnappliedRelativeTaxValues(allTaxValues);
        for (final TaxValue taxValue : allTaxValues) {
          addEntryTaxValue(taxValueMap, entry, entryTotal, relativeTaxGroupKey, taxValue);
        }
      }
      for (final AbstractOrderEntryModel entry : order.getMarketplaceEntries()) {
        calculateTotals(entry, recalculate);
        final double entryTotal = entry.getTotalPrice();
        subtotal += entryTotal;
      }
      subtotal = commonI18nService.roundCurrency(subtotal, order.getCurrency().getDigits());
      order.setSubtotal(subtotal);
      return taxValueMap;
    }
    return Collections.emptyMap();

  }

  /**
   * This implementation overrides to allow the calculation to be performed with the Mirakl Tax Connector if enable at the Base
   * Store level:
   *
   * @see de.hybris.platform.order.impl.DefaultCalculationService#findTaxValues
   */
  @Override
  public Collection<TaxValue> findTaxValues(final AbstractOrderEntryModel entry) throws CalculationException {
    if (!miraklTaxConnectorActivationStrategy.isMiraklTaxConnectorComputation(entry.getOrder())) {
      return super.findTaxValues(entry);
    }
    if (entry.getOfferId() != null) {
      return miraklTaxConnectorFindTaxValuesStrategy.findTaxValues(entry);
    }
    if (findTaxesStrategies.isEmpty()) {
      LOG.warn("No strategies for finding tax values could be found!");
      return Collections.emptyList();
    }
    final List<TaxValue> result = new ArrayList<>();
    for (final FindTaxValuesStrategy findStrategy : findTaxesStrategies) {
      result.addAll(findStrategy.findTaxValues(entry));
    }
    return result;
  }

  /**
   * Calculates all totals. this does not trigger price, tax and discount calculation but takes all currently set price, tax and
   * discount values as base. this method requires the correct subtotal to be set before and the correct tax value map.
   *
   * @param recalculate if false calculation is done only if the calculated flag is not set
   * @param taxValueMap the map { tax value -> Double( sum of all entry totals for this tax ) } obtainable via
   *        {@link #calculateSubtotal(AbstractOrderModel, boolean)}
   * @throws CalculationException
   */
  @Override
  public void calculateTotals(final AbstractOrderModel order, final boolean recalculate,
      final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap) throws CalculationException {
    if (!miraklTaxConnectorActivationStrategy.isMiraklTaxConnectorComputation(order) || order.getMarketplaceEntries().isEmpty()) {
      super.calculateTotals(order, recalculate, taxValueMap);
      return;
    }

    if (recalculate || orderRequiresCalculationStrategy.requiresCalculation(order)) {
      MiraklOrderShippingFees miraklOrderShippingFees =
          shippingFeeService.getStoredShippingFeesWithCartCalculationFallback(order);
      if (miraklOrderShippingFees == null) {
        throw new CalculationException("Unable to retrieve taxes, order total calculation is not possible.");
      }
      final CurrencyModel curr = order.getCurrency();
      final int digits = curr.getDigits();
      // subtotal
      double operatorSubtotal = 0D;
      double marketplaceSubtotal = 0D;
      for (AbstractOrderEntryModel operatorEntry : order.getOperatorEntries()) {
        operatorSubtotal += operatorEntry.getTotalPrice();
      }
      for (AbstractOrderEntryModel marketplaceEntry : order.getMarketplaceEntries()) {
        marketplaceSubtotal += marketplaceEntry.getTotalPrice();
      }

      // discounts
      final double orderTotalDiscounts = calculateDiscountValues(order, recalculate);
      double orderRoundedTotalDiscounts = commonI18nService.roundCurrency(orderTotalDiscounts, digits);
      BigDecimal marketplaceDiscounts = BigDecimal.ZERO;
      if (miraklPromotionsActivationStrategy.isMiraklPromotionsEnabled() && !order.getGlobalDiscountValues().isEmpty()) {
        for (MiraklOrderShippingFee miraklOrderShippingFeesOrder : miraklOrderShippingFees.getOrders()) {
          marketplaceDiscounts = marketplaceDiscounts.add(miraklOrderShippingFeesOrder.getPromotions().getTotalDeducedAmount());
        }
      }
      order.setTotalDiscounts(orderRoundedTotalDiscounts);

      double operatorRoundedTotalDiscounts = orderRoundedTotalDiscounts - marketplaceDiscounts.doubleValue();

      // set total
      final double operatorTotal = operatorSubtotal + order.getPaymentCost()
          + operatorFindDeliveryCostStrategy.getDeliveryCost(order).getValue() - operatorRoundedTotalDiscounts;
      final double operatorTotalRounded = commonI18nService.roundCurrency(operatorTotal, digits);

      final double marketplaceTotal = marketplaceSubtotal + marketplaceDeliveryCostStrategy.getMarketplaceDeliveryCost(order)
          - marketplaceDiscounts.doubleValue();
      final double marketplaceTotalRounded = commonI18nService.roundCurrency(marketplaceTotal, digits);

      order.setTotalPrice(operatorTotalRounded + marketplaceTotalRounded);

      // taxes
      final double operatorTotalTaxes = calculateTotalTaxValues(//
          order, recalculate, //
          digits, //
          getTaxCorrectionFactor(taxValueMap, operatorSubtotal, operatorTotal, order), //
          taxValueMap);//
      final double operatorTotalRoundedTaxes = commonI18nService.roundCurrency(operatorTotalTaxes, digits);

      double marketplaceTotalTaxes = 0D;
      for (AbstractOrderEntryModel marketplaceEntry : order.getMarketplaceEntries()) {
        if (marketplaceEntry.getTaxValues().isEmpty()) {
          miraklTaxConnectorEmptyTaxesStrategy.setEmptyTaxValues(marketplaceEntry);
        }
        for (TaxValue taxValue : marketplaceEntry.getTaxValues()) {
          marketplaceTotalTaxes += taxValue.getAppliedValue();
        }
      }

      final double marketplaceTotalRoundedTaxes = commonI18nService.roundCurrency(marketplaceTotalTaxes, digits);

      order.setTotalTax(operatorTotalRoundedTaxes + marketplaceTotalRoundedTaxes);
      setCalculatedStatus(order);
      saveOrder(order);
    }
  }

  @Override
  public double calculateTotalTaxValues(final AbstractOrderModel order, final boolean recalculate, final int digits,
      final double taxAdjustmentFactor, final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap) {
    if (recalculate || orderRequiresCalculationStrategy.requiresCalculation(order)) {
      final CurrencyModel curr = order.getCurrency();
      final String iso = curr.getIsocode();
      final boolean net = order.getNet();
      double totalTaxes = 0D;
      if (MapUtils.isNotEmpty(taxValueMap)) {
        final Collection<TaxValue> orderTaxValues = new ArrayList<>(taxValueMap.size());
        for (final Map.Entry<TaxValue, Map<Set<TaxValue>, Double>> taxValueEntry : taxValueMap.entrySet()) {
          final TaxValue unappliedTaxValue = taxValueEntry.getKey();
          final Map<Set<TaxValue>, Double> taxGroups = taxValueEntry.getValue();
          final TaxValue appliedTaxValue;
          appliedTaxValue = applyTaxValue(digits, taxAdjustmentFactor, curr, iso, net, unappliedTaxValue, taxGroups);
          totalTaxes += appliedTaxValue.getAppliedValue();
          orderTaxValues.add(appliedTaxValue);
        }
        order.setTotalTaxValues(orderTaxValues);
      }
      return totalTaxes;
    }
    return order.getTotalTax();
  }

  @Override
  public void addEntryTaxValue(final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap, final AbstractOrderEntryModel entry,
      final double entryTotal, final Set<TaxValue> relativeTaxGroupKey, final TaxValue taxValue) {
    if (taxValue.isAbsolute()) {
      addAbsoluteEntryTaxValue(entry.getQuantity(), taxValue.unapply(), taxValueMap);
    } else {
      addRelativeEntryTaxValue(entryTotal, taxValue.unapply(), relativeTaxGroupKey, taxValueMap);
    }
  }

  @Override
  public TaxValue applyTaxValue(final int digits, final double taxAdjustmentFactor, final CurrencyModel curr, final String iso,
      final boolean net, final TaxValue unappliedTaxValue, final Map<Set<TaxValue>, Double> taxGroups) {
    final TaxValue appliedTaxValue;
    if (unappliedTaxValue.isAbsolute()) {
      final double quantitySum = taxGroups.entrySet().iterator().next().getValue();
      appliedTaxValue = calculateAbsoluteTotalTaxValue(curr, iso, digits, net, unappliedTaxValue, quantitySum);
    } else if (net) {
      appliedTaxValue = applyNetMixedRate(unappliedTaxValue, taxGroups, digits, taxAdjustmentFactor);
    } else {
      appliedTaxValue = applyGrossMixedRate(unappliedTaxValue, taxGroups, digits, taxAdjustmentFactor);
    }
    return appliedTaxValue;
  }

  @Required
  public void setShippingFeeService(ShippingFeeService shippingFeeService) {
    this.shippingFeeService = shippingFeeService;
  }

  @Required
  @Override
  public void setFindTaxesStrategies(List<FindTaxValuesStrategy> findTaxesStrategies) {
    super.setFindTaxesStrategies(findTaxesStrategies);
    this.findTaxesStrategies = findTaxesStrategies;
  }

  @Required
  public void setMiraklTaxConnectorFindTaxValuesStrategy(
      MiraklTaxConnectorFindTaxValuesStrategy miraklTaxConnectorFindTaxValuesStrategy) {
    this.miraklTaxConnectorFindTaxValuesStrategy = miraklTaxConnectorFindTaxValuesStrategy;
  }

  @Required
  @Override
  public void setOrderRequiresCalculationStrategy(OrderRequiresCalculationStrategy orderRequiresCalculationStrategy) {
    super.setOrderRequiresCalculationStrategy(orderRequiresCalculationStrategy);
    this.orderRequiresCalculationStrategy = orderRequiresCalculationStrategy;
  }

  @Required
  public void setOperatorFindDeliveryCostStrategy(FindDeliveryCostStrategy operatorFindDeliveryCostStrategy) {
    this.operatorFindDeliveryCostStrategy = operatorFindDeliveryCostStrategy;
  }

  @Required
  public void setMiraklTaxConnectorActivationStrategy(MiraklTaxConnectorActivationStrategy miraklTaxConnectorActivationStrategy) {
    this.miraklTaxConnectorActivationStrategy = miraklTaxConnectorActivationStrategy;
  }

  @Required
  public void setMiraklPromotionsActivationStrategy(MiraklPromotionsActivationStrategy miraklPromotionsActivationStrategy) {
    this.miraklPromotionsActivationStrategy = miraklPromotionsActivationStrategy;
  }

  @Required
  public void setMiraklTaxConnectorEmptyTaxesStrategy(MiraklTaxConnectorEmptyTaxesStrategy miraklTaxConnectorEmptyTaxesStrategy) {
    this.miraklTaxConnectorEmptyTaxesStrategy = miraklTaxConnectorEmptyTaxesStrategy;
  }

  @Required
  public void setMarketplaceDeliveryCostStrategy(MarketplaceDeliveryCostStrategy marketplaceDeliveryCostStrategy) {
    this.marketplaceDeliveryCostStrategy = marketplaceDeliveryCostStrategy;
  }
}
