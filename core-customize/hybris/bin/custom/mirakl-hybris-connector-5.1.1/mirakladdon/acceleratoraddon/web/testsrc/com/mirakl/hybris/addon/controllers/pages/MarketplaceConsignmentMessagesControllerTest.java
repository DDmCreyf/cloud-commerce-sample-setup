package com.mirakl.hybris.addon.controllers.pages;

import static com.mirakl.hybris.addon.controllers.pages.MarketplaceConsignmentMessagesController.ACCOUNT_ORDERS_URL;
import static com.mirakl.hybris.addon.controllers.pages.MarketplaceConsignmentMessagesController.CONSIGNMENT_CODE_ATTRIBUTE;
import static com.mirakl.hybris.addon.controllers.pages.MarketplaceConsignmentMessagesController.CONSIGNMENT_MESSAGES_ATTRIBUTE;
import static com.mirakl.hybris.addon.controllers.pages.MarketplaceConsignmentMessagesController.CONSIGNMENT_MESSAGES_PAGE;
import static com.mirakl.hybris.addon.controllers.pages.MarketplaceConsignmentMessagesController.LAST_CONSIGNMENT_MESSAGE_SUBJECT_ATTRIBUTE;
import static com.mirakl.hybris.addon.controllers.pages.MarketplaceConsignmentMessagesController.ORDER_CODE_ATTRIBUTE;
import static de.hybris.platform.addonsupport.controllers.AbstractAddOnController.FORWARD_PREFIX;
import static de.hybris.platform.addonsupport.controllers.AbstractAddOnController.REDIRECT_PREFIX;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Locale;

