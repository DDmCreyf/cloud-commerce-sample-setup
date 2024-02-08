package com.mirakl.hybris.addon.controllers.pages;

import static com.mirakl.hybris.addon.constants.MirakladdonWebConstants.*;
import static com.mirakl.hybris.addon.utils.InboxUtils.*;
import static com.mirakl.hybris.core.enums.MiraklThreadEntityType.MMP_ORDER;
import static com.mirakl.hybris.core.enums.MiraklThreadParticipantType.OPERATOR;
import static com.mirakl.hybris.core.enums.MiraklThreadParticipantType.SHOP;
import static de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants.BREADCRUMBS_KEY;
import static de.hybris.platform.acceleratorstorefrontcommons.tags.Functions.encodeUrl;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mirakl.client.domain.common.FileWithContext;
import com.mirakl.client.mmp.domain.message.MiraklThreadCreated;
import com.mirakl.client.mmp.domain.reason.MiraklReasonType;
import com.mirakl.hybris.addon.controllers.MirakladdonControllerConstants;
import com.mirakl.hybris.addon.forms.ThreadMessageForm;
import com.mirakl.hybris.addon.forms.ThreadPostResult;
import com.mirakl.hybris.addon.forms.validation.ThreadFormValidator;
import com.mirakl.hybris.addon.utils.InboxUtils.InboxBreadcrumbBuilder;
import com.mirakl.hybris.beans.CreateThreadMessageData;
import com.mirakl.hybris.beans.ThreadDetailsData;
import com.mirakl.hybris.beans.ThreadListData;
import com.mirakl.hybris.beans.ThreadRecipientData;
import com.mirakl.hybris.beans.ThreadRequestData;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.order.services.MiraklDocumentService;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;
import com.mirakl.hybris.facades.message.MessagingThreadFacade;
import com.mirakl.hybris.facades.setting.ReasonFacade;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.addonsupport.controllers.page.AbstractAddOnPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

@Controller
@RequestMapping(value = "/my-account/inbox")
public class InboxPageController extends AbstractAddOnPageController {

  private static final Logger LOG = Logger.getLogger(InboxPageController.class);

  protected MarketplaceConsignmentService marketplaceConsignmentService;
  protected MiraklDocumentService miraklDocumentService;
  protected ConfigurationService configurationService;
  protected ReasonFacade reasonFacade;
  protected MessagingThreadFacade messagingThreadFacade;
  protected ThreadFormValidator threadFormValidator;
  protected MessageSource messageSource;

  @RequireHardLogIn
  @RequestMapping(method = RequestMethod.GET)
  public String displayInbox(@RequestParam(value = PAGE_TOKEN_ATTRIBUTE, required = false) final String pageToken,
      @RequestParam(value = CONSIGNMENT_CODE_ATTRIBUTE, required = false) final String consignmentCode, final Model model,
      final HttpServletRequest request, final HttpServletResponse response, final RedirectAttributes redirectModel)
      throws CMSItemNotFoundException {

    if (isNotBlank(consignmentCode)) {
      return displayInboxForConsignment(consignmentCode, pageToken, model, redirectModel);
    }
    prepareInboxPage(getThreads(consignmentCode, pageToken), null, pageToken, model);

    return pageToken == null ? getViewForPage(model) : MirakladdonControllerConstants.Fragments.Inbox.threadListLinesFragment;
  }

  @RequireHardLogIn
  @RequestMapping(value = "/thread", method = RequestMethod.GET)
  public String composeNewThread(@RequestParam(value = CONSIGNMENT_CODE_ATTRIBUTE, required = false) final String consignmentCode,
      final Model model, final HttpServletRequest request, final HttpServletResponse response,
      final RedirectAttributes redirectModel) throws CMSItemNotFoundException {

    MarketplaceConsignmentModel consignment = marketplaceConsignmentService.getMarketplaceConsignmentForCode(consignmentCode);
    prepareThreadDetailsPage(consignment, null, model, null);

    return getViewForPage(model);
  }

