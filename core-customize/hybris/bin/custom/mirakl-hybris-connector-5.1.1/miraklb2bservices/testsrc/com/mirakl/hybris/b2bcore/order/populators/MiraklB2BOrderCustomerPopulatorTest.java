package com.mirakl.hybris.b2bcore.order.populators;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.order.MiraklCustomerShippingAddress;
import com.mirakl.client.mmp.domain.order.MiraklOrderCustomer;
import com.mirakl.client.mmp.domain.order.customerorganization.MiraklCustomerOrganization;
import com.mirakl.hybris.b2bcore.order.strategies.B2BUnitBillingAddressStrategy;
import com.mirakl.hybris.beans.OrganizationData;
import com.mirakl.hybris.core.user.strategies.MiraklCustomerOrganizationResolvingStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.InvoicePaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Copyright (C) 2017 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklB2BOrderCustomerPopulatorTest {

  @Mock
  private B2BUnitBillingAddressStrategy b2BUnitBillingAddressStrategy;

  @Mock
  private MiraklCustomerOrganizationResolvingStrategy organizationResolvingStrategy;

  @Mock
  private OrderModel order;

  @Mock
  private AddressModel deliveryAddress, paymentAddress, unitAddress;

  @Mock
  private CreditCardPaymentInfoModel creditCardPaymentInfo;

  @Mock
  private InvoicePaymentInfoModel invoicePaymentInfo;

  @Mock
  private MiraklCustomerShippingAddress miraklDeliveryAddress, miraklPaymentAddress, miraklUnitAddress;

  @Mock
  private Converter<AddressModel, MiraklCustomerShippingAddress> miraklCustomerAddressConverter;

  @Mock
  private Converter<OrganizationData, MiraklCustomerOrganization> miraklCustomerOrganizationConverter;

  @Mock
  private B2BUnitModel b2bUnit;

  @Mock
  private UserModel user;

  @Mock
  private OrganizationData organizationData;

  @Mock
  private MiraklCustomerOrganization miraklCustomerOrganization;

  @InjectMocks
  private MiraklB2BOrderCustomerPopulator testObj;

  @Before
  public void setUp() {
    when(order.getUnit()).thenReturn(b2bUnit);
    when(order.getDeliveryAddress()).thenReturn(deliveryAddress);
    when(b2BUnitBillingAddressStrategy.getBillingAddressForUnit(b2bUnit)).thenReturn(unitAddress);
    when(order.getUser()).thenReturn(user);
    when(organizationResolvingStrategy.resolveUserOrganization(user)).thenReturn(organizationData);
    when(miraklCustomerOrganizationConverter.convert(organizationData)).thenReturn(miraklCustomerOrganization);
    when(order.getPaymentInfo()).thenReturn(creditCardPaymentInfo);
    when(order.getPaymentAddress()).thenReturn(paymentAddress);
    when(miraklCustomerAddressConverter.convert(deliveryAddress)).thenReturn(miraklDeliveryAddress);
    when(miraklCustomerAddressConverter.convert(paymentAddress)).thenReturn(miraklPaymentAddress);
    when(miraklCustomerAddressConverter.convert(unitAddress)).thenReturn(miraklUnitAddress);
  }

  @Test
  public void setCustomerAddresses() throws Exception {
    MiraklOrderCustomer output = new MiraklOrderCustomer();

    testObj.setCustomerAddresses(output, order);

    assertThat(output.getShippingAddress()).isEqualTo(miraklDeliveryAddress);
    assertThat(output.getBillingAddress()).isEqualTo(miraklPaymentAddress);
  }

  @Test
  public void setCustomerAddressesForInvoicePayment() throws Exception {
    MiraklOrderCustomer output = new MiraklOrderCustomer();
    when(order.getPaymentInfo()).thenReturn(invoicePaymentInfo);

    testObj.setCustomerAddresses(output, order);

    assertThat(output.getShippingAddress()).isEqualTo(miraklDeliveryAddress);
    assertThat(output.getBillingAddress()).isEqualTo(miraklUnitAddress);
  }

  @Test
  public void setCustomerOrganization() throws Exception {
    MiraklOrderCustomer output = new MiraklOrderCustomer();

    testObj.setCustomerAddresses(output, order);

    assertThat(output.getOrganization()).isEqualTo(miraklCustomerOrganization);
  }

  @Test
  public void handlesNullOrganization() throws Exception {
    when(organizationResolvingStrategy.resolveUserOrganization(user)).thenReturn(null);
    MiraklOrderCustomer output = new MiraklOrderCustomer();

    testObj.setCustomerAddresses(output, order);

    assertThat(output.getOrganization()).isNull();
  }

}
