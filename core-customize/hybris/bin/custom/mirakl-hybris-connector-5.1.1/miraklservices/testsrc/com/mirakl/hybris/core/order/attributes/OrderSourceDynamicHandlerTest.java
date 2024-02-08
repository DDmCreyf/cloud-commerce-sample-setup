package com.mirakl.hybris.core.order.attributes;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.enums.OrderSource;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderSourceDynamicHandlerTest {

  @Mock
  private OrderModel order;

  @Mock
  private AbstractOrderEntryModel marketplaceEntry;

  @InjectMocks
  private OrderSourceDynamicHandler testObj;

  @Test
  public void sourceForMarketplaceConsignments() {
    when(order.isMarketplaceOrder()).thenReturn(true);

    OrderSource output = testObj.get(order);

    assertThat(output).isEqualTo(OrderSource.MARKETPLACE);
  }

  @Test
  public void sourceForMixedConsignments() {
    when(order.isMarketplaceOrder()).thenReturn(false);
    when(order.getMarketplaceEntries()).thenReturn(Arrays.asList(marketplaceEntry));

    OrderSource output = testObj.get(order);

    assertThat(output).isEqualTo(OrderSource.MIXED);
  }

  @Test
  public void sourceForOperatorConsignments() {
    when(order.isMarketplaceOrder()).thenReturn(false);
    when(order.getMarketplaceEntries()).thenReturn(java.util.Collections.<AbstractOrderEntryModel>emptyList());

    OrderSource output = testObj.get(order);

    assertThat(output).isEqualTo(OrderSource.OPERATOR);
  }

}
