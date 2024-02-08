package com.mirakl.hybris.addon.controllers.pages;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mirakl.hybris.addon.forms.AssessmentForm;
import com.mirakl.hybris.addon.forms.ConsignmentEvaluationForm;
import com.mirakl.hybris.addon.forms.validation.ConsignmentEvaluationValidator;
import com.mirakl.hybris.beans.AssessmentData;
import com.mirakl.hybris.beans.EvaluationData;
import com.mirakl.hybris.core.ordersplitting.services.MarketplaceConsignmentService;
import com.mirakl.hybris.facades.order.MarketplaceConsignmentFacade;
import com.mirakl.hybris.facades.order.MarketplaceOrderFacade;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.addonsupport.controllers.page.AbstractAddOnPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;

@RequestMapping(value = "/my-account/consignment")
public class MiraklConsignmentEvaluationController extends AbstractAddOnPageController {

  private static final Logger LOG = Logger.getLogger(MiraklConsignmentEvaluationController.class);

  protected static final String ACCOUNT_ORDERS_URL = "/my-account/orders";
  protected static final String ACCOUNT_ORDER_URL = "/my-account/order/";
  protected static final String CONSIGNMENT_CODE_VARIABLE_PATTERN = "/{consignmentCode:.*}";
  protected static final String REDIRECT_TO_ORDER_DETAIL_PAGE = REDIRECT_PREFIX + ACCOUNT_ORDER_URL;
  protected static final String CONSIGNMENT_CODE_ATTRIBUTE = "consignmentCode";
  protected static final String ORDER_CODE_ATTRIBUTE = "orderCode";
  protected static final String MIRAKL_CONSIGNMENT_EVALUATION_PAGE = "mirakl-consignment-evaluation";
  protected static final String MIRAKL_ASSESSMENTS_ATTRIBUTE = "miraklAssessments";
  protected static final String CONSIGNMENT_EVALUATION_FORM_ATTRIBUTE = "consignmentEvaluationForm";
  protected static final String FORM_ERROR_MESSAGE_ATTRIBUTE = "formErrorMessage";

  protected MarketplaceConsignmentService marketplaceConsignmentService;
  protected ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;
  protected MarketplaceOrderFacade marketplaceOrderFacade;
  protected MarketplaceConsignmentFacade marketplaceConsignmentFacade;
  protected ConsignmentEvaluationValidator formValidator;
  protected UserService userService;
  protected MessageSource messageSource;

  @RequireHardLogIn
  @RequestMapping(value = CONSIGNMENT_CODE_VARIABLE_PATTERN + "/receive", method = RequestMethod.POST)
  public String consignmentReceptionConfirmation(@PathVariable("consignmentCode") String consignmentCode, final Model model,
      final HttpServletRequest request, final HttpServletResponse response, final RedirectAttributes redirectModel)
          throws CMSItemNotFoundException {

    AbstractOrderModel order = marketplaceConsignmentService.getMarketplaceConsignmentForCode(consignmentCode).getOrder();

    try {
      marketplaceConsignmentFacade.confirmConsignmentReceptionForCode(consignmentCode, userService.getCurrentUser());
    } catch (IllegalStateException e) {
      LOG.warn("Attempted to receive a consignment in an incorrect state", e);
      GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "system.error.page.not.found", null);
      return REDIRECT_TO_ORDER_DETAIL_PAGE + order.getCode();
    }

