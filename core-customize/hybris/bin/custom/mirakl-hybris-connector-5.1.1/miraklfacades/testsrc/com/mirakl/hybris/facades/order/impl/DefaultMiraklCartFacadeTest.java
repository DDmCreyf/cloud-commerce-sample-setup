package com.mirakl.hybris.facades.order.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.strategies.OfferCodeGenerationStrategy;
import com.mirakl.hybris.facades.product.OfferFacade;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultMiraklCartFacadeTest {

  private static final String OFFER_CODE = "offerCode";
  private static final String PRODUCT_CODE = "productCode";
  private static final int REQUESTED_QUANTITY = 1;

  @InjectMocks
  private DefaultMiraklCartFacade cartFacade;

  @Mock
  private OfferFacade offerFacade;

  @Mock
  private ProductService productService;

  @Mock
  private CartService cartService;

  @Mock
  private CommerceCartService commerceCartService;

  @Mock
  private OfferCodeGenerationStrategy offerCodeGenerationStrategy;

  @Mock
  private Converter<CommerceCartModification, CartModificationData> cartModificationConverter;

  @Mock
  private OfferModel offer;

  @Mock
  private ProductModel product;

  @Mock
  private CartModel cart;

  @Mock
  private CommerceCartModification commerceCartModification;

  @Mock
  private CartModificationData cartModificationData;

  @Captor
  private ArgumentCaptor<CommerceCartParameter> commerceCartParameterCaptor;

  @Before
  public void setUp() throws Exception {
    cartFacade.setCartModificationConverter(cartModificationConverter);

    when(offerFacade.getOfferForCode(OFFER_CODE)).thenReturn(offer);
    when(offer.getProductCode()).thenReturn(PRODUCT_CODE);
    when(productService.getProductForCode(PRODUCT_CODE)).thenReturn(product);
    when(cartService.getSessionCart()).thenReturn(cart);
    when(commerceCartService.addToCart(any(CommerceCartParameter.class))).thenReturn(commerceCartModification);
    when(cartModificationConverter.convert(commerceCartModification)).thenReturn(cartModificationData);
    when(offerCodeGenerationStrategy.isOfferCode(OFFER_CODE)).thenReturn(true);
  }

  @Test
  public void shouldAddOfferToCart() throws CommerceCartModificationException {
    CartModificationData cartModification = cartFacade.addToCart(OFFER_CODE, REQUESTED_QUANTITY);

    assertThat(cartModification).isNotNull();
    verify(offerFacade).getOfferForCode(OFFER_CODE);
    verify(productService).getProductForCode(PRODUCT_CODE);
    verify(commerceCartService).addToCart(commerceCartParameterCaptor.capture());
    CommerceCartParameter commerceCartParameter = commerceCartParameterCaptor.getValue();
    assertThat(commerceCartParameter.getOffer()).isEqualTo(offer);
    assertThat(commerceCartParameter.getProduct()).isEqualTo(product);
    assertThat(commerceCartParameter.getQuantity()).isEqualTo(REQUESTED_QUANTITY);
  }

  @Test
  public void shouldAddProductToCart() throws CommerceCartModificationException {
    CartModificationData cartModification = cartFacade.addToCart(PRODUCT_CODE, REQUESTED_QUANTITY);

    assertThat(cartModification).isNotNull();
    verify(offerFacade, never()).getOfferForCode(OFFER_CODE);
    verify(productService).getProductForCode(PRODUCT_CODE);
    verify(commerceCartService).addToCart(commerceCartParameterCaptor.capture());
    CommerceCartParameter commerceCartParameter = commerceCartParameterCaptor.getValue();
    assertThat(commerceCartParameter.getOffer()).isNull();
    assertThat(commerceCartParameter.getProduct()).isEqualTo(product);
    assertThat(commerceCartParameter.getQuantity()).isEqualTo(REQUESTED_QUANTITY);
  }

}
