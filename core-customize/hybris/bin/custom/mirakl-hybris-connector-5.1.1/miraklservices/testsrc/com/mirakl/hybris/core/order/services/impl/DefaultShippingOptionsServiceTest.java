package com.mirakl.hybris.core.order.services.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Optional;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeError;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.order.services.ShippingFeeService;
import com.mirakl.hybris.core.product.services.OfferService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultShippingOptionsServiceTest {

  private static final String OFFER_1 = "offer1";
  private static final String OFFER_2 = "offer2";
  private static final String ERROR_OFFER = "errorOffer";
  private static final String SHIPPING_FEES_JSON = "shippingFeesJSON";
  private static final String ORIGINAL_SHIPPING_FEES_JSON = "originalShippingFeesJSON";
  private static final String ORIGINAL_SHIPPING_CODE_FOR_OFFER_1 = "originalShippingCodeForOffer1";
  private static final int LEAD_TIME_TO_SHIP = 3;
  private static final int FIRST_OFFER_QUANTITY = 2;
  private static final int SECOND_OFFER_QUANTITY = 1;
  private static final int ZERO_QUANTITY = 0;
  private static final Long LOWER_QUANTITY = 5L;
  private static final Long HIGHER_QUANTITY = 7L;
  private static final double LINE_SHIPPING_PRICE = 12.50;
  private static final String SELECTED_SHIPPING_OPTION_CODE = "selectedShippingOptionCode";
  private static final String SHOP_ID = "shopId";

  @InjectMocks
  private DefaultShippingOptionsService shippingOptionsService = new DefaultShippingOptionsService();


  @Mock
  private OfferService offerService;
  @Mock
  private ShippingFeeService shippingFeeService;
  @Mock
  private ModelService modelService;
  @Mock
  private OfferModel firstOffer, secondOffer;
  @Mock
  private AbstractOrderModel order;
  @Mock
  private AbstractOrderEntryModel firstCartEntry, secondCartEntry, entryWithoutOffer;
  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private AddressModel deliveryAddress;

  @Mock
  private MiraklOrderShippingFees shippingRates, originalShippingRates;
  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private MiraklOrderShippingFee shippingFee, originalShippingFee;
  @Mock
  private MiraklOrderShippingFeeOffer firstShippingOffer, secondShippingOffer;
  @Mock
  private MiraklOrderShippingFeeError shippingFeeError;
  @Captor
  private ArgumentCaptor<Set<ItemModel>> modelsToSave;

  @Captor
  private ArgumentCaptor<Collection<AbstractOrderEntryModel>> collectionArgumentCaptor;

  @Before
  public void setUp() {
    when(shippingFee.getOffers()).thenReturn(singletonList(firstShippingOffer));

    when(order.getMarketplaceEntries()).thenReturn(asList(firstCartEntry, secondCartEntry));
    when(firstCartEntry.getQuantity()).thenReturn(Long.valueOf(FIRST_OFFER_QUANTITY));
    when(firstCartEntry.getOfferId()).thenReturn(OFFER_1);
    when(secondCartEntry.getOfferId()).thenReturn(OFFER_2);

    when(shippingFeeService.getStoredShippingFees(order)).thenReturn(originalShippingRates);
    when(shippingFeeService.getShippingFeesAsJson(shippingRates)).thenReturn(SHIPPING_FEES_JSON);
    when(shippingFeeService.getShippingFeesAsJson(originalShippingRates)).thenReturn(ORIGINAL_SHIPPING_FEES_JSON);
    when(shippingFeeService.extractShippingFeeForShop(originalShippingRates, SHOP_ID, LEAD_TIME_TO_SHIP))
        .thenReturn(Optional.of(shippingFee));

    when(order.getShippingFeesJSON()).thenReturn(ORIGINAL_SHIPPING_FEES_JSON);
    when(originalShippingFee.getOffers()).thenReturn(singletonList(firstShippingOffer));
    when(originalShippingFee.getSelectedShippingType().getCode()).thenReturn(ORIGINAL_SHIPPING_CODE_FOR_OFFER_1);
    when(firstShippingOffer.getId()).thenReturn(OFFER_1);
    when(shippingFeeService.getShippingFees(order)).thenReturn(shippingRates);

  }

  @Test
  public void setsShippingOptions() {
    shippingOptionsService.setShippingOptions(order);

    verify(order).setShippingFeesJSON(SHIPPING_FEES_JSON);
    verify(shippingFeeService).setLineShippingDetails(order, shippingRates);
    verify(modelService).save(order);
  }

  @Test
  public void setShippingOptionsResetsJSONShippingFeesWhenNoMarketplaceEntries() {
    when(shippingFeeService.getShippingFees(order)).thenReturn(null);

    shippingOptionsService.setShippingOptions(order);

    verify(order).setShippingFeesJSON(null);
    verify(modelService).save(order);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setShippingOptionsThrowsIllegalArgumentExceptionIfCartIsNull() {
    shippingOptionsService.setShippingOptions(null);
  }

  @Test
  public void setsSelectedShippingOption() {
    shippingOptionsService.setSelectedShippingOption(order, SELECTED_SHIPPING_OPTION_CODE, LEAD_TIME_TO_SHIP, SHOP_ID);

    verify(shippingFeeService).getStoredShippingFees(order);
    verify(shippingFeeService).extractShippingFeeForShop(originalShippingRates, SHOP_ID, LEAD_TIME_TO_SHIP);
    verify(shippingFeeService).updateSelectedShippingOption(shippingFee, SELECTED_SHIPPING_OPTION_CODE);
    verify(shippingFeeService).getShippingFeesAsJson(originalShippingRates);
    verify(order).setShippingFeesJSON(ORIGINAL_SHIPPING_FEES_JSON);
    verify(modelService).save(order);
  }

  @Test(expected = IllegalStateException.class)
  public void setSelectedShippingOptionThrowsIllegalStateExceptionIfNoShippingFeesFoundInOrder() {
    when(shippingFeeService.getStoredShippingFees(order)).thenReturn(null);

    shippingOptionsService.setSelectedShippingOption(order, SELECTED_SHIPPING_OPTION_CODE, LEAD_TIME_TO_SHIP, SHOP_ID);
  }

  @Test(expected = IllegalStateException.class)
  public void setSelectedShippingOptionThrowsIllegalStateExceptionIfNoShippingFeeFoundForShop() {
    when(shippingFeeService.extractShippingFeeForShop(originalShippingRates, SHOP_ID, LEAD_TIME_TO_SHIP))
        .thenReturn(Optional.<MiraklOrderShippingFee>absent());

    shippingOptionsService.setSelectedShippingOption(order, SELECTED_SHIPPING_OPTION_CODE, LEAD_TIME_TO_SHIP, SHOP_ID);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setSelectedShippingOptionThrowsIllegalArgumentExceptionIfOrderIsNull() {
    shippingOptionsService.setSelectedShippingOption(null, SELECTED_SHIPPING_OPTION_CODE, LEAD_TIME_TO_SHIP, SHOP_ID);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setSelectedShippingOptionThrowsIllegalArgumentExceptionIfSelectedShippingOptionCodeIsNull() {
    shippingOptionsService.setSelectedShippingOption(order, null, LEAD_TIME_TO_SHIP, SHOP_ID);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setSelectedShippingOptionThrowsIllegalArgumentExceptionIfLeadTimeToShipIsNull() {
    shippingOptionsService.setSelectedShippingOption(order, SELECTED_SHIPPING_OPTION_CODE, null, SHOP_ID);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setSelectedShippingOptionThrowsIllegalArgumentExceptionIfShopIdIsNull() {
    shippingOptionsService.setSelectedShippingOption(order, SELECTED_SHIPPING_OPTION_CODE, LEAD_TIME_TO_SHIP, null);
  }

  @Test
  public void removesOfferEntriesWithError() {
    when(firstCartEntry.getOfferId()).thenReturn(ERROR_OFFER);
    when(shippingFeeError.getOfferId()).thenReturn(ERROR_OFFER);

    shippingOptionsService.removeOfferEntriesWithError(order, singletonList(shippingFeeError));

    verify(modelService).removeAll(collectionArgumentCaptor.capture());
    Collection<AbstractOrderEntryModel> removedEntries = collectionArgumentCaptor.getValue();
    assertThat(removedEntries).containsOnly(firstCartEntry);
  }

  @Test
  public void adjustsOfferQuantitiesWhenOfferQuantityIsLower() {
    when(firstCartEntry.getQuantity()).thenReturn(HIGHER_QUANTITY);
    when(firstShippingOffer.getQuantity()).thenReturn(LOWER_QUANTITY.intValue());

    shippingOptionsService.adjustOfferQuantities(singletonList(firstCartEntry), singletonList(firstShippingOffer));

    verify(firstCartEntry).setQuantity(LOWER_QUANTITY);
    verify(modelService).saveAll(singletonList(firstCartEntry));
  }

  @Test
  public void adjustOfferQuantitiesDoesNothingWhenOfferQuantityIsHigher() {
    when(firstCartEntry.getQuantity()).thenReturn(LOWER_QUANTITY);
    when(firstShippingOffer.getQuantity()).thenReturn(HIGHER_QUANTITY.intValue());

    shippingOptionsService.adjustOfferQuantities(singletonList(firstCartEntry), singletonList(firstShippingOffer));

    verify(firstCartEntry, never()).setQuantity(anyLong());
    verify(modelService).saveAll(Collections.emptyList());
  }

  @Test
  public void adjustOfferQuantitiesRemovesEntryIfOfferQuantityIsZero() {
    when(firstCartEntry.getQuantity()).thenReturn(HIGHER_QUANTITY);
    when(firstShippingOffer.getQuantity()).thenReturn(ZERO_QUANTITY);

    shippingOptionsService.adjustOfferQuantities(singletonList(firstCartEntry), singletonList(firstShippingOffer));

    verify(modelService).remove(firstCartEntry);
  }

  @Test
  public void resetSavedShippingOptions() {
    shippingOptionsService.resetSavedShippingOptions(order);

    verify(order).setShippingFeesJSON(eq(null));
    verify(modelService).saveAll(modelsToSave.capture());
    Set<ItemModel> savedModels = modelsToSave.getValue();
    assertThat(savedModels).containsOnly(order, firstCartEntry, secondCartEntry);
    verify(firstCartEntry).setLineShippingCode(eq(null));
    verify(firstCartEntry).setLineShippingLabel(eq(null));
    verify(firstCartEntry).setLineShippingPrice(eq(0d));
    verify(secondCartEntry).setLineShippingCode(eq(null));
    verify(secondCartEntry).setLineShippingLabel(eq(null));
    verify(secondCartEntry).setLineShippingPrice(eq(0d));
  }
}
