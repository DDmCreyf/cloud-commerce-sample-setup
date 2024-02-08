package com.mirakl.hybris.mtc.hook;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.order.services.ShippingFeeService;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;
import com.mirakl.hybris.mtc.strategies.MiraklTaxConnectorActivationStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklTaxConnectorCommerceUpdateCartEntryHookTest {

  @InjectMocks
  private MiraklTaxConnectorCommerceUpdateCartEntryHook testObj;

  private List<AbstractOrderEntryModel> marketplaceEntries = new ArrayList<>();
  @Mock
  private MiraklTaxConnectorActivationStrategy miraklTaxConnectorActivationStrategy;
  @Mock
  private JsonMarshallingService jsonMarshallingService;
  @Mock
  private ShippingFeeService shippingFeeService;
  @Mock
  private AbstractOrderEntryModel marketplaceOrderEntry;
  @Mock
  private CommerceCartParameter cartParameter;
  @Mock
  private CommerceCartModification result;
  @Mock
  private CartModel cart;
  @Mock
  private CurrencyModel currency;
  @Mock
  private OrderModel order;
  @Mock
  private MiraklOrderShippingFees shippingFees;

  @Before
  public void setUp() {
    marketplaceEntries.add(marketplaceOrderEntry);
    when(miraklTaxConnectorActivationStrategy.isMiraklTaxConnectorComputation(cart)).thenReturn(true);
    when(cartParameter.getCart()).thenReturn(cart);
  }

  @Test
  public void shouldDoNothingWhenTaxConnectorIsNotEnabled() {
    when(miraklTaxConnectorActivationStrategy.isMiraklTaxConnectorComputation(cart)).thenReturn(false);

    testObj.afterUpdateCartEntry(cartParameter, result);

    verify(cart, never()).getMarketplaceEntries();
    verifyZeroInteractions(result);
  }

}
