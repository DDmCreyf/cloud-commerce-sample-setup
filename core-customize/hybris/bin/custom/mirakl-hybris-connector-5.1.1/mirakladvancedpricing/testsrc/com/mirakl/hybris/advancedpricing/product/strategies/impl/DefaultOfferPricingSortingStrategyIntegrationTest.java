package com.mirakl.hybris.advancedpricing.product.strategies.impl;

import static java.util.Arrays.asList;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.fest.assertions.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.joda.time.LocalDate;
import org.junit.Test;

import com.mirakl.hybris.advancedpricing.product.strategies.OfferPricingSortingStrategy;
import com.mirakl.hybris.beans.OfferPricingData;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTest;

@IntegrationTest
public class DefaultOfferPricingSortingStrategyIntegrationTest extends ServicelayerTest {

  private static final String CUSTOMER_ORGANIZATION_1 = "customer-organization-1";
  private static final String CUSTOMER_GROUP_1 = "customer-group-1";
  private static final String CHANNEL_1 = "channel-1";

  @Resource
  private OfferPricingSortingStrategy offerPricingSortingStrategy;

  @Test
  public void shouldSortPricings() {
    LocalDate now = LocalDate.now();

    // Default price
    ExtendedOfferPricingData op1 = ExtendedOfferPricingData.pricing(1) //
        .withPrice(100.99);

    // Advanced pricings
    ExtendedOfferPricingData op2 = ExtendedOfferPricingData.pricing(2) //
        .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_1) //
        .withPrice(80.99);

