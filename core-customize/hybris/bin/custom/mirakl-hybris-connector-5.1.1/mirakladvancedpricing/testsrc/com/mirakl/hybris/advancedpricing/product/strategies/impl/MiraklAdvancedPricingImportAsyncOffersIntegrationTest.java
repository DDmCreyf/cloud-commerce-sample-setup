package com.mirakl.hybris.advancedpricing.product.strategies.impl;

import static com.mirakl.hybris.advancedpricing.product.utils.OfferPricingKey.newOfferPricingKey;
import static com.mirakl.hybris.core.enums.MiraklAsyncTaskType.OFFER_IMPORT_DELTA;
import static com.mirakl.hybris.core.enums.MiraklAsyncTaskType.OFFER_IMPORT_FULL;
import static com.mirakl.hybris.core.utils.assertions.MiraklAssertions.assertThatExtractedFields;
import static com.mirakl.hybris.core.utils.assertions.Tuple.tuple;
import static com.mirakl.hybris.core.utils.mock.server.MiraklEndpointMatchers.of53;
import static com.mirakl.hybris.core.utils.mock.server.MiraklEndpointMatchers.of54;
import static org.fest.assertions.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.hybris.advancedpricing.product.daos.OfferPricingDao;
import com.mirakl.hybris.advancedpricing.product.utils.OfferPricingKey;
import com.mirakl.hybris.core.enums.OfferState;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.model.OfferPricingModel;
import com.mirakl.hybris.core.product.daos.OfferDao;
import com.mirakl.hybris.core.product.jobs.MiraklImportOffersPollingJob;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;
import com.mirakl.hybris.core.utils.MiraklAppendSpringConfiguration;
import com.mirakl.hybris.core.utils.mock.server.MockServer;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.search.SearchResult;

@IntegrationTest
@MiraklAppendSpringConfiguration("classpath:/mirakladvancedpricing/test/async/offer/test-spring-context.xml")
public class MiraklAdvancedPricingImportAsyncOffersIntegrationTest extends AbstractAdvancedPricingImportIntegrationTest {

  // Test constants
  private static final String MIRAKL_OF52_TRACKING_ID = "84288e71-f9b8-4ae6-b6f0-8389374f764b";

  // Test file paths
  private static final String SAMPLE_DATA_IMPEX_PATH = "/miraklservices/test/async/offer/testSampleData.impex";
  private static final String OFFER_WITH_DUPLICATE_PRICINGS_IMPEX_PATH =
      "/mirakladvancedpricing/test/async/offer/testOfferAndPricingUpdateWithDuplicates.impex";
  private static final String EXISTING_OFFER_AND_PRICINGS_TO_DELETE_IMPEX_PATH =
      "/mirakladvancedpricing/test/async/offer/testOfferAndPricingDeletion.impex";
  private static final String EXISTING_OFFER_AND_PRICINGS_TO_DELETE_DELTA_IMPEX_PATH =
      "/mirakladvancedpricing/test/async/offer/testOfferAndPricingDeletionForDeltaImport.impex";
  private static final String OFFER_WITH_EXISTING_PRICINGS_IMPEX_PATH =
      "/mirakladvancedpricing/test/async/offer/testOfferAndPricingUpdate.impex";
  private static final String OF53_SUCCESS_1_URL_FILE_PATH = "/miraklservices/test/async/offer/OF53_success_1_url.json";
  private static final String OF54_1_OFFER_FILE_PATH = "/mirakladvancedpricing/test/async/offer/OF54_1_offer.json";
  private static final String OF54_1_OFFER_MULTIPLE_CO_CG_FILE_PATH =
      "/mirakladvancedpricing/test/async/offer/OF54_1_offer_multiple_co_and_cg.json";
  private static final String OF54_DELTA_WITH_DELETION_FILE_PATH =
      "/mirakladvancedpricing/test/async/offer/OF54_delta_with_deletion.json";

  private MockServer mockServer;

  @Resource
  private OfferDao offerDao;
  @Resource
  private OfferPricingDao offerPricingDao;
  @Resource
  private JsonMarshallingService jsonMarshallingService;

  @Resource
  private MiraklImportOffersPollingJob testMiraklImportOffersPollingJob;

  @Before
  public void setUp() throws Exception {
    importCsv(SAMPLE_DATA_IMPEX_PATH, "utf-8");
    mockServer = initMockServer();
  }