    storeConsignmentEvaluationViewInModel(model, consignmentCode, order.getCode());
    return getViewForPage(model);
  }

  @RequireHardLogIn
  @RequestMapping(value = CONSIGNMENT_CODE_VARIABLE_PATTERN + "/evaluate", method = RequestMethod.GET)
  public String consignmentEvaluationPage(@PathVariable("consignmentCode") String consignmentCode, final Model model,
      final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException {
    AbstractOrderModel order = marketplaceConsignmentService.getMarketplaceConsignmentForCode(consignmentCode).getOrder();
    storeConsignmentEvaluationViewInModel(model, consignmentCode, order.getCode());
    return getViewForPage(model);
  }

  @RequireHardLogIn
  @RequestMapping(value = CONSIGNMENT_CODE_VARIABLE_PATTERN + "/evaluate", method = RequestMethod.POST)
  public String sendConsignmentEvaluation(@PathVariable("consignmentCode") String consignmentCode, final Model model,
      @Valid final ConsignmentEvaluationForm form, BindingResult bindingResult, final HttpServletRequest request,
      final HttpServletResponse response, final RedirectAttributes redirectModel) throws CMSItemNotFoundException {

    AbstractOrderModel order = marketplaceConsignmentService.getMarketplaceConsignmentForCode(consignmentCode).getOrder();

    formValidator.validate(form, bindingResult);
    if (bindingResult.hasErrors()) {
      storeConsignmentEvaluationViewInModel(model, consignmentCode, order.getCode(), form);
      model.addAttribute(FORM_ERROR_MESSAGE_ATTRIBUTE, "consignment.evaluation.form.error");
      return getViewForPage(model);
    }

    EvaluationData evaluation = getEvaluationData(form);

    try {
      marketplaceConsignmentFacade.postEvaluation(consignmentCode, evaluation, userService.getCurrentUser());
    } catch (IllegalStateException e) {
      LOG.warn("Attempted to evaluate a consignment in an incorrect state", e);
      GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "consignment.evaluation.already.posted",
          null);
    }
    return REDIRECT_TO_ORDER_DETAIL_PAGE + order.getCode();
  }

  private EvaluationData getEvaluationData(ConsignmentEvaluationForm form) {
    EvaluationData evaluation = new EvaluationData();
    evaluation.setComment(form.getComment());
    evaluation.setGrade(form.getSellerGrade());
    List<AssessmentData> assessments = new ArrayList<>();
    for (AssessmentForm assessmentForm : form.getAssessments()) {
      AssessmentData assessment = new AssessmentData();
      assessment.setCode(assessmentForm.getCode());
      assessment.setResponse(assessmentForm.getResponse());
      assessments.add(assessment);
    }
    evaluation.setAssessments(assessments);
    return evaluation;
  }

  private void storeConsignmentEvaluationViewInModel(Model model, String consignmentCode, String orderCode, ConsignmentEvaluationForm existingForm)
      throws CMSItemNotFoundException {
    storeConsignmentEvaluationViewInModel(model, consignmentCode, orderCode);
    model.addAttribute(CONSIGNMENT_EVALUATION_FORM_ATTRIBUTE, existingForm);
  }

  private void storeConsignmentEvaluationViewInModel(Model model, String consignmentCode, String orderCode) throws CMSItemNotFoundException {
    ContentPageModel consignmentEvaluationPage = getCmsPageService().getPageForLabelOrId(MIRAKL_CONSIGNMENT_EVALUATION_PAGE);
    storeCmsPageInModel(model, consignmentEvaluationPage);
    storeContentPageTitleInModel(model, consignmentEvaluationPage.getTitle());
    model.addAttribute(CONSIGNMENT_CODE_ATTRIBUTE, consignmentCode);
    model.addAttribute(ORDER_CODE_ATTRIBUTE, orderCode);
    List<AssessmentData> assessments = marketplaceOrderFacade.getAssessments();
    model.addAttribute(MIRAKL_ASSESSMENTS_ATTRIBUTE, assessments);
    model.addAttribute(CONSIGNMENT_EVALUATION_FORM_ATTRIBUTE, generateEmptyEvaluationForm(assessments));
    model.addAttribute(WebConstants.BREADCRUMBS_KEY, getBreadcrumbs(orderCode, consignmentCode));
  }

  private ConsignmentEvaluationForm generateEmptyEvaluationForm(List<AssessmentData> assessmentsFromMirakl) {
    ConsignmentEvaluationForm evaluationForm = new ConsignmentEvaluationForm();
    evaluationForm.setAssessments(generateAssessmentForms(assessmentsFromMirakl));
    return evaluationForm;
  }

  private ArrayList<AssessmentForm> generateAssessmentForms(List<AssessmentData> assessmentsFromMirakl) {
    ArrayList<AssessmentForm> assessmentForms = new ArrayList<>();
    for (AssessmentData assessmentData : assessmentsFromMirakl) {
      assessmentForms.add(generateAssessment(assessmentData.getCode()));
    }
    return assessmentForms;
  }

  private AssessmentForm generateAssessment(String code) {
    AssessmentForm assessmentForm = new AssessmentForm();
    assessmentForm.setCode(code);
    return assessmentForm;
  }

  @ExceptionHandler(UnknownIdentifierException.class)
  public String handleUnknownIdentifierException(final UnknownIdentifierException exception, final HttpServletRequest request) {
    request.setAttribute("message", exception.getMessage());
    return FORWARD_PREFIX + "/404";
  }

  protected List<Breadcrumb> getBreadcrumbs(String orderCode, String consignmentCode) {
    List<Breadcrumb> breadcrumbs = new ArrayList<>();
    breadcrumbs.add(new Breadcrumb(ACCOUNT_ORDERS_URL,
        messageSource.getMessage("text.account.orderHistory", null, getI18nService().getCurrentLocale()), null));
    breadcrumbs.add(new Breadcrumb(ACCOUNT_ORDER_URL + orderCode,
        messageSource.getMessage("breadcrumb.consignment", new Object[] {consignmentCode}, getI18nService().getCurrentLocale()),
        null));
    breadcrumbs.add(new Breadcrumb("#", messageSource.getMessage("breadcrumb.consignment.evaluation", null, getI18nService().getCurrentLocale()), null));
    return breadcrumbs;
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
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  @Required
  public void setMarketplaceOrderFacade(MarketplaceOrderFacade marketplaceOrderFacade) {
    this.marketplaceOrderFacade = marketplaceOrderFacade;
  }

  @Required
  public void setMarketplaceConsignmentFacade(MarketplaceConsignmentFacade marketplaceConsignmentFacade) {
    this.marketplaceConsignmentFacade = marketplaceConsignmentFacade;
  }

  @Required
  public void setFormValidator(ConsignmentEvaluationValidator formValidator) {
    this.formValidator = formValidator;
  }

  @Required
  public void setMessageSource(MessageSource messageSource) {
    this.messageSource = messageSource;
  }
}
