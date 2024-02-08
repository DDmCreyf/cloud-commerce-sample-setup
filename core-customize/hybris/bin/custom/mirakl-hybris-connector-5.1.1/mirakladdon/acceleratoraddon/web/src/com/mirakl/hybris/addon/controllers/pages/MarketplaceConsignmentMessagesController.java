package com.mirakl.hybris.addon.controllers.pages;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Iterables;
import com.mirakl.client.core.exception.MiraklException;
import com.mirakl.hybris.addon.forms.MessageForm;
import com.mirakl.hybris.beans.MessageData;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;
import com.mirakl.hybris.facades.order.MarketplaceConsignmentMessagesFacade;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessage;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.addonsupport.controllers.page.AbstractAddOnPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@Controller
@RequestMapping(value = "/my-account/consignment")
public class MarketplaceConsignmentMessagesController extends AbstractAddOnPageController {

  private static final Logger LOG = Logger.getLogger(MarketplaceConsignmentMessagesController.class);

  protected static final String ACCOUNT_ORDERS_URL = "/my-account/orders";
  protected static final String ACCOUNT_ORDER_URL = "/my-account/order/";
  protected static final String CONSIGNMENT_MESSAGES_ATTRIBUTE = "messages";
  protected static final String CONSIGNMENT_CODE_ATTRIBUTE = "consignmentCode";
  protected static final String ORDER_CODE_ATTRIBUTE = "orderCode";
  protected static final String LAST_CONSIGNMENT_MESSAGE_SUBJECT_ATTRIBUTE = "lastSubject";
  protected static final String CONSIGNMENT_MESSAGES_PAGE = "mirakl-consignment-messages";

  protected ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;
  protected MarketplaceConsignmentService marketplaceConsignmentService;
  protected MarketplaceConsignmentMessagesFacade marketplaceConsignmentMessagesFacade;
  protected MessageSource messageSource;
  protected UserService userService;

  @RequireHardLogIn
  @RequestMapping(value = "/{consignmentCode:.*}/messages", method = RequestMethod.GET)
  public String displayConsignmentMessages(@PathVariable("consignmentCode") String consignmentCode, final Model model,
      final HttpServletRequest request, final HttpServletResponse response, final RedirectAttributes redirectModel)
      throws CMSItemNotFoundException {

    if (!marketplaceConsignmentMessagesFacade.canWriteMessages(consignmentCode)) {
      return FORWARD_PREFIX + "/404";
    }

    try {
      setOrderMessagesPage(model, consignmentCode);
      return getViewForPage(model);
    } catch (MiraklException e) {
      LOG.error(format("An error occurred when retrieving the messages for the consignment [%s]", consignmentCode), e);
      GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "consignment.messages.display.error");
      return REDIRECT_PREFIX + ACCOUNT_ORDERS_URL;
    }
  }

  @RequireHardLogIn
  @RequestMapping(value = "/{consignmentCode:.*}/messages", method = RequestMethod.POST)
  public String postConsignmentMessage(@PathVariable("consignmentCode") String consignmentCode, @Valid final MessageForm message,
      final Model model, final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException {

    CustomerModel currentCustomer = (CustomerModel) userService.getCurrentUser();
    MessageData messageData = new MessageData();
    messageData.setSubject(message.getSubject());
    messageData.setBody(message.getBody());
    messageData.setAuthorId(currentCustomer.getOriginalUid());

    try {
      if (!marketplaceConsignmentMessagesFacade.postMessageForConsignment(consignmentCode, messageData)) {
        return FORWARD_PREFIX + "/404";
      }
      setSingleGlobalMessage(model, GlobalMessages.INFO_MESSAGES_HOLDER, "consignment.messages.post.success");
    } catch (MiraklException e) {
      LOG.error(format("An error occurred from Mirakl when posting a message for the consignment [%s]", consignmentCode), e);
      setSingleGlobalMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER, "consignment.messages.post.error");
    }

    setOrderMessagesPage(model, consignmentCode);
    return getViewForPage(model);
  }

  @ExceptionHandler(UnknownIdentifierException.class)
  public String handleUnknownIdentifierException(final UnknownIdentifierException exception, final HttpServletRequest request) {
    request.setAttribute("message", exception.getMessage());
    return FORWARD_PREFIX + "/404";
  }

  protected void setOrderMessagesPage(Model model, String consignmentCode) throws CMSItemNotFoundException {

    ContentPageModel messagesPage = getCmsPageService().getPageForLabelOrId(CONSIGNMENT_MESSAGES_PAGE);
    storeCmsPageInModel(model, messagesPage);
    storeContentPageTitleInModel(model, messagesPage.getTitle());

    List<MessageData> consignmentMessages = marketplaceConsignmentMessagesFacade.getMessagesForConsignment(consignmentCode);
    MarketplaceConsignmentModel consignment = marketplaceConsignmentService.getMarketplaceConsignmentForCode(consignmentCode);
    String orderCode = consignment.getOrder().getCode();

    model.addAttribute(WebConstants.BREADCRUMBS_KEY, getBreadcrumbs(orderCode, consignmentCode));
    model.addAttribute(CONSIGNMENT_MESSAGES_ATTRIBUTE, consignmentMessages);
    model.addAttribute(CONSIGNMENT_CODE_ATTRIBUTE, consignmentCode);
    model.addAttribute(ORDER_CODE_ATTRIBUTE, orderCode);

    MessageData lastMessage = Iterables.getLast(consignmentMessages, null);
    if (lastMessage != null) {
      model.addAttribute(LAST_CONSIGNMENT_MESSAGE_SUBJECT_ATTRIBUTE, lastMessage.getSubject());
    }
  }

  protected List<Breadcrumb> getBreadcrumbs(String orderCode, String consignmentCode) {
    List<Breadcrumb> breadcrumbs = new ArrayList<>();
    breadcrumbs.add(new Breadcrumb(ACCOUNT_ORDERS_URL,
        messageSource.getMessage("text.account.orderHistory", null, getI18nService().getCurrentLocale()), null));
    breadcrumbs.add(new Breadcrumb(ACCOUNT_ORDER_URL + orderCode,
        messageSource.getMessage("breadcrumb.consignment", new Object[] {consignmentCode}, getI18nService().getCurrentLocale()),
        null));
    breadcrumbs.add(new Breadcrumb("#" + orderCode,
        messageSource.getMessage("breadcrumb.consignment.messages", null, getI18nService().getCurrentLocale()), null));
    return breadcrumbs;
  }

  private Model setSingleGlobalMessage(Model model, String messageType, String messageCode) {
    GlobalMessage message = new GlobalMessage();
    message.setCode(messageCode);
    message.setAttributes(Collections.emptyList());
    model.addAttribute(messageType, Collections.singletonList(message));
    return model;
  }

  @Required
  public void setMarketplaceConsignmentMessagesFacade(MarketplaceConsignmentMessagesFacade marketplaceConsignmentMessagesFacade) {
    this.marketplaceConsignmentMessagesFacade = marketplaceConsignmentMessagesFacade;
  }

  @Required
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  @Required
  public void setMarketplaceConsignmentService(MarketplaceConsignmentService marketplaceConsignmentService) {
    this.marketplaceConsignmentService = marketplaceConsignmentService;
  }

  @Required
  public void setResourceBreadcrumbBuilder(ResourceBreadcrumbBuilder resourceBreadcrumbBuilder) {
    this.resourceBreadcrumbBuilder = resourceBreadcrumbBuilder;
  }

  @Required
  public void setMessageSource(MessageSource messageSource) {
    this.messageSource = messageSource;
  }
}
