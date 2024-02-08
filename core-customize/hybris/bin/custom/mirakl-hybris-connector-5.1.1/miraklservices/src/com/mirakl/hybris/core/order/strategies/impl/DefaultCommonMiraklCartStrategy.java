package com.mirakl.hybris.core.order.strategies.impl;

import com.mirakl.client.mmp.front.core.MiraklMarketplacePlatformFrontApi;
import com.mirakl.hybris.beans.CartAdjustment;
import com.mirakl.hybris.beans.OfferOrderingConditions;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.order.daos.MiraklAbstractOrderEntryDao;
import com.mirakl.hybris.core.order.strategies.CommonMiraklCartStrategy;
import com.mirakl.hybris.core.order.strategies.SynchronousCartUpdateActivationStrategy;
import de.hybris.platform.commerceservices.order.impl.AbstractCommerceCartStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import org.springframework.beans.factory.annotation.Required;

import static com.mirakl.hybris.core.order.MiraklCommerceCartModificationStatus.MIN_ORDER_QUANTITY_UNREACHED;
import static com.mirakl.hybris.core.order.MiraklCommerceCartModificationStatus.PACKAGE_QUANTITY_CONSTRAINT;
import static de.hybris.platform.commerceservices.order.CommerceCartModificationStatus.*;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class DefaultCommonMiraklCartStrategy extends AbstractCommerceCartStrategy implements CommonMiraklCartStrategy {

  protected MiraklAbstractOrderEntryDao<CartEntryModel> miraklCartEntryDao;
  protected Converter<OfferModel, OfferOrderingConditions> offerOrderingConditionsConverter;
  protected MiraklMarketplacePlatformFrontApi miraklApi;
  protected SynchronousCartUpdateActivationStrategy synchronousCartUpdateActivationStrategy;

  @Override
  public CartAdjustment calculateCartAdjustment(CommerceCartParameter parameter) {

    final CartModel cart = parameter.getCart();
    AbstractOrderEntryModel entryToUpdate = null;

    int entryNumber = (int) parameter.getEntryNumber();
    if (entryNumber >= 0) {
      entryToUpdate = getEntryForNumber(cart, entryNumber);
    }

    final long stockLevel = getOfferStockLevel(parameter.getOffer());
    final long cartLevelForOffer = checkCartLevel(parameter.getOffer(), entryToUpdate, cart);
    long newQuantity = parameter.getQuantity();
    long newTotalQuantityAfterCheck = newQuantity;

    OfferOrderingConditions offerOrderingConditions = offerOrderingConditionsConverter.convert(parameter.getOffer());

    String status = SUCCESS;
    if (newQuantity > stockLevel) {
      newTotalQuantityAfterCheck = stockLevel;
      status = cartLevelForOffer == newTotalQuantityAfterCheck ? NO_STOCK : LOW_STOCK;
    }

    long allowedAdjustment = newTotalQuantityAfterCheck - cartLevelForOffer;
    if (allowedAdjustment == 0) {
      return getAdjustment(allowedAdjustment, status, newQuantity, cartLevelForOffer, offerOrderingConditions);
    }

    Integer maxOrderQuantity = offerOrderingConditions.getMaxOrderQuantity();
    if (maxOrderQuantity != null && newTotalQuantityAfterCheck > maxOrderQuantity) {
      final long maxAllowedAdjustment = maxOrderQuantity - cartLevelForOffer;
      allowedAdjustment = min(allowedAdjustment, maxAllowedAdjustment);
      status = updateStatus(status, MAX_ORDER_QUANTITY_EXCEEDED);
      newTotalQuantityAfterCheck = cartLevelForOffer + allowedAdjustment;
    }

    Integer packageQuantity = offerOrderingConditions.getPackageQuantity();
    if (packageQuantity != null && newTotalQuantityAfterCheck % packageQuantity != 0) {
      allowedAdjustment = max(allowedAdjustment - newTotalQuantityAfterCheck % packageQuantity, 0);
      status = updateStatus(status, PACKAGE_QUANTITY_CONSTRAINT);
      newTotalQuantityAfterCheck = cartLevelForOffer + allowedAdjustment;
    }

    Integer minOrderQuantity = offerOrderingConditions.getMinOrderQuantity();
    if (minOrderQuantity != null && newTotalQuantityAfterCheck != 0 && newTotalQuantityAfterCheck < minOrderQuantity) {
      status = updateStatus(status, MIN_ORDER_QUANTITY_UNREACHED);
      allowedAdjustment = 0;
    }

    return getAdjustment(allowedAdjustment, status, newQuantity, cartLevelForOffer, offerOrderingConditions);
  }

  protected String updateStatus(String actualStatus, String newStatus) {
    return SUCCESS.equals(actualStatus) ? newStatus : actualStatus;
  }

  protected long getOfferStockLevel(OfferModel offer) {
    return offer.getQuantity() == null ? 0 : offer.getQuantity();
  }


  protected CartAdjustment getAdjustment(long allowedAdjustment, String status, long requestedQuantity,
      final long cartLevelForOffer, OfferOrderingConditions offerOrderingConditions) {
    CartAdjustment adjustment = new CartAdjustment();
    adjustment.setStatus(status);
    adjustment.setAllowedQuantityChange(allowedAdjustment);
    adjustment.setRequestedQuantity(requestedQuantity);
    adjustment.setCartLevelForOffer(cartLevelForOffer);
    adjustment.setMinOrderQuantity(offerOrderingConditions.getMinOrderQuantity());
    adjustment.setMaxOrderQuantity(offerOrderingConditions.getMaxOrderQuantity());
    adjustment.setPackageQuantity(offerOrderingConditions.getPackageQuantity());

    return adjustment;
  }


  protected long checkCartLevel(final OfferModel offerModel, AbstractOrderEntryModel entryToUpdate, final CartModel cartModel) {
    if (entryToUpdate != null) {
      return entryToUpdate.getQuantity();
    }

    CartEntryModel entryModel = miraklCartEntryDao.findEntryByOffer(cartModel, offerModel);
    if (entryModel != null) {
      return entryModel.getQuantity() != null ? entryModel.getQuantity() : 0;
    }

    return 0;
  }

  @Override
  protected long checkCartLevel(ProductModel productModel, CartModel cartModel, PointOfServiceModel pointOfServiceModel) {
    return super.checkCartLevel(productModel, cartModel, pointOfServiceModel);
  }

  @Required
  public void setOfferOrderingConditionsConverter(
      Converter<OfferModel, OfferOrderingConditions> offerOrderingConditionsConverter) {
    this.offerOrderingConditionsConverter = offerOrderingConditionsConverter;
  }

  @Required
  public void setMiraklCartEntryDao(MiraklAbstractOrderEntryDao<CartEntryModel> miraklCartEntryDao) {
    this.miraklCartEntryDao = miraklCartEntryDao;
  }

  @Required
  public void setMiraklApi(MiraklMarketplacePlatformFrontApi miraklApi) {
    this.miraklApi = miraklApi;
  }

  @Required
  public void setSynchronousCartUpdateActivationStrategy(
      SynchronousCartUpdateActivationStrategy synchronousCartUpdateActivationStrategy) {
    this.synchronousCartUpdateActivationStrategy = synchronousCartUpdateActivationStrategy;
  }
}
