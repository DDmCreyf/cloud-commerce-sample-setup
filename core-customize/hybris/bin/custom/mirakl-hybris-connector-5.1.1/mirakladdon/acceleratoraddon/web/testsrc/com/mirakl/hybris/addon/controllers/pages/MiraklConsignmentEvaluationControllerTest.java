package com.mirakl.hybris.addon.controllers.pages;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.mirakl.hybris.addon.forms.AssessmentForm;
import com.mirakl.hybris.addon.forms.ConsignmentEvaluationForm;
import com.mirakl.hybris.addon.forms.validation.ConsignmentEvaluationValidator;
import com.mirakl.hybris.beans.AssessmentData;
import com.mirakl.hybris.beans.EvaluationData;
import com.mirakl.hybris.core.enums.MiraklOrderStatus;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;
import com.mirakl.hybris.facades.order.MarketplaceConsignmentFacade;
import com.mirakl.hybris.facades.order.MarketplaceOrderFacade;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.storefront.util.PageTitleResolver;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.addonsupport.controllers.page.AbstractAddOnPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Copyright (C) 2016 Mirakl. www.mirakl.com - info@mirakl.com All Rights Reserved. Tous droits réservés.
 */

@UnitTest
@RunWith(value = MockitoJUnitRunner.class)
public class MiraklConsignmentEvaluationControllerTest {

  public static final String CONSIGNMENT_CODE = "consignmentCode";
  public static final String MIRAKL_CONSIGNMENT_EVALUATION_PAGE = "mirakl-consignment-evaluation";
  public static final String PAGE_TITLE = "pageTitle";
  public static final String ORDER_CODE = "orderCode";

  @Mock
  private MarketplaceConsignmentService marketplaceConsignmentService;
  @Mock
  private MarketplaceConsignmentModel marketplaceConsignmentModel;
  @Mock
  private Model model;
  @Mock
  private CMSPageService cmsPageService;
  @Mock
  private ContentPageModel contentPageModel;
  @Mock
  private PageTitleResolver pageTitleResolver;
  @Mock
  private ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;
  @Mock
  private UserService userService;
  @Mock
  private UserModel user;
  @Mock
  private MarketplaceOrderFacade marketplaceOrderFacade;
  @Mock
  private AbstractOrderModel order;
  @Mock
  private ConsignmentEvaluationValidator formValidator;
  @Mock
  private BindingResult bindingResult;
  @Mock
  private MarketplaceConsignmentFacade consignmentFacade;
  @Mock
  private MessageSource messageSource;
  @Mock
  private I18NService i18NService;

  private AssessmentData assessmentData = new AssessmentData();
  private ConsignmentEvaluationForm evaluationForm = new ConsignmentEvaluationForm();

  @InjectMocks
  private MiraklConsignmentEvaluationController testObj;

  @Before
  public void setUp() throws Exception {
    contentPageModel.setTitle(PAGE_TITLE);
    evaluationForm.setAssessments(Collections.<AssessmentForm>emptyList());
    when(marketplaceConsignmentService.getMarketplaceConsignmentForCode(CONSIGNMENT_CODE))
        .thenReturn(marketplaceConsignmentModel);
    when(marketplaceConsignmentService.confirmConsignmentReceptionForCode(CONSIGNMENT_CODE, user))
        .thenReturn(marketplaceConsignmentModel);
    when(marketplaceConsignmentModel.getMiraklOrderStatus()).thenReturn(MiraklOrderStatus.SHIPPED);
    when(marketplaceConsignmentModel.getOrder()).thenReturn(order);
    when(cmsPageService.getPageForLabelOrId(MIRAKL_CONSIGNMENT_EVALUATION_PAGE)).thenReturn(contentPageModel);
    when(pageTitleResolver.resolveContentPageTitle(PAGE_TITLE)).thenReturn(PAGE_TITLE);
    when(userService.getCurrentUser()).thenReturn(user);
    when(marketplaceOrderFacade.getAssessments()).thenReturn(Collections.singletonList(assessmentData));
    when(order.getCode()).thenReturn(ORDER_CODE);
    when(bindingResult.hasErrors()).thenReturn(false);
  }

  @Test
  public void consignmentReceptionConfirmation() throws Exception {
    testObj.consignmentReceptionConfirmation(CONSIGNMENT_CODE, model, null, null, null);

    verify(marketplaceConsignmentService).getMarketplaceConsignmentForCode(CONSIGNMENT_CODE);
    verify(marketplaceConsignmentService).confirmConsignmentReceptionForCode(CONSIGNMENT_CODE, user);
    verify(model).containsAttribute(AbstractAddOnPageController.CMS_PAGE_MODEL);
  }

  @Test
  public void sendConsignmentEvaluation() throws CMSItemNotFoundException {
    testObj.sendConsignmentEvaluation(CONSIGNMENT_CODE, model, evaluationForm, bindingResult, null, null, null);

    verify(formValidator).validate(evaluationForm, bindingResult);
    verify(consignmentFacade).postEvaluation(anyString(), any(EvaluationData.class), any(UserModel.class));
  }

  @Test
  public void sendConsignmentEvaluationShouldDisplayAnErrorWhenFormNotFilledProperly() throws CMSItemNotFoundException {
    when(bindingResult.hasErrors()).thenReturn(true);

    testObj.sendConsignmentEvaluation(CONSIGNMENT_CODE, model, evaluationForm, bindingResult, null, null, null);

    verify(model).addAttribute(MiraklConsignmentEvaluationController.FORM_ERROR_MESSAGE_ATTRIBUTE,
        "consignment.evaluation.form.error");
  }
}
