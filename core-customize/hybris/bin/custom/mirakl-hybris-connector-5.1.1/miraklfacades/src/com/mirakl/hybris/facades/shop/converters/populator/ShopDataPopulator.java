package com.mirakl.hybris.facades.shop.converters.populator;

import static java.util.Collections.singletonList;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.ShopData;
import com.mirakl.hybris.core.model.ShopModel;

import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class ShopDataPopulator implements Populator<ShopModel, ShopData> {

  protected static final String SELLER_KEY = "seller";

  protected Converter<SolrSearchQueryData, SearchStateData> solrSearchStateConverter;

  @Override
  public void populate(ShopModel shopModel, ShopData shopData) throws ConversionException {

    shopData.setApprovalDelay(shopModel.getApprovalDelay());
    shopData.setApprovalRate(shopModel.getApprovalRate());
    shopData.setAvailableShippingOptions(null);
    shopData.setDescription(shopModel.getDescription());
    shopData.setEvaluationCount(shopModel.getEvaluationCount());
    shopData.setGrade(shopModel.getGrade());
    shopData.setId(shopModel.getId());
    shopData.setName(shopModel.getName());
    shopData.setPremium(shopModel.getPremium());
    shopData.setRegistrationDate(shopModel.getRegistrationDate());
    shopData.setReturnPolicy(shopModel.getReturnPolicy());
    if (shopModel.getShippingCountry() != null) {
      shopData.setShippingCountry(shopModel.getShippingCountry().getName());
    }
    if (shopModel.getBanner() != null) {
      shopData.setBanner(shopModel.getBanner().getURL());
    }
    if (shopModel.getLogo() != null) {
      shopData.setLogo(shopModel.getLogo().getURL());
    }

    shopData.setOffersPageUrl(getOffersPageUrl(shopModel));

  }

  protected String getOffersPageUrl(ShopModel shopModel) {
    SolrSearchQueryData shopOffersSolrQuery = getSolrQueryForShopOffers(shopModel);
    SearchStateData shopOffersSearchState = solrSearchStateConverter.convert(shopOffersSolrQuery);
    return shopOffersSearchState.getUrl();
  }

  protected SolrSearchQueryData getSolrQueryForShopOffers(ShopModel shopModel) {
    SolrSearchQueryData solrQueryData = new SolrSearchQueryData();
    solrQueryData.setFilterTerms(getSolrFilterTermsForShopOffers(shopModel));
    return solrQueryData;
  }

  protected List<SolrSearchQueryTermData> getSolrFilterTermsForShopOffers(ShopModel shopModel) {
    SolrSearchQueryTermData queryTermData = new SolrSearchQueryTermData();
    queryTermData.setKey(SELLER_KEY);
    queryTermData.setValue(shopModel.getId());
    return singletonList(queryTermData);
  }

  @Required
  public void setSolrSearchStateConverter(Converter<SolrSearchQueryData, SearchStateData> solrSearchStateConverter) {
    this.solrSearchStateConverter = solrSearchStateConverter;
  }
}
