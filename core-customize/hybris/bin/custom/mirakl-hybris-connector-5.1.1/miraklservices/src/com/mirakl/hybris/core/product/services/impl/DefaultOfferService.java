package com.mirakl.hybris.core.product.services.impl;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.offer.MiraklOffer;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.request.offer.MiraklGetOfferRequest;
import com.mirakl.hybris.beans.OfferOverviewData;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.daos.OfferDao;
import com.mirakl.hybris.core.product.services.MiraklPriceService;
import com.mirakl.hybris.core.product.services.OfferService;
import com.mirakl.hybris.core.product.strategies.OfferRelevanceSortingStrategy;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import org.springframework.beans.factory.annotation.Required;
import shaded.com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;

public class DefaultOfferService implements OfferService {

  private static final String OFFER_PARAMETER = "offer";
  protected OfferDao offerDao;
  protected OfferRelevanceSortingStrategy sortingStrategy;
  protected CommonI18NService commonI18NService;
  protected JsonMarshallingService jsonMarshallingService;
  protected MiraklMarketplacePlatformFrontApi miraklApi;
  protected ModelService modelService;
  protected SearchRestrictionService searchRestrictionService;
  protected SessionService sessionService;
  protected MiraklPriceService miraklPriceService;
  protected Converter<MiraklOffer, OfferModel> miraklOfferUpdateConverter;

  @Override
  public OfferModel getOfferForId(String offerId) {
    return getOfferForId(offerId, false);
  }

  protected OfferModel getOfferForId(String offerId, boolean ignoreQueryDecorators) {
    validateParameterNotNullStandardMessage("offerId", offerId);
    OfferModel offer = offerDao.findOfferById(offerId, ignoreQueryDecorators);
    if (offer == null) {
      throw new UnknownIdentifierException(format("No offer having for id [%s] can be found.", offerId));
    }
    return offer;
  }

  @Override
  public OfferModel getOfferForIdIgnoreSearchRestrictions(final String offerId) {
    return sessionService.executeInLocalView(new SessionExecutionBody() {
      @Override
      public Object execute() {
        try {
          searchRestrictionService.disableSearchRestrictions();
          return getOfferForId(offerId, true);
        } finally {
          searchRestrictionService.enableSearchRestrictions();
        }
      }
    });
  }

  @Override
  public OfferModel updateExistingOfferForId(String offerId) {
    MiraklOffer realTimeOffer = miraklApi.getOffer(new MiraklGetOfferRequest(offerId));
    OfferModel storedOffer = getOfferForIdIgnoreSearchRestrictions(offerId);
    if (realTimeOffer != null) {
      miraklOfferUpdateConverter.convert(realTimeOffer, storedOffer);
      modelService.save(storedOffer);
    }
    return storedOffer;
  }

  @Override
  public List<OfferModel> getOffersForProductCode(String productCode) {
    return offerDao.findOffersForProductCodeAndCurrency(productCode, commonI18NService.getCurrentCurrency());
  }

  @Override
  public Map<String, List<OfferModel>> getOffersForProductCodes(List<String> productCodes) {
    return offerDao.findOffersForProductCodesAndCurrency(productCodes, commonI18NService.getCurrentCurrency());
  }

  @Override
  public List<OfferModel> getSortedOffersForProductCode(String productCode) {
    List<OfferModel> offersModifiableList = new ArrayList<>(getOffersForProductCode(productCode));
    offersModifiableList = sortingStrategy.sort(offersModifiableList);
    return offersModifiableList;
  }

