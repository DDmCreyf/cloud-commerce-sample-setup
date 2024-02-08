package com.mirakl.hybris.core.product.populators;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.offer.price.MiraklOfferPricing;
import com.mirakl.client.mmp.domain.offer.price.MiraklVolumePrice;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.beans.OfferVolumePricingData;
import com.mirakl.hybris.core.product.services.MiraklPriceService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class MiraklOfferPricingToDataPopulatorTest {

  private static final BigDecimal DISCOUNT_PRICE = BigDecimal.valueOf(35.99);
  private static final BigDecimal ORIGIN_PRICE = BigDecimal.valueOf(49.99);
  private static final Date DISCOUNT_END_DATE = new DateTime().plusDays(3).toDate();
  private static final Date DISCOUNT_START_DATE = new DateTime().minusDays(2).toDate();
  private static final String CHANNEL_CODE = "CHANNEL-1";

  @InjectMocks
  private MiraklOfferPricingToDataPopulator populator;

  @Mock
  private Converter<MiraklVolumePrice, OfferVolumePricingData> offerVolumePricingDataConverter;
  @Mock
  private MiraklPriceService miraklPriceService;
  @Mock
  private MiraklOfferPricing miraklOfferPricing;
  @Mock
  private MiraklVolumePrice vp1, vp2;
  private List<MiraklVolumePrice> miraklVolumePrices;

  @Captor
  private ArgumentCaptor<List<MiraklVolumePrice>> volumePricesCaptor;

  @Before
  public void setUp() throws Exception {
    miraklVolumePrices = asList(vp1, vp2);
  }

  @Test
  public void shouldPopulateWithVolumePrices() {
    when(miraklOfferPricing.getChannelCode()).thenReturn(CHANNEL_CODE);
    when(miraklOfferPricing.getDiscountStartDate()).thenReturn(DISCOUNT_START_DATE);
    when(miraklOfferPricing.getDiscountEndDate()).thenReturn(DISCOUNT_END_DATE);
    when(miraklOfferPricing.getUnitOriginPrice()).thenReturn(ORIGIN_PRICE);
    when(miraklOfferPricing.getUnitDiscountPrice()).thenReturn(DISCOUNT_PRICE);
    when(miraklOfferPricing.getVolumePrices()).thenReturn(miraklVolumePrices);

    OfferPricingData offerPricingData = new OfferPricingData();
    populator.populate(miraklOfferPricing, offerPricingData);

    assertThat(offerPricingData.getChannelCode()).isEqualTo(CHANNEL_CODE);
    assertThat(offerPricingData.getDiscountEndDate()).isEqualTo(DISCOUNT_END_DATE);
    assertThat(offerPricingData.getDiscountStartDate()).isEqualTo(DISCOUNT_START_DATE);
    assertThat(offerPricingData.getUnitOriginPrice()).isEqualTo(ORIGIN_PRICE);
    assertThat(offerPricingData.getUnitDiscountPrice()).isEqualTo(DISCOUNT_PRICE);
    verify(offerVolumePricingDataConverter).convertAll(volumePricesCaptor.capture());
    List<MiraklVolumePrice> volumePrices = volumePricesCaptor.getValue();
    assertThat(volumePrices).containsOnly(vp1, vp2);
  }

  @Test
  public void shouldPopulateWithoutVolumePrices() {
    OfferPricingData offerPricingData = new OfferPricingData();
    populator.populate(miraklOfferPricing, offerPricingData);

    verify(offerVolumePricingDataConverter).convertAll(volumePricesCaptor.capture());
    assertThat(volumePricesCaptor.getValue()).isEmpty();

  }
}
