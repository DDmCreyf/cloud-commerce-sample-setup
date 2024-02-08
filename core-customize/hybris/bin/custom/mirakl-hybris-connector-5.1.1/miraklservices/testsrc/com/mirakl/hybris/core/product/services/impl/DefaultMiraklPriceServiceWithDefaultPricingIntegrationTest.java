package com.mirakl.hybris.core.product.services.impl;

import static java.math.BigDecimal.valueOf;
import static org.fest.assertions.Assertions.assertThat;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.beans.OfferVolumePricingData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.OfferService;
import com.mirakl.hybris.core.product.strategies.impl.DefaultOfferPricingSelectionStrategy;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTest;

@IntegrationTest
public class DefaultMiraklPriceServiceWithDefaultPricingIntegrationTest extends ServicelayerTest {

  private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm.ssZ";

  @Resource
  private DefaultMiraklPriceService miraklPriceService;

  @Resource(name = "defaultOfferPricingSelectionStrategy")
  private DefaultOfferPricingSelectionStrategy offerPricingSelectionStrategy;

  @Resource
  private OfferService offerService;

  @Before
  public void setUp() throws Exception {
    miraklPriceService.setOfferPricingSelectionStrategy(offerPricingSelectionStrategy);
    importCsv("/miraklservices/test/testMiraklPriceService.impex", "utf-8");
  }

  @Test
  public void shouldGetOfferPricingWithoutVolumePrices() throws Exception {
    BigDecimal discountPrice = valueOf(99.99);
    BigDecimal originPrice = valueOf(120.99);
    OfferModel offer = offerService.getOfferForId("2161");

    OfferPricingData offerPricing = miraklPriceService.getOfferPricing(offer);

    assertThat(offerPricing.getPrice()).isEqualByComparingTo(discountPrice);
    assertThat(offerPricing.getUnitOriginPrice()).isEqualByComparingTo(originPrice);
    assertThat(offerPricing.getUnitDiscountPrice()).isEqualByComparingTo(discountPrice);
    assertThat(offerPricing.getDiscountStartDate()).isNull();
    assertThat(offerPricing.getDiscountEndDate()).isNull();
    assertThat(offerPricing.getVolumePrices()).hasSize(1);
    OfferVolumePricingData volumePrice = offerPricing.getVolumePrices().get(0);
    assertThat(volumePrice.getPrice()).isEqualByComparingTo(discountPrice);
    assertThat(volumePrice.getUnitOriginPrice()).isEqualByComparingTo(originPrice);
    assertThat(volumePrice.getUnitDiscountPrice()).isEqualByComparingTo(discountPrice);
  }

  @Test
  public void shouldGetOfferPricingWithVolumePrices() throws Exception {
    OfferModel offer = offerService.getOfferForId("2162");

    OfferPricingData offerPricing = miraklPriceService.getOfferPricing(offer);

    assertThat(offerPricing.getPrice()).isEqualByComparingTo(valueOf(75.00));
    assertThat(offerPricing.getUnitOriginPrice()).isEqualByComparingTo(valueOf(75.00));
    assertThat(offerPricing.getUnitDiscountPrice()).isNull();
    assertThat(offerPricing.getDiscountStartDate())
        .isEqualTo(new SimpleDateFormat(DATE_FORMAT).parse("2022-02-14T00:00.00+0000"));
    assertThat(offerPricing.getDiscountEndDate()).isNull();
    List<OfferVolumePricingData> volumePrices = offerPricing.getVolumePrices();
    assertThat(volumePrices).isNotEmpty();
    {
      OfferVolumePricingData volumePricing = volumePrices.get(0);
      assertThat(volumePricing.getQuantityThreshold()).isEqualTo(1);
      assertThat(volumePricing.getPrice()).isEqualByComparingTo(valueOf(75.00));
      assertThat(volumePricing.getUnitOriginPrice()).isEqualByComparingTo(valueOf(75.00));
      assertThat(volumePricing.getUnitDiscountPrice()).isNull();
    }
    {
      OfferVolumePricingData volumePricing = volumePrices.get(1);
      assertThat(volumePricing.getQuantityThreshold()).isEqualTo(5);
      assertThat(volumePricing.getPrice()).isEqualByComparingTo(valueOf(60.00));
      assertThat(volumePricing.getUnitOriginPrice()).isEqualByComparingTo(valueOf(65.00));
      assertThat(volumePricing.getUnitDiscountPrice()).isEqualByComparingTo(valueOf(60.00));
    }
    {
      OfferVolumePricingData volumePricing = volumePrices.get(2);
      assertThat(volumePricing.getQuantityThreshold()).isEqualTo(10);
      assertThat(volumePricing.getPrice()).isEqualByComparingTo(valueOf(55.00));
      assertThat(volumePricing.getUnitOriginPrice()).isEqualByComparingTo(valueOf(55.00));
      assertThat(volumePricing.getUnitDiscountPrice()).isNull();
    }
  }

