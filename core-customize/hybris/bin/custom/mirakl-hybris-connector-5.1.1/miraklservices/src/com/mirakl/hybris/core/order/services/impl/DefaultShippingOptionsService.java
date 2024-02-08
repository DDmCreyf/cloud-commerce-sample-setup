package com.mirakl.hybris.core.order.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFee;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeError;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFeeOffer;
import com.mirakl.client.mmp.front.domain.shipping.MiraklOrderShippingFees;
import com.mirakl.hybris.core.order.services.ShippingFeeService;
import com.mirakl.hybris.core.order.services.ShippingOptionsService;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultShippingOptionsService implements ShippingOptionsService {

  protected ShippingFeeService shippingFeeService;
  protected ModelService modelService;

  @Override
  public void setShippingOptions(AbstractOrderModel order) {
    validateParameterNotNull(order, "AbstractOrder cannot be null for the shipping rates request");

    setShippingFeesForOrder(order, shippingFeeService.getShippingFees(order));
  }

  @Override
  public void resetSavedShippingOptions(AbstractOrderModel order) {
    order.setShippingFeesJSON(null);
    Set<ItemModel> modelsToSave = new HashSet<>();
    modelsToSave.add(order);

    List<AbstractOrderEntryModel> marketplaceEntries = order.getMarketplaceEntries();
    if (isNotEmpty(marketplaceEntries)) {
      for (AbstractOrderEntryModel orderEntry : marketplaceEntries) {
        orderEntry.setLineShippingCode(null);
        orderEntry.setLineShippingLabel(null);
        orderEntry.setLineShippingPrice(0);
        modelsToSave.add(orderEntry);
      }
    }
    modelService.saveAll(modelsToSave);
  }

  protected void setShippingFeesForOrder(AbstractOrderModel order, MiraklOrderShippingFees shippingFees) {
    if (shippingFees == null) {
      if (isNotBlank(order.getShippingFeesJSON())) {
        overwriteStoredShippingFees(order, null);
      }
      return;
    }
    shippingFeeService.setLineShippingDetails(order, shippingFees);
    modelService.saveAll(order.getEntries());

    overwriteStoredShippingFees(order, shippingFeeService.getShippingFeesAsJson(shippingFees));
  }

  @Override
  public void setSelectedShippingOption(AbstractOrderModel order, String shippingOptionCode, Integer leadTimeToShip,
      String shopId) {
    validateParameterNotNullStandardMessage("order", order);
    validateParameterNotNullStandardMessage("shippingOptionCode", shippingOptionCode);
    validateParameterNotNullStandardMessage("leadTimeToShip", leadTimeToShip);
    validateParameterNotNullStandardMessage("shopId", shopId);

    MiraklOrderShippingFees miraklOrderShippingFees = shippingFeeService.getStoredShippingFees(order);
    if (miraklOrderShippingFees == null) {
      throw new IllegalStateException(
          format("Cannot set selected shipping option - no shipping fees found for order [%s]", order.getCode()));
    }

    Optional<MiraklOrderShippingFee> shippingFee =
        shippingFeeService.extractShippingFeeForShop(miraklOrderShippingFees, shopId, leadTimeToShip);
    if (!shippingFee.isPresent()) {
      throw new IllegalStateException(
          format("No shipping fee found for shop with id [%s] and lead time to ship [%s]", shopId, leadTimeToShip));
    }

    shippingFeeService.updateSelectedShippingOption(shippingFee.get(), shippingOptionCode);
    order.setShippingFeesJSON(shippingFeeService.getShippingFeesAsJson(miraklOrderShippingFees));
    List<AbstractOrderEntryModel> updatedEntries =
        shippingFeeService.setLineShippingDetails(order, singletonList(shippingFee.get()));
    modelService.saveAll(updatedEntries);
    modelService.save(order);
  }

  @Override
  public void removeOfferEntriesWithError(AbstractOrderModel order, final List<MiraklOrderShippingFeeError> shippingFeeErrors) {

    Collection<AbstractOrderEntryModel> entriesToRemove =
        Collections2.filter(order.getMarketplaceEntries(), new Predicate<AbstractOrderEntryModel>() {
          @Override
          public boolean apply(AbstractOrderEntryModel entry) {
            for (MiraklOrderShippingFeeError shippingFeeError : shippingFeeErrors) {
              if (entry.getOfferId().equals(shippingFeeError.getOfferId())) {
                return true;
              }
            }
            return false;
          }
        });
    modelService.removeAll(entriesToRemove);
    modelService.refresh(order);
  }

  @Override
  public void adjustOfferQuantities(List<AbstractOrderEntryModel> orderEntries,
      List<MiraklOrderShippingFeeOffer> shippingFeeOffers) {
    List<AbstractOrderEntryModel> modifiedEntries = Lists.newArrayList();
    for (AbstractOrderEntryModel orderEntry : orderEntries) {
      for (MiraklOrderShippingFeeOffer shippingFeeOffer : shippingFeeOffers) {
        Optional<AbstractOrderEntryModel> modifiedEntry = processEntryQuantity(orderEntry, shippingFeeOffer);
        if (modifiedEntry.isPresent()) {
          modifiedEntries.add(modifiedEntry.get());
        }
      }
    }
    modelService.saveAll(modifiedEntries);
  }

  protected void overwriteStoredShippingFees(AbstractOrderModel order, String shippingFeesJSON) {
    order.setShippingFeesJSON(shippingFeesJSON);
    order.setCalculated(false);
    modelService.save(order);
  }

  protected Optional<AbstractOrderEntryModel> processEntryQuantity(AbstractOrderEntryModel orderEntry,
      MiraklOrderShippingFeeOffer shippingFeeOffer) {
    if (shippingFeeOffer.getId().equals(orderEntry.getOfferId()) && hasInsufficientQuantity(orderEntry, shippingFeeOffer)) {
      long newQuantity = getAvailableQuantity(shippingFeeOffer.getQuantity(), orderEntry.getQuantity());
      if (newQuantity > 0) {
        orderEntry.setQuantity(newQuantity);
        return Optional.of(orderEntry);
      } else {
        modelService.remove(orderEntry);
      }
    }
    return Optional.absent();
  }

  protected boolean hasInsufficientQuantity(AbstractOrderEntryModel orderEntry, MiraklOrderShippingFeeOffer shippingFeeOffer) {
    return shippingFeeOffer.getQuantity() < orderEntry.getQuantity();
  }

  protected long getAvailableQuantity(Integer offerQuantity, Long entryQuantity) {
    return Math.min(offerQuantity, entryQuantity);
  }

  @Required
  public void setShippingFeeService(ShippingFeeService shippingFeeService) {
    this.shippingFeeService = shippingFeeService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

}
