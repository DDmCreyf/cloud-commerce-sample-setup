package com.mirakl.hybris.promotions.converters.populators;

import static org.apache.commons.lang.StringUtils.isBlank;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class MiraklOrderEntryRaoPopulator implements Populator<AbstractOrderEntryModel, OrderEntryRAO> {
  @Override
  public void populate(AbstractOrderEntryModel source, OrderEntryRAO target) throws ConversionException {
    target.setIsMarketplace(!isBlank(source.getOfferId()));
  }
}
