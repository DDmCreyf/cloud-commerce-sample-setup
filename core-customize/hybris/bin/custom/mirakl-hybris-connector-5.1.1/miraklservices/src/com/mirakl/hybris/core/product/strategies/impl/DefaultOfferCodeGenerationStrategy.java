package com.mirakl.hybris.core.product.strategies.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang.StringUtils.stripStart;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.product.strategies.OfferCodeGenerationStrategy;

public class DefaultOfferCodeGenerationStrategy implements OfferCodeGenerationStrategy {

  protected String offerCodePrefix;

  @Override
  public String generateCode(String offerId) {
    return offerCodePrefix + offerId;
  }

  @Override
  public String translateCodeToId(String offerCode) {
    checkArgument(isOfferCode(offerCode), "Given offerCode does not match the offer code pattern");

    return stripStart(offerCode, offerCodePrefix);
  }

  @Override
  public boolean isOfferCode(String code) {
    return code.startsWith(offerCodePrefix);
  }

  @Required
  public void setOfferCodePrefix(String offerCodePrefix) {
    this.offerCodePrefix = offerCodePrefix;
  }


}
