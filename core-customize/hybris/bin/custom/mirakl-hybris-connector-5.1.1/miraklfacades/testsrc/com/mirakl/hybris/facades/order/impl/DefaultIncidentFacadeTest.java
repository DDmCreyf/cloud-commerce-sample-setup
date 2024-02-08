package com.mirakl.hybris.facades.order.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.reason.MiraklReason;
import com.mirakl.client.mmp.domain.reason.MiraklReasonType;
import com.mirakl.client.mmp.request.order.message.MiraklCreateOrderMessage;
import com.mirakl.hybris.beans.CreateThreadMessageData;
import com.mirakl.hybris.beans.MessageData;
import com.mirakl.hybris.beans.ReasonData;
import com.mirakl.hybris.core.order.services.IncidentService;
import com.mirakl.hybris.facades.message.MessagingThreadFacade;
import com.mirakl.hybris.facades.setting.ReasonFacade;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIncidentFacadeTest {

  private static final MiraklReasonType INCIDENT_OPEN_REASON_TYPE = MiraklReasonType.INCIDENT_OPEN;
  private static final String CONSIGNMENT_CODE = "1234a7984-45687e6541-12354987-A";
  private static final String CONSIGNMENT_ENTRY_CODE = "1234a7984-45687e6541-12354987-A-1";
  private static final String REASON_CODE = "5";

  @Mock
  private MessagingThreadFacade messagingThreadFacade;
  @Mock
  private Converter<MiraklReason, ReasonData> reasonDataConverter;
  @Mock
  private IncidentService incidentService;
  @Mock
  private ReasonFacade reasonFacade;
  @Mock
  private CreateThreadMessageData createThreadMessageData;
  @Mock
  private UserModel user;
  @Mock
  private ConsignmentModel consignment;
  @Mock
  private ConsignmentEntryModel consignmentEntry;
  @Mock
  private MessageData messageDataOpen;
  @Mock
  private MessageData messageDataClose;
  @Mock
  private MiraklCreateOrderMessage miraklCreateOrderMessageOpen;
  @Mock
  private MiraklCreateOrderMessage miraklCreateOrderMessageClose;
  @Mock
  private Converter<MessageData, MiraklCreateOrderMessage> miraklCreateOrderMessageConverter;

  @InjectMocks
  private DefaultIncidentFacade testObj;

  @Before
  public void setUp() {
    when(consignmentEntry.getConsignment()).thenReturn(consignment);
    when(consignment.getCode()).thenReturn(CONSIGNMENT_CODE);
  }

  @Test
  public void getReasons() {
    testObj.getReasons(INCIDENT_OPEN_REASON_TYPE);

    verify(reasonFacade).getReasons(INCIDENT_OPEN_REASON_TYPE);
  }

  @Test
  public void shouldOpenIncidentWithThread() {
    when(incidentService.openIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE)).thenReturn(consignmentEntry);

    testObj.openIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE, createThreadMessageData);

    verify(incidentService).openIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE);
    verify(messagingThreadFacade).createConsignmentThread(CONSIGNMENT_CODE, createThreadMessageData);
  }

  @Test
  public void openIncidentWithMessage() {
    when(miraklCreateOrderMessageConverter.convert(messageDataOpen)).thenReturn(miraklCreateOrderMessageOpen);

    testObj.openIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE, messageDataOpen);

    verify(incidentService).openIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE, miraklCreateOrderMessageOpen);
  }

  @Test
  public void shouldCloseIncidentWithThread() {
    when(incidentService.closeIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE)).thenReturn(consignmentEntry);

    testObj.closeIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE, createThreadMessageData);

    verify(incidentService).closeIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE);
    verify(messagingThreadFacade).createConsignmentThread(CONSIGNMENT_CODE, createThreadMessageData);
  }

  @Test
  public void closeIncidentWithMessage() {
    when(miraklCreateOrderMessageConverter.convert(messageDataClose)).thenReturn(miraklCreateOrderMessageClose);

    testObj.closeIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE, messageDataClose);

    verify(incidentService).closeIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE, miraklCreateOrderMessageClose);
  }

  @Test
  public void openIncident() {
    testObj.openIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE);

    verify(incidentService).openIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE);
  }

  @Test
  public void closeIncident() {
    testObj.closeIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE);

    verify(incidentService).closeIncident(CONSIGNMENT_ENTRY_CODE, REASON_CODE);
  }

}