  @Test
  public void shouldGetVolumePriceForQuantity() {
    OfferModel offer = offerService.getOfferForId("2162");

    {
      OfferVolumePricingData volumePricing = miraklPriceService.getVolumePriceForQuantity(offer, 3);
      assertThat(volumePricing.getQuantityThreshold()).isEqualTo(1);
      assertThat(volumePricing.getPrice()).isEqualByComparingTo(valueOf(75.00));
      assertThat(volumePricing.getUnitOriginPrice()).isEqualByComparingTo(valueOf(75.00));
      assertThat(volumePricing.getUnitDiscountPrice()).isNull();
    }

    {
      OfferVolumePricingData volumePricing = miraklPriceService.getVolumePriceForQuantity(offer, 5);
      assertThat(volumePricing.getQuantityThreshold()).isEqualTo(5);
      assertThat(volumePricing.getPrice()).isEqualByComparingTo(valueOf(60.00));
      assertThat(volumePricing.getUnitOriginPrice()).isEqualByComparingTo(valueOf(65.00));
      assertThat(volumePricing.getUnitDiscountPrice()).isEqualByComparingTo(valueOf(60.00));
    }

    {
      OfferVolumePricingData volumePricing = miraklPriceService.getVolumePriceForQuantity(offer, 11);
      assertThat(volumePricing.getQuantityThreshold()).isEqualTo(10);
      assertThat(volumePricing.getPrice()).isEqualByComparingTo(valueOf(55.00));
      assertThat(volumePricing.getUnitOriginPrice()).isEqualByComparingTo(valueOf(55.00));
      assertThat(volumePricing.getUnitDiscountPrice()).isNull();
    }
  }

  @Test
  public void shouldGetVolumePriceForQuantityForReverseThresholds() {
    OfferModel offer = offerService.getOfferForId("2163");

    {
      OfferVolumePricingData volumePricing = miraklPriceService.getVolumePriceForQuantity(offer, 2);
      assertThat(volumePricing.getQuantityThreshold()).isEqualTo(1);
      assertThat(volumePricing.getPrice()).isEqualByComparingTo(valueOf(100.00));
      assertThat(volumePricing.getUnitOriginPrice()).isEqualByComparingTo(valueOf(100.00));
      assertThat(volumePricing.getUnitDiscountPrice()).isNull();
    }

    {
      OfferVolumePricingData volumePricing = miraklPriceService.getVolumePriceForQuantity(offer, 5);
      assertThat(volumePricing.getQuantityThreshold()).isEqualTo(5);
      assertThat(volumePricing.getPrice()).isEqualByComparingTo(valueOf(90.00));
      assertThat(volumePricing.getUnitOriginPrice()).isEqualByComparingTo(valueOf(150.00));
      assertThat(volumePricing.getUnitDiscountPrice()).isEqualByComparingTo(valueOf(90.00));
    }

    {
      OfferVolumePricingData volumePricing = miraklPriceService.getVolumePriceForQuantity(offer, 10);
      assertThat(volumePricing.getQuantityThreshold()).isEqualTo(10);
      assertThat(volumePricing.getPrice()).isEqualByComparingTo(valueOf(90.00));
      assertThat(volumePricing.getUnitOriginPrice()).isEqualByComparingTo(valueOf(200.00));
      assertThat(volumePricing.getUnitDiscountPrice()).isEqualByComparingTo(valueOf(90.00));
    }

  }


}
