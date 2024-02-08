package com.mirakl.hybris.core.util.services.impl;


import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.mirakl.client.core.internal.mapper.CustomObjectMapper;
import com.mirakl.client.mmp.domain.offer.price.MiraklOfferPricing;
import com.mirakl.client.mmp.domain.offer.price.MiraklVolumePrice;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;

import de.hybris.bootstrap.annotations.UnitTest;
import shaded.com.fasterxml.jackson.core.JsonParseException;
import shaded.com.fasterxml.jackson.core.type.TypeReference;
import shaded.com.fasterxml.jackson.databind.JsonMappingException;

@UnitTest
public class DefaultJsonMarshallingServiceTest {

  private final static String JSON_FROM_API =
      "{ \"errors\": [], \"orders\": [ { \"currency_iso_code\": \"USD\", \"leadtime_to_ship\": 0, \"offers\": [ { \"allow_quote_requests\": false, \"allowed_shipping_types\": [ { \"code\": \"STD\", \"label\": \"Standard\", \"shipping_additional_fields\": [] }, { \"code\": \"NXD\", \"label\": \"Next day\", \"shipping_additional_fields\": [] }, { \"code\": \"SMD\", \"label\": \"Same day\", \"shipping_additional_fields\": [] } ], \"line_only_shipping_price\": 5, \"line_only_total_price\": 85, \"line_original_quantity\": 1, \"line_price\": 80, \"line_quantity\": 1, \"line_shipping_price\": 5, \"line_total_price\": 85, \"offer_additional_fields\": [ { \"code\": \"temp2\", \"type\": \"STRING\", \"value\": \"TEmp\" } ], \"offer_discount\": null, \"offer_id\": 2003, \"offer_price\": 80, \"offer_quantity\": 37, \"product_category_code\": \"576\", \"promotions\": [], \"shipping_price_additional_unit\": 2, \"shipping_price_unit\": 5 } ], \"promotions\": { \"applied_promotions\": [], \"total_deduced_amount\": 0 }, \"selected_shipping_type\": { \"code\": \"STD\", \"label\": \"Standard\", \"shipping_additional_fields\": [] }, \"shipping_types\": [ { \"code\": \"STD\", \"label\": \"Standard\", \"shipping_additional_fields\": [], \"total_shipping_price\": 5 }, { \"code\": \"NXD\", \"label\": \"Next day\", \"shipping_additional_fields\": [], \"total_shipping_price\": 8 }, { \"code\": \"SMD\", \"label\": \"Same day\", \"shipping_additional_fields\": [], \"total_shipping_price\": 15 } ], \"shop_id\": 2000, \"shop_name\": \"CAMERA Pro\" } ], \"total_count\": 1 }";

  private DefaultJsonMarshallingService jsonMarshallingService = new DefaultJsonMarshallingService();

  @Before
  public void setUp() {
    jsonMarshallingService.setMapper(CustomObjectMapper.getInstance());
  }

  @Test
  public void shouldHandleDeserializeJson() throws JsonParseException, JsonMappingException, IOException {

    MiraklOrderShippingFees shippingFees = jsonMarshallingService.fromJson(JSON_FROM_API, MiraklOrderShippingFees.class);

    assertThat(shippingFees).isNotNull();
    assertThat(shippingFees.getOrders()).isNotEmpty();
    assertThat(shippingFees.getOrders().get(0).getOffers()).isNotEmpty();
    assertThat(shippingFees.getOrders().get(0).getOffers().get(0).getOfferAdditionalFields()).isNotEmpty();
  }

  @Test
  public void shouldHandleNullDeserialization() throws JsonParseException, JsonMappingException, IOException {
    MiraklOrderShippingFees shippingFees = jsonMarshallingService.fromJson(null, MiraklOrderShippingFees.class);

    assertThat(shippingFees).isNull();
  }

