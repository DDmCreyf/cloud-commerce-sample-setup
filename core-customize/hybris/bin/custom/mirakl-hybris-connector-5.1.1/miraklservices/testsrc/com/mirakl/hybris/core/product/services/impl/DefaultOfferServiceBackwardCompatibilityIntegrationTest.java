package com.mirakl.hybris.core.product.services.impl;

import static java.math.BigDecimal.valueOf;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.junit.Test;

import com.mirakl.client.mmp.domain.offer.price.MiraklOfferPricing;
import com.mirakl.client.mmp.domain.offer.price.MiraklVolumePrice;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.beans.OfferVolumePricingData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.product.services.OfferService;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

@IntegrationTest
public class DefaultOfferServiceBackwardCompatibilityIntegrationTest extends ServicelayerTest {

  private static final String SHOP_ID = "100";
  private static final String OFFER_ID = "101";
  private static final String PRODUCT_CODE = "1234567890";
  private static final String CHANNEL_1 = "CHANNEL_1";

  @Resource
  private OfferService offerService;
  @Resource
  private ModelService modelService;
  @Resource
  private JsonMarshallingService jsonMarshallingService;

  @Test
  public void shouldLoadLegacyOfferPricings() {
    BigDecimal discountPrice1 = valueOf(11.99);
    Date discountStartDate1 = new DateTime(2025, 12, 28, 14, 55, 00).toDate();
    Date discountEndDate1 = new DateTime(2026, 02, 28, 14, 55, 00).toDate();
    BigDecimal originPrice1 = valueOf(13.99);
    MiraklOfferPricing pricing1 = new MiraklOfferPricing();
    pricing1.setChannelCode(CHANNEL_1);
    pricing1.setUnitOriginPrice(originPrice1);
    pricing1.setUnitDiscountPrice(discountPrice1);
    pricing1.setDiscountStartDate(discountStartDate1);
    pricing1.setDiscountEndDate(discountEndDate1);
    
    BigDecimal discountPrice2 = valueOf(21.99);
    Date discountStartDate2 = new DateTime().minusDays(10).toDate();
    Date discountEndDate2 = new DateTime().plusDays(10).toDate();
    BigDecimal originPrice2 = valueOf(23.99);
    MiraklOfferPricing pricing2= new MiraklOfferPricing();
    pricing2.setChannelCode(CHANNEL_1);
    pricing2.setUnitOriginPrice(originPrice2);
    pricing2.setUnitDiscountPrice(discountPrice2);
    pricing2.setDiscountStartDate(discountStartDate2);
    pricing2.setDiscountEndDate(discountEndDate2);

    MiraklVolumePrice volumePrice1 = new MiraklVolumePrice();
    int vpQtyThreshold1 = 1;
    BigDecimal vpUnitDiscountPrice1 = discountPrice2;
    BigDecimal vpUnitOriginPrice1 = originPrice2;
    volumePrice1.setQuantityThreshold(vpQtyThreshold1);
    volumePrice1.setUnitDiscountPrice(vpUnitDiscountPrice1);
    volumePrice1.setUnitOriginPrice(vpUnitOriginPrice1);

    int vpQtyThreshold2 = 20;
    BigDecimal vpUnitDiscountPrice2 = valueOf(7.99);
    BigDecimal vpUnitOriginPrice2 = valueOf(6.99);
    MiraklVolumePrice volumePrice2 = new MiraklVolumePrice();
    volumePrice2.setQuantityThreshold(vpQtyThreshold2);
    volumePrice2.setUnitDiscountPrice(vpUnitDiscountPrice2);
    volumePrice2.setUnitOriginPrice(vpUnitOriginPrice2);

    pricing2.setVolumePrices(asList(volumePrice1, volumePrice2));

    BigDecimal originPrice3 = valueOf(4.99);
    MiraklOfferPricing pricing3 = new MiraklOfferPricing();
    pricing3.setUnitOriginPrice(originPrice3);

    OfferModel offer = createOfferWithLegacyPricing(pricing1, pricing2, pricing3);

    List<OfferPricingData> allPricings = offerService.loadAllOfferPricings(offer);
    assertThat(allPricings).hasSize(3);
    OfferPricingData loadedPricing1 = allPricings.get(0);
    assertThat(loadedPricing1.getPrice()).isEqualTo(originPrice1);
    assertThat(loadedPricing1.getDiscountEndDate()).isEqualTo(discountEndDate1);
    assertThat(loadedPricing1.getDiscountStartDate()).isEqualTo(discountStartDate1);
    assertThat(loadedPricing1.getUnitDiscountPrice()).isEqualTo(discountPrice1);
    assertThat(loadedPricing1.getUnitOriginPrice()).isEqualTo(originPrice1);
    assertThat(loadedPricing1.getChannelCode()).isEqualTo(CHANNEL_1);
    
    OfferPricingData loadedPricing2 = allPricings.get(1);
    assertThat(loadedPricing2.getPrice()).isEqualTo(discountPrice2);
    assertThat(loadedPricing2.getDiscountEndDate()).isEqualTo(discountEndDate2);
    assertThat(loadedPricing2.getDiscountStartDate()).isEqualTo(discountStartDate2);
    assertThat(loadedPricing2.getUnitDiscountPrice()).isEqualTo(discountPrice2);
    assertThat(loadedPricing2.getUnitOriginPrice()).isEqualTo(originPrice2);
    assertThat(loadedPricing2.getChannelCode()).isEqualTo(CHANNEL_1);
    List<OfferVolumePricingData> loadedVolumePrices = loadedPricing2.getVolumePrices();
    assertThat(loadedVolumePrices).hasSize(2);
    OfferVolumePricingData loadedVolumePrice1 = loadedVolumePrices.get(0);
    assertThat(loadedVolumePrice1);
    assertThat(loadedVolumePrice1.getPrice()).isEqualTo(vpUnitDiscountPrice1);
    assertThat(loadedVolumePrice1.getQuantityThreshold()).isEqualTo(vpQtyThreshold1);
    assertThat(loadedVolumePrice1.getUnitOriginPrice()).isEqualTo(vpUnitOriginPrice1);
    assertThat(loadedVolumePrice1.getUnitDiscountPrice()).isEqualTo(vpUnitDiscountPrice1);
    
    OfferVolumePricingData loadedVolumePrice2 = loadedVolumePrices.get(1);
    assertThat(loadedVolumePrice2);
    assertThat(loadedVolumePrice2.getPrice()).isEqualTo(vpUnitDiscountPrice2);
    assertThat(loadedVolumePrice2.getQuantityThreshold()).isEqualTo(vpQtyThreshold2);
    assertThat(loadedVolumePrice2.getUnitOriginPrice()).isEqualTo(vpUnitOriginPrice2);
    assertThat(loadedVolumePrice2.getUnitDiscountPrice()).isEqualTo(vpUnitDiscountPrice2);

    OfferPricingData loadedPricing3 = allPricings.get(2);
    assertThat(loadedPricing3.getPrice()).isEqualTo(originPrice3);
    assertThat(loadedPricing3.getDiscountEndDate()).isNull();
    assertThat(loadedPricing3.getDiscountStartDate()).isNull();
    assertThat(loadedPricing3.getUnitDiscountPrice()).isNull();
    assertThat(loadedPricing3.getUnitOriginPrice()).isEqualTo(originPrice3);
    assertThat(loadedPricing3.getChannelCode()).isNull();
  }

  protected OfferModel createOfferWithLegacyPricing(MiraklOfferPricing... pricings) {
    OfferModel offer = modelService.create(OfferModel.class);
    offer.setProductCode(PRODUCT_CODE);
    offer.setProductCode(OFFER_ID);
    ShopModel shop = modelService.create(ShopModel.class);
    shop.setId(SHOP_ID);
    offer.setShop(shop);
    offer.setAllOfferPricingsJSON(jsonMarshallingService.toJson(asList(pricings)));

    return offer;
  }


}
