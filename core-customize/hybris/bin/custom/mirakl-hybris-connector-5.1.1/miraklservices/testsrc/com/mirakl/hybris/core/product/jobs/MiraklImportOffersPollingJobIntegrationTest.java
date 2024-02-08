package com.mirakl.hybris.core.product.jobs;

import com.mirakl.client.mmp.domain.common.MiraklAdditionalFieldValue;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.beans.OfferVolumePricingData;
import com.mirakl.hybris.core.enums.MiraklAsyncTaskStatus;
import com.mirakl.hybris.core.enums.OfferState;
import com.mirakl.hybris.core.model.MiraklAsyncTaskModel;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;
import com.mirakl.hybris.core.utils.MiraklAppendSpringConfiguration;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.junit.Before;
import org.junit.Test;
import shaded.com.fasterxml.jackson.core.type.TypeReference;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.mirakl.hybris.core.enums.MiraklAsyncTaskType.OFFER_IMPORT_DELTA;
import static com.mirakl.hybris.core.enums.MiraklAsyncTaskType.OFFER_IMPORT_FULL;
import static com.mirakl.hybris.core.utils.assertions.MiraklAssertions.assertThatExtractedFields;
import static com.mirakl.hybris.core.utils.assertions.Tuple.tuple;
import static com.mirakl.hybris.core.utils.mock.server.MiraklEndpointMatchers.of53;
import static com.mirakl.hybris.core.utils.mock.server.MiraklEndpointMatchers.of54;
import static org.fest.assertions.Assertions.assertThat;

@IntegrationTest
@MiraklAppendSpringConfiguration("classpath:/miraklservices/test/async/offer/test-spring-context.xml")
public class MiraklImportOffersPollingJobIntegrationTest extends AbstractImportAsyncOffersIntegrationTest {

  // Test constants
  private static final String MIRAKL_OF52_TRACKING_ID = "84288e71-f9b8-4ae6-b6f0-8389374f764b";

  // Test files paths
  private static final String SAMPLE_DATA_IMPEX_PATH = "/miraklservices/test/async/offer/testSampleData.impex";
  private static final String OF53_SUCCESS_1_URL_FILE_PATH = "/miraklservices/test/async/offer/OF53_success_1_url.json";
  private static final String OF53_SUCCESS_3_URLS_FILE_PATH = "/miraklservices/test/async/offer/OF53_success_3_urls.json";
  private static final String OF53_FAILED_FILE_PATH = "/miraklservices/test/async/offer/OF53_failed.json";
  private static final String OF53_PENDING_FILE_PATH = "/miraklservices/test/async/offer/OF53_pending.json";
  private static final String OF54_MALFORMED_FILE_PATH = "/miraklservices/test/async/offer/OF54_malformed.json";
  private static final String OF54_1_OFFER_FILE_PATH = "/miraklservices/test/async/offer/OF54_1_offer.json";
  private static final String OF54_600_OFFERS_FILE_0_PATH = "/miraklservices/test/async/offer/OF54_600_offers_file_0.json";
  private static final String OF54_600_OFFERS_FILE_1_PATH = "/miraklservices/test/async/offer/OF54_600_offers_file_1.json";
  private static final String OF54_472_OFFERS_FILE_2_PATH = "/miraklservices/test/async/offer/OF54_472_offers_file_2.json";
  private static final String OF54_DELTA_WITH_DELETIONS_PATH =
      "/miraklservices/test/async/offer/OF54_10_offers_with_deletions.json";

  // Flexible search queries
  private static final String ALL_ACTIVE_OFFERS_QUERY =
      "SELECT {o.PK} FROM {Offer AS o} WHERE {o.deleted} = false AND {o.active} = true";
  private static final String ALL_DELETED_OFFERS_QUERY =
      "SELECT {o.PK} FROM {Offer AS o} WHERE {o.deleted} = true AND {o.active} = false";



  @Resource
  private JsonMarshallingService jsonMarshallingService;
  @Resource
  private FlexibleSearchService flexibleSearchService;

  @Resource
  private MiraklImportOffersPollingJob testMiraklImportOffersPollingJob;

  @Before
  public void setUp() throws Exception {
    importCsv(SAMPLE_DATA_IMPEX_PATH, "utf-8");
    mockServer = initMockServer();
  }

  @Test
  public void taskStillPending() {
    // Arrange
    MiraklAsyncTaskModel task = miraklAsyncTaskService.saveNewTask(MIRAKL_OF52_TRACKING_ID, OFFER_IMPORT_FULL, new Date());
    mockServer.returnsFile(of53(), OF53_PENDING_FILE_PATH);

    // Act
    testMiraklImportOffersPollingJob.perform(buildJobModel(true, true));

    // Assert
    List<MiraklAsyncTaskModel> tasksToProcess = miraklAsyncTaskService.findTasksToProcess(OFFER_IMPORT_FULL);
    assertThat(tasksToProcess).containsExactly(task);
    assertThat(task.getStatus()).isEqualTo(MiraklAsyncTaskStatus.PENDING);
    assertThat(schedulerJobModel.getLastImportTime()).isEqualTo(DEFAULT_LAST_IMPORT_TIME);
  }

