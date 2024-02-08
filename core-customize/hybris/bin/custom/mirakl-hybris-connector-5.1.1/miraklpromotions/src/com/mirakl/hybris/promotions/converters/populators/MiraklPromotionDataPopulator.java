package com.mirakl.hybris.promotions.converters.populators;

import static com.mirakl.hybris.promotions.constants.MiraklpromotionsConstants.SOLR_INDEX_PROMOTION_ID_SEPARATOR;
import static com.mirakl.hybris.promotions.constants.MiraklpromotionsConstants.SOLR_MIRAKL_PROMOTIONS_KEY;
import static java.util.Collections.singletonList;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.MiraklPromotionData;
import com.mirakl.hybris.promotions.model.MiraklPromotionModel;

import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class MiraklPromotionDataPopulator implements Populator<MiraklPromotionModel, MiraklPromotionData> {

  protected Converter<SolrSearchQueryData, SearchStateData> solrSearchStateConverter;

  @Override
  public void populate(MiraklPromotionModel miraklPromotionModel, MiraklPromotionData miraklPromotionData)
      throws ConversionException {
    miraklPromotionData.setDescription(miraklPromotionModel.getPublicDescription());
    miraklPromotionData.setSearchPageUrl(getPromotionPageUrl(miraklPromotionModel));
    miraklPromotionData.setMediaUrl(miraklPromotionModel.getMediaUrl());
  }

  protected String getPromotionPageUrl(MiraklPromotionModel promotion) {
    SolrSearchQueryData miraklPromotionsSolrQuery = getSolrQueryForPromotion(promotion);
    SearchStateData promotionsSearchState = solrSearchStateConverter.convert(miraklPromotionsSolrQuery);
    return promotionsSearchState.getUrl();
  }

  protected SolrSearchQueryData getSolrQueryForPromotion(MiraklPromotionModel promotion) {
    SolrSearchQueryData solrQueryData = new SolrSearchQueryData();
    solrQueryData.setFilterTerms(getSolrFilterTermsForPromotion(promotion));
    return solrQueryData;
  }

  protected List<SolrSearchQueryTermData> getSolrFilterTermsForPromotion(MiraklPromotionModel promotion) {
    SolrSearchQueryTermData queryTermData = new SolrSearchQueryTermData();
    queryTermData.setKey(SOLR_MIRAKL_PROMOTIONS_KEY);
    queryTermData.setValue(promotion.getShopId() + SOLR_INDEX_PROMOTION_ID_SEPARATOR + promotion.getInternalId());
    return singletonList(queryTermData);
  }

  @Required
  public void setSolrSearchStateConverter(Converter<SolrSearchQueryData, SearchStateData> solrSearchStateConverter) {
    this.solrSearchStateConverter = solrSearchStateConverter;
  }
}
