package com.mirakl.hybris.facades.search.solrfacetsearch.populators.impl;

import com.mirakl.hybris.beans.OfferOverviewData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.strategies.OfferCodeGenerationStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.beans.factory.annotation.Required;

import java.util.Optional;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

/**
 * Purchasable quantities are derived from information available on the offer.
 * If no sensible value can be determined, -1 will be returned for minPurchasableQty and maxPurchasableQty
 */
public class MiraklOfferOverviewDataPopulator implements Populator<OfferModel, OfferOverviewData> {

  protected static final int PURCHASABLE_QTY_ERROR = -1;
  protected static final int MIN_PURCHASABLE_QTY_DEFAULT = 1;
  protected static final int DEFAULT_PACKAGE_QTY = 1;

  protected OfferCodeGenerationStrategy offerCodeGenerationStrategy;

  @Override
  public void populate(OfferModel source, OfferOverviewData target) throws ConversionException {
    validateParameterNotNullStandardMessage("source", source);

    target.setCode(offerCodeGenerationStrategy.generateCode(source.getId()));
    target.setQuantity(source.getQuantity());
    target.setPackageQty(source.getPackageQuantity());
    target.setShopId(source.getShop().getId());
    target.setShopGrade(source.getShop().getGrade());
    target.setShopName(source.getShop().getName());
    target.setMinPurchasableQty(source.getMinOrderQuantity());
    target.setMaxPurchasableQty(source.getMaxOrderQuantity());
    target.setStateCode(source.getState() != null ? source.getState().getCode() : null);
  }

  @Required
  public void setOfferCodeGenerationStrategy(OfferCodeGenerationStrategy offerCodeGenerationStrategy) {
    this.offerCodeGenerationStrategy = offerCodeGenerationStrategy;
  }

}
