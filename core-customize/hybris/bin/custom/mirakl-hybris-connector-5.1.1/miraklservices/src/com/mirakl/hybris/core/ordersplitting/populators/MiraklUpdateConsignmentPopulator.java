package com.mirakl.hybris.core.ordersplitting.populators;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.order.MiraklOrder;
import com.mirakl.client.mmp.domain.order.MiraklOrderLine;
import com.mirakl.client.mmp.domain.order.MiraklOrderShipping;
import com.mirakl.client.mmp.domain.order.state.AbstractMiraklOrderStatus.State;
import com.mirakl.hybris.core.enums.MiraklOrderStatus;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklUpdateConsignmentPopulator implements Populator<MiraklOrder, MarketplaceConsignmentModel> {

  protected Populator<MiraklOrderLine, ConsignmentEntryModel> consignmentEntryPopulator;
  protected Set<State> shippingStates;
  protected Set<State> cancellationStates;

  @Override
  public void populate(MiraklOrder source, MarketplaceConsignmentModel target) throws ConversionException {
    target.setCanCancel(source.getCanCancel());
    target.setCanEvaluate(source.getCanEvaluate());
    target.setImprintNumber(source.getImprintNumber());
    target.setLastUpdatedDate(source.getLastUpdatedDate());
    target.setTotalPrice(source.getTotalPrice().doubleValue());
    target.setCustomerDebitDate(source.getCustomerDebitedDate());

    populateConsignmentEntries(source, target);
    populateShipping(source, target);
    populateStatus(source, target);
  }


  protected void populateShipping(MiraklOrder source, MarketplaceConsignmentModel target) {
    MiraklOrderShipping shipping = source.getShipping();
    if (shipping != null) {
      target.setTrackingID(shipping.getTrackingUrl());
      target.setShippingCost(shipping.getPrice().doubleValue());
    }
  }

  protected void populateConsignmentEntries(MiraklOrder source, MarketplaceConsignmentModel target) {
    Map<String, ConsignmentEntryModel> consignmentEntriesByOfferId = groupConsignmentEntriesByOfferId(target);
    for (MiraklOrderLine miraklOrderLine : source.getOrderLines()) {
      ConsignmentEntryModel consignmentEntry = consignmentEntriesByOfferId.get(miraklOrderLine.getOffer().getId());
      if (consignmentEntry != null) {
        consignmentEntryPopulator.populate(miraklOrderLine, consignmentEntry);
      }
    }
  }

  protected void populateStatus(MiraklOrder source, MarketplaceConsignmentModel target) {
    if (source.getStatus() != null && source.getStatus().getState() != null) {
      target.setMiraklOrderStatus(MiraklOrderStatus.valueOf(source.getStatus().getState().name()));
      if (shippingStates.contains(source.getStatus().getState())) {
        target.setStatus(ConsignmentStatus.SHIPPED);
      } else if (cancellationStates.contains(source.getStatus().getState())) {
        target.setStatus(ConsignmentStatus.CANCELLED);
      }
    }   
  }

  protected Map<String, ConsignmentEntryModel> groupConsignmentEntriesByOfferId(MarketplaceConsignmentModel consignment) {
    Map<String, ConsignmentEntryModel> consignmentEntriesByOfferId = new HashMap<>();
    if (isNotEmpty(consignment.getConsignmentEntries())) {
      for (ConsignmentEntryModel consignmentEntry : consignment.getConsignmentEntries()) {
        consignmentEntriesByOfferId.put(consignmentEntry.getOrderEntry().getOfferId(), consignmentEntry);
      }
    }

    return consignmentEntriesByOfferId;
  }

  @Required
  public void setConsignmentEntryPopulator(Populator<MiraklOrderLine, ConsignmentEntryModel> consignmentEntryPopulator) {
    this.consignmentEntryPopulator = consignmentEntryPopulator;
  }

  @Required
  public void setShippingStates(Set<State> shippingStates) {
    this.shippingStates = shippingStates;
  }

  @Required
  public void setCancellationStates(Set<State> cancellationStates) {
    this.cancellationStates = cancellationStates;
  }

}
