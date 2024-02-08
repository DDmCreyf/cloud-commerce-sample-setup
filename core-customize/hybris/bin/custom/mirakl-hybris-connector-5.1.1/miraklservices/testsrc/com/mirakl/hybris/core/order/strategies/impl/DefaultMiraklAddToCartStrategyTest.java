package com.mirakl.hybris.core.order.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.CartAdjustment;
import com.mirakl.hybris.beans.OfferOrderingConditions;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.model.ShopModel;
import com.mirakl.hybris.core.order.daos.MiraklAbstractOrderEntryDao;
import com.mirakl.hybris.core.order.strategies.CommonMiraklCartStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultMiraklAddToCartStrategyTest {

  private static final int APPEND_AS_LAST = -1;
  private static final Long EMPTY_QUANTITY = 0L;
  private static final Long REQUESTED_QUANTITY = 4L;
  private static Boolean CREATE_NEW_ENTRY = Boolean.FALSE;

  @Spy
  @InjectMocks
  private DefaultMiraklAddToCartStrategy commerceAddToCartStrategy;

  @Mock
  private CartService cartService;
  @Mock
  private ProductService productService;
  @Mock
  private ModelService modelService;
  @Mock
  private CommonMiraklCartStrategy commonCartStrategy;
  @Mock
  private MiraklAbstractOrderEntryDao<CartEntryModel> miraklCartEntryDao;
  @Mock
  private Converter<OfferModel, OfferOrderingConditions> offerOrderingConditionsConverter;
  @Mock
  private CommerceCartCalculationStrategy commerceCartCalculationStrategy;
  @Mock
  private CommerceCartParameter cartParameter;
  @Mock
  private OfferModel offer;
  @Mock
  private ProductModel product;
  @Mock
  private CartModel cart;
  @Mock
  private CartEntryModel cartEntry, addedCartEntry;
  @Mock
  private StockLevelModel stockLevel;
  @Mock
  private ShopModel shop;
  @Mock
  private UnitModel unit;
  @Mock
  private Converter<OfferModel, AbstractOrderEntryModel> orderEntryConverter;
  @Mock
  private OfferOrderingConditions offerOrderingConditions;

  private CartAdjustment result;

  @Before
  public void setUp() throws Exception {
    result = new CartAdjustment();
    when(cartParameter.getCart()).thenReturn(cart);
    when(cartParameter.getOffer()).thenReturn(offer);
    when(cartParameter.getProduct()).thenReturn(product);
    when(cartParameter.getQuantity()).thenReturn(REQUESTED_QUANTITY);
    when(productService.getOrderableUnit(product)).thenReturn(unit);
    when(cartService.addNewEntry(eq(cart), eq(product), any(Long.class), eq(unit), eq(APPEND_AS_LAST),
        eq(CREATE_NEW_ENTRY))).thenReturn(addedCartEntry);
    when(commonCartStrategy.calculateCartAdjustment(cartParameter)).thenReturn(result);
    doNothing().when(commerceAddToCartStrategy).validateAddToCart(Mockito.eq(cartParameter));
  }

  @Test
  public void shouldAddOfferToCartWhenAdjustmentAllowed() throws CommerceCartModificationException {
    result.setAllowedQuantityChange(REQUESTED_QUANTITY);
    result.setStatus(CommerceCartModificationStatus.SUCCESS);

    CommerceCartModification cartModification = commerceAddToCartStrategy.addToCart(cartParameter);

    assertThat(cartModification.getQuantityAdded()).isEqualTo(REQUESTED_QUANTITY);
    assertThat(cartModification.getEntry()).isEqualTo(addedCartEntry);
    assertThat(cartModification.getStatusCode()).isEqualTo(CommerceCartModificationStatus.SUCCESS);
    assertThat(cartModification.getQuantity()).isEqualTo(REQUESTED_QUANTITY);
    verify(orderEntryConverter).convert(any(OfferModel.class), any(CartEntryModel.class));
    verify(cartService).addNewEntry(eq(cart), eq(product), eq(REQUESTED_QUANTITY), eq(unit),
        eq(APPEND_AS_LAST), eq(CREATE_NEW_ENTRY));
  }

  @Test
  public void shouldPartiallyAddOfferToCartWhenRequestedMoreThanAdjustmentAllowed() throws CommerceCartModificationException {
    long stockLevel = REQUESTED_QUANTITY - 1;
    result.setAllowedQuantityChange(stockLevel);
    result.setStatus(CommerceCartModificationStatus.LOW_STOCK);

    CommerceCartModification cartModification = commerceAddToCartStrategy.addToCart(cartParameter);

    assertThat(cartModification.getQuantityAdded()).isEqualTo(stockLevel);
    assertThat(cartModification.getEntry()).isEqualTo(addedCartEntry);
    assertThat(cartModification.getStatusCode()).isEqualTo(CommerceCartModificationStatus.LOW_STOCK);
    assertThat(cartModification.getQuantity()).isEqualTo(REQUESTED_QUANTITY);
    verify(orderEntryConverter).convert(any(OfferModel.class), any(CartEntryModel.class));
    verify(orderEntryConverter).convert(any(OfferModel.class), any(CartEntryModel.class));
    verify(cartService).addNewEntry(eq(cart), eq(product), eq(stockLevel), eq(unit),
        eq(APPEND_AS_LAST), eq(CREATE_NEW_ENTRY));
  }

  @Test
  public void shouldNotAddOfferToCartWhenNoAdjustmentAllowed() throws CommerceCartModificationException {
    result.setAllowedQuantityChange(EMPTY_QUANTITY);
    result.setStatus(CommerceCartModificationStatus.NO_STOCK);

    CommerceCartModification cartModification = commerceAddToCartStrategy.addToCart(cartParameter);

    assertThat(cartModification.getQuantityAdded()).isEqualTo(EMPTY_QUANTITY);
    assertThat(cartModification.getEntry()).isNotEqualTo(addedCartEntry);
    assertThat(cartModification.getStatusCode()).isEqualTo(CommerceCartModificationStatus.NO_STOCK);
    assertThat(cartModification.getQuantity()).isEqualTo(REQUESTED_QUANTITY);
    verify(cartService, never()).addNewEntry(any(CartModel.class), any(ProductModel.class), anyLong(), any(UnitModel.class),
        anyInt(), anyBoolean());
  }

}
