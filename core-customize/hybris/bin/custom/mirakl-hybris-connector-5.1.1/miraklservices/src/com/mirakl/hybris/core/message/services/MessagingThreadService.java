package com.mirakl.hybris.core.message.services;

import java.io.File;
import java.util.List;
import java.util.UUID;

import com.mirakl.client.mmp.domain.message.MiraklThreadCreated;
import com.mirakl.client.mmp.domain.message.thread.MiraklThreadDetails;
import com.mirakl.client.mmp.domain.message.thread.MiraklThreadReplyCreated;
import com.mirakl.client.mmp.domain.message.thread.MiraklThreads;
import com.mirakl.client.mmp.request.order.message.MiraklCreateOrderThread;
import com.mirakl.client.mmp.request.order.message.MiraklThreadReplyMessageInput;
import com.mirakl.hybris.beans.ThreadRequestData;
import com.mirakl.hybris.core.constants.MiraklservicesConstants;

public interface MessagingThreadService {

  /**
   * Retrieves a thread detail from Mirakl using the M10 Mirakl API. <br/>
   * The id of the current user is added implicitly as a request filter (for security reasons)
   *
   * @param threadId the {@link UUID} of the thread to retrieve
   * @return a {@link MiraklThreadDetails}
   */
  MiraklThreadDetails getThreadDetails(UUID threadId);

  /**
   * Replies to a given thread using the M12 Mirakl API
   *
   * @param threadId the {@link UUID} of the thread to reply to
   * @param replyMessageInput the content of the reply
   * @param attachments a list of files to attach optionally to the message
   * @return a {@link MiraklThreadReplyCreated} containing the created message id and the thread id
   */
  MiraklThreadReplyCreated replyToThread(UUID threadId, MiraklThreadReplyMessageInput replyMessageInput, List<File> attachments);

  /**
   * Creates a new thread on a consignment (Mirakl order) using the OR43 Mirakl API
   *
   * @param consignmentCode the consignment (Mirakl order) code on which to create a new thread
   * @param thread the details of the thread to create
   * @param attachments a list of files to attach optionally to the first thread message
   * @return a {@link MiraklThreadCreated} containing the created thread id and the created message id
   */
  MiraklThreadCreated createConsignmentThread(String consignmentCode, MiraklCreateOrderThread thread, List<File> attachments);

  /**
   * Lists threads from Mirakl using the M11 Mirakl API.<br/>
   * The search is paginated by default and uses the {@link MiraklservicesConstants#INBOX_MESSAGES_PAGE_SIZE} property to define
   * the number of records to retrieve. <br/>
   * The id of the current user is added implicitly as a request filter (for security reasons)
   *
   * @param request a bean containing eventual request filters (consignment code, page token,..)
   * @return a {@link MiraklThreads} with the list of the retrieved threads
   */
  MiraklThreads getThreads(ThreadRequestData request);

}
