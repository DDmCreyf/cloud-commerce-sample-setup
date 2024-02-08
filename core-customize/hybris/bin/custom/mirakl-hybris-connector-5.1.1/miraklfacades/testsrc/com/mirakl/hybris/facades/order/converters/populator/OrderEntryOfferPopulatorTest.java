package com.mirakl.hybris.facades.order.converters.populator;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.OrderEntryModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderEntryOfferPopulatorTest {

  private static final String OFFER_ID = "offer-id";
  public static final String SHOP_NAME = "shop_name";
  public static final String SHOP_ID = "shop_id";

  @InjectMocks
  private OrderEntryOfferPopulator populator;

  @Mock
  private OrderEntryModel orderEntryModel;

  @Before
  public void setUp() {
    when(orderEntryModel.getOfferId()).thenReturn(OFFER_ID);
    when(orderEntryModel.getShopId()).thenReturn(SHOP_ID);
    when(orderEntryModel.getShopName()).thenReturn(SHOP_NAME);
  }

  @Test
  public void shouldPopulateOfferId() {
    OrderEntryData orderEntryData = new OrderEntryData();
    populator.populate(orderEntryModel, orderEntryData);

    assertThat(orderEntryData.getOfferId()).isEqualTo(OFFER_ID);
    assertThat(orderEntryData.getShopId()).isEqualTo(SHOP_ID);
    assertThat(orderEntryData.getShopName()).isEqualTo(SHOP_NAME);
  }

}
