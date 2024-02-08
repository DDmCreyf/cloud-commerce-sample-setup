package com.mirakl.hybris.core.order.strategies.hooks.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.services.OfferService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklCartValidationHookTest {

  private static final String OFFER_ID = "offer_id";

  @Mock
  private ModelService modelService;
  @Mock
  private OfferService offerService;
  @Mock
  private CartModel cart;
  @Mock
  private CartEntryModel operatorEntry, marketplaceEntry;
  @Mock
  private OfferModel offer;
  @Mock
  private CurrencyModel currencyUSD, currencyEUR;
  @Mock
  private CommonI18NService commonI18NService;

  @InjectMocks
  private DefaultMiraklCartValidationHook cartValidationHook;

  @Before
  public void setUp() throws Exception {
    when(marketplaceEntry.getOrder()).thenReturn(cart);
    when(marketplaceEntry.getOfferId()).thenReturn(OFFER_ID);
    when(offerService.getOfferForId(OFFER_ID)).thenReturn(offer);
    when(commonI18NService.getCurrentCurrency()).thenReturn(currencyUSD);
  }

  @Test
  public void shouldDeleteMarketplaceEntriesInOtherCurrencies() throws Exception {
    when(cart.getCurrency()).thenReturn(currencyUSD);
    when(offer.getCurrency()).thenReturn(currencyEUR);

    CommerceCartModification modification = cartValidationHook.beforeValidateCartEntry(marketplaceEntry);

    verify(modelService).remove(marketplaceEntry);
    assertThat(modification).isNotNull();
    assertThat(modification.getStatusCode()).isEqualTo(CommerceCartModificationStatus.UNAVAILABLE);
    assertThat(modification.getQuantityAdded()).isEqualTo(0);
  }

  @Test
  public void shouldNotDeleteOperatorEntriesInOtherCurrencies() throws Exception {
    CommerceCartModification modification = cartValidationHook.beforeValidateCartEntry(operatorEntry);

    assertThat(modification).isNull();
    verifyZeroInteractions(modelService);
    verifyZeroInteractions(offerService);
  }

  @Test
  public void shouldNotDeleteMarketplaceEntriesWhenInSameCurrencies() throws Exception {
    when(cart.getCurrency()).thenReturn(currencyUSD);
    when(offer.getCurrency()).thenReturn(currencyUSD);

    CommerceCartModification modification = cartValidationHook.beforeValidateCartEntry(marketplaceEntry);

    assertThat(modification).isNull();
    verifyZeroInteractions(modelService);
  }

  @Test
  public void shouldDeleteMarketplaceEntriesNotMatchingSessionCurrency() throws Exception {
    when(cart.getCurrency()).thenReturn(currencyUSD);
    when(commonI18NService.getCurrentCurrency()).thenReturn(currencyEUR);
    when(offer.getCurrency()).thenReturn(currencyUSD);

    CommerceCartModification modification = cartValidationHook.beforeValidateCartEntry(marketplaceEntry);

    verify(modelService).remove(marketplaceEntry);
    assertThat(modification).isNotNull();
    assertThat(modification.getStatusCode()).isEqualTo(CommerceCartModificationStatus.UNAVAILABLE);
    assertThat(modification.getQuantityAdded()).isEqualTo(0);
  }
}
