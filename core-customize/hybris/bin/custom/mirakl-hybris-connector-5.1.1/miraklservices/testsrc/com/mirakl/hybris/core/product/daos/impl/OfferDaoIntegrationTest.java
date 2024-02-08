package com.mirakl.hybris.core.product.daos.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.*;

import javax.annotation.Resource;

import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.mirakl.hybris.core.enums.OfferState;
import com.mirakl.hybris.core.i18n.services.CurrencyService;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.daos.OfferDao;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

@IntegrationTest
public class OfferDaoIntegrationTest extends ServicelayerTransactionalTest {

  private static final String OFFER_ID_1 = "testOffer1";
  private static final String OFFER_ID_2 = "testOffer2";
  private static final String OFFER_ID_3 = "testOffer3";
  private static final String OFFER_ID_4 = "testOffer4";
  private static final String OFFER_ID_5 = "testOffer5";
  private static final String OFFER_ID_6 = "testOffer6";
  private static final String OFFER_ID_7 = "testOffer7";
  private static final String OFFER_ID_8 = "testOffer8";
  private static final String OFFER_ID_9 = "testOffer9";
  private static final String OFFER_ID_10 = "testOffer10";
  private static final String OFFER_ID_11 = "testOffer11";
  private static final String OFFER_ID_12 = "testOffer12";
  private static final String OFFER_ID_13 = "testOffer13";
  private static final String OFFER_ID_14 = "testOffer14";


  private static final String PRODUCT_CODE_1 = "testProduct1";
  private static final String PRODUCT_CODE_2 = "testProduct2";
  private static final String PRODUCT_CODE_3 = "testProduct3";
  private static final String PRODUCT_CODE_4 = "testProduct4";
  private static final String PRODUCT_CODE_5 = "testProduct5";
  private static final String PRODUCT_CODE_6 = "testProduct6";


  private static final String UNKNOWN_PRODUCT = "UNKNOWN_PRODUCT";
  private static final String CURRENCY_EUR_ISOCODE = "EUR";
  private static final String CURRENCY_USD_ISOCODE = "USD";
  private static final String CURRENCY_FAKE_CURR_ISOCODE = "CURR";

  private static final String OFFRE_STATE_CODE_USED = "1";
  private static final String OFFRE_STATE_CODE_NEW = "11";
  @Resource
  CommonI18NService commonI18NService;
  @Resource
  private OfferDao offerDao;
  @Resource
  private CurrencyService currencyService;

  @Before
  public void setUp() throws ImpExException {
    importCsv("/miraklservices/test/testOffers.impex", "utf-8");
    commonI18NService.setCurrentCurrency(commonI18NService.getCurrency(CURRENCY_USD_ISOCODE));
  }


  @Test
  public void findsOfferById() {
    OfferModel result = offerDao.findOfferById(OFFER_ID_1);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(OFFER_ID_1);
  }

  @Test
  public void findsUndeletedOffersModifiedBeforeDate() {
    List<OfferModel> offers = offerDao.findUndeletedOffersModifiedBeforeDate(new DateTime().plusDays(1).toDate());

    assertThat(offers).hasSize(13);
    assertThat(offers).onProperty(OfferModel.ID)
        .containsOnly(OFFER_ID_1, OFFER_ID_2, OFFER_ID_4, OFFER_ID_5, OFFER_ID_6, OFFER_ID_7, OFFER_ID_8, OFFER_ID_9, OFFER_ID_10, OFFER_ID_11,
            OFFER_ID_12, OFFER_ID_13, OFFER_ID_14);
  }

  @Test
  public void findsNoOffersModifiedBeforeDate() {
    List<OfferModel> offers = offerDao.findUndeletedOffersModifiedBeforeDate(new DateTime().minusDays(1).toDate());

    assertThat(offers).isEmpty();
  }

  @Test
  public void findsOffersForProductCode() {
    List<OfferModel> offers = offerDao.findOffersForProductCode(PRODUCT_CODE_2);

    assertThat(offers).hasSize(2);
    assertThat(offers).onProperty(OfferModel.ID).containsOnly(OFFER_ID_2, OFFER_ID_3);

    offers = offerDao.findOffersForProductCode(PRODUCT_CODE_1);

    assertThat(offers).hasSize(1);
    assertThat(offers).onProperty(OfferModel.ID).containsOnly(OFFER_ID_1);
  }

  @Test
  public void findsOffersForProductCodeAndCurrency() {
    List<OfferModel> offersUSD =
        offerDao.findOffersForProductCodeAndCurrency(PRODUCT_CODE_2, currencyService.getCurrencyForCode(CURRENCY_USD_ISOCODE));

    assertThat(offersUSD).hasSize(1);
    assertThat(offersUSD).onProperty(OfferModel.ID).containsOnly(OFFER_ID_3);

    List<OfferModel> offersEUR =
        offerDao.findOffersForProductCodeAndCurrency(PRODUCT_CODE_2, currencyService.getCurrencyForCode(CURRENCY_EUR_ISOCODE));

    assertThat(offersEUR).hasSize(1);
    assertThat(offersEUR).onProperty(OfferModel.ID).containsOnly(OFFER_ID_2);
  }