import org.fest.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mirakl.client.core.error.MiraklErrorResponseBean;
import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.hybris.addon.forms.MessageForm;
import com.mirakl.hybris.beans.MessageData;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;
import com.mirakl.hybris.facades.order.MarketplaceConsignmentMessagesFacade;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.storefront.util.PageTitleResolver;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessage;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MarketplaceConsignmentMessagesControllerTest {

  private static final String ORDER_CODE = "b3bcad74-1942-43d1-b428-8f7a0351c5cf";
  private static final String CONSIGNMENT_CODE = "b3bcad74-1942-43d1-b428-8f7a0351c5cf-A";
  private static final String TEMPLATE_NAME = "template_name";
  private static final String CONTENT_PAGE_TITLE = "Messages";
  private static final String LAST_MESSAGE_SUBJECT = "I want a refund";
  private static final String MESSAGE_SUBJECT = "I want a refund";
  private static final String MESSAGE_BODY = "My parcel was damaged. I want a refund.";
  private static final String CUSTOMER_ORIGINAL_UID = "chuck.norris@web.com";

  @Mock
  private ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;

  @Mock
  private MarketplaceConsignmentService marketplaceConsignmentService;

  @Mock
  private MarketplaceConsignmentMessagesFacade marketplaceConsignmentMessagesFacade;

  @Mock
  private MessageSource messageSource;

  @Mock
  private UserService userService;

  @Mock
  private Model model;

  @Mock
  private RedirectAttributes redirectModel;

  @Mock
  private CMSPageService cmsPageService;

  @Mock
  private ContentPageModel contentPageModel;

  @Mock
  private MessageData message1, message2;

  @Mock
  private MarketplaceConsignmentModel marketplaceConsignment;

  @Mock
  private I18NService i18NService;

  @Mock
  private PageTitleResolver pageTitleResolver;

  @Mock
  private AbstractOrderModel order;

  @Mock
  private MessageForm messageForm;

  @Mock
  private CustomerModel currentUser;

  @InjectMocks
  private MarketplaceConsignmentMessagesController testObj;

  @Before
  public void setUp() throws Exception {
    when(marketplaceConsignmentMessagesFacade.canWriteMessages(CONSIGNMENT_CODE)).thenReturn(true);
    when(cmsPageService.getFrontendTemplateName(any(PageTemplateModel.class))).thenReturn(TEMPLATE_NAME);
    when(cmsPageService.getPageForLabelOrId(anyString())).thenReturn(contentPageModel);
    when(contentPageModel.getTitle()).thenReturn(CONTENT_PAGE_TITLE);
    when(marketplaceConsignmentMessagesFacade.getMessagesForConsignment(CONSIGNMENT_CODE))
        .thenReturn(Collections.list(message1, message2));
    when(marketplaceConsignmentService.getMarketplaceConsignmentForCode(CONSIGNMENT_CODE)).thenReturn(marketplaceConsignment);
    when(marketplaceConsignment.getOrder()).thenReturn(order);
    when(order.getCode()).thenReturn(ORDER_CODE);
    when(i18NService.getCurrentLocale()).thenReturn(Locale.ENGLISH);
    when(messageSource.getMessage(anyString(), any(Object[].class), anyString(), any(Locale.class))).thenReturn("Breadcrumb");
    when(message2.getSubject()).thenReturn(LAST_MESSAGE_SUBJECT);
    when(messageForm.getSubject()).thenReturn(MESSAGE_SUBJECT);
    when(messageForm.getBody()).thenReturn(MESSAGE_BODY);
    when(userService.getCurrentUser()).thenReturn(currentUser);
    when(currentUser.getOriginalUid()).thenReturn(CUSTOMER_ORIGINAL_UID);
    when(marketplaceConsignmentMessagesFacade.postMessageForConsignment(eq(CONSIGNMENT_CODE), any(MessageData.class)))
        .thenReturn(true);
  }

  @Test
  public void displayConsignmentMessages() throws Exception {
    String output = testObj.displayConsignmentMessages(CONSIGNMENT_CODE, model, null, null, redirectModel);

    verify(marketplaceConsignmentMessagesFacade).canWriteMessages(CONSIGNMENT_CODE);
    assertThat(output).isNotEqualTo(REDIRECT_PREFIX + ACCOUNT_ORDERS_URL);
  }

  @Test
  public void displayConsignmentMessagesWhenError() throws Exception {
    when(marketplaceConsignmentMessagesFacade.getMessagesForConsignment(CONSIGNMENT_CODE))
        .thenThrow(new MiraklApiException(new MiraklErrorResponseBean()));

    String output = testObj.displayConsignmentMessages(CONSIGNMENT_CODE, model, null, null, redirectModel);

    verify(marketplaceConsignmentMessagesFacade).canWriteMessages(CONSIGNMENT_CODE);
    assertThat(output).isEqualTo(REDIRECT_PREFIX + ACCOUNT_ORDERS_URL);
  }

  @Test
  public void postConsignmentMessage() throws Exception {
    ArgumentCaptor<MessageData> messageDataCaptor = ArgumentCaptor.forClass(MessageData.class);

    testObj.postConsignmentMessage(CONSIGNMENT_CODE, messageForm, model, null, null);

    verify(marketplaceConsignmentMessagesFacade).postMessageForConsignment(eq(CONSIGNMENT_CODE), messageDataCaptor.capture());
    assertThat(messageDataCaptor.getValue().getSubject()).isEqualTo(MESSAGE_SUBJECT);
    assertThat(messageDataCaptor.getValue().getBody()).isEqualTo(MESSAGE_BODY);
    assertThat(messageDataCaptor.getValue().getAuthorId()).isEqualTo(CUSTOMER_ORIGINAL_UID);
    verify(marketplaceConsignmentMessagesFacade).postMessageForConsignment(eq(CONSIGNMENT_CODE), any(MessageData.class));
    verify(model).addAttribute(eq(GlobalMessages.INFO_MESSAGES_HOLDER), anyCollectionOf(GlobalMessage.class));
  }

  @Test
  public void postConsignmentMessageWhenPostFails() throws Exception {
    when(marketplaceConsignmentMessagesFacade.postMessageForConsignment(eq(CONSIGNMENT_CODE), any(MessageData.class)))
        .thenReturn(false);

    String output = testObj.postConsignmentMessage(CONSIGNMENT_CODE, messageForm, model, null, null);

    verify(marketplaceConsignmentMessagesFacade).postMessageForConsignment(eq(CONSIGNMENT_CODE), any(MessageData.class));
    assertThat(output).isEqualTo(FORWARD_PREFIX + "/404");
  }

  @Test
  public void postConsignmentMessageWhenError() throws Exception {
    doThrow(new MiraklApiException(new MiraklErrorResponseBean())).when(marketplaceConsignmentMessagesFacade)
        .postMessageForConsignment(anyString(), any(MessageData.class));

    testObj.postConsignmentMessage(CONSIGNMENT_CODE, messageForm, model, null, null);

    verify(marketplaceConsignmentMessagesFacade).postMessageForConsignment(eq(CONSIGNMENT_CODE), any(MessageData.class));
    verify(model).addAttribute(eq(GlobalMessages.ERROR_MESSAGES_HOLDER), anyCollectionOf(GlobalMessage.class));
  }

  @Test
  public void setOrderMessagesPage() throws CMSItemNotFoundException {
    testObj.setOrderMessagesPage(model, CONSIGNMENT_CODE);

    verify(cmsPageService).getPageForLabelOrId(CONSIGNMENT_MESSAGES_PAGE);
    verify(marketplaceConsignmentMessagesFacade).getMessagesForConsignment(CONSIGNMENT_CODE);
    verify(marketplaceConsignmentService).getMarketplaceConsignmentForCode(CONSIGNMENT_CODE);
    verify(model).addAttribute(eq(WebConstants.BREADCRUMBS_KEY), anyCollectionOf(Breadcrumb.class));
    verify(model).addAttribute(eq(CONSIGNMENT_MESSAGES_ATTRIBUTE), anyCollectionOf(MessageData.class));
    verify(model).addAttribute(CONSIGNMENT_CODE_ATTRIBUTE, CONSIGNMENT_CODE);
    verify(model).addAttribute(ORDER_CODE_ATTRIBUTE, ORDER_CODE);
    verify(model).addAttribute(LAST_CONSIGNMENT_MESSAGE_SUBJECT_ATTRIBUTE, LAST_MESSAGE_SUBJECT);
  }

  @Test
  public void getBreadcrumbs() {
    List<Breadcrumb> output = testObj.getBreadcrumbs(ORDER_CODE, CONSIGNMENT_CODE);

    verify(messageSource, times(3)).getMessage(anyString(), any(Object[].class), any(Locale.class));
    assertThat(output).hasSize(3);
  }
}
