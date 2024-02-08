package com.mirakl.hybris.core.ordersplitting.populators;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.mirakl.client.mmp.domain.order.MiraklOrder;
import com.mirakl.client.mmp.domain.order.MiraklOrderLine;
import com.mirakl.client.mmp.domain.order.MiraklOrderLineOfferInformation;
import com.mirakl.client.mmp.domain.order.MiraklOrderShipping;
import com.mirakl.client.mmp.domain.shipping.MiraklShippingType;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class MiraklCreateConsignmentPopulatorTest {

  private static final String OFFER_ID_2 = "offer-id-2";
  private static final String OFFER_ID_1 = "offer-id-1";
  private static final String SHIPPING_TYPE_LABEL = "shipping-type-label";
  private static final String SHIPPING_TYPE_CODE = "shipping-type-code";
  private static final String SHOP_NAME = "shop-name";
  private static final String SHOP_ID = "shop-id";
  private static final int LEADTIME_TO_SHIP = 10000;
  private static final Boolean CAN_EVALUATE = Boolean.FALSE;
  private static final Boolean CAN_CANCEL = Boolean.TRUE;
  private static final String MIRAKL_ORDER_ID = "mirakl-order-id";
  private static final BigDecimal CONSIGNMENT_TOTAL_PRICE = BigDecimal.valueOf(100.5);
  private static final BigDecimal CONSIGNMENT_SHIPPING_COST = BigDecimal.valueOf(7.5);

  @InjectMocks
  private MiraklCreateConsignmentPopulator populator;

  @Mock
  private Converter<Pair<AbstractOrderEntryModel, MiraklOrderLine>, ConsignmentEntryModel> consignmentEntryConverter;

  @Mock
  private WarehouseService warehouseService;

  @Mock
  private OrderModel orderModel;

  @Mock
  private MiraklOrder miraklOrder;

  @Mock
  private AbstractOrderEntryModel orderEntryModel1, orderEntryModel2;

  @Mock
  private MiraklOrderLine miraklOrderLine1, miraklOrderLine2;

  @Mock
  private ConsignmentEntryModel consignmentEntryModel1, consignmentEntryModel2;

  @Mock
  private MiraklOrderLineOfferInformation orderLineOffer1, orderLineOffer2;

  @Mock
  private MiraklOrderShipping miraklOrderShipping;

  @Mock
  private MiraklShippingType miraklShippingType;

  private List<MiraklOrderLine> orderLines;
  private List<AbstractOrderEntryModel> orderEntries;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() throws Exception {
    orderLines = asList(miraklOrderLine1, miraklOrderLine2);
    orderEntries = asList(orderEntryModel1, orderEntryModel2);
    when(miraklOrder.getId()).thenReturn(MIRAKL_ORDER_ID);
    when(miraklOrder.getOrderLines()).thenReturn(orderLines);
    when(miraklOrder.getCanCancel()).thenReturn(CAN_CANCEL);
    when(miraklOrder.getCanEvaluate()).thenReturn(CAN_EVALUATE);
    when(miraklOrder.getLeadtimeToShip()).thenReturn(LEADTIME_TO_SHIP);
    when(miraklOrder.getShipping()).thenReturn(miraklOrderShipping);
    when(miraklOrder.getShopId()).thenReturn(SHOP_ID);
    when(miraklOrder.getShopName()).thenReturn(SHOP_NAME);
    when(miraklOrder.getTotalPrice()).thenReturn(CONSIGNMENT_TOTAL_PRICE);
    when(miraklOrderShipping.getPrice()).thenReturn(CONSIGNMENT_SHIPPING_COST);
    when(miraklOrderShipping.getType()).thenReturn(miraklShippingType);
    when(miraklShippingType.getCode()).thenReturn(SHIPPING_TYPE_CODE);
    when(miraklShippingType.getLabel()).thenReturn(SHIPPING_TYPE_LABEL);
    when(orderModel.getEntries()).thenReturn(orderEntries);
    when(orderEntryModel1.getOfferId()).thenReturn(OFFER_ID_1);
    when(orderEntryModel2.getOfferId()).thenReturn(OFFER_ID_2);
    when(miraklOrderLine1.getOffer()).thenReturn(orderLineOffer1);
    when(miraklOrderLine2.getOffer()).thenReturn(orderLineOffer2);
    when(orderLineOffer1.getId()).thenReturn(OFFER_ID_1);
    when(orderLineOffer2.getId()).thenReturn(OFFER_ID_2);
    when(consignmentEntryConverter.convert(any(Pair.class))).thenAnswer(new Answer<ConsignmentEntryModel>() {

      @Override
      public ConsignmentEntryModel answer(InvocationOnMock invocation) throws Throwable {
        return mock(ConsignmentEntryModel.class);
      }
    });
  }

  @SuppressWarnings("unchecked")
  @Test
  public void shouldPopulateMarketplaceConsignment() {
    MarketplaceConsignmentModel consignment = new MarketplaceConsignmentModel();

    populator.populate(Pair.of(orderModel, miraklOrder), consignment);

    verify(consignmentEntryConverter, times(orderLines.size())).convert(any(Pair.class));
    assertThat(consignment.getCode()).isEqualTo(MIRAKL_ORDER_ID);
    assertThat(consignment.getCanCancel()).isEqualTo(CAN_CANCEL);
    assertThat(consignment.getCanEvaluate()).isEqualTo(CAN_EVALUATE);
    assertThat(consignment.getConsignmentEntries().size()).isEqualTo(orderLines.size());
    assertThat(consignment.getDeliveryMode()).isNull();
    assertThat(consignment.getDeliveryPointOfService()).isNull();
    assertThat(consignment.getLeadTimeToShip()).isEqualTo(LEADTIME_TO_SHIP);
    assertThat(consignment.getOrder()).isEqualTo(orderModel);
    assertThat(consignment.getShippingTypeCode()).isEqualTo(SHIPPING_TYPE_CODE);
    assertThat(consignment.getShippingTypeLabel()).isEqualTo(SHIPPING_TYPE_LABEL);
    assertThat(consignment.getShopId()).isEqualTo(SHOP_ID);
    assertThat(consignment.getShopName()).isEqualTo(SHOP_NAME);
    assertThat(consignment.getTotalPrice()).isEqualTo(CONSIGNMENT_TOTAL_PRICE.doubleValue());
    assertThat(consignment.getShippingCost()).isEqualTo(CONSIGNMENT_SHIPPING_COST.doubleValue());
  }


}
