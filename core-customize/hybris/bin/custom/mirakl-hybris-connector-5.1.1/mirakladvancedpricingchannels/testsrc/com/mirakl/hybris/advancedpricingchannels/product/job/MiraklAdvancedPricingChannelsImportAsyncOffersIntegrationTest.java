package com.mirakl.hybris.advancedpricingchannels.product.job;

import static com.mirakl.hybris.advancedpricing.product.utils.OfferPricingKey.newOfferPricingKey;
import static com.mirakl.hybris.core.enums.MiraklAsyncTaskType.OFFER_IMPORT_FULL;
import static com.mirakl.hybris.core.utils.assertions.MiraklAssertions.assertThatExtractedFields;
import static com.mirakl.hybris.core.utils.assertions.Tuple.tuple;
import static com.mirakl.hybris.core.utils.mock.server.MiraklEndpointMatchers.of53;
import static com.mirakl.hybris.core.utils.mock.server.MiraklEndpointMatchers.of54;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.hybris.advancedpricing.product.strategies.impl.AbstractAdvancedPricingImportIntegrationTest;
import com.mirakl.hybris.advancedpricing.product.utils.OfferPricingKey;
import com.mirakl.hybris.core.enums.OfferState;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.model.OfferPricingModel;
import com.mirakl.hybris.core.product.jobs.MiraklImportOffersPollingJob;
import com.mirakl.hybris.core.utils.MiraklAppendSpringConfiguration;
import com.mirakl.hybris.core.utils.mock.server.MockServer;

import de.hybris.bootstrap.annotations.IntegrationTest;
import jersey.repackaged.com.google.common.collect.Lists;

@IntegrationTest
@MiraklAppendSpringConfiguration("classpath:/mirakladvancedpricingchannels/test/async/offer/test-spring-context.xml")
public class MiraklAdvancedPricingChannelsImportAsyncOffersIntegrationTest extends AbstractAdvancedPricingImportIntegrationTest {

  // Test constants
  private static final String MIRAKL_OF52_TRACKING_ID = "84288e71-f9b8-4ae6-b6f0-8389374f764b";

  // Test file paths
  private static final String SAMPLE_DATA_IMPEX_PATH = "/miraklservices/test/async/offer/testSampleData.impex";
  private static final String OF53_SUCCESS_1_URL_FILE_PATH = "/miraklservices/test/async/offer/OF53_success_1_url.json";
  private static final String OF54_1_OFFER_FILE_PATH =
      "/mirakladvancedpricingchannels/test/async/offer/OF54_1_offer_channels_and_advanced_pricing.json";

  private MockServer mockServer;

  @Resource
  private MiraklImportOffersPollingJob testMiraklImportOffersPollingJob;

  @Before
  public void setUp() throws Exception {
    importCsv(SAMPLE_DATA_IMPEX_PATH, "utf-8");
    mockServer = new MockServer();
    modelService.save(buildMiraklEnvironment(mockServer));
  }

