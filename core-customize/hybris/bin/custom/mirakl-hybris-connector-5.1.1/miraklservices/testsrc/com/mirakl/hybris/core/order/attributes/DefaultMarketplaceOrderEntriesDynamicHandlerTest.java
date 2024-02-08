package com.mirakl.hybris.core.order.attributes;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMarketplaceOrderEntriesDynamicHandlerTest {

  private static final String OFFER_ID = "offerId";

  private DefaultMarketplaceOrderEntriesDynamicHandler testObj = new DefaultMarketplaceOrderEntriesDynamicHandler();

  @Mock
  private AbstractOrderModel orderMock;
  @Mock
  private AbstractOrderEntryModel offerEntryMock, operatorEntryMock;

  @Test
  public void getsOnlyEntriesWithoutOfferAndWithoutDeliveryPointOfService() {
    when(orderMock.getEntries()).thenReturn(asList(offerEntryMock, operatorEntryMock));
    when(offerEntryMock.getOfferId()).thenReturn(OFFER_ID);

    List<AbstractOrderEntryModel> result = testObj.get(orderMock);

    assertThat(result).containsOnly(offerEntryMock);
  }

  @Test
  public void returnsEmptyListIfAbstractOrderIsEmpty() {
    List<AbstractOrderEntryModel> result = testObj.get(orderMock);

    assertThat(result).isEmpty();
  }

  @Test(expected = IllegalArgumentException.class)
  public void throwsIllegalArgumentExceptionIfAbstractOrderIsNull() {
    testObj.get(null);
  }
}
