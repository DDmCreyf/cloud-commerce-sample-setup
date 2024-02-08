package com.mirakl.hybris.facades.order.converters.populator;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import com.mirakl.hybris.core.order.strategies.MarketplaceConsignmentMessagesStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.enums.MiraklOrderStatus;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(value = MockitoJUnitRunner.class)
public class MarketplaceConsignmentPopulatorTest {

  public static final String SHIPPING_TYPE_LABEL = "shipping_type_label";
  public static final String MIRAKL_ORDER_STATUS_CLOSED_LABEL = "mirakl_order_status_closed_label";

  @Mock
  private EnumerationService enumerationService;

  @Mock
  private MarketplaceConsignmentModel marketplaceConsignmentModel;

  @Mock
  private ConsignmentModel operatorConsignmentModel;

  @Mock
  private MarketplaceConsignmentMessagesStrategy marketplaceConsignmentMessagesStrategy;

  @InjectMocks
  MarketplaceConsignmentPopulator testObj;

  @Before
  public void setUp() throws Exception {
    when(marketplaceConsignmentModel.getCanEvaluate()).thenReturn(true);
    when(marketplaceConsignmentModel.getShippingTypeLabel()).thenReturn(SHIPPING_TYPE_LABEL);
    when(marketplaceConsignmentModel.getMiraklOrderStatus()).thenReturn(MiraklOrderStatus.CLOSED);
    when(enumerationService.getEnumerationName(MiraklOrderStatus.CLOSED)).thenReturn(MIRAKL_ORDER_STATUS_CLOSED_LABEL);
  }

  @Test
  public void testPopulate() throws Exception {
    ConsignmentData target = new ConsignmentData();
    testObj.populate(marketplaceConsignmentModel, target);

    verify(marketplaceConsignmentModel).getShippingTypeLabel();
    verify(marketplaceConsignmentModel).getMiraklOrderStatus();
    assertTrue(target.getCanEvaluate());
    assertThat(target.getShippingModeLabel()).isEqualTo(SHIPPING_TYPE_LABEL);
    assertThat(target.getMarketplaceStatus()).isEqualTo(MiraklOrderStatus.CLOSED.getCode());
    assertThat(target.getMarketplaceStatusLabel()).isEqualTo(MIRAKL_ORDER_STATUS_CLOSED_LABEL);
  }

  @Test
  public void testPopulateWithOperatorConsignment() {
    ConsignmentData target = new ConsignmentData();
    testObj.populate(operatorConsignmentModel, target);

    verify(marketplaceConsignmentModel, times(0)).getShippingTypeLabel();
    verify(marketplaceConsignmentModel, times(0)).getMiraklOrderStatus();
    assertThat(target.getShippingModeLabel()).isNull();
    assertThat(target.getMarketplaceStatus()).isNull();
  }
}
