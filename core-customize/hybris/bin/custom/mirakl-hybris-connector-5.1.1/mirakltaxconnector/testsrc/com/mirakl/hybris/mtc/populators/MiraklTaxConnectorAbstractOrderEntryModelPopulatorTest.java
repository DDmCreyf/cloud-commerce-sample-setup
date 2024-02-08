package com.mirakl.hybris.mtc.populators;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.beans.MiraklTaxValuesData;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.order.services.ShippingFeeService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.TaxValue;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklTaxConnectorAbstractOrderEntryModelPopulatorTest {

  private static final String DEFAULT_OFFER_ID = "DEFAULT-OFFER-ID";

  @InjectMocks
  private MiraklTaxConnectorAbstractOrderEntryModelPopulator testObj;

  @Mock
  private ShippingFeeService shippingFeeService;
  @Mock
  private Converter<Pair<MiraklOrderShippingFee, AbstractOrderEntryModel>, MiraklTaxValuesData> taxValuesDataConverter;
  @Mock
  private AbstractOrderEntryModel marketplaceEntry, operatorEntry;
  @Mock
  private AbstractOrderModel order;
  @Mock
  private MiraklOrderShippingFees shippingFees;
  @Mock
  private MiraklOrderShippingFee shippingFee;
  @Mock
  private OfferModel offer;
  @Mock
  private TaxValue taxValue;
  @Mock
  private MiraklTaxValuesData taxValuesData;
  @Captor
  private ArgumentCaptor<List<TaxValue>> taxValueCaptor;
  @Spy
  private List<TaxValue> taxValuesWithMock = new ArrayList<>();

  @Before
  public void setUp() {
    taxValuesWithMock.add(taxValue);
    when(marketplaceEntry.getOfferId()).thenReturn(DEFAULT_OFFER_ID);
    when(marketplaceEntry.getOrder()).thenReturn(order);
    when(shippingFees.getOrders()).thenReturn(Collections.singletonList(shippingFee));
    when(taxValuesDataConverter.convert(any(Pair.class))).thenReturn(taxValuesData);
    when(taxValuesData.getTaxValues()).thenReturn(taxValuesWithMock);
    when(shippingFeeService.getStoredShippingFeesWithCartCalculationFallback(order)).thenReturn(shippingFees);
  }

  @Test
  public void populateShouldPopulateTaxValues() {

    testObj.populate(offer, marketplaceEntry);

    verify(marketplaceEntry).setTaxValues(taxValueCaptor.capture());

    assertThat(taxValueCaptor.getValue()).containsExactly(taxValue);
  }


  @Test
  public void populateShouldNotPopulateTaxValuesForOperatorEntry() {
    when(shippingFeeService.getStoredShippingFeesWithCartCalculationFallback(order)).thenReturn(null);
    testObj.populate(offer, marketplaceEntry);

    verify(operatorEntry, never()).getTaxValues();
    verify(operatorEntry, never()).setTaxValues(anyCollectionOf(TaxValue.class));
  }

  @Test
  public void populateShouldNotPopulateWhenNoStoredShippingFeesEntry() {
    testObj.populate(offer, operatorEntry);

    verify(operatorEntry, never()).getTaxValues();
    verify(operatorEntry, never()).setTaxValues(anyCollectionOf(TaxValue.class));
  }

}
