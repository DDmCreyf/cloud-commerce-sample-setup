package com.mirakl.hybris.mtc.services.impl;

import static com.mirakl.hybris.mtc.constants.MirakltaxconnectorConstants.ALLOWED_MIRAKL_TAX_CONNECTOR_CURRENCIES;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.promotion.MiraklOrderPromotionsSummary;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.order.services.ShippingFeeService;
import com.mirakl.hybris.core.order.strategies.MarketplaceDeliveryCostStrategy;
import com.mirakl.hybris.core.promotions.strategies.MiraklPromotionsActivationStrategy;
import com.mirakl.hybris.mtc.strategies.MiraklTaxConnectorActivationStrategy;
import com.mirakl.hybris.mtc.strategies.MiraklTaxConnectorFindTaxValuesStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.strategies.calculation.FindDeliveryCostStrategy;
import de.hybris.platform.order.strategies.calculation.FindTaxValuesStrategy;
import de.hybris.platform.order.strategies.calculation.OrderRequiresCalculationStrategy;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.util.TaxValue;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklTaxConnectorCalculationServiceTest {

  private static final MiraklIsoCurrencyCode MIRAKL_ISO_CURRENCY_CODE = ALLOWED_MIRAKL_TAX_CONNECTOR_CURRENCIES.iterator().next();
  private static final String OFFER_ID = "OFFER-ID";
  private static final Double OPERATOR_TOTAL_PRICE = 20D, OPERATOR_TAX = 3D, OPERATOR_DELIVERY_COST = 4D,
      OPERATOR_PAYMENT_COST = 5D, OPERATOR_DISCOUNT = 2D;
  private static final Double MARKETPLACE_TOTAL_PRICE = 15D, MARKETPLACE_TAX = 6D, MARKETPLACE_DELIVERY_COST = 3D;
  private static final BigDecimal MARKETPLACE_PROMOTION = BigDecimal.ONE;
  private static final int DIGITS = 2;
  private static final Answer<Double> ROUND_ANSWER = new Answer<Double>() {
    @Override
    public Double answer(InvocationOnMock invocation) throws Throwable {
      return (Double) invocation.getArguments()[0];
    }
  };

  @InjectMocks
  @Spy
  private DefaultMiraklTaxConnectorCalculationService testObj;

  @Mock
  private OrderRequiresCalculationStrategy orderRequiresCalculationStrategy;
  @Mock
  private MiraklTaxConnectorFindTaxValuesStrategy miraklTaxConnectorFindTaxValuesStrategy;
  @Mock
  private MarketplaceDeliveryCostStrategy marketplaceDeliveryCostStrategy;
  @Mock
  private FindDeliveryCostStrategy operatorFindDeliveryCostStrategy;
  @Mock
  private ShippingFeeService shippingFeeService;
  @Mock
  private MiraklTaxConnectorActivationStrategy miraklTaxConnectorActivationStrategy;
  @Mock
  private AbstractOrderModel order;
  @Mock
  private CommonI18NService commonI18NService;
  @Mock
  private AbstractOrderEntryModel operatorOrderEntry, marketplaceOrderEntry;
  @Mock
  private CurrencyModel currency;
  @Mock
  private ModelService modelService;
  @Mock
  private TaxValue operatorTaxValue, marketplaceTaxValue;
  @Mock
  private FindTaxValuesStrategy findTaxValuesStrategy;
  @Mock
  private MiraklOrderShippingFees miraklOrderShippingFees;
  @Mock
  private MiraklOrderShippingFee miraklOrderShippingFee;
  @Mock
  private MiraklOrderPromotionsSummary miraklOrderPromotionsSummary;
  @Mock
  private DiscountValue discountValue;
  @Mock
  private PriceValue deliveryPriceValue;
  @Mock
  private MiraklPromotionsActivationStrategy miraklPromotionsActivationStrategy;
  @Captor
  private ArgumentCaptor<Double> totalPriceCaptor, totalTaxCaptor, totalDiscountCaptor;

  private List<AbstractOrderEntryModel> marketplaceOrderEntries = new ArrayList<>(), operatorOrderEntries = new ArrayList<>(),
      orderEntries = new ArrayList<>();
  private List<TaxValue> operatorTaxValues = new ArrayList<>(), marketplaceTaxValues = new ArrayList<>();
  private List<FindTaxValuesStrategy> findTaxesStrategies = new ArrayList<>();
  private List<MiraklOrderShippingFee> orderShippingFees = new ArrayList<>();
  private List<DiscountValue> discountValues = new ArrayList<>();

  @Before
  public void setUp() throws CalculationException {
    marketplaceOrderEntries.add(marketplaceOrderEntry);
    operatorOrderEntries.add(operatorOrderEntry);
    orderEntries.addAll(marketplaceOrderEntries);
    orderEntries.addAll(operatorOrderEntries);
    operatorTaxValues.add(operatorTaxValue);
    marketplaceTaxValues.add(marketplaceTaxValue);
    findTaxesStrategies.add(findTaxValuesStrategy);
    orderShippingFees.add(miraklOrderShippingFee);
    discountValues.add(discountValue);
    when(currency.getDigits()).thenReturn(DIGITS);
    when(currency.getIsocode()).thenReturn(MIRAKL_ISO_CURRENCY_CODE.name());
    when(order.getMarketplaceEntries()).thenReturn(marketplaceOrderEntries);
    when(order.getOperatorEntries()).thenReturn(operatorOrderEntries);
    when(order.getGlobalDiscountValues()).thenReturn(discountValues);
    when(order.getPaymentCost()).thenReturn(OPERATOR_PAYMENT_COST);
    when(order.getEntries()).thenReturn(orderEntries);
    when(order.getCurrency()).thenReturn(currency);
    when(order.getNet()).thenReturn(true);
    when(operatorOrderEntry.getTaxValues()).thenReturn(operatorTaxValues);
    when(operatorOrderEntry.getOrder()).thenReturn(order);
    when(operatorOrderEntry.getTotalPrice()).thenReturn(OPERATOR_TOTAL_PRICE);
    when(operatorOrderEntry.getQuantity()).thenReturn(1L);
    when(marketplaceOrderEntry.getOfferId()).thenReturn(OFFER_ID);
    when(marketplaceOrderEntry.getTaxValues()).thenReturn(marketplaceTaxValues);
    when(marketplaceOrderEntry.getOrder()).thenReturn(order);
    when(marketplaceOrderEntry.getTotalPrice()).thenReturn(MARKETPLACE_TOTAL_PRICE);
    when(marketplaceOrderEntry.getQuantity()).thenReturn(1L);
    when(shippingFeeService.getStoredShippingFeesWithCartCalculationFallback(order)).thenReturn(miraklOrderShippingFees);
    when(miraklOrderShippingFees.getOrders()).thenReturn(orderShippingFees);
    when(miraklOrderShippingFee.getPromotions()).thenReturn(miraklOrderPromotionsSummary);
    when(miraklOrderPromotionsSummary.getTotalDeducedAmount()).thenReturn(MARKETPLACE_PROMOTION);
    when(findTaxValuesStrategy.findTaxValues(operatorOrderEntry)).thenReturn(operatorTaxValues);
    when(findTaxValuesStrategy.findTaxValues(marketplaceOrderEntry)).thenReturn(marketplaceTaxValues);
    when(discountValue.getCurrencyIsoCode()).thenReturn(MIRAKL_ISO_CURRENCY_CODE.name());
    when(discountValue.getAppliedValue()).thenReturn(OPERATOR_DISCOUNT);
    when(discountValue.isAbsolute()).thenReturn(true);
    when(discountValue.apply(anyDouble(), anyDouble(), anyInt(), anyString())).thenReturn(discountValue);
    when(operatorTaxValue.getAppliedValue()).thenReturn(OPERATOR_TAX);
    when(operatorTaxValue.isAbsolute()).thenReturn(true);
    when(marketplaceTaxValue.getAppliedValue()).thenReturn(MARKETPLACE_TAX);
    when(marketplaceTaxValue.isAbsolute()).thenReturn(true);
    when(marketplaceDeliveryCostStrategy.getMarketplaceDeliveryCost(order)).thenReturn(MARKETPLACE_DELIVERY_COST);
    when(deliveryPriceValue.getValue()).thenReturn(OPERATOR_DELIVERY_COST);
    when(operatorFindDeliveryCostStrategy.getDeliveryCost(order)).thenReturn(deliveryPriceValue);
    when(commonI18NService.roundCurrency(anyDouble(), Matchers.eq(DIGITS))).thenAnswer(ROUND_ANSWER);
    when(miraklPromotionsActivationStrategy.isMiraklPromotionsEnabled()).thenReturn(true);
    doReturn(true).when(miraklTaxConnectorActivationStrategy).isMiraklTaxConnectorComputation(order);
    testObj.setFindTaxesStrategies(findTaxesStrategies);
  }

  @Test
  public void calculateSubtotalShouldNotPerformMTCSubtotalCalculationWhenNotEnabled() {
    when(miraklTaxConnectorActivationStrategy.isMiraklTaxConnectorComputation(order)).thenReturn(false);
    testObj.calculateSubtotal(order, true);
    verify(testObj).calculateSubtotal(order, true);
  }

  @Test
  public void calculateSubtotalShouldNotDoNothingIfNoRecalculationRequired() {
    when(orderRequiresCalculationStrategy.requiresCalculation(order)).thenReturn(false);
    Map<TaxValue, Map<Set<TaxValue>, Double>> result = testObj.calculateSubtotal(order, false);

    assertThat(result).isEmpty();
  }

  @Test
  public void calculateSubtotalShouldNotUseOperatorTaxOnMarketplaceEntries() {
    testObj.calculateSubtotal(order, true);

    verify(testObj).addEntryTaxValue(anyMap(), any(AbstractOrderEntryModel.class), anyDouble(), anySetOf(TaxValue.class),
        any(TaxValue.class));
    verify(operatorOrderEntry, times(2)).getTaxValues();
    verify(marketplaceOrderEntry).getTaxValues();
  }

  @Test
  public void findTaxValuesWithoutMTC() throws CalculationException {
    doReturn(false).when(miraklTaxConnectorActivationStrategy).isMiraklTaxConnectorComputation(order);
    testObj.findTaxValues(marketplaceOrderEntry);

    verify(testObj).findTaxValues(marketplaceOrderEntry);
  }

  @Test
  public void findTaxValuesWithMarketplaceEntry() throws CalculationException {
    when(miraklTaxConnectorFindTaxValuesStrategy.findTaxValues(marketplaceOrderEntry)).thenReturn(marketplaceTaxValues);

    Collection<TaxValue> result = testObj.findTaxValues(marketplaceOrderEntry);

    assertThat(result).containsOnly(marketplaceTaxValue);
  }

  @Test
  public void findTaxValuesWithOperatorEntry() throws CalculationException {
    when(findTaxValuesStrategy.findTaxValues(operatorOrderEntry)).thenReturn(operatorTaxValues);

    Collection<TaxValue> result = testObj.findTaxValues(operatorOrderEntry);

    assertThat(result).containsOnly(operatorTaxValue);
  }

  @Test
  public void findTaxValuesWhenMiraklTaxConnectorNotEnabled() throws CalculationException {
    doReturn(false).when(miraklTaxConnectorActivationStrategy).isMiraklTaxConnectorComputation(order);

    Collection<TaxValue> result = testObj.findTaxValues(marketplaceOrderEntry);
    assertThat(result).containsOnly(marketplaceTaxValue);

    result = testObj.findTaxValues(operatorOrderEntry);
    assertThat(result).containsOnly(operatorTaxValue);
  }

  @Test(expected = CalculationException.class)
  public void calculateTotalsShouldThrowCalculationExceptionWhenNoShippingFeesStored() throws CalculationException {
    when(shippingFeeService.getStoredShippingFeesWithCartCalculationFallback(order)).thenReturn(null);

    testObj.calculateTotals(order, true, new HashMap<>());
  }

  @Test
  public void calculateTotalsWithNoOperatorTaxes() throws CalculationException {
    testObj.calculateTotals(order, true, new HashMap<>());

    verify(order).setTotalPrice(totalPriceCaptor.capture());
    assertThat(totalPriceCaptor.getValue()).isEqualTo(OPERATOR_TOTAL_PRICE + OPERATOR_DELIVERY_COST + OPERATOR_PAYMENT_COST
        - OPERATOR_DISCOUNT + MARKETPLACE_TOTAL_PRICE + MARKETPLACE_DELIVERY_COST);

    verify(order).setTotalTax(totalTaxCaptor.capture());
    assertThat(totalTaxCaptor.getValue()).isEqualTo(MARKETPLACE_TAX);

    verify(order).setTotalDiscounts(totalDiscountCaptor.capture());
    assertThat(totalDiscountCaptor.getValue()).isEqualTo(OPERATOR_DISCOUNT);
  }

  @Test
  public void calculateTotalsForMixedOrderWithPromotions() throws CalculationException {
    doReturn(operatorTaxValue).when(testObj).applyTaxValue(anyInt(), anyDouble(), any(CurrencyModel.class), anyString(),
        anyBoolean(), any(TaxValue.class), Matchers.any());

    Map<TaxValue, Map<Set<TaxValue>, Double>> taxValuesMap = new HashMap<>();
    Map<Set<TaxValue>, Double> taxValueMap = new HashMap<>();
    taxValueMap.put(Collections.singleton(operatorTaxValue), operatorOrderEntry.getQuantity().doubleValue());
    taxValuesMap.put(operatorTaxValue, taxValueMap);
    testObj.calculateTotals(order, true, taxValuesMap);

    verify(order).setTotalPrice(totalPriceCaptor.capture());
    assertThat(totalPriceCaptor.getValue()).isEqualTo(OPERATOR_TOTAL_PRICE + OPERATOR_DELIVERY_COST + OPERATOR_PAYMENT_COST
        - OPERATOR_DISCOUNT + MARKETPLACE_TOTAL_PRICE + MARKETPLACE_DELIVERY_COST);

    verify(order).setTotalTax(totalTaxCaptor.capture());
    assertThat(totalTaxCaptor.getValue()).isEqualTo(OPERATOR_TAX + MARKETPLACE_TAX);

    verify(order).setTotalDiscounts(totalDiscountCaptor.capture());
    assertThat(totalDiscountCaptor.getValue()).isEqualTo(OPERATOR_DISCOUNT);
  }
}
