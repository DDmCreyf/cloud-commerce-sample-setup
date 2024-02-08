package com.mirakl.hybris.core.payment.populators;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.common.currency.MiraklIsoCurrencyCode;
import com.mirakl.client.mmp.domain.payment.debit.MiraklDebitOrder;
import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderPayment;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklOrderPaymentPopulatorTest {
  private static final String MIRAKL_MIRAKL_ORDER_ID = "54545";
  private static final String MIRAKL_CUSTOMER_ID = "565431";
  private static final MiraklIsoCurrencyCode MIRAKL_ISO_CURRENCY_CODE = MiraklIsoCurrencyCode.EUR;

  @InjectMocks
  private MiraklOrderPaymentPopulator testObj;
  @Mock
  private MiraklDebitOrder miraklDebitOrder;
  @Mock
  private BigDecimal amount;

  @Test
  public void populate() throws Exception {
    when(miraklDebitOrder.getOrderId()).thenReturn(MIRAKL_MIRAKL_ORDER_ID);
    when(miraklDebitOrder.getAmount()).thenReturn(amount);
    when(miraklDebitOrder.getCurrencyIsoCode()).thenReturn(MIRAKL_ISO_CURRENCY_CODE);
    when(miraklDebitOrder.getCustomerId()).thenReturn(MIRAKL_CUSTOMER_ID);


    MiraklOrderPayment debit = new MiraklOrderPayment();

    testObj.populate(miraklDebitOrder, debit);

    assertThat(debit.getAmount()).isEqualTo(amount);
    assertThat(debit.getCurrencyIsoCode()).isEqualTo(MIRAKL_ISO_CURRENCY_CODE);
    assertThat(debit.getCustomerId()).isEqualTo(MIRAKL_CUSTOMER_ID);
    assertThat(debit.getOrderId()).isEqualTo(MIRAKL_MIRAKL_ORDER_ID);
  }

}
