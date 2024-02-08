package com.mirakl.hybris.core.order.populators;

import static de.hybris.platform.core.enums.CreditCardType.VISA;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.front.domain.order.create.MiraklCreateOrderPaymentInfo;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklCreateOrderPaymentInfoPopulatorTest {

  private static final String PAYMENT_INFO_ID = "paymentInfoId";

  private MiraklCreateOrderPaymentInfoPopulator testObj = new MiraklCreateOrderPaymentInfoPopulator();

  @Mock
  private CreditCardPaymentInfoModel creditCardPaymentInfoMock;
  @Mock
  private PaymentInfoModel notCreditCardPaymentInfoMock;

  @Before
  public void setUp() {
    when(creditCardPaymentInfoMock.getCode()).thenReturn(PAYMENT_INFO_ID);
    when(creditCardPaymentInfoMock.getType()).thenReturn(VISA);
    when(notCreditCardPaymentInfoMock.getCode()).thenReturn(PAYMENT_INFO_ID);
  }

  @Test
  public void shouldPopulateMiraklCreatePaymentInfoWithCreditCardPayment() {
    MiraklCreateOrderPaymentInfo result = new MiraklCreateOrderPaymentInfo();

    testObj.populate(creditCardPaymentInfoMock, result);

    assertThat(result.getPaymentId()).isEqualTo(PAYMENT_INFO_ID);
    assertThat(result.getPaymentType()).isEqualTo(VISA.name());
  }

  @Test
  public void shouldNotPopulatePaymentTypeIfNotCreditCardPaymentInfo() {
    MiraklCreateOrderPaymentInfo result = new MiraklCreateOrderPaymentInfo();

    testObj.populate(notCreditCardPaymentInfoMock, result);

    assertThat(result.getPaymentId()).isEqualTo(PAYMENT_INFO_ID);
    assertThat(result.getPaymentType()).isNull();
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfSourcePaymentInfoIsNull() {
    testObj.populate(null, new MiraklCreateOrderPaymentInfo());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfTargetMiraklPaymentInfoIsNull() {
    testObj.populate(creditCardPaymentInfoMock, null);
  }
}
