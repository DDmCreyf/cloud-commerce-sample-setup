package com.mirakl.hybris.core.order.attributes;

import static com.google.common.collect.Sets.newHashSet;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMarketplaceConsignmentsDynamicHandlerTest {

  @InjectMocks
  private DefaultMarketplaceConsignmentsDynamicHandler handler;

  @Mock
  private ConsignmentModel consignment;

  @Mock
  private MarketplaceConsignmentModel marketplaceConsignment1, marketplaceConsignment2;

  @Mock
  private OrderModel order;

  @Test
  public void getsMarketplaceConsignments() {
    when(order.getConsignments()).thenReturn(newHashSet(consignment, marketplaceConsignment1, marketplaceConsignment2));

    Set<MarketplaceConsignmentModel> marketplaceConsignments = handler.get(order);

    assertThat(marketplaceConsignments).containsOnly(marketplaceConsignment1, marketplaceConsignment2);
  }

  @Test
  public void getsEmptySetWhenNoMarketplaceConsignments() {
    when(order.getConsignments()).thenReturn(newHashSet(consignment));

    Set<MarketplaceConsignmentModel> marketplaceConsignments = handler.get(order);

    assertThat(marketplaceConsignments).isEmpty();
  }

  @Test
  public void getsEmptySetWhenNoConsignments() {
    when(order.getConsignments()).thenReturn(null);

    Set<MarketplaceConsignmentModel> marketplaceConsignments = handler.get(order);

    assertThat(marketplaceConsignments).isEmpty();
  }

}
