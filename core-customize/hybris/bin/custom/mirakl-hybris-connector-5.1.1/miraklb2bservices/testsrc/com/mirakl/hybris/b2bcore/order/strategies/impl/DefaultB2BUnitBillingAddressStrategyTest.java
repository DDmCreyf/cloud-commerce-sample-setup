package com.mirakl.hybris.b2bcore.order.strategies.impl;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.AddressModel;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultB2BUnitBillingAddressStrategyTest {

  @Mock
  private B2BUnitModel b2bUnit;

  @Mock
  private AddressModel billingAddress, otherAddress;

  @InjectMocks
  private DefaultB2BUnitBillingAddressStrategy testObj;

  @Test
  public void getBillingAddressForUnitIfAvailable() throws Exception {
    when(b2bUnit.getBillingAddress()).thenReturn(billingAddress);

    AddressModel output = testObj.getBillingAddressForUnit(b2bUnit);

    assertThat(output).isEqualTo(billingAddress);
  }

  @Test
  public void getBillingAddressForUnitIfNotAvailable() throws Exception {
    when(b2bUnit.getAddresses()).thenReturn(asList(billingAddress, otherAddress));

    AddressModel output = testObj.getBillingAddressForUnit(b2bUnit);

    assertThat(output).isEqualTo(billingAddress);
  }

  @Test(expected = IllegalStateException.class)
  public void getBillingAddressForUnitShouldThrowException() throws Exception {
    testObj.getBillingAddressForUnit(b2bUnit);
  }

}
