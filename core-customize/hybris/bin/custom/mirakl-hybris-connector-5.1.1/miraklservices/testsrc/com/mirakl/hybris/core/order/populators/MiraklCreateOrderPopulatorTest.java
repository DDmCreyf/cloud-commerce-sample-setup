package com.mirakl.hybris.core.order.populators;

import static de.hybris.platform.commerceservices.enums.SalesApplication.WEB;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.order.MiraklOrderCustomer;
import com.mirakl.client.mmp.front.domain.order.create.MiraklCreateOrder;
import com.mirakl.client.mmp.front.domain.order.create.MiraklCreateOrderOffer;
import com.mirakl.client.mmp.front.domain.order.create.MiraklCreateOrderPaymentInfo;
import com.mirakl.hybris.core.order.strategies.ShippingZoneStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklCreateOrderPopulatorTest {

  private static final String ORDER_CODE = "orderCode";
  private static final String SHIPPING_ZONE_CODE = "FR";

  @InjectMocks
  private MiraklCreateOrderPopulator testObj = new MiraklCreateOrderPopulator();

  @Mock
  private ShippingZoneStrategy shippingZoneStrategyMock;
  @Mock(name = "createOrderPaymentInfoConverter")
  private Converter<PaymentInfoModel, MiraklCreateOrderPaymentInfo> createOrderPaymentInfoConverterMock;
  @Mock(name = "miraklOrderCustomerConverter")
  private Converter<OrderModel, MiraklOrderCustomer> miraklOrderCustomerConverterMock;
  @Mock(name = "miraklCreateOrderOfferConverter")
  private Converter<AbstractOrderEntryModel, MiraklCreateOrderOffer> miraklCreateOrderOfferConverterMock;

  @Mock
  private OrderModel orderMock;
  @Mock
  private PaymentInfoModel paymentInfoMock;
  @Mock
  private MiraklCreateOrderPaymentInfo miraklCreateOrderPaymentInfoMock;
  @Mock
  private MiraklOrderCustomer miraklOrderCustomerMock;
  @Mock
  private MiraklCreateOrderOffer miraklCreateOrderOfferMock;
  @Mock
  private AbstractOrderEntryModel orderEntryWithOfferMock;

  @Before
  public void setUp() {
    testObj.setScoringAlreadyDone(true);
    when(orderMock.getCode()).thenReturn(ORDER_CODE);
    when(orderMock.getPaymentInfo()).thenReturn(paymentInfoMock);
    when(orderMock.getMarketplaceEntries()).thenReturn(singletonList(orderEntryWithOfferMock));

    when(shippingZoneStrategyMock.getShippingZoneCode(orderMock)).thenReturn(SHIPPING_ZONE_CODE);
    when(createOrderPaymentInfoConverterMock.convert(paymentInfoMock)).thenReturn(miraklCreateOrderPaymentInfoMock);
    when(miraklOrderCustomerConverterMock.convert(orderMock)).thenReturn(miraklOrderCustomerMock);
    when(miraklCreateOrderOfferConverterMock.convertAll(singletonList(orderEntryWithOfferMock)))
        .thenReturn(singletonList(miraklCreateOrderOfferMock));
  }

  @Test
  public void populatesMiraklCreateOrder() {
    MiraklCreateOrder result = new MiraklCreateOrder();

    testObj.populate(orderMock, result);

    assertThat(result.getCommercialId()).isEqualTo(ORDER_CODE);
    assertThat(result.getScored()).isEqualTo(true);
    assertThat(result.getShippingZoneCode()).isEqualTo(SHIPPING_ZONE_CODE);
    assertThat(result.getPaymentInfo()).isEqualTo(miraklCreateOrderPaymentInfoMock);
    assertThat(result.getCustomer()).isEqualTo(miraklOrderCustomerMock);
    assertThat(result.getOffers()).containsOnly(miraklCreateOrderOfferMock);
  }

  @Test
  public void populateDoesNotSetChannelCodeIfNoSalesApplicationFound() {
    MiraklCreateOrder result = new MiraklCreateOrder();

    testObj.populate(orderMock, result);

    assertThat(result.getCommercialId()).isEqualTo(ORDER_CODE);
    assertThat(result.getScored()).isEqualTo(true);
    assertThat(result.getShippingZoneCode()).isEqualTo(SHIPPING_ZONE_CODE);
    assertThat(result.getPaymentInfo()).isEqualTo(miraklCreateOrderPaymentInfoMock);
    assertThat(result.getCustomer()).isEqualTo(miraklOrderCustomerMock);
    assertThat(result.getOffers()).containsOnly(miraklCreateOrderOfferMock);
  }

  @Test(expected = IllegalArgumentException.class)
  public void populateThrowsIllegalArgumentExceptionIfOrderIsNull() {
    testObj.populate(null, new MiraklCreateOrder());
  }

  @Test(expected = IllegalArgumentException.class)
  public void populateThrowsIllegalArgumentExceptionIfMiraklCreateOrderIsNull() {
    testObj.populate(orderMock, null);
  }
}
