package com.mirakl.hybris.core.order.attributes;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

/**
 * Abstract class for filtering order entries
 */
public abstract class AbstractOrderEntryFilteringDynamicHandler
    extends AbstractDynamicAttributeHandler<List<AbstractOrderEntryModel>, AbstractOrderModel> {

  @Override
  public List<AbstractOrderEntryModel> get(AbstractOrderModel abstractOrder) {
    validateParameterNotNullStandardMessage("abstractOrder", abstractOrder);

    return ImmutableList.copyOf(Iterables.filter(abstractOrder.getEntries(), new Predicate<AbstractOrderEntryModel>() {
      @Override
      public boolean apply(AbstractOrderEntryModel orderEntry) {
        return filter(orderEntry);
      }
    }));
  }

  /**
   * Returns a boolean value indicating if a given orderEntry should be filtered or not
   *
   * @param orderEntry
   * @return true if the order entry should be kept, false otherwise
   */
  protected abstract boolean filter(AbstractOrderEntryModel orderEntry);

}
