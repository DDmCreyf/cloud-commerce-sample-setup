package com.mirakl.hybris.advancedpricing.product.daos.impl;

import static com.mirakl.hybris.advancedpricing.product.utils.OfferPricingCriteriaBuilder.offerPricingCriteria;
import static org.fest.assertions.Assertions.assertThat;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.mirakl.hybris.advancedpricing.product.daos.OfferPricingDao;
import com.mirakl.hybris.beans.OfferPricingCriteria;
import com.mirakl.hybris.core.model.OfferPricingModel;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;

@IntegrationTest
public class DefaultOfferPricingDaoIntegrationTest extends ServicelayerTest {

  private static final String OFFER_ID_1 = "1";
  private static final String CHANNEL_1 = "CHAN1";
  private static final String CUSTOMER_ORGANIZATION_ID = "2000";
  private static final String CUSTOMER_GROUP_ID = "1000";
  private static final Date OUT_OF_RANGE_DATE = new DateTime(2051, 2, 23, 8, 0).toDate();
  private static final Date DATE_IN_RANGE = new DateTime(2051, 2, 8, 8, 0).toDate();
  private static final Date DATE_BEFORE_RANGE = new DateTime(2051, 1, 1, 8, 0).toDate();
  private static final String REGEX = "^.*\"price\":\\s*([^,]*),.*$";
  private static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.MULTILINE);
  @Resource
  private OfferPricingDao offerPricingDao;

  @Before
  public void setUp() throws ImpExException {
    importCsv("/mirakladvancedpricing/test/testOfferPricings.impex", "utf-8");
  }

  @Test
  public void findByOfferId() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .build());

    assertResults(offerPricings, "10", "90");
  }

  @Test
  public void findByCustomerGroupId() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withCustomerGroupId(CUSTOMER_GROUP_ID) //
            .build());

    assertResults(offerPricings, "10", "20", "90", "100");
  }

  @Test
  public void findByCustomerOrg() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_ID) //
            .build());

    assertResults(offerPricings, "10", "30", "90", "110");
  }

  @Test
  public void findByCustomerOrgAndCustomerGroup() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_ID) //
            .withCustomerGroupId(CUSTOMER_GROUP_ID) //
            .build());

    assertResults(offerPricings, "10", "20", "30", "90", "100", "110");
  }

  @Test
  public void findByChannel() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withChannelCode(CHANNEL_1) //
            .build());

    assertResults(offerPricings, "10", "50", "90", "130");
  }

  @Test
  public void findByChannelAndCustomerGroup() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withChannelCode(CHANNEL_1) //
            .withCustomerGroupId(CUSTOMER_GROUP_ID) //
            .build());

    assertResults(offerPricings, "10", "20", "50", "60", "90", "100", "130", "140");
  }

  @Test
  public void findByChannelAndCustomerOrg() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withChannelCode(CHANNEL_1) //
            .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_ID) //
            .build());

    assertResults(offerPricings, "10", "30", "50", "70", "90", "110", "130", "150");
  }

  @Test
  public void findByChannelAndCustomerOrgAndCustomerGroup() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withChannelCode(CHANNEL_1) //
            .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_ID) //
            .withCustomerGroupId(CUSTOMER_GROUP_ID) //
            .build());

    assertResults(offerPricings, "10", "20", "30", "50", "60", "70", "90", "100", "110", "130", "140", "150");
  }

  @Test
  public void findByOfferIdBeforeRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withDate(DATE_BEFORE_RANGE) //
            .build());

    assertResults(offerPricings, "10", "90");
  }

  @Test
  public void findByCustomerGroupBeforeRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withCustomerGroupId(CUSTOMER_GROUP_ID) //
            .withDate(DATE_BEFORE_RANGE) //
            .build());

    assertResults(offerPricings, "10", "20", "90", "100");
  }

  @Test
  public void findByCustomerOrgBeforeRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_ID) //
            .withDate(DATE_BEFORE_RANGE) //
            .build());

    assertResults(offerPricings, "10", "30", "90", "110");
  }

  @Test
  public void findByCustomerGroupAndCustomerOrgBeforeRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_ID) //
            .withCustomerGroupId(CUSTOMER_GROUP_ID) //
            .withDate(DATE_BEFORE_RANGE) //
            .build());

    assertResults(offerPricings, "10", "20", "30", "90", "100", "110");
  }

  @Test
  public void findByChannelBeforeRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withChannelCode(CHANNEL_1) //
            .withDate(DATE_BEFORE_RANGE) //
            .build());

    assertResults(offerPricings, "10", "50", "90", "130");
  }

  @Test
  public void findByChannelAndCustomerGroupBeforeRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withChannelCode(CHANNEL_1) //
            .withCustomerGroupId(CUSTOMER_GROUP_ID) //
            .withDate(DATE_BEFORE_RANGE) //
            .build());

    assertResults(offerPricings, "10", "20", "50", "60", "90", "100", "130", "140");
  }

  @Test
  public void findByChannelAndCustomerOrgBeforeRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withChannelCode(CHANNEL_1) //
            .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_ID) //
            .withDate(DATE_BEFORE_RANGE) //
            .build());

    assertResults(offerPricings, "10", "30", "50", "70", "90", "110", "130", "150");
  }

  @Test
  public void findByChannelAndCustomerOrgAndCustomerGroupBeforeRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withChannelCode(CHANNEL_1) //
            .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_ID) //
            .withCustomerGroupId(CUSTOMER_GROUP_ID) //
            .withDate(DATE_BEFORE_RANGE) //
            .build());

    assertResults(offerPricings, "10", "20", "30", "50", "60", "70", "90", "100", "110", "130", "140", "150");
  }

  @Test
  public void findByOfferIdInRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withDate(DATE_IN_RANGE) //
            .build());

    assertResults(offerPricings, "10", "90", "170", "250");
  }

  @Test
  public void findByCustomerGroupIdInRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withCustomerGroupId(CUSTOMER_GROUP_ID) //
            .withDate(DATE_IN_RANGE) //
            .build());

    assertResults(offerPricings, "10", "20", "90", "100", "170", "180", "250", "260");
  }

  @Test
  public void findByCustomerOrgInRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_ID) //
            .withDate(DATE_IN_RANGE) //
            .build());

    assertResults(offerPricings, "10", "30", "90", "110", "170", "190", "250", "270");
  }

  @Test
  public void findByCustomerOrgAndCustomerGroupInRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_ID) //
            .withCustomerGroupId(CUSTOMER_GROUP_ID) //
            .withDate(DATE_IN_RANGE) //
            .build());

    assertResults(offerPricings, "10", "20", "30", "90", "100", "110", "170", "180", "190", "250", "260", "270");
  }

  @Test
  public void findByChannelInRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withChannelCode(CHANNEL_1) //
            .withDate(DATE_IN_RANGE) //
            .build());

    assertResults(offerPricings, "10", "50", "90", "130", "170", "210", "250", "290");
  }

  @Test
  public void findByChannelAndCustomerGroupInRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withChannelCode(CHANNEL_1) //
            .withCustomerGroupId(CUSTOMER_GROUP_ID) //
            .withDate(DATE_IN_RANGE) //
            .build());

    assertResults(offerPricings, "10", "20", "50", "60", "90", "100", "130", "140", "170", "180", "210", "220", "250", "260",
        "290", "300");
  }

  @Test
  public void findByChannelAndCustomerOrgInRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withChannelCode(CHANNEL_1) //
            .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_ID) //
            .withDate(DATE_IN_RANGE) //
            .build());

    assertResults(offerPricings, "10", "30", "50", "70", "90", "110", "130", "150", "170", "190", "210", "230", "250", "270",
        "290", "310");
  }

  @Test
  public void findByChannelAndCustomerOrgAndCustomerGroupInRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withChannelCode(CHANNEL_1) //
            .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_ID) //
            .withCustomerGroupId(CUSTOMER_GROUP_ID) //
            .withDate(DATE_IN_RANGE) //
            .build());

    assertResults(offerPricings, "10", "20", "30", "50", "60", "70", "90", "100", "110", "130", "140", "150", "170", "180", "190",
        "210", "220", "230", "250", "260", "270", "290", "300", "310");
  }

  @Test
  public void findByOfferIdOutOfRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withDate(OUT_OF_RANGE_DATE) //
            .build());

    assertResults(offerPricings, "10", "170");
  }

  @Test
  public void findByCustomerGroupOutOfRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withCustomerGroupId(CUSTOMER_GROUP_ID) //
            .withDate(OUT_OF_RANGE_DATE) //
            .build());

    assertResults(offerPricings, "10", "20", "170", "180");
  }

  @Test
  public void findByCustomerOrgOutOfRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_ID) //
            .withDate(OUT_OF_RANGE_DATE) //
            .build());

    assertResults(offerPricings, "10", "30", "170", "190");
  }

  @Test
  public void findByCustomerOrgAndCustomerGroupOutOfRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_ID) //
            .withCustomerGroupId(CUSTOMER_GROUP_ID) //
            .withDate(OUT_OF_RANGE_DATE) //
            .build());

    assertResults(offerPricings, "10", "20", "30", "170", "180", "190");
  }

  @Test
  public void findByChannelOutOfRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withChannelCode(CHANNEL_1) //
            .withDate(OUT_OF_RANGE_DATE) //
            .build());

    assertResults(offerPricings, "10", "50", "170", "210");
  }

  @Test
  public void findByChannelAndCustomerGroupOutOfRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withChannelCode(CHANNEL_1) //
            .withCustomerGroupId(CUSTOMER_GROUP_ID) //
            .withDate(OUT_OF_RANGE_DATE) //
            .build());

    assertResults(offerPricings, "10", "20", "50", "60", "170", "180", "210", "220");
  }

  @Test
  public void findByChannelAndCustomerOrgOutOfRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withChannelCode(CHANNEL_1) //
            .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_ID) //
            .withDate(OUT_OF_RANGE_DATE) //
            .build());

    assertResults(offerPricings, "10", "30", "50", "70", "170", "190", "210", "230");
  }

  @Test
  public void findByChannelAndCustomerOrgAndCustomerGroupOutOfRange() {
    List<OfferPricingModel> offerPricings = offerPricingDao.findOfferPricingsByCriteria( //
        offerPricingCriteria(OFFER_ID_1) //
            .withChannelCode(CHANNEL_1) //
            .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_ID) //
            .withCustomerGroupId(CUSTOMER_GROUP_ID) //
            .withDate(OUT_OF_RANGE_DATE) //
            .build());

    assertResults(offerPricings, "10", "20", "30", "50", "60", "70", "170", "180", "190", "210", "220", "230");
  }

  protected void assertResults(List<OfferPricingModel> offerPricings, String... expectedResults) {
    printResults(offerPricings);
    assertThat(offerPricings).hasSize(expectedResults.length);
    assertPrices(offerPricings, expectedResults);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionOnNullCriteria() {
    offerPricingDao.findOfferPricingsByCriteria(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionOnNullOfferId() {
    offerPricingDao.findOfferPricingsByCriteria(new OfferPricingCriteria());
  }

  protected void assertPrices(List<OfferPricingModel> offerPricings, String... priceValues) {
    Set<String> retrievedPrices = new HashSet<>();
    for (OfferPricingModel offerPricing : offerPricings) {
      Matcher matcher = PATTERN.matcher(offerPricing.getPriceDetailsJSON());
      if (matcher.find()) {
        retrievedPrices.add(matcher.group(1));
      }
    }
    assertThat(retrievedPrices).containsOnly((Object[]) priceValues);
  }

  protected void printResults(List<OfferPricingModel> offerPricings) {
    for (OfferPricingModel offerPricingModel : offerPricings) {
      System.out.println(offerPricingToString(offerPricingModel));
    }
  }

  private String offerPricingToString(OfferPricingModel offerPricing) {
    StringBuilder debug = new StringBuilder("<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
    debug.append("offerId: ").append(offerPricing.getOfferId()).append("\n");
    debug.append("channelCode: ").append(offerPricing.getChannelCode()).append("\n");
    debug.append("startDate: ").append(offerPricing.getStartDate()).append("\n");
    debug.append("endDate: ").append(offerPricing.getEndDate()).append("\n");
    debug.append("customerOrganizationId: ").append(offerPricing.getCustomerOrganizationId()).append("\n");
    debug.append("customerGroupId: ").append(offerPricing.getCustomerGroupId()).append("\n");
    debug.append("priceDetailsJSON: ").append(offerPricing.getPriceDetailsJSON()).append("\n");
    debug.append(">>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
    debug.append("\n");
    return debug.toString();
  }

}
