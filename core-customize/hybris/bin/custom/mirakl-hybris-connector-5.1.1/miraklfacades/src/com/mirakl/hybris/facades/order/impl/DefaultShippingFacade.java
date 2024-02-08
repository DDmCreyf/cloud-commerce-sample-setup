package com.mirakl.hybris.facades.order.impl;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang.ObjectUtils.compare;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Predicates;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.order.services.MiraklOrderService;
import com.mirakl.hybris.core.order.services.ShippingFeeService;
import com.mirakl.hybris.core.order.services.ShippingOptionsService;
import com.mirakl.hybris.facades.order.ShippingFacade;
import com.mirakl.hybris.facades.shipping.data.ShippingOfferDiscrepancyData;

import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DefaultShippingFacade implements ShippingFacade {

  protected ShippingOptionsService shippingOptionsService;
  protected ShippingFeeService shippingFeeService;
  protected CommerceCheckoutService commerceCheckoutService;
  protected CartService cartService;
  protected Converter<AbstractOrderEntryModel, ShippingOfferDiscrepancyData> offerDiscrepancyConverter;
  protected MiraklOrderService miraklOrderService;

  @Override
  public void setAvailableShippingOptions() {
    CartModel sessionCart = getSessionCart();
    shippingOptionsService.setShippingOptions(sessionCart);

    recalculateCart(sessionCart);
  }

  @Override
  public boolean updateAvailableShippingOptions() {
    CartModel sessionCart = getSessionCart();
    shippingOptionsService.setShippingOptions(sessionCart);

    final Double subTotalBefore = sessionCart.getSubtotal();
    final Double totalTaxBefore = sessionCart.getTotalTax();
    final Double totalDiscountsBefore = sessionCart.getTotalDiscounts();

    recalculateCart(sessionCart);
    cartService.saveOrder(sessionCart);

    return priceHasChanged(subTotalBefore, sessionCart.getSubtotal())
        || priceHasChanged(totalTaxBefore, sessionCart.getTotalTax())
        || priceHasChanged(totalDiscountsBefore, sessionCart.getTotalDiscounts());
  }

  protected boolean priceHasChanged(Double priceBefore, Double priceAfter) {
    return compare(priceBefore, priceAfter) != 0;
  }

  @Override
  public void updateShippingOptions(String selectedShippingOptionCode, Integer leadTimeToShip, String shopId) {
    CartModel sessionCart = getSessionCart();
    shippingOptionsService.setSelectedShippingOption(sessionCart, selectedShippingOptionCode, leadTimeToShip, shopId);
  }

  @Override
  public List<ShippingOfferDiscrepancyData> getOfferDiscrepancies() {
    CartModel sessionCart = getSessionCart();

    List<ShippingOfferDiscrepancyData> nullableOfferDiscrepancies =
        offerDiscrepancyConverter.convertAllIgnoreExceptions(sessionCart.getEntries());

    return newArrayList(filter(nullableOfferDiscrepancies, Predicates.<ShippingOfferDiscrepancyData>notNull()));
  }

  @Override
  public void removeInvalidOffers() {
    CartModel sessionCart = getSessionCart();

    MiraklOrderShippingFees shippingFees = shippingFeeService.getStoredShippingFees(sessionCart);
    if (shippingFees != null) {
      List<MiraklOrderShippingFeeOffer> allShippingOffers = shippingFeeService.extractAllShippingFeeOffers(shippingFees);
      shippingOptionsService.adjustOfferQuantities(sessionCart.getMarketplaceEntries(), allShippingOffers);
      shippingOptionsService.removeOfferEntriesWithError(sessionCart, shippingFees.getErrors());

      recalculateCart(sessionCart);
    }
  }

  @Override
  public boolean updateOffersPrice() {
    CartModel cart = getSessionCart();
    MiraklOrderShippingFees shippingFees = shippingFeeService.getStoredShippingFees(cart);
    boolean priceHasChanged = shippingFees != null && miraklOrderService.updateOffersPrice(cart, shippingFees);
    if (priceHasChanged) {
      recalculateCart(cart);
    }
    return priceHasChanged;
  }

  protected CartModel getSessionCart() {
    if (cartService.hasSessionCart()) {
      return cartService.getSessionCart();
    }
    throw new IllegalArgumentException("No session cart found");
  }

  protected void recalculateCart(CartModel sessionCart) {
    CommerceCheckoutParameter checkoutParameter = new CommerceCheckoutParameter();
    checkoutParameter.setEnableHooks(true);
    checkoutParameter.setCart(sessionCart);
    commerceCheckoutService.calculateCart(checkoutParameter);
  }

  @Required
  public void setShippingOptionsService(ShippingOptionsService shippingOptionsService) {
    this.shippingOptionsService = shippingOptionsService;
  }

  @Required
  public void setOfferDiscrepancyConverter(
      Converter<AbstractOrderEntryModel, ShippingOfferDiscrepancyData> offerDiscrepancyConverter) {
    this.offerDiscrepancyConverter = offerDiscrepancyConverter;
  }

  @Required
  public void setCommerceCheckoutService(CommerceCheckoutService commerceCheckoutService) {
    this.commerceCheckoutService = commerceCheckoutService;
  }

  @Required
  public void setCartService(CartService cartService) {
    this.cartService = cartService;
  }

  @Required
  public void setShippingFeeService(ShippingFeeService shippingFeeService) {
    this.shippingFeeService = shippingFeeService;
  }

  @Required
  public void setMiraklOrderService(MiraklOrderService miraklOrderService) {
    this.miraklOrderService = miraklOrderService;
  }
}