    ExtendedOfferPricingData op3 = ExtendedOfferPricingData.pricing(3) //
        .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_1) //
        .withStartDate(now.minusDays(1).toDate()) //
        .withPrice(82.99);

    ExtendedOfferPricingData op4 = ExtendedOfferPricingData.pricing(4) //
        .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_1) //
        .withEndtDate(now.plusDays(3).toDate()) //
        .withPrice(83.99);

    ExtendedOfferPricingData op5 = ExtendedOfferPricingData.pricing(5) //
        .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_1) //
        .withStartDate(now.minusDays(1).toDate()) //
        .withEndtDate(now.plusDays(3).toDate()) //
        .withPrice(83.00);

    ExtendedOfferPricingData op6 = ExtendedOfferPricingData.pricing(6) //
        .withChannelCode(CHANNEL_1) //
        .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_1) //
        .withPrice(180.99);

    ExtendedOfferPricingData op7 = ExtendedOfferPricingData.pricing(7) //
        .withChannelCode(CHANNEL_1) //
        .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_1) //
        .withStartDate(now.minusDays(1).toDate()) //
        .withPrice(182.99);

    ExtendedOfferPricingData op8 = ExtendedOfferPricingData.pricing(8) //
        .withChannelCode(CHANNEL_1) //
        .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_1) //
        .withEndtDate(now.plusDays(3).toDate()) //
        .withPrice(183.99);

    ExtendedOfferPricingData op9 = ExtendedOfferPricingData.pricing(9) //
        .withChannelCode(CHANNEL_1) //
        .withCustomerOrganizationId(CUSTOMER_ORGANIZATION_1) //
        .withStartDate(now.minusDays(1).toDate()) //
        .withEndtDate(now.plusDays(3).toDate()) //
        .withPrice(183.00);

    ExtendedOfferPricingData op10 = ExtendedOfferPricingData.pricing(10) //
        .withCustomerGroupId(CUSTOMER_GROUP_1) //
        .withPrice(70.99);

    ExtendedOfferPricingData op11 = ExtendedOfferPricingData.pricing(11) //
        .withCustomerGroupId(CUSTOMER_GROUP_1) //
        .withStartDate(now.minusDays(1).toDate()) //
        .withPrice(72.99);

    ExtendedOfferPricingData op12 = ExtendedOfferPricingData.pricing(12) //
        .withCustomerGroupId(CUSTOMER_GROUP_1) //
        .withEndtDate(now.plusDays(3).toDate()) //
        .withPrice(73.99);

    ExtendedOfferPricingData op13 = ExtendedOfferPricingData.pricing(13) //
        .withCustomerGroupId(CUSTOMER_GROUP_1) //
        .withStartDate(now.minusDays(1).toDate()) //
        .withEndtDate(now.plusDays(3).toDate()) //
        .withPrice(73.00);

    ExtendedOfferPricingData op14 = ExtendedOfferPricingData.pricing(14) //
        .withChannelCode(CHANNEL_1) //
        .withCustomerGroupId(CUSTOMER_GROUP_1) //
        .withPrice(170.99);

    ExtendedOfferPricingData op15 = ExtendedOfferPricingData.pricing(15) //
        .withChannelCode(CHANNEL_1) //
        .withCustomerGroupId(CUSTOMER_GROUP_1) //
        .withStartDate(now.minusDays(1).toDate()) //
        .withPrice(172.99);

    ExtendedOfferPricingData op16 = ExtendedOfferPricingData.pricing(16) //
        .withChannelCode(CHANNEL_1) //
        .withCustomerGroupId(CUSTOMER_GROUP_1) //
        .withEndtDate(now.plusDays(3).toDate()) //
        .withPrice(173.99);

    ExtendedOfferPricingData op17 = ExtendedOfferPricingData.pricing(17) //
        .withChannelCode(CHANNEL_1) //
        .withCustomerGroupId(CUSTOMER_GROUP_1) //
        .withStartDate(now.minusDays(1).toDate()) //
        .withEndtDate(now.plusDays(3).toDate()) //
        .withPrice(173.00);

    ExtendedOfferPricingData op18 = ExtendedOfferPricingData.pricing(18) //
        .withStartDate(now.minusDays(1).toDate()) //
        .withPrice(50.99);

    ExtendedOfferPricingData op19 = ExtendedOfferPricingData.pricing(19) //
        .withEndtDate(now.plusDays(3).toDate()) //
        .withPrice(52.99);

    ExtendedOfferPricingData op20 = ExtendedOfferPricingData.pricing(20) //
        .withStartDate(now.minusDays(1).toDate()) //
        .withEndtDate(now.plusDays(3).toDate()) //
        .withPrice(51.99);

    ExtendedOfferPricingData op21 = ExtendedOfferPricingData.pricing(21) //
        .withChannelCode(CHANNEL_1) //
        .withPrice(60.99);

    ExtendedOfferPricingData op22 = ExtendedOfferPricingData.pricing(22) //
        .withChannelCode(CHANNEL_1) //
        .withStartDate(now.minusDays(1).toDate()) //
        .withPrice(62.99);

    ExtendedOfferPricingData op23 = ExtendedOfferPricingData.pricing(23) //
        .withChannelCode(CHANNEL_1) //
        .withEndtDate(now.plusDays(3).toDate()) //
        .withPrice(63.99);

    ExtendedOfferPricingData op24 = ExtendedOfferPricingData.pricing(24) //
        .withChannelCode(CHANNEL_1) //
        .withStartDate(now.minusDays(1).toDate()) //
        .withEndtDate(now.plusDays(3).toDate()) //
        .withPrice(63.00);

    List<OfferPricingData> sortedList = offerPricingSortingStrategy.sort(asList(op1, op2, op3, op4, op5, op6, op7, op8, op9, op10,
        op11, op12, op13, op14, op15, op16, op17, op18, op19, op20, op21, op22, op23, op24));


    printPricings(sortedList);
    assertThat(sortedList).containsExactly(op7, op9, op8, op6, op15, op17, op16, op14, op3, op5, op4, op2, op11, op13, op12, op10,
        op22, op24, op23, op21, op18, op20, op19, op1);

  }

  private static class ExtendedOfferPricingData extends OfferPricingData {
    private int id;

    private static ExtendedOfferPricingData pricing(int id) {
      ExtendedOfferPricingData offerPricingData = new ExtendedOfferPricingData();
      offerPricingData.setId(id);
      return offerPricingData;
    }

    ExtendedOfferPricingData withPrice(double price) {
      setPrice(BigDecimal.valueOf(price));
      return this;
    }

    ExtendedOfferPricingData withChannelCode(String channelCode) {
      setChannelCode(channelCode);
      return this;
    }

    ExtendedOfferPricingData withCustomerOrganizationId(String customerOrganizationId) {
      setCustomerOrganizationId(customerOrganizationId);
      return this;
    }

    ExtendedOfferPricingData withCustomerGroupId(String customerGroupId) {
      setCustomerGroupId(customerGroupId);
      return this;
    }

    ExtendedOfferPricingData withStartDate(Date startDate) {
      setStartDate(startDate);
      return this;
    }

    ExtendedOfferPricingData withEndtDate(Date endDate) {
      setEndDate(endDate);
      return this;
    }

    public void setId(int id) {
      this.id = id;
    }

    public int getId() {
      return id;
    }
  }

  private void printPricings(List<OfferPricingData> offerPricings) {
    if (isNotEmpty(offerPricings)) {
      for (OfferPricingData offerPricingData : offerPricings) {
        System.out.println(toDebug(offerPricingData));
      }
    }
  }

  private String toDebug(OfferPricingData offerPricing) {
    StringBuilder debug = new StringBuilder("<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
    if (offerPricing instanceof ExtendedOfferPricingData) {
      debug.append("id: ").append(((ExtendedOfferPricingData) offerPricing).getId()).append("\n");
    }
    debug.append("channelCode: ").append(offerPricing.getChannelCode()).append("\n");
    debug.append("startDate: ").append(offerPricing.getStartDate()).append("\n");
    debug.append("endDate: ").append(offerPricing.getEndDate()).append("\n");
    debug.append("customerOrganizationId: ").append(offerPricing.getCustomerOrganizationId()).append("\n");
    debug.append("customerGroupId: ").append(offerPricing.getCustomerGroupId()).append("\n");
    debug.append("price: ").append(offerPricing.getPrice()).append("\n");
    debug.append(">>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
    debug.append("\n");
    return debug.toString();
  }


}
