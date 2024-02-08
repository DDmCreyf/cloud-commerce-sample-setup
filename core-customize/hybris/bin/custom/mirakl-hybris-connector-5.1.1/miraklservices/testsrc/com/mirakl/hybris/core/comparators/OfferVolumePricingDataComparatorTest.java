package com.mirakl.hybris.core.comparators;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.mirakl.hybris.beans.OfferVolumePricingData;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class OfferVolumePricingDataComparatorTest {

  private OfferVolumePricingDataComparator comparator = new OfferVolumePricingDataComparator();

  private OfferVolumePricingData volumePrice1 = new OfferVolumePricingData();
  private OfferVolumePricingData volumePrice2 = new OfferVolumePricingData();

  @Test
  public void compareVolume1LowerThanVolume2() throws Exception {
    volumePrice1.setQuantityThreshold(1);
    volumePrice2.setQuantityThreshold(5);

    assertThat(comparator.compare(volumePrice1, volumePrice2)).isEqualTo(-1);
  }

  @Test
  public void compareVolume2LowerThanVolume1() throws Exception {
    volumePrice1.setQuantityThreshold(10);
    volumePrice2.setQuantityThreshold(5);

    assertThat(comparator.compare(volumePrice1, volumePrice2)).isEqualTo(1);
  }

  @Test
  public void compareVolume2EqualsVolume1() throws Exception {
    volumePrice1.setQuantityThreshold(10);
    volumePrice2.setQuantityThreshold(10);

    assertThat(comparator.compare(volumePrice1, volumePrice2)).isEqualTo(0);
  }

}
