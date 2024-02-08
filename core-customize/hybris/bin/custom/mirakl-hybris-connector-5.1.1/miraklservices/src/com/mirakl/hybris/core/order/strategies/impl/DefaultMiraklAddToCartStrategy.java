package com.mirakl.hybris.core.order.strategies.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.hybris.beans.CartAdjustment;
import com.mirakl.hybris.beans.OfferOrderingConditions;
import com.mirakl.hybris.core.model.OfferModel;
import com.mirakl.hybris.core.order.daos.MiraklAbstractOrderEntryDao;
import com.mirakl.hybris.core.order.strategies.CommonMiraklCartStrategy;
import com.mirakl.hybris.core.product.services.OfferService;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceUpdateCartEntryStrategy;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceAddToCartStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

public class DefaultMiraklAddToCartStrategy extends DefaultCommerceAddToCartStrategy {

  protected OfferService offerService;
  protected CommerceUpdateCartEntryStrategy updateCartEntryStrategy;
  protected CommonMiraklCartStrategy commonCartStrategy;
  protected MiraklAbstractOrderEntryDao<CartEntryModel> miraklCartEntryDao;
  protected Converter<OfferModel, AbstractOrderEntryModel> orderEntryConverter;
  protected Converter<OfferModel, OfferOrderingConditions> offerOrderingConditionsConverter;

  @Override
  public CommerceCartModification addToCart(CommerceCartParameter parameter) throws CommerceCartModificationException {
    final OfferModel offer = parameter.getOffer();
    if (offer == null) {
      forceNewEntryForProductIfApplicable(parameter);
      return super.addToCart(parameter);
    }

    beforeAddToCart(parameter);
    validateAddToCart(parameter);

    final CartModel cart = parameter.getCart();
    CartEntryModel existingOfferEntry = miraklCartEntryDao.findEntryByOffer(cart, offer);
    if (existingOfferEntry != null) {
      CommerceCartModification modification = updateExistingEntry(parameter, existingOfferEntry);
      afterAddToCart(parameter, modification);
      return modification;
    }

    CommerceCartModification modification;
    CartAdjustment cartAdjustment = commonCartStrategy.calculateCartAdjustment(parameter);
    Long allowedQuantityChange = cartAdjustment.getAllowedQuantityChange();
    if (allowedQuantityChange > 0) {
      final CartEntryModel entryModel = addMarketplaceCartEntry(parameter, allowedQuantityChange);
      modification = createAddToCartResp(parameter, cartAdjustment.getStatus(), entryModel, allowedQuantityChange);
    } else {
      modification = createAddToCartResp(parameter, cartAdjustment.getStatus(), createEmptyCartEntry(parameter), 0);
    }

    afterAddToCart(parameter, modification);

    return modification;
  }

  protected CommerceCartModification updateExistingEntry(CommerceCartParameter parameter, CartEntryModel existingEntry)
      throws CommerceCartModificationException {
    parameter.setEntryNumber(existingEntry.getEntryNumber());
    parameter.setQuantity(existingEntry.getQuantity() + parameter.getQuantity());
    return updateCartEntryStrategy.updateQuantityForCartEntry(parameter);
  }

  protected void forceNewEntryForProductIfApplicable(CommerceCartParameter parameter) {
    List<CartEntryModel> entriesWithNoOffers = getCartService().getEntriesForProduct(parameter.getCart(), parameter.getProduct());
    if (CollectionUtils.isEmpty(entriesWithNoOffers)) {
      parameter.setCreateNewEntry(true);
    }
  }

  @Override
  protected void validateAddToCart(CommerceCartParameter parameters) throws CommerceCartModificationException {
    super.validateAddToCart(parameters);
    if (parameters.getPointOfService() != null && parameters.getOffer() != null) {
      throw new CommerceCartModificationException("Offers should not be combined with point of service deliveries");
    }
  }

  protected CartEntryModel addMarketplaceCartEntry(final CommerceCartParameter parameter, final long actualAllowedQuantityChange)
      throws CommerceCartModificationException {
    final CartModel cartModel = parameter.getCart();
    final ProductModel productModel = parameter.getProduct();
    final UnitModel unit = parameter.getUnit();
    final UnitModel orderableUnit = unit != null ? unit : getUnit(parameter);

    CartEntryModel cartEntryModel =
        getCartService().addNewEntry(cartModel, productModel, actualAllowedQuantityChange, orderableUnit, APPEND_AS_LAST, false);
    orderEntryConverter.convert(parameter.getOffer(), cartEntryModel);

    getModelService().save(cartEntryModel);
    getCommerceCartCalculationStrategy().calculateCart(cartModel);
    getModelService().save(cartEntryModel);

    return cartEntryModel;
  }

  protected CartEntryModel createEmptyCartEntry(final CommerceCartParameter parameter) {

    final ProductModel productModel = parameter.getProduct();
    final PointOfServiceModel deliveryPointOfService = parameter.getPointOfService();

    final CartEntryModel entry = new CartEntryModel();
    entry.setProduct(productModel);
    entry.setDeliveryPointOfService(deliveryPointOfService);
    if (parameter.getOffer() != null) {
      entry.setOfferId(parameter.getOffer().getId());
    }

    return entry;
  }

  protected CommerceCartModification createAddToCartResp(final CommerceCartParameter parameter, final String status,
      final CartEntryModel entry, final long quantityAdded) {
    final long quantityToAdd = parameter.getQuantity();

    final CommerceCartModification modification = new CommerceCartModification();
    modification.setStatusCode(status);
    modification.setQuantityAdded(quantityAdded);
    modification.setQuantity(quantityToAdd);

    modification.setEntry(entry);

    return modification;
  }

  protected UnitModel getUnit(final CommerceCartParameter parameter) throws CommerceCartModificationException {
    final ProductModel productModel = parameter.getProduct();
    try {
      return getProductService().getOrderableUnit(productModel);
    } catch (final ModelNotFoundException e) {
      throw new CommerceCartModificationException(e.getMessage(), e);
    }
  }

  protected int getAppendAsLast() {
    return APPEND_AS_LAST;
  }

  @Required
  public void setOfferService(OfferService offerService) {
    this.offerService = offerService;
  }

  @Required
  public void setCommonCartStrategy(CommonMiraklCartStrategy commonCartStrategy) {
    this.commonCartStrategy = commonCartStrategy;
  }

  @Required
  public void setMiraklCartEntryDao(MiraklAbstractOrderEntryDao<CartEntryModel> miraklCartEntryDao) {
    this.miraklCartEntryDao = miraklCartEntryDao;
  }

  @Required
  public void setUpdateCartEntryStrategy(CommerceUpdateCartEntryStrategy updateCartEntryStrategy) {
    this.updateCartEntryStrategy = updateCartEntryStrategy;
  }

  @Required
  public void setAbstractOrderEntryModelConverter(Converter<OfferModel, AbstractOrderEntryModel> orderEntryConverter) {
    this.orderEntryConverter = orderEntryConverter;
  }

  public Converter<OfferModel, OfferOrderingConditions> getOfferOrderingConditionsConverter() {
    return offerOrderingConditionsConverter;
  }

  @Required
  public void setOfferOrderingConditionsConverter(
      Converter<OfferModel, OfferOrderingConditions> offerOrderingConditionsConverter) {
    this.offerOrderingConditionsConverter = offerOrderingConditionsConverter;
  }


}
