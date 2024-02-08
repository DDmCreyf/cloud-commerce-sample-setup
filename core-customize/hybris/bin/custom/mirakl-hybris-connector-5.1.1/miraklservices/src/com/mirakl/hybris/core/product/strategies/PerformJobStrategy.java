package com.mirakl.hybris.core.product.strategies;

import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public interface PerformJobStrategy<T extends CronJobModel> {

  /**
   * Performs a job and returns a {@link PerformResult}
   *
   * @param cronJob cronJob to perform
   * @return PerformResult
   */
  PerformResult perform(final T cronJob);
}
