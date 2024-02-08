package com.mirakl.hybris.core.order.populators;

import org.apache.commons.lang3.tuple.Pair;

import com.mirakl.client.mmp.domain.shipping.MiraklShippingTypeWithConfiguration;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklAbstractOrderEntryShippingPopulator
    implements Populator<Pair<MiraklOrderShippingFee, MiraklOrderShippingFeeOffer>, AbstractOrderEntryModel> {

  @Override
  public void populate(Pair<MiraklOrderShippingFee, MiraklOrderShippingFeeOffer> source, AbstractOrderEntryModel target)
      throws ConversionException {
    MiraklOrderShippingFee orderShippingFee = source.getLeft();
    MiraklOrderShippingFeeOffer orderLineShippingFee = source.getRight();

    target.setLineShippingPrice(orderLineShippingFee.getLineShippingPrice().doubleValue());
    MiraklShippingTypeWithConfiguration selectedShippingType = orderShippingFee.getSelectedShippingType();
    target.setLineShippingCode(selectedShippingType.getCode());
    target.setLineShippingLabel(selectedShippingType.getLabel());
  }

}
