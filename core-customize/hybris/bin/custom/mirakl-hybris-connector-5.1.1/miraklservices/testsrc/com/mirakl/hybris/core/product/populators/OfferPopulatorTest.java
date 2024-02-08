package com.mirakl.hybris.core.product.populators;

import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
import com.mirakl.hybris.core.product.strategies.OfferCodeGenerationStrategy;
import com.mirakl.hybris.core.shop.daos.ShopDao;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import shaded.com.fasterxml.jackson.core.type.TypeReference;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OfferPopulatorTest {

  private static final String OFFER_ID = "offerId";
  private static final String OFFER_DESCRIPTION = "offerDescription";
  private static final String OFFER_PRICE_ADDITIONAL_INFO = "offerPriceAdditionalInfo";
  private static final String PRODUCT_CODE = "productCode";
  private static final String OFFER_SHOP_ID = "offerShopId";
  private static final String OFFER_STATE_CODE = "stateCode";
  private static final String JSON_OFFER_CUSTOM_FIELD = "json-offer-custom-field";
  private static final int FAVORITE_RANK = 100;
  private static final int LEAD_TIME_TO_SHIP = 20;
  private static final int OFFER_QUANTITY = 999;
  private static final BigDecimal OFFER_PRICE = new BigDecimal(99.99);
  private static final BigDecimal OFFER_DISCOUNT_PRICE = new BigDecimal(88.88);
  private static final BigDecimal OFFER_ORIGINAL_PRICE = new BigDecimal(20.00);
  private static final BigDecimal OFFER_MIN_SHIPPING_PRICE = new BigDecimal(10.00);
  private static final BigDecimal OFFER_MIN_SHIPPING_PRICE_ADDITIONAL = new BigDecimal(5.00);
  private static final BigDecimal OFFER_TOTAL_PRICE = new BigDecimal(109.99);

  @InjectMocks
  private OfferPopulator populator;

  @Mock
  private CurrencyService currencyService;
  @Mock
  private ShopDao shopDao;
  @Mock
  private EnumerationService enumerationService;
  @Mock
  private OfferCodeGenerationStrategy offerCodeGenerationStrategy;
  @Mock
  private OfferService offerService;
  @Mock
  private Converter<MiraklOfferPricing, OfferPricingData> offerPricingToDataConverter;

  @Mock
  private MiraklExportOffer miraklExportOffer;
  @Mock
  private OfferModel offerModel;
  @Mock
  private Date availableStartDate, availableEndDate, discountStartDate, discountEndDate;
  @Mock
  private MiraklDiscount miraklDiscount;
  @Mock
  private MiraklOfferMinimumShipping miraklOfferMinimumShipping;
  @Mock
  private CurrencyModel currency;
  @Mock
  private ShopModel shop;
  @Mock
  private OfferState offerState;
  @Mock
  private List<MiraklOfferPricing> allOfferPricings;
  @Mock
  private List<OfferPricingData> allOfferDataPricings;
  @Mock
  private Map<String, String> customFields;
  @Mock
  private List<MiraklAdditionalFieldValue> convertedCustomFields;
  @Mock
  private CustomFieldService customFieldsService;
  @Mock
  private JsonMarshallingService jsonMarshallingService;

  @Before
  public void setUp() {
    when(miraklExportOffer.getId()).thenReturn(OFFER_ID);
    when(miraklExportOffer.getAvailableEndDate()).thenReturn(availableEndDate);
    when(miraklExportOffer.getAvailableStartDate()).thenReturn(availableStartDate);
    when(miraklExportOffer.getCurrencyIsoCode()).thenReturn(MiraklIsoCurrencyCode.EUR);
    when(miraklExportOffer.getDescription()).thenReturn(OFFER_DESCRIPTION);
    when(miraklExportOffer.getFavoriteRank()).thenReturn(FAVORITE_RANK);
    when(miraklExportOffer.getLeadtimeToShip()).thenReturn(LEAD_TIME_TO_SHIP);
    when(miraklExportOffer.getPrice()).thenReturn(OFFER_PRICE);
    when(miraklExportOffer.getPriceAdditionalInfo()).thenReturn(OFFER_PRICE_ADDITIONAL_INFO);
    when(miraklExportOffer.getProductSku()).thenReturn(PRODUCT_CODE);
    when(miraklExportOffer.getDiscount()).thenReturn(miraklDiscount);
    when(miraklExportOffer.getMinShipping()).thenReturn(miraklOfferMinimumShipping);
    when(miraklExportOffer.getQuantity()).thenReturn(OFFER_QUANTITY);
    when(miraklExportOffer.getShopId()).thenReturn(OFFER_SHOP_ID);
    when(miraklExportOffer.isDeleted()).thenReturn(false);
    when(miraklExportOffer.isActive()).thenReturn(true);
    when(miraklExportOffer.getTotalPrice()).thenReturn(OFFER_TOTAL_PRICE);
    when(miraklExportOffer.getStateCode()).thenReturn(OFFER_STATE_CODE);
    when(miraklExportOffer.getAllPrices()).thenReturn(allOfferPricings);
    when(miraklExportOffer.getAdditionalFields()).thenReturn(customFields);

    when(miraklDiscount.getDiscountPrice()).thenReturn(OFFER_DISCOUNT_PRICE);
    when(miraklDiscount.getOriginPrice()).thenReturn(OFFER_ORIGINAL_PRICE);
    when(miraklDiscount.getStartDate()).thenReturn(discountStartDate);
    when(miraklDiscount.getEndDate()).thenReturn(discountEndDate);

    when(miraklOfferMinimumShipping.getPrice()).thenReturn(OFFER_MIN_SHIPPING_PRICE);
    when(miraklOfferMinimumShipping.getPriceAdditional()).thenReturn(OFFER_MIN_SHIPPING_PRICE_ADDITIONAL);

    when(currencyService.getCurrencyForCode(MiraklIsoCurrencyCode.EUR.name())).thenReturn(currency);
    when(shopDao.findShopById(OFFER_SHOP_ID)).thenReturn(shop);
    when(enumerationService.getEnumerationValue(OfferState.class, OFFER_STATE_CODE)).thenReturn(offerState);

    when(customFieldsService.getCustomFieldValues(customFields, MiraklCustomFieldLinkedEntity.OFFER))
        .thenReturn(convertedCustomFields);
    when(jsonMarshallingService.toJson(eq(convertedCustomFields), any(TypeReference.class))).thenReturn(JSON_OFFER_CUSTOM_FIELD);

    when(offerPricingToDataConverter.convertAll(allOfferPricings)).thenReturn(allOfferDataPricings);
  }

  @Test
  public void populatesAllOfferProperties() {
    populator.populate(miraklExportOffer, offerModel);

    verifyPopulateNullableOfferProperties();
    verifyPopulateOfferDiscount();
    verifyPopulateOfferMinShipping();
    verifyPopulateAllOfferPricings();
    verifyPopulateCustomFields();

    verify(offerModel).setCurrency(currency);
    verify(offerModel).setState(offerState);
    verify(offerModel).setShop(shop);
  }

  @Test
  public void populatesOfferPropertiesWithoutDiscount() {
    when(miraklExportOffer.getDiscount()).thenReturn(null);

    populator.populate(miraklExportOffer, offerModel);

    verifyPopulateNullableOfferProperties();
    verifyPopulateOfferMinShipping();

    verify(offerModel).setCurrency(currency);
    verify(offerModel).setState(offerState);
    verify(offerModel).setShop(shop);
  }

  @Test
  public void populatesOfferPropertiesWithoutMinShipping() {
    when(miraklExportOffer.getMinShipping()).thenReturn(null);

    populator.populate(miraklExportOffer, offerModel);

    verifyPopulateNullableOfferProperties();
    verifyPopulateOfferDiscount();

    verify(offerModel).setCurrency(currency);
    verify(offerModel).setState(offerState);
    verify(offerModel).setShop(shop);
  }

  @Test
  public void populatesOfferPropertiesWithoutOfferState() {
    when(miraklExportOffer.getStateCode()).thenReturn(null);

    populator.populate(miraklExportOffer, offerModel);

    verifyPopulateNullableOfferProperties();
    verifyPopulateOfferMinShipping();
    verifyPopulateOfferDiscount();

    verify(offerModel).setCurrency(currency);
    verify(offerModel, never()).setState(any(OfferState.class));
    verify(offerModel).setShop(shop);
  }

  @Test(expected = ConversionException.class)
  public void populateThrowsConversionExceptionIfNoOfferStateFoundForOffer() {
    when(enumerationService.getEnumerationValue(OfferState.class, OFFER_STATE_CODE))
        .thenThrow(new UnknownIdentifierException(EMPTY));

    populator.populate(miraklExportOffer, offerModel);
  }

  @Test
  public void populatesOfferPropertiesWithoutCurrency() {
    when(miraklExportOffer.getCurrencyIsoCode()).thenReturn(null);

    populator.populate(miraklExportOffer, offerModel);

    verifyPopulateNullableOfferProperties();
    verifyPopulateOfferMinShipping();
    verifyPopulateOfferDiscount();

    verify(offerModel, never()).setCurrency(any(CurrencyModel.class));
    verify(offerModel).setState(offerState);
    verify(offerModel).setShop(shop);
  }

  @Test
  public void populatesOfferPropertiesWithoutShop() {
    when(miraklExportOffer.getShopId()).thenReturn(null);

    populator.populate(miraklExportOffer, offerModel);

    verifyPopulateNullableOfferProperties();
    verifyPopulateOfferMinShipping();
    verifyPopulateOfferDiscount();

    verify(offerModel).setCurrency(currency);
    verify(offerModel).setState(offerState);
    verify(offerModel, never()).setShop(any(ShopModel.class));
  }

  @Test(expected = ConversionException.class)
  public void populateThrowsConversionExceptionIfNoShopFoundForOffer() {
    when(shopDao.findShopById(OFFER_SHOP_ID)).thenReturn(null);

    populator.populate(miraklExportOffer, offerModel);
  }

  @Test(expected = ConversionException.class)
  public void populateThrowsConversionExceptionIfCurrencyFoundForOffer() {
    when(currencyService.getCurrencyForCode(MiraklIsoCurrencyCode.EUR.name())).thenReturn(null);

    populator.populate(miraklExportOffer, offerModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void throwsIllegalArgumentExceptionIfMiraklExportOfferIsNull() {
    populator.populate(null, offerModel);
  }

  @Test(expected = IllegalArgumentException.class)
  public void throwsIllegalArgumentExceptionIfOfferModelIsNull() {
    populator.populate(miraklExportOffer, null);
  }

  @Test
  public void shouldEraseDiscountInformations() throws Exception {
    when(miraklExportOffer.getDiscount()).thenReturn(null);

    populator.populate(miraklExportOffer, offerModel);

    verify(offerModel).setDiscountStartDate(null);
    verify(offerModel).setDiscountEndDate(null);
    verify(offerModel).setDiscountPrice(null);
    verify(offerModel).setOriginPrice(null);
  }

  @Test
  public void shouldEraseOfferMinShipping() throws Exception {
    when(miraklExportOffer.getMinShipping()).thenReturn(null);

    populator.populate(miraklExportOffer, offerModel);

    verify(offerModel).setMinShippingPrice(null);
    verify(offerModel).setMinShippingPriceAdditional(null);
  }

  private void verifyPopulateNullableOfferProperties() {
    verify(offerModel).setActive(true);
    verify(offerModel).setAvailableEndDate(availableEndDate);
    verify(offerModel).setAvailableStartDate(availableStartDate);
    verify(offerModel).setDeleted(false);
    verify(offerModel).setDescription(OFFER_DESCRIPTION);
    verify(offerModel).setFavoriteRank(FAVORITE_RANK);
    verify(offerModel).setId(OFFER_ID);
    verify(offerModel).setLeadTimeToShip(LEAD_TIME_TO_SHIP);
    verify(offerModel).setPrice(OFFER_PRICE);
    verify(offerModel).setPriceAdditionalInfo(OFFER_PRICE_ADDITIONAL_INFO);
    verify(offerModel).setProductCode(PRODUCT_CODE);
    verify(offerModel).setQuantity(OFFER_QUANTITY);
    verify(offerModel).setTotalPrice(OFFER_TOTAL_PRICE);
  }

  private void verifyPopulateOfferDiscount() {
    verify(offerModel).setDiscountEndDate(discountEndDate);
    verify(offerModel).setDiscountStartDate(discountStartDate);
    verify(offerModel).setDiscountPrice(OFFER_DISCOUNT_PRICE);
    verify(offerModel).setOriginPrice(OFFER_ORIGINAL_PRICE);
  }

  private void verifyPopulateOfferMinShipping() {
    verify(offerModel).setMinShippingPrice(OFFER_MIN_SHIPPING_PRICE);
    verify(offerModel).setMinShippingPriceAdditional(OFFER_MIN_SHIPPING_PRICE_ADDITIONAL);
  }

  private void verifyPopulateAllOfferPricings() {
    verify(offerService).storeAllOfferPricings(allOfferDataPricings, offerModel);
  }

  private void verifyPopulateCustomFields() {
    verify(offerModel).setCustomFieldsJSON(JSON_OFFER_CUSTOM_FIELD);
  }

}
