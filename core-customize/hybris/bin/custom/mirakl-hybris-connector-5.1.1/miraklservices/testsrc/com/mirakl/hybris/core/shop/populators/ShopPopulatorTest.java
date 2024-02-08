package com.mirakl.hybris.core.shop.populators;

import static java.lang.String.valueOf;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.shop.MiraklPremiumState;
import com.mirakl.client.mmp.domain.shop.MiraklShippingInformation;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShopState;
import com.mirakl.client.mmp.domain.shop.MiraklShopStats;
import com.mirakl.hybris.core.i18n.services.CountryService;
import com.mirakl.hybris.core.i18n.services.CurrencyService;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import shaded.com.fasterxml.jackson.core.type.TypeReference;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ShopPopulatorTest {

  private static final String SHOP_ID = "2086";
  private static final String OPERATOR_INTERNAL_ID = "operator-internal-id";
  private static final BigDecimal GRADE = BigDecimal.valueOf(4.567);
  private static final String NAME = "name";
  private static final MiraklPremiumState PREMIUM_STATE = MiraklPremiumState.NOT_PREMIUM;
  private static final Long EVALUATION_COUNT = 10000L;
  private static final MiraklShopState STATE = MiraklShopState.SUSPENDED;
  private static final MiraklIsoCurrencyCode CURRENCY_CODE = MiraklIsoCurrencyCode.USD;
  private static final String SHOP_COUNTRY_ISO_ALPHA_3 = "FRA";
  private static final String JSON_SHOP_CUSTOM_FIELD = "json-shop-custom-field";

  @InjectMocks
  private ShopPopulator populator;

  @Mock
  private MiraklShop miraklShop;
  @Mock
  private MiraklShippingInformation miraklShippingInfo;
  @Mock
  private MiraklShopStats miraklShopStats;
  @Mock
  private CurrencyService currencyService;
  @Mock
  private CountryService countryService;
  @Mock
  private CurrencyModel currencyModel;
  @Mock
  private CountryModel countryModel;
  @Mock
  private List<MiraklAdditionalFieldValue> customFields;
  @Mock
  private JsonMarshallingService jsonMarshallingService;

  @Before
  public void setUp() {
    when(miraklShop.getId()).thenReturn(SHOP_ID);
    when(miraklShop.getOperatorInternalId()).thenReturn(OPERATOR_INTERNAL_ID);
    when(miraklShop.getName()).thenReturn(NAME);
    when(miraklShop.getGrade()).thenReturn(GRADE);
    when(miraklShop.getPremiumState()).thenReturn(PREMIUM_STATE);
    when(miraklShop.getState()).thenReturn(STATE);
    when(miraklShop.getGrade()).thenReturn(GRADE);
    when(miraklShop.getShopStatistic()).thenReturn(miraklShopStats);
    when(miraklShop.getAdditionalFieldValues()).thenReturn(customFields);
    when(miraklShopStats.getEvaluationsCount()).thenReturn(EVALUATION_COUNT);
    when(miraklShop.getCurrencyIsoCode()).thenReturn(CURRENCY_CODE);
    when(currencyService.getCurrencyForCode(CURRENCY_CODE.name())).thenReturn(currencyModel);
    when(miraklShop.getShippingInformation()).thenReturn(miraklShippingInfo);
    when(miraklShippingInfo.getShippingCountry()).thenReturn(SHOP_COUNTRY_ISO_ALPHA_3);
    when(countryService.getCountryForIsoAlpha3Code(SHOP_COUNTRY_ISO_ALPHA_3)).thenReturn(countryModel);
    when(jsonMarshallingService.toJson(eq(customFields), any(TypeReference.class))).thenReturn(JSON_SHOP_CUSTOM_FIELD);
  }

  @Test
  public void populate() {
    ShopModel shopModel = new ShopModel();
    populator.populate(miraklShop, shopModel);

    assertThat(shopModel.getId()).isEqualTo(SHOP_ID);
    assertThat(shopModel.getInternalId()).isEqualTo(OPERATOR_INTERNAL_ID);
    assertThat(shopModel.getName()).isEqualTo(NAME);
    assertThat(shopModel.getGrade()).isEqualTo((Double) GRADE.doubleValue());
    assertThat(valueOf(shopModel.getPremiumState())).isEqualTo(valueOf(PREMIUM_STATE));
    assertThat(valueOf(shopModel.getState())).isEqualTo(valueOf(STATE));
    assertThat((Long) shopModel.getEvaluationCount().longValue()).isEqualTo(EVALUATION_COUNT);
    assertThat(shopModel.getShippingCountry()).isEqualTo(countryModel);
    assertThat(shopModel.getCustomFieldsJSON()).isEqualTo(JSON_SHOP_CUSTOM_FIELD);
  }

}
