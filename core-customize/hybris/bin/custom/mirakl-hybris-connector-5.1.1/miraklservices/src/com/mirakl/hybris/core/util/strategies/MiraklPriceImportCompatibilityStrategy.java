package com.mirakl.hybris.core.util.strategies;

import com.mirakl.hybris.core.product.decorators.MiraklAsyncOfferPriceDecorator;
import com.mirakl.hybris.core.product.strategies.MiraklRealtimeOfferPriceRetrievalMode;
import com.mirakl.hybris.core.util.feature.MiraklPriceFeature;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

public class MiraklPriceImportCompatibilityStrategy {

  protected final List<MiraklPriceFeature> features;
  protected List<MiraklPriceFeature> enabledFeatures;
  protected MiraklRealtimeOfferPriceRetrievalMode realtimeOfferPriceRetrievalMode;

  public MiraklPriceImportCompatibilityStrategy(List<MiraklPriceFeature> features) {
    this.features = features;
  }

  public boolean supports(MiraklAsyncOfferPriceDecorator price) {
    for (MiraklPriceFeature feature : features) {
      if (feature.isRequired(price) && isDisabled(feature)) {
        return false;
      }
    }
    return true;
  }

  private boolean isDisabled(MiraklPriceFeature feature) {
    return isEmpty(enabledFeatures) || !enabledFeatures.contains(feature);
  }

  @Required
  public void setEnabledFeatures(List<MiraklPriceFeature> enabledFeatures) {
    this.enabledFeatures = enabledFeatures;
  }

  @Required
  public void setRealtimeOfferPriceRetrievalMode(MiraklRealtimeOfferPriceRetrievalMode realtimeOfferPriceRetrievalMode) {
    this.realtimeOfferPriceRetrievalMode = realtimeOfferPriceRetrievalMode;
  }

}
