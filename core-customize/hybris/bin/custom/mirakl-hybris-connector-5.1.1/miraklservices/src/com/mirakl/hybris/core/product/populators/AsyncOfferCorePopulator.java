package com.mirakl.hybris.core.product.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.offer.async.export.MiraklAsyncExportOffer;
import com.mirakl.client.mmp.domain.offer.async.export.MiraklAsyncExportOfferOfferAdditionalFields;
import com.mirakl.hybris.core.customfields.services.CustomFieldService;
import com.mirakl.hybris.core.enums.OfferState;
import com.mirakl.hybris.core.i18n.services.CurrencyService;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.shop.daos.ShopDao;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import shaded.com.fasterxml.jackson.core.type.TypeReference;

public class AsyncOfferCorePopulator implements Populator<MiraklAsyncExportOffer, OfferModel> {

  protected CurrencyService currencyService;
  protected ShopDao shopDao;
  protected EnumerationService enumerationService;
  protected CustomFieldService customFieldService;
  protected JsonMarshallingService jsonMarshallingService;

  @Override
  public void populate(MiraklAsyncExportOffer miraklOffer, OfferModel hybrisOffer) throws ConversionException {
    validateParameterNotNullStandardMessage("miraklOffer", miraklOffer);
    validateParameterNotNullStandardMessage("hybrisOffer", hybrisOffer);

    if (miraklOffer.getStateCode() != null) {
      populateOfferState(miraklOffer.getStateCode(), hybrisOffer);
    }
    if (miraklOffer.getShopId() != null) {
      populateOfferShop(miraklOffer.getShopId(), hybrisOffer);
    }
    if (miraklOffer.getCurrencyIsoCode() != null) {
      populateOfferCurrency(miraklOffer.getCurrencyIsoCode(), hybrisOffer);
    }

    hybrisOffer.setActive(miraklOffer.getActive());
    hybrisOffer.setAvailableEndDate(miraklOffer.getAvailableEndDate());
    hybrisOffer.setAvailableStartDate(miraklOffer.getAvailableStartDate());
    hybrisOffer.setDeleted(miraklOffer.getDeleted());
    hybrisOffer.setDescription(miraklOffer.getDescription());
    hybrisOffer.setFavoriteRank(miraklOffer.getFavoriteRank());
    hybrisOffer.setId(miraklOffer.getOfferId());
    hybrisOffer.setLeadTimeToShip(miraklOffer.getLeadtimeToShip());

    hybrisOffer.setPriceAdditionalInfo(miraklOffer.getPriceAdditionalInfo());
    hybrisOffer.setProductCode(miraklOffer.getProductSku());
    hybrisOffer.setQuantity(miraklOffer.getQuantity());

    hybrisOffer.setMinShippingPrice(miraklOffer.getMinShippingPrice());
    hybrisOffer.setMinShippingPriceAdditional(miraklOffer.getMinShippingPriceAdditional());

    populateConditions(miraklOffer, hybrisOffer);
    populateCustomFields(miraklOffer.getOfferAdditionalFields(), hybrisOffer);
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

  protected void populateConditions(MiraklAsyncExportOffer miraklAsyncExportOffer, OfferModel offerModel) {
    offerModel.setMinOrderQuantity(miraklAsyncExportOffer.getMinOrderQuantity());
    offerModel.setMaxOrderQuantity(miraklAsyncExportOffer.getMaxOrderQuantity());
    offerModel.setPackageQuantity(miraklAsyncExportOffer.getPackageQuantity());
  }

  protected void populateCustomFields(List<MiraklAsyncExportOfferOfferAdditionalFields> customFields, OfferModel offerModel) {
    List<MiraklAdditionalFieldValue> customFieldValues = customFieldService.getCustomFieldValues(customFields);
    String json = jsonMarshallingService.toJson(customFieldValues, new TypeReference<List<MiraklAdditionalFieldValue>>() {});
    offerModel.setCustomFieldsJSON(json);
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
}
