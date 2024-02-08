package com.mirakl.hybris.core.order.hook;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.lang.StringUtils.isBlank;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.order.services.ShippingFeeService;
import com.mirakl.hybris.core.order.strategies.ShippingZoneStrategy;
import com.mirakl.hybris.core.promotions.strategies.MiraklPromotionsActivationStrategy;
import com.mirakl.hybris.core.util.services.JsonMarshallingService;

import de.hybris.platform.commerceservices.order.hook.CommerceCartCalculationMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;

public class MiraklCommerceCartCalculationMethodHook implements CommerceCartCalculationMethodHook {

  private static final Logger LOG = Logger.getLogger(MiraklCommerceCartCalculationMethodHook.class);

  protected ShippingFeeService shippingFeeService;
  protected MiraklPromotionsActivationStrategy miraklPromotionsActivationStrategy;
  protected ShippingZoneStrategy shippingZoneStrategy;
  protected JsonMarshallingService jsonMarshallingService;

  @Override
  public void afterCalculate(CommerceCartParameter parameter) {
    // Nothing to do here
  }

  @Override
  public void beforeCalculate(CommerceCartParameter parameter) {
    validateParameterNotNullStandardMessage("CommerceCartParameter", parameter);
    validateParameterNotNullStandardMessage("CommerceCartParameter.cart", parameter.getCart());

    if (shouldUpdateCartCalculationJSON(parameter)) {
      updateCartCalculationJSON(parameter);
    }
  }

  protected boolean shouldUpdateCartCalculationJSON(CommerceCartParameter parameter) {
    return miraklPromotionsActivationStrategy.isMiraklPromotionsEnabled()
        && !parameter.getCart().getMarketplaceEntries().isEmpty();
  }

  protected void updateCartCalculationJSON(CommerceCartParameter parameter) {
    CartModel cart = parameter.getCart();
    String shippingZoneCode = shippingZoneStrategy.getEstimatedShippingZoneCode(cart);
    if (isBlank(shippingZoneCode)) {
      LOG.warn(
          "Unable to update cart calculation. You must define a default delivery country on your base store or define an address for the cart.");
      return;
    }
    try {
      MiraklOrderShippingFees calculation = shippingFeeService.getShippingFees(cart, shippingZoneCode);
      cart.setCartCalculationJSON(calculation != null ? jsonMarshallingService.toJson(calculation) : null);
    } catch (MiraklApiException miraklApiException) {
      LOG.warn("Unable to update cart calculation. You must define a valid address for the cart.", miraklApiException);
    }
  }

  @Required
  public void setShippingFeeService(ShippingFeeService shippingFeeService) {
    this.shippingFeeService = shippingFeeService;
  }

  @Required
  public void setMiraklPromotionsActivationStrategy(MiraklPromotionsActivationStrategy miraklPromotionsActivationStrategy) {
    this.miraklPromotionsActivationStrategy = miraklPromotionsActivationStrategy;
  }

  @Required
  public void setShippingZoneStrategy(ShippingZoneStrategy shippingZoneStrategy) {
    this.shippingZoneStrategy = shippingZoneStrategy;
  }

  @Required
  public void setJsonMarshallingService(JsonMarshallingService jsonMarshallingService) {
    this.jsonMarshallingService = jsonMarshallingService;
  }
}
