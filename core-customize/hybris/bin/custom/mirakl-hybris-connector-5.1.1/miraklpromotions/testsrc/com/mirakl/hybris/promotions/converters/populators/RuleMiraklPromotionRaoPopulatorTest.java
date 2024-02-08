package com.mirakl.hybris.promotions.converters.populators;

import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.promotion.MiraklAppliedPromotion;
import com.mirakl.client.mmp.domain.promotion.MiraklOrderPromotionsSummary;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.promotions.strategies.MiraklPromotionsActivationStrategy;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;
import com.mirakl.hybris.promotions.ruleengineservices.rao.MiraklPromotionRAO;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleMiraklPromotionRaoPopulatorTest {
  private static final String CART_JSON = "cart-json";
  private static final String SHOP_ID_1 = "2095";
  private static final String SHOP_ID_2 = "1045";

  @InjectMocks
  private RuleMiraklPromotionRaoPopulator populator;

  @Mock
  private JsonMarshallingService jsonMarshallingService;
  @Mock
  private MiraklPromotionsActivationStrategy miraklPromotionsActivationStrategy;
  @Mock
  private Converter<Pair<MiraklAppliedPromotion, String>, MiraklPromotionRAO> miraklAppliedPromotionConverter;
  @Mock
  private CartModel cartModel;
  @Mock
  private MiraklOrderShippingFees orderShippingFees;
  @Mock
  private MiraklOrderShippingFee order1, order2;
  @Mock
  private MiraklOrderPromotionsSummary promotionsSummary1, promotionsSummary2;
  @Mock
  private MiraklAppliedPromotion promotion1, promotion2;
  @Mock
  private MiraklPromotionRAO promotionRao1, promotionRao2;

  private List<MiraklOrderShippingFee> orders;
  private List<MiraklAppliedPromotion> appliedPromotions1, appliedPromotions2;
  private List<MiraklPromotionRAO> promotionRaos;


  @Before
  public void setUp() throws Exception {
    when(cartModel.getMarketplaceEntries()).thenReturn(newArrayList(mock(AbstractOrderEntryModel.class)));
    when(miraklPromotionsActivationStrategy.isMiraklPromotionsEnabled()).thenReturn(true);
    orders = newArrayList(order1, order2);
    promotionRaos = newArrayList(promotionRao1, promotionRao2);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void shouldPopulatePromotionsWhenPresent() throws Exception {
    when(cartModel.getCartCalculationJSON()).thenReturn(CART_JSON);
    when(jsonMarshallingService.fromJson(CART_JSON, MiraklOrderShippingFees.class)).thenReturn(orderShippingFees);
    when(orderShippingFees.getOrders()).thenReturn(orders);
    when(order1.getPromotions()).thenReturn(promotionsSummary1);
    when(order1.getShopId()).thenReturn(SHOP_ID_1);
    appliedPromotions1 = newArrayList(promotion1);
    when(promotionsSummary1.getAppliedPromotions()).thenReturn(appliedPromotions1);
    when(order2.getPromotions()).thenReturn(promotionsSummary2);
    when(order2.getShopId()).thenReturn(SHOP_ID_2);
    appliedPromotions2 = newArrayList(promotion2);
    when(promotionsSummary2.getAppliedPromotions()).thenReturn(appliedPromotions2);
    when(miraklAppliedPromotionConverter.convertAll(org.mockito.Matchers.<Pair<MiraklAppliedPromotion, String>>anyCollection()))
        .thenReturn(promotionRaos);

    CartRAO cartRao = new CartRAO();
    populator.populate(cartModel, cartRao);

    assertThat(cartRao.getAppliedMiraklPromotions()).isNotEmpty();
    assertThat(cartRao.getAppliedMiraklPromotions().size()).isEqualTo(promotionRaos.size());
  }

  @Test
  public void shouldDoNothingWhenNoPromotion() throws Exception {
    when(cartModel.getCartCalculationJSON()).thenReturn(CART_JSON);
    when(jsonMarshallingService.fromJson(CART_JSON, MiraklOrderShippingFees.class)).thenReturn(orderShippingFees);
    when(orderShippingFees.getOrders()).thenReturn(orders);
    when(order1.getPromotions()).thenReturn(null);
    when(order2.getPromotions()).thenReturn(null);

    CartRAO cartRao = new CartRAO();
    populator.populate(cartModel, cartRao);

    assertThat(cartRao.getAppliedMiraklPromotions()).isNull();
  }

  @Test
  public void shouldDoNothingWhenNotMarketplaceOrder() throws Exception {
    when(cartModel.getMarketplaceEntries()).thenReturn(null);

    CartRAO cartRao = new CartRAO();
    populator.populate(cartModel, cartRao);

    assertThat(cartRao.getAppliedMiraklPromotions()).isNull();
  }

  @Test
  public void shouldDoNothingWhenNoCartCalculationPresent() throws Exception {
    when(cartModel.getCartCalculationJSON()).thenReturn(null);

    CartRAO cartRao = new CartRAO();
    populator.populate(cartModel, cartRao);

    assertThat(cartRao.getAppliedMiraklPromotions()).isNull();
  }

}
