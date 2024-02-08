package com.mirakl.hybris.core.order.attributes;

import static com.google.common.collect.Iterables.any;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.List;

import com.google.common.base.Predicate;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

public class DefaultMarketplaceOrderDynamicHandler extends AbstractDynamicAttributeHandler<Boolean, AbstractOrderModel> {

  @Override
  public Boolean get(AbstractOrderModel model) {
    validateParameterNotNullStandardMessage("abstractOrder", model);

    List<AbstractOrderEntryModel> orderEntries = model.getEntries();

    return isNotEmpty(orderEntries) && !any(orderEntries, new Predicate<AbstractOrderEntryModel>() {
      @Override
      public boolean apply(AbstractOrderEntryModel orderEntry) {
        return orderEntry.getOfferId() == null;
      }
    });
  }
}
