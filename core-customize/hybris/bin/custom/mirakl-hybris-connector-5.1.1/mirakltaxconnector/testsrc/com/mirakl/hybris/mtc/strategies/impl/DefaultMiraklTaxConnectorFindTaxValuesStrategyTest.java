package com.mirakl.hybris.mtc.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.order.services.ShippingFeeService;
import com.mirakl.hybris.mtc.strategies.MiraklTaxConnectorActivationStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.util.TaxValue;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklTaxConnectorFindTaxValuesStrategyTest {

  @InjectMocks
  private DefaultMiraklTaxConnectorFindTaxValuesStrategy testObj;
  @Spy
  private List<AbstractOrderEntryModel> marketplaceEntries = new ArrayList<>();
  @Spy
  private List<MiraklOrderShippingFee> shippingFees = new ArrayList<>();
  @Spy
  private List<TaxValue> taxValues = new ArrayList<>();
  @Mock
  private MiraklTaxConnectorActivationStrategy miraklTaxConnectorActivationStrategy;
  @Mock
  private ShippingFeeService shippingFeeService;
  @Mock
  private AbstractOrderEntryModel marketplaceOrderEntry;
  @Mock
  private MiraklOrderShippingFees miraklOrderShippingFees;
  @Mock
  private CommerceCartParameter cartParameter;
  @Mock
  private CartModel cart;
  @Mock
  private CurrencyModel currency;
  @Mock
  private OrderModel order;
  @Mock
  private MiraklOrderShippingFee shippingFee;
  @Mock
  private TaxValue taxValue;

  @Before
  public void setUp() throws CalculationException {
    marketplaceEntries.add(marketplaceOrderEntry);
    shippingFees.add(shippingFee);
    taxValues.add(taxValue);
    when(marketplaceOrderEntry.getOrder()).thenReturn(order);
  }

  @Test
  public void shouldNotFindValuesIfNoJSON() throws CalculationException {
    when(shippingFeeService.getStoredShippingFeesWithCartCalculationFallback(order)).thenReturn(null);

    Collection<TaxValue> result = testObj.findTaxValues(marketplaceOrderEntry);

    assertThat(result).isEmpty();
  }
}
