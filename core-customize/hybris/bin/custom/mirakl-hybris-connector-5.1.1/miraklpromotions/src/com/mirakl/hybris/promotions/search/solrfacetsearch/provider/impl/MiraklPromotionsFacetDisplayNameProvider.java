package com.mirakl.hybris.promotions.search.solrfacetsearch.provider.impl;

import static com.mirakl.hybris.promotions.constants.MiraklpromotionsConstants.SOLR_INDEX_PROMOTION_ID_SEPARATOR;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isBlank;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.promotions.model.MiraklPromotionModel;
import com.mirakl.hybris.promotions.services.MiraklPromotionService;

import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractFacetValueDisplayNameProvider;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

public class MiraklPromotionsFacetDisplayNameProvider extends AbstractFacetValueDisplayNameProvider {

  protected MiraklPromotionService miraklPromotionService;

  @Override
  public String getDisplayName(SearchQuery paramSearchQuery, IndexedProperty paramIndexedProperty, String promotionIndexedId) {
    if (isBlank(promotionIndexedId)) {
      return EMPTY;
    }

    String[] ids = promotionIndexedId.split(SOLR_INDEX_PROMOTION_ID_SEPARATOR, 2);
    if (ids.length != 2) {
      throw new IllegalStateException(
          format("Promotion indexed id must match the pattern {shopId}%s{internalId}. Received value: [%s]",
              SOLR_INDEX_PROMOTION_ID_SEPARATOR, promotionIndexedId));
    }

    String shopId = ids[0];
    String internalId = ids[1];
    MiraklPromotionModel promotion = miraklPromotionService.getPromotion(shopId, internalId);
    if (promotion == null || isBlank(promotion.getPublicDescription())) {
      return internalId;
    }

    return promotion.getPublicDescription();
  }

  @Required
  public void setMiraklPromotionService(MiraklPromotionService miraklPromotionService) {
    this.miraklPromotionService = miraklPromotionService;
  }

}
