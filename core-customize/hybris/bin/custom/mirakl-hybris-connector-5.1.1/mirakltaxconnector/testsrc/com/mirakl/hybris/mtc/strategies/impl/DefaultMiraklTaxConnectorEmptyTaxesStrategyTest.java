package com.mirakl.hybris.mtc.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.hybris.beans.MiraklTaxValuesData;
import com.mirakl.hybris.mtc.constants.MirakltaxconnectorConstants;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.util.TaxValue;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklTaxConnectorEmptyTaxesStrategyTest {

  @InjectMocks
  private DefaultMiraklTaxConnectorEmptyTaxesStrategy testObj;

  @Spy
  private List<TaxValue> taxValues = new ArrayList<>(), shippingTaxValues = new ArrayList<>();
  @Mock
  private AbstractOrderEntryModel marketplaceOrderEntry;
  @Mock
  private MiraklTaxValuesData miraklTaxValuesData;
  @Mock
  private TaxValue taxValue, shippingTaxValue;

  @Before
  public void setUp() {
    taxValues.add(taxValue);
    taxValues.add(MirakltaxconnectorConstants.MTC_NO_TAXES);
    shippingTaxValues.add(shippingTaxValue);
    shippingTaxValues.add(MirakltaxconnectorConstants.MTC_NO_TAXES);
    when(miraklTaxValuesData.getShippingTaxValues()).thenReturn(taxValues);
    when(miraklTaxValuesData.getShippingTaxValues()).thenReturn(shippingTaxValues);
  }

  @Test
  public void shouldSetTaxValuesToDefaultNoTaxesValue() {
    testObj.setEmptyTaxValues(marketplaceOrderEntry);

    verify(marketplaceOrderEntry).setTaxValues(Collections.singletonList(MirakltaxconnectorConstants.MTC_NO_TAXES));
  }

  @Test
  public void shouldRemoveAllNoTaxesValue() {
    testObj.resetEmptyTaxValues(miraklTaxValuesData);

    assertThat(miraklTaxValuesData.getTaxValues()).isNotIn(Collections.singletonList(MirakltaxconnectorConstants.MTC_NO_TAXES));
    assertThat(miraklTaxValuesData.getShippingTaxValues()).isNotIn(Collections.singletonList(MirakltaxconnectorConstants.MTC_NO_TAXES));
  }


}
