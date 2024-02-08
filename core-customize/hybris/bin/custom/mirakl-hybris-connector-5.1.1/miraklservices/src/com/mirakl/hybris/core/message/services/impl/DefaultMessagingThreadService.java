package com.mirakl.hybris.core.message.services.impl;

import static com.mirakl.hybris.core.constants.MiraklservicesConstants.INBOX_MESSAGES_PAGE_SIZE;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.message.MiraklThreadCreated;
import com.mirakl.client.mmp.domain.message.thread.MiraklThreadDetails;
import com.mirakl.client.mmp.domain.message.thread.MiraklThreadReplyCreated;
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
import com.mirakl.hybris.core.message.services.MessagingThreadService;
import com.mirakl.hybris.core.order.strategies.MiraklCustomerIdDefinitionStrategy;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;

public class DefaultMessagingThreadService implements MessagingThreadService {

  protected UserService userService;
  protected ConfigurationService configurationService;
  protected MiraklCustomerIdDefinitionStrategy miraklCustomerIdDefinitionStrategy;
  protected MiraklMarketplacePlatformFrontApi mmpApi;

  @Override
  public MiraklThreadDetails getThreadDetails(UUID threadId) {
    UserModel currentUser = userService.getCurrentUser();
    if (currentUser == null) {
      throw new IllegalStateException(
          format("Cannot resolve current user in order to retrieve his thread details from Mirakl ([threadId=%s])", threadId));
    }

    MiraklGetThreadDetailsRequest miraklGetThreadDetailsRequest = new MiraklGetThreadDetailsRequest(threadId);
    miraklGetThreadDetailsRequest
        .setCustomerId(miraklCustomerIdDefinitionStrategy.getMiraklCustomerId((CustomerModel) currentUser));

    return mmpApi.getThreadDetails(miraklGetThreadDetailsRequest);
  }

  @Override
  public MiraklThreadReplyCreated replyToThread(UUID threadId, MiraklThreadReplyMessageInput replyMessageInput,
      List<File> attachments) {
    return mmpApi.replyToThread(new MiraklThreadReplyRequest(threadId, replyMessageInput, attachments));
  }

  @Override
  public MiraklThreadCreated createConsignmentThread(String consignment, MiraklCreateOrderThread thread, List<File> attachments) {
    return mmpApi.createOrderThread(new MiraklCreateOrderThreadRequest(consignment, thread, attachments));
  }

  @Override
  public MiraklThreads getThreads(ThreadRequestData request) {

    UserModel currentUser = userService.getCurrentUser();
    if (currentUser == null) {
      throw new IllegalStateException("Cannot resolve current user in order to retrieve his messages from Mirakl");
    }
    MiraklGetThreadsRequest miraklGetThreadsRequest = new MiraklGetThreadsRequest();
    miraklGetThreadsRequest.setCustomerId(miraklCustomerIdDefinitionStrategy.getMiraklCustomerId((CustomerModel) currentUser));
    miraklGetThreadsRequest.setEntityType(MiraklThreadEntityType.MMP_ORDER.getCode());
    Integer inboxMessagesPageSize = configurationService.getConfiguration().getInteger(INBOX_MESSAGES_PAGE_SIZE, null);
    miraklGetThreadsRequest.setLimit(inboxMessagesPageSize);
    if (isNotBlank(request.getConsignmentCode())) {
      miraklGetThreadsRequest.setEntityId(request.getConsignmentCode());
    }
    if (isNotBlank(request.getPageToken())) {
      miraklGetThreadsRequest.setPageToken(request.getPageToken());
    }

    return mmpApi.getThreads(miraklGetThreadsRequest);
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  @Required
  public void setMiraklCustomerIdDefinitionStrategy(MiraklCustomerIdDefinitionStrategy miraklCustomerIdDefinitionStrategy) {
    this.miraklCustomerIdDefinitionStrategy = miraklCustomerIdDefinitionStrategy;
  }

  @Required
  public void setMmpApi(MiraklMarketplacePlatformFrontApi mmpApi) {
    this.mmpApi = mmpApi;
  }

}
