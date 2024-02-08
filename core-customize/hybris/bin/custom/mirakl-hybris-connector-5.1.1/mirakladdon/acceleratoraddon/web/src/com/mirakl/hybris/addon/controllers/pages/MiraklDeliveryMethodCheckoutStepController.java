package com.mirakl.hybris.addon.controllers.pages;

import static com.mirakl.hybris.addon.constants.MirakladdonWebConstants.ORDER_PRICES_CHANGED_MESSAGE;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages.ERROR_MESSAGES_HOLDER;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages.INFO_MESSAGES_HOLDER;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages.addErrorMessage;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages.addFlashMessage;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages.addInfoMessage;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.hybris.addon.controllers.MirakladdonControllerConstants;
import com.mirakl.hybris.addon.forms.UpdateShippingOptionForm;
import com.mirakl.hybris.facades.order.ShippingFacade;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;

@RequestMapping(value = "/checkout/multi/mirakl/delivery-method")
public class MiraklDeliveryMethodCheckoutStepController extends AbstractCheckoutStepController {

  private static final Logger LOG = Logger.getLogger(MiraklDeliveryMethodCheckoutStepController.class);

  protected static final String DELIVERY_METHOD = "delivery-method";

  protected ShippingFacade shippingFacade;

  protected String redirectToDeliveryAddress;
  protected String redirectToDeliveryMethod;

  @RequestMapping(value = "/choose", method = RequestMethod.GET)
  @RequireHardLogIn
  @Override
  @PreValidateCheckoutStep(checkoutStep = DELIVERY_METHOD)
  public String enterStep(Model model, RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {
    boolean updated;
    try {
      updated = shippingFacade.updateAvailableShippingOptions();
      model.addAttribute("offerErrors", shippingFacade.getOfferDiscrepancies());
      shippingFacade.removeInvalidOffers();
    } catch (MiraklApiException e) {
      LOG.error("Exception occurred while setting available delivery options", e);
      addFlashMessage(redirectAttributes, ERROR_MESSAGES_HOLDER, e.getLocalizedMessage());
      return redirectToDeliveryAddress;
    }
    if (updated || shippingFacade.updateOffersPrice()) {
      addInfoMessage(model, ORDER_PRICES_CHANGED_MESSAGE);
    }
    enterStepInternal(model, redirectAttributes);

    return MirakladdonControllerConstants.Views.Pages.MultiStepCheckout.ChooseDeliveryMethodPage;
  }

  @RequestMapping(value = "/update", method = RequestMethod.POST)
  @RequireHardLogIn
  public String updateCartShippingOptions(@RequestParam("shipping_option") String shippingOptionCode, Model model,
      @Valid UpdateShippingOptionForm form, BindingResult bindingResult) throws CMSItemNotFoundException {
    if (bindingResult.hasErrors()) {
      for (ObjectError error : bindingResult.getAllErrors()) {
        addErrorMessage(model, error.getDefaultMessage());
      }
      preparePage(model);

      return MirakladdonControllerConstants.Views.Pages.MultiStepCheckout.ChooseDeliveryMethodPage;
    }

    if (isEmpty(form.getShopId())) {
      getCheckoutFacade().setDeliveryMode(shippingOptionCode);
    } else {
      shippingFacade.updateShippingOptions(shippingOptionCode, form.getLeadTimeToShip(), form.getShopId());
    }

    return redirectToDeliveryMethod;
  }

  @RequestMapping(value = "/next", method = RequestMethod.GET)
  @RequireHardLogIn
  @Override
  public String next(RedirectAttributes redirectAttributes) {
    boolean updated;
    try {
      shippingFacade.removeInvalidOffers();
      updated = shippingFacade.updateAvailableShippingOptions();
    } catch (MiraklApiException e) {
      LOG.error("Exception occurred while setting available delivery options", e);
      addFlashMessage(redirectAttributes, ERROR_MESSAGES_HOLDER, e.getLocalizedMessage());
      return redirectToDeliveryMethod;
    }
    if (updated || shippingFacade.updateOffersPrice()) {
      addFlashMessage(redirectAttributes, INFO_MESSAGES_HOLDER, ORDER_PRICES_CHANGED_MESSAGE);
      return redirectToDeliveryMethod;
    }
    return getCheckoutStep().nextStep();
  }

  @RequestMapping(value = "/back", method = RequestMethod.GET)
  @RequireHardLogIn
  @Override
  public String back(final RedirectAttributes redirectAttributes) {
    return getCheckoutStep().previousStep();
  }

  protected void preparePage(Model model) throws CMSItemNotFoundException {
    prepareDataForPage(model);
    storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
    setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
    model.addAttribute(WebConstants.BREADCRUMBS_KEY,
        getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.deliveryMethod.breadcrumb"));
    model.addAttribute("metaRobots", "noindex,nofollow");
    setCheckoutStepLinksForModel(model, getCheckoutStep());
  }

  protected void enterStepInternal(Model model, RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {
    // Try to set default delivery mode
    getCheckoutFacade().setDeliveryModeIfAvailable();

    final CartData cartData = getCheckoutFacade().getCheckoutCart();
    model.addAttribute("cartData", cartData);
    model.addAttribute("deliveryMethods", getCheckoutFacade().getSupportedDeliveryModes());
    this.prepareDataForPage(model);
    storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
    setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
    model.addAttribute(WebConstants.BREADCRUMBS_KEY,
        getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.deliveryMethod.breadcrumb"));
    model.addAttribute("metaRobots", "noindex,nofollow");
    setCheckoutStepLinksForModel(model, getCheckoutStep());
  }

  protected CheckoutStep getCheckoutStep() {
    return getCheckoutStep(DELIVERY_METHOD);
  }

  @Required
  public void setRedirectToDeliveryAddress(String redirectToDeliveryAddress) {
    this.redirectToDeliveryAddress = redirectToDeliveryAddress;
  }

  @Required
  public void setRedirectToDeliveryMethod(String redirectToDeliveryMethod) {
    this.redirectToDeliveryMethod = redirectToDeliveryMethod;
  }

  @Required
  public void setShippingFacade(ShippingFacade shippingFacade) {
    this.shippingFacade = shippingFacade;
  }
}
