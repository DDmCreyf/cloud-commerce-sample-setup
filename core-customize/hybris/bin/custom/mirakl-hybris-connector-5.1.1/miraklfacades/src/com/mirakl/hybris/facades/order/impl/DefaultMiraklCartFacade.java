package com.mirakl.hybris.facades.order.impl;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.product.strategies.OfferCodeGenerationStrategy;
import com.mirakl.hybris.facades.product.OfferFacade;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.impl.DefaultCartFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

public class DefaultMiraklCartFacade extends DefaultCartFacade {

  protected OfferFacade offerFacade;

  protected OfferCodeGenerationStrategy offerCodeGenerationStrategy;

  @Override
  public CartModificationData addToCart(String code, long quantity) throws CommerceCartModificationException {
    return addToCart(code, quantity, null);
  }

  @Override
  public CartModificationData addToCart(String code, long quantity, String storeId) throws CommerceCartModificationException {
    final CommerceCartParameter parameter = getCartParameter(code, quantity, storeId);
    final CommerceCartModification modification = getCommerceCartService().addToCart(parameter);
    return getCartModificationConverter().convert(modification);
  }

  protected CommerceCartParameter getCartParameter(String code, long quantity, String storeId) {
    final CartModel cartModel = getCartService().getSessionCart();
    final CommerceCartParameter parameter = new CommerceCartParameter();
    parameter.setEnableHooks(true);
    parameter.setCart(cartModel);
    parameter.setQuantity(quantity);
    parameter.setCreateNewEntry(false);
    parameter.setEntryNumber(-1);

    if (isNotBlank(storeId)) {
      final PointOfServiceModel pointOfServiceModel = getPointOfServiceService().getPointOfServiceForName(storeId);
      parameter.setPointOfService(pointOfServiceModel);
    }

    String productCode = code;
    if (offerCodeGenerationStrategy.isOfferCode(code)) {
      OfferModel offer = offerFacade.getOfferForCode(code);
      productCode = offer.getProductCode();
      parameter.setOffer(offer);
    }

    final ProductModel product = getProductService().getProductForCode(productCode);
    parameter.setProduct(product);
    parameter.setUnit(product.getUnit());

    return parameter;
  }

  @Required
  public void setOfferFacade(OfferFacade offerFacade) {
    this.offerFacade = offerFacade;
  }

  @Required
  public void setOfferCodeGenerationStrategy(OfferCodeGenerationStrategy offerCodeGenerationStrategy) {
    this.offerCodeGenerationStrategy = offerCodeGenerationStrategy;
  }

}