  @RequireHardLogIn
  @ResponseBody
  @RequestMapping(value = "/thread", method = RequestMethod.POST)
  public ThreadPostResult createNewThread(
      @Valid @ModelAttribute(value = "threadMessageForm") final ThreadMessageForm threadMessageForm,
      final BindingResult bindingResult, final Model model, final HttpServletRequest request, final HttpServletResponse response,
      final RedirectAttributes redirectModel) throws CMSItemNotFoundException {

    ThreadPostResult result = new ThreadPostResult();
    String consignmentCode = threadMessageForm.getConsignmentCode();
    threadFormValidator.validate(threadMessageForm, bindingResult);

    if (bindingResult.hasErrors()) {
      addErrorMessages(bindingResult, result);
      return result;
    }
    result.setValidated(true);

    MarketplaceConsignmentModel consignment = marketplaceConsignmentService.getMarketplaceConsignmentForCode(consignmentCode);
    CreateThreadMessageData createThreadMessageData;
    try {
      createThreadMessageData = convertToCreateThreadMessageData(threadMessageForm, consignment.getShopId());
    } catch (Exception e) {
      LOG.error(format("Could not populate CreateThreadMessageData from ThreadMessageForm for consignment [%s]", consignmentCode),
          e);
      result.setGlobalErrorMessage(getGenericErrorMessage());
      return result;
    }

    MiraklThreadCreated createdThread = messagingThreadFacade.createConsignmentThread(consignmentCode, createThreadMessageData);
    result.setSubmittedSuccessfully(true);
    result.setThreadPageUrl(format(THREAD_ID_URL, encodeUrl(createdThread.getThreadId().toString())));

    return result;
  }

  @RequireHardLogIn
  @RequestMapping(value = "/thread/{threadId:.*}", method = RequestMethod.GET)
  public String showThread(@PathVariable("threadId") String threadId, final Model model, final HttpServletRequest request,
      final HttpServletResponse response, final RedirectAttributes redirectModel) throws CMSItemNotFoundException {

    ThreadDetailsData thread = messagingThreadFacade.getThreadDetails(UUID.fromString(threadId));
    MarketplaceConsignmentModel consignment = getRelatedConsignmentIfApplicable(thread);

    prepareThreadDetailsPage(consignment, thread, model, new ThreadMessageForm());

    return getViewForPage(model);
  }