  @Test
  public void taskFailedWithCleanup() {
    // Arrange
    miraklAsyncTaskService.saveNewTask(MIRAKL_OF52_TRACKING_ID, OFFER_IMPORT_FULL, new Date());
    mockServer.returnsFile(of53(), OF53_FAILED_FILE_PATH);

    // Act
    testMiraklImportOffersPollingJob.perform(buildJobModel(true, true));

    // Assert
    List<MiraklAsyncTaskModel> allTasks = miraklAsyncTaskDao.find();
    assertThat(allTasks).isEmpty();
  }

  @Test
  public void taskFailedWithoutCleanup() {
    // Arrange
    miraklAsyncTaskService.saveNewTask(MIRAKL_OF52_TRACKING_ID, OFFER_IMPORT_FULL, new Date());
    mockServer.returnsFile(of53(), OF53_FAILED_FILE_PATH);

    // Act
    testMiraklImportOffersPollingJob.perform(buildJobModel(true, false));

    // Assert
    List<MiraklAsyncTaskModel> allTasks = miraklAsyncTaskDao.find();
    assertThat(allTasks).hasSize(1);
    assertThat(allTasks.get(0).getTrackingId()).isEqualTo(MIRAKL_OF52_TRACKING_ID);
    assertThat(allTasks.get(0).getStatus()).isEqualTo(MiraklAsyncTaskStatus.ERROR);
    assertThat(schedulerJobModel.getLastImportTime()).isEqualTo(DEFAULT_LAST_IMPORT_TIME);
  }

  @Test
  public void taskFailedDueToInternalErrorWithoutCleanup() {
    // Arrange
    miraklAsyncTaskService.saveNewTask(MIRAKL_OF52_TRACKING_ID, OFFER_IMPORT_FULL, new Date());
    mockServer.returnsFile(of53(), OF53_SUCCESS_1_URL_FILE_PATH).returnsFile(of54(), OF54_MALFORMED_FILE_PATH);

    // Act
    testMiraklImportOffersPollingJob.perform(buildJobModel(true, false));

    // Assert
    List<MiraklAsyncTaskModel> allTasks = miraklAsyncTaskDao.find();
    assertThat(allTasks).hasSize(1);
    assertThat(allTasks.get(0).getTrackingId()).isEqualTo(MIRAKL_OF52_TRACKING_ID);
    assertThat(allTasks.get(0).getStatus()).isEqualTo(MiraklAsyncTaskStatus.ERROR);
    assertThat(schedulerJobModel.getLastImportTime()).isEqualTo(DEFAULT_LAST_IMPORT_TIME);
  }

  @Test
  public void taskSucceededWithCleanup() {
    // Arrange
    miraklAsyncTaskService.saveNewTask(MIRAKL_OF52_TRACKING_ID, OFFER_IMPORT_FULL, new Date());
    mockServer.returnsFile(of53(), OF53_SUCCESS_1_URL_FILE_PATH).returnsFile(of54(), OF54_1_OFFER_FILE_PATH);

    // Act
    testMiraklImportOffersPollingJob.perform(buildJobModel(true, true));

    // Assert
    List<MiraklAsyncTaskModel> allTasks = miraklAsyncTaskDao.find();
    assertThat(allTasks).isEmpty();
  }

  @Test
  public void taskSucceededWithoutCleanup() {
    // Arrange
    miraklAsyncTaskService.saveNewTask(MIRAKL_OF52_TRACKING_ID, OFFER_IMPORT_FULL, new Date());
    mockServer.returnsFile(of53(), OF53_SUCCESS_1_URL_FILE_PATH).returnsFile(of54(), OF54_1_OFFER_FILE_PATH);

    // Act
    testMiraklImportOffersPollingJob.perform(buildJobModel(true, false));

    // Assert
    List<MiraklAsyncTaskModel> allTasks = miraklAsyncTaskDao.find();
    assertThat(allTasks).hasSize(1);
    assertThat(allTasks.get(0).getTrackingId()).isEqualTo(MIRAKL_OF52_TRACKING_ID);
    assertThat(allTasks.get(0).getStatus()).isEqualTo(MiraklAsyncTaskStatus.DONE);
    assertThat(schedulerJobModel.getLastImportTime().getTime()).isGreaterThan(DEFAULT_LAST_IMPORT_TIME.getTime());
  }

