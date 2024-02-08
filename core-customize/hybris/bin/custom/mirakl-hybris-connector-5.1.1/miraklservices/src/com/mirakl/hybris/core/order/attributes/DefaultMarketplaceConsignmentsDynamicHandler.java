package com.mirakl.hybris.core.order.attributes;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.util.Collections.emptySet;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.mirakl.hybris.core.model.MarketplaceConsignmentModel;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

public class DefaultMarketplaceConsignmentsDynamicHandler
    extends AbstractDynamicAttributeHandler<Set<MarketplaceConsignmentModel>, AbstractOrderModel> {

  @Override
  public Set<MarketplaceConsignmentModel> get(AbstractOrderModel abstractOrder) {
    validateParameterNotNullStandardMessage("abstractOrder", abstractOrder);
    if (abstractOrder.getConsignments() == null) {
      return emptySet();
    }
    Iterable<MarketplaceConsignmentModel> filteredConsignments =
        Iterables.filter(abstractOrder.getConsignments(), MarketplaceConsignmentModel.class);

    return ImmutableSet.copyOf(filteredConsignments);
  }

}
