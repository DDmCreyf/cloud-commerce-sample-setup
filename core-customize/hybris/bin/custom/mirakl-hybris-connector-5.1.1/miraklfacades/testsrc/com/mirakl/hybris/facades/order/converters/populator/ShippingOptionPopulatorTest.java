package com.mirakl.hybris.facades.order.converters.populator;

import static java.util.Collections.singletonList;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklShippingFeeType;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.DeliveryOrderEntryGroupData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ShippingOptionPopulatorTest {

  private static final String SHIPPING_CODE = "shippingCode";
  private static final String SHIPPING_LABEL = "shippingLabel";
  private static final BigDecimal SHIPPING_PRICE = new BigDecimal(10.00);
  private static final String SHOP_ID = "shopId";
  private static final String SHOP_NAME = "shopName";
  private static final int LEAD_TIME_TO_SHIP = 1;

  @InjectMocks
  private ShippingOptionPopulator testObj = new ShippingOptionPopulator();

  @Mock
  private PriceDataFactory priceDataFactoryMock;

  @Mock
  private MiraklShippingFeeType miraklShippingFeeTypeMock;
  @Mock
  private MiraklOrderShippingFee miraklOrderShippingFeeMock;
  @Mock
  private PriceData priceDataMock;

  @Before
  public void setUp() {
    when(miraklOrderShippingFeeMock.getShippingTypes()).thenReturn(singletonList(miraklShippingFeeTypeMock));
    when(miraklOrderShippingFeeMock.getCurrencyIsoCode()).thenReturn(MiraklIsoCurrencyCode.EUR);
    when(miraklOrderShippingFeeMock.getSelectedShippingType()).thenReturn(miraklShippingFeeTypeMock);
    when(miraklOrderShippingFeeMock.getShopId()).thenReturn(SHOP_ID);
    when(miraklOrderShippingFeeMock.getShopName()).thenReturn(SHOP_NAME);
    when(miraklOrderShippingFeeMock.getLeadtimeToShip()).thenReturn(LEAD_TIME_TO_SHIP);

    when(miraklShippingFeeTypeMock.getCode()).thenReturn(SHIPPING_CODE);
    when(miraklShippingFeeTypeMock.getLabel()).thenReturn(SHIPPING_LABEL);
    when(miraklShippingFeeTypeMock.getTotalShippingPrice()).thenReturn(SHIPPING_PRICE);

    when(priceDataFactoryMock.create(PriceDataType.BUY, SHIPPING_PRICE, MiraklIsoCurrencyCode.EUR.name()))
        .thenReturn(priceDataMock);
  }

  @Test
  public void populatesShippingOptions() {
    DeliveryOrderEntryGroupData result = new DeliveryOrderEntryGroupData();

    testObj.populate(miraklOrderShippingFeeMock, result);

    assertThat(result.getLeadTimeToShip()).isEqualTo(LEAD_TIME_TO_SHIP);
    assertThat(result.getShopId()).isEqualTo(SHOP_ID);
    assertThat(result.getShopName()).isEqualTo(SHOP_NAME);

    List<DeliveryModeData> availableShippingOptions = result.getAvailableShippingOptions();
    assertThat(availableShippingOptions).hasSize(1);
    DeliveryModeData shippingOptionData = availableShippingOptions.get(0);

    assertThat(shippingOptionData.getCode()).isSameAs(SHIPPING_CODE);
    assertThat(shippingOptionData.getName()).isSameAs(SHIPPING_LABEL);
    assertThat(shippingOptionData.getDeliveryCost()).isSameAs(priceDataMock);

    assertThat(result.getSelectedShippingOption()).isSameAs(shippingOptionData);
  }

  @Test(expected = ConversionException.class)
  public void populateThrowsConversionExceptionIfPriceDataFactoryThrowsException() {
    when(priceDataFactoryMock.create(PriceDataType.BUY, SHIPPING_PRICE, MiraklIsoCurrencyCode.EUR.name()))
        .thenThrow(new UnknownIdentifierException(EMPTY));

    testObj.populate(miraklOrderShippingFeeMock, new DeliveryOrderEntryGroupData());
  }

  @Test(expected = IllegalArgumentException.class)
  public void populateThrowsIllegalArgumentExceptionIfShippingFeeIsNull() {
    testObj.populate(null, new DeliveryOrderEntryGroupData());
  }

  @Test(expected = IllegalArgumentException.class)
  public void populateThrowsIllegalArgumentExceptionIfDeliveryOrderEntryGroupDataIsNull() {
    testObj.populate(miraklOrderShippingFeeMock, null);
  }
}
