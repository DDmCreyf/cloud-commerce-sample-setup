package com.mirakl.hybris.core.order.populators;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Optional;
import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.order.tax.MiraklOrderTaxAmount;
import com.mirakl.client.mmp.domain.shipping.MiraklShippingTypeWithConfiguration;
import com.mirakl.client.mmp.front.domain.order.create.MiraklCreateOrderOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.order.services.ShippingFeeService;
import com.mirakl.hybris.core.product.services.OfferService;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.TaxValue;

@RunWith(MockitoJUnitRunner.class)
public class MiraklCreateOrderOfferPopulatorTest {
  private static final String OFFER_ID = "offer-id";
  private static final BigDecimal OFFER_UNIT_PRICE = BigDecimal.valueOf(45.99);
  private static final BigDecimal OFFER_PRICE = BigDecimal.valueOf(91.98);
  private static final int OFFER_QUANTITY = 2;
  private static final BigDecimal OFFER_SHIPPING_PRICE = BigDecimal.valueOf(5.0);
  private static final String OFFER_SHIPPING_TYPE_CODE = "offerShippingTypeCode";
  private static final int LEAD_TIME_TO_SHIP = 2;
  private static final MiraklIsoCurrencyCode CURRENCY_ISO_CODE = MiraklIsoCurrencyCode.EUR;

  @InjectMocks
  private MiraklCreateOrderOfferPopulator populator;

  @Mock
  private ShippingFeeService shippingFeeService;
  @Mock
  private Converter<TaxValue, MiraklOrderTaxAmount> miraklOrderTaxAmountConverter;
  @Mock
  private OfferService offerService;
  @Mock
  private AbstractOrderEntryModel orderEntry;
  @Mock
  private AbstractOrderModel order;
  @Mock
  private OfferModel offer;
  @Mock
  private MiraklOrderShippingFees miraklOrderShippingFees;
  @Mock
  private Optional<MiraklOrderShippingFeeOffer> optionalShippingFeeOffer;
  @Mock
  private MiraklOrderShippingFeeOffer shippingFeeOffer;
  @Mock
  private Optional<MiraklOrderShippingFee> optionalOrderShippingFee;
  @Mock
  private MiraklOrderShippingFee orderShippingFee;
  @Mock
  private MiraklShippingTypeWithConfiguration shippingType;

  @Before
  public void setUp() throws Exception {
    when(orderEntry.getOrder()).thenReturn(order);
    when(orderEntry.getOfferId()).thenReturn(OFFER_ID);
    when(shippingFeeService.getStoredShippingFees(order)).thenReturn(miraklOrderShippingFees);
    when(shippingFeeService.extractShippingFeeOffer(OFFER_ID, miraklOrderShippingFees)).thenReturn(optionalShippingFeeOffer);
    when(optionalShippingFeeOffer.isPresent()).thenReturn(true);
    when(optionalShippingFeeOffer.get()).thenReturn(shippingFeeOffer);
    when(shippingFeeOffer.getId()).thenReturn(OFFER_ID);
    when(shippingFeeOffer.getPrice()).thenReturn(OFFER_UNIT_PRICE);
    when(shippingFeeOffer.getLinePrice()).thenReturn(OFFER_PRICE);
    when(shippingFeeOffer.getLineQuantity()).thenReturn(OFFER_QUANTITY);
    when(shippingFeeOffer.getLineShippingPrice()).thenReturn(OFFER_SHIPPING_PRICE);
    when(shippingFeeService.extractOrderShippingFeeForOffer(OFFER_ID, miraklOrderShippingFees))
        .thenReturn(optionalOrderShippingFee);
    when(optionalOrderShippingFee.isPresent()).thenReturn(true);
    when(optionalOrderShippingFee.get()).thenReturn(orderShippingFee);
    when(orderShippingFee.getLeadtimeToShip()).thenReturn(LEAD_TIME_TO_SHIP);
    when(orderShippingFee.getCurrencyIsoCode()).thenReturn(CURRENCY_ISO_CODE);
    when(orderShippingFee.getSelectedShippingType()).thenReturn(shippingType);
    when(shippingType.getCode()).thenReturn(OFFER_SHIPPING_TYPE_CODE);
  }

  @Test
  public void testPopulate() throws Exception {
    MiraklCreateOrderOffer result = new MiraklCreateOrderOffer();
    populator.populate(orderEntry, result);

    assertThat(result.getPriceUnit()).isEqualTo(OFFER_UNIT_PRICE);
    assertThat(result.getPrice()).isEqualTo(OFFER_PRICE);
    assertThat(result.getQuantity()).isEqualTo(OFFER_QUANTITY);
    assertThat(result.getId()).isEqualTo(OFFER_ID);
    assertThat(result.getShippingPrice()).isEqualTo(OFFER_SHIPPING_PRICE);
    assertThat(result.getShippingTypeCode()).isEqualTo(OFFER_SHIPPING_TYPE_CODE);
    assertThat(result.getCurrencyIsoCode()).isEqualTo(CURRENCY_ISO_CODE);
    assertThat(result.getLeadtimeToShip()).isEqualTo(LEAD_TIME_TO_SHIP);
  }

}