  @Test
  public void offerCreation() {
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
    assertThat(createdOffer.getPrice()).isEqualByComparingTo(new BigDecimal("921"));
    assertThat(createdOffer.getCurrency()).isEqualTo(currencyService.getCurrencyForCode("USD"));
    assertThat(createdOffer.getQuantity()).isEqualTo(72);
    assertThat(createdOffer.getProductCode()).isEqualTo("1992696");
    assertThat(createdOffer.getDescription()).isEqualTo("Sample testing offer creation");
    assertThat(createdOffer.getMinShippingPrice()).isEqualByComparingTo(new BigDecimal("10.00"));
    assertThat(createdOffer.getMinShippingPriceAdditional()).isEqualByComparingTo(new BigDecimal("5.00"));
    assertThat(createdOffer.getAvailableStartDate()).isEqualTo(date("2022-02-07T16:16:26Z"));
    assertThat(createdOffer.getAvailableEndDate()).isEqualTo(date("2024-02-07T16:16:26Z"));
    assertThat(createdOffer.getOriginPrice()).isEqualByComparingTo(new BigDecimal("921.00"));
    assertThat(createdOffer.getTotalPrice()).isEqualByComparingTo(new BigDecimal("931.00"));
    assertThat(createdOffer.getDiscountStartDate()).isEqualTo(date("2021-01-07T16:16:26Z"));
    assertThat(createdOffer.getDiscountEndDate()).isEqualTo(date("2021-05-07T16:16:26Z"));
    assertThat(createdOffer.getDiscountPrice()).isEqualByComparingTo(new BigDecimal("18.50"));
    assertThat(createdOffer.getState()).isEqualTo(OfferState.valueOf("1"));
    assertThat(createdOffer.getFavoriteRank()).isEqualTo(12);
    assertThat(createdOffer.getLeadTimeToShip()).isEqualTo(4);
    assertThat(createdOffer.getMinOrderQuantity()).isEqualTo(10);
    assertThat(createdOffer.getMaxOrderQuantity()).isEqualTo(100);
    assertThat(createdOffer.getPackageQuantity()).isEqualTo(5);
    assertThat(createdOffer.getPriceAdditionalInfo()).isEqualTo("Don't miss this offer !");

    List<MiraklAdditionalFieldValue> additionalFields = offerService.loadOfferCustomFields(createdOffer);
    assertThatExtractedFields(additionalFields, "code", "value") //
        .containsExactly(tuple("BOOLEAN_FIELD", "false"), tuple("STRING_FIELD", "This value is a String"));

    List<OfferPricingData> pricingList =
        jsonMarshallingService.fromJson(createdOffer.getAllOfferPricingsJSON(), new TypeReference<List<OfferPricingData>>() {});
    assertThat(pricingList).hasSize(1);

    OfferPricingData defaultPricing = pricingList.get(0);
    assertThat(defaultPricing.getPrice()).isEqualByComparingTo(createdOffer.getPrice());
    assertThat(defaultPricing.getDiscountStartDate()).isEqualTo(createdOffer.getDiscountStartDate());
    assertThat(defaultPricing.getDiscountEndDate()).isEqualTo(createdOffer.getDiscountEndDate());
    assertThat(defaultPricing.getChannelCode()).isNull();
    assertThat(defaultPricing.getUnitDiscountPrice()).isEqualByComparingTo(createdOffer.getDiscountPrice());
    assertThat(defaultPricing.getUnitOriginPrice()).isEqualByComparingTo(createdOffer.getOriginPrice());

    List<OfferVolumePricingData> volumePrices = defaultPricing.getVolumePrices();
    assertThat(volumePrices).hasSize(2);
    assertThatExtractedFields(volumePrices, "price", "unitOriginPrice", "unitDiscountPrice", "quantityThreshold") //
        .containsExactly( //
            tuple(new BigDecimal("921.00"), new BigDecimal("921.00"), null, 1), //
            tuple(new BigDecimal("37.00"), new BigDecimal("37.00"), new BigDecimal("18.50"), 10) //
        );
  }

  @Test
  public void offerUpdate() {
    // Arrange
    miraklAsyncTaskService.saveNewTask(MIRAKL_OF52_TRACKING_ID, OFFER_IMPORT_FULL, new Date());
    mockServer.returnsFile(of53(), OF53_SUCCESS_1_URL_FILE_PATH).returnsFile(of54(), OF54_1_OFFER_FILE_PATH);
    String offerId = saveSampleOffer("10000");

    // Act
    testMiraklImportOffersPollingJob.perform(buildJobModel(true, true));

    // Assert
    OfferModel createdOffer = offerService.getOfferForId(offerId);
    assertThat(createdOffer).isNotNull();
    assertThat(createdOffer.isActive()).isTrue();
    assertThat(createdOffer.isDeleted()).isFalse();
    assertThat(createdOffer.getQuantity()).isEqualTo(72);
    assertThat(createdOffer.getTotalPrice()).isEqualByComparingTo(new BigDecimal("931.00"));
  }

