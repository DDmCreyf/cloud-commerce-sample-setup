package com.mirakl.hybris.facades.search.solrfacetsearch.populators.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.OfferOverviewData;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.beans.OfferStateSummaryData;
import com.mirakl.hybris.beans.OffersSummaryData;
import com.mirakl.hybris.core.product.services.MiraklPriceService;
import com.mirakl.hybris.core.product.strategies.OfferOverviewRelevanceSortingStrategy;
import com.mirakl.hybris.facades.product.helpers.PriceDataFactoryHelper;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class MiraklOffersSummaryDataPopulator implements Populator<List<OfferOverviewData>, OffersSummaryData> {

  protected MiraklPriceService miraklPriceService;
  protected OfferOverviewRelevanceSortingStrategy sortingStrategy;
  protected Converter<List<OfferOverviewData>, List<OfferStateSummaryData>> offersStateSummaryConverter;
  protected PriceDataFactoryHelper priceDataFactoryHelper;

  @Override
  public void populate(List<OfferOverviewData> source, OffersSummaryData target) throws ConversionException {
    validateParameterNotNullStandardMessage("source", source);

    if (isEmpty(source)) {
      return;
    }
    populatePrices(source);
    List<OfferOverviewData> sortedOffers = sortingStrategy.sort(source);
    target.setBestOffer(sortedOffers.get(0));
    target.setOfferCount(sortedOffers.size());
    target.setStates(offersStateSummaryConverter.convert(sortedOffers));
  }

  protected void populatePrices(List<OfferOverviewData> offers) {
    for (OfferOverviewData offerOverview : offers) {
      if (offerOverview.getAllOfferPricingsJSON() != null) {
        OfferPricingData offerPricing = miraklPriceService.getOfferPricing(offerOverview);
        offerOverview.setPrice(priceDataFactoryHelper.createPrice(offerPricing.getPrice()));
        BigDecimal offerOriginPrice = offerPricing.getUnitOriginPrice();
        if (offerOriginPrice != null) {
          offerOverview.setOriginPrice(priceDataFactoryHelper.createPrice(offerOriginPrice));
        }
      }
    }
  }

  @Required
  public void setSortingStrategy(OfferOverviewRelevanceSortingStrategy sortingStrategy) {
    this.sortingStrategy = sortingStrategy;
  }

  @Required
  public void setOffersStateSummaryConverter(
      Converter<List<OfferOverviewData>, List<OfferStateSummaryData>> offersStateSummaryConverter) {
    this.offersStateSummaryConverter = offersStateSummaryConverter;
  }

  @Required
  public void setMiraklPriceService(MiraklPriceService miraklPriceService) {
    this.miraklPriceService = miraklPriceService;
  }

  @Required
  public void setPriceDataFactoryHelper(PriceDataFactoryHelper priceDataFactoryHelper) {
    this.priceDataFactoryHelper = priceDataFactoryHelper;
  }

}
