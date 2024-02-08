package com.mirakl.hybris.facades.product.aspects;

import java.util.Collection;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.strategies.OfferCodeGenerationStrategy;
import com.mirakl.hybris.facades.product.OfferFacade;

import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;

/**
 * When {@link ProductFacade#getProductForCodeAndOptions(String, java.util.Collection)} is called with an offer code, this aspect
 * replaces the latter by the product code. It relies on the {@link OfferCodeGenerationStrategy} to determine if a given code
 * corresponds to an offer code.
 *
 */
public class DefaultProductForOfferAspect {

  protected OfferFacade offerFacade;

  protected OfferCodeGenerationStrategy offerCodeGenerationStrategy;

  public Object preGetProductForCodeAndOptions(ProceedingJoinPoint pjp, String code, Collection<ProductOption> options)
      throws Throwable {
    String productCode = code;
    if (offerCodeGenerationStrategy.isOfferCode(code)) {
      OfferModel offer = offerFacade.getOfferForCodeIgnoreSearchRestrictions(code);
      productCode = offer.getProductCode();
    }

    return pjp.proceed(new Object[] {productCode, options});
  }


  @Required
  public void setOfferFacade(OfferFacade offerFacade) {
    this.offerFacade = offerFacade;
  }

  @Required
  public void setOfferCodeGenerationStrategy(OfferCodeGenerationStrategy offerCodeGenerationStrategy) {
    this.offerCodeGenerationStrategy = offerCodeGenerationStrategy;
  }


}
