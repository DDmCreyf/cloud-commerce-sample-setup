package com.mirakl.hybris.core.ordersplitting.attributes;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.apache.commons.lang.StringUtils;
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
public class ConsignmentShopDisplayNameDynamicHandlerTest {

  private static final String SHOP_NAME = "Foot Lucker";

  @Mock
  private MarketplaceConsignmentModel marketplaceConsignment;

  @Mock
  private ConsignmentModel vanillaConsignment;

  @InjectMocks
  ConsignmentShopDisplayNameDynamicHandler testObj;

  @Test
  public void shopNameForMarketplaceConsignment() throws Exception {
    when(marketplaceConsignment.getShopName()).thenReturn(SHOP_NAME);

    String output = testObj.get(marketplaceConsignment);

    assertThat(output).isEqualTo(SHOP_NAME);
  }

  @Test
  public void shopNameForVanillaConsignment() throws Exception {
    String output = testObj.get(vanillaConsignment);

    assertThat(output).isEqualTo(StringUtils.EMPTY);
  }

}
