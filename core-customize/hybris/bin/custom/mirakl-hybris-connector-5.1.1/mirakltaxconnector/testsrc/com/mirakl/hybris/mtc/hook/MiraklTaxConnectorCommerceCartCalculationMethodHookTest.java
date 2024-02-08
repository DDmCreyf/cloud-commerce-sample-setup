package com.mirakl.hybris.mtc.hook;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.order.services.ShippingFeeService;
import com.mirakl.hybris.core.order.strategies.ShippingZoneStrategy;
import com.mirakl.hybris.core.promotions.strategies.MiraklPromotionsActivationStrategy;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;
import com.mirakl.hybris.mtc.strategies.MiraklTaxConnectorActivationStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklTaxConnectorCommerceCartCalculationMethodHookTest {

  private static final String DEFAULT_DELIVERY_COUNTRY_ISO_CODE = "US";
  private static final String MARSHALLED_SHIPPING_FEES_JSON = "marshalled-shipping-fees-json";

  @InjectMocks
  private MiraklTaxConnectorCommerceCartCalculationMethodHook hook;
  @Mock
  private JsonMarshallingService jsonMarshallingService;
  @Mock
  private ShippingFeeService shippingFeeService;
  @Mock
  private CommerceCartParameter cartParameter;
  @Mock
  private CountryModel country;
  @Mock
  private CartModel cart;
  @Mock
  private CurrencyModel currency;
  @Mock
  private MiraklOrderShippingFees shippingFees;
  @Mock
  private AbstractOrderEntryModel marketplaceEntry;
  @Mock
  private MiraklPromotionsActivationStrategy miraklPromotionsActivationStrategy;
  @Mock
  private MiraklTaxConnectorActivationStrategy miraklTaxConnectorActivationStrategy;
  @Mock
  private ShippingZoneStrategy shippingZoneStrategy;

  @Before
  public void setUp() {
    when(cartParameter.getCart()).thenReturn(cart);
    when(cart.getMarketplaceEntries()).thenReturn(singletonList(marketplaceEntry));
    when(shippingZoneStrategy.getEstimatedShippingZoneCode(cart)).thenReturn(DEFAULT_DELIVERY_COUNTRY_ISO_CODE);
    when(shippingFeeService.getShippingFees(cartParameter.getCart(), DEFAULT_DELIVERY_COUNTRY_ISO_CODE)).thenReturn(shippingFees);
    when(jsonMarshallingService.toJson(shippingFees)).thenReturn(MARSHALLED_SHIPPING_FEES_JSON);
    when(miraklPromotionsActivationStrategy.isMiraklPromotionsEnabled()).thenReturn(false);
  }

  @Test
  public void shouldCallMiraklWhenMiraklTaxConnectorEnabled() {
    when(miraklTaxConnectorActivationStrategy.isMiraklTaxConnectorComputation(cart)).thenReturn(true);

    hook.beforeCalculate(cartParameter);

    verify(shippingFeeService).getShippingFees(cartParameter.getCart(), DEFAULT_DELIVERY_COUNTRY_ISO_CODE);
    verify(cart).setCartCalculationJSON(MARSHALLED_SHIPPING_FEES_JSON);
  }

  @Test
  public void shouldNotCallMiraklWhenMiraklTaxConnectorDisabled() {
    when(miraklTaxConnectorActivationStrategy.isMiraklTaxConnectorComputation(cart)).thenReturn(false);

    hook.beforeCalculate(cartParameter);

    verifyZeroInteractions(shippingFeeService);
    verify(cart, never()).getMarketplaceEntries();
  }

  @Test
  public void shouldIgnoreMiraklTaxConnectorIfNoDefaultDeliveryDefined() {
    when(shippingZoneStrategy.getEstimatedShippingZoneCode(cart)).thenReturn(null);
    when(miraklTaxConnectorActivationStrategy.isMiraklTaxConnectorComputation(cart)).thenReturn(true);

    hook.beforeCalculate(cartParameter);

    verifyZeroInteractions(shippingFeeService);
  }
}
