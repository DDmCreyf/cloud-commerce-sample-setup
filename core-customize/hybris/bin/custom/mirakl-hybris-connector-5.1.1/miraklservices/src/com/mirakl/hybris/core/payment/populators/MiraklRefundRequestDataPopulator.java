package com.mirakl.hybris.core.payment.populators;

import com.mirakl.client.mmp.domain.payment.refund.MiraklRefundOrder;
import com.mirakl.client.mmp.domain.payment.refund.MiraklRefundOrderLine;
import com.mirakl.client.mmp.domain.payment.refund.MiraklRefundedOrderLine;
import com.mirakl.hybris.beans.MiraklRefundRequestData;

import de.hybris.platform.converters.Populator;

import java.util.List;

public class MiraklRefundRequestDataPopulator implements Populator<MiraklRefundOrder, List<MiraklRefundRequestData>> {

  @Override
  public void populate(MiraklRefundOrder order,  List<MiraklRefundRequestData> refunds) {
    for (MiraklRefundedOrderLine miraklRefundedOrderLine : order.getOrderLines().getOrderLine()) {
      for (MiraklRefundOrderLine miraklRefundOrderLine : miraklRefundedOrderLine.getRefunds().getRefund()) {
        MiraklRefundRequestData refund = new MiraklRefundRequestData();
        refund.setRefundId(miraklRefundOrderLine.getId());
        refund.setAmount(miraklRefundOrderLine.getAmount());
        refund.setCommercialOrderId(order.getOrderCommercialId());
        refund.setMiraklOrderId(order.getOrderId());
        refund.setMiraklOrderLineId(miraklRefundedOrderLine.getOrderLineId());
        refunds.add(refund);
      }
    }
  }

}
