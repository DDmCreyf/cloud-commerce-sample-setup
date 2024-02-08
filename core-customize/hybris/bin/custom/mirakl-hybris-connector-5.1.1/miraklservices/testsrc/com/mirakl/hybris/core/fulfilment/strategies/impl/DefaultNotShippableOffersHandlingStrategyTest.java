package com.mirakl.hybris.core.fulfilment.strategies.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.front.domain.order.create.MiraklOfferNotShippable;
import com.mirakl.hybris.core.fulfilment.events.NotShippableOffersEvent;
import com.mirakl.hybris.core.order.services.MiraklOrderService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.model.ModelService;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultNotShippableOffersHandlingStrategyTest {
  private static final OrderEntryStatus STATUS_FOR_NOT_SHIPPABLE_ENTRIES = OrderEntryStatus.DEAD;

  @InjectMocks
  private DefaultNotShippableOffersHandlingStrategy eventHandler;

  @Mock
  private NotShippableOffersEvent event;

  @Mock
  private OrderModel orderModel;

  @Mock
  private MiraklOrderService miraklOrderService;

  @Mock
  private ModelService modelService;

  @Mock
  private MiraklOfferNotShippable notShippableOffer1, notShippableOffer2;

  @Mock
  private AbstractOrderEntryModel orderEntry1, orderEntry2;

  private List<MiraklOfferNotShippable> miraklNotShippableOffers;
  private List<AbstractOrderEntryModel> notShippableEntries;

  @Before
  public void setUp() throws Exception {
    miraklNotShippableOffers = asList(notShippableOffer1, notShippableOffer2);
    notShippableEntries = asList(orderEntry1, orderEntry2);
    when(event.getOrder()).thenReturn(orderModel);
    when(event.getNotShippableOffers()).thenReturn(miraklNotShippableOffers);
    when(miraklOrderService.extractNotShippableEntries(miraklNotShippableOffers, orderModel)).thenReturn(notShippableEntries);
  }

  @Test
  public void shouldMarkNotShippableEntries() {
    eventHandler.handleEvent(event);

    verify(orderEntry1).setQuantityStatus(STATUS_FOR_NOT_SHIPPABLE_ENTRIES);
    verify(orderEntry2).setQuantityStatus(STATUS_FOR_NOT_SHIPPABLE_ENTRIES);
    verify(modelService).saveAll(notShippableEntries);
  }

  @Test
  public void shouldChangeStatusForRetrievedEntriesAndIgnoreMissing() {
    List<AbstractOrderEntryModel> entries = singletonList(orderEntry1);
    when(miraklOrderService.extractNotShippableEntries(miraklNotShippableOffers, orderModel)).thenReturn(entries);

    eventHandler.handleEvent(event);

    verify(orderEntry1).setQuantityStatus(STATUS_FOR_NOT_SHIPPABLE_ENTRIES);
    verify(modelService).saveAll(entries);
    verifyZeroInteractions(orderEntry2);
  }

  @Test
  public void shouldNotChangeStatusIfCouldNotRetrieveEntries() {
    when(miraklOrderService.extractNotShippableEntries(miraklNotShippableOffers, orderModel))
        .thenReturn(Collections.<AbstractOrderEntryModel>emptyList());

    eventHandler.handleEvent(event);

    verifyZeroInteractions(orderEntry1, orderEntry2, modelService);
  }

}
