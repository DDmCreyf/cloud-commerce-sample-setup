package com.mirakl.hybris.mtc.strategies.impl;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.order.services.ShippingFeeService;
import com.mirakl.hybris.mtc.beans.MiraklTaxEstimation;
import com.mirakl.hybris.mtc.strategies.MiraklTaxConnectorFindTaxValuesStrategy;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.TaxValue;

public class DefaultMiraklTaxConnectorFindTaxValuesStrategy implements MiraklTaxConnectorFindTaxValuesStrategy {

  protected ShippingFeeService shippingFeeService;
  protected Converter<MiraklTaxEstimation, List<TaxValue>> miraklTaxConnectorAbsoluteTaxValueConverter;

  @Override
  public Collection<TaxValue> findTaxValues(AbstractOrderEntryModel entry) throws CalculationException {
    if (entry == null || entry.getOrder() == null) {
      return emptyList();
    }
    AbstractOrderModel order = entry.getOrder();
    MiraklOrderShippingFees shippingFees = shippingFeeService.getStoredShippingFeesWithCartCalculationFallback(order);

    return shippingFees == null ? emptyList() : extractMarketplaceTaxesForOrderEntry(entry, shippingFees);
  }

  public List<TaxValue> extractMarketplaceTaxesForOrderEntry(AbstractOrderEntryModel entry, MiraklOrderShippingFees shippingFees)
      throws CalculationException {
    AbstractOrderModel order = entry.getOrder();
    if (isEmpty(order.getMarketplaceEntries())) {
      return emptyList();
    }

    List<TaxValue> allTaxValues = new ArrayList<>();
    for (MiraklOrderShippingFee orderShippingFee : shippingFees.getOrders()) {
      if (orderShippingFee.getOffers().isEmpty()) {
        continue;
      }
      allTaxValues.addAll(extractTaxesForOffers(entry, orderShippingFee));
    }

    return allTaxValues;
  }

  protected List<TaxValue> extractTaxesForOffers(AbstractOrderEntryModel marketplaceEntry, MiraklOrderShippingFee shippingFee)
      throws CalculationException {
    if (CollectionUtils.isEmpty(shippingFee.getOffers())) {
      return Collections.emptyList();
    }
    List<TaxValue> taxValues = new ArrayList<>();
    for (MiraklOrderShippingFeeOffer offer : shippingFee.getOffers()) {
      if (offer.getId().equals(marketplaceEntry.getOfferId())) {
        taxValues.addAll(extractTaxesForOffer(marketplaceEntry, shippingFee, offer));
      }
    }
    return taxValues;
  }

  protected List<TaxValue> extractTaxesForOffer(AbstractOrderEntryModel marketplaceEntry,
      MiraklOrderShippingFee shippingFee, MiraklOrderShippingFeeOffer offer) throws CalculationException {
    if (marketplaceEntry.getOfferId() == null || offer.getId() == null) {
      return Collections.emptyList();
    }
    if (!offer.getId().equals(marketplaceEntry.getOfferId())) {
      throw new CalculationException(
          format("Unable to extract taxes the offer with code [%s] doesn't match the marketplace entry with offer code [%s]",
              offer.getId(), marketplaceEntry.getOfferId()));
    }
    Long quantity = marketplaceEntry.getQuantity();
    if (quantity == null || quantity == 0) {
      throw new CalculationException(format("Unable to calculate taxes for offer with code [%s]", offer.getId()));
    }
    List<TaxValue> taxValues = new ArrayList<>();
    miraklTaxConnectorAbsoluteTaxValueConverter.convert(new MiraklTaxEstimation(offer.getTaxes(), quantity, shippingFee.getCurrencyIsoCode().name()), taxValues);
    miraklTaxConnectorAbsoluteTaxValueConverter.convert(new MiraklTaxEstimation(offer.getShippingTaxes(), quantity, shippingFee.getCurrencyIsoCode().name()), taxValues);
    return taxValues;
  }


  @Required
  public void setShippingFeeService(ShippingFeeService shippingFeeService) {
    this.shippingFeeService = shippingFeeService;
  }

  @Required
  public void setMiraklTaxConnectorAbsoluteTaxValueConverter(Converter<MiraklTaxEstimation, List<TaxValue>> miraklTaxConnectorAbsoluteTaxValueConverter) {
    this.miraklTaxConnectorAbsoluteTaxValueConverter = miraklTaxConnectorAbsoluteTaxValueConverter;
  }
}
