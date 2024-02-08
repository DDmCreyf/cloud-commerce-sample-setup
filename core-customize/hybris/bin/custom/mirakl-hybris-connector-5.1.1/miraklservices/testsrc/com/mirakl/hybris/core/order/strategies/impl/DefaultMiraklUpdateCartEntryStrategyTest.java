package com.mirakl.hybris.core.order.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.CartAdjustment;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.order.strategies.CommonMiraklCartStrategy;
import com.mirakl.hybris.core.product.services.OfferService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.commerceservices.strategies.ModifiableChecker;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultMiraklUpdateCartEntryStrategyTest {

  private static final int ENTRY_NUMBER = 1;
  private static final long REQUESTED_QUANTITY = 3L;
  private static final long ALLOWED_QUANTITY_CHANGE = 2L;
  private static final String STATUS = CommerceCartModificationStatus.SUCCESS;
  private static final String OFFER_ID = "offerId";

  @InjectMocks
  private DefaultMiraklUpdateCartEntryStrategy updateStrategy;

  @Mock
  private CommonMiraklCartStrategy commonCartStrategy;
  @Mock
  private CartService cartService;
  @Mock
  private OfferService offerService;
  @Mock
  private ModelService modelService;
  @Mock
  private CommerceStockService commerceStockService;
  @Mock
  private BaseStoreService baseStoreService;
  @Mock
  private CommerceCartCalculationStrategy cartCalculationStrategy;
  @Mock
  private ModifiableChecker<AbstractOrderEntryModel> entryOrderChecker;
  @Mock
  private CommerceCartParameter parameter;
  @Mock
  private CartModel cart;
  @Mock
  private CartEntryModel cartEntry, otherCartEntry;
  @Mock
  private OfferModel offer;
  @Mock
  private ProductModel product;
  @Mock
  private BaseStoreModel baseStore;

  private CartAdjustment addToCartResult;

  @Before
  public void setUp() throws Exception {
    addToCartResult = new CartAdjustment();
    addToCartResult.setAllowedQuantityChange(ALLOWED_QUANTITY_CHANGE);
    addToCartResult.setStatus(STATUS);
    when(commonCartStrategy.calculateCartAdjustment(parameter)).thenReturn(addToCartResult);
    when(parameter.getCart()).thenReturn(cart);
    when(parameter.getEntryNumber()).thenReturn((long) ENTRY_NUMBER);
    when(parameter.getQuantity()).thenReturn(REQUESTED_QUANTITY);
    when(cart.getEntries()).thenReturn(Arrays.<AbstractOrderEntryModel>asList(cartEntry, otherCartEntry));
    when(cartEntry.getEntryNumber()).thenReturn(ENTRY_NUMBER);
    when(cartEntry.getProduct()).thenReturn(product);
    when(cartEntry.getOfferId()).thenReturn(OFFER_ID);
    when(entryOrderChecker.canModify(cartEntry)).thenReturn(true);
    when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
  }

  @Test
  public void shouldFallbackToDefaultImplementationWhenNotAnOffer() throws Exception {
    when(cartEntry.getOfferId()).thenReturn(null);

    updateStrategy.updateQuantityForCartEntry(parameter);

    verifyZeroInteractions(commonCartStrategy);
  }

  @Test
  public void shouldUseAddToCartResult() throws Exception {
    CommerceCartModification modification = updateStrategy.updateQuantityForCartEntry(parameter);

    assertThat(modification.getStatusCode()).isEqualTo(addToCartResult.getStatus());
    assertThat(modification.getQuantityAdded()).isEqualTo(addToCartResult.getAllowedQuantityChange());
  }

  @Test
  public void shouldRemoveCartEntry() throws Exception {
    addToCartResult.setAllowedQuantityChange(-1L);
    when(cartEntry.getQuantity()).thenReturn(1L);

    CommerceCartModification modification = updateStrategy.updateQuantityForCartEntry(parameter);

    assertThat(modification.getStatusCode()).isEqualTo(STATUS);
    assertThat(modification.getQuantityAdded()).isEqualTo(-1L);
    verify(modelService).remove(cartEntry);
  }

}