  @Test
  public void offerAndPricingCreation() {
    // Arrange
    miraklAsyncTaskService.saveNewTask(MIRAKL_OF52_TRACKING_ID, OFFER_IMPORT_FULL, new Date());
    mockServer.returnsFile(of53(), OF53_SUCCESS_1_URL_FILE_PATH).returnsFile(of54(), OF54_1_OFFER_FILE_PATH);

    // Act
    testMiraklImportOffersPollingJob.perform(buildJobModel(true, true));

    // Assert
    OfferModel createdOffer = offerService.getOfferForId("10000");
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
    assertThatExtractedFields(additionalFields, "code", "value") //
        .containsExactly(tuple("BOOLEAN_FIELD", "false"), tuple("STRING_FIELD", "This value is a String"));

    List<OfferPricingModel> offerPricingList = offerPricingDao.findByOfferId(createdOffer.getId());
    assertThat(offerPricingList).hasSize(6);

    OfferPricingKey defaultPricingKey = newOfferPricingKey("10000");
    OfferPricingModel defaultPricing = getPricing(defaultPricingKey);


    verifyPriceDetails(defaultPricing, "921", "18.50", date("2021-01-07T16:16:26Z"), date("2021-05-07T16:16:26Z"), //
        tuple(new BigDecimal("921.00"), new BigDecimal("921.00"), null, 1), //
        tuple(new BigDecimal("37.00"), new BigDecimal("37.00"), new BigDecimal("18.50"), 10) //
    );

    OfferPricingKey group1PricingKey =
        newOfferPricingKey("10000").customerGroup("GROUP_ID_1").startDate("2022-02-07T16:16:26Z").endDate("2027-02-07T16:16:26Z");
    OfferPricingModel group1Pricing = getPricing(group1PricingKey);
    verifyPriceDetails(group1Pricing, "30.00", null, null, null);

    OfferPricingKey group2PricingKey =
        newOfferPricingKey("10000").customerGroup("GROUP_ID_2").startDate("2022-02-07T16:16:26Z").endDate("2027-02-07T16:16:26Z");
    OfferPricingModel group2Pricing = getPricing(group2PricingKey);
    verifyPriceDetails(group2Pricing, "30.00", null, null, null);

    OfferPricingKey group3PricingKey =
        newOfferPricingKey("10000").customerGroup("GROUP_ID_3").startDate("2022-02-07T16:16:26Z").endDate("2027-02-07T16:16:26Z");
    OfferPricingModel group3Pricing = getPricing(group3PricingKey);
    verifyPriceDetails(group3Pricing, "30.00", null, null, null);

    OfferPricingKey org1PricingKey =
        newOfferPricingKey("10000").customerOrg("ORG_ID_1").startDate("2022-02-07T16:16:26Z").endDate("2027-02-07T16:16:26Z");
    OfferPricingModel org1Pricing = getPricing(org1PricingKey);
    verifyPriceDetails(org1Pricing, "30.00", null, null, null);

    OfferPricingKey org2PricingKey =
        newOfferPricingKey("10000").customerOrg("ORG_ID_2").startDate("2022-02-07T16:16:26Z").endDate("2027-02-07T16:16:26Z");
    OfferPricingModel org2Pricing = getPricing(org2PricingKey);
    verifyPriceDetails(org2Pricing, "30.00", null, null, null);
  }