  @ResponseBody
  @RequireHardLogIn
  @RequestMapping(value = "/thread/{threadId:.*}", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
  public ThreadPostResult postMessageOnthread(@PathVariable("threadId") String threadId,
      @Valid @ModelAttribute(value = "threadMessageForm") final ThreadMessageForm threadMessageForm,
      final BindingResult bindingResult, final Model model, final HttpServletRequest request, final HttpServletResponse response,
      final RedirectAttributes redirectModel) throws CMSItemNotFoundException {

    ThreadDetailsData thread = messagingThreadFacade.getThreadDetails(UUID.fromString(threadId));
    MarketplaceConsignmentModel consignment = getRelatedConsignmentIfApplicable(thread);
    threadFormValidator.validate(threadMessageForm, bindingResult);

    ThreadPostResult result = new ThreadPostResult();
    if (bindingResult.hasErrors()) {
      addErrorMessages(bindingResult, result);
      return result;
    }
    result.setValidated(true);

    CreateThreadMessageData messageData;
    try {
      messageData = convertToCreateThreadMessageData(threadMessageForm, consignment.getShopId());
    } catch (Exception e) {
      LOG.error(
          format("Could not populate CreateThreadMessageData from ThreadMessageForm for consignment [%s]", consignment.getCode()),
          e);
      result.setGlobalErrorMessage(getGenericErrorMessage());
      return result;
    }

    messagingThreadFacade.replyToThread(UUID.fromString(threadId), messageData);
    result.setSubmittedSuccessfully(true);
    result.setThreadPageUrl(format(THREAD_ID_URL, encodeUrl(threadId)));

    return result;
  }

  @RequireHardLogIn
  @ResponseBody
  @RequestMapping(value = "/attachment/{attachmentId:.*}", method = RequestMethod.GET)
  public Resource downloadAttachment(@PathVariable("attachmentId") String attachmentId, final Model model,
      final HttpServletRequest request, final HttpServletResponse response, final RedirectAttributes redirectModel) {
    FileWithContext attachment = messagingThreadFacade.downloadThreadAttachment(attachmentId);
    response.setContentType(attachment.getContentType());
    response.addHeader(CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFilename() + "\"");

    return new FileSystemResource(attachment.getFile());
  }

  protected String displayInboxForConsignment(final String consignmentCode, String pageToken, final Model model,
      final RedirectAttributes redirectModel) throws CMSItemNotFoundException {
    MarketplaceConsignmentModel consignment = marketplaceConsignmentService.getMarketplaceConsignmentForCode(consignmentCode);
    ThreadListData threads = getThreads(consignmentCode, pageToken);
    if (isEmpty(threads.getThreads())) {
      redirectModel.addAttribute(CONSIGNMENT_CODE_ATTRIBUTE, consignmentCode);
      return REDIRECT_PREFIX + THREAD_URL;
    }

    if (threads.getThreads().size() == 1 && isBlank(threads.getPreviousPageToken())) {
      return REDIRECT_PREFIX + format(THREAD_ID_URL, encodeUrl(threads.getThreads().get(0).getId()));
    }

    prepareInboxPage(threads, consignment, pageToken, model);

    return pageToken == null ? getViewForPage(model) : MirakladdonControllerConstants.Fragments.Inbox.threadListLinesFragment;
  }

  protected void prepareInboxPage(ThreadListData threadListData, MarketplaceConsignmentModel consignment, String pageToken,
      final Model model) throws CMSItemNotFoundException {
    if (pageToken == null) {
      ContentPageModel messagesPage = getCmsPageService().getPageForLabelOrId(INBOX_THREADS_PAGE);
      storeCmsPageInModel(model, messagesPage);
      storeContentPageTitleInModel(model, messagesPage.getTitle());
      InboxBreadcrumbBuilder breadcrumbBuilder = new InboxBreadcrumbBuilder(getI18nService(), messageSource).inbox();
      if (consignment != null) {
        model.addAttribute(ORDER_CODE_ATTRIBUTE, consignment.getOrder().getCode());
        model.addAttribute(CONSIGNMENT_CODE_ATTRIBUTE, consignment.getCode());
        breadcrumbBuilder.consignment(consignment.getOrder().getCode(), consignment.getCode());
      }
      model.addAttribute(BREADCRUMBS_KEY, breadcrumbBuilder.build());
    }
    model.addAttribute(THREADS_RESULT_ATTRIBUTE, threadListData);
  }

  protected void prepareThreadDetailsPage(MarketplaceConsignmentModel consignment, ThreadDetailsData thread, final Model model,
      ThreadMessageForm threadMessageForm) throws CMSItemNotFoundException {
    ContentPageModel threadDetailsPage = getCmsPageService().getPageForLabelOrId(INBOX_THREAD_DETAIL_PAGE);

    storeCmsPageInModel(model, threadDetailsPage);
    storeContentPageTitleInModel(model, threadDetailsPage.getTitle());

    String orderCode = consignment.getOrder().getCode();
    List<Breadcrumb> breadcrumb =
        new InboxBreadcrumbBuilder(getI18nService(), messageSource).inbox().consignment(orderCode, consignment.getCode()).build();
    model.addAttribute(BREADCRUMBS_KEY, breadcrumb);
    model.addAttribute(ATTACHMENT_MAX_SIZE_ATTRIBUTE, configurationService.getConfiguration().getLong(ATTACHMENT_MAX_SIZE, 0));
    model.addAttribute(ORDER_CODE_ATTRIBUTE, orderCode);
    model.addAttribute(CONSIGNMENT_CODE_ATTRIBUTE, consignment.getCode());
    if (threadMessageForm == null) {
      threadMessageForm = new ThreadMessageForm();
    }
    threadMessageForm.setConsignmentCode(consignment.getCode());
    model.addAttribute(THREAD_MESSAGE_FORM_ATTRIBUTE, threadMessageForm);

    if (thread != null) {
      model.addAttribute(THREAD_ATTRIBUTE, thread);
      threadMessageForm.setTopicValue(thread.getTopic().getDisplayValue());
    } else {
      model.addAttribute(REASONS_ATTRIBUTE, reasonFacade.getReasons(MiraklReasonType.ORDER_MESSAGING));
      model.addAttribute(TOPIC_CODE_OTHER_ATTRIBUTE, configurationService.getConfiguration().getString(INBOX_TOPIC_CODE_OTHER));
    }
    model.addAttribute(SELECTABLE_PARTICIPANTS_ATTRIBUTE, getRecipients(thread, consignment.getShopName()));
  }

  protected ThreadListData getThreads(final String consignmentCode, final String pageToken) {
    ThreadRequestData threadRequest = new ThreadRequestData();
    threadRequest.setPageToken(pageToken);
    threadRequest.setConsignmentCode(consignmentCode);

    return messagingThreadFacade.getThreads(threadRequest);
  }

  protected List<ThreadRecipientData> getRecipients(ThreadDetailsData thread, String shopName) {
    String operatorName = getCmsSiteService().getCurrentSite().getOperatorName();

    List<ThreadRecipientData> recipients = new ArrayList<ThreadRecipientData>();
    if (thread != null) {
      recipients.addAll(thread.getSelectableParticipants());
    } else {
      recipients.addAll(asList(getRecipient(OPERATOR.getCode(), operatorName), getRecipient(SHOP.getCode(), shopName)));
    }
    recipients.add(getRecipient(INBOX_OPERATOR_AND_SELLER_RECIPIENT, messageSource.getMessage("inbox.thread.form.recipients.both",
        new String[] {operatorName, shopName}, getI18nService().getCurrentLocale())));

    return recipients;
  }

  protected void addErrorMessages(BindingResult bindingResult, ThreadPostResult threadPostResult) {
    for (FieldError fieldError : bindingResult.getFieldErrors()) {
      threadPostResult.getErrorMessages().put(fieldError.getField(), getErrorMessage(fieldError));
    }
    ObjectError globalError = bindingResult.getGlobalError();
    if (globalError != null) {
      threadPostResult.setGlobalErrorMessage(getErrorMessage(globalError));
    }
  }

  protected String getErrorMessage(DefaultMessageSourceResolvable error) {
    String defaultMessage = error.getDefaultMessage();
    if (isBlank(defaultMessage)) {
      defaultMessage = messageSource.getMessage(error.getCode(), null, getI18nService().getCurrentLocale());
    }
    return defaultMessage;
  }

  @ExceptionHandler({UnknownIdentifierException.class, java.lang.IllegalArgumentException.class})
  public String handleUnknownIdentifierException(final UnknownIdentifierException exception, final HttpServletRequest request) {
    request.setAttribute("message", exception.getMessage());
    return FORWARD_PREFIX + "/404";
  }

  protected MarketplaceConsignmentModel getRelatedConsignmentIfApplicable(ThreadDetailsData thread) {
    MarketplaceConsignmentModel consignment = null;
    if (MMP_ORDER.getCode().equals(thread.getEntityType())) {
      consignment = marketplaceConsignmentService.getMarketplaceConsignmentForCode(thread.getEntityId());
    }
    return consignment;
  }

  protected String getGenericErrorMessage() {
    return messageSource.getMessage(INBOX_GENERIC_ERROR_MESSAGE, null, getI18nService().getCurrentLocale());
  }

  @Required
  public void setMessagingThreadFacade(MessagingThreadFacade messagingThreadFacade) {
    this.messagingThreadFacade = messagingThreadFacade;
  }

  @Required
  public void setReasonFacade(ReasonFacade reasonFacade) {
    this.reasonFacade = reasonFacade;
  }

  @Required
  public void setMarketplaceConsignmentService(MarketplaceConsignmentService marketplaceConsignmentService) {
    this.marketplaceConsignmentService = marketplaceConsignmentService;
  }

  @Required
  public void setMiraklDocumentService(MiraklDocumentService miraklDocumentService) {
    this.miraklDocumentService = miraklDocumentService;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setThreadFormValidator(ThreadFormValidator threadFormValidator) {
    this.threadFormValidator = threadFormValidator;
  }

  @Required
  public void setMessageSource(MessageSource messageSource) {
    this.messageSource = messageSource;
  }
}
