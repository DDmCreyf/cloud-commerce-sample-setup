package com.mirakl.hybris.core.message.services.impl;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.INBOX_MESSAGES_PAGE_SIZE;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mirakl.client.mmp.domain.message.thread.MiraklThread;
import com.mirakl.client.mmp.domain.message.thread.MiraklThreadDetails;
import com.mirakl.client.mmp.domain.message.thread.MiraklThreads;
import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.client.mmp.front.request.order.message.MiraklCreateOrderThreadRequest;
import com.mirakl.client.mmp.front.request.order.message.MiraklGetThreadDetailsRequest;
import com.mirakl.client.mmp.front.request.order.message.MiraklGetThreadsRequest;
import com.mirakl.client.mmp.front.request.order.message.MiraklThreadReplyRequest;
import com.mirakl.client.mmp.request.order.message.MiraklCreateOrderThread;
import com.mirakl.client.mmp.request.order.message.MiraklThreadReplyMessageInput;
import com.mirakl.hybris.beans.ThreadRequestData;
import com.mirakl.hybris.core.enums.MiraklThreadEntityType;
import com.mirakl.hybris.core.order.strategies.MiraklCustomerIdDefinitionStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMessagingThreadServiceTest {

  private static final String CUSTOMER_UID = "customer-uid";
  private static final String CONSIGNEMNT_CODE = "consignemnt-code";
  @Captor
  private ArgumentCaptor<MiraklGetThreadsRequest> getThreadsRequestCaptor;
  @Captor
  private ArgumentCaptor<MiraklGetThreadDetailsRequest> getThreadDetailsRequestCaptor;
  @Captor
  private ArgumentCaptor<MiraklCreateOrderThreadRequest> miraklCreateOrderThreadRequest;
  @Captor
  private ArgumentCaptor<MiraklThreadReplyRequest> miraklThreadReplyRequest;
  @Mock
  private MiraklMarketplacePlatformFrontApi miraklApi;
  @Mock
  private MiraklCustomerIdDefinitionStrategy miraklCustomerIdDefinitionStrategy;
  @Mock
  private UserService userService;
  @Mock
  private ConfigurationService configurationService;
  @Mock
  private Configuration configuration;
  @Mock
  private CustomerModel customerModel;
  @Mock
  private MiraklThread miraklThread1, miraklThread2;
  @Mock
  private MiraklThreadDetails miraklThreadDetails;
  @Mock
  private MiraklCreateOrderThread miraklCreateOrderThread;
  @Mock
  private File file1, file2;
  private List<File> attachments;

  @InjectMocks
  private DefaultMessagingThreadService messagingThreadService;

  @Before
  public void setUp() throws Exception {
    when(userService.getCurrentUser()).thenReturn(customerModel);
    when(miraklCustomerIdDefinitionStrategy.getMiraklCustomerId(customerModel)).thenReturn(CUSTOMER_UID);
    when(configurationService.getConfiguration()).thenReturn(configuration);
    attachments = asList(file1, file2);
  }

  @Test
  public void shouldGetAllThreads() {
    MiraklThreads threads = new MiraklThreads();
    threads.setData(asList(miraklThread1, miraklThread2));
    when(miraklApi.getThreads(getThreadsRequestCaptor.capture())).thenReturn(threads);

    MiraklThreads allThreads = messagingThreadService.getThreads(new ThreadRequestData());

    MiraklGetThreadsRequest request = getThreadsRequestCaptor.getValue();
    assertThat(request.getEntityType()).isEqualTo(MiraklThreadEntityType.MMP_ORDER.getCode());
    assertThat(request.getCustomerId()).isEqualTo(CUSTOMER_UID);
    assertThat(allThreads.getData()).isNotEmpty();
    assertThat(allThreads.getData().size()).isEqualTo(2);
  }

  @Test
  public void shouldGetThreadsForConsignment() {
    MiraklThreads threads = new MiraklThreads();
    threads.setData(asList(miraklThread1));
    when(miraklApi.getThreads(getThreadsRequestCaptor.capture())).thenReturn(threads);

    ThreadRequestData requestData = new ThreadRequestData();
    requestData.setConsignmentCode(CONSIGNEMNT_CODE);
    MiraklThreads threadsForConsignment = messagingThreadService.getThreads(requestData);

    MiraklGetThreadsRequest request = getThreadsRequestCaptor.getValue();
    assertThat(request.getEntityType()).isEqualTo(MiraklThreadEntityType.MMP_ORDER.getCode());
    assertThat(request.getEntityIds()).containsExactly(CONSIGNEMNT_CODE);
    assertThat(threadsForConsignment.getData()).isNotEmpty();
    assertThat(threadsForConsignment.getData().size()).isEqualTo(1);
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionWhenGetThreadsWithNoCurrentUser() {
    when(userService.getCurrentUser()).thenReturn(null);

    messagingThreadService.getThreads(new ThreadRequestData());
  }

  @Test(expected = IllegalStateException.class)
  public void shouldThrowExceptionWhenGetThreadDetailssWithNoCurrentUser() {
    when(userService.getCurrentUser()).thenReturn(null);

    messagingThreadService.getThreadDetails(UUID.randomUUID());
  }

  @Test
  public void shouldHandlePaginationOnGetThreads() {
    int pageSize = 4;
    when(configuration.getInteger(INBOX_MESSAGES_PAGE_SIZE, null)).thenReturn(pageSize);
    MiraklThreads threads = new MiraklThreads();
    threads.setData(Arrays.asList(miraklThread1));
    when(miraklApi.getThreads(getThreadsRequestCaptor.capture())).thenReturn(threads);

    ThreadRequestData requestData = new ThreadRequestData();
    String pageToken = UUID.randomUUID().toString();
    requestData.setPageToken(pageToken);

    messagingThreadService.getThreads(requestData);

    MiraklGetThreadsRequest request = getThreadsRequestCaptor.getValue();
    assertThat(request.getPageToken()).isEqualTo(pageToken);
    assertThat(request.getLimit()).isEqualTo(pageSize);
  }

  @Test
  public void shouldGetThreadDetails() {
    UUID threadUuid = UUID.randomUUID();
    when(miraklApi.getThreadDetails(getThreadDetailsRequestCaptor.capture())).thenReturn(miraklThreadDetails);

    MiraklThreadDetails result = messagingThreadService.getThreadDetails(threadUuid);

    MiraklGetThreadDetailsRequest request = getThreadDetailsRequestCaptor.getValue();
    assertThat(request.getCustomerId()).isEqualTo(CUSTOMER_UID);
    assertThat(request.getThreadId()).isEqualTo(threadUuid);
    assertThat(result).isEqualTo(miraklThreadDetails);
  }

  @Test
  public void  shouldCreateThreadConsignment() {
    messagingThreadService.createConsignmentThread(CONSIGNEMNT_CODE, miraklCreateOrderThread, attachments);

    verify(miraklApi).createOrderThread(miraklCreateOrderThreadRequest.capture());

    MiraklCreateOrderThreadRequest request = miraklCreateOrderThreadRequest.getValue();
    assertThat(request.getAttachments()).isEqualTo(attachments);
    assertThat(request.getOrderId()).isEqualTo(CONSIGNEMNT_CODE);
    assertThat(request.getThread()).isEqualTo(miraklCreateOrderThread);
  }

  @Test
  public void shouldReplyToThread() {
    UUID threadId = UUID.randomUUID();
    MiraklThreadReplyMessageInput replyMessageInput = new MiraklThreadReplyMessageInput();

    messagingThreadService.replyToThread(threadId, replyMessageInput, attachments);

    verify(miraklApi).replyToThread(miraklThreadReplyRequest.capture());

    MiraklThreadReplyRequest request = miraklThreadReplyRequest.getValue();
    assertThat(request.getAttachments()).isEqualTo(attachments);
    assertThat(request.getThreadId()).isEqualTo(threadId);
    assertThat(request.getMessageInput()).isEqualTo(replyMessageInput);
  }
}
