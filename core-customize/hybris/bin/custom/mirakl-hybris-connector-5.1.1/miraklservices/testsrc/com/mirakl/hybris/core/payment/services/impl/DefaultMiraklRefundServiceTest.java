package com.mirakl.hybris.core.payment.services.impl;

import com.mirakl.hybris.beans.MiraklRefundRequestData;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;
import com.mirakl.hybris.core.returns.strategies.MiraklRefundValidationStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.returns.ReturnService;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMiraklRefundServiceTest {
  private static final String MIRAKL_REFUND_ID = "mirakl-refund-id";
  private static final String ORDER_LINE_ID = "order-line-id";
  private static final long QUANTITY = 3l;

  @InjectMocks
  private DefaultMiraklRefundService refundService;

  @Mock
  private MarketplaceConsignmentService marketplaceConsignmentService;
  @Mock
  private ReturnService returnService;
  @Mock
  private ModelService modelService;
  @Mock
  private MiraklRefundValidationStrategy validationStrategy;
  @Mock
  private MiraklRefundRequestData refundRequestData;
  @Mock
  private MarketplaceConsignmentModel consignment;
  @Mock
  private ConsignmentEntryModel consignmentEntry;
  @Mock
  private AbstractOrderEntryModel orderEntry;
  @Mock
  private OrderModel order;
  @Mock
  private ReturnRequestModel returnRequest;
  @Mock
  private RefundEntryModel refundEntry;

  @Before
  public void setUp() {
    when(refundRequestData.getMiraklOrderLineId()).thenReturn(ORDER_LINE_ID);
    when(refundRequestData.getRefundId()).thenReturn(MIRAKL_REFUND_ID);
    when(marketplaceConsignmentService.getConsignmentEntryForMiraklLineId(ORDER_LINE_ID)).thenReturn(consignmentEntry);
    when(consignmentEntry.getConsignment()).thenReturn(consignment);
    when(consignmentEntry.getQuantity()).thenReturn(QUANTITY);
    when(consignmentEntry.getOrderEntry()).thenReturn(orderEntry);
    when(consignment.getOrder()).thenReturn(order);
    when(returnService.createReturnRequest(order)).thenReturn(returnRequest);
    when(returnService.createRefund(returnRequest, orderEntry, null, QUANTITY, ReturnAction.IMMEDIATE,
        RefundReason.MARKETPLACE_SELLER_REFUND)).thenReturn(refundEntry);
    when(validationStrategy.isValidRefundRequest(any(MiraklRefundRequestData.class))).thenReturn(true);
  }

  @Test
  public void shouldCreateReturnRequestAndRefundEntry() {
    refundService.saveReceivedRefundRequest(refundRequestData);

    verify(returnService).createReturnRequest(order);
    verify(returnService).createRefund(returnRequest, orderEntry, null, QUANTITY, ReturnAction.IMMEDIATE,
        RefundReason.MARKETPLACE_SELLER_REFUND);
    verify(refundEntry).setMiraklRefundId(MIRAKL_REFUND_ID);
    verify(modelService).save(refundEntry);
  }

  @Test
  public void shouldIgnoreInvalidRefundRequests() {
    when(validationStrategy.isValidRefundRequest(refundRequestData)).thenReturn(false);

    refundService.saveReceivedRefundRequest(refundRequestData);

    verifyZeroInteractions(returnService, modelService);
  }

}
