package com.mirakl.hybris.core.order.populators;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.model.ShopModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklAbstractOrderEntryModelPopulatorTest {

  private static final String SHOP_ID = "shopId";
  private static final String SHOP_NAME = "Shop Name";
  private static final String OFFER_ID = "offerId";

  @Mock
  OfferModel offer;

  @Mock
  ShopModel shop;

  @InjectMocks
  MiraklAbstractOrderEntryModelPopulator testObj;

  @Before
  public void setUp() throws Exception {
    when(offer.getShop()).thenReturn(shop);
    when(offer.getId()).thenReturn(OFFER_ID);
    when(shop.getId()).thenReturn(SHOP_ID);
    when(shop.getName()).thenReturn(SHOP_NAME);
  }

  @Test
  public void populate() throws Exception {
    AbstractOrderEntryModel abstractOrderEntry = new AbstractOrderEntryModel();
    testObj.populate(offer, abstractOrderEntry);

    assertThat(abstractOrderEntry.getOfferId()).isEqualTo(OFFER_ID);
    assertThat(abstractOrderEntry.getShopId()).isEqualTo(SHOP_ID);
    assertThat(abstractOrderEntry.getShopName()).isEqualTo(SHOP_NAME);
  }

}