  @Test
  public void findsOfferStatesAndCurrencyForProductCode() {
    List<Pair<OfferState, CurrencyModel>> statesAndCurrencies = offerDao.findOfferStatesAndCurrencyForProductCode(PRODUCT_CODE_2);

    assertThat(statesAndCurrencies).hasSize(2);
    assertThat(findPair(OFFRE_STATE_CODE_USED, CURRENCY_USD_ISOCODE, statesAndCurrencies)).isNotNull();
    assertThat(findPair(OFFRE_STATE_CODE_NEW, CURRENCY_USD_ISOCODE, statesAndCurrencies)).isNull();
    assertThat(findPair(OFFRE_STATE_CODE_NEW, CURRENCY_EUR_ISOCODE, statesAndCurrencies)).isNotNull();
  }

  @Test
  public void countsOffersForProduct() {
    assertThat(offerDao.countOffersForProduct(PRODUCT_CODE_1)).isEqualTo(1);
    assertThat(offerDao.countOffersForProduct(PRODUCT_CODE_2)).isEqualTo(2);
  }

  @Test
  public void countsOffersForProductAndCurrency() {
    assertThat(offerDao.countOffersForProductAndCurrency(PRODUCT_CODE_1, currencyService.getCurrencyForCode(CURRENCY_USD_ISOCODE))).isEqualTo(1);
    assertThat(offerDao.countOffersForProductAndCurrency(PRODUCT_CODE_1, currencyService.getCurrencyForCode(CURRENCY_EUR_ISOCODE))).isEqualTo(0);
    assertThat(offerDao.countOffersForProductAndCurrency(PRODUCT_CODE_2, currencyService.getCurrencyForCode(CURRENCY_USD_ISOCODE))).isEqualTo(1);
    assertThat(offerDao.countOffersForProductAndCurrency(PRODUCT_CODE_2, currencyService.getCurrencyForCode(CURRENCY_EUR_ISOCODE))).isEqualTo(1);
  }

  @Test
  public void countsOffersForProductsAndCurrency() {
    String productWithNoOffers = "Product with no offers in database";
    Map<String, List<OfferModel>> offersForProductsInUsd =
        offerDao.findOffersForProductCodesAndCurrency(Arrays.asList(PRODUCT_CODE_1, PRODUCT_CODE_2, PRODUCT_CODE_6, productWithNoOffers),
            currencyService.getCurrencyForCode(CURRENCY_USD_ISOCODE));

    assertContainsExactOffers(offersForProductsInUsd.get(PRODUCT_CODE_1), Arrays.asList(OFFER_ID_1));
    assertContainsExactOffers(offersForProductsInUsd.get(PRODUCT_CODE_2), Arrays.asList(OFFER_ID_3));
    assertContainsExactOffers(offersForProductsInUsd.get(PRODUCT_CODE_6), Arrays.asList(OFFER_ID_12, OFFER_ID_13, OFFER_ID_14));
    assertTrue(offersForProductsInUsd.containsKey(productWithNoOffers));
    assertThat(offersForProductsInUsd.get(productWithNoOffers)).isEmpty();

    Map<String, List<OfferModel>> offersForProductsInEur =
        offerDao.findOffersForProductCodesAndCurrency(Arrays.asList(PRODUCT_CODE_4, PRODUCT_CODE_6, productWithNoOffers),
            currencyService.getCurrencyForCode(CURRENCY_EUR_ISOCODE));

    assertContainsExactOffers(offersForProductsInEur.get(PRODUCT_CODE_4), Arrays.asList(OFFER_ID_7));
    assertContainsExactOffers(offersForProductsInEur.get(PRODUCT_CODE_6), Arrays.asList(OFFER_ID_9, OFFER_ID_10, OFFER_ID_11));
    assertTrue(offersForProductsInUsd.containsKey(productWithNoOffers));
    assertThat(offersForProductsInUsd.get(productWithNoOffers)).isEmpty();

  }


  @Test
  public void getOffersIdForProductCode() {
    assertThat(offerDao.findOfferIdsForProductCode(PRODUCT_CODE_1)).containsExactly(OFFER_ID_1);
    assertThat(offerDao.findOfferIdsForProductCode(PRODUCT_CODE_2)).containsOnly(OFFER_ID_2, OFFER_ID_3);
    assertThat(offerDao.findOfferIdsForProductCode(UNKNOWN_PRODUCT)).isEmpty();
  }

  @Test
  public void shouldReturnCountForKnownProducts() {
    Map<String, Integer> offerCounts =
        offerDao.countOffersForProductsAndCurrency(Arrays.asList(PRODUCT_CODE_3, PRODUCT_CODE_4, PRODUCT_CODE_5, UNKNOWN_PRODUCT),
            currencyService.getCurrencyForCode(CURRENCY_FAKE_CURR_ISOCODE));
    assertThat(offerCounts.get(PRODUCT_CODE_3)).isEqualTo(2);
    assertThat(offerCounts.get(PRODUCT_CODE_4)).isEqualTo(1);
    assertThat(offerCounts.get(PRODUCT_CODE_5)).isNull();
    assertThat(offerCounts.get(UNKNOWN_PRODUCT)).isNull();
  }


  private Pair<OfferState, CurrencyModel> findPair(String offerStateCode, String currencyIsocode,
      List<Pair<OfferState, CurrencyModel>> offerStatesAndCurrencies) {
    for (Pair<OfferState, CurrencyModel> pair : offerStatesAndCurrencies) {
      if (offerStateCode.equals(pair.getLeft().getCode()) && currencyIsocode.equals(pair.getRight().getIsocode())) {
        return pair;
      }
    }
    return null;
  }

  private void assertContainsExactOffers(List<OfferModel> actualOffers, List<String> expectedOfferIds) {
    assertThat(actualOffers).onProperty(OfferModel.ID).containsOnly(expectedOfferIds.toArray());

  }
}
