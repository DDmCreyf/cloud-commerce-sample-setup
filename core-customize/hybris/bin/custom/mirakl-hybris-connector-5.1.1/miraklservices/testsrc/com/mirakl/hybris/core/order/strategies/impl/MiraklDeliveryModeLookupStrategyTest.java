package com.mirakl.hybris.core.order.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.delivery.dao.CountryZoneDeliveryModeDao;
import de.hybris.platform.commerceservices.delivery.dao.PickupDeliveryModeDao;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class MiraklDeliveryModeLookupStrategyTest {

  private static final String DEFAULT_FREE_DELIVERY_MODE = "mirakl-default-shipping";

  @Mock
  private DeliveryService deliveryService;
  @Mock
  private AbstractOrderModel order;
  @Mock
  private DeliveryModeModel marketplaceFreeDeliveryMode;
  @Mock
  private CountryZoneDeliveryModeDao countryZoneDeliveryModeDao;
  @Mock
  private PickupDeliveryModeDao pickupDeliveryModeDao;

  @InjectMocks
  private MiraklDeliveryModeLookupStrategy testObj;

  @Before
  public void setUp() throws Exception {
    testObj = new MiraklDeliveryModeLookupStrategy();
    testObj.setDefaultFreeDeliveryModeCode(DEFAULT_FREE_DELIVERY_MODE);
    testObj.setDeliveryService(deliveryService);
    testObj.setCountryZoneDeliveryModeDao(countryZoneDeliveryModeDao);
    testObj.setPickupDeliveryModeDao(pickupDeliveryModeDao);

    when(deliveryService.getDeliveryModeForCode(DEFAULT_FREE_DELIVERY_MODE)).thenReturn(marketplaceFreeDeliveryMode);
  }

  @Test
  public void shouldReturnOneDeliveryModeForMarketplaceOrder() {
    when(order.isMarketplaceOrder()).thenReturn(true);

    List<DeliveryModeModel> output = testObj.getSelectableDeliveryModesForOrder(order);

    assertThat(output).containsExactly(marketplaceFreeDeliveryMode);
  }

  @Test(expected = IllegalStateException.class)
  public void throwsExceptionIfMarketplaceDeliveryModeIsMissing() {
    when(order.isMarketplaceOrder()).thenReturn(true);
    when(deliveryService.getDeliveryModeForCode(DEFAULT_FREE_DELIVERY_MODE)).thenReturn(null);

    testObj.getSelectableDeliveryModesForOrder(order);
  }

  @Test
  public void shouldFallbackOnDefaultMethodForCompositeOrOperatorOrders() {
    when(order.isMarketplaceOrder()).thenReturn(false);

    List<DeliveryModeModel> output = testObj.getSelectableDeliveryModesForOrder(order);

    assertThat(output.contains(marketplaceFreeDeliveryMode)).isFalse();
  }

}
