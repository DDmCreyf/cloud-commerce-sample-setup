package com.mirakl.hybris.core.payment.populators;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Ignore;
import org.mockito.Mock;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.returns.model.RefundEntryModel;

@Ignore
public abstract class AbstractMiraklOrderLineRefundPopulatorTest {

  protected static final Date PAYMENT_TRANSACTION_ENTRY_TIME = new Date();
  protected static final String PAYMENT_TRANSACTION_ENTRY_CODE = "00000056";
  protected static final String MIRAKL_REFUND_ID = "0000087";
  protected static final BigDecimal REFUND_ENTRY_AMOUNT = new BigDecimal(564);
  protected static final String CURRENCY_ISO_CODE = "EUR";

  @Mock
  protected RefundEntryModel refundEntry;
  @Mock
  protected PaymentTransactionEntryModel paymentTransactionEntry;
  @Mock
  protected CurrencyModel currency;

  public void setUp() throws Exception {
    when(refundEntry.getMiraklRefundId()).thenReturn(MIRAKL_REFUND_ID);
    when(refundEntry.getAmount()).thenReturn(REFUND_ENTRY_AMOUNT);
    when(refundEntry.getCurrency()).thenReturn(currency);
    when(currency.getIsocode()).thenReturn(CURRENCY_ISO_CODE);
  }

}
