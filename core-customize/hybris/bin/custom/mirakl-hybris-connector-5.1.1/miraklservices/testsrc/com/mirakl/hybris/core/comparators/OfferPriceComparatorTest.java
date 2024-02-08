package com.mirakl.hybris.core.comparators;

import static org.fest.assertions.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

import com.mirakl.hybris.beans.ComparableOfferData;
import com.mirakl.hybris.core.model.OfferModel;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class OfferPriceComparatorTest {

  private OfferPriceComparator<OfferModel> priceComparator = new OfferPriceComparator<OfferModel>();

  private ComparableOfferData<OfferModel> offer1 = new ComparableOfferData<OfferModel>();
  private ComparableOfferData<OfferModel> offer2 = new ComparableOfferData<OfferModel>();


  @Test
  public void compareOffer1CheaperThanOffer2() {
    offer1.setTotalPrice(BigDecimal.valueOf(150));
    offer2.setTotalPrice(BigDecimal.valueOf(175));

    assertThat(priceComparator.compare(offer1, offer2)).isEqualTo(-1);
  }

  @Test
  public void compareOffer2CheaperThanOffer1() {
    offer1.setTotalPrice(BigDecimal.valueOf(175));
    offer2.setTotalPrice(BigDecimal.valueOf(150));

    assertThat(priceComparator.compare(offer1, offer2)).isEqualTo(1);
  }

  @Test
  public void compareOffer1HasSamePriceThanOffer2() {
    offer1.setTotalPrice(BigDecimal.valueOf(150));
    offer2.setTotalPrice(BigDecimal.valueOf(150));

    assertThat(priceComparator.compare(offer1, offer2)).isEqualTo(0);
  }

  @Test
  public void compareOffer1HasNoPrice() {
    offer1.setTotalPrice(null);
    offer2.setTotalPrice(BigDecimal.valueOf(150));

    assertThat(priceComparator.compare(offer1, offer2)).isEqualTo(1);
  }

  @Test
  public void compareOffer2HasNoPrice() {
    offer1.setTotalPrice(BigDecimal.valueOf(150));
    offer2.setTotalPrice(null);

    assertThat(priceComparator.compare(offer1, offer2)).isEqualTo(-1);
  }
}