  @Test
  public void offerAndPricingCreationWithMultipleCoAndCg() {
    // Arrange
    miraklAsyncTaskService.saveNewTask(MIRAKL_OF52_TRACKING_ID, OFFER_IMPORT_FULL, new Date());
    mockServer.returnsFile(of53(), OF53_SUCCESS_1_URL_FILE_PATH).returnsFile(of54(), OF54_1_OFFER_MULTIPLE_CO_CG_FILE_PATH);

    // Act
    testMiraklImportOffersPollingJob.perform(buildJobModel(true, true));

    // Assert
    OfferModel createdOffer = offerService.getOfferForId("12000");
    assertThat(createdOffer).isNotNull();
    assertThat(createdOffer.isActive()).isTrue();
    assertThat(createdOffer.isDeleted()).isFalse();

    List<OfferPricingModel> offerPricingList = offerPricingDao.findByOfferId(createdOffer.getId());
    assertThat(offerPricingList).hasSize(6);

    OfferPricingKey defaultPricingKey = newOfferPricingKey("12000");
    OfferPricingModel defaultPricing = getPricing(defaultPricingKey);


    verifyPriceDetails(defaultPricing, "200.00", null, null, null);

    OfferPricingKey group1PricingKey = newOfferPricingKey("12000").customerGroup("GROUP_ID_1");
    OfferPricingModel group1Pricing = getPricing(group1PricingKey);
    verifyPriceDetails(group1Pricing, "120.00", null, null, null);

    OfferPricingKey group2PricingKey = newOfferPricingKey("12000").customerGroup("GROUP_ID_2");
    OfferPricingModel group2Pricing = getPricing(group2PricingKey);
    verifyPriceDetails(group2Pricing, "120.00", null, null, null);

    OfferPricingKey group3PricingKey = newOfferPricingKey("12000").customerGroup("GROUP_ID_3");
    OfferPricingModel group3Pricing = getPricing(group3PricingKey);
    verifyPriceDetails(group3Pricing, "160.00", null, null, null);

    OfferPricingKey org1PricingKey = newOfferPricingKey("12000").customerOrg("ORG_ID_1");
    OfferPricingModel org1Pricing = getPricing(org1PricingKey);
    verifyPriceDetails(org1Pricing, "120.00", null, null, null);

    OfferPricingKey org2PricingKey = newOfferPricingKey("12000").customerOrg("ORG_ID_2");
    OfferPricingModel org2Pricing = getPricing(org2PricingKey);
    verifyPriceDetails(org2Pricing, "150.00", null, null, null);
  }

  @Test
  public void offerAndPricingUpdate() throws ImpExException {
    // Arrange
    importCsv(OFFER_WITH_EXISTING_PRICINGS_IMPEX_PATH, "utf-8");
    miraklAsyncTaskService.saveNewTask(MIRAKL_OF52_TRACKING_ID, OFFER_IMPORT_FULL, new Date());
    mockServer.returnsFile(of53(), OF53_SUCCESS_1_URL_FILE_PATH).returnsFile(of54(), OF54_1_OFFER_FILE_PATH);

    // Act
    testMiraklImportOffersPollingJob.perform(buildJobModel(true, true));

    // Assert
    List<OfferPricingModel> offerPricingList = offerPricingDao.findByOfferId("10000");
    assertThat(offerPricingList).hasSize(6);

    OfferPricingKey defaultPricingKey = newOfferPricingKey("10000");
    OfferPricingModel defaultPricing = getPricing(defaultPricingKey);
    verifyPriceDetails(defaultPricing, "921", "18.50", date("2021-01-07T16:16:26Z"), date("2021-05-07T16:16:26Z"), //
        tuple(new BigDecimal("921.00"), new BigDecimal("921.00"), null, 1), //
        tuple(new BigDecimal("37.00"), new BigDecimal("37.00"), new BigDecimal("18.50"), 10) //
    );

    OfferPricingKey group1PricingKey =
        newOfferPricingKey("10000").customerGroup("GROUP_ID_1").startDate("2022-02-07T16:16:26Z").endDate("2027-02-07T16:16:26Z");
    OfferPricingModel group1Pricing = getPricing(group1PricingKey);
    verifyPriceDetails(group1Pricing, "30.00", null, null, null);

    OfferPricingKey group2PricingKey =
        newOfferPricingKey("10000").customerGroup("GROUP_ID_2").startDate("2022-02-07T16:16:26Z").endDate("2027-02-07T16:16:26Z");
    OfferPricingModel group2Pricing = getPricing(group2PricingKey);
    verifyPriceDetails(group2Pricing, "30.00", null, null, null);

    OfferPricingKey group3PricingKey =
        newOfferPricingKey("10000").customerGroup("GROUP_ID_3").startDate("2022-02-07T16:16:26Z").endDate("2027-02-07T16:16:26Z");
    OfferPricingModel group3Pricing = getPricing(group3PricingKey);
    verifyPriceDetails(group3Pricing, "30.00", null, null, null);

    OfferPricingKey org1PricingKey =
        newOfferPricingKey("10000").customerOrg("ORG_ID_1").startDate("2022-02-07T16:16:26Z").endDate("2027-02-07T16:16:26Z");
    OfferPricingModel org1Pricing = getPricing(org1PricingKey);
    verifyPriceDetails(org1Pricing, "30.00", null, null, null);

    OfferPricingKey org2PricingKey =
        newOfferPricingKey("10000").customerOrg("ORG_ID_2").startDate("2022-02-07T16:16:26Z").endDate("2027-02-07T16:16:26Z");
    OfferPricingModel org2Pricing = getPricing(org2PricingKey);
    verifyPriceDetails(org2Pricing, "30.00", null, null, null);
  }