  @Test
  public void fullImportOffersDeletion() {
    // Arrange
    miraklAsyncTaskService.saveNewTask(MIRAKL_OF52_TRACKING_ID, OFFER_IMPORT_FULL, new Date());
    mockServer.returnsFile(of53(), OF53_SUCCESS_1_URL_FILE_PATH).returnsFile(of54(), OF54_1_OFFER_FILE_PATH);
    Object[] offersToDelete = new String[] {saveSampleOffer("2012"), saveSampleOffer("2100"), saveSampleOffer("2110")};

    // Act
    testMiraklImportOffersPollingJob.perform(buildJobModel(true, true));

    // Assert
    SearchResult<OfferModel> searchDeleted = flexibleSearchService.search(ALL_DELETED_OFFERS_QUERY);
    List<OfferModel> deletedOffers = searchDeleted.getResult();
    assertThat(deletedOffers).onProperty("id").containsOnly(offersToDelete);
  }

  @Test
  public void fullImport3FilesWith1672Offers() {
    // Arrange
    miraklAsyncTaskService.saveNewTask(MIRAKL_OF52_TRACKING_ID, OFFER_IMPORT_FULL, new Date());
    mockServer.returnsFile(of53(), OF53_SUCCESS_3_URLS_FILE_PATH).returnsFile(of54("file", "0.json"), OF54_600_OFFERS_FILE_0_PATH)
        .returnsFile(of54("file", "1.json"), OF54_600_OFFERS_FILE_1_PATH)
        .returnsFile(of54("file", "2.json"), OF54_472_OFFERS_FILE_2_PATH);
    Object[] offersToUpdate = new String[] { //
        saveSampleOffer("2012"), //
        saveSampleOffer("2100"), //
        saveSampleOffer("2110"), //
        saveSampleOffer("3075"), //
        saveSampleOffer("3455"), //
    };
    Object[] offersToDelete = new String[] { //
        saveSampleOffer("22000"), //
        saveSampleOffer("22001"), //
        saveSampleOffer("22002"), //
        saveSampleOffer("22003"), //
        saveSampleOffer("22004"), //
        saveSampleOffer("22005"), //
    };

    // Act
    testMiraklImportOffersPollingJob.perform(buildJobModel(true, true));

    // Assert
    SearchResult<OfferModel> searchImported = flexibleSearchService.search(ALL_ACTIVE_OFFERS_QUERY);
    List<OfferModel> importedOffers = searchImported.getResult();
    assertThat(importedOffers.size()).isEqualTo(1672);
    assertThat(importedOffers).onProperty("id").contains(offersToUpdate);
    assertThat(importedOffers).onProperty("id").excludes(offersToDelete);

    SearchResult<OfferModel> searchDeleted = flexibleSearchService.search(ALL_DELETED_OFFERS_QUERY);
    List<OfferModel> deletedOffers = searchDeleted.getResult();
    assertThat(deletedOffers).onProperty("id").containsOnly(offersToDelete);
  }

  @Test
  public void deltaImportOffersWithDeletions() {
    // Arrange
    miraklAsyncTaskService.saveNewTask(MIRAKL_OF52_TRACKING_ID, OFFER_IMPORT_DELTA, new Date());
    mockServer.returnsFile(of53(), OF53_SUCCESS_1_URL_FILE_PATH).returnsFile(of54(), OF54_DELTA_WITH_DELETIONS_PATH);
    Object[] offersToDelete = new String[] { //
        saveSampleOffer("3099"), //
        saveSampleOffer("2951"), //
        saveSampleOffer("2956"), //
        saveSampleOffer("2960"), //
        saveSampleOffer("2971"), //
    };
    Object[] offersToKeep = new String[] {saveSampleOffer("2012"), saveSampleOffer("2100"), saveSampleOffer("2110")};

    // Act
    testMiraklImportOffersPollingJob.perform(buildJobModel(false, true));

    // Assert
    SearchResult<OfferModel> searchImported = flexibleSearchService.search(ALL_ACTIVE_OFFERS_QUERY);
    List<OfferModel> activeOffers = searchImported.getResult();
    assertThat(activeOffers.size()).isEqualTo(8);
    assertThat(activeOffers).onProperty("id").contains(offersToKeep);
    assertThat(activeOffers).onProperty("id").excludes(offersToDelete);

    SearchResult<OfferModel> searchDeleted = flexibleSearchService.search(ALL_DELETED_OFFERS_QUERY);
    List<OfferModel> deletedOffers = searchDeleted.getResult();
    assertThat(deletedOffers).onProperty("id").containsOnly(offersToDelete);
  }

}
