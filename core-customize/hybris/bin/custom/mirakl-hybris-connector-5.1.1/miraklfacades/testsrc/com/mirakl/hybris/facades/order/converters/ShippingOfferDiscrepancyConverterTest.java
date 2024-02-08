package com.mirakl.hybris.facades.order.converters;

import static com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeErrorEnum.OFFER_NOT_FOUND;
import static com.mirakl.hybris.facades.order.converters.ShippingOfferDiscrepancyConverter.INSUFFICIENT_QUANTITY_MESSAGE;
import static java.util.Collections.singletonMap;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Optional;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeError;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.order.services.ShippingFeeService;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;
import com.mirakl.hybris.facades.shipping.data.ShippingOfferDiscrepancyData;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ShippingOfferDiscrepancyConverterTest {

  private static final String OFFER_ID = "offerId";
  private static final String OFFER_NOT_FOUND_MESSAGE = "shipping.discrepancy.offer.notFound";
  private static final Long ENTRY_QUANTITY = 3L;
  private static final Integer OFFER_QUANTITY = 1;

  @InjectMocks
  private ShippingOfferDiscrepancyConverter testObj = new ShippingOfferDiscrepancyConverter();

  @Mock
  private JsonMarshallingService jsonMarshallingServiceMock;
  @Mock
  private ShippingFeeService shippingFeeServiceMock;
  @Mock
  private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;

  @Mock
  private AbstractOrderModel orderMock;
  @Mock
  private AbstractOrderEntryModel orderEntryMock;
  @Mock
  private MiraklOrderShippingFees shippingFeesMock;
  @Mock
  private MiraklOrderShippingFeeOffer feeOfferMock;
  @Mock
  private MiraklOrderShippingFeeError feeErrorMock;
  @Mock
  private OrderEntryData orderEntryDataMock;

  @Before
  public void setUp() {
    when(orderEntryMock.getOfferId()).thenReturn(OFFER_ID);
    when(orderEntryMock.getOrder()).thenReturn(orderMock);
    when(orderEntryMock.getQuantity()).thenReturn(ENTRY_QUANTITY);

    when(shippingFeeServiceMock.getStoredShippingFees(orderMock)).thenReturn(shippingFeesMock);
    when(orderEntryConverter.convert(orderEntryMock)).thenReturn(orderEntryDataMock);

    when(shippingFeeServiceMock.extractShippingFeeOffer(OFFER_ID, shippingFeesMock)).thenReturn(Optional.of(feeOfferMock));
    when(shippingFeeServiceMock.extractShippingFeeError(OFFER_ID, shippingFeesMock)).thenReturn(Optional.of(feeErrorMock));

    when(feeOfferMock.getQuantity()).thenReturn(OFFER_QUANTITY);

    when(feeErrorMock.getErrorCode()).thenReturn(OFFER_NOT_FOUND);

    testObj.setErrorCodes(singletonMap(OFFER_NOT_FOUND, OFFER_NOT_FOUND_MESSAGE));
  }

  @Test
  public void populatesMissingOfferEntryFromMiraklOfferError() {
    ShippingOfferDiscrepancyData result = testObj.convert(orderEntryMock);

    assertThat(result.getMessage()).isEqualTo(OFFER_NOT_FOUND_MESSAGE);
    assertThat(result.getMissingQuantity()).isEqualTo(ENTRY_QUANTITY);
    assertThat(result.getEntry()).isEqualTo(orderEntryDataMock);
  }

  @Test
  public void populatesInsufficientOfferQuantity() {
    when(shippingFeeServiceMock.extractShippingFeeError(OFFER_ID, shippingFeesMock))
        .thenReturn(Optional.<MiraklOrderShippingFeeError>absent());

    ShippingOfferDiscrepancyData result = testObj.convert(orderEntryMock);

    assertThat(result.getMessage()).isEqualTo(INSUFFICIENT_QUANTITY_MESSAGE);
    assertThat(result.getMissingQuantity()).isEqualTo(ENTRY_QUANTITY - OFFER_QUANTITY);
    assertThat(result.getEntry()).isEqualTo(orderEntryDataMock);
  }

  @Test
  public void returnsNullIfNoOfferErrorsFoundAndNoMissingQuantity() {
    when(shippingFeeServiceMock.extractShippingFeeOffer(OFFER_ID, shippingFeesMock))
        .thenReturn(Optional.<MiraklOrderShippingFeeOffer>absent());
    when(shippingFeeServiceMock.extractShippingFeeError(OFFER_ID, shippingFeesMock))
        .thenReturn(Optional.<MiraklOrderShippingFeeError>absent());

    ShippingOfferDiscrepancyData result = testObj.convert(orderEntryMock);

    assertThat(result).isNull();
  }

  @Test
  public void returnsNullIfNoShippingFeesJSONFoundInOrder() {
    when(shippingFeeServiceMock.getStoredShippingFees(orderMock)).thenReturn(null);

    ShippingOfferDiscrepancyData result = testObj.convert(orderEntryMock);

    assertThat(result).isNull();
  }

  @Test
  public void returnsNullIfOrderEntryHasNoOffer() {
    when(orderEntryMock.getOfferId()).thenReturn(null);

    ShippingOfferDiscrepancyData result = testObj.convert(orderEntryMock);

    assertThat(result).isNull();
  }

  @Test(expected = IllegalArgumentException.class)
  public void throwsIllegalArgumentExceptionIfNoOrderFoundForOrderEntry() {
    when(orderEntryMock.getOrder()).thenReturn(null);

    testObj.convert(orderEntryMock);
  }
}
