package com.mirakl.hybris.core.order.populators;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.order.tax.MiraklOrderTaxAmount;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.util.TaxValue;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklOrderTaxAmountPopulatorTest {

  private static final String TAX_CODE = "taxCode";
  private static final double TAX_AMOUNT = 6.0;
  private MiraklOrderTaxAmountPopulator testObj = new MiraklOrderTaxAmountPopulator();

  @Mock
  private TaxValue taxValueMock;

  @Before
  public void setUp() {
    when(taxValueMock.getCode()).thenReturn(TAX_CODE);
    when(taxValueMock.getAppliedValue()).thenReturn(TAX_AMOUNT);
  }

  @Test
  public void shouldPopulateMiraklOrderTaxAmount() {
    MiraklOrderTaxAmount result = new MiraklOrderTaxAmount();

    testObj.populate(taxValueMock, result);

    assertThat(result.getCode()).isEqualTo(TAX_CODE);
    assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(TAX_AMOUNT));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfTaxValueIsNull() {
    testObj.populate(null, new MiraklOrderTaxAmount());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfMiraklOrderTaxAmountIsNull() {
    testObj.populate(taxValueMock, null);
  }
}
