package com.mirakl.hybris.core.order.populators;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.order.MiraklCustomerShippingAddress;
import com.mirakl.client.mmp.domain.order.MiraklOrderCustomer;
import com.mirakl.hybris.core.order.strategies.MiraklCustomerIdDefinitionStrategy;
import com.mirakl.hybris.core.util.strategies.LocaleMappingStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklOrderCustomerPopulatorTest {

  private static final String CUSTOMER_ID = "customer-id";
  private static final String CUSTOMER_EMAIL_ADDRESS = "customerEmailAddress";
  private static final String FIRST_NAME = "firstName";
  private static final String LAST_NAME = "lastName";
  private static final String CUSTOMER_NAME = FIRST_NAME + " " + LAST_NAME;
  private static final Locale LANGUAGE_LOCALE = Locale.FRENCH;

  @InjectMocks
  private MiraklOrderCustomerPopulator testObj = new MiraklOrderCustomerPopulator();

  @Mock
  private CustomerNameStrategy customerNameStrategy;
  @Mock
  private Converter<AddressModel, MiraklCustomerShippingAddress> miraklCustomerAddressConverter;
  @Mock
  private LocaleMappingStrategy localeMappingStrategy;
  @Mock
  private MiraklCustomerIdDefinitionStrategy miraklCustomerIdDefinitionStrategy;
  @Mock
  private OrderModel order;
  @Mock
  private CustomerModel customer;
  @Mock
  private MiraklCustomerShippingAddress miraklCustomerBillingAddress, miraklCustomerShippingAddress;
  @Mock
  private AddressModel paymentAddress, deliveryAddress;
  @Mock
  private LanguageModel language;

  @Before
  public void setUp() {
    when(order.getUser()).thenReturn(customer);
    when(order.getDeliveryAddress()).thenReturn(deliveryAddress);
    when(order.getPaymentAddress()).thenReturn(paymentAddress);
    when(order.getLanguage()).thenReturn(language);

    when(customer.getName()).thenReturn(CUSTOMER_NAME);
    when(customer.getContactEmail()).thenReturn(CUSTOMER_EMAIL_ADDRESS);

    when(customerNameStrategy.splitName(CUSTOMER_NAME)).thenReturn(new String[]{FIRST_NAME, LAST_NAME});
    when(miraklCustomerAddressConverter.convert(paymentAddress)).thenReturn(miraklCustomerBillingAddress);
    when(miraklCustomerAddressConverter.convert(deliveryAddress)).thenReturn(miraklCustomerShippingAddress);
  }

  @Test
  public void shouldPopulateMiraklOrderCustomer() {
    when(localeMappingStrategy.mapToMiraklLocale(language)).thenReturn(LANGUAGE_LOCALE);
    when(miraklCustomerIdDefinitionStrategy.getMiraklCustomerId(customer)).thenReturn(CUSTOMER_ID);

    MiraklOrderCustomer result = new MiraklOrderCustomer();
    testObj.populate(order, result);

    assertThat(result.getEmail()).isEqualTo(CUSTOMER_EMAIL_ADDRESS);
    assertThat(result.getId()).isEqualTo(CUSTOMER_ID);
    assertThat(result.getFirstname()).isEqualTo(FIRST_NAME);
    assertThat(result.getLastname()).isEqualTo(LAST_NAME);
    assertThat(result.getLocale()).isEqualTo(LANGUAGE_LOCALE);
    assertThat(result.getBillingAddress()).isEqualTo(miraklCustomerBillingAddress);
    assertThat(result.getShippingAddress()).isEqualTo(miraklCustomerShippingAddress);
  }

  @Test
  public void shouldNotPopulateLocaleIfNoOrderLanguageFound() {
    when(order.getLanguage()).thenReturn(null);

    MiraklOrderCustomer result = new MiraklOrderCustomer();
    testObj.populate(order, result);

    assertThat(result.getLocale()).isNull();
    verify(localeMappingStrategy, never()).mapToMiraklLocale(any(LanguageModel.class));
  }

  @Test
  public void shouldNotPopulateLocaleIfNoLocaleFoundForOrderLanguage() {
    when(localeMappingStrategy.mapToMiraklLocale(language)).thenReturn(null);

    MiraklOrderCustomer result = new MiraklOrderCustomer();
    testObj.populate(order, result);

    assertThat(result.getLocale()).isNull();
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfFirstNameIsNull() {
    when(customerNameStrategy.splitName(CUSTOMER_NAME)).thenReturn(new String[]{null, LAST_NAME});

    testObj.populate(order, new MiraklOrderCustomer());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfLastNameIsNull() {
    when(customerNameStrategy.splitName(CUSTOMER_NAME)).thenReturn(new String[]{FIRST_NAME, null});

    testObj.populate(order, new MiraklOrderCustomer());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfOrderNull() {
    testObj.populate(null, new MiraklOrderCustomer());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfMiraklOrderCustomerNull() {
    testObj.populate(order, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfCustomerIsNull() {
    when(order.getUser()).thenReturn(null);

    testObj.populate(order, new MiraklOrderCustomer());
  }
}
