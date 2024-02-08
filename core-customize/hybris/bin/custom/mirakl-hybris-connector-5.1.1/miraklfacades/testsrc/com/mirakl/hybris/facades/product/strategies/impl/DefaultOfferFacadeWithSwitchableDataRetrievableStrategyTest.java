package com.mirakl.hybris.facades.product.strategies.impl;

import com.mirakl.hybris.beans.OfferData;
import com.mirakl.hybris.core.i18n.services.CurrencyService;
import com.mirakl.hybris.core.utils.MiraklMockedServerTest;
import com.mirakl.hybris.core.utils.mock.server.MiraklEndpointMatchers;
import com.mirakl.hybris.core.utils.mock.server.MockServer;
import com.mirakl.hybris.facades.product.impl.DefaultOfferFacade;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.REALTIME_OFFER_PRICE_ACCESS_MODE;
import static org.fest.assertions.Assertions.assertThat;

@IntegrationTest
public class DefaultOfferFacadeWithSwitchableDataRetrievableStrategyTest extends MiraklMockedServerTest {

  private static final String OFFER_DATA_FROM_P11_CALL = "/miraklfacades/test/offerdataretrievalstrategy-offerfacade-realtimeOfferData.json";
  private static final String OFFER_DATA_FROM_DATABASE = "/miraklfacades/test/offerdataretrievalstrategy-offerfacade-persistentOfferData.impex";
  private static final String PRODUCT_CODE = "3794514A";
  private static final String EUR_CURRENCY_ISOCODE = "EUR";

  @Resource(name = "defaultOfferFacade")
  private DefaultOfferFacade defaultOfferFacade;

  @Resource
  private ConfigurationService configurationService;
  @Resource
  private CommonI18NService commonI18NService;
  @Resource
  private CurrencyService currencyService;
  private MockServer mockedServer;

  @Before
  public void setup() throws ImpExException {
    importCsv(OFFER_DATA_FROM_DATABASE, "utf-8");
    commonI18NService.setCurrentCurrency(currencyService.getCurrencyForCode(EUR_CURRENCY_ISOCODE));
    mockedServer = initMockServer();
    mockedServer.returnsFile(MiraklEndpointMatchers.p11(), OFFER_DATA_FROM_P11_CALL);
  }

  @Test
  public void shouldSwitchSelectionModeToGetOfferData() {

    configurationService.getConfiguration().setProperty(REALTIME_OFFER_PRICE_ACCESS_MODE, Boolean.FALSE.toString());
    final List<OfferData> offersForProductFromDatabase = defaultOfferFacade.getOffersForProductCode(PRODUCT_CODE);
    checkOfferPrice(offersForProductFromDatabase, BigDecimal.valueOf(10),EUR_CURRENCY_ISOCODE);

    configurationService.getConfiguration().setProperty(REALTIME_OFFER_PRICE_ACCESS_MODE, Boolean.TRUE.toString());

    final List<OfferData> offersForRealtimeP11Call = defaultOfferFacade.getOffersForProductCode(PRODUCT_CODE);
    checkOfferPrice(offersForRealtimeP11Call, BigDecimal.valueOf(15),EUR_CURRENCY_ISOCODE);
  }

  private static void checkOfferPrice(final List<OfferData> offersForProduct, final BigDecimal priceValue, final String currencyIso) {
    assertThat(offersForProduct).isNotEmpty();
    assertThat(offersForProduct).hasSize(1);
    final OfferData offerData = offersForProduct.iterator().next();
    PriceData offerPrice = offerData.getPrice();
    assertThat(offerPrice).isNotNull();
    assertThat(offerPrice.getValue()).isEqualByComparingTo(priceValue);
    assertThat(offerPrice.getCurrencyIso()).isEqualTo(currencyIso);
  }
}
