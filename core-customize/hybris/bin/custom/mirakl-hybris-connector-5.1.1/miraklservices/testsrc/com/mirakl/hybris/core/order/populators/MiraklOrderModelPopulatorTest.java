package com.mirakl.hybris.core.order.populators;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.order.MiraklOrder;
import com.mirakl.client.mmp.front.domain.order.create.MiraklCreatedOrders;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklOrderModelPopulatorTest {

  private static final String CONSIGNMENT_1_CODE = "consignment-1-code";
  private static final String CONSIGNMENT_2_CODE = "consignment-2-code";
  private static final String CREATED_ORDERS_JSON = "created-orders-json";

  @InjectMocks
  private MiraklOrderModelPopulator populator;

  @Mock
  private Converter<MiraklOrder, MarketplaceConsignmentModel> consignmentModelConverter;
  @Mock
  private JsonMarshallingService jsonMarshallingService;
  @Mock
  private MiraklCreatedOrders miraklCreatedOrders;
  @Mock
  private OrderModel order;
  @Mock
  private MarketplaceConsignmentModel consignment1, consignment2;
  @Mock
  private MiraklOrder miraklOrder1, miraklOrder2;
  private Set<MarketplaceConsignmentModel> consignments;
  private List<MiraklOrder> miraklOrders;

  @Before
  public void setUp() throws Exception {
    when(jsonMarshallingService.toJson(miraklCreatedOrders)).thenReturn(CREATED_ORDERS_JSON);
    when(consignment1.getCode()).thenReturn(CONSIGNMENT_1_CODE);
    when(consignment2.getCode()).thenReturn(CONSIGNMENT_2_CODE);
    when(miraklOrder1.getId()).thenReturn(CONSIGNMENT_1_CODE);
    when(miraklOrder2.getId()).thenReturn(CONSIGNMENT_2_CODE);
    consignments = newHashSet(consignment1, consignment2);
    miraklOrders = asList(miraklOrder1, miraklOrder2);
  }

  @Test
  public void shouldPopulateAdditionalFields() throws Exception {
    when(order.getMarketplaceConsignments()).thenReturn(consignments);
    when(miraklCreatedOrders.getOrders()).thenReturn(miraklOrders);

    populator.populate(miraklCreatedOrders, order);

    verify(order).setCreatedOrdersJSON(CREATED_ORDERS_JSON);
    verify(consignmentModelConverter).convert(miraklOrder1, consignment1);
    verify(consignmentModelConverter).convert(miraklOrder2, consignment2);
    verifyNoMoreInteractions(consignmentModelConverter);
  }

}
