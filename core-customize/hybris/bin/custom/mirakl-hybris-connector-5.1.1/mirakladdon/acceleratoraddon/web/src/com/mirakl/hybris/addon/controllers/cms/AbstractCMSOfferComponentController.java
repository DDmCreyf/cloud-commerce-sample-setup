package com.mirakl.hybris.addon.controllers.cms;

import static com.mirakl.hybris.addon.constants.MirakladdonWebConstants.ORDER_CONDITIONS_FRONT_VALIDATION;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.Model;

import com.mirakl.hybris.beans.OfferData;
import com.mirakl.hybris.facades.product.OfferFacade;

import de.hybris.platform.acceleratorservices.data.RequestContextData;
import de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.model.ModelService;

public abstract class AbstractCMSOfferComponentController<N extends AbstractCMSComponentModel>
extends AbstractCMSAddOnComponentController<N> {

  private static final Logger LOG = Logger.getLogger(AbstractCMSOfferComponentController.class);

  protected static final String PRODUCT_ATTRIBUTE = "product";
  protected static final String TOP_OFFER_ATTRIBUTE = "topOffer";
  protected static final String ENABLE_VALIDATION_ATTRIBUTE = "orderConditionValidation";

  protected OfferFacade offerFacade;
  protected ModelService modelService;
  protected ConfigurationService configurationService;

  @Override
  protected void fillModel(HttpServletRequest request, Model model, N component) {
    RequestContextData requestContextData = getRequestContextData(request);
    List<OfferData> offers = requestContextData.getOffers();

    if (isEmpty(offers)) {
      ProductData product = (ProductData) request.getAttribute(PRODUCT_ATTRIBUTE);
      validateParameterNotNull(product, "No product found in request context");

      offers = offerFacade.getOffersForProductCode(product.getCode());
      requestContextData.setOffers(offers);
    }
    model.addAttribute(TOP_OFFER_ATTRIBUTE, isEmpty(offers) ? null : offers.get(0));
    model.addAttribute(ENABLE_VALIDATION_ATTRIBUTE,
        configurationService.getConfiguration().getBoolean(ORDER_CONDITIONS_FRONT_VALIDATION));

    addFrontendComponentPropertiesToModel(model, component);
    fillModel(model, offers);
  }

  protected void addFrontendComponentPropertiesToModel(Model model, N component) {
    for (String property : getCmsComponentService().getEditorProperties(component)) {
      try {
        Object value = modelService.getAttributeValue(component, property);
        model.addAttribute(property, value);
      } catch (AttributeNotSupportedException e) {
        LOG.warn(format("Attribute [%s] is not supported - cannot be added to the model", property));
      }
    }
  }

  protected abstract void fillModel(Model model, List<OfferData> offers);

  @Required
  public void setOfferFacade(OfferFacade offerFacade) {
    this.offerFacade = offerFacade;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }
}
