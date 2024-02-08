package com.mirakl.hybris.occ.order.populators;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.strategies.OfferCodeGenerationStrategy;
import com.mirakl.hybris.facades.product.OfferFacade;

import de.hybris.platform.commercefacades.order.converters.populator.CommerceCartParameterBasicPopulator;
import de.hybris.platform.commercefacades.order.data.AddToCartParams;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DefaultMiraklCartParameterPopulator extends CommerceCartParameterBasicPopulator {

  protected OfferCodeGenerationStrategy offerCodeGenerationStrategy;
  protected OfferFacade offerFacade;

  @Override
  public void populate(AddToCartParams addToCartParams, CommerceCartParameter commerceCartParameter) throws ConversionException {
    final String productCode = addToCartParams.getProductCode();
    if (productCode != null && offerCodeGenerationStrategy.isOfferCode(productCode)) {
      final OfferModel offer = offerFacade.getOfferForCode(productCode);
      commerceCartParameter.setOffer(offer);
      addToCartParams.setProductCode(offer.getProductCode());
    }
    super.populate(addToCartParams, commerceCartParameter);
  }

  @Required
  public void setOfferCodeGenerationStrategy(OfferCodeGenerationStrategy offerCodeGenerationStrategy) {
    this.offerCodeGenerationStrategy = offerCodeGenerationStrategy;
  }

  @Required
  public void setOfferFacade(OfferFacade offerFacade) {
    this.offerFacade = offerFacade;
  }
}
