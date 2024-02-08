package com.mirakl.hybris.core.product.populators;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.offer.price.MiraklVolumePrice;
import com.mirakl.hybris.beans.OfferPricingData;
import com.mirakl.hybris.beans.OfferVolumePricingData;
import com.mirakl.hybris.core.product.services.MiraklPriceService;

import de.hybris.bootstrap.annotations.UnitTest;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class MiraklVolumePriceToDataPopulatorTest {

  private static final BigDecimal ORIGIN_PRICE = BigDecimal.valueOf(39.99);
  private static final BigDecimal DISCOUNT_PRICE = BigDecimal.valueOf(29.99);
  private static final int QUANTITY_THRESHOLD = 3;

  @InjectMocks
  private MiraklVolumePriceToDataPopulator populator;

  @Mock
  private MiraklPriceService miraklPriceService;
  @Mock
  private OfferPricingData offerPricingData;
  @Mock
  private MiraklVolumePrice miraklVolumePrice;

  @Before
  public void setUp() throws Exception {
    when(miraklVolumePrice.getUnitDiscountPrice()).thenReturn(DISCOUNT_PRICE);
    when(miraklVolumePrice.getUnitOriginPrice()).thenReturn(ORIGIN_PRICE);
    when(miraklVolumePrice.getQuantityThreshold()).thenReturn(QUANTITY_THRESHOLD);
  }

  @Test
  public void shouldPopulate() {
    OfferVolumePricingData volumePriceData = new OfferVolumePricingData();
    populator.populate(miraklVolumePrice, volumePriceData);

    assertThat(volumePriceData.getQuantityThreshold()).isEqualTo(QUANTITY_THRESHOLD);
    assertThat(volumePriceData.getUnitDiscountPrice()).isEqualTo(DISCOUNT_PRICE);
    assertThat(volumePriceData.getUnitOriginPrice()).isEqualTo(ORIGIN_PRICE);
  }

}
