package com.mirakl.hybris.core.order.strategies.impl;

import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;

import de.hybris.platform.commerceservices.order.EntryMergeFilter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

/**
 * Disables merging cart items with different offer id.
 */
public class EntryMergeFilterMiraklProduct implements EntryMergeFilter {

  @Override
  public Boolean apply(@Nonnull AbstractOrderEntryModel candidate, @Nonnull AbstractOrderEntryModel target) {
    return StringUtils.equals(candidate.getOfferId(), target.getOfferId());
  }

}
