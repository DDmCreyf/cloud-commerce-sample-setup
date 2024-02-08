package com.mirakl.hybris.core.payment.populators;

import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.mirakl.client.mmp.domain.payment.debit.MiraklOrderPayment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.payment.refund.*;
import com.mirakl.hybris.beans.MiraklRefundRequestData;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MiraklRefundRequestDataPopulatorTest extends AbstractMiraklOrderLineRefundPopulatorTest {
  private static final String MIRAKL_MIRAKL_ORDER_ID = "54545";
  private static final String MIRAKL_COMMERCIAL_ORDER_ID = "786734";
  private static final String MIRAKL_ORDER_LINE_ID = "55215";

  @InjectMocks
  private MiraklRefundRequestDataPopulator testObj;
  @Mock
  private MiraklRefundOrder miraklRefundOrder;
  @Mock
  private MiraklRefundOrderLines miraklRefundOrderLines;
  @Mock
  private MiraklRefundedOrderLine miraklRefundedOrderLine;
  @Mock
  private MiraklRefundsOrderLine miraklRefundsOrderLine;
  @Mock
  private MiraklRefundOrderLine miraklRefundOrderLine;

  @Before
  public void setUp() throws Exception {
    when(miraklRefundOrder.getOrderId()).thenReturn(MIRAKL_MIRAKL_ORDER_ID);
    when(miraklRefundOrder.getOrderCommercialId()).thenReturn(MIRAKL_COMMERCIAL_ORDER_ID);
    when(miraklRefundOrder.getOrderLines()).thenReturn(miraklRefundOrderLines);
    when(miraklRefundOrderLines.getOrderLine()).thenReturn(singletonList(miraklRefundedOrderLine));
    when(miraklRefundedOrderLine.getRefunds()).thenReturn(miraklRefundsOrderLine);
    when(miraklRefundedOrderLine.getOrderLineId()).thenReturn(MIRAKL_ORDER_LINE_ID);
    when(miraklRefundsOrderLine.getRefund()).thenReturn(singletonList(miraklRefundOrderLine));
    when(miraklRefundOrderLine.getAmount()).thenReturn(REFUND_ENTRY_AMOUNT);
    when(miraklRefundOrderLine.getId()).thenReturn(MIRAKL_REFUND_ID);
  }

  @Test
  public void populate() throws Exception {

    List<MiraklRefundRequestData> output = new ArrayList<>();

    testObj.populate(miraklRefundOrder, output);
    assertThat(output.size()).isEqualTo(1);
    MiraklRefundRequestData refund = output.get(0);
    assertThat(refund.getAmount()).isEqualTo(REFUND_ENTRY_AMOUNT);
    assertThat(refund.getMiraklOrderId()).isEqualTo(MIRAKL_MIRAKL_ORDER_ID);
    assertThat(refund.getCommercialOrderId()).isEqualTo(MIRAKL_COMMERCIAL_ORDER_ID);
    assertThat(refund.getMiraklOrderLineId()).isEqualTo(MIRAKL_ORDER_LINE_ID);
    assertThat(refund.getRefundId()).isEqualTo(MIRAKL_REFUND_ID);
  }

}
