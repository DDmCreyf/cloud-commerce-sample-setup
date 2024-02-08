package com.mirakl.hybris.facades.order.converters.populator;

import static com.google.common.collect.FluentIterable.from;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Ordering;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.order.services.ShippingFeeService;

import de.hybris.platform.commercefacades.order.converters.populator.DeliveryOrderEntryGroupPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.DeliveryOrderEntryGroupData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Delivery order entry groups populator splitting entries into delivery groups by {@link MiraklOrderShippingFees}
 */
public class MiraklDeliveryOrderEntryGroupPopulator extends DeliveryOrderEntryGroupPopulator {

  protected ShippingFeeService shippingFeeService;
  protected Populator<MiraklOrderShippingFee, DeliveryOrderEntryGroupData> shippingOptionPopulator;

  @Override
  public void populate(AbstractOrderModel source, AbstractOrderData target) throws ConversionException {
    List<DeliveryOrderEntryGroupData> deliveryGroups = new ArrayList<>();

    MiraklOrderShippingFees shippingFees = shippingFeeService.getStoredShippingFees(source);
    if (shippingFees == null) {
      deliveryGroups.add(createOrderEntryGroupWithoutShippingDetails(target, source.getEntries()));
    } else {
      List<AbstractOrderEntryModel> marketplaceEntries = source.getMarketplaceEntries();
      for (MiraklOrderShippingFee shippingFee : shippingFees.getOrders()) {
        deliveryGroups.add(createDeliveryOrderEntryGroup(shippingFee, marketplaceEntries, target));
      }
      addOperatorDeliveryGroup(target, deliveryGroups, source);
    }

    if (target.getDeliveryOrderGroups() == null) {
      target.setDeliveryOrderGroups(new ArrayList<DeliveryOrderEntryGroupData>());
    }
    target.getDeliveryOrderGroups().addAll(deliveryGroups);

    sortDeliveryGroupsByShop(target);
    target.setDeliveryItemsQuantity(sumDeliveryItemsQuantity(source));
  }

  protected DeliveryOrderEntryGroupData createDeliveryOrderEntryGroup(MiraklOrderShippingFee shippingFee,
      List<AbstractOrderEntryModel> marketplaceEntries, AbstractOrderData target) {
    DeliveryOrderEntryGroupData entryGroupData = new DeliveryOrderEntryGroupData();
    shippingOptionPopulator.populate(shippingFee, entryGroupData);

    Map<AbstractOrderEntryModel, Integer> availableQuantitiesForEntries = getAvailableQuantitiesForEntries(shippingFee, marketplaceEntries);
    entryGroupData.setEntries(getDeliveryEntriesDataWithAdjustedQuantity(availableQuantitiesForEntries, target));
    entryGroupData.setQuantity(sumOrderGroupQuantity(entryGroupData));

    return entryGroupData;
  }

  protected void addOperatorDeliveryGroup(AbstractOrderData abstractOrderData, List<DeliveryOrderEntryGroupData> deliveryGroups,
      AbstractOrderModel order) {
    List<AbstractOrderEntryModel> operatorEntries = order.getOperatorEntriesForDelivery();
    if (isNotEmpty(operatorEntries)) {
      deliveryGroups.add(createOrderEntryGroupWithoutShippingDetails(abstractOrderData, operatorEntries));
    }
  }

  private Collection<OrderEntryData> getDeliveryEntriesDataWithAdjustedQuantity(
      Map<AbstractOrderEntryModel, Integer> deliveryOrderGroupEntries, AbstractOrderData abstractOrderData) {
    List<OrderEntryData> orderEntryDataList = new ArrayList<>();
    for (Map.Entry<AbstractOrderEntryModel, Integer> deliveryOrderGroupEntry : deliveryOrderGroupEntries.entrySet()) {
      OrderEntryData orderEntryData = getOrderEntryData(abstractOrderData, deliveryOrderGroupEntry.getKey().getEntryNumber());
      orderEntryData.setQuantity(orderEntryData.getQuantity());
      orderEntryDataList.add(orderEntryData);
    }
    return orderEntryDataList;
  }

  protected Map<AbstractOrderEntryModel, Integer> getAvailableQuantitiesForEntries(MiraklOrderShippingFee miraklOrderShippingFee,
      List<AbstractOrderEntryModel> orderEntries) {
    Map<AbstractOrderEntryModel, Integer> offerEntries = new HashMap<>();

    for (final AbstractOrderEntryModel orderEntry : orderEntries) {
      Optional<MiraklOrderShippingFeeOffer> matchingShippingFee =
          from(miraklOrderShippingFee.getOffers()).firstMatch(matchingOrderEntryPredicate(orderEntry));
      if (matchingShippingFee.isPresent()) {
        offerEntries.put(orderEntry, matchingShippingFee.get().getQuantity());
      }
    }

    return offerEntries;
  }

  protected DeliveryOrderEntryGroupData createOrderEntryGroupWithoutShippingDetails(AbstractOrderData orderData,
      List<AbstractOrderEntryModel> entries) {
    DeliveryOrderEntryGroupData deliveryOrderEntryGroupData = new DeliveryOrderEntryGroupData();
    deliveryOrderEntryGroupData.setEntries(getOrderEntryDatas(orderData, entries));
    deliveryOrderEntryGroupData.setQuantity(sumOrderGroupQuantity(deliveryOrderEntryGroupData));
    return deliveryOrderEntryGroupData;
  }

  protected List<OrderEntryData> getOrderEntryDatas(AbstractOrderData orderData, List<AbstractOrderEntryModel> entries) {
    List<OrderEntryData> orderEntries = new ArrayList<>();

    for (AbstractOrderEntryModel entry : entries) {
      OrderEntryData orderEntryData = getOrderEntryData(orderData, entry.getEntryNumber());
      if (orderEntryData != null) {
        orderEntries.add(orderEntryData);
      }
    }
    return orderEntries;
  }

  protected void sortDeliveryGroupsByShop(AbstractOrderData target) {
    target.setDeliveryOrderGroups(Ordering.natural().nullsFirst().onResultOf(new Function<DeliveryOrderEntryGroupData, String>() {
      @Override
      public String apply(DeliveryOrderEntryGroupData group) {
        return group.getShopId();
      }
    }).immutableSortedCopy(target.getDeliveryOrderGroups()));
  }

  protected Predicate<MiraklOrderShippingFeeOffer> matchingOrderEntryPredicate(final AbstractOrderEntryModel orderEntry) {
    return new Predicate<MiraklOrderShippingFeeOffer>() {
      @Override
      public boolean apply(MiraklOrderShippingFeeOffer shippingFeeOffer) {
        return shippingFeeOffer.getId().equals(orderEntry.getOfferId());
      }
    };
  }

  @Required
  public void setShippingFeeService(ShippingFeeService shippingFeeService) {
    this.shippingFeeService = shippingFeeService;
  }

  @Required
  public void setShippingOptionPopulator(Populator<MiraklOrderShippingFee, DeliveryOrderEntryGroupData> shippingOptionPopulator) {
    this.shippingOptionPopulator = shippingOptionPopulator;
  }
}
