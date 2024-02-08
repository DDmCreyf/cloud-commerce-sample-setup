package com.mirakl.hybris.core.product.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.common.MiraklDiscount;
import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.offer.MiraklExportOffer;
import com.mirakl.client.mmp.domain.offer.MiraklOfferMinimumShipping;
import com.mirakl.client.mmp.domain.offer.price.MiraklOfferPricing;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.core.customfields.services.CustomFieldService;
import com.mirakl.hybris.core.enums.MiraklCustomFieldLinkedEntity;
import com.mirakl.hybris.core.enums.OfferState;
import com.mirakl.hybris.core.i18n.services.CurrencyService;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.product.services.OfferService;
import com.mirakl.hybris.core.shop.daos.ShopDao;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import shaded.com.fasterxml.jackson.core.type.TypeReference;

public class OfferPopulator implements Populator<MiraklExportOffer, OfferModel> {

  protected CurrencyService currencyService;
  protected ShopDao shopDao;
  protected EnumerationService enumerationService;
  protected OfferService offerService;
  protected CustomFieldService customFieldService;
  protected JsonMarshallingService jsonMarshallingService;
  protected Converter<MiraklOfferPricing, OfferPricingData> offerPricingToDataConverter;

  @Override
  public void populate(MiraklExportOffer miraklExportOffer, OfferModel offerModel) throws ConversionException {
    validateParameterNotNullStandardMessage("miraklExportOffer", miraklExportOffer);
    validateParameterNotNullStandardMessage("offerModel", offerModel);

    if (miraklExportOffer.getStateCode() != null) {
      populateOfferState(miraklExportOffer.getStateCode(), offerModel);
    }
    if (miraklExportOffer.getShopId() != null) {
      populateOfferShop(miraklExportOffer.getShopId(), offerModel);
    }
    if (miraklExportOffer.getCurrencyIsoCode() != null) {
      populateOfferCurrency(miraklExportOffer.getCurrencyIsoCode(), offerModel);
    }

    offerModel.setActive(miraklExportOffer.isActive());
    offerModel.setAvailableEndDate(miraklExportOffer.getAvailableEndDate());
    offerModel.setAvailableStartDate(miraklExportOffer.getAvailableStartDate());
    offerModel.setDeleted(miraklExportOffer.isDeleted());
    offerModel.setDescription(miraklExportOffer.getDescription());
    offerModel.setFavoriteRank(miraklExportOffer.getFavoriteRank());
    offerModel.setId(miraklExportOffer.getId());
    offerModel.setLeadTimeToShip(miraklExportOffer.getLeadtimeToShip());
    offerModel.setPrice(miraklExportOffer.getPrice());
    offerModel.setPriceAdditionalInfo(miraklExportOffer.getPriceAdditionalInfo());
    offerModel.setProductCode(miraklExportOffer.getProductSku());
    offerModel.setQuantity(miraklExportOffer.getQuantity());
    offerModel.setTotalPrice(miraklExportOffer.getTotalPrice());

    populateOfferMinShipping(miraklExportOffer.getMinShipping(), offerModel);
    populateOfferDiscount(miraklExportOffer.getDiscount(), offerModel);
    populateAllPrices(miraklExportOffer.getAllPrices(), offerModel);
    populateConditions(miraklExportOffer, offerModel);
    populateCustomFields(miraklExportOffer.getAdditionalFields(), offerModel);
  }

  protected void populateOfferState(String stateCode, OfferModel offerModel) {
    try {
      offerModel.setState(enumerationService.getEnumerationValue(OfferState.class, stateCode));
    } catch (UnknownIdentifierException e) {
      throw new ConversionException(format("No offer state found with code [%s]", stateCode), e);
    }
  }

  protected void populateOfferShop(String shopId, OfferModel offerModel) {
    ShopModel shopById = shopDao.findShopById(shopId);
    if (shopById == null) {
      throw new ConversionException(format("No shop found with id [%s]", shopId));
    }
    offerModel.setShop(shopById);
  }

  protected void populateOfferCurrency(MiraklIsoCurrencyCode miraklIsoCurrencyCode, OfferModel offerModel) {
    CurrencyModel currency = currencyService.getCurrencyForCode(miraklIsoCurrencyCode.name());
    if (currency == null) {
      throw new ConversionException(format("No currency found with isoCode [%s]", miraklIsoCurrencyCode.name()));
    }
    offerModel.setCurrency(currency);
  }

  protected void populateOfferMinShipping(MiraklOfferMinimumShipping minShipping, OfferModel offerModel) {
    offerModel.setMinShippingPrice(minShipping != null ? minShipping.getPrice() : null);
    offerModel.setMinShippingPriceAdditional(minShipping != null ? minShipping.getPriceAdditional() : null);
  }

  protected void populateOfferDiscount(MiraklDiscount discount, OfferModel offerModel) {
    offerModel.setDiscountEndDate(discount != null ? discount.getEndDate() : null);
    offerModel.setDiscountStartDate(discount != null ? discount.getStartDate() : null);
    offerModel.setDiscountPrice(discount != null ? discount.getDiscountPrice() : null);
    offerModel.setOriginPrice(discount != null ? discount.getOriginPrice() : null);
  }

  protected void populateAllPrices(List<MiraklOfferPricing> allPrices, OfferModel offerModel) {
    offerService.storeAllOfferPricings(offerPricingToDataConverter.convertAll(allPrices), offerModel);
  }

  protected void populateConditions(MiraklExportOffer miraklExportOffer, OfferModel offerModel) {
    offerModel.setMinOrderQuantity(miraklExportOffer.getMinOrderQuantity());
    offerModel.setMaxOrderQuantity(miraklExportOffer.getMaxOrderQuantity());
    offerModel.setPackageQuantity(miraklExportOffer.getPackageQuantity());
  }

  protected void populateCustomFields(Map<String, String> offerCustomFields, OfferModel offerModel) {
    offerModel.setCustomFieldsJSON(jsonMarshallingService.toJson(
        customFieldService.getCustomFieldValues(offerCustomFields, MiraklCustomFieldLinkedEntity.OFFER),
        new TypeReference<List<MiraklAdditionalFieldValue>>() {}));
  }

  @Required
  public void setOfferService(OfferService offerService) {
    this.offerService = offerService;
  }

  @Required
  public void setEnumerationService(EnumerationService enumerationService) {
    this.enumerationService = enumerationService;
  }

  @Required
  public void setCurrencyService(CurrencyService currencyService) {
    this.currencyService = currencyService;
  }

  @Required
  public void setShopDao(ShopDao shopDao) {
    this.shopDao = shopDao;
  }

  @Required
  public void setCustomFieldService(CustomFieldService customFieldsService) {
    this.customFieldService = customFieldsService;
  }

  @Required
  public void setJsonMarshallingService(JsonMarshallingService jsonMarshallingService) {
    this.jsonMarshallingService = jsonMarshallingService;
  }

  @Required
  public void setOfferPricingToDataConverter(Converter<MiraklOfferPricing, OfferPricingData> offerPricingToDataConverter) {
    this.offerPricingToDataConverter = offerPricingToDataConverter;
  }
}
