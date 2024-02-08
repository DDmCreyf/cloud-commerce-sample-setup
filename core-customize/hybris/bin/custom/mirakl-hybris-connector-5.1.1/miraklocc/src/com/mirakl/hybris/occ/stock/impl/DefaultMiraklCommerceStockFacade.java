package com.mirakl.hybris.occ.stock.impl;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.strategies.OfferCodeGenerationStrategy;
import com.mirakl.hybris.facades.product.OfferFacade;
import com.mirakl.hybris.occ.order.strategies.MiraklStockLevelStatusStrategy;

import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercewebservices.core.stock.impl.DefaultCommerceStockFacade;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

public class DefaultMiraklCommerceStockFacade extends DefaultCommerceStockFacade {

  protected OfferCodeGenerationStrategy offerCodeGenerationStrategy;
  protected OfferFacade offerFacade;
  protected MiraklStockLevelStatusStrategy miraklStockLevelStatusStrategy;

  @Override
  public StockData getStockDataForProductAndBaseSite(String productCode, String baseSiteId)
      throws UnknownIdentifierException, IllegalArgumentException, AmbiguousIdentifierException {
    if (productCode != null && offerCodeGenerationStrategy.isOfferCode(productCode)) {
      final OfferModel offerForCode = offerFacade.getOfferForCode(productCode);
      return createStockData(miraklStockLevelStatusStrategy.getStockLevelStatus(offerForCode),
          Long.valueOf(offerForCode.getQuantity()));
    }
    return super.getStockDataForProductAndBaseSite(productCode, baseSiteId);
  }

  @Required
  public void setOfferCodeGenerationStrategy(OfferCodeGenerationStrategy offerCodeGenerationStrategy) {
    this.offerCodeGenerationStrategy = offerCodeGenerationStrategy;
  }

  @Required
  public void setOfferFacade(OfferFacade offerFacade) {
    this.offerFacade = offerFacade;
  }

  @Required
  public void setMiraklStockLevelStatusStrategy(MiraklStockLevelStatusStrategy miraklStockLevelStatusStrategy) {
    this.miraklStockLevelStatusStrategy = miraklStockLevelStatusStrategy;
  }
}
