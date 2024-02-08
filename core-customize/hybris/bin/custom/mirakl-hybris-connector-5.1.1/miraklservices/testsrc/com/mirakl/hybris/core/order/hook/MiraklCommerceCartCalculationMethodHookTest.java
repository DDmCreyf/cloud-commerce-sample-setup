package com.mirakl.hybris.core.order.hook;

import static java.util.Collections.singletonList;
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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklCommerceCartCalculationMethodHookTest {
  private static final String DEFAULT_DELIVERY_COUNTRY_ISO_CODE = "fr";
  private static final String MARSHALLED_SHIPPING_FEES_JSON = "marshalled-shipping-fees-json";

  @InjectMocks
  private MiraklCommerceCartCalculationMethodHook hook;
  @Mock
  private JsonMarshallingService jsonMarshallingService;
  @Mock
  private ShippingFeeService shippingFeeService;
  @Mock
  private MiraklPromotionsActivationStrategy miraklPromotionsActivationStrategy;
  @Mock
  private CommerceCartParameter cartParameter;
  @Mock
  private CountryModel country;
  @Mock
  private CartModel cart;
  @Mock
  private MiraklOrderShippingFees shippingFees;
  @Mock
  private AbstractOrderEntryModel marketplaceEntry;
  @Mock
  private ShippingZoneStrategy shippingZoneStrategy;

  @Before
  public void setUp() throws Exception {
    when(cartParameter.getCart()).thenReturn(cart);
    when(shippingFeeService.getShippingFees(cartParameter.getCart(), DEFAULT_DELIVERY_COUNTRY_ISO_CODE)).thenReturn(shippingFees);
    when(jsonMarshallingService.toJson(shippingFees)).thenReturn(MARSHALLED_SHIPPING_FEES_JSON);
    when(cart.getMarketplaceEntries()).thenReturn(singletonList(marketplaceEntry));
    when(cartParameter.getCart()).thenReturn(cart);
    when(shippingZoneStrategy.getEstimatedShippingZoneCode(cart)).thenReturn(DEFAULT_DELIVERY_COUNTRY_ISO_CODE);
  }

  @Test
  public void shouldCallMiraklWhenPromotionsEnabled() throws Exception {
    when(miraklPromotionsActivationStrategy.isMiraklPromotionsEnabled()).thenReturn(true);

    hook.beforeCalculate(cartParameter);

    verify(shippingFeeService).getShippingFees(cartParameter.getCart(), DEFAULT_DELIVERY_COUNTRY_ISO_CODE);
    verify(cart).setCartCalculationJSON(MARSHALLED_SHIPPING_FEES_JSON);
  }

  @Test
  public void shouldNotCallMiraklWhenPromotionsDisabled() throws Exception {
    when(miraklPromotionsActivationStrategy.isMiraklPromotionsEnabled()).thenReturn(false);

    hook.beforeCalculate(cartParameter);

    verifyZeroInteractions(shippingFeeService);
    verifyZeroInteractions(cart);
  }

  @Test
  public void shouldIgnoreMiraklPromotionsIfNoDefaultDeliveryDefined() throws Exception {
    when(shippingZoneStrategy.getEstimatedShippingZoneCode(cart)).thenReturn(null);
    when(miraklPromotionsActivationStrategy.isMiraklPromotionsEnabled()).thenReturn(true);

    hook.beforeCalculate(cartParameter);

    verifyZeroInteractions(shippingFeeService);
  }

}
