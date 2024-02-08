package com.mirakl.hybris.mtc.populators;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.front.domain.order.create.MiraklOrderTaxEstimation;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.beans.MiraklTaxValuesData;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;
import com.mirakl.hybris.mtc.beans.MiraklTaxEstimation;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.TaxValue;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklTaxConnectorTaxValuesDataPopulatorTest {

  private static final Long DEFAULT_QUANTITY_1 = 6L;
  private static final String DEFAULT_OFFER_ID = "DEFAULT-OFFER-ID";
  private static final MiraklIsoCurrencyCode MIRAKL_ISO_CURRENCY_CODE = MiraklIsoCurrencyCode.USD;

  @InjectMocks
  private MiraklTaxConnectorTaxValuesDataPopulator testObj;

  private List<MiraklOrderTaxEstimation> notEmptyTaxes = new ArrayList<>(),
      notEmptyShippingTaxes = new ArrayList<>();
  @Mock
  private Converter<MiraklTaxEstimation, List<TaxValue>> absoluteTaxValueConverter;
  @Mock
  private MiraklTaxValuesData miraklTaxValuesData;
  @Mock
  private MiraklOrderTaxEstimation miraklOrderTaxEstimation1, miraklOrderTaxEstimation2;
  @Mock
  private AbstractOrderEntryModel marketplaceEntry;
  @Mock
  private AbstractOrderModel order;
  @Mock
  private CurrencyModel currency;
  @Mock
  private MiraklOrderShippingFees shippingFees;
  @Mock
  private MiraklOrderShippingFee shippingFee;
  @Mock
  private MiraklOrderShippingFeeOffer miraklOrderShippingFeeOffer;
  @Mock
  private JsonMarshallingService jsonMarshallingService;

  @Before
  public void setUp() {
    notEmptyTaxes.add(miraklOrderTaxEstimation1);
    notEmptyShippingTaxes.add(miraklOrderTaxEstimation2);
    when(marketplaceEntry.getOfferId()).thenReturn(DEFAULT_OFFER_ID);
    when(marketplaceEntry.getQuantity()).thenReturn(DEFAULT_QUANTITY_1);
    when(marketplaceEntry.getOrder()).thenReturn(order);
    when(miraklOrderShippingFeeOffer.getId()).thenReturn(DEFAULT_OFFER_ID);
    when(miraklOrderShippingFeeOffer.getTaxes()).thenReturn(notEmptyTaxes);
    when(miraklOrderShippingFeeOffer.getShippingTaxes()).thenReturn(notEmptyShippingTaxes);
    when(shippingFee.getOffers()).thenReturn(Collections.singletonList(miraklOrderShippingFeeOffer));
    when(order.getCurrency()).thenReturn(currency);
    when(currency.getIsocode()).thenReturn(MIRAKL_ISO_CURRENCY_CODE.name());
  }

  @Test
  public void populateShouldPopulateTaxValues() {

    testObj.populate(Pair.of(shippingFee, marketplaceEntry), miraklTaxValuesData);

    verify(absoluteTaxValueConverter, times(2)).convert(any(MiraklTaxEstimation.class), any());
    verify(miraklTaxValuesData).setQuantity(DEFAULT_QUANTITY_1);
  }
}
