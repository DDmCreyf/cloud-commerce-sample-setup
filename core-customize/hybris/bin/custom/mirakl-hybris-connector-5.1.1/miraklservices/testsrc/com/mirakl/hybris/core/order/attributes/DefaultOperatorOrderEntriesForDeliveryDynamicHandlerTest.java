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
import de.hybris.platform.storelocator.model.PointOfServiceModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOperatorOrderEntriesForDeliveryDynamicHandlerTest {

  private static final String OFFER_ID = "offerId";

  private DefaultOperatorOrderEntriesForDeliveryDynamicHandler testObj = new DefaultOperatorOrderEntriesForDeliveryDynamicHandler();

  @Mock
  private AbstractOrderModel orderMock;
  @Mock
  private AbstractOrderEntryModel deliveryEntryMock, offerEntryMock, pointOfServiceEntryMock;
  @Mock
  private PointOfServiceModel pointOfServiceMock;

  @Test
  public void getsOnlyEntriesWithoutOfferAndWithoutDeliveryPointOfService() {
    when(orderMock.getEntries()).thenReturn(asList(deliveryEntryMock, offerEntryMock, pointOfServiceEntryMock));
    when(offerEntryMock.getOfferId()).thenReturn(OFFER_ID);
    when(pointOfServiceEntryMock.getDeliveryPointOfService()).thenReturn(pointOfServiceMock);

    List<AbstractOrderEntryModel> result = testObj.get(orderMock);

    assertThat(result).containsOnly(deliveryEntryMock);
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
