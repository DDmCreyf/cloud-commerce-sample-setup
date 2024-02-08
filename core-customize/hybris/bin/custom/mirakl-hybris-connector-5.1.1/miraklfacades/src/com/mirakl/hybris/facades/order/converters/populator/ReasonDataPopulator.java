package com.mirakl.hybris.facades.order.converters.populator;

import com.mirakl.client.mmp.domain.reason.MiraklReason;
import com.mirakl.hybris.beans.ReasonData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class ReasonDataPopulator implements Populator<MiraklReason, ReasonData> {
  @Override
  public void populate(MiraklReason miraklReason, ReasonData reasonData) throws ConversionException {
    reasonData.setCode(miraklReason.getCode());
    reasonData.setLabel(miraklReason.getLabel());
  }
}
