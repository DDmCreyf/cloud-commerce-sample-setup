package com.mirakl.hybris.mtc.populators;


import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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

import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.front.domain.order.create.MiraklOrderTaxEstimation;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.hybris.mtc.beans.MiraklTaxEstimation;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.TaxValue;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklAbstractOrderEntryTaxesPopulatorTest {

  private static final Long DEFAULT_QUANTITY_1 = 2L;
  private static final MiraklIsoCurrencyCode MIRAKL_ISO_CURRENCY_CODE = MiraklIsoCurrencyCode.USD;

  @InjectMocks
  @Spy
  private MiraklAbstractOrderEntryTaxesPopulator testObj;

  @Mock
  private Converter<MiraklTaxEstimation, List<TaxValue>> absoluteTaxValueConverter;
  @Mock
  private MiraklOrderTaxEstimation miraklOrderTaxEstimation1, miraklOrderTaxEstimation2;
  @Mock
  private MiraklTaxEstimation miraklTaxEstimation1, miraklTaxEstimation2;
  @Mock
  private AbstractOrderModel order;
  @Mock
  private AbstractOrderEntryModel orderEntry;
  @Mock
  private CurrencyModel currency;
  @Mock
  private MiraklOrderShippingFee shippingFee;
  @Mock
  private MiraklOrderShippingFeeOffer miraklOffer;
  @Captor
  private ArgumentCaptor<List<TaxValue>> taxValueCaptor;
  @Captor
  private ArgumentCaptor<MiraklTaxEstimation> miraklTaxEstimationCaptor;
  private List<MiraklOrderTaxEstimation> taxValues = new ArrayList<>(), shippingTaxValues = new ArrayList<>();

  @Before
  public void setUp() {
    when(order.getCurrency()).thenReturn(currency);
    when(currency.getIsocode()).thenReturn(MIRAKL_ISO_CURRENCY_CODE.name());
    when(orderEntry.getOrder()).thenReturn(order);
    when(orderEntry.getQuantity()).thenReturn(DEFAULT_QUANTITY_1);
    when(miraklOffer.getTaxes()).thenReturn(taxValues);
    when(miraklOffer.getShippingTaxes()).thenReturn(shippingTaxValues);
    taxValues.add(miraklOrderTaxEstimation1);
    shippingTaxValues.add(miraklOrderTaxEstimation2);
    doReturn(miraklTaxEstimation1).when(testObj).createMiraklTaxEstimation(taxValues, DEFAULT_QUANTITY_1, MIRAKL_ISO_CURRENCY_CODE.name());
    doReturn(miraklTaxEstimation2).when(testObj).createMiraklTaxEstimation(shippingTaxValues, DEFAULT_QUANTITY_1, MIRAKL_ISO_CURRENCY_CODE.name());
  }

  @Test
  public void orderEntryTaxes() {
    testObj.populate(Pair.of(shippingFee, miraklOffer), orderEntry);

    verify(absoluteTaxValueConverter, times(2)).convert(miraklTaxEstimationCaptor.capture(), taxValueCaptor.capture());
    assertThat(miraklTaxEstimationCaptor.getAllValues().get(0)).isEqualTo(miraklTaxEstimation1);
    assertThat(taxValueCaptor.getAllValues().get(0)).isEqualTo(taxValueCaptor.getAllValues().get(1));
    assertThat(miraklTaxEstimationCaptor.getAllValues().get(1)).isEqualTo(miraklTaxEstimation2);
    verify(orderEntry).setTaxValues(taxValueCaptor.getValue());
  }
}
