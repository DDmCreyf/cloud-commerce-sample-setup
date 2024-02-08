package com.mirakl.hybris.core.ordersplitting.populators;

import org.apache.commons.lang3.tuple.Pair;

import com.mirakl.client.mmp.domain.order.MiraklOrderLine;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklCreateConsignmentEntryPopulator
    implements Populator<Pair<AbstractOrderEntryModel, MiraklOrderLine>, ConsignmentEntryModel> {

  @Override
  public void populate(Pair<AbstractOrderEntryModel, MiraklOrderLine> source, ConsignmentEntryModel target)
      throws ConversionException {
    target.setOrderEntry(source.getLeft());
    target.setQuantity((long) source.getRight().getQuantity());
    target.setMiraklOrderLineId(source.getRight().getId());
  }

}
