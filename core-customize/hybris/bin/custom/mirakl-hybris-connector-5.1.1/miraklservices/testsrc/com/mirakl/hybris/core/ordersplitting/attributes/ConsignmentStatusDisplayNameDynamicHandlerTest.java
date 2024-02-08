package com.mirakl.hybris.core.ordersplitting.attributes;

import static com.mirakl.hybris.core.enums.MiraklOrderStatus.INCIDENT_CLOSED;
import static de.hybris.platform.basecommerce.enums.ConsignmentStatus.READY;
import static de.hybris.platform.basecommerce.enums.ConsignmentStatus.WAITING;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ConsignmentStatusDisplayNameDynamicHandlerTest {

  @Mock
  private MarketplaceConsignmentModel marketplaceConsignment;

  @Mock
  private ConsignmentModel vanillaConsignment;

  @InjectMocks
  private ConsignmentStatusDisplayNameDynamicHandler testObj;

  @Test
  public void getStatusForMarketplaceConsignment() throws Exception {
    when(marketplaceConsignment.getMiraklOrderStatus()).thenReturn(INCIDENT_CLOSED);

    String output = testObj.get(marketplaceConsignment);

    assertThat(output).isEqualTo(INCIDENT_CLOSED.getCode());
  }

  @Test
  public void getStatusForMarketplaceConsignmentWhenNull() throws Exception {
    when(marketplaceConsignment.getMiraklOrderStatus()).thenReturn(null);
    when(marketplaceConsignment.getStatus()).thenReturn(WAITING);


    String output = testObj.get(marketplaceConsignment);

    assertThat(output).isEqualTo(WAITING.getCode());
  }

  @Test
  public void getStatusForVanillaConsignment() throws Exception {
    when(vanillaConsignment.getStatus()).thenReturn(READY);

    String output = testObj.get(vanillaConsignment);

    assertThat(output).isEqualTo(READY.getCode());
  }
}
