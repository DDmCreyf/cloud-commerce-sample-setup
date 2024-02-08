package com.mirakl.hybris.core.ordersplitting.populators;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.order.MiraklOrder;
import com.mirakl.client.mmp.domain.order.MiraklOrderLine;
import com.mirakl.client.mmp.domain.order.MiraklOrderShipping;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class MiraklCreateConsignmentPopulator implements Populator<Pair<OrderModel, MiraklOrder>, MarketplaceConsignmentModel> {

  protected Converter<Pair<AbstractOrderEntryModel, MiraklOrderLine>, ConsignmentEntryModel> consignmentEntryConverter;

  protected String defaultMarketplaceWarehouseCode;

  protected WarehouseService warehouseService;

  @Override
  public void populate(Pair<OrderModel, MiraklOrder> source, MarketplaceConsignmentModel target) throws ConversionException {
    MiraklOrder miraklOrder = source.getRight();
    OrderModel order = source.getLeft();
    target.setOrder(order);
    target.setCode(miraklOrder.getId());
    target.setShippingAddress(order.getDeliveryAddress());
    target.setWarehouse(warehouseService.getWarehouseForCode(defaultMarketplaceWarehouseCode));
    target.setCanCancel(miraklOrder.getCanCancel());
    target.setCanEvaluate(miraklOrder.getCanEvaluate());
    target.setLeadTimeToShip(miraklOrder.getLeadtimeToShip());
    target.setShopId(miraklOrder.getShopId());
    target.setShopName(miraklOrder.getShopName());
    target.setTotalPrice(miraklOrder.getTotalPrice().doubleValue());

    populateConsignmentEntries(source, target);
    populateShipping(source, target);
    populateStatus(source, target);
  }

  protected void populateConsignmentEntries(Pair<OrderModel, MiraklOrder> source, MarketplaceConsignmentModel target) {
    Map<String, AbstractOrderEntryModel> orderEntriesByOfferId = groupOrderEntriesByOfferId(source.getKey());
    target.setConsignmentEntries(new HashSet<ConsignmentEntryModel>());

    for (MiraklOrderLine miraklOrderLine : source.getRight().getOrderLines()) {
      AbstractOrderEntryModel orderEntryModel = orderEntriesByOfferId.get(miraklOrderLine.getOffer().getId());
      addEntryToConsignment(consignmentEntryConverter.convert(Pair.of(orderEntryModel, miraklOrderLine)), target);
    }
  }

  protected void populateShipping(Pair<OrderModel, MiraklOrder> source, MarketplaceConsignmentModel target) {
    MiraklOrderShipping shipping = source.getRight().getShipping();
    if (shipping != null) {
      target.setShippingCost(shipping.getPrice().doubleValue());
      if (shipping.getType() != null) {
        target.setShippingTypeCode(shipping.getType().getCode());
        target.setShippingTypeLabel(shipping.getType().getLabel());
      }
    }
  }

  protected void populateStatus(Pair<OrderModel, MiraklOrder> source, MarketplaceConsignmentModel target) {
    target.setStatus(ConsignmentStatus.WAITING);
  }

  protected void addEntryToConsignment(ConsignmentEntryModel consignmentEntry, ConsignmentModel consignment) {
    consignmentEntry.setConsignment(consignment);
    consignment.getConsignmentEntries().add(consignmentEntry);
  }

  protected Map<String, AbstractOrderEntryModel> groupOrderEntriesByOfferId(OrderModel orderModel) {
    Map<String, AbstractOrderEntryModel> orderEntriesByOfferId = new HashMap<>();
    for (AbstractOrderEntryModel orderEntryModel : orderModel.getEntries()) {
      orderEntriesByOfferId.put(orderEntryModel.getOfferId(), orderEntryModel);
    }

    return orderEntriesByOfferId;
  }

  @Required
  public void setConsignmentEntryConverter(
      Converter<Pair<AbstractOrderEntryModel, MiraklOrderLine>, ConsignmentEntryModel> consignmentEntryConverter) {
    this.consignmentEntryConverter = consignmentEntryConverter;
  }

  @Required
  public void setDefaultMarketplaceWarehouseCode(String defaultMarketplaceWarehouseCode) {
    this.defaultMarketplaceWarehouseCode = defaultMarketplaceWarehouseCode;
  }

  @Required
  public void setWarehouseService(WarehouseService warehouseService) {
    this.warehouseService = warehouseService;
  }


}
