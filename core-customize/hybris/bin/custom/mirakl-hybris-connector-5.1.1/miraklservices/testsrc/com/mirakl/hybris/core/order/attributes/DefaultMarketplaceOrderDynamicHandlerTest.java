package com.mirakl.hybris.core.order.attributes;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMarketplaceOrderDynamicHandlerTest {

  private static final String OFFER_ID = "offerId";

  private DefaultMarketplaceOrderDynamicHandler testObj = new DefaultMarketplaceOrderDynamicHandler();

  @Mock
  private AbstractOrderModel model;
  @Mock
  private AbstractOrderEntryModel offerEntryMock, nonOfferEntryMock;

  @Before
  public void setUp() {
    when(offerEntryMock.getOfferId()).thenReturn(OFFER_ID);
  }

  @Test
  public void returnsTrueIfOrderContainsOnlyEntriesWithOffers() {
    when(model.getEntries()).thenReturn(singletonList(offerEntryMock));

    Boolean result = testObj.get(model);

    assertThat(result).isTrue();
  }

  @Test
  public void returnsFalseIfOrderContainsEntriesWithAndWithoutOffers() {
    when(model.getEntries()).thenReturn(asList(offerEntryMock, nonOfferEntryMock));

    Boolean result = testObj.get(model);

    assertThat(result).isFalse();
  }

  @Test
  public void returnsFalseIfOrderContainsOnlyEntriesWithoutOffers() {
    when(model.getEntries()).thenReturn(singletonList(nonOfferEntryMock));

    Boolean result = testObj.get(model);

    assertThat(result).isFalse();
  }

  @Test
  public void returnsFalseIfOrderIsEmpty() {
    when(model.getEntries()).thenReturn(Collections.<AbstractOrderEntryModel>emptyList());

    Boolean result = testObj.get(model);

    assertThat(result).isFalse();
  }

  @Test(expected = IllegalArgumentException.class)
  public void throwsIllegalArgumentExceptionIfOrderIsNull() {
    testObj.get(null);
  }
}
