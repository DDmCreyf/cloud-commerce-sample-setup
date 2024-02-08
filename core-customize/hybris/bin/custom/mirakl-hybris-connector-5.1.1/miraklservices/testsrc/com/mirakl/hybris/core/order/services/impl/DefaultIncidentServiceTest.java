package com.mirakl.hybris.core.order.services.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.core.error.MiraklErrorResponseBean;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.domain.message.MiraklMessageCreated;
import com.mirakl.client.mmp.domain.reason.MiraklReason;
import com.mirakl.client.mmp.domain.reason.MiraklReasonType;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.request.order.message.MiraklCreateOrderMessageRequest;
import com.mirakl.client.mmp.request.order.incident.MiraklCloseIncidentRequest;
import com.mirakl.client.mmp.request.order.incident.MiraklOpenIncidentRequest;
import com.mirakl.client.mmp.request.order.message.MiraklCreateOrderMessage;
import com.mirakl.hybris.core.enums.MiraklOrderLineStatus;
import com.mirakl.hybris.core.ordersplitting.daos.ConsignmentEntryDao;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;
import com.mirakl.hybris.core.setting.services.ReasonService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIncidentServiceTest {

  private static final String CONSIGNMENT_ENTRY_CODE = "1234567-976543-79840496-A-1";
  private static final String CONSIGNMENT_CODE = "1234567-976543-79840496-A";
  private static final String REASON_CODE = "4";
  private static final String REASON_LABEL = "Order not received.";
  private static final String OPENING_MESSAGE = "I didn't received my order.";
  private static final String EMPTY_MESSAGE = "";
  private static final String CLOSING_MESSAGE = "I found my order next door.";
  private static final String ORDER_INCIDENT = "Order incident: ";

  @Spy
  @InjectMocks
  private DefaultIncidentService testObj;

  @Mock
  private MiraklMarketplacePlatformFrontApi miraklApi;
  @Mock
  private ConsignmentEntryDao consignmentEntryDao;
  @Mock
  private UserModel user, wrongUser;
  @Mock
  private ConsignmentEntryModel consignmentEntry;
  @Mock
  private ConsignmentModel consignment;
  @Mock
  private OrderEntryModel orderEntry;
  @Mock
  private OrderModel order;
  @Mock
  private ProductModel product;
  @Mock
  private ModelService modelService;
  @Mock
  private UserService userService;
  @Mock
  private ReasonService reasonService;
  @Mock
  private MarketplaceConsignmentService marketplaceConsignmentService;
  @Mock
  private MiraklCreateOrderMessage miraklCreateOrderMessageOpen;
  @Mock
  private MiraklCreateOrderMessage miraklCreateOrderMessageClosed;
  @Mock
  private MiraklMessageCreated miraklMessageCreated;
  @Mock
  private MiraklReason miraklReason;

  @Before
  public void setUp() {
    when(consignmentEntry.getCanOpenIncident()).thenReturn(true);
    when(consignmentEntry.getConsignment()).thenReturn(consignment);
    when(consignment.getCode()).thenReturn(CONSIGNMENT_CODE);
    when(marketplaceConsignmentService.getConsignmentEntryForMiraklLineId(CONSIGNMENT_ENTRY_CODE)).thenReturn(consignmentEntry);
    when(miraklReason.getCode()).thenReturn(REASON_CODE);
    when(miraklReason.getLabel()).thenReturn(REASON_LABEL);
  }

  @Test
  public void getReasons() {
    testObj.getReasons();

    verify(reasonService).getReasons();
  }

  @Test
  public void openIncidentWithMessage() {
    when(miraklApi.createOrderMessage(any(MiraklCreateOrderMessageRequest.class))).thenReturn(miraklMessageCreated);

    when(miraklCreateOrderMessageOpen.getBody()).thenReturn(OPENING_MESSAGE);
    when(reasonService.getReasonsByType(MiraklReasonType.INCIDENT_OPEN)).thenReturn(Collections.singletonList(miraklReason));

    testObj.openIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE, miraklCreateOrderMessageOpen);

    verify(marketplaceConsignmentService).getConsignmentEntryForMiraklLineId(CONSIGNMENT_ENTRY_CODE);
    verify(miraklApi).openIncident(any(MiraklOpenIncidentRequest.class));
    verify(consignmentEntry).setCanOpenIncident(false);
    verify(consignmentEntry).setMiraklOrderLineStatus(MiraklOrderLineStatus.INCIDENT_OPEN);
    verify(modelService).save(consignmentEntry);
    verify(miraklCreateOrderMessageOpen).setSubject(eq(ORDER_INCIDENT + REASON_LABEL));
    verify(testObj, times(1)).publishIncidentMessage(MiraklReasonType.INCIDENT_OPEN, CONSIGNMENT_ENTRY_CODE, REASON_CODE,
        miraklCreateOrderMessageOpen, CONSIGNMENT_CODE);
  }

  @Test
  public void openIncidentWithoutMessage() {
    testObj.openIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE, miraklCreateOrderMessageOpen);

    verify(marketplaceConsignmentService).getConsignmentEntryForMiraklLineId(CONSIGNMENT_ENTRY_CODE);
    verify(miraklApi).openIncident(any(MiraklOpenIncidentRequest.class));
    verify(consignmentEntry).setCanOpenIncident(false);
    verify(consignmentEntry).setMiraklOrderLineStatus(MiraklOrderLineStatus.INCIDENT_OPEN);
    verify(modelService).save(consignmentEntry);
    verify(miraklCreateOrderMessageOpen, never()).setSubject(any(String.class));
  }

  @Test
  public void openIncidentWithEmptyMessage() {
    when(miraklCreateOrderMessageOpen.getBody()).thenReturn(EMPTY_MESSAGE);

    testObj.openIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE, miraklCreateOrderMessageOpen);

    verify(marketplaceConsignmentService).getConsignmentEntryForMiraklLineId(CONSIGNMENT_ENTRY_CODE);
    verify(miraklApi).openIncident(any(MiraklOpenIncidentRequest.class));
    verify(consignmentEntry).setCanOpenIncident(false);
    verify(consignmentEntry).setMiraklOrderLineStatus(MiraklOrderLineStatus.INCIDENT_OPEN);
    verify(modelService).save(consignmentEntry);
    verify(miraklCreateOrderMessageOpen, times(0)).setSubject(any(String.class));
  }

  @Test(expected = IllegalStateException.class)
  public void openIncidentWhenWrongState() {
    when(consignmentEntry.getCanOpenIncident()).thenReturn(false);

    testObj.openIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE, miraklCreateOrderMessageOpen);

    verify(marketplaceConsignmentService).getConsignmentEntryForMiraklLineId(CONSIGNMENT_ENTRY_CODE);
    verify(modelService, times(0)).save(consignmentEntry);
  }

  @Test(expected = MiraklApiException.class)
  public void openIncidentShouldNotSaveWhenApiCallFails() {
    doThrow(new MiraklApiException(new MiraklErrorResponseBean())).when(miraklApi)
        .openIncident(any(MiraklOpenIncidentRequest.class));

    testObj.openIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE, miraklCreateOrderMessageOpen);

    verify(marketplaceConsignmentService).getConsignmentEntryForMiraklLineId(CONSIGNMENT_ENTRY_CODE);
    verify(modelService, times(0)).save(consignmentEntry);
  }

  @Test
  public void closeIncidentWithMessage() {
    when(miraklApi.createOrderMessage(any(MiraklCreateOrderMessageRequest.class))).thenReturn(miraklMessageCreated);
    when(consignmentEntry.getCanOpenIncident()).thenReturn(false);
    when(consignmentEntry.getMiraklOrderLineStatus()).thenReturn(MiraklOrderLineStatus.INCIDENT_OPEN);
    when(miraklCreateOrderMessageClosed.getBody()).thenReturn(CLOSING_MESSAGE);
    when(reasonService.getReasonsByType(MiraklReasonType.INCIDENT_CLOSE)).thenReturn(Collections.singletonList(miraklReason));

    testObj.closeIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE, miraklCreateOrderMessageClosed);

    verify(miraklCreateOrderMessageClosed).setSubject(eq(ORDER_INCIDENT + REASON_LABEL));
    verify(marketplaceConsignmentService).getConsignmentEntryForMiraklLineId(CONSIGNMENT_ENTRY_CODE);
    verify(miraklApi).closeIncident(any(MiraklCloseIncidentRequest.class));
    verify(consignmentEntry).setCanOpenIncident(true);
    verify(consignmentEntry).setMiraklOrderLineStatus(MiraklOrderLineStatus.INCIDENT_CLOSED);
    verify(modelService).save(consignmentEntry);
    verify(testObj).publishIncidentMessage(MiraklReasonType.INCIDENT_CLOSE, CONSIGNMENT_ENTRY_CODE, REASON_CODE,
        miraklCreateOrderMessageClosed, CONSIGNMENT_CODE);

  }

  @Test
  public void closeIncidentWithoutMessage() {
    when(consignmentEntry.getCanOpenIncident()).thenReturn(false);
    when(consignmentEntry.getMiraklOrderLineStatus()).thenReturn(MiraklOrderLineStatus.INCIDENT_OPEN);

    testObj.closeIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE, miraklCreateOrderMessageClosed);

    verify(marketplaceConsignmentService).getConsignmentEntryForMiraklLineId(CONSIGNMENT_ENTRY_CODE);
    verify(miraklApi).closeIncident(any(MiraklCloseIncidentRequest.class));
    verify(consignmentEntry).setCanOpenIncident(true);
    verify(consignmentEntry).setMiraklOrderLineStatus(MiraklOrderLineStatus.INCIDENT_CLOSED);
    verify(modelService).save(consignmentEntry);
    verify(miraklCreateOrderMessageOpen, times(0)).setSubject(any(String.class));
  }

  @Test
  public void closeIncidentWithWhitespacesMessage() {
    when(consignmentEntry.getCanOpenIncident()).thenReturn(false);
    when(consignmentEntry.getMiraklOrderLineStatus()).thenReturn(MiraklOrderLineStatus.INCIDENT_OPEN);

    testObj.closeIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE, miraklCreateOrderMessageClosed);

    verify(marketplaceConsignmentService).getConsignmentEntryForMiraklLineId(CONSIGNMENT_ENTRY_CODE);
    verify(miraklApi).closeIncident(any(MiraklCloseIncidentRequest.class));
    verify(consignmentEntry).setCanOpenIncident(true);
    verify(consignmentEntry).setMiraklOrderLineStatus(MiraklOrderLineStatus.INCIDENT_CLOSED);
    verify(modelService).save(consignmentEntry);
    verify(miraklCreateOrderMessageOpen, times(0)).setSubject(any(String.class));
  }

  @Test(expected = IllegalStateException.class)
  public void closeIncidentWhenOpeningIncidentIsPossible() {
    testObj.closeIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE, miraklCreateOrderMessageClosed);

    verify(marketplaceConsignmentService).getConsignmentEntryForMiraklLineId(CONSIGNMENT_ENTRY_CODE);
    verify(modelService, times(0)).save(consignmentEntry);
  }

  @Test(expected = IllegalStateException.class)
  public void closeIncidentWhenWrongState() {
    when(consignmentEntry.getCanOpenIncident()).thenReturn(false);
    when(consignmentEntry.getMiraklOrderLineStatus()).thenReturn(MiraklOrderLineStatus.INCIDENT_CLOSED);

    testObj.closeIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE, miraklCreateOrderMessageClosed);

    verify(marketplaceConsignmentService).getConsignmentEntryForMiraklLineId(CONSIGNMENT_ENTRY_CODE);
    verify(modelService, times(0)).save(consignmentEntry);
  }

  @Test(expected = MiraklApiException.class)
  public void closeIncidentShouldNotSaveWhenApiCallFails() {
    when(consignmentEntry.getCanOpenIncident()).thenReturn(false);
    when(consignmentEntry.getMiraklOrderLineStatus()).thenReturn(MiraklOrderLineStatus.INCIDENT_OPEN);

    doThrow(new MiraklApiException(new MiraklErrorResponseBean())).when(miraklApi)
        .closeIncident(any(MiraklCloseIncidentRequest.class));

    testObj.closeIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE, miraklCreateOrderMessageClosed);

    verify(marketplaceConsignmentService).getConsignmentEntryForMiraklLineId(CONSIGNMENT_ENTRY_CODE);
    verify(modelService, times(0)).save(consignmentEntry);
  }

}
