package com.mirakl.hybris.promotions.converters.populators;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.core.order.services.ShippingFeeService;
import com.mirakl.hybris.core.order.strategies.MarketplaceDeliveryCostStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.ruleengineservices.rao.CartRAO;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklCartRaoPopulatorTest {

  private static final Double MIXED_CART_TOTAL_PRICE = 50.0;
  private static final Double MIXED_CART_TOTAL_PRICE_INCL_MARKETPLACE_DELIVERY = 60.0;
  private static final Double OPERATOR_CART_TOTAL_PRICE = 20.0;
  private static final Double MARKETPLACE_ENTRY_TOTAL_PRICE = 30.0;
  private static final Double MARKETPLACE_EXPRESS_DELIVERY_SHIPPING_PRICE = 10.0;

  @InjectMocks
  private MiraklCartRaoPopulator testObj;
  @Mock
  private ShippingFeeService shippingFeeService;
  @Mock
  private MarketplaceDeliveryCostStrategy marketplaceDeliveryCostStrategy;
  @Mock
  private CartModel cartModel;
  @Mock
  private AbstractOrderEntryModel marketplaceEntry, operatorEntry;
  @Mock
  private CartRAO cartRao;

  @Before
  public void setUp() throws Exception {
    when(cartModel.getMarketplaceEntries()).thenReturn(singletonList(marketplaceEntry));
    when(cartModel.getOperatorEntries()).thenReturn(singletonList(operatorEntry));
    when(cartModel.getTotalPrice()).thenReturn(MIXED_CART_TOTAL_PRICE);
    when(marketplaceEntry.getTotalPrice()).thenReturn(MARKETPLACE_ENTRY_TOTAL_PRICE);
  }

  @Test
  public void shouldPopulateFullOperatorCart() throws Exception {
    when(cartModel.getMarketplaceEntries()).thenReturn(emptyList());
    when(cartModel.getTotalPrice()).thenReturn(OPERATOR_CART_TOTAL_PRICE);

    testObj.populate(cartModel, cartRao);

    verify(cartRao).setOperatorTotal(BigDecimal.valueOf(OPERATOR_CART_TOTAL_PRICE));
  }

  @Test
  public void shouldPopulateFullMarketplaceCart() throws Exception {
    when(cartModel.getOperatorEntries()).thenReturn(emptyList());

    testObj.populate(cartModel, cartRao);

    verify(cartRao).setOperatorTotal(BigDecimal.valueOf(0.0));
  }

  @Test
  public void shouldCalculateOperatorTotal() throws Exception {
    testObj.populate(cartModel, cartRao);

    verify(cartRao).setOperatorTotal(BigDecimal.valueOf(OPERATOR_CART_TOTAL_PRICE));
    verifyZeroInteractions(shippingFeeService);
  }

  @Test
  public void shouldCalculateShippingFeesWhenAny() throws Exception {
    when(cartModel.getTotalPrice()).thenReturn(MIXED_CART_TOTAL_PRICE_INCL_MARKETPLACE_DELIVERY);
    when(marketplaceEntry.getLineShippingPrice()).thenReturn(MARKETPLACE_EXPRESS_DELIVERY_SHIPPING_PRICE);

    testObj.populate(cartModel, cartRao);

    verify(cartRao).setOperatorTotal(BigDecimal.valueOf(OPERATOR_CART_TOTAL_PRICE));
  }

}
