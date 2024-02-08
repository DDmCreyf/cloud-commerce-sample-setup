package com.mirakl.hybris.core.product.services.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.offer.MiraklExportOffer;
import com.mirakl.client.mmp.domain.offer.MiraklOffer;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.request.offer.MiraklGetOfferRequest;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.daos.OfferDao;
import com.mirakl.hybris.core.product.services.MiraklPriceService;
import com.mirakl.hybris.core.product.strategies.OfferCodeGenerationStrategy;
import com.mirakl.hybris.core.product.strategies.OfferRelevanceSortingStrategy;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOfferServiceTest {

  private static final String PRODUCT_CODE = "test";
  private static final String OFFER_ID = "offerId";
  private static final String CUSTOM_FIELDS_JSON = "custom-fields-json";

  @Spy
  @InjectMocks
  private DefaultOfferService testObj;

  @Mock
  private OfferDao offerDao;
  @Mock
  private OfferRelevanceSortingStrategy sortingStrategy;
  @Mock
  private OfferCodeGenerationStrategy offerCodeGenerationStrategy;
  @Mock
  private MiraklExportOffer newMiraklExportOffer, existingMiraklExportOffer;
  @Mock
  private OfferModel newOfferModel, existingOfferModel, offerModelSample1, offerModelSample2;
  @Mock
  private CurrencyModel currency;
  @Mock
  private OfferModel offerModel;
  @Mock
  private CommonI18NService commonI18NService;
  @Mock
  private MiraklMarketplacePlatformFrontApi miraklApi;
  @Mock
  private ModelService modelService;
  @Mock
  private SearchRestrictionService searchRestrictionService;
  @Mock
  private SessionService sessionService;
  @Mock
  private MiraklPriceService miraklPriceService;
  @Mock
  private Converter<MiraklOffer, OfferModel> miraklOfferOfferModelConverter;
  @Mock
  private MiraklOffer miraklOffer;
  @Mock
  private JsonMarshallingService jsonMarshallingService;

  private List<MiraklAdditionalFieldValue> customFields =
      new ArrayList<>(Collections.singletonList(mock(MiraklAdditionalFieldValue.MiraklNumericAdditionalFieldValue.class)));

  @Before
  public void setUp() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
    when(offerDao.findOfferById(OFFER_ID, false)).thenReturn(offerModel);
  }

  @Test
  public void shouldGetOfferForId() {
    OfferModel result = testObj.getOfferForId(OFFER_ID);

    assertThat(result).isEqualTo(offerModel);
  }

  @Test(expected = UnknownIdentifierException.class)
  public void shouldThrowUnknownIdentifierExceptionOnNotFoundOffer() {
    when(offerDao.findOfferById(OFFER_ID, false)).thenReturn(null);

    testObj.getOfferForId(OFFER_ID);
  }

  @Test
  public void getSortedOffersForProductCodeWhenOffersExist() {
    List<OfferModel> sampleOfferList = Arrays.asList(offerModelSample1, offerModelSample2);
    when(commonI18NService.getCurrentCurrency()).thenReturn(currency);
    when(offerDao.findOffersForProductCodeAndCurrency(PRODUCT_CODE, currency)).thenReturn(sampleOfferList);
    when(sortingStrategy.sort(sampleOfferList)).thenReturn(sampleOfferList);

    List<OfferModel> resultObj = testObj.getSortedOffersForProductCode(PRODUCT_CODE);

    assertThat(resultObj).contains(offerModelSample1, offerModelSample2);
  }

  @Test
  public void getSortedOffersForProductCodeWhenNoOffersExist() {
    List<OfferModel> emptyList = new ArrayList<>();
    when(sortingStrategy.sort(emptyList)).thenReturn(emptyList);

    List<OfferModel> resultObj = testObj.getSortedOffersForProductCode(PRODUCT_CODE);

    assertThat(resultObj).hasSize(0);
  }

  @Test
  public void hasOffersReturnsTrueIfAtLeastOneOfferFound() {
    when(offerDao.countOffersForProduct(PRODUCT_CODE)).thenReturn(1);

    boolean result = testObj.hasOffers(PRODUCT_CODE);

    assertThat(result).isTrue();
  }

  @Test
  public void hasOffersReturnsTrueIfMoreThanOneOfferFound() {
    when(offerDao.countOffersForProduct(PRODUCT_CODE)).thenReturn(2);

    boolean result = testObj.hasOffers(PRODUCT_CODE);

    assertThat(result).isTrue();
  }

  @Test
  public void hasOffersReturnsFalseIfNoOfferFound() {
    when(offerDao.countOffersForProduct(PRODUCT_CODE)).thenReturn(0);

    boolean result = testObj.hasOffers(PRODUCT_CODE);

    assertThat(result).isFalse();
  }

  @Test
  public void hasOffersWithCurrencyReturnsTrueIfMoreThanOneOfferFound() {
    when(offerDao.countOffersForProductAndCurrency(PRODUCT_CODE, currency)).thenReturn(2);

    boolean result = testObj.hasOffersWithCurrency(PRODUCT_CODE, currency);

    assertThat(result).isTrue();
  }

  @Test
  public void hasOffersWithCurrencyReturnsFalseIfNoOfferFound() {
    when(offerDao.countOffersForProductAndCurrency(PRODUCT_CODE, currency)).thenReturn(0);

    boolean result = testObj.hasOffersWithCurrency(PRODUCT_CODE, currency);

    assertThat(result).isFalse();
  }

  @Test
  public void countOffersForProductIfMoreThanOneOfferFound() {
    when(offerDao.countOffersForProduct(PRODUCT_CODE)).thenReturn(2);

    int result = testObj.countOffersForProduct(PRODUCT_CODE);

    assertThat(result).isEqualTo(2);
  }

  @Test
  public void updateExistingOfferForId() {
    when(miraklApi.getOffer(any(MiraklGetOfferRequest.class))).thenReturn(miraklOffer);
    doReturn(offerModel).when(testObj).getOfferForIdIgnoreSearchRestrictions(OFFER_ID);

    OfferModel offerModelUpdated = testObj.updateExistingOfferForId(OFFER_ID);

    assertThat(offerModel).isEqualTo(offerModelUpdated);
    verify(miraklOfferOfferModelConverter).convert(miraklOffer, offerModel);
    verify(modelService).save(offerModel);
  }


  public void storeOfferCustomFields() {

    testObj.storeOfferCustomFields(customFields, offerModel);

    verify(offerModel).setCustomFieldsJSON(CUSTOM_FIELDS_JSON);
    verify(modelService).save(offerModel);
  }

  public void shouldLoadOfferCustomFields() {
    when(offerModel.getCustomFieldsJSON()).thenReturn(CUSTOM_FIELDS_JSON);

    List<MiraklAdditionalFieldValue> offerCustomField = testObj.loadOfferCustomFields(offerModel);

    assertThat(offerCustomField).isNotNull();
  }

  @Test
  public void loadOfferCustomFieldsReturnNullIfNotPresent() {
    when(offerModel.getCustomFieldsJSON()).thenReturn(null);

    List<MiraklAdditionalFieldValue> offerCustomField = testObj.loadOfferCustomFields(offerModel);

    assertThat(offerCustomField).isNull();
  }

}
