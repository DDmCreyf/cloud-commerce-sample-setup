package com.mirakl.hybris.mtc.services.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.mtc.services.MiraklTaxConnectorShippingFeeService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklTaxConnectorShippingOptionsServiceTest {

  @InjectMocks
  @Spy
  private DefaultMiraklTaxConnectorShippingOptionsService testObj;
  @Mock
  private MiraklTaxConnectorShippingFeeService shippingFeeService;
  @Mock
  private AbstractOrderModel order;
  @Mock
  private MiraklOrderShippingFees shippingFees;
  @Mock
  private ModelService modelService;

  @Before
  public void setUp() {
    when(shippingFeeService.getShippingFeesWithTaxes(order)).thenReturn(shippingFees);
  }

  @Test
  public void setShippingOptions() {
    testObj.setShippingOptions(order);

    verify(testObj).setShippingFeesForOrder(order, shippingFees);
  }
}
