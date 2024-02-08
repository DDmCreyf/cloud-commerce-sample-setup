package com.mirakl.hybris.core.ordersplitting.populators;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.order.MiraklRefund;
import com.mirakl.client.mmp.domain.order.refund.MiraklRefundState;

import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklUpdateRefundEntryPopulator implements Populator<MiraklRefund, RefundEntryModel> {

  protected EnumerationService enumerationService;

  @Override
  public void populate(MiraklRefund source, RefundEntryModel target) throws ConversionException {
    target.setReason(enumerationService.getEnumerationValue(RefundReason.class, source.getReasonCode()));
    if (source.getState() == MiraklRefundState.REFUNDED && !target.isConfirmedToMirakl()) {
      target.setConfirmedToMirakl(true);
    }
  }

  @Required
  public void setEnumerationService(EnumerationService enumerationService) {
    this.enumerationService = enumerationService;
  }

}
