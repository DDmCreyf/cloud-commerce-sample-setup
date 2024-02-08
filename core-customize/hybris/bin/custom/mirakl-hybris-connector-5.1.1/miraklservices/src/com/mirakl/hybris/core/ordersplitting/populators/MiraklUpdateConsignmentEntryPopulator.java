package com.mirakl.hybris.core.ordersplitting.populators;

import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.FluentIterable.from;
import static java.util.Collections.emptyMap;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Predicate;
import com.mirakl.client.mmp.domain.order.MiraklOrderLine;
import com.mirakl.client.mmp.domain.order.MiraklRefund;
import com.mirakl.client.mmp.domain.order.state.AbstractMiraklOrderStatus.State;
import com.mirakl.hybris.core.enums.MiraklOrderLineStatus;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklUpdateConsignmentEntryPopulator implements Populator<MiraklOrderLine, ConsignmentEntryModel> {

  protected Set<State> shippingStates;
  protected Set<State> cancellationStates;

  protected Populator<MiraklRefund, RefundEntryModel> refundEntryPopulator;

  @Override
  public void populate(MiraklOrderLine source, ConsignmentEntryModel target) throws ConversionException {
    if (source.getStatus() != null && source.getStatus().getState() != null) {
      target.setMiraklOrderLineStatus(MiraklOrderLineStatus.valueOf(source.getStatus().getState().name()));
    }
    if (source.getId() != null) {
      target.setMiraklOrderLineId(source.getId());
    }

    target.setCanOpenIncident(source.getCanOpenIncident());
    target.setMiraklOrderLineId(source.getId());

    populateRefunds(source, target);
    populateShippedQuantity(source, target);
  }

  protected void populateRefunds(MiraklOrderLine miraklOrderLine, ConsignmentEntryModel consignmentEntry) {
    if (isEmpty(miraklOrderLine.getRefunds())) {
      return;
    }

    OrderModel order = (OrderModel) consignmentEntry.getOrderEntry().getOrder();
    Map<String, RefundEntryModel> refundsById = groupRefundEntriesByRefundId(order);
    for (MiraklRefund miraklRefund : miraklOrderLine.getRefunds()) {
      RefundEntryModel refundEntry = refundsById.get(miraklRefund.getId());
      if (refundEntry != null) {
        refundEntryPopulator.populate(miraklRefund, refundEntry);
      }
    }
  }

  protected void populateShippedQuantity(MiraklOrderLine miraklOrderLine, ConsignmentEntryModel consignmentEntry) {
    if (miraklOrderLine.getStatus() == null) {
      return;
    }
    State orderLineState = miraklOrderLine.getStatus().getState();
    if (shippingStates.contains(orderLineState)) {
      consignmentEntry.setShippedQuantity((long) miraklOrderLine.getQuantity());
    } else if (cancellationStates.contains(orderLineState)) {
      consignmentEntry.setShippedQuantity(0L);
    }
  }

  protected Map<String, RefundEntryModel> groupRefundEntriesByRefundId(OrderModel order) {
    if (isEmpty(order.getReturnRequests())) {
      return emptyMap();
    }

    Map<String, RefundEntryModel> refundsById = new HashMap<>();
    Set<ReturnEntryModel> miraklRefundEntries = getMiraklRefundEntries(order.getReturnRequests());
    for (ReturnEntryModel returnEntry : miraklRefundEntries) {
      RefundEntryModel refund = (RefundEntryModel) returnEntry;
      refundsById.put(refund.getMiraklRefundId(), refund);
    }

    return refundsById;
  }

  protected Set<ReturnEntryModel> getMiraklRefundEntries(List<ReturnRequestModel> returnRequests) {
    Set<ReturnEntryModel> refundEntries = new HashSet<>();
    for (ReturnRequestModel returnRequest : returnRequests) {
      refundEntries.addAll(from(returnRequest.getReturnEntries())//
          .filter(instanceOf(RefundEntryModel.class))//
          .filter(miraklRefundEntryPredicate())//
          .toSet());
    }

    return refundEntries;
  }


  protected Predicate<ReturnEntryModel> miraklRefundEntryPredicate() {
    return new Predicate<ReturnEntryModel>() {

      @Override
      public boolean apply(ReturnEntryModel returnEntry) {
        return ((RefundEntryModel) returnEntry).getMiraklRefundId() != null;
      }
    };
  }

  @Required
  public void setRefundEntryPopulator(Populator<MiraklRefund, RefundEntryModel> refundEntryPopulator) {
    this.refundEntryPopulator = refundEntryPopulator;
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
