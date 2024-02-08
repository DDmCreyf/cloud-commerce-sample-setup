package com.mirakl.hybris.core.fulfilment.strategies.impl;

import static java.lang.String.format;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.mirakl.client.mmp.front.domain.order.create.MiraklOfferNotShippable;
import com.mirakl.hybris.core.fulfilment.events.NotShippableOffersEvent;
import com.mirakl.hybris.core.fulfilment.strategies.NotShippableOffersHandlingStrategy;
import com.mirakl.hybris.core.order.services.MiraklOrderService;

import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultNotShippableOffersHandlingStrategy implements NotShippableOffersHandlingStrategy {

  private static final Logger LOG = Logger.getLogger(DefaultNotShippableOffersHandlingStrategy.class);

  protected MiraklOrderService miraklOrderService;

  protected ModelService modelService;

  @Override
  public void handleEvent(NotShippableOffersEvent event) {
    OrderModel order = event.getOrder();
    List<MiraklOfferNotShippable> miraklNotShippableOffers = event.getNotShippableOffers();
    List<AbstractOrderEntryModel> notShippableEntries =
        miraklOrderService.extractNotShippableEntries(miraklNotShippableOffers, order);

    if (notShippableEntries.size() != miraklNotShippableOffers.size()) {
      LOG.warn(
          format("A NotShippableOffersEvent was triggered for %s entries for order [%s] but was able to retrieve only %s entries",
              miraklNotShippableOffers.size(), order.getCode(), notShippableEntries.size()));
      if (CollectionUtils.isEmpty(notShippableEntries)) {
        return;
      }
    }

    OrderEntryStatus status = getStatusForNotShippableEntries();
    LOG.info(format("Marking %s entries as %s for order [%s]", notShippableEntries.size(), status, order.getCode()));
    for (AbstractOrderEntryModel orderEntry : notShippableEntries) {
      orderEntry.setQuantityStatus(status);
    }
    modelService.saveAll(notShippableEntries);
  }

  protected OrderEntryStatus getStatusForNotShippableEntries() {
    return OrderEntryStatus.DEAD;
  }

  @Required
  public void setMiraklOrderService(MiraklOrderService miraklOrderService) {
    this.miraklOrderService = miraklOrderService;
  }

  @Required
  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }
}
