package com.mirakl.hybris.core.ordersplitting.populators;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.order.MiraklRefund;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.returns.model.RefundEntryModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklUpdateRefundEntryPopulatorTest {

  private static final String REASON_CODE = "reason-code";
  private static final RefundReason refundReason = RefundReason.DAMAGEDINTRANSIT;

  @InjectMocks
  private MiraklUpdateRefundEntryPopulator populator;

  @Mock
  private EnumerationService enumerationService;

  @Mock
  private MiraklRefund miraklRefund;

  @Test
  public void shouldPopulateRefundEntry() {
    when(miraklRefund.getReasonCode()).thenReturn(REASON_CODE);
    when(enumerationService.getEnumerationValue(RefundReason.class, REASON_CODE)).thenReturn(refundReason);
    RefundEntryModel refundEntryModel = new RefundEntryModel();

    populator.populate(miraklRefund, refundEntryModel);

    assertThat(refundEntryModel.getReason()).isEqualTo(refundReason);
  }
}
