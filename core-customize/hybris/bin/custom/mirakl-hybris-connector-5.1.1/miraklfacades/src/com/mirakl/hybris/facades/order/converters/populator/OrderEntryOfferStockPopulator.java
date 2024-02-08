package com.mirakl.hybris.facades.order.converters.populator;

import static java.lang.Long.valueOf;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isEmpty;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.OfferService;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

public class OrderEntryOfferStockPopulator implements Populator<AbstractOrderEntryModel, OrderEntryData> {

  private static final Logger LOG = Logger.getLogger(OrderEntryOfferStockPopulator.class);

  protected OfferService offerService;

  @Override
  public void populate(AbstractOrderEntryModel source, OrderEntryData target) throws ConversionException {
    if (isEmpty(source.getOfferId())) {
      return;
    }

    try {
      OfferModel offer = offerService.getOfferForIdIgnoreSearchRestrictions(source.getOfferId());
      long stockLevel = offer.getQuantity().longValue();
      target.getProduct().getStock().setStockLevel(stockLevel);
      target.getProduct().getStock().setStockLevelStatus(stockLevel > 0 ? StockLevelStatus.INSTOCK : StockLevelStatus.OUTOFSTOCK);
    } catch (UnknownIdentifierException e) {
      LOG.warn(format("Unable to find offer [%s]. Setting stock to 0 for corresponding order entry", source.getOfferId()), e);
      target.getProduct().getStock().setStockLevel(valueOf(0));
      target.getProduct().getStock().setStockLevelStatus(StockLevelStatus.OUTOFSTOCK);
    }

  }

  @Required
  public void setOfferService(OfferService offerService) {
    this.offerService = offerService;
  }
}
