package com.mirakl.hybris.facades.order.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeError;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.order.services.MiraklOrderService;
import com.mirakl.hybris.core.order.services.ShippingFeeService;
import com.mirakl.hybris.core.order.services.ShippingOptionsService;
import com.mirakl.hybris.facades.shipping.data.ShippingOfferDiscrepancyData;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultShippingFacadeTest {

  private static final double CART_SUB_TOTAL_PRICE_BEFORE = 10d;
  private static final double CART_SUB_TOTAL_PRICE_AFTER = 20d;

  @InjectMocks
  private DefaultShippingFacade testObj;
  @Mock
  private ShippingOptionsService shippingOptionsService;
  @Mock
  private ShippingFeeService shippingFeeService;
  @Mock
  private CartService cartService;
  @Mock
  private ModelService modelService;
  @Mock
  private CommerceCheckoutService commerceCheckoutService;
  @Mock
  private Converter<AbstractOrderEntryModel, ShippingOfferDiscrepancyData> offerDiscrepancyConverter;
  @Mock
  private CartModel cart;
  @Mock
  private CartData cartData;
  @Mock
  private AbstractOrderEntryModel firstOrderEntry, secondOrderEntry;
  @Mock
  private ShippingOfferDiscrepancyData offerDiscrepancy;
  @Mock
  private MiraklOrderShippingFees shippingFees;
  @Mock
  private MiraklOrderShippingFeeError feeError;
  @Mock
  private MiraklOrderShippingFeeOffer feeOffer;
  @Mock
  private MiraklOrderService miraklOrderService;
  @Captor
  private ArgumentCaptor<CommerceCheckoutParameter> checkoutParameterArgumentCaptor;

  @Before
  public void setUp() {
    when(cartService.hasSessionCart()).thenReturn(true);
    when(cartService.getSessionCart()).thenReturn(cart);
    when(cart.getEntries()).thenReturn(asList(firstOrderEntry, secondOrderEntry));
    when(offerDiscrepancyConverter.convertAllIgnoreExceptions(asList(firstOrderEntry, secondOrderEntry)))
        .thenReturn(asList(offerDiscrepancy, null));
  }

  @Test
  public void updateAvailableShippingOptions() throws CalculationException {
    testObj.updateAvailableShippingOptions();

    verify(cartService).hasSessionCart();
    verify(cartService).getSessionCart();
    verify(shippingOptionsService).setShippingOptions(cart);

    verify(commerceCheckoutService).calculateCart(checkoutParameterArgumentCaptor.capture());
    CommerceCheckoutParameter checkoutParameter = checkoutParameterArgumentCaptor.getValue();
    assertThat(checkoutParameter.getCart()).isEqualTo(cart);
    assertThat(checkoutParameter.isEnableHooks()).isTrue();
  }

  @Test(expected = IllegalArgumentException.class)
  public void setAvailableShippingOptionsThrowsIllegalArgumentExceptionIfNoSessionCartIsFound() {
    when(cartService.hasSessionCart()).thenReturn(false);

    testObj.updateOffersPrice();
  }

  @Test
  public void getsOfferDiscrepanciesWithoutNullValues() {
    List<ShippingOfferDiscrepancyData> result = testObj.getOfferDiscrepancies();

    assertThat(result).containsOnly(offerDiscrepancy);
    verify(offerDiscrepancyConverter).convertAllIgnoreExceptions(asList(firstOrderEntry, secondOrderEntry));
  }

  @Test(expected = IllegalArgumentException.class)
  public void getOfferDiscrepanciesThrowsIllegalArgumentExceptionIfNoSessionCartIsFound() {
    when(cartService.hasSessionCart()).thenReturn(false);

    testObj.getOfferDiscrepancies();
  }

  @Test
  public void removesInvalidOffers() throws CalculationException {
    when(shippingFeeService.getStoredShippingFees(cart)).thenReturn(shippingFees);
    when(shippingFees.getErrors()).thenReturn(singletonList(feeError));
    when(cart.getMarketplaceEntries()).thenReturn(asList(firstOrderEntry, secondOrderEntry));
    when(shippingFeeService.extractAllShippingFeeOffers(shippingFees)).thenReturn(singletonList(feeOffer));

    testObj.removeInvalidOffers();

    verify(shippingFeeService).getStoredShippingFees(cart);
    verify(cart).getMarketplaceEntries();
    verify(shippingFeeService).extractAllShippingFeeOffers(shippingFees);
    verify(shippingOptionsService).adjustOfferQuantities(asList(firstOrderEntry, secondOrderEntry),
        singletonList(feeOffer));
    verify(shippingOptionsService).removeOfferEntriesWithError(cart, singletonList(feeError));

    verify(commerceCheckoutService).calculateCart(checkoutParameterArgumentCaptor.capture());
    CommerceCheckoutParameter checkoutParameter = checkoutParameterArgumentCaptor.getValue();
    assertThat(checkoutParameter.getCart()).isEqualTo(cart);
    assertThat(checkoutParameter.isEnableHooks()).isTrue();
  }

  @Test(expected = IllegalArgumentException.class)
  public void removeInvalidOffersThrowsIllegalArgumentExceptionIfNoSessionCartIsFound() {
    when(cartService.hasSessionCart()).thenReturn(false);

    testObj.removeInvalidOffers();
  }

  @Test
  public void removeInvalidOffersDoesNotChangeCartIfNoShippingFeesForCartAreFound() {
    when(shippingFeeService.getStoredShippingFees(cart)).thenReturn(null);

    testObj.removeInvalidOffers();

    verify(shippingFeeService).getStoredShippingFees(cart);
    verifyNoMoreInteractions(cart);
  }

  @Test(expected = IllegalArgumentException.class)
  public void adjustShippingQuantityThrowsIllegalArgumentExceptionIfNoSessionCartIsFound() {
    when(cartService.hasSessionCart()).thenReturn(false);

    testObj.removeInvalidOffers();
  }

  @Test
  public void updateOffersPrice() {
    when(miraklOrderService.updateOffersPrice(cart, shippingFees)).thenReturn(true);
    when(shippingFeeService.getStoredShippingFees(cart)).thenReturn(shippingFees);

    boolean output = testObj.updateOffersPrice();

    verify(miraklOrderService).updateOffersPrice(cart, shippingFees);
    assertThat(output).isEqualTo(true);
  }

  @Test
  public void updateOffersPriceWhenShippingFeesUnavailable() {
    when(shippingFeeService.getStoredShippingFees(cart)).thenReturn(null);

    boolean output = testObj.updateOffersPrice();

    assertThat(output).isFalse();
  }

  @Test
  public void updateAvailableShippingOptionsShouldReturnTrue() throws CalculationException {
    when(cart.getSubtotal()).thenReturn(CART_SUB_TOTAL_PRICE_BEFORE).thenReturn(CART_SUB_TOTAL_PRICE_AFTER);

    boolean updated = testObj.updateAvailableShippingOptions();

    verify(cartService).hasSessionCart();
    verify(cartService).getSessionCart();
    verify(shippingOptionsService).setShippingOptions(cart);
    verify(commerceCheckoutService).calculateCart(checkoutParameterArgumentCaptor.capture());
    CommerceCheckoutParameter checkoutParameter = checkoutParameterArgumentCaptor.getValue();
    assertThat(checkoutParameter.getCart()).isEqualTo(cart);
    assertThat(checkoutParameter.isEnableHooks()).isTrue();
    assertThat(updated).isTrue();
  }
}
