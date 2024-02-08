package com.mirakl.hybris.core.order.strategies.impl;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.order.strategies.hooks.MiraklCartValidationHook;
import com.mirakl.hybris.core.product.services.OfferService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklCartValidationStrategyTest {

  private static final Long REQUESTED_QUANTITY = 20L;
  private static final String OFFER_ID = "offer-id";

  @InjectMocks
  private DefaultMiraklCartValidationStrategy validationStrategy;

  @Mock
  private ProductService productService;
  @Mock
  private CommerceStockService commerceStockService;
  @Mock
  private BaseStoreService baseStoreService;
  @Mock
  private CartService cartService;
  @Mock
  private OfferService offerService;
  @Mock
  private ModelService modelService;
  @Mock
  private CartModel cart;
  @Mock
  private CartEntryModel cartEntry;
  @Mock
  private ProductModel product;
  @Mock
  private OfferModel offer;
  @Mock
  private BaseStoreModel store;
  @Mock
  private CommerceCartParameter commerceCartParameter;
  @Mock
  private MiraklCartValidationHook validationHook;
  @Mock
  private CommerceCartModification hookCartModification;

  @Before
  public void setUp() throws Exception {
    List<AbstractOrderEntryModel> cartEntries = asList((AbstractOrderEntryModel) cartEntry);
    when(cart.getEntries()).thenReturn(cartEntries);
    when(cartEntry.getProduct()).thenReturn(product);
    when(commerceCartParameter.getCart()).thenReturn(cart);
    when(baseStoreService.getCurrentBaseStore()).thenReturn(store);
    when(cartService.getEntriesForProduct(cart, product)).thenReturn(Collections.<CartEntryModel>emptyList());
    when(productService.getProductForCode(cartEntry.getProduct().getCode())).thenReturn(product);
  }

  @Test
  public void shouldValidateUsingProductStockIfNotMarketplaceEntry() {
    whenProductIsOutOfStockForOperator();

    List<CommerceCartModification> modification = validationStrategy.validateCart(commerceCartParameter);

    assertThat(CommerceCartModificationStatus.NO_STOCK).isEqualTo(modification.get(0).getStatusCode());
  }

  @Test
  public void shouldValidateUsingOfferStockIfMarketplaceEntry() {
    whenProductIsOutOfStockForOperator();
    when(cartEntry.getOfferId()).thenReturn(OFFER_ID);
    when(offerService.getOfferForId(OFFER_ID)).thenReturn(offer);
    when(offer.getQuantity()).thenReturn(REQUESTED_QUANTITY.intValue());

    List<CommerceCartModification> modification = validationStrategy.validateCart(commerceCartParameter);

    assertThat(CommerceCartModificationStatus.SUCCESS).isEqualTo(modification.get(0).getStatusCode());
  }

  @Test
  public void shouldReturnZeroForUnkownOffers() {
    whenProductIsOutOfStockForOperator();
    when(cartEntry.getOfferId()).thenReturn(OFFER_ID);
    when(offerService.getOfferForId(OFFER_ID)).thenThrow(new UnknownIdentifierException("Offer not found"));

    List<CommerceCartModification> modification = validationStrategy.validateCart(commerceCartParameter);

    assertThat(CommerceCartModificationStatus.NO_STOCK).isEqualTo(modification.get(0).getStatusCode());
  }

  @Test
  public void shouldLaunchHookBeforeCartEntryValidation() {
    when(validationHook.beforeValidateCartEntry(cartEntry)).thenReturn(hookCartModification);

    CommerceCartModification output = validationStrategy.validateCartEntry(cart, cartEntry);

    verify(validationHook).beforeValidateCartEntry(cartEntry);
    assertThat(output).isEqualTo(hookCartModification);
  }

  protected void whenProductIsOutOfStockForOperator() {
    when(commerceStockService.getStockLevelForProductAndBaseStore(product, store)).thenReturn(0L);
    when(cartEntry.getQuantity()).thenReturn(REQUESTED_QUANTITY);
  }

}