  @Override
  public Map<String, List<OfferModel>> getSortedOffersForProductCodes(List<String> productCodes) {
    return getOffersForProductCodes(productCodes).entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> sortingStrategy.sort(entry.getValue())));
  }

  @Override
  public boolean hasOffers(String productCode) {
    return offerDao.countOffersForProduct(productCode) > 0;
  }

  @Override
  public boolean hasOffersWithCurrency(String productCode, CurrencyModel currency) {
    return offerDao.countOffersForProductAndCurrency(productCode, currency) > 0;
  }

  @Override
  public int countOffersForProduct(String productCode) {
    return offerDao.countOffersForProduct(productCode);
  }

  @Override
  public void storeAllOfferPricings(List<OfferPricingData> allPrices, OfferModel offer) {
    validateParameterNotNullStandardMessage(OFFER_PARAMETER, offer);
    offer.setAllOfferPricingsJSON(jsonMarshallingService.toJson(allPrices));
  }

  @Override
  public List<OfferPricingData> loadAllOfferPricings(OfferModel offer) {
    validateParameterNotNullStandardMessage(OFFER_PARAMETER, offer);
    return getAllOfferPricings(offer.getAllOfferPricingsJSON());
  }

  @Override
  public List<OfferPricingData> loadAllOfferPricings(OfferOverviewData offer) {
    validateParameterNotNullStandardMessage(OFFER_PARAMETER, offer);
    return getAllOfferPricings(offer.getAllOfferPricingsJSON());
  }

  protected List<OfferPricingData> getAllOfferPricings(String allOfferPricingsJSON) {
    if (isBlank(allOfferPricingsJSON)) {
      return emptyList();
    }
    List<OfferPricingData> offerPricings = jsonMarshallingService.fromJson(allOfferPricingsJSON, new TypeReference<List<OfferPricingData>>() {
    });

    if (isNotEmpty(offerPricings)) {
      for (OfferPricingData offerPricing : offerPricings) {
        miraklPriceService.computeAndPopulateApplicablePrice(offerPricing);
      }
    }

    return offerPricings;
  }

  @Override
  public void storeOfferCustomFields(List<MiraklAdditionalFieldValue> customFields, OfferModel offer) {
    validateParameterNotNullStandardMessage(OFFER_PARAMETER, offer);
    if (isNotEmpty(customFields)) {
      offer.setCustomFieldsJSON(jsonMarshallingService.toJson(customFields, new TypeReference<List<MiraklAdditionalFieldValue>>() {
      }));
      modelService.save(offer);
    }
  }

  @Override
  public List<MiraklAdditionalFieldValue> loadOfferCustomFields(OfferModel offer) {
    validateParameterNotNullStandardMessage(OFFER_PARAMETER, offer);
    return jsonMarshallingService.fromJson(offer.getCustomFieldsJSON(), new TypeReference<List<MiraklAdditionalFieldValue>>() {
    });
  }

  @Required
  public void setOfferDao(OfferDao offerDao) {
    this.offerDao = offerDao;
  }

  @Required
  public void setSortingStrategy(OfferRelevanceSortingStrategy sortingStrategy) {
    this.sortingStrategy = sortingStrategy;
  }

  @Required
  public void setCommonI18NService(CommonI18NService commonI18NService) {
    this.commonI18NService = commonI18NService;
  }

  @Required
  public void setJsonMarshallingService(JsonMarshallingService jsonMarshallingService) {
    this.jsonMarshallingService = jsonMarshallingService;
  }

  @Required
  public void setMiraklApi(MiraklMarketplacePlatformFrontApi miraklApi) {
    this.miraklApi = miraklApi;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setSearchRestrictionService(SearchRestrictionService searchRestrictionService) {
    this.searchRestrictionService = searchRestrictionService;
  }

  @Required
  public void setSessionService(SessionService sessionService) {
    this.sessionService = sessionService;
  }

  @Required
  public void setMiraklPriceService(MiraklPriceService miraklPriceService) {
    this.miraklPriceService = miraklPriceService;
  }

  @Required
  public void setMiraklOfferUpdateConverter(Converter<MiraklOffer, OfferModel> miraklOfferUpdateConverter) {
    this.miraklOfferUpdateConverter = miraklOfferUpdateConverter;
  }

}
