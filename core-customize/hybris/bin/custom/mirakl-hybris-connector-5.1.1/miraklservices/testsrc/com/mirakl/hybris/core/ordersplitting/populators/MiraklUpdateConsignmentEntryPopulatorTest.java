package com.mirakl.hybris.core.ordersplitting.populators;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.EnumSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.order.MiraklOrderLine;
import com.mirakl.client.mmp.domain.order.MiraklRefund;
import com.mirakl.client.mmp.domain.order.state.AbstractMiraklOrderStatus.State;
import com.mirakl.client.mmp.domain.order.state.MiraklOrderLineStatus;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklUpdateConsignmentEntryPopulatorTest {

  private static final EnumSet<State> SHIPPING_STATES = EnumSet.of(State.SHIPPED, State.TO_COLLECT, State.RECEIVED);
  private static final EnumSet<State> CANCELLATION_STATES = EnumSet.of(State.CANCELED, State.REFUSED);
  private static final String MIRAKL_REFUND_ID = "mirakl-refund-id";
  private static final Long SHIPPED_QUANTITY = 3L;

  @InjectMocks
  private MiraklUpdateConsignmentEntryPopulator consignmentEntryPopulator;

  @Mock
  private Populator<MiraklRefund, RefundEntryModel> refundEntryPopulator;
  @Mock
  private MiraklOrderLine miraklOrderLine;
  @Mock
  private MiraklOrderLineStatus miraklOrderLineStatus;
  @Mock
  private MiraklRefund miraklRefund;
  @Mock
  private OrderEntryModel orderEntryModel;
  @Mock
  private OrderModel orderModel;
  @Mock
  private ReturnRequestModel returnRequestModel;
  @Mock
  private RefundEntryModel refundEntryModel;


  private ConsignmentEntryModel consignmentEntry;

  @Before
  public void setUp() throws Exception {
    consignmentEntryPopulator.setShippingStates(SHIPPING_STATES);
    consignmentEntryPopulator.setCancellationStates(CANCELLATION_STATES);
    consignmentEntry = new ConsignmentEntryModel();
    consignmentEntry.setOrderEntry(orderEntryModel);
    when(miraklOrderLine.getQuantity()).thenReturn(SHIPPED_QUANTITY.intValue());
    when(miraklRefund.getId()).thenReturn(MIRAKL_REFUND_ID);
    when(orderEntryModel.getOrder()).thenReturn(orderModel);
    when(orderModel.getReturnRequests()).thenReturn(asList(returnRequestModel));
    when(returnRequestModel.getReturnEntries()).thenReturn(Arrays.<ReturnEntryModel>asList(refundEntryModel));
    when(refundEntryModel.getMiraklRefundId()).thenReturn(MIRAKL_REFUND_ID);
  }

  @Test
  public void shouldPopulateShippedQuantityIfShipped() {
    when(miraklOrderLine.getStatus()).thenReturn(miraklOrderLineStatus);
    when(miraklOrderLineStatus.getState()).thenReturn(State.SHIPPED);

    consignmentEntryPopulator.populate(miraklOrderLine, consignmentEntry);

    assertThat(consignmentEntry.getShippedQuantity()).isEqualTo(SHIPPED_QUANTITY);
  }

  @Test
  public void shouldNotPopulateShippedQuantityIfNotShipped() {
    consignmentEntry.setShippedQuantity(null);
    when(miraklOrderLine.getStatus()).thenReturn(miraklOrderLineStatus);
    when(miraklOrderLineStatus.getState()).thenReturn(State.STAGING);

    consignmentEntryPopulator.populate(miraklOrderLine, consignmentEntry);

    assertThat(consignmentEntry.getShippedQuantity()).isNull();
  }

  @Test
  public void shouldPopulateRefunds() {
    when(miraklOrderLine.getRefunds()).thenReturn(asList(miraklRefund));

    consignmentEntryPopulator.populate(miraklOrderLine, consignmentEntry);

    verify(refundEntryPopulator).populate(miraklRefund, refundEntryModel);
  }

  @Test
  public void shouldPopulateShippedQuantityIfCancelled() {
    consignmentEntry.setShippedQuantity(null);
    when(miraklOrderLine.getStatus()).thenReturn(miraklOrderLineStatus);
    when(miraklOrderLineStatus.getState()).thenReturn(State.REFUSED);

    consignmentEntryPopulator.populate(miraklOrderLine, consignmentEntry);

    assertThat(consignmentEntry.getShippedQuantity()).isEqualTo(0l);
  }

}
