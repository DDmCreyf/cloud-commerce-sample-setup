package com.mirakl.hybris.advancedpricing.product.utils;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.OfferPricingModel;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OfferPricingKeyTest {

  private static final Date START_DATE = new Date();
  private static final long START_DATE_TIMESTAMP = START_DATE.getTime();
  private static final Date END_DATE = new Date();
  private static final long END_DATE_TIMESTAMP = END_DATE.getTime();
  private static final String OFFER_ID = "2035";
  private static final String CHANNEL_CODE = "CHAN3";
  private static final String CUSTOMER_ORG = "DADDYDASS";
  private static final String CUSTOMER_GROUP = "GOLD";

  @Test
  public void buildFromOfferPricingModel() {
    OfferPricingModel offerPricingModel = Mockito.mock(OfferPricingModel.class);
    when(offerPricingModel.getOfferId()).thenReturn(OFFER_ID);
    when(offerPricingModel.getChannelCode()).thenReturn(CHANNEL_CODE);
    when(offerPricingModel.getCustomerGroupId()).thenReturn(CUSTOMER_GROUP);
    when(offerPricingModel.getEndDate()).thenReturn(END_DATE);

    OfferPricingKey key = new OfferPricingKey(offerPricingModel);

    assertThat(key.getOfferId()).isEqualTo(OFFER_ID);
    assertThat(key.getChannel()).isEqualTo(CHANNEL_CODE);
    assertThat(key.getCustomerGroup()).isEqualTo(CUSTOMER_GROUP);
    assertThat(key.getCustomerOrg()).isEqualTo(null);
    assertThat(key.getStartDate()).isEqualTo(null);
    assertThat(key.getEndDate()).isEqualTo(END_DATE);
  }

  @Test
  public void serializeHappy() {
    OfferPricingKey key = new OfferPricingKey(OFFER_ID, CHANNEL_CODE, null, CUSTOMER_ORG, null, END_DATE);

    String serializedKey = key.serialize();

    assertThat(serializedKey).isEqualTo(OFFER_ID + "|" + CHANNEL_CODE + "||" + CUSTOMER_ORG + "||" + END_DATE_TIMESTAMP);
    assertThat(new OfferPricingKey(serializedKey)).isEqualTo(key);
  }

  @Test
  public void serializeWithNulls() {
    OfferPricingKey key = new OfferPricingKey(OFFER_ID, CHANNEL_CODE, null, null, null, null);

    String serializedKey = key.serialize();

    assertThat(serializedKey).isEqualTo(OFFER_ID + "|" + CHANNEL_CODE + "||||");
    assertThat(new OfferPricingKey(serializedKey)).isEqualTo(key);
  }

  @Test
  public void serializeWithSeparator() {
    OfferPricingKey key = new OfferPricingKey(OFFER_ID, null, "CG|76", null, null, END_DATE);

    String serializedKey = key.serialize();

    assertThat(serializedKey).isEqualTo(OFFER_ID + "||CG\\|76|||" + END_DATE_TIMESTAMP);
    assertThat(new OfferPricingKey(serializedKey)).isEqualTo(key);
  }

  @Test
  public void serializeWithEscapingChar() {
    OfferPricingKey key = new OfferPricingKey(OFFER_ID, null, CUSTOMER_GROUP, "CO\\|22", START_DATE, null);

    String serializedKey = key.serialize();

    assertThat(serializedKey).isEqualTo(OFFER_ID + "||" + CUSTOMER_GROUP + "|CO\\\\\\|22|" + START_DATE_TIMESTAMP + "|");
    assertThat(new OfferPricingKey(serializedKey)).isEqualTo(key);
  }

  @Test
  public void serializeWithCharArmageddon() {
    OfferPricingKey key = new OfferPricingKey(OFFER_ID + "||||\\\\", null, "\\\\||\\" + CUSTOMER_GROUP + "\\|\\|",
        "CO\\|\\||\\|22", START_DATE, null);

    String serializedKey = key.serialize();

    assertThat(new OfferPricingKey(serializedKey)).isEqualTo(key);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldNotDeserializeKeysWithInvalidEscaping() {
    new OfferPricingKey("20\\35|||GOLD|||CO22|1644941449971|");
  }

  @Test(expected = IllegalStateException.class)
  public void shouldNotDeserializeKeysWithInvalidCount() {
    new OfferPricingKey("2035|||GOLD|||CO22");
  }

}
