package com.mirakl.hybris.core.shop.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isEmpty;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShopStats;
import com.mirakl.hybris.core.enums.PremiumState;
import com.mirakl.hybris.core.enums.ShopState;
import com.mirakl.hybris.core.i18n.services.CountryService;
import com.mirakl.hybris.core.i18n.services.CurrencyService;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import shaded.com.fasterxml.jackson.core.type.TypeReference;

public class ShopPopulator implements Populator<MiraklShop, ShopModel> {

  protected CurrencyService currencyService;
  protected CountryService countryService;
  protected JsonMarshallingService jsonMarshallingService;

  @Override
  public void populate(MiraklShop miraklShop, ShopModel shopModel) throws ConversionException {
    validateParameterNotNullStandardMessage("miraklShop", miraklShop);
    validateParameterNotNullStandardMessage("shopModel", shopModel);

    String currencyCode = miraklShop.getCurrencyIsoCode().toString();
    CurrencyModel currency = currencyService.getCurrencyForCode(currencyCode);
    if (currency != null) {
      shopModel.setCurrency(currency);
    } else {
      throw new ConversionException(format("Impossible to find the currency with code '%s'", currencyCode));
    }

    MiraklShopStats shopStatistic = miraklShop.getShopStatistic();
    if (shopStatistic != null) {
      shopModel.setApprovalDelay(shopStatistic.getApprovalDelay());
      shopModel.setOffersCount(shopStatistic.getOffersCount());
      shopModel.setOrdersCount(shopStatistic.getOrdersCount());
      if (shopStatistic.getApprovalRate() != null) {
        shopModel.setApprovalRate(shopStatistic.getApprovalRate().doubleValue());
      }
      if (shopStatistic.getEvaluationsCount() != null) {
        shopModel.setEvaluationCount(shopStatistic.getEvaluationsCount().intValue());
      }
    }

    if (miraklShop.getShippingInformation() != null) {
      shopModel.setFreeShipping(miraklShop.getShippingInformation().getFreeShipping());
      String shippingCountry = miraklShop.getShippingInformation().getShippingCountry();
      if(!isEmpty(shippingCountry)) {
        shopModel.setShippingCountry(countryService.getCountryForIsoAlpha3Code(shippingCountry));
      }
    }

    if (miraklShop.getPremiumState() != null) {
      shopModel.setPremiumState(PremiumState.valueOf(miraklShop.getPremiumState().toString()));
    }

    if (miraklShop.getState() != null) {
      shopModel.setState(ShopState.valueOf(miraklShop.getState().toString()));
    }

    if (miraklShop.getGrade() != null) {
      shopModel.setGrade(miraklShop.getGrade().doubleValue());
    }

    shopModel.setDescription(miraklShop.getDescription());
    shopModel.setRegistrationDate(miraklShop.getDateCreated());
    shopModel.setClosedFrom(miraklShop.getClosedFrom());
    shopModel.setClosedTo(miraklShop.getClosedTo());
    shopModel.setId(miraklShop.getId());
    shopModel.setInternalId(miraklShop.getOperatorInternalId());
    shopModel.setName(miraklShop.getName());
    shopModel.setPremium(miraklShop.isPremium());
    shopModel.setProfessional(miraklShop.isProfessional());
    shopModel.setReturnPolicy(miraklShop.getReturnPolicy());
    populateCustomFields(miraklShop.getAdditionalFieldValues(), shopModel);
  }

  protected void populateCustomFields(List<MiraklAdditionalFieldValue> customFields, ShopModel shop) {
    shop.setCustomFieldsJSON(
        jsonMarshallingService.toJson(customFields, new TypeReference<List<MiraklAdditionalFieldValue>>() {}));
  }

  @Required
  public void setCurrencyService(CurrencyService currencyService) {
    this.currencyService = currencyService;
  }

  @Required
  public void setCountryService(CountryService countryService) {
    this.countryService = countryService;
  }

  @Required
  public void setJsonMarshallingService(JsonMarshallingService jsonMarshallingService) {
    this.jsonMarshallingService = jsonMarshallingService;
  }

}