  @Test
  public void offerAndPricingUpdateWithDuplicates() throws ImpExException {
    // Arrange
    importCsv(OFFER_WITH_DUPLICATE_PRICINGS_IMPEX_PATH, "utf-8");
    miraklAsyncTaskService.saveNewTask(MIRAKL_OF52_TRACKING_ID, OFFER_IMPORT_FULL, new Date());
    mockServer.returnsFile(of53(), OF53_SUCCESS_1_URL_FILE_PATH).returnsFile(of54(), OF54_1_OFFER_FILE_PATH);

    // Act
    testMiraklImportOffersPollingJob.perform(buildJobModel(true, true));

    // Assert
    List<OfferPricingModel> offerPricingList = offerPricingDao.findByOfferId("10000");
    assertThat(offerPricingList).hasSize(6);

    OfferPricingKey defaultPricingKey = newOfferPricingKey("10000");
    OfferPricingModel defaultPricing = getPricing(defaultPricingKey);
    verifyPriceDetails(defaultPricing, "921", "18.50", date("2021-01-07T16:16:26Z"), date("2021-05-07T16:16:26Z"), //
        tuple(new BigDecimal("921.00"), new BigDecimal("921.00"), null, 1), //
        tuple(new BigDecimal("37.00"), new BigDecimal("37.00"), new BigDecimal("18.50"), 10) //
    );
  }

  @Test
  public void offerAndPricingDeletionForFullImport() throws ImpExException {
    // Arrange
    importCsv(EXISTING_OFFER_AND_PRICINGS_TO_DELETE_IMPEX_PATH, "utf-8");
    miraklAsyncTaskService.saveNewTask(MIRAKL_OF52_TRACKING_ID, OFFER_IMPORT_FULL, new Date());
    mockServer.returnsFile(of53(), OF53_SUCCESS_1_URL_FILE_PATH).returnsFile(of54(), OF54_1_OFFER_FILE_PATH);

    // Act
    testMiraklImportOffersPollingJob.perform(buildJobModel(true, true));

    // Assert
    OfferModel deletedOffer = offerDao.findOfferById("12000");
    assertThat(deletedOffer.isDeleted()).isTrue();
    assertThat(deletedOffer.isActive()).isFalse();
    assertThat(offerPricingDao.findByOfferId(deletedOffer.getId())).isEmpty();
  }

  @Test
  public void offerAndPricingDeletionForDeltaImport() throws ImpExException {
    // Arrange
    importCsv(EXISTING_OFFER_AND_PRICINGS_TO_DELETE_DELTA_IMPEX_PATH, "utf-8");
    miraklAsyncTaskService.saveNewTask(MIRAKL_OF52_TRACKING_ID, OFFER_IMPORT_DELTA, new Date());
    mockServer.returnsFile(of53(), OF53_SUCCESS_1_URL_FILE_PATH).returnsFile(of54(), OF54_DELTA_WITH_DELETION_FILE_PATH);
    Object[] offersToDelete = {"10000", "10001", "10002"};
    Object[] offersToKeep = {"10003", "10004", "10005"};

    // Act
    testMiraklImportOffersPollingJob.perform(buildJobModel(false, true));

    // Assert
    SearchResult<OfferModel> searchDeleted = flexibleSearchService.search(ALL_DELETED_OFFERS_QUERY);
    assertThat(searchDeleted.getResult()).onProperty("id").containsOnly(offersToDelete);
    for (Object offerId : offersToDelete) {
      assertThat(offerPricingDao.findByOfferId((String) offerId)).isEmpty();
    }

    SearchResult<OfferModel> searchActive = flexibleSearchService.search(ALL_ACTIVE_OFFERS_QUERY);
    assertThat(searchActive.getResult()).onProperty("id").containsOnly(offersToKeep);
    for (Object offerId : offersToKeep) {
      assertThat(offerPricingDao.findByOfferId((String) offerId)).isNotEmpty();
    }
  }

  @After
  public void tearDown() {
    mockServer.stop();
  }

}
