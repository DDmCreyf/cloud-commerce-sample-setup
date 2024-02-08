package com.mirakl.hybris.facades.message;

import java.util.UUID;

import com.mirakl.client.domain.common.FileWithContext;
import com.mirakl.client.mmp.domain.message.MiraklThreadCreated;
import com.mirakl.client.mmp.domain.message.thread.MiraklThreadReplyCreated;
import com.mirakl.hybris.beans.CreateThreadMessageData;
import com.mirakl.hybris.beans.ThreadDetailsData;
import com.mirakl.hybris.beans.ThreadListData;
import com.mirakl.hybris.beans.ThreadRequestData;
import com.mirakl.hybris.core.constants.MiraklservicesConstants;

public interface MessagingThreadFacade {

  /**
   * Lists threads from Mirakl using the M11 Mirakl API.<br/>
   * The search is paginated by default and uses the {@link MiraklservicesConstants#INBOX_MESSAGES_PAGE_SIZE} property to define
   * the number of records to retrieve. <br/>
   * The id of the current user is added implicitly as a request filter (for security reasons)
   *
   * @param request a bean containing eventual request filters (consignment code, page token,..)
   * @return a {@link ThreadListData} with the list of the retrieved threads
   */
  ThreadListData getThreads(ThreadRequestData request);

  /**
   * Retrieves a thread detail from Mirakl using the M10 Mirakl API. <br/>
   * The id of the current user is added implicitly as a request filter (for security reasons)
   *
   * @param threadId the {@link UUID} of the thread to retrieve
   * @return a {@link ThreadDetailsData}
   */
  ThreadDetailsData getThreadDetails(UUID threadId);

  /**
   * Replies to a given thread using the M12 Mirakl API
   *
   * @param threadId the {@link UUID} of the thread to reply to
   * @param createThreadMessageData a bean containing the message data to be posted
   * @return a {@link MiraklThreadReplyCreated} containing the created message id and the thread id
   */
  MiraklThreadReplyCreated replyToThread(UUID threadId, CreateThreadMessageData createThreadMessageData);

  /**
   * Creates a new thread on a consignment (Mirakl order) using the OR43 Mirakl API
   *
   * @param consignmentCode the consignment (Mirakl order) code on which to create a new thread
   * @param createThreadMessageData a bean containing the message data to be posted on the created thread
   * @return a {@link MiraklThreadCreated} containing the created thread id and the created message id
   */
  MiraklThreadCreated createConsignmentThread(String consignmentCode, CreateThreadMessageData createThreadMessageData);

  /**
   * Downloads and returns the file for the given attachment id using the Mirakl M13 API
   *
   * @param attachmentId the id of the attachment to download
   * @return a {@link FileWithContext} object containing the file to download
   */
  FileWithContext downloadThreadAttachment(String attachmentId);
}
