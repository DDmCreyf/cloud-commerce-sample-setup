package com.mirakl.hybris.fulfilmentprocess.actions.order;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.front.domain.order.create.MiraklCreatedOrders;
import com.mirakl.client.mmp.front.domain.order.create.MiraklOfferNotShippable;
import com.mirakl.hybris.core.fulfilment.events.NotShippableOffersEvent;
import com.mirakl.hybris.core.order.services.MiraklOrderService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.event.EventService;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class CreateMarketplaceOrderActionTest {

  @InjectMocks
  private CreateMarketplaceOrderAction action;

  @Mock
  private MiraklOrderService miraklOrderService;
  @Mock
  private EventService eventService;
  @Mock
  private OrderModel orderModel;
  @Mock
  private OrderProcessModel orderProcessModel;
  @Mock
  private MiraklCreatedOrders createdOrders;
  @Mock
  private AbstractOrderEntryModel orderEntryWithOffer1, orderEntryWithOffer2, orderEntry1, orderEntry2;
  @Mock
  private MiraklOfferNotShippable notShippableOffer1, notShippableOffer2;

  @Captor
  private ArgumentCaptor<NotShippableOffersEvent> notShippableOffersEventCaptor;

  private List<AbstractOrderEntryModel> orderEntriesWithOffers;
  private List<MiraklOfferNotShippable> notShippableOffers;

  @Before
  public void setUp() throws Exception {
    orderEntriesWithOffers = Arrays.<AbstractOrderEntryModel>asList(orderEntryWithOffer1, orderEntryWithOffer2);
    notShippableOffers = asList(notShippableOffer1, notShippableOffer2);
    when(orderModel.getMarketplaceEntries()).thenReturn(orderEntriesWithOffers);
    when(miraklOrderService.createMarketplaceOrders(orderModel)).thenReturn(createdOrders);
    when(orderProcessModel.getOrder()).thenReturn(orderModel);
    when(createdOrders.getOffersNotShippable()).thenReturn(Collections.<MiraklOfferNotShippable>emptyList());
  }

  @Test
  public void shouldCreateConsignmentsForOrderWithMarketplaceEntries() {
    action.executeAction(orderProcessModel);

    verify(miraklOrderService).createMarketplaceOrders(orderModel);
    verify(eventService, never()).publishEvent(any(NotShippableOffersEvent.class));
  }

  @Test
  public void shouldNotCreateConsignmentsForOrderWithNoMarketplaceEntries() {
    when(orderModel.getMarketplaceEntries()).thenReturn(Collections.<AbstractOrderEntryModel>emptyList());

    action.executeAction(orderProcessModel);

    verify(miraklOrderService, never()).createMarketplaceOrders(orderModel);
    verify(eventService, never()).publishEvent(any(NotShippableOffersEvent.class));
  }

  @Test
  public void shouldHandleNotShippableOffers() {
    when(createdOrders.getOffersNotShippable()).thenReturn(notShippableOffers);

    action.executeAction(orderProcessModel);

    verify(eventService).publishEvent(notShippableOffersEventCaptor.capture());
    NotShippableOffersEvent notShippableOffersEvent = notShippableOffersEventCaptor.getValue();
    assertThat(notShippableOffersEvent.getNotShippableOffers()).isEqualTo(notShippableOffers);
    assertThat(notShippableOffersEvent.getOrder()).isEqualTo(orderModel);
  }


}
