package com.mirakl.hybris.core.ordersplitting.attributes;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ConsignmentDeliveryModeDisplayNameDynamicHandlerTest {

  private static final String MARKETPLACE_DELIVERY_MODE = "Premium Delivery";
  private static final String VANILLA_DELIVERY_MODE = "Standard";

  @Mock
  private MarketplaceConsignmentModel marketplaceConsignment;

  @Mock
  private ConsignmentModel vanillaConsignment;

  @Mock
  private DeliveryModeModel deliveryMode;

  @InjectMocks
  private ConsignmentDeliveryModeDisplayNameDynamicHandler testObj;

  @Test
  public void getDeliveryModeForMarketplaceConsignment() throws Exception {
    when(marketplaceConsignment.getShippingTypeLabel()).thenReturn(MARKETPLACE_DELIVERY_MODE);

    String deliveryMode = testObj.get(marketplaceConsignment);

    assertThat(deliveryMode).isEqualTo(MARKETPLACE_DELIVERY_MODE);
  }

  @Test
  public void getDeliveryModeForMarketplaceConsignmentWhenNull() throws Exception {
    when(marketplaceConsignment.getShippingTypeLabel()).thenReturn(null);
    when(marketplaceConsignment.getDeliveryMode()).thenReturn(deliveryMode);
    when(deliveryMode.getName()).thenReturn(VANILLA_DELIVERY_MODE);

    String deliveryMode = testObj.get(marketplaceConsignment);

    assertThat(deliveryMode).isEqualTo(VANILLA_DELIVERY_MODE);
  }

  @Test
  public void getDeliveryModeForVanillaConsignment() throws Exception {
    when(vanillaConsignment.getDeliveryMode()).thenReturn(deliveryMode);
    when(deliveryMode.getName()).thenReturn(VANILLA_DELIVERY_MODE);

    String deliveryMode = testObj.get(vanillaConsignment);

    assertThat(deliveryMode).isEqualTo(VANILLA_DELIVERY_MODE);
  }

}
