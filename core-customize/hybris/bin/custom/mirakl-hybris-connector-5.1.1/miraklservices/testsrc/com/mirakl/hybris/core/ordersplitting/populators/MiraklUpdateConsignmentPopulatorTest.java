package com.mirakl.hybris.core.ordersplitting.populators;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;
import java.util.EnumSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.order.MiraklOrder;
import com.mirakl.client.mmp.domain.order.MiraklOrderLine;
import com.mirakl.client.mmp.domain.order.MiraklOrderLineOfferInformation;
import com.mirakl.client.mmp.domain.order.MiraklOrderShipping;
import com.mirakl.client.mmp.domain.order.state.AbstractMiraklOrderStatus.State;
import com.mirakl.client.mmp.domain.order.state.MiraklOrderStatus;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class MiraklUpdateConsignmentPopulatorTest {

  private static final EnumSet<State> SHIPPING_STATES = EnumSet.of(State.SHIPPED, State.TO_COLLECT, State.RECEIVED);
  private static final EnumSet<State> CANCELLATION_STATES = EnumSet.of(State.CANCELED, State.REFUSED);
  private static final BigDecimal TOTAL_PRICE = BigDecimal.valueOf(30.75);
  private static final BigDecimal SHIPPING_COST = BigDecimal.valueOf(3.5);
  private static final String OFFER_ID = "offer-id";
  private static final Date LAST_UPDATE_DATE = new Date();
  private static final String IMPRINT_NUMBER = "imprint-number";
  private static final Boolean CAN_CANCEL = Boolean.FALSE;
  private static final Boolean CAN_EVALUATE = Boolean.TRUE;
  private static final State STATE = State.SHIPPING;

  @InjectMocks
  private MiraklUpdateConsignmentPopulator updateConsignmentPopulator;

  @Mock
  private Populator<MiraklOrderLine, ConsignmentEntryModel> consignmentEntryPopulator;
  @Mock
  private MiraklOrder miraklOrder;
  @Mock
  private MiraklOrderLine miraklOrderLine;
  @Mock
  private MiraklOrderLineOfferInformation offerInformation;
  @Mock
  private MiraklOrderStatus miraklOrderStatus;
  @Mock
  private MiraklOrderShipping miraklOrderShipping;
  @Mock
  private ConsignmentEntryModel consignmentEntry;
  @Mock
  private AbstractOrderEntryModel orderEntry;

  private MarketplaceConsignmentModel marketplaceConsignment;

  @Before
  public void setUp() throws Exception {
    updateConsignmentPopulator.setShippingStates(SHIPPING_STATES);
    updateConsignmentPopulator.setCancellationStates(CANCELLATION_STATES);
    marketplaceConsignment = new MarketplaceConsignmentModel();
    marketplaceConsignment.setConsignmentEntries(singleton(consignmentEntry));
    when(consignmentEntry.getOrderEntry()).thenReturn(orderEntry);
    when(orderEntry.getOfferId()).thenReturn(OFFER_ID);
    when(miraklOrder.getOrderLines()).thenReturn(singletonList(miraklOrderLine));
    when(miraklOrder.getCanCancel()).thenReturn(CAN_CANCEL);
    when(miraklOrder.getCanEvaluate()).thenReturn(CAN_EVALUATE);
    when(miraklOrder.getImprintNumber()).thenReturn(IMPRINT_NUMBER);
    when(miraklOrder.getLastUpdatedDate()).thenReturn(LAST_UPDATE_DATE);
    when(miraklOrder.getStatus()).thenReturn(miraklOrderStatus);
    when(miraklOrder.getTotalPrice()).thenReturn(TOTAL_PRICE);
    when(miraklOrder.getShipping()).thenReturn(miraklOrderShipping);
    when(miraklOrderShipping.getPrice()).thenReturn(SHIPPING_COST);
    when(miraklOrderLine.getOffer()).thenReturn(offerInformation);
    when(offerInformation.getId()).thenReturn(OFFER_ID);
    when(miraklOrderStatus.getState()).thenReturn(STATE);
  }

  @Test
  public void shouldPopulateMarketplaceConsignment() {
    updateConsignmentPopulator.populate(miraklOrder, marketplaceConsignment);

    assertThat(marketplaceConsignment.getCanCancel()).isEqualTo(CAN_CANCEL);
    assertThat(marketplaceConsignment.getCanEvaluate()).isEqualTo(CAN_EVALUATE);
    assertThat(marketplaceConsignment.getImprintNumber()).isEqualTo(IMPRINT_NUMBER);
    assertThat(marketplaceConsignment.getLastUpdatedDate()).isEqualTo(LAST_UPDATE_DATE);
    assertThat(marketplaceConsignment.getMiraklOrderStatus().getCode()).isEqualTo(STATE.name());
    assertThat(marketplaceConsignment.getTotalPrice()).isEqualTo(TOTAL_PRICE.doubleValue());
    assertThat(marketplaceConsignment.getShippingCost()).isEqualTo(SHIPPING_COST.doubleValue());
    verify(consignmentEntryPopulator).populate(miraklOrderLine, consignmentEntry);
  }

}
