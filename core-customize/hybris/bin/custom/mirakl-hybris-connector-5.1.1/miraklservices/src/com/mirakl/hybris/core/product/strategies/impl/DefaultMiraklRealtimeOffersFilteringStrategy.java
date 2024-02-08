package com.mirakl.hybris.core.product.strategies.impl;

import com.mirakl.client.mmp.domain.product.MiraklOfferOnProduct;
import com.mirakl.client.mmp.domain.product.MiraklProductWithOffers;
import com.mirakl.hybris.core.product.daos.OfferDao;
import com.mirakl.hybris.core.product.strategies.MiraklRealtimeOffersFilteringStrategy;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DefaultMiraklRealtimeOffersFilteringStrategy implements MiraklRealtimeOffersFilteringStrategy {
  protected OfferDao offerDao;
  protected CommonI18NService commonI18NService;


  @Override
  public List<MiraklOfferOnProduct> getFilteredOffers(final MiraklProductWithOffers productWithOffers) {
    return ListUtils.emptyIfNull(productWithOffers.getOffers()).stream().filter(getOfferFilteringPredicate()).collect(Collectors.toList());
  }

  @Override
  public List<String> getFilteredProductCodes(List<String> productCodes) {
    return offerDao.countOffersForProductsAndCurrency(productCodes, commonI18NService.getCurrentCurrency()).entrySet().stream().filter(nbOffersPerProductEntry -> nbOffersPerProductEntry.getValue() != null && nbOffersPerProductEntry.getValue() > 0).map(Map.Entry::getKey).collect(Collectors.toList());
  }

  private Predicate<? super MiraklOfferOnProduct> getOfferFilteringPredicate() {
    return miraklOfferOnProduct -> StringUtils.equals(miraklOfferOnProduct.getCurrencyIsoCode().name(),
        commonI18NService.getCurrentCurrency().getIsocode());
  }


  @Required
  public void setOfferDao(final OfferDao offerDao) {
    this.offerDao = offerDao;
  }


  @Required
  public void setCommonI18NService(final CommonI18NService commonI18NService) {
    this.commonI18NService = commonI18NService;
  }

}
