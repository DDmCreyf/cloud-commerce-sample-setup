package com.mirakl.hybris.facades.message.impl;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.io.File;
import java.util.List;
import java.util.UUID;

import com.mirakl.client.domain.common.FileWithContext;
import com.mirakl.hybris.core.order.services.MiraklDocumentService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.domain.message.MiraklThreadCreated;
import com.mirakl.client.mmp.domain.message.thread.MiraklThreadDetails;
import com.mirakl.client.mmp.domain.message.thread.MiraklThreadReplyCreated;
import com.mirakl.client.mmp.domain.message.thread.MiraklThreads;
import com.mirakl.client.mmp.request.order.message.MiraklCreateOrderThread;
import com.mirakl.client.mmp.request.order.message.MiraklThreadReplyMessageInput;
import com.mirakl.hybris.beans.CreateThreadMessageData;
import com.mirakl.hybris.beans.ThreadDetailsData;
import com.mirakl.hybris.beans.ThreadListData;
import com.mirakl.hybris.beans.ThreadRequestData;
import com.mirakl.hybris.core.message.services.MessagingThreadService;
import com.mirakl.hybris.facades.message.MessagingThreadFacade;

import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DefaultMessagingThreadFacade implements MessagingThreadFacade {

  private static final Logger LOG = Logger.getLogger(DefaultMessagingThreadFacade.class);

  protected MessagingThreadService messagingThreadService;
  protected Converter<MiraklThreads, ThreadListData> threadListDataConverter;
  protected Converter<CreateThreadMessageData, MiraklThreadReplyMessageInput> miraklThreadReplyMessageInputConverter;
  protected Converter<CreateThreadMessageData, MiraklCreateOrderThread> miraklCreateOrderThreadConverter;
  protected Converter<MiraklThreadDetails, ThreadDetailsData> threadDetailsDataConverter;
  protected MiraklDocumentService miraklDocumentService;

  @Override
  public ThreadListData getThreads(ThreadRequestData request) {
    return threadListDataConverter.convert(messagingThreadService.getThreads(request));
  }

  @Override
  public ThreadDetailsData getThreadDetails(UUID threadId) {
    return threadDetailsDataConverter.convert(messagingThreadService.getThreadDetails(threadId));
  }

  @Override
  public MiraklThreadReplyCreated replyToThread(UUID threadId, CreateThreadMessageData createThreadMessageData) {
    MiraklThreadReplyCreated threadReply = messagingThreadService.replyToThread(threadId,
        miraklThreadReplyMessageInputConverter.convert(createThreadMessageData), createThreadMessageData.getAttachements());
    cleanUpTemporaryFiles(createThreadMessageData);

    return threadReply;
  }

  @Override
  public MiraklThreadCreated createConsignmentThread(String consignmentCode, CreateThreadMessageData createThreadMessageData) {
    MiraklCreateOrderThread createOrderThread = miraklCreateOrderThreadConverter.convert(createThreadMessageData);
    List<File> attachments =
        createThreadMessageData.getAttachements() != null ? createThreadMessageData.getAttachements() : emptyList();

    MiraklThreadCreated createdThread =
        messagingThreadService.createConsignmentThread(consignmentCode, createOrderThread, attachments);
    cleanUpTemporaryFiles(createThreadMessageData);

    return createdThread;
  }

  @Override
  public FileWithContext downloadThreadAttachment(String attachmentId){
    return miraklDocumentService.downloadThreadAttachment(attachmentId);
  }

  protected void cleanUpTemporaryFiles(CreateThreadMessageData messageData) {
    List<File> attachments = messageData.getAttachements();
    if (isNotEmpty(attachments)) {
      for (File file : attachments) {
        if (!file.delete()) {
          LOG.warn(format("Unable to delete temporary attachment [%s]", file.getAbsolutePath()));
        }
      }
    }
  }


  @Required
  public void setMessagingThreadService(MessagingThreadService messagingThreadService) {
    this.messagingThreadService = messagingThreadService;
  }

  @Required
  public void setThreadListDataConverter(Converter<MiraklThreads, ThreadListData> threadListDataConverter) {
    this.threadListDataConverter = threadListDataConverter;
  }

  @Required
  public void setMiraklThreadReplyMessageInputConverter(
      Converter<CreateThreadMessageData, MiraklThreadReplyMessageInput> miraklThreadReplyMessageInputConverter) {
    this.miraklThreadReplyMessageInputConverter = miraklThreadReplyMessageInputConverter;
  }

  @Required
  public void setMiraklCreateOrderThreadConverter(
      Converter<CreateThreadMessageData, MiraklCreateOrderThread> miraklCreateOrderThreadConverter) {
    this.miraklCreateOrderThreadConverter = miraklCreateOrderThreadConverter;
  }

  @Required
  public void setThreadDetailsDataConverter(Converter<MiraklThreadDetails, ThreadDetailsData> threadDetailsDataConverter) {
    this.threadDetailsDataConverter = threadDetailsDataConverter;
  }

  @Required
  public void setMiraklDocumentService(MiraklDocumentService miraklDocumentService) {
    this.miraklDocumentService = miraklDocumentService;
  }
}
