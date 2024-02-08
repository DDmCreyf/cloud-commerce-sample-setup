package com.mirakl.hybris.core.comparators;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.mirakl.hybris.beans.ComparableOfferData;
import com.mirakl.hybris.core.enums.OfferState;
import com.mirakl.hybris.core.model.OfferModel;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class OfferStateComparatorTest {

  private static final String OFFER_STATE_1 = "state1";
  private static final String OFFER_STATE_2 = "state2";
  private static final String OFFER_STATE_PRIORITY_CODE = "offerStatePriorityCode";

  private OfferStateComparator<OfferModel> testObj =
      new OfferStateComparator<OfferModel>(OfferState.valueOf(OFFER_STATE_PRIORITY_CODE));

  private ComparableOfferData<OfferModel> offer1 = new ComparableOfferData<OfferModel>();
  private ComparableOfferData<OfferModel> offer2 = new ComparableOfferData<OfferModel>();

  @Test
  public void compareWhenOffer1IsNew() throws Exception {
    offer1.setState(OfferState.valueOf(OFFER_STATE_PRIORITY_CODE));
    offer2.setState(OfferState.valueOf(OFFER_STATE_1));

    int result = testObj.compare(offer1, offer2);

    assertThat(result).isEqualTo(-1);
  }

  @Test
  public void compareWhenOffer2IsNew() throws Exception {
    offer1.setState(OfferState.valueOf(OFFER_STATE_1));
    offer2.setState(OfferState.valueOf(OFFER_STATE_PRIORITY_CODE));

    int result = testObj.compare(offer1, offer2);

    assertThat(result).isEqualTo(1);
  }

  @Test
  public void compareWhenBothOffersAreNew() throws Exception {
    offer1.setState(OfferState.valueOf(OFFER_STATE_PRIORITY_CODE));
    offer2.setState(OfferState.valueOf(OFFER_STATE_PRIORITY_CODE));

    int result = testObj.compare(offer1, offer2);

    assertThat(result).isEqualTo(0);
  }

  @Test
  public void compareWhenBothOffersAreRandom() throws Exception {
    offer1.setState(OfferState.valueOf(OFFER_STATE_1));
    offer2.setState(OfferState.valueOf(OFFER_STATE_2));

    int result = testObj.compare(offer1, offer2);

    assertThat(result).isEqualTo(0);
  }
}