  @Test
  public void shouldSerializeToJson() throws JsonParseException, JsonMappingException, IOException {
    MiraklOrderShippingFees shippingFees = jsonMarshallingService.fromJson(JSON_FROM_API, MiraklOrderShippingFees.class);
    String valueAsString = jsonMarshallingService.toJson(shippingFees);
    shippingFees = jsonMarshallingService.fromJson(valueAsString, MiraklOrderShippingFees.class);

    assertThat(shippingFees).isNotNull();
    assertThat(shippingFees.getOrders()).isNotEmpty();
    assertThat(shippingFees.getOrders().get(0).getOffers()).isNotEmpty();
    assertThat(shippingFees.getOrders().get(0).getOffers().get(0).getOfferAdditionalFields()).isNotEmpty();
  }

  @Test
  public void shouldDeserializeLists() {
    List<MiraklOfferPricing> offerPricings = new ArrayList<>();
    String channel1 = "channel1";
    String channel2 = "channel2";
    {
      MiraklOfferPricing offerPricing = new MiraklOfferPricing();
      Date startDate = new Date();
      Date endDate = new Date();
      offerPricing.setChannelCode(channel1);
      offerPricing.setDiscountStartDate(startDate);
      offerPricing.setDiscountEndDate(endDate);
      List<MiraklVolumePrice> prices = new ArrayList<>();
      {
        MiraklVolumePrice volumePrice = new MiraklVolumePrice();
        volumePrice.setUnitDiscountPrice(BigDecimal.valueOf(43));
        volumePrice.setUnitOriginPrice(BigDecimal.valueOf(50));
        volumePrice.setQuantityThreshold(1);
        prices.add(volumePrice);
      }
      {
        MiraklVolumePrice volumePrice = new MiraklVolumePrice();
        volumePrice.setUnitDiscountPrice(BigDecimal.valueOf(38));
        volumePrice.setUnitOriginPrice(BigDecimal.valueOf(45));
        volumePrice.setQuantityThreshold(5);
        prices.add(volumePrice);
      }
      offerPricing.setVolumePrices(prices);
      offerPricings.add(offerPricing);
    }
    {
      MiraklOfferPricing offerPricing = new MiraklOfferPricing();
      Date startDate = new Date();
      Date endDate = new Date();
      offerPricing.setChannelCode(channel2);
      offerPricing.setDiscountStartDate(startDate);
      offerPricing.setDiscountEndDate(endDate);
      List<MiraklVolumePrice> prices = new ArrayList<>();
      {
        MiraklVolumePrice volumePrice = new MiraklVolumePrice();
        volumePrice.setUnitDiscountPrice(BigDecimal.valueOf(83));
        volumePrice.setUnitOriginPrice(BigDecimal.valueOf(90));
        volumePrice.setQuantityThreshold(1);
        prices.add(volumePrice);
      }
      {
        MiraklVolumePrice volumePrice = new MiraklVolumePrice();
        volumePrice.setUnitDiscountPrice(BigDecimal.valueOf(78));
        volumePrice.setUnitOriginPrice(BigDecimal.valueOf(85));
        volumePrice.setQuantityThreshold(5);
        prices.add(volumePrice);
      }
      offerPricing.setVolumePrices(prices);
      offerPricings.add(offerPricing);
    }

    String valueAsString = jsonMarshallingService.toJson(offerPricings);

    List<MiraklOfferPricing> fromJson =
        jsonMarshallingService.fromJson(valueAsString, new TypeReference<List<MiraklOfferPricing>>() {});
    assertThat(fromJson).hasSize(2);
    MiraklOfferPricing channel1OfferPricing = extractPricing(offerPricings, channel1);
    assertThat(channel1OfferPricing).isInstanceOf(MiraklOfferPricing.class);
    assertThat(channel1OfferPricing.getVolumePrices()).hasSize(2);
    assertThat(channel1OfferPricing.getVolumePrices().get(0)).isInstanceOf(MiraklVolumePrice.class);
  }

  protected MiraklOfferPricing extractPricing(final List<MiraklOfferPricing> offerPricings, final String channelCode) {
    return FluentIterable.from(offerPricings).firstMatch(new Predicate<MiraklOfferPricing>() {

      @Override
      public boolean apply(MiraklOfferPricing offerPricing) {
        return (channelCode == null && offerPricing.getChannelCode() == null)
            || channelCode.equals(offerPricing.getChannelCode());
      }
    }).orNull();
  }
}
