package com.mirakl.hybris.core.order.attributes;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOperatorOrderEntriesDynamicHandlerTest {
  private static final String OFFER_ID = "offerId";

  @InjectMocks
  private DefaultOperatorOrderEntriesDynamicHandler handler;

  @Mock
  private AbstractOrderModel order;
  @Mock
  private AbstractOrderEntryModel offerEntry, operatorEntry;

  @Before
  public void setUp() {
    when(offerEntry.getOfferId()).thenReturn(OFFER_ID);
  }

  @Test
  public void shouldReturnOperatorEntries() {
    when(order.getEntries()).thenReturn(asList(offerEntry, operatorEntry));

    List<AbstractOrderEntryModel> result = handler.get(order);

    assertThat(result).containsOnly(operatorEntry);
  }

  @Test
  public void returnsEmptyListIfEmptyOrder() {
    when(order.getEntries()).thenReturn(Collections.<AbstractOrderEntryModel>emptyList());

    List<AbstractOrderEntryModel> result = handler.get(order);

    assertThat(result).isEmpty();
  }

  @Test
  public void returnsEmptyListIfNoOperatorEntries() {
    when(order.getEntries()).thenReturn(asList(offerEntry));

    List<AbstractOrderEntryModel> result = handler.get(order);

    assertThat(result).isEmpty();
  }

  @Test(expected = IllegalArgumentException.class)
  public void throwsIllegalArgumentExceptionIfAbstractOrderIsNull() {
    handler.get(null);
  }
}
