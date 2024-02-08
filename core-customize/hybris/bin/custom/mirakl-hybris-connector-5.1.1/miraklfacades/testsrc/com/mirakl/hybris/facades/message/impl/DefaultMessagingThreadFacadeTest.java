package com.mirakl.hybris.facades.message.impl;

import static java.util.Collections.emptyList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.message.thread.MiraklThreadDetails;
import com.mirakl.client.mmp.domain.message.thread.MiraklThreads;
import com.mirakl.client.mmp.request.order.message.MiraklCreateOrderThread;
import com.mirakl.client.mmp.request.order.message.MiraklThreadReplyMessageInput;
import com.mirakl.hybris.beans.CreateThreadMessageData;
import com.mirakl.hybris.beans.ThreadDetailsData;
import com.mirakl.hybris.beans.ThreadListData;
import com.mirakl.hybris.beans.ThreadRequestData;
import com.mirakl.hybris.core.message.services.MessagingThreadService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMessagingThreadFacadeTest {

  private static final String CONSIGNMENT_CODE = "consignment-code";

  @InjectMocks
  private DefaultMessagingThreadFacade miraklThreadFacade;

  @Mock
  private MessagingThreadService messagingThreadService;
  @Mock
  private Converter<CreateThreadMessageData, MiraklThreadReplyMessageInput> miraklThreadReplyMessageInputConverter;
  @Mock
  private Converter<CreateThreadMessageData, MiraklCreateOrderThread> miraklCreateOrderThreadConverter;
  @Mock
  private Converter<MiraklThreads, ThreadListData> threadListDataConverter;
  @Mock
  private Converter<MiraklThreadDetails, ThreadDetailsData> threadDetailsDataConverter;
  @Mock
  private CreateThreadMessageData createThreadMessageData;
  @Mock
  private ThreadRequestData threadRequestData;
  @Mock
  private ThreadDetailsData threadDetailsData;
  @Mock
  private ThreadListData threadListData;
  @Mock
  private MiraklCreateOrderThread createOrderThread;
  @Mock
  private MiraklThreadReplyMessageInput replyMessageInput;
  @Mock
  private MiraklThreads miraklThreads;
  @Mock
  private MiraklThreadDetails threadDetails;
  @Mock
  private File file1, file2;
  private List<File> attachments;

  @Before
  public void setUp() throws Exception {
    attachments = Arrays.asList(file1, file2);
  }

  @Test
  public void shouldGetThreadDetails() {
    UUID threadId = UUID.randomUUID();
    when(messagingThreadService.getThreadDetails(threadId)).thenReturn(threadDetails);
    when(threadDetailsDataConverter.convert(threadDetails)).thenReturn(threadDetailsData);

    ThreadDetailsData result = miraklThreadFacade.getThreadDetails(threadId);

    assertThat(result).isEqualTo(threadDetailsData);
  }

  @Test
  public void shouldCreateConsignmentThread() {
    when(miraklCreateOrderThreadConverter.convert(createThreadMessageData)).thenReturn(createOrderThread);
    when(createThreadMessageData.getAttachements()).thenReturn(attachments);

    miraklThreadFacade.createConsignmentThread(CONSIGNMENT_CODE, createThreadMessageData);

    verify(messagingThreadService).createConsignmentThread(CONSIGNMENT_CODE, createOrderThread, attachments);
  }

  @Test
  public void shouldCreateConsignmentThreadWithNoAttachments() {
    when(miraklCreateOrderThreadConverter.convert(createThreadMessageData)).thenReturn(createOrderThread);
    when(createThreadMessageData.getAttachements()).thenReturn(null);

    miraklThreadFacade.createConsignmentThread(CONSIGNMENT_CODE, createThreadMessageData);

    verify(messagingThreadService).createConsignmentThread(eq(CONSIGNMENT_CODE), eq(createOrderThread), eq(emptyList()));
  }

  @Test
  public void shouldGetThreads() {
    when(messagingThreadService.getThreads(threadRequestData)).thenReturn(miraklThreads);
    when(threadListDataConverter.convert(miraklThreads)).thenReturn(threadListData);

    ThreadListData result = miraklThreadFacade.getThreads(threadRequestData);

    assertThat(result).isEqualTo(threadListData);
  }

  @Test
  public void shouldRepyToThread() {
    UUID threadId = UUID.randomUUID();
    when(miraklThreadReplyMessageInputConverter.convert(createThreadMessageData)).thenReturn(replyMessageInput);
    when(createThreadMessageData.getAttachements()).thenReturn(attachments);

    miraklThreadFacade.replyToThread(threadId, createThreadMessageData);

    verify(messagingThreadService).replyToThread(threadId, replyMessageInput, attachments);
  }
}
