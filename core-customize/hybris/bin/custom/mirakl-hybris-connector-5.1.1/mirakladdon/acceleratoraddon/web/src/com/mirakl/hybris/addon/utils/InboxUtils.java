package com.mirakl.hybris.addon.utils;

import static com.mirakl.hybris.addon.constants.MirakladdonWebConstants.INBOX_OPERATOR_AND_SELLER_RECIPIENT;
import static de.hybris.platform.acceleratorstorefrontcommons.tags.Functions.encodeUrl;
import static java.lang.String.format;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.io.FileUtils.getTempDirectory;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.web.multipart.MultipartFile;

import com.mirakl.hybris.addon.forms.ThreadMessageForm;
import com.mirakl.hybris.beans.CreateThreadMessageData;
import com.mirakl.hybris.beans.ThreadRecipientData;
import com.mirakl.hybris.core.enums.MiraklThreadParticipantType;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.servicelayer.i18n.I18NService;

public class InboxUtils {

  public static final String INBOX_URL = "/my-account/inbox";
  public static final String THREAD_URL = "/my-account/inbox/thread";
  public static final String THREAD_ID_URL = "/my-account/inbox/thread/%s";
  public static final String ACCOUNT_ORDER_URL = "/my-account/order/";
  public static final String CONSIGNMENT_CODE_ATTRIBUTE = "consignmentCode";
  public static final String PAGE_TOKEN_ATTRIBUTE = "pageToken";
  public static final String ORDER_CODE_ATTRIBUTE = "orderCode";
  public static final String THREAD_ATTRIBUTE = "thread";
  public static final String THREADS_RESULT_ATTRIBUTE = "threadsResult";
  public static final String THREAD_MESSAGE_FORM_ATTRIBUTE = "threadMessageForm";
  public static final String SELECTABLE_PARTICIPANTS_ATTRIBUTE = "selectableParticipants";
  public static final String ATTACHMENT_MAX_SIZE_ATTRIBUTE = "attachmentFileMaxSize";
  public static final String REASONS_ATTRIBUTE = "reasons";
  public static final String TOPIC_CODE_OTHER_ATTRIBUTE = "topicCodeOther";
  public static final String INBOX_THREAD_DETAIL_PAGE = "mirakl-inbox-thread-details";
  public static final String INBOX_THREADS_PAGE = "mirakl-inbox-threads";

  private InboxUtils() {
    // No instanciation
  }

  public static CreateThreadMessageData convertToCreateThreadMessageData(final ThreadMessageForm threadMessageForm, String shopId)
      throws IOException {

    CreateThreadMessageData messageData = new CreateThreadMessageData();
    messageData.setBody(handleHtmlBreakingLines(threadMessageForm.getBody()));

    if (isNotBlank(threadMessageForm.getTopicValue())) {
      messageData.setTopic(threadMessageForm.getTopicValue());
    } else {
      messageData.setTopic(threadMessageForm.getTopicCodeDisplayValue());
    }

    messageData.setTo(new HashSet<>());
    if (INBOX_OPERATOR_AND_SELLER_RECIPIENT.equals(threadMessageForm.getTo())) {
      messageData.getTo().add(getThreadRecipientData(MiraklThreadParticipantType.SHOP.getCode(), shopId));
      messageData.getTo().add(getThreadRecipientData(MiraklThreadParticipantType.OPERATOR.getCode(), null));
    } else {
      messageData.getTo().add(getThreadRecipientData(threadMessageForm.getTo(), shopId));
    }

    List<File> files = new ArrayList<>();
    messageData.setAttachements(files);

    List<MultipartFile> attachments = threadMessageForm.getFiles();
    if (isNotEmpty(attachments)) {
      for (MultipartFile multipartFile : attachments) {
        if (multipartFile.getSize() == 0) {
          continue;
        }
        File tmpFile = new File(getTempDirectory(), multipartFile.getOriginalFilename());
        multipartFile.transferTo(tmpFile);
        files.add(tmpFile);
      }
    }
    return messageData;
  }

  public static ThreadRecipientData getRecipient(String type, String displayName) {
    ThreadRecipientData recipient = new ThreadRecipientData();
    recipient.setDisplayName(displayName);
    recipient.setType(type);

    return recipient;
  }

  public static ThreadRecipientData getThreadRecipientData(String recipient, String shopId) {
    ThreadRecipientData recipientData = new ThreadRecipientData();
    recipientData.setType(recipient);
    if (MiraklThreadParticipantType.SHOP.getCode().equals(recipient)) {
      recipientData.setId(shopId);
    }
    return recipientData;
  }

  public static String handleHtmlBreakingLines(String content) {
    return content == null ? content : content.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
  }

  public static class InboxBreadcrumbBuilder {
    private List<Breadcrumb> breadcrumbs;
    private MessageSource messageSource;
    private I18NService i18nService;

    public InboxBreadcrumbBuilder(I18NService i18nService, MessageSource messageSource) {
      this.breadcrumbs = new ArrayList<>();
      this.i18nService = i18nService;
      this.messageSource = messageSource;
    }

    public InboxBreadcrumbBuilder inbox() {
      breadcrumbs.add(
          new Breadcrumb(INBOX_URL, messageSource.getMessage("breadcrumb.inbox", null, i18nService.getCurrentLocale()), null));
      return this;
    }

    public InboxBreadcrumbBuilder consignment(String orderCode, String consignmentCode) {
      breadcrumbs.add(new Breadcrumb(ACCOUNT_ORDER_URL + orderCode,
          messageSource.getMessage("breadcrumb.consignment", new Object[] {consignmentCode}, i18nService.getCurrentLocale()),
          null));
      return this;
    }

    public InboxBreadcrumbBuilder thread(String threadId) {
      breadcrumbs.add(new Breadcrumb(format(THREAD_ID_URL, encodeUrl(threadId)),
          messageSource.getMessage("breadcrumb.consignment.thread", null, i18nService.getCurrentLocale()), null));
      return this;
    }

    public List<Breadcrumb> build() {
      return breadcrumbs;
    }
  }

}
