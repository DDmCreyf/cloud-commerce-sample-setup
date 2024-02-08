package com.mirakl.hybris.facades.product.strategies.impl;

import com.mirakl.hybris.beans.OfferData;
import com.mirakl.hybris.core.enums.OfferState;
import com.mirakl.hybris.core.utils.MiraklMockedServerTest;
import com.mirakl.hybris.core.utils.mock.server.MockServer;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.mirakl.hybris.core.utils.mock.server.MiraklEndpointMatchers.p11;
import static org.fest.assertions.Assertions.assertThat;

@IntegrationTest
public class DefaultMiraklRealtimeOfferDataRetrievalStrategyTest extends MiraklMockedServerTest {

  private static final String P11_GETOFFERS_FILE_PATH = "/miraklfacades/test/real-time.offer/P11_product-offers.json";

  private static final String TEST_DATA_IMPEX_PATH = "/miraklfacades/test/real-time.offer/testData.impex";

  private static final String PRODUCT1_CODE = "3794514";
  private static final String PRODUCT2_CODE = "3780171";

  private static final String OFFER_1_PRODUCT_1_ID = "12103";
  private static final String OFFER_2_PRODUCT_1_ID = "12134";
  private static final String OFFER_1_PRODUCT_2_ID = "12258";
  private static final String LIKE_NEW_STATE_CODE = "1";
  private static final String NEW_STATE_CODE = "11";
  private static final BigDecimal APPLICABLE_PRICE_OFFER1 = BigDecimal.valueOf(15.22);
  private static final BigDecimal APPLICABLE_PRICE_OFFER2 = BigDecimal.valueOf(15.34);
  private static final double TOTAL_PRICE_OFFER1 = BigDecimal.valueOf(18.32).doubleValue();
  private static final double TOTAL_PRICE_OFFER2 = BigDecimal.valueOf(18.44).doubleValue();
  private static final double MIN_SHIPPING_PRICE = 3.10;
  private static final double MIN_SHIPPING_PRICE_ADDITIONAL = 0.51;
  private static final String USD_CURRENCY = "USD";


  @Resource
  private EnumerationService enumerationService;
  @Resource
  private CommonI18NService commonI18NService;
  @Resource(name = "defaultMiraklRealtimeOfferDataRetrievalStrategy")
  private DefaultMiraklRealtimeOfferDataRetrievalStrategy defaultMiraklRealtimeOfferDataRetrievalStrategy;


  @Before
  public void setUp() throws Exception {
    importCsv(TEST_DATA_IMPEX_PATH, "utf-8");
    commonI18NService.setCurrentCurrency(commonI18NService.getCurrency(USD_CURRENCY));
    initMockServer();
  }

  @Test
  public void getOffersForProductCode() {
    mockServer.returnsFile(p11(), P11_GETOFFERS_FILE_PATH);

    List<OfferData> offersForProduct1 = defaultMiraklRealtimeOfferDataRetrievalStrategy.getOffersForProductCode(PRODUCT1_CODE);

    checkOffersOfProduct1(offersForProduct1);
  }

  private void checkOffersOfProduct1(List<OfferData> offersForProduct) {
    assertThat(offersForProduct).isNotNull();
    assertThat(offersForProduct).hasSize(2);
    OfferData firstOffer = offersForProduct.get(0);
    OfferData secondOffer = offersForProduct.get(1);

    checkOffer(firstOffer, OFFER_1_PRODUCT_1_ID, PRODUCT1_CODE, NEW_STATE_CODE, APPLICABLE_PRICE_OFFER1, TOTAL_PRICE_OFFER1);

    checkOffer(secondOffer, OFFER_2_PRODUCT_1_ID, PRODUCT1_CODE, LIKE_NEW_STATE_CODE, APPLICABLE_PRICE_OFFER2, TOTAL_PRICE_OFFER2);
    checkVolumePrices(secondOffer);
  }

  @Test
  public void getOffersForProductsCodes() {
    mockServer.returnsFile(p11(), P11_GETOFFERS_FILE_PATH);

    Map<String, List<OfferData>> offersForProducts = defaultMiraklRealtimeOfferDataRetrievalStrategy.getOffersForProductCodes(Arrays.asList(PRODUCT1_CODE, PRODUCT2_CODE));

    assertThat(offersForProducts).isNotNull();
    assertThat(offersForProducts).hasSize(2);

    checkOffersOfProduct1(offersForProducts.get(PRODUCT1_CODE));
    checkOffersOfProduct2(offersForProducts.get(PRODUCT2_CODE));


  }

  private void checkOffersOfProduct2(List<OfferData> offers) {
    assertThat(offers).isNotNull();
    assertThat(offers).hasSize(1);
    OfferData offer = offers.get(0);

    checkOffer(offer, OFFER_1_PRODUCT_2_ID, PRODUCT2_CODE, NEW_STATE_CODE, APPLICABLE_PRICE_OFFER2, TOTAL_PRICE_OFFER2);

  }

  private void checkVolumePrices(OfferData offer) {
    List<PriceData> volumePrices = offer.getVolumePrices();
    assertThat(volumePrices).isNotNull();
    assertThat(volumePrices).hasSize(2);
    PriceData volumePrice1 = volumePrices.get(0);
    PriceData volumePrice10 = volumePrices.get(1);

    assertThat(volumePrice1.getValue()).isEqualByComparingTo(APPLICABLE_PRICE_OFFER2);
    assertThat(volumePrice1.getMinQuantity()).isEqualTo(1);
    assertThat(volumePrice1.getMaxQuantity()).isEqualTo(9);
    assertThat(volumePrice1.getCurrencyIso()).isEqualTo(USD_CURRENCY);

    assertThat(volumePrice10.getValue()).isEqualByComparingTo(BigDecimal.valueOf(14));
    assertThat(volumePrice10.getMinQuantity()).isEqualTo(10);
    assertThat(volumePrice10.getMaxQuantity()).isNull();
    assertThat(volumePrice10.getCurrencyIso()).isEqualTo(USD_CURRENCY);
  }

  private void checkOffer(OfferData offer, final String offerId, final String productCode, String offerStateCode, BigDecimal applicablePriceOffer, double totalPrice) {
    assertThat(offer).isNotNull();
    assertThat(offer.getProductCode()).isEqualTo(productCode);
    assertThat(offer.getId()).isEqualTo(offerId);
    assertThat(offer.getStateCode()).isEqualTo(offerStateCode);
    assertThat(offer.getState()).isEqualTo(enumerationService.getEnumerationName(OfferState.valueOf(offerStateCode)));
    assertThat(offer.getPrice()).isNotNull();
    assertThat(offer.getPrice().getValue()).isEqualByComparingTo(applicablePriceOffer);
    assertThat(offer.getMinShippingPrice()).isNotNull();
    assertThat(offer.getMinShippingPrice().getValue()).isEqualByComparingTo(BigDecimal.valueOf(MIN_SHIPPING_PRICE));
    assertThat(offer.getMinShippingPriceAdditional()).isNotNull();
    assertThat(offer.getMinShippingPriceAdditional().getValue()).isEqualByComparingTo(BigDecimal.valueOf(MIN_SHIPPING_PRICE_ADDITIONAL));
    assertThat(offer.getTotalPrice()).isNotNull();
    assertThat(offer.getTotalPrice().getValue()).isEqualByComparingTo(BigDecimal.valueOf(totalPrice));

  }

  @After
  public void tearDown() {
    Optional.ofNullable(mockServer).ifPresent(MockServer::stop);
  }
}
