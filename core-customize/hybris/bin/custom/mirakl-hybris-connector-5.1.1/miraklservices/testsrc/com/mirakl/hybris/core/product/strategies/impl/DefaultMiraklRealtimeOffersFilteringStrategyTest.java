package com.mirakl.hybris.core.product.strategies.impl;

import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.offer.MiraklProductInformationWithReferences;
import com.mirakl.client.mmp.domain.offer.async.export.MiraklAsyncExportOfferPriceContext;
import com.mirakl.client.mmp.domain.offer.price.MiraklOfferPricing;
import com.mirakl.client.mmp.domain.product.MiraklOfferOnProduct;
import com.mirakl.client.mmp.domain.product.MiraklProductWithOffers;
import com.mirakl.hybris.core.product.daos.OfferDao;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.apache.commons.collections4.ListUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklRealtimeOffersFilteringStrategyTest {

  private static final String OFFER_ID_1 = "1";
  private static final String OFFER_ID_2 = "2";
  private static final String OFFER_ID_4 = "4";


  private static final String PRODUCT_CODE_1 = "PRODUCT_1";
  private static final String PRODUCT_CODE_2 = "PRODUCT_2";
  private static final String PRODUCT_CODE_3 = "PRODUCT_3";
  private static final String PRODUCT_CODE_4 = "PRODUCT_4";
  private static final String PRODUCT_CODE_5 = "PRODUCT_5";

  private static final String EUR_CURRENCY_ISOCODE = "EUR";
  private static final String USD_CURRENCY_ISOCODE = "USD";

  @InjectMocks
  private DefaultMiraklRealtimeOffersFilteringStrategy defaultMiraklRealtimeOffersFilteringStrategy;
  @Mock
  private CommonI18NService commonI18NService;
  @Mock
  private OfferDao offerDao;
  @Mock
  private CurrencyModel EUR_CURRENCY;

  @Before
  public void setup() {
    when(commonI18NService.getCurrentCurrency()).thenReturn(EUR_CURRENCY);
    when(EUR_CURRENCY.getIsocode()).thenReturn(EUR_CURRENCY_ISOCODE);
  }

  @Test
  public void shouldReturnExistingOffersWithProperCurrency() {
    // Set specific prices on the offer of the requested product
    // Check that only the expected offers come up, with the expected price and currency

    final BigDecimal priceOffer1_1 = BigDecimal.valueOf(10.0);
    final BigDecimal priceOffer2_1 = BigDecimal.valueOf(12);
    final BigDecimal priceOffer4_1 = BigDecimal.valueOf(14.37);


    MiraklProductWithOffers productOneWithOffers = new ProductWithOffersBuilder().forProduct(PRODUCT_CODE_1)
        .withOffer(OFFER_ID_1, priceOffer1_1, EUR_CURRENCY_ISOCODE)
        .withOffer(OFFER_ID_2, priceOffer2_1, EUR_CURRENCY_ISOCODE)
        .withOffer(OFFER_ID_4, priceOffer4_1, USD_CURRENCY_ISOCODE).build();

    List<MiraklOfferOnProduct> filteredOffersOnProductOne = defaultMiraklRealtimeOffersFilteringStrategy.getFilteredOffers(productOneWithOffers);

    assertThat(filteredOffersOnProductOne.size()).isEqualTo(2);
    List<String> expectedOfferIds = Arrays.asList(OFFER_ID_1, OFFER_ID_2);

    for (MiraklOfferOnProduct offer : filteredOffersOnProductOne) {
      assertThat(offer.getId()).isIn(expectedOfferIds);
    }
  }

  @Test
  public void getFilteredProductCodes() {

    List<String> inputProductCodesList = Arrays.asList(PRODUCT_CODE_1, PRODUCT_CODE_2, PRODUCT_CODE_3, PRODUCT_CODE_4, PRODUCT_CODE_5);
    Map<String, Integer> offersCountByProductCode = new HashMap<>();
    offersCountByProductCode.put(PRODUCT_CODE_1, 3);
    offersCountByProductCode.put(PRODUCT_CODE_2, null);
    offersCountByProductCode.put(PRODUCT_CODE_3, 1);
    offersCountByProductCode.put(PRODUCT_CODE_4, -1);
    offersCountByProductCode.put(PRODUCT_CODE_5, 0);

    when(offerDao.countOffersForProductsAndCurrency(inputProductCodesList, EUR_CURRENCY)).thenReturn(offersCountByProductCode);

    List<String> filteredProductCodes = defaultMiraklRealtimeOffersFilteringStrategy.getFilteredProductCodes(inputProductCodesList);
    assertThat(filteredProductCodes).isNotNull();
    assertThat(filteredProductCodes).hasSize(2);
    assertThat(filteredProductCodes).containsOnly(PRODUCT_CODE_1, PRODUCT_CODE_3);


  }


  private static class ProductWithOffersBuilder {

    private final MiraklProductWithOffers productWithOffers = new MiraklProductWithOffers();

    public ProductWithOffersBuilder forProduct(final String productId) {
      MiraklProductInformationWithReferences ref = new MiraklProductInformationWithReferences();
      ref.setSku(productId);

      productWithOffers.setProduct(ref);
      return this;
    }

    public ProductWithOffersBuilder withOffer(final String offerId, final BigDecimal price, final String currencyIsoCode) {
      MiraklOfferOnProduct offerOnProduct = new MiraklOfferOnProduct();
      offerOnProduct.setId(offerId);
      offerOnProduct.setPrice(price);
      offerOnProduct.setCurrencyIsoCode(MiraklIsoCurrencyCode.valueOf(currencyIsoCode));

      MiraklOfferPricing offerPricing = new MiraklOfferPricing();
      MiraklAsyncExportOfferPriceContext priceContext = new MiraklAsyncExportOfferPriceContext();
      offerPricing.setContext(priceContext);
      offerOnProduct.setApplicablePricing(offerPricing);

      productWithOffers.setOffers(ListUtils.union(singletonList(offerOnProduct), ListUtils.emptyIfNull(productWithOffers.getOffers())));
      return this;
    }

    public MiraklProductWithOffers build() {
      return productWithOffers;
    }
  }
}