  @Test
  public void offerAndPricingCreation() {
    // Arrange
    miraklAsyncTaskService.saveNewTask(MIRAKL_OF52_TRACKING_ID, OFFER_IMPORT_FULL, new Date());
    mockServer.returnsFile(of53(), OF53_SUCCESS_1_URL_FILE_PATH).returnsFile(of54(), OF54_1_OFFER_FILE_PATH);

    // Act
    testMiraklImportOffersPollingJob.perform(buildJobModel(true, true));

    // Assert
    OfferModel createdOffer = offerService.getOfferForId("12000");
    assertThat(createdOffer).isNotNull();
    assertThat(createdOffer.isActive()).isTrue();
    assertThat(createdOffer.isDeleted()).isFalse();
    assertThat(createdOffer.getShop()).isEqualTo(shopDao.findShopById("2000"));
    assertThat(createdOffer.getPrice()).isNull();
    assertThat(createdOffer.getCurrency()).isEqualTo(currencyService.getCurrencyForCode("USD"));
    assertThat(createdOffer.getQuantity()).isEqualTo(72);
    assertThat(createdOffer.getProductCode()).isEqualTo("1992696");
    assertThat(createdOffer.getDescription()).isEqualTo("Sample testing offer creation");
    assertThat(createdOffer.getMinShippingPrice()).isEqualByComparingTo(new BigDecimal("10.00"));
    assertThat(createdOffer.getMinShippingPriceAdditional()).isEqualByComparingTo(new BigDecimal("5.00"));
    assertThat(createdOffer.getAvailableStartDate()).isEqualTo(date("2022-02-07T16:16:26Z"));
    assertThat(createdOffer.getAvailableEndDate()).isEqualTo(date("2024-02-07T16:16:26Z"));
    assertThat(createdOffer.getOriginPrice()).isNull();
    assertThat(createdOffer.getTotalPrice()).isNull();
    assertThat(createdOffer.getDiscountStartDate()).isNull();
    assertThat(createdOffer.getDiscountEndDate()).isNull();
    assertThat(createdOffer.getDiscountPrice()).isNull();
    assertThat(createdOffer.getState()).isEqualTo(OfferState.valueOf("1"));
    assertThat(createdOffer.getFavoriteRank()).isEqualTo(12);
    assertThat(createdOffer.getLeadTimeToShip()).isEqualTo(4);
    assertThat(createdOffer.getMinOrderQuantity()).isEqualTo(10);
    assertThat(createdOffer.getMaxOrderQuantity()).isEqualTo(100);
    assertThat(createdOffer.getPackageQuantity()).isEqualTo(5);
    assertThat(createdOffer.getPriceAdditionalInfo()).isEqualTo("Don't miss this offer !");
    assertThat(createdOffer.getAllOfferPricingsJSON()).isNull();

    List<MiraklAdditionalFieldValue> additionalFields = offerService.loadOfferCustomFields(createdOffer);
    assertThatExtractedFields(additionalFields, "code", "value").containsExactly(tuple("BOOLEAN_FIELD", "false"),
        tuple("STRING_FIELD", "This value is a String"));

    List<OfferPricingModel> offerPricingList = offerPricingDao.findByOfferId(createdOffer.getId());
    assertThat(offerPricingList).hasSize(14);

    OfferPricingKey defaultPricingKey = newOfferPricingKey("12000");
    OfferPricingModel defaultPricing = getPricing(defaultPricingKey);


    verifyPriceDetails(defaultPricing, "200.00", null, null, null);

    List<OfferPricingModel> price120 = Lists.newArrayList( //
        getPricing(newOfferPricingKey("12000").channel("CHAN_2").customerGroup("GROUP_ID_1")),
        getPricing(newOfferPricingKey("12000").channel("CHAN_2").customerGroup("GROUP_ID_2")),
        getPricing(newOfferPricingKey("12000").channel("CHAN_2").customerOrg("ORG_ID_1")),
        getPricing(newOfferPricingKey("12000").channel("CHAN_3").customerGroup("GROUP_ID_1")),
        getPricing(newOfferPricingKey("12000").channel("CHAN_3").customerGroup("GROUP_ID_2")),
        getPricing(newOfferPricingKey("12000").channel("CHAN_3").customerOrg("ORG_ID_1")) //
    );
    verifyPricesDetails(price120, "120.00", null, null, null);

    List<OfferPricingModel> price150 = Lists.newArrayList( //
        getPricing(newOfferPricingKey("12000").channel("CHAN_1").customerOrg("ORG_ID_2")),
        getPricing(newOfferPricingKey("12000").channel("CHAN_3").customerOrg("ORG_ID_2")) //
    );
    verifyPricesDetails(price150, "150.00", null, null, null);

    List<OfferPricingModel> price160 = Lists.newArrayList( //
        getPricing(newOfferPricingKey("12000").channel("CHAN_1").customerGroup("GROUP_ID_3")),
        getPricing(newOfferPricingKey("12000").channel("CHAN_2").customerGroup("GROUP_ID_3")) //
    );
    verifyPricesDetails(price160, "160.00", null, null, null);

    List<OfferPricingModel> price180 = singletonList(getPricing(newOfferPricingKey("12000").channel("CHAN_1")));
    verifyPricesDetails(price180, "180.00", null, null, null);


    List<OfferPricingModel> price210 = singletonList(getPricing(newOfferPricingKey("12000").customerOrg("ORG_ID_2")));
    verifyPricesDetails(price210, "210.00", null, null, null);


    List<OfferPricingModel> price230 = singletonList(getPricing(newOfferPricingKey("12000").customerGroup("GROUP_ID_3")));
    verifyPricesDetails(price230, "230.00", null, null, null);
  }

  @After
  public void tearDown() {
    mockServer.stop();
  }

}
